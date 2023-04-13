package com.android.internal.org.bouncycastle.math.raw;
/* loaded from: classes4.dex */
public class Interleave {
    private static final long M32 = 1431655765;
    private static final long M64 = 6148914691236517205L;
    private static final long M64R = -6148914691236517206L;

    public static int expand8to16(int x) {
        int x2 = x & 255;
        int x3 = ((x2 << 4) | x2) & 3855;
        int x4 = ((x3 << 2) | x3) & 13107;
        return ((x4 << 1) | x4) & 21845;
    }

    public static int expand16to32(int x) {
        int x2 = x & 65535;
        int x3 = ((x2 << 8) | x2) & 16711935;
        int x4 = ((x3 << 4) | x3) & 252645135;
        int x5 = ((x4 << 2) | x4) & 858993459;
        return ((x5 << 1) | x5) & 1431655765;
    }

    public static long expand32to64(int x) {
        int x2 = Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 65280, 8), 15728880, 4), 202116108, 2), 572662306, 1);
        return (((x2 >>> 1) & M32) << 32) | (M32 & x2);
    }

    public static void expand64To128(long x, long[] z, int zOff) {
        long x2 = Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 4294901760L, 16), 280375465148160L, 8), 67555025218437360L, 4), 868082074056920076L, 2), 2459565876494606882L, 1);
        z[zOff] = x2 & M64;
        z[zOff + 1] = M64 & (x2 >>> 1);
    }

    public static void expand64To128(long[] xs, int xsOff, int xsLen, long[] zs, int zsOff) {
        for (int i = 0; i < xsLen; i++) {
            expand64To128(xs[xsOff + i], zs, zsOff);
            zsOff += 2;
        }
    }

    public static void expand64To128Rev(long x, long[] z, int zOff) {
        long x2 = Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 4294901760L, 16), 280375465148160L, 8), 67555025218437360L, 4), 868082074056920076L, 2), 2459565876494606882L, 1);
        z[zOff] = x2 & M64R;
        z[zOff + 1] = M64R & (x2 << 1);
    }

    public static int shuffle(int x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 65280, 8), 15728880, 4), 202116108, 2), 572662306, 1);
    }

    public static long shuffle(long x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 4294901760L, 16), 280375465148160L, 8), 67555025218437360L, 4), 868082074056920076L, 2), 2459565876494606882L, 1);
    }

    public static int shuffle2(int x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 11141290, 7), 52428, 14), 15728880, 4), 65280, 8);
    }

    public static long shuffle2(long x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 4278255360L, 24), 57421771435671756L, 6), 264913582878960L, 12), 723401728380766730L, 3);
    }

    public static long shuffle3(long x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 47851476196393130L, 7), 225176545447116L, 14), 4042322160L, 28);
    }

    public static int unshuffle(int x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 572662306, 1), 202116108, 2), 15728880, 4), 65280, 8);
    }

    public static long unshuffle(long x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 2459565876494606882L, 1), 868082074056920076L, 2), 67555025218437360L, 4), 280375465148160L, 8), 4294901760L, 16);
    }

    public static int unshuffle2(int x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 65280, 8), 15728880, 4), 52428, 14), 11141290, 7);
    }

    public static long unshuffle2(long x) {
        return Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(Bits.bitPermuteStep(x, 723401728380766730L, 3), 264913582878960L, 12), 57421771435671756L, 6), 4278255360L, 24);
    }

    public static long unshuffle3(long x) {
        return shuffle3(x);
    }
}
