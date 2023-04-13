package com.android.server.p014wm;

import android.animation.AnimationHandler;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.PowerManagerInternal;
import android.os.Trace;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Choreographer;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.window.ScreenCapture;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.SfVsyncFrameCallbackProvider;
import com.android.server.AnimationThread;
import com.android.server.p014wm.LocalAnimationAdapter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.SurfaceAnimationRunner */
/* loaded from: classes2.dex */
public class SurfaceAnimationRunner {
    public final AnimationHandler mAnimationHandler;
    @GuardedBy({"mLock"})
    public boolean mAnimationStartDeferred;
    public final Handler mAnimationThreadHandler;
    public final AnimatorFactory mAnimatorFactory;
    public boolean mApplyScheduled;
    public final Runnable mApplyTransactionRunnable;
    public final Object mCancelLock;
    @VisibleForTesting
    Choreographer mChoreographer;
    public final ExecutorService mEdgeExtensionExecutor;
    public final Object mEdgeExtensionLock;
    @GuardedBy({"mEdgeExtensionLock"})
    public final ArrayMap<SurfaceControl, ArrayList<SurfaceControl>> mEdgeExtensions;
    public final SurfaceControl.Transaction mFrameTransaction;
    public final Object mLock;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final ArrayMap<SurfaceControl, RunningAnimation> mPendingAnimations;
    public final PowerManagerInternal mPowerManagerInternal;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final ArrayMap<SurfaceControl, RunningAnimation> mPreProcessingAnimations;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final ArrayMap<SurfaceControl, RunningAnimation> mRunningAnimations;
    public final Handler mSurfaceAnimationHandler;

    @VisibleForTesting
    /* renamed from: com.android.server.wm.SurfaceAnimationRunner$AnimatorFactory */
    /* loaded from: classes2.dex */
    public interface AnimatorFactory {
        ValueAnimator makeAnimator();
    }

    public SurfaceAnimationRunner(Supplier<SurfaceControl.Transaction> supplier, PowerManagerInternal powerManagerInternal) {
        this(null, null, supplier.get(), powerManagerInternal);
    }

