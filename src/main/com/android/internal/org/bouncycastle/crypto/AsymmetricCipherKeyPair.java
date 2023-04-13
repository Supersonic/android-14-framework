package com.android.internal.org.bouncycastle.crypto;

import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
/* loaded from: classes4.dex */
public class AsymmetricCipherKeyPair {
    private AsymmetricKeyParameter privateParam;
    private AsymmetricKeyParameter publicParam;

    public AsymmetricCipherKeyPair(AsymmetricKeyParameter publicParam, AsymmetricKeyParameter privateParam) {
        this.publicParam = publicParam;
        this.privateParam = privateParam;
    }

    public AsymmetricCipherKeyPair(CipherParameters publicParam, CipherParameters privateParam) {
        this.publicParam = (AsymmetricKeyParameter) publicParam;
        this.privateParam = (AsymmetricKeyParameter) privateParam;
    }

    public AsymmetricKeyParameter getPublic() {
        return this.publicParam;
    }

    public AsymmetricKeyParameter getPrivate() {
        return this.privateParam;
    }
}
