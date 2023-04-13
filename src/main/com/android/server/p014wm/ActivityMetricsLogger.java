package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IncrementalStatesInfo;
import android.content.pm.dex.ArtManagerInternal;
import android.content.pm.dex.PackageOptimizationInfo;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Trace;
import android.os.incremental.IncrementalManager;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.apphibernation.AppHibernationManagerInternal;
import com.android.server.apphibernation.AppHibernationService;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p006am.MemoryStatUtil;
import com.android.server.p014wm.ActivityRecord;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.ActivityMetricsLogger */
/* loaded from: classes2.dex */
public class ActivityMetricsLogger {
    public static final String[] TRON_WINDOW_STATE_VARZ_STRINGS = {"window_time_0", "window_time_1", "window_time_2", "window_time_3", "window_time_4"};
    public AppHibernationManagerInternal mAppHibernationManagerInternal;
    public ArtManagerInternal mArtManagerInternal;
    public final LaunchObserverRegistryImpl mLaunchObserver;
    public final ActivityTaskSupervisor mSupervisor;
    public int mWindowState = 0;
    public final MetricsLogger mMetricsLogger = new MetricsLogger();
    public final Handler mLoggerHandler = FgThread.getHandler();
    public final ArrayList<TransitionInfo> mTransitionInfoList = new ArrayList<>();
    public final ArrayMap<ActivityRecord, TransitionInfo> mLastTransitionInfo = new ArrayMap<>();
    public final SparseArray<PackageCompatStateInfo> mPackageUidToCompatStateInfo = new SparseArray<>(0);
    public final StringBuilder mStringBuilder = new StringBuilder();
    public final ArrayMap<String, Boolean> mLastHibernationStates = new ArrayMap<>();
    public long mLastLogTimeSecs = SystemClock.elapsedRealtime() / 1000;

    public static int convertTransitionTypeToLaunchObserverTemperature(int i) {
        if (i != 7) {
            if (i != 8) {
                return i != 9 ? -1 : 3;
            }
            return 2;
        }
        return 1;
    }

    public static int getAppStartTransitionType(int i, boolean z) {
        if (i == 7) {
            return 3;
        }
        if (i == 8) {
            return 1;
        }
        if (i == 9) {
            return z ? 4 : 2;
        }
        return 0;
    }

    public static boolean isAppCompateStateChangedToLetterboxed(int i) {
        return i == 5 || i == 4 || i == 3;
    }

    /* renamed from: com.android.server.wm.ActivityMetricsLogger$LaunchingState */
    /* loaded from: classes2.dex */
    public static final class LaunchingState {
        public static int sTraceSeqId;
        public TransitionInfo mAssociatedTransitionInfo;
        public String mTraceName;
        public final long mStartUptimeNs = SystemClock.uptimeNanos();
        public final long mStartRealtimeNs = SystemClock.elapsedRealtimeNanos();

        public LaunchingState() {
            if (Trace.isTagEnabled(64L)) {
                sTraceSeqId++;
                String str = "launchingActivity#" + sTraceSeqId;
                this.mTraceName = str;
                Trace.asyncTraceBegin(64L, str, 0);
            }
        }

        public void stopTrace(boolean z, TransitionInfo transitionInfo) {
            String str;
            String str2;
            String str3 = this.mTraceName;
            if (str3 == null) {
                return;
            }
            if (z || transitionInfo == this.mAssociatedTransitionInfo) {
                Trace.asyncTraceEnd(64L, str3, 0);
                TransitionInfo transitionInfo2 = this.mAssociatedTransitionInfo;
                if (transitionInfo2 == null) {
                    str2 = ":failed";
                } else {
                    if (z) {
                        str = ":canceled:";
                    } else if (transitionInfo2.mProcessSwitch) {
                        int i = transitionInfo.mTransitionType;
                        str = i == 9 ? ":completed-hot:" : i == 8 ? ":completed-warm:" : ":completed-cold:";
                    } else {
                        str = ":completed-same-process:";
                    }
                    str2 = str + this.mAssociatedTransitionInfo.mLastLaunchedActivity.packageName;
                }
                Trace.instant(64L, this.mTraceName + str2);
                this.mTraceName = null;
            }
        }

        @VisibleForTesting
        public boolean allDrawn() {
            TransitionInfo transitionInfo = this.mAssociatedTransitionInfo;
            return transitionInfo != null && transitionInfo.mIsDrawn;
        }

        public boolean hasActiveTransitionInfo() {
            return this.mAssociatedTransitionInfo != null;
        }

        public boolean contains(ActivityRecord activityRecord) {
            TransitionInfo transitionInfo = this.mAssociatedTransitionInfo;
            return transitionInfo != null && transitionInfo.contains(activityRecord);
        }
    }

    /* renamed from: com.android.server.wm.ActivityMetricsLogger$TransitionInfo */
    /* loaded from: classes2.dex */
    public static final class TransitionInfo {
        public int mCurrentTransitionDelayMs;
        public boolean mIsDrawn;
        public ActivityRecord mLastLaunchedActivity;
        public String mLaunchTraceName;
        public final LaunchingState mLaunchingState;
        public boolean mLoggedStartingWindowDrawn;
        public boolean mLoggedTransitionStarting;
        public Runnable mPendingFullyDrawn;
        public final int mProcessOomAdj;
        public final boolean mProcessRunning;
        public final int mProcessState;
        public final boolean mProcessSwitch;
        public boolean mRelaunched;
        public int mSourceEventDelayMs;
        public int mSourceType;
        public final int mTransitionType;
        public int mWindowsDrawnDelayMs;
        public int mStartingWindowDelayMs = -1;
        public int mBindApplicationDelayMs = -1;
        public int mReason = 3;

        public static TransitionInfo create(ActivityRecord activityRecord, LaunchingState launchingState, ActivityOptions activityOptions, boolean z, boolean z2, int i, int i2, boolean z3, int i3) {
            int i4;
            if (i3 == 0 || i3 == 2) {
                if (z) {
                    i4 = (z3 || !activityRecord.attachedToProcess()) ? 8 : 9;
                } else {
                    i4 = 7;
                }
                return new TransitionInfo(activityRecord, launchingState, activityOptions, i4, z, z2, i, i2);
            }
            return null;
        }

