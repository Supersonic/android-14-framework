package com.android.internal.org.bouncycastle.crypto.params;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class RSAPrivateCrtKeyParameters extends RSAKeyParameters {

    /* renamed from: dP */
    private BigInteger f792dP;

    /* renamed from: dQ */
    private BigInteger f793dQ;

    /* renamed from: e */
    private BigInteger f794e;

    /* renamed from: p */
    private BigInteger f795p;

    /* renamed from: q */
    private BigInteger f796q;
    private BigInteger qInv;

    public RSAPrivateCrtKeyParameters(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger p, BigInteger q, BigInteger dP, BigInteger dQ, BigInteger qInv) {
        super(true, modulus, privateExponent);
        this.f794e = publicExponent;
        this.f795p = p;
        this.f796q = q;
        this.f792dP = dP;
        this.f793dQ = dQ;
        this.qInv = qInv;
    }

    public BigInteger getPublicExponent() {
        return this.f794e;
    }

    public BigInteger getP() {
        return this.f795p;
    }

    public BigInteger getQ() {
        return this.f796q;
    }

    public BigInteger getDP() {
        return this.f792dP;
    }

    public BigInteger getDQ() {
        return this.f793dQ;
    }

    public BigInteger getQInv() {
        return this.qInv;
    }
}
