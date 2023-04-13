package android.provider;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.location.ILocationManager;
import android.media.AudioSystem;
import android.media.MediaMetrics;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.LocaleList;
import android.p008os.Process;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MemoryIntArray;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public final class Settings {
    public static final String ACTION_ACCESSIBILITY_COLOR_MOTION_SETTINGS = "android.settings.ACCESSIBILITY_COLOR_MOTION_SETTINGS";
    @SystemApi
    public static final String ACTION_ACCESSIBILITY_DETAILS_SETTINGS = "android.settings.ACCESSIBILITY_DETAILS_SETTINGS";
    public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
    public static final String ACTION_ADD_ACCOUNT = "android.settings.ADD_ACCOUNT_SETTINGS";
    public static final String ACTION_ADVANCED_MEMORY_PROTECTION_SETTINGS = "android.settings.ADVANCED_MEMORY_PROTECTION_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_SETTINGS = "android.settings.AIRPLANE_MODE_SETTINGS";
    public static final String ACTION_ALL_APPS_NOTIFICATION_SETTINGS = "android.settings.ALL_APPS_NOTIFICATION_SETTINGS";
    public static final String ACTION_ALL_APPS_NOTIFICATION_SETTINGS_FOR_REVIEW = "android.settings.ALL_APPS_NOTIFICATION_SETTINGS_FOR_REVIEW";
    public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String ACTION_APPLICATION_DEVELOPMENT_SETTINGS = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS";
    public static final String ACTION_APPLICATION_SETTINGS = "android.settings.APPLICATION_SETTINGS";
    public static final String ACTION_APP_LOCALE_SETTINGS = "android.settings.APP_LOCALE_SETTINGS";
    public static final String ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS = "android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS";
    public static final String ACTION_APP_NOTIFICATION_REDACTION = "android.settings.ACTION_APP_NOTIFICATION_REDACTION";
    public static final String ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS";
    public static final String ACTION_APP_OPEN_BY_DEFAULT_SETTINGS = "android.settings.APP_OPEN_BY_DEFAULT_SETTINGS";
    public static final String ACTION_APP_OPS_SETTINGS = "android.settings.APP_OPS_SETTINGS";
    public static final String ACTION_APP_SEARCH_SETTINGS = "android.settings.APP_SEARCH_SETTINGS";
    public static final String ACTION_APP_USAGE_SETTINGS = "android.settings.action.APP_USAGE_SETTINGS";
    public static final String ACTION_ASSIST_GESTURE_SETTINGS = "android.settings.ASSIST_GESTURE_SETTINGS";
    public static final String ACTION_AUTO_ROTATE_SETTINGS = "android.settings.AUTO_ROTATE_SETTINGS";
    public static final String ACTION_BATTERY_SAVER_SETTINGS = "android.settings.BATTERY_SAVER_SETTINGS";
    @SystemApi
    public static final String ACTION_BEDTIME_SETTINGS = "android.settings.BEDTIME_SETTINGS";
    public static final String ACTION_BIOMETRIC_ENROLL = "android.settings.BIOMETRIC_ENROLL";
    public static final String ACTION_BLUETOOTH_PAIRING_SETTINGS = "android.settings.BLUETOOTH_PAIRING_SETTINGS";
    public static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS";
    @SystemApi
    public static final String ACTION_BUGREPORT_HANDLER_SETTINGS = "android.settings.BUGREPORT_HANDLER_SETTINGS";
    public static final String ACTION_CAPTIONING_SETTINGS = "android.settings.CAPTIONING_SETTINGS";
    public static final String ACTION_CAST_SETTINGS = "android.settings.CAST_SETTINGS";
    public static final String ACTION_CHANNEL_NOTIFICATION_SETTINGS = "android.settings.CHANNEL_NOTIFICATION_SETTINGS";
    public static final String ACTION_COLOR_CORRECTION_SETTINGS = "com.android.settings.ACCESSIBILITY_COLOR_SPACE_SETTINGS";
    public static final String ACTION_COLOR_INVERSION_SETTINGS = "android.settings.COLOR_INVERSION_SETTINGS";
    public static final String ACTION_CONDITION_PROVIDER_SETTINGS = "android.settings.ACTION_CONDITION_PROVIDER_SETTINGS";
    public static final String ACTION_CONVERSATION_SETTINGS = "android.settings.CONVERSATION_SETTINGS";
    public static final String ACTION_DARK_THEME_SETTINGS = "android.settings.DARK_THEME_SETTINGS";
    public static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS";
    public static final String ACTION_DATA_SAVER_SETTINGS = "android.settings.DATA_SAVER_SETTINGS";
    public static final String ACTION_DATA_USAGE_SETTINGS = "android.settings.DATA_USAGE_SETTINGS";
    public static final String ACTION_DATE_SETTINGS = "android.settings.DATE_SETTINGS";
    public static final String ACTION_DEVICE_CONTROLS_SETTINGS = "android.settings.ACTION_DEVICE_CONTROLS_SETTINGS";
    public static final String ACTION_DEVICE_INFO_SETTINGS = "android.settings.DEVICE_INFO_SETTINGS";
    public static final String ACTION_DISPLAY_SETTINGS = "android.settings.DISPLAY_SETTINGS";
    public static final String ACTION_DREAM_SETTINGS = "android.settings.DREAM_SETTINGS";
    public static final String ACTION_ENABLE_MMS_DATA_REQUEST = "android.settings.ENABLE_MMS_DATA_REQUEST";
    @SystemApi
    public static final String ACTION_ENTERPRISE_PRIVACY_SETTINGS = "android.settings.ENTERPRISE_PRIVACY_SETTINGS";
    @Deprecated
    public static final String ACTION_FINGERPRINT_ENROLL = "android.settings.FINGERPRINT_ENROLL";
    public static final String ACTION_FOREGROUND_SERVICES_SETTINGS = "android.settings.FOREGROUND_SERVICES_SETTINGS";
    public static final String ACTION_HARD_KEYBOARD_SETTINGS = "android.settings.HARD_KEYBOARD_SETTINGS";
    public static final String ACTION_HOME_SETTINGS = "android.settings.HOME_SETTINGS";
    public static final String ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS = "android.settings.IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS";
    public static final String ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SETTINGS = "android.settings.INPUT_METHOD_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SUBTYPE_SETTINGS = "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS";
    public static final String ACTION_INTERNAL_STORAGE_SETTINGS = "android.settings.INTERNAL_STORAGE_SETTINGS";
    public static final String ACTION_LOCALE_SETTINGS = "android.settings.LOCALE_SETTINGS";
    @SystemApi
    public static final String ACTION_LOCATION_CONTROLLER_EXTRA_PACKAGE_SETTINGS = "android.settings.LOCATION_CONTROLLER_EXTRA_PACKAGE_SETTINGS";
    public static final String ACTION_LOCATION_SCANNING_SETTINGS = "android.settings.LOCATION_SCANNING_SETTINGS";
    public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
    public static final String ACTION_LOCKSCREEN_SETTINGS = "android.settings.LOCK_SCREEN_SETTINGS";
    public static final String ACTION_MANAGED_PROFILE_SETTINGS = "android.settings.MANAGED_PROFILE_SETTINGS";
    public static final String ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS = "android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS";
    public static final String ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION = "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION";
    public static final String ACTION_MANAGE_ALL_SIM_PROFILES_SETTINGS = "android.settings.MANAGE_ALL_SIM_PROFILES_SETTINGS";
    public static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS";
    public static final String ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION = "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION";
    public static final String ACTION_MANAGE_APP_LONG_RUNNING_JOBS = "android.settings.MANAGE_APP_LONG_RUNNING_JOBS";
    @SystemApi
    public static final String ACTION_MANAGE_APP_OVERLAY_PERMISSION = "android.settings.MANAGE_APP_OVERLAY_PERMISSION";
    public static final String ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT = "android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENT";
    public static final String ACTION_MANAGE_CLONED_APPS_SETTINGS = "android.settings.MANAGE_CLONED_APPS_SETTINGS";
    public static final String ACTION_MANAGE_CROSS_PROFILE_ACCESS = "android.settings.MANAGE_CROSS_PROFILE_ACCESS";
    public static final String ACTION_MANAGE_DEFAULT_APPS_SETTINGS = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS";
    @SystemApi
    public static final String ACTION_MANAGE_DOMAIN_URLS = "android.settings.MANAGE_DOMAIN_URLS";
    @SystemApi
    public static final String ACTION_MANAGE_MORE_DEFAULT_APPS_SETTINGS = "android.settings.MANAGE_MORE_DEFAULT_APPS_SETTINGS";
    public static final String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
    public static final String ACTION_MANAGE_SUPERVISOR_RESTRICTED_SETTING = "android.settings.MANAGE_SUPERVISOR_RESTRICTED_SETTING";
    public static final String ACTION_MANAGE_UNKNOWN_APP_SOURCES = "android.settings.MANAGE_UNKNOWN_APP_SOURCES";
    public static final String ACTION_MANAGE_WRITE_SETTINGS = "android.settings.action.MANAGE_WRITE_SETTINGS";
    public static final String ACTION_MEDIA_CONTROLS_SETTINGS = "android.settings.ACTION_MEDIA_CONTROLS_SETTINGS";
    public static final String ACTION_MEMORY_CARD_SETTINGS = "android.settings.MEMORY_CARD_SETTINGS";
    public static final String ACTION_MMS_MESSAGE_SETTING = "android.settings.MMS_MESSAGE_SETTING";
    public static final String ACTION_MOBILE_DATA_USAGE = "android.settings.MOBILE_DATA_USAGE";
    public static final String ACTION_MONITORING_CERT_INFO = "com.android.settings.MONITORING_CERT_INFO";
    public static final String ACTION_NETWORK_OPERATOR_SETTINGS = "android.settings.NETWORK_OPERATOR_SETTINGS";
    public static final String ACTION_NFCSHARING_SETTINGS = "android.settings.NFCSHARING_SETTINGS";
    public static final String ACTION_NFC_PAYMENT_SETTINGS = "android.settings.NFC_PAYMENT_SETTINGS";
    public static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS";
    public static final String ACTION_NIGHT_DISPLAY_SETTINGS = "android.settings.NIGHT_DISPLAY_SETTINGS";
    public static final String ACTION_NOTIFICATION_ASSISTANT_SETTINGS = "android.settings.NOTIFICATION_ASSISTANT_SETTINGS";
    public static final String ACTION_NOTIFICATION_HISTORY = "android.settings.NOTIFICATION_HISTORY";
    public static final String ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS = "android.settings.NOTIFICATION_LISTENER_DETAIL_SETTINGS";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    @SystemApi
    public static final String ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS = "android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS";
    public static final String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS = "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS";
    public static final String ACTION_NOTIFICATION_SETTINGS = "android.settings.NOTIFICATION_SETTINGS";
    public static final String ACTION_ONE_HANDED_SETTINGS = "android.settings.action.ONE_HANDED_SETTINGS";
    public static final String ACTION_PAIRING_SETTINGS = "android.settings.PAIRING_SETTINGS";
    public static final String ACTION_PICTURE_IN_PICTURE_SETTINGS = "android.settings.PICTURE_IN_PICTURE_SETTINGS";
    public static final String ACTION_POWER_MENU_SETTINGS = "android.settings.ACTION_POWER_MENU_SETTINGS";
    public static final String ACTION_PRINT_SETTINGS = "android.settings.ACTION_PRINT_SETTINGS";
    public static final String ACTION_PRIVACY_SETTINGS = "android.settings.PRIVACY_SETTINGS";
    public static final String ACTION_PROCESS_WIFI_EASY_CONNECT_URI = "android.settings.PROCESS_WIFI_EASY_CONNECT_URI";
    public static final String ACTION_QUICK_ACCESS_WALLET_SETTINGS = "android.settings.QUICK_ACCESS_WALLET_SETTINGS";
    public static final String ACTION_QUICK_LAUNCH_SETTINGS = "android.settings.QUICK_LAUNCH_SETTINGS";
    public static final String ACTION_REDUCE_BRIGHT_COLORS_SETTINGS = "android.settings.REDUCE_BRIGHT_COLORS_SETTINGS";
    public static final String ACTION_REGIONAL_PREFERENCES_SETTINGS = "android.settings.REGIONAL_PREFERENCES_SETTINGS";
    @SystemApi
    public static final String ACTION_REQUEST_ENABLE_CONTENT_CAPTURE = "android.settings.REQUEST_ENABLE_CONTENT_CAPTURE";
    public static final String ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";
    public static final String ACTION_REQUEST_MANAGE_MEDIA = "android.settings.REQUEST_MANAGE_MEDIA";
    public static final String ACTION_REQUEST_SCHEDULE_EXACT_ALARM = "android.settings.REQUEST_SCHEDULE_EXACT_ALARM";
    public static final String ACTION_REQUEST_SET_AUTOFILL_SERVICE = "android.settings.REQUEST_SET_AUTOFILL_SERVICE";
    public static final String ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS";
    public static final String ACTION_SECURITY_SETTINGS = "android.settings.SECURITY_SETTINGS";
    public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
    public static final String ACTION_SETTINGS_EMBED_DEEP_LINK_ACTIVITY = "android.settings.SETTINGS_EMBED_DEEP_LINK_ACTIVITY";
    @SystemApi
    public static final String ACTION_SHOW_ADMIN_SUPPORT_DETAILS = "android.settings.SHOW_ADMIN_SUPPORT_DETAILS";
    public static final String ACTION_SHOW_REGULATORY_INFO = "android.settings.SHOW_REGULATORY_INFO";
    public static final String ACTION_SHOW_REMOTE_BUGREPORT_DIALOG = "android.settings.SHOW_REMOTE_BUGREPORT_DIALOG";
    @SystemApi
    public static final String ACTION_SHOW_RESTRICTED_SETTING_DIALOG = "android.settings.SHOW_RESTRICTED_SETTING_DIALOG";
    public static final String ACTION_SHOW_WORK_POLICY_INFO = "android.settings.SHOW_WORK_POLICY_INFO";
    public static final String ACTION_SOUND_SETTINGS = "android.settings.SOUND_SETTINGS";
    public static final String ACTION_STORAGE_MANAGER_SETTINGS = "android.settings.STORAGE_MANAGER_SETTINGS";
    @Deprecated
    public static final String ACTION_STORAGE_VOLUME_ACCESS_SETTINGS = "android.settings.STORAGE_VOLUME_ACCESS_SETTINGS";
    public static final String ACTION_SYNC_SETTINGS = "android.settings.SYNC_SETTINGS";
    public static final String ACTION_SYSTEM_UPDATE_SETTINGS = "android.settings.SYSTEM_UPDATE_SETTINGS";
    @SystemApi
    public static final String ACTION_TETHER_PROVISIONING_UI = "android.settings.TETHER_PROVISIONING_UI";
    @SystemApi
    public static final String ACTION_TETHER_SETTINGS = "android.settings.TETHER_SETTINGS";
    @SystemApi
    public static final String ACTION_TETHER_UNSUPPORTED_CARRIER_UI = "android.settings.TETHER_UNSUPPORTED_CARRIER_UI";
    public static final String ACTION_TEXT_READING_SETTINGS = "android.settings.TEXT_READING_SETTINGS";
    public static final String ACTION_TRUSTED_CREDENTIALS_USER = "com.android.settings.TRUSTED_CREDENTIALS_USER";
    public static final String ACTION_USAGE_ACCESS_SETTINGS = "android.settings.USAGE_ACCESS_SETTINGS";
    public static final String ACTION_USER_DICTIONARY_INSERT = "com.android.settings.USER_DICTIONARY_INSERT";
    public static final String ACTION_USER_DICTIONARY_SETTINGS = "android.settings.USER_DICTIONARY_SETTINGS";
    @SystemApi
    public static final String ACTION_USER_SETTINGS = "android.settings.USER_SETTINGS";
    public static final String ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL = "android.settings.VIEW_ADVANCED_POWER_USAGE_DETAIL";
    public static final String ACTION_VOICE_CONTROL_AIRPLANE_MODE = "android.settings.VOICE_CONTROL_AIRPLANE_MODE";
    public static final String ACTION_VOICE_CONTROL_BATTERY_SAVER_MODE = "android.settings.VOICE_CONTROL_BATTERY_SAVER_MODE";
    public static final String ACTION_VOICE_CONTROL_DO_NOT_DISTURB_MODE = "android.settings.VOICE_CONTROL_DO_NOT_DISTURB_MODE";
    public static final String ACTION_VOICE_INPUT_SETTINGS = "android.settings.VOICE_INPUT_SETTINGS";
    public static final String ACTION_VPN_SETTINGS = "android.settings.VPN_SETTINGS";
    public static final String ACTION_VR_LISTENER_SETTINGS = "android.settings.VR_LISTENER_SETTINGS";
    public static final String ACTION_WEBVIEW_SETTINGS = "android.settings.WEBVIEW_SETTINGS";
    public static final String ACTION_WIFI_ADD_NETWORKS = "android.settings.WIFI_ADD_NETWORKS";
    public static final String ACTION_WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS";
    public static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
    public static final String ACTION_WIFI_TETHER_SETTING = "com.android.settings.WIFI_TETHER_SETTINGS";
    public static final String ACTION_WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS";
    public static final String ACTION_ZEN_MODE_AUTOMATION_SETTINGS = "android.settings.ZEN_MODE_AUTOMATION_SETTINGS";
    public static final String ACTION_ZEN_MODE_EVENT_RULE_SETTINGS = "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS";
    public static final String ACTION_ZEN_MODE_EXTERNAL_RULE_SETTINGS = "android.settings.ZEN_MODE_EXTERNAL_RULE_SETTINGS";
    public static final String ACTION_ZEN_MODE_PRIORITY_SETTINGS = "android.settings.ZEN_MODE_PRIORITY_SETTINGS";
    public static final String ACTION_ZEN_MODE_SCHEDULE_RULE_SETTINGS = "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS";
    public static final String ACTION_ZEN_MODE_SETTINGS = "android.settings.ZEN_MODE_SETTINGS";
    public static final int ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED = 1;
    public static final int ADD_WIFI_RESULT_ALREADY_EXISTS = 2;
    public static final int ADD_WIFI_RESULT_SUCCESS = 0;
    public static final String AUTHORITY = "settings";
    public static final String CALL_METHOD_DELETE_CONFIG = "DELETE_config";
    public static final String CALL_METHOD_DELETE_GLOBAL = "DELETE_global";
    public static final String CALL_METHOD_DELETE_SECURE = "DELETE_secure";
    public static final String CALL_METHOD_DELETE_SYSTEM = "DELETE_system";
    public static final String CALL_METHOD_FLAGS_KEY = "_flags";
    public static final String CALL_METHOD_GENERATION_INDEX_KEY = "_generation_index";
    public static final String CALL_METHOD_GENERATION_KEY = "_generation";
    public static final String CALL_METHOD_GET_CONFIG = "GET_config";
    public static final String CALL_METHOD_GET_GLOBAL = "GET_global";
    public static final String CALL_METHOD_GET_SECURE = "GET_secure";
    public static final String CALL_METHOD_GET_SYNC_DISABLED_MODE_CONFIG = "GET_SYNC_DISABLED_MODE_config";
    public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
    public static final String CALL_METHOD_LIST_CONFIG = "LIST_config";
    public static final String CALL_METHOD_LIST_GLOBAL = "LIST_global";
    public static final String CALL_METHOD_LIST_SECURE = "LIST_secure";
    public static final String CALL_METHOD_LIST_SYSTEM = "LIST_system";
    public static final String CALL_METHOD_MAKE_DEFAULT_KEY = "_make_default";
    public static final String CALL_METHOD_MONITOR_CALLBACK_KEY = "_monitor_callback_key";
    public static final String CALL_METHOD_OVERRIDEABLE_BY_RESTORE_KEY = "_overrideable_by_restore";
    public static final String CALL_METHOD_PREFIX_KEY = "_prefix";
    public static final String CALL_METHOD_PUT_CONFIG = "PUT_config";
    public static final String CALL_METHOD_PUT_GLOBAL = "PUT_global";
    public static final String CALL_METHOD_PUT_SECURE = "PUT_secure";
    public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";
    public static final String CALL_METHOD_REGISTER_MONITOR_CALLBACK_CONFIG = "REGISTER_MONITOR_CALLBACK_config";
    public static final String CALL_METHOD_RESET_CONFIG = "RESET_config";
    public static final String CALL_METHOD_RESET_GLOBAL = "RESET_global";
    public static final String CALL_METHOD_RESET_MODE_KEY = "_reset_mode";
    public static final String CALL_METHOD_RESET_SECURE = "RESET_secure";
    public static final String CALL_METHOD_SET_ALL_CONFIG = "SET_ALL_config";
    public static final String CALL_METHOD_SET_SYNC_DISABLED_MODE_CONFIG = "SET_SYNC_DISABLED_MODE_config";
    public static final String CALL_METHOD_SYNC_DISABLED_MODE_KEY = "_disabled_mode";
    public static final String CALL_METHOD_TAG_KEY = "_tag";
    public static final String CALL_METHOD_TRACK_GENERATION_KEY = "_track_generation";
    public static final String CALL_METHOD_UNREGISTER_MONITOR_CALLBACK_CONFIG = "UNREGISTER_MONITOR_CALLBACK_config";
    public static final String CALL_METHOD_USER_KEY = "_user";
    public static final boolean DEFAULT_OVERRIDEABLE_BY_RESTORE = false;
    public static final String DEVICE_NAME_SETTINGS = "android.settings.DEVICE_NAME";
    public static final int ENABLE_MMS_DATA_REQUEST_REASON_INCOMING_MMS = 0;
    public static final int ENABLE_MMS_DATA_REQUEST_REASON_OUTGOING_MMS = 1;
    public static final String EXTRA_ACCESS_CALLBACK = "access_callback";
    public static final String EXTRA_ACCOUNT_TYPES = "account_types";
    public static final String EXTRA_AIRPLANE_MODE_ENABLED = "airplane_mode_enabled";
    public static final String EXTRA_APP_PACKAGE = "android.provider.extra.APP_PACKAGE";
    public static final String EXTRA_APP_UID = "app_uid";
    public static final String EXTRA_AUTHORITIES = "authorities";
    public static final String EXTRA_BATTERY_SAVER_MODE_ENABLED = "android.settings.extra.battery_saver_mode_enabled";
    public static final String EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED = "android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED";
    public static final String EXTRA_CALLING_PACKAGE = "calling_package";
    public static final String EXTRA_CHANNEL_FILTER_LIST = "android.provider.extra.CHANNEL_FILTER_LIST";
    public static final String EXTRA_CHANNEL_ID = "android.provider.extra.CHANNEL_ID";
    public static final String EXTRA_CONVERSATION_ID = "android.provider.extra.CONVERSATION_ID";
    public static final String EXTRA_DO_NOT_DISTURB_MODE_ENABLED = "android.settings.extra.do_not_disturb_mode_enabled";
    public static final String EXTRA_DO_NOT_DISTURB_MODE_MINUTES = "android.settings.extra.do_not_disturb_mode_minutes";
    public static final String EXTRA_EASY_CONNECT_ATTEMPTED_SSID = "android.provider.extra.EASY_CONNECT_ATTEMPTED_SSID";
    public static final String EXTRA_EASY_CONNECT_BAND_LIST = "android.provider.extra.EASY_CONNECT_BAND_LIST";
    public static final String EXTRA_EASY_CONNECT_CHANNEL_LIST = "android.provider.extra.EASY_CONNECT_CHANNEL_LIST";
    public static final String EXTRA_EASY_CONNECT_ERROR_CODE = "android.provider.extra.EASY_CONNECT_ERROR_CODE";
    public static final String EXTRA_ENABLE_MMS_DATA_REQUEST_REASON = "android.settings.extra.ENABLE_MMS_DATA_REQUEST_REASON";
    public static final String EXTRA_EXPLICIT_LOCALES = "android.provider.extra.EXPLICIT_LOCALES";
    public static final String EXTRA_INPUT_DEVICE_IDENTIFIER = "input_device_identifier";
    public static final String EXTRA_INPUT_METHOD_ID = "input_method_id";
    public static final String EXTRA_MONITOR_CALLBACK_TYPE = "monitor_callback_type";
    public static final String EXTRA_NAMESPACE = "namespace";
    public static final String EXTRA_NAMESPACE_UPDATED_CALLBACK = "namespace_updated_callback";
    public static final String EXTRA_NETWORK_TEMPLATE = "network_template";
    public static final String EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME = "android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME";
    public static final String EXTRA_NUMBER_OF_CERTIFICATES = "android.settings.extra.number_of_certificates";
    public static final String EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY = "android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_HIGHLIGHT_MENU_KEY";
    public static final String EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI = "android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI";
    public static final String EXTRA_SUB_ID = "android.provider.extra.SUB_ID";
    public static final String EXTRA_SUPERVISOR_RESTRICTED_SETTING_KEY = "android.provider.extra.SUPERVISOR_RESTRICTED_SETTING_KEY";
    public static final String EXTRA_WIFI_NETWORK_LIST = "android.provider.extra.WIFI_NETWORK_LIST";
    public static final String EXTRA_WIFI_NETWORK_RESULT_LIST = "android.provider.extra.WIFI_NETWORK_RESULT_LIST";
    public static final String INTENT_CATEGORY_USAGE_ACCESS_CONFIG = "android.intent.category.USAGE_ACCESS_CONFIG";
    public static final String KEY_CONFIG_GET_SYNC_DISABLED_MODE_RETURN = "config_get_sync_disabled_mode_return";
    public static final String KEY_CONFIG_SET_ALL_RETURN = "config_set_all_return";
    private static final boolean LOCAL_LOGV = false;
    public static final String METADATA_USAGE_ACCESS_REASON = "android.settings.metadata.USAGE_ACCESS_REASON";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESET_MODE_PACKAGE_DEFAULTS = 1;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESET_MODE_TRUSTED_DEFAULTS = 4;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESET_MODE_UNTRUSTED_CHANGES = 3;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESET_MODE_UNTRUSTED_DEFAULTS = 2;
    public static final int SET_ALL_RESULT_DISABLED = 2;
    public static final int SET_ALL_RESULT_FAILURE = 0;
    public static final int SET_ALL_RESULT_SUCCESS = 1;
    public static final int SUPERVISOR_VERIFICATION_SETTING_BIOMETRICS = 1;
    public static final int SUPERVISOR_VERIFICATION_SETTING_UNKNOWN = 0;
    private static final String SYSTEM_PACKAGE_NAME = "android";
    private static final String TAG = "Settings";
    public static final String ZEN_MODE_BLOCKED_EFFECTS_SETTINGS = "android.settings.ZEN_MODE_BLOCKED_EFFECTS_SETTINGS";
    public static final String ZEN_MODE_ONBOARDING = "android.settings.ZEN_MODE_ONBOARDING";
    private static boolean sInSystemServer = false;
    private static final Object sInSystemServerLock = new Object();
    private static final String[] PM_WRITE_SETTINGS = {Manifest.C0000permission.WRITE_SETTINGS};
    private static final String[] PM_CHANGE_NETWORK_STATE = {Manifest.C0000permission.CHANGE_NETWORK_STATE, Manifest.C0000permission.WRITE_SETTINGS};
    private static final String[] PM_SYSTEM_ALERT_WINDOW = {Manifest.C0000permission.SYSTEM_ALERT_WINDOW};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AddWifiResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EnableMmsDataReason {
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: classes3.dex */
    public @interface Readable {
        int maxTargetSdk() default 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ResetMode {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SetAllResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SupervisorVerificationSetting {
    }

    public static void setInSystemServer() {
        synchronized (sInSystemServerLock) {
            sInSystemServer = true;
        }
    }

    public static boolean isInSystemServer() {
        boolean z;
        synchronized (sInSystemServerLock) {
            z = sInSystemServer;
        }
        return z;
    }

    /* loaded from: classes3.dex */
    public static class SettingNotFoundException extends AndroidException {
        public SettingNotFoundException(String msg) {
            super(msg);
        }
    }

    /* loaded from: classes3.dex */
    public static class NameValueTable implements BaseColumns {
        public static final String IS_PRESERVED_IN_RESTORE = "is_preserved_in_restore";
        public static final String NAME = "name";
        public static final String VALUE = "value";

        protected static boolean putString(ContentResolver resolver, Uri uri, String name, String value) {
            try {
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("value", value);
                resolver.insert(uri, values);
                return true;
            } catch (SQLException e) {
                Log.m103w(Settings.TAG, "Can't set key " + name + " in " + uri, e);
                return false;
            }
        }

        public static Uri getUriFor(Uri uri, String name) {
            return Uri.withAppendedPath(uri, name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class GenerationTracker {
        private final MemoryIntArray mArray;
        private int mCurrentGeneration;
        private final Consumer<String> mErrorHandler;
        private final int mIndex;
        private final String mName;

        GenerationTracker(String name, MemoryIntArray array, int index, int generation, Consumer<String> errorHandler) {
            this.mName = name;
            this.mArray = array;
            this.mIndex = index;
            this.mErrorHandler = errorHandler;
            this.mCurrentGeneration = generation;
        }

        public boolean isGenerationChanged() {
            int currentGeneration = readCurrentGeneration();
            if (currentGeneration >= 0) {
                if (currentGeneration == this.mCurrentGeneration) {
                    return false;
                }
                this.mCurrentGeneration = currentGeneration;
                return true;
            }
            return true;
        }

        public int getCurrentGeneration() {
            return this.mCurrentGeneration;
        }

        private int readCurrentGeneration() {
            try {
                return this.mArray.get(this.mIndex);
            } catch (IOException e) {
                Log.m109e(Settings.TAG, "Error getting current generation", e);
                this.mErrorHandler.accept(this.mName);
                return -1;
            }
        }

        public void destroy() {
            try {
                this.mArray.close();
            } catch (IOException e) {
                Log.m109e(Settings.TAG, "Error closing backing array", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class ContentProviderHolder {
        private IContentProvider mContentProvider;
        private final Object mLock = new Object();
        private final Uri mUri;

        public ContentProviderHolder(Uri uri) {
            this.mUri = uri;
        }

        public IContentProvider getProvider(ContentResolver contentResolver) {
            IContentProvider iContentProvider;
            synchronized (this.mLock) {
                if (this.mContentProvider == null) {
                    this.mContentProvider = contentResolver.acquireProvider(this.mUri.getAuthority());
                }
                iContentProvider = this.mContentProvider;
            }
            return iContentProvider;
        }

        public void clearProviderForTest() {
            synchronized (this.mLock) {
                this.mContentProvider = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class NameValueCache {
        private static final boolean DEBUG = false;
        private static final String NAME_EQ_PLACEHOLDER = "name=?";
        private static final String[] SELECT_VALUE_PROJECTION = {"value"};
        private final ArraySet<String> mAllFields;
        private final String mCallDeleteCommand;
        private final String mCallGetCommand;
        private final String mCallListCommand;
        private final String mCallSetAllCommand;
        private final String mCallSetCommand;
        private Consumer<String> mGenerationTrackerErrorHandler;
        private ArrayMap<String, GenerationTracker> mGenerationTrackers;
        private final ContentProviderHolder mProviderHolder;
        private final ArraySet<String> mReadableFields;
        private final ArrayMap<String, Integer> mReadableFieldsWithMaxTargetSdk;
        private final Uri mUri;
        private final ArrayMap<String, String> mValues;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(String name) {
            synchronized (this) {
                Log.m110e(Settings.TAG, "Error accessing generation tracker - removing");
                GenerationTracker tracker = this.mGenerationTrackers.get(name);
                if (tracker != null) {
                    tracker.destroy();
                    this.mGenerationTrackers.remove(name);
                }
                this.mValues.remove(name);
            }
        }

        <T extends NameValueTable> NameValueCache(Uri uri, String getCommand, String setCommand, String deleteCommand, ContentProviderHolder providerHolder, Class<T> callerClass) {
            this(uri, getCommand, setCommand, deleteCommand, null, null, providerHolder, callerClass);
        }

        private <T extends NameValueTable> NameValueCache(Uri uri, String getCommand, String setCommand, String deleteCommand, String listCommand, String setAllCommand, ContentProviderHolder providerHolder, Class<T> callerClass) {
            this.mValues = new ArrayMap<>();
            this.mGenerationTrackers = new ArrayMap<>();
            this.mGenerationTrackerErrorHandler = new Consumer() { // from class: android.provider.Settings$NameValueCache$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    Settings.NameValueCache.this.lambda$new$0((String) obj);
                }
            };
            this.mUri = uri;
            this.mCallGetCommand = getCommand;
            this.mCallSetCommand = setCommand;
            this.mCallDeleteCommand = deleteCommand;
            this.mCallListCommand = listCommand;
            this.mCallSetAllCommand = setAllCommand;
            this.mProviderHolder = providerHolder;
            ArraySet<String> arraySet = new ArraySet<>();
            this.mReadableFields = arraySet;
            ArraySet<String> arraySet2 = new ArraySet<>();
            this.mAllFields = arraySet2;
            ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
            this.mReadableFieldsWithMaxTargetSdk = arrayMap;
            Settings.getPublicSettingsForClass(callerClass, arraySet2, arraySet, arrayMap);
        }

        public boolean putStringForUser(ContentResolver cr, String name, String value, String tag, boolean makeDefault, int userHandle, boolean overrideableByRestore) {
            try {
                Bundle arg = new Bundle();
                arg.putString("value", value);
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                if (tag != null) {
                    arg.putString(Settings.CALL_METHOD_TAG_KEY, tag);
                }
                if (makeDefault) {
                    arg.putBoolean(Settings.CALL_METHOD_MAKE_DEFAULT_KEY, true);
                }
                if (overrideableByRestore) {
                    arg.putBoolean(Settings.CALL_METHOD_OVERRIDEABLE_BY_RESTORE_KEY, true);
                }
                IContentProvider cp = this.mProviderHolder.getProvider(cr);
                cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallSetCommand, name, arg);
                return true;
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't set key " + name + " in " + this.mUri, e);
                return false;
            }
        }

        public int setStringsForPrefix(ContentResolver cr, String prefix, HashMap<String, String> keyValues) {
            if (this.mCallSetAllCommand == null) {
                return 0;
            }
            try {
                Bundle args = new Bundle();
                args.putString(Settings.CALL_METHOD_PREFIX_KEY, prefix);
                args.putSerializable(Settings.CALL_METHOD_FLAGS_KEY, keyValues);
                IContentProvider cp = this.mProviderHolder.getProvider(cr);
                Bundle bundle = cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallSetAllCommand, null, args);
                return bundle.getInt(Settings.KEY_CONFIG_SET_ALL_RETURN);
            } catch (RemoteException e) {
                return 0;
            }
        }

        public boolean deleteStringForUser(ContentResolver cr, String name, int userHandle) {
            try {
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                IContentProvider cp = this.mProviderHolder.getProvider(cr);
                cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallDeleteCommand, name, arg);
                return true;
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't delete key " + name + " in " + this.mUri, e);
                return false;
            }
        }

        public String getStringForUser(ContentResolver cr, String name, int userHandle) {
            int currentGeneration;
            boolean needsGenerationTracker;
            Bundle b;
            String value;
            long token;
            String str;
            Cursor c;
            boolean targetSdkCheckOk = true;
            boolean isSelf = userHandle == UserHandle.myUserId();
            int currentGeneration2 = -1;
            boolean needsGenerationTracker2 = false;
            if (isSelf) {
                synchronized (this) {
                    GenerationTracker generationTracker = this.mGenerationTrackers.get(name);
                    if (generationTracker != null) {
                        if (generationTracker.isGenerationChanged()) {
                            this.mValues.remove(name);
                        } else if (this.mValues.containsKey(name)) {
                            return this.mValues.get(name);
                        }
                        currentGeneration2 = generationTracker.getCurrentGeneration();
                    } else {
                        needsGenerationTracker2 = true;
                    }
                    currentGeneration = currentGeneration2;
                    needsGenerationTracker = needsGenerationTracker2;
                }
            } else {
                currentGeneration = -1;
                needsGenerationTracker = false;
            }
            if (!isCallerExemptFromReadableRestriction() && this.mAllFields.contains(name)) {
                if (!this.mReadableFields.contains(name)) {
                    throw new SecurityException("Settings key: <" + name + "> is not readable. From S+, settings keys annotated with @hide are restricted to system_server and system apps only, unless they are annotated with @Readable.");
                }
                if (this.mReadableFieldsWithMaxTargetSdk.containsKey(name)) {
                    int maxTargetSdk = this.mReadableFieldsWithMaxTargetSdk.get(name).intValue();
                    Application application = ActivityThread.currentApplication();
                    if (application == null || application.getApplicationInfo() == null || application.getApplicationInfo().targetSdkVersion > maxTargetSdk) {
                        targetSdkCheckOk = false;
                    }
                    if (!targetSdkCheckOk) {
                        throw new SecurityException("Settings key: <" + name + "> is only readable to apps with targetSdkVersion lower than or equal to: " + maxTargetSdk);
                    }
                }
            }
            IContentProvider cp = this.mProviderHolder.getProvider(cr);
            if (this.mCallGetCommand != null) {
                try {
                    Bundle args = new Bundle();
                    if (!isSelf) {
                        args.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                    }
                    if (needsGenerationTracker) {
                        args.putString(Settings.CALL_METHOD_TRACK_GENERATION_KEY, null);
                    }
                    if (!Settings.isInSystemServer() || Binder.getCallingUid() == Process.myUid()) {
                        b = cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallGetCommand, name, args);
                    } else {
                        token = Binder.clearCallingIdentity();
                        try {
                            try {
                                Bundle b2 = cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallGetCommand, name, args);
                                Binder.restoreCallingIdentity(token);
                                b = b2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    if (b != null) {
                        String value2 = b.getString("value");
                        if (!isSelf) {
                            return value2;
                        }
                        synchronized (this) {
                            try {
                                if (needsGenerationTracker) {
                                    try {
                                        MemoryIntArray array = (MemoryIntArray) b.getParcelable(Settings.CALL_METHOD_TRACK_GENERATION_KEY, MemoryIntArray.class);
                                        int index = b.getInt(Settings.CALL_METHOD_GENERATION_INDEX_KEY, -1);
                                        if (array == null || index < 0) {
                                            value = value2;
                                        } else {
                                            int generation = b.getInt(Settings.CALL_METHOD_GENERATION_KEY, 0);
                                            value = value2;
                                            this.mGenerationTrackers.put(name, new GenerationTracker(name, array, index, generation, this.mGenerationTrackerErrorHandler));
                                            currentGeneration = generation;
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        throw th;
                                    }
                                } else {
                                    value = value2;
                                }
                                if (this.mGenerationTrackers.get(name) != null && currentGeneration == this.mGenerationTrackers.get(name).getCurrentGeneration()) {
                                    this.mValues.put(name, value);
                                }
                                return value;
                            } catch (Throwable th4) {
                                th = th4;
                            }
                        }
                    }
                } catch (RemoteException e) {
                }
            }
            Cursor c2 = null;
            try {
                try {
                    try {
                        Bundle queryArgs = ContentResolver.createSqlQueryBundle(NAME_EQ_PLACEHOLDER, new String[]{name}, null);
                        if (!Settings.isInSystemServer() || Binder.getCallingUid() == Process.myUid()) {
                            str = null;
                            try {
                                c = cp.query(cr.getAttributionSource(), this.mUri, SELECT_VALUE_PROJECTION, queryArgs, null);
                            } catch (RemoteException e2) {
                                e = e2;
                                Log.m103w(Settings.TAG, "Can't get key " + name + " from " + this.mUri, e);
                                if (c2 != null) {
                                    c2.close();
                                }
                                return str;
                            }
                        } else {
                            token = Binder.clearCallingIdentity();
                            try {
                                c = cp.query(cr.getAttributionSource(), this.mUri, SELECT_VALUE_PROJECTION, queryArgs, null);
                                Binder.restoreCallingIdentity(token);
                                str = null;
                            } finally {
                                Binder.restoreCallingIdentity(token);
                            }
                        }
                        if (c2 == null) {
                            Log.m104w(Settings.TAG, "Can't get key " + name + " from " + this.mUri);
                            return str;
                        }
                        String value3 = c2.moveToNext() ? c2.getString(0) : str;
                        synchronized (this) {
                            if (this.mGenerationTrackers.get(name) != null && currentGeneration == this.mGenerationTrackers.get(name).getCurrentGeneration()) {
                                this.mValues.put(name, value3);
                            }
                        }
                        if (c2 != null) {
                            c2.close();
                        }
                        return value3;
                    } catch (RemoteException e3) {
                        e = e3;
                        str = null;
                    }
                } catch (RemoteException e4) {
                    e = e4;
                    str = null;
                }
            } finally {
                if (c2 != null) {
                    c2.close();
                }
            }
        }

        private static boolean isCallerExemptFromReadableRestriction() {
            if (!Settings.isInSystemServer() && UserHandle.getAppId(Binder.getCallingUid()) >= 10000) {
                Application application = ActivityThread.currentApplication();
                if (application == null || application.getApplicationInfo() == null) {
                    return false;
                }
                ApplicationInfo applicationInfo = application.getApplicationInfo();
                boolean isTestOnly = (applicationInfo.flags & 256) != 0;
                return isTestOnly || applicationInfo.isSystemApp() || applicationInfo.isPrivilegedApp() || applicationInfo.isSignedWithPlatformKey();
            }
            return true;
        }

        public ArrayMap<String, String> getStringsForPrefix(ContentResolver cr, String prefix, List<String> names) {
            int currentGeneration;
            boolean needsGenerationTracker;
            Bundle b;
            String namespace = prefix.substring(0, prefix.length() - 1);
            Config.enforceReadPermission(namespace);
            ArrayMap<String, String> keyValues = new ArrayMap<>();
            synchronized (this) {
                try {
                    GenerationTracker generationTracker = this.mGenerationTrackers.get(prefix);
                    if (generationTracker == null) {
                        currentGeneration = -1;
                        needsGenerationTracker = true;
                    } else {
                        try {
                            if (generationTracker.isGenerationChanged()) {
                                for (int i = 0; i < this.mValues.size(); i++) {
                                    String key = this.mValues.keyAt(i);
                                    if (key.startsWith(prefix)) {
                                        this.mValues.remove(key);
                                    }
                                }
                            } else {
                                boolean prefixCached = this.mValues.containsKey(prefix);
                                if (prefixCached) {
                                    if (!names.isEmpty()) {
                                        for (String name : names) {
                                            if (this.mValues.containsKey(name)) {
                                                keyValues.put(name, this.mValues.get(name));
                                            }
                                        }
                                    } else {
                                        for (int i2 = 0; i2 < this.mValues.size(); i2++) {
                                            String key2 = this.mValues.keyAt(i2);
                                            if (key2.startsWith(prefix) && !key2.equals(prefix)) {
                                                keyValues.put(key2, this.mValues.get(key2));
                                            }
                                        }
                                    }
                                    return keyValues;
                                }
                            }
                            int currentGeneration2 = generationTracker.getCurrentGeneration();
                            currentGeneration = currentGeneration2;
                            needsGenerationTracker = false;
                        } catch (Throwable th) {
                            e = th;
                            while (true) {
                                try {
                                    break;
                                } catch (Throwable th2) {
                                    e = th2;
                                }
                            }
                            throw e;
                        }
                    }
                } catch (Throwable th3) {
                    e = th3;
                }
                try {
                    if (this.mCallListCommand == null) {
                        return keyValues;
                    }
                    IContentProvider cp = this.mProviderHolder.getProvider(cr);
                    try {
                        Bundle args = new Bundle();
                        args.putString(Settings.CALL_METHOD_PREFIX_KEY, prefix);
                        if (needsGenerationTracker) {
                            try {
                                args.putString(Settings.CALL_METHOD_TRACK_GENERATION_KEY, null);
                            } catch (RemoteException e) {
                                return keyValues;
                            }
                        }
                        if (namespace.equals("device_policy_manager") && Settings.isInSystemServer() && Binder.getCallingUid() != Process.myUid()) {
                            long token = Binder.clearCallingIdentity();
                            Bundle b2 = cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallListCommand, null, args);
                            Binder.restoreCallingIdentity(token);
                            b = b2;
                        } else {
                            b = cp.call(cr.getAttributionSource(), this.mProviderHolder.mUri.getAuthority(), this.mCallListCommand, null, args);
                        }
                        if (b == null) {
                            return keyValues;
                        }
                        Map<String, String> flagsToValues = (HashMap) b.getSerializable("value", HashMap.class);
                        if (names.isEmpty()) {
                            keyValues.putAll(flagsToValues);
                        } else {
                            try {
                                for (Map.Entry<String, String> flag : flagsToValues.entrySet()) {
                                    if (names.contains(flag.getKey())) {
                                        keyValues.put(flag.getKey(), flag.getValue());
                                    }
                                }
                            } catch (RemoteException e2) {
                                return keyValues;
                            }
                        }
                        synchronized (this) {
                            try {
                                if (needsGenerationTracker) {
                                    try {
                                        MemoryIntArray array = (MemoryIntArray) b.getParcelable(Settings.CALL_METHOD_TRACK_GENERATION_KEY, MemoryIntArray.class);
                                        int index = b.getInt(Settings.CALL_METHOD_GENERATION_INDEX_KEY, -1);
                                        if (array != null && index >= 0) {
                                            int generation = b.getInt(Settings.CALL_METHOD_GENERATION_KEY, 0);
                                            this.mGenerationTrackers.put(prefix, new GenerationTracker(prefix, array, index, generation, this.mGenerationTrackerErrorHandler));
                                            currentGeneration = generation;
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                        try {
                                            throw th;
                                        } catch (RemoteException e3) {
                                            return keyValues;
                                        }
                                    }
                                }
                                if (this.mGenerationTrackers.get(prefix) != null && currentGeneration == this.mGenerationTrackers.get(prefix).getCurrentGeneration()) {
                                    this.mValues.putAll(flagsToValues);
                                    this.mValues.put(prefix, null);
                                }
                                return keyValues;
                            } catch (Throwable th5) {
                                th = th5;
                            }
                        }
                    } catch (RemoteException e4) {
                    }
                } catch (Throwable th6) {
                    e = th6;
                    while (true) {
                        break;
                        break;
                    }
                    throw e;
                }
            }
        }

        public void clearGenerationTrackerForTest() {
            synchronized (this) {
                for (int i = 0; i < this.mGenerationTrackers.size(); i++) {
                    this.mGenerationTrackers.valueAt(i).destroy();
                }
                this.mGenerationTrackers.clear();
                this.mValues.clear();
            }
        }
    }

    public static boolean canDrawOverlays(Context context) {
        return isCallingPackageAllowedToDrawOverlays(context, Process.myUid(), context.getOpPackageName(), false) || context.checkSelfPermission(Manifest.C0000permission.SYSTEM_APPLICATION_OVERLAY) == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends NameValueTable> void getPublicSettingsForClass(Class<T> callerClass, Set<String> allKeys, Set<String> readableKeys, ArrayMap<String, Integer> keysWithMaxTargetSdk) {
        Field[] allFields = callerClass.getDeclaredFields();
        for (Field field : allFields) {
            try {
                if (field.getType().equals(String.class)) {
                    Object value = field.get(callerClass);
                    if (value.getClass().equals(String.class)) {
                        allKeys.add((String) value);
                        Readable annotation = (Readable) field.getAnnotation(Readable.class);
                        if (annotation != null) {
                            String key = (String) value;
                            int maxTargetSdk = annotation.maxTargetSdk();
                            readableKeys.add(key);
                            if (maxTargetSdk != 0) {
                                keysWithMaxTargetSdk.put(key, Integer.valueOf(maxTargetSdk));
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float parseFloatSetting(String settingValue, String settingName) throws SettingNotFoundException {
        if (settingValue == null) {
            throw new SettingNotFoundException(settingName);
        }
        try {
            return Float.parseFloat(settingValue);
        } catch (NumberFormatException e) {
            throw new SettingNotFoundException(settingName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float parseFloatSettingWithDefault(String settingValue, float defaultValue) {
        if (settingValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(settingValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int parseIntSetting(String settingValue, String settingName) throws SettingNotFoundException {
        if (settingValue == null) {
            throw new SettingNotFoundException(settingName);
        }
        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException e) {
            throw new SettingNotFoundException(settingName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int parseIntSettingWithDefault(String settingValue, int defaultValue) {
        if (settingValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long parseLongSetting(String settingValue, String settingName) throws SettingNotFoundException {
        if (settingValue == null) {
            throw new SettingNotFoundException(settingName);
        }
        try {
            return Long.parseLong(settingValue);
        } catch (NumberFormatException e) {
            throw new SettingNotFoundException(settingName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long parseLongSettingWithDefault(String settingValue, long defaultValue) {
        if (settingValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(settingValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /* loaded from: classes3.dex */
    public static final class System extends NameValueTable {
        @Readable
        public static final String ACCELEROMETER_ROTATION = "accelerometer_rotation";
        @Readable
        @Deprecated
        public static final String ADAPTIVE_SLEEP = "adaptive_sleep";
        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        @Readable
        public static final String ADVANCED_SETTINGS = "advanced_settings";
        public static final int ADVANCED_SETTINGS_DEFAULT = 0;
        @Deprecated
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
        @Deprecated
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
        @Deprecated
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        @Readable
        public static final String ALARM_ALERT = "alarm_alert";
        @Readable
        public static final String ALARM_ALERT_CACHE = "alarm_alert_cache";
        public static final Uri ALARM_ALERT_CACHE_URI;
        public static final String ALARM_VIBRATION_INTENSITY = "alarm_vibration_intensity";
        @Deprecated
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
        @Deprecated
        public static final String ANDROID_ID = "android_id";
        @Deprecated
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        @Readable
        public static final String APPEND_FOR_LAST_AUDIBLE = "_last_audible";
        @Readable
        public static final String APPLY_RAMPING_RINGER = "apply_ramping_ringer";
        public static final String AUTO_LAUNCH_MEDIA_CONTROLS = "auto_launch_media_controls";
        @Deprecated
        public static final String AUTO_TIME = "auto_time";
        @Deprecated
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        @Readable
        public static final String BLUETOOTH_DISCOVERABILITY = "bluetooth_discoverability";
        @Readable
        public static final String BLUETOOTH_DISCOVERABILITY_TIMEOUT = "bluetooth_discoverability_timeout";
        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";
        public static final String CAMERA_FLASH_NOTIFICATION = "camera_flash_notification";
        @Deprecated
        public static final String CAR_DOCK_SOUND = "car_dock_sound";
        @Deprecated
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        public static final String CLOCKWORK_BLUETOOTH_SETTINGS_PREF = "cw_bt_settings_pref";
        public static final Map<String, String> CLONE_FROM_PARENT_ON_VALUE;
        private static final Set<String> CLONE_TO_MANAGED_PROFILE;
        public static final Uri CONTENT_URI;
        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        @Readable
        @Deprecated
        public static final String DATE_FORMAT = "date_format";
        @Deprecated
        public static final String DEBUG_APP = "debug_app";
        @Readable
        public static final String DEBUG_ENABLE_ENHANCED_CALL_BLOCKING = "debug.enable_enhanced_calling";
        public static final Uri DEFAULT_ALARM_ALERT_URI;
        private static final float DEFAULT_FONT_SCALE = 1.0f;
        private static final int DEFAULT_FONT_WEIGHT = 0;
        public static final Uri DEFAULT_NOTIFICATION_URI;
        public static final Uri DEFAULT_RINGTONE_URI;
        @Readable
        public static final String DESKTOP_MODE = "desktop_mode";
        @Deprecated
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";
        @Deprecated
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        @Readable
        @Deprecated
        public static final String DIM_SCREEN = "dim_screen";
        @Readable
        public static final String DISPLAY_COLOR_MODE = "display_color_mode";
        public static final String DISPLAY_COLOR_MODE_VENDOR_HINT = "display_color_mode_vendor_hint";
        @Deprecated
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        @Readable
        public static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type";
        @Readable
        public static final String DTMF_TONE_WHEN_DIALING = "dtmf_tone";
        @Readable
        public static final String EGG_MODE = "egg_mode";
        @Readable
        public static final String END_BUTTON_BEHAVIOR = "end_button_behavior";
        public static final int END_BUTTON_BEHAVIOR_DEFAULT = 2;
        public static final int END_BUTTON_BEHAVIOR_HOME = 1;
        public static final int END_BUTTON_BEHAVIOR_SLEEP = 2;
        @Readable
        public static final String FONT_SCALE = "font_scale";
        @Readable
        @Deprecated
        public static final String HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled";
        @Readable
        public static final String HAPTIC_FEEDBACK_INTENSITY = "haptic_feedback_intensity";
        public static final String HARDWARE_HAPTIC_FEEDBACK_INTENSITY = "hardware_haptic_feedback_intensity";
        @Readable
        public static final String HEARING_AID = "hearing_aid";
        @Readable
        public static final String HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY = "hide_rotation_lock_toggle_for_accessibility";
        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";
        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        public static final Set<String> INSTANT_APP_SETTINGS;
        public static final String[] LEGACY_RESTORE_SETTINGS;
        public static final String LOCALE_PREFERENCES = "locale_preferences";
        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        @Readable
        public static final String LOCKSCREEN_DISABLED = "lockscreen.disabled";
        @Readable
        public static final String LOCKSCREEN_SOUNDS_ENABLED = "lockscreen_sounds_enabled";
        @Deprecated
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
        @Deprecated
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
        @Deprecated
        public static final String LOCK_SOUND = "lock_sound";
        @Readable
        public static final String LOCK_TO_APP_ENABLED = "lock_to_app_enabled";
        @Deprecated
        public static final String LOGGING_ID = "logging_id";
        @Deprecated
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        @Readable
        public static final String MASTER_BALANCE = "master_balance";
        @Readable
        public static final String MASTER_MONO = "master_mono";
        @Readable(maxTargetSdk = 30)
        public static final String MEDIA_BUTTON_RECEIVER = "media_button_receiver";
        public static final String MEDIA_VIBRATION_INTENSITY = "media_vibration_intensity";
        @Readable
        public static final String MIN_REFRESH_RATE = "min_refresh_rate";
        @Deprecated
        public static final String MODE_RINGER = "mode_ringer";
        @Readable
        public static final String MODE_RINGER_STREAMS_AFFECTED = "mode_ringer_streams_affected";
        private static final HashSet<String> MOVED_TO_GLOBAL;
        private static final HashSet<String> MOVED_TO_SECURE;
        private static final HashSet<String> MOVED_TO_SECURE_THEN_GLOBAL;
        @Readable
        public static final String MULTI_AUDIO_FOCUS_ENABLED = "multi_audio_focus_enabled";
        @Readable
        public static final String MUTE_STREAMS_AFFECTED = "mute_streams_affected";
        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        @Readable
        @Deprecated
        public static final String NEXT_ALARM_FORMATTED = "next_alarm_formatted";
        @Readable
        @Deprecated
        public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";
        @Readable
        public static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
        @Readable
        public static final String NOTIFICATION_SOUND = "notification_sound";
        @Readable
        public static final String NOTIFICATION_SOUND_CACHE = "notification_sound_cache";
        public static final Uri NOTIFICATION_SOUND_CACHE_URI;
        @Readable
        public static final String NOTIFICATION_VIBRATION_INTENSITY = "notification_vibration_intensity";
        @Deprecated
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
        @Deprecated
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
        @Deprecated
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        @Readable
        public static final String PEAK_REFRESH_RATE = "peak_refresh_rate";
        @Readable
        public static final String POINTER_LOCATION = "pointer_location";
        @Readable
        public static final String POINTER_SPEED = "pointer_speed";
        @Deprecated
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
        public static final Set<String> PRIVATE_SETTINGS;
        public static final Set<String> PUBLIC_SETTINGS;
        @Deprecated
        public static final String RADIO_BLUETOOTH = "bluetooth";
        @Deprecated
        public static final String RADIO_CELL = "cell";
        @Deprecated
        public static final String RADIO_NFC = "nfc";
        @Deprecated
        public static final String RADIO_WIFI = "wifi";
        @Deprecated
        public static final String RADIO_WIMAX = "wimax";
        @Readable
        public static final String RINGTONE = "ringtone";
        @Readable
        public static final String RINGTONE_CACHE = "ringtone_cache";
        public static final Uri RINGTONE_CACHE_URI;
        @Readable
        public static final String RING_VIBRATION_INTENSITY = "ring_vibration_intensity";
        @Readable
        public static final String SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj";
        @Readable
        public static final String SCREEN_BRIGHTNESS = "screen_brightness";
        @Readable
        public static final String SCREEN_BRIGHTNESS_FLOAT = "screen_brightness_float";
        @Readable
        public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
        public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
        public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
        public static final String SCREEN_FLASH_NOTIFICATION = "screen_flash_notification";
        public static final String SCREEN_FLASH_NOTIFICATION_COLOR = "screen_flash_notification_color_global";
        @Readable
        public static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";
        @Deprecated
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        @Readable
        public static final String SETUP_WIZARD_HAS_RUN = "setup_wizard_has_run";
        @Readable
        public static final String SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
        @Readable
        public static final String SHOW_GTALK_SERVICE_STATUS = "SHOW_GTALK_SERVICE_STATUS";
        @Deprecated
        public static final String SHOW_PROCESSES = "show_processes";
        @Readable
        public static final String SHOW_TOUCHES = "show_touches";
        @Readable
        @Deprecated
        public static final String SHOW_WEB_SUGGESTIONS = "show_web_suggestions";
        @Readable
        public static final String SIP_ADDRESS_ONLY = "SIP_ADDRESS_ONLY";
        @Readable
        public static final String SIP_ALWAYS = "SIP_ALWAYS";
        @Readable
        @Deprecated
        public static final String SIP_ASK_ME_EACH_TIME = "SIP_ASK_ME_EACH_TIME";
        @Readable
        public static final String SIP_CALL_OPTIONS = "sip_call_options";
        @Readable
        public static final String SIP_RECEIVE_CALLS = "sip_receive_calls";
        @Readable
        public static final String SOUND_EFFECTS_ENABLED = "sound_effects_enabled";
        @Deprecated
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        @Readable
        public static final String SYSTEM_LOCALES = "system_locales";
        @Readable
        public static final String TEXT_AUTO_CAPS = "auto_caps";
        @Readable
        public static final String TEXT_AUTO_PUNCTUATE = "auto_punctuate";
        @Readable
        public static final String TEXT_AUTO_REPLACE = "auto_replace";
        @Readable
        public static final String TEXT_SHOW_PASSWORD = "show_password";
        @Readable
        public static final String TIME_12_24 = "time_12_24";
        public static final String TOUCHPAD_NATURAL_SCROLLING = "touchpad_natural_scrolling";
        public static final String TOUCHPAD_POINTER_SPEED = "touchpad_pointer_speed";
        public static final String TOUCHPAD_RIGHT_CLICK_ZONE = "touchpad_right_click_zone";
        public static final String TOUCHPAD_TAP_TO_CLICK = "touchpad_tap_to_click";
        @Deprecated
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        @Readable
        public static final String TTY_MODE = "tty_mode";
        @Deprecated
        public static final String UNLOCK_SOUND = "unlock_sound";
        public static final String UNREAD_NOTIFICATION_DOT_INDICATOR = "unread_notification_dot_indicator";
        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        @Readable
        public static final String USER_ROTATION = "user_rotation";
        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        @Readable
        public static final String VIBRATE_INPUT_DEVICES = "vibrate_input_devices";
        @Readable
        public static final String VIBRATE_IN_SILENT = "vibrate_in_silent";
        @Readable
        public static final String VIBRATE_ON = "vibrate_on";
        @Readable
        @Deprecated
        public static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
        @Readable
        public static final String VOLUME_ACCESSIBILITY = "volume_a11y";
        @Readable
        public static final String VOLUME_ALARM = "volume_alarm";
        @Readable
        public static final String VOLUME_ASSISTANT = "volume_assistant";
        @Readable
        public static final String VOLUME_BLUETOOTH_SCO = "volume_bluetooth_sco";
        @Readable
        public static final String VOLUME_MASTER = "volume_master";
        @Readable
        public static final String VOLUME_MUSIC = "volume_music";
        @Readable
        public static final String VOLUME_NOTIFICATION = "volume_notification";
        @Readable
        public static final String VOLUME_RING = "volume_ring";
        public static final String[] VOLUME_SETTINGS;
        public static final String[] VOLUME_SETTINGS_INT;
        @Readable
        public static final String VOLUME_SYSTEM = "volume_system";
        @Readable
        public static final String VOLUME_VOICE = "volume_voice";
        @Deprecated
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
        @Readable
        @Deprecated
        public static final String WALLPAPER_ACTIVITY = "wallpaper_activity";
        public static final String WEAR_ACCESSIBILITY_GESTURE_ENABLED = "wear_accessibility_gesture_enabled";
        @Readable
        public static final String WHEN_TO_MAKE_WIFI_CALLS = "when_to_make_wifi_calls";
        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        @Deprecated
        public static final String WIFI_ON = "wifi_on";
        @Deprecated
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
        @Readable
        @Deprecated
        public static final String WIFI_STATIC_DNS1 = "wifi_static_dns1";
        @Readable
        @Deprecated
        public static final String WIFI_STATIC_DNS2 = "wifi_static_dns2";
        @Readable
        @Deprecated
        public static final String WIFI_STATIC_GATEWAY = "wifi_static_gateway";
        @Readable
        @Deprecated
        public static final String WIFI_STATIC_IP = "wifi_static_ip";
        @Readable
        @Deprecated
        public static final String WIFI_STATIC_NETMASK = "wifi_static_netmask";
        @Readable
        @Deprecated
        public static final String WIFI_USE_STATIC_IP = "wifi_use_static_ip";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
        @Deprecated
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        @Readable
        public static final String WINDOW_ORIENTATION_LISTENER_LOG = "window_orientation_listener_log";
        private static final NameValueCache sNameValueCache;
        private static final ContentProviderHolder sProviderHolder;

        static {
            Uri parse = Uri.parse("content://settings/system");
            CONTENT_URI = parse;
            ContentProviderHolder contentProviderHolder = new ContentProviderHolder(parse);
            sProviderHolder = contentProviderHolder;
            sNameValueCache = new NameValueCache(parse, Settings.CALL_METHOD_GET_SYSTEM, Settings.CALL_METHOD_PUT_SYSTEM, Settings.CALL_METHOD_DELETE_SYSTEM, contentProviderHolder, System.class);
            HashSet<String> hashSet = new HashSet<>(30);
            MOVED_TO_SECURE = hashSet;
            hashSet.add("adaptive_sleep");
            hashSet.add("android_id");
            hashSet.add("http_proxy");
            hashSet.add("location_providers_allowed");
            hashSet.add(Secure.LOCK_BIOMETRIC_WEAK_FLAGS);
            hashSet.add("lock_pattern_autolock");
            hashSet.add("lock_pattern_visible_pattern");
            hashSet.add("lock_pattern_tactile_feedback_enabled");
            hashSet.add("logging_id");
            hashSet.add("parental_control_enabled");
            hashSet.add("parental_control_last_update");
            hashSet.add("parental_control_redirect_url");
            hashSet.add("settings_classname");
            hashSet.add("use_google_mail");
            hashSet.add("wifi_networks_available_notification_on");
            hashSet.add("wifi_networks_available_repeat_delay");
            hashSet.add("wifi_num_open_networks_kept");
            hashSet.add("wifi_on");
            hashSet.add("wifi_watchdog_acceptable_packet_loss_percentage");
            hashSet.add("wifi_watchdog_ap_count");
            hashSet.add("wifi_watchdog_background_check_delay_ms");
            hashSet.add("wifi_watchdog_background_check_enabled");
            hashSet.add("wifi_watchdog_background_check_timeout_ms");
            hashSet.add("wifi_watchdog_initial_ignored_ping_count");
            hashSet.add("wifi_watchdog_max_ap_checks");
            hashSet.add("wifi_watchdog_on");
            hashSet.add("wifi_watchdog_ping_count");
            hashSet.add("wifi_watchdog_ping_delay_ms");
            hashSet.add("wifi_watchdog_ping_timeout_ms");
            hashSet.add("install_non_market_apps");
            HashSet<String> hashSet2 = new HashSet<>();
            MOVED_TO_GLOBAL = hashSet2;
            HashSet<String> hashSet3 = new HashSet<>();
            MOVED_TO_SECURE_THEN_GLOBAL = hashSet3;
            hashSet3.add("adb_enabled");
            hashSet3.add("bluetooth_on");
            hashSet3.add("data_roaming");
            hashSet3.add("device_provisioned");
            hashSet3.add("http_proxy");
            hashSet3.add("network_preference");
            hashSet3.add("usb_mass_storage_enabled");
            hashSet3.add("wifi_mobile_data_transition_wakelock_timeout_ms");
            hashSet3.add("wifi_max_dhcp_retry_count");
            hashSet2.add("airplane_mode_on");
            hashSet2.add("airplane_mode_radios");
            hashSet2.add("airplane_mode_toggleable_radios");
            hashSet2.add("auto_time");
            hashSet2.add("auto_time_zone");
            hashSet2.add("car_dock_sound");
            hashSet2.add("car_undock_sound");
            hashSet2.add("desk_dock_sound");
            hashSet2.add("desk_undock_sound");
            hashSet2.add("dock_sounds_enabled");
            hashSet2.add("lock_sound");
            hashSet2.add("unlock_sound");
            hashSet2.add("low_battery_sound");
            hashSet2.add("power_sounds_enabled");
            hashSet2.add("stay_on_while_plugged_in");
            hashSet2.add("wifi_sleep_policy");
            hashSet2.add("mode_ringer");
            hashSet2.add("window_animation_scale");
            hashSet2.add("transition_animation_scale");
            hashSet2.add("animator_duration_scale");
            hashSet2.add(Global.FANCY_IME_ANIMATIONS);
            hashSet2.add(Global.COMPATIBILITY_MODE);
            hashSet2.add(Global.EMERGENCY_TONE);
            hashSet2.add(Global.CALL_AUTO_RETRY);
            hashSet2.add("debug_app");
            hashSet2.add("wait_for_debugger");
            hashSet2.add("always_finish_activities");
            hashSet2.add(Global.TZINFO_UPDATE_CONTENT_URL);
            hashSet2.add(Global.TZINFO_UPDATE_METADATA_URL);
            hashSet2.add(Global.SELINUX_UPDATE_CONTENT_URL);
            hashSet2.add(Global.SELINUX_UPDATE_METADATA_URL);
            hashSet2.add(Global.SMS_SHORT_CODES_UPDATE_CONTENT_URL);
            hashSet2.add(Global.SMS_SHORT_CODES_UPDATE_METADATA_URL);
            hashSet2.add(Global.CERT_PIN_UPDATE_CONTENT_URL);
            hashSet2.add(Global.CERT_PIN_UPDATE_METADATA_URL);
            hashSet2.add("nfc");
            hashSet2.add("cell");
            hashSet2.add("wifi");
            hashSet2.add("bluetooth");
            hashSet2.add("wimax");
            hashSet2.add("show_processes");
            VOLUME_SETTINGS = new String[]{VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO};
            VOLUME_SETTINGS_INT = new String[]{VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO, "", "", "", VOLUME_ACCESSIBILITY, VOLUME_ASSISTANT};
            DEFAULT_RINGTONE_URI = getUriFor(RINGTONE);
            RINGTONE_CACHE_URI = getUriFor(RINGTONE_CACHE);
            DEFAULT_NOTIFICATION_URI = getUriFor(NOTIFICATION_SOUND);
            NOTIFICATION_SOUND_CACHE_URI = getUriFor(NOTIFICATION_SOUND_CACHE);
            DEFAULT_ALARM_ALERT_URI = getUriFor(ALARM_ALERT);
            ALARM_ALERT_CACHE_URI = getUriFor(ALARM_ALERT_CACHE);
            LEGACY_RESTORE_SETTINGS = new String[0];
            ArraySet arraySet = new ArraySet();
            PUBLIC_SETTINGS = arraySet;
            arraySet.add(END_BUTTON_BEHAVIOR);
            arraySet.add(WIFI_USE_STATIC_IP);
            arraySet.add(WIFI_STATIC_IP);
            arraySet.add(WIFI_STATIC_GATEWAY);
            arraySet.add(WIFI_STATIC_NETMASK);
            arraySet.add(WIFI_STATIC_DNS1);
            arraySet.add(WIFI_STATIC_DNS2);
            arraySet.add(BLUETOOTH_DISCOVERABILITY);
            arraySet.add(BLUETOOTH_DISCOVERABILITY_TIMEOUT);
            arraySet.add(NEXT_ALARM_FORMATTED);
            arraySet.add(FONT_SCALE);
            arraySet.add(SYSTEM_LOCALES);
            arraySet.add(DIM_SCREEN);
            arraySet.add(SCREEN_OFF_TIMEOUT);
            arraySet.add(SCREEN_BRIGHTNESS);
            arraySet.add(SCREEN_BRIGHTNESS_FLOAT);
            arraySet.add(SCREEN_BRIGHTNESS_MODE);
            arraySet.add(MODE_RINGER_STREAMS_AFFECTED);
            arraySet.add(MUTE_STREAMS_AFFECTED);
            arraySet.add(VIBRATE_ON);
            arraySet.add(VOLUME_RING);
            arraySet.add(VOLUME_SYSTEM);
            arraySet.add(VOLUME_VOICE);
            arraySet.add(VOLUME_MUSIC);
            arraySet.add(VOLUME_ALARM);
            arraySet.add(VOLUME_NOTIFICATION);
            arraySet.add(VOLUME_BLUETOOTH_SCO);
            arraySet.add(VOLUME_ASSISTANT);
            arraySet.add(RINGTONE);
            arraySet.add(NOTIFICATION_SOUND);
            arraySet.add(ALARM_ALERT);
            arraySet.add(TEXT_AUTO_REPLACE);
            arraySet.add(TEXT_AUTO_CAPS);
            arraySet.add(TEXT_AUTO_PUNCTUATE);
            arraySet.add(TEXT_SHOW_PASSWORD);
            arraySet.add(SHOW_GTALK_SERVICE_STATUS);
            arraySet.add(WALLPAPER_ACTIVITY);
            arraySet.add(TIME_12_24);
            arraySet.add(DATE_FORMAT);
            arraySet.add(SETUP_WIZARD_HAS_RUN);
            arraySet.add(ACCELEROMETER_ROTATION);
            arraySet.add(USER_ROTATION);
            arraySet.add(DTMF_TONE_WHEN_DIALING);
            arraySet.add(SOUND_EFFECTS_ENABLED);
            arraySet.add(HAPTIC_FEEDBACK_ENABLED);
            arraySet.add(SHOW_WEB_SUGGESTIONS);
            arraySet.add(VIBRATE_WHEN_RINGING);
            arraySet.add("apply_ramping_ringer");
            ArraySet arraySet2 = new ArraySet();
            PRIVATE_SETTINGS = arraySet2;
            arraySet2.add(WIFI_USE_STATIC_IP);
            arraySet2.add(END_BUTTON_BEHAVIOR);
            arraySet2.add(ADVANCED_SETTINGS);
            arraySet2.add(WEAR_ACCESSIBILITY_GESTURE_ENABLED);
            arraySet2.add(SCREEN_AUTO_BRIGHTNESS_ADJ);
            arraySet2.add(VIBRATE_INPUT_DEVICES);
            arraySet2.add(VOLUME_MASTER);
            arraySet2.add(MASTER_MONO);
            arraySet2.add(MASTER_BALANCE);
            arraySet2.add(NOTIFICATIONS_USE_RING_VOLUME);
            arraySet2.add(VIBRATE_IN_SILENT);
            arraySet2.add(MEDIA_BUTTON_RECEIVER);
            arraySet2.add(HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY);
            arraySet2.add(DTMF_TONE_TYPE_WHEN_DIALING);
            arraySet2.add(HEARING_AID);
            arraySet2.add(TTY_MODE);
            arraySet2.add(NOTIFICATION_LIGHT_PULSE);
            arraySet2.add(POINTER_LOCATION);
            arraySet2.add(SHOW_TOUCHES);
            arraySet2.add(WINDOW_ORIENTATION_LISTENER_LOG);
            arraySet2.add("power_sounds_enabled");
            arraySet2.add("dock_sounds_enabled");
            arraySet2.add(LOCKSCREEN_SOUNDS_ENABLED);
            arraySet2.add("lockscreen.disabled");
            arraySet2.add("low_battery_sound");
            arraySet2.add("desk_dock_sound");
            arraySet2.add("desk_undock_sound");
            arraySet2.add("car_dock_sound");
            arraySet2.add("car_undock_sound");
            arraySet2.add("lock_sound");
            arraySet2.add("unlock_sound");
            arraySet2.add(SIP_RECEIVE_CALLS);
            arraySet2.add(SIP_CALL_OPTIONS);
            arraySet2.add(SIP_ALWAYS);
            arraySet2.add(SIP_ADDRESS_ONLY);
            arraySet2.add(SIP_ASK_ME_EACH_TIME);
            arraySet2.add(POINTER_SPEED);
            arraySet2.add(LOCK_TO_APP_ENABLED);
            arraySet2.add(EGG_MODE);
            arraySet2.add(SHOW_BATTERY_PERCENT);
            arraySet2.add(DISPLAY_COLOR_MODE);
            arraySet2.add(DISPLAY_COLOR_MODE_VENDOR_HINT);
            arraySet2.add(DESKTOP_MODE);
            arraySet2.add(LOCALE_PREFERENCES);
            arraySet2.add(TOUCHPAD_POINTER_SPEED);
            arraySet2.add(TOUCHPAD_NATURAL_SCROLLING);
            arraySet2.add(TOUCHPAD_TAP_TO_CLICK);
            arraySet2.add(TOUCHPAD_RIGHT_CLICK_ZONE);
            arraySet2.add(CAMERA_FLASH_NOTIFICATION);
            arraySet2.add(SCREEN_FLASH_NOTIFICATION);
            arraySet2.add(SCREEN_FLASH_NOTIFICATION_COLOR);
            ArraySet arraySet3 = new ArraySet();
            CLONE_TO_MANAGED_PROFILE = arraySet3;
            arraySet3.add(DATE_FORMAT);
            arraySet3.add(HAPTIC_FEEDBACK_ENABLED);
            arraySet3.add(SOUND_EFFECTS_ENABLED);
            arraySet3.add(TEXT_SHOW_PASSWORD);
            arraySet3.add(TIME_12_24);
            ArrayMap arrayMap = new ArrayMap();
            CLONE_FROM_PARENT_ON_VALUE = arrayMap;
            arrayMap.put(RINGTONE, Secure.SYNC_PARENT_SOUNDS);
            arrayMap.put(NOTIFICATION_SOUND, Secure.SYNC_PARENT_SOUNDS);
            arrayMap.put(ALARM_ALERT, Secure.SYNC_PARENT_SOUNDS);
            ArraySet arraySet4 = new ArraySet();
            INSTANT_APP_SETTINGS = arraySet4;
            arraySet4.add(TEXT_AUTO_REPLACE);
            arraySet4.add(TEXT_AUTO_CAPS);
            arraySet4.add(TEXT_AUTO_PUNCTUATE);
            arraySet4.add(TEXT_SHOW_PASSWORD);
            arraySet4.add(DATE_FORMAT);
            arraySet4.add(FONT_SCALE);
            arraySet4.add(HAPTIC_FEEDBACK_ENABLED);
            arraySet4.add(TIME_12_24);
            arraySet4.add(SOUND_EFFECTS_ENABLED);
            arraySet4.add(ACCELEROMETER_ROTATION);
        }

        public static void getMovedToGlobalSettings(Set<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
            outKeySet.addAll(MOVED_TO_SECURE_THEN_GLOBAL);
        }

        public static void getMovedToSecureSettings(Set<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_SECURE);
        }

        public static void getNonLegacyMovedKeys(HashSet<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
        }

        public static void clearProviderForTest() {
            sProviderHolder.clearProviderForTest();
            sNameValueCache.clearGenerationTrackerForTest();
        }

        public static void getPublicSettings(Set<String> allKeys, Set<String> readableKeys, ArrayMap<String, Integer> readableKeysWithMaxTargetSdk) {
            Settings.getPublicSettingsForClass(System.class, allKeys, readableKeys, readableKeysWithMaxTargetSdk);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, resolver.getUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning read-only value.");
                return Secure.getStringForUser(resolver, name, userHandle);
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only value.");
                return Global.getStringForUser(resolver, name, userHandle);
            } else {
                return sNameValueCache.getStringForUser(resolver, name, userHandle);
            }
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, resolver.getUserId());
        }

        @SystemApi
        public static boolean putString(ContentResolver resolver, String name, String value, boolean overrideableByRestore) {
            return putStringForUser(resolver, name, value, resolver.getUserId(), overrideableByRestore);
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            return putStringForUser(resolver, name, value, userHandle, false);
        }

        private static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle, boolean overrideableByRestore) {
            return putStringForUser(resolver, name, value, null, false, userHandle, overrideableByRestore);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean putStringForUser(ContentResolver resolver, String name, String value, String tag, boolean makeDefault, int userHandle, boolean overrideableByRestore) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, value is unchanged.");
                return false;
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, value is unchanged.");
                return false;
            } else {
                return sNameValueCache.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, overrideableByRestore);
            }
        }

        public static Uri getUriFor(String name) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning Secure URI.");
                return Secure.getUriFor(Secure.CONTENT_URI, name);
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only global URI.");
                return Global.getUriFor(Global.CONTENT_URI, name);
            } else {
                return getUriFor(CONTENT_URI, name);
            }
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, cr.getUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseIntSettingWithDefault(v, def);
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            return getIntForUser(cr, name, cr.getUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseIntSetting(v, name);
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userHandle) {
            return putStringForUser(cr, name, Integer.toString(value), userHandle);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, cr.getUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseLongSettingWithDefault(v, def);
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            return getLongForUser(cr, name, cr.getUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseLongSetting(v, name);
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userHandle) {
            return putStringForUser(cr, name, Long.toString(value), userHandle);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, cr.getUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseFloatSettingWithDefault(v, def);
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            return getFloatForUser(cr, name, cr.getUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseFloatSetting(v, name);
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userHandle) {
            return putStringForUser(cr, name, Float.toString(value), userHandle);
        }

        public static void getConfiguration(ContentResolver cr, Configuration outConfig) {
            adjustConfigurationForUser(cr, outConfig, cr.getUserId(), false);
        }

        public static void adjustConfigurationForUser(ContentResolver cr, Configuration outConfig, int userHandle, boolean updateSettingsIfEmpty) {
            outConfig.fontScale = getFloatForUser(cr, FONT_SCALE, 1.0f, userHandle);
            if (outConfig.fontScale < 0.0f) {
                outConfig.fontScale = 1.0f;
            }
            outConfig.fontWeightAdjustment = Secure.getIntForUser(cr, Secure.FONT_WEIGHT_ADJUSTMENT, 0, userHandle);
            String localeValue = getStringForUser(cr, SYSTEM_LOCALES, userHandle);
            if (localeValue != null) {
                outConfig.setLocales(LocaleList.forLanguageTags(localeValue));
            } else if (updateSettingsIfEmpty) {
                putStringForUser(cr, SYSTEM_LOCALES, outConfig.getLocales().toLanguageTags(), userHandle, false);
            }
        }

        public static void clearConfiguration(Configuration inoutConfig) {
            inoutConfig.fontScale = 0.0f;
            if (!inoutConfig.userSetLocale && !inoutConfig.getLocales().isEmpty()) {
                inoutConfig.clearLocales();
            }
            inoutConfig.fontWeightAdjustment = Integer.MAX_VALUE;
        }

        public static boolean putConfiguration(ContentResolver cr, Configuration config) {
            return putConfigurationForUser(cr, config, cr.getUserId());
        }

        public static boolean putConfigurationForUser(ContentResolver cr, Configuration config, int userHandle) {
            return putFloatForUser(cr, FONT_SCALE, config.fontScale, userHandle) && putStringForUser(cr, SYSTEM_LOCALES, config.getLocales().toLanguageTags(), userHandle, false);
        }

        public static boolean hasInterestingConfigurationChanges(int changes) {
            return ((1073741824 & changes) == 0 && (changes & 4) == 0) ? false : true;
        }

        @Deprecated
        public static boolean getShowGTalkServiceStatus(ContentResolver cr) {
            return getShowGTalkServiceStatusForUser(cr, cr.getUserId());
        }

        @Deprecated
        public static boolean getShowGTalkServiceStatusForUser(ContentResolver cr, int userHandle) {
            return getIntForUser(cr, SHOW_GTALK_SERVICE_STATUS, 0, userHandle) != 0;
        }

        @Deprecated
        public static void setShowGTalkServiceStatus(ContentResolver cr, boolean flag) {
            setShowGTalkServiceStatusForUser(cr, flag, cr.getUserId());
        }

        @Deprecated
        public static void setShowGTalkServiceStatusForUser(ContentResolver cr, boolean flag, int userHandle) {
            putIntForUser(cr, SHOW_GTALK_SERVICE_STATUS, flag ? 1 : 0, userHandle);
        }

        public static void getCloneToManagedProfileSettings(Set<String> outKeySet) {
            outKeySet.addAll(CLONE_TO_MANAGED_PROFILE);
        }

        public static void getCloneFromParentOnValueSettings(Map<String, String> outMap) {
            outMap.putAll(CLONE_FROM_PARENT_ON_VALUE);
        }

        public static boolean canWrite(Context context) {
            return Settings.isCallingPackageAllowedToWriteSettings(context, Process.myUid(), context.getOpPackageName(), false);
        }
    }

    /* loaded from: classes3.dex */
    public static final class Secure extends NameValueTable {
        public static final String ACCESSIBILITY_ALLOW_DIAGONAL_SCROLLING = "accessibility_allow_diagonal_scrolling";
        @Readable
        public static final String ACCESSIBILITY_AUTOCLICK_DELAY = "accessibility_autoclick_delay";
        @Readable
        public static final String ACCESSIBILITY_AUTOCLICK_ENABLED = "accessibility_autoclick_enabled";
        public static final String ACCESSIBILITY_BUTTON_MODE = "accessibility_button_mode";
        public static final int ACCESSIBILITY_BUTTON_MODE_FLOATING_MENU = 1;
        public static final int ACCESSIBILITY_BUTTON_MODE_GESTURE = 2;
        public static final int ACCESSIBILITY_BUTTON_MODE_NAVIGATION_BAR = 0;
        @Readable
        public static final String ACCESSIBILITY_BUTTON_TARGETS = "accessibility_button_targets";
        @Readable
        public static final String ACCESSIBILITY_BUTTON_TARGET_COMPONENT = "accessibility_button_target_component";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR = "accessibility_captioning_background_color";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_COLOR = "accessibility_captioning_edge_color";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_TYPE = "accessibility_captioning_edge_type";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_ENABLED = "accessibility_captioning_enabled";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_FONT_SCALE = "accessibility_captioning_font_scale";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR = "accessibility_captioning_foreground_color";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_LOCALE = "accessibility_captioning_locale";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_PRESET = "accessibility_captioning_preset";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_TYPEFACE = "accessibility_captioning_typeface";
        @Readable
        public static final String ACCESSIBILITY_CAPTIONING_WINDOW_COLOR = "accessibility_captioning_window_color";
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_DALTONIZER = "accessibility_display_daltonizer";
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_INVERSION_ENABLED = "accessibility_display_inversion_enabled";
        @Readable
        @Deprecated
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE = "accessibility_display_magnification_auto_update";
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED = "accessibility_display_magnification_enabled";
        @SystemApi
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_NAVBAR_ENABLED = "accessibility_display_magnification_navbar_enabled";
        @Readable
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE = "accessibility_display_magnification_scale";
        @Readable
        public static final String ACCESSIBILITY_ENABLED = "accessibility_enabled";
        public static final String ACCESSIBILITY_FLOATING_MENU_FADE_ENABLED = "accessibility_floating_menu_fade_enabled";
        public static final String ACCESSIBILITY_FLOATING_MENU_ICON_TYPE = "accessibility_floating_menu_icon_type";
        public static final String ACCESSIBILITY_FLOATING_MENU_MIGRATION_TOOLTIP_PROMPT = "accessibility_floating_menu_migration_tooltip_prompt";
        public static final String ACCESSIBILITY_FLOATING_MENU_OPACITY = "accessibility_floating_menu_opacity";
        public static final String ACCESSIBILITY_FLOATING_MENU_SIZE = "accessibility_floating_menu_size";
        @Readable
        public static final String ACCESSIBILITY_FONT_SCALING_HAS_BEEN_CHANGED = "accessibility_font_scaling_has_been_changed";
        @Readable
        public static final String ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED = "high_text_contrast_enabled";
        @Readable
        public static final String ACCESSIBILITY_INTERACTIVE_UI_TIMEOUT_MS = "accessibility_interactive_ui_timeout_ms";
        @Readable
        public static final String ACCESSIBILITY_LARGE_POINTER_ICON = "accessibility_large_pointer_icon";
        public static final String ACCESSIBILITY_MAGNIFICATION_ALWAYS_ON_ENABLED = "accessibility_magnification_always_on_enabled";
        @Readable
        public static final String ACCESSIBILITY_MAGNIFICATION_CAPABILITY = "accessibility_magnification_capability";
        public static final String ACCESSIBILITY_MAGNIFICATION_FOLLOW_TYPING_ENABLED = "accessibility_magnification_follow_typing_enabled";
        public static final String ACCESSIBILITY_MAGNIFICATION_JOYSTICK_ENABLED = "accessibility_magnification_joystick_enabled";
        @Readable
        public static final String ACCESSIBILITY_MAGNIFICATION_MODE = "accessibility_magnification_mode";
        public static final int ACCESSIBILITY_MAGNIFICATION_MODE_ALL = 3;
        public static final int ACCESSIBILITY_MAGNIFICATION_MODE_FULLSCREEN = 1;
        public static final int ACCESSIBILITY_MAGNIFICATION_MODE_NONE = 0;
        public static final int ACCESSIBILITY_MAGNIFICATION_MODE_WINDOW = 2;
        @Readable
        public static final String ACCESSIBILITY_NON_INTERACTIVE_UI_TIMEOUT_MS = "accessibility_non_interactive_ui_timeout_ms";
        @Readable
        public static final String ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN = "accessibility_shortcut_dialog_shown";
        @Readable
        public static final String ACCESSIBILITY_SHORTCUT_ON_LOCK_SCREEN = "accessibility_shortcut_on_lock_screen";
        @Readable
        public static final String ACCESSIBILITY_SHORTCUT_TARGET_MAGNIFICATION_CONTROLLER = "com.android.server.accessibility.MagnificationController";
        @Readable
        public static final String ACCESSIBILITY_SHORTCUT_TARGET_SERVICE = "accessibility_shortcut_target_service";
        public static final String ACCESSIBILITY_SHOW_WINDOW_MAGNIFICATION_PROMPT = "accessibility_show_window_magnification_prompt";
        @Readable
        public static final String ACCESSIBILITY_SOFT_KEYBOARD_MODE = "accessibility_soft_keyboard_mode";
        @Readable
        @Deprecated
        public static final String ACCESSIBILITY_SPEAK_PASSWORD = "speak_password";
        public static final String ACTIVE_UNLOCK_ON_BIOMETRIC_FAIL = "active_unlock_on_biometric_fail";
        public static final String ACTIVE_UNLOCK_ON_FACE_ACQUIRE_INFO = "active_unlock_on_face_acquire_info";
        public static final String ACTIVE_UNLOCK_ON_FACE_ERRORS = "active_unlock_on_face_errors";
        public static final String ACTIVE_UNLOCK_ON_UNLOCK_INTENT = "active_unlock_on_unlock_intent";
        public static final String ACTIVE_UNLOCK_ON_UNLOCK_INTENT_WHEN_BIOMETRIC_ENROLLED = "active_unlock_on_unlock_intent_when_biometric_enrolled";
        public static final String ACTIVE_UNLOCK_ON_WAKE = "active_unlock_on_wake";
        public static final String ACTIVE_UNLOCK_WAKEUPS_CONSIDERED_UNLOCK_INTENTS = "active_unlock_wakeups_considered_unlock_intents";
        public static final String ACTIVE_UNLOCK_WAKEUPS_TO_FORCE_DISMISS_KEYGUARD = "active_unlock_wakeups_to_force_dismiss_keyguard";
        public static final String ADAPTIVE_CHARGING_ENABLED = "adaptive_charging_enabled";
        public static final String ADAPTIVE_CONNECTIVITY_ENABLED = "adaptive_connectivity_enabled";
        @Readable
        public static final String ADAPTIVE_SLEEP = "adaptive_sleep";
        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        @Readable
        public static final String ALLOWED_GEOLOCATION_ORIGINS = "allowed_geolocation_origins";
        @Readable
        @Deprecated
        public static final String ALLOW_MOCK_LOCATION = "mock_location";
        public static final String ALWAYS_ON_VPN_APP = "always_on_vpn_app";
        @Readable
        public static final String ALWAYS_ON_VPN_LOCKDOWN = "always_on_vpn_lockdown";
        @Readable(maxTargetSdk = 31)
        public static final String ALWAYS_ON_VPN_LOCKDOWN_WHITELIST = "always_on_vpn_lockdown_whitelist";
        public static final String AMBIENT_CONTEXT_CONSENT_COMPONENT = "ambient_context_consent_component";
        public static final String AMBIENT_CONTEXT_EVENT_ARRAY_EXTRA_KEY = "ambient_context_event_array_key";
        public static final String AMBIENT_CONTEXT_PACKAGE_NAME_EXTRA_KEY = "ambient_context_package_name_key";
        @Readable
        public static final String ANDROID_ID = "android_id";
        @Readable
        public static final String ANR_SHOW_BACKGROUND = "anr_show_background";
        @Readable
        public static final String ASSISTANT = "assistant";
        @Readable
        public static final String ASSIST_DISCLOSURE_ENABLED = "assist_disclosure_enabled";
        @Readable
        public static final String ASSIST_GESTURE_ENABLED = "assist_gesture_enabled";
        @Readable
        public static final String ASSIST_GESTURE_SENSITIVITY = "assist_gesture_sensitivity";
        @SystemApi
        @Readable
        public static final String ASSIST_GESTURE_SETUP_COMPLETE = "assist_gesture_setup_complete";
        @Readable
        public static final String ASSIST_GESTURE_SILENCE_ALERTS_ENABLED = "assist_gesture_silence_alerts_enabled";
        @Readable
        public static final String ASSIST_GESTURE_WAKE_ENABLED = "assist_gesture_wake_enabled";
        public static final String ASSIST_HANDLES_LEARNING_EVENT_COUNT = "reminder_exp_learning_event_count";
        public static final String ASSIST_HANDLES_LEARNING_TIME_ELAPSED_MILLIS = "reminder_exp_learning_time_elapsed";
        public static final String ASSIST_LONG_PRESS_HOME_ENABLED = "assist_long_press_home_enabled";
        @Readable
        public static final String ASSIST_SCREENSHOT_ENABLED = "assist_screenshot_enabled";
        @Readable
        public static final String ASSIST_STRUCTURE_ENABLED = "assist_structure_enabled";
        public static final String ASSIST_TOUCH_GESTURE_ENABLED = "assist_touch_gesture_enabled";
        @Readable
        public static final String ATTENTIVE_TIMEOUT = "attentive_timeout";
        @SystemApi
        @Readable
        public static final String AUTOFILL_FEATURE_FIELD_CLASSIFICATION = "autofill_field_classification";
        @Readable
        public static final String AUTOFILL_SERVICE = "autofill_service";
        @Readable
        public static final String AUTOFILL_SERVICE_SEARCH_URI = "autofill_service_search_uri";
        @SystemApi
        @Readable
        public static final String AUTOFILL_USER_DATA_MAX_CATEGORY_COUNT = "autofill_user_data_max_category_count";
        @SystemApi
        @Readable
        public static final String AUTOFILL_USER_DATA_MAX_FIELD_CLASSIFICATION_IDS_SIZE = "autofill_user_data_max_field_classification_size";
        @SystemApi
        @Readable
        public static final String AUTOFILL_USER_DATA_MAX_USER_DATA_SIZE = "autofill_user_data_max_user_data_size";
        @SystemApi
        @Readable
        public static final String AUTOFILL_USER_DATA_MAX_VALUE_LENGTH = "autofill_user_data_max_value_length";
        @SystemApi
        @Readable
        public static final String AUTOFILL_USER_DATA_MIN_VALUE_LENGTH = "autofill_user_data_min_value_length";
        @Readable
        public static final String AUTOMATIC_STORAGE_MANAGER_BYTES_CLEARED = "automatic_storage_manager_bytes_cleared";
        @Readable
        public static final String AUTOMATIC_STORAGE_MANAGER_DAYS_TO_RETAIN = "automatic_storage_manager_days_to_retain";
        public static final int AUTOMATIC_STORAGE_MANAGER_DAYS_TO_RETAIN_DEFAULT = 90;
        @Readable
        public static final String AUTOMATIC_STORAGE_MANAGER_ENABLED = "automatic_storage_manager_enabled";
        @Readable
        public static final String AUTOMATIC_STORAGE_MANAGER_LAST_RUN = "automatic_storage_manager_last_run";
        @Readable
        public static final String AUTOMATIC_STORAGE_MANAGER_TURNED_OFF_BY_POLICY = "automatic_storage_manager_turned_off_by_policy";
        @SystemApi
        @Readable
        public static final String AUTO_REVOKE_DISABLED = "auto_revoke_disabled";
        @Readable
        public static final String AWARE_ENABLED = "aware_enabled";
        @Readable
        public static final String AWARE_LOCK_ENABLED = "aware_lock_enabled";
        @Readable
        public static final String AWARE_TAP_PAUSE_GESTURE_COUNT = "aware_tap_pause_gesture_count";
        @Readable
        public static final String AWARE_TAP_PAUSE_TOUCH_COUNT = "aware_tap_pause_touch_count";
        @Readable
        @Deprecated
        public static final String BACKGROUND_DATA = "background_data";
        @Readable
        public static final String BACKUP_AUTO_RESTORE = "backup_auto_restore";
        @Readable
        public static final String BACKUP_ENABLED = "backup_enabled";
        @Readable
        public static final String BACKUP_LOCAL_TRANSPORT_PARAMETERS = "backup_local_transport_parameters";
        @Readable
        public static final String BACKUP_MANAGER_CONSTANTS = "backup_manager_constants";
        @Readable
        public static final String BACKUP_PROVISIONED = "backup_provisioned";
        public static final String BACKUP_SCHEDULING_ENABLED = "backup_scheduling_enabled";
        @Readable
        public static final String BACKUP_TRANSPORT = "backup_transport";
        @Readable
        public static final String BACK_GESTURE_INSET_SCALE_LEFT = "back_gesture_inset_scale_left";
        @Readable
        public static final String BACK_GESTURE_INSET_SCALE_RIGHT = "back_gesture_inset_scale_right";
        @Readable
        public static final String BIOMETRIC_APP_ENABLED = "biometric_app_enabled";
        @Readable
        public static final String BIOMETRIC_DEBUG_ENABLED = "biometric_debug_enabled";
        @Readable
        public static final String BIOMETRIC_KEYGUARD_ENABLED = "biometric_keyguard_enabled";
        @Readable
        public static final String BIOMETRIC_VIRTUAL_ENABLED = "biometric_virtual_enabled";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable(maxTargetSdk = 31)
        public static final String BLUETOOTH_ADDRESS = "bluetooth_address";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable(maxTargetSdk = 31)
        public static final String BLUETOOTH_ADDR_VALID = "bluetooth_addr_valid";
        public static final String BLUETOOTH_LE_BROADCAST_APP_SOURCE_NAME = "bluetooth_le_broadcast_app_source_name";
        public static final String BLUETOOTH_LE_BROADCAST_CODE = "bluetooth_le_broadcast_code";
        public static final String BLUETOOTH_LE_BROADCAST_PROGRAM_INFO = "bluetooth_le_broadcast_program_info";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable(maxTargetSdk = 31)
        public static final String BLUETOOTH_NAME = "bluetooth_name";
        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";
        @Readable
        public static final String BLUETOOTH_ON_WHILE_DRIVING = "bluetooth_on_while_driving";
        @Readable
        public static final String BUBBLE_IMPORTANT_CONVERSATIONS = "bubble_important_conversations";
        @Readable
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        @Readable
        public static final String CALL_SCREENING_DEFAULT_COMPONENT = "call_screening_default_component";
        public static final String CAMERA_AUTOROTATE = "camera_autorotate";
        @Readable
        public static final String CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED = "camera_double_tap_power_gesture_disabled";
        @Readable
        public static final String CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED = "camera_double_twist_to_flip_enabled";
        @Readable
        public static final String CAMERA_GESTURE_DISABLED = "camera_gesture_disabled";
        @Readable
        public static final String CAMERA_LIFT_TRIGGER_ENABLED = "camera_lift_trigger_enabled";
        public static final int CAMERA_LIFT_TRIGGER_ENABLED_DEFAULT = 1;
        @Readable
        public static final String CARRIER_APPS_HANDLED = "carrier_apps_handled";
        @Readable
        public static final String CHARGING_SOUNDS_ENABLED = "charging_sounds_enabled";
        @Readable
        public static final String CHARGING_VIBRATION_ENABLED = "charging_vibration_enabled";
        public static final String CLIPBOARD_SHOW_ACCESS_NOTIFICATIONS = "clipboard_show_access_notifications";
        private static final Set<String> CLONE_TO_MANAGED_PROFILE;
        @Readable
        public static final String CMAS_ADDITIONAL_BROADCAST_PKG = "cmas_additional_broadcast_pkg";
        public static final String COMMUNAL_MODE_ENABLED = "communal_mode_enabled";
        public static final String COMMUNAL_MODE_TRUSTED_NETWORKS = "communal_mode_trusted_networks";
        @SystemApi
        @Readable
        public static final String COMPLETED_CATEGORY_PREFIX = "suggested.completed_category.";
        @Readable
        public static final String CONNECTIVITY_RELEASE_PENDING_INTENT_DELAY_MS = "connectivity_release_pending_intent_delay_ms";
        @Readable
        public static final String CONTENT_CAPTURE_ENABLED = "content_capture_enabled";
        public static final Uri CONTENT_URI;
        public static final String CONTRAST_LEVEL = "contrast_level";
        @Readable
        @Deprecated
        public static final String CONTROLS_ENABLED = "controls_enabled";
        public static final String CREDENTIAL_SERVICE = "credential_service";
        @Readable
        public static final String CROSS_PROFILE_CALENDAR_ENABLED = "cross_profile_calendar_enabled";
        public static final String CUSTOM_BUGREPORT_HANDLER_APP = "custom_bugreport_handler_app";
        public static final String CUSTOM_BUGREPORT_HANDLER_USER = "custom_bugreport_handler_user";
        @Readable
        public static final String DARK_MODE_DIALOG_SEEN = "dark_mode_dialog_seen";
        @Readable
        public static final String DARK_THEME_CUSTOM_END_TIME = "dark_theme_custom_end_time";
        @Readable
        public static final String DARK_THEME_CUSTOM_START_TIME = "dark_theme_custom_start_time";
        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        @Readable
        public static final String DEFAULT_INPUT_METHOD = "default_input_method";
        public static final String DEFAULT_VOICE_INPUT_METHOD = "default_voice_input_method";
        @Deprecated
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        @Readable
        public static final String DEVICE_PAIRED = "device_paired";
        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String DEVICE_STATE_ROTATION_LOCK = "device_state_rotation_lock";
        public static final int DEVICE_STATE_ROTATION_LOCK_IGNORED = 0;
        public static final int DEVICE_STATE_ROTATION_LOCK_LOCKED = 1;
        public static final int DEVICE_STATE_ROTATION_LOCK_UNLOCKED = 2;
        @Readable
        public static final String DIALER_DEFAULT_APPLICATION = "dialer_default_application";
        @Readable
        public static final String DISABLED_PRINT_SERVICES = "disabled_print_services";
        @Readable(maxTargetSdk = 33)
        public static final String DISABLED_SYSTEM_INPUT_METHODS = "disabled_system_input_methods";
        @Readable
        public static final String DISPLAY_DENSITY_FORCED = "display_density_forced";
        @Readable
        public static final String DISPLAY_WHITE_BALANCE_ENABLED = "display_white_balance_enabled";
        @Readable
        public static final String DOCKED_CLOCK_FACE = "docked_clock_face";
        public static final int DOCK_SETUP_COMPLETED = 10;
        public static final int DOCK_SETUP_INCOMPLETE = 4;
        public static final int DOCK_SETUP_NOT_STARTED = 0;
        public static final int DOCK_SETUP_PAUSED = 2;
        public static final int DOCK_SETUP_PROMPTED = 3;
        public static final int DOCK_SETUP_STARTED = 1;
        public static final String DOCK_SETUP_STATE = "dock_setup_state";
        public static final int DOCK_SETUP_TIMED_OUT = 11;
        @Readable
        public static final String DOUBLE_TAP_TO_WAKE = "double_tap_to_wake";
        @SystemApi
        @Readable
        public static final String DOZE_ALWAYS_ON = "doze_always_on";
        @Readable
        public static final String DOZE_DOUBLE_TAP_GESTURE = "doze_pulse_on_double_tap";
        @Readable
        public static final String DOZE_ENABLED = "doze_enabled";
        @Readable
        public static final String DOZE_PICK_UP_GESTURE = "doze_pulse_on_pick_up";
        @Readable
        public static final String DOZE_PULSE_ON_LONG_PRESS = "doze_pulse_on_long_press";
        public static final String DOZE_QUICK_PICKUP_GESTURE = "doze_quick_pickup_gesture";
        @Readable
        public static final String DOZE_TAP_SCREEN_GESTURE = "doze_tap_gesture";
        @Readable
        public static final String DOZE_WAKE_DISPLAY_GESTURE = "doze_wake_display_gesture";
        @Readable
        public static final String DOZE_WAKE_LOCK_SCREEN_GESTURE = "doze_wake_screen_gesture";
        @Readable
        public static final String EMERGENCY_ASSISTANCE_APPLICATION = "emergency_assistance_application";
        public static final String EMERGENCY_GESTURE_ENABLED = "emergency_gesture_enabled";
        public static final String EMERGENCY_GESTURE_SOUND_ENABLED = "emergency_gesture_sound_enabled";
        public static final String ENABLED_ACCESSIBILITY_AUDIO_DESCRIPTION_BY_DEFAULT = "enabled_accessibility_audio_description_by_default";
        @Readable
        public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
        @Readable(maxTargetSdk = 33)
        public static final String ENABLED_INPUT_METHODS = "enabled_input_methods";
        @Readable
        @Deprecated
        public static final String ENABLED_NOTIFICATION_ASSISTANT = "enabled_notification_assistant";
        @Readable
        @Deprecated
        public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        @Readable
        @Deprecated
        public static final String ENABLED_NOTIFICATION_POLICY_ACCESS_PACKAGES = "enabled_notification_policy_access_packages";
        @Readable
        public static final String ENABLED_PRINT_SERVICES = "enabled_print_services";
        @Readable
        public static final String ENABLED_VR_LISTENERS = "enabled_vr_listeners";
        @Readable
        public static final String ENHANCED_VOICE_PRIVACY_ENABLED = "enhanced_voice_privacy_enabled";
        public static final String EXTRA_AUTOMATIC_POWER_SAVE_MODE = "extra_automatic_power_save_mode";
        public static final String EXTRA_LOW_POWER_WARNING_ACKNOWLEDGED = "extra_low_power_warning_acknowledged";
        @Readable
        public static final String FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION = "face_unlock_always_require_confirmation";
        @Readable
        public static final String FACE_UNLOCK_APP_ENABLED = "face_unlock_app_enabled";
        @Readable
        public static final String FACE_UNLOCK_ATTENTION_REQUIRED = "face_unlock_attention_required";
        @Readable
        public static final String FACE_UNLOCK_DISMISSES_KEYGUARD = "face_unlock_dismisses_keyguard";
        @Readable
        public static final String FACE_UNLOCK_DIVERSITY_REQUIRED = "face_unlock_diversity_required";
        @Readable
        public static final String FACE_UNLOCK_KEYGUARD_ENABLED = "face_unlock_keyguard_enabled";
        @Readable
        public static final String FACE_UNLOCK_RE_ENROLL = "face_unlock_re_enroll";
        @Readable
        public static final String FINGERPRINT_SIDE_FPS_AUTH_DOWNTIME = "fingerprint_side_fps_auth_downtime";
        @Readable
        public static final String FINGERPRINT_SIDE_FPS_BP_POWER_WINDOW = "fingerprint_side_fps_bp_power_window";
        @Readable
        public static final String FINGERPRINT_SIDE_FPS_ENROLL_TAP_WINDOW = "fingerprint_side_fps_enroll_tap_window";
        @Readable
        public static final String FINGERPRINT_SIDE_FPS_KG_POWER_WINDOW = "fingerprint_side_fps_kg_power_window";
        @Readable
        public static final String FLASHLIGHT_AVAILABLE = "flashlight_available";
        @Readable
        public static final String FLASHLIGHT_ENABLED = "flashlight_enabled";
        @Readable
        public static final String FONT_WEIGHT_ADJUSTMENT = "font_weight_adjustment";
        @Readable
        public static final String GAME_DASHBOARD_ALWAYS_ON = "game_dashboard_always_on";
        @Readable
        public static final String GLOBAL_ACTIONS_PANEL_AVAILABLE = "global_actions_panel_available";
        @Readable
        public static final String GLOBAL_ACTIONS_PANEL_DEBUG_ENABLED = "global_actions_panel_debug_enabled";
        @Readable
        public static final String GLOBAL_ACTIONS_PANEL_ENABLED = "global_actions_panel_enabled";
        public static final String HBM_SETTING_KEY = "com.android.server.display.HBM_SETTING_KEY";
        public static final String HDMI_CEC_SET_MENU_LANGUAGE_DENYLIST = "hdmi_cec_set_menu_language_denylist";
        public static final String HEARING_AID_CALL_ROUTING = "hearing_aid_call_routing";
        public static final String HEARING_AID_MEDIA_ROUTING = "hearing_aid_media_routing";
        public static final String HEARING_AID_RINGTONE_ROUTING = "hearing_aid_ringtone_routing";
        public static final String HEARING_AID_SYSTEM_SOUNDS_ROUTING = "hearing_aid_system_sounds_routing";
        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";
        @SystemApi
        @Readable
        public static final String HUSH_GESTURE_USED = "hush_gesture_used";
        @Readable
        public static final String IMMERSIVE_MODE_CONFIRMATIONS = "immersive_mode_confirmations";
        @Readable
        public static final String INCALL_BACK_BUTTON_BEHAVIOR = "incall_back_button_behavior";
        public static final int INCALL_BACK_BUTTON_BEHAVIOR_DEFAULT = 0;
        public static final int INCALL_BACK_BUTTON_BEHAVIOR_HANGUP = 1;
        public static final int INCALL_BACK_BUTTON_BEHAVIOR_NONE = 0;
        @Readable
        public static final String INCALL_POWER_BUTTON_BEHAVIOR = "incall_power_button_behavior";
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT = 1;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_HANGUP = 2;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF = 1;
        @Readable
        public static final String INPUT_METHODS_SUBTYPE_HISTORY = "input_methods_subtype_history";
        @Readable
        public static final String INPUT_METHOD_SELECTOR_VISIBILITY = "input_method_selector_visibility";
        @Readable
        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        @SystemApi
        @Readable
        public static final String INSTANT_APPS_ENABLED = "instant_apps_enabled";
        public static final Set<String> INSTANT_APP_SETTINGS;
        @Readable
        public static final String IN_CALL_NOTIFICATION_ENABLED = "in_call_notification_enabled";
        @Readable
        public static final String KEYGUARD_SLICE_URI = "keyguard_slice_uri";
        public static final String KNOWN_TRUST_AGENTS_INITIALIZED = "known_trust_agents_initialized";
        @SystemApi
        @Readable
        public static final String LAST_SETUP_SHOWN = "last_setup_shown";
        public static final String LAUNCHER_TASKBAR_EDUCATION_SHOWING = "launcher_taskbar_education_showing";
        @Readable
        public static final String[] LEGACY_RESTORE_SETTINGS;
        @SystemApi
        @Readable
        @Deprecated
        public static final String LOCATION_ACCESS_CHECK_DELAY_MILLIS = "location_access_check_delay_millis";
        @SystemApi
        @Readable
        @Deprecated
        public static final String LOCATION_ACCESS_CHECK_INTERVAL_MILLIS = "location_access_check_interval_millis";
        @Readable
        public static final String LOCATION_CHANGER = "location_changer";
        public static final int LOCATION_CHANGER_QUICK_SETTINGS = 2;
        public static final int LOCATION_CHANGER_SYSTEM_SETTINGS = 1;
        public static final int LOCATION_CHANGER_UNKNOWN = 0;
        @Readable
        public static final String LOCATION_COARSE_ACCURACY_M = "locationCoarseAccuracy";
        @Readable
        @Deprecated
        public static final String LOCATION_MODE = "location_mode";
        @Deprecated
        public static final int LOCATION_MODE_BATTERY_SAVING = 2;
        @Deprecated
        public static final int LOCATION_MODE_HIGH_ACCURACY = 3;
        public static final int LOCATION_MODE_OFF = 0;
        @SystemApi
        public static final int LOCATION_MODE_ON = 3;
        @Deprecated
        public static final int LOCATION_MODE_SENSORS_ONLY = 1;
        @SystemApi
        @Readable
        @Deprecated
        public static final String LOCATION_PERMISSIONS_UPGRADE_TO_Q_MODE = "location_permissions_upgrade_to_q_mode";
        @Readable
        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        public static final String LOCATION_SHOW_SYSTEM_OPS = "locationShowSystemOps";
        public static final String LOCATION_TIME_ZONE_DETECTION_ENABLED = "location_time_zone_detection_enabled";
        public static final String LOCKSCREEN_ALLOW_TRIVIAL_CONTROLS = "lockscreen_allow_trivial_controls";
        public static final String LOCKSCREEN_SHOW_CONTROLS = "lockscreen_show_controls";
        public static final String LOCKSCREEN_SHOW_WALLET = "lockscreen_show_wallet";
        public static final String LOCKSCREEN_USE_DOUBLE_LINE_CLOCK = "lockscreen_use_double_line_clock";
        @Readable
        @Deprecated
        public static final String LOCK_BIOMETRIC_WEAK_FLAGS = "lock_biometric_weak_flags";
        @Readable
        @Deprecated
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
        @Readable
        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
        @Readable
        @Deprecated
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
        @SystemApi
        @Readable
        public static final String LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS = "lock_screen_allow_private_notifications";
        @Readable
        public static final String LOCK_SCREEN_ALLOW_REMOTE_INPUT = "lock_screen_allow_remote_input";
        @Readable
        @Deprecated
        public static final String LOCK_SCREEN_APPWIDGET_IDS = "lock_screen_appwidget_ids";
        @Readable
        public static final String LOCK_SCREEN_CUSTOM_CLOCK_FACE = "lock_screen_custom_clock_face";
        @Readable
        @Deprecated
        public static final String LOCK_SCREEN_FALLBACK_APPWIDGET_ID = "lock_screen_fallback_appwidget_id";
        @Readable
        public static final String LOCK_SCREEN_LOCK_AFTER_TIMEOUT = "lock_screen_lock_after_timeout";
        @Readable
        @Deprecated
        public static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
        @Readable
        @Deprecated
        public static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
        @SystemApi
        @Readable
        public static final String LOCK_SCREEN_SHOW_NOTIFICATIONS = "lock_screen_show_notifications";
        public static final String LOCK_SCREEN_SHOW_ONLY_UNSEEN_NOTIFICATIONS = "lock_screen_show_only_unseen_notifications";
        public static final String LOCK_SCREEN_SHOW_QR_CODE_SCANNER = "lock_screen_show_qr_code_scanner";
        @Readable
        public static final String LOCK_SCREEN_SHOW_SILENT_NOTIFICATIONS = "lock_screen_show_silent_notifications";
        @Readable
        @Deprecated
        public static final String LOCK_SCREEN_STICKY_APPWIDGET = "lock_screen_sticky_appwidget";
        public static final String LOCK_SCREEN_WEATHER_ENABLED = "lockscreen_weather_enabled";
        @Readable
        public static final String LOCK_SCREEN_WHEN_TRUST_LOST = "lock_screen_when_trust_lost";
        @Readable
        public static final String LOCK_TO_APP_EXIT_LOCKED = "lock_to_app_exit_locked";
        @Readable
        @Deprecated
        public static final String LOGGING_ID = "logging_id";
        @Readable
        public static final String LONG_PRESS_TIMEOUT = "long_press_timeout";
        @Readable
        public static final String LOW_POWER_MANUAL_ACTIVATION_COUNT = "low_power_manual_activation_count";
        @Readable
        public static final String LOW_POWER_WARNING_ACKNOWLEDGED = "low_power_warning_acknowledged";
        @Readable
        public static final String MANAGED_PROFILE_CONTACT_REMOTE_SEARCH = "managed_profile_contact_remote_search";
        @Readable
        public static final String MANAGED_PROVISIONING_DPC_DOWNLOADED = "managed_provisioning_dpc_downloaded";
        @Readable
        public static final String MANUAL_RINGER_TOGGLE_COUNT = "manual_ringer_toggle_count";
        public static final int MATCH_CONTENT_FRAMERATE_ALWAYS = 2;
        public static final int MATCH_CONTENT_FRAMERATE_NEVER = 0;
        public static final int MATCH_CONTENT_FRAMERATE_SEAMLESSS_ONLY = 1;
        public static final String MATCH_CONTENT_FRAME_RATE = "match_content_frame_rate";
        public static final String MEDIA_CONTROLS_LOCK_SCREEN = "media_controls_lock_screen";
        public static final String MEDIA_CONTROLS_RECOMMENDATION = "qs_media_recommend";
        @Readable
        public static final String MEDIA_CONTROLS_RESUME = "qs_media_resumption";
        @Readable
        public static final String MINIMAL_POST_PROCESSING_ALLOWED = "minimal_post_processing_allowed";
        @Readable
        public static final String MOUNT_PLAY_NOTIFICATION_SND = "mount_play_not_snd";
        @Readable
        public static final String MOUNT_UMS_AUTOSTART = "mount_ums_autostart";
        @Readable
        public static final String MOUNT_UMS_NOTIFY_ENABLED = "mount_ums_notify_enabled";
        @Readable
        public static final String MOUNT_UMS_PROMPT = "mount_ums_prompt";
        private static final HashSet<String> MOVED_TO_GLOBAL;
        private static final HashSet<String> MOVED_TO_LOCK_SETTINGS;
        @Readable
        public static final String MULTI_PRESS_TIMEOUT = "multi_press_timeout";
        @Readable
        public static final String NAS_SETTINGS_UPDATED = "nas_settings_updated";
        @Readable
        public static final String NAVIGATION_MODE = "navigation_mode";
        public static final String NAV_BAR_FORCE_VISIBLE = "nav_bar_force_visible";
        public static final String NAV_BAR_KIDS_MODE = "nav_bar_kids_mode";
        public static final String NEARBY_FAST_PAIR_SETTINGS_DEVICES_COMPONENT = "nearby_fast_pair_settings_devices_component";
        @Readable
        public static final String NEARBY_SHARING_COMPONENT = "nearby_sharing_component";
        public static final String NEARBY_SHARING_SLICE_URI = "nearby_sharing_slice_uri";
        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String NFC_PAYMENT_DEFAULT_COMPONENT = "nfc_payment_default_component";
        @Readable
        public static final String NFC_PAYMENT_FOREGROUND = "nfc_payment_foreground";
        @Readable
        public static final String NIGHT_DISPLAY_ACTIVATED = "night_display_activated";
        @Readable
        public static final String NIGHT_DISPLAY_AUTO_MODE = "night_display_auto_mode";
        @Readable
        public static final String NIGHT_DISPLAY_COLOR_TEMPERATURE = "night_display_color_temperature";
        @Readable
        public static final String NIGHT_DISPLAY_CUSTOM_END_TIME = "night_display_custom_end_time";
        @Readable
        public static final String NIGHT_DISPLAY_CUSTOM_START_TIME = "night_display_custom_start_time";
        @Readable
        public static final String NIGHT_DISPLAY_LAST_ACTIVATED_TIME = "night_display_last_activated_time";
        @Readable
        public static final String NOTIFICATION_BADGING = "notification_badging";
        @Readable
        public static final String NOTIFICATION_BUBBLES = "notification_bubbles";
        @Readable
        public static final String NOTIFICATION_DISMISS_RTL = "notification_dismiss_rtl";
        @Readable
        public static final String NOTIFICATION_HISTORY_ENABLED = "notification_history_enabled";
        @Readable
        public static final String NOTIFIED_NON_ACCESSIBILITY_CATEGORY_SERVICES = "notified_non_accessibility_category_services";
        @Readable
        public static final String NUM_ROTATION_SUGGESTIONS_ACCEPTED = "num_rotation_suggestions_accepted";
        @SystemApi
        @Readable
        public static final String ODI_CAPTIONS_ENABLED = "odi_captions_enabled";
        public static final String ODI_CAPTIONS_VOLUME_UI_ENABLED = "odi_captions_volume_ui_enabled";
        public static final String ONE_HANDED_MODE_ACTIVATED = "one_handed_mode_activated";
        public static final String ONE_HANDED_MODE_ENABLED = "one_handed_mode_enabled";
        public static final String ONE_HANDED_MODE_TIMEOUT = "one_handed_mode_timeout";
        public static final String ONE_HANDED_TUTORIAL_SHOW_COUNT = "one_handed_tutorial_show_count";
        @Readable
        public static final String PACKAGES_TO_CLEAR_DATA_BEFORE_FULL_RESTORE = "packages_to_clear_data_before_full_restore";
        @Readable
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
        @Readable
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
        @Readable
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        @Readable
        public static final String PAYMENT_SERVICE_SEARCH_URI = "payment_service_search_uri";
        @Readable
        public static final String PEOPLE_STRIP = "people_strip";
        @Readable
        public static final String POWER_MENU_LOCKED_SHOW_CONTENT = "power_menu_locked_show_content";
        @Readable
        public static final String PREFERRED_TTY_MODE = "preferred_tty_mode";
        @Readable
        public static final String PRINT_SERVICE_SEARCH_URI = "print_service_search_uri";
        @Readable
        public static final String QS_AUTO_ADDED_TILES = "qs_auto_tiles";
        @Readable(maxTargetSdk = 33)
        public static final String QS_TILES = "sysui_qs_tiles";
        public static final String REDUCE_BRIGHT_COLORS_ACTIVATED = "reduce_bright_colors_activated";
        public static final String REDUCE_BRIGHT_COLORS_LEVEL = "reduce_bright_colors_level";
        public static final String REDUCE_BRIGHT_COLORS_PERSIST_ACROSS_REBOOTS = "reduce_bright_colors_persist_across_reboots";
        public static final String RELEASE_COMPRESS_BLOCKS_ON_INSTALL = "release_compress_blocks_on_install";
        @Readable
        public static final String RTT_CALLING_MODE = "rtt_calling_mode";
        @Readable
        public static final String SCREENSAVER_ACTIVATE_ON_DOCK = "screensaver_activate_on_dock";
        @Readable
        public static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_activate_on_sleep";
        public static final String SCREENSAVER_COMPLICATIONS_ENABLED = "screensaver_complications_enabled";
        @Readable
        public static final String SCREENSAVER_COMPONENTS = "screensaver_components";
        @Readable
        public static final String SCREENSAVER_DEFAULT_COMPONENT = "screensaver_default_component";
        @Readable
        public static final String SCREENSAVER_ENABLED = "screensaver_enabled";
        @Readable
        public static final String SEARCH_GLOBAL_SEARCH_ACTIVITY = "search_global_search_activity";
        @Readable
        public static final String SEARCH_MAX_RESULTS_PER_SOURCE = "search_max_results_per_source";
        @Readable
        public static final String SEARCH_MAX_RESULTS_TO_DISPLAY = "search_max_results_to_display";
        @Readable
        public static final String SEARCH_MAX_SHORTCUTS_RETURNED = "search_max_shortcuts_returned";
        @Readable
        public static final String SEARCH_MAX_SOURCE_EVENT_AGE_MILLIS = "search_max_source_event_age_millis";
        @Readable
        public static final String SEARCH_MAX_STAT_AGE_MILLIS = "search_max_stat_age_millis";
        @Readable
        public static final String SEARCH_MIN_CLICKS_FOR_SOURCE_RANKING = "search_min_clicks_for_source_ranking";
        @Readable
        public static final String SEARCH_MIN_IMPRESSIONS_FOR_SOURCE_RANKING = "search_min_impressions_for_source_ranking";
        @Readable
        public static final String SEARCH_NUM_PROMOTED_SOURCES = "search_num_promoted_sources";
        @Readable
        public static final String SEARCH_PER_SOURCE_CONCURRENT_QUERY_LIMIT = "search_per_source_concurrent_query_limit";
        @Readable
        public static final String SEARCH_PREFILL_MILLIS = "search_prefill_millis";
        @Readable
        public static final String SEARCH_PROMOTED_SOURCE_DEADLINE_MILLIS = "search_promoted_source_deadline_millis";
        @Readable
        public static final String SEARCH_QUERY_THREAD_CORE_POOL_SIZE = "search_query_thread_core_pool_size";
        @Readable
        public static final String SEARCH_QUERY_THREAD_MAX_POOL_SIZE = "search_query_thread_max_pool_size";
        @Readable
        public static final String SEARCH_SHORTCUT_REFRESH_CORE_POOL_SIZE = "search_shortcut_refresh_core_pool_size";
        @Readable
        public static final String SEARCH_SHORTCUT_REFRESH_MAX_POOL_SIZE = "search_shortcut_refresh_max_pool_size";
        @Readable
        public static final String SEARCH_SOURCE_TIMEOUT_MILLIS = "search_source_timeout_millis";
        @Readable
        public static final String SEARCH_THREAD_KEEPALIVE_SECONDS = "search_thread_keepalive_seconds";
        @Readable
        public static final String SEARCH_WEB_RESULTS_OVERRIDE_LIMIT = "search_web_results_override_limit";
        @Readable
        @Deprecated
        public static final String SECURE_FRP_MODE = "secure_frp_mode";
        @Readable
        public static final String SELECTED_INPUT_METHOD_SUBTYPE = "selected_input_method_subtype";
        @Readable
        public static final String SELECTED_SPELL_CHECKER = "selected_spell_checker";
        @Readable
        public static final String SELECTED_SPELL_CHECKER_SUBTYPE = "selected_spell_checker_subtype";
        @Readable
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        public static final String SFPS_PERFORMANT_AUTH_ENABLED = "sfps_performant_auth_enabled_v2";
        @Readable
        public static final String SHOW_FIRST_CRASH_DIALOG_DEV_OPTION = "show_first_crash_dialog_dev_option";
        @Readable
        public static final String SHOW_IME_WITH_HARD_KEYBOARD = "show_ime_with_hard_keyboard";
        @Readable
        public static final String SHOW_MEDIA_WHEN_BYPASSING = "show_media_when_bypassing";
        public static final int SHOW_MODE_AUTO = 0;
        public static final int SHOW_MODE_HIDDEN = 1;
        @Readable
        public static final String SHOW_NOTE_ABOUT_NOTIFICATION_HIDING = "show_note_about_notification_hiding";
        @Readable
        public static final String SHOW_NOTIFICATION_SNOOZE = "show_notification_snooze";
        public static final String SHOW_QR_CODE_SCANNER_SETTING = "show_qr_code_scanner_setting";
        @Readable
        public static final String SHOW_ROTATION_SUGGESTIONS = "show_rotation_suggestions";
        public static final int SHOW_ROTATION_SUGGESTIONS_DEFAULT = 1;
        public static final int SHOW_ROTATION_SUGGESTIONS_DISABLED = 0;
        public static final int SHOW_ROTATION_SUGGESTIONS_ENABLED = 1;
        @Readable
        public static final String SHOW_ZEN_SETTINGS_SUGGESTION = "show_zen_settings_suggestion";
        @Readable
        public static final String SHOW_ZEN_UPGRADE_NOTIFICATION = "show_zen_upgrade_notification";
        @Readable
        public static final String SILENCE_ALARMS_GESTURE_COUNT = "silence_alarms_gesture_count";
        @Readable
        public static final String SILENCE_ALARMS_TOUCH_COUNT = "silence_alarms_touch_count";
        @Readable
        public static final String SILENCE_CALL_GESTURE_COUNT = "silence_call_gesture_count";
        @Readable
        public static final String SILENCE_CALL_TOUCH_COUNT = "silence_call_touch_count";
        @Readable
        public static final String SILENCE_GESTURE = "silence_gesture";
        @Readable
        public static final String SILENCE_TIMER_GESTURE_COUNT = "silence_timer_gesture_count";
        @Readable
        public static final String SILENCE_TIMER_TOUCH_COUNT = "silence_timer_touch_count";
        public static final String SKIP_ACCESSIBILITY_SHORTCUT_DIALOG_TIMEOUT_RESTRICTION = "skip_accessibility_shortcut_dialog_timeout_restriction";
        @Readable
        public static final String SKIP_DIRECTION = "skip_gesture_direction";
        @Readable
        public static final String SKIP_FIRST_USE_HINTS = "skip_first_use_hints";
        @Readable
        public static final String SKIP_GESTURE = "skip_gesture";
        @Readable
        public static final String SKIP_GESTURE_COUNT = "skip_gesture_count";
        @Readable
        public static final String SKIP_TOUCH_COUNT = "skip_touch_count";
        @Readable
        public static final String SLEEP_TIMEOUT = "sleep_timeout";
        @Readable
        public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
        public static final String SPATIAL_AUDIO_ENABLED = "spatial_audio_enabled";
        @Readable
        public static final String SPELL_CHECKER_ENABLED = "spell_checker_enabled";
        public static final String STATUS_BAR_SHOW_VIBRATE_ICON = "status_bar_show_vibrate_icon";
        public static final String STYLUS_BUTTONS_ENABLED = "stylus_buttons_enabled";
        @Readable
        public static final String SUPPRESS_AUTO_BATTERY_SAVER_SUGGESTION = "suppress_auto_battery_saver_suggestion";
        @Readable
        public static final String SUPPRESS_DOZE = "suppress_doze";
        public static final String SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED = "swipe_bottom_to_notification_enabled";
        @Readable
        public static final String SYNC_PARENT_SOUNDS = "sync_parent_sounds";
        @Readable
        public static final String SYSTEM_NAVIGATION_KEYS_ENABLED = "system_navigation_keys_enabled";
        public static final String TAPS_APP_TO_EXIT = "taps_app_to_exit";
        @Readable
        public static final String TAP_GESTURE = "tap_gesture";
        @SystemApi
        @Readable
        public static final String THEME_CUSTOMIZATION_OVERLAY_PACKAGES = "theme_customization_overlay_packages";
        public static final String TIMEOUT_TO_DOCK_USER = "timeout_to_dock_user";
        @Readable
        public static final String TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled";
        @Readable
        public static final String TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES = "touch_exploration_granted_accessibility_services";
        public static final String TRACKPAD_GESTURE_BACK_ENABLED = "trackpad_gesture_back_enabled";
        public static final String TRACKPAD_GESTURE_HOME_ENABLED = "trackpad_gesture_home_enabled";
        public static final String TRACKPAD_GESTURE_NOTIFICATION_ENABLED = "trackpad_gesture_notification_enabled";
        public static final String TRACKPAD_GESTURE_OVERVIEW_ENABLED = "trackpad_gesture_overview_enabled";
        public static final String TRACKPAD_GESTURE_QUICK_SWITCH_ENABLED = "trackpad_gesture_quick_switch_enabled";
        @Readable
        public static final String TRUST_AGENTS_EXTEND_UNLOCK = "trust_agents_extend_unlock";
        @Readable
        public static final String TRUST_AGENTS_INITIALIZED = "trust_agents_initialized";
        @Readable
        @Deprecated
        public static final String TTS_DEFAULT_COUNTRY = "tts_default_country";
        @Readable
        @Deprecated
        public static final String TTS_DEFAULT_LANG = "tts_default_lang";
        @Readable
        public static final String TTS_DEFAULT_LOCALE = "tts_default_locale";
        @Readable
        public static final String TTS_DEFAULT_PITCH = "tts_default_pitch";
        @Readable
        public static final String TTS_DEFAULT_RATE = "tts_default_rate";
        @Readable
        public static final String TTS_DEFAULT_SYNTH = "tts_default_synth";
        @Readable
        @Deprecated
        public static final String TTS_DEFAULT_VARIANT = "tts_default_variant";
        @Readable
        public static final String TTS_ENABLED_PLUGINS = "tts_enabled_plugins";
        @Readable
        @Deprecated
        public static final String TTS_USE_DEFAULTS = "tts_use_defaults";
        @Readable
        public static final String TTY_MODE_ENABLED = "tty_mode_enabled";
        @Readable
        public static final String TV_APP_USES_NON_SYSTEM_INPUTS = "tv_app_uses_non_system_inputs";
        @Readable
        public static final String TV_INPUT_CUSTOM_LABELS = "tv_input_custom_labels";
        @Readable
        public static final String TV_INPUT_HIDDEN_INPUTS = "tv_input_hidden_inputs";
        @Readable
        public static final String TV_USER_SETUP_COMPLETE = "tv_user_setup_complete";
        @Readable
        public static final String UI_NIGHT_MODE = "ui_night_mode";
        @Readable
        public static final String UI_NIGHT_MODE_CUSTOM_TYPE = "ui_night_mode_custom_type";
        public static final String UI_NIGHT_MODE_LAST_COMPUTED = "ui_night_mode_last_computed";
        @Readable
        public static final String UI_NIGHT_MODE_OVERRIDE_OFF = "ui_night_mode_override_off";
        @Readable
        public static final String UI_NIGHT_MODE_OVERRIDE_ON = "ui_night_mode_override_on";
        @SystemApi
        @Readable
        public static final String UI_TRANSLATION_ENABLED = "ui_translation_enabled";
        @Readable
        public static final String UNKNOWN_SOURCES_DEFAULT_REVERSED = "unknown_sources_default_reversed";
        @Readable
        public static final String UNSAFE_VOLUME_MUSIC_ACTIVE_MS = "unsafe_volume_music_active_ms";
        @Readable
        public static final String USB_AUDIO_AUTOMATIC_ROUTING_DISABLED = "usb_audio_automatic_routing_disabled";
        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        @SystemApi
        @Readable
        public static final String USER_SETUP_COMPLETE = "user_setup_complete";
        @SystemApi
        public static final int USER_SETUP_PERSONALIZATION_COMPLETE = 10;
        @SystemApi
        public static final int USER_SETUP_PERSONALIZATION_NOT_STARTED = 0;
        @SystemApi
        public static final int USER_SETUP_PERSONALIZATION_PAUSED = 2;
        @SystemApi
        public static final int USER_SETUP_PERSONALIZATION_STARTED = 1;
        @SystemApi
        @Readable
        public static final String USER_SETUP_PERSONALIZATION_STATE = "user_setup_personalization_state";
        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        @Readable
        public static final String VOICE_INTERACTION_SERVICE = "voice_interaction_service";
        @Readable
        public static final String VOICE_RECOGNITION_SERVICE = "voice_recognition_service";
        @SystemApi
        @Readable
        public static final String VOLUME_HUSH_GESTURE = "volume_hush_gesture";
        @SystemApi
        public static final int VOLUME_HUSH_MUTE = 2;
        @SystemApi
        public static final int VOLUME_HUSH_OFF = 0;
        @SystemApi
        public static final int VOLUME_HUSH_VIBRATE = 1;
        @Readable
        public static final String VR_DISPLAY_MODE = "vr_display_mode";
        public static final int VR_DISPLAY_MODE_LOW_PERSISTENCE = 0;
        public static final int VR_DISPLAY_MODE_OFF = 1;
        @Readable
        public static final String WAKE_GESTURE_ENABLED = "wake_gesture_enabled";
        public static final String WEAR_TALKBACK_ENABLED = "wear_talkback_enabled";
        @Deprecated
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";
        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        @Deprecated
        public static final String WIFI_ON = "wifi_on";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
        @Readable
        @Deprecated
        public static final String WIFI_WATCHDOG_WATCH_LIST = "wifi_watchdog_watch_list";
        @Readable
        public static final String ZEN_DURATION = "zen_duration";
        public static final int ZEN_DURATION_FOREVER = 0;
        public static final int ZEN_DURATION_PROMPT = -1;
        @Readable
        public static final String ZEN_SETTINGS_SUGGESTION_VIEWED = "zen_settings_suggestion_viewed";
        @Readable
        public static final String ZEN_SETTINGS_UPDATED = "zen_settings_updated";
        private static final NameValueCache sNameValueCache;
        private static final ContentProviderHolder sProviderHolder;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface DeviceStateRotationLockSetting {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface DockSetupState {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface UserSetupPersonalization {
        }

        static {
            Uri parse = Uri.parse("content://settings/secure");
            CONTENT_URI = parse;
            ContentProviderHolder contentProviderHolder = new ContentProviderHolder(parse);
            sProviderHolder = contentProviderHolder;
            sNameValueCache = new NameValueCache(parse, Settings.CALL_METHOD_GET_SECURE, Settings.CALL_METHOD_PUT_SECURE, Settings.CALL_METHOD_DELETE_SECURE, contentProviderHolder, Secure.class);
            HashSet<String> hashSet = new HashSet<>(3);
            MOVED_TO_LOCK_SETTINGS = hashSet;
            hashSet.add("lock_pattern_autolock");
            hashSet.add("lock_pattern_visible_pattern");
            hashSet.add("lock_pattern_tactile_feedback_enabled");
            HashSet<String> hashSet2 = new HashSet<>();
            MOVED_TO_GLOBAL = hashSet2;
            hashSet2.add("adb_enabled");
            hashSet2.add(Global.ASSISTED_GPS_ENABLED);
            hashSet2.add("bluetooth_on");
            hashSet2.add(Global.CDMA_CELL_BROADCAST_SMS);
            hashSet2.add(Global.CDMA_ROAMING_MODE);
            hashSet2.add(Global.CDMA_SUBSCRIPTION_MODE);
            hashSet2.add(Global.DATA_ACTIVITY_TIMEOUT_MOBILE);
            hashSet2.add(Global.DATA_ACTIVITY_TIMEOUT_WIFI);
            hashSet2.add("data_roaming");
            hashSet2.add("development_settings_enabled");
            hashSet2.add("device_provisioned");
            hashSet2.add(Global.DISPLAY_SIZE_FORCED);
            hashSet2.add(Global.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
            hashSet2.add(Global.DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE);
            hashSet2.add(Global.MOBILE_DATA);
            hashSet2.add(Global.NETSTATS_DEV_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_DEV_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_DEV_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_DEV_ROTATE_AGE);
            hashSet2.add(Global.NETSTATS_ENABLED);
            hashSet2.add(Global.NETSTATS_GLOBAL_ALERT_BYTES);
            hashSet2.add(Global.NETSTATS_POLL_INTERVAL);
            hashSet2.add(Global.NETSTATS_SAMPLE_ENABLED);
            hashSet2.add(Global.NETSTATS_TIME_CACHE_MAX_AGE);
            hashSet2.add(Global.NETSTATS_UID_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_UID_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_UID_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_UID_ROTATE_AGE);
            hashSet2.add(Global.NETSTATS_UID_TAG_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_UID_TAG_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_UID_TAG_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_UID_TAG_ROTATE_AGE);
            hashSet2.add("network_preference");
            hashSet2.add(Global.NITZ_UPDATE_DIFF);
            hashSet2.add(Global.NITZ_UPDATE_SPACING);
            hashSet2.add(Global.NTP_SERVER);
            hashSet2.add(Global.NTP_TIMEOUT);
            hashSet2.add(Global.PDP_WATCHDOG_ERROR_POLL_COUNT);
            hashSet2.add(Global.PDP_WATCHDOG_LONG_POLL_INTERVAL_MS);
            hashSet2.add(Global.PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT);
            hashSet2.add(Global.PDP_WATCHDOG_POLL_INTERVAL_MS);
            hashSet2.add(Global.PDP_WATCHDOG_TRIGGER_PACKET_COUNT);
            hashSet2.add(Global.SETUP_PREPAID_DATA_SERVICE_URL);
            hashSet2.add(Global.SETUP_PREPAID_DETECTION_REDIR_HOST);
            hashSet2.add(Global.SETUP_PREPAID_DETECTION_TARGET_URL);
            hashSet2.add(Global.TETHER_DUN_APN);
            hashSet2.add(Global.TETHER_DUN_REQUIRED);
            hashSet2.add(Global.TETHER_SUPPORTED);
            hashSet2.add("usb_mass_storage_enabled");
            hashSet2.add("use_google_mail");
            hashSet2.add(Global.WIFI_COUNTRY_CODE);
            hashSet2.add(Global.WIFI_FRAMEWORK_SCAN_INTERVAL_MS);
            hashSet2.add(Global.WIFI_FREQUENCY_BAND);
            hashSet2.add("wifi_idle_ms");
            hashSet2.add("wifi_max_dhcp_retry_count");
            hashSet2.add("wifi_mobile_data_transition_wakelock_timeout_ms");
            hashSet2.add("wifi_networks_available_notification_on");
            hashSet2.add("wifi_networks_available_repeat_delay");
            hashSet2.add("wifi_num_open_networks_kept");
            hashSet2.add("wifi_on");
            hashSet2.add(Global.WIFI_P2P_DEVICE_NAME);
            hashSet2.add(Global.WIFI_SUPPLICANT_SCAN_INTERVAL_MS);
            hashSet2.add(Global.WIFI_VERBOSE_LOGGING_ENABLED);
            hashSet2.add(Global.WIFI_ENHANCED_AUTO_JOIN);
            hashSet2.add(Global.WIFI_NETWORK_SHOW_RSSI);
            hashSet2.add("wifi_watchdog_on");
            hashSet2.add(Global.WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED);
            hashSet2.add(Global.WIFI_P2P_PENDING_FACTORY_RESET);
            hashSet2.add(Global.WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON);
            hashSet2.add(Global.PACKAGE_VERIFIER_TIMEOUT);
            hashSet2.add(Global.PACKAGE_VERIFIER_DEFAULT_RESPONSE);
            hashSet2.add(Global.DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS);
            hashSet2.add(Global.DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS);
            hashSet2.add(Global.GPRS_REGISTER_CHECK_PERIOD_MS);
            hashSet2.add(Global.WTF_IS_FATAL);
            hashSet2.add(Global.BATTERY_DISCHARGE_DURATION_THRESHOLD);
            hashSet2.add(Global.BATTERY_DISCHARGE_THRESHOLD);
            hashSet2.add(Global.SEND_ACTION_APP_ERROR);
            hashSet2.add(Global.DROPBOX_AGE_SECONDS);
            hashSet2.add(Global.DROPBOX_MAX_FILES);
            hashSet2.add(Global.DROPBOX_QUOTA_KB);
            hashSet2.add(Global.DROPBOX_QUOTA_PERCENT);
            hashSet2.add(Global.DROPBOX_RESERVE_PERCENT);
            hashSet2.add(Global.DROPBOX_TAG_PREFIX);
            hashSet2.add(Global.ERROR_LOGCAT_PREFIX);
            hashSet2.add(Global.SYS_FREE_STORAGE_LOG_INTERVAL);
            hashSet2.add(Global.DISK_FREE_CHANGE_REPORTING_THRESHOLD);
            hashSet2.add(Global.SYS_STORAGE_THRESHOLD_PERCENTAGE);
            hashSet2.add(Global.SYS_STORAGE_THRESHOLD_MAX_BYTES);
            hashSet2.add(Global.SYS_STORAGE_FULL_THRESHOLD_BYTES);
            hashSet2.add(Global.SYNC_MAX_RETRY_DELAY_IN_SECONDS);
            hashSet2.add(Global.CONNECTIVITY_CHANGE_DELAY);
            hashSet2.add(Global.CAPTIVE_PORTAL_DETECTION_ENABLED);
            hashSet2.add(Global.CAPTIVE_PORTAL_SERVER);
            hashSet2.add(Global.SET_INSTALL_LOCATION);
            hashSet2.add(Global.DEFAULT_INSTALL_LOCATION);
            hashSet2.add(Global.INET_CONDITION_DEBOUNCE_UP_DELAY);
            hashSet2.add(Global.INET_CONDITION_DEBOUNCE_DOWN_DELAY);
            hashSet2.add(Global.READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT);
            hashSet2.add("http_proxy");
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_HOST);
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_PORT);
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_EXCLUSION_LIST);
            hashSet2.add(Global.SET_GLOBAL_HTTP_PROXY);
            hashSet2.add(Global.DEFAULT_DNS_SERVER);
            hashSet2.add(Global.PREFERRED_NETWORK_MODE);
            hashSet2.add(Global.WEBVIEW_DATA_REDUCTION_PROXY_KEY);
            hashSet2.add("secure_frp_mode");
            LEGACY_RESTORE_SETTINGS = new String[]{ENABLED_NOTIFICATION_LISTENERS, ENABLED_NOTIFICATION_ASSISTANT, ENABLED_NOTIFICATION_POLICY_ACCESS_PACKAGES};
            ArraySet arraySet = new ArraySet();
            CLONE_TO_MANAGED_PROFILE = arraySet;
            arraySet.add(ACCESSIBILITY_ENABLED);
            arraySet.add(ALLOW_MOCK_LOCATION);
            arraySet.add(ALLOWED_GEOLOCATION_ORIGINS);
            arraySet.add(CONTENT_CAPTURE_ENABLED);
            arraySet.add(ENABLED_ACCESSIBILITY_SERVICES);
            arraySet.add(LOCATION_CHANGER);
            arraySet.add(LOCATION_MODE);
            arraySet.add(SHOW_IME_WITH_HARD_KEYBOARD);
            arraySet.add("notification_bubbles");
            ArraySet arraySet2 = new ArraySet();
            INSTANT_APP_SETTINGS = arraySet2;
            arraySet2.add(ENABLED_ACCESSIBILITY_SERVICES);
            arraySet2.add(ACCESSIBILITY_SPEAK_PASSWORD);
            arraySet2.add(ACCESSIBILITY_DISPLAY_INVERSION_ENABLED);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_ENABLED);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_PRESET);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_EDGE_TYPE);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_EDGE_COLOR);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_LOCALE);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_TYPEFACE);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_FONT_SCALE);
            arraySet2.add(ACCESSIBILITY_CAPTIONING_WINDOW_COLOR);
            arraySet2.add(ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED);
            arraySet2.add(ACCESSIBILITY_DISPLAY_DALTONIZER);
            arraySet2.add(ACCESSIBILITY_AUTOCLICK_DELAY);
            arraySet2.add(ACCESSIBILITY_AUTOCLICK_ENABLED);
            arraySet2.add(ACCESSIBILITY_LARGE_POINTER_ICON);
            arraySet2.add(DEFAULT_INPUT_METHOD);
            arraySet2.add(ENABLED_INPUT_METHODS);
            arraySet2.add("android_id");
            arraySet2.add(ALLOW_MOCK_LOCATION);
        }

        public static void getMovedToGlobalSettings(Set<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
        }

        public static void getMovedToSystemSettings(Set<String> outKeySet) {
        }

        public static void clearProviderForTest() {
            sProviderHolder.clearProviderForTest();
            sNameValueCache.clearGenerationTrackerForTest();
        }

        public static void getPublicSettings(Set<String> allKeys, Set<String> readableKeys, ArrayMap<String, Integer> readableKeysWithMaxTargetSdk) {
            Settings.getPublicSettingsForClass(Secure.class, allKeys, readableKeys, readableKeysWithMaxTargetSdk);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, resolver.getUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global.");
                return Global.getStringForUser(resolver, name, userHandle);
            } else if (MOVED_TO_LOCK_SETTINGS.contains(name) && Process.myUid() != 1000) {
                Application application = ActivityThread.currentApplication();
                boolean isPreMnc = (application == null || application.getApplicationInfo() == null || application.getApplicationInfo().targetSdkVersion > 22) ? false : true;
                if (isPreMnc) {
                    return AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS;
                }
                throw new SecurityException("Settings.Secure." + name + " is deprecated and no longer accessible. See API documentation for potential replacements.");
            } else {
                return sNameValueCache.getStringForUser(resolver, name, userHandle);
            }
        }

        public static boolean putString(ContentResolver resolver, String name, String value, boolean overrideableByRestore) {
            return putStringForUser(resolver, name, value, null, false, resolver.getUserId(), overrideableByRestore);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, resolver.getUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            return putStringForUser(resolver, name, value, null, false, userHandle, false);
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, String tag, boolean makeDefault, int userHandle, boolean overrideableByRestore) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global");
                return Global.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, false);
            }
            return sNameValueCache.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, overrideableByRestore);
        }

        @SystemApi
        public static boolean putString(ContentResolver resolver, String name, String value, String tag, boolean makeDefault) {
            return putStringForUser(resolver, name, value, tag, makeDefault, resolver.getUserId(), false);
        }

        @SystemApi
        public static void resetToDefaults(ContentResolver resolver, String tag) {
            resetToDefaultsAsUser(resolver, tag, 1, resolver.getUserId());
        }

        public static void resetToDefaultsAsUser(ContentResolver resolver, String tag, int mode, int userHandle) {
            try {
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                if (tag != null) {
                    arg.putString(Settings.CALL_METHOD_TAG_KEY, tag);
                }
                arg.putInt(Settings.CALL_METHOD_RESET_MODE_KEY, mode);
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_RESET_SECURE, null, arg);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't reset do defaults for " + CONTENT_URI, e);
            }
        }

        public static Uri getUriFor(String name) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global, returning global URI.");
                return Global.getUriFor(Global.CONTENT_URI, name);
            }
            return getUriFor(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, cr.getUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseIntSettingWithDefault(v, def);
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            return getIntForUser(cr, name, cr.getUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseIntSetting(v, name);
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userHandle) {
            return putStringForUser(cr, name, Integer.toString(value), userHandle);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, cr.getUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseLongSettingWithDefault(v, def);
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            return getLongForUser(cr, name, cr.getUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseLongSetting(v, name);
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userHandle) {
            return putStringForUser(cr, name, Long.toString(value), userHandle);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, cr.getUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseFloatSettingWithDefault(v, def);
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            return getFloatForUser(cr, name, cr.getUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            return Settings.parseFloatSetting(v, name);
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, cr.getUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userHandle) {
            return putStringForUser(cr, name, Float.toString(value), userHandle);
        }

        public static void getCloneToManagedProfileSettings(Set<String> outKeySet) {
            outKeySet.addAll(CLONE_TO_MANAGED_PROFILE);
        }

        @Deprecated
        public static boolean isLocationProviderEnabled(ContentResolver cr, String provider) {
            IBinder binder = ServiceManager.getService("location");
            ILocationManager lm = (ILocationManager) Objects.requireNonNull(ILocationManager.Stub.asInterface(binder));
            try {
                return lm.isProviderEnabledForUser(provider, cr.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        @Deprecated
        public static void setLocationProviderEnabled(ContentResolver cr, String provider, boolean enabled) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class Global extends NameValueTable {
        @Readable
        public static final String ACTIVITY_MANAGER_CONSTANTS = "activity_manager_constants";
        @Readable
        public static final String ACTIVITY_STARTS_LOGGING_ENABLED = "activity_starts_logging_enabled";
        @Readable
        public static final String ADAPTIVE_BATTERY_MANAGEMENT_ENABLED = "adaptive_battery_management_enabled";
        @Readable
        public static final String ADB_ALLOWED_CONNECTION_TIME = "adb_allowed_connection_time";
        @Readable
        public static final String ADB_ENABLED = "adb_enabled";
        @Readable
        public static final String ADB_WIFI_ENABLED = "adb_wifi_enabled";
        @Readable
        public static final String ADD_USERS_WHEN_LOCKED = "add_users_when_locked";
        @Readable
        public static final String ADVANCED_BATTERY_USAGE_AMOUNT = "advanced_battery_usage_amount";
        @Readable
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
        @Readable
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
        @SystemApi
        @Readable
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        @Readable
        public static final String ALLOW_USER_SWITCHING_WHEN_SYSTEM_USER_LOCKED = "allow_user_switching_when_system_user_locked";
        @Readable
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
        @Readable
        public static final String ALWAYS_ON_DISPLAY_CONSTANTS = "always_on_display_constants";
        @Readable
        public static final String ANGLE_DEBUG_PACKAGE = "angle_debug_package";
        public static final String ANGLE_DEFERLIST = "angle_deferlist";
        public static final String ANGLE_DEFERLIST_MODE = "angle_deferlist_mode";
        @Readable
        public static final String ANGLE_EGL_FEATURES = "angle_egl_features";
        @Readable
        public static final String ANGLE_GL_DRIVER_ALL_ANGLE = "angle_gl_driver_all_angle";
        @Readable
        public static final String ANGLE_GL_DRIVER_SELECTION_PKGS = "angle_gl_driver_selection_pkgs";
        @Readable
        public static final String ANGLE_GL_DRIVER_SELECTION_VALUES = "angle_gl_driver_selection_values";
        @Readable
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        @Readable
        public static final String ANOMALY_CONFIG = "anomaly_config";
        @Readable
        public static final String ANOMALY_CONFIG_VERSION = "anomaly_config_version";
        @Readable
        public static final String ANOMALY_DETECTION_CONSTANTS = "anomaly_detection_constants";
        @Readable
        public static final String APN_DB_UPDATE_CONTENT_URL = "apn_db_content_url";
        @Readable
        public static final String APN_DB_UPDATE_METADATA_URL = "apn_db_metadata_url";
        @Readable
        @Deprecated
        public static final String APPLY_RAMPING_RINGER = "apply_ramping_ringer";
        @Readable
        public static final String APPOP_HISTORY_BASE_INTERVAL_MILLIS = "baseIntervalMillis";
        @Readable
        public static final String APPOP_HISTORY_INTERVAL_MULTIPLIER = "intervalMultiplier";
        @Readable
        public static final String APPOP_HISTORY_MODE = "mode";
        @Readable
        public static final String APPOP_HISTORY_PARAMETERS = "appop_history_parameters";
        @Readable
        public static final String APP_AUTO_RESTRICTION_ENABLED = "app_auto_restriction_enabled";
        @Readable
        public static final String APP_BINDING_CONSTANTS = "app_binding_constants";
        @Readable
        public static final String APP_INTEGRITY_VERIFICATION_TIMEOUT = "app_integrity_verification_timeout";
        @Readable
        public static final String APP_OPS_CONSTANTS = "app_ops_constants";
        @SystemApi
        @Readable
        public static final String APP_STANDBY_ENABLED = "app_standby_enabled";
        @Readable
        public static final String APP_TIME_LIMIT_USAGE_SOURCE = "app_time_limit_usage_source";
        @Readable
        public static final String ARE_USER_DISABLED_HDR_FORMATS_ALLOWED = "are_user_disabled_hdr_formats_allowed";
        @Readable
        public static final String ART_VERIFIER_VERIFY_DEBUGGABLE = "art_verifier_verify_debuggable";
        @Readable
        public static final String ASSISTED_GPS_ENABLED = "assisted_gps_enabled";
        public static final String AUDIO_SAFE_CSD_CURRENT_VALUE = "audio_safe_csd_current_value";
        public static final String AUDIO_SAFE_CSD_DOSE_RECORDS = "audio_safe_csd_dose_records";
        public static final String AUDIO_SAFE_CSD_NEXT_WARNING = "audio_safe_csd_next_warning";
        @Readable
        public static final String AUDIO_SAFE_VOLUME_STATE = "audio_safe_volume_state";
        @SystemApi
        @Readable
        @Deprecated
        public static final String AUTOFILL_COMPAT_MODE_ALLOWED_PACKAGES = "autofill_compat_mode_allowed_packages";
        @Readable
        public static final String AUTOFILL_LOGGING_LEVEL = "autofill_logging_level";
        @Readable
        public static final String AUTOFILL_MAX_PARTITIONS_SIZE = "autofill_max_partitions_size";
        @Readable
        public static final String AUTOFILL_MAX_VISIBLE_DATASETS = "autofill_max_visible_datasets";
        @Readable
        public static final String AUTOMATIC_POWER_SAVE_MODE = "automatic_power_save_mode";
        @Readable
        public static final String AUTO_REVOKE_PARAMETERS = "auto_revoke_parameters";
        @Readable
        public static final String AUTO_TIME = "auto_time";
        @Readable
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        @Readable
        @Deprecated
        public static final String AVERAGE_TIME_TO_DISCHARGE = "average_time_to_discharge";
        @Readable
        public static final String AWARE_ALLOWED = "aware_allowed";
        @Readable
        public static final String BACKUP_AGENT_TIMEOUT_PARAMETERS = "backup_agent_timeout_parameters";
        @Readable
        public static final String BATTERY_CHARGING_STATE_UPDATE_DELAY = "battery_charging_state_update_delay";
        @Readable
        public static final String BATTERY_DISCHARGE_DURATION_THRESHOLD = "battery_discharge_duration_threshold";
        @Readable
        public static final String BATTERY_DISCHARGE_THRESHOLD = "battery_discharge_threshold";
        @Readable
        @Deprecated
        public static final String BATTERY_ESTIMATES_LAST_UPDATE_TIME = "battery_estimates_last_update_time";
        @Readable
        public static final String BATTERY_SAVER_CONSTANTS = "battery_saver_constants";
        @Readable
        public static final String BATTERY_SAVER_DEVICE_SPECIFIC_CONSTANTS = "battery_saver_device_specific_constants";
        @Readable
        public static final String BATTERY_STATS_CONSTANTS = "battery_stats_constants";
        @Readable
        public static final String BATTERY_TIP_CONSTANTS = "battery_tip_constants";
        @Readable
        public static final String BINDER_CALLS_STATS = "binder_calls_stats";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_ALWAYS_AVAILABLE = "ble_scan_always_enabled";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_BACKGROUND_MODE = "ble_scan_background_mode";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_BALANCED_INTERVAL_MS = "ble_scan_balanced_interval_ms";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_BALANCED_WINDOW_MS = "ble_scan_balanced_window_ms";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_LOW_LATENCY_INTERVAL_MS = "ble_scan_low_latency_interval_ms";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_LOW_LATENCY_WINDOW_MS = "ble_scan_low_latency_window_ms";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_LOW_POWER_INTERVAL_MS = "ble_scan_low_power_interval_ms";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLE_SCAN_LOW_POWER_WINDOW_MS = "ble_scan_low_power_window_ms";
        @Readable
        public static final String BLOCKED_SLICES = "blocked_slices";
        @Readable
        public static final String BLOCKING_HELPER_DISMISS_TO_VIEW_RATIO_LIMIT = "blocking_helper_dismiss_to_view_ratio";
        @Readable
        public static final String BLOCKING_HELPER_STREAK_LIMIT = "blocking_helper_streak_limit";
        @Readable
        public static final String BLUETOOTH_A2DP_OPTIONAL_CODECS_ENABLED_PREFIX = "bluetooth_a2dp_optional_codecs_enabled_";
        @Readable
        public static final String BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX = "bluetooth_a2dp_sink_priority_";
        @Readable
        public static final String BLUETOOTH_A2DP_SRC_PRIORITY_PREFIX = "bluetooth_a2dp_src_priority_";
        @Readable
        public static final String BLUETOOTH_A2DP_SUPPORTS_OPTIONAL_CODECS_PREFIX = "bluetooth_a2dp_supports_optional_codecs_";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLUETOOTH_BTSNOOP_DEFAULT_MODE = "bluetooth_btsnoop_default_mode";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLUETOOTH_CLASS_OF_DEVICE = "bluetooth_class_of_device";
        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        @Readable
        public static final String BLUETOOTH_DISABLED_PROFILES = "bluetooth_disabled_profiles";
        @Readable
        public static final String BLUETOOTH_HEADSET_PRIORITY_PREFIX = "bluetooth_headset_priority_";
        @Readable
        public static final String BLUETOOTH_HEARING_AID_PRIORITY_PREFIX = "bluetooth_hearing_aid_priority_";
        @Readable
        public static final String BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX = "bluetooth_input_device_priority_";
        @Readable
        public static final String BLUETOOTH_INTEROPERABILITY_LIST = "bluetooth_interoperability_list";
        @Readable
        public static final String BLUETOOTH_MAP_CLIENT_PRIORITY_PREFIX = "bluetooth_map_client_priority_";
        @Readable
        public static final String BLUETOOTH_MAP_PRIORITY_PREFIX = "bluetooth_map_priority_";
        @Readable
        public static final String BLUETOOTH_ON = "bluetooth_on";
        @Readable
        public static final String BLUETOOTH_PAN_PRIORITY_PREFIX = "bluetooth_pan_priority_";
        @Readable
        public static final String BLUETOOTH_PBAP_CLIENT_PRIORITY_PREFIX = "bluetooth_pbap_client_priority_";
        @Readable
        public static final String BLUETOOTH_SAP_PRIORITY_PREFIX = "bluetooth_sap_priority_";
        @Readable
        public static final String BOOT_COUNT = "boot_count";
        @Readable
        public static final String BROADCAST_BG_CONSTANTS = "bcast_bg_constants";
        @Readable
        public static final String BROADCAST_FG_CONSTANTS = "bcast_fg_constants";
        @Readable
        public static final String BROADCAST_OFFLOAD_CONSTANTS = "bcast_offload_constants";
        @Readable
        @Deprecated
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        public static final String BYPASS_DEVICE_POLICY_MANAGEMENT_ROLE_QUALIFICATIONS = "bypass_device_policy_management_role_qualifications";
        @Readable
        public static final String CACHED_APPS_FREEZER_ENABLED = "cached_apps_freezer";
        @Readable
        public static final String CALL_AUTO_RETRY = "call_auto_retry";
        @Readable
        @Deprecated
        public static final String CAPTIVE_PORTAL_DETECTION_ENABLED = "captive_portal_detection_enabled";
        @Readable
        public static final String CAPTIVE_PORTAL_FALLBACK_PROBE_SPECS = "captive_portal_fallback_probe_specs";
        @Readable
        public static final String CAPTIVE_PORTAL_FALLBACK_URL = "captive_portal_fallback_url";
        @Readable
        public static final String CAPTIVE_PORTAL_HTTPS_URL = "captive_portal_https_url";
        @Readable
        public static final String CAPTIVE_PORTAL_HTTP_URL = "captive_portal_http_url";
        @Readable
        public static final String CAPTIVE_PORTAL_MODE = "captive_portal_mode";
        public static final int CAPTIVE_PORTAL_MODE_AVOID = 2;
        public static final int CAPTIVE_PORTAL_MODE_IGNORE = 0;
        public static final int CAPTIVE_PORTAL_MODE_PROMPT = 1;
        @Readable
        public static final String CAPTIVE_PORTAL_OTHER_FALLBACK_URLS = "captive_portal_other_fallback_urls";
        @Readable
        public static final String CAPTIVE_PORTAL_SERVER = "captive_portal_server";
        @Readable
        public static final String CAPTIVE_PORTAL_USER_AGENT = "captive_portal_user_agent";
        @Readable
        public static final String CAPTIVE_PORTAL_USE_HTTPS = "captive_portal_use_https";
        @SystemApi
        @Readable
        public static final String CARRIER_APP_NAMES = "carrier_app_names";
        @SystemApi
        @Readable
        public static final String CARRIER_APP_WHITELIST = "carrier_app_whitelist";
        @Readable
        public static final String CAR_DOCK_SOUND = "car_dock_sound";
        @Readable
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        @Readable
        public static final String CDMA_CELL_BROADCAST_SMS = "cdma_cell_broadcast_sms";
        @Readable
        public static final String CDMA_ROAMING_MODE = "roaming_settings";
        @Readable
        public static final String CDMA_SUBSCRIPTION_MODE = "subscription_mode";
        @Readable
        public static final String CELL_ON = "cell_on";
        @Readable
        public static final String CERT_PIN_UPDATE_CONTENT_URL = "cert_pin_content_url";
        @Readable
        public static final String CERT_PIN_UPDATE_METADATA_URL = "cert_pin_metadata_url";
        @Readable
        public static final String CHAINED_BATTERY_ATTRIBUTION_ENABLED = "chained_battery_attribution_enabled";
        @Deprecated
        public static final String CHARGING_SOUNDS_ENABLED = "charging_sounds_enabled";
        @Readable
        public static final String CHARGING_STARTED_SOUND = "charging_started_sound";
        @Deprecated
        public static final String CHARGING_VIBRATION_ENABLED = "charging_vibration_enabled";
        public static final String CLOCKWORK_HOME_READY = "clockwork_home_ready";
        @Readable
        public static final String COMPATIBILITY_MODE = "compatibility_mode";
        @Readable
        public static final String CONNECTIVITY_CHANGE_DELAY = "connectivity_change_delay";
        @Readable
        public static final String CONNECTIVITY_METRICS_BUFFER_SIZE = "connectivity_metrics_buffer_size";
        @Readable
        public static final String CONNECTIVITY_SAMPLING_INTERVAL_IN_SECONDS = "connectivity_sampling_interval_in_seconds";
        @Readable
        public static final String CONTACTS_DATABASE_WAL_ENABLED = "contacts_database_wal_enabled";
        @Readable
        @Deprecated
        public static final String CONTACT_METADATA_SYNC = "contact_metadata_sync";
        @Readable
        public static final String CONTACT_METADATA_SYNC_ENABLED = "contact_metadata_sync_enabled";
        public static final Uri CONTENT_URI;
        @Readable
        public static final String CONVERSATION_ACTIONS_UPDATE_CONTENT_URL = "conversation_actions_content_url";
        @Readable
        public static final String CONVERSATION_ACTIONS_UPDATE_METADATA_URL = "conversation_actions_metadata_url";
        @Readable
        @Deprecated
        public static final String CUSTOM_BUGREPORT_HANDLER_APP = "custom_bugreport_handler_app";
        @Readable
        @Deprecated
        public static final String CUSTOM_BUGREPORT_HANDLER_USER = "custom_bugreport_handler_user";
        @Readable
        public static final String DATABASE_CREATION_BUILDID = "database_creation_buildid";
        @Readable
        public static final String DATABASE_DOWNGRADE_REASON = "database_downgrade_reason";
        @Readable
        public static final String DATA_ACTIVITY_TIMEOUT_MOBILE = "data_activity_timeout_mobile";
        @Readable
        public static final String DATA_ACTIVITY_TIMEOUT_WIFI = "data_activity_timeout_wifi";
        @Readable(maxTargetSdk = 32)
        public static final String DATA_ROAMING = "data_roaming";
        @Readable
        public static final String DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_aggressive_delay_in_ms";
        @Readable
        public static final String DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_non_aggressive_delay_in_ms";
        @Readable
        public static final String DATA_STALL_RECOVERY_ON_BAD_NETWORK = "data_stall_recovery_on_bad_network";
        @Readable
        public static final String DEBUG_APP = "debug_app";
        @Readable
        public static final String DEBUG_VIEW_ATTRIBUTES = "debug_view_attributes";
        @Readable
        public static final String DEBUG_VIEW_ATTRIBUTES_APPLICATION_PACKAGE = "debug_view_attributes_application_package";
        public static final long DEFAULT_ADB_ALLOWED_CONNECTION_TIME = 604800000;
        @Readable
        public static final String DEFAULT_DNS_SERVER = "default_dns_server";
        public static final int DEFAULT_ENABLE_RESTRICTED_BUCKET = 1;
        @Readable
        public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
        @Readable
        public static final String DEFAULT_RESTRICT_BACKGROUND_DATA = "default_restrict_background_data";
        @SystemApi
        @Readable
        public static final String DEFAULT_SM_DP_PLUS = "default_sm_dp_plus";
        @Readable
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";
        @Readable
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
        @Readable
        public static final String DEVELOPMENT_ENABLE_FREEFORM_WINDOWS_SUPPORT = "enable_freeform_support";
        @Readable
        public static final String DEVELOPMENT_ENABLE_NON_RESIZABLE_MULTI_WINDOW = "enable_non_resizable_multi_window";
        @Readable
        public static final String DEVELOPMENT_FORCE_DESKTOP_MODE_ON_EXTERNAL_DISPLAYS = "force_desktop_mode_on_external_displays";
        @Readable
        public static final String DEVELOPMENT_FORCE_RESIZABLE_ACTIVITIES = "force_resizable_activities";
        @Readable
        public static final String DEVELOPMENT_FORCE_RTL = "debug.force_rtl";
        @Readable
        public static final String DEVELOPMENT_RENDER_SHADOWS_IN_COMPOSITOR = "render_shadows_in_compositor";
        @Readable
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        @Readable
        public static final String DEVELOPMENT_USE_BLAST_ADAPTER_VR = "use_blast_adapter_vr";
        public static final String DEVELOPMENT_WM_DISPLAY_SETTINGS_PATH = "wm_display_settings_path";
        public static final String DEVICE_CONFIG_SYNC_DISABLED = "device_config_sync_disabled";
        @SystemApi
        @Readable
        public static final String DEVICE_DEMO_MODE = "device_demo_mode";
        @Readable
        public static final String DEVICE_NAME = "device_name";
        @Readable
        public static final String DEVICE_POLICY_CONSTANTS = "device_policy_constants";
        @Readable
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        @SystemApi
        @Readable
        public static final String DEVICE_PROVISIONING_MOBILE_DATA_ENABLED = "device_provisioning_mobile_data";
        @Readable
        public static final String DISABLE_WINDOW_BLURS = "disable_window_blurs";
        @Readable
        public static final String DISK_FREE_CHANGE_REPORTING_THRESHOLD = "disk_free_change_reporting_threshold";
        @Readable
        public static final String DISPLAY_PANEL_LPM = "display_panel_lpm";
        @Readable
        public static final String DISPLAY_SCALING_FORCE = "display_scaling_force";
        @Readable
        public static final String DISPLAY_SIZE_FORCED = "display_size_forced";
        @Readable
        public static final String DNS_RESOLVER_MAX_SAMPLES = "dns_resolver_max_samples";
        @Readable
        public static final String DNS_RESOLVER_MIN_SAMPLES = "dns_resolver_min_samples";
        @Readable
        public static final String DNS_RESOLVER_SAMPLE_VALIDITY_SECONDS = "dns_resolver_sample_validity_seconds";
        @Readable
        public static final String DNS_RESOLVER_SUCCESS_THRESHOLD_PERCENT = "dns_resolver_success_threshold_percent";
        @Readable
        public static final String DOCK_AUDIO_MEDIA_ENABLED = "dock_audio_media_enabled";
        @Readable
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        @Readable
        public static final String DOCK_SOUNDS_ENABLED_WHEN_ACCESSIBILITY = "dock_sounds_enabled_when_accessbility";
        @Readable
        public static final String DOWNLOAD_MAX_BYTES_OVER_MOBILE = "download_manager_max_bytes_over_mobile";
        @Readable
        public static final String DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = "download_manager_recommended_max_bytes_over_mobile";
        @Readable
        public static final String DROPBOX_AGE_SECONDS = "dropbox_age_seconds";
        @Readable
        public static final String DROPBOX_MAX_FILES = "dropbox_max_files";
        @Readable
        public static final String DROPBOX_QUOTA_KB = "dropbox_quota_kb";
        @Readable
        public static final String DROPBOX_QUOTA_PERCENT = "dropbox_quota_percent";
        @Readable
        public static final String DROPBOX_RESERVE_PERCENT = "dropbox_reserve_percent";
        @Readable
        public static final String DROPBOX_TAG_PREFIX = "dropbox:";
        @Readable
        public static final String DYNAMIC_POWER_SAVINGS_DISABLE_THRESHOLD = "dynamic_power_savings_disable_threshold";
        @Readable
        public static final String DYNAMIC_POWER_SAVINGS_ENABLED = "dynamic_power_savings_enabled";
        @Readable
        public static final String EMERGENCY_AFFORDANCE_NEEDED = "emergency_affordance_needed";
        public static final String EMERGENCY_GESTURE_POWER_BUTTON_COOLDOWN_PERIOD_MS = "emergency_gesture_power_button_cooldown_period_ms";
        public static final String EMERGENCY_GESTURE_TAP_DETECTION_MIN_TIME_MS = "emergency_gesture_tap_detection_min_time_ms";
        @Readable
        public static final String EMERGENCY_TONE = "emergency_tone";
        @Readable
        public static final String EMULATE_DISPLAY_CUTOUT = "emulate_display_cutout";
        public static final int EMULATE_DISPLAY_CUTOUT_OFF = 0;
        public static final int EMULATE_DISPLAY_CUTOUT_ON = 1;
        @Readable
        public static final String ENABLED_SUBSCRIPTION_FOR_SLOT = "enabled_subscription_for_slot";
        @Readable
        public static final String ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED = "enable_accessibility_global_gesture_enabled";
        @Readable
        public static final String ENABLE_ADB_INCREMENTAL_INSTALL_DEFAULT = "enable_adb_incremental_install_default";
        @Readable
        public static final String ENABLE_AUTOMATIC_SYSTEM_SERVER_HEAP_DUMPS = "enable_automatic_system_server_heap_dumps";
        public static final String ENABLE_BACK_ANIMATION = "enable_back_animation";
        @Readable
        public static final String ENABLE_CACHE_QUOTA_CALCULATION = "enable_cache_quota_calculation";
        @Readable
        public static final String ENABLE_CELLULAR_ON_BOOT = "enable_cellular_on_boot";
        @Readable
        public static final String ENABLE_DELETION_HELPER_NO_THRESHOLD_TOGGLE = "enable_deletion_helper_no_threshold_toggle";
        @Readable
        public static final String ENABLE_DISKSTATS_LOGGING = "enable_diskstats_logging";
        @Readable
        public static final String ENABLE_EPHEMERAL_FEATURE = "enable_ephemeral_feature";
        @Readable
        public static final String ENABLE_GNSS_RAW_MEAS_FULL_TRACKING = "enable_gnss_raw_meas_full_tracking";
        @Readable
        public static final String ENABLE_GPU_DEBUG_LAYERS = "enable_gpu_debug_layers";
        public static final String ENABLE_MULTI_SLOT_TIMEOUT_MILLIS = "enable_multi_slot_timeout_millis";
        @Readable
        public static final String ENABLE_RADIO_BUG_DETECTION = "enable_radio_bug_detection";
        @Readable
        public static final String ENABLE_RESTRICTED_BUCKET = "enable_restricted_bucket";
        public static final String ENABLE_TARE = "enable_tare";
        @Readable
        public static final String ENCODED_SURROUND_OUTPUT = "encoded_surround_output";
        public static final int ENCODED_SURROUND_OUTPUT_ALWAYS = 2;
        public static final int ENCODED_SURROUND_OUTPUT_AUTO = 0;
        @Readable
        public static final String ENCODED_SURROUND_OUTPUT_ENABLED_FORMATS = "encoded_surround_output_enabled_formats";
        public static final int ENCODED_SURROUND_OUTPUT_MANUAL = 3;
        public static final int ENCODED_SURROUND_OUTPUT_NEVER = 1;
        public static final int ENCODED_SURROUND_SC_MAX = 3;
        @Readable
        @Deprecated
        public static final String ENHANCED_4G_MODE_ENABLED = "volte_vt_enabled";
        @Readable
        public static final String EPHEMERAL_COOKIE_MAX_SIZE_BYTES = "ephemeral_cookie_max_size_bytes";
        @Readable
        public static final String ERROR_LOGCAT_PREFIX = "logcat_for_";
        @Readable
        public static final String EUICC_FACTORY_RESET_TIMEOUT_MILLIS = "euicc_factory_reset_timeout_millis";
        @SystemApi
        @Readable
        public static final String EUICC_PROVISIONED = "euicc_provisioned";
        @Readable
        public static final String EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS = "euicc_removing_invisible_profiles_timeout_millis";
        @SystemApi
        @Readable
        public static final String EUICC_SUPPORTED_COUNTRIES = "euicc_supported_countries";
        public static final String EUICC_SWITCH_SLOT_TIMEOUT_MILLIS = "euicc_switch_slot_timeout_millis";
        @SystemApi
        @Readable
        public static final String EUICC_UNSUPPORTED_COUNTRIES = "euicc_unsupported_countries";
        public static final String EXTRA_LOW_POWER_MODE = "extra_low_power";
        @Readable
        public static final String FANCY_IME_ANIMATIONS = "fancy_ime_animations";
        @Readable
        public static final String FORCED_APP_STANDBY_FOR_SMALL_BATTERY_ENABLED = "forced_app_standby_for_small_battery_enabled";
        @Readable
        public static final String FORCE_ALLOW_ON_EXTERNAL = "force_allow_on_external";
        public static final String FORCE_NON_DEBUGGABLE_FINAL_BUILD_FOR_COMPAT = "force_non_debuggable_final_build_for_compat";
        @Readable
        public static final String FOREGROUND_SERVICE_STARTS_LOGGING_ENABLED = "foreground_service_starts_logging_enabled";
        @Readable
        public static final String FPS_DEVISOR = "fps_divisor";
        @Readable
        public static final String FSTRIM_MANDATORY_INTERVAL = "fstrim_mandatory_interval";
        @Readable
        public static final String GLOBAL_HTTP_PROXY_EXCLUSION_LIST = "global_http_proxy_exclusion_list";
        @Readable
        public static final String GLOBAL_HTTP_PROXY_HOST = "global_http_proxy_host";
        @Readable
        public static final String GLOBAL_HTTP_PROXY_PAC = "global_proxy_pac_url";
        @Readable
        public static final String GLOBAL_HTTP_PROXY_PORT = "global_http_proxy_port";
        @Readable
        public static final String GNSS_HAL_LOCATION_REQUEST_DURATION_MILLIS = "gnss_hal_location_request_duration_millis";
        public static final String GNSS_SATELLITE_BLOCKLIST = "gnss_satellite_blocklist";
        @Readable
        public static final String GPRS_REGISTER_CHECK_PERIOD_MS = "gprs_register_check_period_ms";
        @Readable
        public static final String GPU_DEBUG_APP = "gpu_debug_app";
        @Readable
        public static final String GPU_DEBUG_LAYERS = "gpu_debug_layers";
        @Readable
        public static final String GPU_DEBUG_LAYERS_GLES = "gpu_debug_layers_gles";
        @Readable
        public static final String GPU_DEBUG_LAYER_APP = "gpu_debug_layer_app";
        @Readable
        public static final String HDR_CONVERSION_MODE = "hdr_conversion_mode";
        @Readable
        public static final String HDR_FORCE_CONVERSION_TYPE = "hdr_force_conversion_type";
        @Readable
        public static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
        public static final int HEADS_UP_OFF = 0;
        public static final int HEADS_UP_ON = 1;
        @Readable
        public static final String HIDDEN_API_BLACKLIST_EXEMPTIONS = "hidden_api_blacklist_exemptions";
        @Readable
        public static final String HIDDEN_API_POLICY = "hidden_api_policy";
        @Readable
        public static final String HIDE_ERROR_DIALOGS = "hide_error_dialogs";
        @Readable
        public static final String HTTP_PROXY = "http_proxy";
        @Readable
        public static final String INET_CONDITION_DEBOUNCE_DOWN_DELAY = "inet_condition_debounce_down_delay";
        @Readable
        public static final String INET_CONDITION_DEBOUNCE_UP_DELAY = "inet_condition_debounce_up_delay";
        @Readable
        public static final String INSTALLED_INSTANT_APP_MAX_CACHE_PERIOD = "installed_instant_app_max_cache_period";
        @Readable
        public static final String INSTALLED_INSTANT_APP_MIN_CACHE_PERIOD = "installed_instant_app_min_cache_period";
        @SystemApi
        @Readable
        public static final String INSTALL_CARRIER_APP_NOTIFICATION_PERSISTENT = "install_carrier_app_notification_persistent";
        @SystemApi
        @Readable
        public static final String INSTALL_CARRIER_APP_NOTIFICATION_SLEEP_MILLIS = "install_carrier_app_notification_sleep_millis";
        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        @Readable
        public static final String INSTANT_APP_DEXOPT_ENABLED = "instant_app_dexopt_enabled";
        public static final Set<String> INSTANT_APP_SETTINGS;
        @Readable
        public static final String INTEGRITY_CHECK_INCLUDES_RULE_PROVIDER = "verify_integrity_for_rule_provider";
        @Readable
        public static final String INTENT_FIREWALL_UPDATE_CONTENT_URL = "intent_firewall_content_url";
        @Readable
        public static final String INTENT_FIREWALL_UPDATE_METADATA_URL = "intent_firewall_metadata_url";
        @Readable
        public static final String KEEP_PROFILE_IN_BACKGROUND = "keep_profile_in_background";
        @Readable
        public static final String KERNEL_CPU_THREAD_READER = "kernel_cpu_thread_reader";
        @Readable
        public static final String KEY_CHORD_POWER_VOLUME_UP = "key_chord_power_volume_up";
        @Readable
        public static final String LANG_ID_UPDATE_CONTENT_URL = "lang_id_content_url";
        @Readable
        public static final String LANG_ID_UPDATE_METADATA_URL = "lang_id_metadata_url";
        public static final String[] LEGACY_RESTORE_SETTINGS;
        @Readable
        public static final String LID_BEHAVIOR = "lid_behavior";
        @Readable
        public static final String LOCATION_BACKGROUND_THROTTLE_INTERVAL_MS = "location_background_throttle_interval_ms";
        @Readable
        public static final String LOCATION_BACKGROUND_THROTTLE_PACKAGE_WHITELIST = "location_background_throttle_package_whitelist";
        @Readable
        public static final String LOCATION_BACKGROUND_THROTTLE_PROXIMITY_ALERT_INTERVAL_MS = "location_background_throttle_proximity_alert_interval_ms";
        public static final String LOCATION_ENABLE_STATIONARY_THROTTLE = "location_enable_stationary_throttle";
        @Readable
        @Deprecated
        public static final String LOCATION_IGNORE_SETTINGS_PACKAGE_WHITELIST = "location_ignore_settings_package_whitelist";
        @Readable
        public static final String LOCATION_SETTINGS_LINK_TO_PERMISSIONS_ENABLED = "location_settings_link_to_permissions_enabled";
        @Readable
        public static final String LOCK_SOUND = "lock_sound";
        @Readable
        public static final String LOOPER_STATS = "looper_stats";
        @Readable
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        @Readable
        public static final String LOW_BATTERY_SOUND_TIMEOUT = "low_battery_sound_timeout";
        @Readable
        public static final String LOW_POWER_MODE = "low_power";
        public static final String LOW_POWER_MODE_REMINDER_ENABLED = "low_power_mode_reminder_enabled";
        @Readable
        public static final String LOW_POWER_MODE_STICKY = "low_power_sticky";
        @Readable
        public static final String LOW_POWER_MODE_STICKY_AUTO_DISABLE_ENABLED = "low_power_sticky_auto_disable_enabled";
        @Readable
        public static final String LOW_POWER_MODE_STICKY_AUTO_DISABLE_LEVEL = "low_power_sticky_auto_disable_level";
        @Readable
        public static final String LOW_POWER_MODE_SUGGESTION_PARAMS = "low_power_mode_suggestion_params";
        @Readable
        public static final String LOW_POWER_MODE_TRIGGER_LEVEL = "low_power_trigger_level";
        @Readable
        public static final String LOW_POWER_MODE_TRIGGER_LEVEL_MAX = "low_power_trigger_level_max";
        public static final String LOW_POWER_STANDBY_ACTIVE_DURING_MAINTENANCE = "low_power_standby_active_during_maintenance";
        public static final String LOW_POWER_STANDBY_ENABLED = "low_power_standby_enabled";
        @Readable
        public static final String LTE_SERVICE_FORCED = "lte_service_forced";
        public static final String MANAGED_PROVISIONING_DEFER_PROVISIONING_TO_ROLE_HOLDER = "managed_provisioning_defer_provisioning_to_role_holder";
        @Readable
        public static final String MAXIMUM_OBSCURING_OPACITY_FOR_TOUCH = "maximum_obscuring_opacity_for_touch";
        @Readable
        public static final String MAX_ERROR_BYTES_PREFIX = "max_error_bytes_for_";
        @Readable
        public static final String MAX_NOTIFICATION_ENQUEUE_RATE = "max_notification_enqueue_rate";
        @Readable
        public static final String MAX_SOUND_TRIGGER_DETECTION_SERVICE_OPS_PER_DAY = "max_sound_trigger_detection_service_ops_per_day";
        @Readable
        public static final String MDC_INITIAL_MAX_RETRY = "mdc_initial_max_retry";
        @Readable
        public static final String MHL_INPUT_SWITCHING_ENABLED = "mhl_input_switching_enabled";
        @Readable
        public static final String MHL_POWER_CHARGE_ENABLED = "mhl_power_charge_enabled";
        @Readable
        public static final String MIN_DURATION_BETWEEN_RECOVERY_STEPS_IN_MS = "min_duration_between_recovery_steps";
        @Readable
        public static final String MOBILE_DATA = "mobile_data";
        @Readable
        public static final String MOBILE_DATA_ALWAYS_ON = "mobile_data_always_on";
        @Readable
        public static final String MODEM_STACK_ENABLED_FOR_SLOT = "modem_stack_enabled_for_slot";
        @Readable
        public static final String MODE_RINGER = "mode_ringer";
        private static final HashSet<String> MOVED_TO_SECURE;
        private static final HashSet<String> MOVED_TO_SYSTEM;
        @Readable
        public static final String MULTI_SIM_DATA_CALL_SUBSCRIPTION = "multi_sim_data_call";
        @Readable
        public static final String MULTI_SIM_SMS_PROMPT = "multi_sim_sms_prompt";
        @Readable
        public static final String MULTI_SIM_SMS_SUBSCRIPTION = "multi_sim_sms";
        @Readable
        public static final String[] MULTI_SIM_USER_PREFERRED_SUBS;
        @Readable
        public static final String MULTI_SIM_VOICE_CALL_SUBSCRIPTION = "multi_sim_voice_call";
        @Readable
        public static final String MULTI_SIM_VOICE_PROMPT = "multi_sim_voice_prompt";
        @Readable
        public static final String NATIVE_FLAGS_HEALTH_CHECK_ENABLED = "native_flags_health_check_enabled";
        @Readable
        public static final String NETPOLICY_OVERRIDE_ENABLED = "netpolicy_override_enabled";
        @Readable
        public static final String NETPOLICY_QUOTA_ENABLED = "netpolicy_quota_enabled";
        @Readable
        public static final String NETPOLICY_QUOTA_FRAC_JOBS = "netpolicy_quota_frac_jobs";
        @Readable
        public static final String NETPOLICY_QUOTA_FRAC_MULTIPATH = "netpolicy_quota_frac_multipath";
        @Readable
        public static final String NETPOLICY_QUOTA_LIMITED = "netpolicy_quota_limited";
        @Readable
        public static final String NETPOLICY_QUOTA_UNLIMITED = "netpolicy_quota_unlimited";
        @Readable
        public static final String NETSTATS_AUGMENT_ENABLED = "netstats_augment_enabled";
        @Readable
        public static final String NETSTATS_COMBINE_SUBTYPE_ENABLED = "netstats_combine_subtype_enabled";
        @Readable
        public static final String NETSTATS_DEV_BUCKET_DURATION = "netstats_dev_bucket_duration";
        @Readable
        public static final String NETSTATS_DEV_DELETE_AGE = "netstats_dev_delete_age";
        @Readable
        public static final String NETSTATS_DEV_PERSIST_BYTES = "netstats_dev_persist_bytes";
        @Readable
        public static final String NETSTATS_DEV_ROTATE_AGE = "netstats_dev_rotate_age";
        @Readable
        public static final String NETSTATS_ENABLED = "netstats_enabled";
        @Readable
        public static final String NETSTATS_GLOBAL_ALERT_BYTES = "netstats_global_alert_bytes";
        @Readable
        public static final String NETSTATS_POLL_INTERVAL = "netstats_poll_interval";
        @Readable
        public static final String NETSTATS_SAMPLE_ENABLED = "netstats_sample_enabled";
        @Readable
        @Deprecated
        public static final String NETSTATS_TIME_CACHE_MAX_AGE = "netstats_time_cache_max_age";
        @Readable
        public static final String NETSTATS_UID_BUCKET_DURATION = "netstats_uid_bucket_duration";
        @Readable
        public static final String NETSTATS_UID_DELETE_AGE = "netstats_uid_delete_age";
        @Readable
        public static final String NETSTATS_UID_PERSIST_BYTES = "netstats_uid_persist_bytes";
        @Readable
        public static final String NETSTATS_UID_ROTATE_AGE = "netstats_uid_rotate_age";
        @Readable
        public static final String NETSTATS_UID_TAG_BUCKET_DURATION = "netstats_uid_tag_bucket_duration";
        @Readable
        public static final String NETSTATS_UID_TAG_DELETE_AGE = "netstats_uid_tag_delete_age";
        @Readable
        public static final String NETSTATS_UID_TAG_PERSIST_BYTES = "netstats_uid_tag_persist_bytes";
        @Readable
        public static final String NETSTATS_UID_TAG_ROTATE_AGE = "netstats_uid_tag_rotate_age";
        @Readable
        public static final String NETWORK_AVOID_BAD_WIFI = "network_avoid_bad_wifi";
        @Readable
        public static final String NETWORK_DEFAULT_DAILY_MULTIPATH_QUOTA_BYTES = "network_default_daily_multipath_quota_bytes";
        @Readable
        public static final String NETWORK_METERED_MULTIPATH_PREFERENCE = "network_metered_multipath_preference";
        @Readable
        public static final String NETWORK_PREFERENCE = "network_preference";
        @Readable
        @Deprecated
        public static final String NETWORK_RECOMMENDATIONS_ENABLED = "network_recommendations_enabled";
        @Readable
        @Deprecated
        public static final String NETWORK_RECOMMENDATIONS_PACKAGE = "network_recommendations_package";
        @Readable
        public static final String NETWORK_SCORER_APP = "network_scorer_app";
        @Readable
        public static final String NETWORK_SCORING_PROVISIONED = "network_scoring_provisioned";
        @Readable
        @Deprecated
        public static final String NETWORK_SCORING_UI_ENABLED = "network_scoring_ui_enabled";
        @Readable
        public static final String NETWORK_SWITCH_NOTIFICATION_DAILY_LIMIT = "network_switch_notification_daily_limit";
        @Readable
        public static final String NETWORK_SWITCH_NOTIFICATION_RATE_LIMIT_MILLIS = "network_switch_notification_rate_limit_millis";
        @Readable
        public static final String NETWORK_WATCHLIST_ENABLED = "network_watchlist_enabled";
        @Readable
        public static final String NETWORK_WATCHLIST_LAST_REPORT_TIME = "network_watchlist_last_report_time";
        @Readable
        public static final String NEW_CONTACT_AGGREGATOR = "new_contact_aggregator";
        @Readable
        public static final String NIGHT_DISPLAY_FORCED_AUTO_MODE_AVAILABLE = "night_display_forced_auto_mode_available";
        public static final String NITZ_NETWORK_DISCONNECT_RETENTION = "nitz_network_disconnect_retention";
        @Readable
        public static final String NITZ_UPDATE_DIFF = "nitz_update_diff";
        @Readable
        public static final String NITZ_UPDATE_SPACING = "nitz_update_spacing";
        @Readable
        @Deprecated
        public static final String NOTIFICATION_BUBBLES = "notification_bubbles";
        public static final String NOTIFICATION_FEEDBACK_ENABLED = "notification_feedback_enabled";
        @Readable
        public static final String NOTIFICATION_SNOOZE_OPTIONS = "notification_snooze_options";
        @Readable
        public static final String NR_NSA_TRACKING_SCREEN_OFF_MODE = "nr_nsa_tracking_screen_off_mode";
        @Readable
        public static final String NTP_SERVER = "ntp_server";
        @Readable
        public static final String NTP_TIMEOUT = "ntp_timeout";
        public static final String ONE_HANDED_KEYGUARD_SIDE = "one_handed_keyguard_side";
        public static final int ONE_HANDED_KEYGUARD_SIDE_LEFT = 0;
        public static final int ONE_HANDED_KEYGUARD_SIDE_RIGHT = 1;
        @SystemApi
        @Readable
        public static final String OTA_DISABLE_AUTOMATIC_UPDATE = "ota_disable_automatic_update";
        @Readable
        public static final String OVERLAY_DISPLAY_DEVICES = "overlay_display_devices";
        @Readable
        public static final String OVERRIDE_SETTINGS_PROVIDER_RESTORE_ANY_VERSION = "override_settings_provider_restore_any_version";
        @Readable
        public static final String PACKAGE_STREAMING_VERIFIER_TIMEOUT = "streaming_verifier_timeout";
        @Readable
        public static final String PACKAGE_VERIFIER_DEFAULT_RESPONSE = "verifier_default_response";
        @Readable
        public static final String PACKAGE_VERIFIER_INCLUDE_ADB = "verifier_verify_adb_installs";
        @Readable
        public static final String PACKAGE_VERIFIER_SETTING_VISIBLE = "verifier_setting_visible";
        @Readable
        public static final String PACKAGE_VERIFIER_TIMEOUT = "verifier_timeout";
        @Readable
        public static final String PAC_CHANGE_DELAY = "pac_change_delay";
        @Readable
        public static final String PDP_WATCHDOG_ERROR_POLL_COUNT = "pdp_watchdog_error_poll_count";
        @Readable
        public static final String PDP_WATCHDOG_ERROR_POLL_INTERVAL_MS = "pdp_watchdog_error_poll_interval_ms";
        @Readable
        public static final String PDP_WATCHDOG_LONG_POLL_INTERVAL_MS = "pdp_watchdog_long_poll_interval_ms";
        @Readable
        public static final String PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT = "pdp_watchdog_max_pdp_reset_fail_count";
        @Readable
        public static final String PDP_WATCHDOG_POLL_INTERVAL_MS = "pdp_watchdog_poll_interval_ms";
        @Readable
        public static final String PDP_WATCHDOG_TRIGGER_PACKET_COUNT = "pdp_watchdog_trigger_packet_count";
        public static final String PEOPLE_SPACE_CONVERSATION_TYPE = "people_space_conversation_type";
        @Readable
        public static final String POLICY_CONTROL = "policy_control";
        @Readable
        public static final String POWER_BUTTON_LONG_PRESS = "power_button_long_press";
        @Readable
        public static final String POWER_BUTTON_LONG_PRESS_DURATION_MS = "power_button_long_press_duration_ms";
        @Readable
        public static final String POWER_BUTTON_SUPPRESSION_DELAY_AFTER_GESTURE_WAKE = "power_button_suppression_delay_after_gesture_wake";
        @Readable
        public static final String POWER_BUTTON_VERY_LONG_PRESS = "power_button_very_long_press";
        @Readable
        public static final String POWER_MANAGER_CONSTANTS = "power_manager_constants";
        @Readable
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
        @Readable
        public static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";
        @Readable
        public static final String PRIVATE_DNS_DEFAULT_MODE = "private_dns_default_mode";
        @Readable
        public static final String PRIVATE_DNS_MODE = "private_dns_mode";
        @Readable
        public static final String PRIVATE_DNS_SPECIFIER = "private_dns_specifier";
        @Readable
        public static final String PROVISIONING_APN_ALARM_DELAY_IN_MS = "provisioning_apn_alarm_delay_in_ms";
        @Readable
        public static final String RADIO_BLUETOOTH = "bluetooth";
        @Readable
        public static final String RADIO_BUG_SYSTEM_ERROR_COUNT_THRESHOLD = "radio_bug_system_error_count_threshold";
        @Readable
        public static final String RADIO_BUG_WAKELOCK_TIMEOUT_COUNT_THRESHOLD = "radio_bug_wakelock_timeout_count_threshold";
        @Readable
        public static final String RADIO_CELL = "cell";
        @Readable
        public static final String RADIO_NFC = "nfc";
        @Readable
        public static final String RADIO_WIFI = "wifi";
        @Readable
        public static final String RADIO_WIMAX = "wimax";
        @Readable
        public static final String READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT = "read_external_storage_enforced_default";
        public static final String RECEIVE_EXPLICIT_USER_INTERACTION_AUDIO_ENABLED = "receive_explicit_user_interaction_audio_enabled";
        @Readable
        @Deprecated
        public static final String RECOMMENDED_NETWORK_EVALUATOR_CACHE_EXPIRY_MS = "recommended_network_evaluator_cache_expiry_ms";
        public static final String REMOVE_GUEST_ON_EXIT = "remove_guest_on_exit";
        @SystemApi
        @Readable
        public static final String REQUIRE_PASSWORD_TO_DECRYPT = "require_password_to_decrypt";
        public static final String RESTRICTED_NETWORKING_MODE = "restricted_networking_mode";
        public static final String REVIEW_PERMISSIONS_NOTIFICATION_STATE = "review_permissions_notification_state";
        @Readable
        public static final String SAFE_BOOT_DISALLOWED = "safe_boot_disallowed";
        @Readable
        public static final String SECURE_FRP_MODE = "secure_frp_mode";
        @Readable
        public static final String SELINUX_STATUS = "selinux_status";
        @Readable
        public static final String SELINUX_UPDATE_CONTENT_URL = "selinux_content_url";
        @Readable
        public static final String SELINUX_UPDATE_METADATA_URL = "selinux_metadata_url";
        @Readable
        public static final String SEND_ACTION_APP_ERROR = "send_action_app_error";
        @Readable
        public static final String SETTINGS_USE_EXTERNAL_PROVIDER_API = "settings_use_external_provider_api";
        @Readable
        public static final String SETTINGS_USE_PSD_API = "settings_use_psd_api";
        @Readable
        public static final String SETUP_PREPAID_DATA_SERVICE_URL = "setup_prepaid_data_service_url";
        @Readable
        public static final String SETUP_PREPAID_DETECTION_REDIR_HOST = "setup_prepaid_detection_redir_host";
        @Readable
        public static final String SETUP_PREPAID_DETECTION_TARGET_URL = "setup_prepaid_detection_target_url";
        @Readable
        public static final String SET_GLOBAL_HTTP_PROXY = "set_global_http_proxy";
        @Readable
        public static final String SET_INSTALL_LOCATION = "set_install_location";
        @Readable
        public static final String SHORTCUT_MANAGER_CONSTANTS = "shortcut_manager_constants";
        @Readable
        public static final String SHOW_ANGLE_IN_USE_DIALOG_BOX = "show_angle_in_use_dialog_box";
        @Readable
        public static final String SHOW_FIRST_CRASH_DIALOG = "show_first_crash_dialog";
        @Readable
        public static final String SHOW_HIDDEN_LAUNCHER_ICON_APPS_ENABLED = "show_hidden_icon_apps_enabled";
        @Readable
        public static final String SHOW_MEDIA_ON_QUICK_SETTINGS = "qs_media_controls";
        @Readable
        public static final String SHOW_MUTE_IN_CRASH_DIALOG = "show_mute_in_crash_dialog";
        @Readable
        public static final String SHOW_NEW_APP_INSTALLED_NOTIFICATION_ENABLED = "show_new_app_installed_notification_enabled";
        public static final String SHOW_NEW_NOTIF_DISMISS = "show_new_notif_dismiss";
        @Readable
        public static final String SHOW_NOTIFICATION_CHANNEL_WARNINGS = "show_notification_channel_warnings";
        public static final String SHOW_PEOPLE_SPACE = "show_people_space";
        @Readable
        @Deprecated
        public static final String SHOW_PROCESSES = "show_processes";
        @Readable
        public static final String SHOW_RESTART_IN_CRASH_DIALOG = "show_restart_in_crash_dialog";
        @Readable
        public static final String SHOW_TEMPERATURE_WARNING = "show_temperature_warning";
        @Readable
        public static final String SHOW_USB_TEMPERATURE_ALARM = "show_usb_temperature_alarm";
        @Deprecated
        public static final String SHOW_ZEN_SETTINGS_SUGGESTION = "show_zen_settings_suggestion";
        @Deprecated
        public static final String SHOW_ZEN_UPGRADE_NOTIFICATION = "show_zen_upgrade_notification";
        @Readable
        public static final String SIGNED_CONFIG_VERSION = "signed_config_version";
        @Readable
        public static final String SMART_REPLIES_IN_NOTIFICATIONS_FLAGS = "smart_replies_in_notifications_flags";
        @Readable
        public static final String SMART_SELECTION_UPDATE_CONTENT_URL = "smart_selection_content_url";
        @Readable
        public static final String SMART_SELECTION_UPDATE_METADATA_URL = "smart_selection_metadata_url";
        @Readable
        public static final String SMART_SUGGESTIONS_IN_NOTIFICATIONS_FLAGS = "smart_suggestions_in_notifications_flags";
        @Readable
        public static final String SMS_OUTGOING_CHECK_INTERVAL_MS = "sms_outgoing_check_interval_ms";
        @Readable
        public static final String SMS_OUTGOING_CHECK_MAX_COUNT = "sms_outgoing_check_max_count";
        @Readable
        public static final String SMS_SHORT_CODES_UPDATE_CONTENT_URL = "sms_short_codes_content_url";
        @Readable
        public static final String SMS_SHORT_CODES_UPDATE_METADATA_URL = "sms_short_codes_metadata_url";
        @Readable
        public static final String SMS_SHORT_CODE_CONFIRMATION = "sms_short_code_confirmation";
        @Readable
        public static final String SMS_SHORT_CODE_RULE = "sms_short_code_rule";
        @Readable
        @Deprecated
        public static final String SOFT_AP_TIMEOUT_ENABLED = "soft_ap_timeout_enabled";
        @Readable
        public static final String SOUND_TRIGGER_DETECTION_SERVICE_OP_TIMEOUT = "sound_trigger_detection_service_op_timeout";
        @Readable
        @Deprecated
        public static final String SPEED_LABEL_CACHE_EVICTION_AGE_MILLIS = "speed_label_cache_eviction_age_millis";
        @Readable
        public static final String SQLITE_COMPATIBILITY_WAL_FLAGS = "sqlite_compatibility_wal_flags";
        @Readable
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        @Readable
        public static final String STORAGE_BENCHMARK_INTERVAL = "storage_benchmark_interval";
        @Readable
        public static final String STORAGE_SETTINGS_CLOBBER_THRESHOLD = "storage_settings_clobber_threshold";
        @Readable
        public static final String STYLUS_EVER_USED = "stylus_ever_used";
        @Readable
        public static final String STYLUS_HANDWRITING_ENABLED = "stylus_handwriting_enabled";
        @Readable
        public static final String SYNC_MANAGER_CONSTANTS = "sync_manager_constants";
        @Readable
        public static final String SYNC_MAX_RETRY_DELAY_IN_SECONDS = "sync_max_retry_delay_in_seconds";
        @Readable
        public static final String SYS_FREE_STORAGE_LOG_INTERVAL = "sys_free_storage_log_interval";
        @Readable
        public static final String SYS_STORAGE_CACHE_PERCENTAGE = "sys_storage_cache_percentage";
        @Readable
        public static final String SYS_STORAGE_FULL_THRESHOLD_BYTES = "sys_storage_full_threshold_bytes";
        @Readable
        public static final String SYS_STORAGE_THRESHOLD_MAX_BYTES = "sys_storage_threshold_max_bytes";
        @Readable
        public static final String SYS_STORAGE_THRESHOLD_PERCENTAGE = "sys_storage_threshold_percentage";
        @Readable
        public static final String SYS_TRACED = "sys_traced";
        @Readable
        public static final String SYS_UIDCPUPOWER = "sys_uidcpupower";
        public static final String TARE_ALARM_MANAGER_CONSTANTS = "tare_alarm_manager_constants";
        public static final String TARE_JOB_SCHEDULER_CONSTANTS = "tare_job_scheduler_constants";
        @Readable
        public static final String TCP_DEFAULT_INIT_RWND = "tcp_default_init_rwnd";
        @Readable
        public static final String TETHER_DUN_APN = "tether_dun_apn";
        @Readable
        public static final String TETHER_DUN_REQUIRED = "tether_dun_required";
        @Readable
        public static final String TETHER_ENABLE_LEGACY_DHCP_SERVER = "tether_enable_legacy_dhcp_server";
        @SystemApi
        @Readable
        public static final String TETHER_OFFLOAD_DISABLED = "tether_offload_disabled";
        @SystemApi
        @Readable
        public static final String TETHER_SUPPORTED = "tether_supported";
        @Readable
        public static final String TEXT_CLASSIFIER_ACTION_MODEL_PARAMS = "text_classifier_action_model_params";
        @Readable
        public static final String TEXT_CLASSIFIER_CONSTANTS = "text_classifier_constants";
        @SystemApi
        @Readable
        public static final String THEATER_MODE_ON = "theater_mode_on";
        @Readable
        public static final String TIME_ONLY_MODE_CONSTANTS = "time_only_mode_constants";
        @Readable
        @Deprecated
        public static final String TIME_REMAINING_ESTIMATE_BASED_ON_USAGE = "time_remaining_estimate_based_on_usage";
        @Readable
        @Deprecated
        public static final String TIME_REMAINING_ESTIMATE_MILLIS = "time_remaining_estimate_millis";
        public static final String[] TRANSIENT_SETTINGS;
        @Readable
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        @Readable
        public static final String TRUSTED_SOUND = "trusted_sound";
        @Readable
        public static final String TZINFO_UPDATE_CONTENT_URL = "tzinfo_content_url";
        @Readable
        public static final String TZINFO_UPDATE_METADATA_URL = "tzinfo_metadata_url";
        @Readable
        public static final String UNGAZE_SLEEP_ENABLED = "ungaze_sleep_enabled";
        @Readable
        public static final String UNINSTALLED_INSTANT_APP_MAX_CACHE_PERIOD = "uninstalled_instant_app_max_cache_period";
        @Readable
        public static final String UNINSTALLED_INSTANT_APP_MIN_CACHE_PERIOD = "uninstalled_instant_app_min_cache_period";
        @Readable
        public static final String UNLOCK_SOUND = "unlock_sound";
        @Readable
        public static final String UNUSED_STATIC_SHARED_LIB_MIN_CACHE_PERIOD = "unused_static_shared_lib_min_cache_period";
        @Readable
        public static final String UPDATABLE_DRIVER_ALL_APPS = "updatable_driver_all_apps";
        @Readable
        public static final String UPDATABLE_DRIVER_PRERELEASE_OPT_IN_APPS = "updatable_driver_prerelease_opt_in_apps";
        @Readable
        public static final String UPDATABLE_DRIVER_PRODUCTION_ALLOWLIST = "updatable_driver_production_allowlist";
        @Readable
        public static final String UPDATABLE_DRIVER_PRODUCTION_DENYLIST = "updatable_driver_production_denylist";
        @Readable
        public static final String UPDATABLE_DRIVER_PRODUCTION_DENYLISTS = "updatable_driver_production_denylists";
        @Readable
        public static final String UPDATABLE_DRIVER_PRODUCTION_OPT_IN_APPS = "updatable_driver_production_opt_in_apps";
        @Readable
        public static final String UPDATABLE_DRIVER_PRODUCTION_OPT_OUT_APPS = "updatable_driver_production_opt_out_apps";
        @Readable
        public static final String UPDATABLE_DRIVER_SPHAL_LIBRARIES = "updatable_driver_sphal_libraries";
        @Readable
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        @Readable
        public static final String USER_ABSENT_RADIOS_OFF_FOR_SMALL_BATTERY_ENABLED = "user_absent_radios_off_for_small_battery_enabled";
        @Readable
        public static final String USER_ABSENT_TOUCH_OFF_FOR_SMALL_BATTERY_ENABLED = "user_absent_touch_off_for_small_battery_enabled";
        @Readable
        public static final String USER_DISABLED_HDR_FORMATS = "user_disabled_hdr_formats";
        @Readable
        public static final String USER_PREFERRED_REFRESH_RATE = "user_preferred_refresh_rate";
        @Readable
        public static final String USER_PREFERRED_RESOLUTION_HEIGHT = "user_preferred_resolution_height";
        @Readable
        public static final String USER_PREFERRED_RESOLUTION_WIDTH = "user_preferred_resolution_width";
        @Readable
        public static final String USER_SWITCHER_ENABLED = "user_switcher_enabled";
        @Readable
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        @Readable
        @Deprecated
        public static final String USE_OPEN_WIFI_PACKAGE = "use_open_wifi_package";
        public static final String UWB_ENABLED = "uwb_enabled";
        @Readable
        @Deprecated
        public static final String VT_IMS_ENABLED = "vt_ims_enabled";
        @Readable
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
        @Readable
        public static final String WARNING_TEMPERATURE = "warning_temperature";
        public static final String WATCHDOG_TIMEOUT_MILLIS = "system_server_watchdog_timeout_ms";
        @Readable
        public static final String WEBVIEW_DATA_REDUCTION_PROXY_KEY = "webview_data_reduction_proxy_key";
        @SystemApi
        @Readable
        public static final String WEBVIEW_MULTIPROCESS = "webview_multiprocess";
        @Readable
        public static final String WEBVIEW_PROVIDER = "webview_provider";
        @Readable
        @Deprecated
        public static final String WFC_IMS_ENABLED = "wfc_ims_enabled";
        @Readable
        @Deprecated
        public static final String WFC_IMS_MODE = "wfc_ims_mode";
        @Readable
        @Deprecated
        public static final String WFC_IMS_ROAMING_ENABLED = "wfc_ims_roaming_enabled";
        @Readable
        @Deprecated
        public static final String WFC_IMS_ROAMING_MODE = "wfc_ims_roaming_mode";
        @Readable
        public static final String WIFI_ALWAYS_REQUESTED = "wifi_always_requested";
        @SystemApi
        @Readable
        public static final String WIFI_BADGING_THRESHOLDS = "wifi_badging_thresholds";
        @Readable
        public static final String WIFI_BOUNCE_DELAY_OVERRIDE_MS = "wifi_bounce_delay_override_ms";
        @Readable
        @Deprecated
        public static final String WIFI_CONNECTED_MAC_RANDOMIZATION_ENABLED = "wifi_connected_mac_randomization_enabled";
        @Readable
        public static final String WIFI_COUNTRY_CODE = "wifi_country_code";
        @Readable
        public static final String WIFI_DEVICE_OWNER_CONFIGS_LOCKDOWN = "wifi_device_owner_configs_lockdown";
        @Readable
        public static final String WIFI_DISPLAY_CERTIFICATION_ON = "wifi_display_certification_on";
        @Readable
        public static final String WIFI_DISPLAY_ON = "wifi_display_on";
        @Readable
        public static final String WIFI_DISPLAY_WPS_CONFIG = "wifi_display_wps_config";
        @Readable
        public static final String WIFI_ENHANCED_AUTO_JOIN = "wifi_enhanced_auto_join";
        @Readable
        public static final String WIFI_EPHEMERAL_OUT_OF_RANGE_TIMEOUT_MS = "wifi_ephemeral_out_of_range_timeout_ms";
        @Readable
        public static final String WIFI_FRAMEWORK_SCAN_INTERVAL_MS = "wifi_framework_scan_interval_ms";
        @Readable
        public static final String WIFI_FREQUENCY_BAND = "wifi_frequency_band";
        @Readable
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";
        @Readable
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        @Readable
        public static final String WIFI_MIGRATION_COMPLETED = "wifi_migration_completed";
        @Readable
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        @Readable
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        @Readable
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        @Readable
        public static final String WIFI_NETWORK_SHOW_RSSI = "wifi_network_show_rssi";
        @Readable
        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        @Readable
        public static final String WIFI_ON = "wifi_on";
        @Readable
        public static final String WIFI_ON_WHEN_PROXY_DISCONNECTED = "wifi_on_when_proxy_disconnected";
        @Readable
        @Deprecated
        public static final String WIFI_P2P_DEVICE_NAME = "wifi_p2p_device_name";
        @Readable
        @Deprecated
        public static final String WIFI_P2P_PENDING_FACTORY_RESET = "wifi_p2p_pending_factory_reset";
        @Readable
        @Deprecated
        public static final String WIFI_SCAN_ALWAYS_AVAILABLE = "wifi_scan_always_enabled";
        @Readable
        public static final String WIFI_SCAN_INTERVAL_WHEN_P2P_CONNECTED_MS = "wifi_scan_interval_p2p_connected_ms";
        @Readable
        @Deprecated
        public static final String WIFI_SCAN_THROTTLE_ENABLED = "wifi_scan_throttle_enabled";
        @Readable
        @Deprecated
        public static final String WIFI_SCORE_PARAMS = "wifi_score_params";
        @Readable
        @Deprecated
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
        @Readable
        public static final String WIFI_SUPPLICANT_SCAN_INTERVAL_MS = "wifi_supplicant_scan_interval_ms";
        @Readable
        @Deprecated
        public static final String WIFI_VERBOSE_LOGGING_ENABLED = "wifi_verbose_logging_enabled";
        @SystemApi
        @Readable
        @Deprecated
        public static final String WIFI_WAKEUP_ENABLED = "wifi_wakeup_enabled";
        @Readable
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        @Readable
        public static final String WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED = "wifi_watchdog_poor_network_test_enabled";
        @Readable
        public static final String WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wimax_networks_available_notification_on";
        @Readable
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        @Readable
        public static final String WIRELESS_CHARGING_STARTED_SOUND = "wireless_charging_started_sound";
        @Readable
        public static final String WTF_IS_FATAL = "wtf_is_fatal";
        @Deprecated
        public static final String ZEN_DURATION = "zen_duration";
        @Deprecated
        public static final int ZEN_DURATION_FOREVER = 0;
        @Deprecated
        public static final int ZEN_DURATION_PROMPT = -1;
        @Readable
        public static final String ZEN_MODE = "zen_mode";
        public static final int ZEN_MODE_ALARMS = 3;
        @Readable
        public static final String ZEN_MODE_CONFIG_ETAG = "zen_mode_config_etag";
        public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
        public static final int ZEN_MODE_NO_INTERRUPTIONS = 2;
        public static final int ZEN_MODE_OFF = 0;
        @Readable
        public static final String ZEN_MODE_RINGER_LEVEL = "zen_mode_ringer_level";
        @Deprecated
        public static final String ZEN_SETTINGS_SUGGESTION_VIEWED = "zen_settings_suggestion_viewed";
        @Deprecated
        public static final String ZEN_SETTINGS_UPDATED = "zen_settings_updated";
        @Readable
        public static final String ZRAM_ENABLED = "zram_enabled";
        private static final NameValueCache sNameValueCache;
        private static final ContentProviderHolder sProviderHolder;

        /* loaded from: classes3.dex */
        public static class Wearable {
            public static final String ACCESSIBILITY_VIBRATION_WATCH_ENABLED = "a11y_vibration_watch_enabled";
            public static final String ACCESSIBILITY_VIBRATION_WATCH_SPEED = "vibration_speed";
            public static final int ACCESSIBILITY_VIBRATION_WATCH_SPEED_FAST = 3;
            public static final int ACCESSIBILITY_VIBRATION_WATCH_SPEED_MEDIUM = 2;
            public static final int ACCESSIBILITY_VIBRATION_WATCH_SPEED_SLOW = 1;
            public static final int ACCESSIBILITY_VIBRATION_WATCH_SPEED_VERY_FAST = 4;
            public static final int ACCESSIBILITY_VIBRATION_WATCH_SPEED_VERY_SLOW = 0;
            public static final String ACCESSIBILITY_VIBRATION_WATCH_TYPE = "a11y_vibration_watch_type";
            public static final int ACCESSIBILITY_VIBRATION_WATCH_TYPE_DIGIT = 0;
            public static final int ACCESSIBILITY_VIBRATION_WATCH_TYPE_TERSE = 1;
            public static final String ALT_BYPASS_WIFI_REQUIREMENT_TIME_MILLIS = "alt_bypass_wifi_requirement_time_millis";
            public static final String AMBIENT_ENABLED = "ambient_enabled";
            public static final String AMBIENT_FORCE_WHEN_DOCKED = "ambient_force_when_docked";
            public static final String AMBIENT_LOW_BIT_ENABLED = "ambient_low_bit_enabled";
            public static final String AMBIENT_LOW_BIT_ENABLED_DEV = "ambient_low_bit_enabled_dev";
            public static final String AMBIENT_PLUGGED_TIMEOUT_MIN = "ambient_plugged_timeout_min";
            public static final String AMBIENT_TILT_TO_BRIGHT = "ambient_tilt_to_bright";
            public static final String AMBIENT_TILT_TO_WAKE = "ambient_tilt_to_wake";
            public static final String AMBIENT_TOUCH_TO_WAKE = "ambient_touch_to_wake";
            public static final String ANDROID_WEAR_VERSION = "android_wear_version";
            public static final int AUTO_TIME_OFF = 2;
            public static final int AUTO_TIME_ZONE_OFF = 2;
            public static final String AUTO_WIFI = "auto_wifi";
            public static final int AUTO_WIFI_DISABLED = 0;
            public static final int AUTO_WIFI_ENABLED = 1;
            public static final String BATTERY_SAVER_MODE = "battery_saver_mode";
            public static final int BATTERY_SAVER_MODE_CUSTOM = 4;
            public static final int BATTERY_SAVER_MODE_LIGHT = 1;
            public static final int BATTERY_SAVER_MODE_NONE = 0;
            public static final int BATTERY_SAVER_MODE_TIME_ONLY = 3;
            public static final int BATTERY_SAVER_MODE_TRADITIONAL_WATCH = 2;
            public static final String BEDTIME_HARD_MODE = "bedtime_hard_mode";
            public static final String BEDTIME_MODE = "bedtime_mode";
            public static final int BLUETOOTH_ROLE_CENTRAL = 1;
            public static final int BLUETOOTH_ROLE_PERIPHERAL = 2;
            public static final String BUG_REPORT = "bug_report";
            public static final int BUG_REPORT_DISABLED = 0;
            public static final int BUG_REPORT_ENABLED = 1;
            public static final String BURN_IN_PROTECTION_ENABLED = "burn_in_protection";
            public static final String BUTTON_SET = "button_set";
            public static final int CALL_FORWARD_ACTION_OFF = 2;
            public static final int CALL_FORWARD_ACTION_ON = 1;
            public static final int CALL_FORWARD_NO_LAST_ACTION = -1;
            public static final String CHARGING_SOUNDS_ENABLED = "wear_charging_sounds_enabled";
            public static final String CLOCKWORK_24HR_TIME = "clockwork_24hr_time";
            public static final String CLOCKWORK_AUTO_TIME = "clockwork_auto_time";
            public static final String CLOCKWORK_AUTO_TIME_ZONE = "clockwork_auto_time_zone";
            public static final String CLOCKWORK_LONG_PRESS_TO_ASSISTANT_ENABLED = "clockwork_long_press_to_assistant_enabled";
            public static final String CLOCKWORK_SYSUI_MAIN_ACTIVITY = "clockwork_sysui_main_activity";
            public static final String CLOCKWORK_SYSUI_PACKAGE = "clockwork_sysui_package";
            public static final String COMBINED_LOCATION_ENABLED = "combined_location_enable";
            public static final String COMPANION_BLE_ROLE = "companion_ble_role";
            public static final String COMPANION_NAME = "companion_bt_name";
            public static final String COMPANION_OS_VERSION = "wear_companion_os_version";
            public static final int COMPANION_OS_VERSION_UNDEFINED = -1;
            public static final String COOLDOWN_MODE_ON = "cooldown_mode_on";
            public static final String DECOMPOSABLE_WATCHFACE = "current_watchface_decomposable";
            public static final String DEFAULT_VIBRATION = "default_vibration";
            public static final String DYNAMIC_COLOR_THEME_ENABLED = "dynamic_color_theme_enabled";
            public static final String EARLY_UPDATES_STATUS = "early_updates_status";
            public static final int EARLY_UPDATES_STATUS_ABORTED = 4;
            public static final int EARLY_UPDATES_STATUS_NOT_STARTED = 0;
            public static final int EARLY_UPDATES_STATUS_SKIPPED = 3;
            public static final int EARLY_UPDATES_STATUS_STARTED = 1;
            public static final int EARLY_UPDATES_STATUS_SUCCESS = 2;
            public static final String ENABLE_ALL_LANGUAGES = "enable_all_languages";
            public static final String GESTURE_TOUCH_AND_HOLD_WATCH_FACE_ENABLED = "gesture_touch_and_hold_watchface_enabled";
            public static final String GMS_CHECKIN_TIMEOUT_MIN = "gms_checkin_timeout_min";
            public static final String HAS_PAY_TOKENS = "has_pay_tokens";
            public static final int HFP_CLIENT_DISABLED = 2;
            public static final int HFP_CLIENT_ENABLED = 1;
            public static final int HFP_CLIENT_UNSET = 0;
            public static final String HOTWORD_DETECTION_ENABLED = "hotword_detection_enabled";
            public static final int INVALID_AUTO_TIME_STATE = 3;
            public static final int INVALID_AUTO_TIME_ZONE_STATE = 3;
            public static final String LAST_CALL_FORWARD_ACTION = "last_call_forward_action";
            public static final String LOCK_SCREEN_STATE = "lock_screen_state";
            public static final int LOCK_SCREEN_STATE_NONE = 0;
            public static final int LOCK_SCREEN_STATE_PATTERN = 2;
            public static final int LOCK_SCREEN_STATE_PIN = 1;
            public static final String MASTER_GESTURES_ENABLED = "master_gestures_enabled";
            public static final String MOBILE_SIGNAL_DETECTOR = "mobile_signal_detector";
            public static final String MUTE_WHEN_OFF_BODY_ENABLED = "obtain_mute_when_off_body";
            public static final String OBTAIN_PAIRED_DEVICE_LOCATION = "obtain_paired_device_location";
            public static final int OEM_SETUP_COMPLETED_FAILURE = 0;
            public static final String OEM_SETUP_COMPLETED_STATUS = "oem_setup_completed_status";
            public static final int OEM_SETUP_COMPLETED_SUCCESS = 1;
            public static final String OEM_SETUP_VERSION = "oem_setup_version";
            public static final String PAIRED_DEVICE_OS_TYPE = "paired_device_os_type";
            public static final int PAIRED_DEVICE_OS_TYPE_ANDROID = 1;
            public static final int PAIRED_DEVICE_OS_TYPE_IOS = 2;
            public static final int PAIRED_DEVICE_OS_TYPE_UNKNOWN = 0;
            public static final String PHONE_PLAY_STORE_AVAILABILITY = "phone_play_store_availability";
            public static final int PHONE_PLAY_STORE_AVAILABILITY_UNKNOWN = 0;
            public static final int PHONE_PLAY_STORE_AVAILABLE = 1;
            public static final int PHONE_PLAY_STORE_UNAVAILABLE = 2;
            public static final String RSB_WAKE_ENABLED = "rsb_wake_enabled";
            public static final String SCREENSHOT_ENABLED = "screenshot_enabled";
            public static final String SCREEN_UNLOCK_SOUND_ENABLED = "screen_unlock_sound_enabled";
            public static final String SETUP_LOCALE = "setup_locale";
            public static final String SETUP_SKIPPED = "setup_skipped";
            public static final int SETUP_SKIPPED_NO = 2;
            public static final int SETUP_SKIPPED_UNKNOWN = 0;
            public static final int SETUP_SKIPPED_YES = 1;
            public static final String SIDE_BUTTON = "side_button";
            public static final String SMART_ILLUMINATE_ENABLED = "smart_illuminate_enabled";
            public static final String SMART_REPLIES_ENABLED = "smart_replies_enabled";
            public static final String STEM_1_DATA = "STEM_1_DATA";
            public static final String STEM_1_DEFAULT_DATA = "STEM_1_DEFAULT_DATA";
            public static final String STEM_1_TYPE = "STEM_1_TYPE";
            public static final String STEM_2_DATA = "STEM_2_DATA";
            public static final String STEM_2_DEFAULT_DATA = "STEM_2_DEFAULT_DATA";
            public static final String STEM_2_TYPE = "STEM_2_TYPE";
            public static final String STEM_3_DATA = "STEM_3_DATA";
            public static final String STEM_3_DEFAULT_DATA = "STEM_3_DEFAULT_DATA";
            public static final String STEM_3_TYPE = "STEM_3_TYPE";
            public static final int STEM_TYPE_APP_LAUNCH = 0;
            public static final int STEM_TYPE_CONTACT_LAUNCH = 1;
            public static final int STEM_TYPE_UNKNOWN = -1;
            public static final int SYNC_TIME_FROM_NETWORK = 1;
            public static final int SYNC_TIME_FROM_PHONE = 0;
            public static final int SYNC_TIME_ZONE_FROM_NETWORK = 1;
            public static final int SYNC_TIME_ZONE_FROM_PHONE = 0;
            public static final String SYSTEM_CAPABILITIES = "system_capabilities";
            public static final String SYSTEM_EDITION = "android_wear_system_edition";
            public static final String UNGAZE_ENABLED = "ungaze_enabled";
            public static final int UPGRADE_DATA_MIGRATION_DONE = 2;
            public static final int UPGRADE_DATA_MIGRATION_NOT_NEEDED = 0;
            public static final int UPGRADE_DATA_MIGRATION_PENDING = 1;
            public static final String UPGRADE_DATA_MIGRATION_STATUS = "upgrade_data_migration_status";
            public static final String USER_HFP_CLIENT_SETTING = "user_hfp_client_setting";
            public static final String WEAR_ACTIVITY_AUTO_RESUME_TIMEOUT_MS = "wear_activity_auto_resume_timeout_ms";
            public static final String WEAR_ACTIVITY_AUTO_RESUME_TIMEOUT_SET_BY_USER = "wear_activity_auto_resume_timeout_set_by_user";
            public static final String WEAR_OS_VERSION_STRING = "wear_os_version_string";
            public static final String WEAR_PLATFORM_MR_NUMBER = "wear_platform_mr_number";
            public static final String WET_MODE_ON = "wet_mode_on";
            public static final String WIFI_POWER_SAVE = "wifi_power_save";
            public static final String WRIST_ORIENTATION_MODE = "wear_wrist_orientation_mode";
        }

        static {
            Uri parse = Uri.parse("content://settings/global");
            CONTENT_URI = parse;
            TRANSIENT_SETTINGS = new String[]{CLOCKWORK_HOME_READY};
            LEGACY_RESTORE_SETTINGS = new String[0];
            ContentProviderHolder contentProviderHolder = new ContentProviderHolder(parse);
            sProviderHolder = contentProviderHolder;
            sNameValueCache = new NameValueCache(parse, Settings.CALL_METHOD_GET_GLOBAL, Settings.CALL_METHOD_PUT_GLOBAL, Settings.CALL_METHOD_DELETE_GLOBAL, contentProviderHolder, Global.class);
            HashSet<String> hashSet = new HashSet<>(8);
            MOVED_TO_SECURE = hashSet;
            hashSet.add("install_non_market_apps");
            hashSet.add("zen_duration");
            hashSet.add("show_zen_upgrade_notification");
            hashSet.add("show_zen_settings_suggestion");
            hashSet.add("zen_settings_updated");
            hashSet.add("zen_settings_suggestion_viewed");
            hashSet.add("charging_sounds_enabled");
            hashSet.add("charging_vibration_enabled");
            hashSet.add("notification_bubbles");
            hashSet.add("bugreport_in_power_menu");
            hashSet.add("custom_bugreport_handler_app");
            hashSet.add("custom_bugreport_handler_user");
            HashSet<String> hashSet2 = new HashSet<>(1);
            MOVED_TO_SYSTEM = hashSet2;
            hashSet2.add("apply_ramping_ringer");
            MULTI_SIM_USER_PREFERRED_SUBS = new String[]{"user_preferred_sub1", "user_preferred_sub2", "user_preferred_sub3"};
            ArraySet arraySet = new ArraySet();
            INSTANT_APP_SETTINGS = arraySet;
            arraySet.add("wait_for_debugger");
            arraySet.add("device_provisioned");
            arraySet.add(DEVELOPMENT_FORCE_RESIZABLE_ACTIVITIES);
            arraySet.add(DEVELOPMENT_FORCE_RTL);
            arraySet.add(EPHEMERAL_COOKIE_MAX_SIZE_BYTES);
            arraySet.add("airplane_mode_on");
            arraySet.add("window_animation_scale");
            arraySet.add("transition_animation_scale");
            arraySet.add("animator_duration_scale");
            arraySet.add(DEBUG_VIEW_ATTRIBUTES);
            arraySet.add(DEBUG_VIEW_ATTRIBUTES_APPLICATION_PACKAGE);
            arraySet.add(WTF_IS_FATAL);
            arraySet.add(SEND_ACTION_APP_ERROR);
            arraySet.add(ZEN_MODE);
        }

        public static String zenModeToString(int mode) {
            return mode == 1 ? "ZEN_MODE_IMPORTANT_INTERRUPTIONS" : mode == 3 ? "ZEN_MODE_ALARMS" : mode == 2 ? "ZEN_MODE_NO_INTERRUPTIONS" : "ZEN_MODE_OFF";
        }

        public static boolean isValidZenMode(int value) {
            switch (value) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return true;
                default:
                    return false;
            }
        }

        public static void getMovedToSecureSettings(Set<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_SECURE);
        }

        public static void getMovedToSystemSettings(Set<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_SYSTEM);
        }

        public static void clearProviderForTest() {
            sProviderHolder.clearProviderForTest();
            sNameValueCache.clearGenerationTrackerForTest();
        }

        public static void getPublicSettings(Set<String> allKeys, Set<String> readableKeys, ArrayMap<String, Integer> readableKeysWithMaxTargetSdk) {
            Settings.getPublicSettingsForClass(Global.class, allKeys, readableKeys, readableKeysWithMaxTargetSdk);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, resolver.getUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Global to android.provider.Settings.Secure, returning read-only value.");
                return Secure.getStringForUser(resolver, name, userHandle);
            } else if (MOVED_TO_SYSTEM.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Global to android.provider.Settings.System, returning read-only value.");
                return System.getStringForUser(resolver, name, userHandle);
            } else {
                return sNameValueCache.getStringForUser(resolver, name, userHandle);
            }
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, null, false, resolver.getUserId(), false);
        }

        public static boolean putString(ContentResolver resolver, String name, String value, String tag, boolean makeDefault, boolean overrideableByRestore) {
            return putStringForUser(resolver, name, value, tag, makeDefault, resolver.getUserId(), overrideableByRestore);
        }

        @SystemApi
        public static boolean putString(ContentResolver resolver, String name, String value, String tag, boolean makeDefault) {
            return putStringForUser(resolver, name, value, tag, makeDefault, resolver.getUserId(), false);
        }

        @SystemApi
        public static void resetToDefaults(ContentResolver resolver, String tag) {
            resetToDefaultsAsUser(resolver, tag, 1, resolver.getUserId());
        }

        public static void resetToDefaultsAsUser(ContentResolver resolver, String tag, int mode, int userHandle) {
            try {
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                if (tag != null) {
                    arg.putString(Settings.CALL_METHOD_TAG_KEY, tag);
                }
                arg.putInt(Settings.CALL_METHOD_RESET_MODE_KEY, mode);
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_RESET_GLOBAL, null, arg);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't reset do defaults for " + CONTENT_URI, e);
            }
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            return putStringForUser(resolver, name, value, null, false, userHandle, false);
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, String tag, boolean makeDefault, int userHandle, boolean overrideableByRestore) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Global to android.provider.Settings.Secure, value is unchanged.");
                return Secure.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, overrideableByRestore);
            } else if (MOVED_TO_SYSTEM.contains(name)) {
                Log.m104w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Global to android.provider.Settings.System, value is unchanged.");
                return System.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, overrideableByRestore);
            } else {
                return sNameValueCache.putStringForUser(resolver, name, value, tag, makeDefault, userHandle, overrideableByRestore);
            }
        }

        public static Uri getUriFor(String name) {
            return getUriFor(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            String v = getString(cr, name);
            return Settings.parseIntSettingWithDefault(v, def);
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, name);
            return Settings.parseIntSetting(v, name);
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putString(cr, name, Integer.toString(value));
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            String v = getString(cr, name);
            return Settings.parseLongSettingWithDefault(v, def);
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, name);
            return Settings.parseLongSetting(v, name);
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putString(cr, name, Long.toString(value));
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            String v = getString(cr, name);
            return Settings.parseFloatSettingWithDefault(v, def);
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, name);
            return Settings.parseFloatSetting(v, name);
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putString(cr, name, Float.toString(value));
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* loaded from: classes3.dex */
    public static final class Config extends NameValueTable {
        public static final Uri CONTENT_URI;
        public static final int SYNC_DISABLED_MODE_NONE = 0;
        public static final int SYNC_DISABLED_MODE_PERSISTENT = 1;
        public static final int SYNC_DISABLED_MODE_UNTIL_REBOOT = 2;
        private static final NameValueCache sNameValueCache;
        private static final ContentProviderHolder sProviderHolder;

        @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface SyncDisabledMode {
        }

        static {
            Uri parse = Uri.parse("content://settings/config");
            CONTENT_URI = parse;
            ContentProviderHolder contentProviderHolder = new ContentProviderHolder(parse);
            sProviderHolder = contentProviderHolder;
            sNameValueCache = new NameValueCache(parse, Settings.CALL_METHOD_GET_CONFIG, Settings.CALL_METHOD_PUT_CONFIG, Settings.CALL_METHOD_DELETE_CONFIG, Settings.CALL_METHOD_LIST_CONFIG, Settings.CALL_METHOD_SET_ALL_CONFIG, contentProviderHolder, Config.class);
        }

        private Config() {
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static String getString(String name) {
            ContentResolver resolver = getContentResolver();
            return sNameValueCache.getStringForUser(resolver, name, resolver.getUserId());
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static Map<String, String> getStrings(String namespace, List<String> names) {
            return getStrings(getContentResolver(), namespace, names);
        }

        public static Map<String, String> getStrings(ContentResolver resolver, String namespace, List<String> names) {
            List<String> compositeNames = new ArrayList<>(names.size());
            for (String name : names) {
                compositeNames.add(createCompositeName(namespace, name));
            }
            String prefix = createPrefix(namespace);
            ArrayMap<String, String> rawKeyValues = sNameValueCache.getStringsForPrefix(resolver, prefix, compositeNames);
            int size = rawKeyValues.size();
            int substringLength = prefix.length();
            ArrayMap<String, String> keyValues = new ArrayMap<>(size);
            for (int i = 0; i < size; i++) {
                keyValues.put(rawKeyValues.keyAt(i).substring(substringLength), rawKeyValues.valueAt(i));
            }
            return keyValues;
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static boolean putString(String namespace, String name, String value, boolean makeDefault) {
            ContentResolver resolver = getContentResolver();
            return sNameValueCache.putStringForUser(resolver, createCompositeName(namespace, name), value, null, makeDefault, resolver.getUserId(), false);
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static boolean setStrings(String namespace, Map<String, String> keyValues) throws DeviceConfig.BadConfigException {
            return setStrings(getContentResolver(), namespace, keyValues);
        }

        public static boolean setStrings(ContentResolver resolver, String namespace, Map<String, String> keyValues) throws DeviceConfig.BadConfigException {
            HashMap<String, String> compositeKeyValueMap = new HashMap<>(keyValues.keySet().size());
            for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                compositeKeyValueMap.put(createCompositeName(namespace, entry.getKey()), entry.getValue());
            }
            int result = sNameValueCache.setStringsForPrefix(resolver, createPrefix(namespace), compositeKeyValueMap);
            if (result == 1) {
                return true;
            }
            if (result == 2) {
                return false;
            }
            throw new DeviceConfig.BadConfigException();
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static boolean deleteString(String namespace, String name) {
            ContentResolver resolver = getContentResolver();
            return sNameValueCache.deleteStringForUser(resolver, createCompositeName(namespace, name), resolver.getUserId());
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void resetToDefaults(int resetMode, String namespace) {
            try {
                ContentResolver resolver = getContentResolver();
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, resolver.getUserId());
                arg.putInt(Settings.CALL_METHOD_RESET_MODE_KEY, resetMode);
                if (namespace != null) {
                    arg.putString(Settings.CALL_METHOD_PREFIX_KEY, createPrefix(namespace));
                }
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_RESET_CONFIG, null, arg);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't reset to defaults for " + CONTENT_URI, e);
            }
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void setSyncDisabledMode(int disableSyncMode) {
            try {
                ContentResolver resolver = getContentResolver();
                Bundle args = new Bundle();
                args.putInt(Settings.CALL_METHOD_SYNC_DISABLED_MODE_KEY, disableSyncMode);
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_SET_SYNC_DISABLED_MODE_CONFIG, null, args);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't set sync disabled mode " + CONTENT_URI, e);
            }
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static int getSyncDisabledMode() {
            try {
                ContentResolver resolver = getContentResolver();
                Bundle args = Bundle.EMPTY;
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                Bundle bundle = cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_GET_SYNC_DISABLED_MODE_CONFIG, null, args);
                return bundle.getInt(Settings.KEY_CONFIG_GET_SYNC_DISABLED_MODE_RETURN);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't query sync disabled mode " + CONTENT_URI, e);
                return -1;
            }
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void setMonitorCallback(ContentResolver resolver, Executor executor, DeviceConfig.MonitorCallback callback) {
            setMonitorCallbackAsUser(executor, resolver, resolver.getUserId(), callback);
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void clearMonitorCallback(ContentResolver resolver) {
            try {
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, resolver.getUserId());
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_UNREGISTER_MONITOR_CALLBACK_CONFIG, null, arg);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't clear config monitor callback", e);
            }
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void registerContentObserver(String namespace, boolean notifyForDescendants, ContentObserver observer) {
            ActivityThread.currentApplication().getContentResolver().registerContentObserver(createNamespaceUri(namespace), notifyForDescendants, observer);
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static void unregisterContentObserver(ContentObserver observer) {
            ActivityThread.currentApplication().getContentResolver().unregisterContentObserver(observer);
        }

        public static int checkCallingOrSelfPermission(String permission) {
            return ActivityThread.currentApplication().getApplicationContext().checkCallingOrSelfPermission(permission);
        }

        public static void enforceReadPermission(String namespace) {
            if (ActivityThread.currentApplication().getApplicationContext().checkCallingOrSelfPermission(Manifest.C0000permission.READ_DEVICE_CONFIG) != 0 && !DeviceConfig.getPublicNamespaces().contains(namespace)) {
                throw new SecurityException("Permission denial: reading from settings requires:android.permission.READ_DEVICE_CONFIG");
            }
        }

        private static void setMonitorCallbackAsUser(final Executor executor, ContentResolver resolver, int userHandle, final DeviceConfig.MonitorCallback callback) {
            try {
                Bundle arg = new Bundle();
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                arg.putParcelable(Settings.CALL_METHOD_MONITOR_CALLBACK_KEY, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.provider.Settings$Config$$ExternalSyntheticLambda2
                    @Override // android.p008os.RemoteCallback.OnResultListener
                    public final void onResult(Bundle bundle) {
                        Settings.Config.handleMonitorCallback(bundle, executor, callback);
                    }
                }));
                ContentProviderHolder contentProviderHolder = sProviderHolder;
                IContentProvider cp = contentProviderHolder.getProvider(resolver);
                cp.call(resolver.getAttributionSource(), contentProviderHolder.mUri.getAuthority(), Settings.CALL_METHOD_REGISTER_MONITOR_CALLBACK_CONFIG, null, arg);
            } catch (RemoteException e) {
                Log.m103w(Settings.TAG, "Can't set config monitor callback", e);
            }
        }

        public static void clearProviderForTest() {
            sProviderHolder.clearProviderForTest();
            sNameValueCache.clearGenerationTrackerForTest();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public static void handleMonitorCallback(Bundle result, Executor executor, final DeviceConfig.MonitorCallback monitorCallback) {
            char c;
            String callbackType = result.getString(Settings.EXTRA_MONITOR_CALLBACK_TYPE, "");
            switch (callbackType.hashCode()) {
                case -751689299:
                    if (callbackType.equals(Settings.EXTRA_NAMESPACE_UPDATED_CALLBACK)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1166372032:
                    if (callbackType.equals(Settings.EXTRA_ACCESS_CALLBACK)) {
                        c = 1;
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
                    final String updatedNamespace = result.getString(Settings.EXTRA_NAMESPACE);
                    if (updatedNamespace != null) {
                        executor.execute(new Runnable() { // from class: android.provider.Settings$Config$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                monitorCallback.onNamespaceUpdate(updatedNamespace);
                            }
                        });
                        return;
                    }
                    return;
                case 1:
                    final String callingPackage = result.getString("calling_package", null);
                    final String namespace = result.getString(Settings.EXTRA_NAMESPACE, null);
                    if (namespace != null && callingPackage != null) {
                        executor.execute(new Runnable() { // from class: android.provider.Settings$Config$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                monitorCallback.onDeviceConfigAccess(callingPackage, namespace);
                            }
                        });
                        return;
                    }
                    return;
                default:
                    Slog.m90w(Settings.TAG, "Unrecognized DeviceConfig callback");
                    return;
            }
        }

        private static String createCompositeName(String namespace, String name) {
            Preconditions.checkNotNull(namespace);
            Preconditions.checkNotNull(name);
            return createPrefix(namespace) + name;
        }

        private static String createPrefix(String namespace) {
            Preconditions.checkNotNull(namespace);
            return namespace + "/";
        }

        private static Uri createNamespaceUri(String namespace) {
            Preconditions.checkNotNull(namespace);
            return CONTENT_URI.buildUpon().appendPath(namespace).build();
        }

        private static ContentResolver getContentResolver() {
            return ActivityThread.currentApplication().getContentResolver();
        }
    }

    /* loaded from: classes3.dex */
    public static final class Bookmarks implements BaseColumns {
        public static final String FOLDER = "folder";

        /* renamed from: ID */
        public static final String f338ID = "_id";
        public static final String INTENT = "intent";
        public static final String ORDERING = "ordering";
        public static final String SHORTCUT = "shortcut";
        private static final String TAG = "Bookmarks";
        public static final String TITLE = "title";
        private static final String sShortcutSelection = "shortcut=?";
        public static final Uri CONTENT_URI = Uri.parse("content://settings/bookmarks");
        private static final String[] sIntentProjection = {"intent"};
        private static final String[] sShortcutProjection = {"_id", "shortcut"};

        public static Intent getIntentForShortcut(ContentResolver cr, char shortcut) {
            Intent intent = null;
            Cursor c = cr.query(CONTENT_URI, sIntentProjection, sShortcutSelection, new String[]{String.valueOf((int) shortcut)}, ORDERING);
            while (intent == null) {
                try {
                    if (!c.moveToNext()) {
                        break;
                    }
                    try {
                        String intentURI = c.getString(c.getColumnIndexOrThrow("intent"));
                        intent = Intent.parseUri(intentURI, 0);
                    } catch (IllegalArgumentException e) {
                        Log.m103w(TAG, "Intent column not found", e);
                    } catch (URISyntaxException e2) {
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
            return intent;
        }

        public static Uri add(ContentResolver cr, Intent intent, String title, String folder, char shortcut, int ordering) {
            if (shortcut != 0) {
                cr.delete(CONTENT_URI, sShortcutSelection, new String[]{String.valueOf((int) shortcut)});
            }
            ContentValues values = new ContentValues();
            if (title != null) {
                values.put("title", title);
            }
            if (folder != null) {
                values.put("folder", folder);
            }
            values.put("intent", intent.toUri(0));
            if (shortcut != 0) {
                values.put("shortcut", Integer.valueOf(shortcut));
            }
            values.put(ORDERING, Integer.valueOf(ordering));
            return cr.insert(CONTENT_URI, values);
        }

        public static CharSequence getLabelForFolder(Resources r, String folder) {
            return folder;
        }

        public static CharSequence getTitle(Context context, Cursor cursor) {
            int titleColumn = cursor.getColumnIndex("title");
            int intentColumn = cursor.getColumnIndex("intent");
            if (titleColumn == -1 || intentColumn == -1) {
                throw new IllegalArgumentException("The cursor must contain the TITLE and INTENT columns.");
            }
            String title = cursor.getString(titleColumn);
            if (!TextUtils.isEmpty(title)) {
                return title;
            }
            String intentUri = cursor.getString(intentColumn);
            if (TextUtils.isEmpty(intentUri)) {
                return "";
            }
            try {
                Intent intent = Intent.parseUri(intentUri, 0);
                PackageManager packageManager = context.getPackageManager();
                ResolveInfo info = packageManager.resolveActivity(intent, 0);
                return info != null ? info.loadLabel(packageManager) : "";
            } catch (URISyntaxException e) {
                return "";
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Panel {
        public static final String ACTION_INTERNET_CONNECTIVITY = "android.settings.panel.action.INTERNET_CONNECTIVITY";
        public static final String ACTION_NFC = "android.settings.panel.action.NFC";
        public static final String ACTION_VOLUME = "android.settings.panel.action.VOLUME";
        public static final String ACTION_WIFI = "android.settings.panel.action.WIFI";

        private Panel() {
        }
    }

    public static boolean isCallingPackageAllowedToWriteSettings(Context context, int uid, String callingPackage, boolean throwException) {
        return isCallingPackageAllowedToPerformAppOpsProtectedOperation(context, uid, callingPackage, null, throwException, 23, PM_WRITE_SETTINGS, false);
    }

    @SystemApi
    @Deprecated
    public static boolean checkAndNoteWriteSettingsOperation(Context context, int uid, String callingPackage, boolean throwException) {
        return checkAndNoteWriteSettingsOperation(context, uid, callingPackage, null, throwException);
    }

    @SystemApi
    public static boolean checkAndNoteWriteSettingsOperation(Context context, int uid, String callingPackage, String callingAttributionTag, boolean throwException) {
        return isCallingPackageAllowedToPerformAppOpsProtectedOperation(context, uid, callingPackage, callingAttributionTag, throwException, 23, PM_WRITE_SETTINGS, true);
    }

    public static boolean isCallingPackageAllowedToDrawOverlays(Context context, int uid, String callingPackage, boolean throwException) {
        return isCallingPackageAllowedToPerformAppOpsProtectedOperation(context, uid, callingPackage, null, throwException, 24, PM_SYSTEM_ALERT_WINDOW, false);
    }

    public static boolean checkAndNoteDrawOverlaysOperation(Context context, int uid, String callingPackage, String callingAttributionTag, boolean throwException) {
        return isCallingPackageAllowedToPerformAppOpsProtectedOperation(context, uid, callingPackage, callingAttributionTag, throwException, 24, PM_SYSTEM_ALERT_WINDOW, true);
    }

    @Deprecated
    public static boolean isCallingPackageAllowedToPerformAppOpsProtectedOperation(Context context, int uid, String callingPackage, boolean throwException, int appOpsOpCode, String[] permissions, boolean makeNote) {
        return isCallingPackageAllowedToPerformAppOpsProtectedOperation(context, uid, callingPackage, null, throwException, appOpsOpCode, permissions, makeNote);
    }

    public static boolean isCallingPackageAllowedToPerformAppOpsProtectedOperation(Context context, int uid, String callingPackage, String callingAttributionTag, boolean throwException, int appOpsOpCode, String[] permissions, boolean makeNote) {
        if (callingPackage == null) {
            return false;
        }
        AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = makeNote ? appOpsMgr.noteOpNoThrow(appOpsOpCode, uid, callingPackage, callingAttributionTag, (String) null) : appOpsMgr.checkOpNoThrow(appOpsOpCode, uid, callingPackage);
        switch (mode) {
            case 0:
                return true;
            case 3:
                for (String permission : permissions) {
                    if (context.checkCallingOrSelfPermission(permission) == 0) {
                        return true;
                    }
                }
                break;
        }
        if (throwException) {
            StringBuilder exceptionMessage = new StringBuilder();
            exceptionMessage.append(callingPackage);
            exceptionMessage.append(" was not granted ");
            if (permissions.length > 1) {
                exceptionMessage.append(" either of these permissions: ");
            } else {
                exceptionMessage.append(" this permission: ");
            }
            int i = 0;
            while (i < permissions.length) {
                exceptionMessage.append(permissions[i]);
                exceptionMessage.append(i == permissions.length - 1 ? MediaMetrics.SEPARATOR : ", ");
                i++;
            }
            throw new SecurityException(exceptionMessage.toString());
        }
        return false;
    }

    public static String getPackageNameForUid(Context context, int uid) {
        String[] packages = context.getPackageManager().getPackagesForUid(uid);
        if (packages == null) {
            return null;
        }
        return packages[0];
    }
}
