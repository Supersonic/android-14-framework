package com.android.server.locksettings;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes2.dex */
public class SP800Derive {
    public final byte[] mKeyBytes;

    public SP800Derive(byte[] bArr) {
        this.mKeyBytes = bArr;
    }

    public final Mac getMac() {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(this.mKeyBytes, mac.getAlgorithm()));
            return mac;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update32(Mac mac, int i) {
        mac.update(ByteBuffer.allocate(4).putInt(i).array());
    }

    public byte[] withContext(byte[] bArr, byte[] bArr2) {
        Mac mac = getMac();
        update32(mac, 1);
        mac.update(bArr);
        mac.update((byte) 0);
        mac.update(bArr2);
        update32(mac, bArr2.length * 8);
        update32(mac, 256);
        return mac.doFinal();
    }
}
