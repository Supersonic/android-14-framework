package android.hardware.biometrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBiometricServiceReceiver extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IBiometricServiceReceiver";

    void onAcquired(int i, String str) throws RemoteException;

    void onAuthenticationFailed() throws RemoteException;

    void onAuthenticationSucceeded(int i) throws RemoteException;

    void onDialogDismissed(int i) throws RemoteException;

    void onError(int i, int i2, int i3) throws RemoteException;

    void onSystemEvent(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBiometricServiceReceiver {
        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onAuthenticationSucceeded(int authenticationType) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onAuthenticationFailed() throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onError(int modality, int error, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onAcquired(int acquiredInfo, String message) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onDialogDismissed(int reason) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricServiceReceiver
        public void onSystemEvent(int event) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBiometricServiceReceiver {
        static final int TRANSACTION_onAcquired = 4;
        static final int TRANSACTION_onAuthenticationFailed = 2;
        static final int TRANSACTION_onAuthenticationSucceeded = 1;
        static final int TRANSACTION_onDialogDismissed = 5;
        static final int TRANSACTION_onError = 3;
        static final int TRANSACTION_onSystemEvent = 6;

        public Stub() {
            attachInterface(this, IBiometricServiceReceiver.DESCRIPTOR);
        }

        public static IBiometricServiceReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBiometricServiceReceiver.DESCRIPTOR);
            if (iin != null && (iin instanceof IBiometricServiceReceiver)) {
                return (IBiometricServiceReceiver) iin;
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
                    return "onAuthenticationSucceeded";
                case 2:
                    return "onAuthenticationFailed";
                case 3:
                    return "onError";
                case 4:
                    return "onAcquired";
                case 5:
                    return "onDialogDismissed";
                case 6:
                    return "onSystemEvent";
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
                data.enforceInterface(IBiometricServiceReceiver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBiometricServiceReceiver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onAuthenticationSucceeded(_arg0);
                            break;
                        case 2:
                            onAuthenticationFailed();
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg02, _arg1, _arg2);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            onAcquired(_arg03, _arg12);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onDialogDismissed(_arg04);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onSystemEvent(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBiometricServiceReceiver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBiometricServiceReceiver.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onAuthenticationSucceeded(int authenticationType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    _data.writeInt(authenticationType);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onAuthenticationFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onError(int modality, int error, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    _data.writeInt(modality);
                    _data.writeInt(error);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onAcquired(int acquiredInfo, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    _data.writeInt(acquiredInfo);
                    _data.writeString(message);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onDialogDismissed(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricServiceReceiver
            public void onSystemEvent(int event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricServiceReceiver.DESCRIPTOR);
                    _data.writeInt(event);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
