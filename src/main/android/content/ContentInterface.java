package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ContentInterface {
    ContentProviderResult[] applyBatch(String str, ArrayList<ContentProviderOperation> arrayList) throws RemoteException, OperationApplicationException;

    int bulkInsert(Uri uri, ContentValues[] contentValuesArr) throws RemoteException;

    Bundle call(String str, String str2, String str3, Bundle bundle) throws RemoteException;

    Uri canonicalize(Uri uri) throws RemoteException;

    int checkUriPermission(Uri uri, int i, int i2) throws RemoteException;

    int delete(Uri uri, Bundle bundle) throws RemoteException;

    String[] getStreamTypes(Uri uri, String str) throws RemoteException;

    String getType(Uri uri) throws RemoteException;

    Uri insert(Uri uri, ContentValues contentValues, Bundle bundle) throws RemoteException;

    AssetFileDescriptor openAssetFile(Uri uri, String str, CancellationSignal cancellationSignal) throws RemoteException, FileNotFoundException;

    ParcelFileDescriptor openFile(Uri uri, String str, CancellationSignal cancellationSignal) throws RemoteException, FileNotFoundException;

    AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws RemoteException, FileNotFoundException;

    Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) throws RemoteException;

    boolean refresh(Uri uri, Bundle bundle, CancellationSignal cancellationSignal) throws RemoteException;

    Uri uncanonicalize(Uri uri) throws RemoteException;

    int update(Uri uri, ContentValues contentValues, Bundle bundle) throws RemoteException;
}
