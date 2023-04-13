package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public interface IRemoteTransitionFinishedCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.IRemoteTransitionFinishedCallback";

    void onTransitionFinished(WindowContainerTransaction windowContainerTransaction, SurfaceControl.Transaction transaction) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IRemoteTransitionFinishedCallback {
        @Override // android.window.IRemoteTransitionFinishedCallback
        public void onTransitionFinished(WindowContainerTransaction wct, SurfaceControl.Transaction sct) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IRemoteTransitionFinishedCallback {
        static final int TRANSACTION_onTransitionFinished = 1;

        public Stub() {
            attachInterface(this, IRemoteTransitionFinishedCallback.DESCRIPTOR);
        }

        public static IRemoteTransitionFinishedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRemoteTransitionFinishedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRemoteTransitionFinishedCallback)) {
                return (IRemoteTransitionFinishedCallback) iin;
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
                    return "onTransitionFinished";
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
                data.enforceInterface(IRemoteTransitionFinishedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRemoteTransitionFinishedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            WindowContainerTransaction _arg0 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            SurfaceControl.Transaction _arg1 = (SurfaceControl.Transaction) data.readTypedObject(SurfaceControl.Transaction.CREATOR);
                            data.enforceNoDataAvail();
                            onTransitionFinished(_arg0, _arg1);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IRemoteTransitionFinishedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRemoteTransitionFinishedCallback.DESCRIPTOR;
            }

            @Override // android.window.IRemoteTransitionFinishedCallback
            public void onTransitionFinished(WindowContainerTransaction wct, SurfaceControl.Transaction sct) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRemoteTransitionFinishedCallback.DESCRIPTOR);
                    _data.writeTypedObject(wct, 0);
                    _data.writeTypedObject(sct, 0);
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
