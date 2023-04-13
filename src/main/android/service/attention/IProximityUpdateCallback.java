package android.service.attention;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IProximityUpdateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.attention.IProximityUpdateCallback";

    void onProximityUpdate(double d) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IProximityUpdateCallback {
        @Override // android.service.attention.IProximityUpdateCallback
        public void onProximityUpdate(double distance) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IProximityUpdateCallback {
        static final int TRANSACTION_onProximityUpdate = 1;

        public Stub() {
            attachInterface(this, IProximityUpdateCallback.DESCRIPTOR);
        }

        public static IProximityUpdateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IProximityUpdateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IProximityUpdateCallback)) {
                return (IProximityUpdateCallback) iin;
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
                    return "onProximityUpdate";
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
                data.enforceInterface(IProximityUpdateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IProximityUpdateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            double _arg0 = data.readDouble();
                            data.enforceNoDataAvail();
                            onProximityUpdate(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IProximityUpdateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IProximityUpdateCallback.DESCRIPTOR;
            }

            @Override // android.service.attention.IProximityUpdateCallback
            public void onProximityUpdate(double distance) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IProximityUpdateCallback.DESCRIPTOR);
                    _data.writeDouble(distance);
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
