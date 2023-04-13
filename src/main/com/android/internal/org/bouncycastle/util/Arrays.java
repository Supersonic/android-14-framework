package com.android.internal.org.bouncycastle.util;

import java.math.BigInteger;
import java.util.NoSuchElementException;
/* loaded from: classes4.dex */
public final class Arrays {
    private Arrays() {
    }

    public static boolean areAllZeroes(byte[] buf, int off, int len) {
        int bits = 0;
        for (int i = 0; i < len; i++) {
            bits |= buf[off + i];
        }
        return bits == 0;
    }

    public static boolean areEqual(boolean[] a, boolean[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(byte[] a, byte[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength) {
            return false;
        }
        for (int i = 0; i < aLength; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean areEqual(char[] a, char[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(int[] a, int[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(long[] a, long[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(Object[] a, Object[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean areEqual(short[] a, short[] b) {
        return java.util.Arrays.equals(a, b);
    }

    public static boolean constantTimeAreEqual(byte[] expected, byte[] supplied) {
        if (expected == null || supplied == null) {
            return false;
        }
        if (expected == supplied) {
            return true;
        }
        int len = expected.length < supplied.length ? expected.length : supplied.length;
        int nonEqual = expected.length ^ supplied.length;
        for (int i = 0; i != len; i++) {
            nonEqual |= expected[i] ^ supplied[i];
        }
        for (int i2 = len; i2 < supplied.length; i2++) {
            nonEqual |= supplied[i2] ^ (~supplied[i2]);
        }
        if (nonEqual != 0) {
            return false;
        }
        return true;
    }

    public static boolean constantTimeAreEqual(int len, byte[] a, int aOff, byte[] b, int bOff) {
        if (a == null) {
            throw new NullPointerException("'a' cannot be null");
        }
        if (b == null) {
            throw new NullPointerException("'b' cannot be null");
        }
        if (len < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (aOff > a.length - len) {
            throw new IndexOutOfBoundsException("'aOff' value invalid for specified length");
        }
        if (bOff > b.length - len) {
            throw new IndexOutOfBoundsException("'bOff' value invalid for specified length");
        }
        int d = 0;
        for (int i = 0; i < len; i++) {
            d |= a[aOff + i] ^ b[bOff + i];
        }
        return d == 0;
    }

    public static int compareUnsigned(byte[] a, byte[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        int minLen = Math.min(a.length, b.length);
        for (int i = 0; i < minLen; i++) {
            int aVal = a[i] & 255;
            int bVal = b[i] & 255;
            if (aVal < bVal) {
                return -1;
            }
            if (aVal > bVal) {
                return 1;
            }
        }
        int i2 = a.length;
        if (i2 < b.length) {
            return -1;
        }
        if (a.length <= b.length) {
            return 0;
        }
        return 1;
    }

    public static boolean contains(boolean[] a, boolean val) {
        for (boolean z : a) {
            if (z == val) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(byte[] a, byte val) {
        for (byte b : a) {
            if (b == val) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(char[] a, char val) {
        for (char c : a) {
            if (c == val) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] a, int val) {
        for (int i : a) {
            if (i == val) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(long[] a, long val) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == val) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(short[] a, short val) {
        for (short s : a) {
            if (s == val) {
                return true;
            }
        }
        return false;
    }

    public static void fill(boolean[] a, boolean val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(boolean[] a, int fromIndex, int toIndex, boolean val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(byte[] a, byte val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(byte[] a, int fromIndex, byte val) {
        fill(a, fromIndex, a.length, val);
    }

    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(char[] a, char val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(char[] a, int fromIndex, int toIndex, char val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(int[] a, int val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(int[] a, int fromIndex, int val) {
        java.util.Arrays.fill(a, fromIndex, a.length, val);
    }

    public static void fill(int[] a, int fromIndex, int toIndex, int val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(long[] a, long val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(long[] a, int fromIndex, long val) {
        java.util.Arrays.fill(a, fromIndex, a.length, val);
    }

    public static void fill(long[] a, int fromIndex, int toIndex, long val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(Object[] a, Object val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(short[] a, short val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(short[] a, int fromIndex, short val) {
        java.util.Arrays.fill(a, fromIndex, a.length, val);
    }

    public static void fill(short[] a, int fromIndex, int toIndex, short val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static int hashCode(byte[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ data[i];
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(byte[] data, int off, int len) {
        if (data == null) {
            return 0;
        }
        int i = len;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ data[off + i];
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(char[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ data[i];
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(int[][] ints) {
        int hc = 0;
        for (int i = 0; i != ints.length; i++) {
            hc = (hc * 257) + hashCode(ints[i]);
        }
        return hc;
    }

    public static int hashCode(int[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ data[i];
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(int[] data, int off, int len) {
        if (data == null) {
            return 0;
        }
        int i = len;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ data[off + i];
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(long[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                long di = data[i];
                hc = (((hc * 257) ^ ((int) di)) * 257) ^ ((int) (di >>> 32));
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(long[] data, int off, int len) {
        if (data == null) {
            return 0;
        }
        int i = len;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                long di = data[off + i];
                hc = (((hc * 257) ^ ((int) di)) * 257) ^ ((int) (di >>> 32));
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(short[][][] shorts) {
        int hc = 0;
        for (int i = 0; i != shorts.length; i++) {
            hc = (hc * 257) + hashCode(shorts[i]);
        }
        return hc;
    }

    public static int hashCode(short[][] shorts) {
        int hc = 0;
        for (int i = 0; i != shorts.length; i++) {
            hc = (hc * 257) + hashCode(shorts[i]);
        }
        return hc;
    }

    public static int hashCode(short[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ (data[i] & 255);
            } else {
                return hc;
            }
        }
    }

    public static int hashCode(Object[] data) {
        if (data == null) {
            return 0;
        }
        int i = data.length;
        int hc = i + 1;
        while (true) {
            i--;
            if (i >= 0) {
                hc = (hc * 257) ^ Objects.hashCode(data[i]);
            } else {
                return hc;
            }
        }
    }

    public static boolean[] clone(boolean[] data) {
        if (data == null) {
            return null;
        }
        return (boolean[]) data.clone();
    }

    public static byte[] clone(byte[] data) {
        if (data == null) {
            return null;
        }
        return (byte[]) data.clone();
    }

    public static char[] clone(char[] data) {
        if (data == null) {
            return null;
        }
        return (char[]) data.clone();
    }

    public static int[] clone(int[] data) {
        if (data == null) {
            return null;
        }
        return (int[]) data.clone();
    }

    public static long[] clone(long[] data) {
        if (data == null) {
            return null;
        }
        return (long[]) data.clone();
    }

    public static short[] clone(short[] data) {
        if (data == null) {
            return null;
        }
        return (short[]) data.clone();
    }

    public static BigInteger[] clone(BigInteger[] data) {
        if (data == null) {
            return null;
        }
        return (BigInteger[]) data.clone();
    }

    public static byte[] clone(byte[] data, byte[] existing) {
        if (data == null) {
            return null;
        }
        if (existing == null || existing.length != data.length) {
            return clone(data);
        }
        System.arraycopy(data, 0, existing, 0, existing.length);
        return existing;
    }

    public static long[] clone(long[] data, long[] existing) {
        if (data == null) {
            return null;
        }
        if (existing == null || existing.length != data.length) {
            return clone(data);
        }
        System.arraycopy(data, 0, existing, 0, existing.length);
        return existing;
    }

    public static byte[][] clone(byte[][] data) {
        if (data == null) {
            return null;
        }
        byte[][] copy = new byte[data.length];
        for (int i = 0; i != copy.length; i++) {
            copy[i] = clone(data[i]);
        }
        return copy;
    }

    public static byte[][][] clone(byte[][][] data) {
        if (data == null) {
            return null;
        }
        byte[][][] copy = new byte[data.length][];
        for (int i = 0; i != copy.length; i++) {
            copy[i] = clone(data[i]);
        }
        return copy;
    }

    public static boolean[] copyOf(boolean[] original, int newLength) {
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static int[] copyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static long[] copyOf(long[] original, int newLength) {
        long[] copy = new long[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static short[] copyOf(short[] original, int newLength) {
        short[] copy = new short[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static BigInteger[] copyOf(BigInteger[] original, int newLength) {
        BigInteger[] copy = new BigInteger[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static boolean[] copyOfRange(boolean[] original, int from, int to) {
        int newLength = getLength(from, to);
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = getLength(from, to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        int newLength = getLength(from, to);
        char[] copy = new char[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static int[] copyOfRange(int[] original, int from, int to) {
        int newLength = getLength(from, to);
        int[] copy = new int[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static long[] copyOfRange(long[] original, int from, int to) {
        int newLength = getLength(from, to);
        long[] copy = new long[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static short[] copyOfRange(short[] original, int from, int to) {
        int newLength = getLength(from, to);
        short[] copy = new short[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static BigInteger[] copyOfRange(BigInteger[] original, int from, int to) {
        int newLength = getLength(from, to);
        BigInteger[] copy = new BigInteger[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    private static int getLength(int from, int to) {
        int newLength = to - from;
        if (newLength < 0) {
            StringBuffer sb = new StringBuffer(from);
            sb.append(" > ").append(to);
            throw new IllegalArgumentException(sb.toString());
        }
        return newLength;
    }

    public static byte[] append(byte[] a, byte b) {
        if (a == null) {
            return new byte[]{b};
        }
        int length = a.length;
        byte[] result = new byte[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }

    public static short[] append(short[] a, short b) {
        if (a == null) {
            return new short[]{b};
        }
        int length = a.length;
        short[] result = new short[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }

    public static int[] append(int[] a, int b) {
        if (a == null) {
            return new int[]{b};
        }
        int length = a.length;
        int[] result = new int[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }

    public static String[] append(String[] a, String b) {
        if (a == null) {
            return new String[]{b};
        }
        int length = a.length;
        String[] result = new String[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }

    public static byte[] concatenate(byte[] a, byte[] b) {
        if (a == null) {
            return clone(b);
        }
        if (b == null) {
            return clone(a);
        }
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static short[] concatenate(short[] a, short[] b) {
        if (a == null) {
            return clone(b);
        }
        if (b == null) {
            return clone(a);
        }
        short[] r = new short[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static byte[] concatenate(byte[] a, byte[] b, byte[] c) {
        if (a == null) {
            return concatenate(b, c);
        }
        if (b == null) {
            return concatenate(a, c);
        }
        if (c == null) {
            return concatenate(a, b);
        }
        byte[] r = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, r, 0, a.length);
        int pos = 0 + a.length;
        System.arraycopy(b, 0, r, pos, b.length);
        System.arraycopy(c, 0, r, pos + b.length, c.length);
        return r;
    }

    public static byte[] concatenate(byte[] a, byte[] b, byte[] c, byte[] d) {
        if (a == null) {
            return concatenate(b, c, d);
        }
        if (b == null) {
            return concatenate(a, c, d);
        }
        if (c == null) {
            return concatenate(a, b, d);
        }
        if (d == null) {
            return concatenate(a, b, c);
        }
        byte[] r = new byte[a.length + b.length + c.length + d.length];
        System.arraycopy(a, 0, r, 0, a.length);
        int pos = 0 + a.length;
        System.arraycopy(b, 0, r, pos, b.length);
        int pos2 = pos + b.length;
        System.arraycopy(c, 0, r, pos2, c.length);
        System.arraycopy(d, 0, r, pos2 + c.length, d.length);
        return r;
    }

    public static byte[] concatenate(byte[][] arrays) {
        int size = 0;
        for (int i = 0; i != arrays.length; i++) {
            size += arrays[i].length;
        }
        byte[] rv = new byte[size];
        int offSet = 0;
        for (int i2 = 0; i2 != arrays.length; i2++) {
            System.arraycopy(arrays[i2], 0, rv, offSet, arrays[i2].length);
            offSet += arrays[i2].length;
        }
        return rv;
    }

    public static int[] concatenate(int[] a, int[] b) {
        if (a == null) {
            return clone(b);
        }
        if (b == null) {
            return clone(a);
        }
        int[] r = new int[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static byte[] prepend(byte[] a, byte b) {
        if (a == null) {
            return new byte[]{b};
        }
        int length = a.length;
        byte[] result = new byte[length + 1];
        System.arraycopy(a, 0, result, 1, length);
        result[0] = b;
        return result;
    }

    public static short[] prepend(short[] a, short b) {
        if (a == null) {
            return new short[]{b};
        }
        int length = a.length;
        short[] result = new short[length + 1];
        System.arraycopy(a, 0, result, 1, length);
        result[0] = b;
        return result;
    }

    public static int[] prepend(int[] a, int b) {
        if (a == null) {
            return new int[]{b};
        }
        int length = a.length;
        int[] result = new int[length + 1];
        System.arraycopy(a, 0, result, 1, length);
        result[0] = b;
        return result;
    }

    public static byte[] reverse(byte[] a) {
        if (a == null) {
            return null;
        }
        int p1 = 0;
        int p2 = a.length;
        byte[] result = new byte[p2];
        while (true) {
            p2--;
            if (p2 >= 0) {
                result[p2] = a[p1];
                p1++;
            } else {
                return result;
            }
        }
    }

    public static int[] reverse(int[] a) {
        if (a == null) {
            return null;
        }
        int p1 = 0;
        int p2 = a.length;
        int[] result = new int[p2];
        while (true) {
            p2--;
            if (p2 >= 0) {
                result[p2] = a[p1];
                p1++;
            } else {
                return result;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class Iterator<T> implements java.util.Iterator<T> {
        private final T[] dataArray;
        private int position = 0;

        public Iterator(T[] dataArray) {
            this.dataArray = dataArray;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.position < this.dataArray.length;
        }

        @Override // java.util.Iterator
        public T next() {
            int i = this.position;
            T[] tArr = this.dataArray;
            if (i == tArr.length) {
                throw new NoSuchElementException("Out of elements: " + this.position);
            }
            this.position = i + 1;
            return tArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove element from an Array.");
        }
    }

    public static void clear(byte[] data) {
        if (data != null) {
            java.util.Arrays.fill(data, (byte) 0);
        }
    }

    public static void clear(int[] data) {
        if (data != null) {
            java.util.Arrays.fill(data, 0);
        }
    }

    public static boolean isNullOrContainsNull(Object[] array) {
        if (array == null) {
            return true;
        }
        for (Object obj : array) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNullOrEmpty(byte[] array) {
        return array == null || array.length < 1;
    }

    public static boolean isNullOrEmpty(int[] array) {
        return array == null || array.length < 1;
    }

    public static boolean isNullOrEmpty(Object[] array) {
        return array == null || array.length < 1;
    }
}
