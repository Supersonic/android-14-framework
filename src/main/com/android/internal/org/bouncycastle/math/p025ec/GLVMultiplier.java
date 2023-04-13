package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.p025ec.endo.EndoUtil;
import com.android.internal.org.bouncycastle.math.p025ec.endo.GLVEndomorphism;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.GLVMultiplier */
/* loaded from: classes4.dex */
public class GLVMultiplier extends AbstractECMultiplier {
    protected final ECCurve curve;
    protected final GLVEndomorphism glvEndomorphism;

    public GLVMultiplier(ECCurve curve, GLVEndomorphism glvEndomorphism) {
        if (curve == null || curve.getOrder() == null) {
            throw new IllegalArgumentException("Need curve with known group order");
        }
        this.curve = curve;
        this.glvEndomorphism = glvEndomorphism;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        if (!this.curve.equals(p.getCurve())) {
            throw new IllegalStateException();
        }
        BigInteger n = p.getCurve().getOrder();
        BigInteger[] ab = this.glvEndomorphism.decomposeScalar(k.mod(n));
        BigInteger a = ab[0];
        BigInteger b = ab[1];
        if (this.glvEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implShamirsTrickWNaf(this.glvEndomorphism, p, a, b);
        }
        ECPoint q = EndoUtil.mapPoint(this.glvEndomorphism, p);
        return ECAlgorithms.implShamirsTrickWNaf(p, a, q, b);
    }
}
