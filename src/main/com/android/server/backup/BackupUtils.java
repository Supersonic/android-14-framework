package com.android.server.backup;

import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class BackupUtils {
    public static boolean signaturesMatch(ArrayList<byte[]> arrayList, PackageInfo packageInfo, PackageManagerInternal packageManagerInternal) {
        boolean z;
        if (packageInfo == null || packageInfo.packageName == null) {
            return false;
        }
        if ((packageInfo.applicationInfo.flags & 1) != 0) {
            return true;
        }
        if (ArrayUtils.isEmpty(arrayList)) {
            return false;
        }
        SigningInfo signingInfo = packageInfo.signingInfo;
        if (signingInfo == null) {
            Slog.w("BackupUtils", "signingInfo is empty, app was either unsigned or the flag PackageManager#GET_SIGNING_CERTIFICATES was not specified");
            return false;
        }
        int size = arrayList.size();
        if (size == 1) {
            return packageManagerInternal.isDataRestoreSafe(arrayList.get(0), packageInfo.packageName);
        }
        ArrayList<byte[]> hashSignatureArray = hashSignatureArray(signingInfo.getApkContentsSigners());
        int size2 = hashSignatureArray.size();
        for (int i = 0; i < size; i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= size2) {
                    z = false;
                    break;
                } else if (Arrays.equals(arrayList.get(i), hashSignatureArray.get(i2))) {
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }

    public static byte[] hashSignature(byte[] bArr) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bArr);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException unused) {
            Slog.w("BackupUtils", "No SHA-256 algorithm found!");
            return null;
        }
    }

    public static byte[] hashSignature(Signature signature) {
        return hashSignature(signature.toByteArray());
    }

    public static ArrayList<byte[]> hashSignatureArray(Signature[] signatureArr) {
        if (signatureArr == null) {
            return null;
        }
        ArrayList<byte[]> arrayList = new ArrayList<>(signatureArr.length);
        for (Signature signature : signatureArr) {
            arrayList.add(hashSignature(signature));
        }
        return arrayList;
    }

    public static ArrayList<byte[]> hashSignatureArray(List<byte[]> list) {
        if (list == null) {
            return null;
        }
        ArrayList<byte[]> arrayList = new ArrayList<>(list.size());
        for (byte[] bArr : list) {
            arrayList.add(hashSignature(bArr));
        }
        return arrayList;
    }
}
