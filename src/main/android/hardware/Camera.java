package android.hardware;

import android.app.ActivityThread;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.IAudioService;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
@Deprecated
/* loaded from: classes.dex */
public class Camera {
    public static final String ACTION_NEW_PICTURE = "android.hardware.action.NEW_PICTURE";
    public static final String ACTION_NEW_VIDEO = "android.hardware.action.NEW_VIDEO";
    public static final int CAMERA_ERROR_DISABLED = 3;
    public static final int CAMERA_ERROR_EVICTED = 2;
    public static final int CAMERA_ERROR_SERVER_DIED = 100;
    public static final int CAMERA_ERROR_UNKNOWN = 1;
    private static final int CAMERA_FACE_DETECTION_HW = 0;
    private static final int CAMERA_FACE_DETECTION_SW = 1;
    public static final int CAMERA_HAL_API_VERSION_1_0 = 256;
    public static final int CAMERA_HAL_API_VERSION_3_0 = 768;
    private static final int CAMERA_MSG_COMPRESSED_IMAGE = 256;
    private static final int CAMERA_MSG_ERROR = 1;
    private static final int CAMERA_MSG_FOCUS = 4;
    private static final int CAMERA_MSG_FOCUS_MOVE = 2048;
    private static final int CAMERA_MSG_POSTVIEW_FRAME = 64;
    private static final int CAMERA_MSG_PREVIEW_FRAME = 16;
    private static final int CAMERA_MSG_PREVIEW_METADATA = 1024;
    private static final int CAMERA_MSG_RAW_IMAGE = 128;
    private static final int CAMERA_MSG_RAW_IMAGE_NOTIFY = 512;
    private static final int CAMERA_MSG_SHUTTER = 2;
    private static final int CAMERA_MSG_VIDEO_FRAME = 32;
    private static final int CAMERA_MSG_ZOOM = 8;
    private static final int NO_ERROR = 0;
    private static final String TAG = "Camera";
    private IAppOpsService mAppOps;
    private IAppOpsCallback mAppOpsCallback;
    private AutoFocusCallback mAutoFocusCallback;
    private AutoFocusMoveCallback mAutoFocusMoveCallback;
    private ErrorCallback mDetailedErrorCallback;
    private ErrorCallback mErrorCallback;
    private EventHandler mEventHandler;
    private FaceDetectionListener mFaceListener;
    private PictureCallback mJpegCallback;
    private long mNativeContext;
    private boolean mOneShot;
    private PictureCallback mPostviewCallback;
    private PreviewCallback mPreviewCallback;
    private PictureCallback mRawImageCallback;
    private ShutterCallback mShutterCallback;
    private boolean mUsingPreviewAllocation;
    private boolean mWithBuffer;
    private OnZoomChangeListener mZoomListener;
    private boolean mFaceDetectionRunning = false;
    private final Object mAutoFocusCallbackLock = new Object();
    private final Object mShutterSoundLock = new Object();
    private boolean mHasAppOpsPlayAudio = true;
    private boolean mShutterSoundEnabledFromApp = true;

