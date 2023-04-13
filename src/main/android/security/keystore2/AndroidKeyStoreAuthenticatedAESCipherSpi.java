package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.ArrayUtils;
import android.security.keystore.KeyProperties;
import android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.EmptyArray;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public abstract class AndroidKeyStoreAuthenticatedAESCipherSpi extends AndroidKeyStoreCipherSpiBase {
    private static final int BLOCK_SIZE_BYTES = 16;
    private byte[] mIv;
    private boolean mIvHasBeenUsed;
    private final int mKeymasterBlockMode;
    private final int mKeymasterPadding;

    /* loaded from: classes3.dex */
    static abstract class GCM extends AndroidKeyStoreAuthenticatedAESCipherSpi {
        private static final int DEFAULT_TAG_LENGTH_BITS = 128;
        private static final int IV_LENGTH_BYTES = 12;
        private static final int MAX_SUPPORTED_TAG_LENGTH_BITS = 128;
        static final int MIN_SUPPORTED_TAG_LENGTH_BITS = 96;
        private int mTagLengthBits;

        GCM(int keymasterPadding) {
            super(32, keymasterPadding);
            this.mTagLengthBits = 128;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final String getTransform() {
            return "AES/GCM/NoPadding";
        }

        @Override // android.security.keystore2.AndroidKeyStoreAuthenticatedAESCipherSpi, android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void resetAll() {
            this.mTagLengthBits = 128;
            super.resetAll();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        public final void resetWhilePreservingInitState() {
            super.resetWhilePreservingInitState();
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters() throws InvalidKeyException {
            if (!isEncrypting()) {
                throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            if (params == null) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("GCMParameterSpec must be provided when decrypting");
                }
            } else if (!(params instanceof GCMParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only GCMParameterSpec supported");
            } else {
                GCMParameterSpec spec = (GCMParameterSpec) params;
                byte[] iv = spec.getIV();
                if (iv == null) {
                    throw new InvalidAlgorithmParameterException("Null IV in GCMParameterSpec");
                }
                if (iv.length != 12) {
                    throw new InvalidAlgorithmParameterException("Unsupported IV length: " + iv.length + " bytes. Only 12 bytes long IV supported");
                }
                int tagLengthBits = spec.getTLen();
                if (tagLengthBits < 96 || tagLengthBits > 128 || tagLengthBits % 8 != 0) {
                    throw new InvalidAlgorithmParameterException("Unsupported tag length: " + tagLengthBits + " bits. Supported lengths: 96, 104, 112, 120, 128");
                }
                setIv(iv);
                this.mTagLengthBits = tagLengthBits;
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameters params) throws InvalidAlgorithmParameterException {
            if (params == null) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("IV required when decrypting. Use GCMParameterSpec or GCM AlgorithmParameters to provide it.");
                }
            } else if (!KeyProperties.BLOCK_MODE_GCM.equalsIgnoreCase(params.getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + params.getAlgorithm() + ". Supported: GCM");
            } else {
                try {
                    GCMParameterSpec spec = (GCMParameterSpec) params.getParameterSpec(GCMParameterSpec.class);
                    initAlgorithmSpecificParameters(spec);
                } catch (InvalidParameterSpecException e) {
                    if (!isEncrypting()) {
                        throw new InvalidAlgorithmParameterException("IV and tag length required when decrypting, but not found in parameters: " + params, e);
                    }
                    setIv(null);
                }
            }
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
        protected final AlgorithmParameters engineGetParameters() {
            byte[] iv = getIv();
            if (iv != null && iv.length > 0) {
                try {
                    AlgorithmParameters params = AlgorithmParameters.getInstance(KeyProperties.BLOCK_MODE_GCM);
                    params.init(new GCMParameterSpec(this.mTagLengthBits, iv));
                    return params;
                } catch (NoSuchAlgorithmException e) {
                    throw new ProviderException("Failed to obtain GCM AlgorithmParameters", e);
                } catch (InvalidParameterSpecException e2) {
                    throw new ProviderException("Failed to initialize GCM AlgorithmParameters", e2);
                }
            }
            return null;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStoreOperation operation) {
            KeyStoreCryptoOperationStreamer streamer = new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(operation), 0);
            if (isEncrypting()) {
                return streamer;
            }
            return new BufferAllOutputUntilDoFinalStreamer(streamer);
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final KeyStoreCryptoOperationStreamer createAdditionalAuthenticationDataStreamer(KeyStoreOperation operation) {
            return new KeyStoreCryptoOperationChunkedStreamer(new AdditionalAuthenticationDataStream(operation), 0);
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForBegin() {
            if (getIv() == null && isEncrypting()) {
                return 12;
            }
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForFinish() {
            return 0;
        }

        @Override // android.security.keystore2.AndroidKeyStoreAuthenticatedAESCipherSpi, android.security.keystore2.AndroidKeyStoreCipherSpiBase
        protected final void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
            super.addAlgorithmSpecificParametersToBegin(parameters);
            parameters.add(KeyStore2ParameterUtils.makeInt(805307371, this.mTagLengthBits));
        }

        protected final int getTagLengthBits() {
            return this.mTagLengthBits;
        }

        /* loaded from: classes3.dex */
        public static final class NoPadding extends GCM {
            @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }

            @Override // javax.crypto.CipherSpi
            protected final int engineGetOutputSize(int inputLen) {
                long result;
                int tagLengthBytes = (getTagLengthBits() + 7) / 8;
                if (isEncrypting()) {
                    result = (getConsumedInputSizeBytes() - getProducedOutputSizeBytes()) + inputLen + tagLengthBytes;
                } else {
                    long result2 = getConsumedInputSizeBytes();
                    result = ((result2 - getProducedOutputSizeBytes()) + inputLen) - tagLengthBytes;
                }
                if (result < 0) {
                    return 0;
                }
                if (result > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) result;
            }
        }
    }

    AndroidKeyStoreAuthenticatedAESCipherSpi(int keymasterBlockMode, int keymasterPadding) {
        this.mKeymasterBlockMode = keymasterBlockMode;
        this.mKeymasterPadding = keymasterPadding;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreCipherSpiBase
    public void resetAll() {
        this.mIv = null;
        this.mIvHasBeenUsed = false;
        super.resetAll();
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
    protected void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
        if (isEncrypting() && this.mIvHasBeenUsed) {
            throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
        }
        parameters.add(KeyStore2ParameterUtils.makeEnum(268435458, 32));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870916, this.mKeymasterBlockMode));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870918, this.mKeymasterPadding));
        byte[] bArr = this.mIv;
        if (bArr != null) {
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
        byte[] bArr = this.mIv;
        if (bArr == null) {
            this.mIv = returnedIv;
        } else if (returnedIv != null && !Arrays.equals(returnedIv, bArr)) {
            throw new ProviderException("IV in use differs from provided IV");
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return 16;
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return ArrayUtils.cloneIfNotEmpty(this.mIv);
    }

    protected void setIv(byte[] iv) {
        this.mIv = iv;
    }

    protected byte[] getIv() {
        return this.mIv;
    }

    /* loaded from: classes3.dex */
    private static class BufferAllOutputUntilDoFinalStreamer implements KeyStoreCryptoOperationStreamer {
        private ByteArrayOutputStream mBufferedOutput;
        private final KeyStoreCryptoOperationStreamer mDelegate;
        private long mProducedOutputSizeBytes;

        private BufferAllOutputUntilDoFinalStreamer(KeyStoreCryptoOperationStreamer delegate) {
            this.mBufferedOutput = new ByteArrayOutputStream();
            this.mDelegate = delegate;
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
        public byte[] update(byte[] input, int inputOffset, int inputLength) throws KeyStoreException {
            byte[] output = this.mDelegate.update(input, inputOffset, inputLength);
            if (output != null) {
                try {
                    this.mBufferedOutput.write(output);
                } catch (IOException e) {
                    throw new ProviderException("Failed to buffer output", e);
                }
            }
            return EmptyArray.BYTE;
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
        public byte[] doFinal(byte[] input, int inputOffset, int inputLength, byte[] signature) throws KeyStoreException {
            byte[] output = this.mDelegate.doFinal(input, inputOffset, inputLength, signature);
            if (output != null) {
                try {
                    this.mBufferedOutput.write(output);
                } catch (IOException e) {
                    throw new ProviderException("Failed to buffer output", e);
                }
            }
            byte[] result = this.mBufferedOutput.toByteArray();
            this.mBufferedOutput.reset();
            this.mProducedOutputSizeBytes += result.length;
            return result;
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
        public long getConsumedInputSizeBytes() {
            return this.mDelegate.getConsumedInputSizeBytes();
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
        public long getProducedOutputSizeBytes() {
            return this.mProducedOutputSizeBytes;
        }
    }

    /* loaded from: classes3.dex */
    private static class AdditionalAuthenticationDataStream implements KeyStoreCryptoOperationChunkedStreamer.Stream {
        private final KeyStoreOperation mOperation;

        private AdditionalAuthenticationDataStream(KeyStoreOperation operation) {
            this.mOperation = operation;
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer.Stream
        public byte[] update(byte[] input) throws KeyStoreException {
            this.mOperation.updateAad(input);
            return null;
        }

        @Override // android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer.Stream
        public byte[] finish(byte[] input, byte[] signature) {
            return null;
        }
    }
}
