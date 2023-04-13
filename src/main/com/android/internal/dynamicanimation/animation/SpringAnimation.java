package com.android.internal.dynamicanimation.animation;

import android.util.AndroidRuntimeException;
import android.util.FloatProperty;
import com.android.internal.dynamicanimation.animation.DynamicAnimation;
/* loaded from: classes4.dex */
public final class SpringAnimation extends DynamicAnimation<SpringAnimation> {
    private static final float UNSET = Float.MAX_VALUE;
    private boolean mEndRequested;
    private float mPendingPosition;
    private SpringForce mSpring;

    public SpringAnimation(FloatValueHolder floatValueHolder) {
        super(floatValueHolder);
        this.mSpring = null;
        this.mPendingPosition = Float.MAX_VALUE;
        this.mEndRequested = false;
    }

    public SpringAnimation(FloatValueHolder floatValueHolder, float finalPosition) {
        super(floatValueHolder);
        this.mSpring = null;
        this.mPendingPosition = Float.MAX_VALUE;
        this.mEndRequested = false;
        this.mSpring = new SpringForce(finalPosition);
    }

    public <K> SpringAnimation(K object, FloatProperty<K> property) {
        super(object, property);
        this.mSpring = null;
        this.mPendingPosition = Float.MAX_VALUE;
        this.mEndRequested = false;
    }

    public <K> SpringAnimation(K object, FloatProperty<K> property, float finalPosition) {
        super(object, property);
        this.mSpring = null;
        this.mPendingPosition = Float.MAX_VALUE;
        this.mEndRequested = false;
        this.mSpring = new SpringForce(finalPosition);
    }

    public SpringForce getSpring() {
        return this.mSpring;
    }

    public SpringAnimation setSpring(SpringForce force) {
        this.mSpring = force;
        return this;
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    public void start() {
        sanityCheck();
        this.mSpring.setValueThreshold(getValueThreshold());
        super.start();
    }

    public void animateToFinalPosition(float finalPosition) {
        if (isRunning()) {
            this.mPendingPosition = finalPosition;
            return;
        }
        if (this.mSpring == null) {
            this.mSpring = new SpringForce(finalPosition);
        }
        this.mSpring.setFinalPosition(finalPosition);
        start();
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    public void cancel() {
        super.cancel();
        float f = this.mPendingPosition;
        if (f != Float.MAX_VALUE) {
            SpringForce springForce = this.mSpring;
            if (springForce == null) {
                this.mSpring = new SpringForce(f);
            } else {
                springForce.setFinalPosition(f);
            }
            this.mPendingPosition = Float.MAX_VALUE;
        }
    }

    public void skipToEnd() {
        if (!canSkipToEnd()) {
            throw new UnsupportedOperationException("Spring animations can only come to an end when there is damping");
        }
        if (!isCurrentThread()) {
            throw new AndroidRuntimeException("Animations may only be started on the same thread as the animation handler");
        }
        if (this.mRunning) {
            this.mEndRequested = true;
        }
    }

    public boolean canSkipToEnd() {
        return this.mSpring.mDampingRatio > 0.0d;
    }

    private void sanityCheck() {
        SpringForce springForce = this.mSpring;
        if (springForce == null) {
            throw new UnsupportedOperationException("Incomplete SpringAnimation: Either final position or a spring force needs to be set.");
        }
        double finalPosition = springForce.getFinalPosition();
        if (finalPosition > this.mMaxValue) {
            throw new UnsupportedOperationException("Final position of the spring cannot be greater than the max value.");
        }
        if (finalPosition < this.mMinValue) {
            throw new UnsupportedOperationException("Final position of the spring cannot be less than the min value.");
        }
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    boolean updateValueAndVelocity(long deltaT) {
        if (this.mEndRequested) {
            float f = this.mPendingPosition;
            if (f != Float.MAX_VALUE) {
                this.mSpring.setFinalPosition(f);
                this.mPendingPosition = Float.MAX_VALUE;
            }
            this.mValue = this.mSpring.getFinalPosition();
            this.mVelocity = 0.0f;
            this.mEndRequested = false;
            return true;
        }
        if (this.mPendingPosition != Float.MAX_VALUE) {
            DynamicAnimation.MassState massState = this.mSpring.updateValues(this.mValue, this.mVelocity, deltaT / 2);
            this.mSpring.setFinalPosition(this.mPendingPosition);
            this.mPendingPosition = Float.MAX_VALUE;
            DynamicAnimation.MassState massState2 = this.mSpring.updateValues(massState.mValue, massState.mVelocity, deltaT / 2);
            this.mValue = massState2.mValue;
            this.mVelocity = massState2.mVelocity;
        } else {
            DynamicAnimation.MassState massState3 = this.mSpring.updateValues(this.mValue, this.mVelocity, deltaT);
            this.mValue = massState3.mValue;
            this.mVelocity = massState3.mVelocity;
        }
        this.mValue = Math.max(this.mValue, this.mMinValue);
        this.mValue = Math.min(this.mValue, this.mMaxValue);
        if (isAtEquilibrium(this.mValue, this.mVelocity)) {
            this.mValue = this.mSpring.getFinalPosition();
            this.mVelocity = 0.0f;
            return true;
        }
        return false;
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    float getAcceleration(float value, float velocity) {
        return this.mSpring.getAcceleration(value, velocity);
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    boolean isAtEquilibrium(float value, float velocity) {
        return this.mSpring.isAtEquilibrium(value, velocity);
    }

    @Override // com.android.internal.dynamicanimation.animation.DynamicAnimation
    void setValueThreshold(float threshold) {
    }
}
