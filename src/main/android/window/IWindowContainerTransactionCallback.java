package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public interface IWindowContainerTransactionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.IWindowContainerTransactionCallback";

    void onTransactionReady(int i, SurfaceControl.Transaction transaction) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IWindowContainerTransactionCallback {
        @Override // android.window.IWindowContainerTransactionCallback
        public void onTransactionReady(int id, SurfaceControl.Transaction t) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IWindowContainerTransactionCallback {
        static final int TRANSACTION_onTransactionReady = 1;

        public Stub() {
            attachInterface(this, IWindowContainerTransactionCallback.DESCRIPTOR);
        }

        public static IWindowContainerTransactionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWindowContainerTransactionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowContainerTransactionCallback)) {
                return (IWindowContainerTransactionCallback) iin;
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
                data.enforceInterface(IWindowContainerTransactionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWindowContainerTransactionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            SurfaceControl.Transaction _arg1 = (SurfaceControl.Transaction) data.readTypedObject(SurfaceControl.Transaction.CREATOR);
                            data.enforceNoDataAvail();
                            onTransactionReady(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IWindowContainerTransactionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWindowContainerTransactionCallback.DESCRIPTOR;
            }

            @Override // android.window.IWindowContainerTransactionCallback
            public void onTransactionReady(int id, SurfaceControl.Transaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowContainerTransactionCallback.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeTypedObject(t, 0);
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