        public TransitionInfo(ActivityRecord activityRecord, LaunchingState launchingState, ActivityOptions activityOptions, int i, boolean z, boolean z2, int i2, int i3) {
            ActivityOptions.SourceInfo sourceInfo;
            this.mSourceEventDelayMs = -1;
            this.mLaunchingState = launchingState;
            this.mTransitionType = i;
            this.mProcessRunning = z;
            this.mProcessSwitch = z2;
            this.mProcessState = i2;
            this.mProcessOomAdj = i3;
            setLatestLaunchedActivity(activityRecord);
            if (launchingState.mAssociatedTransitionInfo == null) {
                launchingState.mAssociatedTransitionInfo = this;
            }
            if (activityOptions == null || (sourceInfo = activityOptions.getSourceInfo()) == null) {
                return;
            }
            this.mSourceType = sourceInfo.type;
            this.mSourceEventDelayMs = (int) (TimeUnit.NANOSECONDS.toMillis(launchingState.mStartUptimeNs) - sourceInfo.eventTimeMs);
        }

        public void setLatestLaunchedActivity(ActivityRecord activityRecord) {
            ActivityRecord activityRecord2 = this.mLastLaunchedActivity;
            if (activityRecord2 == activityRecord) {
                return;
            }
            if (activityRecord2 != null) {
                activityRecord.mLaunchCookie = activityRecord2.mLaunchCookie;
                activityRecord2.mLaunchCookie = null;
                activityRecord.mLaunchRootTask = activityRecord2.mLaunchRootTask;
                activityRecord2.mLaunchRootTask = null;
            }
            this.mLastLaunchedActivity = activityRecord;
            this.mIsDrawn = activityRecord.isReportedDrawn();
        }

        public boolean canCoalesce(ActivityRecord activityRecord) {
            ActivityRecord activityRecord2 = this.mLastLaunchedActivity;
            return activityRecord2.mDisplayContent == activityRecord.mDisplayContent && activityRecord2.getWindowingMode() == activityRecord.getWindowingMode();
        }

        public boolean contains(ActivityRecord activityRecord) {
            return activityRecord == this.mLastLaunchedActivity;
        }

        public boolean isInterestingToLoggerAndObserver() {
            return this.mProcessSwitch;
        }

        public int calculateCurrentDelay() {
            return calculateDelay(SystemClock.uptimeNanos());
        }

        public int calculateDelay(long j) {
            return (int) TimeUnit.NANOSECONDS.toMillis(j - this.mLaunchingState.mStartUptimeNs);
        }

        public String toString() {
            return "TransitionInfo{" + Integer.toHexString(System.identityHashCode(this)) + " a=" + this.mLastLaunchedActivity + " d=" + this.mIsDrawn + "}";
        }
    }

    /* renamed from: com.android.server.wm.ActivityMetricsLogger$TransitionInfoSnapshot */
    /* loaded from: classes2.dex */
    public static final class TransitionInfoSnapshot {
        public final int activityRecordIdHashCode;
        public final ApplicationInfo applicationInfo;
        public final int bindApplicationDelayMs;
        public final String launchedActivityAppRecordRequiredAbi;
        public final String launchedActivityLaunchToken;
        public final String launchedActivityLaunchedFromPackage;
        public final String launchedActivityName;
        public final String launchedActivityShortComponentName;
        public final String packageName;
        public final String processName;
        public final WindowProcessController processRecord;
        public final int reason;
        public final boolean relaunched;
        @VisibleForTesting
        final int sourceEventDelayMs;
        @VisibleForTesting
        final int sourceType;
        public final int startingWindowDelayMs;
        public final long timestampNs;
        public final int type;
        public final int userId;
        public final int windowsDrawnDelayMs;
        public final int windowsFullyDrawnDelayMs;

        public TransitionInfoSnapshot(TransitionInfo transitionInfo) {
            this(transitionInfo, transitionInfo.mLastLaunchedActivity, -1);
        }

        public TransitionInfoSnapshot(TransitionInfo transitionInfo, ActivityRecord activityRecord, int i) {
            ActivityInfo activityInfo = activityRecord.info;
            this.applicationInfo = activityInfo.applicationInfo;
            this.packageName = activityRecord.packageName;
            this.launchedActivityName = activityInfo.name;
            this.launchedActivityLaunchedFromPackage = activityRecord.launchedFromPackage;
            this.launchedActivityLaunchToken = activityInfo.launchToken;
            WindowProcessController windowProcessController = activityRecord.app;
            this.launchedActivityAppRecordRequiredAbi = windowProcessController == null ? null : windowProcessController.getRequiredAbi();
            this.reason = transitionInfo.mReason;
            this.sourceEventDelayMs = transitionInfo.mSourceEventDelayMs;
            this.startingWindowDelayMs = transitionInfo.mStartingWindowDelayMs;
            this.bindApplicationDelayMs = transitionInfo.mBindApplicationDelayMs;
            this.windowsDrawnDelayMs = transitionInfo.mWindowsDrawnDelayMs;
            this.type = transitionInfo.mTransitionType;
            this.processRecord = activityRecord.app;
            this.processName = activityRecord.processName;
            this.sourceType = transitionInfo.mSourceType;
            this.userId = activityRecord.mUserId;
            this.launchedActivityShortComponentName = activityRecord.shortComponentName;
            this.activityRecordIdHashCode = System.identityHashCode(activityRecord);
            this.windowsFullyDrawnDelayMs = i;
            this.relaunched = transitionInfo.mRelaunched;
            this.timestampNs = transitionInfo.mLaunchingState.mStartRealtimeNs;
        }

        public int getLaunchState() {
            int i = this.type;
            if (i != 7) {
                if (i != 8) {
                    if (i != 9) {
                        return -1;
                    }
                    return this.relaunched ? 4 : 3;
                }
                return 2;
            }
            return 1;
        }

        public boolean isIntresetedToEventLog() {
            int i = this.type;
            return i == 8 || i == 7;
        }

        public PackageOptimizationInfo getPackageOptimizationInfo(ArtManagerInternal artManagerInternal) {
            String str;
            if (artManagerInternal == null || (str = this.launchedActivityAppRecordRequiredAbi) == null) {
                return PackageOptimizationInfo.createWithNoInfo();
            }
            return artManagerInternal.getPackageOptimizationInfo(this.applicationInfo, str, this.launchedActivityName);
        }
    }

    /* renamed from: com.android.server.wm.ActivityMetricsLogger$PackageCompatStateInfo */
    /* loaded from: classes2.dex */
    public static final class PackageCompatStateInfo {
        public ActivityRecord mLastLoggedActivity;
        public int mLastLoggedState;
        public final ArrayList<ActivityRecord> mVisibleActivities;

        public PackageCompatStateInfo() {
            this.mVisibleActivities = new ArrayList<>();
            this.mLastLoggedState = 1;
        }
    }

