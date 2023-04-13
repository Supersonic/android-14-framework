package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat192;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP192K1Field */
/* loaded from: classes4.dex */
public class SecP192K1Field {

    /* renamed from: P5 */
    private static final int f854P5 = -1;
    private static final int PExt11 = -1;
    private static final int PInv33 = 4553;

    /* renamed from: P */
    static final int[] f853P = {-4553, -2, -1, -1, -1, -1};
    private static final int[] PExt = {20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, -1, -1};
    private static final int[] PExtInv = {-20729809, -9107, -2, -1, -1, -1, 9105, 2};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat192.add(x, y, z);
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f853P))) {
            Nat.add33To(6, PInv33, z);
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
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f853P))) {
            Nat.add33To(6, PInv33, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat192.fromBigInteger(x);
        if (z[5] == -1) {
            int[] iArr = f853P;
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
        int c = Nat192.add(x, f853P, z);
        Nat.shiftDownBit(6, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f853P, x, z);
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
            int[] iArr = f853P;
            Nat192.sub(iArr, iArr, z);
            return;
        }
        Nat192.sub(f853P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[24];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 6);
        } while (Nat.lessThan(6, z, f853P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat192.mul33Add(PInv33, xx, 6, xx, 0, z, 0);
        int c = Nat192.mul33DWordAdd(PInv33, cc, z, 0);
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f853P))) {
            Nat.add33To(6, PInv33, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if ((x != 0 && Nat192.mul33WordAdd(PInv33, x, z, 0) != 0) || (z[5] == -1 && Nat192.gte(z, f853P))) {
            Nat.add33To(6, PInv33, z);
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
            Nat.sub33From(6, PInv33, z);
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
        if (c != 0 || (z[5] == -1 && Nat192.gte(z, f853P))) {
            Nat.add33To(6, PInv33, z);
        }
    }
}
