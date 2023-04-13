package com.android.internal.org.bouncycastle.math.p025ec;

import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.AbstractECMultiplier */
/* loaded from: classes4.dex */
public abstract class AbstractECMultiplier implements ECMultiplier {
    protected abstract ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger);

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECMultiplier
    public ECPoint multiply(ECPoint p, BigInteger k) {
        int sign = k.signum();
        if (sign == 0 || p.isInfinity()) {
            return p.getCurve().getInfinity();
        }
        ECPoint positive = multiplyPositive(p, k.abs());
        ECPoint result = sign > 0 ? positive : positive.negate();
        return checkResult(result);
    }

    protected ECPoint checkResult(ECPoint p) {
        return ECAlgorithms.implCheckResult(p);
    }
}
