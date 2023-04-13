package android.p008os;

import android.annotation.SystemApi;
import android.app.PropertyInvalidatedCache;
import android.content.Context;
import android.content.p001pm.PackageParser;
import android.p008os.IPowerManager;
import android.p008os.IThermalStatusListener;
import android.p008os.IWakeLockCallback;
import android.p008os.PowerManager;
import android.service.dreams.Sandman;
import android.sysprop.InitProperties;
import android.telephony.ims.SipDelegateImsConfiguration;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.internal.C4057R;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
/* renamed from: android.os.PowerManager */
/* loaded from: classes3.dex */
public final class PowerManager {
    @Deprecated
    public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
    public static final String ACTION_DEVICE_IDLE_MODE_CHANGED = "android.os.action.DEVICE_IDLE_MODE_CHANGED";
    public static final String ACTION_DEVICE_LIGHT_IDLE_MODE_CHANGED = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED";
    public static final String ACTION_ENHANCED_DISCHARGE_PREDICTION_CHANGED = "android.os.action.ENHANCED_DISCHARGE_PREDICTION_CHANGED";
    @Deprecated
    public static final String ACTION_LIGHT_DEVICE_IDLE_MODE_CHANGED = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED";
    public static final String ACTION_LOW_POWER_STANDBY_ENABLED_CHANGED = "android.os.action.LOW_POWER_STANDBY_ENABLED_CHANGED";
    public static final String ACTION_LOW_POWER_STANDBY_POLICY_CHANGED = "android.os.action.LOW_POWER_STANDBY_POLICY_CHANGED";
    @SystemApi
    public static final String ACTION_LOW_POWER_STANDBY_PORTS_CHANGED = "android.os.action.LOW_POWER_STANDBY_PORTS_CHANGED";
    public static final String ACTION_POWER_SAVE_MODE_CHANGED = "android.os.action.POWER_SAVE_MODE_CHANGED";
    public static final String ACTION_POWER_SAVE_MODE_CHANGED_INTERNAL = "android.os.action.POWER_SAVE_MODE_CHANGED_INTERNAL";
    public static final String ACTION_POWER_SAVE_TEMP_WHITELIST_CHANGED = "android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED";
    public static final String ACTION_POWER_SAVE_WHITELIST_CHANGED = "android.os.action.POWER_SAVE_WHITELIST_CHANGED";
    public static final int BRIGHTNESS_CONSTRAINT_TYPE_DEFAULT = 2;
    public static final int BRIGHTNESS_CONSTRAINT_TYPE_DIM = 3;
    public static final int BRIGHTNESS_CONSTRAINT_TYPE_DOZE = 4;
    public static final int BRIGHTNESS_CONSTRAINT_TYPE_MAXIMUM = 1;
    public static final int BRIGHTNESS_CONSTRAINT_TYPE_MINIMUM = 0;
    public static final int BRIGHTNESS_DEFAULT = -1;
    public static final int BRIGHTNESS_INVALID = -1;
    public static final float BRIGHTNESS_INVALID_FLOAT = Float.NaN;
    public static final float BRIGHTNESS_MAX = 1.0f;
    public static final float BRIGHTNESS_MIN = 0.0f;
    public static final int BRIGHTNESS_OFF = 0;
    public static final float BRIGHTNESS_OFF_FLOAT = -1.0f;
    public static final int BRIGHTNESS_ON = 255;
    private static final String CACHE_KEY_IS_INTERACTIVE_PROPERTY = "cache_key.is_interactive";
    private static final String CACHE_KEY_IS_POWER_SAVE_MODE_PROPERTY = "cache_key.is_power_save_mode";
    public static final int DOZE_WAKE_LOCK = 64;
    public static final int DRAW_WAKE_LOCK = 128;
    public static final String FEATURE_WAKE_ON_LAN_IN_LOW_POWER_STANDBY = "com.android.lowpowerstandby.WAKE_ON_LAN";
    @Deprecated
    public static final int FULL_WAKE_LOCK = 26;
    public static final int GO_TO_SLEEP_FLAG_NO_DOZE = 1;
    public static final int GO_TO_SLEEP_FLAG_SOFT_SLEEP = 2;
    public static final int GO_TO_SLEEP_REASON_ACCESSIBILITY = 7;
    public static final int GO_TO_SLEEP_REASON_APPLICATION = 0;
    public static final int GO_TO_SLEEP_REASON_DEVICE_ADMIN = 1;
    public static final int GO_TO_SLEEP_REASON_DEVICE_FOLD = 13;
    public static final int GO_TO_SLEEP_REASON_DISPLAY_GROUPS_TURNED_OFF = 12;
    public static final int GO_TO_SLEEP_REASON_DISPLAY_GROUP_REMOVED = 11;
    public static final int GO_TO_SLEEP_REASON_FORCE_SUSPEND = 8;
    public static final int GO_TO_SLEEP_REASON_HDMI = 5;
    public static final int GO_TO_SLEEP_REASON_INATTENTIVE = 9;
    public static final int GO_TO_SLEEP_REASON_LID_SWITCH = 3;
    public static final int GO_TO_SLEEP_REASON_MAX = 13;
    public static final int GO_TO_SLEEP_REASON_MIN = 0;
    public static final int GO_TO_SLEEP_REASON_POWER_BUTTON = 4;
    public static final int GO_TO_SLEEP_REASON_QUIESCENT = 10;
    public static final int GO_TO_SLEEP_REASON_SLEEP_BUTTON = 6;
    public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
    public static final int LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF = 2;
    public static final int LOCATION_MODE_FOREGROUND_ONLY = 3;
    public static final int LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF = 1;
    public static final int LOCATION_MODE_NO_CHANGE = 0;
    public static final int LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF = 4;
    public static final int LOW_POWER_STANDBY_ALLOWED_REASON_ONGOING_CALL = 4;
    public static final int LOW_POWER_STANDBY_ALLOWED_REASON_TEMP_POWER_SAVE_ALLOWLIST = 2;
    public static final int LOW_POWER_STANDBY_ALLOWED_REASON_VOICE_INTERACTION = 1;
    private static final int MAX_CACHE_ENTRIES = 1;
    public static final int MAX_LOCATION_MODE = 4;
    public static final int MAX_SOUND_TRIGGER_MODE = 2;
    private static final int MINIMUM_HEADROOM_TIME_MILLIS = 500;
    public static final int MIN_LOCATION_MODE = 0;
    public static final int MIN_SOUND_TRIGGER_MODE = 0;
    public static final int ON_AFTER_RELEASE = 536870912;
    public static final int PARTIAL_WAKE_LOCK = 1;
    @SystemApi
    public static final int POWER_SAVE_MODE_TRIGGER_DYNAMIC = 1;
    @SystemApi
    public static final int POWER_SAVE_MODE_TRIGGER_PERCENTAGE = 0;
    public static final int PRE_IDLE_TIMEOUT_MODE_LONG = 1;
    public static final int PRE_IDLE_TIMEOUT_MODE_NORMAL = 0;
    public static final int PRE_IDLE_TIMEOUT_MODE_SHORT = 2;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final String REBOOT_QUIESCENT = "quiescent";
    public static final String REBOOT_RECOVERY = "recovery";
    public static final String REBOOT_RECOVERY_UPDATE = "recovery-update";
    public static final String REBOOT_REQUESTED_BY_DEVICE_OWNER = "deviceowner";
    public static final String REBOOT_SAFE_MODE = "safemode";
    @SystemApi
    public static final String REBOOT_USERSPACE = "userspace";
    public static final int RELEASE_FLAG_TIMEOUT = 65536;
    public static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY = 1;
    @Deprecated
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    @Deprecated
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    public static final String SHUTDOWN_BATTERY_THERMAL_STATE = "thermal,battery";
    public static final String SHUTDOWN_LOW_BATTERY = "battery";
    public static final int SHUTDOWN_REASON_BATTERY_THERMAL = 6;
    public static final int SHUTDOWN_REASON_LOW_BATTERY = 5;
    public static final int SHUTDOWN_REASON_REBOOT = 2;
    public static final int SHUTDOWN_REASON_SHUTDOWN = 1;
    public static final int SHUTDOWN_REASON_THERMAL_SHUTDOWN = 4;
    public static final int SHUTDOWN_REASON_UNKNOWN = 0;
    public static final int SHUTDOWN_REASON_USER_REQUESTED = 3;
    public static final String SHUTDOWN_THERMAL_STATE = "thermal";
    public static final String SHUTDOWN_USER_REQUESTED = "userrequested";
    @SystemApi
    public static final int SOUND_TRIGGER_MODE_ALL_DISABLED = 2;
    @SystemApi
    public static final int SOUND_TRIGGER_MODE_ALL_ENABLED = 0;
    @SystemApi
    public static final int SOUND_TRIGGER_MODE_CRITICAL_ONLY = 1;
    public static final int SYSTEM_WAKELOCK = Integer.MIN_VALUE;
    private static final String TAG = "PowerManager";
    public static final int THERMAL_STATUS_CRITICAL = 4;
    public static final int THERMAL_STATUS_EMERGENCY = 5;
    public static final int THERMAL_STATUS_LIGHT = 1;
    public static final int THERMAL_STATUS_MODERATE = 2;
    public static final int THERMAL_STATUS_NONE = 0;
    public static final int THERMAL_STATUS_SEVERE = 3;
    public static final int THERMAL_STATUS_SHUTDOWN = 6;
    public static final int UNIMPORTANT_FOR_LOGGING = 1073741824;
    @SystemApi
    public static final int USER_ACTIVITY_EVENT_ACCESSIBILITY = 3;
    public static final int USER_ACTIVITY_EVENT_ATTENTION = 4;
    @SystemApi
    public static final int USER_ACTIVITY_EVENT_BUTTON = 1;
    public static final int USER_ACTIVITY_EVENT_DEVICE_STATE = 6;
    public static final int USER_ACTIVITY_EVENT_FACE_DOWN = 5;
    @SystemApi
    public static final int USER_ACTIVITY_EVENT_OTHER = 0;
    @SystemApi
    public static final int USER_ACTIVITY_EVENT_TOUCH = 2;
    @SystemApi
    public static final int USER_ACTIVITY_FLAG_INDIRECT = 2;
    @SystemApi
    public static final int USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS = 1;
    public static final int WAKE_LOCK_LEVEL_MASK = 65535;
    public static final int WAKE_REASON_APPLICATION = 2;
    public static final int WAKE_REASON_BIOMETRIC = 17;
    public static final int WAKE_REASON_CAMERA_LAUNCH = 5;
    public static final int WAKE_REASON_DISPLAY_GROUP_ADDED = 10;
    public static final int WAKE_REASON_DISPLAY_GROUP_TURNED_ON = 11;
    public static final int WAKE_REASON_DREAM_FINISHED = 13;
    public static final int WAKE_REASON_GESTURE = 4;
    public static final int WAKE_REASON_HDMI = 8;
    public static final int WAKE_REASON_LID = 9;
    public static final int WAKE_REASON_LIFT = 16;
    public static final int WAKE_REASON_PLUGGED_IN = 3;
    public static final int WAKE_REASON_POWER_BUTTON = 1;
    public static final int WAKE_REASON_TAP = 15;
    public static final int WAKE_REASON_TILT = 14;
    public static final int WAKE_REASON_UNFOLD_DEVICE = 12;
    public static final int WAKE_REASON_UNKNOWN = 0;
    public static final int WAKE_REASON_WAKE_KEY = 6;
    public static final int WAKE_REASON_WAKE_MOTION = 7;
    final Context mContext;
    final Handler mHandler;
    private PowerExemptionManager mPowerExemptionManager;
    final IPowerManager mService;
    final IThermalService mThermalService;
    private final PropertyInvalidatedCache<Void, Boolean> mPowerSaveModeCache = new PropertyInvalidatedCache<Void, Boolean>(1, CACHE_KEY_IS_POWER_SAVE_MODE_PROPERTY) { // from class: android.os.PowerManager.1
        @Override // android.app.PropertyInvalidatedCache
        public Boolean recompute(Void query) {
            try {
                return Boolean.valueOf(PowerManager.this.mService.isPowerSaveMode());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    };
    private final PropertyInvalidatedCache<Void, Boolean> mInteractiveCache = new PropertyInvalidatedCache<Void, Boolean>(1, CACHE_KEY_IS_INTERACTIVE_PROPERTY) { // from class: android.os.PowerManager.2
        @Override // android.app.PropertyInvalidatedCache
        public Boolean recompute(Void query) {
            try {
                return Boolean.valueOf(PowerManager.this.mService.isInteractive());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    };
    private final ArrayMap<OnThermalStatusChangedListener, IThermalStatusListener> mListenerMap = new ArrayMap<>();
    private final AtomicLong mLastHeadroomUpdate = new AtomicLong(0);

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$AutoPowerSaveModeTriggers */
    /* loaded from: classes3.dex */
    public @interface AutoPowerSaveModeTriggers {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$BrightnessConstraint */
    /* loaded from: classes3.dex */
    public @interface BrightnessConstraint {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$GoToSleepReason */
    /* loaded from: classes3.dex */
    public @interface GoToSleepReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$LocationPowerSaveMode */
    /* loaded from: classes3.dex */
    public @interface LocationPowerSaveMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$LowPowerStandbyAllowedReason */
    /* loaded from: classes3.dex */
    public @interface LowPowerStandbyAllowedReason {
    }

    /* renamed from: android.os.PowerManager$OnThermalStatusChangedListener */
    /* loaded from: classes3.dex */
    public interface OnThermalStatusChangedListener {
        void onThermalStatusChanged(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$ServiceType */
    /* loaded from: classes3.dex */
    public @interface ServiceType {
        public static final int ANIMATION = 3;
        public static final int AOD = 14;
        public static final int BATTERY_STATS = 9;
        public static final int DATA_SAVER = 10;
        public static final int FORCE_ALL_APPS_STANDBY = 11;
        public static final int FORCE_BACKGROUND_CHECK = 12;
        public static final int FULL_BACKUP = 4;
        public static final int KEYVALUE_BACKUP = 5;
        public static final int LOCATION = 1;
        public static final int NETWORK_FIREWALL = 6;
        public static final int NIGHT_MODE = 16;
        public static final int NULL = 0;
        public static final int OPTIONAL_SENSORS = 13;
        public static final int QUICK_DOZE = 15;
        public static final int SCREEN_BRIGHTNESS = 7;
        public static final int SOUND = 8;
        public static final int VIBRATION = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$ShutdownReason */
    /* loaded from: classes3.dex */
    public @interface ShutdownReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$SoundTriggerPowerSaveMode */
    /* loaded from: classes3.dex */
    public @interface SoundTriggerPowerSaveMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$ThermalStatus */
    /* loaded from: classes3.dex */
    public @interface ThermalStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$UserActivityEvent */
    /* loaded from: classes3.dex */
    public @interface UserActivityEvent {
    }

    /* renamed from: android.os.PowerManager$WakeLockStateListener */
    /* loaded from: classes3.dex */
    public interface WakeLockStateListener {
        void onStateChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.PowerManager$WakeReason */
    /* loaded from: classes3.dex */
    public @interface WakeReason {
    }

    public static String userActivityEventToString(int userActivityEvent) {
        switch (userActivityEvent) {
            case 0:
                return "other";
            case 1:
                return "button";
            case 2:
                return "touch";
            case 3:
                return Context.ACCESSIBILITY_SERVICE;
            case 4:
                return Context.ATTENTION_SERVICE;
            case 5:
                return "faceDown";
            case 6:
                return "deviceState";
            default:
                return Integer.toString(userActivityEvent);
        }
    }

    public static String sleepReasonToString(int sleepReason) {
        switch (sleepReason) {
            case 0:
                return PackageParser.TAG_APPLICATION;
            case 1:
                return "device_admin";
            case 2:
                return GpsNetInitiatedHandler.NI_INTENT_KEY_TIMEOUT;
            case 3:
                return "lid_switch";
            case 4:
                return "power_button";
            case 5:
                return "hdmi";
            case 6:
                return "sleep_button";
            case 7:
                return Context.ACCESSIBILITY_SERVICE;
            case 8:
                return "force_suspend";
            case 9:
                return "inattentive";
            case 10:
                return REBOOT_QUIESCENT;
            case 11:
                return "display_group_removed";
            case 12:
                return "display_groups_turned_off";
            case 13:
                return "device_folded";
            default:
                return Integer.toString(sleepReason);
        }
    }

    public static String wakeReasonToString(int wakeReason) {
        switch (wakeReason) {
            case 0:
                return "WAKE_REASON_UNKNOWN";
            case 1:
                return "WAKE_REASON_POWER_BUTTON";
            case 2:
                return "WAKE_REASON_APPLICATION";
            case 3:
                return "WAKE_REASON_PLUGGED_IN";
            case 4:
                return "WAKE_REASON_GESTURE";
            case 5:
                return "WAKE_REASON_CAMERA_LAUNCH";
            case 6:
                return "WAKE_REASON_WAKE_KEY";
            case 7:
                return "WAKE_REASON_WAKE_MOTION";
            case 8:
                return "WAKE_REASON_HDMI";
            case 9:
                return "WAKE_REASON_LID";
            case 10:
                return "WAKE_REASON_DISPLAY_GROUP_ADDED";
            case 11:
                return "WAKE_REASON_DISPLAY_GROUP_TURNED_ON";
            case 12:
                return "WAKE_REASON_UNFOLD_DEVICE";
            case 13:
                return "WAKE_REASON_DREAM_FINISHED";
            case 14:
                return "WAKE_REASON_TILT";
            case 15:
                return "WAKE_REASON_TAP";
            case 16:
                return "WAKE_REASON_LIFT";
            case 17:
                return "WAKE_REASON_BIOMETRIC";
            default:
                return Integer.toString(wakeReason);
        }
    }

    /* renamed from: android.os.PowerManager$WakeData */
    /* loaded from: classes3.dex */
    public static class WakeData {
        public final long sleepDurationRealtime;
        public final int wakeReason;
        public final long wakeTime;

        public WakeData(long wakeTime, int wakeReason, long sleepDurationRealtime) {
            this.wakeTime = wakeTime;
            this.wakeReason = wakeReason;
            this.sleepDurationRealtime = sleepDurationRealtime;
        }

        public boolean equals(Object o) {
            if (o instanceof WakeData) {
                WakeData other = (WakeData) o;
                return this.wakeTime == other.wakeTime && this.wakeReason == other.wakeReason && this.sleepDurationRealtime == other.sleepDurationRealtime;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.wakeTime), Integer.valueOf(this.wakeReason), Long.valueOf(this.sleepDurationRealtime));
        }
    }

    /* renamed from: android.os.PowerManager$SleepData */
    /* loaded from: classes3.dex */
    public static class SleepData {
        public final int goToSleepReason;
        public final long goToSleepUptimeMillis;

        public SleepData(long goToSleepUptimeMillis, int goToSleepReason) {
            this.goToSleepUptimeMillis = goToSleepUptimeMillis;
            this.goToSleepReason = goToSleepReason;
        }

        public boolean equals(Object o) {
            if (o instanceof SleepData) {
                SleepData other = (SleepData) o;
                return this.goToSleepUptimeMillis == other.goToSleepUptimeMillis && this.goToSleepReason == other.goToSleepReason;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.goToSleepUptimeMillis), Integer.valueOf(this.goToSleepReason));
        }
    }

    public static String locationPowerSaveModeToString(int mode) {
        switch (mode) {
            case 0:
                return "NO_CHANGE";
            case 1:
                return "GPS_DISABLED_WHEN_SCREEN_OFF";
            case 2:
                return "ALL_DISABLED_WHEN_SCREEN_OFF";
            case 3:
                return "FOREGROUND_ONLY";
            case 4:
                return "THROTTLE_REQUESTS_WHEN_SCREEN_OFF";
            default:
                return Integer.toString(mode);
        }
    }

    public PowerManager(Context context, IPowerManager service, IThermalService thermalService, Handler handler) {
        this.mContext = context;
        this.mService = service;
        this.mThermalService = thermalService;
        this.mHandler = handler;
    }

    private PowerExemptionManager getPowerExemptionManager() {
        if (this.mPowerExemptionManager == null) {
            this.mPowerExemptionManager = (PowerExemptionManager) this.mContext.getSystemService(PowerExemptionManager.class);
        }
        return this.mPowerExemptionManager;
    }

    public int getMinimumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(C4057R.integer.config_screenBrightnessSettingMinimum);
    }

    public int getMaximumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(C4057R.integer.config_screenBrightnessSettingMaximum);
    }

    public int getDefaultScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(C4057R.integer.config_screenBrightnessSettingDefault);
    }

    public float getBrightnessConstraint(int constraint) {
        try {
            return this.mService.getBrightnessConstraint(constraint);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        validateWakeLockParameters(levelAndFlags, tag);
        return new WakeLock(levelAndFlags, tag, this.mContext.getOpPackageName(), -1);
    }

    public WakeLock newWakeLock(int levelAndFlags, String tag, int displayId) {
        validateWakeLockParameters(levelAndFlags, tag);
        return new WakeLock(levelAndFlags, tag, this.mContext.getOpPackageName(), displayId);
    }

    public static void validateWakeLockParameters(int levelAndFlags, String tag) {
        switch (65535 & levelAndFlags) {
            case 1:
            case 6:
            case 10:
            case 26:
            case 32:
            case 64:
            case 128:
                if (tag == null) {
                    throw new IllegalArgumentException("The tag must not be null.");
                }
                return;
            default:
                throw new IllegalArgumentException("Must specify a valid wake lock level.");
        }
    }

    @Deprecated
    public void userActivity(long when, boolean noChangeLights) {
        userActivity(when, 0, noChangeLights ? 1 : 0);
    }

    @SystemApi
    public void userActivity(long when, int event, int flags) {
        try {
            this.mService.userActivity(this.mContext.getDisplayId(), when, event, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void goToSleep(long time) {
        goToSleep(time, 0, 0);
    }

    public void goToSleep(long time, int reason, int flags) {
        try {
            this.mService.goToSleep(time, reason, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void goToSleep(int displayId, long time, int reason, int flags) {
        try {
            this.mService.goToSleepWithDisplayId(displayId, time, reason, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void wakeUp(long time) {
        wakeUp(time, 0, "wakeUp");
    }

    @Deprecated
    public void wakeUp(long time, String details) {
        wakeUp(time, 0, details);
    }

    public void wakeUp(long time, int reason, String details) {
        try {
            this.mService.wakeUp(time, reason, details, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void nap(long time) {
        try {
            this.mService.nap(time);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void dream(long time) {
        Sandman.startDreamByUserRequest(this.mContext);
    }

    public void boostScreenBrightness(long time) {
        try {
            this.mService.boostScreenBrightness(time);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isWakeLockLevelSupported(int level) {
        try {
            return this.mService.isWakeLockLevelSupported(level);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean isScreenOn() {
        return isInteractive();
    }

    public boolean isInteractive() {
        return this.mInteractiveCache.query(null).booleanValue();
    }

    public static boolean isRebootingUserspaceSupportedImpl() {
        return InitProperties.is_userspace_reboot_supported().orElse(false).booleanValue();
    }

    public boolean isRebootingUserspaceSupported() {
        return isRebootingUserspaceSupportedImpl();
    }

    public void reboot(String reason) {
        if (REBOOT_USERSPACE.equals(reason) && !isRebootingUserspaceSupported()) {
            throw new UnsupportedOperationException("Attempted userspace reboot on a device that doesn't support it");
        }
        try {
            this.mService.reboot(false, reason, true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void rebootSafeMode() {
        try {
            this.mService.rebootSafeMode(false, true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean areAutoPowerSaveModesEnabled() {
        try {
            return this.mService.areAutoPowerSaveModesEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isPowerSaveMode() {
        return this.mPowerSaveModeCache.query(null).booleanValue();
    }

    @SystemApi
    public boolean setPowerSaveModeEnabled(boolean mode) {
        try {
            return this.mService.setPowerSaveModeEnabled(mode);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public BatterySaverPolicyConfig getFullPowerSavePolicy() {
        try {
            return this.mService.getFullPowerSavePolicy();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig config) {
        try {
            return this.mService.setFullPowerSavePolicy(config);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) {
        try {
            return this.mService.setDynamicPowerSaveHint(powerSaveHint, disableThreshold);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) {
        try {
            return this.mService.setAdaptivePowerSavePolicy(config);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean setAdaptivePowerSaveEnabled(boolean enabled) {
        try {
            return this.mService.setAdaptivePowerSaveEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getPowerSaveModeTrigger() {
        try {
            return this.mService.getPowerSaveModeTrigger();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setBatteryDischargePrediction(Duration timeRemaining, boolean isPersonalized) {
        if (timeRemaining == null) {
            throw new IllegalArgumentException("time remaining must not be null");
        }
        try {
            this.mService.setBatteryDischargePrediction(new ParcelDuration(timeRemaining), isPersonalized);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Duration getBatteryDischargePrediction() {
        try {
            ParcelDuration parcelDuration = this.mService.getBatteryDischargePrediction();
            if (parcelDuration == null) {
                return null;
            }
            return parcelDuration.getDuration();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isBatteryDischargePredictionPersonalized() {
        try {
            return this.mService.isBatteryDischargePredictionPersonalized();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public PowerSaveState getPowerSaveState(int serviceType) {
        try {
            return this.mService.getPowerSaveState(serviceType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getLocationPowerSaveMode() {
        PowerSaveState powerSaveState = getPowerSaveState(1);
        if (!powerSaveState.batterySaverEnabled) {
            return 0;
        }
        return powerSaveState.locationMode;
    }

    public int getSoundTriggerPowerSaveMode() {
        PowerSaveState powerSaveState = getPowerSaveState(8);
        if (!powerSaveState.batterySaverEnabled) {
            return 0;
        }
        return powerSaveState.soundTriggerMode;
    }

    public boolean isDeviceIdleMode() {
        try {
            return this.mService.isDeviceIdleMode();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isDeviceLightIdleMode() {
        try {
            return this.mService.isLightDeviceIdleMode();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean isLightDeviceIdleMode() {
        return isDeviceLightIdleMode();
    }

    @SystemApi
    public boolean isLowPowerStandbySupported() {
        try {
            return this.mService.isLowPowerStandbySupported();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isLowPowerStandbyEnabled() {
        try {
            return this.mService.isLowPowerStandbyEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setLowPowerStandbyEnabled(boolean enabled) {
        try {
            this.mService.setLowPowerStandbyEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setLowPowerStandbyActiveDuringMaintenance(boolean activeDuringMaintenance) {
        try {
            this.mService.setLowPowerStandbyActiveDuringMaintenance(activeDuringMaintenance);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void forceLowPowerStandbyActive(boolean active) {
        try {
            this.mService.forceLowPowerStandbyActive(active);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setLowPowerStandbyPolicy(LowPowerStandbyPolicy policy) {
        try {
            this.mService.setLowPowerStandbyPolicy(LowPowerStandbyPolicy.toParcelable(policy));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public LowPowerStandbyPolicy getLowPowerStandbyPolicy() {
        try {
            return LowPowerStandbyPolicy.fromParcelable(this.mService.getLowPowerStandbyPolicy());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isExemptFromLowPowerStandby() {
        try {
            return this.mService.isExemptFromLowPowerStandby();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAllowedInLowPowerStandby(int reason) {
        try {
            return this.mService.isReasonAllowedInLowPowerStandby(reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAllowedInLowPowerStandby(String feature) {
        try {
            return this.mService.isFeatureAllowedInLowPowerStandby(feature);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public LowPowerStandbyPortsLock newLowPowerStandbyPortsLock(List<LowPowerStandbyPortDescription> ports) {
        LowPowerStandbyPortsLock standbyPorts = new LowPowerStandbyPortsLock(ports);
        return standbyPorts;
    }

    @SystemApi
    public List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() {
        try {
            return LowPowerStandbyPortDescription.fromParcelable(this.mService.getActiveLowPowerStandbyPorts());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isIgnoringBatteryOptimizations(String packageName) {
        return getPowerExemptionManager().isAllowListed(packageName, true);
    }

    public void shutdown(boolean confirm, String reason, boolean wait) {
        try {
            this.mService.shutdown(confirm, reason, wait);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isSustainedPerformanceModeSupported() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_sustainedPerformanceModeSupported);
    }

    public int getCurrentThermalStatus() {
        try {
            return this.mThermalService.getCurrentThermalStatus();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addThermalStatusListener(OnThermalStatusChangedListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addThermalStatusListener(this.mContext.getMainExecutor(), listener);
    }

    public void addThermalStatusListener(Executor executor, OnThermalStatusChangedListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        Objects.requireNonNull(executor, "executor cannot be null");
        Preconditions.checkArgument(!this.mListenerMap.containsKey(listener), "Listener already registered: %s", listener);
        IThermalStatusListener internalListener = new IThermalStatusListener$StubC22113(executor, listener);
        try {
            if (this.mThermalService.registerThermalStatusListener(internalListener)) {
                this.mListenerMap.put(listener, internalListener);
                return;
            }
            throw new RuntimeException("Listener failed to set");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.PowerManager$3 */
    /* loaded from: classes3.dex */
    public class IThermalStatusListener$StubC22113 extends IThermalStatusListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ OnThermalStatusChangedListener val$listener;

        IThermalStatusListener$StubC22113(Executor executor, OnThermalStatusChangedListener onThermalStatusChangedListener) {
            this.val$executor = executor;
            this.val$listener = onThermalStatusChangedListener;
        }

        @Override // android.p008os.IThermalStatusListener
        public void onStatusChange(final int status) {
            long token = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final OnThermalStatusChangedListener onThermalStatusChangedListener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.os.PowerManager$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PowerManager.OnThermalStatusChangedListener.this.onThermalStatusChanged(status);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public void removeThermalStatusListener(OnThermalStatusChangedListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        IThermalStatusListener internalListener = this.mListenerMap.get(listener);
        Preconditions.checkArgument(internalListener != null, "Listener was not added");
        try {
            if (this.mThermalService.unregisterThermalStatusListener(internalListener)) {
                this.mListenerMap.remove(listener);
                return;
            }
            throw new RuntimeException("Listener failed to remove");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public float getThermalHeadroom(int forecastSeconds) {
        long now = SystemClock.elapsedRealtime();
        long timeSinceLastUpdate = now - this.mLastHeadroomUpdate.get();
        if (timeSinceLastUpdate < 500) {
            return Float.NaN;
        }
        try {
            float forecast = this.mThermalService.getThermalHeadroom(forecastSeconds);
            this.mLastHeadroomUpdate.set(SystemClock.elapsedRealtime());
            return forecast;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setDozeAfterScreenOff(boolean dozeAfterScreenOf) {
        try {
            this.mService.setDozeAfterScreenOff(dozeAfterScreenOf);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isAmbientDisplayAvailable() {
        try {
            return this.mService.isAmbientDisplayAvailable();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void suppressAmbientDisplay(String token, boolean suppress) {
        try {
            this.mService.suppressAmbientDisplay(token, suppress);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isAmbientDisplaySuppressedForToken(String token) {
        try {
            return this.mService.isAmbientDisplaySuppressedForToken(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isAmbientDisplaySuppressed() {
        try {
            return this.mService.isAmbientDisplaySuppressed();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAmbientDisplaySuppressedForTokenByApp(String token, int appUid) {
        try {
            return this.mService.isAmbientDisplaySuppressedForTokenByApp(token, appUid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getLastShutdownReason() {
        try {
            return this.mService.getLastShutdownReason();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getLastSleepReason() {
        try {
            return this.mService.getLastSleepReason();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean forceSuspend() {
        try {
            return this.mService.forceSuspend();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static String lowPowerStandbyAllowedReasonsToString(int allowedReasons) {
        ArrayList<String> allowedStrings = new ArrayList<>();
        if ((allowedReasons & 1) != 0) {
            allowedStrings.add("ALLOWED_REASON_VOICE_INTERACTION");
            allowedReasons &= -2;
        }
        if ((allowedReasons & 2) != 0) {
            allowedStrings.add("ALLOWED_REASON_TEMP_POWER_SAVE_ALLOWLIST");
            allowedReasons &= -3;
        }
        if ((allowedReasons & 4) != 0) {
            allowedStrings.add("ALLOWED_REASON_ONGOING_CALL");
            allowedReasons &= -5;
        }
        if (allowedReasons != 0) {
            allowedStrings.add(String.valueOf(allowedReasons));
        }
        return String.join(",", allowedStrings);
    }

    @SystemApi
    /* renamed from: android.os.PowerManager$LowPowerStandbyPolicy */
    /* loaded from: classes3.dex */
    public static final class LowPowerStandbyPolicy {
        private final Set<String> mAllowedFeatures;
        private final int mAllowedReasons;
        private final Set<String> mExemptPackages;
        private final String mIdentifier;

        public LowPowerStandbyPolicy(String identifier, Set<String> exemptPackages, int allowedReasons, Set<String> allowedFeatures) {
            Objects.requireNonNull(identifier);
            Objects.requireNonNull(exemptPackages);
            Objects.requireNonNull(allowedFeatures);
            this.mIdentifier = identifier;
            this.mExemptPackages = Collections.unmodifiableSet(exemptPackages);
            this.mAllowedReasons = allowedReasons;
            this.mAllowedFeatures = Collections.unmodifiableSet(allowedFeatures);
        }

        public String getIdentifier() {
            return this.mIdentifier;
        }

        public Set<String> getExemptPackages() {
            return this.mExemptPackages;
        }

        public int getAllowedReasons() {
            return this.mAllowedReasons;
        }

        public Set<String> getAllowedFeatures() {
            return this.mAllowedFeatures;
        }

        public String toString() {
            return "Policy{mIdentifier='" + this.mIdentifier + DateFormat.QUOTE + ", mExemptPackages=" + String.join(",", this.mExemptPackages) + ", mAllowedReasons=" + PowerManager.lowPowerStandbyAllowedReasonsToString(this.mAllowedReasons) + ", mAllowedFeatures=" + String.join(",", this.mAllowedFeatures) + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof LowPowerStandbyPolicy) {
                LowPowerStandbyPolicy that = (LowPowerStandbyPolicy) o;
                return this.mAllowedReasons == that.mAllowedReasons && Objects.equals(this.mIdentifier, that.mIdentifier) && Objects.equals(this.mExemptPackages, that.mExemptPackages) && Objects.equals(this.mAllowedFeatures, that.mAllowedFeatures);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mIdentifier, this.mExemptPackages, Integer.valueOf(this.mAllowedReasons), this.mAllowedFeatures);
        }

        public static IPowerManager.LowPowerStandbyPolicy toParcelable(LowPowerStandbyPolicy policy) {
            if (policy == null) {
                return null;
            }
            IPowerManager.LowPowerStandbyPolicy parcelablePolicy = new IPowerManager.LowPowerStandbyPolicy();
            parcelablePolicy.identifier = policy.mIdentifier;
            parcelablePolicy.exemptPackages = new ArrayList(policy.mExemptPackages);
            parcelablePolicy.allowedReasons = policy.mAllowedReasons;
            parcelablePolicy.allowedFeatures = new ArrayList(policy.mAllowedFeatures);
            return parcelablePolicy;
        }

        public static LowPowerStandbyPolicy fromParcelable(IPowerManager.LowPowerStandbyPolicy parcelablePolicy) {
            if (parcelablePolicy == null) {
                return null;
            }
            return new LowPowerStandbyPolicy(parcelablePolicy.identifier, new ArraySet(parcelablePolicy.exemptPackages), parcelablePolicy.allowedReasons, new ArraySet(parcelablePolicy.allowedFeatures));
        }
    }

    @SystemApi
    /* renamed from: android.os.PowerManager$LowPowerStandbyPortDescription */
    /* loaded from: classes3.dex */
    public static final class LowPowerStandbyPortDescription {
        public static final int MATCH_PORT_LOCAL = 1;
        public static final int MATCH_PORT_REMOTE = 2;
        public static final int PROTOCOL_TCP = 6;
        public static final int PROTOCOL_UDP = 17;
        private final InetAddress mLocalAddress;
        private final int mPortMatcher;
        private final int mPortNumber;
        private final int mProtocol;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.PowerManager$LowPowerStandbyPortDescription$PortMatcher */
        /* loaded from: classes3.dex */
        public @interface PortMatcher {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.PowerManager$LowPowerStandbyPortDescription$Protocol */
        /* loaded from: classes3.dex */
        public @interface Protocol {
        }

        public LowPowerStandbyPortDescription(int protocol, int portMatcher, int portNumber) {
            this.mProtocol = protocol;
            this.mPortMatcher = portMatcher;
            this.mPortNumber = portNumber;
            this.mLocalAddress = null;
        }

        public LowPowerStandbyPortDescription(int protocol, int portMatcher, int portNumber, InetAddress localAddress) {
            this.mProtocol = protocol;
            this.mPortMatcher = portMatcher;
            this.mPortNumber = portNumber;
            this.mLocalAddress = localAddress;
        }

        private String protocolToString(int protocol) {
            switch (protocol) {
                case 6:
                    return SipDelegateImsConfiguration.SIP_TRANSPORT_TCP;
                case 17:
                    return SipDelegateImsConfiguration.SIP_TRANSPORT_UDP;
                default:
                    return String.valueOf(protocol);
            }
        }

        private String portMatcherToString(int portMatcher) {
            switch (portMatcher) {
                case 1:
                    return "MATCH_PORT_LOCAL";
                case 2:
                    return "MATCH_PORT_REMOTE";
                default:
                    return String.valueOf(portMatcher);
            }
        }

        public int getProtocol() {
            return this.mProtocol;
        }

        public int getPortMatcher() {
            return this.mPortMatcher;
        }

        public int getPortNumber() {
            return this.mPortNumber;
        }

        public InetAddress getLocalAddress() {
            return this.mLocalAddress;
        }

        public String toString() {
            return "PortDescription{mProtocol=" + protocolToString(this.mProtocol) + ", mPortMatcher=" + portMatcherToString(this.mPortMatcher) + ", mPortNumber=" + this.mPortNumber + ", mLocalAddress=" + this.mLocalAddress + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof LowPowerStandbyPortDescription) {
                LowPowerStandbyPortDescription that = (LowPowerStandbyPortDescription) o;
                return this.mProtocol == that.mProtocol && this.mPortMatcher == that.mPortMatcher && this.mPortNumber == that.mPortNumber && Objects.equals(this.mLocalAddress, that.mLocalAddress);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mProtocol), Integer.valueOf(this.mPortMatcher), Integer.valueOf(this.mPortNumber), this.mLocalAddress);
        }

        public static IPowerManager.LowPowerStandbyPortDescription toParcelable(LowPowerStandbyPortDescription portDescription) {
            if (portDescription == null) {
                return null;
            }
            IPowerManager.LowPowerStandbyPortDescription parcelablePortDescription = new IPowerManager.LowPowerStandbyPortDescription();
            parcelablePortDescription.protocol = portDescription.mProtocol;
            parcelablePortDescription.portMatcher = portDescription.mPortMatcher;
            parcelablePortDescription.portNumber = portDescription.mPortNumber;
            InetAddress inetAddress = portDescription.mLocalAddress;
            if (inetAddress != null) {
                parcelablePortDescription.localAddress = inetAddress.getAddress();
            }
            return parcelablePortDescription;
        }

        public static List<IPowerManager.LowPowerStandbyPortDescription> toParcelable(List<LowPowerStandbyPortDescription> portDescriptions) {
            if (portDescriptions == null) {
                return null;
            }
            ArrayList<IPowerManager.LowPowerStandbyPortDescription> result = new ArrayList<>();
            for (LowPowerStandbyPortDescription port : portDescriptions) {
                result.add(toParcelable(port));
            }
            return result;
        }

        public static LowPowerStandbyPortDescription fromParcelable(IPowerManager.LowPowerStandbyPortDescription parcelablePortDescription) {
            if (parcelablePortDescription == null) {
                return null;
            }
            InetAddress localAddress = null;
            if (parcelablePortDescription.localAddress != null) {
                try {
                    localAddress = InetAddress.getByAddress(parcelablePortDescription.localAddress);
                } catch (UnknownHostException e) {
                    Log.m103w(PowerManager.TAG, "Address has invalid length", e);
                }
            }
            return new LowPowerStandbyPortDescription(parcelablePortDescription.protocol, parcelablePortDescription.portMatcher, parcelablePortDescription.portNumber, localAddress);
        }

        public static List<LowPowerStandbyPortDescription> fromParcelable(List<IPowerManager.LowPowerStandbyPortDescription> portDescriptions) {
            if (portDescriptions == null) {
                return null;
            }
            ArrayList<LowPowerStandbyPortDescription> result = new ArrayList<>();
            for (IPowerManager.LowPowerStandbyPortDescription port : portDescriptions) {
                result.add(fromParcelable(port));
            }
            return result;
        }
    }

    @SystemApi
    /* renamed from: android.os.PowerManager$LowPowerStandbyPortsLock */
    /* loaded from: classes3.dex */
    public final class LowPowerStandbyPortsLock {
        private boolean mHeld;
        private final List<LowPowerStandbyPortDescription> mPorts;
        private final IBinder mToken = new Binder();

        LowPowerStandbyPortsLock(List<LowPowerStandbyPortDescription> ports) {
            this.mPorts = ports;
        }

        public void acquire() {
            synchronized (this.mToken) {
                try {
                    try {
                        PowerManager.this.mService.acquireLowPowerStandbyPorts(this.mToken, LowPowerStandbyPortDescription.toParcelable(this.mPorts));
                        this.mHeld = true;
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void release() {
            synchronized (this.mToken) {
                try {
                    try {
                        PowerManager.this.mService.releaseLowPowerStandbyPorts(this.mToken);
                        this.mHeld = false;
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        protected void finalize() {
            synchronized (this.mToken) {
                if (this.mHeld) {
                    Log.wtf(PowerManager.TAG, "LowPowerStandbyPorts finalized while still held");
                    release();
                }
            }
        }
    }

    /* renamed from: android.os.PowerManager$WakeLock */
    /* loaded from: classes3.dex */
    public final class WakeLock {
        private IWakeLockCallback mCallback;
        private final int mDisplayId;
        private int mExternalCount;
        private int mFlags;
        private boolean mHeld;
        private String mHistoryTag;
        private int mInternalCount;
        private WakeLockStateListener mListener;
        private final String mPackageName;
        private String mTag;
        private int mTagHash;
        private WorkSource mWorkSource;
        private boolean mRefCounted = true;
        private final Runnable mReleaser = new Runnable() { // from class: android.os.PowerManager$WakeLock$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PowerManager.WakeLock.this.lambda$new$0();
            }
        };
        private final IBinder mToken = new Binder();

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            release(65536);
        }

        WakeLock(int flags, String tag, String packageName, int displayId) {
            this.mFlags = flags;
            this.mTag = tag;
            this.mTagHash = tag.hashCode();
            this.mPackageName = packageName;
            this.mDisplayId = displayId;
        }

        protected void finalize() throws Throwable {
            synchronized (this.mToken) {
                if (this.mHeld) {
                    Log.wtf(PowerManager.TAG, "WakeLock finalized while still held: " + this.mTag);
                    Trace.asyncTraceForTrackEnd(131072L, "WakeLocks", this.mTagHash);
                    try {
                        PowerManager.this.mService.releaseWakeLock(this.mToken, 0);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }

        public void setReferenceCounted(boolean value) {
            synchronized (this.mToken) {
                this.mRefCounted = value;
            }
        }

        public void acquire() {
            synchronized (this.mToken) {
                acquireLocked();
            }
        }

        public void acquire(long timeout) {
            synchronized (this.mToken) {
                acquireLocked();
                PowerManager.this.mHandler.postDelayed(this.mReleaser, timeout);
            }
        }

        private void acquireLocked() {
            int i = this.mInternalCount + 1;
            this.mInternalCount = i;
            this.mExternalCount++;
            if (!this.mRefCounted || i == 1) {
                PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
                Trace.asyncTraceForTrackBegin(131072L, "WakeLocks", this.mTag, this.mTagHash);
                try {
                    PowerManager.this.mService.acquireWakeLock(this.mToken, this.mFlags, this.mTag, this.mPackageName, this.mWorkSource, this.mHistoryTag, this.mDisplayId, this.mCallback);
                    this.mHeld = true;
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }

        public void release() {
            release(0);
        }

        public void release(int flags) {
            synchronized (this.mToken) {
                int i = this.mInternalCount;
                if (i > 0) {
                    this.mInternalCount = i - 1;
                }
                if ((65536 & flags) == 0) {
                    this.mExternalCount--;
                }
                if (!this.mRefCounted || this.mInternalCount == 0) {
                    PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
                    if (this.mHeld) {
                        Trace.asyncTraceForTrackEnd(131072L, "WakeLocks", this.mTagHash);
                        try {
                            PowerManager.this.mService.releaseWakeLock(this.mToken, flags);
                            this.mHeld = false;
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
                if (this.mRefCounted && this.mExternalCount < 0) {
                    throw new RuntimeException("WakeLock under-locked " + this.mTag);
                }
            }
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mToken) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource ws) {
            synchronized (this.mToken) {
                if (ws != null) {
                    try {
                        if (ws.isEmpty()) {
                            ws = null;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                boolean changed = true;
                if (ws == null) {
                    if (this.mWorkSource == null) {
                        changed = false;
                    }
                    this.mWorkSource = null;
                } else {
                    WorkSource workSource = this.mWorkSource;
                    if (workSource != null) {
                        changed = true ^ workSource.equals(ws);
                        if (changed) {
                            this.mWorkSource.set(ws);
                        }
                    } else {
                        changed = true;
                        this.mWorkSource = new WorkSource(ws);
                    }
                }
                if (changed && this.mHeld) {
                    try {
                        PowerManager.this.mService.updateWakeLockWorkSource(this.mToken, this.mWorkSource, this.mHistoryTag);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }

        public void setTag(String tag) {
            this.mTag = tag;
            this.mTagHash = tag.hashCode();
        }

        public String getTag() {
            return this.mTag;
        }

        public void setHistoryTag(String tag) {
            this.mHistoryTag = tag;
        }

        public void setUnimportantForLogging(boolean state) {
            if (!state) {
                this.mFlags &= -1073741825;
            } else {
                this.mFlags |= 1073741824;
            }
        }

        public String toString() {
            String str;
            synchronized (this.mToken) {
                str = "WakeLock{" + Integer.toHexString(System.identityHashCode(this)) + " held=" + this.mHeld + ", refCount=" + this.mInternalCount + "}";
            }
            return str;
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            synchronized (this.mToken) {
                long token = proto.start(fieldId);
                proto.write(1138166333441L, this.mTag);
                proto.write(1138166333442L, this.mPackageName);
                proto.write(1133871366147L, this.mHeld);
                proto.write(1120986464260L, this.mInternalCount);
                WorkSource workSource = this.mWorkSource;
                if (workSource != null) {
                    workSource.dumpDebug(proto, 1146756268037L);
                }
                proto.end(token);
            }
        }

        public Runnable wrap(final Runnable r) {
            acquire();
            return new Runnable() { // from class: android.os.PowerManager$WakeLock$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PowerManager.WakeLock.this.lambda$wrap$1(r);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$wrap$1(Runnable r) {
            try {
                r.run();
            } finally {
                release();
            }
        }

        public void setStateListener(Executor executor, WakeLockStateListener listener) {
            Preconditions.checkNotNull(executor, "executor cannot be null");
            synchronized (this.mToken) {
                if (listener != this.mListener) {
                    this.mListener = listener;
                    if (listener != null) {
                        this.mCallback = new IWakeLockCallback$StubC22121(executor, listener);
                    } else {
                        this.mCallback = null;
                    }
                    if (this.mHeld) {
                        try {
                            PowerManager.this.mService.updateWakeLockCallback(this.mToken, this.mCallback);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
            }
        }

        /* renamed from: android.os.PowerManager$WakeLock$1 */
        /* loaded from: classes3.dex */
        class IWakeLockCallback$StubC22121 extends IWakeLockCallback.Stub {
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ WakeLockStateListener val$listener;

            IWakeLockCallback$StubC22121(Executor executor, WakeLockStateListener wakeLockStateListener) {
                this.val$executor = executor;
                this.val$listener = wakeLockStateListener;
            }

            @Override // android.p008os.IWakeLockCallback
            public void onStateChanged(final boolean enabled) {
                long token = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final WakeLockStateListener wakeLockStateListener = this.val$listener;
                    executor.execute(new Runnable() { // from class: android.os.PowerManager$WakeLock$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PowerManager.WakeLockStateListener.this.onStateChanged(enabled);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }
    }

    public static void invalidatePowerSaveModeCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_IS_POWER_SAVE_MODE_PROPERTY);
    }

    public static void invalidateIsInteractiveCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_IS_INTERACTIVE_PROPERTY);
    }
}
