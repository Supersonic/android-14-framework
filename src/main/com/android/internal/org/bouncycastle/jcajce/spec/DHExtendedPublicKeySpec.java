package com.android.internal.org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
/* loaded from: classes4.dex */
public class DHExtendedPublicKeySpec extends DHPublicKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPublicKeySpec(BigInteger y, DHParameterSpec params) {
        super(y, params.getP(), params.getG());
        this.params = params;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}
