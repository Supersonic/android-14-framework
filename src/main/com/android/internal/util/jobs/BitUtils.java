package com.android.internal.util.jobs;

import android.text.TextUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.IntFunction;
/* loaded from: classes.dex */
public final class BitUtils {
    public static long bitAt(int i) {
        return 1 << i;
    }

    public static boolean maskedEquals(byte b, byte b2, byte b3) {
        return (b & b3) == (b2 & b3);
    }

    public static boolean maskedEquals(long j, long j2, long j3) {
        return (j & j3) == (j2 & j3);
    }

    public static int uint16(byte b, byte b2) {
        return ((b & 255) << 8) | (b2 & 255);
    }

    public static int uint16(short s) {
        return s & 65535;
    }

    public static long uint32(int i) {
        return i & 4294967295L;
    }

    public static int uint8(byte b) {
        return b & 255;
    }

    public static boolean maskedEquals(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null || bArr2 == null) {
            return bArr == bArr2;
        }
        Preconditions.checkArgument(bArr.length == bArr2.length, "Inputs must be of same size");
        if (bArr3 == null) {
            return Arrays.equals(bArr, bArr2);
        }
        Preconditions.checkArgument(bArr.length == bArr3.length, "Mask must be of same size as inputs");
        for (int i = 0; i < bArr3.length; i++) {
            if (!maskedEquals(bArr[i], bArr2[i], bArr3[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean maskedEquals(UUID uuid, UUID uuid2, UUID uuid3) {
        if (uuid3 == null) {
            return Objects.equals(uuid, uuid2);
        }
        return maskedEquals(uuid.getLeastSignificantBits(), uuid2.getLeastSignificantBits(), uuid3.getLeastSignificantBits()) && maskedEquals(uuid.getMostSignificantBits(), uuid2.getMostSignificantBits(), uuid3.getMostSignificantBits());
    }

    public static int[] unpackBits(long j) {
        int[] iArr = new int[Long.bitCount(j)];
        int i = 0;
        int i2 = 0;
        while (j != 0) {
            if ((j & 1) == 1) {
                iArr[i] = i2;
                i++;
            }
            j >>>= 1;
            i2++;
        }
        return iArr;
    }

    public static long packBits(int[] iArr) {
        long j = 0;
        for (int i : iArr) {
            j |= 1 << i;
        }
        return j;
    }

    public static int bytesToBEInt(byte[] bArr) {
        return (uint8(bArr[0]) << 24) + (uint8(bArr[1]) << 16) + (uint8(bArr[2]) << 8) + uint8(bArr[3]);
    }

    public static int bytesToLEInt(byte[] bArr) {
        return Integer.reverseBytes(bytesToBEInt(bArr));
    }

    public static int getUint8(ByteBuffer byteBuffer, int i) {
        return uint8(byteBuffer.get(i));
    }

    public static int getUint16(ByteBuffer byteBuffer, int i) {
        return uint16(byteBuffer.getShort(i));
    }

    public static long getUint32(ByteBuffer byteBuffer, int i) {
        return uint32(byteBuffer.getInt(i));
    }

    public static void put(ByteBuffer byteBuffer, int i, byte[] bArr) {
        int position = byteBuffer.position();
        byteBuffer.position(i);
        byteBuffer.put(bArr);
        byteBuffer.position(position);
    }

    public static boolean isBitSet(long j, int i) {
        return (j & bitAt(i)) != 0;
    }

    public static String flagsToString(int i, IntFunction<String> intFunction) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        while (i != 0) {
            int numberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= ~numberOfTrailingZeros;
            if (i2 > 0) {
                sb.append(", ");
            }
            sb.append(intFunction.apply(numberOfTrailingZeros));
            i2++;
        }
        TextUtils.wrap(sb, "[", "]");
        return sb.toString();
    }

    public static byte[] toBytes(long j) {
        return ByteBuffer.allocate(8).putLong(j).array();
    }

    public static int flagsUpTo(int i) {
        if (i <= 0) {
            return 0;
        }
        return i | flagsUpTo(i >> 1);
    }

    public static int flagsWithin(int i, int i2) {
        return i | (flagsUpTo(i2) & (~flagsUpTo(i)));
    }
}
