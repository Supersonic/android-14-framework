package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.app.ResultInfo;
import android.app.WindowConfiguration;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ActivityResultItem;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.NewIntentItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.ResumeActivityItem;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.ITaskFragmentOrganizer;
import android.window.ScreenCapture;
import android.window.TaskFragmentAnimationParams;
import android.window.TaskFragmentInfo;
import android.window.TaskFragmentOrganizerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.function.pooled.PooledPredicate;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p014wm.ActivityRecord;
import com.android.server.p014wm.DisplayPolicy;
import com.android.server.p014wm.RemoteAnimationController;
import com.android.server.p014wm.WindowContainer;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.TaskFragment */
/* loaded from: classes2.dex */
public class TaskFragment extends WindowContainer<WindowContainer> {
    public TaskFragment mAdjacentTaskFragment;
    public TaskFragmentAnimationParams mAnimationParams;
    public final ActivityTaskManagerService mAtmService;
    public HashMap<String, ScreenCapture.ScreenshotHardwareBuffer> mBackScreenshots;
    public boolean mClearedForReorderActivityToFront;
    public boolean mClearedTaskForReuse;
    public boolean mClearedTaskFragmentForPip;
    public TaskFragment mCompanionTaskFragment;
    @VisibleForTesting
    boolean mCreatedByOrganizer;
    public boolean mDelayLastActivityRemoval;
    public boolean mDelayOrganizedTaskFragmentSurfaceUpdate;
    public Dimmer mDimmer;
    public final EnsureActivitiesVisibleHelper mEnsureActivitiesVisibleHelper;
    public final EnsureVisibleActivitiesConfigHelper mEnsureVisibleActivitiesConfigHelper;
    public final IBinder mFragmentToken;
    public final boolean mIsEmbedded;
    public boolean mIsRemovalRequested;
    public ActivityRecord mLastPausedActivity;
    public final Point mLastSurfaceSize;
    public int mMinHeight;
    public int mMinWidth;
    public ActivityRecord mPausingActivity;
    public final Rect mRelativeEmbeddedBounds;
    public ActivityRecord mResumedActivity;
    public final RootWindowContainer mRootWindowContainer;
    public boolean mTaskFragmentAppearedSent;
    public ITaskFragmentOrganizer mTaskFragmentOrganizer;
    public final TaskFragmentOrganizerController mTaskFragmentOrganizerController;
    public String mTaskFragmentOrganizerProcessName;
    public int mTaskFragmentOrganizerUid;
    public boolean mTaskFragmentVanishedSent;
    public final ActivityTaskSupervisor mTaskSupervisor;
    public final Rect mTmpAbsBounds;
    public final Rect mTmpBounds;
    public final Rect mTmpFullBounds;
    public final Rect mTmpNonDecorBounds;
    public final Rect mTmpStableBounds;

    @Override // com.android.server.p014wm.WindowContainer
    public TaskFragment asTaskFragment() {
        return this;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canCreateRemoteAnimationTarget() {
        return true;
    }

    public void executeAppTransition(ActivityOptions activityOptions) {
    }

    @Override // com.android.server.p014wm.WindowContainer
    public long getProtoFieldId() {
        return 1146756268041L;
    }

    public boolean isForceHidden() {
        return false;
    }

    public boolean isForceTranslucent() {
        return false;
    }

    /* renamed from: com.android.server.wm.TaskFragment$EnsureVisibleActivitiesConfigHelper */
    /* loaded from: classes2.dex */
    public class EnsureVisibleActivitiesConfigHelper implements Predicate<ActivityRecord> {
        public boolean mBehindFullscreen;
        public boolean mPreserveWindow;
        public boolean mUpdateConfig;

        public EnsureVisibleActivitiesConfigHelper() {
        }

        @Override // java.util.function.Predicate
        public boolean test(ActivityRecord activityRecord) {
            this.mUpdateConfig |= activityRecord.ensureActivityConfiguration(0, this.mPreserveWindow);
            boolean occludesParent = activityRecord.occludesParent() | this.mBehindFullscreen;
            this.mBehindFullscreen = occludesParent;
            return occludesParent;
        }
    }

    public TaskFragment(ActivityTaskManagerService activityTaskManagerService, IBinder iBinder, boolean z) {
        this(activityTaskManagerService, iBinder, z, true);
    }

    public TaskFragment(ActivityTaskManagerService activityTaskManagerService, IBinder iBinder, boolean z, boolean z2) {
        super(activityTaskManagerService.mWindowManager);
        this.mDimmer = new Dimmer(this);
        this.mPausingActivity = null;
        this.mLastPausedActivity = null;
        this.mResumedActivity = null;
        this.mTaskFragmentOrganizerUid = -1;
        this.mAnimationParams = TaskFragmentAnimationParams.DEFAULT;
        this.mLastSurfaceSize = new Point();
        this.mTmpBounds = new Rect();
        this.mTmpAbsBounds = new Rect();
        this.mTmpFullBounds = new Rect();
        this.mTmpStableBounds = new Rect();
        this.mTmpNonDecorBounds = new Rect();
        this.mBackScreenshots = new HashMap<>();
        this.mEnsureActivitiesVisibleHelper = new EnsureActivitiesVisibleHelper(this);
        this.mEnsureVisibleActivitiesConfigHelper = new EnsureVisibleActivitiesConfigHelper();
        this.mAtmService = activityTaskManagerService;
        this.mTaskSupervisor = activityTaskManagerService.mTaskSupervisor;
        this.mRootWindowContainer = activityTaskManagerService.mRootWindowContainer;
        this.mCreatedByOrganizer = z;
        this.mIsEmbedded = z2;
        this.mRelativeEmbeddedBounds = z2 ? new Rect() : null;
        this.mTaskFragmentOrganizerController = activityTaskManagerService.mWindowOrganizerController.mTaskFragmentOrganizerController;
        this.mFragmentToken = iBinder;
        this.mRemoteToken = new WindowContainer.RemoteToken(this);
    }

    public static TaskFragment fromTaskFragmentToken(IBinder iBinder, ActivityTaskManagerService activityTaskManagerService) {
        if (iBinder == null) {
            return null;
        }
        return activityTaskManagerService.mWindowOrganizerController.getTaskFragment(iBinder);
    }

    public void setAdjacentTaskFragment(TaskFragment taskFragment) {
        if (this.mAdjacentTaskFragment == taskFragment) {
            return;
        }
        resetAdjacentTaskFragment();
        if (taskFragment != null) {
            this.mAdjacentTaskFragment = taskFragment;
            taskFragment.setAdjacentTaskFragment(this);
        }
    }

    public void setCompanionTaskFragment(TaskFragment taskFragment) {
        this.mCompanionTaskFragment = taskFragment;
    }

    public TaskFragment getCompanionTaskFragment() {
        return this.mCompanionTaskFragment;
    }

    public void resetAdjacentTaskFragment() {
        TaskFragment taskFragment = this.mAdjacentTaskFragment;
        if (taskFragment != null && taskFragment.mAdjacentTaskFragment == this) {
            taskFragment.mAdjacentTaskFragment = null;
            this.mAdjacentTaskFragment.mDelayLastActivityRemoval = false;
        }
        this.mAdjacentTaskFragment = null;
        this.mDelayLastActivityRemoval = false;
    }

    public void setTaskFragmentOrganizer(TaskFragmentOrganizerToken taskFragmentOrganizerToken, int i, String str) {
        this.mTaskFragmentOrganizer = ITaskFragmentOrganizer.Stub.asInterface(taskFragmentOrganizerToken.asBinder());
        this.mTaskFragmentOrganizerUid = i;
        this.mTaskFragmentOrganizerProcessName = str;
    }

    public void onTaskFragmentOrganizerRemoved() {
        this.mTaskFragmentOrganizer = null;
    }

    public boolean hasTaskFragmentOrganizer(ITaskFragmentOrganizer iTaskFragmentOrganizer) {
        return (iTaskFragmentOrganizer == null || this.mTaskFragmentOrganizer == null || !iTaskFragmentOrganizer.asBinder().equals(this.mTaskFragmentOrganizer.asBinder())) ? false : true;
    }

    public void setAnimationParams(TaskFragmentAnimationParams taskFragmentAnimationParams) {
        this.mAnimationParams = taskFragmentAnimationParams;
    }

    public TaskFragmentAnimationParams getAnimationParams() {
        return this.mAnimationParams;
    }

    public TaskFragment getAdjacentTaskFragment() {
        return this.mAdjacentTaskFragment;
    }

    public ActivityRecord getTopResumedActivity() {
        ActivityRecord resumedActivity = getResumedActivity();
        int childCount = getChildCount();
        while (true) {
            childCount--;
            ActivityRecord activityRecord = null;
            if (childCount < 0) {
                return null;
            }
            WindowContainer childAt = getChildAt(childCount);
            if (resumedActivity != null && childAt == resumedActivity) {
                activityRecord = childAt.asActivityRecord();
            } else if (childAt.asTaskFragment() != null) {
                activityRecord = childAt.asTaskFragment().getTopResumedActivity();
            }
            if (activityRecord != null) {
                return activityRecord;
            }
        }
    }

    public ActivityRecord getResumedActivity() {
        return this.mResumedActivity;
    }

    public void setResumedActivity(ActivityRecord activityRecord, String str) {
        DisplayContent displayContent;
        warnForNonLeafTaskFragment("setResumedActivity");
        ActivityRecord activityRecord2 = this.mResumedActivity;
        if (activityRecord2 == activityRecord) {
            return;
        }
        if (activityRecord != null && activityRecord2 == null) {
            getTask().touchActiveTime();
        }
        ActivityRecord activityRecord3 = this.mResumedActivity;
        this.mResumedActivity = activityRecord;
        this.mTaskSupervisor.updateTopResumedActivityIfNeeded(str);
        if (activityRecord == null && (displayContent = activityRecord3.mDisplayContent) != null && displayContent.getFocusedRootTask() == null) {
            activityRecord3.mDisplayContent.onRunningActivityChanged();
        } else if (activityRecord != null) {
            activityRecord.mDisplayContent.onRunningActivityChanged();
        }
    }

    @VisibleForTesting
    public void setPausingActivity(ActivityRecord activityRecord) {
        this.mPausingActivity = activityRecord;
    }

    public ActivityRecord getTopPausingActivity() {
        ActivityRecord pausingActivity = getPausingActivity();
        int childCount = getChildCount();
        while (true) {
            childCount--;
            ActivityRecord activityRecord = null;
            if (childCount < 0) {
                return null;
            }
            WindowContainer childAt = getChildAt(childCount);
            if (pausingActivity != null && childAt == pausingActivity) {
                activityRecord = childAt.asActivityRecord();
            } else if (childAt.asTaskFragment() != null) {
                activityRecord = childAt.asTaskFragment().getTopPausingActivity();
            }
            if (activityRecord != null) {
                return activityRecord;
            }
        }
    }

    public ActivityRecord getPausingActivity() {
        return this.mPausingActivity;
    }

    public int getDisplayId() {
        DisplayContent displayContent = getDisplayContent();
        if (displayContent != null) {
            return displayContent.mDisplayId;
        }
        return -1;
    }

    public Task getTask() {
        if (asTask() != null) {
            return asTask();
        }
        TaskFragment asTaskFragment = getParent() != null ? getParent().asTaskFragment() : null;
        if (asTaskFragment != null) {
            return asTaskFragment.getTask();
        }
        return null;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public TaskDisplayArea getDisplayArea() {
        return (TaskDisplayArea) super.getDisplayArea();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isAttached() {
        TaskDisplayArea displayArea = getDisplayArea();
        return (displayArea == null || displayArea.isRemoved()) ? false : true;
    }

    public TaskFragment getRootTaskFragment() {
        TaskFragment asTaskFragment;
        WindowContainer parent = getParent();
        return (parent == null || (asTaskFragment = parent.asTaskFragment()) == null) ? this : asTaskFragment.getRootTaskFragment();
    }

    public Task getRootTask() {
        return getRootTaskFragment().asTask();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isEmbedded() {
        return this.mIsEmbedded;
    }

    public int isAllowedToEmbedActivity(ActivityRecord activityRecord) {
        return isAllowedToEmbedActivity(activityRecord, this.mTaskFragmentOrganizerUid);
    }

    public int isAllowedToEmbedActivity(ActivityRecord activityRecord, int i) {
        if (isAllowedToEmbedActivityInUntrustedMode(activityRecord) || isAllowedToEmbedActivityInTrustedMode(activityRecord, i)) {
            return smallerThanMinDimension(activityRecord) ? 2 : 0;
        }
        return 1;
    }

    public boolean smallerThanMinDimension(ActivityRecord activityRecord) {
        Point minDimensions;
        Rect bounds = getBounds();
        Task task = getTask();
        if (task == null || bounds.equals(task.getBounds()) || (minDimensions = activityRecord.getMinDimensions()) == null) {
            return false;
        }
        return bounds.width() < minDimensions.x || bounds.height() < minDimensions.y;
    }

    public boolean isAllowedToEmbedActivityInUntrustedMode(ActivityRecord activityRecord) {
        WindowContainer parent = getParent();
        return parent != null && parent.getBounds().contains(getBounds()) && (activityRecord.info.flags & 268435456) == 268435456;
    }

    public boolean isAllowedToEmbedActivityInTrustedMode(ActivityRecord activityRecord) {
        return isAllowedToEmbedActivityInTrustedMode(activityRecord, this.mTaskFragmentOrganizerUid);
    }

    public boolean isAllowedToEmbedActivityInTrustedMode(ActivityRecord activityRecord, int i) {
        if (isFullyTrustedEmbedding(activityRecord, i)) {
            return true;
        }
        Set knownActivityEmbeddingCerts = activityRecord.info.getKnownActivityEmbeddingCerts();
        if (knownActivityEmbeddingCerts.isEmpty()) {
            return false;
        }
        AndroidPackage androidPackage = this.mAtmService.getPackageManagerInternalLocked().getPackage(i);
        return androidPackage != null && androidPackage.getSigningDetails().hasAncestorOrSelfWithDigest(knownActivityEmbeddingCerts);
    }

    public static boolean isFullyTrustedEmbedding(ActivityRecord activityRecord, int i) {
        return UserHandle.getAppId(i) == 1000 || activityRecord.isUid(i);
    }

    public static /* synthetic */ boolean lambda$isFullyTrustedEmbedding$0(int i, ActivityRecord activityRecord) {
        return !isFullyTrustedEmbedding(activityRecord, i);
    }

    public boolean isFullyTrustedEmbedding(final int i) {
        return !forAllActivities(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isFullyTrustedEmbedding$0;
                lambda$isFullyTrustedEmbedding$0 = TaskFragment.lambda$isFullyTrustedEmbedding$0(i, (ActivityRecord) obj);
                return lambda$isFullyTrustedEmbedding$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isAllowedToBeEmbeddedInTrustedMode$1(ActivityRecord activityRecord) {
        return !isAllowedToEmbedActivityInTrustedMode(activityRecord);
    }

    public boolean isAllowedToBeEmbeddedInTrustedMode() {
        return !forAllActivities(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAllowedToBeEmbeddedInTrustedMode$1;
                lambda$isAllowedToBeEmbeddedInTrustedMode$1 = TaskFragment.this.lambda$isAllowedToBeEmbeddedInTrustedMode$1((ActivityRecord) obj);
                return lambda$isAllowedToBeEmbeddedInTrustedMode$1;
            }
        });
    }

    public TaskFragment getOrganizedTaskFragment() {
        if (this.mTaskFragmentOrganizer != null) {
            return this;
        }
        TaskFragment asTaskFragment = getParent() != null ? getParent().asTaskFragment() : null;
        if (asTaskFragment != null) {
            return asTaskFragment.getOrganizedTaskFragment();
        }
        return null;
    }

    public final void warnForNonLeafTaskFragment(String str) {
        if (isLeafTaskFragment()) {
            return;
        }
        Slog.w("ActivityTaskManager", str + " on non-leaf task fragment " + this);
    }

    public boolean hasDirectChildActivities() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (((WindowContainer) this.mChildren.get(size)).asActivityRecord() != null) {
                return true;
            }
        }
        return false;
    }

    public void cleanUpActivityReferences(ActivityRecord activityRecord) {
        ActivityRecord activityRecord2 = this.mPausingActivity;
        if (activityRecord2 != null && activityRecord2 == activityRecord) {
            this.mPausingActivity = null;
        }
        ActivityRecord activityRecord3 = this.mResumedActivity;
        if (activityRecord3 != null && activityRecord3 == activityRecord) {
            setResumedActivity(null, "cleanUpActivityReferences");
        }
        activityRecord.removeTimeouts();
    }

    public boolean isLeafTaskFragment() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (((WindowContainer) this.mChildren.get(size)).asTaskFragment() != null) {
                return false;
            }
        }
        return true;
    }

