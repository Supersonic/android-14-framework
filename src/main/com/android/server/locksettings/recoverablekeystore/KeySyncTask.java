package com.android.server.locksettings.recoverablekeystore;

import android.content.Context;
import android.os.RemoteException;
import android.security.Scrypt;
import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.KeyDerivationParams;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverySnapshotStorage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class KeySyncTask implements Runnable {
    @VisibleForTesting
    static final int SCRYPT_PARAM_N = 4096;
    @VisibleForTesting
    static final int SCRYPT_PARAM_OUTLEN_BYTES = 32;
    @VisibleForTesting
    static final int SCRYPT_PARAM_P = 1;
    @VisibleForTesting
    static final int SCRYPT_PARAM_R = 8;
    public final byte[] mCredential;
    public final int mCredentialType;
    public final boolean mCredentialUpdated;
    public final PlatformKeyManager mPlatformKeyManager;
    public final RecoverableKeyStoreDb mRecoverableKeyStoreDb;
    public final RecoverySnapshotStorage mRecoverySnapshotStorage;
    public final Scrypt mScrypt;
    public final RecoverySnapshotListenersStorage mSnapshotListenersStorage;
    public final TestOnlyInsecureCertificateHelper mTestOnlyInsecureCertificateHelper;
    public final int mUserId;

    @VisibleForTesting
    public static int getUiFormat(int i) {
        if (i == 1) {
            return 3;
        }
        return i == 3 ? 1 : 2;
    }

    public static KeySyncTask newInstance(Context context, RecoverableKeyStoreDb recoverableKeyStoreDb, RecoverySnapshotStorage recoverySnapshotStorage, RecoverySnapshotListenersStorage recoverySnapshotListenersStorage, int i, int i2, byte[] bArr, boolean z) throws NoSuchAlgorithmException, KeyStoreException, InsecureUserException {
        return new KeySyncTask(recoverableKeyStoreDb, recoverySnapshotStorage, recoverySnapshotListenersStorage, i, i2, bArr, z, PlatformKeyManager.getInstance(context, recoverableKeyStoreDb), new TestOnlyInsecureCertificateHelper(), new Scrypt());
    }

    @VisibleForTesting
    public KeySyncTask(RecoverableKeyStoreDb recoverableKeyStoreDb, RecoverySnapshotStorage recoverySnapshotStorage, RecoverySnapshotListenersStorage recoverySnapshotListenersStorage, int i, int i2, byte[] bArr, boolean z, PlatformKeyManager platformKeyManager, TestOnlyInsecureCertificateHelper testOnlyInsecureCertificateHelper, Scrypt scrypt) {
        this.mSnapshotListenersStorage = recoverySnapshotListenersStorage;
        this.mRecoverableKeyStoreDb = recoverableKeyStoreDb;
        this.mUserId = i;
        this.mCredentialType = i2;
        this.mCredential = bArr;
        this.mCredentialUpdated = z;
        this.mPlatformKeyManager = platformKeyManager;
        this.mRecoverySnapshotStorage = recoverySnapshotStorage;
        this.mTestOnlyInsecureCertificateHelper = testOnlyInsecureCertificateHelper;
        this.mScrypt = scrypt;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            synchronized (KeySyncTask.class) {
                syncKeys();
            }
        } catch (Exception e) {
            Log.e("KeySyncTask", "Unexpected exception thrown during KeySyncTask", e);
        }
    }

    public final void syncKeys() throws RemoteException {
        int generationId = this.mPlatformKeyManager.getGenerationId(this.mUserId);
        if (this.mCredentialType == -1) {
            Log.w("KeySyncTask", "Credentials are not set for user " + this.mUserId);
            if (generationId < 1001000) {
                this.mPlatformKeyManager.invalidatePlatformKey(this.mUserId, generationId);
            }
        } else if (isCustomLockScreen()) {
            Log.w("KeySyncTask", "Unsupported credential type " + this.mCredentialType + " for user " + this.mUserId);
            if (generationId < 1001000) {
                this.mRecoverableKeyStoreDb.invalidateKeysForUserIdOnCustomScreenLock(this.mUserId);
            }
        } else if (this.mPlatformKeyManager.isDeviceLocked(this.mUserId) && this.mUserId == 0) {
            Log.w("KeySyncTask", "Can't sync keys for locked user " + this.mUserId);
        } else {
            List<Integer> recoveryAgents = this.mRecoverableKeyStoreDb.getRecoveryAgents(this.mUserId);
            for (Integer num : recoveryAgents) {
                int intValue = num.intValue();
                try {
                    syncKeysForAgent(intValue);
                } catch (IOException e) {
                    Log.e("KeySyncTask", "IOException during sync for agent " + intValue, e);
                }
            }
            if (recoveryAgents.isEmpty()) {
                Log.w("KeySyncTask", "No recovery agent initialized for user " + this.mUserId);
            }
        }
    }

    public final boolean isCustomLockScreen() {
        int i = this.mCredentialType;
        return (i == -1 || i == 1 || i == 3 || i == 4) ? false : true;
    }

    public final void syncKeysForAgent(int i) throws IOException, RemoteException {
        boolean z;
        PublicKey recoveryServicePublicKey;
        byte[] hashCredentialsBySaltedSha256;
        Long counterId;
        KeyDerivationParams createSha256Params;
        if (shouldCreateSnapshot(i)) {
            z = false;
        } else {
            z = this.mRecoverableKeyStoreDb.getSnapshotVersion(this.mUserId, i) != null && this.mRecoverySnapshotStorage.get(i) == null;
            if (z) {
                Log.d("KeySyncTask", "Recreating most recent snapshot");
            } else {
                Log.d("KeySyncTask", "Key sync not needed.");
                return;
            }
        }
        String defaultCertificateAliasIfEmpty = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(this.mRecoverableKeyStoreDb.getActiveRootOfTrust(this.mUserId, i));
        CertPath recoveryServiceCertPath = this.mRecoverableKeyStoreDb.getRecoveryServiceCertPath(this.mUserId, i, defaultCertificateAliasIfEmpty);
        if (recoveryServiceCertPath != null) {
            Log.d("KeySyncTask", "Using the public key in stored CertPath for syncing");
            recoveryServicePublicKey = recoveryServiceCertPath.getCertificates().get(0).getPublicKey();
        } else {
            Log.d("KeySyncTask", "Using the stored raw public key for syncing");
            recoveryServicePublicKey = this.mRecoverableKeyStoreDb.getRecoveryServicePublicKey(this.mUserId, i);
        }
        if (recoveryServicePublicKey == null) {
            Log.w("KeySyncTask", "Not initialized for KeySync: no public key set. Cancelling task.");
            return;
        }
        byte[] serverParams = this.mRecoverableKeyStoreDb.getServerParams(this.mUserId, i);
        if (serverParams == null) {
            Log.w("KeySyncTask", "No device ID set for user " + this.mUserId);
            return;
        }
        if (this.mTestOnlyInsecureCertificateHelper.isTestOnlyCertificateAlias(defaultCertificateAliasIfEmpty)) {
            Log.w("KeySyncTask", "Insecure root certificate is used by recovery agent " + i);
            if (this.mTestOnlyInsecureCertificateHelper.doesCredentialSupportInsecureMode(this.mCredentialType, this.mCredential)) {
                Log.w("KeySyncTask", "Whitelisted credential is used to generate snapshot by recovery agent " + i);
            } else {
                Log.w("KeySyncTask", "Non whitelisted credential is used to generate recovery snapshot by " + i + " - ignore attempt.");
                return;
            }
        }
        boolean shouldUseScryptToHashCredential = shouldUseScryptToHashCredential();
        byte[] generateSalt = generateSalt();
        if (shouldUseScryptToHashCredential) {
            hashCredentialsBySaltedSha256 = hashCredentialsByScrypt(generateSalt, this.mCredential);
        } else {
            hashCredentialsBySaltedSha256 = hashCredentialsBySaltedSha256(generateSalt, this.mCredential);
        }
        try {
            Map<String, Pair<SecretKey, byte[]>> keysToSync = getKeysToSync(i);
            if (this.mTestOnlyInsecureCertificateHelper.isTestOnlyCertificateAlias(defaultCertificateAliasIfEmpty)) {
                keysToSync = this.mTestOnlyInsecureCertificateHelper.keepOnlyWhitelistedInsecureKeys(keysToSync);
            }
            try {
                SecretKey generateRecoveryKey = generateRecoveryKey();
                try {
                    Map<String, byte[]> encryptKeysWithRecoveryKey = KeySyncUtils.encryptKeysWithRecoveryKey(generateRecoveryKey, keysToSync);
                    if (this.mCredentialUpdated) {
                        counterId = Long.valueOf(generateAndStoreCounterId(i));
                    } else {
                        counterId = this.mRecoverableKeyStoreDb.getCounterId(this.mUserId, i);
                        if (counterId == null) {
                            counterId = Long.valueOf(generateAndStoreCounterId(i));
                        }
                    }
                    try {
                        byte[] thmEncryptRecoveryKey = KeySyncUtils.thmEncryptRecoveryKey(recoveryServicePublicKey, hashCredentialsBySaltedSha256, KeySyncUtils.packVaultParams(recoveryServicePublicKey, counterId.longValue(), 10, serverParams), generateRecoveryKey);
                        if (shouldUseScryptToHashCredential) {
                            createSha256Params = KeyDerivationParams.createScryptParams(generateSalt, 4096);
                        } else {
                            createSha256Params = KeyDerivationParams.createSha256Params(generateSalt);
                        }
                        KeyChainProtectionParams build = new KeyChainProtectionParams.Builder().setUserSecretType(100).setLockScreenUiFormat(getUiFormat(this.mCredentialType)).setKeyDerivationParams(createSha256Params).setSecret(new byte[0]).build();
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(build);
                        KeyChainSnapshot.Builder encryptedRecoveryKeyBlob = new KeyChainSnapshot.Builder().setSnapshotVersion(getSnapshotVersion(i, z)).setMaxAttempts(10).setCounterId(counterId.longValue()).setServerParams(serverParams).setKeyChainProtectionParams(arrayList).setWrappedApplicationKeys(createApplicationKeyEntries(encryptKeysWithRecoveryKey, keysToSync)).setEncryptedRecoveryKeyBlob(thmEncryptRecoveryKey);
                        try {
                            encryptedRecoveryKeyBlob.setTrustedHardwareCertPath(recoveryServiceCertPath);
                            this.mRecoverySnapshotStorage.put(i, encryptedRecoveryKeyBlob.build());
                            this.mSnapshotListenersStorage.recoverySnapshotAvailable(i);
                            this.mRecoverableKeyStoreDb.setShouldCreateSnapshot(this.mUserId, i, false);
                        } catch (CertificateException e) {
                            Log.wtf("KeySyncTask", "Cannot serialize CertPath when calling setTrustedHardwareCertPath", e);
                        }
                    } catch (InvalidKeyException e2) {
                        Log.e("KeySyncTask", "Could not encrypt with recovery key", e2);
                    } catch (NoSuchAlgorithmException e3) {
                        Log.wtf("KeySyncTask", "SecureBox encrypt algorithms unavailable", e3);
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException e4) {
                    Log.wtf("KeySyncTask", "Should be impossible: could not encrypt application keys with random key", e4);
                }
            } catch (NoSuchAlgorithmException e5) {
                Log.wtf("AES should never be unavailable", e5);
            }
        } catch (BadPlatformKeyException e6) {
            Log.e("KeySyncTask", "Loaded keys for same generation ID as platform key, so BadPlatformKeyException should be impossible.", e6);
        } catch (InsecureUserException e7) {
            Log.e("KeySyncTask", "A screen unlock triggered the key sync flow, so user must have lock screen. This should be impossible.", e7);
        } catch (IOException e8) {
            Log.e("KeySyncTask", "Local database error.", e8);
        } catch (GeneralSecurityException e9) {
            Log.e("KeySyncTask", "Failed to load recoverable keys for sync", e9);
        }
    }

    @VisibleForTesting
    public int getSnapshotVersion(int i, boolean z) throws IOException {
        Long valueOf;
        Long snapshotVersion = this.mRecoverableKeyStoreDb.getSnapshotVersion(this.mUserId, i);
        if (z) {
            valueOf = Long.valueOf(snapshotVersion != null ? snapshotVersion.longValue() : 1L);
        } else {
            valueOf = Long.valueOf(snapshotVersion != null ? 1 + snapshotVersion.longValue() : 1L);
        }
        if (this.mRecoverableKeyStoreDb.setSnapshotVersion(this.mUserId, i, valueOf.longValue()) < 0) {
            Log.e("KeySyncTask", "Failed to set the snapshot version in the local DB.");
            throw new IOException("Failed to set the snapshot version in the local DB.");
        }
        return valueOf.intValue();
    }

    public final long generateAndStoreCounterId(int i) throws IOException {
        long nextLong = new SecureRandom().nextLong();
        if (this.mRecoverableKeyStoreDb.setCounterId(this.mUserId, i, nextLong) >= 0) {
            return nextLong;
        }
        Log.e("KeySyncTask", "Failed to set the snapshot version in the local DB.");
        throw new IOException("Failed to set counterId in the local DB.");
    }

    public final Map<String, Pair<SecretKey, byte[]>> getKeysToSync(int i) throws InsecureUserException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPlatformKeyException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, RemoteException {
        PlatformDecryptionKey decryptKey = this.mPlatformKeyManager.getDecryptKey(this.mUserId);
        return WrappedKey.unwrapKeys(decryptKey, this.mRecoverableKeyStoreDb.getAllKeys(this.mUserId, i, decryptKey.getGenerationId()));
    }

    public final boolean shouldCreateSnapshot(int i) {
        if (ArrayUtils.contains(this.mRecoverableKeyStoreDb.getRecoverySecretTypes(this.mUserId, i), 100)) {
            if (this.mCredentialUpdated && this.mRecoverableKeyStoreDb.getSnapshotVersion(this.mUserId, i) != null) {
                this.mRecoverableKeyStoreDb.setShouldCreateSnapshot(this.mUserId, i, true);
                return true;
            }
            return this.mRecoverableKeyStoreDb.getShouldCreateSnapshot(this.mUserId, i);
        }
        return false;
    }

    public static byte[] generateSalt() {
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    @VisibleForTesting
    public static byte[] hashCredentialsBySaltedSha256(byte[] bArr, byte[] bArr2) {
        ByteBuffer allocate = ByteBuffer.allocate(bArr.length + bArr2.length + 8);
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        allocate.putInt(bArr.length);
        allocate.put(bArr);
        allocate.putInt(bArr2.length);
        allocate.put(bArr2);
        byte[] array = allocate.array();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(array);
            Arrays.fill(array, (byte) 0);
            return digest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public final byte[] hashCredentialsByScrypt(byte[] bArr, byte[] bArr2) {
        return this.mScrypt.scrypt(bArr2, bArr, 4096, 8, 1, 32);
    }

    public static SecretKey generateRecoveryKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static List<WrappedApplicationKey> createApplicationKeyEntries(Map<String, byte[]> map, Map<String, Pair<SecretKey, byte[]>> map2) {
        ArrayList arrayList = new ArrayList();
        for (String str : map.keySet()) {
            arrayList.add(new WrappedApplicationKey.Builder().setAlias(str).setEncryptedKeyMaterial(map.get(str)).setMetadata((byte[]) map2.get(str).second).build());
        }
        return arrayList;
    }

    public final boolean shouldUseScryptToHashCredential() {
        int i = this.mCredentialType;
        return i == 4 || i == 3;
    }
}
