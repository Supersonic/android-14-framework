package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
/* loaded from: classes4.dex */
public class AsymmetricKeyParameter implements CipherParameters {
    boolean privateKey;

    public AsymmetricKeyParameter(boolean privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isPrivate() {
        return this.privateKey;
    }
}
