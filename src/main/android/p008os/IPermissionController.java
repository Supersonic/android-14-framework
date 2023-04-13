package android.p008os;
/* renamed from: android.os.IPermissionController */
/* loaded from: classes3.dex */
public interface IPermissionController extends IInterface {
    boolean checkPermission(String str, int i, int i2) throws RemoteException;

    int getPackageUid(String str, int i) throws RemoteException;

    String[] getPackagesForUid(int i) throws RemoteException;

    boolean isRuntimePermission(String str) throws RemoteException;

    int noteOp(String str, int i, String str2) throws RemoteException;

    /* renamed from: android.os.IPermissionController$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IPermissionController {
        @Override // android.p008os.IPermissionController
        public boolean checkPermission(String permission, int pid, int uid) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPermissionController
        public int noteOp(String op, int uid, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IPermissionController
        public String[] getPackagesForUid(int uid) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IPermissionController
        public boolean isRuntimePermission(String permission) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IPermissionController
        public int getPackageUid(String packageName, int flags) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IPermissionController$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPermissionController {
        public static final String DESCRIPTOR = "android.os.IPermissionController";
        static final int TRANSACTION_checkPermission = 1;
        static final int TRANSACTION_getPackageUid = 5;
        static final int TRANSACTION_getPackagesForUid = 3;
        static final int TRANSACTION_isRuntimePermission = 4;
        static final int TRANSACTION_noteOp = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPermissionController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPermissionController)) {
                return (IPermissionController) iin;
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
                    return "checkPermission";
                case 2:
                    return "noteOp";
                case 3:
                    return "getPackagesForUid";
                case 4:
                    return "isRuntimePermission";
                case 5:
                    return "getPackageUid";
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
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = checkPermission(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            int _result2 = noteOp(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result3 = getPackagesForUid(_arg03);
                            reply.writeNoException();
                            reply.writeStringArray(_result3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = isRuntimePermission(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = getPackageUid(_arg05, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IPermissionController$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IPermissionController {
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

            @Override // android.p008os.IPermissionController
            public boolean checkPermission(String permission, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPermissionController
            public int noteOp(String op, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(op);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPermissionController
            public String[] getPackagesForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPermissionController
            public boolean isRuntimePermission(String permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IPermissionController
            public int getPackageUid(String packageName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    this.mRemote.transact(5, _data, _reply, 0);
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
            return 4;
        }
    }
}
