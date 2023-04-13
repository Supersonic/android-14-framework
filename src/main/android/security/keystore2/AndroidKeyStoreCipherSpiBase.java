package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyStoreCryptoOperation;
import android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
abstract class AndroidKeyStoreCipherSpiBase extends CipherSpi implements KeyStoreCryptoOperation {
    public static final String DEFAULT_MGF1_DIGEST = "SHA-1";
    private static final String TAG = "AndroidKeyStoreCipherSpiBase";
    private int mKeymasterPurposeOverride;
    private KeyStoreOperation mOperation = null;
    private boolean mEncrypting = false;
    private AndroidKeyStoreKey mKey = null;
    private SecureRandom mRng = null;
    private long mOperationChallenge = 0;
    private KeyStoreCryptoOperationStreamer mMainDataStreamer = null;
    private KeyStoreCryptoOperationStreamer mAdditionalAuthenticationDataStreamer = null;
    private boolean mAdditionalAuthenticationDataStreamerClosed = false;
    private Exception mCachedException = null;
    private Cipher mCipher = null;

    protected abstract void addAlgorithmSpecificParametersToBegin(List<KeyParameter> list);

    @Override // javax.crypto.CipherSpi
    protected abstract AlgorithmParameters engineGetParameters();

    protected abstract int getAdditionalEntropyAmountForBegin();

    protected abstract int getAdditionalEntropyAmountForFinish();

    protected abstract String getTransform();

    protected abstract void initAlgorithmSpecificParameters() throws InvalidKeyException;

    protected abstract void initAlgorithmSpecificParameters(AlgorithmParameters algorithmParameters) throws InvalidAlgorithmParameterException;

