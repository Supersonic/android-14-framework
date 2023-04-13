package android.service.attention;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.attention.IAttentionCallback;
import android.service.attention.IProximityUpdateCallback;
/* loaded from: classes3.dex */
public interface IAttentionService extends IInterface {
    public static final String DESCRIPTOR = "android.service.attention.IAttentionService";

    void cancelAttentionCheck(IAttentionCallback iAttentionCallback) throws RemoteException;

    void checkAttention(IAttentionCallback iAttentionCallback) throws RemoteException;

    void onStartProximityUpdates(IProximityUpdateCallback iProximityUpdateCallback) throws RemoteException;

    void onStopProximityUpdates() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAttentionService {
        @Override // android.service.attention.IAttentionService
        public void checkAttention(IAttentionCallback callback) throws RemoteException {
        }

        @Override // android.service.attention.IAttentionService
        public void cancelAttentionCheck(IAttentionCallback callback) throws RemoteException {
        }

        @Override // android.service.attention.IAttentionService
        public void onStartProximityUpdates(IProximityUpdateCallback callback) throws RemoteException {
        }

        @Override // android.service.attention.IAttentionService
        public void onStopProximityUpdates() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAttentionService {
        static final int TRANSACTION_cancelAttentionCheck = 2;
        static final int TRANSACTION_checkAttention = 1;
        static final int TRANSACTION_onStartProximityUpdates = 3;
        static final int TRANSACTION_onStopProximityUpdates = 4;

        public Stub() {
            attachInterface(this, IAttentionService.DESCRIPTOR);
        }

        public static IAttentionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAttentionService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAttentionService)) {
                return (IAttentionService) iin;
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
                    return "checkAttention";
                case 2:
                    return "cancelAttentionCheck";
                case 3:
                    return "onStartProximityUpdates";
                case 4:
                    return "onStopProximityUpdates";
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
                data.enforceInterface(IAttentionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAttentionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IAttentionCallback _arg0 = IAttentionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            checkAttention(_arg0);
                            break;
                        case 2:
                            IAttentionCallback _arg02 = IAttentionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelAttentionCheck(_arg02);
                            break;
                        case 3:
                            IProximityUpdateCallback _arg03 = IProximityUpdateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onStartProximityUpdates(_arg03);
                            break;
                        case 4:
                            onStopProximityUpdates();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAttentionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAttentionService.DESCRIPTOR;
            }

            @Override // android.service.attention.IAttentionService
            public void checkAttention(IAttentionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttentionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.attention.IAttentionService
            public void cancelAttentionCheck(IAttentionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttentionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.attention.IAttentionService
            public void onStartProximityUpdates(IProximityUpdateCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttentionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.attention.IAttentionService
            public void onStopProximityUpdates() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttentionService.DESCRIPTOR);
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
