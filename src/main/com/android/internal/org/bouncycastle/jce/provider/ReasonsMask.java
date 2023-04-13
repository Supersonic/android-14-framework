package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.x509.ReasonFlags;
/* loaded from: classes4.dex */
class ReasonsMask {
    static final ReasonsMask allReasons = new ReasonsMask(33023);
    private int _reasons;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReasonsMask(ReasonFlags reasons) {
        this._reasons = reasons.intValue();
    }

    private ReasonsMask(int reasons) {
        this._reasons = reasons;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReasonsMask() {
        this(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addReasons(ReasonsMask mask) {
        this._reasons |= mask.getReasons();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAllReasons() {
        return this._reasons == allReasons._reasons;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReasonsMask intersect(ReasonsMask mask) {
        ReasonsMask _mask = new ReasonsMask();
        _mask.addReasons(new ReasonsMask(this._reasons & mask.getReasons()));
        return _mask;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasNewReasons(ReasonsMask mask) {
        return (this._reasons | (mask.getReasons() ^ this._reasons)) != 0;
    }

    int getReasons() {
        return this._reasons;
    }
}
