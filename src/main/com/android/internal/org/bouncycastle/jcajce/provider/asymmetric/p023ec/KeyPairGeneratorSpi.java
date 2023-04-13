package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveSpec;
import com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Hashtable;
import java.util.Map;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi */
/* loaded from: classes4.dex */
public abstract class KeyPairGeneratorSpi extends KeyPairGenerator {
    public KeyPairGeneratorSpi(String algorithmName) {
        super(algorithmName);
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$EC */
    /* loaded from: classes4.dex */
    public static class C4279EC extends KeyPairGeneratorSpi {
        private static Hashtable ecParameters;
        String algorithm;
        ProviderConfiguration configuration;
        Object ecParams;
        ECKeyPairGenerator engine;
        boolean initialised;
        ECKeyGenerationParameters param;
        SecureRandom random;
        int strength;

        static {
            Hashtable hashtable = new Hashtable();
            ecParameters = hashtable;
            hashtable.put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
            ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
            ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
            ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
            ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
            ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
        }

        public C4279EC() {
            super(KeyProperties.KEY_ALGORITHM_EC);
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 256;
            this.random = CryptoServicesRegistrar.getSecureRandom();
            this.initialised = false;
            this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
            this.configuration = BouncyCastleProvider.CONFIGURATION;
        }

        public C4279EC(String algorithm, ProviderConfiguration configuration) {
            super(algorithm);
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 256;
            this.random = CryptoServicesRegistrar.getSecureRandom();
            this.initialised = false;
            this.algorithm = algorithm;
            this.configuration = configuration;
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(int strength, SecureRandom random) {
            this.strength = strength;
            if (random != null) {
                this.random = random;
            }
            ECGenParameterSpec ecParams = (ECGenParameterSpec) ecParameters.get(Integers.valueOf(strength));
            if (ecParams == null) {
                throw new InvalidParameterException("unknown key size.");
            }
            try {
                initialize(ecParams, random);
            } catch (InvalidAlgorithmParameterException e) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
            if (random == null) {
                random = this.random;
            }
            if (params == null) {
                ECParameterSpec implicitCA = this.configuration.getEcImplicitlyCa();
                if (implicitCA == null) {
                    throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
                }
                this.ecParams = null;
                this.param = createKeyGenParamsBC(implicitCA, random);
            } else if (params instanceof ECParameterSpec) {
                this.ecParams = params;
                this.param = createKeyGenParamsBC((ECParameterSpec) params, random);
            } else if (params instanceof java.security.spec.ECParameterSpec) {
                this.ecParams = params;
                this.param = createKeyGenParamsJCE((java.security.spec.ECParameterSpec) params, random);
            } else if (params instanceof ECGenParameterSpec) {
                initializeNamedCurve(((ECGenParameterSpec) params).getName(), random);
            } else if (params instanceof ECNamedCurveGenParameterSpec) {
                initializeNamedCurve(((ECNamedCurveGenParameterSpec) params).getName(), random);
            } else {
                String name = ECUtil.getNameFrom(params);
                if (name != null) {
                    initializeNamedCurve(name, random);
                } else {
                    throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + params);
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public KeyPair generateKeyPair() {
            if (!this.initialised) {
                initialize(this.strength, new SecureRandom());
            }
            AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
            ECPublicKeyParameters pub = (ECPublicKeyParameters) pair.getPublic();
            ECPrivateKeyParameters priv = (ECPrivateKeyParameters) pair.getPrivate();
            Object obj = this.ecParams;
            if (obj instanceof ECParameterSpec) {
                ECParameterSpec p = (ECParameterSpec) obj;
                BCECPublicKey pubKey = new BCECPublicKey(this.algorithm, pub, p, this.configuration);
                return new KeyPair(pubKey, new BCECPrivateKey(this.algorithm, priv, pubKey, p, this.configuration));
            } else if (obj == null) {
                return new KeyPair(new BCECPublicKey(this.algorithm, pub, this.configuration), new BCECPrivateKey(this.algorithm, priv, this.configuration));
            } else {
                java.security.spec.ECParameterSpec p2 = (java.security.spec.ECParameterSpec) obj;
                BCECPublicKey pubKey2 = new BCECPublicKey(this.algorithm, pub, p2, this.configuration);
                return new KeyPair(pubKey2, new BCECPrivateKey(this.algorithm, priv, pubKey2, p2, this.configuration));
            }
        }

        protected ECKeyGenerationParameters createKeyGenParamsBC(ECParameterSpec p, SecureRandom r) {
            return new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), r);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(java.security.spec.ECParameterSpec p, SecureRandom r) {
            X9ECParameters x9P;
            if ((p instanceof ECNamedCurveSpec) && (x9P = ECUtils.getDomainParametersFromName(((ECNamedCurveSpec) p).getName())) != null) {
                ECDomainParameters dp = new ECDomainParameters(x9P.getCurve(), x9P.getG(), x9P.getN(), x9P.getH());
                return new ECKeyGenerationParameters(dp, r);
            }
            ECCurve curve = EC5Util.convertCurve(p.getCurve());
            ECPoint g = EC5Util.convertPoint(curve, p.getGenerator());
            BigInteger n = p.getOrder();
            BigInteger h = BigInteger.valueOf(p.getCofactor());
            ECDomainParameters dp2 = new ECDomainParameters(curve, g, n, h);
            return new ECKeyGenerationParameters(dp2, r);
        }

        protected ECNamedCurveSpec createNamedCurveSpec(String curveName) throws InvalidAlgorithmParameterException {
            X9ECParameters p = ECUtils.getDomainParametersFromName(curveName);
            if (p == null) {
                try {
                    p = ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(curveName));
                    if (p == null) {
                        Map extraCurves = this.configuration.getAdditionalECParameters();
                        p = (X9ECParameters) extraCurves.get(new ASN1ObjectIdentifier(curveName));
                        if (p == null) {
                            throw new InvalidAlgorithmParameterException("unknown curve OID: " + curveName);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
                }
            }
            return new ECNamedCurveSpec(curveName, p.getCurve(), p.getG(), p.getN(), p.getH(), null);
        }

        protected void initializeNamedCurve(String curveName, SecureRandom random) throws InvalidAlgorithmParameterException {
            ECNamedCurveSpec namedCurve = createNamedCurveSpec(curveName);
            this.ecParams = namedCurve;
            this.param = createKeyGenParamsJCE(namedCurve, random);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDSA */
    /* loaded from: classes4.dex */
    public static class ECDSA extends C4279EC {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH */
    /* loaded from: classes4.dex */
    public static class ECDH extends C4279EC {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDHC */
    /* loaded from: classes4.dex */
    public static class ECDHC extends C4279EC {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECMQV */
    /* loaded from: classes4.dex */
    public static class ECMQV extends C4279EC {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}
