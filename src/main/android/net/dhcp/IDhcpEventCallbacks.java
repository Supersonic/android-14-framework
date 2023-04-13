package android.net.dhcp;

import android.net.IpPrefix;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IDhcpEventCallbacks extends IInterface {
    public static final String DESCRIPTOR = "android$net$dhcp$IDhcpEventCallbacks".replace('$', '.');
    public static final String HASH = "a7ed197af8532361170ac44595771304b7f04034";
    public static final int VERSION = 16;

    /* loaded from: classes.dex */
    public static class Default implements IDhcpEventCallbacks {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.dhcp.IDhcpEventCallbacks
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.dhcp.IDhcpEventCallbacks
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.dhcp.IDhcpEventCallbacks
        public void onLeasesChanged(List<DhcpLeaseParcelable> list) throws RemoteException {
        }

        @Override // android.net.dhcp.IDhcpEventCallbacks
        public void onNewPrefixRequest(IpPrefix ipPrefix) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onLeasesChanged(List<DhcpLeaseParcelable> list) throws RemoteException;

    void onNewPrefixRequest(IpPrefix ipPrefix) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDhcpEventCallbacks {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onLeasesChanged = 1;
        static final int TRANSACTION_onNewPrefixRequest = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IDhcpEventCallbacks.DESCRIPTOR);
        }

        public static IDhcpEventCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IDhcpEventCallbacks.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDhcpEventCallbacks)) {
                return (IDhcpEventCallbacks) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IDhcpEventCallbacks.DESCRIPTOR;
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
                        onLeasesChanged(parcel.createTypedArrayList(DhcpLeaseParcelable.CREATOR));
                    } else if (i == 2) {
                        onNewPrefixRequest((IpPrefix) parcel.readTypedObject(IpPrefix.CREATOR));
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IDhcpEventCallbacks {
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

            @Override // android.net.dhcp.IDhcpEventCallbacks
            public void onLeasesChanged(List<DhcpLeaseParcelable> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpEventCallbacks.DESCRIPTOR);
                    _Parcel.writeTypedList(obtain, list, 0);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onLeasesChanged is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpEventCallbacks
            public void onNewPrefixRequest(IpPrefix ipPrefix) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IDhcpEventCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(ipPrefix, 0);
                    if (this.mRemote.transact(2, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onNewPrefixRequest is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.dhcp.IDhcpEventCallbacks
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IDhcpEventCallbacks.DESCRIPTOR);
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

            @Override // android.net.dhcp.IDhcpEventCallbacks
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IDhcpEventCallbacks.DESCRIPTOR);
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

    /* loaded from: classes.dex */
    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedList(Parcel parcel, List<T> list, int i) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeTypedObject(list.get(i2), i);
            }
        }
    }
}
