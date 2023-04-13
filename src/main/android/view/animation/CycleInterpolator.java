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
public class CycleInterpolator extends BaseInterpolator implements NativeInterpolator {
    private float mCycles;

    public CycleInterpolator(float cycles) {
        this.mCycles = cycles;
    }

    public CycleInterpolator(Context context, AttributeSet attrs) {
        this(context.getResources(), context.getTheme(), attrs);
    }

    public CycleInterpolator(Resources resources, Resources.Theme theme, AttributeSet attrs) {
        TypedArray a;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, C4057R.styleable.CycleInterpolator, 0, 0);
        } else {
            a = resources.obtainAttributes(attrs, C4057R.styleable.CycleInterpolator);
        }
        this.mCycles = a.getFloat(0, 1.0f);
        setChangingConfiguration(a.getChangingConfigurations());
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return (float) Math.sin(this.mCycles * 2.0f * 3.141592653589793d * input);
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createCycleInterpolator(this.mCycles);
    }
}
