package android.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IGnssNmeaListener extends IInterface {
    public static final String DESCRIPTOR = "android.location.IGnssNmeaListener";

    void onNmeaReceived(long j, String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IGnssNmeaListener {
        @Override // android.location.IGnssNmeaListener
        public void onNmeaReceived(long timestamp, String nmea) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IGnssNmeaListener {
        static final int TRANSACTION_onNmeaReceived = 1;

        public Stub() {
            attachInterface(this, IGnssNmeaListener.DESCRIPTOR);
        }

        public static IGnssNmeaListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGnssNmeaListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IGnssNmeaListener)) {
                return (IGnssNmeaListener) iin;
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
                    return "onNmeaReceived";
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
                data.enforceInterface(IGnssNmeaListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGnssNmeaListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onNmeaReceived(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IGnssNmeaListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGnssNmeaListener.DESCRIPTOR;
            }

            @Override // android.location.IGnssNmeaListener
            public void onNmeaReceived(long timestamp, String nmea) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGnssNmeaListener.DESCRIPTOR);
                    _data.writeLong(timestamp);
                    _data.writeString(nmea);
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
