package com.android.server.p006am;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.IndentingPrintWriter;
import android.util.KeyValueListParser;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.util.FrameworkStatsLog;
import dalvik.annotation.optimization.NeverCompile;
/* renamed from: com.android.server.am.BroadcastConstants */
/* loaded from: classes.dex */
public class BroadcastConstants {
    public static final long DEFAULT_ALLOW_BG_ACTIVITY_START_TIMEOUT;
    public static final long DEFAULT_DEFERRAL;
    public static final int DEFAULT_MAX_HISTORY_COMPLETE_SIZE;
    public static final int DEFAULT_MAX_HISTORY_SUMMARY_SIZE;
    public static final int DEFAULT_MAX_PENDING_BROADCASTS;
    public static final int DEFAULT_MAX_RUNNING_ACTIVE_BROADCASTS;
    public static final int DEFAULT_MAX_RUNNING_PROCESS_QUEUES;
    public static final long DEFAULT_SLOW_TIME;
    public static final long DEFAULT_TIMEOUT;
    public ContentResolver mResolver;
    public String mSettingsKey;
    public SettingsObserver mSettingsObserver;
    public long TIMEOUT = DEFAULT_TIMEOUT;
    public long SLOW_TIME = DEFAULT_SLOW_TIME;
    public long DEFERRAL = DEFAULT_DEFERRAL;
    public float DEFERRAL_DECAY_FACTOR = 0.75f;
    public long DEFERRAL_FLOOR = 0;
    public long ALLOW_BG_ACTIVITY_START_TIMEOUT = DEFAULT_ALLOW_BG_ACTIVITY_START_TIMEOUT;
    public boolean MODERN_QUEUE_ENABLED = true;
    public int MAX_RUNNING_PROCESS_QUEUES = DEFAULT_MAX_RUNNING_PROCESS_QUEUES;
    public int EXTRA_RUNNING_URGENT_PROCESS_QUEUES = 1;
    public int MAX_CONSECUTIVE_URGENT_DISPATCHES = 3;
    public int MAX_CONSECUTIVE_NORMAL_DISPATCHES = 10;
    public int MAX_RUNNING_ACTIVE_BROADCASTS = DEFAULT_MAX_RUNNING_ACTIVE_BROADCASTS;
    public int MAX_PENDING_BROADCASTS = DEFAULT_MAX_PENDING_BROADCASTS;
    public long DELAY_NORMAL_MILLIS = 500;
    public long DELAY_CACHED_MILLIS = 120000;
    public long DELAY_URGENT_MILLIS = -120000;
    public int MAX_HISTORY_COMPLETE_SIZE = DEFAULT_MAX_HISTORY_COMPLETE_SIZE;
    public int MAX_HISTORY_SUMMARY_SIZE = DEFAULT_MAX_HISTORY_SUMMARY_SIZE;
    public final KeyValueListParser mParser = new KeyValueListParser(',');

    static {
        int i = Build.HW_TIMEOUT_MULTIPLIER;
        DEFAULT_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        DEFAULT_SLOW_TIME = i * 5000;
        DEFAULT_DEFERRAL = i * 5000;
        DEFAULT_ALLOW_BG_ACTIVITY_START_TIMEOUT = i * FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        DEFAULT_MAX_RUNNING_PROCESS_QUEUES = ActivityManager.isLowRamDeviceStatic() ? 2 : 4;
        DEFAULT_MAX_RUNNING_ACTIVE_BROADCASTS = ActivityManager.isLowRamDeviceStatic() ? 8 : 16;
        DEFAULT_MAX_PENDING_BROADCASTS = ActivityManager.isLowRamDeviceStatic() ? 128 : 256;
        DEFAULT_MAX_HISTORY_COMPLETE_SIZE = ActivityManager.isLowRamDeviceStatic() ? 64 : 256;
        DEFAULT_MAX_HISTORY_SUMMARY_SIZE = ActivityManager.isLowRamDeviceStatic() ? 256 : 1024;
    }

