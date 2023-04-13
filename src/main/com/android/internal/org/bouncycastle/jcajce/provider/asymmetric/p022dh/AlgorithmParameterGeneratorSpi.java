package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p022dh;

import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.generators.DHParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.DHParameterSpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.AlgorithmParameterGeneratorSpi */
/* loaded from: classes4.dex */
public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
    protected SecureRandom random;
    protected int strength = 2048;

    /* renamed from: l */
    private int f798l = 0;

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(genParamSpec instanceof DHGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation");
        }
        DHGenParameterSpec spec = (DHGenParameterSpec) genParamSpec;
        this.strength = spec.getPrimeSize();
        this.f798l = spec.getExponentSize();
        this.random = random;
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected AlgorithmParameters engineGenerateParameters() {
        DHParametersGenerator pGen = new DHParametersGenerator();
        int certainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
        pGen.init(this.strength, certainty, CryptoServicesRegistrar.getSecureRandom(this.random));
        DHParameters p = pGen.generateParameters();
        try {
            AlgorithmParameters params = createParametersInstance("DH");
            params.init(new DHParameterSpec(p.getP(), p.getG(), this.f798l));
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
