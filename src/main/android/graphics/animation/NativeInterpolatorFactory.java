package android.graphics.animation;

import android.animation.TimeInterpolator;
/* loaded from: classes.dex */
public final class NativeInterpolatorFactory {
    public static native long createAccelerateDecelerateInterpolator();

    public static native long createAccelerateInterpolator(float f);

    public static native long createAnticipateInterpolator(float f);

    public static native long createAnticipateOvershootInterpolator(float f);

    public static native long createBounceInterpolator();

    public static native long createCycleInterpolator(float f);

    public static native long createDecelerateInterpolator(float f);

    public static native long createLinearInterpolator();

    public static native long createLutInterpolator(float[] fArr);

    public static native long createOvershootInterpolator(float f);

    public static native long createPathInterpolator(float[] fArr, float[] fArr2);

    private NativeInterpolatorFactory() {
    }

    public static long createNativeInterpolator(TimeInterpolator interpolator, long duration) {
        if (interpolator == null) {
            return createLinearInterpolator();
        }
        if (RenderNodeAnimator.isNativeInterpolator(interpolator)) {
            return ((NativeInterpolator) interpolator).createNativeInterpolator();
        }
        return FallbackLUTInterpolator.createNativeInterpolator(interpolator, duration);
    }
}
