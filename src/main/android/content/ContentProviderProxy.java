package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.BulkCursorDescriptor;
import android.database.BulkCursorToCursorAdaptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ContentProviderNative.java */
/* loaded from: classes.dex */
public final class ContentProviderProxy implements IContentProvider {
    private IBinder mRemote;

    public ContentProviderProxy(IBinder remote) {
        this.mRemote = remote;
    }

    @Override // android.p008os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    @Override // android.content.IContentProvider
    public Cursor query(AttributionSource attributionSource, Uri url, String[] projection, Bundle queryArgs, ICancellationSignal cancellationSignal) throws RemoteException {
        BulkCursorToCursorAdaptor adaptor = new BulkCursorToCursorAdaptor();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            try {
                data.writeInterfaceToken(IContentProvider.descriptor);
                attributionSource.writeToParcel(data, 0);
                url.writeToParcel(data, 0);
                int length = 0;
                if (projection != null) {
                    length = projection.length;
                }
                data.writeInt(length);
                for (int i = 0; i < length; i++) {
                    data.writeString(projection[i]);
                }
                data.writeBundle(queryArgs);
                data.writeStrongBinder(adaptor.getObserver().asBinder());
                data.writeStrongBinder(cancellationSignal != null ? cancellationSignal.asBinder() : null);
                this.mRemote.transact(1, data, reply, 0);
                DatabaseUtils.readExceptionFromParcel(reply);
                if (reply.readInt() != 0) {
                    BulkCursorDescriptor d = BulkCursorDescriptor.CREATOR.createFromParcel(reply);
                    Binder.copyAllowBlocking(this.mRemote, d.cursor != null ? d.cursor.asBinder() : null);
                    adaptor.initialize(d);
                } else {
                    adaptor.close();
                    adaptor = null;
                }
                return adaptor;
            } catch (RemoteException ex) {
                adaptor.close();
                throw ex;
            } catch (RuntimeException ex2) {
                adaptor.close();
                throw ex2;
            }
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public String getType(AttributionSource attributionSource, Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            this.mRemote.transact(2, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            String out = reply.readString();
            return out;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public void getTypeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) throws RemoteException {
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            uri.writeToParcel(data, 0);
            callback.writeToParcel(data, 0);
            this.mRemote.transact(29, data, null, 1);
        } finally {
            data.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public void getTypeAnonymousAsync(Uri uri, RemoteCallback callback) throws RemoteException {
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            uri.writeToParcel(data, 0);
            callback.writeToParcel(data, 0);
            this.mRemote.transact(32, data, null, 1);
        } finally {
            data.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public Uri insert(AttributionSource attributionSource, Uri url, ContentValues values, Bundle extras) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            values.writeToParcel(data, 0);
            data.writeBundle(extras);
            this.mRemote.transact(3, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            return out;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public int bulkInsert(AttributionSource attributionSource, Uri url, ContentValues[] values) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeTypedArray(values, 0);
            this.mRemote.transact(13, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            return count;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public ContentProviderResult[] applyBatch(AttributionSource attributionSource, String authority, ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            data.writeString(authority);
            data.writeInt(operations.size());
            Iterator<ContentProviderOperation> it = operations.iterator();
            while (it.hasNext()) {
                ContentProviderOperation operation = it.next();
                operation.writeToParcel(data, 0);
            }
            this.mRemote.transact(20, data, reply, 0);
            DatabaseUtils.readExceptionWithOperationApplicationExceptionFromParcel(reply);
            ContentProviderResult[] results = (ContentProviderResult[]) reply.createTypedArray(ContentProviderResult.CREATOR);
            return results;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public int delete(AttributionSource attributionSource, Uri url, Bundle extras) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeBundle(extras);
            this.mRemote.transact(4, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            return count;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public int update(AttributionSource attributionSource, Uri url, ContentValues values, Bundle extras) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            values.writeToParcel(data, 0);
            data.writeBundle(extras);
            this.mRemote.transact(10, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            return count;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public ParcelFileDescriptor openFile(AttributionSource attributionSource, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeString(mode);
            ParcelFileDescriptor fd = null;
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(14, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            if (has != 0) {
                fd = ParcelFileDescriptor.CREATOR.createFromParcel(reply);
            }
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public AssetFileDescriptor openAssetFile(AttributionSource attributionSource, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeString(mode);
            AssetFileDescriptor fd = null;
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(15, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            if (has != 0) {
                fd = AssetFileDescriptor.CREATOR.createFromParcel(reply);
            }
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public Bundle call(AttributionSource attributionSource, String authority, String method, String request, Bundle extras) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            data.writeString(authority);
            data.writeString(method);
            data.writeString(request);
            data.writeBundle(extras);
            this.mRemote.transact(21, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Bundle bundle = reply.readBundle();
            return bundle;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public String[] getStreamTypes(Uri url, String mimeTypeFilter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            url.writeToParcel(data, 0);
            data.writeString(mimeTypeFilter);
            this.mRemote.transact(22, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            String[] out = reply.createStringArray();
            return out;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public AssetFileDescriptor openTypedAssetFile(AttributionSource attributionSource, Uri url, String mimeType, Bundle opts, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeString(mimeType);
            data.writeBundle(opts);
            AssetFileDescriptor fd = null;
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(23, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            if (has != 0) {
                fd = AssetFileDescriptor.CREATOR.createFromParcel(reply);
            }
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public ICancellationSignal createCancellationSignal() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            this.mRemote.transact(24, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            ICancellationSignal cancellationSignal = ICancellationSignal.Stub.asInterface(reply.readStrongBinder());
            return cancellationSignal;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public Uri canonicalize(AttributionSource attributionSource, Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            this.mRemote.transact(25, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            return out;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public void canonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) throws RemoteException {
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            uri.writeToParcel(data, 0);
            callback.writeToParcel(data, 0);
            this.mRemote.transact(30, data, null, 1);
        } finally {
            data.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public Uri uncanonicalize(AttributionSource attributionSource, Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            this.mRemote.transact(26, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            return out;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public void uncanonicalizeAsync(AttributionSource attributionSource, Uri uri, RemoteCallback callback) throws RemoteException {
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            uri.writeToParcel(data, 0);
            callback.writeToParcel(data, 0);
            this.mRemote.transact(31, data, null, 1);
        } finally {
            data.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public boolean refresh(AttributionSource attributionSource, Uri url, Bundle extras, ICancellationSignal signal) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeBundle(extras);
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(27, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int success = reply.readInt();
            return success == 0;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public int checkUriPermission(AttributionSource attributionSource, Uri url, int uid, int modeFlags) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            attributionSource.writeToParcel(data, 0);
            url.writeToParcel(data, 0);
            data.writeInt(uid);
            data.writeInt(modeFlags);
            this.mRemote.transact(28, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            return reply.readInt();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
