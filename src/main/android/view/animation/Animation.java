package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.p008os.Handler;
import android.p008os.SystemProperties;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.C4057R;
import dalvik.system.CloseGuard;
/* loaded from: classes4.dex */
public abstract class Animation implements Cloneable {
    public static final int ABSOLUTE = 0;
    public static final int INFINITE = -1;
    public static final int RELATIVE_TO_PARENT = 2;
    public static final int RELATIVE_TO_SELF = 1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int START_ON_FIRST_FRAME = -1;
    public static final int ZORDER_BOTTOM = -1;
    public static final int ZORDER_NORMAL = 0;
    public static final int ZORDER_TOP = 1;
    private int mBackdropColor;
    long mDuration;
    private boolean mHasRoundedCorners;
    Interpolator mInterpolator;
    private AnimationListener mListener;
    private Handler mListenerHandler;
    private Runnable mOnEnd;
    private Runnable mOnRepeat;
    private Runnable mOnStart;
    private boolean mShowBackdrop;
    private boolean mShowWallpaper;
    long mStartOffset;
    private int mZAdjustment;
    boolean mEnded = false;
    boolean mStarted = false;
    boolean mCycleFlip = false;
    boolean mInitialized = false;
    boolean mFillBefore = true;
    boolean mFillAfter = false;
    boolean mFillEnabled = false;
    long mStartTime = -1;
    int mRepeatCount = 0;
    int mRepeated = 0;
    int mRepeatMode = 1;
    private float mScaleFactor = 1.0f;
    private boolean mMore = true;
    private boolean mOneMoreTime = true;
    RectF mPreviousRegion = new RectF();
    RectF mRegion = new RectF();
    Transformation mTransformation = new Transformation();
    Transformation mPreviousTransformation = new Transformation();
    private final CloseGuard guard = CloseGuard.get();

    /* loaded from: classes4.dex */
    public interface AnimationListener {
        void onAnimationEnd(Animation animation);

        void onAnimationRepeat(Animation animation);

