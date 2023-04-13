package com.android.server.locksettings.recoverablekeystore;

import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class PlatformDecryptionKey {
    public final int mGenerationId;
    public final SecretKey mKey;

    public PlatformDecryptionKey(int i, SecretKey secretKey) {
        this.mGenerationId = i;
        this.mKey = secretKey;
    }

    public int getGenerationId() {
        return this.mGenerationId;
    }

    public SecretKey getKey() {
        return this.mKey;
    }
}
