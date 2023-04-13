package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraOfflineSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.impl.CameraDeviceImpl;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.SurfaceUtils;
import android.p008os.ConditionVariable;
import android.p008os.Handler;
import android.util.Log;
import android.util.Range;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CameraConstrainedHighSpeedCaptureSessionImpl extends CameraConstrainedHighSpeedCaptureSession implements CameraCaptureSessionCore {
    private final String TAG;
    private final CameraCharacteristics mCharacteristics;
    private final ConditionVariable mInitialized;
    private final CameraCaptureSessionImpl mSessionImpl;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CameraConstrainedHighSpeedCaptureSessionImpl(int id, CameraCaptureSession.StateCallback callback, Executor stateExecutor, CameraDeviceImpl deviceImpl, Executor deviceStateExecutor, boolean configureSuccess, CameraCharacteristics characteristics) {
        ConditionVariable conditionVariable = new ConditionVariable();
        this.mInitialized = conditionVariable;
        this.TAG = "CameraConstrainedHighSpeedCaptureSessionImpl";
        this.mCharacteristics = characteristics;
        CameraCaptureSession.StateCallback wrapperCallback = new WrapperCallback(callback);
        this.mSessionImpl = new CameraCaptureSessionImpl(id, null, wrapperCallback, stateExecutor, deviceImpl, deviceStateExecutor, configureSuccess);
        conditionVariable.open();
    }

    @Override // android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession
    public List<CaptureRequest> createHighSpeedRequestList(CaptureRequest request) throws CameraAccessException {
        if (request != null) {
            CameraCharacteristics.Key<StreamConfigurationMap> ck = CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;
            Integer sensorPixelMode = (Integer) request.get(CaptureRequest.SENSOR_PIXEL_MODE);
            if (sensorPixelMode != null && sensorPixelMode.intValue() == 1) {
                ck = CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION;
            }
            Collection<Surface> outputSurfaces = request.getTargets();
            Range<Integer> fpsRange = (Range) request.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
            StreamConfigurationMap config = (StreamConfigurationMap) this.mCharacteristics.get(ck);
            SurfaceUtils.checkConstrainedHighSpeedSurfaces(outputSurfaces, fpsRange, config);
            Range<Integer>[] highSpeedFpsRanges = config.getHighSpeedVideoFpsRangesFor(SurfaceUtils.getSurfaceSize(outputSurfaces.iterator().next()));
            Log.m106v("CameraConstrainedHighSpeedCaptureSessionImpl", "High speed fps ranges: " + Arrays.toString(highSpeedFpsRanges));
            int previewFps = Integer.MAX_VALUE;
            for (Range<Integer> range : highSpeedFpsRanges) {
                int rangeMin = range.getLower().intValue();
                if (previewFps > rangeMin) {
                    previewFps = rangeMin;
                }
            }
            if (previewFps != 60 && previewFps != 30) {
                Log.m104w("CameraConstrainedHighSpeedCaptureSessionImpl", "previewFps is neither 60 nor 30.");
                previewFps = 30;
            }
            Log.m106v("CameraConstrainedHighSpeedCaptureSessionImpl", "previewFps: " + previewFps);
            int requestListSize = fpsRange.getUpper().intValue() / previewFps;
            if (fpsRange.getUpper().intValue() > fpsRange.getLower().intValue()) {
                requestListSize = 1;
            }
            Log.m106v("CameraConstrainedHighSpeedCaptureSessionImpl", "Request list size is: " + requestListSize);
            List<CaptureRequest> requestList = new ArrayList<>();
            CameraMetadataNative requestMetadata = new CameraMetadataNative(request.getNativeCopy());
            CaptureRequest.Builder singleTargetRequestBuilder = new CaptureRequest.Builder(requestMetadata, false, -1, request.getLogicalCameraId(), null);
            singleTargetRequestBuilder.setTag(request.getTag());
            Iterator<Surface> iterator = outputSurfaces.iterator();
            Surface firstSurface = iterator.next();
            if (outputSurfaces.size() == 1 && SurfaceUtils.isSurfaceForHwVideoEncoder(firstSurface)) {
                singleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, 1);
            } else {
                singleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, 3);
            }
            singleTargetRequestBuilder.setPartOfCHSRequestList(true);
            CaptureRequest.Builder doubleTargetRequestBuilder = null;
            if (outputSurfaces.size() == 2) {
                CameraMetadataNative requestMetadata2 = new CameraMetadataNative(request.getNativeCopy());
                doubleTargetRequestBuilder = new CaptureRequest.Builder(requestMetadata2, false, -1, request.getLogicalCameraId(), null);
                doubleTargetRequestBuilder.setTag(request.getTag());
                doubleTargetRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, 3);
                doubleTargetRequestBuilder.addTarget(firstSurface);
                Surface secondSurface = iterator.next();
                doubleTargetRequestBuilder.addTarget(secondSurface);
                doubleTargetRequestBuilder.setPartOfCHSRequestList(true);
                Surface recordingSurface = firstSurface;
                if (!SurfaceUtils.isSurfaceForHwVideoEncoder(recordingSurface)) {
                    recordingSurface = secondSurface;
                }
                singleTargetRequestBuilder.addTarget(recordingSurface);
            } else {
                singleTargetRequestBuilder.addTarget(firstSurface);
            }
            for (int i = 0; i < requestListSize; i++) {
                if (i == 0 && doubleTargetRequestBuilder != null) {
                    requestList.add(doubleTargetRequestBuilder.build());
                } else {
                    requestList.add(singleTargetRequestBuilder.build());
                }
            }
            return Collections.unmodifiableList(requestList);
        }
        throw new IllegalArgumentException("Input capture request must not be null");
    }

    private boolean isConstrainedHighSpeedRequestList(List<CaptureRequest> requestList) {
        Preconditions.checkCollectionNotEmpty(requestList, "High speed request list");
        for (CaptureRequest request : requestList) {
            if (!request.isPartOfCRequestList()) {
                return false;
            }
        }
        return true;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraDevice getDevice() {
        return this.mSessionImpl.getDevice();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(Surface surface) throws CameraAccessException {
        this.mSessionImpl.prepare(surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(int maxCount, Surface surface) throws CameraAccessException {
        this.mSessionImpl.prepare(maxCount, surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void tearDown(Surface surface) throws CameraAccessException {
        this.mSessionImpl.tearDown(surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int capture(CaptureRequest request, CameraCaptureSession.CaptureCallback listener, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback listener, Handler handler) throws CameraAccessException {
        if (!isConstrainedHighSpeedRequestList(requests)) {
            throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
        }
        return this.mSessionImpl.captureBurst(requests, listener, handler);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        if (!isConstrainedHighSpeedRequestList(requests)) {
            throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
        }
        return this.mSessionImpl.captureBurstRequests(requests, executor, listener);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback listener, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback listener, Handler handler) throws CameraAccessException {
        if (!isConstrainedHighSpeedRequestList(requests)) {
            throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
        }
        return this.mSessionImpl.setRepeatingBurst(requests, listener, handler);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback listener) throws CameraAccessException {
        if (!isConstrainedHighSpeedRequestList(requests)) {
            throw new IllegalArgumentException("Only request lists created by createHighSpeedRequestList() can be submitted to a constrained high speed capture session");
        }
        return this.mSessionImpl.setRepeatingBurstRequests(requests, executor, listener);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void stopRepeating() throws CameraAccessException {
        this.mSessionImpl.stopRepeating();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void abortCaptures() throws CameraAccessException {
        this.mSessionImpl.abortCaptures();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public Surface getInputSurface() {
        return null;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void updateOutputConfiguration(OutputConfiguration config) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraOfflineSession switchToOffline(Collection<Surface> offlineOutputs, Executor executor, CameraOfflineSession.CameraOfflineSessionCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean supportsOfflineProcessing(Surface surface) {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support offline mode");
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public void closeWithoutDraining() {
        throw new UnsupportedOperationException("Constrained high speed session doesn't support this method");
    }

    @Override // android.hardware.camera2.CameraCaptureSession, java.lang.AutoCloseable
    public void close() {
        this.mSessionImpl.close();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean isReprocessable() {
        return false;
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public void replaceSessionClose() {
        this.mSessionImpl.replaceSessionClose();
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public CameraDeviceImpl.StateCallbackKK getDeviceStateCallback() {
        return this.mSessionImpl.getDeviceStateCallback();
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public boolean isAborting() {
        return this.mSessionImpl.isAborting();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void finalizeOutputConfigurations(List<OutputConfiguration> deferredOutputConfigs) throws CameraAccessException {
        this.mSessionImpl.finalizeOutputConfigurations(deferredOutputConfigs);
    }

    /* loaded from: classes.dex */
    private class WrapperCallback extends CameraCaptureSession.StateCallback {
        private final CameraCaptureSession.StateCallback mCallback;

        public WrapperCallback(CameraCaptureSession.StateCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(CameraCaptureSession session) {
            CameraConstrainedHighSpeedCaptureSessionImpl.this.mInitialized.block();
            this.mCallback.onConfigured(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(CameraCaptureSession session) {
            CameraConstrainedHighSpeedCaptureSessionImpl.this.mInitialized.block();
            this.mCallback.onConfigureFailed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onReady(CameraCaptureSession session) {
            this.mCallback.onReady(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onActive(CameraCaptureSession session) {
            this.mCallback.onActive(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onCaptureQueueEmpty(CameraCaptureSession session) {
            this.mCallback.onCaptureQueueEmpty(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onClosed(CameraCaptureSession session) {
            this.mCallback.onClosed(CameraConstrainedHighSpeedCaptureSessionImpl.this);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            this.mCallback.onSurfacePrepared(CameraConstrainedHighSpeedCaptureSessionImpl.this, surface);
        }
    }
}
