package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.DerivationParameters;
/* loaded from: classes4.dex */
public class KDFParameters implements DerivationParameters {

    /* renamed from: iv */
    byte[] f789iv;
    byte[] shared;

    public KDFParameters(byte[] shared, byte[] iv) {
        this.shared = shared;
        this.f789iv = iv;
    }

    public byte[] getSharedSecret() {
        return this.shared;
    }

    public byte[] getIV() {
        return this.f789iv;
    }
}
