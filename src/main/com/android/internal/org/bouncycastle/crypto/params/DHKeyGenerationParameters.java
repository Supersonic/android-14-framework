package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class DHKeyGenerationParameters extends KeyGenerationParameters {
    private DHParameters params;

    public DHKeyGenerationParameters(SecureRandom random, DHParameters params) {
        super(random, getStrength(params));
        this.params = params;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    static int getStrength(DHParameters params) {
        return params.getL() != 0 ? params.getL() : params.getP().bitLength();
    }
}
