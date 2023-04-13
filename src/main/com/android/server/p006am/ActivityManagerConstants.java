package com.android.server.p006am;

import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* renamed from: com.android.server.am.ActivityManagerConstants */
/* loaded from: classes.dex */
public final class ActivityManagerConstants extends ContentObserver {
    public static final Uri ACTIVITY_MANAGER_CONSTANTS_URI;
    public static final Uri ACTIVITY_STARTS_LOGGING_ENABLED_URI;
    public static int BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE;
    public static boolean BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED;
    public static float BINDER_HEAVY_HITTER_AUTO_SAMPLER_THRESHOLD;
    public static int BINDER_HEAVY_HITTER_WATCHER_BATCHSIZE;
    public static boolean BINDER_HEAVY_HITTER_WATCHER_ENABLED;
    public static float BINDER_HEAVY_HITTER_WATCHER_THRESHOLD;
    public static final long[] DEFAULT_EXTRA_SERVICE_RESTART_DELAY_ON_MEM_PRESSURE = {0, 10000, 20000, 30000};
    public static final long DEFAULT_SERVICE_BACKGROUND_TIMEOUT;
    public static final long DEFAULT_SERVICE_TIMEOUT;
    public static final Uri ENABLE_AUTOMATIC_SYSTEM_SERVER_HEAP_DUMPS_URI;
    public static final Uri FOREGROUND_SERVICE_STARTS_LOGGING_ENABLED_URI;
    public static float LOW_SWAP_THRESHOLD_PERCENT;
    public static long MAX_PREVIOUS_TIME;
    public static long MIN_ASSOC_LOG_DURATION;
    public static int MIN_CRASH_INTERVAL;
    public static boolean PROACTIVE_KILLS_ENABLED;
    public static int PROCESS_CRASH_COUNT_LIMIT;
    public static long PROCESS_CRASH_COUNT_RESET_INTERVAL;
    public long BACKGROUND_SETTLE_TIME;
    public long BG_START_TIMEOUT;
    public long BOUND_SERVICE_CRASH_RESTART_DURATION;
    public long BOUND_SERVICE_MAX_CRASH_RETRY;
    public long CONTENT_PROVIDER_RETAIN_TIME;
    public int CUR_MAX_CACHED_PROCESSES;
    public int CUR_MAX_EMPTY_PROCESSES;
    public int CUR_TRIM_CACHED_PROCESSES;
    public int CUR_TRIM_EMPTY_PROCESSES;
    public long FGSERVICE_MIN_REPORT_TIME;
    public long FGSERVICE_MIN_SHOWN_TIME;
    public long FGSERVICE_SCREEN_ON_AFTER_TIME;
    public long FGSERVICE_SCREEN_ON_BEFORE_TIME;
    public boolean FLAG_PROCESS_START_ASYNC;
    public boolean FORCE_BACKGROUND_CHECK_ON_RESTRICTED_APPS;
    public long FULL_PSS_LOWERED_INTERVAL;
    public long FULL_PSS_MIN_INTERVAL;
    public long GC_MIN_INTERVAL;
    public long GC_TIMEOUT;
    public ArraySet<String> IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES;
    public ArraySet<Integer> IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES;
    public final ArraySet<ComponentName> KEEP_WARMING_SERVICES;
    public int MAX_CACHED_PROCESSES;
    public int MAX_PHANTOM_PROCESSES;
    public long MAX_SERVICE_INACTIVITY;
    public long MEMORY_INFO_THROTTLE_TIME;
    public boolean OOMADJ_UPDATE_QUICK;
    public int PENDINGINTENT_WARNING_THRESHOLD;
    public long POWER_CHECK_INTERVAL;
    public int POWER_CHECK_MAX_CPU_1;
    public int POWER_CHECK_MAX_CPU_2;
    public int POWER_CHECK_MAX_CPU_3;
    public int POWER_CHECK_MAX_CPU_4;
    public long SERVICE_BACKGROUND_TIMEOUT;
    public long SERVICE_BG_ACTIVITY_START_TIMEOUT;
    public long SERVICE_MIN_RESTART_TIME_BETWEEN;
    public long SERVICE_RESET_RUN_DURATION;
    public long SERVICE_RESTART_DURATION;
    public int SERVICE_RESTART_DURATION_FACTOR;
    public long SERVICE_TIMEOUT;
    public long SERVICE_USAGE_INTERACTION_TIME_POST_S;
    public long SERVICE_USAGE_INTERACTION_TIME_PRE_S;
    public long TIERED_CACHED_ADJ_DECAY_TIME;
    public long TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION;
    public volatile long TOP_TO_FGS_GRACE_DURATION;
    public long USAGE_STATS_INTERACTION_INTERVAL_POST_S;
    public long USAGE_STATS_INTERACTION_INTERVAL_PRE_S;
    public boolean USE_TIERED_CACHED_ADJ;
    public volatile long mBootTimeTempAllowlistDuration;
    public volatile String mComponentAliasOverrides;
    public final int mCustomizedMaxCachedProcesses;
    public final int mDefaultBinderHeavyHitterAutoSamplerBatchSize;
    public final boolean mDefaultBinderHeavyHitterAutoSamplerEnabled;
    public final float mDefaultBinderHeavyHitterAutoSamplerThreshold;
    public final int mDefaultBinderHeavyHitterWatcherBatchSize;
    public final boolean mDefaultBinderHeavyHitterWatcherEnabled;
    public final float mDefaultBinderHeavyHitterWatcherThreshold;
    public List<String> mDefaultImperceptibleKillExemptPackages;
    public List<Integer> mDefaultImperceptibleKillExemptProcStates;
    @GuardedBy({"mService"})
    public volatile int mDeferBootCompletedBroadcast;
    public volatile boolean mEnableComponentAlias;
    @GuardedBy({"mService"})
    public boolean mEnableExtraServiceRestartDelayOnMemPressure;
    public volatile boolean mEnableWaitForFinishAttachApplication;
    @GuardedBy({"mService"})
    public long[] mExtraServiceRestartDelayOnMemPressure;
    public volatile long mFgToBgFgsGraceDuration;
    public volatile boolean mFgsAllowOptOut;
    public volatile float mFgsAtomSampleRate;
    public volatile long mFgsNotificationDeferralExclusionTime;
    public volatile long mFgsNotificationDeferralExclusionTimeForShort;
    public volatile long mFgsNotificationDeferralInterval;
    public volatile long mFgsNotificationDeferralIntervalForShort;
    public volatile float mFgsStartAllowedLogSampleRate;
    public volatile float mFgsStartDeniedLogSampleRate;
    public volatile long mFgsStartForegroundTimeoutMs;
    public volatile boolean mFgsStartRestrictionCheckCallerTargetSdk;
    public volatile boolean mFgsStartRestrictionNotificationEnabled;
    public volatile boolean mFlagActivityStartsLoggingEnabled;
    public volatile boolean mFlagApplicationStartInfoEnabled;
    public volatile boolean mFlagBackgroundActivityStartsEnabled;
    public volatile boolean mFlagBackgroundFgsStartRestrictionEnabled;
    public volatile boolean mFlagFgsNotificationDeferralApiGated;
    public volatile boolean mFlagFgsNotificationDeferralEnabled;
    public volatile boolean mFlagFgsStartRestrictionEnabled;
    public volatile boolean mFlagForegroundServiceStartsLoggingEnabled;
    public volatile boolean mFlagSystemExemptPowerRestrictionsEnabled;
    public volatile boolean mKillBgRestrictedAndCachedIdle;
    public volatile long mKillBgRestrictedAndCachedIdleSettleTimeMs;
    public volatile long mMaxEmptyTimeMillis;
    public volatile int mMaxServiceConnectionsPerProcess;
    public volatile long mNetworkAccessTimeoutMs;
    public volatile long mNoKillCachedProcessesPostBootCompletedDurationMillis;
    public volatile boolean mNoKillCachedProcessesUntilBootCompleted;
    public final DeviceConfig.OnPropertiesChangedListener mOnDeviceConfigChangedForComponentAliasListener;
    public final DeviceConfig.OnPropertiesChangedListener mOnDeviceConfigChangedListener;
    public int mOverrideMaxCachedProcesses;
    public final KeyValueListParser mParser;
    public volatile boolean mPrioritizeAlarmBroadcasts;
    public volatile long mProcessKillTimeoutMs;
    public volatile int mPushMessagingOverQuotaBehavior;
    public ContentResolver mResolver;
    public final ActivityManagerService mService;
    public volatile long mServiceBindAlmostPerceptibleTimeoutMs;
    public volatile int mServiceStartForegroundAnrDelayMs;
    public volatile int mServiceStartForegroundTimeoutMs;
    public volatile long mShortFgsAnrExtraWaitDuration;
    public volatile long mShortFgsProcStateExtraWaitDuration;
    public volatile long mShortFgsTimeoutDuration;
    public final boolean mSystemServerAutomaticHeapDumpEnabled;
    public final String mSystemServerAutomaticHeapDumpPackageName;
    public long mSystemServerAutomaticHeapDumpPssThresholdBytes;

    static {
        long j = Build.HW_TIMEOUT_MULTIPLIER * 20000;
        DEFAULT_SERVICE_TIMEOUT = j;
        DEFAULT_SERVICE_BACKGROUND_TIMEOUT = j * 10;
        MAX_PREVIOUS_TIME = 60000L;
        MIN_CRASH_INTERVAL = 120000;
        PROCESS_CRASH_COUNT_RESET_INTERVAL = 43200000L;
        PROCESS_CRASH_COUNT_LIMIT = 12;
        ACTIVITY_MANAGER_CONSTANTS_URI = Settings.Global.getUriFor("activity_manager_constants");
        ACTIVITY_STARTS_LOGGING_ENABLED_URI = Settings.Global.getUriFor("activity_starts_logging_enabled");
        FOREGROUND_SERVICE_STARTS_LOGGING_ENABLED_URI = Settings.Global.getUriFor("foreground_service_starts_logging_enabled");
        ENABLE_AUTOMATIC_SYSTEM_SERVER_HEAP_DUMPS_URI = Settings.Global.getUriFor("enable_automatic_system_server_heap_dumps");
        MIN_ASSOC_LOG_DURATION = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        PROACTIVE_KILLS_ENABLED = false;
        LOW_SWAP_THRESHOLD_PERCENT = 0.1f;
    }