    public ActivityMetricsLogger(ActivityTaskSupervisor activityTaskSupervisor, Looper looper) {
        this.mSupervisor = activityTaskSupervisor;
        this.mLaunchObserver = new LaunchObserverRegistryImpl(looper);
    }

    public final void logWindowState(String str, int i) {
        this.mMetricsLogger.count(str, i);
    }

    public void logWindowState() {
        long elapsedRealtime = SystemClock.elapsedRealtime() / 1000;
        if (this.mWindowState != -1) {
            this.mLoggerHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((ActivityMetricsLogger) obj).logWindowState((String) obj2, ((Integer) obj3).intValue());
                }
            }, this, TRON_WINDOW_STATE_VARZ_STRINGS[this.mWindowState], Integer.valueOf((int) (elapsedRealtime - this.mLastLogTimeSecs))));
        }
        this.mLastLogTimeSecs = elapsedRealtime;
        this.mWindowState = -1;
        Task topDisplayFocusedRootTask = this.mSupervisor.mRootWindowContainer.getTopDisplayFocusedRootTask();
        if (topDisplayFocusedRootTask == null) {
            return;
        }
        if (topDisplayFocusedRootTask.isActivityTypeAssistant()) {
            this.mWindowState = 3;
            return;
        }
        int windowingMode = topDisplayFocusedRootTask.getWindowingMode();
        if (windowingMode == 1) {
            this.mWindowState = 0;
        } else if (windowingMode == 5) {
            this.mWindowState = 2;
        } else if (windowingMode == 6) {
            this.mWindowState = 4;
        } else if (windowingMode != 0) {
            Slog.wtf("ActivityTaskManager", "Unknown windowing mode for task=" + topDisplayFocusedRootTask + " windowingMode=" + windowingMode);
        }
    }

    public final TransitionInfo getActiveTransitionInfo(ActivityRecord activityRecord) {
        for (int size = this.mTransitionInfoList.size() - 1; size >= 0; size--) {
            TransitionInfo transitionInfo = this.mTransitionInfoList.get(size);
            if (transitionInfo.contains(activityRecord)) {
                return transitionInfo;
            }
        }
        return null;
    }

    public LaunchingState notifyActivityLaunching(Intent intent) {
        return notifyActivityLaunching(intent, null, -1);
    }

    public LaunchingState notifyActivityLaunching(Intent intent, ActivityRecord activityRecord, int i) {
        TransitionInfo transitionInfo = null;
        if (i != -1) {
            int size = this.mTransitionInfoList.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                TransitionInfo transitionInfo2 = this.mTransitionInfoList.get(size);
                if (activityRecord != null && transitionInfo2.contains(activityRecord)) {
                    transitionInfo = transitionInfo2;
                    break;
                }
                if (transitionInfo == null && i == transitionInfo2.mLastLaunchedActivity.getUid()) {
                    transitionInfo = transitionInfo2;
                }
                size--;
            }
        }
        if (transitionInfo == null) {
            LaunchingState launchingState = new LaunchingState();
            launchObserverNotifyIntentStarted(intent, launchingState.mStartUptimeNs);
            return launchingState;
        }
        return transitionInfo.mLaunchingState;
    }

    public void notifyActivityLaunched(LaunchingState launchingState, int i, boolean z, ActivityRecord activityRecord, ActivityOptions activityOptions) {
        int i2;
        int i3;
        if (activityRecord == null) {
            abort(launchingState, "nothing launched");
            return;
        }
        WindowProcessController windowProcessController = activityRecord.app;
        if (windowProcessController == null) {
            windowProcessController = this.mSupervisor.mService.getProcessController(activityRecord.processName, activityRecord.info.applicationInfo.uid);
        }
        boolean z2 = windowProcessController != null;
        boolean z3 = (z2 && windowProcessController.hasStartedActivity(activityRecord)) ? false : true;
        if (z2) {
            int currentProcState = windowProcessController.getCurrentProcState();
            i3 = windowProcessController.getCurrentAdj();
            i2 = currentProcState;
        } else {
            i2 = 20;
            i3 = -10000;
        }
        TransitionInfo transitionInfo = launchingState.mAssociatedTransitionInfo;
        if (activityRecord.isReportedDrawn() && activityRecord.isVisible()) {
            abort(launchingState, "launched activity already visible");
        } else if (transitionInfo != null && transitionInfo.canCoalesce(activityRecord)) {
            boolean z4 = !transitionInfo.mLastLaunchedActivity.packageName.equals(activityRecord.packageName);
            if (z4) {
                stopLaunchTrace(transitionInfo);
            }
            this.mLastTransitionInfo.remove(transitionInfo.mLastLaunchedActivity);
            transitionInfo.setLatestLaunchedActivity(activityRecord);
            this.mLastTransitionInfo.put(activityRecord, transitionInfo);
            if (z4) {
                startLaunchTrace(transitionInfo);
            }
            scheduleCheckActivityToBeDrawnIfSleeping(activityRecord);
        } else {
            TransitionInfo create = TransitionInfo.create(activityRecord, launchingState, activityOptions, z2, z3, i2, i3, z, i);
            if (create == null) {
                abort(launchingState, "unrecognized launch");
                return;
            }
            this.mTransitionInfoList.add(create);
            this.mLastTransitionInfo.put(activityRecord, create);
            startLaunchTrace(create);
            if (create.isInterestingToLoggerAndObserver()) {
                launchObserverNotifyActivityLaunched(create);
            } else {
                launchObserverNotifyIntentFailed(create.mLaunchingState.mStartUptimeNs);
            }
            scheduleCheckActivityToBeDrawnIfSleeping(activityRecord);
            for (int size = this.mTransitionInfoList.size() - 2; size >= 0; size--) {
                TransitionInfo transitionInfo2 = this.mTransitionInfoList.get(size);
                if (transitionInfo2.mIsDrawn || !transitionInfo2.mLastLaunchedActivity.isVisibleRequested()) {
                    scheduleCheckActivityToBeDrawn(transitionInfo2.mLastLaunchedActivity, 0L);
                }
            }
        }
    }

    public final void scheduleCheckActivityToBeDrawnIfSleeping(ActivityRecord activityRecord) {
        if (activityRecord.mDisplayContent.isSleeping()) {
            scheduleCheckActivityToBeDrawn(activityRecord, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        }
    }

    public TransitionInfoSnapshot notifyWindowsDrawn(ActivityRecord activityRecord) {
        long uptimeNanos = SystemClock.uptimeNanos();
        TransitionInfo activeTransitionInfo = getActiveTransitionInfo(activityRecord);
        if (activeTransitionInfo == null || activeTransitionInfo.mIsDrawn) {
            return null;
        }
        activeTransitionInfo.mWindowsDrawnDelayMs = activeTransitionInfo.calculateDelay(uptimeNanos);
        activeTransitionInfo.mIsDrawn = true;
        TransitionInfoSnapshot transitionInfoSnapshot = new TransitionInfoSnapshot(activeTransitionInfo);
        if (activeTransitionInfo.mLoggedTransitionStarting || (!activityRecord.mDisplayContent.mOpeningApps.contains(activityRecord) && !activityRecord.mTransitionController.isCollecting(activityRecord))) {
            done(false, activeTransitionInfo, "notifyWindowsDrawn", uptimeNanos);
        }
        return transitionInfoSnapshot;
    }

    public void notifyStartingWindowDrawn(ActivityRecord activityRecord) {
        TransitionInfo activeTransitionInfo = getActiveTransitionInfo(activityRecord);
        if (activeTransitionInfo == null || activeTransitionInfo.mLoggedStartingWindowDrawn) {
            return;
        }
        activeTransitionInfo.mLoggedStartingWindowDrawn = true;
        activeTransitionInfo.mStartingWindowDelayMs = activeTransitionInfo.calculateCurrentDelay();
    }

    public void notifyTransitionStarting(ArrayMap<WindowContainer, Integer> arrayMap) {
        long uptimeNanos = SystemClock.uptimeNanos();
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            WindowContainer keyAt = arrayMap.keyAt(size);
            ActivityRecord asActivityRecord = keyAt.asActivityRecord();
            if (asActivityRecord == null) {
                asActivityRecord = keyAt.getTopActivity(false, true);
            }
            TransitionInfo activeTransitionInfo = getActiveTransitionInfo(asActivityRecord);
            if (activeTransitionInfo != null && !activeTransitionInfo.mLoggedTransitionStarting) {
                activeTransitionInfo.mCurrentTransitionDelayMs = activeTransitionInfo.calculateDelay(uptimeNanos);
                activeTransitionInfo.mReason = arrayMap.valueAt(size).intValue();
                activeTransitionInfo.mLoggedTransitionStarting = true;
                if (activeTransitionInfo.mIsDrawn) {
                    done(false, activeTransitionInfo, "notifyTransitionStarting drawn", uptimeNanos);
                }
            }
        }
    }

    public void notifyActivityRelaunched(ActivityRecord activityRecord) {
        TransitionInfo activeTransitionInfo = getActiveTransitionInfo(activityRecord);
        if (activeTransitionInfo != null) {
            activeTransitionInfo.mRelaunched = true;
        }
    }

    public void notifyActivityRemoved(ActivityRecord activityRecord) {
        this.mLastTransitionInfo.remove(activityRecord);
        TransitionInfo activeTransitionInfo = getActiveTransitionInfo(activityRecord);
        if (activeTransitionInfo != null) {
            abort(activeTransitionInfo, "removed");
        }
        PackageCompatStateInfo packageCompatStateInfo = this.mPackageUidToCompatStateInfo.get(activityRecord.info.applicationInfo.uid);
        if (packageCompatStateInfo == null) {
            return;
        }
        packageCompatStateInfo.mVisibleActivities.remove(activityRecord);
        if (packageCompatStateInfo.mLastLoggedActivity == activityRecord) {
            packageCompatStateInfo.mLastLoggedActivity = null;
        }
    }

    public void notifyVisibilityChanged(ActivityRecord activityRecord) {
        if (getActiveTransitionInfo(activityRecord) == null) {
            return;
        }
        if (activityRecord.isState(ActivityRecord.State.RESUMED) && activityRecord.mDisplayContent.isSleeping()) {
            return;
        }
        if (!activityRecord.isVisibleRequested() || activityRecord.finishing) {
            scheduleCheckActivityToBeDrawn(activityRecord, 0L);
        }
    }

    public final void scheduleCheckActivityToBeDrawn(ActivityRecord activityRecord, long j) {
        activityRecord.mAtmService.f1161mH.sendMessageDelayed(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda4
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((ActivityMetricsLogger) obj).checkActivityToBeDrawn((Task) obj2, (ActivityRecord) obj3);
            }
        }, this, activityRecord.getTask(), activityRecord), j);
    }

    public final void checkActivityToBeDrawn(Task task, ActivityRecord activityRecord) {
        synchronized (this.mSupervisor.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                TransitionInfo activeTransitionInfo = getActiveTransitionInfo(activityRecord);
                if (activeTransitionInfo == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (task != null && task.forAllActivities(new Predicate() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda8
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$checkActivityToBeDrawn$0;
                        lambda$checkActivityToBeDrawn$0 = ActivityMetricsLogger.lambda$checkActivityToBeDrawn$0((ActivityRecord) obj);
                        return lambda$checkActivityToBeDrawn$0;
                    }
                })) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else {
                    logAppTransitionCancel(activeTransitionInfo);
                    abort(activeTransitionInfo, "checkActivityToBeDrawn (invisible or drawn already)");
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public static /* synthetic */ boolean lambda$checkActivityToBeDrawn$0(ActivityRecord activityRecord) {
        return (!activityRecord.isVisibleRequested() || activityRecord.isReportedDrawn() || activityRecord.finishing) ? false : true;
    }

    public final AppHibernationManagerInternal getAppHibernationManagerInternal() {
        if (AppHibernationService.isAppHibernationEnabled()) {
            if (this.mAppHibernationManagerInternal == null) {
                this.mAppHibernationManagerInternal = (AppHibernationManagerInternal) LocalServices.getService(AppHibernationManagerInternal.class);
            }
            return this.mAppHibernationManagerInternal;
        }
        return null;
    }

    public void notifyBeforePackageUnstopped(String str) {
        AppHibernationManagerInternal appHibernationManagerInternal = getAppHibernationManagerInternal();
        if (appHibernationManagerInternal != null) {
            this.mLastHibernationStates.put(str, Boolean.valueOf(appHibernationManagerInternal.isHibernatingGlobally(str)));
        }
    }

    public void notifyBindApplication(ApplicationInfo applicationInfo) {
        for (int size = this.mTransitionInfoList.size() - 1; size >= 0; size--) {
            TransitionInfo transitionInfo = this.mTransitionInfoList.get(size);
            if (transitionInfo.mLastLaunchedActivity.info.applicationInfo == applicationInfo) {
                transitionInfo.mBindApplicationDelayMs = transitionInfo.calculateCurrentDelay();
            }
        }
    }

    public final void abort(LaunchingState launchingState, String str) {
        if (launchingState.mAssociatedTransitionInfo != null) {
            abort(launchingState.mAssociatedTransitionInfo, str);
            return;
        }
        launchingState.stopTrace(true, null);
        launchObserverNotifyIntentFailed(launchingState.mStartUptimeNs);
    }

    public final void abort(TransitionInfo transitionInfo, String str) {
        done(true, transitionInfo, str, 0L);
    }

    public final void done(boolean z, TransitionInfo transitionInfo, String str, long j) {
        transitionInfo.mLaunchingState.stopTrace(z, transitionInfo);
        stopLaunchTrace(transitionInfo);
        Boolean remove = this.mLastHibernationStates.remove(transitionInfo.mLastLaunchedActivity.packageName);
        if (z) {
            this.mLastTransitionInfo.remove(transitionInfo.mLastLaunchedActivity);
            this.mSupervisor.stopWaitingForActivityVisible(transitionInfo.mLastLaunchedActivity);
            launchObserverNotifyActivityLaunchCancelled(transitionInfo);
        } else {
            if (transitionInfo.isInterestingToLoggerAndObserver()) {
                launchObserverNotifyActivityLaunchFinished(transitionInfo, j);
            }
            logAppTransitionFinished(transitionInfo, remove != null ? remove.booleanValue() : false);
            if (transitionInfo.mReason == 5) {
                logRecentsAnimationLatency(transitionInfo);
            }
        }
        this.mTransitionInfoList.remove(transitionInfo);
    }

    public final void logAppTransitionCancel(TransitionInfo transitionInfo) {
        int i = transitionInfo.mTransitionType;
        ActivityRecord activityRecord = transitionInfo.mLastLaunchedActivity;
        LogMaker logMaker = new LogMaker(1144);
        logMaker.setPackageName(activityRecord.packageName);
        logMaker.setType(i);
        logMaker.addTaggedData(871, activityRecord.info.name);
        this.mMetricsLogger.write(logMaker);
        FrameworkStatsLog.write(49, activityRecord.info.applicationInfo.uid, activityRecord.packageName, getAppStartTransitionType(i, transitionInfo.mRelaunched), activityRecord.info.name);
    }

    public final void logAppTransitionFinished(TransitionInfo transitionInfo, final boolean z) {
        final TransitionInfoSnapshot transitionInfoSnapshot = new TransitionInfoSnapshot(transitionInfo);
        if (transitionInfo.isInterestingToLoggerAndObserver()) {
            final long j = transitionInfo.mLaunchingState.mStartUptimeNs;
            final int i = transitionInfo.mCurrentTransitionDelayMs;
            final int i2 = transitionInfo.mProcessState;
            final int i3 = transitionInfo.mProcessOomAdj;
            this.mLoggerHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityMetricsLogger.this.lambda$logAppTransitionFinished$1(j, i, transitionInfoSnapshot, z, i2, i3);
                }
            });
        }
        if (transitionInfoSnapshot.isIntresetedToEventLog()) {
            this.mLoggerHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityMetricsLogger.this.lambda$logAppTransitionFinished$2(transitionInfoSnapshot);
                }
            });
        }
        Runnable runnable = transitionInfo.mPendingFullyDrawn;
        if (runnable != null) {
            runnable.run();
        }
        transitionInfo.mLastLaunchedActivity.info.launchToken = null;
    }

    /* renamed from: logAppTransition */
    public final void lambda$logAppTransitionFinished$1(long j, int i, TransitionInfoSnapshot transitionInfoSnapshot, boolean z, int i2, int i3) {
        boolean z2;
        boolean z3;
        LogMaker logMaker = new LogMaker(761);
        logMaker.setPackageName(transitionInfoSnapshot.packageName);
        logMaker.setType(transitionInfoSnapshot.type);
        logMaker.addTaggedData(871, transitionInfoSnapshot.launchedActivityName);
        boolean isInstantApp = transitionInfoSnapshot.applicationInfo.isInstantApp();
        if (transitionInfoSnapshot.launchedActivityLaunchedFromPackage != null) {
            logMaker.addTaggedData(904, transitionInfoSnapshot.launchedActivityLaunchedFromPackage);
        }
        String str = transitionInfoSnapshot.launchedActivityLaunchToken;
        if (str != null) {
            logMaker.addTaggedData(903, str);
        }
        logMaker.addTaggedData(905, Integer.valueOf(isInstantApp ? 1 : 0));
        TimeUnit timeUnit = TimeUnit.NANOSECONDS;
        logMaker.addTaggedData((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_TOP_SLEEPING, Long.valueOf(timeUnit.toSeconds(j)));
        logMaker.addTaggedData((int) FrameworkStatsLog.f107x9a09c896, Integer.valueOf(i));
        logMaker.setSubtype(transitionInfoSnapshot.reason);
        if (transitionInfoSnapshot.startingWindowDelayMs != -1) {
            logMaker.addTaggedData(321, Integer.valueOf(transitionInfoSnapshot.startingWindowDelayMs));
        }
        if (transitionInfoSnapshot.bindApplicationDelayMs != -1) {
            logMaker.addTaggedData(945, Integer.valueOf(transitionInfoSnapshot.bindApplicationDelayMs));
        }
        logMaker.addTaggedData(322, Integer.valueOf(transitionInfoSnapshot.windowsDrawnDelayMs));
        PackageOptimizationInfo packageOptimizationInfo = transitionInfoSnapshot.getPackageOptimizationInfo(getArtManagerInternal());
        logMaker.addTaggedData(1321, Integer.valueOf(packageOptimizationInfo.getCompilationReason()));
        logMaker.addTaggedData(1320, Integer.valueOf(packageOptimizationInfo.getCompilationFilter()));
        this.mMetricsLogger.write(logMaker);
        String codePath = transitionInfoSnapshot.applicationInfo.getCodePath();
        if (codePath == null || !IncrementalManager.isIncrementalPath(codePath)) {
            z2 = false;
            z3 = false;
        } else {
            z3 = isIncrementalLoading(transitionInfoSnapshot.packageName, transitionInfoSnapshot.userId);
            z2 = true;
        }
        FrameworkStatsLog.write(48, transitionInfoSnapshot.applicationInfo.uid, transitionInfoSnapshot.packageName, getAppStartTransitionType(transitionInfoSnapshot.type, transitionInfoSnapshot.relaunched), transitionInfoSnapshot.launchedActivityName, transitionInfoSnapshot.launchedActivityLaunchedFromPackage, isInstantApp, 0L, transitionInfoSnapshot.reason, i, transitionInfoSnapshot.startingWindowDelayMs, transitionInfoSnapshot.bindApplicationDelayMs, transitionInfoSnapshot.windowsDrawnDelayMs, str, packageOptimizationInfo.getCompilationReason(), packageOptimizationInfo.getCompilationFilter(), transitionInfoSnapshot.sourceType, transitionInfoSnapshot.sourceEventDelayMs, z, z2, z3, transitionInfoSnapshot.launchedActivityName.hashCode(), timeUnit.toMillis(transitionInfoSnapshot.timestampNs), i2, i3, (transitionInfoSnapshot.applicationInfo.flags & 2097152) != 0 ? 2 : 1);
        logAppStartMemoryStateCapture(transitionInfoSnapshot);
    }

    public final boolean isIncrementalLoading(String str, int i) {
        IncrementalStatesInfo incrementalStatesInfo = this.mSupervisor.mService.getPackageManagerInternalLocked().getIncrementalStatesInfo(str, 0, i);
        return incrementalStatesInfo != null && incrementalStatesInfo.isLoading();
    }

    /* renamed from: logAppDisplayed */
    public final void lambda$logAppTransitionFinished$2(TransitionInfoSnapshot transitionInfoSnapshot) {
        EventLog.writeEvent(30009, Integer.valueOf(transitionInfoSnapshot.userId), Integer.valueOf(transitionInfoSnapshot.activityRecordIdHashCode), transitionInfoSnapshot.launchedActivityShortComponentName, Integer.valueOf(transitionInfoSnapshot.windowsDrawnDelayMs));
        StringBuilder sb = this.mStringBuilder;
        sb.setLength(0);
        sb.append("Displayed ");
        sb.append(transitionInfoSnapshot.launchedActivityShortComponentName);
        sb.append(": ");
        TimeUtils.formatDuration(transitionInfoSnapshot.windowsDrawnDelayMs, sb);
        Log.i("ActivityTaskManager", sb.toString());
    }

    public final void logRecentsAnimationLatency(TransitionInfo transitionInfo) {
        final int i = transitionInfo.mSourceEventDelayMs + transitionInfo.mWindowsDrawnDelayMs;
        final ActivityRecord activityRecord = transitionInfo.mLastLaunchedActivity;
        final long j = activityRecord.topResumedStateLossTime;
        final WindowManagerService windowManagerService = this.mSupervisor.mService.mWindowManager;
        final RecentsAnimationController recentsAnimationController = windowManagerService.getRecentsAnimationController();
        this.mLoggerHandler.postDelayed(new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ActivityMetricsLogger.lambda$logRecentsAnimationLatency$3(j, activityRecord, recentsAnimationController, windowManagerService, i);
            }
        }, 300L);
    }

    public static /* synthetic */ void lambda$logRecentsAnimationLatency$3(long j, ActivityRecord activityRecord, Object obj, WindowManagerService windowManagerService, int i) {
        if (j == activityRecord.topResumedStateLossTime && obj == windowManagerService.getRecentsAnimationController()) {
            windowManagerService.mLatencyTracker.logAction(8, i);
        }
    }

    public TransitionInfoSnapshot notifyFullyDrawn(final ActivityRecord activityRecord, final boolean z) {
        long millis;
        final TransitionInfo transitionInfo = this.mLastTransitionInfo.get(activityRecord);
        if (transitionInfo == null) {
            return null;
        }
        if (!transitionInfo.mIsDrawn && transitionInfo.mPendingFullyDrawn == null) {
            transitionInfo.mPendingFullyDrawn = new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityMetricsLogger.this.lambda$notifyFullyDrawn$4(activityRecord, z, transitionInfo);
                }
            };
            return null;
        }
        long uptimeNanos = SystemClock.uptimeNanos();
        if (transitionInfo.mPendingFullyDrawn != null) {
            millis = transitionInfo.mWindowsDrawnDelayMs;
        } else {
            millis = TimeUnit.NANOSECONDS.toMillis(uptimeNanos - transitionInfo.mLaunchingState.mStartUptimeNs);
        }
        final TransitionInfoSnapshot transitionInfoSnapshot = new TransitionInfoSnapshot(transitionInfo, activityRecord, (int) millis);
        if (transitionInfoSnapshot.isIntresetedToEventLog()) {
            this.mLoggerHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityMetricsLogger.this.lambda$notifyFullyDrawn$5(transitionInfoSnapshot);
                }
            });
        }
        this.mLastTransitionInfo.remove(activityRecord);
        if (transitionInfo.isInterestingToLoggerAndObserver()) {
            Trace.traceBegin(64L, "ActivityManager:ReportingFullyDrawn " + transitionInfo.mLastLaunchedActivity.packageName);
            this.mLoggerHandler.post(new Runnable() { // from class: com.android.server.wm.ActivityMetricsLogger$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityMetricsLogger.this.lambda$notifyFullyDrawn$6(transitionInfoSnapshot, z, transitionInfo);
                }
            });
            Trace.traceEnd(64L);
            launchObserverNotifyReportFullyDrawn(transitionInfo, uptimeNanos);
            return transitionInfoSnapshot;
        }
        return transitionInfoSnapshot;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyFullyDrawn$4(ActivityRecord activityRecord, boolean z, TransitionInfo transitionInfo) {
        notifyFullyDrawn(activityRecord, z);
        transitionInfo.mPendingFullyDrawn = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyFullyDrawn$6(TransitionInfoSnapshot transitionInfoSnapshot, boolean z, TransitionInfo transitionInfo) {
        logAppFullyDrawnMetrics(transitionInfoSnapshot, z, transitionInfo.mProcessRunning);
    }

    public final void logAppFullyDrawnMetrics(TransitionInfoSnapshot transitionInfoSnapshot, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        LogMaker logMaker = new LogMaker(1090);
        logMaker.setPackageName(transitionInfoSnapshot.packageName);
        logMaker.addTaggedData(871, transitionInfoSnapshot.launchedActivityName);
        logMaker.addTaggedData(1091, Long.valueOf(transitionInfoSnapshot.windowsFullyDrawnDelayMs));
        logMaker.setType(z ? 13 : 12);
        logMaker.addTaggedData((int) FrameworkStatsLog.f56x60da79b1, Integer.valueOf(z2 ? 1 : 0));
        this.mMetricsLogger.write(logMaker);
        PackageOptimizationInfo packageOptimizationInfo = transitionInfoSnapshot.getPackageOptimizationInfo(getArtManagerInternal());
        String codePath = transitionInfoSnapshot.applicationInfo.getCodePath();
        if (codePath == null || !IncrementalManager.isIncrementalPath(codePath)) {
            z3 = false;
            z4 = false;
        } else {
            z4 = isIncrementalLoading(transitionInfoSnapshot.packageName, transitionInfoSnapshot.userId);
            z3 = true;
        }
        FrameworkStatsLog.write(50, transitionInfoSnapshot.applicationInfo.uid, transitionInfoSnapshot.packageName, z ? 1 : 2, transitionInfoSnapshot.launchedActivityName, z2, transitionInfoSnapshot.windowsFullyDrawnDelayMs, packageOptimizationInfo.getCompilationReason(), packageOptimizationInfo.getCompilationFilter(), transitionInfoSnapshot.sourceType, transitionInfoSnapshot.sourceEventDelayMs, z3, z4, transitionInfoSnapshot.launchedActivityName.hashCode(), TimeUnit.NANOSECONDS.toMillis(transitionInfoSnapshot.timestampNs));
    }

    /* renamed from: logAppFullyDrawn */
    public final void lambda$notifyFullyDrawn$5(TransitionInfoSnapshot transitionInfoSnapshot) {
        StringBuilder sb = this.mStringBuilder;
        sb.setLength(0);
        sb.append("Fully drawn ");
        sb.append(transitionInfoSnapshot.launchedActivityShortComponentName);
        sb.append(": ");
        TimeUtils.formatDuration(transitionInfoSnapshot.windowsFullyDrawnDelayMs, sb);
        Log.i("ActivityTaskManager", sb.toString());
    }

    public void logAbortedBgActivityStart(Intent intent, WindowProcessController windowProcessController, int i, String str, int i2, boolean z, int i3, int i4, boolean z2, boolean z3) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptimeMillis = SystemClock.uptimeMillis();
        LogMaker logMaker = new LogMaker(1513);
        logMaker.setTimestamp(System.currentTimeMillis());
        logMaker.addTaggedData(1514, Integer.valueOf(i));
        logMaker.addTaggedData(1515, str);
        logMaker.addTaggedData(1516, Integer.valueOf(ActivityManager.processStateAmToProto(i2)));
        logMaker.addTaggedData(1517, Integer.valueOf(z ? 1 : 0));
        logMaker.addTaggedData(1518, Integer.valueOf(i3));
        logMaker.addTaggedData(1519, Integer.valueOf(ActivityManager.processStateAmToProto(i4)));
        logMaker.addTaggedData(1520, Integer.valueOf(z2 ? 1 : 0));
        logMaker.addTaggedData(1527, Integer.valueOf(z3 ? 1 : 0));
        if (intent != null) {
            logMaker.addTaggedData(1528, intent.getAction());
            ComponentName component = intent.getComponent();
            if (component != null) {
                logMaker.addTaggedData(1526, component.flattenToShortString());
            }
        }
        if (windowProcessController != null) {
            logMaker.addTaggedData(1529, windowProcessController.mName);
            logMaker.addTaggedData(1530, Integer.valueOf(ActivityManager.processStateAmToProto(windowProcessController.getCurrentProcState())));
            logMaker.addTaggedData(1531, Integer.valueOf(windowProcessController.hasClientActivities() ? 1 : 0));
            logMaker.addTaggedData(1532, Integer.valueOf(windowProcessController.hasForegroundServices() ? 1 : 0));
            logMaker.addTaggedData(1533, Integer.valueOf(windowProcessController.hasForegroundActivities() ? 1 : 0));
            logMaker.addTaggedData(1534, Integer.valueOf(windowProcessController.hasTopUi() ? 1 : 0));
            logMaker.addTaggedData(1535, Integer.valueOf(windowProcessController.hasOverlayUi() ? 1 : 0));
            logMaker.addTaggedData((int) FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM, Integer.valueOf(windowProcessController.hasPendingUiClean() ? 1 : 0));
            if (windowProcessController.getInteractionEventTime() != 0) {
                logMaker.addTaggedData(1537, Long.valueOf(elapsedRealtime - windowProcessController.getInteractionEventTime()));
            }
            if (windowProcessController.getFgInteractionTime() != 0) {
                logMaker.addTaggedData(1538, Long.valueOf(elapsedRealtime - windowProcessController.getFgInteractionTime()));
            }
            if (windowProcessController.getWhenUnimportant() != 0) {
                logMaker.addTaggedData(1539, Long.valueOf(uptimeMillis - windowProcessController.getWhenUnimportant()));
            }
        }
        this.mMetricsLogger.write(logMaker);
    }

    public final void logAppStartMemoryStateCapture(TransitionInfoSnapshot transitionInfoSnapshot) {
        if (transitionInfoSnapshot.processRecord == null) {
            return;
        }
        int pid = transitionInfoSnapshot.processRecord.getPid();
        int i = transitionInfoSnapshot.applicationInfo.uid;
        MemoryStatUtil.MemoryStat readMemoryStatFromFilesystem = MemoryStatUtil.readMemoryStatFromFilesystem(i, pid);
        if (readMemoryStatFromFilesystem == null) {
            return;
        }
        FrameworkStatsLog.write(55, i, transitionInfoSnapshot.processName, transitionInfoSnapshot.launchedActivityName, readMemoryStatFromFilesystem.pgfault, readMemoryStatFromFilesystem.pgmajfault, readMemoryStatFromFilesystem.rssInBytes, readMemoryStatFromFilesystem.cacheInBytes, readMemoryStatFromFilesystem.swapInBytes);
    }

    public void logAppCompatState(ActivityRecord activityRecord) {
        int i = activityRecord.info.applicationInfo.uid;
        int appCompatState = activityRecord.getAppCompatState();
        if (!this.mPackageUidToCompatStateInfo.contains(i)) {
            this.mPackageUidToCompatStateInfo.put(i, new PackageCompatStateInfo());
        }
        PackageCompatStateInfo packageCompatStateInfo = this.mPackageUidToCompatStateInfo.get(i);
        int i2 = packageCompatStateInfo.mLastLoggedState;
        ActivityRecord activityRecord2 = packageCompatStateInfo.mLastLoggedActivity;
        boolean z = appCompatState != 1;
        ArrayList<ActivityRecord> arrayList = packageCompatStateInfo.mVisibleActivities;
        if (z && !arrayList.contains(activityRecord)) {
            arrayList.add(activityRecord);
        } else if (!z) {
            arrayList.remove(activityRecord);
            if (arrayList.isEmpty()) {
                this.mPackageUidToCompatStateInfo.remove(i);
            }
        }
        if (appCompatState == i2) {
            return;
        }
        if (!z && !arrayList.isEmpty()) {
            if (activityRecord2 == null || activityRecord == activityRecord2) {
                findAppCompatStateToLog(packageCompatStateInfo, i);
            }
        } else if (activityRecord2 == null || activityRecord == activityRecord2 || i2 == 1 || i2 == 2) {
            logAppCompatStateInternal(activityRecord, appCompatState, packageCompatStateInfo);
        }
    }

    public final void findAppCompatStateToLog(PackageCompatStateInfo packageCompatStateInfo, int i) {
        ArrayList<ActivityRecord> arrayList = packageCompatStateInfo.mVisibleActivities;
        int i2 = packageCompatStateInfo.mLastLoggedState;
        ActivityRecord activityRecord = null;
        int i3 = 1;
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            ActivityRecord activityRecord2 = arrayList.get(i4);
            int appCompatState = activityRecord2.getAppCompatState();
            if (appCompatState == i2) {
                packageCompatStateInfo.mLastLoggedActivity = activityRecord2;
                return;
            }
            if (appCompatState == 1) {
                Slog.w("ActivityTaskManager", "Visible activity with NOT_VISIBLE App Compat state for package UID: " + i);
            } else if (i3 == 1 || (i3 == 2 && appCompatState != 2)) {
                activityRecord = activityRecord2;
                i3 = appCompatState;
            }
        }
        if (activityRecord == null || i3 == 1) {
            return;
        }
        logAppCompatStateInternal(activityRecord, i3, packageCompatStateInfo);
    }

    public final void logAppCompatStateInternal(ActivityRecord activityRecord, int i, PackageCompatStateInfo packageCompatStateInfo) {
        packageCompatStateInfo.mLastLoggedState = i;
        packageCompatStateInfo.mLastLoggedActivity = activityRecord;
        FrameworkStatsLog.write((int) FrameworkStatsLog.APP_COMPAT_STATE_CHANGED, activityRecord.info.applicationInfo.uid, i, isAppCompateStateChangedToLetterboxed(i) ? activityRecord.mLetterboxUiController.getLetterboxPositionForLogging() : 1);
    }

    public void logLetterboxPositionChange(ActivityRecord activityRecord, int i) {
        int i2 = activityRecord.info.applicationInfo.uid;
        FrameworkStatsLog.write((int) FrameworkStatsLog.LETTERBOX_POSITION_CHANGED, i2, i);
        if (this.mPackageUidToCompatStateInfo.contains(i2)) {
            PackageCompatStateInfo packageCompatStateInfo = this.mPackageUidToCompatStateInfo.get(i2);
            if (activityRecord != packageCompatStateInfo.mLastLoggedActivity) {
                return;
            }
            logAppCompatStateInternal(activityRecord, activityRecord.getAppCompatState(), packageCompatStateInfo);
        }
    }

    public void logCameraCompatControlAppearedEventReported(int i, int i2) {
        if (i != 0) {
            if (i == 1) {
                logCameraCompatControlEventReported(1, i2);
            } else if (i == 2) {
                logCameraCompatControlEventReported(2, i2);
            } else {
                Slog.w("ActivityTaskManager", "Unexpected state in logCameraCompatControlAppearedEventReported: " + i);
            }
        }
    }

    public void logCameraCompatControlClickedEventReported(int i, int i2) {
        if (i == 1) {
            logCameraCompatControlEventReported(4, i2);
        } else if (i == 2) {
            logCameraCompatControlEventReported(3, i2);
        } else if (i == 3) {
            logCameraCompatControlEventReported(5, i2);
        } else {
            Slog.w("ActivityTaskManager", "Unexpected state in logCameraCompatControlAppearedEventReported: " + i);
        }
    }

    public final void logCameraCompatControlEventReported(int i, int i2) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.CAMERA_COMPAT_CONTROL_EVENT_REPORTED, i2, i);
    }

    public final ArtManagerInternal getArtManagerInternal() {
        if (this.mArtManagerInternal == null) {
            this.mArtManagerInternal = (ArtManagerInternal) LocalServices.getService(ArtManagerInternal.class);
        }
        return this.mArtManagerInternal;
    }

    public final void startLaunchTrace(TransitionInfo transitionInfo) {
        if (transitionInfo.mLaunchingState.mTraceName == null) {
            return;
        }
        String str = "launching: " + transitionInfo.mLastLaunchedActivity.packageName;
        transitionInfo.mLaunchTraceName = str;
        Trace.asyncTraceBegin(64L, str, (int) transitionInfo.mLaunchingState.mStartRealtimeNs);
    }

    public final void stopLaunchTrace(TransitionInfo transitionInfo) {
        String str = transitionInfo.mLaunchTraceName;
        if (str == null) {
            return;
        }
        Trace.asyncTraceEnd(64L, str, (int) transitionInfo.mLaunchingState.mStartRealtimeNs);
        transitionInfo.mLaunchTraceName = null;
    }

    public ActivityMetricsLaunchObserverRegistry getLaunchObserverRegistry() {
        return this.mLaunchObserver;
    }

    public final void launchObserverNotifyIntentStarted(Intent intent, long j) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyIntentStarted");
        this.mLaunchObserver.onIntentStarted(intent, j);
        Trace.traceEnd(64L);
    }

    public final void launchObserverNotifyIntentFailed(long j) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyIntentFailed");
        this.mLaunchObserver.onIntentFailed(j);
        Trace.traceEnd(64L);
    }

    public final void launchObserverNotifyActivityLaunched(TransitionInfo transitionInfo) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyActivityLaunched");
        this.mLaunchObserver.onActivityLaunched(transitionInfo.mLaunchingState.mStartUptimeNs, transitionInfo.mLastLaunchedActivity.mActivityComponent, convertTransitionTypeToLaunchObserverTemperature(transitionInfo.mTransitionType));
        Trace.traceEnd(64L);
    }

    public final void launchObserverNotifyReportFullyDrawn(TransitionInfo transitionInfo, long j) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyReportFullyDrawn");
        this.mLaunchObserver.onReportFullyDrawn(transitionInfo.mLaunchingState.mStartUptimeNs, j);
        Trace.traceEnd(64L);
    }

    public final void launchObserverNotifyActivityLaunchCancelled(TransitionInfo transitionInfo) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyActivityLaunchCancelled");
        this.mLaunchObserver.onActivityLaunchCancelled(transitionInfo.mLaunchingState.mStartUptimeNs);
        Trace.traceEnd(64L);
    }

    public final void launchObserverNotifyActivityLaunchFinished(TransitionInfo transitionInfo, long j) {
        Trace.traceBegin(64L, "MetricsLogger:launchObserverNotifyActivityLaunchFinished");
        this.mLaunchObserver.onActivityLaunchFinished(transitionInfo.mLaunchingState.mStartUptimeNs, transitionInfo.mLastLaunchedActivity.mActivityComponent, j);
        Trace.traceEnd(64L);
    }
}
