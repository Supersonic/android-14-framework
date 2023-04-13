package android.service.dreams;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IDreamOverlayCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.dreams.IDreamOverlayCallback";

    void onExitRequested() throws RemoteException;

    void onWakeUpComplete() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDreamOverlayCallback {
        @Override // android.service.dreams.IDreamOverlayCallback
        public void onExitRequested() throws RemoteException {
        }

        @Override // android.service.dreams.IDreamOverlayCallback
        public void onWakeUpComplete() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDreamOverlayCallback {
        static final int TRANSACTION_onExitRequested = 1;
        static final int TRANSACTION_onWakeUpComplete = 2;

        public Stub() {
            attachInterface(this, IDreamOverlayCallback.DESCRIPTOR);
        }

        public static IDreamOverlayCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDreamOverlayCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IDreamOverlayCallback)) {
                return (IDreamOverlayCallback) iin;
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
                    return "onExitRequested";
                case 2:
                    return "onWakeUpComplete";
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
                data.enforceInterface(IDreamOverlayCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDreamOverlayCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onExitRequested();
                            reply.writeNoException();
                            break;
                        case 2:
                            onWakeUpComplete();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDreamOverlayCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDreamOverlayCallback.DESCRIPTOR;
            }

            @Override // android.service.dreams.IDreamOverlayCallback
            public void onExitRequested() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayCallback.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.dreams.IDreamOverlayCallback
            public void onWakeUpComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
