package com.android.server.p014wm;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerService;
import com.android.server.p014wm.KeyguardController;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.KeyguardController */
/* loaded from: classes2.dex */
public class KeyguardController {
    public final SparseArray<KeyguardDisplayState> mDisplayStates = new SparseArray<>();
    public final Runnable mResetWaitTransition = new Runnable() { // from class: com.android.server.wm.KeyguardController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            KeyguardController.this.lambda$new$0();
        }
    };
    public RootWindowContainer mRootWindowContainer;
    public final ActivityTaskManagerService mService;
    public final ActivityTaskManagerInternal.SleepTokenAcquirer mSleepTokenAcquirer;
    public final ActivityTaskSupervisor mTaskSupervisor;
    public boolean mWaitingForWakeTransition;
    public WindowManagerService mWindowManager;

    public final int convertTransitFlags(int i) {
        int i2 = (i & 1) != 0 ? FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP : 256;
        if ((i & 2) != 0) {
            i2 |= 2;
        }
        if ((i & 4) != 0) {
            i2 |= 4;
        }
        if ((i & 8) != 0) {
            i2 |= 8;
        }
        return (i & 16) != 0 ? i2 | 22 : i2;
    }

    public KeyguardController(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor) {
        this.mService = activityTaskManagerService;
        this.mTaskSupervisor = activityTaskSupervisor;
        Objects.requireNonNull(activityTaskManagerService);
        this.mSleepTokenAcquirer = new ActivityTaskManagerService.SleepTokenAcquirerImpl("keyguard");
    }

    public void setWindowManager(WindowManagerService windowManagerService) {
        this.mWindowManager = windowManagerService;
        this.mRootWindowContainer = this.mService.mRootWindowContainer;
    }

    public boolean isAodShowing(int i) {
        return getDisplayState(i).mAodShowing;
    }

    public boolean isKeyguardOrAodShowing(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        return ((!displayState.mKeyguardShowing && !displayState.mAodShowing) || displayState.mKeyguardGoingAway || isDisplayOccluded(i)) ? false : true;
    }

    public boolean isKeyguardUnoccludedOrAodShowing(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        if (i == 0 && displayState.mAodShowing) {
            return !displayState.mKeyguardGoingAway;
        }
        return isKeyguardOrAodShowing(i);
    }

    public boolean isKeyguardShowing(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        return (!displayState.mKeyguardShowing || displayState.mKeyguardGoingAway || isDisplayOccluded(i)) ? false : true;
    }

    public boolean isKeyguardLocked(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        return displayState.mKeyguardShowing && !displayState.mKeyguardGoingAway;
    }

    public boolean topActivityOccludesKeyguard(ActivityRecord activityRecord) {
        return getDisplayState(activityRecord.getDisplayId()).mTopOccludesActivity == activityRecord;
    }

    public boolean isKeyguardGoingAway(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        return displayState.mKeyguardGoingAway && displayState.mKeyguardShowing;
    }

    public void setKeyguardShown(int i, boolean z, boolean z2) {
        if (this.mRootWindowContainer.getDisplayContent(i).isKeyguardAlwaysUnlocked()) {
            Slog.i("ActivityTaskManager", "setKeyguardShown ignoring always unlocked display " + i);
            return;
        }
        KeyguardDisplayState displayState = getDisplayState(i);
        boolean z3 = true;
        Object[] objArr = z2 != displayState.mAodShowing ? 1 : null;
        Object[] objArr2 = (!displayState.mAodShowing || z2) ? null : 1;
        if (z == displayState.mKeyguardShowing && (!displayState.mKeyguardGoingAway || !z || objArr2 != null)) {
            z3 = false;
        }
        if (objArr2 != null) {
            updateDeferTransitionForAod(false);
        }
        if (!z3 && objArr == null) {
            setWakeTransitionReady();
            return;
        }
        EventLogTags.writeWmSetKeyguardShown(i, z ? 1 : 0, z2 ? 1 : 0, displayState.mKeyguardGoingAway ? 1 : 0, displayState.mOccluded ? 1 : 0, "setKeyguardShown");
        if ((((z2 ? 1 : 0) ^ (z ? 1 : 0)) || (z2 && objArr != null && z3)) && !displayState.mKeyguardGoingAway && Display.isOnState(this.mRootWindowContainer.getDefaultDisplay().getDisplayInfo().state)) {
            this.mWindowManager.mTaskSnapshotController.snapshotForSleeping(0);
        }
        displayState.mKeyguardShowing = z;
        displayState.mAodShowing = z2;
        if (z3) {
            displayState.mKeyguardGoingAway = false;
            if (z) {
                displayState.mDismissalRequested = false;
            }
        }
        updateKeyguardSleepToken();
        this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
        InputMethodManagerInternal.get().updateImeWindowStatus(false);
        setWakeTransitionReady();
        if (objArr != null) {
            this.mWindowManager.mWindowPlacerLocked.performSurfacePlacement();
        }
    }

    public final void setWakeTransitionReady() {
        if (this.mWindowManager.mAtmService.getTransitionController().getCollectingTransitionType() == 11) {
            this.mWindowManager.mAtmService.getTransitionController().setReady(this.mRootWindowContainer.getDefaultDisplay());
        }
    }

    public void keyguardGoingAway(int i, int i2) {
        KeyguardDisplayState displayState = getDisplayState(i);
        if (!displayState.mKeyguardShowing || displayState.mKeyguardGoingAway) {
            return;
        }
        Trace.traceBegin(32L, "keyguardGoingAway");
        this.mService.deferWindowLayout();
        displayState.mKeyguardGoingAway = true;
        try {
            EventLogTags.writeWmSetKeyguardShown(i, displayState.mKeyguardShowing ? 1 : 0, displayState.mAodShowing ? 1 : 0, 1, displayState.mOccluded ? 1 : 0, "keyguardGoingAway");
            int convertTransitFlags = convertTransitFlags(i2);
            DisplayContent defaultDisplay = this.mRootWindowContainer.getDefaultDisplay();
            defaultDisplay.prepareAppTransition(7, convertTransitFlags);
            defaultDisplay.mAtmService.getTransitionController().requestTransitionIfNeeded(4, convertTransitFlags, null, defaultDisplay);
            updateKeyguardSleepToken();
            this.mRootWindowContainer.resumeFocusedTasksTopActivities();
            this.mRootWindowContainer.ensureActivitiesVisible(null, 0, false);
            this.mRootWindowContainer.addStartingWindowsForVisibleActivities();
            this.mWindowManager.executeAppTransition();
        } finally {
            this.mService.continueWindowLayout();
            Trace.traceEnd(32L);
        }
    }

    public void dismissKeyguard(IBinder iBinder, IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(iBinder);
        if (forTokenLocked == null || !forTokenLocked.visibleIgnoringKeyguard) {
            failCallback(iKeyguardDismissCallback);
            return;
        }
        Slog.i("ActivityTaskManager", "Activity requesting to dismiss Keyguard: " + forTokenLocked);
        if (forTokenLocked.getTurnScreenOnFlag() && forTokenLocked.isTopRunningActivity()) {
            this.mTaskSupervisor.wakeUp("dismissKeyguard");
        }
        this.mWindowManager.dismissKeyguard(iKeyguardDismissCallback, charSequence);
    }

    public final void failCallback(IKeyguardDismissCallback iKeyguardDismissCallback) {
        try {
            iKeyguardDismissCallback.onDismissError();
        } catch (RemoteException e) {
            Slog.w("ActivityTaskManager", "Failed to call callback", e);
        }
    }

    public boolean canShowActivityWhileKeyguardShowing(ActivityRecord activityRecord) {
        KeyguardDisplayState displayState = getDisplayState(activityRecord.getDisplayId());
        return activityRecord.containsDismissKeyguardWindow() && canDismissKeyguard() && !displayState.mAodShowing && (displayState.mDismissalRequested || (activityRecord.canShowWhenLocked() && displayState.mDismissingKeyguardActivity != activityRecord));
    }

    public boolean canShowWhileOccluded(boolean z, boolean z2) {
        return z2 || (z && !this.mWindowManager.isKeyguardSecure(this.mService.getCurrentUserId()));
    }

    public boolean checkKeyguardVisibility(ActivityRecord activityRecord) {
        if (activityRecord.mDisplayContent.canShowWithInsecureKeyguard() && canDismissKeyguard()) {
            return true;
        }
        if (isKeyguardOrAodShowing(activityRecord.mDisplayContent.getDisplayId())) {
            return canShowActivityWhileKeyguardShowing(activityRecord);
        }
        if (isKeyguardLocked(activityRecord.getDisplayId())) {
            return canShowWhileOccluded(activityRecord.containsDismissKeyguardWindow(), activityRecord.canShowWhenLocked());
        }
        return true;
    }

    public void updateVisibility() {
        for (int childCount = this.mRootWindowContainer.getChildCount() - 1; childCount >= 0; childCount--) {
            DisplayContent childAt = this.mRootWindowContainer.getChildAt(childCount);
            if (!childAt.isRemoving() && !childAt.isRemoved()) {
                KeyguardDisplayState displayState = getDisplayState(childAt.mDisplayId);
                displayState.updateVisibility(this, childAt);
                if (displayState.mRequestDismissKeyguard) {
                    handleDismissKeyguard(childAt.getDisplayId());
                }
            }
        }
    }

    public final void handleOccludedChanged(int i, ActivityRecord activityRecord) {
        if (i != 0) {
            updateKeyguardSleepToken(i);
            return;
        }
        boolean isKeyguardLocked = isKeyguardLocked(i);
        this.mWindowManager.mPolicy.onKeyguardOccludedChangedLw(isDisplayOccluded(0), isKeyguardLocked);
        if (isKeyguardLocked) {
            this.mService.deferWindowLayout();
            try {
                this.mRootWindowContainer.getDefaultDisplay().requestTransitionAndLegacyPrepare(isDisplayOccluded(0) ? 8 : 9, 0);
                updateKeyguardSleepToken(0);
                this.mWindowManager.executeAppTransition();
            } finally {
                this.mService.continueWindowLayout();
            }
        }
        dismissMultiWindowModeForTaskIfNeeded(i, activityRecord != null ? activityRecord.getRootTask() : null);
    }

    public final void handleKeyguardGoingAwayChanged(DisplayContent displayContent) {
        this.mService.deferWindowLayout();
        try {
            displayContent.prepareAppTransition(7, 0);
            displayContent.mAtmService.getTransitionController().requestTransitionIfNeeded(1, 256, null, displayContent);
            updateKeyguardSleepToken();
            this.mWindowManager.executeAppTransition();
        } finally {
            this.mService.continueWindowLayout();
        }
    }

    public final void handleDismissKeyguard(int i) {
        if (this.mWindowManager.isKeyguardSecure(this.mService.getCurrentUserId())) {
            this.mWindowManager.dismissKeyguard(null, null);
            KeyguardDisplayState displayState = getDisplayState(i);
            displayState.mDismissalRequested = true;
            DisplayContent defaultDisplay = this.mRootWindowContainer.getDefaultDisplay();
            if (displayState.mKeyguardShowing && canDismissKeyguard() && defaultDisplay.mAppTransition.containsTransitRequest(9)) {
                this.mWindowManager.executeAppTransition();
            }
        }
    }

    public boolean isDisplayOccluded(int i) {
        return getDisplayState(i).mOccluded;
    }

    public boolean canDismissKeyguard() {
        return this.mWindowManager.mPolicy.isKeyguardTrustedLw() || !this.mWindowManager.isKeyguardSecure(this.mService.getCurrentUserId());
    }

    public boolean isShowingDream() {
        return getDisplayState(0).mShowingDream;
    }

    public final void dismissMultiWindowModeForTaskIfNeeded(int i, Task task) {
        if (getDisplayState(i).mKeyguardShowing && isDisplayOccluded(0) && task != null && task.inFreeformWindowingMode()) {
            task.setWindowingMode(1);
        }
    }

    public final void updateKeyguardSleepToken() {
        for (int childCount = this.mRootWindowContainer.getChildCount() - 1; childCount >= 0; childCount--) {
            updateKeyguardSleepToken(this.mRootWindowContainer.getChildAt(childCount).mDisplayId);
        }
    }

    public final void updateKeyguardSleepToken(int i) {
        KeyguardDisplayState displayState = getDisplayState(i);
        if (isKeyguardUnoccludedOrAodShowing(i)) {
            displayState.mSleepTokenAcquirer.acquire(i);
        } else {
            displayState.mSleepTokenAcquirer.release(i);
        }
    }

    public final KeyguardDisplayState getDisplayState(int i) {
        KeyguardDisplayState keyguardDisplayState = this.mDisplayStates.get(i);
        if (keyguardDisplayState == null) {
            KeyguardDisplayState keyguardDisplayState2 = new KeyguardDisplayState(this.mService, i, this.mSleepTokenAcquirer);
            this.mDisplayStates.append(i, keyguardDisplayState2);
            return keyguardDisplayState2;
        }
        return keyguardDisplayState;
    }

    public void onDisplayRemoved(int i) {
        KeyguardDisplayState keyguardDisplayState = this.mDisplayStates.get(i);
        if (keyguardDisplayState != null) {
            keyguardDisplayState.onRemoved();
            this.mDisplayStates.remove(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mWindowManager.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                updateDeferTransitionForAod(false);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void updateDeferTransitionForAod(boolean z) {
        if (z != this.mWaitingForWakeTransition && this.mService.getTransitionController().isCollecting()) {
            if (z && isAodShowing(0)) {
                this.mWaitingForWakeTransition = true;
                this.mWindowManager.mAtmService.getTransitionController().deferTransitionReady();
                this.mWindowManager.f1164mH.postDelayed(this.mResetWaitTransition, 5000L);
            } else if (z) {
            } else {
                this.mWaitingForWakeTransition = false;
                this.mWindowManager.mAtmService.getTransitionController().continueTransitionReady();
                this.mWindowManager.f1164mH.removeCallbacks(this.mResetWaitTransition);
            }
        }
    }

    /* renamed from: com.android.server.wm.KeyguardController$KeyguardDisplayState */
    /* loaded from: classes2.dex */
    public static class KeyguardDisplayState {
        public boolean mAodShowing;
        public boolean mDismissalRequested;
        public ActivityRecord mDismissingKeyguardActivity;
        public final int mDisplayId;
        public boolean mKeyguardGoingAway;
        public boolean mKeyguardShowing;
        public boolean mOccluded;
        public boolean mRequestDismissKeyguard;
        public final ActivityTaskManagerService mService;
        public boolean mShowingDream;
        public final ActivityTaskManagerInternal.SleepTokenAcquirer mSleepTokenAcquirer;
        public ActivityRecord mTopOccludesActivity;
        public ActivityRecord mTopTurnScreenOnActivity;

        public KeyguardDisplayState(ActivityTaskManagerService activityTaskManagerService, int i, ActivityTaskManagerInternal.SleepTokenAcquirer sleepTokenAcquirer) {
            this.mService = activityTaskManagerService;
            this.mDisplayId = i;
            this.mSleepTokenAcquirer = sleepTokenAcquirer;
        }

        public void onRemoved() {
            this.mTopOccludesActivity = null;
            this.mDismissingKeyguardActivity = null;
            this.mTopTurnScreenOnActivity = null;
            this.mSleepTokenAcquirer.release(this.mDisplayId);
        }

        public void updateVisibility(KeyguardController keyguardController, DisplayContent displayContent) {
            boolean z;
            boolean z2 = this.mOccluded;
            boolean z3 = this.mKeyguardGoingAway;
            ActivityRecord activityRecord = this.mDismissingKeyguardActivity;
            ActivityRecord activityRecord2 = this.mTopTurnScreenOnActivity;
            boolean z4 = false;
            this.mRequestDismissKeyguard = false;
            this.mOccluded = false;
            this.mShowingDream = false;
            this.mTopOccludesActivity = null;
            this.mDismissingKeyguardActivity = null;
            this.mTopTurnScreenOnActivity = null;
            Task rootTaskForControllingOccluding = getRootTaskForControllingOccluding(displayContent);
            ActivityRecord topNonFinishingActivity = rootTaskForControllingOccluding != null ? rootTaskForControllingOccluding.getTopNonFinishingActivity() : null;
            if (topNonFinishingActivity != null) {
                if (topNonFinishingActivity.containsDismissKeyguardWindow()) {
                    this.mDismissingKeyguardActivity = topNonFinishingActivity;
                }
                if (topNonFinishingActivity.getTurnScreenOnFlag() && topNonFinishingActivity.currentLaunchCanTurnScreenOn()) {
                    this.mTopTurnScreenOnActivity = topNonFinishingActivity;
                }
                if (topNonFinishingActivity.mDismissKeyguard && this.mKeyguardShowing) {
                    this.mKeyguardGoingAway = true;
                } else if (topNonFinishingActivity.canShowWhenLocked()) {
                    this.mTopOccludesActivity = topNonFinishingActivity;
                }
                topNonFinishingActivity.mDismissKeyguard = false;
                boolean z5 = this.mTopOccludesActivity != null || (this.mDismissingKeyguardActivity != null && rootTaskForControllingOccluding.topRunningActivity() == this.mDismissingKeyguardActivity && keyguardController.canShowWhileOccluded(true, false));
                z = z5;
                if (this.mDisplayId != 0) {
                    z = z5 | (displayContent.canShowWithInsecureKeyguard() && keyguardController.canDismissKeyguard());
                }
            } else {
                z = false;
            }
            boolean z6 = displayContent.getDisplayPolicy().isShowingDreamLw() && topNonFinishingActivity != null && topNonFinishingActivity.getActivityType() == 5;
            this.mShowingDream = z6;
            boolean z7 = z6 || z;
            this.mOccluded = z7;
            ActivityRecord activityRecord3 = this.mDismissingKeyguardActivity;
            this.mRequestDismissKeyguard = (activityRecord == activityRecord3 || z7 || this.mKeyguardGoingAway || activityRecord3 == null) ? false : true;
            if (z7 && this.mKeyguardShowing && !displayContent.isSleeping() && !topNonFinishingActivity.fillsParent() && displayContent.mWallpaperController.getWallpaperTarget() == null) {
                displayContent.pendingLayoutChanges |= 4;
            }
            ActivityRecord activityRecord4 = this.mTopTurnScreenOnActivity;
            if (activityRecord4 != activityRecord2 && activityRecord4 != null && !this.mService.mWindowManager.mPowerManager.isInteractive() && (this.mRequestDismissKeyguard || z)) {
                keyguardController.mTaskSupervisor.wakeUp("handleTurnScreenOn");
                this.mTopTurnScreenOnActivity.setCurrentLaunchCanTurnScreenOn(false);
            }
            boolean z8 = this.mOccluded;
            if (z2 != z8) {
                int i = this.mDisplayId;
                if (i == 0) {
                    EventLogTags.writeWmSetKeyguardShown(i, this.mKeyguardShowing ? 1 : 0, this.mAodShowing ? 1 : 0, this.mKeyguardGoingAway ? 1 : 0, z8 ? 1 : 0, "updateVisibility");
                }
                keyguardController.handleOccludedChanged(this.mDisplayId, this.mTopOccludesActivity);
            } else {
                if (!z3 && this.mKeyguardGoingAway) {
                    keyguardController.handleKeyguardGoingAwayChanged(displayContent);
                }
                if (z4 || topNonFinishingActivity == null) {
                }
                if (this.mOccluded || this.mKeyguardGoingAway) {
                    displayContent.mTransitionController.collect(topNonFinishingActivity);
                    return;
                }
                return;
            }
            z4 = true;
            if (z4) {
            }
        }

        public final Task getRootTaskForControllingOccluding(DisplayContent displayContent) {
            return displayContent.getRootTask(new Predicate() { // from class: com.android.server.wm.KeyguardController$KeyguardDisplayState$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getRootTaskForControllingOccluding$0;
                    lambda$getRootTaskForControllingOccluding$0 = KeyguardController.KeyguardDisplayState.lambda$getRootTaskForControllingOccluding$0((Task) obj);
                    return lambda$getRootTaskForControllingOccluding$0;
                }
            });
        }

        public static /* synthetic */ boolean lambda$getRootTaskForControllingOccluding$0(Task task) {
            return (task == null || !task.isFocusableAndVisible() || task.inPinnedWindowingMode()) ? false : true;
        }

        public void dumpStatus(PrintWriter printWriter, String str) {
            printWriter.println(str + " KeyguardShowing=" + this.mKeyguardShowing + " AodShowing=" + this.mAodShowing + " KeyguardGoingAway=" + this.mKeyguardGoingAway + " DismissalRequested=" + this.mDismissalRequested + "  Occluded=" + this.mOccluded + " DismissingKeyguardActivity=" + this.mDismissingKeyguardActivity + " TurnScreenOnActivity=" + this.mTopTurnScreenOnActivity + " at display=" + this.mDisplayId);
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.mDisplayId);
            protoOutputStream.write(1133871366146L, this.mKeyguardShowing);
            protoOutputStream.write(1133871366147L, this.mAodShowing);
            protoOutputStream.write(1133871366148L, this.mOccluded);
            protoOutputStream.write(1133871366149L, this.mKeyguardGoingAway);
            protoOutputStream.end(start);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        KeyguardDisplayState displayState = getDisplayState(0);
        printWriter.println(str + "KeyguardController:");
        printWriter.println(str + "  mKeyguardShowing=" + displayState.mKeyguardShowing);
        printWriter.println(str + "  mAodShowing=" + displayState.mAodShowing);
        printWriter.println(str + "  mKeyguardGoingAway=" + displayState.mKeyguardGoingAway);
        dumpDisplayStates(printWriter, str);
        printWriter.println(str + "  mDismissalRequested=" + displayState.mDismissalRequested);
        printWriter.println();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        KeyguardDisplayState displayState = getDisplayState(0);
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1133871366147L, displayState.mAodShowing);
        protoOutputStream.write(1133871366145L, displayState.mKeyguardShowing);
        protoOutputStream.write(1133871366149L, displayState.mKeyguardGoingAway);
        writeDisplayStatesToProto(protoOutputStream, 2246267895812L);
        protoOutputStream.end(start);
    }

    public final void dumpDisplayStates(PrintWriter printWriter, String str) {
        for (int i = 0; i < this.mDisplayStates.size(); i++) {
            this.mDisplayStates.valueAt(i).dumpStatus(printWriter, str);
        }
    }

    public final void writeDisplayStatesToProto(ProtoOutputStream protoOutputStream, long j) {
        for (int i = 0; i < this.mDisplayStates.size(); i++) {
            this.mDisplayStates.valueAt(i).dumpDebug(protoOutputStream, j);
        }
    }
}
