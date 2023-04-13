package android.service.persistentdata;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IPersistentDataBlockService extends IInterface {
    int getDataBlockSize() throws RemoteException;

    int getFlashLockState() throws RemoteException;

    long getMaximumDataBlockSize() throws RemoteException;

    boolean getOemUnlockEnabled() throws RemoteException;

    String getPersistentDataPackageName() throws RemoteException;

    boolean hasFrpCredentialHandle() throws RemoteException;

    byte[] read() throws RemoteException;

    void setOemUnlockEnabled(boolean z) throws RemoteException;

    void wipe() throws RemoteException;

    int write(byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPersistentDataBlockService {
        @Override // android.service.persistentdata.IPersistentDataBlockService
        public int write(byte[] data) throws RemoteException {
            return 0;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public byte[] read() throws RemoteException {
            return null;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public void wipe() throws RemoteException {
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public int getDataBlockSize() throws RemoteException {
            return 0;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public long getMaximumDataBlockSize() throws RemoteException {
            return 0L;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public void setOemUnlockEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public boolean getOemUnlockEnabled() throws RemoteException {
            return false;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public int getFlashLockState() throws RemoteException {
            return 0;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public boolean hasFrpCredentialHandle() throws RemoteException {
            return false;
        }

        @Override // android.service.persistentdata.IPersistentDataBlockService
        public String getPersistentDataPackageName() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPersistentDataBlockService {
        public static final String DESCRIPTOR = "android.service.persistentdata.IPersistentDataBlockService";
        static final int TRANSACTION_getDataBlockSize = 4;
        static final int TRANSACTION_getFlashLockState = 8;
        static final int TRANSACTION_getMaximumDataBlockSize = 5;
        static final int TRANSACTION_getOemUnlockEnabled = 7;
        static final int TRANSACTION_getPersistentDataPackageName = 10;
        static final int TRANSACTION_hasFrpCredentialHandle = 9;
        static final int TRANSACTION_read = 2;
        static final int TRANSACTION_setOemUnlockEnabled = 6;
        static final int TRANSACTION_wipe = 3;
        static final int TRANSACTION_write = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPersistentDataBlockService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPersistentDataBlockService)) {
                return (IPersistentDataBlockService) iin;
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
                    return "write";
                case 2:
                    return "read";
                case 3:
                    return "wipe";
                case 4:
                    return "getDataBlockSize";
                case 5:
                    return "getMaximumDataBlockSize";
                case 6:
                    return "setOemUnlockEnabled";
                case 7:
                    return "getOemUnlockEnabled";
                case 8:
                    return "getFlashLockState";
                case 9:
                    return "hasFrpCredentialHandle";
                case 10:
                    return "getPersistentDataPackageName";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            int _result = write(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            byte[] _result2 = read();
                            reply.writeNoException();
                            reply.writeByteArray(_result2);
                            break;
                        case 3:
                            wipe();
                            reply.writeNoException();
                            break;
                        case 4:
                            int _result3 = getDataBlockSize();
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 5:
                            long _result4 = getMaximumDataBlockSize();
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 6:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setOemUnlockEnabled(_arg02);
                            reply.writeNoException();
                            break;
                        case 7:
                            boolean _result5 = getOemUnlockEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            int _result6 = getFlashLockState();
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 9:
                            boolean _result7 = hasFrpCredentialHandle();
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 10:
                            String _result8 = getPersistentDataPackageName();
                            reply.writeNoException();
                            reply.writeString(_result8);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPersistentDataBlockService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public int write(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public byte[] read() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public void wipe() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public int getDataBlockSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public long getMaximumDataBlockSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public void setOemUnlockEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public boolean getOemUnlockEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public int getFlashLockState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public boolean hasFrpCredentialHandle() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.persistentdata.IPersistentDataBlockService
            public String getPersistentDataPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
