package android.p008os;
/* renamed from: android.os.IExternalVibratorService */
/* loaded from: classes3.dex */
public interface IExternalVibratorService extends IInterface {
    public static final String DESCRIPTOR = "android.os.IExternalVibratorService";
    public static final int SCALE_HIGH = 1;
    public static final int SCALE_LOW = -1;
    public static final int SCALE_MUTE = -100;
    public static final int SCALE_NONE = 0;
    public static final int SCALE_VERY_HIGH = 2;
    public static final int SCALE_VERY_LOW = -2;

    int onExternalVibrationStart(ExternalVibration externalVibration) throws RemoteException;

    void onExternalVibrationStop(ExternalVibration externalVibration) throws RemoteException;

    /* renamed from: android.os.IExternalVibratorService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IExternalVibratorService {
        @Override // android.p008os.IExternalVibratorService
        public int onExternalVibrationStart(ExternalVibration vib) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IExternalVibratorService
        public void onExternalVibrationStop(ExternalVibration vib) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IExternalVibratorService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IExternalVibratorService {
        static final int TRANSACTION_onExternalVibrationStart = 1;
        static final int TRANSACTION_onExternalVibrationStop = 2;

        public Stub() {
            attachInterface(this, IExternalVibratorService.DESCRIPTOR);
        }

        public static IExternalVibratorService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IExternalVibratorService.DESCRIPTOR);
            if (iin != null && (iin instanceof IExternalVibratorService)) {
                return (IExternalVibratorService) iin;
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
                    return "onExternalVibrationStart";
                case 2:
                    return "onExternalVibrationStop";
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
                data.enforceInterface(IExternalVibratorService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IExternalVibratorService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ExternalVibration _arg0 = (ExternalVibration) data.readTypedObject(ExternalVibration.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = onExternalVibrationStart(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            ExternalVibration _arg02 = (ExternalVibration) data.readTypedObject(ExternalVibration.CREATOR);
                            data.enforceNoDataAvail();
                            onExternalVibrationStop(_arg02);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IExternalVibratorService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IExternalVibratorService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IExternalVibratorService.DESCRIPTOR;
            }

            @Override // android.p008os.IExternalVibratorService
            public int onExternalVibrationStart(ExternalVibration vib) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IExternalVibratorService.DESCRIPTOR);
                    _data.writeTypedObject(vib, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IExternalVibratorService
            public void onExternalVibrationStop(ExternalVibration vib) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IExternalVibratorService.DESCRIPTOR);
                    _data.writeTypedObject(vib, 0);
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
