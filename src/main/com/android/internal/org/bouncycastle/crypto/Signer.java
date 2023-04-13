package com.android.internal.org.bouncycastle.crypto;
/* loaded from: classes4.dex */
public interface Signer {
    byte[] generateSignature() throws CryptoException, DataLengthException;

    void init(boolean z, CipherParameters cipherParameters);

    void reset();

    void update(byte b);

    void update(byte[] bArr, int i, int i2);

    boolean verifySignature(byte[] bArr);
}
