package android.p008os.image;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.gsi.AvbPublicKey;
import android.gsi.GsiProgress;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
/* renamed from: android.os.image.IDynamicSystemService */
/* loaded from: classes3.dex */
public interface IDynamicSystemService extends IInterface {
    public static final String DESCRIPTOR = "android.os.image.IDynamicSystemService";

    boolean abort() throws RemoteException;

    boolean closePartition() throws RemoteException;

    int createPartition(String str, long j, boolean z) throws RemoteException;

    boolean finishInstallation() throws RemoteException;

    boolean getAvbPublicKey(AvbPublicKey avbPublicKey) throws RemoteException;

    GsiProgress getInstallationProgress() throws RemoteException;

    boolean isEnabled() throws RemoteException;

    boolean isInUse() throws RemoteException;

    boolean isInstalled() throws RemoteException;

    boolean remove() throws RemoteException;

    boolean setAshmem(ParcelFileDescriptor parcelFileDescriptor, long j) throws RemoteException;

    boolean setEnable(boolean z, boolean z2) throws RemoteException;

    boolean startInstallation(String str) throws RemoteException;

    boolean submitFromAshmem(long j) throws RemoteException;

    long suggestScratchSize() throws RemoteException;

    /* renamed from: android.os.image.IDynamicSystemService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IDynamicSystemService {
        @Override // android.p008os.image.IDynamicSystemService
        public boolean startInstallation(String dsuSlot) throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public int createPartition(String name, long size, boolean readOnly) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean closePartition() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean finishInstallation() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public GsiProgress getInstallationProgress() throws RemoteException {
            return null;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean abort() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean isInUse() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean isInstalled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean isEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean remove() throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean setEnable(boolean enable, boolean oneShot) throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean setAshmem(ParcelFileDescriptor fd, long size) throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean submitFromAshmem(long bytes) throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public boolean getAvbPublicKey(AvbPublicKey dst) throws RemoteException {
            return false;
        }

        @Override // android.p008os.image.IDynamicSystemService
        public long suggestScratchSize() throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.image.IDynamicSystemService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDynamicSystemService {
        static final int TRANSACTION_abort = 6;
        static final int TRANSACTION_closePartition = 3;
        static final int TRANSACTION_createPartition = 2;
        static final int TRANSACTION_finishInstallation = 4;
        static final int TRANSACTION_getAvbPublicKey = 14;
        static final int TRANSACTION_getInstallationProgress = 5;
        static final int TRANSACTION_isEnabled = 9;
        static final int TRANSACTION_isInUse = 7;
        static final int TRANSACTION_isInstalled = 8;
        static final int TRANSACTION_remove = 10;
        static final int TRANSACTION_setAshmem = 12;
        static final int TRANSACTION_setEnable = 11;
        static final int TRANSACTION_startInstallation = 1;
        static final int TRANSACTION_submitFromAshmem = 13;
        static final int TRANSACTION_suggestScratchSize = 15;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, IDynamicSystemService.DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IDynamicSystemService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDynamicSystemService.DESCRIPTOR);
            if (iin != null && (iin instanceof IDynamicSystemService)) {
                return (IDynamicSystemService) iin;
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
                    return "startInstallation";
                case 2:
                    return "createPartition";
                case 3:
                    return "closePartition";
                case 4:
                    return "finishInstallation";
                case 5:
                    return "getInstallationProgress";
                case 6:
                    return "abort";
                case 7:
                    return "isInUse";
                case 8:
                    return "isInstalled";
                case 9:
                    return "isEnabled";
                case 10:
                    return "remove";
                case 11:
                    return "setEnable";
                case 12:
                    return "setAshmem";
                case 13:
                    return "submitFromAshmem";
                case 14:
                    return "getAvbPublicKey";
                case 15:
                    return "suggestScratchSize";
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
                data.enforceInterface(IDynamicSystemService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDynamicSystemService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = startInstallation(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            long _arg1 = data.readLong();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result2 = createPartition(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            boolean _result3 = closePartition();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            boolean _result4 = finishInstallation();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            GsiProgress _result5 = getInstallationProgress();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 6:
                            boolean _result6 = abort();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 7:
                            boolean _result7 = isInUse();
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            boolean _result8 = isInstalled();
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 9:
                            boolean _result9 = isEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 10:
                            boolean _result10 = remove();
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 11:
                            boolean _arg03 = data.readBoolean();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result11 = setEnable(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 12:
                            ParcelFileDescriptor _arg04 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            long _arg13 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result12 = setAshmem(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 13:
                            long _arg05 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result13 = submitFromAshmem(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 14:
                            AvbPublicKey _arg06 = new AvbPublicKey();
                            data.enforceNoDataAvail();
                            boolean _result14 = getAvbPublicKey(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            reply.writeTypedObject(_arg06, 1);
                            break;
                        case 15:
                            long _result15 = suggestScratchSize();
                            reply.writeNoException();
                            reply.writeLong(_result15);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.image.IDynamicSystemService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IDynamicSystemService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDynamicSystemService.DESCRIPTOR;
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean startInstallation(String dsuSlot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    _data.writeString(dsuSlot);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public int createPartition(String name, long size, boolean readOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(size);
                    _data.writeBoolean(readOnly);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean closePartition() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean finishInstallation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public GsiProgress getInstallationProgress() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    GsiProgress _result = (GsiProgress) _reply.readTypedObject(GsiProgress.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean abort() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean isInUse() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean isInstalled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean isEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean remove() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean setEnable(boolean enable, boolean oneShot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    _data.writeBoolean(oneShot);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean setAshmem(ParcelFileDescriptor fd, long size) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    _data.writeLong(size);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean submitFromAshmem(long bytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    _data.writeLong(bytes);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public boolean getAvbPublicKey(AvbPublicKey dst) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    if (_reply.readInt() != 0) {
                        dst.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.image.IDynamicSystemService
            public long suggestScratchSize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDynamicSystemService.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void startInstallation_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void createPartition_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void closePartition_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void finishInstallation_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void getInstallationProgress_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void abort_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void isEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void remove_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void setEnable_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void setAshmem_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void submitFromAshmem_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void getAvbPublicKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        protected void suggestScratchSize_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_DYNAMIC_SYSTEM, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
