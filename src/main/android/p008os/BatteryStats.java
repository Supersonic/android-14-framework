package android.p008os;

import android.app.backup.FullBackup;
import android.app.blob.XmlTags;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.hardware.gnss.GnssSignalType;
import android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback;
import android.hardware.p005tv.tuner.FrontendInnerFec;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.location.LocationManager;
import android.media.AudioSystem;
import android.media.audio.common.AudioDeviceDescription;
import android.p008os.BatteryStats;
import android.security.Credentials;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.MutableBoolean;
import android.util.Pair;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseDoubleArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import com.android.internal.C4057R;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.p028os.BatteryStatsHistoryIterator;
import com.android.internal.telephony.DctConstants;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* renamed from: android.os.BatteryStats */
/* loaded from: classes3.dex */
public abstract class BatteryStats {
    private static final String AGGREGATED_WAKELOCK_DATA = "awl";
    public static final int AGGREGATED_WAKE_TYPE_PARTIAL = 20;
    private static final String APK_DATA = "apk";
    private static final String AUDIO_DATA = "aud";
    public static final int AUDIO_TURNED_ON = 15;
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final int BATTERY_STATS_CHECKIN_VERSION = 9;
    private static final String BLUETOOTH_CONTROLLER_DATA = "ble";
    private static final String BLUETOOTH_MISC_DATA = "blem";
    public static final int BLUETOOTH_SCAN_ON = 19;
    public static final int BLUETOOTH_UNOPTIMIZED_SCAN_ON = 21;
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final String CAMERA_DATA = "cam";
    public static final int CAMERA_TURNED_ON = 17;
    private static final String CELLULAR_CONTROLLER_NAME = "Cellular";
    private static final String CHARGE_STEP_DATA = "csd";
    private static final String CHARGE_TIME_REMAIN_DATA = "ctr";
    private static final String[] CHECKIN_POWER_COMPONENT_LABELS;
    static final int CHECKIN_VERSION = 36;
    private static final String CPU_DATA = "cpu";
    private static final String CPU_TIMES_AT_FREQ_DATA = "ctf";
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    public static final int DATA_CONNECTION_EMERGENCY_SERVICE;
    static final String[] DATA_CONNECTION_NAMES;
    public static final int DATA_CONNECTION_OTHER;
    public static final int DATA_CONNECTION_OUT_OF_SERVICE = 0;
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    public static final int DEVICE_IDLE_MODE_DEEP = 2;
    public static final int DEVICE_IDLE_MODE_LIGHT = 1;
    public static final int DEVICE_IDLE_MODE_OFF = 0;
    private static final String DISCHARGE_STEP_DATA = "dsd";
    private static final String DISCHARGE_TIME_REMAIN_DATA = "dtr";
    public static final int DUMP_CHARGED_ONLY = 2;
    public static final int DUMP_DAILY_ONLY = 4;
    public static final int DUMP_DEVICE_WIFI_ONLY = 64;
    public static final int DUMP_HISTORY_ONLY = 8;
    public static final int DUMP_INCLUDE_HISTORY = 16;
    public static final int DUMP_VERBOSE = 32;
    public static final long DURATION_UNAVAILABLE = -1;
    private static final String FLASHLIGHT_DATA = "fla";
    public static final int FLASHLIGHT_TURNED_ON = 16;
    public static final int FOREGROUND_ACTIVITY = 10;
    private static final String FOREGROUND_ACTIVITY_DATA = "fg";
    public static final int FOREGROUND_SERVICE = 22;
    private static final String FOREGROUND_SERVICE_DATA = "fgs";
    public static final int FULL_WIFI_LOCK = 5;
    private static final String GLOBAL_BLUETOOTH_CONTROLLER_DATA = "gble";
    private static final String GLOBAL_CPU_FREQ_DATA = "gcf";
    private static final String GLOBAL_MODEM_CONTROLLER_DATA = "gmcd";
    private static final String GLOBAL_NETWORK_DATA = "gn";
    private static final String GLOBAL_WIFI_CONTROLLER_DATA = "gwfcd";
    private static final String GLOBAL_WIFI_DATA = "gwfl";
    private static final String HISTORY_DATA = "h";
    public static final String[] HISTORY_EVENT_CHECKIN_NAMES;
    public static final IntToString[] HISTORY_EVENT_INT_FORMATTERS;
    public static final String[] HISTORY_EVENT_NAMES;
    public static final BitDescription[] HISTORY_STATE2_DESCRIPTIONS;
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS;
    private static final String HISTORY_STRING_POOL = "hsp";
    public static final int JOB = 14;
    private static final String JOBS_DEFERRED_DATA = "jbd";
    private static final String JOB_COMPLETION_DATA = "jbc";
    private static final String JOB_DATA = "jb";
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final boolean LOCAL_LOGV = false;
    public static final int MAX_TRACKED_SCREEN_STATE = 4;
    public static final double MILLISECONDS_IN_HOUR = 3600000.0d;
    private static final String MISC_DATA = "m";
    private static final String MODEM_CONTROLLER_DATA = "mcd";
    public static final int NETWORK_BT_RX_DATA = 4;
    public static final int NETWORK_BT_TX_DATA = 5;
    private static final String NETWORK_DATA = "nt";
    public static final int NETWORK_MOBILE_BG_RX_DATA = 6;
    public static final int NETWORK_MOBILE_BG_TX_DATA = 7;
    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_BG_RX_DATA = 8;
    public static final int NETWORK_WIFI_BG_TX_DATA = 9;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    public static final int NUM_DATA_CONNECTION_TYPES;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 10;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    public static final int NUM_WIFI_SIGNAL_STRENGTH_BINS = 5;
    public static final long POWER_DATA_UNAVAILABLE = -1;
    private static final String POWER_USE_ITEM_DATA = "pwi";
    private static final String POWER_USE_SUMMARY_DATA = "pws";
    private static final String PROCESS_DATA = "pr";
    public static final int PROCESS_STATE = 12;
    public static final int RADIO_ACCESS_TECHNOLOGY_COUNT = 3;
    public static final int RADIO_ACCESS_TECHNOLOGY_LTE = 1;
    public static final String[] RADIO_ACCESS_TECHNOLOGY_NAMES;
    public static final int RADIO_ACCESS_TECHNOLOGY_NR = 2;
    public static final int RADIO_ACCESS_TECHNOLOGY_OTHER = 0;
    private static final String RESOURCE_POWER_MANAGER_DATA = "rpm";
    public static final String RESULT_RECEIVER_CONTROLLER_KEY = "controller_activity";
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    static final String[] SCREEN_BRIGHTNESS_NAMES;
    static final String[] SCREEN_BRIGHTNESS_SHORT_NAMES;
    protected static final boolean SCREEN_OFF_RPM_STATS_ENABLED = false;
    public static final int SENSOR = 3;
    private static final String SENSOR_DATA = "sr";
    public static final String SERVICE_NAME = "batterystats";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    private static final String STATE_TIME_DATA = "st";
    @Deprecated
    public static final int STATS_CURRENT = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    @Deprecated
    public static final int STATS_SINCE_UNPLUGGED = 2;
    public static final long STEP_LEVEL_INITIAL_MODE_MASK = 71776119061217280L;
    public static final int STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
    public static final long STEP_LEVEL_LEVEL_MASK = 280375465082880L;
    public static final int STEP_LEVEL_LEVEL_SHIFT = 40;
    public static final int[] STEP_LEVEL_MODES_OF_INTEREST;
    public static final int STEP_LEVEL_MODE_DEVICE_IDLE = 8;
    public static final String[] STEP_LEVEL_MODE_LABELS;
    public static final int STEP_LEVEL_MODE_POWER_SAVE = 4;
    public static final int STEP_LEVEL_MODE_SCREEN_STATE = 3;
    public static final int[] STEP_LEVEL_MODE_VALUES;
    public static final long STEP_LEVEL_MODIFIED_MODE_MASK = -72057594037927936L;
    public static final int STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
    public static final long STEP_LEVEL_TIME_MASK = 1099511627775L;
    public static final int SYNC = 13;
    private static final String SYNC_DATA = "sy";
    private static final String TAG = "BatteryStats";
    private static final String UID_DATA = "uid";
    public static final String UID_TIMES_TYPE_ALL = "A";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String VERSION_DATA = "vers";
    private static final String VIBRATOR_DATA = "vib";
    public static final int VIBRATOR_ON = 9;
    private static final String VIDEO_DATA = "vid";
    public static final int VIDEO_TURNED_ON = 8;
    private static final String WAKELOCK_DATA = "wl";
    private static final String WAKEUP_ALARM_DATA = "wua";
    private static final String WAKEUP_REASON_DATA = "wr";
    public static final int WAKE_TYPE_DRAW = 18;
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int WIFI_AGGREGATE_MULTICAST_ENABLED = 23;
    public static final int WIFI_BATCHED_SCAN = 11;
    private static final String WIFI_CONTROLLER_DATA = "wfcd";
    private static final String WIFI_CONTROLLER_NAME = "WiFi";
    private static final String WIFI_DATA = "wfl";
    private static final String WIFI_MULTICAST_DATA = "wmc";
    public static final int WIFI_MULTICAST_ENABLED = 7;
    private static final String WIFI_MULTICAST_TOTAL_DATA = "wmct";
    public static final int WIFI_RUNNING = 4;
    public static final int WIFI_SCAN = 6;
    private static final String WIFI_SIGNAL_STRENGTH_COUNT_DATA = "wsgc";
    private static final String WIFI_SIGNAL_STRENGTH_TIME_DATA = "wsgt";
    private static final String WIFI_STATE_COUNT_DATA = "wsc";
    static final String[] WIFI_STATE_NAMES;
    private static final String WIFI_STATE_TIME_DATA = "wst";
    private static final String WIFI_SUPPL_STATE_COUNT_DATA = "wssc";
    static final String[] WIFI_SUPPL_STATE_NAMES;
    static final String[] WIFI_SUPPL_STATE_SHORT_NAMES;
    private static final String WIFI_SUPPL_STATE_TIME_DATA = "wsst";
    private static final IntToString sIntToString;
    private static final IntToString sUidToString;
    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;
    private static final String[] STAT_NAMES = {XmlTags.TAG_LEASEE, "c", XmlTags.ATTR_UID};
    public static final long[] JOB_FRESHNESS_BUCKETS = {3600000, 7200000, 14400000, 28800000, Long.MAX_VALUE};

    /* renamed from: android.os.BatteryStats$ControllerActivityCounter */
    /* loaded from: classes3.dex */
    public static abstract class ControllerActivityCounter {
        public abstract LongCounter getIdleTimeCounter();

        public abstract LongCounter getMonitoredRailChargeConsumedMaMs();

        public abstract LongCounter getPowerCounter();

        public abstract LongCounter getRxTimeCounter();

        public abstract LongCounter getScanTimeCounter();

        public abstract LongCounter getSleepTimeCounter();

        public abstract LongCounter[] getTxTimeCounters();
    }

    /* renamed from: android.os.BatteryStats$Counter */
    /* loaded from: classes3.dex */
    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    /* renamed from: android.os.BatteryStats$DailyItem */
    /* loaded from: classes3.dex */
    public static final class DailyItem {
        public LevelStepTracker mChargeSteps;
        public LevelStepTracker mDischargeSteps;
        public long mEndTime;
        public ArrayList<PackageChange> mPackageChanges;
        public long mStartTime;
    }

    @FunctionalInterface
    /* renamed from: android.os.BatteryStats$IntToString */
    /* loaded from: classes3.dex */
    public interface IntToString {
        String applyAsString(int i);
    }

    /* renamed from: android.os.BatteryStats$LongCounter */
    /* loaded from: classes3.dex */
    public static abstract class LongCounter {
        public abstract long getCountForProcessState(int i);

        public abstract long getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    /* renamed from: android.os.BatteryStats$LongCounterArray */
    /* loaded from: classes3.dex */
    public static abstract class LongCounterArray {
        public abstract long[] getCountsLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    /* renamed from: android.os.BatteryStats$PackageChange */
    /* loaded from: classes3.dex */
    public static final class PackageChange {
        public String mPackageName;
        public boolean mUpdate;
        public long mVersionCode;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryStats$RadioAccessTechnology */
    /* loaded from: classes3.dex */
    public @interface RadioAccessTechnology {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryStats$StatName */
    /* loaded from: classes3.dex */
    public @interface StatName {
    }

    public abstract void commitCurrentHistoryBatchLocked();

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeBatteryScreenOffRealtime(long j, int i);

    public abstract long computeBatteryScreenOffUptime(long j, int i);

    public abstract long computeBatteryTimeRemaining(long j);

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeChargeTimeRemaining(long j);

    public abstract long computeRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public abstract long getActiveRadioDurationMs(int i, int i2, int i3, long j);

    public abstract long getActiveRxRadioDurationMs(int i, int i2, long j);

    public abstract long getActiveTxRadioDurationMs(int i, int i2, int i3, long j);

    public abstract long getBatteryRealtime(long j);

    public abstract long getBatteryUptime(long j);

    protected abstract BatteryUsageStats getBatteryUsageStats(Context context, boolean z);

    public abstract BluetoothBatteryStats getBluetoothBatteryStats();

    public abstract ControllerActivityCounter getBluetoothControllerActivity();

    public abstract long getBluetoothEnergyConsumptionUC();

    public abstract long getBluetoothScanTime(long j, int i);

    public abstract long getCameraEnergyConsumptionUC();

    public abstract long getCameraOnTime(long j, int i);

    public abstract LevelStepTracker getChargeLevelStepTracker();

    public abstract long getCpuEnergyConsumptionUC();

    public abstract int getCpuFreqCount();

    public abstract long[] getCpuFreqs();

    public abstract long getCurrentDailyStartTime();

    public abstract long[] getCustomEnergyConsumerBatteryConsumptionUC();

    public abstract String[] getCustomEnergyConsumerNames();

    public abstract LevelStepTracker getDailyChargeLevelStepTracker();

    public abstract LevelStepTracker getDailyDischargeLevelStepTracker();

    public abstract DailyItem getDailyItemLocked(int i);

    public abstract ArrayList<PackageChange> getDailyPackageChanges();

    public abstract int getDeviceIdleModeCount(int i, int i2);

    public abstract long getDeviceIdleModeTime(int i, long j, int i2);

    public abstract int getDeviceIdlingCount(int i, int i2);

    public abstract long getDeviceIdlingTime(int i, long j, int i2);

    public abstract int getDischargeAmount(int i);

    public abstract int getDischargeAmountScreenDoze();

    public abstract int getDischargeAmountScreenDozeSinceCharge();

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeCurrentLevel();

    public abstract LevelStepTracker getDischargeLevelStepTracker();

    public abstract int getDischargeStartLevel();

    public abstract int getDisplayCount();

    public abstract long getDisplayScreenBrightnessTime(int i, int i2, long j);

    public abstract long getDisplayScreenDozeTime(int i, long j);

    public abstract long getDisplayScreenOnTime(int i, long j);

    public abstract String getEndPlatformVersion();

    public abstract int getEstimatedBatteryCapacity();

    public abstract long getFlashlightOnCount(int i);

    public abstract long getFlashlightOnTime(long j, int i);

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract long getGnssEnergyConsumptionUC();

    public abstract long getGpsBatteryDrainMaMs();

    public abstract long getGpsSignalQualityTime(int i, long j, int i2);

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract int getHistoryStringPoolBytes();

    public abstract int getHistoryStringPoolSize();

    public abstract String getHistoryTagPoolString(int i);

    public abstract int getHistoryTagPoolUid(int i);

    public abstract int getHistoryTotalSize();

    public abstract int getHistoryUsedSize();

    public abstract long getInteractiveTime(long j, int i);

    public abstract boolean getIsOnBattery();

    public abstract LongSparseArray<? extends Timer> getKernelMemoryStats();

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract int getLearnedBatteryCapacity();

    public abstract long getLongestDeviceIdleModeTime(int i);

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract int getMaxLearnedBatteryCapacity();

    public abstract int getMinLearnedBatteryCapacity();

    public abstract long getMobileRadioActiveAdjustedTime(int i);

    public abstract int getMobileRadioActiveCount(int i);

    public abstract long getMobileRadioActiveTime(long j, int i);

    public abstract int getMobileRadioActiveUnknownCount(int i);

    public abstract long getMobileRadioActiveUnknownTime(int i);

    public abstract long getMobileRadioEnergyConsumptionUC();

    public abstract ControllerActivityCounter getModemControllerActivity();

    public abstract long getNetworkActivityBytes(int i, int i2);

    public abstract long getNetworkActivityPackets(int i, int i2);

    public abstract long getNextMaxDailyDeadline();

    public abstract long getNextMinDailyDeadline();

    public abstract int getNumConnectivityChange(int i);

    public abstract int getParcelVersion();

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract Timer getPhoneDataConnectionTimer(int i);

    public abstract long getPhoneEnergyConsumptionUC();

    public abstract int getPhoneOnCount(int i);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract Timer getPhoneSignalScanningTimer();

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    protected abstract Timer getPhoneSignalStrengthTimer(int i);

    public abstract int getPowerSaveModeEnabledCount(int i);

    public abstract long getPowerSaveModeEnabledTime(long j, int i);

    public abstract Map<String, ? extends Timer> getRpmStats();

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract Timer getScreenBrightnessTimer(int i);

    public abstract int getScreenDozeCount(int i);

    public abstract long getScreenDozeEnergyConsumptionUC();

    public abstract long getScreenDozeTime(long j, int i);

    public abstract Map<String, ? extends Timer> getScreenOffRpmStats();

    public abstract int getScreenOnCount(int i);

    public abstract long getScreenOnEnergyConsumptionUC();

    public abstract long getScreenOnTime(long j, int i);

    public abstract long getStartClockTime();

    public abstract int getStartCount();

    public abstract String getStartPlatformVersion();

    public abstract long getStatsStartRealtime();

    public abstract long[] getSystemServiceTimeAtCpuSpeeds();

    public abstract long getUahDischarge(int i);

    public abstract long getUahDischargeDeepDoze(int i);

    public abstract long getUahDischargeLightDoze(int i);

    public abstract long getUahDischargeScreenDoze(int i);

    public abstract long getUahDischargeScreenOff(int i);

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract WakeLockStats getWakeLockStats();

    public abstract Map<String, ? extends Timer> getWakeupReasonStats();

    public abstract long getWifiActiveTime(long j, int i);

    public abstract ControllerActivityCounter getWifiControllerActivity();

    public abstract long getWifiEnergyConsumptionUC();

    public abstract int getWifiMulticastWakelockCount(int i);

    public abstract long getWifiMulticastWakelockTime(long j, int i);

    public abstract long getWifiOnTime(long j, int i);

    public abstract int getWifiSignalStrengthCount(int i, int i2);

    public abstract long getWifiSignalStrengthTime(int i, long j, int i2);

    public abstract Timer getWifiSignalStrengthTimer(int i);

    public abstract int getWifiStateCount(int i, int i2);

    public abstract long getWifiStateTime(int i, long j, int i2);

    public abstract Timer getWifiStateTimer(int i);

    public abstract int getWifiSupplStateCount(int i, int i2);

    public abstract long getWifiSupplStateTime(int i, long j, int i2);

    public abstract Timer getWifiSupplStateTimer(int i);

    public abstract boolean hasBluetoothActivityReporting();

    public abstract boolean hasModemActivityReporting();

    public abstract boolean hasWifiActivityReporting();

    public abstract boolean isProcessStateDataAvailable();

    public abstract BatteryStatsHistoryIterator iterateBatteryStatsHistory();

    public BatteryStats() {
        StringBuilder sb = new StringBuilder(32);
        this.mFormatBuilder = sb;
        this.mFormatter = new Formatter(sb);
    }

    static {
        String[] strArr = {"dark", "dim", "medium", "light", "bright"};
        SCREEN_BRIGHTNESS_NAMES = strArr;
        String[] strArr2 = {AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, "1", "2", "3", "4"};
        SCREEN_BRIGHTNESS_SHORT_NAMES = strArr2;
        int length = TelephonyManager.getAllNetworkTypes().length + 1;
        DATA_CONNECTION_EMERGENCY_SERVICE = length;
        int i = length + 1;
        DATA_CONNECTION_OTHER = i;
        String[] strArr3 = {"oos", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", "hspap", "gsm", "td_scdma", "iwlan", "lte_ca", "nr", "emngcy", "other"};
        DATA_CONNECTION_NAMES = strArr3;
        NUM_DATA_CONNECTION_TYPES = i + 1;
        RADIO_ACCESS_TECHNOLOGY_NAMES = new String[]{"Other", DctConstants.RAT_NAME_LTE, "NR"};
        String[] strArr4 = {"invalid", "disconn", "disabled", "inactive", "scanning", "authenticating", "associating", "associated", "4-way-handshake", "group-handshake", "completed", "dormant", "uninit"};
        WIFI_SUPPL_STATE_NAMES = strArr4;
        String[] strArr5 = {"inv", "dsc", "dis", "inact", "scan", Context.AUTH_SERVICE, "ascing", "asced", "4-way", "group", "compl", "dorm", "uninit"};
        WIFI_SUPPL_STATE_SHORT_NAMES = strArr5;
        HISTORY_STATE_DESCRIPTIONS = new BitDescription[]{new BitDescription(Integer.MIN_VALUE, "running", "r"), new BitDescription(1073741824, "wake_lock", "w"), new BitDescription(8388608, Context.SENSOR_SERVICE, XmlTags.TAG_SESSION), new BitDescription(536870912, LocationManager.GPS_PROVIDER, "g"), new BitDescription(268435456, "wifi_full_lock", "Wl"), new BitDescription(134217728, "wifi_scan", "Ws"), new BitDescription(65536, "wifi_multicast", "Wm"), new BitDescription(67108864, "wifi_radio", "Wr"), new BitDescription(33554432, "mobile_radio", "Pr"), new BitDescription(2097152, "phone_scanning", "Psc"), new BitDescription(4194304, "audio", FullBackup.APK_TREE_TOKEN), new BitDescription(1048576, "screen", GnssSignalType.CODE_TYPE_S), new BitDescription(524288, BatteryManager.EXTRA_PLUGGED, "BP"), new BitDescription(262144, "screen_doze", "Sd"), new BitDescription(HistoryItem.STATE_DATA_CONNECTION_MASK, 9, "data_conn", "Pcn", strArr3, strArr3), new BitDescription(448, 6, "phone_state", "Pst", new String[]{"in", "out", "emergency", "off"}, new String[]{"in", "out", "em", "off"}), new BitDescription(56, 3, "phone_signal_strength", "Pss", new String[]{"none", "poor", "moderate", "good", "great"}, new String[]{AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, "1", "2", "3", "4"}), new BitDescription(7, 0, "brightness", "Sb", strArr, strArr2)};
        HISTORY_STATE2_DESCRIPTIONS = new BitDescription[]{new BitDescription(Integer.MIN_VALUE, "power_save", "ps"), new BitDescription(1073741824, "video", "v"), new BitDescription(536870912, "wifi_running", "Ww"), new BitDescription(268435456, "wifi", GnssSignalType.CODE_TYPE_W), new BitDescription(134217728, "flashlight", "fl"), new BitDescription(100663296, 25, "device_idle", "di", new String[]{"off", "light", "full", "???"}, new String[]{"off", "light", "full", "???"}), new BitDescription(16777216, "charging", "ch"), new BitDescription(262144, "usb_data", "Ud"), new BitDescription(8388608, "phone_in_call", "Pcl"), new BitDescription(4194304, "bluetooth", XmlTags.TAG_BLOB), new BitDescription(112, 4, "wifi_signal_strength", "Wss", new String[]{AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, "1", "2", "3", "4"}, new String[]{AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, "1", "2", "3", "4"}), new BitDescription(15, 0, "wifi_suppl", "Wsp", strArr4, strArr5), new BitDescription(2097152, Context.CAMERA_SERVICE, Credentials.CERTIFICATE_USAGE_CA), new BitDescription(1048576, "ble_scan", "bles"), new BitDescription(524288, "cellular_high_tx_power", "Chtp"), new BitDescription(128, 7, "gps_signal_quality", "Gss", new String[]{"poor", "good"}, new String[]{"poor", "good"})};
        HISTORY_EVENT_NAMES = new String[]{"null", "proc", FOREGROUND_ACTIVITY_DATA, "top", "sync", "wake_lock_in", "job", "user", "userfg", "conn", "active", "pkginst", "pkgunin", "alarm", Context.STATS_MANAGER, "pkginactive", "pkgactive", "tmpwhitelist", "screenwake", "wakeupap", "longwake", "est_capacity"};
        HISTORY_EVENT_CHECKIN_NAMES = new String[]{"Enl", "Epr", "Efg", "Etp", "Esy", "Ewl", "Ejb", "Eur", "Euf", "Ecn", "Eac", "Epi", "Epu", "Eal", "Est", "Eai", "Eaa", "Etw", "Esw", "Ewa", "Elw", "Eec"};
        IntToString intToString = new IntToString() { // from class: android.os.BatteryStats$$ExternalSyntheticLambda0
            @Override // android.p008os.BatteryStats.IntToString
            public final String applyAsString(int i2) {
                return UserHandle.formatUid(i2);
            }
        };
        sUidToString = intToString;
        IntToString intToString2 = new IntToString() { // from class: android.os.BatteryStats$$ExternalSyntheticLambda1
            @Override // android.p008os.BatteryStats.IntToString
            public final String applyAsString(int i2) {
                return Integer.toString(i2);
            }
        };
        sIntToString = intToString2;
        HISTORY_EVENT_INT_FORMATTERS = new IntToString[]{intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString2, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString2};
        WIFI_STATE_NAMES = new String[]{"off", "scanning", "no_net", "disconn", "sta", "p2p", "sta_p2p", "soft_ap"};
        STEP_LEVEL_MODES_OF_INTEREST = new int[]{7, 15, 11, 7, 7, 7, 7, 7, 15, 11};
        STEP_LEVEL_MODE_VALUES = new int[]{0, 4, 8, 1, 5, 2, 6, 3, 7, 11};
        STEP_LEVEL_MODE_LABELS = new String[]{"screen off", "screen off power save", "screen off device idle", "screen on", "screen on power save", "screen doze", "screen doze power save", "screen doze-suspend", "screen doze-suspend power save", "screen doze-suspend device idle"};
        String[] strArr6 = new String[18];
        CHECKIN_POWER_COMPONENT_LABELS = strArr6;
        strArr6[0] = "scrn";
        strArr6[1] = CPU_DATA;
        strArr6[2] = "blue";
        strArr6[3] = Context.CAMERA_SERVICE;
        strArr6[4] = "audio";
        strArr6[5] = "video";
        strArr6[6] = "flashlight";
        strArr6[8] = "cell";
        strArr6[9] = "sensors";
        strArr6[10] = "gnss";
        strArr6[11] = "wifi";
        strArr6[13] = "memory";
        strArr6[14] = "phone";
        strArr6[15] = "ambi";
        strArr6[16] = "idle";
    }

    /* renamed from: android.os.BatteryStats$Timer */
    /* loaded from: classes3.dex */
    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public abstract long getTimeSinceMarkLocked(long j);

        public abstract long getTotalTimeLocked(long j, int i);

        public abstract void logState(Printer printer, String str);

        public long getMaxDurationMsLocked(long elapsedRealtimeMs) {
            return -1L;
        }

        public long getCurrentDurationMsLocked(long elapsedRealtimeMs) {
            return -1L;
        }

        public long getTotalDurationMsLocked(long elapsedRealtimeMs) {
            return -1L;
        }

        public Timer getSubTimer() {
            return null;
        }

        public boolean isRunningLocked() {
            return false;
        }
    }

