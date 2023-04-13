package com.android.server.locksettings;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes2.dex */
public class RebootEscrowKey {
    public final SecretKey mKey;

    public RebootEscrowKey(SecretKey secretKey) {
        this.mKey = secretKey;
    }

    public static RebootEscrowKey fromKeyBytes(byte[] bArr) {
        return new RebootEscrowKey(new SecretKeySpec(bArr, "AES"));
    }

    public static RebootEscrowKey generate() throws IOException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            return new RebootEscrowKey(keyGenerator.generateKey());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Could not generate new secret key", e);
        }
    }

    public SecretKey getKey() {
        return this.mKey;
    }

    public byte[] getKeyBytes() {
        return this.mKey.getEncoded();
    }
}
