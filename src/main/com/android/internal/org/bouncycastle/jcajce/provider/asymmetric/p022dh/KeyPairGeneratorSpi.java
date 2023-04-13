package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p022dh;

import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.generators.DHBasicKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.generators.DHParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.Integers;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import javax.crypto.spec.DHParameterSpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyPairGeneratorSpi */
/* loaded from: classes4.dex */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    DHBasicKeyPairGenerator engine;
    boolean initialised;
    DHKeyGenerationParameters param;
    SecureRandom random;
    int strength;
    private static Hashtable params = new Hashtable();
    private static Object lock = new Object();

    public KeyPairGeneratorSpi() {
        super("DH");
        this.engine = new DHBasicKeyPairGenerator();
        this.strength = 2048;
        this.random = CryptoServicesRegistrar.getSecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params2, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params2 instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DHParameterSpec");
        }
        DHParameterSpec dhParams = (DHParameterSpec) params2;
        try {
            DHKeyGenerationParameters convertParams = convertParams(random, dhParams);
            this.param = convertParams;
            this.engine.init(convertParams);
            this.initialised = true;
        } catch (IllegalArgumentException e) {
            throw new InvalidAlgorithmParameterException(e.getMessage(), e);
        }
    }

    private DHKeyGenerationParameters convertParams(SecureRandom random, DHParameterSpec dhParams) {
        return new DHKeyGenerationParameters(random, new DHParameters(dhParams.getP(), dhParams.getG(), null, dhParams.getL()));
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            Integer paramStrength = Integers.valueOf(this.strength);
            if (params.containsKey(paramStrength)) {
                this.param = (DHKeyGenerationParameters) params.get(paramStrength);
            } else {
                DHParameterSpec dhParams = BouncyCastleProvider.CONFIGURATION.getDHDefaultParameters(this.strength);
                if (dhParams != null) {
                    this.param = convertParams(this.random, dhParams);
                } else {
                    synchronized (lock) {
                        if (params.containsKey(paramStrength)) {
                            this.param = (DHKeyGenerationParameters) params.get(paramStrength);
                        } else {
                            DHParametersGenerator pGen = new DHParametersGenerator();
                            int i = this.strength;
                            pGen.init(i, PrimeCertaintyCalculator.getDefaultCertainty(i), this.random);
                            DHKeyGenerationParameters dHKeyGenerationParameters = new DHKeyGenerationParameters(this.random, pGen.generateParameters());
                            this.param = dHKeyGenerationParameters;
                            params.put(paramStrength, dHKeyGenerationParameters);
                        }
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        DHPublicKeyParameters pub = (DHPublicKeyParameters) pair.getPublic();
        DHPrivateKeyParameters priv = (DHPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCDHPublicKey(pub), new BCDHPrivateKey(priv));
    }
}
