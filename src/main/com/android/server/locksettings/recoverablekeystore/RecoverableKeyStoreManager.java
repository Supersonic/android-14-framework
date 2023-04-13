package com.android.server.locksettings.recoverablekeystore;

import android.app.PendingIntent;
import android.app.RemoteLockscreenValidationResult;
import android.app.StartLockscreenValidationRequest;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.os.UserHandle;
import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.RecoveryCertPath;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.util.ArrayMap;
import android.util.FeatureFlagUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.HexDump;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.security.SecureBox;
import com.android.server.locksettings.LockSettingsService;
import com.android.server.locksettings.recoverablekeystore.certificate.CertParsingException;
import com.android.server.locksettings.recoverablekeystore.certificate.CertUtils;
import com.android.server.locksettings.recoverablekeystore.certificate.CertValidationException;
import com.android.server.locksettings.recoverablekeystore.certificate.CertXml;
import com.android.server.locksettings.recoverablekeystore.certificate.SigXml;
import com.android.server.locksettings.recoverablekeystore.storage.ApplicationKeyStorage;
import com.android.server.locksettings.recoverablekeystore.storage.CleanupManager;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverySessionStorage;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverySnapshotStorage;
import com.android.server.locksettings.recoverablekeystore.storage.RemoteLockscreenValidationSessionStorage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.crypto.AEADBadTagException;
/* loaded from: classes2.dex */
public class RecoverableKeyStoreManager {
    public static RecoverableKeyStoreManager mInstance;
    public final ApplicationKeyStorage mApplicationKeyStorage;
    public final CleanupManager mCleanupManager;
    public final Context mContext;
    public final RecoverableKeyStoreDb mDatabase;
    public final ScheduledExecutorService mExecutorService;
    public final RecoverySnapshotListenersStorage mListenersStorage;
    public final PlatformKeyManager mPlatformKeyManager;
    public final RecoverableKeyGenerator mRecoverableKeyGenerator;
    public final RecoverySessionStorage mRecoverySessionStorage;
    public final RemoteLockscreenValidationSessionStorage mRemoteLockscreenValidationSessionStorage;
    public final RecoverySnapshotStorage mSnapshotStorage;
    public final TestOnlyInsecureCertificateHelper mTestCertHelper;

    public static synchronized RecoverableKeyStoreManager getInstance(Context context) {
        RecoverableKeyStoreManager recoverableKeyStoreManager;
        RecoverableKeyStoreDb newInstance;
        RemoteLockscreenValidationSessionStorage remoteLockscreenValidationSessionStorage;
        synchronized (RecoverableKeyStoreManager.class) {
            if (mInstance == null) {
                if (FeatureFlagUtils.isEnabled(context, "settings_enable_lockscreen_transfer_api")) {
                    newInstance = RecoverableKeyStoreDb.newInstance(context, 7);
                    remoteLockscreenValidationSessionStorage = new RemoteLockscreenValidationSessionStorage();
                } else {
                    newInstance = RecoverableKeyStoreDb.newInstance(context);
                    remoteLockscreenValidationSessionStorage = null;
                }
                RecoverableKeyStoreDb recoverableKeyStoreDb = newInstance;
                RemoteLockscreenValidationSessionStorage remoteLockscreenValidationSessionStorage2 = remoteLockscreenValidationSessionStorage;
                try {
                    PlatformKeyManager platformKeyManager = PlatformKeyManager.getInstance(context, recoverableKeyStoreDb);
                    ApplicationKeyStorage applicationKeyStorage = ApplicationKeyStorage.getInstance();
                    RecoverySnapshotStorage newInstance2 = RecoverySnapshotStorage.newInstance();
                    mInstance = new RecoverableKeyStoreManager(context.getApplicationContext(), recoverableKeyStoreDb, new RecoverySessionStorage(), Executors.newScheduledThreadPool(1), newInstance2, new RecoverySnapshotListenersStorage(), platformKeyManager, applicationKeyStorage, new TestOnlyInsecureCertificateHelper(), CleanupManager.getInstance(context.getApplicationContext(), newInstance2, recoverableKeyStoreDb, applicationKeyStorage), remoteLockscreenValidationSessionStorage2);
                } catch (KeyStoreException e) {
                    throw new ServiceSpecificException(22, e.getMessage());
                } catch (NoSuchAlgorithmException e2) {
                    throw new RuntimeException(e2);
                }
            }
            recoverableKeyStoreManager = mInstance;
        }
        return recoverableKeyStoreManager;
    }

