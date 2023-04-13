package android.p008os;
/* renamed from: android.os.IHardwarePropertiesManager */
/* loaded from: classes3.dex */
public interface IHardwarePropertiesManager extends IInterface {
    CpuUsageInfo[] getCpuUsages(String str) throws RemoteException;

    float[] getDeviceTemperatures(String str, int i, int i2) throws RemoteException;

    float[] getFanSpeeds(String str) throws RemoteException;

    /* renamed from: android.os.IHardwarePropertiesManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IHardwarePropertiesManager {
        @Override // android.p008os.IHardwarePropertiesManager
        public float[] getDeviceTemperatures(String callingPackage, int type, int source) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IHardwarePropertiesManager
        public CpuUsageInfo[] getCpuUsages(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IHardwarePropertiesManager
        public float[] getFanSpeeds(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IHardwarePropertiesManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IHardwarePropertiesManager {
        public static final String DESCRIPTOR = "android.os.IHardwarePropertiesManager";
        static final int TRANSACTION_getCpuUsages = 2;
        static final int TRANSACTION_getDeviceTemperatures = 1;
        static final int TRANSACTION_getFanSpeeds = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IHardwarePropertiesManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IHardwarePropertiesManager)) {
                return (IHardwarePropertiesManager) iin;
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
                    return "getDeviceTemperatures";
                case 2:
                    return "getCpuUsages";
                case 3:
                    return "getFanSpeeds";
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
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            float[] _result = getDeviceTemperatures(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeFloatArray(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            CpuUsageInfo[] _result2 = getCpuUsages(_arg02);
                            reply.writeNoException();
                            reply.writeTypedArray(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            float[] _result3 = getFanSpeeds(_arg03);
                            reply.writeNoException();
                            reply.writeFloatArray(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IHardwarePropertiesManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IHardwarePropertiesManager {
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

            @Override // android.p008os.IHardwarePropertiesManager
            public float[] getDeviceTemperatures(String callingPackage, int type, int source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(type);
                    _data.writeInt(source);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    float[] _result = _reply.createFloatArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IHardwarePropertiesManager
            public CpuUsageInfo[] getCpuUsages(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    CpuUsageInfo[] _result = (CpuUsageInfo[]) _reply.createTypedArray(CpuUsageInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IHardwarePropertiesManager
            public float[] getFanSpeeds(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    float[] _result = _reply.createFloatArray();
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
