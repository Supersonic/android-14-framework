package com.android.internal.org.bouncycastle.math.p025ec;

import android.media.MediaMetrics;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.SimpleBigDecimal */
/* loaded from: classes4.dex */
class SimpleBigDecimal {
    private static final long serialVersionUID = 1;
    private final BigInteger bigInt;
    private final int scale;

    public static SimpleBigDecimal getInstance(BigInteger value, int scale) {
        return new SimpleBigDecimal(value.shiftLeft(scale), scale);
    }

    public SimpleBigDecimal(BigInteger bigInt, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("scale may not be negative");
        }
        this.bigInt = bigInt;
        this.scale = scale;
    }

    private void checkScale(SimpleBigDecimal b) {
        if (this.scale != b.scale) {
            throw new IllegalArgumentException("Only SimpleBigDecimal of same scale allowed in arithmetic operations");
        }
    }

    public SimpleBigDecimal adjustScale(int newScale) {
        if (newScale < 0) {
            throw new IllegalArgumentException("scale may not be negative");
        }
        int i = this.scale;
        if (newScale == i) {
            return this;
        }
        return new SimpleBigDecimal(this.bigInt.shiftLeft(newScale - i), newScale);
    }

    public SimpleBigDecimal add(SimpleBigDecimal b) {
        checkScale(b);
        return new SimpleBigDecimal(this.bigInt.add(b.bigInt), this.scale);
    }

    public SimpleBigDecimal add(BigInteger b) {
        return new SimpleBigDecimal(this.bigInt.add(b.shiftLeft(this.scale)), this.scale);
    }

    public SimpleBigDecimal negate() {
        return new SimpleBigDecimal(this.bigInt.negate(), this.scale);
    }

    public SimpleBigDecimal subtract(SimpleBigDecimal b) {
        return add(b.negate());
    }

    public SimpleBigDecimal subtract(BigInteger b) {
        return new SimpleBigDecimal(this.bigInt.subtract(b.shiftLeft(this.scale)), this.scale);
    }

    public SimpleBigDecimal multiply(SimpleBigDecimal b) {
        checkScale(b);
        BigInteger multiply = this.bigInt.multiply(b.bigInt);
        int i = this.scale;
        return new SimpleBigDecimal(multiply, i + i);
    }

    public SimpleBigDecimal multiply(BigInteger b) {
        return new SimpleBigDecimal(this.bigInt.multiply(b), this.scale);
    }

    public SimpleBigDecimal divide(SimpleBigDecimal b) {
        checkScale(b);
        BigInteger dividend = this.bigInt.shiftLeft(this.scale);
        return new SimpleBigDecimal(dividend.divide(b.bigInt), this.scale);
    }

    public SimpleBigDecimal divide(BigInteger b) {
        return new SimpleBigDecimal(this.bigInt.divide(b), this.scale);
    }

    public SimpleBigDecimal shiftLeft(int n) {
        return new SimpleBigDecimal(this.bigInt.shiftLeft(n), this.scale);
    }

    public int compareTo(SimpleBigDecimal val) {
        checkScale(val);
        return this.bigInt.compareTo(val.bigInt);
    }

    public int compareTo(BigInteger val) {
        return this.bigInt.compareTo(val.shiftLeft(this.scale));
    }

    public BigInteger floor() {
        return this.bigInt.shiftRight(this.scale);
    }

    public BigInteger round() {
        SimpleBigDecimal oneHalf = new SimpleBigDecimal(ECConstants.ONE, 1);
        return add(oneHalf.adjustScale(this.scale)).floor();
    }

    public int intValue() {
        return floor().intValue();
    }

    public long longValue() {
        return floor().longValue();
    }

    public int getScale() {
        return this.scale;
    }

    public String toString() {
        if (this.scale == 0) {
            return this.bigInt.toString();
        }
        BigInteger floorBigInt = floor();
        BigInteger fract = this.bigInt.subtract(floorBigInt.shiftLeft(this.scale));
        if (this.bigInt.signum() == -1) {
            fract = ECConstants.ONE.shiftLeft(this.scale).subtract(fract);
        }
        if (floorBigInt.signum() == -1 && !fract.equals(ECConstants.ZERO)) {
            floorBigInt = floorBigInt.add(ECConstants.ONE);
        }
        String leftOfPoint = floorBigInt.toString();
        char[] fractCharArr = new char[this.scale];
        String fractStr = fract.toString(2);
        int fractLen = fractStr.length();
        int zeroes = this.scale - fractLen;
        for (int i = 0; i < zeroes; i++) {
            fractCharArr[i] = '0';
        }
        for (int j = 0; j < fractLen; j++) {
            fractCharArr[zeroes + j] = fractStr.charAt(j);
        }
        String rightOfPoint = new String(fractCharArr);
        StringBuffer sb = new StringBuffer(leftOfPoint);
        sb.append(MediaMetrics.SEPARATOR);
        sb.append(rightOfPoint);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SimpleBigDecimal) {
            SimpleBigDecimal other = (SimpleBigDecimal) o;
            return this.bigInt.equals(other.bigInt) && this.scale == other.scale;
        }
        return false;
    }

    public int hashCode() {
        return this.bigInt.hashCode() ^ this.scale;
    }
}
