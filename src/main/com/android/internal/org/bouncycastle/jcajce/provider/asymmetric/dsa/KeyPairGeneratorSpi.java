package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dsa;

import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.digests.SHA256Digest;
import com.android.internal.org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.generators.DSAParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Properties;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    DSAKeyPairGenerator engine;
    boolean initialised;
    DSAKeyGenerationParameters param;
    SecureRandom random;
    int strength;
    private static Hashtable params = new Hashtable();
    private static Object lock = new Object();

    public KeyPairGeneratorSpi() {
        super("DSA");
        this.engine = new DSAKeyPairGenerator();
        this.strength = 1024;
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        if (strength < 512 || strength > 4096 || ((strength < 1024 && strength % 64 != 0) || (strength >= 1024 && strength % 1024 != 0))) {
            throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
        }
        if (random == null) {
            random = new SecureRandom();
        }
        DSAParameterSpec spec = BouncyCastleProvider.CONFIGURATION.getDSADefaultParameters(strength);
        if (spec != null) {
            DSAKeyGenerationParameters dSAKeyGenerationParameters = new DSAKeyGenerationParameters(random, new DSAParameters(spec.getP(), spec.getQ(), spec.getG()));
            this.param = dSAKeyGenerationParameters;
            this.engine.init(dSAKeyGenerationParameters);
            this.initialised = true;
            return;
        }
        this.strength = strength;
        this.random = random;
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params2, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params2 instanceof DSAParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec");
        }
        DSAParameterSpec dsaParams = (DSAParameterSpec) params2;
        if (random == null) {
            random = new SecureRandom();
        }
        DSAKeyGenerationParameters dSAKeyGenerationParameters = new DSAKeyGenerationParameters(random, new DSAParameters(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG()));
        this.param = dSAKeyGenerationParameters;
        this.engine.init(dSAKeyGenerationParameters);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        DSAParametersGenerator pGen;
        if (!this.initialised) {
            Integer paramStrength = Integers.valueOf(this.strength);
            if (params.containsKey(paramStrength)) {
                this.param = (DSAKeyGenerationParameters) params.get(paramStrength);
            } else {
                synchronized (lock) {
                    if (params.containsKey(paramStrength)) {
                        this.param = (DSAKeyGenerationParameters) params.get(paramStrength);
                    } else {
                        int certainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
                        int i = this.strength;
                        if (i == 1024) {
                            pGen = new DSAParametersGenerator();
                            if (Properties.isOverrideSet("com.android.internal.org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                                pGen.init(this.strength, certainty, this.random);
                            } else {
                                DSAParameterGenerationParameters dsaParams = new DSAParameterGenerationParameters(1024, 160, certainty, this.random);
                                pGen.init(dsaParams);
                            }
                        } else if (i > 1024) {
                            DSAParameterGenerationParameters dsaParams2 = new DSAParameterGenerationParameters(i, 256, certainty, this.random);
                            DSAParametersGenerator pGen2 = new DSAParametersGenerator(new SHA256Digest());
                            pGen2.init(dsaParams2);
                            pGen = pGen2;
                        } else {
                            pGen = new DSAParametersGenerator();
                            pGen.init(this.strength, certainty, this.random);
                        }
                        DSAKeyGenerationParameters dSAKeyGenerationParameters = new DSAKeyGenerationParameters(this.random, pGen.generateParameters());
                        this.param = dSAKeyGenerationParameters;
                        params.put(paramStrength, dSAKeyGenerationParameters);
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        DSAPublicKeyParameters pub = (DSAPublicKeyParameters) pair.getPublic();
        DSAPrivateKeyParameters priv = (DSAPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCDSAPublicKey(pub), new BCDSAPrivateKey(priv));
    }
}
