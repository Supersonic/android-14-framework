package android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseConfiguration;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.util.List;
/* loaded from: classes.dex */
public final class DefaultDatabaseErrorHandler implements DatabaseErrorHandler {
    private static final String TAG = "DefaultDatabaseErrorHandler";

    @Override // android.database.DatabaseErrorHandler
    public void onCorruption(SQLiteDatabase dbObj) {
        Log.m110e(TAG, "Corruption reported by sqlite on database: " + dbObj.getPath());
        SQLiteDatabase.wipeDetected(dbObj.getPath(), "corruption");
        if (!dbObj.isOpen()) {
            deleteDatabaseFile(dbObj.getPath());
            return;
        }
        List<Pair<String, String>> attachedDbs = null;
        try {
            try {
                attachedDbs = dbObj.getAttachedDbs();
            } finally {
                if (attachedDbs != null) {
                    for (Pair<String, String> p : attachedDbs) {
                        deleteDatabaseFile(p.second);
                    }
                } else {
                    deleteDatabaseFile(dbObj.getPath());
                }
            }
        } catch (SQLiteException e) {
        }
        try {
            dbObj.close();
        } catch (SQLiteException e2) {
        }
    }

    private void deleteDatabaseFile(String fileName) {
        if (fileName.equalsIgnoreCase(SQLiteDatabaseConfiguration.MEMORY_DB_PATH) || fileName.trim().length() == 0) {
            return;
        }
        Log.m110e(TAG, "deleting the database file: " + fileName);
        try {
            SQLiteDatabase.deleteDatabase(new File(fileName), false);
        } catch (Exception e) {
            Log.m104w(TAG, "delete failed: " + e.getMessage());
        }
    }
}
