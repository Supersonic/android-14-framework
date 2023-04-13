package android.util;

import android.app.ActivityManager;
import android.content.p001pm.Signature;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import libcore.util.HexEncoding;
/* loaded from: classes3.dex */
public final class PackageUtils {
    private static final int HIGH_RAM_BUFFER_SIZE_BYTES = 1000000;
    private static final int LOW_RAM_BUFFER_SIZE_BYTES = 1000;

    private PackageUtils() {
    }

    public static String[] computeSignaturesSha256Digests(Signature[] signatures) {
        return computeSignaturesSha256Digests(signatures, null);
    }

    public static String[] computeSignaturesSha256Digests(Signature[] signatures, String separator) {
        int signatureCount = signatures.length;
        String[] digests = new String[signatureCount];
        for (int i = 0; i < signatureCount; i++) {
            digests[i] = computeSha256Digest(signatures[i].toByteArray(), separator);
        }
        return digests;
    }

    public static String computeSignaturesSha256Digest(Signature[] signatures) {
        if (signatures.length == 1) {
            return computeSha256Digest(signatures[0].toByteArray(), null);
        }
        String[] sha256Digests = computeSignaturesSha256Digests(signatures, null);
        return computeSignaturesSha256Digest(sha256Digests);
    }

    public static String computeSignaturesSha256Digest(String[] sha256Digests) {
        if (sha256Digests.length == 1) {
            return sha256Digests[0];
        }
        Arrays.sort(sha256Digests);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (String sha256Digest : sha256Digests) {
            try {
                bytes.write(sha256Digest.getBytes());
            } catch (IOException e) {
            }
        }
        return computeSha256Digest(bytes.toByteArray(), null);
    }

    public static byte[] computeSha256DigestBytes(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String computeSha256Digest(byte[] data) {
        return computeSha256Digest(data, null);
    }

    public static String computeSha256Digest(byte[] data, String separator) {
        byte[] sha256DigestBytes = computeSha256DigestBytes(data);
        if (sha256DigestBytes == null) {
            return null;
        }
        if (separator == null) {
            return HexEncoding.encodeToString(sha256DigestBytes, true);
        }
        int length = sha256DigestBytes.length;
        String[] pieces = new String[length];
        for (int index = 0; index < length; index++) {
            pieces[index] = HexEncoding.encodeToString(sha256DigestBytes[index], true);
        }
        return TextUtils.join(separator, pieces);
    }

    public static byte[] createLargeFileBuffer() {
        int bufferSize = ActivityManager.isLowRamDeviceStatic() ? 1000 : 1000000;
        return new byte[bufferSize];
    }

    public static byte[] computeSha256DigestForLargeFileAsBytes(String filePath, byte[] fileBuffer) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.reset();
            File f = new File(filePath);
            try {
                DigestInputStream digestInputStream = new DigestInputStream(new FileInputStream(f), messageDigest);
                do {
                } while (digestInputStream.read(fileBuffer) != -1);
                return messageDigest.digest();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }

    public static String computeSha256DigestForLargeFile(String filePath, byte[] fileBuffer) {
        return computeSha256DigestForLargeFile(filePath, fileBuffer, null);
    }

    public static String computeSha256DigestForLargeFile(String filePath, byte[] fileBuffer, String separator) {
        byte[] resultBytes = computeSha256DigestForLargeFileAsBytes(filePath, fileBuffer);
        if (separator == null) {
            return HexEncoding.encodeToString(resultBytes, false);
        }
        int length = resultBytes.length;
        String[] pieces = new String[length];
        for (int index = 0; index < length; index++) {
            pieces[index] = HexEncoding.encodeToString(resultBytes[index], true);
        }
        return TextUtils.join(separator, pieces);
    }
}
