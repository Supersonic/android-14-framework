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
public class DecelerateInterpolator extends BaseInterpolator implements NativeInterpolator {
    private float mFactor;

    public DecelerateInterpolator() {
        this.mFactor = 1.0f;
    }

    public DecelerateInterpolator(float factor) {
        this.mFactor = 1.0f;
        this.mFactor = factor;
    }

    public DecelerateInterpolator(Context context, AttributeSet attrs) {
        this(context.getResources(), context.getTheme(), attrs);
    }

    public DecelerateInterpolator(Resources res, Resources.Theme theme, AttributeSet attrs) {
        TypedArray a;
        this.mFactor = 1.0f;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, C4057R.styleable.DecelerateInterpolator, 0, 0);
        } else {
            a = res.obtainAttributes(attrs, C4057R.styleable.DecelerateInterpolator);
        }
        this.mFactor = a.getFloat(0, 1.0f);
        setChangingConfiguration(a.getChangingConfigurations());
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        float f = this.mFactor;
        if (f == 1.0f) {
            float result = 1.0f - ((1.0f - input) * (1.0f - input));
            return result;
        }
        float result2 = 1.0f - input;
        return (float) (1.0d - Math.pow(result2, f * 2.0f));
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createDecelerateInterpolator(this.mFactor);
    }
}
