package com.android.internal.org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
/* loaded from: classes4.dex */
public class DHExtendedPrivateKeySpec extends DHPrivateKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPrivateKeySpec(BigInteger x, DHParameterSpec params) {
        super(x, params.getP(), params.getG());
        this.params = params;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}
