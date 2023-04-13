package com.android.internal.org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;
/* loaded from: classes4.dex */
public class ECKeySpec implements KeySpec {
    private ECParameterSpec spec;

    /* JADX INFO: Access modifiers changed from: protected */
    public ECKeySpec(ECParameterSpec spec) {
        this.spec = spec;
    }

    public ECParameterSpec getParams() {
        return this.spec;
    }
}
