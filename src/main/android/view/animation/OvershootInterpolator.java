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
public class OvershootInterpolator extends BaseInterpolator implements NativeInterpolator {
    private final float mTension;

    public OvershootInterpolator() {
        this.mTension = 2.0f;
    }

    public OvershootInterpolator(float tension) {
        this.mTension = tension;
    }

    public OvershootInterpolator(Context context, AttributeSet attrs) {
        this(context.getResources(), context.getTheme(), attrs);
    }

    public OvershootInterpolator(Resources res, Resources.Theme theme, AttributeSet attrs) {
        TypedArray a;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, C4057R.styleable.OvershootInterpolator, 0, 0);
        } else {
            a = res.obtainAttributes(attrs, C4057R.styleable.OvershootInterpolator);
        }
        this.mTension = a.getFloat(0, 2.0f);
        setChangingConfiguration(a.getChangingConfigurations());
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t - 1.0f;
        float f = this.mTension;
        return (t2 * t2 * (((f + 1.0f) * t2) + f)) + 1.0f;
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createOvershootInterpolator(this.mTension);
    }
}
