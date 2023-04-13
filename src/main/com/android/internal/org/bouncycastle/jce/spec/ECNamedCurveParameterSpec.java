package com.android.internal.org.bouncycastle.jce.spec;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class ECNamedCurveParameterSpec extends ECParameterSpec {
    private String name;

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n) {
        super(curve, G, n);
        this.name = name;
    }

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        super(curve, G, n, h);
        this.name = name;
    }

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        super(curve, G, n, h, seed);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
