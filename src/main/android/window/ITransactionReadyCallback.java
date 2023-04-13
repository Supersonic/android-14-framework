package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public interface ITransactionReadyCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITransactionReadyCallback";

    void onTransactionReady(SurfaceControl.Transaction transaction) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITransactionReadyCallback {
        @Override // android.window.ITransactionReadyCallback
        public void onTransactionReady(SurfaceControl.Transaction t) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITransactionReadyCallback {
        static final int TRANSACTION_onTransactionReady = 1;

        public Stub() {
            attachInterface(this, ITransactionReadyCallback.DESCRIPTOR);
        }

        public static ITransactionReadyCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITransactionReadyCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITransactionReadyCallback)) {
                return (ITransactionReadyCallback) iin;
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
                    return "onTransactionReady";
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
                data.enforceInterface(ITransactionReadyCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITransactionReadyCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SurfaceControl.Transaction _arg0 = (SurfaceControl.Transaction) data.readTypedObject(SurfaceControl.Transaction.CREATOR);
                            data.enforceNoDataAvail();
                            onTransactionReady(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ITransactionReadyCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITransactionReadyCallback.DESCRIPTOR;
            }

            @Override // android.window.ITransactionReadyCallback
            public void onTransactionReady(SurfaceControl.Transaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITransactionReadyCallback.DESCRIPTOR);
                    _data.writeTypedObject(t, 0);
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
