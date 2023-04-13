package android.util;

import libcore.util.FP16;
/* loaded from: classes3.dex */
public final class Half extends Number implements Comparable<Half> {
    public static final short EPSILON = 5120;
    public static final short LOWEST_VALUE = -1025;
    public static final int MAX_EXPONENT = 15;
    public static final short MAX_VALUE = 31743;
    public static final int MIN_EXPONENT = -14;
    public static final short MIN_NORMAL = 1024;
    public static final short MIN_VALUE = 1;
    public static final short NEGATIVE_INFINITY = -1024;
    public static final short NEGATIVE_ZERO = Short.MIN_VALUE;
    public static final short NaN = 32256;
    public static final short POSITIVE_INFINITY = 31744;
    public static final short POSITIVE_ZERO = 0;
    public static final int SIZE = 16;
    private final short mValue;

    public Half(short value) {
        this.mValue = value;
    }

    public Half(float value) {
        this.mValue = toHalf(value);
    }

    public Half(double value) {
        this.mValue = toHalf((float) value);
    }

    public Half(String value) throws NumberFormatException {
        this.mValue = toHalf(Float.parseFloat(value));
    }

    public short halfValue() {
        return this.mValue;
    }

    @Override // java.lang.Number
    public byte byteValue() {
        return (byte) toFloat(this.mValue);
    }

    @Override // java.lang.Number
    public short shortValue() {
        return (short) toFloat(this.mValue);
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) toFloat(this.mValue);
    }

    @Override // java.lang.Number
    public long longValue() {
        return toFloat(this.mValue);
    }

    @Override // java.lang.Number
    public float floatValue() {
        return toFloat(this.mValue);
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return toFloat(this.mValue);
    }

    public boolean isNaN() {
        return isNaN(this.mValue);
    }

    public boolean equals(Object o) {
        return (o instanceof Half) && halfToIntBits(((Half) o).mValue) == halfToIntBits(this.mValue);
    }

    public int hashCode() {
        return hashCode(this.mValue);
    }

    public String toString() {
        return toString(this.mValue);
    }

    @Override // java.lang.Comparable
    public int compareTo(Half h) {
        return compare(this.mValue, h.mValue);
    }

    public static int hashCode(short h) {
        return halfToIntBits(h);
    }

    public static int compare(short x, short y) {
        return FP16.compare(x, y);
    }

    public static short halfToShortBits(short h) {
        return (h & Short.MAX_VALUE) > 31744 ? NaN : h;
    }

    public static int halfToIntBits(short h) {
        if ((h & Short.MAX_VALUE) > 31744) {
            return 32256;
        }
        return 65535 & h;
    }

    public static int halfToRawIntBits(short h) {
        return 65535 & h;
    }

    public static short intBitsToHalf(int bits) {
        return (short) (65535 & bits);
    }

    public static short copySign(short magnitude, short sign) {
        return (short) ((32768 & sign) | (magnitude & Short.MAX_VALUE));
    }

    public static short abs(short h) {
        return (short) (h & Short.MAX_VALUE);
    }

    public static short round(short h) {
        return FP16.rint(h);
    }

    public static short ceil(short h) {
        return FP16.ceil(h);
    }

    public static short floor(short h) {
        return FP16.floor(h);
    }

    public static short trunc(short h) {
        return FP16.trunc(h);
    }

    public static short min(short x, short y) {
        return FP16.min(x, y);
    }

    public static short max(short x, short y) {
        return FP16.max(x, y);
    }

    public static boolean less(short x, short y) {
        return FP16.less(x, y);
    }

    public static boolean lessEquals(short x, short y) {
        return FP16.lessEquals(x, y);
    }

    public static boolean greater(short x, short y) {
        return FP16.greater(x, y);
    }

    public static boolean greaterEquals(short x, short y) {
        return FP16.greaterEquals(x, y);
    }

    public static boolean equals(short x, short y) {
        return FP16.equals(x, y);
    }

    public static int getSign(short h) {
        return (32768 & h) == 0 ? 1 : -1;
    }

    public static int getExponent(short h) {
        return ((h >>> 10) & 31) - 15;
    }

    public static int getSignificand(short h) {
        return h & 1023;
    }

    public static boolean isInfinite(short h) {
        return FP16.isInfinite(h);
    }

    public static boolean isNaN(short h) {
        return FP16.isNaN(h);
    }

    public static boolean isNormalized(short h) {
        return FP16.isNormalized(h);
    }

    public static float toFloat(short h) {
        return FP16.toFloat(h);
    }

    public static short toHalf(float f) {
        return FP16.toHalf(f);
    }

    public static Half valueOf(short h) {
        return new Half(h);
    }

    public static Half valueOf(float f) {
        return new Half(f);
    }

    public static Half valueOf(String s) {
        return new Half(s);
    }

    public static short parseHalf(String s) throws NumberFormatException {
        return toHalf(Float.parseFloat(s));
    }

    public static String toString(short h) {
        return Float.toString(toFloat(h));
    }

    public static String toHexString(short h) {
        return FP16.toHexString(h);
    }
}
