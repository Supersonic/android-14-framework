package android.p005os;
/* renamed from: android.os.IVoldTaskListener */
/* loaded from: classes.dex */
public interface IVoldTaskListener extends IInterface {

    /* renamed from: android.os.IVoldTaskListener$Default */
    /* loaded from: classes.dex */
    public static class Default implements IVoldTaskListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IVoldTaskListener
        public void onFinished(int i, PersistableBundle persistableBundle) throws RemoteException {
        }

        @Override // android.p005os.IVoldTaskListener
        public void onStatus(int i, PersistableBundle persistableBundle) throws RemoteException {
        }
    }

    void onFinished(int i, PersistableBundle persistableBundle) throws RemoteException;

    void onStatus(int i, PersistableBundle persistableBundle) throws RemoteException;

    /* renamed from: android.os.IVoldTaskListener$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVoldTaskListener {
        public static final String DESCRIPTOR = "android.os.IVoldTaskListener";
        public static final int TRANSACTION_onFinished = 2;
        public static final int TRANSACTION_onStatus = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoldTaskListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IVoldTaskListener)) {
                return (IVoldTaskListener) queryLocalInterface;
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
                parcel.enforceNoDataAvail();
                onStatus(parcel.readInt(), (PersistableBundle) parcel.readTypedObject(PersistableBundle.CREATOR));
            } else if (i == 2) {
                parcel.enforceNoDataAvail();
                onFinished(parcel.readInt(), (PersistableBundle) parcel.readTypedObject(PersistableBundle.CREATOR));
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* renamed from: android.os.IVoldTaskListener$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IVoldTaskListener {
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

            @Override // android.p005os.IVoldTaskListener
            public void onStatus(int i, PersistableBundle persistableBundle) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(persistableBundle, 0);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVoldTaskListener
            public void onFinished(int i, PersistableBundle persistableBundle) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(persistableBundle, 0);
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
