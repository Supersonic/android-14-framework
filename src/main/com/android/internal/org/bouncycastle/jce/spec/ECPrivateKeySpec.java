package com.android.internal.org.bouncycastle.jce.spec;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class ECPrivateKeySpec extends ECKeySpec {

    /* renamed from: d */
    private BigInteger f830d;

    public ECPrivateKeySpec(BigInteger d, ECParameterSpec spec) {
        super(spec);
        this.f830d = d;
    }

    public BigInteger getD() {
        return this.f830d;
    }
}
