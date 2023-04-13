package com.android.server.policy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.IUiModeManager;
import android.app.NotificationManager;
import android.app.ProfilerInfo;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.hdmi.HdmiAudioSystemClient;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.media.AudioManagerInternal;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeviceIdleManager;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager;
import android.service.vr.IPersistentVrStateCallbacks;
import android.telecom.TelecomManager;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.MathUtils;
import android.util.MutableBoolean;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.IDisplayFoldListener;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.autofill.AutofillManagerInternal;
import android.widget.Toast;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.accessibility.util.AccessibilityUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.KeyInterceptionInfo;
import com.android.internal.policy.LogDecelerateInterpolator;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.AccessibilityManagerInternal;
import com.android.server.ExtconStateObserver;
import com.android.server.ExtconUEventObserver;
import com.android.server.GestureLauncherService;
import com.android.server.LocalServices;
import com.android.server.SystemServiceManager;
import com.android.server.UiThread;
import com.android.server.display.BrightnessUtils;
import com.android.server.input.InputManagerInternal;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p013vr.VrManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.DisplayPolicy;
import com.android.server.p014wm.DisplayRotation;
import com.android.server.p014wm.StartingSurfaceController;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.policy.KeyCombinationManager;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.policy.SingleKeyGestureDetector;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;
import com.android.server.policy.keyguard.KeyguardStateMonitor;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class PhoneWindowManager implements WindowManagerPolicy {
    public AccessibilityManager mAccessibilityManager;
    public AccessibilityManagerInternal mAccessibilityManagerInternal;
    public AccessibilityShortcutController mAccessibilityShortcutController;
    public ActivityManagerInternal mActivityManagerInternal;
    public ActivityTaskManagerInternal mActivityTaskManagerInternal;
    public boolean mAllowStartActivityForLongPressOnPowerDuringSetup;
    public boolean mAllowTheaterModeWakeFromCameraLens;
    public boolean mAllowTheaterModeWakeFromKey;
    public boolean mAllowTheaterModeWakeFromLidSwitch;
    public boolean mAllowTheaterModeWakeFromMotion;
    public boolean mAllowTheaterModeWakeFromMotionWhenNotDreaming;
    public boolean mAllowTheaterModeWakeFromPowerKey;
    public boolean mAllowTheaterModeWakeFromWakeGesture;
    public AppOpsManager mAppOpsManager;
    public AudioManagerInternal mAudioManagerInternal;
    public AutofillManagerInternal mAutofillManagerInternal;
    public volatile boolean mBackKeyHandled;
    public volatile boolean mBootAnimationDismissable;
    public boolean mBootMessageNeedsHiding;
    public PowerManager.WakeLock mBroadcastWakeLock;
    public BurnInProtectionHelper mBurnInProtectionHelper;
    public volatile boolean mCameraGestureTriggered;
    public volatile boolean mCameraGestureTriggeredDuringGoingToSleep;
    public Intent mCarDockIntent;
    public Context mContext;
    public int mCurrentUserId;
    public Display mDefaultDisplay;
    public DisplayPolicy mDefaultDisplayPolicy;
    public DisplayRotation mDefaultDisplayRotation;
    public Intent mDeskDockIntent;
    public volatile boolean mDeviceGoingToSleep;
    public volatile boolean mDismissImeOnBackKeyPressed;
    public DisplayFoldController mDisplayFoldController;
    public DisplayManager mDisplayManager;
    public DisplayManagerInternal mDisplayManagerInternal;
    public int mDoublePressOnPowerBehavior;
    public int mDoublePressOnStemPrimaryBehavior;
    public int mDoubleTapOnHomeBehavior;
    public DreamManagerInternal mDreamManagerInternal;
    public volatile boolean mEndCallKeyHandled;
    public int mEndcallBehavior;
    public GestureLauncherService mGestureLauncherService;
    public GlobalActions mGlobalActions;
    public Supplier<GlobalActions> mGlobalActionsFactory;
    public GlobalKeyManager mGlobalKeyManager;
    public boolean mGoToSleepOnButtonPressTheaterMode;
    public boolean mHandleVolumeKeysInWM;
    public Handler mHandler;
    public boolean mHapticTextHandleEnabled;
    public boolean mHasFeatureAuto;
    public boolean mHasFeatureHdmiCec;
    public boolean mHasFeatureLeanback;
    public boolean mHasFeatureWatch;
    public boolean mHaveBuiltInKeyboard;
    public boolean mHavePendingMediaKeyRepeatWithWakeLock;
    public HdmiControl mHdmiControl;
    public Intent mHomeIntent;
    public int mIncallBackBehavior;
    public int mIncallPowerBehavior;
    public InputManagerInternal mInputManagerInternal;
    public KeyCombinationManager mKeyCombinationManager;
    public boolean mKeyguardBound;
    public KeyguardServiceDelegate mKeyguardDelegate;
    public boolean mKeyguardDrawnOnce;
    public boolean mKeyguardOccludedChanged;
    public int mLidKeyboardAccessibility;
    public int mLidNavigationAccessibility;
    public boolean mLockAfterAppTransitionFinished;
    public LockPatternUtils mLockPatternUtils;
    public int mLockScreenTimeout;
    public boolean mLockScreenTimerActive;
    public MetricsLogger mLogger;
    public int mLongPressOnBackBehavior;
    public int mLongPressOnHomeBehavior;
    public long mLongPressOnPowerAssistantTimeoutMs;
    public int mLongPressOnPowerBehavior;
    public int mLongPressOnStemPrimaryBehavior;
    public ModifierShortcutManager mModifierShortcutManager;
    public PackageManager mPackageManager;
    public boolean mPendingCapsLockToggle;
    public boolean mPendingKeyguardOccluded;
    public boolean mPendingMetaAction;
    public volatile boolean mPictureInPictureVisible;
    public ComponentName mPowerDoublePressTargetActivity;
    public volatile boolean mPowerKeyHandled;
    public PowerManager.WakeLock mPowerKeyWakeLock;
    public PowerManager mPowerManager;
    public PowerManagerInternal mPowerManagerInternal;
    public int mPowerVolUpBehavior;
    public boolean mPreloadedRecentApps;
    public int mRecentAppsHeldModifiers;
    public volatile boolean mRecentsVisible;
    public volatile boolean mRequestedOrSleepingDefaultDisplay;
    public boolean mSafeMode;
    public long[] mSafeModeEnabledVibePattern;
    public ActivityTaskManagerInternal.SleepTokenAcquirer mScreenOffSleepTokenAcquirer;
    public int mSearchKeyBehavior;
    public ComponentName mSearchKeyTargetActivity;
    public SensorPrivacyManager mSensorPrivacyManager;
    public SettingsObserver mSettingsObserver;
    public int mShortPressOnPowerBehavior;
    public int mShortPressOnSleepBehavior;
    public int mShortPressOnStemPrimaryBehavior;
    public int mShortPressOnWindowBehavior;
    public SideFpsEventHandler mSideFpsEventHandler;
    public SingleKeyGestureDetector mSingleKeyGestureDetector;
    public StatusBarManagerInternal mStatusBarManagerInternal;
    public IStatusBarService mStatusBarService;
    public boolean mSupportLongPressPowerWhenNonInteractive;
    public boolean mSystemBooted;
    public boolean mSystemNavigationKeysEnabled;
    public boolean mSystemReady;
    public int mTriplePressOnPowerBehavior;
    public int mTriplePressOnStemPrimaryBehavior;
    public int mUiMode;
    public IUiModeManager mUiModeManager;
    public boolean mUseTvRouting;
    public int mVeryLongPressOnPowerBehavior;
    public Vibrator mVibrator;
    public Intent mVrHeadsetHomeIntent;
    public volatile VrManagerInternal mVrManagerInternal;
    public boolean mWakeGestureEnabledSetting;
    public MyWakeGestureListener mWakeGestureListener;
    public boolean mWakeOnAssistKeyPress;
    public boolean mWakeOnBackKeyPress;
    public boolean mWakeOnDpadKeyPress;
    public long mWakeUpToLastStateTimeout;
    public WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
    public WindowManagerInternal mWindowManagerInternal;
    public static final VibrationAttributes TOUCH_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(18);
    public static final VibrationAttributes PHYSICAL_EMULATION_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(34);
    public static final VibrationAttributes HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(50);
    public static final int[] WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = {2003, 2010};
    public final Object mLock = new Object();
    public final SparseArray<WindowManagerPolicy.ScreenOnListener> mScreenOnListeners = new SparseArray<>();
    public final Object mServiceAcquireLock = new Object();
    public boolean mEnableShiftMenuBugReports = false;
    public boolean mEnableCarDockHomeCapture = true;
    public final KeyguardServiceDelegate.DrawnListener mKeyguardDrawnCallback = new KeyguardServiceDelegate.DrawnListener() { // from class: com.android.server.policy.PhoneWindowManager.1
        @Override // com.android.server.policy.keyguard.KeyguardServiceDelegate.DrawnListener
        public void onDrawn() {
            PhoneWindowManager.this.mHandler.sendEmptyMessage(5);
        }
    };
    public volatile boolean mNavBarVirtualKeyHapticFeedbackEnabled = true;
    public volatile int mPendingWakeKey = -1;
    public int mCameraLensCoverState = -1;
    public boolean mStylusButtonsEnabled = true;
    public boolean mHasSoftInput = false;
    public HashSet<Integer> mAllowLockscreenWhenOnDisplays = new HashSet<>();
    public int mRingerToggleChord = 0;
    public final SparseArray<KeyCharacterMap.FallbackAction> mFallbackActions = new SparseArray<>();
    public final LogDecelerateInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
    public volatile int mTopFocusedDisplayId = -1;
    public int mPowerButtonSuppressionDelayMillis = 800;
    public boolean mLockNowPending = false;
    public UEventObserver mHDMIObserver = new UEventObserver() { // from class: com.android.server.policy.PhoneWindowManager.2
        public void onUEvent(UEventObserver.UEvent uEvent) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setHdmiPlugged("1".equals(uEvent.get("SWITCH_STATE")));
        }
    };
    public final IPersistentVrStateCallbacks mPersistentVrModeListener = new IPersistentVrStateCallbacks.Stub() { // from class: com.android.server.policy.PhoneWindowManager.3
        public void onPersistentVrStateChanged(boolean z) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setPersistentVrModeEnabled(z);
        }
    };
    public final Runnable mEndCallLongPress = new Runnable() { // from class: com.android.server.policy.PhoneWindowManager.4
        @Override // java.lang.Runnable
        public void run() {
            PhoneWindowManager.this.mEndCallKeyHandled = true;
            PhoneWindowManager.this.performHapticFeedback(0, false, "End Call - Long Press - Show Global Actions");
            PhoneWindowManager.this.showGlobalActionsInternal();
        }
    };
    public final SparseArray<DisplayHomeButtonHandler> mDisplayHomeButtonHandlers = new SparseArray<>();
    public BroadcastReceiver mDockReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.PhoneWindowManager.14
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DOCK_EVENT".equals(intent.getAction())) {
                PhoneWindowManager.this.mDefaultDisplayPolicy.setDockMode(intent.getIntExtra("android.intent.extra.DOCK_STATE", 0));
            } else {
                try {
                    IUiModeManager asInterface = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
                    PhoneWindowManager.this.mUiMode = asInterface.getCurrentModeType();
                } catch (RemoteException unused) {
                }
            }
            PhoneWindowManager.this.updateRotation(true);
            PhoneWindowManager.this.mDefaultDisplayRotation.updateOrientationListener();
        }
    };
    public BroadcastReceiver mDreamReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.PhoneWindowManager.15
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DREAMING_STARTED".equals(intent.getAction())) {
                if (PhoneWindowManager.this.mKeyguardDelegate != null) {
                    PhoneWindowManager.this.mKeyguardDelegate.onDreamingStarted();
                }
            } else if (!"android.intent.action.DREAMING_STOPPED".equals(intent.getAction()) || PhoneWindowManager.this.mKeyguardDelegate == null) {
            } else {
                PhoneWindowManager.this.mKeyguardDelegate.onDreamingStopped();
            }
        }
    };
    public BroadcastReceiver mMultiuserReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.PhoneWindowManager.16
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                PhoneWindowManager.this.mSettingsObserver.onChange(false);
                PhoneWindowManager.this.mDefaultDisplayRotation.onUserSwitch();
                PhoneWindowManager.this.mWindowManagerFuncs.onUserSwitched();
            }
        }
    };
    public ProgressDialog mBootMsgDialog = null;
    public final ScreenLockTimeout mScreenLockTimeout = new ScreenLockTimeout();

    public static String incallBackBehaviorToString(int i) {
        return (i & 1) != 0 ? "hangup" : "<nothing>";
    }

    public static String incallPowerBehaviorToString(int i) {
        return (i & 2) != 0 ? "hangup" : "sleep";
    }

    public static boolean isValidGlobalKey(int i) {
        return (i == 26 || i == 223 || i == 224) ? false : true;
    }

    /* loaded from: classes2.dex */
    public class PolicyHandler extends Handler {
        public PolicyHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 3:
                    PhoneWindowManager.this.dispatchMediaKeyWithWakeLock((KeyEvent) message.obj);
                    return;
                case 4:
                    PhoneWindowManager.this.dispatchMediaKeyRepeatWithWakeLock((KeyEvent) message.obj);
                    return;
                case 5:
                    PhoneWindowManager.this.finishKeyguardDrawn();
                    return;
                case 6:
                    Slog.w(StartingSurfaceController.TAG, "Keyguard drawn timeout. Setting mKeyguardDrawComplete");
                    PhoneWindowManager.this.finishKeyguardDrawn();
                    return;
                case 7:
                    PhoneWindowManager.this.finishWindowsDrawn(message.arg1);
                    return;
                case 8:
                case 13:
                case 14:
                default:
                    return;
                case 9:
                    PhoneWindowManager.this.showRecentApps(false);
                    return;
                case 10:
                    PhoneWindowManager.this.showGlobalActionsInternal();
                    return;
                case 11:
                    PhoneWindowManager.this.handleHideBootMessage();
                    return;
                case 12:
                    PhoneWindowManager.this.launchVoiceAssistWithWakeLock();
                    return;
                case 15:
                    PhoneWindowManager.this.showPictureInPictureMenuInternal();
                    return;
                case 16:
                    PhoneWindowManager.this.handleScreenShot(message.arg1);
                    return;
                case 17:
                    PhoneWindowManager.this.accessibilityShortcutActivated();
                    return;
                case 18:
                    PhoneWindowManager.this.requestBugreportForTv();
                    return;
                case 19:
                    if (PhoneWindowManager.this.mAccessibilityShortcutController.isAccessibilityShortcutAvailable(false)) {
                        PhoneWindowManager.this.accessibilityShortcutActivated();
                        return;
                    }
                    return;
                case 20:
                    PhoneWindowManager.this.mAutofillManagerInternal.onBackKeyPressed();
                    return;
                case 21:
                    PhoneWindowManager.this.sendSystemKeyToStatusBar(message.arg1);
                    return;
                case 22:
                    PhoneWindowManager.this.launchAllAppsAction();
                    return;
                case 23:
                    PhoneWindowManager.this.launchAssistAction(null, message.arg1, ((Long) message.obj).longValue(), 0);
                    return;
                case 24:
                    PhoneWindowManager.this.handleRingerChordGesture();
                    return;
                case 25:
                    PhoneWindowManager.this.handleSwitchKeyboardLayout(message.arg1, message.arg2);
                    return;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void observe() {
            ContentResolver contentResolver = PhoneWindowManager.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(Settings.System.getUriFor("end_button_behavior"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("incall_power_button_behavior"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("incall_back_button_behavior"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("wake_gesture_enabled"), false, this, -1);
            contentResolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("volume_hush_gesture"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("system_navigation_keys_enabled"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("power_button_long_press"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("power_button_long_press_duration_ms"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("power_button_very_long_press"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("key_chord_power_volume_up"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("power_button_suppression_delay_after_gesture_wake"), false, this, -1);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("stylus_buttons_enabled"), false, this, -1);
            PhoneWindowManager.this.updateSettings();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            PhoneWindowManager.this.updateSettings();
            PhoneWindowManager.this.updateRotation(false);
        }
    }

    /* loaded from: classes2.dex */
    public class MyWakeGestureListener extends WakeGestureListener {
        public MyWakeGestureListener(Context context, Handler handler) {
            super(context, handler);
        }

        @Override // com.android.server.policy.WakeGestureListener
        public void onWakeUp() {
            synchronized (PhoneWindowManager.this.mLock) {
                if (PhoneWindowManager.this.shouldEnableWakeGestureLp()) {
                    PhoneWindowManager.this.performHapticFeedback(1, false, "Wake Up");
                    PhoneWindowManager.this.wakeUp(SystemClock.uptimeMillis(), PhoneWindowManager.this.mAllowTheaterModeWakeFromWakeGesture, 4, "android.policy:GESTURE");
                }
            }
        }
    }

    public final void handleRingerChordGesture() {
        if (this.mRingerToggleChord == 0) {
            return;
        }
        getAudioManagerInternal();
        this.mAudioManagerInternal.silenceRingerModeInternal("volume_hush");
        Settings.Secure.putInt(this.mContext.getContentResolver(), "hush_gesture_used", 1);
        this.mLogger.action(1440, this.mRingerToggleChord);
    }

    public IStatusBarService getStatusBarService() {
        IStatusBarService iStatusBarService;
        synchronized (this.mServiceAcquireLock) {
            if (this.mStatusBarService == null) {
                this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
            }
            iStatusBarService = this.mStatusBarService;
        }
        return iStatusBarService;
    }

    public StatusBarManagerInternal getStatusBarManagerInternal() {
        StatusBarManagerInternal statusBarManagerInternal;
        synchronized (this.mServiceAcquireLock) {
            if (this.mStatusBarManagerInternal == null) {
                this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            }
            statusBarManagerInternal = this.mStatusBarManagerInternal;
        }
        return statusBarManagerInternal;
    }

    public AudioManagerInternal getAudioManagerInternal() {
        AudioManagerInternal audioManagerInternal;
        synchronized (this.mServiceAcquireLock) {
            if (this.mAudioManagerInternal == null) {
                this.mAudioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
            }
            audioManagerInternal = this.mAudioManagerInternal;
        }
        return audioManagerInternal;
    }

    public AccessibilityManagerInternal getAccessibilityManagerInternal() {
        AccessibilityManagerInternal accessibilityManagerInternal;
        synchronized (this.mServiceAcquireLock) {
            if (this.mAccessibilityManagerInternal == null) {
                this.mAccessibilityManagerInternal = (AccessibilityManagerInternal) LocalServices.getService(AccessibilityManagerInternal.class);
            }
            accessibilityManagerInternal = this.mAccessibilityManagerInternal;
        }
        return accessibilityManagerInternal;
    }

    public final boolean backKeyPress() {
        TelecomManager telecommService;
        this.mLogger.count("key_back_press", 1);
        boolean z = this.mBackKeyHandled;
        if (this.mHasFeatureWatch && (telecommService = getTelecommService()) != null) {
            if (telecommService.isRinging()) {
                telecommService.silenceRinger();
                return false;
            } else if ((1 & this.mIncallBackBehavior) != 0 && telecommService.isInCall()) {
                return telecommService.endCall();
            }
        }
        if (this.mAutofillManagerInternal != null) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(20));
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0065  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void interceptPowerKeyDown(KeyEvent keyEvent, boolean z) {
        boolean z2;
        if (!this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.acquire();
        }
        this.mWindowManagerFuncs.onPowerKeyDown(z);
        TelecomManager telecommService = getTelecommService();
        boolean z3 = false;
        if (telecommService != null) {
            if (telecommService.isRinging()) {
                telecommService.silenceRinger();
            } else if ((this.mIncallPowerBehavior & 2) != 0 && telecommService.isInCall() && z) {
                z2 = telecommService.endCall();
                boolean interceptPowerKeyDown = this.mPowerManagerInternal.interceptPowerKeyDown(keyEvent);
                sendSystemKeyToStatusBarAsync(keyEvent.getKeyCode());
                this.mPowerKeyHandled = (!this.mPowerKeyHandled || z2 || interceptPowerKeyDown || this.mKeyCombinationManager.isPowerKeyIntercepted()) ? true : true;
                if (this.mPowerKeyHandled) {
                    if (z) {
                        return;
                    }
                    wakeUpFromPowerKey(keyEvent.getDownTime());
                    return;
                } else if (this.mSingleKeyGestureDetector.isKeyIntercepted(26)) {
                    Slog.d(StartingSurfaceController.TAG, "Skip power key gesture for other policy has handled it.");
                    this.mSingleKeyGestureDetector.reset();
                    return;
                } else {
                    return;
                }
            }
        }
        z2 = false;
        boolean interceptPowerKeyDown2 = this.mPowerManagerInternal.interceptPowerKeyDown(keyEvent);
        sendSystemKeyToStatusBarAsync(keyEvent.getKeyCode());
        this.mPowerKeyHandled = (!this.mPowerKeyHandled || z2 || interceptPowerKeyDown2 || this.mKeyCombinationManager.isPowerKeyIntercepted()) ? true : true;
        if (this.mPowerKeyHandled) {
        }
    }

    public final void interceptPowerKeyUp(KeyEvent keyEvent, boolean z) {
        if (!(z || this.mPowerKeyHandled) && (keyEvent.getFlags() & 128) == 0) {
            Handler handler = this.mHandler;
            final WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs = this.mWindowManagerFuncs;
            Objects.requireNonNull(windowManagerFuncs);
            handler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WindowManagerPolicy.WindowManagerFuncs.this.triggerAnimationFailsafe();
                }
            });
        }
        finishPowerKeyPress();
    }

    public final void finishPowerKeyPress() {
        this.mPowerKeyHandled = false;
        if (this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.release();
        }
    }

    public final void powerPress(final long j, int i, boolean z) {
        if (i == 1) {
            this.mSideFpsEventHandler.notifyPowerPressed();
        }
        if (this.mDefaultDisplayPolicy.isScreenOnEarly() && !this.mDefaultDisplayPolicy.isScreenOnFully()) {
            Slog.i(StartingSurfaceController.TAG, "Suppressed redundant power key press while already in the process of turning the screen on.");
            return;
        }
        boolean isOnState = Display.isOnState(this.mDefaultDisplay.getState());
        Slog.d(StartingSurfaceController.TAG, "powerPress: eventTime=" + j + " interactive=" + isOnState + " count=" + i + " beganFromNonInteractive=" + z + " mShortPressOnPowerBehavior=" + this.mShortPressOnPowerBehavior);
        if (i == 2) {
            powerMultiPressAction(j, isOnState, this.mDoublePressOnPowerBehavior);
        } else if (i == 3) {
            powerMultiPressAction(j, isOnState, this.mTriplePressOnPowerBehavior);
        } else if (i > 3 && i <= getMaxMultiPressPowerCount()) {
            Slog.d(StartingSurfaceController.TAG, "No behavior defined for power press count " + i);
        } else if (i == 1 && isOnState && !z) {
            if (this.mSideFpsEventHandler.shouldConsumeSinglePress(j)) {
                Slog.i(StartingSurfaceController.TAG, "Suppressing power key because the user is interacting with the fingerprint sensor");
                return;
            }
            switch (this.mShortPressOnPowerBehavior) {
                case 1:
                    sleepDefaultDisplayFromPowerButton(j, 0);
                    return;
                case 2:
                    sleepDefaultDisplayFromPowerButton(j, 1);
                    return;
                case 3:
                    if (sleepDefaultDisplayFromPowerButton(j, 1)) {
                        launchHomeFromHotKey(0);
                        return;
                    }
                    return;
                case 4:
                    shortPressPowerGoHome();
                    return;
                case 5:
                    if (this.mDismissImeOnBackKeyPressed) {
                        InputMethodManagerInternal.get().hideCurrentInputMethod(17);
                        return;
                    } else {
                        shortPressPowerGoHome();
                        return;
                    }
                case 6:
                    KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                    if (keyguardServiceDelegate == null || !keyguardServiceDelegate.hasKeyguard() || !this.mKeyguardDelegate.isSecure(this.mCurrentUserId) || keyguardOn()) {
                        sleepDefaultDisplayFromPowerButton(j, 0);
                        return;
                    } else {
                        lockNow(null);
                        return;
                    }
                case 7:
                    attemptToDreamFromShortPowerButtonPress(true, new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhoneWindowManager.this.lambda$powerPress$0(j);
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$powerPress$0(long j) {
        sleepDefaultDisplayFromPowerButton(j, 0);
    }

    public final void attemptToDreamFromShortPowerButtonPress(boolean z, Runnable runnable) {
        if (this.mShortPressOnPowerBehavior != 7) {
            runnable.run();
            return;
        }
        DreamManagerInternal dreamManagerInternal = getDreamManagerInternal();
        if (dreamManagerInternal == null || !dreamManagerInternal.canStartDreaming(z)) {
            runnable.run();
            return;
        }
        synchronized (this.mLock) {
            this.mLockAfterAppTransitionFinished = this.mLockPatternUtils.getPowerButtonInstantlyLocks(this.mCurrentUserId);
        }
        dreamManagerInternal.requestDream();
    }

    public final boolean sleepDefaultDisplayFromPowerButton(long j, int i) {
        int i2;
        PowerManager.WakeData lastWakeup = this.mPowerManagerInternal.getLastWakeup();
        if (lastWakeup != null && ((i2 = lastWakeup.wakeReason) == 4 || i2 == 16 || i2 == 17)) {
            long uptimeMillis = SystemClock.uptimeMillis();
            int i3 = this.mPowerButtonSuppressionDelayMillis;
            if (i3 > 0 && uptimeMillis < lastWakeup.wakeTime + i3) {
                Slog.i(StartingSurfaceController.TAG, "Sleep from power button suppressed. Time since gesture: " + (uptimeMillis - lastWakeup.wakeTime) + "ms");
                return false;
            }
        }
        sleepDefaultDisplay(j, 4, i);
        return true;
    }

    public final void sleepDefaultDisplay(long j, int i, int i2) {
        this.mRequestedOrSleepingDefaultDisplay = true;
        this.mPowerManager.goToSleep(j, i, i2);
    }

    public final void shortPressPowerGoHome() {
        launchHomeFromHotKey(0, true, false);
        if (isKeyguardShowingAndNotOccluded()) {
            this.mKeyguardDelegate.onShortPowerPressedGoHome();
        }
    }

    public final void powerMultiPressAction(long j, boolean z, int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return;
                }
                launchTargetActivityOnMultiPressPower();
                return;
            }
            Slog.i(StartingSurfaceController.TAG, "Starting brightness boost.");
            if (!z) {
                wakeUpFromPowerKey(j);
            }
            this.mPowerManager.boostScreenBrightness(j);
        } else if (!isUserSetupComplete()) {
            Slog.i(StartingSurfaceController.TAG, "Ignoring toggling theater mode - device not setup.");
        } else if (isTheaterModeEnabled()) {
            Slog.i(StartingSurfaceController.TAG, "Toggling theater mode off.");
            Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
            if (z) {
                return;
            }
            wakeUpFromPowerKey(j);
        } else {
            Slog.i(StartingSurfaceController.TAG, "Toggling theater mode on.");
            Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 1);
            if (this.mGoToSleepOnButtonPressTheaterMode && z) {
                sleepDefaultDisplay(j, 4, 0);
            }
        }
    }

    public final void launchTargetActivityOnMultiPressPower() {
        if (this.mPowerDoublePressTargetActivity != null) {
            Intent intent = new Intent();
            intent.setComponent(this.mPowerDoublePressTargetActivity);
            boolean z = false;
            if (this.mContext.getPackageManager().resolveActivity(intent, 0) != null) {
                KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                if (keyguardServiceDelegate != null && keyguardServiceDelegate.isShowing()) {
                    z = true;
                }
                intent.addFlags(270532608);
                if (!z) {
                    startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
                    return;
                } else {
                    this.mKeyguardDelegate.dismissKeyguardToLaunch(intent);
                    return;
                }
            }
            Slog.e(StartingSurfaceController.TAG, "Could not resolve activity with : " + this.mPowerDoublePressTargetActivity.flattenToString() + " name.");
        }
    }

    public final int getLidBehavior() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "lid_behavior", 0);
    }

    public final int getMaxMultiPressPowerCount() {
        if (this.mHasFeatureWatch && GestureLauncherService.isEmergencyGestureSettingEnabled(this.mContext, ActivityManager.getCurrentUser())) {
            return 5;
        }
        if (this.mTriplePressOnPowerBehavior != 0) {
            return 3;
        }
        return this.mDoublePressOnPowerBehavior != 0 ? 2 : 1;
    }

    public final void powerLongPress(long j) {
        int resolvedLongPressOnPowerBehavior = getResolvedLongPressOnPowerBehavior();
        Slog.d(StartingSurfaceController.TAG, "powerLongPress: eventTime=" + j + " mLongPressOnPowerBehavior=" + this.mLongPressOnPowerBehavior);
        if (resolvedLongPressOnPowerBehavior == 1) {
            this.mPowerKeyHandled = true;
            performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, false, "Power - Long Press - Global Actions");
            showGlobalActions();
        } else if (resolvedLongPressOnPowerBehavior == 2 || resolvedLongPressOnPowerBehavior == 3) {
            this.mPowerKeyHandled = true;
            if (ActivityManager.isUserAMonkey()) {
                return;
            }
            performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, false, "Power - Long Press - Shut Off");
            sendCloseSystemWindows("globalactions");
            this.mWindowManagerFuncs.shutdown(resolvedLongPressOnPowerBehavior == 2);
        } else if (resolvedLongPressOnPowerBehavior == 4) {
            this.mPowerKeyHandled = true;
            performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, false, "Power - Long Press - Go To Voice Assist");
            launchVoiceAssist(this.mAllowStartActivityForLongPressOnPowerDuringSetup);
        } else if (resolvedLongPressOnPowerBehavior != 5) {
        } else {
            this.mPowerKeyHandled = true;
            performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER, false, "Power - Long Press - Go To Assistant");
            launchAssistAction(null, Integer.MIN_VALUE, j, 6);
        }
    }

    public final void powerVeryLongPress() {
        if (this.mVeryLongPressOnPowerBehavior != 1) {
            return;
        }
        this.mPowerKeyHandled = true;
        performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, false, "Power - Very Long Press - Show Global Actions");
        showGlobalActions();
    }

    public final void backLongPress() {
        this.mBackKeyHandled = true;
        if (this.mLongPressOnBackBehavior != 1) {
            return;
        }
        launchVoiceAssist(false);
    }

    public final void accessibilityShortcutActivated() {
        this.mAccessibilityShortcutController.performAccessibilityShortcut();
    }

    public final void sleepPress() {
        if (this.mShortPressOnSleepBehavior == 1) {
            launchHomeFromHotKey(0, false, true);
        }
    }

    public final void sleepRelease(long j) {
        int i = this.mShortPressOnSleepBehavior;
        if (i == 0 || i == 1) {
            Slog.i(StartingSurfaceController.TAG, "sleepRelease() calling goToSleep(GO_TO_SLEEP_REASON_SLEEP_BUTTON)");
            sleepDefaultDisplay(j, 6, 0);
        }
    }

    public final int getResolvedLongPressOnPowerBehavior() {
        if (FactoryTest.isLongPressOnPowerOffEnabled()) {
            return 3;
        }
        if (this.mLongPressOnPowerBehavior != 5 || isDeviceProvisioned()) {
            if (this.mLongPressOnPowerBehavior != 4 || isLongPressToAssistantEnabled(this.mContext)) {
                return this.mLongPressOnPowerBehavior;
            }
            return 0;
        }
        return 1;
    }

    public final void stemPrimaryPress(int i) {
        if (i == 3) {
            stemPrimaryTriplePressAction(this.mTriplePressOnStemPrimaryBehavior);
        } else if (i == 2) {
            stemPrimaryDoublePressAction(this.mDoublePressOnStemPrimaryBehavior);
        } else if (i == 1) {
            stemPrimarySinglePressAction(this.mShortPressOnStemPrimaryBehavior);
        }
    }

    public final void stemPrimarySinglePressAction(int i) {
        boolean z = true;
        if (i != 1) {
            return;
        }
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (!((keyguardServiceDelegate == null || !keyguardServiceDelegate.isShowing()) ? false : false)) {
            Intent intent = new Intent("android.intent.action.ALL_APPS");
            intent.addFlags(270532608);
            startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
            return;
        }
        this.mKeyguardDelegate.onSystemKeyPressed(264);
    }

    public final void stemPrimaryDoublePressAction(int i) {
        if (i != 1) {
            return;
        }
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null ? false : keyguardServiceDelegate.isShowing()) {
            return;
        }
        switchRecentTask();
    }

    public final void stemPrimaryTriplePressAction(int i) {
        if (i != 1) {
            return;
        }
        toggleTalkBack();
    }

    public final void stemPrimaryLongPress() {
        if (this.mLongPressOnStemPrimaryBehavior != 1) {
            return;
        }
        launchVoiceAssist(false);
    }

    public final void toggleTalkBack() {
        ComponentName talkbackComponent = getTalkbackComponent();
        if (talkbackComponent == null) {
            return;
        }
        AccessibilityUtils.setAccessibilityServiceState(this.mContext, talkbackComponent, !AccessibilityUtils.getEnabledServicesFromSettings(this.mContext, this.mCurrentUserId).contains(talkbackComponent));
    }

    public final ComponentName getTalkbackComponent() {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            if (isTalkback(serviceInfo)) {
                return new ComponentName(serviceInfo.packageName, serviceInfo.name);
            }
        }
        return null;
    }

    public final boolean isTalkback(ServiceInfo serviceInfo) {
        return serviceInfo.loadLabel(this.mPackageManager).toString().equals("TalkBack");
    }

    public final void switchRecentTask() {
        ActivityManager.RecentTaskInfo mostRecentTaskFromBackground = this.mActivityTaskManagerInternal.getMostRecentTaskFromBackground();
        if (mostRecentTaskFromBackground == null) {
            goHome();
            return;
        }
        try {
            ActivityManager.getService().startActivityFromRecents(mostRecentTaskFromBackground.persistentId, (Bundle) null);
        } catch (RemoteException | IllegalArgumentException e) {
            Slog.e(StartingSurfaceController.TAG, "Failed to start task " + mostRecentTaskFromBackground.persistentId + " from recents", e);
        }
    }

    public final int getMaxMultiPressStemPrimaryCount() {
        if (this.mTriplePressOnStemPrimaryBehavior == 1 && Settings.System.getIntForUser(this.mContext.getContentResolver(), "wear_accessibility_gesture_enabled", 0, -2) == 1) {
            return 3;
        }
        return this.mDoublePressOnStemPrimaryBehavior != 0 ? 2 : 1;
    }

    public final boolean hasLongPressOnPowerBehavior() {
        return getResolvedLongPressOnPowerBehavior() != 0;
    }

    public final boolean hasVeryLongPressOnPowerBehavior() {
        return this.mVeryLongPressOnPowerBehavior != 0;
    }

    public final boolean hasLongPressOnBackBehavior() {
        return this.mLongPressOnBackBehavior != 0;
    }

    public final boolean hasLongPressOnStemPrimaryBehavior() {
        return this.mLongPressOnStemPrimaryBehavior != 0;
    }

    public final boolean hasStemPrimaryBehavior() {
        return getMaxMultiPressStemPrimaryCount() > 1 || hasLongPressOnStemPrimaryBehavior() || this.mShortPressOnStemPrimaryBehavior != 0;
    }

    public final void interceptScreenshotChord(int i, long j) {
        this.mHandler.removeMessages(16);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(16, Integer.valueOf(i)), j);
    }

    public final void interceptAccessibilityShortcutChord() {
        this.mHandler.removeMessages(17);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(17), getAccessibilityShortcutTimeout());
    }

    public final void interceptRingerToggleChord() {
        this.mHandler.removeMessages(24);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(24), getRingerToggleChordDelay());
    }

    public final long getAccessibilityShortcutTimeout() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mContext);
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_shortcut_dialog_shown", 0, this.mCurrentUserId) != 0;
        boolean z2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "skip_accessibility_shortcut_dialog_timeout_restriction", 0, this.mCurrentUserId) != 0;
        if (z || z2) {
            return viewConfiguration.getAccessibilityShortcutKeyTimeoutAfterConfirmation();
        }
        return viewConfiguration.getAccessibilityShortcutKeyTimeout();
    }

    public final long getScreenshotChordLongPressDelay() {
        long j = DeviceConfig.getLong("systemui", "screenshot_keychord_delay", ViewConfiguration.get(this.mContext).getScreenshotChordKeyTimeout());
        return this.mKeyguardDelegate.isShowing() ? ((float) j) * 2.5f : j;
    }

    public final long getRingerToggleChordDelay() {
        return ViewConfiguration.getTapTimeout();
    }

    public final void cancelPendingScreenshotChordAction() {
        this.mHandler.removeMessages(16);
    }

    public final void cancelPendingAccessibilityShortcutAction() {
        this.mHandler.removeMessages(17);
    }

    public final void cancelPendingRingerToggleChordAction() {
        this.mHandler.removeMessages(24);
    }

    public final void handleScreenShot(@WindowManager.ScreenshotSource int i) {
        this.mDefaultDisplayPolicy.takeScreenshot(1, i);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void showGlobalActions() {
        this.mHandler.removeMessages(10);
        this.mHandler.sendEmptyMessage(10);
    }

    public void showGlobalActionsInternal() {
        if (this.mGlobalActions == null) {
            this.mGlobalActions = this.mGlobalActionsFactory.get();
        }
        this.mGlobalActions.showDialog(isKeyguardShowingAndNotOccluded(), isDeviceProvisioned());
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
    }

    public final void cancelGlobalActionsAction() {
        this.mHandler.removeMessages(10);
    }

    public boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isUserSetupComplete() {
        boolean isAutoUserSetupComplete;
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0;
        if (this.mHasFeatureLeanback) {
            isAutoUserSetupComplete = isTvUserSetupComplete();
        } else if (!this.mHasFeatureAuto) {
            return z;
        } else {
            isAutoUserSetupComplete = isAutoUserSetupComplete();
        }
        return z & isAutoUserSetupComplete;
    }

    public final boolean isAutoUserSetupComplete() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "android.car.SETUP_WIZARD_IN_PROGRESS", 0, -2) == 0;
    }

    public final boolean isTvUserSetupComplete() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "tv_user_setup_complete", 0, -2) != 0;
    }

    public final void handleShortPressOnHome(int i) {
        HdmiControl hdmiControl = getHdmiControl();
        if (hdmiControl != null) {
            hdmiControl.turnOnTv();
        }
        DreamManagerInternal dreamManagerInternal = getDreamManagerInternal();
        if (dreamManagerInternal != null && dreamManagerInternal.isDreaming()) {
            this.mDreamManagerInternal.stopDream(false, "short press on home");
        } else {
            launchHomeFromHotKey(i);
        }
    }

    public final HdmiControl getHdmiControl() {
        if (this.mHdmiControl == null) {
            if (!this.mHasFeatureHdmiCec) {
                return null;
            }
            HdmiControlManager hdmiControlManager = (HdmiControlManager) this.mContext.getSystemService("hdmi_control");
            this.mHdmiControl = new HdmiControl(hdmiControlManager != null ? hdmiControlManager.getPlaybackClient() : null);
        }
        return this.mHdmiControl;
    }

    /* loaded from: classes2.dex */
    public static class HdmiControl {
        public final HdmiPlaybackClient mClient;

        public HdmiControl(HdmiPlaybackClient hdmiPlaybackClient) {
            this.mClient = hdmiPlaybackClient;
        }

        public void turnOnTv() {
            HdmiPlaybackClient hdmiPlaybackClient = this.mClient;
            if (hdmiPlaybackClient == null) {
                return;
            }
            hdmiPlaybackClient.oneTouchPlay(new HdmiPlaybackClient.OneTouchPlayCallback() { // from class: com.android.server.policy.PhoneWindowManager.HdmiControl.1
                public void onComplete(int i) {
                    if (i != 0) {
                        Log.w(StartingSurfaceController.TAG, "One touch play failed: " + i);
                    }
                }
            });
        }
    }

    public final void launchAllAppsAction() {
        Intent intent = new Intent("android.intent.action.ALL_APPS");
        if (this.mHasFeatureLeanback) {
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.addCategory("android.intent.category.HOME");
            ResolveInfo resolveActivityAsUser = this.mPackageManager.resolveActivityAsUser(intent2, 1048576, this.mCurrentUserId);
            if (resolveActivityAsUser != null) {
                intent.setPackage(resolveActivityAsUser.activityInfo.packageName);
            }
        }
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    public final void launchAllAppsViaA11y() {
        getAccessibilityManagerInternal().performSystemAction(14);
    }

    public final void toggleNotificationPanel() {
        IStatusBarService statusBarService = getStatusBarService();
        if (!isUserSetupComplete() || statusBarService == null) {
            return;
        }
        try {
            statusBarService.togglePanel();
        } catch (RemoteException unused) {
        }
    }

    public final void showSystemSettings() {
        startActivityAsUser(new Intent("android.settings.SETTINGS"), UserHandle.CURRENT_OR_SELF);
    }

    public final void showPictureInPictureMenu(KeyEvent keyEvent) {
        this.mHandler.removeMessages(15);
        Message obtainMessage = this.mHandler.obtainMessage(15);
        obtainMessage.setAsynchronous(true);
        obtainMessage.sendToTarget();
    }

    public final void showPictureInPictureMenuInternal() {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.showPictureInPictureMenu();
        }
    }

    /* loaded from: classes2.dex */
    public class DisplayHomeButtonHandler {
        public final int mDisplayId;
        public boolean mHomeConsumed;
        public boolean mHomeDoubleTapPending;
        public final Runnable mHomeDoubleTapTimeoutRunnable = new Runnable() { // from class: com.android.server.policy.PhoneWindowManager.DisplayHomeButtonHandler.1
            @Override // java.lang.Runnable
            public void run() {
                if (DisplayHomeButtonHandler.this.mHomeDoubleTapPending) {
                    DisplayHomeButtonHandler.this.mHomeDoubleTapPending = false;
                    DisplayHomeButtonHandler displayHomeButtonHandler = DisplayHomeButtonHandler.this;
                    PhoneWindowManager.this.handleShortPressOnHome(displayHomeButtonHandler.mDisplayId);
                }
            }
        };
        public boolean mHomePressed;

        public DisplayHomeButtonHandler(int i) {
            this.mDisplayId = i;
        }

        public int handleHomeButton(IBinder iBinder, final KeyEvent keyEvent) {
            boolean keyguardOn = PhoneWindowManager.this.keyguardOn();
            int repeatCount = keyEvent.getRepeatCount();
            boolean z = keyEvent.getAction() == 0;
            boolean isCanceled = keyEvent.isCanceled();
            if (!z) {
                if (this.mDisplayId == 0) {
                    PhoneWindowManager.this.cancelPreloadRecentApps();
                }
                this.mHomePressed = false;
                if (this.mHomeConsumed) {
                    this.mHomeConsumed = false;
                    return -1;
                } else if (isCanceled) {
                    Log.i(StartingSurfaceController.TAG, "Ignoring HOME; event canceled.");
                    return -1;
                } else if (PhoneWindowManager.this.mDoubleTapOnHomeBehavior != 0 && (PhoneWindowManager.this.mDoubleTapOnHomeBehavior != 2 || PhoneWindowManager.this.mPictureInPictureVisible)) {
                    PhoneWindowManager.this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                    this.mHomeDoubleTapPending = true;
                    PhoneWindowManager.this.mHandler.postDelayed(this.mHomeDoubleTapTimeoutRunnable, ViewConfiguration.getDoubleTapTimeout());
                    return -1;
                } else {
                    PhoneWindowManager.this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhoneWindowManager.DisplayHomeButtonHandler.this.lambda$handleHomeButton$0();
                        }
                    });
                    return -1;
                }
            }
            KeyInterceptionInfo keyInterceptionInfoFromToken = PhoneWindowManager.this.mWindowManagerInternal.getKeyInterceptionInfoFromToken(iBinder);
            if (keyInterceptionInfoFromToken != null) {
                int i = keyInterceptionInfoFromToken.layoutParamsType;
                if (i == 2009 || (i == 2040 && PhoneWindowManager.this.isKeyguardShowing())) {
                    return 0;
                }
                for (int i2 : PhoneWindowManager.WINDOW_TYPES_WHERE_HOME_DOESNT_WORK) {
                    if (keyInterceptionInfoFromToken.layoutParamsType == i2) {
                        return -1;
                    }
                }
            }
            if (repeatCount == 0) {
                this.mHomePressed = true;
                if (this.mHomeDoubleTapPending) {
                    this.mHomeDoubleTapPending = false;
                    PhoneWindowManager.this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                    PhoneWindowManager.this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhoneWindowManager.DisplayHomeButtonHandler.this.handleDoubleTapOnHome();
                        }
                    });
                } else if (PhoneWindowManager.this.mDoubleTapOnHomeBehavior == 1 && this.mDisplayId == 0) {
                    PhoneWindowManager.this.preloadRecentApps();
                }
            } else if ((keyEvent.getFlags() & 128) != 0 && !keyguardOn) {
                PhoneWindowManager.this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$DisplayHomeButtonHandler$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhoneWindowManager.DisplayHomeButtonHandler.this.lambda$handleHomeButton$1(keyEvent);
                    }
                });
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleHomeButton$0() {
            PhoneWindowManager.this.handleShortPressOnHome(this.mDisplayId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleHomeButton$1(KeyEvent keyEvent) {
            handleLongPressOnHome(keyEvent.getDeviceId(), keyEvent.getEventTime());
        }

        public final void handleDoubleTapOnHome() {
            if (this.mHomeConsumed) {
                return;
            }
            int i = PhoneWindowManager.this.mDoubleTapOnHomeBehavior;
            if (i == 1) {
                this.mHomeConsumed = true;
                PhoneWindowManager.this.toggleRecentApps();
            } else if (i == 2) {
                this.mHomeConsumed = true;
                PhoneWindowManager.this.showPictureInPictureMenuInternal();
            } else {
                Log.w(StartingSurfaceController.TAG, "No action or undefined behavior for double tap home: " + PhoneWindowManager.this.mDoubleTapOnHomeBehavior);
            }
        }

        public final void handleLongPressOnHome(int i, long j) {
            if (this.mHomeConsumed || PhoneWindowManager.this.mLongPressOnHomeBehavior == 0) {
                return;
            }
            this.mHomeConsumed = true;
            PhoneWindowManager.this.performHapticFeedback(0, false, "Home - Long Press");
            int i2 = PhoneWindowManager.this.mLongPressOnHomeBehavior;
            if (i2 == 1) {
                PhoneWindowManager.this.launchAllAppsAction();
            } else if (i2 == 2) {
                PhoneWindowManager.this.launchAssistAction(null, i, j, 5);
            } else if (i2 == 3) {
                PhoneWindowManager.this.toggleNotificationPanel();
            } else {
                Log.w(StartingSurfaceController.TAG, "Undefined long press on home behavior: " + PhoneWindowManager.this.mLongPressOnHomeBehavior);
            }
        }

        public String toString() {
            return String.format("mDisplayId = %d, mHomePressed = %b", Integer.valueOf(this.mDisplayId), Boolean.valueOf(this.mHomePressed));
        }
    }

    public final boolean isRoundWindow() {
        return this.mContext.getResources().getConfiguration().isScreenRound();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setDefaultDisplay(WindowManagerPolicy.DisplayContentInfo displayContentInfo) {
        this.mDefaultDisplay = displayContentInfo.getDisplay();
        DisplayRotation displayRotation = displayContentInfo.getDisplayRotation();
        this.mDefaultDisplayRotation = displayRotation;
        this.mDefaultDisplayPolicy = displayRotation.getDisplayPolicy();
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public final Context mContext;
        public final WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;

        public Injector(Context context, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
            this.mContext = context;
            this.mWindowManagerFuncs = windowManagerFuncs;
        }

        public Context getContext() {
            return this.mContext;
        }

        public WindowManagerPolicy.WindowManagerFuncs getWindowManagerFuncs() {
            return this.mWindowManagerFuncs;
        }

        public AccessibilityShortcutController getAccessibilityShortcutController(Context context, Handler handler, int i) {
            return new AccessibilityShortcutController(context, handler, i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ GlobalActions lambda$getGlobalActionsFactory$0() {
            return new GlobalActions(this.mContext, this.mWindowManagerFuncs);
        }

        public Supplier<GlobalActions> getGlobalActionsFactory() {
            return new Supplier() { // from class: com.android.server.policy.PhoneWindowManager$Injector$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    GlobalActions lambda$getGlobalActionsFactory$0;
                    lambda$getGlobalActionsFactory$0 = PhoneWindowManager.Injector.this.lambda$getGlobalActionsFactory$0();
                    return lambda$getGlobalActionsFactory$0;
                }
            };
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void init(Context context, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        init(new Injector(context, windowManagerFuncs));
    }

    @VisibleForTesting
    public void init(Injector injector) {
        int integer;
        int i;
        int i2;
        int i3;
        int i4;
        this.mContext = injector.getContext();
        this.mWindowManagerFuncs = injector.getWindowManagerFuncs();
        this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
        this.mDreamManagerInternal = (DreamManagerInternal) LocalServices.getService(DreamManagerInternal.class);
        this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mSensorPrivacyManager = (SensorPrivacyManager) this.mContext.getSystemService(SensorPrivacyManager.class);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        PackageManager packageManager = this.mContext.getPackageManager();
        this.mPackageManager = packageManager;
        this.mHasFeatureWatch = packageManager.hasSystemFeature("android.hardware.type.watch");
        this.mHasFeatureLeanback = this.mPackageManager.hasSystemFeature("android.software.leanback");
        this.mHasFeatureAuto = this.mPackageManager.hasSystemFeature("android.hardware.type.automotive");
        this.mHasFeatureHdmiCec = this.mPackageManager.hasSystemFeature("android.hardware.hdmi.cec");
        this.mAccessibilityShortcutController = injector.getAccessibilityShortcutController(this.mContext, new Handler(), this.mCurrentUserId);
        this.mGlobalActionsFactory = injector.getGlobalActionsFactory();
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mLogger = new MetricsLogger();
        this.mScreenOffSleepTokenAcquirer = this.mActivityTaskManagerInternal.createSleepTokenAcquirer("ScreenOff");
        Resources resources = this.mContext.getResources();
        this.mWakeOnDpadKeyPress = resources.getBoolean(17891883);
        this.mWakeOnAssistKeyPress = resources.getBoolean(17891881);
        this.mWakeOnBackKeyPress = resources.getBoolean(17891882);
        boolean z = this.mContext.getResources().getBoolean(17891646);
        boolean z2 = SystemProperties.getBoolean("persist.debug.force_burn_in", false);
        if (z || z2) {
            if (z2) {
                integer = isRoundWindow() ? 6 : -1;
                i = -8;
                i3 = -8;
                i2 = 8;
                i4 = -4;
            } else {
                Resources resources2 = this.mContext.getResources();
                int integer2 = resources2.getInteger(17694768);
                int integer3 = resources2.getInteger(17694765);
                int integer4 = resources2.getInteger(17694769);
                int integer5 = resources2.getInteger(17694767);
                integer = resources2.getInteger(17694766);
                i = integer2;
                i2 = integer3;
                i3 = integer4;
                i4 = integer5;
            }
            this.mBurnInProtectionHelper = new BurnInProtectionHelper(this.mContext, i, i2, i3, i4, integer);
        }
        PolicyHandler policyHandler = new PolicyHandler();
        this.mHandler = policyHandler;
        this.mWakeGestureListener = new MyWakeGestureListener(this.mContext, policyHandler);
        SettingsObserver settingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver = settingsObserver;
        settingsObserver.observe();
        this.mModifierShortcutManager = new ModifierShortcutManager(this.mContext);
        this.mUiMode = this.mContext.getResources().getInteger(17694807);
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mHomeIntent = intent;
        intent.addCategory("android.intent.category.HOME");
        this.mHomeIntent.addFlags(270532608);
        this.mEnableCarDockHomeCapture = this.mContext.getResources().getBoolean(17891647);
        Intent intent2 = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mCarDockIntent = intent2;
        intent2.addCategory("android.intent.category.CAR_DOCK");
        this.mCarDockIntent.addFlags(270532608);
        Intent intent3 = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mDeskDockIntent = intent3;
        intent3.addCategory("android.intent.category.DESK_DOCK");
        this.mDeskDockIntent.addFlags(270532608);
        Intent intent4 = new Intent("android.intent.action.MAIN", (Uri) null);
        this.mVrHeadsetHomeIntent = intent4;
        intent4.addCategory("android.intent.category.VR_HOME");
        this.mVrHeadsetHomeIntent.addFlags(270532608);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mBroadcastWakeLock = powerManager.newWakeLock(1, "PhoneWindowManager.mBroadcastWakeLock");
        this.mPowerKeyWakeLock = this.mPowerManager.newWakeLock(1, "PhoneWindowManager.mPowerKeyWakeLock");
        this.mEnableShiftMenuBugReports = "1".equals(SystemProperties.get("ro.debuggable"));
        this.mLidKeyboardAccessibility = this.mContext.getResources().getInteger(17694862);
        this.mLidNavigationAccessibility = this.mContext.getResources().getInteger(17694863);
        boolean z3 = this.mContext.getResources().getBoolean(17891356);
        this.mAllowTheaterModeWakeFromKey = z3;
        this.mAllowTheaterModeWakeFromPowerKey = z3 || this.mContext.getResources().getBoolean(17891360);
        this.mAllowTheaterModeWakeFromMotion = this.mContext.getResources().getBoolean(17891358);
        this.mAllowTheaterModeWakeFromMotionWhenNotDreaming = this.mContext.getResources().getBoolean(17891359);
        this.mAllowTheaterModeWakeFromCameraLens = this.mContext.getResources().getBoolean(17891353);
        this.mAllowTheaterModeWakeFromLidSwitch = this.mContext.getResources().getBoolean(17891357);
        this.mAllowTheaterModeWakeFromWakeGesture = this.mContext.getResources().getBoolean(17891355);
        this.mGoToSleepOnButtonPressTheaterMode = this.mContext.getResources().getBoolean(17891692);
        this.mSupportLongPressPowerWhenNonInteractive = this.mContext.getResources().getBoolean(17891819);
        this.mLongPressOnBackBehavior = this.mContext.getResources().getInteger(17694867);
        this.mShortPressOnPowerBehavior = this.mContext.getResources().getInteger(17694956);
        this.mLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694869);
        this.mLongPressOnPowerAssistantTimeoutMs = this.mContext.getResources().getInteger(17694870);
        this.mVeryLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694982);
        this.mDoublePressOnPowerBehavior = this.mContext.getResources().getInteger(17694826);
        this.mPowerDoublePressTargetActivity = ComponentName.unflattenFromString(this.mContext.getResources().getString(17039923));
        this.mTriplePressOnPowerBehavior = this.mContext.getResources().getInteger(17694976);
        this.mShortPressOnSleepBehavior = this.mContext.getResources().getInteger(17694957);
        this.mAllowStartActivityForLongPressOnPowerDuringSetup = this.mContext.getResources().getBoolean(17891352);
        this.mHapticTextHandleEnabled = this.mContext.getResources().getBoolean(17891654);
        this.mUseTvRouting = AudioSystem.getPlatformType(this.mContext) == 2;
        this.mHandleVolumeKeysInWM = this.mContext.getResources().getBoolean(17891697);
        this.mWakeUpToLastStateTimeout = this.mContext.getResources().getInteger(17694988);
        this.mSearchKeyBehavior = this.mContext.getResources().getInteger(17694954);
        this.mSearchKeyTargetActivity = ComponentName.unflattenFromString(this.mContext.getResources().getString(17039996));
        readConfigurationDependentBehaviors();
        this.mDisplayFoldController = DisplayFoldController.create(this.mContext, 0);
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
        intentFilter.addAction(UiModeManager.ACTION_EXIT_CAR_MODE);
        intentFilter.addAction(UiModeManager.ACTION_ENTER_DESK_MODE);
        intentFilter.addAction(UiModeManager.ACTION_EXIT_DESK_MODE);
        intentFilter.addAction("android.intent.action.DOCK_EVENT");
        Intent registerReceiver = this.mContext.registerReceiver(this.mDockReceiver, intentFilter);
        if (registerReceiver != null) {
            this.mDefaultDisplayPolicy.setDockMode(registerReceiver.getIntExtra("android.intent.extra.DOCK_STATE", 0));
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.DREAMING_STARTED");
        intentFilter2.addAction("android.intent.action.DREAMING_STOPPED");
        this.mContext.registerReceiver(this.mDreamReceiver, intentFilter2);
        this.mContext.registerReceiver(this.mMultiuserReceiver, new IntentFilter("android.intent.action.USER_SWITCHED"));
        this.mVibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mSafeModeEnabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236129);
        this.mGlobalKeyManager = new GlobalKeyManager(this.mContext);
        initializeHdmiState();
        if (!this.mPowerManager.isInteractive()) {
            startedGoingToSleep(2);
            finishedGoingToSleep(2);
        }
        this.mWindowManagerInternal.registerAppTransitionListener(new WindowManagerInternal.AppTransitionListener() { // from class: com.android.server.policy.PhoneWindowManager.5
            @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
            public int onAppTransitionStartingLocked(long j, long j2) {
                return PhoneWindowManager.this.handleTransitionForKeyguardLw(false, false);
            }

            @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
            public void onAppTransitionCancelledLocked(boolean z4) {
                PhoneWindowManager.this.handleTransitionForKeyguardLw(z4, true);
                synchronized (PhoneWindowManager.this.mLock) {
                    PhoneWindowManager.this.mLockAfterAppTransitionFinished = false;
                }
            }

            @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
            public void onAppTransitionFinishedLocked(IBinder iBinder) {
                synchronized (PhoneWindowManager.this.mLock) {
                    if (PhoneWindowManager.this.mLockAfterAppTransitionFinished) {
                        PhoneWindowManager.this.mLockAfterAppTransitionFinished = false;
                        PhoneWindowManager.this.lockNow(null);
                    }
                }
            }
        });
        this.mKeyguardDelegate = new KeyguardServiceDelegate(this.mContext, new KeyguardStateMonitor.StateCallback() { // from class: com.android.server.policy.PhoneWindowManager.6
            @Override // com.android.server.policy.keyguard.KeyguardStateMonitor.StateCallback
            public void onTrustedChanged() {
                PhoneWindowManager.this.mWindowManagerFuncs.notifyKeyguardTrustedChanged();
            }

            @Override // com.android.server.policy.keyguard.KeyguardStateMonitor.StateCallback
            public void onShowingChanged() {
                PhoneWindowManager.this.mWindowManagerFuncs.onKeyguardShowingAndNotOccludedChanged();
            }
        });
        initKeyCombinationRules();
        initSingleKeyGestureRules();
        this.mSideFpsEventHandler = new SideFpsEventHandler(this.mContext, this.mHandler, this.mPowerManager);
    }

    public final void initKeyCombinationRules() {
        this.mKeyCombinationManager = new KeyCombinationManager(this.mHandler);
        if (this.mContext.getResources().getBoolean(17891666)) {
            this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(25, 26) { // from class: com.android.server.policy.PhoneWindowManager.7
                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void execute() {
                    PhoneWindowManager.this.mPowerKeyHandled = true;
                    PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                    phoneWindowManager.interceptScreenshotChord(1, phoneWindowManager.getScreenshotChordLongPressDelay());
                }

                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void cancel() {
                    PhoneWindowManager.this.cancelPendingScreenshotChordAction();
                }
            });
            if (this.mHasFeatureWatch) {
                this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(26, 264) { // from class: com.android.server.policy.PhoneWindowManager.8
                    @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                    public void execute() {
                        PhoneWindowManager.this.mPowerKeyHandled = true;
                        PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                        phoneWindowManager.interceptScreenshotChord(1, phoneWindowManager.getScreenshotChordLongPressDelay());
                    }

                    @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                    public void cancel() {
                        PhoneWindowManager.this.cancelPendingScreenshotChordAction();
                    }
                });
            }
        }
        this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(25, 24) { // from class: com.android.server.policy.PhoneWindowManager.9
            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public boolean preCondition() {
                return PhoneWindowManager.this.mAccessibilityShortcutController.isAccessibilityShortcutAvailable(PhoneWindowManager.this.isKeyguardLocked());
            }

            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public void execute() {
                PhoneWindowManager.this.interceptAccessibilityShortcutChord();
            }

            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public void cancel() {
                PhoneWindowManager.this.cancelPendingAccessibilityShortcutAction();
            }
        });
        this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(24, 26) { // from class: com.android.server.policy.PhoneWindowManager.10
            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public boolean preCondition() {
                PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                return (phoneWindowManager.mPowerVolUpBehavior == 1 && phoneWindowManager.mRingerToggleChord == 0) ? false : true;
            }

            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public void execute() {
                PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                int i = phoneWindowManager.mPowerVolUpBehavior;
                if (i == 1) {
                    phoneWindowManager.interceptRingerToggleChord();
                    PhoneWindowManager.this.mPowerKeyHandled = true;
                } else if (i != 2) {
                } else {
                    phoneWindowManager.performHapticFeedback(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, false, "Power + Volume Up - Global Actions");
                    PhoneWindowManager.this.showGlobalActions();
                    PhoneWindowManager.this.mPowerKeyHandled = true;
                }
            }

            @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
            public void cancel() {
                PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                int i = phoneWindowManager.mPowerVolUpBehavior;
                if (i == 1) {
                    phoneWindowManager.cancelPendingRingerToggleChordAction();
                } else if (i != 2) {
                } else {
                    phoneWindowManager.cancelGlobalActionsAction();
                }
            }
        });
        if (this.mHasFeatureLeanback) {
            this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(4, 20) { // from class: com.android.server.policy.PhoneWindowManager.11
                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public long getKeyInterceptDelayMs() {
                    return 0L;
                }

                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void execute() {
                    PhoneWindowManager.this.mBackKeyHandled = true;
                    PhoneWindowManager.this.interceptAccessibilityGestureTv();
                }

                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void cancel() {
                    PhoneWindowManager.this.cancelAccessibilityGestureTv();
                }
            });
            this.mKeyCombinationManager.addRule(new KeyCombinationManager.TwoKeysCombinationRule(23, 4) { // from class: com.android.server.policy.PhoneWindowManager.12
                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public long getKeyInterceptDelayMs() {
                    return 0L;
                }

                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void execute() {
                    PhoneWindowManager.this.mBackKeyHandled = true;
                    PhoneWindowManager.this.interceptBugreportGestureTv();
                }

                @Override // com.android.server.policy.KeyCombinationManager.TwoKeysCombinationRule
                public void cancel() {
                    PhoneWindowManager.this.cancelBugreportGestureTv();
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public final class PowerKeyRule extends SingleKeyGestureDetector.SingleKeyRule {
        public PowerKeyRule() {
            super(26);
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public boolean supportLongPress() {
            return PhoneWindowManager.this.hasLongPressOnPowerBehavior();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public boolean supportVeryLongPress() {
            return PhoneWindowManager.this.hasVeryLongPressOnPowerBehavior();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public int getMaxMultiPressCount() {
            return PhoneWindowManager.this.getMaxMultiPressPowerCount();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onPress(long j) {
            PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
            phoneWindowManager.powerPress(j, 1, phoneWindowManager.mSingleKeyGestureDetector.beganFromNonInteractive());
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public long getLongPressTimeoutMs() {
            if (PhoneWindowManager.this.getResolvedLongPressOnPowerBehavior() == 5) {
                return PhoneWindowManager.this.mLongPressOnPowerAssistantTimeoutMs;
            }
            return super.getLongPressTimeoutMs();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onLongPress(long j) {
            if (PhoneWindowManager.this.mSingleKeyGestureDetector.beganFromNonInteractive() && !PhoneWindowManager.this.mSupportLongPressPowerWhenNonInteractive) {
                Slog.v(StartingSurfaceController.TAG, "Not support long press power when device is not interactive.");
            } else {
                PhoneWindowManager.this.powerLongPress(j);
            }
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onVeryLongPress(long j) {
            PhoneWindowManager.this.mActivityManagerInternal.prepareForPossibleShutdown();
            PhoneWindowManager.this.powerVeryLongPress();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onMultiPress(long j, int i) {
            PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
            phoneWindowManager.powerPress(j, i, phoneWindowManager.mSingleKeyGestureDetector.beganFromNonInteractive());
        }
    }

    /* loaded from: classes2.dex */
    public final class BackKeyRule extends SingleKeyGestureDetector.SingleKeyRule {
        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public int getMaxMultiPressCount() {
            return 1;
        }

        public BackKeyRule() {
            super(4);
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public boolean supportLongPress() {
            return PhoneWindowManager.this.hasLongPressOnBackBehavior();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onPress(long j) {
            PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
            phoneWindowManager.mBackKeyHandled = PhoneWindowManager.this.backKeyPress() | phoneWindowManager.mBackKeyHandled;
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onLongPress(long j) {
            PhoneWindowManager.this.backLongPress();
        }
    }

    /* loaded from: classes2.dex */
    public final class StemPrimaryKeyRule extends SingleKeyGestureDetector.SingleKeyRule {
        public StemPrimaryKeyRule() {
            super(264);
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public boolean supportLongPress() {
            return PhoneWindowManager.this.hasLongPressOnStemPrimaryBehavior();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public int getMaxMultiPressCount() {
            return PhoneWindowManager.this.getMaxMultiPressStemPrimaryCount();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onPress(long j) {
            PhoneWindowManager.this.stemPrimaryPress(1);
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onLongPress(long j) {
            PhoneWindowManager.this.stemPrimaryLongPress();
        }

        @Override // com.android.server.policy.SingleKeyGestureDetector.SingleKeyRule
        public void onMultiPress(long j, int i) {
            PhoneWindowManager.this.stemPrimaryPress(i);
        }
    }

    public final void initSingleKeyGestureRules() {
        SingleKeyGestureDetector singleKeyGestureDetector = SingleKeyGestureDetector.get(this.mContext);
        this.mSingleKeyGestureDetector = singleKeyGestureDetector;
        singleKeyGestureDetector.addRule(new PowerKeyRule());
        if (hasLongPressOnBackBehavior()) {
            this.mSingleKeyGestureDetector.addRule(new BackKeyRule());
        }
        if (hasStemPrimaryBehavior()) {
            this.mSingleKeyGestureDetector.addRule(new StemPrimaryKeyRule());
        }
    }

    public final void readConfigurationDependentBehaviors() {
        Resources resources = this.mContext.getResources();
        int integer = resources.getInteger(17694868);
        this.mLongPressOnHomeBehavior = integer;
        if (integer < 0 || integer > 3) {
            this.mLongPressOnHomeBehavior = 0;
        }
        int integer2 = resources.getInteger(17694828);
        this.mDoubleTapOnHomeBehavior = integer2;
        if (integer2 < 0 || integer2 > 2) {
            this.mDoubleTapOnHomeBehavior = 0;
        }
        this.mShortPressOnWindowBehavior = 0;
        if (this.mPackageManager.hasSystemFeature("android.software.picture_in_picture")) {
            this.mShortPressOnWindowBehavior = 1;
        }
        this.mShortPressOnStemPrimaryBehavior = this.mContext.getResources().getInteger(17694958);
        this.mLongPressOnStemPrimaryBehavior = this.mContext.getResources().getInteger(17694871);
        this.mDoublePressOnStemPrimaryBehavior = this.mContext.getResources().getInteger(17694827);
        this.mTriplePressOnStemPrimaryBehavior = this.mContext.getResources().getInteger(17694977);
    }

    public void updateSettings() {
        boolean z;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        synchronized (this.mLock) {
            this.mEndcallBehavior = Settings.System.getIntForUser(contentResolver, "end_button_behavior", 2, -2);
            this.mIncallPowerBehavior = Settings.Secure.getIntForUser(contentResolver, "incall_power_button_behavior", 1, -2);
            this.mIncallBackBehavior = Settings.Secure.getIntForUser(contentResolver, "incall_back_button_behavior", 0, -2);
            this.mSystemNavigationKeysEnabled = Settings.Secure.getIntForUser(contentResolver, "system_navigation_keys_enabled", 0, -2) == 1;
            this.mRingerToggleChord = Settings.Secure.getIntForUser(contentResolver, "volume_hush_gesture", 0, -2);
            this.mPowerButtonSuppressionDelayMillis = Settings.Global.getInt(contentResolver, "power_button_suppression_delay_after_gesture_wake", 800);
            if (!this.mContext.getResources().getBoolean(17891879)) {
                this.mRingerToggleChord = 0;
            }
            boolean z2 = Settings.Secure.getIntForUser(contentResolver, "wake_gesture_enabled", 0, -2) != 0;
            if (this.mWakeGestureEnabledSetting != z2) {
                this.mWakeGestureEnabledSetting = z2;
                updateWakeGestureListenerLp();
            }
            this.mLockScreenTimeout = Settings.System.getIntForUser(contentResolver, "screen_off_timeout", 0, -2);
            String stringForUser = Settings.Secure.getStringForUser(contentResolver, "default_input_method", -2);
            boolean z3 = stringForUser != null && stringForUser.length() > 0;
            if (this.mHasSoftInput != z3) {
                this.mHasSoftInput = z3;
                z = true;
            } else {
                z = false;
            }
            int i = Settings.Global.getInt(contentResolver, "power_button_long_press", this.mContext.getResources().getInteger(17694869));
            int i2 = Settings.Global.getInt(contentResolver, "power_button_very_long_press", this.mContext.getResources().getInteger(17694982));
            if (this.mLongPressOnPowerBehavior != i || this.mVeryLongPressOnPowerBehavior != i2) {
                this.mLongPressOnPowerBehavior = i;
                this.mVeryLongPressOnPowerBehavior = i2;
            }
            this.mLongPressOnPowerAssistantTimeoutMs = Settings.Global.getLong(this.mContext.getContentResolver(), "power_button_long_press_duration_ms", this.mContext.getResources().getInteger(17694870));
            this.mPowerVolUpBehavior = Settings.Global.getInt(contentResolver, "key_chord_power_volume_up", this.mContext.getResources().getInteger(17694855));
            boolean z4 = Settings.Secure.getIntForUser(contentResolver, "stylus_buttons_enabled", 1, -2) == 1;
            this.mStylusButtonsEnabled = z4;
            this.mInputManagerInternal.setStylusButtonMotionEventsEnabled(z4);
        }
        if (z) {
            updateRotation(true);
        }
    }

    public final DreamManagerInternal getDreamManagerInternal() {
        if (this.mDreamManagerInternal == null) {
            this.mDreamManagerInternal = (DreamManagerInternal) LocalServices.getService(DreamManagerInternal.class);
        }
        return this.mDreamManagerInternal;
    }

    public final void updateWakeGestureListenerLp() {
        if (shouldEnableWakeGestureLp()) {
            this.mWakeGestureListener.requestWakeUpTrigger();
        } else {
            this.mWakeGestureListener.cancelWakeUpTrigger();
        }
    }

    public final boolean shouldEnableWakeGestureLp() {
        return this.mWakeGestureEnabledSetting && !this.mDefaultDisplayPolicy.isAwake() && !(getLidBehavior() == 1 && this.mDefaultDisplayPolicy.getLidState() == 0) && this.mWakeGestureListener.isSupported();
    }

    /* JADX WARN: Removed duplicated region for block: B:76:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    @Override // com.android.server.policy.WindowManagerPolicy
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int checkAddPermission(int i, boolean z, String str, int[] iArr) {
        ApplicationInfo applicationInfo;
        int noteOpNoThrow;
        if (!z || this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0) {
            iArr[0] = -1;
            if ((i < 1 || i > 99) && ((i < 1000 || i > 1999) && (i < 2000 || i > 2999))) {
                return -10;
            }
            if (i < 2000 || i > 2999) {
                return 0;
            }
            if (!WindowManager.LayoutParams.isSystemAlertWindowType(i)) {
                if (i == 2005) {
                    iArr[0] = 45;
                    return 0;
                }
                if (i != 2011 && i != 2013 && i != 2024 && i != 2035 && i != 2037) {
                    switch (i) {
                        case 2030:
                        case 2031:
                        case 2032:
                            break;
                        default:
                            return this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0 ? 0 : -8;
                    }
                }
                return 0;
            }
            iArr[0] = 24;
            int callingUid = Binder.getCallingUid();
            if (UserHandle.getAppId(callingUid) == 1000) {
                return 0;
            }
            try {
                try {
                    applicationInfo = this.mPackageManager.getApplicationInfoAsUser(str, 0, UserHandle.getUserId(callingUid));
                } catch (PackageManager.NameNotFoundException unused) {
                    applicationInfo = null;
                    if (applicationInfo != null) {
                    }
                    if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0) {
                    }
                }
            } catch (PackageManager.NameNotFoundException unused2) {
            }
            if (applicationInfo != null || (i != 2038 && applicationInfo.targetSdkVersion >= 26)) {
                return this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") != 0 ? 0 : -8;
            } else if (this.mContext.checkCallingOrSelfPermission("android.permission.SYSTEM_APPLICATION_OVERLAY") == 0 || (noteOpNoThrow = this.mAppOpsManager.noteOpNoThrow(iArr[0], callingUid, str, (String) null, "check-add")) == 0 || noteOpNoThrow == 1) {
                return 0;
            } else {
                return noteOpNoThrow != 2 ? this.mContext.checkCallingOrSelfPermission("android.permission.SYSTEM_ALERT_WINDOW") == 0 ? 0 : -8 : applicationInfo.targetSdkVersion < 23 ? 0 : -8;
            }
        }
        return -8;
    }

    public void readLidState() {
        this.mDefaultDisplayPolicy.setLidState(this.mWindowManagerFuncs.getLidState());
    }

    public final void readCameraLensCoverState() {
        this.mCameraLensCoverState = this.mWindowManagerFuncs.getCameraLensCoverState();
    }

    public final boolean isHidden(int i) {
        int lidState = this.mDefaultDisplayPolicy.getLidState();
        return i != 1 ? i == 2 && lidState == 1 : lidState == 0;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void adjustConfigurationLw(Configuration configuration, int i, int i2) {
        this.mHaveBuiltInKeyboard = (i & 1) != 0;
        readConfigurationDependentBehaviors();
        readLidState();
        if (configuration.keyboard == 1 || (i == 1 && isHidden(this.mLidKeyboardAccessibility))) {
            configuration.hardKeyboardHidden = 2;
            if (!this.mHasSoftInput) {
                configuration.keyboardHidden = 2;
            }
        }
        if (configuration.navigation == 1 || (i2 == 1 && isHidden(this.mLidNavigationAccessibility))) {
            configuration.navigationHidden = 2;
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardHostWindow(WindowManager.LayoutParams layoutParams) {
        return layoutParams.type == 2040;
    }

    public static void awakenDreams() {
        IDreamManager dreamManager = getDreamManager();
        if (dreamManager != null) {
            try {
                dreamManager.awaken();
            } catch (RemoteException unused) {
            }
        }
    }

    public static IDreamManager getDreamManager() {
        return IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
    }

    public TelecomManager getTelecommService() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }

    public NotificationManager getNotificationService() {
        return (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
    }

    public static IAudioService getAudioService() {
        IAudioService asInterface = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
        if (asInterface == null) {
            Log.w(StartingSurfaceController.TAG, "Unable to find IAudioService interface.");
        }
        return asInterface;
    }

    public boolean keyguardOn() {
        return isKeyguardShowingAndNotOccluded() || inKeyguardRestrictedKeyInputMode();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public long interceptKeyBeforeDispatching(IBinder iBinder, KeyEvent keyEvent, int i) {
        boolean keyguardOn = keyguardOn();
        int keyCode = keyEvent.getKeyCode();
        int repeatCount = keyEvent.getRepeatCount();
        int metaState = keyEvent.getMetaState();
        int flags = keyEvent.getFlags();
        boolean z = keyEvent.getAction() == 0;
        boolean isCanceled = keyEvent.isCanceled();
        int displayId = keyEvent.getDisplayId();
        if (this.mKeyCombinationManager.isKeyConsumed(keyEvent)) {
            return -1L;
        }
        if ((flags & 1024) == 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long keyInterceptTimeout = this.mKeyCombinationManager.getKeyInterceptTimeout(keyCode);
            if (uptimeMillis < keyInterceptTimeout) {
                return keyInterceptTimeout - uptimeMillis;
            }
        }
        if (this.mPendingMetaAction && !KeyEvent.isMetaKey(keyCode)) {
            this.mPendingMetaAction = false;
        }
        if (this.mPendingCapsLockToggle && !KeyEvent.isMetaKey(keyCode) && !KeyEvent.isAltKey(keyCode)) {
            this.mPendingCapsLockToggle = false;
        }
        if (isUserSetupComplete() && !keyguardOn && this.mModifierShortcutManager.interceptKey(keyEvent)) {
            dismissKeyboardShortcutsMenu();
            this.mPendingMetaAction = false;
            this.mPendingCapsLockToggle = false;
            return -1L;
        }
        switch (keyCode) {
            case 3:
                return handleHomeShortcuts(displayId, iBinder, keyEvent);
            case 19:
                if (z && keyEvent.isMetaPressed() && keyEvent.isCtrlPressed() && repeatCount == 0) {
                    StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
                    if (statusBarManagerInternal != null) {
                        statusBarManagerInternal.goToFullscreenFromSplit();
                        return -1L;
                    }
                    return -1L;
                }
                break;
            case 21:
                if (z && keyEvent.isMetaPressed() && keyEvent.isCtrlPressed() && repeatCount == 0) {
                    enterStageSplitFromRunningApp(true);
                    return -1L;
                }
                break;
            case 22:
                if (z && keyEvent.isMetaPressed() && keyEvent.isCtrlPressed() && repeatCount == 0) {
                    enterStageSplitFromRunningApp(false);
                    return -1L;
                }
                break;
            case 24:
            case 25:
            case FrameworkStatsLog.f376xd07885aa /* 164 */:
                if (this.mUseTvRouting || this.mHandleVolumeKeysInWM) {
                    dispatchDirectAudioEvent(keyEvent);
                    return -1L;
                } else if (this.mDefaultDisplayPolicy.isPersistentVrModeEnabled()) {
                    InputDevice device = keyEvent.getDevice();
                    if (device == null || device.isExternal()) {
                        break;
                    } else {
                        return -1L;
                    }
                }
                break;
            case 29:
                if (z && keyEvent.isMetaPressed()) {
                    launchAssistAction("android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD", keyEvent.getDeviceId(), keyEvent.getEventTime(), 0);
                    return -1L;
                }
                break;
            case 36:
                if (keyEvent.isMetaPressed()) {
                    return handleHomeShortcuts(displayId, iBinder, keyEvent);
                }
                break;
            case 37:
                if (z && keyEvent.isMetaPressed()) {
                    showSystemSettings();
                    return -1L;
                }
                break;
            case 42:
                if (z && keyEvent.isMetaPressed()) {
                    toggleNotificationPanel();
                    return -1L;
                }
                break;
            case 47:
                if (z && keyEvent.isMetaPressed() && keyEvent.isCtrlPressed() && repeatCount == 0) {
                    interceptScreenshotChord(2, 0L);
                    return -1L;
                }
                break;
            case 48:
                if (z && keyEvent.isMetaPressed()) {
                    toggleTaskbar();
                    return -1L;
                }
                break;
            case 57:
            case 58:
                if (z) {
                    if (keyEvent.isMetaPressed()) {
                        this.mPendingCapsLockToggle = true;
                        this.mPendingMetaAction = false;
                        break;
                    } else {
                        this.mPendingCapsLockToggle = false;
                        break;
                    }
                } else {
                    int i2 = this.mRecentAppsHeldModifiers;
                    if (i2 != 0 && (i2 & metaState) == 0) {
                        this.mRecentAppsHeldModifiers = 0;
                        hideRecentApps(true, false);
                        return -1L;
                    } else if (this.mPendingCapsLockToggle) {
                        this.mInputManagerInternal.toggleCapsLock(keyEvent.getDeviceId());
                        this.mPendingCapsLockToggle = false;
                        return -1L;
                    }
                }
                break;
            case 61:
                if (z && keyEvent.isMetaPressed()) {
                    if (!keyguardOn && isUserSetupComplete()) {
                        showRecentApps(false);
                        return -1L;
                    }
                } else if (z && repeatCount == 0 && this.mRecentAppsHeldModifiers == 0 && !keyguardOn && isUserSetupComplete()) {
                    int modifiers = keyEvent.getModifiers() & (-194);
                    if (KeyEvent.metaStateHasModifiers(modifiers, 2)) {
                        this.mRecentAppsHeldModifiers = modifiers;
                        showRecentApps(true);
                        return -1L;
                    }
                }
                break;
            case 62:
                if ((458752 & metaState) == 0) {
                    return 0L;
                }
                if (z && repeatCount == 0) {
                    sendSwitchKeyboardLayout(keyEvent, (metaState & FrameworkStatsLog.f390xde8506f2) != 0 ? -1 : 1);
                    return -1L;
                }
                break;
            case 76:
                if (z && repeatCount == 0 && keyEvent.isMetaPressed() && !keyguardOn) {
                    toggleKeyboardShortcutsMenu(keyEvent.getDeviceId());
                    return -1L;
                }
                break;
            case 82:
                if (z && repeatCount == 0 && this.mEnableShiftMenuBugReports && (metaState & 1) == 1) {
                    this.mContext.sendOrderedBroadcastAsUser(new Intent("android.intent.action.BUG_REPORT"), UserHandle.CURRENT, null, null, null, 0, null, null);
                    return -1L;
                }
                break;
            case 83:
                if (!z) {
                    toggleNotificationPanel();
                }
                return -1L;
            case 84:
                if (z && repeatCount == 0 && !keyguardOn() && this.mSearchKeyBehavior == 1) {
                    launchTargetSearchActivity();
                    return -1L;
                }
                break;
            case 117:
            case 118:
                if (z) {
                    if (keyEvent.isAltPressed()) {
                        this.mPendingCapsLockToggle = true;
                        this.mPendingMetaAction = false;
                        return -1L;
                    }
                    this.mPendingCapsLockToggle = false;
                    this.mPendingMetaAction = true;
                    return -1L;
                } else if (this.mPendingCapsLockToggle) {
                    this.mInputManagerInternal.toggleCapsLock(keyEvent.getDeviceId());
                    this.mPendingCapsLockToggle = false;
                    return -1L;
                } else if (this.mPendingMetaAction) {
                    if (!isCanceled) {
                        launchAllAppsViaA11y();
                    }
                    this.mPendingMetaAction = false;
                    return -1L;
                } else {
                    return -1L;
                }
            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__CREDENTIAL_MANAGEMENT_APP_REMOVED /* 187 */:
                if (keyguardOn) {
                    return -1L;
                }
                if (z && repeatCount == 0) {
                    preloadRecentApps();
                    return -1L;
                } else if (z) {
                    return -1L;
                } else {
                    toggleRecentApps();
                    return -1L;
                }
            case 219:
                Slog.wtf(StartingSurfaceController.TAG, "KEYCODE_ASSIST should be handled in interceptKeyBeforeQueueing");
                return -1L;
            case 220:
            case 221:
                if (z) {
                    int i3 = keyCode != 221 ? -1 : 1;
                    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -3) != 0) {
                        Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -3);
                    }
                    int i4 = displayId >= 0 ? displayId : 0;
                    this.mDisplayManager.setBrightness(i4, MathUtils.constrain(BrightnessUtils.convertGammaToLinear(BrightnessUtils.convertLinearToGamma(this.mDisplayManager.getBrightness(i4)) + (i3 * 0.1f)), 0.0f, 1.0f));
                    startActivityAsUser(new Intent("com.android.intent.action.SHOW_BRIGHTNESS_DIALOG"), UserHandle.CURRENT_OR_SELF);
                    return -1L;
                }
                return -1L;
            case 231:
                Slog.wtf(StartingSurfaceController.TAG, "KEYCODE_VOICE_ASSIST should be handled in interceptKeyBeforeQueueing");
                return -1L;
            case 284:
                if (!z) {
                    this.mHandler.removeMessages(22);
                    Message obtainMessage = this.mHandler.obtainMessage(22);
                    obtainMessage.setAsynchronous(true);
                    obtainMessage.sendToTarget();
                }
                return -1L;
            case 289:
            case 290:
            case 291:
            case 292:
            case 293:
            case 294:
            case 295:
            case 296:
            case 297:
            case FrameworkStatsLog.BLOB_COMMITTED /* 298 */:
            case FrameworkStatsLog.BLOB_LEASED /* 299 */:
            case 300:
            case FrameworkStatsLog.f60xe9b52732 /* 301 */:
            case FrameworkStatsLog.f61x1c64b730 /* 302 */:
            case FrameworkStatsLog.f109x2b18bc4b /* 303 */:
            case FrameworkStatsLog.f83x3da64833 /* 304 */:
                Slog.wtf(StartingSurfaceController.TAG, "KEYCODE_APP_X should be handled in interceptKeyBeforeQueueing");
                return -1L;
            case 305:
                if (z) {
                    this.mInputManagerInternal.decrementKeyboardBacklight(keyEvent.getDeviceId());
                }
                return -1L;
            case 306:
                if (z) {
                    this.mInputManagerInternal.incrementKeyboardBacklight(keyEvent.getDeviceId());
                }
                return -1L;
            case FrameworkStatsLog.f74x6a63db72 /* 307 */:
                return -1L;
            case 308:
            case 309:
            case 310:
            case FrameworkStatsLog.f93x9057b857 /* 311 */:
                Slog.wtf(StartingSurfaceController.TAG, "KEYCODE_STYLUS_BUTTON_* should be handled in interceptKeyBeforeQueueing");
                return -1L;
            case FrameworkStatsLog.f85x84af90cd /* 312 */:
                if (z && repeatCount == 0) {
                    showRecentApps(false);
                    return -1L;
                }
                return -1L;
        }
        return (!(isValidGlobalKey(keyCode) && this.mGlobalKeyManager.handleGlobalKey(this.mContext, keyCode, keyEvent)) && (65536 & metaState) == 0) ? 0L : -1L;
    }

    public final int handleHomeShortcuts(int i, IBinder iBinder, KeyEvent keyEvent) {
        DisplayHomeButtonHandler displayHomeButtonHandler = this.mDisplayHomeButtonHandlers.get(i);
        if (displayHomeButtonHandler == null) {
            displayHomeButtonHandler = new DisplayHomeButtonHandler(i);
            this.mDisplayHomeButtonHandlers.put(i, displayHomeButtonHandler);
        }
        return displayHomeButtonHandler.handleHomeButton(iBinder, keyEvent);
    }

    public final void toggleMicrophoneMuteFromKey() {
        if (this.mSensorPrivacyManager.supportsSensorToggle(1, 1)) {
            boolean isSensorPrivacyEnabled = this.mSensorPrivacyManager.isSensorPrivacyEnabled(1, 1);
            this.mSensorPrivacyManager.setSensorPrivacy(1, !isSensorPrivacyEnabled);
            Toast.makeText(this.mContext, UiThread.get().getLooper(), this.mContext.getString(isSensorPrivacyEnabled ? 17040771 : 17040770), 0).show();
        }
    }

    public final void interceptBugreportGestureTv() {
        this.mHandler.removeMessages(18);
        Message obtain = Message.obtain(this.mHandler, 18);
        obtain.setAsynchronous(true);
        this.mHandler.sendMessageDelayed(obtain, 1000L);
    }

    public final void cancelBugreportGestureTv() {
        this.mHandler.removeMessages(18);
    }

    public final void interceptAccessibilityGestureTv() {
        this.mHandler.removeMessages(19);
        Message obtain = Message.obtain(this.mHandler, 19);
        obtain.setAsynchronous(true);
        this.mHandler.sendMessageDelayed(obtain, getAccessibilityShortcutTimeout());
    }

    public final void cancelAccessibilityGestureTv() {
        this.mHandler.removeMessages(19);
    }

    public final void requestBugreportForTv() {
        if ("1".equals(SystemProperties.get("ro.debuggable")) || Settings.Global.getInt(this.mContext.getContentResolver(), "development_settings_enabled", 0) == 1) {
            try {
                if (ActivityManager.getService().launchBugReportHandlerApp()) {
                    return;
                }
                ActivityManager.getService().requestInteractiveBugReport();
            } catch (RemoteException e) {
                Slog.e(StartingSurfaceController.TAG, "Error taking bugreport", e);
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public KeyEvent dispatchUnhandledKey(IBinder iBinder, KeyEvent keyEvent, int i) {
        KeyCharacterMap.FallbackAction fallbackAction;
        KeyEvent keyEvent2 = null;
        if (interceptUnhandledKey(keyEvent)) {
            return null;
        }
        if ((keyEvent.getFlags() & 1024) == 0) {
            KeyCharacterMap keyCharacterMap = keyEvent.getKeyCharacterMap();
            int keyCode = keyEvent.getKeyCode();
            int metaState = keyEvent.getMetaState();
            boolean z = keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0;
            if (z) {
                fallbackAction = keyCharacterMap.getFallbackAction(keyCode, metaState);
            } else {
                fallbackAction = this.mFallbackActions.get(keyCode);
            }
            if (fallbackAction != null) {
                KeyEvent obtain = KeyEvent.obtain(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), fallbackAction.keyCode, keyEvent.getRepeatCount(), fallbackAction.metaState, keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags() | 1024, keyEvent.getSource(), keyEvent.getDisplayId(), null);
                if (interceptFallback(iBinder, obtain, i)) {
                    keyEvent2 = obtain;
                } else {
                    obtain.recycle();
                }
                if (z) {
                    this.mFallbackActions.put(keyCode, fallbackAction);
                } else if (keyEvent.getAction() == 1) {
                    this.mFallbackActions.remove(keyCode);
                    fallbackAction.recycle();
                }
            }
        }
        return keyEvent2;
    }

    public final boolean interceptUnhandledKey(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        int repeatCount = keyEvent.getRepeatCount();
        boolean z = keyEvent.getAction() == 0;
        int modifiers = keyEvent.getModifiers();
        if (keyCode != 54) {
            if (keyCode != 62) {
                if (keyCode == 120) {
                    if (z && repeatCount == 0) {
                        interceptScreenshotChord(2, 0L);
                    }
                    return true;
                }
            } else if (z && repeatCount == 0 && KeyEvent.metaStateHasModifiers(modifiers & (-194), IInstalld.FLAG_USE_QUOTA)) {
                sendSwitchKeyboardLayout(keyEvent, (modifiers & FrameworkStatsLog.f390xde8506f2) != 0 ? -1 : 1);
                return true;
            }
        } else if (z && KeyEvent.metaStateHasModifiers(modifiers, 4098) && this.mAccessibilityShortcutController.isAccessibilityShortcutAvailable(isKeyguardLocked())) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(17));
            return true;
        }
        return false;
    }

    public final void sendSwitchKeyboardLayout(KeyEvent keyEvent, int i) {
        this.mHandler.obtainMessage(25, keyEvent.getDeviceId(), i).sendToTarget();
    }

    public final void handleSwitchKeyboardLayout(int i, int i2) {
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_new_keyboard_ui")) {
            InputMethodManagerInternal.get().switchKeyboardLayout(i2);
        } else {
            this.mWindowManagerFuncs.switchKeyboardLayout(i, i2);
        }
    }

    public final boolean interceptFallback(IBinder iBinder, KeyEvent keyEvent, int i) {
        return ((interceptKeyBeforeQueueing(keyEvent, i) & 1) == 0 || interceptKeyBeforeDispatching(iBinder, keyEvent, i) != 0 || interceptUnhandledKey(keyEvent)) ? false : true;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setTopFocusedDisplay(int i) {
        this.mTopFocusedDisplayId = i;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void registerDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.registerDisplayFoldListener(iDisplayFoldListener);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void unregisterDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.unregisterDisplayFoldListener(iDisplayFoldListener);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setOverrideFoldedArea(Rect rect) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.setOverrideFoldedArea(rect);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public Rect getFoldedArea() {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            return displayFoldController.getFoldedArea();
        }
        return new Rect();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void onDefaultDisplayFocusChangedLw(WindowManagerPolicy.WindowState windowState) {
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.onDefaultDisplayFocusChanged(windowState != null ? windowState.getOwningPackage() : null);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void registerShortcutKey(long j, IShortcutService iShortcutService) throws RemoteException {
        synchronized (this.mLock) {
            this.mModifierShortcutManager.registerShortcutKey(j, iShortcutService);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void onKeyguardOccludedChangedLw(boolean z, boolean z2) {
        if (this.mKeyguardDelegate != null && z2) {
            this.mPendingKeyguardOccluded = z;
            this.mKeyguardOccludedChanged = true;
            return;
        }
        setKeyguardOccludedLw(z, true);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public int applyKeyguardOcclusionChange(boolean z) {
        return (this.mKeyguardOccludedChanged && setKeyguardOccludedLw(this.mPendingKeyguardOccluded, z)) ? 5 : 0;
    }

    public final int handleTransitionForKeyguardLw(boolean z, boolean z2) {
        int applyKeyguardOcclusionChange = applyKeyguardOcclusionChange(z2);
        if (applyKeyguardOcclusionChange != 0) {
            return applyKeyguardOcclusionChange;
        }
        if (z) {
            startKeyguardExitAnimation(SystemClock.uptimeMillis());
            return 0;
        }
        return 0;
    }

    public final void launchAssistAction(String str, int i, long j, int i2) {
        sendCloseSystemWindows("assist");
        if (isUserSetupComplete()) {
            Bundle bundle = new Bundle();
            if (i > Integer.MIN_VALUE) {
                bundle.putInt("android.intent.extra.ASSIST_INPUT_DEVICE_ID", i);
            }
            if (str != null) {
                bundle.putBoolean(str, true);
            }
            bundle.putLong("android.intent.extra.TIME", j);
            bundle.putInt("invocation_type", i2);
            ((SearchManager) this.mContext.createContextAsUser(UserHandle.of(this.mCurrentUserId), 0).getSystemService("search")).launchAssist(bundle);
        }
    }

    public final void launchVoiceAssist(boolean z) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (!(keyguardServiceDelegate != null && keyguardServiceDelegate.isShowing())) {
            if (this.mHasFeatureWatch && isInRetailMode()) {
                launchRetailVoiceAssist(z);
                return;
            } else {
                startVoiceAssistIntent(z);
                return;
            }
        }
        this.mKeyguardDelegate.dismissKeyguardToLaunch(new Intent("android.intent.action.VOICE_ASSIST"));
    }

    public final void launchRetailVoiceAssist(boolean z) {
        Intent intent = new Intent("android.intent.action.VOICE_ASSIST_RETAIL");
        ResolveInfo resolveActivity = this.mContext.getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity != null) {
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            startActivityAsUser(intent, null, UserHandle.CURRENT_OR_SELF, z);
            return;
        }
        Slog.w(StartingSurfaceController.TAG, "Couldn't find an app to process android.intent.action.VOICE_ASSIST_RETAIL. Fall back to start android.intent.action.VOICE_ASSIST");
        startVoiceAssistIntent(z);
    }

    public final void startVoiceAssistIntent(boolean z) {
        startActivityAsUser(new Intent("android.intent.action.VOICE_ASSIST"), null, UserHandle.CURRENT_OR_SELF, z);
    }

    public final boolean isInRetailMode() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_demo_mode", 0) == 1;
    }

    public final void startActivityAsUser(Intent intent, UserHandle userHandle) {
        startActivityAsUser(intent, null, userHandle);
    }

    public final void startActivityAsUser(Intent intent, Bundle bundle, UserHandle userHandle) {
        startActivityAsUser(intent, bundle, userHandle, false);
    }

    public final void startActivityAsUser(Intent intent, Bundle bundle, UserHandle userHandle, boolean z) {
        if (z || isUserSetupComplete()) {
            this.mContext.startActivityAsUser(intent, bundle, userHandle);
            return;
        }
        Slog.i(StartingSurfaceController.TAG, "Not starting activity because user setup is in progress: " + intent);
    }

    public final void preloadRecentApps() {
        this.mPreloadedRecentApps = true;
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.preloadRecentApps();
        }
    }

    public final void cancelPreloadRecentApps() {
        if (this.mPreloadedRecentApps) {
            this.mPreloadedRecentApps = false;
            StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.cancelPreloadRecentApps();
            }
        }
    }

    public final void toggleTaskbar() {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.toggleTaskbar();
        }
    }

    public final void toggleRecentApps() {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.toggleRecentApps();
        }
    }

    public final void showRecentApps(boolean z) {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.showRecentApps(z);
        }
    }

    public final void toggleKeyboardShortcutsMenu(int i) {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.toggleKeyboardShortcutsMenu(i);
        }
    }

    public final void dismissKeyboardShortcutsMenu() {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.dismissKeyboardShortcutsMenu();
        }
    }

    public final void hideRecentApps(boolean z, boolean z2) {
        this.mPreloadedRecentApps = false;
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.hideRecentApps(z, z2);
        }
    }

    public final void enterStageSplitFromRunningApp(boolean z) {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.enterStageSplitFromRunningApp(z);
        }
    }

    public void launchHomeFromHotKey(int i) {
        launchHomeFromHotKey(i, true, true);
    }

    public void launchHomeFromHotKey(final int i, final boolean z, boolean z2) {
        if (z2) {
            if (isKeyguardShowingAndNotOccluded()) {
                return;
            }
            if (!isKeyguardOccluded() && this.mKeyguardDelegate.isInputRestricted()) {
                this.mKeyguardDelegate.verifyUnlock(new WindowManagerPolicy.OnKeyguardExitResult() { // from class: com.android.server.policy.PhoneWindowManager.13
                    @Override // com.android.server.policy.WindowManagerPolicy.OnKeyguardExitResult
                    public void onKeyguardExitResult(boolean z3) {
                        if (z3) {
                            long clearCallingIdentity = Binder.clearCallingIdentity();
                            try {
                                PhoneWindowManager.this.startDockOrHome(i, true, z);
                            } finally {
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                            }
                        }
                    }
                });
                return;
            }
        }
        if (this.mRecentsVisible) {
            try {
                ActivityManager.getService().stopAppSwitches();
            } catch (RemoteException unused) {
            }
            if (z) {
                awakenDreams();
            }
            hideRecentApps(false, true);
            return;
        }
        startDockOrHome(i, true, z);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setRecentsVisibilityLw(boolean z) {
        this.mRecentsVisible = z;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setPipVisibilityLw(boolean z) {
        this.mPictureInPictureVisible = z;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setNavBarVirtualKeyHapticFeedbackEnabledLw(boolean z) {
        this.mNavBarVirtualKeyHapticFeedbackEnabled = z;
    }

    public final boolean setKeyguardOccludedLw(boolean z, boolean z2) {
        this.mKeyguardOccludedChanged = false;
        if (isKeyguardOccluded() == z) {
            return false;
        }
        this.mKeyguardDelegate.setOccluded(z, z2);
        return this.mKeyguardDelegate.isShowing();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void notifyLidSwitchChanged(long j, boolean z) {
        if (z == this.mDefaultDisplayPolicy.getLidState()) {
            return;
        }
        this.mDefaultDisplayPolicy.setLidState(z ? 1 : 0);
        applyLidSwitchState();
        updateRotation(true);
        if (z) {
            wakeUp(SystemClock.uptimeMillis(), this.mAllowTheaterModeWakeFromLidSwitch, 9, "android.policy:LID");
        } else if (getLidBehavior() != 1) {
            this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void notifyCameraLensCoverSwitchChanged(long j, boolean z) {
        Intent intent;
        if (this.mCameraLensCoverState != z && this.mContext.getResources().getBoolean(17891717)) {
            if (this.mCameraLensCoverState == 1 && !z) {
                KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                if (keyguardServiceDelegate == null ? false : keyguardServiceDelegate.isShowing()) {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE");
                } else {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
                }
                wakeUp(j / 1000000, this.mAllowTheaterModeWakeFromCameraLens, 5, "android.policy:CAMERA_COVER");
                startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
            }
            this.mCameraLensCoverState = z ? 1 : 0;
        }
    }

    public void initializeHdmiState() {
        int allowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        try {
            initializeHdmiStateInternal();
        } finally {
            StrictMode.setThreadPolicyMask(allowThreadDiskReadsMask);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0073, code lost:
        if (r2 == null) goto L18;
     */
    /* JADX WARN: Removed duplicated region for block: B:45:0x007a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void initializeHdmiStateInternal() {
        NumberFormatException e;
        IOException e2;
        FileReader fileReader = null;
        boolean z = false;
        if (new File("/sys/devices/virtual/switch/hdmi/state").exists()) {
            FileReader fileReader2 = this.mHDMIObserver;
            fileReader2.startObserving("DEVPATH=/devices/virtual/switch/hdmi");
            try {
                try {
                    fileReader2 = new FileReader("/sys/class/switch/hdmi/state");
                    try {
                        char[] cArr = new char[15];
                        int read = fileReader2.read(cArr);
                        if (read > 1) {
                            if (Integer.parseInt(new String(cArr, 0, read - 1)) != 0) {
                                z = true;
                            }
                        }
                    } catch (IOException e3) {
                        e2 = e3;
                        Slog.w(StartingSurfaceController.TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + e2);
                    } catch (NumberFormatException e4) {
                        e = e4;
                        Slog.w(StartingSurfaceController.TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + e);
                        if (fileReader2 != null) {
                            fileReader2.close();
                        }
                        this.mDefaultDisplayPolicy.setHdmiPlugged(z, true);
                    }
                } catch (Throwable th) {
                    th = th;
                    fileReader = fileReader2;
                    if (fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (IOException unused) {
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e2 = e5;
                fileReader2 = null;
            } catch (NumberFormatException e6) {
                e = e6;
                fileReader2 = null;
            } catch (Throwable th2) {
                th = th2;
                if (fileReader != null) {
                }
                throw th;
            }
            try {
                fileReader2.close();
            } catch (IOException unused2) {
            }
        } else {
            List<ExtconUEventObserver.ExtconInfo> extconInfoForTypes = ExtconUEventObserver.ExtconInfo.getExtconInfoForTypes(new String[]{"HDMI"});
            if (!extconInfoForTypes.isEmpty()) {
                HdmiVideoExtconUEventObserver hdmiVideoExtconUEventObserver = new HdmiVideoExtconUEventObserver();
                z = hdmiVideoExtconUEventObserver.init(extconInfoForTypes.get(0));
                this.mHDMIObserver = hdmiVideoExtconUEventObserver;
            }
        }
        this.mDefaultDisplayPolicy.setHdmiPlugged(z, true);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x0396, code lost:
        if (r30.mBackKeyHandled != false) goto L133;
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:101:0x013f  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x0142  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x037f  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:271:0x03a5  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x03ac  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0125 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0132  */
    @Override // com.android.server.policy.WindowManagerPolicy
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i) {
        int i2;
        boolean z;
        TelecomManager telecommService;
        int i3;
        int i4;
        boolean z2;
        HdmiControl hdmiControl;
        int keyCode = keyEvent.getKeyCode();
        boolean z3 = keyEvent.getAction() == 0;
        boolean z4 = (i & 1) != 0 || keyEvent.isWakeKey();
        if (!this.mSystemBooted) {
            if (z3 && (keyCode == 26 || keyCode == 177)) {
                wakeUpFromPowerKey(keyEvent.getDownTime());
            } else if (!z3 || ((!z4 && keyCode != 224) || !isWakeKeyWhenScreenOff(keyCode))) {
                z2 = false;
                if (z2 && (hdmiControl = getHdmiControl()) != null) {
                    hdmiControl.turnOnTv();
                }
                return 0;
            } else {
                wakeUpFromWakeKey(keyEvent);
            }
            z2 = true;
            if (z2) {
                hdmiControl.turnOnTv();
            }
            return 0;
        }
        boolean z5 = (536870912 & i) != 0;
        boolean isCanceled = keyEvent.isCanceled();
        int displayId = keyEvent.getDisplayId();
        boolean z6 = (16777216 & i) != 0;
        if (!z5 && (!z6 || z4)) {
            if (shouldDispatchInputWhenNonInteractive(displayId, keyCode)) {
                this.mPendingWakeKey = -1;
                i2 = 1;
                if (!isValidGlobalKey(keyCode)) {
                }
                HdmiControlManager hdmiControlManager = getHdmiControlManager();
                if (keyCode != 177) {
                }
                if (z5) {
                }
                if ((keyEvent.getFlags() & 1024) == 0) {
                }
                if (z3) {
                }
                if (keyCode == 4) {
                }
                if (z) {
                }
                if (z4) {
                }
                if ((i2 & 1) != 0) {
                }
                return i2;
            }
            if (z4 && (!z3 || !isWakeKeyWhenScreenOff(keyCode))) {
                z4 = false;
            }
            if (z4 && z3) {
                this.mPendingWakeKey = keyCode;
            }
            i2 = 0;
            if (!isValidGlobalKey(keyCode)) {
            }
            HdmiControlManager hdmiControlManager2 = getHdmiControlManager();
            if (keyCode != 177) {
            }
            if (z5) {
            }
            if ((keyEvent.getFlags() & 1024) == 0) {
            }
            if (z3) {
            }
            if (keyCode == 4) {
            }
            if (z) {
            }
            if (z4) {
            }
            if ((i2 & 1) != 0) {
            }
            return i2;
        } else if (z5) {
            int i5 = (keyCode != this.mPendingWakeKey || z3) ? 1 : 0;
            this.mPendingWakeKey = -1;
            i2 = i5;
            z4 = false;
            if (!isValidGlobalKey(keyCode) && this.mGlobalKeyManager.shouldHandleGlobalKey(keyCode)) {
                if (!z5 && z4 && z3 && this.mGlobalKeyManager.shouldDispatchFromNonInteractive(keyCode)) {
                    this.mGlobalKeyManager.setBeganFromNonInteractive();
                    this.mPendingWakeKey = -1;
                    i4 = 1;
                } else {
                    i4 = i2;
                }
                if (z4) {
                    wakeUpFromWakeKey(keyEvent);
                }
                return i4;
            }
            HdmiControlManager hdmiControlManager22 = getHdmiControlManager();
            if (keyCode != 177 && this.mHasFeatureLeanback && (hdmiControlManager22 == null || !hdmiControlManager22.shouldHandleTvPowerKey())) {
                return interceptKeyBeforeQueueing(KeyEvent.obtain(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), 26, keyEvent.getRepeatCount(), keyEvent.getMetaState(), keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags(), keyEvent.getSource(), keyEvent.getDisplayId(), null), i);
            }
            boolean z7 = !z5 && Display.isOnState(this.mDefaultDisplay.getState());
            if ((keyEvent.getFlags() & 1024) == 0) {
                handleKeyGesture(keyEvent, z7);
            }
            z = (z3 || (i & 2) == 0 || (((keyEvent.getFlags() & 64) == 0) && !this.mNavBarVirtualKeyHapticFeedbackEnabled) || keyEvent.getRepeatCount() != 0) ? false : true;
            if (keyCode == 4) {
                if (keyCode != 5) {
                    if (keyCode != 6) {
                        if (keyCode != 79 && keyCode != 130) {
                            if (keyCode != 164) {
                                if (keyCode != 171) {
                                    if (keyCode != 177) {
                                        if (keyCode == 219) {
                                            boolean z8 = keyEvent.getRepeatCount() > 0;
                                            if (z3 && !z8) {
                                                Message obtainMessage = this.mHandler.obtainMessage(23, keyEvent.getDeviceId(), 0, Long.valueOf(keyEvent.getEventTime()));
                                                obtainMessage.setAsynchronous(true);
                                                obtainMessage.sendToTarget();
                                            }
                                        } else if (keyCode != 231) {
                                            if (keyCode == 276) {
                                                i2 &= -2;
                                                if (!z3) {
                                                    this.mPowerManagerInternal.setUserInactiveOverrideFromWindowManager();
                                                }
                                            } else if (keyCode != 126 && keyCode != 127) {
                                                switch (keyCode) {
                                                    case 24:
                                                    case 25:
                                                        break;
                                                    case 26:
                                                        EventLogTags.writeInterceptPower(KeyEvent.actionToString(keyEvent.getAction()), this.mPowerKeyHandled ? 1 : 0, this.mSingleKeyGestureDetector.getKeyPressCounter(26));
                                                        i2 &= -2;
                                                        if (z3) {
                                                            interceptPowerKeyDown(keyEvent, z7);
                                                            break;
                                                        } else {
                                                            interceptPowerKeyUp(keyEvent, isCanceled);
                                                            break;
                                                        }
                                                    default:
                                                        switch (keyCode) {
                                                            case 85:
                                                            case 86:
                                                            case 87:
                                                            case 88:
                                                            case 89:
                                                            case 90:
                                                                break;
                                                            case 91:
                                                                i2 &= -2;
                                                                if (z3 && keyEvent.getRepeatCount() == 0) {
                                                                    toggleMicrophoneMuteFromKey();
                                                                    break;
                                                                }
                                                                break;
                                                            default:
                                                                switch (keyCode) {
                                                                    case 222:
                                                                        break;
                                                                    case FrameworkStatsLog.EXCLUSION_RECT_STATE_CHANGED /* 223 */:
                                                                        i2 &= -2;
                                                                        if (!this.mPowerManager.isInteractive()) {
                                                                            z = false;
                                                                        }
                                                                        if (z3) {
                                                                            sleepPress();
                                                                            break;
                                                                        } else {
                                                                            sleepRelease(keyEvent.getEventTime());
                                                                            break;
                                                                        }
                                                                    case 224:
                                                                        i2 &= -2;
                                                                        z4 = true;
                                                                        break;
                                                                    default:
                                                                        switch (keyCode) {
                                                                            case FrameworkStatsLog.TV_CAS_SESSION_OPEN_STATUS /* 280 */:
                                                                            case FrameworkStatsLog.ASSISTANT_INVOCATION_REPORTED /* 281 */:
                                                                            case FrameworkStatsLog.DISPLAY_WAKE_REPORTED /* 282 */:
                                                                            case 283:
                                                                                i2 &= -2;
                                                                                interceptSystemNavigationKey(keyEvent);
                                                                                break;
                                                                            default:
                                                                                switch (keyCode) {
                                                                                    default:
                                                                                        switch (keyCode) {
                                                                                            case 308:
                                                                                            case 309:
                                                                                            case 310:
                                                                                            case FrameworkStatsLog.f93x9057b857 /* 311 */:
                                                                                                if (z3 && this.mStylusButtonsEnabled) {
                                                                                                    sendSystemKeyToStatusBarAsync(keyCode);
                                                                                                }
                                                                                                break;
                                                                                        }
                                                                                    case 289:
                                                                                    case 290:
                                                                                    case 291:
                                                                                    case 292:
                                                                                    case 293:
                                                                                    case 294:
                                                                                    case 295:
                                                                                    case 296:
                                                                                    case 297:
                                                                                    case FrameworkStatsLog.BLOB_COMMITTED /* 298 */:
                                                                                    case FrameworkStatsLog.BLOB_LEASED /* 299 */:
                                                                                    case 300:
                                                                                    case FrameworkStatsLog.f60xe9b52732 /* 301 */:
                                                                                    case FrameworkStatsLog.f61x1c64b730 /* 302 */:
                                                                                    case FrameworkStatsLog.f109x2b18bc4b /* 303 */:
                                                                                    case FrameworkStatsLog.f83x3da64833 /* 304 */:
                                                                                        i2 &= -2;
                                                                                        break;
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                            }
                                        } else if (!z3) {
                                            this.mBroadcastWakeLock.acquire();
                                            Message obtainMessage2 = this.mHandler.obtainMessage(12);
                                            obtainMessage2.setAsynchronous(true);
                                            obtainMessage2.sendToTarget();
                                        }
                                        i2 &= -2;
                                    } else {
                                        i2 &= -2;
                                        if (z3 && hdmiControlManager22 != null) {
                                            hdmiControlManager22.toggleAndFollowTvPower();
                                        }
                                    }
                                    z4 = false;
                                } else if (this.mShortPressOnWindowBehavior == 1 && this.mPictureInPictureVisible) {
                                    if (!z3) {
                                        showPictureInPictureMenu(keyEvent);
                                    }
                                    i2 &= -2;
                                }
                            }
                            if (z3) {
                                sendSystemKeyToStatusBarAsync(keyEvent.getKeyCode());
                                NotificationManager notificationService = getNotificationService();
                                if (notificationService != null && !this.mHandleVolumeKeysInWM) {
                                    notificationService.silenceNotificationSound();
                                }
                                TelecomManager telecommService2 = getTelecommService();
                                if (telecommService2 != null && !this.mHandleVolumeKeysInWM && telecommService2.isRinging()) {
                                    Log.i(StartingSurfaceController.TAG, "interceptKeyBeforeQueueing: VOLUME key-down while ringing: Silence ringer!");
                                    telecommService2.silenceRinger();
                                    i2 &= -2;
                                } else {
                                    try {
                                        i3 = getAudioService().getMode();
                                    } catch (Exception e) {
                                        Log.e(StartingSurfaceController.TAG, "Error getting AudioService in interceptKeyBeforeQueueing.", e);
                                        i3 = 0;
                                    }
                                    if (((telecommService2 != null && telecommService2.isInCall()) || i3 == 3) && (i2 & 1) == 0) {
                                        MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(keyEvent, Integer.MIN_VALUE, false);
                                    }
                                }
                            }
                            if (this.mUseTvRouting || this.mHandleVolumeKeysInWM) {
                                i2 |= 1;
                            } else if ((i2 & 1) == 0) {
                                MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(keyEvent, Integer.MIN_VALUE, true);
                            }
                        }
                        if (MediaSessionLegacyHelper.getHelper(this.mContext).isGlobalPriorityActive()) {
                            i2 &= -2;
                        }
                        if ((i2 & 1) == 0) {
                            this.mBroadcastWakeLock.acquire();
                            Message obtainMessage3 = this.mHandler.obtainMessage(3, new KeyEvent(keyEvent));
                            obtainMessage3.setAsynchronous(true);
                            obtainMessage3.sendToTarget();
                        }
                    } else {
                        i2 &= -2;
                        if (z3) {
                            TelecomManager telecommService3 = getTelecommService();
                            boolean endCall = telecommService3 != null ? telecommService3.endCall() : false;
                            if (z5 && !endCall) {
                                this.mEndCallKeyHandled = false;
                                this.mHandler.postDelayed(this.mEndCallLongPress, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
                            } else {
                                this.mEndCallKeyHandled = true;
                            }
                        } else if (!this.mEndCallKeyHandled) {
                            this.mHandler.removeCallbacks(this.mEndCallLongPress);
                            if (!isCanceled && (((this.mEndcallBehavior & 1) == 0 || !goHome()) && (this.mEndcallBehavior & 2) != 0)) {
                                sleepDefaultDisplay(keyEvent.getEventTime(), 4, 0);
                                z4 = false;
                            }
                        }
                    }
                } else if (z3 && (telecommService = getTelecommService()) != null && telecommService.isRinging()) {
                    Log.i(StartingSurfaceController.TAG, "interceptKeyBeforeQueueing: CALL key-down while ringing: Answer the call!");
                    telecommService.acceptRingingCall();
                    i2 &= -2;
                }
            } else if (z3) {
                this.mBackKeyHandled = false;
            } else if (!hasLongPressOnBackBehavior()) {
                this.mBackKeyHandled |= backKeyPress();
            }
            if (z) {
                performHapticFeedback(1, false, "Virtual Key - Press");
            }
            if (z4) {
                wakeUpFromWakeKey(keyEvent);
            }
            if ((i2 & 1) != 0 && displayId != -1 && displayId != this.mTopFocusedDisplayId) {
                Log.i(StartingSurfaceController.TAG, "Attempting to move non-focused display " + displayId + " to top because a key is targeting it");
                this.mWindowManagerFuncs.moveDisplayToTopIfAllowed(displayId);
            }
            return i2;
        } else {
            z4 = false;
            i2 = 1;
            if (!isValidGlobalKey(keyCode)) {
            }
            HdmiControlManager hdmiControlManager222 = getHdmiControlManager();
            if (keyCode != 177) {
            }
            if (z5) {
            }
            if ((keyEvent.getFlags() & 1024) == 0) {
            }
            if (z3) {
            }
            if (keyCode == 4) {
            }
            if (z) {
            }
            if (z4) {
            }
            if ((i2 & 1) != 0) {
                Log.i(StartingSurfaceController.TAG, "Attempting to move non-focused display " + displayId + " to top because a key is targeting it");
                this.mWindowManagerFuncs.moveDisplayToTopIfAllowed(displayId);
            }
            return i2;
        }
    }

    public final void handleKeyGesture(KeyEvent keyEvent, boolean z) {
        if (this.mKeyCombinationManager.interceptKey(keyEvent, z)) {
            this.mSingleKeyGestureDetector.reset();
            return;
        }
        if (keyEvent.getKeyCode() == 26 && keyEvent.getAction() == 0) {
            this.mPowerKeyHandled = handleCameraGesture(keyEvent, z);
            if (this.mPowerKeyHandled) {
                this.mSingleKeyGestureDetector.reset();
                return;
            }
        }
        this.mSingleKeyGestureDetector.interceptKey(keyEvent, z);
    }

    public final boolean handleCameraGesture(KeyEvent keyEvent, boolean z) {
        if (this.mGestureLauncherService == null) {
            return false;
        }
        this.mCameraGestureTriggered = false;
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        boolean interceptPowerKeyDown = this.mGestureLauncherService.interceptPowerKeyDown(keyEvent, z, mutableBoolean);
        if (mutableBoolean.value) {
            this.mCameraGestureTriggered = true;
            if (this.mRequestedOrSleepingDefaultDisplay) {
                this.mCameraGestureTriggeredDuringGoingToSleep = true;
                wakeUp(SystemClock.uptimeMillis(), this.mAllowTheaterModeWakeFromPowerKey, 5, "android.policy:CAMERA_GESTURE_PREVENT_LOCK");
            }
            return true;
        }
        return interceptPowerKeyDown;
    }

    public final void interceptSystemNavigationKey(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1) {
            if (!(this.mAccessibilityManager.isEnabled() && this.mAccessibilityManager.sendFingerprintGesture(keyEvent.getKeyCode())) && this.mSystemNavigationKeysEnabled) {
                sendSystemKeyToStatusBarAsync(keyEvent.getKeyCode());
            }
        }
    }

    public final void sendSystemKeyToStatusBar(int i) {
        IStatusBarService statusBarService = getStatusBarService();
        if (statusBarService != null) {
            try {
                statusBarService.handleSystemKey(i);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void sendSystemKeyToStatusBarAsync(int i) {
        Message obtainMessage = this.mHandler.obtainMessage(21, i, 0);
        obtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(obtainMessage);
    }

    public final boolean isWakeKeyWhenScreenOff(int i) {
        if (i != 4) {
            if (i != 219) {
                switch (i) {
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                        return this.mWakeOnDpadKeyPress;
                    default:
                        return true;
                }
            }
            return this.mWakeOnAssistKeyPress;
        }
        return this.mWakeOnBackKeyPress;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public int interceptMotionBeforeQueueingNonInteractive(int i, long j, int i2) {
        int i3 = i2 & 1;
        if (i3 == 0 || !wakeUp(j / 1000000, this.mAllowTheaterModeWakeFromMotion, 7, "android.policy:MOTION")) {
            if (shouldDispatchInputWhenNonInteractive(i, 0)) {
                return 1;
            }
            if (isTheaterModeEnabled() && i3 != 0) {
                wakeUp(j / 1000000, this.mAllowTheaterModeWakeFromMotionWhenNotDreaming, 7, "android.policy:MOTION");
            }
            return 0;
        }
        return 0;
    }

    public final boolean shouldDispatchInputWhenNonInteractive(int i, int i2) {
        Display display;
        IDreamManager dreamManager;
        boolean z = i == 0 || i == -1;
        if (z) {
            display = this.mDefaultDisplay;
        } else {
            display = this.mDisplayManager.getDisplay(i);
        }
        boolean z2 = display == null || display.getState() == 1;
        if (!z2 || this.mHasFeatureWatch) {
            if (!isKeyguardShowingAndNotOccluded() || z2) {
                if ((!this.mHasFeatureWatch || (i2 != 4 && i2 != 264 && i2 != 265 && i2 != 266 && i2 != 267)) && z && (dreamManager = getDreamManager()) != null) {
                    try {
                        if (dreamManager.isDreaming()) {
                            return true;
                        }
                    } catch (RemoteException e) {
                        Slog.e(StartingSurfaceController.TAG, "RemoteException when checking if dreaming", e);
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public final void dispatchDirectAudioEvent(KeyEvent keyEvent) {
        HdmiAudioSystemClient audioSystemClient;
        HdmiControlManager hdmiControlManager = getHdmiControlManager();
        if (hdmiControlManager != null && !hdmiControlManager.getSystemAudioMode() && shouldCecAudioDeviceForwardVolumeKeysSystemAudioModeOff() && (audioSystemClient = hdmiControlManager.getAudioSystemClient()) != null) {
            audioSystemClient.sendKeyEvent(keyEvent.getKeyCode(), keyEvent.getAction() == 0);
            return;
        }
        try {
            getAudioService().handleVolumeKey(keyEvent, this.mUseTvRouting, this.mContext.getOpPackageName(), StartingSurfaceController.TAG);
        } catch (Exception e) {
            Log.e(StartingSurfaceController.TAG, "Error dispatching volume key in handleVolumeKey for event:" + keyEvent, e);
        }
    }

    public final HdmiControlManager getHdmiControlManager() {
        if (this.mHasFeatureHdmiCec) {
            return (HdmiControlManager) this.mContext.getSystemService(HdmiControlManager.class);
        }
        return null;
    }

    public final boolean shouldCecAudioDeviceForwardVolumeKeysSystemAudioModeOff() {
        return RoSystemProperties.CEC_AUDIO_DEVICE_FORWARD_VOLUME_KEYS_SYSTEM_AUDIO_MODE_OFF;
    }

    public void dispatchMediaKeyWithWakeLock(KeyEvent keyEvent) {
        if (this.mHavePendingMediaKeyRepeatWithWakeLock) {
            this.mHandler.removeMessages(4);
            this.mHavePendingMediaKeyRepeatWithWakeLock = false;
            this.mBroadcastWakeLock.release();
        }
        dispatchMediaKeyWithWakeLockToAudioService(keyEvent);
        if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
            this.mHavePendingMediaKeyRepeatWithWakeLock = true;
            Message obtainMessage = this.mHandler.obtainMessage(4, keyEvent);
            obtainMessage.setAsynchronous(true);
            this.mHandler.sendMessageDelayed(obtainMessage, ViewConfiguration.getKeyRepeatTimeout());
            return;
        }
        this.mBroadcastWakeLock.release();
    }

    public void dispatchMediaKeyRepeatWithWakeLock(KeyEvent keyEvent) {
        this.mHavePendingMediaKeyRepeatWithWakeLock = false;
        dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), 1, keyEvent.getFlags() | 128));
        this.mBroadcastWakeLock.release();
    }

    public void dispatchMediaKeyWithWakeLockToAudioService(KeyEvent keyEvent) {
        if (this.mActivityManagerInternal.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(this.mContext).sendMediaButtonEvent(keyEvent, true);
        }
    }

    public void launchVoiceAssistWithWakeLock() {
        Intent intent;
        sendCloseSystemWindows("assist");
        if (!keyguardOn()) {
            intent = new Intent("android.speech.action.WEB_SEARCH");
        } else {
            DeviceIdleManager deviceIdleManager = (DeviceIdleManager) this.mContext.getSystemService(DeviceIdleManager.class);
            if (deviceIdleManager != null) {
                deviceIdleManager.endIdle("voice-search");
            }
            intent = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
            intent.putExtra("android.speech.extras.EXTRA_SECURE", true);
        }
        startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
        this.mBroadcastWakeLock.release();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void startedGoingToSleep(int i) {
        this.mDeviceGoingToSleep = true;
        this.mRequestedOrSleepingDefaultDisplay = true;
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onStartedGoingToSleep(i);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void finishedGoingToSleep(int i) {
        EventLogTags.writeScreenToggled(0);
        MetricsLogger.histogram(this.mContext, "screen_timeout", this.mLockScreenTimeout / 1000);
        this.mDeviceGoingToSleep = false;
        this.mRequestedOrSleepingDefaultDisplay = false;
        this.mDefaultDisplayPolicy.setAwake(false);
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
            updateLockScreenTimeout();
        }
        this.mDefaultDisplayRotation.updateOrientationListener();
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onFinishedGoingToSleep(i, this.mCameraGestureTriggeredDuringGoingToSleep);
        }
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.finishedGoingToSleep();
        }
        this.mCameraGestureTriggeredDuringGoingToSleep = false;
        this.mCameraGestureTriggered = false;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void onPowerGroupWakefulnessChanged(int i, int i2, int i3, int i4) {
        KeyguardServiceDelegate keyguardServiceDelegate;
        if (i2 == i4 || i2 == 1 || i != 0 || (keyguardServiceDelegate = this.mKeyguardDelegate) == null) {
            return;
        }
        keyguardServiceDelegate.doKeyguardTimeout(null);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void startedWakingUp(int i) {
        EventLogTags.writeScreenToggled(1);
        this.mDefaultDisplayPolicy.setAwake(true);
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
            updateLockScreenTimeout();
        }
        this.mDefaultDisplayRotation.updateOrientationListener();
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onStartedWakingUp(i, this.mCameraGestureTriggered);
        }
        this.mCameraGestureTriggered = false;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void finishedWakingUp(int i) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onFinishedWakingUp();
        }
        DisplayFoldController displayFoldController = this.mDisplayFoldController;
        if (displayFoldController != null) {
            displayFoldController.finishedWakingUp();
        }
    }

    public final boolean shouldWakeUpWithHomeIntent() {
        return this.mWakeUpToLastStateTimeout > 0 && this.mPowerManagerInternal.getLastWakeup().sleepDurationRealtime > this.mWakeUpToLastStateTimeout;
    }

    public final void wakeUpFromPowerKey(long j) {
        if (wakeUp(j, this.mAllowTheaterModeWakeFromPowerKey, 1, "android.policy:POWER") && shouldWakeUpWithHomeIntent()) {
            startDockOrHome(0, false, true, PowerManager.wakeReasonToString(1));
        }
    }

    public final void wakeUpFromWakeKey(KeyEvent keyEvent) {
        if (wakeUp(keyEvent.getEventTime(), this.mAllowTheaterModeWakeFromKey, 6, "android.policy:KEY") && shouldWakeUpWithHomeIntent() && keyEvent.getKeyCode() == 3) {
            startDockOrHome(0, true, true, PowerManager.wakeReasonToString(6));
        }
    }

    public final boolean wakeUp(long j, boolean z, int i, String str) {
        boolean isTheaterModeEnabled = isTheaterModeEnabled();
        if (z || !isTheaterModeEnabled) {
            if (isTheaterModeEnabled) {
                Settings.Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", 0);
            }
            this.mPowerManager.wakeUp(j, i, str);
            return true;
        }
        return false;
    }

    public final void finishKeyguardDrawn() {
        if (this.mDefaultDisplayPolicy.finishKeyguardDrawn()) {
            synchronized (this.mLock) {
                if (this.mKeyguardDelegate != null) {
                    this.mHandler.removeMessages(6);
                }
            }
            Trace.asyncTraceBegin(32L, "waitForAllWindowsDrawn", 0);
            this.mWindowManagerInternal.waitForAllWindowsDrawn(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneWindowManager.this.lambda$finishKeyguardDrawn$1();
                }
            }, 1000L, -1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishKeyguardDrawn$1() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(7, -1, 0));
        Trace.asyncTraceEnd(32L, "waitForAllWindowsDrawn", 0);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void screenTurnedOff(int i) {
        if (i == 0) {
            updateScreenOffSleepToken(true);
            this.mRequestedOrSleepingDefaultDisplay = false;
            this.mDefaultDisplayPolicy.screenTurnedOff();
            synchronized (this.mLock) {
                KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                if (keyguardServiceDelegate != null) {
                    keyguardServiceDelegate.onScreenTurnedOff();
                }
            }
            this.mDefaultDisplayRotation.updateOrientationListener();
            reportScreenStateToVrManager(false);
        }
    }

    public final long getKeyguardDrawnTimeout() {
        return ((SystemServiceManager) LocalServices.getService(SystemServiceManager.class)).isBootCompleted() ? 1000L : 5000L;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void screenTurningOn(final int i, WindowManagerPolicy.ScreenOnListener screenOnListener) {
        if (i == 0) {
            Trace.asyncTraceBegin(32L, "screenTurningOn", 0);
            updateScreenOffSleepToken(false);
            this.mDefaultDisplayPolicy.screenTurnedOn(screenOnListener);
            this.mBootAnimationDismissable = false;
            synchronized (this.mLock) {
                KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
                if (keyguardServiceDelegate != null && keyguardServiceDelegate.hasKeyguard()) {
                    this.mHandler.removeMessages(6);
                    this.mHandler.sendEmptyMessageDelayed(6, getKeyguardDrawnTimeout());
                    this.mKeyguardDelegate.onScreenTurningOn(this.mKeyguardDrawnCallback);
                } else {
                    this.mHandler.sendEmptyMessage(5);
                }
            }
            return;
        }
        this.mScreenOnListeners.put(i, screenOnListener);
        Trace.asyncTraceBegin(32L, "waitForAllWindowsDrawn", 0);
        this.mWindowManagerInternal.waitForAllWindowsDrawn(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PhoneWindowManager.this.lambda$screenTurningOn$2(i);
            }
        }, 1000L, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$screenTurningOn$2(int i) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(7, i, 0));
        Trace.asyncTraceEnd(32L, "waitForAllWindowsDrawn", 0);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void screenTurnedOn(int i) {
        if (i != 0) {
            return;
        }
        synchronized (this.mLock) {
            KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
            if (keyguardServiceDelegate != null) {
                keyguardServiceDelegate.onScreenTurnedOn();
            }
        }
        reportScreenStateToVrManager(true);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void screenTurningOff(int i, WindowManagerPolicy.ScreenOffListener screenOffListener) {
        this.mWindowManagerFuncs.screenTurningOff(i, screenOffListener);
        if (i != 0) {
            return;
        }
        this.mRequestedOrSleepingDefaultDisplay = true;
        synchronized (this.mLock) {
            KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
            if (keyguardServiceDelegate != null) {
                keyguardServiceDelegate.onScreenTurningOff();
            }
        }
    }

    public final void reportScreenStateToVrManager(boolean z) {
        if (this.mVrManagerInternal == null) {
            return;
        }
        this.mVrManagerInternal.onScreenStateChanged(z);
    }

    public final void finishWindowsDrawn(int i) {
        if (i != 0 && i != -1) {
            WindowManagerPolicy.ScreenOnListener screenOnListener = (WindowManagerPolicy.ScreenOnListener) this.mScreenOnListeners.removeReturnOld(i);
            if (screenOnListener != null) {
                screenOnListener.onScreenOn();
            }
        } else if (this.mDefaultDisplayPolicy.finishWindowsDrawn()) {
            finishScreenTurningOn();
        }
    }

    public final void finishScreenTurningOn() {
        this.mDefaultDisplayRotation.updateOrientationListener();
        WindowManagerPolicy.ScreenOnListener screenOnListener = this.mDefaultDisplayPolicy.getScreenOnListener();
        if (this.mDefaultDisplayPolicy.finishScreenTurningOn()) {
            Trace.asyncTraceEnd(32L, "screenTurningOn", 0);
            enableScreen(screenOnListener, true);
        }
    }

    public final void enableScreen(WindowManagerPolicy.ScreenOnListener screenOnListener, boolean z) {
        boolean z2;
        boolean isAwake = this.mDefaultDisplayPolicy.isAwake();
        synchronized (this.mLock) {
            z2 = false;
            if (!this.mKeyguardDrawnOnce && isAwake) {
                this.mKeyguardDrawnOnce = true;
                if (this.mBootMessageNeedsHiding) {
                    this.mBootMessageNeedsHiding = false;
                    hideBootMessages();
                }
                z2 = true;
            }
        }
        if (z && screenOnListener != null) {
            screenOnListener.onScreenOn();
        }
        if (z2) {
            this.mWindowManagerFuncs.enableScreenIfNeeded();
        }
    }

    public final void handleHideBootMessage() {
        synchronized (this.mLock) {
            if (!this.mKeyguardDrawnOnce) {
                this.mBootMessageNeedsHiding = true;
                return;
            }
            ProgressDialog progressDialog = this.mBootMsgDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mBootMsgDialog = null;
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isScreenOn() {
        return this.mDefaultDisplayPolicy.isScreenOnEarly();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean okToAnimate(boolean z) {
        return (z || isScreenOn()) && !this.mDeviceGoingToSleep;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void enableKeyguard(boolean z) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.setKeyguardEnabled(z);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void exitKeyguardSecurely(WindowManagerPolicy.OnKeyguardExitResult onKeyguardExitResult) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.verifyUnlock(onKeyguardExitResult);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardShowing() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isShowing();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardShowingAndNotOccluded() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        return (keyguardServiceDelegate == null || !keyguardServiceDelegate.isShowing() || isKeyguardOccluded()) ? false : true;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardTrustedLw() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isTrusted();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardLocked() {
        return keyguardOn();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardSecure(int i) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isSecure(i);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardOccluded() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isOccluded();
    }

    public boolean inKeyguardRestrictedKeyInputMode() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate == null) {
            return false;
        }
        return keyguardServiceDelegate.isInputRestricted();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardUnoccluding() {
        return keyguardOn() && !this.mWindowManagerFuncs.isAppTransitionStateIdle();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void dismissKeyguardLw(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null && keyguardServiceDelegate.isShowing()) {
            this.mKeyguardDelegate.dismiss(iKeyguardDismissCallback, charSequence);
        } else if (iKeyguardDismissCallback != null) {
            try {
                iKeyguardDismissCallback.onDismissError();
            } catch (RemoteException e) {
                Slog.w(StartingSurfaceController.TAG, "Failed to call callback", e);
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isKeyguardDrawnLw() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mKeyguardDrawnOnce;
        }
        return z;
    }

    public void startKeyguardExitAnimation(long j) {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.startKeyguardExitAnimation(j);
        }
    }

    public void sendCloseSystemWindows() {
        PhoneWindow.sendCloseSystemWindows(this.mContext, (String) null);
    }

    public void sendCloseSystemWindows(String str) {
        PhoneWindow.sendCloseSystemWindows(this.mContext, str);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setSafeMode(boolean z) {
        this.mSafeMode = z;
        if (z) {
            performHapticFeedback(FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG, true, "Safe Mode Enabled");
        }
    }

    public static long[] getLongIntArray(Resources resources, int i) {
        return ArrayUtils.convertToLongArray(resources.getIntArray(i));
    }

    public final void bindKeyguard() {
        synchronized (this.mLock) {
            if (this.mKeyguardBound) {
                return;
            }
            this.mKeyguardBound = true;
            this.mKeyguardDelegate.bindService(this.mContext);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void onSystemUiStarted() {
        bindKeyguard();
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void systemReady() {
        this.mKeyguardDelegate.onSystemReady();
        this.mVrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        if (this.mVrManagerInternal != null) {
            this.mVrManagerInternal.addPersistentVrModeStateListener(this.mPersistentVrModeListener);
        }
        readCameraLensCoverState();
        updateUiMode();
        this.mDefaultDisplayRotation.updateOrientationListener();
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager.17
                @Override // java.lang.Runnable
                public void run() {
                    PhoneWindowManager.this.updateSettings();
                }
            });
            if (this.mSystemBooted) {
                this.mKeyguardDelegate.onBootCompleted();
            }
        }
        this.mAutofillManagerInternal = (AutofillManagerInternal) LocalServices.getService(AutofillManagerInternal.class);
        this.mGestureLauncherService = (GestureLauncherService) LocalServices.getService(GestureLauncherService.class);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void systemBooted() {
        bindKeyguard();
        synchronized (this.mLock) {
            this.mSystemBooted = true;
            if (this.mSystemReady) {
                this.mKeyguardDelegate.onBootCompleted();
            }
        }
        this.mSideFpsEventHandler.onFingerprintSensorReady();
        startedWakingUp(0);
        finishedWakingUp(0);
        boolean z = this.mDisplayManager.getDisplay(0).getState() == 2;
        boolean z2 = this.mDefaultDisplayPolicy.getScreenOnListener() != null;
        if (z || z2) {
            screenTurningOn(0, this.mDefaultDisplayPolicy.getScreenOnListener());
            screenTurnedOn(0);
            return;
        }
        this.mBootAnimationDismissable = true;
        enableScreen(null, false);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean canDismissBootAnimation() {
        return this.mDefaultDisplayPolicy.isKeyguardDrawComplete() || this.mBootAnimationDismissable;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void showBootMessage(final CharSequence charSequence, boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PhoneWindowManager.18
            @Override // java.lang.Runnable
            public void run() {
                PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                if (phoneWindowManager.mBootMsgDialog == null) {
                    PhoneWindowManager.this.mBootMsgDialog = new ProgressDialog(PhoneWindowManager.this.mContext, phoneWindowManager.mPackageManager.hasSystemFeature("android.software.leanback") ? 16974875 : 0) { // from class: com.android.server.policy.PhoneWindowManager.18.1
                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
                            return true;
                        }

                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                            return true;
                        }

                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
                            return true;
                        }

                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                            return true;
                        }

                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                            return true;
                        }

                        @Override // android.app.Dialog, android.view.Window.Callback
                        public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
                            return true;
                        }
                    };
                    if (PhoneWindowManager.this.mPackageManager.isDeviceUpgrading()) {
                        PhoneWindowManager.this.mBootMsgDialog.setTitle(17039669);
                    } else {
                        PhoneWindowManager.this.mBootMsgDialog.setTitle(17039664);
                    }
                    PhoneWindowManager.this.mBootMsgDialog.setProgressStyle(0);
                    PhoneWindowManager.this.mBootMsgDialog.setIndeterminate(true);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setType(2021);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().addFlags(258);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setDimAmount(1.0f);
                    WindowManager.LayoutParams attributes = PhoneWindowManager.this.mBootMsgDialog.getWindow().getAttributes();
                    attributes.screenOrientation = 5;
                    attributes.setFitInsetsTypes(0);
                    PhoneWindowManager.this.mBootMsgDialog.getWindow().setAttributes(attributes);
                    PhoneWindowManager.this.mBootMsgDialog.setCancelable(false);
                    PhoneWindowManager.this.mBootMsgDialog.show();
                }
                PhoneWindowManager.this.mBootMsgDialog.setMessage(charSequence);
            }
        });
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void hideBootMessages() {
        this.mHandler.sendEmptyMessage(11);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void userActivity(int i, int i2) {
        if (i == 0 && i2 == 2) {
            this.mDefaultDisplayPolicy.onUserActivityEventTouch();
        }
        synchronized (this.mScreenLockTimeout) {
            if (this.mLockScreenTimerActive) {
                this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                this.mHandler.postDelayed(this.mScreenLockTimeout, this.mLockScreenTimeout);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class ScreenLockTimeout implements Runnable {
        public Bundle options;

        public ScreenLockTimeout() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this) {
                if (PhoneWindowManager.this.mKeyguardDelegate != null) {
                    PhoneWindowManager.this.mKeyguardDelegate.doKeyguardTimeout(this.options);
                }
                PhoneWindowManager phoneWindowManager = PhoneWindowManager.this;
                phoneWindowManager.mLockScreenTimerActive = false;
                phoneWindowManager.mLockNowPending = false;
                this.options = null;
            }
        }

        public void setLockOptions(Bundle bundle) {
            this.options = bundle;
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void lockNow(Bundle bundle) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
        this.mHandler.removeCallbacks(this.mScreenLockTimeout);
        if (bundle != null) {
            this.mScreenLockTimeout.setLockOptions(bundle);
        }
        this.mHandler.post(this.mScreenLockTimeout);
        synchronized (this.mScreenLockTimeout) {
            this.mLockNowPending = true;
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setAllowLockscreenWhenOn(int i, boolean z) {
        if (z) {
            this.mAllowLockscreenWhenOnDisplays.add(Integer.valueOf(i));
        } else {
            this.mAllowLockscreenWhenOnDisplays.remove(Integer.valueOf(i));
        }
        updateLockScreenTimeout();
    }

    public final void updateLockScreenTimeout() {
        KeyguardServiceDelegate keyguardServiceDelegate;
        synchronized (this.mScreenLockTimeout) {
            if (this.mLockNowPending) {
                Log.w(StartingSurfaceController.TAG, "lockNow pending, ignore updating lockscreen timeout");
                return;
            }
            boolean z = !this.mAllowLockscreenWhenOnDisplays.isEmpty() && this.mDefaultDisplayPolicy.isAwake() && (keyguardServiceDelegate = this.mKeyguardDelegate) != null && keyguardServiceDelegate.isSecure(this.mCurrentUserId);
            if (this.mLockScreenTimerActive != z) {
                if (z) {
                    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                    this.mHandler.postDelayed(this.mScreenLockTimeout, this.mLockScreenTimeout);
                } else {
                    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                }
                this.mLockScreenTimerActive = z;
            }
        }
    }

    public final void updateScreenOffSleepToken(boolean z) {
        if (z) {
            this.mScreenOffSleepTokenAcquirer.acquire(0);
        } else {
            this.mScreenOffSleepTokenAcquirer.release(0);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void enableScreenAfterBoot() {
        readLidState();
        applyLidSwitchState();
        updateRotation(true);
    }

    public final void applyLidSwitchState() {
        if (this.mDefaultDisplayPolicy.getLidState() == 0) {
            int lidBehavior = getLidBehavior();
            if (lidBehavior == 1) {
                sleepDefaultDisplay(SystemClock.uptimeMillis(), 3, 1);
            } else if (lidBehavior == 2) {
                this.mWindowManagerFuncs.lockDeviceNow();
            }
        }
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
        }
    }

    public void updateUiMode() {
        if (this.mUiModeManager == null) {
            this.mUiModeManager = IUiModeManager.Stub.asInterface(ServiceManager.getService("uimode"));
        }
        try {
            this.mUiMode = this.mUiModeManager.getCurrentModeType();
        } catch (RemoteException unused) {
        }
    }

    public void updateRotation(boolean z) {
        this.mWindowManagerFuncs.updateRotation(z, false);
    }

    public Intent createHomeDockIntent() {
        Intent intent;
        Bundle bundle;
        int i = this.mUiMode;
        if (i == 3) {
            if (this.mEnableCarDockHomeCapture) {
                intent = this.mCarDockIntent;
            }
            intent = null;
        } else {
            if (i != 2) {
                if (i == 6) {
                    int dockMode = this.mDefaultDisplayPolicy.getDockMode();
                    if (dockMode == 1 || dockMode == 4 || dockMode == 3) {
                        intent = this.mDeskDockIntent;
                    }
                } else if (i == 7) {
                    intent = this.mVrHeadsetHomeIntent;
                }
            }
            intent = null;
        }
        if (intent == null) {
            return null;
        }
        ResolveInfo resolveActivityAsUser = this.mPackageManager.resolveActivityAsUser(intent, 65664, this.mCurrentUserId);
        ActivityInfo activityInfo = resolveActivityAsUser != null ? resolveActivityAsUser.activityInfo : null;
        if (activityInfo == null || (bundle = activityInfo.metaData) == null || !bundle.getBoolean("android.dock_home")) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setClassName(activityInfo.packageName, activityInfo.name);
        return intent2;
    }

    public void startDockOrHome(int i, boolean z, boolean z2, String str) {
        try {
            ActivityManager.getService().stopAppSwitches();
        } catch (RemoteException unused) {
        }
        sendCloseSystemWindows("homekey");
        if (z2) {
            awakenDreams();
        }
        if (!this.mHasFeatureAuto && !isUserSetupComplete()) {
            Slog.i(StartingSurfaceController.TAG, "Not going home because user setup is in progress.");
            return;
        }
        Intent createHomeDockIntent = createHomeDockIntent();
        if (createHomeDockIntent != null) {
            if (z) {
                try {
                    createHomeDockIntent.putExtra("android.intent.extra.FROM_HOME_KEY", z);
                } catch (ActivityNotFoundException unused2) {
                }
            }
            startActivityAsUser(createHomeDockIntent, UserHandle.CURRENT);
            return;
        }
        this.mActivityTaskManagerInternal.startHomeOnDisplay(this.mCurrentUserId, str, i, true, z);
    }

    public void startDockOrHome(int i, boolean z, boolean z2) {
        startDockOrHome(i, z, z2, "startDockOrHome");
    }

    public boolean goHome() {
        IActivityTaskManager service;
        String opPackageName;
        String attributionTag;
        Intent intent;
        if (!isUserSetupComplete()) {
            Slog.i(StartingSurfaceController.TAG, "Not going home because user setup is in progress.");
            return false;
        }
        try {
            if (SystemProperties.getInt("persist.sys.uts-test-mode", 0) == 1) {
                Log.d(StartingSurfaceController.TAG, "UTS-TEST-MODE");
            } else {
                ActivityManager.getService().stopAppSwitches();
                sendCloseSystemWindows();
                Intent createHomeDockIntent = createHomeDockIntent();
                if (createHomeDockIntent != null && ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), createHomeDockIntent, createHomeDockIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 1, (ProfilerInfo) null, (Bundle) null, -2) == 1) {
                    return false;
                }
            }
            service = ActivityTaskManager.getService();
            opPackageName = this.mContext.getOpPackageName();
            attributionTag = this.mContext.getAttributionTag();
            intent = this.mHomeIntent;
        } catch (RemoteException unused) {
        }
        return service.startActivityAsUser((IApplicationThread) null, opPackageName, attributionTag, intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 1, (ProfilerInfo) null, (Bundle) null, -2) != 1;
    }

    public final boolean isTheaterModeEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) == 1;
    }

    public final boolean performHapticFeedback(int i, boolean z, String str) {
        return performHapticFeedback(Process.myUid(), this.mContext.getOpPackageName(), i, z, str);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean isGlobalKey(int i) {
        return this.mGlobalKeyManager.shouldHandleGlobalKey(i);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public boolean performHapticFeedback(int i, String str, int i2, boolean z, String str2) {
        VibrationEffect vibrationEffect;
        if (this.mVibrator.hasVibrator() && (vibrationEffect = getVibrationEffect(i2)) != null) {
            VibrationAttributes vibrationAttributes = getVibrationAttributes(i2);
            if (z) {
                vibrationAttributes = new VibrationAttributes.Builder(vibrationAttributes).setFlags(2).build();
            }
            this.mVibrator.vibrate(i, str, vibrationEffect, str2, vibrationAttributes);
            return true;
        }
        return false;
    }

    public final VibrationEffect getVibrationEffect(int i) {
        if (i != 0) {
            if (i != 1) {
                switch (i) {
                    case 3:
                    case 5:
                    case 12:
                    case 15:
                    case 16:
                    case 19:
                    case 20:
                        break;
                    case 4:
                    case 27:
                        return VibrationEffect.get(21);
                    case 6:
                    case 13:
                    case 18:
                    case 23:
                    case 26:
                        return VibrationEffect.get(2);
                    case 7:
                    case 8:
                    case 10:
                    case 11:
                        return VibrationEffect.get(2, false);
                    case 9:
                        if (!this.mHapticTextHandleEnabled) {
                            return null;
                        }
                        return VibrationEffect.get(21);
                    case 14:
                    case 25:
                        break;
                    case 17:
                        return VibrationEffect.get(1);
                    case 21:
                        return getScaledPrimitiveOrElseEffect(7, 0.5f, 2);
                    case 22:
                        return getScaledPrimitiveOrElseEffect(8, 0.2f, 21);
                    case 24:
                        return getScaledPrimitiveOrElseEffect(7, 0.4f, 21);
                    default:
                        switch (i) {
                            case FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG /* 10001 */:
                                long[] jArr = this.mSafeModeEnabledVibePattern;
                                if (jArr.length == 0) {
                                    return null;
                                }
                                if (jArr.length == 1) {
                                    return VibrationEffect.createOneShot(jArr[0], -1);
                                }
                                return VibrationEffect.createWaveform(jArr, -1);
                            case FrameworkStatsLog.MOBILE_BYTES_TRANSFER /* 10002 */:
                                if (this.mVibrator.areAllPrimitivesSupported(4, 7)) {
                                    return VibrationEffect.startComposition().addPrimitive(4, 0.25f).addPrimitive(7, 1.0f, 50).compose();
                                }
                                return VibrationEffect.get(5);
                            case FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG /* 10003 */:
                                break;
                            default:
                                return null;
                        }
                }
            }
            return VibrationEffect.get(0);
        }
        return VibrationEffect.get(5);
    }

    public final VibrationEffect getScaledPrimitiveOrElseEffect(int i, float f, int i2) {
        if (this.mVibrator.areAllPrimitivesSupported(i)) {
            return VibrationEffect.startComposition().addPrimitive(i, f).compose();
        }
        return VibrationEffect.get(i2);
    }

    public final VibrationAttributes getVibrationAttributes(int i) {
        if (i == 14 || i == 15) {
            return PHYSICAL_EMULATION_VIBRATION_ATTRIBUTES;
        }
        if (i != 10002 && i != 10003) {
            switch (i) {
                case 18:
                case 19:
                case 20:
                    break;
                default:
                    return TOUCH_VIBRATION_ATTRIBUTES;
            }
        }
        return HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setDismissImeOnBackKeyPressed(boolean z) {
        this.mDismissImeOnBackKeyPressed = z;
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setCurrentUserLw(int i) {
        this.mCurrentUserId = i;
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.setCurrentUser(i);
        }
        AccessibilityShortcutController accessibilityShortcutController = this.mAccessibilityShortcutController;
        if (accessibilityShortcutController != null) {
            accessibilityShortcutController.setCurrentUser(i);
        }
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.setCurrentUser(i);
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void setSwitchingUser(boolean z) {
        this.mKeyguardDelegate.setSwitchingUser(z);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1159641169922L, this.mDefaultDisplayRotation.getUserRotationMode());
        protoOutputStream.write(1159641169923L, this.mDefaultDisplayRotation.getUserRotation());
        protoOutputStream.write(1159641169924L, this.mDefaultDisplayRotation.getCurrentAppOrientation());
        protoOutputStream.write(1133871366149L, this.mDefaultDisplayPolicy.isScreenOnFully());
        protoOutputStream.write(1133871366150L, this.mDefaultDisplayPolicy.isKeyguardDrawComplete());
        protoOutputStream.write(1133871366151L, this.mDefaultDisplayPolicy.isWindowManagerDrawComplete());
        protoOutputStream.write(1133871366156L, isKeyguardOccluded());
        protoOutputStream.write(1133871366157L, this.mKeyguardOccludedChanged);
        protoOutputStream.write(1133871366158L, this.mPendingKeyguardOccluded);
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.dumpDebug(protoOutputStream, 1146756268052L);
        }
        protoOutputStream.end(start);
    }

    @Override // com.android.server.policy.WindowManagerPolicy
    public void dump(String str, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mSafeMode=");
        printWriter.print(this.mSafeMode);
        printWriter.print(" mSystemReady=");
        printWriter.print(this.mSystemReady);
        printWriter.print(" mSystemBooted=");
        printWriter.println(this.mSystemBooted);
        printWriter.print(str);
        printWriter.print("mCameraLensCoverState=");
        printWriter.println(WindowManagerPolicy.WindowManagerFuncs.cameraLensStateToString(this.mCameraLensCoverState));
        printWriter.print(str);
        printWriter.print("mWakeGestureEnabledSetting=");
        printWriter.println(this.mWakeGestureEnabledSetting);
        printWriter.print(str);
        printWriter.print("mUiMode=");
        printWriter.print(Configuration.uiModeToString(this.mUiMode));
        printWriter.print("mEnableCarDockHomeCapture=");
        printWriter.println(this.mEnableCarDockHomeCapture);
        printWriter.print(str);
        printWriter.print("mLidKeyboardAccessibility=");
        printWriter.print(this.mLidKeyboardAccessibility);
        printWriter.print(" mLidNavigationAccessibility=");
        printWriter.print(this.mLidNavigationAccessibility);
        printWriter.print(" getLidBehavior=");
        printWriter.println(lidBehaviorToString(getLidBehavior()));
        printWriter.print(str);
        printWriter.print("mLongPressOnBackBehavior=");
        printWriter.println(longPressOnBackBehaviorToString(this.mLongPressOnBackBehavior));
        printWriter.print(str);
        printWriter.print("mLongPressOnHomeBehavior=");
        printWriter.println(longPressOnHomeBehaviorToString(this.mLongPressOnHomeBehavior));
        printWriter.print(str);
        printWriter.print("mDoubleTapOnHomeBehavior=");
        printWriter.println(doubleTapOnHomeBehaviorToString(this.mDoubleTapOnHomeBehavior));
        printWriter.print(str);
        printWriter.print("mShortPressOnPowerBehavior=");
        printWriter.println(shortPressOnPowerBehaviorToString(this.mShortPressOnPowerBehavior));
        printWriter.print(str);
        printWriter.print("mLongPressOnPowerBehavior=");
        printWriter.println(longPressOnPowerBehaviorToString(this.mLongPressOnPowerBehavior));
        printWriter.print(str);
        printWriter.print("mLongPressOnPowerAssistantTimeoutMs=");
        printWriter.println(this.mLongPressOnPowerAssistantTimeoutMs);
        printWriter.print(str);
        printWriter.print("mVeryLongPressOnPowerBehavior=");
        printWriter.println(veryLongPressOnPowerBehaviorToString(this.mVeryLongPressOnPowerBehavior));
        printWriter.print(str);
        printWriter.print("mDoublePressOnPowerBehavior=");
        printWriter.println(multiPressOnPowerBehaviorToString(this.mDoublePressOnPowerBehavior));
        printWriter.print(str);
        printWriter.print("mTriplePressOnPowerBehavior=");
        printWriter.println(multiPressOnPowerBehaviorToString(this.mTriplePressOnPowerBehavior));
        printWriter.print(str);
        printWriter.print("mPowerVolUpBehavior=");
        printWriter.println(powerVolumeUpBehaviorToString(this.mPowerVolUpBehavior));
        printWriter.print(str);
        printWriter.print("mShortPressOnSleepBehavior=");
        printWriter.println(shortPressOnSleepBehaviorToString(this.mShortPressOnSleepBehavior));
        printWriter.print(str);
        printWriter.print("mShortPressOnWindowBehavior=");
        printWriter.println(shortPressOnWindowBehaviorToString(this.mShortPressOnWindowBehavior));
        printWriter.print(str);
        printWriter.print("mShortPressOnStemPrimaryBehavior=");
        printWriter.println(shortPressOnStemPrimaryBehaviorToString(this.mShortPressOnStemPrimaryBehavior));
        printWriter.print(str);
        printWriter.print("mDoublePressOnStemPrimaryBehavior=");
        printWriter.println(doublePressOnStemPrimaryBehaviorToString(this.mDoublePressOnStemPrimaryBehavior));
        printWriter.print(str);
        printWriter.print("mTriplePressOnStemPrimaryBehavior=");
        printWriter.println(triplePressOnStemPrimaryBehaviorToString(this.mTriplePressOnStemPrimaryBehavior));
        printWriter.print(str);
        printWriter.print("mLongPressOnStemPrimaryBehavior=");
        printWriter.println(longPressOnStemPrimaryBehaviorToString(this.mLongPressOnStemPrimaryBehavior));
        printWriter.print(str);
        printWriter.print("mAllowStartActivityForLongPressOnPowerDuringSetup=");
        printWriter.println(this.mAllowStartActivityForLongPressOnPowerDuringSetup);
        printWriter.print(str);
        printWriter.print("mHasSoftInput=");
        printWriter.print(this.mHasSoftInput);
        printWriter.print(" mHapticTextHandleEnabled=");
        printWriter.println(this.mHapticTextHandleEnabled);
        printWriter.print(str);
        printWriter.print("mDismissImeOnBackKeyPressed=");
        printWriter.print(this.mDismissImeOnBackKeyPressed);
        printWriter.print(" mIncallPowerBehavior=");
        printWriter.println(incallPowerBehaviorToString(this.mIncallPowerBehavior));
        printWriter.print(str);
        printWriter.print("mIncallBackBehavior=");
        printWriter.print(incallBackBehaviorToString(this.mIncallBackBehavior));
        printWriter.print(" mEndcallBehavior=");
        printWriter.println(endcallBehaviorToString(this.mEndcallBehavior));
        printWriter.print(str);
        printWriter.print("mDisplayHomeButtonHandlers=");
        for (int i = 0; i < this.mDisplayHomeButtonHandlers.size(); i++) {
            printWriter.println(this.mDisplayHomeButtonHandlers.get(this.mDisplayHomeButtonHandlers.keyAt(i)));
        }
        printWriter.print(str);
        printWriter.print("mKeyguardOccluded=");
        printWriter.print(isKeyguardOccluded());
        printWriter.print(" mKeyguardOccludedChanged=");
        printWriter.print(this.mKeyguardOccludedChanged);
        printWriter.print(" mPendingKeyguardOccluded=");
        printWriter.println(this.mPendingKeyguardOccluded);
        printWriter.print(str);
        printWriter.print("mAllowLockscreenWhenOnDisplays=");
        printWriter.print(!this.mAllowLockscreenWhenOnDisplays.isEmpty());
        printWriter.print(" mLockScreenTimeout=");
        printWriter.print(this.mLockScreenTimeout);
        printWriter.print(" mLockScreenTimerActive=");
        printWriter.println(this.mLockScreenTimerActive);
        this.mGlobalKeyManager.dump(str, printWriter);
        this.mKeyCombinationManager.dump(str, printWriter);
        this.mSingleKeyGestureDetector.dump(str, printWriter);
        MyWakeGestureListener myWakeGestureListener = this.mWakeGestureListener;
        if (myWakeGestureListener != null) {
            myWakeGestureListener.dump(printWriter, str);
        }
        BurnInProtectionHelper burnInProtectionHelper = this.mBurnInProtectionHelper;
        if (burnInProtectionHelper != null) {
            burnInProtectionHelper.dump(str, printWriter);
        }
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.dump(str, printWriter);
        }
        printWriter.print(str);
        printWriter.println("Looper state:");
        Looper looper = this.mHandler.getLooper();
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        looper.dump(printWriterPrinter, str + "  ");
    }

    public static String endcallBehaviorToString(int i) {
        StringBuilder sb = new StringBuilder();
        if ((i & 1) != 0) {
            sb.append("home|");
        }
        if ((i & 2) != 0) {
            sb.append("sleep|");
        }
        int length = sb.length();
        return length == 0 ? "<nothing>" : sb.substring(0, length - 1);
    }

    public static String longPressOnBackBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "LONG_PRESS_BACK_GO_TO_VOICE_ASSIST" : "LONG_PRESS_BACK_NOTHING";
    }

    public static String longPressOnHomeBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? Integer.toString(i) : "LONG_PRESS_HOME_NOTIFICATION_PANEL" : "LONG_PRESS_HOME_ASSIST" : "LONG_PRESS_HOME_ALL_APPS" : "LONG_PRESS_HOME_NOTHING";
    }

    public static String doubleTapOnHomeBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "DOUBLE_TAP_HOME_PIP_MENU" : "DOUBLE_TAP_HOME_RECENT_SYSTEM_UI" : "DOUBLE_TAP_HOME_NOTHING";
    }

    public static String shortPressOnPowerBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? Integer.toString(i) : "SHORT_PRESS_POWER_CLOSE_IME_OR_GO_HOME" : "SHORT_PRESS_POWER_GO_HOME" : "SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME" : "SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP" : "SHORT_PRESS_POWER_GO_TO_SLEEP" : "SHORT_PRESS_POWER_NOTHING";
    }

    public static String longPressOnPowerBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? Integer.toString(i) : "LONG_PRESS_POWER_ASSISTANT" : "LONG_PRESS_POWER_GO_TO_VOICE_ASSIST" : "LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM" : "LONG_PRESS_POWER_SHUT_OFF" : "LONG_PRESS_POWER_GLOBAL_ACTIONS" : "LONG_PRESS_POWER_NOTHING";
    }

    public static String veryLongPressOnPowerBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "VERY_LONG_PRESS_POWER_GLOBAL_ACTIONS" : "VERY_LONG_PRESS_POWER_NOTHING";
    }

    public static String powerVolumeUpBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "POWER_VOLUME_UP_BEHAVIOR_GLOBAL_ACTIONS" : "POWER_VOLUME_UP_BEHAVIOR_MUTE" : "POWER_VOLUME_UP_BEHAVIOR_NOTHING";
    }

    public static String multiPressOnPowerBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? Integer.toString(i) : "MULTI_PRESS_POWER_LAUNCH_TARGET_ACTIVITY" : "MULTI_PRESS_POWER_BRIGHTNESS_BOOST" : "MULTI_PRESS_POWER_THEATER_MODE" : "MULTI_PRESS_POWER_NOTHING";
    }

    public static String shortPressOnSleepBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME" : "SHORT_PRESS_SLEEP_GO_TO_SLEEP";
    }

    public static String shortPressOnWindowBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "SHORT_PRESS_WINDOW_PICTURE_IN_PICTURE" : "SHORT_PRESS_WINDOW_NOTHING";
    }

    public static String shortPressOnStemPrimaryBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "SHORT_PRESS_PRIMARY_LAUNCH_ALL_APPS" : "SHORT_PRESS_PRIMARY_NOTHING";
    }

    public static String doublePressOnStemPrimaryBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "DOUBLE_PRESS_PRIMARY_SWITCH_RECENT_APP" : "DOUBLE_PRESS_PRIMARY_NOTHING";
    }

    public static String triplePressOnStemPrimaryBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "TRIPLE_PRESS_PRIMARY_TOGGLE_ACCESSIBILITY" : "TRIPLE_PRESS_PRIMARY_NOTHING";
    }

    public static String longPressOnStemPrimaryBehaviorToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "LONG_PRESS_PRIMARY_LAUNCH_VOICE_ASSISTANT" : "LONG_PRESS_PRIMARY_NOTHING";
    }

    public static String lidBehaviorToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "LID_BEHAVIOR_LOCK" : "LID_BEHAVIOR_SLEEP" : "LID_BEHAVIOR_NONE";
    }

    public static boolean isLongPressToAssistantEnabled(Context context) {
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), "clockwork_long_press_to_assistant_enabled", 1, -2);
        if (Log.isLoggable(StartingSurfaceController.TAG, 3)) {
            Log.d(StartingSurfaceController.TAG, "longPressToAssistant = " + intForUser);
        }
        return intForUser == 1;
    }

    /* loaded from: classes2.dex */
    public class HdmiVideoExtconUEventObserver extends ExtconStateObserver<Boolean> {
        public HdmiVideoExtconUEventObserver() {
        }

        public final boolean init(ExtconUEventObserver.ExtconInfo extconInfo) {
            boolean z;
            try {
                z = parseStateFromFile(extconInfo).booleanValue();
            } catch (FileNotFoundException e) {
                Slog.w(StartingSurfaceController.TAG, extconInfo.getStatePath() + " not found while attempting to determine initial state", e);
                z = false;
                startObserving(extconInfo);
                return z;
            } catch (IOException e2) {
                Slog.e(StartingSurfaceController.TAG, "Error reading " + extconInfo.getStatePath() + " while attempting to determine initial state", e2);
                z = false;
                startObserving(extconInfo);
                return z;
            }
            startObserving(extconInfo);
            return z;
        }

        @Override // com.android.server.ExtconStateObserver
        public void updateState(ExtconUEventObserver.ExtconInfo extconInfo, String str, Boolean bool) {
            PhoneWindowManager.this.mDefaultDisplayPolicy.setHdmiPlugged(bool.booleanValue());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.ExtconStateObserver
        public Boolean parseState(ExtconUEventObserver.ExtconInfo extconInfo, String str) {
            return Boolean.valueOf(str.contains("HDMI=1"));
        }
    }

    public final void launchTargetSearchActivity() {
        Intent intent;
        if (this.mSearchKeyTargetActivity != null) {
            intent = new Intent();
            intent.setComponent(this.mSearchKeyTargetActivity);
        } else {
            intent = new Intent("android.intent.action.WEB_SEARCH");
        }
        intent.addFlags(270532608);
        try {
            startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
        } catch (ActivityNotFoundException unused) {
            Slog.e(StartingSurfaceController.TAG, "Could not resolve activity with : " + intent.getComponent().flattenToString() + " name.");
        }
    }
}
