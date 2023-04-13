package android.security.keystore;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collection;
import java.util.Locale;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public abstract class KeyProperties {
    public static final int AUTH_BIOMETRIC_STRONG = 2;
    public static final int AUTH_DEVICE_CREDENTIAL = 1;
    public static final String BLOCK_MODE_CBC = "CBC";
    public static final String BLOCK_MODE_CTR = "CTR";
    public static final String BLOCK_MODE_ECB = "ECB";
    public static final String BLOCK_MODE_GCM = "GCM";
    public static final String DIGEST_MD5 = "MD5";
    public static final String DIGEST_NONE = "NONE";
    public static final String DIGEST_SHA1 = "SHA-1";
    public static final String DIGEST_SHA224 = "SHA-224";
    public static final String DIGEST_SHA256 = "SHA-256";
    public static final String DIGEST_SHA384 = "SHA-384";
    public static final String DIGEST_SHA512 = "SHA-512";
    public static final String ENCRYPTION_PADDING_NONE = "NoPadding";
    public static final String ENCRYPTION_PADDING_PKCS7 = "PKCS7Padding";
    public static final String ENCRYPTION_PADDING_RSA_OAEP = "OAEPPadding";
    public static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding";
    @Deprecated
    public static final String KEY_ALGORITHM_3DES = "DESede";
    public static final String KEY_ALGORITHM_AES = "AES";
    public static final String KEY_ALGORITHM_EC = "EC";
    public static final String KEY_ALGORITHM_HMAC_SHA1 = "HmacSHA1";
    public static final String KEY_ALGORITHM_HMAC_SHA224 = "HmacSHA224";
    public static final String KEY_ALGORITHM_HMAC_SHA256 = "HmacSHA256";
    public static final String KEY_ALGORITHM_HMAC_SHA384 = "HmacSHA384";
    public static final String KEY_ALGORITHM_HMAC_SHA512 = "HmacSHA512";
    public static final String KEY_ALGORITHM_RSA = "RSA";
    public static final String KEY_ALGORITHM_XDH = "XDH";
    @SystemApi
    public static final int NAMESPACE_APPLICATION = -1;
    public static final int NAMESPACE_LOCKSETTINGS = 103;
    @SystemApi
    public static final int NAMESPACE_WIFI = 102;
    public static final int ORIGIN_GENERATED = 1;
    public static final int ORIGIN_IMPORTED = 2;
    public static final int ORIGIN_SECURELY_IMPORTED = 8;
    public static final int ORIGIN_UNKNOWN = 4;
    public static final int PURPOSE_AGREE_KEY = 64;
    public static final int PURPOSE_ATTEST_KEY = 128;
    public static final int PURPOSE_DECRYPT = 2;
    public static final int PURPOSE_ENCRYPT = 1;
    public static final int PURPOSE_SIGN = 4;
    public static final int PURPOSE_VERIFY = 8;
    public static final int PURPOSE_WRAP_KEY = 32;
    public static final int SECURITY_LEVEL_SOFTWARE = 0;
    public static final int SECURITY_LEVEL_STRONGBOX = 2;
    public static final int SECURITY_LEVEL_TRUSTED_ENVIRONMENT = 1;
    public static final int SECURITY_LEVEL_UNKNOWN = -2;
    public static final int SECURITY_LEVEL_UNKNOWN_SECURE = -1;
    public static final String SIGNATURE_PADDING_RSA_PKCS1 = "PKCS1";
    public static final String SIGNATURE_PADDING_RSA_PSS = "PSS";
    public static final int UNRESTRICTED_USAGE_COUNT = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AuthEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface BlockModeEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DigestEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EncryptionPaddingEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface KeyAlgorithmEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Namespace {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface OriginEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PurposeEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SecurityLevelEnum {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SignaturePaddingEnum {
    }

    private KeyProperties() {
    }

    /* loaded from: classes3.dex */
    public static abstract class Purpose {
        private Purpose() {
        }

        public static int toKeymaster(int purpose) {
            switch (purpose) {
                case 1:
                    return 0;
                case 2:
                    return 1;
                case 4:
                    return 2;
                case 8:
                    return 3;
                case 32:
                    return 5;
                case 64:
                    return 6;
                case 128:
                    return 7;
                default:
                    throw new IllegalArgumentException("Unknown purpose: " + purpose);
            }
        }

        public static int fromKeymaster(int purpose) {
            switch (purpose) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 4;
                case 3:
                    return 8;
                case 4:
                default:
                    throw new IllegalArgumentException("Unknown purpose: " + purpose);
                case 5:
                    return 32;
                case 6:
                    return 64;
                case 7:
                    return 128;
            }
        }

        public static int[] allToKeymaster(int purposes) {
            int[] result = KeyProperties.getSetFlags(purposes);
            for (int i = 0; i < result.length; i++) {
                result[i] = toKeymaster(result[i]);
            }
            return result;
        }

        public static int allFromKeymaster(Collection<Integer> purposes) {
            int result = 0;
            for (Integer num : purposes) {
                int keymasterPurpose = num.intValue();
                result |= fromKeymaster(keymasterPurpose);
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class KeyAlgorithm {
        private KeyAlgorithm() {
        }

        public static int toKeymasterAsymmetricKeyAlgorithm(String algorithm) {
            if (KeyProperties.KEY_ALGORITHM_EC.equalsIgnoreCase(algorithm) || KeyProperties.KEY_ALGORITHM_XDH.equalsIgnoreCase(algorithm)) {
                return 3;
            }
            if (KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(algorithm)) {
                return 1;
            }
            throw new IllegalArgumentException("Unsupported key algorithm: " + algorithm);
        }

        public static String fromKeymasterAsymmetricKeyAlgorithm(int keymasterAlgorithm) {
            switch (keymasterAlgorithm) {
                case 1:
                    return KeyProperties.KEY_ALGORITHM_RSA;
                case 2:
                default:
                    throw new IllegalArgumentException("Unsupported key algorithm: " + keymasterAlgorithm);
                case 3:
                    return KeyProperties.KEY_ALGORITHM_EC;
            }
        }

        public static int toKeymasterSecretKeyAlgorithm(String algorithm) {
            if (KeyProperties.KEY_ALGORITHM_AES.equalsIgnoreCase(algorithm)) {
                return 32;
            }
            if (KeyProperties.KEY_ALGORITHM_3DES.equalsIgnoreCase(algorithm)) {
                return 33;
            }
            if (algorithm.toUpperCase(Locale.US).startsWith("HMAC")) {
                return 128;
            }
            throw new IllegalArgumentException("Unsupported secret key algorithm: " + algorithm);
        }

        public static String fromKeymasterSecretKeyAlgorithm(int keymasterAlgorithm, int keymasterDigest) {
            switch (keymasterAlgorithm) {
                case 32:
                    return KeyProperties.KEY_ALGORITHM_AES;
                case 33:
                    return KeyProperties.KEY_ALGORITHM_3DES;
                case 128:
                    switch (keymasterDigest) {
                        case 2:
                            return KeyProperties.KEY_ALGORITHM_HMAC_SHA1;
                        case 3:
                            return KeyProperties.KEY_ALGORITHM_HMAC_SHA224;
                        case 4:
                            return KeyProperties.KEY_ALGORITHM_HMAC_SHA256;
                        case 5:
                            return KeyProperties.KEY_ALGORITHM_HMAC_SHA384;
                        case 6:
                            return KeyProperties.KEY_ALGORITHM_HMAC_SHA512;
                        default:
                            throw new IllegalArgumentException("Unsupported HMAC digest: " + Digest.fromKeymaster(keymasterDigest));
                    }
                default:
                    throw new IllegalArgumentException("Unsupported key algorithm: " + keymasterAlgorithm);
            }
        }

        public static int toKeymasterDigest(String algorithm) {
            String algorithmUpper = algorithm.toUpperCase(Locale.US);
            char c = 65535;
            if (algorithmUpper.startsWith("HMAC")) {
                String digestUpper = algorithmUpper.substring("HMAC".length());
                switch (digestUpper.hashCode()) {
                    case -1850268184:
                        if (digestUpper.equals("SHA224")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1850268089:
                        if (digestUpper.equals("SHA256")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1850267037:
                        if (digestUpper.equals("SHA384")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1850265334:
                        if (digestUpper.equals("SHA512")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 2543909:
                        if (digestUpper.equals("SHA1")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 4;
                    case 3:
                        return 5;
                    case 4:
                        return 6;
                    default:
                        throw new IllegalArgumentException("Unsupported HMAC digest: " + digestUpper);
                }
            }
            return -1;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class BlockMode {
        private BlockMode() {
        }

        public static int toKeymaster(String blockMode) {
            if (KeyProperties.BLOCK_MODE_ECB.equalsIgnoreCase(blockMode)) {
                return 1;
            }
            if (KeyProperties.BLOCK_MODE_CBC.equalsIgnoreCase(blockMode)) {
                return 2;
            }
            if (KeyProperties.BLOCK_MODE_CTR.equalsIgnoreCase(blockMode)) {
                return 3;
            }
            if (KeyProperties.BLOCK_MODE_GCM.equalsIgnoreCase(blockMode)) {
                return 32;
            }
            throw new IllegalArgumentException("Unsupported block mode: " + blockMode);
        }

        public static String fromKeymaster(int blockMode) {
            switch (blockMode) {
                case 1:
                    return KeyProperties.BLOCK_MODE_ECB;
                case 2:
                    return KeyProperties.BLOCK_MODE_CBC;
                case 3:
                    return KeyProperties.BLOCK_MODE_CTR;
                case 32:
                    return KeyProperties.BLOCK_MODE_GCM;
                default:
                    throw new IllegalArgumentException("Unsupported block mode: " + blockMode);
            }
        }

        public static String[] allFromKeymaster(Collection<Integer> blockModes) {
            if (blockModes == null || blockModes.isEmpty()) {
                return EmptyArray.STRING;
            }
            String[] result = new String[blockModes.size()];
            int offset = 0;
            for (Integer num : blockModes) {
                int blockMode = num.intValue();
                result[offset] = fromKeymaster(blockMode);
                offset++;
            }
            return result;
        }

        public static int[] allToKeymaster(String[] blockModes) {
            if (blockModes == null || blockModes.length == 0) {
                return EmptyArray.INT;
            }
            int[] result = new int[blockModes.length];
            for (int i = 0; i < blockModes.length; i++) {
                result[i] = toKeymaster(blockModes[i]);
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class EncryptionPadding {
        private EncryptionPadding() {
        }

        public static int toKeymaster(String padding) {
            if (KeyProperties.ENCRYPTION_PADDING_NONE.equalsIgnoreCase(padding)) {
                return 1;
            }
            if (KeyProperties.ENCRYPTION_PADDING_PKCS7.equalsIgnoreCase(padding)) {
                return 64;
            }
            if (KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1.equalsIgnoreCase(padding)) {
                return 4;
            }
            if (KeyProperties.ENCRYPTION_PADDING_RSA_OAEP.equalsIgnoreCase(padding)) {
                return 2;
            }
            throw new IllegalArgumentException("Unsupported encryption padding scheme: " + padding);
        }

        public static String fromKeymaster(int padding) {
            switch (padding) {
                case 1:
                    return KeyProperties.ENCRYPTION_PADDING_NONE;
                case 2:
                    return KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
                case 4:
                    return KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;
                case 64:
                    return KeyProperties.ENCRYPTION_PADDING_PKCS7;
                default:
                    throw new IllegalArgumentException("Unsupported encryption padding: " + padding);
            }
        }

        public static int[] allToKeymaster(String[] paddings) {
            if (paddings == null || paddings.length == 0) {
                return EmptyArray.INT;
            }
            int[] result = new int[paddings.length];
            for (int i = 0; i < paddings.length; i++) {
                result[i] = toKeymaster(paddings[i]);
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class SignaturePadding {
        private SignaturePadding() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public static int toKeymaster(String padding) {
            char c;
            String upperCase = padding.toUpperCase(Locale.US);
            switch (upperCase.hashCode()) {
                case 79536:
                    if (upperCase.equals(KeyProperties.SIGNATURE_PADDING_RSA_PSS)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 76183014:
                    if (upperCase.equals(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return 5;
                case 1:
                    return 3;
                default:
                    throw new IllegalArgumentException("Unsupported signature padding scheme: " + padding);
            }
        }

        public static String fromKeymaster(int padding) {
            switch (padding) {
                case 3:
                    return KeyProperties.SIGNATURE_PADDING_RSA_PSS;
                case 4:
                default:
                    throw new IllegalArgumentException("Unsupported signature padding: " + padding);
                case 5:
                    return KeyProperties.SIGNATURE_PADDING_RSA_PKCS1;
            }
        }

        public static int[] allToKeymaster(String[] paddings) {
            if (paddings == null || paddings.length == 0) {
                return EmptyArray.INT;
            }
            int[] result = new int[paddings.length];
            for (int i = 0; i < paddings.length; i++) {
                result[i] = toKeymaster(paddings[i]);
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Digest {
        private Digest() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public static int toKeymaster(String digest) {
            char c;
            String upperCase = digest.toUpperCase(Locale.US);
            switch (upperCase.hashCode()) {
                case -1523887821:
                    if (upperCase.equals(KeyProperties.DIGEST_SHA224)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1523887726:
                    if (upperCase.equals("SHA-256")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -1523886674:
                    if (upperCase.equals(KeyProperties.DIGEST_SHA384)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1523884971:
                    if (upperCase.equals(KeyProperties.DIGEST_SHA512)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 76158:
                    if (upperCase.equals(KeyProperties.DIGEST_MD5)) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 2402104:
                    if (upperCase.equals(KeyProperties.DIGEST_NONE)) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 78861104:
                    if (upperCase.equals("SHA-1")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return 2;
                case 1:
                    return 3;
                case 2:
                    return 4;
                case 3:
                    return 5;
                case 4:
                    return 6;
                case 5:
                    return 0;
                case 6:
                    return 1;
                default:
                    throw new IllegalArgumentException("Unsupported digest algorithm: " + digest);
            }
        }

        public static String fromKeymaster(int digest) {
            switch (digest) {
                case 0:
                    return KeyProperties.DIGEST_NONE;
                case 1:
                    return KeyProperties.DIGEST_MD5;
                case 2:
                    return "SHA-1";
                case 3:
                    return KeyProperties.DIGEST_SHA224;
                case 4:
                    return "SHA-256";
                case 5:
                    return KeyProperties.DIGEST_SHA384;
                case 6:
                    return KeyProperties.DIGEST_SHA512;
                default:
                    throw new IllegalArgumentException("Unsupported digest algorithm: " + digest);
            }
        }

        public static AlgorithmParameterSpec fromKeymasterToMGF1ParameterSpec(int digest) {
            switch (digest) {
                case 3:
                    return MGF1ParameterSpec.SHA224;
                case 4:
                    return MGF1ParameterSpec.SHA256;
                case 5:
                    return MGF1ParameterSpec.SHA384;
                case 6:
                    return MGF1ParameterSpec.SHA512;
                default:
                    return MGF1ParameterSpec.SHA1;
            }
        }

        public static String fromKeymasterToSignatureAlgorithmDigest(int digest) {
            switch (digest) {
                case 0:
                    return KeyProperties.DIGEST_NONE;
                case 1:
                    return KeyProperties.DIGEST_MD5;
                case 2:
                    return "SHA1";
                case 3:
                    return "SHA224";
                case 4:
                    return "SHA256";
                case 5:
                    return "SHA384";
                case 6:
                    return "SHA512";
                default:
                    throw new IllegalArgumentException("Unsupported digest algorithm: " + digest);
            }
        }

        public static String[] allFromKeymaster(Collection<Integer> digests) {
            if (digests.isEmpty()) {
                return EmptyArray.STRING;
            }
            String[] result = new String[digests.size()];
            int offset = 0;
            for (Integer num : digests) {
                int digest = num.intValue();
                result[offset] = fromKeymaster(digest);
                offset++;
            }
            return result;
        }

        public static int[] allToKeymaster(String[] digests) {
            if (digests == null || digests.length == 0) {
                return EmptyArray.INT;
            }
            int[] result = new int[digests.length];
            int offset = 0;
            for (String digest : digests) {
                result[offset] = toKeymaster(digest);
                offset++;
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Origin {
        private Origin() {
        }

        public static int fromKeymaster(int origin) {
            switch (origin) {
                case 0:
                    return 1;
                case 1:
                default:
                    throw new IllegalArgumentException("Unknown origin: " + origin);
                case 2:
                    return 2;
                case 3:
                    return 4;
                case 4:
                    return 8;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int[] getSetFlags(int flags) {
        if (flags == 0) {
            return EmptyArray.INT;
        }
        int[] result = new int[getSetBitCount(flags)];
        int resultOffset = 0;
        int flag = 1;
        while (flags != 0) {
            if ((flags & 1) != 0) {
                result[resultOffset] = flag;
                resultOffset++;
            }
            flags >>>= 1;
            flag <<= 1;
        }
        return result;
    }

    private static int getSetBitCount(int value) {
        if (value == 0) {
            return 0;
        }
        int result = 0;
        while (value != 0) {
            if ((value & 1) != 0) {
                result++;
            }
            value >>>= 1;
        }
        return result;
    }

    /* loaded from: classes3.dex */
    public static abstract class SecurityLevel {
        private SecurityLevel() {
        }

        public static int toKeymaster(int securityLevel) {
            switch (securityLevel) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                default:
                    throw new IllegalArgumentException("Unsupported security level: " + securityLevel);
            }
        }

        public static int fromKeymaster(int securityLevel) {
            switch (securityLevel) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                default:
                    throw new IllegalArgumentException("Unsupported security level: " + securityLevel);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class EcCurve {
        private EcCurve() {
        }

        public static int toKeymasterCurve(ECParameterSpec spec) {
            int keySize = spec.getCurve().getField().getFieldSize();
            switch (keySize) {
                case 224:
                    return 0;
                case 256:
                    return 1;
                case 384:
                    return 2;
                case 521:
                    return 3;
                default:
                    return -1;
            }
        }

        public static int fromKeymasterCurve(int ecCurve) {
            switch (ecCurve) {
                case 0:
                    return 224;
                case 1:
                case 4:
                    return 256;
                case 2:
                    return 384;
                case 3:
                    return 521;
                default:
                    return -1;
            }
        }
    }

    public static int namespaceToLegacyUid(int namespace) {
        switch (namespace) {
            case -1:
                return -1;
            case 102:
                return 1010;
            default:
                throw new IllegalArgumentException("No UID corresponding to namespace " + namespace);
        }
    }

    public static int legacyUidToNamespace(int uid) {
        switch (uid) {
            case -1:
                return -1;
            case 1010:
                return 102;
            default:
                throw new IllegalArgumentException("No namespace corresponding to uid " + uid);
        }
    }
}
