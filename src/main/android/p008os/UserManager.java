package android.p008os;

import android.accounts.AccountManager;
import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.app.PropertyInvalidatedCache;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.app.compat.CompatChanges;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p001pm.UserInfo;
import android.content.p001pm.UserProperties;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.p008os.IUserManager;
import android.p008os.Parcelable;
import android.provider.Settings;
import android.util.AndroidException;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.C4057R;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
/* renamed from: android.os.UserManager */
/* loaded from: classes3.dex */
public class UserManager {
    @SystemApi
    public static final String ACTION_CREATE_SUPERVISED_USER = "android.os.action.CREATE_SUPERVISED_USER";
    private static final String ACTION_CREATE_USER = "android.os.action.CREATE_USER";
    @SystemApi
    public static final String ACTION_USER_RESTRICTIONS_CHANGED = "android.os.action.USER_RESTRICTIONS_CHANGED";
    public static final String ALLOW_PARENT_PROFILE_APP_LINKING = "allow_parent_profile_app_linking";
    public static final long ALWAYS_USE_CONTEXT_USER = 183155436;
    private static final String CACHE_KEY_IS_USER_UNLOCKED_PROPERTY = "cache_key.is_user_unlocked";
    private static final String CACHE_KEY_STATIC_USER_PROPERTIES = "cache_key.static_user_props";
    private static final String CACHE_KEY_USER_PROPERTIES = "cache_key.user_properties";
    public static final String DEV_CREATE_OVERRIDE_PROPERTY = "debug.user.creation_override";
    public static final String DISALLOW_ADD_CLONE_PROFILE = "no_add_clone_profile";
    @Deprecated
    public static final String DISALLOW_ADD_MANAGED_PROFILE = "no_add_managed_profile";
    public static final String DISALLOW_ADD_USER = "no_add_user";
    public static final String DISALLOW_ADD_WIFI_CONFIG = "no_add_wifi_config";
    public static final String DISALLOW_ADJUST_VOLUME = "no_adjust_volume";
    public static final String DISALLOW_AIRPLANE_MODE = "no_airplane_mode";
    public static final String DISALLOW_AMBIENT_DISPLAY = "no_ambient_display";
    public static final String DISALLOW_APPS_CONTROL = "no_control_apps";
    public static final String DISALLOW_AUTOFILL = "no_autofill";
    public static final String DISALLOW_BIOMETRIC = "disallow_biometric";
    public static final String DISALLOW_BLUETOOTH = "no_bluetooth";
    public static final String DISALLOW_BLUETOOTH_SHARING = "no_bluetooth_sharing";
    public static final String DISALLOW_CAMERA = "no_camera";
    public static final String DISALLOW_CAMERA_TOGGLE = "disallow_camera_toggle";
    public static final String DISALLOW_CELLULAR_2G = "no_cellular_2g";
    public static final String DISALLOW_CHANGE_WIFI_STATE = "no_change_wifi_state";
    public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
    public static final String DISALLOW_CONFIG_BRIGHTNESS = "no_config_brightness";
    public static final String DISALLOW_CONFIG_CELL_BROADCASTS = "no_config_cell_broadcasts";
    public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
    public static final String DISALLOW_CONFIG_DATE_TIME = "no_config_date_time";
    public static final String DISALLOW_CONFIG_DEFAULT_APPS = "disallow_config_default_apps";
    public static final String DISALLOW_CONFIG_LOCALE = "no_config_locale";
    public static final String DISALLOW_CONFIG_LOCATION = "no_config_location";
    public static final String DISALLOW_CONFIG_MOBILE_NETWORKS = "no_config_mobile_networks";
    public static final String DISALLOW_CONFIG_PRIVATE_DNS = "disallow_config_private_dns";
    public static final String DISALLOW_CONFIG_SCREEN_TIMEOUT = "no_config_screen_timeout";
    public static final String DISALLOW_CONFIG_TETHERING = "no_config_tethering";
    public static final String DISALLOW_CONFIG_VPN = "no_config_vpn";
    public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
    public static final String DISALLOW_CONTENT_CAPTURE = "no_content_capture";
    public static final String DISALLOW_CONTENT_SUGGESTIONS = "no_content_suggestions";
    public static final String DISALLOW_CREATE_WINDOWS = "no_create_windows";
    public static final String DISALLOW_CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste";
    public static final String DISALLOW_DATA_ROAMING = "no_data_roaming";
    public static final String DISALLOW_DEBUGGING_FEATURES = "no_debugging_features";
    public static final String DISALLOW_FACTORY_RESET = "no_factory_reset";
    public static final String DISALLOW_FUN = "no_fun";
    public static final String DISALLOW_GRANT_ADMIN = "no_grant_admin";
    public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY = "no_install_unknown_sources_globally";
    public static final String DISALLOW_MICROPHONE_TOGGLE = "disallow_microphone_toggle";
    public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
    public static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "no_physical_media";
    public static final String DISALLOW_NETWORK_RESET = "no_network_reset";
    @SystemApi
    @Deprecated
    public static final String DISALLOW_OEM_UNLOCK = "no_oem_unlock";
    public static final String DISALLOW_OUTGOING_BEAM = "no_outgoing_beam";
    public static final String DISALLOW_OUTGOING_CALLS = "no_outgoing_calls";
    public static final String DISALLOW_PRINTING = "no_printing";
    public static final String DISALLOW_RECORD_AUDIO = "no_record_audio";
    @Deprecated
    public static final String DISALLOW_REMOVE_MANAGED_PROFILE = "no_remove_managed_profile";
    public static final String DISALLOW_REMOVE_USER = "no_remove_user";
    @SystemApi
    public static final String DISALLOW_RUN_IN_BACKGROUND = "no_run_in_background";
    public static final String DISALLOW_SAFE_BOOT = "no_safe_boot";
    public static final String DISALLOW_SET_USER_ICON = "no_set_user_icon";
    public static final String DISALLOW_SET_WALLPAPER = "no_set_wallpaper";
    public static final String DISALLOW_SHARE_INTO_MANAGED_PROFILE = "no_sharing_into_profile";
    public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
    public static final String DISALLOW_SHARING_ADMIN_CONFIGURED_WIFI = "no_sharing_admin_configured_wifi";
    public static final String DISALLOW_SMS = "no_sms";
    public static final String DISALLOW_SYSTEM_ERROR_DIALOGS = "no_system_error_dialogs";
    public static final String DISALLOW_ULTRA_WIDEBAND_RADIO = "no_ultra_wideband_radio";
    public static final String DISALLOW_UNIFIED_PASSWORD = "no_unified_password";
    public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
    public static final String DISALLOW_UNMUTE_DEVICE = "disallow_unmute_device";
    public static final String DISALLOW_UNMUTE_MICROPHONE = "no_unmute_microphone";
    public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
    public static final String DISALLOW_USER_SWITCH = "no_user_switch";
    public static final String DISALLOW_WALLPAPER = "no_wallpaper";
    public static final String DISALLOW_WIFI_DIRECT = "no_wifi_direct";
    public static final String DISALLOW_WIFI_TETHERING = "no_wifi_tethering";
    public static final String ENSURE_VERIFY_APPS = "ensure_verify_apps";
    public static final String EXTRA_USER_ACCOUNT_NAME = "android.os.extra.USER_ACCOUNT_NAME";
    public static final String EXTRA_USER_ACCOUNT_OPTIONS = "android.os.extra.USER_ACCOUNT_OPTIONS";
    public static final String EXTRA_USER_ACCOUNT_TYPE = "android.os.extra.USER_ACCOUNT_TYPE";
    public static final String EXTRA_USER_NAME = "android.os.extra.USER_NAME";
    public static final String KEY_RESTRICTIONS_PENDING = "restrictions_pending";
    public static final int PIN_VERIFICATION_FAILED_INCORRECT = -3;
    public static final int PIN_VERIFICATION_FAILED_NOT_SET = -2;
    public static final int PIN_VERIFICATION_SUCCESS = -1;
    public static final int QUIET_MODE_DISABLE_DONT_ASK_CREDENTIAL = 2;
    public static final int QUIET_MODE_DISABLE_ONLY_IF_CREDENTIAL_NOT_REQUIRED = 1;
    @SystemApi
    public static final int REMOVE_RESULT_ALREADY_BEING_REMOVED = 2;
    @SystemApi
    public static final int REMOVE_RESULT_DEFERRED = 1;
    @SystemApi
    public static final int REMOVE_RESULT_ERROR_MAIN_USER_PERMANENT_ADMIN = -5;
    @SystemApi
    public static final int REMOVE_RESULT_ERROR_SYSTEM_USER = -4;
    @SystemApi
    public static final int REMOVE_RESULT_ERROR_UNKNOWN = -1;
    @SystemApi
    public static final int REMOVE_RESULT_ERROR_USER_NOT_FOUND = -3;
    @SystemApi
    public static final int REMOVE_RESULT_ERROR_USER_RESTRICTION = -2;
    @SystemApi
    public static final int REMOVE_RESULT_REMOVED = 0;
    @SystemApi
    public static final int RESTRICTION_NOT_SET = 0;
    @SystemApi
    public static final int RESTRICTION_SOURCE_DEVICE_OWNER = 2;
    @SystemApi
    public static final int RESTRICTION_SOURCE_PROFILE_OWNER = 4;
    @SystemApi
    public static final int RESTRICTION_SOURCE_SYSTEM = 1;
    @SystemApi
    public static final int SWITCHABILITY_STATUS_OK = 0;
    @SystemApi
    public static final int SWITCHABILITY_STATUS_SYSTEM_USER_LOCKED = 4;
    @SystemApi
    public static final int SWITCHABILITY_STATUS_USER_IN_CALL = 1;
    @SystemApi
    public static final int SWITCHABILITY_STATUS_USER_SWITCH_DISALLOWED = 2;
    public static final String SYSTEM_USER_MODE_EMULATION_DEFAULT = "default";
    public static final String SYSTEM_USER_MODE_EMULATION_FULL = "full";
    public static final String SYSTEM_USER_MODE_EMULATION_HEADLESS = "headless";
    public static final String SYSTEM_USER_MODE_EMULATION_PROPERTY = "persist.debug.user_mode_emulation";
    private static final String TAG = "UserManager";
    public static final int USER_CREATION_FAILED_NOT_PERMITTED = 1;
    public static final int USER_CREATION_FAILED_NO_MORE_USERS = 2;
    public static final int USER_OPERATION_ERROR_CURRENT_USER = 4;
    public static final int USER_OPERATION_ERROR_LOW_STORAGE = 5;
    public static final int USER_OPERATION_ERROR_MANAGED_PROFILE = 2;
    public static final int USER_OPERATION_ERROR_MAX_RUNNING_USERS = 3;
    public static final int USER_OPERATION_ERROR_MAX_USERS = 6;
    public static final int USER_OPERATION_ERROR_UNKNOWN = 1;
    @SystemApi
    public static final int USER_OPERATION_ERROR_USER_ACCOUNT_ALREADY_EXISTS = 7;
    public static final int USER_OPERATION_SUCCESS = 0;
    public static final String USER_TYPE_FULL_DEMO = "android.os.usertype.full.DEMO";
    @SystemApi
    public static final String USER_TYPE_FULL_GUEST = "android.os.usertype.full.GUEST";
    public static final String USER_TYPE_FULL_RESTRICTED = "android.os.usertype.full.RESTRICTED";
    @SystemApi
    public static final String USER_TYPE_FULL_SECONDARY = "android.os.usertype.full.SECONDARY";
    @SystemApi
    public static final String USER_TYPE_FULL_SYSTEM = "android.os.usertype.full.SYSTEM";
    @SystemApi
    public static final String USER_TYPE_PROFILE_CLONE = "android.os.usertype.profile.CLONE";
    @SystemApi
    public static final String USER_TYPE_PROFILE_MANAGED = "android.os.usertype.profile.MANAGED";
    public static final String USER_TYPE_PROFILE_TEST = "android.os.usertype.profile.TEST";
    @SystemApi
    public static final String USER_TYPE_SYSTEM_HEADLESS = "android.os.usertype.system.HEADLESS";
    private static Boolean sIsHeadlessSystemUser = null;
    private final Context mContext;
    private final IUserManager mService;
    private final int mUserId;
    private String mProfileTypeOfProcessUser = null;
    private final PropertyInvalidatedCache<Integer, Boolean> mIsUserUnlockedCache = new PropertyInvalidatedCache<Integer, Boolean>(32, CACHE_KEY_IS_USER_UNLOCKED_PROPERTY) { // from class: android.os.UserManager.1
        @Override // android.app.PropertyInvalidatedCache
        public Boolean recompute(Integer query) {
            try {
                return Boolean.valueOf(UserManager.this.mService.isUserUnlocked(query.intValue()));
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean bypass(Integer query) {
            return query.intValue() < 0;
        }
    };
    private final PropertyInvalidatedCache<Integer, Boolean> mIsUserUnlockingOrUnlockedCache = new PropertyInvalidatedCache<Integer, Boolean>(32, CACHE_KEY_IS_USER_UNLOCKED_PROPERTY) { // from class: android.os.UserManager.2
        @Override // android.app.PropertyInvalidatedCache
        public Boolean recompute(Integer query) {
            try {
                return Boolean.valueOf(UserManager.this.mService.isUserUnlockingOrUnlocked(query.intValue()));
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean bypass(Integer query) {
            return query.intValue() < 0;
        }
    };
    private final PropertyInvalidatedCache<Integer, String> mProfileTypeCache = new PropertyInvalidatedCache<Integer, String>(32, CACHE_KEY_STATIC_USER_PROPERTIES) { // from class: android.os.UserManager.3
        @Override // android.app.PropertyInvalidatedCache
        public String recompute(Integer query) {
            try {
                String profileType = UserManager.this.mService.getProfileType(query.intValue());
                return profileType != null ? profileType.intern() : profileType;
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean bypass(Integer query) {
            return query.intValue() < 0;
        }
    };
    private final PropertyInvalidatedCache<Integer, UserProperties> mUserPropertiesCache = new PropertyInvalidatedCache<Integer, UserProperties>(16, CACHE_KEY_USER_PROPERTIES) { // from class: android.os.UserManager.4
        @Override // android.app.PropertyInvalidatedCache
        public UserProperties recompute(Integer userId) {
            try {
                return UserManager.this.mService.getUserPropertiesCopy(userId.intValue());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean bypass(Integer query) {
            return query.intValue() < 0;
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$QuietModeFlag */
    /* loaded from: classes3.dex */
    public @interface QuietModeFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$RemoveResult */
    /* loaded from: classes3.dex */
    public @interface RemoveResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$UserOperationResult */
    /* loaded from: classes3.dex */
    public @interface UserOperationResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$UserRestrictionKey */
    /* loaded from: classes3.dex */
    public @interface UserRestrictionKey {
    }

    @SystemApi
    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$UserRestrictionSource */
    /* loaded from: classes3.dex */
    public @interface UserRestrictionSource {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UserManager$UserSwitchabilityResult */
    /* loaded from: classes3.dex */
    public @interface UserSwitchabilityResult {
    }

    /* renamed from: android.os.UserManager$UserOperationException */
    /* loaded from: classes3.dex */
    public static class UserOperationException extends RuntimeException {
        private final int mUserOperationResult;

        public UserOperationException(String message, int userOperationResult) {
            super(message);
            this.mUserOperationResult = userOperationResult;
        }

        public int getUserOperationResult() {
            return this.mUserOperationResult;
        }

        public static UserOperationException from(ServiceSpecificException exception) {
            return new UserOperationException(exception.getMessage(), exception.errorCode);
        }
    }

    private <T> T returnNullOrThrowUserOperationException(ServiceSpecificException exception, boolean throwInsteadOfNull) throws UserOperationException {
        if (throwInsteadOfNull) {
            throw UserOperationException.from(exception);
        }
        return null;
    }

    /* renamed from: android.os.UserManager$CheckedUserOperationException */
    /* loaded from: classes3.dex */
    public static class CheckedUserOperationException extends AndroidException {
        private final int mUserOperationResult;

        public CheckedUserOperationException(String message, int userOperationResult) {
            super(message);
            this.mUserOperationResult = userOperationResult;
        }

        public int getUserOperationResult() {
            return this.mUserOperationResult;
        }

        public ServiceSpecificException toServiceSpecificException() {
            return new ServiceSpecificException(this.mUserOperationResult, getMessage());
        }
    }

    private int getContextUserIfAppropriate() {
        if (CompatChanges.isChangeEnabled(ALWAYS_USE_CONTEXT_USER)) {
            return this.mUserId;
        }
        int callingUser = UserHandle.myUserId();
        if (callingUser != this.mUserId) {
            Log.m103w(TAG, "Using the calling user " + callingUser + ", rather than the specified context user " + this.mUserId + ", because API is only UserHandleAware on higher targetSdkVersions.", new Throwable());
        }
        return callingUser;
    }

    public static UserManager get(Context context) {
        return (UserManager) context.getSystemService("user");
    }

    public UserManager(Context context, IUserManager service) {
        this.mService = service;
        Context appContext = context.getApplicationContext();
        this.mContext = appContext == null ? context : appContext;
        this.mUserId = context.getUserId();
    }

    public static boolean supportsMultipleUsers() {
        return getMaxSupportedUsers() > 1 && SystemProperties.getBoolean("fw.show_multiuserui", Resources.getSystem().getBoolean(C4057R.bool.config_enableMultiUserUI));
    }

    public static boolean isGuestUserAlwaysEphemeral() {
        return Resources.getSystem().getBoolean(C4057R.bool.config_guestUserEphemeral);
    }

    public static boolean isGuestUserAllowEphemeralStateChange() {
        return Resources.getSystem().getBoolean(C4057R.bool.config_guestUserAllowEphemeralStateChange);
    }

    public static boolean isMultipleAdminEnabled() {
        return Resources.getSystem().getBoolean(C4057R.bool.config_enableMultipleAdmins);
    }

    public static boolean isHeadlessSystemUserMode() {
        if (sIsHeadlessSystemUser == null) {
            try {
                IUserManager service = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
                sIsHeadlessSystemUser = Boolean.valueOf(service.isHeadlessSystemUserMode());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return sIsHeadlessSystemUser.booleanValue();
    }

    @Deprecated
    public boolean canSwitchUsers() {
        try {
            return this.mService.getUserSwitchability(this.mUserId) == 0;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getUserSwitchability() {
        return getUserSwitchability(UserHandle.m145of(getContextUserIfAppropriate()));
    }

    public int getUserSwitchability(UserHandle userHandle) {
        try {
            return this.mService.getUserSwitchability(userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int getUserHandle() {
        return getContextUserIfAppropriate();
    }

    @Deprecated
    public int getProcessUserId() {
        return UserHandle.myUserId();
    }

    public String getUserType() {
        UserInfo userInfo = getUserInfo(this.mUserId);
        return userInfo == null ? "" : userInfo.userType;
    }

    public String getUserName() {
        int myUserId = UserHandle.myUserId();
        int i = this.mUserId;
        if (myUserId == i) {
            try {
                return this.mService.getUserName();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        UserInfo userInfo = getUserInfo(i);
        if (userInfo != null && userInfo.name != null) {
            return userInfo.name;
        }
        return "";
    }

    @SystemApi
    public boolean isUserNameSet() {
        try {
            return this.mService.isUserNameSet(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserAGoat() {
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 30) {
            return false;
        }
        return this.mContext.getPackageManager().isPackageAvailable("com.coffeestainstudios.goatsimulator");
    }

    @SystemApi
    @Deprecated
    public boolean isPrimaryUser() {
        UserInfo user = getUserInfo(getContextUserIfAppropriate());
        return user != null && user.isPrimary();
    }

    public boolean isSystemUser() {
        return getContextUserIfAppropriate() == 0;
    }

    @SystemApi
    public boolean isMainUser() {
        UserInfo user = getUserInfo(this.mUserId);
        return user != null && user.isMain();
    }

    @SystemApi
    public UserHandle getMainUser() {
        try {
            int mainUserId = this.mService.getMainUserId();
            if (mainUserId == -10000) {
                return null;
            }
            return UserHandle.m145of(mainUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isAdminUser() {
        try {
            return this.mService.isAdminUser(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserAdmin(int userId) {
        UserInfo user = getUserInfo(userId);
        return user != null && user.isAdmin();
    }

    @SystemApi
    public boolean isUserOfType(String userType) {
        try {
            return this.mService.isUserOfType(this.mUserId, userType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public static boolean isUserTypeManagedProfile(String userType) {
        return USER_TYPE_PROFILE_MANAGED.equals(userType);
    }

    public static boolean isUserTypeGuest(String userType) {
        return USER_TYPE_FULL_GUEST.equals(userType);
    }

    public static boolean isUserTypeRestricted(String userType) {
        return USER_TYPE_FULL_RESTRICTED.equals(userType);
    }

    public static boolean isUserTypeDemo(String userType) {
        return USER_TYPE_FULL_DEMO.equals(userType);
    }

    public static boolean isUserTypeCloneProfile(String userType) {
        return USER_TYPE_PROFILE_CLONE.equals(userType);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getUserTypeForStatsd(String userType) {
        char c;
        switch (userType.hashCode()) {
            case -1103927049:
                if (userType.equals(USER_TYPE_FULL_GUEST)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -159818852:
                if (userType.equals(USER_TYPE_PROFILE_MANAGED)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 34001850:
                if (userType.equals(USER_TYPE_SYSTEM_HEADLESS)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 485661392:
                if (userType.equals(USER_TYPE_FULL_SYSTEM)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 942013715:
                if (userType.equals(USER_TYPE_FULL_SECONDARY)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1711075452:
                if (userType.equals(USER_TYPE_FULL_RESTRICTED)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1765400260:
                if (userType.equals(USER_TYPE_FULL_DEMO)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1966344346:
                if (userType.equals(USER_TYPE_PROFILE_CLONE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            default:
                return 0;
        }
    }

    @Deprecated
    public boolean isLinkedUser() {
        return isRestrictedProfile();
    }

    @SystemApi
    public boolean isRestrictedProfile() {
        try {
            return this.mService.isRestricted(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isRestrictedProfile(UserHandle user) {
        try {
            return this.mService.isRestricted(user.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean canHaveRestrictedProfile() {
        try {
            return this.mService.canHaveRestrictedProfile(this.mUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean hasRestrictedProfiles() {
        try {
            return this.mService.hasRestrictedProfiles(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserHandle getRestrictedProfileParent() {
        int parent;
        UserInfo info = getUserInfo(this.mUserId);
        if (info == null || !info.isRestricted() || (parent = info.restrictedProfileParentId) == -10000) {
            return null;
        }
        return UserHandle.m145of(parent);
    }

    public boolean isGuestUser(int userId) {
        UserInfo user = getUserInfo(userId);
        return user != null && user.isGuest();
    }

    @SystemApi
    public boolean isGuestUser() {
        UserInfo user = getUserInfo(getContextUserIfAppropriate());
        return user != null && user.isGuest();
    }

    public boolean isDemoUser() {
        try {
            return this.mService.isDemoUser(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isProfile() {
        return isProfile(this.mUserId);
    }

    private boolean isProfile(int userId) {
        String profileType = getProfileType(userId);
        return (profileType == null || profileType.equals("")) ? false : true;
    }

    private String getProfileType() {
        return getProfileType(this.mUserId);
    }

    private String getProfileType(int userId) {
        if (userId == UserHandle.myUserId()) {
            String str = this.mProfileTypeOfProcessUser;
            if (str != null) {
                return str;
            }
            try {
                String profileType = this.mService.getProfileType(userId);
                if (profileType != null) {
                    String intern = profileType.intern();
                    this.mProfileTypeOfProcessUser = intern;
                    return intern;
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return this.mProfileTypeCache.query(Integer.valueOf(userId));
    }

    public boolean isManagedProfile() {
        return isManagedProfile(getContextUserIfAppropriate());
    }

    @SystemApi
    public boolean isManagedProfile(int userId) {
        return isUserTypeManagedProfile(getProfileType(userId));
    }

    @SystemApi
    public boolean isCloneProfile() {
        return isUserTypeCloneProfile(getProfileType());
    }

    public boolean isEphemeralUser() {
        return isUserEphemeral(this.mUserId);
    }

    public boolean isUserEphemeral(int userId) {
        UserInfo user = getUserInfo(userId);
        return user != null && user.isEphemeral();
    }

    public boolean isUserRunning(UserHandle user) {
        return isUserRunning(user.getIdentifier());
    }

    public boolean isUserRunning(int userId) {
        try {
            return this.mService.isUserRunning(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserRunningOrStopping(UserHandle user) {
        try {
            return ActivityManager.getService().isUserRunning(user.getIdentifier(), 1);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserForeground() {
        try {
            return this.mService.isUserForeground(this.mUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public static boolean isVisibleBackgroundUsersEnabled() {
        return SystemProperties.getBoolean("fw.visible_bg_users", Resources.getSystem().getBoolean(C4057R.bool.config_multiuserVisibleBackgroundUsers));
    }

    public boolean isVisibleBackgroundUsersSupported() {
        return isVisibleBackgroundUsersEnabled();
    }

    public static boolean isVisibleBackgroundUsersOnDefaultDisplayEnabled() {
        return SystemProperties.getBoolean("fw.visible_bg_users_on_default_display", Resources.getSystem().getBoolean(C4057R.bool.config_multiuserVisibleBackgroundUsersOnDefaultDisplay));
    }

    public boolean isVisibleBackgroundUsersOnDefaultDisplaySupported() {
        return isVisibleBackgroundUsersOnDefaultDisplayEnabled();
    }

    @SystemApi
    public boolean isUserVisible() {
        try {
            return this.mService.isUserVisible(this.mUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Set<UserHandle> getVisibleUsers() {
        ArraySet<UserHandle> result = new ArraySet<>();
        try {
            int[] visibleUserIds = this.mService.getVisibleUsers();
            if (visibleUserIds != null) {
                for (int userId : visibleUserIds) {
                    result.add(UserHandle.m145of(userId));
                }
            }
            return result;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getDisplayIdAssignedToUser() {
        try {
            return this.mService.getDisplayIdAssignedToUser();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserUnlocked() {
        return isUserUnlocked(getContextUserIfAppropriate());
    }

    public boolean isUserUnlocked(UserHandle user) {
        return isUserUnlocked(user.getIdentifier());
    }

    public boolean isUserUnlocked(int userId) {
        return this.mIsUserUnlockedCache.query(Integer.valueOf(userId)).booleanValue();
    }

    public void disableIsUserUnlockedCache() {
        this.mIsUserUnlockedCache.disableLocal();
        this.mIsUserUnlockingOrUnlockedCache.disableLocal();
    }

    public static final void invalidateIsUserUnlockedCache() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_IS_USER_UNLOCKED_PROPERTY);
    }

    @SystemApi
    public boolean isUserUnlockingOrUnlocked(UserHandle user) {
        return isUserUnlockingOrUnlocked(user.getIdentifier());
    }

    public boolean isUserUnlockingOrUnlocked(int userId) {
        return this.mIsUserUnlockingOrUnlockedCache.query(Integer.valueOf(userId)).booleanValue();
    }

    public long getUserStartRealtime() {
        if (getContextUserIfAppropriate() != UserHandle.myUserId()) {
            throw new IllegalArgumentException("Calling from a context differing from the calling user is not currently supported.");
        }
        try {
            return this.mService.getUserStartRealtime();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getUserUnlockRealtime() {
        if (getContextUserIfAppropriate() != UserHandle.myUserId()) {
            throw new IllegalArgumentException("Calling from a context differing from the calling user is not currently supported.");
        }
        try {
            return this.mService.getUserUnlockRealtime();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public UserInfo getUserInfo(int userId) {
        try {
            return this.mService.getUserInfo(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserProperties getUserProperties(UserHandle userHandle) {
        return this.mUserPropertiesCache.query(Integer.valueOf(userHandle.getIdentifier()));
    }

    @SystemApi
    @Deprecated
    public int getUserRestrictionSource(String restrictionKey, UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictionSource(restrictionKey, userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<EnforcingUser> getUserRestrictionSources(String restrictionKey, UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictionSources(restrictionKey, userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Bundle getUserRestrictions() {
        try {
            return this.mService.getUserRestrictions(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Bundle getUserRestrictions(UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictions(userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasBaseUserRestriction(String restrictionKey, UserHandle userHandle) {
        try {
            return this.mService.hasBaseUserRestriction(restrictionKey, userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setUserRestrictions(Bundle restrictions) {
        throw new UnsupportedOperationException("This method is no longer supported");
    }

    @Deprecated
    public void setUserRestrictions(Bundle restrictions, UserHandle userHandle) {
        throw new UnsupportedOperationException("This method is no longer supported");
    }

    @Deprecated
    public void setUserRestriction(String key, boolean value) {
        try {
            this.mService.setUserRestriction(key, value, getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setUserRestriction(String key, boolean value, UserHandle userHandle) {
        try {
            this.mService.setUserRestriction(key, value, userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasUserRestriction(String restrictionKey) {
        return hasUserRestrictionForUser(restrictionKey, getContextUserIfAppropriate());
    }

    @Deprecated
    public boolean hasUserRestriction(String restrictionKey, UserHandle userHandle) {
        return hasUserRestrictionForUser(restrictionKey, userHandle);
    }

    @SystemApi
    public boolean hasUserRestrictionForUser(String restrictionKey, UserHandle userHandle) {
        return hasUserRestrictionForUser(restrictionKey, userHandle.getIdentifier());
    }

    private boolean hasUserRestrictionForUser(String restrictionKey, int userId) {
        try {
            return this.mService.hasUserRestriction(restrictionKey, userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasUserRestrictionOnAnyUser(String restrictionKey) {
        try {
            return this.mService.hasUserRestrictionOnAnyUser(restrictionKey);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isSettingRestrictedForUser(String setting, int userId, String value, int callingUid) {
        try {
            return this.mService.isSettingRestrictedForUser(setting, userId, value, callingUid);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void addUserRestrictionsListener(IUserRestrictionsListener listener) {
        try {
            this.mService.addUserRestrictionsListener(listener);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getSerialNumberForUser(UserHandle user) {
        return getUserSerialNumber(user.getIdentifier());
    }

    public UserHandle getUserForSerialNumber(long serialNumber) {
        int ident = getUserHandle((int) serialNumber);
        if (ident >= 0) {
            return new UserHandle(ident);
        }
        return null;
    }

    @Deprecated
    public UserInfo createUser(String name, int flags) {
        return createUser(name, UserInfo.getDefaultUserType(flags), flags);
    }

    public UserInfo createUser(String name, String userType, int flags) {
        try {
            return this.mService.createUserWithThrow(name, userType, flags);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return null;
        }
    }

    @SystemApi
    public NewUserResponse createUser(NewUserRequest newUserRequest) {
        try {
            UserHandle userHandle = this.mService.createUserWithAttributes(newUserRequest.getName(), newUserRequest.getUserType(), newUserRequest.getFlags(), newUserRequest.getUserIcon(), newUserRequest.getAccountName(), newUserRequest.getAccountType(), newUserRequest.getAccountOptions());
            return new NewUserResponse(userHandle, 0);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            Log.m103w(TAG, "Exception while creating user " + newUserRequest, e);
            return new NewUserResponse(null, e.errorCode);
        }
    }

    public UserInfo preCreateUser(String userType) throws UserOperationException {
        try {
            return this.mService.preCreateUserWithThrow(userType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            throw UserOperationException.from(e);
        }
    }

    public UserInfo createGuest(Context context) {
        try {
            UserInfo guest = this.mService.createUserWithThrow(null, USER_TYPE_FULL_GUEST, 0);
            Settings.Secure.putStringForUser(context.getContentResolver(), Settings.Secure.SKIP_FIRST_USE_HINTS, "1", guest.f48id);
            if (isGuestUserAllowEphemeralStateChange()) {
                boolean resetGuestOnExit = Settings.Global.getInt(context.getContentResolver(), Settings.Global.REMOVE_GUEST_ON_EXIT, 1) == 1;
                if (resetGuestOnExit && !guest.isEphemeral()) {
                    setUserEphemeral(guest.f48id, true);
                }
            }
            return guest;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return null;
        }
    }

    @Deprecated
    public UserInfo findCurrentGuestUser() {
        try {
            List<UserInfo> guestUsers = this.mService.getGuestUsers();
            if (guestUsers.size() == 0) {
                return null;
            }
            return guestUsers.get(0);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<UserInfo> getGuestUsers() {
        try {
            return this.mService.getGuestUsers();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserHandle createProfile(String name, String userType, Set<String> disallowedPackages) throws UserOperationException {
        try {
            return this.mService.createProfileForUserWithThrow(name, userType, 0, this.mUserId, (String[]) disallowedPackages.toArray(new String[disallowedPackages.size()])).getUserHandle();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return (UserHandle) returnNullOrThrowUserOperationException(e, this.mContext.getApplicationInfo().targetSdkVersion >= 30);
        }
    }

    @Deprecated
    public UserInfo createProfileForUser(String name, int flags, int userId) {
        return createProfileForUser(name, UserInfo.getDefaultUserType(flags), flags, userId, null);
    }

    public UserInfo createProfileForUser(String name, String userType, int flags, int userId) {
        return createProfileForUser(name, userType, flags, userId, null);
    }

    public UserInfo createProfileForUser(String name, String userType, int flags, int userId, String[] disallowedPackages) {
        try {
            return this.mService.createProfileForUserWithThrow(name, userType, flags, userId, disallowedPackages);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return null;
        }
    }

    public UserInfo createProfileForUserEvenWhenDisallowed(String name, String userType, int flags, int userId, String[] disallowedPackages) {
        try {
            return this.mService.createProfileForUserEvenWhenDisallowedWithThrow(name, userType, flags, userId, disallowedPackages);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return null;
        }
    }

    public UserInfo createRestrictedProfile(String name) {
        try {
            int parentUserId = this.mUserId;
            UserInfo profile = this.mService.createRestrictedProfileWithThrow(name, parentUserId);
            UserHandle parentUserHandle = UserHandle.m145of(parentUserId);
            AccountManager.get(this.mContext).addSharedAccountsFromParentUser(parentUserHandle, UserHandle.m145of(profile.f48id));
            return profile;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            return null;
        }
    }

    public static Intent createUserCreationIntent(String userName, String accountName, String accountType, PersistableBundle accountOptions) {
        Intent intent = new Intent(ACTION_CREATE_USER);
        if (userName != null) {
            intent.putExtra(EXTRA_USER_NAME, userName);
        }
        if (accountName != null && accountType == null) {
            throw new IllegalArgumentException("accountType must be specified if accountName is specified");
        }
        if (accountName != null) {
            intent.putExtra(EXTRA_USER_ACCOUNT_NAME, accountName);
        }
        if (accountType != null) {
            intent.putExtra(EXTRA_USER_ACCOUNT_TYPE, accountType);
        }
        if (accountOptions != null) {
            intent.putExtra(EXTRA_USER_ACCOUNT_OPTIONS, accountOptions);
        }
        return intent;
    }

    public Set<String> getPreInstallableSystemPackages(String userType) {
        try {
            String[] installableSystemPackages = this.mService.getPreInstallableSystemPackages(userType);
            if (installableSystemPackages == null) {
                return null;
            }
            return new ArraySet(installableSystemPackages);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public String getSeedAccountName() {
        try {
            return this.mService.getSeedAccountName(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public String getSeedAccountType() {
        try {
            return this.mService.getSeedAccountType(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public PersistableBundle getSeedAccountOptions() {
        try {
            return this.mService.getSeedAccountOptions(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setSeedAccountData(int userId, String accountName, String accountType, PersistableBundle accountOptions) {
        try {
            this.mService.setSeedAccountData(userId, accountName, accountType, accountOptions, true);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void clearSeedAccountData() {
        try {
            this.mService.clearSeedAccountData(getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean markGuestForDeletion(int userId) {
        try {
            return this.mService.markGuestForDeletion(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setUserEnabled(int userId) {
        try {
            this.mService.setUserEnabled(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setUserAdmin(int userId) {
        try {
            this.mService.setUserAdmin(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void revokeUserAdmin(int userId) {
        try {
            this.mService.revokeUserAdmin(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void evictCredentialEncryptionKey(int userId) {
        try {
            this.mService.evictCredentialEncryptionKey(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserCount() {
        List<UserInfo> users = getUsers();
        if (users != null) {
            return users.size();
        }
        return 1;
    }

    public List<UserInfo> getUsers() {
        return getUsers(true, false, true);
    }

    public List<UserInfo> getAliveUsers() {
        return getUsers(true, true, true);
    }

    @Deprecated
    public List<UserInfo> getUsers(boolean excludeDying) {
        return getUsers(true, excludeDying, true);
    }

    public List<UserInfo> getUsers(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) {
        try {
            return this.mService.getUsers(excludePartial, excludeDying, excludePreCreated);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<UserHandle> getUserHandles(boolean excludeDying) {
        List<UserInfo> users = getUsers(true, excludeDying, true);
        List<UserHandle> result = new ArrayList<>(users.size());
        for (UserInfo user : users) {
            result.add(user.getUserHandle());
        }
        return result;
    }

    @SystemApi
    public long[] getSerialNumbersOfUsers(boolean excludeDying) {
        List<UserInfo> users = getUsers(true, excludeDying, true);
        long[] result = new long[users.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = users.get(i).serialNumber;
        }
        return result;
    }

    public String getUserAccount(int userId) {
        try {
            return this.mService.getUserAccount(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setUserAccount(int userId, String accountName) {
        try {
            this.mService.setUserAccount(userId, accountName);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public UserInfo getPrimaryUser() {
        try {
            return this.mService.getPrimaryUser();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserHandle getPreviousForegroundUser() {
        try {
            int previousUser = this.mService.getPreviousFullUserToEnterForeground();
            if (previousUser == -10000) {
                return null;
            }
            return UserHandle.m145of(previousUser);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean canAddMoreUsers() {
        List<UserInfo> users = getAliveUsers();
        int totalUserCount = users.size();
        int aliveUserCount = 0;
        for (int i = 0; i < totalUserCount; i++) {
            UserInfo user = users.get(i);
            if (!user.isGuest()) {
                aliveUserCount++;
            }
        }
        int i2 = getMaxSupportedUsers();
        return aliveUserCount < i2;
    }

    public boolean canAddMoreUsers(String userType) {
        try {
            if (userType.equals(USER_TYPE_FULL_GUEST)) {
                return this.mService.canAddMoreUsersOfType(userType);
            }
            return canAddMoreUsers() && this.mService.canAddMoreUsersOfType(userType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getRemainingCreatableUserCount(String userType) {
        Objects.requireNonNull(userType, "userType must not be null");
        try {
            return this.mService.getRemainingCreatableUserCount(userType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getRemainingCreatableProfileCount(String userType) {
        Objects.requireNonNull(userType, "userType must not be null");
        try {
            return this.mService.getRemainingCreatableProfileCount(userType, this.mUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean canAddMoreManagedProfiles(int userId, boolean allowedToRemoveOne) {
        try {
            return this.mService.canAddMoreManagedProfiles(userId, allowedToRemoveOne);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean canAddMoreProfilesToUser(String userType, int userId) {
        try {
            return this.mService.canAddMoreProfilesToUser(userType, userId, false);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isUserTypeEnabled(String userType) {
        try {
            return this.mService.isUserTypeEnabled(userType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<UserInfo> getProfiles(int userId) {
        try {
            return this.mService.getProfiles(userId, false);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isSameProfileGroup(UserHandle user, UserHandle otherUser) {
        return isSameProfileGroup(user.getIdentifier(), otherUser.getIdentifier());
    }

    public boolean isSameProfileGroup(int userId, int otherUserId) {
        try {
            return this.mService.isSameProfileGroup(userId, otherUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public List<UserInfo> getEnabledProfiles(int userId) {
        try {
            return this.mService.getProfiles(userId, true);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<UserHandle> getUserProfiles() {
        int[] userIds = getProfileIds(getContextUserIfAppropriate(), true);
        return convertUserIdsToUserHandles(userIds);
    }

    @SystemApi
    public List<UserHandle> getEnabledProfiles() {
        return getProfiles(true);
    }

    @SystemApi
    public List<UserHandle> getAllProfiles() {
        return getProfiles(false);
    }

    private List<UserHandle> getProfiles(boolean enabledOnly) {
        int[] userIds = getProfileIds(this.mUserId, enabledOnly);
        return convertUserIdsToUserHandles(userIds);
    }

    private List<UserHandle> convertUserIdsToUserHandles(int[] userIds) {
        List<UserHandle> result = new ArrayList<>(userIds.length);
        for (int userId : userIds) {
            result.add(UserHandle.m145of(userId));
        }
        return result;
    }

    public int[] getProfileIds(int userId, boolean enabledOnly) {
        try {
            return this.mService.getProfileIds(userId, enabledOnly);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int[] getProfileIdsWithDisabled(int userId) {
        return getProfileIds(userId, false);
    }

    public int[] getEnabledProfileIds(int userId) {
        return getProfileIds(userId, true);
    }

    public int getCredentialOwnerProfile(int userId) {
        try {
            return this.mService.getCredentialOwnerProfile(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public UserInfo getProfileParent(int userId) {
        try {
            return this.mService.getProfileParent(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserHandle getProfileParent(UserHandle user) {
        UserInfo info = getProfileParent(user.getIdentifier());
        if (info == null) {
            return null;
        }
        return UserHandle.m145of(info.f48id);
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle userHandle) {
        return requestQuietModeEnabled(enableQuietMode, userHandle, (IntentSender) null);
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle userHandle, int flags) {
        return requestQuietModeEnabled(enableQuietMode, userHandle, null, flags);
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle userHandle, IntentSender target) {
        return requestQuietModeEnabled(enableQuietMode, userHandle, target, 0);
    }

    public boolean requestQuietModeEnabled(boolean enableQuietMode, UserHandle userHandle, IntentSender target, int flags) {
        try {
            return this.mService.requestQuietModeEnabled(this.mContext.getPackageName(), enableQuietMode, userHandle.getIdentifier(), target, flags);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isQuietModeEnabled(UserHandle userHandle) {
        try {
            return this.mService.isQuietModeEnabled(userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasBadge(int userId) {
        if (!isProfile(userId)) {
            return false;
        }
        try {
            return this.mService.hasBadge(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasBadge() {
        return hasBadge(this.mUserId);
    }

    public int getUserBadgeColor(int userId) {
        try {
            int resourceId = this.mService.getUserBadgeColorResId(userId);
            return Resources.getSystem().getColor(resourceId, null);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserBadgeDarkColor(int userId) {
        try {
            int resourceId = this.mService.getUserBadgeDarkColorResId(userId);
            return Resources.getSystem().getColor(resourceId, null);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserIconBadgeResId(int userId) {
        try {
            return this.mService.getUserIconBadgeResId(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserBadgeResId(int userId) {
        try {
            return this.mService.getUserBadgeResId(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserBadgeNoBackgroundResId(int userId) {
        try {
            return this.mService.getUserBadgeNoBackgroundResId(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Drawable getBadgedIconForUser(Drawable icon, UserHandle user) {
        return this.mContext.getPackageManager().getUserBadgedIcon(icon, user);
    }

    public Drawable getBadgedDrawableForUser(Drawable badgedDrawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return this.mContext.getPackageManager().getUserBadgedDrawableForDensity(badgedDrawable, user, badgeLocation, badgeDensity);
    }

    public CharSequence getBadgedLabelForUser(final CharSequence label, UserHandle user) {
        final int userId = user.getIdentifier();
        if (!hasBadge(userId)) {
            return label;
        }
        DevicePolicyManager dpm = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        return dpm.getResources().getString(getUpdatableUserBadgedLabelId(userId), new Supplier() { // from class: android.os.UserManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getBadgedLabelForUser$0;
                lambda$getBadgedLabelForUser$0 = UserManager.this.lambda$getBadgedLabelForUser$0(label, userId);
                return lambda$getBadgedLabelForUser$0;
            }
        }, label);
    }

    private String getUpdatableUserBadgedLabelId(int userId) {
        return isManagedProfile(userId) ? DevicePolicyResources.Strings.Core.WORK_PROFILE_BADGED_LABEL : DevicePolicyResources.UNDEFINED;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getDefaultUserBadgedLabel */
    public String lambda$getBadgedLabelForUser$0(CharSequence label, int userId) {
        try {
            int resourceId = this.mService.getUserBadgeLabelResId(userId);
            return Resources.getSystem().getString(resourceId, label);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public boolean isMediaSharedWithParent() {
        try {
            return getUserProperties(UserHandle.m145of(this.mUserId)).isMediaSharedWithParent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SystemApi
    @Deprecated
    public boolean isCredentialSharableWithParent() {
        try {
            return getUserProperties(UserHandle.m145of(this.mUserId)).isCredentialShareableWithParent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean removeUser(int userId) {
        try {
            return this.mService.removeUser(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean removeUser(UserHandle user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        return removeUser(user.getIdentifier());
    }

    public boolean removeUserEvenWhenDisallowed(int userId) {
        try {
            return this.mService.removeUserEvenWhenDisallowed(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int removeUserWhenPossible(UserHandle user, boolean overrideDevicePolicy) {
        try {
            return this.mService.removeUserWhenPossible(user.getIdentifier(), overrideDevicePolicy);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public static boolean isRemoveResultSuccessful(int result) {
        return result >= 0;
    }

    public void setUserName(int userId, String name) {
        try {
            this.mService.setUserName(userId, name);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean setUserEphemeral(int userId, boolean enableEphemeral) {
        try {
            return this.mService.setUserEphemeral(userId, enableEphemeral);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setUserName(String name) {
        setUserName(this.mUserId, name);
    }

    public void setUserIcon(int userId, Bitmap icon) {
        try {
            this.mService.setUserIcon(userId, icon);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
        }
    }

    @SystemApi
    public void setUserIcon(Bitmap icon) throws UserOperationException {
        setUserIcon(this.mUserId, icon);
    }

    public Bitmap getUserIcon(int userId) {
        try {
            ParcelFileDescriptor fd = this.mService.getUserIcon(userId);
            if (fd != null) {
                Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                try {
                    fd.close();
                } catch (IOException e) {
                }
                return decodeFileDescriptor;
            }
            return null;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Bitmap getUserIcon() {
        return getUserIcon(this.mUserId);
    }

    public static int getMaxSupportedUsers() {
        if (Build.f303ID.startsWith("JVP")) {
            return 1;
        }
        return Math.max(1, SystemProperties.getInt("fw.max_users", Resources.getSystem().getInteger(C4057R.integer.config_multiuserMaximumUsers)));
    }

    public boolean isUserSwitcherEnabled() {
        return isUserSwitcherEnabled(true);
    }

    public boolean isUserSwitcherEnabled(boolean showEvenIfNotActionable) {
        try {
            return this.mService.isUserSwitcherEnabled(showEvenIfNotActionable, this.mUserId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public static boolean isDeviceInDemoMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVICE_DEMO_MODE, 0) > 0;
    }

    public int getUserSerialNumber(int userId) {
        try {
            return this.mService.getUserSerialNumber(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getUserHandle(int userSerialNumber) {
        try {
            return this.mService.getUserHandle(userSerialNumber);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public Bundle getApplicationRestrictions(String packageName) {
        try {
            return this.mService.getApplicationRestrictionsForUser(packageName, getContextUserIfAppropriate());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public Bundle getApplicationRestrictions(String packageName, UserHandle user) {
        try {
            return this.mService.getApplicationRestrictionsForUser(packageName, user.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setApplicationRestrictions(String packageName, Bundle restrictions, UserHandle user) {
        try {
            this.mService.setApplicationRestrictions(packageName, restrictions, user.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean setRestrictionsChallenge(String newPin) {
        return false;
    }

    public void setDefaultGuestRestrictions(Bundle restrictions) {
        try {
            this.mService.setDefaultGuestRestrictions(restrictions);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Bundle getDefaultGuestRestrictions() {
        try {
            return this.mService.getDefaultGuestRestrictions();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getUserCreationTime(UserHandle userHandle) {
        try {
            return this.mService.getUserCreationTime(userHandle.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean someUserHasSeedAccount(String accountName, String accountType) {
        try {
            return this.mService.someUserHasSeedAccount(accountName, accountType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean someUserHasAccount(String accountName, String accountType) {
        Objects.requireNonNull(accountName, "accountName must not be null");
        Objects.requireNonNull(accountType, "accountType must not be null");
        try {
            return this.mService.someUserHasAccount(accountName, accountType);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setBootUser(UserHandle bootUser) {
        try {
            this.mService.setBootUser(bootUser.getIdentifier());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public UserHandle getBootUser() {
        try {
            return UserHandle.m145of(this.mService.getBootUser());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public static final void invalidateStaticUserProperties() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_STATIC_USER_PROPERTIES);
    }

    public static final void invalidateUserPropertiesCache() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_USER_PROPERTIES);
    }

    @SystemApi
    /* renamed from: android.os.UserManager$EnforcingUser */
    /* loaded from: classes3.dex */
    public static final class EnforcingUser implements Parcelable {
        public static final Parcelable.Creator<EnforcingUser> CREATOR = new Parcelable.Creator<EnforcingUser>() { // from class: android.os.UserManager.EnforcingUser.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public EnforcingUser createFromParcel(Parcel in) {
                return new EnforcingUser(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public EnforcingUser[] newArray(int size) {
                return new EnforcingUser[size];
            }
        };
        private final int userId;
        private final int userRestrictionSource;

        public EnforcingUser(int userId, int userRestrictionSource) {
            this.userId = userId;
            this.userRestrictionSource = userRestrictionSource;
        }

        private EnforcingUser(Parcel in) {
            this.userId = in.readInt();
            this.userRestrictionSource = in.readInt();
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.userId);
            dest.writeInt(this.userRestrictionSource);
        }

        public UserHandle getUserHandle() {
            return UserHandle.m145of(this.userId);
        }

        public int getUserRestrictionSource() {
            return this.userRestrictionSource;
        }
    }
}
