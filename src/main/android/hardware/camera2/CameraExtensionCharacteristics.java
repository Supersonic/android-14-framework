package android.hardware.camera2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.extension.IAdvancedExtenderImpl;
import android.hardware.camera2.extension.ICameraExtensionsProxyService;
import android.hardware.camera2.extension.IImageCaptureExtenderImpl;
import android.hardware.camera2.extension.IInitializeSessionCallback;
import android.hardware.camera2.extension.IPreviewExtenderImpl;
import android.hardware.camera2.extension.LatencyRange;
import android.hardware.camera2.extension.SizeList;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.p008os.AsyncTask;
import android.p008os.ConditionVariable;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Size;
import android.view.SurfaceView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public final class CameraExtensionCharacteristics {
    public static final int EXTENSION_AUTOMATIC = 0;
    @Deprecated
    public static final int EXTENSION_BEAUTY = 1;
    public static final int EXTENSION_BOKEH = 2;
    public static final int EXTENSION_FACE_RETOUCH = 1;
    public static final int EXTENSION_HDR = 3;
    private static final int[] EXTENSION_LIST = {0, 1, 2, 3, 4};
    public static final int EXTENSION_NIGHT = 4;
    public static final int NON_PROCESSING_INPUT_FORMAT = 34;
    public static final int PROCESSING_INPUT_FORMAT = 35;
    private static final String TAG = "CameraExtensionCharacteristics";
    private final String mCameraId;
    private final CameraCharacteristics mChars;
    private final Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Extension {
    }

    public CameraExtensionCharacteristics(Context context, String cameraId, CameraCharacteristics chars) {
        this.mContext = context;
        this.mCameraId = cameraId;
        this.mChars = chars;
    }

    private static ArrayList<Size> getSupportedSizes(List<SizeList> sizesList, Integer format) {
        ArrayList<Size> ret = new ArrayList<>();
        if (sizesList != null && !sizesList.isEmpty()) {
            for (SizeList entry : sizesList) {
                if (entry.format == format.intValue() && !entry.sizes.isEmpty()) {
                    for (android.hardware.camera2.extension.Size sz : entry.sizes) {
                        ret.add(new Size(sz.width, sz.height));
                    }
                    return ret;
                }
            }
        }
        return ret;
    }

    private static List<Size> generateSupportedSizes(List<SizeList> sizesList, Integer format, StreamConfigurationMap streamMap) {
        ArrayList<Size> ret = getSupportedSizes(sizesList, format);
        Size[] supportedSizes = streamMap.getOutputSizes(format.intValue());
        if (ret.isEmpty() && supportedSizes != null) {
            ret.addAll(Arrays.asList(supportedSizes));
        }
        return ret;
    }

    private static List<Size> generateJpegSupportedSizes(List<SizeList> sizesList, StreamConfigurationMap streamMap) {
        HashSet<Size> hashSet;
        ArrayList<Size> extensionSizes = getSupportedSizes(sizesList, 35);
        if (extensionSizes.isEmpty()) {
            hashSet = new HashSet<>(Arrays.asList(streamMap.getOutputSizes(35)));
        } else {
            hashSet = new HashSet<>(extensionSizes);
        }
        HashSet<Size> supportedSizes = hashSet;
        HashSet<Size> supportedJpegSizes = new HashSet<>(Arrays.asList(streamMap.getOutputSizes(256)));
        supportedSizes.retainAll(supportedJpegSizes);
        return new ArrayList(supportedSizes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class CameraExtensionManagerGlobal {
        private static final CameraExtensionManagerGlobal GLOBAL_CAMERA_MANAGER = new CameraExtensionManagerGlobal();
        private static final String PROXY_PACKAGE_NAME = "com.android.cameraextensions";
        private static final String PROXY_SERVICE_NAME = "com.android.cameraextensions.CameraExtensionsProxyService";
        private static final String TAG = "CameraExtensionManagerGlobal";
        private final Object mLock = new Object();
        private final int PROXY_SERVICE_DELAY_MS = 1000;
        private InitializerFuture mInitFuture = null;
        private ServiceConnection mConnection = null;
        private ICameraExtensionsProxyService mProxy = null;
        private boolean mSupportsAdvancedExtensions = false;

        private CameraExtensionManagerGlobal() {
        }

        public static CameraExtensionManagerGlobal get() {
            return GLOBAL_CAMERA_MANAGER;
        }

        private void connectToProxyLocked(Context ctx) {
            if (this.mConnection == null) {
                Intent intent = new Intent();
                intent.setClassName(PROXY_PACKAGE_NAME, PROXY_SERVICE_NAME);
                String vendorProxyPackage = SystemProperties.get("ro.vendor.camera.extensions.package");
                String vendorProxyService = SystemProperties.get("ro.vendor.camera.extensions.service");
                if (!vendorProxyPackage.isEmpty() && !vendorProxyService.isEmpty()) {
                    Log.m106v(TAG, "Choosing the vendor camera extensions proxy package: " + vendorProxyPackage);
                    Log.m106v(TAG, "Choosing the vendor camera extensions proxy service: " + vendorProxyService);
                    intent.setClassName(vendorProxyPackage, vendorProxyService);
                }
                this.mInitFuture = new InitializerFuture();
                this.mConnection = new ServiceConnection() { // from class: android.hardware.camera2.CameraExtensionCharacteristics.CameraExtensionManagerGlobal.1
                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName component) {
                        CameraExtensionManagerGlobal.this.mInitFuture.setStatus(false);
                        CameraExtensionManagerGlobal.this.mConnection = null;
                        CameraExtensionManagerGlobal.this.mProxy = null;
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName component, IBinder binder) {
                        CameraExtensionManagerGlobal.this.mProxy = ICameraExtensionsProxyService.Stub.asInterface(binder);
                        if (CameraExtensionManagerGlobal.this.mProxy == null) {
                            throw new IllegalStateException("Camera Proxy service is null");
                        }
                        try {
                            CameraExtensionManagerGlobal cameraExtensionManagerGlobal = CameraExtensionManagerGlobal.this;
                            cameraExtensionManagerGlobal.mSupportsAdvancedExtensions = cameraExtensionManagerGlobal.mProxy.advancedExtensionsSupported();
                        } catch (RemoteException e) {
                            Log.m110e(CameraExtensionManagerGlobal.TAG, "Remote IPC failed!");
                        }
                        CameraExtensionManagerGlobal.this.mInitFuture.setStatus(true);
                    }
                };
                ctx.bindService(intent, 1073741897, AsyncTask.THREAD_POOL_EXECUTOR, this.mConnection);
                try {
                    this.mInitFuture.get(1000L, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    Log.m110e(TAG, "Timed out while initializing proxy service!");
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class InitializerFuture implements Future<Boolean> {
            ConditionVariable mCondVar;
            private volatile Boolean mStatus;

            private InitializerFuture() {
                this.mCondVar = new ConditionVariable(false);
            }

            public void setStatus(boolean status) {
                this.mStatus = Boolean.valueOf(status);
                this.mCondVar.open();
            }

            @Override // java.util.concurrent.Future
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override // java.util.concurrent.Future
            public boolean isCancelled() {
                return false;
            }

            @Override // java.util.concurrent.Future
            public boolean isDone() {
                return this.mStatus != null;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Future
            public Boolean get() {
                this.mCondVar.block();
                return this.mStatus;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Future
            public Boolean get(long timeout, TimeUnit unit) throws TimeoutException {
                long timeoutMs = unit.convert(timeout, TimeUnit.MILLISECONDS);
                if (!this.mCondVar.block(timeoutMs)) {
                    throw new TimeoutException("Failed to receive status after " + timeout + " " + unit);
                }
                if (this.mStatus == null) {
                    throw new AssertionError();
                }
                return this.mStatus;
            }
        }

        public long registerClient(Context ctx) {
            synchronized (this.mLock) {
                connectToProxyLocked(ctx);
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    try {
                        return iCameraExtensionsProxyService.registerClient();
                    } catch (RemoteException e) {
                        Log.m110e(TAG, "Failed to initialize extension! Extension service does  not respond!");
                        return -1L;
                    }
                }
                return -1L;
            }
        }

        public void unregisterClient(long clientId) {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    try {
                        iCameraExtensionsProxyService.unregisterClient(clientId);
                    } catch (RemoteException e) {
                        Log.m110e(TAG, "Failed to de-initialize extension! Extension service does not respond!");
                    }
                }
            }
        }

        public void initializeSession(IInitializeSessionCallback cb) throws RemoteException {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    iCameraExtensionsProxyService.initializeSession(cb);
                }
            }
        }

        public void releaseSession() {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    try {
                        iCameraExtensionsProxyService.releaseSession();
                    } catch (RemoteException e) {
                        Log.m110e(TAG, "Failed to release session! Extension service does not respond!");
                    }
                }
            }
        }

        public boolean areAdvancedExtensionsSupported() {
            return this.mSupportsAdvancedExtensions;
        }

        public IPreviewExtenderImpl initializePreviewExtension(int extensionType) throws RemoteException {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    return iCameraExtensionsProxyService.initializePreviewExtension(extensionType);
                }
                return null;
            }
        }

        public IImageCaptureExtenderImpl initializeImageExtension(int extensionType) throws RemoteException {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    return iCameraExtensionsProxyService.initializeImageExtension(extensionType);
                }
                return null;
            }
        }

        public IAdvancedExtenderImpl initializeAdvancedExtension(int extensionType) throws RemoteException {
            synchronized (this.mLock) {
                ICameraExtensionsProxyService iCameraExtensionsProxyService = this.mProxy;
                if (iCameraExtensionsProxyService != null) {
                    return iCameraExtensionsProxyService.initializeAdvancedExtension(extensionType);
                }
                return null;
            }
        }
    }

    public static long registerClient(Context ctx) {
        return CameraExtensionManagerGlobal.get().registerClient(ctx);
    }

    public static void unregisterClient(long clientId) {
        CameraExtensionManagerGlobal.get().unregisterClient(clientId);
    }

    public static void initializeSession(IInitializeSessionCallback cb) throws RemoteException {
        CameraExtensionManagerGlobal.get().initializeSession(cb);
    }

    public static void releaseSession() {
        CameraExtensionManagerGlobal.get().releaseSession();
    }

    public static boolean areAdvancedExtensionsSupported() {
        return CameraExtensionManagerGlobal.get().areAdvancedExtensionsSupported();
    }

    public static boolean isExtensionSupported(String cameraId, int extensionType, CameraCharacteristics chars) {
        if (areAdvancedExtensionsSupported()) {
            try {
                IAdvancedExtenderImpl extender = initializeAdvancedExtension(extensionType);
                return extender.isExtensionAvailable(cameraId);
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to query extension availability! Extension service does not respond!");
                return false;
            }
        }
        try {
            Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extensionType);
            try {
                if (extenders.first.isExtensionAvailable(cameraId, chars.getNativeMetadata())) {
                    return extenders.second.isExtensionAvailable(cameraId, chars.getNativeMetadata());
                }
                return false;
            } catch (RemoteException e2) {
                Log.m110e(TAG, "Failed to query extension availability! Extension service does not respond!");
                return false;
            }
        } catch (IllegalArgumentException e3) {
            return false;
        }
    }

    public static IAdvancedExtenderImpl initializeAdvancedExtension(int extensionType) {
        try {
            IAdvancedExtenderImpl extender = CameraExtensionManagerGlobal.get().initializeAdvancedExtension(extensionType);
            if (extender == null) {
                throw new IllegalArgumentException("Unknown extension: " + extensionType);
            }
            return extender;
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed to initialize extension: " + extensionType);
        }
    }

    public static Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> initializeExtension(int extensionType) {
        try {
            IPreviewExtenderImpl previewExtender = CameraExtensionManagerGlobal.get().initializePreviewExtension(extensionType);
            IImageCaptureExtenderImpl imageExtender = CameraExtensionManagerGlobal.get().initializeImageExtension(extensionType);
            if (imageExtender == null || previewExtender == null) {
                throw new IllegalArgumentException("Unknown extension: " + extensionType);
            }
            return new Pair<>(previewExtender, imageExtender);
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed to initialize extension: " + extensionType);
        }
    }

    private static <T> boolean isOutputSupportedFor(Class<T> klass) {
        Objects.requireNonNull(klass, "klass must not be null");
        if (klass == SurfaceTexture.class || klass == SurfaceView.class) {
            return true;
        }
        return false;
    }

    public List<Integer> getSupportedExtensions() {
        int[] iArr;
        ArrayList<Integer> ret = new ArrayList<>();
        long clientId = registerClient(this.mContext);
        if (clientId < 0) {
            return Collections.unmodifiableList(ret);
        }
        try {
            for (int extensionType : EXTENSION_LIST) {
                if (isExtensionSupported(this.mCameraId, extensionType, this.mChars)) {
                    ret.add(Integer.valueOf(extensionType));
                }
            }
            unregisterClient(clientId);
            return Collections.unmodifiableList(ret);
        } catch (Throwable th) {
            unregisterClient(clientId);
            throw th;
        }
    }

    public boolean isPostviewAvailable(int extension) {
        long clientId = registerClient(this.mContext);
        try {
            if (clientId >= 0) {
                try {
                    if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                        if (areAdvancedExtensionsSupported()) {
                            IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                            extender.init(this.mCameraId);
                            return extender.isPostviewAvailable();
                        }
                        Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                        extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                        return extenders.second.isPostviewAvailable();
                    }
                    throw new IllegalArgumentException("Unsupported extension");
                } catch (RemoteException e) {
                    Log.m110e(TAG, "Failed to query the extension for postview availability! Extension service does not respond!");
                    unregisterClient(clientId);
                    return false;
                }
            }
            throw new IllegalArgumentException("Unsupported extensions");
        } finally {
            unregisterClient(clientId);
        }
    }

    public List<Size> getPostviewSupportedSizes(int extension, Size captureSize, int format) {
        long clientId = registerClient(this.mContext);
        try {
            if (clientId >= 0) {
                if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                    android.hardware.camera2.extension.Size sz = new android.hardware.camera2.extension.Size();
                    sz.width = captureSize.getWidth();
                    sz.height = captureSize.getHeight();
                    StreamConfigurationMap streamMap = (StreamConfigurationMap) this.mChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (areAdvancedExtensionsSupported()) {
                        switch (format) {
                            case 35:
                            case 256:
                                IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                                extender.init(this.mCameraId);
                                return generateSupportedSizes(extender.getSupportedPostviewResolutions(sz), Integer.valueOf(format), streamMap);
                            default:
                                throw new IllegalArgumentException("Unsupported format: " + format);
                        }
                    }
                    Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                    extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                    if (extenders.second.getCaptureProcessor() == null || !isPostviewAvailable(extension)) {
                        throw new IllegalArgumentException("Extension does not support postview feature");
                    }
                    if (format == 35) {
                        return generateSupportedSizes(extenders.second.getSupportedPostviewResolutions(sz), Integer.valueOf(format), streamMap);
                    }
                    if (format == 256) {
                        return generateJpegSupportedSizes(extenders.second.getSupportedPostviewResolutions(sz), streamMap);
                    }
                    throw new IllegalArgumentException("Unsupported format: " + format);
                }
                throw new IllegalArgumentException("Unsupported extension");
            }
            throw new IllegalArgumentException("Unsupported extensions");
        } catch (RemoteException e) {
            Log.m110e(TAG, "Failed to query the extension postview supported sizes! Extension service does not respond!");
            return Collections.emptyList();
        } finally {
            unregisterClient(clientId);
        }
    }

    public <T> List<Size> getExtensionSupportedSizes(int extension, Class<T> klass) {
        if (isOutputSupportedFor(klass)) {
            long clientId = registerClient(this.mContext);
            try {
                if (clientId >= 0) {
                    if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                        StreamConfigurationMap streamMap = (StreamConfigurationMap) this.mChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        if (areAdvancedExtensionsSupported()) {
                            IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                            extender.init(this.mCameraId);
                            return generateSupportedSizes(extender.getSupportedPreviewOutputResolutions(this.mCameraId), 34, streamMap);
                        }
                        Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                        extenders.first.init(this.mCameraId, this.mChars.getNativeMetadata());
                        return generateSupportedSizes(extenders.first.getSupportedResolutions(), 34, streamMap);
                    }
                    throw new IllegalArgumentException("Unsupported extension");
                }
                throw new IllegalArgumentException("Unsupported extensions");
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to query the extension supported sizes! Extension service does not respond!");
                return new ArrayList();
            } finally {
                unregisterClient(clientId);
            }
        }
        return new ArrayList();
    }

    public List<Size> getExtensionSupportedSizes(int extension, int format) {
        try {
            long clientId = registerClient(this.mContext);
            if (clientId >= 0) {
                if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                    StreamConfigurationMap streamMap = (StreamConfigurationMap) this.mChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (areAdvancedExtensionsSupported()) {
                        switch (format) {
                            case 35:
                            case 256:
                                IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                                extender.init(this.mCameraId);
                                List<Size> generateSupportedSizes = generateSupportedSizes(extender.getSupportedCaptureOutputResolutions(this.mCameraId), Integer.valueOf(format), streamMap);
                                unregisterClient(clientId);
                                return generateSupportedSizes;
                            default:
                                throw new IllegalArgumentException("Unsupported format: " + format);
                        }
                    } else if (format == 35) {
                        Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                        extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                        if (extenders.second.getCaptureProcessor() == null) {
                            ArrayList arrayList = new ArrayList();
                            unregisterClient(clientId);
                            return arrayList;
                        }
                        List<Size> generateSupportedSizes2 = generateSupportedSizes(extenders.second.getSupportedResolutions(), Integer.valueOf(format), streamMap);
                        unregisterClient(clientId);
                        return generateSupportedSizes2;
                    } else if (format == 256) {
                        Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders2 = initializeExtension(extension);
                        extenders2.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                        if (extenders2.second.getCaptureProcessor() != null) {
                            List<Size> generateJpegSupportedSizes = generateJpegSupportedSizes(extenders2.second.getSupportedResolutions(), streamMap);
                            unregisterClient(clientId);
                            return generateJpegSupportedSizes;
                        }
                        List<Size> generateSupportedSizes3 = generateSupportedSizes(null, Integer.valueOf(format), streamMap);
                        unregisterClient(clientId);
                        return generateSupportedSizes3;
                    } else {
                        throw new IllegalArgumentException("Unsupported format: " + format);
                    }
                }
                throw new IllegalArgumentException("Unsupported extension");
            }
            throw new IllegalArgumentException("Unsupported extensions");
        } catch (RemoteException e) {
            Log.m110e(TAG, "Failed to query the extension supported sizes! Extension service does not respond!");
            return new ArrayList();
        }
    }

    public Range<Long> getEstimatedCaptureLatencyRangeMillis(int extension, Size captureOutputSize, int format) {
        switch (format) {
            case 35:
            case 256:
                long clientId = registerClient(this.mContext);
                if (clientId >= 0) {
                    try {
                        try {
                        } catch (RemoteException e) {
                            Log.m110e(TAG, "Failed to query the extension capture latency! Extension service does not respond!");
                        }
                        if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                            android.hardware.camera2.extension.Size sz = new android.hardware.camera2.extension.Size();
                            sz.width = captureOutputSize.getWidth();
                            sz.height = captureOutputSize.getHeight();
                            if (areAdvancedExtensionsSupported()) {
                                IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                                extender.init(this.mCameraId);
                                LatencyRange latencyRange = extender.getEstimatedCaptureLatencyRange(this.mCameraId, sz, format);
                                if (latencyRange != null) {
                                    return new Range<>(Long.valueOf(latencyRange.min), Long.valueOf(latencyRange.max));
                                }
                            } else {
                                Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                                extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                                if (format == 35 && extenders.second.getCaptureProcessor() == null) {
                                    return null;
                                }
                                if (format == 256 && extenders.second.getCaptureProcessor() != null) {
                                    return null;
                                }
                                LatencyRange latencyRange2 = extenders.second.getEstimatedCaptureLatencyRange(sz);
                                if (latencyRange2 != null) {
                                    return new Range<>(Long.valueOf(latencyRange2.min), Long.valueOf(latencyRange2.max));
                                }
                            }
                            return null;
                        }
                        throw new IllegalArgumentException("Unsupported extension");
                    } finally {
                        unregisterClient(clientId);
                    }
                }
                throw new IllegalArgumentException("Unsupported extensions");
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public boolean isCaptureProcessProgressAvailable(int extension) {
        long clientId = registerClient(this.mContext);
        try {
            if (clientId >= 0) {
                try {
                    if (isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                        if (areAdvancedExtensionsSupported()) {
                            IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                            extender.init(this.mCameraId);
                            return extender.isCaptureProcessProgressAvailable();
                        }
                        Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                        extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                        return extenders.second.isCaptureProcessProgressAvailable();
                    }
                    throw new IllegalArgumentException("Unsupported extension");
                } catch (RemoteException e) {
                    Log.m110e(TAG, "Failed to query the extension progress callbacks! Extension service does not respond!");
                    unregisterClient(clientId);
                    return false;
                }
            }
            throw new IllegalArgumentException("Unsupported extensions");
        } finally {
            unregisterClient(clientId);
        }
    }

    public Set<CaptureRequest.Key> getAvailableCaptureRequestKeys(int extension) {
        CameraMetadataNative captureRequestMeta;
        long clientId = registerClient(this.mContext);
        if (clientId < 0) {
            throw new IllegalArgumentException("Unsupported extensions");
        }
        HashSet<CaptureRequest.Key> ret = new HashSet<>();
        try {
            try {
                if (!isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                    throw new IllegalArgumentException("Unsupported extension");
                }
                if (areAdvancedExtensionsSupported()) {
                    IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                    extender.init(this.mCameraId);
                    captureRequestMeta = extender.getAvailableCaptureRequestKeys(this.mCameraId);
                } else {
                    Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                    extenders.second.onInit(this.mCameraId, this.mChars.getNativeMetadata());
                    extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                    captureRequestMeta = extenders.second.getAvailableCaptureRequestKeys();
                    extenders.second.onDeInit();
                }
                if (captureRequestMeta != null) {
                    int[] requestKeys = (int[]) captureRequestMeta.get(CameraCharacteristics.REQUEST_AVAILABLE_REQUEST_KEYS);
                    if (requestKeys == null) {
                        throw new AssertionError("android.request.availableRequestKeys must be non-null in the characteristics");
                    }
                    CameraCharacteristics requestChars = new CameraCharacteristics(captureRequestMeta);
                    ret.addAll(requestChars.getAvailableKeyList(CaptureRequest.class, crKeyTyped, requestKeys, true));
                }
                if (!ret.contains(CaptureRequest.JPEG_QUALITY)) {
                    ret.add(CaptureRequest.JPEG_QUALITY);
                }
                if (!ret.contains(CaptureRequest.JPEG_ORIENTATION)) {
                    ret.add(CaptureRequest.JPEG_ORIENTATION);
                }
                unregisterClient(clientId);
                return Collections.unmodifiableSet(ret);
            } catch (RemoteException e) {
                throw new IllegalStateException("Failed to query the available capture request keys!");
            }
        } catch (Throwable th) {
            unregisterClient(clientId);
            throw th;
        }
    }

    public Set<CaptureResult.Key> getAvailableCaptureResultKeys(int extension) {
        CameraMetadataNative captureResultMeta;
        long clientId = registerClient(this.mContext);
        if (clientId < 0) {
            throw new IllegalArgumentException("Unsupported extensions");
        }
        HashSet<CaptureResult.Key> ret = new HashSet<>();
        try {
            try {
                if (!isExtensionSupported(this.mCameraId, extension, this.mChars)) {
                    throw new IllegalArgumentException("Unsupported extension");
                }
                if (areAdvancedExtensionsSupported()) {
                    IAdvancedExtenderImpl extender = initializeAdvancedExtension(extension);
                    extender.init(this.mCameraId);
                    captureResultMeta = extender.getAvailableCaptureResultKeys(this.mCameraId);
                } else {
                    Pair<IPreviewExtenderImpl, IImageCaptureExtenderImpl> extenders = initializeExtension(extension);
                    extenders.second.onInit(this.mCameraId, this.mChars.getNativeMetadata());
                    extenders.second.init(this.mCameraId, this.mChars.getNativeMetadata());
                    captureResultMeta = extenders.second.getAvailableCaptureResultKeys();
                    extenders.second.onDeInit();
                }
                if (captureResultMeta != null) {
                    int[] resultKeys = (int[]) captureResultMeta.get(CameraCharacteristics.REQUEST_AVAILABLE_RESULT_KEYS);
                    if (resultKeys == null) {
                        throw new AssertionError("android.request.availableResultKeys must be non-null in the characteristics");
                    }
                    CameraCharacteristics resultChars = new CameraCharacteristics(captureResultMeta);
                    ret.addAll(resultChars.getAvailableKeyList(CaptureResult.class, crKeyTyped, resultKeys, true));
                    if (!ret.contains(CaptureResult.JPEG_QUALITY)) {
                        ret.add(CaptureResult.JPEG_QUALITY);
                    }
                    if (!ret.contains(CaptureResult.JPEG_ORIENTATION)) {
                        ret.add(CaptureResult.JPEG_ORIENTATION);
                    }
                    if (!ret.contains(CaptureResult.SENSOR_TIMESTAMP)) {
                        ret.add(CaptureResult.SENSOR_TIMESTAMP);
                    }
                }
                unregisterClient(clientId);
                return Collections.unmodifiableSet(ret);
            } catch (RemoteException e) {
                throw new IllegalStateException("Failed to query the available capture result keys!");
            }
        } catch (Throwable th) {
            unregisterClient(clientId);
            throw th;
        }
    }
}
