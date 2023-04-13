package com.android.server.p011pm;

import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Trace;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageManagerInternal;
import android.os.storage.VolumeInfo;
import android.p005os.CreateAppDataArgs;
import android.security.AndroidKeyStoreMaintenance;
import android.text.TextUtils;
import android.util.Slog;
import android.util.TimingsTraceLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.dex.ArtManagerService;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.SELinuxUtil;
import dalvik.system.VMRuntime;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.pm.AppDataHelper */
/* loaded from: classes2.dex */
public class AppDataHelper {
    public final ArtManagerService mArtManagerService;
    public final PackageManagerServiceInjector mInjector;
    public final Installer mInstaller;
    public final PackageManagerService mPm;

    public AppDataHelper(PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
        PackageManagerServiceInjector packageManagerServiceInjector = packageManagerService.mInjector;
        this.mInjector = packageManagerServiceInjector;
        this.mInstaller = packageManagerServiceInjector.getInstaller();
        this.mArtManagerService = packageManagerServiceInjector.getArtManagerService();
    }

    @GuardedBy({"mPm.mInstallLock"})
    public void prepareAppDataAfterInstallLIF(AndroidPackage androidPackage) {
        prepareAppDataPostCommitLIF(androidPackage, 0);
    }

    @GuardedBy({"mPm.mInstallLock"})
    public void prepareAppDataPostCommitLIF(final AndroidPackage androidPackage, int i) {
        PackageSetting packageLPr;
        int i2;
        synchronized (this.mPm.mLock) {
            packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
            this.mPm.mSettings.writeKernelMappingLPr(packageLPr);
        }
        if (!shouldHaveAppStorage(androidPackage)) {
            Slog.w("PackageManager", "Skipping preparing app data for " + androidPackage.getPackageName());
            return;
        }
        Installer.Batch batch = new Installer.Batch();
        final UserManagerInternal userManagerInternal = this.mInjector.getUserManagerInternal();
        final StorageManagerInternal storageManagerInternal = (StorageManagerInternal) this.mInjector.getLocalService(StorageManagerInternal.class);
        for (final UserInfo userInfo : userManagerInternal.getUsers(false)) {
            if (StorageManager.isUserKeyUnlocked(userInfo.id) && storageManagerInternal.isCeStoragePrepared(userInfo.id)) {
                i2 = 3;
            } else if (userManagerInternal.isUserRunning(userInfo.id)) {
                i2 = 1;
            }
            int i3 = i2;
            if (packageLPr.getInstalled(userInfo.id)) {
                prepareAppData(batch, androidPackage, i, userInfo.id, i3).thenRun(new Runnable() { // from class: com.android.server.pm.AppDataHelper$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppDataHelper.lambda$prepareAppDataPostCommitLIF$0(UserManagerInternal.this, userInfo, androidPackage, storageManagerInternal);
                    }
                });
            }
        }
        executeBatchLI(batch);
    }

    public static /* synthetic */ void lambda$prepareAppDataPostCommitLIF$0(UserManagerInternal userManagerInternal, UserInfo userInfo, AndroidPackage androidPackage, StorageManagerInternal storageManagerInternal) {
        if (userManagerInternal.isUserUnlockingOrUnlocked(userInfo.id)) {
            storageManagerInternal.prepareAppDataAfterInstall(androidPackage.getPackageName(), UserHandle.getUid(userInfo.id, UserHandle.getAppId(androidPackage.getUid())));
        }
    }

    public final void executeBatchLI(Installer.Batch batch) {
        try {
            batch.execute(this.mInstaller);
        } catch (Installer.InstallerException e) {
            Slog.w("PackageManager", "Failed to execute pending operations", e);
        }
    }

    public final CompletableFuture<?> prepareAppData(Installer.Batch batch, AndroidPackage androidPackage, int i, int i2, int i3) {
        if (androidPackage == null) {
            Slog.wtf("PackageManager", "Package was null!", new Throwable());
            return CompletableFuture.completedFuture(null);
        } else if (!shouldHaveAppStorage(androidPackage)) {
            Slog.w("PackageManager", "Skipping preparing app data for " + androidPackage.getPackageName());
            return CompletableFuture.completedFuture(null);
        } else {
            return prepareAppDataLeaf(batch, androidPackage, i, i2, i3);
        }
    }

    public final void prepareAppDataAndMigrate(Installer.Batch batch, final PackageState packageState, final AndroidPackage androidPackage, final int i, final int i2, final boolean z) {
        prepareAppData(batch, androidPackage, -1, i, i2).thenRun(new Runnable() { // from class: com.android.server.pm.AppDataHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AppDataHelper.this.lambda$prepareAppDataAndMigrate$1(z, packageState, androidPackage, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareAppDataAndMigrate$1(boolean z, PackageState packageState, AndroidPackage androidPackage, int i, int i2) {
        if (z && maybeMigrateAppDataLIF(packageState, androidPackage, i)) {
            Installer.Batch batch = new Installer.Batch();
            prepareAppData(batch, androidPackage, -1, i, i2);
            executeBatchLI(batch);
        }
    }

    public final CompletableFuture<?> prepareAppDataLeaf(Installer.Batch batch, final AndroidPackage androidPackage, int i, final int i2, final int i3) {
        final PackageSetting packageLPr;
        String seinfoUser;
        synchronized (this.mPm.mLock) {
            packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
            seinfoUser = SELinuxUtil.getSeinfoUser(packageLPr.readUserState(i2));
        }
        String volumeUuid = androidPackage.getVolumeUuid();
        final String packageName = androidPackage.getPackageName();
        int appId = UserHandle.getAppId(androidPackage.getUid());
        String seInfo = packageLPr.getSeInfo();
        Preconditions.checkNotNull(seInfo);
        final CreateAppDataArgs buildCreateAppDataArgs = Installer.buildCreateAppDataArgs(volumeUuid, packageName, i2, i3, appId, seInfo + seinfoUser, androidPackage.getTargetSdkVersion(), !androidPackage.getUsesSdkLibraries().isEmpty());
        buildCreateAppDataArgs.previousAppId = i;
        return batch.createAppData(buildCreateAppDataArgs).whenComplete(new BiConsumer() { // from class: com.android.server.pm.AppDataHelper$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AppDataHelper.this.lambda$prepareAppDataLeaf$2(packageName, androidPackage, i2, i3, buildCreateAppDataArgs, packageLPr, (Long) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareAppDataLeaf$2(String str, AndroidPackage androidPackage, int i, int i2, CreateAppDataArgs createAppDataArgs, PackageSetting packageSetting, Long l, Throwable th) {
        if (th != null) {
            PackageManagerServiceUtils.logCriticalInfo(5, "Failed to create app data for " + str + ", but trying to recover: " + th);
            destroyAppDataLeafLIF(androidPackage, i, i2);
            try {
                l = Long.valueOf(this.mInstaller.createAppData(createAppDataArgs).ceDataInode);
                PackageManagerServiceUtils.logCriticalInfo(3, "Recovery succeeded!");
            } catch (Installer.InstallerException unused) {
                PackageManagerServiceUtils.logCriticalInfo(3, "Recovery failed!");
            }
        }
        if (!DexOptHelper.useArtService() && (this.mPm.isDeviceUpgrading() || this.mPm.isFirstBoot() || i != 0)) {
            try {
                this.mArtManagerService.prepareAppProfiles(androidPackage, i, false);
            } catch (Installer.LegacyDexoptDisabledException e) {
                throw new RuntimeException(e);
            }
        }
        if ((i2 & 2) != 0 && l.longValue() != -1) {
            synchronized (this.mPm.mLock) {
                packageSetting.setCeDataInode(l.longValue(), i);
            }
        }
        prepareAppDataContentsLeafLIF(androidPackage, packageSetting, i, i2);
    }

    public void prepareAppDataContentsLIF(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, int i, int i2) {
        if (androidPackage == null) {
            Slog.wtf("PackageManager", "Package was null!", new Throwable());
        } else {
            prepareAppDataContentsLeafLIF(androidPackage, packageStateInternal, i, i2);
        }
    }

    public final void prepareAppDataContentsLeafLIF(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, int i, int i2) {
        String volumeUuid = androidPackage.getVolumeUuid();
        String packageName = androidPackage.getPackageName();
        if ((i2 & 2) != 0) {
            String rawPrimaryCpuAbi = packageStateInternal == null ? AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackage) : packageStateInternal.getPrimaryCpuAbi();
            if (rawPrimaryCpuAbi == null || VMRuntime.is64BitAbi(rawPrimaryCpuAbi)) {
                return;
            }
            String nativeLibraryDir = androidPackage.getNativeLibraryDir();
            if (new File(nativeLibraryDir).exists()) {
                try {
                    this.mInstaller.linkNativeLibraryDirectory(volumeUuid, packageName, nativeLibraryDir, i);
                } catch (Installer.InstallerException e) {
                    Slog.e("PackageManager", "Failed to link native for " + packageName + ": " + e);
                }
            }
        }
    }

    public final boolean maybeMigrateAppDataLIF(PackageState packageState, AndroidPackage androidPackage, int i) {
        if (!packageState.isSystem() || StorageManager.isFileEncrypted()) {
            return false;
        }
        try {
            this.mInstaller.migrateAppData(androidPackage.getVolumeUuid(), androidPackage.getPackageName(), i, androidPackage.isDefaultToDeviceProtectedStorage() ? 1 : 2);
        } catch (Installer.InstallerException e) {
            PackageManagerServiceUtils.logCriticalInfo(5, "Failed to migrate " + androidPackage.getPackageName() + ": " + e.getMessage());
        }
        return true;
    }

    public void reconcileAppsData(int i, int i2, boolean z) {
        for (VolumeInfo volumeInfo : ((StorageManager) this.mInjector.getSystemService(StorageManager.class)).getWritablePrivateVolumes()) {
            String fsUuid = volumeInfo.getFsUuid();
            synchronized (this.mPm.mInstallLock) {
                reconcileAppsDataLI(fsUuid, i, i2, z);
            }
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public void reconcileAppsDataLI(String str, int i, int i2, boolean z) {
        reconcileAppsDataLI(str, i, i2, z, false);
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final List<String> reconcileAppsDataLI(String str, int i, int i2, boolean z, boolean z2) {
        String str2;
        ArrayList arrayList;
        ArrayList arrayList2;
        String str3;
        int i3;
        int i4;
        String str4;
        String str5;
        String str6;
        ArrayList arrayList3;
        int i5;
        int i6;
        Computer computer;
        String str7;
        String str8 = "PackageManager";
        Slog.v("PackageManager", "reconcileAppsData for " + str + " u" + i + " 0x" + Integer.toHexString(i2) + " migrateAppData=" + z);
        ArrayList arrayList4 = z2 ? new ArrayList() : null;
        try {
            this.mInstaller.cleanupInvalidPackageDirs(str, i, i2);
        } catch (Installer.InstallerException e) {
            PackageManagerServiceUtils.logCriticalInfo(5, "Failed to cleanup deleted dirs: " + e);
        }
        File dataUserCeDirectory = Environment.getDataUserCeDirectory(str, i);
        File dataUserDeDirectory = Environment.getDataUserDeDirectory(str, i);
        Computer snapshotComputer = this.mPm.snapshotComputer();
        String str9 = "Failed to destroy: ";
        String str10 = " due to: ";
        String str11 = "Destroying ";
        if ((i2 & 2) != 0) {
            if (StorageManager.isFileEncrypted() && !StorageManager.isUserKeyUnlocked(i)) {
                throw new RuntimeException("Yikes, someone asked us to reconcile CE storage while " + i + " was still locked; this would have caused massive data loss!");
            }
            File[] listFilesOrEmpty = FileUtils.listFilesOrEmpty(dataUserCeDirectory);
            int length = listFilesOrEmpty.length;
            int i7 = 0;
            while (i7 < length) {
                File file = listFilesOrEmpty[i7];
                File[] fileArr = listFilesOrEmpty;
                String name = file.getName();
                try {
                    assertPackageStorageValid(snapshotComputer, str, name, i);
                    i5 = i7;
                    i6 = length;
                    str4 = str11;
                    str5 = str10;
                    str6 = str8;
                    arrayList3 = arrayList4;
                    computer = snapshotComputer;
                    str7 = str9;
                } catch (PackageManagerException e2) {
                    int i8 = i7;
                    PackageManagerServiceUtils.logCriticalInfo(5, str11 + file + str10 + e2);
                    try {
                        i5 = i8;
                        i6 = length;
                        str4 = str11;
                        str5 = str10;
                        str6 = str8;
                        arrayList3 = arrayList4;
                        computer = snapshotComputer;
                        str7 = str9;
                    } catch (Installer.InstallerException e3) {
                        e = e3;
                        str4 = str11;
                        str5 = str10;
                        str6 = str8;
                        arrayList3 = arrayList4;
                        i5 = i8;
                        i6 = length;
                        computer = snapshotComputer;
                        str7 = str9;
                    }
                    try {
                        this.mInstaller.destroyAppData(str, name, i, 2, 0L);
                    } catch (Installer.InstallerException e4) {
                        e = e4;
                        PackageManagerServiceUtils.logCriticalInfo(5, str7 + e);
                        i7 = i5 + 1;
                        str10 = str5;
                        str9 = str7;
                        snapshotComputer = computer;
                        listFilesOrEmpty = fileArr;
                        length = i6;
                        str11 = str4;
                        arrayList4 = arrayList3;
                        str8 = str6;
                    }
                }
                i7 = i5 + 1;
                str10 = str5;
                str9 = str7;
                snapshotComputer = computer;
                listFilesOrEmpty = fileArr;
                length = i6;
                str11 = str4;
                arrayList4 = arrayList3;
                str8 = str6;
            }
        }
        String str12 = str11;
        String str13 = str10;
        String str14 = str8;
        ArrayList arrayList5 = arrayList4;
        Computer computer2 = snapshotComputer;
        String str15 = str9;
        if ((i2 & 1) != 0) {
            File[] listFilesOrEmpty2 = FileUtils.listFilesOrEmpty(dataUserDeDirectory);
            int i9 = 0;
            for (int length2 = listFilesOrEmpty2.length; i9 < length2; length2 = i3) {
                File file2 = listFilesOrEmpty2[i9];
                String name2 = file2.getName();
                try {
                    assertPackageStorageValid(computer2, str, name2, i);
                    i3 = length2;
                    i4 = i9;
                    str3 = str12;
                } catch (PackageManagerException e5) {
                    StringBuilder sb = new StringBuilder();
                    String str16 = str12;
                    sb.append(str16);
                    sb.append(file2);
                    sb.append(str13);
                    sb.append(e5);
                    PackageManagerServiceUtils.logCriticalInfo(5, sb.toString());
                    try {
                        str3 = str16;
                        i3 = length2;
                        i4 = i9;
                        try {
                            this.mInstaller.destroyAppData(str, name2, i, 1, 0L);
                        } catch (Installer.InstallerException e6) {
                            e = e6;
                            PackageManagerServiceUtils.logCriticalInfo(5, str15 + e);
                            i9 = i4 + 1;
                            str12 = str3;
                        }
                    } catch (Installer.InstallerException e7) {
                        e = e7;
                        str3 = str16;
                        i3 = length2;
                        i4 = i9;
                    }
                }
                i9 = i4 + 1;
                str12 = str3;
            }
        }
        Trace.traceBegin(262144L, "prepareAppDataAndMigrate");
        Installer.Batch batch = new Installer.Batch();
        int i10 = 0;
        for (PackageStateInternal packageStateInternal : computer2.getVolumePackages(str)) {
            String packageName = packageStateInternal.getPackageName();
            if (packageStateInternal.getPkg() == null) {
                str2 = str14;
                Slog.w(str2, "Odd, missing scanned package " + packageName);
                arrayList = arrayList5;
            } else {
                str2 = str14;
                if (z2 && !packageStateInternal.getPkg().isCoreApp()) {
                    arrayList = arrayList5;
                    arrayList.add(packageName);
                } else {
                    ArrayList arrayList6 = arrayList5;
                    if (packageStateInternal.getUserStateOrDefault(i).isInstalled()) {
                        arrayList2 = arrayList6;
                        prepareAppDataAndMigrate(batch, packageStateInternal, packageStateInternal.getPkg(), i, i2, z);
                        i10++;
                    } else {
                        arrayList2 = arrayList6;
                    }
                    str14 = str2;
                    arrayList5 = arrayList2;
                }
            }
            arrayList5 = arrayList;
            str14 = str2;
        }
        ArrayList arrayList7 = arrayList5;
        executeBatchLI(batch);
        Trace.traceEnd(262144L);
        Slog.v(str14, "reconcileAppsData finished " + i10 + " packages");
        return arrayList7;
    }

    public final void assertPackageStorageValid(Computer computer, String str, String str2, int i) throws PackageManagerException {
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str2);
        if (packageStateInternal == null) {
            throw PackageManagerException.ofInternalError("Package " + str2 + " is unknown", -7);
        } else if (!TextUtils.equals(str, packageStateInternal.getVolumeUuid())) {
            throw PackageManagerException.ofInternalError("Package " + str2 + " found on unknown volume " + str + "; expected volume " + packageStateInternal.getVolumeUuid(), -8);
        } else if (!packageStateInternal.getUserStateOrDefault(i).isInstalled()) {
            throw PackageManagerException.ofInternalError("Package " + str2 + " not installed for user " + i, -9);
        } else if (packageStateInternal.getPkg() == null || shouldHaveAppStorage(packageStateInternal.getPkg())) {
        } else {
            throw PackageManagerException.ofInternalError("Package " + str2 + " shouldn't have storage", -10);
        }
    }

    public Future<?> fixAppsDataOnBoot() {
        final int i = StorageManager.isFileEncrypted() ? 1 : 3;
        final List<String> reconcileAppsDataLI = reconcileAppsDataLI(StorageManager.UUID_PRIVATE_INTERNAL, 0, i, true, true);
        return SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.pm.AppDataHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AppDataHelper.this.lambda$fixAppsDataOnBoot$3(reconcileAppsDataLI, i);
            }
        }, "prepareAppData");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fixAppsDataOnBoot$3(List list, int i) {
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemServerTimingAsync", 262144L);
        timingsTraceLog.traceBegin("AppDataFixup");
        try {
            this.mInstaller.fixupAppData(StorageManager.UUID_PRIVATE_INTERNAL, 3);
        } catch (Installer.InstallerException e) {
            Slog.w("PackageManager", "Trouble fixing GIDs", e);
        }
        timingsTraceLog.traceEnd();
        timingsTraceLog.traceBegin("AppDataPrepare");
        if (list == null || list.isEmpty()) {
            return;
        }
        Installer.Batch batch = new Installer.Batch();
        Iterator it = list.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal((String) it.next());
            if (packageStateInternal != null && packageStateInternal.getUserStateOrDefault(0).isInstalled()) {
                prepareAppDataAndMigrate(batch, packageStateInternal, packageStateInternal.getPkg(), 0, i, true);
                i2++;
            }
        }
        synchronized (this.mPm.mInstallLock) {
            executeBatchLI(batch);
        }
        timingsTraceLog.traceEnd();
        Slog.i("PackageManager", "Deferred reconcileAppsData finished " + i2 + " packages");
    }

    public void clearAppDataLIF(AndroidPackage androidPackage, int i, int i2) {
        if (androidPackage == null) {
            return;
        }
        clearAppDataLeafLIF(androidPackage, i, i2);
        if ((131072 & i2) == 0) {
            clearAppProfilesLIF(androidPackage);
        }
    }

    public final void clearAppDataLeafLIF(AndroidPackage androidPackage, int i, int i2) {
        int[] resolveUserIds;
        PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(androidPackage.getPackageName());
        for (int i3 : this.mPm.resolveUserIds(i)) {
            try {
                this.mInstaller.clearAppData(androidPackage.getVolumeUuid(), androidPackage.getPackageName(), i3, i2, packageStateInternal != null ? packageStateInternal.getUserStateOrDefault(i3).getCeDataInode() : 0L);
            } catch (Installer.InstallerException e) {
                Slog.w("PackageManager", String.valueOf(e));
            }
        }
    }

    public void clearAppProfilesLIF(AndroidPackage androidPackage) {
        if (androidPackage == null) {
            Slog.wtf("PackageManager", "Package was null!", new Throwable());
        } else if (DexOptHelper.useArtService()) {
            destroyAppProfilesWithArtService(androidPackage);
        } else {
            try {
                this.mArtManagerService.clearAppProfiles(androidPackage);
            } catch (Installer.LegacyDexoptDisabledException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroyAppDataLIF(AndroidPackage androidPackage, int i, int i2) {
        if (androidPackage == null) {
            Slog.wtf("PackageManager", "Package was null!", new Throwable());
        } else {
            destroyAppDataLeafLIF(androidPackage, i, i2);
        }
    }

    public void destroyAppDataLeafLIF(AndroidPackage androidPackage, int i, int i2) {
        int[] resolveUserIds;
        PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(androidPackage.getPackageName());
        for (int i3 : this.mPm.resolveUserIds(i)) {
            try {
                this.mInstaller.destroyAppData(androidPackage.getVolumeUuid(), androidPackage.getPackageName(), i3, i2, packageStateInternal != null ? packageStateInternal.getUserStateOrDefault(i3).getCeDataInode() : 0L);
            } catch (Installer.InstallerException e) {
                Slog.w("PackageManager", String.valueOf(e));
            }
            this.mPm.getDexManager().notifyPackageDataDestroyed(androidPackage.getPackageName(), i);
            this.mPm.getDynamicCodeLogger().notifyPackageDataDestroyed(androidPackage.getPackageName(), i);
        }
    }

    public void destroyAppProfilesLIF(AndroidPackage androidPackage) {
        if (androidPackage == null) {
            Slog.wtf("PackageManager", "Package was null!", new Throwable());
        } else {
            destroyAppProfilesLeafLIF(androidPackage);
        }
    }

    public final void destroyAppProfilesLeafLIF(AndroidPackage androidPackage) {
        if (DexOptHelper.useArtService()) {
            destroyAppProfilesWithArtService(androidPackage);
            return;
        }
        try {
            this.mInstaller.destroyAppProfiles(androidPackage.getPackageName());
        } catch (Installer.InstallerException e) {
            Slog.w("PackageManager", String.valueOf(e));
        } catch (Installer.LegacyDexoptDisabledException e2) {
            throw new RuntimeException(e2);
        }
    }

    public final void destroyAppProfilesWithArtService(AndroidPackage androidPackage) {
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = PackageManagerServiceUtils.getPackageManagerLocal().withFilteredSnapshot();
        try {
            try {
                DexOptHelper.getArtManagerLocal().clearAppProfiles(withFilteredSnapshot, androidPackage.getPackageName());
            } catch (Throwable th) {
                if (withFilteredSnapshot != null) {
                    try {
                        withFilteredSnapshot.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IllegalArgumentException e) {
            Slog.w("PackageManager", e);
        }
        if (withFilteredSnapshot != null) {
            withFilteredSnapshot.close();
        }
    }

    public final boolean shouldHaveAppStorage(AndroidPackage androidPackage) {
        PackageManager.Property property = androidPackage.getProperties().get("android.internal.PROPERTY_NO_APP_DATA_STORAGE");
        return (property == null || !property.getBoolean()) && androidPackage.getUid() >= 0;
    }

    public void clearKeystoreData(int i, int i2) {
        int[] resolveUserIds;
        if (i2 < 0) {
            return;
        }
        int length = this.mPm.resolveUserIds(i).length;
        for (int i3 = 0; i3 < length; i3++) {
            AndroidKeyStoreMaintenance.clearNamespace(0, UserHandle.getUid(resolveUserIds[i3], i2));
        }
    }
}
