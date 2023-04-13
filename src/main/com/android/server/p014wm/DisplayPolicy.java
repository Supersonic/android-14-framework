package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.LoadedApk;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.ArraySet;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayInfo;
import android.view.InsetsFlags;
import android.view.InsetsFrameProvider;
import android.view.InsetsSource;
import android.view.InsetsState;
import android.view.Surface;
import android.view.ViewDebug;
import android.view.WindowInsets;
import android.view.WindowLayout;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.window.ClientWindowFrames;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.ForceShowNavBarSettingsObserver;
import com.android.internal.policy.GestureNavigationSettingsObserver;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.statusbar.LetterboxDetails;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.util.ScreenshotRequest;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.view.AppearanceRegion;
import com.android.internal.widget.PointerLocationView;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.input.InputManagerService;
import com.android.server.p014wm.DisplayPolicy;
import com.android.server.p014wm.SystemGesturesPointerEventListener;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wallpaper.WallpaperManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.DisplayPolicy */
/* loaded from: classes2.dex */
public class DisplayPolicy {
    public final AccessibilityManager mAccessibilityManager;
    public boolean mAllowLockscreenWhenOn;
    public final WindowManagerInternal.AppTransitionListener mAppTransitionListener;
    public volatile boolean mAwake;
    public WindowState mBottomGestureHost;
    public boolean mCanSystemBarsBeShownByUser;
    public final boolean mCarDockEnablesAccelerometer;
    public final Context mContext;
    public Resources mCurrentUserResources;
    public final DecorInsets mDecorInsets;
    public final boolean mDeskDockEnablesAccelerometer;
    public final DisplayContent mDisplayContent;
    public boolean mDreamingLockscreen;
    public String mFocusedApp;
    public WindowState mFocusedWindow;
    public boolean mForceConsumeSystemBars;
    public final ForceShowNavBarSettingsObserver mForceShowNavBarSettingsObserver;
    public boolean mForceShowNavigationBarEnabled;
    public boolean mForceShowSystemBars;
    public final GestureNavigationSettingsObserver mGestureNavigationSettingsObserver;
    public final Handler mHandler;
    public volatile boolean mHasNavigationBar;
    public volatile boolean mHasStatusBar;
    public volatile boolean mHdmiPlugged;
    public final ImmersiveModeConfirmation mImmersiveModeConfirmation;
    public boolean mIsFreeformWindowOverlappingWithNavBar;
    public boolean mIsImmersiveMode;
    public volatile boolean mKeyguardDrawComplete;
    public int mLastAppearance;
    public int mLastBehavior;
    public int mLastDisableFlags;
    public WindowState mLastFocusedWindow;
    public LetterboxDetails[] mLastLetterboxDetails;
    public boolean mLastShowingDream;
    public AppearanceRegion[] mLastStatusBarAppearanceRegions;
    public WindowState mLeftGestureHost;
    public int mLeftGestureInset;
    public final Object mLock;
    public WindowState mNavBarBackgroundWindow;
    public WindowState mNavBarColorWindowCandidate;
    public volatile boolean mNavigationBarAlwaysShowOnSideGesture;
    public volatile boolean mNavigationBarCanMove;
    public volatile WindowState mNotificationShade;
    public long mPendingPanicGestureUptime;
    public volatile boolean mPersistentVrModeEnabled;
    public PointerLocationView mPointerLocationView;
    public RefreshRatePolicy mRefreshRatePolicy;
    public boolean mRemoteInsetsControllerControlsSystemBars;
    public WindowState mRightGestureHost;
    public int mRightGestureInset;
    public volatile boolean mScreenOnEarly;
    public volatile boolean mScreenOnFully;
    public volatile WindowManagerPolicy.ScreenOnListener mScreenOnListener;
    public final ScreenshotHelper mScreenshotHelper;
    public final WindowManagerService mService;
    public boolean mShouldAttachNavBarToAppDuringTransition;
    public boolean mShowingDream;
    public StatusBarManagerInternal mStatusBarManagerInternal;
    public final SystemGesturesPointerEventListener mSystemGestures;
    public WindowState mSystemUiControllingWindow;
    public WindowState mTopFullscreenOpaqueWindowState;
    public WindowState mTopGestureHost;
    public boolean mTopIsFullscreen;
    public final Context mUiContext;
    public volatile boolean mWindowManagerDrawComplete;
    public static final int SHOW_TYPES_FOR_SWIPE = WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars();
    public static final int SHOW_TYPES_FOR_PANIC = WindowInsets.Type.navigationBars();
    public static final Rect sTmpRect = new Rect();
    public static final Rect sTmpRect2 = new Rect();
    public static final Rect sTmpDisplayCutoutSafe = new Rect();
    public static final ClientWindowFrames sTmpClientFrames = new ClientWindowFrames();
    public final Object mServiceAcquireLock = new Object();
    public volatile int mLidState = -1;
    public volatile int mDockMode = 0;
    public WindowState mStatusBar = null;
    public WindowState mNavigationBar = null;
    public int mNavigationBarPosition = 4;
    public final ArraySet<WindowState> mInsetsSourceWindowsExceptIme = new ArraySet<>();
    public final ArraySet<ActivityRecord> mSystemBarColorApps = new ArraySet<>();
    public final ArraySet<ActivityRecord> mRelaunchingSystemBarColorApps = new ArraySet<>();
    public final ArrayList<AppearanceRegion> mStatusBarAppearanceRegionList = new ArrayList<>();
    public final ArrayList<WindowState> mStatusBarBackgroundWindows = new ArrayList<>();
    public final ArrayList<LetterboxDetails> mLetterboxDetails = new ArrayList<>();
    public int mLastRequestedVisibleTypes = WindowInsets.Type.defaultVisible();
    public final Rect mStatusBarColorCheckedBounds = new Rect();
    public final Rect mStatusBarBackgroundCheckedBounds = new Rect();
    public boolean mLastFocusIsFullscreen = false;
    public final WindowLayout mWindowLayout = new WindowLayout();
    public int mNavBarOpacityMode = 0;
    public final Runnable mHiddenNavPanic = new Runnable() { // from class: com.android.server.wm.DisplayPolicy.3
        @Override // java.lang.Runnable
        public void run() {
            synchronized (DisplayPolicy.this.mLock) {
                if (DisplayPolicy.this.mService.mPolicy.isUserSetupComplete()) {
                    DisplayPolicy.this.mPendingPanicGestureUptime = SystemClock.uptimeMillis();
                    DisplayPolicy.this.updateSystemBarAttributes();
                }
            }
        }
    };

    public static boolean isNavBarEmpty(int i) {
        return (i & 23068672) == 23068672;
    }

