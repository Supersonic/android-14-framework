package android.net.ipmemorystore;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IOnNetworkAttributesRetrievedListener extends IInterface {
    public static final String DESCRIPTOR = "android$net$ipmemorystore$IOnNetworkAttributesRetrievedListener".replace('$', '.');
    public static final String HASH = "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
    public static final int VERSION = 10;

    /* loaded from: classes.dex */
    public static class Default implements IOnNetworkAttributesRetrievedListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
        public void onNetworkAttributesRetrieved(StatusParcelable statusParcelable, String str, NetworkAttributesParcelable networkAttributesParcelable) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onNetworkAttributesRetrieved(StatusParcelable statusParcelable, String str, NetworkAttributesParcelable networkAttributesParcelable) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOnNetworkAttributesRetrievedListener {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onNetworkAttributesRetrieved = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IOnNetworkAttributesRetrievedListener.DESCRIPTOR);
        }

        public static IOnNetworkAttributesRetrievedListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IOnNetworkAttributesRetrievedListener.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IOnNetworkAttributesRetrievedListener)) {
                return (IOnNetworkAttributesRetrievedListener) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IOnNetworkAttributesRetrievedListener.DESCRIPTOR;
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
                        onNetworkAttributesRetrieved((StatusParcelable) parcel.readTypedObject(StatusParcelable.CREATOR), parcel.readString(), (NetworkAttributesParcelable) parcel.readTypedObject(NetworkAttributesParcelable.CREATOR));
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IOnNetworkAttributesRetrievedListener {
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

            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public void onNetworkAttributesRetrieved(StatusParcelable statusParcelable, String str, NetworkAttributesParcelable networkAttributesParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOnNetworkAttributesRetrievedListener.DESCRIPTOR);
                    obtain.writeTypedObject(statusParcelable, 0);
                    obtain.writeString(str);
                    obtain.writeTypedObject(networkAttributesParcelable, 0);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onNetworkAttributesRetrieved is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IOnNetworkAttributesRetrievedListener.DESCRIPTOR);
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

            @Override // android.net.ipmemorystore.IOnNetworkAttributesRetrievedListener
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IOnNetworkAttributesRetrievedListener.DESCRIPTOR);
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
