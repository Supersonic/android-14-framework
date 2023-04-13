package com.android.internal.org.bouncycastle.crypto.params;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DSAPrivateKeyParameters extends DSAKeyParameters {

    /* renamed from: x */
    private BigInteger f782x;

    public DSAPrivateKeyParameters(BigInteger x, DSAParameters params) {
        super(true, params);
        this.f782x = x;
    }

    public BigInteger getX() {
        return this.f782x;
    }
}