    /* renamed from: com.android.server.am.BroadcastConstants$SettingsObserver */
    /* loaded from: classes.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            BroadcastConstants.this.updateSettingsConstants();
        }
    }

    public BroadcastConstants(String str) {
        this.mSettingsKey = str;
        updateDeviceConfigConstants();
    }

    public void startObserving(Handler handler, ContentResolver contentResolver) {
        this.mResolver = contentResolver;
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mResolver.registerContentObserver(Settings.Global.getUriFor(this.mSettingsKey), false, this.mSettingsObserver);
        updateSettingsConstants();
        DeviceConfig.addOnPropertiesChangedListener("activity_manager_native_boot", new HandlerExecutor(handler), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.am.BroadcastConstants$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                BroadcastConstants.this.updateDeviceConfigConstants(properties);
            }
        });
        updateDeviceConfigConstants();
    }

    public int getMaxRunningQueues() {
        return this.MAX_RUNNING_PROCESS_QUEUES + this.EXTRA_RUNNING_URGENT_PROCESS_QUEUES;
    }

    public final void updateSettingsConstants() {
        synchronized (this) {
            try {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, this.mSettingsKey));
                    this.TIMEOUT = this.mParser.getLong("bcast_timeout", this.TIMEOUT);
                    this.SLOW_TIME = this.mParser.getLong("bcast_slow_time", this.SLOW_TIME);
                    this.DEFERRAL = this.mParser.getLong("bcast_deferral", this.DEFERRAL);
                    this.DEFERRAL_DECAY_FACTOR = this.mParser.getFloat("bcast_deferral_decay_factor", this.DEFERRAL_DECAY_FACTOR);
                    this.DEFERRAL_FLOOR = this.mParser.getLong("bcast_deferral_floor", this.DEFERRAL_FLOOR);
                    this.ALLOW_BG_ACTIVITY_START_TIMEOUT = this.mParser.getLong("bcast_allow_bg_activity_start_timeout", this.ALLOW_BG_ACTIVITY_START_TIMEOUT);
                } catch (IllegalArgumentException e) {
                    Slog.e("BroadcastConstants", "Bad broadcast settings in key '" + this.mSettingsKey + "'", e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final String propertyFor(String str) {
        return "persist.device_config.activity_manager_native_boot." + str;
    }

    public final String propertyOverrideFor(String str) {
        return "persist.sys.activity_manager_native_boot." + str;
    }

    public final boolean getDeviceConfigBoolean(String str, boolean z) {
        return SystemProperties.getBoolean(propertyOverrideFor(str), SystemProperties.getBoolean(propertyFor(str), z));
    }

    public final int getDeviceConfigInt(String str, int i) {
        return SystemProperties.getInt(propertyOverrideFor(str), SystemProperties.getInt(propertyFor(str), i));
    }

    public final long getDeviceConfigLong(String str, long j) {
        return SystemProperties.getLong(propertyOverrideFor(str), SystemProperties.getLong(propertyFor(str), j));
    }

    public final void updateDeviceConfigConstants(DeviceConfig.Properties properties) {
        updateDeviceConfigConstants();
    }

    public final void updateDeviceConfigConstants() {
        synchronized (this) {
            this.MODERN_QUEUE_ENABLED = getDeviceConfigBoolean("modern_queue_enabled", true);
            this.MAX_RUNNING_PROCESS_QUEUES = getDeviceConfigInt("bcast_max_running_process_queues", DEFAULT_MAX_RUNNING_PROCESS_QUEUES);
            this.EXTRA_RUNNING_URGENT_PROCESS_QUEUES = getDeviceConfigInt("bcast_extra_running_urgent_process_queues", 1);
            this.MAX_CONSECUTIVE_URGENT_DISPATCHES = getDeviceConfigInt("bcast_max_consecutive_urgent_dispatches", 3);
            this.MAX_CONSECUTIVE_NORMAL_DISPATCHES = getDeviceConfigInt("bcast_max_consecutive_normal_dispatches", 10);
            this.MAX_RUNNING_ACTIVE_BROADCASTS = getDeviceConfigInt("bcast_max_running_active_broadcasts", DEFAULT_MAX_RUNNING_ACTIVE_BROADCASTS);
            this.MAX_PENDING_BROADCASTS = getDeviceConfigInt("bcast_max_pending_broadcasts", DEFAULT_MAX_PENDING_BROADCASTS);
            this.DELAY_NORMAL_MILLIS = getDeviceConfigLong("bcast_delay_normal_millis", 500L);
            this.DELAY_CACHED_MILLIS = getDeviceConfigLong("bcast_delay_cached_millis", 120000L);
            this.DELAY_URGENT_MILLIS = getDeviceConfigLong("bcast_delay_urgent_millis", -120000L);
            this.MAX_HISTORY_COMPLETE_SIZE = getDeviceConfigInt("bcast_max_history_complete_size", DEFAULT_MAX_HISTORY_COMPLETE_SIZE);
            this.MAX_HISTORY_SUMMARY_SIZE = getDeviceConfigInt("bcast_max_history_summary_size", DEFAULT_MAX_HISTORY_SUMMARY_SIZE);
        }
    }

    @NeverCompile
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this) {
            indentingPrintWriter.print("Broadcast parameters (key=");
            indentingPrintWriter.print(this.mSettingsKey);
            indentingPrintWriter.print(", observing=");
            indentingPrintWriter.print(this.mSettingsObserver != null);
            indentingPrintWriter.println("):");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("bcast_timeout", TimeUtils.formatDuration(this.TIMEOUT)).println();
            indentingPrintWriter.print("bcast_slow_time", TimeUtils.formatDuration(this.SLOW_TIME)).println();
            indentingPrintWriter.print("bcast_deferral", TimeUtils.formatDuration(this.DEFERRAL)).println();
            indentingPrintWriter.print("bcast_deferral_decay_factor", Float.valueOf(this.DEFERRAL_DECAY_FACTOR)).println();
            indentingPrintWriter.print("bcast_deferral_floor", Long.valueOf(this.DEFERRAL_FLOOR)).println();
            indentingPrintWriter.print("bcast_allow_bg_activity_start_timeout", TimeUtils.formatDuration(this.ALLOW_BG_ACTIVITY_START_TIMEOUT)).println();
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Broadcast parameters (namespace=");
            indentingPrintWriter.print("activity_manager_native_boot");
            indentingPrintWriter.println("):");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("modern_queue_enabled", Boolean.valueOf(this.MODERN_QUEUE_ENABLED)).println();
            indentingPrintWriter.print("bcast_max_running_process_queues", Integer.valueOf(this.MAX_RUNNING_PROCESS_QUEUES)).println();
            indentingPrintWriter.print("bcast_max_running_active_broadcasts", Integer.valueOf(this.MAX_RUNNING_ACTIVE_BROADCASTS)).println();
            indentingPrintWriter.print("bcast_max_pending_broadcasts", Integer.valueOf(this.MAX_PENDING_BROADCASTS)).println();
            indentingPrintWriter.print("bcast_delay_normal_millis", TimeUtils.formatDuration(this.DELAY_NORMAL_MILLIS)).println();
            indentingPrintWriter.print("bcast_delay_cached_millis", TimeUtils.formatDuration(this.DELAY_CACHED_MILLIS)).println();
            indentingPrintWriter.print("bcast_delay_urgent_millis", TimeUtils.formatDuration(this.DELAY_URGENT_MILLIS)).println();
            indentingPrintWriter.print("bcast_max_history_complete_size", Integer.valueOf(this.MAX_HISTORY_COMPLETE_SIZE)).println();
            indentingPrintWriter.print("bcast_max_history_summary_size", Integer.valueOf(this.MAX_HISTORY_SUMMARY_SIZE)).println();
            indentingPrintWriter.print("bcast_max_consecutive_urgent_dispatches", Integer.valueOf(this.MAX_CONSECUTIVE_URGENT_DISPATCHES)).println();
            indentingPrintWriter.print("bcast_max_consecutive_normal_dispatches", Integer.valueOf(this.MAX_CONSECUTIVE_NORMAL_DISPATCHES)).println();
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
        }
    }
}
