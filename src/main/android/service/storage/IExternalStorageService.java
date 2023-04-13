package android.service.storage;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.storage.StorageVolume;
/* loaded from: classes3.dex */
public interface IExternalStorageService extends IInterface {
    public static final String DESCRIPTOR = "android.service.storage.IExternalStorageService";

    void endSession(String str, RemoteCallback remoteCallback) throws RemoteException;

    void freeCache(String str, String str2, long j, RemoteCallback remoteCallback) throws RemoteException;

    void notifyAnrDelayStarted(String str, int i, int i2, int i3) throws RemoteException;

    void notifyVolumeStateChanged(String str, StorageVolume storageVolume, RemoteCallback remoteCallback) throws RemoteException;

    void startSession(String str, int i, ParcelFileDescriptor parcelFileDescriptor, String str2, String str3, RemoteCallback remoteCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IExternalStorageService {
        @Override // android.service.storage.IExternalStorageService
        public void startSession(String sessionId, int type, ParcelFileDescriptor deviceFd, String upperPath, String lowerPath, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.storage.IExternalStorageService
        public void endSession(String sessionId, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.storage.IExternalStorageService
        public void notifyVolumeStateChanged(String sessionId, StorageVolume vol, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.storage.IExternalStorageService
        public void freeCache(String sessionId, String volumeUuid, long bytes, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.storage.IExternalStorageService
        public void notifyAnrDelayStarted(String packageName, int uid, int tid, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IExternalStorageService {
        static final int TRANSACTION_endSession = 2;
        static final int TRANSACTION_freeCache = 4;
        static final int TRANSACTION_notifyAnrDelayStarted = 5;
        static final int TRANSACTION_notifyVolumeStateChanged = 3;
        static final int TRANSACTION_startSession = 1;

        public Stub() {
            attachInterface(this, IExternalStorageService.DESCRIPTOR);
        }

        public static IExternalStorageService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IExternalStorageService.DESCRIPTOR);
            if (iin != null && (iin instanceof IExternalStorageService)) {
                return (IExternalStorageService) iin;
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
                    return "startSession";
                case 2:
                    return "endSession";
                case 3:
                    return "notifyVolumeStateChanged";
                case 4:
                    return "freeCache";
                case 5:
                    return "notifyAnrDelayStarted";
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
                data.enforceInterface(IExternalStorageService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IExternalStorageService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            ParcelFileDescriptor _arg2 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            String _arg3 = data.readString();
                            String _arg4 = data.readString();
                            RemoteCallback _arg5 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            startSession(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            RemoteCallback _arg12 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            endSession(_arg02, _arg12);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            StorageVolume _arg13 = (StorageVolume) data.readTypedObject(StorageVolume.CREATOR);
                            RemoteCallback _arg22 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            notifyVolumeStateChanged(_arg03, _arg13, _arg22);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            long _arg23 = data.readLong();
                            RemoteCallback _arg32 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            freeCache(_arg04, _arg14, _arg23, _arg32);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg15 = data.readInt();
                            int _arg24 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAnrDelayStarted(_arg05, _arg15, _arg24, _arg33);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IExternalStorageService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IExternalStorageService.DESCRIPTOR;
            }

            @Override // android.service.storage.IExternalStorageService
            public void startSession(String sessionId, int type, ParcelFileDescriptor deviceFd, String upperPath, String lowerPath, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExternalStorageService.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeInt(type);
                    _data.writeTypedObject(deviceFd, 0);
                    _data.writeString(upperPath);
                    _data.writeString(lowerPath);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.storage.IExternalStorageService
            public void endSession(String sessionId, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExternalStorageService.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.storage.IExternalStorageService
            public void notifyVolumeStateChanged(String sessionId, StorageVolume vol, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExternalStorageService.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(vol, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.storage.IExternalStorageService
            public void freeCache(String sessionId, String volumeUuid, long bytes, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExternalStorageService.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeString(volumeUuid);
                    _data.writeLong(bytes);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.storage.IExternalStorageService
            public void notifyAnrDelayStarted(String packageName, int uid, int tid, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExternalStorageService.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(tid);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
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
