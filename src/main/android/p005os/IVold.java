package android.p005os;

import android.os.incremental.IncrementalFileSystemControlParcel;
import android.p005os.IVoldListener;
import android.p005os.IVoldMountCallback;
import android.p005os.IVoldTaskListener;
import java.io.FileDescriptor;
/* renamed from: android.os.IVold */
/* loaded from: classes.dex */
public interface IVold extends IInterface {
    public static final int FSTRIM_FLAG_DEEP_TRIM = 1;
    public static final int MOUNT_FLAG_PRIMARY = 1;
    public static final int MOUNT_FLAG_VISIBLE_FOR_READ = 2;
    public static final int MOUNT_FLAG_VISIBLE_FOR_WRITE = 4;
    public static final int PARTITION_TYPE_MIXED = 2;
    public static final int PARTITION_TYPE_PRIVATE = 1;
    public static final int PARTITION_TYPE_PUBLIC = 0;
    public static final int REMOUNT_MODE_ANDROID_WRITABLE = 4;
    public static final int REMOUNT_MODE_DEFAULT = 1;
    public static final int REMOUNT_MODE_INSTALLER = 2;
    public static final int REMOUNT_MODE_NONE = 0;
    public static final int REMOUNT_MODE_PASS_THROUGH = 3;
    public static final int STORAGE_FLAG_CE = 2;
    public static final int STORAGE_FLAG_DE = 1;
    public static final int VOLUME_STATE_BAD_REMOVAL = 8;
    public static final int VOLUME_STATE_CHECKING = 1;
    public static final int VOLUME_STATE_EJECTING = 5;
    public static final int VOLUME_STATE_FORMATTING = 4;
    public static final int VOLUME_STATE_MOUNTED = 2;
    public static final int VOLUME_STATE_MOUNTED_READ_ONLY = 3;
    public static final int VOLUME_STATE_REMOVED = 7;
    public static final int VOLUME_STATE_UNMOUNTABLE = 6;
    public static final int VOLUME_STATE_UNMOUNTED = 0;
    public static final int VOLUME_TYPE_ASEC = 3;
    public static final int VOLUME_TYPE_EMULATED = 2;
    public static final int VOLUME_TYPE_OBB = 4;
    public static final int VOLUME_TYPE_PRIVATE = 1;
    public static final int VOLUME_TYPE_PUBLIC = 0;
    public static final int VOLUME_TYPE_STUB = 5;

