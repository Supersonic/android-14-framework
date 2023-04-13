package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.animation.HasNativeInterpolator;
import android.graphics.animation.NativeInterpolator;
import android.graphics.animation.NativeInterpolatorFactory;
import android.util.AttributeSet;
import com.android.internal.C4057R;
@HasNativeInterpolator
/* loaded from: classes4.dex */
public class AccelerateInterpolator extends BaseInterpolator implements NativeInterpolator {
    private final double mDoubleFactor;
    private final float mFactor;

    public AccelerateInterpolator() {
        this.mFactor = 1.0f;
        this.mDoubleFactor = 2.0d;
    }

    public AccelerateInterpolator(float factor) {
        this.mFactor = factor;
        this.mDoubleFactor = 2.0f * factor;
    }

    public AccelerateInterpolator(Context context, AttributeSet attrs) {
        this(context.getResources(), context.getTheme(), attrs);
    }

    public AccelerateInterpolator(Resources res, Resources.Theme theme, AttributeSet attrs) {
        TypedArray a;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, C4057R.styleable.AccelerateInterpolator, 0, 0);
        } else {
            a = res.obtainAttributes(attrs, C4057R.styleable.AccelerateInterpolator);
        }
        float f = a.getFloat(0, 1.0f);
        this.mFactor = f;
        this.mDoubleFactor = f * 2.0f;
        setChangingConfiguration(a.getChangingConfigurations());
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        if (this.mFactor == 1.0f) {
            return input * input;
        }
        return (float) Math.pow(input, this.mDoubleFactor);
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createAccelerateInterpolator(this.mFactor);
    }
}
