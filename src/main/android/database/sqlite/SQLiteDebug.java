package android.database.sqlite;

import android.p008os.Build;
import android.p008os.Process;
import android.p008os.SystemProperties;
import android.util.Log;
import android.util.Printer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class SQLiteDebug {

    /* loaded from: classes.dex */
    public static class PagerStats {
        public ArrayList<DbStats> dbStats;
        public int largestMemAlloc;
        public int memoryUsed;
        public int pageCacheOverflow;
    }

    private static native void nativeGetPagerStats(PagerStats pagerStats);

    /* loaded from: classes.dex */
    public static final class NoPreloadHolder {
        public static final boolean DEBUG_LOG_DETAILED;
        private static final String SLOW_QUERY_THRESHOLD_PROP = "db.log.slow_query_threshold";
        public static final boolean DEBUG_SQL_LOG = Log.isLoggable("SQLiteLog", 2);
        public static final boolean DEBUG_SQL_STATEMENTS = Log.isLoggable("SQLiteStatements", 2);
        public static final boolean DEBUG_SQL_TIME = Log.isLoggable("SQLiteTime", 2);
        public static final boolean DEBUG_LOG_SLOW_QUERIES = Log.isLoggable("SQLiteSlowQueries", 2);
        private static final String SLOW_QUERY_THRESHOLD_UID_PROP = "db.log.slow_query_threshold." + Process.myUid();

        static {
            boolean z = false;
            if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("db.log.detailed", false)) {
                z = true;
            }
            DEBUG_LOG_DETAILED = z;
        }
    }

    private SQLiteDebug() {
    }

    public static boolean shouldLogSlowQuery(long elapsedTimeMillis) {
        int slowQueryMillis = Math.min(SystemProperties.getInt("db.log.slow_query_threshold", Integer.MAX_VALUE), SystemProperties.getInt(NoPreloadHolder.SLOW_QUERY_THRESHOLD_UID_PROP, Integer.MAX_VALUE));
        return elapsedTimeMillis >= ((long) slowQueryMillis);
    }

    /* loaded from: classes.dex */
    public static class DbStats {
        public final boolean arePoolStats;
        public final int cacheHits;
        public final int cacheMisses;
        public final int cacheSize;
        public String dbName;
        public long dbSize;
        public int lookaside;
        public long pageSize;

        public DbStats(String dbName, long pageCount, long pageSize, int lookaside, int hits, int misses, int cachesize, boolean arePoolStats) {
            this.dbName = dbName;
            this.pageSize = pageSize / 1024;
            this.dbSize = (pageCount * pageSize) / 1024;
            this.lookaside = lookaside;
            this.cacheHits = hits;
            this.cacheMisses = misses;
            this.cacheSize = cachesize;
            this.arePoolStats = arePoolStats;
        }
    }

    public static PagerStats getDatabaseInfo() {
        PagerStats stats = new PagerStats();
        nativeGetPagerStats(stats);
        stats.dbStats = SQLiteDatabase.getDbStats();
        return stats;
    }

    public static void dump(Printer printer, String[] args) {
        dump(printer, args, false);
    }

    public static void dump(Printer printer, String[] args, boolean isSystem) {
        boolean verbose = false;
        for (String arg : args) {
            if (arg.equals("-v")) {
                verbose = true;
            }
        }
        SQLiteDatabase.dumpAll(printer, verbose, isSystem);
    }
}
