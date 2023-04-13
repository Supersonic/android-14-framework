package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
/* loaded from: classes4.dex */
public class KeyParameter implements CipherParameters {
    private byte[] key;

    public KeyParameter(byte[] key) {
        this(key, 0, key.length);
    }

    public KeyParameter(byte[] key, int keyOff, int keyLen) {
        byte[] bArr = new byte[keyLen];
        this.key = bArr;
        System.arraycopy(key, keyOff, bArr, 0, keyLen);
    }

    public byte[] getKey() {
        return this.key;
    }
}
