package android.p008os;
/* renamed from: android.os.IDeviceIdentifiersPolicyService */
/* loaded from: classes3.dex */
public interface IDeviceIdentifiersPolicyService extends IInterface {
    String getSerial() throws RemoteException;

    String getSerialForPackage(String str, String str2) throws RemoteException;

    /* renamed from: android.os.IDeviceIdentifiersPolicyService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IDeviceIdentifiersPolicyService {
        @Override // android.p008os.IDeviceIdentifiersPolicyService
        public String getSerial() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IDeviceIdentifiersPolicyService
        public String getSerialForPackage(String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IDeviceIdentifiersPolicyService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDeviceIdentifiersPolicyService {
        public static final String DESCRIPTOR = "android.os.IDeviceIdentifiersPolicyService";
        static final int TRANSACTION_getSerial = 1;
        static final int TRANSACTION_getSerialForPackage = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDeviceIdentifiersPolicyService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDeviceIdentifiersPolicyService)) {
                return (IDeviceIdentifiersPolicyService) iin;
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
                    return "getSerial";
                case 2:
                    return "getSerialForPackage";
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
                            String _result = getSerial();
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            String _result2 = getSerialForPackage(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IDeviceIdentifiersPolicyService$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IDeviceIdentifiersPolicyService {
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

            @Override // android.p008os.IDeviceIdentifiersPolicyService
            public String getSerial() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IDeviceIdentifiersPolicyService
            public String getSerialForPackage(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(2, _data, _reply, 0);
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
            return 1;
        }
    }
}
