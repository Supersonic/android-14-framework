package com.android.server.locksettings;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteLockscreenValidationResult;
import android.app.StartLockscreenValidationRequest;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.DeviceStateCache;
import android.app.admin.PasswordMetrics;
import android.app.trust.IStrongAuthTracker;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.authsecret.IAuthSecret;
import android.hardware.biometrics.BiometricManager;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IProgressListener;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IStorageManager;
import android.os.storage.StorageManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.security.AndroidKeyStoreMaintenance;
import android.security.Authorization;
import android.security.keystore.KeyProtection;
import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.RecoveryCertPath;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import android.security.keystore2.AndroidKeyStoreProvider;
import android.service.gatekeeper.IGateKeeperService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.IWeakEscrowTokenActivatedListener;
import com.android.internal.widget.IWeakEscrowTokenRemovedListener;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockSettingsInternal;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.RebootEscrowListener;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.locksettings.LockSettingsStorage;
import com.android.server.locksettings.RebootEscrowManager;
import com.android.server.locksettings.SyntheticPasswordManager;
import com.android.server.locksettings.recoverablekeystore.RecoverableKeyStoreManager;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.utils.Slogf;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.HexEncoding;
/* loaded from: classes2.dex */
public class LockSettingsService extends ILockSettings.Stub {
    public static final int[] SYSTEM_CREDENTIAL_UIDS = {1016, 0, 1000};
    public final IActivityManager mActivityManager;
    @GuardedBy({"mHeadlessAuthSecretLock"})
    @VisibleForTesting
    protected byte[] mAuthSecret;
    public IAuthSecret mAuthSecretService;
    public final BiometricDeferredQueue mBiometricDeferredQueue;
    public final BroadcastReceiver mBroadcastReceiver;
    public final Context mContext;
    public final DeviceProvisionedObserver mDeviceProvisionedObserver;
    @GuardedBy({"mUserCreationAndRemovalLock"})
    public SparseIntArray mEarlyCreatedUsers;
    @GuardedBy({"mUserCreationAndRemovalLock"})
    public SparseIntArray mEarlyRemovedUsers;
    public IGateKeeperService mGateKeeperService;
    public final LongSparseArray<byte[]> mGatekeeperPasswords;
    @VisibleForTesting
    protected final Handler mHandler;
    @VisibleForTesting
    protected boolean mHasSecureLockScreen;
    @VisibleForTesting
    protected final Object mHeadlessAuthSecretLock;
    public final Injector mInjector;
    public final KeyStore mJavaKeyStore;
    public ManagedProfilePasswordCache mManagedProfilePasswordCache;
    public final NotificationManager mNotificationManager;
    public final RebootEscrowManager mRebootEscrowManager;
    public final RecoverableKeyStoreManager mRecoverableKeyStoreManager;
    public final Object mSeparateChallengeLock;
    public final SyntheticPasswordManager mSpManager;
    @VisibleForTesting
    protected final LockSettingsStorage mStorage;
    public final IStorageManager mStorageManager;
    public final LockSettingsStrongAuth mStrongAuth;
    public final SynchronizedStrongAuthTracker mStrongAuthTracker;
    @GuardedBy({"mUserCreationAndRemovalLock"})
    public boolean mThirdPartyAppsStarted;
    public final Object mUserCreationAndRemovalLock;
    public final UserManager mUserManager;
    public HashMap<UserHandle, UserManager> mUserManagerCache;
    @GuardedBy({"this"})
    public final SparseArray<PasswordMetrics> mUserPasswordMetrics;

