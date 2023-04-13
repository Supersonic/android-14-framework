package com.android.internal.org.bouncycastle.math.p025ec;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.ScaleXNegateYPointMap */
/* loaded from: classes4.dex */
public class ScaleXNegateYPointMap implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleXNegateYPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPointMap
    public ECPoint map(ECPoint p) {
        return p.scaleXNegateY(this.scale);
    }
}
