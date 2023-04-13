package android.nfc;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface INfcDta extends IInterface {
    void disableClient() throws RemoteException;

    void disableDta() throws RemoteException;

    void disableServer() throws RemoteException;

    boolean enableClient(String str, int i, int i2, int i3) throws RemoteException;

    void enableDta() throws RemoteException;

    boolean enableServer(String str, int i, int i2, int i3, int i4) throws RemoteException;

    boolean registerMessageService(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INfcDta {
        @Override // android.nfc.INfcDta
        public void enableDta() throws RemoteException {
        }

        @Override // android.nfc.INfcDta
        public void disableDta() throws RemoteException {
        }

        @Override // android.nfc.INfcDta
        public boolean enableServer(String serviceName, int serviceSap, int miu, int rwSize, int testCaseId) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcDta
        public void disableServer() throws RemoteException {
        }

        @Override // android.nfc.INfcDta
        public boolean enableClient(String serviceName, int miu, int rwSize, int testCaseId) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcDta
        public void disableClient() throws RemoteException {
        }

        @Override // android.nfc.INfcDta
        public boolean registerMessageService(String msgServiceName) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INfcDta {
        public static final String DESCRIPTOR = "android.nfc.INfcDta";
        static final int TRANSACTION_disableClient = 6;
        static final int TRANSACTION_disableDta = 2;
        static final int TRANSACTION_disableServer = 4;
        static final int TRANSACTION_enableClient = 5;
        static final int TRANSACTION_enableDta = 1;
        static final int TRANSACTION_enableServer = 3;
        static final int TRANSACTION_registerMessageService = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INfcDta asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INfcDta)) {
                return (INfcDta) iin;
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
                    return "enableDta";
                case 2:
                    return "disableDta";
                case 3:
                    return "enableServer";
                case 4:
                    return "disableServer";
                case 5:
                    return "enableClient";
                case 6:
                    return "disableClient";
                case 7:
                    return "registerMessageService";
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
                            enableDta();
                            reply.writeNoException();
                            break;
                        case 2:
                            disableDta();
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = enableServer(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            disableServer();
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = enableClient(_arg02, _arg12, _arg22, _arg32);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 6:
                            disableClient();
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = registerMessageService(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements INfcDta {
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

            @Override // android.nfc.INfcDta
            public void enableDta() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcDta
            public void disableDta() throws RemoteException {
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

            @Override // android.nfc.INfcDta
            public boolean enableServer(String serviceName, int serviceSap, int miu, int rwSize, int testCaseId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(serviceName);
                    _data.writeInt(serviceSap);
                    _data.writeInt(miu);
                    _data.writeInt(rwSize);
                    _data.writeInt(testCaseId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcDta
            public void disableServer() throws RemoteException {
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

            @Override // android.nfc.INfcDta
            public boolean enableClient(String serviceName, int miu, int rwSize, int testCaseId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(serviceName);
                    _data.writeInt(miu);
                    _data.writeInt(rwSize);
                    _data.writeInt(testCaseId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcDta
            public void disableClient() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcDta
            public boolean registerMessageService(String msgServiceName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(msgServiceName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
