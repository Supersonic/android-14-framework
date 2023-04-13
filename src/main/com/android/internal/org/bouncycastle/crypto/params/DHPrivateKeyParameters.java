package com.android.internal.org.bouncycastle.crypto.params;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DHPrivateKeyParameters extends DHKeyParameters {

    /* renamed from: x */
    private BigInteger f775x;

    public DHPrivateKeyParameters(BigInteger x, DHParameters params) {
        super(true, params);
        this.f775x = x;
    }

    public BigInteger getX() {
        return this.f775x;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.params.DHKeyParameters
    public int hashCode() {
        return this.f775x.hashCode() ^ super.hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.params.DHKeyParameters
    public boolean equals(Object obj) {
        if (obj instanceof DHPrivateKeyParameters) {
            DHPrivateKeyParameters other = (DHPrivateKeyParameters) obj;
            return other.getX().equals(this.f775x) && super.equals(obj);
        }
        return false;
    }
}
