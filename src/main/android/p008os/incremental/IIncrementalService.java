package android.p008os.incremental;

import android.content.p001pm.DataLoaderParamsParcel;
import android.content.p001pm.IDataLoaderStatusListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.incremental.IStorageHealthListener;
import android.p008os.incremental.IStorageLoadingProgressListener;
/* renamed from: android.os.incremental.IIncrementalService */
/* loaded from: classes3.dex */
public interface IIncrementalService extends IInterface {
    public static final int BIND_PERMANENT = 1;
    public static final int BIND_TEMPORARY = 0;
    public static final int CREATE_MODE_CREATE = 4;
    public static final int CREATE_MODE_OPEN_EXISTING = 8;
    public static final int CREATE_MODE_PERMANENT_BIND = 2;
    public static final int CREATE_MODE_TEMPORARY_BIND = 1;
    public static final String DESCRIPTOR = "android.os.incremental.IIncrementalService";
    public static final String METRICS_DATA_LOADER_BIND_DELAY_MILLIS = "dataLoaderBindDelayMillis";
    public static final String METRICS_DATA_LOADER_STATUS_CODE = "dataLoaderStatusCode";
    public static final String METRICS_LAST_READ_ERROR_NUMBER = "lastReadErrorNo";
    public static final String METRICS_LAST_READ_ERROR_UID = "lastReadErrorUid";
    public static final String METRICS_MILLIS_SINCE_LAST_DATA_LOADER_BIND = "millisSinceLastDataLoaderBind";
    public static final String METRICS_MILLIS_SINCE_LAST_READ_ERROR = "millisSinceLastReadError";
    public static final String METRICS_MILLIS_SINCE_OLDEST_PENDING_READ = "millisSinceOldestPendingRead";
    public static final String METRICS_READ_LOGS_ENABLED = "readLogsEnabled";
    public static final String METRICS_STORAGE_HEALTH_STATUS_CODE = "storageHealthStatusCode";
    public static final String METRICS_TOTAL_DELAYED_READS = "totalDelayedReads";
    public static final String METRICS_TOTAL_DELAYED_READS_MILLIS = "totalDelayedReadsMillis";
    public static final String METRICS_TOTAL_FAILED_READS = "totalFailedReads";

    boolean configureNativeBinaries(int i, String str, String str2, String str3, boolean z) throws RemoteException;

    int createLinkedStorage(String str, int i, int i2) throws RemoteException;

    int createStorage(String str, DataLoaderParamsParcel dataLoaderParamsParcel, int i) throws RemoteException;

    int deleteBindMount(int i, String str) throws RemoteException;

    void deleteStorage(int i) throws RemoteException;

    void disallowReadLogs(int i) throws RemoteException;

    float getLoadingProgress(int i) throws RemoteException;

    byte[] getMetadataById(int i, byte[] bArr) throws RemoteException;

    byte[] getMetadataByPath(int i, String str) throws RemoteException;

    PersistableBundle getMetrics(int i) throws RemoteException;

    int isFileFullyLoaded(int i, String str) throws RemoteException;

    int isFullyLoaded(int i) throws RemoteException;

    int makeBindMount(int i, String str, String str2, int i2) throws RemoteException;

    int makeDirectories(int i, String str) throws RemoteException;

    int makeDirectory(int i, String str) throws RemoteException;

    int makeFile(int i, String str, int i2, IncrementalNewFileParams incrementalNewFileParams, byte[] bArr) throws RemoteException;

    int makeFileFromRange(int i, String str, String str2, long j, long j2) throws RemoteException;

    int makeLink(int i, String str, int i2, String str2) throws RemoteException;

    void onInstallationComplete(int i) throws RemoteException;

    int openStorage(String str) throws RemoteException;

    boolean registerLoadingProgressListener(int i, IStorageLoadingProgressListener iStorageLoadingProgressListener) throws RemoteException;

    boolean startLoading(int i, DataLoaderParamsParcel dataLoaderParamsParcel, IDataLoaderStatusListener iDataLoaderStatusListener, StorageHealthCheckParams storageHealthCheckParams, IStorageHealthListener iStorageHealthListener, PerUidReadTimeouts[] perUidReadTimeoutsArr) throws RemoteException;

