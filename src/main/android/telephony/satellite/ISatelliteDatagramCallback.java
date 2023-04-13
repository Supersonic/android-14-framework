package android.telephony.satellite;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telephony.ILongConsumer;
/* loaded from: classes3.dex */
public interface ISatelliteDatagramCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.ISatelliteDatagramCallback";

    void onSatelliteDatagramReceived(long j, SatelliteDatagram satelliteDatagram, int i, ILongConsumer iLongConsumer) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatelliteDatagramCallback {
        @Override // android.telephony.satellite.ISatelliteDatagramCallback
        public void onSatelliteDatagramReceived(long datagramId, SatelliteDatagram datagram, int pendingCount, ILongConsumer callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatelliteDatagramCallback {
        static final int TRANSACTION_onSatelliteDatagramReceived = 1;

        public Stub() {
            attachInterface(this, ISatelliteDatagramCallback.DESCRIPTOR);
        }

        public static ISatelliteDatagramCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatelliteDatagramCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatelliteDatagramCallback)) {
                return (ISatelliteDatagramCallback) iin;
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
                    return "onSatelliteDatagramReceived";
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
                data.enforceInterface(ISatelliteDatagramCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatelliteDatagramCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            SatelliteDatagram _arg1 = (SatelliteDatagram) data.readTypedObject(SatelliteDatagram.CREATOR);
                            int _arg2 = data.readInt();
                            ILongConsumer _arg3 = ILongConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSatelliteDatagramReceived(_arg0, _arg1, _arg2, _arg3);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISatelliteDatagramCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatelliteDatagramCallback.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.ISatelliteDatagramCallback
            public void onSatelliteDatagramReceived(long datagramId, SatelliteDatagram datagram, int pendingCount, ILongConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteDatagramCallback.DESCRIPTOR);
                    _data.writeLong(datagramId);
                    _data.writeTypedObject(datagram, 0);
                    _data.writeInt(pendingCount);
                    _data.writeStrongInterface(callback);
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
