package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class ScaleAnimation extends Animation {
    private float mFromX;
    private int mFromXData;
    private int mFromXType;
    private float mFromY;
    private int mFromYData;
    private int mFromYType;
    private float mPivotX;
    private int mPivotXType;
    private float mPivotXValue;
    private float mPivotY;
    private int mPivotYType;
    private float mPivotYValue;
    private final Resources mResources;
    private float mToX;
    private int mToXData;
    private int mToXType;
    private float mToY;
    private int mToYData;
    private int mToYType;

    public ScaleAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXData = 0;
        this.mToXData = 0;
        this.mFromYData = 0;
        this.mToYData = 0;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mResources = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ScaleAnimation);
        TypedValue tv = a.peekValue(2);
        this.mFromX = 0.0f;
        if (tv != null) {
            if (tv.type == 4) {
                this.mFromX = tv.getFloat();
            } else {
                this.mFromXType = tv.type;
                this.mFromXData = tv.data;
            }
        }
        TypedValue tv2 = a.peekValue(3);
        this.mToX = 0.0f;
        if (tv2 != null) {
            if (tv2.type == 4) {
                this.mToX = tv2.getFloat();
            } else {
                this.mToXType = tv2.type;
                this.mToXData = tv2.data;
            }
        }
        TypedValue tv3 = a.peekValue(4);
        this.mFromY = 0.0f;
        if (tv3 != null) {
            if (tv3.type == 4) {
                this.mFromY = tv3.getFloat();
            } else {
                this.mFromYType = tv3.type;
                this.mFromYData = tv3.data;
            }
        }
        TypedValue tv4 = a.peekValue(5);
        this.mToY = 0.0f;
        if (tv4 != null) {
            if (tv4.type == 4) {
                this.mToY = tv4.getFloat();
            } else {
                this.mToYType = tv4.type;
                this.mToYData = tv4.data;
            }
        }
        Animation.Description d = Animation.Description.parseValue(a.peekValue(0), context);
        this.mPivotXType = d.type;
        this.mPivotXValue = d.value;
        Animation.Description d2 = Animation.Description.parseValue(a.peekValue(1), context);
        this.mPivotYType = d2.type;
        this.mPivotYValue = d2.value;
        a.recycle();
        initializePivotPoint();
    }

    public ScaleAnimation(float fromX, float toX, float fromY, float toY) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXData = 0;
        this.mToXData = 0;
        this.mFromYData = 0;
        this.mToYData = 0;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mResources = null;
        this.mFromX = fromX;
        this.mToX = toX;
        this.mFromY = fromY;
        this.mToY = toY;
        this.mPivotX = 0.0f;
        this.mPivotY = 0.0f;
    }

    public ScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXData = 0;
        this.mToXData = 0;
        this.mFromYData = 0;
        this.mToYData = 0;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mResources = null;
        this.mFromX = fromX;
        this.mToX = toX;
        this.mFromY = fromY;
        this.mToY = toY;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotXValue = pivotX;
        this.mPivotYValue = pivotY;
        initializePivotPoint();
    }

    public ScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromXData = 0;
        this.mToXData = 0;
        this.mFromYData = 0;
        this.mToYData = 0;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mResources = null;
        this.mFromX = fromX;
        this.mToX = toX;
        this.mFromY = fromY;
        this.mToY = toY;
        this.mPivotXValue = pivotXValue;
        this.mPivotXType = pivotXType;
        this.mPivotYValue = pivotYValue;
        this.mPivotYType = pivotYType;
        initializePivotPoint();
    }

    private void initializePivotPoint() {
        if (this.mPivotXType == 0) {
            this.mPivotX = this.mPivotXValue;
        }
        if (this.mPivotYType == 0) {
            this.mPivotY = this.mPivotYValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float sx = 1.0f;
        float sy = 1.0f;
        float scale = getScaleFactor();
        float f = this.mFromX;
        if (f != 1.0f || this.mToX != 1.0f) {
            sx = f + ((this.mToX - f) * interpolatedTime);
        }
        float f2 = this.mFromY;
        if (f2 != 1.0f || this.mToY != 1.0f) {
            sy = f2 + ((this.mToY - f2) * interpolatedTime);
        }
        if (this.mPivotX == 0.0f && this.mPivotY == 0.0f) {
            t.getMatrix().setScale(sx, sy);
        } else {
            t.getMatrix().setScale(sx, sy, this.mPivotX * scale, this.mPivotY * scale);
        }
    }

    float resolveScale(float scale, int type, int data, int size, int psize) {
        float targetSize;
        if (type == 6) {
            targetSize = TypedValue.complexToFraction(data, size, psize);
        } else if (type == 5) {
            targetSize = TypedValue.complexToDimension(data, this.mResources.getDisplayMetrics());
        } else {
            return scale;
        }
        if (size == 0) {
            return 1.0f;
        }
        return targetSize / size;
    }

    @Override // android.view.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mFromX = resolveScale(this.mFromX, this.mFromXType, this.mFromXData, width, parentWidth);
        this.mToX = resolveScale(this.mToX, this.mToXType, this.mToXData, width, parentWidth);
        this.mFromY = resolveScale(this.mFromY, this.mFromYType, this.mFromYData, height, parentHeight);
        this.mToY = resolveScale(this.mToY, this.mToYType, this.mToYData, height, parentHeight);
        this.mPivotX = resolveSize(this.mPivotXType, this.mPivotXValue, width, parentWidth);
        this.mPivotY = resolveSize(this.mPivotYType, this.mPivotYValue, height, parentHeight);
    }
}
