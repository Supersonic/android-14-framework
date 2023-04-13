package android.p008os;
/* renamed from: android.os.IWakeLockCallback */
/* loaded from: classes3.dex */
public interface IWakeLockCallback extends IInterface {
    public static final String DESCRIPTOR = "android.os.IWakeLockCallback";

    void onStateChanged(boolean z) throws RemoteException;

    /* renamed from: android.os.IWakeLockCallback$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IWakeLockCallback {
        @Override // android.p008os.IWakeLockCallback
        public void onStateChanged(boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IWakeLockCallback$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWakeLockCallback {
        static final int TRANSACTION_onStateChanged = 1;

        public Stub() {
            attachInterface(this, IWakeLockCallback.DESCRIPTOR);
        }

        public static IWakeLockCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWakeLockCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IWakeLockCallback)) {
                return (IWakeLockCallback) iin;
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
                    return "onStateChanged";
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
                data.enforceInterface(IWakeLockCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWakeLockCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onStateChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IWakeLockCallback$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IWakeLockCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWakeLockCallback.DESCRIPTOR;
            }

            @Override // android.p008os.IWakeLockCallback
            public void onStateChanged(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWakeLockCallback.DESCRIPTOR);
                    _data.writeBoolean(enabled);
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
