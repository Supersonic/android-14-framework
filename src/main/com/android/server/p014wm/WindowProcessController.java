package com.android.server.p014wm;

import android.app.ActivityThread;
import android.app.BackgroundStartPrivileges;
import android.app.IActivityController;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.servertransaction.ConfigurationChangeItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.InputConstants;
import android.os.LocaleList;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.Watchdog;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.ActivityTaskManagerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.WindowProcessController */
/* loaded from: classes2.dex */
public class WindowProcessController extends ConfigurationContainer<ConfigurationContainer> implements ConfigurationContainerListener {
    public final ActivityTaskManagerService mAtm;
    public final BackgroundLaunchProcessController mBgLaunchController;
    public ActivityRecord mConfigActivityRecord;
    public volatile boolean mCrashing;
    public volatile int mCurSchedGroup;
    public volatile boolean mDebugging;
    public DisplayArea mDisplayArea;
    public volatile long mFgInteractionTime;
    public volatile boolean mHasActivities;
    public volatile boolean mHasCachedConfiguration;
    public volatile boolean mHasClientActivities;
    public volatile boolean mHasForegroundServices;
    public volatile boolean mHasImeService;
    public volatile boolean mHasOverlayUi;
    public boolean mHasPendingConfigurationChange;
    public volatile boolean mHasRecentTasks;
    public volatile boolean mHasTopUi;
    public ArrayList<ActivityRecord> mInactiveActivities;
    public final ApplicationInfo mInfo;
    public volatile boolean mInstrumenting;
    public volatile boolean mInstrumentingWithBackgroundActivityStartPrivileges;
    public volatile long mInteractionEventTime;
    public volatile boolean mIsActivityConfigOverrideAllowed;
    public volatile long mLastActivityFinishTime;
    public volatile long mLastActivityLaunchTime;
    public final WindowProcessListener mListener;
    public final String mName;
    public volatile boolean mNotResponding;
    public final Object mOwner;
    public int mPauseConfigurationDispatchCount;
    public volatile boolean mPendingUiClean;
    public volatile boolean mPerceptible;
    public volatile boolean mPersistent;
    public volatile int mPid;
    public volatile String mRequiredAbi;
    public boolean mRunningRecentsAnimation;
    public boolean mRunningRemoteAnimation;
    public IApplicationThread mThread;
    public final int mUid;
    public final int mUserId;
    public volatile boolean mUsingWrapper;
    public int mVrThreadTid;
    public volatile long mWhenUnimportant;
    @GuardedBy({"itself"})
    public final ArrayList<String> mPkgList = new ArrayList<>(1);
    public volatile int mCurProcState = 20;
    public volatile int mRepProcState = 20;
    public volatile int mCurAdj = -10000;
    public volatile int mInstrumentationSourceUid = -1;
    public final ArrayList<ActivityRecord> mActivities = new ArrayList<>();
    public final ArrayList<Task> mRecentTasks = new ArrayList<>();
    public ActivityRecord mPreQTopResumedActivity = null;
    public final Configuration mLastReportedConfiguration = new Configuration();
    public int mLastTopActivityDeviceId = 0;
    public final ArrayList<ActivityRecord> mHostActivities = new ArrayList<>();
    public volatile int mActivityStateFlags = GnssNative.GNSS_AIDING_TYPE_ALL;

    /* renamed from: com.android.server.wm.WindowProcessController$ComputeOomAdjCallback */
    /* loaded from: classes2.dex */
    public interface ComputeOomAdjCallback {
        void onOtherActivity();

        void onPausedActivity();

        void onStoppingActivity(boolean z);

        void onVisibleActivity();
    }

    public static /* synthetic */ boolean lambda$updateTopResumingActivityInProcessIfNeeded$0(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        return activityRecord2 == activityRecord;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public ConfigurationContainer getChildAt(int i) {
        return null;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public int getChildCount() {
        return 0;
    }

    public WindowProcessController(final ActivityTaskManagerService activityTaskManagerService, ApplicationInfo applicationInfo, String str, int i, int i2, Object obj, WindowProcessListener windowProcessListener) {
        this.mIsActivityConfigOverrideAllowed = true;
        this.mInfo = applicationInfo;
        this.mName = str;
        this.mUid = i;
        this.mUserId = i2;
        this.mOwner = obj;
        this.mListener = windowProcessListener;
        this.mAtm = activityTaskManagerService;
        Objects.requireNonNull(activityTaskManagerService);
        this.mBgLaunchController = new BackgroundLaunchProcessController(new IntPredicate() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda3
            @Override // java.util.function.IntPredicate
            public final boolean test(int i3) {
                return ActivityTaskManagerService.this.hasActiveVisibleWindow(i3);
            }
        }, activityTaskManagerService.getBackgroundActivityStartCallback());
        if (applicationInfo.packageName.equals(activityTaskManagerService.getSysUiServiceComponentLocked().getPackageName()) || UserHandle.getAppId(i) == 1000) {
            this.mIsActivityConfigOverrideAllowed = false;
        }
        onConfigurationChanged(activityTaskManagerService.getGlobalConfiguration());
        activityTaskManagerService.mPackageConfigPersister.updateConfigIfNeeded(this, i2, applicationInfo.packageName);
    }

    public void setPid(int i) {
        this.mPid = i;
    }

    public int getPid() {
        return this.mPid;
    }

    public void setThread(IApplicationThread iApplicationThread) {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            this.mThread = iApplicationThread;
            if (iApplicationThread != null) {
                setLastReportedConfiguration(getConfiguration());
            } else {
                this.mAtm.mVisibleActivityProcessTracker.removeProcess(this);
            }
        }
    }

    public IApplicationThread getThread() {
        return this.mThread;
    }

    public boolean hasThread() {
        return this.mThread != null;
    }

    public void setCurrentSchedulingGroup(int i) {
        this.mCurSchedGroup = i;
    }

    public int getCurrentSchedulingGroup() {
        return this.mCurSchedGroup;
    }

