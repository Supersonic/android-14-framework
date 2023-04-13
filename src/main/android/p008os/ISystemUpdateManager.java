package android.p008os;
/* renamed from: android.os.ISystemUpdateManager */
/* loaded from: classes3.dex */
public interface ISystemUpdateManager extends IInterface {
    Bundle retrieveSystemUpdateInfo() throws RemoteException;

    void updateSystemUpdateInfo(PersistableBundle persistableBundle) throws RemoteException;

    /* renamed from: android.os.ISystemUpdateManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISystemUpdateManager {
        @Override // android.p008os.ISystemUpdateManager
        public Bundle retrieveSystemUpdateInfo() throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemUpdateManager
        public void updateSystemUpdateInfo(PersistableBundle data) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.ISystemUpdateManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISystemUpdateManager {
        public static final String DESCRIPTOR = "android.os.ISystemUpdateManager";
        static final int TRANSACTION_retrieveSystemUpdateInfo = 1;
        static final int TRANSACTION_updateSystemUpdateInfo = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISystemUpdateManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISystemUpdateManager)) {
                return (ISystemUpdateManager) iin;
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
                    return "retrieveSystemUpdateInfo";
                case 2:
                    return "updateSystemUpdateInfo";
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
                            Bundle _result = retrieveSystemUpdateInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            PersistableBundle _arg0 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            data.enforceNoDataAvail();
                            updateSystemUpdateInfo(_arg0);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.ISystemUpdateManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ISystemUpdateManager {
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

            @Override // android.p008os.ISystemUpdateManager
            public Bundle retrieveSystemUpdateInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemUpdateManager
            public void updateSystemUpdateInfo(PersistableBundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
