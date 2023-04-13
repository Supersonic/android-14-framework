package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraOfflineSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraOfflineSession;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class CameraOfflineSessionImpl extends CameraOfflineSession implements IBinder.DeathRecipient {
    private static final long NANO_PER_SECOND = 1000000000;
    private static final int REQUEST_ID_NONE = -1;
    private static final String TAG = "CameraOfflineSessionImpl";
    private final String mCameraId;
    private SparseArray<CaptureCallbackHolder> mCaptureCallbackMap;
    private final CameraCharacteristics mCharacteristics;
    private SparseArray<OutputConfiguration> mConfiguredOutputs;
    private FrameNumberTracker mFrameNumberTracker;
    private final CameraOfflineSession.CameraOfflineSessionCallback mOfflineCallback;
    private final Executor mOfflineExecutor;
    private AbstractMap.SimpleEntry<Integer, InputConfiguration> mOfflineInput;
    private SparseArray<OutputConfiguration> mOfflineOutputs;
    private ICameraOfflineSession mRemoteSession;
    private final int mTotalPartialCount;
    private final boolean DEBUG = false;
    private final AtomicBoolean mClosing = new AtomicBoolean();
    final Object mInterfaceLock = new Object();
    private final CameraDeviceCallbacks mCallbacks = new CameraDeviceCallbacks();
    private List<RequestLastFrameNumbersHolder> mOfflineRequestLastFrameNumbersList = new ArrayList();

    public CameraOfflineSessionImpl(String cameraId, CameraCharacteristics characteristics, Executor offlineExecutor, CameraOfflineSession.CameraOfflineSessionCallback offlineCallback, SparseArray<OutputConfiguration> offlineOutputs, AbstractMap.SimpleEntry<Integer, InputConfiguration> offlineInput, SparseArray<OutputConfiguration> configuredOutputs, FrameNumberTracker frameNumberTracker, SparseArray<CaptureCallbackHolder> callbackMap, List<RequestLastFrameNumbersHolder> frameNumberList) {
        this.mOfflineInput = new AbstractMap.SimpleEntry<>(-1, null);
        this.mOfflineOutputs = new SparseArray<>();
        this.mConfiguredOutputs = new SparseArray<>();
        this.mFrameNumberTracker = new FrameNumberTracker();
        this.mCaptureCallbackMap = new SparseArray<>();
        if (cameraId == null || characteristics == null) {
            throw new IllegalArgumentException("Null argument given");
        }
        this.mCameraId = cameraId;
        this.mCharacteristics = characteristics;
        Integer partialCount = (Integer) characteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT);
        if (partialCount == null) {
            this.mTotalPartialCount = 1;
        } else {
            this.mTotalPartialCount = partialCount.intValue();
        }
        this.mOfflineRequestLastFrameNumbersList.addAll(frameNumberList);
        this.mFrameNumberTracker = frameNumberTracker;
        this.mCaptureCallbackMap = callbackMap;
        this.mConfiguredOutputs = configuredOutputs;
        this.mOfflineOutputs = offlineOutputs;
        this.mOfflineInput = offlineInput;
        this.mOfflineExecutor = (Executor) Preconditions.checkNotNull(offlineExecutor, "offline executor must not be null");
        this.mOfflineCallback = (CameraOfflineSession.CameraOfflineSessionCallback) Preconditions.checkNotNull(offlineCallback, "offline callback must not be null");
    }

    public CameraDeviceCallbacks getCallbacks() {
        return this.mCallbacks;
    }

    /* loaded from: classes.dex */
    public class CameraDeviceCallbacks extends ICameraDeviceCallbacks.Stub {
        public CameraDeviceCallbacks() {
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks.Stub, android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onDeviceError(int errorCode, CaptureResultExtras resultExtras) {
            synchronized (CameraOfflineSessionImpl.this.mInterfaceLock) {
                try {
                    switch (errorCode) {
                        case 3:
                        case 4:
                        case 5:
                            onCaptureErrorLocked(errorCode, resultExtras);
                            break;
                        default:
                            Runnable errorDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (!CameraOfflineSessionImpl.this.isClosed()) {
                                        CameraOfflineSessionImpl.this.mOfflineCallback.onError(CameraOfflineSessionImpl.this, 0);
                                    }
                                }
                            };
                            long ident = Binder.clearCallingIdentity();
                            CameraOfflineSessionImpl.this.mOfflineExecutor.execute(errorDispatch);
                            Binder.restoreCallingIdentity(ident);
                            break;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onRepeatingRequestError(long lastFrameNumber, int repeatingRequestId) {
            Log.m110e(CameraOfflineSessionImpl.TAG, "Unexpected repeating request error received. Last frame number is " + lastFrameNumber);
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onDeviceIdle() {
            synchronized (CameraOfflineSessionImpl.this.mInterfaceLock) {
                if (CameraOfflineSessionImpl.this.mRemoteSession == null) {
                    Log.m106v(CameraOfflineSessionImpl.TAG, "Ignoring idle state notifications during offline switches");
                    return;
                }
                CameraOfflineSessionImpl.this.removeCompletedCallbackHolderLocked(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
                Runnable idleDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!CameraOfflineSessionImpl.this.isClosed()) {
                            CameraOfflineSessionImpl.this.mOfflineCallback.onIdle(CameraOfflineSessionImpl.this);
                        }
                    }
                };
                long ident = Binder.clearCallingIdentity();
                CameraOfflineSessionImpl.this.mOfflineExecutor.execute(idleDispatch);
                Binder.restoreCallingIdentity(ident);
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCaptureStarted(final CaptureResultExtras resultExtras, final long timestamp) {
            Object obj;
            int requestId = resultExtras.getRequestId();
            final long frameNumber = resultExtras.getFrameNumber();
            long lastCompletedRegularFrameNumber = resultExtras.getLastCompletedRegularFrameNumber();
            long lastCompletedReprocessFrameNumber = resultExtras.getLastCompletedReprocessFrameNumber();
            long lastCompletedZslFrameNumber = resultExtras.getLastCompletedZslFrameNumber();
            Object obj2 = CameraOfflineSessionImpl.this.mInterfaceLock;
            synchronized (obj2) {
                try {
                    try {
                        CameraOfflineSessionImpl.this.removeCompletedCallbackHolderLocked(lastCompletedRegularFrameNumber, lastCompletedReprocessFrameNumber, lastCompletedZslFrameNumber);
                        final CaptureCallbackHolder holder = (CaptureCallbackHolder) CameraOfflineSessionImpl.this.mCaptureCallbackMap.get(requestId);
                        if (holder == null) {
                            return;
                        }
                        Executor executor = holder.getCallback().getExecutor();
                        if (CameraOfflineSessionImpl.this.isClosed()) {
                            obj = obj2;
                        } else if (executor == null) {
                            obj = obj2;
                        } else {
                            long ident = Binder.clearCallingIdentity();
                            try {
                                try {
                                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.3
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            CameraCaptureSession.CaptureCallback callback = holder.getCallback().getSessionCallback();
                                            if (!CameraOfflineSessionImpl.this.isClosed() && callback != null) {
                                                int subsequenceId = resultExtras.getSubsequenceId();
                                                CaptureRequest request = holder.getRequest(subsequenceId);
                                                if (holder.hasBatchedOutputs()) {
                                                    Range<Integer> fpsRange = (Range) request.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
                                                    for (int i = 0; i < holder.getRequestCount(); i++) {
                                                        CaptureRequest cbRequest = holder.getRequest(i);
                                                        long cbTimestamp = timestamp - (((subsequenceId - i) * 1000000000) / fpsRange.getUpper().intValue());
                                                        long cbFrameNumber = frameNumber - (subsequenceId - i);
                                                        callback.onCaptureStarted(CameraOfflineSessionImpl.this, cbRequest, cbTimestamp, cbFrameNumber);
                                                    }
                                                    return;
                                                }
                                                callback.onCaptureStarted(CameraOfflineSessionImpl.this, holder.getRequest(resultExtras.getSubsequenceId()), timestamp, frameNumber);
                                            }
                                        }
                                    });
                                    Binder.restoreCallingIdentity(ident);
                                    return;
                                } catch (Throwable th) {
                                    th = th;
                                    Binder.restoreCallingIdentity(ident);
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onResultReceived(CameraMetadataNative result, final CaptureResultExtras resultExtras, PhysicalCaptureResultInfo[] physicalResults) throws RemoteException {
            Object obj;
            CameraMetadataNative resultCopy;
            Executor executor;
            long frameNumber;
            Object obj2;
            Runnable resultDispatch;
            TotalCaptureResult totalCaptureResult;
            int requestId = resultExtras.getRequestId();
            long frameNumber2 = resultExtras.getFrameNumber();
            Object obj3 = CameraOfflineSessionImpl.this.mInterfaceLock;
            synchronized (obj3) {
                try {
                    try {
                        result.set((CameraCharacteristics.Key<CameraCharacteristics.Key<Size>>) CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE, (CameraCharacteristics.Key<Size>) ((Size) CameraOfflineSessionImpl.this.mCharacteristics.get(CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE)));
                        final CaptureCallbackHolder holder = (CaptureCallbackHolder) CameraOfflineSessionImpl.this.mCaptureCallbackMap.get(requestId);
                        final CaptureRequest request = holder.getRequest(resultExtras.getSubsequenceId());
                        boolean isPartialResult = resultExtras.getPartialResultCount() < CameraOfflineSessionImpl.this.mTotalPartialCount;
                        int requestType = request.getRequestType();
                        try {
                            if (holder == null) {
                                CameraOfflineSessionImpl.this.mFrameNumberTracker.updateTracker(frameNumber2, null, isPartialResult, requestType);
                            } else if (CameraOfflineSessionImpl.this.isClosed()) {
                                CameraOfflineSessionImpl.this.mFrameNumberTracker.updateTracker(frameNumber2, null, isPartialResult, requestType);
                            } else {
                                if (holder.hasBatchedOutputs()) {
                                    resultCopy = new CameraMetadataNative(result);
                                } else {
                                    resultCopy = null;
                                }
                                Executor executor2 = holder.getCallback().getExecutor();
                                if (isPartialResult) {
                                    final CaptureResult resultAsCapture = new CaptureResult(CameraOfflineSessionImpl.this.mCameraId, result, request, resultExtras);
                                    final CameraMetadataNative cameraMetadataNative = resultCopy;
                                    executor = executor2;
                                    Runnable resultDispatch2 = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.4
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            CameraCaptureSession.CaptureCallback callback = holder.getCallback().getSessionCallback();
                                            if (!CameraOfflineSessionImpl.this.isClosed() && callback != null) {
                                                if (holder.hasBatchedOutputs()) {
                                                    for (int i = 0; i < holder.getRequestCount(); i++) {
                                                        CameraMetadataNative resultLocal = new CameraMetadataNative(cameraMetadataNative);
                                                        CaptureResult resultInBatch = new CaptureResult(CameraOfflineSessionImpl.this.mCameraId, resultLocal, holder.getRequest(i), resultExtras);
                                                        CaptureRequest cbRequest = holder.getRequest(i);
                                                        callback.onCaptureProgressed(CameraOfflineSessionImpl.this, cbRequest, resultInBatch);
                                                    }
                                                    return;
                                                }
                                                callback.onCaptureProgressed(CameraOfflineSessionImpl.this, request, resultAsCapture);
                                            }
                                        }
                                    };
                                    resultDispatch = resultDispatch2;
                                    obj2 = obj3;
                                    frameNumber = frameNumber2;
                                    totalCaptureResult = resultAsCapture;
                                } else {
                                    executor = executor2;
                                    final List<CaptureResult> partialResults = CameraOfflineSessionImpl.this.mFrameNumberTracker.popPartialResults(frameNumber2);
                                    final long sensorTimestamp = ((Long) result.get(CaptureResult.SENSOR_TIMESTAMP)).longValue();
                                    final Range<Integer> fpsRange = (Range) request.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
                                    final int subsequenceId = resultExtras.getSubsequenceId();
                                    frameNumber = frameNumber2;
                                    try {
                                        final TotalCaptureResult resultAsCapture2 = new TotalCaptureResult(CameraOfflineSessionImpl.this.mCameraId, result, request, resultExtras, partialResults, holder.getSessionId(), physicalResults);
                                        final CameraMetadataNative cameraMetadataNative2 = resultCopy;
                                        obj2 = obj3;
                                        Runnable resultDispatch3 = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.5
                                            @Override // java.lang.Runnable
                                            public void run() {
                                                CameraCaptureSession.CaptureCallback callback = holder.getCallback().getSessionCallback();
                                                if (!CameraOfflineSessionImpl.this.isClosed() && callback != null) {
                                                    if (holder.hasBatchedOutputs()) {
                                                        for (int i = 0; i < holder.getRequestCount(); i++) {
                                                            cameraMetadataNative2.set((CaptureResult.Key<CaptureResult.Key<Long>>) CaptureResult.SENSOR_TIMESTAMP, (CaptureResult.Key<Long>) Long.valueOf(sensorTimestamp - (((subsequenceId - i) * 1000000000) / ((Integer) fpsRange.getUpper()).intValue())));
                                                            CameraMetadataNative resultLocal = new CameraMetadataNative(cameraMetadataNative2);
                                                            TotalCaptureResult resultInBatch = new TotalCaptureResult(CameraOfflineSessionImpl.this.mCameraId, resultLocal, holder.getRequest(i), resultExtras, partialResults, holder.getSessionId(), new PhysicalCaptureResultInfo[0]);
                                                            CaptureRequest cbRequest = holder.getRequest(i);
                                                            callback.onCaptureCompleted(CameraOfflineSessionImpl.this, cbRequest, resultInBatch);
                                                        }
                                                        return;
                                                    }
                                                    callback.onCaptureCompleted(CameraOfflineSessionImpl.this, request, resultAsCapture2);
                                                }
                                            }
                                        };
                                        resultDispatch = resultDispatch3;
                                        totalCaptureResult = resultAsCapture2;
                                    } catch (Throwable th) {
                                        th = th;
                                        obj = obj3;
                                        throw th;
                                    }
                                }
                                Executor executor3 = executor;
                                if (executor3 != null) {
                                    long ident = Binder.clearCallingIdentity();
                                    executor3.execute(resultDispatch);
                                    Binder.restoreCallingIdentity(ident);
                                }
                                CameraOfflineSessionImpl.this.mFrameNumberTracker.updateTracker(frameNumber, totalCaptureResult, isPartialResult, requestType);
                                if (!isPartialResult) {
                                    CameraOfflineSessionImpl.this.checkAndFireSequenceComplete();
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            obj = obj3;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        obj = obj3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onPrepared(int streamId) {
            Log.m110e(CameraOfflineSessionImpl.TAG, "Unexpected stream " + streamId + " is prepared");
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onRequestQueueEmpty() {
            Log.m106v(CameraOfflineSessionImpl.TAG, "onRequestQueueEmpty");
        }

        private void onCaptureErrorLocked(int errorCode, CaptureResultExtras resultExtras) {
            long ident;
            OutputConfiguration config;
            int requestId = resultExtras.getRequestId();
            int subsequenceId = resultExtras.getSubsequenceId();
            final long frameNumber = resultExtras.getFrameNumber();
            String errorPhysicalCameraId = resultExtras.getErrorPhysicalCameraId();
            final CaptureCallbackHolder holder = (CaptureCallbackHolder) CameraOfflineSessionImpl.this.mCaptureCallbackMap.get(requestId);
            if (holder == null) {
                Log.m110e(CameraOfflineSessionImpl.TAG, String.format("Receive capture error on unknown request ID %d", Integer.valueOf(requestId)));
                return;
            }
            final CaptureRequest request = holder.getRequest(subsequenceId);
            if (errorCode == 5) {
                if (CameraOfflineSessionImpl.this.mRemoteSession == null && !CameraOfflineSessionImpl.this.isClosed()) {
                    config = (OutputConfiguration) CameraOfflineSessionImpl.this.mConfiguredOutputs.get(resultExtras.getErrorStreamId());
                } else {
                    config = (OutputConfiguration) CameraOfflineSessionImpl.this.mOfflineOutputs.get(resultExtras.getErrorStreamId());
                }
                if (config == null) {
                    Log.m106v(CameraOfflineSessionImpl.TAG, String.format("Stream %d has been removed. Skipping buffer lost callback", Integer.valueOf(resultExtras.getErrorStreamId())));
                    return;
                }
                for (final Surface surface : config.getSurfaces()) {
                    if (request.containsTarget(surface)) {
                        Executor executor = holder.getCallback().getExecutor();
                        final CaptureCallbackHolder captureCallbackHolder = holder;
                        CaptureCallbackHolder holder2 = holder;
                        Runnable failureDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.6
                            @Override // java.lang.Runnable
                            public void run() {
                                CameraCaptureSession.CaptureCallback callback = captureCallbackHolder.getCallback().getSessionCallback();
                                if (!CameraOfflineSessionImpl.this.isClosed() && callback != null) {
                                    callback.onCaptureBufferLost(CameraOfflineSessionImpl.this, request, surface, frameNumber);
                                }
                            }
                        };
                        if (executor != null) {
                            ident = Binder.clearCallingIdentity();
                            try {
                                executor.execute(failureDispatch);
                            } finally {
                            }
                        }
                        holder = holder2;
                    }
                }
                return;
            }
            boolean mayHaveBuffers = errorCode == 4;
            final CaptureFailure failure = new CaptureFailure(request, 0, mayHaveBuffers, requestId, frameNumber, errorPhysicalCameraId);
            Executor executor2 = holder.getCallback().getExecutor();
            Runnable failureDispatch2 = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.CameraDeviceCallbacks.7
                @Override // java.lang.Runnable
                public void run() {
                    CameraCaptureSession.CaptureCallback callback = holder.getCallback().getSessionCallback();
                    if (!CameraOfflineSessionImpl.this.isClosed() && callback != null) {
                        callback.onCaptureFailed(CameraOfflineSessionImpl.this, request, failure);
                    }
                }
            };
            CameraOfflineSessionImpl.this.mFrameNumberTracker.updateTracker(frameNumber, true, request.getRequestType());
            CameraOfflineSessionImpl.this.checkAndFireSequenceComplete();
            if (executor2 != null) {
                ident = Binder.clearCallingIdentity();
                try {
                    executor2.execute(failureDispatch2);
                } finally {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:57:? -> B:47:0x00cd). Please submit an issue!!! */
    public void checkAndFireSequenceComplete() {
        CaptureCallbackHolder holder;
        long completedFrameNumber;
        Executor executor;
        final CameraCaptureSession.CaptureCallback callback;
        long completedFrameNumber2 = this.mFrameNumberTracker.getCompletedFrameNumber();
        long completedReprocessFrameNumber = this.mFrameNumberTracker.getCompletedReprocessFrameNumber();
        long completedZslStillFrameNumber = this.mFrameNumberTracker.getCompletedZslStillFrameNumber();
        Iterator<RequestLastFrameNumbersHolder> iter = this.mOfflineRequestLastFrameNumbersList.iterator();
        while (iter.hasNext()) {
            final RequestLastFrameNumbersHolder requestLastFrameNumbers = iter.next();
            boolean sequenceCompleted = false;
            final int requestId = requestLastFrameNumbers.getRequestId();
            synchronized (this.mInterfaceLock) {
                try {
                    int index = this.mCaptureCallbackMap.indexOfKey(requestId);
                    if (index >= 0) {
                        try {
                            holder = this.mCaptureCallbackMap.valueAt(index);
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } else {
                        holder = null;
                    }
                    if (holder != null) {
                        long lastRegularFrameNumber = requestLastFrameNumbers.getLastRegularFrameNumber();
                        long lastReprocessFrameNumber = requestLastFrameNumbers.getLastReprocessFrameNumber();
                        long lastZslStillFrameNumber = requestLastFrameNumbers.getLastZslStillFrameNumber();
                        Executor executor2 = holder.getCallback().getExecutor();
                        CameraCaptureSession.CaptureCallback callback2 = holder.getCallback().getSessionCallback();
                        if (lastRegularFrameNumber > completedFrameNumber2 || lastReprocessFrameNumber > completedReprocessFrameNumber || lastZslStillFrameNumber > completedZslStillFrameNumber) {
                            completedFrameNumber = completedFrameNumber2;
                        } else {
                            sequenceCompleted = true;
                            completedFrameNumber = completedFrameNumber2;
                            try {
                                this.mCaptureCallbackMap.removeAt(index);
                            } catch (Throwable th2) {
                                th = th2;
                                throw th;
                            }
                        }
                        executor = executor2;
                        callback = callback2;
                    } else {
                        completedFrameNumber = completedFrameNumber2;
                        executor = null;
                        callback = null;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            if (holder == null || sequenceCompleted) {
                iter.remove();
            }
            if (sequenceCompleted && callback != null && executor != null) {
                Runnable resultDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!CameraOfflineSessionImpl.this.isClosed()) {
                            callback.onCaptureSequenceCompleted(CameraOfflineSessionImpl.this, requestId, requestLastFrameNumbers.getLastFrameNumber());
                        }
                    }
                };
                long ident = Binder.clearCallingIdentity();
                try {
                    executor.execute(resultDispatch);
                    Binder.restoreCallingIdentity(ident);
                    if (this.mCaptureCallbackMap.size() == 0) {
                        getCallbacks().onDeviceIdle();
                    }
                } catch (Throwable th4) {
                    Binder.restoreCallingIdentity(ident);
                    throw th4;
                }
            }
            completedFrameNumber2 = completedFrameNumber;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeCompletedCallbackHolderLocked(long lastCompletedRegularFrameNumber, long lastCompletedReprocessFrameNumber, long lastCompletedZslStillFrameNumber) {
        Iterator<RequestLastFrameNumbersHolder> iter = this.mOfflineRequestLastFrameNumbersList.iterator();
        while (iter.hasNext()) {
            RequestLastFrameNumbersHolder requestLastFrameNumbers = iter.next();
            int requestId = requestLastFrameNumbers.getRequestId();
            int index = this.mCaptureCallbackMap.indexOfKey(requestId);
            CaptureCallbackHolder holder = index >= 0 ? this.mCaptureCallbackMap.valueAt(index) : null;
            if (holder != null) {
                long lastRegularFrameNumber = requestLastFrameNumbers.getLastRegularFrameNumber();
                long lastReprocessFrameNumber = requestLastFrameNumbers.getLastReprocessFrameNumber();
                long lastZslStillFrameNumber = requestLastFrameNumbers.getLastZslStillFrameNumber();
                if (lastRegularFrameNumber <= lastCompletedRegularFrameNumber && lastReprocessFrameNumber <= lastCompletedReprocessFrameNumber && lastZslStillFrameNumber <= lastCompletedZslStillFrameNumber) {
                    if (requestLastFrameNumbers.isSequenceCompleted()) {
                        this.mCaptureCallbackMap.removeAt(index);
                        iter.remove();
                    } else {
                        Log.m110e(TAG, "Sequence not yet completed for request id " + requestId);
                    }
                }
            }
        }
    }

    public void notifyFailedSwitch() {
        synchronized (this.mInterfaceLock) {
            Runnable switchFailDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.2
                @Override // java.lang.Runnable
                public void run() {
                    CameraOfflineSessionImpl.this.mOfflineCallback.onSwitchFailed(CameraOfflineSessionImpl.this);
                }
            };
            long ident = Binder.clearCallingIdentity();
            this.mOfflineExecutor.execute(switchFailDispatch);
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void setRemoteSession(ICameraOfflineSession remoteSession) throws CameraAccessException {
        synchronized (this.mInterfaceLock) {
            if (remoteSession == null) {
                notifyFailedSwitch();
                return;
            }
            this.mRemoteSession = remoteSession;
            IBinder remoteSessionBinder = remoteSession.asBinder();
            if (remoteSessionBinder == null) {
                throw new CameraAccessException(2, "The camera offline session has encountered a serious error");
            }
            try {
                remoteSessionBinder.linkToDeath(this, 0);
                Runnable readyDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!CameraOfflineSessionImpl.this.isClosed()) {
                            CameraOfflineSessionImpl.this.mOfflineCallback.onReady(CameraOfflineSessionImpl.this);
                        }
                    }
                };
                long ident = Binder.clearCallingIdentity();
                this.mOfflineExecutor.execute(readyDispatch);
                Binder.restoreCallingIdentity(ident);
            } catch (RemoteException e) {
                throw new CameraAccessException(2, "The camera offline session has encountered a serious error");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isClosed() {
        return this.mClosing.get();
    }

    private void disconnect() {
        synchronized (this.mInterfaceLock) {
            if (this.mClosing.getAndSet(true)) {
                return;
            }
            ICameraOfflineSession iCameraOfflineSession = this.mRemoteSession;
            if (iCameraOfflineSession != null) {
                iCameraOfflineSession.asBinder().unlinkToDeath(this, 0);
                try {
                    this.mRemoteSession.disconnect();
                } catch (RemoteException e) {
                    Log.m109e(TAG, "Exception while disconnecting from offline session: ", e);
                }
                this.mRemoteSession = null;
                Runnable closeDispatch = new Runnable() { // from class: android.hardware.camera2.impl.CameraOfflineSessionImpl.4
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraOfflineSessionImpl.this.mOfflineCallback.onClosed(CameraOfflineSessionImpl.this);
                    }
                };
                long ident = Binder.clearCallingIdentity();
                this.mOfflineExecutor.execute(closeDispatch);
                Binder.restoreCallingIdentity(ident);
                return;
            }
            throw new IllegalStateException("Offline session is not yet ready");
        }
    }

    protected void finalize() throws Throwable {
        try {
            disconnect();
        } finally {
            super.finalize();
        }
    }

    @Override // android.p008os.IBinder.DeathRecipient
    public void binderDied() {
        Log.m104w(TAG, "CameraOfflineSession on device " + this.mCameraId + " died unexpectedly");
        disconnect();
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraDevice getDevice() {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(Surface surface) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(int maxCount, Surface surface) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void tearDown(Surface surface) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void finalizeOutputConfigurations(List<OutputConfiguration> outputConfigs) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int capture(CaptureRequest request, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void stopRepeating() throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void abortCaptures() throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void updateOutputConfiguration(OutputConfiguration config) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean isReprocessable() {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public Surface getInputSurface() {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraOfflineSession switchToOffline(Collection<Surface> offlineOutputs, Executor executor, CameraOfflineSession.CameraOfflineSessionCallback listener) throws CameraAccessException {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean supportsOfflineProcessing(Surface surface) {
        throw new UnsupportedOperationException("Operation not supported in offline mode");
    }

    @Override // android.hardware.camera2.CameraOfflineSession, android.hardware.camera2.CameraCaptureSession, java.lang.AutoCloseable
    public void close() {
        disconnect();
    }
}
