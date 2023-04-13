package android.security.keystore2;

import android.app.ActivityThread;
import android.app.AppGlobals;
import android.hardware.security.keymint.KeyParameter;
import android.p008os.Build;
import android.p008os.RemoteException;
import android.security.GenerateRkpKey;
import android.security.KeyPairGeneratorSpec;
import android.security.KeyStore2;
import android.security.KeyStoreException;
import android.security.KeyStoreSecurityLevel;
import android.security.keymaster.KeymasterArguments;
import android.security.keystore.ArrayUtils;
import android.security.keystore.DeviceIdAttestationException;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.SecureKeyImportUnavailableException;
import android.security.keystore.StrongBoxUnavailableException;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyEntryResponse;
import android.system.keystore2.KeyMetadata;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public abstract class AndroidKeyStoreKeyPairGeneratorSpi extends KeyPairGeneratorSpi {
    private static final String CURVE_ED_25519;
    private static final String CURVE_X_25519;
    private static final int EC_DEFAULT_KEY_SIZE = 256;
    private static final int RSA_DEFAULT_KEY_SIZE = 2048;
    private static final int RSA_MAX_KEY_SIZE = 8192;
    private static final int RSA_MIN_KEY_SIZE = 512;
    private static final List<String> SUPPORTED_EC_CURVE_NAMES;
    private static final Map<String, Integer> SUPPORTED_EC_CURVE_NAME_TO_SIZE;
    private static final List<Integer> SUPPORTED_EC_CURVE_SIZES;
    private static final String TAG = "AndroidKeyStoreKeyPairGeneratorSpi";
    private KeyDescriptor mAttestKeyDescriptor;
    private String mEcCurveName;
    private String mEntryAlias;
    private int mEntryNamespace;
    private String mJcaKeyAlgorithm;
    private int mKeySizeBits;
    private KeyStore2 mKeyStore;
    private int mKeymasterAlgorithm = -1;
    private int[] mKeymasterBlockModes;
    private int[] mKeymasterDigests;
    private int[] mKeymasterEncryptionPaddings;
    private int[] mKeymasterPurposes;
    private int[] mKeymasterSignaturePaddings;
    private final int mOriginalKeymasterAlgorithm;
    private Long mRSAPublicExponent;
    private SecureRandom mRng;
    private KeyGenParameterSpec mSpec;

    /* loaded from: classes3.dex */
    public static class RSA extends AndroidKeyStoreKeyPairGeneratorSpi {
        public RSA() {
            super(1);
        }
    }

    /* renamed from: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$EC */
    /* loaded from: classes3.dex */
    public static class C2411EC extends AndroidKeyStoreKeyPairGeneratorSpi {
        public C2411EC() {
            super(3);
        }
    }

    /* loaded from: classes3.dex */
    public static class XDH extends AndroidKeyStoreKeyPairGeneratorSpi {
        public XDH() {
            super(3);
        }
    }

    static {
        HashMap hashMap = new HashMap();
        SUPPORTED_EC_CURVE_NAME_TO_SIZE = hashMap;
        ArrayList arrayList = new ArrayList();
        SUPPORTED_EC_CURVE_NAMES = arrayList;
        ArrayList arrayList2 = new ArrayList();
        SUPPORTED_EC_CURVE_SIZES = arrayList2;
        String name = NamedParameterSpec.X25519.getName();
        CURVE_X_25519 = name;
        String name2 = NamedParameterSpec.ED25519.getName();
        CURVE_ED_25519 = name2;
        hashMap.put("p-224", 224);
        hashMap.put("secp224r1", 224);
        hashMap.put("p-256", 256);
        hashMap.put("secp256r1", 256);
        hashMap.put("prime256v1", 256);
        hashMap.put(name.toLowerCase(Locale.US), 256);
        hashMap.put(name2.toLowerCase(Locale.US), 256);
        hashMap.put("p-384", 384);
        hashMap.put("secp384r1", 384);
        hashMap.put("p-521", 521);
        hashMap.put("secp521r1", 521);
        arrayList.addAll(hashMap.keySet());
        Collections.sort(arrayList);
        arrayList2.addAll(new HashSet(hashMap.values()));
        Collections.sort(arrayList2);
    }

    protected AndroidKeyStoreKeyPairGeneratorSpi(int keymasterAlgorithm) {
        this.mOriginalKeymasterAlgorithm = keymasterAlgorithm;
    }

    private static int keySizeAndNameToEcCurve(int keySizeBits, String ecCurveName) throws InvalidAlgorithmParameterException {
        switch (keySizeBits) {
            case 224:
                return 0;
            case 256:
                if (isCurve25519(ecCurveName)) {
                    return 4;
                }
                return 1;
            case 384:
                return 2;
            case 521:
                return 3;
            default:
                throw new InvalidAlgorithmParameterException("Unsupported EC curve keysize: " + keySizeBits);
        }
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(int keysize, SecureRandom random) {
        throw new IllegalArgumentException(KeyGenParameterSpec.class.getName() + " or " + KeyPairGeneratorSpec.class.getName() + " required to initialize this KeyPairGenerator");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        KeyGenParameterSpec spec;
        int[] iArr;
        resetAll();
        try {
            if (params == null) {
                throw new InvalidAlgorithmParameterException("Must supply params of type " + KeyGenParameterSpec.class.getName() + " or " + KeyPairGeneratorSpec.class.getName());
            }
            int keymasterAlgorithm = this.mOriginalKeymasterAlgorithm;
            if (params instanceof KeyGenParameterSpec) {
                spec = (KeyGenParameterSpec) params;
            } else if (params instanceof KeyPairGeneratorSpec) {
                KeyPairGeneratorSpec legacySpec = (KeyPairGeneratorSpec) params;
                try {
                    keymasterAlgorithm = getKeymasterAlgorithmFromLegacy(keymasterAlgorithm, legacySpec);
                    KeyGenParameterSpec spec2 = buildKeyGenParameterSpecFromLegacy(legacySpec, keymasterAlgorithm);
                    spec = spec2;
                } catch (IllegalArgumentException | NullPointerException e) {
                    throw new InvalidAlgorithmParameterException(e);
                }
            } else if (params instanceof NamedParameterSpec) {
                NamedParameterSpec namedSpec = (NamedParameterSpec) params;
                if (!namedSpec.getName().equalsIgnoreCase(NamedParameterSpec.X25519.getName()) && !namedSpec.getName().equalsIgnoreCase(NamedParameterSpec.ED25519.getName())) {
                    throw new InvalidAlgorithmParameterException("Unsupported algorithm specified via NamedParameterSpec: " + namedSpec.getName());
                }
                throw new IllegalArgumentException("This KeyPairGenerator cannot be initialized using NamedParameterSpec. use " + KeyGenParameterSpec.class.getName() + " or " + KeyPairGeneratorSpec.class.getName());
            } else {
                throw new InvalidAlgorithmParameterException("Unsupported params class: " + params.getClass().getName() + ". Supported: " + KeyGenParameterSpec.class.getName() + ", " + KeyPairGeneratorSpec.class.getName());
            }
            this.mEntryAlias = spec.getKeystoreAlias();
            this.mEntryNamespace = spec.getNamespace();
            this.mSpec = spec;
            this.mKeymasterAlgorithm = keymasterAlgorithm;
            this.mKeySizeBits = spec.getKeySize();
            initAlgorithmSpecificParameters();
            if (this.mKeySizeBits == -1) {
                this.mKeySizeBits = getDefaultKeySize(keymasterAlgorithm);
            }
            checkValidKeySize(keymasterAlgorithm, this.mKeySizeBits, this.mSpec.isStrongBoxBacked(), this.mEcCurveName);
            if (spec.getKeystoreAlias() == null) {
                throw new InvalidAlgorithmParameterException("KeyStore entry alias not provided");
            }
            try {
                String jcaKeyAlgorithm = KeyProperties.KeyAlgorithm.fromKeymasterAsymmetricKeyAlgorithm(keymasterAlgorithm);
                this.mKeymasterPurposes = KeyProperties.Purpose.allToKeymaster(spec.getPurposes());
                this.mKeymasterBlockModes = KeyProperties.BlockMode.allToKeymaster(spec.getBlockModes());
                this.mKeymasterEncryptionPaddings = KeyProperties.EncryptionPadding.allToKeymaster(spec.getEncryptionPaddings());
                if ((spec.getPurposes() & 1) != 0 && spec.isRandomizedEncryptionRequired()) {
                    for (int keymasterPadding : this.mKeymasterEncryptionPaddings) {
                        if (!KeymasterUtils.isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(keymasterPadding)) {
                            throw new InvalidAlgorithmParameterException("Randomized encryption (IND-CPA) required but may be violated by padding scheme: " + KeyProperties.EncryptionPadding.fromKeymaster(keymasterPadding) + ". See " + KeyGenParameterSpec.class.getName() + " documentation.");
                        }
                    }
                }
                this.mKeymasterSignaturePaddings = KeyProperties.SignaturePadding.allToKeymaster(spec.getSignaturePaddings());
                if (spec.isDigestsSpecified()) {
                    this.mKeymasterDigests = KeyProperties.Digest.allToKeymaster(spec.getDigests());
                } else {
                    this.mKeymasterDigests = EmptyArray.INT;
                }
                KeyStore2ParameterUtils.addUserAuthArgs(new ArrayList(), this.mSpec);
                this.mJcaKeyAlgorithm = jcaKeyAlgorithm;
                this.mRng = random;
                this.mKeyStore = KeyStore2.getInstance();
                this.mAttestKeyDescriptor = buildAndCheckAttestKeyDescriptor(spec);
                checkAttestKeyPurpose(spec);
                checkCorrectKeyPurposeForCurve(spec);
                if (1 == 0) {
                    resetAll();
                }
            } catch (IllegalArgumentException | IllegalStateException e2) {
                throw new InvalidAlgorithmParameterException(e2);
            }
        } catch (Throwable th) {
            if (0 == 0) {
                resetAll();
            }
            throw th;
        }
    }

    private void checkAttestKeyPurpose(KeyGenParameterSpec spec) throws InvalidAlgorithmParameterException {
        if ((spec.getPurposes() & 128) != 0 && spec.getPurposes() != 128) {
            throw new InvalidAlgorithmParameterException("PURPOSE_ATTEST_KEY may not be specified with any other purposes");
        }
    }

    private void checkCorrectKeyPurposeForCurve(KeyGenParameterSpec spec) throws InvalidAlgorithmParameterException {
        if (!isCurve25519(this.mEcCurveName)) {
            return;
        }
        if (this.mEcCurveName.equalsIgnoreCase(CURVE_X_25519) && spec.getPurposes() != 64) {
            throw new InvalidAlgorithmParameterException("x25519 may only be used for key agreement.");
        }
        if (this.mEcCurveName.equalsIgnoreCase(CURVE_ED_25519) && !hasOnlyAllowedPurposeForEd25519(spec.getPurposes())) {
            throw new InvalidAlgorithmParameterException("ed25519 may not be used for key agreement.");
        }
    }

    private static boolean isCurve25519(String ecCurveName) {
        if (ecCurveName == null) {
            return false;
        }
        return ecCurveName.equalsIgnoreCase(CURVE_X_25519) || ecCurveName.equalsIgnoreCase(CURVE_ED_25519);
    }

    private static boolean hasOnlyAllowedPurposeForEd25519(int purpose) {
        boolean hasAllowedPurpose = (purpose & 140) != 0;
        boolean hasDisallowedPurpose = (purpose & (-141)) != 0;
        return hasAllowedPurpose && !hasDisallowedPurpose;
    }

    private KeyDescriptor buildAndCheckAttestKeyDescriptor(KeyGenParameterSpec spec) throws InvalidAlgorithmParameterException {
        if (spec.getAttestKeyAlias() != null) {
            KeyDescriptor attestKeyDescriptor = new KeyDescriptor();
            attestKeyDescriptor.domain = 0;
            attestKeyDescriptor.alias = spec.getAttestKeyAlias();
            try {
                KeyEntryResponse attestKey = this.mKeyStore.getKeyEntry(attestKeyDescriptor);
                checkAttestKeyChallenge(spec);
                checkAttestKeyPurpose(attestKey.metadata.authorizations);
                checkAttestKeySecurityLevel(spec, attestKey);
                return attestKeyDescriptor;
            } catch (KeyStoreException e) {
                throw new InvalidAlgorithmParameterException("Invalid attestKeyAlias", e);
            }
        }
        return null;
    }

    private void checkAttestKeyChallenge(KeyGenParameterSpec spec) throws InvalidAlgorithmParameterException {
        if (spec.getAttestationChallenge() == null) {
            throw new InvalidAlgorithmParameterException("AttestKey specified but no attestation challenge provided");
        }
    }

    private void checkAttestKeyPurpose(Authorization[] keyAuths) throws InvalidAlgorithmParameterException {
        Predicate<Authorization> isAttestKeyPurpose = new Predicate() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AndroidKeyStoreKeyPairGeneratorSpi.lambda$checkAttestKeyPurpose$0((Authorization) obj);
            }
        };
        if (Arrays.stream(keyAuths).noneMatch(isAttestKeyPurpose)) {
            throw new InvalidAlgorithmParameterException("Invalid attestKey, does not have PURPOSE_ATTEST_KEY");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$checkAttestKeyPurpose$0(Authorization x) {
        return x.keyParameter.tag == 536870913 && x.keyParameter.value.getKeyPurpose() == 7;
    }

    private void checkAttestKeySecurityLevel(KeyGenParameterSpec spec, KeyEntryResponse key) throws InvalidAlgorithmParameterException {
        boolean attestKeyInStrongBox = key.metadata.keySecurityLevel == 2;
        if (spec.isStrongBoxBacked() != attestKeyInStrongBox) {
            if (attestKeyInStrongBox) {
                throw new InvalidAlgorithmParameterException("Invalid security level: Cannot sign non-StrongBox key with StrongBox attestKey");
            }
            throw new InvalidAlgorithmParameterException("Invalid security level: Cannot sign StrongBox key with non-StrongBox attestKey");
        }
    }

    private int getKeymasterAlgorithmFromLegacy(int keymasterAlgorithm, KeyPairGeneratorSpec legacySpec) throws InvalidAlgorithmParameterException {
        String specKeyAlgorithm = legacySpec.getKeyType();
        if (specKeyAlgorithm != null) {
            try {
                int keymasterAlgorithm2 = KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm(specKeyAlgorithm);
                return keymasterAlgorithm2;
            } catch (IllegalArgumentException e) {
                throw new InvalidAlgorithmParameterException("Invalid key type in parameters", e);
            }
        }
        return keymasterAlgorithm;
    }

    private KeyGenParameterSpec buildKeyGenParameterSpecFromLegacy(KeyPairGeneratorSpec legacySpec, int keymasterAlgorithm) {
        KeyGenParameterSpec.Builder specBuilder;
        switch (keymasterAlgorithm) {
            case 1:
                specBuilder = new KeyGenParameterSpec.Builder(legacySpec.getKeystoreAlias(), 15);
                specBuilder.setDigests(KeyProperties.DIGEST_NONE, KeyProperties.DIGEST_MD5, "SHA-1", KeyProperties.DIGEST_SHA224, "SHA-256", KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
                specBuilder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE, KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1, KeyProperties.ENCRYPTION_PADDING_RSA_OAEP);
                specBuilder.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1, KeyProperties.SIGNATURE_PADDING_RSA_PSS);
                specBuilder.setRandomizedEncryptionRequired(false);
                break;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
            case 3:
                specBuilder = new KeyGenParameterSpec.Builder(legacySpec.getKeystoreAlias(), 12);
                specBuilder.setDigests(KeyProperties.DIGEST_NONE, "SHA-1", KeyProperties.DIGEST_SHA224, "SHA-256", KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
                break;
        }
        if (legacySpec.getKeySize() != -1) {
            specBuilder.setKeySize(legacySpec.getKeySize());
        }
        if (legacySpec.getAlgorithmParameterSpec() != null) {
            specBuilder.setAlgorithmParameterSpec(legacySpec.getAlgorithmParameterSpec());
        }
        specBuilder.setCertificateSubject(legacySpec.getSubjectDN());
        specBuilder.setCertificateSerialNumber(legacySpec.getSerialNumber());
        specBuilder.setCertificateNotBefore(legacySpec.getStartDate());
        specBuilder.setCertificateNotAfter(legacySpec.getEndDate());
        specBuilder.setUserAuthenticationRequired(false);
        return specBuilder.build();
    }

    private void resetAll() {
        this.mEntryAlias = null;
        this.mEntryNamespace = -1;
        this.mJcaKeyAlgorithm = null;
        this.mKeymasterAlgorithm = -1;
        this.mKeymasterPurposes = null;
        this.mKeymasterBlockModes = null;
        this.mKeymasterEncryptionPaddings = null;
        this.mKeymasterSignaturePaddings = null;
        this.mKeymasterDigests = null;
        this.mKeySizeBits = 0;
        this.mSpec = null;
        this.mRSAPublicExponent = null;
        this.mRng = null;
        this.mKeyStore = null;
        this.mEcCurveName = null;
    }

    private void initAlgorithmSpecificParameters() throws InvalidAlgorithmParameterException {
        AlgorithmParameterSpec algSpecificSpec = this.mSpec.getAlgorithmParameterSpec();
        switch (this.mKeymasterAlgorithm) {
            case 1:
                BigInteger publicExponent = null;
                if (algSpecificSpec instanceof RSAKeyGenParameterSpec) {
                    RSAKeyGenParameterSpec rsaSpec = (RSAKeyGenParameterSpec) algSpecificSpec;
                    int i = this.mKeySizeBits;
                    if (i == -1) {
                        this.mKeySizeBits = rsaSpec.getKeysize();
                    } else if (i != rsaSpec.getKeysize()) {
                        throw new InvalidAlgorithmParameterException("RSA key size must match  between " + this.mSpec + " and " + algSpecificSpec + ": " + this.mKeySizeBits + " vs " + rsaSpec.getKeysize());
                    }
                    publicExponent = rsaSpec.getPublicExponent();
                } else if (algSpecificSpec != null) {
                    throw new InvalidAlgorithmParameterException("RSA may only use RSAKeyGenParameterSpec");
                }
                if (publicExponent == null) {
                    publicExponent = RSAKeyGenParameterSpec.F4;
                }
                if (publicExponent.compareTo(BigInteger.ZERO) < 1) {
                    throw new InvalidAlgorithmParameterException("RSA public exponent must be positive: " + publicExponent);
                }
                if (publicExponent.signum() == -1 || publicExponent.compareTo(KeymasterArguments.UINT64_MAX_VALUE) > 0) {
                    throw new InvalidAlgorithmParameterException("Unsupported RSA public exponent: " + publicExponent + ". Maximum supported value: " + KeymasterArguments.UINT64_MAX_VALUE);
                }
                this.mRSAPublicExponent = Long.valueOf(publicExponent.longValue());
                return;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
            case 3:
                if (algSpecificSpec instanceof ECGenParameterSpec) {
                    ECGenParameterSpec ecSpec = (ECGenParameterSpec) algSpecificSpec;
                    String name = ecSpec.getName();
                    this.mEcCurveName = name;
                    Integer ecSpecKeySizeBits = SUPPORTED_EC_CURVE_NAME_TO_SIZE.get(name.toLowerCase(Locale.US));
                    if (ecSpecKeySizeBits == null) {
                        throw new InvalidAlgorithmParameterException("Unsupported EC curve name: " + this.mEcCurveName + ". Supported: " + SUPPORTED_EC_CURVE_NAMES);
                    }
                    int i2 = this.mKeySizeBits;
                    if (i2 == -1) {
                        this.mKeySizeBits = ecSpecKeySizeBits.intValue();
                        return;
                    } else if (i2 != ecSpecKeySizeBits.intValue()) {
                        throw new InvalidAlgorithmParameterException("EC key size must match  between " + this.mSpec + " and " + algSpecificSpec + ": " + this.mKeySizeBits + " vs " + ecSpecKeySizeBits);
                    } else {
                        return;
                    }
                } else if (algSpecificSpec != null) {
                    throw new InvalidAlgorithmParameterException("EC may only use ECGenParameterSpec");
                } else {
                    return;
                }
        }
    }

    @Override // java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        GenerateKeyPairHelperResult result = new GenerateKeyPairHelperResult(0, null);
        for (int i = 0; i < 2; i++) {
            result = generateKeyPairHelper();
            if (result.rkpStatus == 0 && result.keyPair != null) {
                return result.keyPair;
            }
        }
        int i2 = result.rkpStatus;
        if (i2 != 0) {
            KeyStoreException ksException = new KeyStoreException(22, "Could not get RKP keys", result.rkpStatus);
            throw new ProviderException("Failed to provision new attestation keys.", ksException);
        }
        return result.keyPair;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class GenerateKeyPairHelperResult {
        public final KeyPair keyPair;
        public final int rkpStatus;

        private GenerateKeyPairHelperResult(int rkpStatus, KeyPair keyPair) {
            this.rkpStatus = rkpStatus;
            this.keyPair = keyPair;
        }
    }

    private GenerateKeyPairHelperResult generateKeyPairHelper() {
        KeyGenParameterSpec keyGenParameterSpec;
        if (this.mKeyStore == null || (keyGenParameterSpec = this.mSpec) == null) {
            throw new IllegalStateException("Not initialized");
        }
        int securityLevel = keyGenParameterSpec.isStrongBoxBacked() ? 2 : 1;
        int flags = this.mSpec.isCriticalToDeviceEncryption() ? 1 : 0;
        byte[] additionalEntropy = KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.mRng, (this.mKeySizeBits + 7) / 8);
        KeyDescriptor descriptor = new KeyDescriptor();
        descriptor.alias = this.mEntryAlias;
        descriptor.domain = this.mEntryNamespace == -1 ? 0 : 2;
        descriptor.nspace = this.mEntryNamespace;
        descriptor.blob = null;
        boolean success = false;
        try {
            try {
                try {
                    KeyStoreSecurityLevel iSecurityLevel = this.mKeyStore.getSecurityLevel(securityLevel);
                    KeyMetadata metadata = iSecurityLevel.generateKey(descriptor, this.mAttestKeyDescriptor, constructKeyGenerationArguments(), flags, additionalEntropy);
                    AndroidKeyStorePublicKey publicKey = AndroidKeyStoreProvider.makeAndroidKeyStorePublicKeyFromKeyEntryResponse(descriptor, metadata, iSecurityLevel, this.mKeymasterAlgorithm);
                    GenerateRkpKey keyGen = new GenerateRkpKey(ActivityThread.currentApplication());
                    try {
                        if (this.mSpec.getAttestationChallenge() != null) {
                            keyGen.notifyKeyGenerated(securityLevel);
                        }
                    } catch (RemoteException e) {
                        Log.m111d(TAG, "Couldn't connect to the RemoteProvisioner backend.", e);
                    }
                    success = true;
                    KeyPair kp = new KeyPair(publicKey, publicKey.getPrivateKey());
                    GenerateKeyPairHelperResult generateKeyPairHelperResult = new GenerateKeyPairHelperResult(0, kp);
                    if (1 == 0) {
                        try {
                            this.mKeyStore.deleteKey(descriptor);
                        } catch (KeyStoreException e2) {
                            if (e2.getErrorCode() != 7) {
                                Log.m109e(TAG, "Failed to delete newly generated key after generation failed unexpectedly.", e2);
                            }
                        }
                    }
                    return generateKeyPairHelperResult;
                } catch (DeviceIdAttestationException | IllegalArgumentException | InvalidAlgorithmParameterException | UnrecoverableKeyException e3) {
                    throw new ProviderException("Failed to construct key object from newly generated key pair.", e3);
                }
            } catch (KeyStoreException e4) {
                switch (e4.getErrorCode()) {
                    case -68:
                        throw new StrongBoxUnavailableException("Failed to generated key pair.", e4);
                    case 22:
                        GenerateKeyPairHelperResult checkIfRetryableOrThrow = checkIfRetryableOrThrow(e4, securityLevel);
                        if (!success) {
                            try {
                                this.mKeyStore.deleteKey(descriptor);
                            } catch (KeyStoreException e5) {
                                if (e5.getErrorCode() != 7) {
                                    Log.m109e(TAG, "Failed to delete newly generated key after generation failed unexpectedly.", e5);
                                }
                            }
                        }
                        return checkIfRetryableOrThrow;
                    default:
                        ProviderException p = new ProviderException("Failed to generate key pair.", e4);
                        if ((this.mSpec.getPurposes() & 32) != 0) {
                            throw new SecureKeyImportUnavailableException(p);
                        }
                        throw p;
                }
            }
        } catch (Throwable e6) {
            if (!success) {
                try {
                    this.mKeyStore.deleteKey(descriptor);
                } catch (KeyStoreException e7) {
                    if (e7.getErrorCode() != 7) {
                        Log.m109e(TAG, "Failed to delete newly generated key after generation failed unexpectedly.", e7);
                    }
                }
            }
            throw e6;
        }
    }

    GenerateKeyPairHelperResult checkIfRetryableOrThrow(KeyStoreException e, int securityLevel) {
        KeyStoreException ksException;
        int rkpStatus;
        GenerateRkpKey keyGen = new GenerateRkpKey(ActivityThread.currentApplication());
        try {
            int keyGenStatus = keyGen.notifyEmpty(securityLevel);
            switch (keyGenStatus) {
                case 0:
                    return new GenerateKeyPairHelperResult(1, null);
                case 1:
                    rkpStatus = 3;
                    break;
                case 2:
                case 3:
                default:
                    rkpStatus = 1;
                    break;
                case 4:
                    rkpStatus = 2;
                    break;
            }
            ksException = new KeyStoreException(22, "Out of RKP keys due to IGenerateRkpKeyService status: " + keyGenStatus, rkpStatus);
        } catch (RemoteException f) {
            ksException = new KeyStoreException(22, "Remote exception: " + f.getMessage(), 1);
        }
        ksException.initCause(e);
        throw new ProviderException("Failed to provision new attestation keys.", ksException);
    }

    private void addAttestationParameters(List<KeyParameter> params) throws ProviderException, IllegalArgumentException, DeviceIdAttestationException {
        byte[] challenge = this.mSpec.getAttestationChallenge();
        if (challenge != null) {
            params.add(KeyStore2ParameterUtils.makeBytes(-1879047484, challenge));
            if (this.mSpec.isDevicePropertiesAttestationIncluded()) {
                String platformReportedBrand = isPropertyEmptyOrUnknown(Build.BRAND_FOR_ATTESTATION) ? Build.BRAND : Build.BRAND_FOR_ATTESTATION;
                params.add(KeyStore2ParameterUtils.makeBytes(-1879047482, platformReportedBrand.getBytes(StandardCharsets.UTF_8)));
                params.add(KeyStore2ParameterUtils.makeBytes(-1879047481, Build.DEVICE.getBytes(StandardCharsets.UTF_8)));
                String platformReportedProduct = isPropertyEmptyOrUnknown(Build.PRODUCT_FOR_ATTESTATION) ? Build.PRODUCT : Build.PRODUCT_FOR_ATTESTATION;
                params.add(KeyStore2ParameterUtils.makeBytes(-1879047480, platformReportedProduct.getBytes(StandardCharsets.UTF_8)));
                params.add(KeyStore2ParameterUtils.makeBytes(-1879047476, Build.MANUFACTURER.getBytes(StandardCharsets.UTF_8)));
                String platformReportedModel = isPropertyEmptyOrUnknown(Build.MODEL_FOR_ATTESTATION) ? Build.MODEL : Build.MODEL_FOR_ATTESTATION;
                params.add(KeyStore2ParameterUtils.makeBytes(-1879047475, platformReportedModel.getBytes(StandardCharsets.UTF_8)));
            }
            int[] idTypes = this.mSpec.getAttestationIds();
            if (idTypes.length == 0) {
                return;
            }
            Set<Integer> idTypesSet = new ArraySet<>(idTypes.length);
            for (int idType : idTypes) {
                idTypesSet.add(Integer.valueOf(idType));
            }
            TelephonyManager telephonyService = null;
            if ((idTypesSet.contains(2) || idTypesSet.contains(3)) && (telephonyService = (TelephonyManager) AppGlobals.getInitialApplication().getSystemService("phone")) == null) {
                throw new DeviceIdAttestationException("Unable to access telephony service");
            }
            for (Integer idType2 : idTypesSet) {
                switch (idType2.intValue()) {
                    case 1:
                        params.add(KeyStore2ParameterUtils.makeBytes(-1879047479, Build.getSerial().getBytes(StandardCharsets.UTF_8)));
                        break;
                    case 2:
                        String imei = telephonyService.getImei(0);
                        if (imei == null) {
                            throw new DeviceIdAttestationException("Unable to retrieve IMEI");
                        }
                        params.add(KeyStore2ParameterUtils.makeBytes(-1879047478, imei.getBytes(StandardCharsets.UTF_8)));
                        String secondImei = telephonyService.getImei(1);
                        if (TextUtils.isEmpty(secondImei)) {
                            break;
                        } else {
                            params.add(KeyStore2ParameterUtils.makeBytes(-1879047469, secondImei.getBytes(StandardCharsets.UTF_8)));
                            break;
                        }
                    case 3:
                        String meid = telephonyService.getMeid(0);
                        if (meid == null) {
                            throw new DeviceIdAttestationException("Unable to retrieve MEID");
                        }
                        params.add(KeyStore2ParameterUtils.makeBytes(-1879047477, meid.getBytes(StandardCharsets.UTF_8)));
                        break;
                    case 4:
                        params.add(KeyStore2ParameterUtils.makeBool(1879048912));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown device ID type " + idType2);
                }
            }
        }
    }

    private Collection<KeyParameter> constructKeyGenerationArguments() throws DeviceIdAttestationException, IllegalArgumentException, InvalidAlgorithmParameterException {
        final List<KeyParameter> params = new ArrayList<>();
        params.add(KeyStore2ParameterUtils.makeInt(805306371, this.mKeySizeBits));
        params.add(KeyStore2ParameterUtils.makeEnum(268435458, this.mKeymasterAlgorithm));
        if (this.mKeymasterAlgorithm == 3) {
            params.add(KeyStore2ParameterUtils.makeEnum(268435466, keySizeAndNameToEcCurve(this.mKeySizeBits, this.mEcCurveName)));
        }
        ArrayUtils.forEach(this.mKeymasterPurposes, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                params.add(KeyStore2ParameterUtils.makeEnum(536870913, ((Integer) obj).intValue()));
            }
        });
        ArrayUtils.forEach(this.mKeymasterBlockModes, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                params.add(KeyStore2ParameterUtils.makeEnum(536870916, ((Integer) obj).intValue()));
            }
        });
        ArrayUtils.forEach(this.mKeymasterEncryptionPaddings, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidKeyStoreKeyPairGeneratorSpi.this.lambda$constructKeyGenerationArguments$4(params, (Integer) obj);
            }
        });
        ArrayUtils.forEach(this.mKeymasterSignaturePaddings, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                params.add(KeyStore2ParameterUtils.makeEnum(536870918, ((Integer) obj).intValue()));
            }
        });
        ArrayUtils.forEach(this.mKeymasterDigests, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                params.add(KeyStore2ParameterUtils.makeEnum(536870917, ((Integer) obj).intValue()));
            }
        });
        KeyStore2ParameterUtils.addUserAuthArgs(params, this.mSpec);
        if (this.mSpec.getKeyValidityStart() != null) {
            params.add(KeyStore2ParameterUtils.makeDate(1610613136, this.mSpec.getKeyValidityStart()));
        }
        if (this.mSpec.getKeyValidityForOriginationEnd() != null) {
            params.add(KeyStore2ParameterUtils.makeDate(1610613137, this.mSpec.getKeyValidityForOriginationEnd()));
        }
        if (this.mSpec.getKeyValidityForConsumptionEnd() != null) {
            params.add(KeyStore2ParameterUtils.makeDate(1610613138, this.mSpec.getKeyValidityForConsumptionEnd()));
        }
        if (this.mSpec.getCertificateNotAfter() != null) {
            params.add(KeyStore2ParameterUtils.makeDate(1610613745, this.mSpec.getCertificateNotAfter()));
        }
        if (this.mSpec.getCertificateNotBefore() != null) {
            params.add(KeyStore2ParameterUtils.makeDate(1610613744, this.mSpec.getCertificateNotBefore()));
        }
        if (this.mSpec.getCertificateSerialNumber() != null) {
            params.add(KeyStore2ParameterUtils.makeBignum(-2147482642, this.mSpec.getCertificateSerialNumber()));
        }
        if (this.mSpec.getCertificateSubject() != null) {
            params.add(KeyStore2ParameterUtils.makeBytes(-1879047185, this.mSpec.getCertificateSubject().getEncoded()));
        }
        if (this.mSpec.getMaxUsageCount() != -1) {
            params.add(KeyStore2ParameterUtils.makeInt(805306773, this.mSpec.getMaxUsageCount()));
        }
        addAlgorithmSpecificParameters(params);
        if (this.mSpec.isUniqueIdIncluded()) {
            params.add(KeyStore2ParameterUtils.makeBool(1879048394));
        }
        addAttestationParameters(params);
        return params;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$constructKeyGenerationArguments$4(final List params, Integer padding) {
        params.add(KeyStore2ParameterUtils.makeEnum(536870918, padding.intValue()));
        if (padding.intValue() == 2) {
            final boolean[] hasDefaultMgf1DigestBeenAdded = {false};
            ArrayUtils.forEach(this.mKeymasterDigests, new Consumer() { // from class: android.security.keystore2.AndroidKeyStoreKeyPairGeneratorSpi$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AndroidKeyStoreKeyPairGeneratorSpi.lambda$constructKeyGenerationArguments$3(params, hasDefaultMgf1DigestBeenAdded, (Integer) obj);
                }
            });
            if (!hasDefaultMgf1DigestBeenAdded[0]) {
                params.add(KeyStore2ParameterUtils.makeEnum(536871115, KeyProperties.Digest.toKeymaster("SHA-1")));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$constructKeyGenerationArguments$3(List params, boolean[] hasDefaultMgf1DigestBeenAdded, Integer digest) {
        params.add(KeyStore2ParameterUtils.makeEnum(536871115, digest.intValue()));
        hasDefaultMgf1DigestBeenAdded[0] = hasDefaultMgf1DigestBeenAdded[0] | digest.equals(Integer.valueOf(KeyProperties.Digest.toKeymaster("SHA-1")));
    }

    private void addAlgorithmSpecificParameters(List<KeyParameter> params) {
        switch (this.mKeymasterAlgorithm) {
            case 1:
                params.add(KeyStore2ParameterUtils.makeLong(1342177480, this.mRSAPublicExponent.longValue()));
                return;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + this.mKeymasterAlgorithm);
            case 3:
                return;
        }
    }

    private static int getDefaultKeySize(int keymasterAlgorithm) {
        switch (keymasterAlgorithm) {
            case 1:
                return 2048;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + keymasterAlgorithm);
            case 3:
                return 256;
        }
    }

    private static void checkValidKeySize(int keymasterAlgorithm, int keySize, boolean isStrongBoxBacked, String mEcCurveName) throws InvalidAlgorithmParameterException {
        switch (keymasterAlgorithm) {
            case 1:
                if (keySize < 512 || keySize > 8192) {
                    throw new InvalidAlgorithmParameterException("RSA key size must be >= 512 and <= 8192");
                }
                return;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + keymasterAlgorithm);
            case 3:
                if (isStrongBoxBacked && keySize != 256) {
                    throw new InvalidAlgorithmParameterException("Unsupported StrongBox EC key size: " + keySize + " bits. Supported: 256");
                }
                if (isStrongBoxBacked && isCurve25519(mEcCurveName)) {
                    throw new InvalidAlgorithmParameterException("Unsupported StrongBox EC: " + mEcCurveName);
                }
                List<Integer> list = SUPPORTED_EC_CURVE_SIZES;
                if (!list.contains(Integer.valueOf(keySize))) {
                    throw new InvalidAlgorithmParameterException("Unsupported EC key size: " + keySize + " bits. Supported: " + list);
                }
                return;
        }
    }

    private static String getCertificateSignatureAlgorithm(int keymasterAlgorithm, int keySizeBits, KeyGenParameterSpec spec) {
        if ((spec.getPurposes() & 4) == 0 || spec.isUserAuthenticationRequired() || !spec.isDigestsSpecified()) {
            return null;
        }
        switch (keymasterAlgorithm) {
            case 1:
                boolean pkcs1SignaturePaddingSupported = com.android.internal.util.ArrayUtils.contains(KeyProperties.SignaturePadding.allToKeymaster(spec.getSignaturePaddings()), 5);
                if (pkcs1SignaturePaddingSupported) {
                    Set<Integer> availableKeymasterDigests = getAvailableKeymasterSignatureDigests(spec.getDigests(), AndroidKeyStoreBCWorkaroundProvider.getSupportedEcdsaSignatureDigests());
                    int maxDigestOutputSizeBits = keySizeBits - 240;
                    int bestKeymasterDigest = -1;
                    int bestDigestOutputSizeBits = -1;
                    for (Integer num : availableKeymasterDigests) {
                        int keymasterDigest = num.intValue();
                        int outputSizeBits = KeymasterUtils.getDigestOutputSizeBits(keymasterDigest);
                        if (outputSizeBits <= maxDigestOutputSizeBits) {
                            if (bestKeymasterDigest == -1) {
                                bestKeymasterDigest = keymasterDigest;
                                bestDigestOutputSizeBits = outputSizeBits;
                            } else if (outputSizeBits > bestDigestOutputSizeBits) {
                                bestKeymasterDigest = keymasterDigest;
                                bestDigestOutputSizeBits = outputSizeBits;
                            }
                        }
                    }
                    if (bestKeymasterDigest == -1) {
                        return null;
                    }
                    return KeyProperties.Digest.fromKeymasterToSignatureAlgorithmDigest(bestKeymasterDigest) + "WithRSA";
                }
                return null;
            case 2:
            default:
                throw new ProviderException("Unsupported algorithm: " + keymasterAlgorithm);
            case 3:
                Set<Integer> availableKeymasterDigests2 = getAvailableKeymasterSignatureDigests(spec.getDigests(), AndroidKeyStoreBCWorkaroundProvider.getSupportedEcdsaSignatureDigests());
                int bestKeymasterDigest2 = -1;
                int bestDigestOutputSizeBits2 = -1;
                Iterator<Integer> it = availableKeymasterDigests2.iterator();
                while (true) {
                    if (it.hasNext()) {
                        int keymasterDigest2 = it.next().intValue();
                        int outputSizeBits2 = KeymasterUtils.getDigestOutputSizeBits(keymasterDigest2);
                        if (outputSizeBits2 == keySizeBits) {
                            bestKeymasterDigest2 = keymasterDigest2;
                        } else if (bestKeymasterDigest2 == -1) {
                            bestKeymasterDigest2 = keymasterDigest2;
                            bestDigestOutputSizeBits2 = outputSizeBits2;
                        } else if (bestDigestOutputSizeBits2 < keySizeBits) {
                            if (outputSizeBits2 > bestDigestOutputSizeBits2) {
                                bestKeymasterDigest2 = keymasterDigest2;
                                bestDigestOutputSizeBits2 = outputSizeBits2;
                            }
                        } else if (outputSizeBits2 < bestDigestOutputSizeBits2 && outputSizeBits2 >= keySizeBits) {
                            bestKeymasterDigest2 = keymasterDigest2;
                            bestDigestOutputSizeBits2 = outputSizeBits2;
                        }
                    }
                }
                if (bestKeymasterDigest2 == -1) {
                    return null;
                }
                return KeyProperties.Digest.fromKeymasterToSignatureAlgorithmDigest(bestKeymasterDigest2) + "WithECDSA";
        }
    }

    private static Set<Integer> getAvailableKeymasterSignatureDigests(String[] authorizedKeyDigests, String[] supportedSignatureDigests) {
        int[] allToKeymaster;
        int[] allToKeymaster2;
        Set<Integer> authorizedKeymasterKeyDigests = new HashSet<>();
        for (int keymasterDigest : KeyProperties.Digest.allToKeymaster(authorizedKeyDigests)) {
            authorizedKeymasterKeyDigests.add(Integer.valueOf(keymasterDigest));
        }
        Set<Integer> supportedKeymasterSignatureDigests = new HashSet<>();
        for (int keymasterDigest2 : KeyProperties.Digest.allToKeymaster(supportedSignatureDigests)) {
            supportedKeymasterSignatureDigests.add(Integer.valueOf(keymasterDigest2));
        }
        Set<Integer> result = new HashSet<>(supportedKeymasterSignatureDigests);
        result.retainAll(authorizedKeymasterKeyDigests);
        return result;
    }

    private boolean isPropertyEmptyOrUnknown(String property) {
        return TextUtils.isEmpty(property) || property.equals("unknown");
    }
}
