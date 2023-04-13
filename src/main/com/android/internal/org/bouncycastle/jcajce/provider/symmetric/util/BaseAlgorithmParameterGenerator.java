package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public abstract class BaseAlgorithmParameterGenerator extends AlgorithmParameterGeneratorSpi {
    protected SecureRandom random;
    private final JcaJceHelper helper = new DefaultJcaJceHelper();
    protected int strength = 1024;

    protected final AlgorithmParameters createParametersInstance(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(algorithm);
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
}