    @VisibleForTesting
    public RecoverableKeyStoreManager(Context context, RecoverableKeyStoreDb recoverableKeyStoreDb, RecoverySessionStorage recoverySessionStorage, ScheduledExecutorService scheduledExecutorService, RecoverySnapshotStorage recoverySnapshotStorage, RecoverySnapshotListenersStorage recoverySnapshotListenersStorage, PlatformKeyManager platformKeyManager, ApplicationKeyStorage applicationKeyStorage, TestOnlyInsecureCertificateHelper testOnlyInsecureCertificateHelper, CleanupManager cleanupManager, RemoteLockscreenValidationSessionStorage remoteLockscreenValidationSessionStorage) {
        this.mContext = context;
        this.mDatabase = recoverableKeyStoreDb;
        this.mRecoverySessionStorage = recoverySessionStorage;
        this.mExecutorService = scheduledExecutorService;
        this.mListenersStorage = recoverySnapshotListenersStorage;
        this.mSnapshotStorage = recoverySnapshotStorage;
        this.mPlatformKeyManager = platformKeyManager;
        this.mApplicationKeyStorage = applicationKeyStorage;
        this.mTestCertHelper = testOnlyInsecureCertificateHelper;
        this.mCleanupManager = cleanupManager;
        cleanupManager.verifyKnownUsers();
        try {
            this.mRecoverableKeyGenerator = RecoverableKeyGenerator.newInstance(recoverableKeyStoreDb);
            this.mRemoteLockscreenValidationSessionStorage = remoteLockscreenValidationSessionStorage;
        } catch (NoSuchAlgorithmException e) {
            Log.wtf("RecoverableKeyStoreMgr", "AES keygen algorithm not available. AOSP must support this.", e);
            throw new ServiceSpecificException(22, e.getMessage());
        }
    }

