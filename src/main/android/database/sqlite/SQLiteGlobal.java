package android.database.sqlite;

import android.content.res.Resources;
import android.p008os.StatFs;
import android.p008os.SystemProperties;
import com.android.internal.C4057R;
/* loaded from: classes.dex */
public final class SQLiteGlobal {
    public static final String SYNC_MODE_FULL = "FULL";
    private static final String TAG = "SQLiteGlobal";
    static final String WIPE_CHECK_FILE_SUFFIX = "-wipecheck";
    private static int sDefaultPageSize;
    public static volatile String sDefaultSyncMode;
    private static final Object sLock = new Object();

    private static native int nativeReleaseMemory();

    private SQLiteGlobal() {
    }

    public static int releaseMemory() {
        return nativeReleaseMemory();
    }

    public static int getDefaultPageSize() {
        int i;
        synchronized (sLock) {
            if (sDefaultPageSize == 0) {
                sDefaultPageSize = new StatFs("/data").getBlockSize();
            }
            i = SystemProperties.getInt("debug.sqlite.pagesize", sDefaultPageSize);
        }
        return i;
    }

    public static String getDefaultJournalMode() {
        return SystemProperties.get("debug.sqlite.journalmode", Resources.getSystem().getString(C4057R.string.db_default_journal_mode));
    }

    public static int getJournalSizeLimit() {
        return SystemProperties.getInt("debug.sqlite.journalsizelimit", Resources.getSystem().getInteger(C4057R.integer.db_journal_size_limit));
    }

    public static String getDefaultSyncMode() {
        String defaultMode = sDefaultSyncMode;
        if (defaultMode != null) {
            return defaultMode;
        }
        return SystemProperties.get("debug.sqlite.syncmode", Resources.getSystem().getString(C4057R.string.db_default_sync_mode));
    }

    public static String getWALSyncMode() {
        String defaultMode = sDefaultSyncMode;
        if (defaultMode != null) {
            return defaultMode;
        }
        return SystemProperties.get("debug.sqlite.wal.syncmode", Resources.getSystem().getString(C4057R.string.db_wal_sync_mode));
    }

    public static int getWALAutoCheckpoint() {
        int value = SystemProperties.getInt("debug.sqlite.wal.autocheckpoint", Resources.getSystem().getInteger(C4057R.integer.db_wal_autocheckpoint));
        return Math.max(1, value);
    }

    public static int getWALConnectionPoolSize() {
        int value = SystemProperties.getInt("debug.sqlite.wal.poolsize", Resources.getSystem().getInteger(C4057R.integer.db_connection_pool_size));
        return Math.max(2, value);
    }

    public static int getIdleConnectionTimeout() {
        return SystemProperties.getInt("debug.sqlite.idle_connection_timeout", Resources.getSystem().getInteger(C4057R.integer.db_default_idle_connection_timeout));
    }

    public static long getWALTruncateSize() {
        long setting = SQLiteCompatibilityWalFlags.getTruncateSize();
        if (setting >= 0) {
            return setting;
        }
        return SystemProperties.getInt("debug.sqlite.wal.truncatesize", Resources.getSystem().getInteger(C4057R.integer.db_wal_truncate_size));
    }

    public static boolean checkDbWipe() {
        return false;
    }
}