    public void onActivityStateChanged(ActivityRecord activityRecord, ActivityRecord.State state, String str) {
        warnForNonLeafTaskFragment("onActivityStateChanged");
        if (activityRecord == this.mResumedActivity && state != ActivityRecord.State.RESUMED) {
            setResumedActivity(null, str + " - onActivityStateChanged");
        }
        if (state == ActivityRecord.State.RESUMED) {
            setResumedActivity(activityRecord, str + " - onActivityStateChanged");
            this.mTaskSupervisor.mRecentTasks.add(activityRecord.getTask());
        }
    }

    public boolean handleAppDied(WindowProcessController windowProcessController) {
        warnForNonLeafTaskFragment("handleAppDied");
        ActivityRecord activityRecord = this.mPausingActivity;
        boolean z = false;
        if (activityRecord != null && activityRecord.app == windowProcessController) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1564228464, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
            }
            this.mPausingActivity = null;
            z = true;
        }
        ActivityRecord activityRecord2 = this.mLastPausedActivity;
        if (activityRecord2 != null && activityRecord2.app == windowProcessController) {
            this.mLastPausedActivity = null;
        }
        return z;
    }

    public void awakeFromSleeping() {
        if (this.mPausingActivity != null) {
            Slog.d("ActivityTaskManager", "awakeFromSleeping: previously pausing activity didn't pause");
            this.mPausingActivity.activityPaused(true);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0049  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean sleepIfPossible(boolean z) {
        boolean z2;
        ActivityRecord activityRecord = this.mResumedActivity;
        if (activityRecord != null) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 987903142, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
            }
            startPausing(false, true, null, "sleep");
        } else {
            ActivityRecord activityRecord2 = this.mPausingActivity;
            if (activityRecord2 != null) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1912291550, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                }
            } else {
                z2 = true;
                if (!z && containsStoppingActivity()) {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 669361121, 1, (String) null, new Object[]{Long.valueOf(this.mTaskSupervisor.mStoppingActivities.size())});
                    }
                    this.mTaskSupervisor.scheduleIdle();
                    z2 = false;
                }
                if (z2) {
                    updateActivityVisibilities(null, 0, false, true);
                }
                return z2;
            }
        }
        z2 = false;
        if (!z) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            }
            this.mTaskSupervisor.scheduleIdle();
            z2 = false;
        }
        if (z2) {
        }
        return z2;
    }

    public final boolean containsStoppingActivity() {
        for (int size = this.mTaskSupervisor.mStoppingActivities.size() - 1; size >= 0; size--) {
            if (this.mTaskSupervisor.mStoppingActivities.get(size).getTaskFragment() == this) {
                return true;
            }
        }
        return false;
    }

    public boolean isTranslucent(ActivityRecord activityRecord) {
        if (!isAttached() || isForceHidden() || isForceTranslucent()) {
            return true;
        }
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TaskFragment$$ExternalSyntheticLambda1(), PooledLambda.__(ActivityRecord.class), activityRecord, Boolean.FALSE);
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity == null;
    }

    public boolean isTranslucentForTransition() {
        if (!isAttached() || isForceHidden() || isForceTranslucent()) {
            return true;
        }
        PooledPredicate obtainPredicate = PooledLambda.obtainPredicate(new TaskFragment$$ExternalSyntheticLambda1(), PooledLambda.__(ActivityRecord.class), (Object) null, Boolean.valueOf(!isVisibleRequested()));
        ActivityRecord activity = getActivity(obtainPredicate);
        obtainPredicate.recycle();
        return activity == null;
    }

    public static boolean isOpaqueActivity(ActivityRecord activityRecord, ActivityRecord activityRecord2, boolean z) {
        if (activityRecord.visibleIgnoringKeyguard || activityRecord == activityRecord2) {
            return activityRecord.occludesParent(z);
        }
        return false;
    }

    public ActivityRecord getTopNonFinishingActivity() {
        return getTopNonFinishingActivity(true);
    }

    public static /* synthetic */ boolean lambda$getTopNonFinishingActivity$2(ActivityRecord activityRecord) {
        return !activityRecord.finishing;
    }

    public ActivityRecord getTopNonFinishingActivity(boolean z) {
        if (z) {
            return getActivity(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getTopNonFinishingActivity$2;
                    lambda$getTopNonFinishingActivity$2 = TaskFragment.lambda$getTopNonFinishingActivity$2((ActivityRecord) obj);
                    return lambda$getTopNonFinishingActivity$2;
                }
            });
        }
        return getActivity(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getTopNonFinishingActivity$3;
                lambda$getTopNonFinishingActivity$3 = TaskFragment.lambda$getTopNonFinishingActivity$3((ActivityRecord) obj);
                return lambda$getTopNonFinishingActivity$3;
            }
        });
    }

    public static /* synthetic */ boolean lambda$getTopNonFinishingActivity$3(ActivityRecord activityRecord) {
        return (activityRecord.finishing || activityRecord.isTaskOverlay()) ? false : true;
    }

    public ActivityRecord topRunningActivity() {
        return topRunningActivity(false);
    }

    public static /* synthetic */ boolean lambda$topRunningActivity$4(ActivityRecord activityRecord) {
        return activityRecord.canBeTopRunning() && activityRecord.isFocusable();
    }

    public ActivityRecord topRunningActivity(boolean z) {
        if (z) {
            return getActivity(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$topRunningActivity$4;
                    lambda$topRunningActivity$4 = TaskFragment.lambda$topRunningActivity$4((ActivityRecord) obj);
                    return lambda$topRunningActivity$4;
                }
            });
        }
        return getActivity(new Task$$ExternalSyntheticLambda14());
    }

    public int getNonFinishingActivityCount() {
        final int[] iArr = new int[1];
        forAllActivities(new Consumer() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TaskFragment.lambda$getNonFinishingActivityCount$5(iArr, (ActivityRecord) obj);
            }
        });
        return iArr[0];
    }

    public static /* synthetic */ void lambda$getNonFinishingActivityCount$5(int[] iArr, ActivityRecord activityRecord) {
        if (activityRecord.finishing) {
            return;
        }
        iArr[0] = iArr[0] + 1;
    }

    public boolean isTopActivityFocusable() {
        ActivityRecord activityRecord = topRunningActivity();
        if (activityRecord != null) {
            return activityRecord.isFocusable();
        }
        return isFocusable() && getWindowConfiguration().canReceiveKeys();
    }

    /* JADX WARN: Code restructure failed: missing block: B:79:0x00ed, code lost:
        if (r4 != false) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x00ef, code lost:
        return 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x00f0, code lost:
        return r3;
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x003f  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x00ed A[EDGE_INSN: B:86:0x00ed->B:79:0x00ed ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int getVisibility(ActivityRecord activityRecord) {
        int i;
        int childCount;
        TaskFragment taskFragment;
        if (!isAttached() || isForceHidden()) {
            return 2;
        }
        boolean z = false;
        if (isTopActivityLaunchedBehind()) {
            return 0;
        }
        WindowContainer parent = getParent();
        boolean z2 = true;
        if (parent.asTaskFragment() != null) {
            int visibility = parent.asTaskFragment().getVisibility(activityRecord);
            if (visibility == 2) {
                return 2;
            }
            if (visibility == 1) {
                i = 1;
                ArrayList arrayList = new ArrayList();
                childCount = parent.getChildCount() - 1;
                boolean z3 = false;
                while (true) {
                    if (childCount < 0) {
                        break;
                    }
                    WindowContainer childAt = parent.getChildAt(childCount);
                    if (childAt != null) {
                        boolean hasRunningActivity = hasRunningActivity(childAt);
                        if (childAt == this) {
                            if (!arrayList.isEmpty() && !z3) {
                                ((WindowContainer) this).mTmpRect.set(getBounds());
                                for (int size = arrayList.size() - 1; size >= 0; size--) {
                                    TaskFragment taskFragment2 = (TaskFragment) arrayList.get(size);
                                    TaskFragment taskFragment3 = taskFragment2.mAdjacentTaskFragment;
                                    if (taskFragment3 != this && (((WindowContainer) this).mTmpRect.intersect(taskFragment2.getBounds()) || ((WindowContainer) this).mTmpRect.intersect(taskFragment3.getBounds()))) {
                                        return 2;
                                    }
                                }
                            }
                            z2 = (hasRunningActivity || (activityRecord != null && activityRecord.isDescendantOf(this)) || isActivityTypeHome()) ? true : true;
                        } else if (hasRunningActivity) {
                            int windowingMode = childAt.getWindowingMode();
                            if (windowingMode == 1) {
                                if (!isTranslucent(childAt, activityRecord)) {
                                    return 2;
                                }
                            } else if (windowingMode == 6 && childAt.matchParentBounds()) {
                                if (!isTranslucent(childAt, activityRecord)) {
                                    return 2;
                                }
                            } else {
                                TaskFragment asTaskFragment = childAt.asTaskFragment();
                                if (asTaskFragment != null && (taskFragment = asTaskFragment.mAdjacentTaskFragment) != null) {
                                    if (arrayList.contains(taskFragment)) {
                                        if (!asTaskFragment.isTranslucent(activityRecord) && !asTaskFragment.mAdjacentTaskFragment.isTranslucent(activityRecord)) {
                                            return 2;
                                        }
                                        i = 1;
                                        z3 = true;
                                    } else {
                                        arrayList.add(asTaskFragment);
                                    }
                                }
                            }
                            i = 1;
                        } else {
                            continue;
                        }
                    }
                    childCount--;
                }
            }
        }
        i = 0;
        ArrayList arrayList2 = new ArrayList();
        childCount = parent.getChildCount() - 1;
        boolean z32 = false;
        while (true) {
            if (childCount < 0) {
            }
            childCount--;
        }
    }

    public static boolean hasRunningActivity(WindowContainer windowContainer) {
        return windowContainer.asTaskFragment() != null ? windowContainer.asTaskFragment().topRunningActivity() != null : (windowContainer.asActivityRecord() == null || windowContainer.asActivityRecord().finishing) ? false : true;
    }

    public static boolean isTranslucent(WindowContainer windowContainer, ActivityRecord activityRecord) {
        if (windowContainer.asTaskFragment() != null) {
            return windowContainer.asTaskFragment().isTranslucent(activityRecord);
        }
        if (windowContainer.asActivityRecord() != null) {
            return !windowContainer.asActivityRecord().occludesParent();
        }
        return false;
    }

    public final boolean isTopActivityLaunchedBehind() {
        ActivityRecord activityRecord = topRunningActivity();
        return activityRecord != null && activityRecord.mLaunchTaskBehind;
    }

    public final void updateActivityVisibilities(ActivityRecord activityRecord, int i, boolean z, boolean z2) {
        this.mTaskSupervisor.beginActivityVisibilityUpdate();
        try {
            this.mEnsureActivitiesVisibleHelper.process(activityRecord, i, z, z2);
        } finally {
            this.mTaskSupervisor.endActivityVisibilityUpdate();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:106:0x01c8  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01fe  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0213  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x0227  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x03be  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean resumeTopActivity(ActivityRecord activityRecord, ActivityOptions activityOptions, boolean z) {
        boolean z2;
        ActivityRecord activityRecord2;
        DisplayContent displayContent;
        ActivityRecord activityRecord3 = topRunningActivity(true);
        if (activityRecord3 == null || !activityRecord3.canResumeByCompat()) {
            return false;
        }
        activityRecord3.delayedResume = false;
        if (!this.mRootWindowContainer.allPausedActivitiesComplete()) {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 935418348, 0, (String) null, (Object[]) null);
            }
            return false;
        }
        TaskDisplayArea displayArea = getDisplayArea();
        if (this.mResumedActivity == activityRecord3 && activityRecord3.isState(ActivityRecord.State.RESUMED) && displayArea.allResumedActivitiesComplete()) {
            displayArea.ensureActivitiesVisible(null, 0, false, true);
            executeAppTransition(activityOptions);
            if (displayArea.inMultiWindowMode() && (displayContent = displayArea.mDisplayContent) != null && displayContent.mFocusedApp != activityRecord3) {
                displayContent.setFocusedApp(activityRecord3);
            }
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 675705156, 0, (String) null, new Object[]{String.valueOf(activityRecord3)});
            }
            return false;
        } else if (this.mLastPausedActivity == activityRecord3 && shouldSleepOrShutDownActivities()) {
            executeAppTransition(activityOptions);
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -1886145147, 0, (String) null, (Object[]) null);
            }
            return false;
        } else if (!this.mAtmService.mAmInternal.hasStartedUserState(activityRecord3.mUserId)) {
            Slog.w("ActivityTaskManager", "Skipping resume of top activity " + activityRecord3 + ": user " + activityRecord3.mUserId + " is stopped");
            return false;
        } else {
            this.mTaskSupervisor.mStoppingActivities.remove(activityRecord3);
            this.mTaskSupervisor.setLaunchSource(activityRecord3.info.applicationInfo.uid);
            Task lastFocusedRootTask = displayArea.getLastFocusedRootTask();
            ActivityRecord topResumedActivity = (lastFocusedRootTask == null || lastFocusedRootTask == getRootTaskFragment().asTask()) ? null : lastFocusedRootTask.getTopResumedActivity();
            boolean z3 = !z && displayArea.pauseBackTasks(activityRecord3);
            ActivityRecord activityRecord4 = this.mResumedActivity;
            if (activityRecord4 != null) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 102618780, 0, (String) null, new Object[]{String.valueOf(activityRecord4)});
                }
                z3 |= startPausing(this.mTaskSupervisor.mUserLeaving, false, activityRecord3, "resumeTopActivity");
            }
            if (z3) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 341055768, 0, (String) null, (Object[]) null);
                }
                if (activityRecord3.attachedToProcess()) {
                    activityRecord3.app.updateProcessInfo(false, true, false, false);
                } else if (!activityRecord3.isProcessRunning()) {
                    boolean z4 = this == displayArea.getFocusedRootTask();
                    this.mAtmService.startProcessAsync(activityRecord3, false, z4, z4 ? "next-top-activity" : "next-activity");
                }
                if (topResumedActivity != null) {
                    topResumedActivity.setWillCloseOrEnterPip(true);
                }
                return true;
            } else if (this.mResumedActivity == activityRecord3 && activityRecord3.isState(ActivityRecord.State.RESUMED) && displayArea.allResumedActivitiesComplete()) {
                executeAppTransition(activityOptions);
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -2010331310, 0, (String) null, new Object[]{String.valueOf(activityRecord3)});
                }
                return true;
            } else {
                if (shouldSleepActivities()) {
                    this.mTaskSupervisor.finishNoHistoryActivitiesIfNeeded(activityRecord3);
                }
                if (activityRecord != null && activityRecord != activityRecord3 && activityRecord3.nowVisible && activityRecord.finishing) {
                    activityRecord.setVisibility(false);
                }
                try {
                    this.mTaskSupervisor.getActivityMetricsLogger().notifyBeforePackageUnstopped(activityRecord3.packageName);
                    this.mAtmService.getPackageManager().setPackageStoppedState(activityRecord3.packageName, false, activityRecord3.mUserId);
                } catch (RemoteException unused) {
                    DisplayContent displayContent2 = displayArea.mDisplayContent;
                    if (activityRecord == null) {
                        if (activityRecord.finishing) {
                            if (this.mTaskSupervisor.mNoAnimActivities.contains(activityRecord)) {
                                displayContent2.prepareAppTransition(0);
                                z2 = false;
                            } else {
                                displayContent2.prepareAppTransition(2);
                                z2 = true;
                            }
                            activityRecord.setVisibility(false);
                        } else if (this.mTaskSupervisor.mNoAnimActivities.contains(activityRecord3)) {
                            displayContent2.prepareAppTransition(0);
                            z2 = false;
                        } else {
                            displayContent2.prepareAppTransition(1, activityRecord3.mLaunchTaskBehind ? 32 : 0);
                            z2 = true;
                        }
                    } else if (this.mTaskSupervisor.mNoAnimActivities.contains(activityRecord3)) {
                        displayContent2.prepareAppTransition(0);
                        z2 = false;
                    } else {
                        displayContent2.prepareAppTransition(1);
                        z2 = true;
                    }
                    if (!z2) {
                        activityRecord3.applyOptionsAnimation();
                    } else {
                        activityRecord3.abortAndClearOptionsAnimation();
                    }
                    this.mTaskSupervisor.mNoAnimActivities.clear();
                    if (!activityRecord3.attachedToProcess()) {
                        boolean z5 = inMultiWindowMode() || !((activityRecord2 = this.mLastPausedActivity) == null || activityRecord2.occludesParent());
                        if (!activityRecord3.isVisibleRequested() || activityRecord3.mAppStopped || z5) {
                            activityRecord3.app.addToPendingTop();
                            activityRecord3.setVisibility(true);
                        }
                        activityRecord3.startLaunchTickingLocked();
                        ActivityRecord topResumedActivity2 = lastFocusedRootTask == null ? null : lastFocusedRootTask.getTopResumedActivity();
                        ActivityRecord.State state = activityRecord3.getState();
                        this.mAtmService.updateCpuStats();
                        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1421296808, 0, (String) null, new Object[]{String.valueOf(activityRecord3)});
                        }
                        activityRecord3.setState(ActivityRecord.State.RESUMED, "resumeTopActivity");
                        if (shouldBeVisible(activityRecord3) ? !this.mRootWindowContainer.ensureVisibilityAndConfig(activityRecord3, getDisplayId(), true, false) : true) {
                            ActivityRecord activityRecord5 = topRunningActivity();
                            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_STATES, -310337305, 0, (String) null, new Object[]{String.valueOf(activityRecord3), String.valueOf(activityRecord5)});
                            }
                            if (activityRecord5 != activityRecord3) {
                                this.mTaskSupervisor.scheduleResumeTopActivities();
                            }
                            if (!activityRecord3.isVisibleRequested() || activityRecord3.mAppStopped) {
                                activityRecord3.setVisibility(true);
                            }
                            activityRecord3.completeResumeLocked();
                            return true;
                        }
                        try {
                            ClientTransaction obtain = ClientTransaction.obtain(activityRecord3.app.getThread(), activityRecord3.token);
                            ArrayList<ResultInfo> arrayList = activityRecord3.results;
                            if (arrayList != null) {
                                int size = arrayList.size();
                                if (!activityRecord3.finishing && size > 0) {
                                    obtain.addCallback(ActivityResultItem.obtain(arrayList));
                                }
                            }
                            ArrayList<ReferrerIntent> arrayList2 = activityRecord3.newIntents;
                            if (arrayList2 != null) {
                                obtain.addCallback(NewIntentItem.obtain(arrayList2, true));
                            }
                            activityRecord3.notifyAppResumed();
                            EventLogTags.writeWmResumeActivity(activityRecord3.mUserId, System.identityHashCode(activityRecord3), activityRecord3.getTask().mTaskId, activityRecord3.shortComponentName);
                            this.mAtmService.getAppWarningsLocked().onResumeActivity(activityRecord3);
                            activityRecord3.app.setPendingUiCleanAndForceProcessStateUpTo(this.mAtmService.mTopProcessState);
                            activityRecord3.abortAndClearOptionsAnimation();
                            obtain.setLifecycleStateRequest(ResumeActivityItem.obtain(activityRecord3.app.getReportedProcState(), displayContent2.isNextTransitionForward(), activityRecord3.shouldSendCompatFakeFocus()));
                            this.mAtmService.getLifecycleManager().scheduleTransaction(obtain);
                            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -1419461256, 0, (String) null, new Object[]{String.valueOf(activityRecord3)});
                            }
                            try {
                                activityRecord3.completeResumeLocked();
                            } catch (Exception e) {
                                Slog.w("ActivityTaskManager", "Exception thrown during resume of " + activityRecord3, e);
                                activityRecord3.finishIfPossible("resume-exception", true);
                                return true;
                            }
                        } catch (Exception unused2) {
                            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -80004683, 0, (String) null, new Object[]{String.valueOf(state), String.valueOf(activityRecord3)});
                            }
                            activityRecord3.setState(state, "resumeTopActivityInnerLocked");
                            if (topResumedActivity2 != null) {
                                topResumedActivity2.setState(ActivityRecord.State.RESUMED, "resumeTopActivityInnerLocked");
                            }
                            Slog.i("ActivityTaskManager", "Restarting because process died: " + activityRecord3);
                            if (!activityRecord3.hasBeenLaunched) {
                                activityRecord3.hasBeenLaunched = true;
                            } else if (lastFocusedRootTask != null && lastFocusedRootTask.isTopRootTaskInDisplayArea()) {
                                activityRecord3.showStartingWindow(false);
                            }
                            this.mTaskSupervisor.startSpecificActivity(activityRecord3, true, false);
                            return true;
                        }
                    } else {
                        if (!activityRecord3.hasBeenLaunched) {
                            activityRecord3.hasBeenLaunched = true;
                        } else {
                            activityRecord3.showStartingWindow(false);
                        }
                        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, 1856783490, 0, (String) null, new Object[]{String.valueOf(activityRecord3)});
                        }
                        this.mTaskSupervisor.startSpecificActivity(activityRecord3, true, true);
                    }
                    return true;
                } catch (IllegalArgumentException e2) {
                    Slog.w("ActivityTaskManager", "Failed trying to unstop package " + activityRecord3.packageName + ": " + e2);
                    DisplayContent displayContent22 = displayArea.mDisplayContent;
                    if (activityRecord == null) {
                    }
                    if (!z2) {
                    }
                    this.mTaskSupervisor.mNoAnimActivities.clear();
                    if (!activityRecord3.attachedToProcess()) {
                    }
                    return true;
                }
            }
        }
    }

    public boolean shouldSleepOrShutDownActivities() {
        return shouldSleepActivities() || this.mAtmService.mShuttingDown;
    }

    public boolean shouldBeVisible(ActivityRecord activityRecord) {
        return getVisibility(activityRecord) != 2;
    }

    public boolean canBeResumed(ActivityRecord activityRecord) {
        return isTopActivityFocusable() && getVisibility(activityRecord) == 0;
    }

    public boolean isFocusableAndVisible() {
        return isTopActivityFocusable() && shouldBeVisible(null);
    }

    public final boolean startPausing(boolean z, ActivityRecord activityRecord, String str) {
        return startPausing(this.mTaskSupervisor.mUserLeaving, z, activityRecord, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x011e  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x015f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean startPausing(boolean z, boolean z2, ActivityRecord activityRecord, String str) {
        boolean z3;
        boolean z4;
        if (!hasDirectChildActivities()) {
            return false;
        }
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -248761393, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(this.mResumedActivity)});
        }
        if (this.mPausingActivity != null) {
            Slog.wtf("ActivityTaskManager", "Going to pause when pause is already pending for " + this.mPausingActivity + " state=" + this.mPausingActivity.getState());
            if (!shouldSleepActivities()) {
                completePause(false, activityRecord);
            }
        }
        ActivityRecord activityRecord2 = this.mResumedActivity;
        if (activityRecord2 == null) {
            if (activityRecord == null) {
                Slog.wtf("ActivityTaskManager", "Trying to pause when nothing is resumed");
                this.mRootWindowContainer.resumeFocusedTasksTopActivities();
            }
            return false;
        } else if (activityRecord2 == activityRecord) {
            Slog.wtf("ActivityTaskManager", "Trying to pause activity that is in process of being resumed");
            return false;
        } else {
            if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -957060823, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
            }
            this.mPausingActivity = activityRecord2;
            this.mLastPausedActivity = activityRecord2;
            if (!activityRecord2.finishing && activityRecord2.isNoHistory() && !this.mTaskSupervisor.mNoHistoryActivities.contains(activityRecord2)) {
                this.mTaskSupervisor.mNoHistoryActivities.add(activityRecord2);
            }
            activityRecord2.setState(ActivityRecord.State.PAUSING, "startPausingLocked");
            activityRecord2.getTask().touchActiveTime();
            this.mAtmService.updateCpuStats();
            if (activityRecord != null) {
                boolean occludesParent = activityRecord.occludesParent();
                boolean checkEnterPictureInPictureState = activityRecord2.checkEnterPictureInPictureState("shouldAutoPipWhilePausing", z);
                if (z && occludesParent && checkEnterPictureInPictureState && activityRecord2.pictureInPictureArgs.isAutoEnterEnabled()) {
                    z4 = false;
                    z3 = true;
                } else if (!checkEnterPictureInPictureState) {
                    z4 = (activityRecord.info.flags & 16384) != 0;
                    z3 = false;
                }
                if (activityRecord2.attachedToProcess()) {
                    this.mPausingActivity = null;
                    this.mLastPausedActivity = null;
                    this.mTaskSupervisor.mNoHistoryActivities.remove(activityRecord2);
                } else if (z3) {
                    activityRecord2.mPauseSchedulePendingForPip = true;
                    boolean enterPictureInPictureMode = this.mAtmService.enterPictureInPictureMode(activityRecord2, activityRecord2.pictureInPictureArgs, false);
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_STATES, -1101551167, 12, (String) null, new Object[]{String.valueOf(activityRecord2), Boolean.valueOf(enterPictureInPictureMode)});
                    }
                } else {
                    schedulePauseActivity(activityRecord2, z, z4, false, str);
                }
                if (!z2 && !this.mAtmService.isSleepingOrShuttingDownLocked()) {
                    this.mTaskSupervisor.acquireLaunchWakelock();
                }
                if (this.mPausingActivity != null) {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -648891906, 0, (String) null, (Object[]) null);
                    }
                    if (activityRecord == null) {
                        this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                    }
                    return false;
                }
                if (!z2) {
                    activityRecord2.pauseKeyDispatchingLocked();
                } else if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1633115609, 0, (String) null, (Object[]) null);
                }
                if (z4) {
                    completePause(false, activityRecord);
                    return false;
                }
                activityRecord2.schedulePauseTimeout();
                if (!z2) {
                    this.mTransitionController.setReady(this, false);
                }
                return true;
            }
            z3 = false;
            z4 = false;
            if (activityRecord2.attachedToProcess()) {
            }
            if (!z2) {
                this.mTaskSupervisor.acquireLaunchWakelock();
            }
            if (this.mPausingActivity != null) {
            }
        }
    }

    public void schedulePauseActivity(ActivityRecord activityRecord, boolean z, boolean z2, boolean z3, String str) {
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 378825104, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
        }
        try {
            activityRecord.mPauseSchedulePendingForPip = false;
            int i = activityRecord.mUserId;
            int identityHashCode = System.identityHashCode(activityRecord);
            String str2 = activityRecord.shortComponentName;
            EventLogTags.writeWmPauseActivity(i, identityHashCode, str2, "userLeaving=" + z, str);
            this.mAtmService.getLifecycleManager().scheduleTransaction(activityRecord.app.getThread(), activityRecord.token, (ActivityLifecycleItem) PauseActivityItem.obtain(activityRecord.finishing, z, activityRecord.configChangeFlags, z2, z3));
        } catch (Exception e) {
            Slog.w("ActivityTaskManager", "Exception thrown during pause", e);
            this.mPausingActivity = null;
            this.mLastPausedActivity = null;
            this.mTaskSupervisor.mNoHistoryActivities.remove(activityRecord);
        }
    }

    @VisibleForTesting
    public void completePause(boolean z, ActivityRecord activityRecord) {
        ActivityRecord activityRecord2 = this.mPausingActivity;
        if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 327461496, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
        }
        if (activityRecord2 != null) {
            activityRecord2.setWillCloseOrEnterPip(false);
            ActivityRecord.State state = ActivityRecord.State.STOPPING;
            boolean isState = activityRecord2.isState(state);
            activityRecord2.setState(ActivityRecord.State.PAUSED, "completePausedLocked");
            if (activityRecord2.finishing) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -312353598, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                }
                activityRecord2 = activityRecord2.completeFinishing(false, "completePausedLocked");
            } else if (activityRecord2.attachedToProcess()) {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -1187377055, 60, (String) null, new Object[]{String.valueOf(activityRecord2), Boolean.valueOf(isState), Boolean.valueOf(activityRecord2.isVisibleRequested())});
                }
                if (activityRecord2.deferRelaunchUntilPaused) {
                    if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1011462000, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                    }
                    activityRecord2.relaunchActivityLocked(activityRecord2.preserveWindowOnDeferredRelaunch);
                } else if (isState) {
                    activityRecord2.setState(state, "completePausedLocked");
                } else if (!activityRecord2.isVisibleRequested() || shouldSleepOrShutDownActivities()) {
                    activityRecord2.setDeferHidingClient(false);
                    activityRecord2.addToStopping(true, false, "completePauseLocked");
                }
            } else {
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, -521613870, 0, (String) null, new Object[]{String.valueOf(activityRecord2)});
                }
                activityRecord2 = null;
            }
            if (activityRecord2 != null) {
                activityRecord2.stopFreezingScreenLocked(true);
            }
            this.mPausingActivity = null;
        }
        if (z) {
            Task topDisplayFocusedRootTask = this.mRootWindowContainer.getTopDisplayFocusedRootTask();
            if (topDisplayFocusedRootTask != null && !topDisplayFocusedRootTask.shouldSleepOrShutDownActivities()) {
                this.mRootWindowContainer.resumeFocusedTasksTopActivities(topDisplayFocusedRootTask, activityRecord2, null);
            } else {
                ActivityRecord activityRecord3 = topDisplayFocusedRootTask != null ? topDisplayFocusedRootTask.topRunningActivity() : null;
                if (activityRecord3 == null || (activityRecord2 != null && activityRecord3 != activityRecord2)) {
                    this.mRootWindowContainer.resumeFocusedTasksTopActivities();
                }
            }
        }
        if (activityRecord2 != null) {
            activityRecord2.resumeKeyDispatchingLocked();
        }
        this.mRootWindowContainer.ensureActivitiesVisible(activityRecord, 0, false);
        if (this.mTaskSupervisor.mAppVisibilitiesChangedSinceLastPause || (getDisplayArea() != null && getDisplayArea().hasPinnedTask())) {
            this.mAtmService.getTaskChangeNotificationController().notifyTaskStackChanged();
            this.mTaskSupervisor.mAppVisibilitiesChangedSinceLastPause = false;
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public int getOrientation(int i) {
        if (shouldReportOrientationUnspecified()) {
            return -1;
        }
        if (canSpecifyOrientation()) {
            return super.getOrientation(i);
        }
        return -2;
    }

    public boolean canSpecifyOrientation() {
        int windowingMode = getWindowingMode();
        int activityType = getActivityType();
        return windowingMode == 1 || activityType == 2 || activityType == 3 || activityType == 4;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean providesOrientation() {
        return super.providesOrientation() || shouldReportOrientationUnspecified();
    }

    public final boolean shouldReportOrientationUnspecified() {
        return getAdjacentTaskFragment() != null && isVisibleRequested();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllTaskFragments(Consumer<TaskFragment> consumer, boolean z) {
        super.forAllTaskFragments(consumer, z);
        consumer.accept(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllLeafTaskFragments(Consumer<TaskFragment> consumer, boolean z) {
        int size = this.mChildren.size();
        boolean z2 = true;
        if (z) {
            for (int i = size - 1; i >= 0; i--) {
                TaskFragment asTaskFragment = ((WindowContainer) this.mChildren.get(i)).asTaskFragment();
                if (asTaskFragment != null) {
                    asTaskFragment.forAllLeafTaskFragments(consumer, z);
                    z2 = false;
                }
            }
        } else {
            for (int i2 = 0; i2 < size; i2++) {
                TaskFragment asTaskFragment2 = ((WindowContainer) this.mChildren.get(i2)).asTaskFragment();
                if (asTaskFragment2 != null) {
                    asTaskFragment2.forAllLeafTaskFragments(consumer, z);
                    z2 = false;
                }
            }
        }
        if (z2) {
            consumer.accept(this);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean forAllLeafTaskFragments(Predicate<TaskFragment> predicate) {
        boolean z = true;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            TaskFragment asTaskFragment = ((WindowContainer) this.mChildren.get(size)).asTaskFragment();
            if (asTaskFragment != null) {
                if (asTaskFragment.forAllLeafTaskFragments(predicate)) {
                    return true;
                }
                z = false;
            }
        }
        if (z) {
            return predicate.test(this);
        }
        return false;
    }

    public void addChild(ActivityRecord activityRecord) {
        addChild(activityRecord, Integer.MAX_VALUE);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void addChild(WindowContainer windowContainer, int i) {
        ActivityRecord activityRecord = topRunningActivity();
        this.mClearedTaskForReuse = false;
        this.mClearedTaskFragmentForPip = false;
        this.mClearedForReorderActivityToFront = false;
        ActivityRecord asActivityRecord = windowContainer.asActivityRecord();
        boolean z = asActivityRecord != null;
        Task task = z ? getTask() : null;
        boolean z2 = (task == null || task.getTopMostActivity() == null) ? false : true;
        int activityType = task != null ? task.getActivityType() : 0;
        super.addChild((TaskFragment) windowContainer, i);
        if (!z || task == null) {
            return;
        }
        if (activityRecord != null && BackNavigationController.isScreenshotEnabled()) {
            if (ProtoLogCache.WM_DEBUG_BACK_PREVIEW_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW, -134091882, 0, "Screenshotting Activity %s", new Object[]{String.valueOf(activityRecord.mActivityComponent.flattenToString())});
            }
            Rect bounds = activityRecord.getBounds();
            this.mBackScreenshots.put(activityRecord.mActivityComponent.flattenToString(), ScreenCapture.captureLayers(activityRecord.mSurfaceControl, new Rect(0, 0, bounds.width(), bounds.height()), 1.0f));
        }
        asActivityRecord.inHistory = true;
        task.onDescendantActivityAdded(z2, activityType, asActivityRecord);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void onChildPositionChanged(WindowContainer windowContainer) {
        super.onChildPositionChanged(windowContainer);
        sendTaskFragmentInfoChanged();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public RemoteAnimationTarget createRemoteAnimationTarget(RemoteAnimationController.RemoteAnimationRecord remoteAnimationRecord) {
        ActivityRecord topMostActivity;
        if (remoteAnimationRecord.getMode() == 0) {
            topMostActivity = getActivity(new Predicate() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$createRemoteAnimationTarget$6;
                    lambda$createRemoteAnimationTarget$6 = TaskFragment.lambda$createRemoteAnimationTarget$6((ActivityRecord) obj);
                    return lambda$createRemoteAnimationTarget$6;
                }
            });
        } else {
            topMostActivity = getTopMostActivity();
        }
        if (topMostActivity != null) {
            return topMostActivity.createRemoteAnimationTarget(remoteAnimationRecord);
        }
        return null;
    }

    public static /* synthetic */ boolean lambda$createRemoteAnimationTarget$6(ActivityRecord activityRecord) {
        return !activityRecord.finishing && activityRecord.hasChild();
    }

    public boolean shouldSleepActivities() {
        Task rootTask = getRootTask();
        return rootTask != null && rootTask.shouldSleepActivities();
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void resolveOverrideConfiguration(Configuration configuration) {
        this.mTmpBounds.set(getResolvedOverrideConfiguration().windowConfiguration.getBounds());
        super.resolveOverrideConfiguration(configuration);
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        Rect rect = this.mRelativeEmbeddedBounds;
        if (rect != null && !rect.isEmpty()) {
            resolvedOverrideConfiguration.windowConfiguration.setBounds(translateRelativeBoundsToAbsoluteBounds(this.mRelativeEmbeddedBounds, configuration.windowConfiguration.getBounds()));
        }
        int windowingMode = resolvedOverrideConfiguration.windowConfiguration.getWindowingMode();
        int windowingMode2 = configuration.windowConfiguration.getWindowingMode();
        if (getActivityType() == 2 && windowingMode == 0) {
            resolvedOverrideConfiguration.windowConfiguration.setWindowingMode(1);
            windowingMode = 1;
        }
        if (!supportsMultiWindow()) {
            if (windowingMode != 0) {
                windowingMode2 = windowingMode;
            }
            if (WindowConfiguration.inMultiWindowMode(windowingMode2) && windowingMode2 != 2) {
                resolvedOverrideConfiguration.windowConfiguration.setWindowingMode(1);
            }
        }
        Task asTask = asTask();
        if (asTask != null) {
            asTask.resolveLeafTaskOnlyOverrideConfigs(configuration, this.mTmpBounds);
        }
        computeConfigResourceOverrides(resolvedOverrideConfiguration, configuration);
    }

    public boolean supportsMultiWindow() {
        return supportsMultiWindowInDisplayArea(getDisplayArea());
    }

    public boolean supportsMultiWindowInDisplayArea(TaskDisplayArea taskDisplayArea) {
        Task task;
        if (!this.mAtmService.mSupportsMultiWindow || taskDisplayArea == null || (task = getTask()) == null) {
            return false;
        }
        if (task.isResizeable() || taskDisplayArea.supportsNonResizableMultiWindow()) {
            ActivityRecord rootActivity = task.getRootActivity();
            return taskDisplayArea.supportsActivityMinWidthHeightMultiWindow(this.mMinWidth, this.mMinHeight, rootActivity != null ? rootActivity.info : null);
        }
        return false;
    }

    public void computeConfigResourceOverrides(Configuration configuration, Configuration configuration2) {
        computeConfigResourceOverrides(configuration, configuration2, null, null);
    }

    public void computeConfigResourceOverrides(Configuration configuration, Configuration configuration2, DisplayInfo displayInfo) {
        if (displayInfo != null) {
            configuration.screenLayout = 0;
            invalidateAppBoundsConfig(configuration);
        }
        computeConfigResourceOverrides(configuration, configuration2, displayInfo, null);
    }

    public void computeConfigResourceOverrides(Configuration configuration, Configuration configuration2, ActivityRecord.CompatDisplayInsets compatDisplayInsets) {
        if (compatDisplayInsets != null) {
            invalidateAppBoundsConfig(configuration);
        }
        computeConfigResourceOverrides(configuration, configuration2, null, compatDisplayInsets);
    }

    public static void invalidateAppBoundsConfig(Configuration configuration) {
        Rect appBounds = configuration.windowConfiguration.getAppBounds();
        if (appBounds != null) {
            appBounds.setEmpty();
        }
        configuration.screenWidthDp = 0;
        configuration.screenHeightDp = 0;
    }

    public void computeConfigResourceOverrides(Configuration configuration, Configuration configuration2, DisplayInfo displayInfo, ActivityRecord.CompatDisplayInsets compatDisplayInsets) {
        boolean contains;
        Rect appBounds;
        int windowingMode = configuration.windowConfiguration.getWindowingMode();
        if (windowingMode == 0) {
            windowingMode = configuration2.windowConfiguration.getWindowingMode();
        }
        float f = configuration.densityDpi;
        if (f == 0.0f) {
            f = configuration2.densityDpi;
        }
        float f2 = f * 0.00625f;
        Rect bounds = configuration2.windowConfiguration.getBounds();
        Rect bounds2 = configuration.windowConfiguration.getBounds();
        if (bounds2.isEmpty()) {
            this.mTmpFullBounds.set(bounds);
            contains = true;
        } else {
            this.mTmpFullBounds.set(bounds2);
            contains = bounds.contains(bounds2);
        }
        boolean z = compatDisplayInsets != null;
        Rect appBounds2 = configuration.windowConfiguration.getAppBounds();
        if (appBounds2 == null || appBounds2.isEmpty()) {
            configuration.windowConfiguration.setAppBounds(this.mTmpFullBounds);
            appBounds2 = configuration.windowConfiguration.getAppBounds();
            if (!z && windowingMode != 5) {
                if (contains) {
                    appBounds = configuration2.windowConfiguration.getAppBounds();
                } else {
                    TaskDisplayArea displayArea = getDisplayArea();
                    appBounds = displayArea != null ? displayArea.getWindowConfiguration().getAppBounds() : null;
                }
                if (appBounds != null && !appBounds.isEmpty()) {
                    appBounds2.intersect(appBounds);
                }
            }
        }
        if (configuration.screenWidthDp == 0 || configuration.screenHeightDp == 0) {
            if (!z && WindowConfiguration.isFloating(windowingMode)) {
                this.mTmpNonDecorBounds.set(this.mTmpFullBounds);
                this.mTmpStableBounds.set(this.mTmpFullBounds);
            } else if (!z && (displayInfo != null || getDisplayContent() != null)) {
                calculateInsetFrames(this.mTmpNonDecorBounds, this.mTmpStableBounds, this.mTmpFullBounds, displayInfo != null ? displayInfo : getDisplayContent().getDisplayInfo());
            } else {
                int rotation = configuration.windowConfiguration.getRotation();
                if (rotation == -1) {
                    rotation = configuration2.windowConfiguration.getRotation();
                }
                if (rotation != -1 && z) {
                    this.mTmpNonDecorBounds.set(this.mTmpFullBounds);
                    this.mTmpStableBounds.set(this.mTmpFullBounds);
                    compatDisplayInsets.getBoundsByRotation(this.mTmpBounds, rotation);
                    intersectWithInsetsIfFits(this.mTmpNonDecorBounds, this.mTmpBounds, compatDisplayInsets.mNonDecorInsets[rotation]);
                    intersectWithInsetsIfFits(this.mTmpStableBounds, this.mTmpBounds, compatDisplayInsets.mStableInsets[rotation]);
                    appBounds2.set(this.mTmpNonDecorBounds);
                } else {
                    this.mTmpNonDecorBounds.set(appBounds2);
                    this.mTmpStableBounds.set(appBounds2);
                }
            }
            if (configuration.screenWidthDp == 0) {
                int width = (int) ((this.mTmpStableBounds.width() / f2) + 0.5f);
                if (contains && !z) {
                    width = Math.min(width, configuration2.screenWidthDp);
                }
                configuration.screenWidthDp = width;
            }
            if (configuration.screenHeightDp == 0) {
                int height = (int) ((this.mTmpStableBounds.height() / f2) + 0.5f);
                if (contains && !z) {
                    height = Math.min(height, configuration2.screenHeightDp);
                }
                configuration.screenHeightDp = height;
            }
            if (configuration.smallestScreenWidthDp == 0) {
                boolean z2 = windowingMode == 2 && !this.mTmpFullBounds.isEmpty() && this.mTmpFullBounds.equals(bounds);
                if (WindowConfiguration.isFloating(windowingMode) && !z2) {
                    configuration.smallestScreenWidthDp = (int) ((Math.min(this.mTmpFullBounds.width(), this.mTmpFullBounds.height()) / f2) + 0.5f);
                } else if (windowingMode == 6 && isEmbeddedWithBoundsOverride()) {
                    configuration.smallestScreenWidthDp = Math.min(configuration.screenWidthDp, configuration.screenHeightDp);
                }
            }
        }
        if (configuration.orientation == 0) {
            configuration.orientation = configuration.screenWidthDp <= configuration.screenHeightDp ? 1 : 2;
        }
        if (configuration.screenLayout == 0) {
            int width2 = (int) ((this.mTmpNonDecorBounds.width() / f2) + 0.5f);
            int height2 = (int) ((this.mTmpNonDecorBounds.height() / f2) + 0.5f);
            int i = configuration.screenWidthDp;
            if (i != 0) {
                width2 = i;
            }
            int i2 = configuration.screenHeightDp;
            if (i2 != 0) {
                height2 = i2;
            }
            configuration.screenLayout = WindowContainer.computeScreenLayout(configuration2.screenLayout, width2, height2);
        }
    }

    public void calculateInsetFrames(Rect rect, Rect rect2, Rect rect3, DisplayInfo displayInfo) {
        rect.set(rect3);
        rect2.set(rect3);
        if (this.mDisplayContent == null) {
            return;
        }
        this.mTmpBounds.set(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        DisplayPolicy.DecorInsets.Info decorInsetsInfo = this.mDisplayContent.getDisplayPolicy().getDecorInsetsInfo(displayInfo.rotation, displayInfo.logicalWidth, displayInfo.logicalHeight);
        intersectWithInsetsIfFits(rect, this.mTmpBounds, decorInsetsInfo.mNonDecorInsets);
        intersectWithInsetsIfFits(rect2, this.mTmpBounds, decorInsetsInfo.mConfigInsets);
    }

    public static void intersectWithInsetsIfFits(Rect rect, Rect rect2, Rect rect3) {
        int i = rect.right;
        int i2 = rect2.right;
        if (i <= i2) {
            rect.right = Math.min(i2 - rect3.right, i);
        }
        int i3 = rect.bottom;
        int i4 = rect2.bottom;
        if (i3 <= i4) {
            rect.bottom = Math.min(i4 - rect3.bottom, i3);
        }
        int i5 = rect.left;
        int i6 = rect2.left;
        if (i5 >= i6) {
            rect.left = Math.max(i6 + rect3.left, i5);
        }
        int i7 = rect.top;
        int i8 = rect2.top;
        if (i7 >= i8) {
            rect.top = Math.max(i8 + rect3.top, i7);
        }
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public int getActivityType() {
        int activityType = super.getActivityType();
        if (activityType == 0 && hasChild()) {
            ActivityRecord topMostActivity = getTopMostActivity();
            return topMostActivity != null ? topMostActivity.getActivityType() : getTopChild().getActivityType();
        }
        return activityType;
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateOrganizedTaskFragmentSurface();
        sendTaskFragmentInfoChanged();
    }

    public void deferOrganizedTaskFragmentSurfaceUpdate() {
        this.mDelayOrganizedTaskFragmentSurfaceUpdate = true;
    }

    public void continueOrganizedTaskFragmentSurfaceUpdate() {
        this.mDelayOrganizedTaskFragmentSurfaceUpdate = false;
        updateOrganizedTaskFragmentSurface();
    }

    public void updateOrganizedTaskFragmentSurface() {
        if (this.mDelayOrganizedTaskFragmentSurfaceUpdate || this.mTaskFragmentOrganizer == null) {
            return;
        }
        if (this.mTransitionController.isShellTransitionsEnabled() && !this.mTransitionController.isCollecting(this)) {
            updateOrganizedTaskFragmentSurfaceUnchecked();
        } else if (this.mTransitionController.isShellTransitionsEnabled() || isAnimating()) {
        } else {
            updateOrganizedTaskFragmentSurfaceUnchecked();
        }
    }

    public final void updateOrganizedTaskFragmentSurfaceUnchecked() {
        SurfaceControl.Transaction syncTransaction = getSyncTransaction();
        updateSurfacePosition(syncTransaction);
        updateOrganizedTaskFragmentSurfaceSize(syncTransaction, false);
    }

    public final void updateOrganizedTaskFragmentSurfaceSize(SurfaceControl.Transaction transaction, boolean z) {
        Rect bounds;
        if (this.mTaskFragmentOrganizer == null || this.mSurfaceControl == null || this.mSurfaceAnimator.hasLeash() || this.mSurfaceFreezer.hasLeash()) {
            return;
        }
        if (isClosingWhenResizing()) {
            bounds = this.mDisplayContent.mClosingChangingContainers.get(this);
        } else {
            bounds = getBounds();
        }
        int width = bounds.width();
        int height = bounds.height();
        if (!z) {
            Point point = this.mLastSurfaceSize;
            if (width == point.x && height == point.y) {
                return;
            }
        }
        transaction.setWindowCrop(this.mSurfaceControl, width, height);
        this.mLastSurfaceSize.set(width, height);
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashCreated(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        super.onAnimationLeashCreated(transaction, surfaceControl);
        if (this.mTaskFragmentOrganizer != null) {
            Point point = this.mLastSurfaceSize;
            if (point.x == 0 && point.y == 0) {
                return;
            }
            transaction.setWindowCrop(this.mSurfaceControl, 0, 0);
            SurfaceControl.Transaction syncTransaction = getSyncTransaction();
            if (transaction != syncTransaction) {
                syncTransaction.setWindowCrop(this.mSurfaceControl, 0, 0);
            }
            this.mLastSurfaceSize.set(0, 0);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashLost(SurfaceControl.Transaction transaction) {
        super.onAnimationLeashLost(transaction);
        if (this.mTaskFragmentOrganizer != null) {
            updateOrganizedTaskFragmentSurfaceSize(transaction, true);
        }
    }

    public Rect getRelativeEmbeddedBounds() {
        Rect rect = this.mRelativeEmbeddedBounds;
        if (rect != null) {
            return rect;
        }
        throw new IllegalStateException("The TaskFragment is not embedded");
    }

    public Rect translateRelativeBoundsToAbsoluteBounds(Rect rect, Rect rect2) {
        if (rect.isEmpty()) {
            this.mTmpAbsBounds.setEmpty();
            return this.mTmpAbsBounds;
        }
        this.mTmpAbsBounds.set(rect);
        this.mTmpAbsBounds.offset(rect2.left, rect2.top);
        if (!isAllowedToBeEmbeddedInTrustedMode() && !rect2.contains(this.mTmpAbsBounds) && !this.mTmpAbsBounds.intersect(rect2)) {
            this.mTmpAbsBounds.setEmpty();
        }
        return this.mTmpAbsBounds;
    }

    public void recomputeConfiguration() {
        onRequestedOverrideConfigurationChanged(getRequestedOverrideConfiguration());
    }

    public void setRelativeEmbeddedBounds(Rect rect) {
        Rect rect2 = this.mRelativeEmbeddedBounds;
        if (rect2 == null) {
            throw new IllegalStateException("The TaskFragment is not embedded");
        }
        if (rect2.equals(rect)) {
            return;
        }
        this.mRelativeEmbeddedBounds.set(rect);
    }

    public boolean shouldStartChangeTransition(Rect rect, Rect rect2) {
        if (this.mTaskFragmentOrganizer == null || !canStartChangeTransition()) {
            return false;
        }
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            Rect bounds = getConfiguration().windowConfiguration.getBounds();
            return (bounds.width() == rect.width() && bounds.height() == rect.height()) ? false : true;
        }
        return !rect2.equals(this.mRelativeEmbeddedBounds);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canStartChangeTransition() {
        Task task = getTask();
        return (task == null || task.isDragResizing() || !super.canStartChangeTransition()) ? false : true;
    }

    public void setClosingChangingStartBoundsIfNeeded() {
        DisplayContent displayContent;
        if (isOrganizedTaskFragment() && (displayContent = this.mDisplayContent) != null && displayContent.mChangingContainers.remove(this)) {
            this.mDisplayContent.mClosingChangingContainers.put(this, new Rect(this.mSurfaceFreezer.mFreezeBounds));
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isSyncFinished() {
        return super.isSyncFinished() && isReadyToTransit();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void setSurfaceControl(SurfaceControl surfaceControl) {
        super.setSurfaceControl(surfaceControl);
        if (this.mTaskFragmentOrganizer != null) {
            updateOrganizedTaskFragmentSurfaceUnchecked();
            sendTaskFragmentAppeared();
        }
    }

    public void sendTaskFragmentInfoChanged() {
        ITaskFragmentOrganizer iTaskFragmentOrganizer = this.mTaskFragmentOrganizer;
        if (iTaskFragmentOrganizer != null) {
            this.mTaskFragmentOrganizerController.onTaskFragmentInfoChanged(iTaskFragmentOrganizer, this);
        }
    }

    public void sendTaskFragmentParentInfoChanged() {
        Task asTask = getParent().asTask();
        ITaskFragmentOrganizer iTaskFragmentOrganizer = this.mTaskFragmentOrganizer;
        if (iTaskFragmentOrganizer == null || asTask == null) {
            return;
        }
        this.mTaskFragmentOrganizerController.onTaskFragmentParentInfoChanged(iTaskFragmentOrganizer, asTask);
    }

    public final void sendTaskFragmentAppeared() {
        ITaskFragmentOrganizer iTaskFragmentOrganizer = this.mTaskFragmentOrganizer;
        if (iTaskFragmentOrganizer != null) {
            this.mTaskFragmentOrganizerController.onTaskFragmentAppeared(iTaskFragmentOrganizer, this);
        }
    }

    public final void sendTaskFragmentVanished() {
        ITaskFragmentOrganizer iTaskFragmentOrganizer = this.mTaskFragmentOrganizer;
        if (iTaskFragmentOrganizer != null) {
            this.mTaskFragmentOrganizerController.onTaskFragmentVanished(iTaskFragmentOrganizer, this);
        }
    }

    public TaskFragmentInfo getTaskFragmentInfo() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < getChildCount(); i++) {
            ActivityRecord asActivityRecord = getChildAt(i).asActivityRecord();
            if (this.mTaskFragmentOrganizerUid != -1 && asActivityRecord != null && asActivityRecord.info.processName.equals(this.mTaskFragmentOrganizerProcessName) && asActivityRecord.getUid() == this.mTaskFragmentOrganizerUid && !asActivityRecord.finishing) {
                arrayList.add(asActivityRecord.token);
                if (asActivityRecord.mRequestedLaunchingTaskFragmentToken == this.mFragmentToken) {
                    arrayList2.add(asActivityRecord.token);
                }
            }
        }
        Point point = new Point();
        getRelativePosition(point);
        return new TaskFragmentInfo(this.mFragmentToken, this.mRemoteToken.toWindowContainerToken(), getConfiguration(), getNonFinishingActivityCount(), shouldBeVisible(null), arrayList, arrayList2, point, this.mClearedTaskForReuse, this.mClearedTaskFragmentForPip, this.mClearedForReorderActivityToFront, calculateMinDimension());
    }

    public Point calculateMinDimension() {
        final int[] iArr = new int[1];
        final int[] iArr2 = new int[1];
        forAllActivities(new Consumer() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TaskFragment.lambda$calculateMinDimension$7(iArr, iArr2, (ActivityRecord) obj);
            }
        });
        return new Point(iArr[0], iArr2[0]);
    }

    public static /* synthetic */ void lambda$calculateMinDimension$7(int[] iArr, int[] iArr2, ActivityRecord activityRecord) {
        Point minDimensions;
        if (activityRecord.finishing || (minDimensions = activityRecord.getMinDimensions()) == null) {
            return;
        }
        iArr[0] = Math.max(iArr[0], minDimensions.x);
        iArr2[0] = Math.max(iArr2[0], minDimensions.y);
    }

    public IBinder getFragmentToken() {
        return this.mFragmentToken;
    }

    public ITaskFragmentOrganizer getTaskFragmentOrganizer() {
        return this.mTaskFragmentOrganizer;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isOrganized() {
        return this.mTaskFragmentOrganizer != null;
    }

    public final boolean isOrganizedTaskFragment() {
        return this.mTaskFragmentOrganizer != null;
    }

    public boolean isEmbeddedWithBoundsOverride() {
        Task task;
        if (this.mIsEmbedded && (task = getTask()) != null) {
            Rect bounds = task.getBounds();
            Rect bounds2 = getBounds();
            return !bounds.equals(bounds2) && bounds.contains(bounds2);
        }
        return false;
    }

    public boolean isTaskVisibleRequested() {
        Task task = getTask();
        return task != null && task.isVisibleRequested();
    }

    public boolean isReadyToTransit() {
        if (isOrganizedTaskFragment() && getTopNonFinishingActivity() == null && !this.mIsRemovalRequested && !isEmbeddedTaskFragmentInPip()) {
            return this.mClearedTaskFragmentForPip && !isTaskVisibleRequested();
        }
        return true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean canCustomizeAppTransition() {
        return isEmbedded() && matchParentBounds();
    }

    public void clearLastPausedActivity() {
        forAllTaskFragments(new Consumer() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskFragment) obj).mLastPausedActivity = null;
            }
        });
    }

    public void setMinDimensions(int i, int i2) {
        if (asTask() != null) {
            throw new UnsupportedOperationException("This method must not be used to Task. The  minimum dimension of Task should be passed from Task constructor.");
        }
        this.mMinWidth = i;
        this.mMinHeight = i2;
    }

    public boolean isEmbeddedTaskFragmentInPip() {
        return isOrganizedTaskFragment() && getTask() != null && getTask().inPinnedWindowingMode();
    }

    public boolean shouldRemoveSelfOnLastChildRemoval() {
        return !this.mCreatedByOrganizer || this.mIsRemovalRequested;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeChild(WindowContainer windowContainer) {
        removeChild(windowContainer, true);
    }

    public void removeChild(WindowContainer windowContainer, boolean z) {
        ActivityRecord asActivityRecord;
        super.removeChild(windowContainer);
        if (BackNavigationController.isScreenshotEnabled() && (asActivityRecord = windowContainer.asActivityRecord()) != null) {
            this.mBackScreenshots.remove(asActivityRecord.mActivityComponent.flattenToString());
        }
        if (z && shouldRemoveSelfOnLastChildRemoval() && !hasChild()) {
            removeImmediately("removeLastChild " + windowContainer);
        }
    }

    public void remove(boolean z, String str) {
        if (!hasChild()) {
            removeImmediately(str);
            return;
        }
        this.mIsRemovalRequested = true;
        ArrayList arrayList = new ArrayList();
        forAllActivities(new Task$$ExternalSyntheticLambda46(arrayList));
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ActivityRecord activityRecord = (ActivityRecord) arrayList.get(size);
            if (z && activityRecord.isVisible()) {
                activityRecord.finishIfPossible(str, false);
            } else {
                activityRecord.destroyIfPossible(str);
            }
        }
    }

    public void setDelayLastActivityRemoval(boolean z) {
        if (!this.mIsEmbedded) {
            Slog.w("ActivityTaskManager", "Set delaying last activity removal on a non-embedded TF.");
        }
        this.mDelayLastActivityRemoval = z;
    }

    public boolean isDelayLastActivityRemoval() {
        return this.mDelayLastActivityRemoval;
    }

    public boolean shouldDeferRemoval() {
        if (hasChild()) {
            return isExitAnimationRunningSelfOrChild();
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean handleCompleteDeferredRemoval() {
        if (shouldDeferRemoval()) {
            return true;
        }
        return super.handleCompleteDeferredRemoval();
    }

    public void removeImmediately(String str) {
        Slog.d("ActivityTaskManager", "Remove task fragment: " + str);
        removeImmediately();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeImmediately() {
        boolean z = false;
        this.mIsRemovalRequested = false;
        resetAdjacentTaskFragment();
        cleanUpEmbeddedTaskFragment();
        if (this.mClearedTaskFragmentForPip && isTaskVisibleRequested()) {
            z = true;
        }
        super.removeImmediately();
        sendTaskFragmentVanished();
        if (!z || this.mDisplayContent == null) {
            return;
        }
        this.mAtmService.addWindowLayoutReasons(2);
        this.mDisplayContent.executeAppTransition();
    }

    public final void cleanUpEmbeddedTaskFragment() {
        if (this.mIsEmbedded) {
            this.mAtmService.mWindowOrganizerController.cleanUpEmbeddedTaskFragment(this);
            Task task = getTask();
            if (task == null) {
                return;
            }
            task.forAllLeafTaskFragments(new Consumer() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TaskFragment.this.lambda$cleanUpEmbeddedTaskFragment$9((TaskFragment) obj);
                }
            }, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanUpEmbeddedTaskFragment$9(TaskFragment taskFragment) {
        if (taskFragment.getCompanionTaskFragment() == this) {
            taskFragment.setCompanionTaskFragment(null);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public Dimmer getDimmer() {
        if (asTask() == null) {
            return this.mDimmer;
        }
        return super.getDimmer();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void prepareSurfaces() {
        if (asTask() != null) {
            super.prepareSurfaces();
            return;
        }
        this.mDimmer.resetDimStates();
        super.prepareSurfaces();
        Rect bounds = getBounds();
        bounds.offsetTo(0, 0);
        if (this.mDimmer.updateDims(getSyncTransaction(), bounds)) {
            scheduleAnimation();
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean fillsParent() {
        return getWindowingMode() == 1 || matchParentBounds();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean onChildVisibleRequestedChanged(WindowContainer windowContainer) {
        if (super.onChildVisibleRequestedChanged(windowContainer)) {
            sendTaskFragmentInfoChanged();
            return true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public TaskFragment getTaskFragment(Predicate<TaskFragment> predicate) {
        TaskFragment taskFragment = super.getTaskFragment(predicate);
        if (taskFragment != null) {
            return taskFragment;
        }
        if (predicate.test(this)) {
            return this;
        }
        return null;
    }

    public boolean moveChildToFront(WindowContainer windowContainer) {
        int distanceFromTop = getDistanceFromTop(windowContainer);
        positionChildAt(Integer.MAX_VALUE, windowContainer, false);
        return getDistanceFromTop(windowContainer) != distanceFromTop;
    }

    public String toFullString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(this);
        sb.setLength(sb.length() - 1);
        if (this.mTaskFragmentOrganizerUid != -1) {
            sb.append(" organizerUid=");
            sb.append(this.mTaskFragmentOrganizerUid);
        }
        if (this.mTaskFragmentOrganizerProcessName != null) {
            sb.append(" organizerProc=");
            sb.append(this.mTaskFragmentOrganizerProcessName);
        }
        if (this.mAdjacentTaskFragment != null) {
            sb.append(" adjacent=");
            sb.append(this.mAdjacentTaskFragment);
        }
        sb.append('}');
        return sb.toString();
    }

    public String toString() {
        return "TaskFragment{" + Integer.toHexString(System.identityHashCode(this)) + " mode=" + WindowConfiguration.windowingModeToString(getWindowingMode()) + "}";
    }

    public boolean dump(final String str, FileDescriptor fileDescriptor, final PrintWriter printWriter, final boolean z, boolean z2, final String str2, final boolean z3, final Runnable runnable) {
        boolean z4;
        Runnable runnable2 = new Runnable() { // from class: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                TaskFragment.this.lambda$dump$10(z3, printWriter, runnable, str, z, str2);
            }
        };
        if (str2 == null) {
            runnable2.run();
            runnable2 = null;
            z4 = true;
        } else {
            z4 = false;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowContainer windowContainer = (WindowContainer) this.mChildren.get(size);
            if (windowContainer.asTaskFragment() != null) {
                z4 = windowContainer.asTaskFragment().dump(str + "  ", fileDescriptor, printWriter, z, z2, str2, z3, runnable2) | z4;
            } else if (windowContainer.asActivityRecord() != null) {
                ActivityRecord.dumpActivity(fileDescriptor, printWriter, size, windowContainer.asActivityRecord(), str + "  ", "Hist ", true, !z, z2, str2, false, runnable2, getTask());
            }
        }
        return z4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dump$10(boolean z, PrintWriter printWriter, Runnable runnable, String str, boolean z2, String str2) {
        if (z) {
            printWriter.println();
        }
        if (runnable != null) {
            runnable.run();
        }
        dumpInner(str, printWriter, z2, str2);
    }

    public void dumpInner(String str, PrintWriter printWriter, boolean z, String str2) {
        printWriter.print(str);
        printWriter.print("* ");
        printWriter.println(toFullString());
        Rect requestedOverrideBounds = getRequestedOverrideBounds();
        if (!requestedOverrideBounds.isEmpty()) {
            printWriter.println(str + "  mBounds=" + requestedOverrideBounds);
        }
        if (this.mIsRemovalRequested) {
            printWriter.println(str + "  mIsRemovalRequested=true");
        }
        if (z) {
            ActivityRecord activityRecord = this.mLastPausedActivity;
            ActivityTaskSupervisor.printThisActivity(printWriter, activityRecord, str2, false, str + "  mLastPausedActivity: ", null);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void dump(PrintWriter printWriter, String str, boolean z) {
        super.dump(printWriter, str, z);
        printWriter.println(str + "bounds=" + getBounds().toShortString());
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowContainer windowContainer = (WindowContainer) this.mChildren.get(size);
            TaskFragment asTaskFragment = windowContainer.asTaskFragment();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append("* ");
            sb3.append((Object) (asTaskFragment != null ? asTaskFragment.toFullString() : windowContainer));
            printWriter.println(sb3.toString());
            if (asTaskFragment != null) {
                windowContainer.dump(printWriter, sb2, z);
            }
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void writeIdentifierToProto(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, System.identityHashCode(this));
        ActivityRecord activityRecord = topRunningActivity();
        protoOutputStream.write(1120986464258L, activityRecord != null ? activityRecord.mUserId : -10000);
        protoOutputStream.write(1138166333443L, activityRecord != null ? activityRecord.intent.getComponent().flattenToShortString() : "TaskFragment");
        protoOutputStream.end(start);
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, int i) {
        if (i != 2 || isVisible()) {
            long start = protoOutputStream.start(j);
            super.dumpDebug(protoOutputStream, 1146756268033L, i);
            protoOutputStream.write(1120986464258L, getDisplayId());
            protoOutputStream.write(1120986464259L, getActivityType());
            protoOutputStream.write(1120986464260L, this.mMinWidth);
            protoOutputStream.write(1120986464261L, this.mMinHeight);
            protoOutputStream.end(start);
        }
    }
}
