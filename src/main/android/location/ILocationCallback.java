package android.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ILocationCallback extends IInterface {
    public static final String DESCRIPTOR = "android.location.ILocationCallback";

    void onLocation(Location location) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ILocationCallback {
        @Override // android.location.ILocationCallback
        public void onLocation(Location location) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILocationCallback {
        static final int TRANSACTION_onLocation = 1;

        public Stub() {
            attachInterface(this, ILocationCallback.DESCRIPTOR);
        }

        public static ILocationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILocationCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ILocationCallback)) {
                return (ILocationCallback) iin;
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
                    return "onLocation";
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
                data.enforceInterface(ILocationCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILocationCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Location _arg0 = (Location) data.readTypedObject(Location.CREATOR);
                            data.enforceNoDataAvail();
                            onLocation(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ILocationCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILocationCallback.DESCRIPTOR;
            }

            @Override // android.location.ILocationCallback
            public void onLocation(Location location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILocationCallback.DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
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
