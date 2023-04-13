package com.android.internal.org.bouncycastle.asn1.p018x9;
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.X9ECParametersHolder */
/* loaded from: classes4.dex */
public abstract class X9ECParametersHolder {
    private X9ECParameters params;

    protected abstract X9ECParameters createParameters();

    public synchronized X9ECParameters getParameters() {
        if (this.params == null) {
            this.params = createParameters();
        }
        return this.params;
    }
}
