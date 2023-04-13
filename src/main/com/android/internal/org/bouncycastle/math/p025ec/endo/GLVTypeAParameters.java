package com.android.internal.org.bouncycastle.math.p025ec.endo;

import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.endo.GLVTypeAParameters */
/* loaded from: classes4.dex */
public class GLVTypeAParameters {

    /* renamed from: i */
    protected final BigInteger f894i;
    protected final BigInteger lambda;
    protected final ScalarSplitParameters splitParams;

    public GLVTypeAParameters(BigInteger i, BigInteger lambda, ScalarSplitParameters splitParams) {
        this.f894i = i;
        this.lambda = lambda;
        this.splitParams = splitParams;
    }

    public BigInteger getI() {
        return this.f894i;
    }

    public BigInteger getLambda() {
        return this.lambda;
    }

    public ScalarSplitParameters getSplitParams() {
        return this.splitParams;
    }
}
