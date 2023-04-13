package com.android.internal.org.bouncycastle.math.p025ec;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.ScaleYNegateXPointMap */
/* loaded from: classes4.dex */
public class ScaleYNegateXPointMap implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleYNegateXPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPointMap
    public ECPoint map(ECPoint p) {
        return p.scaleYNegateX(this.scale);
    }
}
