package com.android.internal.org.bouncycastle.math.p025ec.endo;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPointMap;
import com.android.internal.org.bouncycastle.math.p025ec.ScaleXPointMap;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.endo.GLVTypeBEndomorphism */
/* loaded from: classes4.dex */
public class GLVTypeBEndomorphism implements GLVEndomorphism {
    protected final GLVTypeBParameters parameters;
    protected final ECPointMap pointMap;

    public GLVTypeBEndomorphism(ECCurve curve, GLVTypeBParameters parameters) {
        this.parameters = parameters;
        this.pointMap = new ScaleXPointMap(curve.fromBigInteger(parameters.getBeta()));
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
