package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat256;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256R1Field */
/* loaded from: classes4.dex */
public class SecP256R1Field {

    /* renamed from: M */
    private static final long f880M = 4294967295L;

    /* renamed from: P7 */
    private static final int f882P7 = -1;
    private static final int PExt15s1 = Integer.MAX_VALUE;

    /* renamed from: P */
    static final int[] f881P = {-1, -1, -1, 0, 0, 0, 1, -1};
    private static final int[] PExt = {1, 0, 0, -2, -1, -1, -2, 1, -2, 1, -2, 1, 1, -2, 2, -2};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat256.add(x, y, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f881P))) {
            addPInvTo(z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(16, xx, yy, zz);
        if (c != 0 || ((zz[15] >>> 1) >= Integer.MAX_VALUE && Nat.gte(16, zz, PExt))) {
            Nat.subFrom(16, PExt, zz);
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(8, x, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f881P))) {
            addPInvTo(z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat256.fromBigInteger(x);
        if (z[7] == -1) {
            int[] iArr = f881P;
            if (Nat256.gte(z, iArr)) {
                Nat256.subFrom(iArr, z);
            }
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(8, x, 0, z);
            return;
        }
        int c = Nat256.add(x, f881P, z);
        Nat.shiftDownBit(8, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f881P, x, z);
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < 8; i++) {
            d |= x[i];
        }
        int i2 = d >>> 1;
        return ((i2 | (d & 1)) - 1) >> 31;
    }

    public static void multiply(int[] x, int[] y, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.mul(x, y, tt);
        reduce(tt, z);
    }

    public static void multiplyAddToExt(int[] x, int[] y, int[] zz) {
        int c = Nat256.mulAddTo(x, y, zz);
        if (c != 0 || ((zz[15] >>> 1) >= Integer.MAX_VALUE && Nat.gte(16, zz, PExt))) {
            Nat.subFrom(16, PExt, zz);
        }
    }

    public static void negate(int[] x, int[] z) {
        if (isZero(x) != 0) {
            int[] iArr = f881P;
            Nat256.sub(iArr, iArr, z);
            return;
        }
        Nat256.sub(f881P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[32];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 8);
        } while (Nat.lessThan(8, z, f881P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long xx09 = xx[9] & 4294967295L;
        long xx10 = xx[10] & 4294967295L;
        long xx11 = xx[11] & 4294967295L;
        long xx12 = xx[12] & 4294967295L;
        long xx13 = xx[13] & 4294967295L;
        long xx14 = xx[14] & 4294967295L;
        long xx15 = xx[15] & 4294967295L;
        long xx08 = (xx[8] & 4294967295L) - 6;
        long t0 = xx08 + xx09;
        long t1 = xx09 + xx10;
        long t2 = (xx10 + xx11) - xx15;
        long t3 = xx11 + xx12;
        long t4 = xx12 + xx13;
        long t5 = xx13 + xx14;
        long t6 = xx14 + xx15;
        long t7 = t5 - t0;
        long cc = 0 + (((xx[0] & 4294967295L) - t3) - t7);
        z[0] = (int) cc;
        long cc2 = (cc >> 32) + ((((xx[1] & 4294967295L) + t1) - t4) - t6);
        z[1] = (int) cc2;
        long cc3 = (cc2 >> 32) + (((xx[2] & 4294967295L) + t2) - t5);
        z[2] = (int) cc3;
        long cc4 = (cc3 >> 32) + ((((xx[3] & 4294967295L) + (t3 << 1)) + t7) - t6);
        z[3] = (int) cc4;
        long cc5 = (cc4 >> 32) + ((((xx[4] & 4294967295L) + (t4 << 1)) + xx14) - t1);
        z[4] = (int) cc5;
        long cc6 = (cc5 >> 32) + (((xx[5] & 4294967295L) + (t5 << 1)) - t2);
        z[5] = (int) cc6;
        long cc7 = (cc6 >> 32) + (xx[6] & 4294967295L) + (t6 << 1) + t7;
        z[6] = (int) cc7;
        long cc8 = (cc7 >> 32) + (((((xx[7] & 4294967295L) + (xx15 << 1)) + xx08) - t2) - t4);
        z[7] = (int) cc8;
        reduce32((int) ((cc8 >> 32) + 6), z);
    }

    public static void reduce32(int x, int[] z) {
        long cc = 0;
        if (x != 0) {
            long xx08 = x & 4294967295L;
            long cc2 = 0 + (z[0] & 4294967295L) + xx08;
            z[0] = (int) cc2;
            long cc3 = cc2 >> 32;
            if (cc3 != 0) {
                long cc4 = cc3 + (z[1] & 4294967295L);
                z[1] = (int) cc4;
                long cc5 = (cc4 >> 32) + (z[2] & 4294967295L);
                z[2] = (int) cc5;
                cc3 = cc5 >> 32;
            }
            long cc6 = cc3 + ((z[3] & 4294967295L) - xx08);
            z[3] = (int) cc6;
            long cc7 = cc6 >> 32;
            if (cc7 != 0) {
                long cc8 = cc7 + (z[4] & 4294967295L);
                z[4] = (int) cc8;
                long cc9 = (cc8 >> 32) + (z[5] & 4294967295L);
                z[5] = (int) cc9;
                cc7 = cc9 >> 32;
            }
            long cc10 = cc7 + ((z[6] & 4294967295L) - xx08);
            z[6] = (int) cc10;
            long cc11 = (cc10 >> 32) + (4294967295L & z[7]) + xx08;
            z[7] = (int) cc11;
            cc = cc11 >> 32;
        }
        if (cc != 0 || (z[7] == -1 && Nat256.gte(z, f881P))) {
            addPInvTo(z);
        }
    }

    public static void square(int[] x, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        reduce(tt, z);
    }

    public static void squareN(int[] x, int n, int[] z) {
        int[] tt = Nat256.createExt();
        Nat256.square(x, tt);
        reduce(tt, z);
        while (true) {
            n--;
            if (n > 0) {
                Nat256.square(z, tt);
                reduce(tt, z);
            } else {
                return;
            }
        }
    }

    public static void subtract(int[] x, int[] y, int[] z) {
        int c = Nat256.sub(x, y, z);
        if (c != 0) {
            subPInvFrom(z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(16, xx, yy, zz);
        if (c != 0) {
            Nat.addTo(16, PExt, zz);
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(8, x, 0, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f881P))) {
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
            long c4 = (c3 >> 32) + (z[2] & 4294967295L);
            z[2] = (int) c4;
            c2 = c4 >> 32;
        }
        long c5 = c2 + ((z[3] & 4294967295L) - 1);
        z[3] = (int) c5;
        long c6 = c5 >> 32;
        if (c6 != 0) {
            long c7 = c6 + (z[4] & 4294967295L);
            z[4] = (int) c7;
            long c8 = (c7 >> 32) + (z[5] & 4294967295L);
            z[5] = (int) c8;
            c6 = c8 >> 32;
        }
        long c9 = c6 + ((z[6] & 4294967295L) - 1);
        z[6] = (int) c9;
        z[7] = (int) ((c9 >> 32) + (4294967295L & z[7]) + 1);
    }

    private static void subPInvFrom(int[] z) {
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
        long c5 = c2 + (z[3] & 4294967295L) + 1;
        z[3] = (int) c5;
        long c6 = c5 >> 32;
        if (c6 != 0) {
            long c7 = c6 + (z[4] & 4294967295L);
            z[4] = (int) c7;
            long c8 = (c7 >> 32) + (z[5] & 4294967295L);
            z[5] = (int) c8;
            c6 = c8 >> 32;
        }
        long c9 = c6 + (z[6] & 4294967295L) + 1;
        z[6] = (int) c9;
        z[7] = (int) ((c9 >> 32) + ((4294967295L & z[7]) - 1));
    }
}
