package android.telephony.satellite;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISatellitePositionUpdateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.ISatellitePositionUpdateCallback";

    void onDatagramTransferStateChanged(int i, int i2, int i3, int i4) throws RemoteException;

    void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatellitePositionUpdateCallback {
        @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
        public void onDatagramTransferStateChanged(int state, int sendPendingCount, int receivePendingCount, int errorCode) throws RemoteException {
        }

        @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
        public void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatellitePositionUpdateCallback {
        static final int TRANSACTION_onDatagramTransferStateChanged = 1;
        static final int TRANSACTION_onSatellitePositionChanged = 2;

        public Stub() {
            attachInterface(this, ISatellitePositionUpdateCallback.DESCRIPTOR);
        }

        public static ISatellitePositionUpdateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatellitePositionUpdateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatellitePositionUpdateCallback)) {
                return (ISatellitePositionUpdateCallback) iin;
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
                    return "onDatagramTransferStateChanged";
                case 2:
                    return "onSatellitePositionChanged";
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
                data.enforceInterface(ISatellitePositionUpdateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatellitePositionUpdateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onDatagramTransferStateChanged(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            PointingInfo _arg02 = (PointingInfo) data.readTypedObject(PointingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onSatellitePositionChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISatellitePositionUpdateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatellitePositionUpdateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
            public void onDatagramTransferStateChanged(int state, int sendPendingCount, int receivePendingCount, int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellitePositionUpdateCallback.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(sendPendingCount);
                    _data.writeInt(receivePendingCount);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
            public void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellitePositionUpdateCallback.DESCRIPTOR);
                    _data.writeTypedObject(pointingInfo, 0);
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
