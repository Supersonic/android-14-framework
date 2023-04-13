package android.database.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.p008os.CancellationSignal;
/* loaded from: classes.dex */
public final class SQLiteDirectCursorDriver implements SQLiteCursorDriver {
    private final CancellationSignal mCancellationSignal;
    private final SQLiteDatabase mDatabase;
    private final String mEditTable;
    private SQLiteQuery mQuery;
    private final String mSql;

    public SQLiteDirectCursorDriver(SQLiteDatabase db, String sql, String editTable, CancellationSignal cancellationSignal) {
        this.mDatabase = db;
        this.mEditTable = editTable;
        this.mSql = sql;
        this.mCancellationSignal = cancellationSignal;
    }

    @Override // android.database.sqlite.SQLiteCursorDriver
    public Cursor query(SQLiteDatabase.CursorFactory factory, String[] selectionArgs) {
        Cursor cursor;
        SQLiteQuery query = new SQLiteQuery(this.mDatabase, this.mSql, this.mCancellationSignal);
        try {
            query.bindAllArgsAsStrings(selectionArgs);
            if (factory == null) {
                cursor = new SQLiteCursor(this, this.mEditTable, query);
            } else {
                cursor = factory.newCursor(this.mDatabase, this, this.mEditTable, query);
            }
            this.mQuery = query;
            return cursor;
        } catch (RuntimeException ex) {
            query.close();
            throw ex;
        }
    }

    @Override // android.database.sqlite.SQLiteCursorDriver
    public void cursorClosed() {
    }

    @Override // android.database.sqlite.SQLiteCursorDriver
    public void setBindArguments(String[] bindArgs) {
        this.mQuery.bindAllArgsAsStrings(bindArgs);
    }

    @Override // android.database.sqlite.SQLiteCursorDriver
    public void cursorDeactivated() {
    }

    @Override // android.database.sqlite.SQLiteCursorDriver
    public void cursorRequeried(Cursor cursor) {
    }

    public String toString() {
        return "SQLiteDirectCursorDriver: " + this.mSql;
    }
}
