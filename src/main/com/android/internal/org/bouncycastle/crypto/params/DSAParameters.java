package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DSAParameters implements CipherParameters {

    /* renamed from: g */
    private BigInteger f779g;

    /* renamed from: p */
    private BigInteger f780p;

    /* renamed from: q */
    private BigInteger f781q;
    private DSAValidationParameters validation;

    public DSAParameters(BigInteger p, BigInteger q, BigInteger g) {
        this.f779g = g;
        this.f780p = p;
        this.f781q = q;
    }

    public DSAParameters(BigInteger p, BigInteger q, BigInteger g, DSAValidationParameters params) {
        this.f779g = g;
        this.f780p = p;
        this.f781q = q;
        this.validation = params;
    }

    public BigInteger getP() {
        return this.f780p;
    }

    public BigInteger getQ() {
        return this.f781q;
    }

    public BigInteger getG() {
        return this.f779g;
    }

    public DSAValidationParameters getValidationParameters() {
        return this.validation;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DSAParameters) {
            DSAParameters pm = (DSAParameters) obj;
            return pm.getP().equals(this.f780p) && pm.getQ().equals(this.f781q) && pm.getG().equals(this.f779g);
        }
        return false;
    }

    public int hashCode() {
        return (getP().hashCode() ^ getQ().hashCode()) ^ getG().hashCode();
    }
}
