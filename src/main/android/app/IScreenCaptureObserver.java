package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IScreenCaptureObserver extends IInterface {
    public static final String DESCRIPTOR = "android.app.IScreenCaptureObserver";

    void onScreenCaptured() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IScreenCaptureObserver {
        @Override // android.app.IScreenCaptureObserver
        public void onScreenCaptured() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IScreenCaptureObserver {
        static final int TRANSACTION_onScreenCaptured = 1;

        public Stub() {
            attachInterface(this, IScreenCaptureObserver.DESCRIPTOR);
        }

        public static IScreenCaptureObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IScreenCaptureObserver.DESCRIPTOR);
            if (iin != null && (iin instanceof IScreenCaptureObserver)) {
                return (IScreenCaptureObserver) iin;
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
                    return "onScreenCaptured";
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
                data.enforceInterface(IScreenCaptureObserver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IScreenCaptureObserver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onScreenCaptured();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IScreenCaptureObserver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IScreenCaptureObserver.DESCRIPTOR;
            }

            @Override // android.app.IScreenCaptureObserver
            public void onScreenCaptured() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScreenCaptureObserver.DESCRIPTOR);
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
