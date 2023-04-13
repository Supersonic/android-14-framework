package android.net.ipmemorystore;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IOnSameL3NetworkResponseListener extends IInterface {
    public static final String DESCRIPTOR = "android$net$ipmemorystore$IOnSameL3NetworkResponseListener".replace('$', '.');
    public static final String HASH = "d5ea5eb3ddbdaa9a986ce6ba70b0804ca3e39b0c";
    public static final int VERSION = 10;

    /* loaded from: classes.dex */
    public static class Default implements IOnSameL3NetworkResponseListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
        public void onSameL3NetworkResponse(StatusParcelable statusParcelable, SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onSameL3NetworkResponse(StatusParcelable statusParcelable, SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOnSameL3NetworkResponseListener {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onSameL3NetworkResponse = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IOnSameL3NetworkResponseListener.DESCRIPTOR);
        }

        public static IOnSameL3NetworkResponseListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IOnSameL3NetworkResponseListener.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IOnSameL3NetworkResponseListener)) {
                return (IOnSameL3NetworkResponseListener) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IOnSameL3NetworkResponseListener.DESCRIPTOR;
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
                        onSameL3NetworkResponse((StatusParcelable) parcel.readTypedObject(StatusParcelable.CREATOR), (SameL3NetworkResponseParcelable) parcel.readTypedObject(SameL3NetworkResponseParcelable.CREATOR));
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IOnSameL3NetworkResponseListener {
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

            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public void onSameL3NetworkResponse(StatusParcelable statusParcelable, SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOnSameL3NetworkResponseListener.DESCRIPTOR);
                    obtain.writeTypedObject(statusParcelable, 0);
                    obtain.writeTypedObject(sameL3NetworkResponseParcelable, 0);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onSameL3NetworkResponse is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IOnSameL3NetworkResponseListener.DESCRIPTOR);
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

            @Override // android.net.ipmemorystore.IOnSameL3NetworkResponseListener
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IOnSameL3NetworkResponseListener.DESCRIPTOR);
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
