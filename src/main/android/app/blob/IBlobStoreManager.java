package android.app.blob;

import android.app.blob.IBlobStoreSession;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes.dex */
public interface IBlobStoreManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.blob.IBlobStoreManager";

    void abandonSession(long j, String str) throws RemoteException;

    void acquireLease(BlobHandle blobHandle, int i, CharSequence charSequence, long j, String str) throws RemoteException;

    long createSession(BlobHandle blobHandle, String str) throws RemoteException;

    void deleteBlob(long j) throws RemoteException;

    LeaseInfo getLeaseInfo(BlobHandle blobHandle, String str) throws RemoteException;

    List<BlobHandle> getLeasedBlobs(String str) throws RemoteException;

    long getRemainingLeaseQuotaBytes(String str) throws RemoteException;

    ParcelFileDescriptor openBlob(BlobHandle blobHandle, String str) throws RemoteException;

    IBlobStoreSession openSession(long j, String str) throws RemoteException;

    List<BlobInfo> queryBlobsForUser(int i) throws RemoteException;

    void releaseAllLeases(String str) throws RemoteException;

    void releaseLease(BlobHandle blobHandle, String str) throws RemoteException;

    void waitForIdle(RemoteCallback remoteCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBlobStoreManager {
        @Override // android.app.blob.IBlobStoreManager
        public long createSession(BlobHandle handle, String packageName) throws RemoteException {
            return 0L;
        }

        @Override // android.app.blob.IBlobStoreManager
        public IBlobStoreSession openSession(long sessionId, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreManager
        public ParcelFileDescriptor openBlob(BlobHandle handle, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreManager
        public void abandonSession(long sessionId, String packageName) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public void acquireLease(BlobHandle handle, int descriptionResId, CharSequence description, long leaseTimeoutMillis, String packageName) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public void releaseLease(BlobHandle handle, String packageName) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public void releaseAllLeases(String packageName) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public long getRemainingLeaseQuotaBytes(String packageName) throws RemoteException {
            return 0L;
        }

        @Override // android.app.blob.IBlobStoreManager
        public void waitForIdle(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public List<BlobInfo> queryBlobsForUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreManager
        public void deleteBlob(long blobId) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreManager
        public List<BlobHandle> getLeasedBlobs(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreManager
        public LeaseInfo getLeaseInfo(BlobHandle blobHandle, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBlobStoreManager {
        static final int TRANSACTION_abandonSession = 4;
        static final int TRANSACTION_acquireLease = 5;
        static final int TRANSACTION_createSession = 1;
        static final int TRANSACTION_deleteBlob = 11;
        static final int TRANSACTION_getLeaseInfo = 13;
        static final int TRANSACTION_getLeasedBlobs = 12;
        static final int TRANSACTION_getRemainingLeaseQuotaBytes = 8;
        static final int TRANSACTION_openBlob = 3;
        static final int TRANSACTION_openSession = 2;
        static final int TRANSACTION_queryBlobsForUser = 10;
        static final int TRANSACTION_releaseAllLeases = 7;
        static final int TRANSACTION_releaseLease = 6;
        static final int TRANSACTION_waitForIdle = 9;

        public Stub() {
            attachInterface(this, IBlobStoreManager.DESCRIPTOR);
        }

        public static IBlobStoreManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBlobStoreManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IBlobStoreManager)) {
                return (IBlobStoreManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "createSession";
                case 2:
                    return "openSession";
                case 3:
                    return "openBlob";
                case 4:
                    return "abandonSession";
                case 5:
                    return "acquireLease";
                case 6:
                    return "releaseLease";
                case 7:
                    return "releaseAllLeases";
                case 8:
                    return "getRemainingLeaseQuotaBytes";
                case 9:
                    return "waitForIdle";
                case 10:
                    return "queryBlobsForUser";
                case 11:
                    return "deleteBlob";
                case 12:
                    return "getLeasedBlobs";
                case 13:
                    return "getLeaseInfo";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IBlobStoreManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBlobStoreManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            BlobHandle _arg0 = (BlobHandle) data.readTypedObject(BlobHandle.CREATOR);
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            long _result = createSession(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeLong(_result);
                            break;
                        case 2:
                            long _arg02 = data.readLong();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            IBlobStoreSession _result2 = openSession(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            BlobHandle _arg03 = (BlobHandle) data.readTypedObject(BlobHandle.CREATOR);
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result3 = openBlob(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            long _arg04 = data.readLong();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            abandonSession(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            BlobHandle _arg05 = (BlobHandle) data.readTypedObject(BlobHandle.CREATOR);
                            int _arg15 = data.readInt();
                            CharSequence _arg2 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            long _arg3 = data.readLong();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            acquireLease(_arg05, _arg15, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 6:
                            BlobHandle _arg06 = (BlobHandle) data.readTypedObject(BlobHandle.CREATOR);
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            releaseLease(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            releaseAllLeases(_arg07);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            long _result4 = getRemainingLeaseQuotaBytes(_arg08);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 9:
                            RemoteCallback _arg09 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            waitForIdle(_arg09);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            List<BlobInfo> _result5 = queryBlobsForUser(_arg010);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        case 11:
                            long _arg011 = data.readLong();
                            data.enforceNoDataAvail();
                            deleteBlob(_arg011);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            data.enforceNoDataAvail();
                            List<BlobHandle> _result6 = getLeasedBlobs(_arg012);
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        case 13:
                            BlobHandle _arg013 = (BlobHandle) data.readTypedObject(BlobHandle.CREATOR);
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            LeaseInfo _result7 = getLeaseInfo(_arg013, _arg17);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBlobStoreManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBlobStoreManager.DESCRIPTOR;
            }

            @Override // android.app.blob.IBlobStoreManager
            public long createSession(BlobHandle handle, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(handle, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public IBlobStoreSession openSession(long sessionId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IBlobStoreSession _result = IBlobStoreSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public ParcelFileDescriptor openBlob(BlobHandle handle, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(handle, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void abandonSession(long sessionId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeString(packageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void acquireLease(BlobHandle handle, int descriptionResId, CharSequence description, long leaseTimeoutMillis, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(handle, 0);
                    _data.writeInt(descriptionResId);
                    if (description != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(description, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(leaseTimeoutMillis);
                    _data.writeString(packageName);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void releaseLease(BlobHandle handle, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(handle, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void releaseAllLeases(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public long getRemainingLeaseQuotaBytes(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void waitForIdle(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public List<BlobInfo> queryBlobsForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    List<BlobInfo> _result = _reply.createTypedArrayList(BlobInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public void deleteBlob(long blobId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeLong(blobId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public List<BlobHandle> getLeasedBlobs(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    List<BlobHandle> _result = _reply.createTypedArrayList(BlobHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreManager
            public LeaseInfo getLeaseInfo(BlobHandle blobHandle, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreManager.DESCRIPTOR);
                    _data.writeTypedObject(blobHandle, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    LeaseInfo _result = (LeaseInfo) _reply.readTypedObject(LeaseInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
