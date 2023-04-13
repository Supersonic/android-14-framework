package android.security.identity;

import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.security.KeyChain;
import android.security.identity.CredstoreResultData;
import android.security.keystore.KeyProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes3.dex */
class CredstoreIdentityCredential extends IdentityCredential {
    private static final String TAG = "CredstoreIdentityCredential";
    private ICredential mBinder;
    private int mCipherSuite;
    private Context mContext;
    private String mCredentialName;
    private int mEphemeralCounter;
    private int mFeatureVersion;
    private int mReadersExpectedEphemeralCounter;
    private CredstorePresentationSession mSession;
    private KeyPair mEphemeralKeyPair = null;
    private SecretKey mSecretKey = null;
    private SecretKey mReaderSecretKey = null;
    private boolean mAllowUsingExhaustedKeys = true;
    private boolean mAllowUsingExpiredKeys = false;
    private boolean mIncrementKeyUsageCount = true;
    private boolean mOperationHandleSet = false;
    private long mOperationHandle = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CredstoreIdentityCredential(Context context, String credentialName, int cipherSuite, ICredential binder, CredstorePresentationSession session, int featureVersion) {
        this.mContext = context;
        this.mCredentialName = credentialName;
        this.mCipherSuite = cipherSuite;
        this.mBinder = binder;
        this.mSession = session;
        this.mFeatureVersion = featureVersion;
    }

