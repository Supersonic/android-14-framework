package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IOnBackInvokedCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.IOnBackInvokedCallback";

    void onBackCancelled() throws RemoteException;

    void onBackInvoked() throws RemoteException;

    void onBackProgressed(BackMotionEvent backMotionEvent) throws RemoteException;

    void onBackStarted(BackMotionEvent backMotionEvent) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IOnBackInvokedCallback {
        @Override // android.window.IOnBackInvokedCallback
        public void onBackStarted(BackMotionEvent backMotionEvent) throws RemoteException {
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackProgressed(BackMotionEvent backMotionEvent) throws RemoteException {
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackCancelled() throws RemoteException {
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackInvoked() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IOnBackInvokedCallback {
        static final int TRANSACTION_onBackCancelled = 3;
        static final int TRANSACTION_onBackInvoked = 4;
        static final int TRANSACTION_onBackProgressed = 2;
        static final int TRANSACTION_onBackStarted = 1;

        public Stub() {
            attachInterface(this, IOnBackInvokedCallback.DESCRIPTOR);
        }

        public static IOnBackInvokedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOnBackInvokedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IOnBackInvokedCallback)) {
                return (IOnBackInvokedCallback) iin;
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
                    return "onBackStarted";
                case 2:
                    return "onBackProgressed";
                case 3:
                    return "onBackCancelled";
                case 4:
                    return "onBackInvoked";
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
                data.enforceInterface(IOnBackInvokedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOnBackInvokedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            BackMotionEvent _arg0 = (BackMotionEvent) data.readTypedObject(BackMotionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onBackStarted(_arg0);
                            break;
                        case 2:
                            BackMotionEvent _arg02 = (BackMotionEvent) data.readTypedObject(BackMotionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onBackProgressed(_arg02);
                            break;
                        case 3:
                            onBackCancelled();
                            break;
                        case 4:
                            onBackInvoked();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IOnBackInvokedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOnBackInvokedCallback.DESCRIPTOR;
            }

            @Override // android.window.IOnBackInvokedCallback
            public void onBackStarted(BackMotionEvent backMotionEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnBackInvokedCallback.DESCRIPTOR);
                    _data.writeTypedObject(backMotionEvent, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IOnBackInvokedCallback
            public void onBackProgressed(BackMotionEvent backMotionEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnBackInvokedCallback.DESCRIPTOR);
                    _data.writeTypedObject(backMotionEvent, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IOnBackInvokedCallback
            public void onBackCancelled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnBackInvokedCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IOnBackInvokedCallback
            public void onBackInvoked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnBackInvokedCallback.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
