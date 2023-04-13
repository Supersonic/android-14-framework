package com.google.android.rappor;

import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes.dex */
public class HmacDrbg {
    private static final byte[] BYTE_ARRAY_0 = {0};
    private static final byte[] BYTE_ARRAY_1 = {1};
    private static final int DIGEST_NUM_BYTES = 32;
    public static final int ENTROPY_INPUT_SIZE_BYTES = 48;
    private static final int MAX_BYTES_PER_REQUEST = 937;
    public static final int MAX_BYTES_TOTAL = 10000;
    public static final int MAX_PERSONALIZATION_STRING_LENGTH_BYTES = 20;
    public static final int SECURITY_STRENGTH = 256;
    private int bytesGenerated;
    private Mac hmac;
    private byte[] value;

    public HmacDrbg(byte[] entropyInput, byte[] personalizationString) {
        byte[] seedMaterial = bytesConcat(entropyInput, emptyIfNull(personalizationString));
        setKey(new byte[32]);
        byte[] bArr = new byte[32];
        this.value = bArr;
        Arrays.fill(bArr, (byte) 1);
        hmacDrbgUpdate(seedMaterial);
        this.bytesGenerated = 0;
    }

    private static byte[] emptyIfNull(byte[] b) {
        return b == null ? new byte[0] : b;
    }

    private void setKey(byte[] key) {
        try {
            this.hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            this.hmac.init(secretKey);
        } catch (Exception e) {
        }
    }

    private byte[] hash(byte[] x) {
        try {
            return this.hmac.doFinal(x);
        } catch (Exception e) {
            return null;
        }
    }

    private void hmacDrbgUpdate(byte[] providedData) {
        setKey(hash(bytesConcat(this.value, BYTE_ARRAY_0, emptyIfNull(providedData))));
        byte[] hash = hash(this.value);
        this.value = hash;
        if (providedData == null) {
            return;
        }
        setKey(hash(bytesConcat(hash, BYTE_ARRAY_1, providedData)));
        this.value = hash(this.value);
    }

    private void hmacDrbgGenerate(byte[] out, int start, int count) {
        int bytesWritten = 0;
        while (bytesWritten < count) {
            this.value = hash(this.value);
            int bytesToWrite = Math.min(count - bytesWritten, 32);
            System.arraycopy(this.value, 0, out, start + bytesWritten, bytesToWrite);
            bytesWritten += bytesToWrite;
        }
        hmacDrbgUpdate(null);
    }

    public static byte[] generateEntropyInput() {
        byte[] result = new byte[48];
        new SecureRandom().nextBytes(result);
        return result;
    }

    public byte[] nextBytes(int length) {
        byte[] result = new byte[length];
        nextBytes(result);
        return result;
    }

    public void nextBytes(byte[] out) {
        nextBytes(out, 0, out.length);
    }

    public void nextBytes(byte[] out, int start, int count) {
        if (count == 0) {
            return;
        }
        if (this.bytesGenerated + count > 10000) {
            throw new IllegalStateException("Cannot generate more than a total of " + count + " bytes.");
        }
        int bytesWritten = 0;
        while (bytesWritten < count) {
            try {
                int bytesToWrite = Math.min(count - bytesWritten, (int) MAX_BYTES_PER_REQUEST);
                hmacDrbgGenerate(out, start + bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
            } finally {
                this.bytesGenerated += count;
            }
        }
    }

    private static byte[] bytesConcat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array2 : arrays) {
            System.arraycopy(array2, 0, result, pos, array2.length);
            pos += array2.length;
        }
        return result;
    }
}
