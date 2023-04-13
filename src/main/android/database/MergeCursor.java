package android.database;
/* loaded from: classes.dex */
public class MergeCursor extends AbstractCursor {
    private Cursor mCursor;
    private Cursor[] mCursors;
    private DataSetObserver mObserver = new DataSetObserver() { // from class: android.database.MergeCursor.1
        @Override // android.database.DataSetObserver
        public void onChanged() {
            MergeCursor.this.mPos = -1;
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            MergeCursor.this.mPos = -1;
        }
    };

    public MergeCursor(Cursor[] cursors) {
        this.mCursors = cursors;
        this.mCursor = cursors[0];
        int i = 0;
        while (true) {
            Cursor[] cursorArr = this.mCursors;
            if (i < cursorArr.length) {
                Cursor cursor = cursorArr[i];
                if (cursor != null) {
                    cursor.registerDataSetObserver(this.mObserver);
                }
                i++;
            } else {
                return;
            }
        }
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
        this.mCursor = null;
        int cursorStartPos = 0;
        int length = this.mCursors.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                if (newPosition < cursor.getCount() + cursorStartPos) {
                    this.mCursor = this.mCursors[i];
                    break;
                }
                cursorStartPos += this.mCursors[i].getCount();
            }
            i++;
        }
        Cursor cursor2 = this.mCursor;
        if (cursor2 != null) {
            boolean ret = cursor2.moveToPosition(newPosition - cursorStartPos);
            return ret;
        }
        return false;
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
        return new String[0];
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
        super.deactivate();
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
        super.close();
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void registerContentObserver(ContentObserver observer) {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.registerContentObserver(observer);
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void unregisterContentObserver(ContentObserver observer) {
        int length = this.mCursors.length;
        for (int i = 0; i < length; i++) {
            Cursor cursor = this.mCursors[i];
            if (cursor != null) {
                cursor.unregisterContentObserver(observer);
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
