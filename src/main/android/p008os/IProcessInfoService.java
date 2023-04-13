package android.p008os;
/* renamed from: android.os.IProcessInfoService */
/* loaded from: classes3.dex */
public interface IProcessInfoService extends IInterface {
    void getProcessStatesAndOomScoresFromPids(int[] iArr, int[] iArr2, int[] iArr3) throws RemoteException;

    void getProcessStatesFromPids(int[] iArr, int[] iArr2) throws RemoteException;

    /* renamed from: android.os.IProcessInfoService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IProcessInfoService {
        @Override // android.p008os.IProcessInfoService
        public void getProcessStatesFromPids(int[] pids, int[] states) throws RemoteException {
        }

        @Override // android.p008os.IProcessInfoService
        public void getProcessStatesAndOomScoresFromPids(int[] pids, int[] states, int[] scores) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IProcessInfoService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IProcessInfoService {
        public static final String DESCRIPTOR = "android.os.IProcessInfoService";
        static final int TRANSACTION_getProcessStatesAndOomScoresFromPids = 2;
        static final int TRANSACTION_getProcessStatesFromPids = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IProcessInfoService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IProcessInfoService)) {
                return (IProcessInfoService) iin;
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
                    return "getProcessStatesFromPids";
                case 2:
                    return "getProcessStatesAndOomScoresFromPids";
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
            int[] _arg1;
            int[] _arg12;
            int[] _arg2;
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
                            int[] _arg0 = data.createIntArray();
                            int _arg1_length = data.readInt();
                            if (_arg1_length < 0) {
                                _arg1 = null;
                            } else {
                                _arg1 = new int[_arg1_length];
                            }
                            data.enforceNoDataAvail();
                            getProcessStatesFromPids(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeIntArray(_arg1);
                            break;
                        case 2:
                            int[] _arg02 = data.createIntArray();
                            int _arg1_length2 = data.readInt();
                            if (_arg1_length2 < 0) {
                                _arg12 = null;
                            } else {
                                _arg12 = new int[_arg1_length2];
                            }
                            int _arg2_length = data.readInt();
                            if (_arg2_length < 0) {
                                _arg2 = null;
                            } else {
                                _arg2 = new int[_arg2_length];
                            }
                            data.enforceNoDataAvail();
                            getProcessStatesAndOomScoresFromPids(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeIntArray(_arg12);
                            reply.writeIntArray(_arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IProcessInfoService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IProcessInfoService {
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

            @Override // android.p008os.IProcessInfoService
            public void getProcessStatesFromPids(int[] pids, int[] states) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    _data.writeInt(states.length);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.readIntArray(states);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IProcessInfoService
            public void getProcessStatesAndOomScoresFromPids(int[] pids, int[] states, int[] scores) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(pids);
                    _data.writeInt(states.length);
                    _data.writeInt(scores.length);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    _reply.readIntArray(states);
                    _reply.readIntArray(scores);
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
