package android.hardware.camera2;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICameraInjectionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.ICameraInjectionCallback";
    public static final int ERROR_INJECTION_INVALID_ERROR = -1;
    public static final int ERROR_INJECTION_SERVICE = 1;
    public static final int ERROR_INJECTION_SESSION = 0;
    public static final int ERROR_INJECTION_UNSUPPORTED = 2;

    void onInjectionError(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICameraInjectionCallback {
        @Override // android.hardware.camera2.ICameraInjectionCallback
        public void onInjectionError(int errorCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICameraInjectionCallback {
        static final int TRANSACTION_onInjectionError = 1;

        public Stub() {
            attachInterface(this, ICameraInjectionCallback.DESCRIPTOR);
        }

        public static ICameraInjectionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICameraInjectionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraInjectionCallback)) {
                return (ICameraInjectionCallback) iin;
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
                    return "onInjectionError";
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
                data.enforceInterface(ICameraInjectionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICameraInjectionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onInjectionError(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICameraInjectionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICameraInjectionCallback.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.ICameraInjectionCallback
            public void onInjectionError(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICameraInjectionCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
