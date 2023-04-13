package android.content;

import android.annotation.SystemApi;
import android.content.res.AssetFileDescriptor;
import android.database.CrossProcessCursorWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.DeadObjectException;
import android.p008os.Handler;
import android.p008os.ICancellationSignal;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class ContentProviderClient implements ContentInterface, AutoCloseable {
    private static final String TAG = "ContentProviderClient";
    private static Handler sAnrHandler;
    private NotRespondingRunnable mAnrRunnable;
    private long mAnrTimeout;
    private final AttributionSource mAttributionSource;
    private final String mAuthority;
    private final CloseGuard mCloseGuard;
    private final AtomicBoolean mClosed;
    private final IContentProvider mContentProvider;
    private final ContentResolver mContentResolver;
    private final String mPackageName;
    private final boolean mStable;

    public ContentProviderClient(ContentResolver contentResolver, IContentProvider contentProvider, boolean stable) {
        this(contentResolver, contentProvider, "unknown", stable);
    }

    public ContentProviderClient(ContentResolver contentResolver, IContentProvider contentProvider, String authority, boolean stable) {
        this.mClosed = new AtomicBoolean();
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mContentResolver = contentResolver;
        this.mContentProvider = contentProvider;
        this.mPackageName = contentResolver.mPackageName;
        this.mAttributionSource = contentResolver.getAttributionSource();
        this.mAuthority = authority;
        this.mStable = stable;
        closeGuard.open("ContentProviderClient.close");
    }

    @SystemApi
    public void setDetectNotResponding(long timeoutMillis) {
        synchronized (ContentProviderClient.class) {
            this.mAnrTimeout = timeoutMillis;
            if (timeoutMillis > 0) {
                if (this.mAnrRunnable == null) {
                    this.mAnrRunnable = new NotRespondingRunnable();
                }
                if (sAnrHandler == null) {
                    sAnrHandler = new Handler(Looper.getMainLooper(), null, true);
                }
                Binder.allowBlocking(this.mContentProvider.asBinder());
            } else {
                this.mAnrRunnable = null;
                Binder.defaultBlocking(this.mContentProvider.asBinder());
            }
        }
    }

    private void beforeRemote() {
        NotRespondingRunnable notRespondingRunnable = this.mAnrRunnable;
        if (notRespondingRunnable != null) {
            sAnrHandler.postDelayed(notRespondingRunnable, this.mAnrTimeout);
        }
    }

    private void afterRemote() {
        NotRespondingRunnable notRespondingRunnable = this.mAnrRunnable;
        if (notRespondingRunnable != null) {
            sAnrHandler.removeCallbacks(notRespondingRunnable);
        }
    }

    public Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs, String sortOrder) throws RemoteException {
        return query(url, projection, selection, selectionArgs, sortOrder, null);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) throws RemoteException {
        Bundle queryArgs = ContentResolver.createSqlQueryBundle(selection, selectionArgs, sortOrder);
        return query(uri, projection, queryArgs, cancellationSignal);
    }

    @Override // android.content.ContentInterface
    public Cursor query(Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws RemoteException {
        Objects.requireNonNull(uri, "url");
        beforeRemote();
        ICancellationSignal remoteCancellationSignal = null;
        if (cancellationSignal != null) {
            try {
                try {
                    cancellationSignal.throwIfCanceled();
                    remoteCancellationSignal = this.mContentProvider.createCancellationSignal();
                    cancellationSignal.setRemote(remoteCancellationSignal);
                } catch (DeadObjectException e) {
                    if (!this.mStable) {
                        this.mContentResolver.unstableProviderDied(this.mContentProvider);
                    }
                    throw e;
                }
            } finally {
                afterRemote();
            }
        }
        Cursor cursor = this.mContentProvider.query(this.mAttributionSource, uri, projection, queryArgs, remoteCancellationSignal);
        if (cursor == null) {
            afterRemote();
            return null;
        }
        return new CursorWrapperInner(cursor);
    }

    @Override // android.content.ContentInterface
    public String getType(Uri url) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.getType(url);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public String[] getStreamTypes(Uri url, String mimeTypeFilter) throws RemoteException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(mimeTypeFilter, "mimeTypeFilter");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.getStreamTypes(url, mimeTypeFilter);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public final Uri canonicalize(Uri url) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.canonicalize(this.mAttributionSource, url);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public final Uri uncanonicalize(Uri url) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.uncanonicalize(this.mAttributionSource, url);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public boolean refresh(Uri url, Bundle extras, CancellationSignal cancellationSignal) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        ICancellationSignal remoteCancellationSignal = null;
        try {
            if (cancellationSignal != null) {
                try {
                    cancellationSignal.throwIfCanceled();
                    remoteCancellationSignal = this.mContentProvider.createCancellationSignal();
                    cancellationSignal.setRemote(remoteCancellationSignal);
                } catch (DeadObjectException e) {
                    if (!this.mStable) {
                        this.mContentResolver.unstableProviderDied(this.mContentProvider);
                    }
                    throw e;
                }
            }
            return this.mContentProvider.refresh(this.mAttributionSource, url, extras, remoteCancellationSignal);
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public int checkUriPermission(Uri uri, int uid, int modeFlags) throws RemoteException {
        Objects.requireNonNull(uri, "uri");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.checkUriPermission(this.mAttributionSource, uri, uid, modeFlags);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    public Uri insert(Uri url, ContentValues initialValues) throws RemoteException {
        return insert(url, initialValues, null);
    }

    @Override // android.content.ContentInterface
    public Uri insert(Uri url, ContentValues initialValues, Bundle extras) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.insert(this.mAttributionSource, url, initialValues, extras);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // android.content.ContentInterface
    public int bulkInsert(Uri url, ContentValues[] initialValues) throws RemoteException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(initialValues, "initialValues");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.bulkInsert(this.mAttributionSource, url, initialValues);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    public int delete(Uri url, String selection, String[] selectionArgs) throws RemoteException {
        return delete(url, ContentResolver.createSqlQueryBundle(selection, selectionArgs));
    }

    @Override // android.content.ContentInterface
    public int delete(Uri url, Bundle extras) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.delete(this.mAttributionSource, url, extras);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    public int update(Uri url, ContentValues values, String selection, String[] selectionArgs) throws RemoteException {
        return update(url, values, ContentResolver.createSqlQueryBundle(selection, selectionArgs));
    }

    @Override // android.content.ContentInterface
    public int update(Uri url, ContentValues values, Bundle extras) throws RemoteException {
        Objects.requireNonNull(url, "url");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.update(this.mAttributionSource, url, values, extras);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    public ParcelFileDescriptor openFile(Uri url, String mode) throws RemoteException, FileNotFoundException {
        return openFile(url, mode, null);
    }

    @Override // android.content.ContentInterface
    public ParcelFileDescriptor openFile(Uri url, String mode, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(mode, "mode");
        beforeRemote();
        ICancellationSignal remoteSignal = null;
        if (signal != null) {
            try {
                try {
                    signal.throwIfCanceled();
                    remoteSignal = this.mContentProvider.createCancellationSignal();
                    signal.setRemote(remoteSignal);
                } catch (DeadObjectException e) {
                    if (!this.mStable) {
                        this.mContentResolver.unstableProviderDied(this.mContentProvider);
                    }
                    throw e;
                }
            } finally {
                afterRemote();
            }
        }
        return this.mContentProvider.openFile(this.mAttributionSource, url, mode, remoteSignal);
    }

    public AssetFileDescriptor openAssetFile(Uri url, String mode) throws RemoteException, FileNotFoundException {
        return openAssetFile(url, mode, null);
    }

    @Override // android.content.ContentInterface
    public AssetFileDescriptor openAssetFile(Uri url, String mode, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(mode, "mode");
        beforeRemote();
        ICancellationSignal remoteSignal = null;
        if (signal != null) {
            try {
                try {
                    signal.throwIfCanceled();
                    remoteSignal = this.mContentProvider.createCancellationSignal();
                    signal.setRemote(remoteSignal);
                } catch (DeadObjectException e) {
                    if (!this.mStable) {
                        this.mContentResolver.unstableProviderDied(this.mContentProvider);
                    }
                    throw e;
                }
            } finally {
                afterRemote();
            }
        }
        return this.mContentProvider.openAssetFile(this.mAttributionSource, url, mode, remoteSignal);
    }

    public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri uri, String mimeType, Bundle opts) throws RemoteException, FileNotFoundException {
        return openTypedAssetFileDescriptor(uri, mimeType, opts, null);
    }

    public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri uri, String mimeType, Bundle opts, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        return openTypedAssetFile(uri, mimeType, opts, signal);
    }

    @Override // android.content.ContentInterface
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws RemoteException, FileNotFoundException {
        Objects.requireNonNull(uri, "uri");
        Objects.requireNonNull(mimeTypeFilter, "mimeTypeFilter");
        beforeRemote();
        ICancellationSignal remoteSignal = null;
        if (signal != null) {
            try {
                try {
                    signal.throwIfCanceled();
                    remoteSignal = this.mContentProvider.createCancellationSignal();
                    signal.setRemote(remoteSignal);
                } catch (DeadObjectException e) {
                    if (!this.mStable) {
                        this.mContentResolver.unstableProviderDied(this.mContentProvider);
                    }
                    throw e;
                }
            } finally {
                afterRemote();
            }
        }
        return this.mContentProvider.openTypedAssetFile(this.mAttributionSource, uri, mimeTypeFilter, opts, remoteSignal);
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
        return applyBatch(this.mAuthority, operations);
    }

    @Override // android.content.ContentInterface
    public ContentProviderResult[] applyBatch(String authority, ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
        Objects.requireNonNull(operations, "operations");
        beforeRemote();
        try {
            try {
                return this.mContentProvider.applyBatch(this.mAttributionSource, authority, operations);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    public Bundle call(String method, String arg, Bundle extras) throws RemoteException {
        return call(this.mAuthority, method, arg, extras);
    }

    @Override // android.content.ContentInterface
    public Bundle call(String authority, String method, String arg, Bundle extras) throws RemoteException {
        Objects.requireNonNull(authority, ContactsContract.Directory.DIRECTORY_AUTHORITY);
        Objects.requireNonNull(method, CalendarContract.RemindersColumns.METHOD);
        beforeRemote();
        try {
            try {
                return this.mContentProvider.call(this.mAttributionSource, authority, method, arg, extras);
            } catch (DeadObjectException e) {
                if (!this.mStable) {
                    this.mContentResolver.unstableProviderDied(this.mContentProvider);
                }
                throw e;
            }
        } finally {
            afterRemote();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        closeInternal();
    }

    @Deprecated
    public boolean release() {
        return closeInternal();
    }

    private boolean closeInternal() {
        this.mCloseGuard.close();
        if (this.mClosed.compareAndSet(false, true)) {
            setDetectNotResponding(0L);
            if (this.mStable) {
                return this.mContentResolver.releaseProvider(this.mContentProvider);
            }
            return this.mContentResolver.releaseUnstableProvider(this.mContentProvider);
        }
        return false;
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public ContentProvider getLocalContentProvider() {
        return ContentProvider.coerceToLocalContentProvider(this.mContentProvider);
    }

    @Deprecated
    public static void closeQuietly(ContentProviderClient client) {
        IoUtils.closeQuietly(client);
    }

    @Deprecated
    public static void releaseQuietly(ContentProviderClient client) {
        IoUtils.closeQuietly(client);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NotRespondingRunnable implements Runnable {
        private NotRespondingRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.m104w(ContentProviderClient.TAG, "Detected provider not responding: " + ContentProviderClient.this.mContentProvider);
            ContentProviderClient.this.mContentResolver.appNotRespondingViaProvider(ContentProviderClient.this.mContentProvider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class CursorWrapperInner extends CrossProcessCursorWrapper {
        private final CloseGuard mCloseGuard;

        CursorWrapperInner(Cursor cursor) {
            super(cursor);
            CloseGuard closeGuard = CloseGuard.get();
            this.mCloseGuard = closeGuard;
            closeGuard.open("CursorWrapperInner.close");
        }

        @Override // android.database.CursorWrapper, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.mCloseGuard.close();
            super.close();
        }

        protected void finalize() throws Throwable {
            try {
                CloseGuard closeGuard = this.mCloseGuard;
                if (closeGuard != null) {
                    closeGuard.warnIfOpen();
                }
                close();
            } finally {
                super.finalize();
            }
        }
    }
}
