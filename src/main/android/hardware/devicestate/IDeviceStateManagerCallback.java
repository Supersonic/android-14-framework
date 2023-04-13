package android.hardware.devicestate;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IDeviceStateManagerCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.devicestate.IDeviceStateManagerCallback";

    void onDeviceStateInfoChanged(DeviceStateInfo deviceStateInfo) throws RemoteException;

    void onRequestActive(IBinder iBinder) throws RemoteException;

    void onRequestCanceled(IBinder iBinder) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IDeviceStateManagerCallback {
        @Override // android.hardware.devicestate.IDeviceStateManagerCallback
        public void onDeviceStateInfoChanged(DeviceStateInfo info) throws RemoteException {
        }

        @Override // android.hardware.devicestate.IDeviceStateManagerCallback
        public void onRequestActive(IBinder token) throws RemoteException {
        }

        @Override // android.hardware.devicestate.IDeviceStateManagerCallback
        public void onRequestCanceled(IBinder token) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDeviceStateManagerCallback {
        static final int TRANSACTION_onDeviceStateInfoChanged = 1;
        static final int TRANSACTION_onRequestActive = 2;
        static final int TRANSACTION_onRequestCanceled = 3;

        public Stub() {
            attachInterface(this, IDeviceStateManagerCallback.DESCRIPTOR);
        }

        public static IDeviceStateManagerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDeviceStateManagerCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IDeviceStateManagerCallback)) {
                return (IDeviceStateManagerCallback) iin;
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
                    return "onDeviceStateInfoChanged";
                case 2:
                    return "onRequestActive";
                case 3:
                    return "onRequestCanceled";
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
                data.enforceInterface(IDeviceStateManagerCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDeviceStateManagerCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            DeviceStateInfo _arg0 = (DeviceStateInfo) data.readTypedObject(DeviceStateInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDeviceStateInfoChanged(_arg0);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onRequestActive(_arg02);
                            break;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onRequestCanceled(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IDeviceStateManagerCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDeviceStateManagerCallback.DESCRIPTOR;
            }

            @Override // android.hardware.devicestate.IDeviceStateManagerCallback
            public void onDeviceStateInfoChanged(DeviceStateInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDeviceStateManagerCallback.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.devicestate.IDeviceStateManagerCallback
            public void onRequestActive(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDeviceStateManagerCallback.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.devicestate.IDeviceStateManagerCallback
            public void onRequestCanceled(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDeviceStateManagerCallback.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
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
