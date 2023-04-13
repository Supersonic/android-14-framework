package android.widget;

import android.content.Context;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;
/* loaded from: classes4.dex */
public class OverScroller {
    private static final int DEFAULT_DURATION = 250;
    private static final int FLING_MODE = 1;
    private static final int SCROLL_MODE = 0;
    private final boolean mFlywheel;
    private Interpolator mInterpolator;
    private int mMode;
    private final SplineOverScroller mScrollerX;
    private final SplineOverScroller mScrollerY;

    public OverScroller(Context context) {
        this(context, null);
    }

    public OverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public OverScroller(Context context, Interpolator interpolator, boolean flywheel) {
        if (interpolator == null) {
            this.mInterpolator = new Scroller.ViscousFluidInterpolator();
        } else {
            this.mInterpolator = interpolator;
        }
        this.mFlywheel = flywheel;
        this.mScrollerX = new SplineOverScroller(context);
        this.mScrollerY = new SplineOverScroller(context);
    }

    @Deprecated
    public OverScroller(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY) {
        this(context, interpolator, true);
    }

    @Deprecated
    public OverScroller(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
        this(context, interpolator, flywheel);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            this.mInterpolator = new Scroller.ViscousFluidInterpolator();
        } else {
            this.mInterpolator = interpolator;
        }
    }

    public final void setFriction(float friction) {
        this.mScrollerX.setFriction(friction);
        this.mScrollerY.setFriction(friction);
    }

    public final boolean isFinished() {
        return this.mScrollerX.mFinished && this.mScrollerY.mFinished;
    }

    public final void forceFinished(boolean finished) {
        SplineOverScroller splineOverScroller = this.mScrollerX;
        this.mScrollerY.mFinished = finished;
        splineOverScroller.mFinished = finished;
    }

    public final int getCurrX() {
        return this.mScrollerX.mCurrentPosition;
    }

    public final int getCurrY() {
        return this.mScrollerY.mCurrentPosition;
    }

    public float getCurrVelocity() {
        return (float) Math.hypot(this.mScrollerX.mCurrVelocity, this.mScrollerY.mCurrVelocity);
    }

    public final int getStartX() {
        return this.mScrollerX.mStart;
    }

    public final int getStartY() {
        return this.mScrollerY.mStart;
    }

    public final int getFinalX() {
        return this.mScrollerX.mFinal;
    }

    public final int getFinalY() {
        return this.mScrollerY.mFinal;
    }

    public final int getDuration() {
        return Math.max(this.mScrollerX.mDuration, this.mScrollerY.mDuration);
    }

    public void extendDuration(int extend) {
        this.mScrollerX.extendDuration(extend);
        this.mScrollerY.extendDuration(extend);
    }

    public void setFinalX(int newX) {
        this.mScrollerX.setFinalPosition(newX);
    }

    public void setFinalY(int newY) {
        this.mScrollerY.setFinalPosition(newY);
    }

    public boolean computeScrollOffset() {
        if (isFinished()) {
            return false;
        }
        switch (this.mMode) {
            case 0:
                long time = AnimationUtils.currentAnimationTimeMillis();
                long elapsedTime = time - this.mScrollerX.mStartTime;
                int duration = this.mScrollerX.mDuration;
                if (elapsedTime < duration) {
                    float q = this.mInterpolator.getInterpolation(((float) elapsedTime) / duration);
                    this.mScrollerX.updateScroll(q);
                    this.mScrollerY.updateScroll(q);
                    return true;
                }
                abortAnimation();
                return true;
            case 1:
                if (!this.mScrollerX.mFinished && !this.mScrollerX.update() && !this.mScrollerX.continueWhenFinished()) {
                    this.mScrollerX.finish();
                }
                if (!this.mScrollerY.mFinished && !this.mScrollerY.update() && !this.mScrollerY.continueWhenFinished()) {
                    this.mScrollerY.finish();
                    return true;
                }
                return true;
            default:
                return true;
        }
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mScrollerX.startScroll(startX, dx, duration);
        this.mScrollerY.startScroll(startY, dy, duration);
    }

    public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        this.mMode = 1;
        boolean spingbackX = this.mScrollerX.springback(startX, minX, maxX);
        boolean spingbackY = this.mScrollerY.springback(startY, minY, maxY);
        return spingbackX || spingbackY;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
        int velocityY2;
        int velocityX2 = velocityX;
        if (this.mFlywheel && !isFinished()) {
            float oldVelocityX = this.mScrollerX.mCurrVelocity;
            float oldVelocityY = this.mScrollerY.mCurrVelocity;
            if (Math.signum(velocityX2) == Math.signum(oldVelocityX) && Math.signum(velocityY) == Math.signum(oldVelocityY)) {
                velocityX2 = (int) (velocityX2 + oldVelocityX);
                velocityY2 = (int) (velocityY + oldVelocityY);
                this.mMode = 1;
                this.mScrollerX.fling(startX, velocityX2, minX, maxX, overX);
                this.mScrollerY.fling(startY, velocityY2, minY, maxY, overY);
            }
        }
        velocityY2 = velocityY;
        this.mMode = 1;
        this.mScrollerX.fling(startX, velocityX2, minX, maxX, overX);
        this.mScrollerY.fling(startY, velocityY2, minY, maxY, overY);
    }

    public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {
        this.mScrollerX.notifyEdgeReached(startX, finalX, overX);
    }

    public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
        this.mScrollerY.notifyEdgeReached(startY, finalY, overY);
    }

    public boolean isOverScrolled() {
        return ((this.mScrollerX.mFinished || this.mScrollerX.mState == 0) && (this.mScrollerY.mFinished || this.mScrollerY.mState == 0)) ? false : true;
    }

    public void abortAnimation() {
        this.mScrollerX.finish();
        this.mScrollerY.finish();
    }

    public int timePassed() {
        long time = AnimationUtils.currentAnimationTimeMillis();
        long startTime = Math.min(this.mScrollerX.mStartTime, this.mScrollerY.mStartTime);
        return (int) (time - startTime);
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        int dx = this.mScrollerX.mFinal - this.mScrollerX.mStart;
        int dy = this.mScrollerY.mFinal - this.mScrollerY.mStart;
        return !isFinished() && Math.signum(xvel) == Math.signum((float) dx) && Math.signum(yvel) == Math.signum((float) dy);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public double getSplineFlingDistance(int velocity) {
        return this.mScrollerY.getSplineFlingDistance(velocity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SplineOverScroller {
        private static final int BALLISTIC = 2;
        private static final int CUBIC = 1;
        private static final float END_TENSION = 1.0f;
        private static final float GRAVITY = 2000.0f;
        private static final float INFLEXION = 0.35f;
        private static final int NB_SAMPLES = 100;

        /* renamed from: P1 */
        private static final float f525P1 = 0.175f;

        /* renamed from: P2 */
        private static final float f526P2 = 0.35000002f;
        private static final int SPLINE = 0;
        private static final float START_TENSION = 0.5f;
        private float mCurrVelocity;
        private int mCurrentPosition;
        private float mDeceleration;
        private int mDuration;
        private int mFinal;
        private int mOver;
        private float mPhysicalCoeff;
        private int mSplineDistance;
        private int mSplineDuration;
        private int mStart;
        private long mStartTime;
        private int mVelocity;
        private static float DECELERATION_RATE = (float) (Math.log(0.78d) / Math.log(0.9d));
        private static final float[] SPLINE_POSITION = new float[101];
        private static final float[] SPLINE_TIME = new float[101];
        private float mFlingFriction = ViewConfiguration.getScrollFriction();
        private int mState = 0;
        private boolean mFinished = true;

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
                    float tx = ((((1.0f - x) * f525P1) + (x * f526P2)) * coef) + (x * x * x);
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
                SPLINE_TIME[i] = (coef2 * (((1.0f - y) * f525P1) + (f526P2 * y))) + (y * y * y);
            }
            float[] fArr = SPLINE_POSITION;
            SPLINE_TIME[100] = 1.0f;
            fArr[100] = 1.0f;
        }

        void setFriction(float friction) {
            this.mFlingFriction = friction;
        }

        SplineOverScroller(Context context) {
            float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
            this.mPhysicalCoeff = 386.0878f * ppi * 0.84f;
        }

        void updateScroll(float q) {
            int i = this.mStart;
            this.mCurrentPosition = i + Math.round((this.mFinal - i) * q);
        }

        private static float getDeceleration(int velocity) {
            if (velocity > 0) {
                return -2000.0f;
            }
            return GRAVITY;
        }

        private void adjustDuration(int start, int oldFinal, int newFinal) {
            int oldDistance = oldFinal - start;
            int newDistance = newFinal - start;
            float x = Math.abs(newDistance / oldDistance);
            int index = (int) (x * 100.0f);
            if (index < 100) {
                float x_inf = index / 100.0f;
                float x_sup = (index + 1) / 100.0f;
                float[] fArr = SPLINE_TIME;
                float t_inf = fArr[index];
                float t_sup = fArr[index + 1];
                float timeCoef = (((x - x_inf) / (x_sup - x_inf)) * (t_sup - t_inf)) + t_inf;
                this.mDuration = (int) (this.mDuration * timeCoef);
            }
        }

        void startScroll(int start, int distance, int duration) {
            this.mFinished = false;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mFinal = start + distance;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = duration;
            this.mDeceleration = 0.0f;
            this.mVelocity = 0;
        }

        void finish() {
            this.mCurrentPosition = this.mFinal;
            this.mFinished = true;
        }

        void setFinalPosition(int position) {
            this.mFinal = position;
            this.mSplineDistance = position - this.mStart;
            this.mFinished = false;
        }

        void extendDuration(int extend) {
            long time = AnimationUtils.currentAnimationTimeMillis();
            int elapsedTime = (int) (time - this.mStartTime);
            int i = elapsedTime + extend;
            this.mSplineDuration = i;
            this.mDuration = i;
            this.mFinished = false;
        }

        boolean springback(int start, int min, int max) {
            this.mFinished = true;
            this.mFinal = start;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mVelocity = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 0;
            if (start < min) {
                startSpringback(start, min, 0);
            } else if (start > max) {
                startSpringback(start, max, 0);
            }
            return true ^ this.mFinished;
        }

        private void startSpringback(int start, int end, int velocity) {
            this.mFinished = false;
            this.mState = 1;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mFinal = end;
            int delta = start - end;
            this.mDeceleration = getDeceleration(delta);
            this.mVelocity = -delta;
            this.mOver = Math.abs(delta);
            this.mDuration = (int) (Math.sqrt((delta * (-2.0d)) / this.mDeceleration) * 1000.0d);
        }

        void fling(int start, int velocity, int min, int max, int over) {
            this.mOver = over;
            this.mFinished = false;
            this.mVelocity = velocity;
            this.mCurrVelocity = velocity;
            this.mSplineDuration = 0;
            this.mDuration = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStart = start;
            this.mCurrentPosition = start;
            if (start > max || start < min) {
                startAfterEdge(start, min, max, velocity);
                return;
            }
            this.mState = 0;
            double totalDistance = 0.0d;
            if (velocity != 0) {
                int splineFlingDuration = getSplineFlingDuration(velocity);
                this.mSplineDuration = splineFlingDuration;
                this.mDuration = splineFlingDuration;
                totalDistance = getSplineFlingDistance(velocity);
            }
            int signum = (int) (Math.signum(velocity) * totalDistance);
            this.mSplineDistance = signum;
            int i = signum + start;
            this.mFinal = i;
            if (i < min) {
                adjustDuration(this.mStart, i, min);
                this.mFinal = min;
            }
            int i2 = this.mFinal;
            if (i2 > max) {
                adjustDuration(this.mStart, i2, max);
                this.mFinal = max;
            }
        }

        private double getSplineDeceleration(int velocity) {
            return Math.log((Math.abs(velocity) * INFLEXION) / (this.mFlingFriction * this.mPhysicalCoeff));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public double getSplineFlingDistance(int velocity) {
            double l = getSplineDeceleration(velocity);
            float f = DECELERATION_RATE;
            double decelMinusOne = f - 1.0d;
            return this.mFlingFriction * this.mPhysicalCoeff * Math.exp((f / decelMinusOne) * l);
        }

        private int getSplineFlingDuration(int velocity) {
            double l = getSplineDeceleration(velocity);
            double decelMinusOne = DECELERATION_RATE - 1.0d;
            return (int) (Math.exp(l / decelMinusOne) * 1000.0d);
        }

        private void fitOnBounceCurve(int start, int end, int velocity) {
            float f = this.mDeceleration;
            float durationToApex = (-velocity) / f;
            float velocitySquared = velocity * velocity;
            float distanceToApex = (velocitySquared / 2.0f) / Math.abs(f);
            float distanceToEdge = Math.abs(end - start);
            float totalDuration = (float) Math.sqrt(((distanceToApex + distanceToEdge) * 2.0d) / Math.abs(this.mDeceleration));
            this.mStartTime -= (int) ((totalDuration - durationToApex) * 1000.0f);
            this.mStart = end;
            this.mCurrentPosition = end;
            this.mVelocity = (int) ((-this.mDeceleration) * totalDuration);
        }

        private void startBounceAfterEdge(int start, int end, int velocity) {
            this.mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
            fitOnBounceCurve(start, end, velocity);
            onEdgeReached();
        }

        private void startAfterEdge(int start, int min, int max, int velocity) {
            if (start > min && start < max) {
                Log.m110e("OverScroller", "startAfterEdge called from a valid position");
                this.mFinished = true;
                return;
            }
            boolean positive = start > max;
            int edge = positive ? max : min;
            int overDistance = start - edge;
            boolean keepIncreasing = overDistance * velocity >= 0;
            if (keepIncreasing) {
                startBounceAfterEdge(start, edge, velocity);
                return;
            }
            double totalDistance = getSplineFlingDistance(velocity);
            if (totalDistance > Math.abs(overDistance)) {
                fling(start, velocity, positive ? min : start, positive ? start : max, this.mOver);
            } else {
                startSpringback(start, edge, velocity);
            }
        }

        void notifyEdgeReached(int start, int end, int over) {
            if (this.mState == 0) {
                this.mOver = over;
                this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                startAfterEdge(start, end, end, (int) this.mCurrVelocity);
            }
        }

        private void onEdgeReached() {
            int i = this.mVelocity;
            float velocitySquared = i * i;
            float distance = velocitySquared / (Math.abs(this.mDeceleration) * 2.0f);
            float sign = Math.signum(this.mVelocity);
            int i2 = this.mOver;
            if (distance > i2) {
                this.mDeceleration = ((-sign) * velocitySquared) / (i2 * 2.0f);
                distance = i2;
            }
            this.mOver = (int) distance;
            this.mState = 2;
            int i3 = this.mStart;
            int i4 = this.mVelocity;
            this.mFinal = i3 + ((int) (i4 > 0 ? distance : -distance));
            this.mDuration = -((int) ((i4 * 1000.0f) / this.mDeceleration));
        }

        boolean continueWhenFinished() {
            switch (this.mState) {
                case 0:
                    if (this.mDuration < this.mSplineDuration) {
                        int i = this.mFinal;
                        this.mStart = i;
                        this.mCurrentPosition = i;
                        int i2 = (int) this.mCurrVelocity;
                        this.mVelocity = i2;
                        this.mDeceleration = getDeceleration(i2);
                        this.mStartTime += this.mDuration;
                        onEdgeReached();
                        break;
                    } else {
                        return false;
                    }
                case 1:
                    return false;
                case 2:
                    this.mStartTime += this.mDuration;
                    startSpringback(this.mFinal, this.mStart, 0);
                    break;
            }
            update();
            return true;
        }

        boolean update() {
            long time = AnimationUtils.currentAnimationTimeMillis();
            long currentTime = time - this.mStartTime;
            if (currentTime == 0) {
                return this.mDuration > 0;
            }
            int i = this.mDuration;
            if (currentTime > i) {
                return false;
            }
            double distance = 0.0d;
            switch (this.mState) {
                case 0:
                    int i2 = this.mSplineDuration;
                    float t = ((float) currentTime) / i2;
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
                    int i3 = this.mSplineDistance;
                    distance = i3 * distanceCoef;
                    this.mCurrVelocity = ((i3 * velocityCoef) / i2) * 1000.0f;
                    break;
                case 1:
                    float t2 = ((float) currentTime) / i;
                    float t22 = t2 * t2;
                    float sign = Math.signum(this.mVelocity);
                    int i4 = this.mOver;
                    distance = i4 * sign * ((3.0f * t22) - ((2.0f * t2) * t22));
                    this.mCurrVelocity = i4 * sign * 6.0f * ((-t2) + t22);
                    break;
                case 2:
                    float t3 = ((float) currentTime) / 1000.0f;
                    int i5 = this.mVelocity;
                    float f = this.mDeceleration;
                    this.mCurrVelocity = i5 + (f * t3);
                    distance = (i5 * t3) + (((f * t3) * t3) / 2.0f);
                    break;
            }
            this.mCurrentPosition = this.mStart + ((int) Math.round(distance));
            return true;
        }
    }
}
