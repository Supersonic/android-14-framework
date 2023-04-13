package android.telephony.satellite;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISatelliteStateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.ISatelliteStateCallback";

    void onPendingDatagramCount(int i) throws RemoteException;

    void onSatelliteModemStateChanged(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatelliteStateCallback {
        @Override // android.telephony.satellite.ISatelliteStateCallback
        public void onPendingDatagramCount(int count) throws RemoteException {
        }

        @Override // android.telephony.satellite.ISatelliteStateCallback
        public void onSatelliteModemStateChanged(int state) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatelliteStateCallback {
        static final int TRANSACTION_onPendingDatagramCount = 1;
        static final int TRANSACTION_onSatelliteModemStateChanged = 2;

        public Stub() {
            attachInterface(this, ISatelliteStateCallback.DESCRIPTOR);
        }

        public static ISatelliteStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatelliteStateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatelliteStateCallback)) {
                return (ISatelliteStateCallback) iin;
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
                    return "onPendingDatagramCount";
                case 2:
                    return "onSatelliteModemStateChanged";
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
                data.enforceInterface(ISatelliteStateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatelliteStateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onPendingDatagramCount(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteModemStateChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISatelliteStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatelliteStateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.ISatelliteStateCallback
            public void onPendingDatagramCount(int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteStateCallback.DESCRIPTOR);
                    _data.writeInt(count);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.ISatelliteStateCallback
            public void onSatelliteModemStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteStateCallback.DESCRIPTOR);
                    _data.writeInt(state);
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
