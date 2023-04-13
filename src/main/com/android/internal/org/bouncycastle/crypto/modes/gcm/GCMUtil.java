package com.android.internal.org.bouncycastle.crypto.modes.gcm;

import android.content.Context;
import com.android.internal.org.bouncycastle.math.raw.Interleave;
import com.android.internal.org.bouncycastle.util.Longs;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public abstract class GCMUtil {

    /* renamed from: E1 */
    private static final int f764E1 = -520093696;
    private static final long E1L = -2233785415175766016L;

    public static byte[] oneAsBytes() {
        byte[] tmp = new byte[16];
        tmp[0] = Byte.MIN_VALUE;
        return tmp;
    }

    public static int[] oneAsInts() {
        int[] tmp = new int[4];
        tmp[0] = Integer.MIN_VALUE;
        return tmp;
    }

    public static long[] oneAsLongs() {
        long[] tmp = {Long.MIN_VALUE};
        return tmp;
    }

    public static byte[] asBytes(int[] x) {
        byte[] z = new byte[16];
        Pack.intToBigEndian(x, z, 0);
        return z;
    }

    public static void asBytes(int[] x, byte[] z) {
        Pack.intToBigEndian(x, z, 0);
    }

    public static byte[] asBytes(long[] x) {
        byte[] z = new byte[16];
        Pack.longToBigEndian(x, z, 0);
        return z;
    }

    public static void asBytes(long[] x, byte[] z) {
        Pack.longToBigEndian(x, z, 0);
    }

    public static int[] asInts(byte[] x) {
        int[] z = new int[4];
        Pack.bigEndianToInt(x, 0, z);
        return z;
    }

    public static void asInts(byte[] x, int[] z) {
        Pack.bigEndianToInt(x, 0, z);
    }

    public static long[] asLongs(byte[] x) {
        long[] z = new long[2];
        Pack.bigEndianToLong(x, 0, z);
        return z;
    }

    public static void asLongs(byte[] x, long[] z) {
        Pack.bigEndianToLong(x, 0, z);
    }

    public static void copy(int[] x, int[] z) {
        z[0] = x[0];
        z[1] = x[1];
        z[2] = x[2];
        z[3] = x[3];
    }

    public static void copy(long[] x, long[] z) {
        z[0] = x[0];
        z[1] = x[1];
    }

    public static void divideP(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long m = x0 >> 63;
        z[0] = ((x0 ^ (E1L & m)) << 1) | (x1 >>> 63);
        z[1] = (x1 << 1) | (-m);
    }

    public static void multiply(byte[] x, byte[] y) {
        long[] t1 = asLongs(x);
        long[] t2 = asLongs(y);
        multiply(t1, t2);
        asBytes(t1, x);
    }

    public static void multiply(int[] x, int[] y) {
        int y0 = y[0];
        int y1 = y[1];
        int y2 = y[2];
        int y3 = y[3];
        int z0 = 0;
        int z1 = 0;
        int z2 = 0;
        int z3 = 0;
        for (int i = 0; i < 4; i++) {
            int bits = x[i];
            for (int j = 0; j < 32; j++) {
                int m1 = bits >> 31;
                bits <<= 1;
                z0 ^= y0 & m1;
                z1 ^= y1 & m1;
                z2 ^= y2 & m1;
                z3 ^= y3 & m1;
                int m2 = (y3 << 31) >> 8;
                y3 = (y3 >>> 1) | (y2 << 31);
                y2 = (y2 >>> 1) | (y1 << 31);
                y1 = (y1 >>> 1) | (y0 << 31);
                y0 = (y0 >>> 1) ^ (m2 & f764E1);
            }
        }
        x[0] = z0;
        x[1] = z1;
        x[2] = z2;
        x[3] = z3;
    }

    public static void multiply(long[] x, long[] y) {
        long x0 = x[0];
        long x1 = x[1];
        long y0 = y[0];
        long y1 = y[1];
        long x0r = Longs.reverse(x0);
        long x1r = Longs.reverse(x1);
        long y0r = Longs.reverse(y0);
        long y1r = Longs.reverse(y1);
        long h0 = Longs.reverse(implMul64(x0r, y0r));
        long h1 = implMul64(x0, y0) << 1;
        long h2 = Longs.reverse(implMul64(x1r, y1r));
        long h3 = implMul64(x1, y1) << 1;
        long h4 = Longs.reverse(implMul64(x0r ^ x1r, y0r ^ y1r));
        long h5 = implMul64(x0 ^ x1, y0 ^ y1) << 1;
        long z1 = ((h1 ^ h0) ^ h2) ^ h4;
        long z2 = (((h2 ^ h1) ^ h3) ^ h5) ^ ((h3 << 62) ^ (h3 << 57));
        long z0 = h0 ^ (((z2 ^ (z2 >>> 1)) ^ (z2 >>> 2)) ^ (z2 >>> 7));
        x[0] = z0;
        x[1] = (z1 ^ (((h3 ^ (h3 >>> 1)) ^ (h3 >>> 2)) ^ (h3 >>> 7))) ^ (((z2 << 63) ^ (z2 << 62)) ^ (z2 << 57));
    }

    public static void multiplyP(int[] x) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int m = (x3 << 31) >> 31;
        x[0] = (x0 >>> 1) ^ (f764E1 & m);
        x[1] = (x1 >>> 1) | (x0 << 31);
        x[2] = (x2 >>> 1) | (x1 << 31);
        x[3] = (x3 >>> 1) | (x2 << 31);
    }

    public static void multiplyP(int[] x, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int m = (x3 << 31) >> 31;
        z[0] = (x0 >>> 1) ^ (f764E1 & m);
        z[1] = (x1 >>> 1) | (x0 << 31);
        z[2] = (x2 >>> 1) | (x1 << 31);
        z[3] = (x3 >>> 1) | (x2 << 31);
    }

    public static void multiplyP(long[] x) {
        long x0 = x[0];
        long x1 = x[1];
        long m = (x1 << 63) >> 63;
        x[0] = (x0 >>> 1) ^ (E1L & m);
        x[1] = (x1 >>> 1) | (x0 << 63);
    }

    public static void multiplyP(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long m = (x1 << 63) >> 63;
        z[0] = (x0 >>> 1) ^ (E1L & m);
        z[1] = (x1 >>> 1) | (x0 << 63);
    }

    public static void multiplyP3(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long c = x1 << 61;
        z[0] = ((((x0 >>> 3) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        z[1] = (x1 >>> 3) | (x0 << 61);
    }

    public static void multiplyP4(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long c = x1 << 60;
        z[0] = ((((x0 >>> 4) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        z[1] = (x1 >>> 4) | (x0 << 60);
    }

    public static void multiplyP7(long[] x, long[] z) {
        long x0 = x[0];
        long x1 = x[1];
        long c = x1 << 57;
        z[0] = ((((x0 >>> 7) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        z[1] = (x1 >>> 7) | (x0 << 57);
    }

    public static void multiplyP8(int[] x) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int c = x3 << 24;
        x[0] = ((((x0 >>> 8) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        x[1] = (x1 >>> 8) | (x0 << 24);
        x[2] = (x2 >>> 8) | (x1 << 24);
        x[3] = (x3 >>> 8) | (x2 << 24);
    }

    public static void multiplyP8(int[] x, int[] y) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int c = x3 << 24;
        y[0] = ((((x0 >>> 8) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        y[1] = (x1 >>> 8) | (x0 << 24);
        y[2] = (x2 >>> 8) | (x1 << 24);
        y[3] = (x3 >>> 8) | (x2 << 24);
    }

    public static void multiplyP8(long[] x) {
        long x0 = x[0];
        long x1 = x[1];
        long c = x1 << 56;
        x[0] = ((((x0 >>> 8) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        x[1] = (x1 >>> 8) | (x0 << 56);
    }

    public static void multiplyP8(long[] x, long[] y) {
        long x0 = x[0];
        long x1 = x[1];
        long c = x1 << 56;
        y[0] = ((((x0 >>> 8) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        y[1] = (x1 >>> 8) | (x0 << 56);
    }

    public static long[] pAsLongs() {
        long[] tmp = {Context.BIND_EXTERNAL_SERVICE_LONG};
        return tmp;
    }

    public static void square(long[] x, long[] z) {
        long[] t = new long[4];
        Interleave.expand64To128Rev(x[0], t, 0);
        Interleave.expand64To128Rev(x[1], t, 2);
        long z0 = t[0];
        long z1 = t[1];
        long z2 = t[2];
        long z3 = t[3];
        long z22 = z2 ^ (((z3 << 63) ^ (z3 << 62)) ^ (z3 << 57));
        z[0] = z0 ^ ((((z22 >>> 1) ^ z22) ^ (z22 >>> 2)) ^ (z22 >>> 7));
        z[1] = (z1 ^ ((((z3 >>> 1) ^ z3) ^ (z3 >>> 2)) ^ (z3 >>> 7))) ^ (((z22 << 62) ^ (z22 << 63)) ^ (z22 << 57));
    }

    public static void xor(byte[] x, byte[] y) {
        int i = 0;
        do {
            x[i] = (byte) (x[i] ^ y[i]);
            int i2 = i + 1;
            x[i2] = (byte) (x[i2] ^ y[i2]);
            int i3 = i2 + 1;
            x[i3] = (byte) (x[i3] ^ y[i3]);
            int i4 = i3 + 1;
            x[i4] = (byte) (x[i4] ^ y[i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(byte[] x, byte[] y, int yOff) {
        int i = 0;
        do {
            x[i] = (byte) (x[i] ^ y[yOff + i]);
            int i2 = i + 1;
            x[i2] = (byte) (x[i2] ^ y[yOff + i2]);
            int i3 = i2 + 1;
            x[i3] = (byte) (x[i3] ^ y[yOff + i3]);
            int i4 = i3 + 1;
            x[i4] = (byte) (x[i4] ^ y[yOff + i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(byte[] x, int xOff, byte[] y, int yOff, byte[] z, int zOff) {
        int i = 0;
        do {
            z[zOff + i] = (byte) (x[xOff + i] ^ y[yOff + i]);
            int i2 = i + 1;
            z[zOff + i2] = (byte) (x[xOff + i2] ^ y[yOff + i2]);
            int i3 = i2 + 1;
            z[zOff + i3] = (byte) (x[xOff + i3] ^ y[yOff + i3]);
            int i4 = i3 + 1;
            z[zOff + i4] = (byte) (x[xOff + i4] ^ y[yOff + i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(byte[] x, byte[] y, int yOff, int yLen) {
        while (true) {
            yLen--;
            if (yLen >= 0) {
                x[yLen] = (byte) (x[yLen] ^ y[yOff + yLen]);
            } else {
                return;
            }
        }
    }

    public static void xor(byte[] x, int xOff, byte[] y, int yOff, int len) {
        while (true) {
            len--;
            if (len >= 0) {
                int i = xOff + len;
                x[i] = (byte) (x[i] ^ y[yOff + len]);
            } else {
                return;
            }
        }
    }

    public static void xor(byte[] x, byte[] y, byte[] z) {
        int i = 0;
        do {
            z[i] = (byte) (x[i] ^ y[i]);
            int i2 = i + 1;
            z[i2] = (byte) (x[i2] ^ y[i2]);
            int i3 = i2 + 1;
            z[i3] = (byte) (x[i3] ^ y[i3]);
            int i4 = i3 + 1;
            z[i4] = (byte) (x[i4] ^ y[i4]);
            i = i4 + 1;
        } while (i < 16);
    }

    public static void xor(int[] x, int[] y) {
        x[0] = x[0] ^ y[0];
        x[1] = x[1] ^ y[1];
        x[2] = x[2] ^ y[2];
        x[3] = x[3] ^ y[3];
    }

    public static void xor(int[] x, int[] y, int[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
    }

    public static void xor(long[] x, long[] y) {
        x[0] = x[0] ^ y[0];
        x[1] = x[1] ^ y[1];
    }

    public static void xor(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
    }

    private static long implMul64(long x, long y) {
        long x0 = x & 1229782938247303441L;
        long x1 = x & 2459565876494606882L;
        long x2 = x & 4919131752989213764L;
        long x3 = x & (-8608480567731124088L);
        long y0 = y & 1229782938247303441L;
        long y1 = y & 2459565876494606882L;
        long y2 = y & 4919131752989213764L;
        long y3 = y & (-8608480567731124088L);
        long z0 = (((x0 * y0) ^ (x1 * y3)) ^ (x2 * y2)) ^ (x3 * y1);
        long z1 = (((x0 * y1) ^ (x1 * y0)) ^ (x2 * y3)) ^ (x3 * y2);
        long z2 = (((x0 * y2) ^ (x1 * y1)) ^ (x2 * y0)) ^ (x3 * y3);
        long z3 = (((x0 * y3) ^ (x1 * y2)) ^ (x2 * y1)) ^ (x3 * y0);
        return (z0 & 1229782938247303441L) | (z1 & 2459565876494606882L) | (z2 & 4919131752989213764L) | (z3 & (-8608480567731124088L));
    }
}
