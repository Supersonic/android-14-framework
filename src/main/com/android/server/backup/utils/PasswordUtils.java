package com.android.server.backup.utils;

import android.util.Slog;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import libcore.util.HexEncoding;
/* loaded from: classes.dex */
public class PasswordUtils {
    public static SecretKey buildPasswordKey(String str, String str2, byte[] bArr, int i) {
        return buildCharArrayKey(str, str2.toCharArray(), bArr, i);
    }

    public static String buildPasswordHash(String str, String str2, byte[] bArr, int i) {
        SecretKey buildPasswordKey = buildPasswordKey(str, str2, bArr, i);
        if (buildPasswordKey != null) {
            return byteArrayToHex(buildPasswordKey.getEncoded());
        }
        return null;
    }

    public static String byteArrayToHex(byte[] bArr) {
        return HexEncoding.encodeToString(bArr, true);
    }

    public static byte[] hexToByteArray(String str) {
        int length = str.length() / 2;
        if (length * 2 != str.length()) {
            throw new IllegalArgumentException("Hex string must have an even number of digits");
        }
        byte[] bArr = new byte[length];
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 2;
            bArr[i / 2] = (byte) Integer.parseInt(str.substring(i, i2), 16);
            i = i2;
        }
        return bArr;
    }

    public static byte[] makeKeyChecksum(String str, byte[] bArr, byte[] bArr2, int i) {
        char[] cArr = new char[bArr.length];
        for (int i2 = 0; i2 < bArr.length; i2++) {
            cArr[i2] = (char) bArr[i2];
        }
        return buildCharArrayKey(str, cArr, bArr2, i).getEncoded();
    }

    public static SecretKey buildCharArrayKey(String str, char[] cArr, byte[] bArr, int i) {
        try {
            return SecretKeyFactory.getInstance(str).generateSecret(new PBEKeySpec(cArr, bArr, i, 256));
        } catch (NoSuchAlgorithmException unused) {
            Slog.e("BackupManagerService", "PBKDF2 unavailable!");
            return null;
        } catch (InvalidKeySpecException unused2) {
            Slog.e("BackupManagerService", "Invalid key spec for PBKDF2!");
            return null;
        }
    }
}
