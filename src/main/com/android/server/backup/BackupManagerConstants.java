package com.android.server.backup;

import android.content.ContentResolver;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.KeyValueSettingObserver;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
/* loaded from: classes.dex */
public class BackupManagerConstants extends KeyValueSettingObserver {
    @VisibleForTesting
    public static final String BACKUP_FINISHED_NOTIFICATION_RECEIVERS = "backup_finished_notification_receivers";
    @VisibleForTesting
    public static final String DEFAULT_BACKUP_FINISHED_NOTIFICATION_RECEIVERS = "";
    @VisibleForTesting
    public static final long DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS = 86400000;
    @VisibleForTesting
    public static final int DEFAULT_FULL_BACKUP_REQUIRED_NETWORK_TYPE = 2;
    @VisibleForTesting
    public static final boolean DEFAULT_FULL_BACKUP_REQUIRE_CHARGING = true;
    @VisibleForTesting
    public static final long DEFAULT_KEY_VALUE_BACKUP_FUZZ_MILLISECONDS = 600000;
    @VisibleForTesting
    public static final long DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS = 14400000;
    @VisibleForTesting
    public static final int DEFAULT_KEY_VALUE_BACKUP_REQUIRED_NETWORK_TYPE = 1;
    @VisibleForTesting
    public static final boolean DEFAULT_KEY_VALUE_BACKUP_REQUIRE_CHARGING = true;
    @VisibleForTesting
    public static final String FULL_BACKUP_INTERVAL_MILLISECONDS = "full_backup_interval_milliseconds";
    @VisibleForTesting
    public static final String FULL_BACKUP_REQUIRED_NETWORK_TYPE = "full_backup_required_network_type";
    @VisibleForTesting
    public static final String FULL_BACKUP_REQUIRE_CHARGING = "full_backup_require_charging";
    @VisibleForTesting
    public static final String KEY_VALUE_BACKUP_FUZZ_MILLISECONDS = "key_value_backup_fuzz_milliseconds";
    @VisibleForTesting
    public static final String KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS = "key_value_backup_interval_milliseconds";
    @VisibleForTesting
    public static final String KEY_VALUE_BACKUP_REQUIRED_NETWORK_TYPE = "key_value_backup_required_network_type";
    @VisibleForTesting
    public static final String KEY_VALUE_BACKUP_REQUIRE_CHARGING = "key_value_backup_require_charging";
    public String[] mBackupFinishedNotificationReceivers;
    public long mFullBackupIntervalMilliseconds;
    public boolean mFullBackupRequireCharging;
    public int mFullBackupRequiredNetworkType;
    public long mKeyValueBackupFuzzMilliseconds;
    public long mKeyValueBackupIntervalMilliseconds;
    public boolean mKeyValueBackupRequireCharging;
    public int mKeyValueBackupRequiredNetworkType;

    public BackupManagerConstants(Handler handler, ContentResolver contentResolver) {
        super(handler, contentResolver, Settings.Secure.getUriFor("backup_manager_constants"));
    }

    public String getSettingValue(ContentResolver contentResolver) {
        return Settings.Secure.getStringForUser(contentResolver, "backup_manager_constants", contentResolver.getUserId());
    }

    public synchronized void update(KeyValueListParser keyValueListParser) {
        this.mKeyValueBackupIntervalMilliseconds = keyValueListParser.getLong(KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS, (long) DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        this.mKeyValueBackupFuzzMilliseconds = keyValueListParser.getLong(KEY_VALUE_BACKUP_FUZZ_MILLISECONDS, 600000L);
        this.mKeyValueBackupRequireCharging = keyValueListParser.getBoolean(KEY_VALUE_BACKUP_REQUIRE_CHARGING, true);
        this.mKeyValueBackupRequiredNetworkType = keyValueListParser.getInt(KEY_VALUE_BACKUP_REQUIRED_NETWORK_TYPE, 1);
        this.mFullBackupIntervalMilliseconds = keyValueListParser.getLong(FULL_BACKUP_INTERVAL_MILLISECONDS, (long) DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
        this.mFullBackupRequireCharging = keyValueListParser.getBoolean(FULL_BACKUP_REQUIRE_CHARGING, true);
        this.mFullBackupRequiredNetworkType = keyValueListParser.getInt(FULL_BACKUP_REQUIRED_NETWORK_TYPE, 2);
        String string = keyValueListParser.getString(BACKUP_FINISHED_NOTIFICATION_RECEIVERS, "");
        if (string.isEmpty()) {
            this.mBackupFinishedNotificationReceivers = new String[0];
        } else {
            this.mBackupFinishedNotificationReceivers = string.split(XmlUtils.STRING_ARRAY_SEPARATOR);
        }
    }

    public synchronized long getKeyValueBackupIntervalMilliseconds() {
        Slog.v("BackupManagerConstants", "getKeyValueBackupIntervalMilliseconds(...) returns " + this.mKeyValueBackupIntervalMilliseconds);
        return this.mKeyValueBackupIntervalMilliseconds;
    }

    public synchronized long getKeyValueBackupFuzzMilliseconds() {
        Slog.v("BackupManagerConstants", "getKeyValueBackupFuzzMilliseconds(...) returns " + this.mKeyValueBackupFuzzMilliseconds);
        return this.mKeyValueBackupFuzzMilliseconds;
    }

    public synchronized boolean getKeyValueBackupRequireCharging() {
        Slog.v("BackupManagerConstants", "getKeyValueBackupRequireCharging(...) returns " + this.mKeyValueBackupRequireCharging);
        return this.mKeyValueBackupRequireCharging;
    }

    public synchronized int getKeyValueBackupRequiredNetworkType() {
        Slog.v("BackupManagerConstants", "getKeyValueBackupRequiredNetworkType(...) returns " + this.mKeyValueBackupRequiredNetworkType);
        return this.mKeyValueBackupRequiredNetworkType;
    }

    public synchronized long getFullBackupIntervalMilliseconds() {
        Slog.v("BackupManagerConstants", "getFullBackupIntervalMilliseconds(...) returns " + this.mFullBackupIntervalMilliseconds);
        return this.mFullBackupIntervalMilliseconds;
    }

    public synchronized boolean getFullBackupRequireCharging() {
        Slog.v("BackupManagerConstants", "getFullBackupRequireCharging(...) returns " + this.mFullBackupRequireCharging);
        return this.mFullBackupRequireCharging;
    }

    public synchronized int getFullBackupRequiredNetworkType() {
        Slog.v("BackupManagerConstants", "getFullBackupRequiredNetworkType(...) returns " + this.mFullBackupRequiredNetworkType);
        return this.mFullBackupRequiredNetworkType;
    }

    public synchronized String[] getBackupFinishedNotificationReceivers() {
        Slog.v("BackupManagerConstants", "getBackupFinishedNotificationReceivers(...) returns " + TextUtils.join(", ", this.mBackupFinishedNotificationReceivers));
        return this.mBackupFinishedNotificationReceivers;
    }
}
