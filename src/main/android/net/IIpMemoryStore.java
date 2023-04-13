package android.net;

import android.net.ipmemorystore.Blob;
import android.net.ipmemorystore.IOnBlobRetrievedListener;
import android.net.ipmemorystore.IOnL2KeyResponseListener;
import android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener;
import android.net.ipmemorystore.IOnSameL3NetworkResponseListener;
import android.net.ipmemorystore.IOnStatusAndCountListener;
import android.net.ipmemorystore.IOnStatusListener;
import android.net.ipmemorystore.NetworkAttributesParcelable;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IIpMemoryStore extends IInterface {
    public static final String DESCRIPTOR = "android$net$IIpMemoryStore".replace('$', '.');
    public static final String HASH = "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
    public static final int VERSION = 10;

    /* loaded from: classes.dex */
    public static class Default implements IIpMemoryStore {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.IIpMemoryStore
        public void delete(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void deleteCluster(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void factoryReset() throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void findL2Key(NetworkAttributesParcelable networkAttributesParcelable, IOnL2KeyResponseListener iOnL2KeyResponseListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.IIpMemoryStore
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.IIpMemoryStore
        public void isSameNetwork(String str, String str2, IOnSameL3NetworkResponseListener iOnSameL3NetworkResponseListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void retrieveBlob(String str, String str2, String str3, IOnBlobRetrievedListener iOnBlobRetrievedListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void retrieveNetworkAttributes(String str, IOnNetworkAttributesRetrievedListener iOnNetworkAttributesRetrievedListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void storeBlob(String str, String str2, String str3, Blob blob, IOnStatusListener iOnStatusListener) throws RemoteException {
        }

        @Override // android.net.IIpMemoryStore
        public void storeNetworkAttributes(String str, NetworkAttributesParcelable networkAttributesParcelable, IOnStatusListener iOnStatusListener) throws RemoteException {
        }
    }

    void delete(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException;

    void deleteCluster(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException;

    void factoryReset() throws RemoteException;

    void findL2Key(NetworkAttributesParcelable networkAttributesParcelable, IOnL2KeyResponseListener iOnL2KeyResponseListener) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void isSameNetwork(String str, String str2, IOnSameL3NetworkResponseListener iOnSameL3NetworkResponseListener) throws RemoteException;

    void retrieveBlob(String str, String str2, String str3, IOnBlobRetrievedListener iOnBlobRetrievedListener) throws RemoteException;

    void retrieveNetworkAttributes(String str, IOnNetworkAttributesRetrievedListener iOnNetworkAttributesRetrievedListener) throws RemoteException;

    void storeBlob(String str, String str2, String str3, Blob blob, IOnStatusListener iOnStatusListener) throws RemoteException;

    void storeNetworkAttributes(String str, NetworkAttributesParcelable networkAttributesParcelable, IOnStatusListener iOnStatusListener) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IIpMemoryStore {
        static final int TRANSACTION_delete = 8;
        static final int TRANSACTION_deleteCluster = 9;
        static final int TRANSACTION_factoryReset = 7;
        static final int TRANSACTION_findL2Key = 3;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_isSameNetwork = 4;
        static final int TRANSACTION_retrieveBlob = 6;
        static final int TRANSACTION_retrieveNetworkAttributes = 5;
        static final int TRANSACTION_storeBlob = 2;
        static final int TRANSACTION_storeNetworkAttributes = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IIpMemoryStore.DESCRIPTOR);
        }

        public static IIpMemoryStore asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IIpMemoryStore.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IIpMemoryStore)) {
                return (IIpMemoryStore) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IIpMemoryStore.DESCRIPTOR;
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
                    switch (i) {
                        case 1:
                            storeNetworkAttributes(parcel.readString(), (NetworkAttributesParcelable) parcel.readTypedObject(NetworkAttributesParcelable.CREATOR), IOnStatusListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 2:
                            storeBlob(parcel.readString(), parcel.readString(), parcel.readString(), (Blob) parcel.readTypedObject(Blob.CREATOR), IOnStatusListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 3:
                            findL2Key((NetworkAttributesParcelable) parcel.readTypedObject(NetworkAttributesParcelable.CREATOR), IOnL2KeyResponseListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 4:
                            isSameNetwork(parcel.readString(), parcel.readString(), IOnSameL3NetworkResponseListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 5:
                            retrieveNetworkAttributes(parcel.readString(), IOnNetworkAttributesRetrievedListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 6:
                            retrieveBlob(parcel.readString(), parcel.readString(), parcel.readString(), IOnBlobRetrievedListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 7:
                            factoryReset();
                            break;
                        case 8:
                            delete(parcel.readString(), parcel.readBoolean(), IOnStatusAndCountListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 9:
                            deleteCluster(parcel.readString(), parcel.readBoolean(), IOnStatusAndCountListener.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IIpMemoryStore {
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

            @Override // android.net.IIpMemoryStore
            public void storeNetworkAttributes(String str, NetworkAttributesParcelable networkAttributesParcelable, IOnStatusListener iOnStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedObject(networkAttributesParcelable, 0);
                    obtain.writeStrongInterface(iOnStatusListener);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method storeNetworkAttributes is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void storeBlob(String str, String str2, String str3, Blob blob, IOnStatusListener iOnStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeTypedObject(blob, 0);
                    obtain.writeStrongInterface(iOnStatusListener);
                    if (this.mRemote.transact(2, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method storeBlob is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void findL2Key(NetworkAttributesParcelable networkAttributesParcelable, IOnL2KeyResponseListener iOnL2KeyResponseListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeTypedObject(networkAttributesParcelable, 0);
                    obtain.writeStrongInterface(iOnL2KeyResponseListener);
                    if (this.mRemote.transact(3, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method findL2Key is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void isSameNetwork(String str, String str2, IOnSameL3NetworkResponseListener iOnSameL3NetworkResponseListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStrongInterface(iOnSameL3NetworkResponseListener);
                    if (this.mRemote.transact(4, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method isSameNetwork is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void retrieveNetworkAttributes(String str, IOnNetworkAttributesRetrievedListener iOnNetworkAttributesRetrievedListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongInterface(iOnNetworkAttributesRetrievedListener);
                    if (this.mRemote.transact(5, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method retrieveNetworkAttributes is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void retrieveBlob(String str, String str2, String str3, IOnBlobRetrievedListener iOnBlobRetrievedListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeStrongInterface(iOnBlobRetrievedListener);
                    if (this.mRemote.transact(6, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method retrieveBlob is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void factoryReset() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    if (this.mRemote.transact(7, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method factoryReset is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void delete(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    obtain.writeStrongInterface(iOnStatusAndCountListener);
                    if (this.mRemote.transact(8, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method delete is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public void deleteCluster(String str, boolean z, IOnStatusAndCountListener iOnStatusAndCountListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    obtain.writeStrongInterface(iOnStatusAndCountListener);
                    if (this.mRemote.transact(9, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method deleteCluster is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.IIpMemoryStore
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
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

            @Override // android.net.IIpMemoryStore
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IIpMemoryStore.DESCRIPTOR);
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
