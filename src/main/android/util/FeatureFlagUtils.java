package android.util;

import android.content.Context;
import android.p008os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public class FeatureFlagUtils {
    private static final Map<String, String> DEFAULT_FLAGS;
    public static final String FFLAG_OVERRIDE_PREFIX = "sys.fflag.override.";
    public static final String FFLAG_PREFIX = "sys.fflag.";
    public static final String HEARING_AID_SETTINGS = "settings_bluetooth_hearing_aid";
    private static final Set<String> PERSISTENT_FLAGS;
    public static final String PERSIST_PREFIX = "persist.sys.fflag.override.";
    public static final String SETTINGS_ACCESSIBILITY_HEARING_AID_PAGE = "settings_accessibility_hearing_aid_page";
    public static final String SETTINGS_ADB_METRICS_WRITER = "settings_adb_metrics_writer";
    public static final String SETTINGS_APP_ALLOW_DARK_THEME_ACTIVATION_AT_BEDTIME = "settings_app_allow_dark_theme_activation_at_bedtime";
    public static final String SETTINGS_APP_LOCALE_OPT_IN_ENABLED = "settings_app_locale_opt_in_enabled";
    public static final String SETTINGS_AUDIO_ROUTING = "settings_audio_routing";
    public static final String SETTINGS_AUTO_TEXT_WRAPPING = "settings_auto_text_wrapping";
    public static final String SETTINGS_BIOMETRICS2_ENROLLMENT = "settings_biometrics2_enrollment";
    public static final String SETTINGS_DO_NOT_RESTORE_PRESERVED = "settings_do_not_restore_preserved";
    public static final String SETTINGS_ENABLE_LOCKSCREEN_TRANSFER_API = "settings_enable_lockscreen_transfer_api";
    public static final String SETTINGS_ENABLE_MONITOR_PHANTOM_PROCS = "settings_enable_monitor_phantom_procs";
    public static final String SETTINGS_ENABLE_SECURITY_HUB = "settings_enable_security_hub";
    public static final String SETTINGS_ENABLE_SPA = "settings_enable_spa";
    public static final String SETTINGS_ENABLE_SPA_PHASE2 = "settings_enable_spa_phase2";
    public static final String SETTINGS_FLASH_NOTIFICATIONS = "settings_flash_notifications";
    public static final String SETTINGS_NEED_CONNECTED_BLE_DEVICE_FOR_BROADCAST = "settings_need_connected_ble_device_for_broadcast";
    public static final String SETTINGS_NEW_KEYBOARD_MODIFIER_KEY = "settings_new_keyboard_modifier_key";
    public static final String SETTINGS_NEW_KEYBOARD_TRACKPAD = "settings_new_keyboard_trackpad";
    public static final String SETTINGS_NEW_KEYBOARD_TRACKPAD_GESTURE = "settings_new_keyboard_trackpad_gesture";
    public static final String SETTINGS_NEW_KEYBOARD_UI = "settings_new_keyboard_ui";
    public static final String SETTINGS_PREFER_ACCESSIBILITY_MENU_IN_SYSTEM = "settings_prefer_accessibility_menu_in_system";
    public static final String SETTINGS_REMOTE_DEVICE_CREDENTIAL_VALIDATION = "settings_remote_device_credential_validation";
    public static final String SETTINGS_SHOW_STYLUS_PREFERENCES = "settings_show_stylus_preferences";
    public static final String SETTINGS_SHOW_UDFPS_ENROLL_IN_SETTINGS = "settings_show_udfps_enroll_in_settings";
    public static final String SETTINGS_SUPPORT_LARGE_SCREEN = "settings_support_large_screen";
    public static final String SETTINGS_USE_NEW_BACKUP_ELIGIBILITY_RULES = "settings_use_new_backup_eligibility_rules";
    public static final String SETTINGS_VOLUME_PANEL_IN_SYSTEMUI = "settings_volume_panel_in_systemui";
    public static final String SETTINGS_WIFITRACKER2 = "settings_wifitracker2";

    static {
        HashMap hashMap = new HashMap();
        DEFAULT_FLAGS = hashMap;
        hashMap.put("settings_audio_switcher", "true");
        hashMap.put("settings_systemui_theme", "true");
        hashMap.put(HEARING_AID_SETTINGS, "false");
        hashMap.put("settings_wifi_details_datausage_header", "false");
        hashMap.put("settings_skip_direction_mutable", "true");
        hashMap.put(SETTINGS_WIFITRACKER2, "true");
        hashMap.put("settings_controller_loading_enhancement", "true");
        hashMap.put("settings_conditionals", "false");
        hashMap.put(SETTINGS_DO_NOT_RESTORE_PRESERVED, "true");
        hashMap.put("settings_tether_all_in_one", "false");
        hashMap.put("settings_contextual_home", "false");
        hashMap.put(SETTINGS_USE_NEW_BACKUP_ELIGIBILITY_RULES, "true");
        hashMap.put(SETTINGS_ENABLE_SECURITY_HUB, "true");
        hashMap.put(SETTINGS_SUPPORT_LARGE_SCREEN, "true");
        hashMap.put("settings_search_always_expand", "true");
        hashMap.put(SETTINGS_APP_LOCALE_OPT_IN_ENABLED, "true");
        hashMap.put(SETTINGS_VOLUME_PANEL_IN_SYSTEMUI, "false");
        hashMap.put(SETTINGS_ENABLE_MONITOR_PHANTOM_PROCS, "true");
        hashMap.put(SETTINGS_APP_ALLOW_DARK_THEME_ACTIVATION_AT_BEDTIME, "true");
        hashMap.put(SETTINGS_NEED_CONNECTED_BLE_DEVICE_FOR_BROADCAST, "true");
        hashMap.put(SETTINGS_AUTO_TEXT_WRAPPING, "false");
        hashMap.put(SETTINGS_NEW_KEYBOARD_UI, "false");
        hashMap.put(SETTINGS_NEW_KEYBOARD_MODIFIER_KEY, "false");
        hashMap.put(SETTINGS_NEW_KEYBOARD_TRACKPAD, "false");
        hashMap.put(SETTINGS_NEW_KEYBOARD_TRACKPAD_GESTURE, "false");
        hashMap.put(SETTINGS_ENABLE_SPA, "true");
        hashMap.put(SETTINGS_ENABLE_SPA_PHASE2, "false");
        hashMap.put(SETTINGS_ADB_METRICS_WRITER, "false");
        hashMap.put(SETTINGS_SHOW_STYLUS_PREFERENCES, "true");
        hashMap.put(SETTINGS_BIOMETRICS2_ENROLLMENT, "false");
        hashMap.put(SETTINGS_ACCESSIBILITY_HEARING_AID_PAGE, "false");
        hashMap.put(SETTINGS_PREFER_ACCESSIBILITY_MENU_IN_SYSTEM, "false");
        hashMap.put(SETTINGS_AUDIO_ROUTING, "false");
        hashMap.put(SETTINGS_FLASH_NOTIFICATIONS, "true");
        hashMap.put(SETTINGS_SHOW_UDFPS_ENROLL_IN_SETTINGS, "true");
        hashMap.put(SETTINGS_ENABLE_LOCKSCREEN_TRANSFER_API, "false");
        hashMap.put(SETTINGS_REMOTE_DEVICE_CREDENTIAL_VALIDATION, "false");
        HashSet hashSet = new HashSet();
        PERSISTENT_FLAGS = hashSet;
        hashSet.add(SETTINGS_APP_LOCALE_OPT_IN_ENABLED);
        hashSet.add(SETTINGS_SUPPORT_LARGE_SCREEN);
        hashSet.add(SETTINGS_ENABLE_MONITOR_PHANTOM_PROCS);
        hashSet.add(SETTINGS_APP_ALLOW_DARK_THEME_ACTIVATION_AT_BEDTIME);
        hashSet.add(SETTINGS_AUTO_TEXT_WRAPPING);
        hashSet.add(SETTINGS_NEW_KEYBOARD_UI);
        hashSet.add(SETTINGS_NEW_KEYBOARD_MODIFIER_KEY);
        hashSet.add(SETTINGS_NEW_KEYBOARD_TRACKPAD);
        hashSet.add(SETTINGS_NEW_KEYBOARD_TRACKPAD_GESTURE);
        hashSet.add(SETTINGS_ENABLE_SPA);
        hashSet.add(SETTINGS_ENABLE_SPA_PHASE2);
        hashSet.add(SETTINGS_PREFER_ACCESSIBILITY_MENU_IN_SYSTEM);
    }

    public static boolean isEnabled(Context context, String feature) {
        if (context != null) {
            String value = Settings.Global.getString(context.getContentResolver(), feature);
            if (!TextUtils.isEmpty(value)) {
                return Boolean.parseBoolean(value);
            }
        }
        String value2 = SystemProperties.get(getSystemPropertyPrefix(feature) + feature);
        if (!TextUtils.isEmpty(value2)) {
            return Boolean.parseBoolean(value2);
        }
        return Boolean.parseBoolean(getAllFeatureFlags().get(feature));
    }

    public static void setEnabled(Context context, String feature, boolean enabled) {
        SystemProperties.set(getSystemPropertyPrefix(feature) + feature, enabled ? "true" : "false");
    }

    public static Map<String, String> getAllFeatureFlags() {
        return DEFAULT_FLAGS;
    }

    private static String getSystemPropertyPrefix(String feature) {
        return PERSISTENT_FLAGS.contains(feature) ? PERSIST_PREFIX : FFLAG_OVERRIDE_PREFIX;
    }
}
