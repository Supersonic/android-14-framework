package com.android.internal.org.bouncycastle.math.raw;
/* loaded from: classes4.dex */
public abstract class Bits {
    public static int bitPermuteStep(int x, int m, int s) {
        int t = ((x >>> s) ^ x) & m;
        return ((t << s) ^ t) ^ x;
    }

    public static long bitPermuteStep(long x, long m, int s) {
        long t = ((x >>> s) ^ x) & m;
        return ((t << s) ^ t) ^ x;
    }

    public static int bitPermuteStepSimple(int x, int m, int s) {
        return ((x & m) << s) | ((x >>> s) & m);
    }

    public static long bitPermuteStepSimple(long x, long m, int s) {
        return ((x & m) << s) | ((x >>> s) & m);
    }
}
