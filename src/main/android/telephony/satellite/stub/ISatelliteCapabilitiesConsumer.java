package android.telephony.satellite.stub;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISatelliteCapabilitiesConsumer extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.stub.ISatelliteCapabilitiesConsumer";

    void accept(SatelliteCapabilities satelliteCapabilities) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatelliteCapabilitiesConsumer {
        @Override // android.telephony.satellite.stub.ISatelliteCapabilitiesConsumer
        public void accept(SatelliteCapabilities result) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatelliteCapabilitiesConsumer {
        static final int TRANSACTION_accept = 1;

        public Stub() {
            attachInterface(this, ISatelliteCapabilitiesConsumer.DESCRIPTOR);
        }

        public static ISatelliteCapabilitiesConsumer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatelliteCapabilitiesConsumer.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatelliteCapabilitiesConsumer)) {
                return (ISatelliteCapabilitiesConsumer) iin;
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
                    return "accept";
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
                data.enforceInterface(ISatelliteCapabilitiesConsumer.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatelliteCapabilitiesConsumer.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SatelliteCapabilities _arg0 = (SatelliteCapabilities) data.readTypedObject(SatelliteCapabilities.CREATOR);
                            data.enforceNoDataAvail();
                            accept(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISatelliteCapabilitiesConsumer {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatelliteCapabilitiesConsumer.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.stub.ISatelliteCapabilitiesConsumer
            public void accept(SatelliteCapabilities result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteCapabilitiesConsumer.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
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
