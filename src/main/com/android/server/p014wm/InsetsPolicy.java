package com.android.server.p014wm;

import android.app.WindowConfiguration;
import android.content.res.CompatibilityInfo;
import android.graphics.Rect;
import android.p005os.IInstalld;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.InsetsAnimationControlCallbacks;
import android.view.InsetsAnimationControlImpl;
import android.view.InsetsAnimationControlRunner;
import android.view.InsetsController;
import android.view.InsetsFrameProvider;
import android.view.InsetsSource;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.InternalInsetsAnimationController;
import android.view.SurfaceControl;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.WindowInsetsAnimationControlListener;
import android.view.WindowManager;
import android.view.inputmethod.ImeTracker;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.DisplayThread;
import com.android.server.p014wm.InsetsPolicy;
import com.android.server.statusbar.StatusBarManagerInternal;
/* renamed from: com.android.server.wm.InsetsPolicy */
/* loaded from: classes2.dex */
public class InsetsPolicy {
    public static final int CONTROLLABLE_TYPES = (WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars()) | WindowInsets.Type.ime();
    public boolean mAnimatingShown;
    public final DisplayContent mDisplayContent;
    public WindowState mFocusedWin;
    public final boolean mHideNavBarForKeyboard;
    public final DisplayPolicy mPolicy;
    public int mShowingTransientTypes;
    public final InsetsStateController mStateController;
    public final InsetsControlTarget mDummyControlTarget = new InsetsControlTarget() { // from class: com.android.server.wm.InsetsPolicy.1
        @Override // com.android.server.p014wm.InsetsControlTarget
        public void notifyInsetsControlChanged() {
            SurfaceControl leash;
            InsetsSourceControl[] controlsForDispatch = InsetsPolicy.this.mStateController.getControlsForDispatch(this);
            if (controlsForDispatch == null) {
                return;
            }
            boolean z = false;
            for (InsetsSourceControl insetsSourceControl : controlsForDispatch) {
                if (!InsetsPolicy.this.isTransient(insetsSourceControl.getType()) && (leash = insetsSourceControl.getLeash()) != null) {
                    InsetsPolicy.this.mDisplayContent.getPendingTransaction().setAlpha(leash, (insetsSourceControl.getType() & WindowInsets.Type.defaultVisible()) != 0 ? 1.0f : 0.0f);
                    z = true;
                }
            }
            if (z) {
                InsetsPolicy.this.mDisplayContent.scheduleAnimation();
            }
        }
    };
    public final BarWindow mStatusBar = new BarWindow(1);
    public final BarWindow mNavBar = new BarWindow(2);
    public final float[] mTmpFloat9 = new float[9];

    public InsetsPolicy(InsetsStateController insetsStateController, DisplayContent displayContent) {
        this.mStateController = insetsStateController;
        this.mDisplayContent = displayContent;
        DisplayPolicy displayPolicy = displayContent.getDisplayPolicy();
        this.mPolicy = displayPolicy;
        this.mHideNavBarForKeyboard = displayPolicy.getContext().getResources().getBoolean(17891702);
    }

    public void updateBarControlTarget(WindowState windowState) {
        InsetsControlTarget statusControlTarget;
        if (this.mFocusedWin != windowState) {
            abortTransient();
        }
        this.mFocusedWin = windowState;
        InsetsControlTarget statusControlTarget2 = getStatusControlTarget(windowState, false);
        InsetsControlTarget navControlTarget = getNavControlTarget(windowState, false);
        WindowState notificationShade = this.mPolicy.getNotificationShade();
        WindowState topFullscreenOpaqueWindow = this.mPolicy.getTopFullscreenOpaqueWindow();
        InsetsStateController insetsStateController = this.mStateController;
        InsetsControlTarget insetsControlTarget = null;
        if (statusControlTarget2 == this.mDummyControlTarget) {
            statusControlTarget = getStatusControlTarget(windowState, true);
        } else {
            statusControlTarget = statusControlTarget2 == notificationShade ? getStatusControlTarget(topFullscreenOpaqueWindow, true) : null;
        }
        if (navControlTarget == this.mDummyControlTarget) {
            insetsControlTarget = getNavControlTarget(windowState, true);
        } else if (navControlTarget == notificationShade) {
            insetsControlTarget = getNavControlTarget(topFullscreenOpaqueWindow, true);
        }
        insetsStateController.onBarControlTargetChanged(statusControlTarget2, statusControlTarget, navControlTarget, insetsControlTarget);
        this.mStatusBar.updateVisibility(statusControlTarget2, WindowInsets.Type.statusBars());
        this.mNavBar.updateVisibility(navControlTarget, WindowInsets.Type.navigationBars());
    }

