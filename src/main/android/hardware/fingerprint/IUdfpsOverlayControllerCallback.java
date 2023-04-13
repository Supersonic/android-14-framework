package android.hardware.fingerprint;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IUdfpsOverlayControllerCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.fingerprint.IUdfpsOverlayControllerCallback";

    void onUserCanceled() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUdfpsOverlayControllerCallback {
        @Override // android.hardware.fingerprint.IUdfpsOverlayControllerCallback
        public void onUserCanceled() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUdfpsOverlayControllerCallback {
        static final int TRANSACTION_onUserCanceled = 1;

        public Stub() {
            attachInterface(this, IUdfpsOverlayControllerCallback.DESCRIPTOR);
        }

        public static IUdfpsOverlayControllerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IUdfpsOverlayControllerCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IUdfpsOverlayControllerCallback)) {
                return (IUdfpsOverlayControllerCallback) iin;
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
                    return "onUserCanceled";
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
                data.enforceInterface(IUdfpsOverlayControllerCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IUdfpsOverlayControllerCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onUserCanceled();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUdfpsOverlayControllerCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IUdfpsOverlayControllerCallback.DESCRIPTOR;
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlayControllerCallback
            public void onUserCanceled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlayControllerCallback.DESCRIPTOR);
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
