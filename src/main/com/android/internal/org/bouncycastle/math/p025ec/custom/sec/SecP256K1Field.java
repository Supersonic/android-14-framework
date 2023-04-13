package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat256;
import com.android.internal.org.bouncycastle.util.Pack;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256K1Field */
/* loaded from: classes4.dex */
public class SecP256K1Field {

    /* renamed from: P7 */
    private static final int f876P7 = -1;
    private static final int PExt15 = -1;
    private static final int PInv33 = 977;

    /* renamed from: P */
    static final int[] f875P = {-977, -2, -1, -1, -1, -1, -1, -1};
    private static final int[] PExt = {954529, 1954, 1, 0, 0, 0, 0, 0, -1954, -3, -1, -1, -1, -1, -1, -1};
    private static final int[] PExtInv = {-954529, -1955, -2, -1, -1, -1, -1, -1, 1953, 2};

    public static void add(int[] x, int[] y, int[] z) {
        int c = Nat256.add(x, y, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f875P))) {
            Nat.add33To(8, PInv33, z);
        }
    }

    public static void addExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.add(16, xx, yy, zz);
        if (c != 0 || (zz[15] == -1 && Nat.gte(16, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(16, zz, iArr.length);
            }
        }
    }

    public static void addOne(int[] x, int[] z) {
        int c = Nat.inc(8, x, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f875P))) {
            Nat.add33To(8, PInv33, z);
        }
    }

    public static int[] fromBigInteger(BigInteger x) {
        int[] z = Nat256.fromBigInteger(x);
        if (z[7] == -1) {
            int[] iArr = f875P;
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
        int c = Nat256.add(x, f875P, z);
        Nat.shiftDownBit(8, z, c);
    }

    public static void inv(int[] x, int[] z) {
        Mod.checkedModOddInverse(f875P, x, z);
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
        if (c != 0 || (zz[15] == -1 && Nat.gte(16, zz, PExt))) {
            int[] iArr = PExtInv;
            if (Nat.addTo(iArr.length, iArr, zz) != 0) {
                Nat.incAt(16, zz, iArr.length);
            }
        }
    }

    public static void negate(int[] x, int[] z) {
        if (isZero(x) != 0) {
            int[] iArr = f875P;
            Nat256.sub(iArr, iArr, z);
            return;
        }
        Nat256.sub(f875P, x, z);
    }

    public static void random(SecureRandom r, int[] z) {
        byte[] bb = new byte[32];
        do {
            r.nextBytes(bb);
            Pack.littleEndianToInt(bb, 0, z, 0, 8);
        } while (Nat.lessThan(8, z, f875P) == 0);
    }

    public static void randomMult(SecureRandom r, int[] z) {
        do {
            random(r, z);
        } while (isZero(z) != 0);
    }

    public static void reduce(int[] xx, int[] z) {
        long cc = Nat256.mul33Add(PInv33, xx, 8, xx, 0, z, 0);
        int c = Nat256.mul33DWordAdd(PInv33, cc, z, 0);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f875P))) {
            Nat.add33To(8, PInv33, z);
        }
    }

    public static void reduce32(int x, int[] z) {
        if ((x != 0 && Nat256.mul33WordAdd(PInv33, x, z, 0) != 0) || (z[7] == -1 && Nat256.gte(z, f875P))) {
            Nat.add33To(8, PInv33, z);
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
            Nat.sub33From(8, PInv33, z);
        }
    }

    public static void subtractExt(int[] xx, int[] yy, int[] zz) {
        int c = Nat.sub(16, xx, yy, zz);
        if (c != 0) {
            int[] iArr = PExtInv;
            if (Nat.subFrom(iArr.length, iArr, zz) != 0) {
                Nat.decAt(16, zz, iArr.length);
            }
        }
    }

    public static void twice(int[] x, int[] z) {
        int c = Nat.shiftUpBit(8, x, 0, z);
        if (c != 0 || (z[7] == -1 && Nat256.gte(z, f875P))) {
            Nat.add33To(8, PInv33, z);
        }
    }
}
