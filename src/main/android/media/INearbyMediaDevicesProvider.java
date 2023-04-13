package android.media;

import android.media.INearbyMediaDevicesUpdateCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface INearbyMediaDevicesProvider extends IInterface {
    public static final String DESCRIPTOR = "android.media.INearbyMediaDevicesProvider";

    void registerNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback iNearbyMediaDevicesUpdateCallback) throws RemoteException;

    void unregisterNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback iNearbyMediaDevicesUpdateCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INearbyMediaDevicesProvider {
        @Override // android.media.INearbyMediaDevicesProvider
        public void registerNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback callback) throws RemoteException {
        }

        @Override // android.media.INearbyMediaDevicesProvider
        public void unregisterNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INearbyMediaDevicesProvider {
        static final int TRANSACTION_registerNearbyDevicesCallback = 3;
        static final int TRANSACTION_unregisterNearbyDevicesCallback = 4;

        public Stub() {
            attachInterface(this, INearbyMediaDevicesProvider.DESCRIPTOR);
        }

        public static INearbyMediaDevicesProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(INearbyMediaDevicesProvider.DESCRIPTOR);
            if (iin != null && (iin instanceof INearbyMediaDevicesProvider)) {
                return (INearbyMediaDevicesProvider) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 3:
                    return "registerNearbyDevicesCallback";
                case 4:
                    return "unregisterNearbyDevicesCallback";
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
                data.enforceInterface(INearbyMediaDevicesProvider.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(INearbyMediaDevicesProvider.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 3:
                            INearbyMediaDevicesUpdateCallback _arg0 = INearbyMediaDevicesUpdateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerNearbyDevicesCallback(_arg0);
                            break;
                        case 4:
                            INearbyMediaDevicesUpdateCallback _arg02 = INearbyMediaDevicesUpdateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterNearbyDevicesCallback(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INearbyMediaDevicesProvider {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return INearbyMediaDevicesProvider.DESCRIPTOR;
            }

            @Override // android.media.INearbyMediaDevicesProvider
            public void registerNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(INearbyMediaDevicesProvider.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.INearbyMediaDevicesProvider
            public void unregisterNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(INearbyMediaDevicesProvider.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
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
