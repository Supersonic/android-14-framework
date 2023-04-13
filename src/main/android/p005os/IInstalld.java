package android.p005os;

import android.p005os.storage.CrateMetadata;
/* renamed from: android.os.IInstalld */
/* loaded from: classes.dex */
public interface IInstalld extends IInterface {
    public static final int FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES = 131072;
    public static final int FLAG_CLEAR_CACHE_ONLY = 16;
    public static final int FLAG_CLEAR_CODE_CACHE_ONLY = 32;
    public static final int FLAG_FORCE = 8192;
    public static final int FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES = 2048;
    public static final int FLAG_FREE_CACHE_NOOP = 1024;
    public static final int FLAG_FREE_CACHE_V2 = 256;
    public static final int FLAG_FREE_CACHE_V2_DEFY_QUOTA = 512;
    public static final int FLAG_STORAGE_CE = 2;
    public static final int FLAG_STORAGE_DE = 1;
    public static final int FLAG_STORAGE_EXTERNAL = 4;
    public static final int FLAG_STORAGE_SDK = 8;
    public static final int FLAG_USE_QUOTA = 4096;

    /* renamed from: android.os.IInstalld$Default */
    /* loaded from: classes.dex */
    public static class Default implements IInstalld {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IInstalld
        public void cleanupInvalidPackageDirs(String str, int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void clearAppData(String str, String str2, int i, int i2, long j) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void clearAppProfiles(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean compileLayouts(String str, String str2, String str3, int i) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void controlDexOptBlocking(boolean z) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean copySystemProfile(String str, int i, String str2, String str3) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public CreateAppDataResult createAppData(CreateAppDataArgs createAppDataArgs) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public CreateAppDataResult[] createAppDataBatched(CreateAppDataArgs[] createAppDataArgsArr) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public void createOatDir(String str, String str2, String str3) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean createProfileSnapshot(int i, String str, String str2, String str3) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void createUserData(String str, int i, int i2, int i3) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public long deleteOdex(String str, String str2, String str3, String str4) throws RemoteException {
            return 0L;
        }

        @Override // android.p005os.IInstalld
        public void deleteReferenceProfile(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyAppData(String str, String str2, int i, int i2, long j) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyAppDataSnapshot(String str, String str2, int i, long j, int i2, int i3) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyAppProfiles(String str) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyCeSnapshotsNotSpecified(String str, int i, int[] iArr) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyProfileSnapshot(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void destroyUserData(String str, int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean dexopt(String str, int i, String str2, String str3, int i2, String str4, int i3, String str5, String str6, String str7, String str8, boolean z, int i4, String str9, String str10, String str11) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public boolean dumpProfiles(int i, String str, String str2, String str3, boolean z) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void fixupAppData(String str, int i) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void freeCache(String str, long j, int i) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public CrateMetadata[] getAppCrates(String str, String[] strArr, int i) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public long[] getAppSize(String str, String[] strArr, int i, int i2, int i3, long[] jArr, String[] strArr2) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public long[] getExternalSize(String str, int i, int i2, int[] iArr) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public int getOdexVisibility(String str, String str2, String str3, String str4) throws RemoteException {
            return 0;
        }

        @Override // android.p005os.IInstalld
        public CrateMetadata[] getUserCrates(String str, int i) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public long[] getUserSize(String str, int i, int i2, int[] iArr) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public byte[] hashSecondaryDexFile(String str, String str2, int i, String str3, int i2) throws RemoteException {
            return null;
        }

        @Override // android.p005os.IInstalld
        public void invalidateMounts() throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean isQuotaSupported(String str) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void linkFile(String str, String str2, String str3, String str4) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void linkNativeLibraryDirectory(String str, String str2, String str3, int i) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public int mergeProfiles(int i, String str, String str2) throws RemoteException {
            return 0;
        }

        @Override // android.p005os.IInstalld
        public void migrateAppData(String str, String str2, int i, int i2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void migrateLegacyObbData() throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void moveAb(String str, String str2, String str3, String str4) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void moveCompleteApp(String str, String str2, String str3, int i, String str4, int i2, String str5) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void onPrivateVolumeRemoved(String str) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean prepareAppProfile(String str, int i, int i2, String str2, String str3, String str4) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void reconcileSdkData(ReconcileSdkDataArgs reconcileSdkDataArgs) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public boolean reconcileSecondaryDexFile(String str, String str2, int i, String[] strArr, String str3, int i2) throws RemoteException {
            return false;
        }

        @Override // android.p005os.IInstalld
        public void restoreAppDataSnapshot(String str, String str2, int i, String str3, int i2, int i3, int i4) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void restoreconAppData(String str, String str2, int i, int i2, int i3, String str3) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void rmPackageDir(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void rmdex(String str, String str2) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void setAppQuota(String str, int i, int i2, long j) throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public void setFirstBoot() throws RemoteException {
        }

        @Override // android.p005os.IInstalld
        public long snapshotAppData(String str, String str2, int i, int i2, int i3) throws RemoteException {
            return 0L;
        }

        @Override // android.p005os.IInstalld
        public void tryMountDataMirror(String str) throws RemoteException {
        }
    }

    void cleanupInvalidPackageDirs(String str, int i, int i2) throws RemoteException;

    void clearAppData(String str, String str2, int i, int i2, long j) throws RemoteException;

    void clearAppProfiles(String str, String str2) throws RemoteException;

    boolean compileLayouts(String str, String str2, String str3, int i) throws RemoteException;

    void controlDexOptBlocking(boolean z) throws RemoteException;

    boolean copySystemProfile(String str, int i, String str2, String str3) throws RemoteException;

    CreateAppDataResult createAppData(CreateAppDataArgs createAppDataArgs) throws RemoteException;

    CreateAppDataResult[] createAppDataBatched(CreateAppDataArgs[] createAppDataArgsArr) throws RemoteException;

    void createOatDir(String str, String str2, String str3) throws RemoteException;

    boolean createProfileSnapshot(int i, String str, String str2, String str3) throws RemoteException;

    void createUserData(String str, int i, int i2, int i3) throws RemoteException;

    long deleteOdex(String str, String str2, String str3, String str4) throws RemoteException;

    void deleteReferenceProfile(String str, String str2) throws RemoteException;

    void destroyAppData(String str, String str2, int i, int i2, long j) throws RemoteException;

    void destroyAppDataSnapshot(String str, String str2, int i, long j, int i2, int i3) throws RemoteException;

    void destroyAppProfiles(String str) throws RemoteException;

    void destroyCeSnapshotsNotSpecified(String str, int i, int[] iArr) throws RemoteException;

    void destroyProfileSnapshot(String str, String str2) throws RemoteException;

    void destroyUserData(String str, int i, int i2) throws RemoteException;

    boolean dexopt(String str, int i, String str2, String str3, int i2, String str4, int i3, String str5, String str6, String str7, String str8, boolean z, int i4, String str9, String str10, String str11) throws RemoteException;

    boolean dumpProfiles(int i, String str, String str2, String str3, boolean z) throws RemoteException;

    void fixupAppData(String str, int i) throws RemoteException;

    void freeCache(String str, long j, int i) throws RemoteException;

    CrateMetadata[] getAppCrates(String str, String[] strArr, int i) throws RemoteException;

    long[] getAppSize(String str, String[] strArr, int i, int i2, int i3, long[] jArr, String[] strArr2) throws RemoteException;

    long[] getExternalSize(String str, int i, int i2, int[] iArr) throws RemoteException;

    int getOdexVisibility(String str, String str2, String str3, String str4) throws RemoteException;

    CrateMetadata[] getUserCrates(String str, int i) throws RemoteException;

    long[] getUserSize(String str, int i, int i2, int[] iArr) throws RemoteException;

    byte[] hashSecondaryDexFile(String str, String str2, int i, String str3, int i2) throws RemoteException;

    void invalidateMounts() throws RemoteException;

    boolean isQuotaSupported(String str) throws RemoteException;

    void linkFile(String str, String str2, String str3, String str4) throws RemoteException;

    void linkNativeLibraryDirectory(String str, String str2, String str3, int i) throws RemoteException;

    int mergeProfiles(int i, String str, String str2) throws RemoteException;

    void migrateAppData(String str, String str2, int i, int i2) throws RemoteException;

    void migrateLegacyObbData() throws RemoteException;

    void moveAb(String str, String str2, String str3, String str4) throws RemoteException;

    void moveCompleteApp(String str, String str2, String str3, int i, String str4, int i2, String str5) throws RemoteException;

    void onPrivateVolumeRemoved(String str) throws RemoteException;

    boolean prepareAppProfile(String str, int i, int i2, String str2, String str3, String str4) throws RemoteException;

    void reconcileSdkData(ReconcileSdkDataArgs reconcileSdkDataArgs) throws RemoteException;

    boolean reconcileSecondaryDexFile(String str, String str2, int i, String[] strArr, String str3, int i2) throws RemoteException;

    void restoreAppDataSnapshot(String str, String str2, int i, String str3, int i2, int i3, int i4) throws RemoteException;

    void restoreconAppData(String str, String str2, int i, int i2, int i3, String str3) throws RemoteException;

    void rmPackageDir(String str, String str2) throws RemoteException;

    void rmdex(String str, String str2) throws RemoteException;

    void setAppQuota(String str, int i, int i2, long j) throws RemoteException;

    void setFirstBoot() throws RemoteException;

    long snapshotAppData(String str, String str2, int i, int i2, int i3) throws RemoteException;

    void tryMountDataMirror(String str) throws RemoteException;

    /* renamed from: android.os.IInstalld$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IInstalld {
        public static final String DESCRIPTOR = "android.os.IInstalld";
        public static final int TRANSACTION_cleanupInvalidPackageDirs = 50;
        public static final int TRANSACTION_clearAppData = 9;
        public static final int TRANSACTION_clearAppProfiles = 26;
        public static final int TRANSACTION_compileLayouts = 21;
        public static final int TRANSACTION_controlDexOptBlocking = 20;
        public static final int TRANSACTION_copySystemProfile = 25;
        public static final int TRANSACTION_createAppData = 4;
        public static final int TRANSACTION_createAppDataBatched = 5;
        public static final int TRANSACTION_createOatDir = 34;
        public static final int TRANSACTION_createProfileSnapshot = 29;
        public static final int TRANSACTION_createUserData = 1;
        public static final int TRANSACTION_deleteOdex = 37;
        public static final int TRANSACTION_deleteReferenceProfile = 28;
        public static final int TRANSACTION_destroyAppData = 10;
        public static final int TRANSACTION_destroyAppDataSnapshot = 45;
        public static final int TRANSACTION_destroyAppProfiles = 27;
        public static final int TRANSACTION_destroyCeSnapshotsNotSpecified = 46;
        public static final int TRANSACTION_destroyProfileSnapshot = 30;
        public static final int TRANSACTION_destroyUserData = 2;
        public static final int TRANSACTION_dexopt = 19;
        public static final int TRANSACTION_dumpProfiles = 24;
        public static final int TRANSACTION_fixupAppData = 11;
        public static final int TRANSACTION_freeCache = 32;
        public static final int TRANSACTION_getAppCrates = 15;
        public static final int TRANSACTION_getAppSize = 12;
        public static final int TRANSACTION_getExternalSize = 14;
        public static final int TRANSACTION_getOdexVisibility = 51;
        public static final int TRANSACTION_getUserCrates = 16;
        public static final int TRANSACTION_getUserSize = 13;
        public static final int TRANSACTION_hashSecondaryDexFile = 39;
        public static final int TRANSACTION_invalidateMounts = 40;
        public static final int TRANSACTION_isQuotaSupported = 41;
        public static final int TRANSACTION_linkFile = 35;
        public static final int TRANSACTION_linkNativeLibraryDirectory = 33;
        public static final int TRANSACTION_mergeProfiles = 23;
        public static final int TRANSACTION_migrateAppData = 8;
        public static final int TRANSACTION_migrateLegacyObbData = 49;
        public static final int TRANSACTION_moveAb = 36;
        public static final int TRANSACTION_moveCompleteApp = 18;
        public static final int TRANSACTION_onPrivateVolumeRemoved = 48;
        public static final int TRANSACTION_prepareAppProfile = 42;
        public static final int TRANSACTION_reconcileSdkData = 6;
        public static final int TRANSACTION_reconcileSecondaryDexFile = 38;
        public static final int TRANSACTION_restoreAppDataSnapshot = 44;
        public static final int TRANSACTION_restoreconAppData = 7;
        public static final int TRANSACTION_rmPackageDir = 31;
        public static final int TRANSACTION_rmdex = 22;
        public static final int TRANSACTION_setAppQuota = 17;
        public static final int TRANSACTION_setFirstBoot = 3;
        public static final int TRANSACTION_snapshotAppData = 43;
        public static final int TRANSACTION_tryMountDataMirror = 47;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInstalld asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IInstalld)) {
                return (IInstalld) queryLocalInterface;
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
                    String readString = parcel.readString();
                    int readInt = parcel.readInt();
                    int readInt2 = parcel.readInt();
                    int readInt3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    createUserData(readString, readInt, readInt2, readInt3);
                    parcel2.writeNoException();
                    return true;
                case 2:
                    String readString2 = parcel.readString();
                    int readInt4 = parcel.readInt();
                    int readInt5 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    destroyUserData(readString2, readInt4, readInt5);
                    parcel2.writeNoException();
                    return true;
                case 3:
                    setFirstBoot();
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceNoDataAvail();
                    CreateAppDataResult createAppData = createAppData((CreateAppDataArgs) parcel.readTypedObject(CreateAppDataArgs.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeTypedObject(createAppData, 1);
                    return true;
                case 5:
                    parcel.enforceNoDataAvail();
                    CreateAppDataResult[] createAppDataBatched = createAppDataBatched((CreateAppDataArgs[]) parcel.createTypedArray(CreateAppDataArgs.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(createAppDataBatched, 1);
                    return true;
                case 6:
                    parcel.enforceNoDataAvail();
                    reconcileSdkData((ReconcileSdkDataArgs) parcel.readTypedObject(ReconcileSdkDataArgs.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 7:
                    String readString3 = parcel.readString();
                    String readString4 = parcel.readString();
                    int readInt6 = parcel.readInt();
                    int readInt7 = parcel.readInt();
                    int readInt8 = parcel.readInt();
                    String readString5 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    restoreconAppData(readString3, readString4, readInt6, readInt7, readInt8, readString5);
                    parcel2.writeNoException();
                    return true;
                case 8:
                    String readString6 = parcel.readString();
                    String readString7 = parcel.readString();
                    int readInt9 = parcel.readInt();
                    int readInt10 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    migrateAppData(readString6, readString7, readInt9, readInt10);
                    parcel2.writeNoException();
                    return true;
                case 9:
                    String readString8 = parcel.readString();
                    String readString9 = parcel.readString();
                    int readInt11 = parcel.readInt();
                    int readInt12 = parcel.readInt();
                    long readLong = parcel.readLong();
                    parcel.enforceNoDataAvail();
                    clearAppData(readString8, readString9, readInt11, readInt12, readLong);
                    parcel2.writeNoException();
                    return true;
                case 10:
                    String readString10 = parcel.readString();
                    String readString11 = parcel.readString();
                    int readInt13 = parcel.readInt();
                    int readInt14 = parcel.readInt();
                    long readLong2 = parcel.readLong();
                    parcel.enforceNoDataAvail();
                    destroyAppData(readString10, readString11, readInt13, readInt14, readLong2);
                    parcel2.writeNoException();
                    return true;
                case 11:
                    String readString12 = parcel.readString();
                    int readInt15 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    fixupAppData(readString12, readInt15);
                    parcel2.writeNoException();
                    return true;
                case 12:
                    String readString13 = parcel.readString();
                    String[] createStringArray = parcel.createStringArray();
                    int readInt16 = parcel.readInt();
                    int readInt17 = parcel.readInt();
                    int readInt18 = parcel.readInt();
                    long[] createLongArray = parcel.createLongArray();
                    String[] createStringArray2 = parcel.createStringArray();
                    parcel.enforceNoDataAvail();
                    long[] appSize = getAppSize(readString13, createStringArray, readInt16, readInt17, readInt18, createLongArray, createStringArray2);
                    parcel2.writeNoException();
                    parcel2.writeLongArray(appSize);
                    return true;
                case 13:
                    String readString14 = parcel.readString();
                    int readInt19 = parcel.readInt();
                    int readInt20 = parcel.readInt();
                    int[] createIntArray = parcel.createIntArray();
                    parcel.enforceNoDataAvail();
                    long[] userSize = getUserSize(readString14, readInt19, readInt20, createIntArray);
                    parcel2.writeNoException();
                    parcel2.writeLongArray(userSize);
                    return true;
                case 14:
                    String readString15 = parcel.readString();
                    int readInt21 = parcel.readInt();
                    int readInt22 = parcel.readInt();
                    int[] createIntArray2 = parcel.createIntArray();
                    parcel.enforceNoDataAvail();
                    long[] externalSize = getExternalSize(readString15, readInt21, readInt22, createIntArray2);
                    parcel2.writeNoException();
                    parcel2.writeLongArray(externalSize);
                    return true;
                case 15:
                    String readString16 = parcel.readString();
                    String[] createStringArray3 = parcel.createStringArray();
                    int readInt23 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    CrateMetadata[] appCrates = getAppCrates(readString16, createStringArray3, readInt23);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(appCrates, 1);
                    return true;
                case 16:
                    String readString17 = parcel.readString();
                    int readInt24 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    CrateMetadata[] userCrates = getUserCrates(readString17, readInt24);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(userCrates, 1);
                    return true;
                case 17:
                    String readString18 = parcel.readString();
                    int readInt25 = parcel.readInt();
                    int readInt26 = parcel.readInt();
                    long readLong3 = parcel.readLong();
                    parcel.enforceNoDataAvail();
                    setAppQuota(readString18, readInt25, readInt26, readLong3);
                    parcel2.writeNoException();
                    return true;
                case 18:
                    String readString19 = parcel.readString();
                    String readString20 = parcel.readString();
                    String readString21 = parcel.readString();
                    int readInt27 = parcel.readInt();
                    String readString22 = parcel.readString();
                    int readInt28 = parcel.readInt();
                    String readString23 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    moveCompleteApp(readString19, readString20, readString21, readInt27, readString22, readInt28, readString23);
                    parcel2.writeNoException();
                    return true;
                case 19:
                    String readString24 = parcel.readString();
                    int readInt29 = parcel.readInt();
                    String readString25 = parcel.readString();
                    String readString26 = parcel.readString();
                    int readInt30 = parcel.readInt();
                    String readString27 = parcel.readString();
                    int readInt31 = parcel.readInt();
                    String readString28 = parcel.readString();
                    String readString29 = parcel.readString();
                    String readString30 = parcel.readString();
                    String readString31 = parcel.readString();
                    boolean readBoolean = parcel.readBoolean();
                    int readInt32 = parcel.readInt();
                    String readString32 = parcel.readString();
                    String readString33 = parcel.readString();
                    String readString34 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    boolean dexopt = dexopt(readString24, readInt29, readString25, readString26, readInt30, readString27, readInt31, readString28, readString29, readString30, readString31, readBoolean, readInt32, readString32, readString33, readString34);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(dexopt);
                    return true;
                case 20:
                    boolean readBoolean2 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    controlDexOptBlocking(readBoolean2);
                    parcel2.writeNoException();
                    return true;
                case 21:
                    String readString35 = parcel.readString();
                    String readString36 = parcel.readString();
                    String readString37 = parcel.readString();
                    int readInt33 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    boolean compileLayouts = compileLayouts(readString35, readString36, readString37, readInt33);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(compileLayouts);
                    return true;
                case 22:
                    String readString38 = parcel.readString();
                    String readString39 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    rmdex(readString38, readString39);
                    parcel2.writeNoException();
                    return true;
                case 23:
                    int readInt34 = parcel.readInt();
                    String readString40 = parcel.readString();
                    String readString41 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    int mergeProfiles = mergeProfiles(readInt34, readString40, readString41);
                    parcel2.writeNoException();
                    parcel2.writeInt(mergeProfiles);
                    return true;
                case 24:
                    int readInt35 = parcel.readInt();
                    String readString42 = parcel.readString();
                    String readString43 = parcel.readString();
                    String readString44 = parcel.readString();
                    boolean readBoolean3 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    boolean dumpProfiles = dumpProfiles(readInt35, readString42, readString43, readString44, readBoolean3);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(dumpProfiles);
                    return true;
                case 25:
                    String readString45 = parcel.readString();
                    int readInt36 = parcel.readInt();
                    String readString46 = parcel.readString();
                    String readString47 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    boolean copySystemProfile = copySystemProfile(readString45, readInt36, readString46, readString47);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(copySystemProfile);
                    return true;
                case 26:
                    String readString48 = parcel.readString();
                    String readString49 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    clearAppProfiles(readString48, readString49);
                    parcel2.writeNoException();
                    return true;
                case 27:
                    String readString50 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    destroyAppProfiles(readString50);
                    parcel2.writeNoException();
                    return true;
                case 28:
                    String readString51 = parcel.readString();
                    String readString52 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    deleteReferenceProfile(readString51, readString52);
                    parcel2.writeNoException();
                    return true;
                case 29:
                    int readInt37 = parcel.readInt();
                    String readString53 = parcel.readString();
                    String readString54 = parcel.readString();
                    String readString55 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    boolean createProfileSnapshot = createProfileSnapshot(readInt37, readString53, readString54, readString55);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(createProfileSnapshot);
                    return true;
                case 30:
                    String readString56 = parcel.readString();
                    String readString57 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    destroyProfileSnapshot(readString56, readString57);
                    parcel2.writeNoException();
                    return true;
                case 31:
                    String readString58 = parcel.readString();
                    String readString59 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    rmPackageDir(readString58, readString59);
                    parcel2.writeNoException();
                    return true;
                case 32:
                    String readString60 = parcel.readString();
                    long readLong4 = parcel.readLong();
                    int readInt38 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    freeCache(readString60, readLong4, readInt38);
                    parcel2.writeNoException();
                    return true;
                case 33:
                    String readString61 = parcel.readString();
                    String readString62 = parcel.readString();
                    String readString63 = parcel.readString();
                    int readInt39 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    linkNativeLibraryDirectory(readString61, readString62, readString63, readInt39);
                    parcel2.writeNoException();
                    return true;
                case 34:
                    String readString64 = parcel.readString();
                    String readString65 = parcel.readString();
                    String readString66 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    createOatDir(readString64, readString65, readString66);
                    parcel2.writeNoException();
                    return true;
                case 35:
                    String readString67 = parcel.readString();
                    String readString68 = parcel.readString();
                    String readString69 = parcel.readString();
                    String readString70 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    linkFile(readString67, readString68, readString69, readString70);
                    parcel2.writeNoException();
                    return true;
                case 36:
                    String readString71 = parcel.readString();
                    String readString72 = parcel.readString();
                    String readString73 = parcel.readString();
                    String readString74 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    moveAb(readString71, readString72, readString73, readString74);
                    parcel2.writeNoException();
                    return true;
                case 37:
                    String readString75 = parcel.readString();
                    String readString76 = parcel.readString();
                    String readString77 = parcel.readString();
                    String readString78 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    long deleteOdex = deleteOdex(readString75, readString76, readString77, readString78);
                    parcel2.writeNoException();
                    parcel2.writeLong(deleteOdex);
                    return true;
                case 38:
                    String readString79 = parcel.readString();
                    String readString80 = parcel.readString();
                    int readInt40 = parcel.readInt();
                    String[] createStringArray4 = parcel.createStringArray();
                    String readString81 = parcel.readString();
                    int readInt41 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    boolean reconcileSecondaryDexFile = reconcileSecondaryDexFile(readString79, readString80, readInt40, createStringArray4, readString81, readInt41);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(reconcileSecondaryDexFile);
                    return true;
                case 39:
                    String readString82 = parcel.readString();
                    String readString83 = parcel.readString();
                    int readInt42 = parcel.readInt();
                    String readString84 = parcel.readString();
                    int readInt43 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    byte[] hashSecondaryDexFile = hashSecondaryDexFile(readString82, readString83, readInt42, readString84, readInt43);
                    parcel2.writeNoException();
                    parcel2.writeByteArray(hashSecondaryDexFile);
                    return true;
                case 40:
                    invalidateMounts();
                    parcel2.writeNoException();
                    return true;
                case 41:
                    String readString85 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    boolean isQuotaSupported = isQuotaSupported(readString85);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(isQuotaSupported);
                    return true;
                case 42:
                    String readString86 = parcel.readString();
                    int readInt44 = parcel.readInt();
                    int readInt45 = parcel.readInt();
                    String readString87 = parcel.readString();
                    String readString88 = parcel.readString();
                    String readString89 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    boolean prepareAppProfile = prepareAppProfile(readString86, readInt44, readInt45, readString87, readString88, readString89);
                    parcel2.writeNoException();
                    parcel2.writeBoolean(prepareAppProfile);
                    return true;
                case 43:
                    String readString90 = parcel.readString();
                    String readString91 = parcel.readString();
                    int readInt46 = parcel.readInt();
                    int readInt47 = parcel.readInt();
                    int readInt48 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    long snapshotAppData = snapshotAppData(readString90, readString91, readInt46, readInt47, readInt48);
                    parcel2.writeNoException();
                    parcel2.writeLong(snapshotAppData);
                    return true;
                case 44:
                    String readString92 = parcel.readString();
                    String readString93 = parcel.readString();
                    int readInt49 = parcel.readInt();
                    String readString94 = parcel.readString();
                    int readInt50 = parcel.readInt();
                    int readInt51 = parcel.readInt();
                    int readInt52 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    restoreAppDataSnapshot(readString92, readString93, readInt49, readString94, readInt50, readInt51, readInt52);
                    parcel2.writeNoException();
                    return true;
                case 45:
                    String readString95 = parcel.readString();
                    String readString96 = parcel.readString();
                    int readInt53 = parcel.readInt();
                    long readLong5 = parcel.readLong();
                    int readInt54 = parcel.readInt();
                    int readInt55 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    destroyAppDataSnapshot(readString95, readString96, readInt53, readLong5, readInt54, readInt55);
                    parcel2.writeNoException();
                    return true;
                case 46:
                    String readString97 = parcel.readString();
                    int readInt56 = parcel.readInt();
                    int[] createIntArray3 = parcel.createIntArray();
                    parcel.enforceNoDataAvail();
                    destroyCeSnapshotsNotSpecified(readString97, readInt56, createIntArray3);
                    parcel2.writeNoException();
                    return true;
                case 47:
                    String readString98 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    tryMountDataMirror(readString98);
                    parcel2.writeNoException();
                    return true;
                case 48:
                    String readString99 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    onPrivateVolumeRemoved(readString99);
                    parcel2.writeNoException();
                    return true;
                case 49:
                    migrateLegacyObbData();
                    parcel2.writeNoException();
                    return true;
                case 50:
                    String readString100 = parcel.readString();
                    int readInt57 = parcel.readInt();
                    int readInt58 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    cleanupInvalidPackageDirs(readString100, readInt57, readInt58);
                    parcel2.writeNoException();
                    return true;
                case 51:
                    String readString101 = parcel.readString();
                    String readString102 = parcel.readString();
                    String readString103 = parcel.readString();
                    String readString104 = parcel.readString();
                    parcel.enforceNoDataAvail();
                    int odexVisibility = getOdexVisibility(readString101, readString102, readString103, readString104);
                    parcel2.writeNoException();
                    parcel2.writeInt(odexVisibility);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* renamed from: android.os.IInstalld$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IInstalld {
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

            @Override // android.p005os.IInstalld
            public void createUserData(String str, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyUserData(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void setFirstBoot() throws RemoteException {
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

            @Override // android.p005os.IInstalld
            public CreateAppDataResult createAppData(CreateAppDataArgs createAppDataArgs) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(createAppDataArgs, 0);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return (CreateAppDataResult) obtain2.readTypedObject(CreateAppDataResult.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public CreateAppDataResult[] createAppDataBatched(CreateAppDataArgs[] createAppDataArgsArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedArray(createAppDataArgsArr, 0);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return (CreateAppDataResult[]) obtain2.createTypedArray(CreateAppDataResult.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void reconcileSdkData(ReconcileSdkDataArgs reconcileSdkDataArgs) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedObject(reconcileSdkDataArgs, 0);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void restoreconAppData(String str, String str2, int i, int i2, int i3, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeString(str3);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void migrateAppData(String str, String str2, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void clearAppData(String str, String str2, int i, int i2, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeLong(j);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyAppData(String str, String str2, int i, int i2, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeLong(j);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void fixupAppData(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public long[] getAppSize(String str, String[] strArr, int i, int i2, int i3, long[] jArr, String[] strArr2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStringArray(strArr);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeLongArray(jArr);
                    obtain.writeStringArray(strArr2);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createLongArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public long[] getUserSize(String str, int i, int i2, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createLongArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public long[] getExternalSize(String str, int i, int i2, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createLongArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public CrateMetadata[] getAppCrates(String str, String[] strArr, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStringArray(strArr);
                    obtain.writeInt(i);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return (CrateMetadata[]) obtain2.createTypedArray(CrateMetadata.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public CrateMetadata[] getUserCrates(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return (CrateMetadata[]) obtain2.createTypedArray(CrateMetadata.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void setAppQuota(String str, int i, int i2, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeLong(j);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void moveCompleteApp(String str, String str2, String str3, int i, String str4, int i2, String str5) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeString(str4);
                    obtain.writeInt(i2);
                    obtain.writeString(str5);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean dexopt(String str, int i, String str2, String str3, int i2, String str4, int i3, String str5, String str6, String str7, String str8, boolean z, int i4, String str9, String str10, String str11) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    obtain.writeString(str4);
                    obtain.writeInt(i3);
                    obtain.writeString(str5);
                    obtain.writeString(str6);
                    obtain.writeString(str7);
                    obtain.writeString(str8);
                    obtain.writeBoolean(z);
                    obtain.writeInt(i4);
                    obtain.writeString(str9);
                    obtain.writeString(str10);
                    obtain.writeString(str11);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void controlDexOptBlocking(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean compileLayouts(String str, String str2, String str3, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void rmdex(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public int mergeProfiles(int i, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean dumpProfiles(int i, String str, String str2, String str3, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeBoolean(z);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean copySystemProfile(String str, int i, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void clearAppProfiles(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyAppProfiles(String str) throws RemoteException {
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

            @Override // android.p005os.IInstalld
            public void deleteReferenceProfile(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean createProfileSnapshot(int i, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyProfileSnapshot(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(30, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void rmPackageDir(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(31, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void freeCache(String str, long j, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeLong(j);
                    obtain.writeInt(i);
                    this.mRemote.transact(32, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void linkNativeLibraryDirectory(String str, String str2, String str3, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    this.mRemote.transact(33, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void createOatDir(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(34, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void linkFile(String str, String str2, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(35, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void moveAb(String str, String str2, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(36, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public long deleteOdex(String str, String str2, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(37, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean reconcileSecondaryDexFile(String str, String str2, int i, String[] strArr, String str3, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeStringArray(strArr);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    this.mRemote.transact(38, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public byte[] hashSecondaryDexFile(String str, String str2, int i, String str3, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    this.mRemote.transact(39, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createByteArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void invalidateMounts() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean isQuotaSupported(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(41, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public boolean prepareAppProfile(String str, int i, int i2, String str2, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(42, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public long snapshotAppData(String str, String str2, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(43, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void restoreAppDataSnapshot(String str, String str2, int i, String str3, int i2, int i3, int i4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeString(str3);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    this.mRemote.transact(44, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyAppDataSnapshot(String str, String str2, int i, long j, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeLong(j);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(45, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void destroyCeSnapshotsNotSpecified(String str, int i, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void tryMountDataMirror(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(47, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void onPrivateVolumeRemoved(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(48, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void migrateLegacyObbData() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public void cleanupInvalidPackageDirs(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(50, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.p005os.IInstalld
            public int getOdexVisibility(String str, String str2, String str3, String str4) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    this.mRemote.transact(51, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
