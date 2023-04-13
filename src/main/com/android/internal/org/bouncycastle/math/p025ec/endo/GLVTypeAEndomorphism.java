package com.android.internal.org.bouncycastle.math.p025ec.endo;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPointMap;
import com.android.internal.org.bouncycastle.math.p025ec.ScaleYNegateXPointMap;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.endo.GLVTypeAEndomorphism */
/* loaded from: classes4.dex */
public class GLVTypeAEndomorphism implements GLVEndomorphism {
    protected final GLVTypeAParameters parameters;
    protected final ECPointMap pointMap;

    public GLVTypeAEndomorphism(ECCurve curve, GLVTypeAParameters parameters) {
        this.parameters = parameters;
        this.pointMap = new ScaleYNegateXPointMap(curve.fromBigInteger(parameters.getI()));
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.endo.GLVEndomorphism
    public BigInteger[] decomposeScalar(BigInteger k) {
        return EndoUtil.decomposeScalar(this.parameters.getSplitParams(), k);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.endo.ECEndomorphism
    public ECPointMap getPointMap() {
        return this.pointMap;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.endo.ECEndomorphism
    public boolean hasEfficientPointMap() {
        return true;
    }
}