    public final int redactActualQualityToMostLenientEquivalentQuality(int i) {
        int i2 = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
        if (i != 131072 && i != 196608) {
            i2 = 262144;
            if (i != 262144 && i != 327680 && i != 393216) {
                return i;
            }
        }
        return i2;
    }

    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public LockSettingsService mLockSettingsService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.locksettings.LockSettingsService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            AndroidKeyStoreProvider.install();
            ?? lockSettingsService = new LockSettingsService(getContext());
            this.mLockSettingsService = lockSettingsService;
            publishBinderService("lock_settings", lockSettingsService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            super.onBootPhase(i);
            if (i == 550) {
                this.mLockSettingsService.migrateOldDataAfterSystemReady();
                this.mLockSettingsService.loadEscrowData();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            this.mLockSettingsService.onStartUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mLockSettingsService.onUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            this.mLockSettingsService.onCleanupUser(targetUser.getUserIdentifier());
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class SynchronizedStrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        public SynchronizedStrongAuthTracker(Context context) {
            super(context);
        }

        public void handleStrongAuthRequiredChanged(int i, int i2) {
            synchronized (this) {
                super.handleStrongAuthRequiredChanged(i, i2);
            }
        }

        public int getStrongAuthForUser(int i) {
            int strongAuthForUser;
            synchronized (this) {
                strongAuthForUser = super.getStrongAuthForUser(i);
            }
            return strongAuthForUser;
        }

        public void register(LockSettingsStrongAuth lockSettingsStrongAuth) {
            lockSettingsStrongAuth.registerStrongAuthTracker(getStub());
        }
    }

    public final LockscreenCredential generateRandomProfilePassword() {
        byte[] randomBytes = SecureRandomUtils.randomBytes(40);
        char[] encode = HexEncoding.encode(randomBytes);
        byte[] bArr = new byte[encode.length];
        for (int i = 0; i < encode.length; i++) {
            bArr[i] = (byte) encode[i];
        }
        LockscreenCredential createManagedPassword = LockscreenCredential.createManagedPassword(bArr);
        Arrays.fill(encode, (char) 0);
        Arrays.fill(bArr, (byte) 0);
        Arrays.fill(randomBytes, (byte) 0);
        return createManagedPassword;
    }

    public final void tieProfileLockIfNecessary(int i, LockscreenCredential lockscreenCredential) {
        if (!isCredentialSharableWithParent(i) || getSeparateProfileChallengeEnabledInternal(i) || this.mStorage.hasChildProfileLock(i)) {
            return;
        }
        int i2 = this.mUserManager.getProfileParent(i).id;
        if (!isUserSecure(i2) && !lockscreenCredential.isNone()) {
            Slogf.m20i("LockSettingsService", "Clearing password for profile user %d to match parent", Integer.valueOf(i));
            setLockCredentialInternal(LockscreenCredential.createNone(), lockscreenCredential, i, true);
            return;
        }
        try {
            if (getGateKeeperService().getSecureUserId(i2) == 0) {
                return;
            }
            LockscreenCredential generateRandomProfilePassword = generateRandomProfilePassword();
            try {
                setLockCredentialInternal(generateRandomProfilePassword, lockscreenCredential, i, true);
                tieProfileLockToParent(i, i2, generateRandomProfilePassword);
                this.mManagedProfilePasswordCache.storePassword(i, generateRandomProfilePassword);
                if (generateRandomProfilePassword != null) {
                    generateRandomProfilePassword.close();
                }
            } catch (Throwable th) {
                if (generateRandomProfilePassword != null) {
                    try {
                        generateRandomProfilePassword.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e) {
            Slog.e("LockSettingsService", "Failed to talk to GateKeeper service", e);
        }
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public Context mContext;

        public Injector(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return this.mContext;
        }

        public ServiceThread getServiceThread() {
            ServiceThread serviceThread = new ServiceThread("LockSettingsService", 10, true);
            serviceThread.start();
            return serviceThread;
        }

        public Handler getHandler(ServiceThread serviceThread) {
            return new Handler(serviceThread.getLooper());
        }

        public LockSettingsStorage getStorage() {
            final LockSettingsStorage lockSettingsStorage = new LockSettingsStorage(this.mContext);
            lockSettingsStorage.setDatabaseOnCreateCallback(new LockSettingsStorage.Callback() { // from class: com.android.server.locksettings.LockSettingsService.Injector.1
                @Override // com.android.server.locksettings.LockSettingsStorage.Callback
                public void initialize(SQLiteDatabase sQLiteDatabase) {
                    if (SystemProperties.getBoolean("ro.lockscreen.disable.default", false)) {
                        lockSettingsStorage.writeKeyValue(sQLiteDatabase, "lockscreen.disabled", "1", 0);
                    }
                }
            });
            return lockSettingsStorage;
        }

        public LockSettingsStrongAuth getStrongAuth() {
            return new LockSettingsStrongAuth(this.mContext);
        }

        public SynchronizedStrongAuthTracker getStrongAuthTracker() {
            return new SynchronizedStrongAuthTracker(this.mContext);
        }

        public IActivityManager getActivityManager() {
            return ActivityManager.getService();
        }

        public NotificationManager getNotificationManager() {
            return (NotificationManager) this.mContext.getSystemService("notification");
        }

        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService("user");
        }

        public UserManagerInternal getUserManagerInternal() {
            return (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }

        public DevicePolicyManager getDevicePolicyManager() {
            return (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        }

        public DeviceStateCache getDeviceStateCache() {
            return DeviceStateCache.getInstance();
        }

        public RecoverableKeyStoreManager getRecoverableKeyStoreManager() {
            return RecoverableKeyStoreManager.getInstance(this.mContext);
        }

        public IStorageManager getStorageManager() {
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                return IStorageManager.Stub.asInterface(service);
            }
            return null;
        }

        public SyntheticPasswordManager getSyntheticPasswordManager(LockSettingsStorage lockSettingsStorage) {
            return new SyntheticPasswordManager(getContext(), lockSettingsStorage, getUserManager(), new PasswordSlotManager());
        }

        public RebootEscrowManager getRebootEscrowManager(RebootEscrowManager.Callbacks callbacks, LockSettingsStorage lockSettingsStorage) {
            return new RebootEscrowManager(this.mContext, callbacks, lockSettingsStorage);
        }

        public boolean isGsiRunning() {
            return SystemProperties.getInt("ro.gsid.image_running", 0) > 0;
        }

        public FingerprintManager getFingerprintManager() {
            if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
                return (FingerprintManager) this.mContext.getSystemService("fingerprint");
            }
            return null;
        }

        public FaceManager getFaceManager() {
            if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
                return (FaceManager) this.mContext.getSystemService("face");
            }
            return null;
        }

        public BiometricManager getBiometricManager() {
            return (BiometricManager) this.mContext.getSystemService("biometric");
        }

        public KeyStore getJavaKeyStore() {
            try {
                KeyStore keyStore = KeyStore.getInstance(SyntheticPasswordCrypto.androidKeystoreProviderName());
                keyStore.load(new AndroidKeyStoreLoadStoreParameter(SyntheticPasswordCrypto.keyNamespace()));
                return keyStore;
            } catch (Exception e) {
                throw new IllegalStateException("Cannot load keystore", e);
            }
        }

        public ManagedProfilePasswordCache getManagedProfilePasswordCache(KeyStore keyStore) {
            return new ManagedProfilePasswordCache(keyStore, getUserManager());
        }

        public boolean isHeadlessSystemUserMode() {
            return UserManager.isHeadlessSystemUserMode();
        }

        public boolean isMainUserPermanentAdmin() {
            return Resources.getSystem().getBoolean(17891710);
        }
    }

    public LockSettingsService(Context context) {
        this(new Injector(context));
    }

    @VisibleForTesting
    public LockSettingsService(Injector injector) {
        this.mSeparateChallengeLock = new Object();
        this.mDeviceProvisionedObserver = new DeviceProvisionedObserver();
        this.mUserCreationAndRemovalLock = new Object();
        this.mEarlyCreatedUsers = new SparseIntArray();
        this.mEarlyRemovedUsers = new SparseIntArray();
        this.mUserPasswordMetrics = new SparseArray<>();
        this.mHeadlessAuthSecretLock = new Object();
        this.mUserManagerCache = new HashMap<>();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.locksettings.LockSettingsService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_ADDED".equals(intent.getAction())) {
                    AndroidKeyStoreMaintenance.onUserAdded(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_STARTING".equals(intent.getAction())) {
                    LockSettingsService.this.mStorage.prefetchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction())) {
                    LockSettingsService.this.updateActivatedEncryptionNotifications("locale changed");
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mInjector = injector;
        this.mContext = injector.getContext();
        KeyStore javaKeyStore = injector.getJavaKeyStore();
        this.mJavaKeyStore = javaKeyStore;
        this.mRecoverableKeyStoreManager = injector.getRecoverableKeyStoreManager();
        Handler handler = injector.getHandler(injector.getServiceThread());
        this.mHandler = handler;
        LockSettingsStrongAuth strongAuth = injector.getStrongAuth();
        this.mStrongAuth = strongAuth;
        this.mActivityManager = injector.getActivityManager();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_STARTING");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        injector.getContext().registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, null);
        LockSettingsStorage storage = injector.getStorage();
        this.mStorage = storage;
        this.mNotificationManager = injector.getNotificationManager();
        this.mUserManager = injector.getUserManager();
        this.mStorageManager = injector.getStorageManager();
        SynchronizedStrongAuthTracker strongAuthTracker = injector.getStrongAuthTracker();
        this.mStrongAuthTracker = strongAuthTracker;
        strongAuthTracker.register(strongAuth);
        this.mGatekeeperPasswords = new LongSparseArray<>();
        SyntheticPasswordManager syntheticPasswordManager = injector.getSyntheticPasswordManager(storage);
        this.mSpManager = syntheticPasswordManager;
        this.mManagedProfilePasswordCache = injector.getManagedProfilePasswordCache(javaKeyStore);
        this.mBiometricDeferredQueue = new BiometricDeferredQueue(syntheticPasswordManager, handler);
        this.mRebootEscrowManager = injector.getRebootEscrowManager(new RebootEscrowCallbacks(), storage);
        LocalServices.addService(LockSettingsInternal.class, new LocalService());
    }

    public final void updateActivatedEncryptionNotifications(String str) {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            int i = 0;
            StatusBarNotification[] activeNotifications = ((NotificationManager) this.mContext.createContextAsUser(UserHandle.of(userInfo.id), 0).getSystemService("notification")).getActiveNotifications();
            int length = activeNotifications.length;
            while (true) {
                if (i >= length) {
                    break;
                } else if (activeNotifications[i].getId() == 9) {
                    maybeShowEncryptionNotificationForUser(userInfo.id, str);
                    break;
                } else {
                    i++;
                }
            }
        }
    }

    public final void maybeShowEncryptionNotificationForUser(int i, String str) {
        UserInfo profileParent;
        UserInfo userInfo = this.mUserManager.getUserInfo(i);
        if (userInfo.isManagedProfile() && !isUserKeyUnlocked(i)) {
            UserHandle userHandle = userInfo.getUserHandle();
            if (!isUserSecure(i) || this.mUserManager.isUserUnlockingOrUnlocked(userHandle) || (profileParent = this.mUserManager.getProfileParent(i)) == null || !this.mUserManager.isUserUnlockingOrUnlocked(profileParent.getUserHandle()) || this.mUserManager.isQuietModeEnabled(userHandle)) {
                return;
            }
            showEncryptionNotificationForProfile(userHandle, str);
        }
    }

    public final void showEncryptionNotificationForProfile(UserHandle userHandle, String str) {
        String encryptionNotificationTitle = getEncryptionNotificationTitle();
        String encryptionNotificationMessage = getEncryptionNotificationMessage();
        String encryptionNotificationDetail = getEncryptionNotificationDetail();
        Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, userHandle.getIdentifier());
        if (createConfirmDeviceCredentialIntent != null && StorageManager.isFileEncrypted()) {
            createConfirmDeviceCredentialIntent.setFlags(276824064);
            PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, createConfirmDeviceCredentialIntent, 167772160);
            Slogf.m28d("LockSettingsService", "Showing encryption notification for user %d; reason: %s", Integer.valueOf(userHandle.getIdentifier()), str);
            showEncryptionNotification(userHandle, encryptionNotificationTitle, encryptionNotificationMessage, encryptionNotificationDetail, activity);
        }
    }

    public final String getEncryptionNotificationTitle() {
        return this.mInjector.getDevicePolicyManager().getResources().getString("Core.PROFILE_ENCRYPTED_TITLE", new Supplier() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getEncryptionNotificationTitle$0;
                lambda$getEncryptionNotificationTitle$0 = LockSettingsService.this.lambda$getEncryptionNotificationTitle$0();
                return lambda$getEncryptionNotificationTitle$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEncryptionNotificationTitle$0() {
        return this.mContext.getString(17041373);
    }

    public final String getEncryptionNotificationDetail() {
        return this.mInjector.getDevicePolicyManager().getResources().getString("Core.PROFILE_ENCRYPTED_DETAIL", new Supplier() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getEncryptionNotificationDetail$1;
                lambda$getEncryptionNotificationDetail$1 = LockSettingsService.this.lambda$getEncryptionNotificationDetail$1();
                return lambda$getEncryptionNotificationDetail$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEncryptionNotificationDetail$1() {
        return this.mContext.getString(17041371);
    }

    public final String getEncryptionNotificationMessage() {
        return this.mInjector.getDevicePolicyManager().getResources().getString("Core.PROFILE_ENCRYPTED_MESSAGE", new Supplier() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getEncryptionNotificationMessage$2;
                lambda$getEncryptionNotificationMessage$2 = LockSettingsService.this.lambda$getEncryptionNotificationMessage$2();
                return lambda$getEncryptionNotificationMessage$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getEncryptionNotificationMessage$2() {
        return this.mContext.getString(17041372);
    }

    public final void showEncryptionNotification(UserHandle userHandle, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, PendingIntent pendingIntent) {
        this.mNotificationManager.notifyAsUser(null, 9, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302893).setWhen(0L).setOngoing(true).setTicker(charSequence).setColor(this.mContext.getColor(17170460)).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence3).setVisibility(1).setContentIntent(pendingIntent).build(), userHandle);
    }

    public final void hideEncryptionNotification(UserHandle userHandle) {
        Slogf.m28d("LockSettingsService", "Hiding encryption notification for user %d", Integer.valueOf(userHandle.getIdentifier()));
        this.mNotificationManager.cancelAsUser(null, 9, userHandle);
    }

    @VisibleForTesting
    public void onCleanupUser(int i) {
        hideEncryptionNotification(new UserHandle(i));
        requireStrongAuth(LockPatternUtils.StrongAuthTracker.getDefaultFlags(this.mContext), i);
        synchronized (this) {
            this.mUserPasswordMetrics.remove(i);
        }
    }

    public final void onStartUser(int i) {
        maybeShowEncryptionNotificationForUser(i, "user started");
    }

    public final void removeStateForReusedUserIdIfNecessary(int i, int i2) {
        int i3;
        if (i == 0 || (i3 = this.mStorage.getInt("serial-number", -1, i)) == i2) {
            return;
        }
        if (i3 != -1) {
            Slogf.m20i("LockSettingsService", "Removing stale state for reused userId %d (serial %d => %d)", Integer.valueOf(i), Integer.valueOf(i3), Integer.valueOf(i2));
            removeUserState(i);
        }
        this.mStorage.setInt("serial-number", i2, i);
    }

    public final void onUnlockUser(final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService.1
            @Override // java.lang.Runnable
            public void run() {
                LockSettingsService.this.hideEncryptionNotification(new UserHandle(i));
                if (LockSettingsService.this.isCredentialSharableWithParent(i)) {
                    LockSettingsService.this.tieProfileLockIfNecessary(i, LockscreenCredential.createNone());
                }
            }
        });
    }

    public void systemReady() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") != 0) {
            EventLog.writeEvent(1397638484, "28251513", Integer.valueOf(ILockSettings.Stub.getCallingUid()), "");
        }
        checkWritePermission();
        this.mHasSecureLockScreen = this.mContext.getPackageManager().hasSystemFeature("android.software.secure_lock_screen");
        migrateOldData();
        getGateKeeperService();
        getAuthSecretHal();
        this.mDeviceProvisionedObserver.onSystemReady();
        this.mStorage.prefetchUser(0);
        this.mBiometricDeferredQueue.systemReady(this.mInjector.getFingerprintManager(), this.mInjector.getFaceManager(), this.mInjector.getBiometricManager());
    }

    public final void loadEscrowData() {
        this.mRebootEscrowManager.loadRebootEscrowDataIfAvailable(this.mHandler);
    }

    public final void getAuthSecretHal() {
        IAuthSecret asInterface = IAuthSecret.Stub.asInterface(ServiceManager.waitForDeclaredService(IAuthSecret.DESCRIPTOR + "/default"));
        this.mAuthSecretService = asInterface;
        if (asInterface != null) {
            Slog.i("LockSettingsService", "Device implements AIDL AuthSecret HAL");
            return;
        }
        try {
            this.mAuthSecretService = new AuthSecretHidlAdapter(android.hardware.authsecret.V1_0.IAuthSecret.getService(true));
            Slog.i("LockSettingsService", "Device implements HIDL AuthSecret HAL");
        } catch (RemoteException e) {
            Slog.w("LockSettingsService", "Failed to get AuthSecret HAL(hidl)", e);
        } catch (NoSuchElementException unused) {
            Slog.i("LockSettingsService", "Device doesn't implement AuthSecret HAL");
        }
    }

    public final void migrateOldData() {
        boolean migrateKeyNamespace;
        if (getString("migrated_keystore_namespace", null, 0) == null) {
            synchronized (this.mSpManager) {
                migrateKeyNamespace = this.mSpManager.migrateKeyNamespace() & true;
            }
            if (migrateProfileLockKeys() & migrateKeyNamespace) {
                setString("migrated_keystore_namespace", "true", 0);
                Slog.i("LockSettingsService", "Migrated keys to LSS namespace");
                return;
            }
            Slog.w("LockSettingsService", "Failed to migrate keys to LSS namespace");
        }
    }

    public final void migrateOldDataAfterSystemReady() {
        if (!LockPatternUtils.frpCredentialEnabled(this.mContext) || getBoolean("migrated_frp", false, 0)) {
            return;
        }
        migrateFrpCredential();
        setBoolean("migrated_frp", true, 0);
    }

    public final void migrateFrpCredential() {
        if (this.mStorage.readPersistentDataBlock() != LockSettingsStorage.PersistentData.NONE) {
            return;
        }
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (LockPatternUtils.userOwnsFrpCredential(this.mContext, userInfo) && isUserSecure(userInfo.id)) {
                synchronized (this.mSpManager) {
                    this.mSpManager.migrateFrpPasswordLocked(getCurrentLskfBasedProtectorId(userInfo.id), userInfo, redactActualQualityToMostLenientEquivalentQuality((int) getLong("lockscreen.password_type", 0L, userInfo.id)));
                }
                return;
            }
        }
    }

    public final boolean migrateProfileLockKeys() {
        List users = this.mUserManager.getUsers();
        int size = users.size();
        boolean z = true;
        for (int i = 0; i < size; i++) {
            UserInfo userInfo = (UserInfo) users.get(i);
            if (isCredentialSharableWithParent(userInfo.id) && !getSeparateProfileChallengeEnabledInternal(userInfo.id)) {
                boolean migrateLockSettingsKey = z & SyntheticPasswordCrypto.migrateLockSettingsKey("profile_key_name_encrypt_" + userInfo.id);
                z = migrateLockSettingsKey & SyntheticPasswordCrypto.migrateLockSettingsKey("profile_key_name_decrypt_" + userInfo.id);
            }
        }
        return z;
    }

    public final void onThirdPartyAppsStarted() {
        synchronized (this.mUserCreationAndRemovalLock) {
            for (int i = 0; i < this.mEarlyRemovedUsers.size(); i++) {
                int keyAt = this.mEarlyRemovedUsers.keyAt(i);
                Slogf.m20i("LockSettingsService", "Removing locksettings state for removed user %d now that boot is complete", Integer.valueOf(keyAt));
                removeUserState(keyAt);
            }
            this.mEarlyRemovedUsers = null;
            for (int i2 = 0; i2 < this.mEarlyCreatedUsers.size(); i2++) {
                int keyAt2 = this.mEarlyCreatedUsers.keyAt(i2);
                removeStateForReusedUserIdIfNecessary(keyAt2, this.mEarlyCreatedUsers.valueAt(i2));
                Slogf.m20i("LockSettingsService", "Creating locksettings state for user %d now that boot is complete", Integer.valueOf(keyAt2));
                initializeSyntheticPassword(keyAt2);
            }
            this.mEarlyCreatedUsers = null;
            if (getString("migrated_all_users_to_sp_and_bound_ce", null, 0) == null) {
                for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
                    removeStateForReusedUserIdIfNecessary(userInfo.id, userInfo.serialNumber);
                    synchronized (this.mSpManager) {
                        migrateUserToSpWithBoundCeKeyLocked(userInfo.id);
                    }
                }
                setString("migrated_all_users_to_sp_and_bound_ce", "true", 0);
            }
            this.mThirdPartyAppsStarted = true;
        }
    }

    @GuardedBy({"mSpManager"})
    public final void migrateUserToSpWithBoundCeKeyLocked(int i) {
        if (isUserSecure(i)) {
            Slogf.m28d("LockSettingsService", "User %d is secured; no migration needed", Integer.valueOf(i));
            return;
        }
        long currentLskfBasedProtectorId = getCurrentLskfBasedProtectorId(i);
        if (currentLskfBasedProtectorId == 0) {
            Slogf.m20i("LockSettingsService", "Migrating unsecured user %d to SP-based credential", Integer.valueOf(i));
            initializeSyntheticPassword(i);
            return;
        }
        Slogf.m20i("LockSettingsService", "Existing unsecured user %d has a synthetic password; re-encrypting CE key with it", Integer.valueOf(i));
        SyntheticPasswordManager.SyntheticPassword syntheticPassword = this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), currentLskfBasedProtectorId, LockscreenCredential.createNone(), i, null).syntheticPassword;
        if (syntheticPassword == null) {
            Slogf.wtf("LockSettingsService", "Failed to unwrap synthetic password for unsecured user %d", Integer.valueOf(i));
        } else {
            setUserKeyProtection(i, syntheticPassword.deriveFileBasedEncryptionKey());
        }
    }

    public final void enforceFrpResolved() {
        int mainUserId = this.mInjector.getUserManagerInternal().getMainUserId();
        if (mainUserId < 0) {
            Slog.d("LockSettingsService", "No Main user on device; skipping enforceFrpResolved");
            return;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = Settings.Secure.getIntForUser(contentResolver, "user_setup_complete", 0, mainUserId) == 0;
        boolean z2 = Settings.Global.getInt(contentResolver, "secure_frp_mode", 0) == 1;
        if (z && z2) {
            throw new SecurityException("Cannot change credential in SUW while factory reset protection is not resolved yet");
        }
    }

    public final void checkWritePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "LockSettingsWrite");
    }

    public final void checkPasswordReadPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "LockSettingsRead");
    }

    public final void checkPasswordHavePermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") != 0) {
            EventLog.writeEvent(1397638484, "28251513", Integer.valueOf(ILockSettings.Stub.getCallingUid()), "");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "LockSettingsHave");
    }

    public final void checkDatabaseReadPermission(String str, int i) {
        if (hasPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE")) {
            return;
        }
        throw new SecurityException("uid=" + ILockSettings.Stub.getCallingUid() + " needs permission android.permission.ACCESS_KEYGUARD_SECURE_STORAGE to read " + str + " for user " + i);
    }

    public final void checkBiometricPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_BIOMETRIC", "LockSettingsBiometric");
    }

    public final boolean hasPermission(String str) {
        return this.mContext.checkCallingOrSelfPermission(str) == 0;
    }

    public final void checkManageWeakEscrowTokenMethodUsage() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_WEAK_ESCROW_TOKEN", "Requires MANAGE_WEAK_ESCROW_TOKEN permission.");
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalArgumentException("Weak escrow token are only for automotive devices.");
        }
    }

    public boolean hasSecureLockScreen() {
        return this.mHasSecureLockScreen;
    }

    public boolean getSeparateProfileChallengeEnabled(int i) {
        checkDatabaseReadPermission("lockscreen.profilechallenge", i);
        return getSeparateProfileChallengeEnabledInternal(i);
    }

    public final boolean getSeparateProfileChallengeEnabledInternal(int i) {
        boolean z;
        synchronized (this.mSeparateChallengeLock) {
            z = this.mStorage.getBoolean("lockscreen.profilechallenge", false, i);
        }
        return z;
    }

    public void setSeparateProfileChallengeEnabled(int i, boolean z, LockscreenCredential lockscreenCredential) {
        checkWritePermission();
        if (!this.mHasSecureLockScreen && lockscreenCredential != null && lockscreenCredential.getType() != -1) {
            throw new UnsupportedOperationException("This operation requires secure lock screen feature.");
        }
        synchronized (this.mSeparateChallengeLock) {
            if (lockscreenCredential == null) {
                lockscreenCredential = LockscreenCredential.createNone();
            }
            setSeparateProfileChallengeEnabledLocked(i, z, lockscreenCredential);
        }
        notifySeparateProfileChallengeChanged(i);
    }

    @GuardedBy({"mSeparateChallengeLock"})
    public final void setSeparateProfileChallengeEnabledLocked(int i, boolean z, LockscreenCredential lockscreenCredential) {
        boolean z2 = getBoolean("lockscreen.profilechallenge", false, i);
        setBoolean("lockscreen.profilechallenge", z, i);
        try {
            if (z) {
                this.mStorage.removeChildProfileLock(i);
                removeKeystoreProfileKey(i);
                return;
            }
            tieProfileLockIfNecessary(i, lockscreenCredential);
        } catch (IllegalStateException e) {
            setBoolean("lockscreen.profilechallenge", z2, i);
            throw e;
        }
    }

    public final void notifySeparateProfileChallengeChanged(final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LockSettingsService.lambda$notifySeparateProfileChallengeChanged$3(i);
            }
        });
    }

    public static /* synthetic */ void lambda$notifySeparateProfileChallengeChanged$3(int i) {
        DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        if (devicePolicyManagerInternal != null) {
            devicePolicyManagerInternal.reportSeparateProfileChallengeChanged(i);
        }
    }

    public void setBoolean(String str, boolean z, int i) {
        checkWritePermission();
        Objects.requireNonNull(str);
        this.mStorage.setBoolean(str, z, i);
    }

    public void setLong(String str, long j, int i) {
        checkWritePermission();
        Objects.requireNonNull(str);
        this.mStorage.setLong(str, j, i);
    }

    public void setString(String str, String str2, int i) {
        checkWritePermission();
        Objects.requireNonNull(str);
        this.mStorage.setString(str, str2, i);
    }

    public boolean getBoolean(String str, boolean z, int i) {
        checkDatabaseReadPermission(str, i);
        return this.mStorage.getBoolean(str, z, i);
    }

    public long getLong(String str, long j, int i) {
        checkDatabaseReadPermission(str, i);
        return this.mStorage.getLong(str, j, i);
    }

    public String getString(String str, String str2, int i) {
        checkDatabaseReadPermission(str, i);
        return this.mStorage.getString(str, str2, i);
    }

    public final int getKeyguardStoredQuality(int i) {
        return (int) this.mStorage.getLong("lockscreen.password_type", 0L, i);
    }

    public int getCredentialType(int i) {
        checkPasswordHavePermission();
        return getCredentialTypeInternal(i);
    }

    public final int getCredentialTypeInternal(int i) {
        if (i == -9999) {
            return getFrpCredentialType();
        }
        synchronized (this.mSpManager) {
            long currentLskfBasedProtectorId = getCurrentLskfBasedProtectorId(i);
            if (currentLskfBasedProtectorId == 0) {
                return -1;
            }
            int credentialType = this.mSpManager.getCredentialType(currentLskfBasedProtectorId, i);
            if (credentialType != 2) {
                return credentialType;
            }
            return pinOrPasswordQualityToCredentialType(getKeyguardStoredQuality(i));
        }
    }

    public final int getFrpCredentialType() {
        LockSettingsStorage.PersistentData readPersistentDataBlock = this.mStorage.readPersistentDataBlock();
        int i = readPersistentDataBlock.type;
        if (i == 1 || i == 2) {
            int frpCredentialType = SyntheticPasswordManager.getFrpCredentialType(readPersistentDataBlock.payload);
            return frpCredentialType != 2 ? frpCredentialType : pinOrPasswordQualityToCredentialType(readPersistentDataBlock.qualityForUi);
        }
        return -1;
    }

    public static int pinOrPasswordQualityToCredentialType(int i) {
        if (LockPatternUtils.isQualityAlphabeticPassword(i)) {
            return 4;
        }
        if (LockPatternUtils.isQualityNumericPin(i)) {
            return 3;
        }
        throw new IllegalArgumentException("Quality is neither Pin nor password: " + i);
    }

    public final boolean isUserSecure(int i) {
        return getCredentialTypeInternal(i) != -1;
    }

    @VisibleForTesting
    public void setKeystorePassword(byte[] bArr, int i) {
        AndroidKeyStoreMaintenance.onUserPasswordChanged(i, bArr);
    }

    public final void unlockKeystore(byte[] bArr, int i) {
        Authorization.onLockScreenEvent(false, i, bArr, (long[]) null);
    }

    @VisibleForTesting
    public LockscreenCredential getDecryptedPasswordForTiedProfile(int i) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, IOException {
        Slogf.m28d("LockSettingsService", "Decrypting password for tied profile %d", Integer.valueOf(i));
        byte[] readChildProfileLock = this.mStorage.readChildProfileLock(i);
        if (readChildProfileLock == null) {
            throw new FileNotFoundException("Child profile lock file not found");
        }
        byte[] copyOfRange = Arrays.copyOfRange(readChildProfileLock, 0, 12);
        byte[] copyOfRange2 = Arrays.copyOfRange(readChildProfileLock, 12, readChildProfileLock.length);
        KeyStore keyStore = this.mJavaKeyStore;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, (SecretKey) keyStore.getKey("profile_key_name_decrypt_" + i, null), new GCMParameterSpec(128, copyOfRange));
        byte[] doFinal = cipher.doFinal(copyOfRange2);
        LockscreenCredential createManagedPassword = LockscreenCredential.createManagedPassword(doFinal);
        Arrays.fill(doFinal, (byte) 0);
        this.mManagedProfilePasswordCache.storePassword(i, createManagedPassword);
        return createManagedPassword;
    }

    public final void unlockChildProfile(int i) {
        try {
            doVerifyCredential(getDecryptedPasswordForTiedProfile(i), i, null, 0);
        } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            if (e instanceof FileNotFoundException) {
                Slog.i("LockSettingsService", "Child profile key not found");
            } else {
                Slog.e("LockSettingsService", "Failed to decrypt child profile key", e);
            }
        }
    }

    /* renamed from: unlockUser */
    public final void lambda$setLockCredentialWithToken$7(int i) {
        boolean isUserUnlockingOrUnlocked = this.mUserManager.isUserUnlockingOrUnlocked(i);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            this.mActivityManager.unlockUser2(i, new IProgressListener.Stub() { // from class: com.android.server.locksettings.LockSettingsService.3
                public void onStarted(int i2, Bundle bundle) throws RemoteException {
                    Slog.d("LockSettingsService", "unlockUser started");
                }

                public void onProgress(int i2, int i3, Bundle bundle) throws RemoteException {
                    Slog.d("LockSettingsService", "unlockUser progress " + i3);
                }

                public void onFinished(int i2, Bundle bundle) throws RemoteException {
                    Slog.d("LockSettingsService", "unlockUser finished");
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(15L, TimeUnit.SECONDS);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
            if (isCredentialSharableWithParent(i)) {
                if (hasUnifiedChallenge(i)) {
                    return;
                }
                this.mBiometricDeferredQueue.processPendingLockoutResets();
                return;
            }
            for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
                int i2 = userInfo.id;
                if (i2 != i && isCredentialSharableWithParent(i2)) {
                    if (hasUnifiedChallenge(userInfo.id)) {
                        if (this.mUserManager.isUserRunning(userInfo.id)) {
                            unlockChildProfile(userInfo.id);
                        } else {
                            try {
                                getDecryptedPasswordForTiedProfile(userInfo.id);
                            } catch (IOException | GeneralSecurityException e) {
                                Slog.d("LockSettingsService", "Cache work profile password failed", e);
                            }
                        }
                    }
                    if (isUserUnlockingOrUnlocked) {
                        continue;
                    } else {
                        long clearCallingIdentity = ILockSettings.Stub.clearCallingIdentity();
                        try {
                            maybeShowEncryptionNotificationForUser(userInfo.id, "parent unlocked");
                        } finally {
                            ILockSettings.Stub.restoreCallingIdentity(clearCallingIdentity);
                        }
                    }
                }
            }
            this.mBiometricDeferredQueue.processPendingLockoutResets();
        } catch (RemoteException e2) {
            throw e2.rethrowAsRuntimeException();
        }
    }

    public final boolean hasUnifiedChallenge(int i) {
        return !getSeparateProfileChallengeEnabledInternal(i) && this.mStorage.hasChildProfileLock(i);
    }

    public final Map<Integer, LockscreenCredential> getDecryptedPasswordsForAllTiedProfiles(int i) {
        if (isCredentialSharableWithParent(i)) {
            return null;
        }
        ArrayMap arrayMap = new ArrayMap();
        List profiles = this.mUserManager.getProfiles(i);
        int size = profiles.size();
        for (int i2 = 0; i2 < size; i2++) {
            UserInfo userInfo = (UserInfo) profiles.get(i2);
            if (isCredentialSharableWithParent(userInfo.id)) {
                int i3 = userInfo.id;
                if (!getSeparateProfileChallengeEnabledInternal(i3)) {
                    try {
                        arrayMap.put(Integer.valueOf(i3), getDecryptedPasswordForTiedProfile(i3));
                    } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                        Slog.e("LockSettingsService", "getDecryptedPasswordsForAllTiedProfiles failed for user " + i3, e);
                    }
                }
            }
        }
        return arrayMap;
    }

    public final void synchronizeUnifiedWorkChallengeForProfiles(int i, Map<Integer, LockscreenCredential> map) {
        if (isCredentialSharableWithParent(i)) {
            return;
        }
        boolean isUserSecure = isUserSecure(i);
        List profiles = this.mUserManager.getProfiles(i);
        int size = profiles.size();
        for (int i2 = 0; i2 < size; i2++) {
            int i3 = ((UserInfo) profiles.get(i2)).id;
            if (isCredentialSharableWithParent(i3) && !getSeparateProfileChallengeEnabledInternal(i3)) {
                if (isUserSecure) {
                    tieProfileLockIfNecessary(i3, LockscreenCredential.createNone());
                } else if (map != null && map.containsKey(Integer.valueOf(i3))) {
                    setLockCredentialInternal(LockscreenCredential.createNone(), map.get(Integer.valueOf(i3)), i3, true);
                    this.mStorage.removeChildProfileLock(i3);
                    removeKeystoreProfileKey(i3);
                } else {
                    Slog.wtf("LockSettingsService", "Attempt to clear tied challenge, but no password supplied.");
                }
            }
        }
    }

    public final boolean isProfileWithUnifiedLock(int i) {
        return isCredentialSharableWithParent(i) && !getSeparateProfileChallengeEnabledInternal(i);
    }

    public final boolean isProfileWithSeparatedLock(int i) {
        return isCredentialSharableWithParent(i) && getSeparateProfileChallengeEnabledInternal(i);
    }

    public final void sendCredentialsOnUnlockIfRequired(LockscreenCredential lockscreenCredential, int i) {
        if (i == -9999 || lockscreenCredential.isNone() || isProfileWithUnifiedLock(i)) {
            return;
        }
        for (Integer num : getProfilesWithSameLockScreen(i)) {
            this.mRecoverableKeyStoreManager.lockScreenSecretAvailable(lockscreenCredential.getType(), lockscreenCredential.getCredential(), num.intValue());
        }
    }

    public final void sendCredentialsOnChangeIfRequired(LockscreenCredential lockscreenCredential, int i, boolean z) {
        if (z) {
            return;
        }
        byte[] credential = lockscreenCredential.isNone() ? null : lockscreenCredential.getCredential();
        for (Integer num : getProfilesWithSameLockScreen(i)) {
            this.mRecoverableKeyStoreManager.lockScreenSecretChanged(lockscreenCredential.getType(), credential, num.intValue());
        }
    }

    public final Set<Integer> getProfilesWithSameLockScreen(int i) {
        ArraySet arraySet = new ArraySet();
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            int i2 = userInfo.id;
            if (i2 == i || (userInfo.profileGroupId == i && isProfileWithUnifiedLock(i2))) {
                arraySet.add(Integer.valueOf(userInfo.id));
            }
        }
        return arraySet;
    }

    public boolean setLockCredential(LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i) {
        if (!this.mHasSecureLockScreen && lockscreenCredential != null && lockscreenCredential.getType() != -1) {
            throw new UnsupportedOperationException("This operation requires secure lock screen feature");
        }
        if (!hasPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") && !hasPermission("android.permission.SET_AND_VERIFY_LOCKSCREEN_CREDENTIALS") && (!hasPermission("android.permission.SET_INITIAL_LOCK") || !lockscreenCredential2.isNone())) {
            throw new SecurityException("setLockCredential requires SET_AND_VERIFY_LOCKSCREEN_CREDENTIALS or android.permission.ACCESS_KEYGUARD_SECURE_STORAGE");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            enforceFrpResolved();
            if (!lockscreenCredential2.isNone() && isProfileWithUnifiedLock(i)) {
                verifyCredential(lockscreenCredential2, this.mUserManager.getProfileParent(i).id, 0);
                lockscreenCredential2.zeroize();
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            synchronized (this.mSeparateChallengeLock) {
                if (!setLockCredentialInternal(lockscreenCredential, lockscreenCredential2, i, false)) {
                    scheduleGc();
                    return false;
                }
                setSeparateProfileChallengeEnabledLocked(i, true, null);
                notifyPasswordChanged(lockscreenCredential, i);
                if (isCredentialSharableWithParent(i)) {
                    setDeviceUnlockedForUser(i);
                }
                notifySeparateProfileChallengeChanged(i);
                onPostPasswordChanged(lockscreenCredential, i);
                scheduleGc();
                return true;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean setLockCredentialInternal(LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i, boolean z) {
        Objects.requireNonNull(lockscreenCredential);
        Objects.requireNonNull(lockscreenCredential2);
        synchronized (this.mSpManager) {
            if (lockscreenCredential2.isNone() && isProfileWithUnifiedLock(i)) {
                try {
                    lockscreenCredential2 = getDecryptedPasswordForTiedProfile(i);
                } catch (FileNotFoundException unused) {
                    Slog.i("LockSettingsService", "Child profile key not found");
                } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                    Slog.e("LockSettingsService", "Failed to decrypt child profile key", e);
                }
            }
            long currentLskfBasedProtectorId = getCurrentLskfBasedProtectorId(i);
            SyntheticPasswordManager.AuthenticationResult unlockLskfBasedProtector = this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), currentLskfBasedProtectorId, lockscreenCredential2, i, null);
            VerifyCredentialResponse verifyCredentialResponse = unlockLskfBasedProtector.gkResponse;
            SyntheticPasswordManager.SyntheticPassword syntheticPassword = unlockLskfBasedProtector.syntheticPassword;
            if (syntheticPassword == null) {
                if (verifyCredentialResponse != null && verifyCredentialResponse.getResponseCode() != -1) {
                    if (verifyCredentialResponse.getResponseCode() == 1) {
                        Slog.w("LockSettingsService", "Failed to enroll: rate limit exceeded.");
                        return false;
                    }
                    throw new IllegalStateException("password change failed");
                }
                Slog.w("LockSettingsService", "Failed to enroll: incorrect credential.");
                return false;
            }
            onSyntheticPasswordUnlocked(i, syntheticPassword);
            setLockCredentialWithSpLocked(lockscreenCredential, syntheticPassword, i);
            sendCredentialsOnChangeIfRequired(lockscreenCredential, i, z);
            return true;
        }
    }

    public final void onPostPasswordChanged(LockscreenCredential lockscreenCredential, int i) {
        if (lockscreenCredential.isPattern()) {
            setBoolean("lockscreen.patterneverchosen", true, i);
        }
        if (LockPatternUtils.isAutoPinConfirmFeatureAvailable() && lockscreenCredential.isPin()) {
            setLong("lockscreen.pin_length", lockscreenCredential.size(), i);
        }
        updatePasswordHistory(lockscreenCredential, i);
        ((TrustManager) this.mContext.getSystemService(TrustManager.class)).reportEnabledTrustAgentsChanged(i);
    }

    public final void updatePasswordHistory(LockscreenCredential lockscreenCredential, int i) {
        if (lockscreenCredential.isNone() || lockscreenCredential.isPattern()) {
            return;
        }
        String string = getString("lockscreen.passwordhistory", null, i);
        String str = "";
        if (string == null) {
            string = "";
        }
        int requestedPasswordHistoryLength = getRequestedPasswordHistoryLength(i);
        if (requestedPasswordHistoryLength != 0) {
            Slogf.m28d("LockSettingsService", "Adding new password to password history for user %d", Integer.valueOf(i));
            str = lockscreenCredential.passwordToHistoryHash(getSalt(i).getBytes(), getHashFactor(lockscreenCredential, i));
            if (str == null) {
                Slog.e("LockSettingsService", "Failed to compute password hash; password history won't be updated");
                return;
            } else if (!TextUtils.isEmpty(string)) {
                String[] split = string.split(",");
                StringJoiner stringJoiner = new StringJoiner(",");
                stringJoiner.add(str);
                for (int i2 = 0; i2 < requestedPasswordHistoryLength - 1 && i2 < split.length; i2++) {
                    stringJoiner.add(split[i2]);
                }
                str = stringJoiner.toString();
            }
        }
        setString("lockscreen.passwordhistory", str, i);
    }

    public final String getSalt(int i) {
        long j = getLong("lockscreen.password_salt", 0L, i);
        if (j == 0) {
            j = SecureRandomUtils.randomLong();
            setLong("lockscreen.password_salt", j, i);
        }
        return Long.toHexString(j);
    }

    public final int getRequestedPasswordHistoryLength(int i) {
        return this.mInjector.getDevicePolicyManager().getPasswordHistoryLength(null, i);
    }

    public final UserManager getUserManagerFromCache(int i) {
        UserHandle of = UserHandle.of(i);
        if (this.mUserManagerCache.containsKey(of)) {
            return this.mUserManagerCache.get(of);
        }
        try {
            UserManager userManager = (UserManager) this.mContext.createPackageContextAsUser("system", 0, of).getSystemService(UserManager.class);
            this.mUserManagerCache.put(of, userManager);
            return userManager;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to create context for user " + of, e);
        }
    }

    @VisibleForTesting
    public boolean isCredentialSharableWithParent(int i) {
        return getUserManagerFromCache(i).isCredentialSharableWithParent();
    }

    public boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) {
        checkManageWeakEscrowTokenMethodUsage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSpManager.registerWeakEscrowTokenRemovedListener(iWeakEscrowTokenRemovedListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) {
        checkManageWeakEscrowTokenMethodUsage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSpManager.unregisterWeakEscrowTokenRemovedListener(iWeakEscrowTokenRemovedListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public long addWeakEscrowToken(byte[] bArr, int i, final IWeakEscrowTokenActivatedListener iWeakEscrowTokenActivatedListener) {
        checkManageWeakEscrowTokenMethodUsage();
        Objects.requireNonNull(iWeakEscrowTokenActivatedListener, "Listener can not be null.");
        LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback = new LockPatternUtils.EscrowTokenStateChangeCallback() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda2
            public final void onEscrowTokenActivated(long j, int i2) {
                LockSettingsService.lambda$addWeakEscrowToken$4(iWeakEscrowTokenActivatedListener, j, i2);
            }
        };
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return addEscrowToken(bArr, 1, i, escrowTokenStateChangeCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$addWeakEscrowToken$4(IWeakEscrowTokenActivatedListener iWeakEscrowTokenActivatedListener, long j, int i) {
        try {
            iWeakEscrowTokenActivatedListener.onWeakEscrowTokenActivated(j, i);
        } catch (RemoteException e) {
            Slog.e("LockSettingsService", "Exception while notifying weak escrow token has been activated", e);
        }
    }

    public boolean removeWeakEscrowToken(long j, int i) {
        checkManageWeakEscrowTokenMethodUsage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return removeEscrowToken(j, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isWeakEscrowTokenActive(long j, int i) {
        checkManageWeakEscrowTokenMethodUsage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return isEscrowTokenActive(j, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isWeakEscrowTokenValid(long j, byte[] bArr, int i) {
        checkManageWeakEscrowTokenMethodUsage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mSpManager) {
                if (!this.mSpManager.hasEscrowData(i)) {
                    Slog.w("LockSettingsService", "Escrow token is disabled on the current user");
                    return false;
                } else if (this.mSpManager.unlockWeakTokenBasedProtector(getGateKeeperService(), j, bArr, i).syntheticPassword == null) {
                    Slog.w("LockSettingsService", "Invalid escrow token supplied");
                    return false;
                } else {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public void tieProfileLockToParent(int i, int i2, LockscreenCredential lockscreenCredential) {
        Slogf.m20i("LockSettingsService", "Tying lock for profile user %d to parent user %d", Integer.valueOf(i), Integer.valueOf(i2));
        try {
            long secureUserId = getGateKeeperService().getSecureUserId(i2);
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(new SecureRandom());
                SecretKey generateKey = keyGenerator.generateKey();
                KeyStore keyStore = this.mJavaKeyStore;
                keyStore.setEntry("profile_key_name_encrypt_" + i, new KeyStore.SecretKeyEntry(generateKey), new KeyProtection.Builder(1).setBlockModes("GCM").setEncryptionPaddings("NoPadding").build());
                KeyStore keyStore2 = this.mJavaKeyStore;
                keyStore2.setEntry("profile_key_name_decrypt_" + i, new KeyStore.SecretKeyEntry(generateKey), new KeyProtection.Builder(2).setBlockModes("GCM").setEncryptionPaddings("NoPadding").setUserAuthenticationRequired(true).setBoundToSpecificSecureUserId(secureUserId).setUserAuthenticationValidityDurationSeconds(30).build());
                KeyStore keyStore3 = this.mJavaKeyStore;
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(1, (SecretKey) keyStore3.getKey("profile_key_name_encrypt_" + i, null));
                byte[] doFinal = cipher.doFinal(lockscreenCredential.getCredential());
                byte[] iv = cipher.getIV();
                KeyStore keyStore4 = this.mJavaKeyStore;
                keyStore4.deleteEntry("profile_key_name_encrypt_" + i);
                if (iv.length != 12) {
                    throw new IllegalArgumentException("Invalid iv length: " + iv.length);
                }
                this.mStorage.writeChildProfileLock(i, ArrayUtils.concat(new byte[][]{iv, doFinal}));
            } catch (InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                throw new IllegalStateException("Failed to encrypt key", e);
            }
        } catch (RemoteException e2) {
            throw new IllegalStateException("Failed to talk to GateKeeper service", e2);
        }
    }

    public final void setUserKeyProtection(int i, byte[] bArr) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mStorageManager.setUserKeyProtection(i, bArr);
            } catch (RemoteException e) {
                throw new IllegalStateException("Failed to protect CE key for user " + i, e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean isUserKeyUnlocked(int i) {
        try {
            return this.mStorageManager.isUserKeyUnlocked(i);
        } catch (RemoteException e) {
            Slog.e("LockSettingsService", "failed to check user key locked state", e);
            return false;
        }
    }

    public final void unlockUserKey(int i, SyntheticPasswordManager.SyntheticPassword syntheticPassword) {
        if (isUserKeyUnlocked(i)) {
            Slogf.m28d("LockSettingsService", "CE storage for user %d is already unlocked", Integer.valueOf(i));
            return;
        }
        UserInfo userInfo = this.mUserManager.getUserInfo(i);
        String str = isUserSecure(i) ? "secured" : "unsecured";
        byte[] deriveFileBasedEncryptionKey = syntheticPassword.deriveFileBasedEncryptionKey();
        try {
            try {
                this.mStorageManager.unlockUserKey(i, userInfo.serialNumber, deriveFileBasedEncryptionKey);
                Slogf.m20i("LockSettingsService", "Unlocked CE storage for %s user %d", str, Integer.valueOf(i));
            } catch (RemoteException e) {
                Slogf.wtf("LockSettingsService", e, "Failed to unlock CE storage for %s user %d", str, Integer.valueOf(i));
            }
        } finally {
            Arrays.fill(deriveFileBasedEncryptionKey, (byte) 0);
        }
    }

    public final void unlockUserKeyIfUnsecured(int i) {
        synchronized (this.mSpManager) {
            if (isUserKeyUnlocked(i)) {
                Slogf.m28d("LockSettingsService", "CE storage for user %d is already unlocked", Integer.valueOf(i));
            } else if (isUserSecure(i)) {
                Slogf.m28d("LockSettingsService", "Not unlocking CE storage for user %d yet because user is secured", Integer.valueOf(i));
            } else {
                Slogf.m20i("LockSettingsService", "Unwrapping synthetic password for unsecured user %d", Integer.valueOf(i));
                SyntheticPasswordManager.AuthenticationResult unlockLskfBasedProtector = this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), getCurrentLskfBasedProtectorId(i), LockscreenCredential.createNone(), i, null);
                SyntheticPasswordManager.SyntheticPassword syntheticPassword = unlockLskfBasedProtector.syntheticPassword;
                if (syntheticPassword == null) {
                    Slogf.wtf("LockSettingsService", "Failed to unwrap synthetic password for unsecured user %d", Integer.valueOf(i));
                    return;
                }
                onSyntheticPasswordUnlocked(i, syntheticPassword);
                unlockUserKey(i, unlockLskfBasedProtector.syntheticPassword);
            }
        }
    }

    public void resetKeyStore(int i) {
        int[] profileIdsWithDisabled;
        int[] iArr;
        checkWritePermission();
        Slogf.m28d("LockSettingsService", "Resetting keystore for user %d", Integer.valueOf(i));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            if (isCredentialSharableWithParent(userInfo.id) && !getSeparateProfileChallengeEnabledInternal(userInfo.id) && this.mStorage.hasChildProfileLock(userInfo.id)) {
                try {
                    arrayList2.add(getDecryptedPasswordForTiedProfile(userInfo.id));
                    arrayList.add(Integer.valueOf(userInfo.id));
                } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                    Slog.e("LockSettingsService", "Failed to decrypt child profile key", e);
                }
            }
        }
        int i2 = 0;
        try {
            for (int i3 : this.mUserManager.getProfileIdsWithDisabled(i)) {
                int length = SYSTEM_CREDENTIAL_UIDS.length;
                for (int i4 = 0; i4 < length; i4++) {
                    AndroidKeyStoreMaintenance.clearNamespace(0, UserHandle.getUid(i3, iArr[i4]));
                }
            }
            if (this.mUserManager.getUserInfo(i).isPrimary()) {
                AndroidKeyStoreMaintenance.clearNamespace(2, 102L);
            }
        } finally {
            while (i2 < arrayList.size()) {
                int intValue = ((Integer) arrayList.get(i2)).intValue();
                LockscreenCredential lockscreenCredential = (LockscreenCredential) arrayList2.get(i2);
                if (intValue != -1 && lockscreenCredential != null) {
                    tieProfileLockToParent(intValue, i, lockscreenCredential);
                }
                if (lockscreenCredential != null) {
                    lockscreenCredential.zeroize();
                }
                i2++;
            }
        }
    }

    public VerifyCredentialResponse checkCredential(LockscreenCredential lockscreenCredential, int i, ICheckCredentialProgressCallback iCheckCredentialProgressCallback) {
        checkPasswordReadPermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return doVerifyCredential(lockscreenCredential, i, iCheckCredentialProgressCallback, 0);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            scheduleGc();
        }
    }

    public VerifyCredentialResponse verifyCredential(LockscreenCredential lockscreenCredential, int i, int i2) {
        if (!hasPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") && !hasPermission("android.permission.SET_AND_VERIFY_LOCKSCREEN_CREDENTIALS")) {
            throw new SecurityException("verifyCredential requires SET_AND_VERIFY_LOCKSCREEN_CREDENTIALS or android.permission.ACCESS_KEYGUARD_SECURE_STORAGE");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return doVerifyCredential(lockscreenCredential, i, null, i2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            scheduleGc();
        }
    }

    public VerifyCredentialResponse verifyGatekeeperPasswordHandle(long j, long j2, int i) {
        byte[] bArr;
        VerifyCredentialResponse verifyChallengeInternal;
        checkPasswordReadPermission();
        synchronized (this.mGatekeeperPasswords) {
            bArr = this.mGatekeeperPasswords.get(j);
        }
        synchronized (this.mSpManager) {
            if (bArr == null) {
                Slog.d("LockSettingsService", "No gatekeeper password for handle");
                verifyChallengeInternal = VerifyCredentialResponse.ERROR;
            } else {
                verifyChallengeInternal = this.mSpManager.verifyChallengeInternal(getGateKeeperService(), bArr, j2, i);
            }
        }
        return verifyChallengeInternal;
    }

    public void removeGatekeeperPasswordHandle(long j) {
        checkPasswordReadPermission();
        synchronized (this.mGatekeeperPasswords) {
            this.mGatekeeperPasswords.remove(j);
        }
    }

    public final VerifyCredentialResponse doVerifyCredential(LockscreenCredential lockscreenCredential, int i, ICheckCredentialProgressCallback iCheckCredentialProgressCallback, int i2) {
        if (lockscreenCredential == null || lockscreenCredential.isNone()) {
            throw new IllegalArgumentException("Credential can't be null or empty");
        }
        if (i == -9999 && Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
            Slog.e("LockSettingsService", "FRP credential can only be verified prior to provisioning.");
            return VerifyCredentialResponse.ERROR;
        }
        Slogf.m20i("LockSettingsService", "Verifying lockscreen credential for user %d", Integer.valueOf(i));
        synchronized (this.mSpManager) {
            if (i == -9999) {
                return this.mSpManager.verifyFrpCredential(getGateKeeperService(), lockscreenCredential, iCheckCredentialProgressCallback);
            }
            SyntheticPasswordManager.AuthenticationResult unlockLskfBasedProtector = this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), getCurrentLskfBasedProtectorId(i), lockscreenCredential, i, iCheckCredentialProgressCallback);
            VerifyCredentialResponse verifyCredentialResponse = unlockLskfBasedProtector.gkResponse;
            if (verifyCredentialResponse.getResponseCode() == 0) {
                this.mBiometricDeferredQueue.addPendingLockoutResetForUser(i, unlockLskfBasedProtector.syntheticPassword.deriveGkPassword());
                verifyCredentialResponse = this.mSpManager.verifyChallenge(getGateKeeperService(), unlockLskfBasedProtector.syntheticPassword, 0L, i);
                if (verifyCredentialResponse.getResponseCode() != 0) {
                    Slog.wtf("LockSettingsService", "verifyChallenge with SP failed.");
                    return VerifyCredentialResponse.ERROR;
                }
            }
            if (verifyCredentialResponse.getResponseCode() == 0) {
                Slogf.m20i("LockSettingsService", "Successfully verified lockscreen credential for user %d", Integer.valueOf(i));
                onCredentialVerified(unlockLskfBasedProtector.syntheticPassword, PasswordMetrics.computeForCredential(lockscreenCredential), i);
                if ((i2 & 1) != 0) {
                    verifyCredentialResponse = new VerifyCredentialResponse.Builder().setGatekeeperPasswordHandle(storeGatekeeperPasswordTemporarily(unlockLskfBasedProtector.syntheticPassword.deriveGkPassword())).build();
                }
                sendCredentialsOnUnlockIfRequired(lockscreenCredential, i);
            } else if (verifyCredentialResponse.getResponseCode() == 1 && verifyCredentialResponse.getTimeout() > 0) {
                requireStrongAuth(8, i);
            }
            return verifyCredentialResponse;
        }
    }

    public VerifyCredentialResponse verifyTiedProfileChallenge(LockscreenCredential lockscreenCredential, int i, int i2) {
        checkPasswordReadPermission();
        if (!isProfileWithUnifiedLock(i)) {
            throw new IllegalArgumentException("User id must be managed/clone profile with unified lock");
        }
        VerifyCredentialResponse doVerifyCredential = doVerifyCredential(lockscreenCredential, this.mUserManager.getProfileParent(i).id, null, i2);
        if (doVerifyCredential.getResponseCode() != 0) {
            return doVerifyCredential;
        }
        try {
            try {
                return doVerifyCredential(getDecryptedPasswordForTiedProfile(i), i, null, i2);
            } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                Slog.e("LockSettingsService", "Failed to decrypt child profile key", e);
                throw new IllegalStateException("Unable to get tied profile token");
            }
        } finally {
            scheduleGc();
        }
    }

    public final void setUserPasswordMetrics(LockscreenCredential lockscreenCredential, int i) {
        synchronized (this) {
            this.mUserPasswordMetrics.put(i, PasswordMetrics.computeForCredential(lockscreenCredential));
        }
    }

    @VisibleForTesting
    public PasswordMetrics getUserPasswordMetrics(int i) {
        PasswordMetrics passwordMetrics;
        if (!isUserSecure(i)) {
            return new PasswordMetrics(-1);
        }
        synchronized (this) {
            passwordMetrics = this.mUserPasswordMetrics.get(i);
        }
        return passwordMetrics;
    }

    public final PasswordMetrics loadPasswordMetrics(SyntheticPasswordManager.SyntheticPassword syntheticPassword, int i) {
        synchronized (this.mSpManager) {
            if (isUserSecure(i)) {
                return this.mSpManager.getPasswordMetrics(syntheticPassword, getCurrentLskfBasedProtectorId(i), i);
            }
            return null;
        }
    }

    public final void notifyPasswordChanged(final LockscreenCredential lockscreenCredential, final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                LockSettingsService.this.lambda$notifyPasswordChanged$5(lockscreenCredential, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyPasswordChanged$5(LockscreenCredential lockscreenCredential, int i) {
        this.mInjector.getDevicePolicyManager().reportPasswordChanged(PasswordMetrics.computeForCredential(lockscreenCredential), i);
        ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).reportPasswordChanged(i);
    }

    public final void createNewUser(int i, int i2) {
        synchronized (this.mUserCreationAndRemovalLock) {
            if (!this.mThirdPartyAppsStarted) {
                Slogf.m20i("LockSettingsService", "Delaying locksettings state creation for user %d until third-party apps are started", Integer.valueOf(i));
                this.mEarlyCreatedUsers.put(i, i2);
                this.mEarlyRemovedUsers.delete(i);
                return;
            }
            removeStateForReusedUserIdIfNecessary(i, i2);
            initializeSyntheticPassword(i);
        }
    }

    public final void removeUser(int i) {
        synchronized (this.mUserCreationAndRemovalLock) {
            if (!this.mThirdPartyAppsStarted) {
                Slogf.m20i("LockSettingsService", "Delaying locksettings state removal for user %d until third-party apps are started", Integer.valueOf(i));
                if (this.mEarlyCreatedUsers.indexOfKey(i) >= 0) {
                    this.mEarlyCreatedUsers.delete(i);
                } else {
                    this.mEarlyRemovedUsers.put(i, -1);
                }
                return;
            }
            Slogf.m20i("LockSettingsService", "Removing state for user %d", Integer.valueOf(i));
            removeUserState(i);
        }
    }

    public final void removeUserState(int i) {
        removeBiometricsForUser(i);
        this.mSpManager.removeUser(getGateKeeperService(), i);
        this.mStrongAuth.removeUser(i);
        AndroidKeyStoreMaintenance.onUserRemoved(i);
        this.mManagedProfilePasswordCache.removePassword(i);
        gateKeeperClearSecureUserId(i);
        removeKeystoreProfileKey(i);
        this.mStorage.removeUser(i);
    }

    public final void removeKeystoreProfileKey(int i) {
        String str = "profile_key_name_encrypt_" + i;
        String str2 = "profile_key_name_decrypt_" + i;
        try {
            if (this.mJavaKeyStore.containsAlias(str) || this.mJavaKeyStore.containsAlias(str2)) {
                Slogf.m20i("LockSettingsService", "Removing keystore profile key for user %d", Integer.valueOf(i));
                this.mJavaKeyStore.deleteEntry(str);
                this.mJavaKeyStore.deleteEntry(str2);
            }
        } catch (KeyStoreException e) {
            Slogf.m23e("LockSettingsService", e, "Error removing keystore profile key for user %d", Integer.valueOf(i));
        }
    }

    public void registerStrongAuthTracker(IStrongAuthTracker iStrongAuthTracker) {
        checkPasswordReadPermission();
        this.mStrongAuth.registerStrongAuthTracker(iStrongAuthTracker);
    }

    public void unregisterStrongAuthTracker(IStrongAuthTracker iStrongAuthTracker) {
        checkPasswordReadPermission();
        this.mStrongAuth.unregisterStrongAuthTracker(iStrongAuthTracker);
    }

    public void requireStrongAuth(int i, int i2) {
        checkWritePermission();
        this.mStrongAuth.requireStrongAuth(i, i2);
    }

    public void reportSuccessfulBiometricUnlock(boolean z, int i) {
        checkBiometricPermission();
        this.mStrongAuth.reportSuccessfulBiometricUnlock(z, i);
    }

    public void scheduleNonStrongBiometricIdleTimeout(int i) {
        checkBiometricPermission();
        this.mStrongAuth.scheduleNonStrongBiometricIdleTimeout(i);
    }

    public void userPresent(int i) {
        checkWritePermission();
        this.mStrongAuth.reportUnlock(i);
    }

    public int getStrongAuthForUser(int i) {
        checkPasswordReadPermission();
        return this.mStrongAuthTracker.getStrongAuthForUser(i);
    }

    public final boolean isCallerShell() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    public final void enforceShell() {
        if (!isCallerShell()) {
            throw new SecurityException("Caller must be shell");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        enforceShell();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        Object[] objArr = new Object[3];
        objArr[0] = ArrayUtils.isEmpty(strArr) ? "" : strArr[0];
        objArr[1] = Integer.valueOf(callingPid);
        objArr[2] = Integer.valueOf(callingUid);
        Slogf.m20i("LockSettingsService", "Executing shell command '%s'; callingPid=%d, callingUid=%d", objArr);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            new LockSettingsShellCommand(new LockPatternUtils(this.mContext), this.mContext, callingPid, callingUid).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void initRecoveryServiceWithSigFile(String str, byte[] bArr, byte[] bArr2) throws RemoteException {
        this.mRecoverableKeyStoreManager.initRecoveryServiceWithSigFile(str, bArr, bArr2);
    }

    public KeyChainSnapshot getKeyChainSnapshot() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getKeyChainSnapshot();
    }

    public void setSnapshotCreatedPendingIntent(PendingIntent pendingIntent) throws RemoteException {
        this.mRecoverableKeyStoreManager.setSnapshotCreatedPendingIntent(pendingIntent);
    }

    public void setServerParams(byte[] bArr) throws RemoteException {
        this.mRecoverableKeyStoreManager.setServerParams(bArr);
    }

    public void setRecoveryStatus(String str, int i) throws RemoteException {
        this.mRecoverableKeyStoreManager.setRecoveryStatus(str, i);
    }

    public Map getRecoveryStatus() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getRecoveryStatus();
    }

    public void setRecoverySecretTypes(int[] iArr) throws RemoteException {
        this.mRecoverableKeyStoreManager.setRecoverySecretTypes(iArr);
    }

    public int[] getRecoverySecretTypes() throws RemoteException {
        return this.mRecoverableKeyStoreManager.getRecoverySecretTypes();
    }

    public byte[] startRecoverySessionWithCertPath(String str, String str2, RecoveryCertPath recoveryCertPath, byte[] bArr, byte[] bArr2, List<KeyChainProtectionParams> list) throws RemoteException {
        return this.mRecoverableKeyStoreManager.startRecoverySessionWithCertPath(str, str2, recoveryCertPath, bArr, bArr2, list);
    }

    public Map<String, String> recoverKeyChainSnapshot(String str, byte[] bArr, List<WrappedApplicationKey> list) throws RemoteException {
        return this.mRecoverableKeyStoreManager.recoverKeyChainSnapshot(str, bArr, list);
    }

    public void closeSession(String str) throws RemoteException {
        this.mRecoverableKeyStoreManager.closeSession(str);
    }

    public void removeKey(String str) throws RemoteException {
        this.mRecoverableKeyStoreManager.removeKey(str);
    }

    public String generateKey(String str) throws RemoteException {
        return this.mRecoverableKeyStoreManager.generateKey(str);
    }

    public String generateKeyWithMetadata(String str, byte[] bArr) throws RemoteException {
        return this.mRecoverableKeyStoreManager.generateKeyWithMetadata(str, bArr);
    }

    public String importKey(String str, byte[] bArr) throws RemoteException {
        return this.mRecoverableKeyStoreManager.importKey(str, bArr);
    }

    public String importKeyWithMetadata(String str, byte[] bArr, byte[] bArr2) throws RemoteException {
        return this.mRecoverableKeyStoreManager.importKeyWithMetadata(str, bArr, bArr2);
    }

    public String getKey(String str) throws RemoteException {
        return this.mRecoverableKeyStoreManager.getKey(str);
    }

    public StartLockscreenValidationRequest startRemoteLockscreenValidation() {
        return this.mRecoverableKeyStoreManager.startRemoteLockscreenValidation(this);
    }

    public RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] bArr) {
        return this.mRecoverableKeyStoreManager.validateRemoteLockscreen(bArr, this);
    }

    /* loaded from: classes2.dex */
    public class GateKeeperDiedRecipient implements IBinder.DeathRecipient {
        public GateKeeperDiedRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            LockSettingsService.this.mGateKeeperService.asBinder().unlinkToDeath(this, 0);
            LockSettingsService.this.mGateKeeperService = null;
        }
    }

    public final synchronized IGateKeeperService getGateKeeperService() {
        IGateKeeperService iGateKeeperService = this.mGateKeeperService;
        if (iGateKeeperService != null) {
            return iGateKeeperService;
        }
        IBinder service = ServiceManager.getService("android.service.gatekeeper.IGateKeeperService");
        if (service != null) {
            try {
                service.linkToDeath(new GateKeeperDiedRecipient(), 0);
            } catch (RemoteException e) {
                Slog.w("LockSettingsService", " Unable to register death recipient", e);
            }
            IGateKeeperService asInterface = IGateKeeperService.Stub.asInterface(service);
            this.mGateKeeperService = asInterface;
            return asInterface;
        }
        Slog.e("LockSettingsService", "Unable to acquire GateKeeperService");
        return null;
    }

    public final void gateKeeperClearSecureUserId(int i) {
        try {
            getGateKeeperService().clearSecureUserId(i);
        } catch (RemoteException e) {
            Slog.w("LockSettingsService", "Failed to clear SID", e);
        }
    }

    public final void onSyntheticPasswordCreated(int i, SyntheticPasswordManager.SyntheticPassword syntheticPassword) {
        onSyntheticPasswordKnown(i, syntheticPassword, true);
    }

    public final void onSyntheticPasswordUnlocked(int i, SyntheticPasswordManager.SyntheticPassword syntheticPassword) {
        onSyntheticPasswordKnown(i, syntheticPassword, false);
    }

    public final void onSyntheticPasswordKnown(int i, SyntheticPasswordManager.SyntheticPassword syntheticPassword, boolean z) {
        if (this.mInjector.isGsiRunning()) {
            Slog.w("LockSettingsService", "Running in GSI; skipping calls to AuthSecret and RebootEscrow");
            return;
        }
        this.mRebootEscrowManager.callToRebootEscrowIfNeeded(i, syntheticPassword.getVersion(), syntheticPassword.getSyntheticPassword());
        callToAuthSecretIfNeeded(i, syntheticPassword, z);
    }

    public final void callToAuthSecretIfNeeded(int i, SyntheticPasswordManager.SyntheticPassword syntheticPassword, boolean z) {
        UserInfo userInfo;
        byte[] readVendorAuthSecret;
        byte[] bArr;
        byte[] bArr2;
        if (this.mAuthSecretService == null || (userInfo = this.mInjector.getUserManagerInternal().getUserInfo(i)) == null) {
            return;
        }
        if (this.mInjector.isHeadlessSystemUserMode()) {
            if (!this.mInjector.isMainUserPermanentAdmin() || !userInfo.isFull()) {
                return;
            }
            if (z) {
                if (userInfo.isMain()) {
                    Slog.i("LockSettingsService", "Generating new vendor auth secret and storing for user: " + i);
                    bArr2 = SecureRandomUtils.randomBytes(32);
                    synchronized (this.mHeadlessAuthSecretLock) {
                        this.mAuthSecret = bArr2;
                    }
                } else {
                    synchronized (this.mHeadlessAuthSecretLock) {
                        bArr = this.mAuthSecret;
                    }
                    if (bArr == null) {
                        Slog.e("LockSettingsService", "Creating non-main user " + i + " but vendor auth secret is not in memory");
                        return;
                    }
                    bArr2 = bArr;
                }
                this.mSpManager.writeVendorAuthSecret(bArr2, syntheticPassword, i);
                readVendorAuthSecret = bArr2;
            } else {
                readVendorAuthSecret = this.mSpManager.readVendorAuthSecret(syntheticPassword, i);
                if (readVendorAuthSecret == null) {
                    Slog.e("LockSettingsService", "Unable to read vendor auth secret for user: " + i);
                    return;
                }
                synchronized (this.mHeadlessAuthSecretLock) {
                    this.mAuthSecret = readVendorAuthSecret;
                }
            }
        } else if (i != 0) {
            return;
        } else {
            readVendorAuthSecret = syntheticPassword.deriveVendorAuthSecret();
        }
        Slog.i("LockSettingsService", "Sending vendor auth secret to IAuthSecret HAL as user: " + i);
        try {
            this.mAuthSecretService.setPrimaryUserCredential(readVendorAuthSecret);
        } catch (RemoteException e) {
            Slog.w("LockSettingsService", "Failed to send vendor auth secret to IAuthSecret HAL", e);
        }
    }

    @VisibleForTesting
    public SyntheticPasswordManager.SyntheticPassword initializeSyntheticPassword(int i) {
        SyntheticPasswordManager.SyntheticPassword newSyntheticPassword;
        synchronized (this.mSpManager) {
            Slogf.m20i("LockSettingsService", "Initializing synthetic password for user %d", Integer.valueOf(i));
            Preconditions.checkState(getCurrentLskfBasedProtectorId(i) == 0, "Cannot reinitialize SP");
            newSyntheticPassword = this.mSpManager.newSyntheticPassword(i);
            setCurrentLskfBasedProtectorId(this.mSpManager.createLskfBasedProtector(getGateKeeperService(), LockscreenCredential.createNone(), newSyntheticPassword, i), i);
            setUserKeyProtection(i, newSyntheticPassword.deriveFileBasedEncryptionKey());
            onSyntheticPasswordCreated(i, newSyntheticPassword);
            Slogf.m20i("LockSettingsService", "Successfully initialized synthetic password for user %d", Integer.valueOf(i));
        }
        return newSyntheticPassword;
    }

    @VisibleForTesting
    public long getCurrentLskfBasedProtectorId(int i) {
        return getLong("sp-handle", 0L, i);
    }

    public final void setCurrentLskfBasedProtectorId(long j, int i) {
        long currentLskfBasedProtectorId = getCurrentLskfBasedProtectorId(i);
        setLong("sp-handle", j, i);
        setLong("prev-sp-handle", currentLskfBasedProtectorId, i);
        setLong("sp-handle-ts", System.currentTimeMillis(), i);
    }

    public final long storeGatekeeperPasswordTemporarily(byte[] bArr) {
        final long j;
        synchronized (this.mGatekeeperPasswords) {
            j = 0;
            while (true) {
                if (j != 0) {
                    if (this.mGatekeeperPasswords.get(j) == null) {
                        this.mGatekeeperPasswords.put(j, bArr);
                    }
                }
                j = SecureRandomUtils.randomLong();
            }
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                LockSettingsService.this.lambda$storeGatekeeperPasswordTemporarily$6(j);
            }
        }, 600000L);
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$storeGatekeeperPasswordTemporarily$6(long j) {
        synchronized (this.mGatekeeperPasswords) {
            if (this.mGatekeeperPasswords.get(j) != null) {
                Slogf.m28d("LockSettingsService", "Cached Gatekeeper password with handle %016x has expired", Long.valueOf(j));
                this.mGatekeeperPasswords.remove(j);
            }
        }
    }

    public final void onCredentialVerified(SyntheticPasswordManager.SyntheticPassword syntheticPassword, PasswordMetrics passwordMetrics, int i) {
        if (passwordMetrics != null) {
            synchronized (this) {
                this.mUserPasswordMetrics.put(i, passwordMetrics);
            }
        }
        unlockKeystore(syntheticPassword.deriveKeyStorePassword(), i);
        unlockUserKey(i, syntheticPassword);
        lambda$setLockCredentialWithToken$7(i);
        activateEscrowTokens(syntheticPassword, i);
        if (isProfileWithSeparatedLock(i)) {
            setDeviceUnlockedForUser(i);
        }
        this.mStrongAuth.reportSuccessfulStrongAuthUnlock(i);
        onSyntheticPasswordUnlocked(i, syntheticPassword);
    }

    public final void setDeviceUnlockedForUser(int i) {
        ((TrustManager) this.mContext.getSystemService(TrustManager.class)).setDeviceLockedForUser(i, false);
    }

    @GuardedBy({"mSpManager"})
    public final long setLockCredentialWithSpLocked(LockscreenCredential lockscreenCredential, SyntheticPasswordManager.SyntheticPassword syntheticPassword, int i) {
        String str;
        Map<Integer, LockscreenCredential> decryptedPasswordsForAllTiedProfiles;
        Map<Integer, LockscreenCredential> map;
        Slogf.m20i("LockSettingsService", "Changing lockscreen credential of user %d; newCredentialType=%s\n", Integer.valueOf(i), LockPatternUtils.credentialTypeToString(lockscreenCredential.getType()));
        int credentialTypeInternal = getCredentialTypeInternal(i);
        long currentLskfBasedProtectorId = getCurrentLskfBasedProtectorId(i);
        long createLskfBasedProtector = this.mSpManager.createLskfBasedProtector(getGateKeeperService(), lockscreenCredential, syntheticPassword, i);
        if (!lockscreenCredential.isNone()) {
            if (this.mSpManager.hasSidForUser(i)) {
                this.mSpManager.verifyChallenge(getGateKeeperService(), syntheticPassword, 0L, i);
                str = "LockSettingsService";
                map = null;
            } else {
                this.mSpManager.newSidForUser(getGateKeeperService(), syntheticPassword, i);
                str = "LockSettingsService";
                map = null;
                this.mSpManager.verifyChallenge(getGateKeeperService(), syntheticPassword, 0L, i);
                setKeystorePassword(syntheticPassword.deriveKeyStorePassword(), i);
            }
            decryptedPasswordsForAllTiedProfiles = map;
        } else {
            str = "LockSettingsService";
            decryptedPasswordsForAllTiedProfiles = getDecryptedPasswordsForAllTiedProfiles(i);
            this.mSpManager.clearSidForUser(i);
            gateKeeperClearSecureUserId(i);
            unlockUserKey(i, syntheticPassword);
            unlockKeystore(syntheticPassword.deriveKeyStorePassword(), i);
            setKeystorePassword(null, i);
            removeBiometricsForUser(i);
        }
        setCurrentLskfBasedProtectorId(createLskfBasedProtector, i);
        LockPatternUtils.invalidateCredentialTypeCache();
        synchronizeUnifiedWorkChallengeForProfiles(i, decryptedPasswordsForAllTiedProfiles);
        setUserPasswordMetrics(lockscreenCredential, i);
        this.mManagedProfilePasswordCache.removePassword(i);
        if (credentialTypeInternal != -1) {
            this.mSpManager.destroyAllWeakTokenBasedProtectors(i);
        }
        if (decryptedPasswordsForAllTiedProfiles != null) {
            for (Map.Entry<Integer, LockscreenCredential> entry : decryptedPasswordsForAllTiedProfiles.entrySet()) {
                entry.getValue().zeroize();
            }
        }
        this.mSpManager.destroyLskfBasedProtector(currentLskfBasedProtectorId, i);
        Slogf.m20i(str, "Successfully changed lockscreen credential of user %d", Integer.valueOf(i));
        return createLskfBasedProtector;
    }

    public final void removeBiometricsForUser(int i) {
        removeAllFingerprintForUser(i);
        removeAllFaceForUser(i);
    }

    public final void removeAllFingerprintForUser(int i) {
        FingerprintManager fingerprintManager = this.mInjector.getFingerprintManager();
        if (fingerprintManager != null && fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints(i)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            fingerprintManager.removeAll(i, fingerprintManagerRemovalCallback(countDownLatch));
            try {
                countDownLatch.await(10000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Slog.e("LockSettingsService", "Latch interrupted when removing fingerprint", e);
            }
        }
    }

    public final void removeAllFaceForUser(int i) {
        FaceManager faceManager = this.mInjector.getFaceManager();
        if (faceManager != null && faceManager.isHardwareDetected() && faceManager.hasEnrolledTemplates(i)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            faceManager.removeAll(i, faceManagerRemovalCallback(countDownLatch));
            try {
                countDownLatch.await(10000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Slog.e("LockSettingsService", "Latch interrupted when removing face", e);
            }
        }
    }

    public final FingerprintManager.RemovalCallback fingerprintManagerRemovalCallback(final CountDownLatch countDownLatch) {
        return new FingerprintManager.RemovalCallback() { // from class: com.android.server.locksettings.LockSettingsService.4
            public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
                Slog.e("LockSettingsService", "Unable to remove fingerprint, error: " + ((Object) charSequence));
                countDownLatch.countDown();
            }

            public void onRemovalSucceeded(Fingerprint fingerprint, int i) {
                if (i == 0) {
                    countDownLatch.countDown();
                }
            }
        };
    }

    public final FaceManager.RemovalCallback faceManagerRemovalCallback(final CountDownLatch countDownLatch) {
        return new FaceManager.RemovalCallback() { // from class: com.android.server.locksettings.LockSettingsService.5
            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                Slog.e("LockSettingsService", "Unable to remove face, error: " + ((Object) charSequence));
                countDownLatch.countDown();
            }

            public void onRemovalSucceeded(Face face, int i) {
                if (i == 0) {
                    countDownLatch.countDown();
                }
            }
        };
    }

    public byte[] getHashFactor(LockscreenCredential lockscreenCredential, int i) {
        checkPasswordReadPermission();
        try {
            Slogf.m28d("LockSettingsService", "Getting password history hash factor for user %d", Integer.valueOf(i));
            if (isProfileWithUnifiedLock(i)) {
                lockscreenCredential = getDecryptedPasswordForTiedProfile(i);
            }
            LockscreenCredential lockscreenCredential2 = lockscreenCredential;
            synchronized (this.mSpManager) {
                SyntheticPasswordManager.SyntheticPassword syntheticPassword = this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), getCurrentLskfBasedProtectorId(i), lockscreenCredential2, i, null).syntheticPassword;
                if (syntheticPassword == null) {
                    Slog.w("LockSettingsService", "Current credential is incorrect");
                    return null;
                }
                return syntheticPassword.derivePasswordHashFactor();
            }
        } catch (Exception e) {
            Slog.e("LockSettingsService", "Failed to get work profile credential", e);
            return null;
        } finally {
            scheduleGc();
        }
    }

    public final long addEscrowToken(byte[] bArr, int i, int i2, LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback) {
        long addPendingToken;
        Slogf.m20i("LockSettingsService", "Adding escrow token for user %d", Integer.valueOf(i2));
        synchronized (this.mSpManager) {
            SyntheticPasswordManager.SyntheticPassword syntheticPassword = !isUserSecure(i2) ? this.mSpManager.unlockLskfBasedProtector(getGateKeeperService(), getCurrentLskfBasedProtectorId(i2), LockscreenCredential.createNone(), i2, null).syntheticPassword : null;
            disableEscrowTokenOnNonManagedDevicesIfNeeded(i2);
            if (!this.mSpManager.hasEscrowData(i2)) {
                throw new SecurityException("Escrow token is disabled on the current user");
            }
            addPendingToken = this.mSpManager.addPendingToken(bArr, i, i2, escrowTokenStateChangeCallback);
            if (syntheticPassword != null) {
                Slogf.m20i("LockSettingsService", "Immediately activating escrow token %016x", Long.valueOf(addPendingToken));
                this.mSpManager.createTokenBasedProtector(addPendingToken, syntheticPassword, i2);
            } else {
                Slogf.m20i("LockSettingsService", "Escrow token %016x will be activated when user is unlocked", Long.valueOf(addPendingToken));
            }
        }
        return addPendingToken;
    }

    public final void activateEscrowTokens(SyntheticPasswordManager.SyntheticPassword syntheticPassword, int i) {
        synchronized (this.mSpManager) {
            disableEscrowTokenOnNonManagedDevicesIfNeeded(i);
            for (Long l : this.mSpManager.getPendingTokensForUser(i)) {
                long longValue = l.longValue();
                Slogf.m20i("LockSettingsService", "Activating escrow token %016x for user %d", Long.valueOf(longValue), Integer.valueOf(i));
                this.mSpManager.createTokenBasedProtector(longValue, syntheticPassword, i);
            }
        }
    }

    public final boolean isEscrowTokenActive(long j, int i) {
        boolean protectorExists;
        synchronized (this.mSpManager) {
            protectorExists = this.mSpManager.protectorExists(j, i);
        }
        return protectorExists;
    }

    public boolean hasPendingEscrowToken(int i) {
        boolean z;
        checkPasswordReadPermission();
        synchronized (this.mSpManager) {
            z = !this.mSpManager.getPendingTokensForUser(i).isEmpty();
        }
        return z;
    }

    public final boolean removeEscrowToken(long j, int i) {
        synchronized (this.mSpManager) {
            if (j == getCurrentLskfBasedProtectorId(i)) {
                Slog.w("LockSettingsService", "Escrow token handle equals LSKF-based protector ID");
                return false;
            } else if (this.mSpManager.removePendingToken(j, i)) {
                return true;
            } else {
                if (this.mSpManager.protectorExists(j, i)) {
                    this.mSpManager.destroyTokenBasedProtector(j, i);
                    return true;
                }
                return false;
            }
        }
    }

    public final boolean setLockCredentialWithToken(LockscreenCredential lockscreenCredential, long j, byte[] bArr, final int i) {
        synchronized (this.mSpManager) {
            if (!this.mSpManager.hasEscrowData(i)) {
                throw new SecurityException("Escrow token is disabled on the current user");
            }
            if (!isEscrowTokenActive(j, i)) {
                Slog.e("LockSettingsService", "Unknown or unactivated token: " + Long.toHexString(j));
                return false;
            }
            boolean lockCredentialWithTokenInternalLocked = setLockCredentialWithTokenInternalLocked(lockscreenCredential, j, bArr, i);
            if (lockCredentialWithTokenInternalLocked) {
                synchronized (this.mSeparateChallengeLock) {
                    setSeparateProfileChallengeEnabledLocked(i, true, null);
                }
                if (lockscreenCredential.isNone()) {
                    this.mHandler.post(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            LockSettingsService.this.lambda$setLockCredentialWithToken$7(i);
                        }
                    });
                }
                notifyPasswordChanged(lockscreenCredential, i);
                notifySeparateProfileChallengeChanged(i);
            }
            return lockCredentialWithTokenInternalLocked;
        }
    }

    @GuardedBy({"mSpManager"})
    public final boolean setLockCredentialWithTokenInternalLocked(LockscreenCredential lockscreenCredential, long j, byte[] bArr, int i) {
        Slogf.m20i("LockSettingsService", "Resetting lockscreen credential of user %d using escrow token %016x", Integer.valueOf(i), Long.valueOf(j));
        SyntheticPasswordManager.AuthenticationResult unlockTokenBasedProtector = this.mSpManager.unlockTokenBasedProtector(getGateKeeperService(), j, bArr, i);
        if (unlockTokenBasedProtector.syntheticPassword == null) {
            Slog.w("LockSettingsService", "Invalid escrow token supplied");
            return false;
        } else if (unlockTokenBasedProtector.gkResponse.getResponseCode() != 0) {
            Slog.e("LockSettingsService", "Obsolete token: synthetic password decrypted but it fails GK verification.");
            return false;
        } else {
            onSyntheticPasswordUnlocked(i, unlockTokenBasedProtector.syntheticPassword);
            setLockCredentialWithSpLocked(lockscreenCredential, unlockTokenBasedProtector.syntheticPassword, i);
            return true;
        }
    }

    public final boolean unlockUserWithToken(long j, byte[] bArr, int i) {
        synchronized (this.mSpManager) {
            Slogf.m20i("LockSettingsService", "Unlocking user %d using escrow token %016x", Integer.valueOf(i), Long.valueOf(j));
            if (!this.mSpManager.hasEscrowData(i)) {
                Slogf.m12w("LockSettingsService", "Escrow token support is disabled on user %d", Integer.valueOf(i));
                return false;
            }
            SyntheticPasswordManager.AuthenticationResult unlockTokenBasedProtector = this.mSpManager.unlockTokenBasedProtector(getGateKeeperService(), j, bArr, i);
            if (unlockTokenBasedProtector.syntheticPassword == null) {
                Slog.w("LockSettingsService", "Invalid escrow token supplied");
                return false;
            }
            Slogf.m20i("LockSettingsService", "Unlocked synthetic password for user %d using escrow token", Integer.valueOf(i));
            SyntheticPasswordManager.SyntheticPassword syntheticPassword = unlockTokenBasedProtector.syntheticPassword;
            onCredentialVerified(syntheticPassword, loadPasswordMetrics(syntheticPassword, i), i);
            return true;
        }
    }

    public boolean tryUnlockWithCachedUnifiedChallenge(int i) {
        checkPasswordReadPermission();
        LockscreenCredential retrievePassword = this.mManagedProfilePasswordCache.retrievePassword(i);
        if (retrievePassword == null) {
            if (retrievePassword != null) {
                retrievePassword.close();
            }
            return false;
        }
        try {
            boolean z = doVerifyCredential(retrievePassword, i, null, 0).getResponseCode() == 0;
            retrievePassword.close();
            return z;
        } catch (Throwable th) {
            try {
                retrievePassword.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public void removeCachedUnifiedChallenge(int i) {
        checkWritePermission();
        this.mManagedProfilePasswordCache.removePassword(i);
    }

    public static String timestampToString(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(j));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "LockSettingsService", printWriter)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                dumpInternal(printWriter);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final void dumpInternal(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("Current lock settings service state:");
        indentingPrintWriter.println();
        indentingPrintWriter.println("User State:");
        indentingPrintWriter.increaseIndent();
        List users = this.mUserManager.getUsers();
        for (int i = 0; i < users.size(); i++) {
            int i2 = ((UserInfo) users.get(i)).id;
            indentingPrintWriter.println("User " + i2);
            indentingPrintWriter.increaseIndent();
            synchronized (this.mSpManager) {
                indentingPrintWriter.println(TextUtils.formatSimple("LSKF-based SP protector ID: %016x", new Object[]{Long.valueOf(getCurrentLskfBasedProtectorId(i2))}));
                indentingPrintWriter.println(TextUtils.formatSimple("LSKF last changed: %s (previous protector: %016x)", new Object[]{timestampToString(getLong("sp-handle-ts", 0L, i2)), Long.valueOf(getLong("prev-sp-handle", 0L, i2))}));
            }
            try {
                indentingPrintWriter.println(TextUtils.formatSimple("SID: %016x", new Object[]{Long.valueOf(getGateKeeperService().getSecureUserId(i2))}));
            } catch (RemoteException unused) {
            }
            indentingPrintWriter.println("Quality: " + getKeyguardStoredQuality(i2));
            indentingPrintWriter.println("CredentialType: " + LockPatternUtils.credentialTypeToString(getCredentialTypeInternal(i2)));
            indentingPrintWriter.println("SeparateChallenge: " + getSeparateProfileChallengeEnabledInternal(i2));
            Object[] objArr = new Object[1];
            objArr[0] = getUserPasswordMetrics(i2) != null ? "known" : "unknown";
            indentingPrintWriter.println(TextUtils.formatSimple("Metrics: %s", objArr));
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Keys in namespace:");
        indentingPrintWriter.increaseIndent();
        dumpKeystoreKeys(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Storage:");
        indentingPrintWriter.increaseIndent();
        this.mStorage.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("StrongAuth:");
        indentingPrintWriter.increaseIndent();
        this.mStrongAuth.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("RebootEscrow:");
        indentingPrintWriter.increaseIndent();
        this.mRebootEscrowManager.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("PasswordHandleCount: " + this.mGatekeeperPasswords.size());
        synchronized (this.mUserCreationAndRemovalLock) {
            indentingPrintWriter.println("ThirdPartyAppsStarted: " + this.mThirdPartyAppsStarted);
        }
    }

    public final void dumpKeystoreKeys(IndentingPrintWriter indentingPrintWriter) {
        try {
            Enumeration<String> aliases = this.mJavaKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                indentingPrintWriter.println(aliases.nextElement());
            }
        } catch (KeyStoreException e) {
            indentingPrintWriter.println("Unable to get keys: " + e.toString());
            Slog.d("LockSettingsService", "Dump error", e);
        }
    }

    public final void disableEscrowTokenOnNonManagedDevicesIfNeeded(int i) {
        if (this.mSpManager.hasAnyEscrowData(i)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (!DeviceConfig.getBoolean("device_policy_manager", "deprecate_usermanagerinternal_devicepolicy", true)) {
                    UserManagerInternal userManagerInternal = this.mInjector.getUserManagerInternal();
                    if (userManagerInternal.isUserManaged(i)) {
                        Slog.i("LockSettingsService", "Managed profile can have escrow token");
                        return;
                    } else if (userManagerInternal.isDeviceManaged()) {
                        Slog.i("LockSettingsService", "Corp-owned device can have escrow token");
                        return;
                    }
                } else if (this.mInjector.getDeviceStateCache().isUserOrganizationManaged(i)) {
                    Slog.i("LockSettingsService", "Organization managed users can have escrow token");
                    return;
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (!this.mInjector.getDeviceStateCache().isDeviceProvisioned()) {
                    Slog.i("LockSettingsService", "Postpone disabling escrow tokens until device is provisioned");
                } else if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                } else {
                    Slogf.m20i("LockSettingsService", "Permanently disabling support for escrow tokens on user %d", Integer.valueOf(i));
                    this.mSpManager.destroyEscrowData(i);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final void scheduleGc() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.locksettings.LockSettingsService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                LockSettingsService.lambda$scheduleGc$8();
            }
        }, 2000L);
    }

    public static /* synthetic */ void lambda$scheduleGc$8() {
        System.gc();
        System.runFinalization();
        System.gc();
    }

    /* loaded from: classes2.dex */
    public class DeviceProvisionedObserver extends ContentObserver {
        public final Uri mDeviceProvisionedUri;
        public boolean mRegistered;

        public DeviceProvisionedObserver() {
            super(null);
            this.mDeviceProvisionedUri = Settings.Global.getUriFor("device_provisioned");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.mDeviceProvisionedUri.equals(uri)) {
                updateRegistration();
                if (isProvisioned()) {
                    Slog.i("LockSettingsService", "Reporting device setup complete to IGateKeeperService");
                    reportDeviceSetupComplete();
                    clearFrpCredentialIfOwnerNotSecure();
                }
            }
        }

        public void onSystemReady() {
            if (LockPatternUtils.frpCredentialEnabled(LockSettingsService.this.mContext)) {
                updateRegistration();
            } else if (isProvisioned()) {
            } else {
                Slog.i("LockSettingsService", "FRP credential disabled, reporting device setup complete to Gatekeeper immediately");
                reportDeviceSetupComplete();
            }
        }

        public final void reportDeviceSetupComplete() {
            try {
                LockSettingsService.this.getGateKeeperService().reportDeviceSetupComplete();
            } catch (RemoteException e) {
                Slog.e("LockSettingsService", "Failure reporting to IGateKeeperService", e);
            }
        }

        public final void clearFrpCredentialIfOwnerNotSecure() {
            for (UserInfo userInfo : LockSettingsService.this.mUserManager.getUsers()) {
                if (LockPatternUtils.userOwnsFrpCredential(LockSettingsService.this.mContext, userInfo)) {
                    if (LockSettingsService.this.isUserSecure(userInfo.id)) {
                        return;
                    }
                    Slogf.m28d("LockSettingsService", "Clearing FRP credential tied to user %d", Integer.valueOf(userInfo.id));
                    LockSettingsService.this.mStorage.writePersistentDataBlock(0, userInfo.id, 0, null);
                    return;
                }
            }
        }

        public final void updateRegistration() {
            boolean z = !isProvisioned();
            if (z == this.mRegistered) {
                return;
            }
            if (z) {
                LockSettingsService.this.mContext.getContentResolver().registerContentObserver(this.mDeviceProvisionedUri, false, this);
            } else {
                LockSettingsService.this.mContext.getContentResolver().unregisterContentObserver(this);
            }
            this.mRegistered = z;
        }

        public final boolean isProvisioned() {
            return Settings.Global.getInt(LockSettingsService.this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
        }
    }

    /* loaded from: classes2.dex */
    public final class LocalService extends LockSettingsInternal {
        public LocalService() {
        }

        public void onThirdPartyAppsStarted() {
            LockSettingsService.this.onThirdPartyAppsStarted();
        }

        public void unlockUserKeyIfUnsecured(int i) {
            LockSettingsService.this.unlockUserKeyIfUnsecured(i);
        }

        public void createNewUser(int i, int i2) {
            LockSettingsService.this.createNewUser(i, i2);
        }

        public void removeUser(int i) {
            LockSettingsService.this.removeUser(i);
        }

        public long addEscrowToken(byte[] bArr, int i, LockPatternUtils.EscrowTokenStateChangeCallback escrowTokenStateChangeCallback) {
            return LockSettingsService.this.addEscrowToken(bArr, 0, i, escrowTokenStateChangeCallback);
        }

        public boolean removeEscrowToken(long j, int i) {
            return LockSettingsService.this.removeEscrowToken(j, i);
        }

        public boolean isEscrowTokenActive(long j, int i) {
            return LockSettingsService.this.isEscrowTokenActive(j, i);
        }

        public boolean setLockCredentialWithToken(LockscreenCredential lockscreenCredential, long j, byte[] bArr, int i) {
            if (!LockSettingsService.this.mHasSecureLockScreen && lockscreenCredential != null && lockscreenCredential.getType() != -1) {
                throw new UnsupportedOperationException("This operation requires secure lock screen feature.");
            }
            if (LockSettingsService.this.setLockCredentialWithToken(lockscreenCredential, j, bArr, i)) {
                LockSettingsService.this.onPostPasswordChanged(lockscreenCredential, i);
                return true;
            }
            return false;
        }

        public boolean unlockUserWithToken(long j, byte[] bArr, int i) {
            return LockSettingsService.this.unlockUserWithToken(j, bArr, i);
        }

        public PasswordMetrics getUserPasswordMetrics(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (LockSettingsService.this.isProfileWithUnifiedLock(i)) {
                    Slog.w("LockSettingsService", "Querying password metrics for unified challenge profile: " + i);
                }
                return LockSettingsService.this.getUserPasswordMetrics(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean prepareRebootEscrow() {
            if (LockSettingsService.this.mRebootEscrowManager.prepareRebootEscrow()) {
                LockSettingsService.this.mStrongAuth.requireStrongAuth(64, -1);
                return true;
            }
            return false;
        }

        public void setRebootEscrowListener(RebootEscrowListener rebootEscrowListener) {
            LockSettingsService.this.mRebootEscrowManager.setRebootEscrowListener(rebootEscrowListener);
        }

        public boolean clearRebootEscrow() {
            if (LockSettingsService.this.mRebootEscrowManager.clearRebootEscrow()) {
                LockSettingsService.this.mStrongAuth.noLongerRequireStrongAuth(64, -1);
                return true;
            }
            return false;
        }

        public int armRebootEscrow() {
            return LockSettingsService.this.mRebootEscrowManager.armRebootEscrowIfNeeded();
        }

        public void refreshStrongAuthTimeout(int i) {
            LockSettingsService.this.mStrongAuth.refreshStrongAuthTimeout(i);
        }
    }

    /* loaded from: classes2.dex */
    public class RebootEscrowCallbacks implements RebootEscrowManager.Callbacks {
        public RebootEscrowCallbacks() {
        }

        @Override // com.android.server.locksettings.RebootEscrowManager.Callbacks
        public boolean isUserSecure(int i) {
            return LockSettingsService.this.isUserSecure(i);
        }

        @Override // com.android.server.locksettings.RebootEscrowManager.Callbacks
        public void onRebootEscrowRestored(byte b, byte[] bArr, int i) {
            SyntheticPasswordManager.SyntheticPassword syntheticPassword = new SyntheticPasswordManager.SyntheticPassword(b);
            syntheticPassword.recreateDirectly(bArr);
            synchronized (LockSettingsService.this.mSpManager) {
                LockSettingsService.this.mSpManager.verifyChallenge(LockSettingsService.this.getGateKeeperService(), syntheticPassword, 0L, i);
            }
            Slogf.m20i("LockSettingsService", "Restored synthetic password for user %d using reboot escrow", Integer.valueOf(i));
            LockSettingsService lockSettingsService = LockSettingsService.this;
            lockSettingsService.onCredentialVerified(syntheticPassword, lockSettingsService.loadPasswordMetrics(syntheticPassword, i), i);
        }
    }
}