    public static int mapToInternalProcessState(int procState) {
        if (procState == 20) {
            return 7;
        }
        if (procState == 2) {
            return 0;
        }
        if (procState == 3) {
            return 3;
        }
        if (procState == 4 || procState == 5) {
            return 1;
        }
        if (procState <= 6) {
            return 2;
        }
        if (procState <= 11) {
            return 3;
        }
        if (procState <= 12) {
            return 4;
        }
        if (procState <= 13) {
            return 5;
        }
        return 6;
    }

    public static int mapUidProcessStateToBatteryConsumerProcessState(int processState) {
        switch (processState) {
            case 0:
            case 2:
                return 1;
            case 1:
                return 3;
            case 3:
            case 4:
                return 2;
            case 5:
            default:
                return 0;
            case 6:
                return 4;
        }
    }

    /* renamed from: android.os.BatteryStats$Uid */
    /* loaded from: classes3.dex */
    public static abstract class Uid {
        public static final int NUM_PROCESS_STATE = 7;
        public static final int NUM_USER_ACTIVITY_TYPES;
        public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
        public static final int PROCESS_STATE_BACKGROUND = 3;
        public static final int PROCESS_STATE_CACHED = 6;
        public static final int PROCESS_STATE_FOREGROUND = 2;
        public static final int PROCESS_STATE_FOREGROUND_SERVICE = 1;
        public static final int PROCESS_STATE_HEAVY_WEIGHT = 5;
        public static final int PROCESS_STATE_NONEXISTENT = 7;
        public static final int PROCESS_STATE_TOP = 0;
        public static final int PROCESS_STATE_TOP_SLEEPING = 4;
        static final String[] USER_ACTIVITY_TYPES;
        static final String[] PROCESS_STATE_NAMES = {"Top", "Fg Service", "Foreground", "Background", "Top Sleeping", "Heavy Weight", "Cached"};
        public static final String[] UID_PROCESS_TYPES = {"T", "FS", "F", GnssSignalType.CODE_TYPE_B, "TS", "HW", GnssSignalType.CODE_TYPE_C};

        /* renamed from: android.os.BatteryStats$Uid$Pkg */
        /* loaded from: classes3.dex */
        public static abstract class Pkg {

            /* renamed from: android.os.BatteryStats$Uid$Pkg$Serv */
            /* loaded from: classes3.dex */
            public static abstract class Serv {
                public abstract int getLaunches(int i);

                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);
            }

            public abstract ArrayMap<String, ? extends Serv> getServiceStats();

            public abstract ArrayMap<String, ? extends Counter> getWakeupAlarmStats();
        }

        /* renamed from: android.os.BatteryStats$Uid$Proc */
        /* loaded from: classes3.dex */
        public static abstract class Proc {

            /* renamed from: android.os.BatteryStats$Uid$Proc$ExcessivePower */
            /* loaded from: classes3.dex */
            public static class ExcessivePower {
                public static final int TYPE_CPU = 2;
                public static final int TYPE_WAKE = 1;
                public long overTime;
                public int type;
                public long usedTime;
            }

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);

            public abstract long getForegroundTime(int i);

            public abstract int getNumAnrs(int i);

            public abstract int getNumCrashes(int i);

            public abstract int getStarts(int i);

            public abstract long getSystemTime(int i);

            public abstract long getUserTime(int i);

            public abstract boolean isActive();
        }

        /* renamed from: android.os.BatteryStats$Uid$Sensor */
        /* loaded from: classes3.dex */
        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorBackgroundTime();

            public abstract Timer getSensorTime();
        }

        /* renamed from: android.os.BatteryStats$Uid$Wakelock */
        /* loaded from: classes3.dex */
        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);
        }

        public abstract Timer getAggregatedPartialWakelockTimer();

        public abstract Timer getAudioTurnedOnTimer();

        public abstract ControllerActivityCounter getBluetoothControllerActivity();

        public abstract long getBluetoothEnergyConsumptionUC();

        public abstract long getBluetoothEnergyConsumptionUC(int i);

        public abstract Timer getBluetoothScanBackgroundTimer();

        public abstract Counter getBluetoothScanResultBgCounter();

        public abstract Counter getBluetoothScanResultCounter();

        public abstract Timer getBluetoothScanTimer();

        public abstract Timer getBluetoothUnoptimizedScanBackgroundTimer();

        public abstract Timer getBluetoothUnoptimizedScanTimer();

        public abstract long getCameraEnergyConsumptionUC();

        public abstract Timer getCameraTurnedOnTimer();

        public abstract long getCpuActiveTime();

        public abstract long getCpuActiveTime(int i);

        public abstract long[] getCpuClusterTimes();

        public abstract long getCpuEnergyConsumptionUC();

        public abstract long getCpuEnergyConsumptionUC(int i);

        public abstract boolean getCpuFreqTimes(long[] jArr, int i);

        public abstract long[] getCpuFreqTimes(int i);

        public abstract long[] getCustomEnergyConsumerBatteryConsumptionUC();

        public abstract void getDeferredJobsCheckinLineLocked(StringBuilder sb, int i);

        public abstract void getDeferredJobsLineLocked(StringBuilder sb, int i);

        public abstract Timer getFlashlightTurnedOnTimer();

        public abstract Timer getForegroundActivityTimer();

        public abstract Timer getForegroundServiceTimer();

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract long getGnssEnergyConsumptionUC();

        public abstract ArrayMap<String, SparseIntArray> getJobCompletionStats();

        public abstract ArrayMap<String, ? extends Timer> getJobStats();

        public abstract int getMobileRadioActiveCount(int i);

        public abstract long getMobileRadioActiveTime(int i);

        public abstract long getMobileRadioActiveTimeInProcessState(int i);

        public abstract long getMobileRadioApWakeupCount(int i);

        public abstract long getMobileRadioEnergyConsumptionUC();

        public abstract long getMobileRadioEnergyConsumptionUC(int i);

        public abstract ControllerActivityCounter getModemControllerActivity();

        public abstract Timer getMulticastWakelockStats();

        public abstract long getNetworkActivityBytes(int i, int i2);

        public abstract long getNetworkActivityPackets(int i, int i2);

        public abstract ArrayMap<String, ? extends Pkg> getPackageStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract long getProcessStateTime(int i, long j, int i2);

        public abstract Timer getProcessStateTimer(int i);

        public abstract ArrayMap<String, ? extends Proc> getProcessStats();

        public abstract double getProportionalSystemServiceUsage();

        public abstract boolean getScreenOffCpuFreqTimes(long[] jArr, int i);

        public abstract long[] getScreenOffCpuFreqTimes(int i);

        public abstract long getScreenOnEnergyConsumptionUC();

        public abstract SparseArray<? extends Sensor> getSensorStats();

        public abstract ArrayMap<String, ? extends Timer> getSyncStats();

        public abstract long getSystemCpuTimeUs(int i);

        public abstract long getTimeAtCpuSpeed(int i, int i2, int i3);

        public abstract int getUid();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract long getUserCpuTimeUs(int i);

        public abstract Timer getVibratorOnTimer();

        public abstract Timer getVideoTurnedOnTimer();

        public abstract ArrayMap<String, ? extends Wakelock> getWakelockStats();

        public abstract int getWifiBatchedScanCount(int i, int i2);

        public abstract long getWifiBatchedScanTime(int i, long j, int i2);

        public abstract ControllerActivityCounter getWifiControllerActivity();

        public abstract long getWifiEnergyConsumptionUC();

        public abstract long getWifiEnergyConsumptionUC(int i);

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getWifiRadioApWakeupCount(int i);

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getWifiScanActualTime(long j);

        public abstract int getWifiScanBackgroundCount(int i);

        public abstract long getWifiScanBackgroundTime(long j);

        public abstract Timer getWifiScanBackgroundTimer();

        public abstract int getWifiScanCount(int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract Timer getWifiScanTimer();

        public abstract boolean hasNetworkActivity();

        public abstract boolean hasUserActivity();

        public abstract void noteActivityPausedLocked(long j);

        public abstract void noteActivityResumedLocked(long j);

        public abstract void noteFullWifiLockAcquiredLocked(long j);

        public abstract void noteFullWifiLockReleasedLocked(long j);

        public abstract void noteUserActivityLocked(int i);

        public abstract void noteWifiBatchedScanStartedLocked(int i, long j);

        public abstract void noteWifiBatchedScanStoppedLocked(long j);

        public abstract void noteWifiMulticastDisabledLocked(long j);

        public abstract void noteWifiMulticastEnabledLocked(long j);

        public abstract void noteWifiRunningLocked(long j);

        public abstract void noteWifiScanStartedLocked(long j);

        public abstract void noteWifiScanStoppedLocked(long j);

        public abstract void noteWifiStoppedLocked(long j);

        static {
            String[] strArr = {"other", "button", "touch", Context.ACCESSIBILITY_SERVICE, Context.ATTENTION_SERVICE, "faceDown", "deviceState"};
            USER_ACTIVITY_TYPES = strArr;
            NUM_USER_ACTIVITY_TYPES = strArr.length;
        }

        /* renamed from: android.os.BatteryStats$Uid$Pid */
        /* loaded from: classes3.dex */
        public class Pid {
            public int mWakeNesting;
            public long mWakeStartMs;
            public long mWakeSumMs;

            public Pid() {
            }
        }
    }

    /* renamed from: android.os.BatteryStats$LevelStepTracker */
    /* loaded from: classes3.dex */
    public static final class LevelStepTracker {
        public long mLastStepTime = -1;
        public int mNumStepDurations;
        public final long[] mStepDurations;

        public LevelStepTracker(int maxLevelSteps) {
            this.mStepDurations = new long[maxLevelSteps];
        }

        public LevelStepTracker(int numSteps, long[] steps) {
            this.mNumStepDurations = numSteps;
            long[] jArr = new long[numSteps];
            this.mStepDurations = jArr;
            System.arraycopy(steps, 0, jArr, 0, numSteps);
        }

        public long getDurationAt(int index) {
            return this.mStepDurations[index] & BatteryStats.STEP_LEVEL_TIME_MASK;
        }

        public int getLevelAt(int index) {
            return (int) ((this.mStepDurations[index] & BatteryStats.STEP_LEVEL_LEVEL_MASK) >> 40);
        }

        public int getInitModeAt(int index) {
            return (int) ((this.mStepDurations[index] & BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK) >> 48);
        }

        public int getModModeAt(int index) {
            return (int) ((this.mStepDurations[index] & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56);
        }

        private void appendHex(long val, int topOffset, StringBuilder out) {
            boolean hasData = false;
            while (topOffset >= 0) {
                int digit = (int) ((val >> topOffset) & 15);
                topOffset -= 4;
                if (hasData || digit != 0) {
                    hasData = true;
                    if (digit >= 0 && digit <= 9) {
                        out.append((char) (digit + 48));
                    } else {
                        out.append((char) ((digit + 97) - 10));
                    }
                }
            }
        }

        public void encodeEntryAt(int index, StringBuilder out) {
            long item = this.mStepDurations[index];
            long duration = BatteryStats.STEP_LEVEL_TIME_MASK & item;
            int level = (int) ((BatteryStats.STEP_LEVEL_LEVEL_MASK & item) >> 40);
            int initMode = (int) ((BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK & item) >> 48);
            int modMode = (int) ((BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK & item) >> 56);
            switch ((initMode & 3) + 1) {
                case 1:
                    out.append('f');
                    break;
                case 2:
                    out.append('o');
                    break;
                case 3:
                    out.append(DateFormat.DATE);
                    break;
                case 4:
                    out.append(DateFormat.TIME_ZONE);
                    break;
            }
            if ((initMode & 4) != 0) {
                out.append('p');
            }
            if ((initMode & 8) != 0) {
                out.append('i');
            }
            switch ((modMode & 3) + 1) {
                case 1:
                    out.append('F');
                    break;
                case 2:
                    out.append('O');
                    break;
                case 3:
                    out.append('D');
                    break;
                case 4:
                    out.append('Z');
                    break;
            }
            if ((modMode & 4) != 0) {
                out.append('P');
            }
            if ((modMode & 8) != 0) {
                out.append('I');
            }
            out.append('-');
            appendHex(level, 4, out);
            out.append('-');
            appendHex(duration, 36, out);
        }

        public void decodeEntryAt(int index, String value) {
            char c;
            char c2;
            char c3;
            char c4;
            char c5;
            char c6;
            int N = value.length();
            int i = 0;
            long out = 0;
            while (true) {
                c = '-';
                if (i < N && (c6 = value.charAt(i)) != '-') {
                    i++;
                    switch (c6) {
                        case 'D':
                            out |= 144115188075855872L;
                            break;
                        case 'F':
                            out |= 0;
                            break;
                        case 'I':
                            out |= 576460752303423488L;
                            break;
                        case 'O':
                            out |= 72057594037927936L;
                            break;
                        case 'P':
                            out |= 288230376151711744L;
                            break;
                        case 'Z':
                            out |= 216172782113783808L;
                            break;
                        case 'd':
                            out |= FrontendInnerFec.FEC_128_180;
                            break;
                        case 'f':
                            out |= 0;
                            break;
                        case 'i':
                            out |= FrontendInnerFec.FEC_135_180;
                            break;
                        case 'o':
                            out |= FrontendInnerFec.FEC_104_180;
                            break;
                        case 'p':
                            out |= FrontendInnerFec.FEC_132_180;
                            break;
                        case 'z':
                            out |= 844424930131968L;
                            break;
                    }
                }
            }
            int i2 = i + 1;
            long level = 0;
            while (true) {
                c2 = '9';
                c3 = 4;
                if (i2 < N && (c5 = value.charAt(i2)) != '-') {
                    i2++;
                    level <<= 4;
                    if (c5 >= '0' && c5 <= '9') {
                        level += c5 - '0';
                    } else if (c5 >= 'a' && c5 <= 'f') {
                        level += (c5 - 'a') + 10;
                    } else if (c5 >= 'A' && c5 <= 'F') {
                        level += (c5 - 'A') + 10;
                    }
                }
            }
            int i3 = i2 + 1;
            long out2 = out | ((level << 40) & BatteryStats.STEP_LEVEL_LEVEL_MASK);
            long duration = 0;
            while (i3 < N) {
                char c7 = value.charAt(i3);
                if (c7 != c) {
                    i3++;
                    duration <<= c3;
                    if (c7 >= '0' && c7 <= c2) {
                        duration += c7 - '0';
                        c = '-';
                        c2 = '9';
                        c3 = 4;
                    } else if (c7 >= 'a' && c7 <= 'f') {
                        duration += (c7 - 'a') + 10;
                        c = '-';
                        c2 = '9';
                        c3 = 4;
                    } else {
                        if (c7 >= 'A') {
                            c4 = 'F';
                            if (c7 <= 'F') {
                                duration += (c7 - 'A') + 10;
                                c = '-';
                                c2 = '9';
                                c3 = 4;
                            }
                        } else {
                            c4 = 'F';
                        }
                        c = '-';
                        c2 = '9';
                        c3 = 4;
                    }
                } else {
                    this.mStepDurations[index] = (BatteryStats.STEP_LEVEL_TIME_MASK & duration) | out2;
                }
            }
            this.mStepDurations[index] = (BatteryStats.STEP_LEVEL_TIME_MASK & duration) | out2;
        }

        public void init() {
            this.mLastStepTime = -1L;
            this.mNumStepDurations = 0;
        }

        public void clearTime() {
            this.mLastStepTime = -1L;
        }

        public long computeTimePerLevel() {
            long[] steps = this.mStepDurations;
            int numSteps = this.mNumStepDurations;
            if (numSteps <= 0) {
                return -1L;
            }
            long total = 0;
            for (int i = 0; i < numSteps; i++) {
                total += steps[i] & BatteryStats.STEP_LEVEL_TIME_MASK;
            }
            return total / numSteps;
        }

        public long computeTimeEstimate(long modesOfInterest, long modeValues, int[] outNumOfInterest) {
            long[] steps = this.mStepDurations;
            int count = this.mNumStepDurations;
            if (count <= 0) {
                return -1L;
            }
            long total = 0;
            int numOfInterest = 0;
            for (int i = 0; i < count; i++) {
                long initMode = (steps[i] & BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK) >> 48;
                long modMode = (steps[i] & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56;
                if ((modMode & modesOfInterest) == 0 && (initMode & modesOfInterest) == modeValues) {
                    numOfInterest++;
                    total += steps[i] & BatteryStats.STEP_LEVEL_TIME_MASK;
                }
            }
            if (numOfInterest <= 0) {
                return -1L;
            }
            if (outNumOfInterest != null) {
                outNumOfInterest[0] = numOfInterest;
            }
            return (total / numOfInterest) * 100;
        }

        public void addLevelSteps(int numStepLevels, long modeBits, long elapsedRealtime) {
            int stepCount = this.mNumStepDurations;
            long lastStepTime = this.mLastStepTime;
            if (lastStepTime >= 0 && numStepLevels > 0) {
                long[] steps = this.mStepDurations;
                long duration = elapsedRealtime - lastStepTime;
                for (int i = 0; i < numStepLevels; i++) {
                    System.arraycopy(steps, 0, steps, 1, steps.length - 1);
                    long thisDuration = duration / (numStepLevels - i);
                    duration -= thisDuration;
                    if (thisDuration > BatteryStats.STEP_LEVEL_TIME_MASK) {
                        thisDuration = BatteryStats.STEP_LEVEL_TIME_MASK;
                    }
                    steps[0] = thisDuration | modeBits;
                }
                stepCount += numStepLevels;
                if (stepCount > steps.length) {
                    stepCount = steps.length;
                }
            }
            this.mNumStepDurations = stepCount;
            this.mLastStepTime = elapsedRealtime;
        }

        public void readFromParcel(Parcel in) {
            int N = in.readInt();
            if (N > this.mStepDurations.length) {
                throw new ParcelFormatException("more step durations than available: " + N);
            }
            this.mNumStepDurations = N;
            for (int i = 0; i < N; i++) {
                this.mStepDurations[i] = in.readLong();
            }
        }

        public void writeToParcel(Parcel out) {
            int N = this.mNumStepDurations;
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                out.writeLong(this.mStepDurations[i]);
            }
        }
    }

    /* renamed from: android.os.BatteryStats$HistoryTag */
    /* loaded from: classes3.dex */
    public static final class HistoryTag {
        public int poolIdx;
        public String string;
        public int uid;

        public void setTo(HistoryTag o) {
            this.string = o.string;
            this.uid = o.uid;
            this.poolIdx = o.poolIdx;
        }

        public void setTo(String _string, int _uid) {
            this.string = _string;
            this.uid = _uid;
            this.poolIdx = -1;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.string);
            dest.writeInt(this.uid);
        }

        public void readFromParcel(Parcel src) {
            this.string = src.readString();
            this.uid = src.readInt();
            this.poolIdx = -1;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            HistoryTag that = (HistoryTag) o;
            if (this.uid == that.uid && this.string.equals(that.string)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.string.hashCode();
            return (result * 31) + this.uid;
        }
    }

    /* renamed from: android.os.BatteryStats$HistoryStepDetails */
    /* loaded from: classes3.dex */
    public static final class HistoryStepDetails {
        public int appCpuSTime1;
        public int appCpuSTime2;
        public int appCpuSTime3;
        public int appCpuUTime1;
        public int appCpuUTime2;
        public int appCpuUTime3;
        public int appCpuUid1;
        public int appCpuUid2;
        public int appCpuUid3;
        public int statIOWaitTime;
        public int statIdlTime;
        public int statIrqTime;
        public int statSoftIrqTime;
        public String statSubsystemPowerState;
        public int statSystemTime;
        public int statUserTime;
        public int systemTime;
        public int userTime;

        public HistoryStepDetails() {
            clear();
        }

        public void clear() {
            this.systemTime = 0;
            this.userTime = 0;
            this.appCpuUid3 = -1;
            this.appCpuUid2 = -1;
            this.appCpuUid1 = -1;
            this.appCpuSTime3 = 0;
            this.appCpuUTime3 = 0;
            this.appCpuSTime2 = 0;
            this.appCpuUTime2 = 0;
            this.appCpuSTime1 = 0;
            this.appCpuUTime1 = 0;
        }