    @Deprecated
    /* loaded from: classes.dex */
    public interface AutoFocusCallback {
        void onAutoFocus(boolean z, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface AutoFocusMoveCallback {
        void onAutoFocusMoving(boolean z, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class CameraInfo {
        public static final int CAMERA_FACING_BACK = 0;
        public static final int CAMERA_FACING_FRONT = 1;
        public boolean canDisableShutterSound;
        public int facing;
        public int orientation;
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface ErrorCallback {
        void onError(int i, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class Face {
        public Rect rect;
        public int score;

        /* renamed from: id */
        public int f91id = -1;
        public Point leftEye = null;
        public Point rightEye = null;
        public Point mouth = null;
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface FaceDetectionListener {
        void onFaceDetection(Face[] faceArr, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface OnZoomChangeListener {
        void onZoomChange(int i, boolean z, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface PictureCallback {
        void onPictureTaken(byte[] bArr, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface PreviewCallback {
        void onPreviewFrame(byte[] bArr, Camera camera);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface ShutterCallback {
        void onShutter();
    }

    private final native void _addCallbackBuffer(byte[] bArr, int i);

    private final native boolean _enableShutterSound(boolean z);

    private static native void _getCameraInfo(int i, boolean z, CameraInfo cameraInfo);

    private final native void _startFaceDetection(int i);

    private final native void _stopFaceDetection();

    private final native void _stopPreview();

    private native void enableFocusMoveCallback(int i);

    public static native int getNumberOfCameras();

    private final native void native_autoFocus();

    private final native void native_cancelAutoFocus();

    private final native String native_getParameters();

    private final native void native_release();

    private final native void native_setParameters(String str);

    private native int native_setup(Object obj, int i, String str, boolean z);

    private final native void native_takePicture(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void setHasPreviewCallback(boolean z, boolean z2);

    private final native void setPreviewCallbackSurface(Surface surface);

    public final native int getAudioRestriction();

    public final native void lock();

    public final native boolean previewEnabled();

    public final native void reconnect() throws IOException;

    public final native void setAudioRestriction(int i);

    public final native void setDisplayOrientation(int i);

    public final native void setPreviewSurface(Surface surface) throws IOException;

    public final native void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException;

    public final native void startPreview();

    public final native void startSmoothZoom(int i);

    public final native void stopSmoothZoom();

    public final native void unlock();

    public static void getCameraInfo(int cameraId, CameraInfo cameraInfo) {
        boolean overrideToPortrait = CameraManager.shouldOverrideToPortrait(ActivityThread.currentApplication().getApplicationContext());
        _getCameraInfo(cameraId, overrideToPortrait, cameraInfo);
        IBinder b = ServiceManager.getService("audio");
        IAudioService audioService = IAudioService.Stub.asInterface(b);
        try {
            if (audioService.isCameraSoundForced()) {
                cameraInfo.canDisableShutterSound = false;
            }
        } catch (RemoteException e) {
            Log.m110e(TAG, "Audio service is unavailable for queries");
        }
    }

    public static Camera open(int cameraId) {
        return new Camera(cameraId);
    }

    public static Camera open() {
        int numberOfCameras = getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return new Camera(i);
            }
        }
        return null;
    }

    public static Camera openLegacy(int cameraId, int halVersion) {
        if (halVersion < 768) {
            throw new IllegalArgumentException("Unsupported HAL version " + halVersion);
        }
        return new Camera(cameraId);
    }

    private int cameraInit(int cameraId) {
        this.mShutterCallback = null;
        this.mRawImageCallback = null;
        this.mJpegCallback = null;
        this.mPreviewCallback = null;
        this.mPostviewCallback = null;
        this.mUsingPreviewAllocation = false;
        this.mZoomListener = null;
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this.mEventHandler = new EventHandler(this, looper2);
            } else {
                this.mEventHandler = null;
            }
        }
        boolean overrideToPortrait = CameraManager.shouldOverrideToPortrait(ActivityThread.currentApplication().getApplicationContext());
        return native_setup(new WeakReference(this), cameraId, ActivityThread.currentOpPackageName(), overrideToPortrait);
    }

    Camera(int cameraId) {
        int err = cameraInit(cameraId);
        if (checkInitErrors(err)) {
            if (err == (-OsConstants.EACCES)) {
                throw new RuntimeException("Fail to connect to camera service");
            }
            if (err == (-OsConstants.ENODEV)) {
                throw new RuntimeException("Camera initialization failed");
            }
            throw new RuntimeException("Unknown camera error");
        }
        initAppOps();
    }

    public static boolean checkInitErrors(int err) {
        return err != 0;
    }

    public static Camera openUninitialized() {
        return new Camera();
    }

    Camera() {
    }

    private void initAppOps() {
        IBinder b = ServiceManager.getService(Context.APP_OPS_SERVICE);
        this.mAppOps = IAppOpsService.Stub.asInterface(b);
        updateAppOpsPlayAudio();
        this.mAppOpsCallback = new IAppOpsCallbackWrapper(this);
        try {
            this.mAppOps.startWatchingMode(28, ActivityThread.currentPackageName(), this.mAppOpsCallback);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error registering appOps callback", e);
            this.mHasAppOpsPlayAudio = false;
        }
    }

    private void releaseAppOps() {
        try {
            IAppOpsService iAppOpsService = this.mAppOps;
            if (iAppOpsService != null) {
                iAppOpsService.stopWatchingMode(this.mAppOpsCallback);
            }
        } catch (Exception e) {
        }
    }

    protected void finalize() {
        release();
    }

    public final void release() {
        native_release();
        this.mFaceDetectionRunning = false;
        releaseAppOps();
    }

    public final void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        if (holder != null) {
            setPreviewSurface(holder.getSurface());
        } else {
            setPreviewSurface(null);
        }
    }

    public final void stopPreview() {
        _stopPreview();
        this.mFaceDetectionRunning = false;
        this.mShutterCallback = null;
        this.mRawImageCallback = null;
        this.mPostviewCallback = null;
        this.mJpegCallback = null;
        synchronized (this.mAutoFocusCallbackLock) {
            this.mAutoFocusCallback = null;
        }
        this.mAutoFocusMoveCallback = null;
    }

    public final void setPreviewCallback(PreviewCallback cb) {
        this.mPreviewCallback = cb;
        this.mOneShot = false;
        this.mWithBuffer = false;
        if (cb != null) {
            this.mUsingPreviewAllocation = false;
        }
        setHasPreviewCallback(cb != null, false);
    }

    public final void setOneShotPreviewCallback(PreviewCallback cb) {
        this.mPreviewCallback = cb;
        boolean z = true;
        this.mOneShot = true;
        this.mWithBuffer = false;
        if (cb != null) {
            this.mUsingPreviewAllocation = false;
        }
        if (cb == null) {
            z = false;
        }
        setHasPreviewCallback(z, false);
    }

    public final void setPreviewCallbackWithBuffer(PreviewCallback cb) {
        this.mPreviewCallback = cb;
        boolean z = false;
        this.mOneShot = false;
        this.mWithBuffer = true;
        if (cb != null) {
            this.mUsingPreviewAllocation = false;
        }
        if (cb != null) {
            z = true;
        }
        setHasPreviewCallback(z, true);
    }

    public final void addCallbackBuffer(byte[] callbackBuffer) {
        _addCallbackBuffer(callbackBuffer, 16);
    }

    public final void addRawImageCallbackBuffer(byte[] callbackBuffer) {
        addCallbackBuffer(callbackBuffer, 128);
    }

    private final void addCallbackBuffer(byte[] callbackBuffer, int msgType) {
        if (msgType != 16 && msgType != 128) {
            throw new IllegalArgumentException("Unsupported message type: " + msgType);
        }
        _addCallbackBuffer(callbackBuffer, msgType);
    }

    public final Allocation createPreviewAllocation(RenderScript rs, int usage) throws RSIllegalArgumentException {
        Parameters p = getParameters();
        Size previewSize = p.getPreviewSize();
        Type.Builder yuvBuilder = new Type.Builder(rs, Element.createPixel(rs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        yuvBuilder.setYuvFormat(842094169);
        yuvBuilder.setX(previewSize.width);
        yuvBuilder.setY(previewSize.height);
        Allocation a = Allocation.createTyped(rs, yuvBuilder.create(), usage | 32);
        return a;
    }

    public final void setPreviewCallbackAllocation(Allocation previewAllocation) throws IOException {
        Surface previewSurface = null;
        if (previewAllocation != null) {
            Parameters p = getParameters();
            Size previewSize = p.getPreviewSize();
            if (previewSize.width != previewAllocation.getType().getX() || previewSize.height != previewAllocation.getType().getY()) {
                throw new IllegalArgumentException("Allocation dimensions don't match preview dimensions: Allocation is " + previewAllocation.getType().getX() + ", " + previewAllocation.getType().getY() + ". Preview is " + previewSize.width + ", " + previewSize.height);
            }
            if ((previewAllocation.getUsage() & 32) == 0) {
                throw new IllegalArgumentException("Allocation usage does not include USAGE_IO_INPUT");
            }
            if (previewAllocation.getType().getElement().getDataKind() != Element.DataKind.PIXEL_YUV) {
                throw new IllegalArgumentException("Allocation is not of a YUV type");
            }
            previewSurface = previewAllocation.getSurface();
            this.mUsingPreviewAllocation = true;
        } else {
            this.mUsingPreviewAllocation = false;
        }
        setPreviewCallbackSurface(previewSurface);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EventHandler extends Handler {
        private final Camera mCamera;

        public EventHandler(Camera c, Looper looper) {
            super(looper);
            this.mCamera = c;
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            AutoFocusCallback cb;
            boolean success;
            switch (msg.what) {
                case 1:
                    Log.m110e(Camera.TAG, "Error " + msg.arg1);
                    if (Camera.this.mDetailedErrorCallback != null) {
                        Camera.this.mDetailedErrorCallback.onError(msg.arg1, this.mCamera);
                        return;
                    } else if (Camera.this.mErrorCallback != null) {
                        if (msg.arg1 == 3) {
                            Camera.this.mErrorCallback.onError(2, this.mCamera);
                            return;
                        } else {
                            Camera.this.mErrorCallback.onError(msg.arg1, this.mCamera);
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    if (Camera.this.mShutterCallback != null) {
                        Camera.this.mShutterCallback.onShutter();
                        return;
                    }
                    return;
                case 4:
                    synchronized (Camera.this.mAutoFocusCallbackLock) {
                        cb = Camera.this.mAutoFocusCallback;
                    }
                    if (cb != null) {
                        success = msg.arg1 != 0;
                        cb.onAutoFocus(success, this.mCamera);
                        return;
                    }
                    return;
                case 8:
                    if (Camera.this.mZoomListener != null) {
                        OnZoomChangeListener onZoomChangeListener = Camera.this.mZoomListener;
                        int i = msg.arg1;
                        success = msg.arg2 != 0;
                        onZoomChangeListener.onZoomChange(i, success, this.mCamera);
                        return;
                    }
                    return;
                case 16:
                    PreviewCallback pCb = Camera.this.mPreviewCallback;
                    if (pCb != null) {
                        if (Camera.this.mOneShot) {
                            Camera.this.mPreviewCallback = null;
                        } else if (!Camera.this.mWithBuffer) {
                            Camera.this.setHasPreviewCallback(true, false);
                        }
                        pCb.onPreviewFrame((byte[]) msg.obj, this.mCamera);
                        return;
                    }
                    return;
                case 64:
                    if (Camera.this.mPostviewCallback != null) {
                        Camera.this.mPostviewCallback.onPictureTaken((byte[]) msg.obj, this.mCamera);
                        return;
                    }
                    return;
                case 128:
                    if (Camera.this.mRawImageCallback != null) {
                        Camera.this.mRawImageCallback.onPictureTaken((byte[]) msg.obj, this.mCamera);
                        return;
                    }
                    return;
                case 256:
                    if (Camera.this.mJpegCallback != null) {
                        Camera.this.mJpegCallback.onPictureTaken((byte[]) msg.obj, this.mCamera);
                        return;
                    }
                    return;
                case 1024:
                    if (Camera.this.mFaceListener != null) {
                        Camera.this.mFaceListener.onFaceDetection((Face[]) msg.obj, this.mCamera);
                        return;
                    }
                    return;
                case 2048:
                    if (Camera.this.mAutoFocusMoveCallback != null) {
                        AutoFocusMoveCallback autoFocusMoveCallback = Camera.this.mAutoFocusMoveCallback;
                        success = msg.arg1 != 0;
                        autoFocusMoveCallback.onAutoFocusMoving(success, this.mCamera);
                        return;
                    }
                    return;
                default:
                    Log.m110e(Camera.TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object camera_ref, int what, int arg1, int arg2, Object obj) {
        EventHandler eventHandler;
        Camera c = (Camera) ((WeakReference) camera_ref).get();
        if (c != null && (eventHandler = c.mEventHandler) != null) {
            Message m = eventHandler.obtainMessage(what, arg1, arg2, obj);
            c.mEventHandler.sendMessage(m);
        }
    }

    public final void autoFocus(AutoFocusCallback cb) {
        synchronized (this.mAutoFocusCallbackLock) {
            this.mAutoFocusCallback = cb;
        }
        native_autoFocus();
    }

    public final void cancelAutoFocus() {
        synchronized (this.mAutoFocusCallbackLock) {
            this.mAutoFocusCallback = null;
        }
        native_cancelAutoFocus();
        this.mEventHandler.removeMessages(4);
    }

    public void setAutoFocusMoveCallback(AutoFocusMoveCallback cb) {
        this.mAutoFocusMoveCallback = cb;
        enableFocusMoveCallback(cb != null ? 1 : 0);
    }

    public final void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg) {
        takePicture(shutter, raw, null, jpeg);
    }

    public final void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback postview, PictureCallback jpeg) {
        this.mShutterCallback = shutter;
        this.mRawImageCallback = raw;
        this.mPostviewCallback = postview;
        this.mJpegCallback = jpeg;
        int msgType = 0;
        if (shutter != null) {
            msgType = 0 | 2;
        }
        if (raw != null) {
            msgType |= 128;
        }
        if (postview != null) {
            msgType |= 64;
        }
        if (jpeg != null) {
            msgType |= 256;
        }
        native_takePicture(msgType);
        this.mFaceDetectionRunning = false;
    }

    public final boolean enableShutterSound(boolean enabled) {
        boolean ret;
        boolean canDisableShutterSound = true;
        IBinder b = ServiceManager.getService("audio");
        IAudioService audioService = IAudioService.Stub.asInterface(b);
        try {
            if (audioService.isCameraSoundForced()) {
                canDisableShutterSound = false;
            }
        } catch (RemoteException e) {
            Log.m110e(TAG, "Audio service is unavailable for queries");
        }
        if (!enabled && !canDisableShutterSound) {
            return false;
        }
        synchronized (this.mShutterSoundLock) {
            this.mShutterSoundEnabledFromApp = enabled;
            ret = _enableShutterSound(enabled);
            if (enabled && !this.mHasAppOpsPlayAudio) {
                Log.m108i(TAG, "Shutter sound is not allowed by AppOpsManager");
                if (canDisableShutterSound) {
                    _enableShutterSound(false);
                }
            }
        }
        return ret;
    }

    public final boolean disableShutterSound() {
        return _enableShutterSound(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class IAppOpsCallbackWrapper extends IAppOpsCallback.Stub {
        private final WeakReference<Camera> mWeakCamera;

        IAppOpsCallbackWrapper(Camera camera) {
            this.mWeakCamera = new WeakReference<>(camera);
        }

        @Override // com.android.internal.app.IAppOpsCallback
        public void opChanged(int op, int uid, String packageName) {
            Camera camera;
            if (op == 28 && (camera = this.mWeakCamera.get()) != null) {
                camera.updateAppOpsPlayAudio();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppOpsPlayAudio() {
        synchronized (this.mShutterSoundLock) {
            boolean oldHasAppOpsPlayAudio = this.mHasAppOpsPlayAudio;
            int mode = 1;
            try {
                IAppOpsService iAppOpsService = this.mAppOps;
                if (iAppOpsService != null) {
                    mode = iAppOpsService.checkAudioOperation(28, 13, Process.myUid(), ActivityThread.currentPackageName());
                }
                this.mHasAppOpsPlayAudio = mode == 0;
            } catch (RemoteException e) {
                Log.m110e(TAG, "AppOpsService check audio operation failed");
                this.mHasAppOpsPlayAudio = false;
            }
            boolean z = this.mHasAppOpsPlayAudio;
            if (oldHasAppOpsPlayAudio != z) {
                if (!z) {
                    IBinder b = ServiceManager.getService("audio");
                    IAudioService audioService = IAudioService.Stub.asInterface(b);
                    try {
                    } catch (RemoteException e2) {
                        Log.m110e(TAG, "Audio service is unavailable for queries");
                    }
                    if (audioService.isCameraSoundForced()) {
                        return;
                    }
                    _enableShutterSound(false);
                } else {
                    enableShutterSound(this.mShutterSoundEnabledFromApp);
                }
            }
        }
    }

    public final void setZoomChangeListener(OnZoomChangeListener listener) {
        this.mZoomListener = listener;
    }

    public final void setFaceDetectionListener(FaceDetectionListener listener) {
        this.mFaceListener = listener;
    }

    public final void startFaceDetection() {
        if (this.mFaceDetectionRunning) {
            throw new RuntimeException("Face detection is already running");
        }
        _startFaceDetection(0);
        this.mFaceDetectionRunning = true;
    }

    public final void stopFaceDetection() {
        _stopFaceDetection();
        this.mFaceDetectionRunning = false;
    }

    public final void setErrorCallback(ErrorCallback cb) {
        this.mErrorCallback = cb;
    }

    public final void setDetailedErrorCallback(ErrorCallback cb) {
        this.mDetailedErrorCallback = cb;
    }

    public void setParameters(Parameters params) {
        if (this.mUsingPreviewAllocation) {
            Size newPreviewSize = params.getPreviewSize();
            Size currentPreviewSize = getParameters().getPreviewSize();
            if (newPreviewSize.width != currentPreviewSize.width || newPreviewSize.height != currentPreviewSize.height) {
                throw new IllegalStateException("Cannot change preview size while a preview allocation is configured.");
            }
        }
        native_setParameters(params.flatten());
    }

    public Parameters getParameters() {
        Parameters p = new Parameters();
        String s = native_getParameters();
        p.unflatten(s);
        return p;
    }

    public static Parameters getEmptyParameters() {
        Camera camera = new Camera();
        Objects.requireNonNull(camera);
        return new Parameters();
    }

    public static Parameters getParametersCopy(Parameters parameters) {
        if (parameters == null) {
            throw new NullPointerException("parameters must not be null");
        }
        Camera camera = parameters.getOuter();
        Objects.requireNonNull(camera);
        Parameters p = new Parameters();
        p.copyFrom(parameters);
        return p;
    }

    @Deprecated
    /* loaded from: classes.dex */
    public class Size {
        public int height;
        public int width;

        public Size(int w, int h) {
            this.width = w;
            this.height = h;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Size) {
                Size s = (Size) obj;
                return this.width == s.width && this.height == s.height;
            }
            return false;
        }

        public int hashCode() {
            return (this.width * 32713) + this.height;
        }
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class Area {
        public Rect rect;
        public int weight;

        public Area(Rect rect, int weight) {
            this.rect = rect;
            this.weight = weight;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Area) {
                Area a = (Area) obj;
                Rect rect = this.rect;
                if (rect == null) {
                    if (a.rect != null) {
                        return false;
                    }
                } else if (!rect.equals(a.rect)) {
                    return false;
                }
                return this.weight == a.weight;
            }
            return false;
        }
    }

    @Deprecated
    /* loaded from: classes.dex */
    public class Parameters {
        public static final String ANTIBANDING_50HZ = "50hz";
        public static final String ANTIBANDING_60HZ = "60hz";
        public static final String ANTIBANDING_AUTO = "auto";
        public static final String ANTIBANDING_OFF = "off";
        public static final String EFFECT_AQUA = "aqua";
        public static final String EFFECT_BLACKBOARD = "blackboard";
        public static final String EFFECT_MONO = "mono";
        public static final String EFFECT_NEGATIVE = "negative";
        public static final String EFFECT_NONE = "none";
        public static final String EFFECT_POSTERIZE = "posterize";
        public static final String EFFECT_SEPIA = "sepia";
        public static final String EFFECT_SOLARIZE = "solarize";
        public static final String EFFECT_WHITEBOARD = "whiteboard";
        private static final String FALSE = "false";
        public static final String FLASH_MODE_AUTO = "auto";
        public static final String FLASH_MODE_OFF = "off";
        public static final String FLASH_MODE_ON = "on";
        public static final String FLASH_MODE_RED_EYE = "red-eye";
        public static final String FLASH_MODE_TORCH = "torch";
        public static final int FOCUS_DISTANCE_FAR_INDEX = 2;
        public static final int FOCUS_DISTANCE_NEAR_INDEX = 0;
        public static final int FOCUS_DISTANCE_OPTIMAL_INDEX = 1;
        public static final String FOCUS_MODE_AUTO = "auto";
        public static final String FOCUS_MODE_CONTINUOUS_PICTURE = "continuous-picture";
        public static final String FOCUS_MODE_CONTINUOUS_VIDEO = "continuous-video";
        public static final String FOCUS_MODE_EDOF = "edof";
        public static final String FOCUS_MODE_FIXED = "fixed";
        public static final String FOCUS_MODE_INFINITY = "infinity";
        public static final String FOCUS_MODE_MACRO = "macro";
        private static final String KEY_ANTIBANDING = "antibanding";
        private static final String KEY_AUTO_EXPOSURE_LOCK = "auto-exposure-lock";
        private static final String KEY_AUTO_EXPOSURE_LOCK_SUPPORTED = "auto-exposure-lock-supported";
        private static final String KEY_AUTO_WHITEBALANCE_LOCK = "auto-whitebalance-lock";
        private static final String KEY_AUTO_WHITEBALANCE_LOCK_SUPPORTED = "auto-whitebalance-lock-supported";
        private static final String KEY_EFFECT = "effect";
        private static final String KEY_EXPOSURE_COMPENSATION = "exposure-compensation";
        private static final String KEY_EXPOSURE_COMPENSATION_STEP = "exposure-compensation-step";
        private static final String KEY_FLASH_MODE = "flash-mode";
        private static final String KEY_FOCAL_LENGTH = "focal-length";
        private static final String KEY_FOCUS_AREAS = "focus-areas";
        private static final String KEY_FOCUS_DISTANCES = "focus-distances";
        private static final String KEY_FOCUS_MODE = "focus-mode";
        private static final String KEY_GPS_ALTITUDE = "gps-altitude";
        private static final String KEY_GPS_LATITUDE = "gps-latitude";
        private static final String KEY_GPS_LONGITUDE = "gps-longitude";
        private static final String KEY_GPS_PROCESSING_METHOD = "gps-processing-method";
        private static final String KEY_GPS_TIMESTAMP = "gps-timestamp";
        private static final String KEY_HORIZONTAL_VIEW_ANGLE = "horizontal-view-angle";
        private static final String KEY_JPEG_QUALITY = "jpeg-quality";
        private static final String KEY_JPEG_THUMBNAIL_HEIGHT = "jpeg-thumbnail-height";
        private static final String KEY_JPEG_THUMBNAIL_QUALITY = "jpeg-thumbnail-quality";
        private static final String KEY_JPEG_THUMBNAIL_SIZE = "jpeg-thumbnail-size";
        private static final String KEY_JPEG_THUMBNAIL_WIDTH = "jpeg-thumbnail-width";
        private static final String KEY_MAX_EXPOSURE_COMPENSATION = "max-exposure-compensation";
        private static final String KEY_MAX_NUM_DETECTED_FACES_HW = "max-num-detected-faces-hw";
        private static final String KEY_MAX_NUM_DETECTED_FACES_SW = "max-num-detected-faces-sw";
        private static final String KEY_MAX_NUM_FOCUS_AREAS = "max-num-focus-areas";
        private static final String KEY_MAX_NUM_METERING_AREAS = "max-num-metering-areas";
        private static final String KEY_MAX_ZOOM = "max-zoom";
        private static final String KEY_METERING_AREAS = "metering-areas";
        private static final String KEY_MIN_EXPOSURE_COMPENSATION = "min-exposure-compensation";
        private static final String KEY_PICTURE_FORMAT = "picture-format";
        private static final String KEY_PICTURE_SIZE = "picture-size";
        private static final String KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO = "preferred-preview-size-for-video";
        private static final String KEY_PREVIEW_FORMAT = "preview-format";
        private static final String KEY_PREVIEW_FPS_RANGE = "preview-fps-range";
        private static final String KEY_PREVIEW_FRAME_RATE = "preview-frame-rate";
        private static final String KEY_PREVIEW_SIZE = "preview-size";
        private static final String KEY_RECORDING_HINT = "recording-hint";
        private static final String KEY_ROTATION = "rotation";
        private static final String KEY_SCENE_MODE = "scene-mode";
        private static final String KEY_SMOOTH_ZOOM_SUPPORTED = "smooth-zoom-supported";
        private static final String KEY_VERTICAL_VIEW_ANGLE = "vertical-view-angle";
        private static final String KEY_VIDEO_SIZE = "video-size";
        private static final String KEY_VIDEO_SNAPSHOT_SUPPORTED = "video-snapshot-supported";
        private static final String KEY_VIDEO_STABILIZATION = "video-stabilization";
        private static final String KEY_VIDEO_STABILIZATION_SUPPORTED = "video-stabilization-supported";
        private static final String KEY_WHITE_BALANCE = "whitebalance";
        private static final String KEY_ZOOM = "zoom";
        private static final String KEY_ZOOM_RATIOS = "zoom-ratios";
        private static final String KEY_ZOOM_SUPPORTED = "zoom-supported";
        private static final String PIXEL_FORMAT_BAYER_RGGB = "bayer-rggb";
        private static final String PIXEL_FORMAT_JPEG = "jpeg";
        private static final String PIXEL_FORMAT_RGB565 = "rgb565";
        private static final String PIXEL_FORMAT_YUV420P = "yuv420p";
        private static final String PIXEL_FORMAT_YUV420SP = "yuv420sp";
        private static final String PIXEL_FORMAT_YUV422I = "yuv422i-yuyv";
        private static final String PIXEL_FORMAT_YUV422SP = "yuv422sp";
        public static final int PREVIEW_FPS_MAX_INDEX = 1;
        public static final int PREVIEW_FPS_MIN_INDEX = 0;
        public static final String SCENE_MODE_ACTION = "action";
        public static final String SCENE_MODE_AUTO = "auto";
        public static final String SCENE_MODE_BARCODE = "barcode";
        public static final String SCENE_MODE_BEACH = "beach";
        public static final String SCENE_MODE_CANDLELIGHT = "candlelight";
        public static final String SCENE_MODE_FIREWORKS = "fireworks";
        public static final String SCENE_MODE_HDR = "hdr";
        public static final String SCENE_MODE_LANDSCAPE = "landscape";
        public static final String SCENE_MODE_NIGHT = "night";
        public static final String SCENE_MODE_NIGHT_PORTRAIT = "night-portrait";
        public static final String SCENE_MODE_PARTY = "party";
        public static final String SCENE_MODE_PORTRAIT = "portrait";
        public static final String SCENE_MODE_SNOW = "snow";
        public static final String SCENE_MODE_SPORTS = "sports";
        public static final String SCENE_MODE_STEADYPHOTO = "steadyphoto";
        public static final String SCENE_MODE_SUNSET = "sunset";
        public static final String SCENE_MODE_THEATRE = "theatre";
        private static final String SUPPORTED_VALUES_SUFFIX = "-values";
        private static final String TRUE = "true";
        public static final String WHITE_BALANCE_AUTO = "auto";
        public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
        public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
        public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
        public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
        public static final String WHITE_BALANCE_SHADE = "shade";
        public static final String WHITE_BALANCE_TWILIGHT = "twilight";
        public static final String WHITE_BALANCE_WARM_FLUORESCENT = "warm-fluorescent";
        private final LinkedHashMap<String, String> mMap;

        private Parameters() {
            this.mMap = new LinkedHashMap<>(64);
        }

        public void copyFrom(Parameters other) {
            if (other == null) {
                throw new NullPointerException("other must not be null");
            }
            this.mMap.putAll(other.mMap);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Camera getOuter() {
            return Camera.this;
        }

        public boolean same(Parameters other) {
            if (this == other) {
                return true;
            }
            return other != null && this.mMap.equals(other.mMap);
        }

        @Deprecated
        public void dump() {
            Log.m110e(Camera.TAG, "dump: size=" + this.mMap.size());
            for (String k : this.mMap.keySet()) {
                Log.m110e(Camera.TAG, "dump: " + k + "=" + this.mMap.get(k));
            }
        }

        public String flatten() {
            StringBuilder flattened = new StringBuilder(128);
            for (String k : this.mMap.keySet()) {
                flattened.append(k);
                flattened.append("=");
                flattened.append(this.mMap.get(k));
                flattened.append(NavigationBarInflaterView.GRAVITY_SEPARATOR);
            }
            flattened.deleteCharAt(flattened.length() - 1);
            return flattened.toString();
        }

        public void unflatten(String flattened) {
            this.mMap.clear();
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(';');
            splitter.setString(flattened);
            for (String kv : splitter) {
                int pos = kv.indexOf(61);
                if (pos != -1) {
                    String k = kv.substring(0, pos);
                    String v = kv.substring(pos + 1);
                    this.mMap.put(k, v);
                }
            }
        }

        public void remove(String key) {
            this.mMap.remove(key);
        }

        public void set(String key, String value) {
            if (key.indexOf(61) != -1 || key.indexOf(59) != -1 || key.indexOf(0) != -1) {
                Log.m110e(Camera.TAG, "Key \"" + key + "\" contains invalid character (= or ; or \\0)");
            } else if (value.indexOf(61) != -1 || value.indexOf(59) != -1 || value.indexOf(0) != -1) {
                Log.m110e(Camera.TAG, "Value \"" + value + "\" contains invalid character (= or ; or \\0)");
            } else {
                put(key, value);
            }
        }

        public void set(String key, int value) {
            put(key, Integer.toString(value));
        }

        private void put(String key, String value) {
            this.mMap.remove(key);
            this.mMap.put(key, value);
        }

        private void set(String key, List<Area> areas) {
            if (areas == null) {
                set(key, "(0,0,0,0,0)");
                return;
            }
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                Rect rect = area.rect;
                buffer.append('(');
                buffer.append(rect.left);
                buffer.append(',');
                buffer.append(rect.top);
                buffer.append(',');
                buffer.append(rect.right);
                buffer.append(',');
                buffer.append(rect.bottom);
                buffer.append(',');
                buffer.append(area.weight);
                buffer.append(')');
                if (i != areas.size() - 1) {
                    buffer.append(',');
                }
            }
            set(key, buffer.toString());
        }

        public String get(String key) {
            return this.mMap.get(key);
        }

        public int getInt(String key) {
            return Integer.parseInt(this.mMap.get(key));
        }

        public void setPreviewSize(int width, int height) {
            String v = Integer.toString(width) + "x" + Integer.toString(height);
            set(KEY_PREVIEW_SIZE, v);
        }

        public Size getPreviewSize() {
            String pair = get(KEY_PREVIEW_SIZE);
            return strToSize(pair);
        }

        public List<Size> getSupportedPreviewSizes() {
            String str = get("preview-size-values");
            return splitSize(str);
        }

        public List<Size> getSupportedVideoSizes() {
            String str = get("video-size-values");
            return splitSize(str);
        }

        public Size getPreferredPreviewSizeForVideo() {
            String pair = get(KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO);
            return strToSize(pair);
        }

        public void setJpegThumbnailSize(int width, int height) {
            set(KEY_JPEG_THUMBNAIL_WIDTH, width);
            set(KEY_JPEG_THUMBNAIL_HEIGHT, height);
        }

        public Size getJpegThumbnailSize() {
            return new Size(getInt(KEY_JPEG_THUMBNAIL_WIDTH), getInt(KEY_JPEG_THUMBNAIL_HEIGHT));
        }

        public List<Size> getSupportedJpegThumbnailSizes() {
            String str = get("jpeg-thumbnail-size-values");
            return splitSize(str);
        }

        public void setJpegThumbnailQuality(int quality) {
            set(KEY_JPEG_THUMBNAIL_QUALITY, quality);
        }

        public int getJpegThumbnailQuality() {
            return getInt(KEY_JPEG_THUMBNAIL_QUALITY);
        }

        public void setJpegQuality(int quality) {
            set(KEY_JPEG_QUALITY, quality);
        }

        public int getJpegQuality() {
            return getInt(KEY_JPEG_QUALITY);
        }

        @Deprecated
        public void setPreviewFrameRate(int fps) {
            set(KEY_PREVIEW_FRAME_RATE, fps);
        }

        @Deprecated
        public int getPreviewFrameRate() {
            return getInt(KEY_PREVIEW_FRAME_RATE);
        }

        @Deprecated
        public List<Integer> getSupportedPreviewFrameRates() {
            String str = get("preview-frame-rate-values");
            return splitInt(str);
        }

        public void setPreviewFpsRange(int min, int max) {
            set(KEY_PREVIEW_FPS_RANGE, "" + min + "," + max);
        }

        public void getPreviewFpsRange(int[] range) {
            if (range == null || range.length != 2) {
                throw new IllegalArgumentException("range must be an array with two elements.");
            }
            splitInt(get(KEY_PREVIEW_FPS_RANGE), range);
        }

        public List<int[]> getSupportedPreviewFpsRange() {
            String str = get("preview-fps-range-values");
            return splitRange(str);
        }

        public void setPreviewFormat(int pixel_format) {
            String s = cameraFormatForPixelFormat(pixel_format);
            if (s == null) {
                throw new IllegalArgumentException("Invalid pixel_format=" + pixel_format);
            }
            set(KEY_PREVIEW_FORMAT, s);
        }

        public int getPreviewFormat() {
            return pixelFormatForCameraFormat(get(KEY_PREVIEW_FORMAT));
        }

        public List<Integer> getSupportedPreviewFormats() {
            String str = get("preview-format-values");
            ArrayList<Integer> formats = new ArrayList<>();
            Iterator<String> it = split(str).iterator();
            while (it.hasNext()) {
                String s = it.next();
                int f = pixelFormatForCameraFormat(s);
                if (f != 0) {
                    formats.add(Integer.valueOf(f));
                }
            }
            return formats;
        }

        public void setPictureSize(int width, int height) {
            String v = Integer.toString(width) + "x" + Integer.toString(height);
            set(KEY_PICTURE_SIZE, v);
        }

        public Size getPictureSize() {
            String pair = get(KEY_PICTURE_SIZE);
            return strToSize(pair);
        }

        public List<Size> getSupportedPictureSizes() {
            String str = get("picture-size-values");
            return splitSize(str);
        }

        public void setPictureFormat(int pixel_format) {
            String s = cameraFormatForPixelFormat(pixel_format);
            if (s == null) {
                throw new IllegalArgumentException("Invalid pixel_format=" + pixel_format);
            }
            set(KEY_PICTURE_FORMAT, s);
        }

        public int getPictureFormat() {
            return pixelFormatForCameraFormat(get(KEY_PICTURE_FORMAT));
        }

        public List<Integer> getSupportedPictureFormats() {
            String str = get("picture-format-values");
            ArrayList<Integer> formats = new ArrayList<>();
            Iterator<String> it = split(str).iterator();
            while (it.hasNext()) {
                String s = it.next();
                int f = pixelFormatForCameraFormat(s);
                if (f != 0) {
                    formats.add(Integer.valueOf(f));
                }
            }
            return formats;
        }

        private String cameraFormatForPixelFormat(int pixel_format) {
            switch (pixel_format) {
                case 4:
                    return PIXEL_FORMAT_RGB565;
                case 16:
                    return PIXEL_FORMAT_YUV422SP;
                case 17:
                    return PIXEL_FORMAT_YUV420SP;
                case 20:
                    return PIXEL_FORMAT_YUV422I;
                case 256:
                    return PIXEL_FORMAT_JPEG;
                case 842094169:
                    return PIXEL_FORMAT_YUV420P;
                default:
                    return null;
            }
        }

        private int pixelFormatForCameraFormat(String format) {
            if (format == null) {
                return 0;
            }
            if (format.equals(PIXEL_FORMAT_YUV422SP)) {
                return 16;
            }
            if (format.equals(PIXEL_FORMAT_YUV420SP)) {
                return 17;
            }
            if (format.equals(PIXEL_FORMAT_YUV422I)) {
                return 20;
            }
            if (format.equals(PIXEL_FORMAT_YUV420P)) {
                return 842094169;
            }
            if (format.equals(PIXEL_FORMAT_RGB565)) {
                return 4;
            }
            if (!format.equals(PIXEL_FORMAT_JPEG)) {
                return 0;
            }
            return 256;
        }

        public void setRotation(int rotation) {
            if (rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270) {
                set(KEY_ROTATION, Integer.toString(rotation));
                return;
            }
            throw new IllegalArgumentException("Invalid rotation=" + rotation);
        }

        public void setGpsLatitude(double latitude) {
            set(KEY_GPS_LATITUDE, Double.toString(latitude));
        }

        public void setGpsLongitude(double longitude) {
            set(KEY_GPS_LONGITUDE, Double.toString(longitude));
        }

        public void setGpsAltitude(double altitude) {
            set(KEY_GPS_ALTITUDE, Double.toString(altitude));
        }

        public void setGpsTimestamp(long timestamp) {
            set(KEY_GPS_TIMESTAMP, Long.toString(timestamp));
        }

        public void setGpsProcessingMethod(String processing_method) {
            set(KEY_GPS_PROCESSING_METHOD, processing_method);
        }

        public void removeGpsData() {
            remove(KEY_GPS_LATITUDE);
            remove(KEY_GPS_LONGITUDE);
            remove(KEY_GPS_ALTITUDE);
            remove(KEY_GPS_TIMESTAMP);
            remove(KEY_GPS_PROCESSING_METHOD);
        }

        public String getWhiteBalance() {
            return get(KEY_WHITE_BALANCE);
        }

        public void setWhiteBalance(String value) {
            String oldValue = get(KEY_WHITE_BALANCE);
            if (same(value, oldValue)) {
                return;
            }
            set(KEY_WHITE_BALANCE, value);
            set(KEY_AUTO_WHITEBALANCE_LOCK, FALSE);
        }

        public List<String> getSupportedWhiteBalance() {
            String str = get("whitebalance-values");
            return split(str);
        }

        public String getColorEffect() {
            return get(KEY_EFFECT);
        }

        public void setColorEffect(String value) {
            set(KEY_EFFECT, value);
        }

        public List<String> getSupportedColorEffects() {
            String str = get("effect-values");
            return split(str);
        }

        public String getAntibanding() {
            return get(KEY_ANTIBANDING);
        }

        public void setAntibanding(String antibanding) {
            set(KEY_ANTIBANDING, antibanding);
        }

        public List<String> getSupportedAntibanding() {
            String str = get("antibanding-values");
            return split(str);
        }

        public String getSceneMode() {
            return get(KEY_SCENE_MODE);
        }

        public void setSceneMode(String value) {
            set(KEY_SCENE_MODE, value);
        }

        public List<String> getSupportedSceneModes() {
            String str = get("scene-mode-values");
            return split(str);
        }

        public String getFlashMode() {
            return get(KEY_FLASH_MODE);
        }

        public void setFlashMode(String value) {
            set(KEY_FLASH_MODE, value);
        }

        public List<String> getSupportedFlashModes() {
            String str = get("flash-mode-values");
            return split(str);
        }

        public String getFocusMode() {
            return get(KEY_FOCUS_MODE);
        }

        public void setFocusMode(String value) {
            set(KEY_FOCUS_MODE, value);
        }

        public List<String> getSupportedFocusModes() {
            String str = get("focus-mode-values");
            return split(str);
        }

        public float getFocalLength() {
            return Float.parseFloat(get(KEY_FOCAL_LENGTH));
        }

        public float getHorizontalViewAngle() {
            return Float.parseFloat(get(KEY_HORIZONTAL_VIEW_ANGLE));
        }

        public float getVerticalViewAngle() {
            return Float.parseFloat(get(KEY_VERTICAL_VIEW_ANGLE));
        }

        public int getExposureCompensation() {
            return getInt(KEY_EXPOSURE_COMPENSATION, 0);
        }

        public void setExposureCompensation(int value) {
            set(KEY_EXPOSURE_COMPENSATION, value);
        }

        public int getMaxExposureCompensation() {
            return getInt(KEY_MAX_EXPOSURE_COMPENSATION, 0);
        }

        public int getMinExposureCompensation() {
            return getInt(KEY_MIN_EXPOSURE_COMPENSATION, 0);
        }

        public float getExposureCompensationStep() {
            return getFloat(KEY_EXPOSURE_COMPENSATION_STEP, 0.0f);
        }

        public void setAutoExposureLock(boolean toggle) {
            set(KEY_AUTO_EXPOSURE_LOCK, toggle ? TRUE : FALSE);
        }

        public boolean getAutoExposureLock() {
            String str = get(KEY_AUTO_EXPOSURE_LOCK);
            return TRUE.equals(str);
        }

        public boolean isAutoExposureLockSupported() {
            String str = get(KEY_AUTO_EXPOSURE_LOCK_SUPPORTED);
            return TRUE.equals(str);
        }

        public void setAutoWhiteBalanceLock(boolean toggle) {
            set(KEY_AUTO_WHITEBALANCE_LOCK, toggle ? TRUE : FALSE);
        }

        public boolean getAutoWhiteBalanceLock() {
            String str = get(KEY_AUTO_WHITEBALANCE_LOCK);
            return TRUE.equals(str);
        }

        public boolean isAutoWhiteBalanceLockSupported() {
            String str = get(KEY_AUTO_WHITEBALANCE_LOCK_SUPPORTED);
            return TRUE.equals(str);
        }

        public int getZoom() {
            return getInt(KEY_ZOOM, 0);
        }

        public void setZoom(int value) {
            set(KEY_ZOOM, value);
        }

        public boolean isZoomSupported() {
            String str = get(KEY_ZOOM_SUPPORTED);
            return TRUE.equals(str);
        }

        public int getMaxZoom() {
            return getInt(KEY_MAX_ZOOM, 0);
        }

        public List<Integer> getZoomRatios() {
            return splitInt(get(KEY_ZOOM_RATIOS));
        }

        public boolean isSmoothZoomSupported() {
            String str = get(KEY_SMOOTH_ZOOM_SUPPORTED);
            return TRUE.equals(str);
        }

        public void getFocusDistances(float[] output) {
            if (output == null || output.length != 3) {
                throw new IllegalArgumentException("output must be a float array with three elements.");
            }
            splitFloat(get(KEY_FOCUS_DISTANCES), output);
        }

        public int getMaxNumFocusAreas() {
            return getInt(KEY_MAX_NUM_FOCUS_AREAS, 0);
        }

        public List<Area> getFocusAreas() {
            return splitArea(get(KEY_FOCUS_AREAS));
        }

        public void setFocusAreas(List<Area> focusAreas) {
            set(KEY_FOCUS_AREAS, focusAreas);
        }

        public int getMaxNumMeteringAreas() {
            return getInt(KEY_MAX_NUM_METERING_AREAS, 0);
        }

        public List<Area> getMeteringAreas() {
            return splitArea(get(KEY_METERING_AREAS));
        }

        public void setMeteringAreas(List<Area> meteringAreas) {
            set(KEY_METERING_AREAS, meteringAreas);
        }

        public int getMaxNumDetectedFaces() {
            return getInt(KEY_MAX_NUM_DETECTED_FACES_HW, 0);
        }

        public void setRecordingHint(boolean hint) {
            set(KEY_RECORDING_HINT, hint ? TRUE : FALSE);
        }

        public boolean isVideoSnapshotSupported() {
            String str = get(KEY_VIDEO_SNAPSHOT_SUPPORTED);
            return TRUE.equals(str);
        }

        public void setVideoStabilization(boolean toggle) {
            set(KEY_VIDEO_STABILIZATION, toggle ? TRUE : FALSE);
        }

        public boolean getVideoStabilization() {
            String str = get(KEY_VIDEO_STABILIZATION);
            return TRUE.equals(str);
        }

        public boolean isVideoStabilizationSupported() {
            String str = get(KEY_VIDEO_STABILIZATION_SUPPORTED);
            return TRUE.equals(str);
        }

        private ArrayList<String> split(String str) {
            if (str == null) {
                return null;
            }
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(str);
            ArrayList<String> substrings = new ArrayList<>();
            for (String s : splitter) {
                substrings.add(s);
            }
            return substrings;
        }

        private ArrayList<Integer> splitInt(String str) {
            if (str == null) {
                return null;
            }
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(str);
            ArrayList<Integer> substrings = new ArrayList<>();
            for (String s : splitter) {
                substrings.add(Integer.valueOf(Integer.parseInt(s)));
            }
            if (substrings.size() == 0) {
                return null;
            }
            return substrings;
        }

        private void splitInt(String str, int[] output) {
            if (str == null) {
                return;
            }
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(str);
            int index = 0;
            for (String s : splitter) {
                output[index] = Integer.parseInt(s);
                index++;
            }
        }

        private void splitFloat(String str, float[] output) {
            if (str == null) {
                return;
            }
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(str);
            int index = 0;
            for (String s : splitter) {
                output[index] = Float.parseFloat(s);
                index++;
            }
        }

        private float getFloat(String key, float defaultValue) {
            try {
                return Float.parseFloat(this.mMap.get(key));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        private int getInt(String key, int defaultValue) {
            try {
                return Integer.parseInt(this.mMap.get(key));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        private ArrayList<Size> splitSize(String str) {
            if (str == null) {
                return null;
            }
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
            splitter.setString(str);
            ArrayList<Size> sizeList = new ArrayList<>();
            for (String s : splitter) {
                Size size = strToSize(s);
                if (size != null) {
                    sizeList.add(size);
                }
            }
            if (sizeList.size() == 0) {
                return null;
            }
            return sizeList;
        }

        private Size strToSize(String str) {
            if (str == null) {
                return null;
            }
            int pos = str.indexOf(120);
            if (pos != -1) {
                String width = str.substring(0, pos);
                String height = str.substring(pos + 1);
                return new Size(Integer.parseInt(width), Integer.parseInt(height));
            }
            Log.m110e(Camera.TAG, "Invalid size parameter string=" + str);
            return null;
        }

        private ArrayList<int[]> splitRange(String str) {
            int endIndex;
            if (str == null || str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
                Log.m110e(Camera.TAG, "Invalid range list string=" + str);
                return null;
            }
            ArrayList<int[]> rangeList = new ArrayList<>();
            int fromIndex = 1;
            do {
                int[] range = new int[2];
                endIndex = str.indexOf("),(", fromIndex);
                if (endIndex == -1) {
                    endIndex = str.length() - 1;
                }
                splitInt(str.substring(fromIndex, endIndex), range);
                rangeList.add(range);
                fromIndex = endIndex + 3;
            } while (endIndex != str.length() - 1);
            if (rangeList.size() == 0) {
                return null;
            }
            return rangeList;
        }

        private ArrayList<Area> splitArea(String str) {
            int endIndex;
            if (str == null || str.charAt(0) != '(' || str.charAt(str.length() - 1) != ')') {
                Log.m110e(Camera.TAG, "Invalid area string=" + str);
                return null;
            }
            ArrayList<Area> result = new ArrayList<>();
            int fromIndex = 1;
            int[] array = new int[5];
            do {
                endIndex = str.indexOf("),(", fromIndex);
                if (endIndex == -1) {
                    endIndex = str.length() - 1;
                }
                splitInt(str.substring(fromIndex, endIndex), array);
                result.add(new Area(new Rect(array[0], array[1], array[2], array[3]), array[4]));
                fromIndex = endIndex + 3;
            } while (endIndex != str.length() - 1);
            if (result.size() == 0) {
                return null;
            }
            if (result.size() == 1) {
                Area area = result.get(0);
                Rect rect = area.rect;
                if (rect.left == 0 && rect.top == 0 && rect.right == 0 && rect.bottom == 0 && area.weight == 0) {
                    return null;
                }
            }
            return result;
        }

        private boolean same(String s1, String s2) {
            if (s1 == null && s2 == null) {
                return true;
            }
            if (s1 != null && s1.equals(s2)) {
                return true;
            }
            return false;
        }
    }
}
