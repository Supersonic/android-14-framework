package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.ArrayUtils;
import android.security.keystore.KeyStoreCryptoOperation;
import android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.util.ArrayList;
import java.util.List;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
abstract class AndroidKeyStoreSignatureSpiBase extends SignatureSpi implements KeyStoreCryptoOperation {
    private static final String TAG = "AndroidKeyStoreSignatureSpiBase";
    private Exception mCachedException;
    private KeyStoreCryptoOperationStreamer mMessageStreamer;
    private Signature mSignature;
    private KeyStoreOperation mOperation = null;
    private long mOperationChallenge = 0;
    private boolean mSigning = false;
    private AndroidKeyStoreKey mKey = null;

    protected abstract void addAlgorithmSpecificParametersToBegin(List<KeyParameter> list);

    protected abstract int getAdditionalEntropyAmountForSign();

    protected abstract String getAlgorithm();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AndroidKeyStoreSignatureSpiBase() {
        this.appRandom = null;
        this.mMessageStreamer = null;
        this.mCachedException = null;
        this.mSignature = null;
    }

    @Override // java.security.SignatureSpi
    protected final void engineInitSign(PrivateKey key) throws InvalidKeyException {
        engineInitSign(key, null);
    }

    @Override // java.security.SignatureSpi
    protected final void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        resetAll();
        try {
            if (privateKey == null) {
                throw new InvalidKeyException("Unsupported key: null");
            }
            if (privateKey instanceof AndroidKeyStorePrivateKey) {
                AndroidKeyStoreKey keystoreKey = (AndroidKeyStoreKey) privateKey;
                this.mSigning = true;
                initKey(keystoreKey);
                this.appRandom = random;
                ensureKeystoreOperationInitialized();
                if (1 == 0) {
                    resetAll();
                    return;
                }
                return;
            }
            throw new InvalidKeyException("Unsupported private key type: " + privateKey);
        } catch (Throwable th) {
            if (0 == 0) {
                resetAll();
            }
            throw th;
        }
    }

    @Override // java.security.SignatureSpi
    protected final void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        resetAll();
        try {
            Signature signature = Signature.getInstance(getAlgorithm());
            this.mSignature = signature;
            signature.initVerify(publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidKeyException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initKey(AndroidKeyStoreKey key) throws InvalidKeyException {
        this.mKey = key;
    }

    private void abortOperation() {
        KeyStoreCryptoOperationUtils.abortOperation(this.mOperation);
        this.mOperation = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetAll() {
        abortOperation();
        this.mOperationChallenge = 0L;
        this.mSigning = false;
        this.mKey = null;
        this.appRandom = null;
        this.mMessageStreamer = null;
        this.mCachedException = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetWhilePreservingInitState() {
        abortOperation();
        this.mOperationChallenge = 0L;
        this.mMessageStreamer = null;
        this.mCachedException = null;
    }

    private void ensureKeystoreOperationInitialized() throws InvalidKeyException {
        if (this.mMessageStreamer != null || this.mCachedException != null) {
            return;
        }
        if (this.mKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        List<KeyParameter> parameters = new ArrayList<>();
        addAlgorithmSpecificParametersToBegin(parameters);
        int purpose = this.mSigning ? 2 : 3;
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870913, purpose));
        try {
            KeyStoreOperation createOperation = this.mKey.getSecurityLevel().createOperation(this.mKey.getKeyIdDescriptor(), parameters);
            this.mOperation = createOperation;
            this.mOperationChallenge = KeyStoreCryptoOperationUtils.getOrMakeOperationChallenge(createOperation, this.mKey);
            this.mMessageStreamer = createMainDataStreamer(this.mOperation);
        } catch (KeyStoreException keyStoreException) {
            throw KeyStoreCryptoOperationUtils.getInvalidKeyException(this.mKey, keyStoreException);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStoreOperation operation) {
        return new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(operation));
    }

    @Override // android.security.keystore.KeyStoreCryptoOperation
    public final long getOperationHandle() {
        return this.mOperationChallenge;
    }

    @Override // java.security.SignatureSpi
    protected final void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        Signature signature = this.mSignature;
        if (signature != null) {
            signature.update(b, off, len);
        } else if (this.mCachedException != null) {
            throw new SignatureException(this.mCachedException);
        } else {
            try {
                ensureKeystoreOperationInitialized();
                if (len == 0) {
                    return;
                }
                try {
                    byte[] output = this.mMessageStreamer.update(b, off, len);
                    if (output.length != 0) {
                        throw new ProviderException("Update operation unexpectedly produced output: " + output.length + " bytes");
                    }
                } catch (KeyStoreException e) {
                    throw new SignatureException(e);
                }
            } catch (InvalidKeyException e2) {
                throw new SignatureException(e2);
            }
        }
    }

    @Override // java.security.SignatureSpi
    protected final void engineUpdate(byte b) throws SignatureException {
        engineUpdate(new byte[]{b}, 0, 1);
    }

    @Override // java.security.SignatureSpi
    protected final void engineUpdate(ByteBuffer input) {
        byte[] b;
        int off;
        int len = input.remaining();
        if (input.hasArray()) {
            b = input.array();
            off = input.arrayOffset() + input.position();
            input.position(input.limit());
        } else {
            b = new byte[len];
            off = 0;
            input.get(b);
        }
        try {
            engineUpdate(b, off, len);
        } catch (SignatureException e) {
            this.mCachedException = e;
        }
    }

    @Override // java.security.SignatureSpi
    protected final int engineSign(byte[] out, int outOffset, int outLen) throws SignatureException {
        return super.engineSign(out, outOffset, outLen);
    }

    @Override // java.security.SignatureSpi
    protected final byte[] engineSign() throws SignatureException {
        if (this.mCachedException != null) {
            throw new SignatureException(this.mCachedException);
        }
        try {
            ensureKeystoreOperationInitialized();
            KeyStoreCryptoOperationUtils.getRandomBytesToMixIntoKeystoreRng(this.appRandom, getAdditionalEntropyAmountForSign());
            byte[] signature = this.mMessageStreamer.doFinal(EmptyArray.BYTE, 0, 0, null);
            resetWhilePreservingInitState();
            return signature;
        } catch (KeyStoreException | InvalidKeyException e) {
            throw new SignatureException(e);
        }
    }

    @Override // java.security.SignatureSpi
    protected final boolean engineVerify(byte[] signature) throws SignatureException {
        Signature signature2 = this.mSignature;
        if (signature2 != null) {
            return signature2.verify(signature);
        }
        throw new IllegalStateException("Not initialised.");
    }

    @Override // java.security.SignatureSpi
    protected final boolean engineVerify(byte[] sigBytes, int offset, int len) throws SignatureException {
        return engineVerify(ArrayUtils.subarray(sigBytes, offset, len));
    }

    @Override // java.security.SignatureSpi
    @Deprecated
    protected final Object engineGetParameter(String param) throws InvalidParameterException {
        throw new InvalidParameterException();
    }

    @Override // java.security.SignatureSpi
    @Deprecated
    protected final void engineSetParameter(String param, Object value) throws InvalidParameterException {
        throw new InvalidParameterException();
    }

    protected final boolean isSigning() {
        return this.mSigning;
    }
}
