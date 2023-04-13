package com.android.internal.org.bouncycastle.math.raw;

import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public abstract class Nat192 {

    /* renamed from: M */
    private static final long f898M = 4294967295L;

    public static int add(int[] x, int[] y, int[] z) {
        long c = 0 + (x[0] & 4294967295L) + (y[0] & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (x[1] & 4294967295L) + (y[1] & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (x[2] & 4294967295L) + (y[2] & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (x[3] & 4294967295L) + (y[3] & 4294967295L);
        z[3] = (int) c4;
        long c5 = (c4 >>> 32) + (x[4] & 4294967295L) + (y[4] & 4294967295L);
        z[4] = (int) c5;
        long c6 = (c5 >>> 32) + (x[5] & 4294967295L) + (y[5] & 4294967295L);
        z[5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static int addBothTo(int[] x, int[] y, int[] z) {
        long c = 0 + (x[0] & 4294967295L) + (y[0] & 4294967295L) + (z[0] & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (x[1] & 4294967295L) + (y[1] & 4294967295L) + (z[1] & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (x[2] & 4294967295L) + (y[2] & 4294967295L) + (z[2] & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (x[3] & 4294967295L) + (y[3] & 4294967295L) + (z[3] & 4294967295L);
        z[3] = (int) c4;
        long c5 = (c4 >>> 32) + (x[4] & 4294967295L) + (y[4] & 4294967295L) + (z[4] & 4294967295L);
        z[4] = (int) c5;
        long c6 = (c5 >>> 32) + (x[5] & 4294967295L) + (y[5] & 4294967295L) + (z[5] & 4294967295L);
        z[5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static int addTo(int[] x, int[] z) {
        long c = 0 + (x[0] & 4294967295L) + (z[0] & 4294967295L);
        z[0] = (int) c;
        long c2 = (c >>> 32) + (x[1] & 4294967295L) + (z[1] & 4294967295L);
        z[1] = (int) c2;
        long c3 = (c2 >>> 32) + (x[2] & 4294967295L) + (z[2] & 4294967295L);
        z[2] = (int) c3;
        long c4 = (c3 >>> 32) + (x[3] & 4294967295L) + (z[3] & 4294967295L);
        z[3] = (int) c4;
        long c5 = (c4 >>> 32) + (x[4] & 4294967295L) + (z[4] & 4294967295L);
        z[4] = (int) c5;
        long c6 = (c5 >>> 32) + (x[5] & 4294967295L) + (z[5] & 4294967295L);
        z[5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static int addTo(int[] x, int xOff, int[] z, int zOff, int cIn) {
        long c = (cIn & 4294967295L) + (x[xOff + 0] & 4294967295L) + (z[zOff + 0] & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + (x[xOff + 1] & 4294967295L) + (z[zOff + 1] & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (x[xOff + 2] & 4294967295L) + (z[zOff + 2] & 4294967295L);
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (x[xOff + 3] & 4294967295L) + (z[zOff + 3] & 4294967295L);
        z[zOff + 3] = (int) c4;
        long c5 = (c4 >>> 32) + (x[xOff + 4] & 4294967295L) + (z[zOff + 4] & 4294967295L);
        z[zOff + 4] = (int) c5;
        long c6 = (c5 >>> 32) + (x[xOff + 5] & 4294967295L) + (4294967295L & z[zOff + 5]);
        z[zOff + 5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static int addToEachOther(int[] u, int uOff, int[] v, int vOff) {
        long c = 0 + (u[uOff + 0] & 4294967295L) + (v[vOff + 0] & 4294967295L);
        u[uOff + 0] = (int) c;
        v[vOff + 0] = (int) c;
        long c2 = (c >>> 32) + (u[uOff + 1] & 4294967295L) + (v[vOff + 1] & 4294967295L);
        u[uOff + 1] = (int) c2;
        v[vOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (u[uOff + 2] & 4294967295L) + (v[vOff + 2] & 4294967295L);
        u[uOff + 2] = (int) c3;
        v[vOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (u[uOff + 3] & 4294967295L) + (v[vOff + 3] & 4294967295L);
        u[uOff + 3] = (int) c4;
        v[vOff + 3] = (int) c4;
        long c5 = (c4 >>> 32) + (u[uOff + 4] & 4294967295L) + (v[vOff + 4] & 4294967295L);
        u[uOff + 4] = (int) c5;
        v[vOff + 4] = (int) c5;
        long c6 = (c5 >>> 32) + (u[uOff + 5] & 4294967295L) + (v[vOff + 5] & 4294967295L);
        u[uOff + 5] = (int) c6;
        v[vOff + 5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static void copy(int[] x, int[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
        z[4] = x[4];
        z[5] = x[5];
    }

    public static void copy(int[] x, int xOff, int[] z, int zOff) {
        z[zOff + 0] = x[xOff + 0];
        z[zOff + 1] = x[xOff + 1];
        z[zOff + 2] = x[xOff + 2];
        z[zOff + 3] = x[xOff + 3];
        z[zOff + 4] = x[xOff + 4];
        z[zOff + 5] = x[xOff + 5];
    }

    public static void copy64(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
    }

    public static void copy64(long[] x, int xOff, long[] z, int zOff) {
        z[zOff + 0] = x[xOff + 0];
        z[zOff + 1] = x[xOff + 1];
        z[zOff + 2] = x[xOff + 2];
    }

    public static int[] create() {
        return new int[6];
    }

    public static long[] create64() {
        return new long[3];
    }

    public static int[] createExt() {
        return new int[12];
    }

    public static long[] createExt64() {
        return new long[6];
    }

    public static boolean diff(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        boolean pos = gte(x, xOff, y, yOff);
        if (pos) {
            sub(x, xOff, y, yOff, z, zOff);
        } else {
            sub(y, yOff, x, xOff, z, zOff);
        }
        return pos;
    }

    /* renamed from: eq */
    public static boolean m41eq(int[] x, int[] y) {
        for (int i = 5; i >= 0; i--) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean eq64(long[] x, long[] y) {
        for (int i = 2; i >= 0; i--) {
            if (x[i] != y[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] fromBigInteger(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 192) {
            throw new IllegalArgumentException();
        }
        int[] z = create();
        for (int i = 0; i < 6; i++) {
            z[i] = x.intValue();
            x = x.shiftRight(32);
        }
        return z;
    }

    public static long[] fromBigInteger64(BigInteger x) {
        if (x.signum() < 0 || x.bitLength() > 192) {
            throw new IllegalArgumentException();
        }
        long[] z = create64();
        for (int i = 0; i < 3; i++) {
            z[i] = x.longValue();
            x = x.shiftRight(64);
        }
        return z;
    }

    public static int getBit(int[] x, int bit) {
        if (bit == 0) {
            return x[0] & 1;
        }
        int w = bit >> 5;
        if (w < 0 || w >= 6) {
            return 0;
        }
        int b = bit & 31;
        return (x[w] >>> b) & 1;
    }

    public static boolean gte(int[] x, int[] y) {
        for (int i = 5; i >= 0; i--) {
            int x_i = x[i] ^ Integer.MIN_VALUE;
            int y_i = Integer.MIN_VALUE ^ y[i];
            if (x_i < y_i) {
                return false;
            }
            if (x_i > y_i) {
                return true;
            }
        }
        return true;
    }

    public static boolean gte(int[] x, int xOff, int[] y, int yOff) {
        for (int i = 5; i >= 0; i--) {
            int x_i = x[xOff + i] ^ Integer.MIN_VALUE;
            int y_i = Integer.MIN_VALUE ^ y[yOff + i];
            if (x_i < y_i) {
                return false;
            }
            if (x_i > y_i) {
                return true;
            }
        }
        return true;
    }

    public static boolean isOne(int[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < 6; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOne64(long[] x) {
        if (x[0] != 1) {
            return false;
        }
        for (int i = 1; i < 3; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero(int[] x) {
        for (int i = 0; i < 6; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZero64(long[] x) {
        for (int i = 0; i < 3; i++) {
            if (x[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static void mul(int[] x, int[] y, int[] zz) {
        long y_0 = y[0] & 4294967295L;
        long y_1 = y[1] & 4294967295L;
        long y_2 = y[2] & 4294967295L;
        long y_3 = y[3] & 4294967295L;
        long y_4 = y[4] & 4294967295L;
        long y_5 = y[5] & 4294967295L;
        long x_0 = x[0] & 4294967295L;
        long c = 0 + (x_0 * y_0);
        zz[0] = (int) c;
        long c2 = (c >>> 32) + (x_0 * y_1);
        zz[1] = (int) c2;
        long c3 = (c2 >>> 32) + (x_0 * y_2);
        zz[2] = (int) c3;
        long c4 = (c3 >>> 32) + (x_0 * y_3);
        zz[3] = (int) c4;
        long c5 = (c4 >>> 32) + (x_0 * y_4);
        zz[4] = (int) c5;
        long c6 = (c5 >>> 32) + (x_0 * y_5);
        zz[5] = (int) c6;
        zz[6] = (int) (c6 >>> 32);
        int i = 1;
        for (int i2 = 6; i < i2; i2 = 6) {
            long x_i = x[i] & 4294967295L;
            long y_52 = y_5;
            long c7 = 0 + (x_i * y_0) + (zz[i + 0] & 4294967295L);
            zz[i + 0] = (int) c7;
            long y_12 = y_1;
            long c8 = (c7 >>> 32) + (x_i * y_1) + (zz[i + 1] & 4294967295L);
            zz[i + 1] = (int) c8;
            long y_42 = y_4;
            long c9 = (c8 >>> 32) + (x_i * y_2) + (zz[i + 2] & 4294967295L);
            zz[i + 2] = (int) c9;
            long c10 = (c9 >>> 32) + (x_i * y_3) + (zz[i + 3] & 4294967295L);
            zz[i + 3] = (int) c10;
            long c11 = (c10 >>> 32) + (x_i * y_42) + (zz[i + 4] & 4294967295L);
            zz[i + 4] = (int) c11;
            long c12 = (c11 >>> 32) + (x_i * y_52) + (zz[i + 5] & 4294967295L);
            zz[i + 5] = (int) c12;
            zz[i + 6] = (int) (c12 >>> 32);
            i++;
            y_1 = y_12;
            y_5 = y_52;
            y_4 = y_42;
        }
    }

    public static void mul(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = y[yOff + 0] & 4294967295L;
        long y_1 = y[yOff + 1] & 4294967295L;
        long y_2 = y[yOff + 2] & 4294967295L;
        long y_3 = y[yOff + 3] & 4294967295L;
        long y_4 = y[yOff + 4] & 4294967295L;
        long y_5 = y[yOff + 5] & 4294967295L;
        long x_0 = x[xOff + 0] & 4294967295L;
        long c = 0 + (x_0 * y_0);
        zz[zzOff + 0] = (int) c;
        long c2 = (c >>> 32) + (x_0 * y_1);
        zz[zzOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (x_0 * y_2);
        zz[zzOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (x_0 * y_3);
        zz[zzOff + 3] = (int) c4;
        long c5 = (c4 >>> 32) + (x_0 * y_4);
        zz[zzOff + 4] = (int) c5;
        long c6 = (c5 >>> 32) + (x_0 * y_5);
        zz[zzOff + 5] = (int) c6;
        zz[zzOff + 6] = (int) (c6 >>> 32);
        int i = 1;
        int zzOff2 = zzOff;
        while (i < 6) {
            zzOff2++;
            long x_i = x[xOff + i] & 4294967295L;
            long c7 = 0 + (x_i * y_0) + (zz[zzOff2 + 0] & 4294967295L);
            zz[zzOff2 + 0] = (int) c7;
            long y_12 = y_1;
            long c8 = (c7 >>> 32) + (x_i * y_1) + (zz[zzOff2 + 1] & 4294967295L);
            zz[zzOff2 + 1] = (int) c8;
            long c9 = (c8 >>> 32) + (x_i * y_2) + (zz[zzOff2 + 2] & 4294967295L);
            zz[zzOff2 + 2] = (int) c9;
            long c10 = (c9 >>> 32) + (x_i * y_3) + (zz[zzOff2 + 3] & 4294967295L);
            zz[zzOff2 + 3] = (int) c10;
            long c11 = (c10 >>> 32) + (x_i * y_4) + (zz[zzOff2 + 4] & 4294967295L);
            zz[zzOff2 + 4] = (int) c11;
            long c12 = (c11 >>> 32) + (x_i * y_5) + (zz[zzOff2 + 5] & 4294967295L);
            zz[zzOff2 + 5] = (int) c12;
            zz[zzOff2 + 6] = (int) (c12 >>> 32);
            i++;
            y_1 = y_12;
            y_0 = y_0;
        }
    }

    public static int mulAddTo(int[] x, int[] y, int[] zz) {
        long y_0 = y[0] & 4294967295L;
        long y_1 = y[1] & 4294967295L;
        long y_2 = y[2] & 4294967295L;
        long y_3 = y[3] & 4294967295L;
        long y_4 = y[4] & 4294967295L;
        long y_5 = y[5] & 4294967295L;
        long x_i = 0;
        int i = 0;
        while (i < 6) {
            long zc = x_i;
            long zc2 = x[i];
            long x_i2 = zc2 & 4294967295L;
            long j = x_i2 * y_0;
            long y_02 = y_0;
            long y_03 = zz[i + 0];
            long c = 0 + j + (y_03 & 4294967295L);
            long y_52 = y_5;
            zz[i + 0] = (int) c;
            long c2 = (c >>> 32) + (x_i2 * y_1) + (zz[i + 1] & 4294967295L);
            zz[i + 1] = (int) c2;
            long c3 = (c2 >>> 32) + (x_i2 * y_2) + (zz[i + 2] & 4294967295L);
            zz[i + 2] = (int) c3;
            long c4 = (c3 >>> 32) + (x_i2 * y_3) + (zz[i + 3] & 4294967295L);
            zz[i + 3] = (int) c4;
            long c5 = (c4 >>> 32) + (x_i2 * y_4) + (zz[i + 4] & 4294967295L);
            zz[i + 4] = (int) c5;
            long c6 = (c5 >>> 32) + (x_i2 * y_52) + (zz[i + 5] & 4294967295L);
            zz[i + 5] = (int) c6;
            long zc3 = zc + (zz[i + 6] & 4294967295L) + (c6 >>> 32);
            zz[i + 6] = (int) zc3;
            x_i = zc3 >>> 32;
            i++;
            y_5 = y_52;
            y_0 = y_02;
            y_1 = y_1;
        }
        return (int) x_i;
    }

    public static int mulAddTo(int[] x, int xOff, int[] y, int yOff, int[] zz, int zzOff) {
        long y_0 = y[yOff + 0] & 4294967295L;
        long y_1 = y[yOff + 1] & 4294967295L;
        long y_2 = y[yOff + 2] & 4294967295L;
        long y_3 = y[yOff + 3] & 4294967295L;
        long y_4 = y[yOff + 4] & 4294967295L;
        long y_5 = y[yOff + 5] & 4294967295L;
        long zc = 0;
        int i = 0;
        int zzOff2 = zzOff;
        while (i < 6) {
            long y_52 = y_5;
            long y_53 = x[xOff + i];
            long x_i = y_53 & 4294967295L;
            long c = 0 + (x_i * y_0) + (zz[zzOff2 + 0] & 4294967295L);
            zz[zzOff2 + 0] = (int) c;
            long c2 = (c >>> 32) + (x_i * y_1) + (zz[zzOff2 + 1] & 4294967295L);
            zz[zzOff2 + 1] = (int) c2;
            long c3 = (c2 >>> 32) + (x_i * y_2) + (zz[zzOff2 + 2] & 4294967295L);
            zz[zzOff2 + 2] = (int) c3;
            long c4 = (c3 >>> 32) + (x_i * y_3) + (zz[zzOff2 + 3] & 4294967295L);
            zz[zzOff2 + 3] = (int) c4;
            long c5 = (c4 >>> 32) + (x_i * y_4) + (zz[zzOff2 + 4] & 4294967295L);
            zz[zzOff2 + 4] = (int) c5;
            long c6 = (c5 >>> 32) + (x_i * y_52) + (zz[zzOff2 + 5] & 4294967295L);
            zz[zzOff2 + 5] = (int) c6;
            long zc2 = (zz[zzOff2 + 6] & 4294967295L) + (c6 >>> 32) + zc;
            zz[zzOff2 + 6] = (int) zc2;
            zc = zc2 >>> 32;
            zzOff2++;
            y_2 = y_2;
            y_5 = y_52;
            y_1 = y_1;
            i++;
            y_0 = y_0;
        }
        return (int) zc;
    }

    public static long mul33Add(int w, int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long wVal = w & 4294967295L;
        long x0 = x[xOff + 0] & 4294967295L;
        long c = 0 + (wVal * x0) + (y[yOff + 0] & 4294967295L);
        z[zOff + 0] = (int) c;
        long x1 = x[xOff + 1] & 4294967295L;
        long c2 = (c >>> 32) + (wVal * x1) + x0 + (y[yOff + 1] & 4294967295L);
        z[zOff + 1] = (int) c2;
        long x2 = x[xOff + 2] & 4294967295L;
        long c3 = (c2 >>> 32) + (wVal * x2) + x1 + (y[yOff + 2] & 4294967295L);
        z[zOff + 2] = (int) c3;
        long x3 = x[xOff + 3] & 4294967295L;
        long c4 = (c3 >>> 32) + (wVal * x3) + x2 + (y[yOff + 3] & 4294967295L);
        z[zOff + 3] = (int) c4;
        long x4 = x[xOff + 4] & 4294967295L;
        long c5 = (c4 >>> 32) + (wVal * x4) + x3 + (y[yOff + 4] & 4294967295L);
        z[zOff + 4] = (int) c5;
        long x5 = x[xOff + 5] & 4294967295L;
        long c6 = (c5 >>> 32) + (wVal * x5) + x4 + (y[yOff + 5] & 4294967295L);
        z[zOff + 5] = (int) c6;
        return (c6 >>> 32) + x5;
    }

    public static int mulWordAddExt(int x, int[] yy, int yyOff, int[] zz, int zzOff) {
        long xVal = x & 4294967295L;
        long c = 0 + ((yy[yyOff + 0] & 4294967295L) * xVal) + (zz[zzOff + 0] & 4294967295L);
        zz[zzOff + 0] = (int) c;
        long c2 = (c >>> 32) + ((yy[yyOff + 1] & 4294967295L) * xVal) + (zz[zzOff + 1] & 4294967295L);
        zz[zzOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + ((yy[yyOff + 2] & 4294967295L) * xVal) + (zz[zzOff + 2] & 4294967295L);
        zz[zzOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + ((yy[yyOff + 3] & 4294967295L) * xVal) + (zz[zzOff + 3] & 4294967295L);
        zz[zzOff + 3] = (int) c4;
        long c5 = (c4 >>> 32) + ((yy[yyOff + 4] & 4294967295L) * xVal) + (zz[zzOff + 4] & 4294967295L);
        zz[zzOff + 4] = (int) c5;
        long c6 = (c5 >>> 32) + ((yy[yyOff + 5] & 4294967295L) * xVal) + (4294967295L & zz[zzOff + 5]);
        zz[zzOff + 5] = (int) c6;
        return (int) (c6 >>> 32);
    }

    public static int mul33DWordAdd(int x, long y, int[] z, int zOff) {
        long xVal = x & 4294967295L;
        long y00 = y & 4294967295L;
        long c = 0 + (xVal * y00) + (z[zOff + 0] & 4294967295L);
        z[zOff + 0] = (int) c;
        long y01 = y >>> 32;
        long c2 = (c >>> 32) + (xVal * y01) + y00 + (z[zOff + 1] & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (z[zOff + 2] & 4294967295L) + y01;
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >>> 32) + (z[zOff + 3] & 4294967295L);
        z[zOff + 3] = (int) c4;
        if ((c4 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(6, z, zOff, 4);
    }

    public static int mul33WordAdd(int x, int y, int[] z, int zOff) {
        long xVal = x & 4294967295L;
        long yVal = y & 4294967295L;
        long c = 0 + (yVal * xVal) + (z[zOff + 0] & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + (z[zOff + 1] & 4294967295L) + yVal;
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (4294967295L & z[zOff + 2]);
        z[zOff + 2] = (int) c3;
        if ((c3 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(6, z, zOff, 3);
    }

    public static int mulWordDwordAdd(int x, long y, int[] z, int zOff) {
        long xVal = x & 4294967295L;
        long c = 0 + ((y & 4294967295L) * xVal) + (z[zOff + 0] & 4294967295L);
        z[zOff + 0] = (int) c;
        long c2 = (c >>> 32) + ((y >>> 32) * xVal) + (z[zOff + 1] & 4294967295L);
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >>> 32) + (4294967295L & z[zOff + 2]);
        z[zOff + 2] = (int) c3;
        if ((c3 >>> 32) == 0) {
            return 0;
        }
        return Nat.incAt(6, z, zOff, 3);
    }

    public static int mulWord(int x, int[] y, int[] z, int zOff) {
        long c = 0;
        long xVal = x & 4294967295L;
        int i = 0;
        do {
            long c2 = c + ((y[i] & 4294967295L) * xVal);
            z[zOff + i] = (int) c2;
            c = c2 >>> 32;
            i++;
        } while (i < 6);
        return (int) c;
    }

    public static void square(int[] x, int[] zz) {
        long x_0 = x[0] & 4294967295L;
        int c = 0;
        int i = 5;
        int j = 12;
        while (true) {
            int i2 = i - 1;
            long xVal = x[i] & 4294967295L;
            long p = xVal * xVal;
            int j2 = j - 1;
            zz[j2] = (c << 31) | ((int) (p >>> 33));
            j = j2 - 1;
            zz[j] = (int) (p >>> 1);
            c = (int) p;
            if (i2 <= 0) {
                long p2 = x_0 * x_0;
                long zz_1 = ((c << 31) & 4294967295L) | (p2 >>> 33);
                zz[0] = (int) p2;
                int c2 = ((int) (p2 >>> 32)) & 1;
                int j3 = x[1];
                long x_1 = j3 & 4294967295L;
                long zz_12 = zz_1 + (x_1 * x_0);
                int w = (int) zz_12;
                zz[1] = (w << 1) | c2;
                int c3 = w >>> 31;
                long x_2 = x[2] & 4294967295L;
                long zz_2 = (zz[2] & 4294967295L) + (zz_12 >>> 32) + (x_2 * x_0);
                int w2 = (int) zz_2;
                zz[2] = (w2 << 1) | c3;
                int c4 = w2 >>> 31;
                long zz_3 = (zz[3] & 4294967295L) + (zz_2 >>> 32) + (x_2 * x_1);
                long zz_4 = (zz[4] & 4294967295L) + (zz_3 >>> 32);
                long x_3 = x[3] & 4294967295L;
                long zz_5 = (zz[5] & 4294967295L) + (zz_4 >>> 32);
                long zz_6 = (zz[6] & 4294967295L) + (zz_5 >>> 32);
                long zz_32 = (zz_3 & 4294967295L) + (x_3 * x_0);
                int w3 = (int) zz_32;
                zz[3] = (w3 << 1) | c4;
                int c5 = w3 >>> 31;
                long zz_42 = (zz_4 & 4294967295L) + (zz_32 >>> 32) + (x_3 * x_1);
                long zz_52 = (zz_5 & 4294967295L) + (zz_42 >>> 32) + (x_3 * x_2);
                long zz_62 = zz_6 + (zz_52 >>> 32);
                long x_4 = x[4] & 4294967295L;
                long zz_7 = (zz[7] & 4294967295L) + (zz_62 >>> 32);
                long zz_8 = (zz[8] & 4294967295L) + (zz_7 >>> 32);
                long zz_43 = (zz_42 & 4294967295L) + (x_4 * x_0);
                int w4 = (int) zz_43;
                zz[4] = (w4 << 1) | c5;
                int c6 = w4 >>> 31;
                long zz_53 = (zz_52 & 4294967295L) + (zz_43 >>> 32) + (x_4 * x_1);
                long zz_63 = (zz_62 & 4294967295L) + (zz_53 >>> 32) + (x_4 * x_2);
                long zz_72 = (zz_7 & 4294967295L) + (zz_63 >>> 32) + (x_4 * x_3);
                long zz_64 = zz_63 & 4294967295L;
                long zz_65 = zz_72 >>> 32;
                long zz_82 = zz_8 + zz_65;
                long x_5 = x[5] & 4294967295L;
                long zz_9 = (zz[9] & 4294967295L) + (zz_82 >>> 32);
                long zz_10 = (zz[10] & 4294967295L) + (zz_9 >>> 32);
                long zz_54 = (zz_53 & 4294967295L) + (x_5 * x_0);
                int w5 = (int) zz_54;
                zz[5] = (w5 << 1) | c6;
                int c7 = w5 >>> 31;
                long zz_55 = zz_64 + (zz_54 >>> 32) + (x_5 * x_1);
                long zz_73 = (zz_72 & 4294967295L) + (zz_55 >>> 32) + (x_5 * x_2);
                long zz_83 = (zz_82 & 4294967295L) + (zz_73 >>> 32) + (x_5 * x_3);
                long zz_92 = (zz_9 & 4294967295L) + (zz_83 >>> 32) + (x_5 * x_4);
                long zz_102 = zz_10 + (zz_92 >>> 32);
                int w6 = (int) zz_55;
                zz[6] = (w6 << 1) | c7;
                int c8 = w6 >>> 31;
                int w7 = (int) zz_73;
                zz[7] = (w7 << 1) | c8;
                int c9 = w7 >>> 31;
                int w8 = (int) zz_83;
                zz[8] = (w8 << 1) | c9;
                int c10 = w8 >>> 31;
                int w9 = (int) zz_92;
                zz[9] = (w9 << 1) | c10;
                int c11 = w9 >>> 31;
                int w10 = (int) zz_102;
                zz[10] = (w10 << 1) | c11;
                int c12 = w10 >>> 31;
                zz[11] = ((zz[11] + ((int) (zz_102 >>> 32))) << 1) | c12;
                return;
            }
            i = i2;
        }
    }

    public static void square(int[] x, int xOff, int[] zz, int zzOff) {
        long x_0 = x[xOff + 0] & 4294967295L;
        int c = 0;
        int i = 5;
        int j = 12;
        while (true) {
            int i2 = i - 1;
            long xVal = x[xOff + i] & 4294967295L;
            long p = xVal * xVal;
            int j2 = j - 1;
            zz[zzOff + j2] = (c << 31) | ((int) (p >>> 33));
            j = j2 - 1;
            zz[zzOff + j] = (int) (p >>> 1);
            c = (int) p;
            if (i2 <= 0) {
                long p2 = x_0 * x_0;
                long zz_1 = ((c << 31) & 4294967295L) | (p2 >>> 33);
                zz[zzOff + 0] = (int) p2;
                int c2 = ((int) (p2 >>> 32)) & 1;
                int j3 = xOff + 1;
                long x_1 = x[j3] & 4294967295L;
                long zz_2 = zz[zzOff + 2] & 4294967295L;
                long zz_12 = zz_1 + (x_1 * x_0);
                int w = (int) zz_12;
                zz[zzOff + 1] = (w << 1) | c2;
                int c3 = w >>> 31;
                long zz_22 = zz_2 + (zz_12 >>> 32);
                long x_2 = x[xOff + 2] & 4294967295L;
                long zz_13 = zz[zzOff + 4];
                long zz_23 = zz_22 + (x_2 * x_0);
                int w2 = (int) zz_23;
                zz[zzOff + 2] = (w2 << 1) | c3;
                int c4 = w2 >>> 31;
                long zz_3 = (zz[zzOff + 3] & 4294967295L) + (zz_23 >>> 32) + (x_2 * x_1);
                long zz_4 = (zz_13 & 4294967295L) + (zz_3 >>> 32);
                long x_3 = x[xOff + 3] & 4294967295L;
                long zz_5 = (zz[zzOff + 5] & 4294967295L) + (zz_4 >>> 32);
                long zz_32 = (zz_3 & 4294967295L) + (x_3 * x_0);
                int w3 = (int) zz_32;
                zz[zzOff + 3] = (w3 << 1) | c4;
                int c5 = w3 >>> 31;
                long zz_42 = (zz_4 & 4294967295L) + (zz_32 >>> 32) + (x_3 * x_1);
                long zz_52 = (zz_5 & 4294967295L) + (zz_42 >>> 32) + (x_3 * x_2);
                long zz_6 = (zz[zzOff + 6] & 4294967295L) + (zz_5 >>> 32) + (zz_52 >>> 32);
                long x_4 = x[xOff + 4] & 4294967295L;
                long zz_7 = (zz[zzOff + 7] & 4294967295L) + (zz_6 >>> 32);
                long zz_8 = (zz[zzOff + 8] & 4294967295L) + (zz_7 >>> 32);
                long zz_43 = (zz_42 & 4294967295L) + (x_4 * x_0);
                int w4 = (int) zz_43;
                zz[zzOff + 4] = (w4 << 1) | c5;
                int c6 = w4 >>> 31;
                long zz_53 = (zz_52 & 4294967295L) + (zz_43 >>> 32) + (x_4 * x_1);
                long zz_62 = (zz_6 & 4294967295L) + (zz_53 >>> 32) + (x_4 * x_2);
                long zz_72 = (zz_7 & 4294967295L) + (zz_62 >>> 32) + (x_4 * x_3);
                long zz_82 = zz_8 + (zz_72 >>> 32);
                long x_5 = x[xOff + 5] & 4294967295L;
                long zz_9 = (zz[zzOff + 9] & 4294967295L) + (zz_82 >>> 32);
                long zz_83 = zz_82 & 4294967295L;
                long zz_84 = zz[zzOff + 10];
                long zz_10 = (zz_84 & 4294967295L) + (zz_9 >>> 32);
                long zz_54 = (zz_53 & 4294967295L) + (x_5 * x_0);
                int w5 = (int) zz_54;
                zz[zzOff + 5] = (w5 << 1) | c6;
                int c7 = w5 >>> 31;
                long zz_63 = (zz_62 & 4294967295L) + (zz_54 >>> 32) + (x_5 * x_1);
                long zz_73 = (zz_72 & 4294967295L) + (zz_63 >>> 32) + (x_5 * x_2);
                long x_02 = zz_83 + (zz_73 >>> 32) + (x_5 * x_3);
                long zz_92 = (zz_9 & 4294967295L) + (x_02 >>> 32) + (x_5 * x_4);
                long zz_102 = zz_10 + (zz_92 >>> 32);
                int w6 = (int) zz_63;
                zz[zzOff + 6] = (w6 << 1) | c7;
                int c8 = w6 >>> 31;
                int w7 = (int) zz_73;
                zz[zzOff + 7] = (w7 << 1) | c8;
                int c9 = w7 >>> 31;
                int w8 = (int) x_02;
                zz[zzOff + 8] = (w8 << 1) | c9;
                int c10 = w8 >>> 31;
                int w9 = (int) zz_92;
                zz[zzOff + 9] = (w9 << 1) | c10;
                int c11 = w9 >>> 31;
                int w10 = (int) zz_102;
                zz[zzOff + 10] = (w10 << 1) | c11;
                int c12 = w10 >>> 31;
                long zz_85 = zz_102 >>> 32;
                zz[zzOff + 11] = ((zz[zzOff + 11] + ((int) zz_85)) << 1) | c12;
                return;
            }
            i = i2;
        }
    }

    public static int sub(int[] x, int[] y, int[] z) {
        long c = 0 + ((x[0] & 4294967295L) - (y[0] & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + ((x[1] & 4294967295L) - (y[1] & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + ((x[2] & 4294967295L) - (y[2] & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + ((x[3] & 4294967295L) - (y[3] & 4294967295L));
        z[3] = (int) c4;
        long c5 = (c4 >> 32) + ((x[4] & 4294967295L) - (y[4] & 4294967295L));
        z[4] = (int) c5;
        long c6 = (c5 >> 32) + ((x[5] & 4294967295L) - (y[5] & 4294967295L));
        z[5] = (int) c6;
        return (int) (c6 >> 32);
    }

    public static int sub(int[] x, int xOff, int[] y, int yOff, int[] z, int zOff) {
        long c = 0 + ((x[xOff + 0] & 4294967295L) - (y[yOff + 0] & 4294967295L));
        z[zOff + 0] = (int) c;
        long c2 = (c >> 32) + ((x[xOff + 1] & 4294967295L) - (y[yOff + 1] & 4294967295L));
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >> 32) + ((x[xOff + 2] & 4294967295L) - (y[yOff + 2] & 4294967295L));
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >> 32) + ((x[xOff + 3] & 4294967295L) - (y[yOff + 3] & 4294967295L));
        z[zOff + 3] = (int) c4;
        long c5 = (c4 >> 32) + ((x[xOff + 4] & 4294967295L) - (y[yOff + 4] & 4294967295L));
        z[zOff + 4] = (int) c5;
        long c6 = (c5 >> 32) + ((x[xOff + 5] & 4294967295L) - (y[yOff + 5] & 4294967295L));
        z[zOff + 5] = (int) c6;
        return (int) (c6 >> 32);
    }

    public static int subBothFrom(int[] x, int[] y, int[] z) {
        long c = 0 + (((z[0] & 4294967295L) - (x[0] & 4294967295L)) - (y[0] & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + (((z[1] & 4294967295L) - (x[1] & 4294967295L)) - (y[1] & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + (((z[2] & 4294967295L) - (x[2] & 4294967295L)) - (y[2] & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + (((z[3] & 4294967295L) - (x[3] & 4294967295L)) - (y[3] & 4294967295L));
        z[3] = (int) c4;
        long c5 = (c4 >> 32) + (((z[4] & 4294967295L) - (x[4] & 4294967295L)) - (y[4] & 4294967295L));
        z[4] = (int) c5;
        long c6 = (c5 >> 32) + (((z[5] & 4294967295L) - (x[5] & 4294967295L)) - (y[5] & 4294967295L));
        z[5] = (int) c6;
        return (int) (c6 >> 32);
    }

    public static int subFrom(int[] x, int[] z) {
        long c = 0 + ((z[0] & 4294967295L) - (x[0] & 4294967295L));
        z[0] = (int) c;
        long c2 = (c >> 32) + ((z[1] & 4294967295L) - (x[1] & 4294967295L));
        z[1] = (int) c2;
        long c3 = (c2 >> 32) + ((z[2] & 4294967295L) - (x[2] & 4294967295L));
        z[2] = (int) c3;
        long c4 = (c3 >> 32) + ((z[3] & 4294967295L) - (x[3] & 4294967295L));
        z[3] = (int) c4;
        long c5 = (c4 >> 32) + ((z[4] & 4294967295L) - (x[4] & 4294967295L));
        z[4] = (int) c5;
        long c6 = (c5 >> 32) + ((z[5] & 4294967295L) - (x[5] & 4294967295L));
        z[5] = (int) c6;
        return (int) (c6 >> 32);
    }

    public static int subFrom(int[] x, int xOff, int[] z, int zOff) {
        long c = 0 + ((z[zOff + 0] & 4294967295L) - (x[xOff + 0] & 4294967295L));
        z[zOff + 0] = (int) c;
        long c2 = (c >> 32) + ((z[zOff + 1] & 4294967295L) - (x[xOff + 1] & 4294967295L));
        z[zOff + 1] = (int) c2;
        long c3 = (c2 >> 32) + ((z[zOff + 2] & 4294967295L) - (x[xOff + 2] & 4294967295L));
        z[zOff + 2] = (int) c3;
        long c4 = (c3 >> 32) + ((z[zOff + 3] & 4294967295L) - (x[xOff + 3] & 4294967295L));
        z[zOff + 3] = (int) c4;
        long c5 = (c4 >> 32) + ((z[zOff + 4] & 4294967295L) - (x[xOff + 4] & 4294967295L));
        z[zOff + 4] = (int) c5;
        long c6 = (c5 >> 32) + ((z[zOff + 5] & 4294967295L) - (x[xOff + 5] & 4294967295L));
        z[zOff + 5] = (int) c6;
        return (int) (c6 >> 32);
    }

    public static BigInteger toBigInteger(int[] x) {
        byte[] bs = new byte[24];
        for (int i = 0; i < 6; i++) {
            int x_i = x[i];
            if (x_i != 0) {
                Pack.intToBigEndian(x_i, bs, (5 - i) << 2);
            }
        }
        return new BigInteger(1, bs);
    }

    public static BigInteger toBigInteger64(long[] x) {
        byte[] bs = new byte[24];
        for (int i = 0; i < 3; i++) {
            long x_i = x[i];
            if (x_i != 0) {
                Pack.longToBigEndian(x_i, bs, (2 - i) << 3);
            }
        }
        return new BigInteger(1, bs);
    }

    public static void zero(int[] z) {
        z[0] = 0;
        z[1] = 0;
        z[2] = 0;
        z[3] = 0;
        z[4] = 0;
        z[5] = 0;
    }
}
