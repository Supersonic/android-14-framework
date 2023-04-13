package android.database;

import android.database.sqlite.SQLiteDatabase;
/* loaded from: classes.dex */
public interface DatabaseErrorHandler {
    void onCorruption(SQLiteDatabase sQLiteDatabase);
}