    private void ensureEphemeralKeyPair() {
        if (this.mEphemeralKeyPair != null) {
            return;
        }
        try {
            byte[] pkcs12 = this.mBinder.createEphemeralKeyPair();
            char[] password = new char[0];
            KeyStore ks = KeyStore.getInstance(KeyChain.EXTRA_PKCS12);
            ByteArrayInputStream bais = new ByteArrayInputStream(pkcs12);
            ks.load(bais, password);
            PrivateKey privKey = (PrivateKey) ks.getKey("ephemeralKey", password);
            Certificate cert = ks.getCertificate("ephemeralKey");
            PublicKey pubKey = cert.getPublicKey();
            this.mEphemeralKeyPair = new KeyPair(pubKey, privKey);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e3) {
            throw new RuntimeException("Unexpected exception ", e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public KeyPair createEphemeralKeyPair() {
        ensureEphemeralKeyPair();
        return this.mEphemeralKeyPair;
    }

    @Override // android.security.identity.IdentityCredential
    public void setReaderEphemeralPublicKey(PublicKey readerEphemeralPublicKey) throws InvalidKeyException {
        try {
            byte[] uncompressedForm = Util.publicKeyEncodeUncompressedForm(readerEphemeralPublicKey);
            this.mBinder.setReaderEphemeralPublicKey(uncompressedForm);
            ensureEphemeralKeyPair();
            try {
                KeyAgreement ka = KeyAgreement.getInstance("ECDH");
                ka.init(this.mEphemeralKeyPair.getPrivate());
                ka.doPhase(readerEphemeralPublicKey, true);
                byte[] sharedSecret = ka.generateSecret();
                byte[] info = new byte[0];
                byte[] salt = {1};
                byte[] derivedKey = Util.computeHkdf("HmacSha256", sharedSecret, salt, info, 32);
                this.mSecretKey = new SecretKeySpec(derivedKey, KeyProperties.KEY_ALGORITHM_AES);
                salt[0] = 0;
                byte[] derivedKey2 = Util.computeHkdf("HmacSha256", sharedSecret, salt, info, 32);
                this.mReaderSecretKey = new SecretKeySpec(derivedKey2, KeyProperties.KEY_ALGORITHM_AES);
                this.mEphemeralCounter = 1;
                this.mReadersExpectedEphemeralCounter = 1;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error performing key agreement", e);
            }
        } catch (RemoteException e2) {
            throw new RuntimeException("Unexpected RemoteException ", e2);
        } catch (ServiceSpecificException e3) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e3.errorCode, e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public byte[] encryptMessageToReader(byte[] messagePlaintext) {
        try {
            ByteBuffer iv = ByteBuffer.allocate(12);
            iv.putInt(0, 0);
            iv.putInt(4, 1);
            iv.putInt(8, this.mEphemeralCounter);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec encryptionParameterSpec = new GCMParameterSpec(128, iv.array());
            cipher.init(1, this.mSecretKey, encryptionParameterSpec);
            byte[] messageCiphertextAndAuthTag = cipher.doFinal(messagePlaintext);
            this.mEphemeralCounter++;
            return messageCiphertextAndAuthTag;
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new RuntimeException("Error encrypting message", e);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public byte[] decryptMessageFromReader(byte[] messageCiphertext) throws MessageDecryptionException {
        ByteBuffer iv = ByteBuffer.allocate(12);
        iv.putInt(0, 0);
        iv.putInt(4, 0);
        iv.putInt(8, this.mReadersExpectedEphemeralCounter);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(2, this.mReaderSecretKey, new GCMParameterSpec(128, iv.array()));
            byte[] plainText = cipher.doFinal(messageCiphertext);
            this.mReadersExpectedEphemeralCounter++;
            return plainText;
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new MessageDecryptionException("Error decrypting message", e);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public Collection<X509Certificate> getCredentialKeyCertificateChain() {
        try {
            byte[] certsBlob = this.mBinder.getCredentialKeyCertificateChain();
            ByteArrayInputStream bais = new ByteArrayInputStream(certsBlob);
            try {
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> certs = factory.generateCertificates(bais);
                ArrayList<X509Certificate> x509Certs = new ArrayList<>();
                for (Certificate cert : certs) {
                    x509Certs.add((X509Certificate) cert);
                }
                return x509Certs;
            } catch (CertificateException e) {
                throw new RuntimeException("Error decoding certificates", e);
            }
        } catch (RemoteException e2) {
            throw new RuntimeException("Unexpected RemoteException ", e2);
        } catch (ServiceSpecificException e3) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e3.errorCode, e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public void setAllowUsingExhaustedKeys(boolean allowUsingExhaustedKeys) {
        this.mAllowUsingExhaustedKeys = allowUsingExhaustedKeys;
    }

    @Override // android.security.identity.IdentityCredential
    public void setAllowUsingExpiredKeys(boolean allowUsingExpiredKeys) {
        this.mAllowUsingExpiredKeys = allowUsingExpiredKeys;
    }

    @Override // android.security.identity.IdentityCredential
    public void setIncrementKeyUsageCount(boolean incrementKeyUsageCount) {
        this.mIncrementKeyUsageCount = incrementKeyUsageCount;
    }

    @Override // android.security.identity.IdentityCredential
    public long getCredstoreOperationHandle() {
        if (!this.mOperationHandleSet) {
            try {
                this.mOperationHandle = this.mBinder.selectAuthKey(this.mAllowUsingExhaustedKeys, this.mAllowUsingExpiredKeys, this.mIncrementKeyUsageCount);
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

    @Override // android.security.identity.IdentityCredential
    public ResultData getEntries(byte[] requestMessage, Map<String, Collection<String>> entriesToRequest, byte[] sessionTranscript, byte[] readerSignature) throws SessionTranscriptMismatchException, NoAuthenticationKeyAvailableException, InvalidReaderSignatureException, EphemeralPublicKeyNotFoundException, InvalidRequestMessageException {
        byte[] mac;
        GetEntriesResultParcel resultParcel;
        byte[] signature;
        RequestNamespaceParcel[] rnsParcels = new RequestNamespaceParcel[entriesToRequest.size()];
        int n = 0;
        for (String namespaceName : entriesToRequest.keySet()) {
            Collection<String> entryNames = entriesToRequest.get(namespaceName);
            rnsParcels[n] = new RequestNamespaceParcel();
            rnsParcels[n].namespaceName = namespaceName;
            rnsParcels[n].entries = new RequestEntryParcel[entryNames.size()];
            int m = 0;
            for (String entryName : entryNames) {
                rnsParcels[n].entries[m] = new RequestEntryParcel();
                rnsParcels[n].entries[m].name = entryName;
                m++;
            }
            n++;
        }
        try {
            int i = 0;
            GetEntriesResultParcel resultParcel2 = this.mBinder.getEntries(requestMessage != null ? requestMessage : new byte[0], rnsParcels, sessionTranscript != null ? sessionTranscript : new byte[0], readerSignature != null ? readerSignature : new byte[0], this.mAllowUsingExhaustedKeys, this.mAllowUsingExpiredKeys, this.mIncrementKeyUsageCount);
            byte[] signature2 = resultParcel2.signature;
            if (signature2 != null && signature2.length == 0) {
                signature2 = null;
            }
            byte[] mac2 = resultParcel2.mac;
            if (mac2 != null && mac2.length == 0) {
                mac = null;
            } else {
                mac = mac2;
            }
            CredstoreResultData.Builder resultDataBuilder = new CredstoreResultData.Builder(this.mFeatureVersion, resultParcel2.staticAuthenticationData, resultParcel2.deviceNameSpaces, mac, signature2);
            ResultNamespaceParcel[] resultNamespaceParcelArr = resultParcel2.resultNamespaces;
            int length = resultNamespaceParcelArr.length;
            int i2 = 0;
            while (i2 < length) {
                ResultNamespaceParcel resultNamespaceParcel = resultNamespaceParcelArr[i2];
                ResultEntryParcel[] resultEntryParcelArr = resultNamespaceParcel.entries;
                int length2 = resultEntryParcelArr.length;
                int i3 = i;
                while (i3 < length2) {
                    ResultEntryParcel resultEntryParcel = resultEntryParcelArr[i3];
                    if (resultEntryParcel.status == 0) {
                        resultParcel = resultParcel2;
                        signature = signature2;
                        resultDataBuilder.addEntry(resultNamespaceParcel.namespaceName, resultEntryParcel.name, resultEntryParcel.value);
                    } else {
                        resultParcel = resultParcel2;
                        signature = signature2;
                        resultDataBuilder.addErrorStatus(resultNamespaceParcel.namespaceName, resultEntryParcel.name, resultEntryParcel.status);
                    }
                    i3++;
                    resultParcel2 = resultParcel;
                    signature2 = signature;
                }
                i2++;
                i = 0;
            }
            return resultDataBuilder.build();
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 5) {
                throw new EphemeralPublicKeyNotFoundException(e2.getMessage(), e2);
            }
            if (e2.errorCode == 7) {
                throw new InvalidReaderSignatureException(e2.getMessage(), e2);
            }
            if (e2.errorCode == 6) {
                throw new NoAuthenticationKeyAvailableException(e2.getMessage(), e2);
            }
            if (e2.errorCode == 10) {
                throw new InvalidRequestMessageException(e2.getMessage(), e2);
            }
            if (e2.errorCode == 11) {
                throw new SessionTranscriptMismatchException(e2.getMessage(), e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public void setAvailableAuthenticationKeys(int keyCount, int maxUsesPerKey) {
        setAvailableAuthenticationKeys(keyCount, maxUsesPerKey, 0L);
    }

    @Override // android.security.identity.IdentityCredential
    public void setAvailableAuthenticationKeys(int keyCount, int maxUsesPerKey, long minValidTimeMillis) {
        try {
            this.mBinder.setAvailableAuthenticationKeys(keyCount, maxUsesPerKey, minValidTimeMillis);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public Collection<X509Certificate> getAuthKeysNeedingCertification() {
        try {
            AuthKeyParcel[] authKeyParcels = this.mBinder.getAuthKeysNeedingCertification();
            ArrayList<X509Certificate> x509Certs = new ArrayList<>();
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            for (AuthKeyParcel authKeyParcel : authKeyParcels) {
                ByteArrayInputStream bais = new ByteArrayInputStream(authKeyParcel.x509cert);
                Collection<? extends Certificate> certs = factory.generateCertificates(bais);
                if (certs.size() != 1) {
                    throw new RuntimeException("Returned blob yields more than one X509 cert");
                }
                X509Certificate authKeyCert = (X509Certificate) certs.iterator().next();
                x509Certs.add(authKeyCert);
            }
            return x509Certs;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        } catch (CertificateException e3) {
            throw new RuntimeException("Error decoding authenticationKey", e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public void storeStaticAuthenticationData(X509Certificate authenticationKey, byte[] staticAuthData) throws UnknownAuthenticationKeyException {
        try {
            AuthKeyParcel authKeyParcel = new AuthKeyParcel();
            authKeyParcel.x509cert = authenticationKey.getEncoded();
            this.mBinder.storeStaticAuthenticationData(authKeyParcel, staticAuthData);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 9) {
                throw new UnknownAuthenticationKeyException(e2.getMessage(), e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        } catch (CertificateEncodingException e3) {
            throw new RuntimeException("Error encoding authenticationKey", e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public void storeStaticAuthenticationData(X509Certificate authenticationKey, Instant expirationDate, byte[] staticAuthData) throws UnknownAuthenticationKeyException {
        try {
            AuthKeyParcel authKeyParcel = new AuthKeyParcel();
            authKeyParcel.x509cert = authenticationKey.getEncoded();
            long millisSinceEpoch = (expirationDate.getEpochSecond() * 1000) + (expirationDate.getNano() / 1000000);
            this.mBinder.storeStaticAuthenticationDataWithExpiration(authKeyParcel, millisSinceEpoch, staticAuthData);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 12) {
                throw new UnsupportedOperationException("Not supported", e2);
            }
            if (e2.errorCode == 9) {
                throw new UnknownAuthenticationKeyException(e2.getMessage(), e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        } catch (CertificateEncodingException e3) {
            throw new RuntimeException("Error encoding authenticationKey", e3);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public int[] getAuthenticationDataUsageCount() {
        try {
            int[] usageCount = this.mBinder.getAuthenticationDataUsageCount();
            return usageCount;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public List<AuthenticationKeyMetadata> getAuthenticationKeyMetadata() {
        try {
            int[] usageCount = this.mBinder.getAuthenticationDataUsageCount();
            long[] expirationsMillis = this.mBinder.getAuthenticationDataExpirations();
            if (usageCount.length != expirationsMillis.length) {
                throw new IllegalStateException("Size og usageCount and expirationMillis differ");
            }
            List<AuthenticationKeyMetadata> mds = new ArrayList<>();
            for (int n = 0; n < expirationsMillis.length; n++) {
                AuthenticationKeyMetadata md = null;
                long expirationMillis = expirationsMillis[n];
                if (expirationMillis != Long.MAX_VALUE) {
                    md = new AuthenticationKeyMetadata(usageCount[n], Instant.ofEpochMilli(expirationMillis));
                }
                mds.add(md);
            }
            return mds;
        } catch (RemoteException e) {
            throw new IllegalStateException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new IllegalStateException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public byte[] proveOwnership(byte[] challenge) {
        try {
            byte[] proofOfOwnership = this.mBinder.proveOwnership(challenge);
            return proofOfOwnership;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 12) {
                throw new UnsupportedOperationException("Not supported", e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public byte[] delete(byte[] challenge) {
        try {
            byte[] proofOfDeletion = this.mBinder.deleteWithChallenge(challenge);
            return proofOfDeletion;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredential
    public byte[] update(PersonalizationData personalizationData) {
        try {
            IWritableCredential binder = this.mBinder.update();
            byte[] proofOfProvision = CredstoreWritableIdentityCredential.personalize(binder, personalizationData);
            return proofOfProvision;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }
}
