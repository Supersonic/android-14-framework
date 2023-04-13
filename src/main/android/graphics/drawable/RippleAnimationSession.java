package android.graphics.drawable;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.animation.RenderNodeAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class RippleAnimationSession {
    private static final int ENTER_ANIM_DURATION = 450;
    private static final int EXIT_ANIM_DURATION = 375;
    private static final long MAX_NOISE_PHASE = 32;
    private static final long NOISE_ANIMATION_DURATION = 7000;
    private static final String TAG = "RippleAnimationSession";
    private AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> mCanvasProperties;
    private Animator mCurrentAnimation;
    private boolean mForceSoftware;
    private Animator mLoopAnimation;
    private Consumer<RippleAnimationSession> mOnSessionEnd;
    private Runnable mOnUpdate;
    private final AnimationProperties<Float, Paint> mProperties;
    private long mStartTime;
    private static final TimeInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession(AnimationProperties<Float, Paint> properties, boolean forceSoftware) {
        this.mProperties = properties;
        this.mForceSoftware = forceSoftware;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isForceSoftware() {
        return this.mForceSoftware;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession enter(Canvas canvas) {
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        if (useRTAnimations(canvas)) {
            enterHardware((RecordingCanvas) canvas);
        } else {
            enterSoftware();
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void end() {
        Animator animator = this.mCurrentAnimation;
        if (animator != null) {
            animator.end();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession exit(Canvas canvas) {
        if (useRTAnimations(canvas)) {
            exitHardware((RecordingCanvas) canvas);
        } else {
            exitSoftware();
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationEnd(Animator anim) {
        notifyUpdate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession setOnSessionEnd(Consumer<RippleAnimationSession> onSessionEnd) {
        this.mOnSessionEnd = onSessionEnd;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession setOnAnimationUpdated(Runnable run) {
        this.mOnUpdate = run;
        return this;
    }

    private boolean useRTAnimations(Canvas canvas) {
        if (!this.mForceSoftware && canvas.isHardwareAccelerated()) {
            RecordingCanvas hwCanvas = (RecordingCanvas) canvas;
            return hwCanvas.mNode != null && hwCanvas.mNode.isAttached();
        }
        return false;
    }

    private void exitSoftware() {
        final ValueAnimator expand = ValueAnimator.ofFloat(0.5f, 1.0f);
        expand.setDuration(375L);
        expand.setStartDelay(computeDelay());
        expand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.graphics.drawable.RippleAnimationSession$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RippleAnimationSession.this.lambda$exitSoftware$0(expand, valueAnimator);
            }
        });
        expand.addListener(new AnimatorListener(this) { // from class: android.graphics.drawable.RippleAnimationSession.1
            @Override // android.graphics.drawable.RippleAnimationSession.AnimatorListener, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (RippleAnimationSession.this.mLoopAnimation != null) {
                    RippleAnimationSession.this.mLoopAnimation.cancel();
                }
                Consumer<RippleAnimationSession> onEnd = RippleAnimationSession.this.mOnSessionEnd;
                if (onEnd != null) {
                    onEnd.accept(RippleAnimationSession.this);
                }
                if (RippleAnimationSession.this.mCurrentAnimation == expand) {
                    RippleAnimationSession.this.mCurrentAnimation = null;
                }
            }
        });
        expand.setInterpolator(LINEAR_INTERPOLATOR);
        expand.start();
        this.mCurrentAnimation = expand;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$exitSoftware$0(ValueAnimator expand, ValueAnimator updatedAnimation) {
        notifyUpdate();
        this.mProperties.getShader().setProgress(((Float) expand.getAnimatedValue()).floatValue());
    }

    private long computeDelay() {
        long timePassed = AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
        return Math.max(450 - timePassed, 0L);
    }

    private void notifyUpdate() {
        Runnable runnable = this.mOnUpdate;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleAnimationSession setForceSoftwareAnimation(boolean forceSw) {
        this.mForceSoftware = forceSw;
        return this;
    }

    private void exitHardware(RecordingCanvas canvas) {
        AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> props = getCanvasProperties();
        final RenderNodeAnimator exit = new RenderNodeAnimator(props.getProgress(), 1.0f);
        exit.setDuration(375L);
        exit.addListener(new AnimatorListener(this) { // from class: android.graphics.drawable.RippleAnimationSession.2
            @Override // android.graphics.drawable.RippleAnimationSession.AnimatorListener, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (RippleAnimationSession.this.mLoopAnimation != null) {
                    RippleAnimationSession.this.mLoopAnimation.cancel();
                }
                Consumer<RippleAnimationSession> onEnd = RippleAnimationSession.this.mOnSessionEnd;
                if (onEnd != null) {
                    onEnd.accept(RippleAnimationSession.this);
                }
                if (RippleAnimationSession.this.mCurrentAnimation == exit) {
                    RippleAnimationSession.this.mCurrentAnimation = null;
                }
            }
        });
        exit.setTarget(canvas);
        exit.setInterpolator(LINEAR_INTERPOLATOR);
        long delay = computeDelay();
        exit.setStartDelay(delay);
        exit.start();
        this.mCurrentAnimation = exit;
    }

    private void enterHardware(RecordingCanvas canvas) {
        AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> props = getCanvasProperties();
        RenderNodeAnimator expand = new RenderNodeAnimator(props.getProgress(), 0.5f);
        expand.setTarget(canvas);
        RenderNodeAnimator loop = new RenderNodeAnimator(props.getNoisePhase(), (float) (this.mStartTime + 32));
        loop.setTarget(canvas);
        startAnimation(expand, loop);
        this.mCurrentAnimation = expand;
    }

    private void startAnimation(Animator expand, Animator loop) {
        expand.setDuration(450L);
        expand.addListener(new AnimatorListener(this));
        expand.setInterpolator(FAST_OUT_SLOW_IN);
        expand.start();
        loop.setDuration(NOISE_ANIMATION_DURATION);
        loop.addListener(new AnimatorListener(this) { // from class: android.graphics.drawable.RippleAnimationSession.3
            @Override // android.graphics.drawable.RippleAnimationSession.AnimatorListener, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                RippleAnimationSession.this.mLoopAnimation = null;
            }
        });
        loop.setInterpolator(LINEAR_INTERPOLATOR);
        loop.start();
        Animator animator = this.mLoopAnimation;
        if (animator != null) {
            animator.cancel();
        }
        this.mLoopAnimation = loop;
    }

    private void enterSoftware() {
        final ValueAnimator expand = ValueAnimator.ofFloat(0.0f, 0.5f);
        expand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.graphics.drawable.RippleAnimationSession$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RippleAnimationSession.this.lambda$enterSoftware$1(expand, valueAnimator);
            }
        });
        long j = this.mStartTime;
        final ValueAnimator loop = ValueAnimator.ofFloat((float) j, (float) (j + 32));
        loop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.graphics.drawable.RippleAnimationSession$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RippleAnimationSession.this.lambda$enterSoftware$2(loop, valueAnimator);
            }
        });
        startAnimation(expand, loop);
        this.mCurrentAnimation = expand;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enterSoftware$1(ValueAnimator expand, ValueAnimator updatedAnimation) {
        notifyUpdate();
        this.mProperties.getShader().setProgress(((Float) expand.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enterSoftware$2(ValueAnimator loop, ValueAnimator updatedAnimation) {
        notifyUpdate();
        this.mProperties.getShader().setNoisePhase(((Float) loop.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRadius(float radius) {
        this.mProperties.setRadius(Float.valueOf(radius));
        this.mProperties.getShader().setRadius(radius);
        AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> animationProperties = this.mCanvasProperties;
        if (animationProperties != null) {
            animationProperties.setRadius(CanvasProperty.createFloat(radius));
            this.mCanvasProperties.getShader().setRadius(radius);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnimationProperties<Float, Paint> getProperties() {
        return this.mProperties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> getCanvasProperties() {
        if (this.mCanvasProperties == null) {
            this.mCanvasProperties = new AnimationProperties<>(CanvasProperty.createFloat(this.mProperties.getX().floatValue()), CanvasProperty.createFloat(this.mProperties.getY().floatValue()), CanvasProperty.createFloat(this.mProperties.getMaxRadius().floatValue()), CanvasProperty.createFloat(this.mProperties.getNoisePhase().floatValue()), CanvasProperty.createPaint(this.mProperties.getPaint()), CanvasProperty.createFloat(this.mProperties.getProgress().floatValue()), this.mProperties.getColor(), this.mProperties.getShader());
        }
        return this.mCanvasProperties;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AnimatorListener implements Animator.AnimatorListener {
        private final RippleAnimationSession mSession;

        AnimatorListener(RippleAnimationSession session) {
            this.mSession = session;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            this.mSession.onAnimationEnd(animation);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AnimationProperties<FloatType, PaintType> {
        private final int mColor;
        private FloatType mMaxRadius;
        private final FloatType mNoisePhase;
        private final PaintType mPaint;
        private final FloatType mProgress;
        private final RippleShader mShader;

        /* renamed from: mX */
        private FloatType f89mX;

        /* renamed from: mY */
        private FloatType f90mY;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AnimationProperties(FloatType x, FloatType y, FloatType maxRadius, FloatType noisePhase, PaintType paint, FloatType progress, int color, RippleShader shader) {
            this.f90mY = y;
            this.f89mX = x;
            this.mMaxRadius = maxRadius;
            this.mNoisePhase = noisePhase;
            this.mPaint = paint;
            this.mShader = shader;
            this.mProgress = progress;
            this.mColor = color;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public FloatType getProgress() {
            return this.mProgress;
        }

        void setRadius(FloatType radius) {
            this.mMaxRadius = radius;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setOrigin(FloatType x, FloatType y) {
            this.f89mX = x;
            this.f90mY = y;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public FloatType getX() {
            return this.f89mX;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public FloatType getY() {
            return this.f90mY;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public FloatType getMaxRadius() {
            return this.mMaxRadius;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public PaintType getPaint() {
            return this.mPaint;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public RippleShader getShader() {
            return this.mShader;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public FloatType getNoisePhase() {
            return this.mNoisePhase;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getColor() {
            return this.mColor;
        }
    }
}
