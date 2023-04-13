package android.net;

import android.net.IIpMemoryStoreCallbacks;
import android.net.INetworkMonitorCallbacks;
import android.net.INetworkStackStatusCallback;
import android.net.dhcp.DhcpServingParamsParcel;
import android.net.dhcp.IDhcpServerCallbacks;
import android.net.p003ip.IIpClientCallbacks;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface INetworkStackConnector extends IInterface {
    public static final String DESCRIPTOR = "android$net$INetworkStackConnector".replace('$', '.');
    public static final String HASH = "a7ed197af8532361170ac44595771304b7f04034";
    public static final int VERSION = 16;

    /* loaded from: classes.dex */
    public static class Default implements INetworkStackConnector {
        @Override // android.net.INetworkStackConnector
        public void allowTestUid(int i, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.INetworkStackConnector
        public void fetchIpMemoryStore(IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks) throws RemoteException {
        }

        @Override // android.net.INetworkStackConnector
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.INetworkStackConnector
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.INetworkStackConnector
        public void makeDhcpServer(String str, DhcpServingParamsParcel dhcpServingParamsParcel, IDhcpServerCallbacks iDhcpServerCallbacks) throws RemoteException {
        }

        @Override // android.net.INetworkStackConnector
        public void makeIpClient(String str, IIpClientCallbacks iIpClientCallbacks) throws RemoteException {
        }

        @Override // android.net.INetworkStackConnector
        public void makeNetworkMonitor(Network network, String str, INetworkMonitorCallbacks iNetworkMonitorCallbacks) throws RemoteException {
        }
    }

    void allowTestUid(int i, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException;

    void fetchIpMemoryStore(IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void makeDhcpServer(String str, DhcpServingParamsParcel dhcpServingParamsParcel, IDhcpServerCallbacks iDhcpServerCallbacks) throws RemoteException;

    void makeIpClient(String str, IIpClientCallbacks iIpClientCallbacks) throws RemoteException;

    void makeNetworkMonitor(Network network, String str, INetworkMonitorCallbacks iNetworkMonitorCallbacks) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements INetworkStackConnector {
        static final int TRANSACTION_allowTestUid = 5;
        static final int TRANSACTION_fetchIpMemoryStore = 4;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_makeDhcpServer = 1;
        static final int TRANSACTION_makeIpClient = 3;
        static final int TRANSACTION_makeNetworkMonitor = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, INetworkStackConnector.DESCRIPTOR);
        }

        public static INetworkStackConnector asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(INetworkStackConnector.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof INetworkStackConnector)) {
                return (INetworkStackConnector) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = INetworkStackConnector.DESCRIPTOR;
            if (i >= 1 && i <= TRANSACTION_getInterfaceVersion) {
                parcel.enforceInterface(str);
            }
            switch (i) {
                case TRANSACTION_getInterfaceHash /* 16777214 */:
                    parcel2.writeNoException();
                    parcel2.writeString(getInterfaceHash());
                    return true;
                case TRANSACTION_getInterfaceVersion /* 16777215 */:
                    parcel2.writeNoException();
                    parcel2.writeInt(getInterfaceVersion());
                    return true;
                case 1598968902:
                    parcel2.writeString(str);
                    return true;
                default:
                    if (i == 1) {
                        makeDhcpServer(parcel.readString(), (DhcpServingParamsParcel) parcel.readTypedObject(DhcpServingParamsParcel.CREATOR), IDhcpServerCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 2) {
                        makeNetworkMonitor((Network) parcel.readTypedObject(Network.CREATOR), parcel.readString(), INetworkMonitorCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 3) {
                        makeIpClient(parcel.readString(), IIpClientCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 4) {
                        fetchIpMemoryStore(IIpMemoryStoreCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 5) {
                        allowTestUid(parcel.readInt(), INetworkStackStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements INetworkStackConnector {
            public IBinder mRemote;
            public int mCachedVersion = -1;
            public String mCachedHash = "-1";

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.net.INetworkStackConnector
            public void makeDhcpServer(String str, DhcpServingParamsParcel dhcpServingParamsParcel, IDhcpServerCallbacks iDhcpServerCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedObject(dhcpServingParamsParcel, 0);
                    obtain.writeStrongInterface(iDhcpServerCallbacks);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method makeDhcpServer is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.INetworkStackConnector
            public void makeNetworkMonitor(Network network, String str, INetworkMonitorCallbacks iNetworkMonitorCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    obtain.writeTypedObject(network, 0);
                    obtain.writeString(str);
                    obtain.writeStrongInterface(iNetworkMonitorCallbacks);
                    if (this.mRemote.transact(2, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method makeNetworkMonitor is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.INetworkStackConnector
            public void makeIpClient(String str, IIpClientCallbacks iIpClientCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongInterface(iIpClientCallbacks);
                    if (this.mRemote.transact(3, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method makeIpClient is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.INetworkStackConnector
            public void fetchIpMemoryStore(IIpMemoryStoreCallbacks iIpMemoryStoreCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    obtain.writeStrongInterface(iIpMemoryStoreCallbacks);
                    if (this.mRemote.transact(4, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method fetchIpMemoryStore is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.INetworkStackConnector
            public void allowTestUid(int i, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongInterface(iNetworkStackStatusCallback);
                    if (this.mRemote.transact(5, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method allowTestUid is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.INetworkStackConnector
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                        this.mRemote.transact(Stub.TRANSACTION_getInterfaceVersion, obtain, obtain2, 0);
                        obtain2.readException();
                        this.mCachedVersion = obtain2.readInt();
                    } finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.net.INetworkStackConnector
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(INetworkStackConnector.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInterfaceHash, obtain, obtain2, 0);
                    obtain2.readException();
                    this.mCachedHash = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
