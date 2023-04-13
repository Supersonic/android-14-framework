package com.android.server.net.watchlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes2.dex */
public class DigestUtils {
    public static byte[] getSha256Hash(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            byte[] sha256Hash = getSha256Hash(fileInputStream);
            fileInputStream.close();
            return sha256Hash;
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static byte[] getSha256Hash(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
        byte[] bArr = new byte[16384];
        while (true) {
            int read = inputStream.read(bArr);
            if (read >= 0) {
                messageDigest.update(bArr, 0, read);
            } else {
                return messageDigest.digest();
            }
        }
    }
}
