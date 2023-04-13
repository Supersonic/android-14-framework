package android.content;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.backup.FullBackup;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.BundleMerger;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.ShellCommand;
import android.p008os.StrictMode;
import android.p008os.UserHandle;
import android.p008os.storage.StorageManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.internal.C4057R;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Intent implements Parcelable, Cloneable {
    @SystemApi
    public static final String ACTION_ACTIVITY_RECOGNIZER = "android.intent.action.ACTIVITY_RECOGNIZER";
    public static final String ACTION_ADVANCED_SETTINGS_CHANGED = "android.intent.action.ADVANCED_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE";
    public static final String ACTION_ALARM_CHANGED = "android.intent.action.ALARM_CHANGED";
    public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_APPLICATION_LOCALE_CHANGED = "android.intent.action.APPLICATION_LOCALE_CHANGED";
    public static final String ACTION_APPLICATION_PREFERENCES = "android.intent.action.APPLICATION_PREFERENCES";
    public static final String ACTION_APPLICATION_RESTRICTIONS_CHANGED = "android.intent.action.APPLICATION_RESTRICTIONS_CHANGED";
    public static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR";
    public static final String ACTION_ASSIST = "android.intent.action.ASSIST";
    public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";
    public static final String ACTION_AUTO_REVOKE_PERMISSIONS = "android.intent.action.AUTO_REVOKE_PERMISSIONS";
    public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    @SystemApi
    public static final String ACTION_BATTERY_LEVEL_CHANGED = "android.intent.action.BATTERY_LEVEL_CHANGED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";
    @SystemApi
    public static final String ACTION_CALL_EMERGENCY = "android.intent.action.CALL_EMERGENCY";
    @SystemApi
    public static final String ACTION_CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";
    public static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";
    public static final String ACTION_CANCEL_ENABLE_ROLLBACK = "android.intent.action.CANCEL_ENABLE_ROLLBACK";
    public static final String ACTION_CARRIER_SETUP = "android.intent.action.CARRIER_SETUP";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
    @Deprecated
    public static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
    public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    public static final String ACTION_CREATE_DOCUMENT = "android.intent.action.CREATE_DOCUMENT";
    public static final String ACTION_CREATE_NOTE = "android.intent.action.CREATE_NOTE";
    public static final String ACTION_CREATE_REMINDER = "android.intent.action.CREATE_REMINDER";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
    public static final String ACTION_DEFAULT = "android.intent.action.VIEW";
    public static final String ACTION_DEFINE = "android.intent.action.DEFINE";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    @SystemApi
    public static final String ACTION_DEVICE_CUSTOMIZATION_READY = "android.intent.action.DEVICE_CUSTOMIZATION_READY";
    @SystemApi
    @Deprecated
    public static final String ACTION_DEVICE_INITIALIZATION_WIZARD = "android.intent.action.DEVICE_INITIALIZATION_WIZARD";
    public static final String ACTION_DEVICE_LOCKED_CHANGED = "android.intent.action.DEVICE_LOCKED_CHANGED";
    @Deprecated
    public static final String ACTION_DEVICE_STORAGE_FULL = "android.intent.action.DEVICE_STORAGE_FULL";
    @Deprecated
    public static final String ACTION_DEVICE_STORAGE_LOW = "android.intent.action.DEVICE_STORAGE_LOW";
    @Deprecated
    public static final String ACTION_DEVICE_STORAGE_NOT_FULL = "android.intent.action.DEVICE_STORAGE_NOT_FULL";
    @Deprecated
    public static final String ACTION_DEVICE_STORAGE_OK = "android.intent.action.DEVICE_STORAGE_OK";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    @SystemApi
    public static final String ACTION_DIAL_EMERGENCY = "android.intent.action.DIAL_EMERGENCY";
    public static final String ACTION_DISMISS_KEYBOARD_SHORTCUTS = "com.android.intent.action.DISMISS_KEYBOARD_SHORTCUTS";
    public static final String ACTION_DISTRACTING_PACKAGES_CHANGED = "android.intent.action.DISTRACTING_PACKAGES_CHANGED";
    public static final String ACTION_DOCK_ACTIVE = "android.intent.action.DOCK_ACTIVE";
    public static final String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT";
    public static final String ACTION_DOCK_IDLE = "android.intent.action.DOCK_IDLE";
    @SystemApi
    public static final String ACTION_DOMAINS_NEED_VERIFICATION = "android.intent.action.DOMAINS_NEED_VERIFICATION";
    public static final String ACTION_DREAMING_STARTED = "android.intent.action.DREAMING_STARTED";
    public static final String ACTION_DREAMING_STOPPED = "android.intent.action.DREAMING_STOPPED";
    public static final String ACTION_DYNAMIC_SENSOR_CHANGED = "android.intent.action.DYNAMIC_SENSOR_CHANGED";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
    public static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
    @SystemApi
    public static final String ACTION_FACTORY_RESET = "android.intent.action.FACTORY_RESET";
    public static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_GET_RESTRICTION_ENTRIES = "android.intent.action.GET_RESTRICTION_ENTRIES";
    @SystemApi
    public static final String ACTION_GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";
    public static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED";
    public static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED";
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String ACTION_IDLE_MAINTENANCE_END = "android.intent.action.ACTION_IDLE_MAINTENANCE_END";
    public static final String ACTION_IDLE_MAINTENANCE_START = "android.intent.action.ACTION_IDLE_MAINTENANCE_START";
    @SystemApi
    public static final String ACTION_INCIDENT_REPORT_READY = "android.intent.action.INCIDENT_REPORT_READY";
    public static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";
    public static final String ACTION_INSTALL_FAILURE = "android.intent.action.INSTALL_FAILURE";
    @SystemApi
    public static final String ACTION_INSTALL_INSTANT_APP_PACKAGE = "android.intent.action.INSTALL_INSTANT_APP_PACKAGE";
    @Deprecated
    public static final String ACTION_INSTALL_PACKAGE = "android.intent.action.INSTALL_PACKAGE";
    @SystemApi
    public static final String ACTION_INSTANT_APP_RESOLVER_SETTINGS = "android.intent.action.INSTANT_APP_RESOLVER_SETTINGS";
    @SystemApi
    @Deprecated
    public static final String ACTION_INTENT_FILTER_NEEDS_VERIFICATION = "android.intent.action.INTENT_FILTER_NEEDS_VERIFICATION";
    public static final String ACTION_LAUNCH_CAPTURE_CONTENT_ACTIVITY_FOR_NOTE = "android.intent.action.LAUNCH_CAPTURE_CONTENT_ACTIVITY_FOR_NOTE";
    @SystemApi
    public static final String ACTION_LOAD_DATA = "android.intent.action.LOAD_DATA";
    public static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
    public static final String ACTION_LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED";
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_MANAGED_PROFILE_ADDED = "android.intent.action.MANAGED_PROFILE_ADDED";
    public static final String ACTION_MANAGED_PROFILE_AVAILABLE = "android.intent.action.MANAGED_PROFILE_AVAILABLE";
    public static final String ACTION_MANAGED_PROFILE_REMOVED = "android.intent.action.MANAGED_PROFILE_REMOVED";
    public static final String ACTION_MANAGED_PROFILE_UNAVAILABLE = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE";
    public static final String ACTION_MANAGED_PROFILE_UNLOCKED = "android.intent.action.MANAGED_PROFILE_UNLOCKED";
    @SystemApi
    public static final String ACTION_MANAGE_APP_PERMISSION = "android.intent.action.MANAGE_APP_PERMISSION";
    @SystemApi
    public static final String ACTION_MANAGE_APP_PERMISSIONS = "android.intent.action.MANAGE_APP_PERMISSIONS";
    @SystemApi
    public static final String ACTION_MANAGE_DEFAULT_APP = "android.intent.action.MANAGE_DEFAULT_APP";
    public static final String ACTION_MANAGE_NETWORK_USAGE = "android.intent.action.MANAGE_NETWORK_USAGE";
    public static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE";
    @SystemApi
    public static final String ACTION_MANAGE_PERMISSIONS = "android.intent.action.MANAGE_PERMISSIONS";
    @SystemApi
    public static final String ACTION_MANAGE_PERMISSION_APPS = "android.intent.action.MANAGE_PERMISSION_APPS";
    @SystemApi
    public static final String ACTION_MANAGE_PERMISSION_USAGE = "android.intent.action.MANAGE_PERMISSION_USAGE";
    @SystemApi
    public static final String ACTION_MANAGE_SPECIAL_APP_ACCESSES = "android.intent.action.MANAGE_SPECIAL_APP_ACCESSES";
    public static final String ACTION_MANAGE_UNUSED_APPS = "android.intent.action.MANAGE_UNUSED_APPS";
    @SystemApi
    @Deprecated
    public static final String ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";
    @SystemApi
    public static final String ACTION_MASTER_CLEAR_NOTIFICATION = "android.intent.action.MASTER_CLEAR_NOTIFICATION";
    public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
    public static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";
    public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
    public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    public static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS";
    public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
    public static final String ACTION_MEDIA_RESOURCE_GRANTED = "android.intent.action.MEDIA_RESOURCE_GRANTED";
    public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
    @Deprecated
    public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
    public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
    public static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED";
    public static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE";
    public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String ACTION_MEDIA_UNSHARED = "android.intent.action.MEDIA_UNSHARED";
    public static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";
    public static final String ACTION_MY_PACKAGE_SUSPENDED = "android.intent.action.MY_PACKAGE_SUSPENDED";
    public static final String ACTION_MY_PACKAGE_UNSUSPENDED = "android.intent.action.MY_PACKAGE_UNSUSPENDED";
    @Deprecated
    public static final String ACTION_NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
    public static final String ACTION_OPEN_DOCUMENT_TREE = "android.intent.action.OPEN_DOCUMENT_TREE";
    public static final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    public static final String ACTION_PACKAGES_SUSPENDED = "android.intent.action.PACKAGES_SUSPENDED";
    public static final String ACTION_PACKAGES_SUSPENSION_CHANGED = "android.intent.action.PACKAGES_SUSPENSION_CHANGED";
    public static final String ACTION_PACKAGES_UNSUSPENDED = "android.intent.action.PACKAGES_UNSUSPENDED";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED";
    public static final String ACTION_PACKAGE_ENABLE_ROLLBACK = "android.intent.action.PACKAGE_ENABLE_ROLLBACK";
    public static final String ACTION_PACKAGE_FIRST_LAUNCH = "android.intent.action.PACKAGE_FIRST_LAUNCH";
    public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";
    @Deprecated
    public static final String ACTION_PACKAGE_INSTALL = "android.intent.action.PACKAGE_INSTALL";
    @SystemApi
    public static final String ACTION_PACKAGE_NEEDS_INTEGRITY_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_INTEGRITY_VERIFICATION";
    public static final String ACTION_PACKAGE_NEEDS_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_VERIFICATION";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_REMOVED_INTERNAL = "android.intent.action.PACKAGE_REMOVED_INTERNAL";
    public static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    public static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED";
    @SystemApi
    public static final String ACTION_PACKAGE_UNSUSPENDED_MANUALLY = "android.intent.action.PACKAGE_UNSUSPENDED_MANUALLY";
    public static final String ACTION_PACKAGE_VERIFIED = "android.intent.action.PACKAGE_VERIFIED";
    public static final String ACTION_PASTE = "android.intent.action.PASTE";
    @SystemApi
    public static final String ACTION_PENDING_INCIDENT_REPORTS_CHANGED = "android.intent.action.PENDING_INCIDENT_REPORTS_CHANGED";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";
    public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
    public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";
    public static final String ACTION_PREFERRED_ACTIVITY_CHANGED = "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED";
    @SystemApi
    public static final String ACTION_PRE_BOOT_COMPLETED = "android.intent.action.PRE_BOOT_COMPLETED";
    public static final String ACTION_PROCESS_TEXT = "android.intent.action.PROCESS_TEXT";
    public static final String ACTION_PROFILE_ACCESSIBLE = "android.intent.action.PROFILE_ACCESSIBLE";
    public static final String ACTION_PROFILE_ADDED = "android.intent.action.PROFILE_ADDED";
    public static final String ACTION_PROFILE_INACCESSIBLE = "android.intent.action.PROFILE_INACCESSIBLE";
    public static final String ACTION_PROFILE_REMOVED = "android.intent.action.PROFILE_REMOVED";
    public static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
    @SystemApi
    public static final String ACTION_QUERY_PACKAGE_RESTART = "android.intent.action.QUERY_PACKAGE_RESTART";
    public static final String ACTION_QUICK_CLOCK = "android.intent.action.QUICK_CLOCK";
    public static final String ACTION_QUICK_VIEW = "android.intent.action.QUICK_VIEW";
    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
    public static final String ACTION_REMOTE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
    public static final String ACTION_REQUEST_SHUTDOWN = "com.android.internal.intent.action.REQUEST_SHUTDOWN";
    @SystemApi
    public static final String ACTION_RESOLVE_INSTANT_APP_PACKAGE = "android.intent.action.RESOLVE_INSTANT_APP_PACKAGE";
    @SystemApi
    public static final String ACTION_REVIEW_ACCESSIBILITY_SERVICES = "android.intent.action.REVIEW_ACCESSIBILITY_SERVICES";
    @SystemApi
    public static final String ACTION_REVIEW_APP_DATA_SHARING_UPDATES = "android.intent.action.REVIEW_APP_DATA_SHARING_UPDATES";
    @SystemApi
    public static final String ACTION_REVIEW_ONGOING_PERMISSION_USAGE = "android.intent.action.REVIEW_ONGOING_PERMISSION_USAGE";
    @SystemApi
    public static final String ACTION_REVIEW_PERMISSIONS = "android.intent.action.REVIEW_PERMISSIONS";
    @SystemApi
    public static final String ACTION_REVIEW_PERMISSION_HISTORY = "android.intent.action.REVIEW_PERMISSION_HISTORY";
    @SystemApi
    public static final String ACTION_REVIEW_PERMISSION_USAGE = "android.intent.action.REVIEW_PERMISSION_USAGE";
    @SystemApi
    public static final String ACTION_ROLLBACK_COMMITTED = "android.intent.action.ROLLBACK_COMMITTED";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SAFETY_CENTER = "android.intent.action.SAFETY_CENTER";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
    @SystemApi
    @Deprecated
    public static final String ACTION_SERVICE_STATE = "android.intent.action.SERVICE_STATE";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String ACTION_SETTING_RESTORED = "android.os.action.SETTING_RESTORED";
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";
    public static final String ACTION_SHOW_APP_INFO = "android.intent.action.SHOW_APP_INFO";
    public static final String ACTION_SHOW_BRIGHTNESS_DIALOG = "com.android.intent.action.SHOW_BRIGHTNESS_DIALOG";
    public static final String ACTION_SHOW_FOREGROUND_SERVICE_MANAGER = "android.intent.action.SHOW_FOREGROUND_SERVICE_MANAGER";
    public static final String ACTION_SHOW_KEYBOARD_SHORTCUTS = "com.android.intent.action.SHOW_KEYBOARD_SHORTCUTS";
    @SystemApi
    public static final String ACTION_SHOW_SUSPENDED_APP_DETAILS = "android.intent.action.SHOW_SUSPENDED_APP_DETAILS";
    public static final String ACTION_SHOW_WORK_APPS = "android.intent.action.SHOW_WORK_APPS";
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    @SystemApi
    @Deprecated
    public static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    @SystemApi
    public static final String ACTION_SPLIT_CONFIGURATION_CHANGED = "android.intent.action.SPLIT_CONFIGURATION_CHANGED";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";
    public static final String ACTION_THERMAL_EVENT = "android.intent.action.THERMAL_EVENT";
    public static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
    public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
    public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
    public static final String ACTION_TRANSLATE = "android.intent.action.TRANSLATE";
    public static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED";
    @Deprecated
    public static final String ACTION_UMS_CONNECTED = "android.intent.action.UMS_CONNECTED";
    @Deprecated
    public static final String ACTION_UMS_DISCONNECTED = "android.intent.action.UMS_DISCONNECTED";
    @Deprecated
    public static final String ACTION_UNINSTALL_PACKAGE = "android.intent.action.UNINSTALL_PACKAGE";
    @SystemApi
    public static final String ACTION_UPGRADE_SETUP = "android.intent.action.UPGRADE_SETUP";
    @SystemApi
    public static final String ACTION_USER_ADDED = "android.intent.action.USER_ADDED";
    public static final String ACTION_USER_BACKGROUND = "android.intent.action.USER_BACKGROUND";
    public static final String ACTION_USER_FOREGROUND = "android.intent.action.USER_FOREGROUND";
    public static final String ACTION_USER_INFO_CHANGED = "android.intent.action.USER_INFO_CHANGED";
    public static final String ACTION_USER_INITIALIZE = "android.intent.action.USER_INITIALIZE";
    public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
    @SystemApi
    public static final String ACTION_USER_REMOVED = "android.intent.action.USER_REMOVED";
    public static final String ACTION_USER_STARTED = "android.intent.action.USER_STARTED";
    public static final String ACTION_USER_STARTING = "android.intent.action.USER_STARTING";
    public static final String ACTION_USER_STOPPED = "android.intent.action.USER_STOPPED";
    public static final String ACTION_USER_STOPPING = "android.intent.action.USER_STOPPING";
    @SystemApi
    public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    public static final String ACTION_USER_UNLOCKED = "android.intent.action.USER_UNLOCKED";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    @SystemApi
    public static final String ACTION_VIEW_APP_FEATURES = "android.intent.action.VIEW_APP_FEATURES";
    public static final String ACTION_VIEW_LOCUS = "android.intent.action.VIEW_LOCUS";
    public static final String ACTION_VIEW_PERMISSION_USAGE = "android.intent.action.VIEW_PERMISSION_USAGE";
    public static final String ACTION_VIEW_PERMISSION_USAGE_FOR_PERIOD = "android.intent.action.VIEW_PERMISSION_USAGE_FOR_PERIOD";
    @SystemApi
    public static final String ACTION_VIEW_SAFETY_CENTER_QS = "android.intent.action.VIEW_SAFETY_CENTER_QS";
    @SystemApi
    public static final String ACTION_VOICE_ASSIST = "android.intent.action.VOICE_ASSIST";
    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";
    @Deprecated
    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    private static final String ATTR_ACTION = "action";
    private static final String ATTR_CATEGORY = "category";
    private static final String ATTR_COMPONENT = "component";
    private static final String ATTR_DATA = "data";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_IDENTIFIER = "ident";
    private static final String ATTR_TYPE = "type";
    public static final int CAPTURE_CONTENT_FOR_NOTE_BLOCKED_BY_ADMIN = 4;
    public static final int CAPTURE_CONTENT_FOR_NOTE_FAILED = 1;
    public static final int CAPTURE_CONTENT_FOR_NOTE_SUCCESS = 0;
    public static final int CAPTURE_CONTENT_FOR_NOTE_USER_CANCELED = 2;
    public static final int CAPTURE_CONTENT_FOR_NOTE_WINDOW_MODE_UNSUPPORTED = 3;
    public static final String CATEGORY_ACCESSIBILITY_SHORTCUT_TARGET = "android.intent.category.ACCESSIBILITY_SHORTCUT_TARGET";
    public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
    public static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER";
    public static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR";
    public static final String CATEGORY_APP_CALENDAR = "android.intent.category.APP_CALENDAR";
    public static final String CATEGORY_APP_CONTACTS = "android.intent.category.APP_CONTACTS";
    public static final String CATEGORY_APP_EMAIL = "android.intent.category.APP_EMAIL";
    public static final String CATEGORY_APP_FILES = "android.intent.category.APP_FILES";
    public static final String CATEGORY_APP_FITNESS = "android.intent.category.APP_FITNESS";
    public static final String CATEGORY_APP_GALLERY = "android.intent.category.APP_GALLERY";
    public static final String CATEGORY_APP_MAPS = "android.intent.category.APP_MAPS";
    public static final String CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET";
    public static final String CATEGORY_APP_MESSAGING = "android.intent.category.APP_MESSAGING";
    public static final String CATEGORY_APP_MUSIC = "android.intent.category.APP_MUSIC";
    public static final String CATEGORY_APP_WEATHER = "android.intent.category.APP_WEATHER";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
    public static final String CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK";
    public static final String CATEGORY_CAR_LAUNCHER = "android.intent.category.CAR_LAUNCHER";
    public static final String CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE";
    public static final String CATEGORY_COMMUNAL_MODE = "android.intent.category.COMMUNAL_MODE";
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK";
    public static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE";
    public static final String CATEGORY_EMBED = "android.intent.category.EMBED";
    public static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
    public static final String CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK";
    public static final String CATEGORY_HOME = "android.intent.category.HOME";
    public static final String CATEGORY_HOME_MAIN = "android.intent.category.HOME_MAIN";
    public static final String CATEGORY_INFO = "android.intent.category.INFO";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_LAUNCHER_APP = "android.intent.category.LAUNCHER_APP";
    public static final String CATEGORY_LEANBACK_LAUNCHER = "android.intent.category.LEANBACK_LAUNCHER";
    @SystemApi
    public static final String CATEGORY_LEANBACK_SETTINGS = "android.intent.category.LEANBACK_SETTINGS";
    public static final String CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK";
    public static final String CATEGORY_MONKEY = "android.intent.category.MONKEY";
    public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
    public static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE";
    public static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE";
    public static final String CATEGORY_SECONDARY_HOME = "android.intent.category.SECONDARY_HOME";
    public static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE";
    public static final String CATEGORY_SETUP_WIZARD = "android.intent.category.SETUP_WIZARD";
    public static final String CATEGORY_TAB = "android.intent.category.TAB";
    public static final String CATEGORY_TEST = "android.intent.category.TEST";
    public static final String CATEGORY_TYPED_OPENABLE = "android.intent.category.TYPED_OPENABLE";
    public static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST";
    public static final String CATEGORY_VOICE = "android.intent.category.VOICE";
    public static final String CATEGORY_VR_HOME = "android.intent.category.VR_HOME";
    private static final int COPY_MODE_ALL = 0;
    private static final int COPY_MODE_FILTER = 1;
    private static final int COPY_MODE_HISTORY = 2;
    public static final Parcelable.Creator<Intent> CREATOR = new Parcelable.Creator<Intent>() { // from class: android.content.Intent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Intent createFromParcel(Parcel in) {
            return new Intent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };
    public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
    public static final String EXTRA_ALLOW_MULTIPLE = "android.intent.extra.ALLOW_MULTIPLE";
    @Deprecated
    public static final String EXTRA_ALLOW_REPLACE = "android.intent.extra.ALLOW_REPLACE";
    public static final String EXTRA_ALTERNATE_INTENTS = "android.intent.extra.ALTERNATE_INTENTS";
    public static final String EXTRA_ASSIST_CONTEXT = "android.intent.extra.ASSIST_CONTEXT";
    public static final String EXTRA_ASSIST_INPUT_DEVICE_ID = "android.intent.extra.ASSIST_INPUT_DEVICE_ID";
    public static final String EXTRA_ASSIST_INPUT_HINT_KEYBOARD = "android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD";
    public static final String EXTRA_ASSIST_PACKAGE = "android.intent.extra.ASSIST_PACKAGE";
    public static final String EXTRA_ASSIST_UID = "android.intent.extra.ASSIST_UID";
    public static final String EXTRA_ATTRIBUTION_TAGS = "android.intent.extra.ATTRIBUTION_TAGS";
    public static final String EXTRA_AUTO_LAUNCH_SINGLE_CHOICE = "android.intent.extra.AUTO_LAUNCH_SINGLE_CHOICE";
    public static final String EXTRA_BCC = "android.intent.extra.BCC";
    public static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT";
    @SystemApi
    public static final String EXTRA_CALLING_PACKAGE = "android.intent.extra.CALLING_PACKAGE";
    public static final String EXTRA_CAPTURE_CONTENT_FOR_NOTE_STATUS_CODE = "android.intent.extra.CAPTURE_CONTENT_FOR_NOTE_STATUS_CODE";
    public static final String EXTRA_CC = "android.intent.extra.CC";
    @SystemApi
    @Deprecated
    public static final String EXTRA_CDMA_DEFAULT_ROAMING_INDICATOR = "cdmaDefaultRoamingIndicator";
    @SystemApi
    @Deprecated
    public static final String EXTRA_CDMA_ROAMING_INDICATOR = "cdmaRoamingIndicator";
    @Deprecated
    public static final String EXTRA_CHANGED_COMPONENT_NAME = "android.intent.extra.changed_component_name";
    public static final String EXTRA_CHANGED_COMPONENT_NAME_LIST = "android.intent.extra.changed_component_name_list";
    public static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list";
    public static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list";
    public static final String EXTRA_CHOOSER_CUSTOM_ACTIONS = "android.intent.extra.CHOOSER_CUSTOM_ACTIONS";
    public static final String EXTRA_CHOOSER_MODIFY_SHARE_ACTION = "android.intent.extra.CHOOSER_MODIFY_SHARE_ACTION";
    public static final String EXTRA_CHOOSER_REFINEMENT_INTENT_SENDER = "android.intent.extra.CHOOSER_REFINEMENT_INTENT_SENDER";
    public static final String EXTRA_CHOOSER_TARGETS = "android.intent.extra.CHOOSER_TARGETS";
    public static final String EXTRA_CHOSEN_COMPONENT = "android.intent.extra.CHOSEN_COMPONENT";
    public static final String EXTRA_CHOSEN_COMPONENT_INTENT_SENDER = "android.intent.extra.CHOSEN_COMPONENT_INTENT_SENDER";
    public static final String EXTRA_CLIENT_INTENT = "android.intent.extra.client_intent";
    public static final String EXTRA_CLIENT_LABEL = "android.intent.extra.client_label";
    public static final String EXTRA_COMPONENT_NAME = "android.intent.extra.COMPONENT_NAME";
    public static final String EXTRA_CONTENT_ANNOTATIONS = "android.intent.extra.CONTENT_ANNOTATIONS";
    public static final String EXTRA_CONTENT_QUERY = "android.intent.extra.CONTENT_QUERY";
    @SystemApi
    @Deprecated
    public static final String EXTRA_CSS_INDICATOR = "cssIndicator";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_OPERATOR_ALPHA_LONG = "data-operator-alpha-long";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_OPERATOR_ALPHA_SHORT = "data-operator-alpha-short";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_OPERATOR_NUMERIC = "data-operator-numeric";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_RADIO_TECH = "dataRadioTechnology";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_REG_STATE = "dataRegState";
    public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
    @SystemApi
    @Deprecated
    public static final String EXTRA_DATA_ROAMING_TYPE = "dataRoamingType";
    public static final String EXTRA_DISTRACTION_RESTRICTIONS = "android.intent.extra.distraction_restrictions";
    public static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
    public static final int EXTRA_DOCK_STATE_CAR = 2;
    public static final int EXTRA_DOCK_STATE_DESK = 1;
    public static final int EXTRA_DOCK_STATE_HE_DESK = 4;
    public static final int EXTRA_DOCK_STATE_LE_DESK = 3;
    public static final int EXTRA_DOCK_STATE_UNDOCKED = 0;
    public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
    public static final String EXTRA_DURATION_MILLIS = "android.intent.extra.DURATION_MILLIS";
    public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
    @SystemApi
    @Deprecated
    public static final String EXTRA_EMERGENCY_ONLY = "emergencyOnly";
    public static final String EXTRA_END_TIME = "android.intent.extra.END_TIME";
    public static final String EXTRA_EXCLUDE_COMPONENTS = "android.intent.extra.EXCLUDE_COMPONENTS";
    @SystemApi
    public static final String EXTRA_FORCE_FACTORY_RESET = "android.intent.extra.FORCE_FACTORY_RESET";
    @Deprecated
    public static final String EXTRA_FORCE_MASTER_CLEAR = "android.intent.extra.FORCE_MASTER_CLEAR";
    public static final String EXTRA_FROM_STORAGE = "android.intent.extra.FROM_STORAGE";
    public static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT";
    public static final String EXTRA_INDEX = "android.intent.extra.INDEX";
    public static final String EXTRA_INITIAL_INTENTS = "android.intent.extra.INITIAL_INTENTS";
    public static final String EXTRA_INSTALLER_PACKAGE_NAME = "android.intent.extra.INSTALLER_PACKAGE_NAME";
    @SystemApi
    public static final String EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_ACTION = "android.intent.extra.INSTANT_APP_ACTION";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_BUNDLES = "android.intent.extra.INSTANT_APP_BUNDLES";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_EXTRAS = "android.intent.extra.INSTANT_APP_EXTRAS";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_FAILURE = "android.intent.extra.INSTANT_APP_FAILURE";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_HOSTNAME = "android.intent.extra.INSTANT_APP_HOSTNAME";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_SUCCESS = "android.intent.extra.INSTANT_APP_SUCCESS";
    @SystemApi
    public static final String EXTRA_INSTANT_APP_TOKEN = "android.intent.extra.INSTANT_APP_TOKEN";
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    @SystemApi
    @Deprecated
    public static final String EXTRA_IS_DATA_ROAMING_FROM_REGISTRATION = "isDataRoamingFromRegistration";
    @SystemApi
    @Deprecated
    public static final String EXTRA_IS_USING_CARRIER_AGGREGATION = "isUsingCarrierAggregation";
    public static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
    public static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
    public static final String EXTRA_LOCALE_LIST = "android.intent.extra.LOCALE_LIST";
    public static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
    public static final String EXTRA_LOCUS_ID = "android.intent.extra.LOCUS_ID";
    @SystemApi
    public static final String EXTRA_LONG_VERSION_CODE = "android.intent.extra.LONG_VERSION_CODE";
    @SystemApi
    @Deprecated
    public static final String EXTRA_LTE_EARFCN_RSRP_BOOST = "LteEarfcnRsrpBoost";
    @SystemApi
    @Deprecated
    public static final String EXTRA_MANUAL = "manual";
    public static final String EXTRA_MEDIA_RESOURCE_TYPE = "android.intent.extra.MEDIA_RESOURCE_TYPE";
    public static final int EXTRA_MEDIA_RESOURCE_TYPE_AUDIO_CODEC = 1;
    public static final int EXTRA_MEDIA_RESOURCE_TYPE_VIDEO_CODEC = 0;
    public static final String EXTRA_MIME_TYPES = "android.intent.extra.MIME_TYPES";
    @SystemApi
    @Deprecated
    public static final String EXTRA_NETWORK_ID = "networkId";
    public static final String EXTRA_NOT_UNKNOWN_SOURCE = "android.intent.extra.NOT_UNKNOWN_SOURCE";
    @SystemApi
    @Deprecated
    public static final String EXTRA_OPERATOR_ALPHA_LONG = "operator-alpha-long";
    @SystemApi
    @Deprecated
    public static final String EXTRA_OPERATOR_ALPHA_SHORT = "operator-alpha-short";
    @SystemApi
    @Deprecated
    public static final String EXTRA_OPERATOR_NUMERIC = "operator-numeric";
    @SystemApi
    public static final String EXTRA_ORIGINATING_UID = "android.intent.extra.ORIGINATING_UID";
    public static final String EXTRA_ORIGINATING_URI = "android.intent.extra.ORIGINATING_URI";
    public static final String EXTRA_PACKAGES = "android.intent.extra.PACKAGES";
    public static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME";
    public static final String EXTRA_PERMISSION_GROUP_NAME = "android.intent.extra.PERMISSION_GROUP_NAME";
    @SystemApi
    public static final String EXTRA_PERMISSION_NAME = "android.intent.extra.PERMISSION_NAME";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
    public static final String EXTRA_PROCESS_TEXT = "android.intent.extra.PROCESS_TEXT";
    public static final String EXTRA_PROCESS_TEXT_READONLY = "android.intent.extra.PROCESS_TEXT_READONLY";
    @Deprecated
    public static final String EXTRA_QUICK_VIEW_ADVANCED = "android.intent.extra.QUICK_VIEW_ADVANCED";
    public static final String EXTRA_QUICK_VIEW_FEATURES = "android.intent.extra.QUICK_VIEW_FEATURES";
    public static final String EXTRA_QUIET_MODE = "android.intent.extra.QUIET_MODE";
    @SystemApi
    public static final String EXTRA_REASON = "android.intent.extra.REASON";
    public static final String EXTRA_REBROADCAST_ON_UNLOCK = "rebroadcastOnUnlock";
    public static final String EXTRA_REFERRER = "android.intent.extra.REFERRER";
    public static final String EXTRA_REFERRER_NAME = "android.intent.extra.REFERRER_NAME";
    @SystemApi
    public static final String EXTRA_REMOTE_CALLBACK = "android.intent.extra.REMOTE_CALLBACK";
    public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
    public static final String EXTRA_REMOVED_FOR_ALL_USERS = "android.intent.extra.REMOVED_FOR_ALL_USERS";
    public static final String EXTRA_REPLACEMENT_EXTRAS = "android.intent.extra.REPLACEMENT_EXTRAS";
    public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
    public static final String EXTRA_RESTRICTIONS_BUNDLE = "android.intent.extra.restrictions_bundle";
    public static final String EXTRA_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent";
    public static final String EXTRA_RESTRICTIONS_LIST = "android.intent.extra.restrictions_list";
    @SystemApi
    public static final String EXTRA_RESULT_NEEDED = "android.intent.extra.RESULT_NEEDED";
    public static final String EXTRA_RESULT_RECEIVER = "android.intent.extra.RESULT_RECEIVER";
    public static final String EXTRA_RETURN_RESULT = "android.intent.extra.RETURN_RESULT";
    @SystemApi
    public static final String EXTRA_ROLE_NAME = "android.intent.extra.ROLE_NAME";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String EXTRA_SETTING_NAME = "setting_name";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String EXTRA_SETTING_NEW_VALUE = "new_value";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String EXTRA_SETTING_PREVIOUS_VALUE = "previous_value";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String EXTRA_SETTING_RESTORED_FROM_SDK_INT = "restored_from_sdk_int";
    @Deprecated
    public static final String EXTRA_SHORTCUT_ICON = "android.intent.extra.shortcut.ICON";
    @Deprecated
    public static final String EXTRA_SHORTCUT_ICON_RESOURCE = "android.intent.extra.shortcut.ICON_RESOURCE";
    public static final String EXTRA_SHORTCUT_ID = "android.intent.extra.shortcut.ID";
    @Deprecated
    public static final String EXTRA_SHORTCUT_INTENT = "android.intent.extra.shortcut.INTENT";
    @Deprecated
    public static final String EXTRA_SHORTCUT_NAME = "android.intent.extra.shortcut.NAME";
    @SystemApi
    public static final String EXTRA_SHOWING_ATTRIBUTION = "android.intent.extra.SHOWING_ATTRIBUTION";
    public static final String EXTRA_SHUTDOWN_USERSPACE_ONLY = "android.intent.extra.SHUTDOWN_USERSPACE_ONLY";
    public static final String EXTRA_SIM_ACTIVATION_RESPONSE = "android.intent.extra.SIM_ACTIVATION_RESPONSE";
    public static final String EXTRA_SIM_LOCKED_REASON = "reason";
    public static final String EXTRA_SIM_STATE = "ss";
    public static final String EXTRA_SPLIT_NAME = "android.intent.extra.SPLIT_NAME";
    public static final String EXTRA_START_TIME = "android.intent.extra.START_TIME";
    public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
    public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
    public static final String EXTRA_SUSPENDED_PACKAGE_EXTRAS = "android.intent.extra.SUSPENDED_PACKAGE_EXTRAS";
    @SystemApi
    @Deprecated
    public static final String EXTRA_SYSTEM_ID = "systemId";
    public static final String EXTRA_SYSTEM_UPDATE_UNINSTALL = "android.intent.extra.SYSTEM_UPDATE_UNINSTALL";
    public static final String EXTRA_TASK_ID = "android.intent.extra.TASK_ID";
    public static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE";
    public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
    public static final String EXTRA_THERMAL_STATE = "android.intent.extra.THERMAL_STATE";
    public static final int EXTRA_THERMAL_STATE_EXCEEDED = 2;
    public static final int EXTRA_THERMAL_STATE_NORMAL = 0;
    public static final int EXTRA_THERMAL_STATE_WARNING = 1;
    public static final String EXTRA_TIME = "android.intent.extra.TIME";
    public static final String EXTRA_TIMEZONE = "time-zone";
    public static final String EXTRA_TIME_PREF_24_HOUR_FORMAT = "android.intent.extra.TIME_PREF_24_HOUR_FORMAT";
    public static final int EXTRA_TIME_PREF_VALUE_USE_12_HOUR = 0;
    public static final int EXTRA_TIME_PREF_VALUE_USE_24_HOUR = 1;
    public static final int EXTRA_TIME_PREF_VALUE_USE_LOCALE_DEFAULT = 2;
    public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
    public static final String EXTRA_UID = "android.intent.extra.UID";
    @SystemApi
    public static final String EXTRA_UNINSTALL_ALL_USERS = "android.intent.extra.UNINSTALL_ALL_USERS";
    @SystemApi
    public static final String EXTRA_UNKNOWN_INSTANT_APP = "android.intent.extra.UNKNOWN_INSTANT_APP";
    public static final String EXTRA_USER = "android.intent.extra.USER";
    @SystemApi
    public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
    public static final String EXTRA_USER_ID = "android.intent.extra.USER_ID";
    public static final String EXTRA_USER_INITIATED = "android.intent.extra.USER_INITIATED";
    public static final String EXTRA_USER_REQUESTED_SHUTDOWN = "android.intent.extra.USER_REQUESTED_SHUTDOWN";
    public static final String EXTRA_USE_STYLUS_MODE = "android.intent.extra.USE_STYLUS_MODE";
    @SystemApi
    public static final String EXTRA_VERIFICATION_BUNDLE = "android.intent.extra.VERIFICATION_BUNDLE";
    @Deprecated
    public static final String EXTRA_VERSION_CODE = "android.intent.extra.VERSION_CODE";
    public static final String EXTRA_VISIBILITY_ALLOW_LIST = "android.intent.extra.VISIBILITY_ALLOW_LIST";
    @SystemApi
    @Deprecated
    public static final String EXTRA_VOICE_RADIO_TECH = "radioTechnology";
    @SystemApi
    @Deprecated
    public static final String EXTRA_VOICE_REG_STATE = "voiceRegState";
    @SystemApi
    @Deprecated
    public static final String EXTRA_VOICE_ROAMING_TYPE = "voiceRoamingType";
    public static final String EXTRA_WIPE_ESIMS = "com.android.internal.intent.extra.WIPE_ESIMS";
    public static final String EXTRA_WIPE_EXTERNAL_STORAGE = "android.intent.extra.WIPE_EXTERNAL_STORAGE";
    public static final int FILL_IN_ACTION = 1;
    public static final int FILL_IN_CATEGORIES = 4;
    public static final int FILL_IN_CLIP_DATA = 128;
    public static final int FILL_IN_COMPONENT = 8;
    public static final int FILL_IN_DATA = 2;
    public static final int FILL_IN_IDENTIFIER = 256;
    public static final int FILL_IN_PACKAGE = 16;
    public static final int FILL_IN_SELECTOR = 64;
    public static final int FILL_IN_SOURCE_BOUNDS = 32;
    public static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304;
    public static final int FLAG_ACTIVITY_CLEAR_TASK = 32768;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864;
    @Deprecated
    public static final int FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET = 524288;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608;
    public static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432;
    public static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576;
    public static final int FLAG_ACTIVITY_LAUNCH_ADJACENT = 4096;
    public static final int FLAG_ACTIVITY_MATCH_EXTERNAL = 2048;
    public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728;
    public static final int FLAG_ACTIVITY_NEW_DOCUMENT = 524288;
    public static final int FLAG_ACTIVITY_NEW_TASK = 268435456;
    public static final int FLAG_ACTIVITY_NO_ANIMATION = 65536;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824;
    public static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144;
    public static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216;
    public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072;
    public static final int FLAG_ACTIVITY_REQUIRE_DEFAULT = 512;
    public static final int FLAG_ACTIVITY_REQUIRE_NON_BROWSER = 1024;
    public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152;
    public static final int FLAG_ACTIVITY_RETAIN_IN_RECENTS = 8192;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912;
    public static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384;
    public static final int FLAG_DEBUG_LOG_RESOLUTION = 8;
    @Deprecated
    public static final int FLAG_DEBUG_TRIAGED_MISSING = 256;
    public static final int FLAG_DIRECT_BOOT_AUTO = 256;
    public static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16;
    public static final int FLAG_FROM_BACKGROUND = 4;
    public static final int FLAG_GRANT_PERSISTABLE_URI_PERMISSION = 64;
    public static final int FLAG_GRANT_PREFIX_URI_PERMISSION = 128;
    public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
    public static final int FLAG_IGNORE_EPHEMERAL = 512;
    public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
    public static final int FLAG_RECEIVER_BOOT_UPGRADE = 33554432;
    public static final int FLAG_RECEIVER_EXCLUDE_BACKGROUND = 8388608;
    public static final int FLAG_RECEIVER_FOREGROUND = 268435456;
    public static final int FLAG_RECEIVER_FROM_SHELL = 4194304;
    @SystemApi
    public static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 16777216;
    public static final int FLAG_RECEIVER_NO_ABORT = 134217728;
    public static final int FLAG_RECEIVER_OFFLOAD = Integer.MIN_VALUE;
    public static final int FLAG_RECEIVER_OFFLOAD_FOREGROUND = 2048;
    public static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824;
    @SystemApi
    public static final int FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT = 67108864;
    public static final int FLAG_RECEIVER_REPLACE_PENDING = 536870912;
    public static final int FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS = 2097152;
    public static final int IMMUTABLE_FLAGS = 195;
    private static final int LOCAL_FLAG_FROM_COPY = 1;
    private static final int LOCAL_FLAG_FROM_PARCEL = 2;
    private static final int LOCAL_FLAG_FROM_PROTECTED_COMPONENT = 4;
    public static final int LOCAL_FLAG_FROM_SYSTEM = 32;
    private static final int LOCAL_FLAG_FROM_URI = 16;
    private static final int LOCAL_FLAG_UNFILTERED_EXTRAS = 8;
    public static final String METADATA_DOCK_HOME = "android.dock_home";
    @SystemApi
    public static final String METADATA_SETUP_VERSION = "android.SETUP_VERSION";
    public static final String SIM_ABSENT_ON_PERM_DISABLED = "PERM_DISABLED";
    public static final String SIM_LOCKED_NETWORK = "NETWORK";
    public static final String SIM_LOCKED_ON_PIN = "PIN";
    public static final String SIM_LOCKED_ON_PUK = "PUK";
    public static final String SIM_STATE_ABSENT = "ABSENT";
    public static final String SIM_STATE_CARD_IO_ERROR = "CARD_IO_ERROR";
    public static final String SIM_STATE_CARD_RESTRICTED = "CARD_RESTRICTED";
    public static final String SIM_STATE_IMSI = "IMSI";
    public static final String SIM_STATE_LOADED = "LOADED";
    public static final String SIM_STATE_LOCKED = "LOCKED";
    public static final String SIM_STATE_NOT_READY = "NOT_READY";
    public static final String SIM_STATE_PRESENT = "PRESENT";
    public static final String SIM_STATE_READY = "READY";
    public static final String SIM_STATE_UNKNOWN = "UNKNOWN";
    private static final String TAG = "Intent";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_EXTRA = "extra";
    public static final int URI_ALLOW_UNSAFE = 4;
    public static final int URI_ANDROID_APP_SCHEME = 2;
    public static final int URI_INTENT_SCHEME = 1;
    private String mAction;
    private ArraySet<String> mCategories;
    private ClipData mClipData;
    private ComponentName mComponent;
    private int mContentUserHint;
    private Uri mData;
    private Bundle mExtras;
    private int mFlags;
    private String mIdentifier;
    private String mLaunchToken;
    private int mLocalFlags;
    private Intent mOriginalIntent;
    private String mPackage;
    private Intent mSelector;
    private Rect mSourceBounds;
    private String mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AccessUriMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CaptureContentForNoteStatusCodes {
    }

    /* loaded from: classes.dex */
    public interface CommandOptionHandler {
        boolean handleOption(String str, ShellCommand shellCommand);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CopyMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FillInFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface GrantUriMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MutableFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UriFlags {
    }

    /* loaded from: classes.dex */
    public static class ShortcutIconResource implements Parcelable {
        public static final Parcelable.Creator<ShortcutIconResource> CREATOR = new Parcelable.Creator<ShortcutIconResource>() { // from class: android.content.Intent.ShortcutIconResource.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ShortcutIconResource createFromParcel(Parcel source) {
                ShortcutIconResource icon = new ShortcutIconResource();
                icon.packageName = source.readString8();
                icon.resourceName = source.readString8();
                return icon;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ShortcutIconResource[] newArray(int size) {
                return new ShortcutIconResource[size];
            }
        };
        public String packageName;
        public String resourceName;

        public static ShortcutIconResource fromContext(Context context, int resourceId) {
            ShortcutIconResource icon = new ShortcutIconResource();
            icon.packageName = context.getPackageName();
            icon.resourceName = context.getResources().getResourceName(resourceId);
            return icon;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString8(this.packageName);
            dest.writeString8(this.resourceName);
        }

        public String toString() {
            return this.resourceName;
        }
    }

    public static Intent createChooser(Intent target, CharSequence title) {
        return createChooser(target, title, null);
    }

    public static Intent createChooser(Intent target, CharSequence title, IntentSender sender) {
        String[] mimeTypes;
        Intent intent = new Intent(ACTION_CHOOSER);
        intent.putExtra(EXTRA_INTENT, target);
        if (title != null) {
            intent.putExtra(EXTRA_TITLE, title);
        }
        if (sender != null) {
            intent.putExtra(EXTRA_CHOSEN_COMPONENT_INTENT_SENDER, sender);
        }
        int permFlags = target.getFlags() & 195;
        if (permFlags != 0) {
            ClipData targetClipData = target.getClipData();
            if (targetClipData == null && target.getData() != null) {
                ClipData.Item item = new ClipData.Item(target.getData());
                if (target.getType() != null) {
                    mimeTypes = new String[]{target.getType()};
                } else {
                    mimeTypes = new String[0];
                }
                targetClipData = new ClipData(null, mimeTypes, item);
            }
            if (targetClipData != null) {
                intent.setClipData(targetClipData);
                intent.addFlags(permFlags);
            }
        }
        return intent;
    }

    public static boolean isAccessUriMode(int modeFlags) {
        return (modeFlags & 3) != 0;
    }

    public Intent() {
        this.mContentUserHint = -2;
    }

    public Intent(Intent o) {
        this(o, 0);
    }

    private Intent(Intent o, int copyMode) {
        this.mContentUserHint = -2;
        this.mAction = o.mAction;
        this.mData = o.mData;
        this.mType = o.mType;
        this.mIdentifier = o.mIdentifier;
        this.mPackage = o.mPackage;
        this.mComponent = o.mComponent;
        this.mOriginalIntent = o.mOriginalIntent;
        if (o.mCategories != null) {
            this.mCategories = new ArraySet<>(o.mCategories);
        }
        int i = o.mLocalFlags;
        this.mLocalFlags = i;
        this.mLocalFlags = i | 1;
        if (copyMode != 1) {
            this.mFlags = o.mFlags;
            this.mContentUserHint = o.mContentUserHint;
            this.mLaunchToken = o.mLaunchToken;
            if (o.mSourceBounds != null) {
                this.mSourceBounds = new Rect(o.mSourceBounds);
            }
            Intent intent = o.mSelector;
            if (intent != null) {
                this.mSelector = new Intent(intent);
            }
            if (copyMode != 2) {
                if (o.mExtras != null) {
                    this.mExtras = new Bundle(o.mExtras);
                }
                if (o.mClipData != null) {
                    this.mClipData = new ClipData(o.mClipData);
                    return;
                }
                return;
            }
            Bundle bundle = o.mExtras;
            if (bundle != null && !bundle.isDefinitelyEmpty()) {
                this.mExtras = Bundle.STRIPPED;
            }
        }
    }

    public Object clone() {
        return new Intent(this);
    }

    public Intent cloneFilter() {
        return new Intent(this, 1);
    }

    public Intent(String action) {
        this.mContentUserHint = -2;
        setAction(action);
    }

    public Intent(String action, Uri uri) {
        this.mContentUserHint = -2;
        setAction(action);
        this.mData = uri;
    }

    public Intent(Context packageContext, Class<?> cls) {
        this.mContentUserHint = -2;
        this.mComponent = new ComponentName(packageContext, cls);
    }

    public Intent(String action, Uri uri, Context packageContext, Class<?> cls) {
        this.mContentUserHint = -2;
        setAction(action);
        this.mData = uri;
        this.mComponent = new ComponentName(packageContext, cls);
    }

    public static Intent makeMainActivity(ComponentName mainActivity) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.setComponent(mainActivity);
        intent.addCategory(CATEGORY_LAUNCHER);
        return intent;
    }

    public static Intent makeMainSelectorActivity(String selectorAction, String selectorCategory) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.addCategory(CATEGORY_LAUNCHER);
        Intent selector = new Intent();
        selector.setAction(selectorAction);
        selector.addCategory(selectorCategory);
        intent.setSelector(selector);
        return intent;
    }

    public static Intent makeRestartActivityTask(ComponentName mainActivity) {
        Intent intent = makeMainActivity(mainActivity);
        intent.addFlags(268468224);
        return intent;
    }

    @Deprecated
    public static Intent getIntent(String uri) throws URISyntaxException {
        return parseUri(uri, 0);
    }

    public static Intent parseUri(String uri, int flags) throws URISyntaxException {
        Intent intent = parseUriInternal(uri, flags);
        intent.mLocalFlags |= 16;
        return intent;
    }

    private static Intent parseUriInternal(String uri, int flags) throws URISyntaxException {
        String data;
        Intent intent;
        String data2;
        int newEnd;
        boolean androidApp;
        Intent intent2;
        int i = 0;
        try {
            boolean androidApp2 = uri.startsWith("android-app:");
            if ((flags & 3) == 0 || uri.startsWith("intent:") || androidApp2) {
                i = uri.lastIndexOf("#");
                if (i != -1) {
                    if (!uri.startsWith("#Intent;", i)) {
                        if (!androidApp2) {
                            return getIntentOld(uri, flags);
                        }
                        i = -1;
                    }
                } else if (!androidApp2) {
                    return new Intent("android.intent.action.VIEW", Uri.parse(uri));
                }
                Intent intent3 = new Intent("android.intent.action.VIEW");
                boolean explicitAction = false;
                boolean inSelector = false;
                String scheme = null;
                if (i >= 0) {
                    data = uri.substring(0, i);
                    i += 8;
                } else {
                    data = uri;
                }
                while (true) {
                    if (i < 0 || uri.startsWith("end", i)) {
                        break;
                    }
                    int eq = uri.indexOf(61, i);
                    if (eq < 0) {
                        eq = i - 1;
                    }
                    int semi = uri.indexOf(59, i);
                    String value = eq < semi ? Uri.decode(uri.substring(eq + 1, semi)) : "";
                    if (!uri.startsWith("action=", i)) {
                        if (!uri.startsWith("category=", i)) {
                            if (!uri.startsWith("type=", i)) {
                                if (!uri.startsWith("identifier=", i)) {
                                    if (!uri.startsWith("launchFlags=", i)) {
                                        if (!uri.startsWith("package=", i)) {
                                            if (!uri.startsWith("component=", i)) {
                                                if (!uri.startsWith("scheme=", i)) {
                                                    if (uri.startsWith("sourceBounds=", i)) {
                                                        intent3.mSourceBounds = Rect.unflattenFromString(value);
                                                        androidApp = androidApp2;
                                                        intent2 = intent3;
                                                    } else if (semi != i + 3 || !uri.startsWith("SEL", i)) {
                                                        String key = Uri.decode(uri.substring(i + 2, eq));
                                                        if (intent3.mExtras == null) {
                                                            intent3.mExtras = new Bundle();
                                                        }
                                                        Bundle b = intent3.mExtras;
                                                        androidApp = androidApp2;
                                                        if (uri.startsWith("S.", i)) {
                                                            b.putString(key, value);
                                                            intent2 = intent3;
                                                        } else if (uri.startsWith("B.", i)) {
                                                            b.putBoolean(key, Boolean.parseBoolean(value));
                                                            intent2 = intent3;
                                                        } else if (uri.startsWith("b.", i)) {
                                                            b.putByte(key, Byte.parseByte(value));
                                                            intent2 = intent3;
                                                        } else if (uri.startsWith("c.", i)) {
                                                            b.putChar(key, value.charAt(0));
                                                            intent2 = intent3;
                                                        } else if (uri.startsWith("d.", i)) {
                                                            intent2 = intent3;
                                                            b.putDouble(key, Double.parseDouble(value));
                                                        } else {
                                                            intent2 = intent3;
                                                            if (uri.startsWith("f.", i)) {
                                                                b.putFloat(key, Float.parseFloat(value));
                                                            } else if (uri.startsWith("i.", i)) {
                                                                b.putInt(key, Integer.parseInt(value));
                                                            } else if (uri.startsWith("l.", i)) {
                                                                b.putLong(key, Long.parseLong(value));
                                                            } else if (!uri.startsWith("s.", i)) {
                                                                throw new URISyntaxException(uri, "unknown EXTRA type", i);
                                                            } else {
                                                                b.putShort(key, Short.parseShort(value));
                                                            }
                                                        }
                                                    } else {
                                                        intent3 = new Intent();
                                                        inSelector = true;
                                                        androidApp = androidApp2;
                                                    }
                                                } else if (inSelector) {
                                                    intent3.mData = Uri.parse(value + ":");
                                                    androidApp = androidApp2;
                                                    intent2 = intent3;
                                                } else {
                                                    scheme = value;
                                                    androidApp = androidApp2;
                                                }
                                            } else {
                                                intent3.mComponent = ComponentName.unflattenFromString(value);
                                                androidApp = androidApp2;
                                                intent2 = intent3;
                                            }
                                        } else {
                                            intent3.mPackage = value;
                                            androidApp = androidApp2;
                                            intent2 = intent3;
                                        }
                                    } else {
                                        int intValue = Integer.decode(value).intValue();
                                        intent3.mFlags = intValue;
                                        if ((flags & 4) != 0) {
                                            androidApp = androidApp2;
                                            intent2 = intent3;
                                        } else {
                                            intent3.mFlags = intValue & (-196);
                                            androidApp = androidApp2;
                                            intent2 = intent3;
                                        }
                                    }
                                } else {
                                    intent3.mIdentifier = value;
                                    androidApp = androidApp2;
                                    intent2 = intent3;
                                }
                            } else {
                                intent3.mType = value;
                                androidApp = androidApp2;
                                intent2 = intent3;
                            }
                        } else {
                            intent3.addCategory(value);
                            androidApp = androidApp2;
                            intent2 = intent3;
                        }
                        intent3 = intent2;
                    } else {
                        intent3.setAction(value);
                        if (inSelector) {
                            androidApp = androidApp2;
                            intent2 = intent3;
                            intent3 = intent2;
                        } else {
                            explicitAction = true;
                            androidApp = androidApp2;
                        }
                    }
                    i = semi + 1;
                    androidApp2 = androidApp;
                }
                Intent intent4 = intent3;
                if (!inSelector) {
                    intent = intent4;
                } else {
                    if (intent3.mPackage == null) {
                        intent3.setSelector(intent4);
                    }
                    intent = intent3;
                }
                if (data != null) {
                    if (data.startsWith("intent:")) {
                        String data3 = data.substring(7);
                        if (scheme == null) {
                            data = data3;
                        } else {
                            data = scheme + ShortcutConstants.SERVICES_SEPARATOR + data3;
                        }
                    } else if (data.startsWith("android-app:")) {
                        if (data.charAt(12) == '/' && data.charAt(13) == '/') {
                            int end = data.indexOf(47, 14);
                            if (end < 0) {
                                intent.mPackage = data.substring(14);
                                if (!explicitAction) {
                                    intent.setAction(ACTION_MAIN);
                                }
                                data2 = "";
                            } else {
                                String authority = null;
                                intent.mPackage = data.substring(14, end);
                                if (end + 1 < data.length()) {
                                    int newEnd2 = data.indexOf(47, end + 1);
                                    if (newEnd2 >= 0) {
                                        scheme = data.substring(end + 1, newEnd2);
                                        end = newEnd2;
                                        if (end < data.length() && (newEnd = data.indexOf(47, end + 1)) >= 0) {
                                            authority = data.substring(end + 1, newEnd);
                                            end = newEnd;
                                        }
                                    } else {
                                        scheme = data.substring(end + 1);
                                    }
                                }
                                if (scheme == null) {
                                    if (!explicitAction) {
                                        intent.setAction(ACTION_MAIN);
                                    }
                                    data2 = "";
                                } else if (authority == null) {
                                    data2 = scheme + ":";
                                } else {
                                    data2 = scheme + "://" + authority + data.substring(end);
                                }
                            }
                            data = data2;
                        } else {
                            data = "";
                        }
                    }
                    if (data.length() > 0) {
                        try {
                            intent.mData = Uri.parse(data);
                        } catch (IllegalArgumentException e) {
                            throw new URISyntaxException(uri, e.getMessage());
                        }
                    }
                }
                return intent;
            }
            Intent intent5 = new Intent("android.intent.action.VIEW");
            try {
                intent5.setData(Uri.parse(uri));
                return intent5;
            } catch (IllegalArgumentException e2) {
                throw new URISyntaxException(uri, e2.getMessage());
            }
        } catch (IndexOutOfBoundsException e3) {
            throw new URISyntaxException(uri, "illegal Intent URI format", i);
        }
        throw new URISyntaxException(uri, "illegal Intent URI format", i);
    }

    public static Intent getIntentOld(String uri) throws URISyntaxException {
        Intent intent = getIntentOld(uri, 0);
        intent.mLocalFlags |= 16;
        return intent;
    }

    private static Intent getIntentOld(String uri, int flags) throws URISyntaxException {
        int i;
        int i2 = uri.lastIndexOf(35);
        if (i2 >= 0) {
            String action = null;
            boolean isIntentFragment = false;
            int i3 = i2 + 1;
            if (uri.regionMatches(i3, "action(", 0, 7)) {
                isIntentFragment = true;
                int i4 = i3 + 7;
                int j = uri.indexOf(41, i4);
                action = uri.substring(i4, j);
                i3 = j + 1;
            }
            Intent intent = new Intent(action);
            int i5 = 33;
            if (uri.regionMatches(i3, "categories(", 0, 11)) {
                isIntentFragment = true;
                int i6 = i3 + 11;
                int j2 = uri.indexOf(41, i6);
                while (i6 < j2) {
                    int sep = uri.indexOf(33, i6);
                    if (sep < 0 || sep > j2) {
                        sep = j2;
                    }
                    if (i6 < sep) {
                        intent.addCategory(uri.substring(i6, sep));
                    }
                    i6 = sep + 1;
                }
                i3 = j2 + 1;
            }
            if (uri.regionMatches(i3, "type(", 0, 5)) {
                isIntentFragment = true;
                int i7 = i3 + 5;
                int j3 = uri.indexOf(41, i7);
                intent.mType = uri.substring(i7, j3);
                i3 = j3 + 1;
            }
            if (uri.regionMatches(i3, "launchFlags(", 0, 12)) {
                isIntentFragment = true;
                int i8 = i3 + 12;
                int j4 = uri.indexOf(41, i8);
                int intValue = Integer.decode(uri.substring(i8, j4)).intValue();
                intent.mFlags = intValue;
                if ((flags & 4) == 0) {
                    intent.mFlags = intValue & (-196);
                }
                i3 = j4 + 1;
            }
            if (uri.regionMatches(i3, "component(", 0, 10)) {
                isIntentFragment = true;
                int i9 = i3 + 10;
                int j5 = uri.indexOf(41, i9);
                int sep2 = uri.indexOf(33, i9);
                if (sep2 >= 0 && sep2 < j5) {
                    String pkg = uri.substring(i9, sep2);
                    String cls = uri.substring(sep2 + 1, j5);
                    intent.mComponent = new ComponentName(pkg, cls);
                }
                i3 = j5 + 1;
            }
            if (uri.regionMatches(i3, "extras(", 0, 7)) {
                isIntentFragment = true;
                int i10 = i3 + 7;
                int closeParen = uri.indexOf(41, i10);
                int i11 = -1;
                if (closeParen == -1) {
                    throw new URISyntaxException(uri, "EXTRA missing trailing ')'", i10);
                }
                while (i10 < closeParen) {
                    int j6 = uri.indexOf(61, i10);
                    if (j6 <= i10 + 1 || i10 >= closeParen) {
                        throw new URISyntaxException(uri, "EXTRA missing '='", i10);
                    }
                    char type = uri.charAt(i10);
                    String key = uri.substring(i10 + 1, j6);
                    int i12 = j6 + 1;
                    int j7 = uri.indexOf(i5, i12);
                    if (j7 == i11 || j7 >= closeParen) {
                        j7 = closeParen;
                    }
                    if (i12 >= j7) {
                        throw new URISyntaxException(uri, "EXTRA missing '!'", i12);
                    }
                    String value = uri.substring(i12, j7);
                    int i13 = j7;
                    if (intent.mExtras == null) {
                        intent.mExtras = new Bundle();
                    }
                    switch (type) {
                        case 'B':
                            try {
                                intent.mExtras.putBoolean(key, Boolean.parseBoolean(value));
                                break;
                            } catch (NumberFormatException e) {
                                i = i13;
                                throw new URISyntaxException(uri, "EXTRA value can't be parsed", i);
                            }
                        case 'S':
                            intent.mExtras.putString(key, Uri.decode(value));
                            break;
                        case 'b':
                            intent.mExtras.putByte(key, Byte.parseByte(value));
                            break;
                        case 'c':
                            intent.mExtras.putChar(key, Uri.decode(value).charAt(0));
                            break;
                        case 'd':
                            intent.mExtras.putDouble(key, Double.parseDouble(value));
                            break;
                        case 'f':
                            intent.mExtras.putFloat(key, Float.parseFloat(value));
                            break;
                        case 'i':
                            intent.mExtras.putInt(key, Integer.parseInt(value));
                            break;
                        case 'l':
                            try {
                                try {
                                    intent.mExtras.putLong(key, Long.parseLong(value));
                                    break;
                                } catch (NumberFormatException e2) {
                                    i = i13;
                                    throw new URISyntaxException(uri, "EXTRA value can't be parsed", i);
                                }
                            } catch (NumberFormatException e3) {
                                i = i13;
                            }
                        case 's':
                            try {
                                intent.mExtras.putShort(key, Short.parseShort(value));
                                break;
                            } catch (NumberFormatException e4) {
                                i = i13;
                                throw new URISyntaxException(uri, "EXTRA value can't be parsed", i);
                            }
                        default:
                            i = i13;
                            try {
                                throw new URISyntaxException(uri, "EXTRA has unknown type", i);
                            } catch (NumberFormatException e5) {
                                throw new URISyntaxException(uri, "EXTRA value can't be parsed", i);
                            }
                    }
                    char ch = uri.charAt(i13);
                    if (ch != ')') {
                        if (ch != '!') {
                            throw new URISyntaxException(uri, "EXTRA missing '!'", i13);
                        }
                        i10 = i13 + 1;
                        i5 = 33;
                        i11 = -1;
                    }
                }
            }
            if (isIntentFragment) {
                intent.mData = Uri.parse(uri.substring(0, i2));
            } else {
                intent.mData = Uri.parse(uri);
            }
            if (intent.mAction == null) {
                intent.mAction = "android.intent.action.VIEW";
                return intent;
            }
            return intent;
        }
        return new Intent("android.intent.action.VIEW", Uri.parse(uri));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:330:0x072d, code lost:
        throw new java.lang.IllegalArgumentException("Unknown option: " + r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0102, code lost:
        if (r0.equals("--activity-single-top") != false) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Intent parseCommandArgs(ShellCommand cmd, CommandOptionHandler optionHandler) throws URISyntaxException {
        boolean arg;
        Intent intent = new Intent();
        String type = null;
        Uri data = null;
        boolean hasIntentInfo = false;
        Intent intent2 = intent;
        while (true) {
            String opt = cmd.getNextOption();
            char c = '/';
            if (opt != null) {
                switch (opt.hashCode()) {
                    case -2147394086:
                        if (opt.equals("--grant-prefix-uri-permission")) {
                            c = 31;
                            break;
                        }
                        c = 65535;
                        break;
                    case -2118172637:
                        if (opt.equals("--activity-task-on-home")) {
                            c = '1';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1630559130:
                        if (opt.equals("--activity-no-history")) {
                            c = '*';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1252939549:
                        if (opt.equals("--activity-clear-task")) {
                            c = '0';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1069446353:
                        if (opt.equals("--debug-log-resolution")) {
                            c = '\"';
                            break;
                        }
                        c = 65535;
                        break;
                    case -848214457:
                        if (opt.equals("--activity-reorder-to-front")) {
                            c = '-';
                            break;
                        }
                        c = 65535;
                        break;
                    case -833172539:
                        if (opt.equals("--activity-brought-to-front")) {
                            c = '#';
                            break;
                        }
                        c = 65535;
                        break;
                    case -792169302:
                        if (opt.equals("--activity-previous-is-top")) {
                            c = ',';
                            break;
                        }
                        c = 65535;
                        break;
                    case -780160399:
                        if (opt.equals("--receiver-include-background")) {
                            c = '7';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1492:
                        if (opt.equals("-a")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1494:
                        if (opt.equals("-c")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1495:
                        if (opt.equals("-d")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1496:
                        if (opt.equals("-e")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1497:
                        if (opt.equals("-f")) {
                            c = 27;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1500:
                        if (opt.equals("-i")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1505:
                        if (opt.equals("-n")) {
                            c = 25;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1507:
                        if (opt.equals("-p")) {
                            c = 26;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1511:
                        if (opt.equals("-t")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387071:
                        if (opt.equals("--ed")) {
                            c = 19;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387073:
                        if (opt.equals("--ef")) {
                            c = 16;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387076:
                        if (opt.equals("--ei")) {
                            c = '\b';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387079:
                        if (opt.equals("--el")) {
                            c = '\r';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387086:
                        if (opt.equals("--es")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387088:
                        if (opt.equals("--eu")) {
                            c = '\t';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1387093:
                        if (opt.equals("--ez")) {
                            c = 24;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999280:
                        if (opt.equals("--ecn")) {
                            c = '\n';
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999298:
                        if (opt.equals("--eda")) {
                            c = 20;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999360:
                        if (opt.equals("--efa")) {
                            c = 17;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999453:
                        if (opt.equals("--eia")) {
                            c = 11;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999546:
                        if (opt.equals("--ela")) {
                            c = 14;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999763:
                        if (opt.equals("--esa")) {
                            c = 22;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42999776:
                        if (opt.equals("--esn")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    case 69120454:
                        if (opt.equals("--activity-exclude-from-recents")) {
                            c = '&';
                            break;
                        }
                        c = 65535;
                        break;
                    case 88747734:
                        if (opt.equals("--activity-no-animation")) {
                            c = ')';
                            break;
                        }
                        c = 65535;
                        break;
                    case 190913209:
                        if (opt.equals("--activity-reset-task-if-needed")) {
                            c = '.';
                            break;
                        }
                        c = 65535;
                        break;
                    case 236677687:
                        if (opt.equals("--activity-clear-top")) {
                            c = '$';
                            break;
                        }
                        c = 65535;
                        break;
                    case 429439306:
                        if (opt.equals("--activity-no-user-action")) {
                            c = '+';
                            break;
                        }
                        c = 65535;
                        break;
                    case 436286937:
                        if (opt.equals("--receiver-registered-only")) {
                            c = '3';
                            break;
                        }
                        c = 65535;
                        break;
                    case 438531630:
                        break;
                    case 527014976:
                        if (opt.equals("--grant-persistable-uri-permission")) {
                            c = 30;
                            break;
                        }
                        c = 65535;
                        break;
                    case 580418080:
                        if (opt.equals("--exclude-stopped-packages")) {
                            c = ' ';
                            break;
                        }
                        c = 65535;
                        break;
                    case 749648146:
                        if (opt.equals("--include-stopped-packages")) {
                            c = '!';
                            break;
                        }
                        c = 65535;
                        break;
                    case 775126336:
                        if (opt.equals("--receiver-replace-pending")) {
                            c = '4';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1110195121:
                        if (opt.equals("--activity-match-external")) {
                            c = '2';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1207327103:
                        if (opt.equals("--selector")) {
                            c = '8';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332978346:
                        if (opt.equals("--edal")) {
                            c = 21;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332980268:
                        if (opt.equals("--efal")) {
                            c = 18;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332983151:
                        if (opt.equals("--eial")) {
                            c = '\f';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332986034:
                        if (opt.equals("--elal")) {
                            c = 15;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332992761:
                        if (opt.equals("--esal")) {
                            c = 23;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1353919836:
                        if (opt.equals("--activity-clear-when-task-reset")) {
                            c = '%';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1398403374:
                        if (opt.equals("--activity-launched-from-history")) {
                            c = DateFormat.QUOTE;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1453225122:
                        if (opt.equals("--receiver-no-abort")) {
                            c = '6';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1652786753:
                        if (opt.equals("--receiver-foreground")) {
                            c = '5';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1742380566:
                        if (opt.equals("--grant-read-uri-permission")) {
                            c = 28;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1765369476:
                        if (opt.equals("--activity-multiple-task")) {
                            c = '(';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1816558127:
                        if (opt.equals("--grant-write-uri-permission")) {
                            c = 29;
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
                        intent2.setAction(cmd.getNextArgRequired());
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        String type2 = cmd.getNextArgRequired();
                        Uri data2 = Uri.parse(type2);
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                            data = data2;
                            break;
                        } else {
                            data = data2;
                            break;
                        }
                    case 2:
                        String type3 = cmd.getNextArgRequired();
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                            type = type3;
                            break;
                        } else {
                            type = type3;
                            break;
                        }
                    case 3:
                        intent2.setIdentifier(cmd.getNextArgRequired());
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        intent2.addCategory(cmd.getNextArgRequired());
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                            break;
                        } else {
                            break;
                        }
                    case 5:
                    case 6:
                        String key = cmd.getNextArgRequired();
                        String value = cmd.getNextArgRequired();
                        intent2.putExtra(key, value);
                        break;
                    case 7:
                        String key2 = cmd.getNextArgRequired();
                        intent2.putExtra(key2, (String) null);
                        break;
                    case '\b':
                        String key3 = cmd.getNextArgRequired();
                        String value2 = cmd.getNextArgRequired();
                        intent2.putExtra(key3, Integer.decode(value2));
                        break;
                    case '\t':
                        String key4 = cmd.getNextArgRequired();
                        String value3 = cmd.getNextArgRequired();
                        intent2.putExtra(key4, Uri.parse(value3));
                        break;
                    case '\n':
                        String key5 = cmd.getNextArgRequired();
                        String value4 = cmd.getNextArgRequired();
                        ComponentName cn = ComponentName.unflattenFromString(value4);
                        if (cn == null) {
                            throw new IllegalArgumentException("Bad component name: " + value4);
                        }
                        intent2.putExtra(key5, cn);
                        break;
                    case 11:
                        String key6 = cmd.getNextArgRequired();
                        String value5 = cmd.getNextArgRequired();
                        String[] strings = value5.split(",");
                        int[] list = new int[strings.length];
                        for (int i = 0; i < strings.length; i++) {
                            list[i] = Integer.decode(strings[i]).intValue();
                        }
                        intent2.putExtra(key6, list);
                        break;
                    case '\f':
                        String key7 = cmd.getNextArgRequired();
                        String value6 = cmd.getNextArgRequired();
                        String[] strings2 = value6.split(",");
                        ArrayList<Integer> list2 = new ArrayList<>(strings2.length);
                        for (String str : strings2) {
                            list2.add(Integer.decode(str));
                        }
                        intent2.putExtra(key7, list2);
                        break;
                    case '\r':
                        String key8 = cmd.getNextArgRequired();
                        String value7 = cmd.getNextArgRequired();
                        intent2.putExtra(key8, Long.valueOf(value7));
                        break;
                    case 14:
                        String key9 = cmd.getNextArgRequired();
                        String value8 = cmd.getNextArgRequired();
                        String[] strings3 = value8.split(",");
                        long[] list3 = new long[strings3.length];
                        for (int i2 = 0; i2 < strings3.length; i2++) {
                            list3[i2] = Long.valueOf(strings3[i2]).longValue();
                        }
                        intent2.putExtra(key9, list3);
                        hasIntentInfo = true;
                        break;
                    case 15:
                        String key10 = cmd.getNextArgRequired();
                        String value9 = cmd.getNextArgRequired();
                        String[] strings4 = value9.split(",");
                        ArrayList<Long> list4 = new ArrayList<>(strings4.length);
                        for (String str2 : strings4) {
                            list4.add(Long.valueOf(str2));
                        }
                        intent2.putExtra(key10, list4);
                        hasIntentInfo = true;
                        break;
                    case 16:
                        String key11 = cmd.getNextArgRequired();
                        String value10 = cmd.getNextArgRequired();
                        intent2.putExtra(key11, Float.valueOf(value10));
                        hasIntentInfo = true;
                        break;
                    case 17:
                        String key12 = cmd.getNextArgRequired();
                        String value11 = cmd.getNextArgRequired();
                        String[] strings5 = value11.split(",");
                        float[] list5 = new float[strings5.length];
                        for (int i3 = 0; i3 < strings5.length; i3++) {
                            list5[i3] = Float.valueOf(strings5[i3]).floatValue();
                        }
                        intent2.putExtra(key12, list5);
                        hasIntentInfo = true;
                        break;
                    case 18:
                        String key13 = cmd.getNextArgRequired();
                        String value12 = cmd.getNextArgRequired();
                        String[] strings6 = value12.split(",");
                        ArrayList<Float> list6 = new ArrayList<>(strings6.length);
                        for (String str3 : strings6) {
                            list6.add(Float.valueOf(str3));
                        }
                        intent2.putExtra(key13, list6);
                        hasIntentInfo = true;
                        break;
                    case 19:
                        String key14 = cmd.getNextArgRequired();
                        String value13 = cmd.getNextArgRequired();
                        intent2.putExtra(key14, Double.valueOf(value13));
                        hasIntentInfo = true;
                        break;
                    case 20:
                        String key15 = cmd.getNextArgRequired();
                        String value14 = cmd.getNextArgRequired();
                        String[] strings7 = value14.split(",");
                        double[] list7 = new double[strings7.length];
                        for (int i4 = 0; i4 < strings7.length; i4++) {
                            list7[i4] = Double.valueOf(strings7[i4]).doubleValue();
                        }
                        intent2.putExtra(key15, list7);
                        hasIntentInfo = true;
                        break;
                    case 21:
                        String key16 = cmd.getNextArgRequired();
                        String value15 = cmd.getNextArgRequired();
                        String[] strings8 = value15.split(",");
                        ArrayList<Double> list8 = new ArrayList<>(strings8.length);
                        for (String str4 : strings8) {
                            list8.add(Double.valueOf(str4));
                        }
                        intent2.putExtra(key16, list8);
                        hasIntentInfo = true;
                        break;
                    case 22:
                        String key17 = cmd.getNextArgRequired();
                        String value16 = cmd.getNextArgRequired();
                        intent2.putExtra(key17, value16.split("(?<!\\\\),"));
                        hasIntentInfo = true;
                        break;
                    case 23:
                        String key18 = cmd.getNextArgRequired();
                        String value17 = cmd.getNextArgRequired();
                        String[] strings9 = value17.split("(?<!\\\\),");
                        ArrayList<String> list9 = new ArrayList<>(strings9.length);
                        for (String str5 : strings9) {
                            list9.add(str5);
                        }
                        intent2.putExtra(key18, list9);
                        hasIntentInfo = true;
                        break;
                    case 24:
                        String key19 = cmd.getNextArgRequired();
                        String value18 = cmd.getNextArgRequired().toLowerCase();
                        if ("true".equals(value18) || "t".equals(value18)) {
                            arg = true;
                        } else if ("false".equals(value18) || FullBackup.FILES_TREE_TOKEN.equals(value18)) {
                            arg = false;
                        } else {
                            try {
                                arg = Integer.decode(value18).intValue() != 0;
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid boolean value: " + value18);
                            }
                        }
                        intent2.putExtra(key19, arg);
                        break;
                    case 25:
                        String str6 = cmd.getNextArgRequired();
                        ComponentName cn2 = ComponentName.unflattenFromString(str6);
                        if (cn2 == null) {
                            throw new IllegalArgumentException("Bad component name: " + str6);
                        }
                        intent2.setComponent(cn2);
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                        }
                        break;
                    case 26:
                        intent2.setPackage(cmd.getNextArgRequired());
                        if (intent2 == intent) {
                            hasIntentInfo = true;
                        }
                        break;
                    case 27:
                        intent2.setFlags(Integer.decode(cmd.getNextArgRequired()).intValue());
                        break;
                    case 28:
                        intent2.addFlags(1);
                        break;
                    case 29:
                        intent2.addFlags(2);
                        break;
                    case 30:
                        intent2.addFlags(64);
                        break;
                    case 31:
                        intent2.addFlags(128);
                        break;
                    case ' ':
                        intent2.addFlags(16);
                        break;
                    case '!':
                        intent2.addFlags(32);
                        break;
                    case '\"':
                        intent2.addFlags(8);
                        break;
                    case '#':
                        intent2.addFlags(4194304);
                        break;
                    case '$':
                        intent2.addFlags(67108864);
                        break;
                    case '%':
                        intent2.addFlags(524288);
                        break;
                    case '&':
                        intent2.addFlags(8388608);
                        break;
                    case '\'':
                        intent2.addFlags(1048576);
                        break;
                    case '(':
                        intent2.addFlags(134217728);
                        break;
                    case ')':
                        intent2.addFlags(65536);
                        break;
                    case '*':
                        intent2.addFlags(1073741824);
                        break;
                    case '+':
                        intent2.addFlags(262144);
                        break;
                    case ',':
                        intent2.addFlags(16777216);
                        break;
                    case '-':
                        intent2.addFlags(131072);
                        break;
                    case '.':
                        intent2.addFlags(2097152);
                        break;
                    case '/':
                        intent2.addFlags(536870912);
                        break;
                    case '0':
                        intent2.addFlags(32768);
                        break;
                    case '1':
                        intent2.addFlags(16384);
                        break;
                    case '2':
                        intent2.addFlags(2048);
                        break;
                    case '3':
                        intent2.addFlags(1073741824);
                        break;
                    case '4':
                        intent2.addFlags(536870912);
                        break;
                    case '5':
                        intent2.addFlags(268435456);
                        break;
                    case '6':
                        intent2.addFlags(134217728);
                        break;
                    case '7':
                        intent2.addFlags(16777216);
                        break;
                    case '8':
                        intent2.setDataAndType(data, type);
                        intent2 = new Intent();
                        break;
                    default:
                        if (optionHandler != null && optionHandler.handleOption(opt, cmd)) {
                            break;
                        }
                        break;
                }
            } else {
                intent2.setDataAndType(data, type);
                boolean hasSelector = intent2 != intent;
                if (hasSelector) {
                    intent.setSelector(intent2);
                    intent2 = intent;
                }
                String arg2 = cmd.getNextArg();
                Intent baseIntent = null;
                if (arg2 == null) {
                    if (hasSelector) {
                        baseIntent = new Intent(ACTION_MAIN);
                        baseIntent.addCategory(CATEGORY_LAUNCHER);
                    }
                } else if (arg2.indexOf(58) >= 0) {
                    baseIntent = parseUri(arg2, 7);
                } else if (arg2.indexOf(47) >= 0) {
                    baseIntent = new Intent(ACTION_MAIN);
                    baseIntent.addCategory(CATEGORY_LAUNCHER);
                    baseIntent.setComponent(ComponentName.unflattenFromString(arg2));
                } else {
                    baseIntent = new Intent(ACTION_MAIN);
                    baseIntent.addCategory(CATEGORY_LAUNCHER);
                    baseIntent.setPackage(arg2);
                }
                if (baseIntent != null) {
                    Bundle extras = intent2.getExtras();
                    intent2.replaceExtras((Bundle) null);
                    Bundle uriExtras = baseIntent.getExtras();
                    baseIntent.replaceExtras((Bundle) null);
                    if (intent2.getAction() != null && baseIntent.getCategories() != null) {
                        HashSet<String> cats = new HashSet<>(baseIntent.getCategories());
                        Iterator<String> it = cats.iterator();
                        while (it.hasNext()) {
                            String c2 = it.next();
                            baseIntent.removeCategory(c2);
                        }
                    }
                    intent2.fillIn(baseIntent, 72);
                    if (extras == null) {
                        extras = uriExtras;
                    } else if (uriExtras != null) {
                        uriExtras.putAll(extras);
                        extras = uriExtras;
                    }
                    intent2.replaceExtras(extras);
                    hasIntentInfo = true;
                }
                if (!hasIntentInfo) {
                    throw new IllegalArgumentException("No intent supplied");
                }
                return intent2;
            }
        }
    }

    public static void printIntentArgsHelp(PrintWriter pw, String prefix) {
        String[] lines = {"<INTENT> specifications include these flags and arguments:", "    [-a <ACTION>] [-d <DATA_URI>] [-t <MIME_TYPE>] [-i <IDENTIFIER>]", "    [-c <CATEGORY> [-c <CATEGORY>] ...]", "    [-n <COMPONENT_NAME>]", "    [-e|--es <EXTRA_KEY> <EXTRA_STRING_VALUE> ...]", "    [--esn <EXTRA_KEY> ...]", "    [--ez <EXTRA_KEY> <EXTRA_BOOLEAN_VALUE> ...]", "    [--ei <EXTRA_KEY> <EXTRA_INT_VALUE> ...]", "    [--el <EXTRA_KEY> <EXTRA_LONG_VALUE> ...]", "    [--ef <EXTRA_KEY> <EXTRA_FLOAT_VALUE> ...]", "    [--ed <EXTRA_KEY> <EXTRA_DOUBLE_VALUE> ...]", "    [--eu <EXTRA_KEY> <EXTRA_URI_VALUE> ...]", "    [--ecn <EXTRA_KEY> <EXTRA_COMPONENT_NAME_VALUE>]", "    [--eia <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]", "        (multiple extras passed as Integer[])", "    [--eial <EXTRA_KEY> <EXTRA_INT_VALUE>[,<EXTRA_INT_VALUE...]]", "        (multiple extras passed as List<Integer>)", "    [--ela <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]", "        (multiple extras passed as Long[])", "    [--elal <EXTRA_KEY> <EXTRA_LONG_VALUE>[,<EXTRA_LONG_VALUE...]]", "        (multiple extras passed as List<Long>)", "    [--efa <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]", "        (multiple extras passed as Float[])", "    [--efal <EXTRA_KEY> <EXTRA_FLOAT_VALUE>[,<EXTRA_FLOAT_VALUE...]]", "        (multiple extras passed as List<Float>)", "    [--eda <EXTRA_KEY> <EXTRA_DOUBLE_VALUE>[,<EXTRA_DOUBLE_VALUE...]]", "        (multiple extras passed as Double[])", "    [--edal <EXTRA_KEY> <EXTRA_DOUBLE_VALUE>[,<EXTRA_DOUBLE_VALUE...]]", "        (multiple extras passed as List<Double>)", "    [--esa <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]", "        (multiple extras passed as String[]; to embed a comma into a string,", "         escape it using \"\\,\")", "    [--esal <EXTRA_KEY> <EXTRA_STRING_VALUE>[,<EXTRA_STRING_VALUE...]]", "        (multiple extras passed as List<String>; to embed a comma into a string,", "         escape it using \"\\,\")", "    [-f <FLAG>]", "    [--grant-read-uri-permission] [--grant-write-uri-permission]", "    [--grant-persistable-uri-permission] [--grant-prefix-uri-permission]", "    [--debug-log-resolution] [--exclude-stopped-packages]", "    [--include-stopped-packages]", "    [--activity-brought-to-front] [--activity-clear-top]", "    [--activity-clear-when-task-reset] [--activity-exclude-from-recents]", "    [--activity-launched-from-history] [--activity-multiple-task]", "    [--activity-no-animation] [--activity-no-history]", "    [--activity-no-user-action] [--activity-previous-is-top]", "    [--activity-reorder-to-front] [--activity-reset-task-if-needed]", "    [--activity-single-top] [--activity-clear-task]", "    [--activity-task-on-home] [--activity-match-external]", "    [--receiver-registered-only] [--receiver-replace-pending]", "    [--receiver-foreground] [--receiver-no-abort]", "    [--receiver-include-background]", "    [--selector]", "    [<URI> | <PACKAGE> | <COMPONENT>]"};
        for (String line : lines) {
            pw.print(prefix);
            pw.println(line);
        }
    }

    public String getAction() {
        return this.mAction;
    }

    public Uri getData() {
        return this.mData;
    }

    public String getDataString() {
        Uri uri = this.mData;
        if (uri != null) {
            return uri.toString();
        }
        return null;
    }

    public String getScheme() {
        Uri uri = this.mData;
        if (uri != null) {
            return uri.getScheme();
        }
        return null;
    }

    public String getType() {
        return this.mType;
    }

    public Intent getOriginalIntent() {
        return this.mOriginalIntent;
    }

    public void setOriginalIntent(Intent originalIntent) {
        this.mOriginalIntent = originalIntent;
    }

    public String resolveType(Context context) {
        return resolveType(context.getContentResolver());
    }

    public String resolveType(ContentResolver resolver) {
        String str = this.mType;
        if (str != null) {
            return str;
        }
        Uri uri = this.mData;
        if (uri != null && "content".equals(uri.getScheme())) {
            return resolver.getType(this.mData);
        }
        return null;
    }

    public String resolveTypeIfNeeded(ContentResolver resolver) {
        if (this.mComponent != null && (Process.myUid() == 0 || Process.myUid() == 1000 || this.mComponent.getPackageName().equals(ActivityThread.currentPackageName()))) {
            return this.mType;
        }
        return resolveType(resolver);
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public boolean hasCategory(String category) {
        ArraySet<String> arraySet = this.mCategories;
        return arraySet != null && arraySet.contains(category);
    }

    public Set<String> getCategories() {
        return this.mCategories;
    }

    public Intent getSelector() {
        return this.mSelector;
    }

    public ClipData getClipData() {
        return this.mClipData;
    }

    public int getContentUserHint() {
        return this.mContentUserHint;
    }

    public String getLaunchToken() {
        return this.mLaunchToken;
    }

    public void setLaunchToken(String launchToken) {
        this.mLaunchToken = launchToken;
    }

    public void setExtrasClassLoader(ClassLoader loader) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.setClassLoader(loader);
        }
    }

    public boolean hasExtra(String name) {
        Bundle bundle = this.mExtras;
        return bundle != null && bundle.containsKey(name);
    }

    public boolean hasFileDescriptors() {
        Bundle bundle = this.mExtras;
        return bundle != null && bundle.hasFileDescriptors();
    }

    public void setAllowFds(boolean allowFds) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.setAllowFds(allowFds);
        }
    }

    public void setDefusable(boolean defusable) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.setDefusable(defusable);
        }
    }

    @Deprecated
    public Object getExtra(String name) {
        return getExtra(name, null);
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getBoolean(name, defaultValue);
    }

    public byte getByteExtra(String name, byte defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getByte(name, defaultValue).byteValue();
    }

    public short getShortExtra(String name, short defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getShort(name, defaultValue);
    }

    public char getCharExtra(String name, char defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getChar(name, defaultValue);
    }

    public int getIntExtra(String name, int defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getInt(name, defaultValue);
    }

    public long getLongExtra(String name, long defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getLong(name, defaultValue);
    }

    public float getFloatExtra(String name, float defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getFloat(name, defaultValue);
    }

    public double getDoubleExtra(String name, double defaultValue) {
        Bundle bundle = this.mExtras;
        return bundle == null ? defaultValue : bundle.getDouble(name, defaultValue);
    }

    public String getStringExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getString(name);
    }

    public CharSequence getCharSequenceExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequence(name);
    }

    @Deprecated
    public <T extends Parcelable> T getParcelableExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return (T) bundle.getParcelable(name);
    }

    public <T> T getParcelableExtra(String name, Class<T> clazz) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return (T) bundle.getParcelable(name, clazz);
    }

    @Deprecated
    public Parcelable[] getParcelableArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelableArray(name);
    }

    public <T> T[] getParcelableArrayExtra(String name, Class<T> clazz) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return (T[]) bundle.getParcelableArray(name, clazz);
    }

    @Deprecated
    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelableArrayList(name);
    }

    public <T> ArrayList<T> getParcelableArrayListExtra(String name, Class<? extends T> clazz) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelableArrayList(name, clazz);
    }

    public Serializable getSerializableExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getSerializable(name);
    }

    public <T extends Serializable> T getSerializableExtra(String name, Class<T> clazz) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return (T) bundle.getSerializable(name, clazz);
    }

    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIntegerArrayList(name);
    }

    public ArrayList<String> getStringArrayListExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getStringArrayList(name);
    }

    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequenceArrayList(name);
    }

    public boolean[] getBooleanArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getBooleanArray(name);
    }

    public byte[] getByteArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getByteArray(name);
    }

    public short[] getShortArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getShortArray(name);
    }

    public char[] getCharArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharArray(name);
    }

    public int[] getIntArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIntArray(name);
    }

    public long[] getLongArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getLongArray(name);
    }

    public float[] getFloatArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getFloatArray(name);
    }

    public double[] getDoubleArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getDoubleArray(name);
    }

    public String[] getStringArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getStringArray(name);
    }

    public CharSequence[] getCharSequenceArrayExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequenceArray(name);
    }

    public Bundle getBundleExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getBundle(name);
    }

    @Deprecated
    public IBinder getIBinderExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIBinder(name);
    }

    @Deprecated
    public Object getExtra(String name, Object defaultValue) {
        Object result2;
        Bundle bundle = this.mExtras;
        if (bundle == null || (result2 = bundle.get(name)) == null) {
            return defaultValue;
        }
        return result2;
    }

    public Bundle getExtras() {
        if (this.mExtras != null) {
            return new Bundle(this.mExtras);
        }
        return null;
    }

    public int getExtrasTotalSize() {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            return bundle.getSize();
        }
        return 0;
    }

    public boolean canStripForHistory() {
        Bundle bundle = this.mExtras;
        return (bundle != null && bundle.isParcelled()) || this.mClipData != null;
    }

    public Intent maybeStripForHistory() {
        if (!canStripForHistory()) {
            return this;
        }
        return new Intent(this, 2);
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isExcludingStopped() {
        return (this.mFlags & 48) == 16;
    }

    public String getPackage() {
        return this.mPackage;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    public Rect getSourceBounds() {
        return this.mSourceBounds;
    }

    public ComponentName resolveActivity(PackageManager pm) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            return componentName;
        }
        ResolveInfo info = pm.resolveActivity(this, 65536);
        if (info != null) {
            return new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
        }
        return null;
    }

    public ActivityInfo resolveActivityInfo(PackageManager pm, int flags) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            try {
                ActivityInfo ai = pm.getActivityInfo(componentName, flags);
                return ai;
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        ResolveInfo info = pm.resolveActivity(this, 65536 | flags);
        if (info == null) {
            return null;
        }
        ActivityInfo ai2 = info.activityInfo;
        return ai2;
    }

    public ComponentName resolveSystemService(PackageManager pm, int flags) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            return componentName;
        }
        List<ResolveInfo> results = pm.queryIntentServices(this, flags);
        if (results == null) {
            return null;
        }
        ComponentName comp = null;
        for (int i = 0; i < results.size(); i++) {
            ResolveInfo ri = results.get(i);
            if ((ri.serviceInfo.applicationInfo.flags & 1) != 0) {
                ComponentName foundComp = new ComponentName(ri.serviceInfo.applicationInfo.packageName, ri.serviceInfo.name);
                if (comp != null) {
                    throw new IllegalStateException("Multiple system services handle " + this + ": " + comp + ", " + foundComp);
                }
                comp = foundComp;
            }
        }
        return comp;
    }

    public Intent setAction(String action) {
        this.mAction = action != null ? action.intern() : null;
        return this;
    }

    public Intent setData(Uri data) {
        this.mData = data;
        this.mType = null;
        return this;
    }

    public Intent setDataAndNormalize(Uri data) {
        return setData(data.normalizeScheme());
    }

    public Intent setType(String type) {
        this.mData = null;
        this.mType = type;
        return this;
    }

    public Intent setTypeAndNormalize(String type) {
        return setType(normalizeMimeType(type));
    }

    public Intent setDataAndType(Uri data, String type) {
        this.mData = data;
        this.mType = type;
        return this;
    }

    public Intent setDataAndTypeAndNormalize(Uri data, String type) {
        return setDataAndType(data.normalizeScheme(), normalizeMimeType(type));
    }

    public Intent setIdentifier(String identifier) {
        this.mIdentifier = identifier;
        return this;
    }

    public Intent addCategory(String category) {
        if (this.mCategories == null) {
            this.mCategories = new ArraySet<>();
        }
        this.mCategories.add(category.intern());
        return this;
    }

    public void removeCategory(String category) {
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            arraySet.remove(category);
            if (this.mCategories.size() == 0) {
                this.mCategories = null;
            }
        }
    }

    public void setSelector(Intent selector) {
        if (selector == this) {
            throw new IllegalArgumentException("Intent being set as a selector of itself");
        }
        if (selector != null && this.mPackage != null) {
            throw new IllegalArgumentException("Can't set selector when package name is already set");
        }
        this.mSelector = selector;
    }

    public void setClipData(ClipData clip) {
        this.mClipData = clip;
    }

    public void prepareToLeaveUser(int userId) {
        if (this.mContentUserHint == -2) {
            this.mContentUserHint = userId;
        }
    }

    public Intent putExtra(String name, boolean value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBoolean(name, value);
        return this;
    }

    public Intent putExtra(String name, byte value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putByte(name, value);
        return this;
    }

    public Intent putExtra(String name, char value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putChar(name, value);
        return this;
    }

    public Intent putExtra(String name, short value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putShort(name, value);
        return this;
    }

    public Intent putExtra(String name, int value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putInt(name, value);
        return this;
    }

    public Intent putExtra(String name, long value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putLong(name, value);
        return this;
    }

    public Intent putExtra(String name, float value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putFloat(name, value);
        return this;
    }

    public Intent putExtra(String name, double value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putDouble(name, value);
        return this;
    }

    public Intent putExtra(String name, String value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putString(name, value);
        return this;
    }

    public Intent putExtra(String name, CharSequence value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequence(name, value);
        return this;
    }

    public Intent putExtra(String name, Parcelable value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelable(name, value);
        return this;
    }

    public Intent putExtra(String name, Parcelable[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelableArray(name, value);
        return this;
    }

    public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelableArrayList(name, value);
        return this;
    }

    public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIntegerArrayList(name, value);
        return this;
    }

    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putStringArrayList(name, value);
        return this;
    }

    public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequenceArrayList(name, value);
        return this;
    }

    public Intent putExtra(String name, Serializable value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putSerializable(name, value);
        return this;
    }

    public Intent putExtra(String name, boolean[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBooleanArray(name, value);
        return this;
    }

    public Intent putExtra(String name, byte[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putByteArray(name, value);
        return this;
    }

    public Intent putExtra(String name, short[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putShortArray(name, value);
        return this;
    }

    public Intent putExtra(String name, char[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharArray(name, value);
        return this;
    }

    public Intent putExtra(String name, int[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIntArray(name, value);
        return this;
    }

    public Intent putExtra(String name, long[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putLongArray(name, value);
        return this;
    }

    public Intent putExtra(String name, float[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putFloatArray(name, value);
        return this;
    }

    public Intent putExtra(String name, double[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putDoubleArray(name, value);
        return this;
    }

    public Intent putExtra(String name, String[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putStringArray(name, value);
        return this;
    }

    public Intent putExtra(String name, CharSequence[] value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequenceArray(name, value);
        return this;
    }

    public Intent putExtra(String name, Bundle value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBundle(name, value);
        return this;
    }

    @Deprecated
    public Intent putExtra(String name, IBinder value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIBinder(name, value);
        return this;
    }

    public Intent putExtras(Intent src) {
        Bundle bundle = src.mExtras;
        if (bundle != null) {
            Bundle bundle2 = this.mExtras;
            if (bundle2 == null) {
                this.mExtras = new Bundle(src.mExtras);
            } else {
                bundle2.putAll(bundle);
            }
        }
        int i = src.mLocalFlags;
        if ((i & 2) != 0 && (i & 36) == 0) {
            this.mLocalFlags |= 8;
        }
        return this;
    }

    public Intent putExtras(Bundle extras) {
        if (extras.isParcelled()) {
            this.mLocalFlags |= 8;
        }
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putAll(extras);
        return this;
    }

    public Intent replaceExtras(Intent src) {
        this.mExtras = src.mExtras != null ? new Bundle(src.mExtras) : null;
        return this;
    }

    public Intent replaceExtras(Bundle extras) {
        this.mExtras = extras != null ? new Bundle(extras) : null;
        return this;
    }

    public void removeExtra(String name) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.remove(name);
            if (this.mExtras.size() == 0) {
                this.mExtras = null;
            }
        }
    }

    public Intent setFlags(int flags) {
        this.mFlags = flags;
        return this;
    }

    public Intent addFlags(int flags) {
        this.mFlags |= flags;
        return this;
    }

    public void removeFlags(int flags) {
        this.mFlags &= ~flags;
    }

    public Intent setPackage(String packageName) {
        if (packageName != null && this.mSelector != null) {
            throw new IllegalArgumentException("Can't set package name when selector is already set");
        }
        this.mPackage = packageName;
        return this;
    }

    public Intent setComponent(ComponentName component) {
        this.mComponent = component;
        return this;
    }

    public Intent setClassName(Context packageContext, String className) {
        this.mComponent = new ComponentName(packageContext, className);
        return this;
    }

    public Intent setClassName(String packageName, String className) {
        this.mComponent = new ComponentName(packageName, className);
        return this;
    }

    public Intent setClass(Context packageContext, Class<?> cls) {
        this.mComponent = new ComponentName(packageContext, cls);
        return this;
    }

    public void setSourceBounds(Rect r) {
        if (r != null) {
            this.mSourceBounds = new Rect(r);
        } else {
            this.mSourceBounds = null;
        }
    }

    public int fillIn(Intent other, int flags) {
        int i;
        int changes = 0;
        boolean mayHaveCopiedUris = false;
        String str = other.mAction;
        if (str != null && (this.mAction == null || (flags & 1) != 0)) {
            this.mAction = str;
            changes = 0 | 1;
        }
        Uri uri = other.mData;
        if ((uri != null || other.mType != null) && ((this.mData == null && this.mType == null) || (flags & 2) != 0)) {
            this.mData = uri;
            this.mType = other.mType;
            changes |= 2;
            mayHaveCopiedUris = true;
        }
        String str2 = other.mIdentifier;
        if (str2 != null && (this.mIdentifier == null || (flags & 256) != 0)) {
            this.mIdentifier = str2;
            changes |= 256;
        }
        ArraySet<String> arraySet = other.mCategories;
        if (arraySet != null && (this.mCategories == null || (flags & 4) != 0)) {
            if (arraySet != null) {
                this.mCategories = new ArraySet<>(other.mCategories);
            }
            changes |= 4;
        }
        String str3 = other.mPackage;
        if (str3 != null && ((this.mPackage == null || (flags & 16) != 0) && this.mSelector == null)) {
            this.mPackage = str3;
            changes |= 16;
        }
        Intent intent = other.mSelector;
        if (intent != null && (flags & 64) != 0 && this.mPackage == null) {
            this.mSelector = new Intent(intent);
            this.mPackage = null;
            changes |= 64;
        }
        ClipData clipData = other.mClipData;
        if (clipData != null && (this.mClipData == null || (flags & 128) != 0)) {
            this.mClipData = clipData;
            changes |= 128;
            mayHaveCopiedUris = true;
        }
        ComponentName componentName = other.mComponent;
        if (componentName != null && (flags & 8) != 0) {
            this.mComponent = componentName;
            changes |= 8;
        }
        this.mFlags |= other.mFlags;
        if (other.mSourceBounds != null && (this.mSourceBounds == null || (flags & 32) != 0)) {
            this.mSourceBounds = new Rect(other.mSourceBounds);
            changes |= 32;
        }
        if (this.mExtras == null) {
            if (other.mExtras != null) {
                this.mExtras = new Bundle(other.mExtras);
                mayHaveCopiedUris = true;
            }
        } else if (other.mExtras != null) {
            try {
                Bundle newb = new Bundle(other.mExtras);
                newb.putAll(this.mExtras);
                this.mExtras = newb;
                mayHaveCopiedUris = true;
            } catch (RuntimeException e) {
                Log.m103w(TAG, "Failure filling in extras", e);
            }
        }
        if (mayHaveCopiedUris && this.mContentUserHint == -2 && (i = other.mContentUserHint) != -2) {
            this.mContentUserHint = i;
        }
        return changes;
    }

    public void mergeExtras(Intent other, BundleMerger extrasMerger) {
        this.mExtras = extrasMerger.merge(this.mExtras, other.mExtras);
    }

    /* loaded from: classes.dex */
    public static final class FilterComparison {
        private final int mHashCode;
        private final Intent mIntent;

        public FilterComparison(Intent intent) {
            this.mIntent = intent;
            this.mHashCode = intent.filterHashCode();
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public boolean equals(Object obj) {
            if (obj instanceof FilterComparison) {
                Intent other = ((FilterComparison) obj).mIntent;
                return this.mIntent.filterEquals(other);
            }
            return false;
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }

    public boolean filterEquals(Intent other) {
        if (other == null || !Objects.equals(this.mAction, other.mAction) || !Objects.equals(this.mData, other.mData) || !Objects.equals(this.mType, other.mType) || !Objects.equals(this.mIdentifier, other.mIdentifier) || !Objects.equals(this.mPackage, other.mPackage) || !Objects.equals(this.mComponent, other.mComponent) || !Objects.equals(this.mCategories, other.mCategories)) {
            return false;
        }
        return true;
    }

    public int filterHashCode() {
        String str = this.mAction;
        int code = str != null ? 0 + str.hashCode() : 0;
        Uri uri = this.mData;
        if (uri != null) {
            code += uri.hashCode();
        }
        String str2 = this.mType;
        if (str2 != null) {
            code += str2.hashCode();
        }
        String str3 = this.mIdentifier;
        if (str3 != null) {
            code += str3.hashCode();
        }
        String str4 = this.mPackage;
        if (str4 != null) {
            code += str4.hashCode();
        }
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            code += componentName.hashCode();
        }
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            return code + arraySet.hashCode();
        }
        return code;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(128);
        b.append("Intent { ");
        toShortString(b, true, true, true, false);
        b.append(" }");
        return b.toString();
    }

    public String toInsecureString() {
        StringBuilder b = new StringBuilder(128);
        b.append("Intent { ");
        toShortString(b, false, true, true, false);
        b.append(" }");
        return b.toString();
    }

    public String toShortString(boolean secure, boolean comp, boolean extras, boolean clip) {
        StringBuilder b = new StringBuilder(128);
        toShortString(b, secure, comp, extras, clip);
        return b.toString();
    }

    public void toShortString(StringBuilder b, boolean secure, boolean comp, boolean extras, boolean clip) {
        boolean first = true;
        if (this.mAction != null) {
            b.append("act=").append(this.mAction);
            first = false;
        }
        if (this.mCategories != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cat=[");
            for (int i = 0; i < this.mCategories.size(); i++) {
                if (i > 0) {
                    b.append(',');
                }
                b.append(this.mCategories.valueAt(i));
            }
            b.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        if (this.mData != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("dat=");
            if (secure) {
                b.append(this.mData.toSafeString());
            } else {
                b.append(this.mData);
            }
        }
        if (this.mType != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("typ=").append(this.mType);
        }
        if (this.mIdentifier != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("id=").append(this.mIdentifier);
        }
        if (this.mFlags != 0) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("flg=0x").append(Integer.toHexString(this.mFlags));
        }
        if (this.mPackage != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("pkg=").append(this.mPackage);
        }
        if (comp && this.mComponent != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cmp=").append(this.mComponent.flattenToShortString());
        }
        if (this.mSourceBounds != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("bnds=").append(this.mSourceBounds.toShortString());
        }
        if (this.mClipData != null) {
            if (!first) {
                b.append(' ');
            }
            b.append("clip={");
            this.mClipData.toShortString(b, !clip || secure);
            first = false;
            b.append('}');
        }
        if (extras && this.mExtras != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("(has extras)");
        }
        if (this.mContentUserHint != -2) {
            if (!first) {
                b.append(' ');
            }
            b.append("u=").append(this.mContentUserHint);
        }
        if (this.mSelector != null) {
            b.append(" sel=");
            this.mSelector.toShortString(b, secure, comp, extras, clip);
            b.append("}");
        }
        if (this.mOriginalIntent != null) {
            b.append(" org={");
            this.mOriginalIntent.toShortString(b, secure, comp, extras, clip);
            b.append("}");
        }
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        dumpDebug(proto, fieldId, true, true, true, false);
    }

    public void dumpDebug(ProtoOutputStream proto) {
        dumpDebugWithoutFieldId(proto, true, true, true, false);
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId, boolean secure, boolean comp, boolean extras, boolean clip) {
        long token = proto.start(fieldId);
        dumpDebugWithoutFieldId(proto, secure, comp, extras, clip);
        proto.end(token);
    }

    private void dumpDebugWithoutFieldId(ProtoOutputStream proto, boolean secure, boolean comp, boolean extras, boolean clip) {
        Bundle bundle;
        ComponentName componentName;
        String str = this.mAction;
        if (str != null) {
            proto.write(1138166333441L, str);
        }
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            Iterator<String> it = arraySet.iterator();
            while (it.hasNext()) {
                String category = it.next();
                proto.write(2237677961218L, category);
            }
        }
        Uri uri = this.mData;
        if (uri != null) {
            proto.write(1138166333443L, secure ? uri.toSafeString() : uri.toString());
        }
        String str2 = this.mType;
        if (str2 != null) {
            proto.write(1138166333444L, str2);
        }
        String str3 = this.mIdentifier;
        if (str3 != null) {
            proto.write(1138166333453L, str3);
        }
        if (this.mFlags != 0) {
            proto.write(1138166333445L, "0x" + Integer.toHexString(this.mFlags));
        }
        String str4 = this.mPackage;
        if (str4 != null) {
            proto.write(1138166333446L, str4);
        }
        if (comp && (componentName = this.mComponent) != null) {
            componentName.dumpDebug(proto, 1146756268039L);
        }
        Rect rect = this.mSourceBounds;
        if (rect != null) {
            proto.write(1138166333448L, rect.toShortString());
        }
        if (this.mClipData != null) {
            StringBuilder b = new StringBuilder();
            this.mClipData.toShortString(b, !clip || secure);
            proto.write(1138166333449L, b.toString());
        }
        if (extras && (bundle = this.mExtras) != null) {
            proto.write(1138166333450L, bundle.toShortString());
        }
        int i = this.mContentUserHint;
        if (i != 0) {
            proto.write(1120986464267L, i);
        }
        Intent intent = this.mSelector;
        if (intent != null) {
            proto.write(1138166333452L, intent.toShortString(secure, comp, extras, clip));
        }
    }

    @Deprecated
    public String toURI() {
        return toUri(0);
    }

    public String toUri(int flags) {
        StringBuilder uri = new StringBuilder(128);
        if ((flags & 2) != 0) {
            if (this.mPackage == null) {
                throw new IllegalArgumentException("Intent must include an explicit package name to build an android-app: " + this);
            }
            uri.append("android-app://");
            uri.append(this.mPackage);
            String scheme = null;
            Uri uri2 = this.mData;
            if (uri2 != null && (scheme = uri2.getScheme()) != null) {
                uri.append('/');
                uri.append(scheme);
                String authority = this.mData.getEncodedAuthority();
                if (authority != null) {
                    uri.append('/');
                    uri.append(authority);
                    String path = this.mData.getEncodedPath();
                    if (path != null) {
                        uri.append(path);
                    }
                    String queryParams = this.mData.getEncodedQuery();
                    if (queryParams != null) {
                        uri.append('?');
                        uri.append(queryParams);
                    }
                    String fragment = this.mData.getEncodedFragment();
                    if (fragment != null) {
                        uri.append('#');
                        uri.append(fragment);
                    }
                }
            }
            toUriFragment(uri, null, scheme == null ? ACTION_MAIN : "android.intent.action.VIEW", this.mPackage, flags);
            return uri.toString();
        }
        String scheme2 = null;
        Uri uri3 = this.mData;
        if (uri3 != null) {
            String data = uri3.toString();
            if ((flags & 1) != 0) {
                int N = data.length();
                int i = 0;
                while (true) {
                    if (i >= N) {
                        break;
                    }
                    char c = data.charAt(i);
                    if ((c >= 'a' && c <= 'z') || ((c >= 'A' && c <= 'Z') || ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+'))) {
                        i++;
                    } else if (c == ':' && i > 0) {
                        scheme2 = data.substring(0, i);
                        uri.append("intent:");
                        data = data.substring(i + 1);
                    }
                }
            }
            uri.append(data);
        } else if ((flags & 1) != 0) {
            uri.append("intent:");
        }
        toUriFragment(uri, scheme2, "android.intent.action.VIEW", null, flags);
        return uri.toString();
    }

    private void toUriFragment(StringBuilder uri, String scheme, String defAction, String defPackage, int flags) {
        StringBuilder frag = new StringBuilder(128);
        toUriInner(frag, scheme, defAction, defPackage, flags);
        if (this.mSelector != null) {
            frag.append("SEL;");
            Intent intent = this.mSelector;
            Uri uri2 = intent.mData;
            intent.toUriInner(frag, uri2 != null ? uri2.getScheme() : null, null, null, flags);
        }
        if (frag.length() > 0) {
            uri.append("#Intent;");
            uri.append((CharSequence) frag);
            uri.append("end");
        }
    }

    private void toUriInner(StringBuilder uri, String scheme, String defAction, String defPackage, int flags) {
        char entryType;
        if (scheme != null) {
            uri.append("scheme=").append(Uri.encode(scheme)).append(';');
        }
        String str = this.mAction;
        if (str != null && !str.equals(defAction)) {
            uri.append("action=").append(Uri.encode(this.mAction)).append(';');
        }
        if (this.mCategories != null) {
            for (int i = 0; i < this.mCategories.size(); i++) {
                uri.append("category=").append(Uri.encode(this.mCategories.valueAt(i))).append(';');
            }
        }
        if (this.mType != null) {
            uri.append("type=").append(Uri.encode(this.mType, "/")).append(';');
        }
        if (this.mIdentifier != null) {
            uri.append("identifier=").append(Uri.encode(this.mIdentifier, "/")).append(';');
        }
        if (this.mFlags != 0) {
            uri.append("launchFlags=0x").append(Integer.toHexString(this.mFlags)).append(';');
        }
        String str2 = this.mPackage;
        if (str2 != null && !str2.equals(defPackage)) {
            uri.append("package=").append(Uri.encode(this.mPackage)).append(';');
        }
        if (this.mComponent != null) {
            uri.append("component=").append(Uri.encode(this.mComponent.flattenToShortString(), "/")).append(';');
        }
        if (this.mSourceBounds != null) {
            uri.append("sourceBounds=").append(Uri.encode(this.mSourceBounds.flattenToString())).append(';');
        }
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = this.mExtras.get(key);
                if (value instanceof String) {
                    entryType = 'S';
                } else if (value instanceof Boolean) {
                    entryType = 'B';
                } else if (value instanceof Byte) {
                    entryType = 'b';
                } else if (value instanceof Character) {
                    entryType = 'c';
                } else if (value instanceof Double) {
                    entryType = DateFormat.DATE;
                } else if (value instanceof Float) {
                    entryType = 'f';
                } else if (value instanceof Integer) {
                    entryType = 'i';
                } else if (value instanceof Long) {
                    entryType = 'l';
                } else {
                    entryType = value instanceof Short ? 's' : (char) 0;
                }
                if (entryType != 0) {
                    uri.append(entryType);
                    uri.append('.');
                    uri.append(Uri.encode(key));
                    uri.append('=');
                    uri.append(Uri.encode(value.toString()));
                    uri.append(';');
                }
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            return bundle.describeContents();
        }
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString8(this.mAction);
        Uri.writeToParcel(out, this.mData);
        out.writeString8(this.mType);
        out.writeString8(this.mIdentifier);
        out.writeInt(this.mFlags);
        out.writeString8(this.mPackage);
        ComponentName.writeToParcel(this.mComponent, out);
        if (this.mSourceBounds != null) {
            out.writeInt(1);
            this.mSourceBounds.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            int N = arraySet.size();
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                out.writeString8(this.mCategories.valueAt(i));
            }
        } else {
            out.writeInt(0);
        }
        if (this.mSelector != null) {
            out.writeInt(1);
            this.mSelector.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        if (this.mClipData != null) {
            out.writeInt(1);
            this.mClipData.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.mContentUserHint);
        out.writeBundle(this.mExtras);
        if (this.mOriginalIntent != null) {
            out.writeInt(1);
            this.mOriginalIntent.writeToParcel(out, flags);
            return;
        }
        out.writeInt(0);
    }

    protected Intent(Parcel in) {
        this.mContentUserHint = -2;
        this.mLocalFlags = 2;
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        setAction(in.readString8());
        this.mData = Uri.CREATOR.createFromParcel(in);
        this.mType = in.readString8();
        this.mIdentifier = in.readString8();
        this.mFlags = in.readInt();
        this.mPackage = in.readString8();
        this.mComponent = ComponentName.readFromParcel(in);
        if (in.readInt() != 0) {
            this.mSourceBounds = Rect.CREATOR.createFromParcel(in);
        }
        int N = in.readInt();
        if (N > 0) {
            this.mCategories = new ArraySet<>();
            for (int i = 0; i < N; i++) {
                this.mCategories.add(in.readString8().intern());
            }
        } else {
            this.mCategories = null;
        }
        if (in.readInt() != 0) {
            this.mSelector = new Intent(in);
        }
        if (in.readInt() != 0) {
            this.mClipData = new ClipData(in);
        }
        this.mContentUserHint = in.readInt();
        this.mExtras = in.readBundle();
        if (in.readInt() != 0) {
            this.mOriginalIntent = new Intent(in);
        }
    }

    public static Intent parseIntent(Resources resources, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        Intent intent = new Intent();
        TypedArray sa = resources.obtainAttributes(attrs, C4057R.styleable.Intent);
        intent.setAction(sa.getString(2));
        int i = 3;
        String data = sa.getString(3);
        String mimeType = sa.getString(1);
        intent.setDataAndType(data != null ? Uri.parse(data) : null, mimeType);
        intent.setIdentifier(sa.getString(5));
        String packageName = sa.getString(0);
        String className = sa.getString(4);
        if (packageName != null && className != null) {
            intent.setComponent(new ComponentName(packageName, className));
        }
        sa.recycle();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == i && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type == i || type == 4) {
                i = 3;
            } else {
                String nodeName = parser.getName();
                if (nodeName.equals("categories")) {
                    TypedArray sa2 = resources.obtainAttributes(attrs, C4057R.styleable.IntentCategory);
                    String cat = sa2.getString(0);
                    sa2.recycle();
                    if (cat != null) {
                        intent.addCategory(cat);
                    }
                    XmlUtils.skipCurrentTag(parser);
                } else if (nodeName.equals(TAG_EXTRA)) {
                    if (intent.mExtras == null) {
                        intent.mExtras = new Bundle();
                    }
                    resources.parseBundleExtra(TAG_EXTRA, attrs, intent.mExtras);
                    XmlUtils.skipCurrentTag(parser);
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
                i = 3;
            }
        }
        return intent;
    }

    public void saveToXml(XmlSerializer out) throws IOException {
        String str = this.mAction;
        if (str != null) {
            out.attribute(null, "action", str);
        }
        Uri uri = this.mData;
        if (uri != null) {
            out.attribute(null, "data", uri.toString());
        }
        String str2 = this.mType;
        if (str2 != null) {
            out.attribute(null, "type", str2);
        }
        String str3 = this.mIdentifier;
        if (str3 != null) {
            out.attribute(null, ATTR_IDENTIFIER, str3);
        }
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            out.attribute(null, "component", componentName.flattenToShortString());
        }
        out.attribute(null, "flags", Integer.toHexString(getFlags()));
        if (this.mCategories != null) {
            out.startTag(null, "categories");
            for (int categoryNdx = this.mCategories.size() - 1; categoryNdx >= 0; categoryNdx--) {
                out.attribute(null, "category", this.mCategories.valueAt(categoryNdx));
            }
            out.endTag(null, "categories");
        }
    }

    public static Intent restoreFromXml(XmlPullParser in) throws IOException, XmlPullParserException {
        Intent intent = new Intent();
        int outerDepth = in.getDepth();
        int attrCount = in.getAttributeCount();
        for (int attrNdx = attrCount - 1; attrNdx >= 0; attrNdx--) {
            String attrName = in.getAttributeName(attrNdx);
            String attrValue = in.getAttributeValue(attrNdx);
            if ("action".equals(attrName)) {
                intent.setAction(attrValue);
            } else if ("data".equals(attrName)) {
                intent.setData(Uri.parse(attrValue));
            } else if ("type".equals(attrName)) {
                intent.setType(attrValue);
            } else if (ATTR_IDENTIFIER.equals(attrName)) {
                intent.setIdentifier(attrValue);
            } else if ("component".equals(attrName)) {
                intent.setComponent(ComponentName.unflattenFromString(attrValue));
            } else if ("flags".equals(attrName)) {
                intent.setFlags(Integer.parseInt(attrValue, 16));
            } else {
                Log.m110e(TAG, "restoreFromXml: unknown attribute=" + attrName);
            }
        }
        while (true) {
            int event = in.next();
            if (event == 1 || (event == 3 && in.getDepth() >= outerDepth)) {
                break;
            } else if (event == 2) {
                String name = in.getName();
                if ("categories".equals(name)) {
                    int attrCount2 = in.getAttributeCount();
                    for (int attrNdx2 = attrCount2 - 1; attrNdx2 >= 0; attrNdx2--) {
                        intent.addCategory(in.getAttributeValue(attrNdx2));
                    }
                } else {
                    Log.m104w(TAG, "restoreFromXml: unknown name=" + name);
                    XmlUtils.skipCurrentTag(in);
                }
            }
        }
        return intent;
    }

    public static String normalizeMimeType(String type) {
        if (type == null) {
            return null;
        }
        String type2 = type.trim().toLowerCase(Locale.ROOT);
        int semicolonIndex = type2.indexOf(59);
        if (semicolonIndex != -1) {
            return type2.substring(0, semicolonIndex);
        }
        return type2;
    }

    public void prepareToLeaveProcess(Context context) {
        boolean leavingPackage;
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            leavingPackage = !Objects.equals(componentName.getPackageName(), context.getPackageName());
        } else {
            String str = this.mPackage;
            if (str != null) {
                leavingPackage = !Objects.equals(str, context.getPackageName());
            } else {
                leavingPackage = true;
            }
        }
        prepareToLeaveProcess(leavingPackage);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x013f, code lost:
        if (r1.equals(android.content.Intent.ACTION_PROVIDER_CHANGED) != false) goto L91;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void prepareToLeaveProcess(boolean leavingPackage) {
        Uri uri;
        char c;
        boolean z = false;
        setAllowFds(false);
        Intent intent = this.mSelector;
        if (intent != null) {
            intent.prepareToLeaveProcess(leavingPackage);
        }
        ClipData clipData = this.mClipData;
        if (clipData != null) {
            clipData.prepareToLeaveProcess(leavingPackage, getFlags());
        }
        Intent intent2 = this.mOriginalIntent;
        if (intent2 != null) {
            intent2.prepareToLeaveProcess(leavingPackage);
        }
        Bundle bundle = this.mExtras;
        if (bundle != null && !bundle.isParcelled()) {
            Object intent3 = this.mExtras.get(EXTRA_INTENT);
            if (intent3 instanceof Intent) {
                ((Intent) intent3).prepareToLeaveProcess(leavingPackage);
            }
        }
        if (this.mAction != null && this.mData != null && StrictMode.vmFileUriExposureEnabled() && leavingPackage) {
            String str = this.mAction;
            switch (str.hashCode()) {
                case -2015721043:
                    if (str.equals(ACTION_PACKAGE_NEEDS_INTEGRITY_VERIFICATION)) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case -1823790459:
                    if (str.equals(ACTION_MEDIA_SHARED)) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -1665311200:
                    if (str.equals(ACTION_MEDIA_REMOVED)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1514214344:
                    if (str.equals(ACTION_MEDIA_MOUNTED)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1142424621:
                    if (str.equals(ACTION_MEDIA_SCANNER_FINISHED)) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -963871873:
                    if (str.equals(ACTION_MEDIA_UNMOUNTED)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -625887599:
                    if (str.equals(ACTION_MEDIA_EJECT)) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 257177710:
                    if (str.equals(ACTION_MEDIA_NOFS)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 410719838:
                    if (str.equals(ACTION_MEDIA_UNSHARED)) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 582421979:
                    if (str.equals(ACTION_PACKAGE_NEEDS_VERIFICATION)) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case 852070077:
                    if (str.equals(ACTION_MEDIA_SCANNER_SCAN_FILE)) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case 1412829408:
                    if (str.equals(ACTION_MEDIA_SCANNER_STARTED)) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case 1431947322:
                    if (str.equals(ACTION_MEDIA_UNMOUNTABLE)) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 1599438242:
                    if (str.equals(ACTION_PACKAGE_ENABLE_ROLLBACK)) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case 1920444806:
                    if (str.equals(ACTION_PACKAGE_VERIFIED)) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case 1964681210:
                    if (str.equals(ACTION_MEDIA_CHECKING)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 2045140818:
                    if (str.equals(ACTION_MEDIA_BAD_REMOVAL)) {
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
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case '\b':
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case 14:
                case 15:
                case 16:
                    break;
                default:
                    this.mData.checkFileUriExposed("Intent.getData()");
                    break;
            }
        }
        if (this.mAction != null && this.mData != null && StrictMode.vmContentUriWithoutPermissionEnabled() && leavingPackage) {
            String str2 = this.mAction;
            switch (str2.hashCode()) {
                case -577088908:
                    if (str2.equals(ContactsContract.QuickContact.ACTION_QUICK_CONTACT)) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 1662413067:
                    break;
                default:
                    z = true;
                    break;
            }
            switch (z) {
                case false:
                case true:
                    break;
                default:
                    this.mData.checkContentUriWithoutPermission("Intent.getData()", getFlags());
                    break;
            }
        }
        if (ACTION_MEDIA_SCANNER_SCAN_FILE.equals(this.mAction) && (uri = this.mData) != null && "file".equals(uri.getScheme()) && leavingPackage) {
            StorageManager sm = (StorageManager) AppGlobals.getInitialApplication().getSystemService(StorageManager.class);
            File before = new File(this.mData.getPath());
            File after = sm.translateAppToSystem(before, Process.myPid(), Process.myUid());
            if (!Objects.equals(before, after)) {
                Log.m106v(TAG, "Translated " + before + " to " + after);
                this.mData = Uri.fromFile(after);
            }
        }
        if (StrictMode.vmUnsafeIntentLaunchEnabled()) {
            int i = this.mLocalFlags;
            if ((i & 2) != 0 && (i & 36) == 0) {
                StrictMode.onUnsafeIntentLaunch(this);
            } else if ((i & 8) != 0) {
                StrictMode.onUnsafeIntentLaunch(this);
            } else if ((i & 16) != 0) {
                ArraySet<String> arraySet = this.mCategories;
                if (arraySet == null || !arraySet.contains(CATEGORY_BROWSABLE) || this.mComponent != null) {
                    StrictMode.onUnsafeIntentLaunch(this);
                }
            }
        }
    }

    public void prepareToEnterProcess(boolean fromProtectedComponent, AttributionSource source) {
        if (fromProtectedComponent) {
            prepareToEnterProcess(4, source);
        } else {
            prepareToEnterProcess(0, source);
        }
    }

    public void prepareToEnterProcess(int localFlags, AttributionSource source) {
        BluetoothDevice device;
        setDefusable(true);
        Intent intent = this.mSelector;
        if (intent != null) {
            intent.prepareToEnterProcess(0, source);
        }
        ClipData clipData = this.mClipData;
        if (clipData != null) {
            clipData.prepareToEnterProcess(source);
        }
        Intent intent2 = this.mOriginalIntent;
        if (intent2 != null) {
            intent2.prepareToEnterProcess(0, source);
        }
        if (this.mContentUserHint != -2 && UserHandle.getAppId(Process.myUid()) != 1000) {
            fixUris(this.mContentUserHint);
            this.mContentUserHint = -2;
        }
        this.mLocalFlags |= localFlags;
        String str = this.mAction;
        if (str != null && str.startsWith("android.bluetooth.") && hasExtra("android.bluetooth.device.extra.DEVICE") && (device = (BluetoothDevice) getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class)) != null) {
            device.prepareToEnterProcess(source);
        }
    }

    public boolean hasWebURI() {
        if (getData() == null) {
            return false;
        }
        String scheme = getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        return scheme.equals(IntentFilter.SCHEME_HTTP) || scheme.equals(IntentFilter.SCHEME_HTTPS);
    }

    public boolean isWebIntent() {
        return "android.intent.action.VIEW".equals(this.mAction) && hasWebURI();
    }

    private boolean isImageCaptureIntent() {
        return "android.media.action.IMAGE_CAPTURE".equals(this.mAction) || "android.media.action.IMAGE_CAPTURE_SECURE".equals(this.mAction) || "android.media.action.VIDEO_CAPTURE".equals(this.mAction);
    }

    public boolean isImplicitImageCaptureIntent() {
        return this.mPackage == null && this.mComponent == null && isImageCaptureIntent();
    }

    public void fixUris(int contentUserHint) {
        Uri output;
        Uri data = getData();
        if (data != null) {
            this.mData = ContentProvider.maybeAddUserId(data, contentUserHint);
        }
        ClipData clipData = this.mClipData;
        if (clipData != null) {
            clipData.fixUris(contentUserHint);
        }
        String action = getAction();
        if (ACTION_SEND.equals(action)) {
            Uri stream = (Uri) getParcelableExtra(EXTRA_STREAM, Uri.class);
            if (stream != null) {
                putExtra(EXTRA_STREAM, ContentProvider.maybeAddUserId(stream, contentUserHint));
            }
        } else if (ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> streams = getParcelableArrayListExtra(EXTRA_STREAM, Uri.class);
            if (streams != null) {
                ArrayList<Uri> newStreams = new ArrayList<>();
                for (int i = 0; i < streams.size(); i++) {
                    newStreams.add(ContentProvider.maybeAddUserId(streams.get(i), contentUserHint));
                }
                putParcelableArrayListExtra(EXTRA_STREAM, newStreams);
            }
        } else if (isImageCaptureIntent() && (output = (Uri) getParcelableExtra("output", Uri.class)) != null) {
            putExtra("output", ContentProvider.maybeAddUserId(output, contentUserHint));
        }
    }

    public boolean migrateExtraStreamToClipData() {
        return migrateExtraStreamToClipData(AppGlobals.getInitialApplication());
    }

    public boolean migrateExtraStreamToClipData(Context context) {
        Bundle bundle = this.mExtras;
        if ((bundle == null || !bundle.isParcelled()) && getClipData() == null) {
            String action = getAction();
            if (ACTION_CHOOSER.equals(action)) {
                boolean migrated = false;
                try {
                    Intent intent = (Intent) getParcelableExtra(EXTRA_INTENT, Intent.class);
                    if (intent != null) {
                        migrated = false | intent.migrateExtraStreamToClipData(context);
                    }
                } catch (ClassCastException e) {
                }
                try {
                    Parcelable[] intents = getParcelableArrayExtra(EXTRA_INITIAL_INTENTS);
                    if (intents != null) {
                        for (Parcelable parcelable : intents) {
                            Intent intent2 = (Intent) parcelable;
                            if (intent2 != null) {
                                migrated |= intent2.migrateExtraStreamToClipData(context);
                            }
                        }
                    }
                } catch (ClassCastException e2) {
                }
                return migrated;
            }
            if (ACTION_SEND.equals(action)) {
                try {
                    Uri stream = (Uri) getParcelableExtra(EXTRA_STREAM, Uri.class);
                    CharSequence text = getCharSequenceExtra(EXTRA_TEXT);
                    String htmlText = getStringExtra(EXTRA_HTML_TEXT);
                    if (stream == null && text == null && htmlText == null) {
                    }
                    setClipData(new ClipData(null, new String[]{getType()}, new ClipData.Item(text, htmlText, null, stream)));
                    addFlags(1);
                    return true;
                } catch (ClassCastException e3) {
                }
            } else if (ACTION_SEND_MULTIPLE.equals(action)) {
                try {
                    ArrayList<Uri> streams = getParcelableArrayListExtra(EXTRA_STREAM, Uri.class);
                    ArrayList<CharSequence> texts = getCharSequenceArrayListExtra(EXTRA_TEXT);
                    ArrayList<String> htmlTexts = getStringArrayListExtra(EXTRA_HTML_TEXT);
                    int num = -1;
                    if (streams != null) {
                        num = streams.size();
                    }
                    if (texts != null) {
                        if (num >= 0 && num != texts.size()) {
                            return false;
                        }
                        num = texts.size();
                    }
                    if (htmlTexts != null) {
                        if (num >= 0 && num != htmlTexts.size()) {
                            return false;
                        }
                        num = htmlTexts.size();
                    }
                    if (num > 0) {
                        ClipData clipData = new ClipData(null, new String[]{getType()}, makeClipItem(streams, texts, htmlTexts, 0));
                        for (int i = 1; i < num; i++) {
                            clipData.addItem(makeClipItem(streams, texts, htmlTexts, i));
                        }
                        setClipData(clipData);
                        addFlags(1);
                        return true;
                    }
                } catch (ClassCastException e4) {
                }
            } else if (isImageCaptureIntent()) {
                try {
                    Uri output = (Uri) getParcelableExtra("output", Uri.class);
                    if (output != null) {
                        Uri output2 = maybeConvertFileToContentUri(context, output);
                        putExtra("output", output2);
                        setClipData(ClipData.newRawUri("", output2));
                        addFlags(3);
                        return true;
                    }
                } catch (ClassCastException e5) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    private Uri maybeConvertFileToContentUri(Context context, Uri uri) {
        if ("file".equals(uri.getScheme()) && context.getApplicationInfo().targetSdkVersion < 30) {
            File file = new File(uri.getPath());
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                uri = MediaStore.scanFile(context.getContentResolver(), new File(uri.getPath()));
                if (uri != null) {
                    return uri;
                }
            } catch (IOException e) {
                Log.m109e(TAG, "Ignoring failure to create file " + file, e);
            }
        }
        return uri;
    }

    public static String dockStateToString(int dock) {
        switch (dock) {
            case 0:
                return "EXTRA_DOCK_STATE_UNDOCKED";
            case 1:
                return "EXTRA_DOCK_STATE_DESK";
            case 2:
                return "EXTRA_DOCK_STATE_CAR";
            case 3:
                return "EXTRA_DOCK_STATE_LE_DESK";
            case 4:
                return "EXTRA_DOCK_STATE_HE_DESK";
            default:
                return Integer.toString(dock);
        }
    }

    private static ClipData.Item makeClipItem(ArrayList<Uri> streams, ArrayList<CharSequence> texts, ArrayList<String> htmlTexts, int which) {
        Uri uri = streams != null ? streams.get(which) : null;
        CharSequence text = texts != null ? texts.get(which) : null;
        String htmlText = htmlTexts != null ? htmlTexts.get(which) : null;
        return new ClipData.Item(text, htmlText, null, uri);
    }

    public boolean isDocument() {
        return (this.mFlags & 524288) == 524288;
    }
}
