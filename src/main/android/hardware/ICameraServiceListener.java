package android.hardware;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICameraServiceListener extends IInterface {
    public static final int STATUS_ENUMERATING = 2;
    public static final int STATUS_NOT_AVAILABLE = -2;
    public static final int STATUS_NOT_PRESENT = 0;
    public static final int STATUS_PRESENT = 1;
    public static final int STATUS_UNKNOWN = -1;
    public static final int TORCH_STATUS_AVAILABLE_OFF = 1;
    public static final int TORCH_STATUS_AVAILABLE_ON = 2;
    public static final int TORCH_STATUS_NOT_AVAILABLE = 0;
    public static final int TORCH_STATUS_UNKNOWN = -1;

    void onCameraAccessPrioritiesChanged() throws RemoteException;

    void onCameraClosed(String str) throws RemoteException;

    void onCameraOpened(String str, String str2) throws RemoteException;

    void onPhysicalCameraStatusChanged(int i, String str, String str2) throws RemoteException;

    void onStatusChanged(int i, String str) throws RemoteException;

    void onTorchStatusChanged(int i, String str) throws RemoteException;

    void onTorchStrengthLevelChanged(String str, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICameraServiceListener {
        @Override // android.hardware.ICameraServiceListener
        public void onStatusChanged(int status, String cameraId) throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onPhysicalCameraStatusChanged(int status, String cameraId, String physicalCameraId) throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onTorchStatusChanged(int status, String cameraId) throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onTorchStrengthLevelChanged(String cameraId, int newTorchStrength) throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onCameraAccessPrioritiesChanged() throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onCameraOpened(String cameraId, String clientPackageId) throws RemoteException {
        }

        @Override // android.hardware.ICameraServiceListener
        public void onCameraClosed(String cameraId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICameraServiceListener {
        public static final String DESCRIPTOR = "android.hardware.ICameraServiceListener";
        static final int TRANSACTION_onCameraAccessPrioritiesChanged = 5;
        static final int TRANSACTION_onCameraClosed = 7;
        static final int TRANSACTION_onCameraOpened = 6;
        static final int TRANSACTION_onPhysicalCameraStatusChanged = 2;
        static final int TRANSACTION_onStatusChanged = 1;
        static final int TRANSACTION_onTorchStatusChanged = 3;
        static final int TRANSACTION_onTorchStrengthLevelChanged = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraServiceListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraServiceListener)) {
                return (ICameraServiceListener) iin;
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
                    return "onStatusChanged";
                case 2:
                    return "onPhysicalCameraStatusChanged";
                case 3:
                    return "onTorchStatusChanged";
                case 4:
                    return "onTorchStrengthLevelChanged";
                case 5:
                    return "onCameraAccessPrioritiesChanged";
                case 6:
                    return "onCameraOpened";
                case 7:
                    return "onCameraClosed";
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
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onStatusChanged(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onPhysicalCameraStatusChanged(_arg02, _arg12, _arg2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            onTorchStatusChanged(_arg03, _arg13);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            onTorchStrengthLevelChanged(_arg04, _arg14);
                            break;
                        case 5:
                            onCameraAccessPrioritiesChanged();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            onCameraOpened(_arg05, _arg15);
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            onCameraClosed(_arg06);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICameraServiceListener {
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

            @Override // android.hardware.ICameraServiceListener
            public void onStatusChanged(int status, String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeString(cameraId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onPhysicalCameraStatusChanged(int status, String cameraId, String physicalCameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeString(cameraId);
                    _data.writeString(physicalCameraId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onTorchStatusChanged(int status, String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeString(cameraId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onTorchStrengthLevelChanged(String cameraId, int newTorchStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeInt(newTorchStrength);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onCameraAccessPrioritiesChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onCameraOpened(String cameraId, String clientPackageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeString(clientPackageId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraServiceListener
            public void onCameraClosed(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