        void onAnimationStart(Animation animation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class NoImagePreloadHolder {
        public static final boolean USE_CLOSEGUARD = SystemProperties.getBoolean("log.closeguard.Animation", false);

        private NoImagePreloadHolder() {
        }
    }

    public Animation() {
        ensureInterpolator();
    }

    public Animation(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.Animation);
        setDuration(a.getInt(2, 0));
        setStartOffset(a.getInt(5, 0));
        setFillEnabled(a.getBoolean(9, this.mFillEnabled));
        setFillBefore(a.getBoolean(3, this.mFillBefore));
        setFillAfter(a.getBoolean(4, this.mFillAfter));
        setRepeatCount(a.getInt(6, this.mRepeatCount));
        setRepeatMode(a.getInt(7, 1));
        setZAdjustment(a.getInt(8, 0));
        setBackdropColor(a.getInt(12, 0));
        setDetachWallpaper(a.getBoolean(10, false));
        setShowWallpaper(a.getBoolean(14, false));
        setHasRoundedCorners(a.getBoolean(13, false));
        setShowBackdrop(a.getBoolean(11, false));
        int resID = a.getResourceId(1, 0);
        a.recycle();
        if (resID > 0) {
            setInterpolator(context, resID);
        }
        ensureInterpolator();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone */
    public Animation mo5340clone() throws CloneNotSupportedException {
        Animation animation = (Animation) super.clone();
        animation.mPreviousRegion = new RectF();
        animation.mRegion = new RectF();
        animation.mTransformation = new Transformation();
        animation.mPreviousTransformation = new Transformation();
        return animation;
    }

    public void reset() {
        this.mPreviousRegion.setEmpty();
        this.mPreviousTransformation.clear();
        this.mInitialized = false;
        this.mCycleFlip = false;
        this.mRepeated = 0;
        this.mMore = true;
        this.mOneMoreTime = true;
        this.mListenerHandler = null;
    }

    public void cancel() {
        if (this.mStarted && !this.mEnded) {
            fireAnimationEnd();
            this.mEnded = true;
            this.guard.close();
        }
        this.mStartTime = Long.MIN_VALUE;
        this.mOneMoreTime = false;
        this.mMore = false;
    }

    public void detach() {
        if (this.mStarted && !this.mEnded) {
            this.mEnded = true;
            this.guard.close();
            fireAnimationEnd();
        }
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        reset();
        this.mInitialized = true;
    }

    public void setListenerHandler(Handler handler) {
        if (this.mListenerHandler == null) {
            this.mOnStart = new Runnable() { // from class: android.view.animation.Animation.1
                @Override // java.lang.Runnable
                public void run() {
                    Animation.this.dispatchAnimationStart();
                }
            };
            this.mOnRepeat = new Runnable() { // from class: android.view.animation.Animation.2
                @Override // java.lang.Runnable
                public void run() {
                    Animation.this.dispatchAnimationRepeat();
                }
            };
            this.mOnEnd = new Runnable() { // from class: android.view.animation.Animation.3
                @Override // java.lang.Runnable
                public void run() {
                    Animation.this.dispatchAnimationEnd();
                }
            };
        }
        this.mListenerHandler = handler;
    }

    public void setInterpolator(Context context, int resID) {
        setInterpolator(AnimationUtils.loadInterpolator(context, resID));
    }

    public void setInterpolator(Interpolator i) {
        this.mInterpolator = i;
    }

    public void setStartOffset(long startOffset) {
        this.mStartOffset = startOffset;
    }

    public void setDuration(long durationMillis) {
        if (durationMillis < 0) {
            throw new IllegalArgumentException("Animation duration cannot be negative");
        }
        this.mDuration = durationMillis;
    }

    public void restrictDuration(long durationMillis) {
        long j = this.mStartOffset;
        if (j > durationMillis) {
            this.mStartOffset = durationMillis;
            this.mDuration = 0L;
            this.mRepeatCount = 0;
            return;
        }
        long dur = this.mDuration + j;
        if (dur > durationMillis) {
            this.mDuration = durationMillis - j;
            dur = durationMillis;
        }
        if (this.mDuration <= 0) {
            this.mDuration = 0L;
            this.mRepeatCount = 0;
            return;
        }
        int i = this.mRepeatCount;
        if (i < 0 || i > durationMillis || i * dur > durationMillis) {
            int i2 = ((int) (durationMillis / dur)) - 1;
            this.mRepeatCount = i2;
            if (i2 < 0) {
                this.mRepeatCount = 0;
            }
        }
    }

    public void scaleCurrentDuration(float scale) {
        this.mDuration = ((float) this.mDuration) * scale;
        this.mStartOffset = ((float) this.mStartOffset) * scale;
    }

    public void setStartTime(long startTimeMillis) {
        this.mStartTime = startTimeMillis;
        this.mEnded = false;
        this.mStarted = false;
        this.mCycleFlip = false;
        this.mRepeated = 0;
        this.mMore = true;
    }

    public void start() {
        setStartTime(-1L);
    }

    public void startNow() {
        setStartTime(AnimationUtils.currentAnimationTimeMillis());
    }

    public void setRepeatMode(int repeatMode) {
        this.mRepeatMode = repeatMode;
    }

    public void setRepeatCount(int repeatCount) {
        if (repeatCount < 0) {
            repeatCount = -1;
        }
        this.mRepeatCount = repeatCount;
    }

    public boolean isFillEnabled() {
        return this.mFillEnabled;
    }

    public void setFillEnabled(boolean fillEnabled) {
        this.mFillEnabled = fillEnabled;
    }

    public void setFillBefore(boolean fillBefore) {
        this.mFillBefore = fillBefore;
    }

    public void setFillAfter(boolean fillAfter) {
        this.mFillAfter = fillAfter;
    }

    public void setZAdjustment(int zAdjustment) {
        this.mZAdjustment = zAdjustment;
    }

    @Deprecated
    public void setBackgroundColor(int bg) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getScaleFactor() {
        return this.mScaleFactor;
    }

    @Deprecated
    public void setDetachWallpaper(boolean detachWallpaper) {
    }

    public void setShowWallpaper(boolean showWallpaper) {
        this.mShowWallpaper = showWallpaper;
    }

    public void setHasRoundedCorners(boolean hasRoundedCorners) {
        this.mHasRoundedCorners = hasRoundedCorners;
    }

    public void setShowBackdrop(boolean showBackdrop) {
        this.mShowBackdrop = showBackdrop;
    }

    public void setBackdropColor(int backdropColor) {
        this.mBackdropColor = backdropColor;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public long getStartTime() {
        return this.mStartTime;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getStartOffset() {
        return this.mStartOffset;
    }

    public int getRepeatMode() {
        return this.mRepeatMode;
    }

    public int getRepeatCount() {
        return this.mRepeatCount;
    }

    public boolean getFillBefore() {
        return this.mFillBefore;
    }

    public boolean getFillAfter() {
        return this.mFillAfter;
    }

    public int getZAdjustment() {
        return this.mZAdjustment;
    }

    @Deprecated
    public int getBackgroundColor() {
        return 0;
    }

    @Deprecated
    public boolean getDetachWallpaper() {
        return true;
    }

    public boolean getShowWallpaper() {
        return this.mShowWallpaper;
    }

    public boolean hasRoundedCorners() {
        return this.mHasRoundedCorners;
    }

    public boolean hasExtension() {
        return false;
    }

    public boolean getShowBackdrop() {
        return this.mShowBackdrop;
    }

    public int getBackdropColor() {
        return this.mBackdropColor;
    }

    public boolean willChangeTransformationMatrix() {
        return true;
    }

    public boolean willChangeBounds() {
        return true;
    }

    private boolean hasAnimationListener() {
        return this.mListener != null;
    }

    public void setAnimationListener(AnimationListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void ensureInterpolator() {
        if (this.mInterpolator == null) {
            this.mInterpolator = new AccelerateDecelerateInterpolator();
        }
    }

    public long computeDurationHint() {
        return (getStartOffset() + getDuration()) * (getRepeatCount() + 1);
    }

    public void getTransformationAt(float normalizedTime, Transformation outTransformation) {
        float interpolatedTime = this.mInterpolator.getInterpolation(normalizedTime);
        applyTransformation(interpolatedTime, outTransformation);
    }

    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        float normalizedTime;
        if (this.mStartTime == -1) {
            this.mStartTime = currentTime;
        }
        long startOffset = getStartOffset();
        long duration = this.mDuration;
        if (duration != 0) {
            normalizedTime = ((float) (currentTime - (this.mStartTime + startOffset))) / ((float) duration);
        } else {
            normalizedTime = currentTime < this.mStartTime ? 0.0f : 1.0f;
        }
        boolean expired = normalizedTime >= 1.0f || isCanceled();
        this.mMore = !expired;
        if (!this.mFillEnabled) {
            normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
        }
        if ((normalizedTime >= 0.0f || this.mFillBefore) && (normalizedTime <= 1.0f || this.mFillAfter)) {
            if (!this.mStarted) {
                fireAnimationStart();
                this.mStarted = true;
                if (NoImagePreloadHolder.USE_CLOSEGUARD) {
                    this.guard.open("cancel or detach or getTransformation");
                }
            }
            if (this.mFillEnabled) {
                normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
            }
            if (this.mCycleFlip) {
                normalizedTime = 1.0f - normalizedTime;
            }
            getTransformationAt(normalizedTime, outTransformation);
        }
        if (expired) {
            if (this.mRepeatCount == this.mRepeated || isCanceled()) {
                if (!this.mEnded) {
                    this.mEnded = true;
                    this.guard.close();
                    fireAnimationEnd();
                }
            } else {
                if (this.mRepeatCount > 0) {
                    this.mRepeated++;
                }
                if (this.mRepeatMode == 2) {
                    this.mCycleFlip = !this.mCycleFlip;
                }
                this.mStartTime = -1L;
                this.mMore = true;
                fireAnimationRepeat();
            }
        }
        boolean z = this.mMore;
        if (!z && this.mOneMoreTime) {
            this.mOneMoreTime = false;
            return true;
        }
        return z;
    }

    private boolean isCanceled() {
        return this.mStartTime == Long.MIN_VALUE;
    }

    private void fireAnimationStart() {
        if (hasAnimationListener()) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnStart);
            } else {
                dispatchAnimationStart();
            }
        }
    }

