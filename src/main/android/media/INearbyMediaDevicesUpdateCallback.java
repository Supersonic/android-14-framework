package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface INearbyMediaDevicesUpdateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.INearbyMediaDevicesUpdateCallback";

    void onDevicesUpdated(List<NearbyDevice> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INearbyMediaDevicesUpdateCallback {
        @Override // android.media.INearbyMediaDevicesUpdateCallback
        public void onDevicesUpdated(List<NearbyDevice> nearbyDevices) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INearbyMediaDevicesUpdateCallback {
        static final int TRANSACTION_onDevicesUpdated = 1;

        public Stub() {
            attachInterface(this, INearbyMediaDevicesUpdateCallback.DESCRIPTOR);
        }

        public static INearbyMediaDevicesUpdateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(INearbyMediaDevicesUpdateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof INearbyMediaDevicesUpdateCallback)) {
                return (INearbyMediaDevicesUpdateCallback) iin;
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
                    return "onDevicesUpdated";
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
                data.enforceInterface(INearbyMediaDevicesUpdateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(INearbyMediaDevicesUpdateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<NearbyDevice> _arg0 = data.createTypedArrayList(NearbyDevice.CREATOR);
                            data.enforceNoDataAvail();
                            onDevicesUpdated(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INearbyMediaDevicesUpdateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return INearbyMediaDevicesUpdateCallback.DESCRIPTOR;
            }

            @Override // android.media.INearbyMediaDevicesUpdateCallback
            public void onDevicesUpdated(List<NearbyDevice> nearbyDevices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(INearbyMediaDevicesUpdateCallback.DESCRIPTOR);
                    _data.writeTypedList(nearbyDevices, 0);
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
