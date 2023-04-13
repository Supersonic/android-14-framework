package com.android.internal.org.bouncycastle.math.p025ec;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.WNafPreCompInfo */
/* loaded from: classes4.dex */
public class WNafPreCompInfo implements PreCompInfo {
    volatile int promotionCountdown = 4;
    protected int confWidth = -1;
    protected ECPoint[] preComp = null;
    protected ECPoint[] preCompNeg = null;
    protected ECPoint twice = null;
    protected int width = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int decrementPromotionCountdown() {
        int t = this.promotionCountdown;
        if (t > 0) {
            int t2 = t - 1;
            this.promotionCountdown = t2;
            return t2;
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPromotionCountdown() {
        return this.promotionCountdown;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPromotionCountdown(int promotionCountdown) {
        this.promotionCountdown = promotionCountdown;
    }

    public boolean isPromoted() {
        return this.promotionCountdown <= 0;
    }

    public int getConfWidth() {
        return this.confWidth;
    }

    public void setConfWidth(int confWidth) {
        this.confWidth = confWidth;
    }

    public ECPoint[] getPreComp() {
        return this.preComp;
    }

    public void setPreComp(ECPoint[] preComp) {
        this.preComp = preComp;
    }

    public ECPoint[] getPreCompNeg() {
        return this.preCompNeg;
    }

    public void setPreCompNeg(ECPoint[] preCompNeg) {
        this.preCompNeg = preCompNeg;
    }

    public ECPoint getTwice() {
        return this.twice;
    }

    public void setTwice(ECPoint twice) {
        this.twice = twice;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
