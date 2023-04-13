package com.android.internal.org.bouncycastle.jce.provider;

import android.media.MediaMetrics;
import android.security.KeyChain;
import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.io.IOException;
import java.security.AccessController;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public final class BouncyCastleProvider extends Provider implements ConfigurableProvider {
    private static final String ASYMMETRIC_PACKAGE = "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.";
    private static final String DIGEST_PACKAGE = "com.android.internal.org.bouncycastle.jcajce.provider.digest.";
    private static final String KEYSTORE_PACKAGE = "com.android.internal.org.bouncycastle.jcajce.provider.keystore.";
    private static final String SYMMETRIC_PACKAGE = "com.android.internal.org.bouncycastle.jcajce.provider.symmetric.";
    private final Provider privateProvider;
    private static String info = "BouncyCastle Security Provider v1.68";
    public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
    private static final Map keyInfoConverters = new HashMap();
    private static final Class revChkClass = ClassUtil.loadClass(BouncyCastleProvider.class, "java.security.cert.PKIXRevocationChecker");
    private static final String[] SYMMETRIC_GENERIC = {"PBEPBKDF2", "PBEPKCS12", "PBES2AlgorithmParameters"};
    private static final String[] SYMMETRIC_MACS = new String[0];
    private static final String[] SYMMETRIC_CIPHERS = {KeyProperties.KEY_ALGORITHM_AES, "ARC4", "Blowfish", "DES", KeyProperties.KEY_ALGORITHM_3DES, "RC2", "Twofish"};
    private static final String[] ASYMMETRIC_GENERIC = {"X509"};
    private static final String[] ASYMMETRIC_CIPHERS = {"DSA", "DH", KeyProperties.KEY_ALGORITHM_EC, KeyProperties.KEY_ALGORITHM_RSA};
    private static final String[] DIGESTS = {KeyProperties.DIGEST_MD5, "SHA1", "SHA224", "SHA256", "SHA384", "SHA512"};
    public static final String PROVIDER_NAME = "BC";
    private static final String[] KEYSTORES = {PROVIDER_NAME, "BCFKS", KeyChain.EXTRA_PKCS12};

    public BouncyCastleProvider() {
        super(PROVIDER_NAME, 1.68d, info);
        this.privateProvider = new PrivateProvider();
        AccessController.doPrivileged(new PrivilegedAction() { // from class: com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                BouncyCastleProvider.this.setup();
                return null;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setup() {
        loadAlgorithms(DIGEST_PACKAGE, DIGESTS);
        loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_GENERIC);
        loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_MACS);
        loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_CIPHERS);
        loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_GENERIC);
        loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_CIPHERS);
        loadAlgorithms(KEYSTORE_PACKAGE, KEYSTORES);
        put("CertPathValidator.PKIX", "com.android.internal.org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
        put("CertPathBuilder.PKIX", "com.android.internal.org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
        put("CertStore.Collection", "com.android.internal.org.bouncycastle.jce.provider.CertStoreCollectionSpi");
    }

    private void loadAlgorithms(String packageName, String[] names) {
        for (int i = 0; i != names.length; i++) {
            Class clazz = ClassUtil.loadClass(BouncyCastleProvider.class, packageName + names[i] + "$Mappings");
            if (clazz != null) {
                try {
                    ((AlgorithmProvider) clazz.newInstance()).configure(this);
                } catch (Exception e) {
                    throw new InternalError("cannot create instance of " + packageName + names[i] + "$Mappings : " + e);
                }
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void setParameter(String parameterName, Object parameter) {
        ProviderConfiguration providerConfiguration = CONFIGURATION;
        synchronized (providerConfiguration) {
            ((BouncyCastleProviderConfiguration) providerConfiguration).setParameter(parameterName, parameter);
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public boolean hasAlgorithm(String type, String name) {
        return containsKey(new StringBuilder().append(type).append(MediaMetrics.SEPARATOR).append(name).toString()) || containsKey(new StringBuilder().append("Alg.Alias.").append(type).append(MediaMetrics.SEPARATOR).append(name).toString());
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addAlgorithm(String key, String value) {
        if (containsKey(key)) {
            throw new IllegalStateException("duplicate provider key (" + key + ") found");
        }
        put(key, value);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addAlgorithm(String type, ASN1ObjectIdentifier oid, String className) {
        addAlgorithm(type + MediaMetrics.SEPARATOR + oid, className);
        addAlgorithm(type + ".OID." + oid, className);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addKeyInfoConverter(ASN1ObjectIdentifier oid, AsymmetricKeyInfoConverter keyInfoConverter) {
        Map map = keyInfoConverters;
        synchronized (map) {
            map.put(oid, keyInfoConverter);
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier oid) {
        return (AsymmetricKeyInfoConverter) keyInfoConverters.get(oid);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addAttributes(String key, Map<String, String> attributeMap) {
        for (String attributeName : attributeMap.keySet()) {
            String attributeKey = key + " " + attributeName;
            if (containsKey(attributeKey)) {
                throw new IllegalStateException("duplicate provider attribute key (" + attributeKey + ") found");
            }
            put(attributeKey, attributeMap.get(attributeName));
        }
    }

    private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier algorithm) {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter;
        Map map = keyInfoConverters;
        synchronized (map) {
            asymmetricKeyInfoConverter = (AsymmetricKeyInfoConverter) map.get(algorithm);
        }
        return asymmetricKeyInfoConverter;
    }

    public static PublicKey getPublicKey(SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        try {
            return KeyFactory.getInstance(publicKeyInfo.getAlgorithmId().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(publicKeyInfo.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeySpecException ex) {
            throw new IOException(ex);
        }
    }

    public static PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        try {
            return KeyFactory.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm().getId()).generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeySpecException ex) {
            throw new IOException(ex);
        }
    }

    /* loaded from: classes4.dex */
    private static final class PrivateProvider extends Provider {
        public PrivateProvider() {
            super("BCPrivate", 1.0d, "Android BC private use only");
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addPrivateAlgorithm(String key, String value) {
        if (this.privateProvider.containsKey(key)) {
            throw new IllegalStateException("duplicate provider key (" + key + ") found");
        }
        this.privateProvider.put(key, value);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider
    public void addPrivateAlgorithm(String type, ASN1ObjectIdentifier oid, String className) {
        addPrivateAlgorithm(type + MediaMetrics.SEPARATOR + oid, className);
    }

    public Provider getPrivateProvider() {
        return this.privateProvider;
    }
}
