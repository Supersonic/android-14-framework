package android.location.provider;

import android.location.provider.ILocationProviderManager;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ILocationProvider extends IInterface {
    public static final String DESCRIPTOR = "android.location.provider.ILocationProvider";

    void flush() throws RemoteException;

    void sendExtraCommand(String str, Bundle bundle) throws RemoteException;

    void setLocationProviderManager(ILocationProviderManager iLocationProviderManager) throws RemoteException;

    void setRequest(ProviderRequest providerRequest) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ILocationProvider {
        @Override // android.location.provider.ILocationProvider
        public void setLocationProviderManager(ILocationProviderManager manager) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProvider
        public void setRequest(ProviderRequest request) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProvider
        public void flush() throws RemoteException {
        }

        @Override // android.location.provider.ILocationProvider
        public void sendExtraCommand(String command, Bundle extras) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILocationProvider {
        static final int TRANSACTION_flush = 3;
        static final int TRANSACTION_sendExtraCommand = 4;
        static final int TRANSACTION_setLocationProviderManager = 1;
        static final int TRANSACTION_setRequest = 2;

        public Stub() {
            attachInterface(this, ILocationProvider.DESCRIPTOR);
        }

        public static ILocationProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILocationProvider.DESCRIPTOR);
            if (iin != null && (iin instanceof ILocationProvider)) {
                return (ILocationProvider) iin;
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
                    return "setLocationProviderManager";
                case 2:
                    return "setRequest";
                case 3:
                    return "flush";
                case 4:
                    return "sendExtraCommand";
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
                data.enforceInterface(ILocationProvider.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILocationProvider.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ILocationProviderManager _arg0 = ILocationProviderManager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setLocationProviderManager(_arg0);
                            break;
                        case 2:
                            ProviderRequest _arg02 = (ProviderRequest) data.readTypedObject(ProviderRequest.CREATOR);
                            data.enforceNoDataAvail();
                            setRequest(_arg02);
                            break;
                        case 3:
                            flush();
                            break;
                        case 4:
                            String _arg03 = data.readString();
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            sendExtraCommand(_arg03, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ILocationProvider {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILocationProvider.DESCRIPTOR;
            }

            @Override // android.location.provider.ILocationProvider
            public void setLocationProviderManager(ILocationProviderManager manager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILocationProvider.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProvider
            public void setRequest(ProviderRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILocationProvider.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProvider
            public void flush() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILocationProvider.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProvider
            public void sendExtraCommand(String command, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILocationProvider.DESCRIPTOR);
                    _data.writeString(command);
                    _data.writeTypedObject(extras, 0);
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
