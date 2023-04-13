package com.android.internal.org.bouncycastle.crypto;
/* loaded from: classes4.dex */
public interface RawAgreement {
    void calculateAgreement(CipherParameters cipherParameters, byte[] bArr, int i);

    int getAgreementSize();

    void init(CipherParameters cipherParameters);
}
