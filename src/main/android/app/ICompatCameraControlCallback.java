package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICompatCameraControlCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.ICompatCameraControlCallback";

    void applyCameraCompatTreatment() throws RemoteException;

    void revertCameraCompatTreatment() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICompatCameraControlCallback {
        @Override // android.app.ICompatCameraControlCallback
        public void applyCameraCompatTreatment() throws RemoteException {
        }

        @Override // android.app.ICompatCameraControlCallback
        public void revertCameraCompatTreatment() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICompatCameraControlCallback {
        static final int TRANSACTION_applyCameraCompatTreatment = 1;
        static final int TRANSACTION_revertCameraCompatTreatment = 2;

        public Stub() {
            attachInterface(this, ICompatCameraControlCallback.DESCRIPTOR);
        }

        public static ICompatCameraControlCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICompatCameraControlCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICompatCameraControlCallback)) {
                return (ICompatCameraControlCallback) iin;
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
                    return "applyCameraCompatTreatment";
                case 2:
                    return "revertCameraCompatTreatment";
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
                data.enforceInterface(ICompatCameraControlCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICompatCameraControlCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            applyCameraCompatTreatment();
                            break;
                        case 2:
                            revertCameraCompatTreatment();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICompatCameraControlCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICompatCameraControlCallback.DESCRIPTOR;
            }

            @Override // android.app.ICompatCameraControlCallback
            public void applyCameraCompatTreatment() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICompatCameraControlCallback.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ICompatCameraControlCallback
            public void revertCameraCompatTreatment() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICompatCameraControlCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
