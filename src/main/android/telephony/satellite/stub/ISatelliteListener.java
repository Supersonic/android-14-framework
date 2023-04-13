package android.telephony.satellite.stub;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISatelliteListener extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.stub.ISatelliteListener";

    void onPendingDatagramCount(int i) throws RemoteException;

    void onSatelliteDatagramReceived(SatelliteDatagram satelliteDatagram, int i) throws RemoteException;

    void onSatelliteModemStateChanged(int i) throws RemoteException;

    void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException;

    void onSatelliteProvisionStateChanged(boolean z) throws RemoteException;

    void onSatelliteRadioTechnologyChanged(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatelliteListener {
        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onSatelliteProvisionStateChanged(boolean provisioned) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onSatelliteDatagramReceived(SatelliteDatagram datagram, int pendingCount) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onPendingDatagramCount(int count) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onSatelliteModemStateChanged(int state) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatelliteListener
        public void onSatelliteRadioTechnologyChanged(int technology) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatelliteListener {
        static final int TRANSACTION_onPendingDatagramCount = 3;
        static final int TRANSACTION_onSatelliteDatagramReceived = 2;
        static final int TRANSACTION_onSatelliteModemStateChanged = 5;
        static final int TRANSACTION_onSatellitePositionChanged = 4;
        static final int TRANSACTION_onSatelliteProvisionStateChanged = 1;
        static final int TRANSACTION_onSatelliteRadioTechnologyChanged = 6;

        public Stub() {
            attachInterface(this, ISatelliteListener.DESCRIPTOR);
        }

        public static ISatelliteListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatelliteListener.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatelliteListener)) {
                return (ISatelliteListener) iin;
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
                case 2:
                    return "onSatelliteDatagramReceived";
                case 3:
                    return "onPendingDatagramCount";
                case 4:
                    return "onSatellitePositionChanged";
                case 5:
                    return "onSatelliteModemStateChanged";
                case 6:
                    return "onSatelliteRadioTechnologyChanged";
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
                data.enforceInterface(ISatelliteListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatelliteListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onSatelliteProvisionStateChanged(_arg0);
                            break;
                        case 2:
                            SatelliteDatagram _arg02 = (SatelliteDatagram) data.readTypedObject(SatelliteDatagram.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteDatagramReceived(_arg02, _arg1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onPendingDatagramCount(_arg03);
                            break;
                        case 4:
                            PointingInfo _arg04 = (PointingInfo) data.readTypedObject(PointingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onSatellitePositionChanged(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteModemStateChanged(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteRadioTechnologyChanged(_arg06);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISatelliteListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatelliteListener.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onSatelliteProvisionStateChanged(boolean provisioned) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeBoolean(provisioned);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onSatelliteDatagramReceived(SatelliteDatagram datagram, int pendingCount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeTypedObject(datagram, 0);
                    _data.writeInt(pendingCount);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onPendingDatagramCount(int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeInt(count);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onSatellitePositionChanged(PointingInfo pointingInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeTypedObject(pointingInfo, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onSatelliteModemStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatelliteListener
            public void onSatelliteRadioTechnologyChanged(int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatelliteListener.DESCRIPTOR);
                    _data.writeInt(technology);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
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
