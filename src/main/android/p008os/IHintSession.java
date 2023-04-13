package android.p008os;
/* renamed from: android.os.IHintSession */
/* loaded from: classes3.dex */
public interface IHintSession extends IInterface {
    public static final String DESCRIPTOR = "android.os.IHintSession";

    void close() throws RemoteException;

    void reportActualWorkDuration(long[] jArr, long[] jArr2) throws RemoteException;

    void sendHint(int i) throws RemoteException;

    void updateTargetWorkDuration(long j) throws RemoteException;

    /* renamed from: android.os.IHintSession$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IHintSession {
        @Override // android.p008os.IHintSession
        public void updateTargetWorkDuration(long targetDurationNanos) throws RemoteException {
        }

        @Override // android.p008os.IHintSession
        public void reportActualWorkDuration(long[] actualDurationNanos, long[] timeStampNanos) throws RemoteException {
        }

        @Override // android.p008os.IHintSession
        public void close() throws RemoteException {
        }

        @Override // android.p008os.IHintSession
        public void sendHint(int hint) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IHintSession$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IHintSession {
        static final int TRANSACTION_close = 3;
        static final int TRANSACTION_reportActualWorkDuration = 2;
        static final int TRANSACTION_sendHint = 4;
        static final int TRANSACTION_updateTargetWorkDuration = 1;

        public Stub() {
            attachInterface(this, IHintSession.DESCRIPTOR);
        }

        public static IHintSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IHintSession.DESCRIPTOR);
            if (iin != null && (iin instanceof IHintSession)) {
                return (IHintSession) iin;
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
                    return "updateTargetWorkDuration";
                case 2:
                    return "reportActualWorkDuration";
                case 3:
                    return "close";
                case 4:
                    return "sendHint";
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
                data.enforceInterface(IHintSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IHintSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            data.enforceNoDataAvail();
                            updateTargetWorkDuration(_arg0);
                            break;
                        case 2:
                            long[] _arg02 = data.createLongArray();
                            long[] _arg1 = data.createLongArray();
                            data.enforceNoDataAvail();
                            reportActualWorkDuration(_arg02, _arg1);
                            break;
                        case 3:
                            close();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            sendHint(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IHintSession$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IHintSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IHintSession.DESCRIPTOR;
            }

            @Override // android.p008os.IHintSession
            public void updateTargetWorkDuration(long targetDurationNanos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHintSession.DESCRIPTOR);
                    _data.writeLong(targetDurationNanos);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IHintSession
            public void reportActualWorkDuration(long[] actualDurationNanos, long[] timeStampNanos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHintSession.DESCRIPTOR);
                    _data.writeLongArray(actualDurationNanos);
                    _data.writeLongArray(timeStampNanos);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IHintSession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHintSession.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IHintSession
            public void sendHint(int hint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHintSession.DESCRIPTOR);
                    _data.writeInt(hint);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
