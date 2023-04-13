package android.hardware.biometrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBiometricSensorReceiver extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IBiometricSensorReceiver";

    void onAcquired(int i, int i2, int i3) throws RemoteException;

    void onAuthenticationFailed(int i) throws RemoteException;

    void onAuthenticationSucceeded(int i, byte[] bArr) throws RemoteException;

    void onError(int i, int i2, int i3, int i4) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBiometricSensorReceiver {
        @Override // android.hardware.biometrics.IBiometricSensorReceiver
        public void onAuthenticationSucceeded(int sensorId, byte[] token) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricSensorReceiver
        public void onAuthenticationFailed(int sensorId) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricSensorReceiver
        public void onError(int sensorId, int cookie, int error, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricSensorReceiver
        public void onAcquired(int sensorId, int acquiredInfo, int vendorCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBiometricSensorReceiver {
        static final int TRANSACTION_onAcquired = 4;
        static final int TRANSACTION_onAuthenticationFailed = 2;
        static final int TRANSACTION_onAuthenticationSucceeded = 1;
        static final int TRANSACTION_onError = 3;

        public Stub() {
            attachInterface(this, IBiometricSensorReceiver.DESCRIPTOR);
        }

        public static IBiometricSensorReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBiometricSensorReceiver.DESCRIPTOR);
            if (iin != null && (iin instanceof IBiometricSensorReceiver)) {
                return (IBiometricSensorReceiver) iin;
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
                data.enforceInterface(IBiometricSensorReceiver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBiometricSensorReceiver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onAuthenticationSucceeded(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onAuthenticationFailed(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg03, _arg12, _arg2, _arg3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onAcquired(_arg04, _arg13, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBiometricSensorReceiver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBiometricSensorReceiver.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IBiometricSensorReceiver
            public void onAuthenticationSucceeded(int sensorId, byte[] token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricSensorReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeByteArray(token);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricSensorReceiver
            public void onAuthenticationFailed(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricSensorReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricSensorReceiver
            public void onError(int sensorId, int cookie, int error, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricSensorReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(cookie);
                    _data.writeInt(error);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricSensorReceiver
            public void onAcquired(int sensorId, int acquiredInfo, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricSensorReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(acquiredInfo);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
