package com.android.server.p014wm;

import android.animation.ValueAnimator;
import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IAssistDataReceiver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.p000pm.TestUtilityService;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.configstore.V1_0.OptionalBool;
import android.hardware.configstore.V1_1.ISurfaceFlingerConfigs;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.InputConstants;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemService;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.provider.DeviceConfigInterface;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.sysprop.SurfaceFlingerProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.MergedConfiguration;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.util.proto.ProtoOutputStream;
import android.view.Choreographer;
import android.view.ContentRecordingSession;
import android.view.DisplayInfo;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.ICrossWindowBlurEnabledListener;
import android.view.IDisplayChangeWindowController;
import android.view.IDisplayFoldListener;
import android.view.IDisplayWindowInsetsController;
import android.view.IDisplayWindowListener;
import android.view.IInputFilter;
import android.view.IOnKeyguardExitResult;
import android.view.IPinnedTaskListener;
import android.view.IRecentsAnimationRunner;
import android.view.IRotationWatcher;
import android.view.IScrollCaptureResponseListener;
import android.view.ISystemGestureExclusionListener;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.InsetsFrameProvider;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.ScrollCaptureResponse;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceSession;
import android.view.TaskTransitionSpec;
import android.view.WindowContentFrameStats;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import android.view.displayhash.DisplayHash;
import android.view.displayhash.VerifiedDisplayHash;
import android.view.inputmethod.ImeTracker;
import android.window.AddToSurfaceSyncGroupResult;
import android.window.ClientWindowFrames;
import android.window.ISurfaceSyncGroupCompletedListener;
import android.window.ITaskFpsCallback;
import android.window.ScreenCapture;
import android.window.TaskSnapshot;
import android.window.WindowContainerToken;
import android.window.WindowProviderService;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.IResultReceiver;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardLockedStateListener;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.KeyInterceptionInfo;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.LatencyTracker;
import com.android.internal.util.jobs.XmlUtils;
import com.android.internal.view.WindowManagerPolicyThread;
import com.android.server.AnimationThread;
import com.android.server.DisplayThread;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.UiThread;
import com.android.server.Watchdog;
import com.android.server.input.InputManagerService;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.location.settings.SettingsStore$$ExternalSyntheticLambda1;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.AccessibilityController;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.DisplayAreaPolicy;
import com.android.server.p014wm.DisplayContent;
import com.android.server.p014wm.EmbeddedWindowController;
import com.android.server.p014wm.RecentsAnimationController;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.p014wm.WindowManagerService;
import com.android.server.p014wm.WindowState;
import com.android.server.p014wm.WindowToken;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.power.ShutdownThread;
import com.android.server.utils.PriorityDump;
import dalvik.annotation.optimization.NeverCompile;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.WindowManagerService */
/* loaded from: classes2.dex */
public class WindowManagerService extends IWindowManager.Stub implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs {
    public final AccessibilityController mAccessibilityController;
    public final IActivityManager mActivityManager;
    public final WindowManagerInternal.AppTransitionListener mActivityManagerAppTransitionNotifier;
    public final boolean mAllowAnimationsInLowPowerMode;
    public final boolean mAllowBootMessages;
    public boolean mAllowTheaterModeWakeFromLayout;
    public final ActivityManagerInternal mAmInternal;
    public final Handler mAnimationHandler;
    public final ArrayMap<AnimationAdapter, SurfaceAnimator> mAnimationTransferMap;
    public boolean mAnimationsDisabled;
    public final WindowAnimator mAnimator;
    public float mAnimatorDurationScaleSetting;
    public final AnrController mAnrController;
    public final ArrayList<AppFreezeListener> mAppFreezeListeners;
    public final AppOpsManager mAppOps;
    public int mAppsFreezingScreen;
    public final boolean mAssistantOnTopOfDream;
    public final ActivityTaskManagerService mAtmService;
    public final BlurController mBlurController;
    public boolean mBootAnimationStopped;
    public long mBootWaitForWindowsStartTime;
    public final BroadcastReceiver mBroadcastReceiver;
    public boolean mClientFreezingScreen;
    public final WindowManagerConstants mConstants;
    @VisibleForTesting
    final ContentRecordingController mContentRecordingController;
    public final Context mContext;
    public int mCurrentUserId;
    public final ArrayList<WindowState> mDestroySurface;
    public boolean mDisableTransitionAnimation;
    public final DisplayAreaPolicy.Provider mDisplayAreaPolicyProvider;
    public IDisplayChangeWindowController mDisplayChangeController;
    public final IBinder.DeathRecipient mDisplayChangeControllerDeath;
    public boolean mDisplayEnabled;
    public long mDisplayFreezeTime;
    public boolean mDisplayFrozen;
    public final DisplayHashController mDisplayHashController;
    public volatile Map<Integer, Integer> mDisplayImePolicyCache;
    public final DisplayManager mDisplayManager;
    public final DisplayManagerInternal mDisplayManagerInternal;
    public final DisplayWindowListenerController mDisplayNotificationController;
    public boolean mDisplayReady;
    public final DisplayWindowSettings mDisplayWindowSettings;
    public final DisplayWindowSettingsProvider mDisplayWindowSettingsProvider;
    public final DragDropController mDragDropController;
    public final long mDrawLockTimeoutMillis;
    public final EmbeddedWindowController mEmbeddedWindowController;
    public EmulatorDisplayOverlay mEmulatorDisplayOverlay;
    public int mEnterAnimId;
    public boolean mEventDispatchingEnabled;
    public int mExitAnimId;
    public boolean mFocusMayChange;
    public InputTarget mFocusedInputTarget;
    public boolean mForceDesktopModeOnExternalDisplays;
    public boolean mForceDisplayEnabled;
    public final ArrayList<WindowState> mForceRemoves;
    public int mFrozenDisplayId;
    public final WindowManagerGlobalLock mGlobalLock;

    /* renamed from: mH */
    public final HandlerC1915H f1164mH;
    public boolean mHardKeyboardAvailable;
    public WindowManagerInternal.OnHardKeyboardStatusChangeListener mHardKeyboardStatusChangeListener;
    public boolean mHasHdrSupport;
    public final boolean mHasPermanentDpad;
    public boolean mHasWideColorGamutSupport;
    public ArrayList<WindowState> mHidingNonSystemOverlayWindows;
    public final HighRefreshRateDenylist mHighRefreshRateDenylist;
    public final InputManagerService mInputManager;
    public final InputManagerCallback mInputManagerCallback;
    public final HashMap<IBinder, WindowState> mInputToWindowMap;
    public boolean mIsFakeTouchDevice;
    public boolean mIsIgnoreOrientationRequestDisabled;
    public boolean mIsPc;
    public boolean mIsTouchDevice;
    public final KeyguardDisableHandler mKeyguardDisableHandler;
    public String mLastANRState;
    public int mLastDisplayFreezeDuration;
    public Object mLastFinishedFreezeSource;
    public final LatencyTracker mLatencyTracker;
    public final LetterboxConfiguration mLetterboxConfiguration;
    public final boolean mLimitedAlphaCompositing;
    public final int mMaxUiWidth;
    public volatile float mMaximumObscuringOpacityForTouch;
    public MousePositionTracker mMousePositionTracker;
    @VisibleForTesting
    boolean mPerDisplayFocusEnabled;
    public final PackageManagerInternal mPmInternal;
    public boolean mPointerLocationEnabled;
    @VisibleForTesting
    WindowManagerPolicy mPolicy;
    public final PossibleDisplayInfoMapper mPossibleDisplayInfoMapper;
    public PowerManager mPowerManager;
    public PowerManagerInternal mPowerManagerInternal;
    public final PriorityDump.PriorityDumper mPriorityDumper;
    public RecentsAnimationController mRecentsAnimationController;
    public final ArrayList<WindowState> mResizingWindows;
    public final RootWindowContainer mRoot;
    public final RotationWatcherController mRotationWatcherController;
    public boolean mSafeMode;
    public final PowerManager.WakeLock mScreenFrozenLock;
    public final ArraySet<Session> mSessions;
    public SettingsObserver mSettingsObserver;
    public boolean mShowAlertWindowNotifications;
    public boolean mShowingBootMessages;
    public final SnapshotPersistQueue mSnapshotPersistQueue;
    public final StartingSurfaceController mStartingSurfaceController;
    public StrictModeFlash mStrictModeFlash;
    public SurfaceAnimationRunner mSurfaceAnimationRunner;
    public Function<SurfaceSession, SurfaceControl.Builder> mSurfaceControlFactory;
    public final SurfaceSyncGroupController mSurfaceSyncGroupController;
    public boolean mSwitchingUser;
    public final BLASTSyncEngine mSyncEngine;
    public boolean mSystemBooted;
    public boolean mSystemReady;
    public final TaskFpsCallbackController mTaskFpsCallbackController;
    public final TaskPositioningController mTaskPositioningController;
    public final TaskSnapshotController mTaskSnapshotController;
    public final TaskSystemBarsListenerController mTaskSystemBarsListenerController;
    public TaskTransitionSpec mTaskTransitionSpec;
    public WindowContentFrameStats mTempWindowRenderStats;
    public final TestUtilityService mTestUtilityService;
    public final Rect mTmpRect;
    public final SurfaceControl.Transaction mTransaction;
    public Supplier<SurfaceControl.Transaction> mTransactionFactory;
    public int mTransactionSequence;
    public float mTransitionAnimationScaleSetting;
    public final TransitionTracer mTransitionTracer;
    public final UserManagerInternal mUmInternal;
    public final boolean mUseBLAST;
    public ViewServer mViewServer;
    public final HashMap<WindowContainer, Runnable> mWaitingForDrawnCallbacks;
    public final WallpaperVisibilityListeners mWallpaperVisibilityListeners;
    public Watermark mWatermark;
    public float mWindowAnimationScaleSetting;
    public final ArrayList<WindowChangeListener> mWindowChangeListeners;
    @VisibleForTesting
    final WindowContextListenerController mWindowContextListenerController;
    public final HashMap<IBinder, WindowState> mWindowMap;
    public final WindowSurfacePlacer mWindowPlacerLocked;
    public final ArrayList<ActivityRecord> mWindowReplacementTimeouts;
    public final WindowTracing mWindowTracing;
    public boolean mWindowsChanged;
    public int mWindowsFreezingScreen;
    public int mWindowsInsetsChanged;
    public static final int MY_PID = Process.myPid();
    public static final int MY_UID = Process.myUid();
    public static final boolean sEnableShellTransitions = SystemProperties.getBoolean("persist.wm.debug.shell_transit", true);
    public static final boolean ENABLE_FIXED_ROTATION_TRANSFORM = SystemProperties.getBoolean("persist.wm.fixed_rotation_transform", true);
    public static WindowManagerThreadPriorityBooster sThreadPriorityBooster = new WindowManagerThreadPriorityBooster();
    public final RemoteCallbackList<IKeyguardLockedStateListener> mKeyguardLockedStateListeners = new RemoteCallbackList<>();
    public boolean mDispatchedKeyguardLockedState = false;
    public int mVr2dDisplayId = -1;
    public boolean mVrModeEnabled = false;
    public final Map<IBinder, KeyInterceptionInfo> mKeyInterceptionInfoForToken = Collections.synchronizedMap(new ArrayMap());
    public final IVrStateCallbacks mVrStateCallbacks = new C19061();

    /* renamed from: com.android.server.wm.WindowManagerService$AppFreezeListener */
    /* loaded from: classes2.dex */
    public interface AppFreezeListener {
        void onAppFreezeTimeout();
    }

    /* renamed from: com.android.server.wm.WindowManagerService$WindowChangeListener */
    /* loaded from: classes2.dex */
    public interface WindowChangeListener {
        void focusChanged();

        void windowsChanged();
    }

    public static boolean excludeWindowTypeFromTapOutTask(int i) {
        return i == 2000 || i == 2012 || i == 2040 || i == 2019 || i == 2020;
    }

    public void endProlongedAnimations() {
    }

    public int getDockedStackSide() {
        return 0;
    }

    /* renamed from: com.android.server.wm.WindowManagerService$1 */
    /* loaded from: classes2.dex */
    public class C19061 extends IVrStateCallbacks.Stub {
        public C19061() {
        }

