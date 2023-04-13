package android.net.dhcp;

import android.net.INetworkStackStatusCallback;
import android.net.dhcp.IDhcpEventCallbacks;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IDhcpServer extends IInterface {
    public static final String DESCRIPTOR = "android$net$dhcp$IDhcpServer".replace('$', '.');
    public static final String HASH = "a7ed197af8532361170ac44595771304b7f04034";
    public static final int STATUS_INVALID_ARGUMENT = 2;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_UNKNOWN_ERROR = 3;
    public static final int VERSION = 16;

    /* loaded from: classes.dex */
    public static class Default implements IDhcpServer {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.dhcp.IDhcpServer
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.dhcp.IDhcpServer
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.dhcp.IDhcpServer
        public void start(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
        }

        @Override // android.net.dhcp.IDhcpServer
        public void startWithCallbacks(INetworkStackStatusCallback iNetworkStackStatusCallback, IDhcpEventCallbacks iDhcpEventCallbacks) throws RemoteException {
        }

        @Override // android.net.dhcp.IDhcpServer
        public void stop(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
        }

        @Override // android.net.dhcp.IDhcpServer
        public void updateParams(DhcpServingParamsParcel dhcpServingParamsParcel, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void start(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException;

    void startWithCallbacks(INetworkStackStatusCallback iNetworkStackStatusCallback, IDhcpEventCallbacks iDhcpEventCallbacks) throws RemoteException;

    void stop(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException;

    void updateParams(DhcpServingParamsParcel dhcpServingParamsParcel, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDhcpServer {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_startWithCallbacks = 4;
        static final int TRANSACTION_stop = 3;
        static final int TRANSACTION_updateParams = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IDhcpServer.DESCRIPTOR);
        }

        public static IDhcpServer asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IDhcpServer.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDhcpServer)) {
                return (IDhcpServer) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IDhcpServer.DESCRIPTOR;
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
                        start(INetworkStackStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 2) {
                        updateParams((DhcpServingParamsParcel) parcel.readTypedObject(DhcpServingParamsParcel.CREATOR), INetworkStackStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 3) {
                        stop(INetworkStackStatusCallback.Stub.asInterface(parcel.readStrongBinder()));
                    } else if (i == 4) {
                        startWithCallbacks(INetworkStackStatusCallback.Stub.asInterface(parcel.readStrongBinder()), IDhcpEventCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IDhcpServer {
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

            @Override // android.net.dhcp.IDhcpServer
            public void start(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
                    obtain.writeStrongInterface(iNetworkStackStatusCallback);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method start is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpServer
            public void startWithCallbacks(INetworkStackStatusCallback iNetworkStackStatusCallback, IDhcpEventCallbacks iDhcpEventCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
                    obtain.writeStrongInterface(iNetworkStackStatusCallback);
                    obtain.writeStrongInterface(iDhcpEventCallbacks);
                    if (this.mRemote.transact(4, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method startWithCallbacks is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpServer
            public void updateParams(DhcpServingParamsParcel dhcpServingParamsParcel, INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
                    obtain.writeTypedObject(dhcpServingParamsParcel, 0);
                    obtain.writeStrongInterface(iNetworkStackStatusCallback);
                    if (this.mRemote.transact(2, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method updateParams is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpServer
            public void stop(INetworkStackStatusCallback iNetworkStackStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
                    obtain.writeStrongInterface(iNetworkStackStatusCallback);
                    if (this.mRemote.transact(3, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method stop is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpServer
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
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

            @Override // android.net.dhcp.IDhcpServer
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IDhcpServer.DESCRIPTOR);
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
