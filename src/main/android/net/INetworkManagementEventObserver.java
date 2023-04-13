package android.net;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface INetworkManagementEventObserver extends IInterface {
    void addressRemoved(String str, LinkAddress linkAddress) throws RemoteException;

    void addressUpdated(String str, LinkAddress linkAddress) throws RemoteException;

    void interfaceAdded(String str) throws RemoteException;

    void interfaceClassDataActivityChanged(int i, boolean z, long j, int i2) throws RemoteException;

    void interfaceDnsServerInfo(String str, long j, String[] strArr) throws RemoteException;

    void interfaceLinkStateChanged(String str, boolean z) throws RemoteException;

    void interfaceRemoved(String str) throws RemoteException;

    void interfaceStatusChanged(String str, boolean z) throws RemoteException;

    void limitReached(String str, String str2) throws RemoteException;

    void routeRemoved(RouteInfo routeInfo) throws RemoteException;

    void routeUpdated(RouteInfo routeInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INetworkManagementEventObserver {
        @Override // android.net.INetworkManagementEventObserver
        public void interfaceStatusChanged(String iface, boolean up) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void interfaceLinkStateChanged(String iface, boolean up) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void interfaceAdded(String iface) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void interfaceRemoved(String iface) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void addressUpdated(String iface, LinkAddress address) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void addressRemoved(String iface, LinkAddress address) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void limitReached(String limitName, String iface) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void interfaceClassDataActivityChanged(int transportType, boolean active, long tsNanos, int uid) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void interfaceDnsServerInfo(String iface, long lifetime, String[] servers) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void routeUpdated(RouteInfo route) throws RemoteException {
        }

        @Override // android.net.INetworkManagementEventObserver
        public void routeRemoved(RouteInfo route) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INetworkManagementEventObserver {
        public static final String DESCRIPTOR = "android.net.INetworkManagementEventObserver";
        static final int TRANSACTION_addressRemoved = 6;
        static final int TRANSACTION_addressUpdated = 5;
        static final int TRANSACTION_interfaceAdded = 3;
        static final int TRANSACTION_interfaceClassDataActivityChanged = 8;
        static final int TRANSACTION_interfaceDnsServerInfo = 9;
        static final int TRANSACTION_interfaceLinkStateChanged = 2;
        static final int TRANSACTION_interfaceRemoved = 4;
        static final int TRANSACTION_interfaceStatusChanged = 1;
        static final int TRANSACTION_limitReached = 7;
        static final int TRANSACTION_routeRemoved = 11;
        static final int TRANSACTION_routeUpdated = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkManagementEventObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkManagementEventObserver)) {
                return (INetworkManagementEventObserver) iin;
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
                    return "interfaceStatusChanged";
                case 2:
                    return "interfaceLinkStateChanged";
                case 3:
                    return "interfaceAdded";
                case 4:
                    return "interfaceRemoved";
                case 5:
                    return "addressUpdated";
                case 6:
                    return "addressRemoved";
                case 7:
                    return "limitReached";
                case 8:
                    return "interfaceClassDataActivityChanged";
                case 9:
                    return "interfaceDnsServerInfo";
                case 10:
                    return "routeUpdated";
                case 11:
                    return "routeRemoved";
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
                            String _arg0 = data.readString();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            interfaceStatusChanged(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            interfaceLinkStateChanged(_arg02, _arg12);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            interfaceAdded(_arg03);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            interfaceRemoved(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            LinkAddress _arg13 = (LinkAddress) data.readTypedObject(LinkAddress.CREATOR);
                            data.enforceNoDataAvail();
                            addressUpdated(_arg05, _arg13);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            LinkAddress _arg14 = (LinkAddress) data.readTypedObject(LinkAddress.CREATOR);
                            data.enforceNoDataAvail();
                            addressRemoved(_arg06, _arg14);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            limitReached(_arg07, _arg15);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            long _arg2 = data.readLong();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            interfaceClassDataActivityChanged(_arg08, _arg16, _arg2, _arg3);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            long _arg17 = data.readLong();
                            String[] _arg22 = data.createStringArray();
                            data.enforceNoDataAvail();
                            interfaceDnsServerInfo(_arg09, _arg17, _arg22);
                            break;
                        case 10:
                            RouteInfo _arg010 = (RouteInfo) data.readTypedObject(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            routeUpdated(_arg010);
                            break;
                        case 11:
                            RouteInfo _arg011 = (RouteInfo) data.readTypedObject(RouteInfo.CREATOR);
                            data.enforceNoDataAvail();
                            routeRemoved(_arg011);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INetworkManagementEventObserver {
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

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceStatusChanged(String iface, boolean up) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeBoolean(up);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceLinkStateChanged(String iface, boolean up) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeBoolean(up);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceAdded(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceRemoved(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void addressUpdated(String iface, LinkAddress address) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeTypedObject(address, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void addressRemoved(String iface, LinkAddress address) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeTypedObject(address, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void limitReached(String limitName, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(limitName);
                    _data.writeString(iface);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceClassDataActivityChanged(int transportType, boolean active, long tsNanos, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(transportType);
                    _data.writeBoolean(active);
                    _data.writeLong(tsNanos);
                    _data.writeInt(uid);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void interfaceDnsServerInfo(String iface, long lifetime, String[] servers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeLong(lifetime);
                    _data.writeStringArray(servers);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void routeUpdated(RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkManagementEventObserver
            public void routeRemoved(RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
