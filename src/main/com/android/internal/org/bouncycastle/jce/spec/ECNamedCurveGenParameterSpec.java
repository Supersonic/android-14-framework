package com.android.internal.org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
/* loaded from: classes4.dex */
public class ECNamedCurveGenParameterSpec implements AlgorithmParameterSpec {
    private String name;

    public ECNamedCurveGenParameterSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
