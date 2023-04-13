package com.android.server.locksettings;

import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.content.pm.UserInfo;
import android.hardware.weaver.IWeaver;
import android.hardware.weaver.WeaverConfig;
import android.hardware.weaver.WeaverReadResponse;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.UserManager;
import android.provider.Settings;
import android.security.Scrypt;
import android.service.gatekeeper.GateKeeperResponse;
import android.service.gatekeeper.IGateKeeperService;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.IWeakEscrowTokenRemovedListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.server.locksettings.LockSettingsStorage;
import com.android.server.utils.Slogf;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import libcore.util.HexEncoding;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class SyntheticPasswordManager {
    public final Context mContext;
    public PasswordSlotManager mPasswordSlotManager;
    public LockSettingsStorage mStorage;
    public final UserManager mUserManager;
    public IWeaver mWeaver;
    public WeaverConfig mWeaverConfig;
    public static final byte[] DEFAULT_PASSWORD = "default-password".getBytes();
    public static final byte[] PERSONALIZATION_SECDISCARDABLE = "secdiscardable-transform".getBytes();
    public static final byte[] PERSONALIZATION_KEY_STORE_PASSWORD = "keystore-password".getBytes();
    public static final byte[] PERSONALIZATION_USER_GK_AUTH = "user-gk-authentication".getBytes();
    public static final byte[] PERSONALIZATION_SP_GK_AUTH = "sp-gk-authentication".getBytes();
    public static final byte[] PERSONALIZATION_FBE_KEY = "fbe-key".getBytes();
    public static final byte[] PERSONALIZATION_AUTHSECRET_KEY = "authsecret-hal".getBytes();
    public static final byte[] PERSONALIZATION_AUTHSECRET_ENCRYPTION_KEY = "vendor-authsecret-encryption-key".getBytes();
    public static final byte[] PERSONALIZATION_SP_SPLIT = "sp-split".getBytes();
    public static final byte[] PERSONALIZATION_PASSWORD_HASH = "pw-hash".getBytes();
    public static final byte[] PERSONALIZATION_E0 = "e0-encryption".getBytes();
    public static final byte[] PERSONALIZATION_WEAVER_PASSWORD = "weaver-pwd".getBytes();
    public static final byte[] PERSONALIZATION_WEAVER_KEY = "weaver-key".getBytes();
    public static final byte[] PERSONALIZATION_WEAVER_TOKEN = "weaver-token".getBytes();
    public static final byte[] PERSONALIZATION_PASSWORD_METRICS = "password-metrics".getBytes();
    public static final byte[] PERSONALIZATION_CONTEXT = "android-synthetic-password-personalization-context".getBytes();
    public final RemoteCallbackList<IWeakEscrowTokenRemovedListener> mListeners = new RemoteCallbackList<>();
    public ArrayMap<Integer, ArrayMap<Long, TokenData>> tokenMap = new ArrayMap<>();

    /* loaded from: classes2.dex */
    public static class AuthenticationResult {
        public VerifyCredentialResponse gkResponse;
        public SyntheticPassword syntheticPassword;
    }

    @VisibleForTesting
    public static int fakeUserId(int i) {
        return i + 100000;
    }

    private native long nativeSidFromPasswordHandle(byte[] bArr);

    public final byte getTokenBasedProtectorType(int i) {
        return i != 1 ? (byte) 1 : (byte) 2;
    }

    /* loaded from: classes2.dex */
    public static class SyntheticPassword {
        public byte[] mEncryptedEscrowSplit0;
        public byte[] mEscrowSplit1;
        public byte[] mSyntheticPassword;
        public final byte mVersion;

        public SyntheticPassword(byte b) {
            this.mVersion = b;
        }

        public final byte[] deriveSubkey(byte[] bArr) {
            if (this.mVersion == 3) {
                return new SP800Derive(this.mSyntheticPassword).withContext(bArr, SyntheticPasswordManager.PERSONALIZATION_CONTEXT);
            }
            return SyntheticPasswordCrypto.personalizedHash(bArr, this.mSyntheticPassword);
        }

        public byte[] deriveKeyStorePassword() {
            return SyntheticPasswordManager.bytesToHex(deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_KEY_STORE_PASSWORD));
        }

        public byte[] deriveGkPassword() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_SP_GK_AUTH);
        }

        public byte[] deriveFileBasedEncryptionKey() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_FBE_KEY);
        }

        public byte[] deriveVendorAuthSecret() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_AUTHSECRET_KEY);
        }

        public byte[] derivePasswordHashFactor() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_PASSWORD_HASH);
        }

        public byte[] deriveMetricsKey() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_PASSWORD_METRICS);
        }

        public byte[] deriveVendorAuthSecretEncryptionKey() {
            return deriveSubkey(SyntheticPasswordManager.PERSONALIZATION_AUTHSECRET_ENCRYPTION_KEY);
        }

        public void setEscrowData(byte[] bArr, byte[] bArr2) {
            this.mEncryptedEscrowSplit0 = bArr;
            this.mEscrowSplit1 = bArr2;
        }

        public void recreateFromEscrow(byte[] bArr) {
            Objects.requireNonNull(this.mEscrowSplit1);
            Objects.requireNonNull(this.mEncryptedEscrowSplit0);
            recreate(bArr, this.mEscrowSplit1);
        }

        public void recreateDirectly(byte[] bArr) {
            this.mSyntheticPassword = Arrays.copyOf(bArr, bArr.length);
        }

        public static SyntheticPassword create() {
            SyntheticPassword syntheticPassword = new SyntheticPassword((byte) 3);
            byte[] randomBytes = SecureRandomUtils.randomBytes(32);
            byte[] randomBytes2 = SecureRandomUtils.randomBytes(32);
            syntheticPassword.recreate(randomBytes, randomBytes2);
            syntheticPassword.setEscrowData(SyntheticPasswordCrypto.encrypt(syntheticPassword.mSyntheticPassword, SyntheticPasswordManager.PERSONALIZATION_E0, randomBytes), randomBytes2);
            return syntheticPassword;
        }

        public final void recreate(byte[] bArr, byte[] bArr2) {
            this.mSyntheticPassword = SyntheticPasswordManager.bytesToHex(SyntheticPasswordCrypto.personalizedHash(SyntheticPasswordManager.PERSONALIZATION_SP_SPLIT, bArr, bArr2));
        }

        public byte[] getEscrowSecret() {
            if (this.mEncryptedEscrowSplit0 == null) {
                return null;
            }
            return SyntheticPasswordCrypto.decrypt(this.mSyntheticPassword, SyntheticPasswordManager.PERSONALIZATION_E0, this.mEncryptedEscrowSplit0);
        }

        public byte[] getSyntheticPassword() {
            return this.mSyntheticPassword;
        }

        public byte getVersion() {
            return this.mVersion;
        }
    }

    /* loaded from: classes2.dex */
    public static class PasswordData {
        public int credentialType;
        public byte[] passwordHandle;
        public byte[] salt;
        public byte scryptLogN;
        public byte scryptLogP;
        public byte scryptLogR;

        public static PasswordData create(int i) {
            PasswordData passwordData = new PasswordData();
            passwordData.scryptLogN = (byte) 11;
            passwordData.scryptLogR = (byte) 3;
            passwordData.scryptLogP = (byte) 1;
            passwordData.credentialType = i;
            passwordData.salt = SecureRandomUtils.randomBytes(16);
            return passwordData;
        }

        public static PasswordData fromBytes(byte[] bArr) {
            PasswordData passwordData = new PasswordData();
            ByteBuffer allocate = ByteBuffer.allocate(bArr.length);
            allocate.put(bArr, 0, bArr.length);
            allocate.flip();
            passwordData.credentialType = allocate.getInt();
            passwordData.scryptLogN = allocate.get();
            passwordData.scryptLogR = allocate.get();
            passwordData.scryptLogP = allocate.get();
            byte[] bArr2 = new byte[allocate.getInt()];
            passwordData.salt = bArr2;
            allocate.get(bArr2);
            int i = allocate.getInt();
            if (i > 0) {
                byte[] bArr3 = new byte[i];
                passwordData.passwordHandle = bArr3;
                allocate.get(bArr3);
            } else {
                passwordData.passwordHandle = null;
            }
            return passwordData;
        }

        public byte[] toBytes() {
            int length = this.salt.length + 11 + 4;
            byte[] bArr = this.passwordHandle;
            ByteBuffer allocate = ByteBuffer.allocate(length + (bArr != null ? bArr.length : 0));
            allocate.putInt(this.credentialType);
            allocate.put(this.scryptLogN);
            allocate.put(this.scryptLogR);
            allocate.put(this.scryptLogP);
            allocate.putInt(this.salt.length);
            allocate.put(this.salt);
            byte[] bArr2 = this.passwordHandle;
            if (bArr2 != null && bArr2.length > 0) {
                allocate.putInt(bArr2.length);
                allocate.put(this.passwordHandle);
            } else {
                allocate.putInt(0);
            }
            return allocate.array();
        }
    }

    /* loaded from: classes2.dex */
    public static class SyntheticPasswordBlob {
        public byte[] mContent;
        public byte mProtectorType;
        public byte mVersion;

        public static SyntheticPasswordBlob create(byte b, byte b2, byte[] bArr) {
            SyntheticPasswordBlob syntheticPasswordBlob = new SyntheticPasswordBlob();
            syntheticPasswordBlob.mVersion = b;
            syntheticPasswordBlob.mProtectorType = b2;
            syntheticPasswordBlob.mContent = bArr;
            return syntheticPasswordBlob;
        }

        public static SyntheticPasswordBlob fromBytes(byte[] bArr) {
            SyntheticPasswordBlob syntheticPasswordBlob = new SyntheticPasswordBlob();
            syntheticPasswordBlob.mVersion = bArr[0];
            syntheticPasswordBlob.mProtectorType = bArr[1];
            syntheticPasswordBlob.mContent = Arrays.copyOfRange(bArr, 2, bArr.length);
            return syntheticPasswordBlob;
        }

        public byte[] toByte() {
            byte[] bArr = this.mContent;
            byte[] bArr2 = new byte[bArr.length + 1 + 1];
            bArr2[0] = this.mVersion;
            bArr2[1] = this.mProtectorType;
            System.arraycopy(bArr, 0, bArr2, 2, bArr.length);
            return bArr2;
        }
    }

    /* loaded from: classes2.dex */
    public static class TokenData {
        public byte[] aggregatedSecret;
        public LockPatternUtils.EscrowTokenStateChangeCallback mCallback;
        public int mType;
        public byte[] secdiscardableOnDisk;
        public byte[] weaverSecret;

        public TokenData() {
        }
    }

    public SyntheticPasswordManager(Context context, LockSettingsStorage lockSettingsStorage, UserManager userManager, PasswordSlotManager passwordSlotManager) {
        this.mContext = context;
        this.mStorage = lockSettingsStorage;
        this.mUserManager = userManager;
        this.mPasswordSlotManager = passwordSlotManager;
    }

    public final boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    @VisibleForTesting
    public android.hardware.weaver.V1_0.IWeaver getWeaverHidlService() throws RemoteException {
        try {
            return android.hardware.weaver.V1_0.IWeaver.getService(true);
        } catch (NoSuchElementException unused) {
            return null;
        }
    }

    public final IWeaver getWeaverService() {
        try {
            IWeaver asInterface = IWeaver.Stub.asInterface(ServiceManager.waitForDeclaredService(IWeaver.DESCRIPTOR + "/default"));
            if (asInterface != null) {
                Slog.i("SyntheticPasswordManager", "Using AIDL weaver service");
                return asInterface;
            }
        } catch (SecurityException unused) {
            Slog.w("SyntheticPasswordManager", "Does not have permissions to get AIDL weaver service");
        }
        try {
            android.hardware.weaver.V1_0.IWeaver weaverHidlService = getWeaverHidlService();
            if (weaverHidlService != null) {
                Slog.i("SyntheticPasswordManager", "Using HIDL weaver service");
                return new WeaverHidlAdapter(weaverHidlService);
            }
        } catch (RemoteException e) {
            Slog.w("SyntheticPasswordManager", "Failed to get HIDL weaver service.", e);
        }
        Slog.w("SyntheticPasswordManager", "Device does not support weaver");
        return null;
    }

    public final synchronized boolean isWeaverAvailable() {
        if (this.mWeaver != null) {
            return true;
        }
        IWeaver weaverService = getWeaverService();
        if (weaverService == null) {
            return false;
        }
        try {
            WeaverConfig config = weaverService.getConfig();
            if (config != null && config.slots > 0) {
                this.mWeaver = weaverService;
                this.mWeaverConfig = config;
                this.mPasswordSlotManager.refreshActiveSlots(getUsedWeaverSlots());
                Slog.i("SyntheticPasswordManager", "Weaver service initialized");
                return true;
            }
            Slog.e("SyntheticPasswordManager", "Invalid weaver config");
            return false;
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e("SyntheticPasswordManager", "Failed to get weaver config", e);
            return false;
        }
    }

    public final byte[] weaverEnroll(int i, byte[] bArr, byte[] bArr2) {
        if (i != -1) {
            WeaverConfig weaverConfig = this.mWeaverConfig;
            if (i < weaverConfig.slots) {
                if (bArr == null) {
                    bArr = new byte[weaverConfig.keySize];
                } else if (bArr.length != weaverConfig.keySize) {
                    throw new IllegalArgumentException("Invalid key size for weaver");
                }
                if (bArr2 == null) {
                    bArr2 = SecureRandomUtils.randomBytes(weaverConfig.valueSize);
                }
                try {
                    this.mWeaver.write(i, bArr, bArr2);
                    return bArr2;
                } catch (ServiceSpecificException e) {
                    Slog.e("SyntheticPasswordManager", "weaver write failed, slot: " + i, e);
                    return null;
                } catch (RemoteException e2) {
                    Slog.e("SyntheticPasswordManager", "weaver write binder call failed, slot: " + i, e2);
                    return null;
                }
            }
        }
        throw new IllegalArgumentException("Invalid slot for weaver");
    }

    public static VerifyCredentialResponse responseFromTimeout(WeaverReadResponse weaverReadResponse) {
        long j = weaverReadResponse.timeout;
        return VerifyCredentialResponse.fromTimeout((j > 2147483647L || j < 0) ? Integer.MAX_VALUE : (int) j);
    }

    public final VerifyCredentialResponse weaverVerify(int i, byte[] bArr) {
        if (i != -1) {
            WeaverConfig weaverConfig = this.mWeaverConfig;
            if (i < weaverConfig.slots) {
                if (bArr == null) {
                    bArr = new byte[weaverConfig.keySize];
                } else if (bArr.length != weaverConfig.keySize) {
                    throw new IllegalArgumentException("Invalid key size for weaver");
                }
                try {
                    WeaverReadResponse read = this.mWeaver.read(i, bArr);
                    int i2 = read.status;
                    if (i2 != 0) {
                        if (i2 == 1) {
                            Slog.e("SyntheticPasswordManager", "weaver read failed (FAILED), slot: " + i);
                            return VerifyCredentialResponse.ERROR;
                        } else if (i2 == 2) {
                            if (read.timeout == 0) {
                                Slog.e("SyntheticPasswordManager", "weaver read failed (INCORRECT_KEY), slot: " + i);
                                return VerifyCredentialResponse.ERROR;
                            }
                            Slog.e("SyntheticPasswordManager", "weaver read failed (INCORRECT_KEY/THROTTLE), slot: " + i);
                            return responseFromTimeout(read);
                        } else if (i2 == 3) {
                            Slog.e("SyntheticPasswordManager", "weaver read failed (THROTTLE), slot: " + i);
                            return responseFromTimeout(read);
                        } else {
                            Slog.e("SyntheticPasswordManager", "weaver read unknown status " + read.status + ", slot: " + i);
                            return VerifyCredentialResponse.ERROR;
                        }
                    }
                    return new VerifyCredentialResponse.Builder().setGatekeeperHAT(read.value).build();
                } catch (RemoteException e) {
                    Slog.e("SyntheticPasswordManager", "weaver read failed, slot: " + i, e);
                    return VerifyCredentialResponse.ERROR;
                }
            }
        }
        throw new IllegalArgumentException("Invalid slot for weaver");
    }

    public void removeUser(IGateKeeperService iGateKeeperService, int i) {
        for (Long l : this.mStorage.listSyntheticPasswordProtectorsForUser("spblob", i)) {
            long longValue = l.longValue();
            destroyWeaverSlot(longValue, i);
            destroyProtectorKey(getProtectorKeyAlias(longValue));
        }
        try {
            iGateKeeperService.clearSecureUserId(fakeUserId(i));
        } catch (RemoteException unused) {
            Slog.w("SyntheticPasswordManager", "Failed to clear SID from gatekeeper");
        }
    }

    public int getCredentialType(long j, int i) {
        byte[] loadState = loadState("pwd", j, i);
        if (loadState == null) {
            return -1;
        }
        return PasswordData.fromBytes(loadState).credentialType;
    }

    public static int getFrpCredentialType(byte[] bArr) {
        if (bArr == null) {
            return -1;
        }
        return PasswordData.fromBytes(bArr).credentialType;
    }

    public SyntheticPassword newSyntheticPassword(int i) {
        clearSidForUser(i);
        SyntheticPassword create = SyntheticPassword.create();
        saveEscrowData(create, i);
        return create;
    }

    public void newSidForUser(IGateKeeperService iGateKeeperService, SyntheticPassword syntheticPassword, int i) {
        try {
            GateKeeperResponse enroll = iGateKeeperService.enroll(i, (byte[]) null, (byte[]) null, syntheticPassword.deriveGkPassword());
            if (enroll.getResponseCode() != 0) {
                throw new IllegalStateException("Fail to create new SID for user " + i + " response: " + enroll.getResponseCode());
            }
            saveSyntheticPasswordHandle(enroll.getPayload(), i);
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed to create new SID for user", e);
        }
    }

    public void clearSidForUser(int i) {
        destroyState("handle", 0L, i);
    }

    public boolean hasSidForUser(int i) {
        return hasState("handle", 0L, i);
    }

    public final byte[] loadSyntheticPasswordHandle(int i) {
        return loadState("handle", 0L, i);
    }

    public final void saveSyntheticPasswordHandle(byte[] bArr, int i) {
        saveState("handle", bArr, 0L, i);
        syncState(i);
    }

    public final boolean loadEscrowData(SyntheticPassword syntheticPassword, int i) {
        byte[] loadState = loadState("e0", 0L, i);
        byte[] loadState2 = loadState("p1", 0L, i);
        syntheticPassword.setEscrowData(loadState, loadState2);
        return (loadState == null || loadState2 == null) ? false : true;
    }

    public final void saveEscrowData(SyntheticPassword syntheticPassword, int i) {
        saveState("e0", syntheticPassword.mEncryptedEscrowSplit0, 0L, i);
        saveState("p1", syntheticPassword.mEscrowSplit1, 0L, i);
    }

    public boolean hasEscrowData(int i) {
        return hasState("e0", 0L, i) && hasState("p1", 0L, i);
    }

    public boolean hasAnyEscrowData(int i) {
        return hasState("e0", 0L, i) || hasState("p1", 0L, i);
    }

    public void destroyEscrowData(int i) {
        destroyState("e0", 0L, i);
        destroyState("p1", 0L, i);
    }

    public final int loadWeaverSlot(long j, int i) {
        byte[] loadState = loadState("weaver", j, i);
        if (loadState == null || loadState.length != 5) {
            return -1;
        }
        ByteBuffer allocate = ByteBuffer.allocate(5);
        allocate.put(loadState, 0, loadState.length);
        allocate.flip();
        if (allocate.get() != 1) {
            Slog.e("SyntheticPasswordManager", "Invalid weaver slot version for protector " + j);
            return -1;
        }
        return allocate.getInt();
    }

    public final void saveWeaverSlot(int i, long j, int i2) {
        ByteBuffer allocate = ByteBuffer.allocate(5);
        allocate.put((byte) 1);
        allocate.putInt(i);
        saveState("weaver", allocate.array(), j, i2);
    }

    public final void destroyWeaverSlot(long j, int i) {
        int loadWeaverSlot = loadWeaverSlot(j, i);
        destroyState("weaver", j, i);
        if (loadWeaverSlot != -1) {
            if (!isWeaverAvailable()) {
                Slog.e("SyntheticPasswordManager", "Cannot erase Weaver slot because Weaver is unavailable");
            } else if (!getUsedWeaverSlots().contains(Integer.valueOf(loadWeaverSlot))) {
                Slogf.m20i("SyntheticPasswordManager", "Erasing Weaver slot %d", Integer.valueOf(loadWeaverSlot));
                weaverEnroll(loadWeaverSlot, null, null);
                this.mPasswordSlotManager.markSlotDeleted(loadWeaverSlot);
            } else {
                Slogf.m20i("SyntheticPasswordManager", "Weaver slot %d was already reused; not erasing it", Integer.valueOf(loadWeaverSlot));
            }
        }
    }

    public final Set<Integer> getUsedWeaverSlots() {
        Map<Integer, List<Long>> listSyntheticPasswordProtectorsForAllUsers = this.mStorage.listSyntheticPasswordProtectorsForAllUsers("weaver");
        HashSet hashSet = new HashSet();
        for (Map.Entry<Integer, List<Long>> entry : listSyntheticPasswordProtectorsForAllUsers.entrySet()) {
            for (Long l : entry.getValue()) {
                hashSet.add(Integer.valueOf(loadWeaverSlot(l.longValue(), entry.getKey().intValue())));
            }
        }
        return hashSet;
    }

    public final int getNextAvailableWeaverSlot() {
        LockSettingsStorage.PersistentData readPersistentDataBlock;
        Set<Integer> usedWeaverSlots = getUsedWeaverSlots();
        usedWeaverSlots.addAll(this.mPasswordSlotManager.getUsedSlots());
        if (!isDeviceProvisioned() && (readPersistentDataBlock = this.mStorage.readPersistentDataBlock()) != null && readPersistentDataBlock.type == 2) {
            usedWeaverSlots.add(Integer.valueOf(readPersistentDataBlock.userId));
        }
        for (int i = 0; i < this.mWeaverConfig.slots; i++) {
            if (!usedWeaverSlots.contains(Integer.valueOf(i))) {
                return i;
            }
        }
        throw new IllegalStateException("Run out of weaver slots.");
    }

    public long createLskfBasedProtector(IGateKeeperService iGateKeeperService, LockscreenCredential lockscreenCredential, SyntheticPassword syntheticPassword, int i) {
        PasswordData create;
        LockscreenCredential lockscreenCredential2;
        byte[] transformUnderSecdiscardable;
        long generateProtectorId = generateProtectorId();
        if (lockscreenCredential.isNone()) {
            lockscreenCredential2 = lockscreenCredential;
            create = null;
        } else {
            create = PasswordData.create(lockscreenCredential.getType());
            lockscreenCredential2 = lockscreenCredential;
        }
        byte[] stretchLskf = stretchLskf(lockscreenCredential2, create);
        Slogf.m20i("SyntheticPasswordManager", "Creating LSKF-based protector %016x for user %d", Long.valueOf(generateProtectorId), Integer.valueOf(i));
        long j = 0;
        if (isWeaverAvailable()) {
            int nextAvailableWeaverSlot = getNextAvailableWeaverSlot();
            Slogf.m20i("SyntheticPasswordManager", "Enrolling LSKF for user %d into Weaver slot %d", Integer.valueOf(i), Integer.valueOf(nextAvailableWeaverSlot));
            byte[] weaverEnroll = weaverEnroll(nextAvailableWeaverSlot, stretchedLskfToWeaverKey(stretchLskf), null);
            if (weaverEnroll == null) {
                throw new IllegalStateException("Fail to enroll user password under weaver " + i);
            }
            saveWeaverSlot(nextAvailableWeaverSlot, generateProtectorId, i);
            this.mPasswordSlotManager.markSlotInUse(nextAvailableWeaverSlot);
            synchronizeWeaverFrpPassword(create, 0, i, nextAvailableWeaverSlot);
            transformUnderSecdiscardable = transformUnderWeaverSecret(stretchLskf, weaverEnroll);
        } else {
            if (!lockscreenCredential.isNone()) {
                try {
                    iGateKeeperService.clearSecureUserId(fakeUserId(i));
                } catch (RemoteException unused) {
                    Slog.w("SyntheticPasswordManager", "Failed to clear SID from gatekeeper");
                }
                Slogf.m20i("SyntheticPasswordManager", "Enrolling LSKF for user %d into Gatekeeper", Integer.valueOf(i));
                try {
                    GateKeeperResponse enroll = iGateKeeperService.enroll(fakeUserId(i), (byte[]) null, (byte[]) null, stretchedLskfToGkPassword(stretchLskf));
                    if (enroll.getResponseCode() != 0) {
                        throw new IllegalStateException("Failed to enroll LSKF for new SP protector for user " + i);
                    }
                    byte[] payload = enroll.getPayload();
                    create.passwordHandle = payload;
                    j = sidFromPasswordHandle(payload);
                } catch (RemoteException e) {
                    throw new IllegalStateException("Failed to enroll LSKF for new SP protector for user " + i, e);
                }
            }
            transformUnderSecdiscardable = transformUnderSecdiscardable(stretchLskf, createSecdiscardable(generateProtectorId, i));
            synchronizeGatekeeperFrpPassword(create, 0, i);
        }
        byte[] bArr = transformUnderSecdiscardable;
        if (!lockscreenCredential.isNone()) {
            saveState("pwd", create.toBytes(), generateProtectorId, i);
            savePasswordMetrics(lockscreenCredential, syntheticPassword, generateProtectorId, i);
        }
        createSyntheticPasswordBlob(generateProtectorId, (byte) 0, syntheticPassword, bArr, j, i);
        syncState(i);
        return generateProtectorId;
    }

    public VerifyCredentialResponse verifyFrpCredential(IGateKeeperService iGateKeeperService, LockscreenCredential lockscreenCredential, ICheckCredentialProgressCallback iCheckCredentialProgressCallback) {
        LockSettingsStorage.PersistentData readPersistentDataBlock = this.mStorage.readPersistentDataBlock();
        int i = readPersistentDataBlock.type;
        if (i == 1) {
            PasswordData fromBytes = PasswordData.fromBytes(readPersistentDataBlock.payload);
            try {
                return VerifyCredentialResponse.fromGateKeeperResponse(iGateKeeperService.verifyChallenge(fakeUserId(readPersistentDataBlock.userId), 0L, fromBytes.passwordHandle, stretchedLskfToGkPassword(stretchLskf(lockscreenCredential, fromBytes))));
            } catch (RemoteException e) {
                Slog.e("SyntheticPasswordManager", "FRP verifyChallenge failed", e);
                return VerifyCredentialResponse.ERROR;
            }
        } else if (i == 2) {
            if (!isWeaverAvailable()) {
                Slog.e("SyntheticPasswordManager", "No weaver service to verify SP-based FRP credential");
                return VerifyCredentialResponse.ERROR;
            }
            return weaverVerify(readPersistentDataBlock.userId, stretchedLskfToWeaverKey(stretchLskf(lockscreenCredential, PasswordData.fromBytes(readPersistentDataBlock.payload)))).stripPayload();
        } else {
            Slog.e("SyntheticPasswordManager", "persistentData.type must be TYPE_SP_GATEKEEPER or TYPE_SP_WEAVER, but is " + readPersistentDataBlock.type);
            return VerifyCredentialResponse.ERROR;
        }
    }

    public void migrateFrpPasswordLocked(long j, UserInfo userInfo, int i) {
        if (this.mStorage.getPersistentDataBlockManager() == null || !LockPatternUtils.userOwnsFrpCredential(this.mContext, userInfo) || getCredentialType(j, userInfo.id) == -1) {
            return;
        }
        Slog.i("SyntheticPasswordManager", "Migrating FRP credential to persistent data block");
        PasswordData fromBytes = PasswordData.fromBytes(loadState("pwd", j, userInfo.id));
        int loadWeaverSlot = loadWeaverSlot(j, userInfo.id);
        if (loadWeaverSlot != -1) {
            synchronizeWeaverFrpPassword(fromBytes, i, userInfo.id, loadWeaverSlot);
        } else {
            synchronizeGatekeeperFrpPassword(fromBytes, i, userInfo.id);
        }
    }

    public static boolean isNoneCredential(PasswordData passwordData) {
        return passwordData == null || passwordData.credentialType == -1;
    }

    public final boolean shouldSynchronizeFrpCredential(PasswordData passwordData, int i) {
        if (this.mStorage.getPersistentDataBlockManager() == null) {
            return false;
        }
        if (LockPatternUtils.userOwnsFrpCredential(this.mContext, this.mUserManager.getUserInfo(i))) {
            if (!isNoneCredential(passwordData) || isDeviceProvisioned()) {
                return true;
            }
            Slog.d("SyntheticPasswordManager", "Not clearing FRP credential yet because device is not yet provisioned");
            return false;
        }
        return false;
    }

    public final void synchronizeGatekeeperFrpPassword(PasswordData passwordData, int i, int i2) {
        if (shouldSynchronizeFrpCredential(passwordData, i2)) {
            Slogf.m28d("SyntheticPasswordManager", "Syncing Gatekeeper-based FRP credential tied to user %d", Integer.valueOf(i2));
            if (!isNoneCredential(passwordData)) {
                this.mStorage.writePersistentDataBlock(1, i2, i, passwordData.toBytes());
            } else {
                this.mStorage.writePersistentDataBlock(0, i2, 0, null);
            }
        }
    }

    public final void synchronizeWeaverFrpPassword(PasswordData passwordData, int i, int i2, int i3) {
        if (shouldSynchronizeFrpCredential(passwordData, i2)) {
            Slogf.m28d("SyntheticPasswordManager", "Syncing Weaver-based FRP credential tied to user %d", Integer.valueOf(i2));
            if (!isNoneCredential(passwordData)) {
                this.mStorage.writePersistentDataBlock(2, i3, i, passwordData.toBytes());
            } else {
                this.mStorage.writePersistentDataBlock(0, 0, 0, null);
            }
        }
    }

    public long addPendingToken(byte[] bArr, int i, int i2, LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback) {
        long generateProtectorId = generateProtectorId();
        if (!this.tokenMap.containsKey(Integer.valueOf(i2))) {
            this.tokenMap.put(Integer.valueOf(i2), new ArrayMap<>());
        }
        TokenData tokenData = new TokenData();
        tokenData.mType = i;
        byte[] randomBytes = SecureRandomUtils.randomBytes(16384);
        if (isWeaverAvailable()) {
            byte[] randomBytes2 = SecureRandomUtils.randomBytes(this.mWeaverConfig.valueSize);
            tokenData.weaverSecret = randomBytes2;
            tokenData.secdiscardableOnDisk = SyntheticPasswordCrypto.encrypt(randomBytes2, PERSONALIZATION_WEAVER_TOKEN, randomBytes);
        } else {
            tokenData.secdiscardableOnDisk = randomBytes;
            tokenData.weaverSecret = null;
        }
        tokenData.aggregatedSecret = transformUnderSecdiscardable(bArr, randomBytes);
        tokenData.mCallback = escrowTokenStateChangeCallback;
        this.tokenMap.get(Integer.valueOf(i2)).put(Long.valueOf(generateProtectorId), tokenData);
        return generateProtectorId;
    }

    public Set<Long> getPendingTokensForUser(int i) {
        if (!this.tokenMap.containsKey(Integer.valueOf(i))) {
            return Collections.emptySet();
        }
        return new ArraySet(this.tokenMap.get(Integer.valueOf(i)).keySet());
    }

    public boolean removePendingToken(long j, int i) {
        return this.tokenMap.containsKey(Integer.valueOf(i)) && this.tokenMap.get(Integer.valueOf(i)).remove(Long.valueOf(j)) != null;
    }

    public boolean createTokenBasedProtector(long j, SyntheticPassword syntheticPassword, int i) {
        TokenData tokenData;
        if (this.tokenMap.containsKey(Integer.valueOf(i)) && (tokenData = this.tokenMap.get(Integer.valueOf(i)).get(Long.valueOf(j))) != null) {
            if (!loadEscrowData(syntheticPassword, i)) {
                Slog.w("SyntheticPasswordManager", "User is not escrowable");
                return false;
            }
            Slogf.m20i("SyntheticPasswordManager", "Creating token-based protector %016x for user %d", Long.valueOf(j), Integer.valueOf(i));
            if (isWeaverAvailable()) {
                int nextAvailableWeaverSlot = getNextAvailableWeaverSlot();
                Slogf.m20i("SyntheticPasswordManager", "Using Weaver slot %d for new token-based protector", Integer.valueOf(nextAvailableWeaverSlot));
                if (weaverEnroll(nextAvailableWeaverSlot, null, tokenData.weaverSecret) == null) {
                    Slog.e("SyntheticPasswordManager", "Failed to enroll weaver secret when activating token");
                    return false;
                }
                saveWeaverSlot(nextAvailableWeaverSlot, j, i);
                this.mPasswordSlotManager.markSlotInUse(nextAvailableWeaverSlot);
            }
            saveSecdiscardable(j, tokenData.secdiscardableOnDisk, i);
            createSyntheticPasswordBlob(j, getTokenBasedProtectorType(tokenData.mType), syntheticPassword, tokenData.aggregatedSecret, 0L, i);
            syncState(i);
            this.tokenMap.get(Integer.valueOf(i)).remove(Long.valueOf(j));
            LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback = tokenData.mCallback;
            if (escrowTokenStateChangeCallback != null) {
                escrowTokenStateChangeCallback.onEscrowTokenActivated(j, i);
                return true;
            }
            return true;
        }
        return false;
    }

    public final void createSyntheticPasswordBlob(long j, byte b, SyntheticPassword syntheticPassword, byte[] bArr, long j2, int i) {
        byte[] escrowSecret;
        if (b == 1 || b == 2) {
            escrowSecret = syntheticPassword.getEscrowSecret();
        } else {
            escrowSecret = syntheticPassword.getSyntheticPassword();
        }
        saveState("spblob", SyntheticPasswordBlob.create(syntheticPassword.mVersion == 3 ? (byte) 3 : (byte) 2, b, createSpBlob(getProtectorKeyAlias(j), escrowSecret, bArr, j2)).toByte(), j, i);
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0160  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AuthenticationResult unlockLskfBasedProtector(IGateKeeperService iGateKeeperService, long j, LockscreenCredential lockscreenCredential, int i, ICheckCredentialProgressCallback iCheckCredentialProgressCallback) {
        PasswordData passwordData;
        int i2;
        byte[] loadSecdiscardable;
        byte[] transformUnderSecdiscardable;
        GateKeeperResponse gateKeeperResponse;
        AuthenticationResult authenticationResult = new AuthenticationResult();
        long j2 = 0;
        if (j == 0) {
            Slogf.wtf("SyntheticPasswordManager", "Synthetic password not found for user %d", Integer.valueOf(i));
            authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
            return authenticationResult;
        }
        byte[] loadState = loadState("pwd", j, i);
        if (loadState != null) {
            PasswordData fromBytes = PasswordData.fromBytes(loadState);
            i2 = fromBytes.credentialType;
            passwordData = fromBytes;
        } else {
            passwordData = null;
            i2 = -1;
        }
        if (!lockscreenCredential.checkAgainstStoredType(i2)) {
            Slogf.m24e("SyntheticPasswordManager", "Credential type mismatch: stored type is %s but provided type is %s", LockPatternUtils.credentialTypeToString(i2), LockPatternUtils.credentialTypeToString(lockscreenCredential.getType()));
            authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
            return authenticationResult;
        }
        byte[] stretchLskf = stretchLskf(lockscreenCredential, passwordData);
        int loadWeaverSlot = loadWeaverSlot(j, i);
        if (loadWeaverSlot != -1) {
            if (!isWeaverAvailable()) {
                Slog.e("SyntheticPasswordManager", "Protector uses Weaver, but Weaver is unavailable");
                authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                return authenticationResult;
            }
            VerifyCredentialResponse weaverVerify = weaverVerify(loadWeaverSlot, stretchedLskfToWeaverKey(stretchLskf));
            authenticationResult.gkResponse = weaverVerify;
            if (weaverVerify.getResponseCode() != 0) {
                return authenticationResult;
            }
            transformUnderSecdiscardable = transformUnderWeaverSecret(stretchLskf, authenticationResult.gkResponse.getGatekeeperHAT());
        } else {
            if (passwordData == null || passwordData.passwordHandle == null) {
                if (!lockscreenCredential.isNone()) {
                    Slog.e("SyntheticPasswordManager", "Missing Gatekeeper password handle for nonempty LSKF");
                    authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                    return authenticationResult;
                }
            } else {
                byte[] stretchedLskfToGkPassword = stretchedLskfToGkPassword(stretchLskf);
                try {
                    GateKeeperResponse verifyChallenge = iGateKeeperService.verifyChallenge(fakeUserId(i), 0L, passwordData.passwordHandle, stretchedLskfToGkPassword);
                    int responseCode = verifyChallenge.getResponseCode();
                    if (responseCode != 0) {
                        if (responseCode == 1) {
                            authenticationResult.gkResponse = VerifyCredentialResponse.fromTimeout(verifyChallenge.getTimeout());
                            return authenticationResult;
                        }
                        authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                        return authenticationResult;
                    }
                    authenticationResult.gkResponse = VerifyCredentialResponse.OK;
                    if (verifyChallenge.getShouldReEnroll()) {
                        try {
                            try {
                                gateKeeperResponse = iGateKeeperService.enroll(fakeUserId(i), passwordData.passwordHandle, stretchedLskfToGkPassword, stretchedLskfToGkPassword);
                            } catch (RemoteException e) {
                                e = e;
                                Slog.w("SyntheticPasswordManager", "Fail to invoke gatekeeper.enroll", e);
                                gateKeeperResponse = GateKeeperResponse.ERROR;
                                if (gateKeeperResponse.getResponseCode() != 0) {
                                }
                                j2 = sidFromPasswordHandle(passwordData.passwordHandle);
                                loadSecdiscardable = loadSecdiscardable(j, i);
                                if (loadSecdiscardable != null) {
                                }
                            }
                        } catch (RemoteException e2) {
                            e = e2;
                        }
                        if (gateKeeperResponse.getResponseCode() != 0) {
                            passwordData.passwordHandle = gateKeeperResponse.getPayload();
                            passwordData.credentialType = lockscreenCredential.getType();
                            saveState("pwd", passwordData.toBytes(), j, i);
                            syncState(i);
                            synchronizeGatekeeperFrpPassword(passwordData, 0, i);
                        } else {
                            Slog.w("SyntheticPasswordManager", "Fail to re-enroll user password for user " + i);
                        }
                    }
                    j2 = sidFromPasswordHandle(passwordData.passwordHandle);
                } catch (RemoteException e3) {
                    Slog.e("SyntheticPasswordManager", "gatekeeper verify failed", e3);
                    authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                    return authenticationResult;
                }
            }
            loadSecdiscardable = loadSecdiscardable(j, i);
            if (loadSecdiscardable != null) {
                Slog.e("SyntheticPasswordManager", "secdiscardable file not found");
                authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                return authenticationResult;
            }
            transformUnderSecdiscardable = transformUnderSecdiscardable(stretchLskf, loadSecdiscardable);
        }
        long j3 = j2;
        byte[] bArr = transformUnderSecdiscardable;
        if (iCheckCredentialProgressCallback != null) {
            try {
                iCheckCredentialProgressCallback.onCredentialVerified();
            } catch (RemoteException e4) {
                Slog.w("SyntheticPasswordManager", "progressCallback throws exception", e4);
            }
        }
        SyntheticPassword unwrapSyntheticPasswordBlob = unwrapSyntheticPasswordBlob(j, (byte) 0, bArr, j3, i);
        authenticationResult.syntheticPassword = unwrapSyntheticPasswordBlob;
        authenticationResult.gkResponse = verifyChallenge(iGateKeeperService, unwrapSyntheticPasswordBlob, 0L, i);
        if (authenticationResult.syntheticPassword != null && !lockscreenCredential.isNone() && !hasPasswordMetrics(j, i)) {
            savePasswordMetrics(lockscreenCredential, authenticationResult.syntheticPassword, j, i);
            syncState(i);
        }
        return authenticationResult;
    }

    public AuthenticationResult unlockTokenBasedProtector(IGateKeeperService iGateKeeperService, long j, byte[] bArr, int i) {
        return unlockTokenBasedProtectorInternal(iGateKeeperService, j, SyntheticPasswordBlob.fromBytes(loadState("spblob", j, i)).mProtectorType, bArr, i);
    }

    public AuthenticationResult unlockWeakTokenBasedProtector(IGateKeeperService iGateKeeperService, long j, byte[] bArr, int i) {
        return unlockTokenBasedProtectorInternal(iGateKeeperService, j, (byte) 2, bArr, i);
    }

    public final AuthenticationResult unlockTokenBasedProtectorInternal(IGateKeeperService iGateKeeperService, long j, byte b, byte[] bArr, int i) {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        byte[] loadSecdiscardable = loadSecdiscardable(j, i);
        if (loadSecdiscardable == null) {
            Slog.e("SyntheticPasswordManager", "secdiscardable file not found");
            authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
            return authenticationResult;
        }
        int loadWeaverSlot = loadWeaverSlot(j, i);
        if (loadWeaverSlot != -1) {
            if (!isWeaverAvailable()) {
                Slog.e("SyntheticPasswordManager", "Protector uses Weaver, but Weaver is unavailable");
                authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                return authenticationResult;
            }
            VerifyCredentialResponse weaverVerify = weaverVerify(loadWeaverSlot, null);
            if (weaverVerify.getResponseCode() != 0 || weaverVerify.getGatekeeperHAT() == null) {
                Slog.e("SyntheticPasswordManager", "Failed to retrieve Weaver secret when unlocking token-based protector");
                authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
                return authenticationResult;
            }
            loadSecdiscardable = SyntheticPasswordCrypto.decrypt(weaverVerify.getGatekeeperHAT(), PERSONALIZATION_WEAVER_TOKEN, loadSecdiscardable);
        }
        SyntheticPassword unwrapSyntheticPasswordBlob = unwrapSyntheticPasswordBlob(j, b, transformUnderSecdiscardable(bArr, loadSecdiscardable), 0L, i);
        authenticationResult.syntheticPassword = unwrapSyntheticPasswordBlob;
        if (unwrapSyntheticPasswordBlob != null) {
            VerifyCredentialResponse verifyChallenge = verifyChallenge(iGateKeeperService, unwrapSyntheticPasswordBlob, 0L, i);
            authenticationResult.gkResponse = verifyChallenge;
            if (verifyChallenge == null) {
                authenticationResult.gkResponse = VerifyCredentialResponse.OK;
            }
        } else {
            authenticationResult.gkResponse = VerifyCredentialResponse.ERROR;
        }
        return authenticationResult;
    }

    public final SyntheticPassword unwrapSyntheticPasswordBlob(long j, byte b, byte[] bArr, long j2, int i) {
        byte[] decryptSpBlob;
        byte[] loadState = loadState("spblob", j, i);
        if (loadState == null) {
            return null;
        }
        SyntheticPasswordBlob fromBytes = SyntheticPasswordBlob.fromBytes(loadState);
        byte b2 = fromBytes.mVersion;
        if (b2 != 3 && b2 != 2 && b2 != 1) {
            throw new IllegalArgumentException("Unknown blob version: " + ((int) fromBytes.mVersion));
        } else if (fromBytes.mProtectorType != b) {
            throw new IllegalArgumentException("Invalid protector type: " + ((int) fromBytes.mProtectorType));
        } else {
            if (b2 == 1) {
                decryptSpBlob = SyntheticPasswordCrypto.decryptBlobV1(getProtectorKeyAlias(j), fromBytes.mContent, bArr);
            } else {
                decryptSpBlob = decryptSpBlob(getProtectorKeyAlias(j), fromBytes.mContent, bArr);
            }
            if (decryptSpBlob == null) {
                Slog.e("SyntheticPasswordManager", "Fail to decrypt SP for user " + i);
                return null;
            }
            SyntheticPassword syntheticPassword = new SyntheticPassword(fromBytes.mVersion);
            byte b3 = fromBytes.mProtectorType;
            if (b3 == 1 || b3 == 2) {
                if (!loadEscrowData(syntheticPassword, i)) {
                    Slog.e("SyntheticPasswordManager", "User is not escrowable: " + i);
                    return null;
                }
                syntheticPassword.recreateFromEscrow(decryptSpBlob);
            } else {
                syntheticPassword.recreateDirectly(decryptSpBlob);
            }
            if (fromBytes.mVersion == 1) {
                Slog.i("SyntheticPasswordManager", "Upgrading v1 SP blob for user " + i + ", protectorType = " + ((int) fromBytes.mProtectorType));
                createSyntheticPasswordBlob(j, fromBytes.mProtectorType, syntheticPassword, bArr, j2, i);
                syncState(i);
            }
            return syntheticPassword;
        }
    }

    public VerifyCredentialResponse verifyChallenge(IGateKeeperService iGateKeeperService, SyntheticPassword syntheticPassword, long j, int i) {
        return verifyChallengeInternal(iGateKeeperService, syntheticPassword.deriveGkPassword(), j, i);
    }

    public VerifyCredentialResponse verifyChallengeInternal(IGateKeeperService iGateKeeperService, byte[] bArr, long j, int i) {
        GateKeeperResponse gateKeeperResponse;
        byte[] loadSyntheticPasswordHandle = loadSyntheticPasswordHandle(i);
        if (loadSyntheticPasswordHandle == null) {
            return null;
        }
        try {
            GateKeeperResponse verifyChallenge = iGateKeeperService.verifyChallenge(i, j, loadSyntheticPasswordHandle, bArr);
            int responseCode = verifyChallenge.getResponseCode();
            if (responseCode != 0) {
                if (responseCode == 1) {
                    return VerifyCredentialResponse.fromTimeout(verifyChallenge.getTimeout());
                }
                return VerifyCredentialResponse.ERROR;
            }
            VerifyCredentialResponse build = new VerifyCredentialResponse.Builder().setGatekeeperHAT(verifyChallenge.getPayload()).build();
            if (verifyChallenge.getShouldReEnroll()) {
                try {
                    gateKeeperResponse = iGateKeeperService.enroll(i, loadSyntheticPasswordHandle, loadSyntheticPasswordHandle, bArr);
                } catch (RemoteException e) {
                    Slog.e("SyntheticPasswordManager", "Failed to invoke gatekeeper.enroll", e);
                    gateKeeperResponse = GateKeeperResponse.ERROR;
                }
                if (gateKeeperResponse.getResponseCode() == 0) {
                    saveSyntheticPasswordHandle(gateKeeperResponse.getPayload(), i);
                    return verifyChallengeInternal(iGateKeeperService, bArr, j, i);
                }
                Slog.w("SyntheticPasswordManager", "Fail to re-enroll SP handle for user " + i);
            }
            return build;
        } catch (RemoteException e2) {
            Slog.e("SyntheticPasswordManager", "Fail to verify with gatekeeper " + i, e2);
            return VerifyCredentialResponse.ERROR;
        }
    }

    public boolean protectorExists(long j, int i) {
        return hasState("spblob", j, i);
    }

    public void destroyTokenBasedProtector(long j, int i) {
        Slogf.m20i("SyntheticPasswordManager", "Destroying token-based protector %016x for user %d", Long.valueOf(j), Integer.valueOf(i));
        SyntheticPasswordBlob fromBytes = SyntheticPasswordBlob.fromBytes(loadState("spblob", j, i));
        destroyProtectorCommon(j, i);
        if (fromBytes.mProtectorType == 2) {
            notifyWeakEscrowTokenRemovedListeners(j, i);
        }
    }

    public void destroyAllWeakTokenBasedProtectors(int i) {
        for (Long l : this.mStorage.listSyntheticPasswordProtectorsForUser("secdis", i)) {
            long longValue = l.longValue();
            if (SyntheticPasswordBlob.fromBytes(loadState("spblob", longValue, i)).mProtectorType == 2) {
                destroyTokenBasedProtector(longValue, i);
            }
        }
    }

    public void destroyLskfBasedProtector(long j, int i) {
        Slogf.m20i("SyntheticPasswordManager", "Destroying LSKF-based protector %016x for user %d", Long.valueOf(j), Integer.valueOf(i));
        destroyProtectorCommon(j, i);
        destroyState("pwd", j, i);
        destroyState("metrics", j, i);
    }

    public final void destroyProtectorCommon(long j, int i) {
        destroyState("spblob", j, i);
        destroyProtectorKey(getProtectorKeyAlias(j));
        destroyState("secdis", j, i);
        if (hasState("weaver", j, i)) {
            destroyWeaverSlot(j, i);
        }
    }

    public final byte[] transformUnderWeaverSecret(byte[] bArr, byte[] bArr2) {
        return ArrayUtils.concat(new byte[][]{bArr, SyntheticPasswordCrypto.personalizedHash(PERSONALIZATION_WEAVER_PASSWORD, bArr2)});
    }

    public final byte[] transformUnderSecdiscardable(byte[] bArr, byte[] bArr2) {
        return ArrayUtils.concat(new byte[][]{bArr, SyntheticPasswordCrypto.personalizedHash(PERSONALIZATION_SECDISCARDABLE, bArr2)});
    }

    public final byte[] createSecdiscardable(long j, int i) {
        byte[] randomBytes = SecureRandomUtils.randomBytes(16384);
        saveSecdiscardable(j, randomBytes, i);
        return randomBytes;
    }

    public final void saveSecdiscardable(long j, byte[] bArr, int i) {
        saveState("secdis", bArr, j, i);
    }

    public final byte[] loadSecdiscardable(long j, int i) {
        return loadState("secdis", j, i);
    }

    @VisibleForTesting
    public boolean hasPasswordData(long j, int i) {
        return hasState("pwd", j, i);
    }

    public PasswordMetrics getPasswordMetrics(SyntheticPassword syntheticPassword, long j, int i) {
        byte[] loadState = loadState("metrics", j, i);
        if (loadState == null) {
            Slogf.m24e("SyntheticPasswordManager", "Failed to read password metrics file for user %d", Integer.valueOf(i));
            return null;
        }
        byte[] decrypt = SyntheticPasswordCrypto.decrypt(syntheticPassword.deriveMetricsKey(), new byte[0], loadState);
        if (decrypt == null) {
            Slogf.m24e("SyntheticPasswordManager", "Failed to decrypt password metrics file for user %d", Integer.valueOf(i));
            return null;
        }
        return VersionedPasswordMetrics.deserialize(decrypt).getMetrics();
    }

    public final void savePasswordMetrics(LockscreenCredential lockscreenCredential, SyntheticPassword syntheticPassword, long j, int i) {
        saveState("metrics", SyntheticPasswordCrypto.encrypt(syntheticPassword.deriveMetricsKey(), new byte[0], new VersionedPasswordMetrics(lockscreenCredential).serialize()), j, i);
    }

    @VisibleForTesting
    public boolean hasPasswordMetrics(long j, int i) {
        return hasState("metrics", j, i);
    }

    public final boolean hasState(String str, long j, int i) {
        return !ArrayUtils.isEmpty(loadState(str, j, i));
    }

    public final byte[] loadState(String str, long j, int i) {
        return this.mStorage.readSyntheticPasswordState(i, j, str);
    }

    public final void saveState(String str, byte[] bArr, long j, int i) {
        this.mStorage.writeSyntheticPasswordState(i, j, str, bArr);
    }

    public final void syncState(int i) {
        this.mStorage.syncSyntheticPasswordState(i);
    }

    public final void destroyState(String str, long j, int i) {
        this.mStorage.deleteSyntheticPasswordState(i, j, str);
    }

    @VisibleForTesting
    public byte[] decryptSpBlob(String str, byte[] bArr, byte[] bArr2) {
        return SyntheticPasswordCrypto.decryptBlob(str, bArr, bArr2);
    }

    @VisibleForTesting
    public byte[] createSpBlob(String str, byte[] bArr, byte[] bArr2, long j) {
        return SyntheticPasswordCrypto.createBlob(str, bArr, bArr2, j);
    }

    @VisibleForTesting
    public void destroyProtectorKey(String str) {
        SyntheticPasswordCrypto.destroyProtectorKey(str);
    }

    public static long generateProtectorId() {
        long randomLong;
        do {
            randomLong = SecureRandomUtils.randomLong();
        } while (randomLong == 0);
        return randomLong;
    }

    public final String getProtectorKeyAlias(long j) {
        return TextUtils.formatSimple("%s%x", new Object[]{"synthetic_password_", Long.valueOf(j)});
    }

    @VisibleForTesting
    public byte[] stretchLskf(LockscreenCredential lockscreenCredential, PasswordData passwordData) {
        byte[] credential = lockscreenCredential.isNone() ? DEFAULT_PASSWORD : lockscreenCredential.getCredential();
        if (passwordData == null) {
            Preconditions.checkArgument(lockscreenCredential.isNone());
            return Arrays.copyOf(credential, 32);
        }
        return scrypt(credential, passwordData.salt, 1 << passwordData.scryptLogN, 1 << passwordData.scryptLogR, 1 << passwordData.scryptLogP, 32);
    }

    public final byte[] stretchedLskfToGkPassword(byte[] bArr) {
        return SyntheticPasswordCrypto.personalizedHash(PERSONALIZATION_USER_GK_AUTH, bArr);
    }

    public final byte[] stretchedLskfToWeaverKey(byte[] bArr) {
        byte[] personalizedHash = SyntheticPasswordCrypto.personalizedHash(PERSONALIZATION_WEAVER_KEY, bArr);
        int length = personalizedHash.length;
        int i = this.mWeaverConfig.keySize;
        if (length < i) {
            throw new IllegalArgumentException("weaver key length too small");
        }
        return Arrays.copyOf(personalizedHash, i);
    }

    @VisibleForTesting
    public long sidFromPasswordHandle(byte[] bArr) {
        return nativeSidFromPasswordHandle(bArr);
    }

    @VisibleForTesting
    public byte[] scrypt(byte[] bArr, byte[] bArr2, int i, int i2, int i3, int i4) {
        return new Scrypt().scrypt(bArr, bArr2, i, i2, i3, i4);
    }

    @VisibleForTesting
    public static byte[] bytesToHex(byte[] bArr) {
        return HexEncoding.encodeToString(bArr).getBytes();
    }

    public boolean migrateKeyNamespace() {
        boolean z = true;
        for (List<Long> list : this.mStorage.listSyntheticPasswordProtectorsForAllUsers("spblob").values()) {
            for (Long l : list) {
                z &= SyntheticPasswordCrypto.migrateLockSettingsKey(getProtectorKeyAlias(l.longValue()));
            }
        }
        return z;
    }

    public boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) {
        return this.mListeners.register(iWeakEscrowTokenRemovedListener);
    }

    public boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) {
        return this.mListeners.unregister(iWeakEscrowTokenRemovedListener);
    }

    public final void notifyWeakEscrowTokenRemovedListeners(long j, int i) {
        int beginBroadcast = this.mListeners.beginBroadcast();
        while (beginBroadcast > 0) {
            beginBroadcast--;
            try {
                try {
                    this.mListeners.getBroadcastItem(beginBroadcast).onWeakEscrowTokenRemoved(j, i);
                } catch (RemoteException e) {
                    Slog.e("SyntheticPasswordManager", "Exception while notifying WeakEscrowTokenRemovedListener.", e);
                }
            } finally {
                this.mListeners.finishBroadcast();
            }
        }
    }

    public void writeVendorAuthSecret(byte[] bArr, SyntheticPassword syntheticPassword, int i) {
        saveState("vendor_auth_secret", SyntheticPasswordCrypto.encrypt(syntheticPassword.deriveVendorAuthSecretEncryptionKey(), new byte[0], bArr), 0L, i);
        syncState(i);
    }

    public byte[] readVendorAuthSecret(SyntheticPassword syntheticPassword, int i) {
        byte[] loadState = loadState("vendor_auth_secret", 0L, i);
        if (loadState == null) {
            return null;
        }
        return SyntheticPasswordCrypto.decrypt(syntheticPassword.deriveVendorAuthSecretEncryptionKey(), new byte[0], loadState);
    }
}
