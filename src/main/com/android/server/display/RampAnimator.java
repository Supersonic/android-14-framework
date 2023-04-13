package com.android.server.display;

import android.animation.ValueAnimator;
import android.util.FloatProperty;
import android.view.Choreographer;
/* loaded from: classes.dex */
public class RampAnimator<T> {
    public float mAnimatedValue;
    public boolean mAnimating;
    public float mAnimationDecreaseMaxTimeSecs;
    public float mAnimationIncreaseMaxTimeSecs;
    public float mCurrentValue;
    public boolean mFirstTime = true;
    public long mLastFrameTimeNanos;
    public final T mObject;
    public final FloatProperty<T> mProperty;
    public float mRate;
    public float mTargetValue;

    /* loaded from: classes.dex */
    public interface Listener {
        void onAnimationEnd();
    }

    public RampAnimator(T t, FloatProperty<T> floatProperty) {
        this.mObject = t;
        this.mProperty = floatProperty;
    }

    public void setAnimationTimeLimits(long j, long j2) {
        this.mAnimationIncreaseMaxTimeSecs = j > 0 ? ((float) j) / 1000.0f : 0.0f;
        this.mAnimationDecreaseMaxTimeSecs = j2 > 0 ? ((float) j2) / 1000.0f : 0.0f;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0061  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean setAnimationTarget(float f, float f2) {
        float f3;
        float f4;
        boolean z;
        float convertLinearToGamma = BrightnessUtils.convertLinearToGamma(f);
        boolean z2 = this.mFirstTime;
        if (z2 || f2 <= 0.0f) {
            if (z2 || convertLinearToGamma != this.mCurrentValue) {
                this.mFirstTime = false;
                this.mRate = 0.0f;
                this.mTargetValue = convertLinearToGamma;
                this.mCurrentValue = convertLinearToGamma;
                setPropertyValue(convertLinearToGamma);
                this.mAnimating = false;
                return true;
            }
            return false;
        }
        float f5 = this.mCurrentValue;
        if (convertLinearToGamma > f5) {
            f3 = this.mAnimationIncreaseMaxTimeSecs;
            if (f3 > 0.0f && (convertLinearToGamma - f5) / f2 > f3) {
                f4 = convertLinearToGamma - f5;
                f2 = f4 / f3;
                z = this.mAnimating;
                if (z || f2 > this.mRate || ((convertLinearToGamma <= f5 && f5 <= this.mTargetValue) || (this.mTargetValue <= f5 && f5 <= convertLinearToGamma))) {
                    this.mRate = f2;
                }
                boolean z3 = this.mTargetValue != convertLinearToGamma;
                this.mTargetValue = convertLinearToGamma;
                if (!z && convertLinearToGamma != f5) {
                    this.mAnimating = true;
                    this.mAnimatedValue = f5;
                    this.mLastFrameTimeNanos = System.nanoTime();
                }
                return z3;
            }
        }
        if (convertLinearToGamma < f5) {
            f3 = this.mAnimationDecreaseMaxTimeSecs;
            if (f3 > 0.0f && (f5 - convertLinearToGamma) / f2 > f3) {
                f4 = f5 - convertLinearToGamma;
                f2 = f4 / f3;
            }
        }
        z = this.mAnimating;
        if (z) {
        }
        this.mRate = f2;
        if (this.mTargetValue != convertLinearToGamma) {
        }
        this.mTargetValue = convertLinearToGamma;
        if (!z) {
            this.mAnimating = true;
            this.mAnimatedValue = f5;
            this.mLastFrameTimeNanos = System.nanoTime();
        }
        return z3;
    }

    public boolean isAnimating() {
        return this.mAnimating;
    }

    public final void setPropertyValue(float f) {
        this.mProperty.setValue(this.mObject, BrightnessUtils.convertGammaToLinear(f));
    }

    public void performNextAnimationStep(long j) {
        float f = ((float) (j - this.mLastFrameTimeNanos)) * 1.0E-9f;
        this.mLastFrameTimeNanos = j;
        float durationScale = ValueAnimator.getDurationScale();
        if (durationScale == 0.0f) {
            this.mAnimatedValue = this.mTargetValue;
        } else {
            float f2 = (f * this.mRate) / durationScale;
            float f3 = this.mTargetValue;
            if (f3 > this.mCurrentValue) {
                this.mAnimatedValue = Math.min(this.mAnimatedValue + f2, f3);
            } else {
                this.mAnimatedValue = Math.max(this.mAnimatedValue - f2, f3);
            }
        }
        float f4 = this.mCurrentValue;
        float f5 = this.mAnimatedValue;
        this.mCurrentValue = f5;
        if (f4 != f5) {
            setPropertyValue(f5);
        }
        if (this.mTargetValue == this.mCurrentValue) {
            this.mAnimating = false;
        }
    }

    /* loaded from: classes.dex */
    public static class DualRampAnimator<T> {
        public boolean mAwaitingCallback;
        public final RampAnimator<T> mFirst;
        public Listener mListener;
        public final RampAnimator<T> mSecond;
        public final Runnable mAnimationCallback = new Runnable() { // from class: com.android.server.display.RampAnimator.DualRampAnimator.1
            @Override // java.lang.Runnable
            public void run() {
                long frameTimeNanos = DualRampAnimator.this.mChoreographer.getFrameTimeNanos();
                DualRampAnimator.this.mFirst.performNextAnimationStep(frameTimeNanos);
                DualRampAnimator.this.mSecond.performNextAnimationStep(frameTimeNanos);
                if (DualRampAnimator.this.isAnimating()) {
                    DualRampAnimator.this.postAnimationCallback();
                    return;
                }
                if (DualRampAnimator.this.mListener != null) {
                    DualRampAnimator.this.mListener.onAnimationEnd();
                }
                DualRampAnimator.this.mAwaitingCallback = false;
            }
        };
        public final Choreographer mChoreographer = Choreographer.getInstance();

        public DualRampAnimator(T t, FloatProperty<T> floatProperty, FloatProperty<T> floatProperty2) {
            this.mFirst = new RampAnimator<>(t, floatProperty);
            this.mSecond = new RampAnimator<>(t, floatProperty2);
        }

        public void setAnimationTimeLimits(long j, long j2) {
            this.mFirst.setAnimationTimeLimits(j, j2);
            this.mSecond.setAnimationTimeLimits(j, j2);
        }

        public boolean animateTo(float f, float f2, float f3) {
            boolean animationTarget = this.mFirst.setAnimationTarget(f, f3) | this.mSecond.setAnimationTarget(f2, f3);
            boolean isAnimating = isAnimating();
            boolean z = this.mAwaitingCallback;
            if (isAnimating != z) {
                if (isAnimating) {
                    this.mAwaitingCallback = true;
                    postAnimationCallback();
                } else if (z) {
                    this.mChoreographer.removeCallbacks(1, this.mAnimationCallback, null);
                    this.mAwaitingCallback = false;
                }
            }
            return animationTarget;
        }

        public void setListener(Listener listener) {
            this.mListener = listener;
        }

        public boolean isAnimating() {
            return this.mFirst.isAnimating() || this.mSecond.isAnimating();
        }

        public final void postAnimationCallback() {
            this.mChoreographer.postCallback(1, this.mAnimationCallback, null);
        }
    }
}
