package android.telephony.satellite;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISatelliteProvisionStateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.ISatelliteProvisionStateCallback";

    void onSatelliteProvisionStateChanged(boolean z) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatelliteProvisionStateCallback {
        @Override // android.telephony.satellite.ISatelliteProvisionStateCallback
        public void onSatelliteProvisionStateChanged(boolean provisioned) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatelliteProvisionStateCallback {
        static final int TRANSACTION_onSatelliteProvisionStateChanged = 1;

        public Stub() {
            attachInterface(this, ISatelliteProvisionStateCallback.DESCRIPTOR);
        }

        public static ISatelliteProvisionStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatelliteProvisionStateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatelliteProvisionStateCallback)) {
                return (ISatelliteProvisionStateCallback) iin;
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
                    return "onSatelliteProvisionStateChanged";
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
                data.enforceInterface(ISatelliteProvisionStateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatelliteProvisionStateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onSatelliteProvisionStateChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISatelliteProvisionStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatelliteProvisionStateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.ISatelliteProvisionStateCallback
            public void onSatelliteProvisionStateChanged(boolean provisioned) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteProvisionStateCallback.DESCRIPTOR);
                    _data.writeBoolean(provisioned);
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
