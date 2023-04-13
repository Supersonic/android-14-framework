package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi */
/* loaded from: classes4.dex */
public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter {
    String algorithm;
    ProviderConfiguration configuration;

    KeyFactorySpi(String algorithm, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.configuration = configuration;
    }

    @Override // java.security.KeyFactorySpi
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof ECPublicKey) {
            return new BCECPublicKey((ECPublicKey) key, this.configuration);
        }
        if (key instanceof ECPrivateKey) {
            return new BCECPrivateKey((ECPrivateKey) key, this.configuration);
        }
        throw new InvalidKeyException("key type unknown");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public KeySpec engineGetKeySpec(Key key, Class spec) throws InvalidKeySpecException {
        if ((spec.isAssignableFrom(KeySpec.class) || spec.isAssignableFrom(ECPublicKeySpec.class)) && (key instanceof ECPublicKey)) {
            ECPublicKey k = (ECPublicKey) key;
            if (k.getParams() != null) {
                return new ECPublicKeySpec(k.getW(), k.getParams());
            }
            ECParameterSpec implicitSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(k.getW(), EC5Util.convertSpec(EC5Util.convertCurve(implicitSpec.getCurve(), implicitSpec.getSeed()), implicitSpec));
        } else if ((spec.isAssignableFrom(KeySpec.class) || spec.isAssignableFrom(ECPrivateKeySpec.class)) && (key instanceof ECPrivateKey)) {
            ECPrivateKey k2 = (ECPrivateKey) key;
            if (k2.getParams() != null) {
                return new ECPrivateKeySpec(k2.getS(), k2.getParams());
            }
            ECParameterSpec implicitSpec2 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(k2.getS(), EC5Util.convertSpec(EC5Util.convertCurve(implicitSpec2.getCurve(), implicitSpec2.getSeed()), implicitSpec2));
        } else if (spec.isAssignableFrom(com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec.class) && (key instanceof ECPublicKey)) {
            ECPublicKey k3 = (ECPublicKey) key;
            if (k3.getParams() != null) {
                return new com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(k3.getParams(), k3.getW()), EC5Util.convertSpec(k3.getParams()));
            }
            return new com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(k3.getParams(), k3.getW()), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        } else if (spec.isAssignableFrom(com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec.class) && (key instanceof ECPrivateKey)) {
            ECPrivateKey k4 = (ECPrivateKey) key;
            if (k4.getParams() != null) {
                return new com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec(k4.getS(), EC5Util.convertSpec(k4.getParams()));
            }
            return new com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec(k4.getS(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        } else {
            return super.engineGetKeySpec(key, spec);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec) keySpec, this.configuration);
        }
        if (keySpec instanceof ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (ECPrivateKeySpec) keySpec, this.configuration);
        }
        return super.engineGeneratePrivate(keySpec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec) keySpec, this.configuration);
            }
            if (keySpec instanceof ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (ECPublicKeySpec) keySpec, this.configuration);
            }
            return super.engineGeneratePublic(keySpec);
        } catch (Exception e) {
            throw new InvalidKeySpecException("invalid KeySpec: " + e.getMessage(), e);
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algOid.equals((ASN1Primitive) X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPrivateKey(this.algorithm, keyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getAlgorithm().getAlgorithm();
        if (algOid.equals((ASN1Primitive) X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPublicKey(this.algorithm, keyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$EC */
    /* loaded from: classes4.dex */
    public static class C4278EC extends KeyFactorySpi {
        public C4278EC() {
            super(KeyProperties.KEY_ALGORITHM_EC, BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDSA */
    /* loaded from: classes4.dex */
    public static class ECDSA extends KeyFactorySpi {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDH */
    /* loaded from: classes4.dex */
    public static class ECDH extends KeyFactorySpi {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDHC */
    /* loaded from: classes4.dex */
    public static class ECDHC extends KeyFactorySpi {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECMQV */
    /* loaded from: classes4.dex */
    public static class ECMQV extends KeyFactorySpi {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}