    @VisibleForTesting
    public void initRecoveryService(String str, byte[] bArr) throws RemoteException {
        checkRecoverKeyStorePermission();
        int callingUserId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        String defaultCertificateAliasIfEmpty = this.mTestCertHelper.getDefaultCertificateAliasIfEmpty(str);
        if (!this.mTestCertHelper.isValidRootCertificateAlias(defaultCertificateAliasIfEmpty)) {
            throw new ServiceSpecificException(28, "Invalid root certificate alias");
        }
        String activeRootOfTrust = this.mDatabase.getActiveRootOfTrust(callingUserId, callingUid);
        if (activeRootOfTrust == null) {
            Log.d("RecoverableKeyStoreMgr", "Root of trust for recovery agent + " + callingUid + " is assigned for the first time to " + defaultCertificateAliasIfEmpty);
        } else if (!activeRootOfTrust.equals(defaultCertificateAliasIfEmpty)) {
            Log.i("RecoverableKeyStoreMgr", "Root of trust for recovery agent " + callingUid + " is changed to " + defaultCertificateAliasIfEmpty + " from  " + activeRootOfTrust);
        }
        if (this.mDatabase.setActiveRootOfTrust(callingUserId, callingUid, defaultCertificateAliasIfEmpty) < 0) {
            throw new ServiceSpecificException(22, "Failed to set the root of trust in the local DB.");
        }
        try {
            CertXml parse = CertXml.parse(bArr);
            long serial = parse.getSerial();
            Long recoveryServiceCertSerial = this.mDatabase.getRecoveryServiceCertSerial(callingUserId, callingUid, defaultCertificateAliasIfEmpty);
            if (recoveryServiceCertSerial != null && recoveryServiceCertSerial.longValue() >= serial && !this.mTestCertHelper.isTestOnlyCertificateAlias(defaultCertificateAliasIfEmpty)) {
                if (recoveryServiceCertSerial.longValue() == serial) {
                    Log.i("RecoverableKeyStoreMgr", "The cert file serial number is the same, so skip updating.");
                    return;
                } else {
                    Log.e("RecoverableKeyStoreMgr", "The cert file serial number is older than the one in database.");
                    throw new ServiceSpecificException(29, "The cert file serial number is older than the one in database.");
                }
            }
            Log.i("RecoverableKeyStoreMgr", "Updating the certificate with the new serial number " + serial);
            X509Certificate rootCertificate = this.mTestCertHelper.getRootCertificate(defaultCertificateAliasIfEmpty);
            try {
                Log.d("RecoverableKeyStoreMgr", "Getting and validating a random endpoint certificate");
                CertPath randomEndpointCert = parse.getRandomEndpointCert(rootCertificate);
                try {
                    Log.d("RecoverableKeyStoreMgr", "Saving the randomly chosen endpoint certificate to database");
                    int i = (this.mDatabase.setRecoveryServiceCertPath(callingUserId, callingUid, defaultCertificateAliasIfEmpty, randomEndpointCert) > 0L ? 1 : (this.mDatabase.setRecoveryServiceCertPath(callingUserId, callingUid, defaultCertificateAliasIfEmpty, randomEndpointCert) == 0L ? 0 : -1));
                    if (i <= 0) {
                        if (i < 0) {
                            throw new ServiceSpecificException(22, "Failed to set the certificate path in the local DB.");
                        }
                    } else if (this.mDatabase.setRecoveryServiceCertSerial(callingUserId, callingUid, defaultCertificateAliasIfEmpty, serial) < 0) {
                        throw new ServiceSpecificException(22, "Failed to set the certificate serial number in the local DB.");
                    } else {
                        if (this.mDatabase.getSnapshotVersion(callingUserId, callingUid) != null) {
                            this.mDatabase.setShouldCreateSnapshot(callingUserId, callingUid, true);
                            Log.i("RecoverableKeyStoreMgr", "This is a certificate change. Snapshot must be updated");
                        } else {
                            Log.i("RecoverableKeyStoreMgr", "This is a certificate change. Snapshot didn't exist");
                        }
                        if (this.mDatabase.setCounterId(callingUserId, callingUid, new SecureRandom().nextLong()) < 0) {
                            Log.e("RecoverableKeyStoreMgr", "Failed to set the counter id in the local DB.");
                        }
                    }
                } catch (CertificateEncodingException e) {
                    Log.e("RecoverableKeyStoreMgr", "Failed to encode CertPath", e);
                    throw new ServiceSpecificException(25, e.getMessage());
                }
            } catch (CertValidationException e2) {
                Log.e("RecoverableKeyStoreMgr", "Invalid endpoint cert", e2);
                throw new ServiceSpecificException(28, e2.getMessage());
            }
        } catch (CertParsingException e3) {
            Log.d("RecoverableKeyStoreMgr", "Failed to parse the input as a cert file: " + HexDump.toHexString(bArr));
            throw new ServiceSpecificException(25, e3.getMessage());
        }
    }

    public void initRecoveryServiceWithSigFile(String str, byte[] bArr, byte[] bArr2) throws RemoteException {
        checkRecoverKeyStorePermission();
        String defaultCertificateAliasIfEmpty = this.mTestCertHelper.getDefaultCertificateAliasIfEmpty(str);
        Objects.requireNonNull(bArr, "recoveryServiceCertFile is null");
        Objects.requireNonNull(bArr2, "recoveryServiceSigFile is null");
        try {
            try {
                SigXml.parse(bArr2).verifyFileSignature(this.mTestCertHelper.getRootCertificate(defaultCertificateAliasIfEmpty), bArr);
                initRecoveryService(defaultCertificateAliasIfEmpty, bArr);
            } catch (CertValidationException e) {
                Log.d("RecoverableKeyStoreMgr", "The signature over the cert file is invalid. Cert: " + HexDump.toHexString(bArr) + " Sig: " + HexDump.toHexString(bArr2));
                throw new ServiceSpecificException(28, e.getMessage());
            }
        } catch (CertParsingException e2) {
            Log.d("RecoverableKeyStoreMgr", "Failed to parse the sig file: " + HexDump.toHexString(bArr2));
            throw new ServiceSpecificException(25, e2.getMessage());
        }
    }

    public KeyChainSnapshot getKeyChainSnapshot() throws RemoteException {
        checkRecoverKeyStorePermission();
        KeyChainSnapshot keyChainSnapshot = this.mSnapshotStorage.get(Binder.getCallingUid());
        if (keyChainSnapshot != null) {
            return keyChainSnapshot;
        }
        throw new ServiceSpecificException(21);
    }