    /* renamed from: android.os.IVold$Default */
    /* loaded from: classes.dex */
    public static class Default implements IVold {
        @Override // android.p005os.IVold
        public void abortChanges(String str, boolean z) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void abortFuse() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void abortIdleMaint(IVoldTaskListener iVoldTaskListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void addAppIds(String[] strArr, int[] iArr) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void addSandboxIds(int[] iArr, String[] strArr) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IVold
        public void benchmark(String str, IVoldTaskListener iVoldTaskListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void bindMount(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void commitChanges() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public String createObb(String str, int i) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public String createStubVolume(String str, String str2, String str3, String str4, String str5, int i) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public void createUserKey(int i, int i2, boolean z) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroyDsuMetadataKey(String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroyObb(String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroySandboxForApp(String str, String str2, int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroyStubVolume(String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroyUserKey(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void destroyUserStorage(String str, int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void earlyBootEnded() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void encryptFstab(String str, String str2, boolean z, String str3, String str4) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void ensureAppDirsCreated(String[] strArr, int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void fbeEnable() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void fixupAppDir(String str, int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void forgetPartition(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void format(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void fstrim(int i, IVoldTaskListener iVoldTaskListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public int getStorageLifeTime() throws RemoteException {
            return 0;
        }

        @Override // android.p005os.IVold
        public int[] getUnlockedUsers() throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public int getWriteAmount() throws RemoteException {
            return 0;
        }

        @Override // android.p005os.IVold
        public boolean incFsEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public void initUser0() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public boolean isCheckpointing() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public void lockUserKey(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void markBootAttempt() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void monitor() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void mount(String str, int i, int i2, IVoldMountCallback iVoldMountCallback) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public FileDescriptor mountAppFuse(int i, int i2) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public void mountFstab(String str, String str2, String str3) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public IncrementalFileSystemControlParcel mountIncFs(String str, String str2, int i, String str3) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public void moveStorage(String str, String str2, IVoldTaskListener iVoldTaskListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public boolean needsCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public boolean needsRollback() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public void onSecureKeyguardStateChanged(boolean z) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void onUserAdded(int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void onUserRemoved(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void onUserStarted(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void onUserStopped(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public FileDescriptor openAppFuseFile(int i, int i2, int i3, int i4) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IVold
        public void partition(String str, int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void prepareCheckpoint() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void prepareSandboxForApp(String str, int i, String str2, int i2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void prepareUserStorage(String str, int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void refreshLatestWrite() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void remountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void remountUid(int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void reset() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void resetCheckpoint() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void restoreCheckpoint(String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void restoreCheckpointPart(String str, int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void runIdleMaint(boolean z, IVoldTaskListener iVoldTaskListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setGCUrgentPace(int i, int i2, float f, float f2, int i3, int i4, int i5) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setIncFsMountOptions(IncrementalFileSystemControlParcel incrementalFileSystemControlParcel, boolean z, boolean z2, String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setListener(IVoldListener iVoldListener) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setStorageBindingSeed(byte[] bArr) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setUserKeyProtection(int i, String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void setupAppDir(String str, int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void shutdown() throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void startCheckpoint(int i) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public boolean supportsBlockCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public boolean supportsCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public boolean supportsFileCheckpoint() throws RemoteException {
            return false;
        }

        @Override // android.p005os.IVold
        public void unlockUserKey(int i, int i2, String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void unmount(String str) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void unmountAppFuse(int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void unmountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException {
        }

        @Override // android.p005os.IVold
        public void unmountIncFs(String str) throws RemoteException {
        }
    }

    void abortChanges(String str, boolean z) throws RemoteException;

    void abortFuse() throws RemoteException;

    void abortIdleMaint(IVoldTaskListener iVoldTaskListener) throws RemoteException;

    void addAppIds(String[] strArr, int[] iArr) throws RemoteException;

    void addSandboxIds(int[] iArr, String[] strArr) throws RemoteException;

    void benchmark(String str, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    void bindMount(String str, String str2) throws RemoteException;

    void commitChanges() throws RemoteException;

    String createObb(String str, int i) throws RemoteException;

    String createStubVolume(String str, String str2, String str3, String str4, String str5, int i) throws RemoteException;

    void createUserKey(int i, int i2, boolean z) throws RemoteException;

    void destroyDsuMetadataKey(String str) throws RemoteException;

    void destroyObb(String str) throws RemoteException;

    void destroySandboxForApp(String str, String str2, int i) throws RemoteException;

    void destroyStubVolume(String str) throws RemoteException;

    void destroyUserKey(int i) throws RemoteException;

    void destroyUserStorage(String str, int i, int i2) throws RemoteException;

    void earlyBootEnded() throws RemoteException;

    void encryptFstab(String str, String str2, boolean z, String str3, String str4) throws RemoteException;

    void ensureAppDirsCreated(String[] strArr, int i) throws RemoteException;

    void fbeEnable() throws RemoteException;

    void fixupAppDir(String str, int i) throws RemoteException;

    void forgetPartition(String str, String str2) throws RemoteException;

    void format(String str, String str2) throws RemoteException;

    void fstrim(int i, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    int getStorageLifeTime() throws RemoteException;

    int[] getUnlockedUsers() throws RemoteException;

    int getWriteAmount() throws RemoteException;

    boolean incFsEnabled() throws RemoteException;

    void initUser0() throws RemoteException;

    boolean isCheckpointing() throws RemoteException;

    void lockUserKey(int i) throws RemoteException;

    void markBootAttempt() throws RemoteException;

    void monitor() throws RemoteException;

    void mount(String str, int i, int i2, IVoldMountCallback iVoldMountCallback) throws RemoteException;

    FileDescriptor mountAppFuse(int i, int i2) throws RemoteException;

    void mountFstab(String str, String str2, String str3) throws RemoteException;

    IncrementalFileSystemControlParcel mountIncFs(String str, String str2, int i, String str3) throws RemoteException;

    void moveStorage(String str, String str2, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    boolean needsCheckpoint() throws RemoteException;

    boolean needsRollback() throws RemoteException;

    void onSecureKeyguardStateChanged(boolean z) throws RemoteException;

    void onUserAdded(int i, int i2, int i3) throws RemoteException;

    void onUserRemoved(int i) throws RemoteException;

    void onUserStarted(int i) throws RemoteException;

    void onUserStopped(int i) throws RemoteException;

    FileDescriptor openAppFuseFile(int i, int i2, int i3, int i4) throws RemoteException;

    void partition(String str, int i, int i2) throws RemoteException;

    void prepareCheckpoint() throws RemoteException;

    void prepareSandboxForApp(String str, int i, String str2, int i2) throws RemoteException;

    void prepareUserStorage(String str, int i, int i2, int i3) throws RemoteException;

    void refreshLatestWrite() throws RemoteException;

    void remountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException;

    void remountUid(int i, int i2) throws RemoteException;

    void reset() throws RemoteException;

    void resetCheckpoint() throws RemoteException;

    void restoreCheckpoint(String str) throws RemoteException;

    void restoreCheckpointPart(String str, int i) throws RemoteException;

    void runIdleMaint(boolean z, IVoldTaskListener iVoldTaskListener) throws RemoteException;

    void setGCUrgentPace(int i, int i2, float f, float f2, int i3, int i4, int i5) throws RemoteException;

    void setIncFsMountOptions(IncrementalFileSystemControlParcel incrementalFileSystemControlParcel, boolean z, boolean z2, String str) throws RemoteException;

    void setListener(IVoldListener iVoldListener) throws RemoteException;

    void setStorageBindingSeed(byte[] bArr) throws RemoteException;

    void setUserKeyProtection(int i, String str) throws RemoteException;

    void setupAppDir(String str, int i) throws RemoteException;

    void shutdown() throws RemoteException;

    void startCheckpoint(int i) throws RemoteException;

    boolean supportsBlockCheckpoint() throws RemoteException;

    boolean supportsCheckpoint() throws RemoteException;

    boolean supportsFileCheckpoint() throws RemoteException;

    void unlockUserKey(int i, int i2, String str) throws RemoteException;

    void unmount(String str) throws RemoteException;

    void unmountAppFuse(int i, int i2) throws RemoteException;

    void unmountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException;

    void unmountIncFs(String str) throws RemoteException;

    /* renamed from: android.os.IVold$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVold {
        public static final String DESCRIPTOR = "android.os.IVold";
        public static final int TRANSACTION_abortChanges = 56;
        public static final int TRANSACTION_abortFuse = 2;
        public static final int TRANSACTION_abortIdleMaint = 30;
        public static final int TRANSACTION_addAppIds = 10;
        public static final int TRANSACTION_addSandboxIds = 11;
        public static final int TRANSACTION_benchmark = 18;
        public static final int TRANSACTION_bindMount = 74;
        public static final int TRANSACTION_commitChanges = 57;
        public static final int TRANSACTION_createObb = 26;
        public static final int TRANSACTION_createStubVolume = 67;
        public static final int TRANSACTION_createUserKey = 42;
        public static final int TRANSACTION_destroyDsuMetadataKey = 75;
        public static final int TRANSACTION_destroyObb = 27;
        public static final int TRANSACTION_destroySandboxForApp = 51;
        public static final int TRANSACTION_destroyStubVolume = 68;
        public static final int TRANSACTION_destroyUserKey = 43;
        public static final int TRANSACTION_destroyUserStorage = 49;
        public static final int TRANSACTION_earlyBootEnded = 66;
        public static final int TRANSACTION_encryptFstab = 40;
        public static final int TRANSACTION_ensureAppDirsCreated = 25;
        public static final int TRANSACTION_fbeEnable = 37;
        public static final int TRANSACTION_fixupAppDir = 24;
        public static final int TRANSACTION_forgetPartition = 14;
        public static final int TRANSACTION_format = 17;
        public static final int TRANSACTION_fstrim = 28;
        public static final int TRANSACTION_getStorageLifeTime = 31;
        public static final int TRANSACTION_getUnlockedUsers = 45;
        public static final int TRANSACTION_getWriteAmount = 34;
        public static final int TRANSACTION_incFsEnabled = 70;
        public static final int TRANSACTION_initUser0 = 38;
        public static final int TRANSACTION_isCheckpointing = 55;
        public static final int TRANSACTION_lockUserKey = 47;
        public static final int TRANSACTION_markBootAttempt = 61;
        public static final int TRANSACTION_monitor = 3;
        public static final int TRANSACTION_mount = 15;
        public static final int TRANSACTION_mountAppFuse = 35;
        public static final int TRANSACTION_mountFstab = 39;
        public static final int TRANSACTION_mountIncFs = 71;
        public static final int TRANSACTION_moveStorage = 19;
        public static final int TRANSACTION_needsCheckpoint = 53;
        public static final int TRANSACTION_needsRollback = 54;
        public static final int TRANSACTION_onSecureKeyguardStateChanged = 12;
        public static final int TRANSACTION_onUserAdded = 6;
        public static final int TRANSACTION_onUserRemoved = 7;
        public static final int TRANSACTION_onUserStarted = 8;
        public static final int TRANSACTION_onUserStopped = 9;
        public static final int TRANSACTION_openAppFuseFile = 69;
        public static final int TRANSACTION_partition = 13;
        public static final int TRANSACTION_prepareCheckpoint = 58;
        public static final int TRANSACTION_prepareSandboxForApp = 50;
        public static final int TRANSACTION_prepareUserStorage = 48;
        public static final int TRANSACTION_refreshLatestWrite = 33;
        public static final int TRANSACTION_remountAppStorageDirs = 21;
        public static final int TRANSACTION_remountUid = 20;
        public static final int TRANSACTION_reset = 4;
        public static final int TRANSACTION_resetCheckpoint = 65;
        public static final int TRANSACTION_restoreCheckpoint = 59;
        public static final int TRANSACTION_restoreCheckpointPart = 60;
        public static final int TRANSACTION_runIdleMaint = 29;
        public static final int TRANSACTION_setGCUrgentPace = 32;
        public static final int TRANSACTION_setIncFsMountOptions = 73;
        public static final int TRANSACTION_setListener = 1;
        public static final int TRANSACTION_setStorageBindingSeed = 41;
        public static final int TRANSACTION_setUserKeyProtection = 44;
        public static final int TRANSACTION_setupAppDir = 23;
        public static final int TRANSACTION_shutdown = 5;
        public static final int TRANSACTION_startCheckpoint = 52;
        public static final int TRANSACTION_supportsBlockCheckpoint = 63;
        public static final int TRANSACTION_supportsCheckpoint = 62;
        public static final int TRANSACTION_supportsFileCheckpoint = 64;
        public static final int TRANSACTION_unlockUserKey = 46;
        public static final int TRANSACTION_unmount = 16;
        public static final int TRANSACTION_unmountAppFuse = 36;
        public static final int TRANSACTION_unmountAppStorageDirs = 22;
        public static final int TRANSACTION_unmountIncFs = 72;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVold asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IVold)) {
                return (IVold) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    IVoldListener asInterface = IVoldListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    setListener(asInterface);
                    parcel2.writeNoException();
                    break;
                case 2:
                    abortFuse();
                    parcel2.writeNoException();
                    break;
                case 3:
                    monitor();
                    parcel2.writeNoException();
                    break;
                case 4:
                    reset();
                    parcel2.writeNoException();
                    break;
                case 5:
                    shutdown();
                    parcel2.writeNoException();
                    break;
                case 6:
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    int readInt3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onUserAdded(readInt, readInt2, readInt3);
                    parcel2.writeNoException();
                    break;
                case 7:
                    int readInt4 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onUserRemoved(readInt4);
                    parcel2.writeNoException();
                    break;
                case 8:
                    int readInt5 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onUserStarted(readInt5);
                    parcel2.writeNoException();
                    break;
                case 9:
                    int readInt6 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onUserStopped(readInt6);
                    parcel2.writeNoException();
                    break;
                case 10:
                    String[] createStringArray = parcel.createStringArray();
                    int[] createIntArray = parcel.createIntArray();
                    parcel.enforceNoDataAvail();
                    addAppIds(createStringArray, createIntArray);
                    parcel2.writeNoException();
                    break;
                case 11:
                    int[] createIntArray2 = parcel.createIntArray();
                    String[] createStringArray2 = parcel.createStringArray();
                    parcel.enforceNoDataAvail();
                    addSandboxIds(createIntArray2, createStringArray2);
                    parcel2.writeNoException();
                    break;
                case 12:
                    boolean readBoolean = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onSecureKeyguardStateChanged(readBoolean);
                    parcel2.writeNoException();
                    break;
                case 13:
                    String readString = parcel.readString();
                    int readInt7 = parcel.readInt();
                    int readInt8 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    partition(readString, readInt7, readInt8);
                    parcel2.writeNoException();
                    break;
                case 14:
                    String readString2 = parcel.readString();
                    String readString3 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    forgetPartition(readString2, readString3);
                    parcel2.writeNoException();
                    break;
                case 15:
                    String readString4 = parcel.readString();
                    int readInt9 = parcel.readInt();
                    int readInt10 = parcel.readInt();
                    IVoldMountCallback asInterface2 = IVoldMountCallback.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    mount(readString4, readInt9, readInt10, asInterface2);
                    parcel2.writeNoException();
                    break;
                case 16:
                    String readString5 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    unmount(readString5);
                    parcel2.writeNoException();
                    break;
                case 17:
                    String readString6 = parcel.readString();
                    String readString7 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    format(readString6, readString7);
                    parcel2.writeNoException();
                    break;
                case 18:
                    String readString8 = parcel.readString();
                    IVoldTaskListener asInterface3 = IVoldTaskListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    benchmark(readString8, asInterface3);
                    parcel2.writeNoException();
                    break;
                case 19:
                    String readString9 = parcel.readString();
                    String readString10 = parcel.readString();
                    IVoldTaskListener asInterface4 = IVoldTaskListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    moveStorage(readString9, readString10, asInterface4);
                    parcel2.writeNoException();
                    break;
                case 20:
                    int readInt11 = parcel.readInt();
                    int readInt12 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    remountUid(readInt11, readInt12);
                    parcel2.writeNoException();
                    break;
                case 21:
                    int readInt13 = parcel.readInt();
                    int readInt14 = parcel.readInt();
                    String[] createStringArray3 = parcel.createStringArray();
                    parcel.enforceNoDataAvail();
                    remountAppStorageDirs(readInt13, readInt14, createStringArray3);
                    parcel2.writeNoException();
                    break;
                case 22:
                    int readInt15 = parcel.readInt();
                    int readInt16 = parcel.readInt();
                    String[] createStringArray4 = parcel.createStringArray();
                    parcel.enforceNoDataAvail();
                    unmountAppStorageDirs(readInt15, readInt16, createStringArray4);
                    parcel2.writeNoException();
                    break;
                case 23:
                    String readString11 = parcel.readString();
                    int readInt17 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    setupAppDir(readString11, readInt17);
                    parcel2.writeNoException();
                    break;
                case 24:
                    String readString12 = parcel.readString();
                    int readInt18 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    fixupAppDir(readString12, readInt18);
                    parcel2.writeNoException();
                    break;
                case 25:
                    String[] createStringArray5 = parcel.createStringArray();
                    int readInt19 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    ensureAppDirsCreated(createStringArray5, readInt19);
                    parcel2.writeNoException();
                    break;
                case 26:
                    String readString13 = parcel.readString();
                    int readInt20 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    String createObb = createObb(readString13, readInt20);
                    parcel2.writeNoException();
                    parcel2.writeString(createObb);
                    break;
                case 27:
                    String readString14 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    destroyObb(readString14);
                    parcel2.writeNoException();
                    break;
                case 28:
                    int readInt21 = parcel.readInt();
                    IVoldTaskListener asInterface5 = IVoldTaskListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    fstrim(readInt21, asInterface5);
                    parcel2.writeNoException();
                    break;
                case 29:
                    boolean readBoolean2 = parcel.readBoolean();
                    IVoldTaskListener asInterface6 = IVoldTaskListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    runIdleMaint(readBoolean2, asInterface6);
                    parcel2.writeNoException();
                    break;
                case 30:
                    IVoldTaskListener asInterface7 = IVoldTaskListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    abortIdleMaint(asInterface7);
                    parcel2.writeNoException();
                    break;
                case 31:
                    int storageLifeTime = getStorageLifeTime();
                    parcel2.writeNoException();
                    parcel2.writeInt(storageLifeTime);
                    break;
                case 32:
                    int readInt22 = parcel.readInt();
                    int readInt23 = parcel.readInt();
                    float readFloat = parcel.readFloat();
                    float readFloat2 = parcel.readFloat();
                    int readInt24 = parcel.readInt();
                    int readInt25 = parcel.readInt();
                    int readInt26 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    setGCUrgentPace(readInt22, readInt23, readFloat, readFloat2, readInt24, readInt25, readInt26);
                    parcel2.writeNoException();
                    break;
                case 33:
                    refreshLatestWrite();
                    parcel2.writeNoException();
                    break;
                case 34:
                    int writeAmount = getWriteAmount();
                    parcel2.writeNoException();
                    parcel2.writeInt(writeAmount);
                    break;
                case 35:
                    int readInt27 = parcel.readInt();
                    int readInt28 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    FileDescriptor mountAppFuse = mountAppFuse(readInt27, readInt28);
                    parcel2.writeNoException();
                    parcel2.writeRawFileDescriptor(mountAppFuse);
                    break;
                case 36:
                    int readInt29 = parcel.readInt();
                    int readInt30 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    unmountAppFuse(readInt29, readInt30);
                    parcel2.writeNoException();
                    break;
                case 37:
                    fbeEnable();
                    parcel2.writeNoException();
                    break;
                case 38:
                    initUser0();
                    parcel2.writeNoException();
                    break;
                case 39:
                    String readString15 = parcel.readString();
                    String readString16 = parcel.readString();
                    String readString17 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    mountFstab(readString15, readString16, readString17);
                    parcel2.writeNoException();
                    break;
                case 40:
                    String readString18 = parcel.readString();
                    String readString19 = parcel.readString();
                    boolean readBoolean3 = parcel.readBoolean();
                    String readString20 = parcel.readString();
                    String readString21 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    encryptFstab(readString18, readString19, readBoolean3, readString20, readString21);
                    parcel2.writeNoException();
                    break;
                case 41:
                    byte[] createByteArray = parcel.createByteArray();
                    parcel.enforceNoDataAvail();
                    setStorageBindingSeed(createByteArray);
                    parcel2.writeNoException();
                    break;
                case 42:
                    int readInt31 = parcel.readInt();
                    int readInt32 = parcel.readInt();
                    boolean readBoolean4 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    createUserKey(readInt31, readInt32, readBoolean4);
                    parcel2.writeNoException();
                    break;
                case 43:
                    int readInt33 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    destroyUserKey(readInt33);
                    parcel2.writeNoException();
                    break;
                case 44:
                    int readInt34 = parcel.readInt();
                    String readString22 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    setUserKeyProtection(readInt34, readString22);
                    parcel2.writeNoException();
                    break;
                case 45:
                    int[] unlockedUsers = getUnlockedUsers();
                    parcel2.writeNoException();
                    parcel2.writeIntArray(unlockedUsers);
                    break;
                case 46:
                    int readInt35 = parcel.readInt();
                    int readInt36 = parcel.readInt();
                    String readString23 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    unlockUserKey(readInt35, readInt36, readString23);
                    parcel2.writeNoException();
                    break;
                case 47:
                    int readInt37 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    lockUserKey(readInt37);
                    parcel2.writeNoException();
                    break;
                case 48:
                    String readString24 = parcel.readString();
                    int readInt38 = parcel.readInt();
                    int readInt39 = parcel.readInt();
                    int readInt40 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    prepareUserStorage(readString24, readInt38, readInt39, readInt40);
                    parcel2.writeNoException();
                    break;
                case 49:
                    String readString25 = parcel.readString();
                    int readInt41 = parcel.readInt();
                    int readInt42 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    destroyUserStorage(readString25, readInt41, readInt42);
                    parcel2.writeNoException();
                    break;
                case 50:
                    String readString26 = parcel.readString();
                    int readInt43 = parcel.readInt();
                    String readString27 = parcel.readString();
                    int readInt44 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    prepareSandboxForApp(readString26, readInt43, readString27, readInt44);
                    parcel2.writeNoException();
                    break;
                case 51:
                    String readString28 = parcel.readString();
                    String readString29 = parcel.readString();
                    int readInt45 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    destroySandboxForApp(readString28, readString29, readInt45);
                    parcel2.writeNoException();
                    break;
                case 52:
                    int readInt46 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    startCheckpoint(readInt46);
                    parcel2.writeNoException();
                    break;
                case 53:
                    boolean needsCheckpoint = needsCheckpoint();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(needsCheckpoint);
                    break;
                case 54:
                    boolean needsRollback = needsRollback();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(needsRollback);
                    break;
                case 55:
                    boolean isCheckpointing = isCheckpointing();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(isCheckpointing);
                    break;
                case 56:
                    String readString30 = parcel.readString();
                    boolean readBoolean5 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    abortChanges(readString30, readBoolean5);
                    parcel2.writeNoException();
                    break;
                case 57:
                    commitChanges();
                    parcel2.writeNoException();
                    break;
                case 58:
                    prepareCheckpoint();
                    parcel2.writeNoException();
                    break;
                case 59:
                    String readString31 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    restoreCheckpoint(readString31);
                    parcel2.writeNoException();
                    break;
                case 60:
                    String readString32 = parcel.readString();
                    int readInt47 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    restoreCheckpointPart(readString32, readInt47);
                    parcel2.writeNoException();
                    break;
                case 61:
                    markBootAttempt();
                    parcel2.writeNoException();
                    break;
                case 62:
                    boolean supportsCheckpoint = supportsCheckpoint();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(supportsCheckpoint);
                    break;
                case 63:
                    boolean supportsBlockCheckpoint = supportsBlockCheckpoint();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(supportsBlockCheckpoint);
                    break;
                case 64:
                    boolean supportsFileCheckpoint = supportsFileCheckpoint();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(supportsFileCheckpoint);
                    break;
                case 65:
                    resetCheckpoint();
                    parcel2.writeNoException();
                    break;
                case 66:
                    earlyBootEnded();
                    parcel2.writeNoException();
                    break;
                case 67:
                    String readString33 = parcel.readString();
                    String readString34 = parcel.readString();
                    String readString35 = parcel.readString();
                    String readString36 = parcel.readString();
                    String readString37 = parcel.readString();
                    int readInt48 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    String createStubVolume = createStubVolume(readString33, readString34, readString35, readString36, readString37, readInt48);
                    parcel2.writeNoException();
                    parcel2.writeString(createStubVolume);
                    break;
                case 68:
                    String readString38 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    destroyStubVolume(readString38);
                    parcel2.writeNoException();
                    break;
                case 69:
                    int readInt49 = parcel.readInt();
                    int readInt50 = parcel.readInt();
                    int readInt51 = parcel.readInt();
                    int readInt52 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    FileDescriptor openAppFuseFile = openAppFuseFile(readInt49, readInt50, readInt51, readInt52);
                    parcel2.writeNoException();
                    parcel2.writeRawFileDescriptor(openAppFuseFile);
                    break;
                case 70:
                    boolean incFsEnabled = incFsEnabled();
                    parcel2.writeNoException();
                    parcel2.writeBoolean(incFsEnabled);
                    break;
                case 71:
                    String readString39 = parcel.readString();
                    String readString40 = parcel.readString();
                    int readInt53 = parcel.readInt();
                    String readString41 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    IncrementalFileSystemControlParcel mountIncFs = mountIncFs(readString39, readString40, readInt53, readString41);
                    parcel2.writeNoException();
                    parcel2.writeTypedObject(mountIncFs, 1);
                    break;
                case 72:
                    String readString42 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    unmountIncFs(readString42);
                    parcel2.writeNoException();
                    break;
                case 73:
                    boolean readBoolean6 = parcel.readBoolean();
                    boolean readBoolean7 = parcel.readBoolean();
                    String readString43 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    setIncFsMountOptions((IncrementalFileSystemControlParcel) parcel.readTypedObject(IncrementalFileSystemControlParcel.CREATOR), readBoolean6, readBoolean7, readString43);
                    parcel2.writeNoException();
                    break;
                case 74:
                    String readString44 = parcel.readString();
                    String readString45 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    bindMount(readString44, readString45);
                    parcel2.writeNoException();
                    break;
                case 75:
                    String readString46 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    destroyDsuMetadataKey(readString46);
                    parcel2.writeNoException();
                    break;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* renamed from: android.os.IVold$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IVold {
            public IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.p005os.IVold
            public void setListener(IVoldListener iVoldListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongInterface(iVoldListener);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void abortFuse() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void monitor() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void reset() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void shutdown() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void onUserAdded(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void onUserRemoved(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void onUserStarted(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void onUserStopped(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void addAppIds(String[] strArr, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStringArray(strArr);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void addSandboxIds(int[] iArr, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void onSecureKeyguardStateChanged(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void partition(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void forgetPartition(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void mount(String str, int i, int i2, IVoldMountCallback iVoldMountCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeStrongInterface(iVoldMountCallback);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void unmount(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void format(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void benchmark(String str, IVoldTaskListener iVoldTaskListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongInterface(iVoldTaskListener);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void moveStorage(String str, String str2, IVoldTaskListener iVoldTaskListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStrongInterface(iVoldTaskListener);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void remountUid(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void remountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void unmountAppStorageDirs(int i, int i2, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void setupAppDir(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void fixupAppDir(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void ensureAppDirsCreated(String[] strArr, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStringArray(strArr);
                    obtain.writeInt(i);
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public String createObb(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroyObb(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void fstrim(int i, IVoldTaskListener iVoldTaskListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongInterface(iVoldTaskListener);
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void runIdleMaint(boolean z, IVoldTaskListener iVoldTaskListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    obtain.writeStrongInterface(iVoldTaskListener);
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void abortIdleMaint(IVoldTaskListener iVoldTaskListener) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongInterface(iVoldTaskListener);
                    this.mRemote.transact(30, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public int getStorageLifeTime() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void setGCUrgentPace(int i, int i2, float f, float f2, int i3, int i4, int i5) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeFloat(f);
                    obtain.writeFloat(f2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    this.mRemote.transact(32, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void refreshLatestWrite() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public int getWriteAmount() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public FileDescriptor mountAppFuse(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(35, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readRawFileDescriptor();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void unmountAppFuse(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(36, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void fbeEnable() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void initUser0() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void mountFstab(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(39, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void encryptFstab(String str, String str2, boolean z, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeBoolean(z);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(40, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void setStorageBindingSeed(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    this.mRemote.transact(41, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void createUserKey(int i, int i2, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(42, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroyUserKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(43, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void setUserKeyProtection(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(44, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public int[] getUnlockedUsers() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createIntArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void unlockUserKey(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    this.mRemote.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void lockUserKey(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(47, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void prepareUserStorage(String str, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(48, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroyUserStorage(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(49, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void prepareSandboxForApp(String str, int i, String str2, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeString(str2);
                    obtain.writeInt(i2);
                    this.mRemote.transact(50, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroySandboxForApp(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    this.mRemote.transact(51, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void startCheckpoint(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(52, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean needsCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(53, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean needsRollback() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(54, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean isCheckpointing() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void abortChanges(String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(56, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void commitChanges() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void prepareCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(58, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void restoreCheckpoint(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(59, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void restoreCheckpointPart(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(60, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void markBootAttempt() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(61, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean supportsCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean supportsBlockCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(63, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean supportsFileCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(64, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void resetCheckpoint() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(65, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void earlyBootEnded() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(66, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public String createStubVolume(String str, String str2, String str3, String str4, String str5, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    obtain.writeString(str5);
                    obtain.writeInt(i);
                    this.mRemote.transact(67, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroyStubVolume(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(68, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public FileDescriptor openAppFuseFile(int i, int i2, int i3, int i4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    this.mRemote.transact(69, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readRawFileDescriptor();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public boolean incFsEnabled() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(70, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public IncrementalFileSystemControlParcel mountIncFs(String str, String str2, int i, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeString(str3);
                    this.mRemote.transact(71, obtain, obtain2, 0);
                    obtain2.readException();
                    return (IncrementalFileSystemControlParcel) obtain2.readTypedObject(IncrementalFileSystemControlParcel.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void unmountIncFs(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(72, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void setIncFsMountOptions(IncrementalFileSystemControlParcel incrementalFileSystemControlParcel, boolean z, boolean z2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(incrementalFileSystemControlParcel, 0);
                    obtain.writeBoolean(z);
                    obtain.writeBoolean(z2);
                    obtain.writeString(str);
                    this.mRemote.transact(73, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void bindMount(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(74, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IVold
            public void destroyDsuMetadataKey(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(75, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
