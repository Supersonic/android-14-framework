package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.ActivityThread;
import android.app.AnrController;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.BackgroundStartPrivileges;
import android.app.Dialog;
import android.app.IActivityClientController;
import android.app.IActivityController;
import android.app.IActivityTaskManager;
import android.app.IAppTask;
import android.app.IApplicationThread;
import android.app.IAssistDataReceiver;
import android.app.INotificationManager;
import android.app.IScreenCaptureObserver;
import android.app.ITaskStackListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.PictureInPictureUiState;
import android.app.ProfilerInfo;
import android.app.WaitResult;
import android.app.admin.DevicePolicyCache;
import android.app.assist.ActivityId;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.app.compat.CompatChanges;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.LocusId;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.InputConstants;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UpdateLock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.service.dreams.DreamActivity;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionManagerInternal;
import android.sysprop.DisplayProperties;
import android.telecom.TelecomManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.window.BackAnimationAdapter;
import android.window.BackNavigationInfo;
import android.window.IWindowOrganizerController;
import android.window.SplashScreenView;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.ProcessMap;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.TransferPipe;
import com.android.internal.policy.AttributeCache;
import com.android.internal.policy.KeyguardDismissCallback;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.HeptConsumer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.UiThread;
import com.android.server.Watchdog;
import com.android.server.firewall.IntentFirewall;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.AppTimeTracker;
import com.android.server.p006am.AssistDataRequester;
import com.android.server.p006am.BaseErrorDialog;
import com.android.server.p006am.EventLogTags;
import com.android.server.p006am.PendingIntentController;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p006am.UserState;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerService;
import com.android.server.p014wm.RootWindowContainer;
import com.android.server.p014wm.Task;
import com.android.server.policy.PermissionPolicyInternal;
import com.android.server.sdksandbox.SdkSandboxManagerLocal;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.uri.NeededUriGrants;
import com.android.server.uri.UriGrantsManagerInternal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.ActivityTaskManagerService */
/* loaded from: classes2.dex */
public class ActivityTaskManagerService extends IActivityTaskManager.Stub {
    public final int GL_ES_VERSION;
    public int[] mAccessibilityServiceUids;
    public volatile ComponentName mActiveDreamComponent;
    public final MirrorActiveUids mActiveUids;
    public ComponentName mActiveVoiceInteractionServiceComponent;
    public ActivityClientController mActivityClientController;
    public SparseArray<ActivityInterceptorCallback> mActivityInterceptorCallbacks;
    public ActivityStartController mActivityStartController;
    public final SparseArray<ArrayMap<String, Integer>> mAllowAppSwitchUids;
    public ActivityManagerInternal mAmInternal;
    public final List<AnrController> mAnrController;
    public AppOpsManager mAppOpsManager;
    public volatile int mAppSwitchesState;
    public AppWarnings mAppWarnings;
    public final BackNavigationController mBackNavigationController;
    public BackgroundActivityStartCallback mBackgroundActivityStartCallback;
    public final Map<Integer, Set<Integer>> mCompanionAppUidsMap;
    public CompatModePackages mCompatModePackages;
    public int mConfigurationSeq;
    public Context mContext;
    public IActivityController mController;
    public boolean mControllerIsAMonkey;
    public AppTimeTracker mCurAppTimeTracker;
    public volatile int mDemoteTopAppReasons;
    public boolean mDevEnableNonResizableMultiWindow;
    public int mDeviceOwnerUid;
    public final int mFactoryTest;
    public boolean mForceResizableActivities;
    public int mGlobalAssetsSeq;
    public final WindowManagerGlobalLock mGlobalLock;
    public final Object mGlobalLockWithoutBoost;

    /* renamed from: mH */
    public HandlerC1840H f1161mH;
    public boolean mHasCompanionDeviceSetupFeature;
    public boolean mHasHeavyWeightFeature;
    public boolean mHasLeanbackFeature;
    public volatile WindowProcessController mHeavyWeightProcess;
    public volatile WindowProcessController mHomeProcess;
    public IntentFirewall mIntentFirewall;
    @VisibleForTesting
    final ActivityTaskManagerInternal mInternal;
    public KeyguardController mKeyguardController;
    public boolean mKeyguardShown;
    public String mLastANRState;
    public ActivityRecord mLastResumedActivity;
    public volatile long mLastStopAppSwitchesTime;
    public int mLaunchPowerModeReasons;
    public int mLayoutReasons;
    public final ClientLifecycleManager mLifecycleManager;
    public LockTaskController mLockTaskController;
    public float mMinPercentageMultiWindowSupportHeight;
    public float mMinPercentageMultiWindowSupportWidth;
    public PackageConfigPersister mPackageConfigPersister;
    public final ArrayList<PendingAssistExtras> mPendingAssistExtras;
    public PendingIntentController mPendingIntentController;
    public PermissionPolicyInternal mPermissionPolicyInternal;
    public PackageManagerInternal mPmInternal;
    public PowerManagerInternal mPowerManagerInternal;
    public volatile WindowProcessController mPreviousProcess;
    public long mPreviousProcessVisibleTime;
    public final WindowProcessControllerMap mProcessMap;
    public final ProcessMap<WindowProcessController> mProcessNames;
    public String mProfileApp;
    public WindowProcessController mProfileProc;
    public ProfilerInfo mProfilerInfo;
    public RecentTasks mRecentTasks;
    public int mRespectsActivityMinWidthHeightMultiWindow;
    public volatile boolean mRetainPowerModeAndTopProcessState;
    public RootWindowContainer mRootWindowContainer;
    public IVoiceInteractionSession mRunningVoice;
    public final List<ActivityTaskManagerInternal.ScreenObserver> mScreenObservers;
    public SettingObserver mSettingsObserver;
    public boolean mShowDialogs;
    public boolean mShuttingDown;
    public volatile boolean mSleeping;
    public StatusBarManagerInternal mStatusBarManagerInternal;
    public String[] mSupportedSystemLocales;
    public boolean mSupportsExpandedPictureInPicture;
    public boolean mSupportsFreeformWindowManagement;
    public boolean mSupportsMultiDisplay;
    public boolean mSupportsMultiWindow;
    public int mSupportsNonResizableMultiWindow;
    public boolean mSupportsPictureInPicture;
    public boolean mSupportsSplitScreenMultiWindow;
    public boolean mSuppressResizeConfigChanges;
    public ComponentName mSysUiServiceComponent;
    public final ActivityThread mSystemThread;
    public TaskChangeNotificationController mTaskChangeNotificationController;
    public TaskFragmentOrganizerController mTaskFragmentOrganizerController;
    public TaskOrganizerController mTaskOrganizerController;
    public ActivityTaskSupervisor mTaskSupervisor;
    public Configuration mTempConfig;
    public int mThumbnailHeight;
    public int mThumbnailWidth;
    public final UpdateConfigurationResult mTmpUpdateConfigurationResult;
    public String mTopAction;
    public volatile WindowProcessController mTopApp;
    public ComponentName mTopComponent;
    public String mTopData;
    public volatile int mTopProcessState;
    public ActivityRecord mTracedResumedActivity;
    public UriGrantsManagerInternal mUgmInternal;
    public final Context mUiContext;
    public UiHandler mUiHandler;
    public final UpdateLock mUpdateLock;
    public final Runnable mUpdateOomAdjRunnable;
    public UsageStatsManagerInternal mUsageStatsInternal;
    public UserManagerService mUserManager;
    public int mViSessionId;
    public final VisibleActivityProcessTracker mVisibleActivityProcessTracker;
    public PowerManager.WakeLock mVoiceWakeLock;
    public int mVr2dDisplayId;
    public VrController mVrController;
    public WindowManagerService mWindowManager;
    public WindowOrganizerController mWindowOrganizerController;

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$UpdateConfigurationResult */
    /* loaded from: classes2.dex */
    public static final class UpdateConfigurationResult {
        public boolean activityRelaunched;
        public int changes;
    }

    public static String relaunchReasonToString(int i) {
        if (i != 1) {
            if (i != 2) {
                return null;
            }
            return "free_resize";
        }
        return "window_resize";
    }

