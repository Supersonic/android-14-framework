package android.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.INetworkServiceCallback;
/* loaded from: classes3.dex */
public interface INetworkService extends IInterface {
    void createNetworkServiceProvider(int i) throws RemoteException;

    void registerForNetworkRegistrationInfoChanged(int i, INetworkServiceCallback iNetworkServiceCallback) throws RemoteException;

    void removeNetworkServiceProvider(int i) throws RemoteException;

    void requestNetworkRegistrationInfo(int i, int i2, INetworkServiceCallback iNetworkServiceCallback) throws RemoteException;

    void unregisterForNetworkRegistrationInfoChanged(int i, INetworkServiceCallback iNetworkServiceCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements INetworkService {
        @Override // android.telephony.INetworkService
        public void createNetworkServiceProvider(int slotId) throws RemoteException {
        }

        @Override // android.telephony.INetworkService
        public void removeNetworkServiceProvider(int slotId) throws RemoteException {
        }

        @Override // android.telephony.INetworkService
        public void requestNetworkRegistrationInfo(int slotId, int domain, INetworkServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.INetworkService
        public void registerForNetworkRegistrationInfoChanged(int slotId, INetworkServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.INetworkService
        public void unregisterForNetworkRegistrationInfoChanged(int slotId, INetworkServiceCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements INetworkService {
        public static final String DESCRIPTOR = "android.telephony.INetworkService";
        static final int TRANSACTION_createNetworkServiceProvider = 1;
        static final int TRANSACTION_registerForNetworkRegistrationInfoChanged = 4;
        static final int TRANSACTION_removeNetworkServiceProvider = 2;
        static final int TRANSACTION_requestNetworkRegistrationInfo = 3;
        static final int TRANSACTION_unregisterForNetworkRegistrationInfoChanged = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkService)) {
                return (INetworkService) iin;
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
                    return "createNetworkServiceProvider";
                case 2:
                    return "removeNetworkServiceProvider";
                case 3:
                    return "requestNetworkRegistrationInfo";
                case 4:
                    return "registerForNetworkRegistrationInfoChanged";
                case 5:
                    return "unregisterForNetworkRegistrationInfoChanged";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            createNetworkServiceProvider(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            removeNetworkServiceProvider(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg1 = data.readInt();
                            INetworkServiceCallback _arg2 = INetworkServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestNetworkRegistrationInfo(_arg03, _arg1, _arg2);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            INetworkServiceCallback _arg12 = INetworkServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerForNetworkRegistrationInfoChanged(_arg04, _arg12);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            INetworkServiceCallback _arg13 = INetworkServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterForNetworkRegistrationInfoChanged(_arg05, _arg13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements INetworkService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.telephony.INetworkService
            public void createNetworkServiceProvider(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.INetworkService
            public void removeNetworkServiceProvider(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.INetworkService
            public void requestNetworkRegistrationInfo(int slotId, int domain, INetworkServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(domain);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.INetworkService
            public void registerForNetworkRegistrationInfoChanged(int slotId, INetworkServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.INetworkService
            public void unregisterForNetworkRegistrationInfoChanged(int slotId, INetworkServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
