package com.android.internal.org.bouncycastle.math.raw;

import com.android.internal.org.bouncycastle.util.Integers;
import java.util.Random;
/* loaded from: classes4.dex */
public abstract class Mod {
    private static final int M30 = 1073741823;
    private static final long M32L = 4294967295L;

    public static void add(int[] p, int[] x, int[] y, int[] z) {
        int len = p.length;
        int c = Nat.add(len, x, y, z);
        if (c != 0) {
            Nat.subFrom(len, p, z);
        }
    }

    public static void checkedModOddInverse(int[] m, int[] x, int[] z) {
        if (modOddInverse(m, x, z) == 0) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static void checkedModOddInverseVar(int[] m, int[] x, int[] z) {
        if (!modOddInverseVar(m, x, z)) {
            throw new ArithmeticException("Inverse does not exist.");
        }
    }

    public static int inverse32(int d) {
        int x = d * (2 - (d * d));
        int x2 = x * (2 - (d * x));
        int x3 = x2 * (2 - (d * x2));
        return x3 * (2 - (d * x3));
    }

    public static void invert(int[] m, int[] x, int[] z) {
        checkedModOddInverseVar(m, x, z);
    }

    public static int modOddInverse(int[] m, int[] x, int[] z) {
        int len32 = m.length;
        int bits = (len32 << 5) - Integers.numberOfLeadingZeros(m[len32 - 1]);
        int len30 = (bits + 29) / 30;
        int[] t = new int[4];
        int[] D = new int[len30];
        int[] E = new int[len30];
        int[] F = new int[len30];
        int[] G = new int[len30];
        int[] M = new int[len30];
        int i = 0;
        E[0] = 1;
        encode30(bits, x, 0, G, 0);
        encode30(bits, m, 0, M, 0);
        System.arraycopy(M, 0, F, 0, len30);
        int eta = -1;
        int m0Inv32 = inverse32(M[0]);
        int maxDivsteps = getMaximumDivsteps(bits);
        int divSteps = 0;
        while (divSteps < maxDivsteps) {
            int eta2 = divsteps30(eta, F[i], G[i], t);
            updateDE30(len30, D, E, t, m0Inv32, M);
            updateFG30(len30, F, G, t);
            divSteps += 30;
            i = i;
            maxDivsteps = maxDivsteps;
            eta = eta2;
        }
        int i2 = i;
        int divSteps2 = len30 - 1;
        int signF = F[divSteps2] >> 31;
        cnegate30(len30, signF, F);
        cnormalize30(len30, signF, D, M);
        decode30(bits, D, i2, z, i2);
        return Nat.equalTo(len30, F, 1) & Nat.equalToZero(len30, G);
    }

    public static boolean modOddInverseVar(int[] m, int[] x, int[] z) {
        int len32 = m.length;
        int bits = (len32 << 5) - Integers.numberOfLeadingZeros(m[len32 - 1]);
        int len30 = (bits + 29) / 30;
        int[] t = new int[4];
        int[] D = new int[len30];
        int[] E = new int[len30];
        int[] F = new int[len30];
        int[] G = new int[len30];
        int[] M = new int[len30];
        E[0] = 1;
        encode30(bits, x, 0, G, 0);
        encode30(bits, m, 0, M, 0);
        System.arraycopy(M, 0, F, 0, len30);
        int clzG = Integers.numberOfLeadingZeros(G[len30 - 1] | 1) - (((len30 * 30) + 2) - bits);
        int eta = (-1) - clzG;
        int lenDE = len30;
        int gn = len30;
        int m0Inv32 = inverse32(M[0]);
        int maxDivsteps = getMaximumDivsteps(bits);
        int divsteps = 0;
        while (!Nat.isZero(gn, G)) {
            if (divsteps >= maxDivsteps) {
                return false;
            }
            int divsteps2 = divsteps + 30;
            int divsteps3 = F[0];
            int eta2 = divsteps30Var(eta, divsteps3, G[0], t);
            int eta3 = lenDE;
            int lenFG = gn;
            int maxDivsteps2 = maxDivsteps;
            int eta4 = lenDE;
            int len322 = len32;
            int lenFG2 = lenFG;
            int len302 = len30;
            updateDE30(eta3, D, E, t, m0Inv32, M);
            updateFG30(lenFG2, F, G, t);
            int fn = F[lenFG2 - 1];
            int gn2 = G[lenFG2 - 1];
            int cond = (lenFG2 - 2) >> 31;
            if ((cond | ((fn >> 31) ^ fn) | ((gn2 >> 31) ^ gn2)) == 0) {
                int i = lenFG2 - 2;
                F[i] = F[i] | (fn << 30);
                int i2 = lenFG2 - 2;
                G[i2] = G[i2] | (gn2 << 30);
                lenFG2--;
            }
            gn = lenFG2;
            lenDE = eta4;
            len30 = len302;
            divsteps = divsteps2;
            maxDivsteps = maxDivsteps2;
            eta = eta2;
            len32 = len322;
        }
        int len323 = gn;
        int lenDE2 = lenDE;
        int lenFG3 = len323 - 1;
        int signF = F[lenFG3] >> 31;
        int signD = D[lenDE2 - 1] >> 31;
        if (signD < 0) {
            signD = add30(lenDE2, D, M);
        }
        if (signF < 0) {
            signD = negate30(lenDE2, D);
            negate30(len323, F);
        }
        if (Nat.isOne(len323, F)) {
            if (signD < 0) {
                add30(lenDE2, D, M);
            }
            decode30(bits, D, 0, z, 0);
            return true;
        }
        return false;
    }

    public static int[] random(int[] p) {
        int len = p.length;
        Random rand = new Random();
        int[] s = Nat.create(len);
        int m = p[len - 1];
        int m2 = m | (m >>> 1);
        int m3 = m2 | (m2 >>> 2);
        int m4 = m3 | (m3 >>> 4);
        int m5 = m4 | (m4 >>> 8);
        int m6 = m5 | (m5 >>> 16);
        do {
            for (int i = 0; i != len; i++) {
                s[i] = rand.nextInt();
            }
            int i2 = len - 1;
            s[i2] = s[i2] & m6;
        } while (Nat.gte(len, s, p));
        return s;
    }

    public static void subtract(int[] p, int[] x, int[] y, int[] z) {
        int len = p.length;
        int c = Nat.sub(len, x, y, z);
        if (c != 0) {
            Nat.addTo(len, p, z);
        }
    }

    private static int add30(int len30, int[] D, int[] M) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; i++) {
            int c2 = c + D[i] + M[i];
            D[i] = 1073741823 & c2;
            c = c2 >> 30;
        }
        int i2 = D[last];
        int c3 = c + i2 + M[last];
        D[last] = c3;
        return c3 >> 30;
    }

    private static void cnegate30(int len30, int cond, int[] D) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; i++) {
            int c2 = c + ((D[i] ^ cond) - cond);
            D[i] = 1073741823 & c2;
            c = c2 >> 30;
        }
        int i2 = D[last];
        D[last] = c + ((i2 ^ cond) - cond);
    }

    private static void cnormalize30(int len30, int condNegate, int[] D, int[] M) {
        int last = len30 - 1;
        int c = 0;
        int condAdd = D[last] >> 31;
        for (int i = 0; i < last; i++) {
            int di = D[i] + (M[i] & condAdd);
            int c2 = c + ((di ^ condNegate) - condNegate);
            D[i] = 1073741823 & c2;
            c = c2 >> 30;
        }
        int i2 = D[last];
        int di2 = i2 + (M[last] & condAdd);
        D[last] = c + ((di2 ^ condNegate) - condNegate);
        int c3 = 0;
        int condAdd2 = D[last] >> 31;
        for (int i3 = 0; i3 < last; i3++) {
            int di3 = D[i3] + (M[i3] & condAdd2);
            int c4 = c3 + di3;
            D[i3] = c4 & 1073741823;
            c3 = c4 >> 30;
        }
        int i4 = D[last];
        int di4 = i4 + (M[last] & condAdd2);
        D[last] = c3 + di4;
    }

    private static void decode30(int bits, int[] x, int xOff, int[] z, int zOff) {
        int avail = 0;
        long data = 0;
        while (bits > 0) {
            while (avail < Math.min(32, bits)) {
                data |= x[xOff] << avail;
                avail += 30;
                xOff++;
            }
            z[zOff] = (int) data;
            data >>>= 32;
            avail -= 32;
            bits -= 32;
            zOff++;
        }
    }

    private static int divsteps30(int eta, int f0, int g0, int[] t) {
        int g = g0;
        int g2 = f0;
        int r = 1;
        int q = 0;
        int q2 = 0;
        int v = 1;
        int eta2 = eta;
        for (int i = 0; i < 30; i++) {
            int c1 = eta2 >> 31;
            int c2 = -(g & 1);
            int x = (g2 ^ c1) - c1;
            int y = (v ^ c1) - c1;
            int z = (q2 ^ c1) - c1;
            int g3 = g + (x & c2);
            q += y & c2;
            r += z & c2;
            int c12 = c1 & c2;
            eta2 = (eta2 ^ c12) - (c12 + 1);
            g2 += g3 & c12;
            int u = v + (q & c12);
            int v2 = q2 + (r & c12);
            g = g3 >> 1;
            v = u << 1;
            q2 = v2 << 1;
        }
        t[0] = v;
        t[1] = q2;
        t[2] = q;
        t[3] = r;
        return eta2;
    }

    private static int divsteps30Var(int eta, int f0, int g0, int[] t) {
        int w;
        int i = 30;
        int g = g0;
        int f = f0;
        int r = 1;
        int q = 0;
        int v = 0;
        int u = 1;
        int eta2 = eta;
        while (true) {
            int zeros = Integers.numberOfTrailingZeros(((-1) << i) | g);
            int g2 = g >> zeros;
            u <<= zeros;
            v <<= zeros;
            eta2 -= zeros;
            i -= zeros;
            if (i > 0) {
                if (eta2 < 0) {
                    eta2 = -eta2;
                    int x = f;
                    f = g2;
                    g2 = -x;
                    u = q;
                    q = -u;
                    v = r;
                    r = -v;
                    int limit = eta2 + 1 > i ? i : eta2 + 1;
                    int m = ((-1) >>> (32 - limit)) & 63;
                    w = (f * g2 * ((f * f) - 2)) & m;
                } else {
                    int w2 = eta2 + 1;
                    int limit2 = w2 > i ? i : eta2 + 1;
                    int m2 = ((-1) >>> (32 - limit2)) & 15;
                    int w3 = (((f + 1) & 4) << 1) + f;
                    w = ((-w3) * g2) & m2;
                }
                g = g2 + (f * w);
                q += u * w;
                r += v * w;
            } else {
                t[0] = u;
                t[1] = v;
                t[2] = q;
                t[3] = r;
                return eta2;
            }
        }
    }

    private static void encode30(int bits, int[] x, int xOff, int[] z, int zOff) {
        int avail = 0;
        long data = 0;
        while (bits > 0) {
            if (avail < Math.min(30, bits)) {
                data |= (x[xOff] & 4294967295L) << avail;
                avail += 32;
                xOff++;
            }
            int xOff2 = zOff + 1;
            z[zOff] = ((int) data) & 1073741823;
            data >>>= 30;
            avail -= 30;
            bits -= 30;
            zOff = xOff2;
        }
    }

    private static int getMaximumDivsteps(int bits) {
        return ((bits * 49) + (bits < 46 ? 80 : 47)) / 17;
    }

    private static int negate30(int len30, int[] D) {
        int c = 0;
        int last = len30 - 1;
        for (int i = 0; i < last; i++) {
            int c2 = c - D[i];
            D[i] = 1073741823 & c2;
            c = c2 >> 30;
        }
        int i2 = D[last];
        int c3 = c - i2;
        D[last] = c3;
        return c3 >> 30;
    }

    private static void updateDE30(int len30, int[] D, int[] E, int[] t, int m0Inv32, int[] M) {
        int q = t[0];
        int v = t[1];
        int q2 = t[2];
        int r = t[3];
        int sd = D[len30 - 1] >> 31;
        int se = E[len30 - 1] >> 31;
        int md = (q & sd) + (v & se);
        int me = (q2 & sd) + (r & se);
        int mi = M[0];
        int di = D[0];
        int ei = E[0];
        long cd = (q * di) + (v * ei);
        int di2 = q2;
        long ce = (q2 * di) + (r * ei);
        int md2 = md - (((((int) cd) * m0Inv32) + md) & 1073741823);
        int me2 = me - (((((int) ce) * m0Inv32) + me) & 1073741823);
        long cd2 = (cd + (mi * md2)) >> 30;
        long ce2 = (ce + (mi * me2)) >> 30;
        int i = 1;
        while (i < len30) {
            int mi2 = M[i];
            int di3 = D[i];
            int ei2 = E[i];
            int i2 = i;
            int u = q;
            long cd3 = cd2 + (q * di3) + (v * ei2) + (mi2 * md2);
            int q3 = di2;
            long ce3 = ce2 + (q3 * di3) + (r * ei2) + (mi2 * me2);
            D[i2 - 1] = ((int) cd3) & 1073741823;
            cd2 = cd3 >> 30;
            E[i2 - 1] = ((int) ce3) & 1073741823;
            ce2 = ce3 >> 30;
            i = i2 + 1;
            di2 = q3;
            q = u;
            v = v;
        }
        D[len30 - 1] = (int) cd2;
        E[len30 - 1] = (int) ce2;
    }

    private static void updateFG30(int len30, int[] F, int[] G, int[] t) {
        int u = t[0];
        int v = t[1];
        int q = t[2];
        int r = t[3];
        int fi = F[0];
        int gi = G[0];
        long cf = (u * fi) + (v * gi);
        long cg = (q * fi) + (r * gi);
        long cf2 = cf >> 30;
        long cg2 = cg >> 30;
        int i = 1;
        while (i < len30) {
            int fi2 = F[i];
            int gi2 = G[i];
            int i2 = i;
            long cf3 = cf2 + (u * fi2) + (v * gi2);
            long cg3 = cg2 + (q * fi2) + (r * gi2);
            F[i2 - 1] = ((int) cf3) & 1073741823;
            cf2 = cf3 >> 30;
            G[i2 - 1] = 1073741823 & ((int) cg3);
            cg2 = cg3 >> 30;
            i = i2 + 1;
            u = u;
            v = v;
        }
        F[len30 - 1] = (int) cf2;
        G[len30 - 1] = (int) cg2;
    }
}
