package android.p008os;
/* renamed from: android.os.IBatteryPropertiesRegistrar */
/* loaded from: classes3.dex */
public interface IBatteryPropertiesRegistrar extends IInterface {
    int getProperty(int i, BatteryProperty batteryProperty) throws RemoteException;

    void scheduleUpdate() throws RemoteException;

    /* renamed from: android.os.IBatteryPropertiesRegistrar$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IBatteryPropertiesRegistrar {
        @Override // android.p008os.IBatteryPropertiesRegistrar
        public int getProperty(int id, BatteryProperty prop) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IBatteryPropertiesRegistrar
        public void scheduleUpdate() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IBatteryPropertiesRegistrar$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBatteryPropertiesRegistrar {
        public static final String DESCRIPTOR = "android.os.IBatteryPropertiesRegistrar";
        static final int TRANSACTION_getProperty = 1;
        static final int TRANSACTION_scheduleUpdate = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBatteryPropertiesRegistrar asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBatteryPropertiesRegistrar)) {
                return (IBatteryPropertiesRegistrar) iin;
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
                    return "getProperty";
                case 2:
                    return "scheduleUpdate";
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
                            BatteryProperty _arg1 = new BatteryProperty();
                            data.enforceNoDataAvail();
                            int _result = getProperty(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            reply.writeTypedObject(_arg1, 1);
                            break;
                        case 2:
                            scheduleUpdate();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IBatteryPropertiesRegistrar$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IBatteryPropertiesRegistrar {
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

            @Override // android.p008os.IBatteryPropertiesRegistrar
            public int getProperty(int id, BatteryProperty prop) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        prop.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IBatteryPropertiesRegistrar
            public void scheduleUpdate() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