    public void setCurrentProcState(int i) {
        this.mCurProcState = i;
    }

    public int getCurrentProcState() {
        return this.mCurProcState;
    }

    public void setCurrentAdj(int i) {
        this.mCurAdj = i;
    }

    public int getCurrentAdj() {
        return this.mCurAdj;
    }

    public void setReportedProcState(int i) {
        Configuration configuration;
        int i2 = this.mRepProcState;
        this.mRepProcState = i;
        IApplicationThread iApplicationThread = this.mThread;
        if (i2 < 16 || i >= 16 || iApplicationThread == null || !this.mHasCachedConfiguration) {
            return;
        }
        synchronized (this.mLastReportedConfiguration) {
            configuration = new Configuration(this.mLastReportedConfiguration);
        }
        scheduleConfigurationChange(iApplicationThread, configuration);
    }

    public int getReportedProcState() {
        return this.mRepProcState;
    }

    public void setCrashing(boolean z) {
        this.mCrashing = z;
    }

    public void handleAppCrash() {
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = this.mActivities.get(size);
            Slog.w("ActivityTaskManager", "  Force finishing activity " + activityRecord.mActivityComponent.flattenToShortString());
            activityRecord.detachFromProcess();
            activityRecord.mDisplayContent.requestTransitionAndLegacyPrepare(2, 16);
            activityRecord.destroyIfPossible("handleAppCrashed");
        }
    }

    public boolean isCrashing() {
        return this.mCrashing;
    }

    public void setNotResponding(boolean z) {
        this.mNotResponding = z;
    }

    public boolean isNotResponding() {
        return this.mNotResponding;
    }

    public void setPersistent(boolean z) {
        this.mPersistent = z;
    }

    public boolean isPersistent() {
        return this.mPersistent;
    }

    public void setHasForegroundServices(boolean z) {
        this.mHasForegroundServices = z;
    }

    public boolean hasForegroundServices() {
        return this.mHasForegroundServices;
    }

    public boolean hasForegroundActivities() {
        return this.mAtm.mTopApp == this || (this.mActivityStateFlags & 458752) != 0;
    }

    public void setHasClientActivities(boolean z) {
        this.mHasClientActivities = z;
    }

    public boolean hasClientActivities() {
        return this.mHasClientActivities;
    }

    public void setHasTopUi(boolean z) {
        this.mHasTopUi = z;
    }

    public boolean hasTopUi() {
        return this.mHasTopUi;
    }

    public void setHasOverlayUi(boolean z) {
        this.mHasOverlayUi = z;
    }

    public boolean hasOverlayUi() {
        return this.mHasOverlayUi;
    }

    public void setPendingUiClean(boolean z) {
        this.mPendingUiClean = z;
    }

    public boolean hasPendingUiClean() {
        return this.mPendingUiClean;
    }

    public boolean registeredForDisplayAreaConfigChanges() {
        return this.mDisplayArea != null;
    }

    @VisibleForTesting
    public boolean registeredForActivityConfigChanges() {
        return this.mConfigActivityRecord != null;
    }

    public void postPendingUiCleanMsg(boolean z) {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda11
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((WindowProcessListener) obj).setPendingUiClean(((Boolean) obj2).booleanValue());
            }
        }, this.mListener, Boolean.valueOf(z)));
    }

    public void setInteractionEventTime(long j) {
        this.mInteractionEventTime = j;
    }

    public long getInteractionEventTime() {
        return this.mInteractionEventTime;
    }

    public void setFgInteractionTime(long j) {
        this.mFgInteractionTime = j;
    }

    public long getFgInteractionTime() {
        return this.mFgInteractionTime;
    }

    public void setWhenUnimportant(long j) {
        this.mWhenUnimportant = j;
    }

    public long getWhenUnimportant() {
        return this.mWhenUnimportant;
    }

    public void setRequiredAbi(String str) {
        this.mRequiredAbi = str;
    }

    public String getRequiredAbi() {
        return this.mRequiredAbi;
    }

    @VisibleForTesting
    public DisplayArea getDisplayArea() {
        return this.mDisplayArea;
    }

    public void setDebugging(boolean z) {
        this.mDebugging = z;
    }

    public void setUsingWrapper(boolean z) {
        this.mUsingWrapper = z;
    }

    public boolean isUsingWrapper() {
        return this.mUsingWrapper;
    }

    public boolean hasEverLaunchedActivity() {
        return this.mLastActivityLaunchTime > 0;
    }

    public void setLastActivityLaunchTime(long j) {
        if (j <= this.mLastActivityLaunchTime) {
            if (j < this.mLastActivityLaunchTime) {
                Slog.w("ActivityTaskManager", "Tried to set launchTime (" + j + ") < mLastActivityLaunchTime (" + this.mLastActivityLaunchTime + ")");
                return;
            }
            return;
        }
        this.mLastActivityLaunchTime = j;
    }

    public void setLastActivityFinishTimeIfNeeded(long j) {
        if (j <= this.mLastActivityFinishTime || !hasActivityInVisibleTask()) {
            return;
        }
        this.mLastActivityFinishTime = j;
    }

    public void addOrUpdateBackgroundStartPrivileges(Binder binder, BackgroundStartPrivileges backgroundStartPrivileges) {
        this.mBgLaunchController.addOrUpdateAllowBackgroundStartPrivileges(binder, backgroundStartPrivileges);
    }

    public void removeBackgroundStartPrivileges(Binder binder) {
        this.mBgLaunchController.removeAllowBackgroundStartPrivileges(binder);
    }

    public boolean areBackgroundFgsStartsAllowed() {
        return areBackgroundActivityStartsAllowed(this.mAtm.getBalAppSwitchesState(), true) != 0;
    }

    public int areBackgroundActivityStartsAllowed(int i) {
        return areBackgroundActivityStartsAllowed(i, false);
    }

    public final int areBackgroundActivityStartsAllowed(int i, boolean z) {
        return this.mBgLaunchController.areBackgroundActivityStartsAllowed(this.mPid, this.mUid, this.mInfo.packageName, i, z, hasActivityInVisibleTask(), this.mInstrumentingWithBackgroundActivityStartPrivileges, this.mAtm.getLastStopAppSwitchesTime(), this.mLastActivityLaunchTime, this.mLastActivityFinishTime);
    }

    public boolean canCloseSystemDialogsByToken() {
        return this.mBgLaunchController.canCloseSystemDialogsByToken(this.mUid);
    }

    public void clearBoundClientUids() {
        this.mBgLaunchController.clearBalOptInBoundClientUids();
    }

    public void addBoundClientUid(int i, String str, long j) {
        this.mBgLaunchController.addBoundClientUid(i, str, j);
    }

    public void setInstrumenting(boolean z, int i, boolean z2) {
        Preconditions.checkArgument(z || i == -1);
        this.mInstrumenting = z;
        this.mInstrumentationSourceUid = i;
        this.mInstrumentingWithBackgroundActivityStartPrivileges = z2;
    }

    public boolean isInstrumenting() {
        return this.mInstrumenting;
    }

    public int getInstrumentationSourceUid() {
        return this.mInstrumentationSourceUid;
    }

    public void setPerceptible(boolean z) {
        this.mPerceptible = z;
    }

    public boolean isPerceptible() {
        return this.mPerceptible;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public ConfigurationContainer getParent() {
        return this.mAtm.mRootWindowContainer;
    }

    public void addPackage(String str) {
        synchronized (this.mPkgList) {
            if (!this.mPkgList.contains(str)) {
                this.mPkgList.add(str);
            }
        }
    }

    public void clearPackageList() {
        synchronized (this.mPkgList) {
            this.mPkgList.clear();
        }
    }

    public boolean containsPackage(String str) {
        boolean contains;
        synchronized (this.mPkgList) {
            contains = this.mPkgList.contains(str);
        }
        return contains;
    }

    public void addActivityIfNeeded(ActivityRecord activityRecord) {
        setLastActivityLaunchTime(activityRecord.lastLaunchTime);
        if (this.mActivities.contains(activityRecord)) {
            return;
        }
        this.mActivities.add(activityRecord);
        this.mHasActivities = true;
        ArrayList<ActivityRecord> arrayList = this.mInactiveActivities;
        if (arrayList != null) {
            arrayList.remove(activityRecord);
        }
        updateActivityConfigurationListener();
    }

    public void removeActivity(ActivityRecord activityRecord, boolean z) {
        if (z) {
            ArrayList<ActivityRecord> arrayList = this.mInactiveActivities;
            if (arrayList == null) {
                ArrayList<ActivityRecord> arrayList2 = new ArrayList<>();
                this.mInactiveActivities = arrayList2;
                arrayList2.add(activityRecord);
            } else if (!arrayList.contains(activityRecord)) {
                this.mInactiveActivities.add(activityRecord);
            }
        } else {
            ArrayList<ActivityRecord> arrayList3 = this.mInactiveActivities;
            if (arrayList3 != null) {
                arrayList3.remove(activityRecord);
            }
        }
        this.mActivities.remove(activityRecord);
        this.mHasActivities = !this.mActivities.isEmpty();
        updateActivityConfigurationListener();
    }

    public void clearActivities() {
        this.mInactiveActivities = null;
        this.mActivities.clear();
        this.mHasActivities = false;
        updateActivityConfigurationListener();
    }

    public boolean hasActivities() {
        return this.mHasActivities;
    }

    public boolean hasVisibleActivities() {
        return (this.mActivityStateFlags & 65536) != 0;
    }

    public boolean hasActivityInVisibleTask() {
        return (this.mActivityStateFlags & 4194304) != 0;
    }

    public boolean hasActivitiesOrRecentTasks() {
        return this.mHasActivities || this.mHasRecentTasks;
    }

    public TaskDisplayArea getTopActivityDisplayArea() {
        if (this.mActivities.isEmpty()) {
            return null;
        }
        int size = this.mActivities.size() - 1;
        ActivityRecord activityRecord = this.mActivities.get(size);
        TaskDisplayArea displayArea = activityRecord.getDisplayArea();
        for (int i = size - 1; i >= 0; i--) {
            ActivityRecord activityRecord2 = this.mActivities.get(i);
            TaskDisplayArea displayArea2 = activityRecord2.getDisplayArea();
            if (activityRecord2.compareTo((WindowContainer) activityRecord) > 0 && displayArea2 != null) {
                activityRecord = activityRecord2;
                displayArea = displayArea2;
            }
        }
        return displayArea;
    }

    public boolean updateTopResumingActivityInProcessIfNeeded(final ActivityRecord activityRecord) {
        TaskFragment taskFragment;
        ActivityRecord activity;
        boolean z = true;
        if (this.mInfo.targetSdkVersion < 29 && this.mPreQTopResumedActivity != activityRecord) {
            if (!activityRecord.isAttached()) {
                return false;
            }
            ActivityRecord activityRecord2 = this.mPreQTopResumedActivity;
            DisplayContent displayContent = (activityRecord2 == null || !activityRecord2.isAttached()) ? null : this.mPreQTopResumedActivity.mDisplayContent;
            boolean z2 = (displayContent != null && this.mPreQTopResumedActivity.isVisibleRequested() && this.mPreQTopResumedActivity.isFocusable()) ? false : true;
            DisplayContent displayContent2 = activityRecord.mDisplayContent;
            if (!z2 && displayContent.compareTo((WindowContainer) displayContent2) < 0) {
                z2 = true;
            }
            if (z2 || (activity = displayContent.getActivity(new Predicate() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateTopResumingActivityInProcessIfNeeded$0;
                    lambda$updateTopResumingActivityInProcessIfNeeded$0 = WindowProcessController.lambda$updateTopResumingActivityInProcessIfNeeded$0(ActivityRecord.this, (ActivityRecord) obj);
                    return lambda$updateTopResumingActivityInProcessIfNeeded$0;
                }
            }, true, this.mPreQTopResumedActivity)) == null || activity == this.mPreQTopResumedActivity) {
                z = z2;
            }
            if (z) {
                ActivityRecord activityRecord3 = this.mPreQTopResumedActivity;
                if (activityRecord3 != null && activityRecord3.isState(ActivityRecord.State.RESUMED) && (taskFragment = this.mPreQTopResumedActivity.getTaskFragment()) != null) {
                    taskFragment.startPausing(taskFragment.shouldBeVisible(null), false, activityRecord, "top-resumed-changed");
                }
                this.mPreQTopResumedActivity = activityRecord;
            }
        }
        return z;
    }

    public void stopFreezingActivities() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int size = this.mActivities.size();
                while (size > 0) {
                    size--;
                    this.mActivities.get(size).stopFreezingScreenLocked(true);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void finishActivities() {
        ArrayList arrayList = new ArrayList(this.mActivities);
        for (int i = 0; i < arrayList.size(); i++) {
            ActivityRecord activityRecord = (ActivityRecord) arrayList.get(i);
            if (!activityRecord.finishing && activityRecord.isInRootTaskLocked()) {
                activityRecord.finishIfPossible("finish-heavy", true);
            }
        }
    }

    public boolean isInterestingToUser() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int size = this.mActivities.size();
                for (int i = 0; i < size; i++) {
                    if (this.mActivities.get(i).isInterestingToUserLocked()) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                }
                if (isEmbedded()) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return true;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final boolean isEmbedded() {
        for (int size = this.mHostActivities.size() - 1; size >= 0; size--) {
            if (this.mHostActivities.get(size).isInterestingToUserLocked()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRunningActivity(String str) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int size = this.mActivities.size() - 1; size >= 0; size--) {
                    if (str.equals(this.mActivities.get(size).packageName)) {
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

    public void updateAppSpecificSettingsForAllActivitiesInPackage(String str, Integer num, LocaleList localeList, @Configuration.GrammaticalGender int i) {
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = this.mActivities.get(size);
            if (str.equals(activityRecord.packageName) && activityRecord.applyAppSpecificConfig(num, localeList, Integer.valueOf(i)) && activityRecord.isVisibleRequested()) {
                activityRecord.ensureActivityConfiguration(0, true);
            }
        }
    }

    public void clearPackagePreferredForHomeActivities() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int size = this.mActivities.size() - 1; size >= 0; size--) {
                    ActivityRecord activityRecord = this.mActivities.get(size);
                    if (activityRecord.isActivityTypeHome()) {
                        Log.i("ActivityTaskManager", "Clearing package preferred activities from " + activityRecord.packageName);
                        try {
                            ActivityThread.getPackageManager().clearPackagePreferredActivities(activityRecord.packageName);
                        } catch (RemoteException unused) {
                        }
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean hasStartedActivity(ActivityRecord activityRecord) {
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord2 = this.mActivities.get(size);
            if (activityRecord != activityRecord2 && !activityRecord2.mAppStopped) {
                return true;
            }
        }
        return false;
    }

    public boolean hasResumedActivity() {
        return (this.mActivityStateFlags & 2097152) != 0;
    }

    public void updateIntentForHeavyWeightActivity(Intent intent) {
        if (this.mActivities.isEmpty()) {
            return;
        }
        ActivityRecord activityRecord = this.mActivities.get(0);
        intent.putExtra("cur_app", activityRecord.packageName);
        intent.putExtra("cur_task", activityRecord.getTask().mTaskId);
    }

    public boolean shouldKillProcessForRemovedTask(Task task) {
        for (int i = 0; i < this.mActivities.size(); i++) {
            ActivityRecord activityRecord = this.mActivities.get(i);
            if (!activityRecord.mAppStopped) {
                return false;
            }
            Task task2 = activityRecord.getTask();
            if (task.mTaskId != task2.mTaskId && task2.inRecents) {
                return false;
            }
        }
        return true;
    }

    public void releaseSomeActivities(String str) {
        ArrayList arrayList = null;
        for (int i = 0; i < this.mActivities.size(); i++) {
            ActivityRecord activityRecord = this.mActivities.get(i);
            if (activityRecord.finishing || activityRecord.isState(ActivityRecord.State.DESTROYING, ActivityRecord.State.DESTROYED)) {
                return;
            }
            if (!activityRecord.isVisibleRequested() && activityRecord.mAppStopped && activityRecord.hasSavedState() && activityRecord.isDestroyable() && !activityRecord.isState(ActivityRecord.State.STARTED, ActivityRecord.State.RESUMED, ActivityRecord.State.PAUSING, ActivityRecord.State.PAUSED, ActivityRecord.State.STOPPING) && activityRecord.getParent() != null) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(activityRecord);
            }
        }
        if (arrayList != null) {
            arrayList.sort(new Comparator() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda5
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ((ActivityRecord) obj).compareTo((WindowContainer) ((ActivityRecord) obj2));
                }
            });
            int max = Math.max(arrayList.size(), 1);
            do {
                ((ActivityRecord) arrayList.remove(0)).destroyImmediately(str);
                max--;
            } while (max > 0);
        }
    }

    public void getDisplayContextsWithErrorDialogs(List<Context> list) {
        if (list == null) {
            return;
        }
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                RootWindowContainer rootWindowContainer = this.mAtm.mWindowManager.mRoot;
                rootWindowContainer.getDisplayContextsWithNonToastVisibleWindows(this.mPid, list);
                for (int size = this.mActivities.size() - 1; size >= 0; size--) {
                    ActivityRecord activityRecord = this.mActivities.get(size);
                    Context displayUiContext = rootWindowContainer.getDisplayUiContext(activityRecord.getDisplayId());
                    if (displayUiContext != null && activityRecord.isVisibleRequested() && !list.contains(displayUiContext)) {
                        list.add(displayUiContext);
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void addHostActivity(ActivityRecord activityRecord) {
        if (this.mHostActivities.contains(activityRecord)) {
            return;
        }
        this.mHostActivities.add(activityRecord);
    }

    public void removeHostActivity(ActivityRecord activityRecord) {
        this.mHostActivities.remove(activityRecord);
    }

    public int computeOomAdjFromActivities(ComputeOomAdjCallback computeOomAdjCallback) {
        int i = this.mActivityStateFlags;
        if ((65536 & i) != 0) {
            computeOomAdjCallback.onVisibleActivity();
        } else if ((131072 & i) != 0) {
            computeOomAdjCallback.onPausedActivity();
        } else if ((262144 & i) != 0) {
            computeOomAdjCallback.onStoppingActivity((524288 & i) != 0);
        } else {
            computeOomAdjCallback.onOtherActivity();
        }
        return i & GnssNative.GNSS_AIDING_TYPE_ALL;
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x00a2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void computeProcessActivityState() {
        int i;
        ActivityRecord.State state;
        int i2;
        ActivityRecord.State state2 = ActivityRecord.State.DESTROYED;
        boolean hasResumedActivity = hasResumedActivity();
        boolean z = (this.mActivityStateFlags & 1114112) != 0;
        int i3 = Integer.MAX_VALUE;
        boolean z2 = true;
        int i4 = 0;
        boolean z3 = false;
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = this.mActivities.get(size);
            if (activityRecord.isVisible()) {
                i4 |= 1048576;
            }
            Task task = activityRecord.getTask();
            if (task != null && task.mLayerRank != -1) {
                i4 |= 4194304;
            }
            if (activityRecord.isVisibleRequested()) {
                if (activityRecord.isState(ActivityRecord.State.RESUMED)) {
                    i4 |= 2097152;
                }
                if (task != null && i3 > 0 && (i2 = task.mLayerRank) >= 0 && i3 > i2) {
                    i3 = i2;
                }
                z3 = true;
            } else if (!z3 && state2 != (state = ActivityRecord.State.PAUSING)) {
                if (!activityRecord.isState(state, ActivityRecord.State.PAUSED)) {
                    state = ActivityRecord.State.STOPPING;
                    if (activityRecord.isState(state)) {
                        z2 &= activityRecord.finishing;
                    }
                }
                state2 = state;
            }
        }
        int i5 = (65535 & i3) | i4;
        if (z3) {
            i = 65536;
        } else if (state2 != ActivityRecord.State.PAUSING) {
            if (state2 == ActivityRecord.State.STOPPING) {
                i5 |= 262144;
                if (z2) {
                    i = 524288;
                }
            }
            this.mActivityStateFlags = i5;
            boolean z4 = (i5 & 1114112) != 0;
            if (z && z4) {
                this.mAtm.mVisibleActivityProcessTracker.onAnyActivityVisible(this);
                return;
            } else if (!z && !z4) {
                this.mAtm.mVisibleActivityProcessTracker.onAllActivitiesInvisible(this);
                return;
            } else if (z || hasResumedActivity || !hasResumedActivity()) {
                return;
            } else {
                this.mAtm.mVisibleActivityProcessTracker.onActivityResumedWhileVisible(this);
                return;
            }
        } else {
            i = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
        }
        i5 |= i;
        this.mActivityStateFlags = i5;
        if ((i5 & 1114112) != 0) {
        }
        if (z) {
        }
        if (!z) {
        }
        if (z) {
        }
    }

    public final void prepareOomAdjustment() {
        this.mAtm.mRootWindowContainer.rankTaskLayers();
        this.mAtm.mTaskSupervisor.computeProcessActivityStateBatch();
    }

    public int computeRelaunchReason() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int size = this.mActivities.size() - 1; size >= 0; size--) {
                    int i = this.mActivities.get(size).mRelaunchReason;
                    if (i != 0) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return i;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return 0;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public long getInputDispatchingTimeoutMillis() {
        long j;
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                j = (isInstrumenting() || isUsingWrapper()) ? 60000L : InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return j;
    }

    public void clearProfilerIfNeeded() {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowProcessListener) obj).clearProfilerIfNeeded();
            }
        }, this.mListener));
    }

    public void updateProcessInfo(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z4) {
            addToPendingTop();
        }
        if (z3) {
            prepareOomAdjustment();
        }
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new WindowProcessController$$ExternalSyntheticLambda4(), this.mListener, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3)));
    }

    public void scheduleUpdateOomAdj() {
        ActivityTaskManagerService.HandlerC1840H handlerC1840H = this.mAtm.f1161mH;
        WindowProcessController$$ExternalSyntheticLambda4 windowProcessController$$ExternalSyntheticLambda4 = new WindowProcessController$$ExternalSyntheticLambda4();
        WindowProcessListener windowProcessListener = this.mListener;
        Boolean bool = Boolean.FALSE;
        handlerC1840H.sendMessage(PooledLambda.obtainMessage(windowProcessController$$ExternalSyntheticLambda4, windowProcessListener, bool, bool, Boolean.TRUE));
    }

    public void addToPendingTop() {
        this.mAtm.mAmInternal.addPendingTopUid(this.mUid, this.mPid, this.mThread);
    }

    public void updateServiceConnectionActivities() {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowProcessListener) obj).updateServiceConnectionActivities();
            }
        }, this.mListener));
    }

    public void setPendingUiCleanAndForceProcessStateUpTo(int i) {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda10
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((WindowProcessListener) obj).setPendingUiCleanAndForceProcessStateUpTo(((Integer) obj2).intValue());
            }
        }, this.mListener, Integer.valueOf(i)));
    }

    public boolean isRemoved() {
        return this.mListener.isRemoved();
    }

    public final boolean shouldSetProfileProc() {
        WindowProcessController windowProcessController;
        String str = this.mAtm.mProfileApp;
        return str != null && str.equals(this.mName) && ((windowProcessController = this.mAtm.mProfileProc) == null || windowProcessController == this);
    }

    public ProfilerInfo createProfilerInfoIfNeeded() {
        ProfilerInfo profilerInfo = this.mAtm.mProfilerInfo;
        if (profilerInfo == null || profilerInfo.profileFile == null || !shouldSetProfileProc()) {
            return null;
        }
        ParcelFileDescriptor parcelFileDescriptor = profilerInfo.profileFd;
        if (parcelFileDescriptor != null) {
            try {
                profilerInfo.profileFd = parcelFileDescriptor.dup();
            } catch (IOException unused) {
                profilerInfo.closeFd();
            }
        }
        return new ProfilerInfo(profilerInfo);
    }

    public void onStartActivity(int i, ActivityInfo activityInfo) {
        String str = ((activityInfo.flags & 1) == 0 || !PackageManagerShellCommandDataLoader.PACKAGE.equals(activityInfo.packageName)) ? activityInfo.packageName : null;
        if (i == 2) {
            this.mAtm.mAmInternal.addPendingTopUid(this.mUid, this.mPid, this.mThread);
        }
        prepareOomAdjustment();
        this.mAtm.f1161mH.sendMessageAtFrontOfQueue(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda7
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ((WindowProcessListener) obj).onStartActivity(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue(), (String) obj4, ((Long) obj5).longValue());
            }
        }, this.mListener, Integer.valueOf(i), Boolean.valueOf(shouldSetProfileProc()), str, Long.valueOf(activityInfo.applicationInfo.longVersionCode)));
    }

    public void appDied(String str) {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda9
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((WindowProcessListener) obj).appDied((String) obj2);
            }
        }, this.mListener, str));
    }

    public boolean handleAppDied() {
        this.mAtm.mTaskSupervisor.removeHistoryRecords(this);
        ArrayList<ActivityRecord> arrayList = this.mInactiveActivities;
        boolean z = false;
        boolean z2 = (arrayList == null || arrayList.isEmpty()) ? false : true;
        ArrayList<ActivityRecord> arrayList2 = (this.mHasActivities || z2) ? new ArrayList<>() : this.mActivities;
        if (this.mHasActivities) {
            arrayList2.addAll(this.mActivities);
        }
        if (z2) {
            arrayList2.addAll(this.mInactiveActivities);
        }
        if (isRemoved()) {
            for (int size = arrayList2.size() - 1; size >= 0; size--) {
                arrayList2.get(size).makeFinishingLocked();
            }
        }
        for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
            ActivityRecord activityRecord = arrayList2.get(size2);
            z = (activityRecord.isVisibleRequested() || activityRecord.isVisible()) ? true : true;
            TaskFragment taskFragment = activityRecord.getTaskFragment();
            if (taskFragment != null) {
                z |= taskFragment.handleAppDied(this);
            }
            activityRecord.handleAppDied();
        }
        clearRecentTasks();
        clearActivities();
        return z;
    }

    public void registerDisplayAreaConfigurationListener(DisplayArea displayArea) {
        if (displayArea == null || displayArea.containsListener(this)) {
            return;
        }
        unregisterConfigurationListeners();
        this.mDisplayArea = displayArea;
        displayArea.registerConfigurationChangeListener(this);
    }

    @VisibleForTesting
    public void unregisterDisplayAreaConfigurationListener() {
        DisplayArea displayArea = this.mDisplayArea;
        if (displayArea == null) {
            return;
        }
        displayArea.unregisterConfigurationChangeListener(this);
        this.mDisplayArea = null;
        onMergedOverrideConfigurationChanged(Configuration.EMPTY);
    }

    public void registerActivityConfigurationListener(ActivityRecord activityRecord) {
        if (activityRecord == null || activityRecord.containsListener(this) || !this.mIsActivityConfigOverrideAllowed) {
            return;
        }
        unregisterConfigurationListeners();
        this.mConfigActivityRecord = activityRecord;
        activityRecord.registerConfigurationChangeListener(this);
    }

    public final void unregisterActivityConfigurationListener() {
        ActivityRecord activityRecord = this.mConfigActivityRecord;
        if (activityRecord == null) {
            return;
        }
        activityRecord.unregisterConfigurationChangeListener(this);
        this.mConfigActivityRecord = null;
        onMergedOverrideConfigurationChanged(Configuration.EMPTY);
    }

    public final void unregisterConfigurationListeners() {
        unregisterActivityConfigurationListener();
        unregisterDisplayAreaConfigurationListener();
    }

    public final void updateActivityConfigurationListener() {
        if (this.mIsActivityConfigOverrideAllowed) {
            for (int size = this.mActivities.size() - 1; size >= 0; size--) {
                ActivityRecord activityRecord = this.mActivities.get(size);
                if (!activityRecord.finishing) {
                    registerActivityConfigurationListener(activityRecord);
                    return;
                }
            }
            unregisterActivityConfigurationListener();
        }
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void onConfigurationChanged(Configuration configuration) {
        boolean z;
        super.onConfigurationChanged(configuration);
        int topActivityDeviceId = getTopActivityDeviceId();
        if (topActivityDeviceId != this.mLastTopActivityDeviceId) {
            this.mLastTopActivityDeviceId = topActivityDeviceId;
            z = true;
        } else {
            z = false;
        }
        Configuration configuration2 = getConfiguration();
        if ((!z) & this.mLastReportedConfiguration.equals(configuration2)) {
            if (Build.IS_DEBUGGABLE && this.mHasImeService) {
                Slog.w("ActivityTaskManager", "Current config: " + configuration2 + " unchanged for IME proc " + this.mName);
            }
        } else if (this.mPauseConfigurationDispatchCount > 0) {
            this.mHasPendingConfigurationChange = true;
        } else {
            dispatchConfiguration(configuration2);
        }
    }

    public final int getTopActivityDeviceId() {
        DisplayContent displayContent;
        ActivityRecord topNonFinishingActivity = getTopNonFinishingActivity();
        if (topNonFinishingActivity == null || (displayContent = topNonFinishingActivity.mDisplayContent) == null) {
            return 0;
        }
        return this.mAtm.mTaskSupervisor.getDeviceIdForDisplayId(displayContent.mDisplayId);
    }

    public final ActivityRecord getTopNonFinishingActivity() {
        if (this.mActivities.isEmpty()) {
            return null;
        }
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            if (!this.mActivities.get(size).finishing) {
                return this.mActivities.get(size);
            }
        }
        return null;
    }

    @Override // com.android.server.p014wm.ConfigurationContainerListener
    public void onMergedOverrideConfigurationChanged(Configuration configuration) {
        super.onRequestedOverrideConfigurationChanged(configuration);
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void resolveOverrideConfiguration(Configuration configuration) {
        Configuration requestedOverrideConfiguration = getRequestedOverrideConfiguration();
        int i = requestedOverrideConfiguration.assetsSeq;
        if (i != 0 && configuration.assetsSeq > i) {
            requestedOverrideConfiguration.assetsSeq = 0;
        }
        super.resolveOverrideConfiguration(configuration);
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        resolvedOverrideConfiguration.windowConfiguration.setActivityType(0);
        resolvedOverrideConfiguration.seq = configuration.seq;
    }

    public void dispatchConfiguration(Configuration configuration) {
        this.mHasPendingConfigurationChange = false;
        if (this.mThread == null) {
            if (Build.IS_DEBUGGABLE && this.mHasImeService) {
                Slog.w("ActivityTaskManager", "Unable to send config for IME proc " + this.mName + ": no app thread");
                return;
            }
            return;
        }
        configuration.seq = this.mAtm.increaseConfigurationSeqLocked();
        setLastReportedConfiguration(configuration);
        if (this.mRepProcState >= 16) {
            this.mHasCachedConfiguration = true;
            if (this.mRepProcState >= 16) {
                return;
            }
        }
        scheduleConfigurationChange(this.mThread, configuration);
    }

    public final void scheduleConfigurationChange(IApplicationThread iApplicationThread, Configuration configuration) {
        if (ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1049367566, 0, (String) null, new Object[]{String.valueOf(this.mName), String.valueOf(configuration)});
        }
        if (Build.IS_DEBUGGABLE && this.mHasImeService) {
            Slog.v("ActivityTaskManager", "Sending to IME proc " + this.mName + " new config " + configuration);
        }
        this.mHasCachedConfiguration = false;
        try {
            this.mAtm.getLifecycleManager().scheduleTransaction(iApplicationThread, ConfigurationChangeItem.obtain(configuration, this.mLastTopActivityDeviceId));
        } catch (Exception e) {
            Slog.e("ActivityTaskManager", "Failed to schedule configuration change: " + this.mOwner, e);
        }
    }

    public void setLastReportedConfiguration(Configuration configuration) {
        synchronized (this.mLastReportedConfiguration) {
            this.mLastReportedConfiguration.setTo(configuration);
        }
    }

    public void pauseConfigurationDispatch() {
        this.mPauseConfigurationDispatchCount++;
    }

    public boolean resumeConfigurationDispatch() {
        int i = this.mPauseConfigurationDispatchCount;
        if (i == 0) {
            return false;
        }
        this.mPauseConfigurationDispatchCount = i - 1;
        return this.mHasPendingConfigurationChange;
    }

    public void updateAssetConfiguration(int i) {
        if (!this.mHasActivities || !this.mIsActivityConfigOverrideAllowed) {
            Configuration configuration = new Configuration(getRequestedOverrideConfiguration());
            configuration.assetsSeq = i;
            onRequestedOverrideConfigurationChanged(configuration);
            return;
        }
        for (int size = this.mActivities.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = this.mActivities.get(size);
            Configuration configuration2 = new Configuration(activityRecord.getRequestedOverrideConfiguration());
            configuration2.assetsSeq = i;
            activityRecord.onRequestedOverrideConfigurationChanged(configuration2);
            if (activityRecord.isVisibleRequested()) {
                activityRecord.ensureActivityConfiguration(0, true);
            }
        }
    }

    public Configuration prepareConfigurationForLaunchingActivity() {
        Configuration configuration = getConfiguration();
        if (this.mHasPendingConfigurationChange) {
            this.mHasPendingConfigurationChange = false;
            configuration.seq = this.mAtm.increaseConfigurationSeqLocked();
        }
        this.mHasCachedConfiguration = false;
        return configuration;
    }

    public long getCpuTime() {
        return this.mListener.getCpuTime();
    }

    public void addRecentTask(Task task) {
        this.mRecentTasks.add(task);
        this.mHasRecentTasks = true;
    }

    public void removeRecentTask(Task task) {
        this.mRecentTasks.remove(task);
        this.mHasRecentTasks = !this.mRecentTasks.isEmpty();
    }

    public boolean hasRecentTasks() {
        return this.mHasRecentTasks;
    }

    public void clearRecentTasks() {
        for (int size = this.mRecentTasks.size() - 1; size >= 0; size--) {
            this.mRecentTasks.get(size).clearRootProcess();
        }
        this.mRecentTasks.clear();
        this.mHasRecentTasks = false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0022, code lost:
        if (r5.mPid != com.android.server.p014wm.WindowManagerService.MY_PID) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void appEarlyNotResponding(String str, Runnable runnable) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                IActivityController iActivityController = this.mAtm.mController;
                if (iActivityController == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Runnable runnable2 = null;
                try {
                    if (iActivityController.appEarlyNotResponding(this.mName, this.mPid, str) < 0) {
                    }
                    runnable = null;
                    runnable2 = runnable;
                } catch (RemoteException unused) {
                    this.mAtm.mController = null;
                    Watchdog.getInstance().setActivityController(null);
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                if (runnable2 != null) {
                    runnable2.run();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0025, code lost:
        if (r6.mPid != com.android.server.p014wm.WindowManagerService.MY_PID) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean appNotResponding(String str, Runnable runnable, Runnable runnable2) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                IActivityController iActivityController = this.mAtm.mController;
                if (iActivityController == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                try {
                    int appNotResponding = iActivityController.appNotResponding(this.mName, this.mPid, str);
                    if (appNotResponding != 0) {
                        if (appNotResponding < 0) {
                        }
                        runnable = runnable2;
                    } else {
                        runnable = null;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (runnable != null) {
                        runnable.run();
                        return true;
                    }
                    return false;
                } catch (RemoteException unused) {
                    this.mAtm.mController = null;
                    Watchdog.getInstance().setActivityController(null);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void onServiceStarted(ServiceInfo serviceInfo) {
        String str = serviceInfo.permission;
        if (str == null) {
            return;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -769871357:
                if (str.equals("android.permission.BIND_VOICE_INTERACTION")) {
                    c = 0;
                    break;
                }
                break;
            case 1412417858:
                if (str.equals("android.permission.BIND_ACCESSIBILITY_SERVICE")) {
                    c = 1;
                    break;
                }
                break;
            case 1448369304:
                if (str.equals("android.permission.BIND_INPUT_METHOD")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                break;
            default:
                return;
            case 2:
                this.mHasImeService = true;
                break;
        }
        this.mIsActivityConfigOverrideAllowed = false;
        unregisterActivityConfigurationListener();
    }

    public void onTopProcChanged() {
        if (this.mAtm.mVrController.isInterestingToSchedGroup()) {
            this.mAtm.f1161mH.post(new Runnable() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    WindowProcessController.this.lambda$onTopProcChanged$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTopProcChanged$1() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAtm.mVrController.onTopProcChangedLocked(this);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isHomeProcess() {
        return this == this.mAtm.mHomeProcess;
    }

    public boolean isPreviousProcess() {
        return this == this.mAtm.mPreviousProcess;
    }

    public boolean isHeavyWeightProcess() {
        return this == this.mAtm.mHeavyWeightProcess;
    }

    public boolean isFactoryTestProcess() {
        ComponentName componentName;
        ActivityTaskManagerService activityTaskManagerService = this.mAtm;
        int i = activityTaskManagerService.mFactoryTest;
        if (i == 0) {
            return false;
        }
        if (i == 1 && (componentName = activityTaskManagerService.mTopComponent) != null && this.mName.equals(componentName.getPackageName())) {
            return true;
        }
        return i == 2 && (this.mInfo.flags & 16) != 0;
    }

    public void setRunningRecentsAnimation(boolean z) {
        if (this.mRunningRecentsAnimation == z) {
            return;
        }
        this.mRunningRecentsAnimation = z;
        updateRunningRemoteOrRecentsAnimation();
    }

    public void setRunningRemoteAnimation(boolean z) {
        if (this.mRunningRemoteAnimation == z) {
            return;
        }
        this.mRunningRemoteAnimation = z;
        updateRunningRemoteOrRecentsAnimation();
    }

    public void updateRunningRemoteOrRecentsAnimation() {
        this.mAtm.f1161mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.WindowProcessController$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((WindowProcessListener) obj).setRunningRemoteAnimation(((Boolean) obj2).booleanValue());
            }
        }, this.mListener, Boolean.valueOf(isRunningRemoteTransition())));
    }

    public boolean isRunningRemoteTransition() {
        return this.mRunningRecentsAnimation || this.mRunningRemoteAnimation;
    }

    public void setRunningAnimationUnsafe() {
        this.mListener.setRunningRemoteAnimation(true);
    }

    public String toString() {
        Object obj = this.mOwner;
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mActivities.size() > 0) {
                    printWriter.print(str);
                    printWriter.println("Activities:");
                    for (int i = 0; i < this.mActivities.size(); i++) {
                        printWriter.print(str);
                        printWriter.print("  - ");
                        printWriter.println(this.mActivities.get(i));
                    }
                }
                if (this.mRecentTasks.size() > 0) {
                    printWriter.println(str + "Recent Tasks:");
                    for (int i2 = 0; i2 < this.mRecentTasks.size(); i2++) {
                        printWriter.println(str + "  - " + this.mRecentTasks.get(i2));
                    }
                }
                if (this.mVrThreadTid != 0) {
                    printWriter.print(str);
                    printWriter.print("mVrThreadTid=");
                    printWriter.println(this.mVrThreadTid);
                }
                this.mBgLaunchController.dump(printWriter, str);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        printWriter.println(str + " Configuration=" + getConfiguration());
        printWriter.println(str + " OverrideConfiguration=" + getRequestedOverrideConfiguration());
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(" mLastReportedConfiguration=");
        sb.append(this.mHasCachedConfiguration ? "(cached) " + this.mLastReportedConfiguration : this.mLastReportedConfiguration);
        printWriter.println(sb.toString());
        int i3 = this.mActivityStateFlags;
        if (i3 != 65535) {
            printWriter.print(str + " mActivityStateFlags=");
            if ((1048576 & i3) != 0) {
                printWriter.print("W|");
            }
            if ((65536 & i3) != 0) {
                printWriter.print("V|");
                if ((2097152 & i3) != 0) {
                    printWriter.print("R|");
                }
            } else if ((131072 & i3) != 0) {
                printWriter.print("P|");
            } else if ((262144 & i3) != 0) {
                printWriter.print("S|");
                if ((524288 & i3) != 0) {
                    printWriter.print("F|");
                }
            }
            if ((4194304 & i3) != 0) {
                printWriter.print("VT|");
            }
            int i4 = i3 & GnssNative.GNSS_AIDING_TYPE_ALL;
            if (i4 != 65535) {
                printWriter.print("taskLayer=" + i4);
            }
            printWriter.println();
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        this.mListener.dumpDebug(protoOutputStream, j);
    }
}