    protected abstract void initAlgorithmSpecificParameters(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException;

    protected abstract void initKey(int i, Key key) throws InvalidKeyException;

    protected abstract void loadAlgorithmSpecificParametersFromBeginResult(KeyParameter[] keyParameterArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AndroidKeyStoreCipherSpiBase() {
        this.mKeymasterPurposeOverride = -1;
        this.mKeymasterPurposeOverride = -1;
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        resetAll();
        if (!(key instanceof AndroidKeyStorePrivateKey) && ((key instanceof PrivateKey) || (key instanceof PublicKey))) {
            try {
                this.mCipher = Cipher.getInstance(getTransform());
                String transform = getTransform();
                if ("RSA/ECB/OAEPWithSHA-224AndMGF1Padding".equals(transform)) {
                    OAEPParameterSpec spec = new OAEPParameterSpec(KeyProperties.DIGEST_SHA224, "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
                    this.mCipher.init(opmode, key, spec, random);
                    return;
                } else if ("RSA/ECB/OAEPWithSHA-256AndMGF1Padding".equals(transform)) {
                    OAEPParameterSpec spec2 = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
                    this.mCipher.init(opmode, key, spec2, random);
                    return;
                } else if ("RSA/ECB/OAEPWithSHA-384AndMGF1Padding".equals(transform)) {
                    OAEPParameterSpec spec3 = new OAEPParameterSpec(KeyProperties.DIGEST_SHA384, "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
                    this.mCipher.init(opmode, key, spec3, random);
                    return;
                } else if ("RSA/ECB/OAEPWithSHA-512AndMGF1Padding".equals(transform)) {
                    OAEPParameterSpec spec4 = new OAEPParameterSpec(KeyProperties.DIGEST_SHA512, "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
                    this.mCipher.init(opmode, key, spec4, random);
                    return;
                } else {
                    this.mCipher.init(opmode, key, random);
                    return;
                }
            } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new InvalidKeyException(e);
            }
        }
        GeneralSecurityException success = null;
        try {
            init(opmode, key, random);
            initAlgorithmSpecificParameters();
            try {
                ensureKeystoreOperationInitialized();
                success = true;
            } catch (InvalidAlgorithmParameterException e2) {
                throw new InvalidKeyException(e2);
            }
        } finally {
            if (success == null) {
                resetAll();
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        resetAll();
        if (!(key instanceof AndroidKeyStorePrivateKey) && ((key instanceof PrivateKey) || (key instanceof PublicKey))) {
            try {
                Cipher cipher = Cipher.getInstance(getTransform());
                this.mCipher = cipher;
                cipher.init(opmode, key, params, random);
                return;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new InvalidKeyException(e);
            }
        }
        GeneralSecurityException success = null;
        try {
            init(opmode, key, random);
            initAlgorithmSpecificParameters(params);
            ensureKeystoreOperationInitialized();
            success = true;
        } finally {
            if (success == null) {
                resetAll();
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        resetAll();
        if (!(key instanceof AndroidKeyStorePrivateKey) && ((key instanceof PrivateKey) || (key instanceof PublicKey))) {
            try {
                Cipher cipher = Cipher.getInstance(getTransform());
                this.mCipher = cipher;
                cipher.init(opmode, key, params, random);
                return;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new InvalidKeyException(e);
            }
        }
        GeneralSecurityException success = null;
        try {
            init(opmode, key, random);
            initAlgorithmSpecificParameters(params);
            ensureKeystoreOperationInitialized();
            success = true;
        } finally {
            if (success == null) {
                resetAll();
            }
        }
    }

    private void init(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        switch (opmode) {
            case 1:
            case 3:
                this.mEncrypting = true;
                break;
            case 2:
            case 4:
                this.mEncrypting = false;
                break;
            default:
                throw new InvalidParameterException("Unsupported opmode: " + opmode);
        }
        initKey(opmode, key);
        if (this.mKey == null) {
            throw new ProviderException("initKey did not initialize the key");
        }
        this.mRng = random;
    }

    private void abortOperation() {
        KeyStoreCryptoOperationUtils.abortOperation(this.mOperation);
        this.mOperation = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetAll() {
        abortOperation();
        this.mEncrypting = false;
        this.mKeymasterPurposeOverride = -1;
        this.mKey = null;
        this.mRng = null;
        this.mOperationChallenge = 0L;
        this.mMainDataStreamer = null;
        this.mAdditionalAuthenticationDataStreamer = null;
        this.mAdditionalAuthenticationDataStreamerClosed = false;
        this.mCachedException = null;
        this.mCipher = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetWhilePreservingInitState() {
        abortOperation();
        this.mOperationChallenge = 0L;
        this.mMainDataStreamer = null;
        this.mAdditionalAuthenticationDataStreamer = null;
        this.mAdditionalAuthenticationDataStreamerClosed = false;
        this.mCachedException = null;
    }

    private void ensureKeystoreOperationInitialized() throws InvalidKeyException, InvalidAlgorithmParameterException {
        int purpose;
        if (this.mMainDataStreamer != null || this.mCachedException != null) {
            return;
        }
        if (this.mKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        List<KeyParameter> parameters = new ArrayList<>();
        addAlgorithmSpecificParametersToBegin(parameters);
        if (this.mKeymasterPurposeOverride != -1) {
            purpose = this.mKeymasterPurposeOverride;
        } else {
            purpose = this.mEncrypting ? 0 : 1;
        }
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870913, purpose));
        try {
            this.mOperation = this.mKey.getSecurityLevel().createOperation(this.mKey.getKeyIdDescriptor(), parameters);
        } catch (KeyStoreException keyStoreException) {
            GeneralSecurityException e = KeyStoreCryptoOperationUtils.getExceptionForCipherInit(this.mKey, keyStoreException);
            if (e != null) {
                if (e instanceof InvalidKeyException) {
                    throw ((InvalidKeyException) e);
                }
                if (e instanceof InvalidAlgorithmParameterException) {
                    throw ((InvalidAlgorithmParameterException) e);
                }
                throw new ProviderException("Unexpected exception type", e);
            }
        }
        this.mOperationChallenge = KeyStoreCryptoOperationUtils.getOrMakeOperationChallenge(this.mOperation, this.mKey);
        loadAlgorithmSpecificParametersFromBeginResult(this.mOperation.getParameters());
        this.mMainDataStreamer = createMainDataStreamer(this.mOperation);
        this.mAdditionalAuthenticationDataStreamer = createAdditionalAuthenticationDataStreamer(this.mOperation);
        this.mAdditionalAuthenticationDataStreamerClosed = false;
    }

    protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStoreOperation operation) {
        return new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(operation), 0);
    }

    protected KeyStoreCryptoOperationStreamer createAdditionalAuthenticationDataStreamer(KeyStoreOperation operation) {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.update(input, inputOffset, inputLen);
        }
        if (this.mCachedException != null) {
            return null;
        }
        try {
            ensureKeystoreOperationInitialized();
            if (inputLen == 0) {
                return null;
            }
            try {
                flushAAD();
                byte[] output = this.mMainDataStreamer.update(input, inputOffset, inputLen);
                if (output.length == 0) {
                    return null;
                }
                return output;
            } catch (KeyStoreException e) {
                this.mCachedException = e;
                return null;
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e2) {
            this.mCachedException = e2;
            return null;
        }
    }

    private void flushAAD() throws KeyStoreException {
        KeyStoreCryptoOperationStreamer keyStoreCryptoOperationStreamer = this.mAdditionalAuthenticationDataStreamer;
        if (keyStoreCryptoOperationStreamer != null && !this.mAdditionalAuthenticationDataStreamerClosed) {
            try {
                byte[] output = keyStoreCryptoOperationStreamer.doFinal(EmptyArray.BYTE, 0, 0, null);
                if (output != null && output.length > 0) {
                    throw new ProviderException("AAD update unexpectedly returned data: " + output.length + " bytes");
                }
            } finally {
                this.mAdditionalAuthenticationDataStreamerClosed = true;
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.update(input, inputOffset, inputLen, output);
        }
        byte[] outputCopy = engineUpdate(input, inputOffset, inputLen);
        if (outputCopy == null) {
            return 0;
        }
        int outputAvailable = output.length - outputOffset;
        if (outputCopy.length <= outputAvailable) {
            System.arraycopy(outputCopy, 0, output, outputOffset, outputCopy.length);
            return outputCopy.length;
        }
        throw new ShortBufferException("Output buffer too short. Produced: " + outputCopy.length + ", available: " + outputAvailable);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineUpdate(ByteBuffer input, ByteBuffer output) throws ShortBufferException {
        byte[] inputArray;
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.update(input, output);
        }
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (output == null) {
            throw new NullPointerException("output == null");
        }
        int inputSize = input.remaining();
        if (input.hasArray()) {
            inputArray = engineUpdate(input.array(), input.arrayOffset() + input.position(), inputSize);
            input.position(input.position() + inputSize);
        } else {
            byte[] outputArray = new byte[inputSize];
            input.get(outputArray);
            inputArray = engineUpdate(outputArray, 0, inputSize);
        }
        int outputSize = inputArray != null ? inputArray.length : 0;
        if (outputSize > 0) {
            int outputBufferAvailable = output.remaining();
            try {
                output.put(inputArray);
            } catch (BufferOverflowException e) {
                throw new ShortBufferException("Output buffer too small. Produced: " + outputSize + ", available: " + outputBufferAvailable);
            }
        }
        return outputSize;
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineUpdateAAD(byte[] input, int inputOffset, int inputLen) {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            cipher.updateAAD(input, inputOffset, inputLen);
        } else if (this.mCachedException != null) {
        } else {
            try {
                ensureKeystoreOperationInitialized();
                if (this.mAdditionalAuthenticationDataStreamerClosed) {
                    throw new IllegalStateException("AAD can only be provided before Cipher.update is invoked");
                }
                KeyStoreCryptoOperationStreamer keyStoreCryptoOperationStreamer = this.mAdditionalAuthenticationDataStreamer;
                if (keyStoreCryptoOperationStreamer == null) {
                    throw new IllegalStateException("This cipher does not support AAD");
                }
                try {
                    byte[] output = keyStoreCryptoOperationStreamer.update(input, inputOffset, inputLen);
                    if (output != null && output.length > 0) {
                        throw new ProviderException("AAD update unexpectedly produced output: " + output.length + " bytes");
                    }
                } catch (KeyStoreException e) {
                    this.mCachedException = e;
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException e2) {
                this.mCachedException = e2;
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineUpdateAAD(ByteBuffer src) {
        byte[] input;
        int inputOffset;
        int inputLen;
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            cipher.updateAAD(src);
        } else if (src == null) {
            throw new IllegalArgumentException("src == null");
        } else {
            if (!src.hasRemaining()) {
                return;
            }
            if (src.hasArray()) {
                input = src.array();
                inputOffset = src.arrayOffset() + src.position();
                inputLen = src.remaining();
                src.position(src.limit());
            } else {
                input = new byte[src.remaining()];
                inputOffset = 0;
                inputLen = input.length;
                src.get(input);
            }
            engineUpdateAAD(input, inputOffset, inputLen);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            if (input == null && inputLen == 0) {
                return cipher.doFinal();
            }
            return cipher.doFinal(input, inputOffset, inputLen);
        } else if (this.mCachedException != null) {
            throw ((IllegalBlockSizeException) new IllegalBlockSizeException().initCause(this.mCachedException));
        } else {
            try {
                ensureKeystoreOperationInitialized();
                try {
                    flushAAD();
                    byte[] output = this.mMainDataStreamer.doFinal(input, inputOffset, inputLen, null);
                    resetWhilePreservingInitState();
                    return output;
                } catch (KeyStoreException e) {
                    switch (e.getErrorCode()) {
                        case -38:
                            throw ((BadPaddingException) new BadPaddingException().initCause(e));
                        case -30:
                            throw ((AEADBadTagException) new AEADBadTagException().initCause(e));
                        default:
                            throw ((IllegalBlockSizeException) new IllegalBlockSizeException().initCause(e));
                    }
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException e2) {
                throw ((IllegalBlockSizeException) new IllegalBlockSizeException().initCause(e2));
            }
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.doFinal(input, inputOffset, inputLen, output);
        }
        byte[] outputCopy = engineDoFinal(input, inputOffset, inputLen);
        if (outputCopy == null) {
            return 0;
        }
        int outputAvailable = output.length - outputOffset;
        if (outputCopy.length <= outputAvailable) {
            System.arraycopy(outputCopy, 0, output, outputOffset, outputCopy.length);
            return outputCopy.length;
        }
        throw new ShortBufferException("Output buffer too short. Produced: " + outputCopy.length + ", available: " + outputAvailable);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineDoFinal(ByteBuffer input, ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] inputArray;
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.doFinal(input, output);
        }
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (output == null) {
            throw new NullPointerException("output == null");
        }
        int inputSize = input.remaining();
        if (input.hasArray()) {
            inputArray = engineDoFinal(input.array(), input.arrayOffset() + input.position(), inputSize);
            input.position(input.position() + inputSize);
        } else {
            byte[] outputArray = new byte[inputSize];
            input.get(outputArray);
            inputArray = engineDoFinal(outputArray, 0, inputSize);
        }
        int outputSize = inputArray != null ? inputArray.length : 0;
        if (outputSize > 0) {
            int outputBufferAvailable = output.remaining();
            try {
                output.put(inputArray);
            } catch (BufferOverflowException e) {
                throw new ShortBufferException("Output buffer too small. Produced: " + outputSize + ", available: " + outputBufferAvailable);
            }
        }
        return outputSize;
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.wrap(key);
        }
        if (this.mKey == null) {
            throw new IllegalStateException("Not initilized");
        }
        if (!isEncrypting()) {
            throw new IllegalStateException("Cipher must be initialized in Cipher.WRAP_MODE to wrap keys");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        byte[] encoded = null;
        if (key instanceof SecretKey) {
            if ("RAW".equalsIgnoreCase(key.getFormat())) {
                encoded = key.getEncoded();
            }
            if (encoded == null) {
                try {
                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(key.getAlgorithm());
                    SecretKeySpec spec = (SecretKeySpec) keyFactory.getKeySpec((SecretKey) key, SecretKeySpec.class);
                    encoded = spec.getEncoded();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new InvalidKeyException("Failed to wrap key because it does not export its key material", e);
                }
            }
        } else if (key instanceof PrivateKey) {
            if ("PKCS8".equalsIgnoreCase(key.getFormat())) {
                encoded = key.getEncoded();
            }
            if (encoded == null) {
                try {
                    KeyFactory keyFactory2 = KeyFactory.getInstance(key.getAlgorithm());
                    PKCS8EncodedKeySpec spec2 = (PKCS8EncodedKeySpec) keyFactory2.getKeySpec(key, PKCS8EncodedKeySpec.class);
                    encoded = spec2.getEncoded();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e2) {
                    throw new InvalidKeyException("Failed to wrap key because it does not export its key material", e2);
                }
            }
        } else if (key instanceof PublicKey) {
            if ("X.509".equalsIgnoreCase(key.getFormat())) {
                encoded = key.getEncoded();
            }
            if (encoded == null) {
                try {
                    KeyFactory keyFactory3 = KeyFactory.getInstance(key.getAlgorithm());
                    X509EncodedKeySpec spec3 = (X509EncodedKeySpec) keyFactory3.getKeySpec(key, X509EncodedKeySpec.class);
                    encoded = spec3.getEncoded();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e3) {
                    throw new InvalidKeyException("Failed to wrap key because it does not export its key material", e3);
                }
            }
        } else {
            throw new InvalidKeyException("Unsupported key type: " + key.getClass().getName());
        }
        if (encoded == null) {
            throw new InvalidKeyException("Failed to wrap key because it does not export its key material");
        }
        try {
            return engineDoFinal(encoded, 0, encoded.length);
        } catch (BadPaddingException e4) {
            throw ((IllegalBlockSizeException) new IllegalBlockSizeException().initCause(e4));
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        Cipher cipher = this.mCipher;
        if (cipher != null) {
            return cipher.unwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
        }
        if (this.mKey == null) {
            throw new IllegalStateException("Not initilized");
        }
        if (isEncrypting()) {
            throw new IllegalStateException("Cipher must be initialized in Cipher.WRAP_MODE to wrap keys");
        }
        if (wrappedKey == null) {
            throw new NullPointerException("wrappedKey == null");
        }
        try {
            byte[] encoded = engineDoFinal(wrappedKey, 0, wrappedKey.length);
            switch (wrappedKeyType) {
                case 1:
                    KeyFactory keyFactory = KeyFactory.getInstance(wrappedKeyAlgorithm);
                    try {
                        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
                    } catch (InvalidKeySpecException e) {
                        throw new InvalidKeyException("Failed to create public key from its X.509 encoded form", e);
                    }
                case 2:
                    KeyFactory keyFactory2 = KeyFactory.getInstance(wrappedKeyAlgorithm);
                    try {
                        return keyFactory2.generatePrivate(new PKCS8EncodedKeySpec(encoded));
                    } catch (InvalidKeySpecException e2) {
                        throw new InvalidKeyException("Failed to create private key from its PKCS#8 encoded form", e2);
                    }
                case 3:
                    return new SecretKeySpec(encoded, wrappedKeyAlgorithm);
                default:
                    throw new InvalidParameterException("Unsupported wrappedKeyType: " + wrappedKeyType);
            }
        } catch (BadPaddingException | IllegalBlockSizeException e3) {
            throw new InvalidKeyException("Failed to unwrap key", e3);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetMode(String mode) throws NoSuchAlgorithmException {
        throw new UnsupportedOperationException();
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetPadding(String arg0) throws NoSuchPaddingException {
        throw new UnsupportedOperationException();
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetKeySize(Key key) throws InvalidKeyException {
        throw new UnsupportedOperationException();
    }

    public void finalize() throws Throwable {
        try {
            abortOperation();
        } finally {
            super.finalize();
        }
    }

    @Override // android.security.keystore.KeyStoreCryptoOperation
    public final long getOperationHandle() {
        return this.mOperationChallenge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setKey(AndroidKeyStoreKey key) {
        this.mKey = key;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setKeymasterPurposeOverride(int keymasterPurpose) {
        this.mKeymasterPurposeOverride = keymasterPurpose;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getKeymasterPurposeOverride() {
        return this.mKeymasterPurposeOverride;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isEncrypting() {
        return this.mEncrypting;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long getConsumedInputSizeBytes() {
        KeyStoreCryptoOperationStreamer keyStoreCryptoOperationStreamer = this.mMainDataStreamer;
        if (keyStoreCryptoOperationStreamer == null) {
            throw new IllegalStateException("Not initialized");
        }
        return keyStoreCryptoOperationStreamer.getConsumedInputSizeBytes();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long getProducedOutputSizeBytes() {
        KeyStoreCryptoOperationStreamer keyStoreCryptoOperationStreamer = this.mMainDataStreamer;
        if (keyStoreCryptoOperationStreamer == null) {
            throw new IllegalStateException("Not initialized");
        }
        return keyStoreCryptoOperationStreamer.getProducedOutputSizeBytes();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String opmodeToString(int opmode) {
        switch (opmode) {
            case 1:
                return "ENCRYPT_MODE";
            case 2:
                return "DECRYPT_MODE";
            case 3:
                return "WRAP_MODE";
            case 4:
                return "UNWRAP_MODE";
            default:
                return String.valueOf(opmode);
        }
    }
}
