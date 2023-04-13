package com.android.internal.database;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import java.lang.reflect.Array;
/* loaded from: classes4.dex */
public class SortCursor extends AbstractCursor {
    private static final String TAG = "SortCursor";
    private int[][] mCurRowNumCache;
    private Cursor mCursor;
    private Cursor[] mCursors;
    private int[] mSortColumns;
    private final int ROWCACHESIZE = 64;
    private int[] mRowNumCache = new int[64];
    private int[] mCursorCache = new int[64];
    private int mLastCacheHit = -1;
    private DataSetObserver mObserver = new DataSetObserver() { // from class: com.android.internal.database.SortCursor.1
        @Override // android.database.DataSetObserver
        public void onChanged() {
            SortCursor.this.mPos = -1;
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            SortCursor.this.mPos = -1;
        }
    };

    public SortCursor(Cursor[] cursors, String sortcolumn) {
        this.mCursors = cursors;
        int length = cursors.length;
        this.mSortColumns = new int[length];
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.registerDataSetObserver(this.mObserver);
                this.mCursors[i].moveToFirst();
                this.mSortColumns[i] = this.mCursors[i].getColumnIndexOrThrow(sortcolumn);
            }
        }
        this.mCursor = null;
        String smallest = "";
        for (int j = 0; j < length; j++) {
            Cursor cursor2 = this.mCursors[j];
            if (cursor2 != null && !cursor2.isAfterLast()) {
                String current = this.mCursors[j].getString(this.mSortColumns[j]);
                if (this.mCursor == null || current.compareToIgnoreCase(smallest) < 0) {
                    smallest = current;
                    this.mCursor = this.mCursors[j];
                }
            }
        }
        for (int i2 = this.mRowNumCache.length - 1; i2 >= 0; i2--) {
            this.mRowNumCache[i2] = -2;
        }
        this.mCurRowNumCache = (int[][]) Array.newInstance(Integer.TYPE, 64, length);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getCount() {
        int count = 0;
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                count += cursor.getCount();
            }
        }
        return count;
    }

    @Override // android.database.AbstractCursor, android.database.CrossProcessCursor
    public boolean onMove(int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return true;
        }
        int cache_entry = newPosition % 64;
        if (this.mRowNumCache[cache_entry] == newPosition) {
            int which = this.mCursorCache[cache_entry];
            Cursor cursor = this.mCursors[which];
            this.mCursor = cursor;
            if (cursor == null) {
                Log.m104w(TAG, "onMove: cache results in a null cursor.");
                return false;
            }
            cursor.moveToPosition(this.mCurRowNumCache[cache_entry][which]);
            this.mLastCacheHit = cache_entry;
            return true;
        }
        this.mCursor = null;
        int length = this.mCursors.length;
        if (this.mLastCacheHit >= 0) {
            for (int i = 0; i < length; i++) {
                Cursor cursor2 = this.mCursors[i];
                if (cursor2 != null) {
                    cursor2.moveToPosition(this.mCurRowNumCache[this.mLastCacheHit][i]);
                }
            }
        }
        if (newPosition < oldPosition || oldPosition == -1) {
            for (int i2 = 0; i2 < length; i2++) {
                Cursor cursor3 = this.mCursors[i2];
                if (cursor3 != null) {
                    cursor3.moveToFirst();
                }
            }
            oldPosition = 0;
        }
        if (oldPosition < 0) {
            oldPosition = 0;
        }
        int smallestIdx = -1;
        for (int i3 = oldPosition; i3 <= newPosition; i3++) {
            String smallest = "";
            smallestIdx = -1;
            for (int j = 0; j < length; j++) {
                Cursor cursor4 = this.mCursors[j];
                if (cursor4 != null && !cursor4.isAfterLast()) {
                    String current = this.mCursors[j].getString(this.mSortColumns[j]);
                    if (smallestIdx < 0 || current.compareToIgnoreCase(smallest) < 0) {
                        smallest = current;
                        smallestIdx = j;
                    }
                }
            }
            if (i3 == newPosition) {
                break;
            }
            Cursor cursor5 = this.mCursors[smallestIdx];
            if (cursor5 != null) {
                cursor5.moveToNext();
            }
        }
        this.mCursor = this.mCursors[smallestIdx];
        this.mRowNumCache[cache_entry] = newPosition;
        this.mCursorCache[cache_entry] = smallestIdx;
        for (int i4 = 0; i4 < length; i4++) {
            Cursor cursor6 = this.mCursors[i4];
            if (cursor6 != null) {
                this.mCurRowNumCache[cache_entry][i4] = cursor6.getPosition();
            }
        }
        this.mLastCacheHit = -1;
        return true;
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public String getString(int column) {
        return this.mCursor.getString(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public short getShort(int column) {
        return this.mCursor.getShort(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getInt(int column) {
        return this.mCursor.getInt(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public long getLong(int column) {
        return this.mCursor.getLong(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public float getFloat(int column) {
        return this.mCursor.getFloat(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public double getDouble(int column) {
        return this.mCursor.getDouble(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getType(int column) {
        return this.mCursor.getType(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public boolean isNull(int column) {
        return this.mCursor.isNull(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public byte[] getBlob(int column) {
        return this.mCursor.getBlob(column);
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public String[] getColumnNames() {
        Cursor cursor = this.mCursor;
        if (cursor != null) {
            return cursor.getColumnNames();
        }
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor2 = this.mCursors[i];
            if (cursor2 != null) {
                return cursor2.getColumnNames();
            }
        }
        throw new IllegalStateException("No cursor that can return names");
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void deactivate() {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.deactivate();
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void registerDataSetObserver(DataSetObserver observer) {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.registerDataSetObserver(observer);
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void unregisterDataSetObserver(DataSetObserver observer) {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.unregisterDataSetObserver(observer);
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public boolean requery() {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null && !cursor.requery()) {
                return false;
            }
        }
        return true;
    }
}
