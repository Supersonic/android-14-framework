package android.location.provider;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IProviderRequestListener extends IInterface {
    public static final String DESCRIPTOR = "android.location.provider.IProviderRequestListener";

    void onProviderRequestChanged(String str, ProviderRequest providerRequest) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IProviderRequestListener {
        @Override // android.location.provider.IProviderRequestListener
        public void onProviderRequestChanged(String provider, ProviderRequest request) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IProviderRequestListener {
        static final int TRANSACTION_onProviderRequestChanged = 1;

        public Stub() {
            attachInterface(this, IProviderRequestListener.DESCRIPTOR);
        }

        public static IProviderRequestListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IProviderRequestListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IProviderRequestListener)) {
                return (IProviderRequestListener) iin;
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
                    return "onProviderRequestChanged";
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
                data.enforceInterface(IProviderRequestListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IProviderRequestListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            ProviderRequest _arg1 = (ProviderRequest) data.readTypedObject(ProviderRequest.CREATOR);
                            data.enforceNoDataAvail();
                            onProviderRequestChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IProviderRequestListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IProviderRequestListener.DESCRIPTOR;
            }

            @Override // android.location.provider.IProviderRequestListener
            public void onProviderRequestChanged(String provider, ProviderRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IProviderRequestListener.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeTypedObject(request, 0);
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
