package android.security.identity;

import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.security.KeyChain;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class CredstorePresentationSession extends PresentationSession {
    private static final String TAG = "CredstorePresentationSession";
    private ISession mBinder;
    private int mCipherSuite;
    private Context mContext;
    private int mFeatureVersion;
    private CredstoreIdentityCredentialStore mStore;
    private Map<String, CredstoreIdentityCredential> mCredentialCache = new LinkedHashMap();
    private KeyPair mEphemeralKeyPair = null;
    private byte[] mSessionTranscript = null;
    private boolean mOperationHandleSet = false;
    private long mOperationHandle = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CredstorePresentationSession(Context context, int cipherSuite, CredstoreIdentityCredentialStore store, ISession binder, int featureVersion) {
        this.mFeatureVersion = 0;
        this.mContext = context;
        this.mCipherSuite = cipherSuite;
        this.mStore = store;
        this.mBinder = binder;
        this.mFeatureVersion = featureVersion;
    }

    private void ensureEphemeralKeyPair() {
        if (this.mEphemeralKeyPair != null) {
            return;
        }
        try {
            byte[] pkcs12 = this.mBinder.getEphemeralKeyPair();
            char[] password = new char[0];
            KeyStore ks = KeyStore.getInstance(KeyChain.EXTRA_PKCS12);
            ByteArrayInputStream bais = new ByteArrayInputStream(pkcs12);
            ks.load(bais, password);
            PrivateKey privKey = (PrivateKey) ks.getKey("ephemeralKey", password);
            Certificate cert = ks.getCertificate("ephemeralKey");
            PublicKey pubKey = cert.getPublicKey();
            this.mEphemeralKeyPair = new KeyPair(pubKey, privKey);
        } catch (RemoteException | IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e) {
            throw new RuntimeException("Unexpected exception ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.PresentationSession
    public KeyPair getEphemeralKeyPair() {
        ensureEphemeralKeyPair();
        return this.mEphemeralKeyPair;
    }

    @Override // android.security.identity.PresentationSession
    public void setReaderEphemeralPublicKey(PublicKey readerEphemeralPublicKey) throws InvalidKeyException {
        try {
            byte[] uncompressedForm = Util.publicKeyEncodeUncompressedForm(readerEphemeralPublicKey);
            this.mBinder.setReaderEphemeralPublicKey(uncompressedForm);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.PresentationSession
    public void setSessionTranscript(byte[] sessionTranscript) {
        try {
            this.mBinder.setSessionTranscript(sessionTranscript);
            this.mSessionTranscript = sessionTranscript;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.PresentationSession
    public CredentialDataResult getCredentialData(String credentialName, CredentialDataRequest request) throws NoAuthenticationKeyAvailableException, InvalidReaderSignatureException, InvalidRequestMessageException, EphemeralPublicKeyNotFoundException {
        try {
            CredstoreIdentityCredential credential = this.mCredentialCache.get(credentialName);
            if (credential == null) {
                ICredential credstoreCredential = this.mBinder.getCredentialForPresentation(credentialName);
                credential = new CredstoreIdentityCredential(this.mContext, credentialName, this.mCipherSuite, credstoreCredential, this, this.mFeatureVersion);
                this.mCredentialCache.put(credentialName, credential);
                credential.setAllowUsingExhaustedKeys(request.isAllowUsingExhaustedKeys());
                credential.setAllowUsingExpiredKeys(request.isAllowUsingExpiredKeys());
                credential.setIncrementKeyUsageCount(request.isIncrementUseCount());
            }
            ResultData deviceSignedResult = credential.getEntries(request.getRequestMessage(), request.getDeviceSignedEntriesToRequest(), this.mSessionTranscript, request.getReaderSignature());
            ResultData issuerSignedResult = credential.getEntries(request.getRequestMessage(), request.getIssuerSignedEntriesToRequest(), this.mSessionTranscript, request.getReaderSignature());
            return new CredstoreCredentialDataResult(deviceSignedResult, issuerSignedResult);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                return null;
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        } catch (SessionTranscriptMismatchException e3) {
            throw new RuntimeException("Unexpected ", e3);
        }
    }

    @Override // android.security.identity.PresentationSession
    public long getCredstoreOperationHandle() {
        if (!this.mOperationHandleSet) {
            try {
                this.mOperationHandle = this.mBinder.getAuthChallenge();
                this.mOperationHandleSet = true;
            } catch (RemoteException e) {
                throw new RuntimeException("Unexpected RemoteException ", e);
            } catch (ServiceSpecificException e2) {
                int i = e2.errorCode;
                throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
            }
        }
        return this.mOperationHandle;
    }
}