    public void logAppTooSlow(WindowProcessController windowProcessController, long j, String str) {
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$SettingObserver */
    /* loaded from: classes2.dex */
    public final class SettingObserver extends ContentObserver {
        public final Uri mFontScaleUri;
        public final Uri mFontWeightAdjustmentUri;
        public final Uri mHideErrorDialogsUri;

        public SettingObserver() {
            super(ActivityTaskManagerService.this.f1161mH);
            Uri uriFor = Settings.System.getUriFor("font_scale");
            this.mFontScaleUri = uriFor;
            Uri uriFor2 = Settings.Global.getUriFor("hide_error_dialogs");
            this.mHideErrorDialogsUri = uriFor2;
            Uri uriFor3 = Settings.Secure.getUriFor("font_weight_adjustment");
            this.mFontWeightAdjustmentUri = uriFor3;
            ContentResolver contentResolver = ActivityTaskManagerService.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(uriFor, false, this, -1);
            contentResolver.registerContentObserver(uriFor2, false, this, -1);
            contentResolver.registerContentObserver(uriFor3, false, this, -1);
        }

        public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
            for (Uri uri : collection) {
                if (this.mFontScaleUri.equals(uri)) {
                    ActivityTaskManagerService.this.updateFontScaleIfNeeded(i2);
                } else if (this.mHideErrorDialogsUri.equals(uri)) {
                    synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                            activityTaskManagerService.updateShouldShowDialogsLocked(activityTaskManagerService.getGlobalConfiguration());
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (this.mFontWeightAdjustmentUri.equals(uri)) {
                    ActivityTaskManagerService.this.updateFontWeightAdjustmentIfNeeded(i2);
                }
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public ActivityTaskManagerService(Context context) {
        WindowManagerGlobalLock windowManagerGlobalLock = new WindowManagerGlobalLock();
        this.mGlobalLock = windowManagerGlobalLock;
        this.mGlobalLockWithoutBoost = windowManagerGlobalLock;
        this.mActiveUids = new MirrorActiveUids();
        this.mProcessNames = new ProcessMap<>();
        this.mProcessMap = new WindowProcessControllerMap();
        this.mKeyguardShown = false;
        this.mViSessionId = 1000;
        this.mPendingAssistExtras = new ArrayList<>();
        this.mCompanionAppUidsMap = new ArrayMap();
        this.mActivityInterceptorCallbacks = new SparseArray<>();
        this.mTmpUpdateConfigurationResult = new UpdateConfigurationResult();
        this.mSupportedSystemLocales = null;
        this.mTempConfig = new Configuration();
        this.mAppSwitchesState = 2;
        this.mAnrController = new ArrayList();
        this.mController = null;
        this.mControllerIsAMonkey = false;
        this.mTopAction = "android.intent.action.MAIN";
        this.mProfileApp = null;
        this.mProfileProc = null;
        this.mProfilerInfo = null;
        this.mUpdateLock = new UpdateLock("immersive");
        this.mAllowAppSwitchUids = new SparseArray<>();
        this.mScreenObservers = new ArrayList();
        this.mVr2dDisplayId = -1;
        this.mTopProcessState = 2;
        this.mShowDialogs = true;
        this.mShuttingDown = false;
        this.mAccessibilityServiceUids = new int[0];
        this.mDeviceOwnerUid = -1;
        this.mUpdateOomAdjRunnable = new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                ActivityTaskManagerService.this.mAmInternal.updateOomAdj();
            }
        };
        this.mContext = context;
        this.mFactoryTest = FactoryTest.getMode();
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        this.mSystemThread = currentActivityThread;
        this.mUiContext = currentActivityThread.getSystemUiContext();
        this.mLifecycleManager = new ClientLifecycleManager();
        this.mVisibleActivityProcessTracker = new VisibleActivityProcessTracker(this);
        this.mInternal = new LocalService();
        this.GL_ES_VERSION = SystemProperties.getInt("ro.opengles.version", 0);
        WindowOrganizerController windowOrganizerController = new WindowOrganizerController(this);
        this.mWindowOrganizerController = windowOrganizerController;
        this.mTaskOrganizerController = windowOrganizerController.mTaskOrganizerController;
        this.mTaskFragmentOrganizerController = windowOrganizerController.mTaskFragmentOrganizerController;
        this.mBackNavigationController = new BackNavigationController();
    }

    public void onSystemReady() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                PackageManager packageManager = this.mContext.getPackageManager();
                this.mHasHeavyWeightFeature = packageManager.hasSystemFeature("android.software.cant_save_state");
                this.mHasLeanbackFeature = packageManager.hasSystemFeature("android.software.leanback");
                this.mHasCompanionDeviceSetupFeature = packageManager.hasSystemFeature("android.software.companion_device_setup");
                this.mVrController.onSystemReady();
                this.mRecentTasks.onSystemReadyLocked();
                this.mTaskSupervisor.onSystemReady();
                this.mActivityClientController.onSystemReady();
                ActivitySecurityModelFeatureFlags.initialize(this.mContext.getMainExecutor(), packageManager);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onInitPowerManagement() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mTaskSupervisor.initPowerManagement();
                this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                PowerManager.WakeLock newWakeLock = ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(1, "*voice*");
                this.mVoiceWakeLock = newWakeLock;
                newWakeLock.setReferenceCounted(false);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void installSystemProviders() {
        this.mSettingsObserver = new SettingObserver();
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x011b A[Catch: all -> 0x0149, TryCatch #0 {all -> 0x0149, blocks: (B:37:0x00cb, B:49:0x00fa, B:50:0x0107, B:52:0x011b, B:53:0x012c, B:54:0x0144, B:48:0x00ec), top: B:61:0x00cb }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void retrieveSettings(ContentResolver contentResolver) {
        boolean z;
        boolean z2 = this.mContext.getPackageManager().hasSystemFeature("android.software.freeform_window_management") || Settings.Global.getInt(contentResolver, "enable_freeform_support", 0) != 0;
        boolean supportsMultiWindow = ActivityTaskManager.supportsMultiWindow(this.mContext);
        boolean z3 = supportsMultiWindow && this.mContext.getPackageManager().hasSystemFeature("android.software.picture_in_picture");
        boolean z4 = z3 && this.mContext.getPackageManager().hasSystemFeature("android.software.expanded_picture_in_picture");
        boolean supportsSplitScreenMultiWindow = ActivityTaskManager.supportsSplitScreenMultiWindow(this.mContext);
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("android.software.activities_on_secondary_displays");
        boolean z5 = Settings.Global.getInt(contentResolver, "debug.force_rtl", 0) != 0;
        boolean z6 = Settings.Global.getInt(contentResolver, "force_resizable_activities", 0) != 0;
        boolean z7 = Settings.Global.getInt(contentResolver, "enable_non_resizable_multi_window", 0) != 0;
        int integer = this.mContext.getResources().getInteger(17694972);
        int integer2 = this.mContext.getResources().getInteger(17694937);
        float f = this.mContext.getResources().getFloat(17105090);
        float f2 = this.mContext.getResources().getFloat(17105091);
        DisplayProperties.debug_force_rtl(Boolean.valueOf(z5));
        Configuration configuration = new Configuration();
        Settings.System.getConfiguration(contentResolver, configuration);
        if (z5) {
            configuration.setLayoutDirection(configuration.locale);
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mForceResizableActivities = z6;
                this.mDevEnableNonResizableMultiWindow = z7;
                this.mSupportsNonResizableMultiWindow = integer;
                this.mRespectsActivityMinWidthHeightMultiWindow = integer2;
                this.mMinPercentageMultiWindowSupportHeight = f;
                this.mMinPercentageMultiWindowSupportWidth = f2;
                if (!z2 && !supportsSplitScreenMultiWindow && !z3 && !hasSystemFeature) {
                    z = false;
                    if ((!supportsMultiWindow || z6) && z) {
                        this.mSupportsMultiWindow = true;
                        this.mSupportsFreeformWindowManagement = z2;
                        this.mSupportsSplitScreenMultiWindow = supportsSplitScreenMultiWindow;
                        this.mSupportsPictureInPicture = z3;
                        this.mSupportsExpandedPictureInPicture = z4;
                        this.mSupportsMultiDisplay = hasSystemFeature;
                    } else {
                        this.mSupportsMultiWindow = false;
                        this.mSupportsFreeformWindowManagement = false;
                        this.mSupportsSplitScreenMultiWindow = false;
                        this.mSupportsPictureInPicture = false;
                        this.mSupportsExpandedPictureInPicture = false;
                        this.mSupportsMultiDisplay = false;
                    }
                    this.mWindowManager.mRoot.onSettingsRetrieved();
                    updateConfigurationLocked(configuration, null, true);
                    Configuration globalConfiguration = getGlobalConfiguration();
                    if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1305755880, 0, (String) null, new Object[]{String.valueOf(globalConfiguration)});
                    }
                    Resources resources = this.mContext.getResources();
                    this.mThumbnailWidth = resources.getDimensionPixelSize(17104898);
                    this.mThumbnailHeight = resources.getDimensionPixelSize(17104897);
                }
                z = true;
                if (!supportsMultiWindow) {
                }
                this.mSupportsMultiWindow = true;
                this.mSupportsFreeformWindowManagement = z2;
                this.mSupportsSplitScreenMultiWindow = supportsSplitScreenMultiWindow;
                this.mSupportsPictureInPicture = z3;
                this.mSupportsExpandedPictureInPicture = z4;
                this.mSupportsMultiDisplay = hasSystemFeature;
                this.mWindowManager.mRoot.onSettingsRetrieved();
                updateConfigurationLocked(configuration, null, true);
                Configuration globalConfiguration2 = getGlobalConfiguration();
                if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                }
                Resources resources2 = this.mContext.getResources();
                this.mThumbnailWidth = resources2.getDimensionPixelSize(17104898);
                this.mThumbnailHeight = resources2.getDimensionPixelSize(17104897);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public WindowManagerGlobalLock getGlobalLock() {
        return this.mGlobalLock;
    }

    @VisibleForTesting
    public ActivityTaskManagerInternal getAtmInternal() {
        return this.mInternal;
    }

    public void initialize(IntentFirewall intentFirewall, PendingIntentController pendingIntentController, Looper looper) {
        this.f1161mH = new HandlerC1840H(looper);
        this.mUiHandler = new UiHandler();
        this.mIntentFirewall = intentFirewall;
        File ensureSystemDir = SystemServiceManager.ensureSystemDir();
        this.mAppWarnings = createAppWarnings(this.mUiContext, this.f1161mH, this.mUiHandler, ensureSystemDir);
        this.mCompatModePackages = new CompatModePackages(this, ensureSystemDir, this.f1161mH);
        this.mPendingIntentController = pendingIntentController;
        this.mTaskSupervisor = createTaskSupervisor();
        this.mActivityClientController = new ActivityClientController(this);
        this.mTaskChangeNotificationController = new TaskChangeNotificationController(this.mTaskSupervisor, this.f1161mH);
        this.mLockTaskController = new LockTaskController(this.mContext, this.mTaskSupervisor, this.f1161mH, this.mTaskChangeNotificationController);
        this.mActivityStartController = new ActivityStartController(this);
        setRecentTasks(new RecentTasks(this, this.mTaskSupervisor));
        this.mVrController = new VrController(this.mGlobalLock);
        this.mKeyguardController = this.mTaskSupervisor.getKeyguardController();
        this.mPackageConfigPersister = new PackageConfigPersister(this.mTaskSupervisor.mPersisterQueue, this);
    }

    public void onActivityManagerInternalAdded() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
                this.mUgmInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public int increaseConfigurationSeqLocked() {
        int i = this.mConfigurationSeq + 1;
        this.mConfigurationSeq = i;
        int max = Math.max(i, 1);
        this.mConfigurationSeq = max;
        return max;
    }

    public ActivityTaskSupervisor createTaskSupervisor() {
        ActivityTaskSupervisor activityTaskSupervisor = new ActivityTaskSupervisor(this, this.f1161mH.getLooper());
        activityTaskSupervisor.initialize();
        return activityTaskSupervisor;
    }

    public AppWarnings createAppWarnings(Context context, Handler handler, Handler handler2, File file) {
        return new AppWarnings(this, context, handler, handler2, file);
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mWindowManager = windowManagerService;
                this.mRootWindowContainer = windowManagerService.mRoot;
                this.mWindowOrganizerController.mTransitionController.setWindowManager(windowManagerService);
                this.mTempConfig.setToDefaults();
                this.mTempConfig.setLocales(LocaleList.getDefault());
                Configuration configuration = this.mTempConfig;
                configuration.seq = 1;
                this.mConfigurationSeq = 1;
                this.mRootWindowContainer.onConfigurationChanged(configuration);
                this.mLockTaskController.setWindowManager(windowManagerService);
                this.mTaskSupervisor.setWindowManager(windowManagerService);
                this.mRootWindowContainer.setWindowManager(windowManagerService);
                this.mBackNavigationController.setWindowManager(windowManagerService);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setUsageStatsManager(UsageStatsManagerInternal usageStatsManagerInternal) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mUsageStatsInternal = usageStatsManagerInternal;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public Context getUiContext() {
        return this.mUiContext;
    }

    public UserManagerService getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        }
        return this.mUserManager;
    }

    public AppOpsManager getAppOpsManager() {
        if (this.mAppOpsManager == null) {
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        }
        return this.mAppOpsManager;
    }

    public boolean hasUserRestriction(String str, int i) {
        return getUserManager().hasUserRestriction(str, i);
    }

    public boolean hasSystemAlertWindowPermission(int i, int i2, String str) {
        int noteOpNoThrow = getAppOpsManager().noteOpNoThrow(24, i, str, (String) null, "");
        return noteOpNoThrow == 3 ? checkPermission("android.permission.SYSTEM_ALERT_WINDOW", i2, i) == 0 : noteOpNoThrow == 0;
    }

    @VisibleForTesting
    public void setRecentTasks(RecentTasks recentTasks) {
        this.mRecentTasks = recentTasks;
        this.mTaskSupervisor.setRecentTasks(recentTasks);
    }

    public RecentTasks getRecentTasks() {
        return this.mRecentTasks;
    }

    public ClientLifecycleManager getLifecycleManager() {
        return this.mLifecycleManager;
    }

    public ActivityStartController getActivityStartController() {
        return this.mActivityStartController;
    }

    public TaskChangeNotificationController getTaskChangeNotificationController() {
        return this.mTaskChangeNotificationController;
    }

    public LockTaskController getLockTaskController() {
        return this.mLockTaskController;
    }

    public TransitionController getTransitionController() {
        return this.mWindowOrganizerController.getTransitionController();
    }

    public Configuration getGlobalConfigurationForCallingPid() {
        return getGlobalConfigurationForPid(Binder.getCallingPid());
    }

    public Configuration getGlobalConfigurationForPid(int i) {
        Configuration configuration;
        if (i == WindowManagerService.MY_PID || i < 0) {
            return getGlobalConfiguration();
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController process = this.mProcessMap.getProcess(i);
                configuration = process != null ? process.getConfiguration() : getGlobalConfiguration();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return configuration;
    }

    public ConfigurationInfo getDeviceConfigurationInfo() {
        ConfigurationInfo configurationInfo = new ConfigurationInfo();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Configuration globalConfigurationForCallingPid = getGlobalConfigurationForCallingPid();
                configurationInfo.reqTouchScreen = globalConfigurationForCallingPid.touchscreen;
                int i = globalConfigurationForCallingPid.keyboard;
                configurationInfo.reqKeyboardType = i;
                int i2 = globalConfigurationForCallingPid.navigation;
                configurationInfo.reqNavigation = i2;
                if (i2 == 2 || i2 == 3) {
                    configurationInfo.reqInputFeatures |= 2;
                }
                if (i != 0 && i != 1) {
                    configurationInfo.reqInputFeatures = 1 | configurationInfo.reqInputFeatures;
                }
                configurationInfo.reqGlEsVersion = this.GL_ES_VERSION;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return configurationInfo;
    }

    public BackgroundActivityStartCallback getBackgroundActivityStartCallback() {
        return this.mBackgroundActivityStartCallback;
    }

    public SparseArray<ActivityInterceptorCallback> getActivityInterceptorCallbacks() {
        return this.mActivityInterceptorCallbacks;
    }

    public final void start() {
        LocalServices.addService(ActivityTaskManagerInternal.class, this.mInternal);
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$Lifecycle */
    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public final ActivityTaskManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new ActivityTaskManagerService(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("activity_task", this.mService);
            this.mService.start();
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocked(SystemService.TargetUser targetUser) {
            synchronized (this.mService.getGlobalLock()) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mService.mTaskSupervisor.onUserUnlocked(targetUser.getUserIdentifier());
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            synchronized (this.mService.getGlobalLock()) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mService.mTaskSupervisor.mLaunchParamsPersister.onCleanupUser(targetUser.getUserIdentifier());
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public ActivityTaskManagerService getService() {
            return this.mService;
        }
    }

    public final int startActivity(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle) {
        return startActivityAsUser(iApplicationThread, str, str2, intent, str3, iBinder, str4, i, i2, profilerInfo, bundle, UserHandle.getCallingUserId());
    }

    public final int startActivities(IApplicationThread iApplicationThread, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) {
        assertPackageMatchesCallingUid(str);
        enforceNotIsolatedCaller("startActivities");
        return getActivityStartController().startActivities(iApplicationThread, -1, 0, -1, str, str2, intentArr, strArr, iBinder, SafeActivityOptions.fromBundle(bundle), handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, "startActivities"), "startActivities", null, BackgroundStartPrivileges.NONE);
    }

    public int startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) {
        return startActivityAsUser(iApplicationThread, str, str2, intent, str3, iBinder, str4, i, i2, profilerInfo, bundle, i3, true);
    }

    /* JADX WARN: Finally extract failed */
    public final int startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3, boolean z) {
        SafeActivityOptions fromBundle = SafeActivityOptions.fromBundle(bundle);
        if (fromBundle != null && fromBundle.getOriginalOptions().getTransientLaunch() && isCallerRecents(Binder.getCallingUid())) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    Trace.traceBegin(32L, "startExistingRecents");
                    if (this.mActivityStartController.startExistingRecentsIfPossible(intent, fromBundle.getOriginalOptions())) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        Trace.traceEnd(32L);
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return 2;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Trace.traceEnd(32L);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (Throwable th) {
                Trace.traceEnd(32L);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
        assertPackageMatchesCallingUid(str);
        enforceNotIsolatedCaller("startActivityAsUser");
        if ((intent == null || intent.getAction() == null || !intent.getAction().equals("android.app.sdksandbox.action.START_SANDBOXED_ACTIVITY")) ? false : true) {
            ((SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class)).enforceAllowedToHostSandboxedActivity(intent, Binder.getCallingUid(), str);
        }
        if (Process.isSdkSandboxUid(Binder.getCallingUid())) {
            SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
            if (sdkSandboxManagerLocal == null) {
                throw new IllegalStateException("SdkSandboxManagerLocal not found when starting an activity from an SDK sandbox uid.");
            }
            sdkSandboxManagerLocal.enforceAllowedToStartActivity(intent);
        }
        return getActivityStartController().obtainStarter(intent, "startActivityAsUser").setCaller(iApplicationThread).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setResultTo(iBinder).setResultWho(str4).setRequestCode(i).setStartFlags(i2).setProfilerInfo(profilerInfo).setActivityOptions(fromBundle).setUserId(getActivityStartController().checkTargetUser(i3, z, Binder.getCallingPid(), Binder.getCallingUid(), "startActivityAsUser")).execute();
    }

    public int startActivityIntentSender(IApplicationThread iApplicationThread, IIntentSender iIntentSender, IBinder iBinder, Intent intent, String str, IBinder iBinder2, String str2, int i, int i2, int i3, Bundle bundle) {
        enforceNotIsolatedCaller("startActivityIntentSender");
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        if (!(iIntentSender instanceof PendingIntentRecord)) {
            throw new IllegalArgumentException("Bad PendingIntent object");
        }
        PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                if (topDisplayFocusedRootTask != null && topDisplayFocusedRootTask.getTopResumedActivity() != null && topDisplayFocusedRootTask.getTopResumedActivity().info.applicationInfo.uid == Binder.getCallingUid()) {
                    this.mAppSwitchesState = 2;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return pendingIntentRecord.sendInner(iApplicationThread, 0, intent, str, iBinder, null, null, iBinder2, str2, i, i2, i3, bundle);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x010b A[Catch: all -> 0x01b3, TRY_ENTER, TryCatch #0 {all -> 0x01b3, blocks: (B:10:0x0018, B:12:0x0022, B:13:0x0025, B:16:0x002a, B:18:0x0030, B:19:0x0033, B:22:0x0038, B:27:0x005c, B:29:0x0074, B:33:0x007d, B:35:0x008f, B:37:0x009d, B:39:0x00a0, B:43:0x00ac, B:47:0x00f9, B:46:0x00e1, B:52:0x010b, B:54:0x0110, B:55:0x0117, B:58:0x011c, B:60:0x0143, B:61:0x0146, B:63:0x016b, B:64:0x016d, B:66:0x01a9, B:69:0x01ae), top: B:78:0x0018 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x011c A[Catch: all -> 0x01b3, TRY_ENTER, TryCatch #0 {all -> 0x01b3, blocks: (B:10:0x0018, B:12:0x0022, B:13:0x0025, B:16:0x002a, B:18:0x0030, B:19:0x0033, B:22:0x0038, B:27:0x005c, B:29:0x0074, B:33:0x007d, B:35:0x008f, B:37:0x009d, B:39:0x00a0, B:43:0x00ac, B:47:0x00f9, B:46:0x00e1, B:52:0x010b, B:54:0x0110, B:55:0x0117, B:58:0x011c, B:60:0x0143, B:61:0x0146, B:63:0x016b, B:64:0x016d, B:66:0x01a9, B:69:0x01ae), top: B:78:0x0018 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean startNextMatchingActivity(IBinder iBinder, Intent intent, Bundle bundle) {
        ActivityInfo activityInfo;
        List list;
        int size;
        int i;
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        SafeActivityOptions fromBundle = SafeActivityOptions.fromBundle(bundle);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    SafeActivityOptions.abort(fromBundle);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } else if (!isInRootTaskLocked.attachedToProcess()) {
                    SafeActivityOptions.abort(fromBundle);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } else {
                    Intent intent2 = new Intent(intent);
                    intent2.setDataAndType(isInRootTaskLocked.intent.getData(), isInRootTaskLocked.intent.getType());
                    intent2.setComponent(null);
                    boolean z = (intent2.getFlags() & 8) != 0;
                    try {
                        list = AppGlobals.getPackageManager().queryIntentActivities(intent2, isInRootTaskLocked.resolvedType, 66560L, UserHandle.getCallingUserId()).getList();
                        size = list != null ? list.size() : 0;
                    } catch (RemoteException unused) {
                    }
                    for (i = 0; i < size; i++) {
                        ResolveInfo resolveInfo = (ResolveInfo) list.get(i);
                        if (resolveInfo.activityInfo.packageName.equals(isInRootTaskLocked.packageName) && resolveInfo.activityInfo.name.equals(isInRootTaskLocked.info.name)) {
                            int i2 = i + 1;
                            activityInfo = i2 < size ? ((ResolveInfo) list.get(i2)).activityInfo : null;
                            if (z) {
                                try {
                                    Slog.v("ActivityTaskManager", "Next matching activity: found current " + isInRootTaskLocked.packageName + "/" + isInRootTaskLocked.info.name);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Next matching activity: next is ");
                                    sb.append(activityInfo == null ? "null" : activityInfo.packageName + "/" + activityInfo.name);
                                    Slog.v("ActivityTaskManager", sb.toString());
                                } catch (RemoteException unused2) {
                                }
                            }
                            if (activityInfo != null) {
                                SafeActivityOptions.abort(fromBundle);
                                if (z) {
                                    Slog.d("ActivityTaskManager", "Next matching activity: nothing found");
                                }
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return false;
                            }
                            intent2.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
                            intent2.setFlags(intent2.getFlags() & (-503316481));
                            boolean z2 = isInRootTaskLocked.finishing;
                            isInRootTaskLocked.finishing = true;
                            ActivityRecord activityRecord = isInRootTaskLocked.resultTo;
                            String str = isInRootTaskLocked.resultWho;
                            int i3 = isInRootTaskLocked.requestCode;
                            isInRootTaskLocked.resultTo = null;
                            if (activityRecord != null) {
                                activityRecord.removeResultsLocked(isInRootTaskLocked, str, i3);
                            }
                            long clearCallingIdentity = Binder.clearCallingIdentity();
                            int execute = getActivityStartController().obtainStarter(intent2, "startNextMatchingActivity").setCaller(isInRootTaskLocked.app.getThread()).setResolvedType(isInRootTaskLocked.resolvedType).setActivityInfo(activityInfo).setResultTo(activityRecord != null ? activityRecord.token : null).setResultWho(str).setRequestCode(i3).setCallingPid(-1).setCallingUid(isInRootTaskLocked.launchedFromUid).setCallingPackage(isInRootTaskLocked.launchedFromPackage).setCallingFeatureId(isInRootTaskLocked.launchedFromFeatureId).setRealCallingPid(-1).setRealCallingUid(isInRootTaskLocked.launchedFromUid).setActivityOptions(fromBundle).execute();
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            isInRootTaskLocked.finishing = z2;
                            if (execute != 0) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return false;
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return true;
                        }
                    }
                    activityInfo = null;
                    if (activityInfo != null) {
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean isDreaming() {
        return this.mActiveDreamComponent != null;
    }

    public boolean canLaunchDreamActivity(String str) {
        if (this.mActiveDreamComponent == null || str == null) {
            if (ProtoLogCache.WM_DEBUG_DREAM_enabled) {
                ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_DREAM, -787664727, 0, "Cannot launch dream activity due to invalid state. dream component: %s packageName: %s", new Object[]{String.valueOf(this.mActiveDreamComponent), String.valueOf(str)});
            }
            return false;
        } else if (str.equals(this.mActiveDreamComponent.getPackageName())) {
            return true;
        } else {
            if (ProtoLogCache.WM_DEBUG_DREAM_enabled) {
                ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_DREAM, 601283564, 0, "Dream packageName does not match active dream. Package %s does not match %s", new Object[]{str, String.valueOf(this.mActiveDreamComponent)});
            }
            return false;
        }
    }

    public final void enforceCallerIsDream(String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (canLaunchDreamActivity(str)) {
                return;
            }
            throw new SecurityException("The dream activity can be started only when the device is dreaming and only by the active dream package.");
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean startDreamActivity(Intent intent) {
        assertPackageMatchesCallingUid(intent.getPackage());
        enforceCallerIsDream(intent.getPackage());
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.theme = 16974854;
        activityInfo.exported = true;
        activityInfo.name = DreamActivity.class.getName();
        activityInfo.enabled = true;
        activityInfo.launchMode = 3;
        activityInfo.persistableMode = 1;
        activityInfo.screenOrientation = -1;
        activityInfo.colorMode = 0;
        activityInfo.flags |= FrameworkStatsLog.f418x97ec91aa;
        activityInfo.resizeMode = 0;
        activityInfo.configChanges = -1;
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchActivityType(5);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController process = this.mProcessMap.getProcess(Binder.getCallingPid());
                ApplicationInfo applicationInfo = process.mInfo;
                activityInfo.packageName = applicationInfo.packageName;
                activityInfo.applicationInfo = applicationInfo;
                activityInfo.processName = process.mName;
                activityInfo.uiOptions = applicationInfo.uiOptions;
                activityInfo.taskAffinity = "android:" + activityInfo.packageName + "/dream";
                int callingUid = Binder.getCallingUid();
                int callingPid = Binder.getCallingPid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                getActivityStartController().obtainStarter(intent, "dream").setCallingUid(callingUid).setCallingPid(callingPid).setCallingPackage(intent.getPackage()).setActivityInfo(activityInfo).setActivityOptions(makeBasic.toBundle()).setRealCallingUid(Binder.getCallingUid()).setBackgroundStartPrivileges(BackgroundStartPrivileges.ALLOW_BAL).execute();
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return true;
    }

    public final WaitResult startActivityAndWait(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, int i3) {
        assertPackageMatchesCallingUid(str);
        WaitResult waitResult = new WaitResult();
        enforceNotIsolatedCaller("startActivityAndWait");
        getActivityStartController().obtainStarter(intent, "startActivityAndWait").setCaller(iApplicationThread).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setResultTo(iBinder).setResultWho(str4).setRequestCode(i).setStartFlags(i2).setActivityOptions(bundle).setUserId(handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i3, "startActivityAndWait")).setProfilerInfo(profilerInfo).setWaitResult(waitResult).execute();
        return waitResult;
    }

    public final int startActivityWithConfig(IApplicationThread iApplicationThread, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i, int i2, Configuration configuration, Bundle bundle, int i3) {
        assertPackageMatchesCallingUid(str);
        enforceNotIsolatedCaller("startActivityWithConfig");
        return getActivityStartController().obtainStarter(intent, "startActivityWithConfig").setCaller(iApplicationThread).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setResultTo(iBinder).setResultWho(str4).setRequestCode(i).setStartFlags(i2).setGlobalConfiguration(configuration).setActivityOptions(bundle).setUserId(handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i3, "startActivityWithConfig")).execute();
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x006e, code lost:
        if (r6.getComponent() == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0074, code lost:
        if (r6.getSelector() != null) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x007e, code lost:
        throw new java.lang.SecurityException("Selector not allowed with ignoreTargetSecurity");
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0086, code lost:
        throw new java.lang.SecurityException("Component must be specified with ignoreTargetSecurity");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int startActivityAsCaller(IApplicationThread iApplicationThread, String str, Intent intent, String str2, IBinder iBinder, String str3, int i, int i2, ProfilerInfo profilerInfo, Bundle bundle, boolean z, int i3) {
        ActivityRecord isInAnyTask;
        int i4;
        String str4;
        String str5;
        boolean isResolverOrChildActivity;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (iBinder == null) {
                    throw new SecurityException("Must be called from an activity");
                }
                isInAnyTask = ActivityRecord.isInAnyTask(iBinder);
                if (isInAnyTask == null) {
                    throw new SecurityException("Called with bad activity token: " + iBinder);
                } else if (isInAnyTask.app == null) {
                    throw new SecurityException("Called without a process attached to activity");
                } else {
                    if (checkCallingPermission("android.permission.START_ACTIVITY_AS_CALLER") != 0) {
                        if (!isInAnyTask.info.packageName.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
                            throw new SecurityException("Must be called from an activity that is declared in the android package");
                        }
                        if (UserHandle.getAppId(isInAnyTask.app.mUid) != 1000 && isInAnyTask.app.mUid != isInAnyTask.launchedFromUid) {
                            throw new SecurityException("Calling activity in uid " + isInAnyTask.app.mUid + " must be system uid or original calling uid " + isInAnyTask.launchedFromUid);
                        }
                    }
                    i4 = isInAnyTask.launchedFromUid;
                    str4 = isInAnyTask.launchedFromPackage;
                    str5 = isInAnyTask.launchedFromFeatureId;
                    isResolverOrChildActivity = isInAnyTask.isResolverOrChildActivity();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (i3 == -10000) {
            i3 = UserHandle.getUserId(isInAnyTask.app.mUid);
        }
        ActivityStarter ignoreTargetSecurity = getActivityStartController().obtainStarter(intent, "startActivityAsCaller").setCallingUid(i4).setCallingPackage(str4).setCallingFeatureId(str5).setResolvedType(str2).setResultTo(iBinder).setResultWho(str3).setRequestCode(i).setStartFlags(i2).setActivityOptions(bundle).setUserId(i3).setIgnoreTargetSecurity(z);
        if (isResolverOrChildActivity) {
            i4 = 0;
        }
        return ignoreTargetSecurity.setFilterCallingUid(i4).setBackgroundStartPrivileges(BackgroundStartPrivileges.ALLOW_BAL).execute();
    }

    public int handleIncomingUser(int i, int i2, int i3, String str) {
        return this.mAmInternal.handleIncomingUser(i, i2, i3, false, 0, str, (String) null);
    }

    public int startVoiceActivity(String str, String str2, int i, int i2, Intent intent, String str3, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor, int i3, ProfilerInfo profilerInfo, Bundle bundle, int i4) {
        assertPackageMatchesCallingUid(str);
        this.mAmInternal.enforceCallingPermission("android.permission.BIND_VOICE_INTERACTION", "startVoiceActivity()");
        if (iVoiceInteractionSession == null || iVoiceInteractor == null) {
            throw new NullPointerException("null session or interactor");
        }
        return getActivityStartController().obtainStarter(intent, "startVoiceActivity").setCallingUid(i2).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setVoiceSession(iVoiceInteractionSession).setVoiceInteractor(iVoiceInteractor).setStartFlags(i3).setProfilerInfo(profilerInfo).setActivityOptions(bundle).setUserId(handleIncomingUser(i, i2, i4, "startVoiceActivity")).setBackgroundStartPrivileges(BackgroundStartPrivileges.ALLOW_BAL).execute();
    }

    public String getVoiceInteractorPackageName(IBinder iBinder) {
        return ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).getVoiceInteractorPackageName(iBinder);
    }

    public int startAssistantActivity(String str, String str2, int i, int i2, Intent intent, String str3, Bundle bundle, int i3) {
        assertPackageMatchesCallingUid(str);
        this.mAmInternal.enforceCallingPermission("android.permission.BIND_VOICE_INTERACTION", "startAssistantActivity()");
        int handleIncomingUser = handleIncomingUser(i, i2, i3, "startAssistantActivity");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getActivityStartController().obtainStarter(intent, "startAssistantActivity").setCallingUid(i2).setCallingPackage(str).setCallingFeatureId(str2).setResolvedType(str3).setActivityOptions(bundle).setUserId(handleIncomingUser).setBackgroundStartPrivileges(BackgroundStartPrivileges.ALLOW_BAL).execute();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void startRecentsActivity(Intent intent, long j, IRecentsAnimationRunner iRecentsAnimationRunner) {
        enforceTaskPermission("startRecentsActivity()");
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                RecentsAnimation recentsAnimation = new RecentsAnimation(this, this.mTaskSupervisor, getActivityStartController(), this.mWindowManager, intent, this.mRecentTasks.getRecentsComponent(), this.mRecentTasks.getRecentsComponentFeatureId(), this.mRecentTasks.getRecentsComponentUid(), getProcessController(callingPid, callingUid));
                if (iRecentsAnimationRunner == null) {
                    recentsAnimation.preloadRecentsActivity();
                } else {
                    recentsAnimation.startRecentsActivity(iRecentsAnimationRunner, j);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int startActivityFromRecents(int i, Bundle bundle) {
        this.mAmInternal.enforceCallingPermission("android.permission.START_TASKS_FROM_RECENTS", "startActivityFromRecents()");
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        SafeActivityOptions fromBundle = SafeActivityOptions.fromBundle(bundle);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mTaskSupervisor.startActivityFromRecents(callingPid, callingUid, i, fromBundle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int startActivityFromGameSession(IApplicationThread iApplicationThread, String str, String str2, int i, int i2, Intent intent, int i3, int i4) {
        if (checkCallingPermission("android.permission.MANAGE_GAME_ACTIVITY") != 0) {
            String str3 = "Permission Denial: startActivityFromGameSession() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.MANAGE_GAME_ACTIVITY";
            Slog.w("ActivityTaskManager", str3);
            throw new SecurityException(str3);
        }
        assertPackageMatchesCallingUid(str);
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchTaskId(i3);
        int handleIncomingUser = handleIncomingUser(i, i2, i4, "startActivityFromGameSession");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getActivityStartController().obtainStarter(intent, "startActivityFromGameSession").setCaller(iApplicationThread).setCallingUid(i2).setCallingPid(i).setCallingPackage(intent.getPackage()).setCallingFeatureId(str2).setUserId(handleIncomingUser).setActivityOptions(makeBasic.toBundle()).setRealCallingUid(Binder.getCallingUid()).execute();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public BackNavigationInfo startBackNavigation(RemoteCallback remoteCallback, BackAnimationAdapter backAnimationAdapter) {
        this.mAmInternal.enforceCallingPermission("android.permission.START_TASKS_FROM_RECENTS", "startBackNavigation()");
        return this.mBackNavigationController.startBackNavigation(remoteCallback, backAnimationAdapter);
    }

    public final boolean isActivityStartAllowedOnDisplay(int i, Intent intent, String str, int i2) {
        boolean canPlaceEntityOnDisplay;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ActivityInfo resolveActivityInfoForIntent = resolveActivityInfoForIntent(intent, str, i2, callingUid, callingPid);
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                canPlaceEntityOnDisplay = this.mTaskSupervisor.canPlaceEntityOnDisplay(i, callingPid, callingUid, resolveActivityInfoForIntent);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return canPlaceEntityOnDisplay;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ActivityInfo resolveActivityInfoForIntent(Intent intent, String str, int i, int i2, int i3) {
        return this.mAmInternal.getActivityInfoForUser(this.mTaskSupervisor.resolveActivity(intent, str, 0, null, i, ActivityStarter.computeResolveFilterUid(i2, i2, -10000), i3), i);
    }

    public IActivityClientController getActivityClientController() {
        return this.mActivityClientController;
    }

    public void applyUpdateLockStateLocked(final ActivityRecord activityRecord) {
        final boolean z = activityRecord != null && activityRecord.immersive;
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$applyUpdateLockStateLocked$0(z, activityRecord);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyUpdateLockStateLocked$0(boolean z, ActivityRecord activityRecord) {
        if (this.mUpdateLock.isHeld() != z) {
            if (ProtoLogCache.WM_DEBUG_IMMERSIVE_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_IMMERSIVE, 556758086, 0, (String) null, new Object[]{String.valueOf(z), String.valueOf(activityRecord)});
            }
            if (z) {
                this.mUpdateLock.acquire();
            } else {
                this.mUpdateLock.release();
            }
        }
    }

    public boolean isTopActivityImmersive() {
        enforceNotIsolatedCaller("isTopActivityImmersive");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                boolean z = false;
                if (topDisplayFocusedRootTask == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                ActivityRecord activityRecord = topDisplayFocusedRootTask.topRunningActivity();
                if (activityRecord != null && activityRecord.immersive) {
                    z = true;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getFrontActivityScreenCompatMode() {
        enforceNotIsolatedCaller("getFrontActivityScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                ActivityRecord activityRecord = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.topRunningActivity() : null;
                if (activityRecord == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -3;
                }
                int computeCompatModeLocked = this.mCompatModePackages.computeCompatModeLocked(activityRecord.info.applicationInfo);
                WindowManagerService.resetPriorityAfterLockedSection();
                return computeCompatModeLocked;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setFrontActivityScreenCompatMode(int i) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setFrontActivityScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                ActivityRecord activityRecord = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.topRunningActivity() : null;
                if (activityRecord == null) {
                    Slog.w("ActivityTaskManager", "setFrontActivityScreenCompatMode failed: no top activity");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                this.mCompatModePackages.setPackageScreenCompatModeLocked(activityRecord.info.applicationInfo, i);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public ActivityTaskManager.RootTaskInfo getFocusedRootTaskInfo() throws RemoteException {
        enforceTaskPermission("getFocusedRootTaskInfo()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                if (topDisplayFocusedRootTask != null) {
                    ActivityTaskManager.RootTaskInfo rootTaskInfo = this.mRootWindowContainer.getRootTaskInfo(topDisplayFocusedRootTask.mTaskId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return rootTaskInfo;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return null;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setFocusedRootTask(int i) {
        enforceTaskPermission("setFocusedRootTask()");
        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, 255339989, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task rootTask = this.mRootWindowContainer.getRootTask(i);
                if (rootTask == null) {
                    Slog.w("ActivityTaskManager", "setFocusedRootTask: No task with id=" + i);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ActivityRecord activityRecord = rootTask.topRunningActivity();
                if (activityRecord != null && activityRecord.moveFocusableActivityToTop("setFocusedRootTask")) {
                    this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setFocusedTask(int i) {
        enforceTaskPermission("setFocusedTask()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                setFocusedTask(i, null);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setFocusedTask(int i, ActivityRecord activityRecord) {
        ActivityRecord activityRecord2;
        TaskFragment taskFragment;
        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, -55185509, 1, (String) null, new Object[]{Long.valueOf(i), String.valueOf(activityRecord)});
        }
        Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 0);
        if (anyTaskForId == null || (activityRecord2 = anyTaskForId.topRunningActivityLocked()) == null) {
            return;
        }
        Transition createTransition = (getTransitionController().isCollecting() || !getTransitionController().isShellTransitionsEnabled()) ? null : getTransitionController().createTransition(3);
        if (createTransition != null) {
            createTransition.setReady(anyTaskForId, true);
        }
        boolean moveFocusableActivityToTop = activityRecord2.moveFocusableActivityToTop("setFocusedTask");
        if (moveFocusableActivityToTop) {
            if (createTransition != null) {
                getTransitionController().requestStartTransition(createTransition, null, null, null);
            }
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        } else if (activityRecord != null && activityRecord.isFocusable() && (taskFragment = activityRecord.getTaskFragment()) != null && taskFragment.isEmbedded()) {
            activityRecord.getDisplayContent().setFocusedApp(activityRecord);
            this.mWindowManager.updateFocusedWindowLocked(0, true);
        }
        if (createTransition == null || moveFocusableActivityToTop) {
            return;
        }
        createTransition.abort();
    }

    public boolean removeTask(int i) {
        this.mAmInternal.enforceCallingPermission("android.permission.REMOVE_TASKS", "removeTask()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "removeTask: No task remove with id=" + i);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                if (anyTaskForId.isLeafTask()) {
                    this.mTaskSupervisor.removeTask(anyTaskForId, true, true, "remove-task");
                } else {
                    this.mTaskSupervisor.removeRootTask(anyTaskForId);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                WindowManagerService.resetPriorityAfterLockedSection();
                return true;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void removeAllVisibleRecentTasks() {
        this.mAmInternal.enforceCallingPermission("android.permission.REMOVE_TASKS", "removeAllVisibleRecentTasks()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                getRecentTasks().removeAllVisibleTasks(this.mAmInternal.getCurrentUserId());
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public Rect getTaskBounds(int i) {
        enforceTaskPermission("getTaskBounds()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Rect rect = new Rect();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "getTaskBounds: taskId=" + i + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return rect;
                }
                if (anyTaskForId.getParent() != null) {
                    rect.set(anyTaskForId.getBounds());
                } else {
                    Rect rect2 = anyTaskForId.mLastNonFullscreenBounds;
                    if (rect2 != null) {
                        rect.set(rect2);
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return rect;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ActivityManager.TaskDescription getTaskDescription(int i) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                enforceTaskPermission("getTaskDescription()");
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                ActivityManager.TaskDescription taskDescription = anyTaskForId.getTaskDescription();
                WindowManagerService.resetPriorityAfterLockedSection();
                return taskDescription;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setLocusId(LocusId locusId, IBinder iBinder) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked != null) {
                    isInRootTaskLocked.setLocusId(locusId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public NeededUriGrants collectGrants(Intent intent, ActivityRecord activityRecord) {
        if (activityRecord != null) {
            return this.mUgmInternal.checkGrantUriPermissionFromIntent(intent, Binder.getCallingUid(), activityRecord.packageName, activityRecord.mUserId);
        }
        return null;
    }

    public void unhandledBack() {
        this.mAmInternal.enforceCallingPermission("android.permission.FORCE_BACK", "unhandledBack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                if (topDisplayFocusedRootTask != null) {
                    topDisplayFocusedRootTask.unhandledBackLocked();
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void moveTaskToFront(IApplicationThread iApplicationThread, String str, int i, int i2, Bundle bundle) {
        this.mAmInternal.enforceCallingPermission("android.permission.REORDER_TASKS", "moveTaskToFront()");
        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 2117696413, 1, (String) null, new Object[]{Long.valueOf(i)});
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                moveTaskToFrontLocked(iApplicationThread, str, i, i2, SafeActivityOptions.fromBundle(bundle));
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void moveTaskToFrontLocked(IApplicationThread iApplicationThread, String str, int i, int i2, SafeActivityOptions safeActivityOptions) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        assertPackageMatchesCallingUid(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (!getActivityStartController().getBackgroundActivityLaunchController().shouldAbortBackgroundActivityStart(callingUid, callingPid, str, -1, -1, iApplicationThread != null ? getProcessController(iApplicationThread) : null, null, BackgroundStartPrivileges.NONE, null, null) || isBackgroundActivityStartsEnabled()) {
            try {
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i);
                if (anyTaskForId == null) {
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1474292612, 1, (String) null, new Object[]{Long.valueOf(i)});
                    }
                    SafeActivityOptions.abort(safeActivityOptions);
                } else if (getLockTaskController().isLockTaskModeViolation(anyTaskForId)) {
                    Slog.e("ActivityTaskManager", "moveTaskToFront: Attempt to violate Lock Task Mode");
                    SafeActivityOptions.abort(safeActivityOptions);
                } else {
                    this.mTaskSupervisor.findTaskToMoveToFront(anyTaskForId, i2, safeActivityOptions != null ? safeActivityOptions.getOptions(this.mTaskSupervisor) : null, "moveTaskToFront", false);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final boolean isSameApp(int i, String str) {
        if (i == 0 || i == 1000) {
            return true;
        }
        return this.mPmInternal.isSameApp(str, i, UserHandle.getUserId(i));
    }

    public void assertPackageMatchesCallingUid(String str) {
        int callingUid = Binder.getCallingUid();
        if (isSameApp(callingUid, str)) {
            return;
        }
        String str2 = "Permission Denial: package=" + str + " does not belong to uid=" + callingUid;
        Slog.w("ActivityTaskManager", str2);
        throw new SecurityException(str2);
    }

    public int getBalAppSwitchesState() {
        return this.mAppSwitchesState;
    }

    public void registerAnrController(AnrController anrController) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAnrController.add(anrController);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterAnrController(AnrController anrController) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAnrController.remove(anrController);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public AnrController getAnrController(ApplicationInfo applicationInfo) {
        ArrayList arrayList;
        AnrController anrController = null;
        if (applicationInfo == null || applicationInfo.packageName == null) {
            return null;
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                arrayList = new ArrayList(this.mAnrController);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        String str = applicationInfo.packageName;
        int i = applicationInfo.uid;
        Iterator it = arrayList.iterator();
        long j = 0;
        while (it.hasNext()) {
            AnrController anrController2 = (AnrController) it.next();
            long anrDelayMillis = anrController2.getAnrDelayMillis(str, i);
            if (anrDelayMillis > 0 && anrDelayMillis > j) {
                anrController = anrController2;
                j = anrDelayMillis;
            }
        }
        return anrController;
    }

    public void setActivityController(IActivityController iActivityController, boolean z) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "setActivityController()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mController = iActivityController;
                this.mControllerIsAMonkey = z;
                Watchdog.getInstance().setActivityController(iActivityController);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isControllerAMonkey() {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                z = this.mController != null && this.mControllerIsAMonkey;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int i) {
        return getTasks(i, false, false, -1);
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int i, boolean z, boolean z2, int i2) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int i3 = (z ? 1 : 0) | (z2 ? 8 : 0) | (isCrossUserAllowed(callingPid, callingUid) ? 4 : 0);
        int[] profileIds = getUserManager().getProfileIds(UserHandle.getUserId(callingUid), true);
        ArraySet<Integer> arraySet = new ArraySet<>();
        for (int i4 : profileIds) {
            arraySet.add(Integer.valueOf(i4));
        }
        ArrayList arrayList = new ArrayList();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mRootWindowContainer.getRunningTasks(i, arrayList, i3 | (isGetTasksAllowed("getTasks", callingPid, callingUid) ? 2 : 0), callingUid, arraySet, i2);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return arrayList;
    }

    public void moveTaskToRootTask(int i, int i2, boolean z) {
        enforceTaskPermission("moveTaskToRootTask()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "moveTaskToRootTask: No task for id=" + i);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -677449371, 53, (String) null, new Object[]{Long.valueOf(i), Long.valueOf(i2), Boolean.valueOf(z)});
                }
                Task rootTask = this.mRootWindowContainer.getRootTask(i2);
                if (rootTask == null) {
                    throw new IllegalStateException("moveTaskToRootTask: No rootTask for rootTaskId=" + i2);
                } else if (!rootTask.isActivityTypeStandardOrUndefined()) {
                    throw new IllegalArgumentException("moveTaskToRootTask: Attempt to move task " + i + " to rootTask " + i2);
                } else {
                    anyTaskForId.reparent(rootTask, z, 1, true, false, "moveTaskToRootTask");
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void removeRootTasksInWindowingModes(int[] iArr) {
        enforceTaskPermission("removeRootTasksInWindowingModes()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mRootWindowContainer.removeRootTasksInWindowingModes(iArr);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void removeRootTasksWithActivityTypes(int[] iArr) {
        enforceTaskPermission("removeRootTasksWithActivityTypes()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mRootWindowContainer.removeRootTasksWithActivityTypes(iArr);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2, int i3) {
        ParceledListSlice<ActivityManager.RecentTaskInfo> recentTasks;
        int callingUid = Binder.getCallingUid();
        int handleIncomingUser = handleIncomingUser(Binder.getCallingPid(), callingUid, i3, "getRecentTasks");
        boolean isGetTasksAllowed = isGetTasksAllowed("getRecentTasks", Binder.getCallingPid(), callingUid);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                recentTasks = this.mRecentTasks.getRecentTasks(i, i2, isGetTasksAllowed, handleIncomingUser, callingUid);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return recentTasks;
    }

    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfos() {
        ArrayList<ActivityTaskManager.RootTaskInfo> allRootTaskInfos;
        enforceTaskPermission("getAllRootTaskInfos()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                allRootTaskInfos = this.mRootWindowContainer.getAllRootTaskInfos(-1);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return allRootTaskInfos;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ActivityTaskManager.RootTaskInfo getRootTaskInfo(int i, int i2) {
        ActivityTaskManager.RootTaskInfo rootTaskInfo;
        enforceTaskPermission("getRootTaskInfo()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                rootTaskInfo = this.mRootWindowContainer.getRootTaskInfo(i, i2);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return rootTaskInfo;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<ActivityTaskManager.RootTaskInfo> getAllRootTaskInfosOnDisplay(int i) {
        ArrayList<ActivityTaskManager.RootTaskInfo> allRootTaskInfos;
        enforceTaskPermission("getAllRootTaskInfosOnDisplay()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                allRootTaskInfos = this.mRootWindowContainer.getAllRootTaskInfos(i);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return allRootTaskInfos;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ActivityTaskManager.RootTaskInfo getRootTaskInfoOnDisplay(int i, int i2, int i3) {
        ActivityTaskManager.RootTaskInfo rootTaskInfo;
        enforceTaskPermission("getRootTaskInfoOnDisplay()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                rootTaskInfo = this.mRootWindowContainer.getRootTaskInfo(i, i2, i3);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return rootTaskInfo;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void cancelRecentsAnimation(boolean z) {
        enforceTaskPermission("cancelRecentsAnimation()");
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mWindowManager.cancelRecentsAnimation(z ? 2 : 0, "cancelRecentsAnimation/uid=" + callingUid);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void startSystemLockTaskMode(int i) {
        enforceTaskPermission("startSystemLockTaskMode");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 0);
                if (anyTaskForId != null) {
                    anyTaskForId.getRootTask().moveToFront("startSystemLockTaskMode");
                    startLockTaskMode(anyTaskForId, true);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void stopSystemLockTaskMode() throws RemoteException {
        enforceTaskPermission("stopSystemLockTaskMode");
        stopLockTaskModeInternal(null, true);
    }

    public void startLockTaskMode(Task task, boolean z) {
        if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
            ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, 295861935, 0, (String) null, new Object[]{String.valueOf(task)});
        }
        if (task == null || task.mLockTaskAuth == 0) {
            return;
        }
        Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
        if (topDisplayFocusedRootTask == null || task != topDisplayFocusedRootTask.getTopMostTask()) {
            throw new IllegalArgumentException("Invalid task, not in foreground");
        }
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mRootWindowContainer.removeRootTasksInWindowingModes(2);
            getLockTaskController().startLockTaskMode(task, z, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void stopLockTaskModeInternal(IBinder iBinder, boolean z) {
        Task task;
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (iBinder != null) {
                    ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                    if (forTokenLocked != null) {
                        task = forTokenLocked.getTask();
                    } else {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                } else {
                    task = null;
                }
                getLockTaskController().stopLockTaskMode(task, z, callingUid);
                WindowManagerService.resetPriorityAfterLockedSection();
                TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
                if (telecomManager != null) {
                    telecomManager.showInCallScreen(false);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateLockTaskPackages(int i, String[] strArr) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != 1000) {
            this.mAmInternal.enforceCallingPermission("android.permission.UPDATE_LOCK_TASK_PACKAGES", "updateLockTaskPackages()");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, 715749922, 1, (String) null, new Object[]{Long.valueOf(i), String.valueOf(Arrays.toString(strArr))});
                }
                getLockTaskController().updateLockTaskPackages(i, strArr);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isInLockTaskMode() {
        return getLockTaskModeState() != 0;
    }

    public int getLockTaskModeState() {
        return getLockTaskController().getLockTaskModeState();
    }

    public List<IBinder> getAppTasks(String str) {
        assertPackageMatchesCallingUid(str);
        return getAppTasks(str, Binder.getCallingUid());
    }

    public final List<IBinder> getAppTasks(String str, int i) {
        ArrayList<IBinder> appTasksList;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                appTasksList = this.mRecentTasks.getAppTasksList(i, str);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return appTasksList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void finishVoiceTask(IVoiceInteractionSession iVoiceInteractionSession) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mRootWindowContainer.finishVoiceTask(iVoiceInteractionSession);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void reportAssistContextExtras(IBinder iBinder, Bundle bundle, AssistStructure assistStructure, AssistContent assistContent, Uri uri) {
        Bundle bundle2;
        PendingAssistExtras pendingAssistExtras = (PendingAssistExtras) iBinder;
        synchronized (pendingAssistExtras) {
            pendingAssistExtras.result = bundle;
            pendingAssistExtras.structure = assistStructure;
            pendingAssistExtras.content = assistContent;
            if (uri != null) {
                pendingAssistExtras.extras.putParcelable("android.intent.extra.REFERRER", uri);
            }
            if (pendingAssistExtras.activity.isAttached()) {
                if (assistStructure != null) {
                    assistStructure.setTaskId(pendingAssistExtras.activity.getTask().mTaskId);
                    assistStructure.setActivityComponent(pendingAssistExtras.activity.mActivityComponent);
                    assistStructure.setHomeActivity(pendingAssistExtras.isHome);
                }
                pendingAssistExtras.haveResult = true;
                pendingAssistExtras.notifyAll();
                if (pendingAssistExtras.intent == null && pendingAssistExtras.receiver == null) {
                    return;
                }
                synchronized (this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        buildAssistBundleLocked(pendingAssistExtras, bundle);
                        boolean remove = this.mPendingAssistExtras.remove(pendingAssistExtras);
                        this.mUiHandler.removeCallbacks(pendingAssistExtras);
                        if (!remove) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        IAssistDataReceiver iAssistDataReceiver = pendingAssistExtras.receiver;
                        if (iAssistDataReceiver != null) {
                            bundle2 = new Bundle();
                            bundle2.putInt("taskId", pendingAssistExtras.activity.getTask().mTaskId);
                            bundle2.putBinder("activityId", pendingAssistExtras.activity.assistToken);
                            bundle2.putBundle("data", pendingAssistExtras.extras);
                            bundle2.putParcelable("structure", pendingAssistExtras.structure);
                            bundle2.putParcelable("content", pendingAssistExtras.content);
                            bundle2.putBundle("receiverExtras", pendingAssistExtras.receiverExtras);
                        } else {
                            bundle2 = null;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        if (iAssistDataReceiver != null) {
                            try {
                                iAssistDataReceiver.onHandleAssistData(bundle2);
                                return;
                            } catch (RemoteException unused) {
                                return;
                            }
                        }
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            pendingAssistExtras.intent.replaceExtras(pendingAssistExtras.extras);
                            pendingAssistExtras.intent.setFlags(872415232);
                            this.mInternal.closeSystemDialogs("assist");
                            try {
                                this.mContext.startActivityAsUser(pendingAssistExtras.intent, new UserHandle(pendingAssistExtras.userHandle));
                            } catch (ActivityNotFoundException e) {
                                Slog.w("ActivityTaskManager", "No activity to handle assist action.", e);
                            }
                        } finally {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }
    }

    public int addAppTask(IBinder iBinder, Intent intent, ActivityManager.TaskDescription taskDescription, Bitmap bitmap) throws RemoteException {
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    throw new IllegalArgumentException("Activity does not exist; token=" + iBinder);
                }
                ComponentName component = intent.getComponent();
                if (component == null) {
                    throw new IllegalArgumentException("Intent " + intent + " must specify explicit component");
                } else if (bitmap.getWidth() != this.mThumbnailWidth || bitmap.getHeight() != this.mThumbnailHeight) {
                    throw new IllegalArgumentException("Bad thumbnail size: got " + bitmap.getWidth() + "x" + bitmap.getHeight() + ", require " + this.mThumbnailWidth + "x" + this.mThumbnailHeight);
                } else {
                    if (intent.getSelector() != null) {
                        intent.setSelector(null);
                    }
                    if (intent.getSourceBounds() != null) {
                        intent.setSourceBounds(null);
                    }
                    if ((intent.getFlags() & 524288) != 0 && (intent.getFlags() & IInstalld.FLAG_FORCE) == 0) {
                        intent.addFlags(IInstalld.FLAG_FORCE);
                    }
                    ActivityInfo activityInfo = AppGlobals.getPackageManager().getActivityInfo(component, 1024L, UserHandle.getUserId(callingUid));
                    if (activityInfo != null && activityInfo.applicationInfo.uid == callingUid) {
                        Task rootTask = isInRootTaskLocked.getRootTask();
                        Task build = new Task.Builder(this).setWindowingMode(rootTask.getWindowingMode()).setActivityType(rootTask.getActivityType()).setActivityInfo(activityInfo).setIntent(intent).setTaskId(rootTask.getDisplayArea().getNextRootTaskId()).build();
                        if (!this.mRecentTasks.addToBottom(build)) {
                            rootTask.removeChild(build, "addAppTask");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return -1;
                        }
                        build.getTaskDescription().copyFrom(taskDescription);
                        int i = build.mTaskId;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return i;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Can't add task for another application: target uid=");
                    sb.append(activityInfo == null ? -1 : activityInfo.applicationInfo.uid);
                    sb.append(", calling uid=");
                    sb.append(callingUid);
                    Slog.e("ActivityTaskManager", sb.toString());
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -1;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public Point getAppTaskThumbnailSize() {
        Point point;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                point = new Point(this.mThumbnailWidth, this.mThumbnailHeight);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return point;
    }

    public void setTaskResizeable(int i, int i2) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "setTaskResizeable: taskId=" + i + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                anyTaskForId.setResizeMode(i2);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void resizeTask(int i, final Rect rect, final int i2) {
        enforceTaskPermission("resizeTask()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                final Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 0);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "resizeTask: taskId=" + i + " not found");
                } else if (!anyTaskForId.getWindowConfiguration().canResizeTask()) {
                    Slog.w("ActivityTaskManager", "resizeTask not allowed on task=" + anyTaskForId);
                } else {
                    final boolean z = (i2 & 1) != 0;
                    if (!getTransitionController().isShellTransitionsEnabled()) {
                        anyTaskForId.resize(rect, i2, z);
                    } else {
                        final Transition transition = new Transition(6, 0, getTransitionController(), this.mWindowManager.mSyncEngine);
                        if (this.mWindowManager.mSyncEngine.hasActiveSync()) {
                            this.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda8
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ActivityTaskManagerService.this.lambda$resizeTask$1(transition);
                                }
                            }, new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda9
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ActivityTaskManagerService.this.lambda$resizeTask$2(anyTaskForId, transition, rect, i2, z);
                                }
                            });
                        } else {
                            getTransitionController().moveToCollecting(transition);
                            getTransitionController().requestStartTransition(transition, anyTaskForId, null, null);
                            getTransitionController().collect(anyTaskForId);
                            anyTaskForId.resize(rect, i2, z);
                            transition.setReady(anyTaskForId, true);
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resizeTask$1(Transition transition) {
        getTransitionController().moveToCollecting(transition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resizeTask$2(Task task, Transition transition, Rect rect, int i, boolean z) {
        if (!task.getWindowConfiguration().canResizeTask()) {
            Slog.w("ActivityTaskManager", "resizeTask not allowed on task=" + task);
            transition.abort();
            return;
        }
        getTransitionController().requestStartTransition(transition, task, null, null);
        getTransitionController().collect(task);
        task.resize(rect, i, z);
        transition.setReady(task, true);
    }

    public void releaseSomeActivities(IApplicationThread iApplicationThread) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                getProcessController(iApplicationThread).releaseSomeActivities("low-mem");
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setLockScreenShown(final boolean z, final boolean z2) {
        if (checkCallingPermission("android.permission.DEVICE_POWER") != 0) {
            throw new SecurityException("Requires permission android.permission.DEVICE_POWER");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (this.mKeyguardShown != z) {
                    this.mKeyguardShown = z;
                    this.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda14
                        @Override // java.util.function.BiConsumer
                        public final void accept(Object obj, Object obj2) {
                            ((ActivityManagerInternal) obj).reportCurKeyguardUsageEvent(((Boolean) obj2).booleanValue());
                        }
                    }, this.mAmInternal, Boolean.valueOf(z)));
                }
                if ((this.mDemoteTopAppReasons & 1) != 0) {
                    this.mDemoteTopAppReasons &= -2;
                    if (this.mTopApp != null) {
                        this.mTopApp.scheduleUpdateOomAdj();
                    }
                }
                Trace.traceBegin(32L, "setLockScreenShown");
                this.mRootWindowContainer.forAllDisplays(new Consumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda15
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityTaskManagerService.this.lambda$setLockScreenShown$3(z, z2, (DisplayContent) obj);
                    }
                });
                Trace.traceEnd(32L);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$setLockScreenShown$4(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLockScreenShown$3(boolean z, boolean z2, DisplayContent displayContent) {
        this.mKeyguardController.setKeyguardShown(displayContent.getDisplayId(), z, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLockScreenShown$4(boolean z) {
        for (int size = this.mScreenObservers.size() - 1; size >= 0; size--) {
            this.mScreenObservers.get(size).onKeyguardStateChanged(z);
        }
    }

    public void onScreenAwakeChanged(final boolean z) {
        WindowProcessController process;
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$onScreenAwakeChanged$5(z);
            }
        });
        if (z) {
            return;
        }
        synchronized (this.mGlobalLockWithoutBoost) {
            this.mDemoteTopAppReasons &= -2;
            WindowState notificationShade = this.mRootWindowContainer.getDefaultDisplay().getDisplayPolicy().getNotificationShade();
            process = notificationShade != null ? notificationShade.getProcess() : null;
        }
        setProcessAnimatingWhileDozing(process);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onScreenAwakeChanged$5(boolean z) {
        for (int size = this.mScreenObservers.size() - 1; size >= 0; size--) {
            this.mScreenObservers.get(size).onAwakeStateChanged(z);
        }
    }

    public void setProcessAnimatingWhileDozing(WindowProcessController windowProcessController) {
        if (windowProcessController == null) {
            return;
        }
        windowProcessController.setRunningAnimationUnsafe();
        this.f1161mH.removeMessages(2, windowProcessController);
        HandlerC1840H handlerC1840H = this.f1161mH;
        handlerC1840H.sendMessageDelayed(handlerC1840H.obtainMessage(2, windowProcessController), 2000L);
    }

    public Bitmap getTaskDescriptionIcon(String str, int i) {
        int handleIncomingUser = handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, "getTaskDescriptionIcon");
        if (!new File(TaskPersister.getUserImagesDir(handleIncomingUser), new File(str).getName()).getPath().equals(str) || !str.contains("_activity_icon_")) {
            throw new IllegalArgumentException("Bad file path: " + str + " passed for userId " + handleIncomingUser);
        }
        return this.mRecentTasks.getTaskDescriptionIcon(str);
    }

    public void moveRootTaskToDisplay(int i, int i2) {
        this.mAmInternal.enforceCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "moveRootTaskToDisplay()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1419762046, 5, (String) null, new Object[]{Long.valueOf(i), Long.valueOf(i2)});
                }
                this.mRootWindowContainer.moveRootTaskToDisplay(i, i2, true);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void registerTaskStackListener(ITaskStackListener iTaskStackListener) {
        enforceTaskPermission("registerTaskStackListener()");
        this.mTaskChangeNotificationController.registerTaskStackListener(iTaskStackListener);
    }

    public void unregisterTaskStackListener(ITaskStackListener iTaskStackListener) {
        enforceTaskPermission("unregisterTaskStackListener()");
        this.mTaskChangeNotificationController.unregisterTaskStackListener(iTaskStackListener);
    }

    public boolean requestAssistContextExtras(int i, IAssistDataReceiver iAssistDataReceiver, Bundle bundle, IBinder iBinder, boolean z, boolean z2) {
        return enqueueAssistContext(i, null, null, iAssistDataReceiver, bundle, iBinder, z, z2, UserHandle.getCallingUserId(), null, 2000L, 0) != null;
    }

    public boolean requestAssistDataForTask(IAssistDataReceiver iAssistDataReceiver, int i, String str, String str2) {
        this.mAmInternal.enforceCallingPermission("android.permission.GET_TOP_ACTIVITY_INFO", "requestAssistDataForTask()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ActivityTaskManagerInternal.ActivityTokens attachedNonFinishingActivityForTask = this.mInternal.getAttachedNonFinishingActivityForTask(i, null);
            if (attachedNonFinishingActivityForTask == null) {
                Log.e("ActivityTaskManager", "Could not find activity for task " + i);
                return false;
            }
            AssistDataRequester assistDataRequester = new AssistDataRequester(this.mContext, this.mWindowManager, getAppOpsManager(), new AssistDataReceiverProxy(iAssistDataReceiver, str), new Object(), 49, -1);
            ArrayList arrayList = new ArrayList();
            arrayList.add(attachedNonFinishingActivityForTask.getActivityToken());
            assistDataRequester.requestAssistData(arrayList, true, false, false, true, false, true, Binder.getCallingUid(), str, str2);
            return true;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean requestAutofillData(IAssistDataReceiver iAssistDataReceiver, Bundle bundle, IBinder iBinder, int i) {
        return enqueueAssistContext(2, null, null, iAssistDataReceiver, bundle, iBinder, true, true, UserHandle.getCallingUserId(), null, 2000L, i) != null;
    }

    public Bundle getAssistContextExtras(int i) {
        PendingAssistExtras enqueueAssistContext = enqueueAssistContext(i, null, null, null, null, null, true, true, UserHandle.getCallingUserId(), null, 500L, 0);
        if (enqueueAssistContext == null) {
            return null;
        }
        synchronized (enqueueAssistContext) {
            while (!enqueueAssistContext.haveResult) {
                try {
                    enqueueAssistContext.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                buildAssistBundleLocked(enqueueAssistContext, enqueueAssistContext.result);
                this.mPendingAssistExtras.remove(enqueueAssistContext);
                this.mUiHandler.removeCallbacks(enqueueAssistContext);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return enqueueAssistContext.extras;
    }

    public static int checkCallingPermission(String str) {
        return checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid());
    }

    public boolean checkCanCloseSystemDialogs(int i, int i2, String str) {
        WindowProcessController process;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                process = this.mProcessMap.getProcess(i);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (str == null && process != null) {
            str = process.mInfo.packageName;
        }
        String str2 = "(pid=" + i + ", uid=" + i2 + ")";
        if (str != null) {
            str2 = str + " " + str2;
        }
        if (canCloseSystemDialogs(i, i2)) {
            return true;
        }
        if (CompatChanges.isChangeEnabled(174664365L, i2)) {
            throw new SecurityException("Permission Denial: android.intent.action.CLOSE_SYSTEM_DIALOGS broadcast from " + str2 + " requires android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS.");
        } else if (CompatChanges.isChangeEnabled(174664120L, i2)) {
            Slog.e("ActivityTaskManager", "Permission Denial: android.intent.action.CLOSE_SYSTEM_DIALOGS broadcast from " + str2 + " requires android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS, dropping broadcast.");
            return false;
        } else {
            Slog.w("ActivityTaskManager", "android.intent.action.CLOSE_SYSTEM_DIALOGS broadcast from " + str2 + " will require android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS in future builds.");
            return true;
        }
    }

    public final boolean canCloseSystemDialogs(int i, int i2) {
        if (checkPermission("android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS", i, i2) == 0) {
            return true;
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ArraySet<WindowProcessController> processes = this.mProcessMap.getProcesses(i2);
                if (processes != null) {
                    int size = processes.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        WindowProcessController valueAt = processes.valueAt(i3);
                        int instrumentationSourceUid = valueAt.getInstrumentationSourceUid();
                        if (valueAt.isInstrumenting() && instrumentationSourceUid != -1 && checkPermission("android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS", -1, instrumentationSourceUid) == 0) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return true;
                        } else if (valueAt.canCloseSystemDialogsByToken()) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return true;
                        }
                    }
                }
                if (!CompatChanges.isChangeEnabled(174664365L, i2)) {
                    if (this.mRootWindowContainer.hasVisibleWindowAboveButDoesNotOwnNotificationShade(i2)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    } else if (ArrayUtils.contains(this.mAccessibilityServiceUids, i2)) {
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

    public static void enforceTaskPermission(String str) {
        if (checkCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS") == 0) {
            return;
        }
        if (checkCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS") == 0) {
            Slog.w("ActivityTaskManager", "MANAGE_ACTIVITY_STACKS is deprecated, please use alternative permission: MANAGE_ACTIVITY_TASKS");
            return;
        }
        String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.MANAGE_ACTIVITY_TASKS";
        Slog.w("ActivityTaskManager", str2);
        throw new SecurityException(str2);
    }

    public static int checkPermission(String str, int i, int i2) {
        if (str == null) {
            return -1;
        }
        return checkComponentPermission(str, i, i2, -1, true);
    }

    public static int checkComponentPermission(String str, int i, int i2, int i3, boolean z) {
        return ActivityManagerService.checkComponentPermission(str, i, i2, i3, z);
    }

    public boolean isCallerRecents(int i) {
        return this.mRecentTasks.isCallerRecents(i);
    }

    public boolean isGetTasksAllowed(String str, int i, int i2) {
        boolean z = true;
        if (isCallerRecents(i2)) {
            return true;
        }
        boolean z2 = checkPermission("android.permission.REAL_GET_TASKS", i, i2) == 0;
        if (!z2) {
            if (checkPermission("android.permission.GET_TASKS", i, i2) == 0) {
                if (AppGlobals.getPackageManager().isUidPrivileged(i2)) {
                    try {
                        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                            ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_TASKS, -917215012, 4, (String) null, new Object[]{String.valueOf(str), Long.valueOf(i2)});
                        }
                    } catch (RemoteException unused) {
                    }
                    z2 = z;
                }
                z = z2;
                z2 = z;
            }
            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_TASKS, -401029526, 4, (String) null, new Object[]{String.valueOf(str), Long.valueOf(i2)});
            }
        }
        return z2;
    }

    public boolean isCrossUserAllowed(int i, int i2) {
        return checkPermission("android.permission.INTERACT_ACROSS_USERS", i, i2) == 0 || checkPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i, i2) == 0;
    }

    public final PendingAssistExtras enqueueAssistContext(int i, Intent intent, String str, IAssistDataReceiver iAssistDataReceiver, Bundle bundle, IBinder iBinder, boolean z, boolean z2, int i2, Bundle bundle2, long j, int i3) {
        ActivityRecord forTokenLocked;
        this.mAmInternal.enforceCallingPermission("android.permission.GET_TOP_ACTIVITY_INFO", "enqueueAssistContext()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                ActivityRecord topNonFinishingActivity = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.getTopNonFinishingActivity() : null;
                if (topNonFinishingActivity == null) {
                    Slog.w("ActivityTaskManager", "getAssistContextExtras failed: no top activity");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                } else if (!topNonFinishingActivity.attachedToProcess()) {
                    Slog.w("ActivityTaskManager", "getAssistContextExtras failed: no process for " + topNonFinishingActivity);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                } else {
                    if (!z) {
                        topNonFinishingActivity = ActivityRecord.forTokenLocked(iBinder);
                        if (topNonFinishingActivity == null) {
                            Slog.w("ActivityTaskManager", "enqueueAssistContext failed: activity for token=" + iBinder + " couldn't be found");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        } else if (!topNonFinishingActivity.attachedToProcess()) {
                            Slog.w("ActivityTaskManager", "enqueueAssistContext failed: no process for " + topNonFinishingActivity);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        }
                    } else if (iBinder != null && topNonFinishingActivity != (forTokenLocked = ActivityRecord.forTokenLocked(iBinder))) {
                        Slog.w("ActivityTaskManager", "enqueueAssistContext failed: caller " + forTokenLocked + " is not current top " + topNonFinishingActivity);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    ActivityRecord activityRecord = topNonFinishingActivity;
                    Bundle bundle3 = new Bundle();
                    if (bundle2 != null) {
                        bundle3.putAll(bundle2);
                    }
                    bundle3.putString("android.intent.extra.ASSIST_PACKAGE", activityRecord.packageName);
                    bundle3.putInt("android.intent.extra.ASSIST_UID", activityRecord.app.mUid);
                    PendingAssistExtras pendingAssistExtras = new PendingAssistExtras(activityRecord, bundle3, intent, str, iAssistDataReceiver, bundle, i2);
                    pendingAssistExtras.isHome = activityRecord.isActivityTypeHome();
                    if (z2) {
                        this.mViSessionId++;
                    }
                    try {
                        activityRecord.app.getThread().requestAssistContextExtras(activityRecord.token, pendingAssistExtras, i, this.mViSessionId, i3);
                        this.mPendingAssistExtras.add(pendingAssistExtras);
                        this.mUiHandler.postDelayed(pendingAssistExtras, j);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return pendingAssistExtras;
                    } catch (RemoteException unused) {
                        Slog.w("ActivityTaskManager", "getAssistContextExtras failed: crash calling " + activityRecord);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void buildAssistBundleLocked(PendingAssistExtras pendingAssistExtras, Bundle bundle) {
        if (bundle != null) {
            pendingAssistExtras.extras.putBundle("android.intent.extra.ASSIST_CONTEXT", bundle);
        }
        String str = pendingAssistExtras.hint;
        if (str != null) {
            pendingAssistExtras.extras.putBoolean(str, true);
        }
    }

    public final void pendingAssistExtrasTimedOut(PendingAssistExtras pendingAssistExtras) {
        IAssistDataReceiver iAssistDataReceiver;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mPendingAssistExtras.remove(pendingAssistExtras);
                iAssistDataReceiver = pendingAssistExtras.receiver;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (iAssistDataReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putBundle("receiverExtras", pendingAssistExtras.receiverExtras);
            try {
                pendingAssistExtras.receiver.onHandleAssistData(bundle);
            } catch (RemoteException unused) {
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$PendingAssistExtras */
    /* loaded from: classes2.dex */
    public class PendingAssistExtras extends Binder implements Runnable {
        public final ActivityRecord activity;
        public final Bundle extras;
        public final String hint;
        public final Intent intent;
        public boolean isHome;
        public final IAssistDataReceiver receiver;
        public Bundle receiverExtras;
        public final int userHandle;
        public boolean haveResult = false;
        public Bundle result = null;
        public AssistStructure structure = null;
        public AssistContent content = null;

        public PendingAssistExtras(ActivityRecord activityRecord, Bundle bundle, Intent intent, String str, IAssistDataReceiver iAssistDataReceiver, Bundle bundle2, int i) {
            this.activity = activityRecord;
            this.extras = bundle;
            this.intent = intent;
            this.hint = str;
            this.receiver = iAssistDataReceiver;
            this.receiverExtras = bundle2;
            this.userHandle = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            Slog.w("ActivityTaskManager", "getAssistContextExtras failed: timeout retrieving from " + this.activity);
            synchronized (this) {
                this.haveResult = true;
                notifyAll();
            }
            ActivityTaskManagerService.this.pendingAssistExtrasTimedOut(this);
        }
    }

    public boolean isAssistDataAllowedOnCurrentActivity() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task topDisplayFocusedRootTask = getTopDisplayFocusedRootTask();
                if (topDisplayFocusedRootTask != null && !topDisplayFocusedRootTask.isActivityTypeAssistant()) {
                    ActivityRecord topNonFinishingActivity = topDisplayFocusedRootTask.getTopNonFinishingActivity();
                    if (topNonFinishingActivity == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                    int i = topNonFinishingActivity.mUserId;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return DevicePolicyCache.getInstance().isScreenCaptureAllowed(i);
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void onLocalVoiceInteractionStartedLocked(IBinder iBinder, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor) {
        ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
        if (forTokenLocked == null) {
            return;
        }
        forTokenLocked.setVoiceSessionLocked(iVoiceInteractionSession);
        try {
            forTokenLocked.app.getThread().scheduleLocalVoiceInteractionStarted(iBinder, iVoiceInteractor);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            startRunningVoiceLocked(iVoiceInteractionSession, forTokenLocked.info.applicationInfo.uid);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        } catch (RemoteException unused) {
            forTokenLocked.clearVoiceSessionLocked();
        }
    }

    public final void startRunningVoiceLocked(IVoiceInteractionSession iVoiceInteractionSession, int i) {
        Slog.d("ActivityTaskManager", "<<<  startRunningVoiceLocked()");
        this.mVoiceWakeLock.setWorkSource(new WorkSource(i));
        IVoiceInteractionSession iVoiceInteractionSession2 = this.mRunningVoice;
        if (iVoiceInteractionSession2 == null || iVoiceInteractionSession2.asBinder() != iVoiceInteractionSession.asBinder()) {
            boolean z = this.mRunningVoice != null;
            this.mRunningVoice = iVoiceInteractionSession;
            if (z) {
                return;
            }
            this.mVoiceWakeLock.acquire();
            updateSleepIfNeededLocked();
        }
    }

    public void finishRunningVoiceLocked() {
        if (this.mRunningVoice != null) {
            this.mRunningVoice = null;
            this.mVoiceWakeLock.release();
            updateSleepIfNeededLocked();
        }
    }

    public void setVoiceKeepAwake(IVoiceInteractionSession iVoiceInteractionSession, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                IVoiceInteractionSession iVoiceInteractionSession2 = this.mRunningVoice;
                if (iVoiceInteractionSession2 != null && iVoiceInteractionSession2.asBinder() == iVoiceInteractionSession.asBinder()) {
                    if (z) {
                        this.mVoiceWakeLock.acquire();
                    } else {
                        this.mVoiceWakeLock.release();
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void keyguardGoingAway(final int i) {
        enforceNotIsolatedCaller("keyguardGoingAway");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if ((i & 16) != 0) {
                    this.mActivityClientController.invalidateHomeTaskSnapshot(null);
                } else if (this.mKeyguardShown) {
                    this.mDemoteTopAppReasons |= 1;
                }
                this.mRootWindowContainer.forAllDisplays(new Consumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActivityTaskManagerService.this.lambda$keyguardGoingAway$6(i, (DisplayContent) obj);
                    }
                });
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$keyguardGoingAway$6(int i, DisplayContent displayContent) {
        this.mKeyguardController.keyguardGoingAway(displayContent.getDisplayId(), i);
    }

    public void suppressResizeConfigChanges(boolean z) throws RemoteException {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS", "suppressResizeConfigChanges()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mSuppressResizeConfigChanges = z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onSplashScreenViewCopyFinished(int i, SplashScreenView.SplashScreenViewParcelable splashScreenViewParcelable) throws RemoteException {
        ActivityRecord topWaitSplashScreenActivity;
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS", "copySplashScreenViewFinish()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 0);
                if (anyTaskForId != null && (topWaitSplashScreenActivity = anyTaskForId.getTopWaitSplashScreenActivity()) != null) {
                    topWaitSplashScreenActivity.onCopySplashScreenFinish(splashScreenViewParcelable);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean enterPictureInPictureMode(final ActivityRecord activityRecord, final PictureInPictureParams pictureInPictureParams, boolean z) {
        if (activityRecord.inPinnedWindowingMode()) {
            return true;
        }
        if (activityRecord.checkEnterPictureInPictureState("enterPictureInPictureMode", false)) {
            final Transition transition = (getTransitionController().isShellTransitionsEnabled() && (z && (!activityRecord.isState(ActivityRecord.State.PAUSING) || pictureInPictureParams.isAutoEnterEnabled()))) ? new Transition(10, 0, getTransitionController(), this.mWindowManager.mSyncEngine) : null;
            Runnable runnable = new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityTaskManagerService.this.lambda$enterPictureInPictureMode$7(activityRecord, pictureInPictureParams, transition);
                }
            };
            if (activityRecord.isKeyguardLocked()) {
                this.mActivityClientController.dismissKeyguard(activityRecord.token, new C18392(transition, runnable), null);
            } else if (transition != null && this.mWindowManager.mSyncEngine.hasActiveSync()) {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 898260097, 0, (String) null, new Object[]{String.valueOf(transition)});
                }
                this.mWindowManager.mSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityTaskManagerService.this.lambda$enterPictureInPictureMode$8(transition);
                    }
                }, runnable);
            } else {
                if (transition != null) {
                    getTransitionController().moveToCollecting(transition);
                }
                runnable.run();
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enterPictureInPictureMode$7(ActivityRecord activityRecord, PictureInPictureParams pictureInPictureParams, Transition transition) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (activityRecord.getParent() == null) {
                    Slog.e("ActivityTaskManager", "Skip enterPictureInPictureMode, destroyed " + activityRecord);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                activityRecord.setPictureInPictureParams(pictureInPictureParams);
                this.mRootWindowContainer.moveActivityToPinnedRootTask(activityRecord, null, "enterPictureInPictureMode", transition);
                if (activityRecord.isState(ActivityRecord.State.PAUSING) && activityRecord.mPauseSchedulePendingForPip) {
                    activityRecord.getTask().schedulePauseActivity(activityRecord, false, false, true, "auto-pip");
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$2 */
    /* loaded from: classes2.dex */
    public class C18392 extends KeyguardDismissCallback {
        public final /* synthetic */ Runnable val$enterPipRunnable;
        public final /* synthetic */ Transition val$transition;

        public C18392(Transition transition, Runnable runnable) {
            this.val$transition = transition;
            this.val$enterPipRunnable = runnable;
        }

        public void onDismissSucceeded() {
            if (this.val$transition != null && ActivityTaskManagerService.this.mWindowManager.mSyncEngine.hasActiveSync()) {
                if (ProtoLogCache.WM_DEBUG_WINDOW_TRANSITIONS_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS, 898260097, 0, (String) null, new Object[]{String.valueOf(this.val$transition)});
                }
                BLASTSyncEngine bLASTSyncEngine = ActivityTaskManagerService.this.mWindowManager.mSyncEngine;
                final Transition transition = this.val$transition;
                bLASTSyncEngine.queueSyncSet(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityTaskManagerService.C18392.this.lambda$onDismissSucceeded$0(transition);
                    }
                }, this.val$enterPipRunnable);
                return;
            }
            if (this.val$transition != null) {
                ActivityTaskManagerService.this.getTransitionController().moveToCollecting(this.val$transition);
            }
            ActivityTaskManagerService.this.f1161mH.post(this.val$enterPipRunnable);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDismissSucceeded$0(Transition transition) {
            ActivityTaskManagerService.this.getTransitionController().moveToCollecting(transition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enterPictureInPictureMode$8(Transition transition) {
        getTransitionController().moveToCollecting(transition);
    }

    public IWindowOrganizerController getWindowOrganizerController() {
        return this.mWindowOrganizerController;
    }

    public void enforceSystemHasVrFeature() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {
            throw new UnsupportedOperationException("VR mode not supported on this device!");
        }
    }

    public boolean supportsLocalVoiceInteraction() {
        return ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).supportsLocalVoiceInteraction();
    }

    public boolean updateConfiguration(Configuration configuration) {
        this.mAmInternal.enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "updateConfiguration()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowManagerService windowManagerService = this.mWindowManager;
                if (windowManagerService == null) {
                    Slog.w("ActivityTaskManager", "Skip updateConfiguration because mWindowManager isn't set");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                if (configuration == null) {
                    configuration = windowManagerService.computeNewConfiguration(0);
                }
                Configuration configuration2 = configuration;
                this.f1161mH.sendMessage(PooledLambda.obtainMessage(new ActivityTaskManagerService$$ExternalSyntheticLambda12(), this.mAmInternal, 0));
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (configuration2 != null) {
                    Settings.System.clearConfiguration(configuration2);
                }
                updateConfigurationLocked(configuration2, null, false, false, -10000, false, this.mTmpUpdateConfigurationResult);
                boolean z = this.mTmpUpdateConfigurationResult.changes != 0;
                Binder.restoreCallingIdentity(clearCallingIdentity);
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void cancelTaskWindowTransition(int i) {
        enforceTaskPermission("cancelTaskWindowTransition()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 0);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "cancelTaskWindowTransition: taskId=" + i + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                anyTaskForId.cancelTaskWindowTransition();
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Finally extract failed */
    public TaskSnapshot getTaskSnapshot(int i, boolean z, boolean z2) {
        this.mAmInternal.enforceCallingPermission("android.permission.READ_FRAME_BUFFER", "getTaskSnapshot()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId == null) {
                    Slog.w("ActivityTaskManager", "getTaskSnapshot: taskId=" + i + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return null;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                TaskSnapshot snapshot = this.mWindowManager.mTaskSnapshotController.getSnapshot(i, anyTaskForId.mUserId, true, z);
                if (snapshot == null && z2) {
                    snapshot = takeTaskSnapshot(i);
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return snapshot;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public TaskSnapshot takeTaskSnapshot(int i) {
        this.mAmInternal.enforceCallingPermission("android.permission.READ_FRAME_BUFFER", "takeTaskSnapshot()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
                if (anyTaskForId != null && anyTaskForId.isVisible()) {
                    TaskSnapshot captureSnapshot = this.mWindowManager.mTaskSnapshotController.captureSnapshot(anyTaskForId, true);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return captureSnapshot;
                }
                Slog.w("ActivityTaskManager", "takeTaskSnapshot: taskId=" + i + " not found or not visible");
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return null;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getLastResumedActivityUserId() {
        this.mAmInternal.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "getLastResumedActivityUserId()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord activityRecord = this.mLastResumedActivity;
                if (activityRecord == null) {
                    int currentUserId = getCurrentUserId();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return currentUserId;
                }
                int i = activityRecord.mUserId;
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void updateLockTaskFeatures(int i, int i2) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != 1000) {
            this.mAmInternal.enforceCallingPermission("android.permission.UPDATE_LOCK_TASK_PACKAGES", "updateLockTaskFeatures()");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_LOCKTASK_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_LOCKTASK, -168799453, 1, (String) null, new Object[]{Long.valueOf(i), String.valueOf(Integer.toHexString(i2))});
                }
                getLockTaskController().updateLockTaskFeatures(i, i2);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void registerRemoteAnimationForNextActivityStart(String str, RemoteAnimationAdapter remoteAnimationAdapter, IBinder iBinder) {
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimationForNextActivityStart");
        remoteAnimationAdapter.setCallingPidUid(Binder.getCallingPid(), Binder.getCallingUid());
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                getActivityStartController().registerRemoteAnimationForNextActivityStart(str, remoteAnimationAdapter, iBinder);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void registerRemoteAnimationsForDisplay(int i, RemoteAnimationDefinition remoteAnimationDefinition) {
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimations");
        remoteAnimationDefinition.setCallingPidUid(Binder.getCallingPid(), Binder.getCallingUid());
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRootWindowContainer.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.e("ActivityTaskManager", "Couldn't find display with id: " + i);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                displayContent.registerRemoteAnimations(remoteAnimationDefinition);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void alwaysShowUnsupportedCompileSdkWarning(ComponentName componentName) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mAppWarnings.alwaysShowUnsupportedCompileSdkWarning(componentName);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setVrThread(int i) {
        enforceSystemHasVrFeature();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int callingPid = Binder.getCallingPid();
                this.mVrController.setVrThreadLocked(i, callingPid, this.mProcessMap.getProcess(callingPid));
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setPersistentVrThread(int i) {
        if (checkCallingPermission("android.permission.RESTRICTED_VR_ACCESS") != 0) {
            String str = "Permission Denial: setPersistentVrThread() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires android.permission.RESTRICTED_VR_ACCESS";
            Slog.w("ActivityTaskManager", str);
            throw new SecurityException(str);
        }
        enforceSystemHasVrFeature();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int callingPid = Binder.getCallingPid();
                this.mVrController.setPersistentVrThreadLocked(i, callingPid, this.mProcessMap.getProcess(callingPid));
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void stopAppSwitches() {
        this.mAmInternal.enforceCallingPermission("android.permission.STOP_APP_SWITCHES", "stopAppSwitches");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAppSwitchesState = 0;
                this.mLastStopAppSwitchesTime = SystemClock.uptimeMillis();
                this.f1161mH.removeMessages(4);
                this.f1161mH.sendEmptyMessageDelayed(4, 500L);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void resumeAppSwitches() {
        this.mAmInternal.enforceCallingPermission("android.permission.STOP_APP_SWITCHES", "resumeAppSwitches");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAppSwitchesState = 2;
                this.f1161mH.removeMessages(4);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public long getLastStopAppSwitchesTime() {
        return this.mLastStopAppSwitchesTime;
    }

    public boolean shouldDisableNonVrUiLocked() {
        return this.mVrController.shouldDisableNonVrUiLocked();
    }

    public void applyUpdateVrModeLocked(final ActivityRecord activityRecord) {
        if (activityRecord.requestedVrComponent != null && activityRecord.getDisplayId() != 0) {
            Slog.i("ActivityTaskManager", "Moving " + activityRecord.shortComponentName + " from display " + activityRecord.getDisplayId() + " to main display for VR");
            this.mRootWindowContainer.moveRootTaskToDisplay(activityRecord.getRootTaskId(), 0, true);
        }
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$applyUpdateVrModeLocked$9(activityRecord);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyUpdateVrModeLocked$9(ActivityRecord activityRecord) {
        if (this.mVrController.onVrModeChanged(activityRecord)) {
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    boolean shouldDisableNonVrUiLocked = this.mVrController.shouldDisableNonVrUiLocked();
                    this.mWindowManager.disableNonVrUi(shouldDisableNonVrUiLocked);
                    if (shouldDisableNonVrUiLocked) {
                        this.mRootWindowContainer.removeRootTasksInWindowingModes(2);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    public int getPackageScreenCompatMode(String str) {
        int packageScreenCompatModeLocked;
        enforceNotIsolatedCaller("getPackageScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                packageScreenCompatModeLocked = this.mCompatModePackages.getPackageScreenCompatModeLocked(str);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return packageScreenCompatModeLocked;
    }

    public void setPackageScreenCompatMode(String str, int i) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setPackageScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mCompatModePackages.setPackageScreenCompatModeLocked(str, i);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean getPackageAskScreenCompat(String str) {
        boolean packageAskCompatModeLocked;
        enforceNotIsolatedCaller("getPackageAskScreenCompat");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                packageAskCompatModeLocked = this.mCompatModePackages.getPackageAskCompatModeLocked(str);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return packageAskCompatModeLocked;
    }

    public void setPackageAskScreenCompat(String str, boolean z) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setPackageAskScreenCompat");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mCompatModePackages.setPackageAskCompatModeLocked(str, z);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public Task getTopDisplayFocusedRootTask() {
        return this.mRootWindowContainer.getTopDisplayFocusedRootTask();
    }

    public void notifyTaskPersisterLocked(Task task, boolean z) {
        this.mRecentTasks.notifyTaskPersisterLocked(task, z);
    }

    public void clearLaunchParamsForPackages(List<String> list) {
        enforceTaskPermission("clearLaunchParamsForPackages");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = 0; i < list.size(); i++) {
                    this.mTaskSupervisor.mLaunchParamsPersister.removeRecordForPackage(list.get(i));
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onPictureInPictureStateChanged(PictureInPictureUiState pictureInPictureUiState) {
        enforceTaskPermission("onPictureInPictureStateChanged");
        Task rootPinnedTask = this.mRootWindowContainer.getDefaultTaskDisplayArea().getRootPinnedTask();
        if (rootPinnedTask == null || rootPinnedTask.getTopMostActivity() == null) {
            return;
        }
        this.mWindowManager.mAtmService.mActivityClientController.onPictureInPictureStateChanged(rootPinnedTask.getTopMostActivity(), pictureInPictureUiState);
    }

    public void detachNavigationBarFromApp(IBinder iBinder) {
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "detachNavigationBarFromApp");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                getTransitionController().legacyDetachNavigationBarFromApp(iBinder);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dumpLastANRLocked(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER LAST ANR (dumpsys activity lastanr)");
        String str = this.mLastANRState;
        if (str == null) {
            printWriter.println("  <no ANR has occurred since boot>");
        } else {
            printWriter.println(str);
        }
    }

    public void dumpLastANRTracesLocked(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER LAST ANR TRACES (dumpsys activity lastanr-traces)");
        File[] listFiles = new File("/data/anr").listFiles();
        if (ArrayUtils.isEmpty(listFiles)) {
            printWriter.println("  <no ANR has occurred since boot>");
            return;
        }
        File file = null;
        for (File file2 : listFiles) {
            if (file == null || file.lastModified() < file2.lastModified()) {
                file = file2;
            }
        }
        printWriter.print("File: ");
        printWriter.print(file.getName());
        printWriter.println();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    printWriter.println(readLine);
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (IOException e) {
            printWriter.print("Unable to read: ");
            printWriter.print(e);
            printWriter.println();
        }
    }

    public void dumpTopResumedActivityLocked(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER TOP-RESUMED (dumpsys activity top-resumed)");
        ActivityRecord topResumedActivity = this.mRootWindowContainer.getTopResumedActivity();
        if (topResumedActivity != null) {
            topResumedActivity.dump(printWriter, "", true);
        }
    }

    public void dumpVisibleActivitiesLocked(PrintWriter printWriter, int i) {
        printWriter.println("ACTIVITY MANAGER VISIBLE ACTIVITIES (dumpsys activity visible)");
        boolean z = false;
        ArrayList<ActivityRecord> dumpActivities = this.mRootWindowContainer.getDumpActivities("all", true, false, -1);
        boolean z2 = false;
        for (int size = dumpActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = dumpActivities.get(size);
            if (activityRecord.isVisible() && (i == -1 || activityRecord.getDisplayId() == i)) {
                if (z2) {
                    printWriter.println();
                }
                activityRecord.dump(printWriter, "", true);
                z = true;
                z2 = true;
            }
        }
        if (z) {
            return;
        }
        printWriter.println("(nothing)");
    }

    public void dumpActivitiesLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, String str, int i2) {
        dumpActivitiesLocked(fileDescriptor, printWriter, strArr, i, z, z2, str, i2, "ACTIVITY MANAGER ACTIVITIES (dumpsys activity activities)");
    }

    public void dumpActivitiesLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, String str, int i2, String str2) {
        boolean z3;
        printWriter.println(str2);
        boolean dumpActivities = this.mRootWindowContainer.dumpActivities(fileDescriptor, printWriter, z, z2, str, i2);
        boolean z4 = true;
        if (ActivityTaskSupervisor.printThisActivity(printWriter, this.mRootWindowContainer.getTopResumedActivity(), str, i2, dumpActivities, "  ResumedActivity: ", null)) {
            dumpActivities = false;
            z3 = true;
        } else {
            z3 = dumpActivities;
        }
        if (str == null) {
            if (dumpActivities) {
                printWriter.println();
            }
            this.mTaskSupervisor.dump(printWriter, "  ");
            this.mTaskOrganizerController.dump(printWriter, "  ");
            this.mVisibleActivityProcessTracker.dump(printWriter, "  ");
            this.mActiveUids.dump(printWriter, "  ");
            if (this.mDemoteTopAppReasons != 0) {
                printWriter.println("  mDemoteTopAppReasons=" + this.mDemoteTopAppReasons);
            }
        } else {
            z4 = z3;
        }
        if (z4) {
            return;
        }
        printWriter.println("  (nothing)");
    }

    public void dumpActivityContainersLocked(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER CONTAINERS (dumpsys activity containers)");
        this.mRootWindowContainer.dumpChildrenNames(printWriter, " ");
        printWriter.println(" ");
    }

    public void dumpActivityStarterLocked(PrintWriter printWriter, String str) {
        printWriter.println("ACTIVITY MANAGER STARTER (dumpsys activity starter)");
        getActivityStartController().dump(printWriter, "", str);
    }

    public void dumpInstalledPackagesConfig(PrintWriter printWriter) {
        this.mPackageConfigPersister.dump(printWriter, getCurrentUserId());
    }

    public boolean dumpActivity(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr, int i, boolean z, boolean z2, boolean z3, boolean z4, int i2, int i3) {
        ArrayList<ActivityRecord> dumpActivities;
        Task task;
        boolean z5;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                dumpActivities = this.mRootWindowContainer.getDumpActivities(str, z2, z3, i3);
            } finally {
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        boolean z6 = false;
        if (dumpActivities.size() <= 0) {
            return false;
        }
        String[] strArr2 = new String[strArr.length - i];
        System.arraycopy(strArr, i, strArr2, 0, strArr.length - i);
        Task task2 = null;
        int size = dumpActivities.size() - 1;
        boolean z7 = false;
        while (size >= 0) {
            ActivityRecord activityRecord = dumpActivities.get(size);
            if (z7) {
                printWriter.println();
            }
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task task3 = activityRecord.getTask();
                    int displayId = task3.getDisplayId();
                    if (i2 == -1 || displayId == i2) {
                        if (task2 != task3) {
                            printWriter.print("TASK ");
                            printWriter.print(task3.affinity);
                            printWriter.print(" id=");
                            printWriter.print(task3.mTaskId);
                            printWriter.print(" userId=");
                            printWriter.print(task3.mUserId);
                            printDisplayInfoAndNewLine(printWriter, activityRecord);
                            if (z) {
                                task3.dump(printWriter, "  ");
                            }
                            task = task3;
                            z5 = true;
                        } else {
                            task = task2;
                            z5 = z6;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        dumpActivity("  ", fileDescriptor, printWriter, dumpActivities.get(size), strArr2, z, z4);
                        task2 = task;
                        z6 = z5;
                    } else {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                } finally {
                }
            }
            size--;
            z7 = true;
        }
        if (!z6) {
            printWriter.println("(nothing)");
        }
        return true;
    }

    public final void dumpActivity(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, ActivityRecord activityRecord, String[] strArr, boolean z, boolean z2) {
        IApplicationThread iApplicationThread;
        String str2 = str + "  ";
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                printWriter.print(str);
                printWriter.print("ACTIVITY ");
                printWriter.print(activityRecord.shortComponentName);
                printWriter.print(" ");
                printWriter.print(Integer.toHexString(System.identityHashCode(activityRecord)));
                printWriter.print(" pid=");
                if (activityRecord.hasProcess()) {
                    printWriter.print(activityRecord.app.getPid());
                    iApplicationThread = activityRecord.app.getThread();
                } else {
                    printWriter.print("(not running)");
                    iApplicationThread = null;
                }
                if (z2) {
                    printWriter.print(" userId=");
                    printWriter.print(activityRecord.mUserId);
                    printWriter.print(" uid=");
                    printWriter.print(activityRecord.getUid());
                    printDisplayInfoAndNewLine(printWriter, activityRecord);
                } else {
                    printWriter.println();
                }
                if (z) {
                    activityRecord.dump(printWriter, str2, true);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (iApplicationThread != null) {
            printWriter.flush();
            try {
                TransferPipe transferPipe = new TransferPipe();
                try {
                    iApplicationThread.dumpActivity(transferPipe.getWriteFd(), activityRecord.token, str2, strArr);
                    transferPipe.go(fileDescriptor);
                    transferPipe.close();
                } catch (Throwable th2) {
                    try {
                        transferPipe.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (RemoteException unused) {
                printWriter.println(str2 + "Got a RemoteException while dumping the activity");
            } catch (IOException e) {
                printWriter.println(str2 + "Failure while dumping the activity: " + e);
            }
        }
    }

    public final void printDisplayInfoAndNewLine(PrintWriter printWriter, ActivityRecord activityRecord) {
        printWriter.print(" displayId=");
        DisplayContent displayContent = activityRecord.getDisplayContent();
        if (displayContent == null) {
            printWriter.println("N/A");
            return;
        }
        Display display = displayContent.getDisplay();
        printWriter.print(display.getDisplayId());
        printWriter.print("(type=");
        printWriter.print(Display.typeToString(display.getType()));
        printWriter.println(")");
    }

    public final void writeSleepStateToProto(ProtoOutputStream protoOutputStream, int i, boolean z) {
        long start = protoOutputStream.start(1146756268059L);
        protoOutputStream.write(1159641169921L, PowerManagerInternal.wakefulnessToProtoEnum(i));
        int size = this.mRootWindowContainer.mSleepTokens.size();
        for (int i2 = 0; i2 < size; i2++) {
            protoOutputStream.write(2237677961218L, this.mRootWindowContainer.mSleepTokens.valueAt(i2).toString());
        }
        protoOutputStream.write(1133871366147L, this.mSleeping);
        protoOutputStream.write(1133871366148L, this.mShuttingDown);
        protoOutputStream.write(1133871366149L, z);
        protoOutputStream.end(start);
    }

    public int getCurrentUserId() {
        return this.mAmInternal.getCurrentUserId();
    }

    public static void enforceNotIsolatedCaller(String str) {
        if (UserHandle.isIsolated(Binder.getCallingUid())) {
            throw new SecurityException("Isolated process not allowed to call " + str);
        }
    }

    public Configuration getConfiguration() {
        Configuration configuration;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                configuration = new Configuration(getGlobalConfigurationForCallingPid());
                configuration.userSetLocale = false;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return configuration;
    }

    public Configuration getGlobalConfiguration() {
        RootWindowContainer rootWindowContainer = this.mRootWindowContainer;
        return rootWindowContainer != null ? rootWindowContainer.getConfiguration() : new Configuration();
    }

    public boolean updateConfigurationLocked(Configuration configuration, ActivityRecord activityRecord, boolean z) {
        return updateConfigurationLocked(configuration, activityRecord, z, false);
    }

    public boolean updateConfigurationLocked(Configuration configuration, ActivityRecord activityRecord, boolean z, boolean z2) {
        return updateConfigurationLocked(configuration, activityRecord, z, false, -10000, z2);
    }

    public void updatePersistentConfiguration(Configuration configuration, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                configuration.windowConfiguration.setToDefaults();
                updateConfigurationLocked(configuration, null, false, true, i, false);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean updateConfigurationLocked(Configuration configuration, ActivityRecord activityRecord, boolean z, boolean z2, int i, boolean z3) {
        return updateConfigurationLocked(configuration, activityRecord, z, z2, i, z3, null);
    }

    public boolean updateConfigurationLocked(Configuration configuration, ActivityRecord activityRecord, boolean z, boolean z2, int i, boolean z3, UpdateConfigurationResult updateConfigurationResult) {
        int updateGlobalConfigurationLocked;
        deferWindowLayout();
        if (configuration != null) {
            try {
                updateGlobalConfigurationLocked = updateGlobalConfigurationLocked(configuration, z, z2, i);
            } catch (Throwable th) {
                continueWindowLayout();
                throw th;
            }
        } else {
            updateGlobalConfigurationLocked = 0;
        }
        boolean ensureConfigAndVisibilityAfterUpdate = !z3 ? ensureConfigAndVisibilityAfterUpdate(activityRecord, updateGlobalConfigurationLocked) : true;
        continueWindowLayout();
        if (updateConfigurationResult != null) {
            updateConfigurationResult.changes = updateGlobalConfigurationLocked;
            updateConfigurationResult.activityRelaunched = !ensureConfigAndVisibilityAfterUpdate;
        }
        return ensureConfigAndVisibilityAfterUpdate;
    }

    public int updateGlobalConfigurationLocked(Configuration configuration, boolean z, boolean z2, int i) {
        int i2;
        this.mTempConfig.setTo(getGlobalConfiguration());
        int updateFrom = this.mTempConfig.updateFrom(configuration);
        if (updateFrom == 0) {
            return 0;
        }
        Trace.traceBegin(32L, "updateGlobalConfiguration");
        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -927199900, 0, (String) null, new Object[]{String.valueOf(configuration)});
        }
        EventLogTags.writeConfigurationChanged(updateFrom);
        FrameworkStatsLog.write(66, configuration.colorMode, configuration.densityDpi, configuration.fontScale, configuration.hardKeyboardHidden, configuration.keyboard, configuration.keyboardHidden, configuration.mcc, configuration.mnc, configuration.navigation, configuration.navigationHidden, configuration.orientation, configuration.screenHeightDp, configuration.screenLayout, configuration.screenWidthDp, configuration.smallestScreenWidthDp, configuration.touchscreen, configuration.uiMode);
        if (Process.myUid() == 1000) {
            SystemProperties.set("debug.tracing.mcc", Integer.toString(configuration.mcc));
            SystemProperties.set("debug.tracing.mnc", Integer.toString(configuration.mnc));
        }
        if (!z && !configuration.getLocales().isEmpty() && configuration.userSetLocale) {
            LocaleList locales = configuration.getLocales();
            if (locales.size() > 1) {
                if (this.mSupportedSystemLocales == null) {
                    this.mSupportedSystemLocales = Resources.getSystem().getAssets().getLocales();
                }
                i2 = Math.max(0, locales.getFirstMatchIndex(this.mSupportedSystemLocales));
            } else {
                i2 = 0;
            }
            SystemProperties.set("persist.sys.locale", locales.get(i2).toLanguageTag());
            LocaleList.setDefault(locales, i2);
        }
        this.mTempConfig.seq = increaseConfigurationSeqLocked();
        Slog.i("ActivityTaskManager", "Config changes=" + Integer.toHexString(updateFrom) + " " + this.mTempConfig);
        this.mUsageStatsInternal.reportConfigurationChange(this.mTempConfig, this.mAmInternal.getCurrentUserId());
        updateShouldShowDialogsLocked(this.mTempConfig);
        AttributeCache instance = AttributeCache.instance();
        if (instance != null) {
            instance.updateConfiguration(this.mTempConfig);
        }
        this.mSystemThread.applyConfigurationToResources(this.mTempConfig);
        if (z2 && Settings.System.hasInterestingConfigurationChanges(updateFrom)) {
            this.f1161mH.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda4
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((ActivityTaskManagerService) obj).sendPutConfigurationForUserMsg(((Integer) obj2).intValue(), (Configuration) obj3);
                }
            }, this, Integer.valueOf(i), new Configuration(this.mTempConfig)));
        }
        SparseArray<WindowProcessController> pidMap = this.mProcessMap.getPidMap();
        for (int size = pidMap.size() - 1; size >= 0; size--) {
            WindowProcessController windowProcessController = pidMap.get(pidMap.keyAt(size));
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -503656156, 0, (String) null, new Object[]{String.valueOf(windowProcessController.mName), String.valueOf(this.mTempConfig)});
            }
            windowProcessController.onConfigurationChanged(this.mTempConfig);
        }
        this.f1161mH.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda5
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((ActivityManagerInternal) obj).broadcastGlobalConfigurationChanged(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue());
            }
        }, this.mAmInternal, Integer.valueOf(updateFrom), Boolean.valueOf(z)));
        Trace.traceBegin(32L, "RootConfigChange");
        this.mRootWindowContainer.onConfigurationChanged(this.mTempConfig);
        Trace.traceEnd(32L);
        Trace.traceEnd(32L);
        return updateFrom;
    }

    public final int increaseAssetConfigurationSeq() {
        int i = this.mGlobalAssetsSeq + 1;
        this.mGlobalAssetsSeq = i;
        int max = Math.max(i, 1);
        this.mGlobalAssetsSeq = max;
        return max;
    }

    public void updateAssetConfiguration(List<WindowProcessController> list, boolean z) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int increaseAssetConfigurationSeq = increaseAssetConfigurationSeq();
                if (z) {
                    Configuration configuration = new Configuration();
                    configuration.assetsSeq = increaseAssetConfigurationSeq;
                    updateConfiguration(configuration);
                }
                for (int size = list.size() - 1; size >= 0; size--) {
                    list.get(size).updateAssetConfiguration(increaseAssetConfigurationSeq);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void startLaunchPowerMode(int i) {
        PowerManagerInternal powerManagerInternal = this.mPowerManagerInternal;
        if (powerManagerInternal != null) {
            powerManagerInternal.setPowerMode(5, true);
        }
        this.mLaunchPowerModeReasons |= i;
        if ((i & 4) != 0) {
            if (this.mRetainPowerModeAndTopProcessState) {
                this.f1161mH.removeMessages(3);
            }
            this.mRetainPowerModeAndTopProcessState = true;
            this.f1161mH.sendEmptyMessageDelayed(3, 1000L);
            Slog.d("ActivityTaskManager", "Temporarily retain top process state for launching app");
        }
    }

    public void endLaunchPowerMode(int i) {
        PowerManagerInternal powerManagerInternal;
        int i2 = this.mLaunchPowerModeReasons;
        if (i2 == 0) {
            return;
        }
        int i3 = (~i) & i2;
        this.mLaunchPowerModeReasons = i3;
        if ((i3 & 4) != 0) {
            boolean z = true;
            for (int childCount = this.mRootWindowContainer.getChildCount() - 1; childCount >= 0; childCount--) {
                z &= this.mRootWindowContainer.getChildAt(childCount).mUnknownAppVisibilityController.allResolved();
            }
            if (z) {
                this.mLaunchPowerModeReasons &= -5;
                this.mRetainPowerModeAndTopProcessState = false;
                this.f1161mH.removeMessages(3);
            }
        }
        if (this.mLaunchPowerModeReasons != 0 || (powerManagerInternal = this.mPowerManagerInternal) == null) {
            return;
        }
        powerManagerInternal.setPowerMode(5, false);
    }

    public void deferWindowLayout() {
        if (!this.mWindowManager.mWindowPlacerLocked.isLayoutDeferred()) {
            this.mLayoutReasons = 0;
        }
        this.mWindowManager.mWindowPlacerLocked.deferLayout();
    }

    public void continueWindowLayout() {
        this.mWindowManager.mWindowPlacerLocked.continueLayout(this.mLayoutReasons != 0);
    }

    public void addWindowLayoutReasons(int i) {
        this.mLayoutReasons = i | this.mLayoutReasons;
    }

    public final void updateEventDispatchingLocked(boolean z) {
        this.mWindowManager.setEventDispatching(z && !this.mShuttingDown);
    }

    public final void sendPutConfigurationForUserMsg(int i, Configuration configuration) {
        Settings.System.putConfigurationForUser(this.mContext.getContentResolver(), configuration, i);
    }

    public boolean isActivityStartsLoggingEnabled() {
        return this.mAmInternal.isActivityStartsLoggingEnabled();
    }

    public boolean isBackgroundActivityStartsEnabled() {
        return this.mAmInternal.isBackgroundActivityStartsEnabled();
    }

    public static long getInputDispatchingTimeoutMillisLocked(ActivityRecord activityRecord) {
        if (activityRecord == null || !activityRecord.hasProcess()) {
            return InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        }
        return getInputDispatchingTimeoutMillisLocked(activityRecord.app);
    }

    public static long getInputDispatchingTimeoutMillisLocked(WindowProcessController windowProcessController) {
        if (windowProcessController == null) {
            return InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        }
        return windowProcessController.getInputDispatchingTimeoutMillis();
    }

    public final void updateShouldShowDialogsLocked(Configuration configuration) {
        boolean z = false;
        boolean z2 = (configuration.keyboard == 1 && configuration.touchscreen == 1 && configuration.navigation == 1) ? false : true;
        boolean z3 = Settings.Global.getInt(this.mContext.getContentResolver(), "hide_error_dialogs", 0) != 0;
        if (z2 && ActivityTaskManager.currentUiModeSupportsErrorDialogs(configuration) && !z3) {
            z = true;
        }
        this.mShowDialogs = z;
    }

    public final void updateFontScaleIfNeeded(int i) {
        if (i != getCurrentUserId()) {
            return;
        }
        float floatForUser = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "font_scale", 1.0f, i);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (getGlobalConfiguration().fontScale == floatForUser) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Configuration computeNewConfiguration = this.mWindowManager.computeNewConfiguration(0);
                computeNewConfiguration.fontScale = floatForUser;
                updatePersistentConfiguration(computeNewConfiguration, i);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void updateFontWeightAdjustmentIfNeeded(int i) {
        if (i != getCurrentUserId()) {
            return;
        }
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "font_weight_adjustment", Integer.MAX_VALUE, i);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (getGlobalConfiguration().fontWeightAdjustment == intForUser) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Configuration computeNewConfiguration = this.mWindowManager.computeNewConfiguration(0);
                computeNewConfiguration.fontWeightAdjustment = intForUser;
                updatePersistentConfiguration(computeNewConfiguration, i);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean isSleepingOrShuttingDownLocked() {
        return isSleepingLocked() || this.mShuttingDown;
    }

    public boolean isSleepingLocked() {
        return this.mSleeping;
    }

    public void setLastResumedActivityUncheckLocked(ActivityRecord activityRecord, String str) {
        IVoiceInteractionSession iVoiceInteractionSession;
        int i;
        Task task = activityRecord.getTask();
        if (task.isActivityTypeStandard()) {
            AppTimeTracker appTimeTracker = this.mCurAppTimeTracker;
            if (appTimeTracker != activityRecord.appTimeTracker) {
                if (appTimeTracker != null) {
                    appTimeTracker.stop();
                    this.f1161mH.obtainMessage(1, this.mCurAppTimeTracker).sendToTarget();
                    this.mRootWindowContainer.clearOtherAppTimeTrackers(activityRecord.appTimeTracker);
                    this.mCurAppTimeTracker = null;
                }
                AppTimeTracker appTimeTracker2 = activityRecord.appTimeTracker;
                if (appTimeTracker2 != null) {
                    this.mCurAppTimeTracker = appTimeTracker2;
                    startTimeTrackingFocusedActivityLocked();
                }
            } else {
                startTimeTrackingFocusedActivityLocked();
            }
        } else {
            activityRecord.appTimeTracker = null;
        }
        if (task.voiceInteractor != null) {
            startRunningVoiceLocked(task.voiceSession, activityRecord.info.applicationInfo.uid);
        } else {
            finishRunningVoiceLocked();
            ActivityRecord activityRecord2 = this.mLastResumedActivity;
            if (activityRecord2 != null) {
                Task task2 = activityRecord2.getTask();
                if (task2 == null || (iVoiceInteractionSession = task2.voiceSession) == null) {
                    iVoiceInteractionSession = this.mLastResumedActivity.voiceSession;
                }
                if (iVoiceInteractionSession != null) {
                    finishVoiceTask(iVoiceInteractionSession);
                }
            }
        }
        ActivityRecord activityRecord3 = this.mLastResumedActivity;
        if (activityRecord3 != null && (i = activityRecord.mUserId) != activityRecord3.mUserId) {
            this.mAmInternal.sendForegroundProfileChanged(i);
        }
        ActivityRecord activityRecord4 = this.mLastResumedActivity;
        Task task3 = activityRecord4 != null ? activityRecord4.getTask() : null;
        updateResumedAppTrace(activityRecord);
        this.mLastResumedActivity = activityRecord;
        if (!getTransitionController().isTransientCollect(activityRecord)) {
            ActivityRecord activityRecord5 = activityRecord.mDisplayContent.mFocusedApp;
            if (activityRecord5 != null) {
                activityRecord5.getTask();
            }
            if (activityRecord.mDisplayContent.setFocusedApp(activityRecord)) {
                this.mWindowManager.updateFocusedWindowLocked(0, true);
            }
        }
        if (task != task3) {
            this.mTaskSupervisor.mRecentTasks.add(task);
        }
        applyUpdateLockStateLocked(activityRecord);
        applyUpdateVrModeLocked(activityRecord);
        EventLogTags.writeWmSetResumedActivity(activityRecord.mUserId, activityRecord.shortComponentName, str);
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$SleepTokenAcquirerImpl */
    /* loaded from: classes2.dex */
    public final class SleepTokenAcquirerImpl implements ActivityTaskManagerInternal.SleepTokenAcquirer {
        public final SparseArray<RootWindowContainer.SleepToken> mSleepTokens = new SparseArray<>();
        public final String mTag;

        public SleepTokenAcquirerImpl(String str) {
            this.mTag = str;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal.SleepTokenAcquirer
        public void acquire(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!this.mSleepTokens.contains(i)) {
                        this.mSleepTokens.append(i, ActivityTaskManagerService.this.mRootWindowContainer.createSleepToken(this.mTag, i));
                        ActivityTaskManagerService.this.updateSleepIfNeededLocked();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal.SleepTokenAcquirer
        public void release(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RootWindowContainer.SleepToken sleepToken = this.mSleepTokens.get(i);
                    if (sleepToken != null) {
                        ActivityTaskManagerService.this.mRootWindowContainer.removeSleepToken(sleepToken);
                        this.mSleepTokens.remove(i);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x002d, code lost:
        if (r2 != false) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updateSleepIfNeededLocked() {
        boolean z = true;
        boolean z2 = !this.mRootWindowContainer.hasAwakeDisplay();
        boolean z3 = this.mSleeping;
        if (!z2) {
            if (z3) {
                this.mSleeping = false;
                FrameworkStatsLog.write(14, 2);
                startTimeTrackingFocusedActivityLocked();
                this.mTopProcessState = 2;
                Slog.d("ActivityTaskManager", "Top Process State changed to PROCESS_STATE_TOP");
                this.mTaskSupervisor.comeOutOfSleepIfNeededLocked();
            }
            this.mRootWindowContainer.applySleepTokens(true);
        } else {
            if (!this.mSleeping && z2) {
                this.mSleeping = true;
                FrameworkStatsLog.write(14, 1);
                AppTimeTracker appTimeTracker = this.mCurAppTimeTracker;
                if (appTimeTracker != null) {
                    appTimeTracker.stop();
                }
                this.mTopProcessState = 12;
                Slog.d("ActivityTaskManager", "Top Process State changed to PROCESS_STATE_TOP_SLEEPING");
                this.mTaskSupervisor.goingToSleepLocked();
                updateResumedAppTrace(null);
            }
            z = false;
        }
        if (z) {
            updateOomAdj();
        }
    }

    public void updateOomAdj() {
        this.f1161mH.removeCallbacks(this.mUpdateOomAdjRunnable);
        this.f1161mH.post(this.mUpdateOomAdjRunnable);
    }

    public void updateCpuStats() {
        HandlerC1840H handlerC1840H = this.f1161mH;
        final ActivityManagerInternal activityManagerInternal = this.mAmInternal;
        Objects.requireNonNull(activityManagerInternal);
        handlerC1840H.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                activityManagerInternal.updateCpuStats();
            }
        });
    }

    public void updateBatteryStats(ActivityRecord activityRecord, boolean z) {
        this.f1161mH.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda2
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ((ActivityManagerInternal) obj).updateBatteryStats((ComponentName) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Boolean) obj5).booleanValue());
            }
        }, this.mAmInternal, activityRecord.mActivityComponent, Integer.valueOf(activityRecord.app.mUid), Integer.valueOf(activityRecord.mUserId), Boolean.valueOf(z)));
    }

    public void updateTopApp(ActivityRecord activityRecord) {
        if (activityRecord == null) {
            activityRecord = this.mRootWindowContainer.getTopResumedActivity();
        }
        this.mTopApp = activityRecord != null ? activityRecord.app : null;
        if (this.mTopApp == this.mPreviousProcess) {
            this.mPreviousProcess = null;
        }
    }

    public void updatePreviousProcess(ActivityRecord activityRecord) {
        if (activityRecord.app == null || this.mTopApp == null || activityRecord.app == this.mTopApp || activityRecord.lastVisibleTime <= this.mPreviousProcessVisibleTime || activityRecord.app == this.mHomeProcess) {
            return;
        }
        this.mPreviousProcess = activityRecord.app;
        this.mPreviousProcessVisibleTime = activityRecord.lastVisibleTime;
    }

    public void updateActivityUsageStats(ActivityRecord activityRecord, int i) {
        int i2;
        Task task = activityRecord.getTask();
        if (task != null) {
            ActivityRecord rootActivity = task.getRootActivity();
            r1 = rootActivity != null ? rootActivity.mActivityComponent : null;
            i2 = task.mTaskId;
        } else {
            i2 = -1;
        }
        this.f1161mH.sendMessage(PooledLambda.obtainMessage(new HeptConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda13
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                ((ActivityManagerInternal) obj).updateActivityUsageStats((ComponentName) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (IBinder) obj5, (ComponentName) obj6, (ActivityId) obj7);
            }
        }, this.mAmInternal, activityRecord.mActivityComponent, Integer.valueOf(activityRecord.mUserId), Integer.valueOf(i), activityRecord.token, r1, new ActivityId(i2, activityRecord.shareableActivityToken)));
    }

    public void startProcessAsync(ActivityRecord activityRecord, boolean z, boolean z2, String str) {
        try {
            if (Trace.isTagEnabled(32L)) {
                Trace.traceBegin(32L, "dispatchingStartProcess:" + activityRecord.processName);
            }
            this.f1161mH.sendMessage(PooledLambda.obtainMessage(new HeptConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda17
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                    ((ActivityManagerInternal) obj).startProcess((String) obj2, (ApplicationInfo) obj3, ((Boolean) obj4).booleanValue(), ((Boolean) obj5).booleanValue(), (String) obj6, (ComponentName) obj7);
                }
            }, this.mAmInternal, activityRecord.processName, activityRecord.info.applicationInfo, Boolean.valueOf(z), Boolean.valueOf(z2), str, activityRecord.intent.getComponent()));
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public void setBooting(boolean z) {
        this.mAmInternal.setBooting(z);
    }

    public boolean isBooting() {
        return this.mAmInternal.isBooting();
    }

    public void setBooted(boolean z) {
        this.mAmInternal.setBooted(z);
    }

    public boolean isBooted() {
        return this.mAmInternal.isBooted();
    }

    public void postFinishBooting(final boolean z, final boolean z2) {
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$postFinishBooting$10(z, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postFinishBooting$10(boolean z, boolean z2) {
        if (z) {
            this.mAmInternal.finishBooting();
        }
        if (z2) {
            this.mInternal.enableScreenAfterBoot(isBooted());
        }
    }

    public void setHeavyWeightProcess(ActivityRecord activityRecord) {
        this.mHeavyWeightProcess = activityRecord.app;
        this.f1161mH.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda19
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                ((ActivityTaskManagerService) obj).postHeavyWeightProcessNotification((WindowProcessController) obj2, (Intent) obj3, ((Integer) obj4).intValue());
            }
        }, this, activityRecord.app, activityRecord.intent, Integer.valueOf(activityRecord.mUserId)));
    }

    public void clearHeavyWeightProcessIfEquals(WindowProcessController windowProcessController) {
        if (this.mHeavyWeightProcess == null || this.mHeavyWeightProcess != windowProcessController) {
            return;
        }
        this.mHeavyWeightProcess = null;
        this.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda7
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((ActivityTaskManagerService) obj).cancelHeavyWeightProcessNotification(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(windowProcessController.mUserId)));
    }

    public final void cancelHeavyWeightProcessNotification(int i) {
        INotificationManager service = NotificationManager.getService();
        if (service == null) {
            return;
        }
        try {
            service.cancelNotificationWithTag(PackageManagerShellCommandDataLoader.PACKAGE, PackageManagerShellCommandDataLoader.PACKAGE, (String) null, 11, i);
        } catch (RemoteException unused) {
        } catch (RuntimeException e) {
            Slog.w("ActivityTaskManager", "Error canceling notification for service", e);
        }
    }

    public final void postHeavyWeightProcessNotification(WindowProcessController windowProcessController, Intent intent, int i) {
        INotificationManager service;
        if (windowProcessController == null || (service = NotificationManager.getService()) == null) {
            return;
        }
        try {
            Context createPackageContext = this.mContext.createPackageContext(windowProcessController.mInfo.packageName, 0);
            String string = this.mContext.getString(17040417, createPackageContext.getApplicationInfo().loadLabel(createPackageContext.getPackageManager()));
            try {
                service.enqueueNotificationWithTag(PackageManagerShellCommandDataLoader.PACKAGE, PackageManagerShellCommandDataLoader.PACKAGE, (String) null, 11, new Notification.Builder(createPackageContext, SystemNotificationChannels.HEAVY_WEIGHT_APP).setSmallIcon(17303596).setWhen(0L).setOngoing(true).setTicker(string).setColor(this.mContext.getColor(17170460)).setContentTitle(string).setContentText(this.mContext.getText(17040418)).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 335544320, null, new UserHandle(i))).build(), i);
            } catch (RemoteException unused) {
            } catch (RuntimeException e) {
                Slog.w("ActivityTaskManager", "Error showing notification for heavy-weight app", e);
            }
        } catch (PackageManager.NameNotFoundException e2) {
            Slog.w("ActivityTaskManager", "Unable to create context for heavy notification", e2);
        }
    }

    public IIntentSender getIntentSenderLocked(int i, String str, String str2, int i2, int i3, IBinder iBinder, String str3, int i4, Intent[] intentArr, String[] strArr, int i5, Bundle bundle) {
        ActivityRecord activityRecord;
        ActivityTaskManagerService activityTaskManagerService;
        if (i == 3) {
            ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
            if (isInRootTaskLocked == null) {
                Slog.w("ActivityTaskManager", "Failed createPendingResult: activity " + iBinder + " not in any root task");
                return null;
            } else if (isInRootTaskLocked.finishing) {
                Slog.w("ActivityTaskManager", "Failed createPendingResult: activity " + isInRootTaskLocked + " is finishing");
                return null;
            } else {
                activityTaskManagerService = this;
                activityRecord = isInRootTaskLocked;
            }
        } else {
            activityRecord = null;
            activityTaskManagerService = this;
        }
        PendingIntentRecord intentSender = activityTaskManagerService.mPendingIntentController.getIntentSender(i, str, str2, i2, i3, iBinder, str3, i4, intentArr, strArr, i5, bundle);
        if (!((i5 & 536870912) != 0) && i == 3) {
            if (activityRecord.pendingResults == null) {
                activityRecord.pendingResults = new HashSet<>();
            }
            activityRecord.pendingResults.add(intentSender.ref);
        }
        return intentSender;
    }

    public final void startTimeTrackingFocusedActivityLocked() {
        AppTimeTracker appTimeTracker;
        ActivityRecord topResumedActivity = this.mRootWindowContainer.getTopResumedActivity();
        if (this.mSleeping || (appTimeTracker = this.mCurAppTimeTracker) == null || topResumedActivity == null) {
            return;
        }
        appTimeTracker.start(topResumedActivity.packageName);
    }

    public final void updateResumedAppTrace(ActivityRecord activityRecord) {
        if (Trace.isTagEnabled(32L)) {
            ActivityRecord activityRecord2 = this.mTracedResumedActivity;
            if (activityRecord2 != null) {
                Trace.asyncTraceForTrackEnd(32L, "Focused app", System.identityHashCode(activityRecord2));
            }
            if (activityRecord != null) {
                Trace.asyncTraceForTrackBegin(32L, "Focused app", activityRecord.mActivityComponent.flattenToShortString(), System.identityHashCode(activityRecord));
            }
        }
        this.mTracedResumedActivity = activityRecord;
    }

    public boolean ensureConfigAndVisibilityAfterUpdate(ActivityRecord activityRecord, int i) {
        Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
        if (topDisplayFocusedRootTask != null) {
            if (i != 0 && activityRecord == null) {
                activityRecord = topDisplayFocusedRootTask.topRunningActivity();
            }
            if (activityRecord != null) {
                boolean ensureActivityConfiguration = activityRecord.ensureActivityConfiguration(i, false);
                this.mRootWindowContainer.ensureActivitiesVisible(activityRecord, i, false);
                return ensureActivityConfiguration;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleAppGcsLocked$11() {
        this.mAmInternal.scheduleAppGcs();
    }

    public void scheduleAppGcsLocked() {
        this.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManagerService.this.lambda$scheduleAppGcsLocked$11();
            }
        });
    }

    public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo applicationInfo) {
        return this.mCompatModePackages.compatibilityInfoForPackageLocked(applicationInfo);
    }

    public IPackageManager getPackageManager() {
        return AppGlobals.getPackageManager();
    }

    public PackageManagerInternal getPackageManagerInternalLocked() {
        if (this.mPmInternal == null) {
            this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPmInternal;
    }

    public ComponentName getSysUiServiceComponentLocked() {
        if (this.mSysUiServiceComponent == null) {
            this.mSysUiServiceComponent = getPackageManagerInternalLocked().getSystemUiServiceComponent();
        }
        return this.mSysUiServiceComponent;
    }

    public PermissionPolicyInternal getPermissionPolicyInternal() {
        if (this.mPermissionPolicyInternal == null) {
            this.mPermissionPolicyInternal = (PermissionPolicyInternal) LocalServices.getService(PermissionPolicyInternal.class);
        }
        return this.mPermissionPolicyInternal;
    }

    public StatusBarManagerInternal getStatusBarManagerInternal() {
        if (this.mStatusBarManagerInternal == null) {
            this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
        }
        return this.mStatusBarManagerInternal;
    }

    public AppWarnings getAppWarningsLocked() {
        return this.mAppWarnings;
    }

    public Intent getHomeIntent() {
        String str = this.mTopAction;
        String str2 = this.mTopData;
        Intent intent = new Intent(str, str2 != null ? Uri.parse(str2) : null);
        intent.setComponent(this.mTopComponent);
        intent.addFlags(256);
        if (this.mFactoryTest != 1) {
            intent.addCategory("android.intent.category.HOME");
        }
        return intent;
    }

    public Intent getSecondaryHomeIntent(String str) {
        String str2 = this.mTopAction;
        String str3 = this.mTopData;
        Intent intent = new Intent(str2, str3 != null ? Uri.parse(str3) : null);
        boolean z = this.mContext.getResources().getBoolean(17891865);
        if (str == null || z) {
            intent.setPackage(this.mContext.getResources().getString(17040000));
        } else {
            intent.setPackage(str);
        }
        intent.addFlags(256);
        if (this.mFactoryTest != 1) {
            intent.addCategory("android.intent.category.SECONDARY_HOME");
        }
        return intent;
    }

    public ApplicationInfo getAppInfoForUser(ApplicationInfo applicationInfo, int i) {
        if (applicationInfo == null) {
            return null;
        }
        ApplicationInfo applicationInfo2 = new ApplicationInfo(applicationInfo);
        applicationInfo2.initForUser(i);
        return applicationInfo2;
    }

    public WindowProcessController getProcessController(String str, int i) {
        if (i == 1000) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().get(str);
            if (sparseArray == null) {
                return null;
            }
            int size = sparseArray.size();
            for (int i2 = 0; i2 < size; i2++) {
                int keyAt = sparseArray.keyAt(i2);
                if (!UserHandle.isApp(keyAt) && UserHandle.isSameUser(keyAt, i)) {
                    return (WindowProcessController) sparseArray.valueAt(i2);
                }
            }
        }
        return (WindowProcessController) this.mProcessNames.get(str, i);
    }

    public WindowProcessController getProcessController(IApplicationThread iApplicationThread) {
        if (iApplicationThread == null) {
            return null;
        }
        IBinder asBinder = iApplicationThread.asBinder();
        ArrayMap map = this.mProcessNames.getMap();
        for (int size = map.size() - 1; size >= 0; size--) {
            SparseArray sparseArray = (SparseArray) map.valueAt(size);
            for (int size2 = sparseArray.size() - 1; size2 >= 0; size2--) {
                WindowProcessController windowProcessController = (WindowProcessController) sparseArray.valueAt(size2);
                if (windowProcessController.hasThread() && windowProcessController.getThread().asBinder() == asBinder) {
                    return windowProcessController;
                }
            }
        }
        return null;
    }

    public WindowProcessController getProcessController(int i, int i2) {
        WindowProcessController process = this.mProcessMap.getProcess(i);
        if (process != null && UserHandle.isApp(i2) && process.mUid == i2) {
            return process;
        }
        return null;
    }

    public boolean hasActiveVisibleWindow(int i) {
        if (this.mVisibleActivityProcessTracker.hasVisibleActivity(i)) {
            return true;
        }
        return this.mActiveUids.hasNonAppVisibleWindow(i);
    }

    public boolean isDeviceOwner(int i) {
        return i >= 0 && this.mDeviceOwnerUid == i;
    }

    public void setDeviceOwnerUid(int i) {
        this.mDeviceOwnerUid = i;
    }

    public void saveANRState(String str) {
        StringWriter stringWriter = new StringWriter();
        FastPrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 1024);
        fastPrintWriter.println("  ANR time: " + DateFormat.getDateTimeInstance().format(new Date()));
        if (str != null) {
            fastPrintWriter.println("  Reason: " + str);
        }
        fastPrintWriter.println();
        getActivityStartController().dump(fastPrintWriter, "  ", null);
        fastPrintWriter.println();
        fastPrintWriter.println("-------------------------------------------------------------------------------");
        dumpActivitiesLocked(null, fastPrintWriter, null, 0, true, false, null, -1, "");
        fastPrintWriter.println();
        fastPrintWriter.close();
        this.mLastANRState = stringWriter.toString();
    }

    public boolean isAssociatedCompanionApp(int i, int i2) {
        Set<Integer> set = this.mCompanionAppUidsMap.get(Integer.valueOf(i));
        if (set == null) {
            return false;
        }
        return set.contains(Integer.valueOf(i2));
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            throw logAndRethrowRuntimeExceptionOnTransact("ActivityTaskManager", e);
        }
    }

    public static RuntimeException logAndRethrowRuntimeExceptionOnTransact(String str, RuntimeException runtimeException) {
        if (!(runtimeException instanceof SecurityException)) {
            Slog.w("ActivityTaskManager", str + " onTransact aborts UID:" + Binder.getCallingUid() + " PID:" + Binder.getCallingPid(), runtimeException);
        }
        throw runtimeException;
    }

    public void onImeWindowSetOnDisplayArea(int i, DisplayArea displayArea) {
        if (i == WindowManagerService.MY_PID || i < 0) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -1810446914, 0, (String) null, (Object[]) null);
                return;
            }
            return;
        }
        WindowProcessController process = this.mProcessMap.getProcess(i);
        if (process == null) {
            if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_CONFIGURATION, -449118559, 1, (String) null, new Object[]{Long.valueOf(i)});
                return;
            }
            return;
        }
        process.registerDisplayAreaConfigurationListener(displayArea);
    }

    public void setRunningRemoteTransitionDelegate(IApplicationThread iApplicationThread) {
        TransitionController transitionController = getTransitionController();
        if (iApplicationThread == null || !transitionController.mRemotePlayer.reportRunning(iApplicationThread)) {
            this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "setRunningRemoteTransition");
            int callingPid = Binder.getCallingPid();
            int callingUid = Binder.getCallingUid();
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowProcessController processController = getProcessController(callingPid, callingUid);
                    if (processController == null || !processController.isRunningRemoteTransition()) {
                        String str = "Can't call setRunningRemoteTransition from a process (pid=" + callingPid + " uid=" + callingUid + ") which isn't itself running a remote transition.";
                        Slog.e("ActivityTaskManager", str);
                        throw new SecurityException(str);
                    }
                    WindowProcessController processController2 = getProcessController(iApplicationThread);
                    if (processController2 == null) {
                        Slog.w("ActivityTaskManager", "setRunningRemoteTransition: no process for " + iApplicationThread);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    transitionController.mRemotePlayer.update(processController2, true, false);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void registerScreenCaptureObserver(IBinder iBinder, IScreenCaptureObserver iScreenCaptureObserver) {
        this.mAmInternal.enforceCallingPermission("android.permission.DETECT_SCREEN_CAPTURE", "registerScreenCaptureObserver");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.registerCaptureObserver(iScreenCaptureObserver);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregisterScreenCaptureObserver(IBinder iBinder, IScreenCaptureObserver iScreenCaptureObserver) {
        this.mAmInternal.enforceCallingPermission("android.permission.DETECT_SCREEN_CAPTURE", "unregisterScreenCaptureObserver");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                if (forTokenLocked != null) {
                    forTokenLocked.unregisterCaptureObserver(iScreenCaptureObserver);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean instrumentationSourceHasPermission(int i, String str) {
        WindowProcessController process;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                process = this.mProcessMap.getProcess(i);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return process != null && process.isInstrumenting() && checkPermission(str, -1, process.getInstrumentationSourceUid()) == 0;
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$H */
    /* loaded from: classes2.dex */
    public final class HandlerC1840H extends Handler {
        public HandlerC1840H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                ((AppTimeTracker) message.obj).deliverResult(ActivityTaskManagerService.this.mContext);
            } else if (i == 2) {
                WindowProcessController windowProcessController = (WindowProcessController) message.obj;
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        windowProcessController.updateRunningRemoteOrRecentsAnimation();
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else if (i != 3) {
                if (i != 4) {
                    return;
                }
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (ActivityTaskManagerService.this.mAppSwitchesState == 0) {
                            ActivityTaskManagerService.this.mAppSwitchesState = 1;
                        }
                    } finally {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else {
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityTaskManagerService.this.mRetainPowerModeAndTopProcessState = false;
                        ActivityTaskManagerService.this.endLaunchPowerMode(4);
                        if (ActivityTaskManagerService.this.mTopApp != null && ActivityTaskManagerService.this.mTopProcessState == 12) {
                            ActivityTaskManagerService.this.mTopApp.updateProcessInfo(false, false, true, false);
                        }
                    } finally {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$UiHandler */
    /* loaded from: classes2.dex */
    public final class UiHandler extends Handler {
        public UiHandler() {
            super(UiThread.get().getLooper(), null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            ((Dialog) message.obj).dismiss();
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskManagerService$LocalService */
    /* loaded from: classes2.dex */
    public final class LocalService extends ActivityTaskManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityTaskManagerInternal.SleepTokenAcquirer createSleepTokenAcquirer(String str) {
            Objects.requireNonNull(str);
            return new SleepTokenAcquirerImpl(str);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ComponentName getHomeActivityForUser(int i) {
            ComponentName componentName;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord defaultDisplayHomeActivityForUser = ActivityTaskManagerService.this.mRootWindowContainer.getDefaultDisplayHomeActivityForUser(i);
                    componentName = defaultDisplayHomeActivityForUser == null ? null : defaultDisplayHomeActivityForUser.mActivityComponent;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return componentName;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onLocalVoiceInteractionStarted(IBinder iBinder, IVoiceInteractionSession iVoiceInteractionSession, IVoiceInteractor iVoiceInteractor) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.onLocalVoiceInteractionStartedLocked(iBinder, iVoiceInteractionSession, iVoiceInteractor);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public List<ActivityAssistInfo> getTopVisibleActivities() {
            List<ActivityAssistInfo> topVisibleActivities;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    topVisibleActivities = ActivityTaskManagerService.this.mRootWindowContainer.getTopVisibleActivities();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return topVisibleActivities;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean hasResumedActivity(int i) {
            return ActivityTaskManagerService.this.mVisibleActivityProcessTracker.hasResumedActivity(i);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setBackgroundActivityStartCallback(BackgroundActivityStartCallback backgroundActivityStartCallback) {
            ActivityTaskManagerService.this.mBackgroundActivityStartCallback = backgroundActivityStartCallback;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setAccessibilityServiceUids(IntArray intArray) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mAccessibilityServiceUids = intArray.toArray();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int startActivitiesAsPackage(String str, String str2, int i, Intent[] intentArr, Bundle bundle) {
            Objects.requireNonNull(intentArr, "intents");
            String[] strArr = new String[intentArr.length];
            long clearCallingIdentity = Binder.clearCallingIdentity();
            int i2 = 0;
            for (int i3 = 0; i3 < intentArr.length; i3++) {
                try {
                    try {
                        strArr[i3] = intentArr[i3].resolveTypeIfNeeded(ActivityTaskManagerService.this.mContext.getContentResolver());
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        throw th;
                    }
                } catch (RemoteException unused) {
                }
            }
            try {
                i2 = AppGlobals.getPackageManager().getPackageUid(str, 268435456L, i);
            } catch (RemoteException unused2) {
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(i2, str, str2, intentArr, strArr, null, SafeActivityOptions.fromBundle(bundle), i, false, null, BackgroundStartPrivileges.NONE);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int startActivitiesInPackage(int i, int i2, int i3, String str, String str2, Intent[] intentArr, String[] strArr, IBinder iBinder, SafeActivityOptions safeActivityOptions, int i4, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
            ActivityTaskManagerService.this.assertPackageMatchesCallingUid(str);
            return ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(i, i2, i3, str, str2, intentArr, strArr, iBinder, safeActivityOptions, i4, z, pendingIntentRecord, backgroundStartPrivileges);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int startActivityInPackage(int i, int i2, int i3, String str, String str2, Intent intent, String str3, IBinder iBinder, String str4, int i4, int i5, SafeActivityOptions safeActivityOptions, int i6, Task task, String str5, boolean z, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
            ActivityTaskManagerService.this.assertPackageMatchesCallingUid(str);
            return ActivityTaskManagerService.this.getActivityStartController().startActivityInPackage(i, i2, i3, str, str2, intent, str3, iBinder, str4, i4, i5, safeActivityOptions, i6, task, str5, z, pendingIntentRecord, backgroundStartPrivileges);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, Intent intent, IBinder iBinder, int i, Bundle bundle, int i2) {
            ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
            return activityTaskManagerService.startActivityAsUser(iApplicationThread, str, str2, intent, intent.resolveTypeIfNeeded(activityTaskManagerService.mContext.getContentResolver()), iBinder, null, 0, i, null, bundle, i2, false);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setVr2dDisplayId(int i) {
            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1679411993, 1, (String) null, new Object[]{Long.valueOf(i)});
            }
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mVr2dDisplayId = i;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setFocusedActivity(IBinder iBinder) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
                    if (forTokenLocked == null) {
                        throw new IllegalArgumentException("setFocusedActivity: No activity record matching token=" + iBinder);
                    } else if (forTokenLocked.moveFocusableActivityToTop("setFocusedActivity")) {
                        ActivityTaskManagerService.this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void registerScreenObserver(ActivityTaskManagerInternal.ScreenObserver screenObserver) {
            ActivityTaskManagerService.this.mScreenObservers.add(screenObserver);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isCallerRecents(int i) {
            return ActivityTaskManagerService.this.isCallerRecents(i);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean checkCanCloseSystemDialogs(int i, int i2, String str) {
            return ActivityTaskManagerService.this.checkCanCloseSystemDialogs(i, i2, str);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean canCloseSystemDialogs(int i, int i2) {
            return ActivityTaskManagerService.this.canCloseSystemDialogs(i, i2);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void notifyActiveVoiceInteractionServiceChanged(ComponentName componentName) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mActiveVoiceInteractionServiceComponent = componentName;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void notifyActiveDreamChanged(ComponentName componentName) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mActiveDreamComponent = componentName;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setAllowAppSwitches(String str, int i, int i2) {
            if (ActivityTaskManagerService.this.mAmInternal.isUserRunning(i2, 1)) {
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ArrayMap<String, Integer> arrayMap = ActivityTaskManagerService.this.mAllowAppSwitchUids.get(i2);
                        if (arrayMap == null) {
                            if (i < 0) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            } else {
                                arrayMap = new ArrayMap<>();
                                ActivityTaskManagerService.this.mAllowAppSwitchUids.put(i2, arrayMap);
                            }
                        }
                        if (i < 0) {
                            arrayMap.remove(str);
                        } else {
                            arrayMap.put(str, Integer.valueOf(i));
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onUserStopped(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.getRecentTasks().unloadUserDataFromMemoryLocked(i);
                    ActivityTaskManagerService.this.mAllowAppSwitchUids.remove(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isGetTasksAllowed(String str, int i, int i2) {
            return ActivityTaskManagerService.this.isGetTasksAllowed(str, i, i2);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onProcessAdded(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mProcessNames.put(windowProcessController.mName, windowProcessController.mUid, windowProcessController);
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onProcessRemoved(String str, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mProcessNames.remove(str, i);
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onCleanUpApplicationRecord(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                if (windowProcessController == ActivityTaskManagerService.this.mHomeProcess) {
                    ActivityTaskManagerService.this.mHomeProcess = null;
                }
                if (windowProcessController == ActivityTaskManagerService.this.mPreviousProcess) {
                    ActivityTaskManagerService.this.mPreviousProcess = null;
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int getTopProcessState() {
            if (ActivityTaskManagerService.this.mRetainPowerModeAndTopProcessState) {
                return 2;
            }
            return ActivityTaskManagerService.this.mTopProcessState;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean useTopSchedGroupForTopProcess() {
            return ActivityTaskManagerService.this.mDemoteTopAppReasons == 0;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void clearHeavyWeightProcessIfEquals(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.clearHeavyWeightProcessIfEquals(windowProcessController);
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void finishHeavyWeightApp() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null) {
                        ActivityTaskManagerService.this.mHeavyWeightProcess.finishActivities();
                    }
                    ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                    activityTaskManagerService.clearHeavyWeightProcessIfEquals(activityTaskManagerService.mHeavyWeightProcess);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isSleeping() {
            return ActivityTaskManagerService.this.mSleeping;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isShuttingDown() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = ActivityTaskManagerService.this.mShuttingDown;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean shuttingDown(boolean z, int i) {
            boolean shutdownLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                    activityTaskManagerService.mShuttingDown = true;
                    activityTaskManagerService.mRootWindowContainer.prepareForShutdown();
                    ActivityTaskManagerService.this.updateEventDispatchingLocked(z);
                    ActivityTaskManagerService.this.notifyTaskPersisterLocked(null, true);
                    shutdownLocked = ActivityTaskManagerService.this.mTaskSupervisor.shutdownLocked(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return shutdownLocked;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void enableScreenAfterBoot(boolean z) {
            EventLogTags.writeBootProgressEnableScreen(SystemClock.uptimeMillis());
            ActivityTaskManagerService.this.mWindowManager.enableScreenAfterBoot();
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.updateEventDispatchingLocked(z);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean showStrictModeViolationDialog() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = (!ActivityTaskManagerService.this.mShowDialogs || ActivityTaskManagerService.this.mSleeping || ActivityTaskManagerService.this.mShuttingDown) ? false : true;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void showSystemReadyErrorDialogsIfNeeded() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (AppGlobals.getPackageManager().hasSystemUidErrors()) {
                            Slog.e("ActivityTaskManager", "UIDs on the system are inconsistent, you need to wipe your data partition or your device will be unstable.");
                            ActivityTaskManagerService.this.mUiHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$LocalService$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ActivityTaskManagerService.LocalService.this.lambda$showSystemReadyErrorDialogsIfNeeded$0();
                                }
                            });
                        }
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                } catch (RemoteException unused) {
                }
                if (!Build.isBuildConsistent()) {
                    Slog.e("ActivityTaskManager", "Build fingerprint is not consistent, warning user");
                    ActivityTaskManagerService.this.mUiHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$LocalService$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActivityTaskManagerService.LocalService.this.lambda$showSystemReadyErrorDialogsIfNeeded$1();
                        }
                    });
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showSystemReadyErrorDialogsIfNeeded$0() {
            if (ActivityTaskManagerService.this.mShowDialogs) {
                BaseErrorDialog baseErrorDialog = new BaseErrorDialog(ActivityTaskManagerService.this.mUiContext);
                baseErrorDialog.getWindow().setType(2010);
                baseErrorDialog.setCancelable(false);
                baseErrorDialog.setTitle(ActivityTaskManagerService.this.mUiContext.getText(17039665));
                baseErrorDialog.setMessage(ActivityTaskManagerService.this.mUiContext.getText(17041635));
                baseErrorDialog.setButton(-1, ActivityTaskManagerService.this.mUiContext.getText(17039370), ActivityTaskManagerService.this.mUiHandler.obtainMessage(1, baseErrorDialog));
                baseErrorDialog.show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showSystemReadyErrorDialogsIfNeeded$1() {
            if (ActivityTaskManagerService.this.mShowDialogs) {
                BaseErrorDialog baseErrorDialog = new BaseErrorDialog(ActivityTaskManagerService.this.mUiContext);
                baseErrorDialog.getWindow().setType(2010);
                baseErrorDialog.setCancelable(false);
                baseErrorDialog.setTitle(ActivityTaskManagerService.this.mUiContext.getText(17039665));
                baseErrorDialog.setMessage(ActivityTaskManagerService.this.mUiContext.getText(17041634));
                baseErrorDialog.setButton(-1, ActivityTaskManagerService.this.mUiContext.getText(17039370), ActivityTaskManagerService.this.mUiHandler.obtainMessage(1, baseErrorDialog));
                baseErrorDialog.show();
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onProcessMapped(int i, WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProcessMap.put(i, windowProcessController);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onProcessUnMapped(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProcessMap.remove(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onPackageDataCleared(String str, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageDataClearedLocked(str);
                    ActivityTaskManagerService.this.mAppWarnings.onPackageDataCleared(str);
                    ActivityTaskManagerService.this.mPackageConfigPersister.onPackageDataCleared(str, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onPackageUninstalled(String str, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mAppWarnings.onPackageUninstalled(str);
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageUninstalledLocked(str);
                    ActivityTaskManagerService.this.mPackageConfigPersister.onPackageUninstall(str, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onPackageAdded(String str, boolean z) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageAddedLocked(str, z);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onPackageReplaced(ApplicationInfo applicationInfo) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootWindowContainer.updateActivityApplicationInfo(applicationInfo);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public CompatibilityInfo compatibilityInfoForPackage(ApplicationInfo applicationInfo) {
            CompatibilityInfo compatibilityInfoForPackageLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    compatibilityInfoForPackageLocked = ActivityTaskManagerService.this.compatibilityInfoForPackageLocked(applicationInfo);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return compatibilityInfoForPackageLocked;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void sendActivityResult(int i, IBinder iBinder, String str, int i2, int i3, Intent intent) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                    if (isInRootTaskLocked != null && isInRootTaskLocked.getRootTask() != null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        NeededUriGrants collectGrants = ActivityTaskManagerService.this.collectGrants(intent, isInRootTaskLocked);
                        synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                isInRootTaskLocked.sendResult(i, str, i2, i3, intent, collectGrants);
                            } finally {
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void clearPendingResultForActivity(IBinder iBinder, WeakReference<PendingIntentRecord> weakReference) {
            HashSet<WeakReference<PendingIntentRecord>> hashSet;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                    if (isInRootTaskLocked != null && (hashSet = isInRootTaskLocked.pendingResults) != null) {
                        hashSet.remove(weakReference);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ComponentName getActivityName(IBinder iBinder) {
            ComponentName component;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                    component = isInRootTaskLocked != null ? isInRootTaskLocked.intent.getComponent() : null;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return component;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityTaskManagerInternal.ActivityTokens getAttachedNonFinishingActivityForTask(int i, IBinder iBinder) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task anyTaskForId = ActivityTaskManagerService.this.mRootWindowContainer.anyTaskForId(i, 0);
                    if (anyTaskForId == null) {
                        Slog.w("ActivityTaskManager", "getApplicationThreadForTopActivity failed: Requested task not found");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    final ArrayList arrayList = new ArrayList();
                    anyTaskForId.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityTaskManagerService$LocalService$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityTaskManagerService.LocalService.lambda$getAttachedNonFinishingActivityForTask$2(arrayList, (ActivityRecord) obj);
                        }
                    });
                    if (arrayList.size() <= 0) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    } else if (iBinder == null && ((ActivityRecord) arrayList.get(0)).attachedToProcess()) {
                        ActivityRecord activityRecord = (ActivityRecord) arrayList.get(0);
                        ActivityTaskManagerInternal.ActivityTokens activityTokens = new ActivityTaskManagerInternal.ActivityTokens(activityRecord.token, activityRecord.assistToken, activityRecord.app.getThread(), activityRecord.shareableActivityToken, activityRecord.getUid());
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return activityTokens;
                    } else {
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            ActivityRecord activityRecord2 = (ActivityRecord) arrayList.get(i2);
                            if (activityRecord2.shareableActivityToken == iBinder && activityRecord2.attachedToProcess()) {
                                ActivityTaskManagerInternal.ActivityTokens activityTokens2 = new ActivityTaskManagerInternal.ActivityTokens(activityRecord2.token, activityRecord2.assistToken, activityRecord2.app.getThread(), activityRecord2.shareableActivityToken, activityRecord2.getUid());
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return activityTokens2;
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        public static /* synthetic */ void lambda$getAttachedNonFinishingActivityForTask$2(List list, ActivityRecord activityRecord) {
            if (activityRecord.finishing) {
                return;
            }
            list.add(activityRecord);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public IIntentSender getIntentSender(int i, String str, String str2, int i2, int i3, IBinder iBinder, String str3, int i4, Intent[] intentArr, String[] strArr, int i5, Bundle bundle) {
            IIntentSender intentSenderLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    intentSenderLocked = ActivityTaskManagerService.this.getIntentSenderLocked(i, str, str2, i2, i3, iBinder, str3, i4, intentArr, strArr, i5, bundle);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return intentSenderLocked;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityServiceConnectionsHolder getServiceConnectionsHolder(IBinder iBinder) {
            ActivityRecord forToken = ActivityRecord.forToken(iBinder);
            if (forToken == null || !forToken.inHistory) {
                return null;
            }
            return forToken.getOrCreateServiceConnectionsHolder();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public Intent getHomeIntent() {
            Intent homeIntent;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    homeIntent = ActivityTaskManagerService.this.getHomeIntent();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return homeIntent;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean startHomeActivity(int i, String str) {
            boolean startHomeOnDisplay;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnDisplay = ActivityTaskManagerService.this.mRootWindowContainer.startHomeOnDisplay(i, str, 0);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnDisplay;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean startHomeOnDisplay(int i, String str, int i2, boolean z, boolean z2) {
            boolean startHomeOnDisplay;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnDisplay = ActivityTaskManagerService.this.mRootWindowContainer.startHomeOnDisplay(i, str, i2, z, z2);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnDisplay;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean startHomeOnAllDisplays(int i, String str) {
            boolean startHomeOnAllDisplays;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnAllDisplays = ActivityTaskManagerService.this.mRootWindowContainer.startHomeOnAllDisplays(i, str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnAllDisplays;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void updateTopComponentForFactoryTest() {
            final CharSequence text;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                    if (activityTaskManagerService.mFactoryTest != 1) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    ResolveInfo resolveActivity = activityTaskManagerService.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.FACTORY_TEST"), 1024);
                    if (resolveActivity != null) {
                        ActivityInfo activityInfo = resolveActivity.activityInfo;
                        ApplicationInfo applicationInfo = activityInfo.applicationInfo;
                        if ((1 & applicationInfo.flags) != 0) {
                            ActivityTaskManagerService activityTaskManagerService2 = ActivityTaskManagerService.this;
                            activityTaskManagerService2.mTopAction = "android.intent.action.FACTORY_TEST";
                            activityTaskManagerService2.mTopData = null;
                            activityTaskManagerService2.mTopComponent = new ComponentName(applicationInfo.packageName, activityInfo.name);
                            text = null;
                        } else {
                            text = ActivityTaskManagerService.this.mContext.getResources().getText(17040295);
                        }
                    } else {
                        text = ActivityTaskManagerService.this.mContext.getResources().getText(17040294);
                    }
                    if (text == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    ActivityTaskManagerService activityTaskManagerService3 = ActivityTaskManagerService.this;
                    activityTaskManagerService3.mTopAction = null;
                    activityTaskManagerService3.mTopData = null;
                    activityTaskManagerService3.mTopComponent = null;
                    activityTaskManagerService3.mUiHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskManagerService$LocalService$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActivityTaskManagerService.LocalService.this.lambda$updateTopComponentForFactoryTest$3(text);
                        }
                    });
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateTopComponentForFactoryTest$3(CharSequence charSequence) {
            new FactoryErrorDialog(ActivityTaskManagerService.this.mUiContext, charSequence).show();
            ActivityTaskManagerService.this.mAmInternal.ensureBootCompleted();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void handleAppDied(WindowProcessController windowProcessController, boolean z, Runnable runnable) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mTaskSupervisor.beginDeferResume();
                boolean handleAppDied = windowProcessController.handleAppDied();
                ActivityTaskManagerService.this.mTaskSupervisor.endDeferResume();
                if (!z && handleAppDied) {
                    ActivityTaskManagerService.this.deferWindowLayout();
                    if (!ActivityTaskManagerService.this.mRootWindowContainer.resumeFocusedTasksTopActivities()) {
                        ActivityTaskManagerService.this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
                    }
                    ActivityTaskManagerService.this.continueWindowLayout();
                }
            }
            if (windowProcessController.isInstrumenting()) {
                runnable.run();
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void closeSystemDialogs(String str) {
            ActivityTaskManagerService.enforceNotIsolatedCaller("closeSystemDialogs");
            int callingPid = Binder.getCallingPid();
            int callingUid = Binder.getCallingUid();
            if (checkCanCloseSystemDialogs(callingPid, callingUid, null)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (callingUid >= 10000) {
                            WindowProcessController process = ActivityTaskManagerService.this.mProcessMap.getProcess(callingPid);
                            if (!process.isPerceptible()) {
                                Slog.w("ActivityTaskManager", "Ignoring closeSystemDialogs " + str + " from background process " + process);
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                        }
                        ActivityTaskManagerService.this.mWindowManager.closeSystemDialogs(str);
                        ActivityTaskManagerService.this.mRootWindowContainer.closeSystemDialogActivities(str);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        ActivityTaskManagerService.this.mAmInternal.broadcastCloseSystemDialogs(str);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void cleanupDisabledPackageComponents(String str, Set<String> set, int i, boolean z) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mRootWindowContainer.finishDisabledPackageActivities(str, set, true, false, i, false) && z) {
                        ActivityTaskManagerService.this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                        ActivityTaskManagerService.this.mTaskSupervisor.scheduleIdle();
                    }
                    ActivityTaskManagerService.this.getRecentTasks().cleanupDisabledPackageTasksLocked(str, set, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean onForceStopPackage(String str, boolean z, boolean z2, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RootWindowContainer rootWindowContainer = ActivityTaskManagerService.this.mRootWindowContainer;
                    if (rootWindowContainer == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                    boolean finishDisabledPackageActivities = rootWindowContainer.finishDisabledPackageActivities(str, null, z, z2, i, true);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return finishDisabledPackageActivities;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void resumeTopActivities(boolean z) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                    if (z) {
                        ActivityTaskManagerService.this.mTaskSupervisor.scheduleIdle();
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void preBindApplication(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mTaskSupervisor.getActivityMetricsLogger().notifyBindApplication(windowProcessController.mInfo);
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean attachApplication(WindowProcessController windowProcessController) throws RemoteException {
            boolean attachApplication;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                if (Trace.isTagEnabled(32L)) {
                    Trace.traceBegin(32L, "attachApplication:" + windowProcessController.mName);
                }
                attachApplication = ActivityTaskManagerService.this.mRootWindowContainer.attachApplication(windowProcessController);
                Trace.traceEnd(32L);
            }
            return attachApplication;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void notifyLockedProfile(int i, int i2) {
            try {
                if (!AppGlobals.getPackageManager().isUidPrivileged(Binder.getCallingUid())) {
                    throw new SecurityException("Only privileged app can call notifyLockedProfile");
                }
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        if (ActivityTaskManagerService.this.mAmInternal.shouldConfirmCredentials(i)) {
                            if (ActivityTaskManagerService.this.mKeyguardController.isKeyguardLocked(0)) {
                                startHomeActivity(i2, "notifyLockedProfile");
                            }
                            ActivityTaskManagerService.this.mRootWindowContainer.lockAllProfileTasks(i);
                        }
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (RemoteException e) {
                throw new SecurityException("Fail to check is caller a privileged app", e);
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void startConfirmDeviceCredentialIntent(Intent intent, Bundle bundle) {
            ActivityTaskManagerService.enforceTaskPermission("startConfirmDeviceCredentialIntent");
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    intent.addFlags(276824064);
                    ActivityTaskManagerService.this.mContext.startActivityAsUser(intent, (bundle != null ? new ActivityOptions(bundle) : ActivityOptions.makeBasic()).toBundle(), UserHandle.CURRENT);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void writeActivitiesToProto(ProtoOutputStream protoOutputStream) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootWindowContainer.dumpDebug(protoOutputStream, 1146756268034L, 0);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, String str2, int i2) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!"activities".equals(str) && !"a".equals(str)) {
                        if ("lastanr".equals(str)) {
                            ActivityTaskManagerService.this.dumpLastANRLocked(printWriter);
                        } else if ("lastanr-traces".equals(str)) {
                            ActivityTaskManagerService.this.dumpLastANRTracesLocked(printWriter);
                        } else if ("starter".equals(str)) {
                            ActivityTaskManagerService.this.dumpActivityStarterLocked(printWriter, str2);
                        } else if ("containers".equals(str)) {
                            ActivityTaskManagerService.this.dumpActivityContainersLocked(printWriter);
                        } else {
                            if (!"recents".equals(str) && !"r".equals(str)) {
                                if ("top-resumed".equals(str)) {
                                    ActivityTaskManagerService.this.dumpTopResumedActivityLocked(printWriter);
                                } else if ("visible".equals(str)) {
                                    ActivityTaskManagerService.this.dumpVisibleActivitiesLocked(printWriter, i2);
                                }
                            }
                            if (ActivityTaskManagerService.this.getRecentTasks() != null) {
                                ActivityTaskManagerService.this.getRecentTasks().dump(printWriter, z, str2);
                            }
                        }
                    }
                    ActivityTaskManagerService.this.dumpActivitiesLocked(fileDescriptor, printWriter, strArr, i, z, z2, str2, i2);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean dumpForProcesses(FileDescriptor fileDescriptor, PrintWriter printWriter, boolean z, String str, int i, boolean z2, boolean z3, int i2) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mHomeProcess != null && (str == null || ActivityTaskManagerService.this.mHomeProcess.containsPackage(str))) {
                        if (z2) {
                            printWriter.println();
                            z2 = false;
                        }
                        printWriter.println("  mHomeProcess: " + ActivityTaskManagerService.this.mHomeProcess);
                    }
                    if (ActivityTaskManagerService.this.mPreviousProcess != null && (str == null || ActivityTaskManagerService.this.mPreviousProcess.containsPackage(str))) {
                        if (z2) {
                            printWriter.println();
                            z2 = false;
                        }
                        printWriter.println("  mPreviousProcess: " + ActivityTaskManagerService.this.mPreviousProcess);
                    }
                    if (z && (ActivityTaskManagerService.this.mPreviousProcess == null || str == null || ActivityTaskManagerService.this.mPreviousProcess.containsPackage(str))) {
                        StringBuilder sb = new StringBuilder(128);
                        sb.append("  mPreviousProcessVisibleTime: ");
                        TimeUtils.formatDuration(ActivityTaskManagerService.this.mPreviousProcessVisibleTime, sb);
                        printWriter.println(sb);
                    }
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null && (str == null || ActivityTaskManagerService.this.mHeavyWeightProcess.containsPackage(str))) {
                        if (z2) {
                            printWriter.println();
                            z2 = false;
                        }
                        printWriter.println("  mHeavyWeightProcess: " + ActivityTaskManagerService.this.mHeavyWeightProcess);
                    }
                    if (str == null) {
                        printWriter.println("  mGlobalConfiguration: " + ActivityTaskManagerService.this.getGlobalConfiguration());
                        ActivityTaskManagerService.this.mRootWindowContainer.dumpDisplayConfigs(printWriter, "  ");
                    }
                    if (z) {
                        Task topDisplayFocusedRootTask = ActivityTaskManagerService.this.getTopDisplayFocusedRootTask();
                        if (str == null && topDisplayFocusedRootTask != null) {
                            printWriter.println("  mConfigWillChange: " + topDisplayFocusedRootTask.mConfigWillChange);
                        }
                        if (ActivityTaskManagerService.this.mCompatModePackages.getPackages().size() > 0) {
                            boolean z4 = false;
                            for (Map.Entry<String, Integer> entry : ActivityTaskManagerService.this.mCompatModePackages.getPackages().entrySet()) {
                                String key = entry.getKey();
                                int intValue = entry.getValue().intValue();
                                if (str == null || str.equals(key)) {
                                    if (!z4) {
                                        printWriter.println("  mScreenCompatPackages:");
                                        z4 = true;
                                    }
                                    printWriter.println("    " + key + ": " + intValue);
                                }
                            }
                        }
                    }
                    if (str == null) {
                        printWriter.println("  mWakefulness=" + PowerManagerInternal.wakefulnessToString(i2));
                        printWriter.println("  mSleepTokens=" + ActivityTaskManagerService.this.mRootWindowContainer.mSleepTokens);
                        if (ActivityTaskManagerService.this.mRunningVoice != null) {
                            printWriter.println("  mRunningVoice=" + ActivityTaskManagerService.this.mRunningVoice);
                            printWriter.println("  mVoiceWakeLock" + ActivityTaskManagerService.this.mVoiceWakeLock);
                        }
                        printWriter.println("  mSleeping=" + ActivityTaskManagerService.this.mSleeping);
                        printWriter.println("  mShuttingDown=" + ActivityTaskManagerService.this.mShuttingDown + " mTestPssMode=" + z3);
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("  mVrController=");
                        sb2.append(ActivityTaskManagerService.this.mVrController);
                        printWriter.println(sb2.toString());
                    }
                    AppTimeTracker appTimeTracker = ActivityTaskManagerService.this.mCurAppTimeTracker;
                    if (appTimeTracker != null) {
                        appTimeTracker.dumpWithHeader(printWriter, "  ", true);
                    }
                    if (ActivityTaskManagerService.this.mAllowAppSwitchUids.size() > 0) {
                        boolean z5 = false;
                        for (int i3 = 0; i3 < ActivityTaskManagerService.this.mAllowAppSwitchUids.size(); i3++) {
                            ArrayMap<String, Integer> valueAt = ActivityTaskManagerService.this.mAllowAppSwitchUids.valueAt(i3);
                            for (int i4 = 0; i4 < valueAt.size(); i4++) {
                                if (str == null || UserHandle.getAppId(valueAt.valueAt(i4).intValue()) == i) {
                                    if (z2) {
                                        printWriter.println();
                                        z2 = false;
                                    }
                                    if (!z5) {
                                        printWriter.println("  mAllowAppSwitchUids:");
                                        z5 = true;
                                    }
                                    printWriter.print("    User ");
                                    printWriter.print(ActivityTaskManagerService.this.mAllowAppSwitchUids.keyAt(i3));
                                    printWriter.print(": Type ");
                                    printWriter.print(valueAt.keyAt(i4));
                                    printWriter.print(" = ");
                                    UserHandle.formatUid(printWriter, valueAt.valueAt(i4).intValue());
                                    printWriter.println();
                                }
                            }
                        }
                    }
                    if (str == null) {
                        if (ActivityTaskManagerService.this.mController != null) {
                            printWriter.println("  mController=" + ActivityTaskManagerService.this.mController + " mControllerIsAMonkey=" + ActivityTaskManagerService.this.mControllerIsAMonkey);
                        }
                        printWriter.println("  mGoingToSleepWakeLock=" + ActivityTaskManagerService.this.mTaskSupervisor.mGoingToSleepWakeLock);
                        printWriter.println("  mLaunchingActivityWakeLock=" + ActivityTaskManagerService.this.mTaskSupervisor.mLaunchingActivityWakeLock);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z2;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void writeProcessesToProto(ProtoOutputStream protoOutputStream, String str, int i, boolean z) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (str == null) {
                        ActivityTaskManagerService.this.getGlobalConfiguration().dumpDebug(protoOutputStream, 1146756268051L);
                        Task topDisplayFocusedRootTask = ActivityTaskManagerService.this.getTopDisplayFocusedRootTask();
                        if (topDisplayFocusedRootTask != null) {
                            protoOutputStream.write(1133871366165L, topDisplayFocusedRootTask.mConfigWillChange);
                        }
                        ActivityTaskManagerService.this.writeSleepStateToProto(protoOutputStream, i, z);
                        if (ActivityTaskManagerService.this.mRunningVoice != null) {
                            long start = protoOutputStream.start(1146756268060L);
                            protoOutputStream.write(1138166333441L, ActivityTaskManagerService.this.mRunningVoice.toString());
                            ActivityTaskManagerService.this.mVoiceWakeLock.dumpDebug(protoOutputStream, 1146756268034L);
                            protoOutputStream.end(start);
                        }
                        ActivityTaskManagerService.this.mVrController.dumpDebug(protoOutputStream, 1146756268061L);
                        if (ActivityTaskManagerService.this.mController != null) {
                            long start2 = protoOutputStream.start(1146756268069L);
                            protoOutputStream.write(1138166333441L, ActivityTaskManagerService.this.mController.toString());
                            protoOutputStream.write(1133871366146L, ActivityTaskManagerService.this.mControllerIsAMonkey);
                            protoOutputStream.end(start2);
                        }
                        ActivityTaskManagerService.this.mTaskSupervisor.mGoingToSleepWakeLock.dumpDebug(protoOutputStream, 1146756268079L);
                        ActivityTaskManagerService.this.mTaskSupervisor.mLaunchingActivityWakeLock.dumpDebug(protoOutputStream, 1146756268080L);
                    }
                    if (ActivityTaskManagerService.this.mHomeProcess != null && (str == null || ActivityTaskManagerService.this.mHomeProcess.containsPackage(str))) {
                        ActivityTaskManagerService.this.mHomeProcess.dumpDebug(protoOutputStream, 1146756268047L);
                    }
                    if (ActivityTaskManagerService.this.mPreviousProcess != null && (str == null || ActivityTaskManagerService.this.mPreviousProcess.containsPackage(str))) {
                        ActivityTaskManagerService.this.mPreviousProcess.dumpDebug(protoOutputStream, 1146756268048L);
                        protoOutputStream.write(1112396529681L, ActivityTaskManagerService.this.mPreviousProcessVisibleTime);
                    }
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null && (str == null || ActivityTaskManagerService.this.mHeavyWeightProcess.containsPackage(str))) {
                        ActivityTaskManagerService.this.mHeavyWeightProcess.dumpDebug(protoOutputStream, 1146756268050L);
                    }
                    for (Map.Entry<String, Integer> entry : ActivityTaskManagerService.this.mCompatModePackages.getPackages().entrySet()) {
                        String key = entry.getKey();
                        int intValue = entry.getValue().intValue();
                        if (str == null || str.equals(key)) {
                            long start3 = protoOutputStream.start(2246267895830L);
                            protoOutputStream.write(1138166333441L, key);
                            protoOutputStream.write(1120986464258L, intValue);
                            protoOutputStream.end(start3);
                        }
                    }
                    AppTimeTracker appTimeTracker = ActivityTaskManagerService.this.mCurAppTimeTracker;
                    if (appTimeTracker != null) {
                        appTimeTracker.dumpDebug(protoOutputStream, 1146756268063L, true);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean dumpActivity(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr, int i, boolean z, boolean z2, boolean z3, boolean z4, int i2, int i3) {
            return ActivityTaskManagerService.this.dumpActivity(fileDescriptor, printWriter, str, strArr, i, z, z2, z3, z4, i2, i3);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void dumpForOom(PrintWriter printWriter) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    printWriter.println("  mHomeProcess: " + ActivityTaskManagerService.this.mHomeProcess);
                    printWriter.println("  mPreviousProcess: " + ActivityTaskManagerService.this.mPreviousProcess);
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null) {
                        printWriter.println("  mHeavyWeightProcess: " + ActivityTaskManagerService.this.mHeavyWeightProcess);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean canGcNow() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = isSleeping() || ActivityTaskManagerService.this.mRootWindowContainer.allResumedActivitiesIdle();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public WindowProcessController getTopApp() {
            return ActivityTaskManagerService.this.mTopApp;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void scheduleDestroyAllActivities(String str) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootWindowContainer.scheduleDestroyAllActivities(str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void removeUser(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootWindowContainer.removeUser(i);
                    ActivityTaskManagerService.this.mPackageConfigPersister.removeUser(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean switchUser(int i, UserState userState) {
            boolean switchUser;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    switchUser = ActivityTaskManagerService.this.mRootWindowContainer.switchUser(i, userState);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return switchUser;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onHandleAppCrash(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    windowProcessController.handleAppCrash();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int finishTopCrashedActivities(WindowProcessController windowProcessController, String str) {
            int finishTopCrashedActivities;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    finishTopCrashedActivities = ActivityTaskManagerService.this.mRootWindowContainer.finishTopCrashedActivities(windowProcessController, str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return finishTopCrashedActivities;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onUidActive(int i, int i2) {
            ActivityTaskManagerService.this.mActiveUids.onUidActive(i, i2);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onUidInactive(int i) {
            ActivityTaskManagerService.this.mActiveUids.onUidInactive(i);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onUidProcStateChanged(int i, int i2) {
            ActivityTaskManagerService.this.mActiveUids.onUidProcStateChanged(i, i2);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean handleAppCrashInActivityController(String str, int i, String str2, String str3, long j, String str4, Runnable runnable) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    IActivityController iActivityController = ActivityTaskManagerService.this.mController;
                    if (iActivityController == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                    Runnable runnable2 = null;
                    try {
                        if (!iActivityController.appCrashed(str, i, str2, str3, j, str4)) {
                            runnable2 = runnable;
                        }
                    } catch (RemoteException unused) {
                        ActivityTaskManagerService.this.mController = null;
                        Watchdog.getInstance().setActivityController(null);
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (runnable2 != null) {
                        runnable2.run();
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void removeRecentTasksByPackageName(String str, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.removeTasksByPackageName(str, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void cleanupRecentTasksForUser(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.cleanupLocked(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void loadRecentTasksForUser(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.loadUserRecentsLocked(i);
                    ActivityTaskManagerService.this.mPackageConfigPersister.loadUserPackages(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void onPackagesSuspendedChanged(String[] strArr, boolean z, int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.onPackagesSuspendedChanged(strArr, z, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void flushRecentTasks() {
            ActivityTaskManagerService.this.mRecentTasks.flush();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void clearLockedTasks(String str) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.getLockTaskController().clearLockedTasks(str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void updateUserConfiguration() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Configuration configuration = new Configuration(ActivityTaskManagerService.this.getGlobalConfiguration());
                    int currentUserId = ActivityTaskManagerService.this.mAmInternal.getCurrentUserId();
                    Settings.System.adjustConfigurationForUser(ActivityTaskManagerService.this.mContext.getContentResolver(), configuration, currentUserId, Settings.System.canWrite(ActivityTaskManagerService.this.mContext));
                    ActivityTaskManagerService.this.updateConfigurationLocked(configuration, null, false, false, currentUserId, false);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean canShowErrorDialogs() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = false;
                    if (ActivityTaskManagerService.this.mShowDialogs && !ActivityTaskManagerService.this.mSleeping) {
                        ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                        if (!activityTaskManagerService.mShuttingDown && !activityTaskManagerService.mKeyguardController.isKeyguardOrAodShowing(0)) {
                            ActivityTaskManagerService activityTaskManagerService2 = ActivityTaskManagerService.this;
                            if (!activityTaskManagerService2.hasUserRestriction("no_system_error_dialogs", activityTaskManagerService2.mAmInternal.getCurrentUserId()) && (!UserManager.isDeviceInDemoMode(ActivityTaskManagerService.this.mContext) || !ActivityTaskManagerService.this.mAmInternal.getCurrentUser().isDemo())) {
                                z = true;
                            }
                        }
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setProfileApp(String str) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfileApp = str;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setProfileProc(WindowProcessController windowProcessController) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfileProc = windowProcessController;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setProfilerInfo(ProfilerInfo profilerInfo) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfilerInfo = profilerInfo;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityMetricsLaunchObserverRegistry getLaunchObserverRegistry() {
            ActivityMetricsLaunchObserverRegistry launchObserverRegistry;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    launchObserverRegistry = ActivityTaskManagerService.this.mTaskSupervisor.getActivityMetricsLogger().getLaunchObserverRegistry();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return launchObserverRegistry;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public IBinder getUriPermissionOwnerForActivity(IBinder iBinder) {
            Binder externalToken;
            ActivityTaskManagerService.enforceNotIsolatedCaller("getUriPermissionOwnerForActivity");
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                    externalToken = isInRootTaskLocked == null ? null : isInRootTaskLocked.getUriPermissionsLocked().getExternalToken();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return externalToken;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public TaskSnapshot getTaskSnapshotBlocking(int i, boolean z) {
            return ActivityTaskManagerService.this.getTaskSnapshot(i, z, false);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isUidForeground(int i) {
            return ActivityTaskManagerService.this.hasActiveVisibleWindow(i);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setDeviceOwnerUid(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.setDeviceOwnerUid(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void setCompanionAppUids(int i, Set<Integer> set) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompanionAppUidsMap.put(Integer.valueOf(i), set);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean isBaseOfLockedTask(String str) {
            boolean isBaseOfLockedTask;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    isBaseOfLockedTask = ActivityTaskManagerService.this.getLockTaskController().isBaseOfLockedTask(str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return isBaseOfLockedTask;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityTaskManagerInternal.PackageConfigurationUpdater createPackageConfigurationUpdater() {
            return new PackageConfigurationUpdaterImpl(Binder.getCallingPid(), ActivityTaskManagerService.this);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityTaskManagerInternal.PackageConfigurationUpdater createPackageConfigurationUpdater(String str, int i) {
            return new PackageConfigurationUpdaterImpl(str, i, ActivityTaskManagerService.this);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityTaskManagerInternal.PackageConfig getApplicationConfig(String str, int i) {
            return ActivityTaskManagerService.this.mPackageConfigPersister.findPackageConfiguration(str, i);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public boolean hasSystemAlertWindowPermission(int i, int i2, String str) {
            return ActivityTaskManagerService.this.hasSystemAlertWindowPermission(i, i2, str);
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void registerActivityStartInterceptor(int i, ActivityInterceptorCallback activityInterceptorCallback) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mActivityInterceptorCallbacks.contains(i)) {
                        throw new IllegalArgumentException("Duplicate id provided: " + i);
                    } else if (activityInterceptorCallback == null) {
                        throw new IllegalArgumentException("The passed ActivityInterceptorCallback can not be null");
                    } else {
                        if (!ActivityInterceptorCallback.isValidOrderId(i)) {
                            throw new IllegalArgumentException("Provided id " + i + " is not in range of valid ids for system services [0,4] nor in range of valid ids for mainline module services [1000,1001]");
                        }
                        ActivityTaskManagerService.this.mActivityInterceptorCallbacks.put(i, activityInterceptorCallback);
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void unregisterActivityStartInterceptor(int i) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!ActivityTaskManagerService.this.mActivityInterceptorCallbacks.contains(i)) {
                        throw new IllegalArgumentException("ActivityInterceptorCallback with id (" + i + ") is not registered");
                    }
                    ActivityTaskManagerService.this.mActivityInterceptorCallbacks.remove(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public ActivityManager.RecentTaskInfo getMostRecentTaskFromBackground() {
            List<ActivityManager.RunningTaskInfo> tasks = ActivityTaskManagerService.this.getTasks(1);
            if (tasks.size() > 0) {
                ActivityManager.RunningTaskInfo runningTaskInfo = tasks.get(0);
                ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
                for (ActivityManager.RecentTaskInfo recentTaskInfo : activityTaskManagerService.getRecentTasks(2, 2, activityTaskManagerService.mContext.getUserId()).getList()) {
                    if (recentTaskInfo.id != runningTaskInfo.id) {
                        return recentTaskInfo;
                    }
                }
                return null;
            }
            Slog.i("ActivityTaskManager", "No running task found!");
            return null;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public List<ActivityManager.AppTask> getAppTasks(String str, int i) {
            ArrayList arrayList = new ArrayList();
            List appTasks = ActivityTaskManagerService.this.getAppTasks(str, i);
            int size = appTasks.size();
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(new ActivityManager.AppTask(IAppTask.Stub.asInterface((IBinder) appTasks.get(i2))));
            }
            return arrayList;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public int getTaskToShowPermissionDialogOn(String str, int i) {
            int taskToShowPermissionDialogOn;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    taskToShowPermissionDialogOn = ActivityTaskManagerService.this.mRootWindowContainer.getTaskToShowPermissionDialogOn(str, i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return taskToShowPermissionDialogOn;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void restartTaskActivityProcessIfVisible(int i, final String str) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Task anyTaskForId = ActivityTaskManagerService.this.mRootWindowContainer.anyTaskForId(i, 0);
                    if (anyTaskForId == null) {
                        Slog.w("ActivityTaskManager", "Failed to restart Activity. No task found for id: " + i);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    ActivityRecord activity = anyTaskForId.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityTaskManagerService$LocalService$$ExternalSyntheticLambda1
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$restartTaskActivityProcessIfVisible$4;
                            lambda$restartTaskActivityProcessIfVisible$4 = ActivityTaskManagerService.LocalService.lambda$restartTaskActivityProcessIfVisible$4(str, (ActivityRecord) obj);
                            return lambda$restartTaskActivityProcessIfVisible$4;
                        }
                    });
                    if (activity == null) {
                        Slog.w("ActivityTaskManager", "Failed to restart Activity. No Activity found for package name: " + str + " in task: " + i);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    activity.restartProcessIfVisible();
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        public static /* synthetic */ boolean lambda$restartTaskActivityProcessIfVisible$4(String str, ActivityRecord activityRecord) {
            return str.equals(activityRecord.packageName) && !activityRecord.finishing;
        }

        @Override // com.android.server.p014wm.ActivityTaskManagerInternal
        public void registerTaskStackListener(ITaskStackListener iTaskStackListener) {
            ActivityTaskManagerService.this.registerTaskStackListener(iTaskStackListener);
        }
    }
}
