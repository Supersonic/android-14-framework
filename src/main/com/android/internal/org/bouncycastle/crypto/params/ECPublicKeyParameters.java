package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
/* loaded from: classes4.dex */
public class ECPublicKeyParameters extends ECKeyParameters {

    /* renamed from: q */
    private final ECPoint f788q;

    public ECPublicKeyParameters(ECPoint q, ECDomainParameters parameters) {
        super(false, parameters);
        this.f788q = parameters.validatePublicPoint(q);
    }

    public ECPoint getQ() {
        return this.f788q;
    }
}
