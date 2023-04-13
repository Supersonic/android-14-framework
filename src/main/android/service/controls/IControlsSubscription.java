package android.service.controls;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IControlsSubscription extends IInterface {
    public static final String DESCRIPTOR = "android.service.controls.IControlsSubscription";

    void cancel() throws RemoteException;

    void request(long j) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IControlsSubscription {
        @Override // android.service.controls.IControlsSubscription
        public void request(long n) throws RemoteException {
        }

        @Override // android.service.controls.IControlsSubscription
        public void cancel() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IControlsSubscription {
        static final int TRANSACTION_cancel = 2;
        static final int TRANSACTION_request = 1;

        public Stub() {
            attachInterface(this, IControlsSubscription.DESCRIPTOR);
        }

        public static IControlsSubscription asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IControlsSubscription.DESCRIPTOR);
            if (iin != null && (iin instanceof IControlsSubscription)) {
                return (IControlsSubscription) iin;
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
                    return "request";
                case 2:
                    return "cancel";
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
                data.enforceInterface(IControlsSubscription.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IControlsSubscription.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            data.enforceNoDataAvail();
                            request(_arg0);
                            break;
                        case 2:
                            cancel();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IControlsSubscription {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IControlsSubscription.DESCRIPTOR;
            }

            @Override // android.service.controls.IControlsSubscription
            public void request(long n) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsSubscription.DESCRIPTOR);
                    _data.writeLong(n);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.controls.IControlsSubscription
            public void cancel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IControlsSubscription.DESCRIPTOR);
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
