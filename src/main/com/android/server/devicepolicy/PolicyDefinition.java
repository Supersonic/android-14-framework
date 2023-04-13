package com.android.server.devicepolicy;

import android.app.admin.BooleanPolicyValue;
import android.app.admin.DevicePolicyIdentifiers;
import android.app.admin.IntegerPolicyValue;
import android.app.admin.IntentFilterPolicyKey;
import android.app.admin.LockTaskPolicy;
import android.app.admin.NoArgsPolicyKey;
import android.app.admin.PackagePermissionPolicyKey;
import android.app.admin.PackagePolicyKey;
import android.app.admin.PolicyKey;
import android.app.admin.PolicyValue;
import android.app.admin.UserRestrictionPolicyKey;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import com.android.internal.util.function.QuadFunction;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class PolicyDefinition<V> {
    public static PolicyDefinition<Boolean> AUTO_TIMEZONE;
    public static final MostRestrictive<Boolean> FALSE_MORE_RESTRICTIVE = new MostRestrictive<>(List.of(new BooleanPolicyValue(false), new BooleanPolicyValue(true)));
    public static PolicyDefinition<Bundle> GENERIC_APPLICATION_RESTRICTIONS;
    public static PolicyDefinition<Boolean> GENERIC_PACKAGE_UNINSTALL_BLOCKED;
    public static final PolicyDefinition<Integer> GENERIC_PERMISSION_GRANT;
    public static PolicyDefinition<ComponentName> GENERIC_PERSISTENT_PREFERRED_ACTIVITY;
    public static PolicyDefinition<LockTaskPolicy> LOCK_TASK;
    public static final Map<String, PolicyDefinition<?>> POLICY_DEFINITIONS;
    public static PolicyDefinition<Long> RESET_PASSWORD_TOKEN;
    public static final MostRestrictive<Boolean> TRUE_MORE_RESTRICTIVE;
    public static PolicyDefinition<Set<String>> USER_CONTROLLED_DISABLED_PACKAGES;
    public static Map<String, Integer> USER_RESTRICTION_FLAGS;
    public final QuadFunction<V, Context, Integer, PolicyKey, Boolean> mPolicyEnforcerCallback;
    public final int mPolicyFlags;
    public final PolicyKey mPolicyKey;
    public final PolicySerializer<V> mPolicySerializer;
    public final ResolutionMechanism<V> mResolutionMechanism;

    static {
        MostRestrictive<Boolean> mostRestrictive = new MostRestrictive<>(List.of(new BooleanPolicyValue(true), new BooleanPolicyValue(false)));
        TRUE_MORE_RESTRICTIVE = mostRestrictive;
        AUTO_TIMEZONE = new PolicyDefinition<>(new NoArgsPolicyKey("autoTimezone"), mostRestrictive, 1, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda0
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                Boolean lambda$static$0;
                lambda$static$0 = PolicyDefinition.lambda$static$0((Boolean) obj, (Context) obj2, (Integer) obj3, (PolicyKey) obj4);
                return lambda$static$0;
            }
        }, new BooleanPolicySerializer());
        PolicyDefinition<Integer> policyDefinition = new PolicyDefinition<>(new PackagePermissionPolicyKey("permissionGrant"), new MostRestrictive(List.of(new IntegerPolicyValue(2), new IntegerPolicyValue(1), new IntegerPolicyValue(0))), 2, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda1
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Boolean.valueOf(PolicyEnforcerCallbacks.setPermissionGrantState((Integer) obj, (Context) obj2, ((Integer) obj3).intValue(), (PolicyKey) obj4));
            }
        }, new IntegerPolicySerializer());
        GENERIC_PERMISSION_GRANT = policyDefinition;
        LOCK_TASK = new PolicyDefinition<>(new NoArgsPolicyKey("lockTask"), new TopPriority(List.of(EnforcingAdmin.getRoleAuthorityOf("android.app.role.SYSTEM_FINANCED_DEVICE_CONTROLLER"), "enterprise")), 2, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda2
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                Boolean lambda$static$1;
                lambda$static$1 = PolicyDefinition.lambda$static$1((LockTaskPolicy) obj, (Context) obj2, (Integer) obj3, (PolicyKey) obj4);
                return lambda$static$1;
            }
        }, new LockTaskPolicySerializer());
        USER_CONTROLLED_DISABLED_PACKAGES = new PolicyDefinition<>(new NoArgsPolicyKey("userControlDisabledPackages"), new StringSetUnion(), new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda3
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                Boolean lambda$static$2;
                lambda$static$2 = PolicyDefinition.lambda$static$2((Set) obj, (Context) obj2, (Integer) obj3, (PolicyKey) obj4);
                return lambda$static$2;
            }
        }, new StringSetPolicySerializer());
        GENERIC_PERSISTENT_PREFERRED_ACTIVITY = new PolicyDefinition<>(new IntentFilterPolicyKey("persistentPreferredActivity"), new TopPriority(List.of(EnforcingAdmin.getRoleAuthorityOf("android.app.role.SYSTEM_FINANCED_DEVICE_CONTROLLER"), "enterprise")), 2, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda4
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Boolean.valueOf(PolicyEnforcerCallbacks.addPersistentPreferredActivity((ComponentName) obj, (Context) obj2, ((Integer) obj3).intValue(), (PolicyKey) obj4));
            }
        }, new ComponentNamePolicySerializer());
        GENERIC_PACKAGE_UNINSTALL_BLOCKED = new PolicyDefinition<>(new PackagePolicyKey("packageUninstallBlocked"), mostRestrictive, 2, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda5
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Boolean.valueOf(PolicyEnforcerCallbacks.setUninstallBlocked((Boolean) obj, (Context) obj2, ((Integer) obj3).intValue(), (PolicyKey) obj4));
            }
        }, new BooleanPolicySerializer());
        GENERIC_APPLICATION_RESTRICTIONS = new PolicyDefinition<>(new PackagePolicyKey("applicationRestrictions"), new MostRecent(), 10, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda6
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                Boolean bool;
                Bundle bundle = (Bundle) obj;
                Context context = (Context) obj2;
                Integer num = (Integer) obj3;
                PolicyKey policyKey = (PolicyKey) obj4;
                bool = Boolean.TRUE;
                return bool;
            }
        }, new BundlePolicySerializer());
        RESET_PASSWORD_TOKEN = new PolicyDefinition<>(new NoArgsPolicyKey("resetPasswordToken"), new MostRecent(), 10, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda7
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                Boolean bool;
                Long l = (Long) obj;
                Context context = (Context) obj2;
                Integer num = (Integer) obj3;
                PolicyKey policyKey = (PolicyKey) obj4;
                bool = Boolean.TRUE;
                return bool;
            }
        }, new LongPolicySerializer());
        HashMap hashMap = new HashMap();
        POLICY_DEFINITIONS = hashMap;
        USER_RESTRICTION_FLAGS = new HashMap();
        hashMap.put("autoTimezone", AUTO_TIMEZONE);
        hashMap.put("permissionGrant", policyDefinition);
        hashMap.put("lockTask", LOCK_TASK);
        hashMap.put("userControlDisabledPackages", USER_CONTROLLED_DISABLED_PACKAGES);
        hashMap.put("persistentPreferredActivity", GENERIC_PERSISTENT_PREFERRED_ACTIVITY);
        hashMap.put("packageUninstallBlocked", GENERIC_PACKAGE_UNINSTALL_BLOCKED);
        hashMap.put("applicationRestrictions", GENERIC_APPLICATION_RESTRICTIONS);
        hashMap.put("resetPasswordToken", RESET_PASSWORD_TOKEN);
        USER_RESTRICTION_FLAGS.put("no_modify_accounts", 0);
        USER_RESTRICTION_FLAGS.put("no_config_wifi", 0);
        USER_RESTRICTION_FLAGS.put("no_change_wifi_state", 1);
        USER_RESTRICTION_FLAGS.put("no_wifi_tethering", 1);
        USER_RESTRICTION_FLAGS.put("no_grant_admin", 0);
        USER_RESTRICTION_FLAGS.put("no_sharing_admin_configured_wifi", 1);
        USER_RESTRICTION_FLAGS.put("no_wifi_direct", 1);
        USER_RESTRICTION_FLAGS.put("no_add_wifi_config", 1);
        USER_RESTRICTION_FLAGS.put("no_config_locale", 0);
        USER_RESTRICTION_FLAGS.put("no_install_apps", 0);
        USER_RESTRICTION_FLAGS.put("no_uninstall_apps", 0);
        USER_RESTRICTION_FLAGS.put("no_share_location", 0);
        USER_RESTRICTION_FLAGS.put("no_airplane_mode", 1);
        USER_RESTRICTION_FLAGS.put("no_config_brightness", 0);
        USER_RESTRICTION_FLAGS.put("no_ambient_display", 0);
        USER_RESTRICTION_FLAGS.put("no_config_screen_timeout", 0);
        USER_RESTRICTION_FLAGS.put("no_install_unknown_sources", 0);
        USER_RESTRICTION_FLAGS.put("no_install_unknown_sources_globally", 1);
        USER_RESTRICTION_FLAGS.put("no_config_bluetooth", 0);
        USER_RESTRICTION_FLAGS.put("no_bluetooth", 0);
        USER_RESTRICTION_FLAGS.put("no_bluetooth_sharing", 0);
        USER_RESTRICTION_FLAGS.put("no_usb_file_transfer", 1);
        USER_RESTRICTION_FLAGS.put("no_config_credentials", 0);
        USER_RESTRICTION_FLAGS.put("no_remove_user", 0);
        USER_RESTRICTION_FLAGS.put("no_remove_managed_profile", 0);
        USER_RESTRICTION_FLAGS.put("no_debugging_features", 0);
        USER_RESTRICTION_FLAGS.put("no_config_vpn", 0);
        USER_RESTRICTION_FLAGS.put("no_config_location", 0);
        USER_RESTRICTION_FLAGS.put("no_config_date_time", 0);
        USER_RESTRICTION_FLAGS.put("no_config_tethering", 0);
        USER_RESTRICTION_FLAGS.put("no_network_reset", 1);
        USER_RESTRICTION_FLAGS.put("no_factory_reset", 0);
        USER_RESTRICTION_FLAGS.put("no_add_user", 0);
        USER_RESTRICTION_FLAGS.put("no_add_managed_profile", 0);
        USER_RESTRICTION_FLAGS.put("no_add_clone_profile", 0);
        USER_RESTRICTION_FLAGS.put("ensure_verify_apps", 1);
        USER_RESTRICTION_FLAGS.put("no_config_cell_broadcasts", 0);
        USER_RESTRICTION_FLAGS.put("no_config_mobile_networks", 0);
        USER_RESTRICTION_FLAGS.put("no_control_apps", 0);
        USER_RESTRICTION_FLAGS.put("no_physical_media", 0);
        USER_RESTRICTION_FLAGS.put("no_unmute_microphone", 0);
        USER_RESTRICTION_FLAGS.put("no_adjust_volume", 0);
        USER_RESTRICTION_FLAGS.put("no_outgoing_calls", 0);
        USER_RESTRICTION_FLAGS.put("no_sms", 0);
        USER_RESTRICTION_FLAGS.put("no_fun", 0);
        USER_RESTRICTION_FLAGS.put("no_create_windows", 0);
        USER_RESTRICTION_FLAGS.put("no_system_error_dialogs", 0);
        USER_RESTRICTION_FLAGS.put("no_cross_profile_copy_paste", 0);
        USER_RESTRICTION_FLAGS.put("no_outgoing_beam", 0);
        USER_RESTRICTION_FLAGS.put("no_wallpaper", 0);
        USER_RESTRICTION_FLAGS.put("no_set_wallpaper", 0);
        USER_RESTRICTION_FLAGS.put("no_safe_boot", 0);
        USER_RESTRICTION_FLAGS.put("no_record_audio", 0);
        USER_RESTRICTION_FLAGS.put("no_run_in_background", 0);
        USER_RESTRICTION_FLAGS.put("no_camera", 0);
        USER_RESTRICTION_FLAGS.put("disallow_unmute_device", 0);
        USER_RESTRICTION_FLAGS.put("no_data_roaming", 0);
        USER_RESTRICTION_FLAGS.put("no_set_user_icon", 0);
        USER_RESTRICTION_FLAGS.put("no_oem_unlock", 1);
        USER_RESTRICTION_FLAGS.put("no_unified_password", 0);
        USER_RESTRICTION_FLAGS.put("allow_parent_profile_app_linking", 0);
        USER_RESTRICTION_FLAGS.put("no_autofill", 0);
        USER_RESTRICTION_FLAGS.put("no_content_capture", 0);
        USER_RESTRICTION_FLAGS.put("no_content_suggestions", 0);
        USER_RESTRICTION_FLAGS.put("no_user_switch", 1);
        USER_RESTRICTION_FLAGS.put("no_sharing_into_profile", 0);
        USER_RESTRICTION_FLAGS.put("no_printing", 0);
        USER_RESTRICTION_FLAGS.put("disallow_config_private_dns", 1);
        USER_RESTRICTION_FLAGS.put("disallow_microphone_toggle", 0);
        USER_RESTRICTION_FLAGS.put("disallow_camera_toggle", 0);
        USER_RESTRICTION_FLAGS.put("disallow_biometric", 0);
        USER_RESTRICTION_FLAGS.put("disallow_config_default_apps", 0);
        USER_RESTRICTION_FLAGS.put("no_cellular_2g", 1);
        USER_RESTRICTION_FLAGS.put("no_ultra_wideband_radio", 1);
        for (String str : USER_RESTRICTION_FLAGS.keySet()) {
            createAndAddUserRestrictionPolicyDefinition(str, USER_RESTRICTION_FLAGS.get(str).intValue());
        }
    }

    public static /* synthetic */ Boolean lambda$static$0(Boolean bool, Context context, Integer num, PolicyKey policyKey) {
        return Boolean.valueOf(PolicyEnforcerCallbacks.setAutoTimezoneEnabled(bool, context));
    }

    public static PolicyDefinition<Integer> PERMISSION_GRANT(String str, String str2) {
        if (str == null || str2 == null) {
            return GENERIC_PERMISSION_GRANT;
        }
        return GENERIC_PERMISSION_GRANT.createPolicyDefinition(new PackagePermissionPolicyKey("permissionGrant", str, str2));
    }

    public static /* synthetic */ Boolean lambda$static$1(LockTaskPolicy lockTaskPolicy, Context context, Integer num, PolicyKey policyKey) {
        return Boolean.valueOf(PolicyEnforcerCallbacks.setLockTask(lockTaskPolicy, context, num.intValue()));
    }

    public static /* synthetic */ Boolean lambda$static$2(Set set, Context context, Integer num, PolicyKey policyKey) {
        return Boolean.valueOf(PolicyEnforcerCallbacks.setUserControlDisabledPackages(set, num.intValue()));
    }

    public static PolicyDefinition<ComponentName> PERSISTENT_PREFERRED_ACTIVITY(IntentFilter intentFilter) {
        if (intentFilter == null) {
            return GENERIC_PERSISTENT_PREFERRED_ACTIVITY;
        }
        return GENERIC_PERSISTENT_PREFERRED_ACTIVITY.createPolicyDefinition(new IntentFilterPolicyKey("persistentPreferredActivity", intentFilter));
    }

    public static PolicyDefinition<Boolean> PACKAGE_UNINSTALL_BLOCKED(String str) {
        if (str == null) {
            return GENERIC_PACKAGE_UNINSTALL_BLOCKED;
        }
        return GENERIC_PACKAGE_UNINSTALL_BLOCKED.createPolicyDefinition(new PackagePolicyKey("packageUninstallBlocked", str));
    }

    public static PolicyDefinition<Bundle> APPLICATION_RESTRICTIONS(String str) {
        if (str == null) {
            return GENERIC_APPLICATION_RESTRICTIONS;
        }
        return GENERIC_APPLICATION_RESTRICTIONS.createPolicyDefinition(new PackagePolicyKey("applicationRestrictions", str));
    }

    public final PolicyDefinition<V> createPolicyDefinition(PolicyKey policyKey) {
        return new PolicyDefinition<>(policyKey, this.mResolutionMechanism, this.mPolicyFlags, this.mPolicyEnforcerCallback, this.mPolicySerializer);
    }

    public static PolicyDefinition<Boolean> getPolicyDefinitionForUserRestriction(String str) {
        String identifierForUserRestriction = DevicePolicyIdentifiers.getIdentifierForUserRestriction(str);
        Map<String, PolicyDefinition<?>> map = POLICY_DEFINITIONS;
        if (!map.containsKey(identifierForUserRestriction)) {
            throw new IllegalArgumentException("Unsupported user restriction " + str);
        }
        return map.get(identifierForUserRestriction);
    }

    public PolicyKey getPolicyKey() {
        return this.mPolicyKey;
    }

    public ResolutionMechanism<V> getResolutionMechanism() {
        return this.mResolutionMechanism;
    }

    public boolean isGlobalOnlyPolicy() {
        return (this.mPolicyFlags & 1) != 0;
    }

    public boolean isLocalOnlyPolicy() {
        return (this.mPolicyFlags & 2) != 0;
    }

    public boolean isInheritable() {
        return (this.mPolicyFlags & 4) != 0;
    }

    public boolean isNonCoexistablePolicy() {
        return (this.mPolicyFlags & 8) != 0;
    }

    public boolean isUserRestrictionPolicy() {
        return (this.mPolicyFlags & 16) != 0;
    }

    public PolicyValue<V> resolvePolicy(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        return this.mResolutionMechanism.resolve(linkedHashMap);
    }

    public boolean enforcePolicy(V v, Context context, int i) {
        return ((Boolean) this.mPolicyEnforcerCallback.apply(v, context, Integer.valueOf(i), this.mPolicyKey)).booleanValue();
    }

    public static void createAndAddUserRestrictionPolicyDefinition(String str, int i) {
        UserRestrictionPolicyKey userRestrictionPolicyKey = new UserRestrictionPolicyKey(DevicePolicyIdentifiers.getIdentifierForUserRestriction(str), str);
        POLICY_DEFINITIONS.put(userRestrictionPolicyKey.getIdentifier(), new PolicyDefinition<>(userRestrictionPolicyKey, TRUE_MORE_RESTRICTIVE, i | 16, new QuadFunction() { // from class: com.android.server.devicepolicy.PolicyDefinition$$ExternalSyntheticLambda8
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Boolean.valueOf(PolicyEnforcerCallbacks.setUserRestriction((Boolean) obj, (Context) obj2, ((Integer) obj3).intValue(), (PolicyKey) obj4));
            }
        }, new BooleanPolicySerializer()));
    }

    public PolicyDefinition(PolicyKey policyKey, ResolutionMechanism<V> resolutionMechanism, QuadFunction<V, Context, Integer, PolicyKey, Boolean> quadFunction, PolicySerializer<V> policySerializer) {
        this(policyKey, resolutionMechanism, 0, quadFunction, policySerializer);
    }

    public PolicyDefinition(PolicyKey policyKey, ResolutionMechanism<V> resolutionMechanism, int i, QuadFunction<V, Context, Integer, PolicyKey, Boolean> quadFunction, PolicySerializer<V> policySerializer) {
        this.mPolicyKey = policyKey;
        this.mResolutionMechanism = resolutionMechanism;
        this.mPolicyFlags = i;
        this.mPolicyEnforcerCallback = quadFunction;
        this.mPolicySerializer = policySerializer;
        if (isNonCoexistablePolicy() && !isLocalOnlyPolicy()) {
            throw new UnsupportedOperationException("Non-coexistable global policies not supported,please add support.");
        }
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        this.mPolicyKey.saveToXml(typedXmlSerializer);
    }

    public static <V> PolicyDefinition<V> readFromXml(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        PolicyKey readPolicyKeyFromXml = readPolicyKeyFromXml(typedXmlPullParser);
        return (PolicyDefinition<V>) POLICY_DEFINITIONS.get(readPolicyKeyFromXml.getIdentifier()).createPolicyDefinition(readPolicyKeyFromXml);
    }

    public static <V> PolicyKey readPolicyKeyFromXml(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        return POLICY_DEFINITIONS.get(PolicyKey.readGenericPolicyKeyFromXml(typedXmlPullParser).getIdentifier()).mPolicyKey.readFromXml(typedXmlPullParser);
    }

    public void savePolicyValueToXml(TypedXmlSerializer typedXmlSerializer, String str, V v) throws IOException {
        this.mPolicySerializer.saveToXml(this.mPolicyKey, typedXmlSerializer, str, v);
    }

    public PolicyValue<V> readPolicyValueFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        return this.mPolicySerializer.readFromXml(typedXmlPullParser, str);
    }

    public String toString() {
        return "PolicyDefinition{ mPolicyKey= " + this.mPolicyKey + ", mResolutionMechanism= " + this.mResolutionMechanism + ", mPolicyFlags= " + this.mPolicyFlags + " }";
    }
}
