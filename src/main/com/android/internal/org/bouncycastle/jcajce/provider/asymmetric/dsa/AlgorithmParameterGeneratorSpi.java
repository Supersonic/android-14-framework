package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dsa;

import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.digests.SHA256Digest;
import com.android.internal.org.bouncycastle.crypto.generators.DSAParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
/* loaded from: classes4.dex */
public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
    protected DSAParameterGenerationParameters params;
    protected SecureRandom random;
    protected int strength = 1024;

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(int strength, SecureRandom random) {
        if (strength < 512 || strength > 3072) {
            throw new InvalidParameterException("strength must be from 512 - 3072");
        }
        if (strength <= 1024 && strength % 64 != 0) {
            throw new InvalidParameterException("strength must be a multiple of 64 below 1024 bits.");
        }
        if (strength > 1024 && strength % 1024 != 0) {
            throw new InvalidParameterException("strength must be a multiple of 1024 above 1024 bits.");
        }
        this.strength = strength;
        this.random = random;
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSA parameter generation.");
    }

    @Override // java.security.AlgorithmParameterGeneratorSpi
    protected AlgorithmParameters engineGenerateParameters() {
        DSAParametersGenerator pGen;
        if (this.strength <= 1024) {
            pGen = new DSAParametersGenerator();
        } else {
            pGen = new DSAParametersGenerator(new SHA256Digest());
        }
        if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        int certainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
        int i = this.strength;
        if (i == 1024) {
            DSAParameterGenerationParameters dSAParameterGenerationParameters = new DSAParameterGenerationParameters(1024, 160, certainty, this.random);
            this.params = dSAParameterGenerationParameters;
            pGen.init(dSAParameterGenerationParameters);
        } else if (i > 1024) {
            DSAParameterGenerationParameters dSAParameterGenerationParameters2 = new DSAParameterGenerationParameters(i, 256, certainty, this.random);
            this.params = dSAParameterGenerationParameters2;
            pGen.init(dSAParameterGenerationParameters2);
        } else {
            pGen.init(i, certainty, this.random);
        }
        DSAParameters p = pGen.generateParameters();
        try {
            AlgorithmParameters params = createParametersInstance("DSA");
            params.init(new DSAParameterSpec(p.getP(), p.getQ(), p.getG()));
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
