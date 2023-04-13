package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.BulkCursorDescriptor;
import android.database.Cursor;
import android.database.CursorToBulkCursorAdaptor;
import android.database.DatabaseUtils;
import android.database.IContentObserver;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class ContentProviderNative extends Binder implements IContentProvider {
    public abstract String getProviderName();

    public ContentProviderNative() {
        attachInterface(this, IContentProvider.descriptor);
    }

    public static IContentProvider asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IContentProvider in = (IContentProvider) obj.queryLocalInterface(IContentProvider.descriptor);
        if (in != null) {
            return in;
        }
        return new ContentProviderProxy(obj);
    }

    @Override // android.p008os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        String[] projection;
        int i = 0;
        try {
            switch (code) {
                case 1:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url = Uri.CREATOR.createFromParcel(data);
                    int num = data.readInt();
                    if (num <= 0) {
                        projection = null;
                    } else {
                        String[] projection2 = new String[num];
                        for (int i2 = 0; i2 < num; i2++) {
                            projection2[i2] = data.readString();
                        }
                        projection = projection2;
                    }
                    Bundle queryArgs = data.readBundle();
                    IContentObserver observer = IContentObserver.Stub.asInterface(data.readStrongBinder());
                    ICancellationSignal cancellationSignal = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                    Cursor cursor = query(attributionSource, url, projection, queryArgs, cancellationSignal);
                    if (cursor != null) {
                        CursorToBulkCursorAdaptor adaptor = new CursorToBulkCursorAdaptor(cursor, observer, getProviderName());
                        Cursor cursor2 = null;
                        BulkCursorDescriptor d = adaptor.getBulkCursorDescriptor();
                        CursorToBulkCursorAdaptor adaptor2 = null;
                        reply.writeNoException();
                        reply.writeInt(1);
                        d.writeToParcel(reply, 1);
                        if (0 != 0) {
                            adaptor2.close();
                        }
                        if (0 != 0) {
                            cursor2.close();
                        }
                    } else {
                        reply.writeNoException();
                        reply.writeInt(0);
                    }
                    return true;
                case 2:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource2 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url2 = Uri.CREATOR.createFromParcel(data);
                    String type = getType(attributionSource2, url2);
                    reply.writeNoException();
                    reply.writeString(type);
                    return true;
                case 3:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource3 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url3 = Uri.CREATOR.createFromParcel(data);
                    ContentValues values = ContentValues.CREATOR.createFromParcel(data);
                    Bundle extras = data.readBundle();
                    Uri out = insert(attributionSource3, url3, values, extras);
                    reply.writeNoException();
                    Uri.writeToParcel(reply, out);
                    return true;
                case 4:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource4 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url4 = Uri.CREATOR.createFromParcel(data);
                    Bundle extras2 = data.readBundle();
                    int count = delete(attributionSource4, url4, extras2);
                    reply.writeNoException();
                    reply.writeInt(count);
                    return true;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 11:
                case 12:
                case 16:
                case 17:
                case 18:
                case 19:
                default:
                    return super.onTransact(code, data, reply, flags);
                case 10:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource5 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url5 = Uri.CREATOR.createFromParcel(data);
                    ContentValues values2 = ContentValues.CREATOR.createFromParcel(data);
                    Bundle extras3 = data.readBundle();
                    int count2 = update(attributionSource5, url5, values2, extras3);
                    reply.writeNoException();
                    reply.writeInt(count2);
                    return true;
                case 13:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource6 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url6 = Uri.CREATOR.createFromParcel(data);
                    ContentValues[] values3 = (ContentValues[]) data.createTypedArray(ContentValues.CREATOR);
                    int count3 = bulkInsert(attributionSource6, url6, values3);
                    reply.writeNoException();
                    reply.writeInt(count3);
                    return true;
                case 14:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource7 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url7 = Uri.CREATOR.createFromParcel(data);
                    String mode = data.readString();
                    ICancellationSignal signal = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                    ParcelFileDescriptor fd = openFile(attributionSource7, url7, mode, signal);
                    reply.writeNoException();
                    if (fd != null) {
                        reply.writeInt(1);
                        fd.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 15:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource8 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url8 = Uri.CREATOR.createFromParcel(data);
                    String mode2 = data.readString();
                    ICancellationSignal signal2 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                    AssetFileDescriptor fd2 = openAssetFile(attributionSource8, url8, mode2, signal2);
                    reply.writeNoException();
                    if (fd2 != null) {
                        reply.writeInt(1);
                        fd2.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 20:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource9 = AttributionSource.CREATOR.createFromParcel(data);
                    String authority = data.readString();
                    int numOperations = data.readInt();
                    ArrayList<ContentProviderOperation> operations = new ArrayList<>(numOperations);
                    for (int i3 = 0; i3 < numOperations; i3++) {
                        operations.add(i3, ContentProviderOperation.CREATOR.createFromParcel(data));
                    }
                    ContentProviderResult[] results = applyBatch(attributionSource9, authority, operations);
                    reply.writeNoException();
                    reply.writeTypedArray(results, 0);
                    return true;
                case 21:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource10 = AttributionSource.CREATOR.createFromParcel(data);
                    String authority2 = data.readString();
                    String method = data.readString();
                    String stringArg = data.readString();
                    Bundle extras4 = data.readBundle();
                    Bundle responseBundle = call(attributionSource10, authority2, method, stringArg, extras4);
                    reply.writeNoException();
                    reply.writeBundle(responseBundle);
                    return true;
                case 22:
                    data.enforceInterface(IContentProvider.descriptor);
                    Uri url9 = Uri.CREATOR.createFromParcel(data);
                    String mimeTypeFilter = data.readString();
                    String[] types = getStreamTypes(url9, mimeTypeFilter);
                    reply.writeNoException();
                    reply.writeStringArray(types);
                    return true;
                case 23:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource11 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url10 = Uri.CREATOR.createFromParcel(data);
                    String mimeType = data.readString();
                    Bundle opts = data.readBundle();
                    ICancellationSignal signal3 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                    AssetFileDescriptor fd3 = openTypedAssetFile(attributionSource11, url10, mimeType, opts, signal3);
                    reply.writeNoException();
                    if (fd3 != null) {
                        reply.writeInt(1);
                        fd3.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 24:
                    data.enforceInterface(IContentProvider.descriptor);
                    ICancellationSignal cancellationSignal2 = createCancellationSignal();
                    reply.writeNoException();
                    reply.writeStrongBinder(cancellationSignal2.asBinder());
                    return true;
                case 25:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource12 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url11 = Uri.CREATOR.createFromParcel(data);
                    Uri out2 = canonicalize(attributionSource12, url11);
                    reply.writeNoException();
                    Uri.writeToParcel(reply, out2);
                    return true;
                case 26:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource13 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url12 = Uri.CREATOR.createFromParcel(data);
                    Uri out3 = uncanonicalize(attributionSource13, url12);
                    reply.writeNoException();
                    Uri.writeToParcel(reply, out3);
                    return true;
                case 27:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource14 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url13 = Uri.CREATOR.createFromParcel(data);
                    Bundle extras5 = data.readBundle();
                    ICancellationSignal signal4 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                    boolean out4 = refresh(attributionSource14, url13, extras5, signal4);
                    reply.writeNoException();
                    if (!out4) {
                        i = -1;
                    }
                    reply.writeInt(i);
                    return true;
                case 28:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource15 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri uri = Uri.CREATOR.createFromParcel(data);
                    int uid = data.readInt();
                    int modeFlags = data.readInt();
                    int out5 = checkUriPermission(attributionSource15, uri, uid, modeFlags);
                    reply.writeNoException();
                    reply.writeInt(out5);
                    return true;
                case 29:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource16 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri url14 = Uri.CREATOR.createFromParcel(data);
                    RemoteCallback callback = RemoteCallback.CREATOR.createFromParcel(data);
                    getTypeAsync(attributionSource16, url14, callback);
                    return true;
                case 30:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource17 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri uri2 = Uri.CREATOR.createFromParcel(data);
                    RemoteCallback callback2 = RemoteCallback.CREATOR.createFromParcel(data);
                    canonicalizeAsync(attributionSource17, uri2, callback2);
                    return true;
                case 31:
                    data.enforceInterface(IContentProvider.descriptor);
                    AttributionSource attributionSource18 = AttributionSource.CREATOR.createFromParcel(data);
                    Uri uri3 = Uri.CREATOR.createFromParcel(data);
                    RemoteCallback callback3 = RemoteCallback.CREATOR.createFromParcel(data);
                    uncanonicalizeAsync(attributionSource18, uri3, callback3);
                    return true;
                case 32:
                    data.enforceInterface(IContentProvider.descriptor);
                    Uri url15 = Uri.CREATOR.createFromParcel(data);
                    RemoteCallback callback4 = RemoteCallback.CREATOR.createFromParcel(data);
                    getTypeAnonymousAsync(url15, callback4);
                    return true;
            }
        } catch (Exception e) {
            DatabaseUtils.writeExceptionToParcel(reply, e);
            return true;
        }
    }

    @Override // android.p008os.IInterface
    public IBinder asBinder() {
        return this;
    }
}
