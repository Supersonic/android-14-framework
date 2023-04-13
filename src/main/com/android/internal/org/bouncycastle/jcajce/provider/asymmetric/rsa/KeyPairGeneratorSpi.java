package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
/* loaded from: classes4.dex */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    private static final AlgorithmIdentifier PKCS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    private static final AlgorithmIdentifier PSS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS);
    static final BigInteger defaultPublicExponent = BigInteger.valueOf(65537);
    AlgorithmIdentifier algId;
    RSAKeyPairGenerator engine;
    RSAKeyGenerationParameters param;

    public KeyPairGeneratorSpi(String algorithmName, AlgorithmIdentifier algId) {
        super(algorithmName);
        this.algId = algId;
        this.engine = new RSAKeyPairGenerator();
        RSAKeyGenerationParameters rSAKeyGenerationParameters = new RSAKeyGenerationParameters(defaultPublicExponent, CryptoServicesRegistrar.getSecureRandom(), 2048, PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.param = rSAKeyGenerationParameters;
        this.engine.init(rSAKeyGenerationParameters);
    }

    public KeyPairGeneratorSpi() {
        this(KeyProperties.KEY_ALGORITHM_RSA, PKCS_ALGID);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        RSAKeyGenerationParameters rSAKeyGenerationParameters = new RSAKeyGenerationParameters(defaultPublicExponent, random != null ? random : new SecureRandom(), strength, PrimeCertaintyCalculator.getDefaultCertainty(strength));
        this.param = rSAKeyGenerationParameters;
        this.engine.init(rSAKeyGenerationParameters);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof RSAKeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a RSAKeyGenParameterSpec");
        }
        RSAKeyGenParameterSpec rsaParams = (RSAKeyGenParameterSpec) params;
        RSAKeyGenerationParameters rSAKeyGenerationParameters = new RSAKeyGenerationParameters(rsaParams.getPublicExponent(), random != null ? random : new SecureRandom(), rsaParams.getKeysize(), PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.param = rSAKeyGenerationParameters;
        this.engine.init(rSAKeyGenerationParameters);
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        RSAKeyParameters pub = (RSAKeyParameters) pair.getPublic();
        RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters) pair.getPrivate();
        return new KeyPair(new BCRSAPublicKey(this.algId, pub), new BCRSAPrivateCrtKey(this.algId, priv));
    }

    /* loaded from: classes4.dex */
    public static class PSS extends KeyPairGeneratorSpi {
        public PSS() {
            super("RSASSA-PSS", KeyPairGeneratorSpi.PSS_ALGID);
        }
    }
}
