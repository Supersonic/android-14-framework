package android.p008os;
/* renamed from: android.os.IThermalEventListener */
/* loaded from: classes3.dex */
public interface IThermalEventListener extends IInterface {
    void notifyThrottling(Temperature temperature) throws RemoteException;

    /* renamed from: android.os.IThermalEventListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IThermalEventListener {
        @Override // android.p008os.IThermalEventListener
        public void notifyThrottling(Temperature temperature) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IThermalEventListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IThermalEventListener {
        public static final String DESCRIPTOR = "android.os.IThermalEventListener";
        static final int TRANSACTION_notifyThrottling = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IThermalEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IThermalEventListener)) {
                return (IThermalEventListener) iin;
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
                    return "notifyThrottling";
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
                            Temperature _arg0 = (Temperature) data.readTypedObject(Temperature.CREATOR);
                            data.enforceNoDataAvail();
                            notifyThrottling(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IThermalEventListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IThermalEventListener {
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

            @Override // android.p008os.IThermalEventListener
            public void notifyThrottling(Temperature temperature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(temperature, 0);
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
