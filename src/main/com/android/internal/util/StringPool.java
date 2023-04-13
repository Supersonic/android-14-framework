package com.android.internal.util;
/* loaded from: classes3.dex */
public final class StringPool {
    private final String[] mPool = new String[512];

    private static boolean contentEquals(String s, char[] chars, int start, int length) {
        if (s.length() != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[start + i] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public String get(char[] array, int start, int length) {
        int hashCode = 0;
        for (int i = start; i < start + length; i++) {
            hashCode = (hashCode * 31) + array[i];
        }
        int i2 = hashCode >>> 20;
        int hashCode2 = hashCode ^ (i2 ^ (hashCode >>> 12));
        String[] strArr = this.mPool;
        int index = (strArr.length - 1) & (hashCode2 ^ ((hashCode2 >>> 7) ^ (hashCode2 >>> 4)));
        String pooled = strArr[index];
        if (pooled != null && contentEquals(pooled, array, start, length)) {
            return pooled;
        }
        String result = new String(array, start, length);
        this.mPool[index] = result;
        return result;
    }
}