    public final int clearNavBarOpaqueFlag(int i) {
        return i & (-3);
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

    /* renamed from: com.android.server.wm.DisplayPolicy$PolicyHandler */
    /* loaded from: classes2.dex */
    public class PolicyHandler extends Handler {
        public PolicyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 4) {
                DisplayPolicy.this.enablePointerLocation();
            } else if (i != 5) {
            } else {
                DisplayPolicy.this.disablePointerLocation();
            }
        }
    }

    public DisplayPolicy(WindowManagerService windowManagerService, DisplayContent displayContent) {
        this.mService = windowManagerService;
        Context createDisplayContext = displayContent.isDefaultDisplay ? windowManagerService.mContext : windowManagerService.mContext.createDisplayContext(displayContent.getDisplay());
        this.mContext = createDisplayContext;
        Context uiContext = displayContent.isDefaultDisplay ? windowManagerService.mAtmService.getUiContext() : windowManagerService.mAtmService.mSystemThread.getSystemUiContext(displayContent.getDisplayId());
        this.mUiContext = uiContext;
        this.mDisplayContent = displayContent;
        this.mDecorInsets = new DecorInsets(displayContent);
        this.mLock = windowManagerService.getWindowManagerLock();
        int displayId = displayContent.getDisplayId();
        Resources resources = createDisplayContext.getResources();
        this.mCarDockEnablesAccelerometer = resources.getBoolean(17891404);
        this.mDeskDockEnablesAccelerometer = resources.getBoolean(17891600);
        this.mCanSystemBarsBeShownByUser = !resources.getBoolean(17891334) || resources.getBoolean(17891773);
        this.mAccessibilityManager = (AccessibilityManager) createDisplayContext.getSystemService("accessibility");
        if (!displayContent.isDefaultDisplay) {
            this.mAwake = true;
            this.mScreenOnEarly = true;
            this.mScreenOnFully = true;
        }
        Looper looper = UiThread.getHandler().getLooper();
        PolicyHandler policyHandler = new PolicyHandler(looper);
        this.mHandler = policyHandler;
        SystemGesturesPointerEventListener systemGesturesPointerEventListener = new SystemGesturesPointerEventListener(uiContext, policyHandler, new C18611());
        this.mSystemGestures = systemGesturesPointerEventListener;
        displayContent.registerPointerEventListener(systemGesturesPointerEventListener);
        C18622 c18622 = new C18622(displayId);
        this.mAppTransitionListener = c18622;
        displayContent.mAppTransition.registerListenerLocked(c18622);
        displayContent.mTransitionController.registerLegacyListener(c18622);
        this.mImmersiveModeConfirmation = new ImmersiveModeConfirmation(createDisplayContext, looper, windowManagerService.mVrModeEnabled, this.mCanSystemBarsBeShownByUser);
        this.mScreenshotHelper = displayContent.isDefaultDisplay ? new ScreenshotHelper(createDisplayContext) : null;
        if (displayContent.isDefaultDisplay) {
            this.mHasStatusBar = true;
            this.mHasNavigationBar = createDisplayContext.getResources().getBoolean(17891792);
            String str = SystemProperties.get("qemu.hw.mainkeys");
            if ("1".equals(str)) {
                this.mHasNavigationBar = false;
            } else if ("0".equals(str)) {
                this.mHasNavigationBar = true;
            }
        } else {
            this.mHasStatusBar = false;
            this.mHasNavigationBar = displayContent.supportsSystemDecorations();
        }
        this.mRefreshRatePolicy = new RefreshRatePolicy(windowManagerService, displayContent.getDisplayInfo(), windowManagerService.mHighRefreshRateDenylist);
        final GestureNavigationSettingsObserver gestureNavigationSettingsObserver = new GestureNavigationSettingsObserver(policyHandler, createDisplayContext, new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.this.lambda$new$0();
            }
        });
        this.mGestureNavigationSettingsObserver = gestureNavigationSettingsObserver;
        Objects.requireNonNull(gestureNavigationSettingsObserver);
        policyHandler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                gestureNavigationSettingsObserver.register();
            }
        });
        final ForceShowNavBarSettingsObserver forceShowNavBarSettingsObserver = new ForceShowNavBarSettingsObserver(policyHandler, createDisplayContext);
        this.mForceShowNavBarSettingsObserver = forceShowNavBarSettingsObserver;
        forceShowNavBarSettingsObserver.setOnChangeRunnable(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.this.updateForceShowNavBarSettings();
            }
        });
        this.mForceShowNavigationBarEnabled = forceShowNavBarSettingsObserver.isEnabled();
        Objects.requireNonNull(forceShowNavBarSettingsObserver);
        policyHandler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                forceShowNavBarSettingsObserver.register();
            }
        });
    }

    /* renamed from: com.android.server.wm.DisplayPolicy$1 */
    /* loaded from: classes2.dex */
    public class C18611 implements SystemGesturesPointerEventListener.Callbacks {
        public Runnable mOnSwipeFromLeft = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$1$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.C18611.this.onSwipeFromLeft();
            }
        };
        public Runnable mOnSwipeFromTop = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$1$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.C18611.this.onSwipeFromTop();
            }
        };
        public Runnable mOnSwipeFromRight = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$1$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.C18611.this.onSwipeFromRight();
            }
        };
        public Runnable mOnSwipeFromBottom = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$1$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.C18611.this.onSwipeFromBottom();
            }
        };

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onDebug() {
        }

        public C18611() {
        }

        public final Insets getControllableInsets(WindowState windowState) {
            if (windowState == null) {
                return Insets.NONE;
            }
            InsetsSourceProvider controllableInsetProvider = windowState.getControllableInsetProvider();
            if (controllableInsetProvider == null) {
                return Insets.NONE;
            }
            return controllableInsetProvider.getSource().calculateInsets(windowState.getBounds(), true);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onSwipeFromTop() {
            synchronized (DisplayPolicy.this.mLock) {
                DisplayPolicy displayPolicy = DisplayPolicy.this;
                displayPolicy.requestTransientBars(displayPolicy.mTopGestureHost, getControllableInsets(DisplayPolicy.this.mTopGestureHost).top > 0);
            }
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onSwipeFromBottom() {
            synchronized (DisplayPolicy.this.mLock) {
                DisplayPolicy displayPolicy = DisplayPolicy.this;
                displayPolicy.requestTransientBars(displayPolicy.mBottomGestureHost, getControllableInsets(DisplayPolicy.this.mBottomGestureHost).bottom > 0);
            }
        }

        public final boolean allowsSideSwipe(Region region) {
            return DisplayPolicy.this.mNavigationBarAlwaysShowOnSideGesture && !DisplayPolicy.this.mSystemGestures.currentGestureStartedInRegion(region);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onSwipeFromRight() {
            Region obtain = Region.obtain();
            synchronized (DisplayPolicy.this.mLock) {
                DisplayPolicy.this.mDisplayContent.calculateSystemGestureExclusion(obtain, null);
                boolean z = getControllableInsets(DisplayPolicy.this.mRightGestureHost).right > 0;
                if (z || allowsSideSwipe(obtain)) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mRightGestureHost, z);
                }
            }
            obtain.recycle();
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onSwipeFromLeft() {
            Region obtain = Region.obtain();
            synchronized (DisplayPolicy.this.mLock) {
                DisplayPolicy.this.mDisplayContent.calculateSystemGestureExclusion(obtain, null);
                boolean z = getControllableInsets(DisplayPolicy.this.mLeftGestureHost).left > 0;
                if (z || allowsSideSwipe(obtain)) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mLeftGestureHost, z);
                }
            }
            obtain.recycle();
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onFling(int i) {
            if (DisplayPolicy.this.mService.mPowerManagerInternal != null) {
                DisplayPolicy.this.mService.mPowerManagerInternal.setPowerBoost(0, i);
            }
        }

        public final WindowOrientationListener getOrientationListener() {
            DisplayRotation displayRotation = DisplayPolicy.this.mDisplayContent.getDisplayRotation();
            if (displayRotation != null) {
                return displayRotation.getOrientationListener();
            }
            return null;
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onDown() {
            WindowOrientationListener orientationListener = getOrientationListener();
            if (orientationListener != null) {
                orientationListener.onTouchStart();
            }
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onUpOrCancel() {
            WindowOrientationListener orientationListener = getOrientationListener();
            if (orientationListener != null) {
                orientationListener.onTouchEnd();
            }
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseHoverAtLeft() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromLeft);
            DisplayPolicy.this.mHandler.postDelayed(this.mOnSwipeFromLeft, 500L);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseHoverAtTop() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromTop);
            DisplayPolicy.this.mHandler.postDelayed(this.mOnSwipeFromTop, 500L);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseHoverAtRight() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromRight);
            DisplayPolicy.this.mHandler.postDelayed(this.mOnSwipeFromRight, 500L);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseHoverAtBottom() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromBottom);
            DisplayPolicy.this.mHandler.postDelayed(this.mOnSwipeFromBottom, 500L);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseLeaveFromLeft() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromLeft);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseLeaveFromTop() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromTop);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseLeaveFromRight() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromRight);
        }

        @Override // com.android.server.p014wm.SystemGesturesPointerEventListener.Callbacks
        public void onMouseLeaveFromBottom() {
            DisplayPolicy.this.mHandler.removeCallbacks(this.mOnSwipeFromBottom);
        }
    }

    /* renamed from: com.android.server.wm.DisplayPolicy$2 */
    /* loaded from: classes2.dex */
    public class C18622 extends WindowManagerInternal.AppTransitionListener {
        public Runnable mAppTransitionCancelled;
        public Runnable mAppTransitionFinished;
        public Runnable mAppTransitionPending;
        public final /* synthetic */ int val$displayId;

        public C18622(final int i) {
            this.val$displayId = i;
            this.mAppTransitionPending = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPolicy.C18622.this.lambda$$0(i);
                }
            };
            this.mAppTransitionCancelled = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPolicy.C18622.this.lambda$$1(i);
                }
            };
            this.mAppTransitionFinished = new Runnable() { // from class: com.android.server.wm.DisplayPolicy$2$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPolicy.C18622.this.lambda$$2(i);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$$0(int i) {
            StatusBarManagerInternal statusBarManagerInternal = DisplayPolicy.this.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.appTransitionPending(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$$1(int i) {
            StatusBarManagerInternal statusBarManagerInternal = DisplayPolicy.this.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.appTransitionCancelled(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$$2(int i) {
            StatusBarManagerInternal statusBarManagerInternal = DisplayPolicy.this.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.appTransitionFinished(i);
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public void onAppTransitionPendingLocked() {
            DisplayPolicy.this.mHandler.post(this.mAppTransitionPending);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public int onAppTransitionStartingLocked(final long j, final long j2) {
            DisplayPolicy.this.mHandler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$2$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPolicy.C18622.this.lambda$onAppTransitionStartingLocked$3(j, j2);
                }
            });
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAppTransitionStartingLocked$3(long j, long j2) {
            StatusBarManagerInternal statusBarManagerInternal = DisplayPolicy.this.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.appTransitionStarting(DisplayPolicy.this.mContext.getDisplayId(), j, j2);
            }
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public void onAppTransitionCancelledLocked(boolean z) {
            DisplayPolicy.this.mHandler.post(this.mAppTransitionCancelled);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AppTransitionListener
        public void onAppTransitionFinishedLocked(IBinder iBinder) {
            DisplayPolicy.this.mHandler.post(this.mAppTransitionFinished);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mLock) {
            onConfigurationChanged();
            this.mSystemGestures.onConfigurationChanged();
            this.mDisplayContent.updateSystemGestureExclusion();
        }
    }

    public final void updateForceShowNavBarSettings() {
        synchronized (this.mLock) {
            this.mForceShowNavigationBarEnabled = this.mForceShowNavBarSettingsObserver.isEnabled();
            updateSystemBarAttributes();
        }
    }

    public void systemReady() {
        this.mSystemGestures.systemReady();
        if (this.mService.mPointerLocationEnabled) {
            setPointerLocationEnabled(true);
        }
    }

    public final int getDisplayId() {
        return this.mDisplayContent.getDisplayId();
    }

    public void setHdmiPlugged(boolean z) {
        setHdmiPlugged(z, false);
    }

    public void setHdmiPlugged(boolean z, boolean z2) {
        if (z2 || this.mHdmiPlugged != z) {
            this.mHdmiPlugged = z;
            this.mService.updateRotation(true, true);
            Intent intent = new Intent("android.intent.action.HDMI_PLUGGED");
            intent.addFlags(67108864);
            intent.putExtra("state", z);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    public boolean isHdmiPlugged() {
        return this.mHdmiPlugged;
    }

    public boolean isCarDockEnablesAccelerometer() {
        return this.mCarDockEnablesAccelerometer;
    }

    public boolean isDeskDockEnablesAccelerometer() {
        return this.mDeskDockEnablesAccelerometer;
    }

    public void setPersistentVrModeEnabled(boolean z) {
        this.mPersistentVrModeEnabled = z;
    }

    public boolean isPersistentVrModeEnabled() {
        return this.mPersistentVrModeEnabled;
    }

    public void setDockMode(int i) {
        this.mDockMode = i;
    }

    public int getDockMode() {
        return this.mDockMode;
    }

    public boolean hasNavigationBar() {
        return this.mHasNavigationBar;
    }

    public boolean hasSideGestures() {
        return this.mHasNavigationBar && (this.mLeftGestureInset > 0 || this.mRightGestureInset > 0);
    }

    public boolean navigationBarCanMove() {
        return this.mNavigationBarCanMove;
    }

    public void setLidState(int i) {
        this.mLidState = i;
    }

    public int getLidState() {
        return this.mLidState;
    }

    public void setAwake(boolean z) {
        synchronized (this.mLock) {
            if (z == this.mAwake) {
                return;
            }
            this.mAwake = z;
            if (this.mDisplayContent.isDefaultDisplay) {
                this.mService.mAtmService.mKeyguardController.updateDeferTransitionForAod(this.mAwake);
            }
        }
    }

    public boolean isAwake() {
        return this.mAwake;
    }

    public boolean isScreenOnEarly() {
        return this.mScreenOnEarly;
    }

    public boolean isScreenOnFully() {
        return this.mScreenOnFully;
    }

    public boolean isKeyguardDrawComplete() {
        return this.mKeyguardDrawComplete;
    }

    public boolean isWindowManagerDrawComplete() {
        return this.mWindowManagerDrawComplete;
    }

    public boolean isForceShowNavigationBarEnabled() {
        return this.mForceShowNavigationBarEnabled;
    }

    public WindowManagerPolicy.ScreenOnListener getScreenOnListener() {
        return this.mScreenOnListener;
    }

    public boolean isRemoteInsetsControllerControllingSystemBars() {
        return this.mRemoteInsetsControllerControlsSystemBars;
    }

    @VisibleForTesting
    public void setRemoteInsetsControllerControlsSystemBars(boolean z) {
        this.mRemoteInsetsControllerControlsSystemBars = z;
    }

    public void screenTurnedOn(WindowManagerPolicy.ScreenOnListener screenOnListener) {
        synchronized (this.mLock) {
            this.mScreenOnEarly = true;
            this.mScreenOnFully = false;
            this.mKeyguardDrawComplete = false;
            this.mWindowManagerDrawComplete = false;
            this.mScreenOnListener = screenOnListener;
        }
    }

    public void screenTurnedOff() {
        synchronized (this.mLock) {
            this.mScreenOnEarly = false;
            this.mScreenOnFully = false;
            this.mKeyguardDrawComplete = false;
            this.mWindowManagerDrawComplete = false;
            this.mScreenOnListener = null;
        }
    }

    public boolean finishKeyguardDrawn() {
        synchronized (this.mLock) {
            if (this.mScreenOnEarly && !this.mKeyguardDrawComplete) {
                this.mKeyguardDrawComplete = true;
                this.mWindowManagerDrawComplete = false;
                return true;
            }
            return false;
        }
    }

    public boolean finishWindowsDrawn() {
        synchronized (this.mLock) {
            if (this.mScreenOnEarly && !this.mWindowManagerDrawComplete) {
                this.mWindowManagerDrawComplete = true;
                return true;
            }
            return false;
        }
    }

    public boolean finishScreenTurningOn() {
        synchronized (this.mLock) {
            if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 1865125884, 1023, (String) null, new Object[]{Boolean.valueOf(this.mAwake), Boolean.valueOf(this.mScreenOnEarly), Boolean.valueOf(this.mScreenOnFully), Boolean.valueOf(this.mKeyguardDrawComplete), Boolean.valueOf(this.mWindowManagerDrawComplete)});
            }
            if (!this.mScreenOnFully && this.mScreenOnEarly && this.mWindowManagerDrawComplete && (!this.mAwake || this.mKeyguardDrawComplete)) {
                if (ProtoLogCache.WM_DEBUG_SCREEN_ON_enabled) {
                    ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_SCREEN_ON, 1140424002, 0, (String) null, (Object[]) null);
                }
                this.mScreenOnListener = null;
                this.mScreenOnFully = true;
                return true;
            }
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0013, code lost:
        if (r0 != 2006) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void adjustWindowParamsLw(WindowState windowState, WindowManager.LayoutParams layoutParams) {
        ActivityRecord activityRecord;
        int i = layoutParams.type;
        if (i != 1) {
            if (i != 2013) {
                if (i != 2015) {
                    if (i == 2005) {
                        long j = layoutParams.hideTimeoutMilliseconds;
                        if (j < 0 || j > 4100) {
                            layoutParams.hideTimeoutMilliseconds = 4100L;
                        }
                        layoutParams.hideTimeoutMilliseconds = this.mAccessibilityManager.getRecommendedTimeoutMillis((int) layoutParams.hideTimeoutMilliseconds, 2);
                        layoutParams.flags |= 16;
                    }
                }
                layoutParams.flags = (layoutParams.flags | 24) & (-262145);
            } else {
                layoutParams.layoutInDisplayCutoutMode = 3;
            }
        } else if (layoutParams.isFullscreen() && (activityRecord = windowState.mActivityRecord) != null && activityRecord.fillsParent() && (windowState.mAttrs.privateFlags & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0 && layoutParams.getFitInsetsTypes() != 0) {
            throw new IllegalArgumentException("Illegal attributes: Main activity window that isn't translucent trying to fit insets: " + layoutParams.getFitInsetsTypes() + " attrs=" + layoutParams);
        }
        if (WindowManager.LayoutParams.isSystemAlertWindowType(layoutParams.type)) {
            float f = this.mService.mMaximumObscuringOpacityForTouch;
            if (layoutParams.alpha > f && (layoutParams.flags & 16) != 0 && (layoutParams.privateFlags & 536870912) == 0) {
                Slog.w(StartingSurfaceController.TAG, String.format("App %s has a system alert window (type = %d) with FLAG_NOT_TOUCHABLE and LayoutParams.alpha = %.2f > %.2f, setting alpha to %.2f to let touches pass through (if this is isn't desirable, remove flag FLAG_NOT_TOUCHABLE).", layoutParams.packageName, Integer.valueOf(layoutParams.type), Float.valueOf(layoutParams.alpha), Float.valueOf(f), Float.valueOf(f)));
                layoutParams.alpha = f;
                windowState.mWinAnimator.mAlpha = f;
            }
        }
        if (!windowState.mSession.mCanSetUnrestrictedGestureExclusion) {
            layoutParams.privateFlags &= -33;
        }
        InsetsSourceProvider controllableInsetProvider = windowState.getControllableInsetProvider();
        if (controllableInsetProvider == null || controllableInsetProvider.getSource().insetsRoundedCornerFrame() == layoutParams.insetsRoundedCornerFrame) {
            return;
        }
        controllableInsetProvider.getSource().setInsetsRoundedCornerFrame(layoutParams.insetsRoundedCornerFrame);
    }

    public void setDropInputModePolicy(WindowState windowState, WindowManager.LayoutParams layoutParams) {
        if (layoutParams.type == 2005 && (layoutParams.privateFlags & 536870912) == 0) {
            this.mService.mTransactionFactory.get().setDropInputMode(windowState.getSurfaceControl(), 1).apply();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x003d, code lost:
        if (r0 != 2041) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int validateAddingWindowLw(WindowManager.LayoutParams layoutParams, int i, int i2) {
        if ((layoutParams.privateFlags & 536870912) != 0) {
            this.mContext.enforcePermission("android.permission.INTERNAL_SYSTEM_WINDOW", i, i2, "DisplayPolicy");
        }
        if ((layoutParams.privateFlags & Integer.MIN_VALUE) != 0) {
            ActivityTaskManagerService.enforceTaskPermission("DisplayPolicy");
        }
        int i3 = layoutParams.type;
        if (i3 == 2000) {
            this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
            WindowState windowState = this.mStatusBar;
            if (windowState != null && windowState.isAlive()) {
                return -7;
            }
        } else if (i3 == 2014) {
            return -10;
        } else {
            if (i3 != 2017) {
                if (i3 == 2019) {
                    this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
                    WindowState windowState2 = this.mNavigationBar;
                    if (windowState2 != null && windowState2.isAlive()) {
                        return -7;
                    }
                } else if (i3 != 2024) {
                    if (i3 != 2033) {
                        if (i3 == 2040) {
                            this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
                            if (this.mNotificationShade != null && this.mNotificationShade.isAlive()) {
                                return -7;
                            }
                        }
                    }
                } else if (!this.mService.mAtmService.isCallerRecents(i2)) {
                    this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
                }
            }
            this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
        }
        if (layoutParams.providedInsets == null || this.mService.mAtmService.isCallerRecents(i2)) {
            return 0;
        }
        this.mContext.enforcePermission("android.permission.STATUS_BAR_SERVICE", i, i2, "DisplayPolicy");
        return 0;
    }

    public void addWindowLw(WindowState windowState, WindowManager.LayoutParams layoutParams) {
        SparseArray<TriConsumer<DisplayFrames, WindowContainer, Rect>> sparseArray;
        int i = layoutParams.type;
        if (i == 2000) {
            this.mStatusBar = windowState;
        } else if (i == 2019) {
            this.mNavigationBar = windowState;
        } else if (i == 2040) {
            this.mNotificationShade = windowState;
        }
        InsetsFrameProvider[] insetsFrameProviderArr = layoutParams.providedInsets;
        if (insetsFrameProviderArr != null) {
            for (int length = insetsFrameProviderArr.length - 1; length >= 0; length--) {
                InsetsFrameProvider insetsFrameProvider = layoutParams.providedInsets[length];
                TriConsumer<DisplayFrames, WindowContainer, Rect> frameProvider = getFrameProvider(windowState, insetsFrameProvider, length);
                InsetsFrameProvider.InsetsSizeOverride[] insetsSizeOverrides = insetsFrameProvider.getInsetsSizeOverrides();
                if (insetsSizeOverrides != null) {
                    sparseArray = new SparseArray<>();
                    for (int length2 = insetsSizeOverrides.length - 1; length2 >= 0; length2--) {
                        sparseArray.put(insetsSizeOverrides[length2].getWindowType(), getOverrideFrameProvider(windowState, length, length2));
                    }
                } else {
                    sparseArray = null;
                }
                int type = insetsFrameProvider.getType();
                this.mDisplayContent.getInsetsStateController().getOrCreateSourceProvider(InsetsSource.createId(insetsFrameProvider.getOwner(), insetsFrameProvider.getIndex(), type), type).setWindowContainer(windowState, frameProvider, sparseArray);
                this.mInsetsSourceWindowsExceptIme.add(windowState);
            }
        }
    }

    public final TriConsumer<DisplayFrames, WindowContainer, Rect> getFrameProvider(final WindowState windowState, InsetsFrameProvider insetsFrameProvider, final int i) {
        if (insetsFrameProvider.getInsetsSize() == null && insetsFrameProvider.getSource() == 2) {
            return null;
        }
        return new TriConsumer() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda4
            public final void accept(Object obj, Object obj2, Object obj3) {
                DisplayPolicy.lambda$getFrameProvider$1(WindowState.this, i, (DisplayFrames) obj, (WindowContainer) obj2, (Rect) obj3);
            }
        };
    }

    public static /* synthetic */ void lambda$getFrameProvider$1(WindowState windowState, int i, DisplayFrames displayFrames, WindowContainer windowContainer, Rect rect) {
        WindowManager.LayoutParams forRotation = windowState.mAttrs.forRotation(displayFrames.mRotation);
        InsetsFrameProvider insetsFrameProvider = forRotation.providedInsets[i];
        InsetsFrameProvider.calculateInsetsFrame(displayFrames.mUnrestricted, windowContainer.getBounds(), displayFrames.mDisplayCutoutSafe, rect, insetsFrameProvider.getSource(), insetsFrameProvider.getInsetsSize(), forRotation.privateFlags, insetsFrameProvider.getMinimalInsetsSizeInDisplayCutoutSafe());
    }

    public final TriConsumer<DisplayFrames, WindowContainer, Rect> getOverrideFrameProvider(final WindowState windowState, final int i, final int i2) {
        return new TriConsumer() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda3
            public final void accept(Object obj, Object obj2, Object obj3) {
                DisplayPolicy.lambda$getOverrideFrameProvider$2(WindowState.this, i, i2, (DisplayFrames) obj, (WindowContainer) obj2, (Rect) obj3);
            }
        };
    }

    public static /* synthetic */ void lambda$getOverrideFrameProvider$2(WindowState windowState, int i, int i2, DisplayFrames displayFrames, WindowContainer windowContainer, Rect rect) {
        WindowManager.LayoutParams forRotation = windowState.mAttrs.forRotation(displayFrames.mRotation);
        InsetsFrameProvider insetsFrameProvider = forRotation.providedInsets[i];
        InsetsFrameProvider.calculateInsetsFrame(displayFrames.mUnrestricted, windowContainer.getBounds(), displayFrames.mDisplayCutoutSafe, rect, insetsFrameProvider.getSource(), insetsFrameProvider.getInsetsSizeOverrides()[i2].getInsetsSize(), forRotation.privateFlags, (Insets) null);
    }

    public TriConsumer<DisplayFrames, WindowContainer, Rect> getImeSourceFrameProvider() {
        return new TriConsumer() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda5
            public final void accept(Object obj, Object obj2, Object obj3) {
                DisplayPolicy.this.lambda$getImeSourceFrameProvider$3((DisplayFrames) obj, (WindowContainer) obj2, (Rect) obj3);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getImeSourceFrameProvider$3(DisplayFrames displayFrames, WindowContainer windowContainer, Rect rect) {
        WindowState asWindowState = windowContainer.asWindowState();
        if (asWindowState == null) {
            throw new IllegalArgumentException("IME insets must be provided by a window.");
        }
        if (this.mNavigationBar != null && navigationBarPosition(displayFrames.mRotation) == 4) {
            Rect rect2 = sTmpRect;
            rect2.set(rect);
            rect2.intersectUnchecked(this.mNavigationBar.getFrame());
            rect.inset(asWindowState.mGivenContentInsets);
            rect.union(rect2);
            return;
        }
        rect.inset(asWindowState.mGivenContentInsets);
    }

    public void removeWindowLw(WindowState windowState) {
        if (this.mStatusBar == windowState) {
            this.mStatusBar = null;
        } else if (this.mNavigationBar == windowState) {
            this.mNavigationBar = null;
        } else if (this.mNotificationShade == windowState) {
            this.mNotificationShade = null;
        }
        if (this.mLastFocusedWindow == windowState) {
            this.mLastFocusedWindow = null;
        }
        if (windowState.hasInsetsSourceProvider()) {
            SparseArray<InsetsSourceProvider> insetsSourceProviders = windowState.getInsetsSourceProviders();
            InsetsStateController insetsStateController = this.mDisplayContent.getInsetsStateController();
            for (int size = insetsSourceProviders.size() - 1; size >= 0; size--) {
                InsetsSourceProvider valueAt = insetsSourceProviders.valueAt(size);
                valueAt.setWindowContainer(null, null, null);
                insetsStateController.removeSourceProvider(valueAt.getSource().getId());
            }
        }
        this.mInsetsSourceWindowsExceptIme.remove(windowState);
    }

    public WindowState getStatusBar() {
        return this.mStatusBar;
    }

    public WindowState getNotificationShade() {
        return this.mNotificationShade;
    }

    public WindowState getNavigationBar() {
        return this.mNavigationBar;
    }

    public int selectAnimation(WindowState windowState, int i) {
        if (ProtoLogCache.WM_DEBUG_ANIM_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ANIM, 341360111, 4, (String) null, new Object[]{String.valueOf(windowState), Long.valueOf(i)});
        }
        if (i == 5 && windowState.hasAppShownWindows()) {
            if (windowState.isActivityTypeHome()) {
                return -1;
            }
            if (ProtoLogCache.WM_DEBUG_ANIM_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ANIM, -1303628829, 0, (String) null, (Object[]) null);
                return 17432595;
            }
            return 17432595;
        }
        return 0;
    }

    public boolean areSystemBarsForcedConsumedLw() {
        return this.mForceConsumeSystemBars;
    }

    public boolean areSystemBarsForcedShownLw() {
        return this.mForceShowSystemBars;
    }

    public void simulateLayoutDisplay(DisplayFrames displayFrames) {
        sTmpClientFrames.attachedFrame = null;
        for (int size = this.mInsetsSourceWindowsExceptIme.size() - 1; size >= 0; size--) {
            WindowState valueAt = this.mInsetsSourceWindowsExceptIme.valueAt(size);
            this.mWindowLayout.computeFrames(valueAt.mAttrs.forRotation(displayFrames.mRotation), displayFrames.mInsetsState, displayFrames.mDisplayCutoutSafe, displayFrames.mUnrestricted, valueAt.getWindowingMode(), -1, -1, valueAt.getRequestedVisibleTypes(), valueAt.mGlobalScale, sTmpClientFrames);
            SparseArray<InsetsSourceProvider> insetsSourceProviders = valueAt.getInsetsSourceProviders();
            InsetsState insetsState = displayFrames.mInsetsState;
            for (int size2 = insetsSourceProviders.size() - 1; size2 >= 0; size2--) {
                insetsState.addSource(insetsSourceProviders.valueAt(size2).createSimulatedSource(displayFrames, sTmpClientFrames.frame));
            }
        }
    }

    public void onDisplayInfoChanged(DisplayInfo displayInfo) {
        this.mSystemGestures.onDisplayInfoChanged(displayInfo);
    }

    public void layoutWindowLw(WindowState windowState, WindowState windowState2, DisplayFrames displayFrames) {
        DisplayPolicy displayPolicy;
        int i;
        if (windowState.skipLayout()) {
            return;
        }
        DisplayFrames displayFrames2 = windowState.getDisplayFrames(displayFrames);
        WindowManager.LayoutParams forRotation = windowState.mAttrs.forRotation(displayFrames2.mRotation);
        ClientWindowFrames clientWindowFrames = sTmpClientFrames;
        clientWindowFrames.attachedFrame = windowState2 != null ? windowState2.getFrame() : null;
        boolean z = forRotation == windowState.mAttrs;
        int i2 = z ? windowState.mRequestedWidth : -1;
        if (z) {
            i = windowState.mRequestedHeight;
            displayPolicy = this;
        } else {
            displayPolicy = this;
            i = -1;
        }
        displayPolicy.mWindowLayout.computeFrames(forRotation, windowState.getInsetsState(), displayFrames2.mDisplayCutoutSafe, windowState.getBounds(), windowState.getWindowingMode(), i2, i, windowState.getRequestedVisibleTypes(), windowState.mGlobalScale, clientWindowFrames);
        windowState.setFrames(clientWindowFrames, windowState.mRequestedWidth, windowState.mRequestedHeight);
    }

    public WindowState getTopFullscreenOpaqueWindow() {
        return this.mTopFullscreenOpaqueWindowState;
    }

    public boolean isTopLayoutFullscreen() {
        return this.mTopIsFullscreen;
    }

    public void beginPostLayoutPolicyLw() {
        this.mLeftGestureHost = null;
        this.mTopGestureHost = null;
        this.mRightGestureHost = null;
        this.mBottomGestureHost = null;
        this.mTopFullscreenOpaqueWindowState = null;
        this.mNavBarColorWindowCandidate = null;
        this.mNavBarBackgroundWindow = null;
        this.mStatusBarAppearanceRegionList.clear();
        this.mLetterboxDetails.clear();
        this.mStatusBarBackgroundWindows.clear();
        this.mStatusBarColorCheckedBounds.setEmpty();
        this.mStatusBarBackgroundCheckedBounds.setEmpty();
        this.mSystemBarColorApps.clear();
        this.mAllowLockscreenWhenOn = false;
        this.mShowingDream = false;
        this.mIsFreeformWindowOverlappingWithNavBar = false;
    }

    public void applyPostLayoutPolicyLw(WindowState windowState, WindowManager.LayoutParams layoutParams, WindowState windowState2, WindowState windowState3) {
        LetterboxDetails letterboxDetails;
        if (layoutParams.type == 2019) {
            this.mNavigationBarPosition = navigationBarPosition(this.mDisplayContent.mDisplayFrames.mRotation);
        }
        boolean canAffectSystemUiFlags = windowState.canAffectSystemUiFlags();
        applyKeyguardPolicy(windowState, windowState3);
        if (!this.mIsFreeformWindowOverlappingWithNavBar && windowState.inFreeformWindowingMode() && windowState.mActivityRecord != null && isOverlappingWithNavBar(windowState)) {
            this.mIsFreeformWindowOverlappingWithNavBar = true;
        }
        boolean z = false;
        if (windowState.hasInsetsSourceProvider()) {
            SparseArray<InsetsSourceProvider> insetsSourceProviders = windowState.getInsetsSourceProviders();
            Rect bounds = windowState.getBounds();
            for (int size = insetsSourceProviders.size() - 1; size >= 0; size--) {
                InsetsSource source = insetsSourceProviders.valueAt(size).getSource();
                if ((source.getType() & (WindowInsets.Type.systemGestures() | WindowInsets.Type.mandatorySystemGestures())) != 0 && (this.mLeftGestureHost == null || this.mTopGestureHost == null || this.mRightGestureHost == null || this.mBottomGestureHost == null)) {
                    Insets calculateInsets = source.calculateInsets(bounds, false);
                    if (this.mLeftGestureHost == null && calculateInsets.left > 0) {
                        this.mLeftGestureHost = windowState;
                    }
                    if (this.mTopGestureHost == null && calculateInsets.top > 0) {
                        this.mTopGestureHost = windowState;
                    }
                    if (this.mRightGestureHost == null && calculateInsets.right > 0) {
                        this.mRightGestureHost = windowState;
                    }
                    if (this.mBottomGestureHost == null && calculateInsets.bottom > 0) {
                        this.mBottomGestureHost = windowState;
                    }
                }
            }
        }
        if (canAffectSystemUiFlags) {
            int i = layoutParams.type;
            if (i >= 1 && i < 2000) {
                z = true;
            }
            if (this.mTopFullscreenOpaqueWindowState == null) {
                int i2 = layoutParams.flags;
                if (windowState.isDreamWindow() && (!this.mDreamingLockscreen || (windowState.isVisible() && windowState.hasDrawn()))) {
                    this.mShowingDream = true;
                    z = true;
                }
                if (z && windowState2 == null && layoutParams.isFullscreen() && (i2 & 1) != 0) {
                    this.mAllowLockscreenWhenOn = true;
                }
            }
            if ((z && windowState2 == null && layoutParams.isFullscreen()) || layoutParams.type == 2031) {
                if (this.mTopFullscreenOpaqueWindowState == null) {
                    this.mTopFullscreenOpaqueWindowState = windowState;
                }
                if (this.mStatusBar != null) {
                    Rect rect = sTmpRect;
                    if (rect.setIntersect(windowState.getFrame(), this.mStatusBar.getFrame()) && !this.mStatusBarBackgroundCheckedBounds.contains(rect)) {
                        this.mStatusBarBackgroundWindows.add(windowState);
                        this.mStatusBarBackgroundCheckedBounds.union(rect);
                        if (!this.mStatusBarColorCheckedBounds.contains(rect)) {
                            this.mStatusBarAppearanceRegionList.add(new AppearanceRegion(windowState.mAttrs.insetsFlags.appearance & 8, new Rect(windowState.getFrame())));
                            this.mStatusBarColorCheckedBounds.union(rect);
                            addSystemBarColorApp(windowState);
                        }
                    }
                }
                if (isOverlappingWithNavBar(windowState)) {
                    if (this.mNavBarColorWindowCandidate == null) {
                        this.mNavBarColorWindowCandidate = windowState;
                        addSystemBarColorApp(windowState);
                    }
                    if (this.mNavBarBackgroundWindow == null) {
                        this.mNavBarBackgroundWindow = windowState;
                    }
                }
                ActivityRecord activityRecord = windowState.getActivityRecord();
                if (activityRecord == null || (letterboxDetails = activityRecord.mLetterboxUiController.getLetterboxDetails()) == null) {
                    return;
                }
                this.mLetterboxDetails.add(letterboxDetails);
            } else if (windowState.isDimming()) {
                WindowState windowState4 = this.mStatusBar;
                if (windowState4 != null && addStatusBarAppearanceRegionsForDimmingWindow(windowState.mAttrs.insetsFlags.appearance & 8, windowState4.getFrame(), windowState.getBounds(), windowState.getFrame())) {
                    addSystemBarColorApp(windowState);
                }
                if (isOverlappingWithNavBar(windowState) && this.mNavBarColorWindowCandidate == null) {
                    this.mNavBarColorWindowCandidate = windowState;
                }
            }
        }
    }

    public final boolean addStatusBarAppearanceRegionsForDimmingWindow(int i, Rect rect, Rect rect2, Rect rect3) {
        Rect rect4 = sTmpRect;
        if (rect4.setIntersect(rect2, rect) && !this.mStatusBarColorCheckedBounds.contains(rect4)) {
            if (i != 0) {
                Rect rect5 = sTmpRect2;
                if (rect5.setIntersect(rect3, rect)) {
                    this.mStatusBarAppearanceRegionList.add(new AppearanceRegion(i, new Rect(rect3)));
                    if (!rect4.equals(rect5) && rect4.height() == rect5.height()) {
                        if (rect4.left != rect5.left) {
                            this.mStatusBarAppearanceRegionList.add(new AppearanceRegion(0, new Rect(rect2.left, rect2.top, rect5.left, rect2.bottom)));
                        }
                        if (rect4.right != rect5.right) {
                            this.mStatusBarAppearanceRegionList.add(new AppearanceRegion(0, new Rect(rect5.right, rect2.top, rect2.right, rect2.bottom)));
                        }
                    }
                    this.mStatusBarColorCheckedBounds.union(rect4);
                    return true;
                }
            }
            this.mStatusBarAppearanceRegionList.add(new AppearanceRegion(0, new Rect(rect2)));
            this.mStatusBarColorCheckedBounds.union(rect4);
            return true;
        }
        return false;
    }

    public final void addSystemBarColorApp(WindowState windowState) {
        ActivityRecord activityRecord = windowState.mActivityRecord;
        if (activityRecord != null) {
            this.mSystemBarColorApps.add(activityRecord);
        }
    }

    public void finishPostLayoutPolicyLw() {
        if (!this.mShowingDream) {
            this.mDreamingLockscreen = this.mService.mPolicy.isKeyguardShowingAndNotOccluded();
        }
        updateSystemBarAttributes();
        boolean z = this.mShowingDream;
        if (z != this.mLastShowingDream) {
            this.mLastShowingDream = z;
            this.mDisplayContent.notifyKeyguardFlagsChanged();
        }
        this.mService.mPolicy.setAllowLockscreenWhenOn(getDisplayId(), this.mAllowLockscreenWhenOn);
    }

    public final void applyKeyguardPolicy(WindowState windowState, WindowState windowState2) {
        if (windowState.canBeHiddenByKeyguard()) {
            boolean shouldBeHiddenByKeyguard = shouldBeHiddenByKeyguard(windowState, windowState2);
            if (windowState.mIsImWindow) {
                this.mDisplayContent.getInsetsStateController().getImeSourceProvider().setFrozen(shouldBeHiddenByKeyguard);
            }
            if (shouldBeHiddenByKeyguard) {
                windowState.hide(false, true);
            } else {
                windowState.show(false, true);
            }
        }
    }

    public final boolean shouldBeHiddenByKeyguard(WindowState windowState, WindowState windowState2) {
        boolean z = false;
        if (windowState.mIsImWindow && (this.mDisplayContent.isAodShowing() || (this.mDisplayContent.isDefaultDisplay && !this.mWindowManagerDrawComplete))) {
            return true;
        }
        if (this.mDisplayContent.isDefaultDisplay && isKeyguardShowing()) {
            if (windowState2 != null && windowState2.isVisible() && windowState.mIsImWindow && (windowState2.canShowWhenLocked() || !windowState2.canBeHiddenByKeyguard())) {
                return false;
            }
            if (isKeyguardOccluded() && (windowState.canShowWhenLocked() || (windowState.mAttrs.privateFlags & 256) != 0)) {
                z = true;
            }
            return !z;
        }
        return false;
    }

    public boolean topAppHidesSystemBar(int i) {
        WindowState windowState = this.mTopFullscreenOpaqueWindowState;
        if (windowState == null || this.mForceShowSystemBars) {
            return false;
        }
        return !windowState.isRequestedVisible(i);
    }

    public void switchUser() {
        updateCurrentUserResources();
        updateForceShowNavBarSettings();
    }

    public void onOverlayChanged() {
        updateCurrentUserResources();
        this.mDisplayContent.updateDisplayInfo();
        onConfigurationChanged();
        this.mSystemGestures.onConfigurationChanged();
    }

    public void onConfigurationChanged() {
        DisplayRotation displayRotation = this.mDisplayContent.getDisplayRotation();
        Resources currentUserResources = getCurrentUserResources();
        displayRotation.getPortraitRotation();
        this.mNavBarOpacityMode = currentUserResources.getInteger(17694900);
        this.mLeftGestureInset = this.mGestureNavigationSettingsObserver.getLeftSensitivity(currentUserResources);
        this.mRightGestureInset = this.mGestureNavigationSettingsObserver.getRightSensitivity(currentUserResources);
        this.mNavigationBarAlwaysShowOnSideGesture = currentUserResources.getBoolean(17891745);
        this.mRemoteInsetsControllerControlsSystemBars = currentUserResources.getBoolean(17891334);
        updateConfigurationAndScreenSizeDependentBehaviors();
        boolean z = currentUserResources.getBoolean(17891371);
        if (this.mShouldAttachNavBarToAppDuringTransition != z) {
            this.mShouldAttachNavBarToAppDuringTransition = z;
        }
    }

    public void updateConfigurationAndScreenSizeDependentBehaviors() {
        Resources currentUserResources = getCurrentUserResources();
        DisplayContent displayContent = this.mDisplayContent;
        this.mNavigationBarCanMove = displayContent.mBaseDisplayWidth != displayContent.mBaseDisplayHeight && currentUserResources.getBoolean(17891746);
        this.mDisplayContent.getDisplayRotation().updateUserDependentConfiguration(currentUserResources);
    }

    public final void updateCurrentUserResources() {
        int currentUserId = this.mService.mAmInternal.getCurrentUserId();
        Context systemUiContext = getSystemUiContext();
        if (currentUserId == 0) {
            this.mCurrentUserResources = systemUiContext.getResources();
            return;
        }
        LoadedApk packageInfo = ActivityThread.currentActivityThread().getPackageInfo(systemUiContext.getPackageName(), (CompatibilityInfo) null, 0, currentUserId);
        this.mCurrentUserResources = ResourcesManager.getInstance().getResources(systemUiContext.getWindowContextToken(), packageInfo.getResDir(), (String[]) null, packageInfo.getOverlayDirs(), packageInfo.getOverlayPaths(), packageInfo.getApplicationInfo().sharedLibraryFiles, Integer.valueOf(this.mDisplayContent.getDisplayId()), (Configuration) null, systemUiContext.getResources().getCompatibilityInfo(), (ClassLoader) null, (List) null);
    }

    @VisibleForTesting
    public Resources getCurrentUserResources() {
        if (this.mCurrentUserResources == null) {
            updateCurrentUserResources();
        }
        return this.mCurrentUserResources;
    }

    @VisibleForTesting
    public Context getContext() {
        return this.mContext;
    }

    public Context getSystemUiContext() {
        return this.mUiContext;
    }

    @VisibleForTesting
    public void setCanSystemBarsBeShownByUser(boolean z) {
        this.mCanSystemBarsBeShownByUser = z;
    }

    public void notifyDisplayReady() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.this.lambda$notifyDisplayReady$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyDisplayReady$4() {
        int displayId = getDisplayId();
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.onDisplayReady(displayId);
        }
        WallpaperManagerInternal wallpaperManagerInternal = (WallpaperManagerInternal) LocalServices.getService(WallpaperManagerInternal.class);
        if (wallpaperManagerInternal != null) {
            wallpaperManagerInternal.onDisplayReady(displayId);
        }
    }

    public float getWindowCornerRadius() {
        if (this.mDisplayContent.getDisplay().getType() == 1) {
            return ScreenDecorationsUtils.getWindowCornerRadius(this.mContext);
        }
        return 0.0f;
    }

    public boolean isShowingDreamLw() {
        return this.mShowingDream;
    }

    /* renamed from: com.android.server.wm.DisplayPolicy$DecorInsets */
    /* loaded from: classes2.dex */
    public static class DecorInsets {
        public static final int DECOR_TYPES = WindowInsets.Type.displayCutout() | WindowInsets.Type.navigationBars();
        public final DisplayContent mDisplayContent;
        public final Info[] mInfoForRotation;
        public final Info mTmpInfo = new Info();

        /* renamed from: com.android.server.wm.DisplayPolicy$DecorInsets$Info */
        /* loaded from: classes2.dex */
        public static class Info {
            public final Rect mNonDecorInsets = new Rect();
            public final Rect mConfigInsets = new Rect();
            public final Rect mNonDecorFrame = new Rect();
            public final Rect mConfigFrame = new Rect();
            public boolean mNeedUpdate = true;

            public void update(DisplayContent displayContent, int i, int i2, int i3) {
                DisplayFrames displayFrames = new DisplayFrames();
                displayContent.updateDisplayFrames(displayFrames, i, i2, i3);
                displayContent.getDisplayPolicy().simulateLayoutDisplay(displayFrames);
                InsetsState insetsState = displayFrames.mInsetsState;
                Rect displayFrame = insetsState.getDisplayFrame();
                Insets calculateInsets = insetsState.calculateInsets(displayFrame, DecorInsets.DECOR_TYPES, true);
                Insets calculateInsets2 = insetsState.calculateInsets(displayFrame, WindowInsets.Type.statusBars(), true);
                this.mNonDecorInsets.set(calculateInsets.left, calculateInsets.top, calculateInsets.right, calculateInsets.bottom);
                this.mConfigInsets.set(Math.max(calculateInsets2.left, calculateInsets.left), Math.max(calculateInsets2.top, calculateInsets.top), Math.max(calculateInsets2.right, calculateInsets.right), Math.max(calculateInsets2.bottom, calculateInsets.bottom));
                this.mNonDecorFrame.set(displayFrame);
                this.mNonDecorFrame.inset(this.mNonDecorInsets);
                this.mConfigFrame.set(displayFrame);
                this.mConfigFrame.inset(this.mConfigInsets);
                this.mNeedUpdate = false;
            }

            public void set(Info info) {
                this.mNonDecorInsets.set(info.mNonDecorInsets);
                this.mConfigInsets.set(info.mConfigInsets);
                this.mNonDecorFrame.set(info.mNonDecorFrame);
                this.mConfigFrame.set(info.mConfigFrame);
                this.mNeedUpdate = false;
            }

            public String toString() {
                return "{nonDecorInsets=" + this.mNonDecorInsets + ", configInsets=" + this.mConfigInsets + ", nonDecorFrame=" + this.mNonDecorFrame + ", configFrame=" + this.mConfigFrame + '}';
            }
        }

        public DecorInsets(DisplayContent displayContent) {
            Info[] infoArr = new Info[4];
            this.mInfoForRotation = infoArr;
            this.mDisplayContent = displayContent;
            for (int length = infoArr.length - 1; length >= 0; length--) {
                this.mInfoForRotation[length] = new Info();
            }
        }

        public Info get(int i, int i2, int i3) {
            Info info = this.mInfoForRotation[i];
            if (info.mNeedUpdate) {
                info.update(this.mDisplayContent, i, i2, i3);
            }
            return info;
        }

        public void invalidate() {
            for (Info info : this.mInfoForRotation) {
                info.mNeedUpdate = true;
            }
        }
    }

    public boolean updateDecorInsetsInfo() {
        DisplayContent displayContent = this.mDisplayContent;
        DisplayFrames displayFrames = displayContent.mDisplayFrames;
        int i = displayFrames.mRotation;
        int i2 = displayFrames.mWidth;
        int i3 = displayFrames.mHeight;
        DecorInsets.Info info = this.mDecorInsets.mTmpInfo;
        info.update(displayContent, i, i2, i3);
        if (info.mNonDecorFrame.equals(getDecorInsetsInfo(i, i2, i3).mNonDecorFrame)) {
            return false;
        }
        this.mDecorInsets.invalidate();
        this.mDecorInsets.mInfoForRotation[i].set(info);
        return true;
    }

    public DecorInsets.Info getDecorInsetsInfo(int i, int i2, int i3) {
        return this.mDecorInsets.get(i, i2, i3);
    }

    public int navigationBarPosition(int i) {
        WindowState windowState = this.mNavigationBar;
        if (windowState != null) {
            int i2 = windowState.mAttrs.forRotation(i).gravity;
            if (i2 != 3) {
                return i2 != 5 ? 4 : 2;
            }
            return 1;
        }
        return -1;
    }

    public void focusChangedLw(WindowState windowState, WindowState windowState2) {
        this.mFocusedWindow = windowState2;
        this.mLastFocusedWindow = windowState;
        if (this.mDisplayContent.isDefaultDisplay) {
            this.mService.mPolicy.onDefaultDisplayFocusChangedLw(windowState2);
        }
        updateSystemBarAttributes();
    }

    @VisibleForTesting
    public void requestTransientBars(WindowState windowState, boolean z) {
        if (windowState == null || !this.mService.mPolicy.isUserSetupComplete()) {
            return;
        }
        if (!this.mCanSystemBarsBeShownByUser) {
            Slog.d(StartingSurfaceController.TAG, "Remote insets controller disallows showing system bars - ignoring request");
            return;
        }
        InsetsSourceProvider controllableInsetProvider = windowState.getControllableInsetProvider();
        InsetsControlTarget controlTarget = controllableInsetProvider != null ? controllableInsetProvider.getControlTarget() : null;
        if (controlTarget == null || controlTarget == getNotificationShade()) {
            return;
        }
        int statusBars = (WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars()) & controlTarget.getRequestedVisibleTypes();
        InsetsSourceProvider controllableInsetProvider2 = windowState.getControllableInsetProvider();
        if (controllableInsetProvider2 != null && controllableInsetProvider2.getSource().getType() == WindowInsets.Type.navigationBars() && (WindowInsets.Type.navigationBars() & statusBars) != 0) {
            controlTarget.showInsets(WindowInsets.Type.navigationBars(), false, null);
            return;
        }
        if (controlTarget.canShowTransient()) {
            this.mDisplayContent.getInsetsPolicy().showTransient(SHOW_TYPES_FOR_SWIPE, z);
            controlTarget.showInsets(statusBars, false, null);
        } else {
            controlTarget.showInsets(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars(), false, null);
            WindowState windowState2 = this.mStatusBar;
            if (windowState == windowState2 && !windowState2.transferTouch()) {
                Slog.i(StartingSurfaceController.TAG, "Could not transfer touch to the status bar");
            }
        }
        this.mImmersiveModeConfirmation.confirmCurrentPrompt();
    }

    public boolean isKeyguardShowing() {
        return this.mService.mPolicy.isKeyguardShowing();
    }

    public final boolean isKeyguardOccluded() {
        return this.mService.mPolicy.isKeyguardOccluded();
    }

    public InsetsPolicy getInsetsPolicy() {
        return this.mDisplayContent.getInsetsPolicy();
    }

    public void addRelaunchingApp(ActivityRecord activityRecord) {
        if (!this.mSystemBarColorApps.contains(activityRecord) || activityRecord.hasStartingWindow()) {
            return;
        }
        this.mRelaunchingSystemBarColorApps.add(activityRecord);
    }

    public void removeRelaunchingApp(ActivityRecord activityRecord) {
        if (this.mRelaunchingSystemBarColorApps.remove(activityRecord) && this.mRelaunchingSystemBarColorApps.isEmpty()) {
            updateSystemBarAttributes();
        }
    }

    public void resetSystemBarAttributes() {
        this.mLastDisableFlags = 0;
        updateSystemBarAttributes();
    }

    public void updateSystemBarAttributes() {
        WindowState windowState = this.mFocusedWindow;
        if (windowState == null) {
            windowState = this.mTopFullscreenOpaqueWindowState;
        }
        if (windowState == null) {
            return;
        }
        if (windowState.getAttrs().token == this.mImmersiveModeConfirmation.getWindowToken()) {
            if (this.mNotificationShade != null && this.mNotificationShade.canReceiveKeys()) {
                windowState = this.mNotificationShade;
            } else {
                WindowState windowState2 = this.mLastFocusedWindow;
                if (windowState2 != null && windowState2.canReceiveKeys()) {
                    windowState = this.mLastFocusedWindow;
                } else {
                    windowState = this.mTopFullscreenOpaqueWindowState;
                }
            }
            if (windowState == null) {
                return;
            }
        }
        this.mSystemUiControllingWindow = windowState;
        final int displayId = getDisplayId();
        final int disableFlags = windowState.getDisableFlags();
        int updateSystemBarsLw = updateSystemBarsLw(windowState, disableFlags);
        if (this.mRelaunchingSystemBarColorApps.isEmpty()) {
            WindowState chooseNavigationColorWindowLw = chooseNavigationColorWindowLw(this.mNavBarColorWindowCandidate, this.mDisplayContent.mInputMethodWindow, this.mNavigationBarPosition);
            boolean z = true;
            boolean z2 = chooseNavigationColorWindowLw != null && chooseNavigationColorWindowLw == this.mDisplayContent.mInputMethodWindow;
            final int updateLightNavigationBarLw = updateSystemBarsLw | updateLightNavigationBarLw(windowState.mAttrs.insetsFlags.appearance, chooseNavigationColorWindowLw);
            final int i = (topAppHidesSystemBar(WindowInsets.Type.navigationBars()) ? this.mTopFullscreenOpaqueWindowState : windowState).mAttrs.insetsFlags.behavior;
            final String str = windowState.mAttrs.packageName;
            boolean z3 = (windowState.isRequestedVisible(WindowInsets.Type.statusBars()) && windowState.isRequestedVisible(WindowInsets.Type.navigationBars())) ? false : true;
            final AppearanceRegion[] appearanceRegionArr = new AppearanceRegion[this.mStatusBarAppearanceRegionList.size()];
            this.mStatusBarAppearanceRegionList.toArray(appearanceRegionArr);
            if (this.mLastDisableFlags != disableFlags) {
                this.mLastDisableFlags = disableFlags;
                final String windowState3 = windowState.toString();
                callStatusBarSafely(new Consumer() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((StatusBarManagerInternal) obj).setDisableFlags(displayId, disableFlags, windowState3);
                    }
                });
            }
            final int requestedVisibleTypes = windowState.getRequestedVisibleTypes();
            final LetterboxDetails[] letterboxDetailsArr = new LetterboxDetails[this.mLetterboxDetails.size()];
            this.mLetterboxDetails.toArray(letterboxDetailsArr);
            if (this.mLastAppearance == updateLightNavigationBarLw && this.mLastBehavior == i && this.mLastRequestedVisibleTypes == requestedVisibleTypes && Objects.equals(this.mFocusedApp, str) && this.mLastFocusIsFullscreen == z3 && Arrays.equals(this.mLastStatusBarAppearanceRegions, appearanceRegionArr) && Arrays.equals(this.mLastLetterboxDetails, letterboxDetailsArr)) {
                return;
            }
            if (this.mDisplayContent.isDefaultDisplay && this.mLastFocusIsFullscreen != z3 && ((this.mLastAppearance ^ updateLightNavigationBarLw) & 4) != 0) {
                InputManagerService inputManagerService = this.mService.mInputManager;
                if (!z3 && (updateLightNavigationBarLw & 4) == 0) {
                    z = false;
                }
                inputManagerService.setSystemUiLightsOut(z);
            }
            this.mLastAppearance = updateLightNavigationBarLw;
            this.mLastBehavior = i;
            this.mLastRequestedVisibleTypes = requestedVisibleTypes;
            this.mFocusedApp = str;
            this.mLastFocusIsFullscreen = z3;
            this.mLastStatusBarAppearanceRegions = appearanceRegionArr;
            this.mLastLetterboxDetails = letterboxDetailsArr;
            final boolean z4 = z2;
            callStatusBarSafely(new Consumer() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((StatusBarManagerInternal) obj).onSystemBarAttributesChanged(displayId, updateLightNavigationBarLw, appearanceRegionArr, z4, i, requestedVisibleTypes, str, letterboxDetailsArr);
                }
            });
        }
    }

    public final void callStatusBarSafely(final Consumer<StatusBarManagerInternal> consumer) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPolicy.this.lambda$callStatusBarSafely$7(consumer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callStatusBarSafely$7(Consumer consumer) {
        StatusBarManagerInternal statusBarManagerInternal = getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            consumer.accept(statusBarManagerInternal);
        }
    }

    @VisibleForTesting
    public static WindowState chooseNavigationColorWindowLw(WindowState windowState, WindowState windowState2, int i) {
        return !(windowState2 != null && windowState2.isVisible() && i == 4 && (windowState2.mAttrs.flags & Integer.MIN_VALUE) != 0) ? windowState : (windowState == null || !windowState.isDimming() || WindowManager.LayoutParams.mayUseInputMethod(windowState.mAttrs.flags)) ? windowState2 : windowState;
    }

    @VisibleForTesting
    public int updateLightNavigationBarLw(int i, WindowState windowState) {
        return (windowState == null || !isLightBarAllowed(windowState, WindowInsets.Type.navigationBars())) ? i & (-17) : (i & (-17)) | (windowState.mAttrs.insetsFlags.appearance & 16);
    }

    public final int updateSystemBarsLw(WindowState windowState, int i) {
        StatusBarManagerInternal statusBarManagerInternal;
        TaskDisplayArea defaultTaskDisplayArea = this.mDisplayContent.getDefaultTaskDisplayArea();
        boolean z = false;
        boolean z2 = defaultTaskDisplayArea.getRootTask(new Predicate() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateSystemBarsLw$8;
                lambda$updateSystemBarsLw$8 = DisplayPolicy.lambda$updateSystemBarsLw$8((Task) obj);
                return lambda$updateSystemBarsLw$8;
            }
        }) != null;
        boolean isRootTaskVisible = defaultTaskDisplayArea.isRootTaskVisible(5);
        boolean z3 = z2 || isRootTaskVisible;
        this.mForceShowSystemBars = z3;
        this.mForceConsumeSystemBars = z3 || this.mDisplayContent.getInsetsPolicy().remoteInsetsControllerControlsSystemBars(windowState);
        this.mDisplayContent.getInsetsPolicy().updateBarControlTarget(windowState);
        boolean z4 = topAppHidesSystemBar(WindowInsets.Type.statusBars());
        if (getStatusBar() != null && (statusBarManagerInternal = getStatusBarManagerInternal()) != null) {
            statusBarManagerInternal.setTopAppHidesStatusBar(z4);
        }
        this.mTopIsFullscreen = z4 && (this.mNotificationShade == null || !this.mNotificationShade.isVisible());
        int configureNavBarOpacity = configureNavBarOpacity(configureStatusBarOpacity(3), z2, isRootTaskVisible);
        boolean z5 = this.mIsImmersiveMode;
        boolean isImmersiveMode = isImmersiveMode(windowState);
        if (z5 != isImmersiveMode) {
            this.mIsImmersiveMode = isImmersiveMode;
            RootDisplayArea rootDisplayArea = windowState.getRootDisplayArea();
            this.mImmersiveModeConfirmation.immersiveModeChangedLw(rootDisplayArea == null ? -1 : rootDisplayArea.mFeatureId, isImmersiveMode, this.mService.mPolicy.isUserSetupComplete(), isNavBarEmpty(i));
        }
        boolean z6 = !windowState.isRequestedVisible(WindowInsets.Type.navigationBars());
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mPendingPanicGestureUptime;
        if (j != 0 && uptimeMillis - j <= 30000) {
            z = true;
        }
        DisplayPolicy displayPolicy = this.mService.getDefaultDisplayContentLocked().getDisplayPolicy();
        if (z && z6 && isImmersiveMode && displayPolicy.isKeyguardDrawComplete()) {
            this.mPendingPanicGestureUptime = 0L;
            if (!isNavBarEmpty(i)) {
                this.mDisplayContent.getInsetsPolicy().showTransient(SHOW_TYPES_FOR_PANIC, true);
            }
        }
        return configureNavBarOpacity;
    }

    public static /* synthetic */ boolean lambda$updateSystemBarsLw$8(Task task) {
        return task.isVisible() && task.getTopLeafTask().getWindowingMode() == 6;
    }

    public static boolean isLightBarAllowed(WindowState windowState, int i) {
        if (windowState == null) {
            return false;
        }
        return intersectsAnyInsets(windowState.getFrame(), windowState.getInsetsState(), i);
    }

    public final Rect getBarContentFrameForWindow(WindowState windowState, int i) {
        DisplayFrames displayFrames = windowState.getDisplayFrames(this.mDisplayContent.mDisplayFrames);
        InsetsState insetsState = displayFrames.mInsetsState;
        Rect rect = displayFrames.mUnrestricted;
        Rect rect2 = sTmpDisplayCutoutSafe;
        Insets waterfallInsets = insetsState.getDisplayCutout().getWaterfallInsets();
        Rect rect3 = new Rect();
        Rect rect4 = sTmpRect;
        rect2.set(displayFrames.mDisplayCutoutSafe);
        for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = insetsState.sourceAt(sourceSize);
            if (sourceAt.getType() == i) {
                if (i == WindowInsets.Type.statusBars()) {
                    rect2.set(displayFrames.mDisplayCutoutSafe);
                    Insets calculateInsets = sourceAt.calculateInsets(rect, true);
                    if (calculateInsets.left > 0) {
                        int i2 = rect.left;
                        rect2.left = Math.max(waterfallInsets.left + i2, i2);
                    } else if (calculateInsets.top > 0) {
                        int i3 = rect.top;
                        rect2.top = Math.max(waterfallInsets.top + i3, i3);
                    } else if (calculateInsets.right > 0) {
                        int i4 = rect.right;
                        rect2.right = Math.max(i4 - waterfallInsets.right, i4);
                    } else if (calculateInsets.bottom > 0) {
                        int i5 = rect.bottom;
                        rect2.bottom = Math.max(i5 - waterfallInsets.bottom, i5);
                    }
                }
                rect4.set(sourceAt.getFrame());
                rect4.intersect(rect2);
                rect3.union(rect4);
            }
        }
        return rect3;
    }

    @VisibleForTesting
    public boolean isFullyTransparentAllowed(WindowState windowState, int i) {
        if (windowState == null) {
            return true;
        }
        return windowState.isFullyTransparentBarAllowed(getBarContentFrameForWindow(windowState, i));
    }

    public final boolean drawsBarBackground(WindowState windowState) {
        if (windowState == null) {
            return true;
        }
        return ((windowState.getAttrs().privateFlags & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0) || ((windowState.getAttrs().flags & Integer.MIN_VALUE) != 0);
    }

    public final int configureStatusBarOpacity(int i) {
        boolean z = true;
        boolean z2 = true;
        for (int size = this.mStatusBarBackgroundWindows.size() - 1; size >= 0; size--) {
            WindowState windowState = this.mStatusBarBackgroundWindows.get(size);
            z &= drawsBarBackground(windowState);
            z2 &= isFullyTransparentAllowed(windowState, WindowInsets.Type.statusBars());
        }
        if (z) {
            i &= -2;
        }
        return !z2 ? i | 32 : i;
    }

    public final int configureNavBarOpacity(int i, boolean z, boolean z2) {
        boolean drawsBarBackground = drawsBarBackground(this.mNavBarBackgroundWindow);
        int i2 = this.mNavBarOpacityMode;
        if (i2 == 2) {
            if (drawsBarBackground) {
                i = clearNavBarOpaqueFlag(i);
            }
        } else if (i2 == 0) {
            if (z || z2) {
                if (this.mIsFreeformWindowOverlappingWithNavBar) {
                    i = clearNavBarOpaqueFlag(i);
                }
            } else if (drawsBarBackground) {
                i = clearNavBarOpaqueFlag(i);
            }
        } else if (i2 == 1 && z2) {
            i = clearNavBarOpaqueFlag(i);
        }
        return !isFullyTransparentAllowed(this.mNavBarBackgroundWindow, WindowInsets.Type.navigationBars()) ? i | 64 : i;
    }

    public final boolean isImmersiveMode(WindowState windowState) {
        if (windowState == null || windowState == getNotificationShade() || windowState.isActivityTypeDream()) {
            return false;
        }
        return getInsetsPolicy().hasHiddenSources(WindowInsets.Type.navigationBars());
    }

    public void onPowerKeyDown(boolean z) {
        if (this.mImmersiveModeConfirmation.onPowerKeyDown(z, SystemClock.elapsedRealtime(), isImmersiveMode(this.mSystemUiControllingWindow), isNavBarEmpty(this.mLastDisableFlags))) {
            this.mHandler.post(this.mHiddenNavPanic);
        }
    }

    public void onVrStateChangedLw(boolean z) {
        this.mImmersiveModeConfirmation.onVrStateChangedLw(z);
    }

    public void onLockTaskStateChangedLw(int i) {
        this.mImmersiveModeConfirmation.onLockTaskModeChangedLw(i);
    }

    public void onUserActivityEventTouch() {
        if (this.mAwake) {
            return;
        }
        WindowState windowState = this.mNotificationShade;
        this.mService.mAtmService.setProcessAnimatingWhileDozing(windowState != null ? windowState.getProcess() : null);
    }

    public boolean onSystemUiSettingsChanged() {
        return this.mImmersiveModeConfirmation.onSettingChanged(this.mService.mCurrentUserId);
    }

    public void takeScreenshot(int i, int i2) {
        if (this.mScreenshotHelper != null) {
            this.mScreenshotHelper.takeScreenshot(new ScreenshotRequest.Builder(i, i2).build(), this.mHandler, (Consumer) null);
        }
    }

    public RefreshRatePolicy getRefreshRatePolicy() {
        return this.mRefreshRatePolicy;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.println("DisplayPolicy");
        String str2 = str + "  ";
        String str3 = str2 + "  ";
        printWriter.print(str2);
        printWriter.print("mCarDockEnablesAccelerometer=");
        printWriter.print(this.mCarDockEnablesAccelerometer);
        printWriter.print(" mDeskDockEnablesAccelerometer=");
        printWriter.println(this.mDeskDockEnablesAccelerometer);
        printWriter.print(str2);
        printWriter.print("mDockMode=");
        printWriter.print(Intent.dockStateToString(this.mDockMode));
        printWriter.print(" mLidState=");
        printWriter.println(WindowManagerPolicy.WindowManagerFuncs.lidStateToString(this.mLidState));
        printWriter.print(str2);
        printWriter.print("mAwake=");
        printWriter.print(this.mAwake);
        printWriter.print(" mScreenOnEarly=");
        printWriter.print(this.mScreenOnEarly);
        printWriter.print(" mScreenOnFully=");
        printWriter.println(this.mScreenOnFully);
        printWriter.print(str2);
        printWriter.print("mKeyguardDrawComplete=");
        printWriter.print(this.mKeyguardDrawComplete);
        printWriter.print(" mWindowManagerDrawComplete=");
        printWriter.println(this.mWindowManagerDrawComplete);
        printWriter.print(str2);
        printWriter.print("mHdmiPlugged=");
        printWriter.println(this.mHdmiPlugged);
        if (this.mLastDisableFlags != 0) {
            printWriter.print(str2);
            printWriter.print("mLastDisableFlags=0x");
            printWriter.println(Integer.toHexString(this.mLastDisableFlags));
        }
        if (this.mLastAppearance != 0) {
            printWriter.print(str2);
            printWriter.print("mLastAppearance=");
            printWriter.println(ViewDebug.flagsToString(InsetsFlags.class, "appearance", this.mLastAppearance));
        }
        if (this.mLastBehavior != 0) {
            printWriter.print(str2);
            printWriter.print("mLastBehavior=");
            printWriter.println(ViewDebug.flagsToString(InsetsFlags.class, "behavior", this.mLastBehavior));
        }
        printWriter.print(str2);
        printWriter.print("mShowingDream=");
        printWriter.print(this.mShowingDream);
        printWriter.print(" mDreamingLockscreen=");
        printWriter.println(this.mDreamingLockscreen);
        if (this.mStatusBar != null) {
            printWriter.print(str2);
            printWriter.print("mStatusBar=");
            printWriter.println(this.mStatusBar);
        }
        if (this.mNotificationShade != null) {
            printWriter.print(str2);
            printWriter.print("mExpandedPanel=");
            printWriter.println(this.mNotificationShade);
        }
        printWriter.print(str2);
        printWriter.print("isKeyguardShowing=");
        printWriter.println(isKeyguardShowing());
        if (this.mNavigationBar != null) {
            printWriter.print(str2);
            printWriter.print("mNavigationBar=");
            printWriter.println(this.mNavigationBar);
            printWriter.print(str2);
            printWriter.print("mNavBarOpacityMode=");
            printWriter.println(this.mNavBarOpacityMode);
            printWriter.print(str2);
            printWriter.print("mNavigationBarCanMove=");
            printWriter.println(this.mNavigationBarCanMove);
            printWriter.print(str2);
            printWriter.print("mNavigationBarPosition=");
            printWriter.println(this.mNavigationBarPosition);
        }
        if (this.mLeftGestureHost != null) {
            printWriter.print(str2);
            printWriter.print("mLeftGestureHost=");
            printWriter.println(this.mLeftGestureHost);
        }
        if (this.mTopGestureHost != null) {
            printWriter.print(str2);
            printWriter.print("mTopGestureHost=");
            printWriter.println(this.mTopGestureHost);
        }
        if (this.mRightGestureHost != null) {
            printWriter.print(str2);
            printWriter.print("mRightGestureHost=");
            printWriter.println(this.mRightGestureHost);
        }
        if (this.mBottomGestureHost != null) {
            printWriter.print(str2);
            printWriter.print("mBottomGestureHost=");
            printWriter.println(this.mBottomGestureHost);
        }
        if (this.mFocusedWindow != null) {
            printWriter.print(str2);
            printWriter.print("mFocusedWindow=");
            printWriter.println(this.mFocusedWindow);
        }
        if (this.mTopFullscreenOpaqueWindowState != null) {
            printWriter.print(str2);
            printWriter.print("mTopFullscreenOpaqueWindowState=");
            printWriter.println(this.mTopFullscreenOpaqueWindowState);
        }
        if (!this.mSystemBarColorApps.isEmpty()) {
            printWriter.print(str2);
            printWriter.print("mSystemBarColorApps=");
            printWriter.println(this.mSystemBarColorApps);
        }
        if (!this.mRelaunchingSystemBarColorApps.isEmpty()) {
            printWriter.print(str2);
            printWriter.print("mRelaunchingSystemBarColorApps=");
            printWriter.println(this.mRelaunchingSystemBarColorApps);
        }
        if (this.mNavBarColorWindowCandidate != null) {
            printWriter.print(str2);
            printWriter.print("mNavBarColorWindowCandidate=");
            printWriter.println(this.mNavBarColorWindowCandidate);
        }
        if (this.mNavBarBackgroundWindow != null) {
            printWriter.print(str2);
            printWriter.print("mNavBarBackgroundWindow=");
            printWriter.println(this.mNavBarBackgroundWindow);
        }
        if (this.mLastStatusBarAppearanceRegions != null) {
            printWriter.print(str2);
            printWriter.println("mLastStatusBarAppearanceRegions=");
            for (int length = this.mLastStatusBarAppearanceRegions.length - 1; length >= 0; length--) {
                printWriter.print(str3);
                printWriter.println(this.mLastStatusBarAppearanceRegions[length]);
            }
        }
        if (this.mLastLetterboxDetails != null) {
            printWriter.print(str2);
            printWriter.println("mLastLetterboxDetails=");
            for (int length2 = this.mLastLetterboxDetails.length - 1; length2 >= 0; length2--) {
                printWriter.print(str3);
                printWriter.println(this.mLastLetterboxDetails[length2]);
            }
        }
        if (!this.mStatusBarBackgroundWindows.isEmpty()) {
            printWriter.print(str2);
            printWriter.println("mStatusBarBackgroundWindows=");
            for (int size = this.mStatusBarBackgroundWindows.size() - 1; size >= 0; size--) {
                printWriter.print(str3);
                printWriter.println(this.mStatusBarBackgroundWindows.get(size));
            }
        }
        printWriter.print(str2);
        printWriter.print("mTopIsFullscreen=");
        printWriter.println(this.mTopIsFullscreen);
        printWriter.print(str2);
        printWriter.print("mForceShowNavigationBarEnabled=");
        printWriter.print(this.mForceShowNavigationBarEnabled);
        printWriter.print(" mAllowLockscreenWhenOn=");
        printWriter.println(this.mAllowLockscreenWhenOn);
        printWriter.print(str2);
        printWriter.print("mRemoteInsetsControllerControlsSystemBars=");
        printWriter.println(this.mRemoteInsetsControllerControlsSystemBars);
        printWriter.print(str2);
        printWriter.println("mDecorInsetsInfo:");
        for (int i = 0; i < this.mDecorInsets.mInfoForRotation.length; i++) {
            printWriter.println(str3 + Surface.rotationToString(i) + "=" + this.mDecorInsets.mInfoForRotation[i]);
        }
        this.mSystemGestures.dump(printWriter, str2);
        printWriter.print(str2);
        printWriter.println("Looper state:");
        this.mHandler.getLooper().dump(new PrintWriterPrinter(printWriter), str2 + "  ");
    }

    public final boolean supportsPointerLocation() {
        DisplayContent displayContent = this.mDisplayContent;
        return displayContent.isDefaultDisplay || !displayContent.isPrivate();
    }

    public void setPointerLocationEnabled(boolean z) {
        if (supportsPointerLocation()) {
            this.mHandler.sendEmptyMessage(z ? 4 : 5);
        }
    }

    public final void enablePointerLocation() {
        if (this.mPointerLocationView != null) {
            return;
        }
        PointerLocationView pointerLocationView = new PointerLocationView(this.mContext);
        this.mPointerLocationView = pointerLocationView;
        pointerLocationView.setPrintCoords(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = 2015;
        layoutParams.flags = FrameworkStatsLog.TV_CAS_SESSION_OPEN_STATUS;
        layoutParams.privateFlags |= 16;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.layoutInDisplayCutoutMode = 3;
        if (ActivityManager.isHighEndGfx()) {
            layoutParams.flags |= 16777216;
            layoutParams.privateFlags |= 2;
        }
        layoutParams.format = -3;
        layoutParams.setTitle("PointerLocation - display " + getDisplayId());
        layoutParams.inputFeatures = layoutParams.inputFeatures | 1;
        ((WindowManager) this.mContext.getSystemService(WindowManager.class)).addView(this.mPointerLocationView, layoutParams);
        this.mDisplayContent.registerPointerEventListener(this.mPointerLocationView);
    }

    public final void disablePointerLocation() {
        if (this.mPointerLocationView == null) {
            return;
        }
        if (!this.mDisplayContent.isRemoved()) {
            this.mDisplayContent.unregisterPointerEventListener(this.mPointerLocationView);
        }
        ((WindowManager) this.mContext.getSystemService(WindowManager.class)).removeView(this.mPointerLocationView);
        this.mPointerLocationView = null;
    }

    public boolean isWindowExcludedFromContent(WindowState windowState) {
        PointerLocationView pointerLocationView;
        return (windowState == null || (pointerLocationView = this.mPointerLocationView) == null || windowState.mClient != pointerLocationView.getWindowToken()) ? false : true;
    }

    public void release() {
        this.mDisplayContent.mTransitionController.unregisterLegacyListener(this.mAppTransitionListener);
        Handler handler = this.mHandler;
        final GestureNavigationSettingsObserver gestureNavigationSettingsObserver = this.mGestureNavigationSettingsObserver;
        Objects.requireNonNull(gestureNavigationSettingsObserver);
        handler.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                gestureNavigationSettingsObserver.unregister();
            }
        });
        Handler handler2 = this.mHandler;
        final ForceShowNavBarSettingsObserver forceShowNavBarSettingsObserver = this.mForceShowNavBarSettingsObserver;
        Objects.requireNonNull(forceShowNavBarSettingsObserver);
        handler2.post(new Runnable() { // from class: com.android.server.wm.DisplayPolicy$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                forceShowNavBarSettingsObserver.unregister();
            }
        });
        this.mImmersiveModeConfirmation.release();
        if (this.mService.mPointerLocationEnabled) {
            setPointerLocationEnabled(false);
        }
    }

    @VisibleForTesting
    public static boolean isOverlappingWithNavBar(WindowState windowState) {
        if (windowState.isVisible()) {
            return intersectsAnyInsets(windowState.isDimming() ? windowState.getBounds() : windowState.getFrame(), windowState.getInsetsState(), WindowInsets.Type.navigationBars());
        }
        return false;
    }

    public static boolean intersectsAnyInsets(Rect rect, InsetsState insetsState, int i) {
        for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = insetsState.sourceAt(sourceSize);
            if ((sourceAt.getType() & i) != 0 && sourceAt.isVisible() && Rect.intersects(rect, sourceAt.getFrame())) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldAttachNavBarToAppDuringTransition() {
        return this.mShouldAttachNavBarToAppDuringTransition && this.mNavigationBar != null;
    }
}
