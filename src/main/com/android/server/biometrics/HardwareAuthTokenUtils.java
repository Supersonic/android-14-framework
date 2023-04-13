package com.android.server.biometrics;

import android.hardware.keymaster.HardwareAuthToken;
import android.hardware.keymaster.Timestamp;
import java.nio.ByteOrder;
/* loaded from: classes.dex */
public class HardwareAuthTokenUtils {
    public static byte[] toByteArray(HardwareAuthToken hardwareAuthToken) {
        byte[] bArr = new byte[69];
        bArr[0] = 0;
        writeLong(hardwareAuthToken.challenge, bArr, 1);
        writeLong(hardwareAuthToken.userId, bArr, 9);
        writeLong(hardwareAuthToken.authenticatorId, bArr, 17);
        writeInt(flipIfNativelyLittle(hardwareAuthToken.authenticatorType), bArr, 25);
        writeLong(flipIfNativelyLittle(hardwareAuthToken.timestamp.milliSeconds), bArr, 29);
        byte[] bArr2 = hardwareAuthToken.mac;
        System.arraycopy(bArr2, 0, bArr, 37, bArr2.length);
        return bArr;
    }

    public static HardwareAuthToken toHardwareAuthToken(byte[] bArr) {
        HardwareAuthToken hardwareAuthToken = new HardwareAuthToken();
        hardwareAuthToken.challenge = getLong(bArr, 1);
        hardwareAuthToken.userId = getLong(bArr, 9);
        hardwareAuthToken.authenticatorId = getLong(bArr, 17);
        hardwareAuthToken.authenticatorType = flipIfNativelyLittle(getInt(bArr, 25));
        Timestamp timestamp = new Timestamp();
        timestamp.milliSeconds = flipIfNativelyLittle(getLong(bArr, 29));
        hardwareAuthToken.timestamp = timestamp;
        byte[] bArr2 = new byte[32];
        hardwareAuthToken.mac = bArr2;
        System.arraycopy(bArr, 37, bArr2, 0, 32);
        return hardwareAuthToken;
    }

    public static long flipIfNativelyLittle(long j) {
        return ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder() ? Long.reverseBytes(j) : j;
    }

    public static int flipIfNativelyLittle(int i) {
        return ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder() ? Integer.reverseBytes(i) : i;
    }

    public static void writeLong(long j, byte[] bArr, int i) {
        bArr[i + 0] = (byte) j;
        bArr[i + 1] = (byte) (j >> 8);
        bArr[i + 2] = (byte) (j >> 16);
        bArr[i + 3] = (byte) (j >> 24);
        bArr[i + 4] = (byte) (j >> 32);
        bArr[i + 5] = (byte) (j >> 40);
        bArr[i + 6] = (byte) (j >> 48);
        bArr[i + 7] = (byte) (j >> 56);
    }

    public static void writeInt(int i, byte[] bArr, int i2) {
        bArr[i2 + 0] = (byte) i;
        bArr[i2 + 1] = (byte) (i >> 8);
        bArr[i2 + 2] = (byte) (i >> 16);
        bArr[i2 + 3] = (byte) (i >> 24);
    }

    public static long getLong(byte[] bArr, int i) {
        long j = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            j += (bArr[i2 + i] & 255) << (i2 * 8);
        }
        return j;
    }

    public static int getInt(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < 4; i3++) {
            i2 += (bArr[i3 + i] & 255) << (i3 * 8);
        }
        return i2;
    }
}
