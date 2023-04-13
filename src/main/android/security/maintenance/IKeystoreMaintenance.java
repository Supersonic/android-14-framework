package android.security.maintenance;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.system.keystore2.KeyDescriptor;
/* loaded from: classes3.dex */
public interface IKeystoreMaintenance extends IInterface {
    public static final String DESCRIPTOR = "android$security$maintenance$IKeystoreMaintenance".replace('$', '.');

    void clearNamespace(int i, long j) throws RemoteException;

    void deleteAllKeys() throws RemoteException;

    void earlyBootEnded() throws RemoteException;

    int getState(int i) throws RemoteException;

    void migrateKeyNamespace(KeyDescriptor keyDescriptor, KeyDescriptor keyDescriptor2) throws RemoteException;

    void onDeviceOffBody() throws RemoteException;

    void onUserAdded(int i) throws RemoteException;

    void onUserPasswordChanged(int i, byte[] bArr) throws RemoteException;

    void onUserRemoved(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IKeystoreMaintenance {
        @Override // android.security.maintenance.IKeystoreMaintenance
        public void onUserAdded(int userId) throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void onUserRemoved(int userId) throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void onUserPasswordChanged(int userId, byte[] password) throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void clearNamespace(int domain, long nspace) throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public int getState(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void earlyBootEnded() throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void onDeviceOffBody() throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void migrateKeyNamespace(KeyDescriptor source, KeyDescriptor destination) throws RemoteException {
        }

        @Override // android.security.maintenance.IKeystoreMaintenance
        public void deleteAllKeys() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IKeystoreMaintenance {
        static final int TRANSACTION_clearNamespace = 4;
        static final int TRANSACTION_deleteAllKeys = 9;
        static final int TRANSACTION_earlyBootEnded = 6;
        static final int TRANSACTION_getState = 5;
        static final int TRANSACTION_migrateKeyNamespace = 8;
        static final int TRANSACTION_onDeviceOffBody = 7;
        static final int TRANSACTION_onUserAdded = 1;
        static final int TRANSACTION_onUserPasswordChanged = 3;
        static final int TRANSACTION_onUserRemoved = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeystoreMaintenance asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeystoreMaintenance)) {
                return (IKeystoreMaintenance) iin;
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
                    return "onUserAdded";
                case 2:
                    return "onUserRemoved";
                case 3:
                    return "onUserPasswordChanged";
                case 4:
                    return "clearNamespace";
                case 5:
                    return "getState";
                case 6:
                    return "earlyBootEnded";
                case 7:
                    return "onDeviceOffBody";
                case 8:
                    return "migrateKeyNamespace";
                case 9:
                    return "deleteAllKeys";
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
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onUserAdded(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onUserRemoved(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onUserPasswordChanged(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            long _arg12 = data.readLong();
                            data.enforceNoDataAvail();
                            clearNamespace(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = getState(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 6:
                            earlyBootEnded();
                            reply.writeNoException();
                            break;
                        case 7:
                            onDeviceOffBody();
                            reply.writeNoException();
                            break;
                        case 8:
                            KeyDescriptor _arg06 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyDescriptor _arg13 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            migrateKeyNamespace(_arg06, _arg13);
                            reply.writeNoException();
                            break;
                        case 9:
                            deleteAllKeys();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IKeystoreMaintenance {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void onUserAdded(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void onUserRemoved(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void onUserPasswordChanged(int userId, byte[] password) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeByteArray(password);
                    this.mRemote.transact(3, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void clearNamespace(int domain, long nspace) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(domain);
                    _data.writeLong(nspace);
                    this.mRemote.transact(4, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public int getState(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 32);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void earlyBootEnded() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void onDeviceOffBody() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void migrateKeyNamespace(KeyDescriptor source, KeyDescriptor destination) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(source, 0);
                    _data.writeTypedObject(destination, 0);
                    this.mRemote.transact(8, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.maintenance.IKeystoreMaintenance
            public void deleteAllKeys() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
