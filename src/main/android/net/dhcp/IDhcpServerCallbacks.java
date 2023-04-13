package android.net.dhcp;

import android.net.dhcp.IDhcpServer;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IDhcpServerCallbacks extends IInterface {
    public static final String DESCRIPTOR = "android$net$dhcp$IDhcpServerCallbacks".replace('$', '.');
    public static final String HASH = "a7ed197af8532361170ac44595771304b7f04034";
    public static final int VERSION = 16;

    /* loaded from: classes.dex */
    public static class Default implements IDhcpServerCallbacks {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.dhcp.IDhcpServerCallbacks
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.dhcp.IDhcpServerCallbacks
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.dhcp.IDhcpServerCallbacks
        public void onDhcpServerCreated(int i, IDhcpServer iDhcpServer) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onDhcpServerCreated(int i, IDhcpServer iDhcpServer) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDhcpServerCallbacks {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onDhcpServerCreated = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IDhcpServerCallbacks.DESCRIPTOR);
        }

        public static IDhcpServerCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IDhcpServerCallbacks.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDhcpServerCallbacks)) {
                return (IDhcpServerCallbacks) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IDhcpServerCallbacks.DESCRIPTOR;
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
                        onDhcpServerCreated(parcel.readInt(), IDhcpServer.Stub.asInterface(parcel.readStrongBinder()));
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IDhcpServerCallbacks {
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

            @Override // android.net.dhcp.IDhcpServerCallbacks
            public void onDhcpServerCreated(int i, IDhcpServer iDhcpServer) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpServerCallbacks.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongInterface(iDhcpServer);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onDhcpServerCreated is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpServerCallbacks
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IDhcpServerCallbacks.DESCRIPTOR);
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

            @Override // android.net.dhcp.IDhcpServerCallbacks
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IDhcpServerCallbacks.DESCRIPTOR);
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
