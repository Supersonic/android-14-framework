package android.p005os;
/* renamed from: android.os.IDumpstateListener */
/* loaded from: classes.dex */
public interface IDumpstateListener extends IInterface {
    public static final int BUGREPORT_ERROR_ANOTHER_REPORT_IN_PROGRESS = 5;
    public static final int BUGREPORT_ERROR_INVALID_INPUT = 1;
    public static final int BUGREPORT_ERROR_NO_BUGREPORT_TO_RETRIEVE = 6;
    public static final int BUGREPORT_ERROR_RUNTIME_ERROR = 2;
    public static final int BUGREPORT_ERROR_USER_CONSENT_TIMED_OUT = 4;
    public static final int BUGREPORT_ERROR_USER_DENIED_CONSENT = 3;
    public static final String DESCRIPTOR = "android.os.IDumpstateListener";

    /* renamed from: android.os.IDumpstateListener$Default */
    /* loaded from: classes.dex */
    public static class Default implements IDumpstateListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IDumpstateListener
        public void onError(int i) throws RemoteException {
        }

        @Override // android.p005os.IDumpstateListener
        public void onFinished(String str) throws RemoteException {
        }

        @Override // android.p005os.IDumpstateListener
        public void onProgress(int i) throws RemoteException {
        }

        @Override // android.p005os.IDumpstateListener
        public void onScreenshotTaken(boolean z) throws RemoteException {
        }

        @Override // android.p005os.IDumpstateListener
        public void onUiIntensiveBugreportDumpsFinished() throws RemoteException {
        }
    }

    void onError(int i) throws RemoteException;

    void onFinished(String str) throws RemoteException;

    void onProgress(int i) throws RemoteException;

    void onScreenshotTaken(boolean z) throws RemoteException;

    void onUiIntensiveBugreportDumpsFinished() throws RemoteException;

    /* renamed from: android.os.IDumpstateListener$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDumpstateListener {
        public static final int TRANSACTION_onError = 2;
        public static final int TRANSACTION_onFinished = 3;
        public static final int TRANSACTION_onProgress = 1;
        public static final int TRANSACTION_onScreenshotTaken = 4;
        public static final int TRANSACTION_onUiIntensiveBugreportDumpsFinished = 5;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IDumpstateListener.DESCRIPTOR);
        }

        public static IDumpstateListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IDumpstateListener.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IDumpstateListener)) {
                return (IDumpstateListener) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IDumpstateListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IDumpstateListener.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                int readInt = parcel.readInt();
                parcel.enforceNoDataAvail();
                onProgress(readInt);
            } else if (i == 2) {
                int readInt2 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onError(readInt2);
            } else if (i == 3) {
                String readString = parcel.readString();
                parcel.enforceNoDataAvail();
                onFinished(readString);
            } else if (i == 4) {
                boolean readBoolean = parcel.readBoolean();
                parcel.enforceNoDataAvail();
                onScreenshotTaken(readBoolean);
            } else if (i == 5) {
                onUiIntensiveBugreportDumpsFinished();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* renamed from: android.os.IDumpstateListener$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IDumpstateListener {
            public IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IDumpstateListener.DESCRIPTOR;
            }

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.p005os.IDumpstateListener
            public void onProgress(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(IDumpstateListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstateListener
            public void onError(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(IDumpstateListener.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstateListener
            public void onFinished(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(IDumpstateListener.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstateListener
            public void onScreenshotTaken(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(IDumpstateListener.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(4, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IDumpstateListener
            public void onUiIntensiveBugreportDumpsFinished() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                try {
                    obtain.writeInterfaceToken(IDumpstateListener.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
