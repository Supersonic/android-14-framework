package com.android.internal.org.bouncycastle.jce.spec;

import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
/* loaded from: classes4.dex */
public class ECPublicKeySpec extends ECKeySpec {

    /* renamed from: q */
    private ECPoint f831q;

    public ECPublicKeySpec(ECPoint q, ECParameterSpec spec) {
        super(spec);
        if (q.getCurve() != null) {
            this.f831q = q.normalize();
        } else {
            this.f831q = q;
        }
    }

    public ECPoint getQ() {
        return this.f831q;
    }
}
