package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.keystore.ArrayUtils;
import android.security.keystore.KeyProperties;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes3.dex */
abstract class AndroidKeyStoreUnauthenticatedAESCipherSpi extends AndroidKeyStoreCipherSpiBase {
    private static final int BLOCK_SIZE_BYTES = 16;
    private byte[] mIv;
    private boolean mIvHasBeenUsed;
    private final boolean mIvRequired;
    private final int mKeymasterBlockMode;
    private final int mKeymasterPadding;

    /* loaded from: classes3.dex */
    static abstract class ECB extends AndroidKeyStoreUnauthenticatedAESCipherSpi {
        protected ECB(int keymasterPadding) {
            super(1, keymasterPadding, false);
        }

        /* loaded from: classes3.dex */
        public static class NoPadding extends ECB {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }

            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            protected final String getTransform() {
                return "AES/ECB/NoPadding";
            }
        }

        /* loaded from: classes3.dex */
        public static class PKCS7Padding extends ECB {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public PKCS7Padding() {
                super(64);
            }

            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            protected final String getTransform() {
                return "AES/ECB/PKCS7Padding";
            }
        }
    }

    /* loaded from: classes3.dex */
    static abstract class CBC extends AndroidKeyStoreUnauthenticatedAESCipherSpi {
        protected CBC(int keymasterPadding) {
            super(2, keymasterPadding, true);
        }

        /* loaded from: classes3.dex */
        public static class NoPadding extends CBC {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }

            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            protected final String getTransform() {
                return "AES/CBC/NoPadding";
            }
        }

        /* loaded from: classes3.dex */
        public static class PKCS7Padding extends CBC {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public PKCS7Padding() {
                super(64);
            }

            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            protected final String getTransform() {
                return "AES/CBC/PKCS7Padding";
            }
        }
    }

    /* loaded from: classes3.dex */
    static abstract class CTR extends AndroidKeyStoreUnauthenticatedAESCipherSpi {
        protected CTR(int keymasterPadding) {
            super(3, keymasterPadding, true);
        }

        /* loaded from: classes3.dex */
        public static class NoPadding extends CTR {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }

            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            protected final String getTransform() {
                return "AES/CTR/NoPadding";
            }
        }
    }

    AndroidKeyStoreUnauthenticatedAESCipherSpi(int keymasterBlockMode, int keymasterPadding, boolean ivRequired) {
        this.mKeymasterBlockMode = keymasterBlockMode;
        this.mKeymasterPadding = keymasterPadding;
        this.mIvRequired = ivRequired;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    public final void resetAll() {
        this.mIv = null;
        this.mIvHasBeenUsed = false;
        super.resetAll();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    public final void resetWhilePreservingInitState() {
        super.resetWhilePreservingInitState();
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void initKey(int opmode, Key key) throws InvalidKeyException {
        if (!(key instanceof AndroidKeyStoreSecretKey)) {
            throw new InvalidKeyException("Unsupported key: " + (key != null ? key.getClass().getName() : "null"));
        } else if (!KeyProperties.KEY_ALGORITHM_AES.equalsIgnoreCase(key.getAlgorithm())) {
            throw new InvalidKeyException("Unsupported key algorithm: " + key.getAlgorithm() + ". Only " + KeyProperties.KEY_ALGORITHM_AES + " supported");
        } else {
            setKey((AndroidKeyStoreSecretKey) key);
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void initAlgorithmSpecificParameters() throws InvalidKeyException {
        if (this.mIvRequired && !isEncrypting()) {
            throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (!this.mIvRequired) {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unsupported parameters: " + params);
            }
        } else if (params == null) {
            if (!isEncrypting()) {
                throw new InvalidAlgorithmParameterException("IvParameterSpec must be provided when decrypting");
            }
        } else if (!(params instanceof IvParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Only IvParameterSpec supported");
        } else {
            byte[] iv = ((IvParameterSpec) params).getIV();
            this.mIv = iv;
            if (iv == null) {
                throw new InvalidAlgorithmParameterException("Null IV in IvParameterSpec");
            }
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void initAlgorithmSpecificParameters(AlgorithmParameters params) throws InvalidAlgorithmParameterException {
        if (!this.mIvRequired) {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Unsupported parameters: " + params);
            }
        } else if (params == null) {
            if (!isEncrypting()) {
                throw new InvalidAlgorithmParameterException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
            }
        } else if (!KeyProperties.KEY_ALGORITHM_AES.equalsIgnoreCase(params.getAlgorithm())) {
            throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + params.getAlgorithm() + ". Supported: AES");
        } else {
            try {
                IvParameterSpec ivSpec = (IvParameterSpec) params.getParameterSpec(IvParameterSpec.class);
                byte[] iv = ivSpec.getIV();
                this.mIv = iv;
                if (iv == null) {
                    throw new InvalidAlgorithmParameterException("Null IV in AlgorithmParameters");
                }
            } catch (InvalidParameterSpecException e) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("IV required when decrypting, but not found in parameters: " + params, e);
                }
                this.mIv = null;
            }
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final int getAdditionalEntropyAmountForBegin() {
        if (this.mIvRequired && this.mIv == null && isEncrypting()) {
            return 16;
        }
        return 0;
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final int getAdditionalEntropyAmountForFinish() {
        return 0;
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
        byte[] bArr;
        if (isEncrypting() && this.mIvRequired && this.mIvHasBeenUsed) {
            throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
        }
        parameters.add(KeyStore2ParameterUtils.makeEnum(268435458, 32));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870916, this.mKeymasterBlockMode));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870918, this.mKeymasterPadding));
        if (this.mIvRequired && (bArr = this.mIv) != null) {
            parameters.add(KeyStore2ParameterUtils.makeBytes(-1879047191, bArr));
        }
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    protected final void loadAlgorithmSpecificParametersFromBeginResult(KeyParameter[] parameters) {
        this.mIvHasBeenUsed = true;
        byte[] returnedIv = null;
        if (parameters != null) {
            int length = parameters.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                KeyParameter p = parameters[i];
                if (p.tag != -1879047191) {
                    i++;
                } else {
                    returnedIv = p.value.getBlob();
                    break;
                }
            }
        }
        if (this.mIvRequired) {
            byte[] bArr = this.mIv;
            if (bArr == null) {
                this.mIv = returnedIv;
            } else if (returnedIv != null && !Arrays.equals(returnedIv, bArr)) {
                throw new ProviderException("IV in use differs from provided IV");
            }
        } else if (returnedIv != null) {
            throw new ProviderException("IV in use despite IV not being used by this transformation");
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return 16;
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetOutputSize(int inputLen) {
        return inputLen + 48;
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return ArrayUtils.cloneIfNotEmpty(this.mIv);
    }

    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
    protected final AlgorithmParameters engineGetParameters() {
        byte[] bArr;
        if (this.mIvRequired && (bArr = this.mIv) != null && bArr.length > 0) {
            try {
                AlgorithmParameters params = AlgorithmParameters.getInstance(KeyProperties.KEY_ALGORITHM_AES);
                params.init(new IvParameterSpec(this.mIv));
                return params;
            } catch (NoSuchAlgorithmException e) {
                throw new ProviderException("Failed to obtain AES AlgorithmParameters", e);
            } catch (InvalidParameterSpecException e2) {
                throw new ProviderException("Failed to initialize AES AlgorithmParameters with an IV", e2);
            }
        }
        return null;
    }
}
