package android.p008os;
/* renamed from: android.os.ISchedulingPolicyService */
/* loaded from: classes3.dex */
public interface ISchedulingPolicyService extends IInterface {
    int requestCpusetBoost(boolean z, IBinder iBinder) throws RemoteException;

    int requestPriority(int i, int i2, int i3, boolean z) throws RemoteException;

    /* renamed from: android.os.ISchedulingPolicyService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISchedulingPolicyService {
        @Override // android.p008os.ISchedulingPolicyService
        public int requestPriority(int pid, int tid, int prio, boolean isForApp) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.ISchedulingPolicyService
        public int requestCpusetBoost(boolean enable, IBinder client) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.ISchedulingPolicyService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISchedulingPolicyService {
        public static final String DESCRIPTOR = "android.os.ISchedulingPolicyService";
        static final int TRANSACTION_requestCpusetBoost = 2;
        static final int TRANSACTION_requestPriority = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISchedulingPolicyService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISchedulingPolicyService)) {
                return (ISchedulingPolicyService) iin;
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
                    return "requestPriority";
                case 2:
                    return "requestCpusetBoost";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result = requestPriority(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            IBinder _arg12 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result2 = requestCpusetBoost(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.ISchedulingPolicyService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ISchedulingPolicyService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.p008os.ISchedulingPolicyService
            public int requestPriority(int pid, int tid, int prio, boolean isForApp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pid);
                    _data.writeInt(tid);
                    _data.writeInt(prio);
                    _data.writeBoolean(isForApp);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISchedulingPolicyService
            public int requestCpusetBoost(boolean enable, IBinder client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    _data.writeStrongBinder(client);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
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
