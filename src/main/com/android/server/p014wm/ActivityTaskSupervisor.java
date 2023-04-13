package com.android.server.p014wm;

import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.app.BackgroundStartPrivileges;
import android.app.ProfilerInfo;
import android.app.ResultInfo;
import android.app.TaskInfo;
import android.app.WaitResult;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.LaunchActivityItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.ResumeActivityItem;
import android.companion.virtual.VirtualDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.SensorPrivacyManagerInternal;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.MergedConfiguration;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.p006am.UserState;
import com.android.server.p011pm.PackageManagerServiceUtils;
import com.android.server.p014wm.ActivityMetricsLogger;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.RecentTasks;
import com.android.server.utils.Slogf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.ActivityTaskSupervisor */
/* loaded from: classes2.dex */
public class ActivityTaskSupervisor implements RecentTasks.Callbacks {
    public static final ArrayMap<String, String> ACTION_TO_RUNTIME_PERMISSION;
    public static final int IDLE_TIMEOUT;
    public static final int LAUNCH_TIMEOUT;
    public static final int SLEEP_TIMEOUT;
    public ActivityMetricsLogger mActivityMetricsLogger;
    public AppOpsManager mAppOpsManager;
    public boolean mAppVisibilitiesChangedSinceLastPause;
    public int mDeferResumeCount;
    public boolean mDeferRootVisibilityUpdate;
    public PowerManager.WakeLock mGoingToSleepWakeLock;
    public final ActivityTaskSupervisorHandler mHandler;
    public boolean mInitialized;
    public KeyguardController mKeyguardController;
    public LaunchParamsController mLaunchParamsController;
    public LaunchParamsPersister mLaunchParamsPersister;
    public PowerManager.WakeLock mLaunchingActivityWakeLock;
    public final Looper mLooper;
    public PersisterQueue mPersisterQueue;
    public Rect mPipModeChangedTargetRootTaskBounds;
    public PowerManager mPowerManager;
    public RecentTasks mRecentTasks;
    public RootWindowContainer mRootWindowContainer;
    public RunningTasks mRunningTasks;
    public final ActivityTaskManagerService mService;
    public ComponentName mSystemChooserActivity;
    public ActivityRecord mTopResumedActivity;
    public boolean mTopResumedActivityWaitingForPrev;
    public VirtualDeviceManager mVirtualDeviceManager;
    public int mVisibilityTransactionDepth;
    public WindowManagerService mWindowManager;
    public final TaskInfoHelper mTaskInfoHelper = new TaskInfoHelper();
    public final ArrayList<WindowProcessController> mActivityStateChangedProcs = new ArrayList<>();
    public final SparseIntArray mCurTaskIdForUser = new SparseIntArray(20);
    public final ArrayList<WaitInfo> mWaitingActivityLaunched = new ArrayList<>();
    public final ArrayList<ActivityRecord> mStoppingActivities = new ArrayList<>();
    public final ArrayList<ActivityRecord> mFinishingActivities = new ArrayList<>();
    public final ArrayList<ActivityRecord> mNoHistoryActivities = new ArrayList<>();
    public final ArrayList<ActivityRecord> mMultiWindowModeChangedActivities = new ArrayList<>();
    public final ArrayList<ActivityRecord> mPipModeChangedActivities = new ArrayList<>();
    public final ArrayList<ActivityRecord> mNoAnimActivities = new ArrayList<>();
    public final ArrayList<UserState> mStartingUsers = new ArrayList<>();
    public boolean mUserLeaving = false;

    public static int nextTaskIdForUser(int i, int i2) {
        int i3 = i + 1;
        return i3 == (i2 + 1) * 100000 ? i3 - 100000 : i3;
    }

    static {
        int i = Build.HW_TIMEOUT_MULTIPLIER;
        IDLE_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        SLEEP_TIMEOUT = i * 5000;
        LAUNCH_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        ACTION_TO_RUNTIME_PERMISSION = arrayMap;
        arrayMap.put("android.media.action.IMAGE_CAPTURE", "android.permission.CAMERA");
        arrayMap.put("android.media.action.VIDEO_CAPTURE", "android.permission.CAMERA");
        arrayMap.put("android.intent.action.CALL", "android.permission.CALL_PHONE");
    }

    public boolean canPlaceEntityOnDisplay(int i, int i2, int i3, ActivityInfo activityInfo) {
        return canPlaceEntityOnDisplay(i, i2, i3, null, activityInfo);
    }

    public boolean canPlaceEntityOnDisplay(int i, int i2, int i3, Task task) {
        return canPlaceEntityOnDisplay(i, i2, i3, task, null);
    }

