package com.android.internal.org.bouncycastle.crypto.util;
/* loaded from: classes4.dex */
public abstract class Pack {
    public static int bigEndianToInt(byte[] bs, int off) {
        int n = bs[off] << 24;
        int off2 = off + 1;
        int off3 = off2 + 1;
        return n | ((bs[off2] & 255) << 16) | ((bs[off3] & 255) << 8) | (bs[off3 + 1] & 255);
    }

    public static void bigEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = bigEndianToInt(bs, off);
            off += 4;
        }
    }

    public static byte[] intToBigEndian(int n) {
        byte[] bs = new byte[4];
        intToBigEndian(n, bs, 0);
        return bs;
    }

    public static void intToBigEndian(int n, byte[] bs, int off) {
        bs[off] = (byte) (n >>> 24);
        int off2 = off + 1;
        bs[off2] = (byte) (n >>> 16);
        int off3 = off2 + 1;
        bs[off3] = (byte) (n >>> 8);
        bs[off3 + 1] = (byte) n;
    }

    public static byte[] intToBigEndian(int[] ns) {
        byte[] bs = new byte[ns.length * 4];
        intToBigEndian(ns, bs, 0);
        return bs;
    }

    public static void intToBigEndian(int[] ns, byte[] bs, int off) {
        for (int i : ns) {
            intToBigEndian(i, bs, off);
            off += 4;
        }
    }

    public static long bigEndianToLong(byte[] bs, int off) {
        int hi = bigEndianToInt(bs, off);
        int lo = bigEndianToInt(bs, off + 4);
        return ((hi & 4294967295L) << 32) | (4294967295L & lo);
    }

    public static void bigEndianToLong(byte[] bs, int off, long[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = bigEndianToLong(bs, off);
            off += 8;
        }
    }

    public static byte[] longToBigEndian(long n) {
        byte[] bs = new byte[8];
        longToBigEndian(n, bs, 0);
        return bs;
    }

    public static void longToBigEndian(long n, byte[] bs, int off) {
        intToBigEndian((int) (n >>> 32), bs, off);
        intToBigEndian((int) (4294967295L & n), bs, off + 4);
    }

    public static byte[] longToBigEndian(long[] ns) {
        byte[] bs = new byte[ns.length * 8];
        longToBigEndian(ns, bs, 0);
        return bs;
    }

    public static void longToBigEndian(long[] ns, byte[] bs, int off) {
        for (long j : ns) {
            longToBigEndian(j, bs, off);
            off += 8;
        }
    }

    public static int littleEndianToInt(byte[] bs, int off) {
        int n = bs[off] & 255;
        int off2 = off + 1;
        int off3 = off2 + 1;
        return n | ((bs[off2] & 255) << 8) | ((bs[off3] & 255) << 16) | (bs[off3 + 1] << 24);
    }

    public static void littleEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = littleEndianToInt(bs, off);
            off += 4;
        }
    }

    public static void littleEndianToInt(byte[] bs, int bOff, int[] ns, int nOff, int count) {
        for (int i = 0; i < count; i++) {
            ns[nOff + i] = littleEndianToInt(bs, bOff);
            bOff += 4;
        }
    }

    public static byte[] intToLittleEndian(int n) {
        byte[] bs = new byte[4];
        intToLittleEndian(n, bs, 0);
        return bs;
    }

    public static void intToLittleEndian(int n, byte[] bs, int off) {
        bs[off] = (byte) n;
        int off2 = off + 1;
        bs[off2] = (byte) (n >>> 8);
        int off3 = off2 + 1;
        bs[off3] = (byte) (n >>> 16);
        bs[off3 + 1] = (byte) (n >>> 24);
    }

    public static byte[] intToLittleEndian(int[] ns) {
        byte[] bs = new byte[ns.length * 4];
        intToLittleEndian(ns, bs, 0);
        return bs;
    }

    public static void intToLittleEndian(int[] ns, byte[] bs, int off) {
        for (int i : ns) {
            intToLittleEndian(i, bs, off);
            off += 4;
        }
    }

    public static long littleEndianToLong(byte[] bs, int off) {
        int lo = littleEndianToInt(bs, off);
        int hi = littleEndianToInt(bs, off + 4);
        return ((hi & 4294967295L) << 32) | (4294967295L & lo);
    }

    public static void littleEndianToLong(byte[] bs, int off, long[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = littleEndianToLong(bs, off);
            off += 8;
        }
    }

    public static byte[] longToLittleEndian(long n) {
        byte[] bs = new byte[8];
        longToLittleEndian(n, bs, 0);
        return bs;
    }

    public static void longToLittleEndian(long n, byte[] bs, int off) {
        intToLittleEndian((int) (4294967295L & n), bs, off);
        intToLittleEndian((int) (n >>> 32), bs, off + 4);
    }

    public static byte[] longToLittleEndian(long[] ns) {
        byte[] bs = new byte[ns.length * 8];
        longToLittleEndian(ns, bs, 0);
        return bs;
    }

    public static void longToLittleEndian(long[] ns, byte[] bs, int off) {
        for (long j : ns) {
            longToLittleEndian(j, bs, off);
            off += 8;
        }
    }
}
