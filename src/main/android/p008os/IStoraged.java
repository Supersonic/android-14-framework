package android.p008os;
/* renamed from: android.os.IStoraged */
/* loaded from: classes3.dex */
public interface IStoraged extends IInterface {
    int getRecentPerf() throws RemoteException;

    void onUserStarted(int i) throws RemoteException;

    void onUserStopped(int i) throws RemoteException;

    /* renamed from: android.os.IStoraged$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IStoraged {
        @Override // android.p008os.IStoraged
        public void onUserStarted(int userId) throws RemoteException {
        }

        @Override // android.p008os.IStoraged
        public void onUserStopped(int userId) throws RemoteException {
        }

        @Override // android.p008os.IStoraged
        public int getRecentPerf() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IStoraged$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStoraged {
        public static final String DESCRIPTOR = "android.os.IStoraged";
        static final int TRANSACTION_getRecentPerf = 3;
        static final int TRANSACTION_onUserStarted = 1;
        static final int TRANSACTION_onUserStopped = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStoraged asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStoraged)) {
                return (IStoraged) iin;
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
                    return "onUserStarted";
                case 2:
                    return "onUserStopped";
                case 3:
                    return "getRecentPerf";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onUserStarted(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onUserStopped(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _result = getRecentPerf();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IStoraged$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IStoraged {
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

            @Override // android.p008os.IStoraged
            public void onUserStarted(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IStoraged
            public void onUserStopped(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IStoraged
            public int getRecentPerf() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
