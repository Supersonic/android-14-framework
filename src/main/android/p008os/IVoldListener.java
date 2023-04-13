package android.p008os;
/* renamed from: android.os.IVoldListener */
/* loaded from: classes3.dex */
public interface IVoldListener extends IInterface {
    void onDiskCreated(String str, int i) throws RemoteException;

    void onDiskDestroyed(String str) throws RemoteException;

    void onDiskMetadataChanged(String str, long j, String str2, String str3) throws RemoteException;

    void onDiskScanned(String str) throws RemoteException;

    void onVolumeCreated(String str, int i, String str2, String str3, int i2) throws RemoteException;

    void onVolumeDestroyed(String str) throws RemoteException;

    void onVolumeInternalPathChanged(String str, String str2) throws RemoteException;

    void onVolumeMetadataChanged(String str, String str2, String str3, String str4) throws RemoteException;

    void onVolumePathChanged(String str, String str2) throws RemoteException;

    void onVolumeStateChanged(String str, int i) throws RemoteException;

    /* renamed from: android.os.IVoldListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IVoldListener {
        @Override // android.p008os.IVoldListener
        public void onDiskCreated(String diskId, int flags) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onDiskScanned(String diskId) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onDiskMetadataChanged(String diskId, long sizeBytes, String label, String sysPath) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onDiskDestroyed(String diskId) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumeCreated(String volId, int type, String diskId, String partGuid, int userId) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumeStateChanged(String volId, int state) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumeMetadataChanged(String volId, String fsType, String fsUuid, String fsLabel) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumePathChanged(String volId, String path) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumeInternalPathChanged(String volId, String internalPath) throws RemoteException {
        }

        @Override // android.p008os.IVoldListener
        public void onVolumeDestroyed(String volId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IVoldListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IVoldListener {
        public static final String DESCRIPTOR = "android.os.IVoldListener";
        static final int TRANSACTION_onDiskCreated = 1;
        static final int TRANSACTION_onDiskDestroyed = 4;
        static final int TRANSACTION_onDiskMetadataChanged = 3;
        static final int TRANSACTION_onDiskScanned = 2;
        static final int TRANSACTION_onVolumeCreated = 5;
        static final int TRANSACTION_onVolumeDestroyed = 10;
        static final int TRANSACTION_onVolumeInternalPathChanged = 9;
        static final int TRANSACTION_onVolumeMetadataChanged = 7;
        static final int TRANSACTION_onVolumePathChanged = 8;
        static final int TRANSACTION_onVolumeStateChanged = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoldListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVoldListener)) {
                return (IVoldListener) iin;
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
                    return "onDiskCreated";
                case 2:
                    return "onDiskScanned";
                case 3:
                    return "onDiskMetadataChanged";
                case 4:
                    return "onDiskDestroyed";
                case 5:
                    return "onVolumeCreated";
                case 6:
                    return "onVolumeStateChanged";
                case 7:
                    return "onVolumeMetadataChanged";
                case 8:
                    return "onVolumePathChanged";
                case 9:
                    return "onVolumeInternalPathChanged";
                case 10:
                    return "onVolumeDestroyed";
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
                            data.enforceNoDataAvail();
                            onDiskCreated(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onDiskScanned(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            long _arg12 = data.readLong();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            onDiskMetadataChanged(_arg03, _arg12, _arg2, _arg3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            onDiskDestroyed(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg13 = data.readInt();
                            String _arg22 = data.readString();
                            String _arg32 = data.readString();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            onVolumeCreated(_arg05, _arg13, _arg22, _arg32, _arg4);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            onVolumeStateChanged(_arg06, _arg14);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg15 = data.readString();
                            String _arg23 = data.readString();
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            onVolumeMetadataChanged(_arg07, _arg15, _arg23, _arg33);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            onVolumePathChanged(_arg08, _arg16);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            onVolumeInternalPathChanged(_arg09, _arg17);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            onVolumeDestroyed(_arg010);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IVoldListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IVoldListener {
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

            @Override // android.p008os.IVoldListener
            public void onDiskCreated(String diskId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onDiskScanned(String diskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onDiskMetadataChanged(String diskId, long sizeBytes, String label, String sysPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    _data.writeLong(sizeBytes);
                    _data.writeString(label);
                    _data.writeString(sysPath);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onDiskDestroyed(String diskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumeCreated(String volId, int type, String diskId, String partGuid, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeInt(type);
                    _data.writeString(diskId);
                    _data.writeString(partGuid);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumeStateChanged(String volId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeInt(state);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumeMetadataChanged(String volId, String fsType, String fsUuid, String fsLabel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeString(fsType);
                    _data.writeString(fsUuid);
                    _data.writeString(fsLabel);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumePathChanged(String volId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeString(path);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumeInternalPathChanged(String volId, String internalPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeString(internalPath);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.IVoldListener
            public void onVolumeDestroyed(String volId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