        public void writeToParcel(Parcel out) {
            out.writeInt(this.userTime);
            out.writeInt(this.systemTime);
            out.writeInt(this.appCpuUid1);
            out.writeInt(this.appCpuUTime1);
            out.writeInt(this.appCpuSTime1);
            out.writeInt(this.appCpuUid2);
            out.writeInt(this.appCpuUTime2);
            out.writeInt(this.appCpuSTime2);
            out.writeInt(this.appCpuUid3);
            out.writeInt(this.appCpuUTime3);
            out.writeInt(this.appCpuSTime3);
            out.writeInt(this.statUserTime);
            out.writeInt(this.statSystemTime);
            out.writeInt(this.statIOWaitTime);
            out.writeInt(this.statIrqTime);
            out.writeInt(this.statSoftIrqTime);
            out.writeInt(this.statIdlTime);
            out.writeString(this.statSubsystemPowerState);
        }

        public void readFromParcel(Parcel in) {
            this.userTime = in.readInt();
            this.systemTime = in.readInt();
            this.appCpuUid1 = in.readInt();
            this.appCpuUTime1 = in.readInt();
            this.appCpuSTime1 = in.readInt();
            this.appCpuUid2 = in.readInt();
            this.appCpuUTime2 = in.readInt();
            this.appCpuSTime2 = in.readInt();
            this.appCpuUid3 = in.readInt();
            this.appCpuUTime3 = in.readInt();
            this.appCpuSTime3 = in.readInt();
            this.statUserTime = in.readInt();
            this.statSystemTime = in.readInt();
            this.statIOWaitTime = in.readInt();
            this.statIrqTime = in.readInt();
            this.statSoftIrqTime = in.readInt();
            this.statIdlTime = in.readInt();
            this.statSubsystemPowerState = in.readString();
        }
    }

    /* renamed from: android.os.BatteryStats$EnergyConsumerDetails */
    /* loaded from: classes3.dex */
    public static final class EnergyConsumerDetails {
        public long[] chargeUC;
        public EnergyConsumer[] consumers;

        /* renamed from: android.os.BatteryStats$EnergyConsumerDetails$EnergyConsumer */
        /* loaded from: classes3.dex */
        public static final class EnergyConsumer {
            public String name;
            public int ordinal;
            public int type;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.consumers.length; i++) {
                if (this.chargeUC[i] != -1) {
                    if (sb.length() != 0) {
                        sb.append(' ');
                    }
                    sb.append(this.consumers[i].name);
                    sb.append('=');
                    sb.append(this.chargeUC[i]);
                }
            }
            return sb.toString();
        }
    }

