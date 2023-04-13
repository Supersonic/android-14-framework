package com.android.internal.widget;

import android.app.PropertyInvalidatedCache;
import android.app.RemoteLockscreenValidationResult;
import android.app.StartLockscreenValidationRequest;
import android.app.admin.DevicePolicyManager;
import android.app.admin.PasswordMetrics;
import android.app.trust.IStrongAuthTracker;
import android.app.trust.TrustManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.UserInfo;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.p008os.storage.StorageManager;
import android.provider.DeviceConfig;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.android.internal.C4057R;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.server.LocalServices;
import com.google.android.collect.Lists;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes5.dex */
public class LockPatternUtils {
    private static final String AUTO_PIN_CONFIRM = "lockscreen.auto_pin_confirm";
    private static final String CREDENTIAL_TYPE_API = "getCredentialType";
    public static final int CREDENTIAL_TYPE_NONE = -1;
    public static final int CREDENTIAL_TYPE_PASSWORD = 4;
    public static final int CREDENTIAL_TYPE_PASSWORD_OR_PIN = 2;
    public static final int CREDENTIAL_TYPE_PATTERN = 1;
    public static final int CREDENTIAL_TYPE_PIN = 3;
    public static final String CURRENT_LSKF_BASED_PROTECTOR_ID_KEY = "sp-handle";
    public static final String DISABLE_LOCKSCREEN_KEY = "lockscreen.disabled";
    private static final String ENABLED_TRUST_AGENTS = "lockscreen.enabledtrustagents";
    public static final byte[] ENCRYPTED_REMOTE_CREDENTIALS_HEADER = "encrypted_remote_credentials".getBytes(StandardCharsets.UTF_8);
    public static final int FAILED_ATTEMPTS_BEFORE_WIPE_GRACE = 5;
    public static final long FAILED_ATTEMPT_COUNTDOWN_INTERVAL_MS = 1000;
    public static final String FLAG_ENABLE_AUTO_PIN_CONFIRMATION = "AutoPinConfirmation__enable_auto_pin_confirmation";
    private static final boolean FRP_CREDENTIAL_ENABLED = true;
    private static final String IS_TRUST_USUALLY_MANAGED = "lockscreen.istrustusuallymanaged";
    private static final String KNOWN_TRUST_AGENTS = "lockscreen.knowntrustagents";
    public static final String LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS = "lockscreen.power_button_instantly_locks";
    @Deprecated
    public static final String LOCKSCREEN_WIDGETS_ENABLED = "lockscreen.widgets_enabled";
    public static final String LOCK_PASSWORD_SALT_KEY = "lockscreen.password_salt";
    private static final String LOCK_PIN_ENHANCED_PRIVACY = "pin_enhanced_privacy";
    private static final String LOCK_SCREEN_DEVICE_OWNER_INFO = "lockscreen.device_owner_info";
    private static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
    private static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
    public static final int MIN_LOCK_PASSWORD_SIZE = 4;
    public static final int MIN_LOCK_PATTERN_SIZE = 4;
    public static final int MIN_PATTERN_REGISTER_FAIL = 4;
    public static final String PASSWORD_HISTORY_DELIMITER = ",";
    public static final String PASSWORD_HISTORY_KEY = "lockscreen.passwordhistory";
    @Deprecated
    public static final String PASSWORD_TYPE_ALTERNATE_KEY = "lockscreen.password_type_alternate";
    public static final String PASSWORD_TYPE_KEY = "lockscreen.password_type";
    public static final String PATTERN_EVER_CHOSEN_KEY = "lockscreen.patterneverchosen";
    public static final String PIN_LENGTH = "lockscreen.pin_length";
    private static final String TAG = "LockPatternUtils";
    public static final int USER_FRP = -9999;
    public static final int VERIFY_FLAG_REQUEST_GK_PW_HANDLE = 1;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final PropertyInvalidatedCache<Integer, Integer> mCredentialTypeCache;
    private final PropertyInvalidatedCache.QueryHandler<Integer, Integer> mCredentialTypeQuery;
    private DevicePolicyManager mDevicePolicyManager;
    private final Handler mHandler;
    private Boolean mHasSecureLockScreen;
    private ILockSettings mLockSettingsService;
    private UserManager mUserManager;
    private final SparseLongArray mLockoutDeadlines = new SparseLongArray();
    private HashMap<UserHandle, UserManager> mUserManagerCache = new HashMap<>();

    /* loaded from: classes5.dex */
    public interface CheckCredentialProgressCallback {
        void onEarlyMatched();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface CredentialType {
    }

