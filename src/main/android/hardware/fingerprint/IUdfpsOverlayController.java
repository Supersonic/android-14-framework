package android.hardware.fingerprint;

import android.hardware.fingerprint.IUdfpsOverlayControllerCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IUdfpsOverlayController extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.fingerprint.IUdfpsOverlayController";

    void hideUdfpsOverlay(int i) throws RemoteException;

    void onAcquired(int i, int i2) throws RemoteException;

    void onEnrollmentHelp(int i) throws RemoteException;

    void onEnrollmentProgress(int i, int i2) throws RemoteException;

    void setDebugMessage(int i, String str) throws RemoteException;

    void showUdfpsOverlay(long j, int i, int i2, IUdfpsOverlayControllerCallback iUdfpsOverlayControllerCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUdfpsOverlayController {
        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void showUdfpsOverlay(long requestId, int sensorId, int reason, IUdfpsOverlayControllerCallback callback) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void hideUdfpsOverlay(int sensorId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void onAcquired(int sensorId, int acquiredInfo) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void onEnrollmentProgress(int sensorId, int remaining) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void onEnrollmentHelp(int sensorId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlayController
        public void setDebugMessage(int sensorId, String message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUdfpsOverlayController {
        static final int TRANSACTION_hideUdfpsOverlay = 2;
        static final int TRANSACTION_onAcquired = 3;
        static final int TRANSACTION_onEnrollmentHelp = 5;
        static final int TRANSACTION_onEnrollmentProgress = 4;
        static final int TRANSACTION_setDebugMessage = 6;
        static final int TRANSACTION_showUdfpsOverlay = 1;

        public Stub() {
            attachInterface(this, IUdfpsOverlayController.DESCRIPTOR);
        }

        public static IUdfpsOverlayController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IUdfpsOverlayController.DESCRIPTOR);
            if (iin != null && (iin instanceof IUdfpsOverlayController)) {
                return (IUdfpsOverlayController) iin;
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
                    return "showUdfpsOverlay";
                case 2:
                    return "hideUdfpsOverlay";
                case 3:
                    return "onAcquired";
                case 4:
                    return "onEnrollmentProgress";
                case 5:
                    return "onEnrollmentHelp";
                case 6:
                    return "setDebugMessage";
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
                data.enforceInterface(IUdfpsOverlayController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IUdfpsOverlayController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            IUdfpsOverlayControllerCallback _arg3 = IUdfpsOverlayControllerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            showUdfpsOverlay(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            hideUdfpsOverlay(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onAcquired(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onEnrollmentProgress(_arg04, _arg13);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onEnrollmentHelp(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            setDebugMessage(_arg06, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUdfpsOverlayController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IUdfpsOverlayController.DESCRIPTOR;
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void showUdfpsOverlay(long requestId, int sensorId, int reason, IUdfpsOverlayControllerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(sensorId);
                    _data.writeInt(reason);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void hideUdfpsOverlay(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void onAcquired(int sensorId, int acquiredInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(acquiredInfo);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void onEnrollmentProgress(int sensorId, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(remaining);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void onEnrollmentHelp(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayController
            public void setDebugMessage(int sensorId, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayController.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeString(message);
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
