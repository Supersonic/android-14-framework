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
public class AnticipateInterpolator extends BaseInterpolator implements NativeInterpolator {
    private final float mTension;

    public AnticipateInterpolator() {
        this.mTension = 2.0f;
    }

    public AnticipateInterpolator(float tension) {
        this.mTension = tension;
    }

    public AnticipateInterpolator(Context context, AttributeSet attrs) {
        this(context.getResources(), context.getTheme(), attrs);
    }

    public AnticipateInterpolator(Resources res, Resources.Theme theme, AttributeSet attrs) {
        TypedArray a;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, C4057R.styleable.AnticipateInterpolator, 0, 0);
        } else {
            a = res.obtainAttributes(attrs, C4057R.styleable.AnticipateInterpolator);
        }
        this.mTension = a.getFloat(0, 2.0f);
        setChangingConfiguration(a.getChangingConfigurations());
        a.recycle();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float f = this.mTension;
        return t * t * (((1.0f + f) * t) - f);
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createAnticipateInterpolator(this.mTension);
    }
}
