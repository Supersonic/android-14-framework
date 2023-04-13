package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.KeyStoreCryptoOperation;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes3.dex */
public class AndroidKeyStoreKeyAgreementSpi extends KeyAgreementSpi implements KeyStoreCryptoOperation {
    private static final String TAG = "AndroidKeyStoreKeyAgreementSpi";
    private AndroidKeyStorePrivateKey mKey;
    private final int mKeymintAlgorithm;
    private KeyStoreOperation mOperation;
    private long mOperationHandle;
    private PublicKey mOtherPartyKey;

    /* loaded from: classes3.dex */
    public static class ECDH extends AndroidKeyStoreKeyAgreementSpi {
        public ECDH() {
            super(3);
        }
    }

    /* loaded from: classes3.dex */
    public static class XDH extends AndroidKeyStoreKeyAgreementSpi {
        public XDH() {
            super(3);
        }
    }

    protected AndroidKeyStoreKeyAgreementSpi(int keymintAlgorithm) {
        resetAll();
        this.mKeymintAlgorithm = keymintAlgorithm;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, SecureRandom random) throws InvalidKeyException {
        resetAll();
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        if (!(key instanceof AndroidKeyStorePrivateKey)) {
            throw new InvalidKeyException("Only Android KeyStore private keys supported. Key: " + key);
        }
        this.mKey = (AndroidKeyStorePrivateKey) key;
        boolean success = false;
        try {
            ensureKeystoreOperationInitialized();
            success = true;
        } finally {
            if (!success) {
                resetAll();
            }
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("Unsupported algorithm parameters: " + params);
        }
        engineInit(key, random);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected Key engineDoPhase(Key key, boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        ensureKeystoreOperationInitialized();
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("Only public keys supported. Key: " + key);
        }
        AndroidKeyStorePrivateKey androidKeyStorePrivateKey = this.mKey;
        if ((androidKeyStorePrivateKey instanceof ECKey) && !(key instanceof ECKey)) {
            throw new InvalidKeyException("Public and Private key should be of the same type.");
        }
        if ((androidKeyStorePrivateKey instanceof ECKey) && !((ECKey) key).getParams().getCurve().equals(((ECKey) this.mKey).getParams().getCurve())) {
            throw new InvalidKeyException("Public and Private key parameters should be same.");
        }
        if (!lastPhase) {
            throw new IllegalStateException("Only one other party supported. lastPhase must be set to true.");
        }
        if (this.mOtherPartyKey != null) {
            throw new IllegalStateException("Only one other party supported. doPhase() must only be called exactly once.");
        }
        this.mOtherPartyKey = (PublicKey) key;
        return null;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        try {
            ensureKeystoreOperationInitialized();
            PublicKey publicKey = this.mOtherPartyKey;
            if (publicKey == null) {
                throw new IllegalStateException("Other party key not provided. Call doPhase() first.");
            }
            byte[] otherPartyKeyEncoded = publicKey.getEncoded();
            try {
                try {
                    return this.mOperation.finish(otherPartyKeyEncoded, null);
                } catch (KeyStoreException e) {
                    throw new ProviderException("Keystore operation failed", e);
                }
            } finally {
                resetWhilePreservingInitState();
            }
        } catch (InvalidKeyException e2) {
            throw new IllegalStateException("Not initialized", e2);
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected SecretKey engineGenerateSecret(String algorithm) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] generatedSecret = engineGenerateSecret();
        return new SecretKeySpec(generatedSecret, algorithm);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected int engineGenerateSecret(byte[] sharedSecret, int offset) throws IllegalStateException, ShortBufferException {
        byte[] generatedSecret = engineGenerateSecret();
        if (generatedSecret.length > sharedSecret.length - offset) {
            throw new ShortBufferException("Needed: " + generatedSecret.length);
        }
        System.arraycopy(generatedSecret, 0, sharedSecret, offset, generatedSecret.length);
        return generatedSecret.length;
    }

    @Override // android.security.keystore.KeyStoreCryptoOperation
    public long getOperationHandle() {
        return this.mOperationHandle;
    }

    protected void finalize() throws Throwable {
        try {
            resetAll();
        } finally {
            super.finalize();
        }
    }

    private void resetWhilePreservingInitState() {
        KeyStoreCryptoOperationUtils.abortOperation(this.mOperation);
        this.mOperationHandle = 0L;
        this.mOperation = null;
        this.mOtherPartyKey = null;
    }

    private void resetAll() {
        resetWhilePreservingInitState();
        this.mKey = null;
    }

    private void ensureKeystoreOperationInitialized() throws InvalidKeyException, IllegalStateException {
        if (this.mKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        if (this.mOperation != null) {
            return;
        }
        List<KeyParameter> parameters = new ArrayList<>();
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870913, 6));
        try {
            this.mOperation = this.mKey.getSecurityLevel().createOperation(this.mKey.getKeyIdDescriptor(), parameters);
        } catch (KeyStoreException keyStoreException) {
            InvalidKeyException e = KeyStoreCryptoOperationUtils.getInvalidKeyException(this.mKey, keyStoreException);
            if (e != null) {
                throw e;
            }
        }
        this.mOperationHandle = KeyStoreCryptoOperationUtils.getOrMakeOperationChallenge(this.mOperation, this.mKey);
    }
}
