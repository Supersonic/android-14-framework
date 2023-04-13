package android.location.provider;

import android.location.Location;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface ILocationProviderManager extends IInterface {
    public static final String DESCRIPTOR = "android.location.provider.ILocationProviderManager";

    void onFlushComplete() throws RemoteException;

    void onInitialize(boolean z, ProviderProperties providerProperties, String str) throws RemoteException;

    void onReportLocation(Location location) throws RemoteException;

    void onReportLocations(List<Location> list) throws RemoteException;

    void onSetAllowed(boolean z) throws RemoteException;

    void onSetProperties(ProviderProperties providerProperties) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ILocationProviderManager {
        @Override // android.location.provider.ILocationProviderManager
        public void onInitialize(boolean allowed, ProviderProperties properties, String attributionTag) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProviderManager
        public void onSetAllowed(boolean allowed) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProviderManager
        public void onSetProperties(ProviderProperties properties) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProviderManager
        public void onReportLocation(Location location) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProviderManager
        public void onReportLocations(List<Location> locations) throws RemoteException {
        }

        @Override // android.location.provider.ILocationProviderManager
        public void onFlushComplete() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILocationProviderManager {
        static final int TRANSACTION_onFlushComplete = 6;
        static final int TRANSACTION_onInitialize = 1;
        static final int TRANSACTION_onReportLocation = 4;
        static final int TRANSACTION_onReportLocations = 5;
        static final int TRANSACTION_onSetAllowed = 2;
        static final int TRANSACTION_onSetProperties = 3;

        public Stub() {
            attachInterface(this, ILocationProviderManager.DESCRIPTOR);
        }

        public static ILocationProviderManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILocationProviderManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ILocationProviderManager)) {
                return (ILocationProviderManager) iin;
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
                    return "onInitialize";
                case 2:
                    return "onSetAllowed";
                case 3:
                    return "onSetProperties";
                case 4:
                    return "onReportLocation";
                case 5:
                    return "onReportLocations";
                case 6:
                    return "onFlushComplete";
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
                data.enforceInterface(ILocationProviderManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILocationProviderManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            ProviderProperties _arg1 = (ProviderProperties) data.readTypedObject(ProviderProperties.CREATOR);
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onInitialize(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onSetAllowed(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            ProviderProperties _arg03 = (ProviderProperties) data.readTypedObject(ProviderProperties.CREATOR);
                            data.enforceNoDataAvail();
                            onSetProperties(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            Location _arg04 = (Location) data.readTypedObject(Location.CREATOR);
                            data.enforceNoDataAvail();
                            onReportLocation(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            List<Location> _arg05 = data.createTypedArrayList(Location.CREATOR);
                            data.enforceNoDataAvail();
                            onReportLocations(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            onFlushComplete();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ILocationProviderManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILocationProviderManager.DESCRIPTOR;
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onInitialize(boolean allowed, ProviderProperties properties, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    _data.writeBoolean(allowed);
                    _data.writeTypedObject(properties, 0);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onSetAllowed(boolean allowed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    _data.writeBoolean(allowed);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onSetProperties(ProviderProperties properties) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    _data.writeTypedObject(properties, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onReportLocation(Location location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onReportLocations(List<Location> locations) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    _data.writeTypedList(locations, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.provider.ILocationProviderManager
            public void onFlushComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILocationProviderManager.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
