package android.p008os;
/* renamed from: android.os.IIncidentDumpCallback */
/* loaded from: classes3.dex */
public interface IIncidentDumpCallback extends IInterface {
    public static final String DESCRIPTOR = "android.os.IIncidentDumpCallback";

    void onDumpSection(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    /* renamed from: android.os.IIncidentDumpCallback$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IIncidentDumpCallback {
        @Override // android.p008os.IIncidentDumpCallback
        public void onDumpSection(ParcelFileDescriptor fd) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IIncidentDumpCallback$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IIncidentDumpCallback {
        static final int TRANSACTION_onDumpSection = 1;

        public Stub() {
            attachInterface(this, IIncidentDumpCallback.DESCRIPTOR);
        }

        public static IIncidentDumpCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IIncidentDumpCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IIncidentDumpCallback)) {
                return (IIncidentDumpCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onDumpSection";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IIncidentDumpCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IIncidentDumpCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _arg0 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            onDumpSection(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.os.IIncidentDumpCallback$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IIncidentDumpCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IIncidentDumpCallback.DESCRIPTOR;
            }

            @Override // android.p008os.IIncidentDumpCallback
            public void onDumpSection(ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentDumpCallback.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
