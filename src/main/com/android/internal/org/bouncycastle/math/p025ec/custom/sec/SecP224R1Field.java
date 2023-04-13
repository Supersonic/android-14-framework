package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat224;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP224R1Field */
/* loaded from: classes4.dex */
public class SecP224R1Field {

    /* renamed from: M */
    private static final long f869M = 4294967295L;

    /* renamed from: P6 */
    private static final int f871P6 = -1;
    private static final int PExt13 = -1;

    /* renamed from: P */
    static final int[] f870P = {1, 0, 0, -1, -1, -1, -1};
    private static final int[] PExt = {1, 0, 0, -2, -1, -1, 0, 2, 0, 0, -2, -1, -1, -1};
    private static final int[] PExtInv = {-1, -1, -1, 1, 0, 0, -1, -3, -1, -1, 1};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat224.add(x, y, z);
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f870P))) {
            addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(14, xx, yy, zz);
        if (c != 0 || (zz[13] == -1 && Nat.gte(14, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(14, zz, iArr.length);
            }
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(7, x, z);
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f870P))) {
            addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat224.fromBigInteger(x);
        if (z[6] == -1) {
            int[] iArr = f870P;
            if (Nat224.gte(z, iArr)) {
                Nat224.subFrom(iArr, z);
            }
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(7, x, 0, z);
            return;
        }
        int c = Nat224.add(x, f870P, z);
        Nat.shiftDownBit(7, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f870P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 7; i++) {
            d |= x[i];
        }
        int i2 = d >>> 1;
        return ((i2 | (d & 1)) - 1) >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.mul(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat224.mulAddTo(x, y, zz);
        if (c != 0 || (zz[13] == -1 && Nat.gte(14, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(14, zz, iArr.length);
            }
        }
    }

    public static void negate(int[] x, int[] z) {
        if (isZero(x) != 0) {
            int[] iArr = f870P;
            Nat224.sub(iArr, iArr, z);
            return;
        }
        Nat224.sub(f870P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[28];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 7);
        } while (Nat.lessThan(7, z, f870P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long xx10 = xx[10] & 4294967295L;
        long xx11 = xx[11] & 4294967295L;
        long xx12 = xx[12] & 4294967295L;
        long xx13 = xx[13] & 4294967295L;
        long t0 = ((xx[7] & 4294967295L) + xx11) - 1;
        long n = xx[8];
        long t1 = (n & 4294967295L) + xx12;
        long t2 = (xx[9] & 4294967295L) + xx13;
        long cc = 0 + ((xx[0] & 4294967295L) - t0);
        long z0 = cc & 4294967295L;
        long z02 = xx[1];
        long cc2 = (cc >> 32) + ((z02 & 4294967295L) - t1);
        z[1] = (int) cc2;
        long cc3 = (cc2 >> 32) + ((xx[2] & 4294967295L) - t2);
        z[2] = (int) cc3;
        long cc4 = (cc3 >> 32) + (((xx[3] & 4294967295L) + t0) - xx10);
        long z3 = cc4 & 4294967295L;
        long cc5 = (cc4 >> 32) + (((xx[4] & 4294967295L) + t1) - xx11);
        z[4] = (int) cc5;
        long cc6 = (cc5 >> 32) + (((xx[5] & 4294967295L) + t2) - xx12);
        z[5] = (int) cc6;
        long cc7 = (cc6 >> 32) + (((xx[6] & 4294967295L) + xx10) - xx13);
        z[6] = (int) cc7;
        long cc8 = (cc7 >> 32) + 1;
        long z32 = z3 + cc8;
        long z03 = z0 - cc8;
        z[0] = (int) z03;
        long cc9 = z03 >> 32;
        if (cc9 != 0) {
            long cc10 = cc9 + (z[1] & 4294967295L);
            z[1] = (int) cc10;
            long cc11 = (cc10 >> 32) + (4294967295L & z[2]);
            z[2] = (int) cc11;
            z32 += cc11 >> 32;
        }
        z[3] = (int) z32;
        if (((z32 >> 32) != 0 && Nat.incAt(7, z, 4) != 0) || (z[6] == -1 && Nat224.gte(z, f870P))) {
            addPInvTo(z);
        }
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0;
        if (x != 0) {
            long xx07 = x & 4294967295L;
            long cc2 = 0 + ((z[0] & 4294967295L) - xx07);
            z[0] = (int) cc2;
            long cc3 = cc2 >> 32;
            if (cc3 != 0) {
                long cc4 = cc3 + (z[1] & 4294967295L);
                z[1] = (int) cc4;
                long cc5 = (cc4 >> 32) + (z[2] & 4294967295L);
                z[2] = (int) cc5;
                cc3 = cc5 >> 32;
            }
            long cc6 = cc3 + (4294967295L & z[3]) + xx07;
            z[3] = (int) cc6;
            cc = cc6 >> 32;
        }
        if ((cc != 0 && Nat.incAt(7, z, 4) != 0) || (z[6] == -1 && Nat224.gte(z, f870P))) {
            addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.square(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat224.createExt();
        Nat224.square(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                Nat224.square(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat224.sub(x, y, z);
        if (c != 0) {
            subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(14, xx, yy, zz);
        if (c != 0) {
            int[] iArr = PExtInv;
            if (Nat.subFrom(iArr.length, iArr, zz) != 0) {
                Nat.decAt(14, zz, iArr.length);
            }
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(7, x, 0, z);
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f870P))) {
            addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = (z[0] & 4294967295L) - 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (z[1] & 4294967295L);
            z[1] = (int) c3;
            long c4 = (c3 >> 32) + (z[2] & 4294967295L);
            z[2] = (int) c4;
            c2 = c4 >> 32;
        }
        long c5 = c2 + (4294967295L & z[3]) + 1;
        z[3] = (int) c5;
        if ((c5 >> 32) != 0) {
            Nat.incAt(7, z, 4);
        }
    }

    private static void subPInvFrom(int[] z) {
        long c = (z[0] & 4294967295L) + 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (z[1] & 4294967295L);
            z[1] = (int) c3;
            long c4 = (c3 >> 32) + (z[2] & 4294967295L);
            z[2] = (int) c4;
            c2 = c4 >> 32;
        }
        long c5 = c2 + ((4294967295L & z[3]) - 1);
        z[3] = (int) c5;
        if ((c5 >> 32) != 0) {
            Nat.decAt(7, z, 4);
        }
    }
}
