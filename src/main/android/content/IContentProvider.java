package android.content;

import android.app.AppGlobals;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface IContentProvider extends IInterface {
    public static final int APPLY_BATCH_TRANSACTION = 20;
    public static final int BULK_INSERT_TRANSACTION = 13;
    public static final int CALL_TRANSACTION = 21;
    public static final int CANONICALIZE_ASYNC_TRANSACTION = 30;
    public static final int CANONICALIZE_TRANSACTION = 25;
    public static final int CHECK_URI_PERMISSION_TRANSACTION = 28;
    public static final int CREATE_CANCELATION_SIGNAL_TRANSACTION = 24;
    public static final int DELETE_TRANSACTION = 4;
    public static final int GET_STREAM_TYPES_TRANSACTION = 22;
    public static final int GET_TYPE_ANONYMOUS_ASYNC_TRANSACTION = 32;
    public static final int GET_TYPE_ASYNC_TRANSACTION = 29;
    public static final int GET_TYPE_TRANSACTION = 2;
    public static final int INSERT_TRANSACTION = 3;
    public static final int OPEN_ASSET_FILE_TRANSACTION = 15;
    public static final int OPEN_FILE_TRANSACTION = 14;
    public static final int OPEN_TYPED_ASSET_FILE_TRANSACTION = 23;
    public static final int QUERY_TRANSACTION = 1;
    public static final int REFRESH_TRANSACTION = 27;
    public static final int UNCANONICALIZE_ASYNC_TRANSACTION = 31;
    public static final int UNCANONICALIZE_TRANSACTION = 26;
    public static final int UPDATE_TRANSACTION = 10;
    public static final String descriptor = "android.content.IContentProvider";

    ContentProviderResult[] applyBatch(AttributionSource attributionSource, String str, ArrayList<ContentProviderOperation> arrayList) throws RemoteException, OperationApplicationException;

    int bulkInsert(AttributionSource attributionSource, Uri uri, ContentValues[] contentValuesArr) throws RemoteException;

    Bundle call(AttributionSource attributionSource, String str, String str2, String str3, Bundle bundle) throws RemoteException;

    Uri canonicalize(AttributionSource attributionSource, Uri uri) throws RemoteException;

    void canonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) throws RemoteException;

    int checkUriPermission(AttributionSource attributionSource, Uri uri, int i, int i2) throws RemoteException;

    ICancellationSignal createCancellationSignal() throws RemoteException;

    int delete(AttributionSource attributionSource, Uri uri, Bundle bundle) throws RemoteException;

    String[] getStreamTypes(Uri uri, String str) throws RemoteException;

    String getType(AttributionSource attributionSource, Uri uri) throws RemoteException;

    void getTypeAnonymousAsync(Uri uri, RemoteCallback remoteCallback) throws RemoteException;

    void getTypeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) throws RemoteException;

    Uri insert(AttributionSource attributionSource, Uri uri, ContentValues contentValues, Bundle bundle) throws RemoteException;

    AssetFileDescriptor openAssetFile(AttributionSource attributionSource, Uri uri, String str, ICancellationSignal iCancellationSignal) throws RemoteException, FileNotFoundException;

    ParcelFileDescriptor openFile(AttributionSource attributionSource, Uri uri, String str, ICancellationSignal iCancellationSignal) throws RemoteException, FileNotFoundException;

    AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource, Uri uri, String str, Bundle bundle, ICancellationSignal iCancellationSignal) throws RemoteException, FileNotFoundException;

    Cursor query(AttributionSource attributionSource, Uri uri, String[] strArr, Bundle bundle, ICancellationSignal iCancellationSignal) throws RemoteException;

    boolean refresh(AttributionSource attributionSource, Uri uri, Bundle bundle, ICancellationSignal iCancellationSignal) throws RemoteException;

    Uri uncanonicalize(AttributionSource attributionSource, Uri uri) throws RemoteException;

    void uncanonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback remoteCallback) throws RemoteException;

    int update(AttributionSource attributionSource, Uri uri, ContentValues contentValues, Bundle bundle) throws RemoteException;

    @Deprecated
    default String getType(Uri url) throws RemoteException {
        return getType(new AttributionSource(Binder.getCallingUid(), AppGlobals.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], null), url);
    }

    @Deprecated
    default void getTypeAsync(Uri uri, RemoteCallback callback) throws RemoteException {
        getTypeAsync(new AttributionSource(Binder.getCallingUid(), AppGlobals.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], null), uri, callback);
    }

    @Deprecated
    default Uri insert(String callingPkg, Uri url, ContentValues initialValues) throws RemoteException {
        return insert(new AttributionSource(Binder.getCallingUid(), callingPkg, null), url, initialValues, null);
    }

    @Deprecated
    default int bulkInsert(String callingPkg, Uri url, ContentValues[] initialValues) throws RemoteException {
        return bulkInsert(new AttributionSource(Binder.getCallingUid(), callingPkg, null), url, initialValues);
    }

    @Deprecated
    default int delete(String callingPkg, Uri url, String selection, String[] selectionArgs) throws RemoteException {
        return delete(new AttributionSource(Binder.getCallingUid(), callingPkg, null), url, ContentResolver.createSqlQueryBundle(selection, selectionArgs));
    }

    @Deprecated
    default int update(String callingPkg, Uri url, ContentValues values, String selection, String[] selectionArgs) throws RemoteException {
        return update(new AttributionSource(Binder.getCallingUid(), callingPkg, null), url, values, ContentResolver.createSqlQueryBundle(selection, selectionArgs));
    }

    @Deprecated
    default Bundle call(String callingPkg, String method, String arg, Bundle extras) throws RemoteException {
        return call(new AttributionSource(Binder.getCallingUid(), callingPkg, null), "unknown", method, arg, extras);
    }
}
