package android.app.admin;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SystemApi;
import android.app.IServiceConnection;
import android.app.admin.DevicePolicyManager;
import android.app.admin.PreferentialNetworkServiceConfig;
import android.app.admin.SecurityLog;
import android.app.admin.StartInstallingUpdateCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.IPackageDataObserver;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ParceledListSlice;
import android.content.p001pm.UserInfo;
import android.graphics.Bitmap;
import android.net.PrivateDnsConnectivityChecker;
import android.net.ProxyInfo;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IpcDataCache;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.security.AttestedKeyPair;
import android.security.Credentials;
import android.security.KeyChain;
import android.security.KeyChainException;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keystore.AttestationUtils;
import android.security.keystore.KeyAttestationException;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.ParcelableKeyGenParameterSpec;
import android.security.keystore.StrongBoxUnavailableException;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.Log;
import android.util.Pair;
import com.android.internal.C4057R;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.net.NetworkUtilsInternal;
import com.android.internal.p028os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class DevicePolicyManager {
    @SystemApi
    public static final String ACCOUNT_FEATURE_DEVICE_OR_PROFILE_OWNER_ALLOWED = "android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED";
    @SystemApi
    public static final String ACCOUNT_FEATURE_DEVICE_OR_PROFILE_OWNER_DISALLOWED = "android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED";
    public static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN";
    public static final String ACTION_ADMIN_POLICY_COMPLIANCE = "android.app.action.ADMIN_POLICY_COMPLIANCE";
    public static final String ACTION_APPLICATION_DELEGATION_SCOPES_CHANGED = "android.app.action.APPLICATION_DELEGATION_SCOPES_CHANGED";
    @SystemApi
    public static final String ACTION_BIND_SECONDARY_LOCKSCREEN_SERVICE = "android.app.action.BIND_SECONDARY_LOCKSCREEN_SERVICE";
    public static final String ACTION_BUGREPORT_SHARING_ACCEPTED = "com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED";
    public static final String ACTION_BUGREPORT_SHARING_DECLINED = "com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED";
    public static final String ACTION_CHECK_POLICY_COMPLIANCE = "android.app.action.CHECK_POLICY_COMPLIANCE";
    public static final String ACTION_DATA_SHARING_RESTRICTION_APPLIED = "android.app.action.DATA_SHARING_RESTRICTION_APPLIED";
    public static final String ACTION_DEVICE_ADMIN_SERVICE = "android.app.action.DEVICE_ADMIN_SERVICE";
    public static final String ACTION_DEVICE_OWNER_CHANGED = "android.app.action.DEVICE_OWNER_CHANGED";
    public static final String ACTION_DEVICE_POLICY_CONSTANTS_CHANGED = "android.app.action.DEVICE_POLICY_CONSTANTS_CHANGED";
    public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
    public static final String ACTION_DEVICE_POLICY_RESOURCE_UPDATED = "android.app.action.DEVICE_POLICY_RESOURCE_UPDATED";
    @SystemApi
    public static final String ACTION_ESTABLISH_NETWORK_CONNECTION = "android.app.action.ESTABLISH_NETWORK_CONNECTION";
    public static final String ACTION_GET_PROVISIONING_MODE = "android.app.action.GET_PROVISIONING_MODE";
    @SystemApi
    public static final String ACTION_LOST_MODE_LOCATION_UPDATE = "android.app.action.LOST_MODE_LOCATION_UPDATE";
    public static final String ACTION_MANAGED_PROFILE_PROVISIONED = "android.app.action.MANAGED_PROFILE_PROVISIONED";
    public static final String ACTION_PROFILE_OWNER_CHANGED = "android.app.action.PROFILE_OWNER_CHANGED";
    public static final String ACTION_PROVISIONING_COMPLETED = "android.app.action.PROVISIONING_COMPLETED";
    public static final String ACTION_PROVISIONING_SUCCESSFUL = "android.app.action.PROVISIONING_SUCCESSFUL";
    @SystemApi
    public static final String ACTION_PROVISION_FINALIZATION = "android.app.action.PROVISION_FINALIZATION";
    @SystemApi
    public static final String ACTION_PROVISION_FINANCED_DEVICE = "android.app.action.PROVISION_FINANCED_DEVICE";
    @Deprecated
    public static final String ACTION_PROVISION_MANAGED_DEVICE = "android.app.action.PROVISION_MANAGED_DEVICE";
    @SystemApi
    public static final String ACTION_PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE = "android.app.action.PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE";
    public static final String ACTION_PROVISION_MANAGED_PROFILE = "android.app.action.PROVISION_MANAGED_PROFILE";
    public static final String ACTION_PROVISION_MANAGED_USER = "android.app.action.PROVISION_MANAGED_USER";
    public static final String ACTION_REMOTE_BUGREPORT_DISPATCH = "android.intent.action.REMOTE_BUGREPORT_DISPATCH";
    @SystemApi
    public static final String ACTION_RESET_PROTECTION_POLICY_CHANGED = "android.app.action.RESET_PROTECTION_POLICY_CHANGED";
    @SystemApi
    public static final String ACTION_ROLE_HOLDER_PROVISION_FINALIZATION = "android.app.action.ROLE_HOLDER_PROVISION_FINALIZATION";
    @SystemApi
    public static final String ACTION_ROLE_HOLDER_PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE = "android.app.action.ROLE_HOLDER_PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE";
    @SystemApi
    public static final String ACTION_ROLE_HOLDER_PROVISION_MANAGED_PROFILE = "android.app.action.ROLE_HOLDER_PROVISION_MANAGED_PROFILE";
    public static final String ACTION_SET_NEW_PARENT_PROFILE_PASSWORD = "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD";
    public static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";
    @SystemApi
    public static final String ACTION_SET_PROFILE_OWNER = "android.app.action.SET_PROFILE_OWNER";
    public static final String ACTION_SHOW_DEVICE_MONITORING_DIALOG = "android.app.action.SHOW_DEVICE_MONITORING_DIALOG";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String ACTION_SHOW_NEW_USER_DISCLAIMER = "android.app.action.SHOW_NEW_USER_DISCLAIMER";
    public static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION";
    @SystemApi
    @Deprecated
    public static final String ACTION_STATE_USER_SETUP_COMPLETE = "android.app.action.STATE_USER_SETUP_COMPLETE";
    public static final String ACTION_SYSTEM_UPDATE_POLICY_CHANGED = "android.app.action.SYSTEM_UPDATE_POLICY_CHANGED";
    @SystemApi
    public static final String ACTION_UPDATE_DEVICE_POLICY_MANAGEMENT_ROLE_HOLDER = "android.app.action.UPDATE_DEVICE_POLICY_MANAGEMENT_ROLE_HOLDER";
    public static final String ADD_ISFINANCED_DEVICE_FLAG = "add-isfinanced-device";
    public static final boolean ADD_ISFINANCED_FEVICE_DEFAULT = true;
    public static final long DEFAULT_STRONG_AUTH_TIMEOUT_MS = 259200000;
    public static final String DELEGATION_APP_RESTRICTIONS = "delegation-app-restrictions";
    public static final String DELEGATION_BLOCK_UNINSTALL = "delegation-block-uninstall";
    public static final String DELEGATION_CERT_INSTALL = "delegation-cert-install";
    public static final String DELEGATION_CERT_SELECTION = "delegation-cert-selection";
    public static final String DELEGATION_ENABLE_SYSTEM_APP = "delegation-enable-system-app";
    public static final String DELEGATION_INSTALL_EXISTING_PACKAGE = "delegation-install-existing-package";
    public static final String DELEGATION_KEEP_UNINSTALLED_PACKAGES = "delegation-keep-uninstalled-packages";
    public static final String DELEGATION_NETWORK_LOGGING = "delegation-network-logging";
    public static final String DELEGATION_PACKAGE_ACCESS = "delegation-package-access";
    public static final String DELEGATION_PERMISSION_GRANT = "delegation-permission-grant";
    public static final String DELEGATION_SECURITY_LOGGING = "delegation-security-logging";
    public static final boolean DEPRECATE_USERMANAGERINTERNAL_DEVICEPOLICY_DEFAULT = true;
    public static final String DEPRECATE_USERMANAGERINTERNAL_DEVICEPOLICY_FLAG = "deprecate_usermanagerinternal_devicepolicy";
    public static final int DEVICE_OWNER_TYPE_DEFAULT = 0;
    public static final int DEVICE_OWNER_TYPE_FINANCED = 1;
    @Deprecated
    public static final int ENCRYPTION_STATUS_ACTIVATING = 2;
    public static final int ENCRYPTION_STATUS_ACTIVE = 3;
    public static final int ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY = 4;
    public static final int ENCRYPTION_STATUS_ACTIVE_PER_USER = 5;
    public static final int ENCRYPTION_STATUS_INACTIVE = 1;
    public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
    public static final int ERROR_PACKAGE_NAME_NOT_FOUND = 1;
    public static final int ERROR_VPN_PACKAGE_NOT_FOUND = 1;
    @SystemApi
    public static final int EXEMPT_FROM_ACTIVITY_BG_START_RESTRICTION = 2;
    @SystemApi
    public static final int EXEMPT_FROM_DISMISSIBLE_NOTIFICATIONS = 1;
    @SystemApi
    public static final int EXEMPT_FROM_HIBERNATION = 3;
    @SystemApi
    public static final int EXEMPT_FROM_POWER_RESTRICTIONS = 4;
    @SystemApi
    public static final int EXEMPT_FROM_SUSPENSION = 0;
    public static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION";
    public static final String EXTRA_BUGREPORT_NOTIFICATION_TYPE = "android.app.extra.bugreport_notification_type";
    public static final String EXTRA_DELEGATION_SCOPES = "android.app.extra.DELEGATION_SCOPES";
    public static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN";
    public static final String EXTRA_DEVICE_PASSWORD_REQUIREMENT_ONLY = "android.app.extra.DEVICE_PASSWORD_REQUIREMENT_ONLY";
    @SystemApi
    public static final String EXTRA_FORCE_UPDATE_ROLE_HOLDER = "android.app.extra.FORCE_UPDATE_ROLE_HOLDER";
    @SystemApi
    public static final String EXTRA_LOST_MODE_LOCATION = "android.app.extra.LOST_MODE_LOCATION";
    public static final String EXTRA_PASSWORD_COMPLEXITY = "android.app.extra.PASSWORD_COMPLEXITY";
    @SystemApi
    public static final String EXTRA_PROFILE_OWNER_NAME = "android.app.extra.PROFILE_OWNER_NAME";
    public static final String EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE = "android.app.extra.PROVISIONING_ACCOUNT_TO_MIGRATE";
    public static final String EXTRA_PROVISIONING_ACTION = "android.app.extra.PROVISIONING_ACTION";
    public static final String EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE = "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE";
    public static final String EXTRA_PROVISIONING_ALLOWED_PROVISIONING_MODES = "android.app.extra.PROVISIONING_ALLOWED_PROVISIONING_MODES";
    public static final String EXTRA_PROVISIONING_ALLOW_OFFLINE = "android.app.extra.PROVISIONING_ALLOW_OFFLINE";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE = "android.app.extra.PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION";
    @SystemApi
    @Deprecated
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_ICON_URI = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_ICON_URI";
    @SystemApi
    @Deprecated
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_LABEL = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_LABEL";
    @Deprecated
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM";
    public static final String EXTRA_PROVISIONING_DISCLAIMERS = "android.app.extra.PROVISIONING_DISCLAIMERS";
    public static final String EXTRA_PROVISIONING_DISCLAIMER_CONTENT = "android.app.extra.PROVISIONING_DISCLAIMER_CONTENT";
    public static final String EXTRA_PROVISIONING_DISCLAIMER_HEADER = "android.app.extra.PROVISIONING_DISCLAIMER_HEADER";
    @Deprecated
    public static final String EXTRA_PROVISIONING_EMAIL_ADDRESS = "android.app.extra.PROVISIONING_EMAIL_ADDRESS";
    public static final String EXTRA_PROVISIONING_IMEI = "android.app.extra.PROVISIONING_IMEI";
    public static final String EXTRA_PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION = "android.app.extra.PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION";
    @Deprecated
    public static final String EXTRA_PROVISIONING_KEEP_SCREEN_ON = "android.app.extra.PROVISIONING_KEEP_SCREEN_ON";
    public static final String EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED = "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED";
    public static final String EXTRA_PROVISIONING_LOCALE = "android.app.extra.PROVISIONING_LOCALE";
    public static final String EXTRA_PROVISIONING_LOCAL_TIME = "android.app.extra.PROVISIONING_LOCAL_TIME";
    @Deprecated
    public static final String EXTRA_PROVISIONING_LOGO_URI = "android.app.extra.PROVISIONING_LOGO_URI";
    @Deprecated
    public static final String EXTRA_PROVISIONING_MAIN_COLOR = "android.app.extra.PROVISIONING_MAIN_COLOR";
    public static final String EXTRA_PROVISIONING_MODE = "android.app.extra.PROVISIONING_MODE";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ORGANIZATION_NAME = "android.app.extra.PROVISIONING_ORGANIZATION_NAME";
    @SystemApi
    public static final String EXTRA_PROVISIONING_RETURN_BEFORE_POLICY_COMPLIANCE = "android.app.extra.PROVISIONING_RETURN_BEFORE_POLICY_COMPLIANCE";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ROLE_HOLDER_CUSTOM_USER_CONSENT_INTENT = "android.app.extra.PROVISIONING_ROLE_HOLDER_CUSTOM_USER_CONSENT_INTENT";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ROLE_HOLDER_EXTRAS_BUNDLE = "android.app.extra.PROVISIONING_ROLE_HOLDER_EXTRAS_BUNDLE";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ROLE_HOLDER_PACKAGE_DOWNLOAD_COOKIE_HEADER = "android.app.extra.PROVISIONING_ROLE_HOLDER_PACKAGE_DOWNLOAD_COOKIE_HEADER";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ROLE_HOLDER_PACKAGE_DOWNLOAD_LOCATION = "android.app.extra.PROVISIONING_ROLE_HOLDER_PACKAGE_DOWNLOAD_LOCATION";
    @SystemApi
    public static final String EXTRA_PROVISIONING_ROLE_HOLDER_SIGNATURE_CHECKSUM = "android.app.extra.PROVISIONING_ROLE_HOLDER_SIGNATURE_CHECKSUM";
    public static final String EXTRA_PROVISIONING_SENSORS_PERMISSION_GRANT_OPT_OUT = "android.app.extra.PROVISIONING_SENSORS_PERMISSION_GRANT_OPT_OUT";
    public static final String EXTRA_PROVISIONING_SERIAL_NUMBER = "android.app.extra.PROVISIONING_SERIAL_NUMBER";
    public static final String EXTRA_PROVISIONING_SHOULD_LAUNCH_RESULT_INTENT = "android.app.extra.PROVISIONING_SHOULD_LAUNCH_RESULT_INTENT";
    public static final String EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS = "android.app.extra.PROVISIONING_SKIP_EDUCATION_SCREENS";
    public static final String EXTRA_PROVISIONING_SKIP_ENCRYPTION = "android.app.extra.PROVISIONING_SKIP_ENCRYPTION";
    @SystemApi
    public static final String EXTRA_PROVISIONING_SKIP_OWNERSHIP_DISCLAIMER = "android.app.extra.PROVISIONING_SKIP_OWNERSHIP_DISCLAIMER";
    @Deprecated
    public static final String EXTRA_PROVISIONING_SKIP_USER_CONSENT = "android.app.extra.PROVISIONING_SKIP_USER_CONSENT";
    @SystemApi
    public static final String EXTRA_PROVISIONING_SUPPORTED_MODES = "android.app.extra.PROVISIONING_SUPPORTED_MODES";
    @SystemApi
    public static final String EXTRA_PROVISIONING_SUPPORT_URL = "android.app.extra.PROVISIONING_SUPPORT_URL";
    public static final String EXTRA_PROVISIONING_TIME_ZONE = "android.app.extra.PROVISIONING_TIME_ZONE";
    @SystemApi
    public static final String EXTRA_PROVISIONING_TRIGGER = "android.app.extra.PROVISIONING_TRIGGER";
    public static final String EXTRA_PROVISIONING_USE_MOBILE_DATA = "android.app.extra.PROVISIONING_USE_MOBILE_DATA";
    public static final String EXTRA_PROVISIONING_WIFI_ANONYMOUS_IDENTITY = "android.app.extra.PROVISIONING_WIFI_ANONYMOUS_IDENTITY";
    public static final String EXTRA_PROVISIONING_WIFI_CA_CERTIFICATE = "android.app.extra.PROVISIONING_WIFI_CA_CERTIFICATE";
    public static final String EXTRA_PROVISIONING_WIFI_DOMAIN = "android.app.extra.PROVISIONING_WIFI_DOMAIN";
    public static final String EXTRA_PROVISIONING_WIFI_EAP_METHOD = "android.app.extra.PROVISIONING_WIFI_EAP_METHOD";
    public static final String EXTRA_PROVISIONING_WIFI_HIDDEN = "android.app.extra.PROVISIONING_WIFI_HIDDEN";
    public static final String EXTRA_PROVISIONING_WIFI_IDENTITY = "android.app.extra.PROVISIONING_WIFI_IDENTITY";
    public static final String EXTRA_PROVISIONING_WIFI_PAC_URL = "android.app.extra.PROVISIONING_WIFI_PAC_URL";
    public static final String EXTRA_PROVISIONING_WIFI_PASSWORD = "android.app.extra.PROVISIONING_WIFI_PASSWORD";
    public static final String EXTRA_PROVISIONING_WIFI_PHASE2_AUTH = "android.app.extra.PROVISIONING_WIFI_PHASE2_AUTH";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_BYPASS = "android.app.extra.PROVISIONING_WIFI_PROXY_BYPASS";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_HOST = "android.app.extra.PROVISIONING_WIFI_PROXY_HOST";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_PORT = "android.app.extra.PROVISIONING_WIFI_PROXY_PORT";
    public static final String EXTRA_PROVISIONING_WIFI_SECURITY_TYPE = "android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE";
    public static final String EXTRA_PROVISIONING_WIFI_SSID = "android.app.extra.PROVISIONING_WIFI_SSID";
    public static final String EXTRA_PROVISIONING_WIFI_USER_CERTIFICATE = "android.app.extra.PROVISIONING_WIFI_USER_CERTIFICATE";
    public static final String EXTRA_REMOTE_BUGREPORT_HASH = "android.intent.extra.REMOTE_BUGREPORT_HASH";
    public static final String EXTRA_REMOTE_BUGREPORT_NONCE = "android.intent.extra.REMOTE_BUGREPORT_NONCE";
    public static final String EXTRA_RESOURCE_IDS = "android.app.extra.RESOURCE_IDS";
    public static final String EXTRA_RESOURCE_TYPE = "android.app.extra.RESOURCE_TYPE";
    public static final int EXTRA_RESOURCE_TYPE_DRAWABLE = 1;
    public static final int EXTRA_RESOURCE_TYPE_STRING = 2;
    @SystemApi
    public static final String EXTRA_RESTRICTION = "android.app.extra.RESTRICTION";
    public static final String EXTRA_RESULT_LAUNCH_INTENT = "android.app.extra.RESULT_LAUNCH_INTENT";
    @SystemApi
    public static final String EXTRA_ROLE_HOLDER_PROVISIONING_INITIATOR_PACKAGE = "android.app.extra.ROLE_HOLDER_PROVISIONING_INITIATOR_PACKAGE";
    @SystemApi
    public static final String EXTRA_ROLE_HOLDER_STATE = "android.app.extra.ROLE_HOLDER_STATE";
    public static final String EXTRA_ROLE_HOLDER_UPDATE_FAILURE_STRATEGY = "android.app.extra.ROLE_HOLDER_UPDATE_FAILURE_STRATEGY";
    @SystemApi
    public static final String EXTRA_ROLE_HOLDER_UPDATE_RESULT_CODE = "android.app.extra.ROLE_HOLDER_UPDATE_RESULT_CODE";
    public static final int FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY = 1;
    public static final int FLAG_MANAGED_CAN_ACCESS_PARENT = 2;
    public static final int FLAG_PARENT_CAN_ACCESS_MANAGED = 1;
    @SystemApi
    public static final int FLAG_SUPPORTED_MODES_DEVICE_OWNER = 4;
    @SystemApi
    public static final int FLAG_SUPPORTED_MODES_ORGANIZATION_OWNED = 1;
    @SystemApi
    public static final int FLAG_SUPPORTED_MODES_PERSONALLY_OWNED = 2;
    public static final int ID_TYPE_BASE_INFO = 1;
    public static final int ID_TYPE_IMEI = 4;
    public static final int ID_TYPE_INDIVIDUAL_ATTESTATION = 16;
    public static final int ID_TYPE_MEID = 8;
    public static final int ID_TYPE_SERIAL = 2;
    public static final int INSTALLKEY_REQUEST_CREDENTIALS_ACCESS = 1;
    public static final int INSTALLKEY_SET_USER_SELECTABLE = 2;
    public static final int KEYGUARD_DISABLE_BIOMETRICS = 416;
    public static final int KEYGUARD_DISABLE_FACE = 128;
    public static final int KEYGUARD_DISABLE_FEATURES_ALL = Integer.MAX_VALUE;
    public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
    public static final int KEYGUARD_DISABLE_FINGERPRINT = 32;
    public static final int KEYGUARD_DISABLE_IRIS = 256;
    @Deprecated
    public static final int KEYGUARD_DISABLE_REMOTE_INPUT = 64;
    public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2;
    public static final int KEYGUARD_DISABLE_SECURE_NOTIFICATIONS = 4;
    public static final int KEYGUARD_DISABLE_SHORTCUTS_ALL = 512;
    public static final int KEYGUARD_DISABLE_TRUST_AGENTS = 16;
    public static final int KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS = 8;
    public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1;
    public static final int KEY_GEN_STRONGBOX_UNAVAILABLE = 1;
    public static final int LEAVE_ALL_SYSTEM_APPS_ENABLED = 16;
    public static final int LOCK_TASK_FEATURE_BLOCK_ACTIVITY_START_IN_TASK = 64;
    public static final int LOCK_TASK_FEATURE_GLOBAL_ACTIONS = 16;
    public static final int LOCK_TASK_FEATURE_HOME = 4;
    public static final int LOCK_TASK_FEATURE_KEYGUARD = 32;
    public static final int LOCK_TASK_FEATURE_NONE = 0;
    public static final int LOCK_TASK_FEATURE_NOTIFICATIONS = 2;
    public static final int LOCK_TASK_FEATURE_OVERVIEW = 8;
    public static final int LOCK_TASK_FEATURE_SYSTEM_INFO = 1;
    public static final int MAKE_USER_DEMO = 4;
    public static final int MAKE_USER_EPHEMERAL = 2;
    public static final int MAX_PASSWORD_LENGTH = 16;
    public static final String MIME_TYPE_PROVISIONING_NFC = "application/com.android.managedprovisioning";
    public static final int MTE_DISABLED = 2;
    public static final int MTE_ENABLED = 1;
    public static final int MTE_NOT_CONTROLLED_BY_POLICY = 0;
    public static final int NEARBY_STREAMING_DISABLED = 1;
    public static final int NEARBY_STREAMING_ENABLED = 2;
    public static final int NEARBY_STREAMING_NOT_CONTROLLED_BY_POLICY = 0;
    public static final int NEARBY_STREAMING_SAME_MANAGED_ACCOUNT_ONLY = 3;
    public static final int NON_ORG_OWNED_PROFILE_KEYGUARD_FEATURES_AFFECT_OWNER = 432;
    public static final int NOTIFICATION_BUGREPORT_ACCEPTED_NOT_FINISHED = 2;
    public static final int NOTIFICATION_BUGREPORT_FINISHED_NOT_ACCEPTED = 3;
    public static final int NOTIFICATION_BUGREPORT_STARTED = 1;
    public static final int OPERATION_CLEAR_APPLICATION_USER_DATA = 23;
    public static final int OPERATION_CREATE_AND_MANAGE_USER = 5;
    public static final int OPERATION_INSTALL_CA_CERT = 24;
    public static final int OPERATION_INSTALL_KEY_PAIR = 25;
    public static final int OPERATION_INSTALL_SYSTEM_UPDATE = 26;
    public static final int OPERATION_LOCK_NOW = 1;
    public static final int OPERATION_LOGOUT_USER = 9;
    public static final int OPERATION_REBOOT = 7;
    public static final int OPERATION_REMOVE_ACTIVE_ADMIN = 27;
    public static final int OPERATION_REMOVE_KEY_PAIR = 28;
    public static final int OPERATION_REMOVE_USER = 6;
    public static final int OPERATION_REQUEST_BUGREPORT = 29;
    public static final int OPERATION_SAFETY_REASON_DRIVING_DISTRACTION = 1;
    public static final int OPERATION_SAFETY_REASON_NONE = -1;
    public static final int OPERATION_SET_ALWAYS_ON_VPN_PACKAGE = 30;
    public static final int OPERATION_SET_APPLICATION_HIDDEN = 15;
    public static final int OPERATION_SET_APPLICATION_RESTRICTIONS = 16;
    public static final int OPERATION_SET_CAMERA_DISABLED = 31;
    public static final int OPERATION_SET_FACTORY_RESET_PROTECTION_POLICY = 32;
    public static final int OPERATION_SET_GLOBAL_PRIVATE_DNS = 33;
    public static final int OPERATION_SET_KEEP_UNINSTALLED_PACKAGES = 17;
    public static final int OPERATION_SET_KEYGUARD_DISABLED = 12;
    public static final int OPERATION_SET_LOCK_TASK_FEATURES = 18;
    public static final int OPERATION_SET_LOCK_TASK_PACKAGES = 19;
    public static final int OPERATION_SET_LOGOUT_ENABLED = 34;
    public static final int OPERATION_SET_MASTER_VOLUME_MUTED = 35;
    public static final int OPERATION_SET_OVERRIDE_APNS_ENABLED = 36;
    public static final int OPERATION_SET_PACKAGES_SUSPENDED = 20;
    public static final int OPERATION_SET_PERMISSION_GRANT_STATE = 37;
    public static final int OPERATION_SET_PERMISSION_POLICY = 38;
    public static final int OPERATION_SET_RESTRICTIONS_PROVIDER = 39;
    public static final int OPERATION_SET_STATUS_BAR_DISABLED = 13;
    public static final int OPERATION_SET_SYSTEM_SETTING = 11;
    public static final int OPERATION_SET_SYSTEM_UPDATE_POLICY = 14;
    public static final int OPERATION_SET_TRUST_AGENT_CONFIGURATION = 21;
    public static final int OPERATION_SET_USER_CONTROL_DISABLED_PACKAGES = 22;
    public static final int OPERATION_SET_USER_RESTRICTION = 10;
    public static final int OPERATION_START_USER_IN_BACKGROUND = 3;
    public static final int OPERATION_STOP_USER = 4;
    public static final int OPERATION_SWITCH_USER = 2;
    public static final int OPERATION_UNINSTALL_CA_CERT = 40;
    public static final int OPERATION_WIPE_DATA = 8;
    public static final int ORG_OWNED_PROFILE_KEYGUARD_FEATURES_PARENT_ONLY = 518;
    public static final int PASSWORD_COMPLEXITY_HIGH = 327680;
    public static final int PASSWORD_COMPLEXITY_LOW = 65536;
    public static final int PASSWORD_COMPLEXITY_MEDIUM = 196608;
    public static final int PASSWORD_COMPLEXITY_NONE = 0;
    public static final int PASSWORD_QUALITY_ALPHABETIC = 262144;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768;
    public static final int PASSWORD_QUALITY_COMPLEX = 393216;
    public static final int PASSWORD_QUALITY_MANAGED = 524288;
    public static final int PASSWORD_QUALITY_NUMERIC = 131072;
    public static final int PASSWORD_QUALITY_NUMERIC_COMPLEX = 196608;
    public static final int PASSWORD_QUALITY_SOMETHING = 65536;
    public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
    public static final int PERMISSION_GRANT_STATE_DEFAULT = 0;
    public static final int PERMISSION_GRANT_STATE_DENIED = 2;
    public static final int PERMISSION_GRANT_STATE_GRANTED = 1;
    public static final int PERMISSION_POLICY_AUTO_DENY = 2;
    public static final int PERMISSION_POLICY_AUTO_GRANT = 1;
    public static final int PERMISSION_POLICY_PROMPT = 0;
    public static final int PERSONAL_APPS_NOT_SUSPENDED = 0;
    public static final int PERSONAL_APPS_SUSPENDED_EXPLICITLY = 1;
    public static final int PERSONAL_APPS_SUSPENDED_PROFILE_TIMEOUT = 2;
    public static final String POLICY_DISABLE_CAMERA = "policy_disable_camera";
    public static final String POLICY_DISABLE_SCREEN_CAPTURE = "policy_disable_screen_capture";
    public static final String POLICY_SUSPEND_PACKAGES = "policy_suspend_packages";
    public static final boolean PREFERENTIAL_NETWORK_SERVICE_ENABLED_DEFAULT = false;
    private static final String PREFIX_OPERATION = "OPERATION_";
    private static final String PREFIX_OPERATION_SAFETY_REASON = "OPERATION_SAFETY_REASON_";
    public static final int PRIVATE_DNS_MODE_OFF = 1;
    public static final int PRIVATE_DNS_MODE_OPPORTUNISTIC = 2;
    public static final int PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = 3;
    public static final int PRIVATE_DNS_MODE_UNKNOWN = 0;
    public static final int PRIVATE_DNS_SET_ERROR_FAILURE_SETTING = 2;
    public static final int PRIVATE_DNS_SET_ERROR_HOST_NOT_SERVING = 1;
    public static final int PRIVATE_DNS_SET_NO_ERROR = 0;
    public static final int PROFILE_KEYGUARD_FEATURES_AFFECT_OWNER = 950;
    public static final int PROVISIONING_MODE_FULLY_MANAGED_DEVICE = 1;
    public static final int PROVISIONING_MODE_MANAGED_PROFILE = 2;
    public static final int PROVISIONING_MODE_MANAGED_PROFILE_ON_PERSONAL_DEVICE = 3;
    @SystemApi
    public static final int PROVISIONING_TRIGGER_CLOUD_ENROLLMENT = 1;
    @SystemApi
    public static final int PROVISIONING_TRIGGER_MANAGED_ACCOUNT = 4;
    @SystemApi
    public static final int PROVISIONING_TRIGGER_NFC = 5;
    @SystemApi
    @Deprecated
    public static final int PROVISIONING_TRIGGER_PERSISTENT_DEVICE_OWNER = 3;
    @SystemApi
    public static final int PROVISIONING_TRIGGER_QR_CODE = 2;
    @SystemApi
    public static final int PROVISIONING_TRIGGER_UNSPECIFIED = 0;
    @SystemApi
    public static final String REQUIRED_APP_MANAGED_DEVICE = "android.app.REQUIRED_APP_MANAGED_DEVICE";
    @SystemApi
    public static final String REQUIRED_APP_MANAGED_PROFILE = "android.app.REQUIRED_APP_MANAGED_PROFILE";
    @SystemApi
    public static final String REQUIRED_APP_MANAGED_USER = "android.app.REQUIRED_APP_MANAGED_USER";
    public static final int RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT = 2;
    public static final int RESET_PASSWORD_REQUIRE_ENTRY = 1;
    @SystemApi
    public static final int RESULT_DEVICE_OWNER_SET = 123;
    @SystemApi

    /* renamed from: RESULT_UPDATE_DEVICE_POLICY_MANAGEMENT_ROLE_HOLDER_PROVISIONING_DISABLED */
    public static final int f21x11afbb80 = 3;
    @SystemApi

    /* renamed from: RESULT_UPDATE_DEVICE_POLICY_MANAGEMENT_ROLE_HOLDER_RECOVERABLE_ERROR */
    public static final int f22x60c7bbf9 = 1;
    @SystemApi

    /* renamed from: RESULT_UPDATE_DEVICE_POLICY_MANAGEMENT_ROLE_HOLDER_UNRECOVERABLE_ERROR */
    public static final int f23xfc76a200 = 2;
    @SystemApi
    public static final int RESULT_UPDATE_ROLE_HOLDER = 2;
    @SystemApi
    public static final int RESULT_WORK_PROFILE_CREATED = 122;
    public static final int ROLE_HOLDER_UPDATE_FAILURE_STRATEGY_FAIL_PROVISIONING = 1;

    /* renamed from: ROLE_HOLDER_UPDATE_FAILURE_STRATEGY_FALLBACK_TO_PLATFORM_PROVISIONING */
    public static final int f24xa2719067 = 2;
    public static final int SKIP_SETUP_WIZARD = 1;
    @SystemApi
    public static final int STATE_USER_PROFILE_COMPLETE = 4;
    @SystemApi
    public static final int STATE_USER_PROFILE_FINALIZED = 5;
    @SystemApi
    public static final int STATE_USER_SETUP_COMPLETE = 2;
    @SystemApi
    public static final int STATE_USER_SETUP_FINALIZED = 3;
    @SystemApi
    public static final int STATE_USER_SETUP_INCOMPLETE = 1;
    @SystemApi
    public static final int STATE_USER_UNMANAGED = 0;
    @SystemApi
    public static final int STATUS_ACCOUNTS_NOT_EMPTY = 6;
    @SystemApi
    public static final int STATUS_CANNOT_ADD_MANAGED_PROFILE = 11;
    @SystemApi
    public static final int STATUS_DEVICE_ADMIN_NOT_SUPPORTED = 13;
    @SystemApi
    public static final int STATUS_HAS_DEVICE_OWNER = 1;
    @SystemApi
    public static final int STATUS_HAS_PAIRED = 8;
    @SystemApi
    public static final int STATUS_HEADLESS_SYSTEM_USER_MODE_NOT_SUPPORTED = 16;
    @SystemApi
    public static final int STATUS_MANAGED_USERS_NOT_SUPPORTED = 9;
    @SystemApi
    public static final int STATUS_NONSYSTEM_USER_EXISTS = 5;
    @SystemApi
    public static final int STATUS_NOT_SYSTEM_USER = 7;
    @SystemApi
    public static final int STATUS_OK = 0;
    @SystemApi
    public static final int STATUS_PROVISIONING_NOT_ALLOWED_FOR_NON_DEVELOPER_USERS = 15;
    @Deprecated
    public static final int STATUS_SPLIT_SYSTEM_USER_DEVICE_SYSTEM_USER = 14;
    @SystemApi
    public static final int STATUS_SYSTEM_USER = 10;
    @SystemApi
    public static final int STATUS_UNKNOWN_ERROR = -1;
    @SystemApi
    public static final int STATUS_USER_HAS_PROFILE_OWNER = 2;
    @SystemApi
    public static final int STATUS_USER_NOT_RUNNING = 3;
    @SystemApi
    public static final int STATUS_USER_SETUP_COMPLETED = 4;
    public static final int WIFI_SECURITY_ENTERPRISE_192 = 3;
    public static final int WIFI_SECURITY_ENTERPRISE_EAP = 2;
    public static final int WIFI_SECURITY_OPEN = 0;
    public static final int WIFI_SECURITY_PERSONAL = 1;
    public static final int WIPE_EUICC = 4;
    public static final int WIPE_EXTERNAL_STORAGE = 1;
    public static final int WIPE_RESET_PROTECTION_DATA = 2;
    public static final int WIPE_SILENTLY = 8;
    private final Context mContext;
    private final IpcDataCache<Void, CharSequence> mGetDeviceOwnerOrganizationNameCache;
    private IpcDataCache<Pair<ComponentName, Integer>, Integer> mGetKeyGuardDisabledFeaturesCache;
    private final IpcDataCache<Integer, CharSequence> mGetOrganizationNameForUserCache;
    private final IpcDataCache<UserHandle, ComponentName> mGetProfileOwnerOrDeviceOwnerSupervisionComponentCache;
    private IpcDataCache<Void, Boolean> mHasDeviceOwnerCache;
    private IpcDataCache<ComponentName, Boolean> mIsNetworkLoggingEnabledCache;
    private final IpcDataCache<Void, Boolean> mIsOrganizationOwnedDeviceWithManagedProfileCache;
    private final boolean mParentInstance;
    private final DevicePolicyResourcesManager mResourcesManager;
    private final IDevicePolicyManager mService;
    private static String TAG = "DevicePolicyManager";
    private static final IpcDataCache.Config sDpmCaches = new IpcDataCache.Config(8, "system_server", "DevicePolicyManagerCaches");

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ApplicationExemptionConstants {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AttestationIdType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CreateAndManageUserFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeviceOwnerType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DevicePolicyOperation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface InstallUpdateCallbackErrorConstants {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LockNowFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LockTaskFeature {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MtePolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NearbyStreamingPolicy {
    }

    /* loaded from: classes.dex */
    public interface OnClearApplicationUserDataListener {
        void onApplicationUserDataCleared(String str, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface OperationSafetyReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PasswordComplexity {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PermissionGrantState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PersonalAppsSuspensionReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PrivateDnsMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PrivateDnsModeErrorCodes {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProvisioningConfiguration {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProvisioningPrecondition {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProvisioningTrigger {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RoleHolderUpdateFailureStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SystemSettingsWhitelist {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UserProvisioningState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface WifiSecurity {
    }

    public DevicePolicyManager(Context context, IDevicePolicyManager service) {
        this(context, service, false);
    }

    protected DevicePolicyManager(Context context, IDevicePolicyManager service, boolean parentInstance) {
        IpcDataCache.Config config = sDpmCaches;
        this.mGetKeyGuardDisabledFeaturesCache = new IpcDataCache<>(config.child("getKeyguardDisabledFeatures"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda5
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                Integer lambda$new$2;
                lambda$new$2 = DevicePolicyManager.this.lambda$new$2((Pair) obj);
                return lambda$new$2;
            }
        });
        this.mHasDeviceOwnerCache = new IpcDataCache<>(config.child("hasDeviceOwner"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda6
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                Boolean lambda$new$3;
                lambda$new$3 = DevicePolicyManager.this.lambda$new$3((Void) obj);
                return lambda$new$3;
            }
        });
        this.mGetProfileOwnerOrDeviceOwnerSupervisionComponentCache = new IpcDataCache<>(config.child("getProfileOwnerOrDeviceOwnerSupervisionComponent"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda7
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                ComponentName lambda$new$4;
                lambda$new$4 = DevicePolicyManager.this.lambda$new$4((UserHandle) obj);
                return lambda$new$4;
            }
        });
        this.mIsOrganizationOwnedDeviceWithManagedProfileCache = new IpcDataCache<>(config.child("isOrganizationOwnedDeviceWithManagedProfile"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda8
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                Object lambda$new$5;
                lambda$new$5 = DevicePolicyManager.this.lambda$new$5(obj);
                return lambda$new$5;
            }
        });
        this.mGetDeviceOwnerOrganizationNameCache = new IpcDataCache<>(config.child("getDeviceOwnerOrganizationName"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda9
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                Object lambda$new$8;
                lambda$new$8 = DevicePolicyManager.this.lambda$new$8(obj);
                return lambda$new$8;
            }
        });
        this.mGetOrganizationNameForUserCache = new IpcDataCache<>(config.child("getOrganizationNameForUser"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda10
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                CharSequence lambda$new$9;
                lambda$new$9 = DevicePolicyManager.this.lambda$new$9((Integer) obj);
                return lambda$new$9;
            }
        });
        this.mIsNetworkLoggingEnabledCache = new IpcDataCache<>(config.child("isNetworkLoggingEnabled"), new IpcDataCache.RemoteCall() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda11
            @Override // android.p008os.IpcDataCache.RemoteCall
            public final Object apply(Object obj) {
                Boolean lambda$new$10;
                lambda$new$10 = DevicePolicyManager.this.lambda$new$10((ComponentName) obj);
                return lambda$new$10;
            }
        });
        this.mContext = context;
        this.mService = service;
        this.mParentInstance = parentInstance;
        this.mResourcesManager = new DevicePolicyResourcesManager(context, service);
    }

    private final IDevicePolicyManager getService() {
        return this.mService;
    }

    private final boolean isParentInstance() {
        return this.mParentInstance;
    }

    private final Context getContext() {
        return this.mContext;
    }

    protected int myUserId() {
        return this.mContext.getUserId();
    }

    /* loaded from: classes.dex */
    public static abstract class InstallSystemUpdateCallback {
        public static final int UPDATE_ERROR_BATTERY_LOW = 5;
        public static final int UPDATE_ERROR_FILE_NOT_FOUND = 4;
        public static final int UPDATE_ERROR_INCORRECT_OS_VERSION = 2;
        public static final int UPDATE_ERROR_UNKNOWN = 1;
        public static final int UPDATE_ERROR_UPDATE_FILE_INVALID = 3;

        public void onInstallUpdateError(int errorCode, String errorMessage) {
        }
    }

    public static String operationToString(int operation) {
        return DebugUtils.constantToString(DevicePolicyManager.class, PREFIX_OPERATION, operation);
    }

    public void setMtePolicy(int policy) {
        throwIfParentInstance("setMtePolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMtePolicy(policy, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int getMtePolicy() {
        throwIfParentInstance("setMtePolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getMtePolicy(this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public static void invalidateBinderCaches() {
        sDpmCaches.invalidateCache();
    }

    public static void disableLocalCaches() {
        sDpmCaches.disableAllForCurrentProcess();
    }

    public static String operationSafetyReasonToString(int reason) {
        return DebugUtils.constantToString(DevicePolicyManager.class, PREFIX_OPERATION_SAFETY_REASON, reason);
    }

    public static boolean isValidOperationSafetyReason(int reason) {
        return reason == 1;
    }

    public boolean isSafeOperation(int reason) {
        throwIfParentInstance("isSafeOperation");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.isSafeOperation(reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void acknowledgeNewUserDisclaimer() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.acknowledgeNewUserDisclaimer(this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isNewUserDisclaimerAcknowledged() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isNewUserDisclaimerAcknowledged(this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isAdminActive(ComponentName admin) {
        throwIfParentInstance("isAdminActive");
        return isAdminActiveAsUser(admin, myUserId());
    }

    public boolean isAdminActiveAsUser(ComponentName admin, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isAdminActive(admin, userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isRemovingAdmin(ComponentName admin, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isRemovingAdmin(admin, userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<ComponentName> getActiveAdmins() {
        throwIfParentInstance("getActiveAdmins");
        return getActiveAdminsAsUser(myUserId());
    }

    public List<ComponentName> getActiveAdminsAsUser(int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getActiveAdmins(userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    public boolean packageHasActiveAdmins(String packageName) {
        return packageHasActiveAdmins(packageName, myUserId());
    }

    public boolean packageHasActiveAdmins(String packageName, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.packageHasActiveAdmins(packageName, userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void removeActiveAdmin(ComponentName admin) {
        throwIfParentInstance("removeActiveAdmin");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.removeActiveAdmin(admin, myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean hasGrantedPolicy(ComponentName admin, int usesPolicy) {
        throwIfParentInstance("hasGrantedPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.hasGrantedPolicy(admin, usesPolicy, myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @Deprecated
    public void setPasswordQuality(ComponentName admin, int quality) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordQuality(admin, quality, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordQuality(ComponentName admin) {
        return getPasswordQuality(admin, myUserId());
    }

    public int getPasswordQuality(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordQuality(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumLength(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLength(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumLength(ComponentName admin) {
        return getPasswordMinimumLength(admin, myUserId());
    }

    public int getPasswordMinimumLength(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumLength(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumUpperCase(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumUpperCase(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumUpperCase(ComponentName admin) {
        return getPasswordMinimumUpperCase(admin, myUserId());
    }

    public int getPasswordMinimumUpperCase(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumUpperCase(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumLowerCase(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLowerCase(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumLowerCase(ComponentName admin) {
        return getPasswordMinimumLowerCase(admin, myUserId());
    }

    public int getPasswordMinimumLowerCase(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumLowerCase(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumLetters(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumLetters(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumLetters(ComponentName admin) {
        return getPasswordMinimumLetters(admin, myUserId());
    }

    public int getPasswordMinimumLetters(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumLetters(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumNumeric(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumNumeric(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumNumeric(ComponentName admin) {
        return getPasswordMinimumNumeric(admin, myUserId());
    }

    public int getPasswordMinimumNumeric(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumNumeric(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumSymbols(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumSymbols(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumSymbols(ComponentName admin) {
        return getPasswordMinimumSymbols(admin, myUserId());
    }

    public int getPasswordMinimumSymbols(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumSymbols(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public void setPasswordMinimumNonLetter(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordMinimumNonLetter(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getPasswordMinimumNonLetter(ComponentName admin) {
        return getPasswordMinimumNonLetter(admin, myUserId());
    }

    public int getPasswordMinimumNonLetter(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumNonLetter(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public PasswordMetrics getPasswordMinimumMetrics(int userHandle) {
        return getPasswordMinimumMetrics(userHandle, false);
    }

    public PasswordMetrics getPasswordMinimumMetrics(int userHandle, boolean deviceWideOnly) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordMinimumMetrics(userHandle, deviceWideOnly);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setPasswordHistoryLength(ComponentName admin, int length) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordHistoryLength(admin, length, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setPasswordExpirationTimeout(ComponentName admin, long timeout) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPasswordExpirationTimeout(admin, this.mContext.getPackageName(), timeout, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public long getPasswordExpirationTimeout(ComponentName admin) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordExpirationTimeout(admin, myUserId(), this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0L;
    }

    public long getPasswordExpiration(ComponentName admin) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordExpiration(admin, myUserId(), this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0L;
    }

    public int getPasswordHistoryLength(ComponentName admin) {
        return getPasswordHistoryLength(admin, myUserId());
    }

    public int getPasswordHistoryLength(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPasswordHistoryLength(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public int getPasswordMaximumLength(int quality) {
        PackageManager pm = this.mContext.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SECURE_LOCK_SCREEN)) {
            return 0;
        }
        return 16;
    }

    public boolean isActivePasswordSufficient() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isActivePasswordSufficient(this.mContext.getPackageName(), myUserId(), this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isActivePasswordSufficientForDeviceRequirement() {
        if (!this.mParentInstance) {
            throw new SecurityException("only callable on the parent instance");
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isActivePasswordSufficientForDeviceRequirement();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public int getPasswordComplexity() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getPasswordComplexity(this.mParentInstance);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setRequiredPasswordComplexity(int passwordComplexity) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setRequiredPasswordComplexity(this.mContext.getPackageName(), passwordComplexity, this.mParentInstance);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getRequiredPasswordComplexity() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getRequiredPasswordComplexity(this.mContext.getPackageName(), this.mParentInstance);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getAggregatedPasswordComplexityForUser(int userId) {
        return getAggregatedPasswordComplexityForUser(userId, false);
    }

    public int getAggregatedPasswordComplexityForUser(int userId, boolean deviceWideOnly) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getAggregatedPasswordComplexityForUser(userId, deviceWideOnly);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isUsingUnifiedPassword(ComponentName admin) {
        throwIfParentInstance("isUsingUnifiedPassword");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isUsingUnifiedPassword(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public boolean isPasswordSufficientAfterProfileUnification(int userHandle, int profileUser) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isPasswordSufficientAfterProfileUnification(userHandle, profileUser);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public int getCurrentFailedPasswordAttempts() {
        return getCurrentFailedPasswordAttempts(myUserId());
    }

    public int getCurrentFailedPasswordAttempts(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCurrentFailedPasswordAttempts(this.mContext.getPackageName(), userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return -1;
    }

    public boolean getDoNotAskCredentialsOnBoot() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDoNotAskCredentialsOnBoot();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName admin, int num) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMaximumFailedPasswordsForWipe(admin, this.mContext.getPackageName(), num, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin) {
        return getMaximumFailedPasswordsForWipe(admin, myUserId());
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getMaximumFailedPasswordsForWipe(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public int getProfileWithMinimumFailedPasswordsForWipe(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return -10000;
    }

    @Deprecated
    public boolean resetPassword(String password, int flags) {
        throwIfParentInstance("resetPassword");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.resetPassword(password, flags);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean setResetPasswordToken(ComponentName admin, byte[] token) {
        throwIfParentInstance("setResetPasswordToken");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setResetPasswordToken(admin, this.mContext.getPackageName(), token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean clearResetPasswordToken(ComponentName admin) {
        throwIfParentInstance("clearResetPasswordToken");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.clearResetPasswordToken(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isResetPasswordTokenActive(ComponentName admin) {
        throwIfParentInstance("isResetPasswordTokenActive");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isResetPasswordTokenActive(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean resetPasswordWithToken(ComponentName admin, String password, byte[] token, int flags) {
        throwIfParentInstance("resetPassword");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.resetPasswordWithToken(admin, this.mContext.getPackageName(), password, token, flags);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setMaximumTimeToLock(ComponentName admin, long timeMs) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMaximumTimeToLock(admin, this.mContext.getPackageName(), timeMs, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public long getMaximumTimeToLock(ComponentName admin) {
        return getMaximumTimeToLock(admin, myUserId());
    }

    public long getMaximumTimeToLock(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getMaximumTimeToLock(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0L;
    }

    public void setRequiredStrongAuthTimeout(ComponentName admin, long timeoutMs) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setRequiredStrongAuthTimeout(admin, this.mContext.getPackageName(), timeoutMs, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public long getRequiredStrongAuthTimeout(ComponentName admin) {
        return getRequiredStrongAuthTimeout(admin, myUserId());
    }

    public long getRequiredStrongAuthTimeout(ComponentName admin, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getRequiredStrongAuthTimeout(admin, userId, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return DEFAULT_STRONG_AUTH_TIMEOUT_MS;
    }

    public void lockNow() {
        lockNow(0);
    }

    public void lockNow(int flags) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.lockNow(flags, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void wipeData(int flags) {
        wipeDataInternal(flags, "", false);
    }

    public void wipeData(int flags, CharSequence reason) {
        Objects.requireNonNull(reason, "reason string is null");
        Preconditions.checkStringNotEmpty(reason, "reason string is empty");
        Preconditions.checkArgument((flags & 8) == 0, "WIPE_SILENTLY cannot be set");
        wipeDataInternal(flags, reason.toString(), false);
    }

    public void wipeDevice(int flags) {
        wipeDataInternal(flags, "", true);
    }

    private void wipeDataInternal(int flags, String wipeReasonForUser, boolean factoryReset) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.wipeDataWithReason(this.mContext.getPackageName(), flags, wipeReasonForUser, this.mParentInstance, factoryReset);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setFactoryResetProtectionPolicy(ComponentName admin, FactoryResetProtectionPolicy policy) {
        throwIfParentInstance("setFactoryResetProtectionPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setFactoryResetProtectionPolicy(admin, this.mContext.getPackageName(), policy);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public FactoryResetProtectionPolicy getFactoryResetProtectionPolicy(ComponentName admin) {
        throwIfParentInstance("getFactoryResetProtectionPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getFactoryResetProtectionPolicy(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    public void sendLostModeLocationUpdate(Executor executor, Consumer<Boolean> callback) {
        throwIfParentInstance("sendLostModeLocationUpdate");
        if (this.mService == null) {
            executeCallback(AndroidFuture.completedFuture(false), executor, callback);
            return;
        }
        try {
            AndroidFuture<Boolean> future = new AndroidFuture<>();
            this.mService.sendLostModeLocationUpdate(future);
            executeCallback(future, executor, callback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private void executeCallback(AndroidFuture<Boolean> future, final Executor executor, final Consumer<Boolean> callback) {
        future.whenComplete(new BiConsumer() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda12
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                executor.execute(new Runnable() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        DevicePolicyManager.lambda$executeCallback$0(r1, r2, r3);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$executeCallback$0(Throwable error, Consumer callback, Boolean result) {
        long token = Binder.clearCallingIdentity();
        try {
            if (error != null) {
                callback.accept(false);
            } else {
                callback.accept(result);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public ComponentName setGlobalProxy(ComponentName admin, Proxy proxySpec, List<String> exclusionList) {
        String hostSpec;
        String hostSpec2;
        throwIfParentInstance("setGlobalProxy");
        if (proxySpec == null) {
            throw new NullPointerException();
        }
        if (this.mService != null) {
            try {
                if (proxySpec.equals(Proxy.NO_PROXY)) {
                    hostSpec = null;
                    hostSpec2 = null;
                } else if (!proxySpec.type().equals(Proxy.Type.HTTP)) {
                    throw new IllegalArgumentException();
                } else {
                    Pair<String, String> proxyParams = getProxyParameters(proxySpec, exclusionList);
                    String hostSpec3 = proxyParams.first;
                    hostSpec = hostSpec3;
                    hostSpec2 = proxyParams.second;
                }
                return this.mService.setGlobalProxy(admin, hostSpec, hostSpec2);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public Pair<String, String> getProxyParameters(Proxy proxySpec, List<String> exclusionList) {
        List<String> trimmedExclList;
        InetSocketAddress sa = (InetSocketAddress) proxySpec.address();
        String hostName = sa.getHostName();
        int port = sa.getPort();
        if (exclusionList == null) {
            trimmedExclList = Collections.emptyList();
        } else {
            trimmedExclList = new ArrayList<>(exclusionList.size());
            for (String exclDomain : exclusionList) {
                trimmedExclList.add(exclDomain.trim());
            }
        }
        ProxyInfo info = ProxyInfo.buildDirectProxy(hostName, port, trimmedExclList);
        if (port == 0 || TextUtils.isEmpty(hostName) || !info.isValid()) {
            throw new IllegalArgumentException();
        }
        return new Pair<>(hostName + ":" + port, TextUtils.join(",", trimmedExclList));
    }

    public void setRecommendedGlobalProxy(ComponentName admin, ProxyInfo proxyInfo) {
        throwIfParentInstance("setRecommendedGlobalProxy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setRecommendedGlobalProxy(admin, proxyInfo);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public ComponentName getGlobalProxyAdmin() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getGlobalProxyAdmin(myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @Deprecated
    public int setStorageEncryption(ComponentName admin, boolean encrypt) {
        throwIfParentInstance("setStorageEncryption");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setStorageEncryption(admin, encrypt);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    @Deprecated
    public boolean getStorageEncryption(ComponentName admin) {
        throwIfParentInstance("getStorageEncryption");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getStorageEncryption(admin, myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public int getStorageEncryptionStatus() {
        throwIfParentInstance("getStorageEncryptionStatus");
        return getStorageEncryptionStatus(myUserId());
    }

    public int getStorageEncryptionStatus(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getStorageEncryptionStatus(this.mContext.getPackageName(), userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public boolean approveCaCert(String alias, int userHandle, boolean approval) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.approveCaCert(alias, userHandle, approval);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isCaCertApproved(String alias, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isCaCertApproved(alias, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean installCaCert(ComponentName admin, byte[] certBuffer) {
        throwIfParentInstance("installCaCert");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.installCaCert(admin, this.mContext.getPackageName(), certBuffer);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void uninstallCaCert(ComponentName admin, byte[] certBuffer) {
        throwIfParentInstance("uninstallCaCert");
        if (this.mService != null) {
            try {
                String alias = getCaCertAlias(certBuffer);
                this.mService.uninstallCaCerts(admin, this.mContext.getPackageName(), new String[]{alias});
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (CertificateException e2) {
                Log.m103w(TAG, "Unable to parse certificate", e2);
            }
        }
    }

    public List<byte[]> getInstalledCaCerts(ComponentName admin) {
        List<byte[]> certs = new ArrayList<>();
        throwIfParentInstance("getInstalledCaCerts");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.enforceCanManageCaCerts(admin, this.mContext.getPackageName());
                TrustedCertificateStore certStore = new TrustedCertificateStore();
                for (String alias : certStore.userAliases()) {
                    try {
                        certs.add(certStore.getCertificate(alias).getEncoded());
                    } catch (CertificateException ce) {
                        Log.m103w(TAG, "Could not encode certificate: " + alias, ce);
                    }
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return certs;
    }

    public void uninstallAllUserCaCerts(ComponentName admin) {
        throwIfParentInstance("uninstallAllUserCaCerts");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.uninstallCaCerts(admin, this.mContext.getPackageName(), (String[]) new TrustedCertificateStore().userAliases().toArray(new String[0]));
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean hasCaCertInstalled(ComponentName admin, byte[] certBuffer) {
        throwIfParentInstance("hasCaCertInstalled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.enforceCanManageCaCerts(admin, this.mContext.getPackageName());
                return getCaCertAlias(certBuffer) != null;
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            } catch (CertificateException ce) {
                Log.m103w(TAG, "Could not parse certificate", ce);
            }
        }
        return false;
    }

    public boolean installKeyPair(ComponentName admin, PrivateKey privKey, Certificate cert, String alias) {
        return installKeyPair(admin, privKey, new Certificate[]{cert}, alias, false);
    }

    public boolean installKeyPair(ComponentName admin, PrivateKey privKey, Certificate[] certs, String alias, boolean requestAccess) {
        int flags = 2;
        if (requestAccess) {
            flags = 2 | 1;
        }
        return installKeyPair(admin, privKey, certs, alias, flags);
    }

    public boolean installKeyPair(ComponentName admin, PrivateKey privKey, Certificate[] certs, String alias, int flags) {
        byte[] pemCert;
        byte[] pemChain;
        throwIfParentInstance("installKeyPair");
        boolean requestAccess = (flags & 1) == 1;
        boolean isUserSelectable = (flags & 2) == 2;
        try {
            pemCert = Credentials.convertToPem(certs[0]);
            pemChain = null;
            if (certs.length > 1) {
                pemChain = Credentials.convertToPem((Certificate[]) Arrays.copyOfRange(certs, 1, certs.length));
            }
        } catch (RemoteException e) {
            e = e;
        } catch (IOException | CertificateException e2) {
            e = e2;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e3) {
            e = e3;
        }
        try {
            byte[] pkcs8Key = ((PKCS8EncodedKeySpec) KeyFactory.getInstance(privKey.getAlgorithm()).getKeySpec(privKey, PKCS8EncodedKeySpec.class)).getEncoded();
            return this.mService.installKeyPair(admin, this.mContext.getPackageName(), pkcs8Key, pemCert, pemChain, alias, requestAccess, isUserSelectable);
        } catch (RemoteException e4) {
            e = e4;
            throw e.rethrowFromSystemServer();
        } catch (IOException | CertificateException e5) {
            e = e5;
            Log.m103w(TAG, "Could not pem-encode certificate", e);
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e6) {
            e = e6;
            Log.m103w(TAG, "Failed to obtain private key material", e);
            return false;
        }
    }

    public boolean removeKeyPair(ComponentName admin, String alias) {
        throwIfParentInstance("removeKeyPair");
        try {
            return this.mService.removeKeyPair(admin, this.mContext.getPackageName(), alias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasKeyPair(String alias) {
        throwIfParentInstance("hasKeyPair");
        try {
            return this.mService.hasKeyPair(this.mContext.getPackageName(), alias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public AttestedKeyPair generateKeyPair(ComponentName admin, String algorithm, KeyGenParameterSpec keySpec, int idAttestationFlags) {
        throwIfParentInstance("generateKeyPair");
        try {
            ParcelableKeyGenParameterSpec parcelableSpec = new ParcelableKeyGenParameterSpec(keySpec);
            KeymasterCertificateChain attestationChain = new KeymasterCertificateChain();
            boolean success = this.mService.generateKeyPair(admin, this.mContext.getPackageName(), algorithm, parcelableSpec, idAttestationFlags, attestationChain);
            if (!success) {
                Log.m110e(TAG, "Error generating key via DevicePolicyManagerService.");
                return null;
            }
            String alias = keySpec.getKeystoreAlias();
            KeyPair keyPair = KeyChain.getKeyPair(this.mContext, alias);
            Certificate[] outputChain = null;
            try {
                if (AttestationUtils.isChainValid(attestationChain)) {
                    outputChain = AttestationUtils.parseCertificateChain(attestationChain);
                }
                return new AttestedKeyPair(keyPair, outputChain);
            } catch (KeyAttestationException e) {
                Log.m109e(TAG, "Error parsing attestation chain for alias " + alias, e);
                this.mService.removeKeyPair(admin, this.mContext.getPackageName(), alias);
                return null;
            }
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        } catch (ServiceSpecificException e3) {
            Log.m104w(TAG, String.format("Key Generation failure: %d", Integer.valueOf(e3.errorCode)));
            switch (e3.errorCode) {
                case 1:
                    throw new StrongBoxUnavailableException("No StrongBox for key generation.");
                default:
                    throw new RuntimeException(String.format("Unknown error while generating key: %d", Integer.valueOf(e3.errorCode)));
            }
        } catch (KeyChainException e4) {
            Log.m103w(TAG, "Failed to generate key", e4);
            return null;
        } catch (InterruptedException e5) {
            Log.m103w(TAG, "Interrupted while generating key", e5);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public boolean grantKeyPairToApp(ComponentName admin, String alias, String packageName) {
        throwIfParentInstance("grantKeyPairToApp");
        try {
            return this.mService.setKeyGrantForApp(admin, this.mContext.getPackageName(), alias, packageName, true);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public Map<Integer, Set<String>> getKeyPairGrants(String alias) {
        throwIfParentInstance("getKeyPairGrants");
        try {
            return this.mService.getKeyPairGrants(this.mContext.getPackageName(), alias).getPackagesByUid();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public boolean revokeKeyPairFromApp(ComponentName admin, String alias, String packageName) {
        throwIfParentInstance("revokeKeyPairFromApp");
        try {
            return this.mService.setKeyGrantForApp(admin, this.mContext.getPackageName(), alias, packageName, false);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean grantKeyPairToWifiAuth(String alias) {
        throwIfParentInstance("grantKeyPairToWifiAuth");
        try {
            return this.mService.setKeyGrantToWifiAuth(this.mContext.getPackageName(), alias, true);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean revokeKeyPairFromWifiAuth(String alias) {
        throwIfParentInstance("revokeKeyPairFromWifiAuth");
        try {
            return this.mService.setKeyGrantToWifiAuth(this.mContext.getPackageName(), alias, false);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean isKeyPairGrantedToWifiAuth(String alias) {
        throwIfParentInstance("isKeyPairGrantedToWifiAuth");
        try {
            return this.mService.isKeyPairGrantedToWifiAuth(this.mContext.getPackageName(), alias);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean isDeviceIdAttestationSupported() {
        PackageManager pm = this.mContext.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_DEVICE_ID_ATTESTATION);
    }

    public boolean isUniqueDeviceAttestationSupported() {
        PackageManager pm = this.mContext.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_DEVICE_UNIQUE_ATTESTATION);
    }

    public boolean setKeyPairCertificate(ComponentName admin, String alias, List<Certificate> certs, boolean isUserSelectable) {
        throwIfParentInstance("setKeyPairCertificate");
        try {
            byte[] pemCert = Credentials.convertToPem(certs.get(0));
            byte[] pemChain = null;
            if (certs.size() > 1) {
                pemChain = Credentials.convertToPem((Certificate[]) certs.subList(1, certs.size()).toArray(new Certificate[0]));
            }
            return this.mService.setKeyPairCertificate(admin, this.mContext.getPackageName(), alias, pemCert, pemChain, isUserSelectable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (IOException | CertificateException e2) {
            Log.m103w(TAG, "Could not pem-encode certificate", e2);
            return false;
        }
    }

    private static String getCaCertAlias(byte[] certBuffer) throws CertificateException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBuffer));
        return new TrustedCertificateStore().getCertificateAlias(cert);
    }

    @Deprecated
    public void setCertInstallerPackage(ComponentName admin, String installerPackage) throws SecurityException {
        throwIfParentInstance("setCertInstallerPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCertInstallerPackage(admin, installerPackage);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public String getCertInstallerPackage(ComponentName admin) throws SecurityException {
        throwIfParentInstance("getCertInstallerPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCertInstallerPackage(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setDelegatedScopes(ComponentName admin, String delegatePackage, List<String> scopes) {
        throwIfParentInstance("setDelegatedScopes");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDelegatedScopes(admin, delegatePackage, scopes);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public List<String> getDelegatedScopes(ComponentName admin, String delegatedPackage) {
        throwIfParentInstance("getDelegatedScopes");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDelegatedScopes(admin, delegatedPackage);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public List<String> getDelegatePackages(ComponentName admin, String delegationScope) {
        throwIfParentInstance("getDelegatePackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDelegatePackages(admin, delegationScope);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setAlwaysOnVpnPackage(ComponentName admin, String vpnPackage, boolean lockdownEnabled) throws PackageManager.NameNotFoundException {
        setAlwaysOnVpnPackage(admin, vpnPackage, lockdownEnabled, Collections.emptySet());
    }

    public void setAlwaysOnVpnPackage(ComponentName admin, String vpnPackage, boolean lockdownEnabled, Set<String> lockdownAllowlist) throws PackageManager.NameNotFoundException {
        ArrayList arrayList;
        throwIfParentInstance("setAlwaysOnVpnPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            if (lockdownAllowlist == null) {
                arrayList = null;
            } else {
                try {
                    arrayList = new ArrayList(lockdownAllowlist);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                } catch (ServiceSpecificException e2) {
                    switch (e2.errorCode) {
                        case 1:
                            throw new PackageManager.NameNotFoundException(e2.getMessage());
                        default:
                            throw new RuntimeException("Unknown error setting always-on VPN: " + e2.errorCode, e2);
                    }
                }
            }
            iDevicePolicyManager.setAlwaysOnVpnPackage(admin, vpnPackage, lockdownEnabled, arrayList);
        }
    }

    public boolean isAlwaysOnVpnLockdownEnabled(ComponentName admin) {
        throwIfParentInstance("isAlwaysOnVpnLockdownEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isAlwaysOnVpnLockdownEnabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isAlwaysOnVpnLockdownEnabled() {
        throwIfParentInstance("isAlwaysOnVpnLockdownEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isAlwaysOnVpnLockdownEnabledForUser(myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public Set<String> getAlwaysOnVpnLockdownWhitelist(ComponentName admin) {
        throwIfParentInstance("getAlwaysOnVpnLockdownWhitelist");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                List<String> allowlist = iDevicePolicyManager.getAlwaysOnVpnLockdownAllowlist(admin);
                return allowlist != null ? new HashSet(allowlist) : null;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public String getAlwaysOnVpnPackage(ComponentName admin) {
        throwIfParentInstance("getAlwaysOnVpnPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAlwaysOnVpnPackage(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public String getAlwaysOnVpnPackage() {
        throwIfParentInstance("getAlwaysOnVpnPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAlwaysOnVpnPackageForUser(myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setCameraDisabled(ComponentName admin, boolean disabled) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCameraDisabled(admin, this.mContext.getPackageName(), disabled, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getCameraDisabled(ComponentName admin) {
        return getCameraDisabled(admin, myUserId());
    }

    public boolean getCameraDisabled(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCameraDisabled(admin, this.mContext.getPackageName(), userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean requestBugreport(ComponentName admin) {
        throwIfParentInstance("requestBugreport");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.requestBugreport(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setScreenCaptureDisabled(ComponentName admin, boolean disabled) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setScreenCaptureDisabled(admin, this.mContext.getPackageName(), disabled, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getScreenCaptureDisabled(ComponentName admin) {
        return getScreenCaptureDisabled(admin, myUserId());
    }

    public boolean getScreenCaptureDisabled(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getScreenCaptureDisabled(admin, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setNearbyNotificationStreamingPolicy(int policy) {
        throwIfParentInstance("setNearbyNotificationStreamingPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setNearbyNotificationStreamingPolicy(policy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getNearbyNotificationStreamingPolicy() {
        return getNearbyNotificationStreamingPolicy(myUserId());
    }

    public int getNearbyNotificationStreamingPolicy(int userId) {
        throwIfParentInstance("getNearbyNotificationStreamingPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getNearbyNotificationStreamingPolicy(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setNearbyAppStreamingPolicy(int policy) {
        throwIfParentInstance("setNearbyAppStreamingPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setNearbyAppStreamingPolicy(policy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getNearbyAppStreamingPolicy() {
        return getNearbyAppStreamingPolicy(myUserId());
    }

    public int getNearbyAppStreamingPolicy(int userId) {
        throwIfParentInstance("getNearbyAppStreamingPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getNearbyAppStreamingPolicy(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setAutoTimeRequired(ComponentName admin, boolean required) {
        throwIfParentInstance("setAutoTimeRequired");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setAutoTimeRequired(admin, required);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public boolean getAutoTimeRequired() {
        throwIfParentInstance("getAutoTimeRequired");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAutoTimeRequired();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setAutoTimeEnabled(ComponentName admin, boolean enabled) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setAutoTimeEnabled(admin, this.mContext.getPackageName(), enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getAutoTimeEnabled(ComponentName admin) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAutoTimeEnabled(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setAutoTimeZoneEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setAutoTimeZone");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setAutoTimeZoneEnabled(admin, this.mContext.getPackageName(), enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getAutoTimeZoneEnabled(ComponentName admin) {
        throwIfParentInstance("getAutoTimeZone");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAutoTimeZoneEnabled(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setForceEphemeralUsers(ComponentName admin, boolean forceEphemeralUsers) {
        throwIfParentInstance("setForceEphemeralUsers");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setForceEphemeralUsers(admin, forceEphemeralUsers);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getForceEphemeralUsers(ComponentName admin) {
        throwIfParentInstance("getForceEphemeralUsers");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getForceEphemeralUsers(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setKeyguardDisabledFeatures(ComponentName admin, int which) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setKeyguardDisabledFeatures(admin, this.mContext.getPackageName(), which, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int getKeyguardDisabledFeatures(ComponentName admin) {
        return getKeyguardDisabledFeatures(admin, myUserId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$new$2(Pair query) throws RemoteException {
        return Integer.valueOf(getService().getKeyguardDisabledFeatures((ComponentName) query.first, ((Integer) query.second).intValue(), isParentInstance()));
    }

    public int getKeyguardDisabledFeatures(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            return this.mGetKeyGuardDisabledFeaturesCache.query(new Pair<>(admin, Integer.valueOf(userHandle))).intValue();
        }
        return 0;
    }

    public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setActiveAdmin(policyReceiver, refreshing, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing) {
        setActiveAdmin(policyReceiver, refreshing, myUserId());
    }

    public void getRemoveWarning(ComponentName admin, RemoteCallback result) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.getRemoveWarning(admin, result, myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportPasswordChanged(PasswordMetrics metrics, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportPasswordChanged(metrics, userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportFailedPasswordAttempt(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportFailedPasswordAttempt(userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportSuccessfulPasswordAttempt(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportSuccessfulPasswordAttempt(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportFailedBiometricAttempt(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportFailedBiometricAttempt(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportSuccessfulBiometricAttempt(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportSuccessfulBiometricAttempt(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportKeyguardDismissed(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportKeyguardDismissed(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void reportKeyguardSecured(int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.reportKeyguardSecured(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean setDeviceOwner(ComponentName who, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setDeviceOwner(who, userId, true);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean setDeviceOwnerOnly(ComponentName who, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setDeviceOwner(who, userId, false);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isDeviceOwnerApp(String packageName) {
        throwIfParentInstance("isDeviceOwnerApp");
        return isDeviceOwnerAppOnCallingUser(packageName);
    }

    public boolean isDeviceOwnerAppOnCallingUser(String packageName) {
        return isDeviceOwnerAppOnAnyUserInner(packageName, true);
    }

    public boolean isDeviceOwnerAppOnAnyUser(String packageName) {
        return isDeviceOwnerAppOnAnyUserInner(packageName, false);
    }

    public ComponentName getDeviceOwnerComponentOnCallingUser() {
        return getDeviceOwnerComponentInner(true);
    }

    @SystemApi
    public ComponentName getDeviceOwnerComponentOnAnyUser() {
        return getDeviceOwnerComponentInner(false);
    }

    private boolean isDeviceOwnerAppOnAnyUserInner(String packageName, boolean callingUserOnly) {
        ComponentName deviceOwner;
        if (packageName == null || (deviceOwner = getDeviceOwnerComponentInner(callingUserOnly)) == null) {
            return false;
        }
        return packageName.equals(deviceOwner.getPackageName());
    }

    private ComponentName getDeviceOwnerComponentInner(boolean callingUserOnly) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDeviceOwnerComponent(callingUserOnly);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    public UserHandle getDeviceOwnerUser() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                int userId = iDevicePolicyManager.getDeviceOwnerUserId();
                if (userId != -10000) {
                    return UserHandle.m145of(userId);
                }
                return null;
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public int getDeviceOwnerUserId() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDeviceOwnerUserId();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return -10000;
    }

    @Deprecated
    public void clearDeviceOwnerApp(String packageName) {
        throwIfParentInstance("clearDeviceOwnerApp");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.clearDeviceOwner(packageName);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public String getDeviceOwner() {
        throwIfParentInstance("getDeviceOwner");
        ComponentName name = getDeviceOwnerComponentOnCallingUser();
        if (name != null) {
            return name.getPackageName();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$new$3(Void query) throws RemoteException {
        return Boolean.valueOf(getService().hasDeviceOwner());
    }

    @SystemApi
    public boolean isDeviceManaged() {
        return this.mHasDeviceOwnerCache.query(null).booleanValue();
    }

    @SystemApi
    public String getDeviceOwnerNameOnAnyUser() {
        throwIfParentInstance("getDeviceOwnerNameOnAnyUser");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDeviceOwnerName();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    @Deprecated
    public boolean setActiveProfileOwner(ComponentName admin, @Deprecated String ownerName) throws IllegalArgumentException {
        throwIfParentInstance("setActiveProfileOwner");
        if (this.mService != null) {
            try {
                int myUserId = myUserId();
                this.mService.setActiveAdmin(admin, false, myUserId);
                return this.mService.setProfileOwner(admin, myUserId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @Deprecated
    public void clearProfileOwner(ComponentName admin) {
        throwIfParentInstance("clearProfileOwner");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.clearProfileOwner(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean hasUserSetupCompleted() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.hasUserSetupCompleted();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public boolean setProfileOwner(ComponentName admin, int userHandle) throws IllegalArgumentException {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setProfileOwner(admin, userHandle);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setDeviceOwnerLockScreenInfo(ComponentName admin, CharSequence info) {
        throwIfParentInstance("setDeviceOwnerLockScreenInfo");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDeviceOwnerLockScreenInfo(admin, info);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public CharSequence getDeviceOwnerLockScreenInfo() {
        throwIfParentInstance("getDeviceOwnerLockScreenInfo");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDeviceOwnerLockScreenInfo();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public String[] setPackagesSuspended(ComponentName admin, String[] packageNames, boolean suspended) {
        throwIfParentInstance("setPackagesSuspended");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setPackagesSuspended(admin, this.mContext.getPackageName(), packageNames, suspended);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return packageNames;
    }

    public boolean isPackageSuspended(ComponentName admin, String packageName) throws PackageManager.NameNotFoundException {
        throwIfParentInstance("isPackageSuspended");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isPackageSuspended(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (IllegalArgumentException e2) {
                throw new PackageManager.NameNotFoundException(packageName);
            }
        }
        return false;
    }

    public void setProfileEnabled(ComponentName admin) {
        throwIfParentInstance("setProfileEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setProfileEnabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setProfileName(ComponentName admin, String profileName) {
        throwIfParentInstance("setProfileName");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setProfileName(admin, profileName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isProfileOwnerApp(String packageName) {
        throwIfParentInstance("isProfileOwnerApp");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ComponentName profileOwner = iDevicePolicyManager.getProfileOwnerAsUser(myUserId());
                if (profileOwner != null) {
                    return profileOwner.getPackageName().equals(packageName);
                }
                return false;
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public ComponentName getProfileOwner() throws IllegalArgumentException {
        throwIfParentInstance("getProfileOwner");
        return getProfileOwnerAsUser(this.mContext.getUserId());
    }

    public ComponentName getProfileOwnerAsUser(UserHandle user) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getProfileOwnerAsUser(user.getIdentifier());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public ComponentName getProfileOwnerAsUser(int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getProfileOwnerAsUser(userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ComponentName lambda$new$4(UserHandle arg) throws RemoteException {
        return getService().getProfileOwnerOrDeviceOwnerSupervisionComponent(arg);
    }

    public ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle user) {
        if (this.mService != null) {
            return this.mGetProfileOwnerOrDeviceOwnerSupervisionComponentCache.query(user);
        }
        return null;
    }

    public boolean isSupervisionComponent(ComponentName who) {
        if (this.mService != null) {
            try {
                return getService().isSupervisionComponent(who);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public String getProfileOwnerName() throws IllegalArgumentException {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getProfileOwnerName(this.mContext.getUserId());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    public String getProfileOwnerNameAsUser(int userId) throws IllegalArgumentException {
        throwIfParentInstance("getProfileOwnerNameAsUser");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getProfileOwnerName(userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$new$5(Object query) throws RemoteException {
        return Boolean.valueOf(getService().isOrganizationOwnedDeviceWithManagedProfile());
    }

    public boolean isOrganizationOwnedDeviceWithManagedProfile() {
        throwIfParentInstance("isOrganizationOwnedDeviceWithManagedProfile");
        if (this.mService != null) {
            return this.mIsOrganizationOwnedDeviceWithManagedProfileCache.query(null).booleanValue();
        }
        return false;
    }

    public boolean hasDeviceIdentifierAccess(String packageName, int pid, int uid) {
        IDevicePolicyManager iDevicePolicyManager;
        throwIfParentInstance("hasDeviceIdentifierAccess");
        if (packageName == null || (iDevicePolicyManager = this.mService) == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.checkDeviceIdentifierAccess(packageName, pid, uid);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void addPersistentPreferredActivity(ComponentName admin, IntentFilter filter, ComponentName activity) {
        throwIfParentInstance("addPersistentPreferredActivity");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.addPersistentPreferredActivity(admin, this.mContext.getPackageName(), filter, activity);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void clearPackagePersistentPreferredActivities(ComponentName admin, String packageName) {
        throwIfParentInstance("clearPackagePersistentPreferredActivities");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.clearPackagePersistentPreferredActivities(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setDefaultSmsApplication(ComponentName admin, String packageName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDefaultSmsApplication(admin, this.mContext.getPackageName(), packageName, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setDefaultDialerApplication(String packageName) {
        throwIfParentInstance("setDefaultDialerApplication");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDefaultDialerApplication(packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public void setApplicationRestrictionsManagingPackage(ComponentName admin, String packageName) throws PackageManager.NameNotFoundException {
        throwIfParentInstance("setApplicationRestrictionsManagingPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                if (!iDevicePolicyManager.setApplicationRestrictionsManagingPackage(admin, packageName)) {
                    throw new PackageManager.NameNotFoundException(packageName);
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public String getApplicationRestrictionsManagingPackage(ComponentName admin) {
        throwIfParentInstance("getApplicationRestrictionsManagingPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getApplicationRestrictionsManagingPackage(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @Deprecated
    public boolean isCallerApplicationRestrictionsManagingPackage() {
        throwIfParentInstance("isCallerApplicationRestrictionsManagingPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isCallerApplicationRestrictionsManagingPackage(this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setApplicationRestrictions(ComponentName admin, String packageName, Bundle settings) {
        throwIfParentInstance("setApplicationRestrictions");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setApplicationRestrictions(admin, this.mContext.getPackageName(), packageName, settings);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setTrustAgentConfiguration(ComponentName admin, ComponentName target, PersistableBundle configuration) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setTrustAgentConfiguration(admin, this.mContext.getPackageName(), target, configuration, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent) {
        return getTrustAgentConfiguration(admin, agent, myUserId());
    }

    public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getTrustAgentConfiguration(admin, agent, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return new ArrayList();
    }

    @Deprecated
    public void setCrossProfileCallerIdDisabled(ComponentName admin, boolean disabled) {
        throwIfParentInstance("setCrossProfileCallerIdDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCrossProfileCallerIdDisabled(admin, disabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public boolean getCrossProfileCallerIdDisabled(ComponentName admin) {
        throwIfParentInstance("getCrossProfileCallerIdDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCrossProfileCallerIdDisabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @Deprecated
    public boolean getCrossProfileCallerIdDisabled(UserHandle userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCrossProfileCallerIdDisabledForUser(userHandle.getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setCredentialManagerPolicy(PackagePolicy policy) {
        throwIfParentInstance("setCredentialManagerPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCredentialManagerPolicy(policy);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public PackagePolicy getCredentialManagerPolicy() {
        throwIfParentInstance("getCredentialManagerPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCredentialManagerPolicy();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setManagedProfileCallerIdAccessPolicy(PackagePolicy policy) {
        throwIfParentInstance("setManagedProfileCallerIdAccessPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setManagedProfileCallerIdAccessPolicy(policy);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public PackagePolicy getManagedProfileCallerIdAccessPolicy() {
        throwIfParentInstance("getManagedProfileCallerIdAccessPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getManagedProfileCallerIdAccessPolicy();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean hasManagedProfileCallerIdAccess(UserHandle userHandle, String packageName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return true;
        }
        try {
            return iDevicePolicyManager.hasManagedProfileCallerIdAccess(userHandle.getIdentifier(), packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setManagedProfileContactsAccessPolicy(PackagePolicy policy) {
        throwIfParentInstance("setManagedProfileContactsAccessPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setManagedProfileContactsAccessPolicy(policy);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public PackagePolicy getManagedProfileContactsAccessPolicy() {
        throwIfParentInstance("getManagedProfileContactsAccessPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getManagedProfileContactsAccessPolicy();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean hasManagedProfileContactsAccess(UserHandle userHandle, String packageName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.hasManagedProfileContactsAccess(userHandle.getIdentifier(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @Deprecated
    public void setCrossProfileContactsSearchDisabled(ComponentName admin, boolean disabled) {
        throwIfParentInstance("setCrossProfileContactsSearchDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCrossProfileContactsSearchDisabled(admin, disabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public boolean getCrossProfileContactsSearchDisabled(ComponentName admin) {
        throwIfParentInstance("getCrossProfileContactsSearchDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCrossProfileContactsSearchDisabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @Deprecated
    public boolean getCrossProfileContactsSearchDisabled(UserHandle userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getCrossProfileContactsSearchDisabledForUser(userHandle.getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void startManagedQuickContact(String actualLookupKey, long actualContactId, boolean isContactIdIgnored, long directoryId, Intent originalIntent) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.startManagedQuickContact(actualLookupKey, actualContactId, isContactIdIgnored, directoryId, originalIntent);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void startManagedQuickContact(String actualLookupKey, long actualContactId, Intent originalIntent) {
        startManagedQuickContact(actualLookupKey, actualContactId, false, 0L, originalIntent);
    }

    public void setBluetoothContactSharingDisabled(ComponentName admin, boolean disabled) {
        throwIfParentInstance("setBluetoothContactSharingDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setBluetoothContactSharingDisabled(admin, disabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean getBluetoothContactSharingDisabled(ComponentName admin) {
        throwIfParentInstance("getBluetoothContactSharingDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getBluetoothContactSharingDisabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    @SystemApi
    public boolean getBluetoothContactSharingDisabled(UserHandle userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getBluetoothContactSharingDisabledForUser(userHandle.getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public void addCrossProfileIntentFilter(ComponentName admin, IntentFilter filter, int flags) {
        throwIfParentInstance("addCrossProfileIntentFilter");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.addCrossProfileIntentFilter(admin, this.mContext.getPackageName(), filter, flags);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void clearCrossProfileIntentFilters(ComponentName admin) {
        throwIfParentInstance("clearCrossProfileIntentFilters");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.clearCrossProfileIntentFilters(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean setPermittedAccessibilityServices(ComponentName admin, List<String> packageNames) {
        throwIfParentInstance("setPermittedAccessibilityServices");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setPermittedAccessibilityServices(admin, packageNames);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<String> getPermittedAccessibilityServices(ComponentName admin) {
        throwIfParentInstance("getPermittedAccessibilityServices");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedAccessibilityServices(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean isAccessibilityServicePermittedByAdmin(ComponentName admin, String packageName, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isAccessibilityServicePermittedByAdmin(admin, packageName, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public List<String> getPermittedAccessibilityServices(int userId) {
        throwIfParentInstance("getPermittedAccessibilityServices");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedAccessibilityServicesForUser(userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean setPermittedInputMethods(ComponentName admin, List<String> packageNames) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setPermittedInputMethods(admin, this.mContext.getPackageName(), packageNames, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<String> getPermittedInputMethods(ComponentName admin) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedInputMethods(admin, this.mContext.getPackageName(), this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean isInputMethodPermittedByAdmin(ComponentName admin, String packageName, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isInputMethodPermittedByAdmin(admin, packageName, userHandle, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public List<String> getPermittedInputMethodsForCurrentUser() {
        throwIfParentInstance("getPermittedInputMethodsForCurrentUser");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedInputMethodsAsUser(UserHandle.myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public List<String> getPermittedInputMethods() {
        throwIfParentInstance("getPermittedInputMethods");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedInputMethodsAsUser(myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean setPermittedCrossProfileNotificationListeners(ComponentName admin, List<String> packageList) {
        throwIfParentInstance("setPermittedCrossProfileNotificationListeners");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setPermittedCrossProfileNotificationListeners(admin, packageList);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<String> getPermittedCrossProfileNotificationListeners(ComponentName admin) {
        throwIfParentInstance("getPermittedCrossProfileNotificationListeners");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPermittedCrossProfileNotificationListeners(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean isNotificationListenerServicePermitted(String packageName, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isNotificationListenerServicePermitted(packageName, userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public List<String> getKeepUninstalledPackages(ComponentName admin) {
        throwIfParentInstance("getKeepUninstalledPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getKeepUninstalledPackages(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setKeepUninstalledPackages(ComponentName admin, List<String> packageNames) {
        throwIfParentInstance("setKeepUninstalledPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setKeepUninstalledPackages(admin, this.mContext.getPackageName(), packageNames);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public UserHandle createAndManageUser(ComponentName admin, String name, ComponentName profileOwner, PersistableBundle adminExtras, int flags) {
        throwIfParentInstance("createAndManageUser");
        try {
            return this.mService.createAndManageUser(admin, name, profileOwner, adminExtras, flags);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (ServiceSpecificException e) {
            throw new UserManager.UserOperationException(e.getMessage(), e.errorCode);
        }
    }

    public boolean removeUser(ComponentName admin, UserHandle userHandle) {
        throwIfParentInstance("removeUser");
        try {
            return this.mService.removeUser(admin, userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean switchUser(ComponentName admin, UserHandle userHandle) {
        throwIfParentInstance("switchUser");
        try {
            return this.mService.switchUser(admin, userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int startUserInBackground(ComponentName admin, UserHandle userHandle) {
        throwIfParentInstance("startUserInBackground");
        try {
            return this.mService.startUserInBackground(admin, userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int stopUser(ComponentName admin, UserHandle userHandle) {
        throwIfParentInstance("stopUser");
        try {
            return this.mService.stopUser(admin, userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int logoutUser(ComponentName admin) {
        throwIfParentInstance("logoutUser");
        try {
            return this.mService.logoutUser(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setManagedSubscriptionsPolicy(ManagedSubscriptionsPolicy policy) {
        throwIfParentInstance("setManagedSubscriptionsPolicy");
        try {
            this.mService.setManagedSubscriptionsPolicy(policy);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public ManagedSubscriptionsPolicy getManagedSubscriptionsPolicy() {
        throwIfParentInstance("getManagedSubscriptionsPolicy");
        try {
            return this.mService.getManagedSubscriptionsPolicy();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public int logoutUser() {
        try {
            return this.mService.logoutUserInternal();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public UserHandle getLogoutUser() {
        try {
            int userId = this.mService.getLogoutUserId();
            if (userId == -10000) {
                return null;
            }
            return UserHandle.m145of(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<UserHandle> getSecondaryUsers(ComponentName admin) {
        throwIfParentInstance("getSecondaryUsers");
        try {
            return this.mService.getSecondaryUsers(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isEphemeralUser(ComponentName admin) {
        throwIfParentInstance("isEphemeralUser");
        try {
            return this.mService.isEphemeralUser(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Bundle getApplicationRestrictions(ComponentName admin, String packageName) {
        throwIfParentInstance("getApplicationRestrictions");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getApplicationRestrictions(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void addUserRestriction(ComponentName admin, String key) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUserRestriction(admin, this.mContext.getPackageName(), key, true, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void addUserRestrictionGlobally(String key) {
        throwIfParentInstance("addUserRestrictionGlobally");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUserRestrictionGlobally(this.mContext.getPackageName(), key);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void clearUserRestriction(ComponentName admin, String key) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUserRestriction(admin, this.mContext.getPackageName(), key, false, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public Bundle getUserRestrictions(ComponentName admin) {
        Bundle ret = null;
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ret = iDevicePolicyManager.getUserRestrictions(admin, this.mContext.getPackageName(), this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return ret == null ? new Bundle() : ret;
    }

    public Bundle getUserRestrictionsGlobally() {
        throwIfParentInstance("createAdminSupportIntent");
        Bundle ret = null;
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ret = iDevicePolicyManager.getUserRestrictionsGlobally(this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return ret == null ? new Bundle() : ret;
    }

    public Intent createAdminSupportIntent(String restriction) {
        throwIfParentInstance("createAdminSupportIntent");
        Intent result = null;
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                result = iDevicePolicyManager.createAdminSupportIntent(restriction);
                if (result != null) {
                    result.prepareToEnterProcess(32, this.mContext.getAttributionSource());
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return result;
    }

    public Bundle getEnforcingAdminAndUserDetails(int userId, String restriction) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getEnforcingAdminAndUserDetails(userId, restriction);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public boolean setApplicationHidden(ComponentName admin, String packageName, boolean hidden) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setApplicationHidden(admin, this.mContext.getPackageName(), packageName, hidden, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isApplicationHidden(ComponentName admin, String packageName) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isApplicationHidden(admin, this.mContext.getPackageName(), packageName, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void enableSystemApp(ComponentName admin, String packageName) {
        throwIfParentInstance("enableSystemApp");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.enableSystemApp(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int enableSystemApp(ComponentName admin, Intent intent) {
        throwIfParentInstance("enableSystemApp");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.enableSystemAppWithIntent(admin, this.mContext.getPackageName(), intent);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public boolean installExistingPackage(ComponentName admin, String packageName) {
        throwIfParentInstance("installExistingPackage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.installExistingPackage(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setAccountManagementDisabled(ComponentName admin, String accountType, boolean disabled) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setAccountManagementDisabled(admin, this.mContext.getPackageName(), accountType, disabled, this.mParentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String[] getAccountTypesWithManagementDisabled() {
        return getAccountTypesWithManagementDisabledAsUser(myUserId(), this.mParentInstance);
    }

    public String[] getAccountTypesWithManagementDisabledAsUser(int userId) {
        return getAccountTypesWithManagementDisabledAsUser(userId, false);
    }

    public String[] getAccountTypesWithManagementDisabledAsUser(int userId, boolean parentInstance) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getAccountTypesWithManagementDisabledAsUser(userId, this.mContext.getPackageName(), parentInstance);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    @SystemApi
    public void setSecondaryLockscreenEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setSecondaryLockscreenEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setSecondaryLockscreenEnabled(admin, enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public boolean isSecondaryLockscreenEnabled(UserHandle userHandle) {
        throwIfParentInstance("isSecondaryLockscreenEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isSecondaryLockscreenEnabled(userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setLockTaskPackages(ComponentName admin, String[] packages) throws SecurityException {
        throwIfParentInstance("setLockTaskPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setLockTaskPackages(admin, this.mContext.getPackageName(), packages);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String[] getLockTaskPackages(ComponentName admin) {
        throwIfParentInstance("getLockTaskPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getLockTaskPackages(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return new String[0];
    }

    public boolean isLockTaskPermitted(String pkg) {
        throwIfParentInstance("isLockTaskPermitted");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isLockTaskPermitted(pkg);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setLockTaskFeatures(ComponentName admin, int flags) {
        throwIfParentInstance("setLockTaskFeatures");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setLockTaskFeatures(admin, this.mContext.getPackageName(), flags);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int getLockTaskFeatures(ComponentName admin) {
        throwIfParentInstance("getLockTaskFeatures");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getLockTaskFeatures(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public void setPreferentialNetworkServiceEnabled(boolean enabled) {
        throwIfParentInstance("setPreferentialNetworkServiceEnabled");
        PreferentialNetworkServiceConfig.Builder configBuilder = new PreferentialNetworkServiceConfig.Builder();
        configBuilder.setEnabled(enabled);
        if (enabled) {
            configBuilder.setNetworkId(1);
        }
        setPreferentialNetworkServiceConfigs(List.of(configBuilder.build()));
    }

    public boolean isPreferentialNetworkServiceEnabled() {
        throwIfParentInstance("isPreferentialNetworkServiceEnabled");
        return getPreferentialNetworkServiceConfigs().stream().anyMatch(new Predicate() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isEnabled;
                isEnabled = ((PreferentialNetworkServiceConfig) obj).isEnabled();
                return isEnabled;
            }
        });
    }

    public void setPreferentialNetworkServiceConfigs(List<PreferentialNetworkServiceConfig> preferentialNetworkServiceConfigs) {
        throwIfParentInstance("setPreferentialNetworkServiceConfigs");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setPreferentialNetworkServiceConfigs(preferentialNetworkServiceConfigs);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs() {
        throwIfParentInstance("getPreferentialNetworkServiceConfigs");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return List.of(PreferentialNetworkServiceConfig.DEFAULT);
        }
        try {
            return iDevicePolicyManager.getPreferentialNetworkServiceConfigs();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setGlobalSetting(ComponentName admin, String setting, String value) {
        throwIfParentInstance("setGlobalSetting");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setGlobalSetting(admin, setting, value);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setSystemSetting(ComponentName admin, String setting, String value) {
        throwIfParentInstance("setSystemSetting");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setSystemSetting(admin, setting, value);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setConfiguredNetworksLockdownState(ComponentName admin, boolean lockdown) {
        throwIfParentInstance("setConfiguredNetworksLockdownState");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setConfiguredNetworksLockdownState(admin, this.mContext.getPackageName(), lockdown);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean hasLockdownAdminConfiguredNetworks(ComponentName admin) {
        throwIfParentInstance("hasLockdownAdminConfiguredNetworks");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.hasLockdownAdminConfiguredNetworks(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean setTime(ComponentName admin, long millis) {
        throwIfParentInstance("setTime");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setTime(admin, this.mContext.getPackageName(), millis);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean setTimeZone(ComponentName admin, String timeZone) {
        throwIfParentInstance("setTimeZone");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setTimeZone(admin, this.mContext.getPackageName(), timeZone);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setLocationEnabled(ComponentName admin, boolean locationEnabled) {
        throwIfParentInstance("setLocationEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setLocationEnabled(admin, locationEnabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setSecureSetting(ComponentName admin, String setting, String value) {
        throwIfParentInstance("setSecureSetting");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setSecureSetting(admin, setting, value);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setRestrictionsProvider(ComponentName admin, ComponentName provider) {
        throwIfParentInstance("setRestrictionsProvider");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setRestrictionsProvider(admin, provider);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public void setMasterVolumeMuted(ComponentName admin, boolean on) {
        throwIfParentInstance("setMasterVolumeMuted");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMasterVolumeMuted(admin, on);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean isMasterVolumeMuted(ComponentName admin) {
        throwIfParentInstance("isMasterVolumeMuted");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isMasterVolumeMuted(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setUninstallBlocked(ComponentName admin, String packageName, boolean uninstallBlocked) {
        throwIfParentInstance("setUninstallBlocked");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUninstallBlocked(admin, this.mContext.getPackageName(), packageName, uninstallBlocked);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean isUninstallBlocked(ComponentName admin, String packageName) {
        throwIfParentInstance("isUninstallBlocked");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isUninstallBlocked(packageName);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean addCrossProfileWidgetProvider(ComponentName admin, String packageName) {
        throwIfParentInstance("addCrossProfileWidgetProvider");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.addCrossProfileWidgetProvider(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean removeCrossProfileWidgetProvider(ComponentName admin, String packageName) {
        throwIfParentInstance("removeCrossProfileWidgetProvider");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.removeCrossProfileWidgetProvider(admin, this.mContext.getPackageName(), packageName);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<String> getCrossProfileWidgetProviders(ComponentName admin) {
        throwIfParentInstance("getCrossProfileWidgetProviders");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                List<String> providers = iDevicePolicyManager.getCrossProfileWidgetProviders(admin, this.mContext.getPackageName());
                if (providers != null) {
                    return providers;
                }
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public void setUserIcon(ComponentName admin, Bitmap icon) {
        throwIfParentInstance("setUserIcon");
        try {
            this.mService.setUserIcon(admin, icon);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setSystemUpdatePolicy(ComponentName admin, SystemUpdatePolicy policy) {
        throwIfParentInstance("setSystemUpdatePolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setSystemUpdatePolicy(admin, this.mContext.getPackageName(), policy);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public SystemUpdatePolicy getSystemUpdatePolicy() {
        throwIfParentInstance("getSystemUpdatePolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getSystemUpdatePolicy();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void clearSystemUpdatePolicyFreezePeriodRecord() {
        throwIfParentInstance("clearSystemUpdatePolicyFreezePeriodRecord");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.clearSystemUpdatePolicyFreezePeriodRecord();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean setKeyguardDisabled(ComponentName admin, boolean disabled) {
        throwIfParentInstance("setKeyguardDisabled");
        try {
            return this.mService.setKeyguardDisabled(admin, disabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean setStatusBarDisabled(ComponentName admin, boolean disabled) {
        throwIfParentInstance("setStatusBarDisabled");
        try {
            return this.mService.setStatusBarDisabled(admin, this.mContext.getPackageName(), disabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isStatusBarDisabled() {
        throwIfParentInstance("isStatusBarDisabled");
        try {
            return this.mService.isStatusBarDisabled(this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyPendingSystemUpdate(long updateReceivedTime) {
        throwIfParentInstance("notifyPendingSystemUpdate");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.notifyPendingSystemUpdate(SystemUpdateInfo.m196of(updateReceivedTime));
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public void notifyPendingSystemUpdate(long updateReceivedTime, boolean isSecurityPatch) {
        throwIfParentInstance("notifyPendingSystemUpdate");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.notifyPendingSystemUpdate(SystemUpdateInfo.m195of(updateReceivedTime, isSecurityPatch));
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public SystemUpdateInfo getPendingSystemUpdate(ComponentName admin) {
        throwIfParentInstance("getPendingSystemUpdate");
        try {
            return this.mService.getPendingSystemUpdate(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setPermissionPolicy(ComponentName admin, int policy) {
        throwIfParentInstance("setPermissionPolicy");
        try {
            this.mService.setPermissionPolicy(admin, this.mContext.getPackageName(), policy);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getPermissionPolicy(ComponentName admin) {
        throwIfParentInstance("getPermissionPolicy");
        try {
            return this.mService.getPermissionPolicy(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean setPermissionGrantState(ComponentName admin, String packageName, String permission, int grantState) {
        throwIfParentInstance("setPermissionGrantState");
        try {
            final CompletableFuture<Boolean> result = new CompletableFuture<>();
            this.mService.setPermissionGrantState(admin, this.mContext.getPackageName(), packageName, permission, grantState, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda1
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    result.complete(Boolean.valueOf(b != null));
                }
            }));
            BackgroundThread.getHandler().sendMessageDelayed(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((CompletableFuture) obj).complete((Boolean) obj2);
                }
            }, result, false), 20000L);
            return result.get().booleanValue();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPermissionGrantState(ComponentName admin, String packageName, String permission) {
        throwIfParentInstance("getPermissionGrantState");
        try {
            return this.mService.getPermissionGrantState(admin, this.mContext.getPackageName(), packageName, permission);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isProvisioningAllowed(String action) {
        throwIfParentInstance("isProvisioningAllowed");
        try {
            return this.mService.isProvisioningAllowed(action, this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int checkProvisioningPrecondition(String action, String packageName) {
        try {
            return this.mService.checkProvisioningPrecondition(action, packageName);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isManagedProfile(ComponentName admin) {
        throwIfParentInstance("isManagedProfile");
        try {
            return this.mService.isManagedProfile(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public String getWifiMacAddress(ComponentName admin) {
        throwIfParentInstance("getWifiMacAddress");
        try {
            return this.mService.getWifiMacAddress(admin, this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void reboot(ComponentName admin) {
        throwIfParentInstance("reboot");
        try {
            this.mService.reboot(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setShortSupportMessage(ComponentName admin, CharSequence message) {
        throwIfParentInstance("setShortSupportMessage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setShortSupportMessage(admin, this.mContext.getPackageName(), message);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public CharSequence getShortSupportMessage(ComponentName admin) {
        throwIfParentInstance("getShortSupportMessage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getShortSupportMessage(admin, this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setLongSupportMessage(ComponentName admin, CharSequence message) {
        throwIfParentInstance("setLongSupportMessage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setLongSupportMessage(admin, message);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public CharSequence getLongSupportMessage(ComponentName admin) {
        throwIfParentInstance("getLongSupportMessage");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getLongSupportMessage(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public CharSequence getShortSupportMessageForUser(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getShortSupportMessageForUser(admin, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public CharSequence getLongSupportMessageForUser(ComponentName admin, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getLongSupportMessageForUser(admin, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public DevicePolicyManager getParentProfileInstance(ComponentName admin) {
        throwIfParentInstance("getParentProfileInstance");
        try {
            if (!this.mService.isManagedProfile(admin)) {
                throw new SecurityException("The current user does not have a parent profile.");
            }
            return new DevicePolicyManager(this.mContext, this.mService, true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setSecurityLoggingEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setSecurityLoggingEnabled");
        try {
            this.mService.setSecurityLoggingEnabled(admin, this.mContext.getPackageName(), enabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isSecurityLoggingEnabled(ComponentName admin) {
        throwIfParentInstance("isSecurityLoggingEnabled");
        try {
            return this.mService.isSecurityLoggingEnabled(admin, this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<SecurityLog.SecurityEvent> retrieveSecurityLogs(ComponentName admin) {
        throwIfParentInstance("retrieveSecurityLogs");
        try {
            ParceledListSlice<SecurityLog.SecurityEvent> list = this.mService.retrieveSecurityLogs(admin, this.mContext.getPackageName());
            if (list != null) {
                return list.getList();
            }
            return null;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long forceNetworkLogs() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return -1L;
        }
        try {
            return iDevicePolicyManager.forceNetworkLogs();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long forceSecurityLogs() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0L;
        }
        try {
            return iDevicePolicyManager.forceSecurityLogs();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public DevicePolicyManager getParentProfileInstance(UserInfo uInfo) {
        this.mContext.checkSelfPermission(Manifest.C0000permission.MANAGE_PROFILE_AND_DEVICE_OWNERS);
        if (!uInfo.isManagedProfile()) {
            throw new SecurityException("The user " + uInfo.f48id + " does not have a parent profile.");
        }
        return new DevicePolicyManager(this.mContext, this.mService, true);
    }

    public List<String> setMeteredDataDisabledPackages(ComponentName admin, List<String> packageNames) {
        throwIfParentInstance("setMeteredDataDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.setMeteredDataDisabledPackages(admin, packageNames);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return packageNames;
    }

    public List<String> getMeteredDataDisabledPackages(ComponentName admin) {
        throwIfParentInstance("getMeteredDataDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getMeteredDataDisabledPackages(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return new ArrayList();
    }

    public boolean isMeteredDataDisabledPackageForUser(ComponentName admin, String packageName, int userId) {
        throwIfParentInstance("getMeteredDataDisabledForUser");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isMeteredDataDisabledPackageForUser(admin, packageName, userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<SecurityLog.SecurityEvent> retrievePreRebootSecurityLogs(ComponentName admin) {
        throwIfParentInstance("retrievePreRebootSecurityLogs");
        try {
            ParceledListSlice<SecurityLog.SecurityEvent> list = this.mService.retrievePreRebootSecurityLogs(admin, this.mContext.getPackageName());
            if (list != null) {
                return list.getList();
            }
            return null;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setOrganizationColor(ComponentName admin, int color) {
        throwIfParentInstance("setOrganizationColor");
        try {
            this.mService.setOrganizationColor(admin, color | (-16777216));
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setOrganizationColorForUser(int color, int userId) {
        try {
            this.mService.setOrganizationColorForUser(color | (-16777216), userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int getOrganizationColor(ComponentName admin) {
        throwIfParentInstance("getOrganizationColor");
        try {
            return this.mService.getOrganizationColor(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int getOrganizationColorForUser(int userHandle) {
        try {
            return this.mService.getOrganizationColorForUser(userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setOrganizationName(ComponentName admin, CharSequence title) {
        throwIfParentInstance("setOrganizationName");
        try {
            this.mService.setOrganizationName(admin, this.mContext.getPackageName(), title);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public CharSequence getOrganizationName(ComponentName admin) {
        throwIfParentInstance("getOrganizationName");
        try {
            return this.mService.getOrganizationName(admin, this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$new$8(Object query) throws RemoteException {
        return getService().getDeviceOwnerOrganizationName();
    }

    @SystemApi
    public CharSequence getDeviceOwnerOrganizationName() {
        return this.mGetDeviceOwnerOrganizationNameCache.query(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$new$9(Integer query) throws RemoteException {
        return getService().getOrganizationNameForUser(query.intValue());
    }

    public CharSequence getOrganizationNameForUser(int userHandle) {
        return this.mGetOrganizationNameForUserCache.query(Integer.valueOf(userHandle));
    }

    @SystemApi
    public int getUserProvisioningState() {
        throwIfParentInstance("getUserProvisioningState");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getUserProvisioningState(this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public void setUserProvisioningState(int state, int userHandle) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUserProvisioningState(state, userHandle);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public void setUserProvisioningState(int state, UserHandle userHandle) {
        setUserProvisioningState(state, userHandle.getIdentifier());
    }

    public void setAffiliationIds(ComponentName admin, Set<String> ids) {
        throwIfParentInstance("setAffiliationIds");
        if (ids == null) {
            throw new IllegalArgumentException("ids must not be null");
        }
        try {
            this.mService.setAffiliationIds(admin, new ArrayList(ids));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Set<String> getAffiliationIds(ComponentName admin) {
        throwIfParentInstance("getAffiliationIds");
        try {
            return new ArraySet(this.mService.getAffiliationIds(admin));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAffiliatedUser() {
        throwIfParentInstance("isAffiliatedUser");
        try {
            return this.mService.isCallingUserAffiliated();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAffiliatedUser(int userId) {
        try {
            return this.mService.isAffiliatedUser(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isUninstallInQueue(String packageName) {
        try {
            return this.mService.isUninstallInQueue(packageName);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void uninstallPackageWithActiveAdmins(String packageName) {
        try {
            this.mService.uninstallPackageWithActiveAdmins(packageName);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void forceRemoveActiveAdmin(ComponentName adminReceiver, int userHandle) {
        try {
            this.mService.forceRemoveActiveAdmin(adminReceiver, userHandle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isDeviceProvisioned() {
        try {
            return this.mService.isDeviceProvisioned();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setDeviceProvisioningConfigApplied() {
        try {
            this.mService.setDeviceProvisioningConfigApplied();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isDeviceProvisioningConfigApplied() {
        try {
            return this.mService.isDeviceProvisioningConfigApplied();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void forceUpdateUserSetupComplete(int userId) {
        try {
            this.mService.forceUpdateUserSetupComplete(userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    private void throwIfParentInstance(String functionName) {
        if (this.mParentInstance) {
            throw new SecurityException(functionName + " cannot be called on the parent instance");
        }
    }

    public void setBackupServiceEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setBackupServiceEnabled");
        try {
            this.mService.setBackupServiceEnabled(admin, enabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isBackupServiceEnabled(ComponentName admin) {
        throwIfParentInstance("isBackupServiceEnabled");
        try {
            return this.mService.isBackupServiceEnabled(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setNetworkLoggingEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setNetworkLoggingEnabled");
        try {
            this.mService.setNetworkLoggingEnabled(admin, this.mContext.getPackageName(), enabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$new$10(ComponentName admin) throws RemoteException {
        return Boolean.valueOf(getService().isNetworkLoggingEnabled(admin, getContext().getPackageName()));
    }

    public boolean isNetworkLoggingEnabled(ComponentName admin) {
        throwIfParentInstance("isNetworkLoggingEnabled");
        return this.mIsNetworkLoggingEnabledCache.query(admin).booleanValue();
    }

    public List<NetworkEvent> retrieveNetworkLogs(ComponentName admin, long batchToken) {
        throwIfParentInstance("retrieveNetworkLogs");
        try {
            return this.mService.retrieveNetworkLogs(admin, this.mContext.getPackageName(), batchToken);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean bindDeviceAdminServiceAsUser(ComponentName admin, Intent serviceIntent, ServiceConnection conn, int flags, UserHandle targetUser) {
        throwIfParentInstance("bindDeviceAdminServiceAsUser");
        try {
            Context context = this.mContext;
            try {
                IServiceConnection sd = context.getServiceDispatcher(conn, context.getMainThreadHandler(), Integer.toUnsignedLong(flags));
                try {
                    serviceIntent.prepareToLeaveProcess(this.mContext);
                    return this.mService.bindDeviceAdminServiceAsUser(admin, this.mContext.getIApplicationThread(), this.mContext.getActivityToken(), serviceIntent, sd, Integer.toUnsignedLong(flags), targetUser.getIdentifier());
                } catch (RemoteException e) {
                    re = e;
                    throw re.rethrowFromSystemServer();
                }
            } catch (RemoteException e2) {
                re = e2;
            }
        } catch (RemoteException e3) {
            re = e3;
        }
    }

    public boolean bindDeviceAdminServiceAsUser(ComponentName admin, Intent serviceIntent, ServiceConnection conn, Context.BindServiceFlags flags, UserHandle targetUser) {
        throwIfParentInstance("bindDeviceAdminServiceAsUser");
        try {
            Context context = this.mContext;
            try {
                IServiceConnection sd = context.getServiceDispatcher(conn, context.getMainThreadHandler(), flags.getValue());
                try {
                    serviceIntent.prepareToLeaveProcess(this.mContext);
                    return this.mService.bindDeviceAdminServiceAsUser(admin, this.mContext.getIApplicationThread(), this.mContext.getActivityToken(), serviceIntent, sd, flags.getValue(), targetUser.getIdentifier());
                } catch (RemoteException e) {
                    re = e;
                    throw re.rethrowFromSystemServer();
                }
            } catch (RemoteException e2) {
                re = e2;
            }
        } catch (RemoteException e3) {
            re = e3;
        }
    }

    public List<UserHandle> getBindDeviceAdminTargetUsers(ComponentName admin) {
        throwIfParentInstance("getBindDeviceAdminTargetUsers");
        try {
            return this.mService.getBindDeviceAdminTargetUsers(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getLastSecurityLogRetrievalTime() {
        try {
            return this.mService.getLastSecurityLogRetrievalTime();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getLastBugReportRequestTime() {
        try {
            return this.mService.getLastBugReportRequestTime();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public long getLastNetworkLogRetrievalTime() {
        try {
            return this.mService.getLastNetworkLogRetrievalTime();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isCurrentInputMethodSetByOwner() {
        try {
            return this.mService.isCurrentInputMethodSetByOwner();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public List<String> getOwnerInstalledCaCerts(UserHandle user) {
        try {
            return this.mService.getOwnerInstalledCaCerts(user).getList();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isFactoryResetProtectionPolicySupported() {
        try {
            return this.mService.isFactoryResetProtectionPolicySupported();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void clearApplicationUserData(ComponentName admin, String packageName, Executor executor, OnClearApplicationUserDataListener listener) {
        throwIfParentInstance("clearAppData");
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        try {
            this.mService.clearApplicationUserData(admin, packageName, new BinderC03841(executor, listener));
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.app.admin.DevicePolicyManager$1 */
    /* loaded from: classes.dex */
    class BinderC03841 extends IPackageDataObserver.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ OnClearApplicationUserDataListener val$listener;

        BinderC03841(Executor executor, OnClearApplicationUserDataListener onClearApplicationUserDataListener) {
            this.val$executor = executor;
            this.val$listener = onClearApplicationUserDataListener;
        }

        @Override // android.content.p001pm.IPackageDataObserver
        public void onRemoveCompleted(final String pkg, final boolean succeeded) {
            Executor executor = this.val$executor;
            final OnClearApplicationUserDataListener onClearApplicationUserDataListener = this.val$listener;
            executor.execute(new Runnable() { // from class: android.app.admin.DevicePolicyManager$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DevicePolicyManager.OnClearApplicationUserDataListener.this.onApplicationUserDataCleared(pkg, succeeded);
                }
            });
        }
    }

    public void setLogoutEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setLogoutEnabled");
        try {
            this.mService.setLogoutEnabled(admin, enabled);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean isLogoutEnabled() {
        throwIfParentInstance("isLogoutEnabled");
        try {
            return this.mService.isLogoutEnabled();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Set<String> getDisallowedSystemApps(ComponentName admin, int userId, String provisioningAction) {
        try {
            return new ArraySet(this.mService.getDisallowedSystemApps(admin, userId, provisioningAction));
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void transferOwnership(ComponentName admin, ComponentName target, PersistableBundle bundle) {
        throwIfParentInstance("transferOwnership");
        try {
            this.mService.transferOwnership(admin, target, bundle);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setStartUserSessionMessage(ComponentName admin, CharSequence startUserSessionMessage) {
        throwIfParentInstance("setStartUserSessionMessage");
        try {
            this.mService.setStartUserSessionMessage(admin, startUserSessionMessage);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setEndUserSessionMessage(ComponentName admin, CharSequence endUserSessionMessage) {
        throwIfParentInstance("setEndUserSessionMessage");
        try {
            this.mService.setEndUserSessionMessage(admin, endUserSessionMessage);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public CharSequence getStartUserSessionMessage(ComponentName admin) {
        throwIfParentInstance("getStartUserSessionMessage");
        try {
            return this.mService.getStartUserSessionMessage(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public CharSequence getEndUserSessionMessage(ComponentName admin) {
        throwIfParentInstance("getEndUserSessionMessage");
        try {
            return this.mService.getEndUserSessionMessage(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int addOverrideApn(ComponentName admin, ApnSetting apnSetting) {
        throwIfParentInstance("addOverrideApn");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.addOverrideApn(admin, apnSetting);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return -1;
    }

    public boolean updateOverrideApn(ComponentName admin, int apnId, ApnSetting apnSetting) {
        throwIfParentInstance("updateOverrideApn");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.updateOverrideApn(admin, apnId, apnSetting);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean removeOverrideApn(ComponentName admin, int apnId) {
        throwIfParentInstance("removeOverrideApn");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.removeOverrideApn(admin, apnId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<ApnSetting> getOverrideApns(ComponentName admin) {
        throwIfParentInstance("getOverrideApns");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getOverrideApns(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public void setOverrideApnsEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setOverrideApnEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setOverrideApnsEnabled(admin, enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isOverrideApnEnabled(ComponentName admin) {
        throwIfParentInstance("isOverrideApnEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isOverrideApnEnabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public PersistableBundle getTransferOwnershipBundle() {
        throwIfParentInstance("getTransferOwnershipBundle");
        try {
            return this.mService.getTransferOwnershipBundle();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int setGlobalPrivateDnsModeOpportunistic(ComponentName admin) {
        throwIfParentInstance("setGlobalPrivateDnsModeOpportunistic");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 2;
        }
        try {
            return iDevicePolicyManager.setGlobalPrivateDns(admin, 2, null);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int setGlobalPrivateDnsModeSpecifiedHost(ComponentName admin, String privateDnsHost) {
        throwIfParentInstance("setGlobalPrivateDnsModeSpecifiedHost");
        Objects.requireNonNull(privateDnsHost, "dns resolver is null");
        if (this.mService == null) {
            return 2;
        }
        if (NetworkUtilsInternal.isWeaklyValidatedHostname(privateDnsHost) && !PrivateDnsConnectivityChecker.canConnectToPrivateDnsServer(privateDnsHost)) {
            return 1;
        }
        try {
            return this.mService.setGlobalPrivateDns(admin, 3, privateDnsHost);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void installSystemUpdate(ComponentName admin, Uri updateFilePath, final Executor executor, final InstallSystemUpdateCallback callback) {
        throwIfParentInstance("installUpdate");
        if (this.mService == null) {
            return;
        }
        try {
            ParcelFileDescriptor fileDescriptor = this.mContext.getContentResolver().openFileDescriptor(updateFilePath, "r");
            try {
                this.mService.installUpdateFromFile(admin, this.mContext.getPackageName(), fileDescriptor, new StartInstallingUpdateCallback.Stub() { // from class: android.app.admin.DevicePolicyManager.2
                    @Override // android.app.admin.StartInstallingUpdateCallback
                    public void onStartInstallingUpdateError(int errorCode, String errorMessage) {
                        DevicePolicyManager.this.executeCallback(errorCode, errorMessage, executor, callback);
                    }
                });
                if (fileDescriptor != null) {
                    fileDescriptor.close();
                }
            } catch (Throwable th) {
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (FileNotFoundException e2) {
            Log.m102w(TAG, e2);
            executeCallback(4, Log.getStackTraceString(e2), executor, callback);
        } catch (IOException e3) {
            Log.m102w(TAG, e3);
            executeCallback(1, Log.getStackTraceString(e3), executor, callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeCallback(final int errorCode, final String errorMessage, Executor executor, final InstallSystemUpdateCallback callback) {
        executor.execute(new Runnable() { // from class: android.app.admin.DevicePolicyManager$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManager.InstallSystemUpdateCallback.this.onInstallUpdateError(errorCode, errorMessage);
            }
        });
    }

    public int getGlobalPrivateDnsMode(ComponentName admin) {
        throwIfParentInstance("setGlobalPrivateDns");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getGlobalPrivateDnsMode(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public String getGlobalPrivateDnsHost(ComponentName admin) {
        throwIfParentInstance("setGlobalPrivateDns");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getGlobalPrivateDnsHost(admin);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public void setProfileOwnerCanAccessDeviceIds(ComponentName who) {
        ApplicationInfo ai = this.mContext.getApplicationInfo();
        if (ai.targetSdkVersion > 29) {
            throw new UnsupportedOperationException("This method is deprecated. use markProfileOwnerOnOrganizationOwnedDevice instead.");
        }
        setProfileOwnerOnOrganizationOwnedDevice(who, true);
    }

    public void setProfileOwnerOnOrganizationOwnedDevice(ComponentName who, boolean isProfileOwnerOnOrganizationOwnedDevice) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setProfileOwnerOnOrganizationOwnedDevice(who, myUserId(), isProfileOwnerOnOrganizationOwnedDevice);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setCrossProfileCalendarPackages(ComponentName admin, Set<String> packageNames) {
        ArrayList arrayList;
        throwIfParentInstance("setCrossProfileCalendarPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            if (packageNames == null) {
                arrayList = null;
            } else {
                try {
                    arrayList = new ArrayList(packageNames);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            iDevicePolicyManager.setCrossProfileCalendarPackages(admin, arrayList);
        }
    }

    @Deprecated
    public Set<String> getCrossProfileCalendarPackages(ComponentName admin) {
        throwIfParentInstance("getCrossProfileCalendarPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                List<String> packageNames = iDevicePolicyManager.getCrossProfileCalendarPackages(admin);
                if (packageNames == null) {
                    return null;
                }
                return new ArraySet(packageNames);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptySet();
    }

    public boolean isPackageAllowedToAccessCalendar(String packageName) {
        throwIfParentInstance("isPackageAllowedToAccessCalendar");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isPackageAllowedToAccessCalendarForUser(packageName, myUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public Set<String> getCrossProfileCalendarPackages() {
        throwIfParentInstance("getCrossProfileCalendarPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                List<String> packageNames = iDevicePolicyManager.getCrossProfileCalendarPackagesForUser(myUserId());
                if (packageNames == null) {
                    return null;
                }
                return new ArraySet(packageNames);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptySet();
    }

    public void setCrossProfilePackages(ComponentName admin, Set<String> packageNames) {
        throwIfParentInstance("setCrossProfilePackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCrossProfilePackages(admin, new ArrayList(packageNames));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public Set<String> getCrossProfilePackages(ComponentName admin) {
        throwIfParentInstance("getCrossProfilePackages");
        if (this.mService != null) {
            try {
                return new ArraySet(this.mService.getCrossProfilePackages(admin));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptySet();
    }

    public Set<String> getAllCrossProfilePackages() {
        throwIfParentInstance("getAllCrossProfilePackages");
        if (this.mService != null) {
            try {
                return new ArraySet(this.mService.getAllCrossProfilePackages());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptySet();
    }

    public Set<String> getDefaultCrossProfilePackages() {
        throwIfParentInstance("getDefaultCrossProfilePackages");
        if (this.mService != null) {
            try {
                return new ArraySet(this.mService.getDefaultCrossProfilePackages());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptySet();
    }

    @SystemApi
    public boolean isManagedKiosk() {
        throwIfParentInstance("isManagedKiosk");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isManagedKiosk();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public boolean isUnattendedManagedKiosk() {
        throwIfParentInstance("isUnattendedManagedKiosk");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isUnattendedManagedKiosk();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean startViewCalendarEventInManagedProfile(long eventId, long start, long end, boolean allDay, int flags) {
        throwIfParentInstance("startViewCalendarEventInManagedProfile");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.startViewCalendarEventInManagedProfile(this.mContext.getPackageName(), eventId, start, end, allDay, flags);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public void setApplicationExemptions(String packageName, Set<Integer> exemptions) throws PackageManager.NameNotFoundException {
        throwIfParentInstance("setApplicationExemptions");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setApplicationExemptions(packageName, ArrayUtils.convertToIntArray(new ArraySet(exemptions)));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (ServiceSpecificException e2) {
                switch (e2.errorCode) {
                    case 1:
                        throw new PackageManager.NameNotFoundException(e2.getMessage());
                    default:
                        throw new RuntimeException("Unknown error setting application exemptions: " + e2.errorCode, e2);
                }
            }
        }
    }

    @SystemApi
    public Set<Integer> getApplicationExemptions(String packageName) throws PackageManager.NameNotFoundException {
        throwIfParentInstance("getApplicationExemptions");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return Collections.emptySet();
        }
        try {
            return intArrayToSet(iDevicePolicyManager.getApplicationExemptions(packageName));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            switch (e2.errorCode) {
                case 1:
                    throw new PackageManager.NameNotFoundException(e2.getMessage());
                default:
                    throw new RuntimeException("Unknown error getting application exemptions: " + e2.errorCode, e2);
            }
        }
    }

    private Set<Integer> intArrayToSet(int[] array) {
        Set<Integer> set = new ArraySet<>();
        for (int item : array) {
            set.add(Integer.valueOf(item));
        }
        return set;
    }

    public void setUserControlDisabledPackages(ComponentName admin, List<String> packages) {
        throwIfParentInstance("setUserControlDisabledPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUserControlDisabledPackages(admin, this.mContext.getPackageName(), packages);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public List<String> getUserControlDisabledPackages(ComponentName admin) {
        throwIfParentInstance("getUserControlDisabledPackages");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getUserControlDisabledPackages(admin, this.mContext.getPackageName());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public void setCommonCriteriaModeEnabled(ComponentName admin, boolean enabled) {
        throwIfParentInstance("setCommonCriteriaModeEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setCommonCriteriaModeEnabled(admin, this.mContext.getPackageName(), enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isCommonCriteriaModeEnabled(ComponentName admin) {
        throwIfParentInstance("isCommonCriteriaModeEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isCommonCriteriaModeEnabled(admin);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public int getPersonalAppsSuspendedReasons(ComponentName admin) {
        throwIfParentInstance("getPersonalAppsSuspendedReasons");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPersonalAppsSuspendedReasons(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public void setPersonalAppsSuspended(ComponentName admin, boolean suspended) {
        throwIfParentInstance("setPersonalAppsSuspended");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setPersonalAppsSuspended(admin, suspended);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public void setManagedProfileMaximumTimeOff(ComponentName admin, long timeoutMillis) {
        throwIfParentInstance("setManagedProfileMaximumTimeOff");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setManagedProfileMaximumTimeOff(admin, timeoutMillis);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public long getManagedProfileMaximumTimeOff(ComponentName admin) {
        throwIfParentInstance("getManagedProfileMaximumTimeOff");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getManagedProfileMaximumTimeOff(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return 0L;
    }

    public void acknowledgeDeviceCompliant() {
        throwIfParentInstance("acknowledgeDeviceCompliant");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.acknowledgeDeviceCompliant();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean isComplianceAcknowledgementRequired() {
        throwIfParentInstance("isComplianceAcknowledgementRequired");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isComplianceAcknowledgementRequired();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean canProfileOwnerResetPasswordWhenLocked(int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.canProfileOwnerResetPasswordWhenLocked(userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public void setNextOperationSafety(int operation, int reason) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setNextOperationSafety(operation, reason);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public String getEnrollmentSpecificId() {
        throwIfParentInstance("getEnrollmentSpecificId");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return "";
        }
        try {
            return iDevicePolicyManager.getEnrollmentSpecificId(this.mContext.getPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setOrganizationId(String enterpriseId) {
        throwIfParentInstance("setOrganizationId");
        setOrganizationIdForUser(this.mContext.getPackageName(), enterpriseId, myUserId());
    }

    public void setOrganizationIdForUser(String packageName, String enterpriseId, int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setOrganizationIdForUser(packageName, enterpriseId, userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void clearOrganizationId() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.clearOrganizationIdForUser(myUserId());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public UserHandle createAndProvisionManagedProfile(ManagedProfileProvisioningParams provisioningParams) throws ProvisioningException {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.createAndProvisionManagedProfile(provisioningParams, this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw new ProvisioningException(e2, e2.errorCode, getErrorMessage(e2));
        }
    }

    @SystemApi
    public void finalizeWorkProfileProvisioning(UserHandle managedProfileUser, Account migratedAccount) {
        Objects.requireNonNull(managedProfileUser, "managedProfileUser can't be null");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            throw new IllegalStateException("Could not find DevicePolicyManagerService");
        }
        try {
            iDevicePolicyManager.finalizeWorkProfileProvisioning(managedProfileUser, migratedAccount);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private String getErrorMessage(ServiceSpecificException e) {
        return null;
    }

    @SystemApi
    public void provisionFullyManagedDevice(FullyManagedDeviceProvisioningParams provisioningParams) throws ProvisioningException {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.provisionFullyManagedDevice(provisioningParams, this.mContext.getPackageName());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            } catch (ServiceSpecificException e) {
                throw new ProvisioningException(e, e.errorCode, getErrorMessage(e));
            }
        }
    }

    public void resetDefaultCrossProfileIntentFilters(int userId) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.resetDefaultCrossProfileIntentFilters(userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean canAdminGrantSensorsPermissions() {
        throwIfParentInstance("canAdminGrantSensorsPermissions");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return false;
        }
        try {
            return iDevicePolicyManager.canAdminGrantSensorsPermissions();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void setDeviceOwnerType(ComponentName admin, int deviceOwnerType) {
        throwIfParentInstance("setDeviceOwnerType");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDeviceOwnerType(admin, deviceOwnerType);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public int getDeviceOwnerType(ComponentName admin) {
        throwIfParentInstance("getDeviceOwnerType");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDeviceOwnerType(admin);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return 0;
    }

    public boolean isFinancedDevice() {
        return isDeviceManaged() && getDeviceOwnerType(getDeviceOwnerComponentOnAnyUser()) == 1;
    }

    public void setUsbDataSignalingEnabled(boolean enabled) {
        throwIfParentInstance("setUsbDataSignalingEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setUsbDataSignalingEnabled(this.mContext.getPackageName(), enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isUsbDataSignalingEnabled() {
        throwIfParentInstance("isUsbDataSignalingEnabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isUsbDataSignalingEnabled(this.mContext.getPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public boolean isUsbDataSignalingEnabledForUser(int userId) {
        throwIfParentInstance("isUsbDataSignalingEnabledForUser");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isUsbDataSignalingEnabledForUser(userId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return true;
    }

    public boolean canUsbDataSignalingBeDisabled() {
        throwIfParentInstance("canUsbDataSignalingBeDisabled");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.canUsbDataSignalingBeDisabled();
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public List<UserHandle> listForegroundAffiliatedUsers() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return Collections.emptyList();
        }
        try {
            return iDevicePolicyManager.listForegroundAffiliatedUsers();
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Set<String> getPolicyExemptApps() {
        if (this.mService == null) {
            return Collections.emptySet();
        }
        try {
            return new HashSet(this.mService.listPolicyExemptApps());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Intent createProvisioningIntentFromNfcIntent(Intent nfcIntent) {
        return ProvisioningIntentHelper.createProvisioningIntentFromNfcIntent(nfcIntent);
    }

    public void setMinimumRequiredWifiSecurityLevel(int level) {
        throwIfParentInstance("setMinimumRequiredWifiSecurityLevel");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setMinimumRequiredWifiSecurityLevel(this.mContext.getPackageName(), level);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int getMinimumRequiredWifiSecurityLevel() {
        throwIfParentInstance("getMinimumRequiredWifiSecurityLevel");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return 0;
        }
        try {
            return iDevicePolicyManager.getMinimumRequiredWifiSecurityLevel();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setWifiSsidPolicy(WifiSsidPolicy policy) {
        throwIfParentInstance("setWifiSsidPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return;
        }
        try {
            iDevicePolicyManager.setWifiSsidPolicy(this.mContext.getPackageName(), policy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public WifiSsidPolicy getWifiSsidPolicy() {
        throwIfParentInstance("getWifiSsidPolicy");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager == null) {
            return null;
        }
        try {
            return iDevicePolicyManager.getWifiSsidPolicy(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public DevicePolicyResourcesManager getResources() {
        return this.mResourcesManager;
    }

    @SystemApi
    public boolean isDpcDownloaded() {
        throwIfParentInstance("isDpcDownloaded");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.isDpcDownloaded();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public void setDpcDownloaded(boolean downloaded) {
        throwIfParentInstance("setDpcDownloaded");
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDpcDownloaded(downloaded);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String getDevicePolicyManagementRoleHolderPackage() {
        String devicePolicyManagementConfig = this.mContext.getString(17039421);
        return extractPackageNameFromDeviceManagerConfig(devicePolicyManagementConfig);
    }

    public String getDevicePolicyManagementRoleHolderUpdaterPackage() {
        String devicePolicyManagementUpdaterConfig = this.mContext.getString(C4057R.string.config_devicePolicyManagementUpdater);
        if (TextUtils.isEmpty(devicePolicyManagementUpdaterConfig)) {
            return null;
        }
        return devicePolicyManagementUpdaterConfig;
    }

    @SystemApi
    public List<UserHandle> getPolicyManagedProfiles(UserHandle user) {
        Objects.requireNonNull(user);
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getPolicyManagedProfiles(user);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    private String extractPackageNameFromDeviceManagerConfig(String deviceManagerConfig) {
        if (TextUtils.isEmpty(deviceManagerConfig)) {
            return null;
        }
        if (deviceManagerConfig.contains(":")) {
            return deviceManagerConfig.split(":")[0];
        }
        return deviceManagerConfig;
    }

    /* renamed from: resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
    public void m198xce7fef36() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.mo197xce7fef36();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public boolean shouldAllowBypassingDevicePolicyManagementRoleQualification() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.shouldAllowBypassingDevicePolicyManagementRoleQualification();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public DevicePolicyState getDevicePolicyState() {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.getDevicePolicyState();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void setOverrideKeepProfilesRunning(boolean enabled) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setOverrideKeepProfilesRunning(enabled);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean triggerDevicePolicyEngineMigration(boolean forceMigration) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                return iDevicePolicyManager.triggerDevicePolicyEngineMigration(forceMigration);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }
}
