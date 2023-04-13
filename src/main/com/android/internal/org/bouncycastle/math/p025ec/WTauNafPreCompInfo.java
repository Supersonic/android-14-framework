package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.WTauNafPreCompInfo */
/* loaded from: classes4.dex */
public class WTauNafPreCompInfo implements PreCompInfo {
    protected ECPoint.AbstractF2m[] preComp = null;

    public ECPoint.AbstractF2m[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint.AbstractF2m[] preComp) {
        this.preComp = preComp;
    }
}
