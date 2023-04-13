package android.p008os;
/* renamed from: android.os.IClientCallback */
/* loaded from: classes3.dex */
public interface IClientCallback extends IInterface {
    public static final String DESCRIPTOR = "android.os.IClientCallback";

    void onClients(IBinder iBinder, boolean z) throws RemoteException;

    /* renamed from: android.os.IClientCallback$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IClientCallback {
        @Override // android.p008os.IClientCallback
        public void onClients(IBinder registered, boolean hasClients) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IClientCallback$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IClientCallback {
        static final int TRANSACTION_onClients = 1;

        public Stub() {
            attachInterface(this, IClientCallback.DESCRIPTOR);
        }

        public static IClientCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IClientCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IClientCallback)) {
                return (IClientCallback) iin;
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
                    return "onClients";
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
                data.enforceInterface(IClientCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IClientCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onClients(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IClientCallback$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IClientCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IClientCallback.DESCRIPTOR;
            }

            @Override // android.p008os.IClientCallback
            public void onClients(IBinder registered, boolean hasClients) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IClientCallback.DESCRIPTOR);
                    _data.writeStrongBinder(registered);
                    _data.writeBoolean(hasClients);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
