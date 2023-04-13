package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat512;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP521R1Field */
/* loaded from: classes4.dex */
public class SecP521R1Field {

    /* renamed from: P */
    static final int[] f891P = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 511};
    private static final int P16 = 511;

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat.add(16, x, y, z) + x[16] + y[16];
        if (c > 511 || (c == 511 && Nat.m42eq(16, z, f891P))) {
            c = (c + Nat.inc(16, z)) & 511;
        }
        z[16] = c;
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(16, x, z) + x[16];
        if (c > 511 || (c == 511 && Nat.m42eq(16, z, f891P))) {
            c = (c + Nat.inc(16, z)) & 511;
        }
        z[16] = c;
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat.fromBigInteger(521, x);
        if (Nat.m42eq(17, z, f891P)) {
            Nat.zero(17, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        int x16 = x[16];
        int c = Nat.shiftDownBit(16, x, x16, z);
        z[16] = (x16 >>> 1) | (c >>> 23);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f891P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 17; i++) {
            d |= x[i];
        }
        int i2 = d >>> 1;
        return ((i2 | (d & 1)) - 1) >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat.create(33);
        implMultiply(x, y, tt);
        reduce(tt, z);
    }

    public static void negate(int[] x, int[] z) {
        if (isZero(x) != 0) {
            int[] iArr = f891P;
            Nat.sub(17, iArr, iArr, z);
            return;
        }
        Nat.sub(17, f891P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[68];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 17);
            z[16] = z[16] & 511;
        } while (Nat.lessThan(17, z, f891P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        int xx32 = xx[32];
        int c = (Nat.shiftDownBits(16, xx, 16, 9, xx32, z, 0) >>> 23) + (xx32 >>> 9) + Nat.addTo(16, xx, z);
        if (c > 511 || (c == 511 && Nat.m42eq(16, z, f891P))) {
            c = (c + Nat.inc(16, z)) & 511;
        }
        z[16] = c;
    }

    public static void reduce23(int[] z) {
        int z16 = z[16];
        int c = Nat.addWordTo(16, z16 >>> 9, z) + (z16 & 511);
        if (c > 511 || (c == 511 && Nat.m42eq(16, z, f891P))) {
            c = (c + Nat.inc(16, z)) & 511;
        }
        z[16] = c;
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat.create(33);
        implSquare(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat.create(33);
        implSquare(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                implSquare(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = (Nat.sub(16, x, y, z) + x[16]) - y[16];
        if (c < 0) {
            c = (c + Nat.dec(16, z)) & 511;
        }
        z[16] = c;
    }

    public static void twice(int[] x, int[] z) {
        int x16 = x[16];
        int c = Nat.shiftUpBit(16, x, x16 << 23, z) | (x16 << 1);
        z[16] = c & 511;
    }

    protected static void implMultiply(int[] x, int[] y, int[] zz) {
        Nat512.mul(x, y, zz);
        int x16 = x[16];
        int y16 = y[16];
        zz[32] = Nat.mul31BothAdd(16, x16, y, y16, x, zz, 16) + (x16 * y16);
    }

    protected static void implSquare(int[] x, int[] zz) {
        Nat512.square(x, zz);
        int x16 = x[16];
        zz[32] = Nat.mulWordAddTo(16, x16 << 1, x, 0, zz, 16) + (x16 * x16);
    }
}
