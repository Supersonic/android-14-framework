package com.android.server.p014wm;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.window.DisplayAreaInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import com.android.server.p014wm.DeviceStateController;
import com.android.server.p014wm.RemoteDisplayChangeController;
/* renamed from: com.android.server.wm.PhysicalDisplaySwitchTransitionLauncher */
/* loaded from: classes2.dex */
public class PhysicalDisplaySwitchTransitionLauncher {
    public final DisplayContent mDisplayContent;
    public boolean mIsFolded;
    public final WindowManagerService mService;
    public Transition mTransition;
    public final TransitionController mTransitionController;

    public PhysicalDisplaySwitchTransitionLauncher(DisplayContent displayContent, TransitionController transitionController) {
        this.mDisplayContent = displayContent;
        this.mService = displayContent.mWmService;
        this.mTransitionController = transitionController;
    }

    public void foldStateChanged(DeviceStateController.DeviceState deviceState) {
        if (deviceState == DeviceStateController.DeviceState.HALF_FOLDED) {
            return;
        }
        this.mIsFolded = deviceState == DeviceStateController.DeviceState.FOLDED;
    }

    public void requestDisplaySwitchTransitionIfNeeded(int i, int i2, int i3, int i4, int i5) {
        if (this.mTransitionController.isShellTransitionsEnabled() && this.mDisplayContent.getLastHasContent()) {
            if (!this.mIsFolded && this.mService.mContext.getResources().getBoolean(17891850) && ValueAnimator.areAnimatorsEnabled()) {
                TransitionRequestInfo.DisplayChange displayChange = new TransitionRequestInfo.DisplayChange(i);
                displayChange.setStartAbsBounds(new Rect(0, 0, i2, i3));
                displayChange.setEndAbsBounds(new Rect(0, 0, i4, i5));
                displayChange.setPhysicalDisplayChanged(true);
                TransitionController transitionController = this.mTransitionController;
                DisplayContent displayContent = this.mDisplayContent;
                Transition requestTransitionIfNeeded = transitionController.requestTransitionIfNeeded(6, 0, displayContent, displayContent, null, displayChange);
                if (requestTransitionIfNeeded != null) {
                    this.mDisplayContent.mAtmService.startLaunchPowerMode(2);
                    this.mTransition = requestTransitionIfNeeded;
                }
            }
        }
    }

    public void onDisplayUpdated(int i, int i2, DisplayAreaInfo displayAreaInfo) {
        if (this.mTransition == null || this.mDisplayContent.mRemoteDisplayChangeController.performRemoteDisplayChange(i, i2, displayAreaInfo, new RemoteDisplayChangeController.ContinueRemoteDisplayChangeCallback() { // from class: com.android.server.wm.PhysicalDisplaySwitchTransitionLauncher$$ExternalSyntheticLambda0
            @Override // com.android.server.p014wm.RemoteDisplayChangeController.ContinueRemoteDisplayChangeCallback
            public final void onContinueRemoteDisplayChange(WindowContainerTransaction windowContainerTransaction) {
                PhysicalDisplaySwitchTransitionLauncher.this.continueDisplayUpdate(windowContainerTransaction);
            }
        })) {
            return;
        }
        markTransitionAsReady();
    }

    public final void continueDisplayUpdate(WindowContainerTransaction windowContainerTransaction) {
        if (this.mTransition == null) {
            return;
        }
        if (windowContainerTransaction != null) {
            this.mService.mAtmService.mWindowOrganizerController.applyTransaction(windowContainerTransaction);
        }
        markTransitionAsReady();
    }

    public final void markTransitionAsReady() {
        Transition transition = this.mTransition;
        if (transition == null) {
            return;
        }
        transition.setAllReady();
        this.mTransition = null;
    }
}
