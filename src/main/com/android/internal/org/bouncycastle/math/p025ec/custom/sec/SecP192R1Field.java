package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat192;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP192R1Field */
/* loaded from: classes4.dex */
public class SecP192R1Field {

    /* renamed from: M */
    private static final long f858M = 4294967295L;

    /* renamed from: P5 */
    private static final int f860P5 = -1;
    private static final int PExt11 = -1;

    /* renamed from: P */
    static final int[] f859P = {-1, -1, -2, -1, -1, -1};
    private static final int[] PExt = {1, 0, 2, 0, 1, 0, -2, -1, -3, -1, -1, -1};
    private static final int[] PExtInv = {-1, -1, -3, -1, -2, -1, 1, 0, 2};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat192.add(x, y, z);
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f859P))) {
            addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(12, xx, yy, zz);
        if (c != 0 || (zz[11] == -1 && Nat.gte(12, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(12, zz, iArr.length);
            }
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(6, x, z);
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f859P))) {
            addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat192.fromBigInteger(x);
        if (z[5] == -1) {
            int[] iArr = f859P;
            if (Nat192.gte(z, iArr)) {
                Nat192.subFrom(iArr, z);
            }
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(6, x, 0, z);
            return;
        }
        int c = Nat192.add(x, f859P, z);
        Nat.shiftDownBit(6, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f859P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 6; i++) {
            d |= x[i];
        }
        int i2 = d >>> 1;
        return ((i2 | (d & 1)) - 1) >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.mul(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat192.mulAddTo(x, y, zz);
        if (c != 0 || (zz[11] == -1 && Nat.gte(12, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(12, zz, iArr.length);
            }
        }
    }

    public static void negate(int[] x, int[] z) {
        if (isZero(x) != 0) {
            int[] iArr = f859P;
            Nat192.sub(iArr, iArr, z);
            return;
        }
        Nat192.sub(f859P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[24];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 6);
        } while (Nat.lessThan(6, z, f859P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long xx06 = xx[6] & 4294967295L;
        long xx07 = xx[7] & 4294967295L;
        long xx08 = xx[8] & 4294967295L;
        long xx09 = xx[9] & 4294967295L;
        long xx10 = xx[10] & 4294967295L;
        long xx11 = xx[11] & 4294967295L;
        long t0 = xx06 + xx10;
        long t1 = xx07 + xx11;
        long xx102 = xx[0];
        long cc = 0 + (xx102 & 4294967295L) + t0;
        int z0 = (int) cc;
        long cc2 = (cc >> 32) + (xx[1] & 4294967295L) + t1;
        z[1] = (int) cc2;
        long t02 = t0 + xx08;
        long t12 = t1 + xx09;
        long cc3 = (cc2 >> 32) + (xx[2] & 4294967295L) + t02;
        long z2 = cc3 & 4294967295L;
        long cc4 = (cc3 >> 32) + (xx[3] & 4294967295L) + t12;
        z[3] = (int) cc4;
        long cc5 = (cc4 >> 32) + (xx[4] & 4294967295L) + (t02 - xx06);
        z[4] = (int) cc5;
        long cc6 = (cc5 >> 32) + (xx[5] & 4294967295L) + (t12 - xx07);
        z[5] = (int) cc6;
        long cc7 = cc6 >> 32;
        long z22 = z2 + cc7;
        long cc8 = cc7 + (z0 & 4294967295L);
        z[0] = (int) cc8;
        long cc9 = cc8 >> 32;
        if (cc9 != 0) {
            long cc10 = cc9 + (z[1] & 4294967295L);
            z[1] = (int) cc10;
            z22 += cc10 >> 32;
        }
        z[2] = (int) z22;
        if (((z22 >> 32) != 0 && Nat.incAt(6, z, 3) != 0) || (z[5] == -1 && Nat192.gte(z, f859P))) {
            addPInvTo(z);
        }
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0;
        if (x != 0) {
            long xx06 = x & 4294967295L;
            long cc2 = 0 + (z[0] & 4294967295L) + xx06;
            z[0] = (int) cc2;
            long cc3 = cc2 >> 32;
            if (cc3 != 0) {
                long cc4 = cc3 + (z[1] & 4294967295L);
                z[1] = (int) cc4;
                cc3 = cc4 >> 32;
            }
            long cc5 = cc3 + (4294967295L & z[2]) + xx06;
            z[2] = (int) cc5;
            cc = cc5 >> 32;
        }
        if ((cc != 0 && Nat.incAt(6, z, 3) != 0) || (z[5] == -1 && Nat192.gte(z, f859P))) {
            addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat192.createExt();
        Nat192.square(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                Nat192.square(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat192.sub(x, y, z);
        if (c != 0) {
            subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(12, xx, yy, zz);
        if (c != 0) {
            int[] iArr = PExtInv;
            if (Nat.subFrom(iArr.length, iArr, zz) != 0) {
                Nat.decAt(12, zz, iArr.length);
            }
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(6, x, 0, z);
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f859P))) {
            addPInvTo(z);
        }
    }

    private static void addPInvTo(int[] z) {
        long c = (z[0] & 4294967295L) + 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (z[1] & 4294967295L);
            z[1] = (int) c3;
            c2 = c3 >> 32;
        }
        long c4 = c2 + (4294967295L & z[2]) + 1;
        z[2] = (int) c4;
        if ((c4 >> 32) != 0) {
            Nat.incAt(6, z, 3);
        }
    }

    private static void subPInvFrom(int[] z) {
        long c = (z[0] & 4294967295L) - 1;
        z[0] = (int) c;
        long c2 = c >> 32;
        if (c2 != 0) {
            long c3 = c2 + (z[1] & 4294967295L);
            z[1] = (int) c3;
            c2 = c3 >> 32;
        }
        long c4 = c2 + ((4294967295L & z[2]) - 1);
        z[2] = (int) c4;
        if ((c4 >> 32) != 0) {
            Nat.decAt(6, z, 3);
        }
    }
}
