package android.p008os;
/* renamed from: android.os.IThermalStatusListener */
/* loaded from: classes3.dex */
public interface IThermalStatusListener extends IInterface {
    public static final String DESCRIPTOR = "android.os.IThermalStatusListener";

    void onStatusChange(int i) throws RemoteException;

    /* renamed from: android.os.IThermalStatusListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IThermalStatusListener {
        @Override // android.p008os.IThermalStatusListener
        public void onStatusChange(int status) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IThermalStatusListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IThermalStatusListener {
        static final int TRANSACTION_onStatusChange = 1;

        public Stub() {
            attachInterface(this, IThermalStatusListener.DESCRIPTOR);
        }

        public static IThermalStatusListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IThermalStatusListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IThermalStatusListener)) {
                return (IThermalStatusListener) iin;
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
                    return "onStatusChange";
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
                data.enforceInterface(IThermalStatusListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IThermalStatusListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onStatusChange(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IThermalStatusListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IThermalStatusListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IThermalStatusListener.DESCRIPTOR;
            }

            @Override // android.p008os.IThermalStatusListener
            public void onStatusChange(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IThermalStatusListener.DESCRIPTOR);
                    _data.writeInt(status);
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
