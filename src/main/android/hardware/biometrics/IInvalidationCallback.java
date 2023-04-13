package android.hardware.biometrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IInvalidationCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IInvalidationCallback";

    void onCompleted() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IInvalidationCallback {
        @Override // android.hardware.biometrics.IInvalidationCallback
        public void onCompleted() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IInvalidationCallback {
        static final int TRANSACTION_onCompleted = 1;

        public Stub() {
            attachInterface(this, IInvalidationCallback.DESCRIPTOR);
        }

        public static IInvalidationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInvalidationCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IInvalidationCallback)) {
                return (IInvalidationCallback) iin;
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
                    return "onCompleted";
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
                data.enforceInterface(IInvalidationCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInvalidationCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onCompleted();
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IInvalidationCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInvalidationCallback.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IInvalidationCallback
            public void onCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IInvalidationCallback.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
