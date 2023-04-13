package android.test.mock;

import android.content.AttributionSource;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.EntityIterator;
import android.content.IContentProvider;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class MockIContentProvider implements IContentProvider {
    public int bulkInsert(AttributionSource attributionSource, Uri url, ContentValues[] initialValues) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public int delete(AttributionSource attributionSource, Uri url, Bundle extras) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public String getType(AttributionSource attributionSource, Uri url) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void getTypeAsync(final AttributionSource attributionSource, final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockIContentProvider$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MockIContentProvider.this.lambda$getTypeAsync$0(attributionSource, uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getTypeAsync$0(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putString("result", getType(attributionSource, uri));
        remoteCallback.sendResult(bundle);
    }

    public String getTypeAnonymous(Uri url) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void getTypeAnonymousAsync(final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockIContentProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MockIContentProvider.this.lambda$getTypeAnonymousAsync$1(uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getTypeAnonymousAsync$1(Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putString("result", getTypeAnonymous(uri));
        remoteCallback.sendResult(bundle);
    }

    public Uri insert(AttributionSource attributionSource, Uri url, ContentValues initialValues, Bundle extras) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public ParcelFileDescriptor openFile(AttributionSource attributionSource, Uri url, String mode, ICancellationSignal signal) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public AssetFileDescriptor openAssetFile(AttributionSource attributionSource, Uri uri, String mode, ICancellationSignal signal) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public ContentProviderResult[] applyBatch(AttributionSource attributionSource, String authority, ArrayList<ContentProviderOperation> operations) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public Cursor query(AttributionSource attributionSource, Uri url, String[] projection, Bundle queryArgs, ICancellationSignal cancellationSignal) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public EntityIterator queryEntities(Uri url, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public int update(AttributionSource attributionSource, Uri url, ContentValues values, Bundle extras) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public Bundle call(AttributionSource attributionSource, String authority, String method, String request, Bundle args) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public IBinder asBinder() {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public String[] getStreamTypes(Uri url, String mimeTypeFilter) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource, Uri url, String mimeType, Bundle opts, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public ICancellationSignal createCancellationSignal() throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public Uri canonicalize(AttributionSource attributionSource, Uri uri) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void canonicalizeAsync(final AttributionSource attributionSource, final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockIContentProvider$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MockIContentProvider.this.lambda$canonicalizeAsync$2(attributionSource, uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$canonicalizeAsync$2(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("result", canonicalize(attributionSource, uri));
        remoteCallback.sendResult(bundle);
    }

    public Uri uncanonicalize(AttributionSource attributionSource, Uri uri) {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public void uncanonicalizeAsync(final AttributionSource attributionSource, final Uri uri, final RemoteCallback remoteCallback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() { // from class: android.test.mock.MockIContentProvider$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MockIContentProvider.this.lambda$uncanonicalizeAsync$3(attributionSource, uri, remoteCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$uncanonicalizeAsync$3(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("result", uncanonicalize(attributionSource, uri));
        remoteCallback.sendResult(bundle);
    }

    public boolean refresh(AttributionSource attributionSource, Uri url, Bundle args, ICancellationSignal cancellationSignal) throws RemoteException {
        throw new UnsupportedOperationException("unimplemented mock method");
    }

    public int checkUriPermission(AttributionSource attributionSource, Uri uri, int uid, int modeFlags) {
        throw new UnsupportedOperationException("unimplemented mock method call");
    }
}
