package android.view;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.window.WindowContainerTransaction;
/* loaded from: classes4.dex */
public interface IDisplayChangeWindowCallback extends IInterface {
    public static final String DESCRIPTOR = "android.view.IDisplayChangeWindowCallback";

    void continueDisplayChange(WindowContainerTransaction windowContainerTransaction) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDisplayChangeWindowCallback {
        @Override // android.view.IDisplayChangeWindowCallback
        public void continueDisplayChange(WindowContainerTransaction t) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDisplayChangeWindowCallback {
        static final int TRANSACTION_continueDisplayChange = 1;

        public Stub() {
            attachInterface(this, IDisplayChangeWindowCallback.DESCRIPTOR);
        }

        public static IDisplayChangeWindowCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayChangeWindowCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayChangeWindowCallback)) {
                return (IDisplayChangeWindowCallback) iin;
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
                    return "continueDisplayChange";
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
                data.enforceInterface(IDisplayChangeWindowCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayChangeWindowCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            WindowContainerTransaction _arg0 = (WindowContainerTransaction) data.readTypedObject(WindowContainerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            continueDisplayChange(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IDisplayChangeWindowCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayChangeWindowCallback.DESCRIPTOR;
            }

            @Override // android.view.IDisplayChangeWindowCallback
            public void continueDisplayChange(WindowContainerTransaction t) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDisplayChangeWindowCallback.DESCRIPTOR);
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