        public void onVrStateChanged(final boolean z) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService windowManagerService = WindowManagerService.this;
                    windowManagerService.mVrModeEnabled = z;
                    windowManagerService.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$1$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((DisplayPolicy) obj).onVrStateChangedLw(z);
                        }
                    });
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mDisplayChangeController = null;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.WindowManagerService$SettingsObserver */
    /* loaded from: classes2.dex */
    public final class SettingsObserver extends ContentObserver {
        public final Uri mAnimationDurationScaleUri;
        public final Uri mDevEnableNonResizableMultiWindowUri;
        public final Uri mDisplayInversionEnabledUri;
        public final Uri mDisplaySettingsPathUri;
        public final Uri mForceDesktopModeOnExternalDisplaysUri;
        public final Uri mForceResizableUri;
        public final Uri mFreeformWindowUri;
        public final Uri mImmersiveModeConfirmationsUri;
        public final Uri mMaximumObscuringOpacityForTouchUri;
        public final Uri mPointerLocationUri;
        public final Uri mPolicyControlUri;
        public final Uri mTransitionAnimationScaleUri;
        public final Uri mWindowAnimationScaleUri;

        public SettingsObserver() {
            super(new Handler());
            Uri uriFor = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");
            this.mDisplayInversionEnabledUri = uriFor;
            Uri uriFor2 = Settings.Global.getUriFor("window_animation_scale");
            this.mWindowAnimationScaleUri = uriFor2;
            Uri uriFor3 = Settings.Global.getUriFor("transition_animation_scale");
            this.mTransitionAnimationScaleUri = uriFor3;
            Uri uriFor4 = Settings.Global.getUriFor("animator_duration_scale");
            this.mAnimationDurationScaleUri = uriFor4;
            Uri uriFor5 = Settings.Secure.getUriFor("immersive_mode_confirmations");
            this.mImmersiveModeConfirmationsUri = uriFor5;
            Uri uriFor6 = Settings.Global.getUriFor("policy_control");
            this.mPolicyControlUri = uriFor6;
            Uri uriFor7 = Settings.System.getUriFor("pointer_location");
            this.mPointerLocationUri = uriFor7;
            Uri uriFor8 = Settings.Global.getUriFor("force_desktop_mode_on_external_displays");
            this.mForceDesktopModeOnExternalDisplaysUri = uriFor8;
            Uri uriFor9 = Settings.Global.getUriFor("enable_freeform_support");
            this.mFreeformWindowUri = uriFor9;
            Uri uriFor10 = Settings.Global.getUriFor("force_resizable_activities");
            this.mForceResizableUri = uriFor10;
            Uri uriFor11 = Settings.Global.getUriFor("enable_non_resizable_multi_window");
            this.mDevEnableNonResizableMultiWindowUri = uriFor11;
            Uri uriFor12 = Settings.Global.getUriFor("wm_display_settings_path");
            this.mDisplaySettingsPathUri = uriFor12;
            Uri uriFor13 = Settings.Global.getUriFor("maximum_obscuring_opacity_for_touch");
            this.mMaximumObscuringOpacityForTouchUri = uriFor13;
            ContentResolver contentResolver = WindowManagerService.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(uriFor, false, this, -1);
            contentResolver.registerContentObserver(uriFor2, false, this, -1);
            contentResolver.registerContentObserver(uriFor3, false, this, -1);
            contentResolver.registerContentObserver(uriFor4, false, this, -1);
            contentResolver.registerContentObserver(uriFor5, false, this, -1);
            contentResolver.registerContentObserver(uriFor6, false, this, -1);
            contentResolver.registerContentObserver(uriFor7, false, this, -1);
            contentResolver.registerContentObserver(uriFor8, false, this, -1);
            contentResolver.registerContentObserver(uriFor9, false, this, -1);
            contentResolver.registerContentObserver(uriFor10, false, this, -1);
            contentResolver.registerContentObserver(uriFor11, false, this, -1);
            contentResolver.registerContentObserver(uriFor12, false, this, -1);
            contentResolver.registerContentObserver(uriFor13, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (uri == null) {
                return;
            }
            int i = 1;
            if (this.mImmersiveModeConfirmationsUri.equals(uri) || this.mPolicyControlUri.equals(uri)) {
                updateSystemUiSettings(true);
            } else if (this.mPointerLocationUri.equals(uri)) {
                updatePointerLocation();
            } else if (this.mForceDesktopModeOnExternalDisplaysUri.equals(uri)) {
                updateForceDesktopModeOnExternalDisplays();
            } else if (this.mFreeformWindowUri.equals(uri)) {
                updateFreeformWindowManagement();
            } else if (this.mForceResizableUri.equals(uri)) {
                updateForceResizableTasks();
            } else if (this.mDevEnableNonResizableMultiWindowUri.equals(uri)) {
                updateDevEnableNonResizableMultiWindow();
            } else if (this.mDisplaySettingsPathUri.equals(uri)) {
                updateDisplaySettingsLocation();
            } else if (this.mMaximumObscuringOpacityForTouchUri.equals(uri)) {
                updateMaximumObscuringOpacityForTouch();
            } else {
                if (this.mWindowAnimationScaleUri.equals(uri)) {
                    i = 0;
                } else if (!this.mTransitionAnimationScaleUri.equals(uri)) {
                    if (!this.mAnimationDurationScaleUri.equals(uri)) {
                        return;
                    }
                    i = 2;
                }
                WindowManagerService.this.f1164mH.sendMessage(WindowManagerService.this.f1164mH.obtainMessage(51, i, 0));
            }
        }

        public void loadSettings() {
            updateSystemUiSettings(false);
            updatePointerLocation();
            updateMaximumObscuringOpacityForTouch();
        }

        public void updateMaximumObscuringOpacityForTouch() {
            ContentResolver contentResolver = WindowManagerService.this.mContext.getContentResolver();
            WindowManagerService.this.mMaximumObscuringOpacityForTouch = Settings.Global.getFloat(contentResolver, "maximum_obscuring_opacity_for_touch", 0.8f);
            if (WindowManagerService.this.mMaximumObscuringOpacityForTouch < 0.0f || WindowManagerService.this.mMaximumObscuringOpacityForTouch > 1.0f) {
                WindowManagerService.this.mMaximumObscuringOpacityForTouch = 0.8f;
            }
        }

        public void updateSystemUiSettings(boolean z) {
            boolean z2;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (z) {
                        z2 = WindowManagerService.this.getDefaultDisplayContentLocked().getDisplayPolicy().onSystemUiSettingsChanged();
                    } else {
                        WindowManagerService windowManagerService = WindowManagerService.this;
                        ImmersiveModeConfirmation.loadSetting(windowManagerService.mCurrentUserId, windowManagerService.mContext);
                        z2 = false;
                    }
                    if (z2) {
                        WindowManagerService.this.mWindowPlacerLocked.requestTraversal();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void updatePointerLocation() {
            boolean z = Settings.System.getIntForUser(WindowManagerService.this.mContext.getContentResolver(), "pointer_location", 0, -2) != 0;
            WindowManagerService windowManagerService = WindowManagerService.this;
            if (windowManagerService.mPointerLocationEnabled == z) {
                return;
            }
            windowManagerService.mPointerLocationEnabled = z;
            synchronized (windowManagerService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$SettingsObserver$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.SettingsObserver.this.lambda$updatePointerLocation$0((DisplayPolicy) obj);
                        }
                    });
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updatePointerLocation$0(DisplayPolicy displayPolicy) {
            displayPolicy.setPointerLocationEnabled(WindowManagerService.this.mPointerLocationEnabled);
        }

        public void updateForceDesktopModeOnExternalDisplays() {
            boolean z = Settings.Global.getInt(WindowManagerService.this.mContext.getContentResolver(), "force_desktop_mode_on_external_displays", 0) != 0;
            WindowManagerService windowManagerService = WindowManagerService.this;
            if (windowManagerService.mForceDesktopModeOnExternalDisplays == z) {
                return;
            }
            windowManagerService.setForceDesktopModeOnExternalDisplays(z);
        }

        /* JADX WARN: Code restructure failed: missing block: B:5:0x001f, code lost:
            if (android.provider.Settings.Global.getInt(r0, "enable_freeform_support", 0) != 0) goto L21;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void updateFreeformWindowManagement() {
            ContentResolver contentResolver = WindowManagerService.this.mContext.getContentResolver();
            boolean z = WindowManagerService.this.mContext.getPackageManager().hasSystemFeature("android.software.freeform_window_management");
            WindowManagerService windowManagerService = WindowManagerService.this;
            ActivityTaskManagerService activityTaskManagerService = windowManagerService.mAtmService;
            if (activityTaskManagerService.mSupportsFreeformWindowManagement != z) {
                activityTaskManagerService.mSupportsFreeformWindowManagement = z;
                synchronized (windowManagerService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        WindowManagerService.this.mRoot.onSettingsRetrieved();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }

        public void updateForceResizableTasks() {
            WindowManagerService.this.mAtmService.mForceResizableActivities = Settings.Global.getInt(WindowManagerService.this.mContext.getContentResolver(), "force_resizable_activities", 0) != 0;
        }

        public void updateDevEnableNonResizableMultiWindow() {
            WindowManagerService.this.mAtmService.mDevEnableNonResizableMultiWindow = Settings.Global.getInt(WindowManagerService.this.mContext.getContentResolver(), "enable_non_resizable_multi_window", 0) != 0;
        }

        public void updateDisplaySettingsLocation() {
            String string = Settings.Global.getString(WindowManagerService.this.mContext.getContentResolver(), "wm_display_settings_path");
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mDisplayWindowSettingsProvider.setBaseSettingsFilePath(string);
                    WindowManagerService.this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$SettingsObserver$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.SettingsObserver.this.lambda$updateDisplaySettingsLocation$1((DisplayContent) obj);
                        }
                    });
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateDisplaySettingsLocation$1(DisplayContent displayContent) {
            WindowManagerService.this.mDisplayWindowSettings.applySettingsToDisplayLocked(displayContent);
            displayContent.reconfigureDisplayLocked();
        }
    }

    public static void boostPriorityForLockedSection() {
        sThreadPriorityBooster.boost();
    }

    public static void resetPriorityAfterLockedSection() {
        sThreadPriorityBooster.reset();
    }

    public void openSurfaceTransaction() {
        try {
            Trace.traceBegin(32L, "openSurfaceTransaction");
            SurfaceControl.openTransaction();
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public void closeSurfaceTransaction(String str) {
        try {
            Trace.traceBegin(32L, "closeSurfaceTransaction");
            SurfaceControl.closeTransaction();
            this.mWindowTracing.logState(str);
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public static WindowManagerService main(Context context, InputManagerService inputManagerService, boolean z, WindowManagerPolicy windowManagerPolicy, ActivityTaskManagerService activityTaskManagerService) {
        return main(context, inputManagerService, z, windowManagerPolicy, activityTaskManagerService, new DisplayWindowSettingsProvider(), new Supplier() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda20
            @Override // java.util.function.Supplier
            public final Object get() {
                return new SurfaceControl.Transaction();
            }
        }, new Function() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda21
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return new SurfaceControl.Builder((SurfaceSession) obj);
            }
        });
    }

    @VisibleForTesting
    public static WindowManagerService main(final Context context, final InputManagerService inputManagerService, final boolean z, final WindowManagerPolicy windowManagerPolicy, final ActivityTaskManagerService activityTaskManagerService, final DisplayWindowSettingsProvider displayWindowSettingsProvider, final Supplier<SurfaceControl.Transaction> supplier, final Function<SurfaceSession, SurfaceControl.Builder> function) {
        final WindowManagerService[] windowManagerServiceArr = new WindowManagerService[1];
        DisplayThread.getHandler().runWithScissors(new Runnable() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerService.lambda$main$1(windowManagerServiceArr, context, inputManagerService, z, windowManagerPolicy, activityTaskManagerService, displayWindowSettingsProvider, supplier, function);
            }
        }, 0L);
        return windowManagerServiceArr[0];
    }

    public static /* synthetic */ void lambda$main$1(WindowManagerService[] windowManagerServiceArr, Context context, InputManagerService inputManagerService, boolean z, WindowManagerPolicy windowManagerPolicy, ActivityTaskManagerService activityTaskManagerService, DisplayWindowSettingsProvider displayWindowSettingsProvider, Supplier supplier, Function function) {
        windowManagerServiceArr[0] = new WindowManagerService(context, inputManagerService, z, windowManagerPolicy, activityTaskManagerService, displayWindowSettingsProvider, supplier, function);
    }

    public final void initPolicy() {
        UiThread.getHandler().runWithScissors(new Runnable() { // from class: com.android.server.wm.WindowManagerService.5
            @Override // java.lang.Runnable
            public void run() {
                WindowManagerPolicyThread.set(Thread.currentThread(), Looper.myLooper());
                WindowManagerService windowManagerService = WindowManagerService.this;
                windowManagerService.mPolicy.init(windowManagerService.mContext, windowManagerService);
            }
        }, 0L);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new WindowManagerShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public WindowManagerService(Context context, InputManagerService inputManagerService, boolean z, WindowManagerPolicy windowManagerPolicy, ActivityTaskManagerService activityTaskManagerService, DisplayWindowSettingsProvider displayWindowSettingsProvider, Supplier<SurfaceControl.Transaction> supplier, Function<SurfaceSession, SurfaceControl.Builder> function) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.wm.WindowManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED")) {
                    WindowManagerService.this.mKeyguardDisableHandler.updateKeyguardEnabled(getSendingUserId());
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mPriorityDumper = new PriorityDump.PriorityDumper() { // from class: com.android.server.wm.WindowManagerService.3
            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dumpCritical(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z2) {
                WindowManagerService.this.doDump(fileDescriptor, printWriter, new String[]{"-a"}, z2);
            }

            @Override // com.android.server.utils.PriorityDump.PriorityDumper
            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z2) {
                WindowManagerService.this.doDump(fileDescriptor, printWriter, strArr, z2);
            }
        };
        this.mShowAlertWindowNotifications = true;
        this.mSessions = new ArraySet<>();
        this.mWindowMap = new HashMap<>();
        this.mInputToWindowMap = new HashMap<>();
        this.mWindowReplacementTimeouts = new ArrayList<>();
        this.mResizingWindows = new ArrayList<>();
        this.mDisplayImePolicyCache = Collections.unmodifiableMap(new ArrayMap());
        this.mDestroySurface = new ArrayList<>();
        this.mForceRemoves = new ArrayList<>();
        this.mWaitingForDrawnCallbacks = new HashMap<>();
        this.mHidingNonSystemOverlayWindows = new ArrayList<>();
        this.mTmpRect = new Rect();
        this.mDisplayEnabled = false;
        this.mSystemBooted = false;
        this.mForceDisplayEnabled = false;
        this.mShowingBootMessages = false;
        this.mSystemReady = false;
        this.mBootAnimationStopped = false;
        this.mBootWaitForWindowsStartTime = -1L;
        this.mWallpaperVisibilityListeners = new WallpaperVisibilityListeners();
        this.mDisplayChangeController = null;
        this.mDisplayChangeControllerDeath = new IBinder.DeathRecipient() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda14
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                WindowManagerService.this.lambda$new$0();
            }
        };
        this.mDisplayFrozen = false;
        this.mDisplayFreezeTime = 0L;
        this.mLastDisplayFreezeDuration = 0;
        this.mLastFinishedFreezeSource = null;
        this.mSwitchingUser = false;
        this.mWindowsFreezingScreen = 0;
        this.mClientFreezingScreen = false;
        this.mAppsFreezingScreen = 0;
        this.mWindowsInsetsChanged = 0;
        HandlerC1915H handlerC1915H = new HandlerC1915H();
        this.f1164mH = handlerC1915H;
        this.mAnimationHandler = new Handler(AnimationThread.getHandler().getLooper());
        this.mMaximumObscuringOpacityForTouch = 0.8f;
        this.mWindowContextListenerController = new WindowContextListenerController();
        this.mContentRecordingController = new ContentRecordingController();
        this.mSurfaceSyncGroupController = new SurfaceSyncGroupController();
        this.mWindowAnimationScaleSetting = 1.0f;
        this.mTransitionAnimationScaleSetting = 1.0f;
        this.mAnimatorDurationScaleSetting = 1.0f;
        this.mAnimationsDisabled = false;
        this.mPointerLocationEnabled = false;
        this.mFrozenDisplayId = -1;
        this.mAnimationTransferMap = new ArrayMap<>();
        this.mWindowChangeListeners = new ArrayList<>();
        this.mWindowsChanged = false;
        this.mActivityManagerAppTransitionNotifier = new WindowManagerInternal.AppTransitionListener() { // from class: com.android.server.wm.WindowManagerService.4
            @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
            public void onAppTransitionCancelledLocked(boolean z2) {
            }

            @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
            public void onAppTransitionFinishedLocked(IBinder iBinder) {
                ActivityRecord activityRecord = WindowManagerService.this.mRoot.getActivityRecord(iBinder);
                if (activityRecord == null) {
                    return;
                }
                if (activityRecord.mLaunchTaskBehind && !WindowManagerService.this.isRecentsAnimationTarget(activityRecord)) {
                    WindowManagerService.this.mAtmService.mTaskSupervisor.scheduleLaunchTaskBehindComplete(activityRecord.token);
                    activityRecord.mLaunchTaskBehind = false;
                    return;
                }
                activityRecord.updateReportedVisibilityLocked();
                if (!activityRecord.mEnteringAnimation || WindowManagerService.this.isRecentsAnimationTarget(activityRecord)) {
                    return;
                }
                activityRecord.mEnteringAnimation = false;
                if (activityRecord.attachedToProcess()) {
                    try {
                        activityRecord.app.getThread().scheduleEnterAnimationComplete(activityRecord.token);
                    } catch (RemoteException unused) {
                    }
                }
            }
        };
        this.mAppFreezeListeners = new ArrayList<>();
        this.mInputManagerCallback = new InputManagerCallback(this);
        this.mMousePositionTracker = new MousePositionTracker();
        LockGuard.installLock(this, 5);
        this.mGlobalLock = activityTaskManagerService.getGlobalLock();
        this.mAtmService = activityTaskManagerService;
        this.mContext = context;
        this.mIsPc = context.getPackageManager().hasSystemFeature("android.hardware.type.pc");
        this.mAllowBootMessages = z;
        this.mLimitedAlphaCompositing = context.getResources().getBoolean(17891786);
        this.mHasPermanentDpad = context.getResources().getBoolean(17891698);
        this.mDrawLockTimeoutMillis = context.getResources().getInteger(17694832);
        this.mAllowAnimationsInLowPowerMode = context.getResources().getBoolean(17891346);
        this.mMaxUiWidth = context.getResources().getInteger(17694885);
        this.mDisableTransitionAnimation = context.getResources().getBoolean(17891608);
        this.mPerDisplayFocusEnabled = context.getResources().getBoolean(17891332);
        this.mAssistantOnTopOfDream = context.getResources().getBoolean(17891333);
        this.mLetterboxConfiguration = new LetterboxConfiguration(ActivityThread.currentActivityThread().getSystemUiContext());
        this.mInputManager = inputManagerService;
        DisplayManagerInternal displayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        this.mDisplayManagerInternal = displayManagerInternal;
        this.mPossibleDisplayInfoMapper = new PossibleDisplayInfoMapper(displayManagerInternal);
        this.mSurfaceControlFactory = function;
        this.mTransactionFactory = supplier;
        this.mTransaction = supplier.get();
        this.mPolicy = windowManagerPolicy;
        this.mAnimator = new WindowAnimator(this);
        this.mRoot = new RootWindowContainer(this);
        ContentResolver contentResolver = context.getContentResolver();
        this.mUseBLAST = Settings.Global.getInt(contentResolver, "use_blast_adapter_vr", 1) == 1;
        this.mSyncEngine = new BLASTSyncEngine(this);
        this.mWindowPlacerLocked = new WindowSurfacePlacer(this);
        SnapshotPersistQueue snapshotPersistQueue = new SnapshotPersistQueue();
        this.mSnapshotPersistQueue = snapshotPersistQueue;
        this.mTaskSnapshotController = new TaskSnapshotController(this, snapshotPersistQueue);
        this.mWindowTracing = WindowTracing.createDefaultAndStartLooper(this, Choreographer.getInstance());
        this.mTransitionTracer = new TransitionTracer();
        LocalServices.addService(WindowManagerPolicy.class, this.mPolicy);
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mKeyguardDisableHandler = KeyguardDisableHandler.create(context, this.mPolicy, handlerC1915H);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        PowerManagerInternal powerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mPowerManagerInternal = powerManagerInternal;
        if (powerManagerInternal != null) {
            powerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() { // from class: com.android.server.wm.WindowManagerService.6
                public int getServiceType() {
                    return 3;
                }

                public void onLowPowerModeChanged(PowerSaveState powerSaveState) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            boolean z2 = powerSaveState.batterySaverEnabled;
                            if (WindowManagerService.this.mAnimationsDisabled != z2) {
                                WindowManagerService windowManagerService = WindowManagerService.this;
                                if (!windowManagerService.mAllowAnimationsInLowPowerMode) {
                                    windowManagerService.mAnimationsDisabled = z2;
                                    WindowManagerService.this.dispatchNewAnimatorScaleLocked(null);
                                }
                            }
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            });
            this.mAnimationsDisabled = this.mPowerManagerInternal.getLowPowerState(3).batterySaverEnabled;
        }
        PowerManager.WakeLock newWakeLock = this.mPowerManager.newWakeLock(1, "SCREEN_FROZEN");
        this.mScreenFrozenLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        this.mRotationWatcherController = new RotationWatcherController(this);
        this.mDisplayNotificationController = new DisplayWindowListenerController(this);
        this.mTaskSystemBarsListenerController = new TaskSystemBarsListenerController();
        this.mActivityManager = ActivityManager.getService();
        this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mUmInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mAppOps = appOpsManager;
        AppOpsManager.OnOpChangedInternalListener onOpChangedInternalListener = new AppOpsManager.OnOpChangedInternalListener() { // from class: com.android.server.wm.WindowManagerService.7
            public void onOpChanged(int i, String str) {
                WindowManagerService.this.updateAppOpsState();
            }
        };
        appOpsManager.startWatchingMode(24, (String) null, (AppOpsManager.OnOpChangedListener) onOpChangedInternalListener);
        appOpsManager.startWatchingMode(45, (String) null, (AppOpsManager.OnOpChangedListener) onOpChangedInternalListener);
        this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mTestUtilityService = (TestUtilityService) LocalServices.getService(TestUtilityService.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        intentFilter.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
        context.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.wm.WindowManagerService.8
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                WindowManagerService.this.updateHiddenWhileSuspendedState(new ArraySet(Arrays.asList(stringArrayExtra)), "android.intent.action.PACKAGES_SUSPENDED".equals(intent.getAction()));
            }
        }, UserHandle.ALL, intentFilter, null, null);
        this.mWindowAnimationScaleSetting = getWindowAnimationScaleSetting();
        this.mTransitionAnimationScaleSetting = getTransitionAnimationScaleSetting();
        setAnimatorDurationScale(getAnimatorDurationScaleSetting());
        this.mForceDesktopModeOnExternalDisplays = Settings.Global.getInt(contentResolver, "force_desktop_mode_on_external_displays", 0) != 0;
        String string = Settings.Global.getString(contentResolver, "wm_display_settings_path");
        this.mDisplayWindowSettingsProvider = displayWindowSettingsProvider;
        if (string != null) {
            displayWindowSettingsProvider.setBaseSettingsFilePath(string);
        }
        this.mDisplayWindowSettings = new DisplayWindowSettings(this, displayWindowSettingsProvider);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter2, null, null);
        this.mLatencyTracker = LatencyTracker.getInstance(context);
        this.mSettingsObserver = new SettingsObserver();
        this.mSurfaceAnimationRunner = new SurfaceAnimationRunner(this.mTransactionFactory, this.mPowerManagerInternal);
        this.mAllowTheaterModeWakeFromLayout = context.getResources().getBoolean(17891362);
        this.mTaskPositioningController = new TaskPositioningController(this);
        this.mDragDropController = new DragDropController(this, handlerC1915H.getLooper());
        this.mHighRefreshRateDenylist = HighRefreshRateDenylist.create(context.getResources());
        WindowManagerConstants windowManagerConstants = new WindowManagerConstants(this, DeviceConfigInterface.REAL);
        this.mConstants = windowManagerConstants;
        windowManagerConstants.start(new HandlerExecutor(handlerC1915H));
        LocalServices.addService(WindowManagerInternal.class, new LocalService());
        LocalServices.addService(ImeTargetVisibilityPolicy.class, new ImeTargetVisibilityPolicyImpl());
        this.mEmbeddedWindowController = new EmbeddedWindowController(activityTaskManagerService);
        this.mDisplayAreaPolicyProvider = DisplayAreaPolicy.Provider.fromResources(context.getResources());
        this.mDisplayHashController = new DisplayHashController(context);
        setGlobalShadowSettings();
        this.mAnrController = new AnrController(this);
        this.mStartingSurfaceController = new StartingSurfaceController(this);
        this.mBlurController = new BlurController(context, this.mPowerManager);
        this.mTaskFpsCallbackController = new TaskFpsCallbackController(context);
        this.mAccessibilityController = new AccessibilityController(this);
    }

    public DisplayAreaPolicy.Provider getDisplayAreaPolicyProvider() {
        return this.mDisplayAreaPolicyProvider;
    }

    public final void setGlobalShadowSettings() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(null, R.styleable.Lighting, 0, 0);
        float dimension = obtainStyledAttributes.getDimension(3, 0.0f);
        float dimension2 = obtainStyledAttributes.getDimension(4, 0.0f);
        float dimension3 = obtainStyledAttributes.getDimension(2, 0.0f);
        float f = obtainStyledAttributes.getFloat(0, 0.0f);
        float f2 = obtainStyledAttributes.getFloat(1, 0.0f);
        obtainStyledAttributes.recycle();
        SurfaceControl.setGlobalShadowSettings(new float[]{0.0f, 0.0f, 0.0f, f}, new float[]{0.0f, 0.0f, 0.0f, f2}, dimension, dimension2, dimension3);
    }

    public final float getTransitionAnimationScaleSetting() {
        return WindowManager.fixScale(Settings.Global.getFloat(this.mContext.getContentResolver(), "transition_animation_scale", this.mContext.getResources().getFloat(17105064)));
    }

    public final float getAnimatorDurationScaleSetting() {
        return WindowManager.fixScale(Settings.Global.getFloat(this.mContext.getContentResolver(), "animator_duration_scale", this.mAnimatorDurationScaleSetting));
    }

    public final float getWindowAnimationScaleSetting() {
        return WindowManager.fixScale(Settings.Global.getFloat(this.mContext.getContentResolver(), "window_animation_scale", this.mWindowAnimationScaleSetting));
    }

    public void onInitReady() {
        initPolicy();
        Watchdog.getInstance().addMonitor(this);
        createWatermark();
        showEmulatorDisplayOverlayIfNeeded();
    }

    public InputManagerCallback getInputManagerCallback() {
        return this.mInputManagerCallback;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException) && ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.wtf(ProtoLogGroup.WM_ERROR, 371641947, 0, "Window Manager Crash %s", new Object[]{String.valueOf(e)});
            }
            throw e;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:243:0x049e A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x04bd A[Catch: all -> 0x07a5, TRY_ENTER, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:271:0x051e A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:274:0x0529 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:290:0x0563  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x056c A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:313:0x05de  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x05e1  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x05e6 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:320:0x05f1 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:323:0x0622  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0624  */
    /* JADX WARN: Removed duplicated region for block: B:327:0x0639 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:332:0x0659 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:337:0x066b A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:339:0x0670  */
    /* JADX WARN: Removed duplicated region for block: B:358:0x06bf A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:361:0x06c7 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:369:0x06e2 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:372:0x06ea  */
    /* JADX WARN: Removed duplicated region for block: B:374:0x06ed A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:377:0x06f9 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:380:0x070d A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:389:0x0743 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:391:0x074a A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:394:0x0766 A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Removed duplicated region for block: B:397:0x077f A[Catch: all -> 0x07a5, TryCatch #0 {all -> 0x07a5, blocks: (B:241:0x0497, B:243:0x049e, B:245:0x04a2, B:246:0x04b8, B:249:0x04bd, B:251:0x04c3, B:253:0x04c7, B:254:0x04d2, B:258:0x04d8, B:260:0x0509, B:265:0x0512, B:271:0x051e, B:274:0x0529, B:276:0x0531, B:278:0x0535, B:279:0x053f, B:283:0x0546, B:285:0x054b, B:287:0x054f, B:291:0x0565, B:293:0x056c, B:295:0x0576, B:297:0x0584, B:299:0x0588, B:301:0x05a8, B:303:0x05ae, B:311:0x05da, B:315:0x05e2, B:317:0x05e6, B:318:0x05eb, B:320:0x05f1, B:321:0x05f6, B:325:0x0625, B:328:0x063b, B:330:0x0642, B:348:0x069a, B:351:0x06aa, B:353:0x06b0, B:355:0x06b6, B:356:0x06b9, B:358:0x06bf, B:359:0x06c1, B:361:0x06c7, B:362:0x06c9, B:364:0x06cd, B:367:0x06d5, B:369:0x06e2, B:374:0x06ed, B:375:0x06f0, B:377:0x06f9, B:378:0x0702, B:380:0x070d, B:381:0x072e, B:383:0x0734, B:387:0x073d, B:389:0x0743, B:391:0x074a, B:392:0x074d, B:394:0x0766, B:396:0x077b, B:398:0x0785, B:399:0x078b, B:397:0x077f, B:366:0x06d3, B:332:0x0659, B:334:0x0663, B:337:0x066b, B:340:0x0672, B:341:0x067e, B:343:0x0684, B:344:0x068b, B:346:0x0693, B:308:0x05ba, B:289:0x0553, B:407:0x07a0, B:403:0x0795, B:404:0x079c), top: B:412:0x003f }] */
    /* JADX WARN: Type inference failed for: r5v13, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v22 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int addWindow(Session session, IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, int i4, InputChannel inputChannel, InsetsState insetsState, InsetsSourceControl.Array array, Rect rect, float[] fArr) {
        boolean z;
        WindowState windowState;
        int i5;
        int i6;
        int i7;
        boolean z2;
        boolean z3;
        int i8;
        int i9;
        DisplayContent displayContent;
        int[] iArr;
        WindowState windowState2;
        int i10;
        int i11;
        int i12;
        IBinder iBinder;
        ActivityRecord activityRecord;
        WindowToken windowToken;
        boolean z4;
        ActivityRecord activityRecord2;
        WindowState windowState3;
        boolean z5;
        boolean z6;
        ?? r5;
        DisplayContent displayContent2;
        int i13;
        int i14;
        int i15;
        int i16;
        WindowState windowState4;
        boolean z7;
        ActivityRecord activityRecord3;
        boolean z8;
        boolean z9;
        boolean z10;
        int i17;
        WindowState windowState5;
        WindowToken build;
        array.set((InsetsSourceControl[]) null);
        int[] iArr2 = new int[1];
        boolean z11 = (layoutParams.privateFlags & 1048576) != 0;
        int checkAddPermission = this.mPolicy.checkAddPermission(layoutParams.type, z11, layoutParams.packageName, iArr2);
        if (checkAddPermission != 0) {
            return checkAddPermission;
        }
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        int i18 = layoutParams.type;
        synchronized (this.mGlobalLock) {
            try {
                try {
                    boostPriorityForLockedSection();
                    if (!this.mDisplayReady) {
                        throw new IllegalStateException("Display has not been initialialized");
                    }
                    DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i2, layoutParams.token);
                    if (displayContentOrCreate == null) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -861859917, 1, "Attempted to add window to a display that does not exist: %d. Aborting.", new Object[]{Long.valueOf(i2)});
                        }
                        resetPriorityAfterLockedSection();
                        return -9;
                    } else if (!displayContentOrCreate.hasAccess(session.mUid)) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 435494046, 1, "Attempted to add window to a display for which the application does not have access: %d.  Aborting.", new Object[]{Long.valueOf(displayContentOrCreate.getDisplayId())});
                        }
                        resetPriorityAfterLockedSection();
                        return -9;
                    } else if (this.mWindowMap.containsKey(iWindow.asBinder())) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -507657818, 0, "Window %s is already added", new Object[]{String.valueOf(iWindow)});
                        }
                        resetPriorityAfterLockedSection();
                        return -5;
                    } else {
                        if (i18 < 1000 || i18 > 1999) {
                            z = z11;
                            windowState = null;
                        } else {
                            z = z11;
                            windowState = windowForClientLocked((Session) null, layoutParams.token, false);
                            if (windowState == null) {
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 631792420, 0, "Attempted to add window with token that is not a window: %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                }
                                resetPriorityAfterLockedSection();
                                return -2;
                            }
                            int i19 = windowState.mAttrs.type;
                            if (i19 >= 1000 && i19 <= 1999) {
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -2072089308, 0, "Attempted to add window with token that is a sub-window: %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                }
                                resetPriorityAfterLockedSection();
                                return -2;
                            }
                        }
                        if (i18 == 2030 && !displayContentOrCreate.isPrivate()) {
                            if (ProtoLogCache.WM_ERROR_enabled) {
                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -784959154, 0, "Attempted to add private presentation window to a non-private display.  Aborting.", (Object[]) null);
                            }
                            resetPriorityAfterLockedSection();
                            return -8;
                        } else if (i18 == 2037 && !displayContentOrCreate.getDisplay().isPublicPresentation()) {
                            if (ProtoLogCache.WM_ERROR_enabled) {
                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1670695197, 0, "Attempted to add presentation window to a non-suitable display.  Aborting.", (Object[]) null);
                            }
                            resetPriorityAfterLockedSection();
                            return -9;
                        } else {
                            int userId = UserHandle.getUserId(session.mUid);
                            if (i3 != userId) {
                                try {
                                    i5 = i18;
                                    i6 = callingPid;
                                    this.mAmInternal.handleIncomingUser(callingPid, callingUid, i3, false, 0, (String) null, (String) null);
                                    i7 = i3;
                                    z2 = true;
                                    z3 = false;
                                } catch (Exception unused) {
                                    if (ProtoLogCache.WM_ERROR_enabled) {
                                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 315395835, 1, "Trying to add window with invalid user=%d", new Object[]{Long.valueOf(i3)});
                                    }
                                    resetPriorityAfterLockedSection();
                                    return -11;
                                }
                            } else {
                                i5 = i18;
                                i6 = callingPid;
                                z2 = true;
                                z3 = false;
                                i7 = userId;
                            }
                            boolean z12 = windowState != null ? z2 : z3;
                            WindowToken windowToken2 = displayContentOrCreate.getWindowToken(z12 ? windowState.mAttrs.token : layoutParams.token);
                            int i20 = z12 ? windowState.mAttrs.type : i5;
                            IBinder iBinder2 = layoutParams.mWindowContextToken;
                            if (windowToken2 == null) {
                                i9 = i6;
                                boolean z13 = z;
                                displayContent = displayContentOrCreate;
                                iArr = iArr2;
                                windowState2 = windowState;
                                if (!unprivilegedAppCanCreateTokenWith(windowState, callingUid, i5, i20, layoutParams.token, layoutParams.packageName)) {
                                    resetPriorityAfterLockedSection();
                                    return -1;
                                }
                                if (z12) {
                                    build = windowState2.mToken;
                                    i8 = i5;
                                } else if (this.mWindowContextListenerController.hasListener(iBinder2)) {
                                    IBinder iBinder3 = layoutParams.token;
                                    if (iBinder3 == null) {
                                        iBinder3 = iBinder2;
                                    }
                                    i8 = i5;
                                    build = new WindowToken.Builder(this, iBinder3, i8).setDisplayContent(displayContent).setOwnerCanManageAppTokens(session.mCanAddInternalSystemWindow).setRoundedCornerOverlay(z13).setFromClientToken(true).setOptions(this.mWindowContextListenerController.getOptions(iBinder2)).build();
                                } else {
                                    i8 = i5;
                                    IBinder iBinder4 = layoutParams.token;
                                    if (iBinder4 == null) {
                                        iBinder4 = iWindow.asBinder();
                                    }
                                    build = new WindowToken.Builder(this, iBinder4, i8).setDisplayContent(displayContent).setOwnerCanManageAppTokens(session.mCanAddInternalSystemWindow).setRoundedCornerOverlay(z13).build();
                                }
                                windowToken = build;
                                iBinder = iBinder2;
                                i12 = callingUid;
                                activityRecord2 = null;
                                activityRecord = null;
                                i11 = 2011;
                                i10 = 3;
                            } else {
                                i8 = i5;
                                i9 = i6;
                                displayContent = displayContentOrCreate;
                                iArr = iArr2;
                                windowState2 = windowState;
                                if (i20 >= 1 && i20 <= 99) {
                                    activityRecord2 = windowToken2.asActivityRecord();
                                    if (activityRecord2 == null) {
                                        if (ProtoLogCache.WM_ERROR_enabled) {
                                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 246676969, 0, "Attempted to add window with non-application token .%s Aborting.", new Object[]{String.valueOf(windowToken2)});
                                        }
                                        resetPriorityAfterLockedSection();
                                        return -3;
                                    } else if (activityRecord2.getParent() == null) {
                                        if (ProtoLogCache.WM_ERROR_enabled) {
                                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -853226675, 0, "Attempted to add window with exiting application token .%s Aborting.", new Object[]{String.valueOf(windowToken2)});
                                        }
                                        resetPriorityAfterLockedSection();
                                        return -4;
                                    } else {
                                        i10 = 3;
                                        if (i8 == 3) {
                                            if (activityRecord2.mStartingWindow != null) {
                                                if (ProtoLogCache.WM_ERROR_enabled) {
                                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -167822951, 0, "Attempted to add starting window to token with already existing starting window", (Object[]) null);
                                                }
                                                resetPriorityAfterLockedSection();
                                                return -5;
                                            } else if (activityRecord2.mStartingData == null) {
                                                if (ProtoLogCache.WM_ERROR_enabled) {
                                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1804245629, 0, "Attempted to add starting window to token but already cleaned", (Object[]) null);
                                                }
                                                resetPriorityAfterLockedSection();
                                                return -5;
                                            }
                                        }
                                        windowToken = windowToken2;
                                        iBinder = iBinder2;
                                        i12 = callingUid;
                                        activityRecord = null;
                                        i11 = 2011;
                                    }
                                } else {
                                    i10 = 3;
                                    i11 = 2011;
                                    if (i20 == 2011) {
                                        if (windowToken2.windowType != 2011) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1949279037, 0, "Attempted to add input method window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -1;
                                        }
                                    } else if (i20 == 2031) {
                                        if (windowToken2.windowType != 2031) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1389772804, 0, "Attempted to add voice interaction window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -1;
                                        }
                                    } else if (i20 == 2013) {
                                        if (windowToken2.windowType != 2013) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1915280162, 0, "Attempted to add wallpaper window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -1;
                                        }
                                    } else if (i20 == 2032) {
                                        if (windowToken2.windowType != 2032) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1976930686, 0, "Attempted to add Accessibility overlay window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -1;
                                        }
                                    } else if (i8 == 2005) {
                                        i12 = callingUid;
                                        boolean doesAddToastWindowRequireToken = doesAddToastWindowRequireToken(layoutParams.packageName, i12, windowState2);
                                        if (doesAddToastWindowRequireToken && windowToken2.windowType != 2005) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 662572728, 0, "Attempted to add a toast window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -1;
                                        }
                                        windowToken = windowToken2;
                                        z4 = doesAddToastWindowRequireToken;
                                        iBinder = iBinder2;
                                        activityRecord2 = null;
                                        activityRecord = null;
                                        int i21 = iArr[0];
                                        int i22 = session.mUid;
                                        boolean z14 = session.mCanAddInternalSystemWindow;
                                        ActivityRecord activityRecord4 = activityRecord2;
                                        int i23 = i8;
                                        int i24 = i12;
                                        int i25 = i11;
                                        int i26 = i10;
                                        DisplayContent displayContent3 = displayContent;
                                        windowState3 = new WindowState(this, session, iWindow, windowToken, windowState2, i21, layoutParams, i, i22, i7, z14);
                                        if (windowState3.mDeathRecipient == null) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1770075711, 0, "Adding window client %s that is dead, aborting.", new Object[]{String.valueOf(iWindow.asBinder())});
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -4;
                                        } else if (windowState3.getDisplayContent() == null) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1720696061, 0, "Adding window to Display that has been removed.", (Object[]) null);
                                            }
                                            resetPriorityAfterLockedSection();
                                            return -9;
                                        } else {
                                            DisplayPolicy displayPolicy = displayContent3.getDisplayPolicy();
                                            displayPolicy.adjustWindowParamsLw(windowState3, windowState3.mAttrs);
                                            int i27 = i9;
                                            layoutParams.flags = sanitizeFlagSlippery(layoutParams.flags, windowState3.getName(), i24, i27);
                                            layoutParams.inputFeatures = sanitizeSpyWindow(layoutParams.inputFeatures, windowState3.getName(), i24, i27);
                                            windowState3.setRequestedVisibleTypes(i4);
                                            int validateAddingWindowLw = displayPolicy.validateAddingWindowLw(layoutParams, i27, i24);
                                            if (validateAddingWindowLw != 0) {
                                                resetPriorityAfterLockedSection();
                                                return validateAddingWindowLw;
                                            }
                                            if (inputChannel != null) {
                                                z5 = true;
                                                r5 = 1;
                                                if ((layoutParams.inputFeatures & 1) == 0) {
                                                    z6 = true;
                                                    if (z6) {
                                                        windowState3.openInputChannel(inputChannel);
                                                    }
                                                    if (i23 != 2005) {
                                                        displayContent2 = displayContent3;
                                                        if (!displayContent2.canAddToastWindowForUid(i24)) {
                                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -883738232, 0, "Adding more than one toast window for UID at a time.", (Object[]) null);
                                                            }
                                                            resetPriorityAfterLockedSection();
                                                            return -5;
                                                        } else if (z4 || (layoutParams.flags & 8) == 0 || (windowState5 = displayContent2.mCurrentFocus) == null || windowState5.mOwnerUid != i24) {
                                                            HandlerC1915H handlerC1915H = this.f1164mH;
                                                            handlerC1915H.sendMessageDelayed(handlerC1915H.obtainMessage(52, windowState3), windowState3.mAttrs.hideTimeoutMilliseconds);
                                                        }
                                                    } else {
                                                        displayContent2 = displayContent3;
                                                    }
                                                    if (!windowState3.isChildWindow()) {
                                                        IBinder iBinder5 = iBinder;
                                                        if (this.mWindowContextListenerController.hasListener(iBinder5)) {
                                                            int windowType = this.mWindowContextListenerController.getWindowType(iBinder5);
                                                            Bundle options = this.mWindowContextListenerController.getOptions(iBinder5);
                                                            if (i23 != windowType) {
                                                                if (ProtoLogCache.WM_ERROR_enabled) {
                                                                    long j = i23;
                                                                    i17 = i23;
                                                                    long j2 = windowType;
                                                                    ProtoLogGroup protoLogGroup = ProtoLogGroup.WM_ERROR;
                                                                    Object[] objArr = new Object[2];
                                                                    objArr[0] = Long.valueOf(j);
                                                                    objArr[r5] = Long.valueOf(j2);
                                                                    ProtoLogImpl.w(protoLogGroup, 1252594551, 5, "Window types in WindowContext and LayoutParams.type should match! Type from LayoutParams is %d, but type from WindowContext is %d", objArr);
                                                                } else {
                                                                    i17 = i23;
                                                                }
                                                                if (!WindowProviderService.isWindowProviderService(options)) {
                                                                    resetPriorityAfterLockedSection();
                                                                    return -10;
                                                                }
                                                                i13 = i17;
                                                                windowState4 = windowState3;
                                                                i14 = 2013;
                                                                i15 = i26;
                                                                i16 = i25;
                                                                int i28 = !this.mUseBLAST ? 8 : 0;
                                                                if (displayContent2.mCurrentFocus == null) {
                                                                    displayContent2.mWinAddedSinceNullFocus.add(windowState4);
                                                                }
                                                                if (excludeWindowTypeFromTapOutTask(i13)) {
                                                                    displayContent2.mTapExcludedWindows.add(windowState4);
                                                                }
                                                                windowState4.attach();
                                                                this.mWindowMap.put(iWindow.asBinder(), windowState4);
                                                                windowState4.initAppOpsState();
                                                                windowState4.setHiddenWhileSuspended(this.mPmInternal.isPackageSuspended(windowState4.getOwningPackage(), UserHandle.getUserId(windowState4.getOwningUid())));
                                                                windowState4.setForceHideNonSystemOverlayWindowIfNeeded(this.mHidingNonSystemOverlayWindows.isEmpty() ? r5 : false);
                                                                windowState4.mToken.addWindow(windowState4);
                                                                displayPolicy.addWindowLw(windowState4, layoutParams);
                                                                displayPolicy.setDropInputModePolicy(windowState4, windowState4.mAttrs);
                                                                if (i13 != i15 && activityRecord4 != null) {
                                                                    activityRecord4.attachStartingWindow(windowState4);
                                                                    if (ProtoLogCache.WM_DEBUG_STARTING_WINDOW_enabled) {
                                                                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW, 150351993, 0, (String) null, new Object[]{String.valueOf(activityRecord4), String.valueOf(windowState4)});
                                                                    }
                                                                } else {
                                                                    if (i13 != i16 && (windowState4.getAttrs().flags & 16) == 0) {
                                                                        displayContent2.setInputMethodWindowLocked(windowState4);
                                                                    } else if (i13 != 2012) {
                                                                        displayContent2.computeImeTarget(r5);
                                                                    } else if (i13 == i14) {
                                                                        displayContent2.mWallpaperController.clearLastWallpaperTimeoutTime();
                                                                        displayContent2.pendingLayoutChanges |= 4;
                                                                    } else if (windowState4.hasWallpaper()) {
                                                                        displayContent2.pendingLayoutChanges |= 4;
                                                                    } else if (displayContent2.mWallpaperController.isBelowWallpaperTarget(windowState4)) {
                                                                        displayContent2.pendingLayoutChanges |= 4;
                                                                    }
                                                                    z7 = false;
                                                                    WindowStateAnimator windowStateAnimator = windowState4.mWinAnimator;
                                                                    windowStateAnimator.mEnterAnimationPending = r5;
                                                                    windowStateAnimator.mEnteringAnimation = r5;
                                                                    if (!windowState4.mTransitionController.isShellTransitionsEnabled() && activityRecord4 != null && activityRecord4.isVisible() && !prepareWindowReplacementTransition(activityRecord4)) {
                                                                        prepareNoneTransitionForRelaunching(activityRecord4);
                                                                    }
                                                                    if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                                                                        i28 |= 4;
                                                                    }
                                                                    if (displayContent2.isInTouchMode()) {
                                                                        i28 |= 1;
                                                                    }
                                                                    activityRecord3 = windowState4.mActivityRecord;
                                                                    if (activityRecord3 != null || activityRecord3.isClientVisible()) {
                                                                        i28 |= 2;
                                                                    }
                                                                    displayContent2.getInputMonitor().setUpdateInputWindowsNeededLw();
                                                                    if (windowState4.canReceiveKeys()) {
                                                                        z8 = false;
                                                                        z9 = z7;
                                                                    } else {
                                                                        z8 = updateFocusedWindowLocked(r5, false);
                                                                        z9 = z7;
                                                                        if (z8) {
                                                                            z9 = false;
                                                                        }
                                                                    }
                                                                    if (z9) {
                                                                        displayContent2.computeImeTarget(r5);
                                                                    }
                                                                    windowState4.getParent().assignChildLayers();
                                                                    if (z8) {
                                                                        displayContent2.getInputMonitor().setInputFocusLw(displayContent2.mCurrentFocus, false);
                                                                    }
                                                                    displayContent2.getInputMonitor().updateInputWindowsLw(false);
                                                                    if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                                                                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1427184084, 0, (String) null, new Object[]{String.valueOf(iWindow.asBinder()), String.valueOf(windowState4), String.valueOf(Debug.getCallers(5))});
                                                                    }
                                                                    z10 = (windowState4.isVisibleRequestedOrAdding() || !displayContent2.updateOrientation()) ? false : r5;
                                                                    if (windowState4.providesNonDecorInsets()) {
                                                                        z10 |= displayPolicy.updateDecorInsetsInfo();
                                                                    }
                                                                    if (z10) {
                                                                        displayContent2.sendNewConfiguration();
                                                                    }
                                                                    displayContent2.getInsetsStateController().updateAboveInsetsState(false);
                                                                    insetsState.set(windowState4.getCompatInsetsState(), (boolean) r5);
                                                                    getInsetsSourceControls(windowState4, array);
                                                                    if (!windowState4.mLayoutAttached) {
                                                                        rect.set(windowState4.getParentWindow().getFrame());
                                                                        float f = windowState4.mInvGlobalScale;
                                                                        if (f != 1.0f) {
                                                                            rect.scale(f);
                                                                        }
                                                                    } else {
                                                                        rect.set(0, 0, -1, -1);
                                                                    }
                                                                    fArr[0] = windowState4.getCompatScaleForClient();
                                                                    resetPriorityAfterLockedSection();
                                                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                                                    return i28;
                                                                }
                                                                z7 = r5;
                                                                WindowStateAnimator windowStateAnimator2 = windowState4.mWinAnimator;
                                                                windowStateAnimator2.mEnterAnimationPending = r5;
                                                                windowStateAnimator2.mEnteringAnimation = r5;
                                                                if (!windowState4.mTransitionController.isShellTransitionsEnabled()) {
                                                                    prepareNoneTransitionForRelaunching(activityRecord4);
                                                                }
                                                                if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                                                                }
                                                                if (displayContent2.isInTouchMode()) {
                                                                }
                                                                activityRecord3 = windowState4.mActivityRecord;
                                                                if (activityRecord3 != null) {
                                                                }
                                                                i28 |= 2;
                                                                displayContent2.getInputMonitor().setUpdateInputWindowsNeededLw();
                                                                if (windowState4.canReceiveKeys()) {
                                                                }
                                                                if (z9) {
                                                                }
                                                                windowState4.getParent().assignChildLayers();
                                                                if (z8) {
                                                                }
                                                                displayContent2.getInputMonitor().updateInputWindowsLw(false);
                                                                if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                                                                }
                                                                if (windowState4.isVisibleRequestedOrAdding()) {
                                                                }
                                                                if (windowState4.providesNonDecorInsets()) {
                                                                }
                                                                if (z10) {
                                                                }
                                                                displayContent2.getInsetsStateController().updateAboveInsetsState(false);
                                                                insetsState.set(windowState4.getCompatInsetsState(), (boolean) r5);
                                                                getInsetsSourceControls(windowState4, array);
                                                                if (!windowState4.mLayoutAttached) {
                                                                }
                                                                fArr[0] = windowState4.getCompatScaleForClient();
                                                                resetPriorityAfterLockedSection();
                                                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                                                return i28;
                                                            }
                                                            i13 = i23;
                                                            i14 = 2013;
                                                            i15 = i26;
                                                            i16 = i25;
                                                            windowState4 = windowState3;
                                                            this.mWindowContextListenerController.registerWindowContainerListener(iBinder5, windowToken, i24, i13, options);
                                                            if (!this.mUseBLAST) {
                                                            }
                                                            if (displayContent2.mCurrentFocus == null) {
                                                            }
                                                            if (excludeWindowTypeFromTapOutTask(i13)) {
                                                            }
                                                            windowState4.attach();
                                                            this.mWindowMap.put(iWindow.asBinder(), windowState4);
                                                            windowState4.initAppOpsState();
                                                            windowState4.setHiddenWhileSuspended(this.mPmInternal.isPackageSuspended(windowState4.getOwningPackage(), UserHandle.getUserId(windowState4.getOwningUid())));
                                                            windowState4.setForceHideNonSystemOverlayWindowIfNeeded(this.mHidingNonSystemOverlayWindows.isEmpty() ? r5 : false);
                                                            windowState4.mToken.addWindow(windowState4);
                                                            displayPolicy.addWindowLw(windowState4, layoutParams);
                                                            displayPolicy.setDropInputModePolicy(windowState4, windowState4.mAttrs);
                                                            if (i13 != i15) {
                                                            }
                                                            if (i13 != i16) {
                                                            }
                                                            if (i13 != 2012) {
                                                            }
                                                        }
                                                    }
                                                    i13 = i23;
                                                    windowState4 = windowState3;
                                                    i14 = 2013;
                                                    i15 = i26;
                                                    i16 = i25;
                                                    if (!this.mUseBLAST) {
                                                    }
                                                    if (displayContent2.mCurrentFocus == null) {
                                                    }
                                                    if (excludeWindowTypeFromTapOutTask(i13)) {
                                                    }
                                                    windowState4.attach();
                                                    this.mWindowMap.put(iWindow.asBinder(), windowState4);
                                                    windowState4.initAppOpsState();
                                                    windowState4.setHiddenWhileSuspended(this.mPmInternal.isPackageSuspended(windowState4.getOwningPackage(), UserHandle.getUserId(windowState4.getOwningUid())));
                                                    windowState4.setForceHideNonSystemOverlayWindowIfNeeded(this.mHidingNonSystemOverlayWindows.isEmpty() ? r5 : false);
                                                    windowState4.mToken.addWindow(windowState4);
                                                    displayPolicy.addWindowLw(windowState4, layoutParams);
                                                    displayPolicy.setDropInputModePolicy(windowState4, windowState4.mAttrs);
                                                    if (i13 != i15) {
                                                    }
                                                    if (i13 != i16) {
                                                    }
                                                    if (i13 != 2012) {
                                                    }
                                                }
                                            } else {
                                                z5 = true;
                                            }
                                            z6 = false;
                                            r5 = z5;
                                            if (z6) {
                                            }
                                            if (i23 != 2005) {
                                            }
                                            if (!windowState3.isChildWindow()) {
                                            }
                                            i13 = i23;
                                            windowState4 = windowState3;
                                            i14 = 2013;
                                            i15 = i26;
                                            i16 = i25;
                                            if (!this.mUseBLAST) {
                                            }
                                            if (displayContent2.mCurrentFocus == null) {
                                            }
                                            if (excludeWindowTypeFromTapOutTask(i13)) {
                                            }
                                            windowState4.attach();
                                            this.mWindowMap.put(iWindow.asBinder(), windowState4);
                                            windowState4.initAppOpsState();
                                            windowState4.setHiddenWhileSuspended(this.mPmInternal.isPackageSuspended(windowState4.getOwningPackage(), UserHandle.getUserId(windowState4.getOwningUid())));
                                            windowState4.setForceHideNonSystemOverlayWindowIfNeeded(this.mHidingNonSystemOverlayWindows.isEmpty() ? r5 : false);
                                            windowState4.mToken.addWindow(windowState4);
                                            displayPolicy.addWindowLw(windowState4, layoutParams);
                                            displayPolicy.setDropInputModePolicy(windowState4, windowState4.mAttrs);
                                            if (i13 != i15) {
                                            }
                                            if (i13 != i16) {
                                            }
                                            if (i13 != 2012) {
                                            }
                                        }
                                    } else {
                                        i12 = callingUid;
                                        if (i8 == 2035) {
                                            if (windowToken2.windowType != 2035) {
                                                if (ProtoLogCache.WM_ERROR_enabled) {
                                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1060365734, 0, "Attempted to add QS dialog window with bad token %s.  Aborting.", new Object[]{String.valueOf(layoutParams.token)});
                                                }
                                                resetPriorityAfterLockedSection();
                                                return -1;
                                            }
                                        } else if (windowToken2.asActivityRecord() != null) {
                                            if (ProtoLogCache.WM_ERROR_enabled) {
                                                iBinder = iBinder2;
                                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 372792199, 1, "Non-null activity for system window of rootType=%d", new Object[]{Long.valueOf(i20)});
                                            } else {
                                                iBinder = iBinder2;
                                            }
                                            activityRecord = null;
                                            layoutParams.token = null;
                                            windowToken2 = new WindowToken.Builder(this, iWindow.asBinder(), i8).setDisplayContent(displayContent).setOwnerCanManageAppTokens(session.mCanAddInternalSystemWindow).build();
                                            windowToken = windowToken2;
                                            activityRecord2 = activityRecord;
                                            z4 = false;
                                            int i212 = iArr[0];
                                            int i222 = session.mUid;
                                            boolean z142 = session.mCanAddInternalSystemWindow;
                                            ActivityRecord activityRecord42 = activityRecord2;
                                            int i232 = i8;
                                            int i242 = i12;
                                            int i252 = i11;
                                            int i262 = i10;
                                            DisplayContent displayContent32 = displayContent;
                                            windowState3 = new WindowState(this, session, iWindow, windowToken, windowState2, i212, layoutParams, i, i222, i7, z142);
                                            if (windowState3.mDeathRecipient == null) {
                                            }
                                        }
                                        iBinder = iBinder2;
                                        activityRecord = null;
                                        windowToken = windowToken2;
                                        activityRecord2 = activityRecord;
                                        z4 = false;
                                        int i2122 = iArr[0];
                                        int i2222 = session.mUid;
                                        boolean z1422 = session.mCanAddInternalSystemWindow;
                                        ActivityRecord activityRecord422 = activityRecord2;
                                        int i2322 = i8;
                                        int i2422 = i12;
                                        int i2522 = i11;
                                        int i2622 = i10;
                                        DisplayContent displayContent322 = displayContent;
                                        windowState3 = new WindowState(this, session, iWindow, windowToken, windowState2, i2122, layoutParams, i, i2222, i7, z1422);
                                        if (windowState3.mDeathRecipient == null) {
                                        }
                                    }
                                    iBinder = iBinder2;
                                    i12 = callingUid;
                                    activityRecord = null;
                                    windowToken = windowToken2;
                                    activityRecord2 = activityRecord;
                                    z4 = false;
                                    int i21222 = iArr[0];
                                    int i22222 = session.mUid;
                                    boolean z14222 = session.mCanAddInternalSystemWindow;
                                    ActivityRecord activityRecord4222 = activityRecord2;
                                    int i23222 = i8;
                                    int i24222 = i12;
                                    int i25222 = i11;
                                    int i26222 = i10;
                                    DisplayContent displayContent3222 = displayContent;
                                    windowState3 = new WindowState(this, session, iWindow, windowToken, windowState2, i21222, layoutParams, i, i22222, i7, z14222);
                                    if (windowState3.mDeathRecipient == null) {
                                    }
                                }
                            }
                            z4 = false;
                            int i212222 = iArr[0];
                            int i222222 = session.mUid;
                            boolean z142222 = session.mCanAddInternalSystemWindow;
                            ActivityRecord activityRecord42222 = activityRecord2;
                            int i232222 = i8;
                            int i242222 = i12;
                            int i252222 = i11;
                            int i262222 = i10;
                            DisplayContent displayContent32222 = displayContent;
                            windowState3 = new WindowState(this, session, iWindow, windowToken, windowState2, i212222, layoutParams, i, i222222, i7, z142222);
                            if (windowState3.mDeathRecipient == null) {
                            }
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final boolean unprivilegedAppCanCreateTokenWith(WindowState windowState, int i, int i2, int i3, IBinder iBinder, String str) {
        if (i3 >= 1 && i3 <= 99) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1113134997, 0, "Attempted to add application window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i3 == 2011) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -2039580386, 0, "Attempted to add input method window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i3 == 2031) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -914253865, 0, "Attempted to add voice interaction window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i3 == 2013) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 424524729, 0, "Attempted to add wallpaper window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i3 == 2035) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 898863925, 0, "Attempted to add QS dialog window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i3 == 2032) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1042574499, 0, "Attempted to add Accessibility overlay window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else if (i2 == 2005 && doesAddToastWindowRequireToken(str, i, windowState)) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1331177619, 0, "Attempted to add a toast window with unknown token %s.  Aborting.", new Object[]{String.valueOf(iBinder)});
            }
            return false;
        } else {
            return true;
        }
    }

    public final DisplayContent getDisplayContentOrCreate(int i, IBinder iBinder) {
        WindowToken windowToken;
        if (iBinder != null && (windowToken = this.mRoot.getWindowToken(iBinder)) != null) {
            return windowToken.getDisplayContent();
        }
        return this.mRoot.getDisplayContentOrCreate(i);
    }

    public final boolean doesAddToastWindowRequireToken(String str, int i, WindowState windowState) {
        if (windowState != null) {
            ActivityRecord activityRecord = windowState.mActivityRecord;
            return activityRecord != null && activityRecord.mTargetSdk >= 26;
        }
        ApplicationInfo applicationInfo = this.mPmInternal.getApplicationInfo(str, 0L, 1000, UserHandle.getUserId(i));
        if (applicationInfo != null && applicationInfo.uid == i) {
            return applicationInfo.targetSdkVersion >= 26;
        }
        throw new SecurityException("Package " + str + " not in UID " + i);
    }

    public final boolean prepareWindowReplacementTransition(ActivityRecord activityRecord) {
        activityRecord.clearAllDrawn();
        WindowState replacingWindow = activityRecord.getReplacingWindow();
        if (replacingWindow == null) {
            return false;
        }
        Rect rect = new Rect(replacingWindow.getFrame());
        WindowManager.LayoutParams layoutParams = replacingWindow.mAttrs;
        rect.inset(replacingWindow.getInsetsStateWithVisibilityOverride().calculateVisibleInsets(rect, layoutParams.type, replacingWindow.getWindowingMode(), layoutParams.softInputMode, layoutParams.flags));
        DisplayContent displayContent = activityRecord.getDisplayContent();
        displayContent.mOpeningApps.add(activityRecord);
        displayContent.prepareAppTransition(5);
        displayContent.mAppTransition.overridePendingAppTransitionClipReveal(rect.left, rect.top, rect.width(), rect.height());
        displayContent.executeAppTransition();
        return true;
    }

    public final void prepareNoneTransitionForRelaunching(ActivityRecord activityRecord) {
        DisplayContent displayContent = activityRecord.getDisplayContent();
        if (this.mDisplayFrozen && !displayContent.mOpeningApps.contains(activityRecord) && activityRecord.isRelaunching()) {
            displayContent.mOpeningApps.add(activityRecord);
            displayContent.prepareAppTransition(0);
            displayContent.executeAppTransition();
        }
    }

    public void refreshScreenCaptureDisabled() {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Only system can call refreshScreenCaptureDisabled.");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.refreshSecureSurfaceState();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void removeWindow(Session session, IWindow iWindow) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked != null) {
                    windowForClientLocked.removeIfPossible();
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mEmbeddedWindowController.remove(iWindow);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void postWindowRemoveCleanupLocked(WindowState windowState) {
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -622997754, 0, (String) null, new Object[]{String.valueOf(windowState)});
        }
        this.mWindowMap.remove(windowState.mClient.asBinder());
        DisplayContent displayContent = windowState.getDisplayContent();
        displayContent.getDisplayRotation().markForSeamlessRotation(windowState, false);
        windowState.resetAppOpsState();
        if (displayContent.mCurrentFocus == null) {
            displayContent.mWinRemovedSinceNullFocus.add(windowState);
        }
        this.mEmbeddedWindowController.onWindowRemoved(windowState);
        this.mResizingWindows.remove(windowState);
        updateNonSystemOverlayWindowsVisibilityIfNeeded(windowState, false);
        this.mWindowsChanged = true;
        if (ProtoLogCache.WM_DEBUG_WINDOW_MOVEMENT_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_MOVEMENT, -193782861, 0, (String) null, new Object[]{String.valueOf(windowState)});
        }
        DisplayContent displayContent2 = windowState.getDisplayContent();
        if (displayContent2.mInputMethodWindow == windowState) {
            displayContent2.setInputMethodWindowLocked(null);
        }
        WindowToken windowToken = windowState.mToken;
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1963461591, 0, (String) null, new Object[]{String.valueOf(windowState), String.valueOf(windowToken)});
        }
        if (windowToken.isEmpty() && !windowToken.mPersistOnEmpty) {
            windowToken.removeImmediately();
        }
        ActivityRecord activityRecord = windowState.mActivityRecord;
        if (activityRecord != null) {
            activityRecord.postWindowRemoveStartingWindowCleanup(windowState);
        }
        if (windowState.mAttrs.type == 2013) {
            displayContent.mWallpaperController.clearLastWallpaperTimeoutTime();
            displayContent.pendingLayoutChanges |= 4;
        } else if (displayContent.mWallpaperController.isWallpaperTarget(windowState)) {
            displayContent.pendingLayoutChanges |= 4;
        }
        if (!this.mWindowPlacerLocked.isInLayout()) {
            displayContent.assignWindowLayers(true);
            this.mWindowPlacerLocked.performSurfacePlacement();
            ActivityRecord activityRecord2 = windowState.mActivityRecord;
            if (activityRecord2 != null) {
                activityRecord2.updateReportedVisibilityLocked();
            }
        }
        displayContent.getInputMonitor().updateInputWindowsLw(true);
    }

    public final void updateHiddenWhileSuspendedState(ArraySet<String> arraySet, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.updateHiddenWhileSuspendedState(arraySet, z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void updateAppOpsState() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.updateAppOpsState();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void clearTouchableRegion(Session session, IWindow iWindow) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                windowForClientLocked(session, iWindow, false).clearClientTouchableRegion();
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setInsetsWindow(Session session, IWindow iWindow, int i, Rect rect, Rect rect2, Region region) {
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked != null) {
                    windowForClientLocked.mGivenInsetsPending = false;
                    windowForClientLocked.mGivenContentInsets.set(rect);
                    windowForClientLocked.mGivenVisibleInsets.set(rect2);
                    windowForClientLocked.mGivenTouchableRegion.set(region);
                    windowForClientLocked.mTouchableInsets = i;
                    float f = windowForClientLocked.mGlobalScale;
                    if (f != 1.0f) {
                        windowForClientLocked.mGivenContentInsets.scale(f);
                        windowForClientLocked.mGivenVisibleInsets.scale(windowForClientLocked.mGlobalScale);
                        windowForClientLocked.mGivenTouchableRegion.scale(windowForClientLocked.mGlobalScale);
                    }
                    windowForClientLocked.setDisplayLayoutNeeded();
                    windowForClientLocked.updateSourceFrame(windowForClientLocked.getFrame());
                    this.mWindowPlacerLocked.performSurfacePlacement();
                    windowForClientLocked.getDisplayContent().getInputMonitor().updateInputWindowsLw(true);
                    if (this.mAccessibilityController.hasCallbacks()) {
                        this.mAccessibilityController.onSomeWindowResizedOrMovedWithCallingUid(callingUid, windowForClientLocked.getDisplayContent().getDisplayId());
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onRectangleOnScreenRequested(IBinder iBinder, Rect rect) {
        WindowState windowState;
        AccessibilityController.AccessibilityControllerInternalImpl accessibilityControllerInternal = AccessibilityController.getAccessibilityControllerInternal(this);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (accessibilityControllerInternal.hasWindowManagerEventDispatcher() && (windowState = this.mWindowMap.get(iBinder)) != null) {
                    accessibilityControllerInternal.onRectangleOnScreenRequested(windowState.getDisplayId(), rect);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public IWindowId getWindowId(IBinder iBinder) {
        WindowState.WindowId windowId;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowState = this.mWindowMap.get(iBinder);
                windowId = windowState != null ? windowState.mWindowId : null;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return windowId;
    }

    public void pokeDrawLock(Session session, IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iBinder, false);
                if (windowForClientLocked != null) {
                    windowForClientLocked.pokeDrawLockLw(this.mDrawLockTimeoutMillis);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final boolean hasStatusBarPermission(int i, int i2) {
        return this.mContext.checkPermission("android.permission.STATUS_BAR", i, i2) == 0;
    }

    public boolean cancelDraw(Session session, IWindow iWindow) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean cancelAndRedraw = windowForClientLocked.cancelAndRedraw();
                resetPriorityAfterLockedSection();
                return cancelAndRedraw;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:305:0x011c, code lost:
        continue;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00f1, code lost:
        if (r13 == null) goto L282;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00f3, code lost:
        r27 = r5;
        r16 = r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00f9, code lost:
        if (r12.length != r13.length) goto L279;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00fb, code lost:
        r5 = r12.length;
        r14 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00fd, code lost:
        if (r14 >= r5) goto L278;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00ff, code lost:
        r30 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x010d, code lost:
        if (r12[r14].getWindowType() != r13[r14].getWindowType()) goto L275;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x010f, code lost:
        r14 = r14 + 1;
        r5 = r30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x011b, code lost:
        throw new java.lang.IllegalArgumentException("Insets override types can not be changed after the window is added.");
     */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0244 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:155:0x0253  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x0256  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x025e A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0275 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:165:0x02b4  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x02c4  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x02c6  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x02cb A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:181:0x02e1 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x02ff A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x0348 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:214:0x0370 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:229:0x03c2 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:234:0x03d0  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x03d2  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x03d5 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:241:0x03e1 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:244:0x03eb A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:247:0x0401 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:252:0x040f A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:255:0x0418 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:258:0x0420 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:259:0x0424  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0429  */
    /* JADX WARN: Removed duplicated region for block: B:267:0x0437 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:270:0x0443 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:273:0x0466 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:284:0x0489 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:287:0x0499 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0178 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0186 A[Catch: all -> 0x04a4, TryCatch #3 {all -> 0x04a4, blocks: (B:7:0x002b, B:9:0x0039, B:12:0x003e, B:14:0x0042, B:20:0x004d, B:22:0x0053, B:26:0x005b, B:28:0x0069, B:30:0x0072, B:32:0x0097, B:35:0x009e, B:37:0x00a8, B:39:0x00ac, B:73:0x0133, B:78:0x014c, B:82:0x0155, B:84:0x015b, B:86:0x0161, B:88:0x0167, B:90:0x016b, B:92:0x0171, B:93:0x0174, B:95:0x0178, B:96:0x0181, B:98:0x0186, B:102:0x0192, B:103:0x0195, B:105:0x0199, B:107:0x01a3, B:108:0x01d8, B:115:0x01f3, B:117:0x01f7, B:118:0x01fb, B:120:0x020a, B:122:0x020e, B:124:0x0212, B:127:0x021a, B:134:0x0228, B:141:0x0236, B:143:0x023a, B:149:0x0244, B:153:0x024d, B:157:0x0258, B:159:0x025e, B:161:0x0262, B:162:0x0269, B:164:0x0275, B:166:0x02bc, B:170:0x02c7, B:172:0x02cb, B:174:0x02cf, B:176:0x02d6, B:181:0x02e1, B:183:0x02e7, B:185:0x02eb, B:188:0x02f3, B:189:0x02f8, B:193:0x0301, B:197:0x0308, B:199:0x0314, B:200:0x0335, B:201:0x0338, B:203:0x033e, B:205:0x0348, B:208:0x0358, B:210:0x0360, B:212:0x0364, B:213:0x0369, B:229:0x03c2, B:232:0x03cc, B:237:0x03d5, B:239:0x03db, B:241:0x03e1, B:242:0x03e7, B:244:0x03eb, B:245:0x03f0, B:247:0x0401, B:249:0x0405, B:250:0x040b, B:252:0x040f, B:253:0x0412, B:255:0x0418, B:256:0x041a, B:258:0x0420, B:264:0x042d, B:267:0x0437, B:268:0x043f, B:270:0x0443, B:271:0x045f, B:273:0x0466, B:276:0x046f, B:278:0x0475, B:280:0x047b, B:281:0x047e, B:282:0x0481, B:284:0x0489, B:287:0x0499, B:288:0x049c, B:214:0x0370, B:217:0x037f, B:219:0x0385, B:222:0x03b2, B:226:0x03bb, B:126:0x0216, B:80:0x0150, B:44:0x00b7, B:46:0x00bb, B:48:0x00bf, B:50:0x00c3, B:52:0x00d3, B:68:0x011c, B:59:0x00f3, B:61:0x00fb, B:63:0x00ff, B:65:0x010f, B:66:0x0114, B:67:0x011b, B:69:0x0123, B:70:0x012a, B:71:0x012b, B:72:0x0132, B:110:0x01de, B:111:0x01e5, B:112:0x01e6, B:113:0x01ed, B:16:0x0047, B:221:0x0394), top: B:297:0x002b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int relayoutWindow(Session session, IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, int i4, int i5, int i6, ClientWindowFrames clientWindowFrames, MergedConfiguration mergedConfiguration, SurfaceControl surfaceControl, InsetsState insetsState, InsetsSourceControl.Array array, Bundle bundle) {
        long j;
        int i7;
        int i8;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        DisplayPolicy displayPolicy;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        boolean z9;
        boolean z10;
        ActivityRecord activityRecord;
        boolean updateOrientation;
        ActivityRecord activityRecord2;
        boolean z11;
        ActivityRecord activityRecord3;
        WindowSurfaceController windowSurfaceController;
        InsetsFrameProvider[] insetsFrameProviderArr;
        int i9;
        boolean z12;
        ActivityRecord activityRecord4;
        ActivityRecord activityRecord5;
        if (array != null) {
            array.set((InsetsSourceControl[]) null);
        }
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked == null) {
                    resetPriorityAfterLockedSection();
                    return 0;
                }
                int i10 = windowForClientLocked.mRelayoutSeq;
                if (i10 < i5) {
                    windowForClientLocked.mRelayoutSeq = i5;
                } else if (i10 > i5) {
                    resetPriorityAfterLockedSection();
                    return 0;
                }
                int i11 = (!windowForClientLocked.cancelAndRedraw() || windowForClientLocked.mPrepareSyncSeqId > i6) ? 0 : 16;
                DisplayContent displayContent = windowForClientLocked.getDisplayContent();
                DisplayPolicy displayPolicy2 = displayContent.getDisplayPolicy();
                WindowStateAnimator windowStateAnimator = windowForClientLocked.mWinAnimator;
                if (i3 != 8) {
                    windowForClientLocked.setRequestedSize(i, i2);
                }
                if (layoutParams != null) {
                    displayPolicy2.adjustWindowParamsLw(windowForClientLocked, layoutParams);
                    layoutParams.flags = sanitizeFlagSlippery(layoutParams.flags, windowForClientLocked.getName(), callingUid, callingPid);
                    layoutParams.inputFeatures = sanitizeSpyWindow(layoutParams.inputFeatures, windowForClientLocked.getName(), callingUid, callingPid);
                    int i12 = (layoutParams.systemUiVisibility | layoutParams.subtreeSystemUiVisibility) & 134152192;
                    if (i12 != 0 && !hasStatusBarPermission(callingPid, callingUid)) {
                        i12 = 0;
                    }
                    windowForClientLocked.mDisableFlags = i12;
                    WindowManager.LayoutParams layoutParams2 = windowForClientLocked.mAttrs;
                    if (layoutParams2.type != layoutParams.type) {
                        throw new IllegalArgumentException("Window type can not be changed after the window is added.");
                    }
                    InsetsFrameProvider[] insetsFrameProviderArr2 = layoutParams2.providedInsets;
                    if (insetsFrameProviderArr2 == null) {
                        if (layoutParams.providedInsets != null) {
                        }
                        j = clearCallingIdentity;
                        WindowManager.LayoutParams layoutParams3 = windowForClientLocked.mAttrs;
                        i8 = layoutParams3.flags ^ layoutParams.flags;
                        i9 = layoutParams3.privateFlags ^ layoutParams.privateFlags;
                        i7 = layoutParams3.copyFrom(layoutParams);
                        z12 = (i7 & 1) == 0;
                        if (!z12 || (i7 & 16384) != 0) {
                            windowForClientLocked.mLayoutNeeded = true;
                        }
                        z = (z12 || !windowForClientLocked.providesNonDecorInsets()) ? false : displayPolicy2.updateDecorInsetsInfo();
                        activityRecord4 = windowForClientLocked.mActivityRecord;
                        if (activityRecord4 != null && ((i8 & 524288) != 0 || (i8 & 4194304) != 0)) {
                            activityRecord4.checkKeyguardFlagsChanged();
                        }
                        if ((i9 & 524288) != 0) {
                            updateNonSystemOverlayWindowsVisibilityIfNeeded(windowForClientLocked, windowForClientLocked.mWinAnimator.getShown());
                        }
                        if ((131072 & i7) != 0) {
                            windowStateAnimator.setColorSpaceAgnosticLocked((windowForClientLocked.mAttrs.privateFlags & 16777216) != 0);
                        }
                        activityRecord5 = windowForClientLocked.mActivityRecord;
                        if (activityRecord5 != null && !displayContent.mDwpcHelper.keepActivityOnWindowFlagsChanged(activityRecord5.info, i8, i9)) {
                            HandlerC1915H handlerC1915H = this.f1164mH;
                            handlerC1915H.sendMessage(handlerC1915H.obtainMessage(65, windowForClientLocked.mActivityRecord.getTask()));
                            Slog.w(StartingSurfaceController.TAG, "Activity " + windowForClientLocked.mActivityRecord + " window flag changed, can't remain on display " + displayContent.getDisplayId());
                            resetPriorityAfterLockedSection();
                            return 0;
                        }
                    }
                    if (insetsFrameProviderArr2 == null || (insetsFrameProviderArr = layoutParams.providedInsets) == null || insetsFrameProviderArr2.length != insetsFrameProviderArr.length) {
                        throw new IllegalArgumentException("Insets amount can not be changed after the window is added.");
                    }
                    int length = insetsFrameProviderArr.length;
                    int i13 = 0;
                    while (i13 < length) {
                        if (!windowForClientLocked.mAttrs.providedInsets[i13].idEquals(layoutParams.providedInsets[i13])) {
                            throw new IllegalArgumentException("Insets ID can not be changed after the window is added.");
                        }
                        InsetsFrameProvider.InsetsSizeOverride[] insetsSizeOverrides = windowForClientLocked.mAttrs.providedInsets[i13].getInsetsSizeOverrides();
                        InsetsFrameProvider.InsetsSizeOverride[] insetsSizeOverrides2 = layoutParams.providedInsets[i13].getInsetsSizeOverrides();
                        if (insetsSizeOverrides == null && insetsSizeOverrides2 == null) {
                            int i14 = length;
                            long j2 = clearCallingIdentity;
                            i13++;
                            length = i14;
                            clearCallingIdentity = j2;
                        }
                        throw new IllegalArgumentException("Insets override types can not be changed after the window is added.");
                    }
                    j = clearCallingIdentity;
                    WindowManager.LayoutParams layoutParams32 = windowForClientLocked.mAttrs;
                    i8 = layoutParams32.flags ^ layoutParams.flags;
                    i9 = layoutParams32.privateFlags ^ layoutParams.privateFlags;
                    i7 = layoutParams32.copyFrom(layoutParams);
                    if ((i7 & 1) == 0) {
                    }
                    if (!z12) {
                    }
                    windowForClientLocked.mLayoutNeeded = true;
                    if (z12) {
                    }
                    activityRecord4 = windowForClientLocked.mActivityRecord;
                    if (activityRecord4 != null) {
                        activityRecord4.checkKeyguardFlagsChanged();
                    }
                    if ((i9 & 524288) != 0) {
                    }
                    if ((131072 & i7) != 0) {
                    }
                    activityRecord5 = windowForClientLocked.mActivityRecord;
                    if (activityRecord5 != null) {
                        HandlerC1915H handlerC1915H2 = this.f1164mH;
                        handlerC1915H2.sendMessage(handlerC1915H2.obtainMessage(65, windowForClientLocked.mActivityRecord.getTask()));
                        Slog.w(StartingSurfaceController.TAG, "Activity " + windowForClientLocked.mActivityRecord + " window flag changed, can't remain on display " + displayContent.getDisplayId());
                        resetPriorityAfterLockedSection();
                        return 0;
                    }
                } else {
                    j = clearCallingIdentity;
                    i7 = 0;
                    i8 = 0;
                    z = false;
                }
                if ((i7 & 128) != 0) {
                    windowStateAnimator.mAlpha = layoutParams.alpha;
                }
                windowForClientLocked.setWindowScale(windowForClientLocked.mRequestedWidth, windowForClientLocked.mRequestedHeight);
                Rect rect = windowForClientLocked.mAttrs.surfaceInsets;
                if (rect.left != 0 || rect.top != 0 || rect.right != 0 || rect.bottom != 0) {
                    windowStateAnimator.setOpaqueLocked(false);
                }
                int i15 = windowForClientLocked.mViewVisibility;
                boolean z13 = (i15 == 4 || i15 == 8) && i3 == 0;
                if ((131080 & i8) == 0 && !z13) {
                    z2 = false;
                    if (i15 == i3 && (i8 & 8) == 0 && windowForClientLocked.mRelayoutCalled) {
                        z3 = false;
                        z4 = (i15 == i3 && windowForClientLocked.hasWallpaper()) | ((i8 & 1048576) == 0);
                        if ((i8 & IInstalld.FLAG_FORCE) != 0 && (windowSurfaceController = windowStateAnimator.mSurfaceController) != null) {
                            windowSurfaceController.setSecure(windowForClientLocked.isSecureLocked());
                        }
                        windowForClientLocked.mRelayoutCalled = true;
                        windowForClientLocked.mInRelayout = true;
                        windowForClientLocked.setViewVisibility(i3);
                        if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                            displayPolicy = displayPolicy2;
                            z5 = z2;
                            z6 = z;
                            z7 = z3;
                        } else {
                            z6 = z;
                            z5 = z2;
                            displayPolicy = displayPolicy2;
                            z7 = z3;
                            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_SCREEN_ON, -754503024, 20, (String) null, new Object[]{String.valueOf(windowForClientLocked), Long.valueOf(i15), Long.valueOf(i3), String.valueOf(new RuntimeException().fillInStackTrace())});
                        }
                        windowForClientLocked.setDisplayLayoutNeeded();
                        windowForClientLocked.mGivenInsetsPending = (i4 & 1) == 0;
                        z8 = i3 != 0 && ((activityRecord3 = windowForClientLocked.mActivityRecord) == null || windowForClientLocked.mAttrs.type == 3 || activityRecord3.isClientVisible());
                        if (!z8 && windowStateAnimator.hasSurface() && !windowForClientLocked.mAnimatingExit) {
                            i11 |= 2;
                            if (!windowForClientLocked.mWillReplaceWindow) {
                                if (z4) {
                                    displayContent.mWallpaperController.adjustWallpaperWindows();
                                }
                                tryStartExitingAnimation(windowForClientLocked, windowStateAnimator);
                            }
                        }
                        if (z8 && surfaceControl != null) {
                            try {
                                i11 = createSurfaceControl(surfaceControl, i11, windowForClientLocked, windowStateAnimator);
                            } catch (Exception e) {
                                displayContent.getInputMonitor().updateInputWindowsLw(true);
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1750206390, 0, "Exception thrown when creating surface for client %s (%s). %s", new Object[]{String.valueOf(iWindow), String.valueOf(windowForClientLocked.mAttrs.getTitle()), String.valueOf(e)});
                                }
                                Binder.restoreCallingIdentity(j);
                                resetPriorityAfterLockedSection();
                                return 0;
                            }
                        }
                        this.mWindowPlacerLocked.performSurfacePlacement(true);
                        if (!z8) {
                            Trace.traceBegin(32L, "relayoutWindow: viewVisibility_1");
                            i11 = windowForClientLocked.relayoutVisibleWindow(i11);
                            if ((i11 & 1) != 0) {
                                z7 = true;
                            }
                            if (windowForClientLocked.mAttrs.type == 2011 && displayContent.mInputMethodWindow == null) {
                                displayContent.setInputMethodWindowLocked(windowForClientLocked);
                                z5 = true;
                            }
                            windowForClientLocked.adjustStartingWindowFlags();
                            Trace.traceEnd(32L);
                        } else {
                            Trace.traceBegin(32L, "relayoutWindow: viewVisibility_2");
                            windowStateAnimator.mEnterAnimationPending = false;
                            windowStateAnimator.mEnteringAnimation = false;
                            if (surfaceControl != null) {
                                if (i3 == 0 && windowStateAnimator.hasSurface()) {
                                    Trace.traceBegin(32L, "relayoutWindow: getSurface");
                                    windowStateAnimator.mSurfaceController.getSurfaceControl(surfaceControl);
                                    Trace.traceEnd(32L);
                                } else {
                                    Trace.traceBegin(32L, "wmReleaseOutSurface_" + ((Object) windowForClientLocked.mAttrs.getTitle()));
                                    surfaceControl.release();
                                    Trace.traceEnd(32L);
                                }
                            }
                            Trace.traceEnd(32L);
                        }
                        z9 = z7;
                        if (z9 && updateFocusedWindowLocked(0, true)) {
                            z5 = false;
                        }
                        z10 = (i11 & 1) == 0;
                        if (z5) {
                            displayContent.computeImeTarget(true);
                            if (z10) {
                                displayContent.assignWindowLayers(false);
                            }
                        }
                        if (z4) {
                            displayContent.pendingLayoutChanges |= 4;
                        }
                        activityRecord = windowForClientLocked.mActivityRecord;
                        if (activityRecord != null) {
                            displayContent.mUnknownAppVisibilityController.notifyRelayouted(activityRecord);
                        }
                        Trace.traceBegin(32L, "relayoutWindow: updateOrientation");
                        updateOrientation = z6 | displayContent.updateOrientation();
                        Trace.traceEnd(32L);
                        if (z10 && windowForClientLocked.mIsWallpaper) {
                            displayContent.mWallpaperController.updateWallpaperOffset(windowForClientLocked, false);
                        }
                        activityRecord2 = windowForClientLocked.mActivityRecord;
                        if (activityRecord2 != null) {
                            activityRecord2.updateReportedVisibilityLocked();
                        }
                        if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                            i11 |= 8;
                        }
                        if (windowForClientLocked.isGoneForLayout()) {
                            z11 = false;
                            windowForClientLocked.mResizedWhileGone = false;
                        } else {
                            z11 = false;
                        }
                        if (clientWindowFrames != null && mergedConfiguration != null) {
                            windowForClientLocked.fillClientWindowFramesAndConfiguration(clientWindowFrames, mergedConfiguration, z11, z8);
                            windowForClientLocked.onResizeHandled();
                        }
                        if (insetsState != null) {
                            insetsState.set(windowForClientLocked.getCompatInsetsState(), true);
                        }
                        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS, 95902367, 12, (String) null, new Object[]{String.valueOf(windowForClientLocked), Boolean.valueOf(z9)});
                        }
                        windowForClientLocked.mInRelayout = false;
                        if (bundle != null) {
                            if (windowForClientLocked.useBLASTSync() && i3 == 0 && windowForClientLocked.mSyncSeqId > i6) {
                                r8 = windowForClientLocked.shouldSyncWithBuffers() ? windowForClientLocked.mSyncSeqId : -1;
                                windowForClientLocked.markRedrawForSyncReported();
                            }
                            bundle.putInt("seqid", r8);
                        }
                        if (updateOrientation) {
                            Trace.traceBegin(32L, "relayoutWindow: postNewConfigurationToHandler");
                            displayContent.sendNewConfiguration();
                            Trace.traceEnd(32L);
                        }
                        if (array != null) {
                            getInsetsSourceControls(windowForClientLocked, array);
                        }
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(j);
                        return i11;
                    }
                    z3 = true;
                    z4 = (i15 == i3 && windowForClientLocked.hasWallpaper()) | ((i8 & 1048576) == 0);
                    if ((i8 & IInstalld.FLAG_FORCE) != 0) {
                        windowSurfaceController.setSecure(windowForClientLocked.isSecureLocked());
                    }
                    windowForClientLocked.mRelayoutCalled = true;
                    windowForClientLocked.mInRelayout = true;
                    windowForClientLocked.setViewVisibility(i3);
                    if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                    }
                    windowForClientLocked.setDisplayLayoutNeeded();
                    windowForClientLocked.mGivenInsetsPending = (i4 & 1) == 0;
                    if (i3 != 0) {
                    }
                    if (!z8) {
                        i11 |= 2;
                        if (!windowForClientLocked.mWillReplaceWindow) {
                        }
                    }
                    if (z8) {
                        i11 = createSurfaceControl(surfaceControl, i11, windowForClientLocked, windowStateAnimator);
                    }
                    this.mWindowPlacerLocked.performSurfacePlacement(true);
                    if (!z8) {
                    }
                    z9 = z7;
                    if (z9) {
                        z5 = false;
                    }
                    if ((i11 & 1) == 0) {
                    }
                    if (z5) {
                    }
                    if (z4) {
                    }
                    activityRecord = windowForClientLocked.mActivityRecord;
                    if (activityRecord != null) {
                    }
                    Trace.traceBegin(32L, "relayoutWindow: updateOrientation");
                    updateOrientation = z6 | displayContent.updateOrientation();
                    Trace.traceEnd(32L);
                    if (z10) {
                        displayContent.mWallpaperController.updateWallpaperOffset(windowForClientLocked, false);
                    }
                    activityRecord2 = windowForClientLocked.mActivityRecord;
                    if (activityRecord2 != null) {
                    }
                    if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                    }
                    if (windowForClientLocked.isGoneForLayout()) {
                    }
                    if (clientWindowFrames != null) {
                        windowForClientLocked.fillClientWindowFramesAndConfiguration(clientWindowFrames, mergedConfiguration, z11, z8);
                        windowForClientLocked.onResizeHandled();
                    }
                    if (insetsState != null) {
                    }
                    if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                    }
                    windowForClientLocked.mInRelayout = false;
                    if (bundle != null) {
                    }
                    if (updateOrientation) {
                    }
                    if (array != null) {
                    }
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(j);
                    return i11;
                }
                z2 = true;
                if (i15 == i3) {
                    z3 = false;
                    z4 = (i15 == i3 && windowForClientLocked.hasWallpaper()) | ((i8 & 1048576) == 0);
                    if ((i8 & IInstalld.FLAG_FORCE) != 0) {
                    }
                    windowForClientLocked.mRelayoutCalled = true;
                    windowForClientLocked.mInRelayout = true;
                    windowForClientLocked.setViewVisibility(i3);
                    if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                    }
                    windowForClientLocked.setDisplayLayoutNeeded();
                    windowForClientLocked.mGivenInsetsPending = (i4 & 1) == 0;
                    if (i3 != 0) {
                    }
                    if (!z8) {
                    }
                    if (z8) {
                    }
                    this.mWindowPlacerLocked.performSurfacePlacement(true);
                    if (!z8) {
                    }
                    z9 = z7;
                    if (z9) {
                    }
                    if ((i11 & 1) == 0) {
                    }
                    if (z5) {
                    }
                    if (z4) {
                    }
                    activityRecord = windowForClientLocked.mActivityRecord;
                    if (activityRecord != null) {
                    }
                    Trace.traceBegin(32L, "relayoutWindow: updateOrientation");
                    updateOrientation = z6 | displayContent.updateOrientation();
                    Trace.traceEnd(32L);
                    if (z10) {
                    }
                    activityRecord2 = windowForClientLocked.mActivityRecord;
                    if (activityRecord2 != null) {
                    }
                    if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                    }
                    if (windowForClientLocked.isGoneForLayout()) {
                    }
                    if (clientWindowFrames != null) {
                    }
                    if (insetsState != null) {
                    }
                    if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                    }
                    windowForClientLocked.mInRelayout = false;
                    if (bundle != null) {
                    }
                    if (updateOrientation) {
                    }
                    if (array != null) {
                    }
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(j);
                    return i11;
                }
                z3 = true;
                z4 = (i15 == i3 && windowForClientLocked.hasWallpaper()) | ((i8 & 1048576) == 0);
                if ((i8 & IInstalld.FLAG_FORCE) != 0) {
                }
                windowForClientLocked.mRelayoutCalled = true;
                windowForClientLocked.mInRelayout = true;
                windowForClientLocked.setViewVisibility(i3);
                if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                }
                windowForClientLocked.setDisplayLayoutNeeded();
                windowForClientLocked.mGivenInsetsPending = (i4 & 1) == 0;
                if (i3 != 0) {
                }
                if (!z8) {
                }
                if (z8) {
                }
                this.mWindowPlacerLocked.performSurfacePlacement(true);
                if (!z8) {
                }
                z9 = z7;
                if (z9) {
                }
                if ((i11 & 1) == 0) {
                }
                if (z5) {
                }
                if (z4) {
                }
                activityRecord = windowForClientLocked.mActivityRecord;
                if (activityRecord != null) {
                }
                Trace.traceBegin(32L, "relayoutWindow: updateOrientation");
                updateOrientation = z6 | displayContent.updateOrientation();
                Trace.traceEnd(32L);
                if (z10) {
                }
                activityRecord2 = windowForClientLocked.mActivityRecord;
                if (activityRecord2 != null) {
                }
                if (displayPolicy.areSystemBarsForcedConsumedLw()) {
                }
                if (windowForClientLocked.isGoneForLayout()) {
                }
                if (clientWindowFrames != null) {
                }
                if (insetsState != null) {
                }
                if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                }
                windowForClientLocked.mInRelayout = false;
                if (bundle != null) {
                }
                if (updateOrientation) {
                }
                if (array != null) {
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(j);
                return i11;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void getInsetsSourceControls(WindowState windowState, InsetsSourceControl.Array array) {
        InsetsSourceControl[] controlsForDispatch = windowState.getDisplayContent().getInsetsStateController().getControlsForDispatch(windowState);
        if (controlsForDispatch != null) {
            int length = controlsForDispatch.length;
            InsetsSourceControl[] insetsSourceControlArr = new InsetsSourceControl[length];
            for (int i = 0; i < length; i++) {
                if (controlsForDispatch[i] != null) {
                    InsetsSourceControl insetsSourceControl = new InsetsSourceControl(controlsForDispatch[i]);
                    insetsSourceControlArr[i] = insetsSourceControl;
                    insetsSourceControl.setParcelableFlags(1);
                }
            }
            array.set(insetsSourceControlArr);
        }
    }

    public final void tryStartExitingAnimation(WindowState windowState, WindowStateAnimator windowStateAnimator) {
        String str;
        int i = windowState.mAttrs.type == 3 ? 5 : 2;
        if (windowState.isVisible() && windowState.isDisplayed() && windowState.mDisplayContent.okToAnimate()) {
            if (windowStateAnimator.applyAnimationLocked(i, false)) {
                str = "applyAnimation";
            } else if (windowState.isSelfAnimating(0, 16)) {
                str = "selfAnimating";
            } else if (windowState.mTransitionController.isShellTransitionsEnabled()) {
                ActivityRecord activityRecord = windowState.mActivityRecord;
                if (activityRecord != null && activityRecord.inTransition()) {
                    windowState.mTransitionController.mAnimatingExitWindows.add(windowState);
                    str = "inTransition";
                }
                str = null;
            } else {
                if (windowState.isAnimating(3, 9)) {
                    str = "inLegacyTransition";
                }
                str = null;
            }
            if (str != null) {
                windowState.mAnimatingExit = true;
                if (ProtoLogCache.WM_DEBUG_ANIM_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ANIM, 2075693141, 0, (String) null, new Object[]{str, String.valueOf(windowState)});
                }
            }
        }
        if (!windowState.mAnimatingExit) {
            ActivityRecord activityRecord2 = windowState.mActivityRecord;
            boolean z = activityRecord2 == null || activityRecord2.mAppStopped;
            windowState.mDestroying = true;
            windowState.destroySurface(false, z);
        }
        if (this.mAccessibilityController.hasCallbacks()) {
            this.mAccessibilityController.onWindowTransition(windowState, i);
        }
    }

    public final int createSurfaceControl(SurfaceControl surfaceControl, int i, WindowState windowState, WindowStateAnimator windowStateAnimator) {
        if (!windowState.mHasSurface) {
            i |= 2;
        }
        try {
            Trace.traceBegin(32L, "createSurfaceControl");
            WindowSurfaceController createSurfaceLocked = windowStateAnimator.createSurfaceLocked();
            Trace.traceEnd(32L);
            if (createSurfaceLocked != null) {
                createSurfaceLocked.getSurfaceControl(surfaceControl);
                if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -1257821162, 0, (String) null, new Object[]{String.valueOf(surfaceControl)});
                }
            } else {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 704998117, 0, "Failed to create surface control for %s", new Object[]{String.valueOf(windowState)});
                }
                surfaceControl.release();
            }
            return i;
        } catch (Throwable th) {
            Trace.traceEnd(32L);
            throw th;
        }
    }

    public boolean outOfMemoryWindow(Session session, IWindow iWindow) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked != null) {
                    boolean reclaimSomeSurfaceMemory = this.mRoot.reclaimSomeSurfaceMemory(windowForClientLocked.mWinAnimator, "from-client", false);
                    resetPriorityAfterLockedSection();
                    return reclaimSomeSurfaceMemory;
                }
                resetPriorityAfterLockedSection();
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void finishDrawingWindow(Session session, IWindow iWindow, SurfaceControl.Transaction transaction, int i) {
        if (transaction != null) {
            transaction.sanitize();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 1112047265, 0, (String) null, new Object[]{String.valueOf(windowForClientLocked), String.valueOf(windowForClientLocked != null ? windowForClientLocked.mWinAnimator.drawStateToString() : "null")});
                }
                if (windowForClientLocked != null && windowForClientLocked.finishDrawing(transaction, i)) {
                    if (windowForClientLocked.hasWallpaper()) {
                        windowForClientLocked.getDisplayContent().pendingLayoutChanges |= 4;
                    }
                    windowForClientLocked.setDisplayLayoutNeeded();
                    this.mWindowPlacerLocked.requestTraversal();
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean checkCallingPermission(String str, String str2) {
        return checkCallingPermission(str, str2, true);
    }

    public boolean checkCallingPermission(String str, String str2, boolean z) {
        if (Binder.getCallingPid() == MY_PID || this.mContext.checkCallingPermission(str) == 0) {
            return true;
        }
        if (z && ProtoLogCache.WM_ERROR_enabled) {
            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1563755163, 20, "Permission Denial: %s from pid=%d, uid=%d requires %s", new Object[]{String.valueOf(str2), Long.valueOf(Binder.getCallingPid()), Long.valueOf(Binder.getCallingUid()), String.valueOf(str)});
            return false;
        }
        return false;
    }

    public void addWindowToken(IBinder iBinder, int i, int i2, Bundle bundle) {
        if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "addWindowToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i2, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1208313423, 4, "addWindowToken: Attempted to add token: %s for non-exiting displayId=%d", new Object[]{String.valueOf(iBinder), Long.valueOf(i2)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                WindowToken windowToken = displayContentOrCreate.getWindowToken(iBinder);
                if (windowToken == null) {
                    if (i == 2013) {
                        new WallpaperWindowToken(this, iBinder, true, displayContentOrCreate, true, bundle);
                    } else {
                        new WindowToken.Builder(this, iBinder, i).setDisplayContent(displayContentOrCreate).setPersistOnEmpty(true).setOwnerCanManageAppTokens(true).setOptions(bundle).build();
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 254883724, 16, "addWindowToken: Attempted to add binder token: %s for already created window token: %s displayId=%d", new Object[]{String.valueOf(iBinder), String.valueOf(windowToken), Long.valueOf(i2)});
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public Configuration attachWindowContextToDisplayArea(IBinder iBinder, int i, int i2, Bundle bundle) {
        if (iBinder == null) {
            throw new IllegalArgumentException("clientToken must not be null!");
        }
        boolean checkCallingPermission = checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "attachWindowContextToDisplayArea", false);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = this.mRoot.getDisplayContentOrCreate(i2);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 666937535, 1, "attachWindowContextToDisplayArea: trying to attach to a non-existing display:%d", new Object[]{Long.valueOf(i2)});
                    }
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return null;
                }
                DisplayArea findAreaForWindowType = displayContentOrCreate.findAreaForWindowType(i, bundle, checkCallingPermission, false);
                this.mWindowContextListenerController.registerWindowContainerListener(iBinder, findAreaForWindowType, callingUid, i, bundle, false);
                Configuration configuration = findAreaForWindowType.getConfiguration();
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return configuration;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void attachWindowContextToWindowToken(IBinder iBinder, IBinder iBinder2) {
        boolean checkCallingPermission = checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "attachWindowContextToWindowToken", false);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowToken windowToken = this.mRoot.getWindowToken(iBinder2);
                if (windowToken == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1789321832, 0, "Then token:%s is invalid. It might be removed", new Object[]{String.valueOf(iBinder2)});
                    }
                } else {
                    int windowType = this.mWindowContextListenerController.getWindowType(iBinder);
                    if (windowType == -1) {
                        throw new IllegalArgumentException("The clientToken:" + iBinder + " should have been attached.");
                    } else if (windowType != windowToken.windowType) {
                        throw new IllegalArgumentException("The WindowToken's type should match the created WindowContext's type. WindowToken's type is " + windowToken.windowType + ", while WindowContext's is " + windowType);
                    } else if (this.mWindowContextListenerController.assertCallerCanModifyListener(iBinder, checkCallingPermission, callingUid)) {
                        this.mWindowContextListenerController.registerWindowContainerListener(iBinder, windowToken, callingUid, windowToken.windowType, windowToken.mOptions);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void detachWindowContextFromWindowContainer(IBinder iBinder) {
        boolean checkCallingPermission = checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "detachWindowContextFromWindowContainer", false);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                if (this.mWindowContextListenerController.assertCallerCanModifyListener(iBinder, checkCallingPermission, callingUid)) {
                    WindowContainer<?> container = this.mWindowContextListenerController.getContainer(iBinder);
                    this.mWindowContextListenerController.unregisterWindowContainerListener(iBinder);
                    WindowToken asWindowToken = container.asWindowToken();
                    if (asWindowToken != null && asWindowToken.isFromClient()) {
                        removeWindowToken(asWindowToken.token, asWindowToken.getDisplayContent().getDisplayId());
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Finally extract failed */
    public Configuration attachToDisplayContent(IBinder iBinder, int i) {
        if (iBinder == null) {
            throw new IllegalArgumentException("clientToken must not be null!");
        }
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (Binder.getCallingPid() != MY_PID) {
                        throw new WindowManager.InvalidDisplayException("attachToDisplayContent: trying to attach to a non-existing display:" + i);
                    }
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return null;
                }
                this.mWindowContextListenerController.registerWindowContainerListener(iBinder, displayContent, callingUid, -1, null, false);
                Configuration configuration = displayContent.getConfiguration();
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return configuration;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean isWindowToken(IBinder iBinder) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                z = this.mRoot.getWindowToken(iBinder) != null;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return z;
    }

    public void removeWindowToken(IBinder iBinder, boolean z, boolean z2, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1739298851, 4, "removeWindowToken: Attempted to remove token: %s for non-exiting displayId=%d", new Object[]{String.valueOf(iBinder), Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                WindowToken removeWindowToken = displayContent.removeWindowToken(iBinder, z2);
                if (removeWindowToken == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1518495446, 0, "removeWindowToken: Attempted to remove non-existing token: %s", new Object[]{String.valueOf(iBinder)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                if (z) {
                    removeWindowToken.removeAllWindowsIfPossible();
                }
                displayContent.getInputMonitor().updateInputWindowsLw(true);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void removeWindowToken(IBinder iBinder, int i) {
        if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "removeWindowToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeWindowToken(iBinder, false, true, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void moveWindowTokenToDisplay(IBinder iBinder, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = this.mRoot.getDisplayContentOrCreate(i);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 2060978050, 4, "moveWindowTokenToDisplay: Attempted to move token: %s to non-exiting displayId=%d", new Object[]{String.valueOf(iBinder), Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                WindowToken windowToken = this.mRoot.getWindowToken(iBinder);
                if (windowToken == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1033274509, 0, "moveWindowTokenToDisplay: Attempted to move non-existing token: %s", new Object[]{String.valueOf(iBinder)});
                    }
                    resetPriorityAfterLockedSection();
                } else if (windowToken.getDisplayContent() == displayContentOrCreate) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 781471998, 0, "moveWindowTokenToDisplay: Cannot move to the original display for token: %s", new Object[]{String.valueOf(iBinder)});
                    }
                    resetPriorityAfterLockedSection();
                } else {
                    displayContentOrCreate.reParentWindowToken(windowToken);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void prepareAppTransitionNone() {
        if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "prepareAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        getDefaultDisplayContentLocked().prepareAppTransition(0);
    }

    public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture iAppTransitionAnimationSpecsFuture, IRemoteCallback iRemoteCallback, boolean z, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempted to call overridePendingAppTransitionMultiThumbFuture for the display " + i + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overridePendingAppTransitionMultiThumbFuture(iAppTransitionAnimationSpecsFuture, iRemoteCallback, z);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, int i) {
        if (!checkCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "overridePendingAppTransitionRemote()")) {
            throw new SecurityException("Requires CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempted to call overridePendingAppTransitionRemote for the display " + i + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                remoteAnimationAdapter.setCallingPidUid(Binder.getCallingPid(), Binder.getCallingUid());
                displayContent.mAppTransition.overridePendingAppTransitionRemote(remoteAnimationAdapter);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void executeAppTransition() {
        if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "executeAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        getDefaultDisplayContentLocked().executeAppTransition();
    }

    public void initializeRecentsAnimation(int i, IRecentsAnimationRunner iRecentsAnimationRunner, RecentsAnimationController.RecentsAnimationCallbacks recentsAnimationCallbacks, int i2, SparseBooleanArray sparseBooleanArray, ActivityRecord activityRecord) {
        this.mRecentsAnimationController = new RecentsAnimationController(this, iRecentsAnimationRunner, recentsAnimationCallbacks, i2);
        this.mRoot.getDisplayContent(i2).mAppTransition.updateBooster();
        this.mRecentsAnimationController.initialize(i, sparseBooleanArray, activityRecord);
    }

    @VisibleForTesting
    public void setRecentsAnimationController(RecentsAnimationController recentsAnimationController) {
        this.mRecentsAnimationController = recentsAnimationController;
    }

    public RecentsAnimationController getRecentsAnimationController() {
        return this.mRecentsAnimationController;
    }

    public void cancelRecentsAnimation(int i, String str) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.cancelAnimation(i, str);
        }
    }

    public void cleanupRecentsAnimation(int i) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            this.mRecentsAnimationController = null;
            recentsAnimationController.cleanupAnimation(i);
            DisplayContent defaultDisplayContentLocked = getDefaultDisplayContentLocked();
            if (defaultDisplayContentLocked.mAppTransition.isTransitionSet()) {
                defaultDisplayContentLocked.mSkipAppTransitionAnimation = true;
            }
            defaultDisplayContentLocked.forAllWindowContainers(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda10
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WindowManagerService.lambda$cleanupRecentsAnimation$2((WindowContainer) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$cleanupRecentsAnimation$2(WindowContainer windowContainer) {
        if (windowContainer.isAnimating(1, 1)) {
            windowContainer.cancelAnimation();
        }
    }

    public boolean isRecentsAnimationTarget(ActivityRecord activityRecord) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        return recentsAnimationController != null && recentsAnimationController.isTargetApp(activityRecord);
    }

    public void setWindowOpaqueLocked(IBinder iBinder, boolean z) {
        ActivityRecord activityRecord = this.mRoot.getActivityRecord(iBinder);
        if (activityRecord != null) {
            activityRecord.setMainWindowOpaque(z);
        }
    }

    public boolean isValidPictureInPictureAspectRatio(DisplayContent displayContent, float f) {
        return displayContent.getPinnedTaskController().isValidPictureInPictureAspectRatio(f);
    }

    public boolean isValidExpandedPictureInPictureAspectRatio(DisplayContent displayContent, float f) {
        return displayContent.getPinnedTaskController().isValidExpandedPictureInPictureAspectRatio(f);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void notifyKeyguardTrustedChanged() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mAtmService.mKeyguardController.isKeyguardShowing(0)) {
                    this.mRoot.ensureActivitiesVisible(null, 0, false);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void screenTurningOff(int i, WindowManagerPolicy.ScreenOffListener screenOffListener) {
        this.mTaskSnapshotController.screenTurningOff(i, screenOffListener);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void triggerAnimationFailsafe() {
        this.f1164mH.sendEmptyMessage(60);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void onKeyguardShowingAndNotOccludedChanged() {
        this.f1164mH.sendEmptyMessage(61);
        dispatchKeyguardLockedState();
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void onPowerKeyDown(final boolean z) {
        this.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda22
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DisplayPolicy) obj).onPowerKeyDown(z);
            }
        });
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void onUserSwitched() {
        this.mSettingsObserver.updateSystemUiSettings(true);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((DisplayPolicy) obj).resetSystemBarAttributes();
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void moveDisplayToTopIfAllowed(int i) {
        moveDisplayToTopInternal(i);
        syncInputTransactions(true);
    }

    public void moveDisplayToTopInternal(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null && this.mRoot.getTopChild() != displayContent) {
                    if (!displayContent.canStealTopFocus()) {
                        if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, 34682671, 5, (String) null, new Object[]{Long.valueOf(i), Long.valueOf(this.mRoot.getTopFocusedDisplayContent().getDisplayId())});
                        }
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    displayContent.getParent().positionChildAt(Integer.MAX_VALUE, displayContent, true);
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public boolean isAppTransitionStateIdle() {
        return getDefaultDisplayContentLocked().mAppTransition.isIdle();
    }

    public void startFreezingScreen(int i, int i2) {
        if (!checkCallingPermission("android.permission.FREEZE_SCREEN", "startFreezingScreen()")) {
            throw new SecurityException("Requires FREEZE_SCREEN permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (!this.mClientFreezingScreen) {
                    this.mClientFreezingScreen = true;
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    startFreezingDisplay(i, i2);
                    this.f1164mH.removeMessages(30);
                    this.f1164mH.sendEmptyMessageDelayed(30, 5000L);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void stopFreezingScreen() {
        if (!checkCallingPermission("android.permission.FREEZE_SCREEN", "stopFreezingScreen()")) {
            throw new SecurityException("Requires FREEZE_SCREEN permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mClientFreezingScreen) {
                    this.mClientFreezingScreen = false;
                    this.mLastFinishedFreezeSource = "client";
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    stopFreezingDisplayLocked();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void disableKeyguard(IBinder iBinder, String str, int i) {
        int handleIncomingUser = this.mAmInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 2, "disableKeyguard", (String) null);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mKeyguardDisableHandler.disableKeyguard(iBinder, str, callingUid, handleIncomingUser);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void reenableKeyguard(IBinder iBinder, int i) {
        int handleIncomingUser = this.mAmInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 2, "reenableKeyguard", (String) null);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        Objects.requireNonNull(iBinder, "token is null");
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mKeyguardDisableHandler.reenableKeyguard(iBinder, callingUid, handleIncomingUser);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void exitKeyguardSecurely(final IOnKeyguardExitResult iOnKeyguardExitResult) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        if (iOnKeyguardExitResult == null) {
            throw new IllegalArgumentException("callback == null");
        }
        this.mPolicy.exitKeyguardSecurely(new WindowManagerPolicy.OnKeyguardExitResult() { // from class: com.android.server.wm.WindowManagerService.9
            @Override // com.android.server.policy.WindowManagerPolicy.OnKeyguardExitResult
            public void onKeyguardExitResult(boolean z) {
                try {
                    iOnKeyguardExitResult.onKeyguardExitResult(z);
                } catch (RemoteException unused) {
                }
            }
        });
    }

    public boolean isKeyguardLocked() {
        return this.mPolicy.isKeyguardLocked();
    }

    public boolean isKeyguardShowingAndNotOccluded() {
        return this.mPolicy.isKeyguardShowingAndNotOccluded();
    }

    public boolean isKeyguardSecure(int i) {
        if (i != UserHandle.getCallingUserId() && !checkCallingPermission("android.permission.INTERACT_ACROSS_USERS", "isKeyguardSecure")) {
            throw new SecurityException("Requires INTERACT_ACROSS_USERS permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mPolicy.isKeyguardSecure(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dismissKeyguard(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        if (!checkCallingPermission("android.permission.CONTROL_KEYGUARD", "dismissKeyguard")) {
            throw new SecurityException("Requires CONTROL_KEYGUARD permission");
        }
        if (this.mAtmService.mKeyguardController.isShowingDream()) {
            this.mAtmService.mTaskSupervisor.wakeUp("leaveDream");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mPolicy.dismissKeyguardLw(iKeyguardDismissCallback, charSequence);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @RequiresPermission("android.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE")
    public void addKeyguardLockedStateListener(IKeyguardLockedStateListener iKeyguardLockedStateListener) {
        enforceSubscribeToKeyguardLockedStatePermission();
        if (this.mKeyguardLockedStateListeners.register(iKeyguardLockedStateListener)) {
            return;
        }
        Slog.w(StartingSurfaceController.TAG, "Failed to register listener: " + iKeyguardLockedStateListener);
    }

    @RequiresPermission("android.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE")
    public void removeKeyguardLockedStateListener(IKeyguardLockedStateListener iKeyguardLockedStateListener) {
        enforceSubscribeToKeyguardLockedStatePermission();
        this.mKeyguardLockedStateListeners.unregister(iKeyguardLockedStateListener);
    }

    public final void enforceSubscribeToKeyguardLockedStatePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE", "android.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE permission required to subscribe to keyguard locked state changes");
    }

    public final void dispatchKeyguardLockedState() {
        this.f1164mH.post(new Runnable() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerService.this.lambda$dispatchKeyguardLockedState$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchKeyguardLockedState$4() {
        boolean isKeyguardShowing = this.mPolicy.isKeyguardShowing();
        if (this.mDispatchedKeyguardLockedState == isKeyguardShowing) {
            return;
        }
        int beginBroadcast = this.mKeyguardLockedStateListeners.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mKeyguardLockedStateListeners.getBroadcastItem(i).onKeyguardLockedStateChanged(isKeyguardShowing);
            } catch (RemoteException unused) {
            }
        }
        this.mKeyguardLockedStateListeners.finishBroadcast();
        this.mDispatchedKeyguardLockedState = isKeyguardShowing;
    }

    public void setSwitchingUser(boolean z) {
        if (!checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "setSwitchingUser()")) {
            throw new SecurityException("Requires INTERACT_ACROSS_USERS_FULL permission");
        }
        this.mPolicy.setSwitchingUser(z);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mSwitchingUser = z;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @RequiresPermission("android.permission.INTERNAL_SYSTEM_WINDOW")
    public void showGlobalActions() {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "showGlobalActions()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        this.mPolicy.showGlobalActions();
    }

    public void closeSystemDialogs(String str) {
        if (this.mAtmService.checkCanCloseSystemDialogs(Binder.getCallingPid(), Binder.getCallingUid(), null)) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.closeSystemDialogs(str);
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public void setAnimationScale(int i, float f) {
        if (!checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
            throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
        }
        float fixScale = WindowManager.fixScale(f);
        if (i == 0) {
            this.mWindowAnimationScaleSetting = fixScale;
        } else if (i == 1) {
            this.mTransitionAnimationScaleSetting = fixScale;
        } else if (i == 2) {
            this.mAnimatorDurationScaleSetting = fixScale;
        }
        this.f1164mH.sendEmptyMessage(14);
    }

    public void setAnimationScales(float[] fArr) {
        if (!checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
            throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
        }
        if (fArr != null) {
            if (fArr.length >= 1) {
                this.mWindowAnimationScaleSetting = WindowManager.fixScale(fArr[0]);
            }
            if (fArr.length >= 2) {
                this.mTransitionAnimationScaleSetting = WindowManager.fixScale(fArr[1]);
            }
            if (fArr.length >= 3) {
                this.mAnimatorDurationScaleSetting = WindowManager.fixScale(fArr[2]);
                dispatchNewAnimatorScaleLocked(null);
            }
        }
        this.f1164mH.sendEmptyMessage(14);
    }

    public final void setAnimatorDurationScale(float f) {
        this.mAnimatorDurationScaleSetting = f;
        ValueAnimator.setDurationScale(f);
    }

    public float getWindowAnimationScaleLocked() {
        if (this.mAnimationsDisabled) {
            return 0.0f;
        }
        return this.mWindowAnimationScaleSetting;
    }

    public float getTransitionAnimationScaleLocked() {
        if (this.mAnimationsDisabled) {
            return 0.0f;
        }
        return this.mTransitionAnimationScaleSetting;
    }

    public float getAnimationScale(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return 0.0f;
                }
                return this.mAnimatorDurationScaleSetting;
            }
            return this.mTransitionAnimationScaleSetting;
        }
        return this.mWindowAnimationScaleSetting;
    }

    public float[] getAnimationScales() {
        return new float[]{this.mWindowAnimationScaleSetting, this.mTransitionAnimationScaleSetting, this.mAnimatorDurationScaleSetting};
    }

    public float getCurrentAnimatorScale() {
        float f;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                f = this.mAnimationsDisabled ? 0.0f : this.mAnimatorDurationScaleSetting;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return f;
    }

    public void dispatchNewAnimatorScaleLocked(Session session) {
        this.f1164mH.obtainMessage(34, session).sendToTarget();
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public int getLidState() {
        int switchState = this.mInputManager.getSwitchState(-1, -256, 0);
        if (switchState > 0) {
            return 0;
        }
        return switchState == 0 ? 1 : -1;
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void lockDeviceNow() {
        lockNow(null);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public int getCameraLensCoverState() {
        int switchState = this.mInputManager.getSwitchState(-1, -256, 9);
        if (switchState > 0) {
            return 1;
        }
        return switchState == 0 ? 0 : -1;
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void switchKeyboardLayout(int i, int i2) {
        this.mInputManager.switchKeyboardLayout(i, i2);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void shutdown(boolean z) {
        ShutdownThread.shutdown(ActivityThread.currentActivityThread().getSystemUiContext(), "userrequested", z);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void reboot(boolean z) {
        ShutdownThread.reboot(ActivityThread.currentActivityThread().getSystemUiContext(), "userrequested", z);
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void rebootSafeMode(boolean z) {
        ShutdownThread.rebootSafeMode(ActivityThread.currentActivityThread().getSystemUiContext(), z);
    }

    public void setCurrentUser(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mCurrentUserId = i;
                this.mPolicy.setCurrentUserLw(i);
                this.mKeyguardDisableHandler.setCurrentUser(i);
                this.mRoot.switchUser(i);
                this.mWindowPlacerLocked.performSurfacePlacement();
                DisplayContent defaultDisplayContentLocked = getDefaultDisplayContentLocked();
                if (this.mDisplayReady) {
                    int forcedDisplayDensityForUserLocked = getForcedDisplayDensityForUserLocked(i);
                    if (forcedDisplayDensityForUserLocked == 0) {
                        forcedDisplayDensityForUserLocked = defaultDisplayContentLocked.getInitialDisplayDensity();
                    }
                    defaultDisplayContentLocked.setForcedDensity(forcedDisplayDensityForUserLocked, -2);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean isUserVisible(int i) {
        return this.mUmInternal.isUserVisible(i);
    }

    public void enableScreenAfterBoot() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                    boolean z = this.mDisplayEnabled;
                    boolean z2 = this.mForceDisplayEnabled;
                    boolean z3 = this.mShowingBootMessages;
                    boolean z4 = this.mSystemBooted;
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, -1884933373, 255, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), String.valueOf(new RuntimeException("here").fillInStackTrace())});
                }
                if (this.mSystemBooted) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mSystemBooted = true;
                hideBootMessagesLocked();
                this.f1164mH.sendEmptyMessageDelayed(23, 30000L);
                resetPriorityAfterLockedSection();
                this.mPolicy.systemBooted();
                performEnableScreen();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void enableScreenIfNeeded() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                enableScreenIfNeededLocked();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void enableScreenIfNeededLocked() {
        if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
            boolean z = this.mDisplayEnabled;
            boolean z2 = this.mForceDisplayEnabled;
            boolean z3 = this.mShowingBootMessages;
            boolean z4 = this.mSystemBooted;
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, -549028919, 255, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), String.valueOf(new RuntimeException("here").fillInStackTrace())});
        }
        if (this.mDisplayEnabled) {
            return;
        }
        if (this.mSystemBooted || this.mShowingBootMessages) {
            this.f1164mH.sendEmptyMessage(16);
        }
    }

    public void performBootTimeout() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mDisplayEnabled) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1001904964, 0, "***** BOOT TIMEOUT: forcing display enabled", (Object[]) null);
                }
                this.mForceDisplayEnabled = true;
                resetPriorityAfterLockedSection();
                performEnableScreen();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void onSystemUiStarted() {
        this.mPolicy.onSystemUiStarted();
    }

    public final void performEnableScreen() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, -1256520588, 255, (String) null, new Object[]{Boolean.valueOf(this.mDisplayEnabled), Boolean.valueOf(this.mForceDisplayEnabled), Boolean.valueOf(this.mShowingBootMessages), Boolean.valueOf(this.mSystemBooted), String.valueOf(new RuntimeException("here").fillInStackTrace())});
                }
                if (this.mDisplayEnabled) {
                    return;
                }
                if (!this.mSystemBooted && !this.mShowingBootMessages) {
                    resetPriorityAfterLockedSection();
                } else if (this.mShowingBootMessages || this.mPolicy.canDismissBootAnimation()) {
                    if (!this.mForceDisplayEnabled) {
                        if (this.mBootWaitForWindowsStartTime < 0) {
                            this.mBootWaitForWindowsStartTime = SystemClock.elapsedRealtime();
                        }
                        for (int childCount = this.mRoot.getChildCount() - 1; childCount >= 0; childCount--) {
                            if (this.mRoot.getChildAt(childCount).shouldWaitForSystemDecorWindowsOnBoot()) {
                                resetPriorityAfterLockedSection();
                                return;
                            }
                        }
                        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mBootWaitForWindowsStartTime;
                        this.mBootWaitForWindowsStartTime = -1L;
                        if (elapsedRealtime > 10 && ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, 544101314, 1, (String) null, new Object[]{Long.valueOf(elapsedRealtime)});
                        }
                    }
                    if (!this.mBootAnimationStopped) {
                        Trace.asyncTraceBegin(32L, "Stop bootanim", 0);
                        SystemProperties.set("service.bootanim.exit", "1");
                        this.mBootAnimationStopped = true;
                    }
                    if (!this.mForceDisplayEnabled && !checkBootAnimationCompleteLocked()) {
                        if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, 374972436, 0, (String) null, (Object[]) null);
                        }
                        resetPriorityAfterLockedSection();
                    } else if (!SurfaceControl.bootFinished()) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1323783276, 0, "performEnableScreen: bootFinished() failed.", (Object[]) null);
                        }
                        resetPriorityAfterLockedSection();
                    } else {
                        EventLogTags.writeWmBootAnimationDone(SystemClock.uptimeMillis());
                        Trace.asyncTraceEnd(32L, "Stop bootanim", 0);
                        this.mDisplayEnabled = true;
                        if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_SCREEN_ON, -116086365, 0, (String) null, (Object[]) null);
                        }
                        this.mInputManagerCallback.setEventDispatchingLw(this.mEventDispatchingEnabled);
                        resetPriorityAfterLockedSection();
                        try {
                            this.mActivityManager.bootAnimationComplete();
                        } catch (RemoteException unused) {
                        }
                        this.mPolicy.enableScreenAfterBoot();
                        updateRotationUnchecked(false, false);
                        synchronized (this.mGlobalLock) {
                            try {
                                boostPriorityForLockedSection();
                                this.mAtmService.getTransitionController().mIsWaitingForDisplayEnabled = false;
                                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, -2088209279, 0, (String) null, (Object[]) null);
                                }
                            } finally {
                            }
                        }
                        resetPriorityAfterLockedSection();
                    }
                } else {
                    resetPriorityAfterLockedSection();
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    public final boolean checkBootAnimationCompleteLocked() {
        if (SystemService.isRunning("bootanim")) {
            this.f1164mH.removeMessages(37);
            this.f1164mH.sendEmptyMessageDelayed(37, 50L);
            if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, 600140673, 0, (String) null, (Object[]) null);
            }
            return false;
        } else if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, 1224307091, 0, (String) null, (Object[]) null);
            return true;
        } else {
            return true;
        }
    }

    public void showBootMessage(CharSequence charSequence, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                boolean z2 = false;
                if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, -874446906, 1020, (String) null, new Object[]{String.valueOf(charSequence), Boolean.valueOf(z), Boolean.valueOf(this.mAllowBootMessages), Boolean.valueOf(this.mShowingBootMessages), Boolean.valueOf(this.mSystemBooted), String.valueOf(new RuntimeException("here").fillInStackTrace())});
                }
                if (!this.mAllowBootMessages) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                if (!this.mShowingBootMessages) {
                    if (!z) {
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    z2 = true;
                }
                if (this.mSystemBooted) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mShowingBootMessages = true;
                this.mPolicy.showBootMessage(charSequence, z);
                resetPriorityAfterLockedSection();
                if (z2) {
                    performEnableScreen();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void hideBootMessagesLocked() {
        if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
            boolean z = this.mDisplayEnabled;
            boolean z2 = this.mForceDisplayEnabled;
            boolean z3 = this.mShowingBootMessages;
            boolean z4 = this.mSystemBooted;
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, -1350198040, 255, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), String.valueOf(new RuntimeException("here").fillInStackTrace())});
        }
        if (this.mShowingBootMessages) {
            this.mShowingBootMessages = false;
            this.mPolicy.hideBootMessages();
        }
    }

    public void setInTouchMode(boolean z, int i) {
        int i2;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (this.mPerDisplayFocusEnabled && (displayContent == null || displayContent.isInTouchMode() == z)) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                boolean z2 = displayContent != null && displayContent.hasOwnFocus();
                if (z2 && displayContent.isInTouchMode() == z) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                int callingPid = Binder.getCallingPid();
                int callingUid = Binder.getCallingUid();
                boolean hasTouchModePermission = hasTouchModePermission(callingPid);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (!this.mPerDisplayFocusEnabled && !z2) {
                    int size = this.mRoot.mChildren.size();
                    int i3 = 0;
                    while (i3 < size) {
                        DisplayContent displayContent2 = (DisplayContent) this.mRoot.mChildren.get(i3);
                        if (displayContent2.isInTouchMode() != z && !displayContent2.hasOwnFocus()) {
                            i2 = size;
                            if (this.mInputManager.setInTouchMode(z, callingPid, callingUid, hasTouchModePermission, displayContent2.mDisplayId)) {
                                displayContent2.setInTouchMode(z);
                            }
                            i3++;
                            size = i2;
                        }
                        i2 = size;
                        i3++;
                        size = i2;
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    resetPriorityAfterLockedSection();
                }
                if (this.mInputManager.setInTouchMode(z, callingPid, callingUid, hasTouchModePermission, i)) {
                    displayContent.setInTouchMode(z);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setInTouchModeOnAllDisplays(boolean z) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        boolean hasTouchModePermission = hasTouchModePermission(callingPid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                for (int i = 0; i < this.mRoot.mChildren.size(); i++) {
                    DisplayContent displayContent = (DisplayContent) this.mRoot.mChildren.get(i);
                    if (displayContent.isInTouchMode() != z && this.mInputManager.setInTouchMode(z, callingPid, callingUid, hasTouchModePermission, displayContent.mDisplayId)) {
                        displayContent.setInTouchMode(z);
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean hasTouchModePermission(int i) {
        return this.mAtmService.instrumentationSourceHasPermission(i, "android.permission.MODIFY_TOUCH_MODE_STATE") || checkCallingPermission("android.permission.MODIFY_TOUCH_MODE_STATE", "setInTouchMode()", false);
    }

    public boolean isInTouchMode(int i) {
        boolean isInTouchMode;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalStateException("No touch mode is defined for displayId {" + i + "}");
                }
                isInTouchMode = displayContent.isInTouchMode();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return isInTouchMode;
    }

    public void showEmulatorDisplayOverlayIfNeeded() {
        if (this.mContext.getResources().getBoolean(17891887) && SystemProperties.getBoolean("ro.emulator.circular", false) && Build.IS_EMULATOR) {
            HandlerC1915H handlerC1915H = this.f1164mH;
            handlerC1915H.sendMessage(handlerC1915H.obtainMessage(36));
        }
    }

    public void showEmulatorDisplayOverlay() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mEmulatorDisplayOverlay == null) {
                    this.mEmulatorDisplayOverlay = new EmulatorDisplayOverlay(this.mContext, getDefaultDisplayContentLocked(), (this.mPolicy.getWindowLayerFromTypeLw(2018) * FrameworkStatsLog.WIFI_BYTES_TRANSFER) + 10, this.mTransaction);
                }
                this.mEmulatorDisplayOverlay.setVisibility(true, this.mTransaction);
                this.mTransaction.apply();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void showStrictModeViolation(boolean z) {
        int callingPid = Binder.getCallingPid();
        if (z) {
            HandlerC1915H handlerC1915H = this.f1164mH;
            handlerC1915H.sendMessage(handlerC1915H.obtainMessage(25, 1, callingPid));
            HandlerC1915H handlerC1915H2 = this.f1164mH;
            handlerC1915H2.sendMessageDelayed(handlerC1915H2.obtainMessage(25, 0, callingPid), 1000L);
            return;
        }
        HandlerC1915H handlerC1915H3 = this.f1164mH;
        handlerC1915H3.sendMessage(handlerC1915H3.obtainMessage(25, 0, callingPid));
    }

    public final void showStrictModeViolation(int i, int i2) {
        boolean z = i != 0;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (z && !this.mRoot.canShowStrictModeViolation(i2)) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                if (this.mStrictModeFlash == null) {
                    this.mStrictModeFlash = new StrictModeFlash(getDefaultDisplayContentLocked(), this.mTransaction);
                }
                this.mStrictModeFlash.setVisibility(z, this.mTransaction);
                this.mTransaction.apply();
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setStrictModeVisualIndicatorPreference(String str) {
        SystemProperties.set("persist.sys.strictmode.visual", str);
    }

    public Bitmap screenshotWallpaper() {
        Bitmap screenshotWallpaperLocked;
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "screenshotWallpaper()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        try {
            Trace.traceBegin(32L, "screenshotWallpaper");
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                screenshotWallpaperLocked = this.mRoot.getDisplayContent(0).mWallpaperController.screenshotWallpaperLocked();
            }
            resetPriorityAfterLockedSection();
            return screenshotWallpaperLocked;
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public SurfaceControl mirrorWallpaperSurface(int i) {
        SurfaceControl mirrorWallpaperSurface;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                mirrorWallpaperSurface = this.mRoot.getDisplayContent(i).mWallpaperController.mirrorWallpaperSurface();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return mirrorWallpaperSurface;
    }

    public boolean requestAssistScreenshot(final IAssistDataReceiver iAssistDataReceiver) {
        final Bitmap screenshotDisplayLocked;
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "requestAssistScreenshot()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(0);
                screenshotDisplayLocked = displayContent == null ? null : displayContent.screenshotDisplayLocked();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        FgThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerService.lambda$requestAssistScreenshot$5(iAssistDataReceiver, screenshotDisplayLocked);
            }
        });
        return true;
    }

    public static /* synthetic */ void lambda$requestAssistScreenshot$5(IAssistDataReceiver iAssistDataReceiver, Bitmap bitmap) {
        try {
            iAssistDataReceiver.onHandleAssistScreenshot(bitmap);
        } catch (RemoteException unused) {
        }
    }

    public TaskSnapshot getTaskSnapshot(int i, int i2, boolean z, boolean z2) {
        return this.mTaskSnapshotController.getSnapshot(i, i2, z2, z);
    }

    public Bitmap captureTaskBitmap(int i, ScreenCapture.LayerCaptureArgs.Builder builder) {
        if (this.mTaskSnapshotController.shouldDisableSnapshots()) {
            return null;
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                Task anyTaskForId = this.mRoot.anyTaskForId(i);
                if (anyTaskForId == null) {
                    resetPriorityAfterLockedSection();
                    return null;
                }
                anyTaskForId.getBounds(this.mTmpRect);
                this.mTmpRect.offsetTo(0, 0);
                ScreenCapture.ScreenshotHardwareBuffer captureLayers = ScreenCapture.captureLayers(builder.setLayer(anyTaskForId.getSurfaceControl()).setSourceCrop(this.mTmpRect).build());
                if (captureLayers == null) {
                    Slog.w(StartingSurfaceController.TAG, "Could not get screenshot buffer for taskId: " + i);
                    resetPriorityAfterLockedSection();
                    return null;
                }
                Bitmap asBitmap = captureLayers.asBitmap();
                resetPriorityAfterLockedSection();
                return asBitmap;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void removeObsoleteTaskFiles(ArraySet<Integer> arraySet, int[] iArr) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mTaskSnapshotController.removeObsoleteTaskFiles(arraySet, iArr);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setFixedToUserRotation(int i, int i2) {
        if (!checkCallingPermission("android.permission.SET_ORIENTATION", "setFixedToUserRotation()")) {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to set fixed to user rotation for a missing display.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.getDisplayRotation().setFixedToUserRotation(i2);
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getFixedToUserRotation(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to get fixed to user rotation for a missing display.");
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int fixedToUserRotationMode = displayContent.getDisplayRotation().getFixedToUserRotationMode();
                resetPriorityAfterLockedSection();
                return fixedToUserRotationMode;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setIgnoreOrientationRequest(int i, boolean z) {
        if (!checkCallingPermission("android.permission.SET_ORIENTATION", "setIgnoreOrientationRequest()")) {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to setIgnoreOrientationRequest() for a missing display.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.setIgnoreOrientationRequest(z);
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean getIgnoreOrientationRequest(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to getIgnoreOrientationRequest() for a missing display.");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean ignoreOrientationRequest = displayContent.getIgnoreOrientationRequest();
                resetPriorityAfterLockedSection();
                return ignoreOrientationRequest;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setIsIgnoreOrientationRequestDisabled(boolean z) {
        if (z == this.mIsIgnoreOrientationRequestDisabled) {
            return;
        }
        this.mIsIgnoreOrientationRequestDisabled = z;
        for (int childCount = this.mRoot.getChildCount() - 1; childCount >= 0; childCount--) {
            this.mRoot.getChildAt(childCount).onIsIgnoreOrientationRequestDisabledChanged();
        }
    }

    public boolean isIgnoreOrientationRequestDisabled() {
        return this.mIsIgnoreOrientationRequestDisabled || !this.mLetterboxConfiguration.isIgnoreOrientationRequestAllowed();
    }

    public void freezeRotation(int i) {
        freezeDisplayRotation(0, i);
    }

    public void freezeDisplayRotation(int i, int i2) {
        if (!checkCallingPermission("android.permission.SET_ORIENTATION", "freezeRotation()")) {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        }
        if (i2 < -1 || i2 > 3) {
            throw new IllegalArgumentException("Rotation argument must be -1 or a valid rotation constant.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to freeze rotation for a missing display.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.getDisplayRotation().freezeRotation(i2);
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                updateRotationUnchecked(false, false);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void thawRotation() {
        thawDisplayRotation(0);
    }

    public void thawDisplayRotation(int i) {
        if (!checkCallingPermission("android.permission.SET_ORIENTATION", "thawRotation()")) {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        }
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -1076978367, 1, (String) null, new Object[]{Long.valueOf(getDefaultDisplayRotation())});
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to thaw rotation for a missing display.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.getDisplayRotation().thawRotation();
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                updateRotationUnchecked(false, false);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isRotationFrozen() {
        return isDisplayRotationFrozen(0);
    }

    public boolean isDisplayRotationFrozen(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to check if rotation is frozen on a missing display.");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean isRotationFrozen = displayContent.getDisplayRotation().isRotationFrozen();
                resetPriorityAfterLockedSection();
                return isRotationFrozen;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getDisplayUserRotation(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w(StartingSurfaceController.TAG, "Trying to get user rotation of a missing display.");
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int userRotation = displayContent.getDisplayRotation().getUserRotation();
                resetPriorityAfterLockedSection();
                return userRotation;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    @Override // com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs
    public void updateRotation(boolean z, boolean z2) {
        updateRotationUnchecked(z, z2);
    }

    public final void updateRotationUnchecked(boolean z, boolean z2) {
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -198463978, 15, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2)});
        }
        Trace.traceBegin(32L, "updateRotation");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                int size = this.mRoot.mChildren.size();
                boolean z3 = false;
                for (int i = 0; i < size; i++) {
                    DisplayContent displayContent = (DisplayContent) this.mRoot.mChildren.get(i);
                    Trace.traceBegin(32L, "updateRotation: display");
                    boolean updateRotationUnchecked = displayContent.updateRotationUnchecked();
                    Trace.traceEnd(32L);
                    if (updateRotationUnchecked) {
                        this.mAtmService.getTaskChangeNotificationController().notifyOnActivityRotation(displayContent.mDisplayId);
                    }
                    if (!(updateRotationUnchecked && (displayContent.mRemoteDisplayChangeController.isWaitingForRemoteDisplayChange() || displayContent.mTransitionController.isCollecting()))) {
                        if (z2) {
                            displayContent.setLayoutNeeded();
                            z3 = true;
                        }
                        if (updateRotationUnchecked || z) {
                            displayContent.sendNewConfiguration();
                        }
                    }
                }
                if (z3) {
                    Trace.traceBegin(32L, "updateRotation: performSurfacePlacement");
                    this.mWindowPlacerLocked.performSurfacePlacement();
                    Trace.traceEnd(32L);
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Trace.traceEnd(32L);
        }
    }

    public int getDefaultDisplayRotation() {
        int rotation;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                rotation = getDefaultDisplayContentLocked().getRotation();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return rotation;
    }

    public void setDisplayChangeWindowController(IDisplayChangeWindowController iDisplayChangeWindowController) {
        ActivityTaskManagerService.enforceTaskPermission("setDisplayWindowRotationController");
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                IDisplayChangeWindowController iDisplayChangeWindowController2 = this.mDisplayChangeController;
                if (iDisplayChangeWindowController2 != null) {
                    iDisplayChangeWindowController2.asBinder().unlinkToDeath(this.mDisplayChangeControllerDeath, 0);
                    this.mDisplayChangeController = null;
                }
                iDisplayChangeWindowController.asBinder().linkToDeath(this.mDisplayChangeControllerDeath, 0);
                this.mDisplayChangeController = iDisplayChangeWindowController;
            }
            resetPriorityAfterLockedSection();
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to set rotation controller", e);
        }
    }

    /* JADX WARN: Finally extract failed */
    public SurfaceControl addShellRoot(int i, IWindow iWindow, int i2) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_APP_TOKENS") != 0) {
            throw new SecurityException("Must hold permission android.permission.MANAGE_APP_TOKENS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    SurfaceControl addShellRoot = displayContent.addShellRoot(iWindow, i2);
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return addShellRoot;
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return null;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void setShellRootAccessibilityWindow(int i, int i2, IWindow iWindow) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_APP_TOKENS") != 0) {
            throw new SecurityException("Must hold permission android.permission.MANAGE_APP_TOKENS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    ShellRoot shellRoot = displayContent.mShellRoots.get(i2);
                    if (shellRoot != null) {
                        shellRoot.setAccessibilityWindow(iWindow);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setDisplayWindowInsetsController(int i, IDisplayWindowInsetsController iDisplayWindowInsetsController) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_APP_TOKENS") != 0) {
            throw new SecurityException("Must hold permission android.permission.MANAGE_APP_TOKENS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setRemoteInsetsController(iDisplayWindowInsetsController);
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateDisplayWindowRequestedVisibleTypes(int i, int i2) {
        DisplayContent.RemoteInsetsControlTarget remoteInsetsControlTarget;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_APP_TOKENS") != 0) {
            throw new SecurityException("Must hold permission android.permission.MANAGE_APP_TOKENS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null && (remoteInsetsControlTarget = displayContent.mRemoteInsetsControlTarget) != null) {
                    remoteInsetsControlTarget.setRequestedVisibleTypes(i2);
                    displayContent.getInsetsStateController().onInsetsModified(displayContent.mRemoteInsetsControlTarget);
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int watchRotation(IRotationWatcher iRotationWatcher, int i) {
        int rotation;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Trying to register rotation event for invalid display: " + i);
                }
                this.mRotationWatcherController.registerDisplayRotationWatcher(iRotationWatcher, i);
                rotation = displayContent.getRotation();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return rotation;
    }

    public void removeRotationWatcher(IRotationWatcher iRotationWatcher) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRotationWatcherController.removeRotationWatcher(iRotationWatcher);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public int registerProposedRotationListener(IBinder iBinder, IRotationWatcher iRotationWatcher) {
        int proposedRotation;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowContainer<?> associatedWindowContainer = this.mRotationWatcherController.getAssociatedWindowContainer(iBinder);
                if (associatedWindowContainer == null) {
                    Slog.w(StartingSurfaceController.TAG, "Register rotation listener from non-existing token, uid=" + Binder.getCallingUid());
                    resetPriorityAfterLockedSection();
                    return 0;
                }
                this.mRotationWatcherController.registerProposedRotationListener(iRotationWatcher, iBinder);
                WindowOrientationListener orientationListener = associatedWindowContainer.mDisplayContent.getDisplayRotation().getOrientationListener();
                if (orientationListener != null && (proposedRotation = orientationListener.getProposedRotation()) >= 0) {
                    resetPriorityAfterLockedSection();
                    return proposedRotation;
                }
                int rotation = associatedWindowContainer.getWindowConfiguration().getRotation();
                resetPriorityAfterLockedSection();
                return rotation;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) {
        boolean isWallpaperVisible;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + i);
                }
                this.mWallpaperVisibilityListeners.registerWallpaperVisibilityListener(iWallpaperVisibilityListener, i);
                isWallpaperVisible = displayContent.mWallpaperController.isWallpaperVisible();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return isWallpaperVisible;
    }

    public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWallpaperVisibilityListeners.unregisterWallpaperVisibilityListener(iWallpaperVisibilityListener, i);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener iSystemGestureExclusionListener, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + i);
                }
                displayContent.registerSystemGestureExclusionListener(iSystemGestureExclusionListener);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener iSystemGestureExclusionListener, int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + i);
                }
                displayContent.unregisterSystemGestureExclusionListener(iSystemGestureExclusionListener);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void reportSystemGestureExclusionChanged(Session session, IWindow iWindow, List<Rect> list) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, true);
                if (windowForClientLocked.setSystemGestureExclusion(list)) {
                    windowForClientLocked.getDisplayContent().updateSystemGestureExclusion();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void reportKeepClearAreasChanged(Session session, IWindow iWindow, List<Rect> list, List<Rect> list2) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, true);
                if (windowForClientLocked.setKeepClearAreas(list, list2)) {
                    windowForClientLocked.getDisplayContent().updateKeepClearAreas();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void registerDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
        this.mPolicy.registerDisplayFoldListener(iDisplayFoldListener);
    }

    public void unregisterDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
        this.mPolicy.unregisterDisplayFoldListener(iDisplayFoldListener);
    }

    public void setOverrideFoldedArea(Rect rect) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                this.mPolicy.setOverrideFoldedArea(rect);
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public Rect getFoldedArea() {
        Rect foldedArea;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                foldedArea = this.mPolicy.getFoldedArea();
            }
            resetPriorityAfterLockedSection();
            return foldedArea;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int[] registerDisplayWindowListener(IDisplayWindowListener iDisplayWindowListener) {
        ActivityTaskManagerService.enforceTaskPermission("registerDisplayWindowListener");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDisplayNotificationController.registerListener(iDisplayWindowListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unregisterDisplayWindowListener(IDisplayWindowListener iDisplayWindowListener) {
        ActivityTaskManagerService.enforceTaskPermission("unregisterDisplayWindowListener");
        this.mDisplayNotificationController.unregisterListener(iDisplayWindowListener);
    }

    public int getPreferredOptionsPanelGravity(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    resetPriorityAfterLockedSection();
                    return 81;
                }
                int preferredOptionsPanelGravity = displayContent.getPreferredOptionsPanelGravity();
                resetPriorityAfterLockedSection();
                return preferredOptionsPanelGravity;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean startViewServer(int i) {
        if (!isSystemSecure() && checkCallingPermission("android.permission.DUMP", "startViewServer") && i >= 1024) {
            ViewServer viewServer = this.mViewServer;
            if (viewServer != null) {
                if (!viewServer.isRunning()) {
                    try {
                        return this.mViewServer.start();
                    } catch (IOException unused) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1545962566, 0, "View server did not start", (Object[]) null);
                        }
                    }
                }
                return false;
            }
            try {
                ViewServer viewServer2 = new ViewServer(this, i);
                this.mViewServer = viewServer2;
                return viewServer2.start();
            } catch (IOException unused2) {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1545962566, 0, "View server did not start", (Object[]) null);
                }
                return false;
            }
        }
        return false;
    }

    public final boolean isSystemSecure() {
        return "1".equals(SystemProperties.get("ro.secure", "1")) && "0".equals(SystemProperties.get("ro.debuggable", "0"));
    }

    public boolean stopViewServer() {
        ViewServer viewServer;
        if (isSystemSecure() || !checkCallingPermission("android.permission.DUMP", "stopViewServer") || (viewServer = this.mViewServer) == null) {
            return false;
        }
        return viewServer.stop();
    }

    public boolean isViewServerRunning() {
        ViewServer viewServer;
        return !isSystemSecure() && checkCallingPermission("android.permission.DUMP", "isViewServerRunning") && (viewServer = this.mViewServer) != null && viewServer.isRunning();
    }

    public boolean viewServerListWindows(Socket socket) {
        BufferedWriter bufferedWriter;
        Throwable th;
        boolean z = false;
        if (isSystemSecure()) {
            return false;
        }
        final ArrayList arrayList = new ArrayList();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda9
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        arrayList.add((WindowState) obj);
                    }
                }, false);
            } catch (Throwable th2) {
                resetPriorityAfterLockedSection();
                throw th2;
            }
        }
        resetPriorityAfterLockedSection();
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()), IInstalld.FLAG_FORCE);
            } catch (IOException unused) {
            }
            try {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    WindowState windowState = (WindowState) arrayList.get(i);
                    bufferedWriter.write(Integer.toHexString(System.identityHashCode(windowState)));
                    bufferedWriter.write(32);
                    bufferedWriter.append(windowState.mAttrs.getTitle());
                    bufferedWriter.write(10);
                }
                bufferedWriter.write("DONE.\n");
                bufferedWriter.flush();
                bufferedWriter.close();
                z = true;
            } catch (Exception unused2) {
                bufferedWriter2 = bufferedWriter;
                if (bufferedWriter2 != null) {
                    bufferedWriter2.close();
                }
                return z;
            } catch (Throwable th3) {
                th = th3;
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (Exception unused4) {
        } catch (Throwable th4) {
            bufferedWriter = null;
            th = th4;
        }
        return z;
    }

    public boolean viewServerGetFocusedWindow(Socket socket) {
        boolean z = false;
        if (isSystemSecure()) {
            return false;
        }
        WindowState focusedWindow = getFocusedWindow();
        BufferedWriter bufferedWriter = null;
        try {
            try {
                BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()), IInstalld.FLAG_FORCE);
                if (focusedWindow != null) {
                    try {
                        bufferedWriter2.write(Integer.toHexString(System.identityHashCode(focusedWindow)));
                        bufferedWriter2.write(32);
                        bufferedWriter2.append(focusedWindow.mAttrs.getTitle());
                    } catch (Exception unused) {
                        bufferedWriter = bufferedWriter2;
                        if (bufferedWriter != null) {
                            bufferedWriter.close();
                        }
                        return z;
                    } catch (Throwable th) {
                        th = th;
                        bufferedWriter = bufferedWriter2;
                        if (bufferedWriter != null) {
                            try {
                                bufferedWriter.close();
                            } catch (IOException unused2) {
                            }
                        }
                        throw th;
                    }
                }
                bufferedWriter2.write(10);
                bufferedWriter2.flush();
                bufferedWriter2.close();
                z = true;
            } catch (IOException unused3) {
            }
        } catch (Exception unused4) {
        } catch (Throwable th2) {
            th = th2;
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:47:0x00b3 A[Catch: all -> 0x00dd, TRY_LEAVE, TryCatch #7 {all -> 0x00dd, blocks: (B:45:0x00af, B:47:0x00b3), top: B:70:0x00af }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00e5  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x00ea A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00d9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:83:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean viewServerWindowCommand(Socket socket, String str, String str2) {
        BufferedWriter bufferedWriter;
        Parcel parcel;
        if (isSystemSecure()) {
            return false;
        }
        Parcel parcel2 = null;
        BufferedWriter bufferedWriter2 = null;
        parcel2 = null;
        try {
            int indexOf = str2.indexOf(32);
            if (indexOf == -1) {
                indexOf = str2.length();
            }
            int parseLong = (int) Long.parseLong(str2.substring(0, indexOf), 16);
            str2 = indexOf < str2.length() ? str2.substring(indexOf + 1) : "";
            WindowState findWindow = findWindow(parseLong);
            if (findWindow == null) {
                return false;
            }
            Parcel obtain = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("android.view.IWindow");
                obtain.writeString(str);
                obtain.writeString(str2);
                obtain.writeInt(1);
                ParcelFileDescriptor.fromSocket(socket).writeToParcel(obtain, 0);
                parcel = Parcel.obtain();
                try {
                    findWindow.mClient.asBinder().transact(1, obtain, parcel, 0);
                    parcel.readException();
                    if (!socket.isOutputShutdown()) {
                        BufferedWriter bufferedWriter3 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        try {
                            bufferedWriter3.write("DONE\n");
                            bufferedWriter3.flush();
                            bufferedWriter2 = bufferedWriter3;
                        } catch (Exception e) {
                            parcel2 = obtain;
                            bufferedWriter = bufferedWriter3;
                            e = e;
                            try {
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                }
                                if (parcel2 != null) {
                                }
                                if (parcel != null) {
                                }
                                if (bufferedWriter == null) {
                                }
                            } catch (Throwable th) {
                                th = th;
                                if (parcel2 != null) {
                                    parcel2.recycle();
                                }
                                if (parcel != null) {
                                    parcel.recycle();
                                }
                                if (bufferedWriter != null) {
                                    try {
                                        bufferedWriter.close();
                                    } catch (IOException unused) {
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th2) {
                            parcel2 = obtain;
                            bufferedWriter = bufferedWriter3;
                            th = th2;
                            if (parcel2 != null) {
                            }
                            if (parcel != null) {
                            }
                            if (bufferedWriter != null) {
                            }
                            throw th;
                        }
                    }
                    obtain.recycle();
                    parcel.recycle();
                    if (bufferedWriter2 != null) {
                        try {
                            bufferedWriter2.close();
                        } catch (IOException unused2) {
                        }
                    }
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    bufferedWriter = null;
                    parcel2 = obtain;
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 2086878461, 0, "Could not send command %s with parameters %s. %s", new Object[]{String.valueOf(str), String.valueOf(str2), String.valueOf(e)});
                    }
                    if (parcel2 != null) {
                        parcel2.recycle();
                    }
                    if (parcel != null) {
                        parcel.recycle();
                    }
                    if (bufferedWriter == null) {
                        try {
                            bufferedWriter.close();
                            return false;
                        } catch (IOException unused3) {
                            return false;
                        }
                    }
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    bufferedWriter = null;
                    parcel2 = obtain;
                    if (parcel2 != null) {
                    }
                    if (parcel != null) {
                    }
                    if (bufferedWriter != null) {
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                bufferedWriter = null;
                parcel = null;
            } catch (Throwable th4) {
                th = th4;
                bufferedWriter = null;
                parcel = null;
            }
        } catch (Exception e4) {
            e = e4;
            bufferedWriter = null;
            parcel = null;
        } catch (Throwable th5) {
            th = th5;
            bufferedWriter = null;
            parcel = null;
        }
    }

    public void addWindowChangeListener(WindowChangeListener windowChangeListener) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowChangeListeners.add(windowChangeListener);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void removeWindowChangeListener(WindowChangeListener windowChangeListener) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowChangeListeners.remove(windowChangeListener);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void notifyWindowsChanged() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mWindowChangeListeners.isEmpty()) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                WindowChangeListener[] windowChangeListenerArr = (WindowChangeListener[]) this.mWindowChangeListeners.toArray(new WindowChangeListener[this.mWindowChangeListeners.size()]);
                resetPriorityAfterLockedSection();
                for (WindowChangeListener windowChangeListener : windowChangeListenerArr) {
                    windowChangeListener.windowsChanged();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void notifyFocusChanged() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mWindowChangeListeners.isEmpty()) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                WindowChangeListener[] windowChangeListenerArr = (WindowChangeListener[]) this.mWindowChangeListeners.toArray(new WindowChangeListener[this.mWindowChangeListeners.size()]);
                resetPriorityAfterLockedSection();
                for (WindowChangeListener windowChangeListener : windowChangeListenerArr) {
                    windowChangeListener.focusChanged();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final WindowState findWindow(final int i) {
        WindowState window;
        if (i == -1) {
            return getFocusedWindow();
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                window = this.mRoot.getWindow(new Predicate() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda12
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$findWindow$7;
                        lambda$findWindow$7 = WindowManagerService.lambda$findWindow$7(i, (WindowState) obj);
                        return lambda$findWindow$7;
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return window;
    }

    public static /* synthetic */ boolean lambda$findWindow$7(int i, WindowState windowState) {
        return System.identityHashCode(windowState) == i;
    }

    public Configuration computeNewConfiguration(int i) {
        Configuration computeNewConfigurationLocked;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                computeNewConfigurationLocked = computeNewConfigurationLocked(i);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return computeNewConfigurationLocked;
    }

    public final Configuration computeNewConfigurationLocked(int i) {
        if (this.mDisplayReady) {
            Configuration configuration = new Configuration();
            this.mRoot.getDisplayContent(i).computeScreenConfiguration(configuration);
            return configuration;
        }
        return null;
    }

    public void notifyHardKeyboardStatusChange() {
        WindowManagerInternal.OnHardKeyboardStatusChangeListener onHardKeyboardStatusChangeListener;
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                onHardKeyboardStatusChangeListener = this.mHardKeyboardStatusChangeListener;
                z = this.mHardKeyboardAvailable;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        if (onHardKeyboardStatusChangeListener != null) {
            onHardKeyboardStatusChangeListener.onHardKeyboardStatusChange(z);
        }
    }

    public void setEventDispatching(boolean z) {
        if (!checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setEventDispatching()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mEventDispatchingEnabled = z;
                if (this.mDisplayEnabled) {
                    this.mInputManagerCallback.setEventDispatchingLw(z);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final WindowState getFocusedWindow() {
        WindowState focusedWindowLocked;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                focusedWindowLocked = getFocusedWindowLocked();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return focusedWindowLocked;
    }

    public WindowState getFocusedWindowLocked() {
        return this.mRoot.getTopFocusedDisplayContent().mCurrentFocus;
    }

    public boolean detectSafeMode() {
        if (!this.mInputManagerCallback.waitForInputDevicesReady(1000L) && ProtoLogCache.WM_ERROR_enabled) {
            ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1774661765, 1, "Devices still not ready after waiting %d milliseconds before attempting to detect safe mode.", new Object[]{1000L});
        }
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "safe_boot_disallowed", 0) != 0) {
            return false;
        }
        int keyCodeState = this.mInputManager.getKeyCodeState(-1, -256, 82);
        int keyCodeState2 = this.mInputManager.getKeyCodeState(-1, -256, 47);
        int keyCodeState3 = this.mInputManager.getKeyCodeState(-1, FrameworkStatsLog.HEARING_AID_INFO_REPORTED, 23);
        int scanCodeState = this.mInputManager.getScanCodeState(-1, 65540, 272);
        this.mSafeMode = keyCodeState > 0 || keyCodeState2 > 0 || keyCodeState3 > 0 || scanCodeState > 0 || this.mInputManager.getKeyCodeState(-1, -256, 25) > 0;
        try {
            if (SystemProperties.getInt("persist.sys.safemode", 0) != 0 || SystemProperties.getInt("ro.sys.safemode", 0) != 0) {
                this.mSafeMode = true;
                SystemProperties.set("persist.sys.safemode", "");
            }
        } catch (IllegalArgumentException unused) {
        }
        if (this.mSafeMode) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_ERROR, -1443029505, 85, "SAFE MODE ENABLED (menu=%d s=%d dpad=%d trackball=%d)", new Object[]{Long.valueOf(keyCodeState), Long.valueOf(keyCodeState2), Long.valueOf(keyCodeState3), Long.valueOf(scanCodeState)});
            }
            if (SystemProperties.getInt("ro.sys.safemode", 0) == 0) {
                SystemProperties.set("ro.sys.safemode", "1");
            }
        } else if (ProtoLogCache.WM_ERROR_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_ERROR, 1866772666, 0, "SAFE MODE not enabled", (Object[]) null);
        }
        this.mPolicy.setSafeMode(this.mSafeMode);
        return this.mSafeMode;
    }

    public void displayReady() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mMaxUiWidth > 0) {
                    this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda18
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.this.lambda$displayReady$8((DisplayContent) obj);
                        }
                    });
                }
                applyForcedPropertiesForDefaultDisplay();
                this.mAnimator.ready();
                this.mDisplayReady = true;
                this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda19
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((DisplayContent) obj).reconfigureDisplayLocked();
                    }
                });
                this.mIsTouchDevice = this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen");
                this.mIsFakeTouchDevice = this.mContext.getPackageManager().hasSystemFeature("android.hardware.faketouch");
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        this.mAtmService.updateConfiguration(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayReady$8(DisplayContent displayContent) {
        displayContent.setMaxUiWidth(this.mMaxUiWidth);
    }

    public void systemReady() {
        this.mSystemReady = true;
        this.mPolicy.systemReady();
        this.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda15
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DisplayPolicy) obj).systemReady();
            }
        });
        this.mSnapshotPersistQueue.systemReady();
        this.mHasWideColorGamutSupport = queryWideColorGamutSupport();
        this.mHasHdrSupport = queryHdrSupport();
        Handler handler = UiThread.getHandler();
        final SettingsObserver settingsObserver = this.mSettingsObserver;
        Objects.requireNonNull(settingsObserver);
        handler.post(new Runnable() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerService.SettingsObserver.this.loadSettings();
            }
        });
        IVrManager asInterface = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (asInterface != null) {
            try {
                boolean vrModeState = asInterface.getVrModeState();
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    asInterface.registerListener(this.mVrStateCallbacks);
                    if (vrModeState) {
                        this.mVrModeEnabled = vrModeState;
                        this.mVrStateCallbacks.onVrStateChanged(vrModeState);
                    }
                }
                resetPriorityAfterLockedSection();
            } catch (RemoteException unused) {
            }
        }
    }

    public static boolean queryWideColorGamutSupport() {
        Optional<Boolean> has_wide_color_display = SurfaceFlingerProperties.has_wide_color_display();
        if (has_wide_color_display.isPresent()) {
            return has_wide_color_display.get().booleanValue();
        }
        try {
            OptionalBool hasWideColorDisplay = ISurfaceFlingerConfigs.getService().hasWideColorDisplay();
            if (hasWideColorDisplay != null) {
                return hasWideColorDisplay.value;
            }
            return false;
        } catch (RemoteException | NoSuchElementException unused) {
            return false;
        }
    }

    public static boolean queryHdrSupport() {
        Optional<Boolean> has_HDR_display = SurfaceFlingerProperties.has_HDR_display();
        if (has_HDR_display.isPresent()) {
            return has_HDR_display.get().booleanValue();
        }
        try {
            OptionalBool hasHDRDisplay = ISurfaceFlingerConfigs.getService().hasHDRDisplay();
            if (hasHDRDisplay != null) {
                return hasHDRDisplay.value;
            }
            return false;
        } catch (RemoteException | NoSuchElementException unused) {
            return false;
        }
    }

    public InputTarget getInputTargetFromToken(IBinder iBinder) {
        WindowState windowState = this.mInputToWindowMap.get(iBinder);
        if (windowState != null) {
            return windowState;
        }
        EmbeddedWindowController.EmbeddedWindow embeddedWindow = this.mEmbeddedWindowController.get(iBinder);
        if (embeddedWindow != null) {
            return embeddedWindow;
        }
        return null;
    }

    public InputTarget getInputTargetFromWindowTokenLocked(IBinder iBinder) {
        WindowState windowState = this.mWindowMap.get(iBinder);
        return windowState != null ? windowState : this.mEmbeddedWindowController.getByWindowToken(iBinder);
    }

    public void reportFocusChanged(IBinder iBinder, IBinder iBinder2) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                InputTarget inputTargetFromToken = getInputTargetFromToken(iBinder);
                InputTarget inputTargetFromToken2 = getInputTargetFromToken(iBinder2);
                if (inputTargetFromToken2 == null && inputTargetFromToken == null) {
                    Slog.v(StartingSurfaceController.TAG, "Unknown focus tokens, dropping reportFocusChanged");
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mFocusedInputTarget = inputTargetFromToken2;
                this.mAccessibilityController.onFocusChanged(inputTargetFromToken, inputTargetFromToken2);
                if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, 115358443, 0, (String) null, new Object[]{String.valueOf(inputTargetFromToken), String.valueOf(inputTargetFromToken2)});
                }
                resetPriorityAfterLockedSection();
                WindowState windowState = inputTargetFromToken2 != null ? inputTargetFromToken2.getWindowState() : null;
                if (windowState != null && windowState.mInputChannelToken == iBinder2) {
                    this.mAnrController.onFocusChanged(windowState);
                    windowState.reportFocusChangedSerialized(true);
                    notifyFocusChanged();
                }
                WindowState windowState2 = inputTargetFromToken != null ? inputTargetFromToken.getWindowState() : null;
                if (windowState2 == null || windowState2.mInputChannelToken != iBinder) {
                    return;
                }
                windowState2.reportFocusChangedSerialized(false);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* renamed from: com.android.server.wm.WindowManagerService$H */
    /* loaded from: classes2.dex */
    public final class HandlerC1915H extends Handler {
        public HandlerC1915H() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Runnable remove;
            Runnable remove2;
            boolean checkBootAnimationCompleteLocked;
            int i = message.what;
            if (i == 11) {
                DisplayContent displayContent = (DisplayContent) message.obj;
                synchronized (WindowManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        displayContent.onWindowFreezeTimeout();
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else if (i == 14) {
                Settings.Global.putFloat(WindowManagerService.this.mContext.getContentResolver(), "window_animation_scale", WindowManagerService.this.mWindowAnimationScaleSetting);
                Settings.Global.putFloat(WindowManagerService.this.mContext.getContentResolver(), "transition_animation_scale", WindowManagerService.this.mTransitionAnimationScaleSetting);
                Settings.Global.putFloat(WindowManagerService.this.mContext.getContentResolver(), "animator_duration_scale", WindowManagerService.this.mAnimatorDurationScaleSetting);
            } else {
                if (i == 19) {
                    WindowManagerService windowManagerService = WindowManagerService.this;
                    if (windowManagerService.mWindowsChanged) {
                        synchronized (windowManagerService.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                WindowManagerService.this.mWindowsChanged = false;
                            } finally {
                                WindowManagerService.resetPriorityAfterLockedSection();
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        WindowManagerService.this.notifyWindowsChanged();
                    }
                } else if (i == 30) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            WindowManagerService windowManagerService2 = WindowManagerService.this;
                            if (windowManagerService2.mClientFreezingScreen) {
                                windowManagerService2.mClientFreezingScreen = false;
                                windowManagerService2.mLastFinishedFreezeSource = "client-timeout";
                                windowManagerService2.stopFreezingDisplayLocked();
                            }
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (i == 41) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            DisplayContent displayContent2 = (DisplayContent) message.obj;
                            if (displayContent2 != null) {
                                displayContent2.adjustForImeIfNeeded();
                            }
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (i == 46) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            for (int size = WindowManagerService.this.mWindowReplacementTimeouts.size() - 1; size >= 0; size--) {
                                WindowManagerService.this.mWindowReplacementTimeouts.get(size).onWindowReplacementTimeout();
                            }
                            WindowManagerService.this.mWindowReplacementTimeouts.clear();
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (i == 55) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            WindowManagerService.this.restorePointerIconLocked((DisplayContent) message.obj, message.arg1, message.arg2);
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (i == 58) {
                    WindowManagerService.this.mAmInternal.setHasOverlayUi(message.arg1, message.arg2 == 1);
                } else if (i == 16) {
                    WindowManagerService.this.performEnableScreen();
                } else if (i == 17) {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (ProtoLogCache.WM_ERROR_enabled) {
                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -322035974, 0, "App freeze timeout expired.", (Object[]) null);
                            }
                            WindowManagerService windowManagerService3 = WindowManagerService.this;
                            windowManagerService3.mWindowsFreezingScreen = 2;
                            for (int size2 = windowManagerService3.mAppFreezeListeners.size() - 1; size2 >= 0; size2--) {
                                WindowManagerService.this.mAppFreezeListeners.get(size2).onAppFreezeTimeout();
                            }
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (i == 51) {
                    int i2 = message.arg1;
                    if (i2 == 0) {
                        WindowManagerService windowManagerService4 = WindowManagerService.this;
                        windowManagerService4.mWindowAnimationScaleSetting = windowManagerService4.getWindowAnimationScaleSetting();
                    } else if (i2 == 1) {
                        WindowManagerService windowManagerService5 = WindowManagerService.this;
                        windowManagerService5.mTransitionAnimationScaleSetting = windowManagerService5.getTransitionAnimationScaleSetting();
                    } else if (i2 == 2) {
                        WindowManagerService windowManagerService6 = WindowManagerService.this;
                        windowManagerService6.mAnimatorDurationScaleSetting = windowManagerService6.getAnimatorDurationScaleSetting();
                        WindowManagerService.this.dispatchNewAnimatorScaleLocked(null);
                    }
                } else if (i != 52) {
                    switch (i) {
                        case 22:
                            WindowManagerService.this.notifyHardKeyboardStatusChange();
                            return;
                        case 23:
                            WindowManagerService.this.performBootTimeout();
                            return;
                        case 24:
                            WindowContainer windowContainer = (WindowContainer) message.obj;
                            synchronized (WindowManagerService.this.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    if (ProtoLogCache.WM_ERROR_enabled) {
                                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1526645239, 0, "Timeout waiting for drawn: undrawn=%s", new Object[]{String.valueOf(windowContainer.mWaitingForDrawn)});
                                    }
                                    if (Trace.isTagEnabled(32L)) {
                                        for (int i3 = 0; i3 < windowContainer.mWaitingForDrawn.size(); i3++) {
                                            WindowManagerService.this.traceEndWaitingForWindowDrawn(windowContainer.mWaitingForDrawn.get(i3));
                                        }
                                    }
                                    windowContainer.mWaitingForDrawn.clear();
                                    remove = WindowManagerService.this.mWaitingForDrawnCallbacks.remove(windowContainer);
                                } finally {
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            if (remove != null) {
                                remove.run();
                                return;
                            }
                            return;
                        case 25:
                            WindowManagerService.this.showStrictModeViolation(message.arg1, message.arg2);
                            return;
                        default:
                            switch (i) {
                                case 32:
                                    ActivityRecord activityRecord = (ActivityRecord) message.obj;
                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                        try {
                                            WindowManagerService.boostPriorityForLockedSection();
                                            if (activityRecord.isAttached()) {
                                                activityRecord.getRootTask().notifyActivityDrawnLocked(activityRecord);
                                            }
                                        } finally {
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                        }
                                    }
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    return;
                                case 33:
                                    WindowContainer windowContainer2 = (WindowContainer) message.obj;
                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                        try {
                                            WindowManagerService.boostPriorityForLockedSection();
                                            remove2 = WindowManagerService.this.mWaitingForDrawnCallbacks.remove(windowContainer2);
                                        } finally {
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                        }
                                    }
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    if (remove2 != null) {
                                        remove2.run();
                                        return;
                                    }
                                    return;
                                case 34:
                                    float currentAnimatorScale = WindowManagerService.this.getCurrentAnimatorScale();
                                    ValueAnimator.setDurationScale(currentAnimatorScale);
                                    Session session = (Session) message.obj;
                                    if (session != null) {
                                        try {
                                            session.mCallback.onAnimatorScaleChanged(currentAnimatorScale);
                                            return;
                                        } catch (RemoteException unused) {
                                            return;
                                        }
                                    }
                                    ArrayList arrayList = new ArrayList();
                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                        try {
                                            WindowManagerService.boostPriorityForLockedSection();
                                            for (int i4 = 0; i4 < WindowManagerService.this.mSessions.size(); i4++) {
                                                arrayList.add(WindowManagerService.this.mSessions.valueAt(i4).mCallback);
                                            }
                                        } finally {
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                        }
                                    }
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    for (int i5 = 0; i5 < arrayList.size(); i5++) {
                                        try {
                                            ((IWindowSessionCallback) arrayList.get(i5)).onAnimatorScaleChanged(currentAnimatorScale);
                                        } catch (RemoteException unused2) {
                                        }
                                    }
                                    return;
                                default:
                                    switch (i) {
                                        case 36:
                                            WindowManagerService.this.showEmulatorDisplayOverlay();
                                            return;
                                        case 37:
                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                try {
                                                    WindowManagerService.boostPriorityForLockedSection();
                                                    if (ProtoLogCache.WM_DEBUG_BOOT_enabled) {
                                                        ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_BOOT, 2034780299, 0, (String) null, (Object[]) null);
                                                    }
                                                    checkBootAnimationCompleteLocked = WindowManagerService.this.checkBootAnimationCompleteLocked();
                                                } finally {
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                }
                                            }
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            if (checkBootAnimationCompleteLocked) {
                                                WindowManagerService.this.performEnableScreen();
                                                return;
                                            }
                                            return;
                                        case 38:
                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                try {
                                                    WindowManagerService.boostPriorityForLockedSection();
                                                    WindowManagerService windowManagerService7 = WindowManagerService.this;
                                                    windowManagerService7.mLastANRState = null;
                                                    windowManagerService7.mAtmService.mLastANRState = null;
                                                } finally {
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                }
                                            }
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            return;
                                        case 39:
                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                try {
                                                    WindowManagerService.boostPriorityForLockedSection();
                                                    WallpaperController wallpaperController = (WallpaperController) message.obj;
                                                    if (wallpaperController != null && wallpaperController.processWallpaperDrawPendingTimeout()) {
                                                        WindowManagerService.this.mWindowPlacerLocked.performSurfacePlacement();
                                                    }
                                                } finally {
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                }
                                            }
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            return;
                                        default:
                                            switch (i) {
                                                case 60:
                                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                                        try {
                                                            WindowManagerService.boostPriorityForLockedSection();
                                                            if (WindowManagerService.this.mRecentsAnimationController != null) {
                                                                WindowManagerService.this.mRecentsAnimationController.scheduleFailsafe();
                                                            }
                                                        } finally {
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                        }
                                                    }
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                    return;
                                                case 61:
                                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                                        try {
                                                            WindowManagerService.boostPriorityForLockedSection();
                                                            WindowManagerService.this.updateFocusedWindowLocked(0, true);
                                                        } finally {
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                        }
                                                    }
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                    return;
                                                case 62:
                                                    synchronized (WindowManagerService.this.mGlobalLock) {
                                                        try {
                                                            WindowManagerService.boostPriorityForLockedSection();
                                                            WindowManagerService.this.onPointerDownOutsideFocusLocked((IBinder) message.obj);
                                                        } finally {
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                        }
                                                    }
                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                    return;
                                                default:
                                                    switch (i) {
                                                        case 64:
                                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                                try {
                                                                    WindowManagerService.boostPriorityForLockedSection();
                                                                    WindowState windowState = (WindowState) message.obj;
                                                                    Slog.i(StartingSurfaceController.TAG, "Blast sync timeout: " + windowState);
                                                                    windowState.immediatelyNotifyBlastSync();
                                                                } finally {
                                                                    WindowManagerService.resetPriorityAfterLockedSection();
                                                                }
                                                            }
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                            return;
                                                        case 65:
                                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                                try {
                                                                    WindowManagerService.boostPriorityForLockedSection();
                                                                    Task task = (Task) message.obj;
                                                                    task.reparent(WindowManagerService.this.mRoot.getDefaultTaskDisplayArea(), true);
                                                                    task.resumeNextFocusAfterReparent();
                                                                } finally {
                                                                }
                                                            }
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                            return;
                                                        case 66:
                                                            synchronized (WindowManagerService.this.mGlobalLock) {
                                                                try {
                                                                    WindowManagerService.boostPriorityForLockedSection();
                                                                    WindowManagerService windowManagerService8 = WindowManagerService.this;
                                                                    if (windowManagerService8.mWindowsInsetsChanged > 0) {
                                                                        windowManagerService8.mWindowsInsetsChanged = 0;
                                                                        windowManagerService8.mWindowPlacerLocked.performSurfacePlacement();
                                                                    }
                                                                } finally {
                                                                }
                                                            }
                                                            WindowManagerService.resetPriorityAfterLockedSection();
                                                            return;
                                                        default:
                                                            return;
                                                    }
                                            }
                                    }
                            }
                    }
                } else {
                    WindowState windowState2 = (WindowState) message.obj;
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            windowState2.mAttrs.flags &= -129;
                            windowState2.hidePermanentlyLw();
                            windowState2.setDisplayLayoutNeeded();
                            WindowManagerService.this.mWindowPlacerLocked.performSurfacePlacement();
                        } finally {
                            WindowManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        public void sendNewMessageDelayed(int i, Object obj, long j) {
            removeMessages(i, obj);
            sendMessageDelayed(obtainMessage(i, obj), j);
        }
    }

    public IWindowSession openSession(IWindowSessionCallback iWindowSessionCallback) {
        return new Session(this, iWindowSessionCallback);
    }

    public boolean useBLAST() {
        return this.mUseBLAST;
    }

    public void getInitialDisplaySize(int i, Point point) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                    point.x = displayContent.mInitialDisplayWidth;
                    point.y = displayContent.mInitialDisplayHeight;
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void getBaseDisplaySize(int i, Point point) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                    point.x = displayContent.mBaseDisplayWidth;
                    point.y = displayContent.mBaseDisplayHeight;
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setForcedDisplaySize(int i, int i2, int i3) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setForcedSize(i2, i3);
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setForcedDisplayScalingMode(int i, int i2) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setForcedScalingMode(i2);
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setSandboxDisplayApis(int i, boolean z) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setSandboxDisplayApis(z);
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a2  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00b3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean applyForcedPropertiesForDefaultDisplay() {
        boolean z;
        int forcedDisplayDensityForUserLocked;
        int indexOf;
        int parseInt;
        int parseInt2;
        DisplayContent defaultDisplayContentLocked = getDefaultDisplayContentLocked();
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "display_size_forced");
        if (string == null || string.length() == 0) {
            string = SystemProperties.get("ro.config.size_override", (String) null);
        }
        if (string != null && string.length() > 0 && (indexOf = string.indexOf(44)) > 0 && string.lastIndexOf(44) == indexOf) {
            try {
                parseInt = Integer.parseInt(string.substring(0, indexOf));
                parseInt2 = Integer.parseInt(string.substring(indexOf + 1));
            } catch (NumberFormatException unused) {
            }
            if (defaultDisplayContentLocked.mBaseDisplayWidth != parseInt || defaultDisplayContentLocked.mBaseDisplayHeight != parseInt2) {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_ERROR, 1115417974, 5, "FORCED DISPLAY SIZE: %dx%d", new Object[]{Long.valueOf(parseInt), Long.valueOf(parseInt2)});
                }
                defaultDisplayContentLocked.updateBaseDisplayMetrics(parseInt, parseInt2, defaultDisplayContentLocked.mBaseDisplayDensity, defaultDisplayContentLocked.mBaseDisplayPhysicalXDpi, defaultDisplayContentLocked.mBaseDisplayPhysicalYDpi);
                z = true;
                forcedDisplayDensityForUserLocked = getForcedDisplayDensityForUserLocked(this.mCurrentUserId);
                if (forcedDisplayDensityForUserLocked != 0 && forcedDisplayDensityForUserLocked != defaultDisplayContentLocked.mBaseDisplayDensity) {
                    defaultDisplayContentLocked.mBaseDisplayDensity = forcedDisplayDensityForUserLocked;
                    z = true;
                }
                if (defaultDisplayContentLocked.mDisplayScalingDisabled == (Settings.Global.getInt(this.mContext.getContentResolver(), "display_scaling_force", 0) == 0)) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.i(ProtoLogGroup.WM_ERROR, 954470154, 0, "FORCED DISPLAY SCALING DISABLED", (Object[]) null);
                    }
                    defaultDisplayContentLocked.mDisplayScalingDisabled = true;
                    return true;
                }
                return z;
            }
        }
        z = false;
        forcedDisplayDensityForUserLocked = getForcedDisplayDensityForUserLocked(this.mCurrentUserId);
        if (forcedDisplayDensityForUserLocked != 0) {
            defaultDisplayContentLocked.mBaseDisplayDensity = forcedDisplayDensityForUserLocked;
            z = true;
        }
        if (defaultDisplayContentLocked.mDisplayScalingDisabled == (Settings.Global.getInt(this.mContext.getContentResolver(), "display_scaling_force", 0) == 0)) {
        }
    }

    public void clearForcedDisplaySize(int i) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    int i2 = displayContent.mInitialDisplayWidth;
                    int i3 = displayContent.mInitialDisplayHeight;
                    float f = displayContent.mInitialPhysicalXDpi;
                    displayContent.setForcedSize(i2, i3, f, f);
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getInitialDisplayDensity(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                    int initialDisplayDensity = displayContent.getInitialDisplayDensity();
                    resetPriorityAfterLockedSection();
                    return initialDisplayDensity;
                }
                DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(i);
                if (displayInfo == null || !displayInfo.hasAccess(Binder.getCallingUid())) {
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int i2 = displayInfo.logicalDensityDpi;
                resetPriorityAfterLockedSection();
                return i2;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getBaseDisplayDensity(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null || !displayContent.hasAccess(Binder.getCallingUid())) {
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int i2 = displayContent.mBaseDisplayDensity;
                resetPriorityAfterLockedSection();
                return i2;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getDisplayIdByUniqueId(String str) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(str);
                if (displayContent == null || !displayContent.hasAccess(Binder.getCallingUid())) {
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int i = displayContent.mDisplayId;
                resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setForcedDisplayDensityForUser(int i, int i2, int i3) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i3, false, true, "setForcedDisplayDensityForUser", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setForcedDensity(i2, handleIncomingUser);
                } else {
                    DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(i);
                    if (displayInfo != null) {
                        this.mDisplayWindowSettings.setForcedDensity(displayInfo, i2, i3);
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void clearForcedDisplayDensityForUser(int i, int i2) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i2, false, true, "clearForcedDisplayDensityForUser", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.setForcedDensity(displayContent.getInitialDisplayDensity(), handleIncomingUser);
                } else {
                    DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(i);
                    if (displayInfo != null) {
                        this.mDisplayWindowSettings.setForcedDensity(displayInfo, displayInfo.logicalDensityDpi, i2);
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int getForcedDisplayDensityForUserLocked(int i) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "display_density_forced", i);
        if (stringForUser == null || stringForUser.length() == 0) {
            stringForUser = SystemProperties.get("ro.config.density_override", (String) null);
        }
        if (stringForUser == null || stringForUser.length() <= 0) {
            return 0;
        }
        try {
            return Integer.parseInt(stringForUser);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public void startWindowTrace() {
        this.mWindowTracing.startTrace(null);
    }

    public void stopWindowTrace() {
        this.mWindowTracing.stopTrace(null);
    }

    public void saveWindowTraceToFile() {
        this.mWindowTracing.saveForBugreport(null);
    }

    public boolean isWindowTraceEnabled() {
        return this.mWindowTracing.isEnabled();
    }

    public void startTransitionTrace() {
        this.mTransitionTracer.startTrace(null);
    }

    public void stopTransitionTrace() {
        this.mTransitionTracer.stopTrace(null);
    }

    public boolean isTransitionTraceEnabled() {
        return this.mTransitionTracer.isActiveTracingEnabled();
    }

    public boolean registerCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) {
        return this.mBlurController.registerCrossWindowBlurEnabledListener(iCrossWindowBlurEnabledListener);
    }

    public void unregisterCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) {
        this.mBlurController.unregisterCrossWindowBlurEnabledListener(iCrossWindowBlurEnabledListener);
    }

    public final WindowState windowForClientLocked(Session session, IWindow iWindow, boolean z) {
        return windowForClientLocked(session, iWindow.asBinder(), z);
    }

    public final WindowState windowForClientLocked(Session session, IBinder iBinder, boolean z) {
        WindowState windowState = this.mWindowMap.get(iBinder);
        if (windowState == null) {
            if (z) {
                throw new IllegalArgumentException("Requested window " + iBinder + " does not exist");
            }
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -2101985723, 0, "Failed looking up window session=%s callers=%s", new Object[]{String.valueOf(session), String.valueOf(Debug.getCallers(3))});
            }
            return null;
        } else if (session == null || windowState.mSession == session) {
            return windowState;
        } else {
            if (z) {
                throw new IllegalArgumentException("Requested window " + iBinder + " is in session " + windowState.mSession + ", not " + session);
            }
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -2101985723, 0, "Failed looking up window session=%s callers=%s", new Object[]{String.valueOf(session), String.valueOf(Debug.getCallers(3))});
            }
            return null;
        }
    }

    public void makeWindowFreezingScreenIfNeededLocked(WindowState windowState) {
        int i = this.mFrozenDisplayId;
        if (i == -1 || i != windowState.getDisplayId() || this.mWindowsFreezingScreen == 2) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -1632122349, 0, (String) null, new Object[]{String.valueOf(windowState)});
        }
        if (windowState.isVisibleRequested()) {
            windowState.setOrientationChanging(true);
        }
        if (this.mWindowsFreezingScreen == 0) {
            this.mWindowsFreezingScreen = 1;
            this.f1164mH.sendNewMessageDelayed(11, windowState.getDisplayContent(), 2000L);
        }
    }

    public void checkDrawnWindowsLocked() {
        if (this.mWaitingForDrawnCallbacks.isEmpty()) {
            return;
        }
        this.mWaitingForDrawnCallbacks.forEach(new BiConsumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda17
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                WindowManagerService.this.lambda$checkDrawnWindowsLocked$9((WindowContainer) obj, (Runnable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDrawnWindowsLocked$9(WindowContainer windowContainer, Runnable runnable) {
        int size = windowContainer.mWaitingForDrawn.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            WindowState windowState = windowContainer.mWaitingForDrawn.get(size);
            if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 892244061, 508, (String) null, new Object[]{String.valueOf(windowState), Boolean.valueOf(windowState.mRemoved), Boolean.valueOf(windowState.isVisible()), Boolean.valueOf(windowState.mHasSurface), Long.valueOf(windowState.mWinAnimator.mDrawState)});
            }
            if (windowState.mRemoved || !windowState.mHasSurface || !windowState.isVisibleByPolicy()) {
                if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 463993897, 0, (String) null, new Object[]{String.valueOf(windowState)});
                }
                windowContainer.mWaitingForDrawn.remove(windowState);
                if (Trace.isTagEnabled(32L)) {
                    traceEndWaitingForWindowDrawn(windowState);
                }
            } else if (windowState.hasDrawn()) {
                if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 1401700824, 0, (String) null, new Object[]{String.valueOf(windowState)});
                }
                windowContainer.mWaitingForDrawn.remove(windowState);
                if (Trace.isTagEnabled(32L)) {
                    traceEndWaitingForWindowDrawn(windowState);
                }
            }
        }
        if (windowContainer.mWaitingForDrawn.isEmpty()) {
            if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 665256544, 0, (String) null, (Object[]) null);
            }
            this.f1164mH.removeMessages(24, windowContainer);
            HandlerC1915H handlerC1915H = this.f1164mH;
            handlerC1915H.sendMessage(handlerC1915H.obtainMessage(33, windowContainer));
        }
    }

    public final void traceStartWaitingForWindowDrawn(WindowState windowState) {
        String str = "waitForAllWindowsDrawn#" + ((Object) windowState.getWindowTag());
        Trace.asyncTraceBegin(32L, str.substring(0, Math.min(127, str.length())), 0);
    }

    public final void traceEndWaitingForWindowDrawn(WindowState windowState) {
        String str = "waitForAllWindowsDrawn#" + ((Object) windowState.getWindowTag());
        Trace.asyncTraceEnd(32L, str.substring(0, Math.min(127, str.length())), 0);
    }

    public void requestTraversal() {
        this.mWindowPlacerLocked.requestTraversal();
    }

    public void scheduleAnimationLocked() {
        this.mAnimator.scheduleAnimation();
    }

    public boolean updateFocusedWindowLocked(int i, boolean z) {
        Trace.traceBegin(32L, "wmUpdateFocus");
        boolean updateFocusedWindowLocked = this.mRoot.updateFocusedWindowLocked(i, z);
        Trace.traceEnd(32L);
        return updateFocusedWindowLocked;
    }

    public void startFreezingDisplay(int i, int i2) {
        startFreezingDisplay(i, i2, getDefaultDisplayContentLocked());
    }

    public void startFreezingDisplay(int i, int i2, DisplayContent displayContent) {
        startFreezingDisplay(i, i2, displayContent, -1);
    }

    public void startFreezingDisplay(int i, int i2, DisplayContent displayContent, int i3) {
        if (this.mDisplayFrozen || displayContent.getDisplayRotation().isRotatingSeamlessly() || !displayContent.isReady() || !displayContent.getDisplayPolicy().isScreenOnFully() || displayContent.getDisplayInfo().state == 1 || !displayContent.okToAnimate()) {
            return;
        }
        Trace.traceBegin(32L, "WMS.doStartFreezingDisplay");
        doStartFreezingDisplay(i, i2, displayContent, i3);
        Trace.traceEnd(32L);
    }

    public final void doStartFreezingDisplay(int i, int i2, DisplayContent displayContent, int i3) {
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 9803449, 5, (String) null, new Object[]{Long.valueOf(i), Long.valueOf(i2), String.valueOf(Debug.getCallers(8))});
        }
        this.mScreenFrozenLock.acquire();
        this.mAtmService.startLaunchPowerMode(2);
        this.mDisplayFrozen = true;
        this.mDisplayFreezeTime = SystemClock.elapsedRealtime();
        this.mLastFinishedFreezeSource = null;
        this.mFrozenDisplayId = displayContent.getDisplayId();
        this.mInputManagerCallback.freezeInputDispatchingLw();
        if (displayContent.mAppTransition.isTransitionSet()) {
            displayContent.mAppTransition.freeze();
        }
        this.mLatencyTracker.onActionStart(6);
        this.mExitAnimId = i;
        this.mEnterAnimId = i2;
        displayContent.updateDisplayInfo();
        if (i3 == -1) {
            i3 = displayContent.getDisplayInfo().rotation;
        }
        displayContent.setRotationAnimation(new ScreenRotationAnimation(displayContent, i3));
    }

    public void stopFreezingDisplayLocked() {
        int i;
        boolean z;
        boolean z2;
        if (this.mDisplayFrozen) {
            DisplayContent displayContent = this.mRoot.getDisplayContent(this.mFrozenDisplayId);
            if (displayContent != null) {
                i = displayContent.mOpeningApps.size();
                z = displayContent.mWaitingForConfig;
                z2 = displayContent.mRemoteDisplayChangeController.isWaitingForRemoteDisplayChange();
            } else {
                i = 0;
                z = false;
                z2 = false;
            }
            if (z || z2 || this.mAppsFreezingScreen > 0 || this.mWindowsFreezingScreen == 1 || this.mClientFreezingScreen || i > 0) {
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 1360176455, 1887, (String) null, new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Long.valueOf(this.mAppsFreezingScreen), Long.valueOf(this.mWindowsFreezingScreen), Boolean.valueOf(this.mClientFreezingScreen), Long.valueOf(i)});
                    return;
                }
                return;
            }
            Trace.traceBegin(32L, "WMS.doStopFreezingDisplayLocked-" + this.mLastFinishedFreezeSource);
            doStopFreezingDisplayLocked(displayContent);
            Trace.traceEnd(32L);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00f1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void doStopFreezingDisplayLocked(DisplayContent displayContent) {
        boolean z;
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 355720268, 0, (String) null, (Object[]) null);
        }
        this.mFrozenDisplayId = -1;
        this.mDisplayFrozen = false;
        this.mInputManagerCallback.thawInputDispatchingLw();
        this.mLastDisplayFreezeDuration = (int) (SystemClock.elapsedRealtime() - this.mDisplayFreezeTime);
        StringBuilder sb = new StringBuilder(128);
        sb.append("Screen frozen for ");
        TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, sb);
        if (this.mLastFinishedFreezeSource != null) {
            sb.append(" due to ");
            sb.append(this.mLastFinishedFreezeSource);
        }
        if (ProtoLogCache.WM_ERROR_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_ERROR, -583031528, 0, "%s", new Object[]{String.valueOf(sb.toString())});
        }
        this.f1164mH.removeMessages(17);
        this.f1164mH.removeMessages(30);
        ScreenRotationAnimation rotationAnimation = displayContent == null ? null : displayContent.getRotationAnimation();
        boolean z2 = true;
        if (rotationAnimation != null && rotationAnimation.hasScreenshot()) {
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ORIENTATION, 1634557978, 0, (String) null, (Object[]) null);
            }
            DisplayInfo displayInfo = displayContent.getDisplayInfo();
            if (!displayContent.getDisplayRotation().validateRotationAnimation(this.mExitAnimId, this.mEnterAnimId, false)) {
                this.mEnterAnimId = 0;
                this.mExitAnimId = 0;
            }
            if (rotationAnimation.dismiss(this.mTransaction, 10000L, getTransitionAnimationScaleLocked(), displayInfo.logicalWidth, displayInfo.logicalHeight, this.mExitAnimId, this.mEnterAnimId)) {
                this.mTransaction.apply();
                z = false;
                if (displayContent != null || !displayContent.updateOrientation()) {
                    z2 = false;
                }
                this.mScreenFrozenLock.release();
                if (z && displayContent != null) {
                    if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, -783405930, 0, (String) null, (Object[]) null);
                    }
                    z2 |= displayContent.updateRotationUnchecked();
                }
                if (z2) {
                    displayContent.sendNewConfiguration();
                }
                this.mAtmService.endLaunchPowerMode(2);
                this.mLatencyTracker.onActionEnd(6);
            }
            rotationAnimation.kill();
            displayContent.setRotationAnimation(null);
        } else if (rotationAnimation != null) {
            rotationAnimation.kill();
            displayContent.setRotationAnimation(null);
        }
        z = true;
        if (displayContent != null) {
        }
        z2 = false;
        this.mScreenFrozenLock.release();
        if (z) {
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            }
            z2 |= displayContent.updateRotationUnchecked();
        }
        if (z2) {
        }
        this.mAtmService.endLaunchPowerMode(2);
        this.mLatencyTracker.onActionEnd(6);
    }

    public static int getPropertyInt(String[] strArr, int i, int i2, int i3, DisplayMetrics displayMetrics) {
        String str;
        if (i < strArr.length && (str = strArr[i]) != null && str.length() > 0) {
            try {
                return Integer.parseInt(str);
            } catch (Exception unused) {
            }
        }
        return i2 == 0 ? i3 : (int) TypedValue.applyDimension(i2, i3, displayMetrics);
    }

    public void createWatermark() {
        FileInputStream fileInputStream;
        String[] split;
        if (this.mWatermark != null) {
            return;
        }
        DataInputStream dataInputStream = null;
        try {
            try {
                fileInputStream = new FileInputStream(new File("/system/etc/setup.conf"));
                try {
                    DataInputStream dataInputStream2 = new DataInputStream(fileInputStream);
                    try {
                        String readLine = dataInputStream2.readLine();
                        if (readLine != null && (split = readLine.split("%")) != null && split.length > 0) {
                            DisplayContent defaultDisplayContentLocked = getDefaultDisplayContentLocked();
                            this.mWatermark = new Watermark(defaultDisplayContentLocked, defaultDisplayContentLocked.mRealDisplayMetrics, split, this.mTransaction);
                            this.mTransaction.apply();
                        }
                        dataInputStream2.close();
                    } catch (FileNotFoundException unused) {
                        dataInputStream = dataInputStream2;
                        if (dataInputStream == null) {
                            if (fileInputStream == null) {
                                return;
                            }
                            fileInputStream.close();
                        }
                        dataInputStream.close();
                    } catch (IOException unused2) {
                        dataInputStream = dataInputStream2;
                        if (dataInputStream == null) {
                            if (fileInputStream == null) {
                                return;
                            }
                            fileInputStream.close();
                        }
                        dataInputStream.close();
                    } catch (Throwable th) {
                        th = th;
                        dataInputStream = dataInputStream2;
                        if (dataInputStream != null) {
                            dataInputStream.close();
                        } else {
                            if (fileInputStream != null) {
                                fileInputStream.close();
                            }
                            throw th;
                        }
                        throw th;
                    }
                } catch (FileNotFoundException unused3) {
                } catch (IOException unused4) {
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException unused5) {
            }
        } catch (FileNotFoundException unused6) {
            fileInputStream = null;
        } catch (IOException unused7) {
            fileInputStream = null;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }

    public void setRecentsVisibility(boolean z) {
        if (!checkCallingPermission("android.permission.STATUS_BAR", "setRecentsVisibility()")) {
            throw new SecurityException("Requires STATUS_BAR permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mPolicy.setRecentsVisibilityLw(z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void hideTransientBars(int i) {
        if (!checkCallingPermission("android.permission.STATUS_BAR", "hideTransientBars()")) {
            throw new SecurityException("Requires STATUS_BAR permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.getInsetsPolicy().hideTransient();
                } else {
                    Slog.w(StartingSurfaceController.TAG, "hideTransientBars with invalid displayId=" + i);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void updateStaticPrivacyIndicatorBounds(int i, Rect[] rectArr) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.updatePrivacyIndicatorBounds(rectArr);
                } else {
                    Slog.w(StartingSurfaceController.TAG, "updateStaticPrivacyIndicatorBounds with invalid displayId=" + i);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean z) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") != 0) {
            throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mPolicy.setNavBarVirtualKeyHapticFeedbackEnabledLw(z);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void createInputConsumer(IBinder iBinder, String str, int i, InputChannel inputChannel) {
        if (!this.mAtmService.isCallerRecents(Binder.getCallingUid()) && this.mContext.checkCallingOrSelfPermission("android.permission.INPUT_CONSUMER") != 0) {
            throw new SecurityException("createInputConsumer requires INPUT_CONSUMER permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.getInputMonitor().createInputConsumer(iBinder, str, inputChannel, Binder.getCallingPid(), Binder.getCallingUserHandle());
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean destroyInputConsumer(String str, int i) {
        if (!this.mAtmService.isCallerRecents(Binder.getCallingUid()) && this.mContext.checkCallingOrSelfPermission("android.permission.INPUT_CONSUMER") != 0) {
            throw new SecurityException("destroyInputConsumer requires INPUT_CONSUMER permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean destroyInputConsumer = displayContent.getInputMonitor().destroyInputConsumer(str);
                resetPriorityAfterLockedSection();
                return destroyInputConsumer;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public Region getCurrentImeTouchRegion() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.RESTRICTED_VR_ACCESS") != 0) {
            throw new SecurityException("getCurrentImeTouchRegion is restricted to VR services");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                Region region = new Region();
                for (int size = this.mRoot.mChildren.size() - 1; size >= 0; size--) {
                    WindowState windowState = ((DisplayContent) this.mRoot.mChildren.get(size)).mInputMethodWindow;
                    if (windowState != null) {
                        windowState.getTouchableRegion(region);
                        resetPriorityAfterLockedSection();
                        return region;
                    }
                }
                resetPriorityAfterLockedSection();
                return region;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean hasNavigationBar(int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean hasNavigationBar = displayContent.getDisplayPolicy().hasNavigationBar();
                resetPriorityAfterLockedSection();
                return hasNavigationBar;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void lockNow(Bundle bundle) {
        this.mPolicy.lockNow(bundle);
    }

    public boolean isSafeModeEnabled() {
        return this.mSafeMode;
    }

    public boolean clearWindowContentFrameStats(IBinder iBinder) {
        if (!checkCallingPermission("android.permission.FRAME_STATS", "clearWindowContentFrameStats()")) {
            throw new SecurityException("Requires FRAME_STATS permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowState = this.mWindowMap.get(iBinder);
                if (windowState == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                WindowSurfaceController windowSurfaceController = windowState.mWinAnimator.mSurfaceController;
                if (windowSurfaceController == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean clearWindowContentFrameStats = windowSurfaceController.clearWindowContentFrameStats();
                resetPriorityAfterLockedSection();
                return clearWindowContentFrameStats;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public WindowContentFrameStats getWindowContentFrameStats(IBinder iBinder) {
        if (!checkCallingPermission("android.permission.FRAME_STATS", "getWindowContentFrameStats()")) {
            throw new SecurityException("Requires FRAME_STATS permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowState = this.mWindowMap.get(iBinder);
                if (windowState == null) {
                    resetPriorityAfterLockedSection();
                    return null;
                }
                WindowSurfaceController windowSurfaceController = windowState.mWinAnimator.mSurfaceController;
                if (windowSurfaceController == null) {
                    resetPriorityAfterLockedSection();
                    return null;
                }
                if (this.mTempWindowRenderStats == null) {
                    this.mTempWindowRenderStats = new WindowContentFrameStats();
                }
                WindowContentFrameStats windowContentFrameStats = this.mTempWindowRenderStats;
                if (windowSurfaceController.getWindowContentFrameStats(windowContentFrameStats)) {
                    resetPriorityAfterLockedSection();
                    return windowContentFrameStats;
                }
                resetPriorityAfterLockedSection();
                return null;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void dumpPolicyLocked(PrintWriter printWriter, String[] strArr) {
        printWriter.println("WINDOW MANAGER POLICY STATE (dumpsys window policy)");
        this.mPolicy.dump("    ", printWriter, strArr);
    }

    public final void dumpAnimatorLocked(PrintWriter printWriter, boolean z) {
        printWriter.println("WINDOW MANAGER ANIMATOR STATE (dumpsys window animator)");
        this.mAnimator.dumpLocked(printWriter, "    ", z);
    }

    public final void dumpTokensLocked(PrintWriter printWriter, boolean z) {
        printWriter.println("WINDOW MANAGER TOKENS (dumpsys window tokens)");
        this.mRoot.dumpTokens(printWriter, z);
    }

    public final void dumpHighRefreshRateBlacklist(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER HIGH REFRESH RATE BLACKLIST (dumpsys window refresh)");
        this.mHighRefreshRateDenylist.dump(printWriter);
    }

    public final void dumpTraceStatus(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER TRACE (dumpsys window trace)");
        printWriter.print(this.mWindowTracing.getStatus() + "\n");
    }

    public final void dumpLogStatus(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER LOGGING (dumpsys window logging)");
        printWriter.println(ProtoLogImpl.getSingleInstance().getStatus());
    }

    public final void dumpSessionsLocked(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER SESSIONS (dumpsys window sessions)");
        for (int i = 0; i < this.mSessions.size(); i++) {
            Session valueAt = this.mSessions.valueAt(i);
            printWriter.print("  Session ");
            printWriter.print(valueAt);
            printWriter.println(':');
            valueAt.dump(printWriter, "    ");
        }
    }

    public void dumpDebugLocked(ProtoOutputStream protoOutputStream, int i) {
        this.mPolicy.dumpDebug(protoOutputStream, 1146756268033L);
        this.mRoot.dumpDebug(protoOutputStream, 1146756268034L, i);
        DisplayContent topFocusedDisplayContent = this.mRoot.getTopFocusedDisplayContent();
        WindowState windowState = topFocusedDisplayContent.mCurrentFocus;
        if (windowState != null) {
            windowState.writeIdentifierToProto(protoOutputStream, 1146756268035L);
        }
        ActivityRecord activityRecord = topFocusedDisplayContent.mFocusedApp;
        if (activityRecord != null) {
            activityRecord.writeNameToProto(protoOutputStream, 1138166333444L);
        }
        WindowState currentInputMethodWindow = this.mRoot.getCurrentInputMethodWindow();
        if (currentInputMethodWindow != null) {
            currentInputMethodWindow.writeIdentifierToProto(protoOutputStream, 1146756268037L);
        }
        protoOutputStream.write(1133871366150L, this.mDisplayFrozen);
        protoOutputStream.write(1120986464265L, topFocusedDisplayContent.getDisplayId());
        protoOutputStream.write(1133871366154L, this.mHardKeyboardAvailable);
        protoOutputStream.write(1133871366155L, true);
        this.mAtmService.mBackNavigationController.dumpDebug(protoOutputStream, 1146756268044L);
    }

    public final void dumpWindowsLocked(PrintWriter printWriter, boolean z, ArrayList<WindowState> arrayList) {
        printWriter.println("WINDOW MANAGER WINDOWS (dumpsys window windows)");
        dumpWindowsNoHeaderLocked(printWriter, z, arrayList);
    }

    public final void dumpWindowsNoHeaderLocked(final PrintWriter printWriter, boolean z, ArrayList<WindowState> arrayList) {
        this.mRoot.dumpWindowsNoHeader(printWriter, z, arrayList);
        if (!this.mHidingNonSystemOverlayWindows.isEmpty()) {
            printWriter.println();
            printWriter.println("  Hiding System Alert Windows:");
            for (int size = this.mHidingNonSystemOverlayWindows.size() - 1; size >= 0; size--) {
                WindowState windowState = this.mHidingNonSystemOverlayWindows.get(size);
                printWriter.print("  #");
                printWriter.print(size);
                printWriter.print(' ');
                printWriter.print(windowState);
                if (z) {
                    printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                    windowState.dump(printWriter, "    ", true);
                } else {
                    printWriter.println();
                }
            }
        }
        ArrayList<WindowState> arrayList2 = this.mForceRemoves;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            printWriter.println();
            printWriter.println("  Windows force removing:");
            for (int size2 = this.mForceRemoves.size() - 1; size2 >= 0; size2--) {
                WindowState windowState2 = this.mForceRemoves.get(size2);
                printWriter.print("  Removing #");
                printWriter.print(size2);
                printWriter.print(' ');
                printWriter.print(windowState2);
                if (z) {
                    printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                    windowState2.dump(printWriter, "    ", true);
                } else {
                    printWriter.println();
                }
            }
        }
        if (!this.mDestroySurface.isEmpty()) {
            printWriter.println();
            printWriter.println("  Windows waiting to destroy their surface:");
            for (int size3 = this.mDestroySurface.size() - 1; size3 >= 0; size3--) {
                WindowState windowState3 = this.mDestroySurface.get(size3);
                if (arrayList == null || arrayList.contains(windowState3)) {
                    printWriter.print("  Destroy #");
                    printWriter.print(size3);
                    printWriter.print(' ');
                    printWriter.print(windowState3);
                    if (z) {
                        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        windowState3.dump(printWriter, "    ", true);
                    } else {
                        printWriter.println();
                    }
                }
            }
        }
        if (!this.mResizingWindows.isEmpty()) {
            printWriter.println();
            printWriter.println("  Windows waiting to resize:");
            for (int size4 = this.mResizingWindows.size() - 1; size4 >= 0; size4--) {
                WindowState windowState4 = this.mResizingWindows.get(size4);
                if (arrayList == null || arrayList.contains(windowState4)) {
                    printWriter.print("  Resizing #");
                    printWriter.print(size4);
                    printWriter.print(' ');
                    printWriter.print(windowState4);
                    if (z) {
                        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        windowState4.dump(printWriter, "    ", true);
                    } else {
                        printWriter.println();
                    }
                }
            }
        }
        if (!this.mWaitingForDrawnCallbacks.isEmpty()) {
            printWriter.println();
            printWriter.println("  Clients waiting for these windows to be drawn:");
            this.mWaitingForDrawnCallbacks.forEach(new BiConsumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda23
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    WindowManagerService.lambda$dumpWindowsNoHeaderLocked$10(printWriter, (WindowContainer) obj, (Runnable) obj2);
                }
            });
        }
        printWriter.println();
        printWriter.print("  mGlobalConfiguration=");
        printWriter.println(this.mRoot.getConfiguration());
        printWriter.print("  mHasPermanentDpad=");
        printWriter.println(this.mHasPermanentDpad);
        this.mRoot.dumpTopFocusedDisplayId(printWriter);
        this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda24
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WindowManagerService.lambda$dumpWindowsNoHeaderLocked$11(printWriter, (DisplayContent) obj);
            }
        });
        printWriter.print("  mBlurEnabled=");
        printWriter.println(this.mBlurController.getBlurEnabled());
        printWriter.print("  mLastDisplayFreezeDuration=");
        TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, printWriter);
        if (this.mLastFinishedFreezeSource != null) {
            printWriter.print(" due to ");
            printWriter.print(this.mLastFinishedFreezeSource);
        }
        printWriter.println();
        this.mInputManagerCallback.dump(printWriter, "  ");
        this.mTaskSnapshotController.dump(printWriter, "  ");
        if (this.mAccessibilityController.hasCallbacks()) {
            this.mAccessibilityController.dump(printWriter, "  ");
        }
        if (z) {
            Object currentInputMethodWindow = this.mRoot.getCurrentInputMethodWindow();
            if (currentInputMethodWindow != null) {
                printWriter.print("  mInputMethodWindow=");
                printWriter.println(currentInputMethodWindow);
            }
            this.mWindowPlacerLocked.dump(printWriter, "  ");
            printWriter.print("  mSystemBooted=");
            printWriter.print(this.mSystemBooted);
            printWriter.print(" mDisplayEnabled=");
            printWriter.println(this.mDisplayEnabled);
            this.mRoot.dumpLayoutNeededDisplayIds(printWriter);
            printWriter.print("  mTransactionSequence=");
            printWriter.println(this.mTransactionSequence);
            printWriter.print("  mDisplayFrozen=");
            printWriter.print(this.mDisplayFrozen);
            printWriter.print(" windows=");
            printWriter.print(this.mWindowsFreezingScreen);
            printWriter.print(" client=");
            printWriter.print(this.mClientFreezingScreen);
            printWriter.print(" apps=");
            printWriter.println(this.mAppsFreezingScreen);
            DisplayContent defaultDisplayContentLocked = getDefaultDisplayContentLocked();
            printWriter.print("  mRotation=");
            printWriter.println(defaultDisplayContentLocked.getRotation());
            printWriter.print("  mLastOrientation=");
            printWriter.println(defaultDisplayContentLocked.getLastOrientation());
            printWriter.print("  mWaitingForConfig=");
            printWriter.println(defaultDisplayContentLocked.mWaitingForConfig);
            this.mRotationWatcherController.dump(printWriter);
            printWriter.print("  Animation settings: disabled=");
            printWriter.print(this.mAnimationsDisabled);
            printWriter.print(" window=");
            printWriter.print(this.mWindowAnimationScaleSetting);
            printWriter.print(" transition=");
            printWriter.print(this.mTransitionAnimationScaleSetting);
            printWriter.print(" animator=");
            printWriter.println(this.mAnimatorDurationScaleSetting);
            if (this.mRecentsAnimationController != null) {
                printWriter.print("  mRecentsAnimationController=");
                printWriter.println(this.mRecentsAnimationController);
                this.mRecentsAnimationController.dump(printWriter, "    ");
            }
        }
    }

    public static /* synthetic */ void lambda$dumpWindowsNoHeaderLocked$10(PrintWriter printWriter, WindowContainer windowContainer, Runnable runnable) {
        printWriter.print("  WindowContainer ");
        printWriter.println(windowContainer.getName());
        for (int size = windowContainer.mWaitingForDrawn.size() - 1; size >= 0; size--) {
            printWriter.print("  Waiting #");
            printWriter.print(size);
            printWriter.print(' ');
            printWriter.print(windowContainer.mWaitingForDrawn.get(size));
        }
    }

    public static /* synthetic */ void lambda$dumpWindowsNoHeaderLocked$11(PrintWriter printWriter, DisplayContent displayContent) {
        int displayId = displayContent.getDisplayId();
        InsetsControlTarget imeTarget = displayContent.getImeTarget(0);
        InputTarget imeInputTarget = displayContent.getImeInputTarget();
        InsetsControlTarget imeTarget2 = displayContent.getImeTarget(2);
        if (imeTarget != null) {
            printWriter.print("  imeLayeringTarget in display# ");
            printWriter.print(displayId);
            printWriter.print(' ');
            printWriter.println(imeTarget);
        }
        if (imeInputTarget != null) {
            printWriter.print("  imeInputTarget in display# ");
            printWriter.print(displayId);
            printWriter.print(' ');
            printWriter.println(imeInputTarget);
        }
        if (imeTarget2 != null) {
            printWriter.print("  imeControlTarget in display# ");
            printWriter.print(displayId);
            printWriter.print(' ');
            printWriter.println(imeTarget2);
        }
        printWriter.print("  Minimum task size of display#");
        printWriter.print(displayId);
        printWriter.print(' ');
        printWriter.print(displayContent.mMinSizeOfResizeableTaskDp);
    }

    public final boolean dumpWindows(PrintWriter printWriter, String str, boolean z) {
        final ArrayList<WindowState> arrayList = new ArrayList<>();
        if ("apps".equals(str) || "visible".equals(str) || "visible-apps".equals(str)) {
            final boolean contains = str.contains("apps");
            final boolean contains2 = str.contains("visible");
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    if (contains) {
                        this.mRoot.dumpDisplayContents(printWriter);
                    }
                    this.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda27
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.lambda$dumpWindows$12(contains2, contains, arrayList, (WindowState) obj);
                        }
                    }, true);
                } finally {
                    resetPriorityAfterLockedSection();
                }
            }
            resetPriorityAfterLockedSection();
        } else {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.getWindowsByName(arrayList, str);
                } finally {
                }
            }
            resetPriorityAfterLockedSection();
        }
        if (arrayList.isEmpty()) {
            return false;
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                dumpWindowsLocked(printWriter, z, arrayList);
            } finally {
            }
        }
        resetPriorityAfterLockedSection();
        return true;
    }

    public static /* synthetic */ void lambda$dumpWindows$12(boolean z, boolean z2, ArrayList arrayList, WindowState windowState) {
        if (!z || windowState.isVisible()) {
            if (z2 && windowState.mActivityRecord == null) {
                return;
            }
            arrayList.add(windowState);
        }
    }

    public final void dumpLastANRLocked(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER LAST ANR (dumpsys window lastanr)");
        String str = this.mLastANRState;
        if (str == null) {
            printWriter.println("  <no ANR has occurred since boot>");
        } else {
            printWriter.println(str);
        }
    }

    public void saveANRStateLocked(ActivityRecord activityRecord, WindowState windowState, String str) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 1024);
        fastPrintWriter.println("  ANR time: " + DateFormat.getDateTimeInstance().format(new Date()));
        if (activityRecord != null) {
            fastPrintWriter.println("  Application at fault: " + activityRecord.stringName);
        }
        if (windowState != null) {
            fastPrintWriter.println("  Window at fault: " + ((Object) windowState.mAttrs.getTitle()));
        }
        if (str != null) {
            fastPrintWriter.println("  Reason: " + str);
        }
        for (int childCount = this.mRoot.getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = this.mRoot.getChildAt(childCount);
            int displayId = childAt.getDisplayId();
            if (!childAt.mWinAddedSinceNullFocus.isEmpty()) {
                fastPrintWriter.println("  Windows added in display #" + displayId + " since null focus: " + childAt.mWinAddedSinceNullFocus);
            }
            if (!childAt.mWinRemovedSinceNullFocus.isEmpty()) {
                fastPrintWriter.println("  Windows removed in display #" + displayId + " since null focus: " + childAt.mWinRemovedSinceNullFocus);
            }
        }
        fastPrintWriter.println();
        dumpWindowsNoHeaderLocked(fastPrintWriter, true, null);
        fastPrintWriter.println();
        fastPrintWriter.println("Last ANR continued");
        this.mRoot.dumpDisplayContents(fastPrintWriter);
        fastPrintWriter.close();
        this.mLastANRState = stringWriter.toString();
        this.f1164mH.removeMessages(38);
        this.f1164mH.sendEmptyMessageDelayed(38, 7200000L);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PriorityDump.dump(this.mPriorityDumper, fileDescriptor, printWriter, strArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x00c1, code lost:
        r9 = new android.util.proto.ProtoOutputStream(r8);
        r8 = r7.mGlobalLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00c8, code lost:
        monitor-enter(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00c9, code lost:
        boostPriorityForLockedSection();
        dumpDebugLocked(r9, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00cf, code lost:
        monitor-exit(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00d0, code lost:
        resetPriorityAfterLockedSection();
        r9.flush();
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00d6, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00d7, code lost:
        r7 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00dc, code lost:
        throw r7;
     */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void doDump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr, boolean z) {
        String str;
        if (DumpUtils.checkDumpPermission(this.mContext, StartingSurfaceController.TAG, printWriter)) {
            int i = 0;
            boolean z2 = false;
            while (i < strArr.length && (str = strArr[i]) != null && str.length() > 0 && str.charAt(0) == '-') {
                i++;
                if ("-a".equals(str)) {
                    z2 = true;
                } else if ("-h".equals(str)) {
                    printWriter.println("Window manager dump options:");
                    printWriter.println("  [-a] [-h] [cmd] ...");
                    printWriter.println("  cmd may be one of:");
                    printWriter.println("    l[astanr]: last ANR information");
                    printWriter.println("    p[policy]: policy state");
                    printWriter.println("    a[animator]: animator state");
                    printWriter.println("    s[essions]: active sessions");
                    printWriter.println("    surfaces: active surfaces (debugging enabled only)");
                    printWriter.println("    d[isplays]: active display contents");
                    printWriter.println("    t[okens]: token list");
                    printWriter.println("    w[indows]: window list");
                    printWriter.println("    package-config: installed packages having app-specific config");
                    printWriter.println("    trace: print trace status and write Winscope trace to file");
                    printWriter.println("  cmd may also be a NAME to dump windows.  NAME may");
                    printWriter.println("    be a partial substring in a window name, a");
                    printWriter.println("    Window hex object identifier, or");
                    printWriter.println("    \"all\" for all windows, or");
                    printWriter.println("    \"visible\" for the visible windows.");
                    printWriter.println("    \"visible-apps\" for the visible app windows.");
                    printWriter.println("  -a: include all available server state.");
                    printWriter.println("  --proto: output dump in protocol buffer format.");
                    return;
                } else {
                    printWriter.println("Unknown argument: " + str + "; use -h for help");
                }
            }
            if (i < strArr.length) {
                String str2 = strArr[i];
                if ("lastanr".equals(str2) || "l".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpLastANRLocked(printWriter);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("policy".equals(str2) || "p".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpPolicyLocked(printWriter, strArr);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("animator".equals(str2) || "a".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpAnimatorLocked(printWriter, true);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("sessions".equals(str2) || "s".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpSessionsLocked(printWriter);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("displays".equals(str2) || "d".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            this.mRoot.dumpDisplayContents(printWriter);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("tokens".equals(str2) || "t".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpTokensLocked(printWriter, true);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("windows".equals(str2) || "w".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpWindowsLocked(printWriter, true, null);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("all".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpWindowsLocked(printWriter, true, null);
                        } finally {
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("containers".equals(str2)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            this.mRoot.dumpChildrenNames(printWriter, " ");
                            printWriter.println(" ");
                            this.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda26
                                @Override // java.util.function.Consumer
                                public final void accept(Object obj) {
                                    printWriter.println((WindowState) obj);
                                }
                            }, true);
                        } finally {
                            resetPriorityAfterLockedSection();
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return;
                } else if ("trace".equals(str2)) {
                    dumpTraceStatus(printWriter);
                    return;
                } else if ("logging".equals(str2)) {
                    dumpLogStatus(printWriter);
                    return;
                } else if ("refresh".equals(str2)) {
                    dumpHighRefreshRateBlacklist(printWriter);
                    return;
                } else if ("constants".equals(str2)) {
                    this.mConstants.dump(printWriter);
                    return;
                } else if ("package-config".equals(str2)) {
                    this.mAtmService.dumpInstalledPackagesConfig(printWriter);
                    return;
                } else if (dumpWindows(printWriter, str2, z2)) {
                    return;
                } else {
                    printWriter.println("Bad window command, or no windows match: " + str2);
                    printWriter.println("Use -h for help.");
                    return;
                }
            }
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpLastANRLocked(printWriter);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpPolicyLocked(printWriter, strArr);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpAnimatorLocked(printWriter, z2);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpSessionsLocked(printWriter);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mRoot.dumpDisplayContents(printWriter);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpTokensLocked(printWriter, z2);
                    printWriter.println();
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpWindowsLocked(printWriter, z2, null);
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpTraceStatus(printWriter);
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpLogStatus(printWriter);
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    dumpHighRefreshRateBlacklist(printWriter);
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mAtmService.dumpInstalledPackagesConfig(printWriter);
                    if (z2) {
                        printWriter.println("-------------------------------------------------------------------------------");
                    }
                    this.mConstants.dump(printWriter);
                } finally {
                    resetPriorityAfterLockedSection();
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public DisplayContent getDefaultDisplayContentLocked() {
        return this.mRoot.getDisplayContent(0);
    }

    public void onOverlayChanged() {
        this.f1164mH.post(new Runnable() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerService.this.lambda$onOverlayChanged$15();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onOverlayChanged$15() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mAtmService.deferWindowLayout();
                this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda25
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WindowManagerService.lambda$onOverlayChanged$14((DisplayContent) obj);
                    }
                });
                this.mAtmService.continueWindowLayout();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public static /* synthetic */ void lambda$onOverlayChanged$14(DisplayContent displayContent) {
        displayContent.getDisplayPolicy().onOverlayChanged();
    }

    public Object getWindowManagerLock() {
        return this.mGlobalLock;
    }

    public void setWillReplaceWindow(IBinder iBinder, boolean z) {
        ActivityRecord activityRecord = this.mRoot.getActivityRecord(iBinder);
        if (activityRecord == null) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1661704580, 0, "Attempted to set replacing window on non-existing app token %s", new Object[]{String.valueOf(iBinder)});
            }
        } else if (!activityRecord.hasContentToDisplay()) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1270731689, 0, "Attempted to set replacing window on app token with no content %s", new Object[]{String.valueOf(iBinder)});
            }
        } else {
            activityRecord.setWillReplaceWindows(z);
        }
    }

    public void setWillReplaceWindows(IBinder iBinder, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                ActivityRecord activityRecord = this.mRoot.getActivityRecord(iBinder);
                if (activityRecord == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1661704580, 0, "Attempted to set replacing window on non-existing app token %s", new Object[]{String.valueOf(iBinder)});
                    }
                    resetPriorityAfterLockedSection();
                } else if (!activityRecord.hasContentToDisplay()) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1270731689, 0, "Attempted to set replacing window on app token with no content %s", new Object[]{String.valueOf(iBinder)});
                    }
                    resetPriorityAfterLockedSection();
                } else {
                    if (z) {
                        activityRecord.setWillReplaceChildWindows();
                    } else {
                        activityRecord.setWillReplaceWindows(false);
                    }
                    scheduleClearWillReplaceWindows(iBinder, true);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void scheduleClearWillReplaceWindows(IBinder iBinder, boolean z) {
        ActivityRecord activityRecord = this.mRoot.getActivityRecord(iBinder);
        if (activityRecord == null) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 38267433, 0, "Attempted to reset replacing window on non-existing app token %s", new Object[]{String.valueOf(iBinder)});
            }
        } else if (z) {
            scheduleWindowReplacementTimeouts(activityRecord);
        } else {
            activityRecord.clearWillReplaceWindows();
        }
    }

    public void scheduleWindowReplacementTimeouts(ActivityRecord activityRecord) {
        if (!this.mWindowReplacementTimeouts.contains(activityRecord)) {
            this.mWindowReplacementTimeouts.add(activityRecord);
        }
        this.f1164mH.removeMessages(46);
        this.f1164mH.sendEmptyMessageDelayed(46, 2000L);
    }

    public void setForceDesktopModeOnExternalDisplays(boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mForceDesktopModeOnExternalDisplays = z;
                this.mRoot.updateDisplayImePolicyCache();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    @VisibleForTesting
    public void setIsPc(boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mIsPc = z;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public static int dipToPixel(int i, DisplayMetrics displayMetrics) {
        return (int) TypedValue.applyDimension(1, i, displayMetrics);
    }

    public void registerPinnedTaskListener(int i, IPinnedTaskListener iPinnedTaskListener) {
        if (checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerPinnedTaskListener()") && this.mAtmService.mSupportsPictureInPicture) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.getDisplayContent(i).getPinnedTaskController().registerPinnedTaskListener(iPinnedTaskListener);
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public void requestAppKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
        try {
            WindowState focusedWindow = getFocusedWindow();
            if (focusedWindow == null || focusedWindow.mClient == null) {
                return;
            }
            getFocusedWindow().mClient.requestAppKeyboardShortcuts(iResultReceiver, i);
        } catch (RemoteException unused) {
        }
    }

    public void getStableInsets(int i, Rect rect) throws RemoteException {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getStableInsetsLocked(i, rect);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void getStableInsetsLocked(int i, Rect rect) {
        rect.setEmpty();
        DisplayContent displayContent = this.mRoot.getDisplayContent(i);
        if (displayContent != null) {
            DisplayInfo displayInfo = displayContent.getDisplayInfo();
            rect.set(displayContent.getDisplayPolicy().getDecorInsetsInfo(displayInfo.rotation, displayInfo.logicalWidth, displayInfo.logicalHeight).mConfigInsets);
        }
    }

    /* renamed from: com.android.server.wm.WindowManagerService$MousePositionTracker */
    /* loaded from: classes2.dex */
    public static class MousePositionTracker implements WindowManagerPolicyConstants.PointerEventListener {
        public boolean mLatestEventWasMouse;
        public float mLatestMouseX;
        public float mLatestMouseY;
        public int mPointerDisplayId;

        public MousePositionTracker() {
            this.mPointerDisplayId = -1;
        }

        public boolean updatePosition(int i, float f, float f2) {
            synchronized (this) {
                this.mLatestEventWasMouse = true;
                if (i != this.mPointerDisplayId) {
                    return false;
                }
                this.mLatestMouseX = f;
                this.mLatestMouseY = f2;
                return true;
            }
        }

        public void setPointerDisplayId(int i) {
            synchronized (this) {
                this.mPointerDisplayId = i;
            }
        }

        public void onPointerEvent(MotionEvent motionEvent) {
            if (motionEvent.isFromSource(8194)) {
                updatePosition(motionEvent.getDisplayId(), motionEvent.getRawX(), motionEvent.getRawY());
                return;
            }
            synchronized (this) {
                this.mLatestEventWasMouse = false;
            }
        }
    }

    public void updatePointerIcon(IWindow iWindow) {
        synchronized (this.mMousePositionTracker) {
            if (this.mMousePositionTracker.mLatestEventWasMouse) {
                float f = this.mMousePositionTracker.mLatestMouseX;
                float f2 = this.mMousePositionTracker.mLatestMouseY;
                int i = this.mMousePositionTracker.mPointerDisplayId;
                synchronized (this.mGlobalLock) {
                    try {
                        boostPriorityForLockedSection();
                        if (this.mDragDropController.dragDropActiveLocked()) {
                            resetPriorityAfterLockedSection();
                            return;
                        }
                        WindowState windowForClientLocked = windowForClientLocked((Session) null, iWindow, false);
                        if (windowForClientLocked == null) {
                            if (ProtoLogCache.WM_ERROR_enabled) {
                                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1325649102, 0, "Bad requesting window %s", new Object[]{String.valueOf(iWindow)});
                            }
                            resetPriorityAfterLockedSection();
                            return;
                        }
                        DisplayContent displayContent = windowForClientLocked.getDisplayContent();
                        if (displayContent == null) {
                            resetPriorityAfterLockedSection();
                        } else if (i != displayContent.getDisplayId()) {
                            resetPriorityAfterLockedSection();
                        } else {
                            WindowState touchableWinAtPointLocked = displayContent.getTouchableWinAtPointLocked(f, f2);
                            if (touchableWinAtPointLocked != windowForClientLocked) {
                                resetPriorityAfterLockedSection();
                                return;
                            }
                            try {
                                touchableWinAtPointLocked.mClient.updatePointerIcon(touchableWinAtPointLocked.translateToWindowX(f), touchableWinAtPointLocked.translateToWindowY(f2));
                            } catch (RemoteException unused) {
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -393505149, 0, "unable to update pointer icon", (Object[]) null);
                                }
                            }
                            resetPriorityAfterLockedSection();
                        }
                    } catch (Throwable th) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }
    }

    public void restorePointerIconLocked(DisplayContent displayContent, float f, float f2) {
        if (this.mMousePositionTracker.updatePosition(displayContent.getDisplayId(), f, f2)) {
            WindowState touchableWinAtPointLocked = displayContent.getTouchableWinAtPointLocked(f, f2);
            if (touchableWinAtPointLocked != null) {
                try {
                    touchableWinAtPointLocked.mClient.updatePointerIcon(touchableWinAtPointLocked.translateToWindowX(f), touchableWinAtPointLocked.translateToWindowY(f2));
                    return;
                } catch (RemoteException unused) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1423418408, 0, "unable to restore pointer icon", (Object[]) null);
                        return;
                    }
                    return;
                }
            }
            ((InputManager) this.mContext.getSystemService(InputManager.class)).setPointerIconType(1000);
        }
    }

    public PointF getLatestMousePosition() {
        PointF pointF;
        synchronized (this.mMousePositionTracker) {
            pointF = new PointF(this.mMousePositionTracker.mLatestMouseX, this.mMousePositionTracker.mLatestMouseY);
        }
        return pointF;
    }

    public void setMousePointerDisplayId(int i) {
        this.mMousePositionTracker.setPointerDisplayId(i);
    }

    public void updateTapExcludeRegion(IWindow iWindow, Region region) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked((Session) null, iWindow, false);
                if (windowForClientLocked == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1325649102, 0, "Bad requesting window %s", new Object[]{String.valueOf(iWindow)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                windowForClientLocked.updateTapExcludeRegion(region);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void requestScrollCapture(int i, IBinder iBinder, int i2, IScrollCaptureResponseListener iScrollCaptureResponseListener) {
        ScrollCaptureResponse.Builder builder;
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "requestScrollCapture()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                builder = new ScrollCaptureResponse.Builder();
            } catch (RemoteException e) {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1046922686, 0, "requestScrollCapture: caught exception dispatching callback: %s", new Object[]{String.valueOf(e)});
                }
            }
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                    if (displayContent == null) {
                        if (ProtoLogCache.WM_ERROR_enabled) {
                            ProtoLogImpl.e(ProtoLogGroup.WM_ERROR, 646981048, 1, "Invalid displayId for requestScrollCapture: %d", new Object[]{Long.valueOf(i)});
                        }
                        builder.setDescription(String.format("bad displayId: %d", Integer.valueOf(i)));
                        iScrollCaptureResponseListener.onScrollCaptureResponse(builder.build());
                    } else {
                        WindowState findScrollCaptureTargetWindow = displayContent.findScrollCaptureTargetWindow(iBinder != null ? windowForClientLocked((Session) null, iBinder, false) : null, i2);
                        if (findScrollCaptureTargetWindow == null) {
                            builder.setDescription("findScrollCaptureTargetWindow returned null");
                            iScrollCaptureResponseListener.onScrollCaptureResponse(builder.build());
                        } else {
                            try {
                                findScrollCaptureTargetWindow.mClient.requestScrollCapture(iScrollCaptureResponseListener);
                            } catch (RemoteException e2) {
                                if (ProtoLogCache.WM_ERROR_enabled) {
                                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1517908912, 0, "requestScrollCapture: caught exception dispatching to window.token=%s", new Object[]{String.valueOf(findScrollCaptureTargetWindow.mClient.asBinder())});
                                }
                                builder.setWindowTitle(findScrollCaptureTargetWindow.getName());
                                builder.setPackageName(findScrollCaptureTargetWindow.getOwningPackage());
                                builder.setDescription(String.format("caught exception: %s", e2));
                                iScrollCaptureResponseListener.onScrollCaptureResponse(builder.build());
                            }
                            resetPriorityAfterLockedSection();
                            return;
                        }
                    }
                    resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getWindowingMode(int i) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "getWindowingMode()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 51628177, 1, "Attempted to get windowing mode of a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return 0;
                }
                int windowingModeLocked = this.mDisplayWindowSettings.getWindowingModeLocked(displayContent);
                resetPriorityAfterLockedSection();
                return windowingModeLocked;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setWindowingMode(int i, int i2) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setWindowingMode()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -1838803135, 1, "Attempted to set windowing mode to a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                int windowingMode = displayContentOrCreate.getWindowingMode();
                this.mDisplayWindowSettings.setWindowingModeLocked(displayContentOrCreate, i2);
                displayContentOrCreate.reconfigureDisplayLocked();
                if (windowingMode != displayContentOrCreate.getWindowingMode()) {
                    displayContentOrCreate.sendNewConfiguration();
                    displayContentOrCreate.executeAppTransition();
                }
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @WindowManager.RemoveContentMode
    public int getRemoveContentMode(int i) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "getRemoveContentMode()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -496681057, 1, "Attempted to get remove mode of a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return 0;
                }
                int removeContentModeLocked = this.mDisplayWindowSettings.getRemoveContentModeLocked(displayContent);
                resetPriorityAfterLockedSection();
                return removeContentModeLocked;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setRemoveContentMode(int i, @WindowManager.RemoveContentMode int i2) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setRemoveContentMode()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 288485303, 1, "Attempted to set remove mode to a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mDisplayWindowSettings.setRemoveContentModeLocked(displayContentOrCreate, i2);
                displayContentOrCreate.reconfigureDisplayLocked();
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean shouldShowWithInsecureKeyguard(int i) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "shouldShowWithInsecureKeyguard()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1434383382, 1, "Attempted to get flag of a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean shouldShowWithInsecureKeyguardLocked = this.mDisplayWindowSettings.shouldShowWithInsecureKeyguardLocked(displayContent);
                resetPriorityAfterLockedSection();
                return shouldShowWithInsecureKeyguardLocked;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setShouldShowWithInsecureKeyguard(int i, boolean z) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setShouldShowWithInsecureKeyguard()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1521476038, 1, "Attempted to set flag to a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mDisplayWindowSettings.setShouldShowWithInsecureKeyguardLocked(displayContentOrCreate, z);
                displayContentOrCreate.reconfigureDisplayLocked();
                resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean shouldShowSystemDecors(int i) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "shouldShowSystemDecors()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 11060725, 1, "Attempted to get system decors flag of a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean supportsSystemDecorations = displayContent.supportsSystemDecorations();
                resetPriorityAfterLockedSection();
                return supportsSystemDecorations;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setShouldShowSystemDecors(int i, boolean z) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setShouldShowSystemDecors()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -386552155, 1, "Attempted to set system decors flag to a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                } else if (!displayContentOrCreate.isTrusted()) {
                    throw new SecurityException("Attempted to set system decors flag to an untrusted virtual display: " + i);
                } else {
                    this.mDisplayWindowSettings.setShouldShowSystemDecorsLocked(displayContentOrCreate, z);
                    displayContentOrCreate.reconfigureDisplayLocked();
                    resetPriorityAfterLockedSection();
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @WindowManager.DisplayImePolicy
    public int getDisplayImePolicy(int i) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "getDisplayImePolicy()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        Map<Integer, Integer> map = this.mDisplayImePolicyCache;
        if (!map.containsKey(Integer.valueOf(i))) {
            if (ProtoLogCache.WM_ERROR_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1100065297, 1, "Attempted to get IME policy of a display that does not exist: %d", new Object[]{Long.valueOf(i)});
            }
            return 1;
        }
        return map.get(Integer.valueOf(i)).intValue();
    }

    public void setDisplayImePolicy(int i, @WindowManager.DisplayImePolicy int i2) {
        if (!checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setDisplayImePolicy()")) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, null);
                if (displayContentOrCreate == null) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, -292790591, 1, "Attempted to set IME policy to a display that does not exist: %d", new Object[]{Long.valueOf(i)});
                    }
                    resetPriorityAfterLockedSection();
                } else if (!displayContentOrCreate.isTrusted()) {
                    throw new SecurityException("Attempted to set IME policy to an untrusted virtual display: " + i);
                } else {
                    this.mDisplayWindowSettings.setDisplayImePolicy(displayContentOrCreate, i2);
                    displayContentOrCreate.reconfigureDisplayLocked();
                    resetPriorityAfterLockedSection();
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerShortcutKey(long j, IShortcutService iShortcutService) throws RemoteException {
        if (!checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerShortcutKey")) {
            throw new SecurityException("Requires REGISTER_WINDOW_MANAGER_LISTENERS permission");
        }
        this.mPolicy.registerShortcutKey(j, iShortcutService);
    }

    /* renamed from: com.android.server.wm.WindowManagerService$LocalService */
    /* loaded from: classes2.dex */
    public final class LocalService extends WindowManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public WindowManagerInternal.AccessibilityControllerInternal getAccessibilityController() {
            return AccessibilityController.getAccessibilityControllerInternal(WindowManagerService.this);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void clearSnapshotCache() {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mTaskSnapshotController.clearSnapshotCache();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void requestTraversalFromDisplayManager() {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.requestTraversal();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setMagnificationSpec(int i, MagnificationSpec magnificationSpec) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController.hasCallbacks()) {
                        WindowManagerService.this.mAccessibilityController.setMagnificationSpec(i, magnificationSpec);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setForceShowMagnifiableBounds(int i, boolean z) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController.hasCallbacks()) {
                        WindowManagerService.this.mAccessibilityController.setForceShowMagnifiableBounds(i, z);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void getMagnificationRegion(int i, Region region) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController.hasCallbacks()) {
                        WindowManagerService.this.mAccessibilityController.getMagnificationRegion(i, region);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean setMagnificationCallbacks(int i, WindowManagerInternal.MagnificationCallbacks magnificationCallbacks) {
            boolean magnificationCallbacks2;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    magnificationCallbacks2 = WindowManagerService.this.mAccessibilityController.setMagnificationCallbacks(i, magnificationCallbacks);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return magnificationCallbacks2;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setWindowsForAccessibilityCallback(int i, WindowManagerInternal.WindowsForAccessibilityCallback windowsForAccessibilityCallback) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mAccessibilityController.setWindowsForAccessibilityCallback(i, windowsForAccessibilityCallback);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setInputFilter(IInputFilter iInputFilter) {
            WindowManagerService.this.mInputManager.setInputFilter(iInputFilter);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public IBinder getFocusedWindowToken() {
            IBinder focusedWindowToken;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    focusedWindowToken = WindowManagerService.this.mAccessibilityController.getFocusedWindowToken();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return focusedWindowToken;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public IBinder getFocusedWindowTokenFromWindowStates() {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState focusedWindowLocked = WindowManagerService.this.getFocusedWindowLocked();
                    if (focusedWindowLocked == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    IBinder asBinder = focusedWindowLocked.mClient.asBinder();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return asBinder;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isKeyguardLocked() {
            return WindowManagerService.this.isKeyguardLocked();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isKeyguardShowingAndNotOccluded() {
            return WindowManagerService.this.isKeyguardShowingAndNotOccluded();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isKeyguardSecure(int i) {
            return WindowManagerService.this.mPolicy.isKeyguardSecure(i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void showGlobalActions() {
            WindowManagerService.this.showGlobalActions();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void getWindowFrame(IBinder iBinder, Rect rect) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (windowState != null) {
                        rect.set(windowState.getFrame());
                    } else {
                        rect.setEmpty();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public Pair<Matrix, MagnificationSpec> getWindowTransformationMatrixAndMagnificationSpec(IBinder iBinder) {
            return WindowManagerService.this.mAccessibilityController.getWindowTransformationMatrixAndMagnificationSpec(iBinder);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void waitForAllWindowsDrawn(Runnable runnable, long j, int i) {
            boolean z;
            WindowContainer displayContent = i == -1 ? WindowManagerService.this.mRoot : WindowManagerService.this.mRoot.getDisplayContent(i);
            if (displayContent == null) {
                runnable.run();
                return;
            }
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    displayContent.waitForAllWindowsDrawn();
                    WindowManagerService.this.mWindowPlacerLocked.requestTraversal();
                    WindowManagerService.this.f1164mH.removeMessages(24, displayContent);
                    if (displayContent.mWaitingForDrawn.isEmpty()) {
                        z = true;
                    } else {
                        if (Trace.isTagEnabled(32L)) {
                            for (int i2 = 0; i2 < displayContent.mWaitingForDrawn.size(); i2++) {
                                WindowManagerService.this.traceStartWaitingForWindowDrawn(displayContent.mWaitingForDrawn.get(i2));
                            }
                        }
                        WindowManagerService.this.mWaitingForDrawnCallbacks.put(displayContent, runnable);
                        WindowManagerService.this.f1164mH.sendNewMessageDelayed(24, displayContent, j);
                        WindowManagerService.this.checkDrawnWindowsLocked();
                        z = false;
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (z) {
                runnable.run();
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setForcedDisplaySize(int i, int i2, int i3) {
            WindowManagerService.this.setForcedDisplaySize(i, i2, i3);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void clearForcedDisplaySize(int i) {
            WindowManagerService.this.clearForcedDisplaySize(i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void addWindowToken(IBinder iBinder, int i, int i2, Bundle bundle) {
            WindowManagerService.this.addWindowToken(iBinder, i, i2, bundle);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void removeWindowToken(IBinder iBinder, boolean z, boolean z2, int i) {
            WindowManagerService.this.removeWindowToken(iBinder, z, z2, i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void moveWindowTokenToDisplay(IBinder iBinder, int i) {
            WindowManagerService.this.moveWindowTokenToDisplay(iBinder, i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void registerAppTransitionListener(WindowManagerInternal.AppTransitionListener appTransitionListener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.getDefaultDisplayContentLocked().mAppTransition.registerListenerLocked(appTransitionListener);
                    WindowManagerService.this.mAtmService.getTransitionController().registerLegacyListener(appTransitionListener);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void registerTaskSystemBarsListener(WindowManagerInternal.TaskSystemBarsListener taskSystemBarsListener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mTaskSystemBarsListenerController.registerListener(taskSystemBarsListener);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void unregisterTaskSystemBarsListener(WindowManagerInternal.TaskSystemBarsListener taskSystemBarsListener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mTaskSystemBarsListenerController.unregisterListener(taskSystemBarsListener);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void registerKeyguardExitAnimationStartListener(WindowManagerInternal.KeyguardExitAnimationStartListener keyguardExitAnimationStartListener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.getDefaultDisplayContentLocked().mAppTransition.registerKeygaurdExitAnimationStartListener(keyguardExitAnimationStartListener);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void reportPasswordChanged(int i) {
            WindowManagerService.this.mKeyguardDisableHandler.updateKeyguardEnabled(i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public int getInputMethodWindowVisibleHeight(int i) {
            int inputMethodWindowVisibleHeight;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    inputMethodWindowVisibleHeight = WindowManagerService.this.mRoot.getDisplayContent(i).getInputMethodWindowVisibleHeight();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return inputMethodWindowVisibleHeight;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setDismissImeOnBackKeyPressed(boolean z) {
            WindowManagerService.this.mPolicy.setDismissImeOnBackKeyPressed(z);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void updateInputMethodTargetWindow(IBinder iBinder, IBinder iBinder2) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    InputTarget inputTargetFromWindowTokenLocked = WindowManagerService.this.getInputTargetFromWindowTokenLocked(iBinder2);
                    if (inputTargetFromWindowTokenLocked != null) {
                        inputTargetFromWindowTokenLocked.getDisplayContent().updateImeInputAndControlTarget(inputTargetFromWindowTokenLocked);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isHardKeyboardAvailable() {
            boolean z;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = WindowManagerService.this.mHardKeyboardAvailable;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setOnHardKeyboardStatusChangeListener(WindowManagerInternal.OnHardKeyboardStatusChangeListener onHardKeyboardStatusChangeListener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mHardKeyboardStatusChangeListener = onHardKeyboardStatusChangeListener;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void computeWindowsForAccessibility(int i) {
            WindowManagerService.this.mAccessibilityController.performComputeChangedWindowsNot(i, true);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setVr2dDisplayId(int i) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mVr2dDisplayId = i;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void registerDragDropControllerCallback(WindowManagerInternal.IDragDropCallback iDragDropCallback) {
            WindowManagerService.this.mDragDropController.registerCallback(iDragDropCallback);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void lockNow() {
            WindowManagerService.this.lockNow(null);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public int getWindowOwnerUserId(IBinder iBinder) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (windowState == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return -10000;
                    }
                    int i = windowState.mShowUserId;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return i;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setWallpaperShowWhenLocked(IBinder iBinder, boolean z) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowToken windowToken = WindowManagerService.this.mRoot.getWindowToken(iBinder);
                    if (windowToken != null && windowToken.asWallpaperToken() != null) {
                        windowToken.asWallpaperToken().setShowWhenLocked(z);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 2043434284, 0, "setWallpaperShowWhenLocked: non-existent wallpaper token: %s", new Object[]{String.valueOf(iBinder)});
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isUidFocused(int i) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    for (int childCount = WindowManagerService.this.mRoot.getChildCount() - 1; childCount >= 0; childCount--) {
                        WindowState windowState = WindowManagerService.this.mRoot.getChildAt(childCount).mCurrentFocus;
                        if (windowState != null && i == windowState.getOwningUid()) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return true;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public int hasInputMethodClientFocus(IBinder iBinder, int i, int i2, int i3) {
            if (i3 == -1) {
                return -3;
            }
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent topFocusedDisplayContent = WindowManagerService.this.mRoot.getTopFocusedDisplayContent();
                    InputTarget inputTargetFromWindowTokenLocked = WindowManagerService.this.getInputTargetFromWindowTokenLocked(iBinder);
                    if (inputTargetFromWindowTokenLocked == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return -1;
                    }
                    int displayId = inputTargetFromWindowTokenLocked.getDisplayContent().getDisplayId();
                    if (displayId != i3) {
                        Slog.e(StartingSurfaceController.TAG, "isInputMethodClientFocus: display ID mismatch. from client: " + i3 + " from window: " + displayId);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return -2;
                    }
                    if (topFocusedDisplayContent != null && topFocusedDisplayContent.getDisplayId() == i3 && topFocusedDisplayContent.hasAccess(i)) {
                        if (inputTargetFromWindowTokenLocked.isInputMethodClientFocus(i, i2)) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return 0;
                        }
                        WindowState windowState = topFocusedDisplayContent.mCurrentFocus;
                        if (windowState != null) {
                            Session session = windowState.mSession;
                            if (session.mUid == i && session.mPid == i2) {
                                int i4 = windowState.canBeImeTarget() ? 0 : -1;
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return i4;
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return -1;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -3;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void showImePostLayout(IBinder iBinder, ImeTracker.Token token) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    InputTarget inputTargetFromWindowTokenLocked = WindowManagerService.this.getInputTargetFromWindowTokenLocked(iBinder);
                    if (inputTargetFromWindowTokenLocked == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    Trace.asyncTraceBegin(32L, "WMS.showImePostLayout", 0);
                    InsetsControlTarget imeControlTarget = inputTargetFromWindowTokenLocked.getImeControlTarget();
                    WindowState window = imeControlTarget.getWindow();
                    (window != null ? window.getDisplayContent() : WindowManagerService.this.getDefaultDisplayContentLocked()).getInsetsStateController().getImeSourceProvider().scheduleShowImePostLayout(imeControlTarget, token);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void hideIme(IBinder iBinder, int i, ImeTracker.Token token) {
            Trace.traceBegin(32L, "WMS.hideIme");
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (ProtoLogCache.WM_DEBUG_IME_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_IME, 95216706, 0, (String) null, new Object[]{String.valueOf(windowState)});
                    }
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(i);
                    if (windowState != null) {
                        WindowState window = windowState.getImeControlTarget().getWindow();
                        if (window != null) {
                            displayContent = window.getDisplayContent();
                        }
                        displayContent.getInsetsStateController().getImeSourceProvider().abortShowImePostLayout();
                    }
                    if (displayContent != null && displayContent.getImeTarget(2) != null) {
                        ImeTracker.forLogging().onProgress(token, 20);
                        if (ProtoLogCache.WM_DEBUG_IME_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_IME, -547111355, 0, (String) null, new Object[]{String.valueOf(displayContent.getImeTarget(2))});
                        }
                        displayContent.getImeTarget(2).hideInsets(WindowInsets.Type.ime(), true, token);
                    } else {
                        ImeTracker.forLogging().onFailed(token, 20);
                    }
                    if (displayContent != null) {
                        displayContent.getInsetsStateController().getImeSourceProvider().setImeShowing(false);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Trace.traceEnd(32L);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isUidAllowedOnDisplay(int i, int i2) {
            boolean z = true;
            if (i == 0) {
                return true;
            }
            if (i == -1) {
                return false;
            }
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(i);
                    if (displayContent == null || !displayContent.hasAccess(i2)) {
                        z = false;
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public int getDisplayIdForWindow(IBinder iBinder) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (windowState == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return -1;
                    }
                    int displayId = windowState.getDisplayContent().getDisplayId();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return displayId;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public int getTopFocusedDisplayId() {
            int displayId;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    displayId = WindowManagerService.this.mRoot.getTopFocusedDisplayContent().getDisplayId();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return displayId;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public Context getTopFocusedDisplayUiContext() {
            Context displayUiContext;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    displayUiContext = WindowManagerService.this.mRoot.getTopFocusedDisplayContent().getDisplayUiContext();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return displayUiContext;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean shouldShowSystemDecorOnDisplay(int i) {
            boolean shouldShowSystemDecors;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    shouldShowSystemDecors = WindowManagerService.this.shouldShowSystemDecors(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return shouldShowSystemDecors;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        @WindowManager.DisplayImePolicy
        public int getDisplayImePolicy(int i) {
            return WindowManagerService.this.getDisplayImePolicy(i);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void addRefreshRateRangeForPackage(final String str, final float f, final float f2) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$LocalService$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.LocalService.lambda$addRefreshRateRangeForPackage$0(str, f, f2, (DisplayContent) obj);
                        }
                    });
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public static /* synthetic */ void lambda$addRefreshRateRangeForPackage$0(String str, float f, float f2, DisplayContent displayContent) {
            displayContent.getDisplayPolicy().getRefreshRatePolicy().addRefreshRateRangeForPackage(str, f, f2);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void removeRefreshRateRangeForPackage(final String str) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$LocalService$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.LocalService.lambda$removeRefreshRateRangeForPackage$1(str, (DisplayContent) obj);
                        }
                    });
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public static /* synthetic */ void lambda$removeRefreshRateRangeForPackage$1(String str, DisplayContent displayContent) {
            displayContent.getDisplayPolicy().getRefreshRatePolicy().removeRefreshRateRangeForPackage(str);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isTouchOrFaketouchDevice() {
            boolean z;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService windowManagerService = WindowManagerService.this;
                    if (windowManagerService.mIsTouchDevice && !windowManagerService.mIsFakeTouchDevice) {
                        throw new IllegalStateException("touchscreen supported device must report faketouch.");
                    }
                    z = windowManagerService.mIsFakeTouchDevice;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public KeyInterceptionInfo getKeyInterceptionInfoFromToken(IBinder iBinder) {
            return WindowManagerService.this.mKeyInterceptionInfoForToken.get(iBinder);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void setAccessibilityIdToSurfaceMetadata(IBinder iBinder, int i) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (windowState == null) {
                        Slog.w(StartingSurfaceController.TAG, "Cannot find window which accessibility connection is added to");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    WindowManagerService.this.mTransaction.setMetadata(windowState.mSurfaceControl, 5, i).apply();
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public String getWindowName(IBinder iBinder) {
            String name;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    name = windowState != null ? windowState.getName() : null;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return name;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public WindowManagerInternal.ImeTargetInfo onToggleImeRequested(boolean z, IBinder iBinder, IBinder iBinder2, int i) {
            String name;
            String name2;
            String str;
            String str2;
            String str3;
            String str4;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    name = windowState != null ? windowState.getName() : "null";
                    WindowState windowState2 = WindowManagerService.this.mWindowMap.get(iBinder2);
                    name2 = windowState2 != null ? windowState2.getName() : "null";
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(i);
                    if (displayContent != null) {
                        InsetsControlTarget imeTarget = displayContent.getImeTarget(2);
                        if (imeTarget != null) {
                            WindowState asWindowOrNull = InsetsControlTarget.asWindowOrNull(imeTarget);
                            str4 = asWindowOrNull != null ? asWindowOrNull.getName() : imeTarget.toString();
                        } else {
                            str4 = "null";
                        }
                        InsetsControlTarget imeTarget2 = displayContent.getImeTarget(0);
                        String name3 = imeTarget2 != null ? imeTarget2.getWindow().getName() : "null";
                        SurfaceControl surfaceControl = displayContent.mInputMethodSurfaceParent;
                        String surfaceControl2 = surfaceControl != null ? surfaceControl.toString() : "null";
                        if (z) {
                            displayContent.onShowImeRequested();
                        }
                        str = str4;
                        str2 = name3;
                        str3 = surfaceControl2;
                    } else {
                        str = "no-display";
                        str2 = str;
                        str3 = str2;
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return new WindowManagerInternal.ImeTargetInfo(name, name2, str, str2, str3);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean shouldRestoreImeVisibility(IBinder iBinder) {
            return WindowManagerService.this.shouldRestoreImeVisibility(iBinder);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void addTrustedTaskOverlay(int i, SurfaceControlViewHost.SurfacePackage surfacePackage) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task rootTask = WindowManagerService.this.mRoot.getRootTask(i);
                    if (rootTask == null) {
                        throw new IllegalArgumentException("no task with taskId" + i);
                    }
                    rootTask.addTrustedOverlay(surfacePackage, rootTask.getTopVisibleAppMainWindow());
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public void removeTrustedTaskOverlay(int i, SurfaceControlViewHost.SurfacePackage surfacePackage) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task rootTask = WindowManagerService.this.mRoot.getRootTask(i);
                    if (rootTask == null) {
                        throw new IllegalArgumentException("no task with taskId" + i);
                    }
                    rootTask.removeTrustedOverlay(surfacePackage);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public SurfaceControl getHandwritingSurfaceForDisplay(int i) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(i);
                    if (displayContent == null) {
                        Slog.e(StartingSurfaceController.TAG, "Failed to create a handwriting surface on display: " + i + " - DisplayContent not found.");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    SurfaceControl build = WindowManagerService.this.makeSurfaceBuilder(displayContent.getSession()).setContainerLayer().setName("IME Handwriting Surface").setCallsite("getHandwritingSurfaceForDisplay").setParent(displayContent.getSurfaceControl()).build();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return build;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean isPointInsideWindow(IBinder iBinder, int i, float f, float f2) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.mWindowMap.get(iBinder);
                    if (windowState != null && windowState.getDisplayId() == i) {
                        boolean contains = windowState.getBounds().contains((int) f, (int) f2);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return contains;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public boolean setContentRecordingSession(ContentRecordingSession contentRecordingSession) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (contentRecordingSession != null && contentRecordingSession.getContentToRecord() == 1) {
                        WindowContainerToken taskWindowContainerTokenForLaunchCookie = WindowManagerService.this.getTaskWindowContainerTokenForLaunchCookie(contentRecordingSession.getTokenToRecord());
                        if (taskWindowContainerTokenForLaunchCookie == null) {
                            Slog.w(StartingSurfaceController.TAG, "Handling a new recording session; unable to find the WindowContainerToken");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        contentRecordingSession.setTokenToRecord(taskWindowContainerTokenForLaunchCookie.asBinder());
                        WindowManagerService windowManagerService = WindowManagerService.this;
                        windowManagerService.mContentRecordingController.setContentRecordingSessionLocked(contentRecordingSession, windowManagerService);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                    WindowManagerService windowManagerService2 = WindowManagerService.this;
                    windowManagerService2.mContentRecordingController.setContentRecordingSessionLocked(contentRecordingSession, windowManagerService2);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return true;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal
        public SurfaceControl getA11yOverlayLayer(int i) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(i);
                    if (displayContent == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    SurfaceControl a11yOverlayLayer = displayContent.getA11yOverlayLayer();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return a11yOverlayLayer;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.WindowManagerService$ImeTargetVisibilityPolicyImpl */
    /* loaded from: classes2.dex */
    public final class ImeTargetVisibilityPolicyImpl extends ImeTargetVisibilityPolicy {
        public ImeTargetVisibilityPolicyImpl() {
        }
    }

    public void registerAppFreezeListener(AppFreezeListener appFreezeListener) {
        if (this.mAppFreezeListeners.contains(appFreezeListener)) {
            return;
        }
        this.mAppFreezeListeners.add(appFreezeListener);
    }

    public void unregisterAppFreezeListener(AppFreezeListener appFreezeListener) {
        this.mAppFreezeListeners.remove(appFreezeListener);
    }

    public void inSurfaceTransaction(Runnable runnable) {
        SurfaceControl.openTransaction();
        try {
            runnable.run();
        } finally {
            SurfaceControl.closeTransaction();
        }
    }

    public void disableNonVrUi(boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                boolean z2 = !z;
                if (z2 == this.mShowAlertWindowNotifications) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mShowAlertWindowNotifications = z2;
                for (int size = this.mSessions.size() - 1; size >= 0; size--) {
                    this.mSessions.valueAt(size).setShowingAlertWindowNotificationAllowed(this.mShowAlertWindowNotifications);
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean hasWideColorGamutSupport() {
        return this.mHasWideColorGamutSupport && SystemProperties.getInt("persist.sys.sf.native_mode", 0) != 1;
    }

    public boolean hasHdrSupport() {
        return this.mHasHdrSupport && hasWideColorGamutSupport();
    }

    public void updateNonSystemOverlayWindowsVisibilityIfNeeded(WindowState windowState, boolean z) {
        if (windowState.hideNonSystemOverlayWindowsWhenVisible() || this.mHidingNonSystemOverlayWindows.contains(windowState)) {
            boolean z2 = !this.mHidingNonSystemOverlayWindows.isEmpty();
            if (z && windowState.hideNonSystemOverlayWindowsWhenVisible()) {
                if (!this.mHidingNonSystemOverlayWindows.contains(windowState)) {
                    this.mHidingNonSystemOverlayWindows.add(windowState);
                }
            } else {
                this.mHidingNonSystemOverlayWindows.remove(windowState);
            }
            final boolean z3 = !this.mHidingNonSystemOverlayWindows.isEmpty();
            if (z2 == z3) {
                return;
            }
            this.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((WindowState) obj).setForceHideNonSystemOverlayWindowIfNeeded(z3);
                }
            }, false);
        }
    }

    public void applyMagnificationSpecLocked(int i, MagnificationSpec magnificationSpec) {
        DisplayContent displayContent = this.mRoot.getDisplayContent(i);
        if (displayContent != null) {
            displayContent.applyMagnificationSpec(magnificationSpec);
        }
    }

    public SurfaceControl.Builder makeSurfaceBuilder(SurfaceSession surfaceSession) {
        return this.mSurfaceControlFactory.apply(surfaceSession);
    }

    public void onLockTaskStateChanged(final int i) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllDisplayPolicies(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda8
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((DisplayPolicy) obj).onLockTaskStateChangedLw(i);
                    }
                });
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void syncInputTransactions(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (z) {
                try {
                    waitForAnimationsToComplete();
                } catch (InterruptedException e) {
                    Slog.e(StartingSurfaceController.TAG, "Exception thrown while waiting for window infos to be reported", e);
                }
            }
            final SurfaceControl.Transaction transaction = this.mTransactionFactory.get();
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mWindowPlacerLocked.performSurfacePlacementIfScheduled();
                    this.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda2
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.lambda$syncInputTransactions$18(transaction, (DisplayContent) obj);
                        }
                    });
                } catch (Throwable th) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            resetPriorityAfterLockedSection();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            transaction.addWindowInfosReportedListener(new SettingsStore$$ExternalSyntheticLambda1(countDownLatch)).apply();
            countDownLatch.await(5000L, TimeUnit.MILLISECONDS);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$syncInputTransactions$18(SurfaceControl.Transaction transaction, DisplayContent displayContent) {
        displayContent.getInputMonitor().updateInputWindowsImmediately(transaction);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(12:7|(1:45)(1:11)|12|(2:18|(5:37|38|39|41|42)(2:22|23))|44|(1:20)|37|38|39|41|42|5) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void waitForAnimationsToComplete() {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mAnimator.mNotifyWhenNoAnimation = true;
                long j = 5000;
                boolean z2 = false;
                while (j > 0) {
                    z2 = !this.mAtmService.getTransitionController().isShellTransitionsEnabled() && this.mRoot.forAllActivities(new Transition$ChangeInfo$$ExternalSyntheticLambda0());
                    if (!this.mAnimator.isAnimationScheduled() && !this.mRoot.isAnimating(5, -1) && !z2) {
                        z = false;
                        if (z && !this.mAtmService.getTransitionController().inTransition()) {
                            break;
                        }
                        long currentTimeMillis = System.currentTimeMillis();
                        this.mGlobalLock.wait(j);
                        j -= System.currentTimeMillis() - currentTimeMillis;
                    }
                    z = true;
                    if (z) {
                    }
                    long currentTimeMillis2 = System.currentTimeMillis();
                    this.mGlobalLock.wait(j);
                    j -= System.currentTimeMillis() - currentTimeMillis2;
                }
                this.mAnimator.mNotifyWhenNoAnimation = false;
                WindowContainer animatingContainer = this.mRoot.getAnimatingContainer(5, -1);
                if (this.mAnimator.isAnimationScheduled() || animatingContainer != null || z2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Timed out waiting for animations to complete, animatingContainer=");
                    sb.append(animatingContainer);
                    sb.append(" animationType=");
                    sb.append(SurfaceAnimator.animationTypeToString(animatingContainer != null ? animatingContainer.mSurfaceAnimator.getAnimationType() : 0));
                    sb.append(" animateStarting=");
                    sb.append(z2);
                    Slog.w(StartingSurfaceController.TAG, sb.toString());
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void onAnimationFinished() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mGlobalLock.notifyAll();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public final void onPointerDownOutsideFocusLocked(IBinder iBinder) {
        InputTarget inputTargetFromToken = getInputTargetFromToken(iBinder);
        if (inputTargetFromToken == null || !inputTargetFromToken.receiveFocusFromTapOutside()) {
            return;
        }
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController == null || recentsAnimationController.getTargetAppMainWindow() != inputTargetFromToken) {
            if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, -561092364, 0, (String) null, new Object[]{String.valueOf(inputTargetFromToken)});
            }
            InputTarget inputTarget = this.mFocusedInputTarget;
            if (inputTarget != inputTargetFromToken && inputTarget != null) {
                inputTarget.handleTapOutsideFocusOutsideSelf();
            }
            this.mAtmService.mTaskSupervisor.mUserLeaving = true;
            inputTargetFromToken.handleTapOutsideFocusInsideSelf();
            this.mAtmService.mTaskSupervisor.mUserLeaving = false;
        }
    }

    @VisibleForTesting
    public void handleTaskFocusChange(Task task, ActivityRecord activityRecord) {
        if (task == null) {
            return;
        }
        if (task.isActivityTypeHome()) {
            TaskDisplayArea displayArea = task.getDisplayArea();
            WindowState focusedWindow = getFocusedWindow();
            if (focusedWindow != null && displayArea != null && focusedWindow.isDescendantOf(displayArea)) {
                return;
            }
        }
        Transition requestTransitionIfNeeded = this.mAtmService.getTransitionController().requestTransitionIfNeeded(3, task);
        this.mAtmService.setFocusedTask(task.mTaskId, activityRecord);
        if (requestTransitionIfNeeded != null) {
            requestTransitionIfNeeded.setReady(task, true);
        }
    }

    @VisibleForTesting
    public WindowContainerToken getTaskWindowContainerTokenForLaunchCookie(final IBinder iBinder) {
        ActivityRecord activity = this.mRoot.getActivity(new Predicate() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTaskWindowContainerTokenForLaunchCookie$19;
                lambda$getTaskWindowContainerTokenForLaunchCookie$19 = WindowManagerService.lambda$getTaskWindowContainerTokenForLaunchCookie$19(iBinder, (ActivityRecord) obj);
                return lambda$getTaskWindowContainerTokenForLaunchCookie$19;
            }
        });
        if (activity == null) {
            Slog.w(StartingSurfaceController.TAG, "Unable to find the activity for this launch cookie");
            return null;
        } else if (activity.getTask() == null) {
            Slog.w(StartingSurfaceController.TAG, "Unable to find the task for this launch cookie");
            return null;
        } else {
            WindowContainerToken windowContainerToken = activity.getTask().mRemoteToken.toWindowContainerToken();
            if (windowContainerToken == null) {
                Slog.w(StartingSurfaceController.TAG, "Unable to find the WindowContainerToken for " + activity.getName());
                return null;
            }
            return windowContainerToken;
        }
    }

    public static /* synthetic */ boolean lambda$getTaskWindowContainerTokenForLaunchCookie$19(IBinder iBinder, ActivityRecord activityRecord) {
        return activityRecord.mLaunchCookie == iBinder;
    }

    public final int sanitizeFlagSlippery(int i, String str, int i2, int i3) {
        if ((536870912 & i) == 0 || this.mContext.checkPermission("android.permission.ALLOW_SLIPPERY_TOUCHES", i3, i2) == 0) {
            return i;
        }
        Slog.w(StartingSurfaceController.TAG, "Removing FLAG_SLIPPERY from '" + str + "' because it doesn't have ALLOW_SLIPPERY_TOUCHES permission");
        return (-536870913) & i;
    }

    public final int sanitizeSpyWindow(int i, String str, int i2, int i3) {
        if ((i & 4) == 0 || this.mContext.checkPermission("android.permission.MONITOR_INPUT", i3, i2) == 0) {
            return i;
        }
        throw new IllegalArgumentException("Cannot use INPUT_FEATURE_SPY from '" + str + "' because it doesn't the have MONITOR_INPUT permission");
    }

    public void grantInputChannel(Session session, int i, int i2, int i3, SurfaceControl surfaceControl, IWindow iWindow, IBinder iBinder, int i4, int i5, int i6, IBinder iBinder2, IBinder iBinder3, String str, InputChannel inputChannel) {
        InputChannel openInputChannel;
        InputApplicationHandle applicationHandle;
        String embeddedWindow;
        int sanitizeWindowType = sanitizeWindowType(session, i3, iBinder2, i6);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                EmbeddedWindowController.EmbeddedWindow embeddedWindow2 = new EmbeddedWindowController.EmbeddedWindow(session, this, iWindow, this.mInputToWindowMap.get(iBinder), i, i2, sanitizeWindowType, i3, iBinder3, str);
                openInputChannel = embeddedWindow2.openInputChannel();
                this.mEmbeddedWindowController.add(openInputChannel.getToken(), embeddedWindow2);
                applicationHandle = embeddedWindow2.getApplicationHandle();
                embeddedWindow = embeddedWindow2.toString();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        updateInputChannel(openInputChannel.getToken(), i, i2, i3, surfaceControl, embeddedWindow, applicationHandle, i4, i5, sanitizeWindowType, null, iWindow);
        openInputChannel.copyTo(inputChannel);
    }

    public boolean transferEmbeddedTouchFocusToHost(IWindow iWindow) {
        IBinder asBinder = iWindow.asBinder();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                EmbeddedWindowController.EmbeddedWindow byWindowToken = this.mEmbeddedWindowController.getByWindowToken(asBinder);
                if (byWindowToken == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempt to transfer touch focus from non-existent embedded window");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                WindowState windowState = byWindowToken.getWindowState();
                if (windowState == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempt to transfer touch focus from embedded window with no associated host");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                IBinder inputChannelToken = byWindowToken.getInputChannelToken();
                if (inputChannelToken == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempt to transfer touch focus from embedded window with no input channel");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                IBinder iBinder = windowState.mInputChannelToken;
                if (iBinder == null) {
                    Slog.w(StartingSurfaceController.TAG, "Attempt to transfer touch focus to a host window with no input channel");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean transferTouchFocus = this.mInputManager.transferTouchFocus(inputChannelToken, iBinder);
                resetPriorityAfterLockedSection();
                return transferTouchFocus;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void updateInputChannel(IBinder iBinder, int i, int i2, int i3, SurfaceControl surfaceControl, String str, InputApplicationHandle inputApplicationHandle, int i4, int i5, int i6, Region region, IWindow iWindow) {
        InputWindowHandle inputWindowHandle = new InputWindowHandle(inputApplicationHandle, i3);
        inputWindowHandle.token = iBinder;
        inputWindowHandle.setWindowToken(iWindow);
        inputWindowHandle.name = str;
        int sanitizeFlagSlippery = sanitizeFlagSlippery(i4, str, i, i2);
        int i7 = (536870936 & sanitizeFlagSlippery) | 32;
        inputWindowHandle.layoutParamsType = i6;
        inputWindowHandle.layoutParamsFlags = i7;
        int inputConfigFromWindowParams = InputConfigAdapter.getInputConfigFromWindowParams(i6, i7, 0);
        inputWindowHandle.inputConfig = inputConfigFromWindowParams;
        if ((sanitizeFlagSlippery & 8) != 0) {
            inputWindowHandle.inputConfig = inputConfigFromWindowParams | 4;
        }
        if ((536870912 & i5) != 0) {
            inputWindowHandle.inputConfig |= 256;
        }
        inputWindowHandle.dispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        inputWindowHandle.ownerUid = i;
        inputWindowHandle.ownerPid = i2;
        if (region == null) {
            inputWindowHandle.replaceTouchableRegionWithCrop((SurfaceControl) null);
        } else {
            inputWindowHandle.touchableRegion.set(region);
            inputWindowHandle.replaceTouchableRegionWithCrop = false;
            inputWindowHandle.setTouchableRegionCrop(surfaceControl);
        }
        SurfaceControl.Transaction transaction = this.mTransactionFactory.get();
        transaction.setInputWindowInfo(surfaceControl, inputWindowHandle);
        transaction.apply();
        transaction.close();
        surfaceControl.release();
    }

    public void updateInputChannel(IBinder iBinder, int i, SurfaceControl surfaceControl, int i2, int i3, Region region) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                EmbeddedWindowController.EmbeddedWindow embeddedWindow = this.mEmbeddedWindowController.get(iBinder);
                if (embeddedWindow == null) {
                    Slog.e(StartingSurfaceController.TAG, "Couldn't find window for provided channelToken.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                String embeddedWindow2 = embeddedWindow.toString();
                InputApplicationHandle applicationHandle = embeddedWindow.getApplicationHandle();
                resetPriorityAfterLockedSection();
                updateInputChannel(iBinder, embeddedWindow.mOwnerUid, embeddedWindow.mOwnerPid, i, surfaceControl, embeddedWindow2, applicationHandle, i2, i3, embeddedWindow.mWindowType, region, embeddedWindow.mClient);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0054 A[Catch: all -> 0x0058, TRY_ENTER, TryCatch #2 {all -> 0x0058, blocks: (B:9:0x002e, B:17:0x0045, B:19:0x004a, B:24:0x0054, B:28:0x005c, B:29:0x005f), top: B:38:0x0010 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x005c A[Catch: all -> 0x0058, TryCatch #2 {all -> 0x0058, blocks: (B:9:0x002e, B:17:0x0045, B:19:0x004a, B:24:0x0054, B:28:0x005c, B:29:0x005f), top: B:38:0x0010 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean isLayerTracing() {
        Parcel parcel;
        if (!checkCallingPermission("android.permission.DUMP", "isLayerTracing()")) {
            throw new SecurityException("Requires DUMP permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Parcel parcel2 = null;
        try {
            try {
                try {
                    IBinder service = ServiceManager.getService("SurfaceFlinger");
                    if (service != null) {
                        parcel = Parcel.obtain();
                        try {
                            parcel2 = Parcel.obtain();
                            parcel2.writeInterfaceToken("android.ui.ISurfaceComposer");
                            service.transact(1026, parcel2, parcel, 0);
                            boolean readBoolean = parcel.readBoolean();
                            parcel2.recycle();
                            parcel.recycle();
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return readBoolean;
                        } catch (RemoteException unused) {
                            Slog.e(StartingSurfaceController.TAG, "Failed to get layer tracing");
                            if (parcel2 != null) {
                                parcel2.recycle();
                            }
                            if (parcel != null) {
                                parcel.recycle();
                            }
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return false;
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    if (0 != 0) {
                        parcel2.recycle();
                    }
                    if (0 != 0) {
                        parcel2.recycle();
                    }
                    throw th;
                }
            } catch (RemoteException unused2) {
                parcel = null;
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                }
                if (0 != 0) {
                }
                throw th;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } catch (Throwable th3) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th3;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0035, code lost:
        if (r5 != null) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0037, code lost:
        r5.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0046, code lost:
        if (r5 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004c, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setLayerTracing(boolean z) {
        Parcel parcel;
        Throwable th;
        if (!checkCallingPermission("android.permission.DUMP", "setLayerTracing()")) {
            throw new SecurityException("Requires DUMP permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Parcel parcel2 = null;
        try {
            try {
                try {
                    IBinder service = ServiceManager.getService("SurfaceFlinger");
                    if (service != null) {
                        parcel = Parcel.obtain();
                        try {
                            parcel.writeInterfaceToken("android.ui.ISurfaceComposer");
                            parcel.writeInt(z ? 1 : 0);
                            service.transact(1025, parcel, null, 0);
                            parcel2 = parcel;
                        } catch (RemoteException unused) {
                            parcel2 = parcel;
                            Slog.e(StartingSurfaceController.TAG, "Failed to set layer tracing");
                        } catch (Throwable th2) {
                            th = th2;
                            if (parcel != null) {
                                parcel.recycle();
                            }
                            throw th;
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (RemoteException unused2) {
            }
        } catch (Throwable th3) {
            parcel = parcel2;
            th = th3;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0030, code lost:
        if (r5 != null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0032, code lost:
        r5.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0041, code lost:
        if (r5 == null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0047, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setLayerTracingFlags(int i) {
        Parcel parcel;
        Throwable th;
        if (!checkCallingPermission("android.permission.DUMP", "setLayerTracingFlags")) {
            throw new SecurityException("Requires DUMP permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Parcel parcel2 = null;
        try {
            try {
                try {
                    IBinder service = ServiceManager.getService("SurfaceFlinger");
                    if (service != null) {
                        parcel = Parcel.obtain();
                        try {
                            parcel.writeInterfaceToken("android.ui.ISurfaceComposer");
                            parcel.writeInt(i);
                            service.transact(1033, parcel, null, 0);
                            parcel2 = parcel;
                        } catch (RemoteException unused) {
                            parcel2 = parcel;
                            Slog.e(StartingSurfaceController.TAG, "Failed to set layer tracing flags");
                        } catch (Throwable th2) {
                            th = th2;
                            if (parcel != null) {
                                parcel.recycle();
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    parcel = null;
                    th = th3;
                }
            } catch (RemoteException unused2) {
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0035, code lost:
        if (r5 != null) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0037, code lost:
        r5.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0046, code lost:
        if (r5 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004c, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setActiveTransactionTracing(boolean z) {
        Parcel parcel;
        Throwable th;
        if (!checkCallingPermission("android.permission.DUMP", "setActiveTransactionTracing()")) {
            throw new SecurityException("Requires DUMP permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Parcel parcel2 = null;
        try {
            try {
                try {
                    IBinder service = ServiceManager.getService("SurfaceFlinger");
                    if (service != null) {
                        parcel = Parcel.obtain();
                        try {
                            parcel.writeInterfaceToken("android.ui.ISurfaceComposer");
                            parcel.writeInt(z ? 1 : 0);
                            service.transact(1041, parcel, null, 0);
                            parcel2 = parcel;
                        } catch (RemoteException unused) {
                            parcel2 = parcel;
                            Slog.e(StartingSurfaceController.TAG, "Failed to set transaction tracing");
                        } catch (Throwable th2) {
                            th = th2;
                            if (parcel != null) {
                                parcel.recycle();
                            }
                            throw th;
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (RemoteException unused2) {
            }
        } catch (Throwable th3) {
            parcel = parcel2;
            th = th3;
        }
    }

    public boolean mirrorDisplay(int i, SurfaceControl surfaceControl) {
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "mirrorDisplay()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.e(StartingSurfaceController.TAG, "Invalid displayId " + i + " for mirrorDisplay");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                SurfaceControl windowingLayer = displayContent.getWindowingLayer();
                resetPriorityAfterLockedSection();
                surfaceControl.copyFrom(SurfaceControl.mirrorSurface(windowingLayer), "WMS.mirrorDisplay");
                return true;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean getWindowInsets(int i, IBinder iBinder, InsetsState insetsState) {
        boolean areSystemBarsForcedConsumedLw;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent displayContentOrCreate = getDisplayContentOrCreate(i, iBinder);
                if (displayContentOrCreate == null) {
                    throw new WindowManager.InvalidDisplayException("Display#" + i + "could not be found!");
                }
                displayContentOrCreate.getInsetsPolicy().getInsetsForWindowMetrics(displayContentOrCreate.getWindowToken(iBinder), insetsState);
                areSystemBarsForcedConsumedLw = displayContentOrCreate.getDisplayPolicy().areSystemBarsForcedConsumedLw();
            }
            resetPriorityAfterLockedSection();
            return areSystemBarsForcedConsumedLw;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<DisplayInfo> getPossibleDisplayInfo(int i) {
        List<DisplayInfo> possibleDisplayInfos;
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                if (!this.mAtmService.isCallerRecents(callingUid)) {
                    Slog.e(StartingSurfaceController.TAG, "Unable to verify uid for getPossibleDisplayInfo on uid " + callingUid);
                    possibleDisplayInfos = new ArrayList<>();
                } else {
                    possibleDisplayInfos = this.mPossibleDisplayInfoMapper.getPossibleDisplayInfos(i);
                }
            }
            resetPriorityAfterLockedSection();
            return possibleDisplayInfos;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<DisplayInfo> getPossibleDisplayInfoLocked(int i) {
        return this.mPossibleDisplayInfoMapper.getPossibleDisplayInfos(i);
    }

    public void grantEmbeddedWindowFocus(Session session, IBinder iBinder, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                EmbeddedWindowController.EmbeddedWindow byFocusToken = this.mEmbeddedWindowController.getByFocusToken(iBinder);
                if (byFocusToken == null) {
                    Slog.e(StartingSurfaceController.TAG, "Embedded window not found");
                    resetPriorityAfterLockedSection();
                } else if (byFocusToken.mSession != session) {
                    Slog.e(StartingSurfaceController.TAG, "Window not in session:" + session);
                    resetPriorityAfterLockedSection();
                } else {
                    IBinder inputChannelToken = byFocusToken.getInputChannelToken();
                    if (inputChannelToken == null) {
                        Slog.e(StartingSurfaceController.TAG, "Focus token found but input channel token not found");
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    SurfaceControl.Transaction transaction = this.mTransactionFactory.get();
                    int i = byFocusToken.mDisplayId;
                    if (z) {
                        transaction.setFocusedWindow(inputChannelToken, byFocusToken.toString(), i).apply();
                        EventLog.writeEvent(62001, "Focus request " + byFocusToken, "reason=grantEmbeddedWindowFocus(true)");
                    } else {
                        DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                        WindowState findFocusedWindow = displayContent == null ? null : displayContent.findFocusedWindow();
                        if (findFocusedWindow == null) {
                            transaction.setFocusedWindow(null, null, i).apply();
                            if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS, 958338552, 0, (String) null, new Object[]{String.valueOf(byFocusToken)});
                            }
                            resetPriorityAfterLockedSection();
                            return;
                        }
                        transaction.setFocusedWindow(findFocusedWindow.mInputChannelToken, findFocusedWindow.getName(), i).apply();
                        EventLog.writeEvent(62001, "Focus request " + findFocusedWindow, "reason=grantEmbeddedWindowFocus(false)");
                    }
                    if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS, -2107721178, 0, (String) null, new Object[]{String.valueOf(byFocusToken), String.valueOf(z)});
                    }
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void grantEmbeddedWindowFocus(Session session, IWindow iWindow, IBinder iBinder, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked == null) {
                    Slog.e(StartingSurfaceController.TAG, "Host window not found");
                    resetPriorityAfterLockedSection();
                } else if (windowForClientLocked.mInputChannel == null) {
                    Slog.e(StartingSurfaceController.TAG, "Host window does not have an input channel");
                    resetPriorityAfterLockedSection();
                } else {
                    EmbeddedWindowController.EmbeddedWindow byFocusToken = this.mEmbeddedWindowController.getByFocusToken(iBinder);
                    if (byFocusToken == null) {
                        Slog.e(StartingSurfaceController.TAG, "Embedded window not found");
                        resetPriorityAfterLockedSection();
                    } else if (byFocusToken.mHostWindowState != windowForClientLocked) {
                        Slog.e(StartingSurfaceController.TAG, "Embedded window does not belong to the host");
                        resetPriorityAfterLockedSection();
                    } else {
                        SurfaceControl.Transaction transaction = this.mTransactionFactory.get();
                        if (z) {
                            transaction.requestFocusTransfer(byFocusToken.getInputChannelToken(), byFocusToken.toString(), windowForClientLocked.mInputChannel.getToken(), windowForClientLocked.getName(), windowForClientLocked.getDisplayId()).apply();
                            EventLog.writeEvent(62001, "Transfer focus request " + byFocusToken, "reason=grantEmbeddedWindowFocus(true)");
                        } else {
                            transaction.requestFocusTransfer(windowForClientLocked.mInputChannel.getToken(), windowForClientLocked.getName(), byFocusToken.getInputChannelToken(), byFocusToken.toString(), windowForClientLocked.getDisplayId()).apply();
                            EventLog.writeEvent(62001, "Transfer focus request " + windowForClientLocked, "reason=grantEmbeddedWindowFocus(false)");
                        }
                        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS, -2107721178, 0, (String) null, new Object[]{String.valueOf(byFocusToken), String.valueOf(z)});
                        }
                        resetPriorityAfterLockedSection();
                    }
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void holdLock(IBinder iBinder, int i) {
        this.mTestUtilityService.verifyHoldLockToken(iBinder);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                SystemClock.sleep(i);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public String[] getSupportedDisplayHashAlgorithms() {
        return this.mDisplayHashController.getSupportedHashAlgorithms();
    }

    public VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) {
        return this.mDisplayHashController.verifyDisplayHash(displayHash);
    }

    public void setDisplayHashThrottlingEnabled(boolean z) {
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "setDisplayHashThrottle()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        this.mDisplayHashController.setDisplayHashThrottlingEnabled(z);
    }

    public boolean isTaskSnapshotSupported() {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                z = !this.mTaskSnapshotController.shouldDisableSnapshots();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return z;
    }

    public void generateDisplayHash(Session session, IWindow iWindow, Rect rect, String str, RemoteCallback remoteCallback) {
        Rect rect2 = new Rect(rect);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowForClientLocked = windowForClientLocked(session, iWindow, false);
                if (windowForClientLocked == null) {
                    Slog.w(StartingSurfaceController.TAG, "Failed to generate DisplayHash. Invalid window");
                    this.mDisplayHashController.sendDisplayHashError(remoteCallback, -3);
                    resetPriorityAfterLockedSection();
                    return;
                }
                ActivityRecord activityRecord = windowForClientLocked.mActivityRecord;
                if (activityRecord != null && activityRecord.isState(ActivityRecord.State.RESUMED)) {
                    DisplayContent displayContent = windowForClientLocked.getDisplayContent();
                    if (displayContent == null) {
                        Slog.w(StartingSurfaceController.TAG, "Failed to generate DisplayHash. Window is not on a display");
                        this.mDisplayHashController.sendDisplayHashError(remoteCallback, -4);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    SurfaceControl surfaceControl = displayContent.getSurfaceControl();
                    this.mDisplayHashController.calculateDisplayHashBoundsLocked(windowForClientLocked, rect, rect2);
                    if (rect2.isEmpty()) {
                        Slog.w(StartingSurfaceController.TAG, "Failed to generate DisplayHash. Bounds are not on screen");
                        this.mDisplayHashController.sendDisplayHashError(remoteCallback, -4);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    resetPriorityAfterLockedSection();
                    int i = session.mUid;
                    this.mDisplayHashController.generateDisplayHash(new ScreenCapture.LayerCaptureArgs.Builder(surfaceControl).setUid(i).setSourceCrop(rect2), rect, str, i, remoteCallback);
                    return;
                }
                this.mDisplayHashController.sendDisplayHashError(remoteCallback, -3);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean shouldRestoreImeVisibility(IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState windowState = this.mWindowMap.get(iBinder);
                if (windowState == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                Task task = windowState.getTask();
                if (task == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                ActivityRecord activityRecord = windowState.mActivityRecord;
                boolean z = activityRecord != null ? activityRecord.mLastImeShown : false;
                resetPriorityAfterLockedSection();
                TaskSnapshot taskSnapshot = getTaskSnapshot(task.mTaskId, task.mUserId, false, false);
                return (taskSnapshot != null && taskSnapshot.hasImeSurface()) || z;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getImeDisplayId() {
        int displayId;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent topFocusedDisplayContent = this.mRoot.getTopFocusedDisplayContent();
                displayId = topFocusedDisplayContent.getImePolicy() == 0 ? topFocusedDisplayContent.getDisplayId() : 0;
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return displayId;
    }

    public void setTaskSnapshotEnabled(boolean z) {
        this.mTaskSnapshotController.setSnapshotEnabled(z);
    }

    public void setTaskTransitionSpec(TaskTransitionSpec taskTransitionSpec) {
        if (!checkCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS", "setTaskTransitionSpec()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_TASKS permission");
        }
        this.mTaskTransitionSpec = taskTransitionSpec;
    }

    public void clearTaskTransitionSpec() {
        if (!checkCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS", "clearTaskTransitionSpec()")) {
            throw new SecurityException("Requires MANAGE_ACTIVITY_TASKS permission");
        }
        this.mTaskTransitionSpec = null;
    }

    @RequiresPermission("android.permission.ACCESS_FPS_COUNTER")
    public void registerTaskFpsCallback(int i, ITaskFpsCallback iTaskFpsCallback) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FPS_COUNTER") != 0) {
            int callingPid = Binder.getCallingPid();
            throw new SecurityException("Access denied to process: " + callingPid + ", must have permission android.permission.ACCESS_FPS_COUNTER");
        } else if (this.mRoot.anyTaskForId(i) == null) {
            throw new IllegalArgumentException("no task with taskId: " + i);
        } else {
            this.mTaskFpsCallbackController.registerListener(i, iTaskFpsCallback);
        }
    }

    @RequiresPermission("android.permission.ACCESS_FPS_COUNTER")
    public void unregisterTaskFpsCallback(ITaskFpsCallback iTaskFpsCallback) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_FPS_COUNTER") != 0) {
            int callingPid = Binder.getCallingPid();
            throw new SecurityException("Access denied to process: " + callingPid + ", must have permission android.permission.ACCESS_FPS_COUNTER");
        }
        this.mTaskFpsCallbackController.lambda$registerListener$0(iTaskFpsCallback);
    }

    public Bitmap snapshotTaskForRecents(int i) {
        TaskSnapshot captureSnapshot;
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "snapshotTaskForRecents()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                Task anyTaskForId = this.mRoot.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    throw new IllegalArgumentException("Failed to find matching task for taskId=" + i);
                }
                captureSnapshot = this.mTaskSnapshotController.captureSnapshot(anyTaskForId, false);
            }
            resetPriorityAfterLockedSection();
            if (captureSnapshot == null || captureSnapshot.getHardwareBuffer() == null) {
                return null;
            }
            return Bitmap.wrapHardwareBuffer(captureSnapshot.getHardwareBuffer(), captureSnapshot.getColorSpace());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setRecentsAppBehindSystemBars(boolean z) {
        if (!checkCallingPermission("android.permission.START_TASKS_FROM_RECENTS", "setRecentsAppBehindSystemBars()")) {
            throw new SecurityException("Requires START_TASKS_FROM_RECENTS permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                Task task = this.mRoot.getTask(new Predicate() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$setRecentsAppBehindSystemBars$20;
                        lambda$setRecentsAppBehindSystemBars$20 = WindowManagerService.lambda$setRecentsAppBehindSystemBars$20((Task) obj);
                        return lambda$setRecentsAppBehindSystemBars$20;
                    }
                });
                if (task != null) {
                    task.getTask().setCanAffectSystemUiFlags(z);
                    this.mWindowPlacerLocked.requestTraversal();
                }
                InputMethodManagerInternal.get().maybeFinishStylusHandwriting();
            }
            resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ boolean lambda$setRecentsAppBehindSystemBars$20(Task task) {
        return task.isActivityTypeHomeOrRecents() && task.getTopVisibleActivity() != null;
    }

    public int getLetterboxBackgroundColorInArgb() {
        return this.mLetterboxConfiguration.getLetterboxBackgroundColor().toArgb();
    }

    public boolean isLetterboxBackgroundMultiColored() {
        int letterboxBackgroundType = this.mLetterboxConfiguration.getLetterboxBackgroundType();
        if (letterboxBackgroundType != 0) {
            if (letterboxBackgroundType == 1 || letterboxBackgroundType == 2 || letterboxBackgroundType == 3) {
                return true;
            }
            throw new AssertionError("Unexpected letterbox background type: " + letterboxBackgroundType);
        }
        return false;
    }

    public void captureDisplay(int i, ScreenCapture.CaptureArgs captureArgs, ScreenCapture.ScreenCaptureListener screenCaptureListener) {
        Slog.d(StartingSurfaceController.TAG, "captureDisplay");
        if (!checkCallingPermission("android.permission.READ_FRAME_BUFFER", "captureDisplay()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        ScreenCapture.captureLayers(getCaptureArgs(i, captureArgs), screenCaptureListener);
    }

    @VisibleForTesting
    public ScreenCapture.LayerCaptureArgs getCaptureArgs(int i, ScreenCapture.CaptureArgs captureArgs) {
        SurfaceControl surfaceControl;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("Trying to screenshot and invalid display: " + i);
                }
                surfaceControl = displayContent.getSurfaceControl();
                if (captureArgs == null) {
                    captureArgs = new ScreenCapture.CaptureArgs.Builder().build();
                }
                if (captureArgs.mSourceCrop.isEmpty()) {
                    displayContent.getBounds(this.mTmpRect);
                    this.mTmpRect.offsetTo(0, 0);
                } else {
                    this.mTmpRect.set(captureArgs.mSourceCrop);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return new ScreenCapture.LayerCaptureArgs.Builder(surfaceControl, captureArgs).setSourceCrop(this.mTmpRect).build();
    }

    public boolean isGlobalKey(int i) {
        return this.mPolicy.isGlobalKey(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0019, code lost:
        if (r7 != r3.getWindowType()) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int sanitizeWindowType(Session session, int i, IBinder iBinder, int i2) {
        boolean z = true;
        if (i2 == 2032 && iBinder != null) {
            WindowToken windowToken = this.mRoot.getDisplayContent(i).getWindowToken(iBinder);
            if (windowToken != null) {
            }
            z = false;
        } else if (!session.mCanAddInternalSystemWindow && i2 != 0) {
            Slog.w(StartingSurfaceController.TAG, "Requires INTERNAL_SYSTEM_WINDOW permission if assign type to input. New type will be 0.");
            z = false;
        }
        if (z) {
            return i2;
        }
        return 0;
    }

    public boolean addToSurfaceSyncGroup(IBinder iBinder, boolean z, ISurfaceSyncGroupCompletedListener iSurfaceSyncGroupCompletedListener, AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) {
        return this.mSurfaceSyncGroupController.addToSyncGroup(iBinder, z, iSurfaceSyncGroupCompletedListener, addToSurfaceSyncGroupResult);
    }

    public void markSurfaceSyncGroupReady(IBinder iBinder) {
        this.mSurfaceSyncGroupController.markSyncGroupReady(iBinder);
    }

    public final ArraySet<ActivityRecord> getVisibleActivityRecords(int i) {
        final ArraySet<ActivityRecord> arraySet = new ArraySet<>();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                final ArraySet arraySet2 = new ArraySet();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent != null) {
                    displayContent.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerService$$ExternalSyntheticLambda3
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            WindowManagerService.lambda$getVisibleActivityRecords$21(arraySet2, arraySet, (WindowState) obj);
                        }
                    }, true);
                }
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return arraySet;
    }

    public static /* synthetic */ void lambda$getVisibleActivityRecords$21(ArraySet arraySet, ArraySet arraySet2, WindowState windowState) {
        ActivityRecord activityRecord;
        if (windowState.isVisible() && windowState.isDisplayed() && (activityRecord = windowState.mActivityRecord) != null && !arraySet.contains(activityRecord.mActivityComponent) && windowState.mActivityRecord.isVisible() && windowState.isVisibleNow()) {
            arraySet.add(windowState.mActivityRecord.mActivityComponent);
            arraySet2.add(windowState.mActivityRecord);
        }
    }

    public List<ComponentName> notifyScreenshotListeners(int i) {
        List<ComponentName> copyOf;
        if (!checkCallingPermission("android.permission.STATUS_BAR_SERVICE", "notifyScreenshotListeners()")) {
            throw new SecurityException("Requires STATUS_BAR_SERVICE permission");
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                ArraySet arraySet = new ArraySet();
                Iterator<ActivityRecord> it = getVisibleActivityRecords(i).iterator();
                while (it.hasNext()) {
                    ActivityRecord next = it.next();
                    if (next.isRegisteredForScreenCaptureCallback()) {
                        next.reportScreenCaptured();
                        arraySet.add(next.mActivityComponent);
                    }
                }
                copyOf = List.copyOf(arraySet);
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
        return copyOf;
    }
}
