package android.view.animation;

import android.content.Context;
import android.graphics.animation.HasNativeInterpolator;
import android.graphics.animation.NativeInterpolator;
import android.graphics.animation.NativeInterpolatorFactory;
import android.util.AttributeSet;
@HasNativeInterpolator
/* loaded from: classes4.dex */
public class BounceInterpolator extends BaseInterpolator implements NativeInterpolator {
    public BounceInterpolator() {
    }

    public BounceInterpolator(Context context, AttributeSet attrs) {
    }

    private static float bounce(float t) {
        return t * t * 8.0f;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t * 1.1226f;
        return t2 < 0.3535f ? bounce(t2) : t2 < 0.7408f ? bounce(t2 - 0.54719f) + 0.7f : t2 < 0.9644f ? bounce(t2 - 0.8526f) + 0.9f : bounce(t2 - 1.0435f) + 0.95f;
    }

    @Override // android.graphics.animation.NativeInterpolator
    public long createNativeInterpolator() {
        return NativeInterpolatorFactory.createBounceInterpolator();
    }
}
