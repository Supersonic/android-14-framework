package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class ParametersWithRandom implements CipherParameters {
    private CipherParameters parameters;
    private SecureRandom random;

    public ParametersWithRandom(CipherParameters parameters, SecureRandom random) {
        this.random = CryptoServicesRegistrar.getSecureRandom(random);
        this.parameters = parameters;
    }

    public ParametersWithRandom(CipherParameters parameters) {
        this(parameters, null);
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
