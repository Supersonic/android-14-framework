package android.p008os;

import android.media.MediaMetrics;
/* renamed from: android.os.IExternalVibrationController */
/* loaded from: classes3.dex */
public interface IExternalVibrationController extends IInterface {
    public static final String DESCRIPTOR = "android.os.IExternalVibrationController";

    boolean mute() throws RemoteException;

    boolean unmute() throws RemoteException;

    /* renamed from: android.os.IExternalVibrationController$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IExternalVibrationController {
        @Override // android.p008os.IExternalVibrationController
        public boolean mute() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IExternalVibrationController
        public boolean unmute() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IExternalVibrationController$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IExternalVibrationController {
        static final int TRANSACTION_mute = 1;
        static final int TRANSACTION_unmute = 2;

        public Stub() {
            attachInterface(this, IExternalVibrationController.DESCRIPTOR);
        }

        public static IExternalVibrationController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IExternalVibrationController.DESCRIPTOR);
            if (iin != null && (iin instanceof IExternalVibrationController)) {
                return (IExternalVibrationController) iin;
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
                    return MediaMetrics.Value.MUTE;
                case 2:
                    return MediaMetrics.Value.UNMUTE;
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
                data.enforceInterface(IExternalVibrationController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IExternalVibrationController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _result = mute();
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            boolean _result2 = unmute();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IExternalVibrationController$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IExternalVibrationController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IExternalVibrationController.DESCRIPTOR;
            }

            @Override // android.p008os.IExternalVibrationController
            public boolean mute() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IExternalVibrationController.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IExternalVibrationController
            public boolean unmute() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IExternalVibrationController.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
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
