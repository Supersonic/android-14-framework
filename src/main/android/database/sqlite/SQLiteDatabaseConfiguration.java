package android.database.sqlite;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class SQLiteDatabaseConfiguration {
    private static final Pattern EMAIL_IN_DB_PATTERN = Pattern.compile("[\\w\\.\\-]+@[\\w\\.\\-]+");
    public static final String MEMORY_DB_PATH = ":memory:";
    public boolean foreignKeyConstraintsEnabled;
    public String journalMode;
    public final String label;
    public Locale locale;
    public int maxSqlCacheSize;
    public int openFlags;
    public final String path;
    public boolean shouldTruncateWalFile;
    public String syncMode;
    public final ArrayMap<String, UnaryOperator<String>> customScalarFunctions = new ArrayMap<>();
    public final ArrayMap<String, BinaryOperator<String>> customAggregateFunctions = new ArrayMap<>();
    public final ArrayList<Pair<String, Object[]>> perConnectionSql = new ArrayList<>();
    public int lookasideSlotSize = -1;
    public int lookasideSlotCount = -1;
    public long idleConnectionTimeoutMs = Long.MAX_VALUE;

    public SQLiteDatabaseConfiguration(String path, int openFlags) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null.");
        }
        this.path = path;
        this.label = stripPathForLogs(path);
        this.openFlags = openFlags;
        this.maxSqlCacheSize = 25;
        this.locale = Locale.getDefault();
    }

    public SQLiteDatabaseConfiguration(SQLiteDatabaseConfiguration other) {
        if (other == null) {
            throw new IllegalArgumentException("other must not be null.");
        }
        this.path = other.path;
        this.label = other.label;
        updateParametersFrom(other);
    }

    public void updateParametersFrom(SQLiteDatabaseConfiguration other) {
        if (other == null) {
            throw new IllegalArgumentException("other must not be null.");
        }
        if (!this.path.equals(other.path)) {
            throw new IllegalArgumentException("other configuration must refer to the same database.");
        }
        this.openFlags = other.openFlags;
        this.maxSqlCacheSize = other.maxSqlCacheSize;
        this.locale = other.locale;
        this.foreignKeyConstraintsEnabled = other.foreignKeyConstraintsEnabled;
        this.customScalarFunctions.clear();
        this.customScalarFunctions.putAll((ArrayMap<? extends String, ? extends UnaryOperator<String>>) other.customScalarFunctions);
        this.customAggregateFunctions.clear();
        this.customAggregateFunctions.putAll((ArrayMap<? extends String, ? extends BinaryOperator<String>>) other.customAggregateFunctions);
        this.perConnectionSql.clear();
        this.perConnectionSql.addAll(other.perConnectionSql);
        this.lookasideSlotSize = other.lookasideSlotSize;
        this.lookasideSlotCount = other.lookasideSlotCount;
        this.idleConnectionTimeoutMs = other.idleConnectionTimeoutMs;
        this.journalMode = other.journalMode;
        this.syncMode = other.syncMode;
    }

    public boolean isInMemoryDb() {
        return this.path.equalsIgnoreCase(MEMORY_DB_PATH);
    }

    public boolean isReadOnlyDatabase() {
        return (this.openFlags & 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLegacyCompatibilityWalEnabled() {
        return this.journalMode == null && this.syncMode == null && (this.openFlags & Integer.MIN_VALUE) != 0;
    }

    private static String stripPathForLogs(String path) {
        if (path.indexOf(64) == -1) {
            return path;
        }
        return EMAIL_IN_DB_PATTERN.matcher(path).replaceAll("XX@YY");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLookasideConfigSet() {
        return this.lookasideSlotCount >= 0 && this.lookasideSlotSize >= 0;
    }

    public String resolveJournalMode() {
        if (isReadOnlyDatabase()) {
            return "";
        }
        if (isInMemoryDb()) {
            String str = this.journalMode;
            if (str != null && str.equalsIgnoreCase("OFF")) {
                return "OFF";
            }
            return SQLiteDatabase.JOURNAL_MODE_MEMORY;
        }
        this.shouldTruncateWalFile = false;
        if (isWalEnabledInternal()) {
            this.shouldTruncateWalFile = true;
            return SQLiteDatabase.JOURNAL_MODE_WAL;
        }
        String str2 = this.journalMode;
        if (str2 != null) {
            return str2;
        }
        return SQLiteGlobal.getDefaultJournalMode();
    }

    public String resolveSyncMode() {
        if (isReadOnlyDatabase() || isInMemoryDb()) {
            return "";
        }
        if (!TextUtils.isEmpty(this.syncMode)) {
            return this.syncMode;
        }
        if (isWalEnabledInternal()) {
            if (isLegacyCompatibilityWalEnabled()) {
                return SQLiteCompatibilityWalFlags.getWALSyncMode();
            }
            return SQLiteGlobal.getWALSyncMode();
        }
        return SQLiteGlobal.getDefaultSyncMode();
    }

    private boolean isWalEnabledInternal() {
        boolean walEnabled = (this.openFlags & 536870912) != 0;
        boolean isCompatibilityWalEnabled = isLegacyCompatibilityWalEnabled();
        if (walEnabled || isCompatibilityWalEnabled) {
            return true;
        }
        String str = this.journalMode;
        return str != null && str.equalsIgnoreCase(SQLiteDatabase.JOURNAL_MODE_WAL);
    }
}
