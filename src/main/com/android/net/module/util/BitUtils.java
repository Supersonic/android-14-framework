package com.android.net.module.util;

import com.android.internal.content.NativeLibraryHelper;
/* loaded from: classes5.dex */
public class BitUtils {

    @FunctionalInterface
    /* loaded from: classes5.dex */
    public interface NameOf {
        String nameOf(int i);
    }

    public static int[] unpackBits(long val) {
        int size = Long.bitCount(val);
        int[] result = new int[size];
        int index = 0;
        int bitPos = 0;
        while (val != 0) {
            if ((val & 1) == 1) {
                result[index] = bitPos;
                index++;
            }
            val >>>= 1;
            bitPos++;
        }
        return result;
    }

    public static long packBitList(int... bits) {
        return packBits(bits);
    }

    public static long packBits(int[] bits) {
        long packed = 0;
        for (int b : bits) {
            packed |= 1 << b;
        }
        return packed;
    }

    public static void appendStringRepresentationOfBitMaskToStringBuilder(StringBuilder sb, long bitMask, NameOf nameFetcher, String separator) {
        int bitPos = 0;
        boolean firstElementAdded = false;
        while (bitMask != 0) {
            if ((1 & bitMask) != 0) {
                if (firstElementAdded) {
                    sb.append(separator);
                } else {
                    firstElementAdded = true;
                }
                sb.append(nameFetcher.nameOf(bitPos));
            }
            bitMask >>>= 1;
            bitPos++;
        }
    }

    public static String describeDifferences(long oldVal, long newVal, NameOf nameFetcher) {
        long changed = oldVal ^ newVal;
        if (0 == changed) {
            return null;
        }
        long removed = oldVal & changed;
        long added = newVal & changed;
        StringBuilder sb = new StringBuilder();
        if (0 != removed) {
            sb.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            appendStringRepresentationOfBitMaskToStringBuilder(sb, removed, nameFetcher, NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
        }
        if (0 != added) {
            sb.append("+");
            appendStringRepresentationOfBitMaskToStringBuilder(sb, added, nameFetcher, "+");
        }
        return sb.toString();
    }
}
