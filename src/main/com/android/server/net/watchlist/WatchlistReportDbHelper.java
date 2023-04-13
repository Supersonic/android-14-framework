package com.android.server.net.watchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Slog;
import com.android.internal.util.HexDump;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes2.dex */
public class WatchlistReportDbHelper extends SQLiteOpenHelper {
    public static final String[] DIGEST_DOMAIN_PROJECTION = {"app_digest", "cnc_domain"};
    public static WatchlistReportDbHelper sInstance;

    /* loaded from: classes2.dex */
    public static class AggregatedResult {
        public final HashMap<String, String> appDigestCNCList;
        public final Set<String> appDigestList;
        public final String cncDomainVisited;

        public AggregatedResult(Set<String> set, String str, HashMap<String, String> hashMap) {
            this.appDigestList = set;
            this.cncDomainVisited = str;
            this.appDigestCNCList = hashMap;
        }
    }

    public static File getSystemWatchlistDbFile() {
        return new File(Environment.getDataSystemDirectory(), "watchlist_report.db");
    }

    public WatchlistReportDbHelper(Context context) {
        super(context, getSystemWatchlistDbFile().getAbsolutePath(), (SQLiteDatabase.CursorFactory) null, 2);
        setIdleConnectionTimeout(30000L);
    }

    public static synchronized WatchlistReportDbHelper getInstance(Context context) {
        synchronized (WatchlistReportDbHelper.class) {
            WatchlistReportDbHelper watchlistReportDbHelper = sInstance;
            if (watchlistReportDbHelper != null) {
                return watchlistReportDbHelper;
            }
            WatchlistReportDbHelper watchlistReportDbHelper2 = new WatchlistReportDbHelper(context);
            sInstance = watchlistReportDbHelper2;
            return watchlistReportDbHelper2;
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE records(app_digest BLOB,cnc_domain TEXT,timestamp INTEGER DEFAULT 0 )");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS records");
        onCreate(sQLiteDatabase);
    }

    public boolean insertNewRecord(byte[] bArr, String str, long j) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_digest", bArr);
            contentValues.put("cnc_domain", str);
            contentValues.put("timestamp", Long.valueOf(j));
            return writableDatabase.insert("records", null, contentValues) != -1;
        } catch (SQLiteException e) {
            Slog.e("WatchlistReportDbHelper", "Error opening the database to insert a new record", e);
            return false;
        }
    }

    public AggregatedResult getAggregatedRecords(long j) {
        Cursor cursor = null;
        try {
            try {
                Cursor query = getReadableDatabase().query(true, "records", DIGEST_DOMAIN_PROJECTION, "timestamp < ?", new String[]{Long.toString(j)}, null, null, null, null);
                if (query == null) {
                    if (query != null) {
                        query.close();
                    }
                    return null;
                }
                try {
                    HashSet hashSet = new HashSet();
                    HashMap hashMap = new HashMap();
                    while (query.moveToNext()) {
                        String hexString = HexDump.toHexString(query.getBlob(0));
                        String string = query.getString(1);
                        hashSet.add(hexString);
                        hashMap.put(hexString, string);
                    }
                    AggregatedResult aggregatedResult = new AggregatedResult(hashSet, null, hashMap);
                    query.close();
                    return aggregatedResult;
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (SQLiteException e) {
            Slog.e("WatchlistReportDbHelper", "Error opening the database", e);
            return null;
        }
    }

    public boolean cleanup(long j) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("timestamp< ");
            sb.append(j);
            return writableDatabase.delete("records", sb.toString(), null) != 0;
        } catch (SQLiteException e) {
            Slog.e("WatchlistReportDbHelper", "Error opening the database to cleanup", e);
            return false;
        }
    }
}