    /* renamed from: android.os.BatteryStats$CpuUsageDetails */
    /* loaded from: classes3.dex */
    public static final class CpuUsageDetails {
        public String[] cpuBracketDescriptions;
        public long[] cpuUsageMs;
        public int uid;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            UserHandle.formatUid(sb, this.uid);
            sb.append(": ");
            for (int bracket = 0; bracket < this.cpuUsageMs.length; bracket++) {
                if (bracket != 0) {
                    sb.append(", ");
                }
                sb.append(this.cpuUsageMs[bracket]);
            }
            return sb.toString();
        }
    }

    /* renamed from: android.os.BatteryStats$HistoryItem */
    /* loaded from: classes3.dex */
    public static final class HistoryItem {
        public static final byte CMD_CURRENT_TIME = 5;
        public static final byte CMD_NULL = -1;
        public static final byte CMD_OVERFLOW = 6;
        public static final byte CMD_RESET = 7;
        public static final byte CMD_SHUTDOWN = 8;
        public static final byte CMD_START = 4;
        public static final byte CMD_UPDATE = 0;
        public static final int EVENT_ACTIVE = 10;
        public static final int EVENT_ALARM = 13;
        public static final int EVENT_ALARM_FINISH = 16397;
        public static final int EVENT_ALARM_START = 32781;
        public static final int EVENT_COLLECT_EXTERNAL_STATS = 14;
        public static final int EVENT_CONNECTIVITY_CHANGED = 9;
        public static final int EVENT_COUNT = 22;
        public static final int EVENT_FLAG_FINISH = 16384;
        public static final int EVENT_FLAG_START = 32768;
        public static final int EVENT_FOREGROUND = 2;
        public static final int EVENT_FOREGROUND_FINISH = 16386;
        public static final int EVENT_FOREGROUND_START = 32770;
        public static final int EVENT_JOB = 6;
        public static final int EVENT_JOB_FINISH = 16390;
        public static final int EVENT_JOB_START = 32774;
        public static final int EVENT_LONG_WAKE_LOCK = 20;
        public static final int EVENT_LONG_WAKE_LOCK_FINISH = 16404;
        public static final int EVENT_LONG_WAKE_LOCK_START = 32788;
        public static final int EVENT_NONE = 0;
        public static final int EVENT_PACKAGE_ACTIVE = 16;
        public static final int EVENT_PACKAGE_INACTIVE = 15;
        public static final int EVENT_PACKAGE_INSTALLED = 11;
        public static final int EVENT_PACKAGE_UNINSTALLED = 12;
        public static final int EVENT_PROC = 1;
        public static final int EVENT_PROC_FINISH = 16385;
        public static final int EVENT_PROC_START = 32769;
        public static final int EVENT_SCREEN_WAKE_UP = 18;
        public static final int EVENT_SYNC = 4;
        public static final int EVENT_SYNC_FINISH = 16388;
        public static final int EVENT_SYNC_START = 32772;
        public static final int EVENT_TEMP_WHITELIST = 17;
        public static final int EVENT_TEMP_WHITELIST_FINISH = 16401;
        public static final int EVENT_TEMP_WHITELIST_START = 32785;
        public static final int EVENT_TOP = 3;
        public static final int EVENT_TOP_FINISH = 16387;
        public static final int EVENT_TOP_START = 32771;
        public static final int EVENT_TYPE_MASK = -49153;
        public static final int EVENT_USER_FOREGROUND = 8;
        public static final int EVENT_USER_FOREGROUND_FINISH = 16392;
        public static final int EVENT_USER_FOREGROUND_START = 32776;
        public static final int EVENT_USER_RUNNING = 7;
        public static final int EVENT_USER_RUNNING_FINISH = 16391;
        public static final int EVENT_USER_RUNNING_START = 32775;
        public static final int EVENT_WAKEUP_AP = 19;
        public static final int EVENT_WAKE_LOCK = 5;
        public static final int EVENT_WAKE_LOCK_FINISH = 16389;
        public static final int EVENT_WAKE_LOCK_START = 32773;
        public static final int MOST_INTERESTING_STATES = 1835008;
        public static final int MOST_INTERESTING_STATES2 = -1749024768;
        public static final int SETTLE_TO_ZERO_STATES = -1900544;
        public static final int SETTLE_TO_ZERO_STATES2 = 1748959232;
        public static final int STATE2_BLUETOOTH_ON_FLAG = 4194304;
        public static final int STATE2_BLUETOOTH_SCAN_FLAG = 1048576;
        public static final int STATE2_CAMERA_FLAG = 2097152;
        public static final int STATE2_CELLULAR_HIGH_TX_POWER_FLAG = 524288;
        public static final int STATE2_CHARGING_FLAG = 16777216;
        public static final int STATE2_DEVICE_IDLE_MASK = 100663296;
        public static final int STATE2_DEVICE_IDLE_SHIFT = 25;
        public static final int STATE2_EXTENSIONS_FLAG = 131072;
        public static final int STATE2_FLASHLIGHT_FLAG = 134217728;
        public static final int STATE2_GPS_SIGNAL_QUALITY_MASK = 128;
        public static final int STATE2_GPS_SIGNAL_QUALITY_SHIFT = 7;
        public static final int STATE2_PHONE_IN_CALL_FLAG = 8388608;
        public static final int STATE2_POWER_SAVE_FLAG = Integer.MIN_VALUE;
        public static final int STATE2_USB_DATA_LINK_FLAG = 262144;
        public static final int STATE2_VIDEO_ON_FLAG = 1073741824;
        public static final int STATE2_WIFI_ON_FLAG = 268435456;
        public static final int STATE2_WIFI_RUNNING_FLAG = 536870912;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_MASK = 112;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE2_WIFI_SUPPL_STATE_MASK = 15;
        public static final int STATE2_WIFI_SUPPL_STATE_SHIFT = 0;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_BRIGHTNESS_MASK = 7;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_CPU_RUNNING_FLAG = Integer.MIN_VALUE;
        public static final int STATE_DATA_CONNECTION_MASK = 15872;
        public static final int STATE_DATA_CONNECTION_SHIFT = 9;
        public static final int STATE_GPS_ON_FLAG = 536870912;
        public static final int STATE_MOBILE_RADIO_ACTIVE_FLAG = 33554432;
        public static final int STATE_PHONE_SCANNING_FLAG = 2097152;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_MASK = 56;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_SHIFT = 3;
        public static final int STATE_PHONE_STATE_MASK = 448;
        public static final int STATE_PHONE_STATE_SHIFT = 6;
        private static final int STATE_RESERVED_0 = 16777216;
        public static final int STATE_SCREEN_DOZE_FLAG = 262144;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_SENSOR_ON_FLAG = 8388608;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 268435456;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 65536;
        public static final int STATE_WIFI_RADIO_ACTIVE_FLAG = 67108864;
        public static final int STATE_WIFI_SCAN_FLAG = 134217728;
        public int batteryChargeUah;
        public byte batteryHealth;
        public byte batteryLevel;
        public byte batteryPlugType;
        public byte batteryStatus;
        public short batteryTemperature;
        public char batteryVoltage;
        public CpuUsageDetails cpuUsageDetails;
        public long currentTime;
        public EnergyConsumerDetails energyConsumerDetails;
        public int eventCode;
        public HistoryTag eventTag;
        public double modemRailChargeMah;
        public HistoryItem next;
        public int numReadInts;
        public int states;
        public int states2;
        public HistoryStepDetails stepDetails;
        public boolean tagsFirstOccurrence;
        public long time;
        public HistoryTag wakeReasonTag;
        public HistoryTag wakelockTag;
        public double wifiRailChargeMah;
        public byte cmd = -1;
        public final HistoryTag localWakelockTag = new HistoryTag();
        public final HistoryTag localWakeReasonTag = new HistoryTag();
        public final HistoryTag localEventTag = new HistoryTag();

        public boolean isDeltaData() {
            return this.cmd == 0;
        }

        public HistoryItem() {
        }

        public HistoryItem(Parcel src) {
            readFromParcel(src);
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.time);
            int bat = (this.cmd & 255) | ((this.batteryLevel << 8) & 65280) | ((this.batteryStatus << 16) & SurfaceControl.FX_SURFACE_MASK) | ((this.batteryHealth << IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT) & 15728640) | ((this.batteryPlugType << 24) & 251658240) | (this.wakelockTag != null ? 268435456 : 0) | (this.wakeReasonTag != null ? 536870912 : 0) | (this.eventCode != 0 ? 1073741824 : 0);
            dest.writeInt(bat);
            int bat2 = (this.batteryTemperature & 65535) | ((this.batteryVoltage << 16) & (-65536));
            dest.writeInt(bat2);
            dest.writeInt(this.batteryChargeUah);
            dest.writeDouble(this.modemRailChargeMah);
            dest.writeDouble(this.wifiRailChargeMah);
            dest.writeInt(this.states);
            dest.writeInt(this.states2);
            HistoryTag historyTag = this.wakelockTag;
            if (historyTag != null) {
                historyTag.writeToParcel(dest, flags);
            }
            HistoryTag historyTag2 = this.wakeReasonTag;
            if (historyTag2 != null) {
                historyTag2.writeToParcel(dest, flags);
            }
            int i = this.eventCode;
            if (i != 0) {
                dest.writeInt(i);
                this.eventTag.writeToParcel(dest, flags);
            }
            byte b = this.cmd;
            if (b == 5 || b == 7) {
                dest.writeLong(this.currentTime);
            }
        }

        public void readFromParcel(Parcel src) {
            int start = src.dataPosition();
            this.time = src.readLong();
            int bat = src.readInt();
            this.cmd = (byte) (bat & 255);
            this.batteryLevel = (byte) ((bat >> 8) & 255);
            this.batteryStatus = (byte) ((bat >> 16) & 15);
            this.batteryHealth = (byte) ((bat >> 20) & 15);
            this.batteryPlugType = (byte) ((bat >> 24) & 15);
            int bat2 = src.readInt();
            this.batteryTemperature = (short) (bat2 & 65535);
            this.batteryVoltage = (char) (65535 & (bat2 >> 16));
            this.batteryChargeUah = src.readInt();
            this.modemRailChargeMah = src.readDouble();
            this.wifiRailChargeMah = src.readDouble();
            this.states = src.readInt();
            this.states2 = src.readInt();
            if ((268435456 & bat) != 0) {
                HistoryTag historyTag = this.localWakelockTag;
                this.wakelockTag = historyTag;
                historyTag.readFromParcel(src);
            } else {
                this.wakelockTag = null;
            }
            if ((536870912 & bat) != 0) {
                HistoryTag historyTag2 = this.localWakeReasonTag;
                this.wakeReasonTag = historyTag2;
                historyTag2.readFromParcel(src);
            } else {
                this.wakeReasonTag = null;
            }
            if ((1073741824 & bat) != 0) {
                this.eventCode = src.readInt();
                HistoryTag historyTag3 = this.localEventTag;
                this.eventTag = historyTag3;
                historyTag3.readFromParcel(src);
            } else {
                this.eventCode = 0;
                this.eventTag = null;
            }
            byte b = this.cmd;
            if (b == 5 || b == 7) {
                this.currentTime = src.readLong();
            } else {
                this.currentTime = 0L;
            }
            this.numReadInts += (src.dataPosition() - start) / 4;
        }

        public void clear() {
            this.time = 0L;
            this.cmd = (byte) -1;
            this.batteryLevel = (byte) 0;
            this.batteryStatus = (byte) 0;
            this.batteryHealth = (byte) 0;
            this.batteryPlugType = (byte) 0;
            this.batteryTemperature = (short) 0;
            this.batteryVoltage = (char) 0;
            this.batteryChargeUah = 0;
            this.modemRailChargeMah = 0.0d;
            this.wifiRailChargeMah = 0.0d;
            this.states = 0;
            this.states2 = 0;
            this.wakelockTag = null;
            this.wakeReasonTag = null;
            this.eventCode = 0;
            this.eventTag = null;
            this.tagsFirstOccurrence = false;
            this.energyConsumerDetails = null;
            this.cpuUsageDetails = null;
        }

        public void setTo(HistoryItem o) {
            this.time = o.time;
            this.cmd = o.cmd;
            setToCommon(o);
        }

        public void setTo(long time, byte cmd, HistoryItem o) {
            this.time = time;
            this.cmd = cmd;
            setToCommon(o);
        }

        private void setToCommon(HistoryItem o) {
            this.batteryLevel = o.batteryLevel;
            this.batteryStatus = o.batteryStatus;
            this.batteryHealth = o.batteryHealth;
            this.batteryPlugType = o.batteryPlugType;
            this.batteryTemperature = o.batteryTemperature;
            this.batteryVoltage = o.batteryVoltage;
            this.batteryChargeUah = o.batteryChargeUah;
            this.modemRailChargeMah = o.modemRailChargeMah;
            this.wifiRailChargeMah = o.wifiRailChargeMah;
            this.states = o.states;
            this.states2 = o.states2;
            if (o.wakelockTag != null) {
                HistoryTag historyTag = this.localWakelockTag;
                this.wakelockTag = historyTag;
                historyTag.setTo(o.wakelockTag);
            } else {
                this.wakelockTag = null;
            }
            if (o.wakeReasonTag != null) {
                HistoryTag historyTag2 = this.localWakeReasonTag;
                this.wakeReasonTag = historyTag2;
                historyTag2.setTo(o.wakeReasonTag);
            } else {
                this.wakeReasonTag = null;
            }
            this.eventCode = o.eventCode;
            if (o.eventTag != null) {
                HistoryTag historyTag3 = this.localEventTag;
                this.eventTag = historyTag3;
                historyTag3.setTo(o.eventTag);
            } else {
                this.eventTag = null;
            }
            this.tagsFirstOccurrence = o.tagsFirstOccurrence;
            this.currentTime = o.currentTime;
            this.energyConsumerDetails = o.energyConsumerDetails;
            this.cpuUsageDetails = o.cpuUsageDetails;
        }

        public boolean sameNonEvent(HistoryItem o) {
            return this.batteryLevel == o.batteryLevel && this.batteryStatus == o.batteryStatus && this.batteryHealth == o.batteryHealth && this.batteryPlugType == o.batteryPlugType && this.batteryTemperature == o.batteryTemperature && this.batteryVoltage == o.batteryVoltage && this.batteryChargeUah == o.batteryChargeUah && this.modemRailChargeMah == o.modemRailChargeMah && this.wifiRailChargeMah == o.wifiRailChargeMah && this.states == o.states && this.states2 == o.states2 && this.currentTime == o.currentTime;
        }

        public boolean same(HistoryItem o) {
            if (sameNonEvent(o) && this.eventCode == o.eventCode) {
                HistoryTag historyTag = this.wakelockTag;
                HistoryTag historyTag2 = o.wakelockTag;
                if (historyTag == historyTag2 || !(historyTag == null || historyTag2 == null || !historyTag.equals(historyTag2))) {
                    HistoryTag historyTag3 = this.wakeReasonTag;
                    HistoryTag historyTag4 = o.wakeReasonTag;
                    if (historyTag3 == historyTag4 || !(historyTag3 == null || historyTag4 == null || !historyTag3.equals(historyTag4))) {
                        HistoryTag historyTag5 = this.eventTag;
                        HistoryTag historyTag6 = o.eventTag;
                        if (historyTag5 != historyTag6) {
                            return (historyTag5 == null || historyTag6 == null || !historyTag5.equals(historyTag6)) ? false : true;
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
    }

    /* renamed from: android.os.BatteryStats$HistoryEventTracker */
    /* loaded from: classes3.dex */
    public static final class HistoryEventTracker {
        private final HashMap<String, SparseIntArray>[] mActiveEvents = new HashMap[22];

        public boolean updateState(int code, String name, int uid, int poolIdx) {
            SparseIntArray uids;
            int idx;
            if ((32768 & code) != 0) {
                int idx2 = code & HistoryItem.EVENT_TYPE_MASK;
                HashMap<String, SparseIntArray> active = this.mActiveEvents[idx2];
                if (active == null) {
                    active = new HashMap<>();
                    this.mActiveEvents[idx2] = active;
                }
                SparseIntArray uids2 = active.get(name);
                if (uids2 == null) {
                    uids2 = new SparseIntArray();
                    active.put(name, uids2);
                }
                if (uids2.indexOfKey(uid) >= 0) {
                    return false;
                }
                uids2.put(uid, poolIdx);
                return true;
            } else if ((code & 16384) != 0) {
                HashMap<String, SparseIntArray> active2 = this.mActiveEvents[code & HistoryItem.EVENT_TYPE_MASK];
                if (active2 == null || (uids = active2.get(name)) == null || (idx = uids.indexOfKey(uid)) < 0) {
                    return false;
                }
                uids.removeAt(idx);
                if (uids.size() <= 0) {
                    active2.remove(name);
                    return true;
                }
                return true;
            } else {
                return true;
            }
        }

        public void removeEvents(int code) {
            int idx = (-49153) & code;
            this.mActiveEvents[idx] = null;
        }

        public HashMap<String, SparseIntArray> getStateForEvent(int code) {
            return this.mActiveEvents[code];
        }
    }

    /* renamed from: android.os.BatteryStats$BitDescription */
    /* loaded from: classes3.dex */
    public static final class BitDescription {
        public final int mask;
        public final String name;
        public final int shift;
        public final String shortName;
        public final String[] shortValues;
        public final String[] values;

        public BitDescription(int mask, String name, String shortName) {
            this.mask = mask;
            this.shift = -1;
            this.name = name;
            this.shortName = shortName;
            this.values = null;
            this.shortValues = null;
        }

        public BitDescription(int mask, int shift, String name, String shortName, String[] values, String[] shortValues) {
            this.mask = mask;
            this.shift = shift;
            this.name = name;
            this.shortName = shortName;
            this.values = values;
            this.shortValues = shortValues;
        }
    }

    private static final void formatTimeRaw(StringBuilder out, long seconds) {
        long days = seconds / 86400;
        if (days != 0) {
            out.append(days);
            out.append("d ");
        }
        long used = days * 60 * 60 * 24;
        long hours = (seconds - used) / 3600;
        if (hours != 0 || used != 0) {
            out.append(hours);
            out.append("h ");
        }
        long used2 = used + (hours * 60 * 60);
        long mins = (seconds - used2) / 60;
        if (mins != 0 || used2 != 0) {
            out.append(mins);
            out.append("m ");
        }
        long used3 = used2 + (60 * mins);
        if (seconds != 0 || used3 != 0) {
            out.append(seconds - used3);
            out.append("s ");
        }
    }

    public static final void formatTimeMs(StringBuilder sb, long time) {
        long sec = time / 1000;
        formatTimeRaw(sb, sec);
        sb.append(time - (1000 * sec));
        sb.append("ms ");
    }

    public static final void formatTimeMsNoSpace(StringBuilder sb, long time) {
        long sec = time / 1000;
        formatTimeRaw(sb, sec);
        sb.append(time - (1000 * sec));
        sb.append("ms");
    }

    public final String formatRatioLocked(long num, long den) {
        if (den == 0) {
            return "--%";
        }
        float perc = (((float) num) / ((float) den)) * 100.0f;
        this.mFormatBuilder.setLength(0);
        this.mFormatter.format("%.1f%%", Float.valueOf(perc));
        return this.mFormatBuilder.toString();
    }

    final String formatBytesLocked(long bytes) {
        this.mFormatBuilder.setLength(0);
        if (bytes < 1024) {
            return bytes + GnssSignalType.CODE_TYPE_B;
        }
        if (bytes < 1048576) {
            this.mFormatter.format("%.2fKB", Double.valueOf(bytes / 1024.0d));
            return this.mFormatBuilder.toString();
        } else if (bytes < 1073741824) {
            this.mFormatter.format("%.2fMB", Double.valueOf(bytes / 1048576.0d));
            return this.mFormatBuilder.toString();
        } else {
            this.mFormatter.format("%.2fGB", Double.valueOf(bytes / 1.073741824E9d));
            return this.mFormatBuilder.toString();
        }
    }

    public static String formatCharge(double power) {
        return formatValue(power);
    }

    private static String formatValue(double value) {
        String format;
        if (value == 0.0d) {
            return AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS;
        }
        if (value < 1.0E-5d) {
            format = "%.8f";
        } else if (value < 1.0E-4d) {
            format = "%.7f";
        } else if (value < 0.001d) {
            format = "%.6f";
        } else if (value < 0.01d) {
            format = "%.5f";
        } else if (value < 0.1d) {
            format = "%.4f";
        } else if (value < 1.0d) {
            format = "%.3f";
        } else if (value < 10.0d) {
            format = "%.2f";
        } else if (value < 100.0d) {
            format = "%.1f";
        } else {
            format = "%.0f";
        }
        return String.format(Locale.ENGLISH, format, Double.valueOf(value));
    }

    private static long roundUsToMs(long timeUs) {
        return (500 + timeUs) / 1000;
    }

    private static long computeWakeLock(Timer timer, long elapsedRealtimeUs, int which) {
        if (timer != null) {
            long totalTimeMicros = timer.getTotalTimeLocked(elapsedRealtimeUs, which);
            long totalTimeMillis = (500 + totalTimeMicros) / 1000;
            return totalTimeMillis;
        }
        return 0L;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long elapsedRealtimeUs, String name, int which, String linePrefix) {
        if (timer != null) {
            long totalTimeMillis = computeWakeLock(timer, elapsedRealtimeUs, which);
            int count = timer.getCountLocked(which);
            if (totalTimeMillis != 0) {
                sb.append(linePrefix);
                formatTimeMs(sb, totalTimeMillis);
                if (name != null) {
                    sb.append(name);
                    sb.append(' ');
                }
                sb.append('(');
                sb.append(count);
                sb.append(" times)");
                long maxDurationMs = timer.getMaxDurationMsLocked(elapsedRealtimeUs / 1000);
                if (maxDurationMs >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMs);
                }
                long totalDurMs = timer.getTotalDurationMsLocked(elapsedRealtimeUs / 1000);
                if (totalDurMs > totalTimeMillis) {
                    sb.append(" actual=");
                    sb.append(totalDurMs);
                }
                if (timer.isRunningLocked()) {
                    long currentMs = timer.getCurrentDurationMsLocked(elapsedRealtimeUs / 1000);
                    if (currentMs >= 0) {
                        sb.append(" (running for ");
                        sb.append(currentMs);
                        sb.append("ms)");
                        return ", ";
                    }
                    sb.append(" (running)");
                    return ", ";
                }
                return ", ";
            }
        }
        return linePrefix;
    }

    private static final boolean printTimer(PrintWriter pw, StringBuilder sb, Timer timer, long rawRealtimeUs, int which, String prefix, String type) {
        if (timer != null) {
            long totalTimeMs = (timer.getTotalTimeLocked(rawRealtimeUs, which) + 500) / 1000;
            int count = timer.getCountLocked(which);
            if (totalTimeMs != 0) {
                sb.setLength(0);
                sb.append(prefix);
                sb.append("    ");
                sb.append(type);
                sb.append(": ");
                formatTimeMs(sb, totalTimeMs);
                sb.append("realtime (");
                sb.append(count);
                sb.append(" times)");
                long maxDurationMs = timer.getMaxDurationMsLocked(rawRealtimeUs / 1000);
                if (maxDurationMs >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMs);
                }
                if (timer.isRunningLocked()) {
                    long currentMs = timer.getCurrentDurationMsLocked(rawRealtimeUs / 1000);
                    if (currentMs >= 0) {
                        sb.append(" (running for ");
                        sb.append(currentMs);
                        sb.append("ms)");
                    } else {
                        sb.append(" (running)");
                    }
                }
                pw.println(sb.toString());
                return true;
            }
        }
        return false;
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long elapsedRealtimeUs, String name, int which, String linePrefix) {
        long totalTimeMicros = 0;
        int count = 0;
        long max = 0;
        long current = 0;
        long totalDuration = 0;
        if (timer != null) {
            long totalTimeMicros2 = timer.getTotalTimeLocked(elapsedRealtimeUs, which);
            count = timer.getCountLocked(which);
            current = timer.getCurrentDurationMsLocked(elapsedRealtimeUs / 1000);
            max = timer.getMaxDurationMsLocked(elapsedRealtimeUs / 1000);
            totalDuration = timer.getTotalDurationMsLocked(elapsedRealtimeUs / 1000);
            totalTimeMicros = totalTimeMicros2;
        }
        sb.append(linePrefix);
        sb.append((totalTimeMicros + 500) / 1000);
        sb.append(',');
        sb.append(name != null ? name + "," : "");
        sb.append(count);
        sb.append(',');
        sb.append(current);
        sb.append(',');
        sb.append(max);
        if (name != null) {
            sb.append(',');
            sb.append(totalDuration);
        }
        return ",";
    }

    private static final void dumpLineHeader(PrintWriter pw, int uid, String category, String type) {
        pw.print(9);
        pw.print(',');
        pw.print(uid);
        pw.print(',');
        pw.print(category);
        pw.print(',');
        pw.print(type);
    }

    private static final void dumpLine(PrintWriter pw, int uid, String category, String type, Object... args) {
        dumpLineHeader(pw, uid, category, type);
        for (Object arg : args) {
            pw.print(',');
            pw.print(arg);
        }
        pw.println();
    }

    private static final void dumpTimer(PrintWriter pw, int uid, String category, String type, Timer timer, long rawRealtime, int which) {
        if (timer != null) {
            long totalTime = roundUsToMs(timer.getTotalTimeLocked(rawRealtime, which));
            int count = timer.getCountLocked(which);
            if (totalTime != 0 || count != 0) {
                dumpLine(pw, uid, category, type, Long.valueOf(totalTime), Integer.valueOf(count));
            }
        }
    }

    private static void dumpTimer(ProtoOutputStream proto, long fieldId, Timer timer, long rawRealtimeUs, int which) {
        if (timer == null) {
            return;
        }
        long timeMs = roundUsToMs(timer.getTotalTimeLocked(rawRealtimeUs, which));
        int count = timer.getCountLocked(which);
        long maxDurationMs = timer.getMaxDurationMsLocked(rawRealtimeUs / 1000);
        long curDurationMs = timer.getCurrentDurationMsLocked(rawRealtimeUs / 1000);
        long totalDurationMs = timer.getTotalDurationMsLocked(rawRealtimeUs / 1000);
        if (timeMs != 0 || count != 0 || maxDurationMs != -1 || curDurationMs != -1 || totalDurationMs != -1) {
            long token = proto.start(fieldId);
            proto.write(1112396529665L, timeMs);
            proto.write(1112396529666L, count);
            if (maxDurationMs != -1) {
                proto.write(1112396529667L, maxDurationMs);
            }
            if (curDurationMs != -1) {
                proto.write(1112396529668L, curDurationMs);
            }
            if (totalDurationMs != -1) {
                proto.write(1112396529669L, totalDurationMs);
            }
            proto.end(token);
        }
    }

    private static boolean controllerActivityHasData(ControllerActivityCounter counter, int which) {
        LongCounter[] txTimeCounters;
        if (counter == null) {
            return false;
        }
        if (counter.getIdleTimeCounter().getCountLocked(which) == 0 && counter.getRxTimeCounter().getCountLocked(which) == 0 && counter.getPowerCounter().getCountLocked(which) == 0 && counter.getMonitoredRailChargeConsumedMaMs().getCountLocked(which) == 0) {
            for (LongCounter c : counter.getTxTimeCounters()) {
                if (c.getCountLocked(which) != 0) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static final void dumpControllerActivityLine(PrintWriter pw, int uid, String category, String type, ControllerActivityCounter counter, int which) {
        LongCounter[] txTimeCounters;
        if (!controllerActivityHasData(counter, which)) {
            return;
        }
        dumpLineHeader(pw, uid, category, type);
        pw.print(",");
        pw.print(counter.getIdleTimeCounter().getCountLocked(which));
        pw.print(",");
        pw.print(counter.getRxTimeCounter().getCountLocked(which));
        pw.print(",");
        pw.print(counter.getPowerCounter().getCountLocked(which) / 3600000.0d);
        pw.print(",");
        pw.print(counter.getMonitoredRailChargeConsumedMaMs().getCountLocked(which) / 3600000.0d);
        for (LongCounter c : counter.getTxTimeCounters()) {
            pw.print(",");
            pw.print(c.getCountLocked(which));
        }
        pw.println();
    }

    private static void dumpControllerActivityProto(ProtoOutputStream proto, long fieldId, ControllerActivityCounter counter, int which) {
        if (!controllerActivityHasData(counter, which)) {
            return;
        }
        long cToken = proto.start(fieldId);
        proto.write(1112396529665L, counter.getIdleTimeCounter().getCountLocked(which));
        proto.write(1112396529666L, counter.getRxTimeCounter().getCountLocked(which));
        proto.write(1112396529667L, counter.getPowerCounter().getCountLocked(which) / 3600000.0d);
        proto.write(1103806595077L, counter.getMonitoredRailChargeConsumedMaMs().getCountLocked(which) / 3600000.0d);
        LongCounter[] txCounters = counter.getTxTimeCounters();
        for (int i = 0; i < txCounters.length; i++) {
            LongCounter c = txCounters[i];
            long tToken = proto.start(2246267895812L);
            proto.write(1120986464257L, i);
            proto.write(1112396529666L, c.getCountLocked(which));
            proto.end(tToken);
        }
        proto.end(cToken);
    }

    private final void printControllerActivityIfInteresting(PrintWriter pw, StringBuilder sb, String prefix, String controllerName, ControllerActivityCounter counter, int which) {
        if (controllerActivityHasData(counter, which)) {
            printControllerActivity(pw, sb, prefix, controllerName, counter, which);
        }
    }

    private final void printControllerActivity(PrintWriter pw, StringBuilder sb, String prefix, String controllerName, ControllerActivityCounter counter, int which) {
        long rxTimeMs;
        String str;
        int i;
        Object obj;
        char c;
        String[] powerLevel;
        String[] powerLevel2;
        long powerDrainMaMs;
        long idleTimeMs = counter.getIdleTimeCounter().getCountLocked(which);
        long rxTimeMs2 = counter.getRxTimeCounter().getCountLocked(which);
        long powerDrainMaMs2 = counter.getPowerCounter().getCountLocked(which);
        long monitoredRailChargeConsumedMaMs = counter.getMonitoredRailChargeConsumedMaMs().getCountLocked(which);
        long totalControllerActivityTimeMs = computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, which) / 1000;
        long totalTxTimeMs = 0;
        LongCounter[] txTimeCounters = counter.getTxTimeCounters();
        int length = txTimeCounters.length;
        int i2 = 0;
        while (i2 < length) {
            int i3 = length;
            LongCounter txState = txTimeCounters[i2];
            totalTxTimeMs += txState.getCountLocked(which);
            i2++;
            length = i3;
        }
        if (!controllerName.equals(WIFI_CONTROLLER_NAME)) {
            rxTimeMs = rxTimeMs2;
            str = " Sleep time:  ";
        } else {
            long scanTimeMs = counter.getScanTimeCounter().getCountLocked(which);
            sb.setLength(0);
            sb.append(prefix);
            sb.append("     ");
            sb.append(controllerName);
            sb.append(" Scan time:  ");
            formatTimeMs(sb, scanTimeMs);
            sb.append(NavigationBarInflaterView.KEY_CODE_START);
            sb.append(formatRatioLocked(scanTimeMs, totalControllerActivityTimeMs));
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            pw.println(sb.toString());
            long scanTimeMs2 = totalControllerActivityTimeMs - ((idleTimeMs + rxTimeMs2) + totalTxTimeMs);
            sb.setLength(0);
            sb.append(prefix);
            sb.append("     ");
            sb.append(controllerName);
            str = " Sleep time:  ";
            sb.append(str);
            formatTimeMs(sb, scanTimeMs2);
            sb.append(NavigationBarInflaterView.KEY_CODE_START);
            rxTimeMs = rxTimeMs2;
            sb.append(formatRatioLocked(scanTimeMs2, totalControllerActivityTimeMs));
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            pw.println(sb.toString());
        }
        if (!controllerName.equals(CELLULAR_CONTROLLER_NAME)) {
            i = which;
            obj = CELLULAR_CONTROLLER_NAME;
        } else {
            i = which;
            long sleepTimeMs = counter.getSleepTimeCounter().getCountLocked(i);
            obj = CELLULAR_CONTROLLER_NAME;
            sb.setLength(0);
            sb.append(prefix);
            sb.append("     ");
            sb.append(controllerName);
            sb.append(str);
            formatTimeMs(sb, sleepTimeMs);
            sb.append(NavigationBarInflaterView.KEY_CODE_START);
            sb.append(formatRatioLocked(sleepTimeMs, totalControllerActivityTimeMs));
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            pw.println(sb.toString());
        }
        sb.setLength(0);
        sb.append(prefix);
        sb.append("     ");
        sb.append(controllerName);
        sb.append(" Idle time:   ");
        formatTimeMs(sb, idleTimeMs);
        sb.append(NavigationBarInflaterView.KEY_CODE_START);
        sb.append(formatRatioLocked(idleTimeMs, totalControllerActivityTimeMs));
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("     ");
        sb.append(controllerName);
        sb.append(" Rx time:     ");
        long rxTimeMs3 = rxTimeMs;
        formatTimeMs(sb, rxTimeMs3);
        sb.append(NavigationBarInflaterView.KEY_CODE_START);
        sb.append(formatRatioLocked(rxTimeMs3, totalControllerActivityTimeMs));
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        pw.println(sb.toString());
        sb.setLength(0);
        sb.append(prefix);
        sb.append("     ");
        sb.append(controllerName);
        sb.append(" Tx time:     ");
        switch (controllerName.hashCode()) {
            case -851952246:
                if (controllerName.equals(obj)) {
                    c = 0;
                    break;
                }
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                powerLevel = new String[]{"   less than 0dBm: ", "   0dBm to 8dBm: ", "   8dBm to 15dBm: ", "   15dBm to 20dBm: ", "   above 20dBm: "};
                break;
            default:
                powerLevel = new String[]{"[0]", "[1]", "[2]", "[3]", "[4]"};
                break;
        }
        int numTxLvls = Math.min(counter.getTxTimeCounters().length, powerLevel.length);
        if (numTxLvls > 1) {
            pw.println(sb.toString());
            for (int lvl = 0; lvl < numTxLvls; lvl++) {
                long txLvlTimeMs = counter.getTxTimeCounters()[lvl].getCountLocked(i);
                sb.setLength(0);
                sb.append(prefix);
                sb.append("    ");
                sb.append(powerLevel[lvl]);
                sb.append(" ");
                formatTimeMs(sb, txLvlTimeMs);
                sb.append(NavigationBarInflaterView.KEY_CODE_START);
                sb.append(formatRatioLocked(txLvlTimeMs, totalControllerActivityTimeMs));
                sb.append(NavigationBarInflaterView.KEY_CODE_END);
                pw.println(sb.toString());
            }
        } else {
            long txLvlTimeMs2 = counter.getTxTimeCounters()[0].getCountLocked(i);
            formatTimeMs(sb, txLvlTimeMs2);
            sb.append(NavigationBarInflaterView.KEY_CODE_START);
            sb.append(formatRatioLocked(txLvlTimeMs2, totalControllerActivityTimeMs));
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            pw.println(sb.toString());
        }
        if (powerDrainMaMs2 <= 0) {
            powerLevel2 = powerLevel;
            powerDrainMaMs = powerDrainMaMs2;
        } else {
            sb.setLength(0);
            sb.append(prefix);
            sb.append("     ");
            sb.append(controllerName);
            powerLevel2 = powerLevel;
            powerDrainMaMs = powerDrainMaMs2;
            sb.append(" Battery drain: ").append(formatCharge(powerDrainMaMs / 3600000.0d));
            sb.append("mAh");
            pw.println(sb.toString());
        }
        if (monitoredRailChargeConsumedMaMs > 0) {
            sb.setLength(0);
            sb.append(prefix);
            sb.append("     ");
            sb.append(controllerName);
            sb.append(" Monitored rail energy drain: ").append(new DecimalFormat("#.##").format(monitoredRailChargeConsumedMaMs / 3600000.0d));
            sb.append(" mAh");
            pw.println(sb.toString());
        }
    }

    private void printCellularPerRatBreakdown(PrintWriter pw, StringBuilder sb, String prefix, long rawRealtimeMs) {
        String[] nrFrequencyRangeDescription;
        String signalStrengthHeader;
        long j = rawRealtimeMs;
        String allFrequenciesHeader = "    All frequencies:\n";
        String[] nrFrequencyRangeDescription2 = {"    Unknown frequency:\n", "    Low frequency (less than 1GHz):\n", "    Middle frequency (1GHz to 3GHz):\n", "    High frequency (3GHz to 6GHz):\n", "    Mmwave frequency (greater than 6GHz):\n"};
        String signalStrengthHeader2 = "      Signal Strength Time:\n";
        String txHeader = "      Tx Time:\n";
        String rxHeader = "      Rx Time: ";
        String[] signalStrengthDescription = {"        unknown:  ", "        poor:     ", "        moderate: ", "        good:     ", "        great:    "};
        int rat = 0;
        long totalActiveTimesMs = getMobileRadioActiveTime(j * 1000, 0) / 1000;
        sb.setLength(0);
        sb.append(prefix);
        sb.append("Active Cellular Radio Access Technology Breakdown:");
        pw.println(sb);
        boolean hasData = false;
        int numSignalStrength = CellSignalStrength.getNumSignalStrengthLevels();
        int rat2 = 2;
        while (rat2 >= 0) {
            sb.setLength(rat);
            sb.append(prefix);
            sb.append("  ");
            sb.append(RADIO_ACCESS_TECHNOLOGY_NAMES[rat2]);
            sb.append(":\n");
            sb.append(prefix);
            int numFreqLvl = rat2 == 2 ? nrFrequencyRangeDescription2.length : 1;
            int freqLvl = numFreqLvl - 1;
            boolean hasData2 = hasData;
            while (freqLvl >= 0) {
                int freqDescriptionStart = sb.length();
                boolean hasFreqData = false;
                String allFrequenciesHeader2 = allFrequenciesHeader;
                if (rat2 == 2) {
                    sb.append(nrFrequencyRangeDescription2[freqLvl]);
                } else {
                    sb.append("    All frequencies:\n");
                }
                sb.append(prefix);
                sb.append("      Signal Strength Time:\n");
                int strength = 0;
                while (true) {
                    nrFrequencyRangeDescription = nrFrequencyRangeDescription2;
                    signalStrengthHeader = signalStrengthHeader2;
                    if (strength >= numSignalStrength) {
                        break;
                    }
                    String txHeader2 = txHeader;
                    int freqDescriptionStart2 = freqDescriptionStart;
                    int rat3 = rat2;
                    String rxHeader2 = rxHeader;
                    long totalActiveTimesMs2 = totalActiveTimesMs;
                    int freqLvl2 = freqLvl;
                    int numSignalStrength2 = numSignalStrength;
                    long timeMs = getActiveRadioDurationMs(rat2, freqLvl, strength, rawRealtimeMs);
                    if (timeMs > 0) {
                        sb.append(prefix);
                        sb.append(signalStrengthDescription[strength]);
                        formatTimeMs(sb, timeMs);
                        sb.append(NavigationBarInflaterView.KEY_CODE_START);
                        sb.append(formatRatioLocked(timeMs, totalActiveTimesMs2));
                        sb.append(")\n");
                        hasFreqData = true;
                    }
                    strength++;
                    numSignalStrength = numSignalStrength2;
                    freqLvl = freqLvl2;
                    totalActiveTimesMs = totalActiveTimesMs2;
                    nrFrequencyRangeDescription2 = nrFrequencyRangeDescription;
                    signalStrengthHeader2 = signalStrengthHeader;
                    txHeader = txHeader2;
                    rat2 = rat3;
                    rxHeader = rxHeader2;
                    freqDescriptionStart = freqDescriptionStart2;
                }
                int freqDescriptionStart3 = freqDescriptionStart;
                int rat4 = rat2;
                int freqLvl3 = freqLvl;
                int numSignalStrength3 = numSignalStrength;
                String txHeader3 = txHeader;
                String rxHeader3 = rxHeader;
                long totalActiveTimesMs3 = totalActiveTimesMs;
                sb.append(prefix);
                sb.append("      Tx Time:\n");
                for (int strength2 = 0; strength2 < numSignalStrength3; strength2++) {
                    long timeMs2 = getActiveTxRadioDurationMs(rat4, freqLvl3, strength2, rawRealtimeMs);
                    if (timeMs2 > 0) {
                        sb.append(prefix);
                        sb.append(signalStrengthDescription[strength2]);
                        formatTimeMs(sb, timeMs2);
                        sb.append(NavigationBarInflaterView.KEY_CODE_START);
                        sb.append(formatRatioLocked(timeMs2, totalActiveTimesMs3));
                        sb.append(")\n");
                        hasFreqData = true;
                    }
                }
                sb.append(prefix);
                sb.append("      Rx Time: ");
                long rxTimeMs = getActiveRxRadioDurationMs(rat4, freqLvl3, rawRealtimeMs);
                formatTimeMs(sb, rxTimeMs);
                sb.append(NavigationBarInflaterView.KEY_CODE_START);
                sb.append(formatRatioLocked(rxTimeMs, totalActiveTimesMs3));
                sb.append(")\n");
                if (hasFreqData) {
                    hasData2 = true;
                    pw.print(sb);
                    sb.setLength(0);
                    sb.append(prefix);
                } else {
                    sb.setLength(freqDescriptionStart3);
                }
                int freqDescriptionStart4 = freqLvl3 - 1;
                j = rawRealtimeMs;
                numSignalStrength = numSignalStrength3;
                rat2 = rat4;
                totalActiveTimesMs = totalActiveTimesMs3;
                allFrequenciesHeader = allFrequenciesHeader2;
                nrFrequencyRangeDescription2 = nrFrequencyRangeDescription;
                signalStrengthHeader2 = signalStrengthHeader;
                txHeader = txHeader3;
                rxHeader = rxHeader3;
                freqLvl = freqDescriptionStart4;
            }
            int freqLvl4 = rat2;
            int i = freqLvl4 - 1;
            j = j;
            rat = 0;
            rat2 = i;
            numSignalStrength = numSignalStrength;
            totalActiveTimesMs = totalActiveTimesMs;
            hasData = hasData2;
            txHeader = txHeader;
            rxHeader = rxHeader;
        }
        int numSignalStrength4 = rat;
        if (!hasData) {
            sb.setLength(numSignalStrength4);
            sb.append(prefix);
            sb.append("  (no activity)");
            pw.println(sb);
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: SSATransform
        java.lang.IndexOutOfBoundsException: bitIndex < 0: -120
        	at java.base/java.util.BitSet.get(BitSet.java:624)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.fillBasicBlockInfo(LiveVarAnalysis.java:65)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.runAnalysis(LiveVarAnalysis.java:36)
        	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:55)
        	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:41)
        */
    public final void dumpCheckinLocked(android.content.Context r214, java.io.PrintWriter r215, int r216, int r217, boolean r218) {
        /*
            Method dump skipped, instructions count: 4558
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.p008os.BatteryStats.dumpCheckinLocked(android.content.Context, java.io.PrintWriter, int, int, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.BatteryStats$TimerEntry */
    /* loaded from: classes3.dex */
    public static final class TimerEntry {
        final int mId;
        final String mName;
        final long mTime;
        final Timer mTimer;

        TimerEntry(String name, int id, Timer timer, long time) {
            this.mName = name;
            this.mId = id;
            this.mTimer = timer;
            this.mTime = time;
        }
    }

    private void printmAh(PrintWriter printer, double power) {
        printer.print(formatCharge(power));
    }

    private void printmAh(StringBuilder sb, double power) {
        sb.append(formatCharge(power));
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: SSATransform
        java.lang.IndexOutOfBoundsException: bitIndex < 0: -20
        	at java.base/java.util.BitSet.get(BitSet.java:624)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.fillBasicBlockInfo(LiveVarAnalysis.java:65)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.runAnalysis(LiveVarAnalysis.java:36)
        	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:55)
        	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:41)
        */
    public final void dumpLocked(android.content.Context r232, java.io.PrintWriter r233, java.lang.String r234, int r235, int r236, boolean r237) {
        /*
            Method dump skipped, instructions count: 9540
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.p008os.BatteryStats.dumpLocked(android.content.Context, java.io.PrintWriter, java.lang.String, int, int, boolean):void");
    }

    static void printBitDescriptions(StringBuilder sb, int oldval, int newval, HistoryTag wakelockTag, BitDescription[] descriptions, boolean longNames) {
        int diff = oldval ^ newval;
        if (diff == 0) {
            return;
        }
        boolean didWake = false;
        for (BitDescription bd : descriptions) {
            if ((bd.mask & diff) != 0) {
                sb.append(longNames ? " " : ",");
                if (bd.shift < 0) {
                    sb.append((bd.mask & newval) != 0 ? "+" : NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    sb.append(longNames ? bd.name : bd.shortName);
                    if (bd.mask == 1073741824 && wakelockTag != null) {
                        didWake = true;
                        sb.append("=");
                        if (longNames) {
                            UserHandle.formatUid(sb, wakelockTag.uid);
                            sb.append(":\"");
                            sb.append(wakelockTag.string);
                            sb.append("\"");
                        } else {
                            sb.append(wakelockTag.poolIdx);
                        }
                    }
                } else {
                    sb.append(longNames ? bd.name : bd.shortName);
                    sb.append("=");
                    int val = (bd.mask & newval) >> bd.shift;
                    if (bd.values != null && val >= 0 && val < bd.values.length) {
                        sb.append(longNames ? bd.values[val] : bd.shortValues[val]);
                    } else {
                        sb.append(val);
                    }
                }
            }
        }
        if (!didWake && wakelockTag != null) {
            sb.append(longNames ? " wake_lock=" : ",w=");
            if (longNames) {
                UserHandle.formatUid(sb, wakelockTag.uid);
                sb.append(":\"");
                sb.append(wakelockTag.string);
                sb.append("\"");
                return;
            }
            sb.append(wakelockTag.poolIdx);
        }
    }

    public void prepareForDumpLocked() {
    }

    /* renamed from: android.os.BatteryStats$HistoryPrinter */
    /* loaded from: classes3.dex */
    public static class HistoryPrinter {
        int oldState = 0;
        int oldState2 = 0;
        int oldLevel = -1;
        int oldStatus = -1;
        int oldHealth = -1;
        int oldPlug = -1;
        int oldTemp = -1;
        int oldVolt = -1;
        int oldChargeMAh = -1;
        double oldModemRailChargeMah = -1.0d;
        double oldWifiRailChargeMah = -1.0d;
        long lastTime = -1;

        void reset() {
            this.oldState2 = 0;
            this.oldState = 0;
            this.oldLevel = -1;
            this.oldStatus = -1;
            this.oldHealth = -1;
            this.oldPlug = -1;
            this.oldTemp = -1;
            this.oldVolt = -1;
            this.oldChargeMAh = -1;
            this.oldModemRailChargeMah = -1.0d;
            this.oldWifiRailChargeMah = -1.0d;
        }

        public void printNextItem(PrintWriter pw, HistoryItem rec, long baseTime, boolean checkin, boolean verbose) {
            pw.print(printNextItem(rec, baseTime, checkin, verbose));
        }

        public void printNextItem(ProtoOutputStream proto, HistoryItem rec, long baseTime, boolean verbose) {
            String[] split;
            String item = printNextItem(rec, baseTime, true, verbose);
            for (String line : item.split("\n")) {
                proto.write(2237677961222L, line);
            }
        }

        private String printNextItem(HistoryItem rec, long baseTime, boolean checkin, boolean verbose) {
            StringBuilder item = new StringBuilder();
            if (rec.cpuUsageDetails != null && rec.cpuUsageDetails.cpuBracketDescriptions != null && checkin) {
                String[] descriptions = rec.cpuUsageDetails.cpuBracketDescriptions;
                for (int bracket = 0; bracket < descriptions.length; bracket++) {
                    item.append(9);
                    item.append(',');
                    item.append(BatteryStats.HISTORY_DATA);
                    item.append(",0,XB,");
                    item.append(descriptions.length);
                    item.append(',');
                    item.append(bracket);
                    item.append(',');
                    item.append(descriptions[bracket]);
                    item.append("\n");
                }
            }
            if (!checkin) {
                item.append("  ");
                TimeUtils.formatDuration(rec.time - baseTime, item, 19);
                item.append(" (");
                item.append(rec.numReadInts);
                item.append(") ");
            } else {
                item.append(9);
                item.append(',');
                item.append(BatteryStats.HISTORY_DATA);
                item.append(',');
                if (this.lastTime >= 0) {
                    item.append(rec.time - this.lastTime);
                } else {
                    item.append(rec.time - baseTime);
                }
                this.lastTime = rec.time;
            }
            if (rec.cmd == 4) {
                if (checkin) {
                    item.append(":");
                }
                item.append("START\n");
                reset();
            } else if (rec.cmd == 5 || rec.cmd == 7) {
                if (checkin) {
                    item.append(":");
                }
                if (rec.cmd == 7) {
                    item.append("RESET:");
                    reset();
                }
                item.append("TIME:");
                if (checkin) {
                    item.append(rec.currentTime);
                    item.append("\n");
                } else {
                    item.append(" ");
                    item.append(DateFormat.format("yyyy-MM-dd-HH-mm-ss", rec.currentTime).toString());
                    item.append("\n");
                }
            } else if (rec.cmd == 8) {
                if (checkin) {
                    item.append(":");
                }
                item.append("SHUTDOWN\n");
            } else if (rec.cmd == 6) {
                if (checkin) {
                    item.append(":");
                }
                item.append("*OVERFLOW*\n");
            } else {
                if (!checkin) {
                    if (rec.batteryLevel < 10) {
                        item.append("00");
                    } else if (rec.batteryLevel < 100) {
                        item.append(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
                    }
                    item.append(rec.batteryLevel);
                    if (verbose) {
                        item.append(" ");
                        if (rec.states >= 0) {
                            if (rec.states < 16) {
                                item.append("0000000");
                            } else if (rec.states < 256) {
                                item.append("000000");
                            } else if (rec.states < 4096) {
                                item.append("00000");
                            } else if (rec.states < 65536) {
                                item.append("0000");
                            } else if (rec.states < 1048576) {
                                item.append("000");
                            } else if (rec.states < 16777216) {
                                item.append("00");
                            } else if (rec.states < 268435456) {
                                item.append(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
                            }
                        }
                        item.append(Integer.toHexString(rec.states));
                    }
                } else if (this.oldLevel != rec.batteryLevel) {
                    this.oldLevel = rec.batteryLevel;
                    item.append(",Bl=");
                    item.append(rec.batteryLevel);
                }
                int i = this.oldStatus;
                byte b = rec.batteryStatus;
                String str = FullBackup.FILES_TREE_TOKEN;
                String str2 = XmlTags.ATTR_DESCRIPTION;
                if (i != b) {
                    this.oldStatus = rec.batteryStatus;
                    item.append(checkin ? ",Bs=" : " status=");
                    int i2 = this.oldStatus;
                    switch (i2) {
                        case 1:
                            item.append(checkin ? "?" : "unknown");
                            break;
                        case 2:
                            item.append(checkin ? "c" : "charging");
                            break;
                        case 3:
                            item.append(checkin ? XmlTags.ATTR_DESCRIPTION : "discharging");
                            break;
                        case 4:
                            item.append(checkin ? "n" : "not-charging");
                            break;
                        case 5:
                            item.append(checkin ? FullBackup.FILES_TREE_TOKEN : "full");
                            break;
                        default:
                            item.append(i2);
                            break;
                    }
                }
                if (this.oldHealth != rec.batteryHealth) {
                    this.oldHealth = rec.batteryHealth;
                    item.append(checkin ? ",Bh=" : " health=");
                    int i3 = this.oldHealth;
                    switch (i3) {
                        case 1:
                            item.append(checkin ? "?" : "unknown");
                            break;
                        case 2:
                            item.append(checkin ? "g" : "good");
                            break;
                        case 3:
                            item.append(checkin ? BatteryStats.HISTORY_DATA : "overheat");
                            break;
                        case 4:
                            if (!checkin) {
                                str2 = "dead";
                            }
                            item.append(str2);
                            break;
                        case 5:
                            item.append(checkin ? "v" : "over-voltage");
                            break;
                        case 6:
                            if (!checkin) {
                                str = "failure";
                            }
                            item.append(str);
                            break;
                        case 7:
                            item.append(checkin ? "c" : "cold");
                            break;
                        default:
                            item.append(i3);
                            break;
                    }
                }
                if (this.oldPlug != rec.batteryPlugType) {
                    this.oldPlug = rec.batteryPlugType;
                    item.append(checkin ? ",Bp=" : " plug=");
                    int i4 = this.oldPlug;
                    switch (i4) {
                        case 0:
                            item.append(checkin ? "n" : "none");
                            break;
                        case 1:
                            item.append(checkin ? FullBackup.APK_TREE_TOKEN : "ac");
                            break;
                        case 2:
                            item.append(checkin ? XmlTags.ATTR_UID : "usb");
                            break;
                        case 3:
                        default:
                            item.append(i4);
                            break;
                        case 4:
                            item.append(checkin ? "w" : AudioDeviceDescription.CONNECTION_WIRELESS);
                            break;
                    }
                }
                if (this.oldTemp != rec.batteryTemperature) {
                    this.oldTemp = rec.batteryTemperature;
                    item.append(checkin ? ",Bt=" : " temp=");
                    item.append(this.oldTemp);
                }
                if (this.oldVolt != rec.batteryVoltage) {
                    this.oldVolt = rec.batteryVoltage;
                    item.append(checkin ? ",Bv=" : " volt=");
                    item.append(this.oldVolt);
                }
                int chargeMAh = rec.batteryChargeUah / 1000;
                if (this.oldChargeMAh != chargeMAh) {
                    this.oldChargeMAh = chargeMAh;
                    item.append(checkin ? ",Bcc=" : " charge=");
                    item.append(this.oldChargeMAh);
                }
                if (this.oldModemRailChargeMah != rec.modemRailChargeMah) {
                    this.oldModemRailChargeMah = rec.modemRailChargeMah;
                    item.append(checkin ? ",Mrc=" : " modemRailChargemAh=");
                    item.append(new DecimalFormat("#.##").format(this.oldModemRailChargeMah));
                }
                if (this.oldWifiRailChargeMah != rec.wifiRailChargeMah) {
                    this.oldWifiRailChargeMah = rec.wifiRailChargeMah;
                    item.append(checkin ? ",Wrc=" : " wifiRailChargemAh=");
                    item.append(new DecimalFormat("#.##").format(this.oldWifiRailChargeMah));
                }
                BatteryStats.printBitDescriptions(item, this.oldState, rec.states, rec.wakelockTag, BatteryStats.HISTORY_STATE_DESCRIPTIONS, !checkin);
                BatteryStats.printBitDescriptions(item, this.oldState2, rec.states2, null, BatteryStats.HISTORY_STATE2_DESCRIPTIONS, !checkin);
                if (rec.wakeReasonTag != null) {
                    if (checkin) {
                        item.append(",wr=");
                        item.append(rec.wakeReasonTag.poolIdx);
                    } else {
                        item.append(" wake_reason=");
                        item.append(rec.wakeReasonTag.uid);
                        item.append(":\"");
                        item.append(rec.wakeReasonTag.string);
                        item.append("\"");
                    }
                }
                if (rec.eventCode != 0) {
                    item.append(checkin ? "," : " ");
                    if ((rec.eventCode & 32768) != 0) {
                        item.append("+");
                    } else if ((rec.eventCode & 16384) != 0) {
                        item.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    }
                    String[] eventNames = checkin ? BatteryStats.HISTORY_EVENT_CHECKIN_NAMES : BatteryStats.HISTORY_EVENT_NAMES;
                    int idx = rec.eventCode & HistoryItem.EVENT_TYPE_MASK;
                    if (idx >= 0 && idx < eventNames.length) {
                        item.append(eventNames[idx]);
                    } else {
                        item.append(checkin ? "Ev" : "event");
                        item.append(idx);
                    }
                    item.append("=");
                    if (checkin) {
                        item.append(rec.eventTag.poolIdx);
                    } else {
                        item.append(BatteryStats.HISTORY_EVENT_INT_FORMATTERS[idx].applyAsString(rec.eventTag.uid));
                        item.append(":\"");
                        item.append(rec.eventTag.string);
                        item.append("\"");
                    }
                }
                boolean firstExtension = true;
                if (rec.energyConsumerDetails != null) {
                    firstExtension = false;
                    if (!checkin) {
                        item.append(" ext=energy:");
                        item.append(rec.energyConsumerDetails);
                    } else {
                        item.append(",XE");
                        for (int i5 = 0; i5 < rec.energyConsumerDetails.consumers.length; i5++) {
                            if (rec.energyConsumerDetails.chargeUC[i5] != -1) {
                                item.append(',');
                                item.append(rec.energyConsumerDetails.consumers[i5].name);
                                item.append('=');
                                item.append(rec.energyConsumerDetails.chargeUC[i5]);
                            }
                        }
                    }
                }
                if (rec.cpuUsageDetails != null) {
                    if (!checkin) {
                        if (!firstExtension) {
                            item.append("\n                ");
                        }
                        String[] descriptions2 = rec.cpuUsageDetails.cpuBracketDescriptions;
                        if (descriptions2 != null) {
                            for (int bracket2 = 0; bracket2 < descriptions2.length; bracket2++) {
                                item.append(" ext=cpu-bracket:");
                                item.append(bracket2);
                                item.append(":");
                                item.append(descriptions2[bracket2]);
                                item.append("\n                ");
                            }
                        }
                        item.append(" ext=cpu:");
                        item.append(rec.cpuUsageDetails);
                    } else {
                        if (!firstExtension) {
                            item.append('\n');
                            item.append(9);
                            item.append(',');
                            item.append(BatteryStats.HISTORY_DATA);
                            item.append(",0");
                        }
                        item.append(",XC,");
                        item.append(rec.cpuUsageDetails.uid);
                        for (int i6 = 0; i6 < rec.cpuUsageDetails.cpuUsageMs.length; i6++) {
                            item.append(',');
                            item.append(rec.cpuUsageDetails.cpuUsageMs[i6]);
                        }
                    }
                }
                item.append("\n");
                if (rec.stepDetails != null) {
                    if (!checkin) {
                        item.append("                 Details: cpu=");
                        item.append(rec.stepDetails.userTime);
                        item.append("u+");
                        item.append(rec.stepDetails.systemTime);
                        item.append(XmlTags.TAG_SESSION);
                        if (rec.stepDetails.appCpuUid1 >= 0) {
                            item.append(" (");
                            printStepCpuUidDetails(item, rec.stepDetails.appCpuUid1, rec.stepDetails.appCpuUTime1, rec.stepDetails.appCpuSTime1);
                            if (rec.stepDetails.appCpuUid2 >= 0) {
                                item.append(", ");
                                printStepCpuUidDetails(item, rec.stepDetails.appCpuUid2, rec.stepDetails.appCpuUTime2, rec.stepDetails.appCpuSTime2);
                            }
                            if (rec.stepDetails.appCpuUid3 >= 0) {
                                item.append(", ");
                                printStepCpuUidDetails(item, rec.stepDetails.appCpuUid3, rec.stepDetails.appCpuUTime3, rec.stepDetails.appCpuSTime3);
                            }
                            item.append(')');
                        }
                        item.append("\n");
                        item.append("                          /proc/stat=");
                        item.append(rec.stepDetails.statUserTime);
                        item.append(" usr, ");
                        item.append(rec.stepDetails.statSystemTime);
                        item.append(" sys, ");
                        item.append(rec.stepDetails.statIOWaitTime);
                        item.append(" io, ");
                        item.append(rec.stepDetails.statIrqTime);
                        item.append(" irq, ");
                        item.append(rec.stepDetails.statSoftIrqTime);
                        item.append(" sirq, ");
                        item.append(rec.stepDetails.statIdlTime);
                        item.append(" idle");
                        int totalRun = rec.stepDetails.statUserTime + rec.stepDetails.statSystemTime + rec.stepDetails.statIOWaitTime + rec.stepDetails.statIrqTime + rec.stepDetails.statSoftIrqTime;
                        int total = rec.stepDetails.statIdlTime + totalRun;
                        if (total > 0) {
                            item.append(" (");
                            float perc = (totalRun / total) * 100.0f;
                            item.append(String.format("%.1f%%", Float.valueOf(perc)));
                            item.append(" of ");
                            StringBuilder sb = new StringBuilder(64);
                            BatteryStats.formatTimeMsNoSpace(sb, total * 10);
                            item.append((CharSequence) sb);
                            item.append(NavigationBarInflaterView.KEY_CODE_END);
                        }
                        item.append(", SubsystemPowerState ");
                        item.append(rec.stepDetails.statSubsystemPowerState);
                        item.append("\n");
                    } else {
                        item.append(9);
                        item.append(',');
                        item.append(BatteryStats.HISTORY_DATA);
                        item.append(",0,Dcpu=");
                        item.append(rec.stepDetails.userTime);
                        item.append(":");
                        item.append(rec.stepDetails.systemTime);
                        if (rec.stepDetails.appCpuUid1 >= 0) {
                            printStepCpuUidCheckinDetails(item, rec.stepDetails.appCpuUid1, rec.stepDetails.appCpuUTime1, rec.stepDetails.appCpuSTime1);
                            if (rec.stepDetails.appCpuUid2 >= 0) {
                                printStepCpuUidCheckinDetails(item, rec.stepDetails.appCpuUid2, rec.stepDetails.appCpuUTime2, rec.stepDetails.appCpuSTime2);
                            }
                            if (rec.stepDetails.appCpuUid3 >= 0) {
                                printStepCpuUidCheckinDetails(item, rec.stepDetails.appCpuUid3, rec.stepDetails.appCpuUTime3, rec.stepDetails.appCpuSTime3);
                            }
                        }
                        item.append("\n");
                        item.append(9);
                        item.append(',');
                        item.append(BatteryStats.HISTORY_DATA);
                        item.append(",0,Dpst=");
                        item.append(rec.stepDetails.statUserTime);
                        item.append(',');
                        item.append(rec.stepDetails.statSystemTime);
                        item.append(',');
                        item.append(rec.stepDetails.statIOWaitTime);
                        item.append(',');
                        item.append(rec.stepDetails.statIrqTime);
                        item.append(',');
                        item.append(rec.stepDetails.statSoftIrqTime);
                        item.append(',');
                        item.append(rec.stepDetails.statIdlTime);
                        item.append(',');
                        if (rec.stepDetails.statSubsystemPowerState != null) {
                            item.append(rec.stepDetails.statSubsystemPowerState);
                        }
                        item.append("\n");
                    }
                }
                this.oldState = rec.states;
                this.oldState2 = rec.states2;
                if ((rec.states2 & 524288) != 0) {
                    rec.states2 &= -524289;
                }
            }
            return item.toString();
        }

        private void printStepCpuUidDetails(StringBuilder sb, int uid, int utime, int stime) {
            UserHandle.formatUid(sb, uid);
            sb.append("=");
            sb.append(utime);
            sb.append("u+");
            sb.append(stime);
            sb.append(XmlTags.TAG_SESSION);
        }

        private void printStepCpuUidCheckinDetails(StringBuilder sb, int uid, int utime, int stime) {
            sb.append('/');
            sb.append(uid);
            sb.append(":");
            sb.append(utime);
            sb.append(":");
            sb.append(stime);
        }
    }

    private void printSizeValue(PrintWriter pw, long size) {
        float result = (float) size;
        String suffix = "";
        if (result >= 10240.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        pw.print((int) result);
        pw.print(suffix);
    }

    private static boolean dumpTimeEstimate(PrintWriter pw, String label1, String label2, String label3, long estimatedTime) {
        if (estimatedTime < 0) {
            return false;
        }
        pw.print(label1);
        pw.print(label2);
        pw.print(label3);
        StringBuilder sb = new StringBuilder(64);
        formatTimeMs(sb, estimatedTime);
        pw.print(sb);
        pw.println();
        return true;
    }

    private static boolean dumpDurationSteps(PrintWriter pw, String prefix, String header, LevelStepTracker steps, boolean checkin) {
        int count;
        int count2;
        String str = header;
        LevelStepTracker levelStepTracker = steps;
        char c = 0;
        if (levelStepTracker != null && (count = levelStepTracker.mNumStepDurations) > 0) {
            if (!checkin) {
                pw.println(str);
            }
            String[] lineArgs = new String[5];
            int i = 0;
            while (i < count) {
                long duration = levelStepTracker.getDurationAt(i);
                int level = levelStepTracker.getLevelAt(i);
                long initMode = levelStepTracker.getInitModeAt(i);
                long modMode = levelStepTracker.getModModeAt(i);
                if (checkin) {
                    lineArgs[c] = Long.toString(duration);
                    lineArgs[1] = Integer.toString(level);
                    if ((modMode & 3) == 0) {
                        count2 = count;
                        switch (((int) (initMode & 3)) + 1) {
                            case 1:
                                lineArgs[2] = "s-";
                                break;
                            case 2:
                                lineArgs[2] = "s+";
                                break;
                            case 3:
                                lineArgs[2] = "sd";
                                break;
                            case 4:
                                lineArgs[2] = "sds";
                                break;
                            default:
                                lineArgs[2] = "?";
                                break;
                        }
                    } else {
                        count2 = count;
                        lineArgs[2] = "";
                    }
                    if ((modMode & 4) == 0) {
                        lineArgs[3] = (initMode & 4) != 0 ? "p+" : "p-";
                    } else {
                        lineArgs[3] = "";
                    }
                    if ((modMode & 8) == 0) {
                        lineArgs[4] = (8 & initMode) != 0 ? "i+" : "i-";
                    } else {
                        lineArgs[4] = "";
                    }
                    dumpLine(pw, 0, "i", str, lineArgs);
                } else {
                    count2 = count;
                    pw.print(prefix);
                    pw.print("#");
                    pw.print(i);
                    pw.print(": ");
                    TimeUtils.formatDuration(duration, pw);
                    pw.print(" to ");
                    pw.print(level);
                    boolean haveModes = false;
                    if ((modMode & 3) == 0) {
                        pw.print(" (");
                        switch (((int) (initMode & 3)) + 1) {
                            case 1:
                                pw.print("screen-off");
                                break;
                            case 2:
                                pw.print("screen-on");
                                break;
                            case 3:
                                pw.print("screen-doze");
                                break;
                            case 4:
                                pw.print("screen-doze-suspend");
                                break;
                            default:
                                pw.print("screen-?");
                                break;
                        }
                        haveModes = true;
                    }
                    if ((modMode & 4) == 0) {
                        pw.print(haveModes ? ", " : " (");
                        pw.print((initMode & 4) != 0 ? "power-save-on" : "power-save-off");
                        haveModes = true;
                    }
                    if ((modMode & 8) == 0) {
                        pw.print(haveModes ? ", " : " (");
                        pw.print((initMode & 8) != 0 ? "device-idle-on" : "device-idle-off");
                        haveModes = true;
                    }
                    if (haveModes) {
                        pw.print(NavigationBarInflaterView.KEY_CODE_END);
                    }
                    pw.println();
                }
                i++;
                str = header;
                levelStepTracker = steps;
                count = count2;
                c = 0;
            }
            return true;
        }
        return false;
    }

    private static void dumpDurationSteps(ProtoOutputStream proto, long fieldId, LevelStepTracker steps) {
        if (steps == null) {
            return;
        }
        int count = steps.mNumStepDurations;
        for (int i = 0; i < count; i++) {
            long token = proto.start(fieldId);
            proto.write(1112396529665L, steps.getDurationAt(i));
            proto.write(1120986464258L, steps.getLevelAt(i));
            long initMode = steps.getInitModeAt(i);
            long modMode = steps.getModModeAt(i);
            int ds = 0;
            if ((modMode & 3) == 0) {
                switch (((int) (3 & initMode)) + 1) {
                    case 1:
                        ds = 2;
                        break;
                    case 2:
                        ds = 1;
                        break;
                    case 3:
                        ds = 3;
                        break;
                    case 4:
                        ds = 4;
                        break;
                    default:
                        ds = 5;
                        break;
                }
            }
            proto.write(1159641169923L, ds);
            int psm = 0;
            if ((modMode & 4) == 0) {
                psm = (4 & initMode) == 0 ? 2 : 1;
            }
            proto.write(1159641169924L, psm);
            int im = 0;
            if ((modMode & 8) == 0) {
                im = (8 & initMode) == 0 ? 3 : 2;
            }
            proto.write(1159641169925L, im);
            proto.end(token);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:130:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x009c A[Catch: all -> 0x0174, TryCatch #3 {all -> 0x0174, blocks: (B:33:0x0063, B:37:0x006f, B:45:0x009c, B:47:0x00a0, B:51:0x00a8, B:52:0x00b5, B:55:0x00c8, B:71:0x014d, B:58:0x00d5, B:59:0x00dd, B:61:0x00e3, B:62:0x00f4, B:64:0x00fa, B:68:0x011f, B:72:0x0153, B:74:0x015e, B:78:0x0166, B:39:0x0081, B:43:0x008b), top: B:114:0x0063 }] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0196  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x019f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void dumpHistory(PrintWriter pw, int flags, long histStart, boolean checkin) {
        long baseTime;
        boolean printed;
        boolean z;
        int i;
        HistoryTag oldEventTag;
        synchronized (this) {
            dumpHistoryTagPoolLocked(pw, checkin);
        }
        HistoryPrinter hprinter = new HistoryPrinter();
        BatteryStatsHistoryIterator iterator = iterateBatteryStatsHistory();
        HistoryEventTracker tracker = null;
        long baseTime2 = -1;
        long lastTime = -1;
        boolean printed2 = false;
        while (true) {
            try {
                HistoryItem rec = iterator.next();
                if (rec == null) {
                    break;
                }
                try {
                    long lastTime2 = rec.time;
                    if (baseTime2 >= 0) {
                        baseTime = baseTime2;
                    } else {
                        baseTime = lastTime2;
                    }
                    try {
                        if (rec.time >= histStart) {
                            if (histStart >= 0 && !printed2) {
                                if (rec.cmd != 5 && rec.cmd != 7 && rec.cmd != 4 && rec.cmd != 8) {
                                    if (rec.currentTime == 0) {
                                        printed = printed2;
                                        z = false;
                                    } else {
                                        printed = true;
                                        try {
                                            byte cmd = rec.cmd;
                                            rec.cmd = (byte) 5;
                                            hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & 32) != 0);
                                            rec.cmd = cmd;
                                            z = false;
                                        } catch (Throwable th) {
                                            t = th;
                                            lastTime = lastTime2;
                                            t.printStackTrace(pw);
                                            Slog.wtf(TAG, "Corrupted battery history", t);
                                            if (iterator != null) {
                                            }
                                            if (histStart < 0) {
                                            }
                                        }
                                    }
                                    if (tracker != null) {
                                        if (rec.cmd != 0) {
                                            hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & 32) != 0 ? true : z ? 1 : 0);
                                            rec.cmd = z ? (byte) 1 : (byte) 0;
                                        }
                                        int oldEventCode = rec.eventCode;
                                        HistoryTag oldEventTag2 = rec.eventTag;
                                        rec.eventTag = new HistoryTag();
                                        int i2 = 0;
                                        while (i2 < 22) {
                                            Map<String, SparseIntArray> active = tracker.getStateForEvent(i2);
                                            if (active == null) {
                                                i = i2;
                                                oldEventTag = oldEventTag2;
                                            } else {
                                                for (Map.Entry<String, SparseIntArray> ent : active.entrySet()) {
                                                    SparseIntArray uids = ent.getValue();
                                                    int j = 0;
                                                    while (j < uids.size()) {
                                                        rec.eventCode = i2;
                                                        rec.eventTag.string = ent.getKey();
                                                        rec.eventTag.uid = uids.keyAt(j);
                                                        rec.eventTag.poolIdx = uids.valueAt(j);
                                                        hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & 32) != 0 ? true : z);
                                                        rec.wakeReasonTag = null;
                                                        rec.wakelockTag = null;
                                                        j++;
                                                        oldEventTag2 = oldEventTag2;
                                                        uids = uids;
                                                        i2 = i2;
                                                        z = false;
                                                    }
                                                    z = false;
                                                }
                                                i = i2;
                                                oldEventTag = oldEventTag2;
                                            }
                                            i2 = i + 1;
                                            oldEventTag2 = oldEventTag;
                                            z = false;
                                        }
                                        rec.eventCode = oldEventCode;
                                        rec.eventTag = oldEventTag2;
                                        tracker = null;
                                    }
                                }
                                printed = true;
                                z = false;
                                hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & 32) != 0);
                                rec.cmd = (byte) 0;
                                if (tracker != null) {
                                }
                            } else {
                                printed = printed2;
                            }
                            hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & 32) != 0);
                            printed2 = printed;
                        }
                        lastTime = lastTime2;
                        baseTime2 = baseTime;
                    } catch (Throwable th2) {
                        t = th2;
                        lastTime = lastTime2;
                    }
                } catch (Throwable th3) {
                    t = th3;
                }
            } catch (Throwable th4) {
                if (iterator != null) {
                    try {
                        iterator.close();
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                    }
                }
                throw th4;
            }
        }
        if (iterator != null) {
            iterator.close();
        }
        if (histStart < 0) {
            commitCurrentHistoryBatchLocked();
            pw.print(checkin ? "NEXT: " : "  NEXT: ");
            pw.println(1 + lastTime);
        }
    }

    private void dumpHistoryTagPoolLocked(PrintWriter pw, boolean checkin) {
        if (checkin) {
            for (int i = 0; i < getHistoryStringPoolSize(); i++) {
                pw.print(9);
                pw.print(',');
                pw.print(HISTORY_STRING_POOL);
                pw.print(',');
                pw.print(i);
                pw.print(",");
                pw.print(getHistoryTagPoolUid(i));
                pw.print(",\"");
                String str = getHistoryTagPoolString(i);
                if (str != null) {
                    pw.print(str.replace("\\", "\\\\").replace("\"", "\\\""));
                }
                pw.print("\"");
                pw.println();
            }
            return;
        }
        long historyTotalSize = getHistoryTotalSize();
        long historyUsedSize = getHistoryUsedSize();
        pw.print("Battery History (");
        pw.print((100 * historyUsedSize) / historyTotalSize);
        pw.print("% used, ");
        printSizeValue(pw, historyUsedSize);
        pw.print(" used of ");
        printSizeValue(pw, historyTotalSize);
        pw.print(", ");
        pw.print(getHistoryStringPoolSize());
        pw.print(" strings using ");
        printSizeValue(pw, getHistoryStringPoolBytes());
        pw.println("):");
    }

    private void dumpDailyLevelStepSummary(PrintWriter pw, String prefix, String label, LevelStepTracker steps, StringBuilder tmpSb, int[] tmpOutInt) {
        int[] iArr;
        if (steps == null) {
            return;
        }
        long timeRemaining = steps.computeTimeEstimate(0L, 0L, tmpOutInt);
        if (timeRemaining >= 0) {
            pw.print(prefix);
            pw.print(label);
            pw.print(" total time: ");
            tmpSb.setLength(0);
            formatTimeMs(tmpSb, timeRemaining);
            pw.print(tmpSb);
            pw.print(" (from ");
            pw.print(tmpOutInt[0]);
            pw.println(" steps)");
        }
        int i = 0;
        while (true) {
            if (i < STEP_LEVEL_MODES_OF_INTEREST.length) {
                int i2 = i;
                long estimatedTime = steps.computeTimeEstimate(iArr[i], STEP_LEVEL_MODE_VALUES[i], tmpOutInt);
                if (estimatedTime > 0) {
                    pw.print(prefix);
                    pw.print(label);
                    pw.print(" ");
                    pw.print(STEP_LEVEL_MODE_LABELS[i2]);
                    pw.print(" time: ");
                    tmpSb.setLength(0);
                    formatTimeMs(tmpSb, estimatedTime);
                    pw.print(tmpSb);
                    pw.print(" (from ");
                    pw.print(tmpOutInt[0]);
                    pw.println(" steps)");
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private void dumpDailyPackageChanges(PrintWriter pw, String prefix, ArrayList<PackageChange> changes) {
        if (changes == null) {
            return;
        }
        pw.print(prefix);
        pw.println("Package changes:");
        for (int i = 0; i < changes.size(); i++) {
            PackageChange pc = changes.get(i);
            if (pc.mUpdate) {
                pw.print(prefix);
                pw.print("  Update ");
                pw.print(pc.mPackageName);
                pw.print(" vers=");
                pw.println(pc.mVersionCode);
            } else {
                pw.print(prefix);
                pw.print("  Uninstall ");
                pw.println(pc.mPackageName);
            }
        }
    }

    public void dump(Context context, PrintWriter pw, int flags, int reqUid, long histStart) {
        synchronized (this) {
            prepareForDumpLocked();
        }
        boolean filtering = (flags & 14) != 0;
        if ((flags & 8) != 0 || !filtering) {
            dumpHistory(pw, flags, histStart, false);
            pw.println();
        }
        if (filtering && (flags & 6) == 0) {
            return;
        }
        synchronized (this) {
            dumpLocked(context, pw, flags, reqUid, filtering);
        }
    }

    private void dumpLocked(Context context, PrintWriter pw, int flags, int reqUid, boolean filtering) {
        int[] iArr;
        String str;
        ArrayList<PackageChange> pkgc;
        LevelStepTracker csteps;
        LevelStepTracker dsteps;
        int[] outInt;
        CharSequence charSequence;
        boolean z;
        boolean z2;
        DailyItem dit;
        LevelStepTracker dsteps2;
        boolean z3;
        String str2;
        SparseArray<? extends Uid> uidStats;
        int NU;
        long j;
        BatteryStats batteryStats = this;
        if (!filtering) {
            SparseArray<? extends Uid> uidStats2 = getUidStats();
            int NU2 = uidStats2.size();
            boolean didPid = false;
            long nowRealtime = SystemClock.elapsedRealtime();
            int i = 0;
            while (i < NU2) {
                Uid uid = uidStats2.valueAt(i);
                SparseArray<? extends Uid.Pid> pids = uid.getPidStats();
                if (pids != null) {
                    int j2 = 0;
                    while (j2 < pids.size()) {
                        Uid.Pid pid = pids.valueAt(j2);
                        if (!didPid) {
                            pw.println("Per-PID Stats:");
                            didPid = true;
                        }
                        long j3 = pid.mWakeSumMs;
                        if (pid.mWakeNesting > 0) {
                            uidStats = uidStats2;
                            NU = NU2;
                            j = nowRealtime - pid.mWakeStartMs;
                        } else {
                            uidStats = uidStats2;
                            NU = NU2;
                            j = 0;
                        }
                        long time = j3 + j;
                        pw.print("  PID ");
                        pw.print(pids.keyAt(j2));
                        pw.print(" wake time: ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println("");
                        j2++;
                        uidStats2 = uidStats;
                        NU2 = NU;
                    }
                }
                i++;
                uidStats2 = uidStats2;
                NU2 = NU2;
            }
            if (didPid) {
                pw.println();
            }
        }
        boolean z4 = false;
        if (!filtering || (flags & 2) != 0) {
            if (dumpDurationSteps(pw, "  ", "Discharge step durations:", getDischargeLevelStepTracker(), false)) {
                long timeRemaining = batteryStats.computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (timeRemaining >= 0) {
                    pw.print("  Estimated discharge time remaining: ");
                    TimeUtils.formatDuration(timeRemaining / 1000, pw);
                    pw.println();
                }
                LevelStepTracker steps = getDischargeLevelStepTracker();
                int i2 = 0;
                while (true) {
                    if (i2 >= STEP_LEVEL_MODES_OF_INTEREST.length) {
                        break;
                    }
                    dumpTimeEstimate(pw, "  Estimated ", STEP_LEVEL_MODE_LABELS[i2], " time: ", steps.computeTimeEstimate(iArr[i2], STEP_LEVEL_MODE_VALUES[i2], null));
                    i2++;
                }
                pw.println();
            }
            z4 = false;
            if (!dumpDurationSteps(pw, "  ", "Charge step durations:", getChargeLevelStepTracker(), false)) {
                batteryStats = this;
            } else {
                batteryStats = this;
                long timeRemaining2 = batteryStats.computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (timeRemaining2 >= 0) {
                    pw.print("  Estimated charge time remaining: ");
                    TimeUtils.formatDuration(timeRemaining2 / 1000, pw);
                    pw.println();
                }
                pw.println();
            }
        }
        if (filtering && (flags & 4) == 0) {
            z2 = z4;
        } else {
            pw.println("Daily stats:");
            pw.print("  Current start time: ");
            pw.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getCurrentDailyStartTime()).toString());
            pw.print("  Next min deadline: ");
            pw.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMinDailyDeadline()).toString());
            pw.print("  Next max deadline: ");
            pw.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMaxDailyDeadline()).toString());
            StringBuilder sb = new StringBuilder(64);
            int[] outInt2 = new int[1];
            LevelStepTracker dsteps3 = getDailyDischargeLevelStepTracker();
            LevelStepTracker csteps2 = getDailyChargeLevelStepTracker();
            ArrayList<PackageChange> pkgc2 = getDailyPackageChanges();
            if (dsteps3.mNumStepDurations <= 0 && csteps2.mNumStepDurations <= 0 && pkgc2 == null) {
                str = "    ";
                dsteps = dsteps3;
                outInt = outInt2;
                charSequence = "yyyy-MM-dd-HH-mm-ss";
                z = z4;
            } else {
                if ((flags & 4) != 0) {
                    str = "    ";
                    pkgc = pkgc2;
                    csteps = csteps2;
                    dsteps = dsteps3;
                    outInt = outInt2;
                    charSequence = "yyyy-MM-dd-HH-mm-ss";
                    z = z4;
                } else if (!filtering) {
                    str = "    ";
                    pkgc = pkgc2;
                    csteps = csteps2;
                    dsteps = dsteps3;
                    outInt = outInt2;
                    charSequence = "yyyy-MM-dd-HH-mm-ss";
                    z = z4;
                } else {
                    pw.println("  Current daily steps:");
                    str = "    ";
                    dumpDailyLevelStepSummary(pw, "    ", "Discharge", dsteps3, sb, outInt2);
                    dsteps = dsteps3;
                    outInt = outInt2;
                    charSequence = "yyyy-MM-dd-HH-mm-ss";
                    z = z4;
                    dumpDailyLevelStepSummary(pw, "    ", "Charge", csteps2, sb, outInt);
                }
                if (dumpDurationSteps(pw, str, "  Current daily discharge step durations:", dsteps, z)) {
                    dumpDailyLevelStepSummary(pw, "      ", "Discharge", dsteps, sb, outInt);
                }
                if (dumpDurationSteps(pw, str, "  Current daily charge step durations:", csteps, z)) {
                    dumpDailyLevelStepSummary(pw, "      ", "Charge", csteps, sb, outInt);
                }
                batteryStats.dumpDailyPackageChanges(pw, str, pkgc);
            }
            int curIndex = 0;
            while (true) {
                DailyItem dit2 = batteryStats.getDailyItemLocked(curIndex);
                if (dit2 == null) {
                    break;
                }
                int curIndex2 = curIndex + 1;
                int curIndex3 = flags & 4;
                if (curIndex3 != 0) {
                    pw.println();
                }
                pw.print("  Daily from ");
                CharSequence charSequence2 = charSequence;
                pw.print(DateFormat.format(charSequence2, dit2.mStartTime).toString());
                pw.print(" to ");
                pw.print(DateFormat.format(charSequence2, dit2.mEndTime).toString());
                pw.println(":");
                if ((flags & 4) != 0) {
                    charSequence = charSequence2;
                    dit = dit2;
                } else if (filtering) {
                    charSequence = charSequence2;
                    int[] iArr2 = outInt;
                    dumpDailyLevelStepSummary(pw, "    ", "Discharge", dit2.mDischargeSteps, sb, iArr2);
                    dumpDailyLevelStepSummary(pw, "    ", "Charge", dit2.mChargeSteps, sb, iArr2);
                    dsteps2 = dsteps;
                    z3 = false;
                    z = z3;
                    curIndex = curIndex2;
                    dsteps = dsteps2;
                } else {
                    charSequence = charSequence2;
                    dit = dit2;
                }
                if (!dumpDurationSteps(pw, "      ", "    Discharge step durations:", dit.mDischargeSteps, false)) {
                    dsteps2 = dsteps;
                    str2 = "      ";
                } else {
                    dsteps2 = dsteps;
                    str2 = "      ";
                    dumpDailyLevelStepSummary(pw, "        ", "Discharge", dit.mDischargeSteps, sb, outInt);
                }
                if (!dumpDurationSteps(pw, str2, "    Charge step durations:", dit.mChargeSteps, false)) {
                    z3 = false;
                } else {
                    z3 = false;
                    dumpDailyLevelStepSummary(pw, "        ", "Charge", dit.mChargeSteps, sb, outInt);
                }
                batteryStats.dumpDailyPackageChanges(pw, str, dit.mPackageChanges);
                z = z3;
                curIndex = curIndex2;
                dsteps = dsteps2;
            }
            z2 = z;
            pw.println();
        }
        if (!filtering || (flags & 2) != 0) {
            pw.println("Statistics since last charge:");
            pw.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
            dumpLocked(context, pw, "", 0, reqUid, (flags & 64) != 0 ? true : z2);
            pw.println();
        }
    }

    public void dumpCheckin(Context context, PrintWriter pw, List<ApplicationInfo> apps, int flags, long histStart) {
        synchronized (this) {
            prepareForDumpLocked();
            dumpLine(pw, 0, "i", VERSION_DATA, 36, Integer.valueOf(getParcelVersion()), getStartPlatformVersion(), getEndPlatformVersion());
        }
        if ((flags & 24) != 0) {
            dumpHistory(pw, flags, histStart, true);
        }
        if ((flags & 8) != 0) {
            return;
        }
        synchronized (this) {
            dumpCheckinLocked(context, pw, apps, flags);
        }
    }

    private void dumpCheckinLocked(Context context, PrintWriter pw, List<ApplicationInfo> apps, int flags) {
        if (apps != null) {
            SparseArray<Pair<ArrayList<String>, MutableBoolean>> uids = new SparseArray<>();
            for (int i = 0; i < apps.size(); i++) {
                ApplicationInfo ai = apps.get(i);
                Pair<ArrayList<String>, MutableBoolean> pkgs = uids.get(UserHandle.getAppId(ai.uid));
                if (pkgs == null) {
                    pkgs = new Pair<>(new ArrayList(), new MutableBoolean(false));
                    uids.put(UserHandle.getAppId(ai.uid), pkgs);
                }
                ((ArrayList) pkgs.first).add(ai.packageName);
            }
            SparseArray<? extends Uid> uidStats = getUidStats();
            int NU = uidStats.size();
            String[] lineArgs = new String[2];
            for (int i2 = 0; i2 < NU; i2++) {
                int uid = UserHandle.getAppId(uidStats.keyAt(i2));
                Pair<ArrayList<String>, MutableBoolean> pkgs2 = uids.get(uid);
                if (pkgs2 != null && !((MutableBoolean) pkgs2.second).value) {
                    ((MutableBoolean) pkgs2.second).value = true;
                    for (int j = 0; j < ((ArrayList) pkgs2.first).size(); j++) {
                        lineArgs[0] = Integer.toString(uid);
                        lineArgs[1] = (String) ((ArrayList) pkgs2.first).get(j);
                        dumpLine(pw, 0, "i", "uid", lineArgs);
                    }
                }
            }
        }
        if ((flags & 4) == 0) {
            dumpDurationSteps(pw, "", DISCHARGE_STEP_DATA, getDischargeLevelStepTracker(), true);
            String[] lineArgs2 = new String[1];
            long timeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (timeRemaining >= 0) {
                lineArgs2[0] = Long.toString(timeRemaining);
                dumpLine(pw, 0, "i", DISCHARGE_TIME_REMAIN_DATA, lineArgs2);
            }
            dumpDurationSteps(pw, "", CHARGE_STEP_DATA, getChargeLevelStepTracker(), true);
            long timeRemaining2 = computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (timeRemaining2 >= 0) {
                lineArgs2[0] = Long.toString(timeRemaining2);
                dumpLine(pw, 0, "i", CHARGE_TIME_REMAIN_DATA, lineArgs2);
            }
            dumpCheckinLocked(context, pw, 0, -1, (flags & 64) != 0);
        }
    }

    public void dumpProtoLocked(Context context, FileDescriptor fd, List<ApplicationInfo> apps, int flags, long histStart) {
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        prepareForDumpLocked();
        if ((flags & 24) != 0) {
            dumpProtoHistoryLocked(proto, flags, histStart);
            proto.flush();
            return;
        }
        long bToken = proto.start(1146756268033L);
        proto.write(1120986464257L, 36);
        proto.write(1112396529666L, getParcelVersion());
        proto.write(1138166333443L, getStartPlatformVersion());
        proto.write(1138166333444L, getEndPlatformVersion());
        if ((flags & 4) == 0) {
            BatteryUsageStats stats = getBatteryUsageStats(context, false);
            ProportionalAttributionCalculator proportionalAttributionCalculator = new ProportionalAttributionCalculator(context, stats);
            dumpProtoAppsLocked(proto, stats, apps, proportionalAttributionCalculator);
            dumpProtoSystemLocked(proto, stats);
        }
        proto.end(bToken);
        proto.flush();
    }

    private void dumpProtoAppsLocked(ProtoOutputStream proto, BatteryUsageStats stats, List<ApplicationInfo> apps, ProportionalAttributionCalculator proportionalAttributionCalculator) {
        long rawRealtimeUs;
        SparseArray<ArrayList<String>> aidToPackages;
        long rawRealtimeMs;
        long j;
        long[] cpuFreqs;
        long rawRealtimeMs2;
        SparseArray<? extends Uid.Sensor> sensors;
        UidBatteryConsumer consumer;
        long[] timesInFreqMs;
        long[] timesInFreqScreenOffMs;
        int iu;
        ArrayList<String> pkgs;
        long[] cpuFreqs2;
        SparseArray<UidBatteryConsumer> uidToConsumer;
        ProtoOutputStream protoOutputStream;
        long uTkn;
        int uid;
        long rawRealtimeMs3;
        long batteryUptimeUs;
        SparseArray<? extends Uid> uidStats;
        Uid u;
        ArrayMap<String, ? extends Uid.Pkg> packageStats;
        ArrayList<String> pkgs2;
        Uid u2;
        ArrayMap<String, ? extends Uid.Pkg> packageStats2;
        ArrayList<String> pkgs3;
        ProtoOutputStream protoOutputStream2 = proto;
        int which = 0;
        long rawUptimeUs = SystemClock.uptimeMillis() * 1000;
        long rawRealtimeMs4 = SystemClock.elapsedRealtime();
        long rawRealtimeUs2 = rawRealtimeMs4 * 1000;
        long batteryUptimeUs2 = getBatteryUptime(rawUptimeUs);
        SparseArray<ArrayList<String>> aidToPackages2 = new SparseArray<>();
        if (apps == null) {
            rawRealtimeUs = rawRealtimeUs2;
        } else {
            int i = 0;
            while (i < apps.size()) {
                ApplicationInfo ai = apps.get(i);
                long rawRealtimeUs3 = rawRealtimeUs2;
                int aid = UserHandle.getAppId(ai.uid);
                ArrayList<String> pkgs4 = aidToPackages2.get(aid);
                if (pkgs4 == null) {
                    pkgs4 = new ArrayList<>();
                    aidToPackages2.put(aid, pkgs4);
                }
                pkgs4.add(ai.packageName);
                i++;
                rawRealtimeUs2 = rawRealtimeUs3;
            }
            rawRealtimeUs = rawRealtimeUs2;
        }
        SparseArray<UidBatteryConsumer> uidToConsumer2 = new SparseArray<>();
        List<UidBatteryConsumer> consumers = stats.getUidBatteryConsumers();
        int i2 = consumers.size() - 1;
        while (i2 >= 0) {
            UidBatteryConsumer bs = consumers.get(i2);
            uidToConsumer2.put(bs.getUid(), bs);
            i2--;
            consumers = consumers;
        }
        List<UidBatteryConsumer> consumers2 = consumers;
        SparseArray<? extends Uid> uidStats2 = getUidStats();
        int n = uidStats2.size();
        int iu2 = 0;
        while (iu2 < n) {
            int n2 = n;
            SparseArray<UidBatteryConsumer> uidToConsumer3 = uidToConsumer2;
            long uTkn2 = protoOutputStream2.start(2246267895813L);
            Uid u3 = uidStats2.valueAt(iu2);
            int which2 = which;
            int uid2 = uidStats2.keyAt(iu2);
            long rawUptimeUs2 = rawUptimeUs;
            protoOutputStream2.write(1120986464257L, uid2);
            ArrayList<String> pkgs5 = aidToPackages2.get(UserHandle.getAppId(uid2));
            if (pkgs5 == null) {
                pkgs5 = new ArrayList<>();
            }
            ArrayMap<String, ? extends Uid.Pkg> packageStats3 = u3.getPackageStats();
            int iu3 = iu2;
            int iu4 = packageStats3.size() - 1;
            while (true) {
                aidToPackages = aidToPackages2;
                if (iu4 < 0) {
                    break;
                }
                String pkg = packageStats3.keyAt(iu4);
                ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats = packageStats3.valueAt(iu4).getServiceStats();
                if (serviceStats.size() == 0) {
                    protoOutputStream = proto;
                    batteryUptimeUs = batteryUptimeUs2;
                    uTkn = uTkn2;
                    uidStats = uidStats2;
                    u = u3;
                    uid = uid2;
                    pkgs2 = pkgs5;
                    packageStats = packageStats3;
                    rawRealtimeMs3 = rawRealtimeMs4;
                } else {
                    protoOutputStream = proto;
                    uTkn = uTkn2;
                    uid = uid2;
                    ArrayMap<String, ? extends Uid.Pkg> packageStats4 = packageStats3;
                    rawRealtimeMs3 = rawRealtimeMs4;
                    long pToken = protoOutputStream.start(2246267895810L);
                    protoOutputStream.write(1138166333441L, pkg);
                    pkgs5.remove(pkg);
                    int isvc = serviceStats.size() - 1;
                    while (isvc >= 0) {
                        Uid.Pkg.Serv ss = serviceStats.valueAt(isvc);
                        String pkg2 = pkg;
                        SparseArray<? extends Uid> uidStats3 = uidStats2;
                        long startTimeMs = roundUsToMs(ss.getStartTime(batteryUptimeUs2, 0));
                        long batteryUptimeUs3 = batteryUptimeUs2;
                        int starts = ss.getStarts(0);
                        int launches = ss.getLaunches(0);
                        if (startTimeMs == 0 && starts == 0 && launches == 0) {
                            u2 = u3;
                            packageStats2 = packageStats4;
                            pkgs3 = pkgs5;
                        } else {
                            u2 = u3;
                            long sToken = protoOutputStream.start(2246267895810L);
                            packageStats2 = packageStats4;
                            pkgs3 = pkgs5;
                            protoOutputStream.write(1138166333441L, serviceStats.keyAt(isvc));
                            protoOutputStream.write(1112396529666L, startTimeMs);
                            protoOutputStream.write(1120986464259L, starts);
                            protoOutputStream.write(1120986464260L, launches);
                            protoOutputStream.end(sToken);
                        }
                        isvc--;
                        pkgs5 = pkgs3;
                        pkg = pkg2;
                        uidStats2 = uidStats3;
                        batteryUptimeUs2 = batteryUptimeUs3;
                        u3 = u2;
                        packageStats4 = packageStats2;
                    }
                    batteryUptimeUs = batteryUptimeUs2;
                    uidStats = uidStats2;
                    u = u3;
                    packageStats = packageStats4;
                    pkgs2 = pkgs5;
                    protoOutputStream.end(pToken);
                }
                iu4--;
                pkgs5 = pkgs2;
                aidToPackages2 = aidToPackages;
                uid2 = uid;
                uidStats2 = uidStats;
                uTkn2 = uTkn;
                rawRealtimeMs4 = rawRealtimeMs3;
                batteryUptimeUs2 = batteryUptimeUs;
                u3 = u;
                packageStats3 = packageStats;
            }
            long batteryUptimeUs4 = batteryUptimeUs2;
            long uTkn3 = uTkn2;
            SparseArray<? extends Uid> uidStats4 = uidStats2;
            Uid u4 = u3;
            int uid3 = uid2;
            ArrayList<String> pkgs6 = pkgs5;
            ArrayMap<String, ? extends Uid.Pkg> packageStats5 = packageStats3;
            long rawRealtimeMs5 = rawRealtimeMs4;
            Iterator<String> it = pkgs6.iterator();
            while (it.hasNext()) {
                String p = it.next();
                long pToken2 = proto.start(2246267895810L);
                proto.write(1138166333441L, p);
                proto.end(pToken2);
            }
            if (u4.getAggregatedPartialWakelockTimer() == null) {
                rawRealtimeMs = rawRealtimeMs5;
            } else {
                Timer timer = u4.getAggregatedPartialWakelockTimer();
                rawRealtimeMs = rawRealtimeMs5;
                long totTimeMs = timer.getTotalDurationMsLocked(rawRealtimeMs);
                Timer bgTimer = timer.getSubTimer();
                long bgTimeMs = bgTimer != null ? bgTimer.getTotalDurationMsLocked(rawRealtimeMs) : 0L;
                long awToken = proto.start(1146756268056L);
                proto.write(1112396529665L, totTimeMs);
                proto.write(1112396529666L, bgTimeMs);
                proto.end(awToken);
            }
            int iu5 = iu3;
            long rawRealtimeUs4 = rawRealtimeUs;
            List<UidBatteryConsumer> consumers3 = consumers2;
            SparseArray<UidBatteryConsumer> uidToConsumer4 = uidToConsumer3;
            dumpTimer(proto, 1146756268040L, u4.getAudioTurnedOnTimer(), rawRealtimeUs4, 0);
            dumpControllerActivityProto(proto, 1146756268035L, u4.getBluetoothControllerActivity(), 0);
            Timer bleTimer = u4.getBluetoothScanTimer();
            if (bleTimer != null) {
                long bmToken = proto.start(1146756268038L);
                dumpTimer(proto, 1146756268033L, bleTimer, rawRealtimeUs4, 0);
                dumpTimer(proto, 1146756268034L, u4.getBluetoothScanBackgroundTimer(), rawRealtimeUs4, 0);
                dumpTimer(proto, 1146756268035L, u4.getBluetoothUnoptimizedScanTimer(), rawRealtimeUs4, 0);
                dumpTimer(proto, 1146756268036L, u4.getBluetoothUnoptimizedScanBackgroundTimer(), rawRealtimeUs4, 0);
                j = 1120986464261L;
                proto.write(1120986464261L, u4.getBluetoothScanResultCounter() != null ? u4.getBluetoothScanResultCounter().getCountLocked(0) : 0);
                proto.write(1120986464262L, u4.getBluetoothScanResultBgCounter() != null ? u4.getBluetoothScanResultBgCounter().getCountLocked(0) : 0);
                proto.end(bmToken);
            } else {
                j = 1120986464261L;
            }
            dumpTimer(proto, 1146756268041L, u4.getCameraTurnedOnTimer(), rawRealtimeUs4, 0);
            long cpuToken = proto.start(1146756268039L);
            proto.write(1112396529665L, roundUsToMs(u4.getUserCpuTimeUs(0)));
            proto.write(1112396529666L, roundUsToMs(u4.getSystemCpuTimeUs(0)));
            long[] cpuFreqs3 = getCpuFreqs();
            if (cpuFreqs3 == null) {
                cpuFreqs = cpuFreqs3;
                rawRealtimeMs2 = rawRealtimeMs;
            } else {
                long[] cpuFreqTimeMs = u4.getCpuFreqTimes(0);
                if (cpuFreqTimeMs == null || cpuFreqTimeMs.length != cpuFreqs3.length) {
                    cpuFreqs = cpuFreqs3;
                    rawRealtimeMs2 = rawRealtimeMs;
                } else {
                    long[] screenOffCpuFreqTimeMs = u4.getScreenOffCpuFreqTimes(0);
                    if (screenOffCpuFreqTimeMs == null) {
                        screenOffCpuFreqTimeMs = new long[cpuFreqTimeMs.length];
                    }
                    int ic = 0;
                    while (ic < cpuFreqTimeMs.length) {
                        long cToken = proto.start(2246267895811L);
                        proto.write(1120986464257L, ic + 1);
                        proto.write(1112396529666L, cpuFreqTimeMs[ic]);
                        proto.write(1112396529667L, screenOffCpuFreqTimeMs[ic]);
                        proto.end(cToken);
                        ic++;
                        cpuFreqs3 = cpuFreqs3;
                        cpuFreqTimeMs = cpuFreqTimeMs;
                        rawRealtimeMs = rawRealtimeMs;
                    }
                    cpuFreqs = cpuFreqs3;
                    rawRealtimeMs2 = rawRealtimeMs;
                }
            }
            long[] timesInFreqMs2 = new long[getCpuFreqCount()];
            long[] timesInFreqScreenOffMs2 = new long[getCpuFreqCount()];
            int procState = 0;
            while (procState < 7) {
                if (!u4.getCpuFreqTimes(timesInFreqMs2, procState)) {
                    iu = iu5;
                    pkgs = pkgs6;
                    cpuFreqs2 = cpuFreqs;
                    uidToConsumer = uidToConsumer4;
                } else {
                    if (!u4.getScreenOffCpuFreqTimes(timesInFreqScreenOffMs2, procState)) {
                        Arrays.fill(timesInFreqScreenOffMs2, 0L);
                    }
                    long procToken = proto.start(2246267895812L);
                    proto.write(1159641169921L, procState);
                    int ic2 = 0;
                    while (ic2 < timesInFreqMs2.length) {
                        int iu6 = iu5;
                        long cToken2 = proto.start(2246267895810L);
                        proto.write(1120986464257L, ic2 + 1);
                        proto.write(1112396529666L, timesInFreqMs2[ic2]);
                        proto.write(1112396529667L, timesInFreqScreenOffMs2[ic2]);
                        proto.end(cToken2);
                        ic2++;
                        pkgs6 = pkgs6;
                        iu5 = iu6;
                        uidToConsumer4 = uidToConsumer4;
                        cpuFreqs = cpuFreqs;
                    }
                    iu = iu5;
                    pkgs = pkgs6;
                    cpuFreqs2 = cpuFreqs;
                    uidToConsumer = uidToConsumer4;
                    proto.end(procToken);
                }
                procState++;
                pkgs6 = pkgs;
                iu5 = iu;
                uidToConsumer4 = uidToConsumer;
                cpuFreqs = cpuFreqs2;
            }
            int iu7 = iu5;
            SparseArray<UidBatteryConsumer> uidToConsumer5 = uidToConsumer4;
            long j2 = 2246267895810L;
            proto.end(cpuToken);
            dumpTimer(proto, 1146756268042L, u4.getFlashlightTurnedOnTimer(), rawRealtimeUs4, 0);
            dumpTimer(proto, 1146756268043L, u4.getForegroundActivityTimer(), rawRealtimeUs4, 0);
            dumpTimer(proto, 1146756268044L, u4.getForegroundServiceTimer(), rawRealtimeUs4, 0);
            ArrayMap<String, SparseIntArray> completions = u4.getJobCompletionStats();
            int ic3 = 0;
            while (ic3 < completions.size()) {
                SparseIntArray types = completions.valueAt(ic3);
                if (types != null) {
                    long jcToken = proto.start(2246267895824L);
                    proto.write(1138166333441L, completions.keyAt(ic3));
                    int[] jobStopReasonCodes = JobParameters.getJobStopReasonCodes();
                    int length = jobStopReasonCodes.length;
                    int i3 = 0;
                    while (i3 < length) {
                        int r = jobStopReasonCodes[i3];
                        long[] timesInFreqMs3 = timesInFreqMs2;
                        long rToken = proto.start(j2);
                        proto.write(1159641169921L, r);
                        proto.write(1120986464258L, types.get(r, 0));
                        proto.end(rToken);
                        i3++;
                        timesInFreqMs2 = timesInFreqMs3;
                        timesInFreqScreenOffMs2 = timesInFreqScreenOffMs2;
                        types = types;
                        jcToken = jcToken;
                        j2 = 2246267895810L;
                    }
                    timesInFreqMs = timesInFreqMs2;
                    timesInFreqScreenOffMs = timesInFreqScreenOffMs2;
                    proto.end(jcToken);
                } else {
                    timesInFreqMs = timesInFreqMs2;
                    timesInFreqScreenOffMs = timesInFreqScreenOffMs2;
                }
                ic3++;
                timesInFreqMs2 = timesInFreqMs;
                timesInFreqScreenOffMs2 = timesInFreqScreenOffMs;
                j2 = 2246267895810L;
            }
            long[] timesInFreqMs4 = timesInFreqMs2;
            long j3 = 1120986464258L;
            ArrayMap<String, ? extends Timer> jobs = u4.getJobStats();
            int ij = jobs.size() - 1;
            while (ij >= 0) {
                Timer timer2 = jobs.valueAt(ij);
                Timer bgTimer2 = timer2.getSubTimer();
                long jToken = proto.start(2246267895823L);
                proto.write(1138166333441L, jobs.keyAt(ij));
                dumpTimer(proto, 1146756268034L, timer2, rawRealtimeUs4, 0);
                dumpTimer(proto, 1146756268035L, bgTimer2, rawRealtimeUs4, 0);
                proto.end(jToken);
                ij--;
                completions = completions;
                j3 = 1120986464258L;
            }
            dumpControllerActivityProto(proto, 1146756268036L, u4.getModemControllerActivity(), 0);
            long nToken = proto.start(1146756268049L);
            proto.write(1112396529665L, u4.getNetworkActivityBytes(0, 0));
            proto.write(1112396529666L, u4.getNetworkActivityBytes(1, 0));
            proto.write(1112396529667L, u4.getNetworkActivityBytes(2, 0));
            proto.write(1112396529668L, u4.getNetworkActivityBytes(3, 0));
            proto.write(1112396529669L, u4.getNetworkActivityBytes(4, 0));
            proto.write(1112396529670L, u4.getNetworkActivityBytes(5, 0));
            proto.write(1112396529671L, u4.getNetworkActivityPackets(0, 0));
            proto.write(1112396529672L, u4.getNetworkActivityPackets(1, 0));
            proto.write(1112396529673L, u4.getNetworkActivityPackets(2, 0));
            proto.write(1112396529674L, u4.getNetworkActivityPackets(3, 0));
            proto.write(1112396529675L, roundUsToMs(u4.getMobileRadioActiveTime(0)));
            proto.write(1120986464268L, u4.getMobileRadioActiveCount(0));
            proto.write(1120986464269L, u4.getMobileRadioApWakeupCount(0));
            proto.write(1120986464270L, u4.getWifiRadioApWakeupCount(0));
            proto.write(1112396529679L, u4.getNetworkActivityBytes(6, 0));
            proto.write(1112396529680L, u4.getNetworkActivityBytes(7, 0));
            proto.write(1112396529681L, u4.getNetworkActivityBytes(8, 0));
            proto.write(1112396529682L, u4.getNetworkActivityBytes(9, 0));
            proto.write(1112396529683L, u4.getNetworkActivityPackets(6, 0));
            proto.write(1112396529684L, u4.getNetworkActivityPackets(7, 0));
            proto.write(1112396529685L, u4.getNetworkActivityPackets(8, 0));
            proto.write(1112396529686L, u4.getNetworkActivityPackets(9, 0));
            proto.end(nToken);
            int uid4 = uid3;
            SparseArray<UidBatteryConsumer> uidToConsumer6 = uidToConsumer5;
            UidBatteryConsumer consumer2 = uidToConsumer6.get(uid4);
            if (consumer2 != null) {
                long bsToken = proto.start(1146756268050L);
                proto.write(1103806595073L, consumer2.getConsumedPower());
                proto.write(1133871366146L, proportionalAttributionCalculator.isSystemBatteryConsumer(consumer2));
                proto.write(1103806595075L, consumer2.getConsumedPower(0));
                proto.write(1103806595076L, proportionalAttributionCalculator.getProportionalPowerMah(consumer2));
                proto.end(bsToken);
            }
            ArrayMap<String, ? extends Uid.Proc> processStats = u4.getProcessStats();
            int ipr = processStats.size() - 1;
            while (ipr >= 0) {
                Uid.Proc ps = processStats.valueAt(ipr);
                long prToken = proto.start(2246267895827L);
                proto.write(1138166333441L, processStats.keyAt(ipr));
                proto.write(1112396529666L, ps.getUserTime(0));
                proto.write(1112396529667L, ps.getSystemTime(0));
                proto.write(1112396529668L, ps.getForegroundTime(0));
                proto.write(1120986464261L, ps.getStarts(0));
                proto.write(1120986464262L, ps.getNumAnrs(0));
                proto.write(1120986464263L, ps.getNumCrashes(0));
                proto.end(prToken);
                ipr--;
                processStats = processStats;
                uidToConsumer6 = uidToConsumer6;
                consumer2 = consumer2;
            }
            UidBatteryConsumer consumer3 = consumer2;
            SparseArray<UidBatteryConsumer> uidToConsumer7 = uidToConsumer6;
            SparseArray<? extends Uid.Sensor> sensors2 = u4.getSensorStats();
            int ise = 0;
            while (ise < sensors2.size()) {
                Uid.Sensor se = sensors2.valueAt(ise);
                Timer timer3 = se.getSensorTime();
                if (timer3 == null) {
                    consumer = consumer3;
                } else {
                    Timer bgTimer3 = se.getSensorBackgroundTime();
                    int sensorNumber = sensors2.keyAt(ise);
                    long seToken = proto.start(2246267895829L);
                    proto.write(1120986464257L, sensorNumber);
                    consumer = consumer3;
                    dumpTimer(proto, 1146756268034L, timer3, rawRealtimeUs4, 0);
                    dumpTimer(proto, 1146756268035L, bgTimer3, rawRealtimeUs4, 0);
                    proto.end(seToken);
                }
                ise++;
                consumer3 = consumer;
            }
            int ips = 0;
            while (ips < 7) {
                long rawRealtimeUs5 = rawRealtimeUs4;
                long durMs = roundUsToMs(u4.getProcessStateTime(ips, rawRealtimeUs5, 0));
                if (durMs == 0) {
                    sensors = sensors2;
                } else {
                    long stToken = proto.start(2246267895828L);
                    proto.write(1159641169921L, ips);
                    sensors = sensors2;
                    proto.write(1112396529666L, durMs);
                    proto.end(stToken);
                }
                ips++;
                rawRealtimeUs4 = rawRealtimeUs5;
                sensors2 = sensors;
            }
            long rawRealtimeUs6 = rawRealtimeUs4;
            long j4 = 1112396529666L;
            ArrayMap<String, ? extends Timer> syncs = u4.getSyncStats();
            int isy = syncs.size() - 1;
            while (isy >= 0) {
                Timer timer4 = syncs.valueAt(isy);
                Timer bgTimer4 = timer4.getSubTimer();
                long syToken = proto.start(2246267895830L);
                proto.write(1138166333441L, syncs.keyAt(isy));
                dumpTimer(proto, 1146756268034L, timer4, rawRealtimeUs6, 0);
                dumpTimer(proto, 1146756268035L, bgTimer4, rawRealtimeUs6, 0);
                proto.end(syToken);
                isy--;
                j4 = 1112396529666L;
                syncs = syncs;
                timesInFreqMs4 = timesInFreqMs4;
                uid4 = uid4;
            }
            if (u4.hasUserActivity()) {
                for (int i4 = 0; i4 < Uid.NUM_USER_ACTIVITY_TYPES; i4++) {
                    int val = u4.getUserActivityCount(i4, 0);
                    if (val != 0) {
                        long uaToken = proto.start(2246267895831L);
                        proto.write(1159641169921L, i4);
                        proto.write(1120986464258L, val);
                        proto.end(uaToken);
                    }
                }
            }
            dumpTimer(proto, 1146756268045L, u4.getVibratorOnTimer(), rawRealtimeUs6, 0);
            dumpTimer(proto, 1146756268046L, u4.getVideoTurnedOnTimer(), rawRealtimeUs6, 0);
            ArrayMap<String, ? extends Uid.Wakelock> wakelocks = u4.getWakelockStats();
            int iw = wakelocks.size() - 1;
            while (iw >= 0) {
                Uid.Wakelock wl = wakelocks.valueAt(iw);
                long wToken = proto.start(2246267895833L);
                proto.write(1138166333441L, wakelocks.keyAt(iw));
                int iw2 = iw;
                ArrayMap<String, ? extends Uid.Wakelock> wakelocks2 = wakelocks;
                dumpTimer(proto, 1146756268034L, wl.getWakeTime(1), rawRealtimeUs6, 0);
                Timer pTimer = wl.getWakeTime(0);
                if (pTimer != null) {
                    dumpTimer(proto, 1146756268035L, pTimer, rawRealtimeUs6, 0);
                    dumpTimer(proto, 1146756268036L, pTimer.getSubTimer(), rawRealtimeUs6, 0);
                }
                dumpTimer(proto, 1146756268037L, wl.getWakeTime(2), rawRealtimeUs6, 0);
                proto.end(wToken);
                iw = iw2 - 1;
                wakelocks = wakelocks2;
            }
            dumpTimer(proto, 1146756268060L, u4.getMulticastWakelockStats(), rawRealtimeUs6, 0);
            int i5 = 1;
            int ipkg = packageStats5.size() - 1;
            while (ipkg >= 0) {
                ArrayMap<String, ? extends Uid.Pkg> packageStats6 = packageStats5;
                ArrayMap<String, ? extends Counter> alarms = packageStats6.valueAt(ipkg).getWakeupAlarmStats();
                for (int iwa = alarms.size() - i5; iwa >= 0; iwa--) {
                    long waToken = proto.start(2246267895834L);
                    proto.write(1138166333441L, alarms.keyAt(iwa));
                    proto.write(1120986464258L, alarms.valueAt(iwa).getCountLocked(0));
                    proto.end(waToken);
                }
                ipkg--;
                packageStats5 = packageStats6;
                i5 = 1;
            }
            dumpControllerActivityProto(proto, 1146756268037L, u4.getWifiControllerActivity(), 0);
            long wToken2 = proto.start(1146756268059L);
            proto.write(1112396529665L, roundUsToMs(u4.getFullWifiLockTime(rawRealtimeUs6, 0)));
            dumpTimer(proto, 1146756268035L, u4.getWifiScanTimer(), rawRealtimeUs6, 0);
            proto.write(1112396529666L, roundUsToMs(u4.getWifiRunningTime(rawRealtimeUs6, 0)));
            dumpTimer(proto, 1146756268036L, u4.getWifiScanBackgroundTimer(), rawRealtimeUs6, 0);
            proto.end(wToken2);
            proto.end(uTkn3);
            iu2 = iu7 + 1;
            protoOutputStream2 = proto;
            uidStats2 = uidStats4;
            aidToPackages2 = aidToPackages;
            n = n2;
            which = which2;
            batteryUptimeUs2 = batteryUptimeUs4;
            uidToConsumer2 = uidToConsumer7;
            consumers2 = consumers3;
            rawRealtimeUs = rawRealtimeUs6;
            rawUptimeUs = rawUptimeUs2;
            rawRealtimeMs4 = rawRealtimeMs2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00ec A[Catch: all -> 0x01c3, TryCatch #1 {all -> 0x01c3, blocks: (B:32:0x00b9, B:36:0x00c5, B:44:0x00ec, B:46:0x00f0, B:50:0x00f8, B:51:0x0103, B:54:0x0116, B:70:0x0199, B:57:0x0123, B:58:0x012b, B:60:0x0131, B:61:0x0142, B:63:0x0148, B:67:0x016d, B:71:0x019f, B:73:0x01aa, B:77:0x01b2, B:38:0x00d4, B:42:0x00dd), top: B:100:0x00b9 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void dumpProtoHistoryLocked(ProtoOutputStream proto, int flags, long histStart) {
        Throwable th;
        boolean printed;
        boolean z;
        int i;
        HistoryTag oldEventTag;
        proto.write(1120986464257L, 36);
        proto.write(1112396529666L, getParcelVersion());
        proto.write(1138166333443L, getStartPlatformVersion());
        proto.write(1138166333444L, getEndPlatformVersion());
        for (int i2 = 0; i2 < getHistoryStringPoolSize(); i2++) {
            long token = proto.start(2246267895813L);
            proto.write(1120986464257L, i2);
            proto.write(1120986464258L, getHistoryTagPoolUid(i2));
            proto.write(1138166333443L, getHistoryTagPoolString(i2));
            proto.end(token);
        }
        HistoryPrinter hprinter = new HistoryPrinter();
        BatteryStatsHistoryIterator iterator = iterateBatteryStatsHistory();
        HistoryEventTracker tracker = null;
        long baseTime = -1;
        long lastTime = -1;
        boolean printed2 = false;
        while (true) {
            try {
                HistoryItem rec = iterator.next();
                if (rec == null) {
                    break;
                }
                long lastTime2 = rec.time;
                long baseTime2 = baseTime < 0 ? lastTime2 : baseTime;
                try {
                    if (rec.time >= histStart) {
                        if (histStart < 0 || printed2) {
                            printed = printed2;
                        } else {
                            if (rec.cmd != 5 && rec.cmd != 7 && rec.cmd != 4 && rec.cmd != 8) {
                                if (rec.currentTime != 0) {
                                    printed = true;
                                    try {
                                        byte cmd = rec.cmd;
                                        rec.cmd = (byte) 5;
                                        hprinter.printNextItem(proto, rec, baseTime2, (flags & 32) != 0);
                                        rec.cmd = cmd;
                                        z = false;
                                    } catch (Throwable th2) {
                                        th = th2;
                                        if (iterator != null) {
                                            try {
                                                iterator.close();
                                            } catch (Throwable th3) {
                                                th.addSuppressed(th3);
                                            }
                                        }
                                        throw th;
                                    }
                                } else {
                                    printed = printed2;
                                    z = false;
                                }
                                if (tracker != null) {
                                    if (rec.cmd != 0) {
                                        hprinter.printNextItem(proto, rec, baseTime2, (flags & 32) != 0 ? true : z ? 1 : 0);
                                        rec.cmd = z ? (byte) 1 : (byte) 0;
                                    }
                                    int oldEventCode = rec.eventCode;
                                    HistoryTag oldEventTag2 = rec.eventTag;
                                    rec.eventTag = new HistoryTag();
                                    int i3 = 0;
                                    while (i3 < 22) {
                                        HashMap<String, SparseIntArray> active = tracker.getStateForEvent(i3);
                                        if (active == null) {
                                            i = i3;
                                            oldEventTag = oldEventTag2;
                                        } else {
                                            for (Map.Entry<String, SparseIntArray> ent : active.entrySet()) {
                                                SparseIntArray uids = ent.getValue();
                                                int j = 0;
                                                while (j < uids.size()) {
                                                    rec.eventCode = i3;
                                                    rec.eventTag.string = ent.getKey();
                                                    rec.eventTag.uid = uids.keyAt(j);
                                                    rec.eventTag.poolIdx = uids.valueAt(j);
                                                    hprinter.printNextItem(proto, rec, baseTime2, (flags & 32) != 0 ? true : z);
                                                    rec.wakeReasonTag = null;
                                                    rec.wakelockTag = null;
                                                    j++;
                                                    oldEventTag2 = oldEventTag2;
                                                    i3 = i3;
                                                    uids = uids;
                                                    z = false;
                                                }
                                                z = false;
                                            }
                                            i = i3;
                                            oldEventTag = oldEventTag2;
                                        }
                                        i3 = i + 1;
                                        oldEventTag2 = oldEventTag;
                                        z = false;
                                    }
                                    rec.eventCode = oldEventCode;
                                    rec.eventTag = oldEventTag2;
                                    tracker = null;
                                }
                            }
                            printed = true;
                            z = false;
                            hprinter.printNextItem(proto, rec, baseTime2, (flags & 32) != 0);
                            rec.cmd = (byte) 0;
                            if (tracker != null) {
                            }
                        }
                        hprinter.printNextItem(proto, rec, baseTime2, (flags & 32) != 0);
                        lastTime = lastTime2;
                        printed2 = printed;
                        baseTime = baseTime2;
                    } else {
                        lastTime = lastTime2;
                        baseTime = baseTime2;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            } catch (Throwable th5) {
                th = th5;
            }
        }
        if (histStart >= 0) {
            commitCurrentHistoryBatchLocked();
            proto.write(2237677961222L, "NEXT: " + (1 + lastTime));
        }
        if (iterator != null) {
            iterator.close();
        }
    }

    private void dumpProtoSystemLocked(ProtoOutputStream proto, BatteryUsageStats stats) {
        long timeRemainingUs;
        int i;
        long pdcToken;
        long sToken = proto.start(1146756268038L);
        long rawUptimeUs = SystemClock.uptimeMillis() * 1000;
        long rawRealtimeMs = SystemClock.elapsedRealtime();
        long rawRealtimeUs = rawRealtimeMs * 1000;
        long bToken = proto.start(1146756268033L);
        proto.write(1112396529665L, getStartClockTime());
        proto.write(1112396529666L, getStartCount());
        proto.write(1112396529667L, computeRealtime(rawRealtimeUs, 0) / 1000);
        proto.write(1112396529668L, computeUptime(rawUptimeUs, 0) / 1000);
        proto.write(1112396529669L, computeBatteryRealtime(rawRealtimeUs, 0) / 1000);
        proto.write(1112396529670L, computeBatteryUptime(rawUptimeUs, 0) / 1000);
        proto.write(1112396529671L, computeBatteryScreenOffRealtime(rawRealtimeUs, 0) / 1000);
        proto.write(1112396529672L, computeBatteryScreenOffUptime(rawUptimeUs, 0) / 1000);
        proto.write(1112396529673L, getScreenDozeTime(rawRealtimeUs, 0) / 1000);
        proto.write(1112396529674L, getEstimatedBatteryCapacity());
        proto.write(1112396529675L, getMinLearnedBatteryCapacity());
        proto.write(1112396529676L, getMaxLearnedBatteryCapacity());
        proto.end(bToken);
        long bdToken = proto.start(1146756268034L);
        proto.write(1120986464257L, getLowDischargeAmountSinceCharge());
        proto.write(1120986464258L, getHighDischargeAmountSinceCharge());
        proto.write(1120986464259L, getDischargeAmountScreenOnSinceCharge());
        proto.write(1120986464260L, getDischargeAmountScreenOffSinceCharge());
        proto.write(1120986464261L, getDischargeAmountScreenDozeSinceCharge());
        proto.write(1112396529670L, getUahDischarge(0) / 1000);
        proto.write(1112396529671L, getUahDischargeScreenOff(0) / 1000);
        proto.write(1112396529672L, getUahDischargeScreenDoze(0) / 1000);
        proto.write(1112396529673L, getUahDischargeLightDoze(0) / 1000);
        proto.write(1112396529674L, getUahDischargeDeepDoze(0) / 1000);
        proto.end(bdToken);
        long timeRemainingUs2 = computeChargeTimeRemaining(rawRealtimeUs);
        if (timeRemainingUs2 >= 0) {
            proto.write(1112396529667L, timeRemainingUs2 / 1000);
            timeRemainingUs = timeRemainingUs2;
        } else {
            long timeRemainingUs3 = computeBatteryTimeRemaining(rawRealtimeUs);
            if (timeRemainingUs3 >= 0) {
                proto.write(1112396529668L, timeRemainingUs3 / 1000);
            } else {
                proto.write(1112396529668L, -1);
            }
            timeRemainingUs = timeRemainingUs3;
        }
        dumpDurationSteps(proto, 2246267895813L, getChargeLevelStepTracker());
        int i2 = 0;
        while (true) {
            if (i2 >= NUM_DATA_CONNECTION_TYPES) {
                break;
            }
            boolean isNone = i2 == 0;
            int telephonyNetworkType = i2;
            int telephonyNetworkType2 = (i2 == DATA_CONNECTION_OTHER || i2 == DATA_CONNECTION_EMERGENCY_SERVICE) ? 0 : telephonyNetworkType;
            long rawRealtimeUs2 = rawRealtimeUs;
            long pdcToken2 = proto.start(2246267895816L);
            if (isNone) {
                pdcToken = pdcToken2;
                proto.write(1133871366146L, isNone);
            } else {
                pdcToken = pdcToken2;
                proto.write(1159641169921L, telephonyNetworkType2);
            }
            rawRealtimeUs = rawRealtimeUs2;
            dumpTimer(proto, 1146756268035L, getPhoneDataConnectionTimer(i2), rawRealtimeUs, 0);
            proto.end(pdcToken);
            i2++;
            timeRemainingUs = timeRemainingUs;
        }
        long rawRealtimeUs3 = rawRealtimeUs;
        dumpDurationSteps(proto, 2246267895814L, getDischargeLevelStepTracker());
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            for (long i3 : cpuFreqs) {
                proto.write(SystemProto.CPU_FREQUENCY, i3);
            }
        }
        dumpControllerActivityProto(proto, 1146756268041L, getBluetoothControllerActivity(), 0);
        dumpControllerActivityProto(proto, 1146756268042L, getModemControllerActivity(), 0);
        long gnToken = proto.start(1146756268044L);
        proto.write(1112396529665L, getNetworkActivityBytes(0, 0));
        proto.write(1112396529666L, getNetworkActivityBytes(1, 0));
        proto.write(1112396529669L, getNetworkActivityPackets(0, 0));
        proto.write(1112396529670L, getNetworkActivityPackets(1, 0));
        proto.write(1112396529667L, getNetworkActivityBytes(2, 0));
        proto.write(1112396529668L, getNetworkActivityBytes(3, 0));
        proto.write(1112396529671L, getNetworkActivityPackets(2, 0));
        proto.write(1112396529672L, getNetworkActivityPackets(3, 0));
        proto.write(1112396529673L, getNetworkActivityBytes(4, 0));
        proto.write(1112396529674L, getNetworkActivityBytes(5, 0));
        proto.end(gnToken);
        dumpControllerActivityProto(proto, 1146756268043L, getWifiControllerActivity(), 0);
        long gwToken = proto.start(1146756268045L);
        proto.write(1112396529665L, getWifiOnTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1112396529666L, getGlobalWifiRunningTime(rawRealtimeUs3, 0) / 1000);
        proto.end(gwToken);
        Map<String, ? extends Timer> kernelWakelocks = getKernelWakelockStats();
        for (Map.Entry<String, ? extends Timer> ent : kernelWakelocks.entrySet()) {
            long kwToken = proto.start(2246267895822L);
            proto.write(1138166333441L, ent.getKey());
            dumpTimer(proto, 1146756268034L, ent.getValue(), rawRealtimeUs3, 0);
            proto.end(kwToken);
            kernelWakelocks = kernelWakelocks;
            gwToken = gwToken;
        }
        int i4 = 1;
        SparseArray<? extends Uid> uidStats = getUidStats();
        int iu = 0;
        long fullWakeLockTimeTotalUs = 0;
        long partialWakeLockTimeTotalUs = 0;
        while (iu < uidStats.size()) {
            Uid u = uidStats.valueAt(iu);
            ArrayMap<String, ? extends Uid.Wakelock> wakelocks = u.getWakelockStats();
            int iw = wakelocks.size() - i4;
            while (iw >= 0) {
                Uid.Wakelock wl = wakelocks.valueAt(iw);
                Timer fullWakeTimer = wl.getWakeTime(i4);
                if (fullWakeTimer == null) {
                    i = 0;
                } else {
                    i = 0;
                    fullWakeLockTimeTotalUs += fullWakeTimer.getTotalTimeLocked(rawRealtimeUs3, 0);
                }
                Uid u2 = u;
                Timer partialWakeTimer = wl.getWakeTime(i);
                if (partialWakeTimer != null) {
                    partialWakeLockTimeTotalUs += partialWakeTimer.getTotalTimeLocked(rawRealtimeUs3, i);
                }
                iw--;
                u = u2;
                i4 = 1;
            }
            iu++;
            i4 = 1;
        }
        long mToken = proto.start(1146756268047L);
        proto.write(1112396529665L, getScreenOnTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1112396529666L, getPhoneOnTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1112396529667L, fullWakeLockTimeTotalUs / 1000);
        proto.write(1112396529668L, partialWakeLockTimeTotalUs / 1000);
        proto.write(1112396529669L, getMobileRadioActiveTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1112396529670L, getMobileRadioActiveAdjustedTime(0) / 1000);
        proto.write(1120986464263L, getMobileRadioActiveCount(0));
        proto.write(1120986464264L, getMobileRadioActiveUnknownTime(0) / 1000);
        proto.write(1112396529673L, getInteractiveTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1112396529674L, getPowerSaveModeEnabledTime(rawRealtimeUs3, 0) / 1000);
        proto.write(1120986464267L, getNumConnectivityChange(0));
        proto.write(1112396529676L, getDeviceIdleModeTime(2, rawRealtimeUs3, 0) / 1000);
        proto.write(1120986464269L, getDeviceIdleModeCount(2, 0));
        proto.write(1112396529678L, getDeviceIdlingTime(2, rawRealtimeUs3, 0) / 1000);
        proto.write(1120986464271L, getDeviceIdlingCount(2, 0));
        proto.write(1112396529680L, getLongestDeviceIdleModeTime(2));
        proto.write(1112396529681L, getDeviceIdleModeTime(1, rawRealtimeUs3, 0) / 1000);
        proto.write(1120986464274L, getDeviceIdleModeCount(1, 0));
        proto.write(1112396529683L, getDeviceIdlingTime(1, rawRealtimeUs3, 0) / 1000);
        proto.write(1120986464276L, getDeviceIdlingCount(1, 0));
        proto.write(1112396529685L, getLongestDeviceIdleModeTime(1));
        proto.end(mToken);
        long multicastWakeLockTimeTotalUs = getWifiMulticastWakelockTime(rawRealtimeUs3, 0);
        int multicastWakeLockCountTotal = getWifiMulticastWakelockCount(0);
        long wmctToken = proto.start(1146756268055L);
        long mToken2 = multicastWakeLockTimeTotalUs / 1000;
        proto.write(1112396529665L, mToken2);
        proto.write(1120986464258L, multicastWakeLockCountTotal);
        proto.end(wmctToken);
        BatteryConsumer deviceConsumer = stats.getAggregateBatteryConsumer(0);
        int powerComponent = 0;
        while (powerComponent < 18) {
            int n = 0;
            switch (powerComponent) {
                case 0:
                    n = 7;
                    break;
                case 2:
                    n = 5;
                    break;
                case 3:
                    n = 11;
                    break;
                case 6:
                    n = 6;
                    break;
                case 8:
                    n = 2;
                    break;
                case 11:
                    n = 4;
                    break;
                case 13:
                    n = 12;
                    break;
                case 14:
                    n = 3;
                    break;
                case 15:
                    n = 13;
                    break;
                case 16:
                    n = 1;
                    break;
            }
            long wmctToken2 = wmctToken;
            long puiToken = proto.start(2246267895825L);
            proto.write(1159641169921L, n);
            proto.write(1120986464258L, 0);
            proto.write(1103806595075L, deviceConsumer.getConsumedPower(powerComponent));
            proto.write(1133871366148L, shouldHidePowerComponent(powerComponent));
            proto.write(1103806595077L, 0);
            proto.write(1103806595078L, 0);
            proto.end(puiToken);
            powerComponent++;
            multicastWakeLockCountTotal = multicastWakeLockCountTotal;
            wmctToken = wmctToken2;
            rawUptimeUs = rawUptimeUs;
        }
        int multicastWakeLockCountTotal2 = multicastWakeLockCountTotal;
        long pusToken = proto.start(1146756268050L);
        proto.write(1103806595073L, stats.getBatteryCapacity());
        proto.write(1103806595074L, stats.getConsumedPower());
        proto.write(1103806595075L, stats.getDischargedPowerRange().getLower().doubleValue());
        proto.write(1103806595076L, stats.getDischargedPowerRange().getUpper().doubleValue());
        proto.end(pusToken);
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        Map<String, ? extends Timer> screenOffRpmStats = getScreenOffRpmStats();
        for (Map.Entry<String, ? extends Timer> ent2 : rpmStats.entrySet()) {
            long rpmToken = proto.start(2246267895827L);
            proto.write(1138166333441L, ent2.getKey());
            Map<String, ? extends Timer> screenOffRpmStats2 = screenOffRpmStats;
            dumpTimer(proto, 1146756268034L, ent2.getValue(), rawRealtimeUs3, 0);
            dumpTimer(proto, 1146756268035L, screenOffRpmStats2.get(ent2.getKey()), rawRealtimeUs3, 0);
            proto.end(rpmToken);
            uidStats = uidStats;
            screenOffRpmStats = screenOffRpmStats2;
            multicastWakeLockCountTotal2 = multicastWakeLockCountTotal2;
        }
        for (int i5 = 0; i5 < 5; i5++) {
            long sbToken = proto.start(2246267895828L);
            proto.write(1159641169921L, i5);
            dumpTimer(proto, 1146756268034L, getScreenBrightnessTimer(i5), rawRealtimeUs3, 0);
            proto.end(sbToken);
        }
        dumpTimer(proto, 1146756268053L, getPhoneSignalScanningTimer(), rawRealtimeUs3, 0);
        for (int i6 = 0; i6 < CellSignalStrength.getNumSignalStrengthLevels(); i6++) {
            long pssToken = proto.start(2246267895824L);
            proto.write(1159641169921L, i6);
            dumpTimer(proto, 1146756268034L, getPhoneSignalStrengthTimer(i6), rawRealtimeUs3, 0);
            proto.end(pssToken);
        }
        Map<String, ? extends Timer> wakeupReasons = getWakeupReasonStats();
        for (Map.Entry<String, ? extends Timer> ent3 : wakeupReasons.entrySet()) {
            long wrToken = proto.start(2246267895830L);
            proto.write(1138166333441L, ent3.getKey());
            dumpTimer(proto, 1146756268034L, ent3.getValue(), rawRealtimeUs3, 0);
            proto.end(wrToken);
        }
        for (int i7 = 0; i7 < 5; i7++) {
            long wssToken = proto.start(2246267895832L);
            proto.write(1159641169921L, i7);
            dumpTimer(proto, 1146756268034L, getWifiSignalStrengthTimer(i7), rawRealtimeUs3, 0);
            proto.end(wssToken);
        }
        for (int i8 = 0; i8 < 8; i8++) {
            long wsToken = proto.start(2246267895833L);
            proto.write(1159641169921L, i8);
            dumpTimer(proto, 1146756268034L, getWifiStateTimer(i8), rawRealtimeUs3, 0);
            proto.end(wsToken);
        }
        for (int i9 = 0; i9 < 13; i9++) {
            long wssToken2 = proto.start(2246267895834L);
            proto.write(1159641169921L, i9);
            dumpTimer(proto, 1146756268034L, getWifiSupplStateTimer(i9), rawRealtimeUs3, 0);
            proto.end(wssToken2);
        }
        proto.end(sToken);
    }

    public static boolean checkWifiOnly(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (tm == null) {
            return false;
        }
        return !tm.isDataCapable();
    }

    private boolean shouldHidePowerComponent(int powerComponent) {
        return powerComponent == 16 || powerComponent == 8 || powerComponent == 0 || powerComponent == 15;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.BatteryStats$ProportionalAttributionCalculator */
    /* loaded from: classes3.dex */
    public static class ProportionalAttributionCalculator {
        private static final double SYSTEM_BATTERY_CONSUMER = -1.0d;
        private final PackageManager mPackageManager;
        private final SparseDoubleArray mProportionalPowerMah;
        private final HashSet<String> mSystemAndServicePackages;

        ProportionalAttributionCalculator(Context context, BatteryUsageStats stats) {
            double d;
            Resources resources;
            ProportionalAttributionCalculator proportionalAttributionCalculator = this;
            proportionalAttributionCalculator.mPackageManager = context.getPackageManager();
            Resources resources2 = context.getResources();
            String[] systemPackageArray = resources2.getStringArray(C4057R.array.config_batteryPackageTypeSystem);
            String[] servicePackageArray = resources2.getStringArray(C4057R.array.config_batteryPackageTypeService);
            proportionalAttributionCalculator.mSystemAndServicePackages = new HashSet<>(systemPackageArray.length + servicePackageArray.length);
            for (String packageName : systemPackageArray) {
                proportionalAttributionCalculator.mSystemAndServicePackages.add(packageName);
            }
            for (String packageName2 : servicePackageArray) {
                proportionalAttributionCalculator.mSystemAndServicePackages.add(packageName2);
            }
            List<UidBatteryConsumer> uidBatteryConsumers = stats.getUidBatteryConsumers();
            proportionalAttributionCalculator.mProportionalPowerMah = new SparseDoubleArray(uidBatteryConsumers.size());
            double systemPowerMah = 0.0d;
            int i = uidBatteryConsumers.size();
            while (true) {
                i--;
                d = -1.0d;
                if (i < 0) {
                    break;
                }
                UidBatteryConsumer consumer = uidBatteryConsumers.get(i);
                int uid = consumer.getUid();
                if (proportionalAttributionCalculator.isSystemUid(uid)) {
                    proportionalAttributionCalculator.mProportionalPowerMah.put(uid, -1.0d);
                    systemPowerMah += consumer.getConsumedPower();
                }
            }
            double totalRemainingPower = stats.getConsumedPower() - systemPowerMah;
            if (Math.abs(totalRemainingPower) > 0.001d) {
                int i2 = uidBatteryConsumers.size() - 1;
                while (i2 >= 0) {
                    UidBatteryConsumer consumer2 = uidBatteryConsumers.get(i2);
                    int uid2 = consumer2.getUid();
                    if (proportionalAttributionCalculator.mProportionalPowerMah.get(uid2) == d) {
                        resources = resources2;
                    } else {
                        double power = consumer2.getConsumedPower();
                        resources = resources2;
                        proportionalAttributionCalculator.mProportionalPowerMah.put(uid2, power + ((systemPowerMah * power) / totalRemainingPower));
                    }
                    i2--;
                    proportionalAttributionCalculator = this;
                    resources2 = resources;
                    d = -1.0d;
                }
            }
        }

        boolean isSystemBatteryConsumer(UidBatteryConsumer consumer) {
            return this.mProportionalPowerMah.get(consumer.getUid()) < 0.0d;
        }

        double getProportionalPowerMah(UidBatteryConsumer consumer) {
            double powerMah = this.mProportionalPowerMah.get(consumer.getUid());
            if (powerMah >= 0.0d) {
                return powerMah;
            }
            return 0.0d;
        }

        private boolean isSystemUid(int uid) {
            if (uid >= 0 && uid < 10000) {
                return true;
            }
            String[] packages = this.mPackageManager.getPackagesForUid(uid);
            if (packages == null) {
                return false;
            }
            for (String packageName : packages) {
                if (this.mSystemAndServicePackages.contains(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.BatteryStats$UidMobileRadioStats */
    /* loaded from: classes3.dex */
    public static class UidMobileRadioStats {
        public final double millisecondsPerPacket;
        public final int radioActiveCount;
        public final long radioActiveMs;
        public final long rxPackets;
        public final long txPackets;
        public final int uid;

        private UidMobileRadioStats(int uid, long rxPackets, long txPackets, long radioActiveMs, int radioActiveCount, double millisecondsPerPacket) {
            this.uid = uid;
            this.txPackets = txPackets;
            this.rxPackets = rxPackets;
            this.radioActiveMs = radioActiveMs;
            this.radioActiveCount = radioActiveCount;
            this.millisecondsPerPacket = millisecondsPerPacket;
        }
    }

    private List<UidMobileRadioStats> getUidMobileRadioStats(List<UidBatteryConsumer> uidBatteryConsumers) {
        SparseArray<? extends Uid> uidStats = getUidStats();
        List<UidMobileRadioStats> uidMobileRadioStats = Lists.newArrayList();
        for (int i = 0; i < uidBatteryConsumers.size(); i++) {
            UidBatteryConsumer consumer = uidBatteryConsumers.get(i);
            if (consumer.getConsumedPower(8) != 0.0d) {
                int uid = consumer.getUid();
                Uid u = uidStats.get(uid);
                long rxPackets = u.getNetworkActivityPackets(0, 0);
                long txPackets = u.getNetworkActivityPackets(1, 0);
                if (rxPackets != 0 || txPackets != 0) {
                    long radioActiveMs = u.getMobileRadioActiveTime(0) / 1000;
                    int radioActiveCount = u.getMobileRadioActiveCount(0);
                    double msPerPacket = radioActiveMs / (rxPackets + txPackets);
                    if (msPerPacket != 0.0d) {
                        uidMobileRadioStats.add(new UidMobileRadioStats(uid, rxPackets, txPackets, radioActiveMs, radioActiveCount, msPerPacket));
                    }
                }
            }
        }
        uidMobileRadioStats.sort(new Comparator() { // from class: android.os.BatteryStats$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compare;
                compare = Double.compare(((BatteryStats.UidMobileRadioStats) obj2).millisecondsPerPacket, ((BatteryStats.UidMobileRadioStats) obj).millisecondsPerPacket);
                return compare;
            }
        });
        return uidMobileRadioStats;
    }
}
