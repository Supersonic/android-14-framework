package android.hardware.camera2.params;

import android.graphics.PointF;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class TonemapCurve {
    public static final int CHANNEL_BLUE = 2;
    public static final int CHANNEL_GREEN = 1;
    public static final int CHANNEL_RED = 0;
    public static final float LEVEL_BLACK = 0.0f;
    public static final float LEVEL_WHITE = 1.0f;
    private static final int MIN_CURVE_LENGTH = 4;
    private static final int OFFSET_POINT_IN = 0;
    private static final int OFFSET_POINT_OUT = 1;
    public static final int POINT_SIZE = 2;
    private static final int TONEMAP_MIN_CURVE_POINTS = 2;
    private final float[] mBlue;
    private final float[] mGreen;
    private boolean mHashCalculated = false;
    private int mHashCode;
    private final float[] mRed;

    public TonemapCurve(float[] red, float[] green, float[] blue) {
        Preconditions.checkNotNull(red, "red must not be null");
        Preconditions.checkNotNull(green, "green must not be null");
        Preconditions.checkNotNull(blue, "blue must not be null");
        checkArgumentArrayLengthDivisibleBy(red, 2, "red");
        checkArgumentArrayLengthDivisibleBy(green, 2, "green");
        checkArgumentArrayLengthDivisibleBy(blue, 2, "blue");
        checkArgumentArrayLengthNoLessThan(red, 4, "red");
        checkArgumentArrayLengthNoLessThan(green, 4, "green");
        checkArgumentArrayLengthNoLessThan(blue, 4, "blue");
        Preconditions.checkArrayElementsInRange(red, 0.0f, 1.0f, "red");
        Preconditions.checkArrayElementsInRange(green, 0.0f, 1.0f, "green");
        Preconditions.checkArrayElementsInRange(blue, 0.0f, 1.0f, "blue");
        this.mRed = Arrays.copyOf(red, red.length);
        this.mGreen = Arrays.copyOf(green, green.length);
        this.mBlue = Arrays.copyOf(blue, blue.length);
    }

    private static void checkArgumentArrayLengthDivisibleBy(float[] array, int divisible, String arrayName) {
        if (array.length % divisible != 0) {
            throw new IllegalArgumentException(arrayName + " size must be divisible by " + divisible);
        }
    }

    private static int checkArgumentColorChannel(int colorChannel) {
        switch (colorChannel) {
            case 0:
            case 1:
            case 2:
                return colorChannel;
            default:
                throw new IllegalArgumentException("colorChannel out of range");
        }
    }

    private static void checkArgumentArrayLengthNoLessThan(float[] array, int minLength, String arrayName) {
        if (array.length < minLength) {
            throw new IllegalArgumentException(arrayName + " size must be at least " + minLength);
        }
    }

    public int getPointCount(int colorChannel) {
        checkArgumentColorChannel(colorChannel);
        return getCurve(colorChannel).length / 2;
    }

    public PointF getPoint(int colorChannel, int index) {
        checkArgumentColorChannel(colorChannel);
        if (index < 0 || index >= getPointCount(colorChannel)) {
            throw new IllegalArgumentException("index out of range");
        }
        float[] curve = getCurve(colorChannel);
        float pIn = curve[(index * 2) + 0];
        float pOut = curve[(index * 2) + 1];
        return new PointF(pIn, pOut);
    }

    public void copyColorCurve(int colorChannel, float[] destination, int offset) {
        Preconditions.checkArgumentNonnegative(offset, "offset must not be negative");
        Preconditions.checkNotNull(destination, "destination must not be null");
        if (destination.length + offset < getPointCount(colorChannel) * 2) {
            throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
        }
        float[] curve = getCurve(colorChannel);
        System.arraycopy(curve, 0, destination, offset, curve.length);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TonemapCurve)) {
            return false;
        }
        TonemapCurve other = (TonemapCurve) obj;
        if (!Arrays.equals(this.mRed, other.mRed) || !Arrays.equals(this.mGreen, other.mGreen) || !Arrays.equals(this.mBlue, other.mBlue)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.mHashCalculated) {
            return this.mHashCode;
        }
        int hashCodeGeneric = HashCodeHelpers.hashCodeGeneric(this.mRed, this.mGreen, this.mBlue);
        this.mHashCode = hashCodeGeneric;
        this.mHashCalculated = true;
        return hashCodeGeneric;
    }

    public String toString() {
        return "TonemapCurve{R:" + curveToString(0) + ", G:" + curveToString(1) + ", B:" + curveToString(2) + "}";
    }

    private String curveToString(int colorChannel) {
        checkArgumentColorChannel(colorChannel);
        StringBuilder sb = new StringBuilder(NavigationBarInflaterView.SIZE_MOD_START);
        float[] curve = getCurve(colorChannel);
        int pointCount = curve.length / 2;
        int i = 0;
        int j = 0;
        while (i < pointCount) {
            sb.append(NavigationBarInflaterView.KEY_CODE_START);
            sb.append(curve[j]);
            sb.append(", ");
            sb.append(curve[j + 1]);
            sb.append("), ");
            i++;
            j += 2;
        }
        int i2 = sb.length();
        sb.setLength(i2 - 2);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    private float[] getCurve(int colorChannel) {
        switch (colorChannel) {
            case 0:
                return this.mRed;
            case 1:
                return this.mGreen;
            case 2:
                return this.mBlue;
            default:
                throw new AssertionError("colorChannel out of range");
        }
    }
}
