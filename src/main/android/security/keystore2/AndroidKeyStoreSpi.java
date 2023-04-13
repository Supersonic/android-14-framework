package android.security.keystore2;

import android.app.AppGlobals;
import android.hardware.biometrics.BiometricManager;
import android.hardware.security.keymint.KeyParameter;
import android.security.GateKeeper;
import android.security.KeyStore2;
import android.security.KeyStoreException;
import android.security.KeyStoreParameter;
import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.security.keystore.SecureKeyImportUnavailableException;
import android.security.keystore.WrappedKeyEntry;
import android.system.keystore2.AuthenticatorSpec;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyEntryResponse;
import android.system.keystore2.KeyMetadata;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.EdECKey;
import java.security.interfaces.EdECPrivateKey;
import java.security.interfaces.XECKey;
import java.security.interfaces.XECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.crypto.SecretKey;
/* loaded from: classes3.dex */
public class AndroidKeyStoreSpi extends KeyStoreSpi {
    public static final String NAME = "AndroidKeyStore";
    public static final String TAG = "AndroidKeyStoreSpi";
    private KeyStore2 mKeyStore;
    private int mNamespace = -1;

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        try {
            return AndroidKeyStoreProvider.loadAndroidKeyStoreKeyFromKeystore(this.mKeyStore, alias, this.mNamespace);
        } catch (KeyPermanentlyInvalidatedException e) {
            throw new UnrecoverableKeyException(e.getMessage());
        } catch (UnrecoverableKeyException e2) {
            Throwable cause = e2.getCause();
            if ((cause instanceof KeyStoreException) && ((KeyStoreException) cause).getErrorCode() == 7) {
                return null;
            }
            throw e2;
        }
    }

    private KeyDescriptor makeKeyDescriptor(String alias) {
        KeyDescriptor descriptor = new KeyDescriptor();
        descriptor.domain = getTargetDomain();
        descriptor.nspace = this.mNamespace;
        descriptor.alias = alias;
        descriptor.blob = null;
        return descriptor;
    }

    private int getTargetDomain() {
        if (this.mNamespace == -1) {
            return 0;
        }
        return 2;
    }

    private KeyEntryResponse getKeyMetadata(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        KeyDescriptor descriptor = makeKeyDescriptor(alias);
        try {
            return this.mKeyStore.getKeyEntry(descriptor);
        } catch (KeyStoreException e) {
            if (e.getErrorCode() != 7) {
                Log.m103w(TAG, "Could not get key metadata from Keystore.", e);
                return null;
            }
            return null;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        X509Certificate leaf;
        Certificate[] caList;
        KeyEntryResponse response = getKeyMetadata(alias);
        if (response == null || response.metadata.certificate == null || (leaf = toCertificate(response.metadata.certificate)) == null) {
            return null;
        }
        byte[] caBytes = response.metadata.certificateChain;
        if (caBytes != null) {
            Collection<X509Certificate> caChain = toCertificates(caBytes);
            caList = new Certificate[caChain.size() + 1];
            int i = 1;
            for (X509Certificate x509Certificate : caChain) {
                caList[i] = x509Certificate;
                i++;
            }
        } else {
            caList = new Certificate[1];
        }
        caList[0] = leaf;
        return caList;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        KeyEntryResponse response = getKeyMetadata(alias);
        if (response == null) {
            return null;
        }
        byte[] encodedCert = response.metadata.certificate;
        if (encodedCert != null) {
            return toCertificate(encodedCert);
        }
        byte[] encodedCert2 = response.metadata.certificateChain;
        if (encodedCert2 == null) {
            return null;
        }
        return toCertificate(encodedCert2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X509Certificate toCertificate(byte[] bytes) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.m103w(NAME, "Couldn't parse certificate in keystore", e);
            return null;
        }
    }

    private static Collection<X509Certificate> toCertificates(byte[] bytes) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return certFactory.generateCertificates(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.m103w(NAME, "Couldn't parse certificates in keystore", e);
            return new ArrayList();
        }
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        KeyEntryResponse response = getKeyMetadata(alias);
        if (response == null || response.metadata.modificationTimeMs == -1) {
            return null;
        }
        return new Date(response.metadata.modificationTimeMs);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws java.security.KeyStoreException {
        if (password != null && password.length > 0) {
            throw new java.security.KeyStoreException("entries cannot be protected with passwords");
        }
        if (key instanceof PrivateKey) {
            setPrivateKeyEntry(alias, (PrivateKey) key, chain, null);
        } else if (key instanceof SecretKey) {
            setSecretKeyEntry(alias, (SecretKey) key, null);
        } else {
            throw new java.security.KeyStoreException("Only PrivateKey and SecretKey are supported");
        }
    }

    private static KeyProtection getLegacyKeyProtectionParameter(PrivateKey key) throws java.security.KeyStoreException {
        KeyProtection.Builder specBuilder;
        String keyAlgorithm = key.getAlgorithm();
        if (KeyProperties.KEY_ALGORITHM_EC.equalsIgnoreCase(keyAlgorithm)) {
            specBuilder = new KeyProtection.Builder(12);
            specBuilder.setDigests(KeyProperties.DIGEST_NONE, "SHA-1", KeyProperties.DIGEST_SHA224, "SHA-256", KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
        } else if (KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(keyAlgorithm)) {
            specBuilder = new KeyProtection.Builder(15);
            specBuilder.setDigests(KeyProperties.DIGEST_NONE, KeyProperties.DIGEST_MD5, "SHA-1", KeyProperties.DIGEST_SHA224, "SHA-256", KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
            specBuilder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE, KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1, KeyProperties.ENCRYPTION_PADDING_RSA_OAEP);
            specBuilder.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1, KeyProperties.SIGNATURE_PADDING_RSA_PSS);
            specBuilder.setRandomizedEncryptionRequired(false);
        } else {
            throw new java.security.KeyStoreException("Unsupported key algorithm: " + keyAlgorithm);
        }
        specBuilder.setUserAuthenticationRequired(false);
        return specBuilder.build();
    }

    private void setPrivateKeyEntry(String alias, PrivateKey key, Certificate[] chain, KeyStore.ProtectionParameter param) throws java.security.KeyStoreException {
        int flags;
        KeyProtection spec;
        int flags2;
        byte[] chainBytes;
        int[] keymasterEncryptionPaddings;
        int length;
        int i;
        int targetDomain;
        KeyStoreSecurityLevel securityLevelInterface;
        KeyDescriptor descriptor;
        int i2;
        String str;
        if (param == null) {
            flags = 0;
            spec = getLegacyKeyProtectionParameter(key);
            flags2 = 1;
        } else if (param instanceof KeyStoreParameter) {
            KeyStoreParameter keyStoreParameter = (KeyStoreParameter) param;
            flags = 0;
            spec = getLegacyKeyProtectionParameter(key);
            flags2 = 1;
        } else if (!(param instanceof KeyProtection)) {
            throw new java.security.KeyStoreException("Unsupported protection parameter class:" + param.getClass().getName() + ". Supported: " + KeyProtection.class.getName() + ", " + KeyStoreParameter.class.getName());
        } else {
            KeyProtection spec2 = (KeyProtection) param;
            int flags3 = spec2.isCriticalToDeviceEncryption() ? 0 | 1 : 0;
            if (spec2.isStrongBoxBacked()) {
                flags = flags3;
                spec = spec2;
                flags2 = 2;
            } else {
                flags = flags3;
                spec = spec2;
                flags2 = 1;
            }
        }
        if (chain == null || chain.length == 0) {
            throw new java.security.KeyStoreException("Must supply at least one Certificate with PrivateKey");
        }
        X509Certificate[] x509chain = new X509Certificate[chain.length];
        for (int i3 = 0; i3 < chain.length; i3++) {
            if (!"X.509".equals(chain[i3].getType())) {
                throw new java.security.KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i3);
            }
            if (!(chain[i3] instanceof X509Certificate)) {
                throw new java.security.KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i3);
            }
            x509chain[i3] = (X509Certificate) chain[i3];
        }
        try {
            byte[] userCertBytes = x509chain[0].getEncoded();
            if (chain.length > 1) {
                byte[][] certsBytes = new byte[x509chain.length - 1];
                int totalCertLength = 0;
                for (int i4 = 0; i4 < certsBytes.length; i4++) {
                    try {
                        certsBytes[i4] = x509chain[i4 + 1].getEncoded();
                        totalCertLength += certsBytes[i4].length;
                    } catch (CertificateEncodingException e) {
                        throw new java.security.KeyStoreException("Failed to encode certificate #" + i4, e);
                    }
                }
                byte[] chainBytes2 = new byte[totalCertLength];
                int outputOffset = 0;
                for (int i5 = 0; i5 < certsBytes.length; i5++) {
                    int certLength = certsBytes[i5].length;
                    System.arraycopy(certsBytes[i5], 0, chainBytes2, outputOffset, certLength);
                    outputOffset += certLength;
                    certsBytes[i5] = null;
                }
                chainBytes = chainBytes2;
            } else {
                chainBytes = null;
            }
            int targetDomain2 = getTargetDomain();
            String str2 = "Failed to store certificate and certificate chain";
            if (key instanceof AndroidKeyStorePrivateKey) {
                AndroidKeyStoreKey ksKey = (AndroidKeyStoreKey) key;
                KeyDescriptor descriptor2 = ksKey.getUserKeyDescriptor();
                assertCanReplace(alias, targetDomain2, this.mNamespace, descriptor2);
                try {
                    this.mKeyStore.updateSubcomponents(((AndroidKeyStorePrivateKey) key).getKeyIdDescriptor(), userCertBytes, chainBytes);
                    return;
                } catch (KeyStoreException e2) {
                    throw new java.security.KeyStoreException("Failed to store certificate and certificate chain", e2);
                }
            }
            String keyFormat = key.getFormat();
            if (keyFormat == null || !"PKCS#8".equals(keyFormat)) {
                throw new java.security.KeyStoreException("Unsupported private key export format: " + keyFormat + ". Only private keys which export their key material in PKCS#8 format are supported.");
            }
            byte[] pkcs8EncodedPrivateKeyBytes = key.getEncoded();
            if (pkcs8EncodedPrivateKeyBytes == null) {
                throw new java.security.KeyStoreException("Private key did not export any key material");
            }
            final List<KeyParameter> importArgs = new ArrayList<>();
            try {
                importArgs.add(KeyStore2ParameterUtils.makeEnum(268435458, KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm(key.getAlgorithm())));
                KeyStore2ParameterUtils.forEachSetFlag(spec.getPurposes(), new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreSpi$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        importArgs.add(KeyStore2ParameterUtils.makeEnum(536870913, KeyProperties.Purpose.toKeymaster(((Integer) obj).intValue())));
                    }
                });
                if (spec.isDigestsSpecified()) {
                    try {
                        String[] digests = spec.getDigests();
                        int length2 = digests.length;
                        int i6 = 0;
                        while (i6 < length2) {
                            String[] strArr = digests;
                            int i7 = length2;
                            importArgs.add(KeyStore2ParameterUtils.makeEnum(536870917, KeyProperties.Digest.toKeymaster(digests[i6])));
                            i6++;
                            digests = strArr;
                            length2 = i7;
                        }
                    } catch (IllegalArgumentException | IllegalStateException e3) {
                        e = e3;
                    }
                }
                String[] blockModes = spec.getBlockModes();
                int length3 = blockModes.length;
                int i8 = 0;
                while (i8 < length3) {
                    String blockMode = blockModes[i8];
                    String[] strArr2 = blockModes;
                    int i9 = length3;
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870916, KeyProperties.BlockMode.toKeymaster(blockMode)));
                    i8++;
                    blockModes = strArr2;
                    length3 = i9;
                }
                keymasterEncryptionPaddings = KeyProperties.EncryptionPadding.allToKeymaster(spec.getEncryptionPaddings());
                if ((spec.getPurposes() & 1) != 0 && spec.isRandomizedEncryptionRequired()) {
                    for (int keymasterPadding : keymasterEncryptionPaddings) {
                        if (!KeymasterUtils.isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(keymasterPadding)) {
                            throw new java.security.KeyStoreException("Randomized encryption (IND-CPA) required but is violated by encryption padding mode: " + KeyProperties.EncryptionPadding.fromKeymaster(keymasterPadding) + ". See KeyProtection documentation.");
                        }
                    }
                }
                length = keymasterEncryptionPaddings.length;
                i = 0;
            } catch (IllegalArgumentException | IllegalStateException e4) {
                e = e4;
            }
            while (true) {
                String keyFormat2 = keyFormat;
                if (i >= length) {
                    break;
                }
                try {
                    int padding = keymasterEncryptionPaddings[i];
                    int[] keymasterEncryptionPaddings2 = keymasterEncryptionPaddings;
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870918, padding));
                    if (padding != 2) {
                        i2 = length;
                        str = str2;
                        targetDomain = targetDomain2;
                    } else if (spec.isDigestsSpecified()) {
                        boolean hasDefaultMgf1DigestBeenAdded = false;
                        String[] digests2 = spec.getDigests();
                        i2 = length;
                        int length4 = digests2.length;
                        int i10 = 0;
                        while (true) {
                            str = str2;
                            targetDomain = targetDomain2;
                            if (i10 >= length4) {
                                break;
                            }
                            try {
                                String digest = digests2[i10];
                                importArgs.add(KeyStore2ParameterUtils.makeEnum(536871115, KeyProperties.Digest.toKeymaster(digest)));
                                hasDefaultMgf1DigestBeenAdded |= digest.equals("SHA-1");
                                i10++;
                                str2 = str;
                                targetDomain2 = targetDomain;
                                digests2 = digests2;
                            } catch (IllegalArgumentException | IllegalStateException e5) {
                                e = e5;
                            }
                        }
                        if (!hasDefaultMgf1DigestBeenAdded) {
                            importArgs.add(KeyStore2ParameterUtils.makeEnum(536871115, KeyProperties.Digest.toKeymaster("SHA-1")));
                        }
                    } else {
                        i2 = length;
                        str = str2;
                        targetDomain = targetDomain2;
                    }
                    i++;
                    keyFormat = keyFormat2;
                    keymasterEncryptionPaddings = keymasterEncryptionPaddings2;
                    length = i2;
                    str2 = str;
                    targetDomain2 = targetDomain;
                } catch (IllegalArgumentException | IllegalStateException e6) {
                    e = e6;
                }
                e = e5;
                throw new java.security.KeyStoreException(e);
            }
            String str3 = str2;
            targetDomain = targetDomain2;
            try {
                for (String padding2 : spec.getSignaturePaddings()) {
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870918, KeyProperties.SignaturePadding.toKeymaster(padding2)));
                }
                KeyStore2ParameterUtils.addUserAuthArgs(importArgs, spec);
                if (spec.getKeyValidityStart() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613136, spec.getKeyValidityStart()));
                }
                if (spec.getKeyValidityForOriginationEnd() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613137, spec.getKeyValidityForOriginationEnd()));
                }
                if (spec.getKeyValidityForConsumptionEnd() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613138, spec.getKeyValidityForConsumptionEnd()));
                }
                if (spec.getMaxUsageCount() != -1) {
                    importArgs.add(KeyStore2ParameterUtils.makeInt(805306773, spec.getMaxUsageCount()));
                }
                if (3 == KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm(key.getAlgorithm())) {
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(268435466, getKeymasterEcCurve(key)));
                }
                try {
                    securityLevelInterface = this.mKeyStore.getSecurityLevel(flags2);
                    descriptor = makeKeyDescriptor(alias);
                } catch (KeyStoreException e7) {
                    e = e7;
                }
                try {
                    KeyMetadata metadata = securityLevelInterface.importKey(descriptor, null, importArgs, flags, pkcs8EncodedPrivateKeyBytes);
                    try {
                        this.mKeyStore.updateSubcomponents(metadata.key, userCertBytes, chainBytes);
                    } catch (KeyStoreException e8) {
                        this.mKeyStore.deleteKey(metadata.key);
                        throw new java.security.KeyStoreException(str3, e8);
                    }
                } catch (KeyStoreException e9) {
                    e = e9;
                    throw new java.security.KeyStoreException("Failed to store private key", e);
                }
            } catch (IllegalArgumentException | IllegalStateException e10) {
                e = e10;
            }
        } catch (CertificateEncodingException e11) {
            throw new java.security.KeyStoreException("Failed to encode certificate #0", e11);
        }
    }

    private int getKeymasterEcCurve(PrivateKey key) {
        if (key instanceof ECKey) {
            ECParameterSpec param = ((ECPrivateKey) key).getParams();
            int kmECCurve = KeymasterUtils.getKeymasterEcCurve(KeymasterUtils.getCurveName(param));
            if (kmECCurve >= 0) {
                return kmECCurve;
            }
        } else if (key instanceof XECKey) {
            AlgorithmParameterSpec param2 = ((XECPrivateKey) key).getParams();
            if (param2.equals(NamedParameterSpec.X25519)) {
                return 4;
            }
        } else if (key.getAlgorithm().equals(KeyProperties.KEY_ALGORITHM_XDH)) {
            return 4;
        } else {
            if ((key instanceof EdECKey) && ((EdECPrivateKey) key).getParams().equals(NamedParameterSpec.ED25519)) {
                return 4;
            }
        }
        throw new IllegalArgumentException("Unexpected Key " + key.getClass().getName());
    }

    private static void assertCanReplace(String alias, int targetDomain, int targetNamespace, KeyDescriptor descriptor) throws java.security.KeyStoreException {
        if (!alias.equals(descriptor.alias) || descriptor.domain != targetDomain || (descriptor.domain == 2 && descriptor.nspace != targetNamespace)) {
            throw new java.security.KeyStoreException("Can only replace keys with same alias: " + alias + " != " + descriptor.alias + " in the same target domain: " + targetDomain + " != " + descriptor.domain + (targetDomain == 2 ? " in the same target namespace: " + targetNamespace + " != " + descriptor.nspace : ""));
        }
    }

    private void setSecretKeyEntry(String alias, SecretKey key, KeyStore.ProtectionParameter param) throws java.security.KeyStoreException {
        String[] digests;
        String[] encryptionPaddings;
        int flags;
        KeyStoreSecurityLevel securityLevelInterface;
        KeyDescriptor descriptor;
        int keymasterAlgorithm;
        int keymasterAlgorithm2;
        if (param != null && !(param instanceof KeyProtection)) {
            throw new java.security.KeyStoreException("Unsupported protection parameter class: " + param.getClass().getName() + ". Supported: " + KeyProtection.class.getName());
        }
        KeyProtection params = (KeyProtection) param;
        int targetDomain = getTargetDomain();
        if (key instanceof AndroidKeyStoreSecretKey) {
            String str = ((AndroidKeyStoreSecretKey) key).getUserKeyDescriptor().alias;
            KeyDescriptor descriptor2 = ((AndroidKeyStoreSecretKey) key).getUserKeyDescriptor();
            assertCanReplace(alias, targetDomain, this.mNamespace, descriptor2);
            if (params != null) {
                throw new java.security.KeyStoreException("Modifying KeyStore-backed key using protection parameters not supported");
            }
        } else if (params == null) {
            throw new java.security.KeyStoreException("Protection parameters must be specified when importing a symmetric key");
        } else {
            String keyExportFormat = key.getFormat();
            if (keyExportFormat == null) {
                throw new java.security.KeyStoreException("Only secret keys that export their key material are supported");
            }
            if (!"RAW".equals(keyExportFormat)) {
                throw new java.security.KeyStoreException("Unsupported secret key material export format: " + keyExportFormat);
            }
            byte[] keyMaterial = key.getEncoded();
            if (keyMaterial == null) {
                throw new java.security.KeyStoreException("Key did not export its key material despite supporting RAW format export");
            }
            final List<KeyParameter> importArgs = new ArrayList<>();
            try {
                int keymasterAlgorithm3 = KeyProperties.KeyAlgorithm.toKeymasterSecretKeyAlgorithm(key.getAlgorithm());
                importArgs.add(KeyStore2ParameterUtils.makeEnum(268435458, keymasterAlgorithm3));
                if (keymasterAlgorithm3 == 128) {
                    int keymasterImpliedDigest = KeyProperties.KeyAlgorithm.toKeymasterDigest(key.getAlgorithm());
                    if (keymasterImpliedDigest == -1) {
                        throw new ProviderException("HMAC key algorithm digest unknown for key algorithm " + key.getAlgorithm());
                    }
                    if (params.isDigestsSpecified()) {
                        int[] keymasterDigestsFromParams = KeyProperties.Digest.allToKeymaster(params.getDigests());
                        if (keymasterDigestsFromParams.length != 1 || keymasterDigestsFromParams[0] != keymasterImpliedDigest) {
                            throw new java.security.KeyStoreException("Unsupported digests specification: " + Arrays.asList(params.getDigests()) + ". Only " + KeyProperties.Digest.fromKeymaster(keymasterImpliedDigest) + " supported for HMAC key algorithm " + key.getAlgorithm());
                        }
                    }
                    int outputBits = KeymasterUtils.getDigestOutputSizeBits(keymasterImpliedDigest);
                    if (outputBits == -1) {
                        throw new ProviderException("HMAC key authorized for unsupported digest: " + KeyProperties.Digest.fromKeymaster(keymasterImpliedDigest));
                    }
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870917, keymasterImpliedDigest));
                    importArgs.add(KeyStore2ParameterUtils.makeInt(805306376, outputBits));
                } else if (params.isDigestsSpecified()) {
                    for (String digest : params.getDigests()) {
                        importArgs.add(KeyStore2ParameterUtils.makeEnum(536870917, KeyProperties.Digest.toKeymaster(digest)));
                    }
                }
                KeyStore2ParameterUtils.forEachSetFlag(params.getPurposes(), new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreSpi$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        importArgs.add(KeyStore2ParameterUtils.makeEnum(536870913, KeyProperties.Purpose.toKeymaster(((Integer) obj).intValue())));
                    }
                });
                boolean indCpa = false;
                if ((params.getPurposes() & 1) != 0) {
                    if (((KeyProtection) param).isRandomizedEncryptionRequired()) {
                        indCpa = true;
                    } else {
                        importArgs.add(KeyStore2ParameterUtils.makeBool(1879048199));
                    }
                }
                String[] blockModes = params.getBlockModes();
                int length = blockModes.length;
                int i = 0;
                while (i < length) {
                    String blockMode = blockModes[i];
                    int keymasterBlockMode = KeyProperties.BlockMode.toKeymaster(blockMode);
                    if (indCpa && !KeymasterUtils.isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(keymasterBlockMode)) {
                        throw new java.security.KeyStoreException("Randomized encryption (IND-CPA) required but may be violated by block mode: " + blockMode + ". See KeyProtection documentation.");
                    }
                    if (keymasterAlgorithm3 == 32) {
                        keymasterAlgorithm = keymasterAlgorithm3;
                        keymasterAlgorithm2 = keymasterBlockMode;
                        if (keymasterAlgorithm2 == 32) {
                            importArgs.add(KeyStore2ParameterUtils.makeInt(805306376, 96));
                        }
                    } else {
                        keymasterAlgorithm = keymasterAlgorithm3;
                        keymasterAlgorithm2 = keymasterBlockMode;
                    }
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870916, keymasterAlgorithm2));
                    i++;
                    keymasterAlgorithm3 = keymasterAlgorithm;
                }
                if (params.getSignaturePaddings().length > 0) {
                    throw new java.security.KeyStoreException("Signature paddings not supported for symmetric keys");
                }
                for (String padding : params.getEncryptionPaddings()) {
                    importArgs.add(KeyStore2ParameterUtils.makeEnum(536870918, KeyProperties.EncryptionPadding.toKeymaster(padding)));
                }
                KeyStore2ParameterUtils.addUserAuthArgs(importArgs, params);
                if (params.getKeyValidityStart() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613136, params.getKeyValidityStart()));
                }
                if (params.getKeyValidityForOriginationEnd() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613137, params.getKeyValidityForOriginationEnd()));
                }
                if (params.getKeyValidityForConsumptionEnd() != null) {
                    importArgs.add(KeyStore2ParameterUtils.makeDate(1610613138, params.getKeyValidityForConsumptionEnd()));
                }
                if (params.getMaxUsageCount() != -1) {
                    importArgs.add(KeyStore2ParameterUtils.makeInt(805306773, params.getMaxUsageCount()));
                }
                if (params.isRollbackResistant()) {
                    importArgs.add(KeyStore2ParameterUtils.makeBool(1879048495));
                }
                if (!params.isCriticalToDeviceEncryption()) {
                    flags = 0;
                } else {
                    int flags2 = 0 | 1;
                    flags = flags2;
                }
                int securityLevel = params.isStrongBoxBacked() ? 2 : 1;
                try {
                    securityLevelInterface = this.mKeyStore.getSecurityLevel(securityLevel);
                    descriptor = makeKeyDescriptor(alias);
                } catch (KeyStoreException e) {
                    e = e;
                }
                try {
                    securityLevelInterface.importKey(descriptor, null, importArgs, flags, keyMaterial);
                } catch (KeyStoreException e2) {
                    e = e2;
                    throw new java.security.KeyStoreException("Failed to import secret key.", e);
                }
            } catch (IllegalArgumentException | IllegalStateException e3) {
                throw new java.security.KeyStoreException(e3);
            }
        }
    }

    private void setWrappedKeyEntry(String alias, WrappedKeyEntry entry, KeyStore.ProtectionParameter param) throws java.security.KeyStoreException {
        int digest;
        int padding;
        if (param != null) {
            throw new java.security.KeyStoreException("Protection parameters are specified inside wrapped keys");
        }
        byte[] maskingKey = new byte[32];
        String[] parts = entry.getTransformation().split("/");
        List<KeyParameter> args = new ArrayList<>();
        String algorithm = parts[0];
        if (KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(algorithm)) {
            args.add(KeyStore2ParameterUtils.makeEnum(268435458, 1));
            if (parts.length > 1) {
                String mode = parts[1];
                args.add(KeyStore2ParameterUtils.makeEnum(536870916, KeyProperties.BlockMode.toKeymaster(mode)));
            }
            if (parts.length > 2 && (padding = KeyProperties.EncryptionPadding.toKeymaster(parts[2])) != 1) {
                args.add(KeyStore2ParameterUtils.makeEnum(536870918, padding));
            }
            KeyGenParameterSpec spec = (KeyGenParameterSpec) entry.getAlgorithmParameterSpec();
            if (spec.isDigestsSpecified() && (digest = KeyProperties.Digest.toKeymaster(spec.getDigests()[0])) != 0) {
                args.add(KeyStore2ParameterUtils.makeEnum(536870917, digest));
            }
            KeyDescriptor wrappingkey = makeKeyDescriptor(entry.getWrappingKeyAlias());
            try {
                KeyEntryResponse response = this.mKeyStore.getKeyEntry(wrappingkey);
                KeyDescriptor wrappedKey = makeKeyDescriptor(alias);
                KeyStoreSecurityLevel securityLevel = new KeyStoreSecurityLevel(response.iSecurityLevel);
                BiometricManager bm = (BiometricManager) AppGlobals.getInitialApplication().getSystemService(BiometricManager.class);
                long[] biometricSids = bm.getAuthenticatorIds();
                List<AuthenticatorSpec> authenticatorSpecs = new ArrayList<>();
                AuthenticatorSpec authenticatorSpec = new AuthenticatorSpec();
                authenticatorSpec.authenticatorType = 1;
                authenticatorSpec.authenticatorId = GateKeeper.getSecureUserId();
                authenticatorSpecs.add(authenticatorSpec);
                int i = 0;
                for (int length = biometricSids.length; i < length; length = length) {
                    long sid = biometricSids[i];
                    AuthenticatorSpec authSpec = new AuthenticatorSpec();
                    authSpec.authenticatorType = 2;
                    authSpec.authenticatorId = sid;
                    authenticatorSpecs.add(authSpec);
                    i++;
                    maskingKey = maskingKey;
                }
                try {
                } catch (KeyStoreException e) {
                    e = e;
                }
                try {
                    securityLevel.importWrappedKey(wrappedKey, wrappingkey, entry.getWrappedKeyBytes(), null, args, (AuthenticatorSpec[]) authenticatorSpecs.toArray(new AuthenticatorSpec[0]));
                    return;
                } catch (KeyStoreException e2) {
                    e = e2;
                    switch (e.getErrorCode()) {
                        case -100:
                            throw new SecureKeyImportUnavailableException("Could not import wrapped key");
                        default:
                            throw new java.security.KeyStoreException("Failed to import wrapped key. Keystore error code: " + e.getErrorCode(), e);
                    }
                }
            } catch (KeyStoreException e3) {
                throw new java.security.KeyStoreException("Failed to import wrapped key. Keystore error code: " + e3.getErrorCode(), e3);
            }
        }
        throw new java.security.KeyStoreException("Algorithm \"" + algorithm + "\" not supported for wrapping. Only RSA wrapping keys are supported.");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] userKey, Certificate[] chain) throws java.security.KeyStoreException {
        throw new java.security.KeyStoreException("Operation not supported because key encoding is unknown");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) throws java.security.KeyStoreException {
        if (isKeyEntry(alias)) {
            throw new java.security.KeyStoreException("Entry exists and is not a trusted certificate");
        }
        if (cert == null) {
            throw new NullPointerException("cert == null");
        }
        try {
            byte[] encoded = cert.getEncoded();
            try {
                this.mKeyStore.updateSubcomponents(makeKeyDescriptor(alias), null, encoded);
            } catch (KeyStoreException e) {
                throw new java.security.KeyStoreException("Couldn't insert certificate.", e);
            }
        } catch (CertificateEncodingException e2) {
            throw new java.security.KeyStoreException(e2);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws java.security.KeyStoreException {
        KeyDescriptor descriptor = makeKeyDescriptor(alias);
        try {
            this.mKeyStore.deleteKey(descriptor);
        } catch (KeyStoreException e) {
            if (e.getErrorCode() != 7) {
                throw new java.security.KeyStoreException("Failed to delete entry: " + alias, e);
            }
        }
    }

    private Set<String> getUniqueAliases() {
        try {
            KeyDescriptor[] keys = this.mKeyStore.list(getTargetDomain(), this.mNamespace);
            Set<String> aliases = new HashSet<>(keys.length);
            for (KeyDescriptor d : keys) {
                aliases.add(d.alias);
            }
            return aliases;
        } catch (KeyStoreException e) {
            Log.m109e(TAG, "Failed to list keystore entries.", e);
            return new HashSet();
        }
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(getUniqueAliases());
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        if (alias != null) {
            return getKeyMetadata(alias) != null;
        }
        throw new NullPointerException("alias == null");
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return getUniqueAliases().size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        return isKeyEntry(alias);
    }

    private boolean isKeyEntry(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        KeyEntryResponse response = getKeyMetadata(alias);
        return (response == null || response.iSecurityLevel == null) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        KeyEntryResponse response = getKeyMetadata(alias);
        return (response == null || response.metadata.certificateChain == null || response.iSecurityLevel != null) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate cert) {
        if (cert == null) {
            return null;
        }
        if (!"X.509".equalsIgnoreCase(cert.getType())) {
            Log.m110e(TAG, "In engineGetCertificateAlias: only X.509 certificates are supported.");
            return null;
        }
        try {
            byte[] targetCertBytes = cert.getEncoded();
            if (targetCertBytes == null) {
                return null;
            }
            KeyDescriptor[] keyDescriptors = null;
            try {
                keyDescriptors = this.mKeyStore.list(getTargetDomain(), this.mNamespace);
            } catch (KeyStoreException e) {
                Log.m103w(TAG, "Failed to get list of keystore entries.", e);
            }
            String caAlias = null;
            for (KeyDescriptor d : keyDescriptors) {
                KeyEntryResponse response = getKeyMetadata(d.alias);
                if (response != null) {
                    if (response.metadata.certificate != null) {
                        if (Arrays.equals(response.metadata.certificate, targetCertBytes)) {
                            return d.alias;
                        }
                    } else if (response.metadata.certificateChain != null && caAlias == null && Arrays.equals(response.metadata.certificateChain, targetCertBytes)) {
                        caAlias = d.alias;
                    }
                }
            }
            return caAlias;
        } catch (CertificateEncodingException e2) {
            Log.m109e(TAG, "While trying to get the alias for a certificate.", e2);
            return null;
        }
    }

    public void initForTesting(KeyStore2 keystore) {
        this.mKeyStore = keystore;
        this.mNamespace = -1;
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (stream != null) {
            throw new IllegalArgumentException("InputStream not supported");
        }
        if (password != null) {
            throw new IllegalArgumentException("password not supported");
        }
        this.mKeyStore = KeyStore2.getInstance();
        this.mNamespace = -1;
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        int namespace = -1;
        if (param != null) {
            if (param instanceof AndroidKeyStoreLoadStoreParameter) {
                namespace = ((AndroidKeyStoreLoadStoreParameter) param).getNamespace();
            } else {
                throw new IllegalArgumentException("Unsupported param type: " + param.getClass());
            }
        }
        this.mKeyStore = KeyStore2.getInstance();
        this.mNamespace = namespace;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter param) throws java.security.KeyStoreException {
        if (entry == null) {
            throw new java.security.KeyStoreException("entry == null");
        }
        if (entry instanceof KeyStore.TrustedCertificateEntry) {
            KeyStore.TrustedCertificateEntry trE = (KeyStore.TrustedCertificateEntry) entry;
            engineDeleteEntry(alias);
            engineSetCertificateEntry(alias, trE.getTrustedCertificate());
        } else if (entry instanceof KeyStore.PrivateKeyEntry) {
            KeyStore.PrivateKeyEntry prE = (KeyStore.PrivateKeyEntry) entry;
            setPrivateKeyEntry(alias, prE.getPrivateKey(), prE.getCertificateChain(), param);
        } else if (entry instanceof KeyStore.SecretKeyEntry) {
            KeyStore.SecretKeyEntry secE = (KeyStore.SecretKeyEntry) entry;
            setSecretKeyEntry(alias, secE.getSecretKey(), param);
        } else if (entry instanceof WrappedKeyEntry) {
            WrappedKeyEntry wke = (WrappedKeyEntry) entry;
            setWrappedKeyEntry(alias, wke, param);
        } else {
            throw new java.security.KeyStoreException("Entry must be a PrivateKeyEntry, SecretKeyEntry, WrappedKeyEntry or TrustedCertificateEntry; was " + entry);
        }
    }
}
