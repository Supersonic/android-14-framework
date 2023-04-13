package android.database.sqlite;

import android.database.AbstractWindowedCursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDebug;
import android.p008os.StrictMode;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class SQLiteCursor extends AbstractWindowedCursor {
    static final int NO_COUNT = -1;
    static final String TAG = "SQLiteCursor";
    private Map<String, Integer> mColumnNameMap;
    private final String[] mColumns;
    private int mCount;
    private int mCursorWindowCapacity;
    private final SQLiteCursorDriver mDriver;
    private final String mEditTable;
    private boolean mFillWindowForwardOnly;
    private final SQLiteQuery mQuery;

    @Deprecated
    public SQLiteCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        this(driver, editTable, query);
    }

    public SQLiteCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        this.mCount = -1;
        if (query == null) {
            throw new IllegalArgumentException("query object cannot be null");
        }
        this.mDriver = driver;
        this.mEditTable = editTable;
        this.mColumnNameMap = null;
        this.mQuery = query;
        this.mColumns = query.getColumnNames();
    }

    public SQLiteDatabase getDatabase() {
        return this.mQuery.getDatabase();
    }

    @Override // android.database.AbstractCursor, android.database.CrossProcessCursor
    public boolean onMove(int oldPosition, int newPosition) {
        if (this.mWindow == null || newPosition < this.mWindow.getStartPosition() || newPosition >= this.mWindow.getStartPosition() + this.mWindow.getNumRows()) {
            fillWindow(newPosition);
            return true;
        }
        return true;
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getCount() {
        if (this.mCount == -1) {
            fillWindow(0);
        }
        return this.mCount;
    }

    private void fillWindow(int requiredPos) {
        clearOrCreateWindow(getDatabase().getPath());
        try {
            Preconditions.checkArgumentNonnegative(requiredPos, "requiredPos cannot be negative");
            if (this.mCount == -1) {
                this.mCount = this.mQuery.fillWindow(this.mWindow, requiredPos, requiredPos, true);
                this.mCursorWindowCapacity = this.mWindow.getNumRows();
                if (SQLiteDebug.NoPreloadHolder.DEBUG_SQL_LOG) {
                    Log.m112d(TAG, "received count(*) from native_fill_window: " + this.mCount);
                    return;
                }
                return;
            }
            int startPos = this.mFillWindowForwardOnly ? requiredPos : DatabaseUtils.cursorPickFillWindowStartPosition(requiredPos, this.mCursorWindowCapacity);
            this.mQuery.fillWindow(this.mWindow, startPos, requiredPos, false);
        } catch (RuntimeException ex) {
            closeWindow();
            throw ex;
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getColumnIndex(String columnName) {
        if (this.mColumnNameMap == null) {
            String[] columns = this.mColumns;
            int columnCount = columns.length;
            HashMap<String, Integer> map = new HashMap<>(columnCount, 1.0f);
            for (int i = 0; i < columnCount; i++) {
                map.put(columns[i], Integer.valueOf(i));
            }
            this.mColumnNameMap = map;
        }
        int periodIndex = columnName.lastIndexOf(46);
        if (periodIndex != -1) {
            Exception e = new Exception();
            Log.m109e(TAG, "requesting column name with table name -- " + columnName, e);
            columnName = columnName.substring(periodIndex + 1);
        }
        Integer i2 = this.mColumnNameMap.get(columnName);
        if (i2 == null) {
            return -1;
        }
        return i2.intValue();
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public String[] getColumnNames() {
        return this.mColumns;
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void deactivate() {
        super.deactivate();
        this.mDriver.cursorDeactivated();
    }

    @Override // android.database.AbstractCursor, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        super.close();
        synchronized (this) {
            this.mQuery.close();
            this.mDriver.cursorClosed();
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public boolean requery() {
        if (isClosed()) {
            return false;
        }
        synchronized (this) {
            if (this.mQuery.getDatabase().isOpen()) {
                if (this.mWindow != null) {
                    this.mWindow.clear();
                }
                this.mPos = -1;
                this.mCount = -1;
                this.mDriver.cursorRequeried(this);
                try {
                    return super.requery();
                } catch (IllegalStateException e) {
                    Log.m103w(TAG, "requery() failed " + e.getMessage(), e);
                    return false;
                }
            }
            return false;
        }
    }

    @Override // android.database.AbstractWindowedCursor
    public void setWindow(CursorWindow window) {
        super.setWindow(window);
        this.mCount = -1;
    }

    public void setSelectionArguments(String[] selectionArgs) {
        this.mDriver.setBindArguments(selectionArgs);
    }

    public void setFillWindowForwardOnly(boolean fillWindowForwardOnly) {
        this.mFillWindowForwardOnly = fillWindowForwardOnly;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.database.AbstractCursor
    public void finalize() {
        try {
            if (this.mWindow != null && StrictMode.vmSqliteObjectLeaksEnabled()) {
                String sql = this.mQuery.getSql();
                int len = sql.length();
                StringBuilder append = new StringBuilder().append("Finalizing a Cursor that has not been deactivated or closed. database = ").append(this.mQuery.getDatabase().getLabel()).append(", table = ").append(this.mEditTable).append(", query = ");
                int i = 1000;
                if (len <= 1000) {
                    i = len;
                }
                StrictMode.onSqliteObjectLeaked(append.append(sql.substring(0, i)).toString(), null);
            }
        } finally {
            super.finalize();
        }
    }
}
