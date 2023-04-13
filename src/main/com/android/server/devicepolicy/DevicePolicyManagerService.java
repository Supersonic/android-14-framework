package com.android.server.devicepolicy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.BooleanPolicyValue;
import android.app.admin.BundlePolicyValue;
import android.app.admin.ComponentNamePolicyValue;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyCache;
import android.app.admin.DevicePolicyDrawableResource;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.DevicePolicyManagerLiteInternal;
import android.app.admin.DevicePolicySafetyChecker;
import android.app.admin.DevicePolicyState;
import android.app.admin.DevicePolicyStringResource;
import android.app.admin.DeviceStateCache;
import android.app.admin.FactoryResetProtectionPolicy;
import android.app.admin.FullyManagedDeviceProvisioningParams;
import android.app.admin.IDevicePolicyManager;
import android.app.admin.IntegerPolicyValue;
import android.app.admin.IntentFilterPolicyKey;
import android.app.admin.LockTaskPolicy;
import android.app.admin.LongPolicyValue;
import android.app.admin.ManagedProfileProvisioningParams;
import android.app.admin.ManagedSubscriptionsPolicy;
import android.app.admin.NetworkEvent;
import android.app.admin.PackagePolicy;
import android.app.admin.ParcelableGranteeMap;
import android.app.admin.ParcelableResource;
import android.app.admin.PasswordMetrics;
import android.app.admin.PasswordPolicy;
import android.app.admin.PolicyKey;
import android.app.admin.PolicyValue;
import android.app.admin.PreferentialNetworkServiceConfig;
import android.app.admin.SecurityLog;
import android.app.admin.StartInstallingUpdateCallback;
import android.app.admin.StringSetPolicyValue;
import android.app.admin.SystemUpdateInfo;
import android.app.admin.SystemUpdatePolicy;
import android.app.admin.UnsafeStateException;
import android.app.admin.UserRestrictionPolicyKey;
import android.app.admin.WifiSsidPolicy;
import android.app.backup.IBackupManager;
import android.app.compat.CompatChanges;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.app.trust.TrustManager;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.PermissionChecker;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.CrossProfileApps;
import android.content.pm.CrossProfileAppsInternal;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.pm.StringParceledListSlice;
import android.content.pm.UserInfo;
import android.content.pm.UserPackage;
import android.content.pm.parsing.FrameworkParsingPackageUtils;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.ConnectivitySettingsManager;
import android.net.IIpConnectivityMetrics;
import android.net.ProfileNetworkPreference;
import android.net.ProxyInfo;
import android.net.Uri;
import android.net.VpnManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.p005os.IInstalld;
import android.permission.AdminPermissionControlParams;
import android.permission.IPermissionManager;
import android.permission.PermissionControllerManager;
import android.provider.ContactsContract;
import android.provider.ContactsInternal;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.provider.Telephony;
import android.security.AppUriAuthenticationPolicy;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.ParcelableKeyGenParameterSpec;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.DebugUtils;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.IWindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.LocalePicker;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.net.NetworkUtilsInternal;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.Preconditions;
import com.android.internal.util.StatLogger;
import com.android.internal.util.jobs.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockSettingsInternal;
import com.android.internal.widget.LockscreenCredential;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.net.module.util.ProxyUtils;
import com.android.server.AlarmManagerInternal;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.PersistentDataBlockManagerInternal;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.SystemService;
import com.android.server.devicepolicy.ActiveAdmin;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.devicepolicy.TransferOwnershipMetadataManager;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.net.NetworkPolicyManagerInternal;
import com.android.server.p011pm.DefaultCrossProfileIntentFilter;
import com.android.server.p011pm.DefaultCrossProfileIntentFiltersUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.RestrictionsSet;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserRestrictionsUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.storage.DeviceStorageMonitorInternal;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.utils.Slogf;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DevicePolicyManagerService extends IDevicePolicyManager.Stub {
    @VisibleForTesting
    static final String ACTION_PROFILE_OFF_DEADLINE = "com.android.server.ACTION_PROFILE_OFF_DEADLINE";
    @VisibleForTesting
    static final String ACTION_TURN_PROFILE_ON_NOTIFICATION = "com.android.server.ACTION_TURN_PROFILE_ON_NOTIFICATION";
    public static final HashMap<String, Integer> ACTIVE_ADMIN_POLICIES;
    public static final Map<Integer, String> APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS;
    public static final HashMap<String, String> CROSS_USER_PERMISSIONS;
    public static final Set<Integer> DA_DISALLOWED_POLICIES;
    public static final List<String> DEFAULT_DEVICE_OWNER_PERMISSIONS;
    public static final HashMap<String, String> DELEGATE_SCOPES;
    public static final String[] DELEGATIONS;
    public static final List<String> DEVICE_OWNER_OR_MANAGED_PROFILE_OWNER_DELEGATIONS;

    /* renamed from: DEVICE_OWNER_OR_ORGANIZATION_OWNED_MANAGED_PROFILE_OWNER_DELEGATIONS */
    public static final List<String> f1138xcbb66b4a;
    public static final HashMap<Integer, List<String>> DPC_PERMISSIONS;
    public static final List<String> EXCLUSIVE_DELEGATIONS;
    public static final long EXPIRATION_GRACE_PERIOD_MS;
    public static final List<String> FINANCED_DEVICE_OWNER_PERMISSIONS;
    public static final Set<String> GLOBAL_SETTINGS_ALLOWLIST;
    public static final Set<String> GLOBAL_SETTINGS_DEPRECATED;
    public static final long MANAGED_PROFILE_MAXIMUM_TIME_OFF_THRESHOLD;
    public static final long MANAGED_PROFILE_OFF_WARNING_PERIOD;
    public static final long MINIMUM_STRONG_AUTH_TIMEOUT_MS;
    public static final long MS_PER_DAY;
    public static final HashMap<String, String> POLICY_IDENTIFIER_TO_PERMISSION;
    public static final List<String> PROFILE_OWNER_OF_ORGANIZATION_OWNED_DEVICE_PERMISSIONS;
    public static final List<String> PROFILE_OWNER_ON_USER_0_PERMISSIONS;
    public static final List<String> PROFILE_OWNER_PERMISSIONS;
    public static final Set<String> SECURE_SETTINGS_ALLOWLIST;
    public static final Set<String> SECURE_SETTINGS_DEVICEOWNER_ALLOWLIST;
    public static final Set<String> SYSTEM_SETTINGS_ALLOWLIST;
    public static final HashMap<String, String> USER_RESTRICTION_PERMISSIONS;
    public final Handler mBackgroundHandler;
    public final RemoteBugreportManager mBugreportCollectionManager;
    public final CertificateMonitor mCertificateMonitor;
    public DevicePolicyConstants mConstants;
    public final DevicePolicyConstantsObserver mConstantsObserver;
    public final Set<String> mContactSystemRoleHolders;
    public final Context mContext;
    public final DeviceAdminServiceController mDeviceAdminServiceController;
    public final DeviceManagementResourcesProvider mDeviceManagementResourcesProvider;
    public final DevicePolicyEngine mDevicePolicyEngine;
    public final DevicePolicyManagementRoleObserver mDevicePolicyManagementRoleObserver;
    public final Object mESIDInitilizationLock;
    public EnterpriseSpecificIdCalculator mEsidCalculator;
    public final Handler mHandler;
    public final boolean mHasFeature;
    public final boolean mHasTelephonyFeature;
    public final IPackageManager mIPackageManager;
    public final IPermissionManager mIPermissionManager;
    public final Injector mInjector;
    public final boolean mIsAutomotive;
    public final boolean mIsWatch;
    public boolean mKeepProfilesRunning;
    public final LocalService mLocalService;
    public final Object mLockDoNoUseDirectly;
    public final LockPatternUtils mLockPatternUtils;
    public final LockSettingsInternal mLockSettingsInternal;
    @GuardedBy({"getLockObject()"})
    public int mLogoutUserId;
    @GuardedBy({"getLockObject()"})
    public NetworkLogger mNetworkLogger;
    public int mNetworkLoggingNotificationUserId;
    public final OverlayPackagesProvider mOverlayPackagesProvider;
    @VisibleForTesting
    final Owners mOwners;
    public final Set<UserPackage> mPackagesToRemove;
    public final PolicyPathProvider mPathProvider;
    @GuardedBy({"getLockObject()"})
    public final ArrayList<Object> mPendingUserCreatedCallbackTokens;
    public final DevicePolicyCacheImpl mPolicyCache;
    public final BroadcastReceiver mReceiver;
    public final RoleManager mRoleManager;
    public DevicePolicySafetyChecker mSafetyChecker;
    public final SecurityLogMonitor mSecurityLogMonitor;
    public final SetupContentObserver mSetupContentObserver;
    public final StatLogger mStatLogger;
    public final DeviceStateCacheImpl mStateCache;
    @GuardedBy({"mSubscriptionsChangedListenerLock"})
    public SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;
    public final Object mSubscriptionsChangedListenerLock;
    public final TelephonyManager mTelephonyManager;
    public final Binder mToken;
    @VisibleForTesting
    final TransferOwnershipMetadataManager mTransferOwnershipMetadataManager;
    public final UsageStatsManagerInternal mUsageStatsManagerInternal;
    @GuardedBy({"getLockObject()"})
    public final SparseArray<DevicePolicyData> mUserData;
    public final UserManager mUserManager;
    public final UserManagerInternal mUserManagerInternal;

    /* renamed from: lambda$getActiveAdminsForAffectedUserInclPermissionBasedAdminLocked$18 */
    public static /* synthetic */ boolean m58xb259fe83(UserInfo userInfo) {
        return false;
    }

    public static /* synthetic */ boolean lambda$getActiveAdminsForAffectedUserLocked$17(UserInfo userInfo) {
        return false;
    }

    public static /* synthetic */ boolean lambda$getAggregatedPasswordComplexityLocked$34(UserInfo userInfo) {
        return false;
    }

    public static /* synthetic */ boolean lambda$getPasswordMinimumMetricsUnchecked$31(UserInfo userInfo) {
        return false;
    }

    public final String getEncryptionStatusName(int i) {
        return i != 0 ? i != 5 ? "unknown" : "per-user" : "unsupported";
    }

    public final boolean hasAccountFeatures(AccountManager accountManager, Account account, String[] strArr) {
        return false;
    }

    public final int makeSuspensionReasons(boolean z, boolean z2) {
        return z2 ? (z ? 1 : 0) | 2 : z ? 1 : 0;
    }

    public final void onCreateAndProvisionManagedProfileCompleted(ManagedProfileProvisioningParams managedProfileProvisioningParams) {
    }

    public final void onCreateAndProvisionManagedProfileStarted(ManagedProfileProvisioningParams managedProfileProvisioningParams) {
    }

    public final void onProvisionFullyManagedDeviceCompleted(FullyManagedDeviceProvisioningParams fullyManagedDeviceProvisioningParams) {
    }

    public final void onProvisionFullyManagedDeviceStarted(FullyManagedDeviceProvisioningParams fullyManagedDeviceProvisioningParams) {
    }

    public final void setEncryptionRequested(boolean z) {
    }

    static {
        long millis = TimeUnit.DAYS.toMillis(1L);
        MS_PER_DAY = millis;
        EXPIRATION_GRACE_PERIOD_MS = 5 * millis;
        MANAGED_PROFILE_MAXIMUM_TIME_OFF_THRESHOLD = 3 * millis;
        MANAGED_PROFILE_OFF_WARNING_PERIOD = millis * 1;
        DELEGATIONS = new String[]{"delegation-cert-install", "delegation-app-restrictions", "delegation-block-uninstall", "delegation-enable-system-app", "delegation-keep-uninstalled-packages", "delegation-package-access", "delegation-permission-grant", "delegation-install-existing-package", "delegation-keep-uninstalled-packages", "delegation-network-logging", "delegation-security-logging", "delegation-cert-selection"};
        DEVICE_OWNER_OR_MANAGED_PROFILE_OWNER_DELEGATIONS = Arrays.asList("delegation-network-logging");
        f1138xcbb66b4a = Arrays.asList("delegation-security-logging");
        EXCLUSIVE_DELEGATIONS = Arrays.asList("delegation-network-logging", "delegation-security-logging", "delegation-cert-selection");
        ArraySet arraySet = new ArraySet();
        SECURE_SETTINGS_ALLOWLIST = arraySet;
        arraySet.add("default_input_method");
        arraySet.add("skip_first_use_hints");
        arraySet.add("install_non_market_apps");
        ArraySet arraySet2 = new ArraySet();
        SECURE_SETTINGS_DEVICEOWNER_ALLOWLIST = arraySet2;
        arraySet2.addAll((Collection) arraySet);
        arraySet2.add("location_mode");
        ArraySet arraySet3 = new ArraySet();
        GLOBAL_SETTINGS_ALLOWLIST = arraySet3;
        arraySet3.add("adb_enabled");
        arraySet3.add("adb_wifi_enabled");
        arraySet3.add("auto_time");
        arraySet3.add("auto_time_zone");
        arraySet3.add("data_roaming");
        arraySet3.add("usb_mass_storage_enabled");
        arraySet3.add("wifi_sleep_policy");
        arraySet3.add("stay_on_while_plugged_in");
        arraySet3.add("wifi_device_owner_configs_lockdown");
        arraySet3.add("private_dns_mode");
        arraySet3.add("private_dns_specifier");
        ArraySet arraySet4 = new ArraySet();
        GLOBAL_SETTINGS_DEPRECATED = arraySet4;
        arraySet4.add("bluetooth_on");
        arraySet4.add("development_settings_enabled");
        arraySet4.add("mode_ringer");
        arraySet4.add("network_preference");
        arraySet4.add("wifi_on");
        ArraySet arraySet5 = new ArraySet();
        SYSTEM_SETTINGS_ALLOWLIST = arraySet5;
        arraySet5.add("screen_brightness");
        arraySet5.add("screen_brightness_float");
        arraySet5.add("screen_brightness_mode");
        arraySet5.add("screen_off_timeout");
        ArraySet arraySet6 = new ArraySet();
        DA_DISALLOWED_POLICIES = arraySet6;
        arraySet6.add(8);
        arraySet6.add(9);
        arraySet6.add(6);
        arraySet6.add(0);
        MINIMUM_STRONG_AUTH_TIMEOUT_MS = TimeUnit.HOURS.toMillis(1L);
        ArrayMap arrayMap = new ArrayMap();
        APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS = arrayMap;
        arrayMap.put(0, "android:system_exempt_from_suspension");
        arrayMap.put(1, "android:system_exempt_from_dismissible_notifications");
        arrayMap.put(2, "android:system_exempt_from_activity_bg_start_restriction");
        arrayMap.put(3, "android:system_exempt_from_hibernation");
        arrayMap.put(4, "android:system_exempt_from_power_restrictions");
        USER_RESTRICTION_PERMISSIONS = new HashMap<>();
        DEFAULT_DEVICE_OWNER_PERMISSIONS = List.of((Object[]) new String[]{"android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL", "android.permission.SET_TIME", "android.permission.SET_TIME_ZONE", "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", "android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", "android.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING", "android.permission.MANAGE_DEVICE_POLICY_MTE", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_CAMERA", "android.permission.MANAGE_DEVICE_POLICY_COMMON_CRITERIA_MODE", "android.permission.MANAGE_DEVICE_POLICY_DEFAULT_SMS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", "android.permission.MANAGE_DEVICE_POLICY_STATUS_BAR", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE", "android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT", "android.permission.MANAGE_DEVICE_POLICY_AUTOFILL", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH", "android.permission.MANAGE_DEVICE_POLICY_CALLS", "android.permission.MANAGE_DEVICE_POLICY_CAMERA", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_FUN", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_LOCALE", "android.permission.MANAGE_DEVICE_POLICY_LOCATION", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_MICROPHONE", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK", "android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION", "android.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA", "android.permission.MANAGE_DEVICE_POLICY_PRINTING", "android.permission.MANAGE_DEVICE_POLICY_RESTRICT_PRIVATE_DNS", "android.permission.MANAGE_DEVICE_POLICY_PROFILES", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT", "android.permission.MANAGE_DEVICE_POLICY_SMS", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS", "android.permission.MANAGE_DEVICE_POLICY_TIME", "android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER", "android.permission.MANAGE_DEVICE_POLICY_USERS", "android.permission.MANAGE_DEVICE_POLICY_VPN", "android.permission.MANAGE_DEVICE_POLICY_WALLPAPER", "android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_WINDOWS", "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS"});
        FINANCED_DEVICE_OWNER_PERMISSIONS = List.of((Object[]) new String[]{"android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL", "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_CALLS", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_USERS", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT", "android.permission.MANAGE_DEVICE_POLICY_TIME"});
        PROFILE_OWNER_OF_ORGANIZATION_OWNED_DEVICE_PERMISSIONS = List.of((Object[]) new String[]{"android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL", "android.permission.SET_TIME", "android.permission.SET_TIME_ZONE", "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", "android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", "android.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING", "android.permission.MANAGE_DEVICE_POLICY_MTE", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT", "android.permission.MANAGE_DEVICE_POLICY_AUTOFILL", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH", "android.permission.MANAGE_DEVICE_POLICY_CALLS", "android.permission.MANAGE_DEVICE_POLICY_CAMERA", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_LOCALE", "android.permission.MANAGE_DEVICE_POLICY_LOCATION", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_MICROPHONE", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK", "android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION", "android.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA", "android.permission.MANAGE_DEVICE_POLICY_PRINTING", "android.permission.MANAGE_DEVICE_POLICY_PROFILES", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_RESTRICT_PRIVATE_DNS", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT", "android.permission.MANAGE_DEVICE_POLICY_SMS", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS", "android.permission.MANAGE_DEVICE_POLICY_TIME", "android.permission.MANAGE_DEVICE_POLICY_VPN", "android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER", "android.permission.MANAGE_DEVICE_POLICY_COMMON_CRITERIA_MODE", "android.permission.MANAGE_DEVICE_POLICY_DEFAULT_SMS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS", "android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", "android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", "android.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING", "android.permission.MANAGE_DEVICE_POLICY_MTE", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES"});
        PROFILE_OWNER_ON_USER_0_PERMISSIONS = List.of((Object[]) new String[]{"android.permission.SET_TIME", "android.permission.SET_TIME_ZONE", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_FUN", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK", "android.permission.MANAGE_DEVICE_POLICY_USERS", "android.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT", "android.permission.MANAGE_DEVICE_POLICY_SMS", "android.permission.MANAGE_DEVICE_POLICY_TIME", "android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER", "android.permission.MANAGE_DEVICE_POLICY_WINDOWS", "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_CAMERA", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", "android.permission.MANAGE_DEVICE_POLICY_STATUS_BAR", "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS"});
        PROFILE_OWNER_PERMISSIONS = List.of((Object[]) new String[]{"android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL", "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT", "android.permission.MANAGE_DEVICE_POLICY_AUTOFILL", "android.permission.MANAGE_DEVICE_POLICY_CALLS", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_LOCALE", "android.permission.MANAGE_DEVICE_POLICY_LOCATION", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION", "android.permission.MANAGE_DEVICE_POLICY_PRINTING", "android.permission.MANAGE_DEVICE_POLICY_PROFILES", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS", "android.permission.MANAGE_DEVICE_POLICY_TIME", "android.permission.MANAGE_DEVICE_POLICY_VPN", "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS"});
        DPC_PERMISSIONS = new HashMap<>();
        ACTIVE_ADMIN_POLICIES = new HashMap<>();
        DELEGATE_SCOPES = new HashMap<>();
        CROSS_USER_PERMISSIONS = new HashMap<>();
        POLICY_IDENTIFIER_TO_PERMISSION = new HashMap<>();
    }

    public final Object getLockObject() {
        long time = this.mStatLogger.getTime();
        LockGuard.guard(8);
        this.mStatLogger.logDurationStat(0, time);
        return this.mLockDoNoUseDirectly;
    }

    public final void ensureLocked() {
        if (Thread.holdsLock(this.mLockDoNoUseDirectly)) {
            return;
        }
        Slogf.wtfStack("DevicePolicyManager", "Not holding DPMS lock.");
    }

    public final void wtfIfInLock() {
        if (Thread.holdsLock(this.mLockDoNoUseDirectly)) {
            Slogf.wtfStack("DevicePolicyManager", "Shouldn't be called with DPMS lock held");
        }
    }

    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public DevicePolicyManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            String string = context.getResources().getString(17039916);
            if (TextUtils.isEmpty(string)) {
                this.mService = new DevicePolicyManagerService(context);
                return;
            }
            try {
                this.mService = (DevicePolicyManagerService) Class.forName(string).getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate DevicePolicyManagerService with class name: " + string, e);
            }
        }

        public void setDevicePolicySafetyChecker(DevicePolicySafetyChecker devicePolicySafetyChecker) {
            this.mService.setDevicePolicySafetyChecker(devicePolicySafetyChecker);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("device_policy", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            this.mService.systemReady(i);
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            if (targetUser.isPreCreated()) {
                return;
            }
            this.mService.handleStartUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            if (targetUser.isPreCreated()) {
                return;
            }
            this.mService.handleUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            if (targetUser.isPreCreated()) {
                return;
            }
            this.mService.handleStopUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocked(SystemService.TargetUser targetUser) {
            if (targetUser.isPreCreated()) {
                return;
            }
            this.mService.handleOnUserUnlocked(targetUser.getUserIdentifier());
        }
    }

    /* loaded from: classes.dex */
    public static class RestrictionsListener implements UserManagerInternal.UserRestrictionsListener {
        public final Context mContext;
        public final DevicePolicyManagerService mDpms;
        public final UserManagerInternal mUserManagerInternal;

        public RestrictionsListener(Context context, UserManagerInternal userManagerInternal, DevicePolicyManagerService devicePolicyManagerService) {
            this.mContext = context;
            this.mUserManagerInternal = userManagerInternal;
            this.mDpms = devicePolicyManagerService;
        }

        @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
        public void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2) {
            resetCrossProfileIntentFiltersIfNeeded(i, bundle, bundle2);
            resetUserVpnIfNeeded(i, bundle, bundle2);
        }

        public final void resetUserVpnIfNeeded(int i, Bundle bundle, Bundle bundle2) {
            if (!bundle2.getBoolean("no_config_vpn") && bundle.getBoolean("no_config_vpn")) {
                this.mDpms.clearUserConfiguredVpns(i);
            }
        }

        public final void resetCrossProfileIntentFiltersIfNeeded(int i, Bundle bundle, Bundle bundle2) {
            int profileParentId;
            if (!UserRestrictionsUtils.restrictionsChanged(bundle2, bundle, "no_sharing_into_profile") || (profileParentId = this.mUserManagerInternal.getProfileParentId(i)) == i) {
                return;
            }
            Slogf.m22i("DevicePolicyManager", "Resetting cross-profile intent filters on restriction change");
            this.mDpms.resetDefaultCrossProfileIntentFilters(profileParentId);
            this.mContext.sendBroadcastAsUser(new Intent("android.app.action.DATA_SHARING_RESTRICTION_APPLIED"), UserHandle.of(i));
        }
    }

    public final void clearUserConfiguredVpns(int i) {
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            if (deviceOrProfileOwnerAdminLocked == null) {
                Slogf.wtf("DevicePolicyManager", "Admin not found");
                return;
            }
            String str = deviceOrProfileOwnerAdminLocked.mAlwaysOnVpnPackage;
            if (str == null) {
                this.mInjector.getVpnManager().setAlwaysOnVpnPackageForUser(i, null, false, null);
            }
            List<AppOpsManager.PackageOps> packagesForOps = this.mInjector.getAppOpsManager().getPackagesForOps(new int[]{47});
            if (packagesForOps == null) {
                return;
            }
            for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                if (UserHandle.getUserId(packageOps.getUid()) == i && !packageOps.getPackageName().equals(str)) {
                    if (packageOps.getOps().size() != 1) {
                        Slogf.wtf("DevicePolicyManager", "Unexpected number of ops returned");
                    } else if (((AppOpsManager.OpEntry) packageOps.getOps().get(0)).getMode() == 0) {
                        Slogf.m22i("DevicePolicyManager", String.format("Revoking VPN authorization for package %s uid %d", packageOps.getPackageName(), Integer.valueOf(packageOps.getUid())));
                        this.mInjector.getAppOpsManager().setMode(47, packageOps.getUid(), packageOps.getPackageName(), 3);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class UserLifecycleListener implements UserManagerInternal.UserLifecycleListener {
        public UserLifecycleListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUserCreated$0(UserInfo userInfo, Object obj) {
            DevicePolicyManagerService.this.handleNewUserCreated(userInfo, obj);
        }

        @Override // com.android.server.p011pm.UserManagerInternal.UserLifecycleListener
        public void onUserCreated(final UserInfo userInfo, final Object obj) {
            DevicePolicyManagerService.this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$UserLifecycleListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DevicePolicyManagerService.UserLifecycleListener.this.lambda$onUserCreated$0(userInfo, obj);
                }
            });
        }
    }

    public final void handlePackagesChanged(String str, int i) {
        boolean z;
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        synchronized (getLockObject()) {
            boolean z2 = false;
            z = false;
            for (int size = lambda$getUserDataUnchecked$2.mAdminList.size() - 1; size >= 0; size--) {
                ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(size);
                try {
                    String packageName = activeAdmin.info.getPackageName();
                    if ((str == null || str.equals(packageName)) && (this.mIPackageManager.getPackageInfo(packageName, 0L, i) == null || this.mIPackageManager.getReceiverInfo(activeAdmin.info.getComponent(), 786432L, i) == null)) {
                        Slogf.m26e("DevicePolicyManager", String.format("Admin package %s not found for user %d, removing active admin", str, Integer.valueOf(i)));
                        try {
                            lambda$getUserDataUnchecked$2.mAdminList.remove(size);
                            lambda$getUserDataUnchecked$2.mAdminMap.remove(activeAdmin.info.getComponent());
                            pushActiveAdminPackagesLocked(i);
                            pushMeteredDisabledPackages(i);
                            z = true;
                        } catch (RemoteException e) {
                            e = e;
                            z = true;
                            Slogf.wtf("DevicePolicyManager", "Error handling package changes", e);
                        }
                    }
                } catch (RemoteException e2) {
                    e = e2;
                }
            }
            if (z) {
                lambda$getUserDataUnchecked$2.validatePasswordOwner();
            }
            for (int size2 = lambda$getUserDataUnchecked$2.mDelegationMap.size() - 1; size2 >= 0; size2--) {
                if (isRemovedPackage(str, lambda$getUserDataUnchecked$2.mDelegationMap.keyAt(size2), i)) {
                    lambda$getUserDataUnchecked$2.mDelegationMap.removeAt(size2);
                    z2 = true;
                }
            }
            ComponentName ownerComponent = getOwnerComponent(i);
            if (str != null && ownerComponent != null && ownerComponent.getPackageName().equals(str)) {
                startOwnerService(i, "package-broadcast");
            }
            if (shouldMigrateToDevicePolicyEngine()) {
                migratePoliciesToDevicePolicyEngine();
            }
            if (isDevicePolicyEngineEnabled()) {
                this.mDevicePolicyEngine.handlePackageChanged(str, i);
            }
            if (z || z2) {
                saveSettingsLocked(lambda$getUserDataUnchecked$2.mUserId);
            }
        }
        if (z) {
            pushUserRestrictions(i);
        }
    }

    public final void removeCredentialManagementApp(final String str) {
        this.mBackgroundHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda189
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$removeCredentialManagementApp$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeCredentialManagementApp$0(String str) {
        try {
            KeyChain.KeyChainConnection keyChainBind = this.mInjector.keyChainBind();
            IKeyChainService service = keyChainBind.getService();
            if (service.hasCredentialManagementApp() && str.equals(service.getCredentialManagementAppPackageName())) {
                service.removeCredentialManagementApp();
            }
            keyChainBind.close();
        } catch (RemoteException | AssertionError | IllegalStateException | InterruptedException e) {
            Slogf.m25e("DevicePolicyManager", "Unable to remove the credential management app", e);
        }
    }

    public final boolean isRemovedPackage(String str, String str2, int i) {
        if (str2 != null) {
            if (str != null) {
                try {
                    if (!str.equals(str2)) {
                        return false;
                    }
                } catch (RemoteException e) {
                    Slogf.wtf("DevicePolicyManager", "Error checking isRemovedPackage", e);
                    return false;
                }
            }
            return this.mIPackageManager.getPackageInfo(str2, 0L, i) == null;
        }
        return false;
    }

    public final void handleNewPackageInstalled(String str, int i) {
        if (lambda$getUserDataUnchecked$2(i).mAppsSuspended) {
            String[] strArr = {str};
            if (this.mInjector.getPackageManager(i).getUnsuspendablePackages(strArr).length != 0) {
                Slogf.m22i("DevicePolicyManager", "Newly installed package is unsuspendable: " + str);
                return;
            }
            this.mInjector.getPackageManagerInternal().setPackagesSuspendedByAdmin(i, strArr, true);
        }
    }

    public void setDevicePolicySafetyChecker(DevicePolicySafetyChecker devicePolicySafetyChecker) {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(this.mIsAutomotive || isAdb(callerIdentity), "can only set DevicePolicySafetyChecker on automotive builds or from ADB (but caller is %s)", new Object[]{callerIdentity});
        setDevicePolicySafetyCheckerUnchecked(devicePolicySafetyChecker);
    }

    public void setDevicePolicySafetyCheckerUnchecked(DevicePolicySafetyChecker devicePolicySafetyChecker) {
        Slogf.m20i("DevicePolicyManager", "Setting DevicePolicySafetyChecker as %s", devicePolicySafetyChecker);
        this.mSafetyChecker = devicePolicySafetyChecker;
        this.mInjector.setDevicePolicySafetyChecker(devicePolicySafetyChecker);
    }

    public DevicePolicySafetyChecker getDevicePolicySafetyChecker() {
        return this.mSafetyChecker;
    }

    public final void checkCanExecuteOrThrowUnsafe(int i) {
        int unsafeOperationReason = getUnsafeOperationReason(i);
        if (unsafeOperationReason == -1) {
            return;
        }
        DevicePolicySafetyChecker devicePolicySafetyChecker = this.mSafetyChecker;
        if (devicePolicySafetyChecker == null) {
            throw new UnsafeStateException(i, unsafeOperationReason);
        }
        throw devicePolicySafetyChecker.newUnsafeStateException(i, unsafeOperationReason);
    }

    public int getUnsafeOperationReason(int i) {
        DevicePolicySafetyChecker devicePolicySafetyChecker = this.mSafetyChecker;
        if (devicePolicySafetyChecker == null) {
            return -1;
        }
        return devicePolicySafetyChecker.getUnsafeOperationReason(i);
    }

    public void setNextOperationSafety(int i, int i2) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS"));
        Slogf.m20i("DevicePolicyManager", "setNextOperationSafety(%s, %s)", DevicePolicyManager.operationToString(i), DevicePolicyManager.operationSafetyReasonToString(i2));
        this.mSafetyChecker = new OneTimeSafetyChecker(this, i, i2);
    }

    public boolean isSafeOperation(int i) {
        DevicePolicySafetyChecker devicePolicySafetyChecker = this.mSafetyChecker;
        if (devicePolicySafetyChecker == null) {
            return true;
        }
        return devicePolicySafetyChecker.isSafeOperation(i);
    }

    public List<OwnerShellData> listAllOwners() {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS"));
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda101
            public final Object getOrThrow() {
                List lambda$listAllOwners$1;
                lambda$listAllOwners$1 = DevicePolicyManagerService.this.lambda$listAllOwners$1();
                return lambda$listAllOwners$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$listAllOwners$1() throws Exception {
        SparseArray<DevicePolicyData> sparseArray;
        List<OwnerShellData> listAllOwners = this.mOwners.listAllOwners();
        synchronized (getLockObject()) {
            for (int i = 0; i < listAllOwners.size(); i++) {
                OwnerShellData ownerShellData = listAllOwners.get(i);
                ownerShellData.isAffiliated = isUserAffiliatedWithDeviceLocked(ownerShellData.userId);
            }
            sparseArray = this.mUserData;
        }
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            DevicePolicyData valueAt = this.mUserData.valueAt(i2);
            int keyAt = sparseArray.keyAt(i2);
            int profileParentId = this.mUserManagerInternal.getProfileParentId(keyAt);
            if (profileParentId != keyAt) {
                for (int i3 = 0; i3 < valueAt.mAdminList.size(); i3++) {
                    listAllOwners.add(OwnerShellData.forManagedProfileOwner(keyAt, profileParentId, valueAt.mAdminList.get(i3).info.getComponent()));
                }
            }
        }
        return listAllOwners;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public final Context mContext;
        public DevicePolicySafetyChecker mSafetyChecker;

        public Injector(Context context) {
            this.mContext = context;
        }

        public boolean hasFeature() {
            return getPackageManager().hasSystemFeature("android.software.device_admin");
        }

        public Context createContextAsUser(UserHandle userHandle) throws PackageManager.NameNotFoundException {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, userHandle);
        }

        public Resources getResources() {
            return this.mContext.getResources();
        }

        public UserManager getUserManager() {
            return UserManager.get(this.mContext);
        }

        public UserManagerInternal getUserManagerInternal() {
            return (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }

        public PackageManagerInternal getPackageManagerInternal() {
            return (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }

        public ActivityTaskManagerInternal getActivityTaskManagerInternal() {
            return (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        }

        public PermissionControllerManager getPermissionControllerManager(UserHandle userHandle) {
            if (userHandle.equals(this.mContext.getUser())) {
                return (PermissionControllerManager) this.mContext.getSystemService(PermissionControllerManager.class);
            }
            try {
                Context context = this.mContext;
                return (PermissionControllerManager) context.createPackageContextAsUser(context.getPackageName(), 0, userHandle).getSystemService(PermissionControllerManager.class);
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        public UsageStatsManagerInternal getUsageStatsManagerInternal() {
            return (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        }

        public NetworkPolicyManagerInternal getNetworkPolicyManagerInternal() {
            return (NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class);
        }

        public NotificationManager getNotificationManager() {
            return (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        }

        public IIpConnectivityMetrics getIIpConnectivityMetrics() {
            return IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
        }

        public PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        public PackageManager getPackageManager(int i) {
            try {
                return createContextAsUser(UserHandle.of(i)).getPackageManager();
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        public PowerManagerInternal getPowerManagerInternal() {
            return (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        }

        public TelephonyManager getTelephonyManager() {
            return (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        }

        public RoleManager getRoleManager() {
            return (RoleManager) this.mContext.getSystemService(RoleManager.class);
        }

        public TrustManager getTrustManager() {
            return (TrustManager) this.mContext.getSystemService("trust");
        }

        public AlarmManager getAlarmManager() {
            return (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        }

        public AlarmManagerInternal getAlarmManagerInternal() {
            return (AlarmManagerInternal) LocalServices.getService(AlarmManagerInternal.class);
        }

        public ConnectivityManager getConnectivityManager() {
            return (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        }

        public VpnManager getVpnManager() {
            return (VpnManager) this.mContext.getSystemService(VpnManager.class);
        }

        public LocationManager getLocationManager() {
            return (LocationManager) this.mContext.getSystemService(LocationManager.class);
        }

        public IWindowManager getIWindowManager() {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        }

        public IActivityManager getIActivityManager() {
            return ActivityManager.getService();
        }

        public IActivityTaskManager getIActivityTaskManager() {
            return ActivityTaskManager.getService();
        }

        public ActivityManagerInternal getActivityManagerInternal() {
            return (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }

        public IPackageManager getIPackageManager() {
            return AppGlobals.getPackageManager();
        }

        public IPermissionManager getIPermissionManager() {
            return AppGlobals.getPermissionManager();
        }

        public IBackupManager getIBackupManager() {
            return IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
        }

        public PersistentDataBlockManagerInternal getPersistentDataBlockManagerInternal() {
            return (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        }

        public AppOpsManager getAppOpsManager() {
            return (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        }

        public LockSettingsInternal getLockSettingsInternal() {
            return (LockSettingsInternal) LocalServices.getService(LockSettingsInternal.class);
        }

        public CrossProfileApps getCrossProfileApps(int i) {
            return (CrossProfileApps) this.mContext.createContextAsUser(UserHandle.of(i), 0).getSystemService(CrossProfileApps.class);
        }

        public boolean hasUserSetupCompleted(DevicePolicyData devicePolicyData) {
            return devicePolicyData.mUserSetupComplete;
        }

        public boolean isBuildDebuggable() {
            return Build.IS_DEBUGGABLE;
        }

        public LockPatternUtils newLockPatternUtils() {
            return new LockPatternUtils(this.mContext);
        }

        public EnterpriseSpecificIdCalculator newEnterpriseSpecificIdCalculator() {
            return new EnterpriseSpecificIdCalculator(this.mContext);
        }

        public boolean storageManagerIsFileBasedEncryptionEnabled() {
            return StorageManager.isFileEncrypted();
        }

        public Looper getMyLooper() {
            return Looper.myLooper();
        }

        public WifiManager getWifiManager() {
            return (WifiManager) this.mContext.getSystemService(WifiManager.class);
        }

        public UsbManager getUsbManager() {
            return (UsbManager) this.mContext.getSystemService(UsbManager.class);
        }

        public long binderClearCallingIdentity() {
            return Binder.clearCallingIdentity();
        }

        public void binderRestoreCallingIdentity(long j) {
            Binder.restoreCallingIdentity(j);
        }

        public int binderGetCallingUid() {
            return Binder.getCallingUid();
        }

        public int binderGetCallingPid() {
            return Binder.getCallingPid();
        }

        public boolean binderIsCallingUidMyUid() {
            return Binder.getCallingUid() == Process.myUid();
        }

        public void binderWithCleanCallingIdentity(FunctionalUtils.ThrowingRunnable throwingRunnable) {
            Binder.withCleanCallingIdentity(throwingRunnable);
        }

        public final <T> T binderWithCleanCallingIdentity(FunctionalUtils.ThrowingSupplier<T> throwingSupplier) {
            return (T) Binder.withCleanCallingIdentity(throwingSupplier);
        }

        public final int userHandleGetCallingUserId() {
            return UserHandle.getUserId(binderGetCallingUid());
        }

        public void powerManagerGoToSleep(long j, int i, int i2) {
            ((PowerManager) this.mContext.getSystemService(PowerManager.class)).goToSleep(j, i, i2);
        }

        public void powerManagerReboot(String str) {
            ((PowerManager) this.mContext.getSystemService(PowerManager.class)).reboot(str);
        }

        public boolean recoverySystemRebootWipeUserData(boolean z, String str, boolean z2, boolean z3, boolean z4, boolean z5) throws IOException {
            return FactoryResetter.newBuilder(this.mContext).setSafetyChecker(this.mSafetyChecker).setReason(str).setShutdown(z).setForce(z2).setWipeEuicc(z3).setWipeAdoptableStorage(z4).setWipeFactoryResetProtection(z5).build().factoryReset();
        }

        public boolean systemPropertiesGetBoolean(String str, boolean z) {
            return SystemProperties.getBoolean(str, z);
        }

        public long systemPropertiesGetLong(String str, long j) {
            return SystemProperties.getLong(str, j);
        }

        public String systemPropertiesGet(String str, String str2) {
            return SystemProperties.get(str, str2);
        }

        public String systemPropertiesGet(String str) {
            return SystemProperties.get(str);
        }

        public void systemPropertiesSet(String str, String str2) {
            SystemProperties.set(str, str2);
        }

        public boolean userManagerIsHeadlessSystemUserMode() {
            return UserManager.isHeadlessSystemUserMode();
        }

        public PendingIntent pendingIntentGetActivityAsUser(Context context, int i, Intent intent, int i2, Bundle bundle, UserHandle userHandle) {
            return PendingIntent.getActivityAsUser(context, i, intent, i2, bundle, userHandle);
        }

        public PendingIntent pendingIntentGetBroadcast(Context context, int i, Intent intent, int i2) {
            return PendingIntent.getBroadcast(context, i, intent, i2);
        }

        public void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver, int i) {
            this.mContext.getContentResolver().registerContentObserver(uri, z, contentObserver, i);
        }

        public int settingsSecureGetIntForUser(String str, int i, int i2) {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), str, i, i2);
        }

        public String settingsSecureGetStringForUser(String str, int i) {
            return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), str, i);
        }

        public void settingsSecurePutIntForUser(String str, int i, int i2) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), str, i, i2);
        }

        public void settingsSecurePutStringForUser(String str, String str2, int i) {
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), str, str2, i);
        }

        public void settingsGlobalPutStringForUser(String str, String str2, int i) {
            Settings.Global.putStringForUser(this.mContext.getContentResolver(), str, str2, i);
        }

        public int settingsGlobalGetInt(String str, int i) {
            return Settings.Global.getInt(this.mContext.getContentResolver(), str, i);
        }

        public String settingsGlobalGetString(String str) {
            return Settings.Global.getString(this.mContext.getContentResolver(), str);
        }

        public void settingsGlobalPutInt(String str, int i) {
            Settings.Global.putInt(this.mContext.getContentResolver(), str, i);
        }

        public void settingsGlobalPutString(String str, String str2) {
            Settings.Global.putString(this.mContext.getContentResolver(), str, str2);
        }

        public void settingsSystemPutStringForUser(String str, String str2, int i) {
            Settings.System.putStringForUser(this.mContext.getContentResolver(), str, str2, i);
        }

        public void securityLogSetLoggingEnabledProperty(boolean z) {
            SecurityLog.setLoggingEnabledProperty(z);
        }

        public boolean securityLogGetLoggingEnabledProperty() {
            return SecurityLog.getLoggingEnabledProperty();
        }

        public boolean securityLogIsLoggingEnabled() {
            return SecurityLog.isLoggingEnabled();
        }

        public KeyChain.KeyChainConnection keyChainBind() throws InterruptedException {
            return KeyChain.bind(this.mContext);
        }

        public KeyChain.KeyChainConnection keyChainBindAsUser(UserHandle userHandle) throws InterruptedException {
            return KeyChain.bindAsUser(this.mContext, userHandle);
        }

        public void postOnSystemServerInitThreadPool(Runnable runnable) {
            SystemServerInitThreadPool.submit(runnable, "DevicePolicyManager");
        }

        public TransferOwnershipMetadataManager newTransferOwnershipMetadataManager() {
            return new TransferOwnershipMetadataManager();
        }

        public void runCryptoSelfTest() {
            CryptoTestHelper.runAndLogSelfTest();
        }

        public String[] getPersonalAppsForSuspension(int i) {
            return PersonalAppsSuspensionHelper.forUser(this.mContext, i).getPersonalAppsForSuspension();
        }

        public long systemCurrentTimeMillis() {
            return System.currentTimeMillis();
        }

        public boolean isChangeEnabled(long j, String str, int i) {
            return CompatChanges.isChangeEnabled(j, str, UserHandle.of(i));
        }

        public void setDevicePolicySafetyChecker(DevicePolicySafetyChecker devicePolicySafetyChecker) {
            this.mSafetyChecker = devicePolicySafetyChecker;
        }

        public DeviceManagementResourcesProvider getDeviceManagementResourcesProvider() {
            return new DeviceManagementResourcesProvider();
        }
    }

    public DevicePolicyManagerService(Context context) {
        this(new Injector(context.createAttributionContext("DevicePolicyManagerService")), new PolicyPathProvider() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.2
        });
    }

    @VisibleForTesting
    public DevicePolicyManagerService(Injector injector, PolicyPathProvider policyPathProvider) {
        this.mKeepProfilesRunning = isKeepProfilesRunningFlagEnabled();
        this.mPolicyCache = new DevicePolicyCacheImpl();
        this.mStateCache = new DeviceStateCacheImpl();
        this.mESIDInitilizationLock = new Object();
        this.mSubscriptionsChangedListenerLock = new Object();
        this.mPackagesToRemove = new ArraySet();
        this.mToken = new Binder();
        this.mLogoutUserId = -10000;
        this.mNetworkLoggingNotificationUserId = -10000;
        this.mStatLogger = new StatLogger(new String[]{"LockGuard.guard()"});
        this.mLockDoNoUseDirectly = LockGuard.installNewLock(8, true);
        this.mPendingUserCreatedCallbackTokens = new ArrayList<>();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final int intExtra = intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId());
                if ("android.intent.action.USER_STARTED".equals(action) && intExtra == 0) {
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        if (DevicePolicyManagerService.this.isNetworkLoggingEnabledInternalLocked()) {
                            DevicePolicyManagerService.this.setNetworkLoggingActiveInternal(true);
                        }
                    }
                }
                if ("android.intent.action.BOOT_COMPLETED".equals(action) && intExtra == DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserId()) {
                    DevicePolicyManagerService.this.mBugreportCollectionManager.checkForPendingBugreportAfterBoot();
                }
                if ("android.intent.action.BOOT_COMPLETED".equals(action) || "com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION".equals(action)) {
                    DevicePolicyManagerService.this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DevicePolicyManagerService.this.handlePasswordExpirationNotification(intExtra);
                        }
                    });
                }
                if ("android.intent.action.USER_ADDED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_ADDED", intExtra);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybePauseDeviceWideLoggingLocked();
                    }
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_REMOVED", intExtra);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        boolean isUserAffiliatedWithDeviceLocked = DevicePolicyManagerService.this.isUserAffiliatedWithDeviceLocked(intExtra);
                        DevicePolicyManagerService.this.removeUserData(intExtra);
                        if (!isUserAffiliatedWithDeviceLocked) {
                            DevicePolicyManagerService.this.discardDeviceWideLogsLocked();
                            DevicePolicyManagerService.this.maybeResumeDeviceWideLoggingLocked();
                        }
                    }
                } else if ("android.intent.action.USER_STARTED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_STARTED", intExtra);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybeSendAdminEnabledBroadcastLocked(intExtra);
                        DevicePolicyManagerService.this.mUserData.remove(intExtra);
                    }
                    DevicePolicyManagerService.this.handlePackagesChanged(null, intExtra);
                    DevicePolicyManagerService.this.updatePersonalAppsSuspensionOnUserStart(intExtra);
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_STOPPED", intExtra);
                    if (DevicePolicyManagerService.this.isManagedProfile(intExtra)) {
                        Slogf.m30d("DevicePolicyManager", "Managed profile was stopped");
                        DevicePolicyManagerService.this.updatePersonalAppsSuspension(intExtra, false);
                    }
                } else if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_SWITCHED", intExtra);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybeSendAdminEnabledBroadcastLocked(intExtra);
                    }
                    if (DevicePolicyManagerService.this.isManagedProfile(intExtra)) {
                        Slogf.m30d("DevicePolicyManager", "Managed profile became unlocked");
                        DevicePolicyManagerService.this.triggerPolicyComplianceCheckIfNeeded(intExtra, DevicePolicyManagerService.this.updatePersonalAppsSuspension(intExtra, true));
                    }
                } else if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                    DevicePolicyManagerService.this.handlePackagesChanged(null, intExtra);
                } else if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                    DevicePolicyManagerService.this.handlePackagesChanged(intent.getData().getSchemeSpecificPart(), intExtra);
                } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                        DevicePolicyManagerService.this.handlePackagesChanged(intent.getData().getSchemeSpecificPart(), intExtra);
                    } else {
                        DevicePolicyManagerService.this.handleNewPackageInstalled(intent.getData().getSchemeSpecificPart(), intExtra);
                    }
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action) && !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    DevicePolicyManagerService.this.handlePackagesChanged(intent.getData().getSchemeSpecificPart(), intExtra);
                    DevicePolicyManagerService.this.removeCredentialManagementApp(intent.getData().getSchemeSpecificPart());
                } else if ("android.intent.action.MANAGED_PROFILE_ADDED".equals(action)) {
                    DevicePolicyManagerService.this.clearWipeProfileNotification();
                } else if ("android.intent.action.DATE_CHANGED".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                    DevicePolicyManagerService.this.updateSystemUpdateFreezePeriodsRecord(true);
                    DevicePolicyManagerService devicePolicyManagerService = DevicePolicyManagerService.this;
                    int managedUserId = devicePolicyManagerService.getManagedUserId(devicePolicyManagerService.getMainUserId());
                    if (managedUserId >= 0) {
                        DevicePolicyManagerService devicePolicyManagerService2 = DevicePolicyManagerService.this;
                        devicePolicyManagerService2.updatePersonalAppsSuspension(managedUserId, devicePolicyManagerService2.mUserManager.isUserUnlocked(managedUserId));
                    }
                } else if (DevicePolicyManagerService.ACTION_PROFILE_OFF_DEADLINE.equals(action)) {
                    Slogf.m22i("DevicePolicyManager", "Profile off deadline alarm was triggered");
                    DevicePolicyManagerService devicePolicyManagerService3 = DevicePolicyManagerService.this;
                    int managedUserId2 = devicePolicyManagerService3.getManagedUserId(devicePolicyManagerService3.getMainUserId());
                    if (managedUserId2 >= 0) {
                        DevicePolicyManagerService devicePolicyManagerService4 = DevicePolicyManagerService.this;
                        devicePolicyManagerService4.updatePersonalAppsSuspension(managedUserId2, devicePolicyManagerService4.mUserManager.isUserUnlocked(managedUserId2));
                        return;
                    }
                    Slogf.wtf("DevicePolicyManager", "Got deadline alarm for nonexistent profile");
                } else if (DevicePolicyManagerService.ACTION_TURN_PROFILE_ON_NOTIFICATION.equals(action)) {
                    Slogf.m22i("DevicePolicyManager", "requesting to turn on the profile: " + intExtra);
                    DevicePolicyManagerService.this.mUserManager.requestQuietModeEnabled(false, UserHandle.of(intExtra));
                } else if ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                    DevicePolicyManagerService.this.notifyIfManagedSubscriptionsAreUnavailable(UserHandle.of(intExtra), false);
                } else if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action)) {
                    DevicePolicyManagerService.this.notifyIfManagedSubscriptionsAreUnavailable(UserHandle.of(intExtra), true);
                }
            }

            public final void sendDeviceOwnerUserCommand(String str, int i) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    ActiveAdmin deviceOwnerAdminLocked = DevicePolicyManagerService.this.getDeviceOwnerAdminLocked();
                    if (deviceOwnerAdminLocked != null) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("android.intent.extra.USER", UserHandle.of(i));
                        DevicePolicyManagerService.this.sendAdminCommandLocked(deviceOwnerAdminLocked, str, bundle, null, true);
                    }
                }
            }
        };
        this.mReceiver = broadcastReceiver;
        HashMap<String, String> hashMap = USER_RESTRICTION_PERMISSIONS;
        hashMap.put("ensure_verify_apps", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES");
        hashMap.put("no_wifi_tethering", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("no_wifi_direct", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("no_user_switch", "android.permission.MANAGE_DEVICE_POLICY_USERS");
        hashMap.put("no_usb_file_transfer", "android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER");
        hashMap.put("no_unmute_microphone", "android.permission.MANAGE_DEVICE_POLICY_MICROPHONE");
        hashMap.put("disallow_unmute_device", "android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT");
        hashMap.put("no_uninstall_apps", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL");
        hashMap.put("no_unified_password", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS");
        hashMap.put("no_system_error_dialogs", "android.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS");
        hashMap.put("no_sms", "android.permission.MANAGE_DEVICE_POLICY_SMS");
        hashMap.put("no_sharing_admin_configured_wifi", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("no_share_location", "android.permission.MANAGE_DEVICE_POLICY_LOCATION");
        hashMap.put("no_sharing_into_profile", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION");
        hashMap.put("no_set_wallpaper", "android.permission.MANAGE_DEVICE_POLICY_WALLPAPER");
        hashMap.put("no_set_user_icon", "android.permission.MANAGE_DEVICE_POLICY_USERS");
        hashMap.put("no_safe_boot", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT");
        hashMap.put("no_run_in_background", "android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT");
        hashMap.put("no_remove_user", "android.permission.MANAGE_DEVICE_POLICY_USERS");
        hashMap.put("no_printing", "android.permission.MANAGE_DEVICE_POLICY_PRINTING");
        hashMap.put("no_outgoing_calls", "android.permission.MANAGE_DEVICE_POLICY_CALLS");
        hashMap.put("no_outgoing_beam", "android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION");
        hashMap.put("no_network_reset", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_physical_media", "android.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA");
        hashMap.put("no_modify_accounts", "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT");
        hashMap.put("disallow_microphone_toggle", "android.permission.MANAGE_DEVICE_POLICY_MICROPHONE");
        hashMap.put("no_install_unknown_sources_globally", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES");
        hashMap.put("no_install_unknown_sources", "android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES");
        hashMap.put("no_install_apps", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL");
        hashMap.put("no_fun", "android.permission.MANAGE_DEVICE_POLICY_FUN");
        hashMap.put("no_factory_reset", "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET");
        hashMap.put("no_debugging_features", "android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES");
        hashMap.put("no_data_roaming", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_cross_profile_copy_paste", "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION");
        hashMap.put("no_create_windows", "android.permission.MANAGE_DEVICE_POLICY_WINDOWS");
        hashMap.put("no_content_suggestions", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT");
        hashMap.put("no_content_capture", "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT");
        hashMap.put("no_config_wifi", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("no_config_vpn", "android.permission.MANAGE_DEVICE_POLICY_VPN");
        hashMap.put("no_config_tethering", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_config_screen_timeout", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY");
        hashMap.put("disallow_config_private_dns", "android.permission.MANAGE_DEVICE_POLICY_RESTRICT_PRIVATE_DNS");
        hashMap.put("no_config_mobile_networks", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_config_location", "android.permission.MANAGE_DEVICE_POLICY_LOCATION");
        hashMap.put("no_config_locale", "android.permission.MANAGE_DEVICE_POLICY_LOCALE");
        hashMap.put("no_config_date_time", "android.permission.MANAGE_DEVICE_POLICY_TIME");
        hashMap.put("no_config_credentials", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS");
        hashMap.put("no_config_cell_broadcasts", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_config_brightness", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY");
        hashMap.put("no_config_bluetooth", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH");
        hashMap.put("no_change_wifi_state", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("disallow_camera_toggle", "android.permission.MANAGE_DEVICE_POLICY_CAMERA");
        hashMap.put("no_camera", "android.permission.MANAGE_DEVICE_POLICY_CAMERA");
        hashMap.put("no_bluetooth_sharing", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH");
        hashMap.put("no_bluetooth", "android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH");
        hashMap.put("disallow_biometric", "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS");
        hashMap.put("no_autofill", "android.permission.MANAGE_DEVICE_POLICY_AUTOFILL");
        hashMap.put("no_control_apps", "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL");
        hashMap.put("no_ambient_display", "android.permission.MANAGE_DEVICE_POLICY_DISPLAY");
        hashMap.put("no_airplane_mode", "android.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE");
        hashMap.put("no_adjust_volume", "android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT");
        hashMap.put("no_add_wifi_config", "android.permission.MANAGE_DEVICE_POLICY_WIFI");
        hashMap.put("no_add_user", "android.permission.MANAGE_DEVICE_POLICY_USERS");
        hashMap.put("no_add_clone_profile", "android.permission.MANAGE_DEVICE_POLICY_PROFILES");
        hashMap.put("allow_parent_profile_app_linking", "android.permission.MANAGE_DEVICE_POLICY_PROFILES");
        hashMap.put("no_cellular_2g", "android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK");
        hashMap.put("no_ultra_wideband_radio", "android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION");
        hashMap.put("no_record_audio", null);
        hashMap.put("no_wallpaper", null);
        hashMap.put("disallow_config_default_apps", null);
        HashMap<Integer, List<String>> hashMap2 = DPC_PERMISSIONS;
        hashMap2.put(0, DEFAULT_DEVICE_OWNER_PERMISSIONS);
        hashMap2.put(1, FINANCED_DEVICE_OWNER_PERMISSIONS);
        hashMap2.put(2, PROFILE_OWNER_OF_ORGANIZATION_OWNED_DEVICE_PERMISSIONS);
        hashMap2.put(3, PROFILE_OWNER_ON_USER_0_PERMISSIONS);
        hashMap2.put(4, PROFILE_OWNER_PERMISSIONS);
        ACTIVE_ADMIN_POLICIES.put("android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", null);
        HashMap<String, String> hashMap3 = DELEGATE_SCOPES;
        hashMap3.put("android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "delegation-permission-grant");
        hashMap3.put("android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS", "delegation-app-restrictions");
        hashMap3.put("android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "delegation-block-uninstall");
        hashMap3.put("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", "delegation-security-logging");
        hashMap3.put("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "delegation-package-access");
        HashMap<String, String> hashMap4 = CROSS_USER_PERMISSIONS;
        hashMap4.put("android.permission.SET_TIME", null);
        hashMap4.put("android.permission.SET_TIME_ZONE", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_MTE", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_STATUS_BAR", null);
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_AUTOFILL", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_BLUETOOTH", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_CALLS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_CAMERA", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_DISPLAY", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_FUN", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCALE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCATION", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_MICROPHONE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PRINTING", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_RESTRICT_PRIVATE_DNS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PROFILES", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SMS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_TIME", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_USERS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_VPN", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_WALLPAPER", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_WIFI", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_WINDOWS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_COMMON_CRITERIA_MODE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_DEFAULT_SMS", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS");
        hashMap4.put("android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", "android.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL");
        POLICY_IDENTIFIER_TO_PERMISSION.put("autoTimezone", "android.permission.SET_TIME_ZONE");
        DevicePolicyManager.disableLocalCaches();
        this.mInjector = injector;
        this.mPathProvider = policyPathProvider;
        Context context = injector.mContext;
        Objects.requireNonNull(context);
        this.mContext = context;
        Looper myLooper = injector.getMyLooper();
        Objects.requireNonNull(myLooper);
        Handler handler = new Handler(myLooper);
        this.mHandler = handler;
        DevicePolicyConstantsObserver devicePolicyConstantsObserver = new DevicePolicyConstantsObserver(handler);
        this.mConstantsObserver = devicePolicyConstantsObserver;
        devicePolicyConstantsObserver.register();
        this.mConstants = loadConstants();
        UserManager userManager = injector.getUserManager();
        Objects.requireNonNull(userManager);
        this.mUserManager = userManager;
        UserManagerInternal userManagerInternal = injector.getUserManagerInternal();
        Objects.requireNonNull(userManagerInternal);
        this.mUserManagerInternal = userManagerInternal;
        UsageStatsManagerInternal usageStatsManagerInternal = injector.getUsageStatsManagerInternal();
        Objects.requireNonNull(usageStatsManagerInternal);
        this.mUsageStatsManagerInternal = usageStatsManagerInternal;
        IPackageManager iPackageManager = injector.getIPackageManager();
        Objects.requireNonNull(iPackageManager);
        this.mIPackageManager = iPackageManager;
        IPermissionManager iPermissionManager = injector.getIPermissionManager();
        Objects.requireNonNull(iPermissionManager);
        this.mIPermissionManager = iPermissionManager;
        TelephonyManager telephonyManager = injector.getTelephonyManager();
        Objects.requireNonNull(telephonyManager);
        this.mTelephonyManager = telephonyManager;
        RoleManager roleManager = injector.getRoleManager();
        Objects.requireNonNull(roleManager);
        this.mRoleManager = roleManager;
        LocalService localService = new LocalService();
        this.mLocalService = localService;
        this.mLockPatternUtils = injector.newLockPatternUtils();
        this.mLockSettingsInternal = injector.getLockSettingsInternal();
        this.mSecurityLogMonitor = new SecurityLogMonitor(this);
        boolean hasFeature = injector.hasFeature();
        this.mHasFeature = hasFeature;
        this.mIsWatch = injector.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        this.mHasTelephonyFeature = injector.getPackageManager().hasSystemFeature("android.hardware.telephony");
        this.mIsAutomotive = injector.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
        Handler handler2 = BackgroundThread.getHandler();
        this.mBackgroundHandler = handler2;
        this.mCertificateMonitor = new CertificateMonitor(this, injector, handler2);
        DeviceAdminServiceController deviceAdminServiceController = new DeviceAdminServiceController(this, this.mConstants);
        this.mDeviceAdminServiceController = deviceAdminServiceController;
        this.mOverlayPackagesProvider = new OverlayPackagesProvider(context);
        this.mTransferOwnershipMetadataManager = injector.newTransferOwnershipMetadataManager();
        this.mBugreportCollectionManager = new RemoteBugreportManager(this, injector);
        DeviceManagementResourcesProvider deviceManagementResourcesProvider = injector.getDeviceManagementResourcesProvider();
        this.mDeviceManagementResourcesProvider = deviceManagementResourcesProvider;
        DevicePolicyManagementRoleObserver devicePolicyManagementRoleObserver = new DevicePolicyManagementRoleObserver(context);
        this.mDevicePolicyManagementRoleObserver = devicePolicyManagementRoleObserver;
        devicePolicyManagementRoleObserver.register();
        LocalServices.addService(DevicePolicyManagerLiteInternal.class, localService);
        if (hasFeature) {
            performPolicyVersionUpgrade();
        }
        if (this.mKeepProfilesRunning) {
            suspendAppsForQuietProfiles();
        }
        this.mUserData = new SparseArray<>();
        this.mOwners = makeOwners(injector, policyPathProvider);
        DevicePolicyEngine devicePolicyEngine = new DevicePolicyEngine(context, deviceAdminServiceController);
        this.mDevicePolicyEngine = devicePolicyEngine;
        if (!hasFeature) {
            this.mSetupContentObserver = null;
            this.mContactSystemRoleHolders = Collections.emptySet();
            return;
        }
        loadOwners();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION");
        intentFilter.addAction(ACTION_TURN_PROFILE_ON_NOTIFICATION);
        intentFilter.addAction(ACTION_PROFILE_OFF_DEADLINE);
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.setPriority(1000);
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, handler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addDataScheme("package");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter2, null, handler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter3.addAction("android.intent.action.TIME_SET");
        intentFilter3.addAction("android.intent.action.DATE_CHANGED");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter3, null, handler);
        LocalServices.addService(DevicePolicyManagerInternal.class, localService);
        this.mSetupContentObserver = new SetupContentObserver(handler);
        userManagerInternal.addUserRestrictionsListener(new RestrictionsListener(context, userManagerInternal, this));
        userManagerInternal.addUserLifecycleListener(new UserLifecycleListener());
        deviceManagementResourcesProvider.load();
        if (isDevicePolicyEngineEnabled()) {
            devicePolicyEngine.load();
        }
        this.mContactSystemRoleHolders = fetchOemSystemHolders(17039396, 17039395, 17039403);
        invalidateBinderCaches();
    }

    public final Set<String> fetchOemSystemHolders(int... iArr) {
        ArraySet arraySet = new ArraySet();
        for (int i : iArr) {
            String defaultRoleHolderPackageName = getDefaultRoleHolderPackageName(i);
            if (defaultRoleHolderPackageName != null) {
                arraySet.add(defaultRoleHolderPackageName);
            }
        }
        return Collections.unmodifiableSet(arraySet);
    }

    public final String getDefaultRoleHolderPackageName(int i) {
        String string = this.mContext.getString(i);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string.contains(XmlUtils.STRING_ARRAY_SEPARATOR) ? string.split(XmlUtils.STRING_ARRAY_SEPARATOR)[0] : string;
    }

    public final void suspendAppsForQuietProfiles() {
        PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (userInfo.isManagedProfile() && userInfo.isQuietModeEnabled()) {
                packageManagerInternal.setPackagesSuspendedForQuietMode(userInfo.id, true);
            }
        }
    }

    public final Owners makeOwners(Injector injector, PolicyPathProvider policyPathProvider) {
        return new Owners(injector.getUserManager(), injector.getUserManagerInternal(), injector.getPackageManagerInternal(), injector.getActivityTaskManagerInternal(), injector.getActivityManagerInternal(), this.mStateCache, policyPathProvider);
    }

    public static void invalidateBinderCaches() {
        DevicePolicyManager.invalidateBinderCaches();
    }

    /* renamed from: getUserData */
    public DevicePolicyData lambda$getUserDataUnchecked$2(int i) {
        DevicePolicyData devicePolicyData;
        synchronized (getLockObject()) {
            devicePolicyData = this.mUserData.get(i);
            if (devicePolicyData == null) {
                devicePolicyData = new DevicePolicyData(i);
                this.mUserData.append(i, devicePolicyData);
                loadSettingsLocked(devicePolicyData, i);
                if (i == 0) {
                    this.mStateCache.setDeviceProvisioned(devicePolicyData.mUserSetupComplete);
                }
            }
        }
        return devicePolicyData;
    }

    public DevicePolicyData getUserDataUnchecked(final int i) {
        return (DevicePolicyData) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda86
            public final Object getOrThrow() {
                DevicePolicyData lambda$getUserDataUnchecked$2;
                lambda$getUserDataUnchecked$2 = DevicePolicyManagerService.this.lambda$getUserDataUnchecked$2(i);
                return lambda$getUserDataUnchecked$2;
            }
        });
    }

    public void removeUserData(int i) {
        synchronized (getLockObject()) {
            if (i == 0) {
                Slogf.m14w("DevicePolicyManager", "Tried to remove device policy file for user 0! Ignoring.");
                return;
            }
            updatePasswordQualityCacheForUserGroup(i);
            this.mPolicyCache.onUserRemoved(i);
            if (isManagedProfile(i)) {
                clearManagedProfileApnUnchecked();
            }
            boolean isProfileOwnerOfOrganizationOwnedDevice = this.mOwners.isProfileOwnerOfOrganizationOwnedDevice(i);
            this.mOwners.removeProfileOwner(i);
            this.mOwners.writeProfileOwner(i);
            pushScreenCapturePolicy(i);
            if (this.mUserData.get(i) != null) {
                this.mUserData.remove(i);
            }
            File file = new File(this.mPathProvider.getUserSystemDirectory(i), "device_policies.xml");
            file.delete();
            Slogf.m22i("DevicePolicyManager", "Removed device policy file " + file.getAbsolutePath());
            if (isProfileOwnerOfOrganizationOwnedDevice) {
                UserInfo primaryUser = this.mUserManager.getPrimaryUser();
                if (primaryUser != null) {
                    clearOrgOwnedProfileOwnerDeviceWidePolicies(primaryUser.id);
                } else {
                    Slogf.wtf("DevicePolicyManager", "Was unable to get primary user.");
                }
            }
        }
    }

    public void loadOwners() {
        synchronized (getLockObject()) {
            this.mOwners.load();
            setDeviceOwnershipSystemPropertyLocked();
            if (this.mOwners.hasDeviceOwner()) {
                Owners owners = this.mOwners;
                setGlobalSettingDeviceOwnerType(owners.getDeviceOwnerType(owners.getDeviceOwnerPackageName()));
            }
        }
    }

    public final CallerIdentity getCallerIdentity() {
        return getCallerIdentity(null, null);
    }

    public final CallerIdentity getCallerIdentity(String str) {
        return getCallerIdentity(null, str);
    }

    @VisibleForTesting
    public CallerIdentity getCallerIdentity(ComponentName componentName) {
        return getCallerIdentity(componentName, null);
    }

    @VisibleForTesting
    public CallerIdentity getCallerIdentity(ComponentName componentName, String str) {
        int binderGetCallingUid = this.mInjector.binderGetCallingUid();
        if (str != null && !isCallingFromPackage(str, binderGetCallingUid)) {
            throw new SecurityException(String.format("Caller with uid %d is not %s", Integer.valueOf(binderGetCallingUid), str));
        }
        if (componentName != null) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2(UserHandle.getUserId(binderGetCallingUid)).mAdminMap.get(componentName);
            if (activeAdmin == null || activeAdmin.getUid() != binderGetCallingUid) {
                throw new SecurityException(String.format("Admin %s does not exist or is not owned by uid %d", componentName, Integer.valueOf(binderGetCallingUid)));
            }
            if (str != null) {
                Preconditions.checkArgument(str.equals(componentName.getPackageName()));
            } else {
                str = componentName.getPackageName();
            }
        }
        return new CallerIdentity(binderGetCallingUid, str, componentName);
    }

    @GuardedBy({"getLockObject()"})
    public final void migrateToProfileOnOrganizationOwnedDeviceIfCompLocked() {
        int deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
        if (deviceOwnerUserId == -10000) {
            return;
        }
        List profiles = this.mUserManager.getProfiles(deviceOwnerUserId);
        if (profiles.size() != 2) {
            if (profiles.size() == 1) {
                return;
            }
            Slogf.wtf("DevicePolicyManager", "Found " + profiles.size() + " profiles, skipping migration");
            return;
        }
        int managedUserId = getManagedUserId(deviceOwnerUserId);
        if (managedUserId < 0) {
            Slogf.wtf("DevicePolicyManager", "Found DO and a profile, but it is not managed, skipping migration");
            return;
        }
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(managedUserId);
        if (deviceOwnerAdminLocked == null || profileOwnerAdminLocked == null) {
            Slogf.wtf("DevicePolicyManager", "Failed to get either PO or DO admin, aborting migration.");
            return;
        }
        ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
        ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(managedUserId);
        if (deviceOwnerComponent == null || profileOwnerComponent == null) {
            Slogf.wtf("DevicePolicyManager", "Cannot find PO or DO component name, aborting migration.");
        } else if (!deviceOwnerComponent.getPackageName().equals(profileOwnerComponent.getPackageName())) {
            Slogf.m26e("DevicePolicyManager", "DO and PO are different packages, aborting migration.");
        } else {
            Slogf.m20i("DevicePolicyManager", "Migrating COMP to PO on a corp owned device; primary user: %d; profile: %d", Integer.valueOf(deviceOwnerUserId), Integer.valueOf(managedUserId));
            Slogf.m22i("DevicePolicyManager", "Giving the PO additional power...");
            setProfileOwnerOnOrganizationOwnedDeviceUncheckedLocked(profileOwnerComponent, managedUserId, true);
            Slogf.m22i("DevicePolicyManager", "Migrating DO policies to PO...");
            moveDoPoliciesToProfileParentAdminLocked(deviceOwnerAdminLocked, profileOwnerAdminLocked.getParentActiveAdmin());
            migratePersonalAppSuspensionLocked(deviceOwnerUserId, managedUserId, profileOwnerAdminLocked);
            saveSettingsLocked(managedUserId);
            Slogf.m22i("DevicePolicyManager", "Clearing the DO...");
            ComponentName component = deviceOwnerAdminLocked.info.getComponent();
            clearDeviceOwnerLocked(deviceOwnerAdminLocked, deviceOwnerUserId);
            Slogf.m22i("DevicePolicyManager", "Removing admin artifacts...");
            removeAdminArtifacts(component, deviceOwnerUserId);
            Slogf.m22i("DevicePolicyManager", "Uninstalling the DO...");
            uninstallOrDisablePackage(deviceOwnerComponent.getPackageName(), deviceOwnerUserId);
            Slogf.m22i("DevicePolicyManager", "Migration complete.");
            DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__COMP_TO_ORG_OWNED_PO_MIGRATED).setAdmin(profileOwnerComponent).write();
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void migratePersonalAppSuspensionLocked(int i, int i2, ActiveAdmin activeAdmin) {
        PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
        if (!packageManagerInternal.isSuspendingAnyPackages(PackageManagerShellCommandDataLoader.PACKAGE, i)) {
            Slogf.m22i("DevicePolicyManager", "DO is not suspending any apps.");
        } else if (getTargetSdk(activeAdmin.info.getPackageName(), i2) >= 30) {
            Slogf.m22i("DevicePolicyManager", "PO is targeting R+, keeping personal apps suspended.");
            lambda$getUserDataUnchecked$2(i).mAppsSuspended = true;
            activeAdmin.mSuspendPersonalApps = true;
        } else {
            Slogf.m22i("DevicePolicyManager", "PO isn't targeting R+, unsuspending personal apps.");
            packageManagerInternal.unsuspendForSuspendingPackage(PackageManagerShellCommandDataLoader.PACKAGE, i);
        }
    }

    public final void uninstallOrDisablePackage(final String str, final int i) {
        try {
            ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(str, 786432L, i);
            if (applicationInfo == null) {
                Slogf.wtf("DevicePolicyManager", "Failed to get package info for " + str);
            } else if ((applicationInfo.flags & 1) != 0) {
                Slogf.m20i("DevicePolicyManager", "Package %s is pre-installed, marking disabled until used", str);
                this.mContext.getPackageManager().setApplicationEnabledSetting(str, 4, 0);
            } else {
                this.mInjector.getPackageManager(i).getPackageInstaller().uninstall(str, 0, new IntentSender(new IIntentSender.Stub() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.3
                    public void send(int i2, Intent intent, String str2, IBinder iBinder, IIntentReceiver iIntentReceiver, String str3, Bundle bundle) {
                        int intExtra = intent.getIntExtra("android.content.pm.extra.STATUS", 1);
                        if (intExtra == 0) {
                            Slogf.m20i("DevicePolicyManager", "Package %s uninstalled for user %d", str, Integer.valueOf(i));
                        } else {
                            Slogf.m24e("DevicePolicyManager", "Failed to uninstall %s; status: %d", str, Integer.valueOf(intExtra));
                        }
                    }
                }));
            }
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error getting application info", e);
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void moveDoPoliciesToProfileParentAdminLocked(ActiveAdmin activeAdmin, ActiveAdmin activeAdmin2) {
        if (activeAdmin2.mPasswordPolicy.quality == 0) {
            activeAdmin2.mPasswordPolicy = activeAdmin.mPasswordPolicy;
        }
        if (activeAdmin2.passwordHistoryLength == 0) {
            activeAdmin2.passwordHistoryLength = activeAdmin.passwordHistoryLength;
        }
        if (activeAdmin2.passwordExpirationTimeout == 0) {
            activeAdmin2.passwordExpirationTimeout = activeAdmin.passwordExpirationTimeout;
        }
        if (activeAdmin2.maximumFailedPasswordsForWipe == 0) {
            activeAdmin2.maximumFailedPasswordsForWipe = activeAdmin.maximumFailedPasswordsForWipe;
        }
        if (activeAdmin2.maximumTimeToUnlock == 0) {
            activeAdmin2.maximumTimeToUnlock = activeAdmin.maximumTimeToUnlock;
        }
        if (activeAdmin2.strongAuthUnlockTimeout == 259200000) {
            activeAdmin2.strongAuthUnlockTimeout = activeAdmin.strongAuthUnlockTimeout;
        }
        activeAdmin2.disabledKeyguardFeatures |= activeAdmin.disabledKeyguardFeatures & 950;
        activeAdmin2.trustAgentInfos.putAll((ArrayMap<? extends String, ? extends ActiveAdmin.TrustAgentInfo>) activeAdmin.trustAgentInfos);
        activeAdmin2.disableCamera = activeAdmin.disableCamera;
        activeAdmin2.disableScreenCapture = activeAdmin.disableScreenCapture;
        activeAdmin2.accountTypesWithManagementDisabled.addAll(activeAdmin.accountTypesWithManagementDisabled);
        moveDoUserRestrictionsToCopeParent(activeAdmin, activeAdmin2);
        if (activeAdmin.requireAutoTime) {
            activeAdmin2.ensureUserRestrictions().putBoolean("no_config_date_time", true);
        }
    }

    public final void moveDoUserRestrictionsToCopeParent(ActiveAdmin activeAdmin, ActiveAdmin activeAdmin2) {
        Bundle bundle = activeAdmin.userRestrictions;
        if (bundle == null) {
            return;
        }
        for (String str : bundle.keySet()) {
            if (UserRestrictionsUtils.canProfileOwnerOfOrganizationOwnedDeviceChange(str)) {
                activeAdmin2.ensureUserRestrictions().putBoolean(str, activeAdmin.userRestrictions.getBoolean(str));
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void applyProfileRestrictionsIfDeviceOwnerLocked() {
        if (this.mOwners.getDeviceOwnerUserId() == -10000) {
            return;
        }
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            UserHandle userHandle = userInfo.getUserHandle();
            if (!this.mUserManager.hasUserRestriction("no_add_clone_profile", userHandle)) {
                this.mUserManager.setUserRestriction("no_add_clone_profile", true, userHandle);
            }
            if (!this.mUserManager.hasUserRestriction("no_add_managed_profile", userHandle)) {
                this.mUserManager.setUserRestriction("no_add_managed_profile", true, userHandle);
            }
        }
    }

    public final void maybeSetDefaultProfileOwnerUserRestrictions() {
        synchronized (getLockObject()) {
            for (Integer num : this.mOwners.getProfileOwnerKeys()) {
                int intValue = num.intValue();
                ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(intValue);
                if (profileOwnerAdminLocked != null && this.mUserManager.isManagedProfile(intValue)) {
                    maybeSetDefaultRestrictionsForAdminLocked(intValue, profileOwnerAdminLocked);
                    ensureUnknownSourcesRestrictionForProfileOwnerLocked(intValue, profileOwnerAdminLocked, false);
                }
            }
        }
    }

    public final void ensureUnknownSourcesRestrictionForProfileOwnerLocked(int i, ActiveAdmin activeAdmin, boolean z) {
        if (z || this.mInjector.settingsSecureGetIntForUser("unknown_sources_default_reversed", 0, i) != 0) {
            if (isDevicePolicyEngineEnabled()) {
                this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.getPolicyDefinitionForUserRestriction("no_install_unknown_sources"), EnforcingAdmin.createEnterpriseEnforcingAdmin(activeAdmin.info.getComponent(), activeAdmin.getUserHandle().getIdentifier()), new BooleanPolicyValue(true), i);
            } else {
                activeAdmin.ensureUserRestrictions().putBoolean("no_install_unknown_sources", true);
                saveUserRestrictionsLocked(i);
            }
            this.mInjector.settingsSecurePutIntForUser("unknown_sources_default_reversed", 0, i);
        }
    }

    public final void maybeSetDefaultRestrictionsForAdminLocked(int i, ActiveAdmin activeAdmin) {
        Set<String> defaultEnabledForManagedProfiles = UserRestrictionsUtils.getDefaultEnabledForManagedProfiles();
        if (defaultEnabledForManagedProfiles.equals(activeAdmin.defaultEnabledRestrictionsAlreadySet)) {
            return;
        }
        if (isDevicePolicyEngineEnabled()) {
            for (String str : defaultEnabledForManagedProfiles) {
                this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.getPolicyDefinitionForUserRestriction(str), EnforcingAdmin.createEnterpriseEnforcingAdmin(activeAdmin.info.getComponent(), activeAdmin.getUserHandle().getIdentifier()), new BooleanPolicyValue(true), i);
            }
            activeAdmin.defaultEnabledRestrictionsAlreadySet.addAll(defaultEnabledForManagedProfiles);
            Slogf.m22i("DevicePolicyManager", "Enabled the following restrictions by default: " + defaultEnabledForManagedProfiles);
            return;
        }
        Slogf.m22i("DevicePolicyManager", "New user restrictions need to be set by default for user " + i);
        ArraySet<String> arraySet = new ArraySet(defaultEnabledForManagedProfiles);
        arraySet.removeAll(activeAdmin.defaultEnabledRestrictionsAlreadySet);
        if (arraySet.isEmpty()) {
            return;
        }
        for (String str2 : arraySet) {
            activeAdmin.ensureUserRestrictions().putBoolean(str2, true);
        }
        activeAdmin.defaultEnabledRestrictionsAlreadySet.addAll(arraySet);
        Slogf.m22i("DevicePolicyManager", "Enabled the following restrictions by default: " + arraySet);
        saveUserRestrictionsLocked(i);
    }

    public final void setDeviceOwnershipSystemPropertyLocked() {
        boolean z = false;
        boolean z2 = this.mInjector.settingsGlobalGetInt("device_provisioned", 0) != 0;
        boolean hasDeviceOwner = this.mOwners.hasDeviceOwner();
        boolean isOrganizationOwnedDeviceWithManagedProfile = isOrganizationOwnedDeviceWithManagedProfile();
        if (hasDeviceOwner || isOrganizationOwnedDeviceWithManagedProfile || z2) {
            if (hasDeviceOwner || isOrganizationOwnedDeviceWithManagedProfile) {
                z = true;
            }
            String bool = Boolean.toString(z);
            String systemPropertiesGet = this.mInjector.systemPropertiesGet("ro.organization_owned", null);
            if (TextUtils.isEmpty(systemPropertiesGet)) {
                Slogf.m22i("DevicePolicyManager", "Set ro.organization_owned property to " + bool);
                this.mInjector.systemPropertiesSet("ro.organization_owned", bool);
            } else if (bool.equals(systemPropertiesGet)) {
            } else {
                Slogf.m14w("DevicePolicyManager", "Cannot change existing ro.organization_owned to " + bool);
            }
        }
    }

    public final void maybeStartSecurityLogMonitorOnActivityManagerReady() {
        synchronized (getLockObject()) {
            if (this.mInjector.securityLogIsLoggingEnabled()) {
                this.mSecurityLogMonitor.start(getSecurityLoggingEnabledUser());
                this.mInjector.runCryptoSelfTest();
                maybePauseDeviceWideLoggingLocked();
            }
        }
    }

    public final void fixupAutoTimeRestrictionDuringOrganizationOwnedDeviceMigration() {
        ActiveAdmin activeAdmin;
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            int i = userInfo.id;
            if (isProfileOwnerOfOrganizationOwnedDevice(i) && (activeAdmin = getProfileOwnerAdminLocked(i).parentAdmin) != null && activeAdmin.requireAutoTime) {
                activeAdmin.requireAutoTime = false;
                saveSettingsLocked(i);
                this.mUserManagerInternal.setDevicePolicyUserRestrictions(0, new Bundle(), new RestrictionsSet(), false);
                activeAdmin.ensureUserRestrictions().putBoolean("no_config_date_time", true);
                pushUserRestrictions(i);
            }
        }
    }

    public final void setExpirationAlarmCheckLocked(final Context context, final int i, final boolean z) {
        long j;
        final long j2;
        long passwordExpirationLocked = getPasswordExpirationLocked(null, i, z);
        long currentTimeMillis = System.currentTimeMillis();
        long j3 = passwordExpirationLocked - currentTimeMillis;
        if (passwordExpirationLocked == 0) {
            j2 = 0;
        } else {
            if (j3 <= 0) {
                j = MS_PER_DAY;
            } else {
                j = MS_PER_DAY;
                long j4 = j3 % j;
                if (j4 != 0) {
                    j = j4;
                }
            }
            j2 = currentTimeMillis + j;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda15
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setExpirationAlarmCheckLocked$3(z, i, context, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setExpirationAlarmCheckLocked$3(boolean z, int i, Context context, long j) throws Exception {
        if (z) {
            i = getProfileParentId(i);
        }
        AlarmManager alarmManager = this.mInjector.getAlarmManager();
        PendingIntent broadcastAsUser = PendingIntent.getBroadcastAsUser(context, 5571, new Intent("com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION"), 1275068416, UserHandle.of(i));
        alarmManager.cancel(broadcastAsUser);
        if (j != 0) {
            alarmManager.set(1, j, broadcastAsUser);
        }
    }

    public ActiveAdmin getActiveAdminUncheckedLocked(ComponentName componentName, int i) {
        ensureLocked();
        ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2(i).mAdminMap.get(componentName);
        if (activeAdmin != null && componentName.getPackageName().equals(activeAdmin.info.getActivityInfo().packageName) && componentName.getClassName().equals(activeAdmin.info.getActivityInfo().name)) {
            return activeAdmin;
        }
        return null;
    }

    public ActiveAdmin getActiveAdminUncheckedLocked(ComponentName componentName, int i, boolean z) {
        ensureLocked();
        if (z) {
            Preconditions.checkCallAuthorization(isManagedProfile(i), "You can not call APIs on the parent profile outside a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
        }
        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
        return (activeAdminUncheckedLocked == null || !z) ? activeAdminUncheckedLocked : activeAdminUncheckedLocked.getParentActiveAdmin();
    }

    public ActiveAdmin getActiveAdminForCallerLocked(ComponentName componentName, int i) throws SecurityException {
        return getActiveAdminOrCheckPermissionForCallerLocked(componentName, i, null);
    }

    public ActiveAdmin getDeviceOwnerLocked(int i) {
        ensureLocked();
        return lambda$getUserDataUnchecked$2(i).mAdminMap.get(this.mOwners.getDeviceOwnerComponent());
    }

    public ActiveAdmin getProfileOwnerLocked(int i) {
        ensureLocked();
        return lambda$getUserDataUnchecked$2(i).mAdminMap.get(this.mOwners.getProfileOwnerComponent(i));
    }

    public ActiveAdmin getOrganizationOwnedProfileOwnerLocked(CallerIdentity callerIdentity) {
        Preconditions.checkCallAuthorization(this.mOwners.isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId()), "Caller %s is not an admin of an org-owned device", new Object[]{callerIdentity.getComponentName()});
        return getProfileOwnerLocked(callerIdentity.getUserId());
    }

    public ActiveAdmin getProfileOwnerOrDeviceOwnerLocked(int i) {
        ensureLocked();
        if (this.mOwners.getProfileOwnerComponent(i) != null) {
            return getProfileOwnerLocked(i);
        }
        return getDeviceOwnerLocked(i);
    }

    public ActiveAdmin getParentOfAdminIfRequired(ActiveAdmin activeAdmin, boolean z) {
        Objects.requireNonNull(activeAdmin);
        return z ? activeAdmin.getParentActiveAdmin() : activeAdmin;
    }

    public ActiveAdmin getActiveAdminOrCheckPermissionForCallerLocked(ComponentName componentName, int i, String str) throws SecurityException {
        return getActiveAdminOrCheckPermissionsForCallerLocked(componentName, i, str == null ? Set.of() : Set.of(str));
    }

    public ActiveAdmin getActiveAdminOrCheckPermissionsForCallerLocked(ComponentName componentName, int i, Set<String> set) throws SecurityException {
        ensureLocked();
        CallerIdentity callerIdentity = getCallerIdentity();
        ActiveAdmin activeAdminWithPolicyForUidLocked = getActiveAdminWithPolicyForUidLocked(componentName, i, callerIdentity.getUid());
        if (activeAdminWithPolicyForUidLocked != null) {
            return activeAdminWithPolicyForUidLocked;
        }
        for (String str : set) {
            if (hasCallingPermission(str)) {
                return null;
            }
        }
        if (componentName != null) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mAdminMap.get(componentName);
            boolean isDeviceOwner = isDeviceOwner(activeAdmin.info.getComponent(), callerIdentity.getUserId());
            boolean isProfileOwner = isProfileOwner(activeAdmin.info.getComponent(), callerIdentity.getUserId());
            if (DA_DISALLOWED_POLICIES.contains(Integer.valueOf(i)) && !isDeviceOwner && !isProfileOwner) {
                throw new SecurityException("Admin " + activeAdmin.info.getComponent() + " is not a device owner or profile owner, so may not use policy: " + activeAdmin.info.getTagForPolicy(i));
            }
            throw new SecurityException("Admin " + activeAdmin.info.getComponent() + " did not specify uses-policy for: " + activeAdmin.info.getTagForPolicy(i));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("No active admin owned by uid ");
        sb.append(callerIdentity.getUid());
        sb.append(" for policy #");
        sb.append(i);
        sb.append(set.isEmpty() ? "" : ", which doesn't have " + set);
        throw new SecurityException(sb.toString());
    }

    public ActiveAdmin getActiveAdminForCallerLocked(ComponentName componentName, int i, boolean z) throws SecurityException {
        return getActiveAdminOrCheckPermissionForCallerLocked(componentName, i, z, null);
    }

    public ActiveAdmin getActiveAdminOrCheckPermissionForCallerLocked(ComponentName componentName, int i, boolean z, String str) throws SecurityException {
        return getActiveAdminOrCheckPermissionsForCallerLocked(componentName, i, z, str == null ? Set.of() : Set.of(str));
    }

    public ActiveAdmin getActiveAdminOrCheckPermissionsForCallerLocked(ComponentName componentName, int i, boolean z, Set<String> set) throws SecurityException {
        ensureLocked();
        if (z) {
            Preconditions.checkCallingUser(isManagedProfile(getCallerIdentity().getUserId()));
        }
        ActiveAdmin activeAdminOrCheckPermissionsForCallerLocked = getActiveAdminOrCheckPermissionsForCallerLocked(componentName, i, set);
        return z ? activeAdminOrCheckPermissionsForCallerLocked.getParentActiveAdmin() : activeAdminOrCheckPermissionsForCallerLocked;
    }

    public final ActiveAdmin getActiveAdminForUidLocked(ComponentName componentName, int i) {
        ensureLocked();
        ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2(UserHandle.getUserId(i)).mAdminMap.get(componentName);
        if (activeAdmin == null) {
            throw new SecurityException("No active admin " + componentName + " for UID " + i);
        } else if (activeAdmin.getUid() == i) {
            return activeAdmin;
        } else {
            throw new SecurityException("Admin " + componentName + " is not owned by uid " + i);
        }
    }

    public final ActiveAdmin getActiveAdminWithPolicyForUidLocked(ComponentName componentName, int i, int i2) {
        ensureLocked();
        int userId = UserHandle.getUserId(i2);
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
        if (componentName != null) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminMap.get(componentName);
            if (activeAdmin == null || activeAdmin.getUid() != i2) {
                throw new SecurityException("Admin " + componentName + " is not active or not owned by uid " + i2);
            } else if (isActiveAdminWithPolicyForUserLocked(activeAdmin, i, userId)) {
                return activeAdmin;
            } else {
                return null;
            }
        }
        Iterator<ActiveAdmin> it = lambda$getUserDataUnchecked$2.mAdminList.iterator();
        while (it.hasNext()) {
            ActiveAdmin next = it.next();
            if (next.getUid() == i2 && isActiveAdminWithPolicyForUserLocked(next, i, userId)) {
                return next;
            }
        }
        return null;
    }

    @VisibleForTesting
    public boolean isActiveAdminWithPolicyForUserLocked(ActiveAdmin activeAdmin, int i, int i2) {
        ensureLocked();
        return (isDeviceOwner(activeAdmin.info.getComponent(), i2) || isProfileOwner(activeAdmin.info.getComponent(), i2) || !DA_DISALLOWED_POLICIES.contains(Integer.valueOf(i)) || getTargetSdk(activeAdmin.info.getPackageName(), i2) < 29) && activeAdmin.info.usesPolicy(i);
    }

    public void sendAdminCommandLocked(ActiveAdmin activeAdmin, String str) {
        sendAdminCommandLocked(activeAdmin, str, null);
    }

    public void sendAdminCommandLocked(ActiveAdmin activeAdmin, String str, BroadcastReceiver broadcastReceiver) {
        sendAdminCommandLocked(activeAdmin, str, (Bundle) null, broadcastReceiver);
    }

    public void sendAdminCommandLocked(ActiveAdmin activeAdmin, String str, Bundle bundle, BroadcastReceiver broadcastReceiver) {
        sendAdminCommandLocked(activeAdmin, str, bundle, broadcastReceiver, false);
    }

    public boolean sendAdminCommandLocked(ActiveAdmin activeAdmin, String str, Bundle bundle, BroadcastReceiver broadcastReceiver, boolean z) {
        Intent intent = new Intent(str);
        intent.setComponent(activeAdmin.info.getComponent());
        if (UserManager.isDeviceInDemoMode(this.mContext)) {
            intent.addFlags(268435456);
        }
        if (str.equals("android.app.action.ACTION_PASSWORD_EXPIRING")) {
            intent.putExtra("expiration", activeAdmin.passwordExpirationDate);
        }
        if (z) {
            intent.addFlags(268435456);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (this.mInjector.getPackageManager().queryBroadcastReceiversAsUser(intent, 268435456, activeAdmin.getUserHandle()).isEmpty()) {
            return false;
        }
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setBackgroundActivityStartsAllowed(true);
        if (broadcastReceiver != null) {
            this.mContext.sendOrderedBroadcastAsUser(intent, activeAdmin.getUserHandle(), null, -1, makeBasic.toBundle(), broadcastReceiver, this.mHandler, -1, null, null);
        } else {
            this.mContext.sendBroadcastAsUser(intent, activeAdmin.getUserHandle(), null, makeBasic.toBundle());
        }
        return true;
    }

    public void sendAdminCommandLocked(String str, int i, int i2, Bundle bundle) {
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i2);
        int size = lambda$getUserDataUnchecked$2.mAdminList.size();
        for (int i3 = 0; i3 < size; i3++) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(i3);
            if (activeAdmin.info.usesPolicy(i)) {
                sendAdminCommandLocked(activeAdmin, str, bundle, (BroadcastReceiver) null);
            }
        }
    }

    public final void sendAdminCommandToSelfAndProfilesLocked(String str, int i, int i2, Bundle bundle) {
        for (int i3 : this.mUserManager.getProfileIdsWithDisabled(i2)) {
            sendAdminCommandLocked(str, i, i3, bundle);
        }
    }

    public final void sendAdminCommandForLockscreenPoliciesLocked(String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("android.intent.extra.USER", UserHandle.of(i2));
        if (isSeparateProfileChallengeEnabled(i2)) {
            sendAdminCommandLocked(str, i, i2, bundle);
        } else {
            sendAdminCommandToSelfAndProfilesLocked(str, i, i2, bundle);
        }
    }

    /* renamed from: removeActiveAdminLocked */
    public void lambda$removeActiveAdmin$13(final ComponentName componentName, final int i) {
        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        if (activeAdminUncheckedLocked == null || lambda$getUserDataUnchecked$2.mRemovingAdmins.contains(componentName)) {
            return;
        }
        lambda$getUserDataUnchecked$2.mRemovingAdmins.add(componentName);
        sendAdminCommandLocked(activeAdminUncheckedLocked, "android.app.action.DEVICE_ADMIN_DISABLED", new BroadcastReceiver() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                DevicePolicyManagerService.this.removeAdminArtifacts(componentName, i);
                DevicePolicyManagerService.this.removePackageIfRequired(componentName.getPackageName(), i);
            }
        });
    }

    public final DeviceAdminInfo findAdmin(final ComponentName componentName, final int i, boolean z) {
        ActivityInfo activityInfo = (ActivityInfo) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda168
            public final Object getOrThrow() {
                ActivityInfo lambda$findAdmin$4;
                lambda$findAdmin$4 = DevicePolicyManagerService.this.lambda$findAdmin$4(componentName, i);
                return lambda$findAdmin$4;
            }
        });
        if (activityInfo == null) {
            throw new IllegalArgumentException("Unknown admin: " + componentName);
        }
        if (!"android.permission.BIND_DEVICE_ADMIN".equals(activityInfo.permission)) {
            String str = "DeviceAdminReceiver " + componentName + " must be protected with android.permission.BIND_DEVICE_ADMIN";
            Slogf.m14w("DevicePolicyManager", str);
            if (z && activityInfo.applicationInfo.targetSdkVersion > 23) {
                throw new IllegalArgumentException(str);
            }
        }
        try {
            return new DeviceAdminInfo(this.mContext, activityInfo);
        } catch (IOException | XmlPullParserException e) {
            Slogf.m13w("DevicePolicyManager", "Bad device admin requested for user=" + i + ": " + componentName, e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ActivityInfo lambda$findAdmin$4(ComponentName componentName, int i) throws Exception {
        try {
            return this.mIPackageManager.getReceiverInfo(componentName, 819328L, i);
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error getting receiver info", e);
            return null;
        }
    }

    public final File getPolicyFileDirectory(int i) {
        if (i == 0) {
            return this.mPathProvider.getDataSystemDirectory();
        }
        return this.mPathProvider.getUserSystemDirectory(i);
    }

    public final JournaledFile makeJournaledFile(int i, String str) {
        String absolutePath = new File(getPolicyFileDirectory(i), str).getAbsolutePath();
        File file = new File(absolutePath);
        return new JournaledFile(file, new File(absolutePath + ".tmp"));
    }

    public final JournaledFile makeJournaledFile(int i) {
        return makeJournaledFile(i, "device_policies.xml");
    }

    @GuardedBy({"getLockObject()"})
    public final void saveSettingsForUsersLocked(Set<Integer> set) {
        for (Integer num : set) {
            saveSettingsLocked(num.intValue());
        }
    }

    public final void saveSettingsLocked(int i) {
        if (DevicePolicyData.store(lambda$getUserDataUnchecked$2(i), makeJournaledFile(i))) {
            sendChangedNotification(i);
        }
        invalidateBinderCaches();
    }

    public final void sendChangedNotification(final int i) {
        final Intent intent = new Intent("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        intent.setFlags(1073741824);
        final Bundle bundle = new BroadcastOptions().setDeliveryGroupPolicy(1).setDeferUntilActive(true).toBundle();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda155
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$sendChangedNotification$5(intent, i, bundle);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendChangedNotification$5(Intent intent, int i, Bundle bundle) throws Exception {
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(i), null, bundle);
    }

    public final void loadSettingsLocked(DevicePolicyData devicePolicyData, final int i) {
        DevicePolicyData.load(devicePolicyData, makeJournaledFile(i), new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda113
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                DeviceAdminInfo lambda$loadSettingsLocked$6;
                lambda$loadSettingsLocked$6 = DevicePolicyManagerService.this.lambda$loadSettingsLocked$6(i, (ComponentName) obj);
                return lambda$loadSettingsLocked$6;
            }
        }, getOwnerComponent(i));
        devicePolicyData.validatePasswordOwner();
        updateMaximumTimeToLockLocked(i);
        updateLockTaskPackagesLocked(this.mContext, devicePolicyData.mLockTaskPackages, i);
        updateLockTaskFeaturesLocked(devicePolicyData.mLockTaskFeatures, i);
        boolean z = devicePolicyData.mStatusBarDisabled;
        if (z) {
            setStatusBarDisabledInternal(z, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DeviceAdminInfo lambda$loadSettingsLocked$6(int i, ComponentName componentName) {
        return findAdmin(componentName, i, false);
    }

    public static void updateLockTaskPackagesLocked(final Context context, final List<String> list, final int i) {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda129
            public final void runOrThrow() {
                DevicePolicyManagerService.lambda$updateLockTaskPackagesLocked$7(list, context, i);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0028  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static /* synthetic */ void lambda$updateLockTaskPackagesLocked$7(List list, Context context, int i) throws Exception {
        String[] strArr;
        try {
            if (!list.isEmpty()) {
                List<String> listPolicyExemptAppsUnchecked = listPolicyExemptAppsUnchecked(context);
                if (!listPolicyExemptAppsUnchecked.isEmpty()) {
                    HashSet hashSet = new HashSet(list);
                    hashSet.addAll(listPolicyExemptAppsUnchecked);
                    strArr = (String[]) hashSet.toArray(new String[hashSet.size()]);
                    if (strArr == null) {
                        strArr = (String[]) list.toArray(new String[list.size()]);
                    }
                    ActivityManager.getService().updateLockTaskPackages(i, strArr);
                    return;
                }
            }
            ActivityManager.getService().updateLockTaskPackages(i, strArr);
            return;
        } catch (RemoteException e) {
            Slog.wtf("DevicePolicyManager", "Remote Exception: ", e);
            return;
        }
        strArr = null;
        if (strArr == null) {
        }
    }

    public static void updateLockTaskFeaturesLocked(final int i, final int i2) {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda154
            public final void runOrThrow() {
                DevicePolicyManagerService.lambda$updateLockTaskFeaturesLocked$8(i2, i);
            }
        });
    }

    public static /* synthetic */ void lambda$updateLockTaskFeaturesLocked$8(int i, int i2) throws Exception {
        try {
            ActivityTaskManager.getService().updateLockTaskFeatures(i, i2);
        } catch (RemoteException e) {
            Slog.wtf("DevicePolicyManager", "Remote Exception: ", e);
        }
    }

    public static void validateQualityConstant(int i) {
        if (i == 0 || i == 32768 || i == 65536 || i == 131072 || i == 196608 || i == 262144 || i == 327680 || i == 393216 || i == 524288) {
            return;
        }
        throw new IllegalArgumentException("Invalid quality constant: 0x" + Integer.toHexString(i));
    }

    @VisibleForTesting
    public void systemReady(int i) {
        if (this.mHasFeature) {
            if (i == 480) {
                onLockSettingsReady();
                loadAdminDataAsync();
                this.mOwners.systemReady();
                if (isWorkProfileTelephonyFlagEnabled()) {
                    applyManagedSubscriptionsPolicyIfRequired();
                }
            } else if (i != 550) {
                if (i != 1000) {
                    return;
                }
                factoryResetIfDelayedEarlier();
                ensureDeviceOwnerUserStarted();
            } else {
                synchronized (getLockObject()) {
                    migrateToProfileOnOrganizationOwnedDeviceIfCompLocked();
                    applyProfileRestrictionsIfDeviceOwnerLocked();
                    if (shouldMigrateToDevicePolicyEngine()) {
                        migratePoliciesToDevicePolicyEngine();
                    }
                }
                maybeStartSecurityLogMonitorOnActivityManagerReady();
            }
        }
    }

    public final void applyManagedSubscriptionsPolicyIfRequired() {
        int organizationOwnedProfileUserId = getOrganizationOwnedProfileUserId();
        if (organizationOwnedProfileUserId != -10000) {
            unregisterOnSubscriptionsChangedListener();
            int policyType = getManagedSubscriptionsPolicy().getPolicyType();
            if (policyType == 0) {
                registerListenerToAssignSubscriptionsToUser(getProfileParentId(organizationOwnedProfileUserId));
            } else if (policyType == 1) {
                registerListenerToAssignSubscriptionsToUser(organizationOwnedProfileUserId);
            }
        }
    }

    public final void updatePersonalAppsSuspensionOnUserStart(int i) {
        int managedUserId = getManagedUserId(i);
        if (managedUserId >= 0) {
            updatePersonalAppsSuspension(managedUserId, false);
        } else {
            suspendPersonalAppsInternal(i, false);
        }
    }

    public final void onLockSettingsReady() {
        List<String> keepUninstalledPackagesLocked;
        synchronized (getLockObject()) {
            fixupAutoTimeRestrictionDuringOrganizationOwnedDeviceMigration();
        }
        lambda$getUserDataUnchecked$2(0);
        cleanUpOldUsers();
        maybeSetDefaultProfileOwnerUserRestrictions();
        handleStartUser(0);
        maybeLogStart();
        this.mSetupContentObserver.register();
        updateUserSetupCompleteAndPaired();
        synchronized (getLockObject()) {
            keepUninstalledPackagesLocked = getKeepUninstalledPackagesLocked();
        }
        if (keepUninstalledPackagesLocked != null) {
            this.mInjector.getPackageManagerInternal().setKeepUninstalledPackages(keepUninstalledPackagesLocked);
        }
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
            if (deviceOwnerAdminLocked != null) {
                this.mUserManagerInternal.setForceEphemeralUsers(deviceOwnerAdminLocked.forceEphemeralUsers);
                ActivityManagerInternal activityManagerInternal = this.mInjector.getActivityManagerInternal();
                activityManagerInternal.setSwitchingFromSystemUserMessage(deviceOwnerAdminLocked.startUserSessionMessage);
                activityManagerInternal.setSwitchingToSystemUserMessage(deviceOwnerAdminLocked.endUserSessionMessage);
            }
            revertTransferOwnershipIfNecessaryLocked();
        }
        updateUsbDataSignal();
    }

    /* loaded from: classes.dex */
    public class DpmsUpgradeDataProvider implements PolicyUpgraderDataProvider {
        public DpmsUpgradeDataProvider() {
        }

        @Override // com.android.server.devicepolicy.PolicyUpgraderDataProvider
        public JournaledFile makeDevicePoliciesJournaledFile(int i) {
            return DevicePolicyManagerService.this.makeJournaledFile(i, "device_policies.xml");
        }

        @Override // com.android.server.devicepolicy.PolicyUpgraderDataProvider
        public JournaledFile makePoliciesVersionJournaledFile(int i) {
            return DevicePolicyManagerService.this.makeJournaledFile(i, "device_policies_version");
        }

        @Override // com.android.server.devicepolicy.PolicyUpgraderDataProvider
        public Function<ComponentName, DeviceAdminInfo> getAdminInfoSupplier(final int i) {
            return new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DpmsUpgradeDataProvider$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    DeviceAdminInfo lambda$getAdminInfoSupplier$0;
                    lambda$getAdminInfoSupplier$0 = DevicePolicyManagerService.DpmsUpgradeDataProvider.this.lambda$getAdminInfoSupplier$0(i, (ComponentName) obj);
                    return lambda$getAdminInfoSupplier$0;
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ DeviceAdminInfo lambda$getAdminInfoSupplier$0(int i, ComponentName componentName) {
            return DevicePolicyManagerService.this.findAdmin(componentName, i, false);
        }

        @Override // com.android.server.devicepolicy.PolicyUpgraderDataProvider
        public int[] getUsersForUpgrade() {
            return DevicePolicyManagerService.this.mUserManager.getUsers().stream().mapToInt(new ToIntFunction() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DpmsUpgradeDataProvider$$ExternalSyntheticLambda3
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int i;
                    i = ((UserInfo) obj).id;
                    return i;
                }
            }).toArray();
        }

        @Override // com.android.server.devicepolicy.PolicyUpgraderDataProvider
        public List<String> getPlatformSuspendedPackages(final int i) {
            final PackageManagerInternal packageManagerInternal = DevicePolicyManagerService.this.mInjector.getPackageManagerInternal();
            return (List) DevicePolicyManagerService.this.mInjector.getPackageManager(i).getInstalledPackages(PackageManager.PackageInfoFlags.of(786432L)).stream().map(new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DpmsUpgradeDataProvider$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((PackageInfo) obj).packageName;
                    return str;
                }
            }).filter(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DpmsUpgradeDataProvider$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getPlatformSuspendedPackages$3;
                    lambda$getPlatformSuspendedPackages$3 = DevicePolicyManagerService.DpmsUpgradeDataProvider.lambda$getPlatformSuspendedPackages$3(PackageManagerInternal.this, i, (String) obj);
                    return lambda$getPlatformSuspendedPackages$3;
                }
            }).collect(Collectors.toList());
        }

        public static /* synthetic */ boolean lambda$getPlatformSuspendedPackages$3(PackageManagerInternal packageManagerInternal, int i, String str) {
            return PackageManagerShellCommandDataLoader.PACKAGE.equals(packageManagerInternal.getSuspendingPackage(str, i));
        }
    }

    public final void performPolicyVersionUpgrade() {
        new PolicyVersionUpgrader(new DpmsUpgradeDataProvider(), this.mPathProvider).upgradePolicy(4);
    }

    public final void revertTransferOwnershipIfNecessaryLocked() {
        if (this.mTransferOwnershipMetadataManager.metadataFileExists()) {
            Slogf.m26e("DevicePolicyManager", "Owner transfer metadata file exists! Reverting transfer.");
            TransferOwnershipMetadataManager.Metadata loadMetadataFile = this.mTransferOwnershipMetadataManager.loadMetadataFile();
            if (loadMetadataFile.adminType.equals("profile-owner")) {
                transferProfileOwnershipLocked(loadMetadataFile.targetComponent, loadMetadataFile.sourceComponent, loadMetadataFile.userId);
                deleteTransferOwnershipMetadataFileLocked();
                deleteTransferOwnershipBundleLocked(loadMetadataFile.userId);
            } else if (loadMetadataFile.adminType.equals("device-owner")) {
                transferDeviceOwnershipLocked(loadMetadataFile.targetComponent, loadMetadataFile.sourceComponent, loadMetadataFile.userId);
                deleteTransferOwnershipMetadataFileLocked();
                deleteTransferOwnershipBundleLocked(loadMetadataFile.userId);
            }
            updateSystemUpdateFreezePeriodsRecord(true);
            pushUserControlDisabledPackagesLocked(loadMetadataFile.userId);
        }
    }

    public final void maybeLogStart() {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210009, new Object[]{this.mInjector.systemPropertiesGet("ro.boot.verifiedbootstate"), this.mInjector.systemPropertiesGet("ro.boot.veritymode")});
        }
    }

    public final void ensureDeviceOwnerUserStarted() {
        synchronized (getLockObject()) {
            if (this.mOwners.hasDeviceOwner()) {
                int deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
                if (deviceOwnerUserId != 0) {
                    try {
                        this.mInjector.getIActivityManager().startUserInBackground(deviceOwnerUserId);
                    } catch (RemoteException e) {
                        Slogf.m13w("DevicePolicyManager", "Exception starting user", e);
                    }
                }
            }
        }
    }

    public void handleStartUser(int i) {
        List<PreferentialNetworkServiceConfig> of;
        boolean z;
        ManagedSubscriptionsPolicy managedSubscriptionsPolicy;
        synchronized (getLockObject()) {
            pushScreenCapturePolicy(i);
            pushUserControlDisabledPackagesLocked(i);
        }
        pushUserRestrictions(i);
        updatePasswordQualityCacheForUserGroup(i == 0 ? -1 : i);
        updatePermissionPolicyCache(i);
        updateAdminCanGrantSensorsPermissionCache(i);
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            if (deviceOrProfileOwnerAdminLocked != null) {
                of = deviceOrProfileOwnerAdminLocked.mPreferentialNetworkServiceConfigs;
            } else {
                of = List.of(PreferentialNetworkServiceConfig.DEFAULT);
            }
            if (deviceOrProfileOwnerAdminLocked != null && (managedSubscriptionsPolicy = deviceOrProfileOwnerAdminLocked.mManagedSubscriptionsPolicy) != null) {
                z = true;
                if (managedSubscriptionsPolicy.getPolicyType() == 1) {
                }
            }
            z = false;
        }
        updateNetworkPreferenceForUser(i, of);
        if (z) {
            updateDialerAndSmsManagedShortcutsOverrideCache(getDefaultRoleHolderPackageName(17039395), getDefaultRoleHolderPackageName(17039396));
        }
        startOwnerService(i, "start-user");
        if (isDevicePolicyEngineEnabled()) {
            this.mDevicePolicyEngine.handleStartUser(i);
        }
    }

    public void pushUserControlDisabledPackagesLocked(int i) {
        final int i2;
        ActiveAdmin profileOwnerAdminLocked;
        if (getDeviceOwnerUserIdUncheckedLocked() == i) {
            profileOwnerAdminLocked = getDeviceOwnerAdminLocked();
            i2 = -1;
        } else {
            i2 = i;
            profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
        }
        final List<String> list = (profileOwnerAdminLocked == null || (list = profileOwnerAdminLocked.protectedPackages) == null) ? null : null;
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda19
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$pushUserControlDisabledPackagesLocked$9(i2, list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pushUserControlDisabledPackagesLocked$9(int i, List list) throws Exception {
        this.mInjector.getPackageManagerInternal().setOwnerProtectedPackages(i, list);
    }

    public void handleUnlockUser(int i) {
        startOwnerService(i, "unlock-user");
        if (isDevicePolicyEngineEnabled()) {
            this.mDevicePolicyEngine.handleUnlockUser(i);
        }
    }

    public void handleOnUserUnlocked(int i) {
        showNewUserDisclaimerIfNecessary(i);
    }

    public void handleStopUser(int i) {
        updateNetworkPreferenceForUser(i, List.of(PreferentialNetworkServiceConfig.DEFAULT));
        this.mDeviceAdminServiceController.stopServicesForUser(i, "stop-user");
        if (isDevicePolicyEngineEnabled()) {
            this.mDevicePolicyEngine.handleStopUser(i);
        }
    }

    public final void startOwnerService(int i, String str) {
        ComponentName ownerComponent = getOwnerComponent(i);
        if (ownerComponent != null) {
            this.mDeviceAdminServiceController.startServiceForAdmin(ownerComponent.getPackageName(), i, str);
            invalidateBinderCaches();
        }
    }

    public final void cleanUpOldUsers() {
        Set<Integer> profileOwnerKeys;
        ArraySet arraySet;
        synchronized (getLockObject()) {
            profileOwnerKeys = this.mOwners.getProfileOwnerKeys();
            arraySet = new ArraySet();
            for (int i = 0; i < this.mUserData.size(); i++) {
                arraySet.add(Integer.valueOf(this.mUserData.keyAt(i)));
            }
        }
        List<UserInfo> users = this.mUserManager.getUsers();
        ArraySet<Integer> arraySet2 = new ArraySet();
        arraySet2.addAll(profileOwnerKeys);
        arraySet2.addAll((Collection) arraySet);
        for (UserInfo userInfo : users) {
            arraySet2.remove(Integer.valueOf(userInfo.id));
        }
        for (Integer num : arraySet2) {
            removeUserData(num.intValue());
        }
    }

    public final void handlePasswordExpirationNotification(int i) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("android.intent.extra.USER", UserHandle.of(i));
        synchronized (getLockObject()) {
            long currentTimeMillis = System.currentTimeMillis();
            List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(i);
            int size = activeAdminsForLockscreenPoliciesLocked.size();
            for (int i2 = 0; i2 < size; i2++) {
                ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i2);
                if ((activeAdmin.isPermissionBased || activeAdmin.info.usesPolicy(6)) && activeAdmin.passwordExpirationTimeout > 0) {
                    long j = activeAdmin.passwordExpirationDate;
                    if (currentTimeMillis >= j - EXPIRATION_GRACE_PERIOD_MS && j > 0) {
                        sendAdminCommandLocked(activeAdmin, "android.app.action.ACTION_PASSWORD_EXPIRING", bundle, (BroadcastReceiver) null);
                    }
                }
            }
            setExpirationAlarmCheckLocked(this.mContext, i, false);
        }
    }

    public void onInstalledCertificatesChanged(UserHandle userHandle, Collection<String> collection) {
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userHandle.getIdentifier());
                if (lambda$getUserDataUnchecked$2.mOwnerInstalledCaCerts.retainAll(collection) | lambda$getUserDataUnchecked$2.mAcceptedCaCertificates.retainAll(collection) | false) {
                    saveSettingsLocked(userHandle.getIdentifier());
                }
            }
        }
    }

    public Set<String> getAcceptedCaCertificates(UserHandle userHandle) {
        ArraySet<String> arraySet;
        if (!this.mHasFeature) {
            return Collections.emptySet();
        }
        synchronized (getLockObject()) {
            arraySet = lambda$getUserDataUnchecked$2(userHandle.getIdentifier()).mAcceptedCaCertificates;
        }
        return arraySet;
    }

    public void setActiveAdmin(final ComponentName componentName, final boolean z, final int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS"));
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            final DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            final DeviceAdminInfo findAdmin = findAdmin(componentName, i, true);
            synchronized (getLockObject()) {
                checkActiveAdminPrecondition(componentName, findAdmin, lambda$getUserDataUnchecked$2);
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda97
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setActiveAdmin$10(componentName, i, z, findAdmin, lambda$getUserDataUnchecked$2);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setActiveAdmin$10(ComponentName componentName, int i, boolean z, DeviceAdminInfo deviceAdminInfo, DevicePolicyData devicePolicyData) throws Exception {
        boolean isPackageTestOnly;
        if (!canAddActiveAdminIfPolicyEngineEnabled(componentName.getPackageName(), i)) {
            throw new IllegalStateException("Can't add non-coexistable admin.");
        }
        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
        if (!z && activeAdminUncheckedLocked != null) {
            throw new IllegalArgumentException("Admin is already added");
        }
        int i2 = 0;
        ActiveAdmin activeAdmin = new ActiveAdmin(deviceAdminInfo, false);
        if (activeAdminUncheckedLocked != null) {
            isPackageTestOnly = activeAdminUncheckedLocked.testOnlyAdmin;
        } else {
            isPackageTestOnly = isPackageTestOnly(componentName.getPackageName(), i);
        }
        activeAdmin.testOnlyAdmin = isPackageTestOnly;
        devicePolicyData.mAdminMap.put(componentName, activeAdmin);
        int size = devicePolicyData.mAdminList.size();
        while (true) {
            if (i2 >= size) {
                i2 = -1;
                break;
            } else if (devicePolicyData.mAdminList.get(i2).info.getComponent().equals(componentName)) {
                break;
            } else {
                i2++;
            }
        }
        if (i2 == -1) {
            devicePolicyData.mAdminList.add(activeAdmin);
            enableIfNecessary(deviceAdminInfo.getPackageName(), i);
            this.mUsageStatsManagerInternal.onActiveAdminAdded(componentName.getPackageName(), i);
        } else {
            devicePolicyData.mAdminList.set(i2, activeAdmin);
        }
        saveSettingsLocked(i);
        sendAdminCommandLocked(activeAdmin, "android.app.action.DEVICE_ADMIN_ENABLED", (Bundle) null, (BroadcastReceiver) null);
    }

    public final void loadAdminDataAsync() {
        this.mInjector.postOnSystemServerInitThreadPool(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$loadAdminDataAsync$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminDataAsync$11() {
        pushActiveAdminPackages();
        this.mUsageStatsManagerInternal.onAdminDataAvailable();
        pushAllMeteredRestrictedPackages();
        this.mInjector.getNetworkPolicyManagerInternal().onAdminDataAvailable();
    }

    public final void pushActiveAdminPackages() {
        synchronized (getLockObject()) {
            List users = this.mUserManager.getUsers();
            for (int size = users.size() - 1; size >= 0; size--) {
                int i = ((UserInfo) users.get(size)).id;
                this.mUsageStatsManagerInternal.setActiveAdminApps(getActiveAdminPackagesLocked(i), i);
            }
        }
    }

    public final void pushAllMeteredRestrictedPackages() {
        synchronized (getLockObject()) {
            List users = this.mUserManager.getUsers();
            for (int size = users.size() - 1; size >= 0; size--) {
                int i = ((UserInfo) users.get(size)).id;
                this.mInjector.getNetworkPolicyManagerInternal().setMeteredRestrictedPackagesAsync(getMeteredDisabledPackages(i), i);
            }
        }
    }

    public final void pushActiveAdminPackagesLocked(int i) {
        this.mUsageStatsManagerInternal.setActiveAdminApps(getActiveAdminPackagesLocked(i), i);
    }

    public final Set<String> getActiveAdminPackagesLocked(int i) {
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        ArraySet arraySet = null;
        for (int size = lambda$getUserDataUnchecked$2.mAdminList.size() - 1; size >= 0; size--) {
            String packageName = lambda$getUserDataUnchecked$2.mAdminList.get(size).info.getPackageName();
            if (arraySet == null) {
                arraySet = new ArraySet();
            }
            arraySet.add(packageName);
        }
        return arraySet;
    }

    public final void transferActiveAdminUncheckedLocked(ComponentName componentName, ComponentName componentName2, int i) {
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        if (lambda$getUserDataUnchecked$2.mAdminMap.containsKey(componentName2) || !lambda$getUserDataUnchecked$2.mAdminMap.containsKey(componentName)) {
            DeviceAdminInfo findAdmin = findAdmin(componentName, i, true);
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminMap.get(componentName2);
            int uid = activeAdmin.getUid();
            activeAdmin.transfer(findAdmin);
            lambda$getUserDataUnchecked$2.mAdminMap.remove(componentName2);
            lambda$getUserDataUnchecked$2.mAdminMap.put(componentName, activeAdmin);
            if (lambda$getUserDataUnchecked$2.mPasswordOwner == uid) {
                lambda$getUserDataUnchecked$2.mPasswordOwner = activeAdmin.getUid();
            }
            saveSettingsLocked(i);
            sendAdminCommandLocked(activeAdmin, "android.app.action.DEVICE_ADMIN_ENABLED", (Bundle) null, (BroadcastReceiver) null);
        }
    }

    public final void checkActiveAdminPrecondition(ComponentName componentName, DeviceAdminInfo deviceAdminInfo, DevicePolicyData devicePolicyData) {
        if (deviceAdminInfo == null) {
            throw new IllegalArgumentException("Bad admin: " + componentName);
        } else if (!deviceAdminInfo.getActivityInfo().applicationInfo.isInternal()) {
            throw new IllegalArgumentException("Only apps in internal storage can be active admin: " + componentName);
        } else if (deviceAdminInfo.getActivityInfo().applicationInfo.isInstantApp()) {
            throw new IllegalArgumentException("Instant apps cannot be device admins: " + componentName);
        } else if (devicePolicyData.mRemovingAdmins.contains(componentName)) {
            throw new IllegalArgumentException("Trying to set an admin which is being removed");
        }
    }

    public final void checkAllUsersAreAffiliatedWithDevice() {
        Preconditions.checkCallAuthorization(areAllUsersAffiliatedWithDeviceLocked(), "operation not allowed when device has unaffiliated users");
    }

    public boolean isAdminActive(ComponentName componentName, int i) {
        boolean z;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            synchronized (getLockObject()) {
                z = getActiveAdminUncheckedLocked(componentName, i) != null;
            }
            return z;
        }
        return false;
    }

    public boolean isRemovingAdmin(ComponentName componentName, int i) {
        boolean contains;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            synchronized (getLockObject()) {
                contains = lambda$getUserDataUnchecked$2(i).mRemovingAdmins.contains(componentName);
            }
            return contains;
        }
        return false;
    }

    public boolean hasGrantedPolicy(ComponentName componentName, int i, int i2) {
        boolean usesPolicy;
        boolean z = false;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i2, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i2));
            Preconditions.checkCallAuthorization((isCallingFromPackage(componentName.getPackageName(), callerIdentity.getUid()) || isSystemUid(callerIdentity)) ? true : true);
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i2);
                if (activeAdminUncheckedLocked == null) {
                    throw new SecurityException("No active admin " + componentName);
                }
                usesPolicy = activeAdminUncheckedLocked.info.usesPolicy(i);
            }
            return usesPolicy;
        }
        return false;
    }

    public List<ComponentName> getActiveAdmins(int i) {
        if (!this.mHasFeature) {
            return Collections.EMPTY_LIST;
        }
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            int size = lambda$getUserDataUnchecked$2.mAdminList.size();
            if (size <= 0) {
                return null;
            }
            ArrayList arrayList = new ArrayList(size);
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(lambda$getUserDataUnchecked$2.mAdminList.get(i2).info.getComponent());
            }
            return arrayList;
        }
    }

    public boolean packageHasActiveAdmins(String str, int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
                int size = lambda$getUserDataUnchecked$2.mAdminList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (lambda$getUserDataUnchecked$2.mAdminList.get(i2).info.getPackageName().equals(str)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void forceRemoveActiveAdmin(final ComponentName componentName, final int i) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isAdb(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"), "Caller must be shell or hold MANAGE_PROFILE_AND_DEVICE_OWNERS to call forceRemoveActiveAdmin");
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda136
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$forceRemoveActiveAdmin$12(componentName, i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$forceRemoveActiveAdmin$12(ComponentName componentName, int i) throws Exception {
        boolean z;
        synchronized (getLockObject()) {
            if (!isAdminTestOnlyLocked(componentName, i)) {
                throw new SecurityException("Attempt to remove non-test admin " + componentName + " " + i);
            }
            if (isDeviceOwner(componentName, i)) {
                clearDeviceOwnerLocked(getDeviceOwnerAdminLocked(), i);
            }
            z = false;
            if (isProfileOwner(componentName, i)) {
                boolean isProfileOwnerOfOrganizationOwnedDevice = isProfileOwnerOfOrganizationOwnedDevice(i);
                clearProfileOwnerLocked(getActiveAdminUncheckedLocked(componentName, i, false), i);
                z = isProfileOwnerOfOrganizationOwnedDevice;
            }
        }
        removeAdminArtifacts(componentName, i);
        if (z) {
            UserHandle of = UserHandle.of(getProfileParentId(i));
            lambda$wipeDataWithReason$52(of);
            clearOrgOwnedProfileOwnerDeviceWidePolicies(of.getIdentifier());
        }
        Slogf.m22i("DevicePolicyManager", "Admin " + componentName + " removed from user " + i);
    }

    /* renamed from: clearOrgOwnedProfileOwnerUserRestrictions */
    public final void lambda$wipeDataWithReason$52(UserHandle userHandle) {
        this.mUserManager.setUserRestriction("no_remove_managed_profile", false, userHandle);
        this.mUserManager.setUserRestriction("no_add_user", false, userHandle);
    }

    public final void clearDeviceOwnerUserRestriction(UserHandle userHandle) {
        if (isHeadlessFlagEnabled()) {
            for (int i : this.mUserManagerInternal.getUserIds()) {
                UserHandle of = UserHandle.of(i);
                if (this.mUserManager.hasUserRestriction("no_add_user", of)) {
                    this.mUserManager.setUserRestriction("no_add_user", false, of);
                }
                if (this.mUserManager.hasUserRestriction("no_add_managed_profile", of)) {
                    this.mUserManager.setUserRestriction("no_add_managed_profile", false, of);
                }
                if (this.mUserManager.hasUserRestriction("no_add_clone_profile", of)) {
                    this.mUserManager.setUserRestriction("no_add_clone_profile", false, of);
                }
            }
            return;
        }
        if (this.mUserManager.hasUserRestriction("no_add_user", userHandle)) {
            this.mUserManager.setUserRestriction("no_add_user", false, userHandle);
        }
        if (this.mUserManager.hasUserRestriction("no_add_managed_profile", userHandle)) {
            this.mUserManager.setUserRestriction("no_add_managed_profile", false, userHandle);
        }
        if (this.mUserManager.hasUserRestriction("no_add_clone_profile", userHandle)) {
            this.mUserManager.setUserRestriction("no_add_clone_profile", false, userHandle);
        }
    }

    public final boolean isPackageTestOnly(String str, int i) {
        try {
            ApplicationInfo applicationInfo = this.mInjector.getIPackageManager().getApplicationInfo(str, 786432L, i);
            if (applicationInfo != null) {
                return (applicationInfo.flags & 256) != 0;
            }
            throw new IllegalStateException("Couldn't find package: " + str + " on user " + i);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public final boolean isAdminTestOnlyLocked(ComponentName componentName, int i) {
        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
        return activeAdminUncheckedLocked != null && activeAdminUncheckedLocked.testOnlyAdmin;
    }

    public void removeActiveAdmin(final ComponentName componentName, final int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS") ? getCallerIdentity() : getCallerIdentity(componentName), i));
            checkCanExecuteOrThrowUnsafe(27);
            enforceUserUnlocked(i);
            synchronized (getLockObject()) {
                if (getActiveAdminUncheckedLocked(componentName, i) == null) {
                    return;
                }
                if (!isDeviceOwner(componentName, i) && !isProfileOwner(componentName, i)) {
                    this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda8
                        public final void runOrThrow() {
                            DevicePolicyManagerService.this.lambda$removeActiveAdmin$13(componentName, i);
                        }
                    });
                    return;
                }
                Slogf.m26e("DevicePolicyManager", "Device/profile owner cannot be removed: component=" + componentName);
            }
        }
    }

    public final boolean canSetPasswordQualityOnParent(String str, CallerIdentity callerIdentity) {
        return !this.mInjector.isChangeEnabled(165573442L, str, callerIdentity.getUserId()) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity);
    }

    public final boolean isPasswordLimitingAdminTargetingP(CallerIdentity callerIdentity) {
        boolean z;
        if (callerIdentity.hasAdminComponent()) {
            synchronized (getLockObject()) {
                z = getActiveAdminWithPolicyForUidLocked(callerIdentity.getComponentName(), 0, callerIdentity.getUid()) != null;
            }
            return z;
        }
        return false;
    }

    public final boolean notSupportedOnAutomotive(String str) {
        if (this.mIsAutomotive) {
            Slogf.m20i("DevicePolicyManager", "%s is not supported on automotive builds", str);
            return true;
        }
        return false;
    }

    public void setPasswordQuality(final ComponentName componentName, final int i, final boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordQuality")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        validateQualityConstant(i);
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isSystemUid(callerIdentity) || isPasswordLimitingAdminTargetingP(callerIdentity));
        if (z) {
            Preconditions.checkCallAuthorization(canSetPasswordQualityOnParent(componentName.getPackageName(), callerIdentity), "Profile Owner may not apply password quality requirements device-wide");
        }
        final int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            final ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            if (z) {
                Preconditions.checkState(!(getActiveAdminForCallerLocked(componentName, 0, false).mPasswordComplexity != 0), "Cannot set password quality when complexity is set on the primary admin. Set the primary admin's complexity to NONE first.");
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda37
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setPasswordQuality$14(activeAdminForCallerLocked, i, userId, z, componentName);
                }
            });
        }
        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(1).setAdmin(componentName).setInt(i);
        String[] strArr = new String[1];
        strArr[0] = z ? "calledFromParent" : "notCalledFromParent";
        devicePolicyEventLogger.setStrings(strArr).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setPasswordQuality$14(ActiveAdmin activeAdmin, int i, int i2, boolean z, ComponentName componentName) throws Exception {
        PasswordPolicy passwordPolicy = activeAdmin.mPasswordPolicy;
        if (passwordPolicy.quality != i) {
            passwordPolicy.quality = i;
            activeAdmin.mPasswordComplexity = 0;
            resetInactivePasswordRequirementsIfRPlus(i2, activeAdmin);
            updatePasswordValidityCheckpointLocked(i2, z);
            updatePasswordQualityCacheForUserGroup(i2);
            saveSettingsLocked(i2);
        }
        logPasswordQualitySetIfSecurityLogEnabled(componentName, i2, z, passwordPolicy);
    }

    public final boolean passwordQualityInvocationOrderCheckEnabled(String str, int i) {
        return this.mInjector.isChangeEnabled(123562444L, str, i);
    }

    public final void resetInactivePasswordRequirementsIfRPlus(int i, ActiveAdmin activeAdmin) {
        if (passwordQualityInvocationOrderCheckEnabled(activeAdmin.info.getPackageName(), i)) {
            PasswordPolicy passwordPolicy = activeAdmin.mPasswordPolicy;
            int i2 = passwordPolicy.quality;
            if (i2 < 131072) {
                passwordPolicy.length = 0;
            }
            if (i2 < 393216) {
                passwordPolicy.letters = 1;
                passwordPolicy.upperCase = 0;
                passwordPolicy.lowerCase = 0;
                passwordPolicy.numeric = 1;
                passwordPolicy.symbols = 1;
                passwordPolicy.nonLetter = 0;
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    public final Set<Integer> updatePasswordValidityCheckpointLocked(int i, boolean z) {
        boolean isPasswordSufficientForUserWithoutCheckpointLocked;
        ArraySet arraySet = new ArraySet();
        int credentialOwner = getCredentialOwner(i, z);
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(credentialOwner);
        PasswordMetrics userPasswordMetrics = this.mLockSettingsInternal.getUserPasswordMetrics(credentialOwner);
        if (userPasswordMetrics != null && (isPasswordSufficientForUserWithoutCheckpointLocked = isPasswordSufficientForUserWithoutCheckpointLocked(userPasswordMetrics, getProfileParentUserIfRequested(i, z))) != lambda$getUserDataUnchecked$2.mPasswordValidAtLastCheckpoint) {
            lambda$getUserDataUnchecked$2.mPasswordValidAtLastCheckpoint = isPasswordSufficientForUserWithoutCheckpointLocked;
            arraySet.add(Integer.valueOf(credentialOwner));
        }
        return arraySet;
    }

    public final void updatePasswordQualityCacheForUserGroup(int i) {
        List<UserInfo> profiles;
        if (i == -1) {
            profiles = this.mUserManager.getUsers();
        } else {
            profiles = this.mUserManager.getProfiles(i);
        }
        for (UserInfo userInfo : profiles) {
            int i2 = userInfo.id;
            this.mPolicyCache.setPasswordQuality(i2, getPasswordQuality(null, i2, false));
        }
    }

    public int getPasswordQuality(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            Preconditions.checkCallAuthorization(componentName == null || isCallingFromPackage(componentName.getPackageName(), callerIdentity.getUid()) || canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.mPasswordPolicy.quality : 0;
                }
                List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                int size = activeAdminsForLockscreenPoliciesLocked.size();
                int i2 = 0;
                while (r1 < size) {
                    int i3 = activeAdminsForLockscreenPoliciesLocked.get(r1).mPasswordPolicy.quality;
                    if (i2 < i3) {
                        i2 = i3;
                    }
                    r1++;
                }
                return i2;
            }
        }
        return 0;
    }

    @GuardedBy({"getLockObject()"})
    public final List<ActiveAdmin> getActiveAdminsForLockscreenPoliciesLocked(int i) {
        if (isSeparateProfileChallengeEnabled(i)) {
            if (isPermissionCheckFlagEnabled()) {
                return getActiveAdminsForAffectedUserInclPermissionBasedAdminLocked(i);
            }
            return getUserDataUnchecked(i).mAdminList;
        } else if (isPermissionCheckFlagEnabled()) {
            return m61x6f6a5307(getProfileParentId(i), new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda151
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getActiveAdminsForLockscreenPoliciesLocked$15;
                    lambda$getActiveAdminsForLockscreenPoliciesLocked$15 = DevicePolicyManagerService.this.lambda$getActiveAdminsForLockscreenPoliciesLocked$15((UserInfo) obj);
                    return lambda$getActiveAdminsForLockscreenPoliciesLocked$15;
                }
            });
        } else {
            return getActiveAdminsForUserAndItsManagedProfilesLocked(getProfileParentId(i), new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda152
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getActiveAdminsForLockscreenPoliciesLocked$16;
                    lambda$getActiveAdminsForLockscreenPoliciesLocked$16 = DevicePolicyManagerService.this.lambda$getActiveAdminsForLockscreenPoliciesLocked$16((UserInfo) obj);
                    return lambda$getActiveAdminsForLockscreenPoliciesLocked$16;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getActiveAdminsForLockscreenPoliciesLocked$15(UserInfo userInfo) {
        return !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userInfo.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getActiveAdminsForLockscreenPoliciesLocked$16(UserInfo userInfo) {
        return !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userInfo.id);
    }

    @GuardedBy({"getLockObject()"})
    public final List<ActiveAdmin> getActiveAdminsForAffectedUserLocked(int i) {
        if (isManagedProfile(i)) {
            return getUserDataUnchecked(i).mAdminList;
        }
        return getActiveAdminsForUserAndItsManagedProfilesLocked(i, new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda38
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getActiveAdminsForAffectedUserLocked$17;
                lambda$getActiveAdminsForAffectedUserLocked$17 = DevicePolicyManagerService.lambda$getActiveAdminsForAffectedUserLocked$17((UserInfo) obj);
                return lambda$getActiveAdminsForAffectedUserLocked$17;
            }
        });
    }

    @GuardedBy({"getLockObject()"})
    public final List<ActiveAdmin> getActiveAdminsForAffectedUserInclPermissionBasedAdminLocked(int i) {
        if (isManagedProfile(i)) {
            ArrayList<ActiveAdmin> arrayList = getUserDataUnchecked(i).mAdminList;
        }
        List<ActiveAdmin> m61x6f6a5307 = m61x6f6a5307(i, new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda98
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m58xb259fe83;
                m58xb259fe83 = DevicePolicyManagerService.m58xb259fe83((UserInfo) obj);
                return m58xb259fe83;
            }
        });
        if (lambda$getUserDataUnchecked$2(i).mPermissionBasedAdmin != null) {
            m61x6f6a5307.add(lambda$getUserDataUnchecked$2(i).mPermissionBasedAdmin);
        }
        return m61x6f6a5307;
    }

    @GuardedBy({"getLockObject()"})
    public final List<ActiveAdmin> getActiveAdminsForUserAndItsManagedProfilesLocked(final int i, final Predicate<UserInfo> predicate) {
        final ArrayList arrayList = new ArrayList();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda182
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$getActiveAdminsForUserAndItsManagedProfilesLocked$19(i, arrayList, predicate);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getActiveAdminsForUserAndItsManagedProfilesLocked$19(int i, ArrayList arrayList, Predicate predicate) throws Exception {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            DevicePolicyData userDataUnchecked = getUserDataUnchecked(userInfo.id);
            if (userInfo.id == i) {
                arrayList.addAll(userDataUnchecked.mAdminList);
            } else if (userInfo.isManagedProfile()) {
                for (int i2 = 0; i2 < userDataUnchecked.mAdminList.size(); i2++) {
                    ActiveAdmin activeAdmin = userDataUnchecked.mAdminList.get(i2);
                    if (activeAdmin.hasParentActiveAdmin()) {
                        arrayList.add(activeAdmin.getParentActiveAdmin());
                    }
                    if (predicate.test(userInfo)) {
                        arrayList.add(activeAdmin);
                    }
                }
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    /* renamed from: getActiveAdminsForUserAndItsManagedProfilesInclPermissionBasedAdminLocked */
    public final List<ActiveAdmin> m61x6f6a5307(final int i, final Predicate<UserInfo> predicate) {
        final ArrayList arrayList = new ArrayList();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda183
            public final void runOrThrow() {
                DevicePolicyManagerService.this.m57xcce652b8(i, arrayList, predicate);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$getActiveAdminsForUserAndItsManagedProfilesInclPermissionBasedAdminLocked$20 */
    public /* synthetic */ void m57xcce652b8(int i, ArrayList arrayList, Predicate predicate) throws Exception {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            DevicePolicyData userDataUnchecked = getUserDataUnchecked(userInfo.id);
            if (userInfo.id == i) {
                arrayList.addAll(userDataUnchecked.mAdminList);
                ActiveAdmin activeAdmin = userDataUnchecked.mPermissionBasedAdmin;
                if (activeAdmin != null) {
                    arrayList.add(activeAdmin);
                }
            } else if (userInfo.isManagedProfile()) {
                for (int i2 = 0; i2 < userDataUnchecked.mAdminList.size(); i2++) {
                    ActiveAdmin activeAdmin2 = userDataUnchecked.mAdminList.get(i2);
                    if (activeAdmin2.hasParentActiveAdmin()) {
                        arrayList.add(activeAdmin2.getParentActiveAdmin());
                    }
                    if (predicate.test(userInfo)) {
                        arrayList.add(activeAdmin2);
                    }
                }
                if (userDataUnchecked.mPermissionBasedAdmin != null && predicate.test(userInfo)) {
                    arrayList.add(userDataUnchecked.mPermissionBasedAdmin);
                }
            }
        }
    }

    public final boolean isSeparateProfileChallengeEnabled(final int i) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda71
            public final Object getOrThrow() {
                Boolean lambda$isSeparateProfileChallengeEnabled$21;
                lambda$isSeparateProfileChallengeEnabled$21 = DevicePolicyManagerService.this.lambda$isSeparateProfileChallengeEnabled$21(i);
                return lambda$isSeparateProfileChallengeEnabled$21;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isSeparateProfileChallengeEnabled$21(int i) throws Exception {
        return Boolean.valueOf(this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i));
    }

    public void setPasswordMinimumLength(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumLength")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, "setPasswordMinimumLength");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.length != i) {
                passwordPolicy.length = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(2).setAdmin(componentName).setInt(i).write();
    }

    public final void ensureMinimumQuality(final int i, final ActiveAdmin activeAdmin, final int i2, final String str) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda119
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$ensureMinimumQuality$22(activeAdmin, i2, i, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$ensureMinimumQuality$22(ActiveAdmin activeAdmin, int i, int i2, String str) throws Exception {
        if (activeAdmin.mPasswordPolicy.quality < i && passwordQualityInvocationOrderCheckEnabled(activeAdmin.info.getPackageName(), i2)) {
            throw new IllegalStateException(String.format("password quality should be at least %d for %s", Integer.valueOf(i), str));
        }
    }

    public int getPasswordMinimumLength(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda102
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumLength$23;
                lambda$getPasswordMinimumLength$23 = DevicePolicyManagerService.lambda$getPasswordMinimumLength$23((ActiveAdmin) obj);
                return lambda$getPasswordMinimumLength$23;
            }
        }, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumLength$23(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.length);
    }

    public void setPasswordHistoryLength(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
                if (activeAdminForCallerLocked.passwordHistoryLength != i) {
                    activeAdminForCallerLocked.passwordHistoryLength = i;
                    updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                    saveSettingsLocked(userHandleGetCallingUserId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210018, new Object[]{componentName.getPackageName(), Integer.valueOf(userHandleGetCallingUserId), Integer.valueOf(z ? getProfileParentId(userHandleGetCallingUserId) : userHandleGetCallingUserId), Integer.valueOf(i)});
            }
        }
    }

    public int getPasswordHistoryLength(ComponentName componentName, int i, boolean z) {
        if (this.mLockPatternUtils.hasSecureLockScreen()) {
            return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda143
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$getPasswordHistoryLength$24;
                    lambda$getPasswordHistoryLength$24 = DevicePolicyManagerService.lambda$getPasswordHistoryLength$24((ActiveAdmin) obj);
                    return lambda$getPasswordHistoryLength$24;
                }
            }, 0);
        }
        return 0;
    }

    public static /* synthetic */ Integer lambda$getPasswordHistoryLength$24(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.passwordHistoryLength);
    }

    public void setPasswordExpirationTimeout(ComponentName componentName, String str, long j, boolean z) {
        ActiveAdmin activeAdminForCallerLocked;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (!isPermissionCheckFlagEnabled()) {
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            Preconditions.checkArgumentNonnegative(j, "Timeout must be >= 0 ms");
            int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
            int profileParentId = z ? getProfileParentId(userHandleGetCallingUserId) : userHandleGetCallingUserId;
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    activeAdminForCallerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", getCallerIdentity(componentName, str).getPackageName(), profileParentId).getActiveAdmin();
                } else {
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 6, z);
                }
                int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
                long currentTimeMillis = i > 0 ? System.currentTimeMillis() + j : 0L;
                activeAdminForCallerLocked.passwordExpirationDate = currentTimeMillis;
                activeAdminForCallerLocked.passwordExpirationTimeout = j;
                if (i > 0) {
                    Slogf.m14w("DevicePolicyManager", "setPasswordExpiration(): password will expire on " + DateFormat.getDateTimeInstance(2, 2).format(new Date(currentTimeMillis)));
                }
                saveSettingsLocked(userHandleGetCallingUserId);
                setExpirationAlarmCheckLocked(this.mContext, userHandleGetCallingUserId, z);
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210016, new Object[]{str, Integer.valueOf(userHandleGetCallingUserId), Integer.valueOf(profileParentId), Long.valueOf(j)});
            }
        }
    }

    public long getPasswordExpirationTimeout(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            synchronized (getLockObject()) {
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.passwordExpirationTimeout : 0L;
                }
                List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                int size = activeAdminsForLockscreenPoliciesLocked.size();
                long j = 0;
                for (int i2 = 0; i2 < size; i2++) {
                    ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i2);
                    if (j != 0) {
                        long j2 = activeAdmin.passwordExpirationTimeout;
                        if (j2 != 0) {
                            if (j <= j2) {
                            }
                        }
                    }
                    j = activeAdmin.passwordExpirationTimeout;
                }
                return j;
            }
        }
        return 0L;
    }

    public boolean addCrossProfileWidgetProvider(ComponentName componentName, String str, String str2) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerLocked;
        ActiveAdmin activeAdmin;
        ArrayList arrayList;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            }
            activeAdmin = profileOwnerLocked;
        }
        synchronized (getLockObject()) {
            if (activeAdmin.crossProfileWidgetProviders == null) {
                activeAdmin.crossProfileWidgetProviders = new ArrayList();
            }
            List<String> list = activeAdmin.crossProfileWidgetProviders;
            if (list.contains(str2)) {
                arrayList = null;
            } else {
                list.add(str2);
                arrayList = new ArrayList(list);
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
        DevicePolicyEventLogger.createEvent(49).setAdmin(callerIdentity.getPackageName()).write();
        if (arrayList != null) {
            this.mLocalService.notifyCrossProfileProvidersChanged(callerIdentity.getUserId(), arrayList);
            return true;
        }
        return false;
    }

    public boolean removeCrossProfileWidgetProvider(ComponentName componentName, String str, String str2) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerLocked;
        ActiveAdmin activeAdmin;
        ArrayList arrayList;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            }
            activeAdmin = profileOwnerLocked;
        }
        synchronized (getLockObject()) {
            List<String> list = activeAdmin.crossProfileWidgetProviders;
            if (list != null && !list.isEmpty()) {
                List<String> list2 = activeAdmin.crossProfileWidgetProviders;
                if (list2.remove(str2)) {
                    arrayList = new ArrayList(list2);
                    saveSettingsLocked(callerIdentity.getUserId());
                } else {
                    arrayList = null;
                }
                DevicePolicyEventLogger.createEvent(117).setAdmin(callerIdentity.getPackageName()).write();
                if (arrayList != null) {
                    this.mLocalService.notifyCrossProfileProvidersChanged(callerIdentity.getUserId(), arrayList);
                    return true;
                }
                return false;
            }
            return false;
        }
    }

    public List<String> getCrossProfileWidgetProviders(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerLocked;
        ActiveAdmin activeAdmin;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            activeAdmin = enforceCanQueryAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            }
            activeAdmin = profileOwnerLocked;
        }
        synchronized (getLockObject()) {
            List<String> list = activeAdmin.crossProfileWidgetProviders;
            if (list != null && !list.isEmpty()) {
                if (this.mInjector.binderIsCallingUidMyUid()) {
                    return new ArrayList(activeAdmin.crossProfileWidgetProviders);
                }
                return activeAdmin.crossProfileWidgetProviders;
            }
            return null;
        }
    }

    @GuardedBy({"getLockObject()"})
    public final long getPasswordExpirationLocked(ComponentName componentName, int i, boolean z) {
        if (componentName != null) {
            ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
            if (activeAdminUncheckedLocked != null) {
                return activeAdminUncheckedLocked.passwordExpirationDate;
            }
            return 0L;
        }
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
        int size = activeAdminsForLockscreenPoliciesLocked.size();
        long j = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i2);
            if (j != 0) {
                long j2 = activeAdmin.passwordExpirationDate;
                if (j2 != 0) {
                    if (j <= j2) {
                    }
                }
            }
            j = activeAdmin.passwordExpirationDate;
        }
        return j;
    }

    public long getPasswordExpiration(ComponentName componentName, int i, boolean z) {
        long passwordExpirationLocked;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            synchronized (getLockObject()) {
                passwordExpirationLocked = getPasswordExpirationLocked(componentName, i, z);
            }
            return passwordExpirationLocked;
        }
        return 0L;
    }

    public void setPasswordMinimumUpperCase(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumUpperCase")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumUpperCase");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.upperCase != i) {
                passwordPolicy.upperCase = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(7).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumUpperCase(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda87
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumUpperCase$25;
                lambda$getPasswordMinimumUpperCase$25 = DevicePolicyManagerService.lambda$getPasswordMinimumUpperCase$25((ActiveAdmin) obj);
                return lambda$getPasswordMinimumUpperCase$25;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumUpperCase$25(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.upperCase);
    }

    public void setPasswordMinimumLowerCase(ComponentName componentName, int i, boolean z) {
        if (notSupportedOnAutomotive("setPasswordMinimumLowerCase")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumLowerCase");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.lowerCase != i) {
                passwordPolicy.lowerCase = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(6).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumLowerCase(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumLowerCase$26;
                lambda$getPasswordMinimumLowerCase$26 = DevicePolicyManagerService.lambda$getPasswordMinimumLowerCase$26((ActiveAdmin) obj);
                return lambda$getPasswordMinimumLowerCase$26;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumLowerCase$26(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.lowerCase);
    }

    public void setPasswordMinimumLetters(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumLetters")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumLetters");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.letters != i) {
                passwordPolicy.letters = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(5).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumLetters(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda94
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumLetters$27;
                lambda$getPasswordMinimumLetters$27 = DevicePolicyManagerService.lambda$getPasswordMinimumLetters$27((ActiveAdmin) obj);
                return lambda$getPasswordMinimumLetters$27;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumLetters$27(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.letters);
    }

    public void setPasswordMinimumNumeric(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumNumeric")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumNumeric");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.numeric != i) {
                passwordPolicy.numeric = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(3).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumNumeric(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda158
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumNumeric$28;
                lambda$getPasswordMinimumNumeric$28 = DevicePolicyManagerService.lambda$getPasswordMinimumNumeric$28((ActiveAdmin) obj);
                return lambda$getPasswordMinimumNumeric$28;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumNumeric$28(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.numeric);
    }

    public void setPasswordMinimumSymbols(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumSymbols")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumSymbols");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.symbols != i) {
                passwordPolicy.symbols = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(8).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumSymbols(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda163
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumSymbols$29;
                lambda$getPasswordMinimumSymbols$29 = DevicePolicyManagerService.lambda$getPasswordMinimumSymbols$29((ActiveAdmin) obj);
                return lambda$getPasswordMinimumSymbols$29;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumSymbols$29(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.symbols);
    }

    public void setPasswordMinimumNonLetter(ComponentName componentName, int i, boolean z) {
        if (!this.mHasFeature || notSupportedOnAutomotive("setPasswordMinimumNonLetter")) {
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 0, z);
            ensureMinimumQuality(userHandleGetCallingUserId, activeAdminForCallerLocked, 393216, "setPasswordMinimumNonLetter");
            PasswordPolicy passwordPolicy = activeAdminForCallerLocked.mPasswordPolicy;
            if (passwordPolicy.nonLetter != i) {
                passwordPolicy.nonLetter = i;
                updatePasswordValidityCheckpointLocked(userHandleGetCallingUserId, z);
                saveSettingsLocked(userHandleGetCallingUserId);
            }
            logPasswordQualitySetIfSecurityLogEnabled(componentName, userHandleGetCallingUserId, z, passwordPolicy);
        }
        DevicePolicyEventLogger.createEvent(4).setAdmin(componentName).setInt(i).write();
    }

    public int getPasswordMinimumNonLetter(ComponentName componentName, int i, boolean z) {
        return getStrictestPasswordRequirement(componentName, i, z, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda171
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPasswordMinimumNonLetter$30;
                lambda$getPasswordMinimumNonLetter$30 = DevicePolicyManagerService.lambda$getPasswordMinimumNonLetter$30((ActiveAdmin) obj);
                return lambda$getPasswordMinimumNonLetter$30;
            }
        }, 393216);
    }

    public static /* synthetic */ Integer lambda$getPasswordMinimumNonLetter$30(ActiveAdmin activeAdmin) {
        return Integer.valueOf(activeAdmin.mPasswordPolicy.nonLetter);
    }

    public final int getStrictestPasswordRequirement(ComponentName componentName, int i, boolean z, Function<ActiveAdmin, Integer> function, int i2) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            synchronized (getLockObject()) {
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    return activeAdminUncheckedLocked != null ? function.apply(activeAdminUncheckedLocked).intValue() : 0;
                }
                List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                int size = activeAdminsForLockscreenPoliciesLocked.size();
                int i3 = 0;
                while (r1 < size) {
                    ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(r1);
                    if (isLimitPasswordAllowed(activeAdmin, i2)) {
                        Integer apply = function.apply(activeAdmin);
                        if (apply.intValue() > i3) {
                            i3 = apply.intValue();
                        }
                    }
                    r1++;
                }
                return i3;
            }
        }
        return 0;
    }

    public PasswordMetrics getPasswordMinimumMetrics(int i, boolean z) {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i) && (isSystemUid(callerIdentity) || hasCallingOrSelfPermission("android.permission.SET_INITIAL_LOCK")));
        return getPasswordMinimumMetricsUnchecked(i, z);
    }

    public final PasswordMetrics getPasswordMinimumMetricsUnchecked(int i) {
        return getPasswordMinimumMetricsUnchecked(i, false);
    }

    public final PasswordMetrics getPasswordMinimumMetricsUnchecked(int i, boolean z) {
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked;
        if (!this.mHasFeature) {
            new PasswordMetrics(-1);
        }
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        if (z) {
            Preconditions.checkArgument(!isManagedProfile(i));
        }
        ArrayList arrayList = new ArrayList();
        synchronized (getLockObject()) {
            if (z) {
                activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForUserAndItsManagedProfilesLocked(i, new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getPasswordMinimumMetricsUnchecked$31;
                        lambda$getPasswordMinimumMetricsUnchecked$31 = DevicePolicyManagerService.lambda$getPasswordMinimumMetricsUnchecked$31((UserInfo) obj);
                        return lambda$getPasswordMinimumMetricsUnchecked$31;
                    }
                });
            } else {
                activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(i);
            }
            for (ActiveAdmin activeAdmin : activeAdminsForLockscreenPoliciesLocked) {
                arrayList.add(activeAdmin.mPasswordPolicy.getMinMetrics());
            }
        }
        return PasswordMetrics.merge(arrayList);
    }

    public boolean isActivePasswordSufficient(String str, int i, boolean z) {
        boolean isActivePasswordSufficientForUserLocked;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            enforceUserUnlocked(i, z);
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    enforcePermission("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", str, z ? getProfileParentId(i) : i);
                } else {
                    getActiveAdminForCallerLocked(null, 0, z);
                }
                int credentialOwner = getCredentialOwner(i, z);
                isActivePasswordSufficientForUserLocked = isActivePasswordSufficientForUserLocked(getUserDataUnchecked(credentialOwner).mPasswordValidAtLastCheckpoint, this.mLockSettingsInternal.getUserPasswordMetrics(credentialOwner), getProfileParentUserIfRequested(i, z));
            }
            return isActivePasswordSufficientForUserLocked;
        }
        return true;
    }

    public boolean isActivePasswordSufficientForDeviceRequirement() {
        boolean isEmpty;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            int userId = callerIdentity.getUserId();
            Preconditions.checkCallingUser(isManagedProfile(userId));
            int profileParentId = getProfileParentId(userId);
            enforceUserUnlocked(profileParentId);
            synchronized (getLockObject()) {
                isEmpty = PasswordMetrics.validatePasswordMetrics(getPasswordMinimumMetricsUnchecked(profileParentId, true), getAggregatedPasswordComplexityLocked(profileParentId, true), this.mLockSettingsInternal.getUserPasswordMetrics(profileParentId)).isEmpty();
            }
            DevicePolicyEventLogger.createEvent(189).setStrings(new String[]{this.mOwners.getProfileOwnerComponent(callerIdentity.getUserId()).getPackageName()}).write();
            return isEmpty;
        }
        return true;
    }

    public boolean isUsingUnifiedPassword(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            Preconditions.checkCallingUser(isManagedProfile(callerIdentity.getUserId()));
            return !isSeparateProfileChallengeEnabled(callerIdentity.getUserId());
        }
        return true;
    }

    public boolean isPasswordSufficientAfterProfileUnification(int i, final int i2) {
        boolean isEmpty;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            Preconditions.checkCallAuthorization(!isManagedProfile(i), "You can not check password sufficiency for a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
            enforceUserUnlocked(i);
            synchronized (getLockObject()) {
                PasswordMetrics userPasswordMetrics = this.mLockSettingsInternal.getUserPasswordMetrics(i);
                List<ActiveAdmin> activeAdminsForUserAndItsManagedProfilesLocked = getActiveAdminsForUserAndItsManagedProfilesLocked(i, new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda162
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$isPasswordSufficientAfterProfileUnification$32;
                        lambda$isPasswordSufficientAfterProfileUnification$32 = DevicePolicyManagerService.this.lambda$isPasswordSufficientAfterProfileUnification$32(i2, (UserInfo) obj);
                        return lambda$isPasswordSufficientAfterProfileUnification$32;
                    }
                });
                ArrayList arrayList = new ArrayList(activeAdminsForUserAndItsManagedProfilesLocked.size());
                int i3 = 0;
                for (ActiveAdmin activeAdmin : activeAdminsForUserAndItsManagedProfilesLocked) {
                    arrayList.add(activeAdmin.mPasswordPolicy.getMinMetrics());
                    i3 = Math.max(i3, activeAdmin.mPasswordComplexity);
                }
                isEmpty = PasswordMetrics.validatePasswordMetrics(PasswordMetrics.merge(arrayList), i3, userPasswordMetrics).isEmpty();
            }
            return isEmpty;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isPasswordSufficientAfterProfileUnification$32(int i, UserInfo userInfo) {
        int i2 = userInfo.id;
        return i2 == i || !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i2);
    }

    public final boolean isActivePasswordSufficientForUserLocked(boolean z, PasswordMetrics passwordMetrics, int i) {
        if (this.mInjector.storageManagerIsFileBasedEncryptionEnabled() || passwordMetrics != null) {
            if (passwordMetrics == null) {
                throw new IllegalStateException("isActivePasswordSufficient called on FBE-locked user");
            }
            return isPasswordSufficientForUserWithoutCheckpointLocked(passwordMetrics, i);
        }
        return z;
    }

    public final boolean isPasswordSufficientForUserWithoutCheckpointLocked(PasswordMetrics passwordMetrics, int i) {
        return PasswordMetrics.validatePasswordMetrics(getPasswordMinimumMetricsUnchecked(i), getAggregatedPasswordComplexityLocked(i), passwordMetrics).isEmpty();
    }

    public int getPasswordComplexity(boolean z) {
        CallerIdentity callerIdentity = getCallerIdentity();
        DevicePolicyEventLogger.createEvent(72).setStrings(z ? "calledFromParent" : "notCalledFromParent", this.mInjector.getPackageManager().getPackagesForUid(callerIdentity.getUid())).write();
        enforceUserUnlocked(callerIdentity.getUserId());
        boolean z2 = true;
        int i = 0;
        if (z) {
            if (!isDefaultDeviceOwner(callerIdentity) && !isProfileOwner(callerIdentity) && !isSystemUid(callerIdentity)) {
                z2 = false;
            }
            Preconditions.checkCallAuthorization(z2, "Only profile owner, device owner and system may call this method on parent.");
        } else if (isPermissionCheckFlagEnabled()) {
            if (!hasCallingOrSelfPermission("android.permission.REQUEST_PASSWORD_COMPLEXITY") && !hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS") && !isDefaultDeviceOwner(callerIdentity) && !isProfileOwner(callerIdentity)) {
                z2 = false;
            }
            Preconditions.checkCallAuthorization(z2, "Must have android.permission.REQUEST_PASSWORD_COMPLEXITY or android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS permissions, or be a profile owner or device owner.");
        } else {
            if (!hasCallingOrSelfPermission("android.permission.REQUEST_PASSWORD_COMPLEXITY") && !isDefaultDeviceOwner(callerIdentity) && !isProfileOwner(callerIdentity)) {
                z2 = false;
            }
            Preconditions.checkCallAuthorization(z2, "Must have android.permission.REQUEST_PASSWORD_COMPLEXITY permission, or be a profile owner or device owner.");
        }
        synchronized (getLockObject()) {
            PasswordMetrics userPasswordMetrics = this.mLockSettingsInternal.getUserPasswordMetrics(getCredentialOwner(callerIdentity.getUserId(), z));
            if (userPasswordMetrics != null) {
                i = userPasswordMetrics.determineComplexity();
            }
        }
        return i;
    }

    public void setRequiredPasswordComplexity(String str, final int i, final boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin parentOfAdminIfRequired;
        if (this.mHasFeature) {
            Preconditions.checkArgument(Set.of(0, 65536, 196608, 327680).contains(Integer.valueOf(i)), "Provided complexity is not one of the allowed values.");
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(str);
            } else {
                callerIdentity = getCallerIdentity();
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                Preconditions.checkArgument(!z || isProfileOwner(callerIdentity));
            }
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(null, "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", callerIdentity.getPackageName(), z ? getProfileParentId(callerIdentity.getUserId()) : callerIdentity.getUserId()).getActiveAdmin();
                } else {
                    parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z);
                }
                final ActiveAdmin activeAdmin = parentOfAdminIfRequired;
                if (activeAdmin.mPasswordComplexity != i) {
                    if (!z) {
                        Preconditions.checkState(activeAdmin.hasParentActiveAdmin() && activeAdmin.getParentActiveAdmin().mPasswordPolicy.quality != 0 ? false : true, "Password quality is set on the parent when attempting to set passwordcomplexity. Clear the quality by setting the password quality on the parent to PASSWORD_QUALITY_UNSPECIFIED first");
                    }
                    final CallerIdentity callerIdentity2 = callerIdentity;
                    this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda69
                        public final void runOrThrow() {
                            DevicePolicyManagerService.this.lambda$setRequiredPasswordComplexity$33(activeAdmin, i, callerIdentity2, z);
                        }
                    });
                    DevicePolicyEventLogger.createEvent(177).setAdmin(callerIdentity.getPackageName()).setInt(i).setBoolean(z).write();
                }
                logPasswordComplexityRequiredIfSecurityLogEnabled(callerIdentity.getPackageName(), callerIdentity.getUserId(), z, i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setRequiredPasswordComplexity$33(ActiveAdmin activeAdmin, int i, CallerIdentity callerIdentity, boolean z) throws Exception {
        activeAdmin.mPasswordComplexity = i;
        activeAdmin.mPasswordPolicy = new PasswordPolicy();
        updatePasswordValidityCheckpointLocked(callerIdentity.getUserId(), z);
        updatePasswordQualityCacheForUserGroup(callerIdentity.getUserId());
        saveSettingsLocked(callerIdentity.getUserId());
    }

    public final void logPasswordComplexityRequiredIfSecurityLogEnabled(String str, int i, boolean z, int i2) {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210035, new Object[]{str, Integer.valueOf(i), Integer.valueOf(z ? getProfileParentId(i) : i), Integer.valueOf(i2)});
        }
    }

    @GuardedBy({"getLockObject()"})
    public final int getAggregatedPasswordComplexityLocked(int i) {
        return getAggregatedPasswordComplexityLocked(i, false);
    }

    @GuardedBy({"getLockObject()"})
    public final int getAggregatedPasswordComplexityLocked(int i, boolean z) {
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked;
        ensureLocked();
        if (z) {
            activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForUserAndItsManagedProfilesLocked(i, new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda160
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getAggregatedPasswordComplexityLocked$34;
                    lambda$getAggregatedPasswordComplexityLocked$34 = DevicePolicyManagerService.lambda$getAggregatedPasswordComplexityLocked$34((UserInfo) obj);
                    return lambda$getAggregatedPasswordComplexityLocked$34;
                }
            });
        } else {
            activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(i);
        }
        int i2 = 0;
        for (ActiveAdmin activeAdmin : activeAdminsForLockscreenPoliciesLocked) {
            i2 = Math.max(i2, activeAdmin.mPasswordComplexity);
        }
        return i2;
    }

    public int getRequiredPasswordComplexity(String str, boolean z) {
        int i;
        int userId;
        boolean z2 = false;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            if (isPermissionCheckFlagEnabled()) {
                if (z) {
                    userId = getProfileParentId(callerIdentity.getUserId());
                } else {
                    userId = callerIdentity.getUserId();
                }
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", str, userId);
            } else {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                if (!z || isProfileOwner(callerIdentity)) {
                    z2 = true;
                }
                Preconditions.checkArgument(z2);
            }
            synchronized (getLockObject()) {
                i = getParentOfAdminIfRequired(getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId()), z).mPasswordComplexity;
            }
            return i;
        }
        return 0;
    }

    public int getAggregatedPasswordComplexityForUser(int i, boolean z) {
        int aggregatedPasswordComplexityLocked;
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            synchronized (getLockObject()) {
                aggregatedPasswordComplexityLocked = getAggregatedPasswordComplexityLocked(i, z);
            }
            return aggregatedPasswordComplexityLocked;
        }
        return 0;
    }

    public int getCurrentFailedPasswordAttempts(String str, int i, boolean z) {
        int i2;
        if (this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            synchronized (getLockObject()) {
                if (!isSystemUid(callerIdentity) && !hasCallingPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE")) {
                    if (isPermissionCheckFlagEnabled()) {
                        enforcePermission("android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", str, z ? getProfileParentId(i) : i);
                    } else {
                        getActiveAdminForCallerLocked(null, 1, z);
                    }
                }
                i2 = getUserDataUnchecked(getCredentialOwner(i, z)).mFailedPasswordAttempts;
            }
            return i2;
        }
        return 0;
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName componentName, String str, int i, boolean z) {
        ActiveAdmin activeAdminForCallerLocked;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (!isPermissionCheckFlagEnabled()) {
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
            int profileParentId = z ? getProfileParentId(userHandleGetCallingUserId) : userHandleGetCallingUserId;
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    activeAdminForCallerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", getCallerIdentity(componentName, str).getPackageName(), profileParentId).getActiveAdmin();
                } else {
                    getActiveAdminForCallerLocked(componentName, 4, z);
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 1, z);
                }
                if (activeAdminForCallerLocked.maximumFailedPasswordsForWipe != i) {
                    activeAdminForCallerLocked.maximumFailedPasswordsForWipe = i;
                    saveSettingsLocked(userHandleGetCallingUserId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210020, new Object[]{str, Integer.valueOf(userHandleGetCallingUserId), Integer.valueOf(profileParentId), Integer.valueOf(i)});
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName componentName, int i, boolean z) {
        ActiveAdmin adminWithMinimumFailedPasswordsForWipeLocked;
        int i2;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            Preconditions.checkCallAuthorization(componentName == null || isCallingFromPackage(componentName.getPackageName(), callerIdentity.getUid()) || canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                if (componentName != null) {
                    adminWithMinimumFailedPasswordsForWipeLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                } else {
                    adminWithMinimumFailedPasswordsForWipeLocked = getAdminWithMinimumFailedPasswordsForWipeLocked(i, z);
                }
                i2 = adminWithMinimumFailedPasswordsForWipeLocked != null ? adminWithMinimumFailedPasswordsForWipeLocked.maximumFailedPasswordsForWipe : 0;
            }
            return i2;
        }
        return 0;
    }

    public int getProfileWithMinimumFailedPasswordsForWipe(int i, boolean z) {
        int userIdToWipeForFailedPasswords;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            synchronized (getLockObject()) {
                ActiveAdmin adminWithMinimumFailedPasswordsForWipeLocked = getAdminWithMinimumFailedPasswordsForWipeLocked(i, z);
                userIdToWipeForFailedPasswords = adminWithMinimumFailedPasswordsForWipeLocked != null ? getUserIdToWipeForFailedPasswords(adminWithMinimumFailedPasswordsForWipeLocked) : -10000;
            }
            return userIdToWipeForFailedPasswords;
        }
        return -10000;
    }

    @GuardedBy({"getLockObject()"})
    public final ActiveAdmin getAdminWithMinimumFailedPasswordsForWipeLocked(int i, boolean z) {
        int i2;
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
        int size = activeAdminsForLockscreenPoliciesLocked.size();
        ActiveAdmin activeAdmin = null;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            ActiveAdmin activeAdmin2 = activeAdminsForLockscreenPoliciesLocked.get(i4);
            if (activeAdmin2.maximumFailedPasswordsForWipe != 0) {
                int userIdToWipeForFailedPasswords = getUserIdToWipeForFailedPasswords(activeAdmin2);
                if (i3 == 0 || i3 > (i2 = activeAdmin2.maximumFailedPasswordsForWipe) || (i3 == i2 && getUserInfo(userIdToWipeForFailedPasswords).isPrimary())) {
                    i3 = activeAdmin2.maximumFailedPasswordsForWipe;
                    activeAdmin = activeAdmin2;
                }
            }
        }
        return activeAdmin;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ UserInfo lambda$getUserInfo$35(int i) throws Exception {
        return this.mUserManager.getUserInfo(i);
    }

    public final UserInfo getUserInfo(final int i) {
        return (UserInfo) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda156
            public final Object getOrThrow() {
                UserInfo lambda$getUserInfo$35;
                lambda$getUserInfo$35 = DevicePolicyManagerService.this.lambda$getUserInfo$35(i);
                return lambda$getUserInfo$35;
            }
        });
    }

    public final boolean setPasswordPrivileged(String str, int i, CallerIdentity callerIdentity) {
        if (isLockScreenSecureUnchecked(callerIdentity.getUserId())) {
            throw new SecurityException("Cannot change current password");
        }
        return resetPasswordInternal(str, 0L, null, i, callerIdentity);
    }

    public boolean resetPassword(String str, int i) throws RemoteException {
        if (!this.mLockPatternUtils.hasSecureLockScreen()) {
            Slogf.m14w("DevicePolicyManager", "Cannot reset password when the device has no lock screen");
            return false;
        }
        if (str == null) {
            str = "";
        }
        CallerIdentity callerIdentity = getCallerIdentity();
        int userId = callerIdentity.getUserId();
        if (hasCallingPermission("android.permission.RESET_PASSWORD")) {
            boolean passwordPrivileged = setPasswordPrivileged(str, i, callerIdentity);
            if (passwordPrivileged) {
                DevicePolicyEventLogger.createEvent(205).write();
            }
            return passwordPrivileged;
        } else if (isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity)) {
            synchronized (getLockObject()) {
                if (getTargetSdk(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).info.getPackageName(), userId) < 26) {
                    Slogf.m26e("DevicePolicyManager", "DPC can no longer call resetPassword()");
                } else {
                    throw new SecurityException("Device admin can no longer call resetPassword()");
                }
            }
            return false;
        } else {
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(null, 2, false);
                Preconditions.checkCallAuthorization(activeAdminForCallerLocked != null, "Unauthorized caller cannot call resetPassword.");
                if (getTargetSdk(activeAdminForCallerLocked.info.getPackageName(), userId) <= 23) {
                    Slogf.m26e("DevicePolicyManager", "Device admin can no longer call resetPassword()");
                } else {
                    throw new SecurityException("Device admin can no longer call resetPassword()");
                }
            }
            return false;
        }
    }

    public final boolean resetPasswordInternal(String str, long j, byte[] bArr, int i, CallerIdentity callerIdentity) {
        int uid = callerIdentity.getUid();
        int userId = UserHandle.getUserId(uid);
        boolean isNumericOnly = PasswordMetrics.isNumericOnly(str);
        synchronized (getLockObject()) {
            PasswordMetrics passwordMinimumMetricsUnchecked = getPasswordMinimumMetricsUnchecked(userId);
            int aggregatedPasswordComplexityLocked = getAggregatedPasswordComplexityLocked(userId);
            List validatePasswordMetrics = str.isEmpty() ? PasswordMetrics.validatePasswordMetrics(passwordMinimumMetricsUnchecked, aggregatedPasswordComplexityLocked, new PasswordMetrics(-1)) : PasswordMetrics.validatePassword(passwordMinimumMetricsUnchecked, aggregatedPasswordComplexityLocked, isNumericOnly, str.getBytes());
            if (!validatePasswordMetrics.isEmpty()) {
                Slogf.m12w("DevicePolicyManager", "Failed to reset password due to constraint violation: %s", validatePasswordMetrics.get(0));
                return false;
            }
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
            int i2 = lambda$getUserDataUnchecked$2.mPasswordOwner;
            if (i2 >= 0 && i2 != uid) {
                Slogf.m14w("DevicePolicyManager", "resetPassword: already set by another uid and not entered by user");
                return false;
            }
            boolean isDefaultDeviceOwner = isDefaultDeviceOwner(callerIdentity);
            boolean z = (i & 2) != 0;
            if (isDefaultDeviceOwner && z) {
                setDoNotAskCredentialsOnBoot();
            }
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            LockscreenCredential createPin = isNumericOnly ? LockscreenCredential.createPin(str) : LockscreenCredential.createPasswordOrNone(str);
            try {
                if (j == 0 || bArr == null) {
                    if (!this.mLockPatternUtils.setLockCredential(createPin, LockscreenCredential.createNone(), userId)) {
                        return false;
                    }
                } else if (!this.mLockPatternUtils.setLockCredentialWithToken(createPin, j, bArr, userId)) {
                    return false;
                }
                boolean z2 = (i & 1) != 0;
                if (z2) {
                    this.mLockPatternUtils.requireStrongAuth(2, -1);
                }
                synchronized (getLockObject()) {
                    if (!z2) {
                        uid = -1;
                    }
                    if (lambda$getUserDataUnchecked$2.mPasswordOwner != uid) {
                        lambda$getUserDataUnchecked$2.mPasswordOwner = uid;
                        saveSettingsLocked(userId);
                    }
                }
                return true;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isLockScreenSecureUnchecked$36(int i) throws Exception {
        return Boolean.valueOf(this.mLockPatternUtils.isSecure(i));
    }

    public final boolean isLockScreenSecureUnchecked(final int i) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda174
            public final Object getOrThrow() {
                Boolean lambda$isLockScreenSecureUnchecked$36;
                lambda$isLockScreenSecureUnchecked$36 = DevicePolicyManagerService.this.lambda$isLockScreenSecureUnchecked$36(i);
                return lambda$isLockScreenSecureUnchecked$36;
            }
        })).booleanValue();
    }

    public final void setDoNotAskCredentialsOnBoot() {
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
            if (!lambda$getUserDataUnchecked$2.mDoNotAskCredentialsOnBoot) {
                lambda$getUserDataUnchecked$2.mDoNotAskCredentialsOnBoot = true;
                saveSettingsLocked(0);
            }
        }
    }

    public boolean getDoNotAskCredentialsOnBoot() {
        boolean z;
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.QUERY_DO_NOT_ASK_CREDENTIALS_ON_BOOT"));
        synchronized (getLockObject()) {
            z = lambda$getUserDataUnchecked$2(0).mDoNotAskCredentialsOnBoot;
        }
        return z;
    }

    public void setMaximumTimeToLock(ComponentName componentName, String str, long j, boolean z) {
        ActiveAdmin activeAdminForCallerLocked;
        if (this.mHasFeature) {
            if (!isPermissionCheckFlagEnabled()) {
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
            int profileParentId = z ? getProfileParentId(userHandleGetCallingUserId) : userHandleGetCallingUserId;
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    activeAdminForCallerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK", getCallerIdentity(componentName, str).getPackageName(), profileParentId).getActiveAdmin();
                } else {
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 3, z);
                }
                if (activeAdminForCallerLocked.maximumTimeToUnlock != j) {
                    activeAdminForCallerLocked.maximumTimeToUnlock = j;
                    saveSettingsLocked(userHandleGetCallingUserId);
                    updateMaximumTimeToLockLocked(userHandleGetCallingUserId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210019, new Object[]{str, Integer.valueOf(userHandleGetCallingUserId), Integer.valueOf(profileParentId), Long.valueOf(j)});
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void updateMaximumTimeToLockLocked(final int i) {
        if (isManagedProfile(i)) {
            updateProfileLockTimeoutLocked(i);
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda11
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$updateMaximumTimeToLockLocked$37(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMaximumTimeToLockLocked$37(int i) throws Exception {
        int profileParentId = getProfileParentId(i);
        long maximumTimeToLockPolicyFromAdmins = getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(profileParentId));
        DevicePolicyData userDataUnchecked = getUserDataUnchecked(profileParentId);
        if (userDataUnchecked.mLastMaximumTimeToLock == maximumTimeToLockPolicyFromAdmins) {
            return;
        }
        userDataUnchecked.mLastMaximumTimeToLock = maximumTimeToLockPolicyFromAdmins;
        if (maximumTimeToLockPolicyFromAdmins != Long.MAX_VALUE) {
            this.mInjector.settingsGlobalPutInt("stay_on_while_plugged_in", 0);
        }
        getPowerManagerInternal().setMaximumScreenOffTimeoutFromDeviceAdmin(0, maximumTimeToLockPolicyFromAdmins);
    }

    @GuardedBy({"getLockObject()"})
    public final void updateProfileLockTimeoutLocked(final int i) {
        long maximumTimeToLockPolicyFromAdmins = isSeparateProfileChallengeEnabled(i) ? getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(i)) : Long.MAX_VALUE;
        final DevicePolicyData userDataUnchecked = getUserDataUnchecked(i);
        if (userDataUnchecked.mLastMaximumTimeToLock == maximumTimeToLockPolicyFromAdmins) {
            return;
        }
        userDataUnchecked.mLastMaximumTimeToLock = maximumTimeToLockPolicyFromAdmins;
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda176
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$updateProfileLockTimeoutLocked$38(i, userDataUnchecked);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProfileLockTimeoutLocked$38(int i, DevicePolicyData devicePolicyData) throws Exception {
        getPowerManagerInternal().setMaximumScreenOffTimeoutFromDeviceAdmin(i, devicePolicyData.mLastMaximumTimeToLock);
    }

    public long getMaximumTimeToLock(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            Preconditions.checkCallAuthorization(componentName == null || isCallingFromPackage(componentName.getPackageName(), callerIdentity.getUid()) || canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.maximumTimeToUnlock : 0L;
                }
                long maximumTimeToLockPolicyFromAdmins = getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z)));
                if (maximumTimeToLockPolicyFromAdmins != Long.MAX_VALUE) {
                    r1 = maximumTimeToLockPolicyFromAdmins;
                }
                return r1;
            }
        }
        return 0L;
    }

    public final long getMaximumTimeToLockPolicyFromAdmins(List<ActiveAdmin> list) {
        long j = Long.MAX_VALUE;
        for (ActiveAdmin activeAdmin : list) {
            long j2 = activeAdmin.maximumTimeToUnlock;
            if (j2 > 0 && j2 < j) {
                j = j2;
            }
        }
        return j;
    }

    public void setRequiredStrongAuthTimeout(ComponentName componentName, String str, long j, boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin parentOfAdminIfRequired;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
            boolean z2 = true;
            Preconditions.checkArgument(i >= 0, "Timeout must not be a negative number.");
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            }
            long minimumStrongAuthTimeoutMs = getMinimumStrongAuthTimeoutMs();
            if (i != 0 && j < minimumStrongAuthTimeoutMs) {
                j = minimumStrongAuthTimeoutMs;
            }
            if (j > 259200000) {
                j = 259200000;
            }
            int userId = callerIdentity.getUserId();
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS", callerIdentity.getPackageName(), z ? getProfileParentId(callerIdentity.getUserId()) : callerIdentity.getUserId()).getActiveAdmin();
                } else {
                    parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z);
                }
                if (parentOfAdminIfRequired.strongAuthUnlockTimeout != j) {
                    parentOfAdminIfRequired.strongAuthUnlockTimeout = j;
                    saveSettingsLocked(userId);
                } else {
                    z2 = false;
                }
            }
            if (z2) {
                this.mLockSettingsInternal.refreshStrongAuthTimeout(userId);
                if (!isManagedProfile(userId) || isSeparateProfileChallengeEnabled(userId)) {
                    return;
                }
                this.mLockSettingsInternal.refreshStrongAuthTimeout(getProfileParentId(userId));
            }
        }
    }

    public long getRequiredStrongAuthTimeout(ComponentName componentName, int i, boolean z) {
        long j = 259200000;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            if (this.mLockPatternUtils.hasSecureLockScreen()) {
                synchronized (getLockObject()) {
                    if (componentName != null) {
                        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                        return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.strongAuthUnlockTimeout : 0L;
                    }
                    List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                    for (int i2 = 0; i2 < activeAdminsForLockscreenPoliciesLocked.size(); i2++) {
                        long j2 = activeAdminsForLockscreenPoliciesLocked.get(i2).strongAuthUnlockTimeout;
                        if (j2 != 0) {
                            j = Math.min(j2, j);
                        }
                    }
                    return Math.max(j, getMinimumStrongAuthTimeoutMs());
                }
            }
            return 0L;
        }
        return 259200000L;
    }

    public final long getMinimumStrongAuthTimeoutMs() {
        if (!this.mInjector.isBuildDebuggable()) {
            return MINIMUM_STRONG_AUTH_TIMEOUT_MS;
        }
        Injector injector = this.mInjector;
        long j = MINIMUM_STRONG_AUTH_TIMEOUT_MS;
        return Math.min(injector.systemPropertiesGetLong("persist.sys.min_str_auth_timeo", j), j);
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x008f A[Catch: RemoteException -> 0x0084, all -> 0x00e0, TryCatch #1 {, blocks: (B:4:0x000d, B:6:0x0015, B:8:0x0028, B:52:0x00da, B:57:0x00ed, B:56:0x00e7, B:7:0x0022, B:16:0x0043, B:20:0x004f, B:22:0x005a, B:24:0x0062, B:25:0x0068, B:26:0x006f, B:27:0x0070, B:28:0x0077, B:31:0x007b, B:37:0x0087, B:39:0x008f, B:43:0x009d, B:45:0x00b0, B:49:0x00ba, B:51:0x00c0, B:42:0x0094, B:44:0x00a7, B:11:0x0036), top: B:64:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00a7 A[Catch: RemoteException -> 0x0084, all -> 0x00e0, TryCatch #1 {, blocks: (B:4:0x000d, B:6:0x0015, B:8:0x0028, B:52:0x00da, B:57:0x00ed, B:56:0x00e7, B:7:0x0022, B:16:0x0043, B:20:0x004f, B:22:0x005a, B:24:0x0062, B:25:0x0068, B:26:0x006f, B:27:0x0070, B:28:0x0077, B:31:0x007b, B:37:0x0087, B:39:0x008f, B:43:0x009d, B:45:0x00b0, B:49:0x00ba, B:51:0x00c0, B:42:0x0094, B:44:0x00a7, B:11:0x0036), top: B:64:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00ba A[Catch: RemoteException -> 0x0084, all -> 0x00e0, TryCatch #1 {, blocks: (B:4:0x000d, B:6:0x0015, B:8:0x0028, B:52:0x00da, B:57:0x00ed, B:56:0x00e7, B:7:0x0022, B:16:0x0043, B:20:0x004f, B:22:0x005a, B:24:0x0062, B:25:0x0068, B:26:0x006f, B:27:0x0070, B:28:0x0077, B:31:0x007b, B:37:0x0087, B:39:0x008f, B:43:0x009d, B:45:0x00b0, B:49:0x00ba, B:51:0x00c0, B:42:0x0094, B:44:0x00a7, B:11:0x0036), top: B:64:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00bf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void lockNow(int i, boolean z) {
        ActiveAdmin activeAdminOrCheckPermissionForCallerLocked;
        ComponentName component;
        int i2;
        int userId = getCallerIdentity().getUserId();
        synchronized (getLockObject()) {
            ComponentName componentName = null;
            if (isPermissionCheckFlagEnabled()) {
                activeAdminOrCheckPermissionForCallerLocked = getActiveAdminOrCheckPermissionsForCallerLocked(null, 3, z, Set.of("android.permission.MANAGE_DEVICE_POLICY_LOCK", "android.permission.LOCK_DEVICE"));
            } else {
                activeAdminOrCheckPermissionForCallerLocked = getActiveAdminOrCheckPermissionForCallerLocked(null, 3, z, "android.permission.LOCK_DEVICE");
            }
            checkCanExecuteOrThrowUnsafe(1);
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            if (activeAdminOrCheckPermissionForCallerLocked == null) {
                component = null;
            } else {
                try {
                    component = activeAdminOrCheckPermissionForCallerLocked.info.getComponent();
                } catch (RemoteException unused) {
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    component = componentName;
                    DevicePolicyEventLogger.createEvent(10).setAdmin(component).setInt(i).write();
                }
            }
            if (component != null && (i & 1) != 0) {
                try {
                    Preconditions.checkCallingUser(isManagedProfile(userId));
                    Preconditions.checkArgument(!z, "Cannot set FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY for the parent");
                    if (!isProfileOwner(component, userId)) {
                        throw new SecurityException("Only profile owner admins can set FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY");
                    }
                    if (!this.mInjector.storageManagerIsFileBasedEncryptionEnabled()) {
                        throw new UnsupportedOperationException("FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY only applies to FBE devices");
                    }
                    this.mUserManager.evictCredentialEncryptionKey(userId);
                } catch (RemoteException unused2) {
                    componentName = component;
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    component = componentName;
                    DevicePolicyEventLogger.createEvent(10).setAdmin(component).setInt(i).write();
                }
            }
            if (!z && isSeparateProfileChallengeEnabled(userId)) {
                i2 = userId;
                this.mLockPatternUtils.requireStrongAuth(2, i2);
                if (i2 != -1) {
                    if (!this.mIsAutomotive) {
                        this.mInjector.powerManagerGoToSleep(SystemClock.uptimeMillis(), 1, 0);
                    }
                    this.mInjector.getIWindowManager().lockNow((Bundle) null);
                } else {
                    this.mInjector.getTrustManager().setDeviceLockedForUser(i2, true);
                }
                if (SecurityLog.isLoggingEnabled() && component != null) {
                    SecurityLog.writeEvent(210022, new Object[]{component.getPackageName(), Integer.valueOf(userId), Integer.valueOf(!z ? getProfileParentId(userId) : userId)});
                }
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
            i2 = -1;
            this.mLockPatternUtils.requireStrongAuth(2, i2);
            if (i2 != -1) {
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210022, new Object[]{component.getPackageName(), Integer.valueOf(userId), Integer.valueOf(!z ? getProfileParentId(userId) : userId)});
            }
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
        DevicePolicyEventLogger.createEvent(10).setAdmin(component).setInt(i).write();
    }

    public void enforceCanManageCaCerts(ComponentName componentName, String str) {
        Preconditions.checkCallAuthorization(canManageCaCerts(getCallerIdentity(componentName, str)));
    }

    public final boolean canManageCaCerts(CallerIdentity callerIdentity) {
        return (callerIdentity.hasAdminComponent() && (isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-cert-install")) || hasCallingOrSelfPermission("android.permission.MANAGE_CA_CERTIFICATES");
    }

    public boolean approveCaCert(String str, int i, boolean z) {
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
        synchronized (getLockObject()) {
            ArraySet<String> arraySet = lambda$getUserDataUnchecked$2(i).mAcceptedCaCertificates;
            if (z ? arraySet.add(str) : arraySet.remove(str)) {
                saveSettingsLocked(i);
                this.mCertificateMonitor.onCertificateApprovalsChanged(i);
                return true;
            }
            return false;
        }
    }

    public boolean isCaCertApproved(String str, int i) {
        boolean contains;
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
        synchronized (getLockObject()) {
            contains = lambda$getUserDataUnchecked$2(i).mAcceptedCaCertificates.contains(str);
        }
        return contains;
    }

    public final Set<Integer> removeCaApprovalsIfNeeded(int i) {
        ArraySet arraySet = new ArraySet();
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            boolean isSecure = this.mLockPatternUtils.isSecure(userInfo.id);
            if (userInfo.isManagedProfile()) {
                isSecure |= this.mLockPatternUtils.isSecure(getProfileParentId(userInfo.id));
            }
            if (!isSecure) {
                synchronized (getLockObject()) {
                    lambda$getUserDataUnchecked$2(userInfo.id).mAcceptedCaCertificates.clear();
                    arraySet.add(Integer.valueOf(userInfo.id));
                }
                this.mCertificateMonitor.onCertificateApprovalsChanged(i);
            }
        }
        return arraySet;
    }

    public boolean installCaCert(final ComponentName componentName, String str, final byte[] bArr) {
        if (this.mHasFeature) {
            final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            Preconditions.checkCallAuthorization(canManageCaCerts(callerIdentity));
            checkCanExecuteOrThrowUnsafe(24);
            String str2 = (String) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda88
                public final Object getOrThrow() {
                    String lambda$installCaCert$39;
                    lambda$installCaCert$39 = DevicePolicyManagerService.this.lambda$installCaCert$39(callerIdentity, bArr, componentName);
                    return lambda$installCaCert$39;
                }
            });
            if (str2 == null) {
                Slogf.m14w("DevicePolicyManager", "Problem installing cert");
                return false;
            }
            synchronized (getLockObject()) {
                lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mOwnerInstalledCaCerts.add(str2);
                saveSettingsLocked(callerIdentity.getUserId());
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$installCaCert$39(CallerIdentity callerIdentity, byte[] bArr, ComponentName componentName) throws Exception {
        String installCaCert = this.mCertificateMonitor.installCaCert(callerIdentity.getUserHandle(), bArr);
        DevicePolicyEventLogger.createEvent(21).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).write();
        return installCaCert;
    }

    public void uninstallCaCerts(final ComponentName componentName, String str, final String[] strArr) {
        if (this.mHasFeature) {
            final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            Preconditions.checkCallAuthorization(canManageCaCerts(callerIdentity));
            checkCanExecuteOrThrowUnsafe(40);
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda17
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$uninstallCaCerts$40(callerIdentity, strArr, componentName);
                }
            });
            synchronized (getLockObject()) {
                if (lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mOwnerInstalledCaCerts.removeAll(Arrays.asList(strArr))) {
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$uninstallCaCerts$40(CallerIdentity callerIdentity, String[] strArr, ComponentName componentName) throws Exception {
        this.mCertificateMonitor.uninstallCaCerts(callerIdentity.getUserHandle(), strArr);
        DevicePolicyEventLogger.createEvent(24).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).write();
    }

    public boolean installKeyPair(ComponentName componentName, String str, byte[] bArr, byte[] bArr2, byte[] bArr3, String str2, boolean z, boolean z2) {
        long j;
        KeyChain.KeyChainConnection bindAsUser;
        IKeyChainService iKeyChainService;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        boolean isCallerDelegate = isCallerDelegate(callerIdentity, "delegation-cert-install");
        boolean isCredentialManagementApp = isCredentialManagementApp(callerIdentity);
        if (isPermissionCheckFlagEnabled()) {
            Preconditions.checkCallAuthorization(hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", callerIdentity.getPackageName(), callerIdentity.getUserId()) || isCredentialManagementApp);
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && (isCallerDelegate || isCredentialManagementApp)));
        }
        if (isCredentialManagementApp) {
            Preconditions.checkCallAuthorization(!z2, "The credential management app is not allowed to install a user selectable key pair");
            Preconditions.checkCallAuthorization(isAliasInCredentialManagementAppPolicy(callerIdentity, str2), "The alias provided must be contained in the aliases specified in the credential management app's authentication policy");
        }
        checkCanExecuteOrThrowUnsafe(25);
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
        } catch (Throwable th) {
            th = th;
        }
        try {
            try {
                try {
                    bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
                } catch (InterruptedException e) {
                    e = e;
                    Slogf.m13w("DevicePolicyManager", "Interrupted while installing certificate", e);
                    Thread.currentThread().interrupt();
                    this.mInjector.binderRestoreCallingIdentity(j);
                    logInstallKeyPairFailure(callerIdentity, isCredentialManagementApp);
                    return false;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                IKeyChainService service = bindAsUser.getService();
                j = binderClearCallingIdentity;
                try {
                    if (!service.installKeyPair(bArr, bArr2, bArr3, str2, -1)) {
                        logInstallKeyPairFailure(callerIdentity, isCredentialManagementApp);
                        bindAsUser.close();
                        this.mInjector.binderRestoreCallingIdentity(j);
                        return false;
                    }
                    if (z) {
                        iKeyChainService = service;
                        iKeyChainService.setGrant(callerIdentity.getUid(), str2, true);
                    } else {
                        iKeyChainService = service;
                    }
                    iKeyChainService.setUserSelectable(str2, z2);
                    DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(20).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate);
                    String[] strArr = new String[1];
                    strArr[0] = isCredentialManagementApp ? "credentialManagementApp" : "notCredentialManagementApp";
                    devicePolicyEventLogger.setStrings(strArr).write();
                    bindAsUser.close();
                    this.mInjector.binderRestoreCallingIdentity(j);
                    return true;
                } catch (RemoteException e2) {
                    e = e2;
                    Slogf.m25e("DevicePolicyManager", "Installing certificate", e);
                    bindAsUser.close();
                    this.mInjector.binderRestoreCallingIdentity(j);
                    logInstallKeyPairFailure(callerIdentity, isCredentialManagementApp);
                    return false;
                }
            } catch (RemoteException e3) {
                e = e3;
                j = binderClearCallingIdentity;
            } catch (Throwable th3) {
                th = th3;
                bindAsUser.close();
                throw th;
            }
        } catch (InterruptedException e4) {
            e = e4;
            j = binderClearCallingIdentity;
            Slogf.m13w("DevicePolicyManager", "Interrupted while installing certificate", e);
            Thread.currentThread().interrupt();
            this.mInjector.binderRestoreCallingIdentity(j);
            logInstallKeyPairFailure(callerIdentity, isCredentialManagementApp);
            return false;
        } catch (Throwable th4) {
            th = th4;
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            throw th;
        }
    }

    public final void logInstallKeyPairFailure(CallerIdentity callerIdentity, boolean z) {
        if (z) {
            DevicePolicyEventLogger.createEvent(184).setStrings(new String[]{callerIdentity.getPackageName()}).write();
        }
    }

    public boolean removeKeyPair(ComponentName componentName, String str, String str2) {
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        boolean isCallerDelegate = isCallerDelegate(callerIdentity, "delegation-cert-install");
        boolean isCredentialManagementApp = isCredentialManagementApp(callerIdentity);
        if (isPermissionCheckFlagEnabled()) {
            Preconditions.checkCallAuthorization(hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", callerIdentity.getPackageName(), callerIdentity.getUserId()) || isCredentialManagementApp);
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && (isCallerDelegate || isCredentialManagementApp)));
        }
        if (isCredentialManagementApp) {
            Preconditions.checkCallAuthorization(isAliasInCredentialManagementAppPolicy(callerIdentity, str2), "The alias provided must be contained in the aliases specified in the credential management app's authentication policy");
        }
        checkCanExecuteOrThrowUnsafe(28);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
                try {
                    try {
                        IKeyChainService service = bindAsUser.getService();
                        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(23).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate);
                        String[] strArr = new String[1];
                        strArr[0] = isCredentialManagementApp ? "credentialManagementApp" : "notCredentialManagementApp";
                        devicePolicyEventLogger.setStrings(strArr).write();
                        return service.removeKeyPair(str2);
                    } finally {
                        bindAsUser.close();
                    }
                } catch (RemoteException e) {
                    Slogf.m25e("DevicePolicyManager", "Removing keypair", e);
                    bindAsUser.close();
                    return false;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        } catch (InterruptedException e2) {
            Slogf.m13w("DevicePolicyManager", "Interrupted while removing keypair", e2);
            Thread.currentThread().interrupt();
        }
    }

    public boolean hasKeyPair(String str, final String str2) {
        final CallerIdentity callerIdentity = getCallerIdentity(str);
        boolean isCredentialManagementApp = isCredentialManagementApp(callerIdentity);
        Preconditions.checkCallAuthorization(canInstallCertificates(callerIdentity) || isCredentialManagementApp);
        if (isCredentialManagementApp) {
            Preconditions.checkCallAuthorization(isAliasInCredentialManagementAppPolicy(callerIdentity, str2), "The alias provided must be contained in the aliases specified in the credential management app's authentication policy");
        }
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda115
            public final Object getOrThrow() {
                Boolean lambda$hasKeyPair$41;
                lambda$hasKeyPair$41 = DevicePolicyManagerService.this.lambda$hasKeyPair$41(callerIdentity, str2);
                return lambda$hasKeyPair$41;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hasKeyPair$41(CallerIdentity callerIdentity, String str) throws Exception {
        try {
            KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
            Boolean valueOf = Boolean.valueOf(bindAsUser.getService().containsKeyPair(str));
            bindAsUser.close();
            return valueOf;
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Querying keypair", e);
            return Boolean.FALSE;
        } catch (InterruptedException e2) {
            Slogf.m13w("DevicePolicyManager", "Interrupted while querying keypair", e2);
            Thread.currentThread().interrupt();
            return Boolean.FALSE;
        }
    }

    public final boolean canInstallCertificates(CallerIdentity callerIdentity) {
        if (isPermissionCheckFlagEnabled()) {
            return hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", callerIdentity.getPackageName(), callerIdentity.getUserId());
        }
        return isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isCallerDelegate(callerIdentity, "delegation-cert-install");
    }

    public final boolean canChooseCertificates(CallerIdentity callerIdentity) {
        return isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isCallerDelegate(callerIdentity, "delegation-cert-selection");
    }

    public boolean setKeyGrantToWifiAuth(String str, String str2, boolean z) {
        Preconditions.checkStringNotEmpty(str2, "Alias to grant cannot be empty");
        CallerIdentity callerIdentity = getCallerIdentity(str);
        Preconditions.checkCallAuthorization(canChooseCertificates(callerIdentity));
        try {
            return setKeyChainGrantInternal(str2, z, 1010, callerIdentity.getUserHandle());
        } catch (IllegalArgumentException e) {
            if (this.mInjector.isChangeEnabled(175101461L, callerIdentity.getPackageName(), callerIdentity.getUserId())) {
                throw e;
            }
            return false;
        }
    }

    public boolean isKeyPairGrantedToWifiAuth(String str, final String str2) {
        Preconditions.checkStringNotEmpty(str2, "Alias to check cannot be empty");
        final CallerIdentity callerIdentity = getCallerIdentity(str);
        Preconditions.checkCallAuthorization(canChooseCertificates(callerIdentity));
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda56
            public final Object getOrThrow() {
                Boolean lambda$isKeyPairGrantedToWifiAuth$42;
                lambda$isKeyPairGrantedToWifiAuth$42 = DevicePolicyManagerService.this.lambda$isKeyPairGrantedToWifiAuth$42(callerIdentity, str2);
                return lambda$isKeyPairGrantedToWifiAuth$42;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isKeyPairGrantedToWifiAuth$42(CallerIdentity callerIdentity, String str) throws Exception {
        try {
            KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
            new ArrayList();
            for (int i : bindAsUser.getService().getGrants(str)) {
                if (i == 1010) {
                    Boolean bool = Boolean.TRUE;
                    bindAsUser.close();
                    return bool;
                }
            }
            Boolean bool2 = Boolean.FALSE;
            bindAsUser.close();
            return bool2;
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Querying grant to wifi auth.", e);
            return Boolean.FALSE;
        }
    }

    public boolean setKeyGrantForApp(ComponentName componentName, String str, String str2, String str3, boolean z) {
        Preconditions.checkStringNotEmpty(str2, "Alias to grant cannot be empty");
        Preconditions.checkStringNotEmpty(str3, "Package to grant to cannot be empty");
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        boolean z2 = true;
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-cert-selection")));
        try {
            ApplicationInfo applicationInfo = this.mInjector.getIPackageManager().getApplicationInfo(str3, 0L, callerIdentity.getUserId());
            if (applicationInfo == null) {
                z2 = false;
            }
            Preconditions.checkArgument(z2, "Provided package %s is not installed", new Object[]{str3});
            try {
                return setKeyChainGrantInternal(str2, z, applicationInfo.uid, callerIdentity.getUserHandle());
            } catch (IllegalArgumentException e) {
                if (this.mInjector.isChangeEnabled(175101461L, str, callerIdentity.getUserId())) {
                    throw e;
                }
                return false;
            }
        } catch (RemoteException e2) {
            throw new IllegalStateException("Failure getting grantee uid", e2);
        }
    }

    public final boolean setKeyChainGrantInternal(String str, boolean z, int i, UserHandle userHandle) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, userHandle);
                try {
                    boolean grant = bindAsUser.getService().setGrant(i, str, z);
                    bindAsUser.close();
                    return grant;
                } catch (Throwable th) {
                    if (bindAsUser != null) {
                        try {
                            bindAsUser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "Setting grant for package.", e);
                return false;
            }
        } catch (InterruptedException e2) {
            Slogf.m13w("DevicePolicyManager", "Interrupted while setting key grant", e2);
            Thread.currentThread().interrupt();
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public ParcelableGranteeMap getKeyPairGrants(String str, final String str2) {
        final CallerIdentity callerIdentity = getCallerIdentity(str);
        Preconditions.checkCallAuthorization(canChooseCertificates(callerIdentity));
        final ArrayMap arrayMap = new ArrayMap();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda90
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$getKeyPairGrants$43(callerIdentity, str2, arrayMap);
            }
        });
        return new ParcelableGranteeMap(arrayMap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getKeyPairGrants$43(CallerIdentity callerIdentity, String str, ArrayMap arrayMap) throws Exception {
        try {
            KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
            try {
                int[] grants = bindAsUser.getService().getGrants(str);
                PackageManager packageManager = this.mInjector.getPackageManager(callerIdentity.getUserId());
                for (int i : grants) {
                    String[] packagesForUid = packageManager.getPackagesForUid(i);
                    if (packagesForUid == null) {
                        Slogf.wtf("DevicePolicyManager", "No packages found for uid " + i);
                    } else {
                        arrayMap.put(Integer.valueOf(i), new ArraySet(packagesForUid));
                    }
                }
                bindAsUser.close();
            } catch (Throwable th) {
                if (bindAsUser != null) {
                    try {
                        bindAsUser.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Querying keypair grants", e);
        } catch (InterruptedException e2) {
            Slogf.m13w("DevicePolicyManager", "Interrupted while querying keypair grants", e2);
            Thread.currentThread().interrupt();
        }
    }

    @VisibleForTesting
    public static int[] translateIdAttestationFlags(int i) {
        HashMap hashMap = new HashMap();
        hashMap.put(2, 1);
        hashMap.put(4, 2);
        hashMap.put(8, 3);
        hashMap.put(16, 4);
        int bitCount = Integer.bitCount(i);
        if (bitCount == 0) {
            return null;
        }
        if ((i & 1) != 0) {
            bitCount--;
            i &= -2;
        }
        int[] iArr = new int[bitCount];
        int i2 = 0;
        for (Integer num : hashMap.keySet()) {
            if ((num.intValue() & i) != 0) {
                iArr[i2] = ((Integer) hashMap.get(num)).intValue();
                i2++;
            }
        }
        return iArr;
    }

    public boolean generateKeyPair(ComponentName componentName, String str, String str2, ParcelableKeyGenParameterSpec parcelableKeyGenParameterSpec, int i, KeymasterCertificateChain keymasterCertificateChain) {
        int[] translateIdAttestationFlags = translateIdAttestationFlags(i);
        boolean z = translateIdAttestationFlags != null;
        KeyGenParameterSpec spec = parcelableKeyGenParameterSpec.getSpec();
        String keystoreAlias = spec.getKeystoreAlias();
        Preconditions.checkStringNotEmpty(keystoreAlias, "Empty alias provided");
        Preconditions.checkArgument((z && spec.getAttestationChallenge() == null) ? false : true, "Requested Device ID attestation but challenge is empty");
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        boolean isCallerDelegate = isCallerDelegate(callerIdentity, "delegation-cert-install");
        boolean isCredentialManagementApp = isCredentialManagementApp(callerIdentity);
        if (!z || translateIdAttestationFlags.length <= 0) {
            if (isPermissionCheckFlagEnabled()) {
                Preconditions.checkCallAuthorization(hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", callerIdentity.getPackageName(), callerIdentity.getUserId()) || isCredentialManagementApp);
            } else {
                Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && (isCallerDelegate || isCredentialManagementApp)));
            }
            if (isCredentialManagementApp) {
                Preconditions.checkCallAuthorization(isAliasInCredentialManagementAppPolicy(callerIdentity, keystoreAlias), "The alias provided must be contained in the aliases specified in the credential management app's authentication policy");
            }
        } else {
            Preconditions.checkCallAuthorization(hasDeviceIdAccessUnchecked(callerIdentity.getPackageName(), callerIdentity.getUid()));
            enforceIndividualAttestationSupportedIfRequested(translateIdAttestationFlags);
        }
        if (TextUtils.isEmpty(keystoreAlias)) {
            throw new IllegalArgumentException("Empty alias provided.");
        }
        if (spec.getUid() != -1) {
            Slogf.m26e("DevicePolicyManager", "Only the caller can be granted access to the generated keypair.");
            logGenerateKeyPairFailure(callerIdentity, isCredentialManagementApp);
            return false;
        }
        if (z) {
            if (spec.getAttestationChallenge() == null) {
                throw new IllegalArgumentException("Requested Device ID attestation but challenge is empty.");
            }
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(spec);
            builder.setAttestationIds(translateIdAttestationFlags);
            builder.setDevicePropertiesAttestationIncluded(true);
            spec = builder.build();
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
                try {
                    IKeyChainService service = bindAsUser.getService();
                    int generateKeyPair = service.generateKeyPair(str2, new ParcelableKeyGenParameterSpec(spec));
                    if (generateKeyPair != 0) {
                        Slogf.m24e("DevicePolicyManager", "KeyChain failed to generate a keypair, error %d.", Integer.valueOf(generateKeyPair));
                        logGenerateKeyPairFailure(callerIdentity, isCredentialManagementApp);
                        if (generateKeyPair != 3) {
                            if (generateKeyPair != 6) {
                                bindAsUser.close();
                                return false;
                            }
                            throw new ServiceSpecificException(1, String.format("KeyChain error: %d", Integer.valueOf(generateKeyPair)));
                        }
                        throw new UnsupportedOperationException("Device does not support Device ID attestation.");
                    }
                    service.setGrant(callerIdentity.getUid(), keystoreAlias, true);
                    try {
                        ArrayList arrayList = new ArrayList();
                        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                        byte[] caCertificates = service.getCaCertificates(keystoreAlias);
                        arrayList.add(service.getCertificate(keystoreAlias));
                        if (caCertificates != null) {
                            Iterator<? extends Certificate> it = certificateFactory.generateCertificates(new ByteArrayInputStream(caCertificates)).iterator();
                            while (it.hasNext()) {
                                arrayList.add(((X509Certificate) it.next()).getEncoded());
                            }
                        }
                        keymasterCertificateChain.shallowCopyFrom(new KeymasterCertificateChain(arrayList));
                        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(59).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate).setInt(i);
                        String[] strArr = new String[2];
                        strArr[0] = str2;
                        strArr[1] = isCredentialManagementApp ? "credentialManagementApp" : "notCredentialManagementApp";
                        devicePolicyEventLogger.setStrings(strArr).write();
                        bindAsUser.close();
                        return true;
                    } catch (CertificateException e) {
                        logGenerateKeyPairFailure(callerIdentity, isCredentialManagementApp);
                        Slogf.m25e("DevicePolicyManager", "While retrieving certificate chain.", e);
                        bindAsUser.close();
                        return false;
                    }
                } catch (Throwable th) {
                    if (bindAsUser != null) {
                        try {
                            bindAsUser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        } catch (RemoteException e2) {
            Slogf.m25e("DevicePolicyManager", "KeyChain error while generating a keypair", e2);
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            logGenerateKeyPairFailure(callerIdentity, isCredentialManagementApp);
            return false;
        } catch (InterruptedException e3) {
            Slogf.m13w("DevicePolicyManager", "Interrupted while generating keypair", e3);
            Thread.currentThread().interrupt();
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            logGenerateKeyPairFailure(callerIdentity, isCredentialManagementApp);
            return false;
        }
    }

    public final void logGenerateKeyPairFailure(CallerIdentity callerIdentity, boolean z) {
        if (z) {
            DevicePolicyEventLogger.createEvent(185).setStrings(new String[]{callerIdentity.getPackageName()}).write();
        }
    }

    public final void enforceIndividualAttestationSupportedIfRequested(int[] iArr) {
        for (int i : iArr) {
            if (i == 4 && !this.mInjector.getPackageManager().hasSystemFeature("android.hardware.device_unique_attestation")) {
                throw new UnsupportedOperationException("Device Individual attestation is not supported on this device.");
            }
        }
    }

    public boolean setKeyPairCertificate(ComponentName componentName, String str, String str2, byte[] bArr, byte[] bArr2, boolean z) {
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        boolean isCallerDelegate = isCallerDelegate(callerIdentity, "delegation-cert-install");
        boolean isCredentialManagementApp = isCredentialManagementApp(callerIdentity);
        if (isPermissionCheckFlagEnabled()) {
            Preconditions.checkCallAuthorization(hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", callerIdentity.getPackageName(), callerIdentity.getUserId()) || isCredentialManagementApp);
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && (isCallerDelegate || isCredentialManagementApp)));
        }
        if (isCredentialManagementApp) {
            Preconditions.checkCallAuthorization(isAliasInCredentialManagementAppPolicy(callerIdentity, str2), "The alias provided must be contained in the aliases specified in the credential management app's authentication policy");
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
                try {
                    IKeyChainService service = bindAsUser.getService();
                    if (!service.setKeyPairCertificate(str2, bArr, bArr2)) {
                        bindAsUser.close();
                        return false;
                    }
                    service.setUserSelectable(str2, z);
                    DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(60).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate);
                    String[] strArr = new String[1];
                    strArr[0] = isCredentialManagementApp ? "credentialManagementApp" : "notCredentialManagementApp";
                    devicePolicyEventLogger.setStrings(strArr).write();
                    bindAsUser.close();
                    return true;
                } catch (Throwable th) {
                    if (bindAsUser != null) {
                        try {
                            bindAsUser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "Failed setting keypair certificate", e);
                return false;
            } catch (InterruptedException e2) {
                Slogf.m13w("DevicePolicyManager", "Interrupted while setting keypair certificate", e2);
                Thread.currentThread().interrupt();
                return false;
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public void choosePrivateKeyAlias(int i, Uri uri, String str, final IBinder iBinder) {
        boolean z;
        final CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isSystemUid(callerIdentity), String.format("Only the system can %s", "choose private key alias"));
        ComponentName lambda$isProfileOwner$66 = lambda$isProfileOwner$66(callerIdentity.getUserId());
        if (lambda$isProfileOwner$66 == null && callerIdentity.getUserHandle().isSystem()) {
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked != null) {
                    lambda$isProfileOwner$66 = deviceOwnerAdminLocked.info.getComponent();
                }
            }
        }
        if (lambda$isProfileOwner$66 == null) {
            sendPrivateKeyAliasResponse(null, iBinder);
            return;
        }
        final Intent intent = new Intent("android.app.action.CHOOSE_PRIVATE_KEY_ALIAS");
        intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_SENDER_UID", i);
        intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_URI", uri);
        intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_ALIAS", str);
        intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_RESPONSE", iBinder);
        intent.addFlags(268435456);
        ComponentName resolveDelegateReceiver = resolveDelegateReceiver("delegation-cert-selection", "android.app.action.CHOOSE_PRIVATE_KEY_ALIAS", callerIdentity.getUserId());
        if (resolveDelegateReceiver != null) {
            intent.setComponent(resolveDelegateReceiver);
            z = true;
        } else {
            intent.setComponent(lambda$isProfileOwner$66);
            z = false;
        }
        final boolean z2 = z;
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda80
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$choosePrivateKeyAlias$44(intent, callerIdentity, iBinder, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$choosePrivateKeyAlias$44(Intent intent, CallerIdentity callerIdentity, final IBinder iBinder, boolean z) throws Exception {
        this.mContext.sendOrderedBroadcastAsUser(intent, callerIdentity.getUserHandle(), null, new BroadcastReceiver() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent2) {
                DevicePolicyManagerService.this.sendPrivateKeyAliasResponse(getResultData(), iBinder);
            }
        }, null, -1, null, null);
        DevicePolicyEventLogger.createEvent(22).setAdmin(intent.getComponent()).setBoolean(z).write();
    }

    public final void sendPrivateKeyAliasResponse(String str, IBinder iBinder) {
        try {
            IKeyChainAliasCallback.Stub.asInterface(iBinder).alias(str);
        } catch (Exception e) {
            Slogf.m25e("DevicePolicyManager", "error while responding to callback", e);
        }
    }

    public static boolean shouldCheckIfDelegatePackageIsInstalled(String str, int i, List<String> list) {
        if (i >= 24) {
            return true;
        }
        return ((list.size() == 1 && list.get(0).equals("delegation-cert-install")) || list.isEmpty()) ? false : true;
    }

    public void setDelegatedScopes(ComponentName componentName, String str, List<String> list) throws SecurityException {
        ArrayList arrayList;
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkStringNotEmpty(str, "Delegate package is null or empty");
        Preconditions.checkCollectionElementsNotNull(list, "Scopes");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        ArrayList<String> arrayList2 = new ArrayList<>(new ArraySet(list));
        if (arrayList2.retainAll(Arrays.asList(DELEGATIONS))) {
            throw new IllegalArgumentException("Unexpected delegation scopes");
        }
        int userId = callerIdentity.getUserId();
        boolean z = false;
        if (!Collections.disjoint(arrayList2, DEVICE_OWNER_OR_MANAGED_PROFILE_OWNER_DELEGATIONS)) {
            if (isDefaultDeviceOwner(callerIdentity) || (isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId()))) {
                z = true;
            }
            Preconditions.checkCallAuthorization(z);
        } else if (!Collections.disjoint(arrayList2, f1138xcbb66b4a)) {
            Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) ? true : true);
        } else {
            Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity)) ? true : true);
        }
        synchronized (getLockObject()) {
            if (shouldCheckIfDelegatePackageIsInstalled(str, getTargetSdk(componentName.getPackageName(), userId), arrayList2) && !isPackageInstalledForUser(str, userId)) {
                throw new IllegalArgumentException("Package " + str + " is not installed on the current user");
            }
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
            if (!arrayList2.isEmpty()) {
                lambda$getUserDataUnchecked$2.mDelegationMap.put(str, new ArrayList(arrayList2));
                arrayList = new ArrayList(arrayList2);
                arrayList.retainAll(EXCLUSIVE_DELEGATIONS);
            } else {
                lambda$getUserDataUnchecked$2.mDelegationMap.remove(str);
                arrayList = null;
            }
            sendDelegationChangedBroadcast(str, arrayList2, userId);
            if (arrayList != null && !arrayList.isEmpty()) {
                for (int size = lambda$getUserDataUnchecked$2.mDelegationMap.size() - 1; size >= 0; size--) {
                    String keyAt = lambda$getUserDataUnchecked$2.mDelegationMap.keyAt(size);
                    List<String> valueAt = lambda$getUserDataUnchecked$2.mDelegationMap.valueAt(size);
                    if (!keyAt.equals(str) && valueAt.removeAll(arrayList)) {
                        if (valueAt.isEmpty()) {
                            lambda$getUserDataUnchecked$2.mDelegationMap.removeAt(size);
                        }
                        sendDelegationChangedBroadcast(keyAt, new ArrayList<>(valueAt), userId);
                    }
                }
            }
            saveSettingsLocked(userId);
        }
    }

    public final void sendDelegationChangedBroadcast(String str, ArrayList<String> arrayList, int i) {
        Intent intent = new Intent("android.app.action.APPLICATION_DELEGATION_SCOPES_CHANGED");
        intent.addFlags(1073741824);
        intent.setPackage(str);
        intent.putStringArrayListExtra("android.app.extra.DELEGATION_SCOPES", arrayList);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i));
    }

    public List<String> getDelegatedScopes(ComponentName componentName, String str) throws SecurityException {
        List<String> list;
        Objects.requireNonNull(str, "Delegate package is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        if (callerIdentity.hasAdminComponent()) {
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        } else {
            Preconditions.checkCallAuthorization(isPackage(callerIdentity, str), String.format("Caller with uid %d is not %s", Integer.valueOf(callerIdentity.getUid()), str));
        }
        synchronized (getLockObject()) {
            list = lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mDelegationMap.get(str);
            if (list == null) {
                list = Collections.EMPTY_LIST;
            }
        }
        return list;
    }

    public List<String> getDelegatePackages(ComponentName componentName, String str) throws SecurityException {
        List<String> delegatePackagesInternalLocked;
        Objects.requireNonNull(componentName, "ComponentName is null");
        Objects.requireNonNull(str, "Scope is null");
        if (!Arrays.asList(DELEGATIONS).contains(str)) {
            throw new IllegalArgumentException("Unexpected delegation scope: " + str);
        }
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        synchronized (getLockObject()) {
            delegatePackagesInternalLocked = getDelegatePackagesInternalLocked(str, callerIdentity.getUserId());
        }
        return delegatePackagesInternalLocked;
    }

    public final List<String> getDelegatePackagesInternalLocked(String str, int i) {
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < lambda$getUserDataUnchecked$2.mDelegationMap.size(); i2++) {
            if (lambda$getUserDataUnchecked$2.mDelegationMap.valueAt(i2).contains(str)) {
                arrayList.add(lambda$getUserDataUnchecked$2.mDelegationMap.keyAt(i2));
            }
        }
        return arrayList;
    }

    public final ComponentName resolveDelegateReceiver(String str, String str2, int i) {
        List<String> delegatePackagesInternalLocked;
        synchronized (getLockObject()) {
            delegatePackagesInternalLocked = getDelegatePackagesInternalLocked(str, i);
        }
        if (delegatePackagesInternalLocked.size() == 0) {
            return null;
        }
        if (delegatePackagesInternalLocked.size() > 1) {
            Slogf.wtf("DevicePolicyManager", "More than one delegate holds " + str);
            return null;
        }
        String str3 = delegatePackagesInternalLocked.get(0);
        Intent intent = new Intent(str2);
        intent.setPackage(str3);
        try {
            List list = this.mIPackageManager.queryIntentReceivers(intent, (String) null, 0L, i).getList();
            int size = list.size();
            if (size >= 1) {
                if (size > 1) {
                    Slogf.m14w("DevicePolicyManager", str3 + " defines more than one delegate receiver for " + str2);
                }
                return ((ResolveInfo) list.get(0)).activityInfo.getComponentName();
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    public final boolean isCallerDelegate(String str, int i, String str2) {
        Objects.requireNonNull(str, "callerPackage is null");
        if (!Arrays.asList(DELEGATIONS).contains(str2)) {
            throw new IllegalArgumentException("Unexpected delegation scope: " + str2);
        }
        int userId = UserHandle.getUserId(i);
        synchronized (getLockObject()) {
            List<String> list = lambda$getUserDataUnchecked$2(userId).mDelegationMap.get(str);
            if (list == null || !list.contains(str2)) {
                return false;
            }
            return isCallingFromPackage(str, i);
        }
    }

    public final boolean isCallerDelegate(CallerIdentity callerIdentity, String str) {
        boolean z = false;
        if (callerIdentity.getPackageName() == null) {
            return false;
        }
        Preconditions.checkArgument(Arrays.asList(DELEGATIONS).contains(str), "Unexpected delegation scope: %s", new Object[]{str});
        synchronized (getLockObject()) {
            List<String> list = lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mDelegationMap.get(callerIdentity.getPackageName());
            if (list != null && list.contains(str)) {
                z = true;
            }
        }
        return z;
    }

    public final boolean isCallerDelegate(CallerIdentity callerIdentity) {
        boolean z;
        Objects.requireNonNull(callerIdentity.getPackageName(), "callerPackage is null");
        synchronized (getLockObject()) {
            z = lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mDelegationMap.get(callerIdentity.getPackageName()) != null;
        }
        return z;
    }

    public final void setDelegatedScopePreO(ComponentName componentName, String str, String str2) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(callerIdentity.getUserId());
            if (str != null) {
                List<String> list = lambda$getUserDataUnchecked$2.mDelegationMap.get(str);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (!list.contains(str2)) {
                    list.add(str2);
                    setDelegatedScopes(componentName, str, list);
                }
            }
            for (int i = 0; i < lambda$getUserDataUnchecked$2.mDelegationMap.size(); i++) {
                String keyAt = lambda$getUserDataUnchecked$2.mDelegationMap.keyAt(i);
                List<String> valueAt = lambda$getUserDataUnchecked$2.mDelegationMap.valueAt(i);
                if (!keyAt.equals(str) && valueAt.contains(str2)) {
                    ArrayList arrayList = new ArrayList(valueAt);
                    arrayList.remove(str2);
                    setDelegatedScopes(componentName, keyAt, arrayList);
                }
            }
        }
    }

    public final boolean isCredentialManagementApp(final CallerIdentity callerIdentity) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda111
            public final Object getOrThrow() {
                Boolean lambda$isCredentialManagementApp$45;
                lambda$isCredentialManagementApp$45 = DevicePolicyManagerService.this.lambda$isCredentialManagementApp$45(callerIdentity);
                return lambda$isCredentialManagementApp$45;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isCredentialManagementApp$45(CallerIdentity callerIdentity) throws Exception {
        AppOpsManager appOpsManager = this.mInjector.getAppOpsManager();
        if (appOpsManager == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(appOpsManager.noteOpNoThrow(104, callerIdentity.getUid(), callerIdentity.getPackageName(), (String) null, (String) null) == 0);
    }

    public final boolean isAliasInCredentialManagementAppPolicy(final CallerIdentity callerIdentity, final String str) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda66
            public final Object getOrThrow() {
                Boolean lambda$isAliasInCredentialManagementAppPolicy$46;
                lambda$isAliasInCredentialManagementAppPolicy$46 = DevicePolicyManagerService.this.lambda$isAliasInCredentialManagementAppPolicy$46(callerIdentity, str);
                return lambda$isAliasInCredentialManagementAppPolicy$46;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isAliasInCredentialManagementAppPolicy$46(CallerIdentity callerIdentity, String str) throws Exception {
        try {
            KeyChain.KeyChainConnection bindAsUser = KeyChain.bindAsUser(this.mContext, callerIdentity.getUserHandle());
            AppUriAuthenticationPolicy credentialManagementAppPolicy = bindAsUser.getService().getCredentialManagementAppPolicy();
            Boolean valueOf = Boolean.valueOf((credentialManagementAppPolicy == null || credentialManagementAppPolicy.getAppAndUriMappings().isEmpty() || !containsAlias(credentialManagementAppPolicy, str)) ? false : true);
            bindAsUser.close();
            return valueOf;
        } catch (RemoteException | InterruptedException unused) {
            return Boolean.FALSE;
        }
    }

    public static boolean containsAlias(AppUriAuthenticationPolicy appUriAuthenticationPolicy, String str) {
        for (Map.Entry<String, Map<Uri, String>> entry : appUriAuthenticationPolicy.getAppAndUriMappings().entrySet()) {
            for (Map.Entry<Uri, String> entry2 : entry.getValue().entrySet()) {
                if (entry2.getValue().equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setCertInstallerPackage(ComponentName componentName, String str) throws SecurityException {
        setDelegatedScopePreO(componentName, str, "delegation-cert-install");
        DevicePolicyEventLogger.createEvent(25).setAdmin(componentName).setStrings(new String[]{str}).write();
    }

    public String getCertInstallerPackage(ComponentName componentName) throws SecurityException {
        List<String> delegatePackages = getDelegatePackages(componentName, "delegation-cert-install");
        if (delegatePackages.size() > 0) {
            return delegatePackages.get(0);
        }
        return null;
    }

    public boolean setAlwaysOnVpnPackage(ComponentName componentName, final String str, final boolean z, final List<String> list) throws SecurityException {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(30);
        if (str == null) {
            synchronized (getLockObject()) {
                String str2 = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).mAlwaysOnVpnPackage;
                if (TextUtils.isEmpty(str2)) {
                    return true;
                }
                revokeVpnAuthorizationForPackage(str2, callerIdentity.getUserId());
            }
        }
        final int userId = callerIdentity.getUserId();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda106
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setAlwaysOnVpnPackage$47(str, userId, z, list);
            }
        });
        DevicePolicyEventLogger.createEvent(26).setAdmin(callerIdentity.getComponentName()).setStrings(new String[]{str}).setBoolean(z).setInt(list != null ? list.size() : 0).write();
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            if (!TextUtils.equals(str, profileOwnerOrDeviceOwnerLocked.mAlwaysOnVpnPackage) || z != profileOwnerOrDeviceOwnerLocked.mAlwaysOnVpnLockdown) {
                profileOwnerOrDeviceOwnerLocked.mAlwaysOnVpnPackage = str;
                profileOwnerOrDeviceOwnerLocked.mAlwaysOnVpnLockdown = z;
                saveSettingsLocked(userId);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAlwaysOnVpnPackage$47(String str, int i, boolean z, List list) throws Exception {
        if (str != null && !isPackageInstalledForUser(str, i)) {
            Slogf.m14w("DevicePolicyManager", "Non-existent VPN package specified: " + str);
            throw new ServiceSpecificException(1, str);
        }
        if (str != null && z && list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (!isPackageInstalledForUser(str2, i)) {
                    Slogf.m14w("DevicePolicyManager", "Non-existent package in VPN allowlist: " + str2);
                    throw new ServiceSpecificException(1, str2);
                }
            }
        }
        if (!this.mInjector.getVpnManager().setAlwaysOnVpnPackageForUser(i, str, z, list)) {
            throw new UnsupportedOperationException();
        }
    }

    public final void revokeVpnAuthorizationForPackage(final String str, final int i) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda177
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$revokeVpnAuthorizationForPackage$48(str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$revokeVpnAuthorizationForPackage$48(String str, int i) throws Exception {
        try {
            ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0L, i);
            if (applicationInfo == null) {
                Slogf.m14w("DevicePolicyManager", "Non-existent VPN package: " + str);
            } else {
                this.mInjector.getAppOpsManager().setMode(47, applicationInfo.uid, str, 3);
            }
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Can't talk to package managed", e);
        }
    }

    public String getAlwaysOnVpnPackage(ComponentName componentName) throws SecurityException {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return (String) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda23
            public final Object getOrThrow() {
                String lambda$getAlwaysOnVpnPackage$49;
                lambda$getAlwaysOnVpnPackage$49 = DevicePolicyManagerService.this.lambda$getAlwaysOnVpnPackage$49(callerIdentity);
                return lambda$getAlwaysOnVpnPackage$49;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getAlwaysOnVpnPackage$49(CallerIdentity callerIdentity) throws Exception {
        return this.mInjector.getVpnManager().getAlwaysOnVpnPackageForUser(callerIdentity.getUserId());
    }

    public String getAlwaysOnVpnPackageForUser(int i) {
        String str;
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "call getAlwaysOnVpnPackageForUser"));
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            str = deviceOrProfileOwnerAdminLocked != null ? deviceOrProfileOwnerAdminLocked.mAlwaysOnVpnPackage : null;
        }
        return str;
    }

    public boolean isAlwaysOnVpnLockdownEnabled(ComponentName componentName) throws SecurityException {
        final CallerIdentity callerIdentity;
        if (hasCallingPermission("android.permission.MAINLINE_NETWORK_STACK")) {
            callerIdentity = getCallerIdentity();
        } else {
            callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        }
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda64
            public final Object getOrThrow() {
                Boolean lambda$isAlwaysOnVpnLockdownEnabled$50;
                lambda$isAlwaysOnVpnLockdownEnabled$50 = DevicePolicyManagerService.this.lambda$isAlwaysOnVpnLockdownEnabled$50(callerIdentity);
                return lambda$isAlwaysOnVpnLockdownEnabled$50;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isAlwaysOnVpnLockdownEnabled$50(CallerIdentity callerIdentity) throws Exception {
        return Boolean.valueOf(this.mInjector.getVpnManager().isVpnLockdownEnabled(callerIdentity.getUserId()));
    }

    public boolean isAlwaysOnVpnLockdownEnabledForUser(int i) {
        boolean z;
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "call isAlwaysOnVpnLockdownEnabledForUser"));
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            z = deviceOrProfileOwnerAdminLocked != null && deviceOrProfileOwnerAdminLocked.mAlwaysOnVpnLockdown;
        }
        return z;
    }

    public List<String> getAlwaysOnVpnLockdownAllowlist(ComponentName componentName) throws SecurityException {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda132
            public final Object getOrThrow() {
                List lambda$getAlwaysOnVpnLockdownAllowlist$51;
                lambda$getAlwaysOnVpnLockdownAllowlist$51 = DevicePolicyManagerService.this.lambda$getAlwaysOnVpnLockdownAllowlist$51(callerIdentity);
                return lambda$getAlwaysOnVpnLockdownAllowlist$51;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$getAlwaysOnVpnLockdownAllowlist$51(CallerIdentity callerIdentity) throws Exception {
        return this.mInjector.getVpnManager().getVpnLockdownAllowlist(callerIdentity.getUserId());
    }

    public final void forceWipeDeviceNoLock(boolean z, String str, boolean z2, boolean z3) {
        wtfIfInLock();
        try {
            try {
                if (!this.mInjector.recoverySystemRebootWipeUserData(false, str, true, z2, z, z3)) {
                    Slogf.m20i("DevicePolicyManager", "Persisting factory reset request as it could be delayed by %s", this.mSafetyChecker);
                    synchronized (getLockObject()) {
                        lambda$getUserDataUnchecked$2(0).setDelayedFactoryReset(str, z, z2, z3);
                        saveSettingsLocked(0);
                    }
                }
            } catch (IOException | SecurityException e) {
                Slogf.m13w("DevicePolicyManager", "Failed requesting data wipe", e);
                SecurityLog.writeEvent(210023, new Object[0]);
            }
        } catch (Throwable th) {
            SecurityLog.writeEvent(210023, new Object[0]);
            throw th;
        }
    }

    public final void factoryResetIfDelayedEarlier() {
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
            if (lambda$getUserDataUnchecked$2.mFactoryResetFlags == 0) {
                return;
            }
            if (lambda$getUserDataUnchecked$2.mFactoryResetReason == null) {
                Slogf.m26e("DevicePolicyManager", "no persisted reason for factory resetting");
                lambda$getUserDataUnchecked$2.mFactoryResetReason = "requested before boot";
            }
            FactoryResetter build = FactoryResetter.newBuilder(this.mContext).setReason(lambda$getUserDataUnchecked$2.mFactoryResetReason).setForce(true).setWipeEuicc((lambda$getUserDataUnchecked$2.mFactoryResetFlags & 4) != 0).setWipeAdoptableStorage((lambda$getUserDataUnchecked$2.mFactoryResetFlags & 2) != 0).setWipeFactoryResetProtection((lambda$getUserDataUnchecked$2.mFactoryResetFlags & 8) != 0).build();
            Slogf.m22i("DevicePolicyManager", "Factory resetting on boot using " + build);
            try {
                if (!build.factoryReset()) {
                    Slogf.wtf("DevicePolicyManager", "Factory reset using " + build + " failed.");
                }
            } catch (IOException e) {
                Slogf.wtf("DevicePolicyManager", "Could not factory reset using " + build, e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x005c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void forceWipeUser(int i, String str, boolean z) {
        boolean z2;
        try {
            try {
                if (getCurrentForegroundUserId() == i) {
                    this.mInjector.getIActivityManager().switchUser(0);
                }
                z2 = this.mUserManagerInternal.removeUserEvenWhenDisallowed(i);
                try {
                    if (!z2) {
                        Slogf.m14w("DevicePolicyManager", "Couldn't remove user " + i);
                    } else if (isManagedProfile(i) && !z) {
                        sendWipeProfileNotification(str);
                    }
                    if (z2) {
                        return;
                    }
                    SecurityLog.writeEvent(210023, new Object[0]);
                } catch (RemoteException e) {
                    e = e;
                    Slogf.wtf("DevicePolicyManager", "Error forcing wipe user", e);
                    if (z2) {
                        return;
                    }
                    SecurityLog.writeEvent(210023, new Object[0]);
                }
            } catch (Throwable th) {
                th = th;
                if (0 == 0) {
                    SecurityLog.writeEvent(210023, new Object[0]);
                }
                throw th;
            }
        } catch (RemoteException e2) {
            e = e2;
            z2 = false;
        } catch (Throwable th2) {
            th = th2;
            if (0 == 0) {
            }
            throw th;
        }
    }

    public void wipeDataWithReason(String str, int i, String str2, boolean z, boolean z2) {
        CallerIdentity callerIdentity;
        ActiveAdmin activeAdminWithPolicyForUidLocked;
        ActiveAdmin activeAdmin;
        int userId;
        String str3;
        if (this.mHasFeature || hasCallingOrSelfPermission("android.permission.MASTER_CLEAR")) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(str);
            } else {
                callerIdentity = getCallerIdentity();
            }
            boolean isProfileOwnerOfOrganizationOwnedDevice = isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId());
            ComponentName componentName = null;
            if (isPermissionCheckFlagEnabled()) {
                activeAdmin = enforcePermissionAndGetEnforcingAdmin(null, "android.permission.MANAGE_DEVICE_POLICY_WIPE_DATA", callerIdentity.getPackageName(), z2 ? -1 : getAffectedUser(z)).getActiveAdmin();
            } else {
                if (z) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice, "Wiping the entire device can only be done by a profile owner on organization-owned device.");
                }
                if ((i & 2) != 0) {
                    Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice || isFinancedDeviceOwner(callerIdentity), "Only device owners or profile owners of organization-owned device can set WIPE_RESET_PROTECTION_DATA");
                }
                synchronized (getLockObject()) {
                    activeAdminWithPolicyForUidLocked = getActiveAdminWithPolicyForUidLocked(null, 4, callerIdentity.getUid());
                }
                activeAdmin = activeAdminWithPolicyForUidLocked;
            }
            Preconditions.checkCallAuthorization(activeAdmin != null || hasCallingOrSelfPermission("android.permission.MASTER_CLEAR"), "No active admin for user %d and caller %d does not hold MASTER_CLEAR permission", new Object[]{Integer.valueOf(callerIdentity.getUserId()), Integer.valueOf(callerIdentity.getUid())});
            checkCanExecuteOrThrowUnsafe(8);
            String genericWipeReason = TextUtils.isEmpty(str2) ? getGenericWipeReason(isProfileOwnerOfOrganizationOwnedDevice, z) : str2;
            if (activeAdmin != null) {
                userId = activeAdmin.getUserHandle().getIdentifier();
            } else {
                userId = callerIdentity.getUserId();
            }
            Slogf.m20i("DevicePolicyManager", "wipeDataWithReason(%s): admin=%s, user=%d", genericWipeReason, activeAdmin, Integer.valueOf(userId));
            if (isProfileOwnerOfOrganizationOwnedDevice) {
                if (z) {
                    userId = 0;
                } else {
                    final UserHandle of = UserHandle.of(getProfileParentId(userId));
                    this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda34
                        public final void runOrThrow() {
                            DevicePolicyManagerService.this.lambda$wipeDataWithReason$52(of);
                        }
                    });
                }
            }
            DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(11).setInt(i);
            String[] strArr = new String[1];
            strArr[0] = z ? "calledFromParent" : "notCalledFromParent";
            DevicePolicyEventLogger strings = devicePolicyEventLogger.setStrings(strArr);
            if (activeAdmin != null) {
                if (activeAdmin.isPermissionBased) {
                    str3 = callerIdentity.getPackageName();
                    strings.setAdmin(str3);
                } else {
                    ComponentName component = activeAdmin.info.getComponent();
                    String flattenToShortString = component.flattenToShortString();
                    strings.setAdmin(component);
                    componentName = component;
                    str3 = flattenToShortString;
                }
            } else {
                str3 = this.mInjector.getPackageManager().getPackagesForUid(callerIdentity.getUid())[0];
                Slogf.m22i("DevicePolicyManager", "Logging wipeData() event admin as " + str3);
                strings.setAdmin(str3);
                if (this.mInjector.userManagerIsHeadlessSystemUserMode()) {
                    userId = 0;
                }
            }
            strings.write();
            wipeDataNoLock(componentName, i, String.format("DevicePolicyManager.wipeDataWithReason() from %s, organization-owned? %s", str3, Boolean.valueOf(isProfileOwnerOfOrganizationOwnedDevice)), genericWipeReason, userId, z, Boolean.valueOf(z2));
        }
    }

    public final String getGenericWipeReason(boolean z, boolean z2) {
        if (z && !z2) {
            return getUpdatableString("Core.WORK_PROFILE_DELETED_ORG_OWNED_MESSAGE", 17040126, new Object[0]);
        }
        return getUpdatableString("Core.WORK_PROFILE_DELETED_GENERIC_MESSAGE", 17041804, new Object[0]);
    }

    public final void clearOrgOwnedProfileOwnerDeviceWidePolicies(int i) {
        boolean z;
        Slogf.m22i("DevicePolicyManager", "Cleaning up device-wide policies left over from org-owned profile...");
        this.mLockPatternUtils.setDeviceOwnerInfo((String) null);
        this.mInjector.settingsGlobalPutInt("wifi_device_owner_configs_lockdown", 0);
        if (this.mInjector.securityLogGetLoggingEnabledProperty()) {
            this.mSecurityLogMonitor.stop();
            this.mInjector.securityLogSetLoggingEnabledProperty(false);
        }
        setNetworkLoggingActiveInternal(false);
        synchronized (getLockObject()) {
            z = this.mOwners.getSystemUpdatePolicy() != null;
            if (z) {
                this.mOwners.clearSystemUpdatePolicy();
                this.mOwners.writeDeviceOwner();
            }
        }
        if (z) {
            this.mContext.sendBroadcastAsUser(new Intent("android.app.action.SYSTEM_UPDATE_POLICY_CHANGED"), UserHandle.SYSTEM);
        }
        suspendPersonalAppsInternal(i, false);
        int frpManagementAgentUid = getFrpManagementAgentUid();
        if (frpManagementAgentUid > 0) {
            lambda$setFactoryResetProtectionPolicy$55(frpManagementAgentUid);
        }
        this.mLockSettingsInternal.refreshStrongAuthTimeout(i);
        if (isWorkProfileTelephonyFlagEnabled()) {
            clearManagedSubscriptionsPolicy();
            clearLauncherShortcutOverrides();
            updateTelephonyCrossProfileIntentFilters(i, -10000, false);
        }
        Slogf.m22i("DevicePolicyManager", "Cleaning up device-wide policies done.");
    }

    public final void clearManagedSubscriptionsPolicy() {
        unregisterOnSubscriptionsChangedListener();
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        for (int i : subscriptionManager.getActiveSubscriptionIdList(false)) {
            subscriptionManager.setSubscriptionUserHandle(i, null);
        }
    }

    public final void clearLauncherShortcutOverrides() {
        this.mPolicyCache.setLauncherShortcutOverrides(new ArrayList());
    }

    public final void updateTelephonyCrossProfileIntentFilters(int i, int i2, boolean z) {
        try {
            String opPackageName = this.mContext.getOpPackageName();
            if (z) {
                for (DefaultCrossProfileIntentFilter defaultCrossProfileIntentFilter : DefaultCrossProfileIntentFiltersUtils.getDefaultManagedProfileTelephonyFilters()) {
                    IntentFilter intentFilter = defaultCrossProfileIntentFilter.filter.getIntentFilter();
                    if (!this.mIPackageManager.removeCrossProfileIntentFilter(intentFilter, opPackageName, i2, i, defaultCrossProfileIntentFilter.flags)) {
                        Slogf.m14w("DevicePolicyManager", "Failed to remove cross-profile intent filter: " + intentFilter);
                    }
                    this.mIPackageManager.addCrossProfileIntentFilter(intentFilter, opPackageName, i, i2, 2);
                }
                return;
            }
            this.mIPackageManager.clearCrossProfileIntentFilters(i, opPackageName);
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error updating telephony cross profile intent filters", e);
        }
    }

    public final void wipeDataNoLock(final ComponentName componentName, final int i, final String str, final String str2, final int i2, final boolean z, final Boolean bool) {
        String str3;
        wtfIfInLock();
        if (componentName != null) {
            str3 = componentName.getPackageName();
        } else {
            int binderGetCallingUid = this.mInjector.binderGetCallingUid();
            String[] packagesForUid = this.mInjector.getPackageManager().getPackagesForUid(binderGetCallingUid);
            Preconditions.checkState(packagesForUid.length > 0, "Caller %s does not have any associated packages", new Object[]{Integer.valueOf(binderGetCallingUid)});
            str3 = packagesForUid[0];
        }
        final String str4 = str3;
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda99
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$wipeDataNoLock$54(i2, componentName, bool, str4, z, i, str, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$wipeDataNoLock$54(final int i, ComponentName componentName, Boolean bool, String str, boolean z, int i2, String str2, String str3) throws Exception {
        String str4;
        if (i == 0) {
            str4 = "no_factory_reset";
        } else {
            str4 = isManagedProfile(i) ? "no_remove_managed_profile" : "no_remove_user";
        }
        if (isAdminAffectedByRestriction(componentName, str4, i)) {
            throw new SecurityException("Cannot wipe data. " + str4 + " restriction is set for user " + i);
        }
        boolean z2 = i == 0;
        if (bool != null && this.mInjector.isChangeEnabled(242193913L, str, i)) {
            if (bool.booleanValue()) {
                Preconditions.checkState(isDeviceOwnerUserId(i) || (isOrganizationOwnedDeviceWithManagedProfile() && z), "Admin %s does not have permission to factory reset the device.", new Object[]{Integer.valueOf(i)});
                z2 = true;
            } else {
                Preconditions.checkCallAuthorization(!z2, "User %s is a system user and cannot be removed", new Object[]{Integer.valueOf(i)});
                Preconditions.checkState(!(getUserInfo(i).isFull() && this.mUserManager.getAliveUsers().stream().filter(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda187
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$wipeDataNoLock$53;
                        lambda$wipeDataNoLock$53 = DevicePolicyManagerService.lambda$wipeDataNoLock$53(i, (UserInfo) obj);
                        return lambda$wipeDataNoLock$53;
                    }
                }).noneMatch(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda188
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return ((UserInfo) obj).isFull();
                    }
                })), "Removing user %s would leave the device without any active users. Consider factory resetting the device instead.", new Object[]{Integer.valueOf(i)});
                z2 = false;
            }
        }
        if (z2) {
            forceWipeDeviceNoLock((i2 & 1) != 0, str2, (i2 & 4) != 0, (i2 & 2) != 0);
        } else {
            forceWipeUser(i, str3, (i2 & 8) != 0);
        }
    }

    public static /* synthetic */ boolean lambda$wipeDataNoLock$53(int i, UserInfo userInfo) {
        return userInfo.getUserHandle().getIdentifier() != i;
    }

    public final void sendWipeProfileNotification(String str) {
        this.mInjector.getNotificationManager().notify(1001, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17301642).setContentTitle(getWorkProfileDeletedTitle()).setContentText(str).setColor(this.mContext.getColor(17170460)).setStyle(new Notification.BigTextStyle().bigText(str)).build());
    }

    public final String getWorkProfileDeletedTitle() {
        return getUpdatableString("Core.WORK_PROFILE_DELETED_TITLE", 17041803, new Object[0]);
    }

    public final void clearWipeProfileNotification() {
        this.mInjector.getNotificationManager().cancel(1001);
    }

    public void setFactoryResetProtectionPolicy(ComponentName componentName, String str, FactoryResetProtectionPolicy factoryResetProtectionPolicy) {
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        if (this.mHasFeature) {
            if (!isPermissionCheckFlagEnabled()) {
                Preconditions.checkNotNull(componentName, "ComponentName is null");
            }
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            if (!isPermissionCheckFlagEnabled()) {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
            }
            checkCanExecuteOrThrowUnsafe(32);
            final int frpManagementAgentUidOrThrow = getFrpManagementAgentUidOrThrow();
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    profileOwnerOrDeviceOwnerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET", callerIdentity.getPackageName(), -1).getActiveAdmin();
                } else {
                    profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                }
                profileOwnerOrDeviceOwnerLocked.mFactoryResetProtectionPolicy = factoryResetProtectionPolicy;
                saveSettingsLocked(callerIdentity.getUserId());
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda35
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setFactoryResetProtectionPolicy$55(frpManagementAgentUidOrThrow);
                }
            });
            DevicePolicyEventLogger.createEvent(130).setAdmin(callerIdentity.getPackageName()).write();
        }
    }

    /* renamed from: notifyResetProtectionPolicyChanged */
    public final void lambda$setFactoryResetProtectionPolicy$55(int i) {
        this.mContext.sendBroadcastAsUser(new Intent("android.app.action.RESET_PROTECTION_POLICY_CHANGED").addFlags(285212672), UserHandle.getUserHandleForUid(i), "android.permission.MANAGE_FACTORY_RESET_PROTECTION");
    }

    public FactoryResetProtectionPolicy getFactoryResetProtectionPolicy(ComponentName componentName) {
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            int frpManagementAgentUidOrThrow = getFrpManagementAgentUidOrThrow();
            synchronized (getLockObject()) {
                boolean z = false;
                if (componentName == null) {
                    if (frpManagementAgentUidOrThrow == callerIdentity.getUid() || hasCallingPermission("android.permission.MASTER_CLEAR") || hasCallingPermission("android.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET")) {
                        z = true;
                    }
                    Preconditions.checkCallAuthorization(z, "Must be called by the FRP management agent on device");
                    profileOwnerOrDeviceOwnerLocked = m60xd6d5d1e4();
                } else {
                    if (isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) {
                        z = true;
                    }
                    Preconditions.checkCallAuthorization(z);
                    profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                }
            }
            if (profileOwnerOrDeviceOwnerLocked != null) {
                return profileOwnerOrDeviceOwnerLocked.mFactoryResetProtectionPolicy;
            }
            return null;
        }
        return null;
    }

    public final int getFrpManagementAgentUid() {
        PersistentDataBlockManagerInternal persistentDataBlockManagerInternal = this.mInjector.getPersistentDataBlockManagerInternal();
        if (persistentDataBlockManagerInternal != null) {
            return persistentDataBlockManagerInternal.getAllowedUid();
        }
        return -1;
    }

    public final int getFrpManagementAgentUidOrThrow() {
        int frpManagementAgentUid = getFrpManagementAgentUid();
        if (frpManagementAgentUid != -1) {
            return frpManagementAgentUid;
        }
        throw new UnsupportedOperationException("The persistent data block service is not supported on this device");
    }

    public boolean isFactoryResetProtectionPolicySupported() {
        return getFrpManagementAgentUid() != -1;
    }

    public void sendLostModeLocationUpdate(final AndroidFuture<Boolean> androidFuture) {
        final ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        if (!this.mHasFeature) {
            androidFuture.complete(Boolean.FALSE);
            return;
        }
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.TRIGGER_LOST_MODE"));
        synchronized (getLockObject()) {
            if (isHeadlessFlagEnabled()) {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
            } else {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
            }
            Preconditions.checkState(deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked != null, "Lost mode location updates can only be sent on an organization-owned device.");
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda167
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$sendLostModeLocationUpdate$56(deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked, androidFuture);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendLostModeLocationUpdate$56(ActiveAdmin activeAdmin, AndroidFuture androidFuture) throws Exception {
        tryRetrieveAndSendLocationUpdate(activeAdmin, androidFuture, new String[]{"fused", "network", "gps"}, 0);
    }

    public final void tryRetrieveAndSendLocationUpdate(final ActiveAdmin activeAdmin, final AndroidFuture<Boolean> androidFuture, final String[] strArr, final int i) {
        if (i == strArr.length) {
            androidFuture.complete(Boolean.FALSE);
        } else if (this.mInjector.getLocationManager().isProviderEnabled(strArr[i])) {
            this.mInjector.getLocationManager().getCurrentLocation(strArr[i], null, this.mContext.getMainExecutor(), new Consumer() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda184
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DevicePolicyManagerService.this.lambda$tryRetrieveAndSendLocationUpdate$57(activeAdmin, androidFuture, strArr, i, (Location) obj);
                }
            });
        } else {
            tryRetrieveAndSendLocationUpdate(activeAdmin, androidFuture, strArr, i + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryRetrieveAndSendLocationUpdate$57(ActiveAdmin activeAdmin, AndroidFuture androidFuture, String[] strArr, int i, Location location) {
        if (location != null) {
            this.mContext.sendBroadcastAsUser(newLostModeLocationUpdateIntent(activeAdmin, location), activeAdmin.getUserHandle());
            androidFuture.complete(Boolean.TRUE);
            return;
        }
        tryRetrieveAndSendLocationUpdate(activeAdmin, androidFuture, strArr, i + 1);
    }

    public final Intent newLostModeLocationUpdateIntent(ActiveAdmin activeAdmin, Location location) {
        Intent intent = new Intent("android.app.action.LOST_MODE_LOCATION_UPDATE");
        intent.putExtra("android.app.extra.LOST_MODE_LOCATION", location);
        intent.setPackage(activeAdmin.info.getPackageName());
        return intent;
    }

    public void getRemoveWarning(ComponentName componentName, final RemoteCallback remoteCallback, int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                if (activeAdminUncheckedLocked == null) {
                    remoteCallback.sendResult((Bundle) null);
                    return;
                }
                Intent intent = new Intent("android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED");
                intent.setFlags(268435456);
                intent.setComponent(activeAdminUncheckedLocked.info.getComponent());
                this.mContext.sendOrderedBroadcastAsUser(intent, new UserHandle(i), null, new BroadcastReceiver() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.6
                    @Override // android.content.BroadcastReceiver
                    public void onReceive(Context context, Intent intent2) {
                        remoteCallback.sendResult(getResultExtras(false));
                    }
                }, null, -1, null, null);
            }
        }
    }

    public void reportPasswordChanged(PasswordMetrics passwordMetrics, int i) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()));
            if (!isSeparateProfileChallengeEnabled(i)) {
                Preconditions.checkCallAuthorization(!isManagedProfile(i), "You can not set the active password for a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
            }
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            ArraySet arraySet = new ArraySet();
            synchronized (getLockObject()) {
                lambda$getUserDataUnchecked$2.mFailedPasswordAttempts = 0;
                arraySet.add(Integer.valueOf(i));
                arraySet.addAll(updatePasswordValidityCheckpointLocked(i, false));
                arraySet.addAll(updatePasswordExpirationsLocked(i));
                setExpirationAlarmCheckLocked(this.mContext, i, false);
                sendAdminCommandForLockscreenPoliciesLocked("android.app.action.ACTION_PASSWORD_CHANGED", 0, i);
                arraySet.addAll(removeCaApprovalsIfNeeded(i));
                saveSettingsForUsersLocked(arraySet);
            }
            if (this.mInjector.securityLogIsLoggingEnabled()) {
                SecurityLog.writeEvent(210036, new Object[]{Integer.valueOf(passwordMetrics.determineComplexity()), Integer.valueOf(i)});
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    public final Set<Integer> updatePasswordExpirationsLocked(int i) {
        ArraySet arraySet = new ArraySet();
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(i);
        for (int i2 = 0; i2 < activeAdminsForLockscreenPoliciesLocked.size(); i2++) {
            ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i2);
            if (activeAdmin.isPermissionBased || activeAdmin.info.usesPolicy(6)) {
                arraySet.add(Integer.valueOf(activeAdmin.getUserHandle().getIdentifier()));
                long j = activeAdmin.passwordExpirationTimeout;
                activeAdmin.passwordExpirationDate = j > 0 ? System.currentTimeMillis() + j : 0L;
            }
        }
        return arraySet;
    }

    public void reportFailedPasswordAttempt(int i, boolean z) {
        ActiveAdmin activeAdmin;
        boolean z2;
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        if (!isSeparateProfileChallengeEnabled(i)) {
            Preconditions.checkCallAuthorization(!isManagedProfile(i), "You can not report failed password attempt if separate profile challenge is not in place for a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
                lambda$getUserDataUnchecked$2.mFailedPasswordAttempts++;
                saveSettingsLocked(i);
                if (this.mHasFeature) {
                    activeAdmin = getAdminWithMinimumFailedPasswordsForWipeLocked(i, false);
                    int i2 = activeAdmin != null ? activeAdmin.maximumFailedPasswordsForWipe : 0;
                    z2 = i2 > 0 && lambda$getUserDataUnchecked$2.mFailedPasswordAttempts >= i2;
                    sendAdminCommandForLockscreenPoliciesLocked("android.app.action.ACTION_PASSWORD_FAILED", 1, i);
                } else {
                    activeAdmin = null;
                    z2 = false;
                }
            }
            if (z2 && activeAdmin != null) {
                int userIdToWipeForFailedPasswords = getUserIdToWipeForFailedPasswords(activeAdmin);
                Slogf.m22i("DevicePolicyManager", "Max failed password attempts policy reached for admin: " + activeAdmin.info.getComponent().flattenToShortString() + ". Calling wipeData for user " + userIdToWipeForFailedPasswords);
                try {
                    wipeDataNoLock(activeAdmin.info.getComponent(), 0, "reportFailedPasswordAttempt()", getFailedPasswordAttemptWipeMessage(), userIdToWipeForFailedPasswords, z, null);
                } catch (SecurityException e) {
                    Slogf.m13w("DevicePolicyManager", "Failed to wipe user " + userIdToWipeForFailedPasswords + " after max failed password attempts reached.", e);
                }
            }
            if (this.mInjector.securityLogIsLoggingEnabled()) {
                SecurityLog.writeEvent(210007, new Object[]{0, 1});
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final String getFailedPasswordAttemptWipeMessage() {
        return getUpdatableString("Core.WORK_PROFILE_DELETED_FAILED_PASSWORD_ATTEMPTS_MESSAGE", 17041806, new Object[0]);
    }

    public final int getUserIdToWipeForFailedPasswords(ActiveAdmin activeAdmin) {
        int identifier = activeAdmin.getUserHandle().getIdentifier();
        return (!activeAdmin.isPermissionBased && isProfileOwnerOfOrganizationOwnedDevice(activeAdmin.info.getComponent(), identifier)) ? getProfileParentId(identifier) : identifier;
    }

    public void reportSuccessfulPasswordAttempt(final int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        synchronized (getLockObject()) {
            final DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            if (lambda$getUserDataUnchecked$2.mFailedPasswordAttempts != 0 || lambda$getUserDataUnchecked$2.mPasswordOwner >= 0) {
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda105
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$reportSuccessfulPasswordAttempt$58(lambda$getUserDataUnchecked$2, i);
                    }
                });
            }
        }
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{1, 1});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportSuccessfulPasswordAttempt$58(DevicePolicyData devicePolicyData, int i) throws Exception {
        devicePolicyData.mFailedPasswordAttempts = 0;
        devicePolicyData.mPasswordOwner = -1;
        saveSettingsLocked(i);
        if (this.mHasFeature) {
            sendAdminCommandForLockscreenPoliciesLocked("android.app.action.ACTION_PASSWORD_SUCCEEDED", 1, i);
        }
    }

    public void reportFailedBiometricAttempt(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{0, 0});
        }
    }

    public void reportSuccessfulBiometricAttempt(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{1, 0});
        }
    }

    public void reportKeyguardDismissed(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210006, new Object[0]);
        }
    }

    public void reportKeyguardSecured(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN"));
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210008, new Object[0]);
        }
    }

    public ComponentName setGlobalProxy(ComponentName componentName, String str, String str2) {
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                Objects.requireNonNull(componentName, "ComponentName is null");
                final DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
                ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 5);
                for (ComponentName componentName2 : lambda$getUserDataUnchecked$2.mAdminMap.keySet()) {
                    if (lambda$getUserDataUnchecked$2.mAdminMap.get(componentName2).specifiesGlobalProxy && !componentName2.equals(componentName)) {
                        return componentName2;
                    }
                }
                if (UserHandle.getCallingUserId() != 0) {
                    Slogf.m14w("DevicePolicyManager", "Only the owner is allowed to set the global proxy. User " + UserHandle.getCallingUserId() + " is not permitted.");
                    return null;
                }
                if (str == null) {
                    activeAdminForCallerLocked.specifiesGlobalProxy = false;
                    activeAdminForCallerLocked.globalProxySpec = null;
                    activeAdminForCallerLocked.globalProxyExclusionList = null;
                } else {
                    activeAdminForCallerLocked.specifiesGlobalProxy = true;
                    activeAdminForCallerLocked.globalProxySpec = str;
                    activeAdminForCallerLocked.globalProxyExclusionList = str2;
                }
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda145
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setGlobalProxy$59(lambda$getUserDataUnchecked$2);
                    }
                });
                return null;
            }
        }
        return null;
    }

    public ComponentName getGlobalProxyAdmin(int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i) && canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
                int size = lambda$getUserDataUnchecked$2.mAdminList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(i2);
                    if (activeAdmin.specifiesGlobalProxy) {
                        return activeAdmin.info.getComponent();
                    }
                }
                return null;
            }
        }
        return null;
    }

    public void setRecommendedGlobalProxy(ComponentName componentName, final ProxyInfo proxyInfo) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        checkAllUsersAreAffiliatedWithDevice();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda73
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setRecommendedGlobalProxy$60(proxyInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setRecommendedGlobalProxy$60(ProxyInfo proxyInfo) throws Exception {
        this.mInjector.getConnectivityManager().setGlobalProxy(proxyInfo);
    }

    /* renamed from: resetGlobalProxyLocked */
    public final void lambda$setGlobalProxy$59(DevicePolicyData devicePolicyData) {
        int size = devicePolicyData.mAdminList.size();
        for (int i = 0; i < size; i++) {
            ActiveAdmin activeAdmin = devicePolicyData.mAdminList.get(i);
            if (activeAdmin.specifiesGlobalProxy) {
                saveGlobalProxyLocked(activeAdmin.globalProxySpec, activeAdmin.globalProxyExclusionList);
                return;
            }
        }
        saveGlobalProxyLocked(null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x004f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void saveGlobalProxyLocked(String str, String str2) {
        int parseInt;
        ProxyInfo buildDirectProxy;
        if (str2 == null) {
            str2 = "";
        }
        if (str == null) {
            str = "";
        }
        String[] split = str.trim().split(XmlUtils.STRING_ARRAY_SEPARATOR);
        if (split.length > 1) {
            try {
                parseInt = Integer.parseInt(split[1]);
            } catch (NumberFormatException unused) {
            }
            String trim = str2.trim();
            buildDirectProxy = ProxyInfo.buildDirectProxy(split[0], parseInt, ProxyUtils.exclusionStringAsList(trim));
            if (buildDirectProxy.isValid()) {
                Slogf.m26e("DevicePolicyManager", "Invalid proxy properties, ignoring: " + buildDirectProxy.toString());
                return;
            }
            this.mInjector.settingsGlobalPutString("global_http_proxy_host", split[0]);
            this.mInjector.settingsGlobalPutInt("global_http_proxy_port", parseInt);
            this.mInjector.settingsGlobalPutString("global_http_proxy_exclusion_list", trim);
            return;
        }
        parseInt = 8080;
        String trim2 = str2.trim();
        buildDirectProxy = ProxyInfo.buildDirectProxy(split[0], parseInt, ProxyUtils.exclusionStringAsList(trim2));
        if (buildDirectProxy.isValid()) {
        }
    }

    public int setStorageEncryption(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                if (callingUserId != 0) {
                    Slogf.m14w("DevicePolicyManager", "Only owner/system user is allowed to set storage encryption. User " + UserHandle.getCallingUserId() + " is not permitted.");
                    return 0;
                }
                ActiveAdmin activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 7);
                if (isEncryptionSupported()) {
                    if (activeAdminForCallerLocked.encryptionRequested != z) {
                        activeAdminForCallerLocked.encryptionRequested = z;
                        saveSettingsLocked(callingUserId);
                    }
                    DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
                    int size = lambda$getUserDataUnchecked$2.mAdminList.size();
                    boolean z2 = false;
                    for (int i = 0; i < size; i++) {
                        z2 |= lambda$getUserDataUnchecked$2.mAdminList.get(i).encryptionRequested;
                    }
                    setEncryptionRequested(z2);
                    return z2 ? 3 : 1;
                }
                return 0;
            }
        }
        return 0;
    }

    public boolean getStorageEncryption(ComponentName componentName, int i) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            synchronized (getLockObject()) {
                if (callerIdentity.hasAdminComponent()) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                    return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.encryptionRequested : false;
                }
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
                int size = lambda$getUserDataUnchecked$2.mAdminList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (lambda$getUserDataUnchecked$2.mAdminList.get(i2).encryptionRequested) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public int getStorageEncryptionStatus(String str, int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(str), i));
        try {
            boolean z = this.mIPackageManager.getApplicationInfo(str, 0L, i).targetSdkVersion <= 23;
            int encryptionStatus = getEncryptionStatus();
            if (encryptionStatus == 5 && z) {
                return 3;
            }
            return encryptionStatus;
        } catch (RemoteException e) {
            throw new SecurityException(e);
        }
    }

    public final boolean isEncryptionSupported() {
        return getEncryptionStatus() != 0;
    }

    public final int getEncryptionStatus() {
        return this.mInjector.storageManagerIsFileBasedEncryptionEnabled() ? 5 : 0;
    }

    public void setScreenCaptureDisabled(ComponentName componentName, String str, boolean z, boolean z2) {
        CallerIdentity callerIdentity;
        ActiveAdmin parentOfAdminIfRequired;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                callerIdentity = getCallerIdentity(componentName);
                if (z2) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
                } else {
                    Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDeviceOwner(callerIdentity));
                }
            }
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    int identifier = Binder.getCallingUserHandle().getIdentifier();
                    if (z2) {
                        identifier = getProfileParentId(identifier);
                    }
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE", callerIdentity.getPackageName(), identifier).getActiveAdmin();
                } else {
                    parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z2);
                }
                if (parentOfAdminIfRequired.disableScreenCapture != z) {
                    parentOfAdminIfRequired.disableScreenCapture = z;
                    saveSettingsLocked(callerIdentity.getUserId());
                    pushScreenCapturePolicy(callerIdentity.getUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(29).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
        }
    }

    public final void pushScreenCapturePolicy(int i) {
        ActiveAdmin m59x4457a0f1;
        if (isHeadlessFlagEnabled()) {
            m59x4457a0f1 = m59x4457a0f1(this.mUserManagerInternal.getProfileParentId(i));
        } else {
            m59x4457a0f1 = m59x4457a0f1(0);
        }
        if (m59x4457a0f1 != null && m59x4457a0f1.disableScreenCapture) {
            setScreenCaptureDisabled(-1);
            return;
        }
        ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
        if (profileOwnerAdminLocked != null && profileOwnerAdminLocked.disableScreenCapture) {
            setScreenCaptureDisabled(i);
        } else {
            setScreenCaptureDisabled(-10000);
        }
    }

    public final void setScreenCaptureDisabled(int i) {
        if (i == this.mPolicyCache.getScreenCaptureDisallowedUser()) {
            return;
        }
        this.mPolicyCache.setScreenCaptureDisallowedUser(i);
        updateScreenCaptureDisabled();
    }

    public boolean getScreenCaptureDisabled(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            if (z) {
                Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(getCallerIdentity().getUserId()));
            }
            return !this.mPolicyCache.isScreenCaptureAllowed(i);
        }
        return false;
    }

    public final void updateScreenCaptureDisabled() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda153
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$updateScreenCaptureDisabled$61();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateScreenCaptureDisabled$61() {
        try {
            this.mInjector.getIWindowManager().refreshScreenCaptureDisabled();
        } catch (RemoteException e) {
            Slogf.m13w("DevicePolicyManager", "Unable to notify WindowManager.", e);
        }
    }

    public void setNearbyNotificationStreamingPolicy(int i) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerOrDeviceOwnerLocked.mNearbyNotificationStreamingPolicy != i) {
                    profileOwnerOrDeviceOwnerLocked.mNearbyNotificationStreamingPolicy = i;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
        }
    }

    public int getNearbyNotificationStreamingPolicy(int i) {
        int i2;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || hasCallingOrSelfPermission("android.permission.READ_NEARBY_STREAMING_POLICY"));
            Preconditions.checkCallAuthorization(hasCrossUsersPermission(callerIdentity, i));
            synchronized (getLockObject()) {
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
                i2 = deviceOrProfileOwnerAdminLocked != null ? deviceOrProfileOwnerAdminLocked.mNearbyNotificationStreamingPolicy : 0;
            }
            return i2;
        }
        return 0;
    }

    public void setNearbyAppStreamingPolicy(int i) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerOrDeviceOwnerLocked.mNearbyAppStreamingPolicy != i) {
                    profileOwnerOrDeviceOwnerLocked.mNearbyAppStreamingPolicy = i;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
        }
    }

    public int getNearbyAppStreamingPolicy(int i) {
        int i2;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || hasCallingOrSelfPermission("android.permission.READ_NEARBY_STREAMING_POLICY"));
            Preconditions.checkCallAuthorization(hasCrossUsersPermission(callerIdentity, i));
            synchronized (getLockObject()) {
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
                i2 = deviceOrProfileOwnerAdminLocked != null ? deviceOrProfileOwnerAdminLocked.mNearbyAppStreamingPolicy : 0;
            }
            return i2;
        }
        return 0;
    }

    public void setAutoTimeRequired(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            boolean z2 = false;
            Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                Preconditions.checkCallAuthorization(!isManagedProfile(callerIdentity.getUserId()), "Managed profile cannot set auto time required");
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerOrDeviceOwnerLocked.requireAutoTime != z) {
                    profileOwnerOrDeviceOwnerLocked.requireAutoTime = z;
                    saveSettingsLocked(callerIdentity.getUserId());
                    z2 = true;
                }
            }
            if (z2) {
                pushUserRestrictions(callerIdentity.getUserId());
            }
            if (z) {
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda0
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setAutoTimeRequired$62();
                    }
                });
            }
            DevicePolicyEventLogger.createEvent(36).setAdmin(componentName).setBoolean(z).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutoTimeRequired$62() throws Exception {
        this.mInjector.settingsGlobalPutInt("auto_time", 1);
    }

    public boolean getAutoTimeRequired() {
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked == null || !deviceOwnerAdminLocked.requireAutoTime) {
                    for (Integer num : this.mOwners.getProfileOwnerKeys()) {
                        ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(num.intValue());
                        if (profileOwnerAdminLocked != null && profileOwnerAdminLocked.requireAutoTime) {
                            return true;
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void setAutoTimeEnabled(ComponentName componentName, String str, final boolean z) {
        CallerIdentity callerIdentity;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (isPermissionCheckFlagEnabled()) {
                enforcePermission("android.permission.SET_TIME", callerIdentity.getPackageName(), -1);
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isProfileOwnerOnUser0(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda161
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setAutoTimeEnabled$63(z);
                }
            });
            DevicePolicyEventLogger.createEvent(127).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutoTimeEnabled$63(boolean z) throws Exception {
        this.mInjector.settingsGlobalPutInt("auto_time", z ? 1 : 0);
    }

    public boolean getAutoTimeEnabled(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (isPermissionCheckFlagEnabled()) {
                enforceCanQuery("android.permission.SET_TIME", callerIdentity.getPackageName(), -1);
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isProfileOwnerOnUser0(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            }
            return this.mInjector.settingsGlobalGetInt("auto_time", 0) > 0;
        }
        return false;
    }

    public void setAutoTimeZoneEnabled(ComponentName componentName, String str, final boolean z) {
        CallerIdentity callerIdentity;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (useDevicePolicyEngine(callerIdentity, null)) {
                this.mDevicePolicyEngine.setGlobalPolicy(PolicyDefinition.AUTO_TIMEZONE, enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.SET_TIME_ZONE", callerIdentity.getPackageName(), -1), new BooleanPolicyValue(z));
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isProfileOwnerOnUser0(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda20
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setAutoTimeZoneEnabled$64(z);
                    }
                });
            }
            DevicePolicyEventLogger.createEvent(128).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutoTimeZoneEnabled$64(boolean z) throws Exception {
        this.mInjector.settingsGlobalPutInt("auto_time_zone", z ? 1 : 0);
    }

    public boolean getAutoTimeZoneEnabled(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (isPermissionCheckFlagEnabled()) {
                enforceCanQuery("android.permission.SET_TIME_ZONE", callerIdentity.getPackageName(), -1);
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isProfileOwnerOnUser0(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            }
            return this.mInjector.settingsGlobalGetInt("auto_time_zone", 0) > 0;
        }
        return false;
    }

    public void setForceEphemeralUsers(ComponentName componentName, boolean z) {
        throw new UnsupportedOperationException("This method was used by split system user only.");
    }

    public boolean getForceEphemeralUsers(ComponentName componentName) {
        throw new UnsupportedOperationException("This method was used by split system user only.");
    }

    public boolean requestBugreport(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            checkAllUsersAreAffiliatedWithDevice();
            checkCanExecuteOrThrowUnsafe(29);
            if (this.mBugreportCollectionManager.requestBugreport()) {
                DevicePolicyEventLogger.createEvent(53).setAdmin(componentName).write();
                long currentTimeMillis = System.currentTimeMillis();
                synchronized (getLockObject()) {
                    DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
                    if (currentTimeMillis > lambda$getUserDataUnchecked$2.mLastBugReportRequestTime) {
                        lambda$getUserDataUnchecked$2.mLastBugReportRequestTime = currentTimeMillis;
                        saveSettingsLocked(0);
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public void sendDeviceOwnerCommand(String str, Bundle bundle) {
        int deviceOwnerUserId;
        ComponentName deviceOwnerComponent;
        synchronized (getLockObject()) {
            deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
            deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
        }
        sendActiveAdminCommand(str, bundle, deviceOwnerUserId, deviceOwnerComponent, false);
    }

    public void sendDeviceOwnerOrProfileOwnerCommand(String str, Bundle bundle, int i) {
        ComponentName componentName;
        boolean z;
        boolean z2 = false;
        int i2 = i == -1 ? 0 : i;
        if (str.equals("android.app.action.NETWORK_LOGS_AVAILABLE")) {
            componentName = resolveDelegateReceiver("delegation-network-logging", str, i2);
            z2 = true;
        } else {
            componentName = null;
        }
        if (str.equals("android.app.action.SECURITY_LOGS_AVAILABLE")) {
            componentName = resolveDelegateReceiver("delegation-security-logging", str, i2);
            z = true;
        } else {
            z = z2;
        }
        if (componentName == null) {
            componentName = getOwnerComponent(i2);
        }
        sendActiveAdminCommand(str, bundle, i2, componentName, z);
    }

    public final void sendProfileOwnerCommand(String str, Bundle bundle, int i) {
        sendActiveAdminCommand(str, bundle, i, this.mOwners.getProfileOwnerComponent(i), false);
    }

    public final void sendActiveAdminCommand(String str, Bundle bundle, int i, ComponentName componentName, boolean z) {
        Intent intent = new Intent(str);
        intent.setComponent(componentName);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (z) {
            intent.addFlags(268435456);
        }
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i));
    }

    public final void sendOwnerChangedBroadcast(String str, int i) {
        this.mContext.sendBroadcastAsUser(new Intent(str).addFlags(16777216), UserHandle.of(i));
    }

    public void sendBugreportToDeviceOwner(Uri uri, String str) {
        synchronized (getLockObject()) {
            Intent intent = new Intent("android.app.action.BUGREPORT_SHARE");
            intent.setComponent(this.mOwners.getDeviceOwnerComponent());
            intent.setDataAndType(uri, "application/vnd.android.bugreport");
            intent.putExtra("android.app.extra.BUGREPORT_HASH", str);
            intent.setFlags(1);
            UriGrantsManagerInternal uriGrantsManagerInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
            uriGrantsManagerInternal.grantUriPermissionUncheckedFromIntent(uriGrantsManagerInternal.checkGrantUriPermissionFromIntent(intent, 2000, this.mOwners.getDeviceOwnerComponent().getPackageName(), this.mOwners.getDeviceOwnerUserId()), null);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.of(this.mOwners.getDeviceOwnerUserId()));
        }
    }

    public void setDeviceOwnerRemoteBugreportUriAndHash(String str, String str2) {
        synchronized (getLockObject()) {
            this.mOwners.setDeviceOwnerRemoteBugreportUriAndHash(str, str2);
        }
    }

    public Pair<String, String> getDeviceOwnerRemoteBugreportUriAndHash() {
        Pair<String, String> pair;
        synchronized (getLockObject()) {
            String deviceOwnerRemoteBugreportUri = this.mOwners.getDeviceOwnerRemoteBugreportUri();
            pair = deviceOwnerRemoteBugreportUri == null ? null : new Pair<>(deviceOwnerRemoteBugreportUri, this.mOwners.getDeviceOwnerRemoteBugreportHash());
        }
        return pair;
    }

    public void setCameraDisabled(ComponentName componentName, String str, boolean z, boolean z2) {
        CallerIdentity callerIdentity;
        ActiveAdmin activeAdminForCallerLocked;
        ActiveAdmin activeAdmin;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            int userId = callerIdentity.getUserId();
            checkCanExecuteOrThrowUnsafe(31);
            if (isPermissionCheckFlagEnabled()) {
                activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_CAMERA", callerIdentity.getPackageName(), getProfileParentUserIfRequested(userId, z2)).getActiveAdmin();
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                if (z2) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
                }
                synchronized (getLockObject()) {
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 8, z2);
                }
                activeAdmin = activeAdminForCallerLocked;
            }
            synchronized (getLockObject()) {
                if (activeAdmin.disableCamera != z) {
                    activeAdmin.disableCamera = z;
                    saveSettingsLocked(userId);
                }
            }
            pushUserRestrictions(userId);
            int profileParentId = z2 ? getProfileParentId(userId) : userId;
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210034, new Object[]{componentName.getPackageName(), Integer.valueOf(userId), Integer.valueOf(profileParentId), Integer.valueOf(z ? 1 : 0)});
            }
            DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(30).setAdmin(callerIdentity.getPackageName()).setBoolean(z);
            String[] strArr = new String[1];
            strArr[0] = z2 ? "calledFromParent" : "notCalledFromParent";
            devicePolicyEventLogger.setStrings(strArr).write();
        }
    }

    public boolean getCameraDisabled(ComponentName componentName, String str, int i, boolean z) {
        CallerIdentity callerIdentity;
        List<ActiveAdmin> activeAdminsForAffectedUserLocked;
        boolean z2 = false;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (isPermissionCheckFlagEnabled()) {
                Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i) || isCameraServerUid(callerIdentity) || hasPermission("android.permission.MANAGE_DEVICE_POLICY_CAMERA", callerIdentity.getPackageName(), i) || hasPermission("android.permission.QUERY_ADMIN_POLICY", callerIdentity.getPackageName()));
            } else {
                Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i) || isCameraServerUid(callerIdentity));
                if (z) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId()));
                }
            }
            synchronized (getLockObject()) {
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    if (activeAdminUncheckedLocked != null && activeAdminUncheckedLocked.disableCamera) {
                        z2 = true;
                    }
                    return z2;
                }
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked == null || !deviceOwnerAdminLocked.disableCamera) {
                    if (z) {
                        i = getProfileParentId(i);
                    }
                    if (isPermissionCheckFlagEnabled()) {
                        activeAdminsForAffectedUserLocked = getActiveAdminsForAffectedUserInclPermissionBasedAdminLocked(i);
                    } else {
                        activeAdminsForAffectedUserLocked = getActiveAdminsForAffectedUserLocked(i);
                    }
                    for (ActiveAdmin activeAdmin : activeAdminsForAffectedUserLocked) {
                        if (activeAdmin.disableCamera) {
                            return true;
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void setKeyguardDisabledFeatures(ComponentName componentName, String str, int i, boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin activeAdminForCallerLocked;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            int userId = callerIdentity.getUserId();
            int profileParentId = z ? getProfileParentId(userId) : userId;
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    activeAdminForCallerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", callerIdentity.getPackageName(), profileParentId).getActiveAdmin();
                } else {
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 9, z);
                }
                if (isManagedProfile(userId)) {
                    if (z) {
                        i = isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) ? i & 950 : i & FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED;
                    } else {
                        i &= 440;
                    }
                }
                if (activeAdminForCallerLocked.disabledKeyguardFeatures != i) {
                    activeAdminForCallerLocked.disabledKeyguardFeatures = i;
                    saveSettingsLocked(userId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210021, new Object[]{callerIdentity.getPackageName(), Integer.valueOf(userId), Integer.valueOf(profileParentId), Integer.valueOf(i)});
            }
            DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(9).setAdmin(callerIdentity.getPackageName()).setInt(i);
            String[] strArr = new String[1];
            strArr[0] = z ? "calledFromParent" : "notCalledFromParent";
            devicePolicyEventLogger.setStrings(strArr).write();
        }
    }

    public int getKeyguardDisabledFeatures(ComponentName componentName, int i, boolean z) {
        List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked;
        int i2;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            Preconditions.checkCallAuthorization(componentName == null || isCallingFromPackage(componentName.getPackageName(), callerIdentity.getUid()) || isSystemUid(callerIdentity));
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (getLockObject()) {
                    if (componentName != null) {
                        ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                        return activeAdminUncheckedLocked != null ? activeAdminUncheckedLocked.disabledKeyguardFeatures : 0;
                    }
                    if (!z && isManagedProfile(i)) {
                        activeAdminsForLockscreenPoliciesLocked = getUserDataUnchecked(i).mAdminList;
                    } else {
                        activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                    }
                    int size = activeAdminsForLockscreenPoliciesLocked.size();
                    int i3 = 0;
                    for (int i4 = 0; i4 < size; i4++) {
                        ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i4);
                        int identifier = activeAdmin.getUserHandle().getIdentifier();
                        if (!(!z && identifier == i) && isManagedProfile(identifier)) {
                            i2 = activeAdmin.disabledKeyguardFeatures & 950;
                            i3 |= i2;
                        }
                        i2 = activeAdmin.disabledKeyguardFeatures;
                        i3 |= i2;
                    }
                    return i3;
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
        return 0;
    }

    public void setKeepUninstalledPackages(ComponentName componentName, String str, List<String> list) {
        if (this.mHasFeature) {
            Objects.requireNonNull(list, "packageList is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && isDefaultDeviceOwner(callerIdentity)) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-keep-uninstalled-packages")));
            checkCanExecuteOrThrowUnsafe(17);
            synchronized (getLockObject()) {
                getDeviceOwnerAdminLocked().keepUninstalledPackages = list;
                saveSettingsLocked(callerIdentity.getUserId());
                this.mInjector.getPackageManagerInternal().setKeepUninstalledPackages(list);
            }
            DevicePolicyEventLogger.createEvent(61).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings((String[]) list.toArray(new String[0])).write();
        }
    }

    public List<String> getKeepUninstalledPackages(ComponentName componentName, String str) {
        List<String> keepUninstalledPackagesLocked;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && isDefaultDeviceOwner(callerIdentity)) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-keep-uninstalled-packages")));
            synchronized (getLockObject()) {
                keepUninstalledPackagesLocked = getKeepUninstalledPackagesLocked();
            }
            return keepUninstalledPackagesLocked;
        }
        return null;
    }

    public final List<String> getKeepUninstalledPackagesLocked() {
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        if (deviceOwnerAdminLocked != null) {
            return deviceOwnerAdminLocked.keepUninstalledPackages;
        }
        return null;
    }

    public final void logMissingFeatureAction(String str) {
        Slogf.m14w("DevicePolicyManager", str + " because device does not have the android.software.device_admin feature.");
    }

    public boolean setDeviceOwner(ComponentName componentName, final int i, boolean z) {
        int currentForegroundUserId;
        if (!this.mHasFeature) {
            logMissingFeatureAction("Cannot set " + ComponentName.flattenToShortString(componentName) + " as device owner for user " + i);
            return false;
        }
        Preconditions.checkArgument(componentName != null);
        CallerIdentity callerIdentity = getCallerIdentity();
        boolean z2 = !isAdb(callerIdentity) || hasIncompatibleAccountsOnAnyUser();
        if (!z2) {
            synchronized (getLockObject()) {
                if (!isAdminTestOnlyLocked(componentName, i) && hasAccountsOnAnyUser()) {
                    Slogf.m14w("DevicePolicyManager", "Non test-only owner can't be installed with existing accounts.");
                    return false;
                }
            }
        }
        synchronized (getLockObject()) {
            enforceCanSetDeviceOwnerLocked(callerIdentity, componentName, i, z2);
            boolean isPackageInstalledForUser = isPackageInstalledForUser(componentName.getPackageName(), i);
            Preconditions.checkArgument(isPackageInstalledForUser, "Invalid component " + componentName + " for device owner");
            ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
            boolean z3 = (activeAdminUncheckedLocked == null || lambda$getUserDataUnchecked$2(i).mRemovingAdmins.contains(componentName)) ? false : true;
            Preconditions.checkArgument(z3, "Not active admin: " + componentName);
            toggleBackupServiceActive(0, false);
            if (isAdb(callerIdentity)) {
                MetricsLogger.action(this.mContext, 617, "device-owner");
                DevicePolicyEventLogger.createEvent(82).setAdmin(componentName).setStrings(new String[]{"device-owner"}).write();
            }
            this.mOwners.setDeviceOwner(componentName, i);
            this.mOwners.writeDeviceOwner();
            setDeviceOwnershipSystemPropertyLocked();
            if (isAdb(callerIdentity)) {
                activeAdminUncheckedLocked.mAdminCanGrantSensorsPermissions = true;
                this.mPolicyCache.setAdminCanGrantSensorsPermissions(true);
                saveSettingsLocked(i);
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda140
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setDeviceOwner$65(i);
                }
            });
            this.mDeviceAdminServiceController.startServiceForAdmin(componentName.getPackageName(), i, "set-device-owner");
            Slogf.m22i("DevicePolicyManager", "Device owner set: " + componentName + " on user " + i);
        }
        if (z && this.mInjector.userManagerIsHeadlessSystemUserMode()) {
            synchronized (getLockObject()) {
                currentForegroundUserId = getCurrentForegroundUserId();
            }
            Slogf.m22i("DevicePolicyManager", "setDeviceOwner(): setting " + componentName + " as profile owner on user " + currentForegroundUserId);
            manageUserUnchecked(componentName, componentName, currentForegroundUserId, null, false);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDeviceOwner$65(int i) throws Exception {
        int[] userIds;
        if (isHeadlessFlagEnabled()) {
            for (int i2 : this.mUserManagerInternal.getUserIds()) {
                this.mUserManager.setUserRestriction("no_add_managed_profile", true, UserHandle.of(i2));
                this.mUserManager.setUserRestriction("no_add_clone_profile", true, UserHandle.of(i2));
            }
        } else {
            this.mUserManager.setUserRestriction("no_add_managed_profile", true, UserHandle.of(i));
            this.mUserManager.setUserRestriction("no_add_clone_profile", true, UserHandle.of(i));
        }
        sendOwnerChangedBroadcast("android.app.action.DEVICE_OWNER_CHANGED", i);
    }

    public boolean hasDeviceOwner() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || canManageUsers(callerIdentity) || isFinancedDeviceOwner(callerIdentity) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        return this.mOwners.hasDeviceOwner();
    }

    public boolean isDeviceOwner(ActiveAdmin activeAdmin) {
        return isDeviceOwner(activeAdmin.info.getComponent(), activeAdmin.getUserHandle().getIdentifier());
    }

    public boolean isDeviceOwner(ComponentName componentName, int i) {
        boolean z;
        synchronized (getLockObject()) {
            z = this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == i && this.mOwners.getDeviceOwnerComponent().equals(componentName);
        }
        return z;
    }

    public final boolean isDefaultDeviceOwner(CallerIdentity callerIdentity) {
        boolean z;
        synchronized (getLockObject()) {
            z = isDeviceOwnerLocked(callerIdentity) && getDeviceOwnerTypeLocked(this.mOwners.getDeviceOwnerPackageName()) == 0;
        }
        return z;
    }

    public boolean isDeviceOwner(CallerIdentity callerIdentity) {
        boolean isDeviceOwnerLocked;
        synchronized (getLockObject()) {
            isDeviceOwnerLocked = isDeviceOwnerLocked(callerIdentity);
        }
        return isDeviceOwnerLocked;
    }

    public final boolean isDeviceOwnerLocked(CallerIdentity callerIdentity) {
        if (this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == callerIdentity.getUserId()) {
            if (callerIdentity.hasAdminComponent()) {
                return this.mOwners.getDeviceOwnerComponent().equals(callerIdentity.getComponentName());
            }
            return isUidDeviceOwnerLocked(callerIdentity.getUid());
        }
        return false;
    }

    public final boolean isDeviceOwnerUserId(int i) {
        boolean z;
        synchronized (getLockObject()) {
            z = this.mOwners.getDeviceOwnerComponent() != null && this.mOwners.getDeviceOwnerUserId() == i;
        }
        return z;
    }

    public boolean isProfileOwner(ComponentName componentName, final int i) {
        return componentName != null && componentName.equals((ComponentName) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda148
            public final Object getOrThrow() {
                ComponentName lambda$isProfileOwner$66;
                lambda$isProfileOwner$66 = DevicePolicyManagerService.this.lambda$isProfileOwner$66(i);
                return lambda$isProfileOwner$66;
            }
        }));
    }

    public boolean isProfileOwner(final CallerIdentity callerIdentity) {
        synchronized (getLockObject()) {
            ComponentName componentName = (ComponentName) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda53
                public final Object getOrThrow() {
                    ComponentName lambda$isProfileOwner$67;
                    lambda$isProfileOwner$67 = DevicePolicyManagerService.this.lambda$isProfileOwner$67(callerIdentity);
                    return lambda$isProfileOwner$67;
                }
            });
            if (componentName == null) {
                return false;
            }
            if (callerIdentity.hasAdminComponent()) {
                return componentName.equals(callerIdentity.getComponentName());
            }
            return isUidProfileOwnerLocked(callerIdentity.getUid());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ComponentName lambda$isProfileOwner$67(CallerIdentity callerIdentity) throws Exception {
        return lambda$isProfileOwner$66(callerIdentity.getUserId());
    }

    public final boolean isUidProfileOwnerLocked(int i) {
        ensureLocked();
        int userId = UserHandle.getUserId(i);
        ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(userId);
        if (profileOwnerComponent == null) {
            return false;
        }
        Iterator<ActiveAdmin> it = lambda$getUserDataUnchecked$2(userId).mAdminList.iterator();
        while (it.hasNext()) {
            ActiveAdmin next = it.next();
            ComponentName component = next.info.getComponent();
            if (next.getUid() == i && profileOwnerComponent.equals(component)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasProfileOwner(int i) {
        boolean hasProfileOwner;
        synchronized (getLockObject()) {
            hasProfileOwner = this.mOwners.hasProfileOwner(i);
        }
        return hasProfileOwner;
    }

    public final boolean isProfileOwnerOfOrganizationOwnedDevice(CallerIdentity callerIdentity) {
        return isProfileOwner(callerIdentity) && isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId());
    }

    public final boolean isProfileOwnerOfOrganizationOwnedDevice(int i) {
        boolean isProfileOwnerOfOrganizationOwnedDevice;
        synchronized (getLockObject()) {
            isProfileOwnerOfOrganizationOwnedDevice = this.mOwners.isProfileOwnerOfOrganizationOwnedDevice(i);
        }
        return isProfileOwnerOfOrganizationOwnedDevice;
    }

    public final boolean isProfileOwnerOfOrganizationOwnedDevice(ComponentName componentName, int i) {
        return isProfileOwner(componentName, i) && isProfileOwnerOfOrganizationOwnedDevice(i);
    }

    public final boolean isProfileOwnerOnUser0(CallerIdentity callerIdentity) {
        return isProfileOwner(callerIdentity) && callerIdentity.getUserHandle().isSystem();
    }

    public final boolean isPackage(CallerIdentity callerIdentity, String str) {
        return isCallingFromPackage(str, callerIdentity.getUid());
    }

    public ComponentName getDeviceOwnerComponent(boolean z) {
        if (this.mHasFeature) {
            if (!z) {
                Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
            }
            synchronized (getLockObject()) {
                if (this.mOwners.hasDeviceOwner()) {
                    if (!z || this.mInjector.userHandleGetCallingUserId() == this.mOwners.getDeviceOwnerUserId()) {
                        return this.mOwners.getDeviceOwnerComponent();
                    }
                    return null;
                }
                return null;
            }
        }
        return null;
    }

    public final int getDeviceOwnerUserIdUncheckedLocked() {
        if (this.mOwners.hasDeviceOwner()) {
            return this.mOwners.getDeviceOwnerUserId();
        }
        return -10000;
    }

    public int getDeviceOwnerUserId() {
        int deviceOwnerUserIdUncheckedLocked;
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
            synchronized (getLockObject()) {
                deviceOwnerUserIdUncheckedLocked = getDeviceOwnerUserIdUncheckedLocked();
            }
            return deviceOwnerUserIdUncheckedLocked;
        }
        return -10000;
    }

    public final int getMainUserId() {
        int mainUserId = this.mUserManagerInternal.getMainUserId();
        if (mainUserId == -10000) {
            Slogf.m30d("DevicePolicyManager", "getMainUserId(): no main user, returning USER_SYSTEM");
            return 0;
        }
        return mainUserId;
    }

    public String getDeviceOwnerName() {
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
            synchronized (getLockObject()) {
                if (this.mOwners.hasDeviceOwner()) {
                    return getApplicationLabel(this.mOwners.getDeviceOwnerPackageName(), 0);
                }
                return null;
            }
        }
        return null;
    }

    @VisibleForTesting
    public ActiveAdmin getDeviceOwnerAdminLocked() {
        ensureLocked();
        ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
        if (deviceOwnerComponent == null) {
            return null;
        }
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(this.mOwners.getDeviceOwnerUserId());
        int size = lambda$getUserDataUnchecked$2.mAdminList.size();
        for (int i = 0; i < size; i++) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(i);
            if (deviceOwnerComponent.equals(activeAdmin.info.getComponent())) {
                return activeAdmin;
            }
        }
        Slogf.wtf("DevicePolicyManager", "Active admin for device owner not found. component=" + deviceOwnerComponent);
        return null;
    }

    @Deprecated
    public ActiveAdmin getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(int i) {
        ensureLocked();
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        return deviceOwnerAdminLocked == null ? getProfileOwnerOfOrganizationOwnedDeviceLocked(i) : deviceOwnerAdminLocked;
    }

    public ActiveAdmin getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked() {
        ensureLocked();
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        return deviceOwnerAdminLocked == null ? getProfileOwnerOfOrganizationOwnedDeviceLocked() : deviceOwnerAdminLocked;
    }

    /* renamed from: getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceOrSystemPermissionBasedAdminLocked */
    public ActiveAdmin m60xd6d5d1e4() {
        ensureLocked();
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
        return (isPermissionCheckFlagEnabled() && deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked == null) ? lambda$getUserDataUnchecked$2(0).mPermissionBasedAdmin : deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
    }

    /* renamed from: getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceParentLocked */
    public ActiveAdmin m59x4457a0f1(int i) {
        ensureLocked();
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        if (deviceOwnerAdminLocked != null) {
            return deviceOwnerAdminLocked;
        }
        ActiveAdmin profileOwnerOfOrganizationOwnedDeviceLocked = getProfileOwnerOfOrganizationOwnedDeviceLocked(i);
        if (profileOwnerOfOrganizationOwnedDeviceLocked != null) {
            return profileOwnerOfOrganizationOwnedDeviceLocked.getParentActiveAdmin();
        }
        return null;
    }

    public void clearDeviceOwner(String str) {
        Objects.requireNonNull(str, "packageName is null");
        CallerIdentity callerIdentity = getCallerIdentity(str);
        synchronized (getLockObject()) {
            final ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
            final int deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
            if (!this.mOwners.hasDeviceOwner() || !deviceOwnerComponent.getPackageName().equals(str) || deviceOwnerUserId != callerIdentity.getUserId()) {
                throw new SecurityException("clearDeviceOwner can only be called by the device owner");
            }
            enforceUserUnlocked(deviceOwnerUserId);
            long j = lambda$getUserDataUnchecked$2(deviceOwnerUserId).mPasswordTokenHandle;
            if (j != 0) {
                this.mLockPatternUtils.removeEscrowToken(j, deviceOwnerUserId);
            }
            final ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda146
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$clearDeviceOwner$68(deviceOwnerAdminLocked, deviceOwnerUserId, deviceOwnerComponent);
                }
            });
            Slogf.m22i("DevicePolicyManager", "Device owner removed: " + deviceOwnerComponent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearDeviceOwner$68(ActiveAdmin activeAdmin, int i, ComponentName componentName) throws Exception {
        clearDeviceOwnerLocked(activeAdmin, i);
        lambda$removeActiveAdmin$13(componentName, i);
        sendOwnerChangedBroadcast("android.app.action.DEVICE_OWNER_CHANGED", i);
    }

    public final void clearOverrideApnUnchecked() {
        if (this.mHasTelephonyFeature) {
            setOverrideApnsEnabledUnchecked(false);
            List<ApnSetting> overrideApnsUnchecked = getOverrideApnsUnchecked();
            for (int i = 0; i < overrideApnsUnchecked.size(); i++) {
                removeOverrideApnUnchecked(overrideApnsUnchecked.get(i).getId());
            }
        }
    }

    public final void clearManagedProfileApnUnchecked() {
        if (this.mHasTelephonyFeature) {
            for (ApnSetting apnSetting : getOverrideApnsUnchecked()) {
                if (apnSetting.getApnTypeBitmask() == 16384) {
                    removeOverrideApnUnchecked(apnSetting.getId());
                }
            }
        }
    }

    public final void clearDeviceOwnerLocked(ActiveAdmin activeAdmin, int i) {
        String deviceOwnerPackageName = this.mOwners.getDeviceOwnerPackageName();
        if (deviceOwnerPackageName != null) {
            this.mDeviceAdminServiceController.stopServiceForAdmin(deviceOwnerPackageName, i, "clear-device-owner");
        }
        if (activeAdmin != null) {
            activeAdmin.disableCamera = false;
            activeAdmin.userRestrictions = null;
            activeAdmin.defaultEnabledRestrictionsAlreadySet.clear();
            activeAdmin.forceEphemeralUsers = false;
            activeAdmin.isNetworkLoggingEnabled = false;
            activeAdmin.requireAutoTime = false;
            this.mUserManagerInternal.setForceEphemeralUsers(false);
        }
        lambda$getUserDataUnchecked$2(i).mCurrentInputMethodSet = false;
        saveSettingsLocked(i);
        this.mPolicyCache.onUserRemoved(i);
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
        lambda$getUserDataUnchecked$2.mLastSecurityLogRetrievalTime = -1L;
        lambda$getUserDataUnchecked$2.mLastBugReportRequestTime = -1L;
        lambda$getUserDataUnchecked$2.mLastNetworkLogsRetrievalTime = -1L;
        saveSettingsLocked(0);
        clearUserPoliciesLocked(i);
        clearOverrideApnUnchecked();
        clearApplicationRestrictions(i);
        this.mInjector.getPackageManagerInternal().clearBlockUninstallForUser(i);
        this.mOwners.clearDeviceOwner();
        this.mOwners.writeDeviceOwner();
        clearDeviceOwnerUserRestriction(UserHandle.of(i));
        this.mInjector.securityLogSetLoggingEnabledProperty(false);
        this.mSecurityLogMonitor.stop();
        setNetworkLoggingActiveInternal(false);
        deleteTransferOwnershipBundleLocked(i);
        toggleBackupServiceActive(0, true);
        pushUserControlDisabledPackagesLocked(i);
        setGlobalSettingDeviceOwnerType(0);
    }

    public final void clearApplicationRestrictions(final int i) {
        this.mBackgroundHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$clearApplicationRestrictions$69(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearApplicationRestrictions$69(int i) {
        List<PackageInfo> installedPackages = this.mInjector.getPackageManager(i).getInstalledPackages(786432);
        UserHandle of = UserHandle.of(i);
        for (PackageInfo packageInfo : installedPackages) {
            this.mInjector.getUserManager().setApplicationRestrictions(packageInfo.packageName, null, of);
        }
    }

    public boolean setProfileOwner(ComponentName componentName, final int i) {
        if (!this.mHasFeature) {
            logMissingFeatureAction("Cannot set " + ComponentName.flattenToShortString(componentName) + " as profile owner for user " + i);
            return false;
        }
        Preconditions.checkArgument(componentName != null);
        CallerIdentity callerIdentity = getCallerIdentity();
        boolean hasIncompatibleAccountsOrNonAdbNoLock = hasIncompatibleAccountsOrNonAdbNoLock(callerIdentity, i, componentName);
        synchronized (getLockObject()) {
            enforceCanSetProfileOwnerLocked(callerIdentity, componentName, i, hasIncompatibleAccountsOrNonAdbNoLock);
            final ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
            boolean z = (!isPackageInstalledForUser(componentName.getPackageName(), i) || activeAdminUncheckedLocked == null || lambda$getUserDataUnchecked$2(i).mRemovingAdmins.contains(componentName)) ? false : true;
            Preconditions.checkArgument(z, "Not active admin: " + componentName);
            int profileParentId = getProfileParentId(i);
            if (profileParentId != i && this.mUserManager.hasUserRestriction("no_add_managed_profile", UserHandle.of(profileParentId))) {
                Slogf.m22i("DevicePolicyManager", "Cannot set profile owner because of restriction.");
                return false;
            }
            if (isAdb(callerIdentity)) {
                MetricsLogger.action(this.mContext, 617, "profile-owner");
                DevicePolicyEventLogger.createEvent(82).setAdmin(componentName).setStrings(new String[]{"profile-owner"}).write();
            }
            toggleBackupServiceActive(i, false);
            this.mOwners.setProfileOwner(componentName, i);
            this.mOwners.writeProfileOwner(i);
            Slogf.m22i("DevicePolicyManager", "Profile owner set: " + componentName + " on user " + i);
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda173
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setProfileOwner$70(i, activeAdminUncheckedLocked);
                }
            });
            this.mDeviceAdminServiceController.startServiceForAdmin(componentName.getPackageName(), i, "set-profile-owner");
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProfileOwner$70(int i, ActiveAdmin activeAdmin) throws Exception {
        if (this.mUserManager.isManagedProfile(i)) {
            maybeSetDefaultRestrictionsForAdminLocked(i, activeAdmin);
            ensureUnknownSourcesRestrictionForProfileOwnerLocked(i, activeAdmin, true);
        }
        sendOwnerChangedBroadcast("android.app.action.PROFILE_OWNER_CHANGED", i);
    }

    public final void toggleBackupServiceActive(int i, boolean z) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                if (this.mInjector.getIBackupManager() != null) {
                    this.mInjector.getIBackupManager().setBackupServiceActive(i, z);
                }
            } catch (RemoteException e) {
                Object[] objArr = new Object[1];
                objArr[0] = z ? "activating" : "deactivating";
                throw new IllegalStateException(String.format("Failed %s backup service.", objArr), e);
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public void clearProfileOwner(final ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            final int userId = callerIdentity.getUserId();
            Preconditions.checkCallingUser(!isManagedProfile(userId));
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            enforceUserUnlocked(userId);
            synchronized (getLockObject()) {
                final ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda62
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$clearProfileOwner$71(profileOwnerLocked, userId, componentName);
                    }
                });
                Slogf.m22i("DevicePolicyManager", "Profile owner " + componentName + " removed from user " + userId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearProfileOwner$71(ActiveAdmin activeAdmin, int i, ComponentName componentName) throws Exception {
        clearProfileOwnerLocked(activeAdmin, i);
        lambda$removeActiveAdmin$13(componentName, i);
        sendOwnerChangedBroadcast("android.app.action.PROFILE_OWNER_CHANGED", i);
    }

    public void clearProfileOwnerLocked(ActiveAdmin activeAdmin, int i) {
        String profileOwnerPackage = this.mOwners.getProfileOwnerPackage(i);
        if (profileOwnerPackage != null) {
            this.mDeviceAdminServiceController.stopServiceForAdmin(profileOwnerPackage, i, "clear-profile-owner");
        }
        if (activeAdmin != null) {
            activeAdmin.disableCamera = false;
            activeAdmin.userRestrictions = null;
            activeAdmin.defaultEnabledRestrictionsAlreadySet.clear();
        }
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        lambda$getUserDataUnchecked$2.mCurrentInputMethodSet = false;
        lambda$getUserDataUnchecked$2.mOwnerInstalledCaCerts.clear();
        saveSettingsLocked(i);
        clearUserPoliciesLocked(i);
        clearApplicationRestrictions(i);
        this.mOwners.removeProfileOwner(i);
        this.mOwners.writeProfileOwner(i);
        deleteTransferOwnershipBundleLocked(i);
        toggleBackupServiceActive(i, true);
        applyProfileRestrictionsIfDeviceOwnerLocked();
        setNetworkLoggingActiveInternal(false);
    }

    public void setDeviceOwnerLockScreenInfo(ComponentName componentName, final CharSequence charSequence) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda93
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setDeviceOwnerLockScreenInfo$72(charSequence);
                }
            });
            DevicePolicyEventLogger.createEvent(42).setAdmin(callerIdentity.getComponentName()).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDeviceOwnerLockScreenInfo$72(CharSequence charSequence) throws Exception {
        this.mLockPatternUtils.setDeviceOwnerInfo(charSequence != null ? charSequence.toString() : null);
    }

    public CharSequence getDeviceOwnerLockScreenInfo() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        return (CharSequence) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda75
            public final Object getOrThrow() {
                String lambda$getDeviceOwnerLockScreenInfo$73;
                lambda$getDeviceOwnerLockScreenInfo$73 = DevicePolicyManagerService.this.lambda$getDeviceOwnerLockScreenInfo$73();
                return lambda$getDeviceOwnerLockScreenInfo$73;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDeviceOwnerLockScreenInfo$73() throws Exception {
        return this.mLockPatternUtils.getDeviceOwnerInfo();
    }

    public final void clearUserPoliciesLocked(int i) {
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        lambda$getUserDataUnchecked$2.mPermissionPolicy = 0;
        lambda$getUserDataUnchecked$2.mDelegationMap.clear();
        lambda$getUserDataUnchecked$2.mStatusBarDisabled = false;
        lambda$getUserDataUnchecked$2.mSecondaryLockscreenEnabled = false;
        lambda$getUserDataUnchecked$2.mUserProvisioningState = 0;
        lambda$getUserDataUnchecked$2.mAffiliationIds.clear();
        lambda$getUserDataUnchecked$2.mLockTaskPackages.clear();
        updateLockTaskPackagesLocked(this.mContext, lambda$getUserDataUnchecked$2.mLockTaskPackages, i);
        lambda$getUserDataUnchecked$2.mLockTaskFeatures = 0;
        saveSettingsLocked(i);
        try {
            this.mIPermissionManager.updatePermissionFlagsForAllApps(4, 0, i);
            pushUserRestrictions(i);
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Failing in updatePermissionFlagsForAllApps", e);
        }
    }

    public boolean hasUserSetupCompleted() {
        return hasUserSetupCompleted(this.mInjector.userHandleGetCallingUserId());
    }

    public final boolean hasUserSetupCompleted(int i) {
        if (this.mHasFeature) {
            return this.mInjector.hasUserSetupCompleted(lambda$getUserDataUnchecked$2(i));
        }
        return true;
    }

    public final boolean hasPaired(int i) {
        if (this.mHasFeature) {
            return lambda$getUserDataUnchecked$2(i).mPaired;
        }
        return true;
    }

    public int getUserProvisioningState(int i) {
        boolean z = false;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(canManageUsers(callerIdentity) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
            if (i != callerIdentity.getUserId()) {
                if (canManageUsers(callerIdentity) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS")) {
                    z = true;
                }
                Preconditions.checkCallAuthorization(z);
            }
            return lambda$getUserDataUnchecked$2(i).mUserProvisioningState;
        }
        return 0;
    }

    public void setUserProvisioningState(int i, int i2) {
        boolean hasProfileOwner;
        int managedUserId;
        if (!this.mHasFeature) {
            logMissingFeatureAction("Cannot set provisioning state " + i + " for user " + i2);
            return;
        }
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        CallerIdentity callerIdentity = getCallerIdentity();
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            int deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
            boolean z = false;
            if (i2 != deviceOwnerUserId && !(hasProfileOwner = this.mOwners.hasProfileOwner(i2)) && (managedUserId = getManagedUserId(i2)) < 0 && i != 0) {
                Slogf.m12w("DevicePolicyManager", "setUserProvisioningState(newState=%d, userId=%d) failed: deviceOwnerId=%d, hasProfileOwner=%b, managedUserId=%d, err=%s", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(deviceOwnerUserId), Boolean.valueOf(hasProfileOwner), Integer.valueOf(managedUserId), "Not allowed to change provisioning state unless a device or profile owner is set.");
                throw new IllegalStateException("Not allowed to change provisioning state unless a device or profile owner is set.");
            }
            synchronized (getLockObject()) {
                if (!isAdb(callerIdentity)) {
                    z = true;
                } else if (getUserProvisioningState(i2) != 0 || i != 3) {
                    throw new IllegalStateException("Not allowed to change provisioning state unless current provisioning state is unmanaged, and new stateis finalized.");
                }
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i2);
                if (z) {
                    checkUserProvisioningStateTransition(lambda$getUserDataUnchecked$2.mUserProvisioningState, i);
                }
                lambda$getUserDataUnchecked$2.mUserProvisioningState = i;
                saveSettingsLocked(i2);
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final void checkUserProvisioningStateTransition(int i, int i2) {
        if (i != 0) {
            if (i == 1 || i == 2) {
                if (i2 == 3) {
                    return;
                }
            } else if (i != 4) {
                if (i == 5 && i2 == 0) {
                    return;
                }
            } else if (i2 == 5) {
                return;
            }
        } else if (i2 != 0) {
            return;
        }
        throw new IllegalStateException("Cannot move to user provisioning state [" + i2 + "] from state [" + i + "]");
    }

    public void setProfileEnabled(ComponentName componentName) {
        if (!this.mHasFeature) {
            logMissingFeatureAction("Cannot enable profile for " + ComponentName.flattenToShortString(componentName));
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        final int userId = callerIdentity.getUserId();
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        Preconditions.checkCallingUser(isManagedProfile(userId));
        synchronized (getLockObject()) {
            if (getUserInfo(userId).isEnabled()) {
                Slogf.m26e("DevicePolicyManager", "setProfileEnabled is called when the profile is already enabled");
            } else {
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda117
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setProfileEnabled$74(userId);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProfileEnabled$74(int i) throws Exception {
        this.mUserManager.setUserEnabled(i);
        UserInfo profileParent = this.mUserManager.getProfileParent(i);
        Intent intent = new Intent("android.intent.action.MANAGED_PROFILE_ADDED");
        intent.putExtra("android.intent.extra.USER", new UserHandle(i));
        UserHandle userHandle = new UserHandle(profileParent.id);
        this.mLocalService.broadcastIntentToManifestReceivers(intent, userHandle, true);
        intent.addFlags(1342177280);
        this.mContext.sendBroadcastAsUser(intent, userHandle);
    }

    public void setProfileName(ComponentName componentName, final String str) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda49
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setProfileName$75(callerIdentity, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProfileName$75(CallerIdentity callerIdentity, String str) throws Exception {
        this.mUserManager.setUserName(callerIdentity.getUserId(), str);
        DevicePolicyEventLogger.createEvent(40).setAdmin(callerIdentity.getComponentName()).write();
    }

    /* renamed from: getProfileOwnerAsUser */
    public ComponentName lambda$isProfileOwner$66(int i) {
        ComponentName profileOwnerComponent;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasCrossUsersPermission(callerIdentity, i) || hasFullCrossUsersPermission(callerIdentity, i));
            synchronized (getLockObject()) {
                profileOwnerComponent = this.mOwners.getProfileOwnerComponent(i);
            }
            return profileOwnerComponent;
        }
        return null;
    }

    @VisibleForTesting
    public ActiveAdmin getProfileOwnerAdminLocked(int i) {
        ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(i);
        if (profileOwnerComponent == null) {
            return null;
        }
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        int size = lambda$getUserDataUnchecked$2.mAdminList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(i2);
            if (profileOwnerComponent.equals(activeAdmin.info.getComponent())) {
                return activeAdmin;
            }
        }
        return null;
    }

    public final ActiveAdmin getDeviceOrProfileOwnerAdminLocked(int i) {
        ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
        return (profileOwnerAdminLocked == null && getDeviceOwnerUserIdUncheckedLocked() == i) ? getDeviceOwnerAdminLocked() : profileOwnerAdminLocked;
    }

    @GuardedBy({"getLockObject()"})
    public ActiveAdmin getProfileOwnerOfOrganizationOwnedDeviceLocked(final int i) {
        return (ActiveAdmin) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda104
            public final Object getOrThrow() {
                ActiveAdmin lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$76;
                lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$76 = DevicePolicyManagerService.this.lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$76(i);
                return lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$76;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ActiveAdmin lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$76(int i) throws Exception {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            if (userInfo.isManagedProfile() && lambda$isProfileOwner$66(userInfo.id) != null && isProfileOwnerOfOrganizationOwnedDevice(userInfo.id)) {
                return getActiveAdminUncheckedLocked(lambda$isProfileOwner$66(userInfo.id), userInfo.id);
            }
        }
        return null;
    }

    @GuardedBy({"getLockObject()"})
    public ActiveAdmin getProfileOwnerOfOrganizationOwnedDeviceLocked() {
        return (ActiveAdmin) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda126
            public final Object getOrThrow() {
                ActiveAdmin lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$77;
                lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$77 = DevicePolicyManagerService.this.lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$77();
                return lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$77;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ActiveAdmin lambda$getProfileOwnerOfOrganizationOwnedDeviceLocked$77() throws Exception {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (userInfo.isManagedProfile() && lambda$isProfileOwner$66(userInfo.id) != null && isProfileOwnerOfOrganizationOwnedDevice(userInfo.id)) {
                return getActiveAdminUncheckedLocked(lambda$isProfileOwner$66(userInfo.id), userInfo.id);
            }
        }
        return null;
    }

    public ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle userHandle) {
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
                ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(userHandle.getIdentifier());
                if (this.mConstants.USE_TEST_ADMIN_AS_SUPERVISION_COMPONENT) {
                    if (isAdminTestOnlyLocked(deviceOwnerComponent, userHandle.getIdentifier())) {
                        return deviceOwnerComponent;
                    }
                    if (isAdminTestOnlyLocked(profileOwnerComponent, userHandle.getIdentifier())) {
                        return profileOwnerComponent;
                    }
                }
                if (isSupervisionComponentLocked(profileOwnerComponent)) {
                    return profileOwnerComponent;
                }
                if (isSupervisionComponentLocked(deviceOwnerComponent)) {
                    return deviceOwnerComponent;
                }
                return null;
            }
        }
        return null;
    }

    public boolean isSupervisionComponent(ComponentName componentName) {
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                if (this.mConstants.USE_TEST_ADMIN_AS_SUPERVISION_COMPONENT && isAdminTestOnlyLocked(componentName, getCallerIdentity().getUserId())) {
                    return true;
                }
                return isSupervisionComponentLocked(componentName);
            }
        }
        return false;
    }

    public final boolean isSupervisionComponentLocked(ComponentName componentName) {
        if (componentName == null) {
            return false;
        }
        String string = this.mContext.getResources().getString(17039902);
        if (string == null || !componentName.equals(ComponentName.unflattenFromString(string))) {
            return componentName.getPackageName().equals(this.mContext.getResources().getString(17039420));
        }
        return true;
    }

    public String getProfileOwnerName(int i) {
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
            return getProfileOwnerNameUnchecked(i);
        }
        return null;
    }

    public final String getProfileOwnerNameUnchecked(int i) {
        ComponentName lambda$isProfileOwner$66 = lambda$isProfileOwner$66(i);
        if (lambda$isProfileOwner$66 == null) {
            return null;
        }
        return getApplicationLabel(lambda$isProfileOwner$66.getPackageName(), i);
    }

    public final int getOrganizationOwnedProfileUserId() {
        UserInfo[] userInfos;
        for (UserInfo userInfo : this.mUserManagerInternal.getUserInfos()) {
            if (userInfo.isManagedProfile() && isProfileOwnerOfOrganizationOwnedDevice(userInfo.id)) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    public boolean isOrganizationOwnedDeviceWithManagedProfile() {
        return this.mHasFeature && getOrganizationOwnedProfileUserId() != -10000;
    }

    public boolean checkDeviceIdentifierAccess(String str, int i, int i2) {
        ensureCallerIdentityMatchesIfNotSystem(str, i, i2, getCallerIdentity());
        if (doesPackageMatchUid(str, i2) && hasPermission("android.permission.READ_PHONE_STATE", i, i2)) {
            return hasDeviceIdAccessUnchecked(str, i2);
        }
        return false;
    }

    @VisibleForTesting
    public boolean hasDeviceIdAccessUnchecked(String str, int i) {
        int userId = UserHandle.getUserId(i);
        if (isPermissionCheckFlagEnabled()) {
            return hasPermission("android.permission.MANAGE_DEVICE_POLICY_CERTIFICATES", str, userId);
        }
        ComponentName deviceOwnerComponent = getDeviceOwnerComponent(true);
        if (deviceOwnerComponent == null || !(deviceOwnerComponent.getPackageName().equals(str) || isCallerDelegate(str, i, "delegation-cert-install"))) {
            ComponentName lambda$isProfileOwner$66 = lambda$isProfileOwner$66(userId);
            return (lambda$isProfileOwner$66 != null && (lambda$isProfileOwner$66.getPackageName().equals(str) || isCallerDelegate(str, i, "delegation-cert-install"))) && (isProfileOwnerOfOrganizationOwnedDevice(userId) || isUserAffiliatedWithDevice(userId));
        }
        return true;
    }

    public final boolean doesPackageMatchUid(String str, int i) {
        try {
            ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0L, UserHandle.getUserId(i));
            if (applicationInfo == null) {
                Slogf.m12w("DevicePolicyManager", "appInfo could not be found for package %s", str);
                return false;
            }
            int i2 = applicationInfo.uid;
            if (i == i2) {
                return true;
            }
            String format = String.format("Package %s (uid=%d) does not match provided uid %d", str, Integer.valueOf(i2), Integer.valueOf(i));
            Slogf.m14w("DevicePolicyManager", format);
            throw new SecurityException(format);
        } catch (RemoteException e) {
            Slogf.m23e("DevicePolicyManager", e, "Exception caught obtaining appInfo for package %s", str);
            return false;
        }
    }

    public final void ensureCallerIdentityMatchesIfNotSystem(String str, int i, int i2, CallerIdentity callerIdentity) {
        int uid = callerIdentity.getUid();
        int binderGetCallingPid = this.mInjector.binderGetCallingPid();
        if (UserHandle.getAppId(uid) >= 10000) {
            if (uid == i2 && binderGetCallingPid == i) {
                return;
            }
            String format = String.format("Calling uid %d, pid %d cannot check device identifier access for package %s (uid=%d, pid=%d)", Integer.valueOf(uid), Integer.valueOf(binderGetCallingPid), str, Integer.valueOf(i2), Integer.valueOf(i));
            Slogf.m14w("DevicePolicyManager", format);
            throw new SecurityException(format);
        }
    }

    public final String getApplicationLabel(final String str, final int i) {
        return (String) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda137
            public final Object getOrThrow() {
                String lambda$getApplicationLabel$78;
                lambda$getApplicationLabel$78 = DevicePolicyManagerService.this.lambda$getApplicationLabel$78(i, str);
                return lambda$getApplicationLabel$78;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getApplicationLabel$78(int i, String str) throws Exception {
        try {
            Context createPackageContextAsUser = this.mContext.createPackageContextAsUser(str, 0, UserHandle.of(i));
            ApplicationInfo applicationInfo = createPackageContextAsUser.getApplicationInfo();
            CharSequence loadUnsafeLabel = applicationInfo != null ? applicationInfo.loadUnsafeLabel(createPackageContextAsUser.getPackageManager()) : null;
            if (loadUnsafeLabel != null) {
                return loadUnsafeLabel.toString();
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            Slogf.m10w("DevicePolicyManager", e, "%s is not installed for user %d", str, Integer.valueOf(i));
            return null;
        }
    }

    public final void enforceCanSetProfileOwnerLocked(CallerIdentity callerIdentity, ComponentName componentName, int i, boolean z) {
        UserInfo userInfo = getUserInfo(i);
        if (userInfo == null) {
            throw new IllegalArgumentException("Attempted to set profile owner for invalid userId: " + i);
        } else if (userInfo.isGuest()) {
            throw new IllegalStateException("Cannot set a profile owner on a guest");
        } else {
            if (this.mOwners.hasProfileOwner(i)) {
                throw new IllegalStateException("Trying to set the profile owner, but profile owner is already set.");
            }
            if (this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == i) {
                throw new IllegalStateException("Trying to set the profile owner, but the user already has a device owner.");
            }
            if (isAdb(callerIdentity)) {
                if ((this.mIsWatch || hasUserSetupCompleted(i)) && z) {
                    throw new IllegalStateException("Not allowed to set the profile owner because there are already some accounts on the profile");
                }
                return;
            }
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
            if (this.mIsWatch || hasUserSetupCompleted(i)) {
                Preconditions.checkState(isSystemUid(callerIdentity), "Cannot set the profile owner on a user which is already set-up");
                if (this.mIsWatch || isSupervisionComponentLocked(componentName)) {
                    return;
                }
                throw new IllegalStateException("Unable to set non-default profile owner post-setup " + componentName);
            }
        }
    }

    public final void enforceCanSetDeviceOwnerLocked(CallerIdentity callerIdentity, ComponentName componentName, int i, boolean z) {
        if (!isAdb(callerIdentity)) {
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        }
        int checkDeviceOwnerProvisioningPreConditionLocked = checkDeviceOwnerProvisioningPreConditionLocked(componentName, i, callerIdentity.getUserId(), isAdb(callerIdentity), z);
        if (checkDeviceOwnerProvisioningPreConditionLocked != 0) {
            throw new IllegalStateException(computeProvisioningErrorString(checkDeviceOwnerProvisioningPreConditionLocked, i));
        }
    }

    public static String computeProvisioningErrorString(int i, int i2) {
        switch (i) {
            case 0:
                return "OK";
            case 1:
                return "Trying to set the device owner, but device owner is already set.";
            case 2:
                return "Trying to set the device owner, but the user already has a profile owner.";
            case 3:
                return "User " + i2 + " not running.";
            case 4:
                return "Cannot set the device owner if the device is already set-up.";
            case 5:
                return "Not allowed to set the device owner because there are already several users on the device.";
            case 6:
                return "Not allowed to set the device owner because there are already some accounts on the device.";
            case 7:
                return "User " + i2 + " is not system user.";
            case 8:
                return "Not allowed to set the device owner because this device has already paired.";
            default:
                return "Unexpected @ProvisioningPreCondition: " + i;
        }
    }

    public final void enforceUserUnlocked(int i) {
        Preconditions.checkState(this.mUserManager.isUserUnlocked(i), "User must be running and unlocked");
    }

    public final void enforceUserUnlocked(int i, boolean z) {
        if (z) {
            enforceUserUnlocked(getProfileParentId(i));
        } else {
            enforceUserUnlocked(i);
        }
    }

    public final boolean canManageUsers(CallerIdentity callerIdentity) {
        return hasCallingOrSelfPermission("android.permission.MANAGE_USERS");
    }

    public final boolean canQueryAdminPolicy(CallerIdentity callerIdentity) {
        return hasCallingOrSelfPermission("android.permission.QUERY_ADMIN_POLICY");
    }

    public final boolean hasPermission(String str, int i, int i2) {
        return this.mContext.checkPermission(str, i, i2) == 0;
    }

    public final boolean hasCallingPermission(String str) {
        return this.mContext.checkCallingPermission(str) == 0;
    }

    public final boolean hasCallingOrSelfPermission(String str) {
        return this.mContext.checkCallingOrSelfPermission(str) == 0;
    }

    public final boolean hasPermissionForPreflight(CallerIdentity callerIdentity, String str) {
        return PermissionChecker.checkPermissionForPreflight(this.mContext, str, this.mInjector.binderGetCallingPid(), callerIdentity.getUid(), this.mContext.getPackageName()) == 0;
    }

    public final boolean hasFullCrossUsersPermission(CallerIdentity callerIdentity, int i) {
        return i == callerIdentity.getUserId() || isSystemUid(callerIdentity) || isRootUid(callerIdentity) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
    }

    public final boolean hasCrossUsersPermission(CallerIdentity callerIdentity, int i) {
        return i == callerIdentity.getUserId() || isSystemUid(callerIdentity) || isRootUid(callerIdentity) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS");
    }

    public final boolean canUserUseLockTaskLocked(int i) {
        if (isUserAffiliatedWithDeviceLocked(i)) {
            return true;
        }
        if (this.mOwners.hasDeviceOwner()) {
            return false;
        }
        return (isPermissionCheckFlagEnabled() || lambda$isProfileOwner$66(i) != null) && !isManagedProfile(i);
    }

    public final void enforceCanCallLockTaskLocked(CallerIdentity callerIdentity) {
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity));
        int userId = callerIdentity.getUserId();
        if (canUserUseLockTaskLocked(userId)) {
            return;
        }
        throw new SecurityException("User " + userId + " is not allowed to use lock task");
    }

    public final void enforceCanQueryLockTaskLocked(ComponentName componentName, String str) {
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        int userId = callerIdentity.getUserId();
        enforceCanQuery("android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", callerIdentity.getPackageName(), userId);
        if (canUserUseLockTaskLocked(userId)) {
            return;
        }
        throw new SecurityException("User " + userId + " is not allowed to use lock task");
    }

    public final EnforcingAdmin enforceCanCallLockTaskLocked(ComponentName componentName, String str) {
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        int userId = callerIdentity.getUserId();
        EnforcingAdmin enforcePermissionAndGetEnforcingAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", callerIdentity.getPackageName(), userId);
        if (canUserUseLockTaskLocked(userId)) {
            return enforcePermissionAndGetEnforcingAdmin;
        }
        throw new SecurityException("User " + userId + " is not allowed to use lock task");
    }

    public final boolean isSystemUid(CallerIdentity callerIdentity) {
        return UserHandle.isSameApp(callerIdentity.getUid(), 1000);
    }

    public final boolean isRootUid(CallerIdentity callerIdentity) {
        return UserHandle.isSameApp(callerIdentity.getUid(), 0);
    }

    public final boolean isShellUid(CallerIdentity callerIdentity) {
        return UserHandle.isSameApp(callerIdentity.getUid(), 2000);
    }

    public final boolean isCameraServerUid(CallerIdentity callerIdentity) {
        return UserHandle.isSameApp(callerIdentity.getUid(), 1047);
    }

    public final int getCurrentForegroundUserId() {
        try {
            UserInfo currentUser = this.mInjector.getIActivityManager().getCurrentUser();
            if (currentUser == null) {
                Slogf.wtf("DevicePolicyManager", "getCurrentForegroundUserId(): mInjector.getIActivityManager().getCurrentUser() returned null, please ignore when running unit tests");
                return ActivityManager.getCurrentUser();
            }
            return currentUser.id;
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "cannot get current user", e);
            return -10000;
        }
    }

    public List<UserHandle> listForegroundAffiliatedUsers() {
        checkIsDeviceOwner(getCallerIdentity());
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda45
            public final Object getOrThrow() {
                List lambda$listForegroundAffiliatedUsers$79;
                lambda$listForegroundAffiliatedUsers$79 = DevicePolicyManagerService.this.lambda$listForegroundAffiliatedUsers$79();
                return lambda$listForegroundAffiliatedUsers$79;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$listForegroundAffiliatedUsers$79() throws Exception {
        boolean isUserAffiliatedWithDeviceLocked;
        int currentForegroundUserId = getCurrentForegroundUserId();
        synchronized (getLockObject()) {
            isUserAffiliatedWithDeviceLocked = isUserAffiliatedWithDeviceLocked(currentForegroundUserId);
        }
        if (!isUserAffiliatedWithDeviceLocked) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(UserHandle.of(currentForegroundUserId));
        return arrayList;
    }

    public int getProfileParentId(final int i) {
        return ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda131
            public final Object getOrThrow() {
                Integer lambda$getProfileParentId$80;
                lambda$getProfileParentId$80 = DevicePolicyManagerService.this.lambda$getProfileParentId$80(i);
                return lambda$getProfileParentId$80;
            }
        })).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getProfileParentId$80(int i) throws Exception {
        UserInfo profileParent = this.mUserManager.getProfileParent(i);
        if (profileParent != null) {
            i = profileParent.id;
        }
        return Integer.valueOf(i);
    }

    public final int getProfileParentUserIfRequested(int i, boolean z) {
        return z ? getProfileParentId(i) : i;
    }

    public final int getCredentialOwner(final int i, final boolean z) {
        return ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda18
            public final Object getOrThrow() {
                Integer lambda$getCredentialOwner$81;
                lambda$getCredentialOwner$81 = DevicePolicyManagerService.this.lambda$getCredentialOwner$81(i, z);
                return lambda$getCredentialOwner$81;
            }
        })).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getCredentialOwner$81(int i, boolean z) throws Exception {
        UserInfo profileParent;
        if (z && (profileParent = this.mUserManager.getProfileParent(i)) != null) {
            i = profileParent.id;
        }
        return Integer.valueOf(this.mUserManager.getCredentialOwnerProfile(i));
    }

    public final boolean isManagedProfile(int i) {
        UserInfo userInfo = getUserInfo(i);
        return userInfo != null && userInfo.isManagedProfile();
    }

    public final void enableIfNecessary(String str, int i) {
        try {
            if (this.mIPackageManager.getApplicationInfo(str, 32768L, i).enabledSetting == 4) {
                this.mIPackageManager.setApplicationEnabledSetting(str, 0, 1, i, "DevicePolicyManager");
            }
        } catch (RemoteException unused) {
        }
    }

    public final void dumpPerUserData(IndentingPrintWriter indentingPrintWriter) {
        int size = this.mUserData.size();
        for (int i = 0; i < size; i++) {
            int keyAt = this.mUserData.keyAt(i);
            lambda$getUserDataUnchecked$2(keyAt).dump(indentingPrintWriter);
            indentingPrintWriter.println();
            if (keyAt == 0) {
                indentingPrintWriter.increaseIndent();
                PersonalAppsSuspensionHelper.forUser(this.mContext, keyAt).dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            } else {
                Slogf.m30d("DevicePolicyManager", "skipping PersonalAppsSuspensionHelper.dump() for user " + keyAt);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "DevicePolicyManager", printWriter)) {
            final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            try {
                indentingPrintWriter.println("Current Device Policy Manager state:");
                indentingPrintWriter.increaseIndent();
                dumpImmutableState(indentingPrintWriter);
                synchronized (getLockObject()) {
                    this.mOwners.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                    this.mDeviceAdminServiceController.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                    dumpPerUserData(indentingPrintWriter);
                    indentingPrintWriter.println();
                    this.mConstants.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                    this.mStatLogger.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Encryption Status: " + getEncryptionStatusName(getEncryptionStatus()));
                    indentingPrintWriter.println("Logout user: " + getLogoutUserIdUnchecked());
                    indentingPrintWriter.println();
                    if (this.mPendingUserCreatedCallbackTokens.isEmpty()) {
                        indentingPrintWriter.println("no pending user created callback tokens");
                    } else {
                        int size = this.mPendingUserCreatedCallbackTokens.size();
                        Object[] objArr = new Object[2];
                        objArr[0] = Integer.valueOf(size);
                        objArr[1] = size == 1 ? "" : "s";
                        indentingPrintWriter.printf("%d pending user created callback token%s\n", objArr);
                    }
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Keep profiles running: " + this.mKeepProfilesRunning);
                    indentingPrintWriter.println();
                    this.mPolicyCache.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                    this.mStateCache.dump(indentingPrintWriter);
                    indentingPrintWriter.println();
                }
                synchronized (this.mSubscriptionsChangedListenerLock) {
                    indentingPrintWriter.println("Subscription changed listener : " + this.mSubscriptionsChangedListener);
                }
                indentingPrintWriter.println("Flag enable_work_profile_telephony : " + isWorkProfileTelephonyFlagEnabled());
                this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda59
                    @Override // java.lang.Runnable
                    public final void run() {
                        DevicePolicyManagerService.this.lambda$dump$82(indentingPrintWriter);
                    }
                });
                dumpResources(indentingPrintWriter);
                indentingPrintWriter.close();
            } catch (Throwable th) {
                try {
                    indentingPrintWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
    }

    /* renamed from: handleDump */
    public final void lambda$dump$82(IndentingPrintWriter indentingPrintWriter) {
        if (this.mNetworkLoggingNotificationUserId != -10000) {
            indentingPrintWriter.println("mNetworkLoggingNotificationUserId:  " + this.mNetworkLoggingNotificationUserId);
        }
    }

    public final void dumpImmutableState(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Immutable state:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.printf("mHasFeature=%b\n", new Object[]{Boolean.valueOf(this.mHasFeature)});
        indentingPrintWriter.printf("mIsWatch=%b\n", new Object[]{Boolean.valueOf(this.mIsWatch)});
        indentingPrintWriter.printf("mIsAutomotive=%b\n", new Object[]{Boolean.valueOf(this.mIsAutomotive)});
        indentingPrintWriter.printf("mHasTelephonyFeature=%b\n", new Object[]{Boolean.valueOf(this.mHasTelephonyFeature)});
        indentingPrintWriter.printf("mSafetyChecker=%s\n", new Object[]{this.mSafetyChecker});
        indentingPrintWriter.decreaseIndent();
    }

    public final void dumpResources(IndentingPrintWriter indentingPrintWriter) {
        this.mOverlayPackagesProvider.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.println("Other overlayable app resources");
        indentingPrintWriter.increaseIndent();
        dumpResources(indentingPrintWriter, this.mContext, "cross_profile_apps", 17236166);
        dumpResources(indentingPrintWriter, this.mContext, "vendor_cross_profile_apps", 17236205);
        dumpResources(indentingPrintWriter, this.mContext, "config_packagesExemptFromSuspension", 17236114);
        dumpResources(indentingPrintWriter, this.mContext, "policy_exempt_apps", 17236190);
        dumpResources(indentingPrintWriter, this.mContext, "vendor_policy_exempt_apps", 17236209);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
    }

    public static void dumpResources(IndentingPrintWriter indentingPrintWriter, Context context, String str, int i) {
        dumpApps(indentingPrintWriter, str, context.getResources().getStringArray(i));
    }

    public static void dumpApps(IndentingPrintWriter indentingPrintWriter, String str, String[] strArr) {
        dumpApps(indentingPrintWriter, str, Arrays.asList(strArr));
    }

    public static void dumpApps(IndentingPrintWriter indentingPrintWriter, String str, List list) {
        if (list == null || list.isEmpty()) {
            indentingPrintWriter.printf("%s: empty\n", new Object[]{str});
            return;
        }
        int size = list.size();
        Object[] objArr = new Object[3];
        objArr[0] = str;
        objArr[1] = Integer.valueOf(size);
        objArr[2] = size == 1 ? "" : "s";
        indentingPrintWriter.printf("%s: %d app%s\n", objArr);
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < size; i++) {
            indentingPrintWriter.printf("%d: %s\n", new Object[]{Integer.valueOf(i), list.get(i)});
        }
        indentingPrintWriter.decreaseIndent();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new DevicePolicyManagerServiceShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void addPersistentPreferredActivity(ComponentName componentName, String str, IntentFilter intentFilter, ComponentName componentName2) {
        CallerIdentity callerIdentity;
        Injector injector;
        EnforcingAdmin enforcingAdminForCaller;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        boolean z = false;
        if (useDevicePolicyEngine(callerIdentity, null)) {
            if (componentName == null) {
                enforcingAdminForCaller = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", callerIdentity.getPackageName(), userId);
            } else {
                if (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity)) {
                    z = true;
                }
                Preconditions.checkCallAuthorization(z);
                enforcingAdminForCaller = getEnforcingAdminForCaller(componentName, str);
            }
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.PERSISTENT_PREFERRED_ACTIVITY(intentFilter), enforcingAdminForCaller, new ComponentNamePolicyValue(componentName2), userId);
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            if (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity)) {
                z = true;
            }
            Preconditions.checkCallAuthorization(z);
            synchronized (getLockObject()) {
                long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                try {
                    this.mIPackageManager.addPersistentPreferredActivity(intentFilter, componentName2, userId);
                    this.mIPackageManager.flushPackageRestrictionsAsUser(userId);
                    injector = this.mInjector;
                } catch (RemoteException e) {
                    Slog.wtf("DevicePolicyManager", "Error adding persistent preferred activity", e);
                    injector = this.mInjector;
                }
                injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
        DevicePolicyEventLogger.createEvent(52).setAdmin(callerIdentity.getPackageName()).setStrings(componentName2 != null ? componentName2.getPackageName() : null, getIntentFilterActions(intentFilter)).write();
    }

    public void clearPackagePersistentPreferredActivities(ComponentName componentName, String str, String str2) {
        CallerIdentity callerIdentity;
        Injector injector;
        EnforcingAdmin enforcingAdminForCaller;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        boolean z = false;
        if (useDevicePolicyEngine(callerIdentity, null)) {
            if (componentName == null) {
                enforcingAdminForCaller = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_LOCK_TASK", callerIdentity.getPackageName(), userId);
            } else {
                if (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity)) {
                    z = true;
                }
                Preconditions.checkCallAuthorization(z);
                enforcingAdminForCaller = getEnforcingAdminForCaller(componentName, str);
            }
            clearPackagePersistentPreferredActivitiesFromPolicyEngine(enforcingAdminForCaller, str2, userId);
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        if (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity)) {
            z = true;
        }
        Preconditions.checkCallAuthorization(z);
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                this.mIPackageManager.clearPackagePersistentPreferredActivities(str2, userId);
                this.mIPackageManager.flushPackageRestrictionsAsUser(userId);
                injector = this.mInjector;
            } catch (RemoteException e) {
                Slogf.wtf("DevicePolicyManager", "Error when clearing package persistent preferred activities", e);
                injector = this.mInjector;
            }
            injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final void clearPackagePersistentPreferredActivitiesFromPolicyEngine(EnforcingAdmin enforcingAdmin, String str, int i) {
        Iterator<PolicyKey> it = this.mDevicePolicyEngine.getLocalPolicyKeysSetByAdmin(PolicyDefinition.GENERIC_PERSISTENT_PREFERRED_ACTIVITY, enforcingAdmin, i).iterator();
        while (it.hasNext()) {
            IntentFilterPolicyKey intentFilterPolicyKey = (PolicyKey) it.next();
            if (!(intentFilterPolicyKey instanceof IntentFilterPolicyKey)) {
                throw new IllegalStateException("PolicyKey for PERSISTENT_PREFERRED_ACTIVITY is notof type PersistentPreferredActivityPolicyKey");
            }
            IntentFilter intentFilter = intentFilterPolicyKey.getIntentFilter();
            Objects.requireNonNull(intentFilter);
            ComponentName componentName = (ComponentName) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.PERSISTENT_PREFERRED_ACTIVITY(intentFilter), enforcingAdmin, i);
            if (componentName != null && componentName.getPackageName().equals(str)) {
                this.mDevicePolicyEngine.removeLocalPolicy(PolicyDefinition.PERSISTENT_PREFERRED_ACTIVITY(intentFilter), enforcingAdmin, i);
            }
        }
    }

    public void setDefaultSmsApplication(ComponentName componentName, String str, final String str2, boolean z) {
        CallerIdentity callerIdentity;
        final int userHandleGetCallingUserId;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_DEFAULT_SMS", callerIdentity.getPackageName(), getAffectedUser(z));
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || (z && isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)));
        }
        if (z) {
            userHandleGetCallingUserId = getProfileParentId(this.mInjector.userHandleGetCallingUserId());
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda29
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setDefaultSmsApplication$83(str2, userHandleGetCallingUserId);
                }
            });
        } else {
            userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda30
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setDefaultSmsApplication$84(str2, userHandleGetCallingUserId);
            }
        });
        synchronized (getLockObject()) {
            ActiveAdmin parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z);
            if (!Objects.equals(parentOfAdminIfRequired.mSmsPackage, str2)) {
                parentOfAdminIfRequired.mSmsPackage = str2;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDefaultSmsApplication$84(String str, int i) throws Exception {
        SmsApplication.setDefaultApplicationAsUser(str, this.mContext, i);
    }

    public void setDefaultDialerApplication(final String str) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            final int userId = callerIdentity.getUserId();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda72
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setDefaultDialerApplication$86(str, userId);
                }
            });
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(userId);
                if (!Objects.equals(profileOwnerOrDeviceOwnerLocked.mDialerPackage, str)) {
                    profileOwnerOrDeviceOwnerLocked.mDialerPackage = str;
                    saveSettingsLocked(userId);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDefaultDialerApplication$86(final String str, int i) throws Exception {
        final CompletableFuture completableFuture = new CompletableFuture();
        this.mRoleManager.addRoleHolderAsUser("android.app.role.DIALER", str, 0, UserHandle.of(i), AsyncTask.THREAD_POOL_EXECUTOR, new Consumer() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda185
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DevicePolicyManagerService.lambda$setDefaultDialerApplication$85(completableFuture, str, (Boolean) obj);
            }
        });
        try {
            completableFuture.get(5L, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                throw ((IllegalArgumentException) cause);
            }
            throw new IllegalStateException(cause);
        } catch (TimeoutException e2) {
            throw new IllegalArgumentException("Timeout when setting the app as the dialer", e2);
        }
    }

    public static /* synthetic */ void lambda$setDefaultDialerApplication$85(CompletableFuture completableFuture, String str, Boolean bool) {
        if (bool.booleanValue()) {
            completableFuture.complete(null);
            return;
        }
        completableFuture.completeExceptionally(new IllegalArgumentException(str + " cannot be set as the dialer"));
    }

    public boolean setApplicationRestrictionsManagingPackage(ComponentName componentName, String str) {
        try {
            setDelegatedScopePreO(componentName, str, "delegation-app-restrictions");
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public String getApplicationRestrictionsManagingPackage(ComponentName componentName) {
        List<String> delegatePackages = getDelegatePackages(componentName, "delegation-app-restrictions");
        if (delegatePackages.size() > 0) {
            return delegatePackages.get(0);
        }
        return null;
    }

    public boolean isCallerApplicationRestrictionsManagingPackage(String str) {
        return isCallerDelegate(str, getCallerIdentity().getUid(), "delegation-app-restrictions");
    }

    public void setApplicationRestrictions(ComponentName componentName, String str, final String str2, final Bundle bundle) {
        final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        checkCanExecuteOrThrowUnsafe(16);
        boolean z = false;
        if (useDevicePolicyEngine(callerIdentity, "delegation-app-restrictions")) {
            EnforcingAdmin enforcePermissionAndGetEnforcingAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS", callerIdentity.getPackageName(), callerIdentity.getUserId());
            String validateName = FrameworkParsingPackageUtils.validateName(str2, false, false);
            if (validateName != null) {
                throw new IllegalArgumentException("Invalid package name: " + validateName);
            }
            if (bundle == null || bundle.isEmpty()) {
                this.mDevicePolicyEngine.removeLocalPolicy(PolicyDefinition.APPLICATION_RESTRICTIONS(str2), enforcePermissionAndGetEnforcingAdmin, callerIdentity.getUserId());
            } else {
                this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.APPLICATION_RESTRICTIONS(str2), enforcePermissionAndGetEnforcingAdmin, new BundlePolicyValue(bundle), callerIdentity.getUserId());
            }
            setBackwardsCompatibleAppRestrictions(str2, bundle, callerIdentity.getUserHandle());
        } else {
            if ((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-app-restrictions"))) {
                z = true;
            }
            Preconditions.checkCallAuthorization(z);
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda36
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setApplicationRestrictions$87(str2, bundle, callerIdentity);
                }
            });
        }
        DevicePolicyEventLogger.createEvent(62).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate(callerIdentity)).setStrings(new String[]{str2}).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setApplicationRestrictions$87(String str, Bundle bundle, CallerIdentity callerIdentity) throws Exception {
        this.mUserManager.setApplicationRestrictions(str, bundle, callerIdentity.getUserHandle());
    }

    public final void setBackwardsCompatibleAppRestrictions(final String str, final Bundle bundle, final UserHandle userHandle) {
        if (bundle == null || bundle.isEmpty()) {
            bundle = getAppRestrictionsSetByAnyAdmin(str, userHandle);
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda191
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setBackwardsCompatibleAppRestrictions$88(str, bundle, userHandle);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setBackwardsCompatibleAppRestrictions$88(String str, Bundle bundle, UserHandle userHandle) throws Exception {
        this.mUserManager.setApplicationRestrictions(str, bundle, userHandle);
    }

    public final Bundle getAppRestrictionsSetByAnyAdmin(String str, UserHandle userHandle) {
        LinkedHashMap localPoliciesSetByAdmins = this.mDevicePolicyEngine.getLocalPoliciesSetByAdmins(PolicyDefinition.APPLICATION_RESTRICTIONS(str), userHandle.getIdentifier());
        if (localPoliciesSetByAdmins.isEmpty()) {
            return null;
        }
        return (Bundle) ((PolicyValue) ((Map.Entry) localPoliciesSetByAdmins.entrySet().stream().findAny().get()).getValue()).getValue();
    }

    public final int getUidForPackage(final String str, final int i) {
        return ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda194
            public final Object getOrThrow() {
                Integer lambda$getUidForPackage$89;
                lambda$getUidForPackage$89 = DevicePolicyManagerService.this.lambda$getUidForPackage$89(str, i);
                return lambda$getUidForPackage$89;
            }
        })).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getUidForPackage$89(String str, int i) throws Exception {
        try {
            return Integer.valueOf(this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, i).uid);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public void setTrustAgentConfiguration(ComponentName componentName, String str, ComponentName componentName2, PersistableBundle persistableBundle, boolean z) {
        ActiveAdmin activeAdminForCallerLocked;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (!isPermissionCheckFlagEnabled()) {
                Objects.requireNonNull(componentName, "admin is null");
            }
            Objects.requireNonNull(componentName2, "agent is null");
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    activeAdminForCallerLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_KEYGUARD", getCallerIdentity(componentName, str).getPackageName(), z ? getProfileParentId(callingUserId) : callingUserId).getActiveAdmin();
                } else {
                    activeAdminForCallerLocked = getActiveAdminForCallerLocked(componentName, 9, z);
                }
                checkCanExecuteOrThrowUnsafe(21);
                activeAdminForCallerLocked.trustAgentInfos.put(componentName2.flattenToString(), new ActiveAdmin.TrustAgentInfo(persistableBundle));
                saveSettingsLocked(callingUserId);
            }
        }
    }

    public List<PersistableBundle> getTrustAgentConfiguration(ComponentName componentName, ComponentName componentName2, int i, boolean z) {
        PersistableBundle persistableBundle;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Objects.requireNonNull(componentName2, "agent null");
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(componentName), i));
            synchronized (getLockObject()) {
                String flattenToString = componentName2.flattenToString();
                if (componentName != null) {
                    ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i, z);
                    if (activeAdminUncheckedLocked == null) {
                        return null;
                    }
                    ActiveAdmin.TrustAgentInfo trustAgentInfo = activeAdminUncheckedLocked.trustAgentInfos.get(flattenToString);
                    if (trustAgentInfo != null && trustAgentInfo.options != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(trustAgentInfo.options);
                        return arrayList;
                    }
                    return null;
                }
                List<ActiveAdmin> activeAdminsForLockscreenPoliciesLocked = getActiveAdminsForLockscreenPoliciesLocked(getProfileParentUserIfRequested(i, z));
                int size = activeAdminsForLockscreenPoliciesLocked.size();
                boolean z2 = false;
                int i2 = 0;
                ArrayList arrayList2 = null;
                while (true) {
                    if (i2 >= size) {
                        z2 = true;
                        break;
                    }
                    ActiveAdmin activeAdmin = activeAdminsForLockscreenPoliciesLocked.get(i2);
                    boolean z3 = (activeAdmin.disabledKeyguardFeatures & 16) != 0;
                    ActiveAdmin.TrustAgentInfo trustAgentInfo2 = activeAdmin.trustAgentInfos.get(flattenToString);
                    if (trustAgentInfo2 == null || (persistableBundle = trustAgentInfo2.options) == null || persistableBundle.isEmpty()) {
                        if (z3) {
                            break;
                        }
                    } else if (z3) {
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                        }
                        arrayList2.add(trustAgentInfo2.options);
                    } else {
                        Slogf.m12w("DevicePolicyManager", "Ignoring admin %s because it has trust options but doesn't declare KEYGUARD_DISABLE_TRUST_AGENTS", activeAdmin.info);
                    }
                    i2++;
                }
                return z2 ? arrayList2 : null;
            }
        }
        return null;
    }

    public void setRestrictionsProvider(ComponentName componentName, ComponentName componentName2) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(39);
        synchronized (getLockObject()) {
            int userId = callerIdentity.getUserId();
            lambda$getUserDataUnchecked$2(userId).mRestrictionsProvider = componentName2;
            saveSettingsLocked(userId);
        }
    }

    public ComponentName getRestrictionsProvider(int i) {
        ComponentName componentName;
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query the permission provider"));
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            componentName = lambda$getUserDataUnchecked$2 != null ? lambda$getUserDataUnchecked$2.mRestrictionsProvider : null;
        }
        return componentName;
    }

    public void addCrossProfileIntentFilter(ComponentName componentName, String str, IntentFilter intentFilter, int i) {
        Injector injector;
        UserInfo profileParent;
        CallerIdentity callerIdentity = isPermissionCheckFlagEnabled() ? getCallerIdentity(componentName, str) : getCallerIdentity(componentName);
        int userId = callerIdentity.getUserId();
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", callerIdentity.getPackageName(), userId);
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        }
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                profileParent = this.mUserManager.getProfileParent(userId);
            } catch (RemoteException e) {
                Slogf.wtf("DevicePolicyManager", "Error adding cross profile intent filter", e);
                injector = this.mInjector;
            }
            if (profileParent == null) {
                Slogf.m26e("DevicePolicyManager", "Cannot call addCrossProfileIntentFilter if there is no parent");
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return;
            }
            if ((i & 1) != 0) {
                this.mIPackageManager.addCrossProfileIntentFilter(intentFilter, componentName.getPackageName(), userId, profileParent.id, 0);
            }
            if ((i & 2) != 0) {
                this.mIPackageManager.addCrossProfileIntentFilter(intentFilter, componentName.getPackageName(), profileParent.id, userId, 0);
            }
            injector = this.mInjector;
            injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            DevicePolicyEventLogger.createEvent(48).setAdmin(callerIdentity.getPackageName()).setStrings(getIntentFilterActions(intentFilter)).setInt(i).write();
        }
    }

    public static String[] getIntentFilterActions(IntentFilter intentFilter) {
        if (intentFilter == null) {
            return null;
        }
        int countActions = intentFilter.countActions();
        String[] strArr = new String[countActions];
        for (int i = 0; i < countActions; i++) {
            strArr[i] = intentFilter.getAction(i);
        }
        return strArr;
    }

    public void clearCrossProfileIntentFilters(ComponentName componentName, String str) {
        Injector injector;
        UserInfo profileParent;
        CallerIdentity callerIdentity = isPermissionCheckFlagEnabled() ? getCallerIdentity(componentName, str) : getCallerIdentity(componentName);
        int userId = callerIdentity.getUserId();
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION", callerIdentity.getPackageName(), userId);
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        }
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                profileParent = this.mUserManager.getProfileParent(userId);
            } catch (RemoteException e) {
                Slogf.wtf("DevicePolicyManager", "Error clearing cross profile intent filters", e);
                injector = this.mInjector;
            }
            if (profileParent == null) {
                Slogf.m26e("DevicePolicyManager", "Cannot call clearCrossProfileIntentFilter if there is no parent");
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return;
            }
            this.mIPackageManager.clearCrossProfileIntentFilters(userId, componentName.getPackageName());
            this.mIPackageManager.clearCrossProfileIntentFilters(profileParent.id, componentName.getPackageName());
            injector = this.mInjector;
            injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0037, code lost:
        if ((r5.flags & 1) != 0) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean checkPackagesInPermittedListOrSystem(List<String> list, List<String> list2, int i) {
        boolean z;
        String next;
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            UserInfo userInfo = getUserInfo(i);
            if (userInfo.isManagedProfile()) {
                i = userInfo.profileGroupId;
            }
            Iterator<String> it = list.iterator();
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    return true;
                }
                next = it.next();
                try {
                    ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(next, 8192L, i);
                    if (applicationInfo == null) {
                        break;
                    }
                } catch (RemoteException e) {
                    Slogf.m21i("DevicePolicyManager", "Can't talk to package managed", e);
                }
            }
            z = false;
            if (!z && !list2.contains(next)) {
                return false;
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final <T> T withAccessibilityManager(int i, Function<AccessibilityManager, T> function) {
        IBinder service = ServiceManager.getService("accessibility");
        AccessibilityManager accessibilityManager = new AccessibilityManager(this.mContext, service == null ? null : IAccessibilityManager.Stub.asInterface(service), i);
        try {
            return function.apply(accessibilityManager);
        } finally {
            accessibilityManager.removeClient();
        }
    }

    public boolean setPermittedAccessibilityServices(ComponentName componentName, List<String> list) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            if (list != null) {
                int userId = callerIdentity.getUserId();
                long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                try {
                    UserInfo userInfo = getUserInfo(userId);
                    if (userInfo.isManagedProfile()) {
                        userId = userInfo.profileGroupId;
                    }
                    List<AccessibilityServiceInfo> list2 = (List) withAccessibilityManager(userId, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda4
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            List enabledAccessibilityServiceList;
                            enabledAccessibilityServiceList = ((AccessibilityManager) obj).getEnabledAccessibilityServiceList(-1);
                            return enabledAccessibilityServiceList;
                        }
                    });
                    if (list2 != null) {
                        ArrayList arrayList = new ArrayList();
                        for (AccessibilityServiceInfo accessibilityServiceInfo : list2) {
                            arrayList.add(accessibilityServiceInfo.getResolveInfo().serviceInfo.packageName);
                        }
                        if (!checkPackagesInPermittedListOrSystem(arrayList, list, userId)) {
                            Slogf.m26e("DevicePolicyManager", "Cannot set permitted accessibility services, because it contains already enabled accesibility services.");
                            return false;
                        }
                    }
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                }
            }
            synchronized (getLockObject()) {
                getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).permittedAccessiblityServices = list;
                saveSettingsLocked(UserHandle.getCallingUserId());
            }
            DevicePolicyEventLogger.createEvent(28).setAdmin(componentName).setStrings(list != null ? (String[]) list.toArray(new String[0]) : null).write();
            return true;
        }
        return false;
    }

    public List<String> getPermittedAccessibilityServices(ComponentName componentName) {
        List<String> list;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                list = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).permittedAccessiblityServices;
            }
            return list;
        }
        return null;
    }

    public List<String> getPermittedAccessibilityServicesForUser(int i) {
        ArrayList arrayList = null;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(canManageUsers(callerIdentity) || canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                for (int i2 : this.mUserManager.getProfileIdsWithDisabled(i)) {
                    DevicePolicyData userDataUnchecked = getUserDataUnchecked(i2);
                    int size = userDataUnchecked.mAdminList.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        List<String> list = userDataUnchecked.mAdminList.get(i3).permittedAccessiblityServices;
                        if (list != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList(list);
                            } else {
                                arrayList.retainAll(list);
                            }
                        }
                    }
                }
                if (arrayList != null) {
                    long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                    UserInfo userInfo = getUserInfo(i);
                    if (userInfo.isManagedProfile()) {
                        i = userInfo.profileGroupId;
                    }
                    List<AccessibilityServiceInfo> list2 = (List) withAccessibilityManager(i, new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda81
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return ((AccessibilityManager) obj).getInstalledAccessibilityServiceList();
                        }
                    });
                    if (list2 != null) {
                        for (AccessibilityServiceInfo accessibilityServiceInfo : list2) {
                            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
                            if ((serviceInfo.applicationInfo.flags & 1) != 0) {
                                arrayList.add(serviceInfo.packageName);
                            }
                        }
                    }
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                }
            }
            return arrayList;
        }
        return null;
    }

    public boolean isAccessibilityServicePermittedByAdmin(ComponentName componentName, String str, int i) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkStringNotEmpty(str, "packageName is null");
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query if an accessibility service is disabled by admin"));
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                if (activeAdminUncheckedLocked == null) {
                    return false;
                }
                if (activeAdminUncheckedLocked.permittedAccessiblityServices == null) {
                    return true;
                }
                return checkPackagesInPermittedListOrSystem(Collections.singletonList(str), activeAdminUncheckedLocked.permittedAccessiblityServices, i);
            }
        }
        return true;
    }

    public boolean setPermittedInputMethods(ComponentName componentName, String str, List<String> list, boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin parentOfAdminIfRequired;
        List<InputMethodInfo> list2;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            final int profileParentUserIfRequested = getProfileParentUserIfRequested(callerIdentity.getUserId(), z);
            if (z) {
                if (!isPermissionCheckFlagEnabled()) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
                }
                Preconditions.checkArgument(list == null || list.isEmpty(), "Permitted input methods must allow all input methods or only system input methods when called on the parent instance of an organization-owned device");
            } else if (!isPermissionCheckFlagEnabled()) {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            }
            if (list != null && (list2 = (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda83
                public final Object getOrThrow() {
                    List lambda$setPermittedInputMethods$91;
                    lambda$setPermittedInputMethods$91 = DevicePolicyManagerService.lambda$setPermittedInputMethods$91(profileParentUserIfRequested);
                    return lambda$setPermittedInputMethods$91;
                }
            })) != null) {
                ArrayList arrayList = new ArrayList();
                for (InputMethodInfo inputMethodInfo : list2) {
                    arrayList.add(inputMethodInfo.getPackageName());
                }
                if (!checkPackagesInPermittedListOrSystem(arrayList, list, profileParentUserIfRequested)) {
                    Slogf.m26e("DevicePolicyManager", "Cannot set permitted input methods, because the list of permitted input methods excludes an already-enabled input method.");
                    return false;
                }
            }
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_INPUT_METHODS", callerIdentity.getPackageName(), profileParentUserIfRequested).getActiveAdmin();
                } else {
                    parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z);
                }
                parentOfAdminIfRequired.permittedInputMethods = list;
                saveSettingsLocked(callerIdentity.getUserId());
            }
            DevicePolicyEventLogger.createEvent(27).setAdmin(callerIdentity.getPackageName()).setStrings(getStringArrayForLogging(list, z)).write();
            return true;
        }
        return false;
    }

    public static /* synthetic */ List lambda$setPermittedInputMethods$91(int i) throws Exception {
        return InputMethodManagerInternal.get().getEnabledInputMethodListAsUser(i);
    }

    public final String[] getStringArrayForLogging(List list, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(z ? "calledFromParent" : "notCalledFromParent");
        if (list == null) {
            arrayList.add("nullStringArray");
        } else {
            arrayList.addAll(list);
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public List<String> getPermittedInputMethods(ComponentName componentName, String str, boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin parentOfAdminIfRequired;
        List<String> list;
        int userId;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
            }
            if (!isPermissionCheckFlagEnabled()) {
                if (z) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
                } else {
                    Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                }
            }
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    if (z) {
                        userId = getProfileParentId(callerIdentity.getUserId());
                    } else {
                        userId = callerIdentity.getUserId();
                    }
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_INPUT_METHODS", callerIdentity.getPackageName(), userId).getActiveAdmin();
                } else {
                    parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z);
                }
                list = parentOfAdminIfRequired.permittedInputMethods;
            }
            return list;
        }
        return null;
    }

    public List<String> getPermittedInputMethodsAsUser(int i) {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
        Preconditions.checkCallAuthorization(canManageUsers(callerIdentity) || canQueryAdminPolicy(callerIdentity));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getPermittedInputMethodsUnchecked(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final List<String> getPermittedInputMethodsUnchecked(int i) {
        ArrayList arrayList;
        List<InputMethodInfo> inputMethodListAsUser;
        synchronized (getLockObject()) {
            arrayList = null;
            for (ActiveAdmin activeAdmin : getActiveAdminsForAffectedUserInclPermissionBasedAdminLocked(i)) {
                List<String> list = activeAdmin.permittedInputMethods;
                if (list != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList(list);
                    } else {
                        arrayList.retainAll(list);
                    }
                }
            }
            if (arrayList != null && (inputMethodListAsUser = InputMethodManagerInternal.get().getInputMethodListAsUser(i)) != null) {
                for (InputMethodInfo inputMethodInfo : inputMethodListAsUser) {
                    ServiceInfo serviceInfo = inputMethodInfo.getServiceInfo();
                    if ((serviceInfo.applicationInfo.flags & 1) != 0) {
                        arrayList.add(serviceInfo.packageName);
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean isInputMethodPermittedByAdmin(ComponentName componentName, String str, int i, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkStringNotEmpty(str, "packageName is null");
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query if an input method is disabled by admin"));
            synchronized (getLockObject()) {
                ActiveAdmin parentOfAdminIfRequired = getParentOfAdminIfRequired(getActiveAdminUncheckedLocked(componentName, i), z);
                if (parentOfAdminIfRequired == null) {
                    return false;
                }
                if (parentOfAdminIfRequired.permittedInputMethods == null) {
                    return true;
                }
                return checkPackagesInPermittedListOrSystem(Collections.singletonList(str), parentOfAdminIfRequired.permittedInputMethods, i);
            }
        }
        return true;
    }

    public boolean setPermittedCrossProfileNotificationListeners(ComponentName componentName, List<String> list) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            if (isManagedProfile(callerIdentity.getUserId())) {
                Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
                synchronized (getLockObject()) {
                    getProfileOwnerLocked(callerIdentity.getUserId()).permittedNotificationListeners = list;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public List<String> getPermittedCrossProfileNotificationListeners(ComponentName componentName) {
        List<String> list;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                list = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).permittedNotificationListeners;
            }
            return list;
        }
        return null;
    }

    public boolean isNotificationListenerServicePermitted(String str, int i) {
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(str, "packageName is null or empty");
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query if a notification listener service is permitted"));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
                if (profileOwnerAdminLocked != null && profileOwnerAdminLocked.permittedNotificationListeners != null) {
                    return checkPackagesInPermittedListOrSystem(Collections.singletonList(str), profileOwnerAdminLocked.permittedNotificationListeners, i);
                }
                return true;
            }
        }
        return true;
    }

    public final void maybeSendAdminEnabledBroadcastLocked(int i) {
        boolean z;
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
        if (lambda$getUserDataUnchecked$2.mAdminBroadcastPending) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked != null) {
                PersistableBundle persistableBundle = lambda$getUserDataUnchecked$2.mInitBundle;
                z = sendAdminCommandLocked(profileOwnerAdminLocked, "android.app.action.DEVICE_ADMIN_ENABLED", persistableBundle == null ? null : new Bundle(persistableBundle), null, true);
            } else {
                z = true;
            }
            if (z) {
                lambda$getUserDataUnchecked$2.mInitBundle = null;
                lambda$getUserDataUnchecked$2.mAdminBroadcastPending = false;
                saveSettingsLocked(i);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x012c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public UserHandle createAndManageUser(ComponentName componentName, String str, ComponentName componentName2, PersistableBundle persistableBundle, int i) {
        UserHandle userHandle;
        UserInfo createUserEvenWhenDisallowed;
        Objects.requireNonNull(componentName, "admin is null");
        Objects.requireNonNull(componentName2, "profileOwner is null");
        if (!componentName.getPackageName().equals(componentName2.getPackageName())) {
            throw new IllegalArgumentException("profileOwner " + componentName2 + " and admin " + componentName + " are not in the same package");
        }
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(callerIdentity.getUserHandle().isSystem(), "createAndManageUser was called from non-system user");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(5);
        boolean z = (i & 2) != 0;
        boolean z2 = (i & 4) != 0 && UserManager.isDeviceInDemoMode(this.mContext);
        boolean z3 = (i & 16) != 0;
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            int uidTargetSdkVersion = this.mInjector.getPackageManagerInternal().getUidTargetSdkVersion(callerIdentity.getUid());
            if (((DeviceStorageMonitorInternal) LocalServices.getService(DeviceStorageMonitorInternal.class)).isMemoryLow()) {
                if (uidTargetSdkVersion < 28) {
                    return null;
                }
                throw new ServiceSpecificException(5, "low device storage");
            }
            String str2 = z2 ? "android.os.usertype.full.DEMO" : "android.os.usertype.full.SECONDARY";
            int i2 = z ? 256 : 0;
            if (!this.mUserManager.canAddMoreUsers(str2)) {
                if (uidTargetSdkVersion < 28) {
                    return null;
                }
                throw new ServiceSpecificException(6, "user limit reached");
            }
            String[] strArr = !z3 ? (String[]) this.mOverlayPackagesProvider.getNonRequiredApps(componentName, UserHandle.myUserId(), "android.app.action.PROVISION_MANAGED_USER").toArray(new String[0]) : null;
            Object obj = new Object();
            Slogf.m30d("DevicePolicyManager", "Adding new pending token: " + obj);
            this.mPendingUserCreatedCallbackTokens.add(obj);
            try {
                createUserEvenWhenDisallowed = this.mUserManagerInternal.createUserEvenWhenDisallowed(str, str2, i2, strArr, obj);
            } catch (UserManager.CheckedUserOperationException e) {
                Slogf.m25e("DevicePolicyManager", "Couldn't createUserEvenWhenDisallowed", (Throwable) e);
            }
            if (createUserEvenWhenDisallowed != null) {
                userHandle = createUserEvenWhenDisallowed.getUserHandle();
                if (userHandle != null) {
                    if (uidTargetSdkVersion < 28) {
                        return null;
                    }
                    throw new ServiceSpecificException(1, "failed to create user");
                }
                int identifier = userHandle.getIdentifier();
                binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                try {
                    maybeInstallDevicePolicyManagementRoleHolderInUser(identifier);
                    manageUserUnchecked(componentName, componentName2, identifier, persistableBundle, true);
                    if ((i & 1) != 0) {
                        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 1, identifier);
                    }
                    sendProvisioningCompletedBroadcast(identifier, "android.app.action.PROVISION_MANAGED_USER", z3);
                    return userHandle;
                } catch (Throwable th) {
                    try {
                        this.mUserManager.removeUser(identifier);
                        if (uidTargetSdkVersion < 28) {
                            return null;
                        }
                        throw new ServiceSpecificException(1, th.getMessage());
                    } finally {
                        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    }
                }
            }
            userHandle = null;
            if (userHandle != null) {
            }
        }
    }

    public final void sendProvisioningCompletedBroadcast(int i, String str, boolean z) {
        this.mContext.sendBroadcastAsUser(new Intent("android.app.action.PROVISIONING_COMPLETED").putExtra("android.intent.extra.user_handle", i).putExtra("android.intent.extra.USER", UserHandle.of(i)).putExtra("android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED", z).putExtra("android.app.extra.PROVISIONING_ACTION", str).setPackage(getManagedProvisioningPackage(this.mContext)).addFlags(268435456), UserHandle.SYSTEM);
    }

    public final void manageUserUnchecked(ComponentName componentName, ComponentName componentName2, final int i, PersistableBundle persistableBundle, boolean z) {
        synchronized (getLockObject()) {
        }
        final String packageName = componentName.getPackageName();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda149
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$manageUserUnchecked$92(packageName, i);
            }
        });
        setActiveAdmin(componentName2, true, i);
        setProfileOwner(componentName2, i);
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            lambda$getUserDataUnchecked$2.mInitBundle = persistableBundle;
            lambda$getUserDataUnchecked$2.mAdminBroadcastPending = true;
            lambda$getUserDataUnchecked$2.mNewUserDisclaimer = z ? "needed" : "not_needed";
            saveSettingsLocked(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$manageUserUnchecked$92(String str, int i) throws Exception {
        try {
            if (this.mIPackageManager.isPackageAvailable(str, i)) {
                return;
            }
            this.mIPackageManager.installExistingPackageAsUser(str, i, 4194304, 1, (List) null);
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", e, "Failed to install admin package %s for user %d", str, Integer.valueOf(i));
        }
    }

    public final void handleNewUserCreated(UserInfo userInfo, Object obj) {
        int i = userInfo.id;
        if (isDevicePolicyEngineEnabled()) {
            this.mDevicePolicyEngine.handleUserCreated(userInfo);
        }
        if (obj != null) {
            synchronized (getLockObject()) {
                if (this.mPendingUserCreatedCallbackTokens.contains(obj)) {
                    Slogf.m30d("DevicePolicyManager", "handleNewUserCreated(): ignoring for user " + i + " due to token " + obj);
                    this.mPendingUserCreatedCallbackTokens.remove(obj);
                    return;
                }
            }
        }
        if (!this.mOwners.hasDeviceOwner() || !userInfo.isFull() || userInfo.isManagedProfile() || userInfo.isGuest()) {
            return;
        }
        if (this.mInjector.userManagerIsHeadlessSystemUserMode()) {
            ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
            Slogf.m22i("DevicePolicyManager", "Automatically setting profile owner (" + deviceOwnerComponent + ") on new user " + i);
            manageUserUnchecked(deviceOwnerComponent, deviceOwnerComponent, i, null, true);
            return;
        }
        Slogf.m20i("DevicePolicyManager", "User %d added on DO mode; setting ShowNewUserDisclaimer", Integer.valueOf(i));
        setShowNewUserDisclaimer(i, "needed");
    }

    public void acknowledgeNewUserDisclaimer(int i) {
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS"));
        setShowNewUserDisclaimer(i, "acked");
    }

    public final void setShowNewUserDisclaimer(int i, String str) {
        Slogf.m22i("DevicePolicyManager", "Setting new user disclaimer for user " + i + " as " + str);
        synchronized (getLockObject()) {
            lambda$getUserDataUnchecked$2(i).mNewUserDisclaimer = str;
            saveSettingsLocked(i);
        }
    }

    public final void showNewUserDisclaimerIfNecessary(int i) {
        boolean equals;
        synchronized (getLockObject()) {
            equals = "needed".equals(lambda$getUserDataUnchecked$2(i).mNewUserDisclaimer);
        }
        if (equals) {
            Intent intent = new Intent("android.app.action.SHOW_NEW_USER_DISCLAIMER");
            Slogf.m22i("DevicePolicyManager", "Dispatching ACTION_SHOW_NEW_USER_DISCLAIMER intent");
            this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i));
        }
    }

    public boolean isNewUserDisclaimerAcknowledged(int i) {
        boolean isNewUserDisclaimerAcknowledged;
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS"));
        synchronized (getLockObject()) {
            isNewUserDisclaimerAcknowledged = lambda$getUserDataUnchecked$2(i).isNewUserDisclaimerAcknowledged();
        }
        return isNewUserDisclaimerAcknowledged;
    }

    public boolean removeUser(final ComponentName componentName, final UserHandle userHandle) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Objects.requireNonNull(userHandle, "UserHandle is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(6);
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda128
            public final Object getOrThrow() {
                Boolean lambda$removeUser$93;
                lambda$removeUser$93 = DevicePolicyManagerService.this.lambda$removeUser$93(userHandle, componentName, callerIdentity);
                return lambda$removeUser$93;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$removeUser$93(UserHandle userHandle, ComponentName componentName, CallerIdentity callerIdentity) throws Exception {
        String str = isManagedProfile(userHandle.getIdentifier()) ? "no_remove_managed_profile" : "no_remove_user";
        if (isAdminAffectedByRestriction(componentName, str, callerIdentity.getUserId())) {
            Slogf.m12w("DevicePolicyManager", "The device owner cannot remove a user because %s is enabled, and was not set by the device owner", str);
            return Boolean.FALSE;
        }
        return Boolean.valueOf(this.mUserManagerInternal.removeUserEvenWhenDisallowed(userHandle.getIdentifier()));
    }

    public final boolean isAdminAffectedByRestriction(ComponentName componentName, String str, int i) {
        int userRestrictionSource = this.mUserManager.getUserRestrictionSource(str, UserHandle.of(i));
        if (userRestrictionSource != 0) {
            if (userRestrictionSource != 2) {
                if (userRestrictionSource != 4) {
                    return true;
                }
                return !isProfileOwner(componentName, i);
            }
            return !isDeviceOwner(componentName, i);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0090 A[Catch: all -> 0x00a2, TryCatch #1 {, blocks: (B:4:0x001d, B:13:0x0069, B:15:0x0070, B:16:0x0073, B:34:0x0097, B:36:0x009e, B:37:0x00a1, B:27:0x0089, B:29:0x0090, B:30:0x0093), top: B:41:0x001d }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x009e A[Catch: all -> 0x00a2, TryCatch #1 {, blocks: (B:4:0x001d, B:13:0x0069, B:15:0x0070, B:16:0x0073, B:34:0x0097, B:36:0x009e, B:37:0x00a1, B:27:0x0089, B:29:0x0090, B:30:0x0093), top: B:41:0x001d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean switchUser(ComponentName componentName, UserHandle userHandle) {
        int identifier;
        boolean z;
        RemoteException e;
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        checkCanExecuteOrThrowUnsafe(2);
        int logoutUserIdUnchecked = getLogoutUserIdUnchecked();
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            boolean z2 = false;
            if (userHandle != null) {
                try {
                    identifier = userHandle.getIdentifier();
                } catch (RemoteException e2) {
                    e = e2;
                    z = false;
                    try {
                        Slogf.m25e("DevicePolicyManager", "Couldn't switch user", e);
                        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                        if (!z) {
                            setLogoutUserIdLocked(logoutUserIdUnchecked);
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        z2 = z;
                        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                        if (!z2) {
                            setLogoutUserIdLocked(logoutUserIdUnchecked);
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    if (!z2) {
                    }
                    throw th;
                }
            } else {
                identifier = 0;
            }
            Slogf.m20i("DevicePolicyManager", "Switching to user %d (logout user is %d)", Integer.valueOf(identifier), Integer.valueOf(logoutUserIdUnchecked));
            setLogoutUserIdLocked(-2);
            boolean switchUser = this.mInjector.getIActivityManager().switchUser(identifier);
            try {
                if (switchUser) {
                    Slogf.m30d("DevicePolicyManager", "Switched");
                } else {
                    Slogf.m12w("DevicePolicyManager", "Failed to switch to user %d", Integer.valueOf(identifier));
                }
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                if (!switchUser) {
                    setLogoutUserIdLocked(logoutUserIdUnchecked);
                }
                return switchUser;
            } catch (RemoteException e3) {
                z = switchUser;
                e = e3;
                Slogf.m25e("DevicePolicyManager", "Couldn't switch user", e);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                if (!z) {
                }
                return false;
            } catch (Throwable th3) {
                z2 = switchUser;
                th = th3;
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                if (!z2) {
                }
                throw th;
            }
        }
    }

    public int getLogoutUserId() {
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS"));
        return getLogoutUserIdUnchecked();
    }

    public final int getLogoutUserIdUnchecked() {
        int i;
        synchronized (getLockObject()) {
            i = this.mLogoutUserId;
        }
        return i;
    }

    public final void clearLogoutUser() {
        synchronized (getLockObject()) {
            setLogoutUserIdLocked(-10000);
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void setLogoutUserIdLocked(int i) {
        if (i == -2) {
            i = getCurrentForegroundUserId();
        }
        Slogf.m28d("DevicePolicyManager", "setLogoutUserId(): %d -> %d", Integer.valueOf(this.mLogoutUserId), Integer.valueOf(i));
        this.mLogoutUserId = i;
    }

    public int startUserInBackground(ComponentName componentName, UserHandle userHandle) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Objects.requireNonNull(userHandle, "UserHandle is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        checkCanExecuteOrThrowUnsafe(3);
        int identifier = userHandle.getIdentifier();
        if (isManagedProfile(identifier)) {
            Slogf.m14w("DevicePolicyManager", "Managed profile cannot be started in background");
            return 2;
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
        } catch (RemoteException unused) {
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            throw th;
        }
        if (this.mInjector.getActivityManagerInternal().canStartMoreUsers()) {
            Slogf.m20i("DevicePolicyManager", "Starting user %d in background", Integer.valueOf(identifier));
            if (this.mInjector.getIActivityManager().startUserInBackground(identifier)) {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return 0;
            }
            Slogf.m12w("DevicePolicyManager", "failed to start user %d in background", Integer.valueOf(identifier));
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            return 1;
        }
        Slogf.m12w("DevicePolicyManager", "Cannot start user %d, too many users in background", Integer.valueOf(identifier));
        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        return 3;
    }

    public int stopUser(ComponentName componentName, UserHandle userHandle) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Objects.requireNonNull(userHandle, "UserHandle is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        checkCanExecuteOrThrowUnsafe(4);
        int identifier = userHandle.getIdentifier();
        if (isManagedProfile(identifier)) {
            Slogf.m14w("DevicePolicyManager", "Managed profile cannot be stopped");
            return 2;
        }
        return stopUserUnchecked(identifier);
    }

    public int logoutUser(ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(9);
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            if (!isUserAffiliatedWithDeviceLocked(userId)) {
                throw new SecurityException("Admin " + componentName + " is neither the device owner or affiliated user's profile owner.");
            }
        }
        if (isManagedProfile(userId)) {
            Slogf.m14w("DevicePolicyManager", "Managed profile cannot be logout");
            return 2;
        } else if (userId != ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda172
            public final Object getOrThrow() {
                Integer lambda$logoutUser$94;
                lambda$logoutUser$94 = DevicePolicyManagerService.this.lambda$logoutUser$94();
                return lambda$logoutUser$94;
            }
        })).intValue()) {
            Slogf.m28d("DevicePolicyManager", "logoutUser(): user %d is in background, just stopping, not switching", Integer.valueOf(userId));
            return stopUserUnchecked(userId);
        } else {
            return logoutUserUnchecked(userId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$logoutUser$94() throws Exception {
        return Integer.valueOf(getCurrentForegroundUserId());
    }

    public int logoutUserInternal() {
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS"));
        return logoutUserUnchecked(getCurrentForegroundUserId());
    }

    public final int logoutUserUnchecked(int i) {
        int logoutUserIdUnchecked = getLogoutUserIdUnchecked();
        if (logoutUserIdUnchecked == -10000) {
            Slogf.m14w("DevicePolicyManager", "logoutUser(): could not determine which user to switch to");
            return 1;
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            Slogf.m20i("DevicePolicyManager", "logoutUser(): switching to user %d", Integer.valueOf(logoutUserIdUnchecked));
            if (!this.mInjector.getIActivityManager().switchUser(logoutUserIdUnchecked)) {
                Slogf.m12w("DevicePolicyManager", "Failed to switch to user %d", Integer.valueOf(logoutUserIdUnchecked));
                return 1;
            }
            clearLogoutUser();
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            return stopUserUnchecked(i);
        } catch (RemoteException unused) {
            return 1;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final int stopUserUnchecked(int i) {
        int stopUser;
        Slogf.m20i("DevicePolicyManager", "Stopping user %d", Integer.valueOf(i));
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            stopUser = this.mInjector.getIActivityManager().stopUser(i, true, (IStopUserCallback) null);
        } catch (RemoteException unused) {
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            throw th;
        }
        if (stopUser == -2) {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            return 4;
        }
        if (stopUser == 0) {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            return 0;
        }
        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        return 1;
    }

    public List<UserHandle> getSecondaryUsers(ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda65
            public final Object getOrThrow() {
                List lambda$getSecondaryUsers$95;
                lambda$getSecondaryUsers$95 = DevicePolicyManagerService.this.lambda$getSecondaryUsers$95();
                return lambda$getSecondaryUsers$95;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$getSecondaryUsers$95() throws Exception {
        List<UserInfo> aliveUsers = this.mInjector.getUserManager().getAliveUsers();
        ArrayList arrayList = new ArrayList();
        for (UserInfo userInfo : aliveUsers) {
            UserHandle userHandle = userInfo.getUserHandle();
            if (!userHandle.isSystem() && !isManagedProfile(userHandle.getIdentifier())) {
                arrayList.add(userInfo.getUserHandle());
            }
        }
        return arrayList;
    }

    public boolean isEphemeralUser(ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda164
            public final Object getOrThrow() {
                Boolean lambda$isEphemeralUser$96;
                lambda$isEphemeralUser$96 = DevicePolicyManagerService.this.lambda$isEphemeralUser$96(callerIdentity);
                return lambda$isEphemeralUser$96;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isEphemeralUser$96(CallerIdentity callerIdentity) throws Exception {
        return Boolean.valueOf(this.mInjector.getUserManager().isUserEphemeral(callerIdentity.getUserId()));
    }

    public Bundle getApplicationRestrictions(ComponentName componentName, String str, final String str2) {
        final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        if (useDevicePolicyEngine(callerIdentity, "delegation-app-restrictions")) {
            EnforcingAdmin enforceCanQueryAndGetEnforcingAdmin = enforceCanQueryAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS", callerIdentity.getPackageName(), callerIdentity.getUserId());
            LinkedHashMap localPoliciesSetByAdmins = this.mDevicePolicyEngine.getLocalPoliciesSetByAdmins(PolicyDefinition.APPLICATION_RESTRICTIONS(str2), callerIdentity.getUserId());
            if (localPoliciesSetByAdmins.isEmpty() || !localPoliciesSetByAdmins.containsKey(enforceCanQueryAndGetEnforcingAdmin)) {
                return Bundle.EMPTY;
            }
            return (Bundle) ((PolicyValue) localPoliciesSetByAdmins.get(enforceCanQueryAndGetEnforcingAdmin)).getValue();
        }
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-app-restrictions")));
        return (Bundle) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda144
            public final Object getOrThrow() {
                Bundle lambda$getApplicationRestrictions$97;
                lambda$getApplicationRestrictions$97 = DevicePolicyManagerService.this.lambda$getApplicationRestrictions$97(str2, callerIdentity);
                return lambda$getApplicationRestrictions$97;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Bundle lambda$getApplicationRestrictions$97(String str, CallerIdentity callerIdentity) throws Exception {
        Bundle applicationRestrictions = this.mUserManager.getApplicationRestrictions(str, callerIdentity.getUserHandle());
        return applicationRestrictions != null ? applicationRestrictions : Bundle.EMPTY;
    }

    public final String[] populateNonExemptAndExemptFromPolicyApps(String[] strArr, Set<String> set) {
        Preconditions.checkArgument(set.isEmpty(), "outputExemptApps is not empty");
        List<String> listPolicyExemptAppsUnchecked = listPolicyExemptAppsUnchecked(this.mContext);
        if (listPolicyExemptAppsUnchecked.isEmpty()) {
            return strArr;
        }
        HashSet hashSet = new HashSet(listPolicyExemptAppsUnchecked);
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            if (hashSet.contains(str)) {
                set.add(str);
            } else {
                arrayList.add(str);
            }
        }
        String[] strArr2 = new String[arrayList.size()];
        arrayList.toArray(strArr2);
        return strArr2;
    }

    public String[] setPackagesSuspended(ComponentName componentName, String str, String[] strArr, boolean z) {
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        ActiveAdmin activeAdmin;
        String[] packagesSuspendedByAdmin;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        if (isPermissionCheckFlagEnabled()) {
            activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-package-access")));
            synchronized (getLockObject()) {
                profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            }
            activeAdmin = profileOwnerOrDeviceOwnerLocked;
        }
        checkCanExecuteOrThrowUnsafe(20);
        HashSet hashSet = new HashSet();
        String[] populateNonExemptAndExemptFromPolicyApps = populateNonExemptAndExemptFromPolicyApps(strArr, hashSet);
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            packagesSuspendedByAdmin = this.mInjector.getPackageManagerInternal().setPackagesSuspendedByAdmin(callerIdentity.getUserId(), populateNonExemptAndExemptFromPolicyApps, z);
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
        DevicePolicyEventLogger.createEvent(68).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings(populateNonExemptAndExemptFromPolicyApps).write();
        if (packagesSuspendedByAdmin == null) {
            Slogf.m12w("DevicePolicyManager", "PM failed to suspend packages (%s)", Arrays.toString(populateNonExemptAndExemptFromPolicyApps));
            return populateNonExemptAndExemptFromPolicyApps;
        }
        ArraySet arraySet = new ArraySet(populateNonExemptAndExemptFromPolicyApps);
        if (z) {
            arraySet.removeAll(List.of((Object[]) packagesSuspendedByAdmin));
        } else {
            arraySet.addAll(hashSet);
        }
        synchronized (getLockObject()) {
            ArraySet arraySet2 = new ArraySet(activeAdmin.suspendedPackages);
            if (z) {
                arraySet2.addAll(arraySet);
            } else {
                arraySet2.removeAll(arraySet);
            }
            activeAdmin.suspendedPackages = arraySet2.isEmpty() ? null : new ArrayList(arraySet2);
            saveSettingsLocked(callerIdentity.getUserId());
        }
        return hashSet.isEmpty() ? packagesSuspendedByAdmin : buildNonSuspendedPackagesUnionArray(packagesSuspendedByAdmin, hashSet);
    }

    public final String[] buildNonSuspendedPackagesUnionArray(String[] strArr, Set<String> set) {
        String[] strArr2 = new String[strArr.length + set.size()];
        int length = strArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            strArr2[i2] = strArr[i];
            i++;
            i2++;
        }
        for (String str : set) {
            strArr2[i2] = str;
            i2++;
        }
        return strArr2;
    }

    public boolean isPackageSuspended(ComponentName componentName, String str, String str2) {
        boolean isPackageSuspendedForUser;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", callerIdentity.getPackageName(), callerIdentity.getUserId());
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-package-access")));
        }
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                isPackageSuspendedForUser = this.mIPackageManager.isPackageSuspendedForUser(str2, callerIdentity.getUserId());
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "Failed talking to the package manager", e);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return false;
            }
        }
        return isPackageSuspendedForUser;
    }

    public List<String> listPolicyExemptApps() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS") || isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return listPolicyExemptAppsUnchecked(this.mContext);
    }

    public static List<String> listPolicyExemptAppsUnchecked(Context context) {
        String[] stringArray = context.getResources().getStringArray(17236190);
        String[] stringArray2 = context.getResources().getStringArray(17236209);
        ArraySet arraySet = new ArraySet(stringArray.length + stringArray2.length);
        for (String str : stringArray) {
            arraySet.add(str);
        }
        for (String str2 : stringArray2) {
            arraySet.add(str2);
        }
        return new ArrayList(arraySet);
    }

    public void setUserRestriction(ComponentName componentName, String str, String str2, boolean z, boolean z2) {
        CallerIdentity callerIdentity;
        boolean z3;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        if (UserRestrictionsUtils.isValidRestriction(str2)) {
            checkCanExecuteOrThrowUnsafe(10);
            if (useDevicePolicyEngine(callerIdentity, null)) {
                int profileParentId = z2 ? getProfileParentId(userId) : userId;
                EnforcingAdmin enforcePermissionForUserRestriction = enforcePermissionForUserRestriction(componentName, str2, callerIdentity.getPackageName(), profileParentId);
                PolicyDefinition<Boolean> policyDefinitionForUserRestriction = PolicyDefinition.getPolicyDefinitionForUserRestriction(str2);
                if (z) {
                    this.mDevicePolicyEngine.setLocalPolicy(policyDefinitionForUserRestriction, enforcePermissionForUserRestriction, new BooleanPolicyValue(true), profileParentId);
                } else {
                    if (!policyDefinitionForUserRestriction.isLocalOnlyPolicy()) {
                        this.mDevicePolicyEngine.removeGlobalPolicy(policyDefinitionForUserRestriction, enforcePermissionForUserRestriction);
                    }
                    if (!policyDefinitionForUserRestriction.isGlobalOnlyPolicy()) {
                        this.mDevicePolicyEngine.removeLocalPolicy(policyDefinitionForUserRestriction, enforcePermissionForUserRestriction, userId);
                        int profileParentId2 = getProfileParentId(userId);
                        if (profileParentId2 != userId) {
                            this.mDevicePolicyEngine.removeLocalPolicy(policyDefinitionForUserRestriction, enforcePermissionForUserRestriction, profileParentId2);
                        }
                    }
                }
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                if (z2) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
                } else {
                    Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                }
                synchronized (getLockObject()) {
                    if (isDefaultDeviceOwner(callerIdentity)) {
                        if (!UserRestrictionsUtils.canDeviceOwnerChange(str2)) {
                            throw new SecurityException("Device owner cannot set user restriction " + str2);
                        }
                        Preconditions.checkArgument(z2 ? false : true, "Cannot use the parent instance in Device Owner mode");
                    } else if (!isFinancedDeviceOwner(callerIdentity)) {
                        if (!z2) {
                            if (UserRestrictionsUtils.canProfileOwnerChange(str2, userId == getMainUserId())) {
                                z3 = true;
                                if (z2 && isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) && UserRestrictionsUtils.canProfileOwnerOfOrganizationOwnedDeviceChange(str2)) {
                                    r6 = true;
                                }
                                if (!z3 && !r6) {
                                    throw new SecurityException("Profile owner cannot set user restriction " + str2);
                                }
                            }
                        }
                        z3 = false;
                        if (z2) {
                            r6 = true;
                        }
                        if (!z3) {
                            throw new SecurityException("Profile owner cannot set user restriction " + str2);
                        }
                    } else if (!UserRestrictionsUtils.canFinancedDeviceOwnerChange(str2)) {
                        throw new SecurityException("Cannot set user restriction " + str2 + " when managing a financed device");
                    } else {
                        Preconditions.checkArgument(z2 ? false : true, "Cannot use the parent instance in Financed Device Owner mode");
                    }
                }
                synchronized (getLockObject()) {
                    Bundle ensureUserRestrictions = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(userId), z2).ensureUserRestrictions();
                    if (z) {
                        ensureUserRestrictions.putBoolean(str2, true);
                    } else {
                        ensureUserRestrictions.remove(str2);
                    }
                    saveUserRestrictionsLocked(userId);
                }
            }
            logUserRestrictionCall(str2, z, z2, callerIdentity);
        }
    }

    public void setUserRestrictionGlobally(String str, String str2) {
        CallerIdentity callerIdentity = getCallerIdentity(str);
        if (UserRestrictionsUtils.isValidRestriction(str2)) {
            callerIdentity.getUserId();
            checkCanExecuteOrThrowUnsafe(10);
            if (!useDevicePolicyEngine(callerIdentity, null)) {
                throw new IllegalStateException("One or more admins are not targeting Android 14.");
            }
            this.mDevicePolicyEngine.setGlobalPolicy(PolicyDefinition.getPolicyDefinitionForUserRestriction(str2), enforcePermissionForUserRestriction(null, str2, callerIdentity.getPackageName(), callerIdentity.getUserId()), new BooleanPolicyValue(true));
            logUserRestrictionCall(str2, true, false, callerIdentity);
        }
    }

    public final void logUserRestrictionCall(String str, boolean z, boolean z2, CallerIdentity callerIdentity) {
        DevicePolicyEventLogger admin = DevicePolicyEventLogger.createEvent(z ? 12 : 13).setAdmin(callerIdentity.getComponentName());
        String[] strArr = new String[2];
        strArr[0] = str;
        strArr[1] = z2 ? "calledFromParent" : "notCalledFromParent";
        admin.setStrings(strArr).write();
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(z ? 210027 : 210028, new Object[]{callerIdentity.getPackageName(), Integer.valueOf(callerIdentity.getUserId()), str});
        }
    }

    public final void saveUserRestrictionsLocked(int i) {
        if (isDevicePolicyEngineEnabled()) {
            return;
        }
        saveSettingsLocked(i);
        pushUserRestrictions(i);
        sendChangedNotification(i);
    }

    public final void pushUserRestrictions(int i) {
        Bundle globalUserRestrictions;
        RestrictionsSet restrictionsSet = new RestrictionsSet();
        synchronized (getLockObject()) {
            boolean isDeviceOwnerUserId = this.mOwners.isDeviceOwnerUserId(i);
            if (isDeviceOwnerUserId) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked == null) {
                    return;
                }
                globalUserRestrictions = deviceOwnerAdminLocked.getGlobalUserRestrictions(0);
                restrictionsSet.updateRestrictions(i, deviceOwnerAdminLocked.getLocalUserRestrictions(0));
            } else {
                ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
                if (profileOwnerAdminLocked == null) {
                    return;
                }
                globalUserRestrictions = profileOwnerAdminLocked.getGlobalUserRestrictions(1);
                restrictionsSet.updateRestrictions(i, profileOwnerAdminLocked.getLocalUserRestrictions(1));
                if (isProfileOwnerOfOrganizationOwnedDevice(profileOwnerAdminLocked.getUserHandle().getIdentifier())) {
                    UserRestrictionsUtils.merge(globalUserRestrictions, profileOwnerAdminLocked.getParentActiveAdmin().getGlobalUserRestrictions(2));
                    restrictionsSet.updateRestrictions(getProfileParentId(profileOwnerAdminLocked.getUserHandle().getIdentifier()), profileOwnerAdminLocked.getParentActiveAdmin().getLocalUserRestrictions(2));
                }
            }
            this.mUserManagerInternal.setDevicePolicyUserRestrictions(i, globalUserRestrictions, restrictionsSet, isDeviceOwnerUserId);
        }
    }

    public Bundle getUserRestrictions(ComponentName componentName, String str, boolean z) {
        CallerIdentity callerIdentity;
        Bundle bundle;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            if (useDevicePolicyEngine(callerIdentity, null)) {
                EnforcingAdmin enforcingAdminForCaller = getEnforcingAdminForCaller(componentName, str);
                int userId = callerIdentity.getUserId();
                if (z) {
                    userId = getProfileParentId(userId);
                }
                return getUserRestrictionsFromPolicyEngine(enforcingAdminForCaller, userId);
            }
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || (z && isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)));
            synchronized (getLockObject()) {
                bundle = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z).userRestrictions;
            }
            return bundle;
        }
        return null;
    }

    public final EnforcingAdmin enforcePermissionForUserRestriction(ComponentName componentName, String str, String str2, int i) {
        String str3 = USER_RESTRICTION_PERMISSIONS.get(str);
        if (str3 != null) {
            return enforcePermissionAndGetEnforcingAdmin(componentName, str3, str2, i);
        }
        throw new SecurityException("Admins are not permitted to set User Restriction: " + str);
    }

    public Bundle getUserRestrictionsGlobally(String str) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(str);
            if (!useDevicePolicyEngine(callerIdentity, null)) {
                throw new IllegalStateException("One or more admins are not targeting Android 14.");
            }
            return getUserRestrictionsFromPolicyEngine(getEnforcingAdminForCaller(null, callerIdentity.getPackageName()), -1);
        }
        return null;
    }

    public final Bundle getUserRestrictionsFromPolicyEngine(EnforcingAdmin enforcingAdmin, int i) {
        Set<UserRestrictionPolicyKey> userRestrictionPolicyKeysForAdmin = this.mDevicePolicyEngine.getUserRestrictionPolicyKeysForAdmin(enforcingAdmin, i);
        Bundle bundle = new Bundle();
        for (UserRestrictionPolicyKey userRestrictionPolicyKey : userRestrictionPolicyKeysForAdmin) {
            bundle.putBoolean(userRestrictionPolicyKey.getRestriction(), true);
        }
        return bundle;
    }

    public boolean setApplicationHidden(ComponentName componentName, String str, final String str2, final boolean z, boolean z2) {
        boolean booleanValue;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        final int userId = callerIdentity.getUserId();
        if (z2) {
            userId = getProfileParentId(userId);
        }
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", callerIdentity.getPackageName(), userId);
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-package-access")));
        }
        if (listPolicyExemptAppsUnchecked(this.mContext).contains(str2)) {
            Slogf.m28d("DevicePolicyManager", "setApplicationHidden(): ignoring %s as it's on policy-exempt list", str2);
            return false;
        }
        synchronized (getLockObject()) {
            if (z2) {
                if (!isPermissionCheckFlagEnabled()) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId()) && isManagedProfile(callerIdentity.getUserId()));
                }
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda133
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setApplicationHidden$98(str2, userId);
                    }
                });
            }
            checkCanExecuteOrThrowUnsafe(15);
            booleanValue = ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda134
                public final Object getOrThrow() {
                    Boolean lambda$setApplicationHidden$99;
                    lambda$setApplicationHidden$99 = DevicePolicyManagerService.this.lambda$setApplicationHidden$99(str2, z, userId);
                    return lambda$setApplicationHidden$99;
                }
            })).booleanValue();
        }
        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(63).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate(callerIdentity));
        String[] strArr = new String[3];
        strArr[0] = str2;
        strArr[1] = z ? "hidden" : "not_hidden";
        strArr[2] = z2 ? "calledFromParent" : "notCalledFromParent";
        devicePolicyEventLogger.setStrings(strArr).write();
        return booleanValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$setApplicationHidden$99(String str, boolean z, int i) throws Exception {
        return Boolean.valueOf(this.mIPackageManager.setApplicationHiddenSettingAsUser(str, z, i));
    }

    public boolean isApplicationHidden(ComponentName componentName, String str, final String str2, boolean z) {
        boolean booleanValue;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        final int userId = callerIdentity.getUserId();
        if (z) {
            userId = getProfileParentId(userId);
        }
        boolean z2 = true;
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE", callerIdentity.getPackageName(), userId);
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-package-access")));
        }
        synchronized (getLockObject()) {
            if (z) {
                if (!isPermissionCheckFlagEnabled()) {
                    if (!isProfileOwnerOfOrganizationOwnedDevice(callerIdentity.getUserId()) || !isManagedProfile(callerIdentity.getUserId())) {
                        z2 = false;
                    }
                    Preconditions.checkCallAuthorization(z2);
                }
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda141
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$isApplicationHidden$100(str2, userId);
                    }
                });
            }
            booleanValue = ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda142
                public final Object getOrThrow() {
                    Boolean lambda$isApplicationHidden$101;
                    lambda$isApplicationHidden$101 = DevicePolicyManagerService.this.lambda$isApplicationHidden$101(str2, userId);
                    return lambda$isApplicationHidden$101;
                }
            })).booleanValue();
        }
        return booleanValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isApplicationHidden$101(String str, int i) throws Exception {
        return Boolean.valueOf(this.mIPackageManager.getApplicationHiddenSettingAsUser(str, i));
    }

    /* renamed from: enforcePackageIsSystemPackage */
    public final void lambda$setDefaultSmsApplication$83(String str, int i) throws RemoteException {
        boolean z;
        try {
            z = isSystemApp(this.mIPackageManager, str, i);
        } catch (IllegalArgumentException unused) {
            z = false;
        }
        if (!z) {
            throw new IllegalArgumentException("The provided package is not a system package");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0062 A[Catch: all -> 0x004b, RemoteException -> 0x004d, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x004d, blocks: (B:16:0x003c, B:25:0x0050, B:27:0x0062), top: B:39:0x003c, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x009b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void enableSystemApp(ComponentName componentName, String str, String str2) {
        boolean z;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-enable-system-app")));
        boolean isCurrentUserDemo = isCurrentUserDemo();
        int userId = callerIdentity.getUserId();
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        if (!isCurrentUserDemo) {
            try {
                try {
                } catch (RemoteException e) {
                    Slogf.wtf("DevicePolicyManager", "Failed to install " + str2, e);
                }
                if (!isSystemApp(this.mIPackageManager, str2, getProfileParentId(userId))) {
                    z = false;
                    Preconditions.checkArgument(z, "Only system apps can be enabled this way");
                    this.mIPackageManager.installExistingPackageAsUser(str2, userId, 4194304, 1, (List) null);
                    if (isCurrentUserDemo) {
                        this.mIPackageManager.setApplicationEnabledSetting(str2, 1, 1, userId, "DevicePolicyManager");
                    }
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    DevicePolicyEventLogger.createEvent(64).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings(new String[]{str2}).write();
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                throw th;
            }
        }
        z = true;
        Preconditions.checkArgument(z, "Only system apps can be enabled this way");
        this.mIPackageManager.installExistingPackageAsUser(str2, userId, 4194304, 1, (List) null);
        if (isCurrentUserDemo) {
        }
        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        DevicePolicyEventLogger.createEvent(64).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings(new String[]{str2}).write();
    }

    public int enableSystemAppWithIntent(ComponentName componentName, String str, Intent intent) {
        int i;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-enable-system-app")));
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                int profileParentId = getProfileParentId(callerIdentity.getUserId());
                List<ResolveInfo> list = this.mIPackageManager.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432L, profileParentId).getList();
                if (list != null) {
                    i = 0;
                    for (ResolveInfo resolveInfo : list) {
                        ActivityInfo activityInfo = resolveInfo.activityInfo;
                        if (activityInfo != null) {
                            String str2 = activityInfo.packageName;
                            if (isSystemApp(this.mIPackageManager, str2, profileParentId)) {
                                i++;
                                this.mIPackageManager.installExistingPackageAsUser(str2, callerIdentity.getUserId(), 4194304, 1, (List) null);
                            } else {
                                Slogf.m30d("DevicePolicyManager", "Not enabling " + str2 + " since is not a system app");
                            }
                        }
                    }
                } else {
                    i = 0;
                }
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                DevicePolicyEventLogger.createEvent(65).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings(new String[]{intent.getAction()}).write();
                return i;
            } catch (RemoteException e) {
                Slogf.wtf("DevicePolicyManager", "Failed to resolve intent for: " + intent, e);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return 0;
            }
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            throw th;
        }
    }

    public final boolean isSystemApp(IPackageManager iPackageManager, String str, int i) throws RemoteException {
        ApplicationInfo applicationInfo = iPackageManager.getApplicationInfo(str, 8192L, i);
        if (applicationInfo != null) {
            return (applicationInfo.flags & 1) != 0;
        }
        throw new IllegalArgumentException("The application " + str + " is not present on this device");
    }

    public boolean installExistingPackage(ComponentName componentName, String str, String str2) {
        boolean z;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-install-existing-package")));
        synchronized (getLockObject()) {
            Preconditions.checkCallAuthorization(isUserAffiliatedWithDeviceLocked(callerIdentity.getUserId()), "Admin %s is neither the device owner or affiliated user's profile owner.", new Object[]{componentName});
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                z = this.mIPackageManager.installExistingPackageAsUser(str2, callerIdentity.getUserId(), 4194304, 1, (List) null) == 1;
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            } catch (RemoteException e) {
                Slogf.wtf("DevicePolicyManager", "Error installing package", e);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                return false;
            }
        }
        if (z) {
            DevicePolicyEventLogger.createEvent(66).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setStrings(new String[]{str2}).write();
        }
        return z;
    }

    public void setAccountManagementDisabled(ComponentName componentName, String str, String str2, boolean z, boolean z2) {
        CallerIdentity callerIdentity;
        boolean z3;
        ActiveAdmin parentOfAdminIfRequired;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    parentOfAdminIfRequired = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", callerIdentity.getPackageName(), getAffectedUser(z2)).getActiveAdmin();
                } else {
                    Objects.requireNonNull(componentName, "ComponentName is null");
                    if (z2) {
                        parentOfAdminIfRequired = getParentOfAdminIfRequired(getOrganizationOwnedProfileOwnerLocked(callerIdentity), z2);
                    } else {
                        if (!isDefaultDeviceOwner(callerIdentity) && !isProfileOwner(callerIdentity)) {
                            z3 = false;
                            Preconditions.checkCallAuthorization(z3);
                            parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z2);
                        }
                        z3 = true;
                        Preconditions.checkCallAuthorization(z3);
                        parentOfAdminIfRequired = getParentOfAdminIfRequired(getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()), z2);
                    }
                }
                if (z) {
                    parentOfAdminIfRequired.accountTypesWithManagementDisabled.add(str2);
                } else {
                    parentOfAdminIfRequired.accountTypesWithManagementDisabled.remove(str2);
                }
                saveSettingsLocked(UserHandle.getCallingUserId());
            }
        }
    }

    public String[] getAccountTypesWithManagementDisabled(String str) {
        return getAccountTypesWithManagementDisabledAsUser(UserHandle.getCallingUserId(), str, false);
    }

    public String[] getAccountTypesWithManagementDisabledAsUser(int i, String str, boolean z) {
        String[] strArr;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            if (isPermissionCheckFlagEnabled()) {
                CallerIdentity callerIdentity = getCallerIdentity(str);
                if (!hasPermission("android.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT", callerIdentity.getPackageName(), i) && !hasFullCrossUsersPermission(callerIdentity, i)) {
                    throw new SecurityException("Caller does not have permission to call this on user: " + i);
                }
            } else {
                Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
            }
            synchronized (getLockObject()) {
                ArraySet arraySet = new ArraySet();
                if (!z) {
                    Iterator<ActiveAdmin> it = lambda$getUserDataUnchecked$2(i).mAdminList.iterator();
                    while (it.hasNext()) {
                        arraySet.addAll(it.next().accountTypesWithManagementDisabled);
                    }
                }
                ActiveAdmin profileOwnerOfOrganizationOwnedDeviceLocked = getProfileOwnerOfOrganizationOwnedDeviceLocked(i);
                if (profileOwnerOfOrganizationOwnedDeviceLocked != null && (z || UserHandle.getUserId(profileOwnerOfOrganizationOwnedDeviceLocked.getUid()) != i)) {
                    arraySet.addAll(profileOwnerOfOrganizationOwnedDeviceLocked.getParentActiveAdmin().accountTypesWithManagementDisabled);
                }
                strArr = (String[]) arraySet.toArray(new String[arraySet.size()]);
            }
            return strArr;
        }
        return null;
    }

    public void setUninstallBlocked(ComponentName componentName, String str, String str2, boolean z) {
        Injector injector;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        if (useDevicePolicyEngine(callerIdentity, "delegation-block-uninstall")) {
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.PACKAGE_UNINSTALL_BLOCKED(str2), enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", callerIdentity.getPackageName(), callerIdentity.getUserId()), new BooleanPolicyValue(z), callerIdentity.getUserId());
        } else {
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-block-uninstall")));
            int userId = callerIdentity.getUserId();
            synchronized (getLockObject()) {
                long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                try {
                    this.mIPackageManager.setBlockUninstallForUser(str2, z, userId);
                    injector = this.mInjector;
                } catch (RemoteException e) {
                    Slogf.m25e("DevicePolicyManager", "Failed to setBlockUninstallForUser", e);
                    injector = this.mInjector;
                }
                injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
            if (z) {
                PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
                packageManagerInternal.removeNonSystemPackageSuspensions(str2, userId);
                packageManagerInternal.removeDistractingPackageRestrictions(str2, userId);
                packageManagerInternal.flushPackageRestrictions(userId);
            }
        }
        DevicePolicyEventLogger.createEvent(67).setAdmin(callerIdentity.getPackageName()).setBoolean(isCallerDelegate(callerIdentity)).setStrings(new String[]{str2}).write();
    }

    public static void setUninstallBlockedUnchecked(final String str, final boolean z, final int i) {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda197
            public final void runOrThrow() {
                DevicePolicyManagerService.lambda$setUninstallBlockedUnchecked$102(str, z, i);
            }
        });
        if (z) {
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            packageManagerInternal.removeNonSystemPackageSuspensions(str, i);
            packageManagerInternal.removeDistractingPackageRestrictions(str, i);
            packageManagerInternal.flushPackageRestrictions(i);
        }
    }

    public static /* synthetic */ void lambda$setUninstallBlockedUnchecked$102(String str, boolean z, int i) throws Exception {
        try {
            AppGlobals.getPackageManager().setBlockUninstallForUser(str, z, i);
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Failed to setBlockUninstallForUser", e);
        }
    }

    public boolean isUninstallBlocked(String str) {
        boolean blockUninstallForUser;
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            try {
                try {
                    blockUninstallForUser = this.mIPackageManager.getBlockUninstallForUser(str, callingUserId);
                } catch (RemoteException e) {
                    Slogf.m25e("DevicePolicyManager", "Failed to getBlockUninstallForUser", e);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return blockUninstallForUser;
    }

    public void setCrossProfileCallerIdDisabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                if (z) {
                    profileOwnerLocked.mManagedProfileCallerIdAccess = new PackagePolicy(3);
                } else {
                    profileOwnerLocked.mManagedProfileCallerIdAccess = new PackagePolicy(1);
                }
                saveSettingsLocked(callerIdentity.getUserId());
            }
            DevicePolicyEventLogger.createEvent(46).setAdmin(componentName).setBoolean(z).write();
        }
    }

    public boolean getCrossProfileCallerIdDisabled(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerLocked == null) {
                    return false;
                }
                PackagePolicy packagePolicy = profileOwnerLocked.mManagedProfileCallerIdAccess;
                if (packagePolicy == null) {
                    return profileOwnerLocked.disableCallerId;
                }
                if (packagePolicy.getPolicyType() == 2) {
                    Slogf.m14w("DevicePolicyManager", "Denying callerId due to PACKAGE_POLICY_SYSTEM policyType");
                }
                return profileOwnerLocked.mManagedProfileCallerIdAccess.getPolicyType() != 1;
            }
        }
        return false;
    }

    public boolean getCrossProfileCallerIdDisabledForUser(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasCrossUsersPermission(getCallerIdentity(), i));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked == null) {
                return false;
            }
            PackagePolicy packagePolicy = profileOwnerAdminLocked.mManagedProfileCallerIdAccess;
            if (packagePolicy == null) {
                return profileOwnerAdminLocked.disableCallerId;
            }
            return packagePolicy.getPolicyType() == 3;
        }
    }

    public void setManagedProfileCallerIdAccessPolicy(PackagePolicy packagePolicy) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId()));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                profileOwnerLocked.disableCallerId = false;
                profileOwnerLocked.mManagedProfileCallerIdAccess = packagePolicy;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    public PackagePolicy getManagedProfileCallerIdAccessPolicy() {
        PackagePolicy packagePolicy;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId()));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                packagePolicy = profileOwnerLocked != null ? profileOwnerLocked.mManagedProfileCallerIdAccess : null;
            }
            return packagePolicy;
        }
        return null;
    }

    public boolean hasManagedProfileCallerIdAccess(int i, String str) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasCrossUsersPermission(getCallerIdentity(), i));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            boolean z = true;
            if (profileOwnerAdminLocked != null) {
                PackagePolicy packagePolicy = profileOwnerAdminLocked.mManagedProfileCallerIdAccess;
                if (packagePolicy == null) {
                    if (profileOwnerAdminLocked.disableCallerId) {
                        z = false;
                    }
                    return z;
                }
                return packagePolicy.isPackageAllowed(str, this.mContactSystemRoleHolders);
            }
            return true;
        }
    }

    public void setManagedProfileContactsAccessPolicy(PackagePolicy packagePolicy) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId()));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                profileOwnerLocked.disableContactsSearch = false;
                profileOwnerLocked.mManagedProfileContactsAccess = packagePolicy;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    public PackagePolicy getManagedProfileContactsAccessPolicy() {
        PackagePolicy packagePolicy;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId()));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                packagePolicy = profileOwnerLocked != null ? profileOwnerLocked.mManagedProfileContactsAccess : null;
            }
            return packagePolicy;
        }
        return null;
    }

    public boolean hasManagedProfileContactsAccess(int i, String str) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasCrossUsersPermission(getCallerIdentity(), i));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            boolean z = true;
            if (profileOwnerAdminLocked != null) {
                PackagePolicy packagePolicy = profileOwnerAdminLocked.mManagedProfileContactsAccess;
                if (packagePolicy == null) {
                    if (profileOwnerAdminLocked.disableContactsSearch) {
                        z = false;
                    }
                    return z;
                }
                return packagePolicy.isPackageAllowed(str, this.mContactSystemRoleHolders);
            }
            return true;
        }
    }

    public void setCrossProfileContactsSearchDisabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                if (z) {
                    profileOwnerLocked.mManagedProfileContactsAccess = new PackagePolicy(3);
                } else {
                    profileOwnerLocked.mManagedProfileContactsAccess = new PackagePolicy(1);
                }
                saveSettingsLocked(callerIdentity.getUserId());
            }
            DevicePolicyEventLogger.createEvent(45).setAdmin(componentName).setBoolean(z).write();
        }
    }

    public boolean getCrossProfileContactsSearchDisabled(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerLocked == null) {
                    return false;
                }
                PackagePolicy packagePolicy = profileOwnerLocked.mManagedProfileContactsAccess;
                if (packagePolicy == null) {
                    return profileOwnerLocked.disableContactsSearch;
                }
                return packagePolicy.getPolicyType() != 1;
            }
        }
        return false;
    }

    public boolean getCrossProfileContactsSearchDisabledForUser(int i) {
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasCrossUsersPermission(getCallerIdentity(), i));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked == null) {
                return false;
            }
            PackagePolicy packagePolicy = profileOwnerAdminLocked.mManagedProfileContactsAccess;
            if (packagePolicy == null) {
                return profileOwnerAdminLocked.disableContactsSearch;
            }
            if (packagePolicy.getPolicyType() == 2) {
                Slogf.m14w("DevicePolicyManager", "Denying contacts due to PACKAGE_POLICY_SYSTEM policyType");
            }
            return profileOwnerAdminLocked.mManagedProfileContactsAccess.getPolicyType() != 1;
        }
    }

    public void startManagedQuickContact(String str, long j, boolean z, long j2, Intent intent) {
        final Intent rebuildManagedQuickContactsIntent = ContactsContract.QuickContact.rebuildManagedQuickContactsIntent(str, j, z, j2, intent);
        final int callingUserId = UserHandle.getCallingUserId();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda124
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$startManagedQuickContact$103(callingUserId, rebuildManagedQuickContactsIntent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startManagedQuickContact$103(int i, Intent intent) throws Exception {
        synchronized (getLockObject()) {
            int managedUserId = getManagedUserId(i);
            if (managedUserId < 0) {
                return;
            }
            if (isCrossProfileQuickContactDisabled(managedUserId)) {
                return;
            }
            ContactsInternal.startQuickContactWithErrorToastForUser(this.mContext, intent, new UserHandle(managedUserId));
        }
    }

    public final boolean isCrossProfileQuickContactDisabled(int i) {
        return getCrossProfileCallerIdDisabledForUser(i) && getCrossProfileContactsSearchDisabledForUser(i);
    }

    public int getManagedUserId(int i) {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            if (userInfo.id != i && userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    public final int getManagedUserId() {
        UserHandle mainUser = this.mUserManager.getMainUser();
        if (mainUser == null) {
            return -10000;
        }
        return getManagedUserId(mainUser.getIdentifier());
    }

    public void setBluetoothContactSharingDisabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                if (profileOwnerOrDeviceOwnerLocked.disableBluetoothContactSharing != z) {
                    profileOwnerOrDeviceOwnerLocked.disableBluetoothContactSharing = z;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(47).setAdmin(componentName).setBoolean(z).write();
        }
    }

    public boolean getBluetoothContactSharingDisabled(ComponentName componentName) {
        boolean z;
        boolean z2 = false;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity)) ? true : true);
            synchronized (getLockObject()) {
                z = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).disableBluetoothContactSharing;
            }
            return z;
        }
        return false;
    }

    public boolean getBluetoothContactSharingDisabledForUser(int i) {
        boolean z;
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            z = profileOwnerAdminLocked != null ? profileOwnerAdminLocked.disableBluetoothContactSharing : false;
        }
        return z;
    }

    public void setSecondaryLockscreenEnabled(ComponentName componentName, boolean z) {
        boolean z2;
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        Preconditions.checkCallAuthorization(!isManagedProfile(callerIdentity.getUserId()), "User %d is not allowed to call setSecondaryLockscreenEnabled", new Object[]{Integer.valueOf(callerIdentity.getUserId())});
        synchronized (getLockObject()) {
            if (!isAdminTestOnlyLocked(componentName, callerIdentity.getUserId()) && !isSupervisionComponentLocked(callerIdentity.getComponentName())) {
                z2 = false;
                Preconditions.checkCallAuthorization(z2, "Admin %s is not the default supervision component", new Object[]{callerIdentity.getComponentName()});
                lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mSecondaryLockscreenEnabled = z;
                saveSettingsLocked(callerIdentity.getUserId());
            }
            z2 = true;
            Preconditions.checkCallAuthorization(z2, "Admin %s is not the default supervision component", new Object[]{callerIdentity.getComponentName()});
            lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mSecondaryLockscreenEnabled = z;
            saveSettingsLocked(callerIdentity.getUserId());
        }
    }

    public boolean isSecondaryLockscreenEnabled(UserHandle userHandle) {
        boolean z;
        synchronized (getLockObject()) {
            z = lambda$getUserDataUnchecked$2(userHandle.getIdentifier()).mSecondaryLockscreenEnabled;
        }
        return z;
    }

    public final boolean isManagedProfileOwner(CallerIdentity callerIdentity) {
        return isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId());
    }

    public void setPreferentialNetworkServiceConfigs(List<PreferentialNetworkServiceConfig> list) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization((isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId())) || isDefaultDeviceOwner(callerIdentity), "Caller is not managed profile owner or device owner; only managed profile owner or device owner may control the preferential network service");
            synchronized (getLockObject()) {
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId());
                if (!deviceOrProfileOwnerAdminLocked.mPreferentialNetworkServiceConfigs.equals(list)) {
                    deviceOrProfileOwnerAdminLocked.mPreferentialNetworkServiceConfigs = new ArrayList(list);
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
            updateNetworkPreferenceForUser(callerIdentity.getUserId(), list);
            DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.f430xa4f1b8b3).setBoolean(list.stream().anyMatch(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda52
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean isEnabled;
                    isEnabled = ((PreferentialNetworkServiceConfig) obj).isEnabled();
                    return isEnabled;
                }
            })).write();
        }
    }

    public List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs() {
        List<PreferentialNetworkServiceConfig> list;
        if (!this.mHasFeature) {
            return List.of(PreferentialNetworkServiceConfig.DEFAULT);
        }
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization((isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId())) || isDefaultDeviceOwner(callerIdentity), "Caller is not managed profile owner or device owner; only managed profile owner or device owner may retrieve the preferential network service configurations");
        synchronized (getLockObject()) {
            list = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId()).mPreferentialNetworkServiceConfigs;
        }
        return list;
    }

    public void setLockTaskPackages(ComponentName componentName, String str, String[] strArr) throws SecurityException {
        CallerIdentity callerIdentity;
        EnforcingAdmin enforceCanCallLockTaskLocked;
        LockTaskPolicy lockTaskPolicy;
        Objects.requireNonNull(strArr, "packages is null");
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        checkCanExecuteOrThrowUnsafe(19);
        if (useDevicePolicyEngine(callerIdentity, null)) {
            synchronized (getLockObject()) {
                enforceCanCallLockTaskLocked = enforceCanCallLockTaskLocked(componentName, str);
            }
            if (strArr.length == 0) {
                this.mDevicePolicyEngine.removeLocalPolicy(PolicyDefinition.LOCK_TASK, enforceCanCallLockTaskLocked, callerIdentity.getUserId());
                return;
            }
            LockTaskPolicy lockTaskPolicy2 = (LockTaskPolicy) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.LOCK_TASK, enforceCanCallLockTaskLocked, callerIdentity.getUserId());
            if (lockTaskPolicy2 == null) {
                lockTaskPolicy = new LockTaskPolicy(Set.of((Object[]) strArr));
            } else {
                LockTaskPolicy lockTaskPolicy3 = new LockTaskPolicy(lockTaskPolicy2);
                lockTaskPolicy3.setPackages(Set.of((Object[]) strArr));
                lockTaskPolicy = lockTaskPolicy3;
            }
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.LOCK_TASK, enforceCanCallLockTaskLocked, lockTaskPolicy, callerIdentity.getUserId());
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(callerIdentity);
            setLockTaskPackagesLocked(callerIdentity.getUserId(), new ArrayList(Arrays.asList(strArr)));
        }
    }

    public final void setLockTaskPackagesLocked(int i, List<String> list) {
        lambda$getUserDataUnchecked$2(i).mLockTaskPackages = list;
        saveSettingsLocked(i);
        updateLockTaskPackagesLocked(this.mContext, list, i);
    }

    public String[] getLockTaskPackages(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        String[] strArr;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        if (useDevicePolicyEngine(callerIdentity, null)) {
            synchronized (getLockObject()) {
                enforceCanQueryLockTaskLocked(componentName, callerIdentity.getPackageName());
            }
            LockTaskPolicy lockTaskPolicy = (LockTaskPolicy) this.mDevicePolicyEngine.getResolvedPolicy(PolicyDefinition.LOCK_TASK, userId);
            return lockTaskPolicy == null ? new String[0] : (String[]) lockTaskPolicy.getPackages().toArray(new String[lockTaskPolicy.getPackages().size()]);
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(callerIdentity);
            List<String> list = lambda$getUserDataUnchecked$2(userId).mLockTaskPackages;
            strArr = (String[]) list.toArray(new String[list.size()]);
        }
        return strArr;
    }

    public boolean isLockTaskPermitted(String str) {
        boolean contains;
        if (listPolicyExemptAppsUnchecked(this.mContext).contains(str)) {
            return true;
        }
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        if (isDevicePolicyEngineFlagEnabled() && this.mDevicePolicyEngine.hasActivePolicies()) {
            LockTaskPolicy lockTaskPolicy = (LockTaskPolicy) this.mDevicePolicyEngine.getResolvedPolicy(PolicyDefinition.LOCK_TASK, userHandleGetCallingUserId);
            if (lockTaskPolicy == null) {
                return false;
            }
            return lockTaskPolicy.getPackages().contains(str);
        }
        synchronized (getLockObject()) {
            contains = lambda$getUserDataUnchecked$2(userHandleGetCallingUserId).mLockTaskPackages.contains(str);
        }
        return contains;
    }

    public void setLockTaskFeatures(ComponentName componentName, String str, int i) {
        CallerIdentity callerIdentity;
        EnforcingAdmin enforceCanCallLockTaskLocked;
        boolean z = true;
        boolean z2 = (i & 4) != 0;
        Preconditions.checkArgument(z2 || !((i & 8) != 0), "Cannot use LOCK_TASK_FEATURE_OVERVIEW without LOCK_TASK_FEATURE_HOME");
        boolean z3 = (i & 2) != 0;
        if (!z2 && z3) {
            z = false;
        }
        Preconditions.checkArgument(z, "Cannot use LOCK_TASK_FEATURE_NOTIFICATIONS without LOCK_TASK_FEATURE_HOME");
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            checkCanExecuteOrThrowUnsafe(18);
        }
        if (useDevicePolicyEngine(callerIdentity, null)) {
            synchronized (getLockObject()) {
                enforceCanCallLockTaskLocked = enforceCanCallLockTaskLocked(componentName, str);
                enforceCanSetLockTaskFeaturesOnFinancedDevice(callerIdentity, i);
            }
            LockTaskPolicy lockTaskPolicy = (LockTaskPolicy) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.LOCK_TASK, enforceCanCallLockTaskLocked, callerIdentity.getUserId());
            if (lockTaskPolicy == null) {
                throw new IllegalArgumentException("Can't set a lock task flags without setting lock task packages first.");
            }
            LockTaskPolicy lockTaskPolicy2 = new LockTaskPolicy(lockTaskPolicy);
            lockTaskPolicy2.setFlags(i);
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.LOCK_TASK, enforceCanCallLockTaskLocked, lockTaskPolicy2, callerIdentity.getUserId());
            return;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(callerIdentity);
            enforceCanSetLockTaskFeaturesOnFinancedDevice(callerIdentity, i);
            setLockTaskFeaturesLocked(userId, i);
        }
    }

    public final void setLockTaskFeaturesLocked(int i, int i2) {
        lambda$getUserDataUnchecked$2(i).mLockTaskFeatures = i2;
        saveSettingsLocked(i);
        updateLockTaskFeaturesLocked(i2, i);
    }

    public int getLockTaskFeatures(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        int i;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        int userId = callerIdentity.getUserId();
        if (useDevicePolicyEngine(callerIdentity, null)) {
            synchronized (getLockObject()) {
                enforceCanQueryLockTaskLocked(componentName, callerIdentity.getPackageName());
            }
            LockTaskPolicy lockTaskPolicy = (LockTaskPolicy) this.mDevicePolicyEngine.getResolvedPolicy(PolicyDefinition.LOCK_TASK, userId);
            if (lockTaskPolicy == null) {
                return 16;
            }
            return lockTaskPolicy.getFlags();
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(callerIdentity);
            i = lambda$getUserDataUnchecked$2(userId).mLockTaskFeatures;
        }
        return i;
    }

    public final void maybeClearLockTaskPolicyLocked() {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda14
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$maybeClearLockTaskPolicyLocked$105();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeClearLockTaskPolicyLocked$105() throws Exception {
        List aliveUsers = this.mUserManager.getAliveUsers();
        for (int size = aliveUsers.size() - 1; size >= 0; size--) {
            int i = ((UserInfo) aliveUsers.get(size)).id;
            if (!canUserUseLockTaskLocked(i)) {
                if (!lambda$getUserDataUnchecked$2(i).mLockTaskPackages.isEmpty()) {
                    Slogf.m30d("DevicePolicyManager", "User id " + i + " not affiliated. Clearing lock task packages");
                    setLockTaskPackagesLocked(i, Collections.emptyList());
                }
                if (lambda$getUserDataUnchecked$2(i).mLockTaskFeatures != 0) {
                    Slogf.m30d("DevicePolicyManager", "User id " + i + " not affiliated. Clearing lock task features");
                    setLockTaskFeaturesLocked(i, 0);
                }
            }
        }
    }

    public final void enforceCanSetLockTaskFeaturesOnFinancedDevice(CallerIdentity callerIdentity, int i) {
        if (isFinancedDeviceOwner(callerIdentity)) {
            if (i == 0 || (i & (-56)) != 0) {
                throw new SecurityException("Permitted lock task features when managing a financed device: LOCK_TASK_FEATURE_SYSTEM_INFO, LOCK_TASK_FEATURE_KEYGUARD, LOCK_TASK_FEATURE_HOME, LOCK_TASK_FEATURE_GLOBAL_ACTIONS, or LOCK_TASK_FEATURE_NOTIFICATIONS");
            }
        }
    }

    public void notifyLockTaskModeChanged(boolean z, String str, int i) {
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "call notifyLockTaskModeChanged"));
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            if (lambda$getUserDataUnchecked$2.mStatusBarDisabled) {
                setStatusBarDisabledInternal(!z, i);
            }
            Bundle bundle = new Bundle();
            bundle.putString("android.app.extra.LOCK_TASK_PACKAGE", str);
            Iterator<ActiveAdmin> it = lambda$getUserDataUnchecked$2.mAdminList.iterator();
            while (it.hasNext()) {
                ActiveAdmin next = it.next();
                boolean isDeviceOwner = isDeviceOwner(next.info.getComponent(), i);
                boolean isProfileOwner = isProfileOwner(next.info.getComponent(), i);
                if (isDeviceOwner || isProfileOwner) {
                    if (z) {
                        sendAdminCommandLocked(next, "android.app.action.LOCK_TASK_ENTERING", bundle, (BroadcastReceiver) null);
                    } else {
                        sendAdminCommandLocked(next, "android.app.action.LOCK_TASK_EXITING");
                    }
                    DevicePolicyEventLogger.createEvent(51).setAdmin(next.info.getPackageName()).setBoolean(z).setStrings(new String[]{str}).write();
                }
            }
        }
    }

    public void setGlobalSetting(ComponentName componentName, final String str, final String str2) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        DevicePolicyEventLogger.createEvent(111).setAdmin(componentName).setStrings(new String[]{str, str2}).write();
        synchronized (getLockObject()) {
            if (GLOBAL_SETTINGS_DEPRECATED.contains(str)) {
                Slogf.m20i("DevicePolicyManager", "Global setting no longer supported: %s", str);
                return;
            }
            if (!GLOBAL_SETTINGS_ALLOWLIST.contains(str) && !UserManager.isDeviceInDemoMode(this.mContext)) {
                throw new SecurityException(String.format("Permission denial: device owners cannot update %1$s", str));
            }
            if ("stay_on_while_plugged_in".equals(str)) {
                long maximumTimeToLock = getMaximumTimeToLock(componentName, this.mInjector.userHandleGetCallingUserId(), false);
                if (maximumTimeToLock > 0 && maximumTimeToLock < Long.MAX_VALUE) {
                    return;
                }
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda70
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setGlobalSetting$106(str, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setGlobalSetting$106(String str, String str2) throws Exception {
        this.mInjector.settingsGlobalPutString(str, str2);
    }

    public void setSystemSetting(ComponentName componentName, final String str, final String str2) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkStringNotEmpty(str, "String setting is null or empty");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(11);
        synchronized (getLockObject()) {
            if (!SYSTEM_SETTINGS_ALLOWLIST.contains(str)) {
                throw new SecurityException(String.format("Permission denial: device owners cannot update %1$s", str));
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda32
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setSystemSetting$107(str, str2, callerIdentity);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSystemSetting$107(String str, String str2, CallerIdentity callerIdentity) throws Exception {
        this.mInjector.settingsSystemPutStringForUser(str, str2, callerIdentity.getUserId());
    }

    public void setConfiguredNetworksLockdownState(ComponentName componentName, String str, final boolean z) {
        CallerIdentity callerIdentity;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_WIFI", callerIdentity.getPackageName(), -1);
            } else {
                CallerIdentity callerIdentity2 = getCallerIdentity(componentName);
                Preconditions.checkNotNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity2) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity2));
                callerIdentity = callerIdentity2;
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda74
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setConfiguredNetworksLockdownState$108(z);
                }
            });
            DevicePolicyEventLogger.createEvent(132).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setConfiguredNetworksLockdownState$108(boolean z) throws Exception {
        this.mInjector.settingsGlobalPutInt("wifi_device_owner_configs_lockdown", z ? 1 : 0);
    }

    public boolean hasLockdownAdminConfiguredNetworks(ComponentName componentName) {
        boolean z = false;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            if (isPermissionCheckFlagEnabled()) {
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_WIFI", componentName.getPackageName(), -1);
            } else {
                Preconditions.checkNotNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) ? true : true);
            }
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda44
                public final Object getOrThrow() {
                    Boolean lambda$hasLockdownAdminConfiguredNetworks$109;
                    lambda$hasLockdownAdminConfiguredNetworks$109 = DevicePolicyManagerService.this.lambda$hasLockdownAdminConfiguredNetworks$109();
                    return lambda$hasLockdownAdminConfiguredNetworks$109;
                }
            })).booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hasLockdownAdminConfiguredNetworks$109() throws Exception {
        return Boolean.valueOf(this.mInjector.settingsGlobalGetInt("wifi_device_owner_configs_lockdown", 0) > 0);
    }

    public void setLocationEnabled(ComponentName componentName, final boolean z) {
        Preconditions.checkNotNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
        final UserHandle userHandle = callerIdentity.getUserHandle();
        if (this.mIsAutomotive && !z) {
            Slogf.m20i("DevicePolicyManager", "setLocationEnabled(%s, %b): ignoring for user %s on automotive build", componentName.flattenToShortString(), Boolean.valueOf(z), userHandle);
            return;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda114
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setLocationEnabled$110(userHandle, z);
            }
        });
        DevicePolicyEventLogger admin = DevicePolicyEventLogger.createEvent(14).setAdmin(componentName);
        String[] strArr = new String[2];
        strArr[0] = "location_mode";
        strArr[1] = Integer.toString(z ? 3 : 0);
        admin.setStrings(strArr).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLocationEnabled$110(UserHandle userHandle, boolean z) throws Exception {
        boolean isLocationEnabledForUser = this.mInjector.getLocationManager().isLocationEnabledForUser(userHandle);
        Slogf.m16v("DevicePolicyManager", "calling locationMgr.setLocationEnabledForUser(%b, %s) when it was %b", Boolean.valueOf(z), userHandle, Boolean.valueOf(isLocationEnabledForUser));
        this.mInjector.getLocationManager().setLocationEnabledForUser(z, userHandle);
        if (!z || isLocationEnabledForUser) {
            return;
        }
        showLocationSettingsEnabledNotification(userHandle);
    }

    public final void showLocationSettingsEnabledNotification(UserHandle userHandle) {
        Intent addFlags = new Intent("android.settings.LOCATION_SOURCE_SETTINGS").addFlags(268435456);
        ActivityInfo resolveActivityInfo = addFlags.resolveActivityInfo(this.mInjector.getPackageManager(userHandle.getIdentifier()), 1048576);
        if (resolveActivityInfo != null) {
            addFlags.setComponent(resolveActivityInfo.getComponentName());
        } else {
            Slogf.wtf("DevicePolicyManager", "Failed to resolve intent for location settings");
        }
        final Notification build = new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302478).setContentTitle(getLocationChangedTitle()).setContentText(getLocationChangedText()).setColor(this.mContext.getColor(17170460)).setShowWhen(true).setContentIntent(this.mInjector.pendingIntentGetActivityAsUser(this.mContext, 0, addFlags, 201326592, null, userHandle)).setAutoCancel(true).build();
        this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda180
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$showLocationSettingsEnabledNotification$111(build);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showLocationSettingsEnabledNotification$111(Notification notification) {
        this.mInjector.getNotificationManager().notify(59, notification);
    }

    public final String getLocationChangedTitle() {
        return getUpdatableString("Core.LOCATION_CHANGED_TITLE", 17040582, new Object[0]);
    }

    public final String getLocationChangedText() {
        return getUpdatableString("Core.LOCATION_CHANGED_MESSAGE", 17040581, new Object[0]);
    }

    public boolean setTime(ComponentName componentName, String str, final long j) {
        CallerIdentity callerIdentity;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
            enforcePermission("android.permission.SET_TIME", callerIdentity.getPackageName(), -1);
        } else {
            CallerIdentity callerIdentity2 = getCallerIdentity(componentName);
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity2) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity2));
            callerIdentity = callerIdentity2;
        }
        if (this.mInjector.settingsGlobalGetInt("auto_time", 0) == 1) {
            return false;
        }
        DevicePolicyEventLogger.createEvent(133).setAdmin(callerIdentity.getPackageName()).write();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda170
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setTime$112(j);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTime$112(long j) throws Exception {
        this.mInjector.getAlarmManager().setTime(j);
    }

    public boolean setTimeZone(ComponentName componentName, String str, final String str2) {
        CallerIdentity callerIdentity;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
            enforcePermission("android.permission.SET_TIME_ZONE", callerIdentity.getPackageName(), -1);
        } else {
            CallerIdentity callerIdentity2 = getCallerIdentity(componentName);
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity2) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity2));
            callerIdentity = callerIdentity2;
        }
        if (this.mInjector.settingsGlobalGetInt("auto_time_zone", 0) == 1) {
            return false;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda118
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setTimeZone$113(str2, r3);
            }
        });
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE).setAdmin(callerIdentity.getPackageName()).write();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTimeZone$113(String str, String str2) throws Exception {
        this.mInjector.getAlarmManagerInternal().setTimeZone(str, 100, str2);
    }

    public void setSecureSetting(ComponentName componentName, final String str, final String str2) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        final int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            if (isDeviceOwner(componentName, userId)) {
                if (!SECURE_SETTINGS_DEVICEOWNER_ALLOWLIST.contains(str) && !isCurrentUserDemo()) {
                    throw new SecurityException(String.format("Permission denial: Device owners cannot update %1$s", str));
                }
            } else if (!SECURE_SETTINGS_ALLOWLIST.contains(str) && !isCurrentUserDemo()) {
                throw new SecurityException(String.format("Permission denial: Profile owners cannot update %1$s", str));
            }
            if (str.equals("location_mode") && isSetSecureSettingLocationModeCheckEnabled(componentName.getPackageName(), userId)) {
                throw new UnsupportedOperationException("location_mode is deprecated. Please use setLocationEnabled() instead.");
            }
            if (str.equals("install_non_market_apps")) {
                if (getTargetSdk(componentName.getPackageName(), userId) >= 26) {
                    throw new UnsupportedOperationException("install_non_market_apps is deprecated. Please use one of the user restrictions no_install_unknown_sources or no_install_unknown_sources_globally instead.");
                }
                if (!this.mUserManager.isManagedProfile(userId)) {
                    Slogf.m26e("DevicePolicyManager", "Ignoring setSecureSetting request for " + str + ". User restriction no_install_unknown_sources or no_install_unknown_sources_globally should be used instead.");
                } else {
                    try {
                        setUserRestriction(componentName, componentName.getPackageName(), "no_install_unknown_sources", Integer.parseInt(str2) == 0, false);
                        DevicePolicyEventLogger.createEvent(14).setAdmin(componentName).setStrings(new String[]{str, str2}).write();
                    } catch (NumberFormatException unused) {
                        Slogf.m26e("DevicePolicyManager", "Invalid value: " + str2 + " for setting " + str);
                    }
                }
                return;
            }
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda103
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setSecureSetting$114(str, userId, str2);
                }
            });
            DevicePolicyEventLogger.createEvent(14).setAdmin(componentName).setStrings(new String[]{str, str2}).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSecureSetting$114(String str, int i, String str2) throws Exception {
        if ("default_input_method".equals(str)) {
            if (!TextUtils.equals(this.mInjector.settingsSecureGetStringForUser("default_input_method", i), str2)) {
                this.mSetupContentObserver.addPendingChangeByOwnerLocked(i);
            }
            lambda$getUserDataUnchecked$2(i).mCurrentInputMethodSet = true;
            saveSettingsLocked(i);
        }
        this.mInjector.settingsSecurePutStringForUser(str, str2, i);
        if (!str.equals("location_mode") || Integer.parseInt(str2) == 0) {
            return;
        }
        showLocationSettingsEnabledNotification(UserHandle.of(i));
    }

    public final boolean isSetSecureSettingLocationModeCheckEnabled(String str, int i) {
        return this.mInjector.isChangeEnabled(117835097L, str, i);
    }

    public void setMasterVolumeMuted(ComponentName componentName, boolean z) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(35);
        synchronized (getLockObject()) {
            setUserRestriction(componentName, componentName.getPackageName(), "disallow_unmute_device", z, false);
            DevicePolicyEventLogger.createEvent(35).setAdmin(componentName).setBoolean(z).write();
        }
    }

    public boolean isMasterVolumeMuted(ComponentName componentName) {
        boolean isMasterMute;
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            isMasterMute = ((AudioManager) this.mContext.getSystemService("audio")).isMasterMute();
        }
        return isMasterMute;
    }

    public void setUserIcon(ComponentName componentName, final Bitmap bitmap) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda107
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setUserIcon$115(callerIdentity, bitmap);
                }
            });
        }
        DevicePolicyEventLogger.createEvent(41).setAdmin(componentName).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setUserIcon$115(CallerIdentity callerIdentity, Bitmap bitmap) throws Exception {
        this.mUserManagerInternal.setUserIcon(callerIdentity.getUserId(), bitmap);
    }

    public boolean setKeyguardDisabled(ComponentName componentName, boolean z) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            Preconditions.checkCallAuthorization(isUserAffiliatedWithDeviceLocked(userId), String.format("Admin %s is neither the device owner or affiliated user's profile owner.", componentName));
        }
        if (isManagedProfile(userId)) {
            throw new SecurityException("Managed profile cannot disable keyguard");
        }
        checkCanExecuteOrThrowUnsafe(12);
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        if (z) {
            try {
                if (this.mLockPatternUtils.isSecure(userId)) {
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    return false;
                }
            } catch (RemoteException unused) {
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                throw th;
            }
        }
        this.mLockPatternUtils.setLockScreenDisabled(z, userId);
        if (z) {
            this.mInjector.getIWindowManager().dismissKeyguard((IKeyguardDismissCallback) null, (CharSequence) null);
        }
        DevicePolicyEventLogger.createEvent(37).setAdmin(componentName).setBoolean(z).write();
        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        return true;
    }

    public boolean setStatusBarDisabled(ComponentName componentName, String str, boolean z) {
        CallerIdentity callerIdentity;
        boolean z2;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_STATUS_BAR", callerIdentity.getPackageName(), -1);
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        }
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            Preconditions.checkCallAuthorization(isUserAffiliatedWithDeviceLocked(userId), "Admin " + componentName + " is neither the device owner or affiliated user's profile owner.");
            if (isManagedProfile(userId)) {
                throw new SecurityException("Managed profile cannot disable status bar");
            }
            checkCanExecuteOrThrowUnsafe(13);
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
            if (lambda$getUserDataUnchecked$2.mStatusBarDisabled != z) {
                try {
                } catch (RemoteException unused) {
                    Slogf.m26e("DevicePolicyManager", "Failed to get LockTask mode");
                }
                if (this.mInjector.getIActivityTaskManager().getLockTaskModeState() != 0) {
                    z2 = true;
                    if (z2 && !setStatusBarDisabledInternal(z, userId)) {
                        return false;
                    }
                    lambda$getUserDataUnchecked$2.mStatusBarDisabled = z;
                    saveSettingsLocked(userId);
                }
                z2 = false;
                if (z2) {
                }
                lambda$getUserDataUnchecked$2.mStatusBarDisabled = z;
                saveSettingsLocked(userId);
            }
            DevicePolicyEventLogger.createEvent(38).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
            return true;
        }
    }

    public final boolean setStatusBarDisabledInternal(boolean z, int i) {
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    int i2 = z ? 34013184 : 0;
                    int i3 = z ? 1 : 0;
                    asInterface.disableForUser(i2, this.mToken, this.mContext.getPackageName(), i);
                    asInterface.disable2ForUser(i3, this.mToken, this.mContext.getPackageName(), i);
                    return true;
                }
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "Failed to disable the status bar", e);
            }
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public boolean isStatusBarDisabled(String str) {
        boolean z;
        CallerIdentity callerIdentity = getCallerIdentity(str);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            boolean isUserAffiliatedWithDeviceLocked = isUserAffiliatedWithDeviceLocked(userId);
            Preconditions.checkCallAuthorization(isUserAffiliatedWithDeviceLocked, "Admin " + str + " is neither the device owner or affiliated user's profile owner.");
            if (isManagedProfile(userId)) {
                throw new SecurityException("Managed profile cannot disable status bar");
            }
            z = lambda$getUserDataUnchecked$2(userId).mStatusBarDisabled;
        }
        return z;
    }

    public final Set<String> getPackagesSuspendedByAdmin(int i) {
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            if (deviceOrProfileOwnerAdminLocked != null && deviceOrProfileOwnerAdminLocked.suspendedPackages != null) {
                return new ArraySet(deviceOrProfileOwnerAdminLocked.suspendedPackages);
            }
            return Collections.emptySet();
        }
    }

    public void updateUserSetupCompleteAndPaired() {
        List aliveUsers = this.mUserManager.getAliveUsers();
        int size = aliveUsers.size();
        for (int i = 0; i < size; i++) {
            int i2 = ((UserInfo) aliveUsers.get(i)).id;
            if (this.mInjector.settingsSecureGetIntForUser("user_setup_complete", 0, i2) != 0) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i2);
                if (!lambda$getUserDataUnchecked$2.mUserSetupComplete) {
                    lambda$getUserDataUnchecked$2.mUserSetupComplete = true;
                    if (i2 == 0) {
                        this.mStateCache.setDeviceProvisioned(true);
                    }
                    synchronized (getLockObject()) {
                        saveSettingsLocked(i2);
                    }
                }
            }
            if (this.mIsWatch && this.mInjector.settingsSecureGetIntForUser("device_paired", 0, i2) != 0) {
                DevicePolicyData lambda$getUserDataUnchecked$22 = lambda$getUserDataUnchecked$2(i2);
                if (lambda$getUserDataUnchecked$22.mPaired) {
                    continue;
                } else {
                    lambda$getUserDataUnchecked$22.mPaired = true;
                    synchronized (getLockObject()) {
                        saveSettingsLocked(i2);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class SetupContentObserver extends ContentObserver {
        public final Uri mDefaultImeChanged;
        public final Uri mDeviceProvisioned;
        public final Uri mPaired;
        @GuardedBy({"getLockObject()"})
        public Set<Integer> mUserIdsWithPendingChangesByOwner;
        public final Uri mUserSetupComplete;

        public SetupContentObserver(Handler handler) {
            super(handler);
            this.mUserSetupComplete = Settings.Secure.getUriFor("user_setup_complete");
            this.mDeviceProvisioned = Settings.Global.getUriFor("device_provisioned");
            this.mPaired = Settings.Secure.getUriFor("device_paired");
            this.mDefaultImeChanged = Settings.Secure.getUriFor("default_input_method");
            this.mUserIdsWithPendingChangesByOwner = new ArraySet();
        }

        public void register() {
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mUserSetupComplete, false, this, -1);
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mDeviceProvisioned, false, this, -1);
            DevicePolicyManagerService devicePolicyManagerService = DevicePolicyManagerService.this;
            if (devicePolicyManagerService.mIsWatch) {
                devicePolicyManagerService.mInjector.registerContentObserver(this.mPaired, false, this, -1);
            }
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mDefaultImeChanged, false, this, -1);
        }

        @GuardedBy({"getLockObject()"})
        public final void addPendingChangeByOwnerLocked(int i) {
            this.mUserIdsWithPendingChangesByOwner.add(Integer.valueOf(i));
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.mUserSetupComplete.equals(uri) || (DevicePolicyManagerService.this.mIsWatch && this.mPaired.equals(uri))) {
                DevicePolicyManagerService.this.updateUserSetupCompleteAndPaired();
            } else if (this.mDeviceProvisioned.equals(uri)) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    DevicePolicyManagerService.this.setDeviceOwnershipSystemPropertyLocked();
                }
            } else if (this.mDefaultImeChanged.equals(uri)) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    if (this.mUserIdsWithPendingChangesByOwner.contains(Integer.valueOf(i))) {
                        this.mUserIdsWithPendingChangesByOwner.remove(Integer.valueOf(i));
                    } else {
                        DevicePolicyManagerService.this.lambda$getUserDataUnchecked$2(i).mCurrentInputMethodSet = false;
                        DevicePolicyManagerService.this.saveSettingsLocked(i);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class DevicePolicyConstantsObserver extends ContentObserver {
        public final Uri mConstantsUri;

        public DevicePolicyConstantsObserver(Handler handler) {
            super(handler);
            this.mConstantsUri = Settings.Global.getUriFor("device_policy_constants");
        }

        public void register() {
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mConstantsUri, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            DevicePolicyManagerService devicePolicyManagerService = DevicePolicyManagerService.this;
            devicePolicyManagerService.mConstants = devicePolicyManagerService.loadConstants();
            DevicePolicyManagerService.invalidateBinderCaches();
            DevicePolicyManagerService.this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyConstantsObserver$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    DevicePolicyManagerService.DevicePolicyConstantsObserver.this.lambda$onChange$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChange$0() throws Exception {
            Intent intent = new Intent("android.app.action.DEVICE_POLICY_CONSTANTS_CHANGED");
            intent.setFlags(1073741824);
            List aliveUsers = DevicePolicyManagerService.this.mUserManager.getAliveUsers();
            for (int i = 0; i < aliveUsers.size(); i++) {
                DevicePolicyManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.of(((UserInfo) aliveUsers.get(i)).id));
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class LocalService extends DevicePolicyManagerInternal implements DevicePolicyManagerLiteInternal {
        public List<DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener> mWidgetProviderListeners;

        public LocalService() {
        }

        public List<String> getCrossProfileWidgetProviders(int i) {
            List<String> list;
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                Owners owners = DevicePolicyManagerService.this.mOwners;
                if (owners == null) {
                    return Collections.emptyList();
                }
                ComponentName profileOwnerComponent = owners.getProfileOwnerComponent(i);
                if (profileOwnerComponent == null) {
                    return Collections.emptyList();
                }
                ActiveAdmin activeAdmin = DevicePolicyManagerService.this.getUserDataUnchecked(i).mAdminMap.get(profileOwnerComponent);
                if (activeAdmin != null && (list = activeAdmin.crossProfileWidgetProviders) != null && !list.isEmpty()) {
                    return activeAdmin.crossProfileWidgetProviders;
                }
                return Collections.emptyList();
            }
        }

        public void addOnCrossProfileWidgetProvidersChangeListener(DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener onCrossProfileWidgetProvidersChangeListener) {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                if (this.mWidgetProviderListeners == null) {
                    this.mWidgetProviderListeners = new ArrayList();
                }
                if (!this.mWidgetProviderListeners.contains(onCrossProfileWidgetProvidersChangeListener)) {
                    this.mWidgetProviderListeners.add(onCrossProfileWidgetProvidersChangeListener);
                }
            }
        }

        public ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle userHandle) {
            return DevicePolicyManagerService.this.getProfileOwnerOrDeviceOwnerSupervisionComponent(userHandle);
        }

        public boolean isActiveDeviceOwner(int i) {
            return DevicePolicyManagerService.this.isDefaultDeviceOwner(new CallerIdentity(i, null, null));
        }

        public boolean isActiveProfileOwner(int i) {
            return DevicePolicyManagerService.this.isProfileOwner(new CallerIdentity(i, null, null));
        }

        public boolean isActiveSupervisionApp(int i) {
            if (DevicePolicyManagerService.this.isProfileOwner(new CallerIdentity(i, null, null))) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    ActiveAdmin profileOwnerAdminLocked = DevicePolicyManagerService.this.getProfileOwnerAdminLocked(UserHandle.getUserId(i));
                    if (profileOwnerAdminLocked == null) {
                        return false;
                    }
                    return DevicePolicyManagerService.this.isSupervisionComponentLocked(profileOwnerAdminLocked.info.getComponent());
                }
            }
            return false;
        }

        public final void notifyCrossProfileProvidersChanged(int i, List<String> list) {
            ArrayList arrayList;
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                arrayList = new ArrayList(this.mWidgetProviderListeners);
            }
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                ((DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener) arrayList.get(i2)).onCrossProfileWidgetProvidersChanged(i, list);
            }
        }

        public Intent createShowAdminSupportIntent(int i, boolean z) {
            if (DevicePolicyManagerService.this.getEnforcingAdminAndUserDetailsInternal(i, null) != null || z) {
                return DevicePolicyManagerService.this.createShowAdminSupportIntent(i);
            }
            return null;
        }

        public Intent createUserRestrictionSupportIntent(int i, String str) {
            if (DevicePolicyManagerService.this.getEnforcingAdminAndUserDetailsInternal(i, str) != null) {
                Intent createShowAdminSupportIntent = DevicePolicyManagerService.this.createShowAdminSupportIntent(i);
                createShowAdminSupportIntent.putExtra("android.app.extra.RESTRICTION", str);
                return createShowAdminSupportIntent;
            }
            return null;
        }

        public boolean isUserAffiliatedWithDevice(int i) {
            return DevicePolicyManagerService.this.isUserAffiliatedWithDeviceLocked(i);
        }

        public boolean canSilentlyInstallPackage(String str, int i) {
            if (str == null) {
                return false;
            }
            CallerIdentity callerIdentity = new CallerIdentity(i, null, null);
            return isUserAffiliatedWithDevice(UserHandle.getUserId(i)) && (isActiveProfileOwner(i) || DevicePolicyManagerService.this.isDefaultDeviceOwner(callerIdentity) || DevicePolicyManagerService.this.isFinancedDeviceOwner(callerIdentity));
        }

        public void reportSeparateProfileChallengeChanged(final int i) {
            DevicePolicyManagerService.this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$LocalService$$ExternalSyntheticLambda1
                public final void runOrThrow() {
                    DevicePolicyManagerService.LocalService.this.lambda$reportSeparateProfileChallengeChanged$0(i);
                }
            });
            DevicePolicyEventLogger.createEvent(110).setBoolean(DevicePolicyManagerService.this.isSeparateProfileChallengeEnabled(i)).write();
            DevicePolicyManagerService.invalidateBinderCaches();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$reportSeparateProfileChallengeChanged$0(int i) throws Exception {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                DevicePolicyManagerService.this.updateMaximumTimeToLockLocked(i);
                DevicePolicyManagerService.this.updatePasswordQualityCacheForUserGroup(i);
            }
        }

        public CharSequence getPrintingDisabledReasonForUser(int i) {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                if (!DevicePolicyManagerService.this.mUserManager.hasUserRestriction("no_printing", UserHandle.of(i))) {
                    Slogf.m24e("DevicePolicyManager", "printing is enabled for user %d", Integer.valueOf(i));
                    return null;
                }
                final String profileOwnerPackage = DevicePolicyManagerService.this.mOwners.getProfileOwnerPackage(i);
                if (profileOwnerPackage == null) {
                    profileOwnerPackage = DevicePolicyManagerService.this.mOwners.getDeviceOwnerPackageName();
                }
                final PackageManager packageManager = DevicePolicyManagerService.this.mInjector.getPackageManager();
                PackageInfo packageInfo = (PackageInfo) DevicePolicyManagerService.this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$LocalService$$ExternalSyntheticLambda0
                    public final Object getOrThrow() {
                        PackageInfo lambda$getPrintingDisabledReasonForUser$1;
                        lambda$getPrintingDisabledReasonForUser$1 = DevicePolicyManagerService.LocalService.lambda$getPrintingDisabledReasonForUser$1(packageManager, profileOwnerPackage);
                        return lambda$getPrintingDisabledReasonForUser$1;
                    }
                });
                if (packageInfo == null) {
                    Slogf.m26e("DevicePolicyManager", "packageInfo is inexplicably null");
                    return null;
                }
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if (applicationInfo == null) {
                    Slogf.m26e("DevicePolicyManager", "appInfo is inexplicably null");
                    return null;
                }
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);
                if (applicationLabel == null) {
                    Slogf.m26e("DevicePolicyManager", "appLabel is inexplicably null");
                    return null;
                }
                return DevicePolicyManagerService.this.getUpdatableString("Core.PRINTING_DISABLED_NAMED_ADMIN", 17041369, applicationLabel);
            }
        }

        public static /* synthetic */ PackageInfo lambda$getPrintingDisabledReasonForUser$1(PackageManager packageManager, String str) throws Exception {
            try {
                return packageManager.getPackageInfo(str, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Slogf.m25e("DevicePolicyManager", "getPackageInfo error", e);
                return null;
            }
        }

        public DevicePolicyCache getDevicePolicyCache() {
            return DevicePolicyManagerService.this.mPolicyCache;
        }

        public DeviceStateCache getDeviceStateCache() {
            return DevicePolicyManagerService.this.mStateCache;
        }

        public List<String> getAllCrossProfilePackages() {
            return DevicePolicyManagerService.this.getAllCrossProfilePackages();
        }

        public List<String> getDefaultCrossProfilePackages() {
            return DevicePolicyManagerService.this.getDefaultCrossProfilePackages();
        }

        public void broadcastIntentToManifestReceivers(Intent intent, UserHandle userHandle, boolean z) {
            Objects.requireNonNull(intent);
            Objects.requireNonNull(userHandle);
            Slogf.m20i("DevicePolicyManager", "Sending %s broadcast to manifest receivers.", intent.getAction());
            broadcastIntentToCrossProfileManifestReceivers(intent, userHandle, z);
            broadcastIntentToDevicePolicyManagerRoleHolder(intent, userHandle);
        }

        public void enforcePermission(String str, String str2, int i) {
            DevicePolicyManagerService.this.enforcePermission(str2, str, i);
        }

        public boolean hasPermission(String str, String str2, int i) {
            return DevicePolicyManagerService.this.hasPermission(str2, str, i);
        }

        public final void broadcastIntentToCrossProfileManifestReceivers(Intent intent, UserHandle userHandle, boolean z) {
            int identifier = userHandle.getIdentifier();
            try {
                for (ResolveInfo resolveInfo : DevicePolicyManagerService.this.mIPackageManager.queryIntentReceivers(intent, (String) null, 1024L, identifier).getList()) {
                    String str = resolveInfo.getComponentInfo().packageName;
                    if (checkCrossProfilePackagePermissions(str, identifier, z) || checkModifyQuietModePermission(str, identifier)) {
                        Slogf.m20i("DevicePolicyManager", "Sending %s broadcast to %s.", intent.getAction(), str);
                        DevicePolicyManagerService.this.mContext.sendBroadcastAsUser(new Intent(intent).setComponent(resolveInfo.getComponentInfo().getComponentName()).addFlags(16777216), userHandle);
                    }
                }
            } catch (RemoteException e) {
                Slogf.m12w("DevicePolicyManager", "Cannot get list of broadcast receivers for %s because: %s.", intent.getAction(), e);
            }
        }

        public final void broadcastIntentToDevicePolicyManagerRoleHolder(Intent intent, UserHandle userHandle) {
            int identifier = userHandle.getIdentifier();
            DevicePolicyManagerService devicePolicyManagerService = DevicePolicyManagerService.this;
            String devicePolicyManagementRoleHolderPackageName = devicePolicyManagerService.getDevicePolicyManagementRoleHolderPackageName(devicePolicyManagerService.mContext);
            if (devicePolicyManagementRoleHolderPackageName == null) {
                return;
            }
            try {
                Intent intent2 = new Intent(intent).setPackage(devicePolicyManagementRoleHolderPackageName);
                List<ResolveInfo> list = DevicePolicyManagerService.this.mIPackageManager.queryIntentReceivers(intent2, (String) null, 1024L, identifier).getList();
                if (list.isEmpty()) {
                    return;
                }
                for (ResolveInfo resolveInfo : list) {
                    DevicePolicyManagerService.this.mContext.sendBroadcastAsUser(new Intent(intent2).setComponent(resolveInfo.getComponentInfo().getComponentName()).addFlags(16777216), userHandle);
                }
            } catch (RemoteException e) {
                Slogf.m12w("DevicePolicyManager", "Cannot get list of broadcast receivers for %s because: %s.", intent.getAction(), e);
            }
        }

        public final boolean checkModifyQuietModePermission(String str, int i) {
            try {
                PackageManager packageManager = DevicePolicyManagerService.this.mInjector.getPackageManager();
                Objects.requireNonNull(str);
                ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(str, 0, i);
                Objects.requireNonNull(applicationInfoAsUser);
                ApplicationInfo applicationInfo = applicationInfoAsUser;
                return ActivityManager.checkComponentPermission("android.permission.MODIFY_QUIET_MODE", applicationInfoAsUser.uid, -1, true) == 0;
            } catch (PackageManager.NameNotFoundException unused) {
                Slogf.m12w("DevicePolicyManager", "Cannot find the package %s to check for permissions.", str);
                return false;
            }
        }

        public final boolean checkCrossProfilePackagePermissions(String str, int i, boolean z) {
            AndroidPackage androidPackage = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackage(str);
            if (androidPackage != null && androidPackage.isCrossProfile()) {
                if (!z) {
                    return true;
                }
                if (!isPackageEnabled(str, i)) {
                    return false;
                }
                try {
                    return ((CrossProfileAppsInternal) LocalServices.getService(CrossProfileAppsInternal.class)).verifyPackageHasInteractAcrossProfilePermission(str, i);
                } catch (PackageManager.NameNotFoundException unused) {
                    Slogf.m12w("DevicePolicyManager", "Cannot find the package %s to check for permissions.", str);
                }
            }
            return false;
        }

        public final boolean isPackageEnabled(String str, int i) {
            boolean z;
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PackageInfo packageInfo = DevicePolicyManagerService.this.mInjector.getPackageManagerInternal().getPackageInfo(str, 786432L, callingUid, i);
                if (packageInfo != null) {
                    if (packageInfo.applicationInfo.enabled) {
                        z = true;
                        return z;
                    }
                }
                z = false;
                return z;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public ComponentName getProfileOwnerAsUser(int i) {
            return DevicePolicyManagerService.this.lambda$isProfileOwner$66(i);
        }

        public int getDeviceOwnerUserId() {
            return DevicePolicyManagerService.this.getDeviceOwnerUserId();
        }

        public boolean isDeviceOrProfileOwnerInCallingUser(String str) {
            return isDeviceOwnerInCallingUser(str) || isProfileOwnerInCallingUser(str);
        }

        public final boolean isDeviceOwnerInCallingUser(String str) {
            ComponentName deviceOwnerComponent = DevicePolicyManagerService.this.getDeviceOwnerComponent(true);
            return deviceOwnerComponent != null && str.equals(deviceOwnerComponent.getPackageName());
        }

        public final boolean isProfileOwnerInCallingUser(String str) {
            ComponentName profileOwnerAsUser = getProfileOwnerAsUser(UserHandle.getCallingUserId());
            return profileOwnerAsUser != null && str.equals(profileOwnerAsUser.getPackageName());
        }

        public boolean supportsResetOp(int i) {
            return i == 93 && LocalServices.getService(CrossProfileAppsInternal.class) != null;
        }

        public void resetOp(int i, String str, int i2) {
            if (i != 93) {
                throw new IllegalArgumentException("Unsupported op for DPM reset: " + i);
            }
            ((CrossProfileAppsInternal) LocalServices.getService(CrossProfileAppsInternal.class)).setInteractAcrossProfilesAppOp(str, findInteractAcrossProfilesResetMode(str), i2);
        }

        public Set<String> getPackagesSuspendedByAdmin(int i) {
            return DevicePolicyManagerService.this.getPackagesSuspendedByAdmin(i);
        }

        public void notifyUnsafeOperationStateChanged(DevicePolicySafetyChecker devicePolicySafetyChecker, int i, boolean z) {
            Preconditions.checkArgument(DevicePolicyManagerService.this.mSafetyChecker == devicePolicySafetyChecker, "invalid checker: should be %s, was %s", new Object[]{DevicePolicyManagerService.this.mSafetyChecker, devicePolicySafetyChecker});
            Bundle bundle = new Bundle();
            bundle.putInt("android.app.extra.OPERATION_SAFETY_REASON", i);
            bundle.putBoolean("android.app.extra.OPERATION_SAFETY_STATE", z);
            if (DevicePolicyManagerService.this.mOwners.hasDeviceOwner()) {
                DevicePolicyManagerService.this.sendDeviceOwnerCommand("android.app.action.OPERATION_SAFETY_STATE_CHANGED", bundle);
            }
            for (Integer num : DevicePolicyManagerService.this.mOwners.getProfileOwnerKeys()) {
                DevicePolicyManagerService.this.sendProfileOwnerCommand("android.app.action.OPERATION_SAFETY_STATE_CHANGED", bundle, num.intValue());
            }
        }

        public boolean isKeepProfilesRunningEnabled() {
            return DevicePolicyManagerService.this.mKeepProfilesRunning;
        }

        public final int findInteractAcrossProfilesResetMode(String str) {
            if (getDefaultCrossProfilePackages().contains(str)) {
                return 0;
            }
            return AppOpsManager.opToDefaultMode(93);
        }

        public boolean isUserOrganizationManaged(int i) {
            return getDeviceStateCache().isUserOrganizationManaged(i);
        }

        public boolean isApplicationExemptionsFlagEnabled() {
            return DeviceConfig.getBoolean("device_policy_manager", "application_exemptions", true);
        }

        public List<Bundle> getApplicationRestrictionsPerAdminForUser(final String str, final int i) {
            if (UserHandle.getCallingUserId() != i || !UserHandle.isSameApp(Binder.getCallingUid(), DevicePolicyManagerService.this.getUidForPackage(str, i))) {
                int callingUid = Binder.getCallingUid();
                if (!UserHandle.isSameApp(callingUid, 1000) && callingUid != 0) {
                    throw new SecurityException("Only system may: get application restrictions for other user/app " + str);
                }
            }
            LinkedHashMap localPoliciesSetByAdmins = DevicePolicyManagerService.this.mDevicePolicyEngine.getLocalPoliciesSetByAdmins(PolicyDefinition.APPLICATION_RESTRICTIONS(str), i);
            ArrayList arrayList = new ArrayList();
            for (EnforcingAdmin enforcingAdmin : localPoliciesSetByAdmins.keySet()) {
                arrayList.add((Bundle) ((PolicyValue) localPoliciesSetByAdmins.get(enforcingAdmin)).getValue());
            }
            return !arrayList.isEmpty() ? arrayList : (List) DevicePolicyManagerService.this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$LocalService$$ExternalSyntheticLambda2
                public final Object getOrThrow() {
                    List lambda$getApplicationRestrictionsPerAdminForUser$2;
                    lambda$getApplicationRestrictionsPerAdminForUser$2 = DevicePolicyManagerService.LocalService.this.lambda$getApplicationRestrictionsPerAdminForUser$2(str, i);
                    return lambda$getApplicationRestrictionsPerAdminForUser$2;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ List lambda$getApplicationRestrictionsPerAdminForUser$2(String str, int i) throws Exception {
            Bundle applicationRestrictions = DevicePolicyManagerService.this.mUserManager.getApplicationRestrictions(str, UserHandle.of(i));
            if (applicationRestrictions == null || applicationRestrictions.isEmpty()) {
                return new ArrayList();
            }
            return List.of(applicationRestrictions);
        }
    }

    public final Intent createShowAdminSupportIntent(int i) {
        Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
        intent.putExtra("android.intent.extra.USER_ID", i);
        intent.setFlags(268435456);
        return intent;
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00e6, code lost:
        if (r14 == 1) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Bundle getEnforcingAdminAndUserDetailsInternal(int i, String str) {
        ActiveAdmin deviceOwnerAdminLocked;
        Bundle bundle = null;
        if (str == null || "policy_suspend_packages".equals(str)) {
            ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(i);
            if (profileOwnerComponent != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("android.intent.extra.USER_ID", i);
                bundle2.putParcelable("android.app.extra.DEVICE_ADMIN", profileOwnerComponent);
                return bundle2;
            }
            Pair<Integer, ComponentName> deviceOwnerUserIdAndComponent = this.mOwners.getDeviceOwnerUserIdAndComponent();
            if (deviceOwnerUserIdAndComponent != null && ((Integer) deviceOwnerUserIdAndComponent.first).intValue() == i) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("android.intent.extra.USER_ID", i);
                bundle3.putParcelable("android.app.extra.DEVICE_ADMIN", (Parcelable) deviceOwnerUserIdAndComponent.second);
                return bundle3;
            }
        } else {
            if ("policy_disable_camera".equals(str) || "policy_disable_screen_capture".equals(str)) {
                synchronized (getLockObject()) {
                    DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
                    int size = lambda$getUserDataUnchecked$2.mAdminList.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        ActiveAdmin activeAdmin = lambda$getUserDataUnchecked$2.mAdminList.get(i2);
                        if ((activeAdmin.disableCamera && "policy_disable_camera".equals(str)) || (activeAdmin.disableScreenCapture && "policy_disable_screen_capture".equals(str))) {
                            Bundle bundle4 = new Bundle();
                            bundle4.putInt("android.intent.extra.USER_ID", i);
                            bundle4.putParcelable("android.app.extra.DEVICE_ADMIN", activeAdmin.info.getComponent());
                            return bundle4;
                        }
                    }
                    if ("policy_disable_camera".equals(str) && (deviceOwnerAdminLocked = getDeviceOwnerAdminLocked()) != null && deviceOwnerAdminLocked.disableCamera) {
                        Bundle bundle5 = new Bundle();
                        bundle5.putInt("android.intent.extra.USER_ID", this.mOwners.getDeviceOwnerUserId());
                        bundle5.putParcelable("android.app.extra.DEVICE_ADMIN", deviceOwnerAdminLocked.info.getComponent());
                        return bundle5;
                    }
                }
            } else {
                long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                try {
                    List userRestrictionSources = this.mUserManager.getUserRestrictionSources(str, UserHandle.of(i));
                    if (userRestrictionSources != null) {
                        int size2 = userRestrictionSources.size();
                        if (size2 > 1) {
                            Slogf.m28d("DevicePolicyManager", "getEnforcingAdminAndUserDetailsInternal(%d, %s): %d sources found, excluding those set by UserManager", Integer.valueOf(i), str, Integer.valueOf(size2));
                            userRestrictionSources = getDevicePolicySources(userRestrictionSources);
                        }
                        if (!userRestrictionSources.isEmpty()) {
                            if (userRestrictionSources.size() > 1) {
                                Slogf.m12w("DevicePolicyManager", "getEnforcingAdminAndUserDetailsInternal(%d, %s): multiple sources for restriction %s on user %d", str, Integer.valueOf(i));
                                Bundle bundle6 = new Bundle();
                                bundle6.putInt("android.intent.extra.USER_ID", i);
                                return bundle6;
                            }
                            UserManager.EnforcingUser enforcingUser = userRestrictionSources.get(0);
                            int userRestrictionSource = enforcingUser.getUserRestrictionSource();
                            int identifier = enforcingUser.getUserHandle().getIdentifier();
                            if (userRestrictionSource == 4) {
                                ComponentName profileOwnerComponent2 = this.mOwners.getProfileOwnerComponent(identifier);
                                if (profileOwnerComponent2 != null) {
                                    bundle = new Bundle();
                                    bundle.putInt("android.intent.extra.USER_ID", identifier);
                                    bundle.putParcelable("android.app.extra.DEVICE_ADMIN", profileOwnerComponent2);
                                }
                            } else if (userRestrictionSource == 2) {
                                Pair<Integer, ComponentName> deviceOwnerUserIdAndComponent2 = this.mOwners.getDeviceOwnerUserIdAndComponent();
                                if (deviceOwnerUserIdAndComponent2 != null) {
                                    Bundle bundle7 = new Bundle();
                                    bundle7.putInt("android.intent.extra.USER_ID", ((Integer) deviceOwnerUserIdAndComponent2.first).intValue());
                                    bundle7.putParcelable("android.app.extra.DEVICE_ADMIN", (Parcelable) deviceOwnerUserIdAndComponent2.second);
                                    return bundle7;
                                }
                            }
                        }
                    }
                    return bundle;
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                }
            }
        }
        return null;
    }

    public final List<UserManager.EnforcingUser> getDevicePolicySources(List<UserManager.EnforcingUser> list) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            UserManager.EnforcingUser enforcingUser = list.get(i);
            int userRestrictionSource = enforcingUser.getUserRestrictionSource();
            if (userRestrictionSource != 4 && userRestrictionSource != 2) {
                Slogf.m28d("DevicePolicyManager", "excluding source of type %s at index %d", userRestrictionSourceToString(userRestrictionSource), Integer.valueOf(i));
            } else {
                arrayList.add(enforcingUser);
            }
        }
        return arrayList;
    }

    public static String userRestrictionSourceToString(int i) {
        return DebugUtils.flagsToString(UserManager.class, "RESTRICTION_", i);
    }

    public Bundle getEnforcingAdminAndUserDetails(int i, String str) {
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()));
        return getEnforcingAdminAndUserDetailsInternal(i, str);
    }

    public Intent createAdminSupportIntent(String str) {
        Objects.requireNonNull(str);
        int userId = getCallerIdentity().getUserId();
        if (getEnforcingAdminAndUserDetailsInternal(userId, str) != null) {
            Intent createShowAdminSupportIntent = createShowAdminSupportIntent(userId);
            createShowAdminSupportIntent.putExtra("android.app.extra.RESTRICTION", str);
            return createShowAdminSupportIntent;
        }
        return null;
    }

    public static boolean isLimitPasswordAllowed(ActiveAdmin activeAdmin, int i) {
        if (activeAdmin.mPasswordPolicy.quality < i) {
            return false;
        }
        return activeAdmin.isPermissionBased || activeAdmin.info.usesPolicy(0);
    }

    public void setCredentialManagerPolicy(PackagePolicy packagePolicy) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(canWriteCredentialManagerPolicy(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                if (Objects.equals(profileOwnerOrDeviceOwnerLocked.mCredentialManagerPolicy, packagePolicy)) {
                    return;
                }
                profileOwnerOrDeviceOwnerLocked.mCredentialManagerPolicy = packagePolicy;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    public final boolean canWriteCredentialManagerPolicy(CallerIdentity callerIdentity) {
        return (isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId())) || isDefaultDeviceOwner(callerIdentity) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS");
    }

    public PackagePolicy getCredentialManagerPolicy() {
        PackagePolicy packagePolicy;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(canWriteCredentialManagerPolicy(callerIdentity) || canQueryAdminPolicy(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                packagePolicy = profileOwnerOrDeviceOwnerLocked != null ? profileOwnerOrDeviceOwnerLocked.mCredentialManagerPolicy : null;
            }
            return packagePolicy;
        }
        return null;
    }

    public void setSystemUpdatePolicy(ComponentName componentName, String str, SystemUpdatePolicy systemUpdatePolicy) {
        CallerIdentity callerIdentity;
        boolean z;
        if (systemUpdatePolicy != null) {
            systemUpdatePolicy.validateType();
            systemUpdatePolicy.validateFreezePeriods();
            Pair<LocalDate, LocalDate> systemUpdateFreezePeriodRecord = this.mOwners.getSystemUpdateFreezePeriodRecord();
            systemUpdatePolicy.validateAgainstPreviousFreezePeriod((LocalDate) systemUpdateFreezePeriodRecord.first, (LocalDate) systemUpdateFreezePeriodRecord.second, LocalDate.now());
        }
        synchronized (getLockObject()) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", callerIdentity.getPackageName(), -1);
            } else {
                callerIdentity = getCallerIdentity(componentName);
                if (!isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) && !isDefaultDeviceOwner(callerIdentity)) {
                    z = false;
                    Preconditions.checkCallAuthorization(z);
                }
                z = true;
                Preconditions.checkCallAuthorization(z);
            }
            checkCanExecuteOrThrowUnsafe(14);
            if (systemUpdatePolicy == null) {
                this.mOwners.clearSystemUpdatePolicy();
            } else {
                this.mOwners.setSystemUpdatePolicy(systemUpdatePolicy);
                updateSystemUpdateFreezePeriodsRecord(false);
            }
            this.mOwners.writeDeviceOwner();
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda63
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setSystemUpdatePolicy$116();
            }
        });
        DevicePolicyEventLogger.createEvent(50).setAdmin(callerIdentity.getPackageName()).setInt(systemUpdatePolicy != null ? systemUpdatePolicy.getPolicyType() : 0).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSystemUpdatePolicy$116() throws Exception {
        this.mContext.sendBroadcastAsUser(new Intent("android.app.action.SYSTEM_UPDATE_POLICY_CHANGED"), UserHandle.SYSTEM);
    }

    public SystemUpdatePolicy getSystemUpdatePolicy() {
        synchronized (getLockObject()) {
            SystemUpdatePolicy systemUpdatePolicy = this.mOwners.getSystemUpdatePolicy();
            if (systemUpdatePolicy == null || systemUpdatePolicy.isValid()) {
                return systemUpdatePolicy;
            }
            Slogf.m14w("DevicePolicyManager", "Stored system update policy is invalid, return null instead.");
            return null;
        }
    }

    public static boolean withinRange(Pair<LocalDate, LocalDate> pair, LocalDate localDate) {
        return (localDate.isBefore((ChronoLocalDate) pair.first) || localDate.isAfter((ChronoLocalDate) pair.second)) ? false : true;
    }

    public final void updateSystemUpdateFreezePeriodsRecord(boolean z) {
        boolean systemUpdateFreezePeriodRecord;
        Slogf.m30d("DevicePolicyManager", "updateSystemUpdateFreezePeriodsRecord");
        synchronized (getLockObject()) {
            SystemUpdatePolicy systemUpdatePolicy = this.mOwners.getSystemUpdatePolicy();
            if (systemUpdatePolicy == null) {
                return;
            }
            LocalDate now = LocalDate.now();
            Pair currentFreezePeriod = systemUpdatePolicy.getCurrentFreezePeriod(now);
            if (currentFreezePeriod == null) {
                return;
            }
            Pair<LocalDate, LocalDate> systemUpdateFreezePeriodRecord2 = this.mOwners.getSystemUpdateFreezePeriodRecord();
            LocalDate localDate = (LocalDate) systemUpdateFreezePeriodRecord2.first;
            LocalDate localDate2 = (LocalDate) systemUpdateFreezePeriodRecord2.second;
            if (localDate2 != null && localDate != null) {
                if (now.equals(localDate2.plusDays(1L))) {
                    systemUpdateFreezePeriodRecord = this.mOwners.setSystemUpdateFreezePeriodRecord(localDate, now);
                } else if (now.isAfter(localDate2.plusDays(1L))) {
                    if (withinRange(currentFreezePeriod, localDate) && withinRange(currentFreezePeriod, localDate2)) {
                        systemUpdateFreezePeriodRecord = this.mOwners.setSystemUpdateFreezePeriodRecord(localDate, now);
                    } else {
                        systemUpdateFreezePeriodRecord = this.mOwners.setSystemUpdateFreezePeriodRecord(now, now);
                    }
                } else {
                    systemUpdateFreezePeriodRecord = now.isBefore(localDate) ? this.mOwners.setSystemUpdateFreezePeriodRecord(now, now) : false;
                }
                if (systemUpdateFreezePeriodRecord && z) {
                    this.mOwners.writeDeviceOwner();
                }
            }
            systemUpdateFreezePeriodRecord = this.mOwners.setSystemUpdateFreezePeriodRecord(now, now);
            if (systemUpdateFreezePeriodRecord) {
                this.mOwners.writeDeviceOwner();
            }
        }
    }

    public void clearSystemUpdatePolicyFreezePeriodRecord() {
        Preconditions.checkCallAuthorization(isAdb(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.CLEAR_FREEZE_PERIOD"), "Caller must be shell, or hold CLEAR_FREEZE_PERIOD permission to call clearSystemUpdatePolicyFreezePeriodRecord");
        synchronized (getLockObject()) {
            Slogf.m22i("DevicePolicyManager", "Clear freeze period record: " + this.mOwners.getSystemUpdateFreezePeriodRecordAsString());
            if (this.mOwners.setSystemUpdateFreezePeriodRecord(null, null)) {
                this.mOwners.writeDeviceOwner();
            }
        }
    }

    public final boolean isUidDeviceOwnerLocked(int i) {
        String[] packagesForUid;
        ensureLocked();
        String packageName = this.mOwners.getDeviceOwnerComponent().getPackageName();
        try {
            packagesForUid = this.mInjector.getIPackageManager().getPackagesForUid(i);
        } catch (RemoteException unused) {
        }
        if (packagesForUid == null) {
            return false;
        }
        for (String str : packagesForUid) {
            if (packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public void notifyPendingSystemUpdate(SystemUpdateInfo systemUpdateInfo) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.NOTIFY_PENDING_SYSTEM_UPDATE"), "Only the system update service can broadcast update information");
        if (UserHandle.getCallingUserId() != 0) {
            Slogf.m14w("DevicePolicyManager", "Only the system update service in the system user can broadcast update information.");
        } else if (this.mOwners.saveSystemUpdateInfo(systemUpdateInfo)) {
            final Intent putExtra = new Intent("android.app.action.NOTIFY_PENDING_SYSTEM_UPDATE").putExtra("android.app.extra.SYSTEM_UPDATE_RECEIVED_TIME", systemUpdateInfo == null ? -1L : systemUpdateInfo.getReceivedTime());
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda22
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$notifyPendingSystemUpdate$117(putExtra);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyPendingSystemUpdate$117(Intent intent) throws Exception {
        int[] runningUserIds;
        synchronized (getLockObject()) {
            if (this.mOwners.hasDeviceOwner()) {
                UserHandle of = UserHandle.of(this.mOwners.getDeviceOwnerUserId());
                intent.setComponent(this.mOwners.getDeviceOwnerComponent());
                this.mContext.sendBroadcastAsUser(intent, of);
            }
        }
        try {
            for (int i : this.mInjector.getIActivityManager().getRunningUserIds()) {
                synchronized (getLockObject()) {
                    ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(i);
                    if (profileOwnerComponent != null) {
                        intent.setComponent(profileOwnerComponent);
                        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(i));
                    }
                }
            }
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "Could not retrieve the list of running users", e);
        }
    }

    public SystemUpdateInfo getPendingSystemUpdate(ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return this.mOwners.getSystemUpdateInfo();
    }

    public void setPermissionPolicy(ComponentName componentName, String str, int i) {
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-permission-grant")));
        checkCanExecuteOrThrowUnsafe(38);
        int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
            if (lambda$getUserDataUnchecked$2.mPermissionPolicy != i) {
                lambda$getUserDataUnchecked$2.mPermissionPolicy = i;
                this.mPolicyCache.setPermissionPolicy(userId, i);
                saveSettingsLocked(userId);
            }
        }
        DevicePolicyEventLogger.createEvent(18).setAdmin(callerIdentity.getPackageName()).setInt(i).setBoolean(componentName == null).write();
    }

    public final void updatePermissionPolicyCache(int i) {
        synchronized (getLockObject()) {
            this.mPolicyCache.setPermissionPolicy(i, lambda$getUserDataUnchecked$2(i).mPermissionPolicy);
        }
    }

    public int getPermissionPolicy(ComponentName componentName) throws RemoteException {
        return this.mPolicyCache.getPermissionPolicy(UserHandle.getCallingUserId());
    }

    public void setPermissionGrantState(ComponentName componentName, String str, String str2, final String str3, final int i, final RemoteCallback remoteCallback) throws RemoteException {
        Injector injector;
        boolean z;
        Objects.requireNonNull(remoteCallback);
        final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        checkCanExecuteOrThrowUnsafe(37);
        synchronized (getLockObject()) {
            if (isFinancedDeviceOwner(callerIdentity)) {
                enforcePermissionGrantStateOnFinancedDevice(str2, str3);
            }
        }
        if (useDevicePolicyEngine(callerIdentity, "delegation-permission-grant")) {
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.PERMISSION_GRANT(str2, str3), enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", str, callerIdentity.getUserId()), new IntegerPolicyValue(i), callerIdentity.getUserId());
            remoteCallback.sendResult(Bundle.EMPTY);
            return;
        }
        Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-permission-grant")));
        synchronized (getLockObject()) {
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                z = getTargetSdk(callerIdentity.getPackageName(), callerIdentity.getUserId()) >= 29;
            } catch (SecurityException e) {
                Slogf.m25e("DevicePolicyManager", "Could not set permission grant state", e);
                remoteCallback.sendResult((Bundle) null);
                injector = this.mInjector;
            }
            if (!z && getTargetSdk(str2, callerIdentity.getUserId()) < 23) {
                remoteCallback.sendResult((Bundle) null);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            } else if (!isRuntimePermission(str3)) {
                remoteCallback.sendResult((Bundle) null);
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            } else {
                if (i == 1 || i == 2 || i == 0) {
                    final boolean z2 = z;
                    this.mInjector.getPermissionControllerManager(callerIdentity.getUserHandle()).setRuntimePermissionGrantStateByDeviceAdmin(callerIdentity.getPackageName(), new AdminPermissionControlParams(str2, str3, i, canAdminGrantSensorsPermissions()), this.mContext.getMainExecutor(), new Consumer() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda41
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            DevicePolicyManagerService.this.lambda$setPermissionGrantState$118(z2, remoteCallback, callerIdentity, str3, i, (Boolean) obj);
                        }
                    });
                }
                injector = this.mInjector;
                injector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setPermissionGrantState$118(boolean z, RemoteCallback remoteCallback, CallerIdentity callerIdentity, String str, int i, Boolean bool) {
        if (z && !bool.booleanValue()) {
            remoteCallback.sendResult((Bundle) null);
            return;
        }
        DevicePolicyEventLogger.createEvent(19).setAdmin(callerIdentity.getPackageName()).setStrings(new String[]{str}).setInt(i).setBoolean(isCallerDelegate(callerIdentity)).write();
        remoteCallback.sendResult(Bundle.EMPTY);
    }

    public final void enforcePermissionGrantStateOnFinancedDevice(String str, String str2) {
        if (!"android.permission.READ_PHONE_STATE".equals(str2)) {
            throw new SecurityException(str2 + " cannot be used when managing a financed device for permission grant state");
        } else if (!this.mOwners.getDeviceOwnerPackageName().equals(str)) {
            throw new SecurityException("Device owner package is the only package that can be used for permission grant state when managing a financed device");
        }
    }

    public int getPermissionGrantState(ComponentName componentName, String str, final String str2, final String str3) throws RemoteException {
        int intValue;
        final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        if (isPermissionCheckFlagEnabled()) {
            enforceCanQuery("android.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS", callerIdentity.getPackageName(), callerIdentity.getUserId());
        } else {
            Preconditions.checkCallAuthorization(isSystemUid(callerIdentity) || (callerIdentity.hasAdminComponent() && (isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-permission-grant")));
        }
        synchronized (getLockObject()) {
            if (isFinancedDeviceOwner(callerIdentity)) {
                enforcePermissionGrantStateOnFinancedDevice(str2, str3);
            }
            intValue = ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda84
                public final Object getOrThrow() {
                    Integer lambda$getPermissionGrantState$119;
                    lambda$getPermissionGrantState$119 = DevicePolicyManagerService.this.lambda$getPermissionGrantState$119(str2, str3, callerIdentity);
                    return lambda$getPermissionGrantState$119;
                }
            })).intValue();
        }
        return intValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getPermissionGrantState$119(String str, String str2, CallerIdentity callerIdentity) throws Exception {
        return Integer.valueOf(getPermissionGrantStateForUser(str, str2, callerIdentity, callerIdentity.getUserId()));
    }

    public final int getPermissionGrantStateForUser(String str, String str2, CallerIdentity callerIdentity, int i) throws RemoteException {
        int i2;
        if (getTargetSdk(callerIdentity.getPackageName(), callerIdentity.getUserId()) < 29) {
            i2 = this.mIPackageManager.checkPermission(str2, str, i);
        } else {
            try {
                i2 = PermissionChecker.checkPermissionForPreflight(this.mContext, str2, -1, this.mInjector.getPackageManager().getPackageUidAsUser(str, i), str) != 0 ? -1 : 0;
            } catch (PackageManager.NameNotFoundException unused) {
                return 0;
            }
        }
        if ((this.mInjector.getPackageManager().getPermissionFlags(str2, str, UserHandle.of(i)) & 4) != 4) {
            return 0;
        }
        return i2 == 0 ? 1 : 2;
    }

    public boolean isPackageInstalledForUser(final String str, final int i) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda82
            public final Object getOrThrow() {
                Boolean lambda$isPackageInstalledForUser$120;
                lambda$isPackageInstalledForUser$120 = DevicePolicyManagerService.this.lambda$isPackageInstalledForUser$120(str, i);
                return lambda$isPackageInstalledForUser$120;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isPackageInstalledForUser$120(String str, int i) throws Exception {
        try {
            PackageInfo packageInfo = this.mInjector.getIPackageManager().getPackageInfo(str, 0L, i);
            return Boolean.valueOf((packageInfo == null || packageInfo.applicationInfo.flags == 0) ? false : true);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    public final boolean isRuntimePermission(String str) {
        try {
            return (this.mInjector.getPackageManager().getPermissionInfo(str, 0).protectionLevel & 15) == 1;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public boolean isProvisioningAllowed(String str, String str2) {
        Objects.requireNonNull(str2);
        CallerIdentity callerIdentity = getCallerIdentity();
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            Preconditions.checkArgument(Arrays.asList(this.mInjector.getPackageManager().getPackagesForUid(callerIdentity.getUid())).contains(str2), "Caller uid doesn't match the one for the provided package.");
            return checkProvisioningPreconditionSkipPermission(str, str2, callerIdentity.getUserId()) == 0;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public int checkProvisioningPrecondition(String str, String str2) {
        Objects.requireNonNull(str2, "packageName is null");
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            return checkProvisioningPreconditionSkipPermission(str, str2, callerIdentity.getUserId());
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final int checkProvisioningPreconditionSkipPermission(String str, String str2, int i) {
        if (!this.mHasFeature) {
            logMissingFeatureAction("Cannot check provisioning for action " + str);
            return 13;
        } else if (isProvisioningAllowed()) {
            int checkProvisioningPreConditionSkipPermissionNoLog = checkProvisioningPreConditionSkipPermissionNoLog(str, str2, i);
            if (checkProvisioningPreConditionSkipPermissionNoLog != 0) {
                Slogf.m30d("DevicePolicyManager", "checkProvisioningPreCondition(" + str + ", " + str2 + ") failed: " + computeProvisioningErrorString(checkProvisioningPreConditionSkipPermissionNoLog, this.mInjector.userHandleGetCallingUserId()));
            }
            return checkProvisioningPreConditionSkipPermissionNoLog;
        } else {
            return 15;
        }
    }

    public final boolean isProvisioningAllowed() {
        return isDeveloperMode(this.mContext) || SystemProperties.getBoolean("ro.config.allowuserprovisioning", true);
    }

    public static boolean isDeveloperMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "adb_enabled", 0) > 0;
    }

    public final int checkProvisioningPreConditionSkipPermissionNoLog(String str, String str2, int i) {
        if (str != null) {
            char c = 65535;
            switch (str.hashCode()) {
                case -920528692:
                    if (str.equals("android.app.action.PROVISION_MANAGED_DEVICE")) {
                        c = 0;
                        break;
                    }
                    break;
                case -340845101:
                    if (str.equals("android.app.action.PROVISION_MANAGED_PROFILE")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1340354933:
                    if (str.equals("android.app.action.PROVISION_FINANCED_DEVICE")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 2:
                    return checkDeviceOwnerProvisioningPreCondition(i);
                case 1:
                    return checkManagedProfileProvisioningPreCondition(str2, i);
            }
        }
        throw new IllegalArgumentException("Unknown provisioning action " + str);
    }

    public final int checkDeviceOwnerProvisioningPreConditionLocked(ComponentName componentName, int i, int i2, boolean z, boolean z2) {
        if (this.mOwners.hasDeviceOwner()) {
            return 1;
        }
        if (this.mOwners.hasProfileOwner(i)) {
            return 2;
        }
        if (this.mUserManager.isUserRunning(new UserHandle(i))) {
            if (this.mIsWatch && hasPaired(0)) {
                return 8;
            }
            if (this.mInjector.userManagerIsHeadlessSystemUserMode()) {
                if (i != 0) {
                    Slogf.m26e("DevicePolicyManager", "In headless system user mode, device owner can only be set on headless system user.");
                    return 7;
                } else if (componentName != null && findAdmin(componentName, i, false).getHeadlessDeviceOwnerMode() != 1) {
                    return 16;
                }
            }
            if (!z) {
                if (i != 0) {
                    return 7;
                }
                return hasUserSetupCompleted(0) ? 4 : 0;
            }
            if (this.mIsWatch || hasUserSetupCompleted(0)) {
                if (nonTestNonPrecreatedUsersExist()) {
                    return 5;
                }
                int currentForegroundUserId = getCurrentForegroundUserId();
                if (i2 != currentForegroundUserId && this.mInjector.userManagerIsHeadlessSystemUserMode() && currentForegroundUserId == 0) {
                    Slogf.wtf("DevicePolicyManager", "In headless system user mode, current user cannot be system user when setting device owner");
                    return 10;
                } else if (z2) {
                    return 6;
                }
            }
            return 0;
        }
        return 3;
    }

    public final boolean nonTestNonPrecreatedUsersExist() {
        return this.mUserManagerInternal.getUsers(true).stream().filter(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda40
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$nonTestNonPrecreatedUsersExist$121;
                lambda$nonTestNonPrecreatedUsersExist$121 = DevicePolicyManagerService.lambda$nonTestNonPrecreatedUsersExist$121((UserInfo) obj);
                return lambda$nonTestNonPrecreatedUsersExist$121;
            }
        }).count() > ((long) (UserManager.isHeadlessSystemUserMode() ? 2 : 1));
    }

    public static /* synthetic */ boolean lambda$nonTestNonPrecreatedUsersExist$121(UserInfo userInfo) {
        return !userInfo.isForTesting();
    }

    public final int checkDeviceOwnerProvisioningPreCondition(int i) {
        int checkDeviceOwnerProvisioningPreConditionLocked;
        synchronized (getLockObject()) {
            int i2 = this.mInjector.userManagerIsHeadlessSystemUserMode() ? 0 : i;
            Slogf.m20i("DevicePolicyManager", "Calling user %d, device owner will be set on user %d", Integer.valueOf(i), Integer.valueOf(i2));
            checkDeviceOwnerProvisioningPreConditionLocked = checkDeviceOwnerProvisioningPreConditionLocked(null, i2, i, false, true);
        }
        return checkDeviceOwnerProvisioningPreConditionLocked;
    }

    public final int checkManagedProfileProvisioningPreCondition(String str, int i) {
        boolean z;
        if (hasFeatureManagedUsers()) {
            if (lambda$isProfileOwner$66(i) != null) {
                return 2;
            }
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                UserHandle of = UserHandle.of(i);
                synchronized (getLockObject()) {
                    z = getDeviceOwnerAdminLocked() != null;
                }
                boolean hasUserRestriction = this.mUserManager.hasUserRestriction("no_add_managed_profile", of);
                if (this.mUserManager.getUserInfo(i).isProfile()) {
                    Slogf.m20i("DevicePolicyManager", "Calling user %d is a profile, cannot add another.", Integer.valueOf(i));
                } else {
                    if (z && !hasUserRestriction) {
                        Slogf.wtf("DevicePolicyManager", "Has a device owner but no restriction on adding a profile.");
                    }
                    if (hasUserRestriction) {
                        Slogf.m20i("DevicePolicyManager", "Adding a profile is restricted: User %s Has device owner? %b", of, Boolean.valueOf(z));
                    } else if (this.mUserManager.canAddMoreManagedProfiles(i, false)) {
                        return 0;
                    } else {
                        Slogf.m22i("DevicePolicyManager", "Cannot add more managed profiles.");
                    }
                }
                return 11;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
        return 9;
    }

    public final void checkIsDeviceOwner(CallerIdentity callerIdentity) {
        boolean isDefaultDeviceOwner = isDefaultDeviceOwner(callerIdentity);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner, callerIdentity.getUid() + " is not device owner");
    }

    public final ComponentName getOwnerComponent(int i) {
        synchronized (getLockObject()) {
            if (this.mOwners.getDeviceOwnerUserId() == i) {
                return this.mOwners.getDeviceOwnerComponent();
            } else if (this.mOwners.hasProfileOwner(i)) {
                return this.mOwners.getProfileOwnerComponent(i);
            } else {
                return null;
            }
        }
    }

    public final boolean hasFeatureManagedUsers() {
        try {
            return this.mIPackageManager.hasSystemFeature("android.software.managed_users", 0);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public String getWifiMacAddress(ComponentName componentName, String str) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        final CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        return (String) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda26
            public final Object getOrThrow() {
                String lambda$getWifiMacAddress$122;
                lambda$getWifiMacAddress$122 = DevicePolicyManagerService.this.lambda$getWifiMacAddress$122(callerIdentity);
                return lambda$getWifiMacAddress$122;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWifiMacAddress$122(CallerIdentity callerIdentity) throws Exception {
        String[] factoryMacAddresses = this.mInjector.getWifiManager().getFactoryMacAddresses();
        if (factoryMacAddresses == null) {
            return null;
        }
        DevicePolicyEventLogger.createEvent(54).setAdmin(callerIdentity.getPackageName()).write();
        if (factoryMacAddresses.length > 0) {
            return factoryMacAddresses[0];
        }
        return null;
    }

    public final int getTargetSdk(String str, int i) {
        try {
            ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0L, i);
            if (applicationInfo == null) {
                return 0;
            }
            return applicationInfo.targetSdkVersion;
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error getting application info", e);
            return 0;
        }
    }

    public boolean isManagedProfile(ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        return isManagedProfile(callerIdentity.getUserId());
    }

    public void reboot(final ComponentName componentName) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
        checkCanExecuteOrThrowUnsafe(7);
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda95
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$reboot$123(componentName);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reboot$123(ComponentName componentName) throws Exception {
        if (this.mTelephonyManager.getCallState() != 0) {
            throw new IllegalStateException("Cannot be called with ongoing call on the device");
        }
        DevicePolicyEventLogger.createEvent(34).setAdmin(componentName).write();
        this.mInjector.powerManagerReboot("deviceowner");
    }

    public void setShortSupportMessage(ComponentName componentName, String str, CharSequence charSequence) {
        CallerIdentity callerIdentity;
        ActiveAdmin activeAdminForUidLocked;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
                activeAdminForUidLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                callerIdentity = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
                synchronized (getLockObject()) {
                    activeAdminForUidLocked = getActiveAdminForUidLocked(componentName, callerIdentity.getUid());
                }
            }
            synchronized (getLockObject()) {
                if (!TextUtils.equals(activeAdminForUidLocked.shortSupportMessage, charSequence)) {
                    activeAdminForUidLocked.shortSupportMessage = charSequence;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(43).setAdmin(callerIdentity.getPackageName()).write();
        }
    }

    public CharSequence getShortSupportMessage(ComponentName componentName, String str) {
        ActiveAdmin activeAdminForUidLocked;
        if (this.mHasFeature) {
            if (isPermissionCheckFlagEnabled()) {
                CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
                activeAdminForUidLocked = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                CallerIdentity callerIdentity2 = getCallerIdentity(componentName);
                Objects.requireNonNull(componentName, "ComponentName is null");
                synchronized (getLockObject()) {
                    activeAdminForUidLocked = getActiveAdminForUidLocked(componentName, callerIdentity2.getUid());
                }
            }
            return activeAdminForUidLocked.shortSupportMessage;
        }
        return null;
    }

    public void setLongSupportMessage(ComponentName componentName, CharSequence charSequence) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminForUidLocked = getActiveAdminForUidLocked(componentName, callerIdentity.getUid());
                if (!TextUtils.equals(activeAdminForUidLocked.longSupportMessage, charSequence)) {
                    activeAdminForUidLocked.longSupportMessage = charSequence;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(44).setAdmin(componentName).write();
        }
    }

    public CharSequence getLongSupportMessage(ComponentName componentName) {
        CharSequence charSequence;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            synchronized (getLockObject()) {
                charSequence = getActiveAdminForUidLocked(componentName, callerIdentity.getUid()).longSupportMessage;
            }
            return charSequence;
        }
        return null;
    }

    public CharSequence getShortSupportMessageForUser(ComponentName componentName, int i) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query support message for user"));
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                if (activeAdminUncheckedLocked != null) {
                    return activeAdminUncheckedLocked.shortSupportMessage;
                }
                return null;
            }
        }
        return null;
    }

    public CharSequence getLongSupportMessageForUser(ComponentName componentName, int i) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query support message for user"));
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                if (activeAdminUncheckedLocked != null) {
                    return activeAdminUncheckedLocked.longSupportMessage;
                }
                return null;
            }
        }
        return null;
    }

    public void setOrganizationColor(ComponentName componentName, int i) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallingUser(isManagedProfile(callerIdentity.getUserId()));
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                getProfileOwnerLocked(callerIdentity.getUserId()).organizationColor = i;
                saveSettingsLocked(callerIdentity.getUserId());
            }
            DevicePolicyEventLogger.createEvent(39).setAdmin(callerIdentity.getComponentName()).write();
        }
    }

    public void setOrganizationColorForUser(int i, int i2) {
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i2, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i2));
            Preconditions.checkCallAuthorization(canManageUsers(callerIdentity));
            Preconditions.checkCallAuthorization(isManagedProfile(i2), "You can not set organization color outside a managed profile, userId = %d", new Object[]{Integer.valueOf(i2)});
            synchronized (getLockObject()) {
                getProfileOwnerAdminLocked(i2).organizationColor = i;
                saveSettingsLocked(i2);
            }
        }
    }

    public int getOrganizationColor(ComponentName componentName) {
        int i;
        if (!this.mHasFeature) {
            return ActiveAdmin.DEF_ORGANIZATION_COLOR;
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallingUser(isManagedProfile(callerIdentity.getUserId()));
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
        synchronized (getLockObject()) {
            i = getProfileOwnerLocked(callerIdentity.getUserId()).organizationColor;
        }
        return i;
    }

    public int getOrganizationColorForUser(int i) {
        int i2;
        if (!this.mHasFeature) {
            return ActiveAdmin.DEF_ORGANIZATION_COLOR;
        }
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(getCallerIdentity(), i));
        Preconditions.checkCallAuthorization(isManagedProfile(i), "You can not get organization color outside a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked != null) {
                i2 = profileOwnerAdminLocked.organizationColor;
            } else {
                i2 = ActiveAdmin.DEF_ORGANIZATION_COLOR;
            }
        }
        return i2;
    }

    public void setOrganizationName(ComponentName componentName, String str, CharSequence charSequence) {
        ActiveAdmin activeAdmin;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            String str2 = null;
            if (isPermissionCheckFlagEnabled()) {
                activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                activeAdmin = null;
            }
            synchronized (getLockObject()) {
                if (!isPermissionCheckFlagEnabled()) {
                    activeAdmin = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                }
                if (!TextUtils.equals(activeAdmin.organizationName, charSequence)) {
                    if (charSequence != null && charSequence.length() != 0) {
                        str2 = charSequence.toString();
                    }
                    activeAdmin.organizationName = str2;
                    saveSettingsLocked(callerIdentity.getUserId());
                }
            }
        }
    }

    public CharSequence getOrganizationName(ComponentName componentName, String str) {
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            if (isPermissionCheckFlagEnabled()) {
                profileOwnerOrDeviceOwnerLocked = enforceCanQueryAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                Preconditions.checkCallingUser(isManagedProfile(callerIdentity.getUserId()));
                Preconditions.checkCallAuthorization(isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
                synchronized (getLockObject()) {
                    profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                }
            }
            return profileOwnerOrDeviceOwnerLocked.organizationName;
        }
        return null;
    }

    public CharSequence getDeviceOwnerOrganizationName() {
        String str = null;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || canManageUsers(callerIdentity) || isFinancedDeviceOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked != null) {
                    str = deviceOwnerAdminLocked.organizationName;
                }
            }
            return str;
        }
        return null;
    }

    public CharSequence getOrganizationNameForUser(int i) {
        String str;
        if (this.mHasFeature) {
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            CallerIdentity callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(hasFullCrossUsersPermission(callerIdentity, i));
            Preconditions.checkCallAuthorization(canManageUsers(callerIdentity));
            Preconditions.checkCallAuthorization(isManagedProfile(i), "You can not get organization name outside a managed profile, userId = %d", new Object[]{Integer.valueOf(i)});
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
                str = profileOwnerAdminLocked != null ? profileOwnerAdminLocked.organizationName : null;
            }
            return str;
        }
        return null;
    }

    public List<String> setMeteredDataDisabledPackages(ComponentName componentName, final List<String> list) {
        List<String> list2;
        Objects.requireNonNull(componentName);
        Objects.requireNonNull(list);
        final CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity), "Admin %s does not own the profile", new Object[]{callerIdentity.getComponentName()});
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                final ActiveAdmin profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
                list2 = (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda92
                    public final Object getOrThrow() {
                        List lambda$setMeteredDataDisabledPackages$124;
                        lambda$setMeteredDataDisabledPackages$124 = DevicePolicyManagerService.this.lambda$setMeteredDataDisabledPackages$124(callerIdentity, list, profileOwnerOrDeviceOwnerLocked);
                        return lambda$setMeteredDataDisabledPackages$124;
                    }
                });
            }
            return list2;
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$setMeteredDataDisabledPackages$124(CallerIdentity callerIdentity, List list, ActiveAdmin activeAdmin) throws Exception {
        List<String> removeInvalidPkgsForMeteredDataRestriction = removeInvalidPkgsForMeteredDataRestriction(callerIdentity.getUserId(), list);
        activeAdmin.meteredDisabledPackages = list;
        pushMeteredDisabledPackages(callerIdentity.getUserId());
        saveSettingsLocked(callerIdentity.getUserId());
        return removeInvalidPkgsForMeteredDataRestriction;
    }

    public final List<String> removeInvalidPkgsForMeteredDataRestriction(int i, List<String> list) {
        Set<String> activeAdminPackagesLocked = getActiveAdminPackagesLocked(i);
        ArrayList arrayList = new ArrayList();
        for (int size = list.size() - 1; size >= 0; size--) {
            String str = list.get(size);
            if (activeAdminPackagesLocked.contains(str)) {
                arrayList.add(str);
            } else {
                try {
                    if (!this.mInjector.getIPackageManager().isPackageAvailable(str, i)) {
                        arrayList.add(str);
                    }
                } catch (RemoteException unused) {
                }
            }
        }
        list.removeAll(arrayList);
        return arrayList;
    }

    public List<String> getMeteredDataDisabledPackages(ComponentName componentName) {
        List<String> list;
        Objects.requireNonNull(componentName);
        if (!this.mHasFeature) {
            return new ArrayList();
        }
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity), "Admin %s does not own the profile", new Object[]{callerIdentity.getComponentName()});
        synchronized (getLockObject()) {
            list = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).meteredDisabledPackages;
            if (list == null) {
                list = new ArrayList<>();
            }
        }
        return list;
    }

    public boolean isMeteredDataDisabledPackageForUser(ComponentName componentName, String str, int i) {
        List<String> list;
        Objects.requireNonNull(componentName);
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "query restricted pkgs for a specific user"));
            synchronized (getLockObject()) {
                ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
                if (activeAdminUncheckedLocked == null || (list = activeAdminUncheckedLocked.meteredDisabledPackages) == null) {
                    return false;
                }
                return list.contains(str);
            }
        }
        return false;
    }

    public void setProfileOwnerOnOrganizationOwnedDevice(ComponentName componentName, int i, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName);
            CallerIdentity callerIdentity = getCallerIdentity();
            if (!isAdb(callerIdentity) && !hasCallingPermission("android.permission.MARK_DEVICE_ORGANIZATION_OWNED") && !hasCallingPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS")) {
                throw new SecurityException("Only the system can mark a profile owner of organization-owned device.");
            }
            synchronized (getLockObject()) {
                if (!z) {
                    if (!isAdminTestOnlyLocked(componentName, i)) {
                        throw new SecurityException("Only a test admin can be unmarked as a profile owner of organization-owned device.");
                    }
                }
            }
            if (isAdb(callerIdentity)) {
                if (hasIncompatibleAccountsOrNonAdbNoLock(callerIdentity, i, componentName)) {
                    throw new SecurityException("Can only be called from ADB if the device has no accounts.");
                }
            } else if (hasUserSetupCompleted(0)) {
                throw new IllegalStateException("Cannot mark profile owner as managing an organization-owned device after set-up");
            }
            synchronized (getLockObject()) {
                setProfileOwnerOnOrganizationOwnedDeviceUncheckedLocked(componentName, i, z);
            }
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void setProfileOwnerOnOrganizationOwnedDeviceUncheckedLocked(ComponentName componentName, final int i, final boolean z) {
        if (!isProfileOwner(componentName, i)) {
            throw new IllegalArgumentException(String.format("Component %s is not a Profile Owner of user %d", componentName.flattenToString(), Integer.valueOf(i)));
        }
        Object[] objArr = new Object[3];
        objArr[0] = z ? "Marking" : "Unmarking";
        objArr[1] = componentName.flattenToString();
        objArr[2] = Integer.valueOf(i);
        Slogf.m20i("DevicePolicyManager", "%s %s as profile owner on organization-owned device for user %d", objArr);
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda121
            public final void runOrThrow() {
                DevicePolicyManagerService.this.m56xe8fb51ee(i, z);
            }
        });
        this.mOwners.setProfileOwnerOfOrganizationOwnedDevice(i, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$setProfileOwnerOnOrganizationOwnedDeviceUncheckedLocked$125 */
    public /* synthetic */ void m56xe8fb51ee(int i, boolean z) throws Exception {
        UserHandle profileParent = this.mUserManager.getProfileParent(UserHandle.of(i));
        if (profileParent == null) {
            throw new IllegalStateException(String.format("User %d is not a profile", Integer.valueOf(i)));
        }
        this.mUserManager.setUserRestriction("no_remove_managed_profile", z, profileParent);
        this.mUserManager.setUserRestriction("no_add_user", z, profileParent);
    }

    public final void pushMeteredDisabledPackages(int i) {
        wtfIfInLock();
        this.mInjector.getNetworkPolicyManagerInternal().setMeteredRestrictedPackages(getMeteredDisabledPackages(i), i);
    }

    public final Set<String> getMeteredDisabledPackages(int i) {
        ArraySet arraySet;
        List<String> list;
        synchronized (getLockObject()) {
            arraySet = new ArraySet();
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            if (deviceOrProfileOwnerAdminLocked != null && (list = deviceOrProfileOwnerAdminLocked.meteredDisabledPackages) != null) {
                arraySet.addAll(list);
            }
        }
        return arraySet;
    }

    public void setAffiliationIds(ComponentName componentName, List<String> list) {
        if (this.mHasFeature) {
            if (list == null) {
                throw new IllegalArgumentException("ids must not be null");
            }
            for (String str : list) {
                if (TextUtils.isEmpty(str)) {
                    throw new IllegalArgumentException("ids must not contain empty string");
                }
            }
            ArraySet arraySet = new ArraySet(list);
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            int userId = callerIdentity.getUserId();
            synchronized (getLockObject()) {
                lambda$getUserDataUnchecked$2(userId).mAffiliationIds = arraySet;
                saveSettingsLocked(userId);
                if (userId != 0 && isDeviceOwner(componentName, userId)) {
                    lambda$getUserDataUnchecked$2(0).mAffiliationIds = arraySet;
                    saveSettingsLocked(0);
                }
                maybePauseDeviceWideLoggingLocked();
                maybeResumeDeviceWideLoggingLocked();
                maybeClearLockTaskPolicyLocked();
                updateAdminCanGrantSensorsPermissionCache(userId);
            }
        }
    }

    public List<String> getAffiliationIds(ComponentName componentName) {
        ArrayList arrayList;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(componentName);
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            arrayList = new ArrayList(lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mAffiliationIds);
        }
        return arrayList;
    }

    public boolean isCallingUserAffiliated() {
        boolean isUserAffiliatedWithDeviceLocked;
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                isUserAffiliatedWithDeviceLocked = isUserAffiliatedWithDeviceLocked(this.mInjector.userHandleGetCallingUserId());
            }
            return isUserAffiliatedWithDeviceLocked;
        }
        return false;
    }

    public boolean isAffiliatedUser(int i) {
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization(hasCrossUsersPermission(getCallerIdentity(), i));
            return isUserAffiliatedWithDevice(i);
        }
        return false;
    }

    public final boolean isUserAffiliatedWithDevice(int i) {
        boolean isUserAffiliatedWithDeviceLocked;
        synchronized (getLockObject()) {
            isUserAffiliatedWithDeviceLocked = isUserAffiliatedWithDeviceLocked(i);
        }
        return isUserAffiliatedWithDeviceLocked;
    }

    public final boolean isUserAffiliatedWithDeviceLocked(int i) {
        if (this.mOwners.hasDeviceOwner()) {
            if (i == 0 || i == this.mOwners.getDeviceOwnerUserId()) {
                return true;
            }
            if (lambda$isProfileOwner$66(i) == null) {
                return false;
            }
            Set<String> set = lambda$getUserDataUnchecked$2(i).mAffiliationIds;
            Set<String> set2 = lambda$getUserDataUnchecked$2(0).mAffiliationIds;
            for (String str : set) {
                if (set2.contains(str)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public final boolean areAllUsersAffiliatedWithDeviceLocked() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda2
            public final Object getOrThrow() {
                Boolean lambda$areAllUsersAffiliatedWithDeviceLocked$126;
                lambda$areAllUsersAffiliatedWithDeviceLocked$126 = DevicePolicyManagerService.this.lambda$areAllUsersAffiliatedWithDeviceLocked$126();
                return lambda$areAllUsersAffiliatedWithDeviceLocked$126;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$areAllUsersAffiliatedWithDeviceLocked$126() throws Exception {
        List aliveUsers = this.mUserManager.getAliveUsers();
        for (int i = 0; i < aliveUsers.size(); i++) {
            int i2 = ((UserInfo) aliveUsers.get(i)).id;
            if (!isUserAffiliatedWithDeviceLocked(i2)) {
                Slogf.m30d("DevicePolicyManager", "User id " + i2 + " not affiliated.");
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public final int getSecurityLoggingEnabledUser() {
        synchronized (getLockObject()) {
            if (this.mOwners.hasDeviceOwner()) {
                return -1;
            }
            return getOrganizationOwnedProfileUserId();
        }
    }

    public void setSecurityLoggingEnabled(ComponentName componentName, String str, boolean z) {
        boolean z2;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            synchronized (getLockObject()) {
                if (isPermissionCheckFlagEnabled()) {
                    enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", callerIdentity.getPackageName(), -1);
                } else if (componentName != null) {
                    if (!isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) && !isDefaultDeviceOwner(callerIdentity)) {
                        z2 = false;
                        Preconditions.checkCallAuthorization(z2);
                    }
                    z2 = true;
                    Preconditions.checkCallAuthorization(z2);
                } else {
                    Preconditions.checkCallAuthorization(isCallerDelegate(callerIdentity, "delegation-security-logging"));
                }
                if (z == this.mInjector.securityLogGetLoggingEnabledProperty()) {
                    return;
                }
                this.mInjector.securityLogSetLoggingEnabledProperty(z);
                if (z) {
                    this.mSecurityLogMonitor.start(getSecurityLoggingEnabledUser());
                    maybePauseDeviceWideLoggingLocked();
                } else {
                    this.mSecurityLogMonitor.stop();
                }
                DevicePolicyEventLogger.createEvent(15).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
            }
        }
    }

    public boolean isSecurityLoggingEnabled(ComponentName componentName, String str) {
        boolean securityLogGetLoggingEnabledProperty;
        boolean z = false;
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                if (!isSystemUid(getCallerIdentity())) {
                    CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
                    if (isPermissionCheckFlagEnabled()) {
                        enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", callerIdentity.getPackageName(), -1);
                    } else if (componentName != null) {
                        Preconditions.checkCallAuthorization((isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity)) ? true : true);
                    } else {
                        Preconditions.checkCallAuthorization(isCallerDelegate(callerIdentity, "delegation-security-logging"));
                    }
                }
                securityLogGetLoggingEnabledProperty = this.mInjector.securityLogGetLoggingEnabledProperty();
            }
            return securityLogGetLoggingEnabledProperty;
        }
        return false;
    }

    public final void recordSecurityLogRetrievalTime() {
        synchronized (getLockObject()) {
            long currentTimeMillis = System.currentTimeMillis();
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
            if (currentTimeMillis > lambda$getUserDataUnchecked$2.mLastSecurityLogRetrievalTime) {
                lambda$getUserDataUnchecked$2.mLastSecurityLogRetrievalTime = currentTimeMillis;
                saveSettingsLocked(0);
            }
        }
    }

    public ParceledListSlice<SecurityLog.SecurityEvent> retrievePreRebootSecurityLogs(ComponentName componentName, String str) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            if (isPermissionCheckFlagEnabled()) {
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", callerIdentity.getPackageName(), -1);
            } else {
                boolean z = false;
                if (componentName != null) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
                } else {
                    Preconditions.checkCallAuthorization(isCallerDelegate(callerIdentity, "delegation-security-logging"));
                }
                if (isOrganizationOwnedDeviceWithManagedProfile() || areAllUsersAffiliatedWithDeviceLocked()) {
                    z = true;
                }
                Preconditions.checkCallAuthorization(z);
            }
            DevicePolicyEventLogger.createEvent(17).setAdmin(callerIdentity.getPackageName()).write();
            if (this.mContext.getResources().getBoolean(17891821) && this.mInjector.securityLogGetLoggingEnabledProperty()) {
                recordSecurityLogRetrievalTime();
                ArrayList arrayList = new ArrayList();
                try {
                    SecurityLog.readPreviousEvents(arrayList);
                    int securityLoggingEnabledUser = getSecurityLoggingEnabledUser();
                    if (securityLoggingEnabledUser != -1) {
                        SecurityLog.redactEvents(arrayList, securityLoggingEnabledUser);
                    }
                    return new ParceledListSlice<>(arrayList);
                } catch (IOException e) {
                    Slogf.m13w("DevicePolicyManager", "Fail to read previous events", e);
                    return new ParceledListSlice<>(Collections.emptyList());
                }
            }
            return null;
        }
        return null;
    }

    public ParceledListSlice<SecurityLog.SecurityEvent> retrieveSecurityLogs(ComponentName componentName, String str) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            if (isPermissionCheckFlagEnabled()) {
                enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING", callerIdentity.getPackageName(), -1);
            } else {
                boolean z = false;
                if (componentName != null) {
                    Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
                } else {
                    Preconditions.checkCallAuthorization(isCallerDelegate(callerIdentity, "delegation-security-logging"));
                }
                if (isOrganizationOwnedDeviceWithManagedProfile() || areAllUsersAffiliatedWithDeviceLocked()) {
                    z = true;
                }
                Preconditions.checkCallAuthorization(z);
            }
            if (this.mInjector.securityLogGetLoggingEnabledProperty()) {
                recordSecurityLogRetrievalTime();
                List<SecurityLog.SecurityEvent> retrieveLogs = this.mSecurityLogMonitor.retrieveLogs();
                DevicePolicyEventLogger.createEvent(16).setAdmin(callerIdentity.getPackageName()).write();
                if (retrieveLogs != null) {
                    return new ParceledListSlice<>(retrieveLogs);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public long forceSecurityLogs() {
        Preconditions.checkCallAuthorization(isAdb(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.FORCE_DEVICE_POLICY_MANAGER_LOGS"), "Caller must be shell or hold FORCE_DEVICE_POLICY_MANAGER_LOGS to call forceSecurityLogs");
        if (!this.mInjector.securityLogGetLoggingEnabledProperty()) {
            throw new IllegalStateException("logging is not available");
        }
        return this.mSecurityLogMonitor.forceLogs();
    }

    public boolean isUninstallInQueue(String str) {
        boolean contains;
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS"));
        UserPackage of = UserPackage.of(callerIdentity.getUserId(), str);
        synchronized (getLockObject()) {
            contains = this.mPackagesToRemove.contains(of);
        }
        return contains;
    }

    public void uninstallPackageWithActiveAdmins(final String str) {
        Preconditions.checkArgument(!TextUtils.isEmpty(str));
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS"));
        final int userId = callerIdentity.getUserId();
        enforceUserUnlocked(userId);
        ComponentName lambda$isProfileOwner$66 = lambda$isProfileOwner$66(userId);
        if (lambda$isProfileOwner$66 != null && str.equals(lambda$isProfileOwner$66.getPackageName())) {
            throw new IllegalArgumentException("Cannot uninstall a package with a profile owner");
        }
        ComponentName deviceOwnerComponent = getDeviceOwnerComponent(false);
        if (getDeviceOwnerUserId() == userId && deviceOwnerComponent != null && str.equals(deviceOwnerComponent.getPackageName())) {
            throw new IllegalArgumentException("Cannot uninstall a package with a device owner");
        }
        UserPackage of = UserPackage.of(userId, str);
        synchronized (getLockObject()) {
            this.mPackagesToRemove.add(of);
        }
        List<ComponentName> activeAdmins = getActiveAdmins(userId);
        final ArrayList arrayList = new ArrayList();
        if (activeAdmins != null) {
            for (ComponentName componentName : activeAdmins) {
                if (str.equals(componentName.getPackageName())) {
                    arrayList.add(componentName);
                    removeActiveAdmin(componentName, userId);
                }
            }
        }
        if (arrayList.size() == 0) {
            startUninstallIntent(str, userId);
        } else {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.7
                @Override // java.lang.Runnable
                public void run() {
                    for (ComponentName componentName2 : arrayList) {
                        DevicePolicyManagerService.this.removeAdminArtifacts(componentName2, userId);
                    }
                    DevicePolicyManagerService.this.startUninstallIntent(str, userId);
                }
            }, 10000L);
        }
    }

    public boolean isDeviceProvisioned() {
        boolean z;
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
        synchronized (getLockObject()) {
            z = getUserDataUnchecked(0).mUserSetupComplete;
        }
        return z;
    }

    public final boolean isCurrentUserDemo() {
        if (UserManager.isDeviceInDemoMode(this.mContext)) {
            final int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda28
                public final Object getOrThrow() {
                    Boolean lambda$isCurrentUserDemo$127;
                    lambda$isCurrentUserDemo$127 = DevicePolicyManagerService.this.lambda$isCurrentUserDemo$127(userHandleGetCallingUserId);
                    return lambda$isCurrentUserDemo$127;
                }
            })).booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isCurrentUserDemo$127(int i) throws Exception {
        return Boolean.valueOf(this.mUserManager.getUserInfo(i).isDemo());
    }

    public final void removePackageIfRequired(String str, int i) {
        if (packageHasActiveAdmins(str, i)) {
            return;
        }
        startUninstallIntent(str, i);
    }

    public final void startUninstallIntent(String str, int i) {
        UserPackage of = UserPackage.of(i, str);
        synchronized (getLockObject()) {
            if (this.mPackagesToRemove.contains(of)) {
                this.mPackagesToRemove.remove(of);
                if (isPackageInstalledForUser(str, i)) {
                    try {
                        this.mInjector.getIActivityManager().forceStopPackage(str, i);
                    } catch (RemoteException unused) {
                        Slogf.m26e("DevicePolicyManager", "Failure talking to ActivityManager while force stopping package");
                    }
                    Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + str));
                    intent.setFlags(268435456);
                    this.mContext.startActivityAsUser(intent, UserHandle.of(i));
                }
            }
        }
    }

    public final void removeAdminArtifacts(ComponentName componentName, int i) {
        synchronized (getLockObject()) {
            ActiveAdmin activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(componentName, i);
            if (activeAdminUncheckedLocked == null) {
                return;
            }
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            boolean usesPolicy = activeAdminUncheckedLocked.info.usesPolicy(5);
            lambda$getUserDataUnchecked$2.mAdminList.remove(activeAdminUncheckedLocked);
            lambda$getUserDataUnchecked$2.mAdminMap.remove(componentName);
            lambda$getUserDataUnchecked$2.validatePasswordOwner();
            if (usesPolicy) {
                lambda$setGlobalProxy$59(lambda$getUserDataUnchecked$2);
            }
            pushActiveAdminPackagesLocked(i);
            saveSettingsLocked(i);
            updateMaximumTimeToLockLocked(i);
            lambda$getUserDataUnchecked$2.mRemovingAdmins.remove(componentName);
            pushScreenCapturePolicy(i);
            Slogf.m22i("DevicePolicyManager", "Device admin " + componentName + " removed from user " + i);
            pushMeteredDisabledPackages(i);
            pushUserRestrictions(i);
            if (shouldMigrateToDevicePolicyEngine()) {
                migratePoliciesToDevicePolicyEngine();
            }
        }
    }

    public void setDeviceProvisioningConfigApplied() {
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
        synchronized (getLockObject()) {
            lambda$getUserDataUnchecked$2(0).mDeviceProvisioningConfigApplied = true;
            saveSettingsLocked(0);
        }
    }

    public boolean isDeviceProvisioningConfigApplied() {
        boolean z;
        Preconditions.checkCallAuthorization(canManageUsers(getCallerIdentity()));
        synchronized (getLockObject()) {
            z = lambda$getUserDataUnchecked$2(0).mDeviceProvisioningConfigApplied;
        }
        return z;
    }

    public void forceUpdateUserSetupComplete(int i) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        boolean z = this.mInjector.settingsSecureGetIntForUser("user_setup_complete", 0, i) != 0;
        lambda$getUserDataUnchecked$2(i).mUserSetupComplete = z;
        this.mStateCache.setDeviceProvisioned(z);
        synchronized (getLockObject()) {
            saveSettingsLocked(i);
        }
    }

    public void setBackupServiceEnabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity));
            toggleBackupServiceActive(callerIdentity.getUserId(), z);
        }
    }

    public boolean isBackupServiceEnabled(ComponentName componentName) {
        boolean z = true;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            final CallerIdentity callerIdentity = getCallerIdentity(componentName);
            if (!isDefaultDeviceOwner(callerIdentity) && !isProfileOwner(callerIdentity) && !isFinancedDeviceOwner(callerIdentity)) {
                z = false;
            }
            Preconditions.checkCallAuthorization(z);
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda89
                public final Object getOrThrow() {
                    Boolean lambda$isBackupServiceEnabled$128;
                    lambda$isBackupServiceEnabled$128 = DevicePolicyManagerService.this.lambda$isBackupServiceEnabled$128(callerIdentity);
                    return lambda$isBackupServiceEnabled$128;
                }
            })).booleanValue();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isBackupServiceEnabled$128(CallerIdentity callerIdentity) throws Exception {
        Boolean valueOf;
        synchronized (getLockObject()) {
            try {
                try {
                    IBackupManager iBackupManager = this.mInjector.getIBackupManager();
                    valueOf = Boolean.valueOf(iBackupManager != null && iBackupManager.isBackupServiceActive(callerIdentity.getUserId()));
                } catch (RemoteException e) {
                    throw new IllegalStateException("Failed requesting backup service state.", e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return valueOf;
    }

    public boolean bindDeviceAdminServiceAsUser(ComponentName componentName, IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, IServiceConnection iServiceConnection, long j, int i) {
        String ownerPackageNameForUserLocked;
        boolean z = false;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName);
            Objects.requireNonNull(iApplicationThread);
            Objects.requireNonNull(intent);
            Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null) ? false : true, "Service intent must be explicit (with a package name or component): " + intent);
            Objects.requireNonNull(iServiceConnection);
            Preconditions.checkArgument(this.mInjector.userHandleGetCallingUserId() != i, "target user id must be different from the calling user id");
            if (!getBindDeviceAdminTargetUsers(componentName).contains(UserHandle.of(i))) {
                throw new SecurityException("Not allowed to bind to target user id");
            }
            synchronized (getLockObject()) {
                ownerPackageNameForUserLocked = getOwnerPackageNameForUserLocked(i);
            }
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                if (createCrossUserServiceIntent(intent, ownerPackageNameForUserLocked, i) != null) {
                    if (this.mInjector.getIActivityManager().bindService(iApplicationThread, iBinder, intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), iServiceConnection, j, this.mContext.getOpPackageName(), i) != 0) {
                        z = true;
                    }
                }
                return z;
            } catch (RemoteException unused) {
                return false;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
        return false;
    }

    public List<UserHandle> getBindDeviceAdminTargetUsers(final ComponentName componentName) {
        List<UserHandle> list;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(componentName);
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            final int userId = callerIdentity.getUserId();
            list = (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda166
                public final Object getOrThrow() {
                    ArrayList lambda$getBindDeviceAdminTargetUsers$129;
                    lambda$getBindDeviceAdminTargetUsers$129 = DevicePolicyManagerService.this.lambda$getBindDeviceAdminTargetUsers$129(componentName, userId);
                    return lambda$getBindDeviceAdminTargetUsers$129;
                }
            });
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ArrayList lambda$getBindDeviceAdminTargetUsers$129(ComponentName componentName, int i) throws Exception {
        ArrayList arrayList = new ArrayList();
        if (!isDeviceOwner(componentName, i)) {
            if (canUserBindToDeviceOwnerLocked(i)) {
                arrayList.add(UserHandle.of(this.mOwners.getDeviceOwnerUserId()));
            }
        } else {
            List aliveUsers = this.mUserManager.getAliveUsers();
            for (int i2 = 0; i2 < aliveUsers.size(); i2++) {
                int i3 = ((UserInfo) aliveUsers.get(i2)).id;
                if (i3 != i && canUserBindToDeviceOwnerLocked(i3)) {
                    arrayList.add(UserHandle.of(i3));
                }
            }
        }
        return arrayList;
    }

    public final boolean canUserBindToDeviceOwnerLocked(int i) {
        if (this.mOwners.hasDeviceOwner() && i != this.mOwners.getDeviceOwnerUserId() && this.mOwners.hasProfileOwner(i) && TextUtils.equals(this.mOwners.getDeviceOwnerPackageName(), this.mOwners.getProfileOwnerPackage(i))) {
            return isUserAffiliatedWithDeviceLocked(i);
        }
        return false;
    }

    public final boolean hasIncompatibleAccountsOrNonAdbNoLock(CallerIdentity callerIdentity, final int i, final ComponentName componentName) {
        if (isAdb(callerIdentity)) {
            wtfIfInLock();
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda47
                public final Object getOrThrow() {
                    Boolean lambda$hasIncompatibleAccountsOrNonAdbNoLock$130;
                    lambda$hasIncompatibleAccountsOrNonAdbNoLock$130 = DevicePolicyManagerService.this.lambda$hasIncompatibleAccountsOrNonAdbNoLock$130(i, componentName);
                    return lambda$hasIncompatibleAccountsOrNonAdbNoLock$130;
                }
            })).booleanValue();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hasIncompatibleAccountsOrNonAdbNoLock$130(int i, ComponentName componentName) throws Exception {
        AccountManager accountManager = (AccountManager) this.mContext.createContextAsUser(UserHandle.of(i), 0).getSystemService(AccountManager.class);
        Account[] accounts = accountManager.getAccounts();
        if (accounts.length == 0) {
            return Boolean.FALSE;
        }
        synchronized (getLockObject()) {
            if (componentName != null) {
                if (isAdminTestOnlyLocked(componentName, i)) {
                    boolean z = !hasIncompatibleAccounts(accountManager, accounts);
                    if (z) {
                        Slogf.m14w("DevicePolicyManager", "All accounts are compatible");
                    } else {
                        Slogf.m26e("DevicePolicyManager", "Found incompatible accounts");
                    }
                    return Boolean.valueOf(!z);
                }
            }
            Slogf.m14w("DevicePolicyManager", "Non test-only owner can't be installed with existing accounts.");
            return Boolean.TRUE;
        }
    }

    public final boolean hasIncompatibleAccounts(AccountManager accountManager, Account[] accountArr) {
        String[] strArr = {"android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED"};
        String[] strArr2 = {"android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED"};
        for (Account account : accountArr) {
            if (hasAccountFeatures(accountManager, account, strArr2)) {
                Slogf.m24e("DevicePolicyManager", "%s has %s", account, strArr2[0]);
                return true;
            } else if (!hasAccountFeatures(accountManager, account, strArr)) {
                Slogf.m24e("DevicePolicyManager", "%s doesn't have %s", account, strArr[0]);
                return true;
            }
        }
        return false;
    }

    public final boolean isAdb(CallerIdentity callerIdentity) {
        return isShellUid(callerIdentity) || isRootUid(callerIdentity);
    }

    public void setNetworkLoggingEnabled(ComponentName componentName, String str, boolean z) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            boolean z2 = isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId());
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isDefaultDeviceOwner(callerIdentity) || z2)) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-network-logging")));
            synchronized (getLockObject()) {
                if (z == isNetworkLoggingEnabledInternalLocked()) {
                    return;
                }
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId());
                deviceOrProfileOwnerAdminLocked.isNetworkLoggingEnabled = z;
                if (!z) {
                    deviceOrProfileOwnerAdminLocked.numNetworkLoggingNotifications = 0;
                    deviceOrProfileOwnerAdminLocked.lastNetworkLoggingNotificationTimeMs = 0L;
                }
                saveSettingsLocked(callerIdentity.getUserId());
                setNetworkLoggingActiveInternal(z);
                DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(119).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null).setInt(z ? 1 : 0);
                String[] strArr = new String[1];
                strArr[0] = z2 ? "profile-owner" : "device-owner";
                devicePolicyEventLogger.setStrings(strArr).write();
            }
        }
    }

    public final void setNetworkLoggingActiveInternal(final boolean z) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda1
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setNetworkLoggingActiveInternal$133(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setNetworkLoggingActiveInternal$133(boolean z) throws Exception {
        boolean z2;
        synchronized (getLockObject()) {
            if (z) {
                if (this.mNetworkLogger == null) {
                    int networkLoggingAffectedUser = getNetworkLoggingAffectedUser();
                    PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
                    if (networkLoggingAffectedUser == 0) {
                        networkLoggingAffectedUser = -1;
                    }
                    this.mNetworkLogger = new NetworkLogger(this, packageManagerInternal, networkLoggingAffectedUser);
                }
                if (!this.mNetworkLogger.startNetworkLogging()) {
                    this.mNetworkLogger = null;
                    Slogf.wtf("DevicePolicyManager", "Network logging could not be started due to the logging service not being available yet.");
                }
                maybePauseDeviceWideLoggingLocked();
                z2 = shouldSendNetworkLoggingNotificationLocked();
            } else {
                NetworkLogger networkLogger = this.mNetworkLogger;
                if (networkLogger != null && !networkLogger.stopNetworkLogging()) {
                    Slogf.wtf("DevicePolicyManager", "Network logging could not be stopped due to the logging service not being available yet.");
                }
                this.mNetworkLogger = null;
                z2 = false;
            }
        }
        if (!z) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda196
                @Override // java.lang.Runnable
                public final void run() {
                    DevicePolicyManagerService.this.lambda$setNetworkLoggingActiveInternal$132();
                }
            });
        } else if (z2) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda195
                @Override // java.lang.Runnable
                public final void run() {
                    DevicePolicyManagerService.this.lambda$setNetworkLoggingActiveInternal$131();
                }
            });
        }
    }

    public final int getNetworkLoggingAffectedUser() {
        synchronized (getLockObject()) {
            if (this.mOwners.hasDeviceOwner()) {
                return this.mOwners.getDeviceOwnerUserId();
            }
            return ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda139
                public final Object getOrThrow() {
                    Integer lambda$getNetworkLoggingAffectedUser$134;
                    lambda$getNetworkLoggingAffectedUser$134 = DevicePolicyManagerService.this.lambda$getNetworkLoggingAffectedUser$134();
                    return lambda$getNetworkLoggingAffectedUser$134;
                }
            })).intValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getNetworkLoggingAffectedUser$134() throws Exception {
        return Integer.valueOf(getManagedUserId());
    }

    public final ActiveAdmin getNetworkLoggingControllingAdminLocked() {
        int networkLoggingAffectedUser = getNetworkLoggingAffectedUser();
        if (networkLoggingAffectedUser < 0) {
            return null;
        }
        return getDeviceOrProfileOwnerAdminLocked(networkLoggingAffectedUser);
    }

    public long forceNetworkLogs() {
        Preconditions.checkCallAuthorization(isAdb(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.FORCE_DEVICE_POLICY_MANAGER_LOGS"), "Caller must be shell or hold FORCE_DEVICE_POLICY_MANAGER_LOGS to call forceNetworkLogs");
        synchronized (getLockObject()) {
            if (!isNetworkLoggingEnabledInternalLocked()) {
                throw new IllegalStateException("logging is not available");
            }
            if (this.mNetworkLogger != null) {
                return ((Long) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda159
                    public final Object getOrThrow() {
                        Long lambda$forceNetworkLogs$135;
                        lambda$forceNetworkLogs$135 = DevicePolicyManagerService.this.lambda$forceNetworkLogs$135();
                        return lambda$forceNetworkLogs$135;
                    }
                })).longValue();
            }
            return 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Long lambda$forceNetworkLogs$135() throws Exception {
        return Long.valueOf(this.mNetworkLogger.forceBatchFinalization());
    }

    @GuardedBy({"getLockObject()"})
    public final void maybePauseDeviceWideLoggingLocked() {
        if (areAllUsersAffiliatedWithDeviceLocked()) {
            return;
        }
        if (this.mOwners.hasDeviceOwner()) {
            Slogf.m22i("DevicePolicyManager", "There are unaffiliated users, network logging will be paused if enabled.");
            NetworkLogger networkLogger = this.mNetworkLogger;
            if (networkLogger != null) {
                networkLogger.pause();
            }
        }
        if (isOrganizationOwnedDeviceWithManagedProfile()) {
            return;
        }
        Slogf.m22i("DevicePolicyManager", "Not org-owned managed profile device, security logging will be paused if enabled.");
        this.mSecurityLogMonitor.pause();
    }

    @GuardedBy({"getLockObject()"})
    public final void maybeResumeDeviceWideLoggingLocked() {
        final boolean areAllUsersAffiliatedWithDeviceLocked = areAllUsersAffiliatedWithDeviceLocked();
        final boolean isOrganizationOwnedDeviceWithManagedProfile = isOrganizationOwnedDeviceWithManagedProfile();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda116
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$maybeResumeDeviceWideLoggingLocked$136(areAllUsersAffiliatedWithDeviceLocked, isOrganizationOwnedDeviceWithManagedProfile);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeResumeDeviceWideLoggingLocked$136(boolean z, boolean z2) throws Exception {
        NetworkLogger networkLogger;
        if (z || z2) {
            this.mSecurityLogMonitor.resume();
        }
        if ((z || !this.mOwners.hasDeviceOwner()) && (networkLogger = this.mNetworkLogger) != null) {
            networkLogger.resume();
        }
    }

    @GuardedBy({"getLockObject()"})
    public final void discardDeviceWideLogsLocked() {
        this.mSecurityLogMonitor.discardLogs();
        NetworkLogger networkLogger = this.mNetworkLogger;
        if (networkLogger != null) {
            networkLogger.discardLogs();
        }
    }

    public boolean isNetworkLoggingEnabled(ComponentName componentName, String str) {
        boolean isNetworkLoggingEnabledInternalLocked;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isDefaultDeviceOwner(callerIdentity) || (isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId())))) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-network-logging")) || hasCallingOrSelfPermission("android.permission.MANAGE_USERS"));
            synchronized (getLockObject()) {
                isNetworkLoggingEnabledInternalLocked = isNetworkLoggingEnabledInternalLocked();
            }
            return isNetworkLoggingEnabledInternalLocked;
        }
        return false;
    }

    public final boolean isNetworkLoggingEnabledInternalLocked() {
        ActiveAdmin networkLoggingControllingAdminLocked = getNetworkLoggingControllingAdminLocked();
        return networkLoggingControllingAdminLocked != null && networkLoggingControllingAdminLocked.isNetworkLoggingEnabled;
    }

    public List<NetworkEvent> retrieveNetworkLogs(ComponentName componentName, String str, long j) {
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
            boolean z = isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId());
            Preconditions.checkCallAuthorization((callerIdentity.hasAdminComponent() && (isDefaultDeviceOwner(callerIdentity) || z)) || (callerIdentity.hasPackage() && isCallerDelegate(callerIdentity, "delegation-network-logging")));
            if (this.mOwners.hasDeviceOwner()) {
                checkAllUsersAreAffiliatedWithDevice();
            }
            synchronized (getLockObject()) {
                if (this.mNetworkLogger != null && isNetworkLoggingEnabledInternalLocked()) {
                    DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(120).setAdmin(callerIdentity.getPackageName()).setBoolean(componentName == null);
                    String[] strArr = new String[1];
                    strArr[0] = z ? "profile-owner" : "device-owner";
                    devicePolicyEventLogger.setStrings(strArr).write();
                    long currentTimeMillis = System.currentTimeMillis();
                    DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(callerIdentity.getUserId());
                    if (currentTimeMillis > lambda$getUserDataUnchecked$2.mLastNetworkLogsRetrievalTime) {
                        lambda$getUserDataUnchecked$2.mLastNetworkLogsRetrievalTime = currentTimeMillis;
                        saveSettingsLocked(callerIdentity.getUserId());
                    }
                    return this.mNetworkLogger.retrieveLogs(j);
                }
                return null;
            }
        }
        return null;
    }

    public final boolean shouldSendNetworkLoggingNotificationLocked() {
        ensureLocked();
        ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
        if (deviceOwnerAdminLocked == null || !deviceOwnerAdminLocked.isNetworkLoggingEnabled || deviceOwnerAdminLocked.numNetworkLoggingNotifications >= 2) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - deviceOwnerAdminLocked.lastNetworkLoggingNotificationTimeMs < MS_PER_DAY) {
            return false;
        }
        int i = deviceOwnerAdminLocked.numNetworkLoggingNotifications + 1;
        deviceOwnerAdminLocked.numNetworkLoggingNotifications = i;
        if (i >= 2) {
            deviceOwnerAdminLocked.lastNetworkLoggingNotificationTimeMs = 0L;
        } else {
            deviceOwnerAdminLocked.lastNetworkLoggingNotificationTimeMs = currentTimeMillis;
        }
        saveSettingsLocked(deviceOwnerAdminLocked.getUserHandle().getIdentifier());
        return true;
    }

    /* renamed from: handleSendNetworkLoggingNotification */
    public final void lambda$setNetworkLoggingActiveInternal$131() {
        PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
        Intent intent = new Intent("android.app.action.SHOW_DEVICE_MONITORING_DIALOG");
        intent.setPackage(packageManagerInternal.getSystemUiServiceComponent().getPackageName());
        this.mNetworkLoggingNotificationUserId = getCurrentForegroundUserId();
        PendingIntent broadcastAsUser = PendingIntent.getBroadcastAsUser(this.mContext, 0, intent, 67108864, UserHandle.CURRENT);
        String networkLoggingTitle = getNetworkLoggingTitle();
        String networkLoggingText = getNetworkLoggingText();
        Notification build = new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302478).setContentTitle(networkLoggingTitle).setContentText(networkLoggingText).setTicker(networkLoggingTitle).setShowWhen(true).setContentIntent(broadcastAsUser).setStyle(new Notification.BigTextStyle().bigText(networkLoggingText)).build();
        Slogf.m20i("DevicePolicyManager", "Sending network logging notification to user %d", Integer.valueOf(this.mNetworkLoggingNotificationUserId));
        this.mInjector.getNotificationManager().notifyAsUser(null, 1002, build, UserHandle.of(this.mNetworkLoggingNotificationUserId));
    }

    public final String getNetworkLoggingTitle() {
        return getUpdatableString("Core.NETWORK_LOGGING_TITLE", 17040833, new Object[0]);
    }

    public final String getNetworkLoggingText() {
        return getUpdatableString("Core.NETWORK_LOGGING_MESSAGE", 17040832, new Object[0]);
    }

    /* renamed from: handleCancelNetworkLoggingNotification */
    public final void lambda$setNetworkLoggingActiveInternal$132() {
        int i = this.mNetworkLoggingNotificationUserId;
        if (i == -10000) {
            Slogf.m30d("DevicePolicyManager", "Not cancelling network logging notification for USER_NULL");
            return;
        }
        Slogf.m20i("DevicePolicyManager", "Cancelling network logging notification for user %d", Integer.valueOf(i));
        this.mInjector.getNotificationManager().cancelAsUser(null, 1002, UserHandle.of(this.mNetworkLoggingNotificationUserId));
        this.mNetworkLoggingNotificationUserId = -10000;
    }

    public final String getOwnerPackageNameForUserLocked(int i) {
        if (this.mOwners.getDeviceOwnerUserId() == i) {
            return this.mOwners.getDeviceOwnerPackageName();
        }
        return this.mOwners.getProfileOwnerPackage(i);
    }

    public final Intent createCrossUserServiceIntent(Intent intent, String str, int i) throws RemoteException, SecurityException {
        ServiceInfo serviceInfo;
        ResolveInfo resolveService = this.mIPackageManager.resolveService(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0L, i);
        if (resolveService == null || (serviceInfo = resolveService.serviceInfo) == null) {
            Slogf.m24e("DevicePolicyManager", "Fail to look up the service: %s or user %d is not running", intent, Integer.valueOf(i));
            return null;
        } else if (!str.equals(serviceInfo.packageName)) {
            throw new SecurityException("Only allow to bind service in " + str);
        } else {
            ServiceInfo serviceInfo2 = resolveService.serviceInfo;
            if (serviceInfo2.exported && !"android.permission.BIND_DEVICE_ADMIN".equals(serviceInfo2.permission)) {
                throw new SecurityException("Service must be protected by BIND_DEVICE_ADMIN permission");
            }
            intent.setComponent(resolveService.serviceInfo.getComponentName());
            return intent;
        }
    }

    public long getLastSecurityLogRetrievalTime() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || canManageUsers(callerIdentity));
        return lambda$getUserDataUnchecked$2(0).mLastSecurityLogRetrievalTime;
    }

    public long getLastBugReportRequestTime() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || canManageUsers(callerIdentity));
        return lambda$getUserDataUnchecked$2(0).mLastBugReportRequestTime;
    }

    public long getLastNetworkLogRetrievalTime() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || (isProfileOwner(callerIdentity) && isManagedProfile(callerIdentity.getUserId())) || canManageUsers(callerIdentity));
        int networkLoggingAffectedUser = getNetworkLoggingAffectedUser();
        if (networkLoggingAffectedUser >= 0) {
            return lambda$getUserDataUnchecked$2(networkLoggingAffectedUser).mLastNetworkLogsRetrievalTime;
        }
        return -1L;
    }

    public boolean setResetPasswordToken(ComponentName componentName, String str, byte[] bArr) {
        CallerIdentity callerIdentity;
        boolean z;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (bArr == null || bArr.length < 32) {
                throw new IllegalArgumentException("token must be at least 32-byte long");
            }
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            int userId = callerIdentity.getUserId();
            if (useDevicePolicyEngine(callerIdentity, null)) {
                EnforcingAdmin enforcePermissionAndGetEnforcingAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", callerIdentity.getPackageName(), -1);
                Long l = (Long) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin, userId);
                long addEscrowToken = addEscrowToken(bArr, l == null ? 0L : l.longValue(), userId);
                if (addEscrowToken == 0) {
                    return false;
                }
                this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin, new LongPolicyValue(addEscrowToken), userId);
                return true;
            }
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
                lambda$getUserDataUnchecked$2.mPasswordTokenHandle = addEscrowToken(bArr, lambda$getUserDataUnchecked$2.mPasswordTokenHandle, userId);
                saveSettingsLocked(userId);
                z = lambda$getUserDataUnchecked$2.mPasswordTokenHandle != 0;
            }
            return z;
        }
        return false;
    }

    public final long addEscrowToken(final byte[] bArr, long j, final int i) {
        resetEscrowToken(j, i);
        return ((Long) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda24
            public final Object getOrThrow() {
                Long lambda$addEscrowToken$137;
                lambda$addEscrowToken$137 = DevicePolicyManagerService.this.lambda$addEscrowToken$137(bArr, i);
                return lambda$addEscrowToken$137;
            }
        })).longValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Long lambda$addEscrowToken$137(byte[] bArr, int i) throws Exception {
        return Long.valueOf(this.mLockPatternUtils.addEscrowToken(bArr, i, (LockPatternUtils.EscrowTokenStateChangeCallback) null));
    }

    public final boolean resetEscrowToken(final long j, final int i) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda48
            public final Object getOrThrow() {
                Boolean lambda$resetEscrowToken$138;
                lambda$resetEscrowToken$138 = DevicePolicyManagerService.this.lambda$resetEscrowToken$138(j, i);
                return lambda$resetEscrowToken$138;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$resetEscrowToken$138(long j, int i) throws Exception {
        if (j != 0) {
            return Boolean.valueOf(this.mLockPatternUtils.removeEscrowToken(j, i));
        }
        return Boolean.FALSE;
    }

    public boolean clearResetPasswordToken(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        boolean z = false;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            int userId = callerIdentity.getUserId();
            if (useDevicePolicyEngine(callerIdentity, null)) {
                EnforcingAdmin enforcePermissionAndGetEnforcingAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", callerIdentity.getPackageName(), -1);
                Long l = (Long) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin, userId);
                if (l != null) {
                    boolean resetEscrowToken = resetEscrowToken(l.longValue(), userId);
                    this.mDevicePolicyEngine.removeLocalPolicy(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin, userId);
                    return resetEscrowToken;
                }
                return false;
            }
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
            synchronized (getLockObject()) {
                DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
                long j = lambda$getUserDataUnchecked$2.mPasswordTokenHandle;
                if (j != 0) {
                    z = resetEscrowToken(j, userId);
                    lambda$getUserDataUnchecked$2.mPasswordTokenHandle = 0L;
                    saveSettingsLocked(userId);
                }
            }
            return z;
        }
        return false;
    }

    public boolean isResetPasswordTokenActive(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        boolean isResetPasswordTokenActiveForUserLocked;
        boolean z = false;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            int userId = callerIdentity.getUserId();
            if (useDevicePolicyEngine(callerIdentity, null)) {
                Long l = (Long) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", callerIdentity.getPackageName(), -1), userId);
                return isResetPasswordTokenActiveForUserLocked(l == null ? 0L : l.longValue(), userId);
            }
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization((isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity)) ? true : true);
            synchronized (getLockObject()) {
                isResetPasswordTokenActiveForUserLocked = isResetPasswordTokenActiveForUserLocked(lambda$getUserDataUnchecked$2(userId).mPasswordTokenHandle, userId);
            }
            return isResetPasswordTokenActiveForUserLocked;
        }
        return false;
    }

    public final boolean isResetPasswordTokenActiveForUserLocked(final long j, final int i) {
        if (j != 0) {
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda112
                public final Object getOrThrow() {
                    Boolean lambda$isResetPasswordTokenActiveForUserLocked$139;
                    lambda$isResetPasswordTokenActiveForUserLocked$139 = DevicePolicyManagerService.this.lambda$isResetPasswordTokenActiveForUserLocked$139(j, i);
                    return lambda$isResetPasswordTokenActiveForUserLocked$139;
                }
            })).booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isResetPasswordTokenActiveForUserLocked$139(long j, int i) throws Exception {
        return Boolean.valueOf(this.mLockPatternUtils.isEscrowTokenActive(j, i));
    }

    public boolean resetPasswordWithToken(ComponentName componentName, String str, String str2, byte[] bArr, int i) {
        CallerIdentity callerIdentity;
        boolean z = false;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Objects.requireNonNull(bArr);
            if (isPermissionCheckFlagEnabled()) {
                callerIdentity = getCallerIdentity(componentName, str);
            } else {
                callerIdentity = getCallerIdentity(componentName);
            }
            int userId = callerIdentity.getUserId();
            if (str2 == null) {
                str2 = "";
            }
            String str3 = str2;
            if (useDevicePolicyEngine(callerIdentity, null)) {
                Long l = (Long) this.mDevicePolicyEngine.getLocalPolicySetByAdmin(PolicyDefinition.RESET_PASSWORD_TOKEN, enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD", callerIdentity.getPackageName(), -1), userId);
                if (l != null && l.longValue() != 0) {
                    z = resetPasswordInternal(str3, l.longValue(), bArr, i, callerIdentity);
                } else {
                    Slogf.m14w("DevicePolicyManager", "No saved token handle");
                }
            } else {
                Objects.requireNonNull(componentName, "ComponentName is null");
                Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
                synchronized (getLockObject()) {
                    long j = lambda$getUserDataUnchecked$2(userId).mPasswordTokenHandle;
                    if (j != 0) {
                        z = resetPasswordInternal(str3, j, bArr, i, callerIdentity);
                    } else {
                        Slogf.m14w("DevicePolicyManager", "No saved token handle");
                    }
                }
            }
            if (z) {
                DevicePolicyEventLogger.createEvent(206).setAdmin(callerIdentity.getComponentName()).write();
            }
            return z;
        }
        return false;
    }

    public boolean isCurrentInputMethodSetByOwner() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isSystemUid(callerIdentity), "Only profile owner, device owner and system may call this method.");
        return lambda$getUserDataUnchecked$2(callerIdentity.getUserId()).mCurrentInputMethodSet;
    }

    public StringParceledListSlice getOwnerInstalledCaCerts(UserHandle userHandle) {
        StringParceledListSlice stringParceledListSlice;
        int identifier = userHandle.getIdentifier();
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization((isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity) || canQueryAdminPolicy(callerIdentity)) && hasFullCrossUsersPermission(callerIdentity, identifier));
        synchronized (getLockObject()) {
            stringParceledListSlice = new StringParceledListSlice(new ArrayList(lambda$getUserDataUnchecked$2(identifier).mOwnerInstalledCaCerts));
        }
        return stringParceledListSlice;
    }

    public void clearApplicationUserData(ComponentName componentName, String str, IPackageDataObserver iPackageDataObserver) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Objects.requireNonNull(str, "packageName is null");
        Objects.requireNonNull(iPackageDataObserver, "callback is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
        checkCanExecuteOrThrowUnsafe(23);
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            try {
                try {
                    ActivityManager.getService().clearApplicationUserData(str, false, iPackageDataObserver, callerIdentity.getUserId());
                } catch (SecurityException e) {
                    Slogf.m13w("DevicePolicyManager", "Not allowed to clear application user data for package " + str, e);
                    iPackageDataObserver.onRemoveCompleted(str, false);
                }
            } catch (RemoteException unused) {
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public void setLogoutEnabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            checkCanExecuteOrThrowUnsafe(34);
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked.isLogoutEnabled == z) {
                    return;
                }
                deviceOwnerAdminLocked.isLogoutEnabled = z;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    public boolean isLogoutEnabled() {
        boolean z = false;
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (deviceOwnerAdminLocked != null && deviceOwnerAdminLocked.isLogoutEnabled) {
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    public List<String> getDisallowedSystemApps(ComponentName componentName, int i, String str) throws RemoteException {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        return new ArrayList(this.mOverlayPackagesProvider.getNonRequiredApps(componentName, i, str));
    }

    public void transferOwnership(ComponentName componentName, ComponentName componentName2, PersistableBundle persistableBundle) {
        String str;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Objects.requireNonNull(componentName2, "Target cannot be null.");
            Preconditions.checkArgument(!componentName.equals(componentName2), "Provided administrator and target are the same object.");
            Preconditions.checkArgument(!componentName.getPackageName().equals(componentName2.getPackageName()), "Provided administrator and target have the same package name.");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity));
            int userId = callerIdentity.getUserId();
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(userId);
            DeviceAdminInfo findAdmin = findAdmin(componentName2, userId, true);
            checkActiveAdminPrecondition(componentName2, findAdmin, lambda$getUserDataUnchecked$2);
            Preconditions.checkArgument(findAdmin.supportsTransferOwnership(), "Provided target does not support ownership transfer.");
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (getLockObject()) {
                    if (persistableBundle == null) {
                        persistableBundle = new PersistableBundle();
                    }
                    if (isProfileOwner(callerIdentity)) {
                        str = "profile-owner";
                        prepareTransfer(componentName, componentName2, persistableBundle, userId, "profile-owner");
                        transferProfileOwnershipLocked(componentName, componentName2, userId);
                        sendProfileOwnerCommand("android.app.action.TRANSFER_OWNERSHIP_COMPLETE", getTransferOwnershipAdminExtras(persistableBundle), userId);
                        postTransfer("android.app.action.PROFILE_OWNER_CHANGED", userId);
                        if (isUserAffiliatedWithDeviceLocked(userId)) {
                            notifyAffiliatedProfileTransferOwnershipComplete(userId);
                        }
                    } else if (isDefaultDeviceOwner(callerIdentity)) {
                        str = "device-owner";
                        prepareTransfer(componentName, componentName2, persistableBundle, userId, "device-owner");
                        transferDeviceOwnershipLocked(componentName, componentName2, userId);
                        sendDeviceOwnerCommand("android.app.action.TRANSFER_OWNERSHIP_COMPLETE", getTransferOwnershipAdminExtras(persistableBundle));
                        postTransfer("android.app.action.DEVICE_OWNER_CHANGED", userId);
                    } else {
                        str = null;
                    }
                }
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                DevicePolicyEventLogger.createEvent(58).setAdmin(componentName).setStrings(new String[]{componentName2.getPackageName(), str}).write();
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                throw th;
            }
        }
    }

    public final void prepareTransfer(ComponentName componentName, ComponentName componentName2, PersistableBundle persistableBundle, int i, String str) {
        saveTransferOwnershipBundleLocked(persistableBundle, i);
        this.mTransferOwnershipMetadataManager.saveMetadataFile(new TransferOwnershipMetadataManager.Metadata(componentName, componentName2, i, str));
    }

    public final void postTransfer(String str, int i) {
        deleteTransferOwnershipMetadataFileLocked();
        sendOwnerChangedBroadcast(str, i);
    }

    public final void notifyAffiliatedProfileTransferOwnershipComplete(int i) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("android.intent.extra.USER", UserHandle.of(i));
        sendDeviceOwnerCommand("android.app.action.AFFILIATED_PROFILE_TRANSFER_OWNERSHIP_COMPLETE", bundle);
    }

    public final void transferProfileOwnershipLocked(ComponentName componentName, ComponentName componentName2, int i) {
        transferActiveAdminUncheckedLocked(componentName2, componentName, i);
        this.mOwners.transferProfileOwner(componentName2, i);
        Slogf.m22i("DevicePolicyManager", "Profile owner set: " + componentName2 + " on user " + i);
        this.mOwners.writeProfileOwner(i);
        this.mDeviceAdminServiceController.startServiceForAdmin(componentName2.getPackageName(), i, "transfer-profile-owner");
    }

    public final void transferDeviceOwnershipLocked(ComponentName componentName, ComponentName componentName2, int i) {
        transferActiveAdminUncheckedLocked(componentName2, componentName, i);
        this.mOwners.transferDeviceOwnership(componentName2);
        Slogf.m22i("DevicePolicyManager", "Device owner set: " + componentName2 + " on user " + i);
        this.mOwners.writeDeviceOwner();
        this.mDeviceAdminServiceController.startServiceForAdmin(componentName2.getPackageName(), i, "transfer-device-owner");
    }

    public final Bundle getTransferOwnershipAdminExtras(PersistableBundle persistableBundle) {
        Bundle bundle = new Bundle();
        if (persistableBundle != null) {
            bundle.putParcelable("android.app.extra.TRANSFER_OWNERSHIP_ADMIN_EXTRAS_BUNDLE", persistableBundle);
        }
        return bundle;
    }

    public void setStartUserSessionMessage(ComponentName componentName, CharSequence charSequence) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            String charSequence2 = charSequence != null ? charSequence.toString() : null;
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (TextUtils.equals(deviceOwnerAdminLocked.startUserSessionMessage, charSequence)) {
                    return;
                }
                deviceOwnerAdminLocked.startUserSessionMessage = charSequence2;
                saveSettingsLocked(callerIdentity.getUserId());
                this.mInjector.getActivityManagerInternal().setSwitchingFromSystemUserMessage(charSequence2);
            }
        }
    }

    public void setEndUserSessionMessage(ComponentName componentName, CharSequence charSequence) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            String charSequence2 = charSequence != null ? charSequence.toString() : null;
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
                if (TextUtils.equals(deviceOwnerAdminLocked.endUserSessionMessage, charSequence)) {
                    return;
                }
                deviceOwnerAdminLocked.endUserSessionMessage = charSequence2;
                saveSettingsLocked(callerIdentity.getUserId());
                this.mInjector.getActivityManagerInternal().setSwitchingToSystemUserMessage(charSequence2);
            }
        }
    }

    public String getStartUserSessionMessage(ComponentName componentName) {
        String str;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            synchronized (getLockObject()) {
                str = getDeviceOwnerAdminLocked().startUserSessionMessage;
            }
            return str;
        }
        return null;
    }

    public String getEndUserSessionMessage(ComponentName componentName) {
        String str;
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            synchronized (getLockObject()) {
                str = getDeviceOwnerAdminLocked().endUserSessionMessage;
            }
            return str;
        }
        return null;
    }

    public final void deleteTransferOwnershipMetadataFileLocked() {
        this.mTransferOwnershipMetadataManager.deleteMetadataFile();
    }

    public PersistableBundle getTransferOwnershipBundle() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity) || isDefaultDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            File file = new File(this.mPathProvider.getUserSystemDirectory(callerIdentity.getUserId()), "transfer-ownership-parameters.xml");
            if (file.exists()) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    try {
                        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                        resolvePullParser.next();
                        PersistableBundle restoreFromXml = PersistableBundle.restoreFromXml(resolvePullParser);
                        fileInputStream.close();
                        return restoreFromXml;
                    } catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (IOException | IllegalArgumentException | XmlPullParserException e) {
                    Slogf.m25e("DevicePolicyManager", "Caught exception while trying to load the owner transfer parameters from file " + file, e);
                    return null;
                }
            }
            return null;
        }
    }

    public int addOverrideApn(ComponentName componentName, final ApnSetting apnSetting) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Objects.requireNonNull(apnSetting, "ApnSetting is null in addOverrideApn");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            if (apnSetting.getApnTypeBitmask() == 16384) {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isManagedProfileOwner(callerIdentity));
            } else {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            }
            final TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                return ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda67
                    public final Object getOrThrow() {
                        Integer lambda$addOverrideApn$140;
                        lambda$addOverrideApn$140 = DevicePolicyManagerService.this.lambda$addOverrideApn$140(telephonyManager, apnSetting);
                        return lambda$addOverrideApn$140;
                    }
                })).intValue();
            }
            Slogf.m14w("DevicePolicyManager", "TelephonyManager is null when trying to add override apn");
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$addOverrideApn$140(TelephonyManager telephonyManager, ApnSetting apnSetting) throws Exception {
        return Integer.valueOf(telephonyManager.addDevicePolicyOverrideApn(this.mContext, apnSetting));
    }

    public boolean updateOverrideApn(ComponentName componentName, final int i, final ApnSetting apnSetting) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Objects.requireNonNull(apnSetting, "ApnSetting is null in updateOverrideApn");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            ApnSetting apnSetting2 = getApnSetting(i);
            if (apnSetting2 != null && apnSetting2.getApnTypeBitmask() == 16384 && apnSetting.getApnTypeBitmask() == 16384) {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isManagedProfileOwner(callerIdentity));
            } else {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            }
            if (i < 0) {
                return false;
            }
            final TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda76
                    public final Object getOrThrow() {
                        Boolean lambda$updateOverrideApn$141;
                        lambda$updateOverrideApn$141 = DevicePolicyManagerService.this.lambda$updateOverrideApn$141(telephonyManager, i, apnSetting);
                        return lambda$updateOverrideApn$141;
                    }
                })).booleanValue();
            }
            Slogf.m14w("DevicePolicyManager", "TelephonyManager is null when trying to modify override apn");
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updateOverrideApn$141(TelephonyManager telephonyManager, int i, ApnSetting apnSetting) throws Exception {
        return Boolean.valueOf(telephonyManager.modifyDevicePolicyOverrideApn(this.mContext, i, apnSetting));
    }

    public boolean removeOverrideApn(ComponentName componentName, int i) {
        boolean z = false;
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            ApnSetting apnSetting = getApnSetting(i);
            if (apnSetting != null && apnSetting.getApnTypeBitmask() == 16384) {
                Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isManagedProfileOwner(callerIdentity)) ? true : true);
            } else {
                Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
            }
            return removeOverrideApnUnchecked(i);
        }
        return false;
    }

    public final boolean removeOverrideApnUnchecked(final int i) {
        return i >= 0 && ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda91
            public final Object getOrThrow() {
                Integer lambda$removeOverrideApnUnchecked$142;
                lambda$removeOverrideApnUnchecked$142 = DevicePolicyManagerService.this.lambda$removeOverrideApnUnchecked$142(i);
                return lambda$removeOverrideApnUnchecked$142;
            }
        })).intValue() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$removeOverrideApnUnchecked$142(int i) throws Exception {
        return Integer.valueOf(this.mContext.getContentResolver().delete(Uri.withAppendedPath(Telephony.Carriers.DPC_URI, Integer.toString(i)), null, null));
    }

    public final ApnSetting getApnSetting(final int i) {
        ApnSetting apnSetting = null;
        if (i < 0) {
            return null;
        }
        Cursor cursor = (Cursor) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda21
            public final Object getOrThrow() {
                Cursor lambda$getApnSetting$143;
                lambda$getApnSetting$143 = DevicePolicyManagerService.this.lambda$getApnSetting$143(i);
                return lambda$getApnSetting$143;
            }
        });
        if (cursor != null) {
            while (cursor.moveToNext() && (apnSetting = ApnSetting.makeApnSetting(cursor)) == null) {
            }
            cursor.close();
        }
        return apnSetting;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Cursor lambda$getApnSetting$143(int i) throws Exception {
        return this.mContext.getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.DPC_URI, Integer.toString(i)), null, null, null, "name ASC");
    }

    public List<ApnSetting> getOverrideApns(ComponentName componentName) {
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isManagedProfileOwner(callerIdentity));
        List<ApnSetting> overrideApnsUnchecked = getOverrideApnsUnchecked();
        if (isProfileOwner(callerIdentity)) {
            ArrayList arrayList = new ArrayList();
            for (ApnSetting apnSetting : overrideApnsUnchecked) {
                if (apnSetting.getApnTypeBitmask() == 16384) {
                    arrayList.add(apnSetting);
                }
            }
            return arrayList;
        }
        return overrideApnsUnchecked;
    }

    public final List<ApnSetting> getOverrideApnsUnchecked() {
        final TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (telephonyManager != null) {
            return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda135
                public final Object getOrThrow() {
                    List lambda$getOverrideApnsUnchecked$144;
                    lambda$getOverrideApnsUnchecked$144 = DevicePolicyManagerService.this.lambda$getOverrideApnsUnchecked$144(telephonyManager);
                    return lambda$getOverrideApnsUnchecked$144;
                }
            });
        }
        Slogf.m14w("DevicePolicyManager", "TelephonyManager is null when trying to get override apns");
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$getOverrideApnsUnchecked$144(TelephonyManager telephonyManager) throws Exception {
        return telephonyManager.getDevicePolicyOverrideApns(this.mContext);
    }

    public void setOverrideApnsEnabled(ComponentName componentName, boolean z) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            checkCanExecuteOrThrowUnsafe(36);
            setOverrideApnsEnabledUnchecked(z);
        }
    }

    public final void setOverrideApnsEnabledUnchecked(boolean z) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("enforced", Boolean.valueOf(z));
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda13
            public final Object getOrThrow() {
                Integer lambda$setOverrideApnsEnabledUnchecked$145;
                lambda$setOverrideApnsEnabledUnchecked$145 = DevicePolicyManagerService.this.lambda$setOverrideApnsEnabledUnchecked$145(contentValues);
                return lambda$setOverrideApnsEnabledUnchecked$145;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$setOverrideApnsEnabledUnchecked$145(ContentValues contentValues) throws Exception {
        return Integer.valueOf(this.mContext.getContentResolver().update(Telephony.Carriers.ENFORCE_MANAGED_URI, contentValues, null, null));
    }

    public boolean isOverrideApnEnabled(ComponentName componentName) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            Cursor cursor = (Cursor) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda109
                public final Object getOrThrow() {
                    Cursor lambda$isOverrideApnEnabled$146;
                    lambda$isOverrideApnEnabled$146 = DevicePolicyManagerService.this.lambda$isOverrideApnEnabled$146();
                    return lambda$isOverrideApnEnabled$146;
                }
            });
            try {
                if (cursor == null) {
                    return false;
                }
                try {
                    if (cursor.moveToFirst()) {
                        return cursor.getInt(cursor.getColumnIndex("enforced")) == 1;
                    }
                } catch (IllegalArgumentException e) {
                    Slogf.m25e("DevicePolicyManager", "Cursor returned from ENFORCE_MANAGED_URI doesn't contain correct info.", e);
                }
                return false;
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Cursor lambda$isOverrideApnEnabled$146() throws Exception {
        return this.mContext.getContentResolver().query(Telephony.Carriers.ENFORCE_MANAGED_URI, null, null, null, null);
    }

    @VisibleForTesting
    public void saveTransferOwnershipBundleLocked(PersistableBundle persistableBundle, int i) {
        File file = new File(this.mPathProvider.getUserSystemDirectory(i), "transfer-ownership-parameters.xml");
        AtomicFile atomicFile = new AtomicFile(file);
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "transfer-ownership-bundle");
                persistableBundle.saveToXml(resolveSerializer);
                resolveSerializer.endTag((String) null, "transfer-ownership-bundle");
                resolveSerializer.endDocument();
                atomicFile.finishWrite(startWrite);
            } catch (IOException | XmlPullParserException e) {
                e = e;
                fileOutputStream = startWrite;
                Slogf.m25e("DevicePolicyManager", "Caught exception while trying to save the owner transfer parameters to file " + file, e);
                file.delete();
                atomicFile.failWrite(fileOutputStream);
            }
        } catch (IOException | XmlPullParserException e2) {
            e = e2;
        }
    }

    public void deleteTransferOwnershipBundleLocked(int i) {
        new File(this.mPathProvider.getUserSystemDirectory(i), "transfer-ownership-parameters.xml").delete();
    }

    public final void logPasswordQualitySetIfSecurityLogEnabled(ComponentName componentName, int i, boolean z, PasswordPolicy passwordPolicy) {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210017, new Object[]{componentName.getPackageName(), Integer.valueOf(i), Integer.valueOf(z ? getProfileParentId(i) : i), Integer.valueOf(passwordPolicy.length), Integer.valueOf(passwordPolicy.quality), Integer.valueOf(passwordPolicy.letters), Integer.valueOf(passwordPolicy.nonLetter), Integer.valueOf(passwordPolicy.numeric), Integer.valueOf(passwordPolicy.upperCase), Integer.valueOf(passwordPolicy.lowerCase), Integer.valueOf(passwordPolicy.symbols)});
        }
    }

    public static String getManagedProvisioningPackage(Context context) {
        return context.getResources().getString(17039958);
    }

    public final void putPrivateDnsSettings(final int i, final String str) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda7
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$putPrivateDnsSettings$147(i, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putPrivateDnsSettings$147(int i, String str) throws Exception {
        ConnectivitySettingsManager.setPrivateDnsMode(this.mContext, i);
        ConnectivitySettingsManager.setPrivateDnsHostname(this.mContext, str);
    }

    public int setGlobalPrivateDns(ComponentName componentName, int i, String str) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            checkAllUsersAreAffiliatedWithDevice();
            checkCanExecuteOrThrowUnsafe(33);
            if (i == 2) {
                if (!TextUtils.isEmpty(str)) {
                    throw new IllegalArgumentException("Host provided for opportunistic mode, but is not needed.");
                }
                putPrivateDnsSettings(2, null);
                return 0;
            } else if (i == 3) {
                if (TextUtils.isEmpty(str) || !NetworkUtilsInternal.isWeaklyValidatedHostname(str)) {
                    throw new IllegalArgumentException(String.format("Provided hostname %s is not valid", str));
                }
                putPrivateDnsSettings(3, str);
                return 0;
            } else {
                throw new IllegalArgumentException(String.format("Provided mode, %d, is not a valid mode.", Integer.valueOf(i)));
            }
        }
        return 2;
    }

    public int getGlobalPrivateDnsMode(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            int privateDnsMode = ConnectivitySettingsManager.getPrivateDnsMode(this.mContext);
            int i = 1;
            if (privateDnsMode != 1) {
                i = 2;
                if (privateDnsMode != 2) {
                    i = 3;
                    if (privateDnsMode != 3) {
                        return 0;
                    }
                }
            }
            return i;
        }
        return 0;
    }

    public String getGlobalPrivateDnsHost(ComponentName componentName) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(getCallerIdentity(componentName)));
            return this.mInjector.settingsGlobalGetString("private_dns_specifier");
        }
        return null;
    }

    public void installUpdateFromFile(ComponentName componentName, String str, final ParcelFileDescriptor parcelFileDescriptor, final StartInstallingUpdateCallback startInstallingUpdateCallback) {
        CallerIdentity callerIdentity;
        if (!isPermissionCheckFlagEnabled()) {
            Objects.requireNonNull(componentName, "ComponentName is null");
        }
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES", callerIdentity.getPackageName(), -1);
        } else {
            callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        }
        checkCanExecuteOrThrowUnsafe(26);
        DevicePolicyEventLogger.createEvent(73).setAdmin(callerIdentity.getPackageName()).setBoolean(isDeviceAB()).write();
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda10
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$installUpdateFromFile$148(parcelFileDescriptor, startInstallingUpdateCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$installUpdateFromFile$148(ParcelFileDescriptor parcelFileDescriptor, StartInstallingUpdateCallback startInstallingUpdateCallback) throws Exception {
        UpdateInstaller nonAbUpdateInstaller;
        if (isDeviceAB()) {
            nonAbUpdateInstaller = new AbUpdateInstaller(this.mContext, parcelFileDescriptor, startInstallingUpdateCallback, this.mInjector, this.mConstants);
        } else {
            nonAbUpdateInstaller = new NonAbUpdateInstaller(this.mContext, parcelFileDescriptor, startInstallingUpdateCallback, this.mInjector, this.mConstants);
        }
        nonAbUpdateInstaller.startInstallUpdate();
    }

    public final boolean isDeviceAB() {
        return "true".equalsIgnoreCase(SystemProperties.get("ro.build.ab_update", ""));
    }

    public void setCrossProfileCalendarPackages(ComponentName componentName, List<String> list) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                getProfileOwnerLocked(callerIdentity.getUserId()).mCrossProfileCalendarPackages = list;
                saveSettingsLocked(callerIdentity.getUserId());
            }
            DevicePolicyEventLogger.createEvent(70).setAdmin(componentName).setStrings(list == null ? null : (String[]) list.toArray(new String[list.size()])).write();
        }
    }

    public List<String> getCrossProfileCalendarPackages(ComponentName componentName) {
        List<String> list;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
        synchronized (getLockObject()) {
            list = getProfileOwnerLocked(callerIdentity.getUserId()).mCrossProfileCalendarPackages;
        }
        return list;
    }

    public boolean isPackageAllowedToAccessCalendarForUser(final String str, final int i) {
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(str, "Package name is null or empty");
            Preconditions.checkArgumentNonnegative(i, "Invalid userId");
            if (getCallerIdentity().getUid() != ((Integer) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda150
                public final Object getOrThrow() {
                    Integer lambda$isPackageAllowedToAccessCalendarForUser$149;
                    lambda$isPackageAllowedToAccessCalendarForUser$149 = DevicePolicyManagerService.this.lambda$isPackageAllowedToAccessCalendarForUser$149(str, i);
                    return lambda$isPackageAllowedToAccessCalendarForUser$149;
                }
            })).intValue()) {
                Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL"));
            }
            synchronized (getLockObject()) {
                if (this.mInjector.settingsSecureGetIntForUser("cross_profile_calendar_enabled", 0, i) == 0) {
                    return false;
                }
                ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
                if (profileOwnerAdminLocked != null) {
                    List<String> list = profileOwnerAdminLocked.mCrossProfileCalendarPackages;
                    if (list == null) {
                        return true;
                    }
                    return list.contains(str);
                }
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$isPackageAllowedToAccessCalendarForUser$149(String str, int i) throws Exception {
        try {
            return Integer.valueOf(this.mInjector.getPackageManager().getPackageUidAsUser(str, i));
        } catch (PackageManager.NameNotFoundException e) {
            Slogf.m10w("DevicePolicyManager", e, "Couldn't find package %s in user %d", str, Integer.valueOf(i));
            return -1;
        }
    }

    public List<String> getCrossProfileCalendarPackagesForUser(int i) {
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Preconditions.checkArgumentNonnegative(i, "Invalid userId");
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") || hasCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL"));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked != null) {
                return profileOwnerAdminLocked.mCrossProfileCalendarPackages;
            }
            return Collections.emptyList();
        }
    }

    public void setCrossProfilePackages(ComponentName componentName, final List<String> list) {
        if (this.mHasFeature) {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Objects.requireNonNull(list, "Package names is null");
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
                final List<String> list2 = profileOwnerLocked.mCrossProfilePackages;
                if (list.equals(list2)) {
                    return;
                }
                profileOwnerLocked.mCrossProfilePackages = list;
                saveSettingsLocked(callerIdentity.getUserId());
                logSetCrossProfilePackages(componentName, list);
                final CrossProfileApps crossProfileApps = (CrossProfileApps) this.mContext.getSystemService(CrossProfileApps.class);
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda57
                    public final void runOrThrow() {
                        DevicePolicyManagerService.lambda$setCrossProfilePackages$150(crossProfileApps, list2, list);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$setCrossProfilePackages$150(CrossProfileApps crossProfileApps, List list, List list2) throws Exception {
        crossProfileApps.resetInteractAcrossProfilesAppOps(list, new HashSet(list2));
    }

    public final void logSetCrossProfilePackages(ComponentName componentName, List<String> list) {
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_CROSS_PROFILE_PACKAGES).setAdmin(componentName).setStrings((String[]) list.toArray(new String[list.size()])).write();
    }

    public List<String> getCrossProfilePackages(ComponentName componentName) {
        List<String> list;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwner(callerIdentity));
        synchronized (getLockObject()) {
            list = getProfileOwnerLocked(callerIdentity.getUserId()).mCrossProfilePackages;
        }
        return list;
    }

    public List<String> getAllCrossProfilePackages() {
        List<String> crossProfilePackagesForAdmins;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isSystemUid(callerIdentity) || isRootUid(callerIdentity) || hasCallingPermission("android.permission.INTERACT_ACROSS_USERS") || hasCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") || hasPermissionForPreflight(callerIdentity, "android.permission.INTERACT_ACROSS_PROFILES"));
        synchronized (getLockObject()) {
            crossProfilePackagesForAdmins = getCrossProfilePackagesForAdmins(getProfileOwnerAdminsForCurrentProfileGroup());
            crossProfilePackagesForAdmins.addAll(getDefaultCrossProfilePackages());
        }
        return crossProfilePackagesForAdmins;
    }

    public final List<String> getCrossProfilePackagesForAdmins(List<ActiveAdmin> list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            arrayList.addAll(list.get(i).mCrossProfilePackages);
        }
        return arrayList;
    }

    public List<String> getDefaultCrossProfilePackages() {
        HashSet hashSet = new HashSet();
        Collections.addAll(hashSet, this.mContext.getResources().getStringArray(17236166));
        Collections.addAll(hashSet, this.mContext.getResources().getStringArray(17236205));
        return new ArrayList(hashSet);
    }

    public final List<ActiveAdmin> getProfileOwnerAdminsForCurrentProfileGroup() {
        ArrayList arrayList;
        ActiveAdmin activeAdminUncheckedLocked;
        synchronized (getLockObject()) {
            arrayList = new ArrayList();
            int[] profileIdsWithDisabled = this.mUserManager.getProfileIdsWithDisabled(this.mInjector.userHandleGetCallingUserId());
            for (int i = 0; i < profileIdsWithDisabled.length; i++) {
                ComponentName lambda$isProfileOwner$66 = lambda$isProfileOwner$66(profileIdsWithDisabled[i]);
                if (lambda$isProfileOwner$66 != null && (activeAdminUncheckedLocked = getActiveAdminUncheckedLocked(lambda$isProfileOwner$66, profileIdsWithDisabled[i])) != null) {
                    arrayList.add(activeAdminUncheckedLocked);
                }
            }
        }
        return arrayList;
    }

    public boolean isManagedKiosk() {
        boolean z = false;
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization((canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS")) ? true : true);
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                try {
                    return isManagedKioskInternal();
                } catch (RemoteException e) {
                    throw new IllegalStateException(e);
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        }
        return false;
    }

    public final boolean isUnattendedManagedKioskUnchecked() {
        try {
            if (isManagedKioskInternal()) {
                if (getPowerManagerInternal().wasDeviceIdleFor(30000L)) {
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isUnattendedManagedKiosk() {
        boolean z = false;
        if (this.mHasFeature) {
            Preconditions.checkCallAuthorization((canManageUsers(getCallerIdentity()) || hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS")) ? true : true);
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda43
                public final Object getOrThrow() {
                    Boolean lambda$isUnattendedManagedKiosk$151;
                    lambda$isUnattendedManagedKiosk$151 = DevicePolicyManagerService.this.lambda$isUnattendedManagedKiosk$151();
                    return lambda$isUnattendedManagedKiosk$151;
                }
            })).booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isUnattendedManagedKiosk$151() throws Exception {
        return Boolean.valueOf(isUnattendedManagedKioskUnchecked());
    }

    public final boolean isManagedKioskInternal() throws RemoteException {
        return (!this.mOwners.hasDeviceOwner() || this.mInjector.getIActivityManager().getLockTaskModeState() != 1 || isLockTaskFeatureEnabled(1) || deviceHasKeyguard() || inEphemeralUserSession()) ? false : true;
    }

    public final boolean isLockTaskFeatureEnabled(int i) throws RemoteException {
        return (lambda$getUserDataUnchecked$2(getCurrentForegroundUserId()).mLockTaskFeatures & i) == i;
    }

    public final boolean deviceHasKeyguard() {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (this.mLockPatternUtils.isSecure(userInfo.id)) {
                return true;
            }
        }
        return false;
    }

    public final boolean inEphemeralUserSession() {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (this.mInjector.getUserManager().isUserEphemeral(userInfo.id)) {
                return true;
            }
        }
        return false;
    }

    public final PowerManagerInternal getPowerManagerInternal() {
        return this.mInjector.getPowerManagerInternal();
    }

    public boolean startViewCalendarEventInManagedProfile(final String str, final long j, final long j2, final long j3, final boolean z, final int i) {
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(str, "Package name is empty");
            final CallerIdentity callerIdentity = getCallerIdentity();
            if (!isCallingFromPackage(str, callerIdentity.getUid())) {
                throw new SecurityException("Input package name doesn't align with actual calling package.");
            }
            return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda55
                public final Object getOrThrow() {
                    Boolean lambda$startViewCalendarEventInManagedProfile$152;
                    lambda$startViewCalendarEventInManagedProfile$152 = DevicePolicyManagerService.this.lambda$startViewCalendarEventInManagedProfile$152(callerIdentity, str, j, j2, j3, z, i);
                    return lambda$startViewCalendarEventInManagedProfile$152;
                }
            })).booleanValue();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$startViewCalendarEventInManagedProfile$152(CallerIdentity callerIdentity, String str, long j, long j2, long j3, boolean z, int i) throws Exception {
        int managedUserId = getManagedUserId(callerIdentity.getUserId());
        if (managedUserId < 0) {
            return Boolean.FALSE;
        }
        if (!isPackageAllowedToAccessCalendarForUser(str, managedUserId)) {
            Slogf.m28d("DevicePolicyManager", "Package %s is not allowed to access cross-profile calendar APIs", str);
            return Boolean.FALSE;
        }
        Intent intent = new Intent("android.provider.calendar.action.VIEW_MANAGED_PROFILE_CALENDAR_EVENT");
        intent.setPackage(str);
        intent.putExtra("id", j);
        intent.putExtra("beginTime", j2);
        intent.putExtra("endTime", j3);
        intent.putExtra("allDay", z);
        intent.setFlags(i);
        try {
            this.mContext.startActivityAsUser(intent, UserHandle.of(managedUserId));
            return Boolean.TRUE;
        } catch (ActivityNotFoundException e) {
            Slogf.m25e("DevicePolicyManager", "View event activity not found", e);
            return Boolean.FALSE;
        }
    }

    public void setApplicationExemptions(final String str, int[] iArr) {
        int i;
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(str, "Package name cannot be empty.");
            Objects.requireNonNull(iArr, "Application exemptions must not be null.");
            Preconditions.checkArgument(areApplicationExemptionsValid(iArr), "Invalid application exemption constant found in application exemptions set.");
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_POLICY_APP_EXEMPTIONS"));
            CallerIdentity callerIdentity = getCallerIdentity();
            final ApplicationInfo packageInfoWithNullCheck = getPackageInfoWithNullCheck(str, callerIdentity);
            Iterator<Map.Entry<Integer, String>> it = APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                final Map.Entry<Integer, String> next = it.next();
                final int unsafeCheckOpNoThrow = this.mInjector.getAppOpsManager().unsafeCheckOpNoThrow(next.getValue(), packageInfoWithNullCheck.uid, packageInfoWithNullCheck.packageName);
                final int i2 = ArrayUtils.contains(iArr, next.getKey().intValue()) ? 0 : 3;
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda39
                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setApplicationExemptions$153(unsafeCheckOpNoThrow, i2, next, packageInfoWithNullCheck, str);
                    }
                });
            }
            String[] strArr = new String[iArr.length];
            for (i = 0; i < iArr.length; i++) {
                strArr[i] = APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS.get(Integer.valueOf(iArr[i]));
            }
            DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_APPLICATION_EXEMPTIONS).setAdmin(callerIdentity.getPackageName()).setStrings(str, strArr).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setApplicationExemptions$153(int i, int i2, Map.Entry entry, ApplicationInfo applicationInfo, String str) throws Exception {
        if (i != i2) {
            this.mInjector.getAppOpsManager().setMode((String) entry.getValue(), applicationInfo.uid, str, i2);
        }
    }

    public int[] getApplicationExemptions(String str) {
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(str, "Package name cannot be empty.");
            Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_DEVICE_POLICY_APP_EXEMPTIONS"));
            ApplicationInfo packageInfoWithNullCheck = getPackageInfoWithNullCheck(str, getCallerIdentity());
            IntArray intArray = new IntArray(0);
            for (Map.Entry<Integer, String> entry : APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS.entrySet()) {
                if (this.mInjector.getAppOpsManager().unsafeCheckOpNoThrow(entry.getValue(), packageInfoWithNullCheck.uid, packageInfoWithNullCheck.packageName) == 0) {
                    intArray.add(entry.getKey().intValue());
                }
            }
            return intArray.toArray();
        }
        return new int[0];
    }

    public final ApplicationInfo getPackageInfoWithNullCheck(String str, CallerIdentity callerIdentity) {
        ApplicationInfo applicationInfo = this.mInjector.getPackageManagerInternal().getApplicationInfo(str, 0L, callerIdentity.getUid(), callerIdentity.getUserId());
        if (applicationInfo != null) {
            return applicationInfo;
        }
        throw new ServiceSpecificException(1, "Package name not found.");
    }

    public final boolean areApplicationExemptionsValid(int[] iArr) {
        for (int i : iArr) {
            if (!APPLICATION_EXEMPTION_CONSTANTS_TO_APP_OPS.containsKey(Integer.valueOf(i))) {
                return false;
            }
        }
        return true;
    }

    public final boolean isCallingFromPackage(final String str, final int i) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda100
            public final Object getOrThrow() {
                Boolean lambda$isCallingFromPackage$154;
                lambda$isCallingFromPackage$154 = DevicePolicyManagerService.this.lambda$isCallingFromPackage$154(str, i);
                return lambda$isCallingFromPackage$154;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isCallingFromPackage$154(String str, int i) throws Exception {
        try {
            return Boolean.valueOf(this.mInjector.getPackageManager().getPackageUidAsUser(str, UserHandle.getUserId(i)) == i);
        } catch (PackageManager.NameNotFoundException e) {
            Slogf.m29d("DevicePolicyManager", "Calling package not found", e);
            return Boolean.FALSE;
        }
    }

    public final DevicePolicyConstants loadConstants() {
        return DevicePolicyConstants.loadFromString(this.mInjector.settingsGlobalGetString("device_policy_constants"));
    }

    public void setUserControlDisabledPackages(ComponentName componentName, String str, final List<String> list) {
        final CallerIdentity callerIdentity;
        Objects.requireNonNull(list, "packages is null");
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        checkCanExecuteOrThrowUnsafe(22);
        List<String> list2 = null;
        if (useDevicePolicyEngine(callerIdentity, null)) {
            final EnforcingAdmin enforcePermissionAndGetEnforcingAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", callerIdentity.getPackageName(), callerIdentity.getUserId());
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda68
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$setUserControlDisabledPackages$155(list, callerIdentity, enforcePermissionAndGetEnforcingAdmin);
                }
            });
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity));
            synchronized (getLockObject()) {
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId());
                if (!Objects.equals(deviceOrProfileOwnerAdminLocked.protectedPackages, list)) {
                    if (!list.isEmpty()) {
                        list2 = list;
                    }
                    deviceOrProfileOwnerAdminLocked.protectedPackages = list2;
                    saveSettingsLocked(callerIdentity.getUserId());
                    pushUserControlDisabledPackagesLocked(callerIdentity.getUserId());
                }
            }
        }
        DevicePolicyEventLogger.createEvent(129).setAdmin(callerIdentity.getPackageName()).setStrings((String[]) list.toArray(new String[list.size()])).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setUserControlDisabledPackages$155(List list, CallerIdentity callerIdentity, EnforcingAdmin enforcingAdmin) throws Exception {
        if (list.isEmpty()) {
            removeUserControlDisabledPackages(callerIdentity, enforcingAdmin);
        } else {
            addUserControlDisabledPackages(callerIdentity, enforcingAdmin, new HashSet(list));
        }
    }

    public final void addUserControlDisabledPackages(CallerIdentity callerIdentity, EnforcingAdmin enforcingAdmin, Set<String> set) {
        if (isCallerDeviceOwner(callerIdentity)) {
            this.mDevicePolicyEngine.setGlobalPolicy(PolicyDefinition.USER_CONTROLLED_DISABLED_PACKAGES, enforcingAdmin, new StringSetPolicyValue(set));
        } else {
            this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.USER_CONTROLLED_DISABLED_PACKAGES, enforcingAdmin, new StringSetPolicyValue(set), callerIdentity.getUserId());
        }
    }

    public final void removeUserControlDisabledPackages(CallerIdentity callerIdentity, EnforcingAdmin enforcingAdmin) {
        if (isCallerDeviceOwner(callerIdentity)) {
            this.mDevicePolicyEngine.removeGlobalPolicy(PolicyDefinition.USER_CONTROLLED_DISABLED_PACKAGES, enforcingAdmin);
        } else {
            this.mDevicePolicyEngine.removeLocalPolicy(PolicyDefinition.USER_CONTROLLED_DISABLED_PACKAGES, enforcingAdmin, callerIdentity.getUserId());
        }
    }

    public final boolean isCallerDeviceOwner(CallerIdentity callerIdentity) {
        boolean z;
        synchronized (getLockObject()) {
            z = getDeviceOwnerUserIdUncheckedLocked() == callerIdentity.getUserId();
        }
        return z;
    }

    public List<String> getUserControlDisabledPackages(ComponentName componentName, String str) {
        CallerIdentity callerIdentity;
        List<String> list;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (useDevicePolicyEngine(callerIdentity, null)) {
            enforceCanQuery("android.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL", callerIdentity.getPackageName(), callerIdentity.getUserId());
            Set set = (Set) this.mDevicePolicyEngine.getResolvedPolicy(PolicyDefinition.USER_CONTROLLED_DISABLED_PACKAGES, callerIdentity.getUserId());
            return set == null ? Collections.emptyList() : set.stream().toList();
        }
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isFinancedDeviceOwner(callerIdentity));
        synchronized (getLockObject()) {
            list = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId()).protectedPackages;
            if (list == null) {
                list = Collections.emptyList();
            }
        }
        return list;
    }

    public void setCommonCriteriaModeEnabled(ComponentName componentName, String str, boolean z) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        ActiveAdmin activeAdmin;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(componentName, str);
        } else {
            callerIdentity = getCallerIdentity(componentName);
        }
        if (isPermissionCheckFlagEnabled()) {
            activeAdmin = enforcePermissionAndGetEnforcingAdmin(componentName, "android.permission.MANAGE_DEVICE_POLICY_COMMON_CRITERIA_MODE", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
        } else {
            Objects.requireNonNull(componentName, "ComponentName is null");
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity), "Common Criteria mode can only be controlled by a device owner or a profile owner on an organization-owned device.");
            synchronized (getLockObject()) {
                profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            }
            activeAdmin = profileOwnerOrDeviceOwnerLocked;
        }
        synchronized (getLockObject()) {
            activeAdmin.mCommonCriteriaMode = z;
            saveSettingsLocked(callerIdentity.getUserId());
        }
        DevicePolicyEventLogger.createEvent(131).setAdmin(callerIdentity.getPackageName()).setBoolean(z).write();
    }

    public boolean isCommonCriteriaModeEnabled(ComponentName componentName) {
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        boolean z;
        boolean z2;
        if (componentName != null) {
            CallerIdentity callerIdentity = getCallerIdentity(componentName);
            Preconditions.checkCallAuthorization((isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) ? true : true, "Common Criteria mode can only be controlled by a device owner or a profile owner on an organization-owned device.");
            synchronized (getLockObject()) {
                z2 = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).mCommonCriteriaMode;
            }
            return z2;
        }
        synchronized (getLockObject()) {
            if (isHeadlessFlagEnabled()) {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
            } else {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
            }
            z = deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked != null ? deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked.mCommonCriteriaMode : false;
        }
        return z;
    }

    public int getPersonalAppsSuspendedReasons(ComponentName componentName) {
        int makeSuspensionReasons;
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            long j = profileOwnerLocked.mProfileOffDeadline;
            makeSuspensionReasons = makeSuspensionReasons(profileOwnerLocked.mSuspendPersonalApps, j != 0 && this.mInjector.systemCurrentTimeMillis() > j);
            Slogf.m28d("DevicePolicyManager", "getPersonalAppsSuspendedReasons user: %d; result: %d", Integer.valueOf(this.mInjector.userHandleGetCallingUserId()), Integer.valueOf(makeSuspensionReasons));
        }
        return makeSuspensionReasons;
    }

    public void setPersonalAppsSuspended(ComponentName componentName, boolean z) {
        boolean z2;
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        Preconditions.checkState(canHandleCheckPolicyComplianceIntent(callerIdentity));
        final int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(userId);
            boolean z3 = true;
            if (profileOwnerLocked.mSuspendPersonalApps != z) {
                profileOwnerLocked.mSuspendPersonalApps = z;
                z2 = true;
            } else {
                z2 = false;
            }
            if (profileOwnerLocked.mProfileOffDeadline != 0) {
                profileOwnerLocked.mProfileOffDeadline = 0L;
            } else {
                z3 = z2;
            }
            if (z3) {
                saveSettingsLocked(userId);
            }
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda123
            public final Object getOrThrow() {
                Boolean lambda$setPersonalAppsSuspended$156;
                lambda$setPersonalAppsSuspended$156 = DevicePolicyManagerService.this.lambda$setPersonalAppsSuspended$156(userId);
                return lambda$setPersonalAppsSuspended$156;
            }
        });
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED).setAdmin(callerIdentity.getComponentName()).setBoolean(z).write();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$setPersonalAppsSuspended$156(int i) throws Exception {
        return Boolean.valueOf(updatePersonalAppsSuspension(i, this.mUserManager.isUserUnlocked(i)));
    }

    public final void triggerPolicyComplianceCheckIfNeeded(int i, boolean z) {
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            if (profileOwnerAdminLocked == null) {
                Slogf.wtf("DevicePolicyManager", "Profile owner not found for compliance check");
                return;
            }
            if (z) {
                Intent intent = new Intent("android.app.action.CHECK_POLICY_COMPLIANCE");
                intent.setPackage(profileOwnerAdminLocked.info.getPackageName());
                this.mContext.startActivityAsUser(intent, UserHandle.of(i));
            } else if (profileOwnerAdminLocked.mProfileOffDeadline > 0) {
                sendAdminCommandLocked(profileOwnerAdminLocked, "android.app.action.COMPLIANCE_ACKNOWLEDGEMENT_REQUIRED", null, null, true);
            }
        }
    }

    public final boolean updatePersonalAppsSuspension(int i, boolean z) {
        boolean z2;
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            z2 = false;
            if (profileOwnerAdminLocked != null) {
                int updateProfileOffDeadlineLocked = updateProfileOffDeadlineLocked(i, profileOwnerAdminLocked, z);
                boolean z3 = profileOwnerAdminLocked.mSuspendPersonalApps;
                boolean z4 = profileOwnerAdminLocked.mProfileOffDeadline == -1;
                Slogf.m28d("DevicePolicyManager", "Personal apps suspended explicitly: %b, by timeout: %b, notification: %d", Boolean.valueOf(z3), Boolean.valueOf(z4), Integer.valueOf(updateProfileOffDeadlineLocked));
                updateProfileOffDeadlineNotificationLocked(i, profileOwnerAdminLocked, updateProfileOffDeadlineLocked);
                if (z3 || z4) {
                    z2 = true;
                }
            }
        }
        suspendPersonalAppsInternal(getProfileParentId(i), z2);
        return z2;
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int updateProfileOffDeadlineLocked(int i, ActiveAdmin activeAdmin, boolean z) {
        boolean z2;
        long j;
        long systemCurrentTimeMillis = this.mInjector.systemCurrentTimeMillis();
        long j2 = activeAdmin.mProfileOffDeadline;
        int i2 = 0;
        if (j2 != 0 && systemCurrentTimeMillis > j2) {
            Slogf.m22i("DevicePolicyManager", "Profile off deadline has been reached, unlocked: " + z);
            if (activeAdmin.mProfileOffDeadline != -1) {
                activeAdmin.mProfileOffDeadline = -1L;
                saveSettingsLocked(i);
            }
            return z ? 0 : 2;
        }
        if (activeAdmin.mSuspendPersonalApps) {
            if (j2 != 0) {
                activeAdmin.mProfileOffDeadline = 0L;
                z2 = true;
            }
            z2 = false;
        } else {
            if (j2 != 0 && activeAdmin.mProfileMaximumTimeOffMillis == 0) {
                Slogf.m22i("DevicePolicyManager", "Profile off deadline is reset to zero");
                activeAdmin.mProfileOffDeadline = 0L;
            } else {
                if (j2 == 0 && activeAdmin.mProfileMaximumTimeOffMillis != 0 && !z) {
                    Slogf.m22i("DevicePolicyManager", "Profile off deadline is set.");
                    activeAdmin.mProfileOffDeadline = activeAdmin.mProfileMaximumTimeOffMillis + systemCurrentTimeMillis;
                }
                z2 = false;
            }
            z2 = true;
        }
        if (z2) {
            saveSettingsLocked(i);
        }
        if (!z) {
            j = activeAdmin.mProfileOffDeadline;
            if (j != 0) {
                long j3 = MANAGED_PROFILE_OFF_WARNING_PERIOD;
                if (j - systemCurrentTimeMillis < j3) {
                    i2 = 1;
                } else {
                    j -= j3;
                }
                AlarmManager alarmManager = this.mInjector.getAlarmManager();
                Intent intent = new Intent(ACTION_PROFILE_OFF_DEADLINE);
                intent.setPackage(this.mContext.getPackageName());
                PendingIntent pendingIntentGetBroadcast = this.mInjector.pendingIntentGetBroadcast(this.mContext, 5572, intent, 1275068416);
                if (j != 0) {
                    Slogf.m22i("DevicePolicyManager", "Profile off deadline alarm is removed.");
                    alarmManager.cancel(pendingIntentGetBroadcast);
                } else {
                    Slogf.m22i("DevicePolicyManager", "Profile off deadline alarm is set.");
                    alarmManager.set(1, j, pendingIntentGetBroadcast);
                }
                return i2;
            }
        }
        j = 0;
        AlarmManager alarmManager2 = this.mInjector.getAlarmManager();
        Intent intent2 = new Intent(ACTION_PROFILE_OFF_DEADLINE);
        intent2.setPackage(this.mContext.getPackageName());
        PendingIntent pendingIntentGetBroadcast2 = this.mInjector.pendingIntentGetBroadcast(this.mContext, 5572, intent2, 1275068416);
        if (j != 0) {
        }
        return i2;
    }

    public final void suspendPersonalAppsInternal(int i, boolean z) {
        if (lambda$getUserDataUnchecked$2(i).mAppsSuspended == z) {
            return;
        }
        Object[] objArr = new Object[2];
        objArr[0] = z ? "Suspending" : "Unsuspending";
        objArr[1] = Integer.valueOf(i);
        Slogf.m20i("DevicePolicyManager", "%s personal apps for user %d", objArr);
        if (z) {
            suspendPersonalAppsInPackageManager(i);
        } else {
            this.mInjector.getPackageManagerInternal().unsuspendForSuspendingPackage(PackageManagerShellCommandDataLoader.PACKAGE, i);
        }
        synchronized (getLockObject()) {
            lambda$getUserDataUnchecked$2(i).mAppsSuspended = z;
            saveSettingsLocked(i);
        }
    }

    public final void suspendPersonalAppsInPackageManager(final int i) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda5
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$suspendPersonalAppsInPackageManager$157(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suspendPersonalAppsInPackageManager$157(int i) throws Exception {
        String[] packagesSuspendedByAdmin = this.mInjector.getPackageManagerInternal().setPackagesSuspendedByAdmin(i, this.mInjector.getPersonalAppsForSuspension(i), true);
        if (ArrayUtils.isEmpty(packagesSuspendedByAdmin)) {
            return;
        }
        Slogf.wtf("DevicePolicyManager", "Failed to suspend apps: " + String.join(",", packagesSuspendedByAdmin));
    }

    public final void notifyIfManagedSubscriptionsAreUnavailable(UserHandle userHandle, boolean z) {
        if (!isManagedProfile(userHandle.getIdentifier())) {
            Slog.wtf("DevicePolicyManager", "Expected managed profile when notified of profile availability change.");
        }
        if (getManagedSubscriptionsPolicy().getPolicyType() != 1) {
            return;
        }
        if (z) {
            this.mInjector.getNotificationManager().cancel(1006);
            return;
        }
        Intent intent = new Intent(ACTION_TURN_PROFILE_ON_NOTIFICATION);
        intent.putExtra("android.intent.extra.user_handle", userHandle.getIdentifier());
        Notification.Action build = new Notification.Action.Builder((Icon) null, getUnpauseWorkAppsButtonText(), this.mInjector.pendingIntentGetBroadcast(this.mContext, 0, intent, 201326592)).build();
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", getWorkProfileContentDescription());
        this.mInjector.getNotificationManager().notifyAsUser(null, 1006, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302822).setContentTitle(getUnpauseWorkAppsForTelephonyTitle()).setContentText(getUnpauseWorkAppsForTelephonyText()).setStyle(new Notification.BigTextStyle().bigText(getUnpauseWorkAppsForTelephonyText())).addAction(build).addExtras(bundle).setOngoing(false).setShowWhen(true).setAutoCancel(true).build(), UserHandle.of(getProfileParentId(userHandle.getIdentifier())));
    }

    public final String getUnpauseWorkAppsButtonText() {
        return getUpdatableString("Core.TURN_ON_WORK_PROFILE_BUTTON_TEXT", 17041809, new Object[0]);
    }

    public final String getUnpauseWorkAppsForTelephonyTitle() {
        return getUpdatableString("Core.WORK_PROFILE_TELEPHONY_UNAVAILABLE_TITLE", 17041808, new Object[0]);
    }

    public final String getUnpauseWorkAppsForTelephonyText() {
        return getUpdatableString("Core.WORK_PROFILE_TELEPHONY_UNAVAILABLE_BODY", 17041807, new Object[0]);
    }

    @GuardedBy({"getLockObject()"})
    public final void updateProfileOffDeadlineNotificationLocked(final int i, ActiveAdmin activeAdmin, int i2) {
        String personalAppSuspensionText;
        if (i2 == 0) {
            this.mInjector.getNotificationManager().cancel(1003);
            return;
        }
        Intent intent = new Intent(ACTION_TURN_PROFILE_ON_NOTIFICATION);
        intent.setPackage(this.mContext.getPackageName());
        intent.putExtra("android.intent.extra.user_handle", i);
        Notification.Action build = new Notification.Action.Builder((Icon) null, getPersonalAppSuspensionButtonText(), this.mInjector.pendingIntentGetBroadcast(this.mContext, 0, intent, 201326592)).build();
        boolean z = true;
        if (i2 == 1) {
            long j = activeAdmin.mProfileMaximumTimeOffMillis;
            long j2 = MS_PER_DAY;
            personalAppSuspensionText = getPersonalAppSuspensionSoonText(DateUtils.formatDateTime(this.mContext, activeAdmin.mProfileOffDeadline, 16), DateUtils.formatDateTime(this.mContext, activeAdmin.mProfileOffDeadline, 1), (int) ((j + (j2 / 2)) / j2));
            z = false;
        } else {
            personalAppSuspensionText = getPersonalAppSuspensionText();
        }
        int color = this.mContext.getColor(17171002);
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", getWorkProfileContentDescription());
        final Notification build2 = new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302407).setOngoing(z).setAutoCancel(false).setContentTitle(getPersonalAppSuspensionTitle()).setContentText(personalAppSuspensionText).setStyle(new Notification.BigTextStyle().bigText(personalAppSuspensionText)).setColor(color).addAction(build).addExtras(bundle).build();
        this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda181
            @Override // java.lang.Runnable
            public final void run() {
                DevicePolicyManagerService.this.lambda$updateProfileOffDeadlineNotificationLocked$158(build2, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProfileOffDeadlineNotificationLocked$158(Notification notification, int i) {
        this.mInjector.getNotificationManager().notifyAsUser(null, 1003, notification, UserHandle.of(getProfileParentId(i)));
    }

    public final String getPersonalAppSuspensionButtonText() {
        return getUpdatableString("Core.PERSONAL_APP_SUSPENSION_TURN_ON_PROFILE", 17041306, new Object[0]);
    }

    public final String getPersonalAppSuspensionTitle() {
        return getUpdatableString("Core.PERSONAL_APP_SUSPENSION_TITLE", 17041309, new Object[0]);
    }

    public final String getPersonalAppSuspensionText() {
        return getUpdatableString("Core.PERSONAL_APP_SUSPENSION_MESSAGE", 17041308, new Object[0]);
    }

    public final String getPersonalAppSuspensionSoonText(String str, String str2, int i) {
        return getUpdatableString("Core.PERSONAL_APP_SUSPENSION_SOON_MESSAGE", 17041307, str, str2, Integer.valueOf(i));
    }

    public final String getWorkProfileContentDescription() {
        return getUpdatableString("Core.NOTIFICATION_WORK_PROFILE_CONTENT_DESCRIPTION", 17040912, new Object[0]);
    }

    public void setManagedProfileMaximumTimeOff(ComponentName componentName, long j) {
        Objects.requireNonNull(componentName, "ComponentName is null");
        Preconditions.checkArgumentNonnegative(j, "Timeout must be non-negative.");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        Preconditions.checkState(canHandleCheckPolicyComplianceIntent(callerIdentity));
        final int userId = callerIdentity.getUserId();
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(userId);
            if (j > 0) {
                long j2 = MANAGED_PROFILE_MAXIMUM_TIME_OFF_THRESHOLD;
                if (j < j2 && !isAdminTestOnlyLocked(componentName, userId)) {
                    j = j2;
                }
            }
            if (profileOwnerLocked.mProfileMaximumTimeOffMillis == j) {
                return;
            }
            profileOwnerLocked.mProfileMaximumTimeOffMillis = j;
            saveSettingsLocked(userId);
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda157
                public final Object getOrThrow() {
                    Boolean lambda$setManagedProfileMaximumTimeOff$159;
                    lambda$setManagedProfileMaximumTimeOff$159 = DevicePolicyManagerService.this.lambda$setManagedProfileMaximumTimeOff$159(userId);
                    return lambda$setManagedProfileMaximumTimeOff$159;
                }
            });
            DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.f428x75732685).setAdmin(callerIdentity.getComponentName()).setTimePeriod(j).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$setManagedProfileMaximumTimeOff$159(int i) throws Exception {
        return Boolean.valueOf(updatePersonalAppsSuspension(i, this.mUserManager.isUserUnlocked()));
    }

    public final boolean canHandleCheckPolicyComplianceIntent(final CallerIdentity callerIdentity) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda186
            public final Object getOrThrow() {
                Boolean lambda$canHandleCheckPolicyComplianceIntent$160;
                lambda$canHandleCheckPolicyComplianceIntent$160 = DevicePolicyManagerService.this.lambda$canHandleCheckPolicyComplianceIntent$160(callerIdentity);
                return lambda$canHandleCheckPolicyComplianceIntent$160;
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$canHandleCheckPolicyComplianceIntent$160(CallerIdentity callerIdentity) throws Exception {
        Intent intent = new Intent("android.app.action.CHECK_POLICY_COMPLIANCE");
        intent.setPackage(callerIdentity.getPackageName());
        return Boolean.valueOf(!this.mInjector.getPackageManager().queryIntentActivitiesAsUser(intent, 0, callerIdentity.getUserId()).isEmpty());
    }

    public long getManagedProfileMaximumTimeOff(ComponentName componentName) {
        long j;
        Objects.requireNonNull(componentName, "ComponentName is null");
        CallerIdentity callerIdentity = getCallerIdentity(componentName);
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        synchronized (getLockObject()) {
            j = getProfileOwnerLocked(callerIdentity.getUserId()).mProfileMaximumTimeOffMillis;
        }
        return j;
    }

    public void acknowledgeDeviceCompliant() {
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        enforceUserUnlocked(callerIdentity.getUserId());
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            if (profileOwnerLocked.mProfileOffDeadline > 0) {
                profileOwnerLocked.mProfileOffDeadline = 0L;
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
    }

    public boolean isComplianceAcknowledgementRequired() {
        boolean z;
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        enforceUserUnlocked(callerIdentity.getUserId());
        synchronized (getLockObject()) {
            z = getProfileOwnerLocked(callerIdentity.getUserId()).mProfileOffDeadline != 0;
        }
        return z;
    }

    public boolean canProfileOwnerResetPasswordWhenLocked(int i) {
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()), String.format("Only the system can %s", "call canProfileOwnerResetPasswordWhenLocked"));
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerAdminLocked = getProfileOwnerAdminLocked(i);
            DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(i);
            if (profileOwnerAdminLocked != null && getEncryptionStatus() == 5 && isResetPasswordTokenActiveForUserLocked(lambda$getUserDataUnchecked$2.mPasswordTokenHandle, i)) {
                try {
                    ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(profileOwnerAdminLocked.info.getPackageName(), 0L, i);
                    if (applicationInfo == null) {
                        Slogf.wtf("DevicePolicyManager", "Cannot find AppInfo for profile owner");
                        return false;
                    } else if (applicationInfo.isEncryptionAware()) {
                        Slogf.m30d("DevicePolicyManager", "PO should be able to reset password from direct boot");
                        return true;
                    } else {
                        return false;
                    }
                } catch (RemoteException e) {
                    Slogf.m25e("DevicePolicyManager", "Failed to query PO app info", e);
                    return false;
                }
            }
            return false;
        }
    }

    public String getEnrollmentSpecificId(String str) {
        String str2;
        if (this.mHasFeature) {
            CallerIdentity callerIdentity = getCallerIdentity(str);
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isCallerDelegate(callerIdentity, "delegation-cert-install"));
            synchronized (getLockObject()) {
                str2 = getDeviceOrProfileOwnerAdminLocked(callerIdentity.getUserId()).mEnrollmentSpecificId;
                if (str2 == null) {
                    str2 = "";
                }
            }
            return str2;
        }
        return "";
    }

    public void setOrganizationIdForUser(String str, String str2, int i) {
        String packageName;
        if (this.mHasFeature) {
            Objects.requireNonNull(str);
            CallerIdentity callerIdentity = getCallerIdentity(str);
            boolean z = false;
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity), "Only a Device Owner or Profile Owner may set the Enterprise ID.");
            Preconditions.checkArgument(!TextUtils.isEmpty(str2), "Enterprise ID may not be empty.");
            Slogf.m20i("DevicePolicyManager", "Setting Enterprise ID to %s for user %d", str2, Integer.valueOf(i));
            synchronized (this.mESIDInitilizationLock) {
                if (this.mEsidCalculator == null) {
                    this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda33
                        public final void runOrThrow() {
                            DevicePolicyManagerService.this.lambda$setOrganizationIdForUser$161();
                        }
                    });
                }
            }
            synchronized (getLockObject()) {
                ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
                Preconditions.checkCallAuthorization(deviceOrProfileOwnerAdminLocked != null && deviceOrProfileOwnerAdminLocked.getUserHandle().getIdentifier() == i, String.format("The Profile Owner or Device Owner may only set the Enterprise ID on its own user, called on user %d but owner user is %d", Integer.valueOf(i), Integer.valueOf(deviceOrProfileOwnerAdminLocked.getUserHandle().getIdentifier())));
                packageName = deviceOrProfileOwnerAdminLocked.info.getPackageName();
                if (TextUtils.isEmpty(deviceOrProfileOwnerAdminLocked.mOrganizationId) || deviceOrProfileOwnerAdminLocked.mOrganizationId.equals(str2)) {
                    z = true;
                }
                Preconditions.checkState(z, "The organization ID has been previously set to a different value and cannot be changed");
                String calculateEnterpriseId = this.mEsidCalculator.calculateEnterpriseId(deviceOrProfileOwnerAdminLocked.info.getPackageName(), str2);
                deviceOrProfileOwnerAdminLocked.mOrganizationId = str2;
                deviceOrProfileOwnerAdminLocked.mEnrollmentSpecificId = calculateEnterpriseId;
                saveSettingsLocked(i);
            }
            DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_ORGANIZATION_ID).setAdmin(packageName).setBoolean(isManagedProfile(i)).write();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOrganizationIdForUser$161() throws Exception {
        this.mEsidCalculator = this.mInjector.newEnterpriseSpecificIdCalculator();
    }

    public void clearOrganizationIdForUser(int i) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            deviceOrProfileOwnerAdminLocked.mOrganizationId = null;
            deviceOrProfileOwnerAdminLocked.mEnrollmentSpecificId = null;
            saveSettingsLocked(i);
        }
    }

    public UserHandle createAndProvisionManagedProfile(ManagedProfileProvisioningParams managedProfileProvisioningParams, String str) {
        UserInfo userInfo;
        Set<String> nonRequiredApps;
        Objects.requireNonNull(managedProfileProvisioningParams, "provisioningParams is null");
        Objects.requireNonNull(str, "callerPackage is null");
        ComponentName profileAdminComponentName = managedProfileProvisioningParams.getProfileAdminComponentName();
        Objects.requireNonNull(profileAdminComponentName, "admin is null");
        CallerIdentity callerIdentity = getCallerIdentity(str);
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        managedProfileProvisioningParams.logParams(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                int checkProvisioningPreconditionSkipPermission = checkProvisioningPreconditionSkipPermission("android.app.action.PROVISION_MANAGED_PROFILE", profileAdminComponentName.getPackageName(), callerIdentity.getUserId());
                if (checkProvisioningPreconditionSkipPermission != 0) {
                    throw new ServiceSpecificException(1, "Provisioning preconditions failed with result: " + checkProvisioningPreconditionSkipPermission);
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                onCreateAndProvisionManagedProfileStarted(managedProfileProvisioningParams);
                if (managedProfileProvisioningParams.isLeaveAllSystemAppsEnabled()) {
                    nonRequiredApps = Collections.emptySet();
                } else {
                    nonRequiredApps = this.mOverlayPackagesProvider.getNonRequiredApps(profileAdminComponentName, callerIdentity.getUserId(), "android.app.action.PROVISION_MANAGED_PROFILE");
                }
                if (nonRequiredApps.isEmpty()) {
                    Slogf.m22i("DevicePolicyManager", "No disallowed packages for the managed profile.");
                } else {
                    Iterator<String> it = nonRequiredApps.iterator();
                    while (it.hasNext()) {
                        Slogf.m22i("DevicePolicyManager", "Disallowed package [" + it.next() + "]");
                    }
                }
                UserInfo createProfileForUserEvenWhenDisallowed = this.mUserManager.createProfileForUserEvenWhenDisallowed(managedProfileProvisioningParams.getProfileName(), "android.os.usertype.profile.MANAGED", 64, callerIdentity.getUserId(), (String[]) nonRequiredApps.toArray(new String[nonRequiredApps.size()]));
                try {
                    if (createProfileForUserEvenWhenDisallowed == null) {
                        throw new ServiceSpecificException(2, "Error creating profile, createProfileForUserEvenWhenDisallowed returned null.");
                    }
                    resetInteractAcrossProfilesAppOps(callerIdentity.getUserId());
                    logEventDuration(FrameworkStatsLog.f391x3c5de2c3, elapsedRealtime, str);
                    maybeInstallDevicePolicyManagementRoleHolderInUser(createProfileForUserEvenWhenDisallowed.id);
                    installExistingAdminPackage(createProfileForUserEvenWhenDisallowed.id, profileAdminComponentName.getPackageName());
                    if (!enableAdminAndSetProfileOwner(createProfileForUserEvenWhenDisallowed.id, callerIdentity.getUserId(), profileAdminComponentName)) {
                        throw new ServiceSpecificException(4, "Error setting profile owner.");
                    }
                    setUserSetupComplete(createProfileForUserEvenWhenDisallowed.id);
                    startProfileForSetup(createProfileForUserEvenWhenDisallowed.id, str);
                    maybeMigrateAccount(createProfileForUserEvenWhenDisallowed.id, callerIdentity.getUserId(), managedProfileProvisioningParams.getAccountToMigrate(), managedProfileProvisioningParams.isKeepingAccountOnMigration(), str);
                    if (managedProfileProvisioningParams.isOrganizationOwnedProvisioning()) {
                        synchronized (getLockObject()) {
                            setProfileOwnerOnOrganizationOwnedDeviceUncheckedLocked(profileAdminComponentName, createProfileForUserEvenWhenDisallowed.id, true);
                        }
                    }
                    onCreateAndProvisionManagedProfileCompleted(managedProfileProvisioningParams);
                    sendProvisioningCompletedBroadcast(createProfileForUserEvenWhenDisallowed.id, "android.app.action.PROVISION_MANAGED_PROFILE", managedProfileProvisioningParams.isLeaveAllSystemAppsEnabled());
                    return createProfileForUserEvenWhenDisallowed.getUserHandle();
                } catch (Exception e) {
                    e = e;
                    userInfo = createProfileForUserEvenWhenDisallowed;
                    DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_ERROR).setStrings(new String[]{str}).write();
                    if (userInfo != null) {
                        this.mUserManager.removeUserEvenWhenDisallowed(userInfo.id);
                    }
                    throw e;
                }
            } catch (Exception e2) {
                e = e2;
                userInfo = null;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void finalizeWorkProfileProvisioning(UserHandle userHandle, Account account) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        if (!isManagedProfile(userHandle.getIdentifier())) {
            throw new IllegalStateException("Given user is not a managed profile");
        }
        ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(userHandle.getIdentifier());
        if (profileOwnerComponent == null) {
            throw new IllegalStateException("There is no profile owner on the given profile");
        }
        Intent intent = new Intent("android.app.action.MANAGED_PROFILE_PROVISIONED");
        intent.setPackage(profileOwnerComponent.getPackageName());
        intent.addFlags(268435488);
        intent.putExtra("android.intent.extra.USER", userHandle);
        if (account != null) {
            intent.putExtra("android.app.extra.PROVISIONING_ACCOUNT_TO_MIGRATE", account);
        }
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(getProfileParentId(userHandle.getIdentifier())));
    }

    public final void maybeInstallDevicePolicyManagementRoleHolderInUser(int i) {
        String devicePolicyManagementRoleHolderPackageName = getDevicePolicyManagementRoleHolderPackageName(this.mContext);
        if (devicePolicyManagementRoleHolderPackageName == null) {
            Slogf.m30d("DevicePolicyManager", "No device policy management role holder specified.");
            return;
        }
        try {
            if (this.mIPackageManager.isPackageAvailable(devicePolicyManagementRoleHolderPackageName, i)) {
                Slogf.m30d("DevicePolicyManager", "The device policy management role holder " + devicePolicyManagementRoleHolderPackageName + " is already installed in user " + i);
                return;
            }
            Slogf.m30d("DevicePolicyManager", "Installing the device policy management role holder " + devicePolicyManagementRoleHolderPackageName + " in user " + i);
            this.mIPackageManager.installExistingPackageAsUser(devicePolicyManagementRoleHolderPackageName, i, 4194304, 1, (List) null);
        } catch (RemoteException unused) {
        }
    }

    public final String getDevicePolicyManagementRoleHolderPackageName(Context context) {
        final RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
        return (String) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda77
            public final Object getOrThrow() {
                String lambda$getDevicePolicyManagementRoleHolderPackageName$162;
                lambda$getDevicePolicyManagementRoleHolderPackageName$162 = DevicePolicyManagerService.lambda$getDevicePolicyManagementRoleHolderPackageName$162(roleManager);
                return lambda$getDevicePolicyManagementRoleHolderPackageName$162;
            }
        });
    }

    public static /* synthetic */ String lambda$getDevicePolicyManagementRoleHolderPackageName$162(RoleManager roleManager) throws Exception {
        List roleHolders = roleManager.getRoleHolders("android.app.role.DEVICE_POLICY_MANAGEMENT");
        if (roleHolders.isEmpty()) {
            return null;
        }
        return (String) roleHolders.get(0);
    }

    public final boolean isCallerDevicePolicyManagementRoleHolder(CallerIdentity callerIdentity) {
        return callerIdentity.getUid() == this.mInjector.getPackageManagerInternal().getPackageUid(getDevicePolicyManagementRoleHolderPackageName(this.mContext), 0L, callerIdentity.getUserId());
    }

    public final void resetInteractAcrossProfilesAppOps(int i) {
        this.mInjector.getCrossProfileApps(i).clearInteractAcrossProfilesAppOps();
        pregrantDefaultInteractAcrossProfilesAppOps(i);
    }

    public final void pregrantDefaultInteractAcrossProfilesAppOps(int i) {
        String permissionToOp = AppOpsManager.permissionToOp("android.permission.INTERACT_ACROSS_PROFILES");
        for (String str : getConfigurableDefaultCrossProfilePackages(i)) {
            if (appOpIsDefaultOrAllowed(i, permissionToOp, str)) {
                this.mInjector.getCrossProfileApps(i).setInteractAcrossProfilesAppOp(str, 0);
            }
        }
    }

    public final Set<String> getConfigurableDefaultCrossProfilePackages(int i) {
        Stream<String> stream = getDefaultCrossProfilePackages().stream();
        final CrossProfileApps crossProfileApps = this.mInjector.getCrossProfileApps(i);
        Objects.requireNonNull(crossProfileApps);
        return (Set) stream.filter(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda42
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return crossProfileApps.canConfigureInteractAcrossProfiles((String) obj);
            }
        }).collect(Collectors.toSet());
    }

    public final boolean appOpIsDefaultOrAllowed(int i, String str, String str2) {
        try {
            int unsafeCheckOpNoThrow = this.mInjector.getAppOpsManager().unsafeCheckOpNoThrow(str, this.mContext.createContextAsUser(UserHandle.of(i), 0).getPackageManager().getPackageUid(str2, 0), str2);
            return unsafeCheckOpNoThrow == 0 || unsafeCheckOpNoThrow == 3;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void installExistingAdminPackage(int i, String str) {
        try {
            int installExistingPackageAsUser = this.mContext.getPackageManager().installExistingPackageAsUser(str, i);
            if (installExistingPackageAsUser == 1) {
                return;
            }
            throw new ServiceSpecificException(3, String.format("Failed to install existing package %s for user %d with result code %d", str, Integer.valueOf(i), Integer.valueOf(installExistingPackageAsUser)));
        } catch (PackageManager.NameNotFoundException e) {
            throw new ServiceSpecificException(3, String.format("Failed to install existing package %s for user %d: %s", str, Integer.valueOf(i), e.getMessage()));
        }
    }

    public final boolean enableAdminAndSetProfileOwner(int i, int i2, ComponentName componentName) {
        enableAndSetActiveAdmin(i, i2, componentName);
        return setProfileOwner(componentName, i);
    }

    public final void enableAndSetActiveAdmin(int i, int i2, ComponentName componentName) {
        enablePackage(componentName.getPackageName(), i2);
        setActiveAdmin(componentName, true, i);
    }

    public final void enablePackage(String str, int i) {
        try {
            int applicationEnabledSetting = this.mIPackageManager.getApplicationEnabledSetting(str, i);
            if (applicationEnabledSetting == 0 || applicationEnabledSetting == 1) {
                return;
            }
            this.mIPackageManager.setApplicationEnabledSetting(str, 0, 1, i, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error setting application enabled", e);
        }
    }

    public final void setUserSetupComplete(int i) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 1, i);
    }

    public final void startProfileForSetup(int i, String str) throws IllegalStateException {
        Slogf.m20i("DevicePolicyManager", "Starting profile %d as requested by package %s", Integer.valueOf(i), str);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        UserUnlockedBlockingReceiver userUnlockedBlockingReceiver = new UserUnlockedBlockingReceiver(i);
        this.mContext.registerReceiverAsUser(userUnlockedBlockingReceiver, new UserHandle(i), new IntentFilter("android.intent.action.USER_UNLOCKED"), null, null);
        try {
            if (!this.mInjector.getActivityManagerInternal().startProfileEvenWhenDisabled(i)) {
                throw new ServiceSpecificException(5, String.format("Unable to start user %d in background", Integer.valueOf(i)));
            }
            if (!userUnlockedBlockingReceiver.waitForUserUnlocked()) {
                throw new ServiceSpecificException(5, String.format("Timeout whilst waiting for unlock of user %d.", Integer.valueOf(i)));
            }
            logEventDuration(FrameworkStatsLog.f392xcd34d435, elapsedRealtime, str);
        } finally {
            this.mContext.unregisterReceiver(userUnlockedBlockingReceiver);
        }
    }

    public final void maybeMigrateAccount(int i, int i2, Account account, boolean z, String str) {
        UserHandle of = UserHandle.of(i2);
        UserHandle of2 = UserHandle.of(i);
        if (account == null) {
            Slogf.m30d("DevicePolicyManager", "No account to migrate.");
        } else if (of.equals(of2)) {
            Slogf.m14w("DevicePolicyManager", "sourceUser and targetUser are the same, won't migrate account.");
        } else {
            copyAccount(of2, of, account, str);
            if (z) {
                return;
            }
            removeAccount(account);
        }
    }

    public final void copyAccount(UserHandle userHandle, UserHandle userHandle2, Account account, String str) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            if (((Boolean) ((AccountManager) this.mContext.getSystemService(AccountManager.class)).copyAccountToUser(account, userHandle2, userHandle, null, null).getResult(180L, TimeUnit.SECONDS)).booleanValue()) {
                logCopyAccountStatus(1, str);
                logEventDuration(FrameworkStatsLog.f389x36bdcca6, elapsedRealtime, str);
            } else {
                logCopyAccountStatus(2, str);
                Slogf.m26e("DevicePolicyManager", "Failed to copy account to " + userHandle);
            }
        } catch (AuthenticatorException | IOException e) {
            logCopyAccountStatus(4, str);
            Slogf.m25e("DevicePolicyManager", "Exception copying account to " + userHandle, e);
        } catch (OperationCanceledException e2) {
            logCopyAccountStatus(3, str);
            Slogf.m25e("DevicePolicyManager", "Exception copying account to " + userHandle, e2);
        }
    }

    public static void logCopyAccountStatus(int i, String str) {
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.f390xde8506f2).setInt(i).setStrings(new String[]{str}).write();
    }

    public final void removeAccount(Account account) {
        try {
            Bundle result = ((AccountManager) this.mContext.getSystemService(AccountManager.class)).removeAccount(account, null, null, null).getResult();
            if (result.getBoolean("booleanResult", false)) {
                Slogf.m22i("DevicePolicyManager", "Account removed from the primary user.");
            } else {
                final Intent intent = (Intent) result.getParcelable("intent", Intent.class);
                intent.addFlags(268435456);
                Slogf.m22i("DevicePolicyManager", "Starting activity to remove account");
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda27
                    @Override // java.lang.Runnable
                    public final void run() {
                        DevicePolicyManagerService.this.lambda$removeAccount$163(intent);
                    }
                });
            }
        } catch (AuthenticatorException | OperationCanceledException | IOException e) {
            Slogf.m25e("DevicePolicyManager", "Exception removing account from the primary user.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeAccount$163(Intent intent) {
        this.mContext.startActivity(intent);
    }

    public void provisionFullyManagedDevice(FullyManagedDeviceProvisioningParams fullyManagedDeviceProvisioningParams, String str) {
        Objects.requireNonNull(fullyManagedDeviceProvisioningParams, "provisioningParams is null.");
        Objects.requireNonNull(str, "callerPackage is null.");
        ComponentName deviceAdminComponentName = fullyManagedDeviceProvisioningParams.getDeviceAdminComponentName();
        Objects.requireNonNull(deviceAdminComponentName, "admin is null.");
        Objects.requireNonNull(fullyManagedDeviceProvisioningParams.getOwnerName(), "owner name is null.");
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS") || (hasCallingOrSelfPermission("android.permission.PROVISION_DEMO_DEVICE") && fullyManagedDeviceProvisioningParams.isDemoDevice()));
        fullyManagedDeviceProvisioningParams.logParams(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                int checkProvisioningPreconditionSkipPermission = checkProvisioningPreconditionSkipPermission("android.app.action.PROVISION_MANAGED_DEVICE", deviceAdminComponentName.getPackageName(), callerIdentity.getUserId());
                if (checkProvisioningPreconditionSkipPermission != 0) {
                    throw new ServiceSpecificException(1, "Provisioning preconditions failed with result: " + checkProvisioningPreconditionSkipPermission);
                }
                onProvisionFullyManagedDeviceStarted(fullyManagedDeviceProvisioningParams);
                setTimeAndTimezone(fullyManagedDeviceProvisioningParams.getTimeZone(), fullyManagedDeviceProvisioningParams.getLocalTime());
                setLocale(fullyManagedDeviceProvisioningParams.getLocale());
                if (!removeNonRequiredAppsForManagedDevice(0, fullyManagedDeviceProvisioningParams.isLeaveAllSystemAppsEnabled(), deviceAdminComponentName)) {
                    throw new ServiceSpecificException(6, "PackageManager failed to remove non required apps.");
                }
                if (!setActiveAdminAndDeviceOwner(0, deviceAdminComponentName)) {
                    throw new ServiceSpecificException(7, "Failed to set device owner.");
                }
                disallowAddUser();
                setAdminCanGrantSensorsPermissionForUserUnchecked(0, fullyManagedDeviceProvisioningParams.canDeviceOwnerGrantSensorsPermissions());
                setDemoDeviceStateUnchecked(0, fullyManagedDeviceProvisioningParams.isDemoDevice());
                onProvisionFullyManagedDeviceCompleted(fullyManagedDeviceProvisioningParams);
                sendProvisioningCompletedBroadcast(0, "android.app.action.PROVISION_MANAGED_DEVICE", fullyManagedDeviceProvisioningParams.isLeaveAllSystemAppsEnabled());
            } catch (Exception e) {
                DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_ERROR).setStrings(new String[]{str}).write();
                throw e;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setTimeAndTimezone(String str, long j) {
        try {
            AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
            if (str != null) {
                alarmManager.setTimeZone(str);
            }
            if (j > 0) {
                alarmManager.setTime(j);
            }
        } catch (Exception e) {
            Slogf.m25e("DevicePolicyManager", "Alarm manager failed to set the system time/timezone.", e);
        }
    }

    public final void setLocale(Locale locale) {
        if (locale == null || locale.equals(Locale.getDefault())) {
            return;
        }
        try {
            LocalePicker.updateLocale(locale);
        } catch (Exception e) {
            Slogf.m25e("DevicePolicyManager", "Failed to set the system locale.", e);
        }
    }

    public final boolean removeNonRequiredAppsForManagedDevice(int i, boolean z, ComponentName componentName) {
        Set<String> nonRequiredApps;
        if (z) {
            nonRequiredApps = Collections.emptySet();
        } else {
            nonRequiredApps = this.mOverlayPackagesProvider.getNonRequiredApps(componentName, i, "android.app.action.PROVISION_MANAGED_DEVICE");
        }
        removeNonInstalledPackages(nonRequiredApps, i);
        if (nonRequiredApps.isEmpty()) {
            Slogf.m22i("DevicePolicyManager", "No packages to delete on user " + i);
            return true;
        }
        IPackageDeleteObserver nonRequiredPackageDeleteObserver = new NonRequiredPackageDeleteObserver(nonRequiredApps.size());
        for (String str : nonRequiredApps) {
            Slogf.m22i("DevicePolicyManager", "Deleting package [" + str + "] as user " + i);
            this.mContext.getPackageManager().deletePackageAsUser(str, nonRequiredPackageDeleteObserver, 4, i);
        }
        Slogf.m22i("DevicePolicyManager", "Waiting for non required apps to be deleted");
        return nonRequiredPackageDeleteObserver.awaitPackagesDeletion();
    }

    public final void removeNonInstalledPackages(Set<String> set, int i) {
        HashSet hashSet = new HashSet();
        for (String str : set) {
            if (!isPackageInstalledForUser(str, i)) {
                hashSet.add(str);
            }
        }
        set.removeAll(hashSet);
    }

    public final void disallowAddUser() {
        if ((!isHeadlessFlagEnabled() || this.mIsAutomotive) && this.mInjector.userManagerIsHeadlessSystemUserMode()) {
            Slogf.m22i("DevicePolicyManager", "Not setting DISALLOW_ADD_USER on headless system user mode.");
            return;
        }
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            UserHandle userHandle = userInfo.getUserHandle();
            if (!this.mUserManager.hasUserRestriction("no_add_user", userHandle)) {
                this.mUserManager.setUserRestriction("no_add_user", true, userHandle);
            }
        }
    }

    public final boolean setActiveAdminAndDeviceOwner(int i, ComponentName componentName) {
        enableAndSetActiveAdmin(i, i, componentName);
        if (getDeviceOwnerComponent(true) == null) {
            return setDeviceOwner(componentName, i, true);
        }
        return true;
    }

    public static void logEventDuration(int i, long j, String str) {
        DevicePolicyEventLogger.createEvent(i).setTimePeriod(SystemClock.elapsedRealtime() - j).setStrings(new String[]{str}).write();
    }

    public void resetDefaultCrossProfileIntentFilters(final int i) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda25
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$resetDefaultCrossProfileIntentFilters$164(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetDefaultCrossProfileIntentFilters$164(int i) throws Exception {
        try {
            List profiles = this.mUserManager.getProfiles(i);
            int size = profiles.size();
            if (size <= 1) {
                return;
            }
            String managedProvisioningPackage = getManagedProvisioningPackage(this.mContext);
            this.mIPackageManager.clearCrossProfileIntentFilters(i, this.mContext.getOpPackageName());
            this.mIPackageManager.clearCrossProfileIntentFilters(i, managedProvisioningPackage);
            for (int i2 = 0; i2 < size; i2++) {
                UserInfo userInfo = (UserInfo) profiles.get(i2);
                this.mIPackageManager.clearCrossProfileIntentFilters(userInfo.id, this.mContext.getOpPackageName());
                this.mIPackageManager.clearCrossProfileIntentFilters(userInfo.id, managedProvisioningPackage);
                this.mUserManagerInternal.setDefaultCrossProfileIntentFilters(i, userInfo.id);
            }
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Error resetting default cross profile intent filters", e);
        }
    }

    public final void setAdminCanGrantSensorsPermissionForUserUnchecked(int i, boolean z) {
        Slogf.m28d("DevicePolicyManager", "setAdminCanGrantSensorsPermissionForUserUnchecked(%d, %b)", Integer.valueOf(i), Boolean.valueOf(z));
        synchronized (getLockObject()) {
            ActiveAdmin deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            Preconditions.checkState(isDeviceOwner(deviceOrProfileOwnerAdminLocked) && deviceOrProfileOwnerAdminLocked.getUserHandle().getIdentifier() == i, "May only be set on a the user of a device owner.");
            deviceOrProfileOwnerAdminLocked.mAdminCanGrantSensorsPermissions = z;
            this.mPolicyCache.setAdminCanGrantSensorsPermissions(z);
            saveSettingsLocked(i);
        }
    }

    public final void setDemoDeviceStateUnchecked(int i, boolean z) {
        Slogf.m28d("DevicePolicyManager", "setDemoDeviceStateUnchecked(%d, %b)", Integer.valueOf(i), Boolean.valueOf(z));
        if (z) {
            synchronized (getLockObject()) {
                this.mInjector.settingsGlobalPutStringForUser("device_demo_mode", Integer.toString(1), i);
            }
            setUserProvisioningState(3, i);
        }
    }

    public final void updateAdminCanGrantSensorsPermissionCache(int i) {
        ActiveAdmin deviceOrProfileOwnerAdminLocked;
        synchronized (getLockObject()) {
            if (isUserAffiliatedWithDeviceLocked(i)) {
                deviceOrProfileOwnerAdminLocked = getDeviceOwnerAdminLocked();
            } else {
                deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(i);
            }
            this.mPolicyCache.setAdminCanGrantSensorsPermissions(deviceOrProfileOwnerAdminLocked != null ? deviceOrProfileOwnerAdminLocked.mAdminCanGrantSensorsPermissions : false);
        }
    }

    public final void updateNetworkPreferenceForUser(final int i, List<PreferentialNetworkServiceConfig> list) {
        if (isManagedProfile(i) || isDeviceOwnerUserId(i)) {
            final ArrayList arrayList = new ArrayList();
            for (PreferentialNetworkServiceConfig preferentialNetworkServiceConfig : list) {
                ProfileNetworkPreference.Builder builder = new ProfileNetworkPreference.Builder();
                if (preferentialNetworkServiceConfig.isEnabled()) {
                    if (preferentialNetworkServiceConfig.isFallbackToDefaultConnectionAllowed()) {
                        builder.setPreference(1);
                    } else if (preferentialNetworkServiceConfig.shouldBlockNonMatchingNetworks()) {
                        builder.setPreference(3);
                    } else {
                        builder.setPreference(2);
                    }
                    builder.setIncludedUids(preferentialNetworkServiceConfig.getIncludedUids());
                    builder.setExcludedUids(preferentialNetworkServiceConfig.getExcludedUids());
                    builder.setPreferenceEnterpriseId(preferentialNetworkServiceConfig.getNetworkId());
                } else {
                    builder.setPreference(0);
                }
                arrayList.add(builder.build());
            }
            Slogf.m30d("DevicePolicyManager", "updateNetworkPreferenceForUser to " + arrayList);
            this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda79
                public final void runOrThrow() {
                    DevicePolicyManagerService.this.lambda$updateNetworkPreferenceForUser$165(i, arrayList);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateNetworkPreferenceForUser$165(int i, List list) throws Exception {
        this.mInjector.getConnectivityManager().setProfileNetworkPreferences(UserHandle.of(i), list, null, null);
    }

    public boolean canAdminGrantSensorsPermissions() {
        if (this.mHasFeature) {
            return this.mPolicyCache.canAdminGrantSensorsPermissions();
        }
        return false;
    }

    public void setDeviceOwnerType(ComponentName componentName, int i) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        synchronized (getLockObject()) {
            setDeviceOwnerTypeLocked(componentName, i);
        }
    }

    public final void setDeviceOwnerTypeLocked(ComponentName componentName, int i) {
        String packageName = componentName.getPackageName();
        verifyDeviceOwnerTypePreconditionsLocked(componentName);
        boolean isAdminTestOnlyLocked = isAdminTestOnlyLocked(componentName, this.mOwners.getDeviceOwnerUserId());
        Preconditions.checkState(isAdminTestOnlyLocked || !this.mOwners.isDeviceOwnerTypeSetForDeviceOwner(packageName), "Test only admins can only set the device owner type more than once");
        this.mOwners.setDeviceOwnerType(packageName, i, isAdminTestOnlyLocked);
        setGlobalSettingDeviceOwnerType(i);
    }

    public final void setGlobalSettingDeviceOwnerType(final int i) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda12
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setGlobalSettingDeviceOwnerType$166(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setGlobalSettingDeviceOwnerType$166(int i) throws Exception {
        this.mInjector.settingsGlobalPutInt("device_owner_type", i);
    }

    public int getDeviceOwnerType(ComponentName componentName) {
        int deviceOwnerTypeLocked;
        synchronized (getLockObject()) {
            verifyDeviceOwnerTypePreconditionsLocked(componentName);
            deviceOwnerTypeLocked = getDeviceOwnerTypeLocked(componentName.getPackageName());
        }
        return deviceOwnerTypeLocked;
    }

    public final int getDeviceOwnerTypeLocked(String str) {
        return this.mOwners.getDeviceOwnerType(str);
    }

    public final boolean isFinancedDeviceOwner(CallerIdentity callerIdentity) {
        boolean z;
        synchronized (getLockObject()) {
            if (isDeviceOwnerLocked(callerIdentity)) {
                z = true;
                if (getDeviceOwnerTypeLocked(this.mOwners.getDeviceOwnerPackageName()) == 1) {
                }
            }
            z = false;
        }
        return z;
    }

    public final void verifyDeviceOwnerTypePreconditionsLocked(ComponentName componentName) {
        Preconditions.checkState(this.mOwners.hasDeviceOwner(), "there is no device owner");
        Preconditions.checkState(this.mOwners.getDeviceOwnerComponent().equals(componentName), "admin is not the device owner");
    }

    public void setUsbDataSignalingEnabled(String str, boolean z) {
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        Objects.requireNonNull(str, "Admin package name must be provided");
        CallerIdentity callerIdentity = getCallerIdentity(str);
        if (!isPermissionCheckFlagEnabled()) {
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity), "USB data signaling can only be controlled by a device owner or a profile owner on an organization-owned device.");
            Preconditions.checkState(canUsbDataSignalingBeDisabled(), "USB data signaling cannot be disabled.");
        }
        synchronized (getLockObject()) {
            if (isPermissionCheckFlagEnabled()) {
                profileOwnerOrDeviceOwnerLocked = enforcePermissionAndGetEnforcingAdmin(null, "android.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            }
            if (profileOwnerOrDeviceOwnerLocked.mUsbDataSignalingEnabled != z) {
                profileOwnerOrDeviceOwnerLocked.mUsbDataSignalingEnabled = z;
                saveSettingsLocked(callerIdentity.getUserId());
                updateUsbDataSignal();
            }
        }
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_USB_DATA_SIGNALING).setAdmin(str).setBoolean(z).write();
    }

    public final void updateUsbDataSignal() {
        final boolean isUsbDataSignalingEnabledInternalLocked;
        if (canUsbDataSignalingBeDisabled()) {
            synchronized (getLockObject()) {
                isUsbDataSignalingEnabledInternalLocked = isUsbDataSignalingEnabledInternalLocked();
            }
            if (((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda46
                public final Object getOrThrow() {
                    Boolean lambda$updateUsbDataSignal$167;
                    lambda$updateUsbDataSignal$167 = DevicePolicyManagerService.this.lambda$updateUsbDataSignal$167(isUsbDataSignalingEnabledInternalLocked);
                    return lambda$updateUsbDataSignal$167;
                }
            })).booleanValue()) {
                return;
            }
            Slogf.m14w("DevicePolicyManager", "Failed to set usb data signaling state");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updateUsbDataSignal$167(boolean z) throws Exception {
        return Boolean.valueOf(this.mInjector.getUsbManager().enableUsbDataSignal(z));
    }

    public boolean isUsbDataSignalingEnabled(String str) {
        CallerIdentity callerIdentity = getCallerIdentity(str);
        synchronized (getLockObject()) {
            if (!isDefaultDeviceOwner(callerIdentity) && !isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) {
                return isUsbDataSignalingEnabledInternalLocked();
            }
            return getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId()).mUsbDataSignalingEnabled;
        }
    }

    public boolean isUsbDataSignalingEnabledForUser(int i) {
        boolean isUsbDataSignalingEnabledInternalLocked;
        Preconditions.checkCallAuthorization(isSystemUid(getCallerIdentity()));
        synchronized (getLockObject()) {
            isUsbDataSignalingEnabledInternalLocked = isUsbDataSignalingEnabledInternalLocked();
        }
        return isUsbDataSignalingEnabledInternalLocked;
    }

    public final boolean isUsbDataSignalingEnabledInternalLocked() {
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        if (isHeadlessFlagEnabled()) {
            deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
        } else {
            deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
        }
        return deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked == null || deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked.mUsbDataSignalingEnabled;
    }

    public boolean canUsbDataSignalingBeDisabled() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda54
            public final Object getOrThrow() {
                Boolean lambda$canUsbDataSignalingBeDisabled$168;
                lambda$canUsbDataSignalingBeDisabled$168 = DevicePolicyManagerService.this.lambda$canUsbDataSignalingBeDisabled$168();
                return lambda$canUsbDataSignalingBeDisabled$168;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$canUsbDataSignalingBeDisabled$168() throws Exception {
        return Boolean.valueOf(this.mInjector.getUsbManager() != null && this.mInjector.getUsbManager().getUsbHalVersion() >= 13);
    }

    public final void notifyMinimumRequiredWifiSecurityLevelChanged(final int i) {
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda127
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$notifyMinimumRequiredWifiSecurityLevelChanged$169(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyMinimumRequiredWifiSecurityLevelChanged$169(int i) throws Exception {
        this.mInjector.getWifiManager().notifyMinimumRequiredWifiSecurityLevelChanged(i);
    }

    public final void notifyWifiSsidPolicyChanged(final WifiSsidPolicy wifiSsidPolicy) {
        if (wifiSsidPolicy == null) {
            return;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda60
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$notifyWifiSsidPolicyChanged$170(wifiSsidPolicy);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyWifiSsidPolicyChanged$170(WifiSsidPolicy wifiSsidPolicy) throws Exception {
        this.mInjector.getWifiManager().notifyWifiSsidPolicyChanged(wifiSsidPolicy);
    }

    public void setMinimumRequiredWifiSecurityLevel(String str, int i) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        boolean z = false;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(str);
        } else {
            callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity), "Wi-Fi minimum security level can only be controlled by a device owner or a profile owner on an organization-owned device.");
        }
        synchronized (getLockObject()) {
            if (isPermissionCheckFlagEnabled()) {
                profileOwnerOrDeviceOwnerLocked = enforcePermissionAndGetEnforcingAdmin(null, "android.permission.MANAGE_DEVICE_POLICY_WIFI", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            }
            if (profileOwnerOrDeviceOwnerLocked.mWifiMinimumSecurityLevel != i) {
                profileOwnerOrDeviceOwnerLocked.mWifiMinimumSecurityLevel = i;
                saveSettingsLocked(callerIdentity.getUserId());
                z = true;
            }
        }
        if (z) {
            notifyMinimumRequiredWifiSecurityLevelChanged(i);
        }
    }

    public int getMinimumRequiredWifiSecurityLevel() {
        int i;
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        synchronized (getLockObject()) {
            i = 0;
            if (isHeadlessFlagEnabled()) {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
            } else {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
            }
            if (deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked != null) {
                i = deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked.mWifiMinimumSecurityLevel;
            }
        }
        return i;
    }

    public WifiSsidPolicy getWifiSsidPolicy(String str) {
        WifiSsidPolicy wifiSsidPolicy;
        CallerIdentity callerIdentity = getCallerIdentity();
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_WIFI", str, callerIdentity.getUserId());
        } else {
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || canQueryAdminPolicy(callerIdentity), "SSID policy can only be retrieved by a device owner or a profile owner on an organization-owned device or an app with the QUERY_ADMIN_POLICY permission.");
        }
        synchronized (getLockObject()) {
            ActiveAdmin m60xd6d5d1e4 = m60xd6d5d1e4();
            wifiSsidPolicy = m60xd6d5d1e4 != null ? m60xd6d5d1e4.mWifiSsidPolicy : null;
        }
        return wifiSsidPolicy;
    }

    public void setWifiSsidPolicy(String str, WifiSsidPolicy wifiSsidPolicy) {
        CallerIdentity callerIdentity;
        ActiveAdmin profileOwnerOrDeviceOwnerLocked;
        boolean z = false;
        if (isPermissionCheckFlagEnabled()) {
            callerIdentity = getCallerIdentity(str);
        } else {
            callerIdentity = getCallerIdentity();
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity), "SSID denylist can only be controlled by a device owner or a profile owner on an organization-owned device.");
        }
        synchronized (getLockObject()) {
            if (isPermissionCheckFlagEnabled()) {
                profileOwnerOrDeviceOwnerLocked = enforcePermissionAndGetEnforcingAdmin(null, "android.permission.MANAGE_DEVICE_POLICY_WIFI", callerIdentity.getPackageName(), callerIdentity.getUserId()).getActiveAdmin();
            } else {
                profileOwnerOrDeviceOwnerLocked = getProfileOwnerOrDeviceOwnerLocked(callerIdentity.getUserId());
            }
            if (!Objects.equals(wifiSsidPolicy, profileOwnerOrDeviceOwnerLocked.mWifiSsidPolicy)) {
                profileOwnerOrDeviceOwnerLocked.mWifiSsidPolicy = wifiSsidPolicy;
                z = true;
            }
            if (z) {
                saveSettingsLocked(callerIdentity.getUserId());
            }
        }
        if (z) {
            notifyWifiSsidPolicyChanged(wifiSsidPolicy);
        }
    }

    public void setDrawables(final List<DevicePolicyDrawableResource> list) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.UPDATE_DEVICE_MANAGEMENT_RESOURCES"));
        Objects.requireNonNull(list, "drawables must be provided.");
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda147
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setDrawables$172(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDrawables$172(List list) throws Exception {
        if (this.mDeviceManagementResourcesProvider.updateDrawables(list)) {
            sendDrawableUpdatedBroadcast((List) list.stream().map(new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda175
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String drawableId;
                    drawableId = ((DevicePolicyDrawableResource) obj).getDrawableId();
                    return drawableId;
                }
            }).collect(Collectors.toList()));
        }
    }

    public void resetDrawables(final List<String> list) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.UPDATE_DEVICE_MANAGEMENT_RESOURCES"));
        Objects.requireNonNull(list, "drawableIds must be provided.");
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda110
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$resetDrawables$173(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetDrawables$173(List list) throws Exception {
        if (this.mDeviceManagementResourcesProvider.removeDrawables(list)) {
            sendDrawableUpdatedBroadcast(list);
        }
    }

    public ParcelableResource getDrawable(final String str, final String str2, final String str3) {
        return (ParcelableResource) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda165
            public final Object getOrThrow() {
                ParcelableResource lambda$getDrawable$174;
                lambda$getDrawable$174 = DevicePolicyManagerService.this.lambda$getDrawable$174(str, str2, str3);
                return lambda$getDrawable$174;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ParcelableResource lambda$getDrawable$174(String str, String str2, String str3) throws Exception {
        return this.mDeviceManagementResourcesProvider.getDrawable(str, str2, str3);
    }

    public final void sendDrawableUpdatedBroadcast(List<String> list) {
        sendResourceUpdatedBroadcast(1, list);
    }

    public void setStrings(final List<DevicePolicyStringResource> list) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.UPDATE_DEVICE_MANAGEMENT_RESOURCES"));
        Objects.requireNonNull(list, "strings must be provided.");
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda16
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setStrings$176(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setStrings$176(List list) throws Exception {
        if (this.mDeviceManagementResourcesProvider.updateStrings(list)) {
            sendStringsUpdatedBroadcast((List) list.stream().map(new Function() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda192
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String stringId;
                    stringId = ((DevicePolicyStringResource) obj).getStringId();
                    return stringId;
                }
            }).collect(Collectors.toList()));
        }
    }

    public void resetStrings(final List<String> list) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.UPDATE_DEVICE_MANAGEMENT_RESOURCES"));
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda138
            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$resetStrings$177(list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetStrings$177(List list) throws Exception {
        if (this.mDeviceManagementResourcesProvider.removeStrings(list)) {
            sendStringsUpdatedBroadcast(list);
        }
    }

    public ParcelableResource getString(final String str) {
        return (ParcelableResource) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda169
            public final Object getOrThrow() {
                ParcelableResource lambda$getString$178;
                lambda$getString$178 = DevicePolicyManagerService.this.lambda$getString$178(str);
                return lambda$getString$178;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ParcelableResource lambda$getString$178(String str) throws Exception {
        return this.mDeviceManagementResourcesProvider.getString(str);
    }

    public final void sendStringsUpdatedBroadcast(List<String> list) {
        sendResourceUpdatedBroadcast(2, list);
    }

    public final void sendResourceUpdatedBroadcast(int i, List<String> list) {
        Intent intent = new Intent("android.app.action.DEVICE_POLICY_RESOURCE_UPDATED");
        intent.putExtra("android.app.extra.RESOURCE_IDS", (String[]) list.toArray(new IntFunction() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda193
            @Override // java.util.function.IntFunction
            public final Object apply(int i2) {
                String[] lambda$sendResourceUpdatedBroadcast$179;
                lambda$sendResourceUpdatedBroadcast$179 = DevicePolicyManagerService.lambda$sendResourceUpdatedBroadcast$179(i2);
                return lambda$sendResourceUpdatedBroadcast$179;
            }
        }));
        intent.putExtra("android.app.extra.RESOURCE_TYPE", i);
        intent.setFlags(268435456);
        intent.setFlags(1073741824);
        List aliveUsers = this.mUserManager.getAliveUsers();
        for (int i2 = 0; i2 < aliveUsers.size(); i2++) {
            this.mContext.sendBroadcastAsUser(intent, ((UserInfo) aliveUsers.get(i2)).getUserHandle());
        }
    }

    public static /* synthetic */ String[] lambda$sendResourceUpdatedBroadcast$179(int i) {
        return new String[i];
    }

    public final String getUpdatableString(String str, final int i, final Object... objArr) {
        ParcelableResource string = this.mDeviceManagementResourcesProvider.getString(str);
        if (string == null) {
            return ParcelableResource.loadDefaultString(new Supplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda50
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getUpdatableString$180;
                    lambda$getUpdatableString$180 = DevicePolicyManagerService.this.lambda$getUpdatableString$180(i, objArr);
                    return lambda$getUpdatableString$180;
                }
            });
        }
        return string.getString(this.mContext, new Supplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda51
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getUpdatableString$181;
                lambda$getUpdatableString$181 = DevicePolicyManagerService.this.lambda$getUpdatableString$181(i, objArr);
                return lambda$getUpdatableString$181;
            }
        }, objArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getUpdatableString$180(int i, Object[] objArr) {
        return this.mContext.getString(i, objArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getUpdatableString$181(int i, Object[] objArr) {
        return this.mContext.getString(i, objArr);
    }

    public boolean isDpcDownloaded() {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda96
            public final Object getOrThrow() {
                Boolean lambda$isDpcDownloaded$182;
                lambda$isDpcDownloaded$182 = DevicePolicyManagerService.lambda$isDpcDownloaded$182(contentResolver);
                return lambda$isDpcDownloaded$182;
            }
        })).booleanValue();
    }

    public static /* synthetic */ Boolean lambda$isDpcDownloaded$182(ContentResolver contentResolver) throws Exception {
        return Boolean.valueOf(Settings.Secure.getIntForUser(contentResolver, "managed_provisioning_dpc_downloaded", 0, contentResolver.getUserId()) == 1);
    }

    public void setDpcDownloaded(boolean z) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        Injector injector = this.mInjector;
        final int i = z ? 1 : 0;
        injector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda108
            public final Object getOrThrow() {
                Boolean lambda$setDpcDownloaded$183;
                lambda$setDpcDownloaded$183 = DevicePolicyManagerService.this.lambda$setDpcDownloaded$183(i);
                return lambda$setDpcDownloaded$183;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$setDpcDownloaded$183(int i) throws Exception {
        return Boolean.valueOf(Settings.Secure.putInt(this.mContext.getContentResolver(), "managed_provisioning_dpc_downloaded", i));
    }

    /* renamed from: resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
    public void m54xce7fef36() {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_ROLE_HOLDERS"));
        setBypassDevicePolicyManagementRoleQualificationStateInternal(null, false);
    }

    public boolean shouldAllowBypassingDevicePolicyManagementRoleQualification() {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_ROLE_HOLDERS"));
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda61
            public final Object getOrThrow() {
                Boolean m55xec364f56;
                m55xec364f56 = DevicePolicyManagerService.this.m55xec364f56();
                return m55xec364f56;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$shouldAllowBypassingDevicePolicyManagementRoleQualification$184 */
    public /* synthetic */ Boolean m55xec364f56() throws Exception {
        if (lambda$getUserDataUnchecked$2(0).mBypassDevicePolicyManagementRoleQualifications) {
            return Boolean.TRUE;
        }
        return Boolean.valueOf(m53x9c2b31c7());
    }

    /* renamed from: shouldAllowBypassingDevicePolicyManagementRoleQualificationInternal */
    public final boolean m53x9c2b31c7() {
        if (nonTestNonPrecreatedUsersExist()) {
            return false;
        }
        return !hasIncompatibleAccountsOnAnyUser();
    }

    public final boolean hasAccountsOnAnyUser() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (UserInfo userInfo : this.mUserManagerInternal.getUsers(true)) {
                if (((AccountManager) this.mContext.createContextAsUser(UserHandle.of(userInfo.id), 0).getSystemService(AccountManager.class)).getAccounts().length != 0) {
                    return true;
                }
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean hasIncompatibleAccountsOnAnyUser() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (UserInfo userInfo : this.mUserManagerInternal.getUsers(true)) {
                AccountManager accountManager = (AccountManager) this.mContext.createContextAsUser(UserHandle.of(userInfo.id), 0).getSystemService(AccountManager.class);
                if (hasIncompatibleAccounts(accountManager, accountManager.getAccounts())) {
                    return true;
                }
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setBypassDevicePolicyManagementRoleQualificationStateInternal(String str, boolean z) {
        boolean z2;
        DevicePolicyData lambda$getUserDataUnchecked$2 = lambda$getUserDataUnchecked$2(0);
        boolean z3 = true;
        if (lambda$getUserDataUnchecked$2.mBypassDevicePolicyManagementRoleQualifications != z) {
            lambda$getUserDataUnchecked$2.mBypassDevicePolicyManagementRoleQualifications = z;
            z2 = true;
        } else {
            z2 = false;
        }
        if (Objects.equals(str, lambda$getUserDataUnchecked$2.mCurrentRoleHolder)) {
            z3 = z2;
        } else {
            lambda$getUserDataUnchecked$2.mCurrentRoleHolder = str;
        }
        if (z3) {
            synchronized (getLockObject()) {
                saveSettingsLocked(0);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class DevicePolicyManagementRoleObserver implements OnRoleHoldersChangedListener {
        public final Context mContext;
        public final Executor mExecutor;
        public RoleManager mRm;

        public static /* synthetic */ void lambda$onRoleHoldersChanged$0(Boolean bool) {
        }

        public DevicePolicyManagementRoleObserver(Context context) {
            this.mContext = context;
            this.mExecutor = context.getMainExecutor();
            this.mRm = (RoleManager) context.getSystemService(RoleManager.class);
        }

        public void register() {
            this.mRm.addOnRoleHoldersChangedListenerAsUser(this.mExecutor, this, UserHandle.SYSTEM);
        }

        public void onRoleHoldersChanged(String str, UserHandle userHandle) {
            if ("android.app.role.DEVICE_POLICY_MANAGEMENT".equals(str)) {
                String roleHolder = getRoleHolder();
                if (isDefaultRoleHolder(roleHolder)) {
                    Slogf.m22i("DevicePolicyManager", "onRoleHoldersChanged: Default role holder is set, returning early");
                } else if (roleHolder == null) {
                    Slogf.m22i("DevicePolicyManager", "onRoleHoldersChanged: New role holder is null, returning early");
                } else if (DevicePolicyManagerService.this.m53x9c2b31c7()) {
                    Slogf.m14w("DevicePolicyManager", "onRoleHoldersChanged: Updating current role holder to " + roleHolder);
                    DevicePolicyManagerService.this.setBypassDevicePolicyManagementRoleQualificationStateInternal(roleHolder, true);
                } else if (roleHolder.equals(DevicePolicyManagerService.this.lambda$getUserDataUnchecked$2(0).mCurrentRoleHolder)) {
                } else {
                    Slogf.m14w("DevicePolicyManager", "onRoleHoldersChanged: You can't set a different role holder, role is getting revoked from " + roleHolder);
                    DevicePolicyManagerService.this.setBypassDevicePolicyManagementRoleQualificationStateInternal(null, false);
                    this.mRm.removeRoleHolderAsUser("android.app.role.DEVICE_POLICY_MANAGEMENT", roleHolder, 0, userHandle, this.mExecutor, new Consumer() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyManagementRoleObserver$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            DevicePolicyManagerService.DevicePolicyManagementRoleObserver.lambda$onRoleHoldersChanged$0((Boolean) obj);
                        }
                    });
                }
            }
        }

        public final String getRoleHolder() {
            return DevicePolicyManagerService.this.getDevicePolicyManagementRoleHolderPackageName(this.mContext);
        }

        public final boolean isDefaultRoleHolder(String str) {
            String defaultRoleHolderPackageName = getDefaultRoleHolderPackageName();
            if (str == null || defaultRoleHolderPackageName == null || !defaultRoleHolderPackageName.equals(str)) {
                return false;
            }
            return hasSigningCertificate(str, getDefaultRoleHolderPackageSignature());
        }

        public final boolean hasSigningCertificate(String str, String str2) {
            if (str != null && str2 != null) {
                try {
                    return DevicePolicyManagerService.this.mInjector.getPackageManager().hasSigningCertificate(str, new Signature(str2).toByteArray(), 1);
                } catch (IllegalArgumentException e) {
                    Slogf.m13w("DevicePolicyManager", "Cannot parse signing certificate: " + str2, e);
                }
            }
            return false;
        }

        public final String getDefaultRoleHolderPackageName() {
            String[] defaultRoleHolderPackageNameAndSignature = getDefaultRoleHolderPackageNameAndSignature();
            if (defaultRoleHolderPackageNameAndSignature == null) {
                return null;
            }
            return defaultRoleHolderPackageNameAndSignature[0];
        }

        public final String getDefaultRoleHolderPackageSignature() {
            String[] defaultRoleHolderPackageNameAndSignature = getDefaultRoleHolderPackageNameAndSignature();
            if (defaultRoleHolderPackageNameAndSignature == null || defaultRoleHolderPackageNameAndSignature.length < 2) {
                return null;
            }
            return defaultRoleHolderPackageNameAndSignature[1];
        }

        public final String[] getDefaultRoleHolderPackageNameAndSignature() {
            String string = this.mContext.getString(17039421);
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            if (string.contains(XmlUtils.STRING_ARRAY_SEPARATOR)) {
                return string.split(XmlUtils.STRING_ARRAY_SEPARATOR);
            }
            return new String[]{string};
        }
    }

    public List<UserHandle> getPolicyManagedProfiles(UserHandle userHandle) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        final int identifier = userHandle.getIdentifier();
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda31
            public final Object getOrThrow() {
                List lambda$getPolicyManagedProfiles$185;
                lambda$getPolicyManagedProfiles$185 = DevicePolicyManagerService.this.lambda$getPolicyManagedProfiles$185(identifier);
                return lambda$getPolicyManagedProfiles$185;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$getPolicyManagedProfiles$185(int i) throws Exception {
        List profiles = this.mUserManager.getProfiles(i);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < profiles.size(); i2++) {
            UserInfo userInfo = (UserInfo) profiles.get(i2);
            if (userInfo.isManagedProfile() && hasProfileOwner(userInfo.id)) {
                arrayList.add(new UserHandle(userInfo.id));
            }
        }
        return arrayList;
    }

    public final EnforcingAdmin enforcePermissionAndGetEnforcingAdmin(ComponentName componentName, String str, String str2, int i) {
        enforcePermission(str, str2, i);
        return getEnforcingAdminForCaller(componentName, str2);
    }

    public final EnforcingAdmin enforceCanQueryAndGetEnforcingAdmin(ComponentName componentName, String str, String str2, int i) {
        enforceCanQuery(str, str2, i);
        return getEnforcingAdminForCaller(componentName, str2);
    }

    public final void enforcePermission(String str, String str2, int i) throws SecurityException {
        if (hasPermission(str, str2, i)) {
            return;
        }
        throw new SecurityException("Caller does not have the required permissions for this user. Permissions required: {" + str + ", " + CROSS_USER_PERMISSIONS.get(str) + "(if calling cross-user)}");
    }

    public final void enforceCanQuery(String str, String str2, int i) throws SecurityException {
        if (hasPermission("android.permission.QUERY_ADMIN_POLICY", str2)) {
            return;
        }
        enforcePermission(str, str2, i);
    }

    public final boolean hasPermission(String str, String str2, int i) {
        CallerIdentity callerIdentity = getCallerIdentity(str2);
        boolean hasPermission = hasPermission(str, str2);
        return hasPermission && ((callerIdentity.getUserId() != i) & hasPermission ? hasPermission(CROSS_USER_PERMISSIONS.get(str), str2) : true);
    }

    public final boolean hasPermission(String str, String str2) {
        Objects.requireNonNull(str2, "callerPackageName is null");
        if (str == null) {
            return true;
        }
        CallerIdentity callerIdentity = getCallerIdentity(str2);
        if (this.mContext.checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        if (isDefaultDeviceOwner(callerIdentity)) {
            return DPC_PERMISSIONS.get(0).contains(str);
        }
        if (isFinancedDeviceOwner(callerIdentity)) {
            return DPC_PERMISSIONS.get(1).contains(str);
        }
        if (isProfileOwnerOfOrganizationOwnedDevice(callerIdentity)) {
            return DPC_PERMISSIONS.get(2).contains(str);
        }
        if (isProfileOwnerOnUser0(callerIdentity)) {
            return DPC_PERMISSIONS.get(3).contains(str);
        }
        if (isProfileOwner(callerIdentity)) {
            return DPC_PERMISSIONS.get(4).contains(str);
        }
        if (isCallerDevicePolicyManagementRoleHolder(callerIdentity)) {
            return anyDpcHasPermission(str, this.mContext.getUserId());
        }
        HashMap<String, String> hashMap = DELEGATE_SCOPES;
        if (hashMap.containsKey(str)) {
            return isCallerDelegate(callerIdentity, hashMap.get(str));
        }
        HashMap<String, Integer> hashMap2 = ACTIVE_ADMIN_POLICIES;
        if (hashMap2.containsKey(str)) {
            try {
                if (hashMap2.get(str) != null) {
                    return getActiveAdminForCallerLocked(null, hashMap2.get(str).intValue(), false) != null;
                }
                return isCallerActiveAdminOrDelegate(callerIdentity, null);
            } catch (SecurityException unused) {
            }
        }
        return false;
    }

    public final boolean anyDpcHasPermission(String str, int i) {
        if (this.mOwners.isDefaultDeviceOwnerUserId(i)) {
            return DPC_PERMISSIONS.get(0).contains(str);
        }
        if (this.mOwners.isFinancedDeviceOwnerUserId(i)) {
            return DPC_PERMISSIONS.get(1).contains(str);
        }
        if (this.mOwners.isProfileOwnerOfOrganizationOwnedDevice(i)) {
            return DPC_PERMISSIONS.get(2).contains(str);
        }
        if (i == 0 && this.mOwners.hasProfileOwner(0)) {
            return DPC_PERMISSIONS.get(3).contains(str);
        }
        if (this.mOwners.hasProfileOwner(i)) {
            return DPC_PERMISSIONS.get(4).contains(str);
        }
        return false;
    }

    public final EnforcingAdmin getEnforcingAdminForCaller(ComponentName componentName, String str) {
        ActiveAdmin deviceOrProfileOwnerAdminLocked;
        CallerIdentity callerIdentity = getCallerIdentity(componentName, str);
        int userId = callerIdentity.getUserId();
        if (isDeviceOwner(callerIdentity) || isProfileOwner(callerIdentity) || isCallerDelegate(callerIdentity)) {
            synchronized (getLockObject()) {
                if (componentName != null) {
                    deviceOrProfileOwnerAdminLocked = getActiveAdminUncheckedLocked(componentName, userId);
                } else {
                    deviceOrProfileOwnerAdminLocked = getDeviceOrProfileOwnerAdminLocked(userId);
                    componentName = deviceOrProfileOwnerAdminLocked.info.getComponent();
                }
            }
            return EnforcingAdmin.createEnterpriseEnforcingAdmin(componentName, userId, deviceOrProfileOwnerAdminLocked);
        }
        ActiveAdmin activeAdminForCaller = getActiveAdminForCaller(componentName, callerIdentity);
        if (activeAdminForCaller != null) {
            return EnforcingAdmin.createDeviceAdminEnforcingAdmin(componentName, userId, activeAdminForCaller);
        }
        if (activeAdminForCaller == null) {
            activeAdminForCaller = lambda$getUserDataUnchecked$2(userId).createOrGetPermissionBasedAdmin(userId);
        }
        return EnforcingAdmin.createEnforcingAdmin(callerIdentity.getPackageName(), userId, activeAdminForCaller);
    }

    public final int getAffectedUser(boolean z) {
        int userHandleGetCallingUserId = this.mInjector.userHandleGetCallingUserId();
        return z ? getProfileParentId(userHandleGetCallingUserId) : userHandleGetCallingUserId;
    }

    public final boolean isPermissionCheckFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "enable_permission_based_access", false);
    }

    public static boolean isKeepProfilesRunningFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "enable_keep_profiles_running", false);
    }

    public static boolean isWorkProfileTelephonyFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "enable_work_profile_telephony", false);
    }

    public void setOverrideKeepProfilesRunning(boolean z) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        this.mKeepProfilesRunning = z;
        Slog.i("DevicePolicyManager", "Keep profiles running overridden to: " + z);
    }

    public void setMtePolicy(int i, String str) {
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        Preconditions.checkArgument(Set.of(0, 2, 1).contains(Integer.valueOf(i)), "Provided mode is not one of the allowed values.");
        Injector injector = this.mInjector;
        if (!injector.systemPropertiesGetBoolean("ro.arm64.memtag.bootctl_device_policy_manager", injector.systemPropertiesGetBoolean("ro.arm64.memtag.bootctl_settings_toggle", false))) {
            throw new UnsupportedOperationException("device does not support MTE");
        }
        CallerIdentity callerIdentity = getCallerIdentity(str);
        if (i == 2) {
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity));
        }
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_MTE", callerIdentity.getPackageName(), -1);
        } else {
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity));
        }
        synchronized (getLockObject()) {
            if (isHeadlessFlagEnabled()) {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
            } else {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
            }
            if (deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked != null) {
                if (i == 1) {
                    this.mInjector.systemPropertiesSet("arm64.memtag.bootctl", "memtag");
                } else if (i == 2) {
                    this.mInjector.systemPropertiesSet("arm64.memtag.bootctl", "memtag-off");
                }
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked.mtePolicy = i;
                saveSettingsLocked(callerIdentity.getUserId());
                DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_MTE_POLICY).setInt(i).setAdmin(callerIdentity.getPackageName()).write();
            }
        }
    }

    public int getMtePolicy(String str) {
        ActiveAdmin deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked;
        int i;
        CallerIdentity callerIdentity = getCallerIdentity(str);
        if (isPermissionCheckFlagEnabled()) {
            enforcePermission("android.permission.MANAGE_DEVICE_POLICY_MTE", callerIdentity.getPackageName(), -1);
        } else {
            Preconditions.checkCallAuthorization(isDefaultDeviceOwner(callerIdentity) || isProfileOwnerOfOrganizationOwnedDevice(callerIdentity) || isSystemUid(callerIdentity));
        }
        synchronized (getLockObject()) {
            if (isHeadlessFlagEnabled()) {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked();
            } else {
                deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked = getDeviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked(0);
            }
            i = deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked != null ? deviceOwnerOrProfileOwnerOfOrganizationOwnedDeviceLocked.mtePolicy : 0;
        }
        return i;
    }

    public final boolean isHeadlessFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "headless", true);
    }

    public ManagedSubscriptionsPolicy getManagedSubscriptionsPolicy() {
        ManagedSubscriptionsPolicy managedSubscriptionsPolicy;
        if (isWorkProfileTelephonyFlagEnabled()) {
            synchronized (getLockObject()) {
                ActiveAdmin profileOwnerOfOrganizationOwnedDeviceLocked = getProfileOwnerOfOrganizationOwnedDeviceLocked();
                if (profileOwnerOfOrganizationOwnedDeviceLocked != null && (managedSubscriptionsPolicy = profileOwnerOfOrganizationOwnedDeviceLocked.mManagedSubscriptionsPolicy) != null) {
                    return managedSubscriptionsPolicy;
                }
            }
        }
        return new ManagedSubscriptionsPolicy(0);
    }

    public void setManagedSubscriptionsPolicy(ManagedSubscriptionsPolicy managedSubscriptionsPolicy) {
        if (!isWorkProfileTelephonyFlagEnabled()) {
            throw new UnsupportedOperationException("This api is not enabled");
        }
        CallerIdentity callerIdentity = getCallerIdentity();
        Preconditions.checkCallAuthorization(isProfileOwnerOfOrganizationOwnedDevice(callerIdentity), "This policy can only be set by a profile owner on an organization-owned device.");
        synchronized (getLockObject()) {
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(callerIdentity.getUserId());
            boolean z = false;
            if (hasUserSetupCompleted(0) && !isAdminTestOnlyLocked(profileOwnerLocked.info.getComponent(), callerIdentity.getUserId())) {
                throw new IllegalStateException("Not allowed to apply this policy after setup");
            }
            if (!Objects.equals(managedSubscriptionsPolicy, profileOwnerLocked.mManagedSubscriptionsPolicy)) {
                profileOwnerLocked.mManagedSubscriptionsPolicy = managedSubscriptionsPolicy;
                z = true;
            }
            if (z) {
                saveSettingsLocked(callerIdentity.getUserId());
                applyManagedSubscriptionsPolicyIfRequired();
                if (getManagedSubscriptionsPolicy().getPolicyType() == 1) {
                    long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
                    try {
                        int profileParentId = getProfileParentId(callerIdentity.getUserId());
                        installOemDefaultDialerAndSmsApp(callerIdentity.getUserId());
                        updateTelephonyCrossProfileIntentFilters(profileParentId, callerIdentity.getUserId(), true);
                    } finally {
                        this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    }
                }
            }
        }
    }

    public final void installOemDefaultDialerAndSmsApp(int i) {
        try {
            String defaultRoleHolderPackageName = getDefaultRoleHolderPackageName(17039395);
            String defaultRoleHolderPackageName2 = getDefaultRoleHolderPackageName(17039396);
            if (defaultRoleHolderPackageName != null) {
                this.mIPackageManager.installExistingPackageAsUser(defaultRoleHolderPackageName, i, 4194304, 1, (List) null);
            } else {
                Slogf.m14w("DevicePolicyManager", "Couldn't install dialer app, dialer app package is null");
            }
            if (defaultRoleHolderPackageName2 != null) {
                this.mIPackageManager.installExistingPackageAsUser(defaultRoleHolderPackageName2, i, 4194304, 1, (List) null);
            } else {
                Slogf.m14w("DevicePolicyManager", "Couldn't install sms app, sms app package is null");
            }
            updateDialerAndSmsManagedShortcutsOverrideCache(defaultRoleHolderPackageName, defaultRoleHolderPackageName2);
        } catch (RemoteException e) {
            Slogf.wtf("DevicePolicyManager", "Failed to install dialer/sms app", e);
        }
    }

    public final void updateDialerAndSmsManagedShortcutsOverrideCache(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        if (str != null) {
            arrayList.add(str);
        }
        if (str2 != null) {
            arrayList.add(str2);
        }
        this.mPolicyCache.setLauncherShortcutOverrides(arrayList);
    }

    public final void registerListenerToAssignSubscriptionsToUser(final int i) {
        synchronized (this.mSubscriptionsChangedListenerLock) {
            if (this.mSubscriptionsChangedListener != null) {
                return;
            }
            final SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
            this.mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener(this.mHandler.getLooper()) { // from class: com.android.server.devicepolicy.DevicePolicyManagerService.8
                @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
                public void onSubscriptionsChanged() {
                    int[] activeSubscriptionIdList;
                    long binderClearCallingIdentity = DevicePolicyManagerService.this.mInjector.binderClearCallingIdentity();
                    try {
                        for (int i2 : subscriptionManager.getActiveSubscriptionIdList(false)) {
                            UserHandle subscriptionUserHandle = subscriptionManager.getSubscriptionUserHandle(i2);
                            if (subscriptionUserHandle == null || subscriptionUserHandle.getIdentifier() != i) {
                                subscriptionManager.setSubscriptionUserHandle(i2, UserHandle.of(i));
                            }
                        }
                    } finally {
                        DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
                    }
                }
            };
            long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
            subscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionsChangedListener.getHandlerExecutor(), this.mSubscriptionsChangedListener);
            this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
        }
    }

    public final void unregisterOnSubscriptionsChangedListener() {
        synchronized (this.mSubscriptionsChangedListenerLock) {
            if (this.mSubscriptionsChangedListener != null) {
                ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).removeOnSubscriptionsChangedListener(this.mSubscriptionsChangedListener);
                this.mSubscriptionsChangedListener = null;
            }
        }
    }

    public DevicePolicyState getDevicePolicyState() {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        Injector injector = this.mInjector;
        final DevicePolicyEngine devicePolicyEngine = this.mDevicePolicyEngine;
        Objects.requireNonNull(devicePolicyEngine);
        return (DevicePolicyState) injector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda78
            public final Object getOrThrow() {
                return DevicePolicyEngine.this.getDevicePolicyState();
            }
        });
    }

    public boolean triggerDevicePolicyEngineMigration(final boolean z) {
        Preconditions.checkCallAuthorization(hasCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS"));
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda58
            public final Object getOrThrow() {
                Boolean lambda$triggerDevicePolicyEngineMigration$186;
                lambda$triggerDevicePolicyEngineMigration$186 = DevicePolicyManagerService.this.lambda$triggerDevicePolicyEngineMigration$186(z);
                return lambda$triggerDevicePolicyEngineMigration$186;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$triggerDevicePolicyEngineMigration$186(boolean z) throws Exception {
        if (!(z && !hasNonTestOnlyActiveAdmins()) && !shouldMigrateToDevicePolicyEngine()) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(migratePoliciesToDevicePolicyEngine());
    }

    public final boolean hasNonTestOnlyActiveAdmins() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda190
            public final Object getOrThrow() {
                Boolean lambda$hasNonTestOnlyActiveAdmins$187;
                lambda$hasNonTestOnlyActiveAdmins$187 = DevicePolicyManagerService.this.lambda$hasNonTestOnlyActiveAdmins$187();
                return lambda$hasNonTestOnlyActiveAdmins$187;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hasNonTestOnlyActiveAdmins$187() throws Exception {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            List<ComponentName> activeAdmins = getActiveAdmins(userInfo.id);
            if (activeAdmins != null) {
                for (ComponentName componentName : activeAdmins) {
                    if (!isAdminTestOnlyLocked(componentName, userInfo.id)) {
                        return Boolean.TRUE;
                    }
                }
                continue;
            }
        }
        return Boolean.FALSE;
    }

    public final boolean shouldMigrateToDevicePolicyEngine() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda85
            public final Object getOrThrow() {
                Boolean lambda$shouldMigrateToDevicePolicyEngine$188;
                lambda$shouldMigrateToDevicePolicyEngine$188 = DevicePolicyManagerService.this.lambda$shouldMigrateToDevicePolicyEngine$188();
                return lambda$shouldMigrateToDevicePolicyEngine$188;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$shouldMigrateToDevicePolicyEngine$188() throws Exception {
        if (!isDevicePolicyEngineFlagEnabled()) {
            return Boolean.FALSE;
        }
        if (this.mOwners.isMigratedToPolicyEngine()) {
            return Boolean.FALSE;
        }
        boolean z = false;
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            List<ComponentName> activeAdmins = getActiveAdmins(userInfo.id);
            if (activeAdmins != null) {
                for (ComponentName componentName : activeAdmins) {
                    if (isProfileOwner(componentName, userInfo.id) || isDeviceOwner(componentName, userInfo.id)) {
                        if (!this.mInjector.isChangeEnabled(260560985L, componentName.getPackageName(), userInfo.id)) {
                            return Boolean.FALSE;
                        }
                        z = true;
                    }
                }
                continue;
            }
        }
        return Boolean.valueOf(z);
    }

    public final boolean migratePoliciesToDevicePolicyEngine() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda120
            public final Object getOrThrow() {
                Boolean lambda$migratePoliciesToDevicePolicyEngine$189;
                lambda$migratePoliciesToDevicePolicyEngine$189 = DevicePolicyManagerService.this.lambda$migratePoliciesToDevicePolicyEngine$189();
                return lambda$migratePoliciesToDevicePolicyEngine$189;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$migratePoliciesToDevicePolicyEngine$189() throws Exception {
        try {
            Slogf.m22i("DevicePolicyManager", "Started device policies migration to the device policy engine.");
            migrateAutoTimezonePolicy();
            migratePermissionGrantStatePolicies();
            this.mOwners.markMigrationToPolicyEngine();
            return Boolean.TRUE;
        } catch (Exception e) {
            this.mDevicePolicyEngine.clearAllPolicies();
            Slogf.m23e("DevicePolicyManager", e, "Error occurred during device policy migration, will reattempt on the next system server restart.", new Object[0]);
            return Boolean.FALSE;
        }
    }

    public final void migrateAutoTimezonePolicy() {
        Slogf.m22i("DevicePolicyManager", "Skipping Migration of AUTO_TIMEZONE policy to device policy engine,as no way to identify if the value was set by the admin or the user.");
    }

    public final void migratePermissionGrantStatePolicies() {
        int i;
        Slogf.m22i("DevicePolicyManager", "Migrating PERMISSION_GRANT policy to device policy engine.");
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            ActiveAdmin mostProbableDPCAdminForLocalPolicy = getMostProbableDPCAdminForLocalPolicy(userInfo.id);
            if (mostProbableDPCAdminForLocalPolicy == null) {
                Slogf.m22i("DevicePolicyManager", "No admin found that can set permission grant state on user " + userInfo.id);
            } else {
                for (PackageInfo packageInfo : getInstalledPackagesOnUser(userInfo.id)) {
                    String[] strArr = packageInfo.requestedPermissions;
                    if (strArr != null) {
                        for (String str : strArr) {
                            if (isRuntimePermission(str)) {
                                try {
                                    i = getPermissionGrantStateForUser(packageInfo.packageName, str, new CallerIdentity(this.mInjector.binderGetCallingUid(), mostProbableDPCAdminForLocalPolicy.info.getComponent().getPackageName(), mostProbableDPCAdminForLocalPolicy.info.getComponent()), userInfo.id);
                                } catch (RemoteException e) {
                                    Slogf.m23e("DevicePolicyManager", e, "Error retrieving permission grant state for %s and %s", packageInfo.packageName, str);
                                    i = 0;
                                }
                                if (i != 0) {
                                    this.mDevicePolicyEngine.setLocalPolicy(PolicyDefinition.PERMISSION_GRANT(packageInfo.packageName, str), EnforcingAdmin.createEnterpriseEnforcingAdmin(mostProbableDPCAdminForLocalPolicy.info.getComponent(), mostProbableDPCAdminForLocalPolicy.getUserHandle().getIdentifier()), new IntegerPolicyValue(i), userInfo.id, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final List<PackageInfo> getInstalledPackagesOnUser(final int i) {
        return (List) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda179
            public final Object getOrThrow() {
                List lambda$getInstalledPackagesOnUser$190;
                lambda$getInstalledPackagesOnUser$190 = DevicePolicyManagerService.this.lambda$getInstalledPackagesOnUser$190(i);
                return lambda$getInstalledPackagesOnUser$190;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$getInstalledPackagesOnUser$190(int i) throws Exception {
        return this.mContext.getPackageManager().getInstalledPackagesAsUser(PackageManager.PackageInfoFlags.of(4096L), i);
    }

    public final ActiveAdmin getMostProbableDPCAdminForLocalPolicy(int i) {
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwnerLocked = getDeviceOwnerLocked(i);
            if (deviceOwnerLocked != null) {
                return deviceOwnerLocked;
            }
            ActiveAdmin profileOwnerLocked = getProfileOwnerLocked(i);
            if (profileOwnerLocked != null) {
                return profileOwnerLocked;
            }
            int[] profileIds = this.mUserManager.getProfileIds(i, false);
            for (int i2 : profileIds) {
                if (i2 != i && isProfileOwnerOfOrganizationOwnedDevice(i2)) {
                    return getProfileOwnerAdminLocked(i2);
                }
            }
            for (int i3 : profileIds) {
                if (i3 != i && isManagedProfile(i3)) {
                    return getProfileOwnerAdminLocked(i3);
                }
            }
            ActiveAdmin deviceOwnerAdminLocked = getDeviceOwnerAdminLocked();
            if (deviceOwnerAdminLocked != null) {
                return deviceOwnerAdminLocked;
            }
            for (UserInfo userInfo : this.mUserManager.getUsers()) {
                ActiveAdmin profileOwnerLocked2 = getProfileOwnerLocked(userInfo.id);
                if (profileOwnerLocked2 != null) {
                    return profileOwnerLocked2;
                }
            }
            return null;
        }
    }

    public final boolean useDevicePolicyEngine(CallerIdentity callerIdentity, String str) {
        return isDevicePolicyEngineEnabled();
    }

    public final boolean isDevicePolicyEngineEnabled() {
        return isDevicePolicyEngineFlagEnabled() && isPermissionCheckFlagEnabled();
    }

    public final boolean isDevicePolicyEngineFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "enable_device_policy_engine", false);
    }

    public final boolean hasDPCsNotSupportingCoexistence() {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda178
            public final Object getOrThrow() {
                Boolean lambda$hasDPCsNotSupportingCoexistence$191;
                lambda$hasDPCsNotSupportingCoexistence$191 = DevicePolicyManagerService.this.lambda$hasDPCsNotSupportingCoexistence$191();
                return lambda$hasDPCsNotSupportingCoexistence$191;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$hasDPCsNotSupportingCoexistence$191() throws Exception {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            List<ComponentName> activeAdmins = getActiveAdmins(userInfo.id);
            if (activeAdmins != null) {
                for (ComponentName componentName : activeAdmins) {
                    if (isProfileOwner(componentName, userInfo.id) || isDeviceOwner(componentName, userInfo.id)) {
                        if (!this.mInjector.isChangeEnabled(260560985L, componentName.getPackageName(), userInfo.id)) {
                            return Boolean.TRUE;
                        }
                    }
                }
                continue;
            }
        }
        return Boolean.FALSE;
    }

    public final boolean isCallerActiveAdminOrDelegate(final CallerIdentity callerIdentity, final String str) {
        return ((Boolean) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda130
            public final Object getOrThrow() {
                Boolean lambda$isCallerActiveAdminOrDelegate$192;
                lambda$isCallerActiveAdminOrDelegate$192 = DevicePolicyManagerService.this.lambda$isCallerActiveAdminOrDelegate$192(callerIdentity, str);
                return lambda$isCallerActiveAdminOrDelegate$192;
            }
        })).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isCallerActiveAdminOrDelegate$192(CallerIdentity callerIdentity, String str) throws Exception {
        List<ComponentName> activeAdmins = getActiveAdmins(callerIdentity.getUserId());
        if (activeAdmins != null) {
            for (ComponentName componentName : activeAdmins) {
                if (componentName.getPackageName().equals(callerIdentity.getPackageName())) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.valueOf(str != null && isCallerDelegate(callerIdentity, str));
    }

    public final ActiveAdmin getActiveAdminForCaller(ComponentName componentName, final CallerIdentity callerIdentity) {
        synchronized (getLockObject()) {
            if (componentName != null) {
                return getActiveAdminUncheckedLocked(componentName, callerIdentity.getUserId());
            }
            return (ActiveAdmin) this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyManagerService$$ExternalSyntheticLambda122
                public final Object getOrThrow() {
                    ActiveAdmin lambda$getActiveAdminForCaller$193;
                    lambda$getActiveAdminForCaller$193 = DevicePolicyManagerService.this.lambda$getActiveAdminForCaller$193(callerIdentity);
                    return lambda$getActiveAdminForCaller$193;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ActiveAdmin lambda$getActiveAdminForCaller$193(CallerIdentity callerIdentity) throws Exception {
        List<ComponentName> activeAdmins = getActiveAdmins(callerIdentity.getUserId());
        if (activeAdmins != null) {
            for (ComponentName componentName : activeAdmins) {
                if (componentName.getPackageName().equals(callerIdentity.getPackageName())) {
                    return getActiveAdminUncheckedLocked(componentName, callerIdentity.getUserId());
                }
            }
            return null;
        }
        return null;
    }

    public final boolean canAddActiveAdminIfPolicyEngineEnabled(String str, int i) {
        if (isDevicePolicyEngineFlagEnabled() && !hasDPCsNotSupportingCoexistence()) {
            if (this.mInjector.isChangeEnabled(260560985L, str, i)) {
                return this.mDevicePolicyEngine.canAdminAddPolicies(str, i);
            }
            return !this.mDevicePolicyEngine.hasActivePolicies();
        }
        return true;
    }
}
