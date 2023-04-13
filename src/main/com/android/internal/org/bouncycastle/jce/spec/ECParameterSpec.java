package com.android.internal.org.bouncycastle.jce.spec;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
/* loaded from: classes4.dex */
public class ECParameterSpec implements AlgorithmParameterSpec {

    /* renamed from: G */
    private ECPoint f827G;
    private ECCurve curve;

    /* renamed from: h */
    private BigInteger f828h;

    /* renamed from: n */
    private BigInteger f829n;
    private byte[] seed;

    public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n) {
        this.curve = curve;
        this.f827G = G.normalize();
        this.f829n = n;
        this.f828h = BigInteger.valueOf(1L);
        this.seed = null;
    }

    public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        this.curve = curve;
        this.f827G = G.normalize();
        this.f829n = n;
        this.f828h = h;
        this.seed = null;
    }

    public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        this.curve = curve;
        this.f827G = G.normalize();
        this.f829n = n;
        this.f828h = h;
        this.seed = seed;
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public ECPoint getG() {
        return this.f827G;
    }

    public BigInteger getN() {
        return this.f829n;
    }

    public BigInteger getH() {
        return this.f828h;
    }

    public byte[] getSeed() {
        return this.seed;
    }

    public boolean equals(Object o) {
        if (o instanceof ECParameterSpec) {
            ECParameterSpec other = (ECParameterSpec) o;
            return getCurve().equals(other.getCurve()) && getG().equals(other.getG());
        }
        return false;
    }

    public int hashCode() {
        return getCurve().hashCode() ^ getG().hashCode();
    }
}
