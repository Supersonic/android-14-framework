package com.android.internal.org.bouncycastle.crypto.params;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DSAPublicKeyParameters extends DSAKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /* renamed from: y */
    private BigInteger f783y;

    public DSAPublicKeyParameters(BigInteger y, DSAParameters params) {
        super(false, params);
        this.f783y = validate(y, params);
    }

    private BigInteger validate(BigInteger y, DSAParameters params) {
        if (params != null) {
            BigInteger bigInteger = TWO;
            if (bigInteger.compareTo(y) <= 0 && params.getP().subtract(bigInteger).compareTo(y) >= 0 && ONE.equals(y.modPow(params.getQ(), params.getP()))) {
                return y;
            }
            throw new IllegalArgumentException("y value does not appear to be in correct group");
        }
        return y;
    }

    public BigInteger getY() {
        return this.f783y;
    }
}
