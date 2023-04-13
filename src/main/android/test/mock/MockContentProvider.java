package android.test.mock;

import android.content.AttributionSource;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.OperationApplicationException;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class MockContentProvider extends ContentProvider {
    private final InversionIContentProvider mIContentProvider;

    /* loaded from: classes.dex */
    private class InversionIContentProvider implements IContentProvider {
        private InversionIContentProvider() {
        }

        public ContentProviderResult[] applyBatch(AttributionSource attributionSource, String authority, ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
            return MockContentProvider.this.applyBatch(authority, operations);
        }

        public int bulkInsert(AttributionSource attributionSource, Uri url, ContentValues[] initialValues) throws RemoteException {
            return MockContentProvider.this.bulkInsert(url, initialValues);
        }

        public int delete(AttributionSource attributionSource, Uri url, Bundle extras) throws RemoteException {
            return MockContentProvider.this.delete(url, extras);
        }

        public String getType(AttributionSource attributionSource, Uri url) throws RemoteException {
            return MockContentProvider.this.getType(url);
        }

        public void getTypeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) throws RemoteException {
            MockContentProvider.this.getTypeAsync(uri, callback);
        }

        public void getTypeAnonymousAsync(Uri uri, RemoteCallback callback) throws RemoteException {
            MockContentProvider.this.getTypeAnonymousAsync(uri, callback);
        }

        public Uri insert(AttributionSource attributionSource, Uri url, ContentValues initialValues, Bundle extras) throws RemoteException {
            return MockContentProvider.this.insert(url, initialValues, extras);
        }

        public AssetFileDescriptor openAssetFile(AttributionSource attributionSource, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
            return MockContentProvider.this.openAssetFile(url, mode);
        }

        public ParcelFileDescriptor openFile(AttributionSource attributionSource, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
            return MockContentProvider.this.openFile(url, mode);
        }

        public Cursor query(AttributionSource attributionSource, Uri url, String[] projection, Bundle queryArgs, ICancellationSignal cancellationSignal) throws RemoteException {
            return MockContentProvider.this.query(url, projection, queryArgs, null);
        }

        public int update(AttributionSource attributionSource, Uri url, ContentValues values, Bundle extras) throws RemoteException {
            return MockContentProvider.this.update(url, values, extras);
        }

        public Bundle call(AttributionSource attributionSource, String authority, String method, String request, Bundle args) throws RemoteException {
            return MockContentProvider.this.call(authority, method, request, args);
        }

        public IBinder asBinder() {
            return MockContentProvider.this.getIContentProviderBinder();
        }

        public String[] getStreamTypes(Uri url, String mimeTypeFilter) throws RemoteException {
            return MockContentProvider.this.getStreamTypes(url, mimeTypeFilter);
        }

        public AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource, Uri url, String mimeType, Bundle opts, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
            return MockContentProvider.this.openTypedAssetFile(url, mimeType, opts);
        }

        public ICancellationSignal createCancellationSignal() throws RemoteException {
            return null;
        }

        public Uri canonicalize(AttributionSource attributionSource, Uri uri) throws RemoteException {
            return MockContentProvider.this.canonicalize(uri);
        }

        public void canonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) {
            MockContentProvider.this.canonicalizeAsync(uri, callback);
        }

        public Uri uncanonicalize(AttributionSource attributionSource, Uri uri) throws RemoteException {
            return MockContentProvider.this.uncanonicalize(uri);
        }

        public void uncanonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) {
            MockContentProvider.this.uncanonicalizeAsync(uri, callback);
        }

        public boolean refresh(AttributionSource attributionSource, Uri url, Bundle args, ICancellationSignal cancellationSignal) throws RemoteException {
            return MockContentProvider.this.refresh(url, args);
        }

        public int checkUriPermission(AttributionSource attributionSource, Uri uri, int uid, int modeFlags) {
            return MockContentProvider.this.checkUriPermission(uri, uid, modeFlags);
        }
    }

    protected MockContentProvider() {
        super(new MockContext(), "", "", null);
        this.mIContentProvider = new InversionIContentProvider();
    }

    public MockContentProvider(Context context) {
        super(context, "", "", null);
        this.mIContentProvider = new InversionIContentProvider();
    }

    public MockContentProvider(Context context, String readPermission, String writePermission, PathPermission[] pathPermissions) {
        super(context, readPermission, writePermission, pathPermissions);
        this.mIContentProvider = new InversionIContentProvider();
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void getTypeAsync(final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockContentProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MockContentProvider.this.lambda$getTypeAsync$0(uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getTypeAsync$0(Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putString("result", getType(uri));
        remoteCallback.sendResult(bundle);
    }

    public String getTypeAnonymous(Uri uri) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void getTypeAnonymousAsync(final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockContentProvider$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MockContentProvider.this.lambda$getTypeAnonymousAsync$1(uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getTypeAnonymousAsync$1(Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putString("result", getTypeAnonymous(uri));
        remoteCallback.sendResult(bundle);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] values) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    @Override // android.content.ContentProvider
    public Bundle call(String method, String request, Bundle args) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }

    @Override // android.content.ContentProvider
    public String[] getStreamTypes(Uri url, String mimeTypeFilter) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openTypedAssetFile(Uri url, String mimeType, Bundle opts) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }

    public void canonicalizeAsync(final Uri uri, final RemoteCallback callback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockContentProvider$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MockContentProvider.this.lambda$canonicalizeAsync$2(uri, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$canonicalizeAsync$2(Uri uri, RemoteCallback callback) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("result", canonicalize(uri));
        callback.sendResult(bundle);
    }

    public void uncanonicalizeAsync(final Uri uri, final RemoteCallback callback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockContentProvider$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MockContentProvider.this.lambda$uncanonicalizeAsync$3(uri, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$uncanonicalizeAsync$3(Uri uri, RemoteCallback callback) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("result", uncanonicalize(uri));
        callback.sendResult(bundle);
    }

    public boolean refresh(Uri url, Bundle args) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }

    public int checkUriPermission(Uri uri, int uid, int modeFlags) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }

    public final IContentProvider getIContentProvider() {
        return this.mIContentProvider;
    }

    public IBinder getIContentProviderBinder() {
        return new Binder();
    }

    @Deprecated
    public static void attachInfoForTesting(ContentProvider provider, Context context, ProviderInfo providerInfo) {
        provider.attachInfoForTesting(context, providerInfo);
    }
}
