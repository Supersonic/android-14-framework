package android.database.sqlite;

import android.app.ActivityThread;
import android.app.Application;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.Log;
/* loaded from: classes.dex */
public class SQLiteCompatibilityWalFlags {
    private static final String TAG = "SQLiteCompatibilityWalFlags";
    private static volatile boolean sCallingGlobalSettings;
    private static volatile boolean sInitialized;
    private static volatile boolean sLegacyCompatibilityWalEnabled;
    private static volatile long sTruncateSize = -1;
    private static volatile String sWALSyncMode;

    private SQLiteCompatibilityWalFlags() {
    }

    public static boolean isLegacyCompatibilityWalEnabled() {
        initIfNeeded();
        return sLegacyCompatibilityWalEnabled;
    }

    public static String getWALSyncMode() {
        initIfNeeded();
        if (!sLegacyCompatibilityWalEnabled) {
            throw new IllegalStateException("isLegacyCompatibilityWalEnabled() == false");
        }
        return sWALSyncMode;
    }

    public static long getTruncateSize() {
        initIfNeeded();
        return sTruncateSize;
    }

    private static void initIfNeeded() {
        if (sInitialized || sCallingGlobalSettings) {
            return;
        }
        ActivityThread activityThread = ActivityThread.currentActivityThread();
        Application app = activityThread == null ? null : activityThread.getApplication();
        String flags = null;
        if (app == null) {
            Log.m104w(TAG, "Cannot read global setting sqlite_compatibility_wal_flags - Application state not available");
        } else {
            try {
                sCallingGlobalSettings = true;
                flags = Settings.Global.getString(app.getContentResolver(), Settings.Global.SQLITE_COMPATIBILITY_WAL_FLAGS);
            } finally {
                sCallingGlobalSettings = false;
            }
        }
        init(flags);
    }

    public static void init(String flags) {
        if (TextUtils.isEmpty(flags)) {
            sInitialized = true;
            return;
        }
        KeyValueListParser parser = new KeyValueListParser(',');
        try {
            parser.setString(flags);
            sLegacyCompatibilityWalEnabled = parser.getBoolean("legacy_compatibility_wal_enabled", false);
            sWALSyncMode = parser.getString("wal_syncmode", SQLiteGlobal.getWALSyncMode());
            sTruncateSize = parser.getInt("truncate_size", -1);
            Log.m108i(TAG, "Read compatibility WAL flags: legacy_compatibility_wal_enabled=" + sLegacyCompatibilityWalEnabled + ", wal_syncmode=" + sWALSyncMode);
            sInitialized = true;
        } catch (IllegalArgumentException e) {
            Log.m109e(TAG, "Setting has invalid format: " + flags, e);
            sInitialized = true;
        }
    }

    public static void reset() {
        sInitialized = false;
        sLegacyCompatibilityWalEnabled = false;
        sWALSyncMode = null;
    }
}