    private void fireAnimationRepeat() {
        if (hasAnimationListener()) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnRepeat);
            } else {
                dispatchAnimationRepeat();
            }
        }
    }

    private void fireAnimationEnd() {
        if (hasAnimationListener()) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnEnd);
            } else {
                dispatchAnimationEnd();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchAnimationStart() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            animationListener.onAnimationStart(this);
        }
    }

    void dispatchAnimationRepeat() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            animationListener.onAnimationRepeat(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchAnimationEnd() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            animationListener.onAnimationEnd(this);
        }
    }

    public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
        this.mScaleFactor = scale;
        return getTransformation(currentTime, outTransformation);
    }

    public boolean hasStarted() {
        return this.mStarted;
    }

    public boolean hasEnded() {
        return this.mEnded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float resolveSize(int type, float value, int size, int parentSize) {
        switch (type) {
            case 0:
                return value;
            case 1:
                return size * value;
            case 2:
                return parentSize * value;
            default:
                return value;
        }
    }

    public void getInvalidateRegion(int left, int top, int right, int bottom, RectF invalidate, Transformation transformation) {
        RectF tempRegion = this.mRegion;
        RectF previousRegion = this.mPreviousRegion;
        invalidate.set(left, top, right, bottom);
        transformation.getMatrix().mapRect(invalidate);
        invalidate.inset(-1.0f, -1.0f);
        tempRegion.set(invalidate);
        invalidate.union(previousRegion);
        previousRegion.set(tempRegion);
        Transformation tempTransformation = this.mTransformation;
        Transformation previousTransformation = this.mPreviousTransformation;
        tempTransformation.set(transformation);
        transformation.set(previousTransformation);
        previousTransformation.set(tempTransformation);
    }

    public void initializeInvalidateRegion(int left, int top, int right, int bottom) {
        RectF region = this.mPreviousRegion;
        region.set(left, top, right, bottom);
        region.inset(-1.0f, -1.0f);
        if (this.mFillBefore) {
            Transformation previousTransformation = this.mPreviousTransformation;
            applyTransformation(this.mInterpolator.getInterpolation(0.0f), previousTransformation);
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.guard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
        } finally {
            super.finalize();
        }
    }

    public boolean hasAlpha() {
        return false;
    }

    /* loaded from: classes4.dex */
    protected static class Description {
        public int type;
        public float value;

        protected Description() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Description parseValue(TypedValue value, Context context) {
            Description d = new Description();
            if (value == null) {
                d.type = 0;
                d.value = 0.0f;
            } else if (value.type == 6) {
                d.type = (value.data & 15) == 1 ? 2 : 1;
                d.value = TypedValue.complexToFloat(value.data);
                return d;
            } else if (value.type == 4) {
                d.type = 0;
                d.value = value.getFloat();
                return d;
            } else if (value.type >= 16 && value.type <= 31) {
                d.type = 0;
                d.value = value.data;
                return d;
            } else if (value.type == 5) {
                d.type = 0;
                d.value = TypedValue.complexToDimension(value.data, context.getResources().getDisplayMetrics());
                return d;
            }
            d.type = 0;
            d.value = 0.0f;
            return d;
        }
    }
}