    public final boolean canPlaceEntityOnDisplay(int i, int i2, int i3, Task task, ActivityInfo activityInfo) {
        if (i == 0) {
            return true;
        }
        if (this.mService.mSupportsMultiDisplay && isCallerAllowedToLaunchOnDisplay(i2, i3, i, activityInfo)) {
            DisplayContent displayContentOrCreate = this.mRootWindowContainer.getDisplayContentOrCreate(i);
            if (displayContentOrCreate != null) {
                final ArrayList arrayList = new ArrayList();
                if (activityInfo != null) {
                    arrayList.add(activityInfo);
                }
                if (task != null) {
                    task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ActivityTaskSupervisor.lambda$canPlaceEntityOnDisplay$0(arrayList, (ActivityRecord) obj);
                        }
                    });
                }
                return displayContentOrCreate.mDwpcHelper.canContainActivities(arrayList, displayContentOrCreate.getWindowingMode());
            }
            return true;
        }
        return false;
    }

    public static /* synthetic */ void lambda$canPlaceEntityOnDisplay$0(ArrayList arrayList, ActivityRecord activityRecord) {
        arrayList.add(activityRecord.info);
    }

    public ActivityTaskSupervisor(ActivityTaskManagerService activityTaskManagerService, Looper looper) {
        this.mService = activityTaskManagerService;
        this.mLooper = looper;
        this.mHandler = new ActivityTaskSupervisorHandler(looper);
    }

    public void initialize() {
        if (this.mInitialized) {
            return;
        }
        this.mInitialized = true;
        setRunningTasks(new RunningTasks());
        this.mActivityMetricsLogger = new ActivityMetricsLogger(this, this.mHandler.getLooper());
        this.mKeyguardController = new KeyguardController(this.mService, this);
        PersisterQueue persisterQueue = new PersisterQueue();
        this.mPersisterQueue = persisterQueue;
        LaunchParamsPersister launchParamsPersister = new LaunchParamsPersister(persisterQueue, this);
        this.mLaunchParamsPersister = launchParamsPersister;
        LaunchParamsController launchParamsController = new LaunchParamsController(this.mService, launchParamsPersister);
        this.mLaunchParamsController = launchParamsController;
        launchParamsController.registerDefaultModifiers(this);
    }

    public void onSystemReady() {
        this.mLaunchParamsPersister.onSystemReady();
    }

    public void onUserUnlocked(int i) {
        this.mPersisterQueue.startPersisting();
        this.mLaunchParamsPersister.onUnlockUser(i);
        scheduleStartHome("userUnlocked");
    }

    public ActivityMetricsLogger getActivityMetricsLogger() {
        return this.mActivityMetricsLogger;
    }

    public KeyguardController getKeyguardController() {
        return this.mKeyguardController;
    }

    public ComponentName getSystemChooserActivity() {
        if (this.mSystemChooserActivity == null) {
            this.mSystemChooserActivity = ComponentName.unflattenFromString(this.mService.mContext.getResources().getString(17039851));
        }
        return this.mSystemChooserActivity;
    }

    public void setRecentTasks(RecentTasks recentTasks) {
        RecentTasks recentTasks2 = this.mRecentTasks;
        if (recentTasks2 != null) {
            recentTasks2.unregisterCallback(this);
        }
        this.mRecentTasks = recentTasks;
        recentTasks.registerCallback(this);
    }

    @VisibleForTesting
    public void setRunningTasks(RunningTasks runningTasks) {
        this.mRunningTasks = runningTasks;
    }

    public RunningTasks getRunningTasks() {
        return this.mRunningTasks;
    }

    public void initPowerManagement() {
        PowerManager powerManager = (PowerManager) this.mService.mContext.getSystemService(PowerManager.class);
        this.mPowerManager = powerManager;
        this.mGoingToSleepWakeLock = powerManager.newWakeLock(1, "ActivityManager-Sleep");
        PowerManager.WakeLock newWakeLock = this.mPowerManager.newWakeLock(1, "*launch*");
        this.mLaunchingActivityWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mWindowManager = windowManagerService;
        getKeyguardController().setWindowManager(windowManagerService);
    }

    public void setNextTaskIdForUser(int i, int i2) {
        if (i > this.mCurTaskIdForUser.get(i2, -1)) {
            this.mCurTaskIdForUser.put(i2, i);
        }
    }

    public void finishNoHistoryActivitiesIfNeeded(ActivityRecord activityRecord) {
        for (int size = this.mNoHistoryActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord2 = this.mNoHistoryActivities.get(size);
            if (!activityRecord2.finishing && activityRecord2 != activityRecord && activityRecord.occludesParent() && activityRecord2.getDisplayId() == activityRecord.getDisplayId()) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -484194149, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                }
                activityRecord2.finishIfPossible("resume-no-history", false);
                this.mNoHistoryActivities.remove(activityRecord2);
            }
        }
    }

    public int getNextTaskIdForUser() {
        return getNextTaskIdForUser(this.mRootWindowContainer.mCurrentUser);
    }

    public int getNextTaskIdForUser(int i) {
        int i2 = this.mCurTaskIdForUser.get(i, 100000 * i);
        int nextTaskIdForUser = nextTaskIdForUser(i2, i);
        do {
            if (this.mRecentTasks.containsTaskId(nextTaskIdForUser, i) || this.mRootWindowContainer.anyTaskForId(nextTaskIdForUser, 1) != null) {
                nextTaskIdForUser = nextTaskIdForUser(nextTaskIdForUser, i);
            } else {
                this.mCurTaskIdForUser.put(i, nextTaskIdForUser);
                return nextTaskIdForUser;
            }
        } while (nextTaskIdForUser != i2);
        throw new IllegalStateException("Cannot get an available task id. Reached limit of 100000 running tasks per user.");
    }

    public void waitActivityVisibleOrLaunched(WaitResult waitResult, ActivityRecord activityRecord, ActivityMetricsLogger.LaunchingState launchingState) {
        int i = waitResult.result;
        if (i == 2 || i == 0) {
            WaitInfo waitInfo = new WaitInfo(waitResult, activityRecord.mActivityComponent, launchingState);
            this.mWaitingActivityLaunched.add(waitInfo);
            do {
                try {
                    this.mService.mGlobalLock.wait();
                } catch (InterruptedException unused) {
                }
            } while (this.mWaitingActivityLaunched.contains(waitInfo));
        }
    }

    public void cleanupActivity(ActivityRecord activityRecord) {
        this.mFinishingActivities.remove(activityRecord);
        stopWaitingForActivityVisible(activityRecord);
    }

    public void stopWaitingForActivityVisible(ActivityRecord activityRecord) {
        reportActivityLaunched(false, activityRecord, -1L, 0);
    }

    public void reportActivityLaunched(boolean z, ActivityRecord activityRecord, long j, int i) {
        boolean z2 = false;
        for (int size = this.mWaitingActivityLaunched.size() - 1; size >= 0; size--) {
            WaitInfo waitInfo = this.mWaitingActivityLaunched.get(size);
            if (waitInfo.matches(activityRecord)) {
                WaitResult waitResult = waitInfo.mResult;
                waitResult.timeout = z;
                waitResult.who = activityRecord.mActivityComponent;
                waitResult.totalTime = j;
                waitResult.launchState = i;
                this.mWaitingActivityLaunched.remove(size);
                z2 = true;
            }
        }
        if (z2) {
            this.mService.mGlobalLock.notifyAll();
        }
    }

    public void reportWaitingActivityLaunchedIfNeeded(ActivityRecord activityRecord, int i) {
        if (this.mWaitingActivityLaunched.isEmpty()) {
            return;
        }
        if (i == 3 || i == 2) {
            boolean z = false;
            for (int size = this.mWaitingActivityLaunched.size() - 1; size >= 0; size--) {
                WaitInfo waitInfo = this.mWaitingActivityLaunched.get(size);
                if (waitInfo.matches(activityRecord)) {
                    WaitResult waitResult = waitInfo.mResult;
                    waitResult.result = i;
                    if (i == 3) {
                        waitResult.who = activityRecord.mActivityComponent;
                        this.mWaitingActivityLaunched.remove(size);
                        z = true;
                    }
                }
            }
            if (z) {
                this.mService.mGlobalLock.notifyAll();
            }
        }
    }

    public ActivityInfo resolveActivity(Intent intent, ResolveInfo resolveInfo, final int i, final ProfilerInfo profilerInfo) {
        final ActivityInfo activityInfo = resolveInfo != null ? resolveInfo.activityInfo : null;
        if (activityInfo != null) {
            intent.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
            boolean z = (i & 14) != 0;
            boolean z2 = profilerInfo != null;
            if (z || z2) {
                boolean z3 = (Build.IS_DEBUGGABLE || (activityInfo.applicationInfo.flags & 2) != 0) && !activityInfo.processName.equals("system");
                if ((z && !z3) || (z2 && !z3 && !activityInfo.applicationInfo.isProfileableByShell())) {
                    Slog.w("ActivityTaskManager", "Ignore debugging for non-debuggable app: " + activityInfo.packageName);
                } else {
                    synchronized (this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            this.mService.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ActivityTaskSupervisor.this.lambda$resolveActivity$1(activityInfo, i, profilerInfo);
                                }
                            });
                            try {
                                this.mService.mGlobalLock.wait();
                            } catch (InterruptedException unused) {
                            }
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
            String launchToken = intent.getLaunchToken();
            if (activityInfo.launchToken == null && launchToken != null) {
                activityInfo.launchToken = launchToken;
            }
        }
        return activityInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resolveActivity$1(ActivityInfo activityInfo, int i, ProfilerInfo profilerInfo) {
        try {
            ActivityTaskManagerService activityTaskManagerService = this.mService;
            activityTaskManagerService.mAmInternal.setDebugFlagsForStartingActivity(activityInfo, i, profilerInfo, activityTaskManagerService.mGlobalLock);
        } finally {
        }
    }

    public ResolveInfo resolveIntent(Intent intent, String str, int i, int i2, int i3, int i4) {
        try {
            Trace.traceBegin(32L, "resolveIntent");
            int i5 = i2 | 65536 | 1024;
            if (intent.isWebIntent() || (intent.getFlags() & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
                i5 |= 8388608;
            }
            int i6 = (!intent.isWebIntent() || (intent.getFlags() & 1024) == 0) ? 0 : 1;
            if ((intent.getFlags() & 512) != 0) {
                i6 |= 2;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            ResolveInfo resolveIntentExported = this.mService.getPackageManagerInternalLocked().resolveIntentExported(intent, str, i5, i6, i, true, i3, i4);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return resolveIntentExported;
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public ActivityInfo resolveActivity(Intent intent, String str, int i, ProfilerInfo profilerInfo, int i2, int i3, int i4) {
        return resolveActivity(intent, resolveIntent(intent, str, i2, 0, i3, i4), i, profilerInfo);
    }

    public boolean realStartActivityLocked(ActivityRecord activityRecord, WindowProcessController windowProcessController, boolean z, boolean z2) throws RemoteException {
        ArrayList<ResultInfo> arrayList;
        ArrayList<ReferrerIntent> arrayList2;
        ResumeActivityItem obtain;
        if (!this.mRootWindowContainer.allPausedActivitiesComplete()) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -641258376, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
            }
            return false;
        }
        Task task = activityRecord.getTask();
        Task rootTask = task.getRootTask();
        beginDeferResume();
        windowProcessController.pauseConfigurationDispatch();
        try {
            activityRecord.startFreezingScreenLocked(windowProcessController, 0);
            activityRecord.startLaunchTickingLocked();
            activityRecord.lastLaunchTime = SystemClock.uptimeMillis();
            activityRecord.setProcess(windowProcessController);
            boolean z3 = (!z || activityRecord.canResumeByCompat()) ? z : false;
            activityRecord.notifyUnknownVisibilityLaunchedForKeyguardTransition();
            if (z2) {
                this.mRootWindowContainer.ensureVisibilityAndConfig(activityRecord, activityRecord.getDisplayId(), false, true);
            }
            if (this.mKeyguardController.checkKeyguardVisibility(activityRecord) && activityRecord.allowMoveToFront()) {
                activityRecord.setVisibility(true);
            }
            ApplicationInfo applicationInfo = activityRecord.info.applicationInfo;
            int i = applicationInfo != null ? applicationInfo.uid : -1;
            if (activityRecord.mUserId != windowProcessController.mUserId || applicationInfo.uid != i) {
                Slog.wtf("ActivityTaskManager", "User ID for activity changing for " + activityRecord + " appInfo.uid=" + activityRecord.info.applicationInfo.uid + " info.ai.uid=" + i + " old=" + activityRecord.app + " new=" + windowProcessController);
            }
            ActivityClientController activityClientController = windowProcessController.hasEverLaunchedActivity() ? null : this.mService.mActivityClientController;
            activityRecord.launchCount++;
            LockTaskController lockTaskController = this.mService.getLockTaskController();
            int i2 = task.mLockTaskAuth;
            if (i2 == 2 || i2 == 4 || (i2 == 3 && lockTaskController.getLockTaskModeState() == 1)) {
                lockTaskController.startLockTaskMode(task, false, 0);
            }
            try {
                if (!windowProcessController.hasThread()) {
                    throw new RemoteException();
                }
                if (z3) {
                    arrayList = activityRecord.results;
                    arrayList2 = activityRecord.newIntents;
                } else {
                    arrayList = null;
                    arrayList2 = null;
                }
                EventLogTags.writeWmRestartActivity(activityRecord.mUserId, System.identityHashCode(activityRecord), task.mTaskId, activityRecord.shortComponentName);
                if (activityRecord.isActivityTypeHome()) {
                    updateHomeProcess(task.getBottomMostActivity().app);
                }
                this.mService.getPackageManagerInternalLocked().notifyPackageUse(activityRecord.intent.getComponent().getPackageName(), 0);
                activityRecord.forceNewConfig = false;
                this.mService.getAppWarningsLocked().onStartActivity(activityRecord);
                Configuration prepareConfigurationForLaunchingActivity = windowProcessController.prepareConfigurationForLaunchingActivity();
                MergedConfiguration mergedConfiguration = new MergedConfiguration(prepareConfigurationForLaunchingActivity, activityRecord.getMergedOverrideConfiguration());
                activityRecord.setLastReportedConfiguration(mergedConfiguration);
                logIfTransactionTooLarge(activityRecord.intent, activityRecord.getSavedState());
                TaskFragment organizedTaskFragment = activityRecord.getOrganizedTaskFragment();
                if (organizedTaskFragment != null) {
                    this.mService.mTaskFragmentOrganizerController.dispatchPendingInfoChangedEvent(organizedTaskFragment);
                }
                ClientTransaction obtain2 = ClientTransaction.obtain(windowProcessController.getThread(), activityRecord.token);
                boolean isTransitionForward = activityRecord.isTransitionForward();
                obtain2.addCallback(LaunchActivityItem.obtain(new Intent(activityRecord.intent), System.identityHashCode(activityRecord), activityRecord.info, mergedConfiguration.getGlobalConfiguration(), mergedConfiguration.getOverrideConfiguration(), getDeviceIdForDisplayId(activityRecord.getDisplayId()), activityRecord.getFilteredReferrer(activityRecord.launchedFromPackage), task.voiceInteractor, windowProcessController.getReportedProcState(), activityRecord.getSavedState(), activityRecord.getPersistentSavedState(), arrayList, arrayList2, activityRecord.takeOptions(), isTransitionForward, windowProcessController.createProfilerInfoIfNeeded(), activityRecord.assistToken, activityClientController, activityRecord.shareableActivityToken, activityRecord.getLaunchedFromBubble(), activityRecord.getTaskFragment().getFragmentToken()));
                if (z3) {
                    obtain = ResumeActivityItem.obtain(isTransitionForward, activityRecord.shouldSendCompatFakeFocus());
                } else {
                    obtain = PauseActivityItem.obtain();
                }
                obtain2.setLifecycleStateRequest(obtain);
                this.mService.getLifecycleManager().scheduleTransaction(obtain2);
                if (prepareConfigurationForLaunchingActivity.seq > this.mRootWindowContainer.getConfiguration().seq) {
                    windowProcessController.setLastReportedConfiguration(prepareConfigurationForLaunchingActivity);
                }
                ApplicationInfo applicationInfo2 = windowProcessController.mInfo;
                if ((applicationInfo2.privateFlags & 2) != 0 && this.mService.mHasHeavyWeightFeature && windowProcessController.mName.equals(applicationInfo2.packageName)) {
                    if (this.mService.mHeavyWeightProcess != null && this.mService.mHeavyWeightProcess != windowProcessController) {
                        Slog.w("ActivityTaskManager", "Starting new heavy weight process " + windowProcessController + " when already running " + this.mService.mHeavyWeightProcess);
                    }
                    this.mService.setHeavyWeightProcess(activityRecord);
                }
                endDeferResume();
                windowProcessController.resumeConfigurationDispatch();
                activityRecord.launchFailed = false;
                if (z3 && readyToResume()) {
                    rootTask.minimalResumeActivityLocked(activityRecord);
                } else {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1468740466, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
                    }
                    activityRecord.setState(ActivityRecord.State.PAUSED, "realStartActivityLocked");
                    this.mRootWindowContainer.executeAppTransitionForAllDisplay();
                }
                windowProcessController.onStartActivity(this.mService.mTopProcessState, activityRecord.info);
                if (this.mRootWindowContainer.isTopDisplayFocusedRootTask(rootTask)) {
                    this.mService.getActivityStartController().startSetupActivity();
                }
                WindowProcessController windowProcessController2 = activityRecord.app;
                if (windowProcessController2 != null) {
                    windowProcessController2.updateServiceConnectionActivities();
                    return true;
                }
                return true;
            } catch (RemoteException e) {
                if (activityRecord.launchFailed) {
                    Slog.e("ActivityTaskManager", "Second failure launching " + activityRecord.intent.getComponent().flattenToShortString() + ", giving up", e);
                    windowProcessController.appDied("2nd-crash");
                    activityRecord.finishIfPossible("2nd-crash", false);
                    endDeferResume();
                    windowProcessController.resumeConfigurationDispatch();
                    return false;
                }
                activityRecord.launchFailed = true;
                activityRecord.detachFromProcess();
                throw e;
            }
        } catch (Throwable th) {
            endDeferResume();
            windowProcessController.resumeConfigurationDispatch();
            throw th;
        }
    }

    public void updateHomeProcess(WindowProcessController windowProcessController) {
        if (windowProcessController == null || this.mService.mHomeProcess == windowProcessController) {
            return;
        }
        scheduleStartHome("homeChanged");
        this.mService.mHomeProcess = windowProcessController;
    }

    public final void scheduleStartHome(String str) {
        if (this.mHandler.hasMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_MTE_POLICY)) {
            return;
        }
        this.mHandler.obtainMessage(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_MTE_POLICY, str).sendToTarget();
    }

    public final void logIfTransactionTooLarge(Intent intent, Bundle bundle) {
        Bundle extras;
        int size = (intent == null || (extras = intent.getExtras()) == null) ? 0 : extras.getSize();
        int size2 = bundle != null ? bundle.getSize() : 0;
        if (size + size2 > 200000) {
            Slog.e("ActivityTaskManager", "Transaction too large, intent: " + intent + ", extras size: " + size + ", icicle size: " + size2);
        }
    }

    public void startSpecificActivity(ActivityRecord activityRecord, boolean z, boolean z2) {
        boolean z3;
        WindowProcessController processController = this.mService.getProcessController(activityRecord.processName, activityRecord.info.applicationInfo.uid);
        boolean z4 = true;
        if (processController == null || !processController.hasThread()) {
            z3 = false;
        } else {
            try {
                realStartActivityLocked(activityRecord, processController, z, z2);
                return;
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception when starting activity " + activityRecord.intent.getComponent().flattenToShortString(), e);
                this.mService.mProcessNames.remove(processController.mName, processController.mUid);
                this.mService.mProcessMap.remove(processController.getPid());
                z3 = true;
            }
        }
        activityRecord.notifyUnknownVisibilityLaunchedForKeyguardTransition();
        if (!z || !activityRecord.isTopRunningActivity()) {
            z4 = false;
        }
        this.mService.startProcessAsync(activityRecord, z3, z4, z4 ? "top-activity" : "activity");
    }

    public boolean checkStartAnyActivityPermission(Intent intent, ActivityInfo activityInfo, String str, int i, int i2, int i3, String str2, String str3, boolean z, boolean z2, WindowProcessController windowProcessController, ActivityRecord activityRecord, Task task) {
        String str4;
        boolean z3 = this.mService.getRecentTasks() != null && this.mService.getRecentTasks().isCallerRecents(i3);
        if (ActivityTaskManagerService.checkPermission("android.permission.START_ANY_ACTIVITY", i2, i3) == 0 || (z3 && z2)) {
            return true;
        }
        int componentRestrictionForCallingPackage = getComponentRestrictionForCallingPackage(activityInfo, str2, str3, i2, i3, z);
        int actionRestrictionForCallingPackage = getActionRestrictionForCallingPackage(intent.getAction(), str2, str3, i2, i3);
        if (componentRestrictionForCallingPackage != 1 && actionRestrictionForCallingPackage != 1) {
            if (actionRestrictionForCallingPackage == 2) {
                Slog.w("ActivityTaskManager", "Appop Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i2 + ", uid=" + i3 + ") requires " + AppOpsManager.permissionToOp(ACTION_TO_RUNTIME_PERMISSION.get(intent.getAction())));
                return false;
            } else if (componentRestrictionForCallingPackage == 2) {
                Slog.w("ActivityTaskManager", "Appop Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i2 + ", uid=" + i3 + ") requires appop " + AppOpsManager.permissionToOp(activityInfo.permission));
                return false;
            } else {
                return true;
            }
        }
        if (activityRecord != null) {
            activityRecord.sendResult(-1, str, i, 0, null, null);
        }
        if (actionRestrictionForCallingPackage == 1) {
            str4 = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i2 + ", uid=" + i3 + ") with revoked permission " + ACTION_TO_RUNTIME_PERMISSION.get(intent.getAction());
        } else if (!activityInfo.exported) {
            str4 = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i2 + ", uid=" + i3 + ") not exported from uid " + activityInfo.applicationInfo.uid;
        } else {
            str4 = "Permission Denial: starting " + intent.toString() + " from " + windowProcessController + " (pid=" + i2 + ", uid=" + i3 + ") requires " + activityInfo.permission;
        }
        Slog.w("ActivityTaskManager", str4);
        throw new SecurityException(str4);
    }

    public boolean isCallerAllowedToLaunchOnTaskDisplayArea(int i, int i2, TaskDisplayArea taskDisplayArea, ActivityInfo activityInfo) {
        return isCallerAllowedToLaunchOnDisplay(i, i2, taskDisplayArea != null ? taskDisplayArea.getDisplayId() : 0, activityInfo);
    }

    public boolean isCallerAllowedToLaunchOnDisplay(int i, int i2, int i3, ActivityInfo activityInfo) {
        if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1228653755, 21, (String) null, new Object[]{Long.valueOf(i3), Long.valueOf(i), Long.valueOf(i2)});
        }
        if (i == -1 && i2 == -1) {
            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 1524174282, 0, (String) null, (Object[]) null);
            }
            return true;
        }
        DisplayContent displayContentOrCreate = this.mRootWindowContainer.getDisplayContentOrCreate(i3);
        if (displayContentOrCreate == null || displayContentOrCreate.isRemoved()) {
            Slog.w("ActivityTaskManager", "Launch on display check: display not found");
            return false;
        } else if ((displayContentOrCreate.mDisplay.getFlags() & IInstalld.FLAG_FORCE) != 0) {
            Slog.w("ActivityTaskManager", "Launch on display check: activity launch is not allowed on rear display");
            return false;
        } else if (ActivityTaskManagerService.checkPermission("android.permission.INTERNAL_SYSTEM_WINDOW", i, i2) == 0) {
            if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 431715812, 0, (String) null, (Object[]) null);
            }
            return true;
        } else {
            boolean isUidPresent = displayContentOrCreate.isUidPresent(i2);
            Display display = displayContentOrCreate.mDisplay;
            if (!display.isTrusted()) {
                if ((activityInfo.flags & Integer.MIN_VALUE) == 0) {
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1474602871, 0, (String) null, (Object[]) null);
                    }
                    return false;
                } else if (ActivityTaskManagerService.checkPermission("android.permission.ACTIVITY_EMBEDDING", i, i2) == -1 && !isUidPresent) {
                    if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, 979347997, 0, (String) null, (Object[]) null);
                    }
                    return false;
                }
            }
            if (!displayContentOrCreate.isPrivate()) {
                int userId = UserHandle.getUserId(i2);
                int displayId = display.getDisplayId();
                boolean isUserVisible = this.mWindowManager.mUmInternal.isUserVisible(userId, displayId);
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1253056469, 20, (String) null, new Object[]{isUserVisible ? "allow" : "disallow", Long.valueOf(userId), Long.valueOf(displayId)});
                }
                return isUserVisible;
            } else if (display.getOwnerUid() == i2) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -856750101, 0, (String) null, (Object[]) null);
                }
                return true;
            } else if (isUidPresent) {
                if (ProtoLogCache.WM_DEBUG_TASKS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_TASKS, -1979455254, 0, (String) null, (Object[]) null);
                }
                return true;
            } else {
                Slog.w("ActivityTaskManager", "Launch on display check: denied");
                return false;
            }
        }
    }

    public UserInfo getUserInfo(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return UserManager.get(this.mService.mContext).getUserInfo(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getDeviceIdForDisplayId(int i) {
        if (i == 0 || i == -1) {
            return 0;
        }
        if (this.mVirtualDeviceManager == null) {
            ActivityTaskManagerService activityTaskManagerService = this.mService;
            if (activityTaskManagerService.mHasCompanionDeviceSetupFeature) {
                this.mVirtualDeviceManager = (VirtualDeviceManager) activityTaskManagerService.mContext.getSystemService(VirtualDeviceManager.class);
            }
            if (this.mVirtualDeviceManager == null) {
                return 0;
            }
        }
        return this.mVirtualDeviceManager.getDeviceIdForDisplayId(i);
    }

    public final AppOpsManager getAppOpsManager() {
        if (this.mAppOpsManager == null) {
            this.mAppOpsManager = (AppOpsManager) this.mService.mContext.getSystemService(AppOpsManager.class);
        }
        return this.mAppOpsManager;
    }

    public final int getComponentRestrictionForCallingPackage(ActivityInfo activityInfo, String str, String str2, int i, int i2, boolean z) {
        int permissionToOpCode;
        if (z || ActivityTaskManagerService.checkComponentPermission(activityInfo.permission, i, i2, activityInfo.applicationInfo.uid, activityInfo.exported) != -1) {
            String str3 = activityInfo.permission;
            return (str3 == null || (permissionToOpCode = AppOpsManager.permissionToOpCode(str3)) == -1 || getAppOpsManager().noteOpNoThrow(permissionToOpCode, i2, str, str2, "") == 0 || z) ? 0 : 2;
        }
        return 1;
    }

    public final int getActionRestrictionForCallingPackage(String str, String str2, String str3, int i, int i2) {
        String str4;
        if (str == null || (str4 = ACTION_TO_RUNTIME_PERMISSION.get(str)) == null) {
            return 0;
        }
        try {
            if (ArrayUtils.contains(this.mService.mContext.getPackageManager().getPackageInfoAsUser(str2, IInstalld.FLAG_USE_QUOTA, UserHandle.getUserId(i2)).requestedPermissions, str4)) {
                if (ActivityTaskManagerService.checkPermission(str4, i, i2) == -1) {
                    return 1;
                }
                int permissionToOpCode = AppOpsManager.permissionToOpCode(str4);
                if (permissionToOpCode == -1 || getAppOpsManager().noteOpNoThrow(permissionToOpCode, i2, str2, str3, "") == 0) {
                    return 0;
                }
                if ("android.permission.CAMERA".equals(str4)) {
                    UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i2);
                    if (((SensorPrivacyManagerInternal) LocalServices.getService(SensorPrivacyManagerInternal.class)).isSensorPrivacyEnabled(userHandleForUid.getIdentifier(), 2) && ((AppOpsManagerInternal) LocalServices.getService(AppOpsManagerInternal.class)).getOpRestrictionCount(26, userHandleForUid, str2, (String) null) == 1) {
                        return 0;
                    }
                }
                return 2;
            }
            return 0;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.i("ActivityTaskManager", "Cannot find package info for " + str2);
            return 0;
        }
    }

    public void setLaunchSource(int i) {
        this.mLaunchingActivityWakeLock.setWorkSource(new WorkSource(i));
    }

    public void acquireLaunchWakelock() {
        this.mLaunchingActivityWakeLock.acquire();
        if (this.mHandler.hasMessages(204)) {
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(204, LAUNCH_TIMEOUT);
    }

    @GuardedBy({"mService"})
    public final void checkFinishBootingLocked() {
        boolean isBooting = this.mService.isBooting();
        boolean z = false;
        this.mService.setBooting(false);
        if (!this.mService.isBooted()) {
            z = true;
            this.mService.setBooted(true);
        }
        if (isBooting || z) {
            this.mService.postFinishBooting(isBooting, z);
        }
    }

    public void activityIdleInternal(ActivityRecord activityRecord, boolean z, boolean z2, Configuration configuration) {
        if (activityRecord != null) {
            this.mHandler.removeMessages(200, activityRecord);
            activityRecord.finishLaunchTickingLocked();
            if (z) {
                reportActivityLaunched(z, activityRecord, -1L, -1);
            }
            if (configuration != null) {
                activityRecord.setLastReportedGlobalConfiguration(configuration);
            }
            activityRecord.idle = true;
            if ((this.mService.isBooting() && this.mRootWindowContainer.allResumedActivitiesIdle()) || z) {
                checkFinishBootingLocked();
            }
            activityRecord.mRelaunchReason = 0;
        }
        if (this.mRootWindowContainer.allResumedActivitiesIdle()) {
            if (activityRecord != null) {
                this.mService.scheduleAppGcsLocked();
                this.mRecentTasks.onActivityIdle(activityRecord);
            }
            if (this.mLaunchingActivityWakeLock.isHeld()) {
                this.mHandler.removeMessages(204);
                this.mLaunchingActivityWakeLock.release();
            }
            this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
        }
        processStoppingAndFinishingActivities(activityRecord, z2, "idle");
        if (!this.mStartingUsers.isEmpty()) {
            ArrayList arrayList = new ArrayList(this.mStartingUsers);
            this.mStartingUsers.clear();
            for (int i = 0; i < arrayList.size(); i++) {
                UserState userState = (UserState) arrayList.get(i);
                Slogf.m20i("ActivityTaskManager", "finishing switch of user %d", Integer.valueOf(userState.mHandle.getIdentifier()));
                this.mService.mAmInternal.finishUserSwitch(userState);
            }
        }
        this.mService.f1161mH.post(new Runnable() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskSupervisor.this.lambda$activityIdleInternal$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$activityIdleInternal$2() {
        this.mService.mAmInternal.trimApplications();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00ac A[Catch: all -> 0x0105, TryCatch #1 {all -> 0x0105, blocks: (B:8:0x0030, B:9:0x0032, B:11:0x0040, B:14:0x0049, B:16:0x0052, B:18:0x0070, B:20:0x0076, B:22:0x007e, B:24:0x009a, B:27:0x00ac, B:28:0x00b3, B:31:0x00bc), top: B:56:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00ba  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00bc A[Catch: all -> 0x0105, TRY_LEAVE, TryCatch #1 {all -> 0x0105, blocks: (B:8:0x0030, B:9:0x0032, B:11:0x0040, B:14:0x0049, B:16:0x0052, B:18:0x0070, B:20:0x0076, B:22:0x007e, B:24:0x009a, B:27:0x00ac, B:28:0x00b3, B:31:0x00bc), top: B:56:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00ee A[Catch: all -> 0x0103, TryCatch #0 {all -> 0x0103, blocks: (B:34:0x00cb, B:37:0x00e3, B:39:0x00e9, B:41:0x00ee, B:43:0x00f2, B:45:0x00f8), top: B:54:0x00cb }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void findTaskToMoveToFront(Task task, int i, ActivityOptions activityOptions, String str, boolean z) {
        boolean z2;
        Transition transition;
        String str2;
        String str3;
        Task task2;
        boolean z3;
        ActivityRecord topNonFinishingActivity;
        Transition transition2;
        boolean z4;
        Task rootTask = task.getRootTask();
        if (rootTask == null) {
            Slog.e("ActivityTaskManager", "findTaskToMoveToFront: can't move task=" + task + " to front. Root task is null");
            return;
        }
        if ((i & 2) == 0) {
            try {
                this.mUserLeaving = true;
            } catch (Throwable th) {
                th = th;
                z2 = false;
                this.mUserLeaving = z2;
                this.mService.continueWindowLayout();
                throw th;
            }
        }
        this.mService.deferWindowLayout();
        try {
            if (task.mTransitionController.isShellTransitionsEnabled() && !task.mTransitionController.isCollecting()) {
                transition = task.mTransitionController.createTransition(3);
                task.mTransitionController.collect(task);
                str2 = str + " findTaskToMoveToFront";
                if (task.isResizeable() || !canUseActivityOptionsLaunchBounds(activityOptions)) {
                    str3 = str2;
                    task2 = rootTask;
                    z3 = false;
                } else {
                    Task orCreateRootTask = this.mRootWindowContainer.getOrCreateRootTask(null, activityOptions, task, true);
                    if (orCreateRootTask != rootTask) {
                        moveHomeRootTaskToFrontIfNeeded(i, orCreateRootTask.getDisplayArea(), str2);
                        task.reparent(orCreateRootTask, true, 1, false, true, str2);
                        str3 = str2;
                        z4 = true;
                        rootTask = orCreateRootTask;
                    } else {
                        str3 = str2;
                        z4 = false;
                    }
                    task.setBounds(activityOptions.getLaunchBounds());
                    boolean z5 = z4;
                    task2 = rootTask;
                    z3 = z5;
                }
                if (!z3) {
                    moveHomeRootTaskToFrontIfNeeded(i, task2.getDisplayArea(), str3);
                }
                topNonFinishingActivity = task.getTopNonFinishingActivity();
                transition2 = transition;
                z2 = false;
                task2.moveTaskToFront(task, false, activityOptions, topNonFinishingActivity != null ? null : topNonFinishingActivity.appTimeTracker, str3);
                handleNonResizableTaskIfNeeded(task, 0, this.mRootWindowContainer.getDefaultTaskDisplayArea(), task2, z);
                if (topNonFinishingActivity != null && (activityOptions == null || !activityOptions.getDisableStartingWindow())) {
                    topNonFinishingActivity.showStartingWindow(true);
                }
                if (transition2 != null) {
                    task.mTransitionController.requestStartTransition(transition2, task, activityOptions != null ? activityOptions.getRemoteTransition() : null, null);
                }
                this.mUserLeaving = false;
                this.mService.continueWindowLayout();
                return;
            }
            task2.moveTaskToFront(task, false, activityOptions, topNonFinishingActivity != null ? null : topNonFinishingActivity.appTimeTracker, str3);
            handleNonResizableTaskIfNeeded(task, 0, this.mRootWindowContainer.getDefaultTaskDisplayArea(), task2, z);
            if (topNonFinishingActivity != null) {
                topNonFinishingActivity.showStartingWindow(true);
            }
            if (transition2 != null) {
            }
            this.mUserLeaving = false;
            this.mService.continueWindowLayout();
            return;
        } catch (Throwable th2) {
            th = th2;
            this.mUserLeaving = z2;
            this.mService.continueWindowLayout();
            throw th;
        }
        transition = null;
        task.mTransitionController.collect(task);
        str2 = str + " findTaskToMoveToFront";
        if (task.isResizeable()) {
        }
        str3 = str2;
        task2 = rootTask;
        z3 = false;
        if (!z3) {
        }
        topNonFinishingActivity = task.getTopNonFinishingActivity();
        transition2 = transition;
        z2 = false;
    }

    public final void moveHomeRootTaskToFrontIfNeeded(int i, TaskDisplayArea taskDisplayArea, String str) {
        Task focusedRootTask = taskDisplayArea.getFocusedRootTask();
        if ((taskDisplayArea.getWindowingMode() != 1 || (i & 1) == 0) && (focusedRootTask == null || !focusedRootTask.isActivityTypeRecents())) {
            return;
        }
        taskDisplayArea.moveHomeRootTaskToFront(str);
    }

    public boolean canUseActivityOptionsLaunchBounds(ActivityOptions activityOptions) {
        if (activityOptions == null || activityOptions.getLaunchBounds() == null) {
            return false;
        }
        return (this.mService.mSupportsPictureInPicture && activityOptions.getLaunchWindowingMode() == 2) || this.mService.mSupportsFreeformWindowManagement;
    }

    public LaunchParamsController getLaunchParamsController() {
        return this.mLaunchParamsController;
    }

    public final void removePinnedRootTaskInSurfaceTransaction(Task task) {
        task.cancelAnimation();
        task.setForceHidden(1, true);
        task.ensureActivitiesVisible(null, 0, true);
        activityIdleInternal(null, false, true, null);
        DisplayContent displayContent = this.mRootWindowContainer.getDisplayContent(0);
        this.mService.deferWindowLayout();
        try {
            task.setWindowingMode(0);
            if (task.getWindowingMode() != 5) {
                task.setBounds(null);
            }
            displayContent.getDefaultTaskDisplayArea().positionTaskBehindHome(task);
            task.setForceHidden(1, false);
            this.mRootWindowContainer.ensureActivitiesVisible(null, 0, true);
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
        } finally {
            this.mService.continueWindowLayout();
        }
    }

    /* renamed from: removeRootTaskInSurfaceTransaction */
    public final void lambda$removeRootTask$4(Task task) {
        if (task.getWindowingMode() == 2) {
            removePinnedRootTaskInSurfaceTransaction(task);
        } else {
            task.forAllLeafTasks(new Consumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda10
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ActivityTaskSupervisor.this.lambda$removeRootTaskInSurfaceTransaction$3((Task) obj);
                }
            }, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRootTaskInSurfaceTransaction$3(Task task) {
        removeTask(task, true, true, "remove-root-task");
    }

    public void removeRootTask(final Task task) {
        this.mWindowManager.inSurfaceTransaction(new Runnable() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskSupervisor.this.lambda$removeRootTask$4(task);
            }
        });
    }

    public boolean removeTaskById(int i, boolean z, boolean z2, String str, int i2) {
        Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i, 1);
        if (anyTaskForId != null) {
            removeTask(anyTaskForId, z, z2, str, i2, null);
            return true;
        }
        Slog.w("ActivityTaskManager", "Request to remove task ignored for non-existent task " + i);
        return false;
    }

    public void removeTask(Task task, boolean z, boolean z2, String str) {
        removeTask(task, z, z2, str, 1000, null);
    }

    public void removeTask(Task task, boolean z, boolean z2, String str, int i, String str2) {
        if (task.mInRemoveTask) {
            return;
        }
        task.mTransitionController.requestCloseTransitionIfNeeded(task);
        task.mInRemoveTask = true;
        try {
            task.removeActivities(str, false);
            cleanUpRemovedTaskLocked(task, z, z2);
            this.mService.getLockTaskController().clearLockedTask(task);
            this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
            if (task.isPersistable) {
                this.mService.notifyTaskPersisterLocked(null, true);
            }
            checkActivitySecurityForTaskClear(i, task, str2);
        } finally {
            task.mInRemoveTask = false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v11, types: [java.lang.CharSequence] */
    public final void checkActivitySecurityForTaskClear(int i, Task task, String str) {
        TaskDisplayArea taskDisplayArea;
        Pair<Boolean, Boolean> doesTopActivityMatchingUidExistForAsm;
        final String applicationLabel;
        String str2;
        if (i == 1000 || !task.isVisible() || task.inMultiWindowMode() || (taskDisplayArea = task.getTaskDisplayArea()) == null) {
            return;
        }
        boolean z = true;
        boolean z2 = !((Boolean) doesTopActivityMatchingUidExistForAsm(task, i, null).first).booleanValue();
        if (!((Boolean) doesTopActivityMatchingUidExistForAsm.second).booleanValue()) {
            ActivityRecord activity = task.getActivity(new Predicate() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$checkActivitySecurityForTaskClear$5;
                    lambda$checkActivitySecurityForTaskClear$5 = ActivityTaskSupervisor.lambda$checkActivitySecurityForTaskClear$5((ActivityRecord) obj);
                    return lambda$checkActivitySecurityForTaskClear$5;
                }
            });
            FrameworkStatsLog.write((int) FrameworkStatsLog.ACTIVITY_ACTION_BLOCKED, i, str, activity == null ? -1 : activity.getUid(), activity != null ? activity.info.name : null, false, -1, (String) null, (String) null, 0, 4, 7, false, -1);
            final boolean z3 = (ActivitySecurityModelFeatureFlags.shouldRestrictActivitySwitch(i) && z2) ? false : false;
            PackageManager packageManager = this.mService.mContext.getPackageManager();
            String nameForUid = packageManager.getNameForUid(i);
            if (nameForUid == null) {
                applicationLabel = String.valueOf(i);
                str2 = applicationLabel;
            } else {
                applicationLabel = getApplicationLabel(packageManager, nameForUid);
                str2 = nameForUid;
            }
            if (ActivitySecurityModelFeatureFlags.shouldShowToast(i)) {
                UiThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActivityTaskSupervisor.this.lambda$checkActivitySecurityForTaskClear$6(z3, applicationLabel);
                    }
                });
            }
            if (z3) {
                Slog.w("ActivityTaskManager", "[ASM] Return to home as source: " + str2 + " is not on top of task t: " + task);
                taskDisplayArea.moveHomeActivityToTop("taskRemoved");
                return;
            }
            Slog.i("ActivityTaskManager", "[ASM] Would return to home as source: " + str2 + " is not on top of task t: " + task);
        }
    }

    public static /* synthetic */ boolean lambda$checkActivitySecurityForTaskClear$5(ActivityRecord activityRecord) {
        return (activityRecord.finishing || activityRecord.isAlwaysOnTop()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkActivitySecurityForTaskClear$6(boolean z, CharSequence charSequence) {
        Context context = this.mService.mContext;
        StringBuilder sb = new StringBuilder();
        sb.append("go/android-asm");
        sb.append(z ? " returned home due to " : " would return home due to ");
        sb.append((Object) charSequence);
        Toast.makeText(context, sb.toString(), 1).show();
    }

    public static Pair<Boolean, Boolean> doesTopActivityMatchingUidExistForAsm(Task task, int i, final ActivityRecord activityRecord) {
        if (activityRecord != null && activityRecord.isVisible()) {
            Boolean bool = Boolean.TRUE;
            return new Pair<>(bool, bool);
        }
        ActivityRecord topMostActivity = task.getTopMostActivity();
        if (topMostActivity != null && topMostActivity.isUid(i)) {
            Boolean bool2 = Boolean.TRUE;
            return new Pair<>(bool2, bool2);
        }
        Predicate<ActivityRecord> predicate = new Predicate() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$doesTopActivityMatchingUidExistForAsm$7;
                lambda$doesTopActivityMatchingUidExistForAsm$7 = ActivityTaskSupervisor.lambda$doesTopActivityMatchingUidExistForAsm$7(ActivityRecord.this, (ActivityRecord) obj);
                return lambda$doesTopActivityMatchingUidExistForAsm$7;
            }
        };
        ActivityRecord activity = task.getActivity(predicate);
        if (activity == null) {
            Boolean bool3 = Boolean.FALSE;
            return new Pair<>(bool3, bool3);
        }
        Pair<Boolean, Boolean> allowCrossUidActivitySwitchFromBelow = activity.allowCrossUidActivitySwitchFromBelow(i);
        if (((Boolean) allowCrossUidActivitySwitchFromBelow.first).booleanValue()) {
            return new Pair<>(Boolean.TRUE, (Boolean) allowCrossUidActivitySwitchFromBelow.second);
        }
        TaskFragment taskFragment = activity.getTaskFragment();
        if (taskFragment == null) {
            Boolean bool4 = Boolean.FALSE;
            return new Pair<>(bool4, bool4);
        }
        TaskFragment adjacentTaskFragment = taskFragment.getAdjacentTaskFragment();
        if (adjacentTaskFragment == null) {
            Boolean bool5 = Boolean.FALSE;
            return new Pair<>(bool5, bool5);
        }
        ActivityRecord activity2 = adjacentTaskFragment.getActivity(predicate);
        if (activity2 == null) {
            Boolean bool6 = Boolean.FALSE;
            return new Pair<>(bool6, bool6);
        }
        return activity2.allowCrossUidActivitySwitchFromBelow(i);
    }

    public static /* synthetic */ boolean lambda$doesTopActivityMatchingUidExistForAsm$7(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        return activityRecord2.equals(activityRecord) || !(activityRecord2.finishing || activityRecord2.isAlwaysOnTop());
    }

    public static CharSequence getApplicationLabel(PackageManager packageManager, String str) {
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, PackageManager.ApplicationInfoFlags.of(0L)));
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
        }
    }

    public void cleanUpRemovedTaskLocked(Task task, boolean z, boolean z2) {
        if (z2) {
            this.mRecentTasks.remove(task);
        }
        ComponentName component = task.getBaseIntent().getComponent();
        if (component == null) {
            Slog.w("ActivityTaskManager", "No component for base intent of task: " + task);
            return;
        }
        this.mService.f1161mH.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda3
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                ((ActivityManagerInternal) obj).cleanUpServices(((Integer) obj2).intValue(), (ComponentName) obj3, (Intent) obj4);
            }
        }, this.mService.mAmInternal, Integer.valueOf(task.mUserId), component, new Intent(task.getBaseIntent())));
        if (z) {
            String packageName = component.getPackageName();
            ArrayList arrayList = new ArrayList();
            ArrayMap map = this.mService.mProcessNames.getMap();
            for (int i = 0; i < map.size(); i++) {
                SparseArray sparseArray = (SparseArray) map.valueAt(i);
                for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                    WindowProcessController windowProcessController = (WindowProcessController) sparseArray.valueAt(i2);
                    if (windowProcessController.mUserId == task.mUserId && windowProcessController != this.mService.mHomeProcess && windowProcessController.containsPackage(packageName)) {
                        if (!windowProcessController.shouldKillProcessForRemovedTask(task) || windowProcessController.hasForegroundServices()) {
                            return;
                        }
                        arrayList.add(windowProcessController);
                    }
                }
            }
            this.mService.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda4
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ActivityManagerInternal) obj).killProcessesForRemovedTask((ArrayList) obj2);
                }
            }, this.mService.mAmInternal, arrayList));
        }
    }

    public boolean restoreRecentTaskLocked(Task task, ActivityOptions activityOptions, boolean z) {
        Task orCreateRootTask = this.mRootWindowContainer.getOrCreateRootTask(null, activityOptions, task, z);
        WindowContainer parent = task.getParent();
        if (parent != orCreateRootTask && task != orCreateRootTask) {
            if (parent != null) {
                task.reparent(orCreateRootTask, Integer.MAX_VALUE, true, "restoreRecentTaskLocked");
                return true;
            }
            orCreateRootTask.addChild((WindowContainer) task, z, true);
        }
        return true;
    }

    @Override // com.android.server.p014wm.RecentTasks.Callbacks
    public void onRecentTaskAdded(Task task) {
        task.touchActiveTime();
    }

    @Override // com.android.server.p014wm.RecentTasks.Callbacks
    public void onRecentTaskRemoved(Task task, boolean z, boolean z2) {
        if (z) {
            removeTaskById(task.mTaskId, z2, false, "recent-task-trimmed", 1000);
        }
        task.removedFromRecents();
    }

    public Task getReparentTargetRootTask(Task task, Task task2, boolean z) {
        Task rootTask = task.getRootTask();
        int i = task2.mTaskId;
        boolean inMultiWindowMode = task2.inMultiWindowMode();
        if (rootTask != null && rootTask.mTaskId == i) {
            Slog.w("ActivityTaskManager", "Can not reparent to same root task, task=" + task + " already in rootTaskId=" + i);
            return rootTask;
        } else if (inMultiWindowMode && !this.mService.mSupportsMultiWindow) {
            throw new IllegalArgumentException("Device doesn't support multi-window, can not reparent task=" + task + " to root-task=" + task2);
        } else if (task2.getDisplayId() != 0 && !this.mService.mSupportsMultiDisplay) {
            throw new IllegalArgumentException("Device doesn't support multi-display, can not reparent task=" + task + " to rootTaskId=" + i);
        } else if (task2.getWindowingMode() == 5 && !this.mService.mSupportsFreeformWindowManagement) {
            throw new IllegalArgumentException("Device doesn't support freeform, can not reparent task=" + task);
        } else if (task2.inPinnedWindowingMode()) {
            throw new IllegalArgumentException("No support to reparent to PIP, task=" + task);
        } else if (!inMultiWindowMode || task.supportsMultiWindowInDisplayArea(task2.getDisplayArea())) {
            return task2;
        } else {
            Slog.w("ActivityTaskManager", "Can not move unresizeable task=" + task + " to multi-window root task=" + task2 + " Moving to a fullscreen root task instead.");
            return rootTask != null ? rootTask : task2.getDisplayArea().createRootTask(1, task2.getActivityType(), z);
        }
    }

    public void goingToSleepLocked() {
        scheduleSleepTimeout();
        if (!this.mGoingToSleepWakeLock.isHeld()) {
            this.mGoingToSleepWakeLock.acquire();
            if (this.mLaunchingActivityWakeLock.isHeld()) {
                this.mLaunchingActivityWakeLock.release();
                this.mHandler.removeMessages(204);
            }
        }
        this.mRootWindowContainer.applySleepTokens(false);
        checkReadyForSleepLocked(true);
    }

    public boolean shutdownLocked(int i) {
        boolean z;
        goingToSleepLocked();
        long currentTimeMillis = System.currentTimeMillis() + i;
        while (true) {
            z = true;
            if (!this.mRootWindowContainer.putTasksToSleep(true, true)) {
                long currentTimeMillis2 = currentTimeMillis - System.currentTimeMillis();
                if (currentTimeMillis2 > 0) {
                    try {
                        this.mService.mGlobalLock.wait(currentTimeMillis2);
                    } catch (InterruptedException unused) {
                    }
                } else {
                    Slog.w("ActivityTaskManager", "Activity manager shutdown timed out");
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        checkReadyForSleepLocked(false);
        return z;
    }

    public void comeOutOfSleepIfNeededLocked() {
        removeSleepTimeouts();
        if (this.mGoingToSleepWakeLock.isHeld()) {
            this.mGoingToSleepWakeLock.release();
        }
    }

    public void checkReadyForSleepLocked(boolean z) {
        if (this.mService.isSleepingOrShuttingDownLocked() && this.mRootWindowContainer.putTasksToSleep(z, false)) {
            this.mService.endLaunchPowerMode(3);
            this.mRootWindowContainer.rankTaskLayers();
            removeSleepTimeouts();
            if (this.mGoingToSleepWakeLock.isHeld()) {
                this.mGoingToSleepWakeLock.release();
            }
            ActivityTaskManagerService activityTaskManagerService = this.mService;
            if (activityTaskManagerService.mShuttingDown) {
                activityTaskManagerService.mGlobalLock.notifyAll();
            }
        }
    }

    public boolean reportResumedActivityLocked(ActivityRecord activityRecord) {
        this.mStoppingActivities.remove(activityRecord);
        if (activityRecord.getRootTask().getDisplayArea().allResumedActivitiesComplete()) {
            this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
            this.mRootWindowContainer.executeAppTransitionForAllDisplay();
            return true;
        }
        return false;
    }

    public final void handleLaunchTaskBehindCompleteLocked(ActivityRecord activityRecord) {
        Task task = activityRecord.getTask();
        Task rootTask = task.getRootTask();
        this.mRecentTasks.add(task);
        this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
        rootTask.ensureActivitiesVisible(null, 0, false);
        ActivityRecord topNonFinishingActivity = rootTask.getTopNonFinishingActivity();
        if (topNonFinishingActivity != null) {
            topNonFinishingActivity.getTask().touchActiveTime();
        }
    }

    public void scheduleLaunchTaskBehindComplete(IBinder iBinder) {
        this.mHandler.obtainMessage(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__ROLE_HOLDER_UPDATER_UPDATE_FAILED, iBinder).sendToTarget();
    }

    public final void processStoppingAndFinishingActivities(ActivityRecord activityRecord, boolean z, String str) {
        ArrayList arrayList = null;
        int i = 0;
        while (i < this.mStoppingActivities.size()) {
            ActivityRecord activityRecord2 = this.mStoppingActivities.get(i);
            boolean isInTransition = activityRecord2.isInTransition();
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1740512980, 60, (String) null, new Object[]{String.valueOf(activityRecord2), Boolean.valueOf(activityRecord2.nowVisible), Boolean.valueOf(isInTransition), String.valueOf(activityRecord2.finishing)});
            }
            if (!isInTransition || this.mService.mShuttingDown) {
                if (!z && activityRecord2.isState(ActivityRecord.State.PAUSING)) {
                    removeIdleTimeoutForActivity(activityRecord);
                    scheduleIdleTimeout(activityRecord);
                } else {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1707370822, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                    }
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(activityRecord2);
                    this.mStoppingActivities.remove(i);
                    i--;
                }
            }
            i++;
        }
        int size = arrayList == null ? 0 : arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ActivityRecord activityRecord3 = (ActivityRecord) arrayList.get(i2);
            if (activityRecord3.isInHistory()) {
                if (activityRecord3.finishing) {
                    activityRecord3.destroyIfPossible(str);
                } else {
                    activityRecord3.stopIfPossible();
                }
            }
        }
        int size2 = this.mFinishingActivities.size();
        if (size2 == 0) {
            return;
        }
        ArrayList arrayList2 = new ArrayList(this.mFinishingActivities);
        this.mFinishingActivities.clear();
        for (int i3 = 0; i3 < size2; i3++) {
            ActivityRecord activityRecord4 = (ActivityRecord) arrayList2.get(i3);
            if (activityRecord4.isInHistory()) {
                activityRecord4.destroyImmediately("finish-" + str);
            }
        }
    }

    public void removeHistoryRecords(WindowProcessController windowProcessController) {
        removeHistoryRecords(this.mStoppingActivities, windowProcessController, "mStoppingActivities");
        removeHistoryRecords(this.mFinishingActivities, windowProcessController, "mFinishingActivities");
        removeHistoryRecords(this.mNoHistoryActivities, windowProcessController, "mNoHistoryActivities");
    }

    public final void removeHistoryRecords(ArrayList<ActivityRecord> arrayList, WindowProcessController windowProcessController, String str) {
        int size = arrayList.size();
        while (size > 0) {
            size--;
            ActivityRecord activityRecord = arrayList.get(size);
            if (activityRecord.app == windowProcessController) {
                arrayList.remove(size);
                activityRecord.removeTimeouts();
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println();
        printWriter.println("ActivityTaskSupervisor state:");
        this.mRootWindowContainer.dump(printWriter, str, true);
        getKeyguardController().dump(printWriter, str);
        this.mService.getLockTaskController().dump(printWriter, str);
        printWriter.print(str);
        printWriter.println("mCurTaskIdForUser=" + this.mCurTaskIdForUser);
        printWriter.println(str + "mUserRootTaskInFront=" + this.mRootWindowContainer.mUserRootTaskInFront);
        printWriter.println(str + "mVisibilityTransactionDepth=" + this.mVisibilityTransactionDepth);
        printWriter.print(str);
        printWriter.print("isHomeRecentsComponent=");
        printWriter.println(this.mRecentTasks.isRecentsComponentHomeActivity(this.mRootWindowContainer.mCurrentUser));
        if (!this.mWaitingActivityLaunched.isEmpty()) {
            printWriter.println(str + "mWaitingActivityLaunched=");
            for (int size = this.mWaitingActivityLaunched.size() - 1; size >= 0; size += -1) {
                this.mWaitingActivityLaunched.get(size).dump(printWriter, str + "  ");
            }
        }
        printWriter.println(str + "mNoHistoryActivities=" + this.mNoHistoryActivities);
        printWriter.println();
    }

    public static boolean printThisActivity(PrintWriter printWriter, ActivityRecord activityRecord, String str, boolean z, String str2, Runnable runnable) {
        return printThisActivity(printWriter, activityRecord, str, -1, z, str2, runnable);
    }

    public static boolean printThisActivity(PrintWriter printWriter, ActivityRecord activityRecord, String str, int i, boolean z, String str2, Runnable runnable) {
        if (activityRecord != null) {
            if (i == -1 || i == activityRecord.getDisplayId()) {
                if (str == null || str.equals(activityRecord.packageName)) {
                    if (z) {
                        printWriter.println();
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                    printWriter.print(str2);
                    printWriter.println(activityRecord);
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static boolean dumpHistoryList(FileDescriptor fileDescriptor, PrintWriter printWriter, List<ActivityRecord> list, String str, String str2, boolean z, boolean z2, boolean z3, String str3, boolean z4, Runnable runnable, Task task) {
        int size = list.size() - 1;
        boolean z5 = z4;
        Runnable runnable2 = runnable;
        Task task2 = task;
        while (size >= 0) {
            ActivityRecord activityRecord = list.get(size);
            ActivityRecord.dumpActivity(fileDescriptor, printWriter, size, activityRecord, str, str2, z, z2, z3, str3, z5, runnable2, task2);
            task2 = activityRecord.getTask();
            z5 = z3 && activityRecord.attachedToProcess();
            size--;
            runnable2 = null;
        }
        return false;
    }

    public void scheduleIdleTimeout(ActivityRecord activityRecord) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(200, activityRecord), IDLE_TIMEOUT);
    }

    public final void scheduleIdle() {
        if (this.mHandler.hasMessages(201)) {
            return;
        }
        this.mHandler.sendEmptyMessage(201);
    }

    public void updateTopResumedActivityIfNeeded(String str) {
        ActivityRecord activityRecord = this.mTopResumedActivity;
        Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
        if (topDisplayFocusedRootTask == null || topDisplayFocusedRootTask.getTopResumedActivity() == activityRecord) {
            if (this.mService.isSleepingLocked()) {
                this.mService.updateTopApp(null);
                return;
            }
            return;
        }
        if (((activityRecord == null || this.mTopResumedActivityWaitingForPrev) ? false : true) && activityRecord.scheduleTopResumedActivityChanged(false)) {
            scheduleTopResumedStateLossTimeout(activityRecord);
            this.mTopResumedActivityWaitingForPrev = true;
        }
        ActivityRecord topResumedActivity = topDisplayFocusedRootTask.getTopResumedActivity();
        this.mTopResumedActivity = topResumedActivity;
        if (topResumedActivity != null && activityRecord != null) {
            WindowProcessController windowProcessController = topResumedActivity.app;
            if (windowProcessController != null) {
                windowProcessController.addToPendingTop();
            }
            this.mService.updateOomAdj();
        }
        ActivityRecord activityRecord2 = this.mTopResumedActivity;
        if (activityRecord2 != null) {
            this.mService.setLastResumedActivityUncheckLocked(activityRecord2, str);
        }
        scheduleTopResumedActivityStateIfNeeded();
        this.mService.updateTopApp(this.mTopResumedActivity);
    }

    public final void scheduleTopResumedActivityStateIfNeeded() {
        ActivityRecord activityRecord = this.mTopResumedActivity;
        if (activityRecord == null || this.mTopResumedActivityWaitingForPrev) {
            return;
        }
        activityRecord.scheduleTopResumedActivityChanged(true);
    }

    public final void scheduleTopResumedStateLossTimeout(ActivityRecord activityRecord) {
        Message obtainMessage = this.mHandler.obtainMessage(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_APPLICATION_EXEMPTIONS);
        obtainMessage.obj = activityRecord;
        activityRecord.topResumedStateLossTime = SystemClock.uptimeMillis();
        this.mHandler.sendMessageDelayed(obtainMessage, 500L);
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 74885950, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
        }
    }

    public void handleTopResumedStateReleased(boolean z) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 708142634, 0, (String) null, new Object[]{z ? "(due to timeout)" : "(transition complete)"});
        }
        this.mHandler.removeMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_APPLICATION_EXEMPTIONS);
        if (this.mTopResumedActivityWaitingForPrev) {
            this.mTopResumedActivityWaitingForPrev = false;
            scheduleTopResumedActivityStateIfNeeded();
        }
    }

    public void removeIdleTimeoutForActivity(ActivityRecord activityRecord) {
        this.mHandler.removeMessages(200, activityRecord);
    }

    public final void scheduleResumeTopActivities() {
        if (this.mHandler.hasMessages(202)) {
            return;
        }
        this.mHandler.sendEmptyMessage(202);
    }

    public void scheduleProcessStoppingAndFinishingActivitiesIfNeeded() {
        if (this.mStoppingActivities.isEmpty() && this.mFinishingActivities.isEmpty()) {
            return;
        }
        if (this.mRootWindowContainer.allResumedActivitiesIdle()) {
            scheduleIdle();
        } else if (this.mHandler.hasMessages(205) || !this.mRootWindowContainer.allResumedActivitiesVisible()) {
        } else {
            this.mHandler.sendEmptyMessage(205);
        }
    }

    public void removeSleepTimeouts() {
        this.mHandler.removeMessages(203);
    }

    public final void scheduleSleepTimeout() {
        removeSleepTimeouts();
        this.mHandler.sendEmptyMessageDelayed(203, SLEEP_TIMEOUT);
    }

    public boolean hasScheduledRestartTimeouts(ActivityRecord activityRecord) {
        return this.mHandler.hasMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_ROLE_HOLDER_UPDATE_START, activityRecord);
    }

    public void removeRestartTimeouts(ActivityRecord activityRecord) {
        this.mHandler.removeMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_ROLE_HOLDER_UPDATE_START, activityRecord);
    }

    public final void scheduleRestartTimeout(ActivityRecord activityRecord) {
        removeRestartTimeouts(activityRecord);
        ActivityTaskSupervisorHandler activityTaskSupervisorHandler = this.mHandler;
        activityTaskSupervisorHandler.sendMessageDelayed(activityTaskSupervisorHandler.obtainMessage(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_ROLE_HOLDER_UPDATE_START, activityRecord), 2000L);
    }

    public void handleNonResizableTaskIfNeeded(Task task, int i, TaskDisplayArea taskDisplayArea, Task task2) {
        handleNonResizableTaskIfNeeded(task, i, taskDisplayArea, task2, false);
    }

    public void handleNonResizableTaskIfNeeded(Task task, int i, TaskDisplayArea taskDisplayArea, Task task2, boolean z) {
        boolean z2 = (taskDisplayArea == null || taskDisplayArea.getDisplayId() == 0) ? false : true;
        if (task.isActivityTypeStandardOrUndefined()) {
            if (!z2) {
                if (z) {
                    return;
                }
                handleForcedResizableTaskIfNeeded(task, 1);
            } else if (!task.canBeLaunchedOnDisplay(task.getDisplayId())) {
                throw new IllegalStateException("Task resolved to incompatible display");
            } else {
                DisplayContent displayContent = taskDisplayArea.mDisplayContent;
                if (displayContent == task.getDisplayContent()) {
                    if (z) {
                        return;
                    }
                    handleForcedResizableTaskIfNeeded(task, 2);
                    return;
                }
                Slog.w("ActivityTaskManager", "Failed to put " + task + " on display " + displayContent.mDisplayId);
                this.mService.getTaskChangeNotificationController().notifyActivityLaunchOnSecondaryDisplayFailed(task.getTaskInfo(), displayContent.mDisplayId);
            }
        }
    }

    public final void handleForcedResizableTaskIfNeeded(Task task, int i) {
        ActivityRecord topNonFinishingActivity = task.getTopNonFinishingActivity();
        if (topNonFinishingActivity == null || topNonFinishingActivity.noDisplay || !topNonFinishingActivity.canForceResizeNonResizable(task.getWindowingMode())) {
            return;
        }
        this.mService.getTaskChangeNotificationController().notifyActivityForcedResizable(task.mTaskId, i, topNonFinishingActivity.info.applicationInfo.packageName);
    }

    public void scheduleUpdateMultiWindowMode(Task task) {
        task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ActivityTaskSupervisor.this.lambda$scheduleUpdateMultiWindowMode$8((ActivityRecord) obj);
            }
        });
        if (this.mHandler.hasMessages(FrameworkStatsLog.f394xe56e23e8)) {
            return;
        }
        this.mHandler.sendEmptyMessage(FrameworkStatsLog.f394xe56e23e8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdateMultiWindowMode$8(ActivityRecord activityRecord) {
        if (activityRecord.attachedToProcess()) {
            this.mMultiWindowModeChangedActivities.add(activityRecord);
        }
    }

    public void scheduleUpdatePictureInPictureModeIfNeeded(Task task, Task task2) {
        Task rootTask = task.getRootTask();
        if (task2 != null) {
            if (task2 == rootTask || task2.inPinnedWindowingMode() || rootTask.inPinnedWindowingMode()) {
                scheduleUpdatePictureInPictureModeIfNeeded(task, rootTask.getRequestedOverrideBounds());
            }
        }
    }

    public void scheduleUpdatePictureInPictureModeIfNeeded(Task task, Rect rect) {
        task.forAllActivities(new Consumer() { // from class: com.android.server.wm.ActivityTaskSupervisor$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ActivityTaskSupervisor.this.lambda$scheduleUpdatePictureInPictureModeIfNeeded$9((ActivityRecord) obj);
            }
        });
        this.mPipModeChangedTargetRootTaskBounds = rect;
        if (this.mHandler.hasMessages(FrameworkStatsLog.f393x532b6133)) {
            return;
        }
        this.mHandler.sendEmptyMessage(FrameworkStatsLog.f393x532b6133);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUpdatePictureInPictureModeIfNeeded$9(ActivityRecord activityRecord) {
        if (activityRecord.attachedToProcess()) {
            this.mPipModeChangedActivities.add(activityRecord);
            this.mMultiWindowModeChangedActivities.remove(activityRecord);
        }
    }

    public void wakeUp(String str) {
        PowerManager powerManager = this.mPowerManager;
        long uptimeMillis = SystemClock.uptimeMillis();
        powerManager.wakeUp(uptimeMillis, 2, "android.server.am:TURN_ON:" + str);
    }

    public void beginActivityVisibilityUpdate() {
        if (this.mVisibilityTransactionDepth == 0) {
            getKeyguardController().updateVisibility();
        }
        this.mVisibilityTransactionDepth++;
    }

    public void endActivityVisibilityUpdate() {
        int i = this.mVisibilityTransactionDepth - 1;
        this.mVisibilityTransactionDepth = i;
        if (i == 0) {
            computeProcessActivityStateBatch();
        }
    }

    public boolean inActivityVisibilityUpdate() {
        return this.mVisibilityTransactionDepth > 0;
    }

    public void setDeferRootVisibilityUpdate(boolean z) {
        this.mDeferRootVisibilityUpdate = z;
    }

    public boolean isRootVisibilityUpdateDeferred() {
        return this.mDeferRootVisibilityUpdate;
    }

    public void onProcessActivityStateChanged(WindowProcessController windowProcessController, boolean z) {
        if (z || inActivityVisibilityUpdate()) {
            if (this.mActivityStateChangedProcs.contains(windowProcessController)) {
                return;
            }
            this.mActivityStateChangedProcs.add(windowProcessController);
            return;
        }
        windowProcessController.computeProcessActivityState();
    }

    public void computeProcessActivityStateBatch() {
        if (this.mActivityStateChangedProcs.isEmpty()) {
            return;
        }
        for (int size = this.mActivityStateChangedProcs.size() - 1; size >= 0; size--) {
            this.mActivityStateChangedProcs.get(size).computeProcessActivityState();
        }
        this.mActivityStateChangedProcs.clear();
    }

    public void beginDeferResume() {
        this.mDeferResumeCount++;
    }

    public void endDeferResume() {
        this.mDeferResumeCount--;
    }

    public boolean readyToResume() {
        return this.mDeferResumeCount == 0;
    }

    /* renamed from: com.android.server.wm.ActivityTaskSupervisor$ActivityTaskSupervisorHandler */
    /* loaded from: classes2.dex */
    public final class ActivityTaskSupervisorHandler extends Handler {
        public ActivityTaskSupervisorHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            String str;
            int i;
            synchronized (ActivityTaskSupervisor.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (handleMessageInner(message)) {
                        return;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (message.what != 213) {
                        return;
                    }
                    ActivityRecord activityRecord = (ActivityRecord) message.obj;
                    synchronized (ActivityTaskSupervisor.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (activityRecord.attachedToProcess() && activityRecord.isState(ActivityRecord.State.RESTARTING_PROCESS)) {
                                WindowProcessController windowProcessController = activityRecord.app;
                                str = windowProcessController.mName;
                                i = windowProcessController.mUid;
                            } else {
                                str = null;
                                i = 0;
                            }
                        } finally {
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (str != null) {
                        ActivityTaskSupervisor.this.mService.mAmInternal.killProcess(str, i, "restartActivityProcessTimeout");
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        public final void activityIdleFromMessage(ActivityRecord activityRecord, boolean z) {
            ActivityTaskSupervisor.this.activityIdleInternal(activityRecord, z, z, null);
        }

        public final boolean handleMessageInner(Message message) {
            int i = message.what;
            if (i != 212) {
                switch (i) {
                    case 200:
                        activityIdleFromMessage((ActivityRecord) message.obj, true);
                        break;
                    case 201:
                        activityIdleFromMessage((ActivityRecord) message.obj, false);
                        break;
                    case 202:
                        ActivityTaskSupervisor.this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                        break;
                    case 203:
                        if (ActivityTaskSupervisor.this.mService.isSleepingOrShuttingDownLocked()) {
                            Slog.w("ActivityTaskManager", "Sleep timeout!  Sleeping now.");
                            ActivityTaskSupervisor.this.checkReadyForSleepLocked(false);
                            break;
                        }
                        break;
                    case 204:
                        if (ActivityTaskSupervisor.this.mLaunchingActivityWakeLock.isHeld()) {
                            Slog.w("ActivityTaskManager", "Launch timeout has expired, giving up wake lock!");
                            ActivityTaskSupervisor.this.mLaunchingActivityWakeLock.release();
                            break;
                        }
                        break;
                    case 205:
                        ActivityTaskSupervisor.this.processStoppingAndFinishingActivities(null, false, "transit");
                        break;
                    default:
                        switch (i) {
                            case FrameworkStatsLog.f394xe56e23e8 /* 214 */:
                                for (int size = ActivityTaskSupervisor.this.mMultiWindowModeChangedActivities.size() - 1; size >= 0; size--) {
                                    ((ActivityRecord) ActivityTaskSupervisor.this.mMultiWindowModeChangedActivities.remove(size)).updateMultiWindowMode();
                                }
                                break;
                            case FrameworkStatsLog.f393x532b6133 /* 215 */:
                                for (int size2 = ActivityTaskSupervisor.this.mPipModeChangedActivities.size() - 1; size2 >= 0; size2--) {
                                    ((ActivityRecord) ActivityTaskSupervisor.this.mPipModeChangedActivities.remove(size2)).updatePictureInPictureMode(ActivityTaskSupervisor.this.mPipModeChangedTargetRootTaskBounds, false);
                                }
                                break;
                            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_MTE_POLICY /* 216 */:
                                ActivityTaskSupervisor.this.mHandler.removeMessages(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_MTE_POLICY);
                                ActivityTaskSupervisor.this.mRootWindowContainer.startHomeOnEmptyDisplays((String) message.obj);
                                break;
                            case FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_APPLICATION_EXEMPTIONS /* 217 */:
                                ActivityRecord activityRecord = (ActivityRecord) message.obj;
                                Slog.w("ActivityTaskManager", "Activity top resumed state loss timeout for " + activityRecord);
                                if (activityRecord.hasProcess()) {
                                    ActivityTaskSupervisor.this.mService.logAppTooSlow(activityRecord.app, activityRecord.topResumedStateLossTime, "top state loss for " + activityRecord);
                                }
                                ActivityTaskSupervisor.this.handleTopResumedStateReleased(true);
                                break;
                            default:
                                return false;
                        }
                }
            } else {
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked((IBinder) message.obj);
                if (forTokenLocked != null) {
                    ActivityTaskSupervisor.this.handleLaunchTaskBehindCompleteLocked(forTokenLocked);
                }
            }
            return true;
        }
    }

    public int startActivityFromRecents(int i, int i2, int i3, SafeActivityOptions safeActivityOptions) {
        boolean z;
        int i4 = i2;
        ActivityOptions options = safeActivityOptions != null ? safeActivityOptions.getOptions(this) : null;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                boolean isCallerRecents = this.mRecentTasks.isCallerRecents(i4);
                int i5 = 0;
                if (options != null) {
                    int launchActivityType = options.getLaunchActivityType();
                    if (options.freezeRecentTasksReordering()) {
                        if (!isCallerRecents) {
                            if (ActivityTaskManagerService.checkPermission("android.permission.MANAGE_ACTIVITY_TASKS", i, i4) == 0) {
                            }
                        }
                        this.mRecentTasks.setFreezeTaskListReordering();
                    }
                    if (options.getLaunchRootTask() != null) {
                        z = false;
                        i5 = launchActivityType;
                        if (i5 != 2 || i5 == 3) {
                            throw new IllegalArgumentException("startActivityFromRecents: Task " + i3 + " can't be launch in the home/recents root task.");
                        }
                        this.mService.deferWindowLayout();
                        Task anyTaskForId = this.mRootWindowContainer.anyTaskForId(i3, 2, options, true);
                        if (anyTaskForId == null) {
                            this.mWindowManager.executeAppTransition();
                            throw new IllegalArgumentException("startActivityFromRecents: Task " + i3 + " not found.");
                        }
                        if (z) {
                            this.mRootWindowContainer.getDefaultTaskDisplayArea().moveHomeRootTaskToFront("startActivityFromRecents");
                        }
                        if (!this.mService.mAmInternal.shouldConfirmCredentials(anyTaskForId.mUserId) && anyTaskForId.getRootActivity() != null) {
                            ActivityRecord topNonFinishingActivity = anyTaskForId.getTopNonFinishingActivity();
                            this.mRootWindowContainer.startPowerModeLaunchIfNeeded(true, topNonFinishingActivity);
                            ActivityMetricsLogger activityMetricsLogger = this.mActivityMetricsLogger;
                            Intent intent = anyTaskForId.intent;
                            if (isCallerRecents) {
                                i4 = -1;
                            }
                            ActivityMetricsLogger.LaunchingState notifyActivityLaunching = activityMetricsLogger.notifyActivityLaunching(intent, null, i4);
                            this.mService.moveTaskToFrontLocked(null, null, anyTaskForId.mTaskId, 0, safeActivityOptions);
                            if (options != null && options.getAnimationType() == 13) {
                                topNonFinishingActivity.mPendingRemoteAnimation = options.getRemoteAnimationAdapter();
                            }
                            topNonFinishingActivity.applyOptionsAnimation();
                            if (options != null && options.getLaunchCookie() != null) {
                                topNonFinishingActivity.mLaunchCookie = options.getLaunchCookie();
                            }
                            this.mActivityMetricsLogger.notifyActivityLaunched(notifyActivityLaunching, 2, false, topNonFinishingActivity, options);
                            this.mService.getActivityStartController().postStartActivityProcessingForLastStarter(anyTaskForId.getTopNonFinishingActivity(), 2, anyTaskForId.getRootTask());
                            this.mService.resumeAppSwitches();
                            this.mService.continueWindowLayout();
                            return 2;
                        }
                        int i6 = anyTaskForId.mCallingUid;
                        String str = anyTaskForId.mCallingPackage;
                        String str2 = anyTaskForId.mCallingFeatureId;
                        Intent intent2 = anyTaskForId.intent;
                        intent2.addFlags(1048576);
                        int i7 = anyTaskForId.mUserId;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        try {
                            ThreadLocal<Boolean> threadLocal = PackageManagerServiceUtils.DISABLE_ENFORCE_INTENTS_TO_MATCH_INTENT_FILTERS;
                            threadLocal.set(Boolean.TRUE);
                            int startActivityInPackage = this.mService.getActivityStartController().startActivityInPackage(i6, i, i2, str, str2, intent2, null, null, null, 0, 0, safeActivityOptions, i7, anyTaskForId, "startActivityFromRecents", false, null, BackgroundStartPrivileges.NONE);
                            threadLocal.set(Boolean.FALSE);
                            synchronized (this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    this.mService.continueWindowLayout();
                                } finally {
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return startActivityInPackage;
                        } catch (Throwable th) {
                            PackageManagerServiceUtils.DISABLE_ENFORCE_INTENTS_TO_MATCH_INTENT_FILTERS.set(Boolean.FALSE);
                            synchronized (this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    this.mService.continueWindowLayout();
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                } finally {
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                }
                            }
                        }
                    }
                    i5 = launchActivityType;
                }
                z = true;
                if (i5 != 2) {
                }
                throw new IllegalArgumentException("startActivityFromRecents: Task " + i3 + " can't be launch in the home/recents root task.");
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskSupervisor$TaskInfoHelper */
    /* loaded from: classes2.dex */
    public static class TaskInfoHelper implements Consumer<ActivityRecord> {
        public TaskInfo mInfo;
        public ActivityRecord mTopRunning;

        public ActivityRecord fillAndReturnTop(Task task, TaskInfo taskInfo) {
            taskInfo.numActivities = 0;
            taskInfo.baseActivity = null;
            this.mInfo = taskInfo;
            task.forAllActivities(this);
            ActivityRecord activityRecord = this.mTopRunning;
            this.mTopRunning = null;
            this.mInfo = null;
            return activityRecord;
        }

        @Override // java.util.function.Consumer
        public void accept(ActivityRecord activityRecord) {
            IBinder iBinder = activityRecord.mLaunchCookie;
            if (iBinder != null) {
                this.mInfo.addLaunchCookie(iBinder);
            }
            if (activityRecord.finishing) {
                return;
            }
            TaskInfo taskInfo = this.mInfo;
            taskInfo.numActivities++;
            taskInfo.baseActivity = activityRecord.mActivityComponent;
            if (this.mTopRunning == null) {
                this.mTopRunning = activityRecord;
            }
        }
    }

    /* renamed from: com.android.server.wm.ActivityTaskSupervisor$WaitInfo */
    /* loaded from: classes2.dex */
    public static class WaitInfo {
        public final ActivityMetricsLogger.LaunchingState mLaunchingState;
        public final WaitResult mResult;
        public final ComponentName mTargetComponent;

        public WaitInfo(WaitResult waitResult, ComponentName componentName, ActivityMetricsLogger.LaunchingState launchingState) {
            this.mResult = waitResult;
            this.mTargetComponent = componentName;
            this.mLaunchingState = launchingState;
        }

        public boolean matches(ActivityRecord activityRecord) {
            if (!this.mLaunchingState.hasActiveTransitionInfo()) {
                return this.mTargetComponent.equals(activityRecord.mActivityComponent);
            }
            return this.mLaunchingState.contains(activityRecord);
        }

        public void dump(PrintWriter printWriter, String str) {
            printWriter.println(str + "WaitInfo:");
            printWriter.println(str + "  mTargetComponent=" + this.mTargetComponent);
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("  mResult=");
            printWriter.println(sb.toString());
            WaitResult waitResult = this.mResult;
            waitResult.dump(printWriter, str + "    ");
        }
    }
}