    @VisibleForTesting
    public SurfaceAnimationRunner(AnimationHandler.AnimationFrameCallbackProvider animationFrameCallbackProvider, AnimatorFactory animatorFactory, SurfaceControl.Transaction transaction, PowerManagerInternal powerManagerInternal) {
        this.mLock = new Object();
        this.mCancelLock = new Object();
        this.mEdgeExtensionLock = new Object();
        this.mAnimationThreadHandler = AnimationThread.getHandler();
        Handler handler = SurfaceAnimationThread.getHandler();
        this.mSurfaceAnimationHandler = handler;
        this.mApplyTransactionRunnable = new Runnable() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceAnimationRunner.this.applyTransaction();
            }
        };
        this.mEdgeExtensionExecutor = Executors.newFixedThreadPool(2);
        this.mPendingAnimations = new ArrayMap<>();
        this.mPreProcessingAnimations = new ArrayMap<>();
        this.mRunningAnimations = new ArrayMap<>();
        this.mEdgeExtensions = new ArrayMap<>();
        handler.runWithScissors(new Runnable() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceAnimationRunner.this.lambda$new$0();
            }
        }, 0L);
        this.mFrameTransaction = transaction;
        AnimationHandler animationHandler = new AnimationHandler();
        this.mAnimationHandler = animationHandler;
        animationHandler.setProvider(animationFrameCallbackProvider == null ? new SfVsyncFrameCallbackProvider(this.mChoreographer) : animationFrameCallbackProvider);
        this.mAnimatorFactory = animatorFactory == null ? new AnimatorFactory() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda3
            @Override // com.android.server.p014wm.SurfaceAnimationRunner.AnimatorFactory
            public final ValueAnimator makeAnimator() {
                ValueAnimator lambda$new$1;
                lambda$new$1 = SurfaceAnimationRunner.this.lambda$new$1();
                return lambda$new$1;
            }
        } : animatorFactory;
        this.mPowerManagerInternal = powerManagerInternal;
    }

    public /* synthetic */ void lambda$new$0() {
        this.mChoreographer = Choreographer.getSfInstance();
    }

    public /* synthetic */ ValueAnimator lambda$new$1() {
        return new SfValueAnimator();
    }

    public void deferStartingAnimations() {
        synchronized (this.mLock) {
            this.mAnimationStartDeferred = true;
        }
    }

    public void continueStartingAnimations() {
        synchronized (this.mLock) {
            this.mAnimationStartDeferred = false;
            if (!this.mPendingAnimations.isEmpty() && this.mPreProcessingAnimations.isEmpty()) {
                this.mChoreographer.postFrameCallback(new SurfaceAnimationRunner$$ExternalSyntheticLambda4(this));
            }
        }
    }

    public void startAnimation(final LocalAnimationAdapter.AnimationSpec animationSpec, final SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, Runnable runnable) {
        synchronized (this.mLock) {
            final RunningAnimation runningAnimation = new RunningAnimation(animationSpec, surfaceControl, runnable);
            boolean requiresEdgeExtension = requiresEdgeExtension(animationSpec);
            if (requiresEdgeExtension) {
                ArrayList<SurfaceControl> arrayList = new ArrayList<>();
                synchronized (this.mEdgeExtensionLock) {
                    this.mEdgeExtensions.put(surfaceControl, arrayList);
                }
                this.mPreProcessingAnimations.put(surfaceControl, runningAnimation);
                transaction.addTransactionCommittedListener(this.mEdgeExtensionExecutor, new SurfaceControl.TransactionCommittedListener() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda5
                    public final void onTransactionCommitted() {
                        SurfaceAnimationRunner.this.lambda$startAnimation$2(animationSpec, surfaceControl, runningAnimation);
                    }
                });
            }
            if (!requiresEdgeExtension) {
                this.mPendingAnimations.put(surfaceControl, runningAnimation);
                if (!this.mAnimationStartDeferred && this.mPreProcessingAnimations.isEmpty()) {
                    this.mChoreographer.postFrameCallback(new SurfaceAnimationRunner$$ExternalSyntheticLambda4(this));
                }
                applyTransformation(runningAnimation, transaction, 0L);
            }
        }
    }

    public /* synthetic */ void lambda$startAnimation$2(LocalAnimationAdapter.AnimationSpec animationSpec, SurfaceControl surfaceControl, RunningAnimation runningAnimation) {
        WindowAnimationSpec asWindowAnimationSpec = animationSpec.asWindowAnimationSpec();
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        edgeExtendWindow(surfaceControl, asWindowAnimationSpec.getRootTaskBounds(), asWindowAnimationSpec.getAnimation(), transaction);
        synchronized (this.mLock) {
            if (this.mPreProcessingAnimations.get(surfaceControl) == runningAnimation) {
                synchronized (this.mEdgeExtensionLock) {
                    if (!this.mEdgeExtensions.isEmpty()) {
                        transaction.apply();
                    }
                }
                this.mPreProcessingAnimations.remove(surfaceControl);
                this.mPendingAnimations.put(surfaceControl, runningAnimation);
                if (!this.mAnimationStartDeferred && this.mPreProcessingAnimations.isEmpty()) {
                    this.mChoreographer.postFrameCallback(new SurfaceAnimationRunner$$ExternalSyntheticLambda4(this));
                }
            }
        }
    }

    public final boolean requiresEdgeExtension(LocalAnimationAdapter.AnimationSpec animationSpec) {
        return animationSpec.asWindowAnimationSpec() != null && animationSpec.asWindowAnimationSpec().hasExtension();
    }

    public void onAnimationCancelled(SurfaceControl surfaceControl) {
        synchronized (this.mLock) {
            if (this.mPendingAnimations.containsKey(surfaceControl)) {
                this.mPendingAnimations.remove(surfaceControl);
            } else if (this.mPreProcessingAnimations.containsKey(surfaceControl)) {
                this.mPreProcessingAnimations.remove(surfaceControl);
            } else {
                final RunningAnimation runningAnimation = this.mRunningAnimations.get(surfaceControl);
                if (runningAnimation != null) {
                    this.mRunningAnimations.remove(surfaceControl);
                    synchronized (this.mCancelLock) {
                        runningAnimation.mCancelled = true;
                    }
                    this.mSurfaceAnimationHandler.post(new Runnable() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SurfaceAnimationRunner.this.lambda$onAnimationCancelled$3(runningAnimation);
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$onAnimationCancelled$3(RunningAnimation runningAnimation) {
        runningAnimation.mAnim.cancel();
        applyTransaction();
    }

    @GuardedBy({"mLock"})
    public final void startPendingAnimationsLocked() {
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            startAnimationLocked(this.mPendingAnimations.valueAt(size));
        }
        this.mPendingAnimations.clear();
    }

    @GuardedBy({"mLock"})
    public final void startAnimationLocked(final RunningAnimation runningAnimation) {
        final ValueAnimator makeAnimator = this.mAnimatorFactory.makeAnimator();
        makeAnimator.overrideDurationScale(1.0f);
        makeAnimator.setDuration(runningAnimation.mAnimSpec.getDuration());
        makeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.server.wm.SurfaceAnimationRunner$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                SurfaceAnimationRunner.this.lambda$startAnimationLocked$4(runningAnimation, makeAnimator, valueAnimator);
            }
        });
        makeAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.server.wm.SurfaceAnimationRunner.1
            {
                SurfaceAnimationRunner.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                synchronized (SurfaceAnimationRunner.this.mCancelLock) {
                    if (!runningAnimation.mCancelled) {
                        SurfaceAnimationRunner.this.mFrameTransaction.setAlpha(runningAnimation.mLeash, 1.0f);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                synchronized (SurfaceAnimationRunner.this.mLock) {
                    SurfaceAnimationRunner.this.mRunningAnimations.remove(runningAnimation.mLeash);
                    synchronized (SurfaceAnimationRunner.this.mCancelLock) {
                        if (!runningAnimation.mCancelled) {
                            SurfaceAnimationRunner.this.mAnimationThreadHandler.post(runningAnimation.mFinishCallback);
                        }
                    }
                }
            }
        });
        runningAnimation.mAnim = makeAnimator;
        this.mRunningAnimations.put(runningAnimation.mLeash, runningAnimation);
        makeAnimator.start();
        if (runningAnimation.mAnimSpec.canSkipFirstFrame()) {
            makeAnimator.setCurrentPlayTime(this.mChoreographer.getFrameIntervalNanos() / 1000000);
        }
        makeAnimator.doAnimationFrame(this.mChoreographer.getFrameTime());
    }

    public /* synthetic */ void lambda$startAnimationLocked$4(RunningAnimation runningAnimation, ValueAnimator valueAnimator, ValueAnimator valueAnimator2) {
        synchronized (this.mCancelLock) {
            if (!runningAnimation.mCancelled) {
                long duration = valueAnimator.getDuration();
                long currentPlayTime = valueAnimator.getCurrentPlayTime();
                if (currentPlayTime <= duration) {
                    duration = currentPlayTime;
                }
                applyTransformation(runningAnimation, this.mFrameTransaction, duration);
            }
        }
        scheduleApplyTransaction();
    }

    public final void applyTransformation(RunningAnimation runningAnimation, SurfaceControl.Transaction transaction, long j) {
        runningAnimation.mAnimSpec.apply(transaction, runningAnimation.mLeash, j);
    }

    public final void startAnimations(long j) {
        synchronized (this.mLock) {
            if (this.mPreProcessingAnimations.isEmpty()) {
                startPendingAnimationsLocked();
                this.mPowerManagerInternal.setPowerBoost(0, 0);
            }
        }
    }

    public final void scheduleApplyTransaction() {
        if (this.mApplyScheduled) {
            return;
        }
        this.mChoreographer.postCallback(3, this.mApplyTransactionRunnable, null);
        this.mApplyScheduled = true;
    }

    public final void applyTransaction() {
        this.mFrameTransaction.setAnimationTransaction();
        this.mFrameTransaction.setFrameTimelineVsync(this.mChoreographer.getVsyncId());
        this.mFrameTransaction.apply();
        this.mApplyScheduled = false;
    }

    public final void edgeExtendWindow(SurfaceControl surfaceControl, Rect rect, Animation animation, SurfaceControl.Transaction transaction) {
        Transformation transformation = new Transformation();
        animation.getTransformationAt(0.0f, transformation);
        Transformation transformation2 = new Transformation();
        animation.getTransformationAt(1.0f, transformation2);
        Insets min = Insets.min(transformation.getInsets(), transformation2.getInsets());
        int height = rect.height();
        int width = rect.width();
        if (min.left < 0) {
            int i = rect.left;
            createExtensionSurface(surfaceControl, new Rect(i, rect.top, i + 1, rect.bottom), new Rect(0, 0, -min.left, height), rect.left + min.left, rect.top, "Left Edge Extension", transaction);
        }
        if (min.top < 0) {
            int i2 = rect.left;
            int i3 = rect.top;
            createExtensionSurface(surfaceControl, new Rect(i2, i3, width, i3 + 1), new Rect(0, 0, width, -min.top), rect.left, rect.top + min.top, "Top Edge Extension", transaction);
        }
        if (min.right < 0) {
            int i4 = rect.right;
            createExtensionSurface(surfaceControl, new Rect(i4 - 1, rect.top, i4, rect.bottom), new Rect(0, 0, -min.right, height), rect.right, rect.top, "Right Edge Extension", transaction);
        }
        if (min.bottom < 0) {
            int i5 = rect.left;
            int i6 = rect.bottom;
            createExtensionSurface(surfaceControl, new Rect(i5, i6 - 1, rect.right, i6), new Rect(0, 0, width, -min.bottom), rect.left, rect.bottom, "Bottom Edge Extension", transaction);
        }
    }

    public final void createExtensionSurface(SurfaceControl surfaceControl, Rect rect, Rect rect2, int i, int i2, String str, SurfaceControl.Transaction transaction) {
        Trace.traceBegin(32L, "createExtensionSurface");
        doCreateExtensionSurface(surfaceControl, rect, rect2, i, i2, str, transaction);
        Trace.traceEnd(32L);
    }

    public final void doCreateExtensionSurface(SurfaceControl surfaceControl, Rect rect, Rect rect2, int i, int i2, String str, SurfaceControl.Transaction transaction) {
        ScreenCapture.ScreenshotHardwareBuffer captureLayers = ScreenCapture.captureLayers(new ScreenCapture.LayerCaptureArgs.Builder(surfaceControl).setSourceCrop(rect).setFrameScale(1.0f).setPixelFormat(1).setChildrenOnly(true).setAllowProtected(true).setCaptureSecureLayers(true).build());
        if (captureLayers == null) {
            Log.e("SurfaceAnimationRunner", "Failed to create edge extension - edge buffer is null");
            return;
        }
        SurfaceControl build = new SurfaceControl.Builder().setName(str).setHidden(true).setCallsite("DefaultTransitionHandler#startAnimation").setOpaque(true).setBufferSize(rect2.width(), rect2.height()).build();
        Bitmap asBitmap = captureLayers.asBitmap();
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(asBitmap, tileMode, tileMode);
        Paint paint = new Paint();
        paint.setShader(bitmapShader);
        Surface surface = new Surface(build);
        Canvas lockHardwareCanvas = surface.lockHardwareCanvas();
        lockHardwareCanvas.drawRect(rect2, paint);
        surface.unlockCanvasAndPost(lockHardwareCanvas);
        surface.release();
        synchronized (this.mEdgeExtensionLock) {
            if (!this.mEdgeExtensions.containsKey(surfaceControl)) {
                transaction.remove(build);
                return;
            }
            transaction.reparent(build, surfaceControl);
            transaction.setLayer(build, Integer.MIN_VALUE);
            transaction.setPosition(build, i, i2);
            transaction.setVisibility(build, true);
            this.mEdgeExtensions.get(surfaceControl).add(build);
        }
    }

    /* renamed from: com.android.server.wm.SurfaceAnimationRunner$RunningAnimation */
    /* loaded from: classes2.dex */
    public static final class RunningAnimation {
        public ValueAnimator mAnim;
        public final LocalAnimationAdapter.AnimationSpec mAnimSpec;
        @GuardedBy({"mCancelLock"})
        public boolean mCancelled;
        public final Runnable mFinishCallback;
        public final SurfaceControl mLeash;

        public RunningAnimation(LocalAnimationAdapter.AnimationSpec animationSpec, SurfaceControl surfaceControl, Runnable runnable) {
            this.mAnimSpec = animationSpec;
            this.mLeash = surfaceControl;
            this.mFinishCallback = runnable;
        }
    }

    public void onAnimationLeashLost(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction) {
        synchronized (this.mEdgeExtensionLock) {
            if (this.mEdgeExtensions.containsKey(surfaceControl)) {
                ArrayList<SurfaceControl> arrayList = this.mEdgeExtensions.get(surfaceControl);
                for (int i = 0; i < arrayList.size(); i++) {
                    transaction.remove(arrayList.get(i));
                }
                this.mEdgeExtensions.remove(surfaceControl);
            }
        }
    }

    /* renamed from: com.android.server.wm.SurfaceAnimationRunner$SfValueAnimator */
    /* loaded from: classes2.dex */
    public class SfValueAnimator extends ValueAnimator {
        public SfValueAnimator() {
            SurfaceAnimationRunner.this = r1;
            setFloatValues(0.0f, 1.0f);
        }

        public AnimationHandler getAnimationHandler() {
            return SurfaceAnimationRunner.this.mAnimationHandler;
        }
    }
}
