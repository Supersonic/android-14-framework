package com.android.server.p014wm;

import android.os.HandlerExecutor;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.AsyncRotationController */
/* loaded from: classes2.dex */
public class AsyncRotationController extends FadeAnimationController implements Consumer<WindowState> {
    public final boolean mHasScreenRotationAnimation;
    public boolean mHideImmediately;
    public boolean mIsStartTransactionCommitted;
    public boolean mIsSyncDrawRequested;
    public WindowToken mNavBarToken;
    public Runnable mOnShowRunnable;
    public final int mOriginalRotation;
    public SeamlessRotator mRotator;
    public final WindowManagerService mService;
    public final ArrayMap<WindowToken, Operation> mTargetWindowTokens;
    public Runnable mTimeoutRunnable;
    public final int mTransitionOp;

    public static /* synthetic */ void lambda$keepAppearanceInPreviousRotation$0(SurfaceControl.Transaction transaction) {
    }

    public AsyncRotationController(DisplayContent displayContent) {
        super(displayContent);
        this.mTargetWindowTokens = new ArrayMap<>();
        this.mService = displayContent.mWmService;
        int rotation = displayContent.getWindowConfiguration().getRotation();
        this.mOriginalRotation = rotation;
        boolean z = false;
        if (displayContent.mTransitionController.getCollectingTransitionType() == 6) {
            DisplayRotation displayRotation = displayContent.getDisplayRotation();
            WindowState topFullscreenOpaqueWindow = displayContent.getDisplayPolicy().getTopFullscreenOpaqueWindow();
            if (topFullscreenOpaqueWindow != null && topFullscreenOpaqueWindow.mAttrs.rotationAnimation == 3 && topFullscreenOpaqueWindow.getTask() != null && displayRotation.canRotateSeamlessly(rotation, displayRotation.getRotation())) {
                this.mTransitionOp = 3;
            } else {
                this.mTransitionOp = 2;
            }
        } else if (displayContent.mTransitionController.isShellTransitionsEnabled()) {
            this.mTransitionOp = 1;
        } else {
            this.mTransitionOp = 0;
        }
        z = (displayContent.getRotationAnimation() != null || this.mTransitionOp == 2) ? true : z;
        this.mHasScreenRotationAnimation = z;
        if (z) {
            this.mHideImmediately = true;
        }
        displayContent.forAllWindows((Consumer<WindowState>) this, true);
        if (this.mTransitionOp == 0) {
            this.mIsStartTransactionCommitted = true;
        } else if (displayContent.mTransitionController.isCollecting(displayContent)) {
            keepAppearanceInPreviousRotation();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0047, code lost:
        if (r1 != 3) goto L23;
     */
    @Override // java.util.function.Consumer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void accept(WindowState windowState) {
        if (windowState.mHasSurface && canBeAsync(windowState.mToken)) {
            int i = this.mTransitionOp;
            if (i == 0 && windowState.mForceSeamlesslyRotate) {
                return;
            }
            int i2 = 1;
            if (windowState.mAttrs.type == 2019) {
                boolean navigationBarCanMove = this.mDisplayContent.getDisplayPolicy().navigationBarCanMove();
                int i3 = this.mTransitionOp;
                if (i3 == 0) {
                    this.mNavBarToken = windowState.mToken;
                    if (navigationBarCanMove) {
                        return;
                    }
                    RecentsAnimationController recentsAnimationController = this.mService.getRecentsAnimationController();
                    if (recentsAnimationController != null && recentsAnimationController.isNavigationBarAttachedToApp()) {
                        return;
                    }
                } else {
                    if (!navigationBarCanMove) {
                    }
                    this.mTargetWindowTokens.put(windowState.mToken, new Operation(i2));
                    return;
                }
                i2 = 2;
                this.mTargetWindowTokens.put(windowState.mToken, new Operation(i2));
                return;
            }
            if (i != 3 && !windowState.mForceSeamlesslyRotate) {
                i2 = 2;
            }
            this.mTargetWindowTokens.put(windowState.mToken, new Operation(i2));
        }
    }

    public static boolean canBeAsync(WindowToken windowToken) {
        int i = windowToken.windowType;
        return (i <= 99 || i == 2011 || i == 2013 || i == 2040) ? false : true;
    }

    public void keepAppearanceInPreviousRotation() {
        if (this.mIsSyncDrawRequested) {
            return;
        }
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            if (!this.mTargetWindowTokens.valueAt(size).canDrawBeforeStartTransaction()) {
                WindowToken keyAt = this.mTargetWindowTokens.keyAt(size);
                for (int childCount = keyAt.getChildCount() - 1; childCount >= 0; childCount--) {
                    keyAt.getChildAt(childCount).applyWithNextDraw(new Consumer() { // from class: com.android.server.wm.AsyncRotationController$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            AsyncRotationController.lambda$keepAppearanceInPreviousRotation$0((SurfaceControl.Transaction) obj);
                        }
                    });
                }
            }
        }
        this.mIsSyncDrawRequested = true;
    }

    public void updateTargetWindows() {
        if (this.mTransitionOp == 0 || !this.mIsStartTransactionCommitted) {
            return;
        }
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            Operation valueAt = this.mTargetWindowTokens.valueAt(size);
            if (!valueAt.mIsCompletionPending && valueAt.mAction != 1) {
                WindowToken keyAt = this.mTargetWindowTokens.keyAt(size);
                int childCount = keyAt.getChildCount();
                int i = 0;
                for (int i2 = childCount - 1; i2 >= 0; i2--) {
                    WindowState childAt = keyAt.getChildAt(i2);
                    if (childAt.isDrawn() || !childAt.mWinAnimator.getShown()) {
                        i++;
                    }
                }
                if (i == childCount) {
                    this.mDisplayContent.finishAsyncRotation(keyAt);
                }
            }
        }
    }

    public final void finishOp(WindowToken windowToken) {
        SurfaceControl surfaceControl;
        Operation remove = this.mTargetWindowTokens.remove(windowToken);
        if (remove == null) {
            return;
        }
        if (remove.mDrawTransaction != null) {
            windowToken.getSyncTransaction().merge(remove.mDrawTransaction);
            remove.mDrawTransaction = null;
        }
        int i = remove.mAction;
        if (i == 2) {
            fadeWindowToken(true, windowToken, 64);
        } else if (i != 1 || this.mRotator == null || (surfaceControl = remove.mLeash) == null || !surfaceControl.isValid()) {
        } else {
            this.mRotator.setIdentityMatrix(windowToken.getSyncTransaction(), remove.mLeash);
        }
    }

    public void completeAll() {
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            finishOp(this.mTargetWindowTokens.keyAt(size));
        }
        this.mTargetWindowTokens.clear();
        Runnable runnable = this.mTimeoutRunnable;
        if (runnable != null) {
            this.mService.f1164mH.removeCallbacks(runnable);
        }
        Runnable runnable2 = this.mOnShowRunnable;
        if (runnable2 != null) {
            runnable2.run();
            this.mOnShowRunnable = null;
        }
    }

    public boolean completeRotation(WindowToken windowToken) {
        Operation operation;
        if (!this.mIsStartTransactionCommitted) {
            Operation operation2 = this.mTargetWindowTokens.get(windowToken);
            if (operation2 != null) {
                operation2.mIsCompletionPending = true;
            }
            return false;
        } else if (!(this.mTransitionOp == 1 && windowToken.mTransitionController.inTransition() && (operation = this.mTargetWindowTokens.get(windowToken)) != null && operation.mAction == 2) && isTargetToken(windowToken)) {
            if (this.mHasScreenRotationAnimation || this.mTransitionOp != 0) {
                finishOp(windowToken);
                if (this.mTargetWindowTokens.isEmpty()) {
                    Runnable runnable = this.mTimeoutRunnable;
                    if (runnable != null) {
                        this.mService.f1164mH.removeCallbacks(runnable);
                    }
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public void start() {
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            WindowToken keyAt = this.mTargetWindowTokens.keyAt(size);
            Operation valueAt = this.mTargetWindowTokens.valueAt(size);
            int i = valueAt.mAction;
            if (i == 2) {
                fadeWindowToken(false, keyAt, 64);
                valueAt.mLeash = keyAt.getAnimationLeash();
            } else if (i == 1) {
                valueAt.mLeash = keyAt.mSurfaceControl;
            }
        }
        if (this.mHasScreenRotationAnimation) {
            scheduleTimeout();
        }
    }

    public final void scheduleTimeout() {
        if (this.mTimeoutRunnable == null) {
            this.mTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.AsyncRotationController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AsyncRotationController.this.lambda$scheduleTimeout$1();
                }
            };
        }
        this.mService.f1164mH.postDelayed(this.mTimeoutRunnable, 2000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleTimeout$1() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                StringBuilder sb = new StringBuilder();
                sb.append("Async rotation timeout: ");
                sb.append(!this.mIsStartTransactionCommitted ? " start transaction is not committed" : this.mTargetWindowTokens);
                Slog.i("AsyncRotation", sb.toString());
                this.mIsStartTransactionCommitted = true;
                this.mDisplayContent.finishAsyncRotationIfPossible();
                this.mService.mWindowPlacerLocked.performSurfacePlacement();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void hideImmediately(WindowToken windowToken) {
        if (isTargetToken(windowToken)) {
            return;
        }
        boolean z = this.mHideImmediately;
        this.mHideImmediately = true;
        Operation operation = new Operation(2);
        this.mTargetWindowTokens.put(windowToken, operation);
        fadeWindowToken(false, windowToken, 64);
        operation.mLeash = windowToken.getAnimationLeash();
        this.mHideImmediately = z;
    }

    public boolean isAsync(WindowState windowState) {
        WindowToken windowToken = windowState.mToken;
        return windowToken == this.mNavBarToken || (windowState.mForceSeamlesslyRotate && this.mTransitionOp == 0) || isTargetToken(windowToken);
    }

    public boolean isTargetToken(WindowToken windowToken) {
        return this.mTargetWindowTokens.containsKey(windowToken);
    }

    public boolean shouldFreezeInsetsPosition(WindowState windowState) {
        return TransitionController.SYNC_METHOD == 1 && this.mTransitionOp != 0 && !this.mIsStartTransactionCommitted && isTargetToken(windowState.mToken);
    }

    public SurfaceControl.Transaction getDrawTransaction(WindowToken windowToken) {
        Operation operation;
        if (this.mTransitionOp == 0 || (operation = this.mTargetWindowTokens.get(windowToken)) == null) {
            return null;
        }
        if (operation.mDrawTransaction == null) {
            operation.mDrawTransaction = new SurfaceControl.Transaction();
        }
        return operation.mDrawTransaction;
    }

    public void setOnShowRunnable(Runnable runnable) {
        this.mOnShowRunnable = runnable;
    }

    public void setupStartTransaction(SurfaceControl.Transaction transaction) {
        if (this.mIsStartTransactionCommitted) {
            return;
        }
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            Operation valueAt = this.mTargetWindowTokens.valueAt(size);
            SurfaceControl surfaceControl = valueAt.mLeash;
            if (surfaceControl != null && surfaceControl.isValid()) {
                if (this.mHasScreenRotationAnimation && valueAt.mAction == 2) {
                    transaction.setAlpha(surfaceControl, 0.0f);
                } else {
                    if (this.mRotator == null) {
                        this.mRotator = new SeamlessRotator(this.mOriginalRotation, this.mDisplayContent.getWindowConfiguration().getRotation(), this.mDisplayContent.getDisplayInfo(), false);
                    }
                    this.mRotator.applyTransform(transaction, surfaceControl);
                }
            }
        }
        transaction.addTransactionCommittedListener(new HandlerExecutor(this.mService.f1164mH), new SurfaceControl.TransactionCommittedListener() { // from class: com.android.server.wm.AsyncRotationController$$ExternalSyntheticLambda2
            public final void onTransactionCommitted() {
                AsyncRotationController.this.lambda$setupStartTransaction$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupStartTransaction$2() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mIsStartTransactionCommitted = true;
                for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
                    if (this.mTargetWindowTokens.valueAt(size).mIsCompletionPending) {
                        this.mDisplayContent.finishAsyncRotation(this.mTargetWindowTokens.keyAt(size));
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onTransitionFinished() {
        if (this.mTransitionOp == 2) {
            return;
        }
        for (int size = this.mTargetWindowTokens.size() - 1; size >= 0; size--) {
            WindowToken keyAt = this.mTargetWindowTokens.keyAt(size);
            if (keyAt.isVisible()) {
                int childCount = keyAt.getChildCount() - 1;
                while (true) {
                    if (childCount < 0) {
                        break;
                    } else if (keyAt.getChildAt(childCount).isDrawFinishedLw()) {
                        this.mDisplayContent.finishAsyncRotation(keyAt);
                        break;
                    } else {
                        childCount--;
                    }
                }
            } else {
                this.mDisplayContent.finishAsyncRotation(keyAt);
            }
        }
        if (this.mTargetWindowTokens.isEmpty()) {
            return;
        }
        scheduleTimeout();
    }

    public boolean handleFinishDrawing(WindowState windowState, SurfaceControl.Transaction transaction) {
        Operation operation;
        if (this.mTransitionOp == 0 || (operation = this.mTargetWindowTokens.get(windowState.mToken)) == null) {
            return false;
        }
        if (transaction == null || !this.mIsSyncDrawRequested || operation.canDrawBeforeStartTransaction()) {
            this.mDisplayContent.finishAsyncRotation(windowState.mToken);
            return false;
        }
        SurfaceControl.Transaction transaction2 = operation.mDrawTransaction;
        if (transaction2 == null) {
            if (windowState.isClientLocal()) {
                SurfaceControl.Transaction transaction3 = this.mService.mTransactionFactory.get();
                operation.mDrawTransaction = transaction3;
                transaction3.merge(transaction);
            } else {
                operation.mDrawTransaction = transaction;
            }
        } else {
            transaction2.merge(transaction);
        }
        this.mDisplayContent.finishAsyncRotation(windowState.mToken);
        return true;
    }

    @Override // com.android.server.p014wm.FadeAnimationController
    public Animation getFadeInAnimation() {
        if (this.mHasScreenRotationAnimation) {
            return AnimationUtils.loadAnimation(this.mContext, 17432704);
        }
        return super.getFadeInAnimation();
    }

    @Override // com.android.server.p014wm.FadeAnimationController
    public Animation getFadeOutAnimation() {
        if (this.mHideImmediately) {
            float f = this.mTransitionOp == 2 ? 1.0f : 0.0f;
            return new AlphaAnimation(f, f);
        }
        return super.getFadeOutAnimation();
    }

    /* renamed from: com.android.server.wm.AsyncRotationController$Operation */
    /* loaded from: classes2.dex */
    public static class Operation {
        public final int mAction;
        public SurfaceControl.Transaction mDrawTransaction;
        public boolean mIsCompletionPending;
        public SurfaceControl mLeash;

        public Operation(int i) {
            this.mAction = i;
        }

        public boolean canDrawBeforeStartTransaction() {
            return (TransitionController.SYNC_METHOD == 1 || this.mAction == 1) ? false : true;
        }
    }
}
