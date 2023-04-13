package android.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.MagnificationConfig;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.p001pm.ParceledListSlice;
import android.graphics.ColorSpace;
import android.graphics.ParcelableColorSpace;
import android.graphics.Region;
import android.hardware.HardwareBuffer;
import android.hardware.display.DisplayManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityCache;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.CancellationGroup;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
import com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import com.android.internal.inputmethod.RemoteAccessibilityInputConnection;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public abstract class AccessibilityService extends Service {
    public static final int ACCESSIBILITY_TAKE_SCREENSHOT_REQUEST_INTERVAL_TIMES_MS = 333;
    public static final int ERROR_TAKE_SCREENSHOT_INTERNAL_ERROR = 1;
    public static final int ERROR_TAKE_SCREENSHOT_INTERVAL_TIME_SHORT = 3;
    public static final int ERROR_TAKE_SCREENSHOT_INVALID_DISPLAY = 4;
    public static final int ERROR_TAKE_SCREENSHOT_INVALID_WINDOW = 5;
    public static final int ERROR_TAKE_SCREENSHOT_NO_ACCESSIBILITY_ACCESS = 2;
    public static final int ERROR_TAKE_SCREENSHOT_SECURE_WINDOW = 6;
    public static final int GESTURE_2_FINGER_DOUBLE_TAP = 20;
    public static final int GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD = 40;
    public static final int GESTURE_2_FINGER_SINGLE_TAP = 19;
    public static final int GESTURE_2_FINGER_SWIPE_DOWN = 26;
    public static final int GESTURE_2_FINGER_SWIPE_LEFT = 27;
    public static final int GESTURE_2_FINGER_SWIPE_RIGHT = 28;
    public static final int GESTURE_2_FINGER_SWIPE_UP = 25;
    public static final int GESTURE_2_FINGER_TRIPLE_TAP = 21;
    public static final int GESTURE_2_FINGER_TRIPLE_TAP_AND_HOLD = 43;
    public static final int GESTURE_3_FINGER_DOUBLE_TAP = 23;
    public static final int GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD = 41;
    public static final int GESTURE_3_FINGER_SINGLE_TAP = 22;
    public static final int GESTURE_3_FINGER_SINGLE_TAP_AND_HOLD = 44;
    public static final int GESTURE_3_FINGER_SWIPE_DOWN = 30;
    public static final int GESTURE_3_FINGER_SWIPE_LEFT = 31;
    public static final int GESTURE_3_FINGER_SWIPE_RIGHT = 32;
    public static final int GESTURE_3_FINGER_SWIPE_UP = 29;
    public static final int GESTURE_3_FINGER_TRIPLE_TAP = 24;
    public static final int GESTURE_3_FINGER_TRIPLE_TAP_AND_HOLD = 45;
    public static final int GESTURE_4_FINGER_DOUBLE_TAP = 38;
    public static final int GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD = 42;
    public static final int GESTURE_4_FINGER_SINGLE_TAP = 37;
    public static final int GESTURE_4_FINGER_SWIPE_DOWN = 34;
    public static final int GESTURE_4_FINGER_SWIPE_LEFT = 35;
    public static final int GESTURE_4_FINGER_SWIPE_RIGHT = 36;
    public static final int GESTURE_4_FINGER_SWIPE_UP = 33;
    public static final int GESTURE_4_FINGER_TRIPLE_TAP = 39;
    public static final int GESTURE_DOUBLE_TAP = 17;
    public static final int GESTURE_DOUBLE_TAP_AND_HOLD = 18;
    public static final int GESTURE_PASSTHROUGH = -1;
    public static final int GESTURE_SWIPE_DOWN = 2;
    public static final int GESTURE_SWIPE_DOWN_AND_LEFT = 15;
    public static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 16;
    public static final int GESTURE_SWIPE_DOWN_AND_UP = 8;
    public static final int GESTURE_SWIPE_LEFT = 3;
    public static final int GESTURE_SWIPE_LEFT_AND_DOWN = 10;
    public static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 5;
    public static final int GESTURE_SWIPE_LEFT_AND_UP = 9;
    public static final int GESTURE_SWIPE_RIGHT = 4;
    public static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 12;
    public static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 6;
    public static final int GESTURE_SWIPE_RIGHT_AND_UP = 11;
    public static final int GESTURE_SWIPE_UP = 1;
    public static final int GESTURE_SWIPE_UP_AND_DOWN = 7;
    public static final int GESTURE_SWIPE_UP_AND_LEFT = 13;
    public static final int GESTURE_SWIPE_UP_AND_RIGHT = 14;
    public static final int GESTURE_TOUCH_EXPLORATION = -2;
    public static final int GESTURE_UNKNOWN = 0;
    public static final int GLOBAL_ACTION_ACCESSIBILITY_ALL_APPS = 14;
    public static final int GLOBAL_ACTION_ACCESSIBILITY_BUTTON = 11;
    public static final int GLOBAL_ACTION_ACCESSIBILITY_BUTTON_CHOOSER = 12;
    public static final int GLOBAL_ACTION_ACCESSIBILITY_SHORTCUT = 13;
    public static final int GLOBAL_ACTION_BACK = 1;
    public static final int GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE = 15;
    public static final int GLOBAL_ACTION_DPAD_CENTER = 20;
    public static final int GLOBAL_ACTION_DPAD_DOWN = 17;
    public static final int GLOBAL_ACTION_DPAD_LEFT = 18;
    public static final int GLOBAL_ACTION_DPAD_RIGHT = 19;
    public static final int GLOBAL_ACTION_DPAD_UP = 16;
    public static final int GLOBAL_ACTION_HOME = 2;
    public static final int GLOBAL_ACTION_KEYCODE_HEADSETHOOK = 10;
    public static final int GLOBAL_ACTION_LOCK_SCREEN = 8;
    public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
    public static final int GLOBAL_ACTION_POWER_DIALOG = 6;
    public static final int GLOBAL_ACTION_QUICK_SETTINGS = 5;
    public static final int GLOBAL_ACTION_RECENTS = 3;
    public static final int GLOBAL_ACTION_TAKE_SCREENSHOT = 9;
    public static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 7;
    public static final String KEY_ACCESSIBILITY_SCREENSHOT_COLORSPACE = "screenshot_colorSpace";
    public static final String KEY_ACCESSIBILITY_SCREENSHOT_HARDWAREBUFFER = "screenshot_hardwareBuffer";
    public static final String KEY_ACCESSIBILITY_SCREENSHOT_STATUS = "screenshot_status";
    public static final String KEY_ACCESSIBILITY_SCREENSHOT_TIMESTAMP = "screenshot_timestamp";
    private static final String LOG_TAG = "AccessibilityService";
    public static final String SERVICE_INTERFACE = "android.accessibilityservice.AccessibilityService";
    public static final String SERVICE_META_DATA = "android.accessibilityservice";
    public static final int SHOW_MODE_AUTO = 0;
    public static final int SHOW_MODE_HARD_KEYBOARD_ORIGINAL_VALUE = 536870912;
    public static final int SHOW_MODE_HARD_KEYBOARD_OVERRIDDEN = 1073741824;
    public static final int SHOW_MODE_HIDDEN = 1;
    public static final int SHOW_MODE_IGNORE_HARD_KEYBOARD = 2;
    public static final int SHOW_MODE_MASK = 3;
    public static final int TAKE_SCREENSHOT_SUCCESS = 0;
    private FingerprintGestureController mFingerprintGestureController;
    private SparseArray<GestureResultCallbackInfo> mGestureStatusCallbackInfos;
    private int mGestureStatusCallbackSequence;
    private AccessibilityServiceInfo mInfo;
    private InputMethod mInputMethod;
    private int mMotionEventSources;
    private SoftKeyboardController mSoftKeyboardController;
    private WindowManager mWindowManager;
    private IBinder mWindowToken;
    private int mConnectionId = -1;
    private final SparseArray<MagnificationController> mMagnificationControllers = new SparseArray<>(0);
    private final SparseArray<TouchInteractionController> mTouchInteractionControllers = new SparseArray<>(0);
    private boolean mInputMethodInitialized = false;
    private final SparseArray<AccessibilityButtonController> mAccessibilityButtonControllers = new SparseArray<>(0);
    private final Object mLock = new Object();

    /* loaded from: classes.dex */
    public interface Callbacks {
        void createImeSession(IAccessibilityInputMethodSessionCallback iAccessibilityInputMethodSessionCallback);

        void init(int i, IBinder iBinder);

        void onAccessibilityButtonAvailabilityChanged(boolean z);

        void onAccessibilityButtonClicked(int i);

        void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        void onFingerprintCapturingGesturesChanged(boolean z);

        void onFingerprintGesture(int i);

        boolean onGesture(AccessibilityGestureEvent accessibilityGestureEvent);

        void onInterrupt();

        boolean onKeyEvent(KeyEvent keyEvent);

        void onMagnificationChanged(int i, Region region, MagnificationConfig magnificationConfig);

        void onMotionEvent(MotionEvent motionEvent);

        void onPerformGestureResult(int i, boolean z);

        void onServiceConnected();

        void onSoftKeyboardShowModeChanged(int i);

        void onSystemActionsChanged();

        void onTouchStateChanged(int i, int i2);

        void startInput(RemoteAccessibilityInputConnection remoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ScreenshotErrorCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SoftKeyboardShowMode {
    }

    /* loaded from: classes.dex */
    public interface TakeScreenshotCallback {
        void onFailure(int i);

        void onSuccess(ScreenshotResult screenshotResult);
    }

    public abstract void onAccessibilityEvent(AccessibilityEvent accessibilityEvent);

    public abstract void onInterrupt();

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchServiceConnected() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mMagnificationControllers.size(); i++) {
                this.mMagnificationControllers.valueAt(i).onServiceConnectedLocked();
            }
            AccessibilityServiceInfo info = getServiceInfo();
            if (info != null) {
                updateInputMethod(info);
                this.mMotionEventSources = info.getMotionEventSources();
            }
        }
        SoftKeyboardController softKeyboardController = this.mSoftKeyboardController;
        if (softKeyboardController != null) {
            softKeyboardController.onServiceConnected();
        }
        onServiceConnected();
    }

    private void updateInputMethod(AccessibilityServiceInfo info) {
        if (info != null) {
            boolean requestIme = (info.flags & 32768) != 0;
            if (requestIme && !this.mInputMethodInitialized) {
                this.mInputMethod = onCreateInputMethod();
                this.mInputMethodInitialized = true;
                return;
            }
            if ((requestIme ? false : true) & this.mInputMethodInitialized) {
                this.mInputMethod = null;
                this.mInputMethodInitialized = false;
            }
        }
    }

    protected void onServiceConnected() {
    }

    @Deprecated
    protected boolean onGesture(int gestureId) {
        return false;
    }

    public boolean onGesture(AccessibilityGestureEvent gestureEvent) {
        if (gestureEvent.getDisplayId() == 0) {
            onGesture(gestureEvent.getGestureId());
            return false;
        }
        return false;
    }

    protected boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    public void onMotionEvent(MotionEvent event) {
    }

    public List<AccessibilityWindowInfo> getWindows() {
        return AccessibilityInteractionClient.getInstance(this).getWindows(this.mConnectionId);
    }

    public final SparseArray<List<AccessibilityWindowInfo>> getWindowsOnAllDisplays() {
        return AccessibilityInteractionClient.getInstance(this).getWindowsOnAllDisplays(this.mConnectionId);
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return getRootInActiveWindow(4);
    }

    public AccessibilityNodeInfo getRootInActiveWindow(int prefetchingStrategy) {
        return AccessibilityInteractionClient.getInstance(this).getRootInActiveWindow(this.mConnectionId, prefetchingStrategy);
    }

    public final void disableSelf() {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.disableSelf();
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createDisplayContext(Display display) {
        return new AccessibilityContext(super.createDisplayContext(display), this.mConnectionId);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createWindowContext(int type, Bundle options) {
        Context context = super.createWindowContext(type, options);
        if (type != 2032) {
            return context;
        }
        return new AccessibilityContext(context, this.mConnectionId);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createWindowContext(Display display, int type, Bundle options) {
        Context context = super.createWindowContext(display, type, options);
        if (type != 2032) {
            return context;
        }
        return new AccessibilityContext(context, this.mConnectionId);
    }

    public final MagnificationController getMagnificationController() {
        return getMagnificationController(0);
    }

    public final MagnificationController getMagnificationController(int displayId) {
        MagnificationController controller;
        synchronized (this.mLock) {
            controller = this.mMagnificationControllers.get(displayId);
            if (controller == null) {
                controller = new MagnificationController(this, this.mLock, displayId);
                this.mMagnificationControllers.put(displayId, controller);
            }
        }
        return controller;
    }

    public final FingerprintGestureController getFingerprintGestureController() {
        if (this.mFingerprintGestureController == null) {
            AccessibilityInteractionClient.getInstance(this);
            this.mFingerprintGestureController = new FingerprintGestureController(AccessibilityInteractionClient.getConnection(this.mConnectionId));
        }
        return this.mFingerprintGestureController;
    }

    public final boolean dispatchGesture(GestureDescription gesture, GestureResultCallback callback, Handler handler) {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection == null) {
            return false;
        }
        int sampleTimeMs = calculateGestureSampleTimeMs(gesture.getDisplayId());
        List<GestureDescription.GestureStep> steps = GestureDescription.MotionEventGenerator.getGestureStepsFromGestureDescription(gesture, sampleTimeMs);
        try {
            synchronized (this.mLock) {
                this.mGestureStatusCallbackSequence++;
                if (callback != null) {
                    if (this.mGestureStatusCallbackInfos == null) {
                        this.mGestureStatusCallbackInfos = new SparseArray<>();
                    }
                    GestureResultCallbackInfo callbackInfo = new GestureResultCallbackInfo(gesture, callback, handler);
                    this.mGestureStatusCallbackInfos.put(this.mGestureStatusCallbackSequence, callbackInfo);
                }
                connection.dispatchGesture(this.mGestureStatusCallbackSequence, new ParceledListSlice(steps), gesture.getDisplayId());
            }
            return true;
        } catch (RemoteException re) {
            throw new RuntimeException(re);
        }
    }

    private int calculateGestureSampleTimeMs(int displayId) {
        Display display;
        int sampleTimeMs;
        if (getApplicationInfo().targetSdkVersion > 29 && (display = ((DisplayManager) getSystemService(DisplayManager.class)).getDisplay(displayId)) != null && (sampleTimeMs = (int) (1000 / display.getRefreshRate())) >= 1) {
            return sampleTimeMs;
        }
        return 100;
    }

    void onPerformGestureResult(int sequence, final boolean completedSuccessfully) {
        final GestureResultCallbackInfo callbackInfo;
        if (this.mGestureStatusCallbackInfos == null) {
            return;
        }
        synchronized (this.mLock) {
            callbackInfo = this.mGestureStatusCallbackInfos.get(sequence);
            this.mGestureStatusCallbackInfos.remove(sequence);
        }
        if (callbackInfo != null && callbackInfo.gestureDescription != null && callbackInfo.callback != null) {
            if (callbackInfo.handler != null) {
                callbackInfo.handler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityService.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (completedSuccessfully) {
                            callbackInfo.callback.onCompleted(callbackInfo.gestureDescription);
                        } else {
                            callbackInfo.callback.onCancelled(callbackInfo.gestureDescription);
                        }
                    }
                });
            } else if (completedSuccessfully) {
                callbackInfo.callback.onCompleted(callbackInfo.gestureDescription);
            } else {
                callbackInfo.callback.onCancelled(callbackInfo.gestureDescription);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMagnificationChanged(int displayId, Region region, MagnificationConfig config) {
        MagnificationController controller;
        synchronized (this.mLock) {
            controller = this.mMagnificationControllers.get(displayId);
        }
        if (controller != null) {
            controller.dispatchMagnificationChanged(region, config);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFingerprintCapturingGesturesChanged(boolean active) {
        getFingerprintGestureController().onGestureDetectionActiveChanged(active);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFingerprintGesture(int gesture) {
        getFingerprintGestureController().onGesture(gesture);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getConnectionId() {
        return this.mConnectionId;
    }

    /* loaded from: classes.dex */
    public static final class MagnificationController {
        private final int mDisplayId;
        private ArrayMap<OnMagnificationChangedListener, Handler> mListeners;
        private final Object mLock;
        private final AccessibilityService mService;

        MagnificationController(AccessibilityService service, Object lock, int displayId) {
            this.mService = service;
            this.mLock = lock;
            this.mDisplayId = displayId;
        }

        void onServiceConnectedLocked() {
            ArrayMap<OnMagnificationChangedListener, Handler> arrayMap = this.mListeners;
            if (arrayMap != null && !arrayMap.isEmpty()) {
                setMagnificationCallbackEnabled(true);
            }
        }

        public void addListener(OnMagnificationChangedListener listener) {
            addListener(listener, null);
        }

        public void addListener(OnMagnificationChangedListener listener, Handler handler) {
            synchronized (this.mLock) {
                if (this.mListeners == null) {
                    this.mListeners = new ArrayMap<>();
                }
                boolean shouldEnableCallback = this.mListeners.isEmpty();
                this.mListeners.put(listener, handler);
                if (shouldEnableCallback) {
                    setMagnificationCallbackEnabled(true);
                }
            }
        }

        public boolean removeListener(OnMagnificationChangedListener listener) {
            boolean hasKey;
            if (this.mListeners == null) {
                return false;
            }
            synchronized (this.mLock) {
                int keyIndex = this.mListeners.indexOfKey(listener);
                hasKey = keyIndex >= 0;
                if (hasKey) {
                    this.mListeners.removeAt(keyIndex);
                }
                if (hasKey && this.mListeners.isEmpty()) {
                    setMagnificationCallbackEnabled(false);
                }
            }
            return hasKey;
        }

        private void setMagnificationCallbackEnabled(boolean enabled) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    connection.setMagnificationCallbackEnabled(this.mDisplayId, enabled);
                } catch (RemoteException re) {
                    throw new RuntimeException(re);
                }
            }
        }

        void dispatchMagnificationChanged(final Region region, final MagnificationConfig config) {
            synchronized (this.mLock) {
                ArrayMap<OnMagnificationChangedListener, Handler> arrayMap = this.mListeners;
                if (arrayMap != null && !arrayMap.isEmpty()) {
                    ArrayMap<OnMagnificationChangedListener, Handler> entries = new ArrayMap<>(this.mListeners);
                    int count = entries.size();
                    for (int i = 0; i < count; i++) {
                        final OnMagnificationChangedListener listener = entries.keyAt(i);
                        Handler handler = entries.valueAt(i);
                        if (handler != null) {
                            handler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$MagnificationController$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AccessibilityService.MagnificationController.this.lambda$dispatchMagnificationChanged$0(listener, region, config);
                                }
                            });
                        } else {
                            listener.onMagnificationChanged(this, region, config);
                        }
                    }
                    return;
                }
                Slog.m98d("AccessibilityService", "Received magnification changed callback with no listeners registered!");
                setMagnificationCallbackEnabled(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchMagnificationChanged$0(OnMagnificationChangedListener listener, Region region, MagnificationConfig config) {
            listener.onMagnificationChanged(this, region, config);
        }

        public MagnificationConfig getMagnificationConfig() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getMagnificationConfig(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain magnification config", re);
                    re.rethrowFromSystemServer();
                    return null;
                }
            }
            return null;
        }

        @Deprecated
        public float getScale() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getMagnificationScale(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain scale", re);
                    re.rethrowFromSystemServer();
                    return 1.0f;
                }
            }
            return 1.0f;
        }

        @Deprecated
        public float getCenterX() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getMagnificationCenterX(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain center X", re);
                    re.rethrowFromSystemServer();
                    return 0.0f;
                }
            }
            return 0.0f;
        }

        @Deprecated
        public float getCenterY() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getMagnificationCenterY(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain center Y", re);
                    re.rethrowFromSystemServer();
                    return 0.0f;
                }
            }
            return 0.0f;
        }

        @Deprecated
        public Region getMagnificationRegion() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getMagnificationRegion(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain magnified region", re);
                    re.rethrowFromSystemServer();
                }
            }
            return Region.obtain();
        }

        public Region getCurrentMagnificationRegion() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getCurrentMagnificationRegion(this.mDisplayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to obtain the current magnified region", re);
                    re.rethrowFromSystemServer();
                }
            }
            return Region.obtain();
        }

        public boolean reset(boolean animate) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.resetMagnification(this.mDisplayId, animate);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to reset", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        public boolean resetCurrentMagnification(boolean animate) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.resetCurrentMagnification(this.mDisplayId, animate);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to reset", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        public boolean setMagnificationConfig(MagnificationConfig config, boolean animate) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.setMagnificationConfig(this.mDisplayId, config, animate);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to set magnification config", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        @Deprecated
        public boolean setScale(float scale, boolean animate) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    MagnificationConfig config = new MagnificationConfig.Builder().setMode(1).setScale(scale).build();
                    return connection.setMagnificationConfig(this.mDisplayId, config, animate);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to set scale", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        @Deprecated
        public boolean setCenter(float centerX, float centerY, boolean animate) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    MagnificationConfig config = new MagnificationConfig.Builder().setMode(1).setCenterX(centerX).setCenterY(centerY).build();
                    return connection.setMagnificationConfig(this.mDisplayId, config, animate);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to set center", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        /* loaded from: classes.dex */
        public interface OnMagnificationChangedListener {
            @Deprecated
            void onMagnificationChanged(MagnificationController magnificationController, Region region, float f, float f2, float f3);

            default void onMagnificationChanged(MagnificationController controller, Region region, MagnificationConfig config) {
                if (config.getMode() == 1) {
                    onMagnificationChanged(controller, region, config.getScale(), config.getCenterX(), config.getCenterY());
                }
            }
        }
    }

    public final SoftKeyboardController getSoftKeyboardController() {
        SoftKeyboardController softKeyboardController;
        synchronized (this.mLock) {
            if (this.mSoftKeyboardController == null) {
                this.mSoftKeyboardController = new SoftKeyboardController(this, this.mLock);
            }
            softKeyboardController = this.mSoftKeyboardController;
        }
        return softKeyboardController;
    }

    public InputMethod onCreateInputMethod() {
        return new InputMethod(this);
    }

    public final InputMethod getInputMethod() {
        return this.mInputMethod;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSoftKeyboardShowModeChanged(int showMode) {
        SoftKeyboardController softKeyboardController = this.mSoftKeyboardController;
        if (softKeyboardController != null) {
            softKeyboardController.dispatchSoftKeyboardShowModeChanged(showMode);
        }
    }

    /* loaded from: classes.dex */
    public static final class SoftKeyboardController {
        public static final int ENABLE_IME_FAIL_BY_ADMIN = 1;
        public static final int ENABLE_IME_FAIL_UNKNOWN = 2;
        public static final int ENABLE_IME_SUCCESS = 0;
        private ArrayMap<OnShowModeChangedListener, Handler> mListeners;
        private final Object mLock;
        private final AccessibilityService mService;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface EnableImeResult {
        }

        /* loaded from: classes.dex */
        public interface OnShowModeChangedListener {
            void onShowModeChanged(SoftKeyboardController softKeyboardController, int i);
        }

        SoftKeyboardController(AccessibilityService service, Object lock) {
            this.mService = service;
            this.mLock = lock;
        }

        void onServiceConnected() {
            synchronized (this.mLock) {
                ArrayMap<OnShowModeChangedListener, Handler> arrayMap = this.mListeners;
                if (arrayMap != null && !arrayMap.isEmpty()) {
                    setSoftKeyboardCallbackEnabled(true);
                }
            }
        }

        public void addOnShowModeChangedListener(OnShowModeChangedListener listener) {
            addOnShowModeChangedListener(listener, null);
        }

        public void addOnShowModeChangedListener(OnShowModeChangedListener listener, Handler handler) {
            synchronized (this.mLock) {
                if (this.mListeners == null) {
                    this.mListeners = new ArrayMap<>();
                }
                boolean shouldEnableCallback = this.mListeners.isEmpty();
                this.mListeners.put(listener, handler);
                if (shouldEnableCallback) {
                    setSoftKeyboardCallbackEnabled(true);
                }
            }
        }

        public boolean removeOnShowModeChangedListener(OnShowModeChangedListener listener) {
            boolean hasKey;
            if (this.mListeners == null) {
                return false;
            }
            synchronized (this.mLock) {
                int keyIndex = this.mListeners.indexOfKey(listener);
                hasKey = keyIndex >= 0;
                if (hasKey) {
                    this.mListeners.removeAt(keyIndex);
                }
                if (hasKey && this.mListeners.isEmpty()) {
                    setSoftKeyboardCallbackEnabled(false);
                }
            }
            return hasKey;
        }

        private void setSoftKeyboardCallbackEnabled(boolean enabled) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    connection.setSoftKeyboardCallbackEnabled(enabled);
                } catch (RemoteException re) {
                    throw new RuntimeException(re);
                }
            }
        }

        void dispatchSoftKeyboardShowModeChanged(final int showMode) {
            synchronized (this.mLock) {
                ArrayMap<OnShowModeChangedListener, Handler> arrayMap = this.mListeners;
                if (arrayMap != null && !arrayMap.isEmpty()) {
                    ArrayMap<OnShowModeChangedListener, Handler> entries = new ArrayMap<>(this.mListeners);
                    int count = entries.size();
                    for (int i = 0; i < count; i++) {
                        final OnShowModeChangedListener listener = entries.keyAt(i);
                        Handler handler = entries.valueAt(i);
                        if (handler != null) {
                            handler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityService.SoftKeyboardController.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    listener.onShowModeChanged(SoftKeyboardController.this, showMode);
                                }
                            });
                        } else {
                            listener.onShowModeChanged(this, showMode);
                        }
                    }
                    return;
                }
                Slog.m90w("AccessibilityService", "Received soft keyboard show mode changed callback with no listeners registered!");
                setSoftKeyboardCallbackEnabled(false);
            }
        }

        public int getShowMode() {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.getSoftKeyboardShowMode();
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to set soft keyboard behavior", re);
                    re.rethrowFromSystemServer();
                    return 0;
                }
            }
            return 0;
        }

        public boolean setShowMode(int showMode) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.setSoftKeyboardShowMode(showMode);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to set soft keyboard behavior", re);
                    re.rethrowFromSystemServer();
                    return false;
                }
            }
            return false;
        }

        public boolean switchToInputMethod(String imeId) {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.switchToInputMethod(imeId);
                } catch (RemoteException re) {
                    throw new RuntimeException(re);
                }
            }
            return false;
        }

        public int setInputMethodEnabled(String imeId, boolean enabled) throws SecurityException {
            AccessibilityInteractionClient.getInstance(this.mService);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mService.mConnectionId);
            if (connection != null) {
                try {
                    return connection.setInputMethodEnabled(imeId, enabled);
                } catch (RemoteException re) {
                    throw new RuntimeException(re);
                }
            }
            return 2;
        }
    }

    public final AccessibilityButtonController getAccessibilityButtonController() {
        return getAccessibilityButtonController(0);
    }

    public final AccessibilityButtonController getAccessibilityButtonController(int displayId) {
        AccessibilityButtonController controller;
        synchronized (this.mLock) {
            controller = this.mAccessibilityButtonControllers.get(displayId);
            if (controller == null) {
                AccessibilityInteractionClient.getInstance(this);
                controller = new AccessibilityButtonController(AccessibilityInteractionClient.getConnection(this.mConnectionId));
                this.mAccessibilityButtonControllers.put(displayId, controller);
            }
        }
        return controller;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccessibilityButtonClicked(int displayId) {
        getAccessibilityButtonController(displayId).dispatchAccessibilityButtonClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccessibilityButtonAvailabilityChanged(boolean available) {
        getAccessibilityButtonController().dispatchAccessibilityButtonAvailabilityChanged(available);
    }

    public boolean setCacheEnabled(boolean enabled) {
        IAccessibilityServiceConnection connection;
        AccessibilityCache cache = AccessibilityInteractionClient.getCache(this.mConnectionId);
        if (cache == null || (connection = AccessibilityInteractionClient.getConnection(this.mConnectionId)) == null) {
            return false;
        }
        try {
            connection.setCacheEnabled(enabled);
            cache.setEnabled(enabled);
            return true;
        } catch (RemoteException re) {
            Log.m103w("AccessibilityService", "Error while setting status of cache", re);
            re.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean clearCachedSubtree(AccessibilityNodeInfo node) {
        AccessibilityCache cache = AccessibilityInteractionClient.getCache(this.mConnectionId);
        if (cache == null) {
            return false;
        }
        return cache.clearSubTree(node);
    }

    public boolean clearCache() {
        AccessibilityCache cache = AccessibilityInteractionClient.getCache(this.mConnectionId);
        if (cache == null) {
            return false;
        }
        cache.clear();
        return true;
    }

    public boolean isNodeInCache(AccessibilityNodeInfo node) {
        AccessibilityCache cache = AccessibilityInteractionClient.getCache(this.mConnectionId);
        if (cache == null) {
            return false;
        }
        return cache.isNodeInCache(node);
    }

    public boolean isCacheEnabled() {
        AccessibilityCache cache = AccessibilityInteractionClient.getCache(this.mConnectionId);
        if (cache == null) {
            return false;
        }
        return cache.isEnabled();
    }

    public void onSystemActionsChanged() {
    }

    public final List<AccessibilityNodeInfo.AccessibilityAction> getSystemActions() {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.getSystemActions();
            } catch (RemoteException re) {
                Log.m103w("AccessibilityService", "Error while calling getSystemActions", re);
                re.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public final boolean performGlobalAction(int action) {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.performGlobalAction(action);
            } catch (RemoteException re) {
                Log.m103w("AccessibilityService", "Error while calling performGlobalAction", re);
                re.rethrowFromSystemServer();
                return false;
            }
        }
        return false;
    }

    public AccessibilityNodeInfo findFocus(int focus) {
        return AccessibilityInteractionClient.getInstance(this).findFocus(this.mConnectionId, -2, AccessibilityNodeInfo.ROOT_NODE_ID, focus);
    }

    public final AccessibilityServiceInfo getServiceInfo() {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.getServiceInfo();
            } catch (RemoteException re) {
                Log.m103w("AccessibilityService", "Error while getting AccessibilityServiceInfo", re);
                re.rethrowFromSystemServer();
                return null;
            }
        }
        return null;
    }

    public final void setServiceInfo(AccessibilityServiceInfo info) {
        this.mInfo = info;
        updateInputMethod(info);
        this.mMotionEventSources = info.getMotionEventSources();
        sendServiceInfo();
    }

    private void sendServiceInfo() {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        AccessibilityServiceInfo accessibilityServiceInfo = this.mInfo;
        if (accessibilityServiceInfo != null && connection != null) {
            if (!accessibilityServiceInfo.isWithinParcelableSize()) {
                throw new IllegalStateException("Cannot update service info: size is larger than safe parcelable limits.");
            }
            try {
                connection.setServiceInfo(this.mInfo);
                this.mInfo = null;
                AccessibilityInteractionClient.getInstance(this).clearCache(this.mConnectionId);
            } catch (RemoteException re) {
                Log.m103w("AccessibilityService", "Error while setting AccessibilityServiceInfo", re);
                re.rethrowFromSystemServer();
            }
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            throw new IllegalStateException("System services not available to Activities before onCreate()");
        }
        if (Context.WINDOW_SERVICE.equals(name)) {
            if (this.mWindowManager == null) {
                WindowManager windowManager = (WindowManager) getBaseContext().getSystemService(name);
                this.mWindowManager = windowManager;
                WindowManagerImpl wm = (WindowManagerImpl) windowManager;
                wm.setDefaultToken(this.mWindowToken);
            }
            return this.mWindowManager;
        }
        return super.getSystemService(name);
    }

    public void takeScreenshot(int displayId, final Executor executor, final TakeScreenshotCallback callback) {
        Preconditions.checkNotNull(executor, "executor cannot be null");
        Preconditions.checkNotNull(callback, "callback cannot be null");
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection == null) {
            sendScreenshotFailure(1, executor, callback);
            return;
        }
        try {
            connection.takeScreenshot(displayId, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.accessibilityservice.AccessibilityService$$ExternalSyntheticLambda2
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    AccessibilityService.this.lambda$takeScreenshot$0(executor, callback, bundle);
                }
            }));
        } catch (RemoteException re) {
            throw new RuntimeException(re);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$takeScreenshot$0(Executor executor, TakeScreenshotCallback callback, Bundle result) {
        int status = result.getInt(KEY_ACCESSIBILITY_SCREENSHOT_STATUS);
        if (status != 0) {
            sendScreenshotFailure(status, executor, callback);
            return;
        }
        HardwareBuffer hardwareBuffer = (HardwareBuffer) result.getParcelable(KEY_ACCESSIBILITY_SCREENSHOT_HARDWAREBUFFER, HardwareBuffer.class);
        ParcelableColorSpace colorSpace = (ParcelableColorSpace) result.getParcelable(KEY_ACCESSIBILITY_SCREENSHOT_COLORSPACE, ParcelableColorSpace.class);
        ScreenshotResult screenshot = new ScreenshotResult(hardwareBuffer, colorSpace.getColorSpace(), result.getLong(KEY_ACCESSIBILITY_SCREENSHOT_TIMESTAMP));
        sendScreenshotSuccess(screenshot, executor, callback);
    }

    public void takeScreenshotOfWindow(int accessibilityWindowId, Executor executor, TakeScreenshotCallback callback) {
        AccessibilityInteractionClient.getInstance(this).takeScreenshotOfWindow(this.mConnectionId, accessibilityWindowId, executor, callback);
    }

    public void setAccessibilityFocusAppearance(int strokeWidth, int color) {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.setFocusAppearance(strokeWidth, color);
            } catch (RemoteException re) {
                Log.m103w("AccessibilityService", "Error while setting the strokeWidth and color of the accessibility focus rectangle", re);
                re.rethrowFromSystemServer();
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IAccessibilityServiceClientWrapper(this, getMainExecutor(), new Callbacks() { // from class: android.accessibilityservice.AccessibilityService.2
            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onServiceConnected() {
                AccessibilityService.this.dispatchServiceConnected();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onInterrupt() {
                AccessibilityService.this.onInterrupt();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onAccessibilityEvent(AccessibilityEvent event) {
                AccessibilityService.this.onAccessibilityEvent(event);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void init(int connectionId, IBinder windowToken) {
                AccessibilityService.this.mConnectionId = connectionId;
                AccessibilityService.this.mWindowToken = windowToken;
                if (AccessibilityService.this.mWindowManager != null) {
                    WindowManagerImpl wm = (WindowManagerImpl) AccessibilityService.this.mWindowManager;
                    wm.setDefaultToken(AccessibilityService.this.mWindowToken);
                }
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onGesture(AccessibilityGestureEvent gestureEvent) {
                return AccessibilityService.this.onGesture(gestureEvent);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public boolean onKeyEvent(KeyEvent event) {
                return AccessibilityService.this.onKeyEvent(event);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onMagnificationChanged(int displayId, Region region, MagnificationConfig config) {
                AccessibilityService.this.onMagnificationChanged(displayId, region, config);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onMotionEvent(MotionEvent event) {
                AccessibilityService.this.sendMotionEventToCallback(event);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onTouchStateChanged(int displayId, int state) {
                AccessibilityService.this.onTouchStateChanged(displayId, state);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onSoftKeyboardShowModeChanged(int showMode) {
                AccessibilityService.this.onSoftKeyboardShowModeChanged(showMode);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onPerformGestureResult(int sequence, boolean completedSuccessfully) {
                AccessibilityService.this.onPerformGestureResult(sequence, completedSuccessfully);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onFingerprintCapturingGesturesChanged(boolean active) {
                AccessibilityService.this.onFingerprintCapturingGesturesChanged(active);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onFingerprintGesture(int gesture) {
                AccessibilityService.this.onFingerprintGesture(gesture);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onAccessibilityButtonClicked(int displayId) {
                AccessibilityService.this.onAccessibilityButtonClicked(displayId);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onAccessibilityButtonAvailabilityChanged(boolean available) {
                AccessibilityService.this.onAccessibilityButtonAvailabilityChanged(available);
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void onSystemActionsChanged() {
                AccessibilityService.this.onSystemActionsChanged();
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void createImeSession(IAccessibilityInputMethodSessionCallback callback) {
                if (AccessibilityService.this.mInputMethod != null) {
                    AccessibilityService.this.mInputMethod.createImeSession(callback);
                }
            }

            @Override // android.accessibilityservice.AccessibilityService.Callbacks
            public void startInput(RemoteAccessibilityInputConnection connection, EditorInfo editorInfo, boolean restarting) {
                if (AccessibilityService.this.mInputMethod != null) {
                    if (restarting) {
                        AccessibilityService.this.mInputMethod.restartInput(connection, editorInfo);
                    } else {
                        AccessibilityService.this.mInputMethod.startInput(connection, editorInfo);
                    }
                }
            }
        });
    }

    /* loaded from: classes.dex */
    public static class IAccessibilityServiceClientWrapper extends IAccessibilityServiceClient.Stub {
        private final Callbacks mCallback;
        private final Context mContext;
        private final Executor mExecutor;
        private int mConnectionId = -1;
        CancellationGroup mCancellationGroup = null;

        public IAccessibilityServiceClientWrapper(Context context, Executor executor, Callbacks callback) {
            this.mCallback = callback;
            this.mContext = context;
            this.mExecutor = executor;
        }

        public IAccessibilityServiceClientWrapper(Context context, Looper looper, Callbacks callback) {
            this.mCallback = callback;
            this.mContext = context;
            this.mExecutor = new HandlerExecutor(new Handler(looper));
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void init(final IAccessibilityServiceConnection connection, final int connectionId, final IBinder windowToken) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$init$0(connectionId, connection, windowToken);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$init$0(int connectionId, IAccessibilityServiceConnection connection, IBinder windowToken) {
            this.mConnectionId = connectionId;
            if (connection != null) {
                AccessibilityInteractionClient.getInstance(this.mContext);
                AccessibilityInteractionClient.addConnection(this.mConnectionId, connection, true);
                Context context = this.mContext;
                if (context != null) {
                    try {
                        connection.setAttributionTag(context.getAttributionTag());
                    } catch (RemoteException re) {
                        Log.m103w("AccessibilityService", "Error while setting attributionTag", re);
                        re.rethrowFromSystemServer();
                    }
                }
                this.mCallback.init(this.mConnectionId, windowToken);
                this.mCallback.onServiceConnected();
                return;
            }
            AccessibilityInteractionClient.getInstance(this.mContext).clearCache(this.mConnectionId);
            AccessibilityInteractionClient.getInstance(this.mContext);
            AccessibilityInteractionClient.removeConnection(this.mConnectionId);
            this.mConnectionId = -1;
            this.mCallback.init(-1, null);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onInterrupt() {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onInterrupt$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterrupt$1() {
            if (this.mConnectionId != -1) {
                this.mCallback.onInterrupt();
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityEvent(final AccessibilityEvent event, final boolean serviceWantsEvent) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onAccessibilityEvent$2(event, serviceWantsEvent);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAccessibilityEvent$2(AccessibilityEvent event, boolean serviceWantsEvent) {
            if (event != null) {
                AccessibilityInteractionClient.getInstance(this.mContext).onAccessibilityEvent(event, this.mConnectionId);
                if (serviceWantsEvent && this.mConnectionId != -1) {
                    this.mCallback.onAccessibilityEvent(event);
                }
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onGesture(final AccessibilityGestureEvent gestureInfo) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onGesture$3(gestureInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onGesture$3(AccessibilityGestureEvent gestureInfo) {
            if (this.mConnectionId != -1) {
                this.mCallback.onGesture(gestureInfo);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void clearAccessibilityCache() {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$clearAccessibilityCache$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$clearAccessibilityCache$4() {
            AccessibilityInteractionClient.getInstance(this.mContext).clearCache(this.mConnectionId);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onKeyEvent(final KeyEvent event, final int sequence) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onKeyEvent$5(event, sequence);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onKeyEvent$5(KeyEvent event, int sequence) {
            try {
                AccessibilityInteractionClient.getInstance(this.mContext);
                IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
                if (connection != null) {
                    boolean result = this.mCallback.onKeyEvent(event);
                    try {
                        connection.setOnKeyEventResult(result, sequence);
                    } catch (RemoteException e) {
                    }
                }
                try {
                    event.recycle();
                } catch (IllegalStateException e2) {
                }
            } catch (Throwable th) {
                try {
                    event.recycle();
                } catch (IllegalStateException e3) {
                }
                throw th;
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onMagnificationChanged(final int displayId, final Region region, final MagnificationConfig config) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onMagnificationChanged$6(displayId, region, config);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMagnificationChanged$6(int displayId, Region region, MagnificationConfig config) {
            if (this.mConnectionId != -1) {
                this.mCallback.onMagnificationChanged(displayId, region, config);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onSoftKeyboardShowModeChanged(final int showMode) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onSoftKeyboardShowModeChanged$7(showMode);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSoftKeyboardShowModeChanged$7(int showMode) {
            if (this.mConnectionId != -1) {
                this.mCallback.onSoftKeyboardShowModeChanged(showMode);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onPerformGestureResult(final int sequence, final boolean successfully) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onPerformGestureResult$8(sequence, successfully);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPerformGestureResult$8(int sequence, boolean successfully) {
            if (this.mConnectionId != -1) {
                this.mCallback.onPerformGestureResult(sequence, successfully);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onFingerprintCapturingGesturesChanged(final boolean active) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onFingerprintCapturingGesturesChanged$9(active);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFingerprintCapturingGesturesChanged$9(boolean active) {
            if (this.mConnectionId != -1) {
                this.mCallback.onFingerprintCapturingGesturesChanged(active);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onFingerprintGesture(final int gesture) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onFingerprintGesture$10(gesture);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFingerprintGesture$10(int gesture) {
            if (this.mConnectionId != -1) {
                this.mCallback.onFingerprintGesture(gesture);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityButtonClicked(final int displayId) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onAccessibilityButtonClicked$11(displayId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAccessibilityButtonClicked$11(int displayId) {
            if (this.mConnectionId != -1) {
                this.mCallback.onAccessibilityButtonClicked(displayId);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onAccessibilityButtonAvailabilityChanged(final boolean available) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onAccessibilityButtonAvailabilityChanged$12(available);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAccessibilityButtonAvailabilityChanged$12(boolean available) {
            if (this.mConnectionId != -1) {
                this.mCallback.onAccessibilityButtonAvailabilityChanged(available);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onSystemActionsChanged() {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onSystemActionsChanged$13();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemActionsChanged$13() {
            if (this.mConnectionId != -1) {
                this.mCallback.onSystemActionsChanged();
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void createImeSession(final IAccessibilityInputMethodSessionCallback callback) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$createImeSession$14(callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$createImeSession$14(IAccessibilityInputMethodSessionCallback callback) {
            if (this.mConnectionId != -1) {
                this.mCallback.createImeSession(callback);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void setImeSessionEnabled(IAccessibilityInputMethodSession session, final boolean enabled) {
            try {
                final AccessibilityInputMethodSession ls = ((AccessibilityInputMethodSessionWrapper) session).getSession();
                if (ls == null) {
                    Log.m104w("AccessibilityService", "Session is already finished: " + session);
                } else {
                    this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$setImeSessionEnabled$15(ls, enabled);
                        }
                    });
                }
            } catch (ClassCastException e) {
                Log.m103w("AccessibilityService", "Incoming session not of correct type: " + session, e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setImeSessionEnabled$15(AccessibilityInputMethodSession ls, boolean enabled) {
            if (this.mConnectionId != -1) {
                ls.setEnabled(enabled);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void bindInput() {
            if (this.mCancellationGroup != null) {
                Log.m110e("AccessibilityService", "bindInput must be paired with unbindInput.");
            }
            this.mCancellationGroup = new CancellationGroup();
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void unbindInput() {
            CancellationGroup cancellationGroup = this.mCancellationGroup;
            if (cancellationGroup != null) {
                cancellationGroup.cancelAll();
                this.mCancellationGroup = null;
                return;
            }
            Log.m110e("AccessibilityService", "unbindInput must be paired with bindInput.");
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void startInput(final IRemoteAccessibilityInputConnection connection, final EditorInfo editorInfo, final boolean restarting) {
            if (this.mCancellationGroup == null) {
                Log.m110e("AccessibilityService", "startInput must be called after bindInput.");
                this.mCancellationGroup = new CancellationGroup();
            }
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$startInput$16(connection, editorInfo, restarting);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startInput$16(IRemoteAccessibilityInputConnection connection, EditorInfo editorInfo, boolean restarting) {
            if (this.mConnectionId != -1) {
                RemoteAccessibilityInputConnection ic = connection == null ? null : new RemoteAccessibilityInputConnection(connection, this.mCancellationGroup);
                editorInfo.makeCompatible(this.mContext.getApplicationInfo().targetSdkVersion);
                this.mCallback.startInput(ic, editorInfo, restarting);
            }
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onMotionEvent(final MotionEvent event) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onMotionEvent$17(event);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMotionEvent$17(MotionEvent event) {
            this.mCallback.onMotionEvent(event);
        }

        @Override // android.accessibilityservice.IAccessibilityServiceClient
        public void onTouchStateChanged(final int displayId, final int state) {
            this.mExecutor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$IAccessibilityServiceClientWrapper$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityService.IAccessibilityServiceClientWrapper.this.lambda$onTouchStateChanged$18(displayId, state);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchStateChanged$18(int displayId, int state) {
            this.mCallback.onTouchStateChanged(displayId, state);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class GestureResultCallback {
        public void onCompleted(GestureDescription gestureDescription) {
        }

        public void onCancelled(GestureDescription gestureDescription) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GestureResultCallbackInfo {
        GestureResultCallback callback;
        GestureDescription gestureDescription;
        Handler handler;

        GestureResultCallbackInfo(GestureDescription gestureDescription, GestureResultCallback callback, Handler handler) {
            this.gestureDescription = gestureDescription;
            this.callback = callback;
            this.handler = handler;
        }
    }

    private void sendScreenshotSuccess(final ScreenshotResult screenshot, Executor executor, final TakeScreenshotCallback callback) {
        executor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityService.TakeScreenshotCallback.this.onSuccess(screenshot);
            }
        });
    }

    private void sendScreenshotFailure(final int errorCode, Executor executor, final TakeScreenshotCallback callback) {
        executor.execute(new Runnable() { // from class: android.accessibilityservice.AccessibilityService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityService.TakeScreenshotCallback.this.onFailure(errorCode);
            }
        });
    }

    /* loaded from: classes.dex */
    public static final class ScreenshotResult {
        private final ColorSpace mColorSpace;
        private final HardwareBuffer mHardwareBuffer;
        private final long mTimestamp;

        public ScreenshotResult(HardwareBuffer hardwareBuffer, ColorSpace colorSpace, long timestamp) {
            Preconditions.checkNotNull(hardwareBuffer, "hardwareBuffer cannot be null");
            Preconditions.checkNotNull(colorSpace, "colorSpace cannot be null");
            this.mHardwareBuffer = hardwareBuffer;
            this.mColorSpace = colorSpace;
            this.mTimestamp = timestamp;
        }

        public ColorSpace getColorSpace() {
            return this.mColorSpace;
        }

        public HardwareBuffer getHardwareBuffer() {
            return this.mHardwareBuffer;
        }

        public long getTimestamp() {
            return this.mTimestamp;
        }
    }

    public void setGestureDetectionPassthroughRegion(int displayId, Region region) {
        Preconditions.checkNotNull(region, "region cannot be null");
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.setGestureDetectionPassthroughRegion(displayId, region);
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void setTouchExplorationPassthroughRegion(int displayId, Region region) {
        Preconditions.checkNotNull(region, "region cannot be null");
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.setTouchExplorationPassthroughRegion(displayId, region);
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    public void setAnimationScale(float scale) {
        AccessibilityInteractionClient.getInstance(this);
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.setAnimationScale(scale);
            } catch (RemoteException re) {
                throw new RuntimeException(re);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class AccessibilityContext extends ContextWrapper {
        private final int mConnectionId;

        private AccessibilityContext(Context base, int connectionId) {
            super(base);
            this.mConnectionId = connectionId;
            setDefaultTokenInternal(this, getDisplayId());
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Context createDisplayContext(Display display) {
            return new AccessibilityContext(super.createDisplayContext(display), this.mConnectionId);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Context createWindowContext(int type, Bundle options) {
            Context context = super.createWindowContext(type, options);
            if (type != 2032) {
                return context;
            }
            return new AccessibilityContext(context, this.mConnectionId);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Context createWindowContext(Display display, int type, Bundle options) {
            Context context = super.createWindowContext(display, type, options);
            if (type != 2032) {
                return context;
            }
            return new AccessibilityContext(context, this.mConnectionId);
        }

        private void setDefaultTokenInternal(Context context, int displayId) {
            WindowManagerImpl wm = (WindowManagerImpl) context.getSystemService(Context.WINDOW_SERVICE);
            IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
            IBinder token = null;
            if (connection != null) {
                try {
                    token = connection.getOverlayWindowToken(displayId);
                } catch (RemoteException re) {
                    Log.m103w("AccessibilityService", "Failed to get window token", re);
                    re.rethrowFromSystemServer();
                }
                wm.setDefaultToken(token);
            }
        }
    }

    public final TouchInteractionController getTouchInteractionController(int displayId) {
        TouchInteractionController controller;
        synchronized (this.mLock) {
            controller = this.mTouchInteractionControllers.get(displayId);
            if (controller == null) {
                controller = new TouchInteractionController(this, this.mLock, displayId);
                this.mTouchInteractionControllers.put(displayId, controller);
            }
        }
        return controller;
    }

    void sendMotionEventToCallback(MotionEvent event) {
        TouchInteractionController controller;
        boolean sendingTouchEventToTouchInteractionController = false;
        if (event.isFromSource(4098)) {
            synchronized (this.mLock) {
                int displayId = event.getDisplayId();
                controller = this.mTouchInteractionControllers.get(displayId);
            }
            if (controller != null) {
                sendingTouchEventToTouchInteractionController = true;
                controller.onMotionEvent(event);
            }
        }
        int eventSourceWithoutClass = event.getSource() & (-256);
        if ((this.mMotionEventSources & eventSourceWithoutClass) != 0 && !sendingTouchEventToTouchInteractionController) {
            onMotionEvent(event);
        }
    }

    void onTouchStateChanged(int displayId, int state) {
        TouchInteractionController controller;
        synchronized (this.mLock) {
            controller = this.mTouchInteractionControllers.get(displayId);
        }
        if (controller != null) {
            controller.onStateChanged(state);
        }
    }

    public void attachAccessibilityOverlayToDisplay(int displayId, SurfaceControl sc) {
        Preconditions.checkNotNull(sc, "SurfaceControl cannot be null");
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection == null) {
            return;
        }
        try {
            connection.attachAccessibilityOverlayToDisplay(displayId, sc);
        } catch (RemoteException re) {
            re.rethrowFromSystemServer();
        }
    }

    public void attachAccessibilityOverlayToWindow(int accessibilityWindowId, SurfaceControl sc) {
        Preconditions.checkNotNull(sc, "SurfaceControl cannot be null");
        AccessibilityInteractionClient.getInstance(this).attachAccessibilityOverlayToWindow(this.mConnectionId, accessibilityWindowId, sc);
    }
}
