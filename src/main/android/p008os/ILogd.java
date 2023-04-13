package android.p008os;
/* renamed from: android.os.ILogd */
/* loaded from: classes3.dex */
public interface ILogd extends IInterface {
    public static final String DESCRIPTOR = "android.os.ILogd";

    void approve(int i, int i2, int i3, int i4) throws RemoteException;

    void decline(int i, int i2, int i3, int i4) throws RemoteException;

    /* renamed from: android.os.ILogd$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ILogd {
        @Override // android.p008os.ILogd
        public void approve(int uid, int gid, int pid, int fd) throws RemoteException {
        }

        @Override // android.p008os.ILogd
        public void decline(int uid, int gid, int pid, int fd) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.ILogd$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ILogd {
        static final int TRANSACTION_approve = 1;
        static final int TRANSACTION_decline = 2;

        public Stub() {
            attachInterface(this, ILogd.DESCRIPTOR);
        }

        public static ILogd asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILogd.DESCRIPTOR);
            if (iin != null && (iin instanceof ILogd)) {
                return (ILogd) iin;
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
                    return "approve";
                case 2:
                    return "decline";
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
                data.enforceInterface(ILogd.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILogd.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            approve(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            decline(_arg02, _arg12, _arg22, _arg32);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.ILogd$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ILogd {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILogd.DESCRIPTOR;
            }

            @Override // android.p008os.ILogd
            public void approve(int uid, int gid, int pid, int fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILogd.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(gid);
                    _data.writeInt(pid);
                    _data.writeInt(fd);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.ILogd
            public void decline(int uid, int gid, int pid, int fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILogd.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(gid);
                    _data.writeInt(pid);
                    _data.writeInt(fd);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