    /* loaded from: classes5.dex */
    public interface EscrowTokenStateChangeCallback {
        void onEscrowTokenActivated(long j, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface VerifyFlag {
    }

    public static String credentialTypeToString(int credentialType) {
        switch (credentialType) {
            case -1:
                return KeyProperties.DIGEST_NONE;
            case 0:
            case 2:
            default:
                return "UNKNOWN_" + credentialType;
            case 1:
                return "PATTERN";
            case 3:
                return "PIN";
            case 4:
                return "PASSWORD";
        }
    }

    public boolean isTrustUsuallyManaged(int userId) {
        if (!(this.mLockSettingsService instanceof ILockSettings.Stub)) {
            throw new IllegalStateException("May only be called by TrustManagerService. Use TrustManager.isTrustUsuallyManaged()");
        }
        try {
            return getLockSettings().getBoolean(IS_TRUST_USUALLY_MANAGED, false, userId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setTrustUsuallyManaged(boolean managed, int userId) {
        try {
            getLockSettings().setBoolean(IS_TRUST_USUALLY_MANAGED, managed, userId);
        } catch (RemoteException e) {
        }
    }

    public void userPresent(int userId) {
        try {
            getLockSettings().userPresent(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes5.dex */
    public static final class RequestThrottledException extends Exception {
        private int mTimeoutMs;

        public RequestThrottledException(int timeoutMs) {
            this.mTimeoutMs = timeoutMs;
        }

        public int getTimeoutMs() {
            return this.mTimeoutMs;
        }
    }

    public DevicePolicyManager getDevicePolicyManager() {
        if (this.mDevicePolicyManager == null) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
            this.mDevicePolicyManager = devicePolicyManager;
            if (devicePolicyManager == null) {
                Log.m109e(TAG, "Can't get DevicePolicyManagerService: is it running?", new IllegalStateException("Stack trace:"));
            }
        }
        return this.mDevicePolicyManager;
    }

    private UserManager getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = UserManager.get(this.mContext);
        }
        return this.mUserManager;
    }

    private UserManager getUserManager(int userId) {
        UserHandle userHandle = UserHandle.m145of(userId);
        if (this.mUserManagerCache.containsKey(userHandle)) {
            return this.mUserManagerCache.get(userHandle);
        }
        try {
            Context userContext = this.mContext.createPackageContextAsUser("system", 0, userHandle);
            UserManager userManager = (UserManager) userContext.getSystemService(UserManager.class);
            this.mUserManagerCache.put(userHandle, userManager);
            return userManager;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to create context for user " + userHandle, e);
        }
    }

    private TrustManager getTrustManager() {
        TrustManager trust = (TrustManager) this.mContext.getSystemService(Context.TRUST_SERVICE);
        if (trust == null) {
            Log.m109e(TAG, "Can't get TrustManagerService: is it running?", new IllegalStateException("Stack trace:"));
        }
        return trust;
    }

    public LockPatternUtils(Context context) {
        PropertyInvalidatedCache.QueryHandler<Integer, Integer> queryHandler = new PropertyInvalidatedCache.QueryHandler<Integer, Integer>() { // from class: com.android.internal.widget.LockPatternUtils.1
            @Override // android.app.PropertyInvalidatedCache.QueryHandler
            public Integer apply(Integer userHandle) {
                try {
                    return Integer.valueOf(LockPatternUtils.this.getLockSettings().getCredentialType(userHandle.intValue()));
                } catch (RemoteException re) {
                    Log.m109e(LockPatternUtils.TAG, "failed to get credential type", re);
                    return -1;
                }
            }

            @Override // android.app.PropertyInvalidatedCache.QueryHandler
            public boolean shouldBypassCache(Integer userHandle) {
                return userHandle.intValue() == -9999;
            }
        };
        this.mCredentialTypeQuery = queryHandler;
        this.mCredentialTypeCache = new PropertyInvalidatedCache<>(4, "system_server", CREDENTIAL_TYPE_API, CREDENTIAL_TYPE_API, queryHandler);
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        Looper looper = Looper.myLooper();
        this.mHandler = looper != null ? new Handler(looper) : null;
    }

    public ILockSettings getLockSettings() {
        if (this.mLockSettingsService == null) {
            ILockSettings service = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
            this.mLockSettingsService = service;
        }
        ILockSettings service2 = this.mLockSettingsService;
        return service2;
    }

    public int getRequestedMinimumPasswordLength(int userId) {
        return getDevicePolicyManager().getPasswordMinimumLength(null, userId);
    }

    public int getMaximumPasswordLength(int quality) {
        return getDevicePolicyManager().getPasswordMaximumLength(quality);
    }

    public PasswordMetrics getRequestedPasswordMetrics(int userId) {
        return getRequestedPasswordMetrics(userId, false);
    }

    public PasswordMetrics getRequestedPasswordMetrics(int userId, boolean deviceWideOnly) {
        return getDevicePolicyManager().getPasswordMinimumMetrics(userId, deviceWideOnly);
    }

    private int getRequestedPasswordHistoryLength(int userId) {
        return getDevicePolicyManager().getPasswordHistoryLength(null, userId);
    }

    public int getRequestedPasswordComplexity(int userId) {
        return getRequestedPasswordComplexity(userId, false);
    }

    public int getRequestedPasswordComplexity(int userId, boolean deviceWideOnly) {
        return getDevicePolicyManager().getAggregatedPasswordComplexityForUser(userId, deviceWideOnly);
    }

    public void reportFailedPasswordAttempt(int userId) {
        if (userId == -9999 && frpCredentialEnabled(this.mContext)) {
            return;
        }
        getDevicePolicyManager().reportFailedPasswordAttempt(userId);
        getTrustManager().reportUnlockAttempt(false, userId);
    }

    public void reportSuccessfulPasswordAttempt(int userId) {
        if (userId == -9999 && frpCredentialEnabled(this.mContext)) {
            return;
        }
        getDevicePolicyManager().reportSuccessfulPasswordAttempt(userId);
        getTrustManager().reportUnlockAttempt(true, userId);
    }

    public void reportPasswordLockout(int timeoutMs, int userId) {
        if (userId == -9999 && frpCredentialEnabled(this.mContext)) {
            return;
        }
        getTrustManager().reportUnlockLockout(timeoutMs, userId);
    }

    public int getCurrentFailedPasswordAttempts(int userId) {
        if (userId == -9999 && frpCredentialEnabled(this.mContext)) {
            return 0;
        }
        return getDevicePolicyManager().getCurrentFailedPasswordAttempts(userId);
    }

    public int getMaximumFailedPasswordsForWipe(int userId) {
        if (userId == -9999 && frpCredentialEnabled(this.mContext)) {
            return 0;
        }
        return getDevicePolicyManager().getMaximumFailedPasswordsForWipe(null, userId);
    }

    public VerifyCredentialResponse verifyCredential(LockscreenCredential credential, int userId, int flags) {
        throwIfCalledOnMainThread();
        try {
            VerifyCredentialResponse response = getLockSettings().verifyCredential(credential, userId, flags);
            if (response == null) {
                return VerifyCredentialResponse.ERROR;
            }
            return response;
        } catch (RemoteException re) {
            Log.m109e(TAG, "failed to verify credential", re);
            return VerifyCredentialResponse.ERROR;
        }
    }

    public VerifyCredentialResponse verifyGatekeeperPasswordHandle(long gatekeeperPasswordHandle, long challenge, int userId) {
        try {
            VerifyCredentialResponse response = getLockSettings().verifyGatekeeperPasswordHandle(gatekeeperPasswordHandle, challenge, userId);
            if (response == null) {
                return VerifyCredentialResponse.ERROR;
            }
            return response;
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to verify gatekeeper password", e);
            return VerifyCredentialResponse.ERROR;
        }
    }

    public void removeGatekeeperPasswordHandle(long gatekeeperPasswordHandle) {
        try {
            getLockSettings().removeGatekeeperPasswordHandle(gatekeeperPasswordHandle);
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to remove gatekeeper password handle", e);
        }
    }

    public boolean checkCredential(LockscreenCredential credential, int userId, CheckCredentialProgressCallback progressCallback) throws RequestThrottledException {
        throwIfCalledOnMainThread();
        try {
            VerifyCredentialResponse response = getLockSettings().checkCredential(credential, userId, wrapCallback(progressCallback));
            if (response == null) {
                return false;
            }
            if (response.getResponseCode() == 0) {
                return true;
            }
            if (response.getResponseCode() != 1) {
                return false;
            }
            throw new RequestThrottledException(response.getTimeout());
        } catch (RemoteException re) {
            Log.m109e(TAG, "failed to check credential", re);
            return false;
        }
    }

    public VerifyCredentialResponse verifyTiedProfileChallenge(LockscreenCredential credential, int userId, int flags) {
        throwIfCalledOnMainThread();
        try {
            VerifyCredentialResponse response = getLockSettings().verifyTiedProfileChallenge(credential, userId, flags);
            if (response == null) {
                return VerifyCredentialResponse.ERROR;
            }
            return response;
        } catch (RemoteException re) {
            Log.m109e(TAG, "failed to verify tied profile credential", re);
            return VerifyCredentialResponse.ERROR;
        }
    }

    public byte[] getPasswordHistoryHashFactor(LockscreenCredential currentPassword, int userId) {
        try {
            return getLockSettings().getHashFactor(currentPassword, userId);
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to get hash factor", e);
            return null;
        }
    }

    public boolean checkPasswordHistory(byte[] passwordToCheck, byte[] hashFactor, int userId) {
        int passwordHistoryLength;
        if (passwordToCheck == null || passwordToCheck.length == 0) {
            Log.m110e(TAG, "checkPasswordHistory: empty password");
            return false;
        }
        String passwordHistory = getString(PASSWORD_HISTORY_KEY, userId);
        if (TextUtils.isEmpty(passwordHistory) || (passwordHistoryLength = getRequestedPasswordHistoryLength(userId)) == 0) {
            return false;
        }
        byte[] salt = getSalt(userId).getBytes();
        String legacyHash = LockscreenCredential.legacyPasswordToHash(passwordToCheck, salt);
        String passwordHash = LockscreenCredential.passwordToHistoryHash(passwordToCheck, salt, hashFactor);
        String[] history = passwordHistory.split(",");
        for (int i = 0; i < Math.min(passwordHistoryLength, history.length); i++) {
            if (history[i].equals(legacyHash) || history[i].equals(passwordHash)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPatternEverChosen(int userId) {
        return getBoolean(PATTERN_EVER_CHOSEN_KEY, false, userId);
    }

    public void setPinLength(int userId, long val) {
        setLong(PIN_LENGTH, val, userId);
    }

    public long getPinLength(int userId) {
        return getLong(PIN_LENGTH, -1L, userId);
    }

    public void reportPatternWasChosen(int userId) {
        setBoolean(PATTERN_EVER_CHOSEN_KEY, true, userId);
    }

    public int getActivePasswordQuality(int userId) {
        return getKeyguardStoredPasswordQuality(userId);
    }

    public void resetKeyStore(int userId) {
        try {
            getLockSettings().resetKeyStore(userId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Couldn't reset keystore " + e);
        }
    }

    public void setLockScreenDisabled(boolean disable, int userId) {
        setBoolean("lockscreen.disabled", disable, userId);
    }

    public boolean isLockScreenDisabled(int userId) {
        if (isSecure(userId)) {
            return false;
        }
        boolean disabledByDefault = this.mContext.getResources().getBoolean(C4057R.bool.config_disableLockscreenByDefault);
        UserInfo userInfo = getUserManager().getUserInfo(userId);
        boolean isDemoUser = UserManager.isDeviceInDemoMode(this.mContext) && userInfo != null && userInfo.isDemo();
        return getBoolean("lockscreen.disabled", false, userId) || disabledByDefault || isDemoUser;
    }

    public void setAutoPinConfirm(boolean enabled, int userId) {
        setBoolean(AUTO_PIN_CONFIRM, enabled, userId);
    }

    public boolean isAutoPinConfirmEnabled(int userId) {
        return getBoolean(AUTO_PIN_CONFIRM, false, userId);
    }

    public static boolean isAutoPinConfirmFeatureAvailable() {
        return DeviceConfig.getBoolean("auto_pin_confirmation", FLAG_ENABLE_AUTO_PIN_CONFIRMATION, false);
    }

    public static boolean isQualityAlphabeticPassword(int quality) {
        return quality >= 262144;
    }

    public static boolean isQualityNumericPin(int quality) {
        return quality == 131072 || quality == 196608;
    }

    public static int credentialTypeToPasswordQuality(int credentialType) {
        switch (credentialType) {
            case -1:
                return 0;
            case 0:
            case 2:
            default:
                throw new IllegalStateException("Unknown type: " + credentialType);
            case 1:
                return 65536;
            case 3:
                return 131072;
            case 4:
                return 262144;
        }
    }

    public boolean setLockCredential(LockscreenCredential newCredential, LockscreenCredential savedCredential, int userHandle) {
        if (!hasSecureLockScreen() && newCredential.getType() != -1) {
            throw new UnsupportedOperationException("This operation requires the lock screen feature.");
        }
        newCredential.checkLength();
        try {
            if (!getLockSettings().setLockCredential(newCredential, savedCredential, userHandle)) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to save lock password", e);
        }
    }

    public void setOwnerInfo(String info, int userId) {
        setString("lock_screen_owner_info", info, userId);
    }

    public void setOwnerInfoEnabled(boolean enabled, int userId) {
        setBoolean("lock_screen_owner_info_enabled", enabled, userId);
    }

    public String getOwnerInfo(int userId) {
        return getString("lock_screen_owner_info", userId);
    }

    public boolean isOwnerInfoEnabled(int userId) {
        return getBoolean("lock_screen_owner_info_enabled", false, userId);
    }

    public void setDeviceOwnerInfo(String info) {
        if (info != null && info.isEmpty()) {
            info = null;
        }
        setString(LOCK_SCREEN_DEVICE_OWNER_INFO, info, 0);
    }

    public String getDeviceOwnerInfo() {
        return getString(LOCK_SCREEN_DEVICE_OWNER_INFO, 0);
    }

    public boolean isDeviceOwnerInfoEnabled() {
        return getDeviceOwnerInfo() != null;
    }

    public static boolean isDeviceEncryptionEnabled() {
        return StorageManager.isEncrypted();
    }

    public static boolean isFileEncryptionEnabled() {
        return StorageManager.isFileEncrypted();
    }

    @Deprecated
    public int getKeyguardStoredPasswordQuality(int userHandle) {
        return credentialTypeToPasswordQuality(getCredentialTypeForUser(userHandle));
    }

    public void setSeparateProfileChallengeEnabled(int userHandle, boolean enabled, LockscreenCredential profilePassword) {
        if (!isCredentialSharableWithParent(userHandle)) {
            return;
        }
        try {
            getLockSettings().setSeparateProfileChallengeEnabled(userHandle, enabled, profilePassword);
            reportEnabledTrustAgentsChanged(userHandle);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Couldn't update work profile challenge enabled");
        }
    }

    public boolean isSeparateProfileChallengeEnabled(int userHandle) {
        return isCredentialSharableWithParent(userHandle) && hasSeparateChallenge(userHandle);
    }

    public boolean isManagedProfileWithUnifiedChallenge(int userHandle) {
        return isManagedProfile(userHandle) && !hasSeparateChallenge(userHandle);
    }

    private boolean hasSeparateChallenge(int userHandle) {
        try {
            return getLockSettings().getSeparateProfileChallengeEnabled(userHandle);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Couldn't get separate profile challenge enabled");
            return false;
        }
    }

    private boolean isManagedProfile(int userHandle) {
        UserInfo info = getUserManager().getUserInfo(userHandle);
        return info != null && info.isManagedProfile();
    }

    private boolean isCredentialSharableWithParent(int userHandle) {
        return getUserManager(userHandle).isCredentialSharableWithParent();
    }

    public static List<LockPatternView.Cell> byteArrayToPattern(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        List<LockPatternView.Cell> result = Lists.newArrayList();
        for (byte b : bytes) {
            byte b2 = (byte) (b - 49);
            result.add(LockPatternView.Cell.m15of(b2 / 3, b2 % 3));
        }
        return result;
    }

    public static byte[] patternToByteArray(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return new byte[0];
        }
        int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) ((cell.getRow() * 3) + cell.getColumn() + 49);
        }
        return res;
    }

    private String getSalt(int userId) {
        long salt = getLong(LOCK_PASSWORD_SALT_KEY, 0L, userId);
        if (salt == 0) {
            try {
                salt = SecureRandom.getInstance("SHA1PRNG").nextLong();
                setLong(LOCK_PASSWORD_SALT_KEY, salt, userId);
                Log.m106v(TAG, "Initialized lock password salt for user: " + userId);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Couldn't get SecureRandom number", e);
            }
        }
        return Long.toHexString(salt);
    }

    public static final void invalidateCredentialTypeCache() {
        PropertyInvalidatedCache.invalidateCache("system_server", CREDENTIAL_TYPE_API);
    }

    public int getCredentialTypeForUser(int userHandle) {
        return this.mCredentialTypeCache.query(Integer.valueOf(userHandle)).intValue();
    }

    public boolean isSecure(int userId) {
        int type = getCredentialTypeForUser(userId);
        return type != -1;
    }

    public boolean isLockPasswordEnabled(int userId) {
        int type = getCredentialTypeForUser(userId);
        return type == 4 || type == 3;
    }

    public boolean isLockPatternEnabled(int userId) {
        int type = getCredentialTypeForUser(userId);
        return type == 1;
    }

    public boolean isVisiblePatternEnabled(int userId) {
        return getBoolean("lock_pattern_visible_pattern", false, userId);
    }

    public void setVisiblePatternEnabled(boolean enabled, int userId) {
        setBoolean("lock_pattern_visible_pattern", enabled, userId);
    }

    public boolean isVisiblePatternEverChosen(int userId) {
        return getString("lock_pattern_visible_pattern", userId) != null;
    }

    public boolean isPinEnhancedPrivacyEnabled(int userId) {
        return getBoolean(LOCK_PIN_ENHANCED_PRIVACY, false, userId);
    }

    public void setPinEnhancedPrivacyEnabled(boolean enabled, int userId) {
        setBoolean(LOCK_PIN_ENHANCED_PRIVACY, enabled, userId);
    }

    public boolean isPinEnhancedPrivacyEverChosen(int userId) {
        return getString(LOCK_PIN_ENHANCED_PRIVACY, userId) != null;
    }

    public void setVisiblePasswordEnabled(boolean enabled, int userId) {
    }

    public long setLockoutAttemptDeadline(int userId, int timeoutMs) {
        long deadline = SystemClock.elapsedRealtime() + timeoutMs;
        if (userId == -9999) {
            return deadline;
        }
        this.mLockoutDeadlines.put(userId, deadline);
        return deadline;
    }

    public long getLockoutAttemptDeadline(int userId) {
        long deadline = this.mLockoutDeadlines.get(userId, 0L);
        long now = SystemClock.elapsedRealtime();
        if (deadline < now && deadline != 0) {
            this.mLockoutDeadlines.put(userId, 0L);
            return 0L;
        }
        return deadline;
    }

    private boolean getBoolean(String secureSettingKey, boolean defaultValue, int userId) {
        try {
            return getLockSettings().getBoolean(secureSettingKey, defaultValue, userId);
        } catch (RemoteException e) {
            return defaultValue;
        }
    }

    private void setBoolean(String secureSettingKey, boolean enabled, int userId) {
        try {
            getLockSettings().setBoolean(secureSettingKey, enabled, userId);
        } catch (RemoteException re) {
            Log.m110e(TAG, "Couldn't write boolean " + secureSettingKey + re);
        }
    }

    private long getLong(String secureSettingKey, long defaultValue, int userHandle) {
        try {
            return getLockSettings().getLong(secureSettingKey, defaultValue, userHandle);
        } catch (RemoteException e) {
            return defaultValue;
        }
    }

    private void setLong(String secureSettingKey, long value, int userHandle) {
        try {
            getLockSettings().setLong(secureSettingKey, value, userHandle);
        } catch (RemoteException re) {
            Log.m110e(TAG, "Couldn't write long " + secureSettingKey + re);
        }
    }

    private String getString(String secureSettingKey, int userHandle) {
        try {
            return getLockSettings().getString(secureSettingKey, null, userHandle);
        } catch (RemoteException e) {
            return null;
        }
    }

    private void setString(String secureSettingKey, String value, int userHandle) {
        try {
            getLockSettings().setString(secureSettingKey, value, userHandle);
        } catch (RemoteException re) {
            Log.m110e(TAG, "Couldn't write string " + secureSettingKey + re);
        }
    }

    public void setPowerButtonInstantlyLocks(boolean enabled, int userId) {
        setBoolean(LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, enabled, userId);
    }

    public boolean getPowerButtonInstantlyLocks(int userId) {
        return getBoolean(LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, true, userId);
    }

    public boolean isPowerButtonInstantlyLocksEverChosen(int userId) {
        return getString(LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, userId) != null;
    }

    public void setEnabledTrustAgents(Collection<ComponentName> activeTrustAgents, int userId) {
        setString(ENABLED_TRUST_AGENTS, serializeTrustAgents(activeTrustAgents), userId);
        getTrustManager().reportEnabledTrustAgentsChanged(userId);
    }

    public List<ComponentName> getEnabledTrustAgents(int userId) {
        return deserializeTrustAgents(getString(ENABLED_TRUST_AGENTS, userId));
    }

    public void setKnownTrustAgents(Collection<ComponentName> knownTrustAgents, int userId) {
        setString(KNOWN_TRUST_AGENTS, serializeTrustAgents(knownTrustAgents), userId);
    }

    public List<ComponentName> getKnownTrustAgents(int userId) {
        return deserializeTrustAgents(getString(KNOWN_TRUST_AGENTS, userId));
    }

    private String serializeTrustAgents(Collection<ComponentName> trustAgents) {
        StringBuilder sb = new StringBuilder();
        for (ComponentName cn : trustAgents) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(cn.flattenToShortString());
        }
        return sb.toString();
    }

    private List<ComponentName> deserializeTrustAgents(String serializedTrustAgents) {
        if (TextUtils.isEmpty(serializedTrustAgents)) {
            return new ArrayList();
        }
        String[] split = serializedTrustAgents.split(",");
        ArrayList<ComponentName> trustAgents = new ArrayList<>(split.length);
        for (String s : split) {
            if (!TextUtils.isEmpty(s)) {
                trustAgents.add(ComponentName.unflattenFromString(s));
            }
        }
        return trustAgents;
    }

    public void requireCredentialEntry(int userId) {
        requireStrongAuth(4, userId);
    }

    public void requireStrongAuth(int strongAuthReason, int userId) {
        try {
            getLockSettings().requireStrongAuth(strongAuthReason, userId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Error while requesting strong auth: " + e);
        }
    }

    private void reportEnabledTrustAgentsChanged(int userHandle) {
        getTrustManager().reportEnabledTrustAgentsChanged(userHandle);
    }

    private void throwIfCalledOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalStateException("should not be called from the main thread.");
        }
    }

    public void registerStrongAuthTracker(StrongAuthTracker strongAuthTracker) {
        try {
            getLockSettings().registerStrongAuthTracker(strongAuthTracker.getStub());
        } catch (RemoteException e) {
            throw new RuntimeException("Could not register StrongAuthTracker");
        }
    }

    public void unregisterStrongAuthTracker(StrongAuthTracker strongAuthTracker) {
        try {
            getLockSettings().unregisterStrongAuthTracker(strongAuthTracker.getStub());
        } catch (RemoteException e) {
            Log.m109e(TAG, "Could not unregister StrongAuthTracker", e);
        }
    }

    public boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) {
        try {
            return getLockSettings().registerWeakEscrowTokenRemovedListener(listener);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not register WeakEscrowTokenRemovedListener.");
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) {
        try {
            return getLockSettings().unregisterWeakEscrowTokenRemovedListener(listener);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not register WeakEscrowTokenRemovedListener.");
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportSuccessfulBiometricUnlock(boolean isStrongBiometric, int userId) {
        try {
            getLockSettings().reportSuccessfulBiometricUnlock(isStrongBiometric, userId);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Could not report successful biometric unlock", e);
        }
    }

    public void scheduleNonStrongBiometricIdleTimeout(int userId) {
        try {
            getLockSettings().scheduleNonStrongBiometricIdleTimeout(userId);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Could not schedule non-strong biometric idle timeout", e);
        }
    }

    public int getStrongAuthForUser(int userId) {
        try {
            return getLockSettings().getStrongAuthForUser(userId);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Could not get StrongAuth", e);
            return StrongAuthTracker.getDefaultFlags(this.mContext);
        }
    }

    public boolean isCredentialsDisabledForUser(int userId) {
        return getDevicePolicyManager().getPasswordQuality(null, userId) == 524288;
    }

    public boolean isTrustAllowedForUser(int userId) {
        return getStrongAuthForUser(userId) == 0;
    }

    public boolean isBiometricAllowedForUser(int userId) {
        return (getStrongAuthForUser(userId) & (-261)) == 0;
    }

    public boolean isUserInLockdown(int userId) {
        return getStrongAuthForUser(userId) == 32;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class WrappedCallback extends ICheckCredentialProgressCallback.Stub {
        private CheckCredentialProgressCallback mCallback;
        private Handler mHandler;

        WrappedCallback(Handler handler, CheckCredentialProgressCallback callback) {
            this.mHandler = handler;
            this.mCallback = callback;
        }

        @Override // com.android.internal.widget.ICheckCredentialProgressCallback
        public void onCredentialVerified() throws RemoteException {
            if (this.mHandler == null) {
                Log.m110e(LockPatternUtils.TAG, "Handler is null during callback");
            }
            this.mHandler.post(new Runnable() { // from class: com.android.internal.widget.LockPatternUtils$WrappedCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LockPatternUtils.WrappedCallback.this.lambda$onCredentialVerified$0();
                }
            });
            this.mHandler = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCredentialVerified$0() {
            this.mCallback.onEarlyMatched();
            this.mCallback = null;
        }
    }

    private ICheckCredentialProgressCallback wrapCallback(CheckCredentialProgressCallback callback) {
        if (callback == null) {
            return null;
        }
        if (this.mHandler == null) {
            throw new IllegalStateException("Must construct LockPatternUtils on a looper thread to use progress callbacks.");
        }
        return new WrappedCallback(this.mHandler, callback);
    }

    private LockSettingsInternal getLockSettingsInternal() {
        LockSettingsInternal service = (LockSettingsInternal) LocalServices.getService(LockSettingsInternal.class);
        if (service == null) {
            throw new SecurityException("Only available to system server itself");
        }
        return service;
    }

    public long addEscrowToken(byte[] token, int userId, EscrowTokenStateChangeCallback callback) {
        return getLockSettingsInternal().addEscrowToken(token, userId, callback);
    }

    public long addWeakEscrowToken(byte[] token, int userId, IWeakEscrowTokenActivatedListener callback) {
        try {
            return getLockSettings().addWeakEscrowToken(token, userId, callback);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not add weak token.");
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean removeEscrowToken(long handle, int userId) {
        return getLockSettingsInternal().removeEscrowToken(handle, userId);
    }

    public boolean removeWeakEscrowToken(long handle, int userId) {
        try {
            return getLockSettings().removeWeakEscrowToken(handle, userId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not remove the weak token.");
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isEscrowTokenActive(long handle, int userId) {
        return getLockSettingsInternal().isEscrowTokenActive(handle, userId);
    }

    public boolean isWeakEscrowTokenActive(long handle, int userId) {
        try {
            return getLockSettings().isWeakEscrowTokenActive(handle, userId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not check the weak token.");
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isWeakEscrowTokenValid(long handle, byte[] token, int userId) {
        try {
            return getLockSettings().isWeakEscrowTokenValid(handle, token, userId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Could not validate the weak token.");
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setLockCredentialWithToken(LockscreenCredential credential, long tokenHandle, byte[] token, int userHandle) {
        if (!hasSecureLockScreen() && credential.getType() != -1) {
            throw new UnsupportedOperationException("This operation requires the lock screen feature.");
        }
        credential.checkLength();
        LockSettingsInternal localService = getLockSettingsInternal();
        return localService.setLockCredentialWithToken(credential, tokenHandle, token, userHandle);
    }

    public boolean unlockUserWithToken(long tokenHandle, byte[] token, int userId) {
        return getLockSettingsInternal().unlockUserWithToken(tokenHandle, token, userId);
    }

    /* loaded from: classes5.dex */
    public static class StrongAuthTracker {
        private static final int ALLOWING_BIOMETRIC = 260;
        public static final int SOME_AUTH_REQUIRED_AFTER_TRUSTAGENT_EXPIRED = 256;
        public static final int SOME_AUTH_REQUIRED_AFTER_USER_REQUEST = 4;
        public static final int STRONG_AUTH_NOT_REQUIRED = 0;
        public static final int STRONG_AUTH_REQUIRED_AFTER_BOOT = 1;
        public static final int STRONG_AUTH_REQUIRED_AFTER_DPM_LOCK_NOW = 2;
        public static final int STRONG_AUTH_REQUIRED_AFTER_LOCKOUT = 8;
        public static final int STRONG_AUTH_REQUIRED_AFTER_NON_STRONG_BIOMETRICS_TIMEOUT = 128;
        public static final int STRONG_AUTH_REQUIRED_AFTER_TIMEOUT = 16;
        public static final int STRONG_AUTH_REQUIRED_AFTER_USER_LOCKDOWN = 32;
        public static final int STRONG_AUTH_REQUIRED_FOR_UNATTENDED_UPDATE = 64;
        private final boolean mDefaultIsNonStrongBiometricAllowed;
        private final int mDefaultStrongAuthFlags;
        private final HandlerC4434H mHandler;
        private final SparseBooleanArray mIsNonStrongBiometricAllowedForUser;
        private final SparseIntArray mStrongAuthRequiredForUser;
        private final IStrongAuthTracker.Stub mStub;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes5.dex */
        public @interface StrongAuthFlags {
        }

        public StrongAuthTracker(Context context) {
            this(context, Looper.myLooper());
        }

        public StrongAuthTracker(Context context, Looper looper) {
            this.mStrongAuthRequiredForUser = new SparseIntArray();
            this.mIsNonStrongBiometricAllowedForUser = new SparseBooleanArray();
            this.mDefaultIsNonStrongBiometricAllowed = true;
            this.mStub = new IStrongAuthTracker.Stub() { // from class: com.android.internal.widget.LockPatternUtils.StrongAuthTracker.1
                @Override // android.app.trust.IStrongAuthTracker
                public void onStrongAuthRequiredChanged(int strongAuthFlags, int userId) {
                    StrongAuthTracker.this.mHandler.obtainMessage(1, strongAuthFlags, userId).sendToTarget();
                }

                @Override // android.app.trust.IStrongAuthTracker
                public void onIsNonStrongBiometricAllowedChanged(boolean allowed, int userId) {
                    StrongAuthTracker.this.mHandler.obtainMessage(2, allowed ? 1 : 0, userId).sendToTarget();
                }
            };
            this.mHandler = new HandlerC4434H(looper);
            this.mDefaultStrongAuthFlags = getDefaultFlags(context);
        }

        public static int getDefaultFlags(Context context) {
            return context.getResources().getBoolean(C4057R.bool.config_strongAuthRequiredOnBoot) ? 1 : 0;
        }

        public int getStrongAuthForUser(int userId) {
            return this.mStrongAuthRequiredForUser.get(userId, this.mDefaultStrongAuthFlags);
        }

        public boolean isTrustAllowedForUser(int userId) {
            return getStrongAuthForUser(userId) == 0;
        }

        public boolean isBiometricAllowedForUser(boolean isStrongBiometric, int userId) {
            boolean allowed = (getStrongAuthForUser(userId) & (-261)) == 0;
            if (!isStrongBiometric) {
                return allowed & isNonStrongBiometricAllowedAfterIdleTimeout(userId);
            }
            return allowed;
        }

        public boolean isNonStrongBiometricAllowedAfterIdleTimeout(int userId) {
            return this.mIsNonStrongBiometricAllowedForUser.get(userId, true);
        }

        public void onStrongAuthRequiredChanged(int userId) {
        }

        public void onIsNonStrongBiometricAllowedChanged(int userId) {
        }

        protected void handleStrongAuthRequiredChanged(int strongAuthFlags, int userId) {
            int oldValue = getStrongAuthForUser(userId);
            if (strongAuthFlags != oldValue) {
                if (strongAuthFlags == this.mDefaultStrongAuthFlags) {
                    this.mStrongAuthRequiredForUser.delete(userId);
                } else {
                    this.mStrongAuthRequiredForUser.put(userId, strongAuthFlags);
                }
                onStrongAuthRequiredChanged(userId);
            }
        }

        protected void handleIsNonStrongBiometricAllowedChanged(boolean allowed, int userId) {
            boolean oldValue = isNonStrongBiometricAllowedAfterIdleTimeout(userId);
            if (allowed != oldValue) {
                if (allowed) {
                    this.mIsNonStrongBiometricAllowedForUser.delete(userId);
                } else {
                    this.mIsNonStrongBiometricAllowedForUser.put(userId, allowed);
                }
                onIsNonStrongBiometricAllowedChanged(userId);
            }
        }

        public IStrongAuthTracker.Stub getStub() {
            return this.mStub;
        }

        /* renamed from: com.android.internal.widget.LockPatternUtils$StrongAuthTracker$H */
        /* loaded from: classes5.dex */
        private class HandlerC4434H extends Handler {
            static final int MSG_ON_IS_NON_STRONG_BIOMETRIC_ALLOWED_CHANGED = 2;
            static final int MSG_ON_STRONG_AUTH_REQUIRED_CHANGED = 1;

            public HandlerC4434H(Looper looper) {
                super(looper);
            }

            @Override // android.p008os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        StrongAuthTracker.this.handleStrongAuthRequiredChanged(msg.arg1, msg.arg2);
                        return;
                    case 2:
                        StrongAuthTracker.this.handleIsNonStrongBiometricAllowedChanged(msg.arg1 == 1, msg.arg2);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public boolean hasPendingEscrowToken(int userId) {
        try {
            return getLockSettings().hasPendingEscrowToken(userId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean hasSecureLockScreen() {
        if (this.mHasSecureLockScreen == null) {
            try {
                this.mHasSecureLockScreen = Boolean.valueOf(getLockSettings().hasSecureLockScreen());
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
        return this.mHasSecureLockScreen.booleanValue();
    }

    public static boolean userOwnsFrpCredential(Context context, UserInfo info) {
        return info != null && info.isMain() && info.isAdmin() && frpCredentialEnabled(context);
    }

    public static boolean frpCredentialEnabled(Context context) {
        return context.getResources().getBoolean(C4057R.bool.config_enableCredentialFactoryResetProtection);
    }

    public boolean tryUnlockWithCachedUnifiedChallenge(int userId) {
        try {
            return getLockSettings().tryUnlockWithCachedUnifiedChallenge(userId);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void removeCachedUnifiedChallenge(int userId) {
        try {
            getLockSettings().removeCachedUnifiedChallenge(userId);
        } catch (RemoteException re) {
            re.rethrowFromSystemServer();
        }
    }

    public void unlockUserKeyIfUnsecured(int userId) {
        getLockSettingsInternal().unlockUserKeyIfUnsecured(userId);
    }

    public void createNewUser(int userId, int userSerialNumber) {
        getLockSettingsInternal().createNewUser(userId, userSerialNumber);
    }

    public void removeUser(int userId) {
        getLockSettingsInternal().removeUser(userId);
    }

    public StartLockscreenValidationRequest startRemoteLockscreenValidation() {
        try {
            return getLockSettings().startRemoteLockscreenValidation();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] encryptedCredential) {
        try {
            return getLockSettings().validateRemoteLockscreen(encryptedCredential);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
