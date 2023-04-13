package com.android.internal.org.bouncycastle.crypto.params;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class ECPrivateKeyParameters extends ECKeyParameters {

    /* renamed from: d */
    private final BigInteger f787d;

    public ECPrivateKeyParameters(BigInteger d, ECDomainParameters parameters) {
        super(true, parameters);
        this.f787d = parameters.validatePrivateScalar(d);
    }

    public BigInteger getD() {
        return this.f787d;
    }
}
