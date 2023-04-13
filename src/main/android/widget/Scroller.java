package android.widget;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
/* loaded from: classes4.dex */
public class Scroller {
    private static final int DEFAULT_DURATION = 250;
    private static final float END_TENSION = 1.0f;
    private static final int FLING_MODE = 1;
    private static final float INFLEXION = 0.35f;
    private static final int NB_SAMPLES = 100;

    /* renamed from: P1 */
    private static final float f538P1 = 0.175f;

    /* renamed from: P2 */
    private static final float f539P2 = 0.35000002f;
    private static final int SCROLL_MODE = 0;
    private static final float START_TENSION = 0.5f;
    private float mCurrVelocity;
    private int mCurrX;
    private int mCurrY;
    private float mDeceleration;
    private float mDeltaX;
    private float mDeltaY;
    private int mDistance;
    private int mDuration;
    private float mDurationReciprocal;
    private int mFinalX;
    private int mFinalY;
    private boolean mFinished;
    private float mFlingFriction;
    private boolean mFlywheel;
    private final Interpolator mInterpolator;
    private int mMaxX;
    private int mMaxY;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private float mPhysicalCoeff;
    private final float mPpi;
    private long mStartTime;
    private int mStartX;
    private int mStartY;
    private float mVelocity;
    private static float DECELERATION_RATE = (float) (Math.log(0.78d) / Math.log(0.9d));
    private static final float[] SPLINE_POSITION = new float[101];
    private static final float[] SPLINE_TIME = new float[101];

    static {
        float f;
        float x;
        float f2;
        float coef;
        float y;
        float coef2;
        float x_min = 0.0f;
        float y_min = 0.0f;
        for (int i = 0; i < 100; i++) {
            float alpha = i / 100.0f;
            float x_max = 1.0f;
            while (true) {
                f = 2.0f;
                x = ((x_max - x_min) / 2.0f) + x_min;
                f2 = 3.0f;
                coef = x * 3.0f * (1.0f - x);
                float tx = ((((1.0f - x) * f538P1) + (x * f539P2)) * coef) + (x * x * x);
                if (Math.abs(tx - alpha) < 1.0E-5d) {
                    break;
                } else if (tx > alpha) {
                    x_max = x;
                } else {
                    x_min = x;
                }
            }
            SPLINE_POSITION[i] = ((((1.0f - x) * 0.5f) + x) * coef) + (x * x * x);
            float y_max = 1.0f;
            while (true) {
                y = ((y_max - y_min) / f) + y_min;
                coef2 = y * f2 * (1.0f - y);
                float dy = ((((1.0f - y) * 0.5f) + y) * coef2) + (y * y * y);
                if (Math.abs(dy - alpha) < 1.0E-5d) {
                    break;
                } else if (dy > alpha) {
                    y_max = y;
                    f = 2.0f;
                    f2 = 3.0f;
                } else {
                    y_min = y;
                    f = 2.0f;
                    f2 = 3.0f;
                }
            }
            SPLINE_TIME[i] = (coef2 * (((1.0f - y) * f538P1) + (f539P2 * y))) + (y * y * y);
        }
        float[] fArr = SPLINE_POSITION;
        SPLINE_TIME[100] = 1.0f;
        fArr[100] = 1.0f;
    }

    public Scroller(Context context) {
        this(context, null);
    }

    public Scroller(Context context, Interpolator interpolator) {
        this(context, interpolator, context.getApplicationInfo().targetSdkVersion >= 11);
    }

    public Scroller(Context context, Interpolator interpolator, boolean flywheel) {
        this.mFlingFriction = ViewConfiguration.getScrollFriction();
        this.mFinished = true;
        if (interpolator == null) {
            this.mInterpolator = new ViscousFluidInterpolator();
        } else {
            this.mInterpolator = interpolator;
        }
        this.mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = flywheel;
        this.mPhysicalCoeff = computeDeceleration(0.84f);
    }

    public final void setFriction(float friction) {
        this.mDeceleration = computeDeceleration(friction);
        this.mFlingFriction = friction;
    }