    public void setSnapshotCreatedPendingIntent(PendingIntent pendingIntent) throws RemoteException {
        checkRecoverKeyStorePermission();
        this.mListenersStorage.setSnapshotListener(Binder.getCallingUid(), pendingIntent);
    }

    public void setServerParams(byte[] bArr) throws RemoteException {
        checkRecoverKeyStorePermission();
        int callingUserId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        byte[] serverParams = this.mDatabase.getServerParams(callingUserId, callingUid);
        if (Arrays.equals(bArr, serverParams)) {
            Log.v("RecoverableKeyStoreMgr", "Not updating server params - same as old value.");
        } else if (this.mDatabase.setServerParams(callingUserId, callingUid, bArr) < 0) {
            throw new ServiceSpecificException(22, "Database failure trying to set server params.");
        } else {
            if (serverParams == null) {
                Log.i("RecoverableKeyStoreMgr", "Initialized server params.");
            } else if (this.mDatabase.getSnapshotVersion(callingUserId, callingUid) != null) {
                this.mDatabase.setShouldCreateSnapshot(callingUserId, callingUid, true);
                Log.i("RecoverableKeyStoreMgr", "Updated server params. Snapshot must be updated");
            } else {
                Log.i("RecoverableKeyStoreMgr", "Updated server params. Snapshot didn't exist");
            }
        }
    }

