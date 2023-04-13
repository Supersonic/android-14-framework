package android.telephony.data;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.data.IQualifiedNetworksServiceCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IQualifiedNetworksService extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.data.IQualifiedNetworksService";

    void createNetworkAvailabilityProvider(int i, IQualifiedNetworksServiceCallback iQualifiedNetworksServiceCallback) throws RemoteException;

    void removeNetworkAvailabilityProvider(int i) throws RemoteException;

    void reportEmergencyDataNetworkPreferredTransportChanged(int i, int i2) throws RemoteException;

    void reportThrottleStatusChanged(int i, List<ThrottleStatus> list) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IQualifiedNetworksService {
        @Override // android.telephony.data.IQualifiedNetworksService
        public void createNetworkAvailabilityProvider(int slotId, IQualifiedNetworksServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IQualifiedNetworksService
        public void removeNetworkAvailabilityProvider(int slotId) throws RemoteException {
        }

        @Override // android.telephony.data.IQualifiedNetworksService
        public void reportThrottleStatusChanged(int slotId, List<ThrottleStatus> statuses) throws RemoteException {
        }

        @Override // android.telephony.data.IQualifiedNetworksService
        public void reportEmergencyDataNetworkPreferredTransportChanged(int slotId, int transportType) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IQualifiedNetworksService {
        static final int TRANSACTION_createNetworkAvailabilityProvider = 1;
        static final int TRANSACTION_removeNetworkAvailabilityProvider = 2;
        static final int TRANSACTION_reportEmergencyDataNetworkPreferredTransportChanged = 4;
        static final int TRANSACTION_reportThrottleStatusChanged = 3;

        public Stub() {
            attachInterface(this, IQualifiedNetworksService.DESCRIPTOR);
        }

        public static IQualifiedNetworksService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IQualifiedNetworksService.DESCRIPTOR);
            if (iin != null && (iin instanceof IQualifiedNetworksService)) {
                return (IQualifiedNetworksService) iin;
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
                    return "createNetworkAvailabilityProvider";
                case 2:
                    return "removeNetworkAvailabilityProvider";
                case 3:
                    return "reportThrottleStatusChanged";
                case 4:
                    return "reportEmergencyDataNetworkPreferredTransportChanged";
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
                data.enforceInterface(IQualifiedNetworksService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IQualifiedNetworksService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            IQualifiedNetworksServiceCallback _arg1 = IQualifiedNetworksServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createNetworkAvailabilityProvider(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            removeNetworkAvailabilityProvider(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            List<ThrottleStatus> _arg12 = data.createTypedArrayList(ThrottleStatus.CREATOR);
                            data.enforceNoDataAvail();
                            reportThrottleStatusChanged(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            reportEmergencyDataNetworkPreferredTransportChanged(_arg04, _arg13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IQualifiedNetworksService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IQualifiedNetworksService.DESCRIPTOR;
            }

            @Override // android.telephony.data.IQualifiedNetworksService
            public void createNetworkAvailabilityProvider(int slotId, IQualifiedNetworksServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQualifiedNetworksService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IQualifiedNetworksService
            public void removeNetworkAvailabilityProvider(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQualifiedNetworksService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IQualifiedNetworksService
            public void reportThrottleStatusChanged(int slotId, List<ThrottleStatus> statuses) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQualifiedNetworksService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeTypedList(statuses, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IQualifiedNetworksService
            public void reportEmergencyDataNetworkPreferredTransportChanged(int slotId, int transportType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQualifiedNetworksService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(transportType);
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