    private float computeDeceleration(float friction) {
        return this.mPpi * 386.0878f * friction;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceFinished(boolean finished) {
        this.mFinished = finished;
    }

    public final int getDuration() {
        return this.mDuration;
    }

    public final int getCurrX() {
        return this.mCurrX;
    }

    public final int getCurrY() {
        return this.mCurrY;
    }

    public float getCurrVelocity() {
        return this.mMode == 1 ? this.mCurrVelocity : this.mVelocity - ((this.mDeceleration * timePassed()) / 2000.0f);
    }

    public final int getStartX() {
        return this.mStartX;
    }

    public final int getStartY() {
        return this.mStartY;
    }

    public final int getFinalX() {
        return this.mFinalX;
    }

    public final int getFinalY() {
        return this.mFinalY;
    }

    public boolean computeScrollOffset() {
        if (this.mFinished) {
            return false;
        }
        int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        int i = this.mDuration;
        if (timePassed < i) {
            switch (this.mMode) {
                case 0:
                    float x = this.mInterpolator.getInterpolation(timePassed * this.mDurationReciprocal);
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * x);
                    this.mCurrY = this.mStartY + Math.round(this.mDeltaY * x);
                    break;
                case 1:
                    float t = timePassed / i;
                    int index = (int) (t * 100.0f);
                    float distanceCoef = 1.0f;
                    float velocityCoef = 0.0f;
                    if (index < 100) {
                        float t_inf = index / 100.0f;
                        float t_sup = (index + 1) / 100.0f;
                        float[] fArr = SPLINE_POSITION;
                        float d_inf = fArr[index];
                        float d_sup = fArr[index + 1];
                        velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
                        distanceCoef = d_inf + ((t - t_inf) * velocityCoef);
                    }
                    this.mCurrVelocity = ((this.mDistance * velocityCoef) / i) * 1000.0f;
                    int i2 = this.mStartX;
                    int round = i2 + Math.round((this.mFinalX - i2) * distanceCoef);
                    this.mCurrX = round;
                    int min = Math.min(round, this.mMaxX);
                    this.mCurrX = min;
                    this.mCurrX = Math.max(min, this.mMinX);
                    int i3 = this.mStartY;
                    int round2 = i3 + Math.round((this.mFinalY - i3) * distanceCoef);
                    this.mCurrY = round2;
                    int min2 = Math.min(round2, this.mMaxY);
                    this.mCurrY = min2;
                    int max = Math.max(min2, this.mMinY);
                    this.mCurrY = max;
                    if (this.mCurrX == this.mFinalX && max == this.mFinalY) {
                        this.mFinished = true;
                        break;
                    }
                    break;
            }
        } else {
            this.mCurrX = this.mFinalX;
            this.mCurrY = this.mFinalY;
            this.mFinished = true;
        }
        return true;
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = duration;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        this.mFinalX = startX + dx;
        this.mFinalY = startY + dy;
        this.mDeltaX = dx;
        this.mDeltaY = dy;
        this.mDurationReciprocal = 1.0f / this.mDuration;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        int velocityX2 = velocityX;
        int velocityY2 = velocityY;
        if (this.mFlywheel && !this.mFinished) {
            float oldVel = getCurrVelocity();
            float dx = this.mFinalX - this.mStartX;
            float dy = this.mFinalY - this.mStartY;
            float hyp = (float) Math.hypot(dx, dy);
            float ndx = dx / hyp;
            float ndy = dy / hyp;
            float oldVelocityX = ndx * oldVel;
            float oldVelocityY = ndy * oldVel;
            if (Math.signum(velocityX2) == Math.signum(oldVelocityX) && Math.signum(velocityY2) == Math.signum(oldVelocityY)) {
                velocityX2 = (int) (velocityX2 + oldVelocityX);
                velocityY2 = (int) (velocityY2 + oldVelocityY);
            }
        }
        this.mMode = 1;
        this.mFinished = false;
        float velocity = (float) Math.hypot(velocityX2, velocityY2);
        this.mVelocity = velocity;
        this.mDuration = getSplineFlingDuration(velocity);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        float coeffX = velocity == 0.0f ? 1.0f : velocityX2 / velocity;
        float coeffY = velocity != 0.0f ? velocityY2 / velocity : 1.0f;
        double totalDistance = getSplineFlingDistance(velocity);
        this.mDistance = (int) (Math.signum(velocity) * totalDistance);
        this.mMinX = minX;
        this.mMaxX = maxX;
        this.mMinY = minY;
        this.mMaxY = maxY;
        int round = ((int) Math.round(coeffX * totalDistance)) + startX;
        this.mFinalX = round;
        int min = Math.min(round, this.mMaxX);
        this.mFinalX = min;
        this.mFinalX = Math.max(min, this.mMinX);
        int round2 = ((int) Math.round(coeffY * totalDistance)) + startY;
        this.mFinalY = round2;
        int min2 = Math.min(round2, this.mMaxY);
        this.mFinalY = min2;
        this.mFinalY = Math.max(min2, this.mMinY);
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log((Math.abs(velocity) * INFLEXION) / (this.mFlingFriction * this.mPhysicalCoeff));
    }

    private int getSplineFlingDuration(float velocity) {
        double l = getSplineDeceleration(velocity);
        double decelMinusOne = DECELERATION_RATE - 1.0d;
        return (int) (Math.exp(l / decelMinusOne) * 1000.0d);
    }

    private double getSplineFlingDistance(float velocity) {
        double l = getSplineDeceleration(velocity);
        float f = DECELERATION_RATE;
        double decelMinusOne = f - 1.0d;
        return this.mFlingFriction * this.mPhysicalCoeff * Math.exp((f / decelMinusOne) * l);
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public void extendDuration(int extend) {
        int passed = timePassed();
        int i = passed + extend;
        this.mDuration = i;
        this.mDurationReciprocal = 1.0f / i;
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int newX) {
        this.mFinalX = newX;
        this.mDeltaX = newX - this.mStartX;
        this.mFinished = false;
    }

    public void setFinalY(int newY) {
        this.mFinalY = newY;
        this.mDeltaY = newY - this.mStartY;
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        return !this.mFinished && Math.signum(xvel) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(yvel) == Math.signum((float) (this.mFinalY - this.mStartY));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ViscousFluidInterpolator implements Interpolator {
        private static final float VISCOUS_FLUID_NORMALIZE;
        private static final float VISCOUS_FLUID_OFFSET;
        private static final float VISCOUS_FLUID_SCALE = 8.0f;

        static {
            float viscousFluid = 1.0f / viscousFluid(1.0f);
            VISCOUS_FLUID_NORMALIZE = viscousFluid;
            VISCOUS_FLUID_OFFSET = 1.0f - (viscousFluid * viscousFluid(1.0f));
        }

        private static float viscousFluid(float x) {
            float x2 = x * VISCOUS_FLUID_SCALE;
            if (x2 < 1.0f) {
                return x2 - (1.0f - ((float) Math.exp(-x2)));
            }
            return 0.36787945f + ((1.0f - 0.36787945f) * (1.0f - ((float) Math.exp(1.0f - x2))));
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
            if (interpolated > 0.0f) {
                return VISCOUS_FLUID_OFFSET + interpolated;
            }
            return interpolated;
        }
    }
}
