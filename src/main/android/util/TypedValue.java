package android.util;

import android.app.backup.FullBackup;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class TypedValue {
    public static final int COMPLEX_MANTISSA_MASK = 16777215;
    public static final int COMPLEX_MANTISSA_SHIFT = 8;
    public static final int COMPLEX_RADIX_0p23 = 3;
    public static final int COMPLEX_RADIX_16p7 = 1;
    public static final int COMPLEX_RADIX_23p0 = 0;
    public static final int COMPLEX_RADIX_8p15 = 2;
    public static final int COMPLEX_RADIX_MASK = 3;
    public static final int COMPLEX_RADIX_SHIFT = 4;
    public static final int COMPLEX_UNIT_DIP = 1;
    public static final int COMPLEX_UNIT_FRACTION = 0;
    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;
    public static final int COMPLEX_UNIT_IN = 4;
    public static final int COMPLEX_UNIT_MASK = 15;
    public static final int COMPLEX_UNIT_MM = 5;
    public static final int COMPLEX_UNIT_PT = 3;
    public static final int COMPLEX_UNIT_PX = 0;
    public static final int COMPLEX_UNIT_SHIFT = 0;
    public static final int COMPLEX_UNIT_SP = 2;
    public static final int DATA_NULL_EMPTY = 1;
    public static final int DATA_NULL_UNDEFINED = 0;
    public static final int DENSITY_DEFAULT = 0;
    public static final int DENSITY_NONE = 65535;
    private static final float INCHES_PER_MM = 0.03937008f;
    private static final float INCHES_PER_PT = 0.013888889f;
    private static final float MANTISSA_MULT = 0.00390625f;
    public static final int TYPE_ATTRIBUTE = 2;
    public static final int TYPE_DIMENSION = 5;
    public static final int TYPE_FIRST_COLOR_INT = 28;
    public static final int TYPE_FIRST_INT = 16;
    public static final int TYPE_FLOAT = 4;
    public static final int TYPE_FRACTION = 6;
    public static final int TYPE_INT_BOOLEAN = 18;
    public static final int TYPE_INT_COLOR_ARGB4 = 30;
    public static final int TYPE_INT_COLOR_ARGB8 = 28;
    public static final int TYPE_INT_COLOR_RGB4 = 31;
    public static final int TYPE_INT_COLOR_RGB8 = 29;
    public static final int TYPE_INT_DEC = 16;
    public static final int TYPE_INT_HEX = 17;
    public static final int TYPE_LAST_COLOR_INT = 31;
    public static final int TYPE_LAST_INT = 31;
    public static final int TYPE_NULL = 0;
    public static final int TYPE_REFERENCE = 1;
    public static final int TYPE_STRING = 3;
    public int assetCookie;
    public int changingConfigurations = -1;
    public int data;
    public int density;
    public int resourceId;
    public int sourceResourceId;
    public CharSequence string;
    public int type;
    private static final float[] RADIX_MULTS = {0.00390625f, 3.0517578E-5f, 1.1920929E-7f, 4.656613E-10f};
    private static final String[] DIMENSION_UNIT_STRS = {"px", "dip", FullBackup.SHAREDPREFS_TREE_TOKEN, "pt", "in", "mm"};
    private static final String[] FRACTION_UNIT_STRS = {"%", "%p"};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ComplexDimensionUnit {
    }

    public final float getFloat() {
        return Float.intBitsToFloat(this.data);
    }

    public boolean isColorType() {
        int i = this.type;
        return i >= 28 && i <= 31;
    }

    public static float complexToFloat(int complex) {
        return (complex & (-256)) * RADIX_MULTS[(complex >> 4) & 3];
    }

    public static float complexToDimension(int data, DisplayMetrics metrics) {
        return applyDimension((data >> 0) & 15, complexToFloat(data), metrics);
    }

    public static int complexToDimensionPixelOffset(int data, DisplayMetrics metrics) {
        return (int) applyDimension((data >> 0) & 15, complexToFloat(data), metrics);
    }

    public static int complexToDimensionPixelSize(int data, DisplayMetrics metrics) {
        float value = complexToFloat(data);
        float f = applyDimension((data >> 0) & 15, value, metrics);
        int res = (int) (f >= 0.0f ? 0.5f + f : f - 0.5f);
        if (res != 0) {
            return res;
        }
        if (value == 0.0f) {
            return 0;
        }
        return value > 0.0f ? 1 : -1;
    }

    @Deprecated
    public static float complexToDimensionNoisy(int data, DisplayMetrics metrics) {
        return complexToDimension(data, metrics);
    }

    public int getComplexUnit() {
        return (this.data >> 0) & 15;
    }

    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case 0:
                return value;
            case 1:
                return metrics.density * value;
            case 2:
                if (metrics.fontScaleConverter != null) {
                    return applyDimension(1, metrics.fontScaleConverter.convertSpToDp(value), metrics);
                }
                return metrics.scaledDensity * value;
            case 3:
                return metrics.xdpi * value * INCHES_PER_PT;
            case 4:
                return metrics.xdpi * value;
            case 5:
                return metrics.xdpi * value * INCHES_PER_MM;
            default:
                return 0.0f;
        }
    }

    public static float deriveDimension(int unitToConvertTo, float pixelValue, DisplayMetrics metrics) {
        switch (unitToConvertTo) {
            case 0:
                return pixelValue;
            case 1:
                if (metrics.density == 0.0f) {
                    return 0.0f;
                }
                return pixelValue / metrics.density;
            case 2:
                if (metrics.fontScaleConverter == null) {
                    if (metrics.scaledDensity == 0.0f) {
                        return 0.0f;
                    }
                    return pixelValue / metrics.scaledDensity;
                }
                float dpValue = deriveDimension(1, pixelValue, metrics);
                return metrics.fontScaleConverter.convertDpToSp(dpValue);
            case 3:
                if (metrics.xdpi == 0.0f) {
                    return 0.0f;
                }
                return (pixelValue / metrics.xdpi) / INCHES_PER_PT;
            case 4:
                if (metrics.xdpi == 0.0f) {
                    return 0.0f;
                }
                return pixelValue / metrics.xdpi;
            case 5:
                if (metrics.xdpi == 0.0f) {
                    return 0.0f;
                }
                return (pixelValue / metrics.xdpi) / INCHES_PER_MM;
            default:
                throw new IllegalArgumentException("Invalid unitToConvertTo " + unitToConvertTo);
        }
    }

    public static float convertPixelsToDimension(int unitToConvertTo, float pixelValue, DisplayMetrics metrics) {
        return deriveDimension(unitToConvertTo, pixelValue, metrics);
    }

    public static float convertDimensionToPixels(int unitToConvertFrom, float value, DisplayMetrics metrics) {
        return applyDimension(unitToConvertFrom, value, metrics);
    }

    public float getDimension(DisplayMetrics metrics) {
        return complexToDimension(this.data, metrics);
    }

    private static int createComplex(int mantissa, int radix) {
        if (mantissa < -8388608 || mantissa >= 8388608) {
            throw new IllegalArgumentException("Magnitude of mantissa is too large: " + mantissa);
        }
        if (radix < 0 || radix > 3) {
            throw new IllegalArgumentException("Invalid radix: " + radix);
        }
        return ((16777215 & mantissa) << 8) | (radix << 4);
    }

    public static int intToComplex(int value) {
        if (value < -8388608 || value >= 8388608) {
            throw new IllegalArgumentException("Magnitude of the value is too large: " + value);
        }
        return createComplex(value, 0);
    }

    public static int floatToComplex(float value) {
        if (value < -8388608.0f || value >= 8388607.5f) {
            throw new IllegalArgumentException("Magnitude of the value is too large: " + value);
        }
        try {
            if (value == ((int) value)) {
                return createComplex((int) value, 0);
            }
            float absValue = Math.abs(value);
            if (absValue < 1.0f) {
                return createComplex(Math.round(8388608.0f * value), 3);
            }
            if (absValue < 256.0f) {
                return createComplex(Math.round(32768.0f * value), 2);
            }
            if (absValue < 65536.0f) {
                return createComplex(Math.round(128.0f * value), 1);
            }
            return createComplex(Math.round(value), 0);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unable to convert value to complex: " + value, ex);
        }
    }

    public static int createComplexDimension(int value, int units) {
        if (units < 0 || units > 5) {
            throw new IllegalArgumentException("Must be a valid COMPLEX_UNIT_*: " + units);
        }
        return intToComplex(value) | units;
    }

    public static int createComplexDimension(float value, int units) {
        if (units < 0 || units > 5) {
            throw new IllegalArgumentException("Must be a valid COMPLEX_UNIT_*: " + units);
        }
        return floatToComplex(value) | units;
    }

    public static float complexToFraction(int data, float base, float pbase) {
        switch ((data >> 0) & 15) {
            case 0:
                return complexToFloat(data) * base;
            case 1:
                return complexToFloat(data) * pbase;
            default:
                return 0.0f;
        }
    }

    public float getFraction(float base, float pbase) {
        return complexToFraction(this.data, base, pbase);
    }

    public final CharSequence coerceToString() {
        int t = this.type;
        if (t == 3) {
            return this.string;
        }
        return coerceToString(t, this.data);
    }

    public static final String coerceToString(int type, int data) {
        switch (type) {
            case 0:
                return null;
            case 1:
                return "@" + data;
            case 2:
                return "?" + data;
            case 4:
                return Float.toString(Float.intBitsToFloat(data));
            case 5:
                return Float.toString(complexToFloat(data)) + DIMENSION_UNIT_STRS[(data >> 0) & 15];
            case 6:
                return Float.toString(complexToFloat(data) * 100.0f) + FRACTION_UNIT_STRS[(data >> 0) & 15];
            case 17:
                return "0x" + Integer.toHexString(data);
            case 18:
                return data != 0 ? "true" : "false";
            default:
                if (type >= 28 && type <= 31) {
                    return "#" + Integer.toHexString(data);
                }
                if (type < 16 || type > 31) {
                    return null;
                }
                return Integer.toString(data);
        }
    }

    public void setTo(TypedValue other) {
        this.type = other.type;
        this.string = other.string;
        this.data = other.data;
        this.assetCookie = other.assetCookie;
        this.resourceId = other.resourceId;
        this.density = other.density;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypedValue{t=0x").append(Integer.toHexString(this.type));
        sb.append("/d=0x").append(Integer.toHexString(this.data));
        if (this.type == 3) {
            StringBuilder append = sb.append(" \"");
            CharSequence charSequence = this.string;
            if (charSequence == null) {
                charSequence = "<null>";
            }
            append.append(charSequence).append("\"");
        }
        if (this.assetCookie != 0) {
            sb.append(" a=").append(this.assetCookie);
        }
        if (this.resourceId != 0) {
            sb.append(" r=0x").append(Integer.toHexString(this.resourceId));
        }
        sb.append("}");
        return sb.toString();
    }
}
