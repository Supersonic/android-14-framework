package android.security.rkp;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.security.rkp.IRegistration;
/* loaded from: classes3.dex */
public interface IGetRegistrationCallback extends IInterface {
    public static final String DESCRIPTOR = "android.security.rkp.IGetRegistrationCallback";

    void onCancel() throws RemoteException;

    void onError(String str) throws RemoteException;

    void onSuccess(IRegistration iRegistration) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGetRegistrationCallback {
        @Override // android.security.rkp.IGetRegistrationCallback
        public void onSuccess(IRegistration registration) throws RemoteException {
        }

        @Override // android.security.rkp.IGetRegistrationCallback
        public void onCancel() throws RemoteException {
        }

        @Override // android.security.rkp.IGetRegistrationCallback
        public void onError(String error) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGetRegistrationCallback {
        static final int TRANSACTION_onCancel = 2;
        static final int TRANSACTION_onError = 3;
        static final int TRANSACTION_onSuccess = 1;

        public Stub() {
            attachInterface(this, IGetRegistrationCallback.DESCRIPTOR);
        }

        public static IGetRegistrationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGetRegistrationCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IGetRegistrationCallback)) {
                return (IGetRegistrationCallback) iin;
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
                    return "onSuccess";
                case 2:
                    return "onCancel";
                case 3:
                    return "onError";
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
                data.enforceInterface(IGetRegistrationCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGetRegistrationCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IRegistration _arg0 = IRegistration.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSuccess(_arg0);
                            break;
                        case 2:
                            onCancel();
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGetRegistrationCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGetRegistrationCallback.DESCRIPTOR;
            }

            @Override // android.security.rkp.IGetRegistrationCallback
            public void onSuccess(IRegistration registration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetRegistrationCallback.DESCRIPTOR);
                    _data.writeStrongInterface(registration);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.rkp.IGetRegistrationCallback
            public void onCancel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetRegistrationCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.rkp.IGetRegistrationCallback
            public void onError(String error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetRegistrationCallback.DESCRIPTOR);
                    _data.writeString(error);
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
