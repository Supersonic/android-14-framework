package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat224;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP224K1Field */
/* loaded from: classes4.dex */
public class SecP224K1Field {

    /* renamed from: P6 */
    private static final int f865P6 = -1;
    private static final int PExt13 = -1;
    private static final int PInv33 = 6803;

    /* renamed from: P */
    static final int[] f864P = {-6803, -2, -1, -1, -1, -1, -1};
    private static final int[] PExt = {46280809, 13606, 1, 0, 0, 0, 0, -13606, -3, -1, -1, -1, -1, -1};
    private static final int[] PExtInv = {-46280809, -13607, -2, -1, -1, -1, -1, 13605, 2};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat224.add(x, y, z);
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f864P))) {
            Nat.add33To(7, PInv33, z);
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
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f864P))) {
            Nat.add33To(7, PInv33, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat224.fromBigInteger(x);
        if (z[6] == -1 && Nat224.gte(z, f864P)) {
            Nat.add33To(7, PInv33, z);
        }
        return z;
    }

    public static void half(int[] x, int[] z) {
        if ((x[0] & 1) == 0) {
            Nat.shiftDownBit(7, x, 0, z);
            return;
        }
        int c = Nat224.add(x, f864P, z);
        Nat.shiftDownBit(7, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f864P, x, z);
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
            int[] iArr = f864P;
            Nat224.sub(iArr, iArr, z);
            return;
        }
        Nat224.sub(f864P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[28];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 7);
        } while (Nat.lessThan(7, z, f864P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat224.mul33Add(PInv33, xx, 7, xx, 0, z, 0);
        int c = Nat224.mul33DWordAdd(PInv33, cc, z, 0);
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f864P))) {
            Nat.add33To(7, PInv33, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if ((x != 0 && Nat224.mul33WordAdd(PInv33, x, z, 0) != 0) || (z[6] == -1 && Nat224.gte(z, f864P))) {
            Nat.add33To(7, PInv33, z);
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
            Nat.sub33From(7, PInv33, z);
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
        if (c != 0 || (z[6] == -1 && Nat224.gte(z, f864P))) {
            Nat.add33To(7, PInv33, z);
        }
    }
}
