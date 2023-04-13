package android.view;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IInputMonitorHost extends IInterface {
    public static final String DESCRIPTOR = "android.view.IInputMonitorHost";

    void dispose() throws RemoteException;

    void pilferPointers() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMonitorHost {
        @Override // android.view.IInputMonitorHost
        public void pilferPointers() throws RemoteException {
        }

        @Override // android.view.IInputMonitorHost
        public void dispose() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMonitorHost {
        static final int TRANSACTION_dispose = 2;
        static final int TRANSACTION_pilferPointers = 1;

        public Stub() {
            attachInterface(this, IInputMonitorHost.DESCRIPTOR);
        }

        public static IInputMonitorHost asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMonitorHost.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMonitorHost)) {
                return (IInputMonitorHost) iin;
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
                    return "pilferPointers";
                case 2:
                    return "dispose";
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
                data.enforceInterface(IInputMonitorHost.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMonitorHost.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            pilferPointers();
                            break;
                        case 2:
                            dispose();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputMonitorHost {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMonitorHost.DESCRIPTOR;
            }

            @Override // android.view.IInputMonitorHost
            public void pilferPointers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMonitorHost.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IInputMonitorHost
            public void dispose() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMonitorHost.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