    public void setRecoveryStatus(String str, int i) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "alias is null");
        if (this.mDatabase.setRecoveryStatus(Binder.getCallingUid(), str, i) < 0) {
            throw new ServiceSpecificException(22, "Failed to set the key recovery status in the local DB.");
        }
    }

    public Map<String, Integer> getRecoveryStatus() throws RemoteException {
        checkRecoverKeyStorePermission();
        return this.mDatabase.getStatusForAllKeys(Binder.getCallingUid());
    }

    public void setRecoverySecretTypes(int[] iArr) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(iArr, "secretTypes is null");
        int callingUserId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        int[] recoverySecretTypes = this.mDatabase.getRecoverySecretTypes(callingUserId, callingUid);
        if (Arrays.equals(iArr, recoverySecretTypes)) {
            Log.v("RecoverableKeyStoreMgr", "Not updating secret types - same as old value.");
        } else if (this.mDatabase.setRecoverySecretTypes(callingUserId, callingUid, iArr) < 0) {
            throw new ServiceSpecificException(22, "Database error trying to set secret types.");
        } else {
            if (recoverySecretTypes.length == 0) {
                Log.i("RecoverableKeyStoreMgr", "Initialized secret types.");
                return;
            }
            Log.i("RecoverableKeyStoreMgr", "Updated secret types. Snapshot pending.");
            if (this.mDatabase.getSnapshotVersion(callingUserId, callingUid) != null) {
                this.mDatabase.setShouldCreateSnapshot(callingUserId, callingUid, true);
                Log.i("RecoverableKeyStoreMgr", "Updated secret types. Snapshot must be updated");
                return;
            }
            Log.i("RecoverableKeyStoreMgr", "Updated secret types. Snapshot didn't exist");
        }
    }

    public int[] getRecoverySecretTypes() throws RemoteException {
        checkRecoverKeyStorePermission();
        return this.mDatabase.getRecoverySecretTypes(UserHandle.getCallingUserId(), Binder.getCallingUid());
    }

    @VisibleForTesting
    public byte[] startRecoverySession(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, List<KeyChainProtectionParams> list) throws RemoteException {
        checkRecoverKeyStorePermission();
        int callingUid = Binder.getCallingUid();
        if (list.size() != 1) {
            throw new UnsupportedOperationException("Only a single KeyChainProtectionParams is supported");
        }
        try {
            PublicKey deserializePublicKey = KeySyncUtils.deserializePublicKey(bArr);
            if (!publicKeysMatch(deserializePublicKey, bArr2)) {
                throw new ServiceSpecificException(28, "The public keys given in verifierPublicKey and vaultParams do not match.");
            }
            byte[] generateKeyClaimant = KeySyncUtils.generateKeyClaimant();
            byte[] secret = list.get(0).getSecret();
            this.mRecoverySessionStorage.add(callingUid, new RecoverySessionStorage.Entry(str, secret, generateKeyClaimant, bArr2));
            Log.i("RecoverableKeyStoreMgr", "Received VaultParams for recovery: " + HexDump.toHexString(bArr2));
            try {
                return KeySyncUtils.encryptRecoveryClaim(deserializePublicKey, bArr2, bArr3, KeySyncUtils.calculateThmKfHash(secret), generateKeyClaimant);
            } catch (InvalidKeyException e) {
                throw new ServiceSpecificException(25, e.getMessage());
            } catch (NoSuchAlgorithmException e2) {
                Log.wtf("RecoverableKeyStoreMgr", "SecureBox algorithm missing. AOSP must support this.", e2);
                throw new ServiceSpecificException(22, e2.getMessage());
            }
        } catch (InvalidKeySpecException e3) {
            throw new ServiceSpecificException(25, e3.getMessage());
        }
    }

    public byte[] startRecoverySessionWithCertPath(String str, String str2, RecoveryCertPath recoveryCertPath, byte[] bArr, byte[] bArr2, List<KeyChainProtectionParams> list) throws RemoteException {
        checkRecoverKeyStorePermission();
        String defaultCertificateAliasIfEmpty = this.mTestCertHelper.getDefaultCertificateAliasIfEmpty(str2);
        Objects.requireNonNull(str, "invalid session");
        Objects.requireNonNull(recoveryCertPath, "verifierCertPath is null");
        Objects.requireNonNull(bArr, "vaultParams is null");
        Objects.requireNonNull(bArr2, "vaultChallenge is null");
        Objects.requireNonNull(list, "secrets is null");
        try {
            CertPath certPath = recoveryCertPath.getCertPath();
            try {
                CertUtils.validateCertPath(this.mTestCertHelper.getRootCertificate(defaultCertificateAliasIfEmpty), certPath);
                byte[] encoded = certPath.getCertificates().get(0).getPublicKey().getEncoded();
                if (encoded == null) {
                    Log.e("RecoverableKeyStoreMgr", "Failed to encode verifierPublicKey");
                    throw new ServiceSpecificException(25, "Failed to encode verifierPublicKey");
                }
                return startRecoverySession(str, encoded, bArr, bArr2, list);
            } catch (CertValidationException e) {
                Log.e("RecoverableKeyStoreMgr", "Failed to validate the given cert path", e);
                throw new ServiceSpecificException(28, e.getMessage());
            }
        } catch (CertificateException e2) {
            throw new ServiceSpecificException(25, e2.getMessage());
        }
    }

    public Map<String, String> recoverKeyChainSnapshot(String str, byte[] bArr, List<WrappedApplicationKey> list) throws RemoteException {
        checkRecoverKeyStorePermission();
        int callingUserId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        RecoverySessionStorage.Entry entry = this.mRecoverySessionStorage.get(callingUid, str);
        try {
            if (entry == null) {
                throw new ServiceSpecificException(24, String.format(Locale.US, "Application uid=%d does not have pending session '%s'", Integer.valueOf(callingUid), str));
            }
            try {
                return importKeyMaterials(callingUserId, callingUid, recoverApplicationKeys(decryptRecoveryKey(entry, bArr), list));
            } catch (KeyStoreException e) {
                throw new ServiceSpecificException(22, e.getMessage());
            }
        } finally {
            entry.destroy();
            this.mRecoverySessionStorage.remove(callingUid);
        }
    }

    public final Map<String, String> importKeyMaterials(int i, int i2, Map<String, byte[]> map) throws KeyStoreException {
        ArrayMap arrayMap = new ArrayMap(map.size());
        for (String str : map.keySet()) {
            this.mApplicationKeyStorage.setSymmetricKeyEntry(i, i2, str, map.get(str));
            String alias = getAlias(i, i2, str);
            Log.i("RecoverableKeyStoreMgr", String.format(Locale.US, "Import %s -> %s", str, alias));
            arrayMap.put(str, alias);
        }
        return arrayMap;
    }

    public final String getAlias(int i, int i2, String str) {
        return this.mApplicationKeyStorage.getGrantAlias(i, i2, str);
    }

    public void closeSession(String str) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "invalid session");
        this.mRecoverySessionStorage.remove(Binder.getCallingUid(), str);
    }

    public void removeKey(String str) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "alias is null");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (this.mDatabase.removeKey(callingUid, str)) {
            this.mDatabase.setShouldCreateSnapshot(callingUserId, callingUid, true);
            this.mApplicationKeyStorage.deleteEntry(callingUserId, callingUid, str);
        }
    }

    @Deprecated
    public String generateKey(String str) throws RemoteException {
        return generateKeyWithMetadata(str, null);
    }

    public String generateKeyWithMetadata(String str, byte[] bArr) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "alias is null");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        try {
            try {
                this.mApplicationKeyStorage.setSymmetricKeyEntry(callingUserId, callingUid, str, this.mRecoverableKeyGenerator.generateAndStoreKey(this.mPlatformKeyManager.getEncryptKey(callingUserId), callingUserId, callingUid, str, bArr));
                return getAlias(callingUserId, callingUid, str);
            } catch (RecoverableKeyStorageException | InvalidKeyException | KeyStoreException e) {
                throw new ServiceSpecificException(22, e.getMessage());
            }
        } catch (IOException | KeyStoreException | UnrecoverableKeyException e2) {
            throw new ServiceSpecificException(22, e2.getMessage());
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException(e3);
        }
    }

    @Deprecated
    public String importKey(String str, byte[] bArr) throws RemoteException {
        return importKeyWithMetadata(str, bArr, null);
    }

    public String importKeyWithMetadata(String str, byte[] bArr, byte[] bArr2) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "alias is null");
        Objects.requireNonNull(bArr, "keyBytes is null");
        if (bArr.length != 32) {
            Log.e("RecoverableKeyStoreMgr", "The given key for import doesn't have the required length 256");
            throw new ServiceSpecificException(27, "The given key does not contain 256 bits.");
        }
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        try {
            try {
                this.mRecoverableKeyGenerator.importKey(this.mPlatformKeyManager.getEncryptKey(callingUserId), callingUserId, callingUid, str, bArr, bArr2);
                this.mApplicationKeyStorage.setSymmetricKeyEntry(callingUserId, callingUid, str, bArr);
                return getAlias(callingUserId, callingUid, str);
            } catch (RecoverableKeyStorageException | InvalidKeyException | KeyStoreException e) {
                throw new ServiceSpecificException(22, e.getMessage());
            }
        } catch (IOException | KeyStoreException | UnrecoverableKeyException e2) {
            throw new ServiceSpecificException(22, e2.getMessage());
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException(e3);
        }
    }

    public String getKey(String str) throws RemoteException {
        checkRecoverKeyStorePermission();
        Objects.requireNonNull(str, "alias is null");
        return getAlias(UserHandle.getCallingUserId(), Binder.getCallingUid(), str);
    }

    public final byte[] decryptRecoveryKey(RecoverySessionStorage.Entry entry, byte[] bArr) throws RemoteException, ServiceSpecificException {
        try {
            try {
                return KeySyncUtils.decryptRecoveryKey(entry.getLskfHash(), KeySyncUtils.decryptRecoveryClaimResponse(entry.getKeyClaimant(), entry.getVaultParams(), bArr));
            } catch (InvalidKeyException e) {
                Log.e("RecoverableKeyStoreMgr", "Got InvalidKeyException during decrypting recovery key", e);
                throw new ServiceSpecificException(26, "Failed to decrypt recovery key " + e.getMessage());
            } catch (NoSuchAlgorithmException e2) {
                throw new ServiceSpecificException(22, e2.getMessage());
            } catch (AEADBadTagException e3) {
                Log.e("RecoverableKeyStoreMgr", "Got AEADBadTagException during decrypting recovery key", e3);
                throw new ServiceSpecificException(26, "Failed to decrypt recovery key " + e3.getMessage());
            }
        } catch (InvalidKeyException e4) {
            Log.e("RecoverableKeyStoreMgr", "Got InvalidKeyException during decrypting recovery claim response", e4);
            throw new ServiceSpecificException(26, "Failed to decrypt recovery key " + e4.getMessage());
        } catch (NoSuchAlgorithmException e5) {
            throw new ServiceSpecificException(22, e5.getMessage());
        } catch (AEADBadTagException e6) {
            Log.e("RecoverableKeyStoreMgr", "Got AEADBadTagException during decrypting recovery claim response", e6);
            throw new ServiceSpecificException(26, "Failed to decrypt recovery key " + e6.getMessage());
        }
    }

    public final Map<String, byte[]> recoverApplicationKeys(byte[] bArr, List<WrappedApplicationKey> list) throws RemoteException {
        HashMap hashMap = new HashMap();
        for (WrappedApplicationKey wrappedApplicationKey : list) {
            String alias = wrappedApplicationKey.getAlias();
            try {
                hashMap.put(alias, KeySyncUtils.decryptApplicationKey(bArr, wrappedApplicationKey.getEncryptedKeyMaterial(), wrappedApplicationKey.getMetadata()));
            } catch (InvalidKeyException e) {
                Log.e("RecoverableKeyStoreMgr", "Got InvalidKeyException during decrypting application key with alias: " + alias, e);
                throw new ServiceSpecificException(26, "Failed to recover key with alias '" + alias + "': " + e.getMessage());
            } catch (NoSuchAlgorithmException e2) {
                Log.wtf("RecoverableKeyStoreMgr", "Missing SecureBox algorithm. AOSP required to support this.", e2);
                throw new ServiceSpecificException(22, e2.getMessage());
            } catch (AEADBadTagException e3) {
                Log.e("RecoverableKeyStoreMgr", "Got AEADBadTagException during decrypting application key with alias: " + alias, e3);
            }
        }
        if (list.isEmpty() || !hashMap.isEmpty()) {
            return hashMap;
        }
        Log.e("RecoverableKeyStoreMgr", "Failed to recover any of the application keys.");
        throw new ServiceSpecificException(26, "Failed to recover any of the application keys.");
    }

    public void lockScreenSecretAvailable(int i, byte[] bArr, int i2) {
        try {
            this.mExecutorService.schedule(KeySyncTask.newInstance(this.mContext, this.mDatabase, this.mSnapshotStorage, this.mListenersStorage, i2, i, bArr, false), 2000L, TimeUnit.MILLISECONDS);
        } catch (InsecureUserException e) {
            Log.wtf("RecoverableKeyStoreMgr", "Impossible - insecure user, but user just entered lock screen", e);
        } catch (KeyStoreException e2) {
            Log.e("RecoverableKeyStoreMgr", "Key store error encountered during recoverable key sync", e2);
        } catch (NoSuchAlgorithmException e3) {
            Log.wtf("RecoverableKeyStoreMgr", "Should never happen - algorithm unavailable for KeySync", e3);
        }
    }

    public void lockScreenSecretChanged(int i, byte[] bArr, int i2) {
        try {
            this.mExecutorService.schedule(KeySyncTask.newInstance(this.mContext, this.mDatabase, this.mSnapshotStorage, this.mListenersStorage, i2, i, bArr, true), 2000L, TimeUnit.MILLISECONDS);
        } catch (InsecureUserException e) {
            Log.e("RecoverableKeyStoreMgr", "InsecureUserException during lock screen secret update", e);
        } catch (KeyStoreException e2) {
            Log.e("RecoverableKeyStoreMgr", "Key store error encountered during recoverable key sync", e2);
        } catch (NoSuchAlgorithmException e3) {
            Log.wtf("RecoverableKeyStoreMgr", "Should never happen - algorithm unavailable for KeySync", e3);
        }
    }

    public StartLockscreenValidationRequest startRemoteLockscreenValidation(LockSettingsService lockSettingsService) {
        if (this.mRemoteLockscreenValidationSessionStorage == null) {
            throw new UnsupportedOperationException("Under development");
        }
        checkVerifyRemoteLockscreenPermission();
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int credentialType = lockSettingsService.getCredentialType(callingUserId);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            int lockPatternUtilsToKeyguardType = lockPatternUtilsToKeyguardType(credentialType);
            return new StartLockscreenValidationRequest.Builder().setLockscreenUiType(lockPatternUtilsToKeyguardType).setRemainingAttempts(Math.max(5 - this.mDatabase.getBadRemoteGuessCounter(callingUserId), 0)).setSourcePublicKey(SecureBox.encodePublicKey(this.mRemoteLockscreenValidationSessionStorage.startSession(callingUserId).getKeyPair().getPublic())).build();
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public synchronized RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] bArr, LockSettingsService lockSettingsService) {
        checkVerifyRemoteLockscreenPermission();
        int callingUserId = UserHandle.getCallingUserId();
        RemoteLockscreenValidationSessionStorage.LockscreenVerificationSession lockscreenVerificationSession = this.mRemoteLockscreenValidationSessionStorage.get(callingUserId);
        if (5 - this.mDatabase.getBadRemoteGuessCounter(callingUserId) <= 0) {
            return new RemoteLockscreenValidationResult.Builder().setResultCode(4).build();
        } else if (lockscreenVerificationSession == null) {
            throw new IllegalStateException("There is no active lock screen check session");
        } else {
            try {
                byte[] decrypt = SecureBox.decrypt(lockscreenVerificationSession.getKeyPair().getPrivate(), null, LockPatternUtils.ENCRYPTED_REMOTE_CREDENTIALS_HEADER, bArr);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                LockscreenCredential createLockscreenCredential = createLockscreenCredential(lockPatternUtilsToKeyguardType(lockSettingsService.getCredentialType(callingUserId)), decrypt);
                RemoteLockscreenValidationResult handleVerifyCredentialResponse = handleVerifyCredentialResponse(lockSettingsService.verifyCredential(createLockscreenCredential, callingUserId, 0), callingUserId);
                if (createLockscreenCredential != null) {
                    createLockscreenCredential.close();
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return handleVerifyCredentialResponse;
            } catch (InvalidKeyException e) {
                Log.e("RecoverableKeyStoreMgr", "Got InvalidKeyException during lock screen credentials decryption");
                throw new IllegalStateException(e);
            } catch (NoSuchAlgorithmException e2) {
                Log.wtf("RecoverableKeyStoreMgr", "Missing SecureBox algorithm. AOSP required to support this.", e2);
                throw new IllegalStateException(e2);
            } catch (AEADBadTagException e3) {
                throw new IllegalStateException("Could not decrypt credentials guess", e3);
            }
        }
    }

    public final RemoteLockscreenValidationResult handleVerifyCredentialResponse(VerifyCredentialResponse verifyCredentialResponse, int i) {
        if (verifyCredentialResponse.getResponseCode() == 0) {
            this.mDatabase.setBadRemoteGuessCounter(i, 0);
            this.mRemoteLockscreenValidationSessionStorage.finishSession(i);
            return new RemoteLockscreenValidationResult.Builder().setResultCode(1).build();
        } else if (verifyCredentialResponse.getResponseCode() == 1) {
            return new RemoteLockscreenValidationResult.Builder().setResultCode(3).setTimeoutMillis(verifyCredentialResponse.getTimeout()).build();
        } else {
            this.mDatabase.setBadRemoteGuessCounter(i, this.mDatabase.getBadRemoteGuessCounter(i) + 1);
            return new RemoteLockscreenValidationResult.Builder().setResultCode(2).build();
        }
    }

    public final LockscreenCredential createLockscreenCredential(int i, byte[] bArr) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return LockscreenCredential.createPattern(LockPatternUtils.byteArrayToPattern(bArr));
                }
                throw new IllegalStateException("Lockscreen is not set");
            }
            return LockscreenCredential.createPin(new String(bArr));
        }
        return LockscreenCredential.createPassword(new String(bArr, StandardCharsets.UTF_8));
    }

    public final void checkVerifyRemoteLockscreenPermission() {
        Context context = this.mContext;
        context.enforceCallingOrSelfPermission("android.permission.CHECK_REMOTE_LOCKSCREEN", "Caller " + Binder.getCallingUid() + " doesn't have CHECK_REMOTE_LOCKSCREEN permission.");
        this.mCleanupManager.registerRecoveryAgent(UserHandle.getCallingUserId(), Binder.getCallingUid());
    }

    public final int lockPatternUtilsToKeyguardType(int i) {
        if (i != -1) {
            if (i != 1) {
                if (i != 3) {
                    if (i == 4) {
                        return 0;
                    }
                    throw new IllegalStateException("Screen lock is not set");
                }
                return 1;
            }
            return 2;
        }
        throw new IllegalStateException("Screen lock is not set");
    }

    public final void checkRecoverKeyStorePermission() {
        Context context = this.mContext;
        context.enforceCallingOrSelfPermission("android.permission.RECOVER_KEYSTORE", "Caller " + Binder.getCallingUid() + " doesn't have RecoverKeyStore permission.");
        this.mCleanupManager.registerRecoveryAgent(UserHandle.getCallingUserId(), Binder.getCallingUid());
    }

    public final boolean publicKeysMatch(PublicKey publicKey, byte[] bArr) {
        byte[] encodePublicKey = SecureBox.encodePublicKey(publicKey);
        return Arrays.equals(encodePublicKey, Arrays.copyOf(bArr, encodePublicKey.length));
    }
}