    public ActivityManagerConstants(Context context, ActivityManagerService activityManagerService, Handler handler) {
        super(handler);
        this.MAX_CACHED_PROCESSES = 32;
        this.BACKGROUND_SETTLE_TIME = 60000L;
        this.FGSERVICE_MIN_SHOWN_TIME = 2000L;
        this.FGSERVICE_MIN_REPORT_TIME = BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS;
        this.FGSERVICE_SCREEN_ON_BEFORE_TIME = 1000L;
        this.FGSERVICE_SCREEN_ON_AFTER_TIME = 5000L;
        this.CONTENT_PROVIDER_RETAIN_TIME = 20000L;
        this.GC_TIMEOUT = 5000L;
        this.GC_MIN_INTERVAL = 60000L;
        boolean z = true;
        this.FORCE_BACKGROUND_CHECK_ON_RESTRICTED_APPS = true;
        this.FULL_PSS_MIN_INTERVAL = 1200000L;
        this.FULL_PSS_LOWERED_INTERVAL = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        this.POWER_CHECK_INTERVAL = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        this.POWER_CHECK_MAX_CPU_1 = 25;
        this.POWER_CHECK_MAX_CPU_2 = 25;
        this.POWER_CHECK_MAX_CPU_3 = 10;
        this.POWER_CHECK_MAX_CPU_4 = 2;
        this.SERVICE_USAGE_INTERACTION_TIME_PRE_S = 1800000L;
        this.SERVICE_USAGE_INTERACTION_TIME_POST_S = 60000L;
        this.USAGE_STATS_INTERACTION_INTERVAL_PRE_S = 7200000L;
        this.USAGE_STATS_INTERACTION_INTERVAL_POST_S = 600000L;
        this.SERVICE_RESTART_DURATION = 1000L;
        this.SERVICE_RESET_RUN_DURATION = 60000L;
        this.SERVICE_RESTART_DURATION_FACTOR = 4;
        this.SERVICE_MIN_RESTART_TIME_BETWEEN = 10000L;
        this.SERVICE_TIMEOUT = DEFAULT_SERVICE_TIMEOUT;
        this.SERVICE_BACKGROUND_TIMEOUT = DEFAULT_SERVICE_BACKGROUND_TIMEOUT;
        this.MAX_SERVICE_INACTIVITY = 1800000L;
        this.BG_START_TIMEOUT = 15000L;
        this.SERVICE_BG_ACTIVITY_START_TIMEOUT = 10000L;
        this.BOUND_SERVICE_CRASH_RESTART_DURATION = 1800000L;
        this.BOUND_SERVICE_MAX_CRASH_RETRY = 16L;
        this.FLAG_PROCESS_START_ASYNC = true;
        this.MEMORY_INFO_THROTTLE_TIME = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
        this.TOP_TO_FGS_GRACE_DURATION = 15000L;
        this.TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION = 15000L;
        this.mFlagBackgroundFgsStartRestrictionEnabled = true;
        this.mFlagFgsStartRestrictionEnabled = true;
        this.mFgsStartRestrictionNotificationEnabled = false;
        this.mFgsStartRestrictionCheckCallerTargetSdk = true;
        this.mFlagFgsNotificationDeferralEnabled = true;
        this.mFlagFgsNotificationDeferralApiGated = false;
        this.mFgsNotificationDeferralInterval = 10000L;
        this.mFgsNotificationDeferralIntervalForShort = this.mFgsNotificationDeferralInterval;
        this.mFgsNotificationDeferralExclusionTime = 120000L;
        this.mFgsNotificationDeferralExclusionTimeForShort = this.mFgsNotificationDeferralExclusionTime;
        this.mFlagSystemExemptPowerRestrictionsEnabled = true;
        this.mPushMessagingOverQuotaBehavior = 1;
        this.mBootTimeTempAllowlistDuration = 20000L;
        this.mFgToBgFgsGraceDuration = 5000L;
        this.mFgsStartForegroundTimeoutMs = 10000L;
        this.mFgsAtomSampleRate = 1.0f;
        this.mFgsStartAllowedLogSampleRate = 0.25f;
        this.mFgsStartDeniedLogSampleRate = 1.0f;
        this.mKillBgRestrictedAndCachedIdle = true;
        this.mKillBgRestrictedAndCachedIdleSettleTimeMs = 60000L;
        this.mProcessKillTimeoutMs = 10000L;
        this.mFgsAllowOptOut = false;
        this.mExtraServiceRestartDelayOnMemPressure = DEFAULT_EXTRA_SERVICE_RESTART_DELAY_ON_MEM_PRESSURE;
        this.mEnableExtraServiceRestartDelayOnMemPressure = true;
        this.mEnableComponentAlias = false;
        this.mDeferBootCompletedBroadcast = 6;
        this.mPrioritizeAlarmBroadcasts = true;
        this.mServiceStartForegroundTimeoutMs = 30000;
        this.mServiceStartForegroundAnrDelayMs = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        this.mServiceBindAlmostPerceptibleTimeoutMs = 15000L;
        this.mComponentAliasOverrides = "";
        this.mMaxServiceConnectionsPerProcess = 3000;
        this.mParser = new KeyValueListParser(',');
        this.mOverrideMaxCachedProcesses = -1;
        this.mNoKillCachedProcessesUntilBootCompleted = true;
        this.mNoKillCachedProcessesPostBootCompletedDurationMillis = 600000L;
        this.CUR_TRIM_EMPTY_PROCESSES = computeEmptyProcessLimit(this.MAX_CACHED_PROCESSES) / 2;
        int i = this.MAX_CACHED_PROCESSES;
        this.CUR_TRIM_CACHED_PROCESSES = (i - computeEmptyProcessLimit(i)) / 3;
        this.mMaxEmptyTimeMillis = 1800000L;
        this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES = new ArraySet<>();
        this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES = new ArraySet<>();
        this.PENDINGINTENT_WARNING_THRESHOLD = 2000;
        ArraySet<ComponentName> arraySet = new ArraySet<>();
        this.KEEP_WARMING_SERVICES = arraySet;
        this.MAX_PHANTOM_PROCESSES = 32;
        this.mNetworkAccessTimeoutMs = 200L;
        this.OOMADJ_UPDATE_QUICK = true;
        this.mShortFgsTimeoutDuration = 180000L;
        this.mShortFgsProcStateExtraWaitDuration = 5000L;
        this.mEnableWaitForFinishAttachApplication = false;
        this.mShortFgsAnrExtraWaitDuration = 10000L;
        this.USE_TIERED_CACHED_ADJ = false;
        this.TIERED_CACHED_ADJ_DECAY_TIME = 60000L;
        this.mOnDeviceConfigChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.ActivityManagerConstants.1
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                String str;
                Iterator it = properties.getKeyset().iterator();
                while (it.hasNext() && (str = (String) it.next()) != null) {
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -2074391906:
                            if (str.equals("imperceptible_kill_exempt_proc_states")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -2038720731:
                            if (str.equals("short_fgs_anr_extra_wait_duration")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -1996097272:
                            if (str.equals("kill_bg_restricted_cached_idle_settle_time")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -1905817813:
                            if (str.equals("default_fgs_starts_restriction_enabled")) {
                                c = 3;
                                break;
                            }
                            break;
                        case -1903697007:
                            if (str.equals("service_start_foreground_anr_delay_ms")) {
                                c = 4;
                                break;
                            }
                            break;
                        case -1830853932:
                            if (str.equals("binder_heavy_hitter_watcher_batchsize")) {
                                c = 5;
                                break;
                            }
                            break;
                        case -1782036688:
                            if (str.equals("default_background_activity_starts_enabled")) {
                                c = 6;
                                break;
                            }
                            break;
                        case -1660341473:
                            if (str.equals("fgs_allow_opt_out")) {
                                c = 7;
                                break;
                            }
                            break;
                        case -1640024320:
                            if (str.equals("fgs_start_denied_log_sample_rate")) {
                                c = '\b';
                                break;
                            }
                            break;
                        case -1600089364:
                            if (str.equals("deferred_fgs_notification_exclusion_time_for_short")) {
                                c = '\t';
                                break;
                            }
                            break;
                        case -1483050242:
                            if (str.equals("max_service_connections_per_process")) {
                                c = '\n';
                                break;
                            }
                            break;
                        case -1406935837:
                            if (str.equals("oomadj_update_policy")) {
                                c = 11;
                                break;
                            }
                            break;
                        case -1327576198:
                            if (str.equals("max_previous_time")) {
                                c = '\f';
                                break;
                            }
                            break;
                        case -1303617396:
                            if (str.equals("deferred_fgs_notification_interval")) {
                                c = '\r';
                                break;
                            }
                            break;
                        case -1220759920:
                            if (str.equals("max_phantom_processes")) {
                                c = 14;
                                break;
                            }
                            break;
                        case -1213341854:
                            if (str.equals("short_fgs_timeout_duration")) {
                                c = 15;
                                break;
                            }
                            break;
                        case -1198352864:
                            if (str.equals("default_background_fgs_starts_restriction_enabled")) {
                                c = 16;
                                break;
                            }
                            break;
                        case -1191409506:
                            if (str.equals("force_bg_check_on_restricted")) {
                                c = 17;
                                break;
                            }
                            break;
                        case -1092962821:
                            if (str.equals("max_cached_processes")) {
                                c = 18;
                                break;
                            }
                            break;
                        case -1055864341:
                            if (str.equals("max_empty_time_millis")) {
                                c = 19;
                                break;
                            }
                            break;
                        case -964261074:
                            if (str.equals("network_access_timeout_ms")) {
                                c = 20;
                                break;
                            }
                            break;
                        case -815375578:
                            if (str.equals("min_assoc_log_duration")) {
                                c = 21;
                                break;
                            }
                            break;
                        case -769365680:
                            if (str.equals("process_kill_timeout")) {
                                c = 22;
                                break;
                            }
                            break;
                        case -728096000:
                            if (str.equals("prioritize_alarm_broadcasts")) {
                                c = 23;
                                break;
                            }
                            break;
                        case -682752716:
                            if (str.equals("fgs_atom_sample_rate")) {
                                c = 24;
                                break;
                            }
                            break;
                        case -577670375:
                            if (str.equals("service_start_foreground_timeout_ms")) {
                                c = 25;
                                break;
                            }
                            break;
                        case -449032007:
                            if (str.equals("fgs_start_allowed_log_sample_rate")) {
                                c = 26;
                                break;
                            }
                            break;
                        case -329920445:
                            if (str.equals("default_fgs_starts_restriction_notification_enabled")) {
                                c = 27;
                                break;
                            }
                            break;
                        case -292047334:
                            if (str.equals("binder_heavy_hitter_watcher_enabled")) {
                                c = 28;
                                break;
                            }
                            break;
                        case -253203740:
                            if (str.equals("push_messaging_over_quota_behavior")) {
                                c = 29;
                                break;
                            }
                            break;
                        case -224039213:
                            if (str.equals("no_kill_cached_processes_post_boot_completed_duration_millis")) {
                                c = 30;
                                break;
                            }
                            break;
                        case -216971728:
                            if (str.equals("deferred_fgs_notifications_api_gated")) {
                                c = 31;
                                break;
                            }
                            break;
                        case -84078814:
                            if (str.equals("top_to_fgs_grace_duration")) {
                                c = ' ';
                                break;
                            }
                            break;
                        case -48740806:
                            if (str.equals("imperceptible_kill_exempt_packages")) {
                                c = '!';
                                break;
                            }
                            break;
                        case 21817133:
                            if (str.equals("defer_boot_completed_broadcast")) {
                                c = '\"';
                                break;
                            }
                            break;
                        case 74597321:
                            if (str.equals("tiered_cached_adj_decay_time")) {
                                c = '#';
                                break;
                            }
                            break;
                        case 102688395:
                            if (str.equals("proactive_kills_enabled")) {
                                c = '$';
                                break;
                            }
                            break;
                        case 174194291:
                            if (str.equals("system_exempt_power_restrictions_enabled")) {
                                c = '%';
                                break;
                            }
                            break;
                        case 273690789:
                            if (str.equals("enable_extra_delay_svc_restart_mem_pressure")) {
                                c = PackageManagerShellCommandDataLoader.ARGS_DELIM;
                                break;
                            }
                            break;
                        case 628754725:
                            if (str.equals("deferred_fgs_notification_exclusion_time")) {
                                c = '\'';
                                break;
                            }
                            break;
                        case 886770227:
                            if (str.equals("default_fgs_starts_restriction_check_caller_target_sdk")) {
                                c = '(';
                                break;
                            }
                            break;
                        case 889934779:
                            if (str.equals("no_kill_cached_processes_until_boot_completed")) {
                                c = ')';
                                break;
                            }
                            break;
                        case 969545596:
                            if (str.equals("fg_to_bg_fgs_grace_duration")) {
                                c = '*';
                                break;
                            }
                            break;
                        case 991356293:
                            if (str.equals("enable_app_start_info")) {
                                c = '+';
                                break;
                            }
                            break;
                        case 1113517584:
                            if (str.equals("low_swap_threshold_percent")) {
                                c = ',';
                                break;
                            }
                            break;
                        case 1163990130:
                            if (str.equals("boot_time_temp_allowlist_duration")) {
                                c = '-';
                                break;
                            }
                            break;
                        case 1199252102:
                            if (str.equals("kill_bg_restricted_cached_idle")) {
                                c = '.';
                                break;
                            }
                            break;
                        case 1239401352:
                            if (str.equals("short_fgs_proc_state_extra_wait_duration")) {
                                c = '/';
                                break;
                            }
                            break;
                        case 1351914345:
                            if (str.equals("extra_delay_svc_restart_mem_pressure")) {
                                c = '0';
                                break;
                            }
                            break;
                        case 1380211165:
                            if (str.equals("deferred_fgs_notifications_enabled")) {
                                c = '1';
                                break;
                            }
                            break;
                        case 1444000894:
                            if (str.equals("enable_wait_for_finish_attach_application")) {
                                c = '2';
                                break;
                            }
                            break;
                        case 1509836936:
                            if (str.equals("binder_heavy_hitter_auto_sampler_threshold")) {
                                c = '3';
                                break;
                            }
                            break;
                        case 1577406544:
                            if (str.equals("use_tiered_cached_adj")) {
                                c = '4';
                                break;
                            }
                            break;
                        case 1598050974:
                            if (str.equals("binder_heavy_hitter_auto_sampler_enabled")) {
                                c = '5';
                                break;
                            }
                            break;
                        case 1626346799:
                            if (str.equals("fgs_start_foreground_timeout")) {
                                c = '6';
                                break;
                            }
                            break;
                        case 1874204051:
                            if (str.equals("deferred_fgs_notification_interval_for_short")) {
                                c = '7';
                                break;
                            }
                            break;
                        case 1896529156:
                            if (str.equals("binder_heavy_hitter_watcher_threshold")) {
                                c = '8';
                                break;
                            }
                            break;
                        case 2013655783:
                            if (str.equals("service_bind_almost_perceptible_timeout_ms")) {
                                c = '9';
                                break;
                            }
                            break;
                        case 2077421144:
                            if (str.equals("binder_heavy_hitter_auto_sampler_batchsize")) {
                                c = ':';
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                        case '!':
                            ActivityManagerConstants.this.updateImperceptibleKillExemptions();
                            break;
                        case 1:
                            ActivityManagerConstants.this.updateShortFgsAnrExtraWaitDuration();
                            break;
                        case 2:
                            ActivityManagerConstants.this.updateKillBgRestrictedCachedIdleSettleTime();
                            break;
                        case 3:
                            ActivityManagerConstants.this.updateFgsStartsRestriction();
                            break;
                        case 4:
                            ActivityManagerConstants.this.updateServiceStartForegroundAnrDealyMs();
                            break;
                        case 5:
                        case 28:
                        case '3':
                        case '5':
                        case '8':
                        case ':':
                            ActivityManagerConstants.this.updateBinderHeavyHitterWatcher();
                            break;
                        case 6:
                            ActivityManagerConstants.this.updateBackgroundActivityStarts();
                            break;
                        case 7:
                            ActivityManagerConstants.this.updateFgsAllowOptOut();
                            break;
                        case '\b':
                            ActivityManagerConstants.this.updateFgsStartDeniedLogSamplePercent();
                            break;
                        case '\t':
                            ActivityManagerConstants.this.updateFgsNotificationDeferralExclusionTimeForShort();
                            break;
                        case '\n':
                            ActivityManagerConstants.this.updateMaxServiceConnectionsPerProcess();
                            break;
                        case 11:
                            ActivityManagerConstants.this.updateOomAdjUpdatePolicy();
                            break;
                        case '\f':
                            ActivityManagerConstants.this.updateMaxPreviousTime();
                            break;
                        case '\r':
                            ActivityManagerConstants.this.updateFgsNotificationDeferralInterval();
                            break;
                        case 14:
                            ActivityManagerConstants.this.updateMaxPhantomProcesses();
                            break;
                        case 15:
                            ActivityManagerConstants.this.updateShortFgsTimeoutDuration();
                            break;
                        case 16:
                            ActivityManagerConstants.this.updateBackgroundFgsStartsRestriction();
                            break;
                        case 17:
                            ActivityManagerConstants.this.updateForceRestrictedBackgroundCheck();
                            break;
                        case 18:
                            ActivityManagerConstants.this.updateMaxCachedProcesses();
                            break;
                        case 19:
                            ActivityManagerConstants.this.updateMaxEmptyTimeMillis();
                            break;
                        case 20:
                            ActivityManagerConstants.this.updateNetworkAccessTimeoutMs();
                            break;
                        case 21:
                            ActivityManagerConstants.this.updateMinAssocLogDuration();
                            break;
                        case 22:
                            ActivityManagerConstants.this.updateProcessKillTimeout();
                            break;
                        case 23:
                            ActivityManagerConstants.this.updatePrioritizeAlarmBroadcasts();
                            break;
                        case 24:
                            ActivityManagerConstants.this.updateFgsAtomSamplePercent();
                            break;
                        case 25:
                            ActivityManagerConstants.this.updateServiceStartForegroundTimeoutMs();
                            break;
                        case 26:
                            ActivityManagerConstants.this.updateFgsStartAllowedLogSamplePercent();
                            break;
                        case 27:
                            ActivityManagerConstants.this.updateFgsStartsRestrictionNotification();
                            break;
                        case 29:
                            ActivityManagerConstants.this.updatePushMessagingOverQuotaBehavior();
                            break;
                        case 30:
                            ActivityManagerConstants.this.updateNoKillCachedProcessesPostBootCompletedDurationMillis();
                            break;
                        case 31:
                            ActivityManagerConstants.this.updateFgsNotificationDeferralApiGated();
                            break;
                        case ' ':
                            ActivityManagerConstants.this.updateTopToFgsGraceDuration();
                            break;
                        case '\"':
                            ActivityManagerConstants.this.updateDeferBootCompletedBroadcast();
                            break;
                        case '#':
                        case '4':
                            ActivityManagerConstants.this.updateUseTieredCachedAdj();
                            break;
                        case '$':
                            ActivityManagerConstants.this.updateProactiveKillsEnabled();
                            break;
                        case '%':
                            ActivityManagerConstants.this.updateSystemExemptPowerRestrictionsEnabled();
                            break;
                        case '&':
                            ActivityManagerConstants.this.updateEnableExtraServiceRestartDelayOnMemPressure();
                            break;
                        case '\'':
                            ActivityManagerConstants.this.updateFgsNotificationDeferralExclusionTime();
                            break;
                        case '(':
                            ActivityManagerConstants.this.updateFgsStartsRestrictionCheckCallerTargetSdk();
                            break;
                        case ')':
                            ActivityManagerConstants.this.updateNoKillCachedProcessesUntilBootCompleted();
                            break;
                        case '*':
                            ActivityManagerConstants.this.updateFgToBgFgsGraceDuration();
                            break;
                        case '+':
                            ActivityManagerConstants.this.updateApplicationStartInfoEnabled();
                            break;
                        case ',':
                            ActivityManagerConstants.this.updateLowSwapThresholdPercent();
                            break;
                        case '-':
                            ActivityManagerConstants.this.updateBootTimeTempAllowListDuration();
                            break;
                        case '.':
                            ActivityManagerConstants.this.updateKillBgRestrictedCachedIdle();
                            break;
                        case '/':
                            ActivityManagerConstants.this.updateShortFgsProcStateExtraWaitDuration();
                            break;
                        case '0':
                            ActivityManagerConstants.this.updateExtraServiceRestartDelayOnMemPressure();
                            break;
                        case '1':
                            ActivityManagerConstants.this.updateFgsNotificationDeferralEnable();
                            break;
                        case '2':
                            ActivityManagerConstants.this.updateEnableWaitForFinishAttachApplication();
                            break;
                        case '6':
                            ActivityManagerConstants.this.updateFgsStartForegroundTimeout();
                            break;
                        case '7':
                            ActivityManagerConstants.this.updateFgsNotificationDeferralIntervalForShort();
                            break;
                        case '9':
                            ActivityManagerConstants.this.updateServiceBindAlmostPerceptibleTimeoutMs();
                            break;
                    }
                }
            }
        };
        this.mOnDeviceConfigChangedForComponentAliasListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.ActivityManagerConstants.2
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                String str;
                Iterator it = properties.getKeyset().iterator();
                while (it.hasNext() && (str = (String) it.next()) != null) {
                    if (str.equals("enable_experimental_component_alias") || str.equals("component_alias_overrides")) {
                        ActivityManagerConstants.this.updateComponentAliases();
                    }
                }
            }
        };
        this.mService = activityManagerService;
        this.mSystemServerAutomaticHeapDumpEnabled = (Build.IS_DEBUGGABLE && context.getResources().getBoolean(17891590)) ? z : false;
        this.mSystemServerAutomaticHeapDumpPackageName = context.getPackageName();
        this.mSystemServerAutomaticHeapDumpPssThresholdBytes = Math.max(102400L, context.getResources().getInteger(17694785));
        this.mDefaultImperceptibleKillExemptPackages = Arrays.asList(context.getResources().getStringArray(17236020));
        this.mDefaultImperceptibleKillExemptProcStates = (List) Arrays.stream(context.getResources().getIntArray(17236021)).boxed().collect(Collectors.toList());
        this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.addAll(this.mDefaultImperceptibleKillExemptPackages);
        this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.addAll(this.mDefaultImperceptibleKillExemptProcStates);
        boolean z2 = context.getResources().getBoolean(17891593);
        this.mDefaultBinderHeavyHitterWatcherEnabled = z2;
        int integer = context.getResources().getInteger(17694789);
        this.mDefaultBinderHeavyHitterWatcherBatchSize = integer;
        float f = context.getResources().getFloat(17105070);
        this.mDefaultBinderHeavyHitterWatcherThreshold = f;
        boolean z3 = context.getResources().getBoolean(17891592);
        this.mDefaultBinderHeavyHitterAutoSamplerEnabled = z3;
        int integer2 = context.getResources().getInteger(17694788);
        this.mDefaultBinderHeavyHitterAutoSamplerBatchSize = integer2;
        float f2 = context.getResources().getFloat(17105069);
        this.mDefaultBinderHeavyHitterAutoSamplerThreshold = f2;
        BINDER_HEAVY_HITTER_WATCHER_ENABLED = z2;
        BINDER_HEAVY_HITTER_WATCHER_BATCHSIZE = integer;
        BINDER_HEAVY_HITTER_WATCHER_THRESHOLD = f;
        BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED = z3;
        BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE = integer2;
        BINDER_HEAVY_HITTER_AUTO_SAMPLER_THRESHOLD = f2;
        activityManagerService.scheduleUpdateBinderHeavyHitterWatcherConfig();
        arraySet.addAll((Collection) Arrays.stream(context.getResources().getStringArray(17236085)).map(new Function() { // from class: com.android.server.am.ActivityManagerConstants$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ComponentName.unflattenFromString((String) obj);
            }
        }).collect(Collectors.toSet()));
        int integer3 = context.getResources().getInteger(17694780);
        this.mCustomizedMaxCachedProcesses = integer3;
        this.CUR_MAX_CACHED_PROCESSES = integer3;
        this.CUR_MAX_EMPTY_PROCESSES = computeEmptyProcessLimit(integer3);
    }

    public void start(ContentResolver contentResolver) {
        this.mResolver = contentResolver;
        contentResolver.registerContentObserver(ACTIVITY_MANAGER_CONSTANTS_URI, false, this);
        this.mResolver.registerContentObserver(ACTIVITY_STARTS_LOGGING_ENABLED_URI, false, this);
        this.mResolver.registerContentObserver(FOREGROUND_SERVICE_STARTS_LOGGING_ENABLED_URI, false, this);
        if (this.mSystemServerAutomaticHeapDumpEnabled) {
            this.mResolver.registerContentObserver(ENABLE_AUTOMATIC_SYSTEM_SERVER_HEAP_DUMPS_URI, false, this);
        }
        updateConstants();
        if (this.mSystemServerAutomaticHeapDumpEnabled) {
            updateEnableAutomaticSystemServerHeapDumps();
        }
        DeviceConfig.addOnPropertiesChangedListener("activity_manager", ActivityThread.currentApplication().getMainExecutor(), this.mOnDeviceConfigChangedListener);
        DeviceConfig.addOnPropertiesChangedListener("activity_manager_ca", ActivityThread.currentApplication().getMainExecutor(), this.mOnDeviceConfigChangedForComponentAliasListener);
        loadDeviceConfigConstants();
        updateActivityStartsLoggingEnabled();
        updateForegroundServiceStartsLoggingEnabled();
    }

    public final void loadDeviceConfigConstants() {
        this.mOnDeviceConfigChangedListener.onPropertiesChanged(DeviceConfig.getProperties("activity_manager", new String[0]));
        this.mOnDeviceConfigChangedForComponentAliasListener.onPropertiesChanged(DeviceConfig.getProperties("activity_manager_ca", new String[0]));
    }

    public void setOverrideMaxCachedProcesses(int i) {
        this.mOverrideMaxCachedProcesses = i;
        updateMaxCachedProcesses();
    }

    public int getOverrideMaxCachedProcesses() {
        return this.mOverrideMaxCachedProcesses;
    }

    public static int computeEmptyProcessLimit(int i) {
        return i / 2;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        if (uri == null) {
            return;
        }
        if (ACTIVITY_MANAGER_CONSTANTS_URI.equals(uri)) {
            updateConstants();
        } else if (ACTIVITY_STARTS_LOGGING_ENABLED_URI.equals(uri)) {
            updateActivityStartsLoggingEnabled();
        } else if (FOREGROUND_SERVICE_STARTS_LOGGING_ENABLED_URI.equals(uri)) {
            updateForegroundServiceStartsLoggingEnabled();
        } else if (ENABLE_AUTOMATIC_SYSTEM_SERVER_HEAP_DUMPS_URI.equals(uri)) {
            updateEnableAutomaticSystemServerHeapDumps();
        }
    }

    public final void updateConstants() {
        String string = Settings.Global.getString(this.mResolver, "activity_manager_constants");
        synchronized (this.mService) {
            try {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mParser.setString(string);
                } catch (IllegalArgumentException e) {
                    Slog.e("ActivityManagerConstants", "Bad activity manager config settings", e);
                }
                long j = this.POWER_CHECK_INTERVAL;
                this.BACKGROUND_SETTLE_TIME = this.mParser.getLong("background_settle_time", 60000L);
                this.FGSERVICE_MIN_SHOWN_TIME = this.mParser.getLong("fgservice_min_shown_time", 2000L);
                this.FGSERVICE_MIN_REPORT_TIME = this.mParser.getLong("fgservice_min_report_time", (long) BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
                this.FGSERVICE_SCREEN_ON_BEFORE_TIME = this.mParser.getLong("fgservice_screen_on_before_time", 1000L);
                this.FGSERVICE_SCREEN_ON_AFTER_TIME = this.mParser.getLong("fgservice_screen_on_after_time", 5000L);
                this.CONTENT_PROVIDER_RETAIN_TIME = this.mParser.getLong("content_provider_retain_time", 20000L);
                this.GC_TIMEOUT = this.mParser.getLong("gc_timeout", 5000L);
                this.GC_MIN_INTERVAL = this.mParser.getLong("gc_min_interval", 60000L);
                this.FULL_PSS_MIN_INTERVAL = this.mParser.getLong("full_pss_min_interval", 1200000L);
                this.FULL_PSS_LOWERED_INTERVAL = this.mParser.getLong("full_pss_lowered_interval", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.POWER_CHECK_INTERVAL = this.mParser.getLong("power_check_interval", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.POWER_CHECK_MAX_CPU_1 = this.mParser.getInt("power_check_max_cpu_1", 25);
                this.POWER_CHECK_MAX_CPU_2 = this.mParser.getInt("power_check_max_cpu_2", 25);
                this.POWER_CHECK_MAX_CPU_3 = this.mParser.getInt("power_check_max_cpu_3", 10);
                this.POWER_CHECK_MAX_CPU_4 = this.mParser.getInt("power_check_max_cpu_4", 2);
                this.SERVICE_USAGE_INTERACTION_TIME_PRE_S = this.mParser.getLong("service_usage_interaction_time", 1800000L);
                this.SERVICE_USAGE_INTERACTION_TIME_POST_S = this.mParser.getLong("service_usage_interaction_time_post_s", 60000L);
                this.USAGE_STATS_INTERACTION_INTERVAL_PRE_S = this.mParser.getLong("usage_stats_interaction_interval", 7200000L);
                this.USAGE_STATS_INTERACTION_INTERVAL_POST_S = this.mParser.getLong("usage_stats_interaction_interval_post_s", 600000L);
                this.SERVICE_RESTART_DURATION = this.mParser.getLong("service_restart_duration", 1000L);
                this.SERVICE_RESET_RUN_DURATION = this.mParser.getLong("service_reset_run_duration", 60000L);
                this.SERVICE_RESTART_DURATION_FACTOR = this.mParser.getInt("service_restart_duration_factor", 4);
                this.SERVICE_MIN_RESTART_TIME_BETWEEN = this.mParser.getLong("service_min_restart_time_between", 10000L);
                this.MAX_SERVICE_INACTIVITY = this.mParser.getLong("service_max_inactivity", 1800000L);
                this.BG_START_TIMEOUT = this.mParser.getLong("service_bg_start_timeout", 15000L);
                this.SERVICE_BG_ACTIVITY_START_TIMEOUT = this.mParser.getLong("service_bg_activity_start_timeout", 10000L);
                this.BOUND_SERVICE_CRASH_RESTART_DURATION = this.mParser.getLong("service_crash_restart_duration", 1800000L);
                this.BOUND_SERVICE_MAX_CRASH_RETRY = this.mParser.getInt("service_crash_max_retry", 16);
                this.FLAG_PROCESS_START_ASYNC = this.mParser.getBoolean("process_start_async", true);
                this.MEMORY_INFO_THROTTLE_TIME = this.mParser.getLong("memory_info_throttle_time", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION = this.mParser.getDurationMillis("top_to_almost_perceptible_grace_duration", 15000L);
                MIN_CRASH_INTERVAL = this.mParser.getInt("min_crash_interval", 120000);
                this.PENDINGINTENT_WARNING_THRESHOLD = this.mParser.getInt("pendingintent_warning_threshold", 2000);
                PROCESS_CRASH_COUNT_RESET_INTERVAL = this.mParser.getInt("process_crash_count_reset_interval", 43200000);
                PROCESS_CRASH_COUNT_LIMIT = this.mParser.getInt("process_crash_count_limit", 12);
                if (this.POWER_CHECK_INTERVAL != j) {
                    this.mService.mHandler.removeMessages(27);
                    this.mService.mHandler.sendMessageDelayed(this.mService.mHandler.obtainMessage(27), this.POWER_CHECK_INTERVAL);
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public final void updateActivityStartsLoggingEnabled() {
        this.mFlagActivityStartsLoggingEnabled = Settings.Global.getInt(this.mResolver, "activity_starts_logging_enabled", 1) == 1;
    }

    public final void updateApplicationStartInfoEnabled() {
        this.mFlagApplicationStartInfoEnabled = DeviceConfig.getBoolean("activity_manager", "enable_app_start_info", false);
    }

    public final void updateBackgroundActivityStarts() {
        this.mFlagBackgroundActivityStartsEnabled = DeviceConfig.getBoolean("activity_manager", "default_background_activity_starts_enabled", false);
    }

    public final void updateForegroundServiceStartsLoggingEnabled() {
        this.mFlagForegroundServiceStartsLoggingEnabled = Settings.Global.getInt(this.mResolver, "foreground_service_starts_logging_enabled", 1) == 1;
    }

    public final void updateBackgroundFgsStartsRestriction() {
        this.mFlagBackgroundFgsStartRestrictionEnabled = DeviceConfig.getBoolean("activity_manager", "default_background_fgs_starts_restriction_enabled", true);
    }

    public final void updateFgsStartsRestriction() {
        this.mFlagFgsStartRestrictionEnabled = DeviceConfig.getBoolean("activity_manager", "default_fgs_starts_restriction_enabled", true);
    }

    public final void updateFgsStartsRestrictionNotification() {
        this.mFgsStartRestrictionNotificationEnabled = DeviceConfig.getBoolean("activity_manager", "default_fgs_starts_restriction_notification_enabled", false);
    }

    public final void updateFgsStartsRestrictionCheckCallerTargetSdk() {
        this.mFgsStartRestrictionCheckCallerTargetSdk = DeviceConfig.getBoolean("activity_manager", "default_fgs_starts_restriction_check_caller_target_sdk", true);
    }

    public final void updateFgsNotificationDeferralEnable() {
        this.mFlagFgsNotificationDeferralEnabled = DeviceConfig.getBoolean("activity_manager", "deferred_fgs_notifications_enabled", true);
    }

    public final void updateFgsNotificationDeferralApiGated() {
        this.mFlagFgsNotificationDeferralApiGated = DeviceConfig.getBoolean("activity_manager", "deferred_fgs_notifications_api_gated", false);
    }

    public final void updateFgsNotificationDeferralInterval() {
        this.mFgsNotificationDeferralInterval = DeviceConfig.getLong("activity_manager", "deferred_fgs_notification_interval", 10000L);
    }

    public final void updateFgsNotificationDeferralIntervalForShort() {
        this.mFgsNotificationDeferralIntervalForShort = DeviceConfig.getLong("activity_manager", "deferred_fgs_notification_interval_for_short", 10000L);
    }

    public final void updateFgsNotificationDeferralExclusionTime() {
        this.mFgsNotificationDeferralExclusionTime = DeviceConfig.getLong("activity_manager", "deferred_fgs_notification_exclusion_time", 120000L);
    }

    public final void updateFgsNotificationDeferralExclusionTimeForShort() {
        this.mFgsNotificationDeferralExclusionTimeForShort = DeviceConfig.getLong("activity_manager", "deferred_fgs_notification_exclusion_time_for_short", 120000L);
    }

    public final void updateSystemExemptPowerRestrictionsEnabled() {
        this.mFlagSystemExemptPowerRestrictionsEnabled = DeviceConfig.getBoolean("activity_manager", "system_exempt_power_restrictions_enabled", true);
    }

    public final void updatePushMessagingOverQuotaBehavior() {
        this.mPushMessagingOverQuotaBehavior = DeviceConfig.getInt("activity_manager", "push_messaging_over_quota_behavior", 1);
        if (this.mPushMessagingOverQuotaBehavior < -1 || this.mPushMessagingOverQuotaBehavior > 1) {
            this.mPushMessagingOverQuotaBehavior = 1;
        }
    }

    public final void updateOomAdjUpdatePolicy() {
        this.OOMADJ_UPDATE_QUICK = DeviceConfig.getInt("activity_manager", "oomadj_update_policy", 1) == 1;
    }

    public final void updateForceRestrictedBackgroundCheck() {
        this.FORCE_BACKGROUND_CHECK_ON_RESTRICTED_APPS = DeviceConfig.getBoolean("activity_manager", "force_bg_check_on_restricted", true);
    }

    public final void updateBootTimeTempAllowListDuration() {
        this.mBootTimeTempAllowlistDuration = DeviceConfig.getLong("activity_manager", "boot_time_temp_allowlist_duration", 20000L);
    }

    public final void updateFgToBgFgsGraceDuration() {
        this.mFgToBgFgsGraceDuration = DeviceConfig.getLong("activity_manager", "fg_to_bg_fgs_grace_duration", 5000L);
    }

    public final void updateFgsStartForegroundTimeout() {
        this.mFgsStartForegroundTimeoutMs = DeviceConfig.getLong("activity_manager", "fgs_start_foreground_timeout", 10000L);
    }

    public final void updateFgsAtomSamplePercent() {
        this.mFgsAtomSampleRate = DeviceConfig.getFloat("activity_manager", "fgs_atom_sample_rate", 1.0f);
    }

    public final void updateFgsStartAllowedLogSamplePercent() {
        this.mFgsStartAllowedLogSampleRate = DeviceConfig.getFloat("activity_manager", "fgs_start_allowed_log_sample_rate", 0.25f);
    }

    public final void updateFgsStartDeniedLogSamplePercent() {
        this.mFgsStartDeniedLogSampleRate = DeviceConfig.getFloat("activity_manager", "fgs_start_denied_log_sample_rate", 1.0f);
    }

    public final void updateKillBgRestrictedCachedIdle() {
        this.mKillBgRestrictedAndCachedIdle = DeviceConfig.getBoolean("activity_manager", "kill_bg_restricted_cached_idle", true);
    }

    public final void updateKillBgRestrictedCachedIdleSettleTime() {
        long j = this.mKillBgRestrictedAndCachedIdleSettleTimeMs;
        this.mKillBgRestrictedAndCachedIdleSettleTimeMs = DeviceConfig.getLong("activity_manager", "kill_bg_restricted_cached_idle_settle_time", 60000L);
        if (this.mKillBgRestrictedAndCachedIdleSettleTimeMs != j) {
            this.mService.mHandler.removeMessages(58);
            this.mService.mHandler.sendEmptyMessageDelayed(58, this.mKillBgRestrictedAndCachedIdleSettleTimeMs);
        }
    }

    public final void updateFgsAllowOptOut() {
        this.mFgsAllowOptOut = DeviceConfig.getBoolean("activity_manager", "fgs_allow_opt_out", false);
    }

    public final void updateExtraServiceRestartDelayOnMemPressure() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int lastMemoryLevelLocked = this.mService.mAppProfiler.getLastMemoryLevelLocked();
                long[] jArr = this.mExtraServiceRestartDelayOnMemPressure;
                long[] parseLongArray = parseLongArray("extra_delay_svc_restart_mem_pressure", DEFAULT_EXTRA_SERVICE_RESTART_DELAY_ON_MEM_PRESSURE);
                this.mExtraServiceRestartDelayOnMemPressure = parseLongArray;
                this.mService.mServices.performRescheduleServiceRestartOnMemoryPressureLocked(parseLongArray[lastMemoryLevelLocked], jArr[lastMemoryLevelLocked], "config", SystemClock.uptimeMillis());
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public final void updateEnableExtraServiceRestartDelayOnMemPressure() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                boolean z = this.mEnableExtraServiceRestartDelayOnMemPressure;
                boolean z2 = DeviceConfig.getBoolean("activity_manager", "enable_extra_delay_svc_restart_mem_pressure", true);
                this.mEnableExtraServiceRestartDelayOnMemPressure = z2;
                this.mService.mServices.rescheduleServiceRestartOnMemoryPressureIfNeededLocked(z, z2, SystemClock.uptimeMillis());
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public final void updatePrioritizeAlarmBroadcasts() {
        String string = DeviceConfig.getString("activity_manager", "prioritize_alarm_broadcasts", "");
        this.mPrioritizeAlarmBroadcasts = TextUtils.isEmpty(string) ? true : Boolean.parseBoolean(string);
    }

    public final void updateDeferBootCompletedBroadcast() {
        this.mDeferBootCompletedBroadcast = DeviceConfig.getInt("activity_manager", "defer_boot_completed_broadcast", 6);
    }

    public final void updateNoKillCachedProcessesUntilBootCompleted() {
        this.mNoKillCachedProcessesUntilBootCompleted = DeviceConfig.getBoolean("activity_manager", "no_kill_cached_processes_until_boot_completed", true);
    }

    public final void updateNoKillCachedProcessesPostBootCompletedDurationMillis() {
        this.mNoKillCachedProcessesPostBootCompletedDurationMillis = DeviceConfig.getLong("activity_manager", "no_kill_cached_processes_post_boot_completed_duration_millis", 600000L);
    }

    public final void updateMaxEmptyTimeMillis() {
        this.mMaxEmptyTimeMillis = DeviceConfig.getLong("activity_manager", "max_empty_time_millis", 1800000L);
    }

    public final void updateNetworkAccessTimeoutMs() {
        this.mNetworkAccessTimeoutMs = DeviceConfig.getLong("activity_manager", "network_access_timeout_ms", 200L);
    }

    public final void updateServiceStartForegroundTimeoutMs() {
        this.mServiceStartForegroundTimeoutMs = DeviceConfig.getInt("activity_manager", "service_start_foreground_timeout_ms", 30000);
    }

    public final void updateServiceStartForegroundAnrDealyMs() {
        this.mServiceStartForegroundAnrDelayMs = DeviceConfig.getInt("activity_manager", "service_start_foreground_anr_delay_ms", (int) FrameworkStatsLog.WIFI_BYTES_TRANSFER);
    }

    public final void updateServiceBindAlmostPerceptibleTimeoutMs() {
        this.mServiceBindAlmostPerceptibleTimeoutMs = DeviceConfig.getLong("activity_manager", "service_bind_almost_perceptible_timeout_ms", 15000L);
    }

    public final long[] parseLongArray(String str, long[] jArr) {
        String string = DeviceConfig.getString("activity_manager", str, (String) null);
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(",");
            if (split.length == jArr.length) {
                long[] jArr2 = new long[split.length];
                for (int i = 0; i < split.length; i++) {
                    try {
                        jArr2[i] = Long.parseLong(split[i]);
                    } catch (NumberFormatException unused) {
                    }
                }
                return jArr2;
            }
        }
        return jArr;
    }

    public final void updateComponentAliases() {
        this.mEnableComponentAlias = DeviceConfig.getBoolean("activity_manager_ca", "enable_experimental_component_alias", false);
        this.mComponentAliasOverrides = DeviceConfig.getString("activity_manager_ca", "component_alias_overrides", "");
        this.mService.mComponentAliasResolver.update(this.mEnableComponentAlias, this.mComponentAliasOverrides);
    }

    public final void updateProcessKillTimeout() {
        this.mProcessKillTimeoutMs = DeviceConfig.getLong("activity_manager", "process_kill_timeout", 10000L);
    }

    public final void updateImperceptibleKillExemptions() {
        this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.clear();
        this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.addAll(this.mDefaultImperceptibleKillExemptPackages);
        String string = DeviceConfig.getString("activity_manager", "imperceptible_kill_exempt_packages", (String) null);
        if (!TextUtils.isEmpty(string)) {
            this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.addAll(Arrays.asList(string.split(",")));
        }
        this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.clear();
        this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.addAll(this.mDefaultImperceptibleKillExemptProcStates);
        String string2 = DeviceConfig.getString("activity_manager", "imperceptible_kill_exempt_proc_states", (String) null);
        if (TextUtils.isEmpty(string2)) {
            return;
        }
        Arrays.asList(string2.split(",")).stream().forEach(new Consumer() { // from class: com.android.server.am.ActivityManagerConstants$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ActivityManagerConstants.this.lambda$updateImperceptibleKillExemptions$0((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateImperceptibleKillExemptions$0(String str) {
        try {
            this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.add(Integer.valueOf(Integer.parseInt(str)));
        } catch (NumberFormatException unused) {
        }
    }

    public final void updateEnableAutomaticSystemServerHeapDumps() {
        if (!this.mSystemServerAutomaticHeapDumpEnabled) {
            Slog.wtf("ActivityManagerConstants", "updateEnableAutomaticSystemServerHeapDumps called when leak detection disabled");
        } else {
            this.mService.setDumpHeapDebugLimit(null, 0, Settings.Global.getInt(this.mResolver, "enable_automatic_system_server_heap_dumps", 1) == 1 ? this.mSystemServerAutomaticHeapDumpPssThresholdBytes : 0L, this.mSystemServerAutomaticHeapDumpPackageName);
        }
    }

    public final void updateMaxCachedProcesses() {
        String property = DeviceConfig.getProperty("activity_manager", "max_cached_processes");
        try {
            int i = this.mOverrideMaxCachedProcesses;
            if (i < 0) {
                i = TextUtils.isEmpty(property) ? this.mCustomizedMaxCachedProcesses : Integer.parseInt(property);
            }
            this.CUR_MAX_CACHED_PROCESSES = i;
        } catch (NumberFormatException e) {
            Slog.e("ActivityManagerConstants", "Unable to parse flag for max_cached_processes: " + property, e);
            this.CUR_MAX_CACHED_PROCESSES = this.mCustomizedMaxCachedProcesses;
        }
        this.CUR_MAX_EMPTY_PROCESSES = computeEmptyProcessLimit(this.CUR_MAX_CACHED_PROCESSES);
        int computeEmptyProcessLimit = computeEmptyProcessLimit(this.MAX_CACHED_PROCESSES);
        this.CUR_TRIM_EMPTY_PROCESSES = computeEmptyProcessLimit / 2;
        this.CUR_TRIM_CACHED_PROCESSES = (this.MAX_CACHED_PROCESSES - computeEmptyProcessLimit) / 3;
    }

    public final void updateProactiveKillsEnabled() {
        PROACTIVE_KILLS_ENABLED = DeviceConfig.getBoolean("activity_manager", "proactive_kills_enabled", false);
    }

    public final void updateLowSwapThresholdPercent() {
        LOW_SWAP_THRESHOLD_PERCENT = DeviceConfig.getFloat("activity_manager", "low_swap_threshold_percent", 0.1f);
    }

    public final void updateTopToFgsGraceDuration() {
        this.TOP_TO_FGS_GRACE_DURATION = DeviceConfig.getLong("activity_manager", "top_to_fgs_grace_duration", 15000L);
    }

    public final void updateMaxPreviousTime() {
        MAX_PREVIOUS_TIME = DeviceConfig.getLong("activity_manager", "max_previous_time", 60000L);
    }

    public final void updateMinAssocLogDuration() {
        MIN_ASSOC_LOG_DURATION = DeviceConfig.getLong("activity_manager", "min_assoc_log_duration", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
    }

    public final void updateBinderHeavyHitterWatcher() {
        BINDER_HEAVY_HITTER_WATCHER_ENABLED = DeviceConfig.getBoolean("activity_manager", "binder_heavy_hitter_watcher_enabled", this.mDefaultBinderHeavyHitterWatcherEnabled);
        BINDER_HEAVY_HITTER_WATCHER_BATCHSIZE = DeviceConfig.getInt("activity_manager", "binder_heavy_hitter_watcher_batchsize", this.mDefaultBinderHeavyHitterWatcherBatchSize);
        BINDER_HEAVY_HITTER_WATCHER_THRESHOLD = DeviceConfig.getFloat("activity_manager", "binder_heavy_hitter_watcher_threshold", this.mDefaultBinderHeavyHitterWatcherThreshold);
        BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED = DeviceConfig.getBoolean("activity_manager", "binder_heavy_hitter_auto_sampler_enabled", this.mDefaultBinderHeavyHitterAutoSamplerEnabled);
        BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE = DeviceConfig.getInt("activity_manager", "binder_heavy_hitter_auto_sampler_batchsize", this.mDefaultBinderHeavyHitterAutoSamplerBatchSize);
        BINDER_HEAVY_HITTER_WATCHER_THRESHOLD = DeviceConfig.getFloat("activity_manager", "binder_heavy_hitter_auto_sampler_threshold", this.mDefaultBinderHeavyHitterAutoSamplerThreshold);
        this.mService.scheduleUpdateBinderHeavyHitterWatcherConfig();
    }

    public final void updateMaxPhantomProcesses() {
        int i = this.MAX_PHANTOM_PROCESSES;
        int i2 = DeviceConfig.getInt("activity_manager", "max_phantom_processes", 32);
        this.MAX_PHANTOM_PROCESSES = i2;
        if (i > i2) {
            ActivityManagerService activityManagerService = this.mService;
            ActivityManagerService.MainHandler mainHandler = activityManagerService.mHandler;
            PhantomProcessList phantomProcessList = activityManagerService.mPhantomProcessList;
            Objects.requireNonNull(phantomProcessList);
            mainHandler.post(new ActivityManagerConstants$$ExternalSyntheticLambda2(phantomProcessList));
        }
    }

    public final void updateMaxServiceConnectionsPerProcess() {
        this.mMaxServiceConnectionsPerProcess = DeviceConfig.getInt("activity_manager", "max_service_connections_per_process", 3000);
    }

    public final void updateShortFgsTimeoutDuration() {
        this.mShortFgsTimeoutDuration = DeviceConfig.getLong("activity_manager", "short_fgs_timeout_duration", 180000L);
    }

    public final void updateShortFgsProcStateExtraWaitDuration() {
        this.mShortFgsProcStateExtraWaitDuration = DeviceConfig.getLong("activity_manager", "short_fgs_proc_state_extra_wait_duration", 5000L);
    }

    public final void updateShortFgsAnrExtraWaitDuration() {
        this.mShortFgsAnrExtraWaitDuration = DeviceConfig.getLong("activity_manager", "short_fgs_anr_extra_wait_duration", 10000L);
    }

    public final void updateEnableWaitForFinishAttachApplication() {
        this.mEnableWaitForFinishAttachApplication = DeviceConfig.getBoolean("activity_manager", "enable_wait_for_finish_attach_application", false);
    }

    public final void updateUseTieredCachedAdj() {
        this.USE_TIERED_CACHED_ADJ = DeviceConfig.getBoolean("activity_manager", "use_tiered_cached_adj", false);
        this.TIERED_CACHED_ADJ_DECAY_TIME = DeviceConfig.getLong("activity_manager", "tiered_cached_adj_decay_time", 60000L);
    }

    @NeverCompile
    public void dump(PrintWriter printWriter) {
        printWriter.println("ACTIVITY MANAGER SETTINGS (dumpsys activity settings) activity_manager_constants:");
        printWriter.print("  ");
        printWriter.print("max_cached_processes");
        printWriter.print("=");
        printWriter.println(this.MAX_CACHED_PROCESSES);
        printWriter.print("  ");
        printWriter.print("background_settle_time");
        printWriter.print("=");
        printWriter.println(this.BACKGROUND_SETTLE_TIME);
        printWriter.print("  ");
        printWriter.print("fgservice_min_shown_time");
        printWriter.print("=");
        printWriter.println(this.FGSERVICE_MIN_SHOWN_TIME);
        printWriter.print("  ");
        printWriter.print("fgservice_min_report_time");
        printWriter.print("=");
        printWriter.println(this.FGSERVICE_MIN_REPORT_TIME);
        printWriter.print("  ");
        printWriter.print("fgservice_screen_on_before_time");
        printWriter.print("=");
        printWriter.println(this.FGSERVICE_SCREEN_ON_BEFORE_TIME);
        printWriter.print("  ");
        printWriter.print("fgservice_screen_on_after_time");
        printWriter.print("=");
        printWriter.println(this.FGSERVICE_SCREEN_ON_AFTER_TIME);
        printWriter.print("  ");
        printWriter.print("content_provider_retain_time");
        printWriter.print("=");
        printWriter.println(this.CONTENT_PROVIDER_RETAIN_TIME);
        printWriter.print("  ");
        printWriter.print("gc_timeout");
        printWriter.print("=");
        printWriter.println(this.GC_TIMEOUT);
        printWriter.print("  ");
        printWriter.print("gc_min_interval");
        printWriter.print("=");
        printWriter.println(this.GC_MIN_INTERVAL);
        printWriter.print("  ");
        printWriter.print("force_bg_check_on_restricted");
        printWriter.print("=");
        printWriter.println(this.FORCE_BACKGROUND_CHECK_ON_RESTRICTED_APPS);
        printWriter.print("  ");
        printWriter.print("full_pss_min_interval");
        printWriter.print("=");
        printWriter.println(this.FULL_PSS_MIN_INTERVAL);
        printWriter.print("  ");
        printWriter.print("full_pss_lowered_interval");
        printWriter.print("=");
        printWriter.println(this.FULL_PSS_LOWERED_INTERVAL);
        printWriter.print("  ");
        printWriter.print("power_check_interval");
        printWriter.print("=");
        printWriter.println(this.POWER_CHECK_INTERVAL);
        printWriter.print("  ");
        printWriter.print("power_check_max_cpu_1");
        printWriter.print("=");
        printWriter.println(this.POWER_CHECK_MAX_CPU_1);
        printWriter.print("  ");
        printWriter.print("power_check_max_cpu_2");
        printWriter.print("=");
        printWriter.println(this.POWER_CHECK_MAX_CPU_2);
        printWriter.print("  ");
        printWriter.print("power_check_max_cpu_3");
        printWriter.print("=");
        printWriter.println(this.POWER_CHECK_MAX_CPU_3);
        printWriter.print("  ");
        printWriter.print("power_check_max_cpu_4");
        printWriter.print("=");
        printWriter.println(this.POWER_CHECK_MAX_CPU_4);
        printWriter.print("  ");
        printWriter.print("service_usage_interaction_time");
        printWriter.print("=");
        printWriter.println(this.SERVICE_USAGE_INTERACTION_TIME_PRE_S);
        printWriter.print("  ");
        printWriter.print("service_usage_interaction_time_post_s");
        printWriter.print("=");
        printWriter.println(this.SERVICE_USAGE_INTERACTION_TIME_POST_S);
        printWriter.print("  ");
        printWriter.print("usage_stats_interaction_interval");
        printWriter.print("=");
        printWriter.println(this.USAGE_STATS_INTERACTION_INTERVAL_PRE_S);
        printWriter.print("  ");
        printWriter.print("usage_stats_interaction_interval_post_s");
        printWriter.print("=");
        printWriter.println(this.USAGE_STATS_INTERACTION_INTERVAL_POST_S);
        printWriter.print("  ");
        printWriter.print("service_restart_duration");
        printWriter.print("=");
        printWriter.println(this.SERVICE_RESTART_DURATION);
        printWriter.print("  ");
        printWriter.print("service_reset_run_duration");
        printWriter.print("=");
        printWriter.println(this.SERVICE_RESET_RUN_DURATION);
        printWriter.print("  ");
        printWriter.print("service_restart_duration_factor");
        printWriter.print("=");
        printWriter.println(this.SERVICE_RESTART_DURATION_FACTOR);
        printWriter.print("  ");
        printWriter.print("service_min_restart_time_between");
        printWriter.print("=");
        printWriter.println(this.SERVICE_MIN_RESTART_TIME_BETWEEN);
        printWriter.print("  ");
        printWriter.print("service_max_inactivity");
        printWriter.print("=");
        printWriter.println(this.MAX_SERVICE_INACTIVITY);
        printWriter.print("  ");
        printWriter.print("service_bg_start_timeout");
        printWriter.print("=");
        printWriter.println(this.BG_START_TIMEOUT);
        printWriter.print("  ");
        printWriter.print("service_bg_activity_start_timeout");
        printWriter.print("=");
        printWriter.println(this.SERVICE_BG_ACTIVITY_START_TIMEOUT);
        printWriter.print("  ");
        printWriter.print("service_crash_restart_duration");
        printWriter.print("=");
        printWriter.println(this.BOUND_SERVICE_CRASH_RESTART_DURATION);
        printWriter.print("  ");
        printWriter.print("service_crash_max_retry");
        printWriter.print("=");
        printWriter.println(this.BOUND_SERVICE_MAX_CRASH_RETRY);
        printWriter.print("  ");
        printWriter.print("process_start_async");
        printWriter.print("=");
        printWriter.println(this.FLAG_PROCESS_START_ASYNC);
        printWriter.print("  ");
        printWriter.print("memory_info_throttle_time");
        printWriter.print("=");
        printWriter.println(this.MEMORY_INFO_THROTTLE_TIME);
        printWriter.print("  ");
        printWriter.print("top_to_fgs_grace_duration");
        printWriter.print("=");
        printWriter.println(this.TOP_TO_FGS_GRACE_DURATION);
        printWriter.print("  ");
        printWriter.print("top_to_almost_perceptible_grace_duration");
        printWriter.print("=");
        printWriter.println(this.TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION);
        printWriter.print("  ");
        printWriter.print("min_crash_interval");
        printWriter.print("=");
        printWriter.println(MIN_CRASH_INTERVAL);
        printWriter.print("  ");
        printWriter.print("process_crash_count_reset_interval");
        printWriter.print("=");
        printWriter.println(PROCESS_CRASH_COUNT_RESET_INTERVAL);
        printWriter.print("  ");
        printWriter.print("process_crash_count_limit");
        printWriter.print("=");
        printWriter.println(PROCESS_CRASH_COUNT_LIMIT);
        printWriter.print("  ");
        printWriter.print("imperceptible_kill_exempt_proc_states");
        printWriter.print("=");
        printWriter.println(Arrays.toString(this.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.toArray()));
        printWriter.print("  ");
        printWriter.print("imperceptible_kill_exempt_packages");
        printWriter.print("=");
        printWriter.println(Arrays.toString(this.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.toArray()));
        printWriter.print("  ");
        printWriter.print("min_assoc_log_duration");
        printWriter.print("=");
        printWriter.println(MIN_ASSOC_LOG_DURATION);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_watcher_enabled");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_WATCHER_ENABLED);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_watcher_batchsize");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_WATCHER_BATCHSIZE);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_watcher_threshold");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_WATCHER_THRESHOLD);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_auto_sampler_enabled");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_AUTO_SAMPLER_ENABLED);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_auto_sampler_batchsize");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_AUTO_SAMPLER_BATCHSIZE);
        printWriter.print("  ");
        printWriter.print("binder_heavy_hitter_auto_sampler_threshold");
        printWriter.print("=");
        printWriter.println(BINDER_HEAVY_HITTER_AUTO_SAMPLER_THRESHOLD);
        printWriter.print("  ");
        printWriter.print("max_phantom_processes");
        printWriter.print("=");
        printWriter.println(this.MAX_PHANTOM_PROCESSES);
        printWriter.print("  ");
        printWriter.print("boot_time_temp_allowlist_duration");
        printWriter.print("=");
        printWriter.println(this.mBootTimeTempAllowlistDuration);
        printWriter.print("  ");
        printWriter.print("fg_to_bg_fgs_grace_duration");
        printWriter.print("=");
        printWriter.println(this.mFgToBgFgsGraceDuration);
        printWriter.print("  ");
        printWriter.print("fgs_start_foreground_timeout");
        printWriter.print("=");
        printWriter.println(this.mFgsStartForegroundTimeoutMs);
        printWriter.print("  ");
        printWriter.print("enable_app_start_info");
        printWriter.print("=");
        printWriter.println(this.mFlagApplicationStartInfoEnabled);
        printWriter.print("  ");
        printWriter.print("default_background_activity_starts_enabled");
        printWriter.print("=");
        printWriter.println(this.mFlagBackgroundActivityStartsEnabled);
        printWriter.print("  ");
        printWriter.print("default_background_fgs_starts_restriction_enabled");
        printWriter.print("=");
        printWriter.println(this.mFlagBackgroundFgsStartRestrictionEnabled);
        printWriter.print("  ");
        printWriter.print("default_fgs_starts_restriction_enabled");
        printWriter.print("=");
        printWriter.println(this.mFlagFgsStartRestrictionEnabled);
        printWriter.print("  ");
        printWriter.print("default_fgs_starts_restriction_notification_enabled");
        printWriter.print("=");
        printWriter.println(this.mFgsStartRestrictionNotificationEnabled);
        printWriter.print("  ");
        printWriter.print("default_fgs_starts_restriction_check_caller_target_sdk");
        printWriter.print("=");
        printWriter.println(this.mFgsStartRestrictionCheckCallerTargetSdk);
        printWriter.print("  ");
        printWriter.print("fgs_atom_sample_rate");
        printWriter.print("=");
        printWriter.println(this.mFgsAtomSampleRate);
        printWriter.print("  ");
        printWriter.print("fgs_start_allowed_log_sample_rate");
        printWriter.print("=");
        printWriter.println(this.mFgsStartAllowedLogSampleRate);
        printWriter.print("  ");
        printWriter.print("fgs_start_denied_log_sample_rate");
        printWriter.print("=");
        printWriter.println(this.mFgsStartDeniedLogSampleRate);
        printWriter.print("  ");
        printWriter.print("push_messaging_over_quota_behavior");
        printWriter.print("=");
        printWriter.println(this.mPushMessagingOverQuotaBehavior);
        printWriter.print("  ");
        printWriter.print("fgs_allow_opt_out");
        printWriter.print("=");
        printWriter.println(this.mFgsAllowOptOut);
        printWriter.print("  ");
        printWriter.print("enable_experimental_component_alias");
        printWriter.print("=");
        printWriter.println(this.mEnableComponentAlias);
        printWriter.print("  ");
        printWriter.print("component_alias_overrides");
        printWriter.print("=");
        printWriter.println(this.mComponentAliasOverrides);
        printWriter.print("  ");
        printWriter.print("defer_boot_completed_broadcast");
        printWriter.print("=");
        printWriter.println(this.mDeferBootCompletedBroadcast);
        printWriter.print("  ");
        printWriter.print("prioritize_alarm_broadcasts");
        printWriter.print("=");
        printWriter.println(this.mPrioritizeAlarmBroadcasts);
        printWriter.print("  ");
        printWriter.print("no_kill_cached_processes_until_boot_completed");
        printWriter.print("=");
        printWriter.println(this.mNoKillCachedProcessesUntilBootCompleted);
        printWriter.print("  ");
        printWriter.print("no_kill_cached_processes_post_boot_completed_duration_millis");
        printWriter.print("=");
        printWriter.println(this.mNoKillCachedProcessesPostBootCompletedDurationMillis);
        printWriter.print("  ");
        printWriter.print("max_empty_time_millis");
        printWriter.print("=");
        printWriter.println(this.mMaxEmptyTimeMillis);
        printWriter.print("  ");
        printWriter.print("service_start_foreground_timeout_ms");
        printWriter.print("=");
        printWriter.println(this.mServiceStartForegroundTimeoutMs);
        printWriter.print("  ");
        printWriter.print("service_start_foreground_anr_delay_ms");
        printWriter.print("=");
        printWriter.println(this.mServiceStartForegroundAnrDelayMs);
        printWriter.print("  ");
        printWriter.print("service_bind_almost_perceptible_timeout_ms");
        printWriter.print("=");
        printWriter.println(this.mServiceBindAlmostPerceptibleTimeoutMs);
        printWriter.print("  ");
        printWriter.print("network_access_timeout_ms");
        printWriter.print("=");
        printWriter.println(this.mNetworkAccessTimeoutMs);
        printWriter.print("  ");
        printWriter.print("max_service_connections_per_process");
        printWriter.print("=");
        printWriter.println(this.mMaxServiceConnectionsPerProcess);
        printWriter.print("  ");
        printWriter.print("proactive_kills_enabled");
        printWriter.print("=");
        printWriter.println(PROACTIVE_KILLS_ENABLED);
        printWriter.print("  ");
        printWriter.print("low_swap_threshold_percent");
        printWriter.print("=");
        printWriter.println(LOW_SWAP_THRESHOLD_PERCENT);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notifications_enabled");
        printWriter.print("=");
        printWriter.println(this.mFlagFgsNotificationDeferralEnabled);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notifications_api_gated");
        printWriter.print("=");
        printWriter.println(this.mFlagFgsNotificationDeferralApiGated);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notification_interval");
        printWriter.print("=");
        printWriter.println(this.mFgsNotificationDeferralInterval);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notification_interval_for_short");
        printWriter.print("=");
        printWriter.println(this.mFgsNotificationDeferralIntervalForShort);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notification_exclusion_time");
        printWriter.print("=");
        printWriter.println(this.mFgsNotificationDeferralExclusionTime);
        printWriter.print("  ");
        printWriter.print("deferred_fgs_notification_exclusion_time_for_short");
        printWriter.print("=");
        printWriter.println(this.mFgsNotificationDeferralExclusionTimeForShort);
        printWriter.print("  ");
        printWriter.print("system_exempt_power_restrictions_enabled");
        printWriter.print("=");
        printWriter.println(this.mFlagSystemExemptPowerRestrictionsEnabled);
        printWriter.print("  ");
        printWriter.print("short_fgs_timeout_duration");
        printWriter.print("=");
        printWriter.println(this.mShortFgsTimeoutDuration);
        printWriter.print("  ");
        printWriter.print("short_fgs_proc_state_extra_wait_duration");
        printWriter.print("=");
        printWriter.println(this.mShortFgsProcStateExtraWaitDuration);
        printWriter.print("  ");
        printWriter.print("short_fgs_anr_extra_wait_duration");
        printWriter.print("=");
        printWriter.println(this.mShortFgsAnrExtraWaitDuration);
        printWriter.print("  ");
        printWriter.print("use_tiered_cached_adj");
        printWriter.print("=");
        printWriter.println(this.USE_TIERED_CACHED_ADJ);
        printWriter.print("  ");
        printWriter.print("tiered_cached_adj_decay_time");
        printWriter.print("=");
        printWriter.println(this.TIERED_CACHED_ADJ_DECAY_TIME);
        printWriter.println();
        if (this.mOverrideMaxCachedProcesses >= 0) {
            printWriter.print("  mOverrideMaxCachedProcesses=");
            printWriter.println(this.mOverrideMaxCachedProcesses);
        }
        printWriter.print("  mCustomizedMaxCachedProcesses=");
        printWriter.println(this.mCustomizedMaxCachedProcesses);
        printWriter.print("  CUR_MAX_CACHED_PROCESSES=");
        printWriter.println(this.CUR_MAX_CACHED_PROCESSES);
        printWriter.print("  CUR_MAX_EMPTY_PROCESSES=");
        printWriter.println(this.CUR_MAX_EMPTY_PROCESSES);
        printWriter.print("  CUR_TRIM_EMPTY_PROCESSES=");
        printWriter.println(this.CUR_TRIM_EMPTY_PROCESSES);
        printWriter.print("  CUR_TRIM_CACHED_PROCESSES=");
        printWriter.println(this.CUR_TRIM_CACHED_PROCESSES);
        printWriter.print("  OOMADJ_UPDATE_QUICK=");
        printWriter.println(this.OOMADJ_UPDATE_QUICK);
        printWriter.print("  ENABLE_WAIT_FOR_FINISH_ATTACH_APPLICATION=");
        printWriter.println(this.mEnableWaitForFinishAttachApplication);
    }
}
