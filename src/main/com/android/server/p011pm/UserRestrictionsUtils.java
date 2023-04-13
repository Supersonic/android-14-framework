package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IStopUserCallback;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.BundleUtils;
import com.android.server.LocalServices;
import com.google.android.collect.Sets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.pm.UserRestrictionsUtils */
/* loaded from: classes2.dex */
public class UserRestrictionsUtils {
    public static final Set<String> USER_RESTRICTIONS = newSetWithUniqueCheck(new String[]{"no_config_wifi", "no_config_locale", "no_modify_accounts", "no_install_apps", "no_uninstall_apps", "no_share_location", "no_install_unknown_sources", "no_install_unknown_sources_globally", "no_config_bluetooth", "no_bluetooth", "no_bluetooth_sharing", "no_usb_file_transfer", "no_config_credentials", "no_remove_user", "no_remove_managed_profile", "no_debugging_features", "no_config_vpn", "no_config_date_time", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "no_add_managed_profile", "no_add_clone_profile", "ensure_verify_apps", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_control_apps", "no_physical_media", "no_unmute_microphone", "no_adjust_volume", "no_outgoing_calls", "no_sms", "no_fun", "no_create_windows", "no_system_error_dialogs", "no_cross_profile_copy_paste", "no_outgoing_beam", "no_wallpaper", "no_safe_boot", "allow_parent_profile_app_linking", "no_record_audio", "no_camera", "no_run_in_background", "no_data_roaming", "no_set_user_icon", "no_set_wallpaper", "no_oem_unlock", "disallow_unmute_device", "no_autofill", "no_content_capture", "no_content_suggestions", "no_user_switch", "no_unified_password", "no_config_location", "no_airplane_mode", "no_config_brightness", "no_sharing_into_profile", "no_ambient_display", "no_config_screen_timeout", "no_printing", "disallow_config_private_dns", "disallow_microphone_toggle", "disallow_camera_toggle", "no_change_wifi_state", "no_wifi_tethering", "no_grant_admin", "no_sharing_admin_configured_wifi", "no_wifi_direct", "no_add_wifi_config", "no_cellular_2g", "no_ultra_wideband_radio", "disallow_config_default_apps"});
    public static final Set<String> DEPRECATED_USER_RESTRICTIONS = Sets.newArraySet(new String[]{"no_add_managed_profile", "no_remove_managed_profile"});
    public static final Set<String> NON_PERSIST_USER_RESTRICTIONS = Sets.newArraySet(new String[]{"no_record_audio"});
    public static final Set<String> MAIN_USER_ONLY_RESTRICTIONS = Sets.newArraySet(new String[]{"no_bluetooth", "no_usb_file_transfer", "no_config_tethering", "no_network_reset", "no_factory_reset", "no_add_user", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_physical_media", "no_sms", "no_fun", "no_safe_boot", "no_create_windows", "no_data_roaming", "no_airplane_mode"});
    public static final Set<String> DEVICE_OWNER_ONLY_RESTRICTIONS = Sets.newArraySet(new String[]{"no_user_switch", "disallow_config_private_dns", "disallow_microphone_toggle", "disallow_camera_toggle", "no_change_wifi_state", "no_wifi_tethering", "no_wifi_direct", "no_add_wifi_config", "no_cellular_2g", "no_ultra_wideband_radio"});
    public static final Set<String> IMMUTABLE_BY_OWNERS = Sets.newArraySet(new String[]{"no_record_audio", "no_wallpaper", "no_oem_unlock"});
    public static final Set<String> GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[]{"no_adjust_volume", "no_bluetooth_sharing", "no_config_date_time", "no_system_error_dialogs", "no_run_in_background", "no_unmute_microphone", "disallow_unmute_device", "no_camera"});
    public static final Set<String> PROFILE_OWNER_ORGANIZATION_OWNED_GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[]{"no_airplane_mode", "no_config_date_time", "disallow_config_private_dns", "no_change_wifi_state", "no_wifi_tethering", "no_wifi_direct", "no_add_wifi_config", "no_cellular_2g", "no_ultra_wideband_radio"});
    public static final Set<String> PROFILE_OWNER_ORGANIZATION_OWNED_LOCAL_RESTRICTIONS = Sets.newArraySet(new String[]{"no_config_bluetooth", "no_config_location", "no_config_wifi", "no_content_capture", "no_content_suggestions", "no_debugging_features", "no_share_location", "no_outgoing_calls", "no_camera", "no_bluetooth", "no_bluetooth_sharing", "no_config_cell_broadcasts", "no_config_mobile_networks", "no_config_tethering", "no_data_roaming", "no_safe_boot", "no_sms", "no_usb_file_transfer", "no_physical_media", "no_unmute_microphone"});
    public static final Set<String> DEFAULT_ENABLED_FOR_MANAGED_PROFILES = Sets.newArraySet(new String[]{"no_bluetooth_sharing"});
    public static final Set<String> PROFILE_GLOBAL_RESTRICTIONS = Sets.newArraySet(new String[]{"ensure_verify_apps", "no_airplane_mode", "no_install_unknown_sources_globally"});
    public static final Set<String> FINANCED_DEVICE_OWNER_RESTRICTIONS = Sets.newArraySet(new String[]{"no_add_user", "no_debugging_features", "no_install_unknown_sources", "no_safe_boot", "no_config_date_time", "no_outgoing_calls"});

    public static Set<String> newSetWithUniqueCheck(String[] strArr) {
        ArraySet newArraySet = Sets.newArraySet(strArr);
        Preconditions.checkState(newArraySet.size() == strArr.length);
        return newArraySet;
    }

    public static boolean isValidRestriction(String str) {
        String[] strArr;
        if (USER_RESTRICTIONS.contains(str)) {
            return true;
        }
        int callingUid = Binder.getCallingUid();
        try {
            strArr = AppGlobals.getPackageManager().getPackagesForUid(callingUid);
        } catch (RemoteException unused) {
            strArr = null;
        }
        StringBuilder sb = new StringBuilder("Unknown restriction queried by uid ");
        sb.append(callingUid);
        if (strArr != null && strArr.length > 0) {
            sb.append(" (");
            sb.append(strArr[0]);
            if (strArr.length > 1) {
                sb.append(" et al");
            }
            sb.append(")");
        }
        sb.append(": ");
        sb.append(str);
        if (str != null && isSystemApp(callingUid, strArr)) {
            Slog.wtf("UserRestrictionsUtils", sb.toString());
        } else {
            Slog.e("UserRestrictionsUtils", sb.toString());
        }
        return false;
    }

    public static boolean isSystemApp(int i, String[] strArr) {
        if (UserHandle.isCore(i)) {
            return true;
        }
        if (strArr == null) {
            return false;
        }
        IPackageManager packageManager = AppGlobals.getPackageManager();
        for (String str : strArr) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 794624L, UserHandle.getUserId(i));
                if (applicationInfo != null && applicationInfo.isSystemApp()) {
                    return true;
                }
            } catch (RemoteException unused) {
            }
        }
        return false;
    }

    public static void writeRestrictions(TypedXmlSerializer typedXmlSerializer, Bundle bundle, String str) throws IOException {
        if (bundle == null) {
            return;
        }
        typedXmlSerializer.startTag((String) null, str);
        for (String str2 : bundle.keySet()) {
            if (!NON_PERSIST_USER_RESTRICTIONS.contains(str2)) {
                if (!USER_RESTRICTIONS.contains(str2)) {
                    Log.w("UserRestrictionsUtils", "Unknown user restriction detected: " + str2);
                } else if (bundle.getBoolean(str2)) {
                    typedXmlSerializer.attributeBoolean((String) null, str2, true);
                }
            }
        }
        typedXmlSerializer.endTag((String) null, str);
    }

    public static void readRestrictions(TypedXmlPullParser typedXmlPullParser, Bundle bundle) {
        bundle.clear();
        for (String str : USER_RESTRICTIONS) {
            if (typedXmlPullParser.getAttributeBoolean((String) null, str, false)) {
                bundle.putBoolean(str, true);
            }
        }
    }

    public static Bundle readRestrictions(TypedXmlPullParser typedXmlPullParser) {
        Bundle bundle = new Bundle();
        readRestrictions(typedXmlPullParser, bundle);
        return bundle;
    }

    public static Bundle nonNull(Bundle bundle) {
        return bundle != null ? bundle : new Bundle();
    }

    public static boolean contains(Bundle bundle, String str) {
        return bundle != null && bundle.getBoolean(str);
    }

    public static void merge(Bundle bundle, Bundle bundle2) {
        Objects.requireNonNull(bundle);
        Preconditions.checkArgument(bundle != bundle2);
        if (bundle2 == null) {
            return;
        }
        for (String str : bundle2.keySet()) {
            if (bundle2.getBoolean(str, false)) {
                bundle.putBoolean(str, true);
            }
        }
    }

    public static boolean canDeviceOwnerChange(String str) {
        return !IMMUTABLE_BY_OWNERS.contains(str);
    }

    public static boolean canProfileOwnerChange(String str, boolean z) {
        return (IMMUTABLE_BY_OWNERS.contains(str) || DEVICE_OWNER_ONLY_RESTRICTIONS.contains(str) || (!z && MAIN_USER_ONLY_RESTRICTIONS.contains(str))) ? false : true;
    }

    public static boolean canProfileOwnerOfOrganizationOwnedDeviceChange(String str) {
        return PROFILE_OWNER_ORGANIZATION_OWNED_GLOBAL_RESTRICTIONS.contains(str) || PROFILE_OWNER_ORGANIZATION_OWNED_LOCAL_RESTRICTIONS.contains(str);
    }

    public static Set<String> getDefaultEnabledForManagedProfiles() {
        return DEFAULT_ENABLED_FOR_MANAGED_PROFILES;
    }

    public static boolean canFinancedDeviceOwnerChange(String str) {
        return FINANCED_DEVICE_OWNER_RESTRICTIONS.contains(str) && canDeviceOwnerChange(str);
    }

    public static boolean isGlobal(int i, String str) {
        return (i == 0 && (MAIN_USER_ONLY_RESTRICTIONS.contains(str) || GLOBAL_RESTRICTIONS.contains(str))) || (i == 2 && PROFILE_OWNER_ORGANIZATION_OWNED_GLOBAL_RESTRICTIONS.contains(str)) || PROFILE_GLOBAL_RESTRICTIONS.contains(str) || DEVICE_OWNER_ONLY_RESTRICTIONS.contains(str);
    }

    public static boolean isLocal(int i, String str) {
        return !isGlobal(i, str);
    }

    public static boolean areEqual(Bundle bundle, Bundle bundle2) {
        if (bundle == bundle2) {
            return true;
        }
        if (BundleUtils.isEmpty(bundle)) {
            return BundleUtils.isEmpty(bundle2);
        }
        if (BundleUtils.isEmpty(bundle2)) {
            return false;
        }
        for (String str : bundle.keySet()) {
            if (bundle.getBoolean(str) != bundle2.getBoolean(str)) {
                return false;
            }
        }
        for (String str2 : bundle2.keySet()) {
            if (bundle.getBoolean(str2) != bundle2.getBoolean(str2)) {
                return false;
            }
        }
        return true;
    }

    public static void applyUserRestrictions(Context context, int i, Bundle bundle, Bundle bundle2) {
        for (String str : USER_RESTRICTIONS) {
            boolean z = bundle.getBoolean(str);
            if (z != bundle2.getBoolean(str)) {
                applyUserRestriction(context, i, str, z);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static void applyUserRestriction(Context context, int i, String str, boolean z) {
        char c;
        ContentResolver contentResolver = context.getContentResolver();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int i2 = 1;
            switch (str.hashCode()) {
                case -1475388515:
                    if (str.equals("no_ambient_display")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case -1387500078:
                    if (str.equals("no_control_apps")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -1315771401:
                    if (str.equals("ensure_verify_apps")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1145953970:
                    if (str.equals("no_install_unknown_sources_globally")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1082175374:
                    if (str.equals("no_airplane_mode")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case -6578707:
                    if (str.equals("no_uninstall_apps")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case 387189153:
                    if (str.equals("no_install_unknown_sources")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 721128150:
                    if (str.equals("no_run_in_background")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 928851522:
                    if (str.equals("no_data_roaming")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 995816019:
                    if (str.equals("no_share_location")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1095593830:
                    if (str.equals("no_safe_boot")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 1760762284:
                    if (str.equals("no_debugging_features")) {
                        c = 2;
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
                    if (z) {
                        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
                        if (activeSubscriptionInfoList != null) {
                            Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
                            while (it.hasNext()) {
                                Settings.Global.putStringForUser(contentResolver, "data_roaming" + it.next().getSubscriptionId(), "0", i);
                            }
                        }
                        Settings.Global.putStringForUser(contentResolver, "data_roaming", "0", i);
                        break;
                    }
                    break;
                case 1:
                    if (z) {
                        Settings.Secure.putIntForUser(contentResolver, "location_mode", 0, i);
                        break;
                    }
                    break;
                case 2:
                    if (z && i == 0) {
                        Settings.Global.putStringForUser(contentResolver, "adb_enabled", "0", i);
                        Settings.Global.putStringForUser(contentResolver, "adb_wifi_enabled", "0", i);
                        break;
                    }
                    break;
                case 3:
                    if (z) {
                        Settings.Global.putStringForUser(context.getContentResolver(), "verifier_verify_adb_installs", "1", i);
                        break;
                    }
                    break;
                case 4:
                    setInstallMarketAppsRestriction(contentResolver, i, getNewUserRestrictionSetting(context, i, "no_install_unknown_sources", z));
                    break;
                case 5:
                    setInstallMarketAppsRestriction(contentResolver, i, getNewUserRestrictionSetting(context, i, "no_install_unknown_sources_globally", z));
                    break;
                case 6:
                    if (z && ActivityManager.getCurrentUser() != i && i != 0) {
                        try {
                            ActivityManager.getService().stopUser(i, false, (IStopUserCallback) null);
                            break;
                        } catch (RemoteException e) {
                            throw e.rethrowAsRuntimeException();
                        }
                    }
                    break;
                case 7:
                    ContentResolver contentResolver2 = context.getContentResolver();
                    if (!z) {
                        i2 = 0;
                    }
                    Settings.Global.putInt(contentResolver2, "safe_boot_disallowed", i2);
                    break;
                case '\b':
                    if (z) {
                        if (Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 1) {
                            i2 = 0;
                        }
                        if (i2 != 0) {
                            Settings.Global.putInt(context.getContentResolver(), "airplane_mode_on", 0);
                            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
                            intent.putExtra("state", false);
                            context.sendBroadcastAsUser(intent, UserHandle.ALL);
                            break;
                        }
                    }
                    break;
                case '\t':
                    if (z) {
                        new AmbientDisplayConfiguration(context).disableDozeSettings(i);
                        break;
                    }
                    break;
                case '\n':
                case 11:
                    PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                    packageManagerInternal.removeAllNonSystemPackageSuspensions(i);
                    packageManagerInternal.removeAllDistractingPackageRestrictions(i);
                    packageManagerInternal.flushPackageRestrictions(i);
                    break;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static boolean isSettingRestrictedForUser(Context context, String str, int i, String str2, int i2) {
        String str3;
        Objects.requireNonNull(str);
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        char c = 65535;
        switch (str.hashCode()) {
            case -2099894345:
                if (str.equals("adb_wifi_enabled")) {
                    c = 0;
                    break;
                }
                break;
            case -1796809747:
                if (str.equals("location_mode")) {
                    c = 1;
                    break;
                }
                break;
            case -1490222856:
                if (str.equals("doze_enabled")) {
                    c = 2;
                    break;
                }
                break;
            case -1115710219:
                if (str.equals("verifier_verify_adb_installs")) {
                    c = 3;
                    break;
                }
                break;
            case -970351711:
                if (str.equals("adb_enabled")) {
                    c = 4;
                    break;
                }
                break;
            case -693072130:
                if (str.equals("screen_brightness_mode")) {
                    c = 5;
                    break;
                }
                break;
            case -623873498:
                if (str.equals("always_on_vpn_app")) {
                    c = 6;
                    break;
                }
                break;
            case -416662510:
                if (str.equals("preferred_network_mode")) {
                    c = 7;
                    break;
                }
                break;
            case -101820922:
                if (str.equals("doze_always_on")) {
                    c = '\b';
                    break;
                }
                break;
            case -32505807:
                if (str.equals("doze_pulse_on_long_press")) {
                    c = '\t';
                    break;
                }
                break;
            case -16943007:
                if (str.equals("screen_brightness_float")) {
                    c = '\n';
                    break;
                }
                break;
            case 58027029:
                if (str.equals("safe_boot_disallowed")) {
                    c = 11;
                    break;
                }
                break;
            case 258514750:
                if (str.equals("screen_off_timeout")) {
                    c = '\f';
                    break;
                }
                break;
            case 683724341:
                if (str.equals("private_dns_mode")) {
                    c = '\r';
                    break;
                }
                break;
            case 926123534:
                if (str.equals("airplane_mode_on")) {
                    c = 14;
                    break;
                }
                break;
            case 1073289638:
                if (str.equals("doze_pulse_on_double_tap")) {
                    c = 15;
                    break;
                }
                break;
            case 1223734380:
                if (str.equals("private_dns_specifier")) {
                    c = 16;
                    break;
                }
                break;
            case 1275530062:
                if (str.equals("auto_time_zone")) {
                    c = 17;
                    break;
                }
                break;
            case 1334097968:
                if (str.equals("always_on_vpn_lockdown_whitelist")) {
                    c = 18;
                    break;
                }
                break;
            case 1602982312:
                if (str.equals("doze_pulse_on_pick_up")) {
                    c = 19;
                    break;
                }
                break;
            case 1646894952:
                if (str.equals("always_on_vpn_lockdown")) {
                    c = 20;
                    break;
                }
                break;
            case 1661297501:
                if (str.equals("auto_time")) {
                    c = 21;
                    break;
                }
                break;
            case 1701140351:
                if (str.equals("install_non_market_apps")) {
                    c = 22;
                    break;
                }
                break;
            case 1735689732:
                if (str.equals("screen_brightness")) {
                    c = 23;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 4:
                if (!"0".equals(str2)) {
                    str3 = "no_debugging_features";
                    break;
                } else {
                    return false;
                }
            case 1:
                if (!userManager.hasUserRestriction("no_config_location", UserHandle.of(i)) || i2 == 1000) {
                    if (!String.valueOf(0).equals(str2)) {
                        str3 = "no_share_location";
                        break;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            case 2:
            case '\b':
            case '\t':
            case 15:
            case 19:
                if (!"0".equals(str2)) {
                    str3 = "no_ambient_display";
                    break;
                } else {
                    return false;
                }
            case 3:
                if (!"1".equals(str2)) {
                    str3 = "ensure_verify_apps";
                    break;
                } else {
                    return false;
                }
            case 5:
            case '\n':
            case 23:
                if (i2 != 1000) {
                    str3 = "no_config_brightness";
                    break;
                } else {
                    return false;
                }
            case 6:
            case 18:
            case 20:
                int appId = UserHandle.getAppId(i2);
                if (appId != 1000 && appId != 0) {
                    str3 = "no_config_vpn";
                    break;
                } else {
                    return false;
                }
            case 7:
                str3 = "no_config_mobile_networks";
                break;
            case 11:
                if (!"1".equals(str2)) {
                    str3 = "no_safe_boot";
                    break;
                } else {
                    return false;
                }
            case '\f':
                if (i2 != 1000) {
                    str3 = "no_config_screen_timeout";
                    break;
                } else {
                    return false;
                }
            case '\r':
            case 16:
                if (i2 != 1000) {
                    str3 = "disallow_config_private_dns";
                    break;
                } else {
                    return false;
                }
            case 14:
                if (!"0".equals(str2)) {
                    str3 = "no_airplane_mode";
                    break;
                } else {
                    return false;
                }
            case 17:
            case 21:
                if (i2 != 1000) {
                    str3 = "no_config_date_time";
                    break;
                } else {
                    return false;
                }
            case 22:
                if (!"0".equals(str2)) {
                    str3 = "no_install_unknown_sources";
                    break;
                } else {
                    return false;
                }
            default:
                if (str.startsWith("data_roaming") && !"0".equals(str2)) {
                    str3 = "no_data_roaming";
                    break;
                } else {
                    return false;
                }
                break;
        }
        return userManager.hasUserRestriction(str3, UserHandle.of(i));
    }

    public static void dumpRestrictions(PrintWriter printWriter, String str, Bundle bundle) {
        if (bundle != null) {
            boolean z = true;
            for (String str2 : bundle.keySet()) {
                if (bundle.getBoolean(str2, false)) {
                    printWriter.println(str + str2);
                    z = false;
                }
            }
            if (z) {
                printWriter.println(str + "none");
                return;
            }
            return;
        }
        printWriter.println(str + "null");
    }

    public static boolean restrictionsChanged(Bundle bundle, Bundle bundle2, String... strArr) {
        if (strArr.length == 0) {
            return areEqual(bundle, bundle2);
        }
        for (String str : strArr) {
            if (bundle.getBoolean(str, false) != bundle2.getBoolean(str, false)) {
                return true;
            }
        }
        return false;
    }

    public static void setInstallMarketAppsRestriction(ContentResolver contentResolver, int i, int i2) {
        Settings.Secure.putIntForUser(contentResolver, "install_non_market_apps", i2, i);
    }

    public static int getNewUserRestrictionSetting(Context context, int i, String str, boolean z) {
        return (z || UserManager.get(context).hasUserRestriction(str, UserHandle.of(i))) ? 0 : 1;
    }
}
