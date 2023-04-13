package android.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.p008os.Bundle;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class AbstractCursor implements CrossProcessCursor {
    private static final String TAG = "Cursor";
    private final CloseGuard mCloseGuard;
    @Deprecated
    protected boolean mClosed;
    @Deprecated
    protected ContentResolver mContentResolver;
    protected Long mCurrentRowID;
    private Uri mNotifyUri;
    private List<Uri> mNotifyUris;
    @Deprecated
    protected int mPos;
    protected int mRowIdColumnIndex;
    private ContentObserver mSelfObserver;
    private boolean mSelfObserverRegistered;
    protected HashMap<Long, Map<String, Object>> mUpdatedRows;
    private final Object mSelfObserverLock = new Object();
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private final ContentObservable mContentObservable = new ContentObservable();
    private Bundle mExtras = Bundle.EMPTY;

    @Override // android.database.Cursor
    public abstract String[] getColumnNames();

    @Override // android.database.Cursor
    public abstract int getCount();

    @Override // android.database.Cursor
    public abstract double getDouble(int i);

    @Override // android.database.Cursor
    public abstract float getFloat(int i);

    @Override // android.database.Cursor
    public abstract int getInt(int i);

    @Override // android.database.Cursor
    public abstract long getLong(int i);

    @Override // android.database.Cursor
    public abstract short getShort(int i);

    @Override // android.database.Cursor
    public abstract String getString(int i);

    @Override // android.database.Cursor
    public abstract boolean isNull(int i);

    @Override // android.database.Cursor
    public int getType(int column) {
        return 3;
    }

    @Override // android.database.Cursor
    public byte[] getBlob(int column) {
        throw new UnsupportedOperationException("getBlob is not supported");
    }

    @Override // android.database.CrossProcessCursor
    public CursorWindow getWindow() {
        return null;
    }

    @Override // android.database.Cursor
    public int getColumnCount() {
        return getColumnNames().length;
    }

    @Override // android.database.Cursor
    public void deactivate() {
        onDeactivateOrClose();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDeactivateOrClose() {
        ContentObserver contentObserver = this.mSelfObserver;
        if (contentObserver != null) {
            this.mContentResolver.unregisterContentObserver(contentObserver);
            this.mSelfObserverRegistered = false;
        }
        this.mDataSetObservable.notifyInvalidated();
    }

    @Override // android.database.Cursor
    public boolean requery() {
        if (this.mSelfObserver != null && !this.mSelfObserverRegistered) {
            int size = this.mNotifyUris.size();
            for (int i = 0; i < size; i++) {
                Uri notifyUri = this.mNotifyUris.get(i);
                this.mContentResolver.registerContentObserver(notifyUri, true, this.mSelfObserver);
            }
            this.mSelfObserverRegistered = true;
        }
        this.mDataSetObservable.notifyChanged();
        return true;
    }

    @Override // android.database.Cursor
    public boolean isClosed() {
        return this.mClosed;
    }

    @Override // android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mClosed = true;
        this.mContentObservable.unregisterAll();
        onDeactivateOrClose();
        this.mCloseGuard.close();
    }

    @Override // android.database.CrossProcessCursor
    public boolean onMove(int oldPosition, int newPosition) {
        return true;
    }

    @Override // android.database.Cursor
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        String result = getString(columnIndex);
        if (result != null) {
            char[] data = buffer.data;
            if (data != null && data.length >= result.length()) {
                result.getChars(0, result.length(), data, 0);
            } else {
                buffer.data = result.toCharArray();
            }
            buffer.sizeCopied = result.length();
            return;
        }
        buffer.sizeCopied = 0;
    }

    public AbstractCursor() {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mPos = -1;
        closeGuard.open("AbstractCursor.close");
    }

    @Override // android.database.Cursor
    public final int getPosition() {
        return this.mPos;
    }

    @Override // android.database.Cursor
    public final boolean moveToPosition(int position) {
        int count = getCount();
        if (position >= count) {
            this.mPos = count;
            return false;
        } else if (position < 0) {
            this.mPos = -1;
            return false;
        } else {
            int i = this.mPos;
            if (position == i) {
                return true;
            }
            boolean result = onMove(i, position);
            if (!result) {
                this.mPos = -1;
            } else {
                this.mPos = position;
            }
            return result;
        }
    }

    @Override // android.database.CrossProcessCursor
    public void fillWindow(int position, CursorWindow window) {
        DatabaseUtils.cursorFillWindow(this, position, window);
    }

    @Override // android.database.Cursor
    public final boolean move(int offset) {
        return moveToPosition(this.mPos + offset);
    }

    @Override // android.database.Cursor
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override // android.database.Cursor
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override // android.database.Cursor
    public final boolean moveToNext() {
        return moveToPosition(this.mPos + 1);
    }

    @Override // android.database.Cursor
    public final boolean moveToPrevious() {
        return moveToPosition(this.mPos - 1);
    }

    @Override // android.database.Cursor
    public final boolean isFirst() {
        return this.mPos == 0 && getCount() != 0;
    }

    @Override // android.database.Cursor
    public final boolean isLast() {
        int cnt = getCount();
        return this.mPos == cnt + (-1) && cnt != 0;
    }

    @Override // android.database.Cursor
    public final boolean isBeforeFirst() {
        return getCount() == 0 || this.mPos == -1;
    }

    @Override // android.database.Cursor
    public final boolean isAfterLast() {
        return getCount() == 0 || this.mPos == getCount();
    }

    @Override // android.database.Cursor
    public int getColumnIndex(String columnName) {
        int periodIndex = columnName.lastIndexOf(46);
        if (periodIndex != -1) {
            Exception e = new Exception();
            Log.m109e(TAG, "requesting column name with table name -- " + columnName, e);
            columnName = columnName.substring(periodIndex + 1);
        }
        String[] columnNames = getColumnNames();
        int length = columnNames.length;
        for (int i = 0; i < length; i++) {
            if (columnNames[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    @Override // android.database.Cursor
    public int getColumnIndexOrThrow(String columnName) {
        int index = getColumnIndex(columnName);
        if (index < 0) {
            String availableColumns = "";
            try {
                availableColumns = Arrays.toString(getColumnNames());
            } catch (Exception e) {
                Log.m111d(TAG, "Cannot collect column names for debug purposes", e);
            }
            throw new IllegalArgumentException("column '" + columnName + "' does not exist. Available columns: " + availableColumns);
        }
        return index;
    }

    @Override // android.database.Cursor
    public String getColumnName(int columnIndex) {
        return getColumnNames()[columnIndex];
    }

    @Override // android.database.Cursor
    public void registerContentObserver(ContentObserver observer) {
        this.mContentObservable.registerObserver(observer);
    }

    @Override // android.database.Cursor
    public void unregisterContentObserver(ContentObserver observer) {
        if (!this.mClosed) {
            this.mContentObservable.unregisterObserver(observer);
        }
    }

    @Override // android.database.Cursor
    public void registerDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.registerObserver(observer);
    }

    @Override // android.database.Cursor
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.unregisterObserver(observer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onChange(boolean selfChange) {
        synchronized (this.mSelfObserverLock) {
            this.mContentObservable.dispatchChange(selfChange, null);
            List<Uri> list = this.mNotifyUris;
            if (list != null && selfChange) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    Uri notifyUri = this.mNotifyUris.get(i);
                    this.mContentResolver.notifyChange(notifyUri, this.mSelfObserver);
                }
            }
        }
    }

    @Override // android.database.Cursor
    public void setNotificationUri(ContentResolver cr, Uri notifyUri) {
        setNotificationUris(cr, Arrays.asList(notifyUri));
    }

    @Override // android.database.Cursor
    public void setNotificationUris(ContentResolver cr, List<Uri> notifyUris) {
        Objects.requireNonNull(cr);
        Objects.requireNonNull(notifyUris);
        setNotificationUris(cr, notifyUris, cr.getUserId(), true);
    }

    public void setNotificationUris(ContentResolver cr, List<Uri> notifyUris, int userHandle, boolean registerSelfObserver) {
        synchronized (this.mSelfObserverLock) {
            this.mNotifyUris = notifyUris;
            this.mNotifyUri = notifyUris.get(0);
            this.mContentResolver = cr;
            ContentObserver contentObserver = this.mSelfObserver;
            if (contentObserver != null) {
                cr.unregisterContentObserver(contentObserver);
                this.mSelfObserverRegistered = false;
            }
            if (registerSelfObserver) {
                this.mSelfObserver = new SelfContentObserver(this);
                int size = this.mNotifyUris.size();
                for (int i = 0; i < size; i++) {
                    Uri notifyUri = this.mNotifyUris.get(i);
                    this.mContentResolver.registerContentObserver(notifyUri, true, this.mSelfObserver, userHandle);
                }
                this.mSelfObserverRegistered = true;
            }
        }
    }

    @Override // android.database.Cursor
    public Uri getNotificationUri() {
        Uri uri;
        synchronized (this.mSelfObserverLock) {
            uri = this.mNotifyUri;
        }
        return uri;
    }

    @Override // android.database.Cursor
    public List<Uri> getNotificationUris() {
        List<Uri> list;
        synchronized (this.mSelfObserverLock) {
            list = this.mNotifyUris;
        }
        return list;
    }

    @Override // android.database.Cursor
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override // android.database.Cursor
    public void setExtras(Bundle extras) {
        this.mExtras = extras == null ? Bundle.EMPTY : extras;
    }

    @Override // android.database.Cursor
    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // android.database.Cursor
    public Bundle respond(Bundle extras) {
        return Bundle.EMPTY;
    }

    @Deprecated
    protected boolean isFieldUpdated(int columnIndex) {
        return false;
    }

    @Deprecated
    protected Object getUpdatedField(int columnIndex) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkPosition() {
        if (-1 == this.mPos || getCount() == this.mPos) {
            throw new CursorIndexOutOfBoundsException(this.mPos, getCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() {
        ContentObserver contentObserver = this.mSelfObserver;
        if (contentObserver != null && this.mSelfObserverRegistered) {
            this.mContentResolver.unregisterContentObserver(contentObserver);
        }
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            if (!this.mClosed) {
                close();
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class SelfContentObserver extends ContentObserver {
        WeakReference<AbstractCursor> mCursor;

        public SelfContentObserver(AbstractCursor cursor) {
            super(null);
            this.mCursor = new WeakReference<>(cursor);
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            AbstractCursor cursor = this.mCursor.get();
            if (cursor != null) {
                cursor.onChange(false);
            }
        }
    }
}
