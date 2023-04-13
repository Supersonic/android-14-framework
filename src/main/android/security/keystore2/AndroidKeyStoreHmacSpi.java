package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.KeyStoreCryptoOperation;
import android.security.keystore2.KeyStoreCryptoOperationChunkedStreamer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.MacSpi;
/* loaded from: classes3.dex */
public abstract class AndroidKeyStoreHmacSpi extends MacSpi implements KeyStoreCryptoOperation {
    private static final String TAG = "AndroidKeyStoreHmacSpi";
    private final int mKeymasterDigest;
    private final int mMacSizeBits;
    private KeyStoreOperation mOperation = null;
    private long mOperationChallenge = 0;
    private AndroidKeyStoreSecretKey mKey = null;
    private KeyStoreCryptoOperationChunkedStreamer mChunkedStreamer = null;

    /* loaded from: classes3.dex */
    public static class HmacSHA1 extends AndroidKeyStoreHmacSpi {
        public HmacSHA1() {
            super(2);
        }
    }

    /* loaded from: classes3.dex */
    public static class HmacSHA224 extends AndroidKeyStoreHmacSpi {
        public HmacSHA224() {
            super(3);
        }
    }

    /* loaded from: classes3.dex */
    public static class HmacSHA256 extends AndroidKeyStoreHmacSpi {
        public HmacSHA256() {
            super(4);
        }
    }

    /* loaded from: classes3.dex */
    public static class HmacSHA384 extends AndroidKeyStoreHmacSpi {
        public HmacSHA384() {
            super(5);
        }
    }

    /* loaded from: classes3.dex */
    public static class HmacSHA512 extends AndroidKeyStoreHmacSpi {
        public HmacSHA512() {
            super(6);
        }
    }

    protected AndroidKeyStoreHmacSpi(int keymasterDigest) {
        this.mKeymasterDigest = keymasterDigest;
        this.mMacSizeBits = KeymasterUtils.getDigestOutputSizeBits(keymasterDigest);
    }

    @Override // javax.crypto.MacSpi
    protected int engineGetMacLength() {
        return (this.mMacSizeBits + 7) / 8;
    }

    @Override // javax.crypto.MacSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        resetAll();
        boolean success = false;
        try {
            init(key, params);
            ensureKeystoreOperationInitialized();
            success = true;
        } finally {
            if (!success) {
                resetAll();
            }
        }
    }

    private void init(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        if (!(key instanceof AndroidKeyStoreSecretKey)) {
            throw new InvalidKeyException("Only Android KeyStore secret keys supported. Key: " + key);
        }
        this.mKey = (AndroidKeyStoreSecretKey) key;
        if (params != null) {
            throw new InvalidAlgorithmParameterException("Unsupported algorithm parameters: " + params);
        }
    }

    private void abortOperation() {
        KeyStoreCryptoOperationUtils.abortOperation(this.mOperation);
        this.mOperation = null;
    }

    private void resetAll() {
        abortOperation();
        this.mOperationChallenge = 0L;
        this.mKey = null;
        this.mChunkedStreamer = null;
    }

    private void resetWhilePreservingInitState() {
        abortOperation();
        this.mOperationChallenge = 0L;
        this.mChunkedStreamer = null;
    }

    @Override // javax.crypto.MacSpi
    protected void engineReset() {
        resetWhilePreservingInitState();
    }

    private void ensureKeystoreOperationInitialized() throws InvalidKeyException {
        if (this.mChunkedStreamer != null) {
            return;
        }
        if (this.mKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        List<KeyParameter> parameters = new ArrayList<>();
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870913, 2));
        parameters.add(KeyStore2ParameterUtils.makeEnum(268435458, 128));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870917, this.mKeymasterDigest));
        parameters.add(KeyStore2ParameterUtils.makeInt(805307371, this.mMacSizeBits));
        try {
            this.mOperation = this.mKey.getSecurityLevel().createOperation(this.mKey.getKeyIdDescriptor(), parameters);
        } catch (KeyStoreException keyStoreException) {
            InvalidKeyException e = KeyStoreCryptoOperationUtils.getInvalidKeyException(this.mKey, keyStoreException);
            if (e != null) {
                throw e;
            }
        }
        this.mOperationChallenge = KeyStoreCryptoOperationUtils.getOrMakeOperationChallenge(this.mOperation, this.mKey);
        this.mChunkedStreamer = new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(this.mOperation));
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte input) {
        engineUpdate(new byte[]{input}, 0, 1);
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte[] input, int offset, int len) {
        try {
            ensureKeystoreOperationInitialized();
            try {
                byte[] output = this.mChunkedStreamer.update(input, offset, len);
                if (output != null && output.length != 0) {
                    throw new ProviderException("Update operation unexpectedly produced output");
                }
            } catch (KeyStoreException e) {
                throw new ProviderException("Keystore operation failed", e);
            }
        } catch (InvalidKeyException e2) {
            throw new ProviderException("Failed to reinitialize MAC", e2);
        }
    }

    @Override // javax.crypto.MacSpi
    protected byte[] engineDoFinal() {
        try {
            ensureKeystoreOperationInitialized();
            try {
                byte[] result = this.mChunkedStreamer.doFinal(null, 0, 0, null);
                resetWhilePreservingInitState();
                return result;
            } catch (KeyStoreException e) {
                throw new ProviderException("Keystore operation failed", e);
            }
        } catch (InvalidKeyException e2) {
            throw new ProviderException("Failed to reinitialize MAC", e2);
        }
    }

    public void finalize() throws Throwable {
        try {
            abortOperation();
        } finally {
            super.finalize();
        }
    }

    @Override // android.security.keystore.KeyStoreCryptoOperation
    public long getOperationHandle() {
        return this.mOperationChallenge;
    }
}
