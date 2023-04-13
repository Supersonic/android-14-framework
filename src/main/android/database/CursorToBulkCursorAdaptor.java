package android.database;

import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class CursorToBulkCursorAdaptor extends BulkCursorNative implements IBinder.DeathRecipient {
    private static final String TAG = "Cursor";
    private CrossProcessCursor mCursor;
    private CursorWindow mFilledWindow;
    private final Object mLock;
    private ContentObserverProxy mObserver;
    private final String mProviderName;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ContentObserverProxy extends ContentObserver {
        protected IContentObserver mRemote;

        public ContentObserverProxy(IContentObserver remoteObserver, IBinder.DeathRecipient recipient) {
            super(null);
            this.mRemote = remoteObserver;
            try {
                remoteObserver.asBinder().linkToDeath(recipient, 0);
            } catch (RemoteException e) {
            }
        }

        public boolean unlinkToDeath(IBinder.DeathRecipient recipient) {
            return this.mRemote.asBinder().unlinkToDeath(recipient, 0);
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Collection<Uri> uris, int flags, int userId) {
            final ArrayList<Uri> asList = new ArrayList<>();
            Objects.requireNonNull(asList);
            uris.forEach(new Consumer() { // from class: android.database.CursorToBulkCursorAdaptor$ContentObserverProxy$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    asList.add((Uri) obj);
                }
            });
            Uri[] asArray = (Uri[]) asList.toArray(new Uri[asList.size()]);
            try {
                this.mRemote.onChangeEtc(selfChange, asArray, flags, userId);
            } catch (RemoteException e) {
            }
        }
    }

    public CursorToBulkCursorAdaptor(Cursor cursor, IContentObserver observer, String providerName) {
        Object obj = new Object();
        this.mLock = obj;
        if (cursor instanceof CrossProcessCursor) {
            this.mCursor = (CrossProcessCursor) cursor;
        } else {
            this.mCursor = new CrossProcessCursorWrapper(cursor);
        }
        this.mProviderName = providerName;
        synchronized (obj) {
            createAndRegisterObserverProxyLocked(observer);
        }
    }

    private void closeFilledWindowLocked() {
        CursorWindow cursorWindow = this.mFilledWindow;
        if (cursorWindow != null) {
            cursorWindow.close();
            this.mFilledWindow = null;
        }
    }

    private void disposeLocked() {
        if (this.mCursor != null) {
            unregisterObserverProxyLocked();
            this.mCursor.close();
            this.mCursor = null;
        }
        closeFilledWindowLocked();
    }

    private void throwIfCursorIsClosed() {
        if (this.mCursor == null) {
            throw new StaleDataException("Attempted to access a cursor after it has been closed.");
        }
    }

    @Override // android.p008os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mLock) {
            disposeLocked();
        }
    }

    public BulkCursorDescriptor getBulkCursorDescriptor() {
        BulkCursorDescriptor d;
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            d = new BulkCursorDescriptor();
            d.cursor = this;
            d.columnNames = this.mCursor.getColumnNames();
            d.wantsAllOnMoveCalls = this.mCursor.getWantsAllOnMoveCalls();
            d.count = this.mCursor.getCount();
            d.window = this.mCursor.getWindow();
            if (d.window != null) {
                d.window.acquireReference();
            }
        }
        return d;
    }

    @Override // android.database.IBulkCursor
    public CursorWindow getWindow(int position) {
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            if (!this.mCursor.moveToPosition(position)) {
                closeFilledWindowLocked();
                return null;
            }
            CursorWindow window = this.mCursor.getWindow();
            if (window != null) {
                closeFilledWindowLocked();
            } else {
                window = this.mFilledWindow;
                if (window == null) {
                    CursorWindow cursorWindow = new CursorWindow(this.mProviderName);
                    this.mFilledWindow = cursorWindow;
                    window = cursorWindow;
                } else if (position < window.getStartPosition() || position >= window.getStartPosition() + window.getNumRows()) {
                    window.clear();
                }
                this.mCursor.fillWindow(position, window);
            }
            if (window != null) {
                window.acquireReference();
            }
            return window;
        }
    }

    @Override // android.database.IBulkCursor
    public void onMove(int position) {
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            CrossProcessCursor crossProcessCursor = this.mCursor;
            crossProcessCursor.onMove(crossProcessCursor.getPosition(), position);
        }
    }

    @Override // android.database.IBulkCursor
    public void deactivate() {
        synchronized (this.mLock) {
            if (this.mCursor != null) {
                unregisterObserverProxyLocked();
                this.mCursor.deactivate();
            }
            closeFilledWindowLocked();
        }
    }

    @Override // android.database.IBulkCursor
    public void close() {
        synchronized (this.mLock) {
            disposeLocked();
        }
    }

    @Override // android.database.IBulkCursor
    public int requery(IContentObserver observer) {
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            closeFilledWindowLocked();
            try {
                if (this.mCursor.requery()) {
                    unregisterObserverProxyLocked();
                    createAndRegisterObserverProxyLocked(observer);
                    return this.mCursor.getCount();
                }
                return -1;
            } catch (IllegalStateException e) {
                IllegalStateException leakProgram = new IllegalStateException(this.mProviderName + " Requery misuse db, mCursor isClosed:" + this.mCursor.isClosed(), e);
                throw leakProgram;
            }
        }
    }

    private void createAndRegisterObserverProxyLocked(IContentObserver observer) {
        if (this.mObserver != null) {
            throw new IllegalStateException("an observer is already registered");
        }
        ContentObserverProxy contentObserverProxy = new ContentObserverProxy(observer, this);
        this.mObserver = contentObserverProxy;
        this.mCursor.registerContentObserver(contentObserverProxy);
    }

    private void unregisterObserverProxyLocked() {
        ContentObserverProxy contentObserverProxy = this.mObserver;
        if (contentObserverProxy != null) {
            this.mCursor.unregisterContentObserver(contentObserverProxy);
            this.mObserver.unlinkToDeath(this);
            this.mObserver = null;
        }
    }

    @Override // android.database.IBulkCursor
    public Bundle getExtras() {
        Bundle extras;
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            extras = this.mCursor.getExtras();
        }
        return extras;
    }

    @Override // android.database.IBulkCursor
    public Bundle respond(Bundle extras) {
        Bundle respond;
        synchronized (this.mLock) {
            throwIfCursorIsClosed();
            respond = this.mCursor.respond(extras);
        }
        return respond;
    }
}
