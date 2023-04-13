package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.util.Properties;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DHParameters implements CipherParameters {
    private static final int DEFAULT_MINIMUM_LENGTH = 160;

    /* renamed from: g */
    private BigInteger f769g;

    /* renamed from: j */
    private BigInteger f770j;

    /* renamed from: l */
    private int f771l;

    /* renamed from: m */
    private int f772m;

    /* renamed from: p */
    private BigInteger f773p;

    /* renamed from: q */
    private BigInteger f774q;
    private DHValidationParameters validation;

    private static int getDefaultMParam(int lParam) {
        if (lParam != 0 && lParam < 160) {
            return lParam;
        }
        return 160;
    }

    public DHParameters(BigInteger p, BigInteger g) {
        this(p, g, null, 0);
    }

    public DHParameters(BigInteger p, BigInteger g, BigInteger q) {
        this(p, g, q, 0);
    }

    public DHParameters(BigInteger p, BigInteger g, BigInteger q, int l) {
        this(p, g, q, getDefaultMParam(l), l, null, null);
    }

    public DHParameters(BigInteger p, BigInteger g, BigInteger q, int m, int l) {
        this(p, g, q, m, l, null, null);
    }

    public DHParameters(BigInteger p, BigInteger g, BigInteger q, BigInteger j, DHValidationParameters validation) {
        this(p, g, q, 160, 0, j, validation);
    }

    public DHParameters(BigInteger p, BigInteger g, BigInteger q, int m, int l, BigInteger j, DHValidationParameters validation) {
        if (l != 0) {
            if (l > p.bitLength()) {
                throw new IllegalArgumentException("when l value specified, it must satisfy 2^(l-1) <= p");
            }
            if (l < m) {
                throw new IllegalArgumentException("when l value specified, it may not be less than m value");
            }
        }
        if (m > p.bitLength() && !Properties.isOverrideSet("com.android.internal.org.bouncycastle.dh.allow_unsafe_p_value")) {
            throw new IllegalArgumentException("unsafe p value so small specific l required");
        }
        this.f769g = g;
        this.f773p = p;
        this.f774q = q;
        this.f772m = m;
        this.f771l = l;
        this.f770j = j;
        this.validation = validation;
    }

    public BigInteger getP() {
        return this.f773p;
    }

    public BigInteger getG() {
        return this.f769g;
    }

    public BigInteger getQ() {
        return this.f774q;
    }

    public BigInteger getJ() {
        return this.f770j;
    }

    public int getM() {
        return this.f772m;
    }

    public int getL() {
        return this.f771l;
    }

    public DHValidationParameters getValidationParameters() {
        return this.validation;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DHParameters) {
            DHParameters pm = (DHParameters) obj;
            if (getQ() != null) {
                if (!getQ().equals(pm.getQ())) {
                    return false;
                }
            } else if (pm.getQ() != null) {
                return false;
            }
            return pm.getP().equals(this.f773p) && pm.getG().equals(this.f769g);
        }
        return false;
    }

    public int hashCode() {
        return (getP().hashCode() ^ getG().hashCode()) ^ (getQ() != null ? getQ().hashCode() : 0);
    }
}
