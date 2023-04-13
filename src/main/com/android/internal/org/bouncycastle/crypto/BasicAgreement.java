package com.android.internal.org.bouncycastle.crypto;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public interface BasicAgreement {
    BigInteger calculateAgreement(CipherParameters cipherParameters);

    int getFieldSize();

    void init(CipherParameters cipherParameters);
}
