package android.app.blob;

import android.app.blob.IBlobCommitCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBlobStoreSession extends IInterface {
    public static final String DESCRIPTOR = "android.app.blob.IBlobStoreSession";

    void abandon() throws RemoteException;

    void allowPackageAccess(String str, byte[] bArr) throws RemoteException;

    void allowPublicAccess() throws RemoteException;

    void allowSameSignatureAccess() throws RemoteException;

    void close() throws RemoteException;

    void commit(IBlobCommitCallback iBlobCommitCallback) throws RemoteException;

    long getSize() throws RemoteException;

    boolean isPackageAccessAllowed(String str, byte[] bArr) throws RemoteException;

    boolean isPublicAccessAllowed() throws RemoteException;

    boolean isSameSignatureAccessAllowed() throws RemoteException;

    ParcelFileDescriptor openRead() throws RemoteException;

    ParcelFileDescriptor openWrite(long j, long j2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBlobStoreSession {
        @Override // android.app.blob.IBlobStoreSession
        public ParcelFileDescriptor openWrite(long offsetBytes, long lengthBytes) throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreSession
        public ParcelFileDescriptor openRead() throws RemoteException {
            return null;
        }

        @Override // android.app.blob.IBlobStoreSession
        public void allowPackageAccess(String packageName, byte[] certificate) throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreSession
        public void allowSameSignatureAccess() throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreSession
        public void allowPublicAccess() throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreSession
        public boolean isPackageAccessAllowed(String packageName, byte[] certificate) throws RemoteException {
            return false;
        }

        @Override // android.app.blob.IBlobStoreSession
        public boolean isSameSignatureAccessAllowed() throws RemoteException {
            return false;
        }

        @Override // android.app.blob.IBlobStoreSession
        public boolean isPublicAccessAllowed() throws RemoteException {
            return false;
        }

        @Override // android.app.blob.IBlobStoreSession
        public long getSize() throws RemoteException {
            return 0L;
        }

        @Override // android.app.blob.IBlobStoreSession
        public void close() throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreSession
        public void abandon() throws RemoteException {
        }

        @Override // android.app.blob.IBlobStoreSession
        public void commit(IBlobCommitCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBlobStoreSession {
        static final int TRANSACTION_abandon = 11;
        static final int TRANSACTION_allowPackageAccess = 3;
        static final int TRANSACTION_allowPublicAccess = 5;
        static final int TRANSACTION_allowSameSignatureAccess = 4;
        static final int TRANSACTION_close = 10;
        static final int TRANSACTION_commit = 12;
        static final int TRANSACTION_getSize = 9;
        static final int TRANSACTION_isPackageAccessAllowed = 6;
        static final int TRANSACTION_isPublicAccessAllowed = 8;
        static final int TRANSACTION_isSameSignatureAccessAllowed = 7;
        static final int TRANSACTION_openRead = 2;
        static final int TRANSACTION_openWrite = 1;

        public Stub() {
            attachInterface(this, IBlobStoreSession.DESCRIPTOR);
        }

        public static IBlobStoreSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBlobStoreSession.DESCRIPTOR);
            if (iin != null && (iin instanceof IBlobStoreSession)) {
                return (IBlobStoreSession) iin;
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
                    return "openWrite";
                case 2:
                    return "openRead";
                case 3:
                    return "allowPackageAccess";
                case 4:
                    return "allowSameSignatureAccess";
                case 5:
                    return "allowPublicAccess";
                case 6:
                    return "isPackageAccessAllowed";
                case 7:
                    return "isSameSignatureAccessAllowed";
                case 8:
                    return "isPublicAccessAllowed";
                case 9:
                    return "getSize";
                case 10:
                    return "close";
                case 11:
                    return "abandon";
                case 12:
                    return "commit";
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
                data.enforceInterface(IBlobStoreSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBlobStoreSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            long _arg1 = data.readLong();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result = openWrite(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ParcelFileDescriptor _result2 = openRead();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            byte[] _arg12 = data.createByteArray();
                            data.enforceNoDataAvail();
                            allowPackageAccess(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 4:
                            allowSameSignatureAccess();
                            reply.writeNoException();
                            break;
                        case 5:
                            allowPublicAccess();
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg03 = data.readString();
                            byte[] _arg13 = data.createByteArray();
                            data.enforceNoDataAvail();
                            boolean _result3 = isPackageAccessAllowed(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 7:
                            boolean _result4 = isSameSignatureAccessAllowed();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            boolean _result5 = isPublicAccessAllowed();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 9:
                            long _result6 = getSize();
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            break;
                        case 10:
                            close();
                            reply.writeNoException();
                            break;
                        case 11:
                            abandon();
                            reply.writeNoException();
                            break;
                        case 12:
                            IBlobCommitCallback _arg04 = IBlobCommitCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            commit(_arg04);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBlobStoreSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBlobStoreSession.DESCRIPTOR;
            }

            @Override // android.app.blob.IBlobStoreSession
            public ParcelFileDescriptor openWrite(long offsetBytes, long lengthBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    _data.writeLong(offsetBytes);
                    _data.writeLong(lengthBytes);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public ParcelFileDescriptor openRead() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void allowPackageAccess(String packageName, byte[] certificate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeByteArray(certificate);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void allowSameSignatureAccess() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void allowPublicAccess() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public boolean isPackageAccessAllowed(String packageName, byte[] certificate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeByteArray(certificate);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public boolean isSameSignatureAccessAllowed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public boolean isPublicAccessAllowed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public long getSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void abandon() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.blob.IBlobStoreSession
            public void commit(IBlobCommitCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBlobStoreSession.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
