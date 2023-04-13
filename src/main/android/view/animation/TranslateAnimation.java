package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class TranslateAnimation extends Animation {
    protected float mFromXDelta;
    private int mFromXType;
    protected float mFromXValue;
    protected float mFromYDelta;
    private int mFromYType;
    protected float mFromYValue;
    private int mParentWidth;
    protected float mToXDelta;
    private int mToXType;
    protected float mToXValue;
    protected float mToYDelta;
    private int mToYType;
    protected float mToYValue;
    private int mWidth;

    public TranslateAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXValue = 0.0f;
        this.mToXValue = 0.0f;
        this.mFromYValue = 0.0f;
        this.mToYValue = 0.0f;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.TranslateAnimation);
        Animation.Description d = Animation.Description.parseValue(a.peekValue(0), context);
        this.mFromXType = d.type;
        this.mFromXValue = d.value;
        Animation.Description d2 = Animation.Description.parseValue(a.peekValue(1), context);
        this.mToXType = d2.type;
        this.mToXValue = d2.value;
        Animation.Description d3 = Animation.Description.parseValue(a.peekValue(2), context);
        this.mFromYType = d3.type;
        this.mFromYValue = d3.value;
        Animation.Description d4 = Animation.Description.parseValue(a.peekValue(3), context);
        this.mToYType = d4.type;
        this.mToYValue = d4.value;
        a.recycle();
    }

    public TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXValue = 0.0f;
        this.mToXValue = 0.0f;
        this.mFromYValue = 0.0f;
        this.mToYValue = 0.0f;
        this.mFromXValue = fromXDelta;
        this.mToXValue = toXDelta;
        this.mFromYValue = fromYDelta;
        this.mToYValue = toYDelta;
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
    }

    public TranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXValue = 0.0f;
        this.mToXValue = 0.0f;
        this.mFromYValue = 0.0f;
        this.mToYValue = 0.0f;
        this.mFromXValue = fromXValue;
        this.mToXValue = toXValue;
        this.mFromYValue = fromYValue;
        this.mToYValue = toYValue;
        this.mFromXType = fromXType;
        this.mToXType = toXType;
        this.mFromYType = fromYType;
        this.mToYType = toYType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float dx = this.mFromXDelta;
        float dy = this.mFromYDelta;
        float f = this.mFromXDelta;
        float f2 = this.mToXDelta;
        if (f != f2) {
            dx = f + ((f2 - f) * interpolatedTime);
        }
        float f3 = this.mFromYDelta;
        float f4 = this.mToYDelta;
        if (f3 != f4) {
            dy = f3 + ((f4 - f3) * interpolatedTime);
        }
        t.getMatrix().setTranslate(dx, dy);
    }

    @Override // android.view.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mFromXDelta = resolveSize(this.mFromXType, this.mFromXValue, width, parentWidth);
        this.mToXDelta = resolveSize(this.mToXType, this.mToXValue, width, parentWidth);
        this.mFromYDelta = resolveSize(this.mFromYType, this.mFromYValue, height, parentHeight);
        this.mToYDelta = resolveSize(this.mToYType, this.mToYValue, height, parentHeight);
        this.mWidth = width;
        this.mParentWidth = parentWidth;
    }

    public boolean isXAxisTransition() {
        return this.mFromXDelta - this.mToXDelta != 0.0f && this.mFromYDelta - this.mToYDelta == 0.0f;
    }

    public boolean isFullWidthTranslate() {
        boolean isXAxisSlideTransition = isSlideInLeft() || isSlideOutRight() || isSlideInRight() || isSlideOutLeft();
        return this.mWidth == this.mParentWidth && isXAxisSlideTransition;
    }

    private boolean isSlideInLeft() {
        boolean startsOutOfParentOnLeft = this.mFromXDelta <= ((float) (-this.mWidth));
        return startsOutOfParentOnLeft && endsXEnclosedWithinParent();
    }

    private boolean isSlideOutRight() {
        boolean endOutOfParentOnRight = this.mToXDelta >= ((float) this.mParentWidth);
        return startsXEnclosedWithinParent() && endOutOfParentOnRight;
    }

    private boolean isSlideInRight() {
        boolean startsOutOfParentOnRight = this.mFromXDelta >= ((float) this.mParentWidth);
        return startsOutOfParentOnRight && endsXEnclosedWithinParent();
    }

    private boolean isSlideOutLeft() {
        boolean endOutOfParentOnLeft = this.mToXDelta <= ((float) (-this.mWidth));
        return startsXEnclosedWithinParent() && endOutOfParentOnLeft;
    }

    private boolean endsXEnclosedWithinParent() {
        int i = this.mWidth;
        int i2 = this.mParentWidth;
        if (i <= i2) {
            float f = this.mToXDelta;
            if (i + f <= i2 && f >= 0.0f) {
                return true;
            }
        }
        return false;
    }

    private boolean startsXEnclosedWithinParent() {
        int i = this.mWidth;
        int i2 = this.mParentWidth;
        if (i <= i2) {
            float f = this.mFromXDelta;
            if (i + f <= i2 && f >= 0.0f) {
                return true;
            }
        }
        return false;
    }
}
