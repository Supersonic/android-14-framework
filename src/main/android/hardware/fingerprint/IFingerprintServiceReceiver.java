package android.hardware.fingerprint;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IFingerprintServiceReceiver extends IInterface {
    void onAcquired(int i, int i2) throws RemoteException;

    void onAuthenticationFailed() throws RemoteException;

    void onAuthenticationSucceeded(Fingerprint fingerprint, int i, boolean z) throws RemoteException;

    void onChallengeGenerated(int i, int i2, long j) throws RemoteException;

    void onEnrollResult(Fingerprint fingerprint, int i) throws RemoteException;

    void onError(int i, int i2) throws RemoteException;

    void onFingerprintDetected(int i, int i2, boolean z) throws RemoteException;

    void onRemoved(Fingerprint fingerprint, int i) throws RemoteException;

    void onUdfpsPointerDown(int i) throws RemoteException;

    void onUdfpsPointerUp(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IFingerprintServiceReceiver {
        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onEnrollResult(Fingerprint fp, int remaining) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAcquired(int acquiredInfo, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAuthenticationSucceeded(Fingerprint fp, int userId, boolean isStrongBiometric) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onFingerprintDetected(int sensorId, int userId, boolean isStrongBiometric) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAuthenticationFailed() throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onError(int error, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onRemoved(Fingerprint fp, int remaining) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onChallengeGenerated(int sensorId, int userId, long challenge) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onUdfpsPointerDown(int sensorId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onUdfpsPointerUp(int sensorId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IFingerprintServiceReceiver {
        public static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintServiceReceiver";
        static final int TRANSACTION_onAcquired = 2;
        static final int TRANSACTION_onAuthenticationFailed = 5;
        static final int TRANSACTION_onAuthenticationSucceeded = 3;
        static final int TRANSACTION_onChallengeGenerated = 8;
        static final int TRANSACTION_onEnrollResult = 1;
        static final int TRANSACTION_onError = 6;
        static final int TRANSACTION_onFingerprintDetected = 4;
        static final int TRANSACTION_onRemoved = 7;
        static final int TRANSACTION_onUdfpsPointerDown = 9;
        static final int TRANSACTION_onUdfpsPointerUp = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFingerprintServiceReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFingerprintServiceReceiver)) {
                return (IFingerprintServiceReceiver) iin;
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
                    return "onEnrollResult";
                case 2:
                    return "onAcquired";
                case 3:
                    return "onAuthenticationSucceeded";
                case 4:
                    return "onFingerprintDetected";
                case 5:
                    return "onAuthenticationFailed";
                case 6:
                    return "onError";
                case 7:
                    return "onRemoved";
                case 8:
                    return "onChallengeGenerated";
                case 9:
                    return "onUdfpsPointerDown";
                case 10:
                    return "onUdfpsPointerUp";
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
                            Fingerprint _arg0 = (Fingerprint) data.readTypedObject(Fingerprint.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onEnrollResult(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onAcquired(_arg02, _arg12);
                            break;
                        case 3:
                            Fingerprint _arg03 = (Fingerprint) data.readTypedObject(Fingerprint.CREATOR);
                            int _arg13 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAuthenticationSucceeded(_arg03, _arg13, _arg2);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onFingerprintDetected(_arg04, _arg14, _arg22);
                            break;
                        case 5:
                            onAuthenticationFailed();
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg05, _arg15);
                            break;
                        case 7:
                            Fingerprint _arg06 = (Fingerprint) data.readTypedObject(Fingerprint.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            onRemoved(_arg06, _arg16);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg17 = data.readInt();
                            long _arg23 = data.readLong();
                            data.enforceNoDataAvail();
                            onChallengeGenerated(_arg07, _arg17, _arg23);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            onUdfpsPointerDown(_arg08);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            onUdfpsPointerUp(_arg09);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IFingerprintServiceReceiver {
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

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onEnrollResult(Fingerprint fp, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fp, 0);
                    _data.writeInt(remaining);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onAcquired(int acquiredInfo, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(acquiredInfo);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onAuthenticationSucceeded(Fingerprint fp, int userId, boolean isStrongBiometric) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fp, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(isStrongBiometric);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onFingerprintDetected(int sensorId, int userId, boolean isStrongBiometric) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeBoolean(isStrongBiometric);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onAuthenticationFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onError(int error, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(error);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onRemoved(Fingerprint fp, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fp, 0);
                    _data.writeInt(remaining);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onChallengeGenerated(int sensorId, int userId, long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeLong(challenge);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onUdfpsPointerDown(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
            public void onUdfpsPointerUp(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