    int unlink(int i, String str) throws RemoteException;

    boolean unregisterLoadingProgressListener(int i) throws RemoteException;

    boolean waitForNativeBinariesExtraction(int i) throws RemoteException;

    /* renamed from: android.os.incremental.IIncrementalService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IIncrementalService {
        @Override // android.p008os.incremental.IIncrementalService
        public int openStorage(String path) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int createStorage(String path, DataLoaderParamsParcel params, int createMode) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int createLinkedStorage(String path, int otherStorageId, int createMode) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public boolean startLoading(int storageId, DataLoaderParamsParcel params, IDataLoaderStatusListener statusListener, StorageHealthCheckParams healthCheckParams, IStorageHealthListener healthListener, PerUidReadTimeouts[] perUidReadTimeouts) throws RemoteException {
            return false;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public void onInstallationComplete(int storageId) throws RemoteException {
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeBindMount(int storageId, String sourcePath, String targetFullPath, int bindType) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int deleteBindMount(int storageId, String targetFullPath) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeDirectory(int storageId, String path) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeDirectories(int storageId, String path) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeFile(int storageId, String path, int mode, IncrementalNewFileParams params, byte[] content) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeFileFromRange(int storageId, String targetPath, String sourcePath, long start, long end) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int makeLink(int sourceStorageId, String sourcePath, int destStorageId, String destPath) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int unlink(int storageId, String path) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int isFileFullyLoaded(int storageId, String path) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public int isFullyLoaded(int storageId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public float getLoadingProgress(int storageId) throws RemoteException {
            return 0.0f;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public byte[] getMetadataByPath(int storageId, String path) throws RemoteException {
            return null;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public byte[] getMetadataById(int storageId, byte[] fileId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public void deleteStorage(int storageId) throws RemoteException {
        }

        @Override // android.p008os.incremental.IIncrementalService
        public void disallowReadLogs(int storageId) throws RemoteException {
        }

        @Override // android.p008os.incremental.IIncrementalService
        public boolean configureNativeBinaries(int storageId, String apkFullPath, String libDirRelativePath, String abi, boolean extractNativeLibs) throws RemoteException {
            return false;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public boolean waitForNativeBinariesExtraction(int storageId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public boolean registerLoadingProgressListener(int storageId, IStorageLoadingProgressListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public boolean unregisterLoadingProgressListener(int storageId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.incremental.IIncrementalService
        public PersistableBundle getMetrics(int storageId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.incremental.IIncrementalService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IIncrementalService {
        static final int TRANSACTION_configureNativeBinaries = 21;
        static final int TRANSACTION_createLinkedStorage = 3;
        static final int TRANSACTION_createStorage = 2;
        static final int TRANSACTION_deleteBindMount = 7;
        static final int TRANSACTION_deleteStorage = 19;
        static final int TRANSACTION_disallowReadLogs = 20;
        static final int TRANSACTION_getLoadingProgress = 16;
        static final int TRANSACTION_getMetadataById = 18;
        static final int TRANSACTION_getMetadataByPath = 17;
        static final int TRANSACTION_getMetrics = 25;
        static final int TRANSACTION_isFileFullyLoaded = 14;
        static final int TRANSACTION_isFullyLoaded = 15;
        static final int TRANSACTION_makeBindMount = 6;
        static final int TRANSACTION_makeDirectories = 9;
        static final int TRANSACTION_makeDirectory = 8;
        static final int TRANSACTION_makeFile = 10;
        static final int TRANSACTION_makeFileFromRange = 11;
        static final int TRANSACTION_makeLink = 12;
        static final int TRANSACTION_onInstallationComplete = 5;
        static final int TRANSACTION_openStorage = 1;
        static final int TRANSACTION_registerLoadingProgressListener = 23;
        static final int TRANSACTION_startLoading = 4;
        static final int TRANSACTION_unlink = 13;
        static final int TRANSACTION_unregisterLoadingProgressListener = 24;
        static final int TRANSACTION_waitForNativeBinariesExtraction = 22;

        public Stub() {
            attachInterface(this, IIncrementalService.DESCRIPTOR);
        }

        public static IIncrementalService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IIncrementalService.DESCRIPTOR);
            if (iin != null && (iin instanceof IIncrementalService)) {
                return (IIncrementalService) iin;
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
                    return "openStorage";
                case 2:
                    return "createStorage";
                case 3:
                    return "createLinkedStorage";
                case 4:
                    return "startLoading";
                case 5:
                    return "onInstallationComplete";
                case 6:
                    return "makeBindMount";
                case 7:
                    return "deleteBindMount";
                case 8:
                    return "makeDirectory";
                case 9:
                    return "makeDirectories";
                case 10:
                    return "makeFile";
                case 11:
                    return "makeFileFromRange";
                case 12:
                    return "makeLink";
                case 13:
                    return "unlink";
                case 14:
                    return "isFileFullyLoaded";
                case 15:
                    return "isFullyLoaded";
                case 16:
                    return "getLoadingProgress";
                case 17:
                    return "getMetadataByPath";
                case 18:
                    return "getMetadataById";
                case 19:
                    return "deleteStorage";
                case 20:
                    return "disallowReadLogs";
                case 21:
                    return "configureNativeBinaries";
                case 22:
                    return "waitForNativeBinariesExtraction";
                case 23:
                    return "registerLoadingProgressListener";
                case 24:
                    return "unregisterLoadingProgressListener";
                case 25:
                    return "getMetrics";
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
                data.enforceInterface(IIncrementalService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IIncrementalService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            int _result = openStorage(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            DataLoaderParamsParcel _arg1 = (DataLoaderParamsParcel) data.readTypedObject(DataLoaderParamsParcel.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = createStorage(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = createLinkedStorage(_arg03, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            DataLoaderParamsParcel _arg13 = (DataLoaderParamsParcel) data.readTypedObject(DataLoaderParamsParcel.CREATOR);
                            IDataLoaderStatusListener _arg23 = IDataLoaderStatusListener.Stub.asInterface(data.readStrongBinder());
                            StorageHealthCheckParams _arg3 = (StorageHealthCheckParams) data.readTypedObject(StorageHealthCheckParams.CREATOR);
                            IStorageHealthListener _arg4 = IStorageHealthListener.Stub.asInterface(data.readStrongBinder());
                            PerUidReadTimeouts[] _arg5 = (PerUidReadTimeouts[]) data.createTypedArray(PerUidReadTimeouts.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = startLoading(_arg04, _arg13, _arg23, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onInstallationComplete(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg14 = data.readString();
                            String _arg24 = data.readString();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = makeBindMount(_arg06, _arg14, _arg24, _arg32);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            int _result6 = deleteBindMount(_arg07, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            int _result7 = makeDirectory(_arg08, _arg16);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            int _result8 = makeDirectories(_arg09, _arg17);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            String _arg18 = data.readString();
                            int _arg25 = data.readInt();
                            IncrementalNewFileParams _arg33 = (IncrementalNewFileParams) data.readTypedObject(IncrementalNewFileParams.CREATOR);
                            byte[] _arg42 = data.createByteArray();
                            data.enforceNoDataAvail();
                            int _result9 = makeFile(_arg010, _arg18, _arg25, _arg33, _arg42);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            String _arg19 = data.readString();
                            String _arg26 = data.readString();
                            long _arg34 = data.readLong();
                            long _arg43 = data.readLong();
                            data.enforceNoDataAvail();
                            int _result10 = makeFileFromRange(_arg011, _arg19, _arg26, _arg34, _arg43);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            String _arg110 = data.readString();
                            int _arg27 = data.readInt();
                            String _arg35 = data.readString();
                            data.enforceNoDataAvail();
                            int _result11 = makeLink(_arg012, _arg110, _arg27, _arg35);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            String _arg111 = data.readString();
                            data.enforceNoDataAvail();
                            int _result12 = unlink(_arg013, _arg111);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg112 = data.readString();
                            data.enforceNoDataAvail();
                            int _result13 = isFileFullyLoaded(_arg014, _arg112);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result14 = isFullyLoaded(_arg015);
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result15 = getLoadingProgress(_arg016);
                            reply.writeNoException();
                            reply.writeFloat(_result15);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            String _arg113 = data.readString();
                            data.enforceNoDataAvail();
                            byte[] _result16 = getMetadataByPath(_arg017, _arg113);
                            reply.writeNoException();
                            reply.writeByteArray(_result16);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            byte[] _arg114 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result17 = getMetadataById(_arg018, _arg114);
                            reply.writeNoException();
                            reply.writeByteArray(_result17);
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteStorage(_arg019);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            disallowReadLogs(_arg020);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            String _arg115 = data.readString();
                            String _arg28 = data.readString();
                            String _arg36 = data.readString();
                            boolean _arg44 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result18 = configureNativeBinaries(_arg021, _arg115, _arg28, _arg36, _arg44);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result19 = waitForNativeBinariesExtraction(_arg022);
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            break;
                        case 23:
                            int _arg023 = data.readInt();
                            IStorageLoadingProgressListener _arg116 = IStorageLoadingProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result20 = registerLoadingProgressListener(_arg023, _arg116);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            break;
                        case 24:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result21 = unregisterLoadingProgressListener(_arg024);
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            break;
                        case 25:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            PersistableBundle _result22 = getMetrics(_arg025);
                            reply.writeNoException();
                            reply.writeTypedObject(_result22, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.incremental.IIncrementalService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IIncrementalService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IIncrementalService.DESCRIPTOR;
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int openStorage(String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int createStorage(String path, DataLoaderParamsParcel params, int createMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeTypedObject(params, 0);
                    _data.writeInt(createMode);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int createLinkedStorage(String path, int otherStorageId, int createMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeInt(otherStorageId);
                    _data.writeInt(createMode);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public boolean startLoading(int storageId, DataLoaderParamsParcel params, IDataLoaderStatusListener statusListener, StorageHealthCheckParams healthCheckParams, IStorageHealthListener healthListener, PerUidReadTimeouts[] perUidReadTimeouts) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeTypedObject(params, 0);
                    _data.writeStrongInterface(statusListener);
                    _data.writeTypedObject(healthCheckParams, 0);
                    _data.writeStrongInterface(healthListener);
                    _data.writeTypedArray(perUidReadTimeouts, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public void onInstallationComplete(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeBindMount(int storageId, String sourcePath, String targetFullPath, int bindType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(sourcePath);
                    _data.writeString(targetFullPath);
                    _data.writeInt(bindType);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int deleteBindMount(int storageId, String targetFullPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(targetFullPath);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeDirectory(int storageId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeDirectories(int storageId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeFile(int storageId, String path, int mode, IncrementalNewFileParams params, byte[] content) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    _data.writeInt(mode);
                    _data.writeTypedObject(params, 0);
                    _data.writeByteArray(content);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeFileFromRange(int storageId, String targetPath, String sourcePath, long start, long end) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(targetPath);
                    _data.writeString(sourcePath);
                    _data.writeLong(start);
                    _data.writeLong(end);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int makeLink(int sourceStorageId, String sourcePath, int destStorageId, String destPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(sourceStorageId);
                    _data.writeString(sourcePath);
                    _data.writeInt(destStorageId);
                    _data.writeString(destPath);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int unlink(int storageId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int isFileFullyLoaded(int storageId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public int isFullyLoaded(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public float getLoadingProgress(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public byte[] getMetadataByPath(int storageId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(path);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public byte[] getMetadataById(int storageId, byte[] fileId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeByteArray(fileId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public void deleteStorage(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public void disallowReadLogs(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public boolean configureNativeBinaries(int storageId, String apkFullPath, String libDirRelativePath, String abi, boolean extractNativeLibs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeString(apkFullPath);
                    _data.writeString(libDirRelativePath);
                    _data.writeString(abi);
                    _data.writeBoolean(extractNativeLibs);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public boolean waitForNativeBinariesExtraction(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public boolean registerLoadingProgressListener(int storageId, IStorageLoadingProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public boolean unregisterLoadingProgressListener(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.incremental.IIncrementalService
            public PersistableBundle getMetrics(int storageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIncrementalService.DESCRIPTOR);
                    _data.writeInt(storageId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
