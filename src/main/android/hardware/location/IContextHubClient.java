package android.hardware.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IContextHubClient extends IInterface {
    void callbackFinished() throws RemoteException;

    void close() throws RemoteException;

    int getId() throws RemoteException;

    int sendMessageToNanoApp(NanoAppMessage nanoAppMessage) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IContextHubClient {
        @Override // android.hardware.location.IContextHubClient
        public int sendMessageToNanoApp(NanoAppMessage message) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubClient
        public void close() throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClient
        public int getId() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubClient
        public void callbackFinished() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IContextHubClient {
        public static final String DESCRIPTOR = "android.hardware.location.IContextHubClient";
        static final int TRANSACTION_callbackFinished = 4;
        static final int TRANSACTION_close = 2;
        static final int TRANSACTION_getId = 3;
        static final int TRANSACTION_sendMessageToNanoApp = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContextHubClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContextHubClient)) {
                return (IContextHubClient) iin;
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
                    return "sendMessageToNanoApp";
                case 2:
                    return "close";
                case 3:
                    return "getId";
                case 4:
                    return "callbackFinished";
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
                            NanoAppMessage _arg0 = (NanoAppMessage) data.readTypedObject(NanoAppMessage.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = sendMessageToNanoApp(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            close();
                            reply.writeNoException();
                            break;
                        case 3:
                            int _result2 = getId();
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 4:
                            callbackFinished();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IContextHubClient {
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

            @Override // android.hardware.location.IContextHubClient
            public int sendMessageToNanoApp(NanoAppMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(message, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClient
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClient
            public int getId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClient
            public void callbackFinished() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
