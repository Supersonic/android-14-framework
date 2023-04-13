package android.hardware.camera2.impl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.SyncFence;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraExtensionCharacteristics;
import android.hardware.camera2.CameraExtensionSession;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.extension.CaptureBundle;
import android.hardware.camera2.extension.CaptureStageImpl;
import android.hardware.camera2.extension.ICaptureProcessorImpl;
import android.hardware.camera2.extension.IImageCaptureExtenderImpl;
import android.hardware.camera2.extension.IInitializeSessionCallback;
import android.hardware.camera2.extension.IPreviewExtenderImpl;
import android.hardware.camera2.extension.IProcessResultImpl;
import android.hardware.camera2.extension.IRequestUpdateProcessorImpl;
import android.hardware.camera2.extension.LatencyPair;
import android.hardware.camera2.extension.ParcelImage;
import android.hardware.camera2.impl.CameraExtensionSessionImpl;
import android.hardware.camera2.impl.CameraExtensionUtils;
import android.hardware.camera2.params.ExtensionSessionConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.utils.SurfaceUtils;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageWriter;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.RemoteException;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class CameraExtensionSessionImpl extends CameraExtensionSession {
    private static final int PREVIEW_QUEUE_SIZE = 10;
    private static final String TAG = "CameraExtensionSessionImpl";
    private final CameraExtensionSession.StateCallback mCallbacks;
    private Surface mCameraBurstSurface;
    private final CameraDevice mCameraDevice;
    private Surface mCameraRepeatingSurface;
    private boolean mCaptureResultsSupported;
    private Surface mClientCaptureSurface;
    private Surface mClientPostviewSurface;
    private Surface mClientRepeatingRequestSurface;
    private final Executor mExecutor;
    private final long mExtensionClientId;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final IImageCaptureExtenderImpl mImageExtender;
    private final InitializeSessionHandler mInitializeHandler;
    private boolean mInitialized;
    private final IPreviewExtenderImpl mPreviewExtender;
    private final int mSessionId;
    private final List<Size> mSupportedPreviewSizes;
    private final Set<CaptureRequest.Key> mSupportedRequestKeys;
    private final Set<CaptureResult.Key> mSupportedResultKeys;
    private CameraCaptureSession mCaptureSession = null;
    private ImageReader mRepeatingRequestImageReader = null;
    private ImageReader mBurstCaptureImageReader = null;
    private ImageReader mStubCaptureImageReader = null;
    private ImageWriter mRepeatingRequestImageWriter = null;
    private CameraOutputImageCallback mRepeatingRequestImageCallback = null;
    private CameraOutputImageCallback mBurstCaptureImageCallback = null;
    private CameraExtensionJpegProcessor mImageJpegProcessor = null;
    private ICaptureProcessorImpl mImageProcessor = null;
    private CameraExtensionForwardProcessor mPreviewImageProcessor = null;
    private IRequestUpdateProcessorImpl mPreviewRequestUpdateProcessor = null;
    private int mPreviewProcessorType = 2;
    private boolean mInternalRepeatingRequestEnabled = true;
    final Object mInterfaceLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader, Image image);

        void onImageDropped(long j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int nativeGetSurfaceFormat(Surface surface) {
        return SurfaceUtils.getSurfaceFormat(surface);
    }

    public static CameraExtensionSessionImpl createCameraExtensionSession(CameraDevice cameraDevice, Context ctx, ExtensionSessionConfiguration config, int sessionId) throws CameraAccessException, RemoteException {
        int[] iArr;
        int suitableSurfaceCount;
        Surface postviewSurface;
        Size burstCaptureSurfaceSize;
        long clientId = CameraExtensionCharacteristics.registerClient(ctx);
        if (clientId < 0) {
            throw new UnsupportedOperationException("Unsupported extension!");
        }
        String cameraId = cameraDevice.getId();
        CameraManager manager = (CameraManager) ctx.getSystemService(CameraManager.class);
        CameraCharacteristics chars = manager.getCameraCharacteristics(cameraId);
        CameraExtensionCharacteristics extensionChars = new CameraExtensionCharacteristics(ctx, cameraId, chars);
        if (!CameraExtensionCharacteristics.isExtensionSupported(cameraDevice.getId(), config.getExtension(), chars)) {
            throw new UnsupportedOperationException("Unsupported extension type: " + config.getExtension());
        }
        if (!config.getOutputConfigurations().isEmpty() && config.getOutputConfigurations().size() <= 2) {
            for (OutputConfiguration c : config.getOutputConfigurations()) {
                if (c.getDynamicRangeProfile() == 1) {
                    if (c.getStreamUseCase() != 0) {
                        throw new IllegalArgumentException("Unsupported stream use case: " + c.getStreamUseCase());
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported dynamic range profile: " + c.getDynamicRangeProfile());
                }
            }
            Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = CameraExtensionCharacteristics.initializeExtension(config.getExtension());
            int suitableSurfaceCount2 = 0;
            List<Size> supportedPreviewSizes = extensionChars.getExtensionSupportedSizes(config.getExtension(), SurfaceTexture.class);
            Surface repeatingRequestSurface = CameraExtensionUtils.getRepeatingRequestSurface(config.getOutputConfigurations(), supportedPreviewSizes);
            if (repeatingRequestSurface != null) {
                suitableSurfaceCount2 = 0 + 1;
            }
            HashMap<Integer, List<Size>> supportedCaptureSizes = new HashMap<>();
            int i = 0;
            for (int format : CameraExtensionUtils.SUPPORTED_CAPTURE_OUTPUT_FORMATS) {
                List<Size> supportedSizes = extensionChars.getExtensionSupportedSizes(config.getExtension(), format);
                if (supportedSizes != null) {
                    supportedCaptureSizes.put(Integer.valueOf(format), supportedSizes);
                }
            }
            Surface burstCaptureSurface = CameraExtensionUtils.getBurstCaptureSurface(config.getOutputConfigurations(), supportedCaptureSizes);
            if (burstCaptureSurface == null) {
                suitableSurfaceCount = suitableSurfaceCount2;
            } else {
                suitableSurfaceCount = suitableSurfaceCount2 + 1;
            }
            if (suitableSurfaceCount != config.getOutputConfigurations().size()) {
                throw new IllegalArgumentException("One or more unsupported output surfaces found!");
            }
            Surface postviewSurface2 = null;
            if (burstCaptureSurface == null || config.getPostviewOutputConfiguration() == null) {
                postviewSurface = null;
            } else {
                CameraExtensionUtils.SurfaceInfo burstCaptureSurfaceInfo = CameraExtensionUtils.querySurface(burstCaptureSurface);
                Size burstCaptureSurfaceSize2 = new Size(burstCaptureSurfaceInfo.mWidth, burstCaptureSurfaceInfo.mHeight);
                HashMap<Integer, List<Size>> supportedPostviewSizes = new HashMap<>();
                int[] iArr2 = CameraExtensionUtils.SUPPORTED_CAPTURE_OUTPUT_FORMATS;
                int length = iArr2.length;
                while (i < length) {
                    int format2 = iArr2[i];
                    Surface postviewSurface3 = postviewSurface2;
                    List<Size> supportedSizesPostview = extensionChars.getPostviewSupportedSizes(config.getExtension(), burstCaptureSurfaceSize2, format2);
                    if (supportedSizesPostview == null) {
                        burstCaptureSurfaceSize = burstCaptureSurfaceSize2;
                    } else {
                        burstCaptureSurfaceSize = burstCaptureSurfaceSize2;
                        supportedPostviewSizes.put(Integer.valueOf(format2), supportedSizesPostview);
                    }
                    i++;
                    postviewSurface2 = postviewSurface3;
                    burstCaptureSurfaceSize2 = burstCaptureSurfaceSize;
                }
                Surface postviewSurface4 = CameraExtensionUtils.getPostviewSurface(config.getPostviewOutputConfiguration(), supportedPostviewSizes, burstCaptureSurfaceInfo.mFormat);
                if (postviewSurface4 == null) {
                    throw new IllegalArgumentException("Unsupported output surface for postview!");
                }
                postviewSurface = postviewSurface4;
            }
            extenders.first.init(cameraId, chars.getNativeMetadata());
            extenders.first.onInit(cameraId, chars.getNativeMetadata());
            extenders.second.init(cameraId, chars.getNativeMetadata());
            extenders.second.onInit(cameraId, chars.getNativeMetadata());
            CameraExtensionSessionImpl session = new CameraExtensionSessionImpl(extenders.second, extenders.first, supportedPreviewSizes, clientId, cameraDevice, repeatingRequestSurface, burstCaptureSurface, postviewSurface, config.getStateCallback(), config.getExecutor(), sessionId, extensionChars.getAvailableCaptureRequestKeys(config.getExtension()), extensionChars.getAvailableCaptureResultKeys(config.getExtension()));
            session.initialize();
            return session;
        }
        throw new IllegalArgumentException("Unexpected amount of output surfaces, received: " + config.getOutputConfigurations().size() + " expected <= 2");
    }

    public CameraExtensionSessionImpl(IImageCaptureExtenderImpl imageExtender, IPreviewExtenderImpl previewExtender, List<Size> previewSizes, long extensionClientId, CameraDevice cameraDevice, Surface repeatingRequestSurface, Surface burstCaptureSurface, Surface postviewSurface, CameraExtensionSession.StateCallback callback, Executor executor, int sessionId, Set<CaptureRequest.Key> requestKeys, Set<CaptureResult.Key> resultKeys) {
        this.mExtensionClientId = extensionClientId;
        this.mImageExtender = imageExtender;
        this.mPreviewExtender = previewExtender;
        this.mCameraDevice = cameraDevice;
        this.mCallbacks = callback;
        this.mExecutor = executor;
        this.mClientRepeatingRequestSurface = repeatingRequestSurface;
        this.mClientCaptureSurface = burstCaptureSurface;
        this.mClientPostviewSurface = postviewSurface;
        this.mSupportedPreviewSizes = previewSizes;
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        this.mInitialized = false;
        this.mInitializeHandler = new InitializeSessionHandler();
        this.mSessionId = sessionId;
        this.mSupportedRequestKeys = requestKeys;
        this.mSupportedResultKeys = resultKeys;
        this.mCaptureResultsSupported = !resultKeys.isEmpty();
    }

    private void initializeRepeatingRequestPipeline() throws RemoteException {
        CameraExtensionUtils.SurfaceInfo repeatingSurfaceInfo = new CameraExtensionUtils.SurfaceInfo();
        this.mPreviewProcessorType = this.mPreviewExtender.getProcessorType();
        Surface surface = this.mClientRepeatingRequestSurface;
        if (surface != null) {
            repeatingSurfaceInfo = CameraExtensionUtils.querySurface(surface);
        } else {
            CameraExtensionUtils.SurfaceInfo captureSurfaceInfo = CameraExtensionUtils.querySurface(this.mClientCaptureSurface);
            Size captureSize = new Size(captureSurfaceInfo.mWidth, captureSurfaceInfo.mHeight);
            Size previewSize = findSmallestAspectMatchedSize(this.mSupportedPreviewSizes, captureSize);
            repeatingSurfaceInfo.mWidth = previewSize.getWidth();
            repeatingSurfaceInfo.mHeight = previewSize.getHeight();
            repeatingSurfaceInfo.mUsage = 256L;
        }
        int i = this.mPreviewProcessorType;
        if (i == 1) {
            try {
                CameraExtensionForwardProcessor cameraExtensionForwardProcessor = new CameraExtensionForwardProcessor(this.mPreviewExtender.getPreviewImageProcessor(), repeatingSurfaceInfo.mFormat, repeatingSurfaceInfo.mUsage, this.mHandler);
                this.mPreviewImageProcessor = cameraExtensionForwardProcessor;
                cameraExtensionForwardProcessor.onImageFormatUpdate(35);
                this.mPreviewImageProcessor.onResolutionUpdate(new Size(repeatingSurfaceInfo.mWidth, repeatingSurfaceInfo.mHeight));
                this.mPreviewImageProcessor.onOutputSurface(null, -1);
                ImageReader newInstance = ImageReader.newInstance(repeatingSurfaceInfo.mWidth, repeatingSurfaceInfo.mHeight, 35, 10, repeatingSurfaceInfo.mUsage);
                this.mRepeatingRequestImageReader = newInstance;
                this.mCameraRepeatingSurface = newInstance.getSurface();
            } catch (ClassCastException e) {
                throw new UnsupportedOperationException("Failed casting preview processor!");
            }
        } else if (i == 0) {
            try {
                this.mPreviewRequestUpdateProcessor = this.mPreviewExtender.getRequestUpdateProcessor();
                ImageReader newInstance2 = ImageReader.newInstance(repeatingSurfaceInfo.mWidth, repeatingSurfaceInfo.mHeight, 34, 10, repeatingSurfaceInfo.mUsage);
                this.mRepeatingRequestImageReader = newInstance2;
                this.mCameraRepeatingSurface = newInstance2.getSurface();
                android.hardware.camera2.extension.Size sz = new android.hardware.camera2.extension.Size();
                sz.width = repeatingSurfaceInfo.mWidth;
                sz.height = repeatingSurfaceInfo.mHeight;
                this.mPreviewRequestUpdateProcessor.onResolutionUpdate(sz);
                this.mPreviewRequestUpdateProcessor.onImageFormatUpdate(34);
            } catch (ClassCastException e2) {
                throw new UnsupportedOperationException("Failed casting preview processor!");
            }
        } else {
            ImageReader newInstance3 = ImageReader.newInstance(repeatingSurfaceInfo.mWidth, repeatingSurfaceInfo.mHeight, 34, 10, repeatingSurfaceInfo.mUsage);
            this.mRepeatingRequestImageReader = newInstance3;
            this.mCameraRepeatingSurface = newInstance3.getSurface();
        }
        CameraOutputImageCallback cameraOutputImageCallback = new CameraOutputImageCallback(this.mRepeatingRequestImageReader);
        this.mRepeatingRequestImageCallback = cameraOutputImageCallback;
        this.mRepeatingRequestImageReader.setOnImageAvailableListener(cameraOutputImageCallback, this.mHandler);
    }

    private void initializeBurstCapturePipeline() throws RemoteException {
        ICaptureProcessorImpl captureProcessor = this.mImageExtender.getCaptureProcessor();
        this.mImageProcessor = captureProcessor;
        if (captureProcessor == null && this.mImageExtender.getMaxCaptureStage() != 1) {
            throw new UnsupportedOperationException("Multiple stages expected without a valid capture processor!");
        }
        if (this.mImageProcessor != null) {
            Surface surface = this.mClientCaptureSurface;
            if (surface != null) {
                CameraExtensionUtils.SurfaceInfo surfaceInfo = CameraExtensionUtils.querySurface(surface);
                if (surfaceInfo.mFormat == 256) {
                    CameraExtensionJpegProcessor cameraExtensionJpegProcessor = new CameraExtensionJpegProcessor(this.mImageProcessor);
                    this.mImageJpegProcessor = cameraExtensionJpegProcessor;
                    this.mImageProcessor = cameraExtensionJpegProcessor;
                }
                this.mBurstCaptureImageReader = ImageReader.newInstance(surfaceInfo.mWidth, surfaceInfo.mHeight, 35, this.mImageExtender.getMaxCaptureStage());
            } else {
                this.mBurstCaptureImageReader = ImageReader.newInstance(this.mRepeatingRequestImageReader.getWidth(), this.mRepeatingRequestImageReader.getHeight(), 35, 1);
                ImageReader newInstance = ImageReader.newInstance(this.mRepeatingRequestImageReader.getWidth(), this.mRepeatingRequestImageReader.getHeight(), 35, 1);
                this.mStubCaptureImageReader = newInstance;
                this.mImageProcessor.onOutputSurface(newInstance.getSurface(), 35);
            }
            CameraOutputImageCallback cameraOutputImageCallback = new CameraOutputImageCallback(this.mBurstCaptureImageReader);
            this.mBurstCaptureImageCallback = cameraOutputImageCallback;
            this.mBurstCaptureImageReader.setOnImageAvailableListener(cameraOutputImageCallback, this.mHandler);
            this.mCameraBurstSurface = this.mBurstCaptureImageReader.getSurface();
            android.hardware.camera2.extension.Size sz = new android.hardware.camera2.extension.Size();
            sz.width = this.mBurstCaptureImageReader.getWidth();
            sz.height = this.mBurstCaptureImageReader.getHeight();
            Surface surface2 = this.mClientPostviewSurface;
            if (surface2 != null) {
                CameraExtensionUtils.SurfaceInfo postviewSurfaceInfo = CameraExtensionUtils.querySurface(surface2);
                android.hardware.camera2.extension.Size postviewSize = new android.hardware.camera2.extension.Size();
                postviewSize.width = postviewSurfaceInfo.mWidth;
                postviewSize.height = postviewSurfaceInfo.mHeight;
                this.mImageProcessor.onResolutionUpdate(sz, postviewSize);
            } else {
                this.mImageProcessor.onResolutionUpdate(sz, null);
            }
            this.mImageProcessor.onImageFormatUpdate(this.mBurstCaptureImageReader.getImageFormat());
            return;
        }
        Surface surface3 = this.mClientCaptureSurface;
        if (surface3 != null) {
            this.mCameraBurstSurface = surface3;
            return;
        }
        ImageReader newInstance2 = ImageReader.newInstance(this.mRepeatingRequestImageReader.getWidth(), this.mRepeatingRequestImageReader.getHeight(), 256, 1);
        this.mBurstCaptureImageReader = newInstance2;
        this.mCameraBurstSurface = newInstance2.getSurface();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishPipelineInitialization() throws RemoteException {
        Surface surface = this.mClientRepeatingRequestSurface;
        if (surface != null) {
            int i = this.mPreviewProcessorType;
            if (i == 0) {
                this.mPreviewRequestUpdateProcessor.onOutputSurface(surface, nativeGetSurfaceFormat(surface));
                this.mRepeatingRequestImageWriter = ImageWriter.newInstance(this.mClientRepeatingRequestSurface, 10, 34);
            } else if (i == 2) {
                this.mRepeatingRequestImageWriter = ImageWriter.newInstance(surface, 10, 34);
            }
        }
        ICaptureProcessorImpl iCaptureProcessorImpl = this.mImageProcessor;
        if (iCaptureProcessorImpl != null && this.mClientCaptureSurface != null) {
            Surface surface2 = this.mClientPostviewSurface;
            if (surface2 != null) {
                iCaptureProcessorImpl.onPostviewOutputSurface(surface2);
            }
            CameraExtensionUtils.SurfaceInfo surfaceInfo = CameraExtensionUtils.querySurface(this.mClientCaptureSurface);
            this.mImageProcessor.onOutputSurface(this.mClientCaptureSurface, surfaceInfo.mFormat);
        }
    }

    public synchronized void initialize() throws CameraAccessException, RemoteException {
        if (this.mInitialized) {
            Log.m112d(TAG, "Session already initialized");
            return;
        }
        int previewSessionType = this.mPreviewExtender.getSessionType();
        int imageSessionType = this.mImageExtender.getSessionType();
        if (previewSessionType != imageSessionType) {
            throw new IllegalStateException("Preview extender session type: " + previewSessionType + "and image extender session type: " + imageSessionType + " mismatch!");
        }
        int sessionType = 0;
        if (previewSessionType != -1 && previewSessionType != 1) {
            sessionType = previewSessionType;
            Log.m106v(TAG, "Using session type: " + sessionType);
        }
        ArrayList<CaptureStageImpl> sessionParamsList = new ArrayList<>();
        ArrayList<OutputConfiguration> outputList = new ArrayList<>();
        initializeRepeatingRequestPipeline();
        OutputConfiguration previewOutput = new OutputConfiguration(this.mCameraRepeatingSurface);
        previewOutput.setTimestampBase(1);
        previewOutput.setReadoutTimestampEnabled(false);
        outputList.add(previewOutput);
        CaptureStageImpl previewSessionParams = this.mPreviewExtender.onPresetSession();
        if (previewSessionParams != null) {
            sessionParamsList.add(previewSessionParams);
        }
        initializeBurstCapturePipeline();
        OutputConfiguration captureOutput = new OutputConfiguration(this.mCameraBurstSurface);
        captureOutput.setTimestampBase(1);
        captureOutput.setReadoutTimestampEnabled(false);
        outputList.add(captureOutput);
        CaptureStageImpl stillCaptureSessionParams = this.mImageExtender.onPresetSession();
        if (stillCaptureSessionParams != null) {
            sessionParamsList.add(stillCaptureSessionParams);
        }
        SessionConfiguration sessionConfig = new SessionConfiguration(sessionType, outputList, new CameraExtensionUtils.HandlerExecutor(this.mHandler), new SessionStateHandler());
        if (!sessionParamsList.isEmpty()) {
            CaptureRequest sessionParamRequest = createRequest(this.mCameraDevice, sessionParamsList, null, 1);
            sessionConfig.setSessionParameters(sessionParamRequest);
        }
        this.mCameraDevice.createCaptureSession(sessionConfig);
    }

    @Override // android.hardware.camera2.CameraExtensionSession
    public CameraDevice getDevice() {
        CameraDevice cameraDevice;
        synchronized (this.mInterfaceLock) {
            cameraDevice = this.mCameraDevice;
        }
        return cameraDevice;
    }

    @Override // android.hardware.camera2.CameraExtensionSession
    public CameraExtensionSession.StillCaptureLatency getRealtimeStillCaptureLatency() throws CameraAccessException {
        synchronized (this.mInterfaceLock) {
            if (!this.mInitialized) {
                throw new IllegalStateException("Uninitialized component");
            }
            try {
                LatencyPair latency = this.mImageExtender.getRealtimeCaptureLatency();
                if (latency != null) {
                    return new CameraExtensionSession.StillCaptureLatency(latency.first, latency.second);
                }
                return null;
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to query realtime latency! Extension service does not respond");
                throw new CameraAccessException(3);
            }
        }
    }

    @Override // android.hardware.camera2.CameraExtensionSession
    public int setRepeatingRequest(CaptureRequest request, Executor executor, CameraExtensionSession.ExtensionCaptureCallback listener) throws CameraAccessException {
        int repeatingRequest;
        synchronized (this.mInterfaceLock) {
            if (!this.mInitialized) {
                throw new IllegalStateException("Uninitialized component");
            }
            Surface surface = this.mClientRepeatingRequestSurface;
            if (surface == null) {
                throw new IllegalArgumentException("No registered preview surface");
            }
            if (!request.containsTarget(surface) || request.getTargets().size() != 1) {
                throw new IllegalArgumentException("Invalid repeating request output target!");
            }
            this.mInternalRepeatingRequestEnabled = false;
            try {
                repeatingRequest = setRepeatingRequest(this.mPreviewExtender.getCaptureStage(), new PreviewRequestHandler(this, request, executor, listener, this.mRepeatingRequestImageCallback), request);
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to set repeating request! Extension service does not respond");
                throw new CameraAccessException(3);
            }
        }
        return repeatingRequest;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<CaptureStageImpl> compileInitialRequestList() {
        ArrayList<CaptureStageImpl> captureStageList = new ArrayList<>();
        try {
            CaptureStageImpl initialPreviewParams = this.mPreviewExtender.onEnableSession();
            if (initialPreviewParams != null) {
                captureStageList.add(initialPreviewParams);
            }
            CaptureStageImpl initialStillCaptureParams = this.mImageExtender.onEnableSession();
            if (initialStillCaptureParams != null) {
                captureStageList.add(initialStillCaptureParams);
            }
        } catch (RemoteException e) {
            Log.m110e(TAG, "Failed to initialize session parameters! Extension service does not respond!");
        }
        return captureStageList;
    }

    private List<CaptureRequest> createBurstRequest(CameraDevice cameraDevice, List<CaptureStageImpl> captureStageList, CaptureRequest clientRequest, Surface target, int captureTemplate, Map<CaptureRequest, Integer> captureMap) {
        ArrayList<CaptureRequest> ret = new ArrayList<>();
        for (CaptureStageImpl captureStage : captureStageList) {
            try {
                CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(captureTemplate);
                for (CaptureRequest.Key requestKey : this.mSupportedRequestKeys) {
                    Object value = clientRequest.get(requestKey);
                    if (value != null) {
                        captureStage.parameters.set((CaptureRequest.Key<CaptureRequest.Key>) requestKey, (CaptureRequest.Key) value);
                    }
                }
                requestBuilder.addTarget(target);
                CaptureRequest request = requestBuilder.build();
                CameraMetadataNative.update(request.getNativeMetadata(), captureStage.parameters);
                ret.add(request);
                if (captureMap != null) {
                    captureMap.put(request, Integer.valueOf(captureStage.f113id));
                }
            } catch (CameraAccessException e) {
                return null;
            }
        }
        return ret;
    }

    private CaptureRequest createRequest(CameraDevice cameraDevice, List<CaptureStageImpl> captureStageList, Surface target, int captureTemplate, CaptureRequest clientRequest) throws CameraAccessException {
        CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(captureTemplate);
        if (target != null) {
            requestBuilder.addTarget(target);
        }
        CaptureRequest ret = requestBuilder.build();
        CameraMetadataNative nativeMeta = ret.getNativeMetadata();
        for (CaptureStageImpl captureStage : captureStageList) {
            if (captureStage != null) {
                CameraMetadataNative.update(nativeMeta, captureStage.parameters);
            }
        }
        if (clientRequest != null) {
            for (CaptureRequest.Key requestKey : this.mSupportedRequestKeys) {
                Object value = clientRequest.get(requestKey);
                if (value != null) {
                    nativeMeta.set((CaptureRequest.Key<CaptureRequest.Key>) requestKey, (CaptureRequest.Key) value);
                }
            }
        }
        return ret;
    }

    private CaptureRequest createRequest(CameraDevice cameraDevice, List<CaptureStageImpl> captureStageList, Surface target, int captureTemplate) throws CameraAccessException {
        return createRequest(cameraDevice, captureStageList, target, captureTemplate, null);
    }

    @Override // android.hardware.camera2.CameraExtensionSession
    public int capture(CaptureRequest request, Executor executor, CameraExtensionSession.ExtensionCaptureCallback listener) throws CameraAccessException {
        if (!this.mInitialized) {
            throw new IllegalStateException("Uninitialized component");
        }
        validateCaptureRequestTargets(request);
        Surface surface = this.mClientCaptureSurface;
        if (surface != null && request.containsTarget(surface)) {
            HashMap<CaptureRequest, Integer> requestMap = new HashMap<>();
            try {
                List<CaptureRequest> burstRequest = createBurstRequest(this.mCameraDevice, this.mImageExtender.getCaptureStages(), request, this.mCameraBurstSurface, 2, requestMap);
                if (burstRequest != null) {
                    int seqId = this.mCaptureSession.captureBurstRequests(burstRequest, new CameraExtensionUtils.HandlerExecutor(this.mHandler), new BurstRequestHandler(request, executor, listener, requestMap, this.mBurstCaptureImageCallback));
                    return seqId;
                }
                throw new UnsupportedOperationException("Failed to create still capture burst request");
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to initialize internal burst request! Extension service does not respond!");
                throw new CameraAccessException(3);
            }
        }
        Surface surface2 = this.mClientRepeatingRequestSurface;
        if (surface2 != null && request.containsTarget(surface2)) {
            try {
                ArrayList<CaptureStageImpl> captureStageList = new ArrayList<>();
                captureStageList.add(this.mPreviewExtender.getCaptureStage());
                CaptureRequest captureRequest = createRequest(this.mCameraDevice, captureStageList, this.mCameraRepeatingSurface, 1, request);
                int seqId2 = this.mCaptureSession.capture(captureRequest, new PreviewRequestHandler(request, executor, listener, this.mRepeatingRequestImageCallback, true), this.mHandler);
                return seqId2;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to initialize capture request! Extension service does not respond!");
                throw new CameraAccessException(3);
            }
        }
        throw new IllegalArgumentException("Capture request to unknown output surface!");
    }

    private void validateCaptureRequestTargets(CaptureRequest request) {
        boolean containsRepeatingTarget = true;
        if (request.getTargets().size() == 1) {
            Surface surface = this.mClientCaptureSurface;
            boolean containsCaptureTarget = surface != null && request.containsTarget(surface);
            Surface surface2 = this.mClientRepeatingRequestSurface;
            if (surface2 == null || !request.containsTarget(surface2)) {
                containsRepeatingTarget = false;
            }
            if (!containsCaptureTarget && !containsRepeatingTarget) {
                throw new IllegalArgumentException("Target output combination requested is not supported!");
            }
        }
        if (request.getTargets().size() == 2 && !request.getTargets().containsAll(Arrays.asList(this.mClientCaptureSurface, this.mClientPostviewSurface))) {
            throw new IllegalArgumentException("Target output combination requested is not supported!");
        }
        if (request.getTargets().size() > 2) {
            throw new IllegalArgumentException("Target output combination requested is not supported!");
        }
    }

    @Override // android.hardware.camera2.CameraExtensionSession
    public void stopRepeating() throws CameraAccessException {
        synchronized (this.mInterfaceLock) {
            if (!this.mInitialized) {
                throw new IllegalStateException("Uninitialized component");
            }
            this.mInternalRepeatingRequestEnabled = true;
            this.mCaptureSession.stopRepeating();
        }
    }

    @Override // android.hardware.camera2.CameraExtensionSession, java.lang.AutoCloseable
    public void close() throws CameraAccessException {
        synchronized (this.mInterfaceLock) {
            if (this.mInitialized) {
                this.mInternalRepeatingRequestEnabled = false;
                this.mCaptureSession.stopRepeating();
                ArrayList<CaptureStageImpl> captureStageList = new ArrayList<>();
                try {
                    CaptureStageImpl disableParams = this.mPreviewExtender.onDisableSession();
                    if (disableParams != null) {
                        captureStageList.add(disableParams);
                    }
                    CaptureStageImpl disableStillCaptureParams = this.mImageExtender.onDisableSession();
                    if (disableStillCaptureParams != null) {
                        captureStageList.add(disableStillCaptureParams);
                    }
                } catch (RemoteException e) {
                    Log.m110e(TAG, "Failed to disable extension! Extension service does not respond!");
                }
                if (!captureStageList.isEmpty()) {
                    CaptureRequest disableRequest = createRequest(this.mCameraDevice, captureStageList, this.mCameraRepeatingSurface, 1);
                    this.mCaptureSession.capture(disableRequest, new CloseRequestHandler(this.mRepeatingRequestImageCallback), this.mHandler);
                }
                this.mCaptureSession.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInitialCaptureRequest(List<CaptureStageImpl> captureStageList, InitialRequestHandler requestHandler) throws CameraAccessException {
        CaptureRequest initialRequest = createRequest(this.mCameraDevice, captureStageList, this.mCameraRepeatingSurface, 1);
        this.mCaptureSession.capture(initialRequest, requestHandler, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int setRepeatingRequest(CaptureStageImpl captureStage, CameraCaptureSession.CaptureCallback requestHandler) throws CameraAccessException {
        return setRepeatingRequest(captureStage, requestHandler, (CaptureRequest) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int setRepeatingRequest(CaptureStageImpl captureStage, CameraCaptureSession.CaptureCallback requestHandler, CaptureRequest clientRequest) throws CameraAccessException {
        ArrayList<CaptureStageImpl> captureStageList = new ArrayList<>();
        captureStageList.add(captureStage);
        CaptureRequest repeatingRequest = createRequest(this.mCameraDevice, captureStageList, this.mCameraRepeatingSurface, 1, clientRequest);
        return this.mCaptureSession.setSingleRepeatingRequest(repeatingRequest, new CameraExtensionUtils.HandlerExecutor(this.mHandler), requestHandler);
    }

    public void release(boolean skipCloseNotification) {
        boolean notifyClose = false;
        synchronized (this.mInterfaceLock) {
            this.mInternalRepeatingRequestEnabled = false;
            this.mHandlerThread.quitSafely();
            try {
                this.mPreviewExtender.onDeInit();
                this.mImageExtender.onDeInit();
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to release extensions! Extension service does not respond!");
            }
            long j = this.mExtensionClientId;
            if (j >= 0) {
                CameraExtensionCharacteristics.unregisterClient(j);
                if (this.mInitialized) {
                    notifyClose = true;
                    CameraExtensionCharacteristics.releaseSession();
                }
            }
            this.mInitialized = false;
            CameraOutputImageCallback cameraOutputImageCallback = this.mRepeatingRequestImageCallback;
            if (cameraOutputImageCallback != null) {
                cameraOutputImageCallback.close();
                this.mRepeatingRequestImageCallback = null;
            }
            ImageReader imageReader = this.mRepeatingRequestImageReader;
            if (imageReader != null) {
                imageReader.close();
                this.mRepeatingRequestImageReader = null;
            }
            CameraOutputImageCallback cameraOutputImageCallback2 = this.mBurstCaptureImageCallback;
            if (cameraOutputImageCallback2 != null) {
                cameraOutputImageCallback2.close();
                this.mBurstCaptureImageCallback = null;
            }
            ImageReader imageReader2 = this.mBurstCaptureImageReader;
            if (imageReader2 != null) {
                imageReader2.close();
                this.mBurstCaptureImageReader = null;
            }
            ImageReader imageReader3 = this.mStubCaptureImageReader;
            if (imageReader3 != null) {
                imageReader3.close();
                this.mStubCaptureImageReader = null;
            }
            ImageWriter imageWriter = this.mRepeatingRequestImageWriter;
            if (imageWriter != null) {
                imageWriter.close();
                this.mRepeatingRequestImageWriter = null;
            }
            CameraExtensionForwardProcessor cameraExtensionForwardProcessor = this.mPreviewImageProcessor;
            if (cameraExtensionForwardProcessor != null) {
                cameraExtensionForwardProcessor.close();
                this.mPreviewImageProcessor = null;
            }
            CameraExtensionJpegProcessor cameraExtensionJpegProcessor = this.mImageJpegProcessor;
            if (cameraExtensionJpegProcessor != null) {
                cameraExtensionJpegProcessor.close();
                this.mImageJpegProcessor = null;
            }
            this.mCaptureSession = null;
            this.mImageProcessor = null;
            this.mClientRepeatingRequestSurface = null;
            this.mCameraRepeatingSurface = null;
            this.mClientCaptureSurface = null;
            this.mCameraBurstSurface = null;
            this.mClientPostviewSurface = null;
        }
        if (notifyClose && !skipCloseNotification) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.this.lambda$release$0();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$release$0() {
        this.mCallbacks.onClosed(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyConfigurationFailure() {
        synchronized (this.mInterfaceLock) {
            if (this.mInitialized) {
                return;
            }
            release(true);
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.this.lambda$notifyConfigurationFailure$1();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyConfigurationFailure$1() {
        this.mCallbacks.onConfigureFailed(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyConfigurationSuccess() {
        synchronized (this.mInterfaceLock) {
            if (this.mInitialized) {
                return;
            }
            this.mInitialized = true;
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.this.lambda$notifyConfigurationSuccess$2();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyConfigurationSuccess$2() {
        this.mCallbacks.onConfigured(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SessionStateHandler extends CameraCaptureSession.StateCallback {
        private SessionStateHandler() {
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onClosed(CameraCaptureSession session) {
            CameraExtensionSessionImpl.this.release(false);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(CameraCaptureSession session) {
            CameraExtensionSessionImpl.this.notifyConfigurationFailure();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(CameraCaptureSession session) {
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                CameraExtensionSessionImpl.this.mCaptureSession = session;
                try {
                    CameraExtensionSessionImpl.this.finishPipelineInitialization();
                    CameraExtensionCharacteristics.initializeSession(CameraExtensionSessionImpl.this.mInitializeHandler);
                } catch (RemoteException e) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to initialize session! Extension service does not respond!");
                    CameraExtensionSessionImpl.this.notifyConfigurationFailure();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class InitializeSessionHandler extends IInitializeSessionCallback.Stub {
        private InitializeSessionHandler() {
        }

        @Override // android.hardware.camera2.extension.IInitializeSessionCallback
        public void onSuccess() {
            boolean status = true;
            ArrayList<CaptureStageImpl> initialRequestList = CameraExtensionSessionImpl.this.compileInitialRequestList();
            if (!initialRequestList.isEmpty()) {
                try {
                    CameraExtensionSessionImpl cameraExtensionSessionImpl = CameraExtensionSessionImpl.this;
                    cameraExtensionSessionImpl.setInitialCaptureRequest(initialRequestList, new InitialRequestHandler(cameraExtensionSessionImpl.mRepeatingRequestImageCallback));
                } catch (CameraAccessException e) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to initialize the initial capture request!");
                    status = false;
                }
            } else {
                try {
                    CameraExtensionSessionImpl cameraExtensionSessionImpl2 = CameraExtensionSessionImpl.this;
                    CaptureStageImpl captureStage = cameraExtensionSessionImpl2.mPreviewExtender.getCaptureStage();
                    CameraExtensionSessionImpl cameraExtensionSessionImpl3 = CameraExtensionSessionImpl.this;
                    cameraExtensionSessionImpl2.setRepeatingRequest(captureStage, new PreviewRequestHandler(cameraExtensionSessionImpl3, null, null, null, cameraExtensionSessionImpl3.mRepeatingRequestImageCallback));
                } catch (CameraAccessException | RemoteException e2) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to initialize internal repeating request!");
                    status = false;
                }
            }
            if (!status) {
                CameraExtensionSessionImpl.this.notifyConfigurationFailure();
            }
        }

        @Override // android.hardware.camera2.extension.IInitializeSessionCallback
        public void onFailure() {
            CameraExtensionSessionImpl.this.mCaptureSession.close();
            Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to initialize proxy service session! This can happen when trying to configure multiple concurrent extension sessions!");
            CameraExtensionSessionImpl.this.notifyConfigurationFailure();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BurstRequestHandler extends CameraCaptureSession.CaptureCallback {
        private final CameraOutputImageCallback mBurstImageCallback;
        private final CameraExtensionSession.ExtensionCaptureCallback mCallbacks;
        private final HashMap<CaptureRequest, Integer> mCaptureRequestMap;
        private final CaptureRequest mClientRequest;
        private final Executor mExecutor;
        private HashMap<Integer, Pair<Image, TotalCaptureResult>> mCaptureStageMap = new HashMap<>();
        private LongSparseArray<Pair<Image, Integer>> mCapturePendingMap = new LongSparseArray<>();
        private ImageCallback mImageCallback = null;
        private boolean mCaptureFailed = false;
        private CaptureResultHandler mCaptureResultHandler = null;

        public BurstRequestHandler(CaptureRequest request, Executor executor, CameraExtensionSession.ExtensionCaptureCallback callbacks, HashMap<CaptureRequest, Integer> requestMap, CameraOutputImageCallback imageCallback) {
            this.mClientRequest = request;
            this.mExecutor = executor;
            this.mCallbacks = callbacks;
            this.mCaptureRequestMap = requestMap;
            this.mBurstImageCallback = imageCallback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyCaptureFailed() {
            if (!this.mCaptureFailed) {
                this.mCaptureFailed = true;
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$notifyCaptureFailed$0();
                        }
                    });
                    Binder.restoreCallingIdentity(ident);
                    for (Pair<Image, TotalCaptureResult> captureStage : this.mCaptureStageMap.values()) {
                        captureStage.first.close();
                    }
                    this.mCaptureStageMap.clear();
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyCaptureFailed$0() {
            this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, final long timestamp, long frameNumber) {
            boolean initialCallback = false;
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                if (CameraExtensionSessionImpl.this.mImageProcessor != null && this.mImageCallback == null) {
                    this.mImageCallback = new ImageCallback();
                    initialCallback = true;
                } else if (CameraExtensionSessionImpl.this.mImageProcessor == null) {
                    initialCallback = true;
                }
            }
            if (initialCallback) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$onCaptureStarted$1(timestamp);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            CameraOutputImageCallback cameraOutputImageCallback = this.mBurstImageCallback;
            if (cameraOutputImageCallback != null && this.mImageCallback != null) {
                cameraOutputImageCallback.registerListener(Long.valueOf(timestamp), this.mImageCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureStarted$1(long timestamp) {
            this.mCallbacks.onCaptureStarted(CameraExtensionSessionImpl.this, this.mClientRequest, timestamp);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            notifyCaptureFailed();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            notifyCaptureFailed();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceAborted(CameraCaptureSession session, final int sequenceId) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$onCaptureSequenceAborted$2(sequenceId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceAborted$2(int sequenceId) {
            this.mCallbacks.onCaptureSequenceAborted(CameraExtensionSessionImpl.this, sequenceId);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceCompleted(CameraCaptureSession session, final int sequenceId, long frameNumber) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$onCaptureSequenceCompleted$3(sequenceId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceCompleted$3(int sequenceId) {
            this.mCallbacks.onCaptureSequenceCompleted(CameraExtensionSessionImpl.this, sequenceId);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            if (!this.mCaptureRequestMap.containsKey(request)) {
                Log.m110e(CameraExtensionSessionImpl.TAG, "Unexpected still capture request received!");
                return;
            }
            Integer stageId = this.mCaptureRequestMap.get(request);
            Long timestamp = (Long) result.get(CaptureResult.SENSOR_TIMESTAMP);
            if (timestamp != null) {
                if (CameraExtensionSessionImpl.this.mCaptureResultsSupported && this.mCaptureResultHandler == null) {
                    this.mCaptureResultHandler = new CaptureResultHandler(this.mClientRequest, this.mExecutor, this.mCallbacks, result.getSequenceId());
                }
                if (CameraExtensionSessionImpl.this.mImageProcessor != null) {
                    if (this.mCapturePendingMap.indexOfKey(timestamp.longValue()) >= 0) {
                        Image img = this.mCapturePendingMap.get(timestamp.longValue()).first;
                        this.mCaptureStageMap.put(stageId, new Pair<>(img, result));
                        checkAndFireBurstProcessing();
                        return;
                    }
                    this.mCapturePendingMap.put(timestamp.longValue(), new Pair<>(null, stageId));
                    this.mCaptureStageMap.put(stageId, new Pair<>(null, result));
                    return;
                }
                this.mCaptureRequestMap.clear();
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$onCaptureCompleted$4();
                        }
                    });
                    CaptureResultHandler captureResultHandler = this.mCaptureResultHandler;
                    if (captureResultHandler != null) {
                        captureResultHandler.onCaptureCompleted(timestamp.longValue(), CameraExtensionSessionImpl.this.initializeFilteredResults(result));
                    }
                    return;
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            Log.m110e(CameraExtensionSessionImpl.TAG, "Capture result without valid sensor timestamp!");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureCompleted$4() {
            this.mCallbacks.onCaptureProcessStarted(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:7:0x001e  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void checkAndFireBurstProcessing() {
            if (this.mCaptureRequestMap.size() == this.mCaptureStageMap.size()) {
                for (Pair<Image, TotalCaptureResult> captureStage : this.mCaptureStageMap.values()) {
                    if (captureStage.first == null || captureStage.second == null) {
                        return;
                    }
                    while (r0.hasNext()) {
                    }
                }
                this.mCaptureRequestMap.clear();
                this.mCapturePendingMap.clear();
                boolean processStatus = true;
                Byte jpegQuality = (Byte) this.mClientRequest.get(CaptureRequest.JPEG_QUALITY);
                Integer jpegOrientation = (Integer) this.mClientRequest.get(CaptureRequest.JPEG_ORIENTATION);
                List<CaptureBundle> captureList = CameraExtensionSessionImpl.initializeParcelable(this.mCaptureStageMap, jpegOrientation, jpegQuality);
                try {
                    boolean isPostviewRequested = this.mClientRequest.containsTarget(CameraExtensionSessionImpl.this.mClientPostviewSurface);
                    CameraExtensionSessionImpl.this.mImageProcessor.process(captureList, this.mCaptureResultHandler, isPostviewRequested);
                } catch (RemoteException e) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to process multi-frame request! Extension service does not respond!");
                    processStatus = false;
                }
                for (CaptureBundle bundle : captureList) {
                    bundle.captureImage.buffer.close();
                }
                captureList.clear();
                for (Pair<Image, TotalCaptureResult> captureStage2 : this.mCaptureStageMap.values()) {
                    captureStage2.first.close();
                }
                this.mCaptureStageMap.clear();
                long ident = Binder.clearCallingIdentity();
                try {
                    if (processStatus) {
                        this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda5
                            @Override // java.lang.Runnable
                            public final void run() {
                                CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$checkAndFireBurstProcessing$5();
                            }
                        });
                    } else {
                        this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$BurstRequestHandler$$ExternalSyntheticLambda6
                            @Override // java.lang.Runnable
                            public final void run() {
                                CameraExtensionSessionImpl.BurstRequestHandler.this.lambda$checkAndFireBurstProcessing$6();
                            }
                        });
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$checkAndFireBurstProcessing$5() {
            this.mCallbacks.onCaptureProcessStarted(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$checkAndFireBurstProcessing$6() {
            this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        /* loaded from: classes.dex */
        private class ImageCallback implements OnImageAvailableListener {
            private ImageCallback() {
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageDropped(long timestamp) {
                BurstRequestHandler.this.notifyCaptureFailed();
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageAvailable(ImageReader reader, Image img) {
                if (BurstRequestHandler.this.mCaptureFailed) {
                    img.close();
                }
                long timestamp = img.getTimestamp();
                reader.detachImage(img);
                if (BurstRequestHandler.this.mCapturePendingMap.indexOfKey(timestamp) >= 0) {
                    Integer stageId = (Integer) ((Pair) BurstRequestHandler.this.mCapturePendingMap.get(timestamp)).second;
                    Pair<Image, TotalCaptureResult> captureStage = (Pair) BurstRequestHandler.this.mCaptureStageMap.get(stageId);
                    if (captureStage != null) {
                        BurstRequestHandler.this.mCaptureStageMap.put(stageId, new Pair(img, (TotalCaptureResult) captureStage.second));
                        BurstRequestHandler.this.checkAndFireBurstProcessing();
                        return;
                    }
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Capture stage: " + ((Pair) BurstRequestHandler.this.mCapturePendingMap.get(timestamp)).second + " is absent!");
                    return;
                }
                BurstRequestHandler.this.mCapturePendingMap.put(timestamp, new Pair(img, -1));
            }
        }
    }

    /* loaded from: classes.dex */
    private class ImageLoopbackCallback implements OnImageAvailableListener {
        private ImageLoopbackCallback() {
        }

        @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
        public void onImageDropped(long timestamp) {
        }

        @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
        public void onImageAvailable(ImageReader reader, Image img) {
            img.close();
        }
    }

    /* loaded from: classes.dex */
    private class InitialRequestHandler extends CameraCaptureSession.CaptureCallback {
        private final CameraOutputImageCallback mImageCallback;

        public InitialRequestHandler(CameraOutputImageCallback imageCallback) {
            this.mImageCallback = imageCallback;
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            this.mImageCallback.registerListener(Long.valueOf(timestamp), new ImageLoopbackCallback());
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            Log.m110e(CameraExtensionSessionImpl.TAG, "Initial capture request aborted!");
            CameraExtensionSessionImpl.this.notifyConfigurationFailure();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            Log.m110e(CameraExtensionSessionImpl.TAG, "Initial capture request failed!");
            CameraExtensionSessionImpl.this.notifyConfigurationFailure();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            boolean status = true;
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                try {
                    CameraExtensionSessionImpl cameraExtensionSessionImpl = CameraExtensionSessionImpl.this;
                    cameraExtensionSessionImpl.setRepeatingRequest(cameraExtensionSessionImpl.mPreviewExtender.getCaptureStage(), new PreviewRequestHandler(CameraExtensionSessionImpl.this, null, null, null, this.mImageCallback));
                } catch (CameraAccessException | RemoteException e) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to start the internal repeating request!");
                    status = false;
                }
            }
            if (!status) {
                CameraExtensionSessionImpl.this.notifyConfigurationFailure();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CameraOutputImageCallback implements ImageReader.OnImageAvailableListener, Closeable {
        private final ImageReader mImageReader;
        private HashMap<Long, Pair<Image, OnImageAvailableListener>> mImageListenerMap = new HashMap<>();
        private boolean mOutOfBuffers = false;

        CameraOutputImageCallback(ImageReader imageReader) {
            this.mImageReader = imageReader;
        }

        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader reader) {
            try {
                Image img = reader.acquireNextImage();
                if (img == null) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Invalid image!");
                    return;
                }
                Long timestamp = Long.valueOf(img.getTimestamp());
                if (this.mImageListenerMap.containsKey(timestamp)) {
                    Pair<Image, OnImageAvailableListener> entry = this.mImageListenerMap.remove(timestamp);
                    if (entry.second == null) {
                        Log.m104w(CameraExtensionSessionImpl.TAG, "Invalid image listener, dropping frame!");
                        img.close();
                    } else {
                        entry.second.onImageAvailable(reader, img);
                    }
                } else {
                    this.mImageListenerMap.put(Long.valueOf(img.getTimestamp()), new Pair<>(img, null));
                }
                notifyDroppedImages(timestamp.longValue());
            } catch (IllegalStateException e) {
                Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to acquire image, too many images pending!");
                this.mOutOfBuffers = true;
            }
        }

        private void notifyDroppedImages(long timestamp) {
            Set<Long> timestamps = this.mImageListenerMap.keySet();
            ArrayList<Long> removedTs = new ArrayList<>();
            for (Long l : timestamps) {
                long ts = l.longValue();
                if (ts < timestamp) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Dropped image with ts: " + ts);
                    Pair<Image, OnImageAvailableListener> entry = this.mImageListenerMap.get(Long.valueOf(ts));
                    if (entry.second != null) {
                        entry.second.onImageDropped(ts);
                    }
                    if (entry.first != null) {
                        entry.first.close();
                    }
                    removedTs.add(Long.valueOf(ts));
                }
            }
            Iterator<Long> it = removedTs.iterator();
            while (it.hasNext()) {
                this.mImageListenerMap.remove(Long.valueOf(it.next().longValue()));
            }
        }

        public void registerListener(Long timestamp, OnImageAvailableListener listener) {
            if (this.mImageListenerMap.containsKey(timestamp)) {
                Pair<Image, OnImageAvailableListener> entry = this.mImageListenerMap.remove(timestamp);
                if (entry.first != null) {
                    listener.onImageAvailable(this.mImageReader, entry.first);
                    if (this.mOutOfBuffers) {
                        this.mOutOfBuffers = false;
                        Log.m104w(CameraExtensionSessionImpl.TAG, "Out of buffers, retry!");
                        onImageAvailable(this.mImageReader);
                        return;
                    }
                    return;
                }
                Log.m104w(CameraExtensionSessionImpl.TAG, "No valid image for listener with ts: " + timestamp.longValue());
                return;
            }
            this.mImageListenerMap.put(timestamp, new Pair<>(null, listener));
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            for (Pair<Image, OnImageAvailableListener> entry : this.mImageListenerMap.values()) {
                if (entry.first != null) {
                    entry.first.close();
                }
            }
            for (Long l : this.mImageListenerMap.keySet()) {
                long timestamp = l.longValue();
                Pair<Image, OnImageAvailableListener> entry2 = this.mImageListenerMap.get(Long.valueOf(timestamp));
                if (entry2.second != null) {
                    entry2.second.onImageDropped(timestamp);
                }
            }
            this.mImageListenerMap.clear();
        }
    }

    /* loaded from: classes.dex */
    private class CloseRequestHandler extends CameraCaptureSession.CaptureCallback {
        private final CameraOutputImageCallback mImageCallback;

        public CloseRequestHandler(CameraOutputImageCallback imageCallback) {
            this.mImageCallback = imageCallback;
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            this.mImageCallback.registerListener(Long.valueOf(timestamp), new ImageLoopbackCallback());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CaptureResultHandler extends IProcessResultImpl.Stub {
        private final CameraExtensionSession.ExtensionCaptureCallback mCallbacks;
        private final CaptureRequest mClientRequest;
        private final Executor mExecutor;
        private final int mRequestId;

        public CaptureResultHandler(CaptureRequest clientRequest, Executor executor, CameraExtensionSession.ExtensionCaptureCallback listener, int requestId) {
            this.mClientRequest = clientRequest;
            this.mExecutor = executor;
            this.mCallbacks = listener;
            this.mRequestId = requestId;
        }

        @Override // android.hardware.camera2.extension.IProcessResultImpl
        public void onCaptureCompleted(long shutterTimestamp, CameraMetadataNative result) {
            if (result == null) {
                Log.m110e(CameraExtensionSessionImpl.TAG, "Invalid capture result!");
                return;
            }
            result.set((CaptureResult.Key<CaptureResult.Key<Long>>) CaptureResult.SENSOR_TIMESTAMP, (CaptureResult.Key<Long>) Long.valueOf(shutterTimestamp));
            final TotalCaptureResult totalResult = new TotalCaptureResult(CameraExtensionSessionImpl.this.mCameraDevice.getId(), result, this.mClientRequest, this.mRequestId, shutterTimestamp, new ArrayList(), CameraExtensionSessionImpl.this.mSessionId, new PhysicalCaptureResultInfo[0]);
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$CaptureResultHandler$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.CaptureResultHandler.this.lambda$onCaptureCompleted$0(totalResult);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureCompleted$0(TotalCaptureResult totalResult) {
            this.mCallbacks.onCaptureResultAvailable(CameraExtensionSessionImpl.this, this.mClientRequest, totalResult);
        }

        @Override // android.hardware.camera2.extension.IProcessResultImpl
        public void onCaptureProcessProgressed(final int progress) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$CaptureResultHandler$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraExtensionSessionImpl.CaptureResultHandler.this.lambda$onCaptureProcessProgressed$1(progress);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureProcessProgressed$1(int progress) {
            this.mCallbacks.onCaptureProcessProgressed(CameraExtensionSessionImpl.this, this.mClientRequest, progress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PreviewRequestHandler extends CameraCaptureSession.CaptureCallback {
        private final CameraExtensionSession.ExtensionCaptureCallback mCallbacks;
        private CaptureResultHandler mCaptureResultHandler;
        private final boolean mClientNotificationsEnabled;
        private final CaptureRequest mClientRequest;
        private final Executor mExecutor;
        private OnImageAvailableListener mImageCallback;
        private LongSparseArray<Pair<Image, TotalCaptureResult>> mPendingResultMap;
        private final CameraOutputImageCallback mRepeatingImageCallback;
        private boolean mRequestUpdatedNeeded;
        private final boolean mSingleCapture;

        public PreviewRequestHandler(CameraExtensionSessionImpl cameraExtensionSessionImpl, CaptureRequest clientRequest, Executor executor, CameraExtensionSession.ExtensionCaptureCallback listener, CameraOutputImageCallback imageCallback) {
            this(clientRequest, executor, listener, imageCallback, false);
        }

        public PreviewRequestHandler(CaptureRequest clientRequest, Executor executor, CameraExtensionSession.ExtensionCaptureCallback listener, CameraOutputImageCallback imageCallback, boolean singleCapture) {
            this.mImageCallback = null;
            this.mPendingResultMap = new LongSparseArray<>();
            this.mCaptureResultHandler = null;
            boolean z = false;
            this.mRequestUpdatedNeeded = false;
            this.mClientRequest = clientRequest;
            this.mExecutor = executor;
            this.mCallbacks = listener;
            if (clientRequest != null && executor != null && listener != null) {
                z = true;
            }
            this.mClientNotificationsEnabled = z;
            this.mRepeatingImageCallback = imageCallback;
            this.mSingleCapture = singleCapture;
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, final long timestamp, long frameNumber) {
            OnImageAvailableListener imageLoopbackCallback;
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                if (this.mImageCallback == null) {
                    if (CameraExtensionSessionImpl.this.mPreviewProcessorType == 1) {
                        if (this.mClientNotificationsEnabled) {
                            CameraExtensionSessionImpl.this.mPreviewImageProcessor.onOutputSurface(CameraExtensionSessionImpl.this.mClientRepeatingRequestSurface, CameraExtensionSessionImpl.nativeGetSurfaceFormat(CameraExtensionSessionImpl.this.mClientRepeatingRequestSurface));
                        } else {
                            CameraExtensionSessionImpl.this.mPreviewImageProcessor.onOutputSurface(null, -1);
                        }
                        this.mImageCallback = new ImageProcessCallback();
                    } else {
                        if (this.mClientNotificationsEnabled) {
                            imageLoopbackCallback = new ImageForwardCallback(CameraExtensionSessionImpl.this.mRepeatingRequestImageWriter);
                        } else {
                            imageLoopbackCallback = new ImageLoopbackCallback();
                        }
                        this.mImageCallback = imageLoopbackCallback;
                    }
                }
            }
            if (this.mClientNotificationsEnabled) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureStarted$0(timestamp);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            this.mRepeatingImageCallback.registerListener(Long.valueOf(timestamp), this.mImageCallback);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureStarted$0(long timestamp) {
            this.mCallbacks.onCaptureStarted(CameraExtensionSessionImpl.this, this.mClientRequest, timestamp);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceAborted(CameraCaptureSession session, final int sequenceId) {
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                if (CameraExtensionSessionImpl.this.mInternalRepeatingRequestEnabled && !this.mSingleCapture) {
                    resumeInternalRepeatingRequest(true);
                }
            }
            if (this.mClientNotificationsEnabled) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureSequenceAborted$1(sequenceId);
                        }
                    });
                    return;
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            CameraExtensionSessionImpl.this.notifyConfigurationFailure();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceAborted$1(int sequenceId) {
            this.mCallbacks.onCaptureSequenceAborted(CameraExtensionSessionImpl.this, sequenceId);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureSequenceCompleted(CameraCaptureSession session, final int sequenceId, long frameNumber) {
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                if (this.mRequestUpdatedNeeded && !this.mSingleCapture) {
                    this.mRequestUpdatedNeeded = false;
                    resumeInternalRepeatingRequest(false);
                } else if (CameraExtensionSessionImpl.this.mInternalRepeatingRequestEnabled && !this.mSingleCapture) {
                    resumeInternalRepeatingRequest(true);
                }
            }
            if (this.mClientNotificationsEnabled) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureSequenceCompleted$2(sequenceId);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceCompleted$2(int sequenceId) {
            this.mCallbacks.onCaptureSequenceCompleted(CameraExtensionSessionImpl.this, sequenceId);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            if (this.mClientNotificationsEnabled) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureFailed$3();
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureFailed$3() {
            this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Image image;
            boolean notifyClient = this.mClientNotificationsEnabled;
            boolean processStatus = true;
            synchronized (CameraExtensionSessionImpl.this.mInterfaceLock) {
                Long timestamp = (Long) result.get(CaptureResult.SENSOR_TIMESTAMP);
                if (timestamp != null) {
                    if (CameraExtensionSessionImpl.this.mCaptureResultsSupported && this.mClientNotificationsEnabled && this.mCaptureResultHandler == null) {
                        this.mCaptureResultHandler = new CaptureResultHandler(this.mClientRequest, this.mExecutor, this.mCallbacks, result.getSequenceId());
                    }
                    if (!this.mSingleCapture && CameraExtensionSessionImpl.this.mPreviewProcessorType == 0) {
                        CaptureStageImpl captureStage = null;
                        try {
                            captureStage = CameraExtensionSessionImpl.this.mPreviewRequestUpdateProcessor.process(result.getNativeMetadata(), result.getSequenceId());
                        } catch (RemoteException e) {
                            Log.m110e(CameraExtensionSessionImpl.TAG, "Extension service does not respond during processing!");
                        }
                        if (captureStage != null) {
                            try {
                                CameraExtensionSessionImpl.this.setRepeatingRequest(captureStage, this, request);
                                this.mRequestUpdatedNeeded = true;
                            } catch (CameraAccessException e2) {
                                Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to update repeating request settings!");
                            } catch (IllegalStateException e3) {
                            }
                        } else {
                            this.mRequestUpdatedNeeded = false;
                        }
                    } else if (CameraExtensionSessionImpl.this.mPreviewProcessorType == 1) {
                        int idx = this.mPendingResultMap.indexOfKey(timestamp.longValue());
                        if (idx >= 0 && this.mPendingResultMap.get(timestamp.longValue()).first == null) {
                            CaptureResultHandler captureResultHandler = this.mCaptureResultHandler;
                            if (captureResultHandler != null) {
                                captureResultHandler.onCaptureCompleted(timestamp.longValue(), CameraExtensionSessionImpl.this.initializeFilteredResults(result));
                            }
                            discardPendingRepeatingResults(idx, this.mPendingResultMap, false);
                        } else if (idx >= 0) {
                            ParcelImage parcelImage = CameraExtensionSessionImpl.initializeParcelImage(this.mPendingResultMap.get(timestamp.longValue()).first);
                            try {
                                CameraExtensionSessionImpl.this.mPreviewImageProcessor.process(parcelImage, result, this.mCaptureResultHandler);
                                parcelImage.buffer.close();
                                image = this.mPendingResultMap.get(timestamp.longValue()).first;
                            } catch (RemoteException e4) {
                                processStatus = false;
                                Log.m110e(CameraExtensionSessionImpl.TAG, "Extension service does not respond during processing, dropping frame!");
                                parcelImage.buffer.close();
                                image = this.mPendingResultMap.get(timestamp.longValue()).first;
                            } catch (RuntimeException e5) {
                                processStatus = false;
                                Log.m110e(CameraExtensionSessionImpl.TAG, "Runtime exception encountered during buffer processing, dropping frame!");
                                parcelImage.buffer.close();
                                image = this.mPendingResultMap.get(timestamp.longValue()).first;
                            }
                            image.close();
                            discardPendingRepeatingResults(idx, this.mPendingResultMap, false);
                        } else {
                            notifyClient = false;
                            this.mPendingResultMap.put(timestamp.longValue(), new Pair<>(null, result));
                        }
                    }
                    if (notifyClient) {
                        long ident = Binder.clearCallingIdentity();
                        if (processStatus) {
                            this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda4
                                @Override // java.lang.Runnable
                                public final void run() {
                                    CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureCompleted$4();
                                }
                            });
                            if (this.mCaptureResultHandler != null && CameraExtensionSessionImpl.this.mPreviewProcessorType != 1) {
                                this.mCaptureResultHandler.onCaptureCompleted(timestamp.longValue(), CameraExtensionSessionImpl.this.initializeFilteredResults(result));
                            }
                        } else {
                            this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$onCaptureCompleted$5();
                                }
                            });
                        }
                        Binder.restoreCallingIdentity(ident);
                    }
                } else {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Result without valid sensor timestamp!");
                }
            }
            if (notifyClient) {
                return;
            }
            CameraExtensionSessionImpl.this.notifyConfigurationSuccess();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureCompleted$4() {
            this.mCallbacks.onCaptureProcessStarted(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureCompleted$5() {
            this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        private void resumeInternalRepeatingRequest(boolean internal) {
            try {
                if (internal) {
                    CameraExtensionSessionImpl cameraExtensionSessionImpl = CameraExtensionSessionImpl.this;
                    cameraExtensionSessionImpl.setRepeatingRequest(cameraExtensionSessionImpl.mPreviewExtender.getCaptureStage(), new PreviewRequestHandler(CameraExtensionSessionImpl.this, null, null, null, this.mRepeatingImageCallback));
                } else {
                    CameraExtensionSessionImpl cameraExtensionSessionImpl2 = CameraExtensionSessionImpl.this;
                    cameraExtensionSessionImpl2.setRepeatingRequest(cameraExtensionSessionImpl2.mPreviewExtender.getCaptureStage(), this, this.mClientRequest);
                }
            } catch (CameraAccessException e) {
                Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to resume internal repeating request!");
            } catch (RemoteException e2) {
                Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to resume internal repeating request, extension service fails to respond!");
            } catch (IllegalStateException e3) {
                Log.m104w(CameraExtensionSessionImpl.TAG, "Failed to resume internal repeating request!");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Long calculatePruneThreshold(LongSparseArray<Pair<Image, TotalCaptureResult>> previewMap) {
            long oldestTimestamp = Long.MAX_VALUE;
            for (int idx = 0; idx < previewMap.size(); idx++) {
                Pair<Image, TotalCaptureResult> entry = previewMap.valueAt(idx);
                long timestamp = previewMap.keyAt(idx);
                if (entry.first != null && timestamp < oldestTimestamp) {
                    oldestTimestamp = timestamp;
                }
            }
            return Long.valueOf(oldestTimestamp == Long.MAX_VALUE ? 0L : oldestTimestamp);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void discardPendingRepeatingResults(int idx, LongSparseArray<Pair<Image, TotalCaptureResult>> previewMap, boolean notifyCurrentIndex) {
            if (idx < 0) {
                return;
            }
            for (int i = idx; i >= 0; i--) {
                if (previewMap.valueAt(i).first != null) {
                    previewMap.valueAt(i).first.close();
                } else if (this.mClientNotificationsEnabled && previewMap.valueAt(i).second != null && (i != idx || notifyCurrentIndex)) {
                    TotalCaptureResult result = previewMap.valueAt(i).second;
                    Long timestamp = (Long) result.get(CaptureResult.SENSOR_TIMESTAMP);
                    CaptureResultHandler captureResultHandler = this.mCaptureResultHandler;
                    if (captureResultHandler != null) {
                        captureResultHandler.onCaptureCompleted(timestamp.longValue(), CameraExtensionSessionImpl.this.initializeFilteredResults(result));
                    }
                    Log.m104w(CameraExtensionSessionImpl.TAG, "Preview frame drop with timestamp: " + previewMap.keyAt(i));
                    long ident = Binder.clearCallingIdentity();
                    try {
                        this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$$ExternalSyntheticLambda3
                            @Override // java.lang.Runnable
                            public final void run() {
                                CameraExtensionSessionImpl.PreviewRequestHandler.this.lambda$discardPendingRepeatingResults$6();
                            }
                        });
                    } finally {
                        Binder.restoreCallingIdentity(ident);
                    }
                }
                previewMap.removeAt(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$discardPendingRepeatingResults$6() {
            this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, this.mClientRequest);
        }

        /* loaded from: classes.dex */
        private class ImageForwardCallback implements OnImageAvailableListener {
            private final ImageWriter mOutputWriter;

            public ImageForwardCallback(ImageWriter imageWriter) {
                this.mOutputWriter = imageWriter;
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageDropped(long timestamp) {
                PreviewRequestHandler previewRequestHandler = PreviewRequestHandler.this;
                previewRequestHandler.discardPendingRepeatingResults(previewRequestHandler.mPendingResultMap.indexOfKey(timestamp), PreviewRequestHandler.this.mPendingResultMap, true);
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageAvailable(ImageReader reader, Image img) {
                if (img == null) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Invalid image!");
                    return;
                }
                try {
                    this.mOutputWriter.queueInputImage(img);
                } catch (IllegalStateException e) {
                    Log.m104w(CameraExtensionSessionImpl.TAG, "Output surface likely abandoned, dropping buffer!");
                    img.close();
                } catch (RuntimeException e2) {
                    if (!e2.getClass().equals(RuntimeException.class)) {
                        throw e2;
                    }
                    Log.m104w(CameraExtensionSessionImpl.TAG, "Output surface likely abandoned, dropping buffer!");
                    img.close();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ImageProcessCallback implements OnImageAvailableListener {
            private ImageProcessCallback() {
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageDropped(long timestamp) {
                PreviewRequestHandler previewRequestHandler = PreviewRequestHandler.this;
                previewRequestHandler.discardPendingRepeatingResults(previewRequestHandler.mPendingResultMap.indexOfKey(timestamp), PreviewRequestHandler.this.mPendingResultMap, true);
                PreviewRequestHandler.this.mPendingResultMap.put(timestamp, new Pair(null, null));
            }

            @Override // android.hardware.camera2.impl.CameraExtensionSessionImpl.OnImageAvailableListener
            public void onImageAvailable(ImageReader reader, Image img) {
                if (PreviewRequestHandler.this.mPendingResultMap.size() + 1 >= 10) {
                    PreviewRequestHandler previewRequestHandler = PreviewRequestHandler.this;
                    LongSparseArray longSparseArray = previewRequestHandler.mPendingResultMap;
                    PreviewRequestHandler previewRequestHandler2 = PreviewRequestHandler.this;
                    previewRequestHandler.discardPendingRepeatingResults(longSparseArray.indexOfKey(previewRequestHandler2.calculatePruneThreshold(previewRequestHandler2.mPendingResultMap).longValue()), PreviewRequestHandler.this.mPendingResultMap, true);
                }
                if (img == null) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Invalid preview buffer!");
                    return;
                }
                try {
                    reader.detachImage(img);
                    long timestamp = img.getTimestamp();
                    int idx = PreviewRequestHandler.this.mPendingResultMap.indexOfKey(timestamp);
                    if (idx >= 0) {
                        boolean processStatus = true;
                        ParcelImage parcelImage = CameraExtensionSessionImpl.initializeParcelImage(img);
                        try {
                            try {
                                CameraExtensionSessionImpl.this.mPreviewImageProcessor.process(parcelImage, (TotalCaptureResult) ((Pair) PreviewRequestHandler.this.mPendingResultMap.get(timestamp)).second, PreviewRequestHandler.this.mCaptureResultHandler);
                            } catch (RemoteException e) {
                                processStatus = false;
                                Log.m110e(CameraExtensionSessionImpl.TAG, "Extension service does not respond during processing, dropping frame!");
                            }
                            parcelImage.buffer.close();
                            img.close();
                            PreviewRequestHandler previewRequestHandler3 = PreviewRequestHandler.this;
                            previewRequestHandler3.discardPendingRepeatingResults(idx, previewRequestHandler3.mPendingResultMap, false);
                            if (PreviewRequestHandler.this.mClientNotificationsEnabled) {
                                long ident = Binder.clearCallingIdentity();
                                try {
                                    if (processStatus) {
                                        PreviewRequestHandler.this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$ImageProcessCallback$$ExternalSyntheticLambda0
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                CameraExtensionSessionImpl.PreviewRequestHandler.ImageProcessCallback.this.lambda$onImageAvailable$0();
                                            }
                                        });
                                    } else {
                                        PreviewRequestHandler.this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraExtensionSessionImpl$PreviewRequestHandler$ImageProcessCallback$$ExternalSyntheticLambda1
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                CameraExtensionSessionImpl.PreviewRequestHandler.ImageProcessCallback.this.lambda$onImageAvailable$1();
                                            }
                                        });
                                    }
                                    return;
                                } finally {
                                    Binder.restoreCallingIdentity(ident);
                                }
                            }
                            return;
                        } catch (Throwable th) {
                            parcelImage.buffer.close();
                            img.close();
                            throw th;
                        }
                    }
                    PreviewRequestHandler.this.mPendingResultMap.put(timestamp, new Pair(img, null));
                } catch (IllegalStateException e2) {
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to detach image!");
                    img.close();
                } catch (RuntimeException e3) {
                    if (!e3.getClass().equals(RuntimeException.class)) {
                        throw e3;
                    }
                    Log.m110e(CameraExtensionSessionImpl.TAG, "Failed to detach image!");
                    img.close();
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onImageAvailable$0() {
                PreviewRequestHandler.this.mCallbacks.onCaptureProcessStarted(CameraExtensionSessionImpl.this, PreviewRequestHandler.this.mClientRequest);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onImageAvailable$1() {
                PreviewRequestHandler.this.mCallbacks.onCaptureFailed(CameraExtensionSessionImpl.this, PreviewRequestHandler.this.mClientRequest);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CameraMetadataNative initializeFilteredResults(TotalCaptureResult result) {
        CameraMetadataNative captureResults = new CameraMetadataNative();
        for (CaptureResult.Key key : this.mSupportedResultKeys) {
            Object value = result.get(key);
            if (value != null) {
                captureResults.set((CaptureResult.Key<CaptureResult.Key>) key, (CaptureResult.Key) value);
            }
        }
        return captureResults;
    }

    private static Size findSmallestAspectMatchedSize(List<Size> sizes, Size arSize) {
        if (arSize.getHeight() == 0) {
            throw new IllegalArgumentException("Invalid input aspect ratio");
        }
        float targetAR = arSize.getWidth() / arSize.getHeight();
        Size ret = null;
        Size fallbackSize = null;
        for (Size sz : sizes) {
            if (fallbackSize == null) {
                fallbackSize = sz;
            }
            if (sz.getHeight() > 0 && (ret == null || ret.getWidth() * ret.getHeight() < sz.getWidth() * sz.getHeight())) {
                float currentAR = sz.getWidth() / sz.getHeight();
                if (Math.abs(currentAR - targetAR) <= 0.01f) {
                    ret = sz;
                }
            }
        }
        if (ret == null) {
            Log.m110e(TAG, "AR matched size not found returning first size in list");
            Size ret2 = fallbackSize;
            return ret2;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ParcelImage initializeParcelImage(Image img) {
        ParcelImage parcelImage = new ParcelImage();
        parcelImage.buffer = img.getHardwareBuffer();
        try {
            SyncFence fd = img.getFence();
            if (fd.isValid()) {
                parcelImage.fence = fd.getFdDup();
            }
        } catch (IOException e) {
            Log.m110e(TAG, "Failed to parcel buffer fence!");
        }
        parcelImage.width = img.getWidth();
        parcelImage.height = img.getHeight();
        parcelImage.format = img.getFormat();
        parcelImage.timestamp = img.getTimestamp();
        parcelImage.transform = img.getTransform();
        parcelImage.scalingMode = img.getScalingMode();
        parcelImage.planeCount = img.getPlaneCount();
        parcelImage.crop = img.getCropRect();
        return parcelImage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<CaptureBundle> initializeParcelable(HashMap<Integer, Pair<Image, TotalCaptureResult>> captureMap, Integer jpegOrientation, Byte jpegQuality) {
        ArrayList<CaptureBundle> ret = new ArrayList<>();
        for (Integer stagetId : captureMap.keySet()) {
            Pair<Image, TotalCaptureResult> entry = captureMap.get(stagetId);
            CaptureBundle bundle = new CaptureBundle();
            bundle.stage = stagetId.intValue();
            bundle.captureImage = initializeParcelImage(entry.first);
            bundle.sequenceId = entry.second.getSequenceId();
            bundle.captureResult = entry.second.getNativeMetadata();
            if (jpegOrientation != null) {
                bundle.captureResult.set((CaptureResult.Key<CaptureResult.Key<Integer>>) CaptureResult.JPEG_ORIENTATION, (CaptureResult.Key<Integer>) jpegOrientation);
            }
            if (jpegQuality != null) {
                bundle.captureResult.set((CaptureResult.Key<CaptureResult.Key<Byte>>) CaptureResult.JPEG_QUALITY, (CaptureResult.Key<Byte>) jpegQuality);
            }
            ret.add(bundle);
        }
        return ret;
    }
}
