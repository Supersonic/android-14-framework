package android.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IRemoteCallback;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface ILocationListener extends IInterface {
    void onFlushComplete(int i) throws RemoteException;

    void onLocationChanged(List<Location> list, IRemoteCallback iRemoteCallback) throws RemoteException;

    void onProviderEnabledChanged(String str, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ILocationListener {
        @Override // android.location.ILocationListener
        public void onLocationChanged(List<Location> locations, IRemoteCallback onCompleteCallback) throws RemoteException {
        }

        @Override // android.location.ILocationListener
        public void onProviderEnabledChanged(String provider, boolean enabled) throws RemoteException {
        }

        @Override // android.location.ILocationListener
        public void onFlushComplete(int requestCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILocationListener {
        public static final String DESCRIPTOR = "android.location.ILocationListener";
        static final int TRANSACTION_onFlushComplete = 3;
        static final int TRANSACTION_onLocationChanged = 1;
        static final int TRANSACTION_onProviderEnabledChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILocationListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILocationListener)) {
                return (ILocationListener) iin;
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
                    return "onLocationChanged";
                case 2:
                    return "onProviderEnabledChanged";
                case 3:
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<Location> _arg0 = data.createTypedArrayList(Location.CREATOR);
                            IRemoteCallback _arg1 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onLocationChanged(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onProviderEnabledChanged(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onFlushComplete(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ILocationListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.location.ILocationListener
            public void onLocationChanged(List<Location> locations, IRemoteCallback onCompleteCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(locations, 0);
                    _data.writeStrongInterface(onCompleteCallback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationListener
            public void onProviderEnabledChanged(String provider, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.location.ILocationListener
            public void onFlushComplete(int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestCode);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
