package android.hardware.camera2.utils;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.util.Rational;
import android.util.Size;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class ParamsUtils {
    private static final int RATIONAL_DENOMINATOR = 1000000;

    public static Rect createRect(Size size) {
        Preconditions.checkNotNull(size, "size must not be null");
        return new Rect(0, 0, size.getWidth(), size.getHeight());
    }

    public static Rect createRect(RectF rect) {
        Preconditions.checkNotNull(rect, "rect must not be null");
        Rect r = new Rect();
        rect.roundOut(r);
        return r;
    }

    public static Rect mapRect(Matrix transform, Rect rect) {
        Preconditions.checkNotNull(transform, "transform must not be null");
        Preconditions.checkNotNull(rect, "rect must not be null");
        RectF rectF = new RectF(rect);
        transform.mapRect(rectF);
        return createRect(rectF);
    }

    public static Size createSize(Rect rect) {
        Preconditions.checkNotNull(rect, "rect must not be null");
        return new Size(rect.width(), rect.height());
    }

    public static Rational createRational(float value) {
        float numF;
        if (Float.isNaN(value)) {
            return Rational.NaN;
        }
        if (value == Float.POSITIVE_INFINITY) {
            return Rational.POSITIVE_INFINITY;
        }
        if (value == Float.NEGATIVE_INFINITY) {
            return Rational.NEGATIVE_INFINITY;
        }
        if (value == 0.0f) {
            return Rational.ZERO;
        }
        int den = 1000000;
        while (true) {
            numF = den * value;
            if ((numF <= -2.14748365E9f || numF >= 2.14748365E9f) && den != 1) {
                den /= 10;
            }
        }
        int num = (int) numF;
        return new Rational(num, den);
    }

    public static void convertRectF(Rect source, RectF destination) {
        Preconditions.checkNotNull(source, "source must not be null");
        Preconditions.checkNotNull(destination, "destination must not be null");
        destination.left = source.left;
        destination.right = source.right;
        destination.bottom = source.bottom;
        destination.top = source.top;
    }

    public static <T> T getOrDefault(CaptureRequest r, CaptureRequest.Key<T> key, T defaultValue) {
        Preconditions.checkNotNull(r, "r must not be null");
        Preconditions.checkNotNull(key, "key must not be null");
        Preconditions.checkNotNull(defaultValue, "defaultValue must not be null");
        T value = (T) r.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private ParamsUtils() {
        throw new AssertionError();
    }
}
