package com.android.server.locksettings;

import android.content.Context;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.widget.RebootEscrowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class RebootEscrowManager {
    @VisibleForTesting
    public static final String REBOOT_ESCROW_ARMED_KEY = "reboot_escrow_armed_count";
    public final Callbacks mCallbacks;
    public final RebootEscrowEventLog mEventLog;
    public final Injector mInjector;
    public final Object mKeyGenerationLock;
    public final RebootEscrowKeyStoreManager mKeyStoreManager;
    public int mLoadEscrowDataErrorCode;
    public boolean mLoadEscrowDataWithRetry;
    public ConnectivityManager.NetworkCallback mNetworkCallback;
    @GuardedBy({"mKeyGenerationLock"})
    public RebootEscrowKey mPendingRebootEscrowKey;
    public RebootEscrowListener mRebootEscrowListener;
    public boolean mRebootEscrowReady;
    public boolean mRebootEscrowTimedOut;
    public boolean mRebootEscrowWanted;
    public final LockSettingsStorage mStorage;
    public final UserManager mUserManager;
    public PowerManager.WakeLock mWakeLock;

    /* loaded from: classes2.dex */
    public interface Callbacks {
        boolean isUserSecure(int i);

        void onRebootEscrowRestored(byte b, byte[] bArr, int i);
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public Context mContext;
        public final RebootEscrowKeyStoreManager mKeyStoreManager = new RebootEscrowKeyStoreManager();
        public RebootEscrowProviderInterface mRebootEscrowProvider;
        public final LockSettingsStorage mStorage;

        @VisibleForTesting
        public int getLoadEscrowTimeoutMillis() {
            return 180000;
        }

        public Injector(Context context, LockSettingsStorage lockSettingsStorage) {
            this.mContext = context;
            this.mStorage = lockSettingsStorage;
        }

        public final RebootEscrowProviderInterface createRebootEscrowProvider() {
            RebootEscrowProviderInterface rebootEscrowProviderHalImpl;
            if (serverBasedResumeOnReboot()) {
                Slog.i("RebootEscrowManager", "Using server based resume on reboot");
                rebootEscrowProviderHalImpl = new RebootEscrowProviderServerBasedImpl(this.mContext, this.mStorage);
            } else {
                Slog.i("RebootEscrowManager", "Using HAL based resume on reboot");
                rebootEscrowProviderHalImpl = new RebootEscrowProviderHalImpl();
            }
            if (rebootEscrowProviderHalImpl.hasRebootEscrowSupport()) {
                return rebootEscrowProviderHalImpl;
            }
            return null;
        }

        public void post(Handler handler, Runnable runnable) {
            handler.post(runnable);
        }

        public void postDelayed(Handler handler, Runnable runnable, long j) {
            handler.postDelayed(runnable, j);
        }

        public boolean serverBasedResumeOnReboot() {
            if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.reboot_escrow")) {
                return DeviceConfig.getBoolean("ota", "server_based_ror_enabled", false);
            }
            return true;
        }

        public boolean waitForInternet() {
            return DeviceConfig.getBoolean("ota", "wait_for_internet_ror", false);
        }

        public boolean isNetworkConnected() {
            NetworkCapabilities networkCapabilities;
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            return connectivityManager != null && (networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork())) != null && networkCapabilities.hasCapability(12) && networkCapabilities.hasCapability(16);
        }

        public boolean requestNetworkWithInternet(ConnectivityManager.NetworkCallback networkCallback) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            if (connectivityManager == null) {
                return false;
            }
            connectivityManager.requestNetwork(new NetworkRequest.Builder().addCapability(12).build(), networkCallback, getLoadEscrowTimeoutMillis());
            return true;
        }

        public void stopRequestingNetwork(ConnectivityManager.NetworkCallback networkCallback) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            if (connectivityManager == null) {
                return;
            }
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService("user");
        }

        public RebootEscrowKeyStoreManager getKeyStoreManager() {
            return this.mKeyStoreManager;
        }

        public RebootEscrowProviderInterface createRebootEscrowProviderIfNeeded() {
            if (this.mRebootEscrowProvider == null) {
                this.mRebootEscrowProvider = createRebootEscrowProvider();
            }
            return this.mRebootEscrowProvider;
        }

        public PowerManager.WakeLock getWakeLock() {
            return ((PowerManager) this.mContext.getSystemService(PowerManager.class)).newWakeLock(1, "RebootEscrowManager");
        }

        public RebootEscrowProviderInterface getRebootEscrowProvider() {
            return this.mRebootEscrowProvider;
        }

        public void clearRebootEscrowProvider() {
            this.mRebootEscrowProvider = null;
        }

        public int getBootCount() {
            return Settings.Global.getInt(this.mContext.getContentResolver(), "boot_count", 0);
        }

        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }

        public int getLoadEscrowDataRetryLimit() {
            return DeviceConfig.getInt("ota", "load_escrow_data_retry_count", 3);
        }

        public int getLoadEscrowDataRetryIntervalSeconds() {
            return DeviceConfig.getInt("ota", "load_escrow_data_retry_interval_seconds", 30);
        }

        @VisibleForTesting
        public int getWakeLockTimeoutMillis() {
            return getLoadEscrowTimeoutMillis() + 5000;
        }

        public void reportMetric(boolean z, int i, int i2, int i3, int i4, int i5, int i6) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.REBOOT_ESCROW_RECOVERY_REPORTED, z, i, i2, i3, i4, i5, i6);
        }

        public RebootEscrowEventLog getEventLog() {
            return new RebootEscrowEventLog();
        }

        public String getVbmetaDigest(boolean z) {
            if (z) {
                return SystemProperties.get("ota.other.vbmeta_digest");
            }
            return SystemProperties.get("ro.boot.vbmeta.digest");
        }
    }

    public RebootEscrowManager(Context context, Callbacks callbacks, LockSettingsStorage lockSettingsStorage) {
        this(new Injector(context, lockSettingsStorage), callbacks, lockSettingsStorage);
    }

    @VisibleForTesting
    public RebootEscrowManager(Injector injector, Callbacks callbacks, LockSettingsStorage lockSettingsStorage) {
        this.mLoadEscrowDataErrorCode = 0;
        this.mRebootEscrowTimedOut = false;
        this.mLoadEscrowDataWithRetry = false;
        this.mKeyGenerationLock = new Object();
        this.mInjector = injector;
        this.mCallbacks = callbacks;
        this.mStorage = lockSettingsStorage;
        this.mUserManager = injector.getUserManager();
        this.mEventLog = injector.getEventLog();
        this.mKeyStoreManager = injector.getKeyStoreManager();
    }

    public final void setLoadEscrowDataErrorCode(final int i, Handler handler) {
        if (this.mInjector.waitForInternet()) {
            this.mInjector.post(handler, new Runnable() { // from class: com.android.server.locksettings.RebootEscrowManager$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    RebootEscrowManager.this.lambda$setLoadEscrowDataErrorCode$0(i);
                }
            });
        } else {
            this.mLoadEscrowDataErrorCode = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLoadEscrowDataErrorCode$0(int i) {
        this.mLoadEscrowDataErrorCode = i;
    }

    public final void compareAndSetLoadEscrowDataErrorCode(int i, int i2, Handler handler) {
        if (i == this.mLoadEscrowDataErrorCode) {
            setLoadEscrowDataErrorCode(i2, handler);
        }
    }

    public final void onGetRebootEscrowKeyFailed(List<UserInfo> list, int i, Handler handler) {
        Slog.w("RebootEscrowManager", "Had reboot escrow data for users, but no key; removing escrow storage.");
        for (UserInfo userInfo : list) {
            this.mStorage.removeRebootEscrow(userInfo.id);
        }
        onEscrowRestoreComplete(false, i, handler);
    }

    public void loadRebootEscrowDataIfAvailable(final Handler handler) {
        final List<UserInfo> users = this.mUserManager.getUsers();
        final ArrayList arrayList = new ArrayList();
        for (UserInfo userInfo : users) {
            if (this.mCallbacks.isUserSecure(userInfo.id) && this.mStorage.hasRebootEscrow(userInfo.id)) {
                arrayList.add(userInfo);
            }
        }
        if (arrayList.isEmpty()) {
            Slog.i("RebootEscrowManager", "No reboot escrow data found for users, skipping loading escrow data");
            clearMetricsStorage();
            return;
        }
        PowerManager.WakeLock wakeLock = this.mInjector.getWakeLock();
        this.mWakeLock = wakeLock;
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            this.mWakeLock.acquire(this.mInjector.getWakeLockTimeoutMillis());
        }
        if (this.mInjector.waitForInternet()) {
            this.mInjector.postDelayed(handler, new Runnable() { // from class: com.android.server.locksettings.RebootEscrowManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RebootEscrowManager.this.lambda$loadRebootEscrowDataIfAvailable$1();
                }
            }, this.mInjector.getLoadEscrowTimeoutMillis());
            this.mInjector.post(handler, new Runnable() { // from class: com.android.server.locksettings.RebootEscrowManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RebootEscrowManager.this.lambda$loadRebootEscrowDataIfAvailable$2(handler, users, arrayList);
                }
            });
            return;
        }
        this.mInjector.post(handler, new Runnable() { // from class: com.android.server.locksettings.RebootEscrowManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RebootEscrowManager.this.lambda$loadRebootEscrowDataIfAvailable$3(handler, users, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRebootEscrowDataIfAvailable$1() {
        this.mRebootEscrowTimedOut = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRebootEscrowDataIfAvailable$3(Handler handler, List list, List list2) {
        lambda$scheduleLoadRebootEscrowDataOrFail$4(handler, 0, list, list2);
    }

    public void scheduleLoadRebootEscrowDataOrFail(final Handler handler, final int i, final List<UserInfo> list, final List<UserInfo> list2) {
        Objects.requireNonNull(handler);
        int loadEscrowDataRetryLimit = this.mInjector.getLoadEscrowDataRetryLimit();
        int loadEscrowDataRetryIntervalSeconds = this.mInjector.getLoadEscrowDataRetryIntervalSeconds();
        if (i < loadEscrowDataRetryLimit && !this.mRebootEscrowTimedOut) {
            Slog.i("RebootEscrowManager", "Scheduling loadRebootEscrowData retry number: " + i);
            this.mInjector.postDelayed(handler, new Runnable() { // from class: com.android.server.locksettings.RebootEscrowManager$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    RebootEscrowManager.this.lambda$scheduleLoadRebootEscrowDataOrFail$4(handler, i, list, list2);
                }
            }, (long) (loadEscrowDataRetryIntervalSeconds * 1000));
        } else if (this.mInjector.waitForInternet()) {
            if (this.mRebootEscrowTimedOut) {
                Slog.w("RebootEscrowManager", "Failed to load reboot escrow data within timeout");
                compareAndSetLoadEscrowDataErrorCode(0, 9, handler);
            } else {
                Slog.w("RebootEscrowManager", "Failed to load reboot escrow data after " + i + " attempts");
                compareAndSetLoadEscrowDataErrorCode(0, 4, handler);
            }
            onGetRebootEscrowKeyFailed(list, i, handler);
        } else {
            Slog.w("RebootEscrowManager", "Failed to load reboot escrow data after " + i + " attempts");
            if (this.mInjector.serverBasedResumeOnReboot() && !this.mInjector.isNetworkConnected()) {
                this.mLoadEscrowDataErrorCode = 8;
            } else {
                this.mLoadEscrowDataErrorCode = 4;
            }
            onGetRebootEscrowKeyFailed(list, i, handler);
        }
    }

    /* renamed from: loadRebootEscrowDataOnInternet */
    public void lambda$loadRebootEscrowDataIfAvailable$2(final Handler handler, final List<UserInfo> list, final List<UserInfo> list2) {
        if (!this.mInjector.serverBasedResumeOnReboot()) {
            lambda$scheduleLoadRebootEscrowDataOrFail$4(handler, 0, list, list2);
            return;
        }
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.server.locksettings.RebootEscrowManager.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                RebootEscrowManager.this.compareAndSetLoadEscrowDataErrorCode(8, 0, handler);
                if (RebootEscrowManager.this.mLoadEscrowDataWithRetry) {
                    return;
                }
                RebootEscrowManager.this.mLoadEscrowDataWithRetry = true;
                RebootEscrowManager.this.lambda$scheduleLoadRebootEscrowDataOrFail$4(handler, 0, list, list2);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onUnavailable() {
                Slog.w("RebootEscrowManager", "Failed to connect to network within timeout");
                RebootEscrowManager.this.compareAndSetLoadEscrowDataErrorCode(0, 8, handler);
                RebootEscrowManager.this.onGetRebootEscrowKeyFailed(list, 0, handler);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                Slog.w("RebootEscrowManager", "Network lost, still attempting to load escrow key.");
                RebootEscrowManager.this.compareAndSetLoadEscrowDataErrorCode(0, 8, handler);
            }
        };
        this.mNetworkCallback = networkCallback;
        if (this.mInjector.requestNetworkWithInternet(networkCallback)) {
            return;
        }
        lambda$scheduleLoadRebootEscrowDataOrFail$4(handler, 0, list, list2);
    }

    /* renamed from: loadRebootEscrowDataWithRetry */
    public void lambda$scheduleLoadRebootEscrowDataOrFail$4(Handler handler, int i, List<UserInfo> list, List<UserInfo> list2) {
        SecretKey keyStoreEncryptionKey = this.mKeyStoreManager.getKeyStoreEncryptionKey();
        if (keyStoreEncryptionKey == null) {
            Slog.i("RebootEscrowManager", "Failed to load the key for resume on reboot from key store.");
        }
        try {
            RebootEscrowKey andClearRebootEscrowKey = getAndClearRebootEscrowKey(keyStoreEncryptionKey, handler);
            if (andClearRebootEscrowKey == null) {
                if (this.mLoadEscrowDataErrorCode == 0) {
                    if (this.mInjector.serverBasedResumeOnReboot() != this.mStorage.getInt("reboot_escrow_key_provider", -1, 0)) {
                        setLoadEscrowDataErrorCode(6, handler);
                    } else {
                        setLoadEscrowDataErrorCode(3, handler);
                    }
                }
                onGetRebootEscrowKeyFailed(list, i + 1, handler);
                return;
            }
            this.mEventLog.addEntry(1);
            boolean z = true;
            for (UserInfo userInfo : list2) {
                z &= restoreRebootEscrowForUser(userInfo.id, andClearRebootEscrowKey, keyStoreEncryptionKey);
            }
            if (!z) {
                compareAndSetLoadEscrowDataErrorCode(0, 5, handler);
            }
            onEscrowRestoreComplete(z, i + 1, handler);
        } catch (IOException e) {
            Slog.i("RebootEscrowManager", "Failed to load escrow key, scheduling retry.", e);
            scheduleLoadRebootEscrowDataOrFail(handler, i + 1, list, list2);
        }
    }

    public final void clearMetricsStorage() {
        this.mStorage.removeKey(REBOOT_ESCROW_ARMED_KEY, 0);
        this.mStorage.removeKey("reboot_escrow_key_stored_timestamp", 0);
        this.mStorage.removeKey("reboot_escrow_key_vbmeta_digest", 0);
        this.mStorage.removeKey("reboot_escrow_key_other_vbmeta_digest", 0);
        this.mStorage.removeKey("reboot_escrow_key_provider", 0);
    }

    public final int getVbmetaDigestStatusOnRestoreComplete() {
        String vbmetaDigest = this.mInjector.getVbmetaDigest(false);
        String string = this.mStorage.getString("reboot_escrow_key_vbmeta_digest", "", 0);
        String string2 = this.mStorage.getString("reboot_escrow_key_other_vbmeta_digest", "", 0);
        if (string2.isEmpty()) {
            return vbmetaDigest.equals(string) ? 0 : 2;
        } else if (vbmetaDigest.equals(string2)) {
            return 0;
        } else {
            return vbmetaDigest.equals(string) ? 1 : 2;
        }
    }

    public final void reportMetricOnRestoreComplete(boolean z, int i, Handler handler) {
        int i2 = this.mInjector.serverBasedResumeOnReboot() ? 2 : 1;
        long j = this.mStorage.getLong("reboot_escrow_key_stored_timestamp", -1L, 0);
        long currentTimeMillis = this.mInjector.getCurrentTimeMillis();
        int i3 = (j == -1 || currentTimeMillis <= j) ? -1 : ((int) (currentTimeMillis - j)) / 1000;
        int vbmetaDigestStatusOnRestoreComplete = getVbmetaDigestStatusOnRestoreComplete();
        if (!z) {
            compareAndSetLoadEscrowDataErrorCode(0, 1, handler);
        }
        Slog.i("RebootEscrowManager", "Reporting RoR recovery metrics, success: " + z + ", service type: " + i2 + ", error code: " + this.mLoadEscrowDataErrorCode);
        this.mInjector.reportMetric(z, this.mLoadEscrowDataErrorCode, i2, i, i3, vbmetaDigestStatusOnRestoreComplete, -1);
        setLoadEscrowDataErrorCode(0, handler);
    }

    public final void onEscrowRestoreComplete(boolean z, int i, Handler handler) {
        int i2 = this.mStorage.getInt(REBOOT_ESCROW_ARMED_KEY, -1, 0);
        int bootCount = this.mInjector.getBootCount() - i2;
        if (z || (i2 != -1 && bootCount <= 5)) {
            reportMetricOnRestoreComplete(z, i, handler);
        }
        this.mKeyStoreManager.clearKeyStoreEncryptionKey();
        this.mInjector.clearRebootEscrowProvider();
        clearMetricsStorage();
        ConnectivityManager.NetworkCallback networkCallback = this.mNetworkCallback;
        if (networkCallback != null) {
            this.mInjector.stopRequestingNetwork(networkCallback);
        }
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    public final RebootEscrowKey getAndClearRebootEscrowKey(SecretKey secretKey, Handler handler) throws IOException {
        RebootEscrowProviderInterface createRebootEscrowProviderIfNeeded = this.mInjector.createRebootEscrowProviderIfNeeded();
        if (createRebootEscrowProviderIfNeeded == null) {
            Slog.w("RebootEscrowManager", "Had reboot escrow data for users, but RebootEscrowProvider is unavailable");
            setLoadEscrowDataErrorCode(2, handler);
            return null;
        } else if (createRebootEscrowProviderIfNeeded.getType() == 1 && secretKey == null) {
            setLoadEscrowDataErrorCode(7, handler);
            return null;
        } else {
            RebootEscrowKey andClearRebootEscrowKey = createRebootEscrowProviderIfNeeded.getAndClearRebootEscrowKey(secretKey);
            if (andClearRebootEscrowKey != null) {
                this.mEventLog.addEntry(4);
            }
            return andClearRebootEscrowKey;
        }
    }

    public final boolean restoreRebootEscrowForUser(int i, RebootEscrowKey rebootEscrowKey, SecretKey secretKey) {
        if (this.mStorage.hasRebootEscrow(i)) {
            try {
                byte[] readRebootEscrow = this.mStorage.readRebootEscrow(i);
                this.mStorage.removeRebootEscrow(i);
                RebootEscrowData fromEncryptedData = RebootEscrowData.fromEncryptedData(rebootEscrowKey, readRebootEscrow, secretKey);
                this.mCallbacks.onRebootEscrowRestored(fromEncryptedData.getSpVersion(), fromEncryptedData.getSyntheticPassword(), i);
                Slog.i("RebootEscrowManager", "Restored reboot escrow data for user " + i);
                this.mEventLog.addEntry(7, i);
                return true;
            } catch (IOException e) {
                Slog.w("RebootEscrowManager", "Could not load reboot escrow data for user " + i, e);
                return false;
            }
        }
        return false;
    }

    public void callToRebootEscrowIfNeeded(int i, byte b, byte[] bArr) {
        if (this.mRebootEscrowWanted) {
            if (this.mInjector.createRebootEscrowProviderIfNeeded() == null) {
                Slog.w("RebootEscrowManager", "Not storing escrow data, RebootEscrowProvider is unavailable");
                return;
            }
            RebootEscrowKey generateEscrowKeyIfNeeded = generateEscrowKeyIfNeeded();
            if (generateEscrowKeyIfNeeded == null) {
                Slog.e("RebootEscrowManager", "Could not generate escrow key");
                return;
            }
            SecretKey generateKeyStoreEncryptionKeyIfNeeded = this.mKeyStoreManager.generateKeyStoreEncryptionKeyIfNeeded();
            if (generateKeyStoreEncryptionKeyIfNeeded == null) {
                Slog.e("RebootEscrowManager", "Failed to generate encryption key from keystore.");
                return;
            }
            try {
                this.mStorage.writeRebootEscrow(i, RebootEscrowData.fromSyntheticPassword(generateEscrowKeyIfNeeded, b, bArr, generateKeyStoreEncryptionKeyIfNeeded).getBlob());
                this.mEventLog.addEntry(6, i);
                setRebootEscrowReady(true);
            } catch (IOException e) {
                setRebootEscrowReady(false);
                Slog.w("RebootEscrowManager", "Could not escrow reboot data", e);
            }
        }
    }

    public final RebootEscrowKey generateEscrowKeyIfNeeded() {
        synchronized (this.mKeyGenerationLock) {
            RebootEscrowKey rebootEscrowKey = this.mPendingRebootEscrowKey;
            if (rebootEscrowKey != null) {
                return rebootEscrowKey;
            }
            try {
                RebootEscrowKey generate = RebootEscrowKey.generate();
                this.mPendingRebootEscrowKey = generate;
                return generate;
            } catch (IOException unused) {
                Slog.w("RebootEscrowManager", "Could not generate reboot escrow key");
                return null;
            }
        }
    }

    public final void clearRebootEscrowIfNeeded() {
        this.mRebootEscrowWanted = false;
        setRebootEscrowReady(false);
        RebootEscrowProviderInterface createRebootEscrowProviderIfNeeded = this.mInjector.createRebootEscrowProviderIfNeeded();
        if (createRebootEscrowProviderIfNeeded == null) {
            Slog.w("RebootEscrowManager", "RebootEscrowProvider is unavailable for clear request");
        } else {
            createRebootEscrowProviderIfNeeded.clearRebootEscrowKey();
        }
        this.mInjector.clearRebootEscrowProvider();
        clearMetricsStorage();
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            this.mStorage.removeRebootEscrow(userInfo.id);
        }
        this.mEventLog.addEntry(3);
    }

    public int armRebootEscrowIfNeeded() {
        RebootEscrowKey rebootEscrowKey;
        if (this.mRebootEscrowReady) {
            RebootEscrowProviderInterface rebootEscrowProvider = this.mInjector.getRebootEscrowProvider();
            if (rebootEscrowProvider == null) {
                Slog.w("RebootEscrowManager", "Not storing escrow key, RebootEscrowProvider is unavailable");
                clearRebootEscrowIfNeeded();
                return 3;
            }
            boolean serverBasedResumeOnReboot = this.mInjector.serverBasedResumeOnReboot();
            int type = rebootEscrowProvider.getType();
            if (serverBasedResumeOnReboot != type) {
                Slog.w("RebootEscrowManager", "Expect reboot escrow provider " + (serverBasedResumeOnReboot ? 1 : 0) + ", but the RoR is prepared with " + type + ". Please prepare the RoR again.");
                clearRebootEscrowIfNeeded();
                return 4;
            }
            synchronized (this.mKeyGenerationLock) {
                rebootEscrowKey = this.mPendingRebootEscrowKey;
            }
            if (rebootEscrowKey == null) {
                Slog.e("RebootEscrowManager", "Escrow key is null, but escrow was marked as ready");
                clearRebootEscrowIfNeeded();
                return 5;
            }
            SecretKey keyStoreEncryptionKey = this.mKeyStoreManager.getKeyStoreEncryptionKey();
            if (keyStoreEncryptionKey == null) {
                Slog.e("RebootEscrowManager", "Failed to get encryption key from keystore.");
                clearRebootEscrowIfNeeded();
                return 6;
            } else if (rebootEscrowProvider.storeRebootEscrowKey(rebootEscrowKey, keyStoreEncryptionKey)) {
                this.mStorage.setInt(REBOOT_ESCROW_ARMED_KEY, this.mInjector.getBootCount(), 0);
                this.mStorage.setLong("reboot_escrow_key_stored_timestamp", this.mInjector.getCurrentTimeMillis(), 0);
                this.mStorage.setString("reboot_escrow_key_vbmeta_digest", this.mInjector.getVbmetaDigest(false), 0);
                this.mStorage.setString("reboot_escrow_key_other_vbmeta_digest", this.mInjector.getVbmetaDigest(true), 0);
                this.mStorage.setInt("reboot_escrow_key_provider", type, 0);
                this.mEventLog.addEntry(2);
                return 0;
            } else {
                return 7;
            }
        }
        return 2;
    }

    public final void setRebootEscrowReady(boolean z) {
        if (this.mRebootEscrowReady != z) {
            this.mRebootEscrowListener.onPreparedForReboot(z);
        }
        this.mRebootEscrowReady = z;
    }

    public boolean prepareRebootEscrow() {
        clearRebootEscrowIfNeeded();
        if (this.mInjector.createRebootEscrowProviderIfNeeded() == null) {
            Slog.w("RebootEscrowManager", "No reboot escrow provider, skipping resume on reboot preparation.");
            return false;
        }
        this.mRebootEscrowWanted = true;
        this.mEventLog.addEntry(5);
        return true;
    }

    public boolean clearRebootEscrow() {
        clearRebootEscrowIfNeeded();
        return true;
    }

    public void setRebootEscrowListener(RebootEscrowListener rebootEscrowListener) {
        this.mRebootEscrowListener = rebootEscrowListener;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class RebootEscrowEvent {
        public final int mEventId;
        public final long mTimestamp;
        public final Integer mUserId;
        public final long mWallTime;

        public RebootEscrowEvent(int i) {
            this(i, null);
        }

        public RebootEscrowEvent(int i, Integer num) {
            this.mEventId = i;
            this.mUserId = num;
            this.mTimestamp = SystemClock.uptimeMillis();
            this.mWallTime = System.currentTimeMillis();
        }

        public String getEventDescription() {
            switch (this.mEventId) {
                case 1:
                    return "Found escrow data";
                case 2:
                    return "Set armed status";
                case 3:
                    return "Cleared request for LSKF";
                case 4:
                    return "Retrieved stored KEK";
                case 5:
                    return "Requested LSKF";
                case 6:
                    return "Stored LSKF for user";
                case 7:
                    return "Retrieved LSKF for user";
                default:
                    return "Unknown event ID " + this.mEventId;
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class RebootEscrowEventLog {
        public RebootEscrowEvent[] mEntries = new RebootEscrowEvent[16];
        public int mNextIndex = 0;

        public void addEntry(int i) {
            addEntryInternal(new RebootEscrowEvent(i));
        }

        public void addEntry(int i, int i2) {
            addEntryInternal(new RebootEscrowEvent(i, Integer.valueOf(i2)));
        }

        public final void addEntryInternal(RebootEscrowEvent rebootEscrowEvent) {
            int i = this.mNextIndex;
            RebootEscrowEvent[] rebootEscrowEventArr = this.mEntries;
            rebootEscrowEventArr[i] = rebootEscrowEvent;
            this.mNextIndex = (i + 1) % rebootEscrowEventArr.length;
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            int i = 0;
            while (true) {
                RebootEscrowEvent[] rebootEscrowEventArr = this.mEntries;
                if (i >= rebootEscrowEventArr.length) {
                    return;
                }
                RebootEscrowEvent rebootEscrowEvent = rebootEscrowEventArr[(this.mNextIndex + i) % rebootEscrowEventArr.length];
                if (rebootEscrowEvent != null) {
                    indentingPrintWriter.print("Event #");
                    indentingPrintWriter.println(i);
                    indentingPrintWriter.println(" time=" + simpleDateFormat.format(new Date(rebootEscrowEvent.mWallTime)) + " (timestamp=" + rebootEscrowEvent.mTimestamp + ")");
                    indentingPrintWriter.print(" event=");
                    indentingPrintWriter.println(rebootEscrowEvent.getEventDescription());
                    if (rebootEscrowEvent.mUserId != null) {
                        indentingPrintWriter.print(" user=");
                        indentingPrintWriter.println(rebootEscrowEvent.mUserId);
                    }
                }
                i++;
            }
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        boolean z;
        indentingPrintWriter.print("mRebootEscrowWanted=");
        indentingPrintWriter.println(this.mRebootEscrowWanted);
        indentingPrintWriter.print("mRebootEscrowReady=");
        indentingPrintWriter.println(this.mRebootEscrowReady);
        indentingPrintWriter.print("mRebootEscrowListener=");
        indentingPrintWriter.println(this.mRebootEscrowListener);
        indentingPrintWriter.print("mLoadEscrowDataErrorCode=");
        indentingPrintWriter.println(this.mLoadEscrowDataErrorCode);
        synchronized (this.mKeyGenerationLock) {
            z = this.mPendingRebootEscrowKey != null;
        }
        indentingPrintWriter.print("mPendingRebootEscrowKey is ");
        indentingPrintWriter.println(z ? "set" : "not set");
        RebootEscrowProviderInterface rebootEscrowProvider = this.mInjector.getRebootEscrowProvider();
        String valueOf = rebootEscrowProvider == null ? "null" : String.valueOf(rebootEscrowProvider.getType());
        indentingPrintWriter.print("RebootEscrowProvider type is " + valueOf);
        indentingPrintWriter.println();
        indentingPrintWriter.println("Event log:");
        indentingPrintWriter.increaseIndent();
        this.mEventLog.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
    }
}