    public boolean hasHiddenSources(int i) {
        InsetsState rawInsetsState = this.mStateController.getRawInsetsState();
        for (int sourceSize = rawInsetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = rawInsetsState.sourceAt(sourceSize);
            if ((sourceAt.getType() & i) != 0 && !sourceAt.getFrame().isEmpty() && !sourceAt.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void showTransient(int i, boolean z) {
        int i2 = this.mShowingTransientTypes;
        InsetsState rawInsetsState = this.mStateController.getRawInsetsState();
        for (int sourceSize = rawInsetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = rawInsetsState.sourceAt(sourceSize);
            if (!sourceAt.isVisible()) {
                int type = sourceAt.getType();
                if ((sourceAt.getType() & i) != 0) {
                    i2 |= type;
                }
            }
        }
        if (this.mShowingTransientTypes != i2) {
            this.mShowingTransientTypes = i2;
            StatusBarManagerInternal statusBarManagerInternal = this.mPolicy.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.showTransient(this.mDisplayContent.getDisplayId(), i2, z);
            }
            updateBarControlTarget(this.mFocusedWin);
            dispatchTransientSystemBarsVisibilityChanged(this.mFocusedWin, (i2 & (WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars())) != 0, z);
            this.mDisplayContent.mWmService.mAnimator.getChoreographer().postFrameCallback(new Choreographer.FrameCallback() { // from class: com.android.server.wm.InsetsPolicy$$ExternalSyntheticLambda0
                @Override // android.view.Choreographer.FrameCallback
                public final void doFrame(long j) {
                    InsetsPolicy.this.lambda$showTransient$0(j);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showTransient$0(long j) {
        synchronized (this.mDisplayContent.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                startAnimation(true, null);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void hideTransient() {
        if (this.mShowingTransientTypes == 0) {
            return;
        }
        dispatchTransientSystemBarsVisibilityChanged(this.mFocusedWin, false, false);
        startAnimation(false, new Runnable() { // from class: com.android.server.wm.InsetsPolicy$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                InsetsPolicy.this.lambda$hideTransient$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hideTransient$1() {
        synchronized (this.mDisplayContent.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                SparseArray<WindowContainerInsetsSourceProvider> sourceProviders = this.mStateController.getSourceProviders();
                for (int size = sourceProviders.size() - 1; size >= 0; size--) {
                    WindowContainerInsetsSourceProvider valueAt = sourceProviders.valueAt(size);
                    if (isTransient(valueAt.getSource().getType())) {
                        valueAt.setClientVisible(false);
                    }
                }
                this.mShowingTransientTypes = 0;
                updateBarControlTarget(this.mFocusedWin);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isTransient(int i) {
        return (this.mShowingTransientTypes & i) != 0;
    }

    public InsetsState adjustInsetsForWindow(WindowState windowState, InsetsState insetsState, boolean z) {
        InsetsState adjustVisibilityForTransientTypes = !z ? adjustVisibilityForTransientTypes(insetsState) : insetsState;
        InsetsState adjustVisibilityForIme = adjustVisibilityForIme(windowState, adjustVisibilityForTransientTypes, adjustVisibilityForTransientTypes == insetsState);
        return adjustInsetsForRoundedCorners(windowState.mToken, adjustVisibilityForIme, adjustVisibilityForIme == insetsState);
    }

    public InsetsState adjustInsetsForWindow(WindowState windowState, InsetsState insetsState) {
        return adjustInsetsForWindow(windowState, insetsState, false);
    }

    public void getInsetsForWindowMetrics(WindowToken windowToken, InsetsState insetsState) {
        InsetsState rawInsetsState;
        if (windowToken != null && windowToken.isFixedRotationTransforming()) {
            rawInsetsState = windowToken.getFixedRotationTransformInsetsState();
        } else {
            rawInsetsState = this.mStateController.getRawInsetsState();
        }
        insetsState.set(rawInsetsState, true);
        for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = insetsState.sourceAt(sourceSize);
            if (isTransient(sourceAt.getType())) {
                sourceAt.setVisible(false);
            }
        }
        adjustInsetsForRoundedCorners(windowToken, insetsState, false);
        if (windowToken == null || !windowToken.hasSizeCompatBounds()) {
            return;
        }
        insetsState.scale(1.0f / windowToken.getCompatScale());
    }

    public InsetsState enforceInsetsPolicyForTarget(WindowManager.LayoutParams layoutParams, @WindowConfiguration.WindowingMode int i, boolean z, InsetsState insetsState) {
        InsetsState insetsState2;
        if (layoutParams.type == 2011) {
            insetsState2 = new InsetsState(insetsState);
            insetsState2.removeSource(InsetsSource.ID_IME);
        } else {
            InsetsFrameProvider[] insetsFrameProviderArr = layoutParams.providedInsets;
            if (insetsFrameProviderArr != null) {
                InsetsState insetsState3 = insetsState;
                for (InsetsFrameProvider insetsFrameProvider : insetsFrameProviderArr) {
                    int createId = InsetsSource.createId(insetsFrameProvider.getOwner(), insetsFrameProvider.getIndex(), insetsFrameProvider.getType());
                    if ((insetsFrameProvider.getType() & WindowInsets.Type.systemBars()) != 0) {
                        if (insetsState3 == insetsState) {
                            insetsState3 = new InsetsState(insetsState3);
                        }
                        insetsState3.removeSource(createId);
                    }
                }
                insetsState2 = insetsState3;
            } else {
                insetsState2 = insetsState;
            }
        }
        if (!layoutParams.isFullscreen() || layoutParams.getFitInsetsTypes() != 0) {
            if (insetsState2 == insetsState) {
                insetsState2 = new InsetsState(insetsState);
            }
            insetsState2.removeSource(2);
        }
        SparseArray<WindowContainerInsetsSourceProvider> sourceProviders = this.mStateController.getSourceProviders();
        int i2 = layoutParams.type;
        for (int size = sourceProviders.size() - 1; size >= 0; size--) {
            WindowContainerInsetsSourceProvider valueAt = sourceProviders.valueAt(size);
            if (valueAt.overridesFrame(i2)) {
                if (insetsState2 == insetsState) {
                    insetsState2 = new InsetsState(insetsState2);
                }
                InsetsSource insetsSource = new InsetsSource(valueAt.getSource());
                insetsSource.setFrame(valueAt.getOverriddenFrame(i2));
                insetsState2.addSource(insetsSource);
            }
        }
        if (WindowConfiguration.isFloating(i) || (i == 6 && z)) {
            int captionBar = WindowInsets.Type.captionBar();
            if (i != 2) {
                captionBar |= WindowInsets.Type.ime();
            }
            InsetsState insetsState4 = new InsetsState();
            insetsState4.set(insetsState2, captionBar);
            return insetsState4;
        }
        return insetsState2;
    }

    public final InsetsState adjustVisibilityForTransientTypes(InsetsState insetsState) {
        InsetsState insetsState2 = insetsState;
        for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
            InsetsSource sourceAt = insetsState2.sourceAt(sourceSize);
            if (isTransient(sourceAt.getType()) && sourceAt.isVisible()) {
                if (insetsState2 == insetsState) {
                    insetsState2 = new InsetsState(insetsState);
                }
                InsetsSource insetsSource = new InsetsSource(sourceAt);
                insetsSource.setVisible(false);
                insetsState2.addSource(insetsSource);
            }
        }
        return insetsState2;
    }

    public final InsetsState adjustVisibilityForIme(WindowState windowState, InsetsState insetsState, boolean z) {
        InsetsSource peekSource;
        if (windowState.mIsImWindow) {
            boolean z2 = !this.mHideNavBarForKeyboard;
            InsetsState insetsState2 = insetsState;
            for (int sourceSize = insetsState.sourceSize() - 1; sourceSize >= 0; sourceSize--) {
                InsetsSource sourceAt = insetsState.sourceAt(sourceSize);
                if (sourceAt.getType() == WindowInsets.Type.navigationBars() && sourceAt.isVisible() != z2) {
                    if (insetsState2 == insetsState && z) {
                        insetsState2 = new InsetsState(insetsState);
                    }
                    InsetsSource insetsSource = new InsetsSource(sourceAt);
                    insetsSource.setVisible(z2);
                    insetsState2.addSource(insetsSource);
                }
            }
            return insetsState2;
        }
        ActivityRecord activityRecord = windowState.mActivityRecord;
        if (activityRecord != null && activityRecord.mImeInsetsFrozenUntilStartInput && (peekSource = insetsState.peekSource(InsetsSource.ID_IME)) != null) {
            boolean isRequestedVisible = windowState.isRequestedVisible(WindowInsets.Type.ime());
            if (z) {
                insetsState = new InsetsState(insetsState);
            }
            InsetsSource insetsSource2 = new InsetsSource(peekSource);
            insetsSource2.setVisible(isRequestedVisible);
            insetsState.addSource(insetsSource2);
        }
        return insetsState;
    }

    public final InsetsState adjustInsetsForRoundedCorners(WindowToken windowToken, InsetsState insetsState, boolean z) {
        if (windowToken != null) {
            ActivityRecord asActivityRecord = windowToken.asActivityRecord();
            Task task = asActivityRecord != null ? asActivityRecord.getTask() : null;
            if (task != null && !task.getWindowConfiguration().tasksAreFloating()) {
                if (z) {
                    insetsState = new InsetsState(insetsState);
                }
                insetsState.setRoundedCornerFrame(task.getBounds());
            }
        }
        return insetsState;
    }

    public void onInsetsModified(InsetsControlTarget insetsControlTarget) {
        this.mStateController.onInsetsModified(insetsControlTarget);
        checkAbortTransient(insetsControlTarget);
        updateBarControlTarget(this.mFocusedWin);
    }

    public final void checkAbortTransient(InsetsControlTarget insetsControlTarget) {
        if (this.mShowingTransientTypes == 0) {
            return;
        }
        int requestedVisibleTypes = (insetsControlTarget.getRequestedVisibleTypes() & this.mStateController.getFakeControllingTypes(insetsControlTarget)) | (this.mStateController.getImeSourceProvider().isClientVisible() ? WindowInsets.Type.navigationBars() : 0);
        this.mShowingTransientTypes &= ~requestedVisibleTypes;
        if (requestedVisibleTypes != 0) {
            this.mDisplayContent.setLayoutNeeded();
            this.mDisplayContent.mWmService.requestTraversal();
            StatusBarManagerInternal statusBarManagerInternal = this.mPolicy.getStatusBarManagerInternal();
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.abortTransient(this.mDisplayContent.getDisplayId(), requestedVisibleTypes);
            }
        }
    }

    public final void abortTransient() {
        if (this.mShowingTransientTypes == 0) {
            return;
        }
        StatusBarManagerInternal statusBarManagerInternal = this.mPolicy.getStatusBarManagerInternal();
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.abortTransient(this.mDisplayContent.getDisplayId(), this.mShowingTransientTypes);
        }
        this.mShowingTransientTypes = 0;
        this.mDisplayContent.setLayoutNeeded();
        this.mDisplayContent.mWmService.requestTraversal();
        dispatchTransientSystemBarsVisibilityChanged(this.mFocusedWin, false, false);
    }

    public final InsetsControlTarget getStatusControlTarget(WindowState windowState, boolean z) {
        if (!z && isTransient(WindowInsets.Type.statusBars())) {
            return this.mDummyControlTarget;
        }
        WindowState notificationShade = this.mPolicy.getNotificationShade();
        if (windowState == notificationShade) {
            return windowState;
        }
        if (remoteInsetsControllerControlsSystemBars(windowState)) {
            ActivityRecord activityRecord = windowState.mActivityRecord;
            this.mDisplayContent.mRemoteInsetsControlTarget.topFocusedWindowChanged(activityRecord != null ? activityRecord.mActivityComponent : null, windowState.getRequestedVisibleTypes());
            return this.mDisplayContent.mRemoteInsetsControlTarget;
        } else if (this.mPolicy.areSystemBarsForcedShownLw()) {
            return null;
        } else {
            if (!forceShowsStatusBarTransiently() || z) {
                return (canBeTopFullscreenOpaqueWindow(windowState) || !this.mPolicy.topAppHidesSystemBar(WindowInsets.Type.statusBars()) || (notificationShade != null && notificationShade.canReceiveKeys())) ? windowState : this.mPolicy.getTopFullscreenOpaqueWindow();
            }
            return this.mDummyControlTarget;
        }
    }

    public static boolean canBeTopFullscreenOpaqueWindow(WindowState windowState) {
        int i;
        return (windowState != null && (i = windowState.mAttrs.type) >= 1 && i <= 99) && windowState.mAttrs.isFullscreen() && !windowState.isFullyTransparent() && !windowState.inMultiWindowMode();
    }

    public final InsetsControlTarget getNavControlTarget(WindowState windowState, boolean z) {
        InsetsSourceProvider controllableInsetProvider;
        WindowState windowState2 = this.mDisplayContent.mInputMethodWindow;
        if (windowState2 == null || !windowState2.isVisible() || this.mHideNavBarForKeyboard) {
            if (!z && isTransient(WindowInsets.Type.navigationBars())) {
                return this.mDummyControlTarget;
            }
            if (windowState == this.mPolicy.getNotificationShade()) {
                return windowState;
            }
            if (windowState == null || (controllableInsetProvider = windowState.getControllableInsetProvider()) == null || controllableInsetProvider.getSource().getType() != WindowInsets.Type.navigationBars()) {
                if (this.mPolicy.isForceShowNavigationBarEnabled() && windowState != null && windowState.getActivityType() == 1) {
                    return null;
                }
                if (remoteInsetsControllerControlsSystemBars(windowState)) {
                    ActivityRecord activityRecord = windowState.mActivityRecord;
                    this.mDisplayContent.mRemoteInsetsControlTarget.topFocusedWindowChanged(activityRecord != null ? activityRecord.mActivityComponent : null, windowState.getRequestedVisibleTypes());
                    return this.mDisplayContent.mRemoteInsetsControlTarget;
                } else if (this.mPolicy.areSystemBarsForcedShownLw()) {
                    return null;
                } else {
                    if (forceShowsNavigationBarTransiently() && !z) {
                        return this.mDummyControlTarget;
                    }
                    WindowState notificationShade = this.mPolicy.getNotificationShade();
                    return (canBeTopFullscreenOpaqueWindow(windowState) || !this.mPolicy.topAppHidesSystemBar(WindowInsets.Type.navigationBars()) || (notificationShade != null && notificationShade.canReceiveKeys())) ? windowState : this.mPolicy.getTopFullscreenOpaqueWindow();
                }
            }
            return windowState;
        }
        return null;
    }

    public boolean remoteInsetsControllerControlsSystemBars(WindowState windowState) {
        DisplayContent displayContent;
        return windowState != null && this.mPolicy.isRemoteInsetsControllerControllingSystemBars() && (displayContent = this.mDisplayContent) != null && displayContent.mRemoteInsetsControlTarget != null && windowState.getAttrs().type >= 1 && windowState.getAttrs().type <= 99;
    }

    public final boolean forceShowsStatusBarTransiently() {
        WindowState statusBar = this.mPolicy.getStatusBar();
        return (statusBar == null || (statusBar.mAttrs.privateFlags & IInstalld.FLAG_USE_QUOTA) == 0) ? false : true;
    }

    public final boolean forceShowsNavigationBarTransiently() {
        WindowState notificationShade = this.mPolicy.getNotificationShade();
        return (notificationShade == null || (notificationShade.mAttrs.privateFlags & 8388608) == 0) ? false : true;
    }

    @VisibleForTesting
    public void startAnimation(boolean z, Runnable runnable) {
        SparseArray<InsetsSourceControl> sparseArray = new SparseArray<>();
        InsetsSourceControl[] controlsForDispatch = this.mStateController.getControlsForDispatch(this.mDummyControlTarget);
        if (controlsForDispatch == null) {
            if (runnable != null) {
                DisplayThread.getHandler().post(runnable);
                return;
            }
            return;
        }
        int i = 0;
        for (InsetsSourceControl insetsSourceControl : controlsForDispatch) {
            if (isTransient(insetsSourceControl.getType()) && insetsSourceControl.getLeash() != null) {
                i |= insetsSourceControl.getType();
                sparseArray.put(insetsSourceControl.getId(), new InsetsSourceControl(insetsSourceControl));
            }
        }
        controlAnimationUnchecked(i, sparseArray, z, runnable);
    }

    public final void controlAnimationUnchecked(int i, SparseArray<InsetsSourceControl> sparseArray, boolean z, Runnable runnable) {
        new InsetsPolicyAnimationControlListener(z, runnable, i).mControlCallbacks.controlAnimationUnchecked(i, sparseArray, z);
    }

    public final void dispatchTransientSystemBarsVisibilityChanged(WindowState windowState, boolean z, boolean z2) {
        Task task;
        if (windowState == null || (task = windowState.getTask()) == null) {
            return;
        }
        int i = task.mTaskId;
        if (i != -1) {
            this.mDisplayContent.mWmService.mTaskSystemBarsListenerController.dispatchTransientSystemBarVisibilityChanged(i, z, z2);
        }
    }

    /* renamed from: com.android.server.wm.InsetsPolicy$BarWindow */
    /* loaded from: classes2.dex */
    public class BarWindow {
        public final int mId;
        public int mState = 0;

        public BarWindow(int i) {
            this.mId = i;
        }

        public final void updateVisibility(InsetsControlTarget insetsControlTarget, int i) {
            setVisible(insetsControlTarget == null || insetsControlTarget.isRequestedVisible(i));
        }

        public final void setVisible(boolean z) {
            int i = z ? 0 : 2;
            if (this.mState != i) {
                this.mState = i;
                StatusBarManagerInternal statusBarManagerInternal = InsetsPolicy.this.mPolicy.getStatusBarManagerInternal();
                if (statusBarManagerInternal != null) {
                    statusBarManagerInternal.setWindowState(InsetsPolicy.this.mDisplayContent.getDisplayId(), this.mId, i);
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.InsetsPolicy$InsetsPolicyAnimationControlListener */
    /* loaded from: classes2.dex */
    public class InsetsPolicyAnimationControlListener extends InsetsController.InternalAnimationControlListener {
        public InsetsPolicyAnimationControlCallbacks mControlCallbacks;
        public Runnable mFinishCallback;

        public InsetsPolicyAnimationControlListener(boolean z, Runnable runnable, int i) {
            super(z, false, i, 2, false, 0, (WindowInsetsAnimationControlListener) null, (ImeTracker.InputMethodJankContext) null);
            this.mFinishCallback = runnable;
            this.mControlCallbacks = new InsetsPolicyAnimationControlCallbacks(this);
        }

        public void onAnimationFinish() {
            super.onAnimationFinish();
            if (this.mFinishCallback != null) {
                DisplayThread.getHandler().post(this.mFinishCallback);
            }
        }

        /* renamed from: com.android.server.wm.InsetsPolicy$InsetsPolicyAnimationControlListener$InsetsPolicyAnimationControlCallbacks */
        /* loaded from: classes2.dex */
        public class InsetsPolicyAnimationControlCallbacks implements InsetsAnimationControlCallbacks {
            public InsetsAnimationControlImpl mAnimationControl = null;
            public InsetsPolicyAnimationControlListener mListener;

            public void notifyFinished(InsetsAnimationControlRunner insetsAnimationControlRunner, boolean z) {
            }

            public void reportPerceptible(int i, boolean z) {
            }

            public <T extends InsetsAnimationControlRunner & InternalInsetsAnimationController> void startAnimation(T t, WindowInsetsAnimationControlListener windowInsetsAnimationControlListener, int i, WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
            }

            public InsetsPolicyAnimationControlCallbacks(InsetsPolicyAnimationControlListener insetsPolicyAnimationControlListener) {
                this.mListener = insetsPolicyAnimationControlListener;
            }

            public final void controlAnimationUnchecked(final int i, SparseArray<InsetsSourceControl> sparseArray, boolean z) {
                if (i == 0) {
                    return;
                }
                InsetsPolicy.this.mAnimatingShown = z;
                InsetsState insetsState = InsetsPolicy.this.mFocusedWin.getInsetsState();
                InsetsController.InternalAnimationControlListener internalAnimationControlListener = this.mListener;
                this.mAnimationControl = new InsetsAnimationControlImpl(sparseArray, (Rect) null, insetsState, internalAnimationControlListener, i, this, internalAnimationControlListener.getDurationMs(), InsetsPolicyAnimationControlListener.this.getInsetsInterpolator(), !z ? 1 : 0, !z ? 1 : 0, (CompatibilityInfo.Translator) null, (ImeTracker.Token) null);
                SurfaceAnimationThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.InsetsPolicy$InsetsPolicyAnimationControlListener$InsetsPolicyAnimationControlCallbacks$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        InsetsPolicy.InsetsPolicyAnimationControlListener.InsetsPolicyAnimationControlCallbacks.this.lambda$controlAnimationUnchecked$0(i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$controlAnimationUnchecked$0(int i) {
                this.mListener.onReady(this.mAnimationControl, i);
            }

            public void scheduleApplyChangeInsets(InsetsAnimationControlRunner insetsAnimationControlRunner) {
                if (this.mAnimationControl.applyChangeInsets((InsetsState) null)) {
                    this.mAnimationControl.finish(InsetsPolicy.this.mAnimatingShown);
                }
            }

            public void applySurfaceParams(SyncRtSurfaceTransactionApplier.SurfaceParams... surfaceParamsArr) {
                SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                for (int length = surfaceParamsArr.length - 1; length >= 0; length--) {
                    SyncRtSurfaceTransactionApplier.applyParams(transaction, surfaceParamsArr[length], InsetsPolicy.this.mTmpFloat9);
                }
                transaction.apply();
                transaction.close();
            }

            public void releaseSurfaceControlFromRt(SurfaceControl surfaceControl) {
                surfaceControl.release();
            }
        }
    }
}
