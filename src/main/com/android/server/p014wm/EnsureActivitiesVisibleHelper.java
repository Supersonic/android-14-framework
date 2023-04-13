package com.android.server.p014wm;

import com.android.server.p014wm.ActivityRecord;
import java.util.ArrayList;
/* renamed from: com.android.server.wm.EnsureActivitiesVisibleHelper */
/* loaded from: classes2.dex */
public class EnsureActivitiesVisibleHelper {
    public boolean mAboveTop;
    public boolean mBehindFullyOccludedContainer;
    public int mConfigChanges;
    public boolean mContainerShouldBeVisible;
    public boolean mNotifyClients;
    public boolean mPreserveWindows;
    public ActivityRecord mStarting;
    public final TaskFragment mTaskFragment;
    public ActivityRecord mTopRunningActivity;

    public EnsureActivitiesVisibleHelper(TaskFragment taskFragment) {
        this.mTaskFragment = taskFragment;
    }

    public void reset(ActivityRecord activityRecord, int i, boolean z, boolean z2) {
        this.mStarting = activityRecord;
        ActivityRecord activityRecord2 = this.mTaskFragment.topRunningActivity();
        this.mTopRunningActivity = activityRecord2;
        this.mAboveTop = activityRecord2 != null;
        boolean shouldBeVisible = this.mTaskFragment.shouldBeVisible(this.mStarting);
        this.mContainerShouldBeVisible = shouldBeVisible;
        this.mBehindFullyOccludedContainer = !shouldBeVisible;
        this.mConfigChanges = i;
        this.mPreserveWindows = z;
        this.mNotifyClients = z2;
    }

    public void process(ActivityRecord activityRecord, int i, boolean z, boolean z2) {
        reset(activityRecord, i, z, z2);
        if (this.mTopRunningActivity != null && this.mTaskFragment.asTask() != null) {
            this.mTaskFragment.asTask().checkTranslucentActivityWaiting(this.mTopRunningActivity);
        }
        ActivityRecord activityRecord2 = this.mTopRunningActivity;
        boolean z3 = activityRecord2 != null && !activityRecord2.mLaunchTaskBehind && this.mTaskFragment.canBeResumed(activityRecord) && (activityRecord == null || !activityRecord.isDescendantOf(this.mTaskFragment));
        ArrayList arrayList = null;
        for (int size = this.mTaskFragment.mChildren.size() - 1; size >= 0; size--) {
            WindowContainer windowContainer = (WindowContainer) this.mTaskFragment.mChildren.get(size);
            TaskFragment asTaskFragment = windowContainer.asTaskFragment();
            if (asTaskFragment != null && asTaskFragment.getTopNonFinishingActivity() != null) {
                asTaskFragment.updateActivityVisibilities(activityRecord, i, z, z2);
                this.mBehindFullyOccludedContainer |= asTaskFragment.getBounds().equals(this.mTaskFragment.getBounds()) && !asTaskFragment.isTranslucent(activityRecord);
                if (this.mAboveTop && this.mTopRunningActivity.getTaskFragment() == asTaskFragment) {
                    this.mAboveTop = false;
                }
                if (!this.mBehindFullyOccludedContainer) {
                    if (arrayList != null && arrayList.contains(asTaskFragment)) {
                        if (!asTaskFragment.isTranslucent(activityRecord) && !asTaskFragment.getAdjacentTaskFragment().isTranslucent(activityRecord)) {
                            this.mBehindFullyOccludedContainer = true;
                        }
                    } else {
                        TaskFragment adjacentTaskFragment = asTaskFragment.getAdjacentTaskFragment();
                        if (adjacentTaskFragment != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(adjacentTaskFragment);
                        }
                    }
                }
            } else if (windowContainer.asActivityRecord() != null) {
                setActivityVisibilityState(windowContainer.asActivityRecord(), activityRecord, z3);
            }
        }
    }

    public final void setActivityVisibilityState(ActivityRecord activityRecord, ActivityRecord activityRecord2, boolean z) {
        boolean z2 = activityRecord == this.mTopRunningActivity;
        if (this.mAboveTop && !z2) {
            activityRecord.makeInvisible();
            return;
        }
        this.mAboveTop = false;
        activityRecord.updateVisibilityIgnoringKeyguard(this.mBehindFullyOccludedContainer);
        boolean shouldBeVisibleUnchecked = activityRecord.shouldBeVisibleUnchecked();
        if (activityRecord.visibleIgnoringKeyguard) {
            if (activityRecord.occludesParent()) {
                this.mBehindFullyOccludedContainer = true;
            } else {
                this.mBehindFullyOccludedContainer = false;
            }
        } else if (activityRecord.isState(ActivityRecord.State.INITIALIZING)) {
            activityRecord.cancelInitializing();
        }
        if (shouldBeVisibleUnchecked) {
            if (activityRecord.finishing) {
                return;
            }
            if (activityRecord != this.mStarting && this.mNotifyClients) {
                activityRecord.ensureActivityConfiguration(0, this.mPreserveWindows, true);
            }
            if (!activityRecord.attachedToProcess()) {
                makeVisibleAndRestartIfNeeded(this.mStarting, this.mConfigChanges, z2, z && z2, activityRecord);
            } else if (activityRecord.isVisibleRequested()) {
                boolean z3 = activityRecord.mClientVisibilityDeferred;
                if (z3 && this.mNotifyClients) {
                    if (z3) {
                        activityRecord2 = null;
                    }
                    activityRecord.makeActiveIfNeeded(activityRecord2);
                    activityRecord.mClientVisibilityDeferred = false;
                }
                activityRecord.handleAlreadyVisible();
                if (this.mNotifyClients) {
                    activityRecord.makeActiveIfNeeded(this.mStarting);
                }
            } else {
                activityRecord.makeVisibleIfNeeded(this.mStarting, this.mNotifyClients);
            }
            this.mConfigChanges |= activityRecord.configChangeFlags;
        } else {
            activityRecord.makeInvisible();
        }
        if (!this.mBehindFullyOccludedContainer && this.mTaskFragment.isActivityTypeHome() && activityRecord.isRootOfTask()) {
            this.mBehindFullyOccludedContainer = true;
        }
    }

    public final void makeVisibleAndRestartIfNeeded(ActivityRecord activityRecord, int i, boolean z, boolean z2, ActivityRecord activityRecord2) {
        if (z || !activityRecord2.isVisibleRequested() || activityRecord2.isState(ActivityRecord.State.INITIALIZING)) {
            if (activityRecord2 != activityRecord) {
                activityRecord2.startFreezingScreenLocked(i);
            }
            if (!activityRecord2.isVisibleRequested() || activityRecord2.mLaunchTaskBehind) {
                activityRecord2.setVisibility(true);
            }
            if (activityRecord2 != activityRecord) {
                this.mTaskFragment.mTaskSupervisor.startSpecificActivity(activityRecord2, z2, true);
            }
        }
    }
}
