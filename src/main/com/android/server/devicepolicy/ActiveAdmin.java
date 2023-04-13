package com.android.server.devicepolicy;

import android.app.admin.DeviceAdminInfo;
import android.app.admin.FactoryResetProtectionPolicy;
import android.app.admin.ManagedSubscriptionsPolicy;
import android.app.admin.PackagePolicy;
import android.app.admin.PasswordPolicy;
import android.app.admin.PreferentialNetworkServiceConfig;
import android.app.admin.WifiSsidPolicy;
import android.graphics.Color;
import android.net.wifi.WifiSsid;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.UserRestrictionsUtils;
import com.android.server.utils.Slogf;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ActiveAdmin {
    public static final int DEF_ORGANIZATION_COLOR = Color.parseColor("#00796B");
    public final Set<String> accountTypesWithManagementDisabled;
    public List<String> crossProfileWidgetProviders;
    public final Set<String> defaultEnabledRestrictionsAlreadySet;
    public boolean disableBluetoothContactSharing;
    public boolean disableCallerId;
    public boolean disableCamera;
    public boolean disableContactsSearch;
    public boolean disableScreenCapture;
    public int disabledKeyguardFeatures;
    public boolean encryptionRequested;
    public String endUserSessionMessage;
    public boolean forceEphemeralUsers;
    public String globalProxyExclusionList;
    public String globalProxySpec;
    public DeviceAdminInfo info;
    public boolean isLogoutEnabled;
    public boolean isNetworkLoggingEnabled;
    public final boolean isParent;
    public final boolean isPermissionBased;
    public List<String> keepUninstalledPackages;
    public long lastNetworkLoggingNotificationTimeMs;
    public CharSequence longSupportMessage;
    public boolean mAdminCanGrantSensorsPermissions;
    public boolean mAlwaysOnVpnLockdown;
    public String mAlwaysOnVpnPackage;
    public boolean mCommonCriteriaMode;
    public PackagePolicy mCredentialManagerPolicy;
    public List<String> mCrossProfileCalendarPackages;
    public List<String> mCrossProfilePackages;
    public String mDialerPackage;
    public String mEnrollmentSpecificId;
    public FactoryResetProtectionPolicy mFactoryResetProtectionPolicy;
    public PackagePolicy mManagedProfileCallerIdAccess;
    public PackagePolicy mManagedProfileContactsAccess;
    public ManagedSubscriptionsPolicy mManagedSubscriptionsPolicy;
    public int mNearbyAppStreamingPolicy;
    public int mNearbyNotificationStreamingPolicy;
    public String mOrganizationId;
    public int mPasswordComplexity;
    public PasswordPolicy mPasswordPolicy;
    public List<PreferentialNetworkServiceConfig> mPreferentialNetworkServiceConfigs;
    public long mProfileMaximumTimeOffMillis;
    public long mProfileOffDeadline;
    public String mSmsPackage;
    public boolean mSuspendPersonalApps;
    public boolean mUsbDataSignalingEnabled;
    public int mWifiMinimumSecurityLevel;
    public WifiSsidPolicy mWifiSsidPolicy;
    public int maximumFailedPasswordsForWipe;
    public long maximumTimeToUnlock;
    public List<String> meteredDisabledPackages;
    public int mtePolicy;
    public int numNetworkLoggingNotifications;
    public int organizationColor;
    public String organizationName;
    public ActiveAdmin parentAdmin;
    public long passwordExpirationDate;
    public long passwordExpirationTimeout;
    public int passwordHistoryLength;
    public List<String> permittedAccessiblityServices;
    public List<String> permittedInputMethods;
    public List<String> permittedNotificationListeners;
    public List<String> protectedPackages;
    public boolean requireAutoTime;
    public CharSequence shortSupportMessage;
    public boolean specifiesGlobalProxy;
    public String startUserSessionMessage;
    public long strongAuthUnlockTimeout;
    public List<String> suspendedPackages;
    public boolean testOnlyAdmin;
    public ArrayMap<String, TrustAgentInfo> trustAgentInfos;
    public final int userId;
    public Bundle userRestrictions;

    /* loaded from: classes.dex */
    public static class TrustAgentInfo {
        public PersistableBundle options;

        public TrustAgentInfo(PersistableBundle persistableBundle) {
            this.options = persistableBundle;
        }
    }

    public ActiveAdmin(DeviceAdminInfo deviceAdminInfo, boolean z) {
        this.passwordHistoryLength = 0;
        this.mPasswordPolicy = new PasswordPolicy();
        this.mPasswordComplexity = 0;
        this.mNearbyNotificationStreamingPolicy = 3;
        this.mNearbyAppStreamingPolicy = 3;
        this.mFactoryResetProtectionPolicy = null;
        this.maximumTimeToUnlock = 0L;
        this.strongAuthUnlockTimeout = 0L;
        this.maximumFailedPasswordsForWipe = 0;
        this.passwordExpirationTimeout = 0L;
        this.passwordExpirationDate = 0L;
        this.disabledKeyguardFeatures = 0;
        this.encryptionRequested = false;
        this.testOnlyAdmin = false;
        this.disableCamera = false;
        this.disableCallerId = false;
        this.disableContactsSearch = false;
        this.disableBluetoothContactSharing = true;
        this.disableScreenCapture = false;
        this.requireAutoTime = false;
        this.forceEphemeralUsers = false;
        this.isNetworkLoggingEnabled = false;
        this.isLogoutEnabled = false;
        this.numNetworkLoggingNotifications = 0;
        this.lastNetworkLoggingNotificationTimeMs = 0L;
        this.mtePolicy = 0;
        this.accountTypesWithManagementDisabled = new ArraySet();
        this.specifiesGlobalProxy = false;
        this.globalProxySpec = null;
        this.globalProxyExclusionList = null;
        this.trustAgentInfos = new ArrayMap<>();
        this.defaultEnabledRestrictionsAlreadySet = new ArraySet();
        this.shortSupportMessage = null;
        this.longSupportMessage = null;
        this.organizationColor = DEF_ORGANIZATION_COLOR;
        this.organizationName = null;
        this.startUserSessionMessage = null;
        this.endUserSessionMessage = null;
        this.mCrossProfileCalendarPackages = Collections.emptyList();
        this.mCrossProfilePackages = Collections.emptyList();
        this.mSuspendPersonalApps = false;
        this.mProfileMaximumTimeOffMillis = 0L;
        this.mProfileOffDeadline = 0L;
        this.mManagedProfileCallerIdAccess = null;
        this.mManagedProfileContactsAccess = null;
        this.mCredentialManagerPolicy = null;
        this.mPreferentialNetworkServiceConfigs = List.of(PreferentialNetworkServiceConfig.DEFAULT);
        this.mUsbDataSignalingEnabled = true;
        this.mWifiMinimumSecurityLevel = 0;
        this.userId = -1;
        this.info = deviceAdminInfo;
        this.isParent = z;
        this.isPermissionBased = false;
    }

    public ActiveAdmin(int i, boolean z) {
        this.passwordHistoryLength = 0;
        this.mPasswordPolicy = new PasswordPolicy();
        this.mPasswordComplexity = 0;
        this.mNearbyNotificationStreamingPolicy = 3;
        this.mNearbyAppStreamingPolicy = 3;
        this.mFactoryResetProtectionPolicy = null;
        this.maximumTimeToUnlock = 0L;
        this.strongAuthUnlockTimeout = 0L;
        this.maximumFailedPasswordsForWipe = 0;
        this.passwordExpirationTimeout = 0L;
        this.passwordExpirationDate = 0L;
        this.disabledKeyguardFeatures = 0;
        this.encryptionRequested = false;
        this.testOnlyAdmin = false;
        this.disableCamera = false;
        this.disableCallerId = false;
        this.disableContactsSearch = false;
        this.disableBluetoothContactSharing = true;
        this.disableScreenCapture = false;
        this.requireAutoTime = false;
        this.forceEphemeralUsers = false;
        this.isNetworkLoggingEnabled = false;
        this.isLogoutEnabled = false;
        this.numNetworkLoggingNotifications = 0;
        this.lastNetworkLoggingNotificationTimeMs = 0L;
        this.mtePolicy = 0;
        this.accountTypesWithManagementDisabled = new ArraySet();
        this.specifiesGlobalProxy = false;
        this.globalProxySpec = null;
        this.globalProxyExclusionList = null;
        this.trustAgentInfos = new ArrayMap<>();
        this.defaultEnabledRestrictionsAlreadySet = new ArraySet();
        this.shortSupportMessage = null;
        this.longSupportMessage = null;
        this.organizationColor = DEF_ORGANIZATION_COLOR;
        this.organizationName = null;
        this.startUserSessionMessage = null;
        this.endUserSessionMessage = null;
        this.mCrossProfileCalendarPackages = Collections.emptyList();
        this.mCrossProfilePackages = Collections.emptyList();
        this.mSuspendPersonalApps = false;
        this.mProfileMaximumTimeOffMillis = 0L;
        this.mProfileOffDeadline = 0L;
        this.mManagedProfileCallerIdAccess = null;
        this.mManagedProfileContactsAccess = null;
        this.mCredentialManagerPolicy = null;
        this.mPreferentialNetworkServiceConfigs = List.of(PreferentialNetworkServiceConfig.DEFAULT);
        this.mUsbDataSignalingEnabled = true;
        this.mWifiMinimumSecurityLevel = 0;
        if (!z) {
            throw new IllegalArgumentException("Can only pass true for permissionBased admin");
        }
        this.userId = i;
        this.isPermissionBased = z;
        this.isParent = false;
        this.info = null;
    }

    public ActiveAdmin getParentActiveAdmin() {
        Preconditions.checkState(!this.isParent);
        if (this.parentAdmin == null) {
            this.parentAdmin = new ActiveAdmin(this.info, true);
        }
        return this.parentAdmin;
    }

    public boolean hasParentActiveAdmin() {
        return this.parentAdmin != null;
    }

    public int getUid() {
        if (this.isPermissionBased) {
            return -1;
        }
        return this.info.getActivityInfo().applicationInfo.uid;
    }

    public UserHandle getUserHandle() {
        if (this.isPermissionBased) {
            return UserHandle.of(this.userId);
        }
        return UserHandle.of(UserHandle.getUserId(this.info.getActivityInfo().applicationInfo.uid));
    }

    public void writeToXml(TypedXmlSerializer typedXmlSerializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (this.info != null) {
            typedXmlSerializer.startTag((String) null, "policies");
            this.info.writePoliciesToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "policies");
        }
        int i = this.mPasswordPolicy.quality;
        if (i != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "password-quality", i);
            int i2 = this.mPasswordPolicy.length;
            if (i2 != 0) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-length", i2);
            }
            int i3 = this.mPasswordPolicy.upperCase;
            if (i3 != 0) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-uppercase", i3);
            }
            int i4 = this.mPasswordPolicy.lowerCase;
            if (i4 != 0) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-lowercase", i4);
            }
            int i5 = this.mPasswordPolicy.letters;
            if (i5 != 1) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-letters", i5);
            }
            int i6 = this.mPasswordPolicy.numeric;
            if (i6 != 1) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-numeric", i6);
            }
            int i7 = this.mPasswordPolicy.symbols;
            if (i7 != 1) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-symbols", i7);
            }
            int i8 = this.mPasswordPolicy.nonLetter;
            if (i8 > 0) {
                writeAttributeValueToXml(typedXmlSerializer, "min-password-nonletter", i8);
            }
        }
        int i9 = this.passwordHistoryLength;
        if (i9 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "password-history-length", i9);
        }
        long j = this.maximumTimeToUnlock;
        if (j != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "max-time-to-unlock", j);
        }
        long j2 = this.strongAuthUnlockTimeout;
        if (j2 != 259200000) {
            writeAttributeValueToXml(typedXmlSerializer, "strong-auth-unlock-timeout", j2);
        }
        int i10 = this.maximumFailedPasswordsForWipe;
        if (i10 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "max-failed-password-wipe", i10);
        }
        boolean z = this.specifiesGlobalProxy;
        if (z) {
            writeAttributeValueToXml(typedXmlSerializer, "specifies-global-proxy", z);
            String str = this.globalProxySpec;
            if (str != null) {
                writeAttributeValueToXml(typedXmlSerializer, "global-proxy-spec", str);
            }
            String str2 = this.globalProxyExclusionList;
            if (str2 != null) {
                writeAttributeValueToXml(typedXmlSerializer, "global-proxy-exclusion-list", str2);
            }
        }
        long j3 = this.passwordExpirationTimeout;
        if (j3 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "password-expiration-timeout", j3);
        }
        long j4 = this.passwordExpirationDate;
        if (j4 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "password-expiration-date", j4);
        }
        boolean z2 = this.encryptionRequested;
        if (z2) {
            writeAttributeValueToXml(typedXmlSerializer, "encryption-requested", z2);
        }
        boolean z3 = this.testOnlyAdmin;
        if (z3) {
            writeAttributeValueToXml(typedXmlSerializer, "test-only-admin", z3);
        }
        boolean z4 = this.disableCamera;
        if (z4) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-camera", z4);
        }
        boolean z5 = this.disableCallerId;
        if (z5) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-caller-id", z5);
        }
        boolean z6 = this.disableContactsSearch;
        if (z6) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-contacts-search", z6);
        }
        boolean z7 = this.disableBluetoothContactSharing;
        if (!z7) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-bt-contacts-sharing", z7);
        }
        boolean z8 = this.disableScreenCapture;
        if (z8) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-screen-capture", z8);
        }
        boolean z9 = this.requireAutoTime;
        if (z9) {
            writeAttributeValueToXml(typedXmlSerializer, "require_auto_time", z9);
        }
        boolean z10 = this.forceEphemeralUsers;
        if (z10) {
            writeAttributeValueToXml(typedXmlSerializer, "force_ephemeral_users", z10);
        }
        if (this.isNetworkLoggingEnabled) {
            typedXmlSerializer.startTag((String) null, "is_network_logging_enabled");
            typedXmlSerializer.attributeBoolean((String) null, "value", this.isNetworkLoggingEnabled);
            typedXmlSerializer.attributeInt((String) null, "num-notifications", this.numNetworkLoggingNotifications);
            typedXmlSerializer.attributeLong((String) null, "last-notification", this.lastNetworkLoggingNotificationTimeMs);
            typedXmlSerializer.endTag((String) null, "is_network_logging_enabled");
        }
        int i11 = this.disabledKeyguardFeatures;
        if (i11 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "disable-keyguard-features", i11);
        }
        if (!this.accountTypesWithManagementDisabled.isEmpty()) {
            writeAttributeValuesToXml(typedXmlSerializer, "disable-account-management", "account-type", this.accountTypesWithManagementDisabled);
        }
        if (!this.trustAgentInfos.isEmpty()) {
            Set<Map.Entry<String, TrustAgentInfo>> entrySet = this.trustAgentInfos.entrySet();
            typedXmlSerializer.startTag((String) null, "manage-trust-agent-features");
            for (Map.Entry<String, TrustAgentInfo> entry : entrySet) {
                TrustAgentInfo value = entry.getValue();
                typedXmlSerializer.startTag((String) null, "component");
                typedXmlSerializer.attribute((String) null, "value", entry.getKey());
                if (value.options != null) {
                    typedXmlSerializer.startTag((String) null, "trust-agent-component-options");
                    try {
                        value.options.saveToXml(typedXmlSerializer);
                    } catch (XmlPullParserException e) {
                        Slogf.m23e("DevicePolicyManager", e, "Failed to save TrustAgent options", new Object[0]);
                    }
                    typedXmlSerializer.endTag((String) null, "trust-agent-component-options");
                }
                typedXmlSerializer.endTag((String) null, "component");
            }
            typedXmlSerializer.endTag((String) null, "manage-trust-agent-features");
        }
        List<String> list = this.crossProfileWidgetProviders;
        if (list != null && !list.isEmpty()) {
            writeAttributeValuesToXml(typedXmlSerializer, "cross-profile-widget-providers", "provider", this.crossProfileWidgetProviders);
        }
        writePackageListToXml(typedXmlSerializer, "permitted-accessiblity-services", this.permittedAccessiblityServices);
        writePackageListToXml(typedXmlSerializer, "permitted-imes", this.permittedInputMethods);
        writePackageListToXml(typedXmlSerializer, "permitted-notification-listeners", this.permittedNotificationListeners);
        writePackageListToXml(typedXmlSerializer, "keep-uninstalled-packages", this.keepUninstalledPackages);
        writePackageListToXml(typedXmlSerializer, "metered_data_disabled_packages", this.meteredDisabledPackages);
        writePackageListToXml(typedXmlSerializer, "protected_packages", this.protectedPackages);
        writePackageListToXml(typedXmlSerializer, "suspended-packages", this.suspendedPackages);
        if (hasUserRestrictions()) {
            UserRestrictionsUtils.writeRestrictions(typedXmlSerializer, this.userRestrictions, "user-restrictions");
        }
        if (!this.defaultEnabledRestrictionsAlreadySet.isEmpty()) {
            writeAttributeValuesToXml(typedXmlSerializer, "default-enabled-user-restrictions", "restriction", this.defaultEnabledRestrictionsAlreadySet);
        }
        if (!TextUtils.isEmpty(this.shortSupportMessage)) {
            writeTextToXml(typedXmlSerializer, "short-support-message", this.shortSupportMessage.toString());
        }
        if (!TextUtils.isEmpty(this.longSupportMessage)) {
            writeTextToXml(typedXmlSerializer, "long-support-message", this.longSupportMessage.toString());
        }
        if (this.parentAdmin != null) {
            typedXmlSerializer.startTag((String) null, "parent-admin");
            this.parentAdmin.writeToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "parent-admin");
        }
        int i12 = this.organizationColor;
        if (i12 != DEF_ORGANIZATION_COLOR) {
            writeAttributeValueToXml(typedXmlSerializer, "organization-color", i12);
        }
        String str3 = this.organizationName;
        if (str3 != null) {
            writeTextToXml(typedXmlSerializer, "organization-name", str3);
        }
        boolean z11 = this.isLogoutEnabled;
        if (z11) {
            writeAttributeValueToXml(typedXmlSerializer, "is_logout_enabled", z11);
        }
        String str4 = this.startUserSessionMessage;
        if (str4 != null) {
            writeTextToXml(typedXmlSerializer, "start_user_session_message", str4);
        }
        String str5 = this.endUserSessionMessage;
        if (str5 != null) {
            writeTextToXml(typedXmlSerializer, "end_user_session_message", str5);
        }
        List<String> list2 = this.mCrossProfileCalendarPackages;
        if (list2 == null) {
            typedXmlSerializer.startTag((String) null, "cross-profile-calendar-packages-null");
            typedXmlSerializer.endTag((String) null, "cross-profile-calendar-packages-null");
        } else {
            writePackageListToXml(typedXmlSerializer, "cross-profile-calendar-packages", list2);
        }
        writePackageListToXml(typedXmlSerializer, "cross-profile-packages", this.mCrossProfilePackages);
        if (this.mFactoryResetProtectionPolicy != null) {
            typedXmlSerializer.startTag((String) null, "factory_reset_protection_policy");
            this.mFactoryResetProtectionPolicy.writeToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "factory_reset_protection_policy");
        }
        boolean z12 = this.mSuspendPersonalApps;
        if (z12) {
            writeAttributeValueToXml(typedXmlSerializer, "suspend-personal-apps", z12);
        }
        long j5 = this.mProfileMaximumTimeOffMillis;
        if (j5 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "profile-max-time-off", j5);
        }
        if (this.mProfileMaximumTimeOffMillis != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "profile-off-deadline", this.mProfileOffDeadline);
        }
        if (!TextUtils.isEmpty(this.mAlwaysOnVpnPackage)) {
            writeAttributeValueToXml(typedXmlSerializer, "vpn-package", this.mAlwaysOnVpnPackage);
        }
        boolean z13 = this.mAlwaysOnVpnLockdown;
        if (z13) {
            writeAttributeValueToXml(typedXmlSerializer, "vpn-lockdown", z13);
        }
        boolean z14 = this.mCommonCriteriaMode;
        if (z14) {
            writeAttributeValueToXml(typedXmlSerializer, "common-criteria-mode", z14);
        }
        int i13 = this.mPasswordComplexity;
        if (i13 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "password-complexity", i13);
        }
        int i14 = this.mNearbyNotificationStreamingPolicy;
        if (i14 != 3) {
            writeAttributeValueToXml(typedXmlSerializer, "nearby-notification-streaming-policy", i14);
        }
        int i15 = this.mNearbyAppStreamingPolicy;
        if (i15 != 3) {
            writeAttributeValueToXml(typedXmlSerializer, "nearby-app-streaming-policy", i15);
        }
        if (!TextUtils.isEmpty(this.mOrganizationId)) {
            writeTextToXml(typedXmlSerializer, "organization-id", this.mOrganizationId);
        }
        if (!TextUtils.isEmpty(this.mEnrollmentSpecificId)) {
            writeTextToXml(typedXmlSerializer, "enrollment-specific-id", this.mEnrollmentSpecificId);
        }
        writeAttributeValueToXml(typedXmlSerializer, "admin-can-grant-sensors-permissions", this.mAdminCanGrantSensorsPermissions);
        boolean z15 = this.mUsbDataSignalingEnabled;
        if (!z15) {
            writeAttributeValueToXml(typedXmlSerializer, "usb-data-signaling", z15);
        }
        int i16 = this.mWifiMinimumSecurityLevel;
        if (i16 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "wifi-min-security", i16);
        }
        WifiSsidPolicy wifiSsidPolicy = this.mWifiSsidPolicy;
        if (wifiSsidPolicy != null) {
            List<String> ssidsToStrings = ssidsToStrings(wifiSsidPolicy.getSsids());
            if (this.mWifiSsidPolicy.getPolicyType() == 0) {
                writeAttributeValuesToXml(typedXmlSerializer, "ssid-allowlist", "ssid", ssidsToStrings);
            } else if (this.mWifiSsidPolicy.getPolicyType() == 1) {
                writeAttributeValuesToXml(typedXmlSerializer, "ssid-denylist", "ssid", ssidsToStrings);
            }
        }
        if (!this.mPreferentialNetworkServiceConfigs.isEmpty()) {
            typedXmlSerializer.startTag((String) null, "preferential_network_service_configs");
            for (PreferentialNetworkServiceConfig preferentialNetworkServiceConfig : this.mPreferentialNetworkServiceConfigs) {
                preferentialNetworkServiceConfig.writeToXml(typedXmlSerializer);
            }
            typedXmlSerializer.endTag((String) null, "preferential_network_service_configs");
        }
        int i17 = this.mtePolicy;
        if (i17 != 0) {
            writeAttributeValueToXml(typedXmlSerializer, "mte-policy", i17);
        }
        writePackagePolicy(typedXmlSerializer, "caller-id-policy", this.mManagedProfileCallerIdAccess);
        writePackagePolicy(typedXmlSerializer, "contacts-policy", this.mManagedProfileContactsAccess);
        writePackagePolicy(typedXmlSerializer, "credential-manager-policy", this.mCredentialManagerPolicy);
        if (this.mManagedSubscriptionsPolicy != null) {
            typedXmlSerializer.startTag((String) null, "managed_subscriptions_policy");
            this.mManagedSubscriptionsPolicy.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "managed_subscriptions_policy");
        }
        if (!TextUtils.isEmpty(this.mDialerPackage)) {
            writeAttributeValueToXml(typedXmlSerializer, "dialer_package", this.mDialerPackage);
        }
        if (TextUtils.isEmpty(this.mSmsPackage)) {
            return;
        }
        writeAttributeValueToXml(typedXmlSerializer, "sms_package", this.mSmsPackage);
    }

    public final void writePackagePolicy(TypedXmlSerializer typedXmlSerializer, String str, PackagePolicy packagePolicy) throws IOException {
        if (packagePolicy == null) {
            return;
        }
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attributeInt((String) null, "package-policy-type", packagePolicy.getPolicyType());
        writePackageListToXml(typedXmlSerializer, "package-policy-packages", new ArrayList(packagePolicy.getPackageNames()));
        typedXmlSerializer.endTag((String) null, str);
    }

    public final List<String> ssidsToStrings(Set<WifiSsid> set) {
        return (List) set.stream().map(new Function() { // from class: com.android.server.devicepolicy.ActiveAdmin$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$ssidsToStrings$0;
                lambda$ssidsToStrings$0 = ActiveAdmin.lambda$ssidsToStrings$0((WifiSsid) obj);
                return lambda$ssidsToStrings$0;
            }
        }).collect(Collectors.toList());
    }

    public static /* synthetic */ String lambda$ssidsToStrings$0(WifiSsid wifiSsid) {
        return new String(wifiSsid.getBytes(), StandardCharsets.UTF_8);
    }

    public void writeTextToXml(TypedXmlSerializer typedXmlSerializer, String str, String str2) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.text(str2);
        typedXmlSerializer.endTag((String) null, str);
    }

    public void writePackageListToXml(TypedXmlSerializer typedXmlSerializer, String str, List<String> list) throws IllegalArgumentException, IllegalStateException, IOException {
        if (list == null) {
            return;
        }
        writeAttributeValuesToXml(typedXmlSerializer, str, "item", list);
    }

    public void writeAttributeValueToXml(TypedXmlSerializer typedXmlSerializer, String str, String str2) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attribute((String) null, "value", str2);
        typedXmlSerializer.endTag((String) null, str);
    }

    public void writeAttributeValueToXml(TypedXmlSerializer typedXmlSerializer, String str, int i) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attributeInt((String) null, "value", i);
        typedXmlSerializer.endTag((String) null, str);
    }

    public void writeAttributeValueToXml(TypedXmlSerializer typedXmlSerializer, String str, long j) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attributeLong((String) null, "value", j);
        typedXmlSerializer.endTag((String) null, str);
    }

    public void writeAttributeValueToXml(TypedXmlSerializer typedXmlSerializer, String str, boolean z) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attributeBoolean((String) null, "value", z);
        typedXmlSerializer.endTag((String) null, str);
    }

    public void writeAttributeValuesToXml(TypedXmlSerializer typedXmlSerializer, String str, String str2, Collection<String> collection) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        for (String str3 : collection) {
            typedXmlSerializer.startTag((String) null, str2);
            typedXmlSerializer.attribute((String) null, "value", str3);
            typedXmlSerializer.endTag((String) null, str2);
        }
        typedXmlSerializer.endTag((String) null, str);
    }

    public void readFromXml(TypedXmlPullParser typedXmlPullParser, boolean z) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if ("policies".equals(name)) {
                    if (z) {
                        Slogf.m30d("DevicePolicyManager", "Overriding device admin policies from XML.");
                        this.info.readPoliciesFromXml(typedXmlPullParser);
                    }
                } else if ("password-quality".equals(name)) {
                    this.mPasswordPolicy.quality = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-length".equals(name)) {
                    this.mPasswordPolicy.length = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("password-history-length".equals(name)) {
                    this.passwordHistoryLength = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-uppercase".equals(name)) {
                    this.mPasswordPolicy.upperCase = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-lowercase".equals(name)) {
                    this.mPasswordPolicy.lowerCase = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-letters".equals(name)) {
                    this.mPasswordPolicy.letters = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-numeric".equals(name)) {
                    this.mPasswordPolicy.numeric = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-symbols".equals(name)) {
                    this.mPasswordPolicy.symbols = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("min-password-nonletter".equals(name)) {
                    this.mPasswordPolicy.nonLetter = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("max-time-to-unlock".equals(name)) {
                    this.maximumTimeToUnlock = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("strong-auth-unlock-timeout".equals(name)) {
                    this.strongAuthUnlockTimeout = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("max-failed-password-wipe".equals(name)) {
                    this.maximumFailedPasswordsForWipe = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("specifies-global-proxy".equals(name)) {
                    this.specifiesGlobalProxy = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("global-proxy-spec".equals(name)) {
                    this.globalProxySpec = typedXmlPullParser.getAttributeValue((String) null, "value");
                } else if ("global-proxy-exclusion-list".equals(name)) {
                    this.globalProxyExclusionList = typedXmlPullParser.getAttributeValue((String) null, "value");
                } else if ("password-expiration-timeout".equals(name)) {
                    this.passwordExpirationTimeout = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("password-expiration-date".equals(name)) {
                    this.passwordExpirationDate = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("encryption-requested".equals(name)) {
                    this.encryptionRequested = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("test-only-admin".equals(name)) {
                    this.testOnlyAdmin = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("disable-camera".equals(name)) {
                    this.disableCamera = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("disable-caller-id".equals(name)) {
                    this.disableCallerId = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("disable-contacts-search".equals(name)) {
                    this.disableContactsSearch = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("disable-bt-contacts-sharing".equals(name)) {
                    this.disableBluetoothContactSharing = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("disable-screen-capture".equals(name)) {
                    this.disableScreenCapture = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("require_auto_time".equals(name)) {
                    this.requireAutoTime = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("force_ephemeral_users".equals(name)) {
                    this.forceEphemeralUsers = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("is_network_logging_enabled".equals(name)) {
                    this.isNetworkLoggingEnabled = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                    this.lastNetworkLoggingNotificationTimeMs = typedXmlPullParser.getAttributeLong((String) null, "last-notification");
                    this.numNetworkLoggingNotifications = typedXmlPullParser.getAttributeInt((String) null, "num-notifications");
                } else if ("disable-keyguard-features".equals(name)) {
                    this.disabledKeyguardFeatures = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("disable-account-management".equals(name)) {
                    readAttributeValues(typedXmlPullParser, "account-type", this.accountTypesWithManagementDisabled);
                } else if ("manage-trust-agent-features".equals(name)) {
                    this.trustAgentInfos = getAllTrustAgentInfos(typedXmlPullParser, name);
                } else if ("cross-profile-widget-providers".equals(name)) {
                    ArrayList arrayList = new ArrayList();
                    this.crossProfileWidgetProviders = arrayList;
                    readAttributeValues(typedXmlPullParser, "provider", arrayList);
                } else if ("permitted-accessiblity-services".equals(name)) {
                    this.permittedAccessiblityServices = readPackageList(typedXmlPullParser, name);
                } else if ("permitted-imes".equals(name)) {
                    this.permittedInputMethods = readPackageList(typedXmlPullParser, name);
                } else if ("permitted-notification-listeners".equals(name)) {
                    this.permittedNotificationListeners = readPackageList(typedXmlPullParser, name);
                } else if ("keep-uninstalled-packages".equals(name)) {
                    this.keepUninstalledPackages = readPackageList(typedXmlPullParser, name);
                } else if ("metered_data_disabled_packages".equals(name)) {
                    this.meteredDisabledPackages = readPackageList(typedXmlPullParser, name);
                } else if ("protected_packages".equals(name)) {
                    this.protectedPackages = readPackageList(typedXmlPullParser, name);
                } else if ("suspended-packages".equals(name)) {
                    this.suspendedPackages = readPackageList(typedXmlPullParser, name);
                } else if ("user-restrictions".equals(name)) {
                    this.userRestrictions = UserRestrictionsUtils.readRestrictions(typedXmlPullParser);
                } else if ("default-enabled-user-restrictions".equals(name)) {
                    readAttributeValues(typedXmlPullParser, "restriction", this.defaultEnabledRestrictionsAlreadySet);
                } else if ("short-support-message".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.shortSupportMessage = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing text when loading short support message");
                    }
                } else if ("long-support-message".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.longSupportMessage = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing text when loading long support message");
                    }
                } else if ("parent-admin".equals(name)) {
                    Preconditions.checkState(!this.isParent);
                    ActiveAdmin activeAdmin = new ActiveAdmin(this.info, true);
                    this.parentAdmin = activeAdmin;
                    activeAdmin.readFromXml(typedXmlPullParser, z);
                } else if ("organization-color".equals(name)) {
                    this.organizationColor = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("organization-name".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.organizationName = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing text when loading organization name");
                    }
                } else if ("is_logout_enabled".equals(name)) {
                    this.isLogoutEnabled = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("start_user_session_message".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.startUserSessionMessage = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing text when loading start session message");
                    }
                } else if ("end_user_session_message".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.endUserSessionMessage = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing text when loading end session message");
                    }
                } else if ("cross-profile-calendar-packages".equals(name)) {
                    this.mCrossProfileCalendarPackages = readPackageList(typedXmlPullParser, name);
                } else if ("cross-profile-calendar-packages-null".equals(name)) {
                    this.mCrossProfileCalendarPackages = null;
                } else if ("cross-profile-packages".equals(name)) {
                    this.mCrossProfilePackages = readPackageList(typedXmlPullParser, name);
                } else if ("factory_reset_protection_policy".equals(name)) {
                    this.mFactoryResetProtectionPolicy = FactoryResetProtectionPolicy.readFromXml(typedXmlPullParser);
                } else if ("suspend-personal-apps".equals(name)) {
                    this.mSuspendPersonalApps = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("profile-max-time-off".equals(name)) {
                    this.mProfileMaximumTimeOffMillis = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("profile-off-deadline".equals(name)) {
                    this.mProfileOffDeadline = typedXmlPullParser.getAttributeLong((String) null, "value");
                } else if ("vpn-package".equals(name)) {
                    this.mAlwaysOnVpnPackage = typedXmlPullParser.getAttributeValue((String) null, "value");
                } else if ("vpn-lockdown".equals(name)) {
                    this.mAlwaysOnVpnLockdown = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("preferential-network-service-enabled".equals(name)) {
                    boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                    if (attributeBoolean) {
                        PreferentialNetworkServiceConfig.Builder builder = new PreferentialNetworkServiceConfig.Builder();
                        builder.setEnabled(attributeBoolean);
                        builder.setNetworkId(1);
                        this.mPreferentialNetworkServiceConfigs = List.of(builder.build());
                    }
                } else if ("common-criteria-mode".equals(name)) {
                    this.mCommonCriteriaMode = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("password-complexity".equals(name)) {
                    this.mPasswordComplexity = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("nearby-notification-streaming-policy".equals(name)) {
                    this.mNearbyNotificationStreamingPolicy = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("nearby-app-streaming-policy".equals(name)) {
                    this.mNearbyAppStreamingPolicy = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("organization-id".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.mOrganizationId = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing Organization ID.");
                    }
                } else if ("enrollment-specific-id".equals(name)) {
                    if (typedXmlPullParser.next() == 4) {
                        this.mEnrollmentSpecificId = typedXmlPullParser.getText();
                    } else {
                        Slogf.m14w("DevicePolicyManager", "Missing Enrollment-specific ID.");
                    }
                } else if ("admin-can-grant-sensors-permissions".equals(name)) {
                    this.mAdminCanGrantSensorsPermissions = typedXmlPullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("usb-data-signaling".equals(name)) {
                    this.mUsbDataSignalingEnabled = typedXmlPullParser.getAttributeBoolean((String) null, "value", true);
                } else if ("wifi-min-security".equals(name)) {
                    this.mWifiMinimumSecurityLevel = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("ssid-allowlist".equals(name)) {
                    this.mWifiSsidPolicy = new WifiSsidPolicy(0, new ArraySet(readWifiSsids(typedXmlPullParser, "ssid")));
                } else if ("ssid-denylist".equals(name)) {
                    this.mWifiSsidPolicy = new WifiSsidPolicy(1, new ArraySet(readWifiSsids(typedXmlPullParser, "ssid")));
                } else if ("preferential_network_service_configs".equals(name)) {
                    List<PreferentialNetworkServiceConfig> preferentialNetworkServiceConfigs = getPreferentialNetworkServiceConfigs(typedXmlPullParser, name);
                    if (!preferentialNetworkServiceConfigs.isEmpty()) {
                        this.mPreferentialNetworkServiceConfigs = preferentialNetworkServiceConfigs;
                    }
                } else if ("mte-policy".equals(name)) {
                    this.mtePolicy = typedXmlPullParser.getAttributeInt((String) null, "value");
                } else if ("caller-id-policy".equals(name)) {
                    this.mManagedProfileCallerIdAccess = readPackagePolicy(typedXmlPullParser);
                } else if ("contacts-policy".equals(name)) {
                    this.mManagedProfileContactsAccess = readPackagePolicy(typedXmlPullParser);
                } else if ("managed_subscriptions_policy".equals(name)) {
                    this.mManagedSubscriptionsPolicy = ManagedSubscriptionsPolicy.readFromXml(typedXmlPullParser);
                } else if ("credential-manager-policy".equals(name)) {
                    this.mCredentialManagerPolicy = readPackagePolicy(typedXmlPullParser);
                } else if ("dialer_package".equals(name)) {
                    this.mDialerPackage = typedXmlPullParser.getAttributeValue((String) null, "value");
                } else if ("sms_package".equals(name)) {
                    this.mSmsPackage = typedXmlPullParser.getAttributeValue((String) null, "value");
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown admin tag: %s", name);
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final PackagePolicy readPackagePolicy(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        return new PackagePolicy(typedXmlPullParser.getAttributeInt((String) null, "package-policy-type"), new ArraySet(readPackageList(typedXmlPullParser, "package-policy-packages")));
    }

    public final List<WifiSsid> readWifiSsids(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
        ArrayList arrayList = new ArrayList();
        readAttributeValues(typedXmlPullParser, str, arrayList);
        return (List) arrayList.stream().map(new Function() { // from class: com.android.server.devicepolicy.ActiveAdmin$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                WifiSsid lambda$readWifiSsids$1;
                lambda$readWifiSsids$1 = ActiveAdmin.lambda$readWifiSsids$1((String) obj);
                return lambda$readWifiSsids$1;
            }
        }).collect(Collectors.toList());
    }

    public static /* synthetic */ WifiSsid lambda$readWifiSsids$1(String str) {
        return WifiSsid.fromBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    public final List<String> readPackageList(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
        ArrayList arrayList = new ArrayList();
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if ("item".equals(name)) {
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "value");
                    if (attributeValue != null) {
                        arrayList.add(attributeValue);
                    } else {
                        Slogf.m12w("DevicePolicyManager", "Package name missing under %s", name);
                    }
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown tag under %s: ", str, name);
                }
            }
        }
        return arrayList;
    }

    public final void readAttributeValues(TypedXmlPullParser typedXmlPullParser, String str, Collection<String> collection) throws XmlPullParserException, IOException {
        collection.clear();
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (str.equals(name)) {
                    collection.add(typedXmlPullParser.getAttributeValue((String) null, "value"));
                } else {
                    Slogf.m24e("DevicePolicyManager", "Expected tag %s but found %s", str, name);
                }
            }
        }
    }

    public final ArrayMap<String, TrustAgentInfo> getAllTrustAgentInfos(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        ArrayMap<String, TrustAgentInfo> arrayMap = new ArrayMap<>();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if ("component".equals(name)) {
                    arrayMap.put(typedXmlPullParser.getAttributeValue((String) null, "value"), getTrustAgentInfo(typedXmlPullParser, str));
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown tag under %s: %s", str, name);
                }
            }
        }
        return arrayMap;
    }

    public final TrustAgentInfo getTrustAgentInfo(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        TrustAgentInfo trustAgentInfo = new TrustAgentInfo(null);
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if ("trust-agent-component-options".equals(name)) {
                    trustAgentInfo.options = PersistableBundle.restoreFromXml(typedXmlPullParser);
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown tag under %s: %s", str, name);
                }
            }
        }
        return trustAgentInfo;
    }

    public final List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        ArrayList arrayList = new ArrayList();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if ("preferential_network_service_config".equals(name)) {
                    arrayList.add(PreferentialNetworkServiceConfig.getPreferentialNetworkServiceConfig(typedXmlPullParser, str));
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown tag under %s: %s", str, name);
                }
            }
        }
        return arrayList;
    }

    public boolean hasUserRestrictions() {
        Bundle bundle = this.userRestrictions;
        return bundle != null && bundle.size() > 0;
    }

    public Bundle ensureUserRestrictions() {
        if (this.userRestrictions == null) {
            this.userRestrictions = new Bundle();
        }
        return this.userRestrictions;
    }

    public void transfer(DeviceAdminInfo deviceAdminInfo) {
        if (hasParentActiveAdmin()) {
            this.parentAdmin.info = deviceAdminInfo;
        }
        this.info = deviceAdminInfo;
    }

    public Bundle addSyntheticRestrictions(Bundle bundle) {
        if (this.disableCamera) {
            bundle.putBoolean("no_camera", true);
        }
        if (this.requireAutoTime) {
            bundle.putBoolean("no_config_date_time", true);
        }
        return bundle;
    }

    public static Bundle removeDeprecatedRestrictions(Bundle bundle) {
        for (String str : UserRestrictionsUtils.DEPRECATED_USER_RESTRICTIONS) {
            bundle.remove(str);
        }
        return bundle;
    }

    public static Bundle filterRestrictions(Bundle bundle, Predicate<String> predicate) {
        Bundle bundle2 = new Bundle();
        for (String str : bundle.keySet()) {
            if (bundle.getBoolean(str) && predicate.test(str)) {
                bundle2.putBoolean(str, true);
            }
        }
        return bundle2;
    }

    public Bundle getEffectiveRestrictions() {
        return addSyntheticRestrictions(removeDeprecatedRestrictions(new Bundle(ensureUserRestrictions())));
    }

    public Bundle getLocalUserRestrictions(final int i) {
        return filterRestrictions(getEffectiveRestrictions(), new Predicate() { // from class: com.android.server.devicepolicy.ActiveAdmin$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isLocal;
                isLocal = UserRestrictionsUtils.isLocal(i, (String) obj);
                return isLocal;
            }
        });
    }

    public Bundle getGlobalUserRestrictions(final int i) {
        return filterRestrictions(getEffectiveRestrictions(), new Predicate() { // from class: com.android.server.devicepolicy.ActiveAdmin$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isGlobal;
                isGlobal = UserRestrictionsUtils.isGlobal(i, (String) obj);
                return isGlobal;
            }
        });
    }

    public void dumpPackagePolicy(final IndentingPrintWriter indentingPrintWriter, String str, PackagePolicy packagePolicy) {
        indentingPrintWriter.print(str);
        indentingPrintWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
        if (packagePolicy != null) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("policyType=");
            indentingPrintWriter.println(packagePolicy.getPolicyType());
            indentingPrintWriter.println("packageNames:");
            indentingPrintWriter.increaseIndent();
            packagePolicy.getPackageNames().forEach(new Consumer() { // from class: com.android.server.devicepolicy.ActiveAdmin$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    indentingPrintWriter.println((String) obj);
                }
            });
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.decreaseIndent();
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("uid=");
        indentingPrintWriter.println(getUid());
        indentingPrintWriter.print("testOnlyAdmin=");
        indentingPrintWriter.println(this.testOnlyAdmin);
        if (this.info != null) {
            indentingPrintWriter.println("policies:");
            ArrayList usedPolicies = this.info.getUsedPolicies();
            if (usedPolicies != null) {
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < usedPolicies.size(); i++) {
                    indentingPrintWriter.println(((DeviceAdminInfo.PolicyInfo) usedPolicies.get(i)).tag);
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
        indentingPrintWriter.print("passwordQuality=0x");
        indentingPrintWriter.println(Integer.toHexString(this.mPasswordPolicy.quality));
        indentingPrintWriter.print("minimumPasswordLength=");
        indentingPrintWriter.println(this.mPasswordPolicy.length);
        indentingPrintWriter.print("passwordHistoryLength=");
        indentingPrintWriter.println(this.passwordHistoryLength);
        indentingPrintWriter.print("minimumPasswordUpperCase=");
        indentingPrintWriter.println(this.mPasswordPolicy.upperCase);
        indentingPrintWriter.print("minimumPasswordLowerCase=");
        indentingPrintWriter.println(this.mPasswordPolicy.lowerCase);
        indentingPrintWriter.print("minimumPasswordLetters=");
        indentingPrintWriter.println(this.mPasswordPolicy.letters);
        indentingPrintWriter.print("minimumPasswordNumeric=");
        indentingPrintWriter.println(this.mPasswordPolicy.numeric);
        indentingPrintWriter.print("minimumPasswordSymbols=");
        indentingPrintWriter.println(this.mPasswordPolicy.symbols);
        indentingPrintWriter.print("minimumPasswordNonLetter=");
        indentingPrintWriter.println(this.mPasswordPolicy.nonLetter);
        indentingPrintWriter.print("maximumTimeToUnlock=");
        indentingPrintWriter.println(this.maximumTimeToUnlock);
        indentingPrintWriter.print("strongAuthUnlockTimeout=");
        indentingPrintWriter.println(this.strongAuthUnlockTimeout);
        indentingPrintWriter.print("maximumFailedPasswordsForWipe=");
        indentingPrintWriter.println(this.maximumFailedPasswordsForWipe);
        indentingPrintWriter.print("specifiesGlobalProxy=");
        indentingPrintWriter.println(this.specifiesGlobalProxy);
        indentingPrintWriter.print("passwordExpirationTimeout=");
        indentingPrintWriter.println(this.passwordExpirationTimeout);
        indentingPrintWriter.print("passwordExpirationDate=");
        indentingPrintWriter.println(this.passwordExpirationDate);
        if (this.globalProxySpec != null) {
            indentingPrintWriter.print("globalProxySpec=");
            indentingPrintWriter.println(this.globalProxySpec);
        }
        if (this.globalProxyExclusionList != null) {
            indentingPrintWriter.print("globalProxyEclusionList=");
            indentingPrintWriter.println(this.globalProxyExclusionList);
        }
        indentingPrintWriter.print("encryptionRequested=");
        indentingPrintWriter.println(this.encryptionRequested);
        indentingPrintWriter.print("disableCamera=");
        indentingPrintWriter.println(this.disableCamera);
        indentingPrintWriter.print("disableCallerId=");
        indentingPrintWriter.println(this.disableCallerId);
        indentingPrintWriter.print("disableContactsSearch=");
        indentingPrintWriter.println(this.disableContactsSearch);
        indentingPrintWriter.print("disableBluetoothContactSharing=");
        indentingPrintWriter.println(this.disableBluetoothContactSharing);
        indentingPrintWriter.print("disableScreenCapture=");
        indentingPrintWriter.println(this.disableScreenCapture);
        indentingPrintWriter.print("requireAutoTime=");
        indentingPrintWriter.println(this.requireAutoTime);
        indentingPrintWriter.print("forceEphemeralUsers=");
        indentingPrintWriter.println(this.forceEphemeralUsers);
        indentingPrintWriter.print("isNetworkLoggingEnabled=");
        indentingPrintWriter.println(this.isNetworkLoggingEnabled);
        indentingPrintWriter.print("disabledKeyguardFeatures=");
        indentingPrintWriter.println(this.disabledKeyguardFeatures);
        indentingPrintWriter.print("crossProfileWidgetProviders=");
        indentingPrintWriter.println(this.crossProfileWidgetProviders);
        if (this.permittedAccessiblityServices != null) {
            indentingPrintWriter.print("permittedAccessibilityServices=");
            indentingPrintWriter.println(this.permittedAccessiblityServices);
        }
        if (this.permittedInputMethods != null) {
            indentingPrintWriter.print("permittedInputMethods=");
            indentingPrintWriter.println(this.permittedInputMethods);
        }
        if (this.permittedNotificationListeners != null) {
            indentingPrintWriter.print("permittedNotificationListeners=");
            indentingPrintWriter.println(this.permittedNotificationListeners);
        }
        if (this.keepUninstalledPackages != null) {
            indentingPrintWriter.print("keepUninstalledPackages=");
            indentingPrintWriter.println(this.keepUninstalledPackages);
        }
        if (this.meteredDisabledPackages != null) {
            indentingPrintWriter.print("meteredDisabledPackages=");
            indentingPrintWriter.println(this.meteredDisabledPackages);
        }
        if (this.protectedPackages != null) {
            indentingPrintWriter.print("protectedPackages=");
            indentingPrintWriter.println(this.protectedPackages);
        }
        if (this.suspendedPackages != null) {
            indentingPrintWriter.print("suspendedPackages=");
            indentingPrintWriter.println(this.suspendedPackages);
        }
        indentingPrintWriter.print("organizationColor=");
        indentingPrintWriter.println(this.organizationColor);
        if (this.organizationName != null) {
            indentingPrintWriter.print("organizationName=");
            indentingPrintWriter.println(this.organizationName);
        }
        indentingPrintWriter.println("userRestrictions:");
        UserRestrictionsUtils.dumpRestrictions(indentingPrintWriter, "  ", this.userRestrictions);
        indentingPrintWriter.print("defaultEnabledRestrictionsAlreadySet=");
        indentingPrintWriter.println(this.defaultEnabledRestrictionsAlreadySet);
        dumpPackagePolicy(indentingPrintWriter, "managedProfileCallerIdPolicy", this.mManagedProfileCallerIdAccess);
        dumpPackagePolicy(indentingPrintWriter, "managedProfileContactsPolicy", this.mManagedProfileContactsAccess);
        dumpPackagePolicy(indentingPrintWriter, "credentialManagerPolicy", this.mCredentialManagerPolicy);
        indentingPrintWriter.print("isParent=");
        indentingPrintWriter.println(this.isParent);
        if (this.parentAdmin != null) {
            indentingPrintWriter.println("parentAdmin:");
            indentingPrintWriter.increaseIndent();
            this.parentAdmin.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mCrossProfileCalendarPackages != null) {
            indentingPrintWriter.print("mCrossProfileCalendarPackages=");
            indentingPrintWriter.println(this.mCrossProfileCalendarPackages);
        }
        indentingPrintWriter.print("mCrossProfilePackages=");
        indentingPrintWriter.println(this.mCrossProfilePackages);
        indentingPrintWriter.print("mSuspendPersonalApps=");
        indentingPrintWriter.println(this.mSuspendPersonalApps);
        indentingPrintWriter.print("mProfileMaximumTimeOffMillis=");
        indentingPrintWriter.println(this.mProfileMaximumTimeOffMillis);
        indentingPrintWriter.print("mProfileOffDeadline=");
        indentingPrintWriter.println(this.mProfileOffDeadline);
        indentingPrintWriter.print("mAlwaysOnVpnPackage=");
        indentingPrintWriter.println(this.mAlwaysOnVpnPackage);
        indentingPrintWriter.print("mAlwaysOnVpnLockdown=");
        indentingPrintWriter.println(this.mAlwaysOnVpnLockdown);
        indentingPrintWriter.print("mCommonCriteriaMode=");
        indentingPrintWriter.println(this.mCommonCriteriaMode);
        indentingPrintWriter.print("mPasswordComplexity=");
        indentingPrintWriter.println(this.mPasswordComplexity);
        indentingPrintWriter.print("mNearbyNotificationStreamingPolicy=");
        indentingPrintWriter.println(this.mNearbyNotificationStreamingPolicy);
        indentingPrintWriter.print("mNearbyAppStreamingPolicy=");
        indentingPrintWriter.println(this.mNearbyAppStreamingPolicy);
        if (!TextUtils.isEmpty(this.mOrganizationId)) {
            indentingPrintWriter.print("mOrganizationId=");
            indentingPrintWriter.println(this.mOrganizationId);
        }
        if (!TextUtils.isEmpty(this.mEnrollmentSpecificId)) {
            indentingPrintWriter.print("mEnrollmentSpecificId=");
            indentingPrintWriter.println(this.mEnrollmentSpecificId);
        }
        indentingPrintWriter.print("mAdminCanGrantSensorsPermissions=");
        indentingPrintWriter.println(this.mAdminCanGrantSensorsPermissions);
        indentingPrintWriter.print("mUsbDataSignaling=");
        indentingPrintWriter.println(this.mUsbDataSignalingEnabled);
        indentingPrintWriter.print("mWifiMinimumSecurityLevel=");
        indentingPrintWriter.println(this.mWifiMinimumSecurityLevel);
        WifiSsidPolicy wifiSsidPolicy = this.mWifiSsidPolicy;
        if (wifiSsidPolicy != null) {
            if (wifiSsidPolicy.getPolicyType() == 0) {
                indentingPrintWriter.print("mSsidAllowlist=");
            } else {
                indentingPrintWriter.print("mSsidDenylist=");
            }
            indentingPrintWriter.println(ssidsToStrings(this.mWifiSsidPolicy.getSsids()));
        }
        if (this.mFactoryResetProtectionPolicy != null) {
            indentingPrintWriter.println("mFactoryResetProtectionPolicy:");
            indentingPrintWriter.increaseIndent();
            this.mFactoryResetProtectionPolicy.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mPreferentialNetworkServiceConfigs != null) {
            indentingPrintWriter.println("mPreferentialNetworkServiceConfigs:");
            indentingPrintWriter.increaseIndent();
            for (PreferentialNetworkServiceConfig preferentialNetworkServiceConfig : this.mPreferentialNetworkServiceConfigs) {
                preferentialNetworkServiceConfig.dump(indentingPrintWriter);
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.print("mtePolicy=");
        indentingPrintWriter.println(this.mtePolicy);
        indentingPrintWriter.print("accountTypesWithManagementDisabled=");
        indentingPrintWriter.println(this.accountTypesWithManagementDisabled);
        if (this.mManagedSubscriptionsPolicy != null) {
            indentingPrintWriter.println("mManagedSubscriptionsPolicy:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println(this.mManagedSubscriptionsPolicy);
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.print("mDialerPackage=");
        indentingPrintWriter.println(this.mDialerPackage);
        indentingPrintWriter.print("mSmsPackage=");
        indentingPrintWriter.println(this.mSmsPackage);
    }
}
