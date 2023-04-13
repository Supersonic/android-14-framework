package android.p008os;
/* renamed from: android.os.IIncidentAuthListener */
/* loaded from: classes3.dex */
public interface IIncidentAuthListener extends IInterface {
    public static final String DESCRIPTOR = "android.os.IIncidentAuthListener";

    void onReportApproved() throws RemoteException;

    void onReportDenied() throws RemoteException;

    /* renamed from: android.os.IIncidentAuthListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IIncidentAuthListener {
        @Override // android.p008os.IIncidentAuthListener
        public void onReportApproved() throws RemoteException {
        }

        @Override // android.p008os.IIncidentAuthListener
        public void onReportDenied() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IIncidentAuthListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IIncidentAuthListener {
        static final int TRANSACTION_onReportApproved = 1;
        static final int TRANSACTION_onReportDenied = 2;

        public Stub() {
            attachInterface(this, IIncidentAuthListener.DESCRIPTOR);
        }

        public static IIncidentAuthListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IIncidentAuthListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IIncidentAuthListener)) {
                return (IIncidentAuthListener) iin;
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
                    return "onReportApproved";
                case 2:
                    return "onReportDenied";
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
                data.enforceInterface(IIncidentAuthListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IIncidentAuthListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onReportApproved();
                            break;
                        case 2:
                            onReportDenied();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IIncidentAuthListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IIncidentAuthListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IIncidentAuthListener.DESCRIPTOR;
            }

            @Override // android.p008os.IIncidentAuthListener
            public void onReportApproved() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentAuthListener.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IIncidentAuthListener
            public void onReportDenied() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IIncidentAuthListener.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
