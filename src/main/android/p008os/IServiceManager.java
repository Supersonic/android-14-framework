package android.p008os;

import android.p008os.IClientCallback;
import android.p008os.IServiceCallback;
/* renamed from: android.os.IServiceManager */
/* loaded from: classes3.dex */
public interface IServiceManager extends IInterface {
    public static final String DESCRIPTOR = "android.os.IServiceManager";
    public static final int DUMP_FLAG_PRIORITY_ALL = 15;
    public static final int DUMP_FLAG_PRIORITY_CRITICAL = 1;
    public static final int DUMP_FLAG_PRIORITY_DEFAULT = 8;
    public static final int DUMP_FLAG_PRIORITY_HIGH = 2;
    public static final int DUMP_FLAG_PRIORITY_NORMAL = 4;
    public static final int DUMP_FLAG_PROTO = 16;

    void addService(String str, IBinder iBinder, boolean z, int i) throws RemoteException;

    IBinder checkService(String str) throws RemoteException;

    ConnectionInfo getConnectionInfo(String str) throws RemoteException;

    String[] getDeclaredInstances(String str) throws RemoteException;

    IBinder getService(String str) throws RemoteException;

    ServiceDebugInfo[] getServiceDebugInfo() throws RemoteException;

    String[] getUpdatableNames(String str) throws RemoteException;

    boolean isDeclared(String str) throws RemoteException;

    String[] listServices(int i) throws RemoteException;

    void registerClientCallback(String str, IBinder iBinder, IClientCallback iClientCallback) throws RemoteException;

    void registerForNotifications(String str, IServiceCallback iServiceCallback) throws RemoteException;

    void tryUnregisterService(String str, IBinder iBinder) throws RemoteException;

    void unregisterForNotifications(String str, IServiceCallback iServiceCallback) throws RemoteException;

    String updatableViaApex(String str) throws RemoteException;

    /* renamed from: android.os.IServiceManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IServiceManager {
        @Override // android.p008os.IServiceManager
        public IBinder getService(String name) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public IBinder checkService(String name) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public void addService(String name, IBinder service, boolean allowIsolated, int dumpPriority) throws RemoteException {
        }

        @Override // android.p008os.IServiceManager
        public String[] listServices(int dumpPriority) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public void registerForNotifications(String name, IServiceCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IServiceManager
        public void unregisterForNotifications(String name, IServiceCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IServiceManager
        public boolean isDeclared(String name) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IServiceManager
        public String[] getDeclaredInstances(String iface) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public String updatableViaApex(String name) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public String[] getUpdatableNames(String apexName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public ConnectionInfo getConnectionInfo(String name) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IServiceManager
        public void registerClientCallback(String name, IBinder service, IClientCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IServiceManager
        public void tryUnregisterService(String name, IBinder service) throws RemoteException {
        }

        @Override // android.p008os.IServiceManager
        public ServiceDebugInfo[] getServiceDebugInfo() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IServiceManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IServiceManager {
        static final int TRANSACTION_addService = 3;
        static final int TRANSACTION_checkService = 2;
        static final int TRANSACTION_getConnectionInfo = 11;
        static final int TRANSACTION_getDeclaredInstances = 8;
        static final int TRANSACTION_getService = 1;
        static final int TRANSACTION_getServiceDebugInfo = 14;
        static final int TRANSACTION_getUpdatableNames = 10;
        static final int TRANSACTION_isDeclared = 7;
        static final int TRANSACTION_listServices = 4;
        static final int TRANSACTION_registerClientCallback = 12;
        static final int TRANSACTION_registerForNotifications = 5;
        static final int TRANSACTION_tryUnregisterService = 13;
        static final int TRANSACTION_unregisterForNotifications = 6;
        static final int TRANSACTION_updatableViaApex = 9;

        public Stub() {
            attachInterface(this, IServiceManager.DESCRIPTOR);
        }

        public static IServiceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IServiceManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IServiceManager)) {
                return (IServiceManager) iin;
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
                    return "getService";
                case 2:
                    return "checkService";
                case 3:
                    return "addService";
                case 4:
                    return "listServices";
                case 5:
                    return "registerForNotifications";
                case 6:
                    return "unregisterForNotifications";
                case 7:
                    return "isDeclared";
                case 8:
                    return "getDeclaredInstances";
                case 9:
                    return "updatableViaApex";
                case 10:
                    return "getUpdatableNames";
                case 11:
                    return "getConnectionInfo";
                case 12:
                    return "registerClientCallback";
                case 13:
                    return "tryUnregisterService";
                case 14:
                    return "getServiceDebugInfo";
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
                data.enforceInterface(IServiceManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IServiceManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            IBinder _result = getService(_arg0);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            IBinder _result2 = checkService(_arg02);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            IBinder _arg1 = data.readStrongBinder();
                            boolean _arg2 = data.readBoolean();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            addService(_arg03, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result3 = listServices(_arg04);
                            reply.writeNoException();
                            reply.writeStringArray(_result3);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            IServiceCallback _arg12 = IServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerForNotifications(_arg05, _arg12);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            IServiceCallback _arg13 = IServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterForNotifications(_arg06, _arg13);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = isDeclared(_arg07);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result5 = getDeclaredInstances(_arg08);
                            reply.writeNoException();
                            reply.writeStringArray(_result5);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            String _result6 = updatableViaApex(_arg09);
                            reply.writeNoException();
                            reply.writeString(_result6);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result7 = getUpdatableNames(_arg010);
                            reply.writeNoException();
                            reply.writeStringArray(_result7);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            ConnectionInfo _result8 = getConnectionInfo(_arg011);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            IBinder _arg14 = data.readStrongBinder();
                            IClientCallback _arg22 = IClientCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerClientCallback(_arg012, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            IBinder _arg15 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            tryUnregisterService(_arg013, _arg15);
                            reply.writeNoException();
                            break;
                        case 14:
                            ServiceDebugInfo[] _result9 = getServiceDebugInfo();
                            reply.writeNoException();
                            reply.writeTypedArray(_result9, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IServiceManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IServiceManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IServiceManager.DESCRIPTOR;
            }

            @Override // android.p008os.IServiceManager
            public IBinder getService(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public IBinder checkService(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public void addService(String name, IBinder service, boolean allowIsolated, int dumpPriority) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(service);
                    _data.writeBoolean(allowIsolated);
                    _data.writeInt(dumpPriority);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public String[] listServices(int dumpPriority) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeInt(dumpPriority);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public void registerForNotifications(String name, IServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public void unregisterForNotifications(String name, IServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public boolean isDeclared(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public String[] getDeclaredInstances(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public String updatableViaApex(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public String[] getUpdatableNames(String apexName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(apexName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public ConnectionInfo getConnectionInfo(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    ConnectionInfo _result = (ConnectionInfo) _reply.readTypedObject(ConnectionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public void registerClientCallback(String name, IBinder service, IClientCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(service);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public void tryUnregisterService(String name, IBinder service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongBinder(service);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IServiceManager
            public ServiceDebugInfo[] getServiceDebugInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IServiceManager.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    ServiceDebugInfo[] _result = (ServiceDebugInfo[]) _reply.createTypedArray(ServiceDebugInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
