package android.p005os;
/* renamed from: android.os.IStoraged */
/* loaded from: classes.dex */
public interface IStoraged extends IInterface {

    /* renamed from: android.os.IStoraged$Default */
    /* loaded from: classes.dex */
    public static class Default implements IStoraged {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IStoraged
        public int getRecentPerf() throws RemoteException {
            return 0;
        }

        @Override // android.p005os.IStoraged
        public void onUserStarted(int i) throws RemoteException {
        }

        @Override // android.p005os.IStoraged
        public void onUserStopped(int i) throws RemoteException {
        }
    }

    int getRecentPerf() throws RemoteException;

    void onUserStarted(int i) throws RemoteException;

    void onUserStopped(int i) throws RemoteException;

    /* renamed from: android.os.IStoraged$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IStoraged {
        public static final String DESCRIPTOR = "android.os.IStoraged";
        public static final int TRANSACTION_getRecentPerf = 3;
        public static final int TRANSACTION_onUserStarted = 1;
        public static final int TRANSACTION_onUserStopped = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStoraged asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IStoraged)) {
                return (IStoraged) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                int readInt = parcel.readInt();
                parcel.enforceNoDataAvail();
                onUserStarted(readInt);
                parcel2.writeNoException();
            } else if (i == 2) {
                int readInt2 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onUserStopped(readInt2);
                parcel2.writeNoException();
            } else if (i == 3) {
                int recentPerf = getRecentPerf();
                parcel2.writeNoException();
                parcel2.writeInt(recentPerf);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* renamed from: android.os.IStoraged$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IStoraged {
            public IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.p005os.IStoraged
            public void onUserStarted(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IStoraged
            public void onUserStopped(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IStoraged
            public int getRecentPerf() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
