package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.keystore.KeyProperties;
import android.system.keystore2.Authorization;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.List;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
/* loaded from: classes3.dex */
abstract class AndroidKeyStoreRSACipherSpi extends AndroidKeyStoreCipherSpiBase {
    private final int mKeymasterPadding;
    private int mKeymasterPaddingOverride;
    private int mModulusSizeBytes = -1;

    /* loaded from: classes3.dex */
    public static final class NoPadding extends AndroidKeyStoreRSACipherSpi {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public NoPadding() {
            super(1);
        }

        @Override // android.security.keystore2.AndroidKeyStoreRSACipherSpi
        protected boolean adjustConfigForEncryptingWithPrivateKey() {
            setKeymasterPurposeOverride(2);
            return true;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters() throws InvalidKeyException {
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unexpected parameters: " + params + ". No parameters supported");
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters(AlgorithmParameters params) throws InvalidAlgorithmParameterException {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unexpected parameters: " + params + ". No parameters supported");
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
        protected AlgorithmParameters engineGetParameters() {
            return null;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForBegin() {
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForFinish() {
            return 0;
        }
    }

    /* loaded from: classes3.dex */
    public static final class PKCS1Padding extends AndroidKeyStoreRSACipherSpi {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public PKCS1Padding() {
            super(4);
        }

        @Override // android.security.keystore2.AndroidKeyStoreRSACipherSpi
        protected boolean adjustConfigForEncryptingWithPrivateKey() {
            setKeymasterPurposeOverride(2);
            setKeymasterPaddingOverride(5);
            return true;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters() throws InvalidKeyException {
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unexpected parameters: " + params + ". No parameters supported");
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected void initAlgorithmSpecificParameters(AlgorithmParameters params) throws InvalidAlgorithmParameterException {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unexpected parameters: " + params + ". No parameters supported");
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
        protected AlgorithmParameters engineGetParameters() {
            return null;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForBegin() {
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForFinish() {
            if (isEncrypting()) {
                return getModulusSizeBytes();
            }
            return 0;
        }
    }

    /* loaded from: classes3.dex */
    static abstract class OAEPWithMGF1Padding extends AndroidKeyStoreRSACipherSpi {
        private static final String MGF_ALGORITHM_MGF1 = "MGF1";
        private int mDigestOutputSizeBytes;
        private int mKeymasterDigest;
        private int mKeymasterMgf1Digest;

        OAEPWithMGF1Padding(int keymasterDigest) {
            super(2);
            this.mKeymasterDigest = -1;
            this.mKeymasterMgf1Digest = 2;
            this.mKeymasterDigest = keymasterDigest;
            this.mDigestOutputSizeBytes = (KeymasterUtils.getDigestOutputSizeBits(keymasterDigest) + 7) / 8;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters() throws InvalidKeyException {
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            if (params == null) {
                return;
            }
            if (!(params instanceof OAEPParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Unsupported parameter spec: " + params + ". Only OAEPParameterSpec supported");
            }
            OAEPParameterSpec spec = (OAEPParameterSpec) params;
            if (!MGF_ALGORITHM_MGF1.equalsIgnoreCase(spec.getMGFAlgorithm())) {
                throw new InvalidAlgorithmParameterException("Unsupported MGF: " + spec.getMGFAlgorithm() + ". Only " + MGF_ALGORITHM_MGF1 + " supported");
            }
            String jcaDigest = spec.getDigestAlgorithm();
            try {
                int keymasterDigest = KeyProperties.Digest.toKeymaster(jcaDigest);
                switch (keymasterDigest) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        AlgorithmParameterSpec mgfParams = spec.getMGFParameters();
                        if (mgfParams == null) {
                            throw new InvalidAlgorithmParameterException("MGF parameters must be provided");
                        }
                        if (!(mgfParams instanceof MGF1ParameterSpec)) {
                            throw new InvalidAlgorithmParameterException("Unsupported MGF parameters: " + mgfParams + ". Only MGF1ParameterSpec supported");
                        }
                        MGF1ParameterSpec mgfSpec = (MGF1ParameterSpec) mgfParams;
                        String mgf1JcaDigest = mgfSpec.getDigestAlgorithm();
                        PSource pSource = spec.getPSource();
                        if (!(pSource instanceof PSource.PSpecified)) {
                            throw new InvalidAlgorithmParameterException("Unsupported source of encoding input P: " + pSource + ". Only pSpecifiedEmpty (PSource.PSpecified.DEFAULT) supported");
                        }
                        PSource.PSpecified pSourceSpecified = (PSource.PSpecified) pSource;
                        byte[] pSourceValue = pSourceSpecified.getValue();
                        if (pSourceValue != null && pSourceValue.length > 0) {
                            throw new InvalidAlgorithmParameterException("Unsupported source of encoding input P: " + pSource + ". Only pSpecifiedEmpty (PSource.PSpecified.DEFAULT) supported");
                        }
                        this.mKeymasterDigest = keymasterDigest;
                        this.mKeymasterMgf1Digest = KeyProperties.Digest.toKeymaster(mgf1JcaDigest);
                        this.mDigestOutputSizeBytes = (KeymasterUtils.getDigestOutputSizeBits(keymasterDigest) + 7) / 8;
                        return;
                    default:
                        throw new InvalidAlgorithmParameterException("Unsupported digest: " + jcaDigest);
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidAlgorithmParameterException("Unsupported digest: " + jcaDigest, e);
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameters params) throws InvalidAlgorithmParameterException {
            if (params == null) {
                return;
            }
            try {
                OAEPParameterSpec spec = (OAEPParameterSpec) params.getParameterSpec(OAEPParameterSpec.class);
                if (spec == null) {
                    throw new InvalidAlgorithmParameterException("OAEP parameters required, but not provided in parameters: " + params);
                }
                initAlgorithmSpecificParameters(spec);
            } catch (InvalidParameterSpecException e) {
                throw new InvalidAlgorithmParameterException("OAEP parameters required, but not found in parameters: " + params, e);
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
        protected final AlgorithmParameters engineGetParameters() {
            OAEPParameterSpec spec = new OAEPParameterSpec(KeyProperties.Digest.fromKeymaster(this.mKeymasterDigest), MGF_ALGORITHM_MGF1, KeyProperties.Digest.fromKeymasterToMGF1ParameterSpec(this.mKeymasterMgf1Digest), PSource.PSpecified.DEFAULT);
            try {
                AlgorithmParameters params = AlgorithmParameters.getInstance("OAEP");
                params.init(spec);
                return params;
            } catch (NoSuchAlgorithmException e) {
                throw new ProviderException("Failed to obtain OAEP AlgorithmParameters", e);
            } catch (InvalidParameterSpecException e2) {
                throw new ProviderException("Failed to initialize OAEP AlgorithmParameters with an IV", e2);
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreRSACipherSpi, android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
            super.addAlgorithmSpecificParametersToBegin(parameters);
            parameters.add(KeyStore2ParameterUtils.makeEnum(536870917, this.mKeymasterDigest));
            parameters.add(KeyStore2ParameterUtils.makeEnum(536871115, this.mKeymasterMgf1Digest));
        }

        @Override // android.security.keystore2.AndroidKeyStoreRSACipherSpi, android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void loadAlgorithmSpecificParametersFromBeginResult(KeyParameter[] parameters) {
            super.loadAlgorithmSpecificParametersFromBeginResult(parameters);
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForBegin() {
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForFinish() {
            if (isEncrypting()) {
                return this.mDigestOutputSizeBytes;
            }
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreRSACipherSpi, android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final String getTransform() {
            switch (this.mKeymasterDigest) {
                case 2:
                    return "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
                case 3:
                    return "RSA/ECB/OAEPWithSHA-224AndMGF1Padding";
                case 4:
                    return "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
                case 5:
                    return "RSA/ECB/OAEPWithSHA-384AndMGF1Padding";
                case 6:
                    return "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
                default:
                    return "RSA/ECB/OAEPPadding";
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class OAEPWithSHA1AndMGF1Padding extends OAEPWithMGF1Padding {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public OAEPWithSHA1AndMGF1Padding() {
            super(2);
        }
    }

    /* loaded from: classes3.dex */
    public static class OAEPWithSHA224AndMGF1Padding extends OAEPWithMGF1Padding {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public OAEPWithSHA224AndMGF1Padding() {
            super(3);
        }
    }

    /* loaded from: classes3.dex */
    public static class OAEPWithSHA256AndMGF1Padding extends OAEPWithMGF1Padding {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public OAEPWithSHA256AndMGF1Padding() {
            super(4);
        }
    }

    /* loaded from: classes3.dex */
    public static class OAEPWithSHA384AndMGF1Padding extends OAEPWithMGF1Padding {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public OAEPWithSHA384AndMGF1Padding() {
            super(5);
        }
    }

    /* loaded from: classes3.dex */
    public static class OAEPWithSHA512AndMGF1Padding extends OAEPWithMGF1Padding {
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public /* bridge */ /* synthetic */ void finalize() throws Throwable {
            super.finalize();
        }

        public OAEPWithSHA512AndMGF1Padding() {
            super(6);
        }
    }

    AndroidKeyStoreRSACipherSpi(int keymasterPadding) {
        this.mKeymasterPadding = keymasterPadding;
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected String getTransform() {
        return "RSA/ECB/" + KeyProperties.EncryptionPadding.fromKeymaster(this.mKeymasterPadding);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void initKey(int opmode, Key key) throws InvalidKeyException {
        AndroidKeyStoreKey keystoreKey;
        Authorization[] authorizations;
        if (key == null) {
            throw new InvalidKeyException("Unsupported key: null");
        }
        if (!KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(key.getAlgorithm())) {
            throw new InvalidKeyException("Unsupported key algorithm: " + key.getAlgorithm() + ". Only " + KeyProperties.KEY_ALGORITHM_RSA + " supported");
        }
        if (key instanceof AndroidKeyStorePrivateKey) {
            keystoreKey = (AndroidKeyStoreKey) key;
        } else if (key instanceof AndroidKeyStorePublicKey) {
            keystoreKey = (AndroidKeyStoreKey) key;
        } else {
            throw new InvalidKeyException("Unsupported key type: " + key);
        }
        if (keystoreKey instanceof PrivateKey) {
            switch (opmode) {
                case 1:
                case 3:
                    if (!adjustConfigForEncryptingWithPrivateKey()) {
                        throw new InvalidKeyException("RSA private keys cannot be used with " + opmodeToString(opmode) + " and padding " + KeyProperties.EncryptionPadding.fromKeymaster(this.mKeymasterPadding) + ". Only RSA public keys supported for this mode");
                    }
                    break;
                case 2:
                case 4:
                    break;
                default:
                    throw new InvalidKeyException("RSA private keys cannot be used with opmode: " + opmode);
            }
        } else {
            switch (opmode) {
                case 1:
                case 3:
                    break;
                case 2:
                case 4:
                    throw new InvalidKeyException("RSA public keys cannot be used with " + opmodeToString(opmode) + " and padding " + KeyProperties.EncryptionPadding.fromKeymaster(this.mKeymasterPadding) + ". Only RSA private keys supported for this opmode.");
                default:
                    throw new InvalidKeyException("RSA public keys cannot be used with " + opmodeToString(opmode));
            }
        }
        long keySizeBits = -1;
        for (Authorization a : keystoreKey.getAuthorizations()) {
            if (a.keyParameter.tag == 805306371) {
                keySizeBits = KeyStore2ParameterUtils.getUnsignedInt(a);
            }
        }
        if (keySizeBits == -1) {
            throw new InvalidKeyException("Size of key not known");
        }
        if (keySizeBits > 2147483647L) {
            throw new InvalidKeyException("Key too large: " + keySizeBits + " bits");
        }
        this.mModulusSizeBytes = (int) ((7 + keySizeBits) / 8);
        setKey(keystoreKey);
    }

    protected boolean adjustConfigForEncryptingWithPrivateKey() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    public final void resetAll() {
        this.mModulusSizeBytes = -1;
        this.mKeymasterPaddingOverride = -1;
        super.resetAll();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    public final void resetWhilePreservingInitState() {
        super.resetWhilePreservingInitState();
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
        parameters.add(KeyStore2ParameterUtils.makeEnum(268435458, 1));
        int keymasterPadding = getKeymasterPaddingOverride();
        if (keymasterPadding == -1) {
            keymasterPadding = this.mKeymasterPadding;
        }
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870918, keymasterPadding));
        int purposeOverride = getKeymasterPurposeOverride();
        if (purposeOverride != -1) {
            if (purposeOverride == 2 || purposeOverride == 3) {
                parameters.add(KeyStore2ParameterUtils.makeEnum(536870917, 0));
            }
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected void loadAlgorithmSpecificParametersFromBeginResult(KeyParameter[] parameters) {
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetOutputSize(int inputLen) {
        return getModulusSizeBytes();
    }

    protected final int getModulusSizeBytes() {
        int i = this.mModulusSizeBytes;
        if (i == -1) {
            throw new IllegalStateException("Not initialized");
        }
        return i;
    }

    protected final void setKeymasterPaddingOverride(int keymasterPadding) {
        this.mKeymasterPaddingOverride = keymasterPadding;
    }

    protected final int getKeymasterPaddingOverride() {
        return this.mKeymasterPaddingOverride;
    }
}
