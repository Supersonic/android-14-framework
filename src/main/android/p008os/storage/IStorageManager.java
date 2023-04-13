package android.p008os.storage;

import android.Manifest;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.AttributionSource;
import android.content.p001pm.IPackageMoveObserver;
import android.content.res.ObbInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IVoldTaskListener;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.p008os.storage.IObbActionListener;
import android.p008os.storage.IStorageEventListener;
import android.p008os.storage.IStorageShutdownObserver;
import android.provider.Telephony;
import com.android.internal.p028os.AppFuseMount;
/* renamed from: android.os.storage.IStorageManager */
/* loaded from: classes3.dex */
public interface IStorageManager extends IInterface {
    void abortChanges(String str, boolean z) throws RemoteException;

    void abortIdleMaintenance() throws RemoteException;

    void allocateBytes(String str, long j, int i, String str2) throws RemoteException;

    void benchmark(String str, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    void commitChanges() throws RemoteException;

    void createUserKey(int i, int i2, boolean z) throws RemoteException;

    void destroyUserKey(int i) throws RemoteException;

    void destroyUserStorage(String str, int i, int i2) throws RemoteException;

    void disableAppDataIsolation(String str, int i, int i2) throws RemoteException;

    void fixupAppDir(String str) throws RemoteException;

    void forgetAllVolumes() throws RemoteException;

    void forgetVolume(String str) throws RemoteException;

    void format(String str) throws RemoteException;

    void fstrim(int i, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    long getAllocatableBytes(String str, int i, String str2) throws RemoteException;

    long getCacheQuotaBytes(String str, int i) throws RemoteException;

    long getCacheSizeBytes(String str, int i) throws RemoteException;

    String getCloudMediaProvider() throws RemoteException;

    DiskInfo[] getDisks() throws RemoteException;

    int getExternalStorageMountMode(int i, String str) throws RemoteException;

    PendingIntent getManageSpaceActivityIntent(String str, int i) throws RemoteException;

    String getMountedObbPath(String str) throws RemoteException;

    String getPrimaryStorageUuid() throws RemoteException;

    StorageVolume[] getVolumeList(int i, String str, int i2) throws RemoteException;

    VolumeRecord[] getVolumeRecords(int i) throws RemoteException;

    VolumeInfo[] getVolumes(int i) throws RemoteException;

    boolean isAppIoBlocked(String str, int i, int i2, int i3) throws RemoteException;

    boolean isObbMounted(String str) throws RemoteException;

    boolean isUserKeyUnlocked(int i) throws RemoteException;

    long lastMaintenance() throws RemoteException;

    void lockUserKey(int i) throws RemoteException;

    void mkdirs(String str, String str2) throws RemoteException;

    void mount(String str) throws RemoteException;

    void mountObb(String str, String str2, IObbActionListener iObbActionListener, int i, ObbInfo obbInfo) throws RemoteException;

    AppFuseMount mountProxyFileDescriptorBridge() throws RemoteException;

    boolean needsCheckpoint() throws RemoteException;

    void notifyAppIoBlocked(String str, int i, int i2, int i3) throws RemoteException;

    void notifyAppIoResumed(String str, int i, int i2, int i3) throws RemoteException;

    ParcelFileDescriptor openProxyFileDescriptor(int i, int i2, int i3) throws RemoteException;

    void partitionMixed(String str, int i) throws RemoteException;

    void partitionPrivate(String str) throws RemoteException;

    void partitionPublic(String str) throws RemoteException;

    void prepareUserStorage(String str, int i, int i2, int i3) throws RemoteException;

    void registerListener(IStorageEventListener iStorageEventListener) throws RemoteException;

    void runIdleMaintenance() throws RemoteException;

    void runMaintenance() throws RemoteException;

    void setCloudMediaProvider(String str) throws RemoteException;

    void setDebugFlags(int i, int i2) throws RemoteException;

    void setPrimaryStorageUuid(String str, IPackageMoveObserver iPackageMoveObserver) throws RemoteException;

    void setUserKeyProtection(int i, byte[] bArr) throws RemoteException;

    void setVolumeNickname(String str, String str2) throws RemoteException;

    void setVolumeUserFlags(String str, int i, int i2) throws RemoteException;

    void shutdown(IStorageShutdownObserver iStorageShutdownObserver) throws RemoteException;

    void startCheckpoint(int i) throws RemoteException;

    boolean supportsCheckpoint() throws RemoteException;

    void unlockUserKey(int i, int i2, byte[] bArr) throws RemoteException;

    void unmount(String str) throws RemoteException;

    void unmountObb(String str, boolean z, IObbActionListener iObbActionListener, int i) throws RemoteException;

    void unregisterListener(IStorageEventListener iStorageEventListener) throws RemoteException;

    /* renamed from: android.os.storage.IStorageManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IStorageManager {
        @Override // android.p008os.storage.IStorageManager
        public void registerListener(IStorageEventListener listener) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void unregisterListener(IStorageEventListener listener) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void shutdown(IStorageShutdownObserver observer) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void mountObb(String rawPath, String canonicalPath, IObbActionListener token, int nonce, ObbInfo obbInfo) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void unmountObb(String rawPath, boolean force, IObbActionListener token, int nonce) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public boolean isObbMounted(String rawPath) throws RemoteException {
            return false;
        }

        @Override // android.p008os.storage.IStorageManager
        public String getMountedObbPath(String rawPath) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public StorageVolume[] getVolumeList(int userId, String callingPackage, int flags) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public void mkdirs(String callingPkg, String path) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public long lastMaintenance() throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.storage.IStorageManager
        public void runMaintenance() throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public DiskInfo[] getDisks() throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public VolumeInfo[] getVolumes(int flags) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public VolumeRecord[] getVolumeRecords(int flags) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public void mount(String volId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void unmount(String volId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void format(String volId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void partitionPublic(String diskId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void partitionPrivate(String diskId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void partitionMixed(String diskId, int ratio) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void setVolumeNickname(String fsUuid, String nickname) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void setVolumeUserFlags(String fsUuid, int flags, int mask) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void forgetVolume(String fsUuid) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void forgetAllVolumes() throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public String getPrimaryStorageUuid() throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public void setPrimaryStorageUuid(String volumeUuid, IPackageMoveObserver callback) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void benchmark(String volId, IVoldTaskListener listener) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void setDebugFlags(int flags, int mask) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void createUserKey(int userId, int serialNumber, boolean ephemeral) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void destroyUserKey(int userId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void unlockUserKey(int userId, int serialNumber, byte[] secret) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void lockUserKey(int userId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public boolean isUserKeyUnlocked(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.storage.IStorageManager
        public void prepareUserStorage(String volumeUuid, int userId, int serialNumber, int flags) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void destroyUserStorage(String volumeUuid, int userId, int flags) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void setUserKeyProtection(int userId, byte[] secret) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void fstrim(int flags, IVoldTaskListener listener) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public AppFuseMount mountProxyFileDescriptorBridge() throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public ParcelFileDescriptor openProxyFileDescriptor(int mountPointId, int fileId, int mode) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public long getCacheQuotaBytes(String volumeUuid, int uid) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.storage.IStorageManager
        public long getCacheSizeBytes(String volumeUuid, int uid) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.storage.IStorageManager
        public long getAllocatableBytes(String volumeUuid, int flags, String callingPackage) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.storage.IStorageManager
        public void allocateBytes(String volumeUuid, long bytes, int flags, String callingPackage) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void runIdleMaintenance() throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void abortIdleMaintenance() throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void commitChanges() throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public boolean supportsCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p008os.storage.IStorageManager
        public void startCheckpoint(int numTries) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public boolean needsCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p008os.storage.IStorageManager
        public void abortChanges(String message, boolean retry) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void fixupAppDir(String path) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void disableAppDataIsolation(String pkgName, int pid, int userId) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public PendingIntent getManageSpaceActivityIntent(String packageName, int requestCode) throws RemoteException {
            return null;
        }

        @Override // android.p008os.storage.IStorageManager
        public void notifyAppIoBlocked(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public void notifyAppIoResumed(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public int getExternalStorageMountMode(int uid, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.storage.IStorageManager
        public boolean isAppIoBlocked(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
            return false;
        }

        @Override // android.p008os.storage.IStorageManager
        public void setCloudMediaProvider(String authority) throws RemoteException {
        }

        @Override // android.p008os.storage.IStorageManager
        public String getCloudMediaProvider() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.storage.IStorageManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStorageManager {
        public static final String DESCRIPTOR = "android.os.storage.IStorageManager";
        static final int TRANSACTION_abortChanges = 88;
        static final int TRANSACTION_abortIdleMaintenance = 81;
        static final int TRANSACTION_allocateBytes = 79;
        static final int TRANSACTION_benchmark = 60;
        static final int TRANSACTION_commitChanges = 84;
        static final int TRANSACTION_createUserKey = 62;
        static final int TRANSACTION_destroyUserKey = 63;
        static final int TRANSACTION_destroyUserStorage = 68;
        static final int TRANSACTION_disableAppDataIsolation = 91;
        static final int TRANSACTION_fixupAppDir = 90;
        static final int TRANSACTION_forgetAllVolumes = 57;
        static final int TRANSACTION_forgetVolume = 56;
        static final int TRANSACTION_format = 50;
        static final int TRANSACTION_fstrim = 73;
        static final int TRANSACTION_getAllocatableBytes = 78;
        static final int TRANSACTION_getCacheQuotaBytes = 76;
        static final int TRANSACTION_getCacheSizeBytes = 77;
        static final int TRANSACTION_getCloudMediaProvider = 98;
        static final int TRANSACTION_getDisks = 45;
        static final int TRANSACTION_getExternalStorageMountMode = 95;
        static final int TRANSACTION_getManageSpaceActivityIntent = 92;
        static final int TRANSACTION_getMountedObbPath = 25;
        static final int TRANSACTION_getPrimaryStorageUuid = 58;
        static final int TRANSACTION_getVolumeList = 30;
        static final int TRANSACTION_getVolumeRecords = 47;
        static final int TRANSACTION_getVolumes = 46;
        static final int TRANSACTION_isAppIoBlocked = 96;
        static final int TRANSACTION_isObbMounted = 24;
        static final int TRANSACTION_isUserKeyUnlocked = 66;
        static final int TRANSACTION_lastMaintenance = 42;
        static final int TRANSACTION_lockUserKey = 65;
        static final int TRANSACTION_mkdirs = 35;
        static final int TRANSACTION_mount = 48;
        static final int TRANSACTION_mountObb = 22;
        static final int TRANSACTION_mountProxyFileDescriptorBridge = 74;
        static final int TRANSACTION_needsCheckpoint = 87;
        static final int TRANSACTION_notifyAppIoBlocked = 93;
        static final int TRANSACTION_notifyAppIoResumed = 94;
        static final int TRANSACTION_openProxyFileDescriptor = 75;
        static final int TRANSACTION_partitionMixed = 53;
        static final int TRANSACTION_partitionPrivate = 52;
        static final int TRANSACTION_partitionPublic = 51;
        static final int TRANSACTION_prepareUserStorage = 67;
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_runIdleMaintenance = 80;
        static final int TRANSACTION_runMaintenance = 43;
        static final int TRANSACTION_setCloudMediaProvider = 97;
        static final int TRANSACTION_setDebugFlags = 61;
        static final int TRANSACTION_setPrimaryStorageUuid = 59;
        static final int TRANSACTION_setUserKeyProtection = 71;
        static final int TRANSACTION_setVolumeNickname = 54;
        static final int TRANSACTION_setVolumeUserFlags = 55;
        static final int TRANSACTION_shutdown = 20;
        static final int TRANSACTION_startCheckpoint = 86;
        static final int TRANSACTION_supportsCheckpoint = 85;
        static final int TRANSACTION_unlockUserKey = 64;
        static final int TRANSACTION_unmount = 49;
        static final int TRANSACTION_unmountObb = 23;
        static final int TRANSACTION_unregisterListener = 2;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IStorageManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStorageManager)) {
                return (IStorageManager) iin;
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
                    return "registerListener";
                case 2:
                    return "unregisterListener";
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 21:
                case 26:
                case 27:
                case 28:
                case 29:
                case 31:
                case 32:
                case 33:
                case 34:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 44:
                case 69:
                case 70:
                case 72:
                case 82:
                case 83:
                case 89:
                default:
                    return null;
                case 20:
                    return "shutdown";
                case 22:
                    return "mountObb";
                case 23:
                    return "unmountObb";
                case 24:
                    return "isObbMounted";
                case 25:
                    return "getMountedObbPath";
                case 30:
                    return "getVolumeList";
                case 35:
                    return "mkdirs";
                case 42:
                    return "lastMaintenance";
                case 43:
                    return "runMaintenance";
                case 45:
                    return "getDisks";
                case 46:
                    return "getVolumes";
                case 47:
                    return "getVolumeRecords";
                case 48:
                    return "mount";
                case 49:
                    return "unmount";
                case 50:
                    return Telephony.CellBroadcasts.MESSAGE_FORMAT;
                case 51:
                    return "partitionPublic";
                case 52:
                    return "partitionPrivate";
                case 53:
                    return "partitionMixed";
                case 54:
                    return "setVolumeNickname";
                case 55:
                    return "setVolumeUserFlags";
                case 56:
                    return "forgetVolume";
                case 57:
                    return "forgetAllVolumes";
                case 58:
                    return "getPrimaryStorageUuid";
                case 59:
                    return "setPrimaryStorageUuid";
                case 60:
                    return "benchmark";
                case 61:
                    return "setDebugFlags";
                case 62:
                    return "createUserKey";
                case 63:
                    return "destroyUserKey";
                case 64:
                    return "unlockUserKey";
                case 65:
                    return "lockUserKey";
                case 66:
                    return "isUserKeyUnlocked";
                case 67:
                    return "prepareUserStorage";
                case 68:
                    return "destroyUserStorage";
                case 71:
                    return "setUserKeyProtection";
                case 73:
                    return "fstrim";
                case 74:
                    return "mountProxyFileDescriptorBridge";
                case 75:
                    return "openProxyFileDescriptor";
                case 76:
                    return "getCacheQuotaBytes";
                case 77:
                    return "getCacheSizeBytes";
                case 78:
                    return "getAllocatableBytes";
                case 79:
                    return "allocateBytes";
                case 80:
                    return "runIdleMaintenance";
                case 81:
                    return "abortIdleMaintenance";
                case 84:
                    return "commitChanges";
                case 85:
                    return "supportsCheckpoint";
                case 86:
                    return "startCheckpoint";
                case 87:
                    return "needsCheckpoint";
                case 88:
                    return "abortChanges";
                case 90:
                    return "fixupAppDir";
                case 91:
                    return "disableAppDataIsolation";
                case 92:
                    return "getManageSpaceActivityIntent";
                case 93:
                    return "notifyAppIoBlocked";
                case 94:
                    return "notifyAppIoResumed";
                case 95:
                    return "getExternalStorageMountMode";
                case 96:
                    return "isAppIoBlocked";
                case 97:
                    return "setCloudMediaProvider";
                case 98:
                    return "getCloudMediaProvider";
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
                            IStorageEventListener _arg0 = IStorageEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            IStorageEventListener _arg02 = IStorageEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 21:
                        case 26:
                        case 27:
                        case 28:
                        case 29:
                        case 31:
                        case 32:
                        case 33:
                        case 34:
                        case 36:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 41:
                        case 44:
                        case 69:
                        case 70:
                        case 72:
                        case 82:
                        case 83:
                        case 89:
                        default:
                            return super.onTransact(code, data, reply, flags);
                        case 20:
                            IStorageShutdownObserver _arg03 = IStorageShutdownObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            shutdown(_arg03);
                            reply.writeNoException();
                            break;
                        case 22:
                            String _arg04 = data.readString();
                            String _arg1 = data.readString();
                            IObbActionListener _arg2 = IObbActionListener.Stub.asInterface(data.readStrongBinder());
                            int _arg3 = data.readInt();
                            ObbInfo _arg4 = (ObbInfo) data.readTypedObject(ObbInfo.CREATOR);
                            data.enforceNoDataAvail();
                            mountObb(_arg04, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 23:
                            String _arg05 = data.readString();
                            boolean _arg12 = data.readBoolean();
                            IObbActionListener _arg22 = IObbActionListener.Stub.asInterface(data.readStrongBinder());
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            unmountObb(_arg05, _arg12, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 24:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = isObbMounted(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 25:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            String _result2 = getMountedObbPath(_arg07);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 30:
                            int _arg08 = data.readInt();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            StorageVolume[] _result3 = getVolumeList(_arg08, _arg13, _arg23);
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 35:
                            String _arg09 = data.readString();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            mkdirs(_arg09, _arg14);
                            reply.writeNoException();
                            break;
                        case 42:
                            long _result4 = lastMaintenance();
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 43:
                            runMaintenance();
                            reply.writeNoException();
                            break;
                        case 45:
                            DiskInfo[] _result5 = getDisks();
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 46:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            VolumeInfo[] _result6 = getVolumes(_arg010);
                            reply.writeNoException();
                            reply.writeTypedArray(_result6, 1);
                            break;
                        case 47:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            VolumeRecord[] _result7 = getVolumeRecords(_arg011);
                            reply.writeNoException();
                            reply.writeTypedArray(_result7, 1);
                            break;
                        case 48:
                            String _arg012 = data.readString();
                            data.enforceNoDataAvail();
                            mount(_arg012);
                            reply.writeNoException();
                            break;
                        case 49:
                            String _arg013 = data.readString();
                            data.enforceNoDataAvail();
                            unmount(_arg013);
                            reply.writeNoException();
                            break;
                        case 50:
                            String _arg014 = data.readString();
                            data.enforceNoDataAvail();
                            format(_arg014);
                            reply.writeNoException();
                            break;
                        case 51:
                            String _arg015 = data.readString();
                            data.enforceNoDataAvail();
                            partitionPublic(_arg015);
                            reply.writeNoException();
                            break;
                        case 52:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            partitionPrivate(_arg016);
                            reply.writeNoException();
                            break;
                        case 53:
                            String _arg017 = data.readString();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            partitionMixed(_arg017, _arg15);
                            reply.writeNoException();
                            break;
                        case 54:
                            String _arg018 = data.readString();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            setVolumeNickname(_arg018, _arg16);
                            reply.writeNoException();
                            break;
                        case 55:
                            String _arg019 = data.readString();
                            int _arg17 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            setVolumeUserFlags(_arg019, _arg17, _arg24);
                            reply.writeNoException();
                            break;
                        case 56:
                            String _arg020 = data.readString();
                            data.enforceNoDataAvail();
                            forgetVolume(_arg020);
                            reply.writeNoException();
                            break;
                        case 57:
                            forgetAllVolumes();
                            reply.writeNoException();
                            break;
                        case 58:
                            String _result8 = getPrimaryStorageUuid();
                            reply.writeNoException();
                            reply.writeString(_result8);
                            break;
                        case 59:
                            String _arg021 = data.readString();
                            IPackageMoveObserver _arg18 = IPackageMoveObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setPrimaryStorageUuid(_arg021, _arg18);
                            reply.writeNoException();
                            break;
                        case 60:
                            String _arg022 = data.readString();
                            IVoldTaskListener _arg19 = IVoldTaskListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            benchmark(_arg022, _arg19);
                            reply.writeNoException();
                            break;
                        case 61:
                            int _arg023 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            setDebugFlags(_arg023, _arg110);
                            reply.writeNoException();
                            break;
                        case 62:
                            int _arg024 = data.readInt();
                            int _arg111 = data.readInt();
                            boolean _arg25 = data.readBoolean();
                            data.enforceNoDataAvail();
                            createUserKey(_arg024, _arg111, _arg25);
                            reply.writeNoException();
                            break;
                        case 63:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyUserKey(_arg025);
                            reply.writeNoException();
                            break;
                        case 64:
                            int _arg026 = data.readInt();
                            int _arg112 = data.readInt();
                            byte[] _arg26 = data.createByteArray();
                            data.enforceNoDataAvail();
                            unlockUserKey(_arg026, _arg112, _arg26);
                            reply.writeNoException();
                            break;
                        case 65:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            lockUserKey(_arg027);
                            reply.writeNoException();
                            break;
                        case 66:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = isUserKeyUnlocked(_arg028);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 67:
                            String _arg029 = data.readString();
                            int _arg113 = data.readInt();
                            int _arg27 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            prepareUserStorage(_arg029, _arg113, _arg27, _arg33);
                            reply.writeNoException();
                            break;
                        case 68:
                            String _arg030 = data.readString();
                            int _arg114 = data.readInt();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyUserStorage(_arg030, _arg114, _arg28);
                            reply.writeNoException();
                            break;
                        case 71:
                            int _arg031 = data.readInt();
                            byte[] _arg115 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setUserKeyProtection(_arg031, _arg115);
                            reply.writeNoException();
                            break;
                        case 73:
                            int _arg032 = data.readInt();
                            IVoldTaskListener _arg116 = IVoldTaskListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            fstrim(_arg032, _arg116);
                            reply.writeNoException();
                            break;
                        case 74:
                            AppFuseMount _result10 = mountProxyFileDescriptorBridge();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 75:
                            int _arg033 = data.readInt();
                            int _arg117 = data.readInt();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result11 = openProxyFileDescriptor(_arg033, _arg117, _arg29);
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            break;
                        case 76:
                            String _arg034 = data.readString();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result12 = getCacheQuotaBytes(_arg034, _arg118);
                            reply.writeNoException();
                            reply.writeLong(_result12);
                            break;
                        case 77:
                            String _arg035 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result13 = getCacheSizeBytes(_arg035, _arg119);
                            reply.writeNoException();
                            reply.writeLong(_result13);
                            break;
                        case 78:
                            String _arg036 = data.readString();
                            int _arg120 = data.readInt();
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            long _result14 = getAllocatableBytes(_arg036, _arg120, _arg210);
                            reply.writeNoException();
                            reply.writeLong(_result14);
                            break;
                        case 79:
                            String _arg037 = data.readString();
                            long _arg121 = data.readLong();
                            int _arg211 = data.readInt();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            allocateBytes(_arg037, _arg121, _arg211, _arg34);
                            reply.writeNoException();
                            break;
                        case 80:
                            runIdleMaintenance();
                            reply.writeNoException();
                            break;
                        case 81:
                            abortIdleMaintenance();
                            reply.writeNoException();
                            break;
                        case 84:
                            commitChanges();
                            reply.writeNoException();
                            break;
                        case 85:
                            boolean _result15 = supportsCheckpoint();
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 86:
                            int _arg038 = data.readInt();
                            data.enforceNoDataAvail();
                            startCheckpoint(_arg038);
                            reply.writeNoException();
                            break;
                        case 87:
                            boolean _result16 = needsCheckpoint();
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 88:
                            String _arg039 = data.readString();
                            boolean _arg122 = data.readBoolean();
                            data.enforceNoDataAvail();
                            abortChanges(_arg039, _arg122);
                            reply.writeNoException();
                            break;
                        case 90:
                            String _arg040 = data.readString();
                            data.enforceNoDataAvail();
                            fixupAppDir(_arg040);
                            reply.writeNoException();
                            break;
                        case 91:
                            String _arg041 = data.readString();
                            int _arg123 = data.readInt();
                            int _arg212 = data.readInt();
                            data.enforceNoDataAvail();
                            disableAppDataIsolation(_arg041, _arg123, _arg212);
                            reply.writeNoException();
                            break;
                        case 92:
                            String _arg042 = data.readString();
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            PendingIntent _result17 = getManageSpaceActivityIntent(_arg042, _arg124);
                            reply.writeNoException();
                            reply.writeTypedObject(_result17, 1);
                            break;
                        case 93:
                            String _arg043 = data.readString();
                            int _arg125 = data.readInt();
                            int _arg213 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAppIoBlocked(_arg043, _arg125, _arg213, _arg35);
                            reply.writeNoException();
                            break;
                        case 94:
                            String _arg044 = data.readString();
                            int _arg126 = data.readInt();
                            int _arg214 = data.readInt();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAppIoResumed(_arg044, _arg126, _arg214, _arg36);
                            reply.writeNoException();
                            break;
                        case 95:
                            int _arg045 = data.readInt();
                            String _arg127 = data.readString();
                            data.enforceNoDataAvail();
                            int _result18 = getExternalStorageMountMode(_arg045, _arg127);
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            break;
                        case 96:
                            String _arg046 = data.readString();
                            int _arg128 = data.readInt();
                            int _arg215 = data.readInt();
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result19 = isAppIoBlocked(_arg046, _arg128, _arg215, _arg37);
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            break;
                        case 97:
                            String _arg047 = data.readString();
                            data.enforceNoDataAvail();
                            setCloudMediaProvider(_arg047);
                            reply.writeNoException();
                            break;
                        case 98:
                            String _result20 = getCloudMediaProvider();
                            reply.writeNoException();
                            reply.writeString(_result20);
                            break;
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.storage.IStorageManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IStorageManager {
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

            @Override // android.p008os.storage.IStorageManager
            public void registerListener(IStorageEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void unregisterListener(IStorageEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void shutdown(IStorageShutdownObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void mountObb(String rawPath, String canonicalPath, IObbActionListener token, int nonce, ObbInfo obbInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    _data.writeString(canonicalPath);
                    _data.writeStrongInterface(token);
                    _data.writeInt(nonce);
                    _data.writeTypedObject(obbInfo, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void unmountObb(String rawPath, boolean force, IObbActionListener token, int nonce) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    _data.writeBoolean(force);
                    _data.writeStrongInterface(token);
                    _data.writeInt(nonce);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public boolean isObbMounted(String rawPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public String getMountedObbPath(String rawPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rawPath);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public StorageVolume[] getVolumeList(int userId, String callingPackage, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    _data.writeInt(flags);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    StorageVolume[] _result = (StorageVolume[]) _reply.createTypedArray(StorageVolume.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void mkdirs(String callingPkg, String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(path);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public long lastMaintenance() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void runMaintenance() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public DiskInfo[] getDisks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    DiskInfo[] _result = (DiskInfo[]) _reply.createTypedArray(DiskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public VolumeInfo[] getVolumes(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    VolumeInfo[] _result = (VolumeInfo[]) _reply.createTypedArray(VolumeInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public VolumeRecord[] getVolumeRecords(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    VolumeRecord[] _result = (VolumeRecord[]) _reply.createTypedArray(VolumeRecord.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void mount(String volId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void unmount(String volId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void format(String volId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void partitionPublic(String diskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void partitionPrivate(String diskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void partitionMixed(String diskId, int ratio) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(diskId);
                    _data.writeInt(ratio);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setVolumeNickname(String fsUuid, String nickname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fsUuid);
                    _data.writeString(nickname);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setVolumeUserFlags(String fsUuid, int flags, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fsUuid);
                    _data.writeInt(flags);
                    _data.writeInt(mask);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void forgetVolume(String fsUuid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(fsUuid);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void forgetAllVolumes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public String getPrimaryStorageUuid() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setPrimaryStorageUuid(String volumeUuid, IPackageMoveObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void benchmark(String volId, IVoldTaskListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setDebugFlags(int flags, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(mask);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void createUserKey(int userId, int serialNumber, boolean ephemeral) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(serialNumber);
                    _data.writeBoolean(ephemeral);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void destroyUserKey(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void unlockUserKey(int userId, int serialNumber, byte[] secret) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(serialNumber);
                    _data.writeByteArray(secret);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void lockUserKey(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public boolean isUserKeyUnlocked(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void prepareUserStorage(String volumeUuid, int userId, int serialNumber, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(userId);
                    _data.writeInt(serialNumber);
                    _data.writeInt(flags);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void destroyUserStorage(String volumeUuid, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setUserKeyProtection(int userId, byte[] secret) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeByteArray(secret);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void fstrim(int flags, IVoldTaskListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public AppFuseMount mountProxyFileDescriptorBridge() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    AppFuseMount _result = (AppFuseMount) _reply.readTypedObject(AppFuseMount.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public ParcelFileDescriptor openProxyFileDescriptor(int mountPointId, int fileId, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mountPointId);
                    _data.writeInt(fileId);
                    _data.writeInt(mode);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public long getCacheQuotaBytes(String volumeUuid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public long getCacheSizeBytes(String volumeUuid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public long getAllocatableBytes(String volumeUuid, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void allocateBytes(String volumeUuid, long bytes, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeLong(bytes);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void runIdleMaintenance() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void abortIdleMaintenance() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void commitChanges() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public boolean supportsCheckpoint() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void startCheckpoint(int numTries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(numTries);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public boolean needsCheckpoint() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void abortChanges(String message, boolean retry) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(message);
                    _data.writeBoolean(retry);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void fixupAppDir(String path) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void disableAppDataIsolation(String pkgName, int pid, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeInt(pid);
                    _data.writeInt(userId);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public PendingIntent getManageSpaceActivityIntent(String packageName, int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(requestCode);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    PendingIntent _result = (PendingIntent) _reply.readTypedObject(PendingIntent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void notifyAppIoBlocked(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeInt(tid);
                    _data.writeInt(reason);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void notifyAppIoResumed(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeInt(tid);
                    _data.writeInt(reason);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public int getExternalStorageMountMode(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public boolean isAppIoBlocked(String volumeUuid, int uid, int tid, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeInt(tid);
                    _data.writeInt(reason);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public void setCloudMediaProvider(String authority) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(authority);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.storage.IStorageManager
            public String getCloudMediaProvider() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void shutdown_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SHUTDOWN, source);
        }

        protected void runMaintenance_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void mount_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void unmount_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void format_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void partitionPublic_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void partitionPrivate_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void partitionMixed_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void setVolumeNickname_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void setVolumeUserFlags_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void forgetVolume_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void forgetAllVolumes_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void setPrimaryStorageUuid_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void benchmark_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void setDebugFlags_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_UNMOUNT_FILESYSTEMS, source);
        }

        protected void createUserKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void destroyUserKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void unlockUserKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void lockUserKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void prepareUserStorage_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void destroyUserStorage_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void setUserKeyProtection_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.STORAGE_INTERNAL, source);
        }

        protected void fstrim_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void needsCheckpoint_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MOUNT_FORMAT_FILESYSTEMS, source);
        }

        protected void getExternalStorageMountMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.WRITE_MEDIA_STORAGE, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 97;
        }
    }
}
