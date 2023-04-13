package android.p008os.storage;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.os.storage.IStorageEventListener */
/* loaded from: classes3.dex */
public interface IStorageEventListener extends IInterface {
    void onDiskDestroyed(DiskInfo diskInfo) throws RemoteException;

    void onDiskScanned(DiskInfo diskInfo, int i) throws RemoteException;

    void onStorageStateChanged(String str, String str2, String str3) throws RemoteException;

    void onUsbMassStorageConnectionChanged(boolean z) throws RemoteException;

    void onVolumeForgotten(String str) throws RemoteException;

    void onVolumeRecordChanged(VolumeRecord volumeRecord) throws RemoteException;

    void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) throws RemoteException;

    /* renamed from: android.os.storage.IStorageEventListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IStorageEventListener {
        @Override // android.p008os.storage.IStorageEventListener
        public void onUsbMassStorageConnectionChanged(boolean connected) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onStorageStateChanged(String path, String oldState, String newState) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeRecordChanged(VolumeRecord rec) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeForgotten(String fsUuid) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onDiskScanned(DiskInfo disk, int volumeCount) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onDiskDestroyed(DiskInfo disk) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.storage.IStorageEventListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStorageEventListener {
        public static final String DESCRIPTOR = "android.os.storage.IStorageEventListener";
        static final int TRANSACTION_onDiskDestroyed = 7;
        static final int TRANSACTION_onDiskScanned = 6;
        static final int TRANSACTION_onStorageStateChanged = 2;
        static final int TRANSACTION_onUsbMassStorageConnectionChanged = 1;
        static final int TRANSACTION_onVolumeForgotten = 5;
        static final int TRANSACTION_onVolumeRecordChanged = 4;
        static final int TRANSACTION_onVolumeStateChanged = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStorageEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStorageEventListener)) {
                return (IStorageEventListener) iin;
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
                    return "onUsbMassStorageConnectionChanged";
                case 2:
                    return "onStorageStateChanged";
                case 3:
                    return "onVolumeStateChanged";
                case 4:
                    return "onVolumeRecordChanged";
                case 5:
                    return "onVolumeForgotten";
                case 6:
                    return "onDiskScanned";
                case 7:
                    return "onDiskDestroyed";
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
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onUsbMassStorageConnectionChanged(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onStorageStateChanged(_arg02, _arg1, _arg2);
                            break;
                        case 3:
                            VolumeInfo _arg03 = (VolumeInfo) data.readTypedObject(VolumeInfo.CREATOR);
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onVolumeStateChanged(_arg03, _arg12, _arg22);
                            break;
                        case 4:
                            VolumeRecord _arg04 = (VolumeRecord) data.readTypedObject(VolumeRecord.CREATOR);
                            data.enforceNoDataAvail();
                            onVolumeRecordChanged(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            onVolumeForgotten(_arg05);
                            break;
                        case 6:
                            DiskInfo _arg06 = (DiskInfo) data.readTypedObject(DiskInfo.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onDiskScanned(_arg06, _arg13);
                            break;
                        case 7:
                            DiskInfo _arg07 = (DiskInfo) data.readTypedObject(DiskInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDiskDestroyed(_arg07);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.storage.IStorageEventListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IStorageEventListener {
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

            @Override // android.p008os.storage.IStorageEventListener
            public void onUsbMassStorageConnectionChanged(boolean connected) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(connected);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onStorageStateChanged(String path, String oldState, String newState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeString(oldState);
                    _data.writeString(newState);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(vol, 0);
                    _data.writeInt(oldState);
                    _data.writeInt(newState);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onVolumeRecordChanged(VolumeRecord rec) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(rec, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onVolumeForgotten(String fsUuid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fsUuid);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onDiskScanned(DiskInfo disk, int volumeCount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(disk, 0);
                    _data.writeInt(volumeCount);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageEventListener
            public void onDiskDestroyed(DiskInfo disk) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(disk, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
