package com.android.internal.org.bouncycastle.crypto.params;
/* loaded from: classes4.dex */
public class DSAKeyParameters extends AsymmetricKeyParameter {
    private DSAParameters params;

    public DSAKeyParameters(boolean isPrivate, DSAParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DSAParameters getParameters() {
        return this.params;
    }
}
