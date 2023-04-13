package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class DSAKeyGenerationParameters extends KeyGenerationParameters {
    private DSAParameters params;

    public DSAKeyGenerationParameters(SecureRandom random, DSAParameters params) {
        super(random, params.getP().bitLength() - 1);
        this.params = params;
    }

    public DSAParameters getParameters() {
        return this.params;
    }
}
