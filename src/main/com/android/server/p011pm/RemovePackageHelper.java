package com.android.server.p011pm;

import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.PackageLite;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.Environment;
import android.os.Trace;
import android.os.incremental.IncrementalManager;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.parsing.PackageCacher;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.File;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.server.pm.RemovePackageHelper */
/* loaded from: classes2.dex */
public final class RemovePackageHelper {
    public final AppDataHelper mAppDataHelper;
    public final IncrementalManager mIncrementalManager;
    public final Installer mInstaller;
    public final PermissionManagerServiceInternal mPermissionManager;
    public final PackageManagerService mPm;
    public final SharedLibrariesImpl mSharedLibraries;
    public final UserManagerInternal mUserManagerInternal;

    public RemovePackageHelper(PackageManagerService packageManagerService, AppDataHelper appDataHelper) {
        this.mPm = packageManagerService;
        this.mIncrementalManager = packageManagerService.mInjector.getIncrementalManager();
        this.mInstaller = packageManagerService.mInjector.getInstaller();
        this.mUserManagerInternal = packageManagerService.mInjector.getUserManagerInternal();
        this.mPermissionManager = packageManagerService.mInjector.getPermissionManagerServiceInternal();
        this.mSharedLibraries = packageManagerService.mInjector.getSharedLibrariesImpl();
        this.mAppDataHelper = appDataHelper;
    }

    public RemovePackageHelper(PackageManagerService packageManagerService) {
        this(packageManagerService, new AppDataHelper(packageManagerService));
    }

    public void removeCodePath(File file) {
        synchronized (this.mPm.mInstallLock) {
            removeCodePathLI(file);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void removeCodePathLI(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File parentFile = file.getParentFile();
            boolean startsWith = parentFile.getName().startsWith("~~");
            try {
                if (this.mIncrementalManager != null && IncrementalManager.isIncrementalPath(file.getAbsolutePath())) {
                    if (startsWith) {
                        this.mIncrementalManager.rmPackageDir(parentFile);
                    } else {
                        this.mIncrementalManager.rmPackageDir(file);
                    }
                }
                String name = file.getName();
                this.mInstaller.rmPackageDir(name, file.getAbsolutePath());
                if (startsWith) {
                    this.mInstaller.rmPackageDir(name, parentFile.getAbsolutePath());
                    removeCachedResult(parentFile);
                    return;
                }
                return;
            } catch (Installer.InstallerException e) {
                Slog.w("PackageManager", "Failed to remove code path", e);
                return;
            }
        }
        file.delete();
    }

    public final void removeCachedResult(File file) {
        if (this.mPm.getCacheDir() == null) {
            return;
        }
        new PackageCacher(this.mPm.getCacheDir()).cleanCachedResult(file);
    }

    public void removePackage(AndroidPackage androidPackage, boolean z) {
        synchronized (this.mPm.mInstallLock) {
            removePackageLI(androidPackage, z);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void removePackageLI(AndroidPackage androidPackage, boolean z) {
        PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(androidPackage.getPackageName());
        if (packageStateInternal != null) {
            removePackageLI(packageStateInternal.getPackageName(), z);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void removePackageLI(String str, boolean z) {
        synchronized (this.mPm.mLock) {
            AndroidPackage remove = this.mPm.mPackages.remove(str);
            if (remove != null) {
                cleanPackageDataStructuresLILPw(remove, AndroidPackageUtils.isSystem(remove), z);
            }
        }
    }

    @GuardedBy({"mPm.mLock"})
    public final void cleanPackageDataStructuresLILPw(AndroidPackage androidPackage, boolean z, boolean z2) {
        this.mPm.mComponentResolver.removeAllComponents(androidPackage, z2);
        this.mPermissionManager.onPackageRemoved(androidPackage);
        this.mPm.getPackageProperty().removeAllProperties(androidPackage);
        int size = ArrayUtils.size(androidPackage.getInstrumentations());
        for (int i = 0; i < size; i++) {
            this.mPm.getInstrumentation().remove(androidPackage.getInstrumentations().get(i).getComponentName());
        }
        if (z) {
            int size2 = androidPackage.getLibraryNames().size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mSharedLibraries.removeSharedLibrary(androidPackage.getLibraryNames().get(i2), 0L);
            }
        }
        if (androidPackage.getSdkLibraryName() != null) {
            this.mSharedLibraries.removeSharedLibrary(androidPackage.getSdkLibraryName(), androidPackage.getSdkLibVersionMajor());
        }
        if (androidPackage.getStaticSharedLibraryName() != null) {
            this.mSharedLibraries.removeSharedLibrary(androidPackage.getStaticSharedLibraryName(), androidPackage.getStaticSharedLibraryVersion());
        }
    }

    public void removePackageData(PackageSetting packageSetting, int[] iArr, PackageRemovedInfo packageRemovedInfo, int i, boolean z) {
        synchronized (this.mPm.mInstallLock) {
            removePackageDataLIF(packageSetting, iArr, packageRemovedInfo, i, z);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public void removePackageDataLIF(PackageSetting packageSetting, int[] iArr, PackageRemovedInfo packageRemovedInfo, int i, boolean z) {
        final int i2;
        int i3;
        String packageName = packageSetting.getPackageName();
        AndroidPackageInternal pkg = packageSetting.getPkg();
        boolean z2 = false;
        if (packageRemovedInfo != null) {
            packageRemovedInfo.mRemovedPackage = packageName;
            packageRemovedInfo.mInstallerPackageName = packageSetting.getInstallSource().mInstallerPackageName;
            packageRemovedInfo.mIsStaticSharedLib = (pkg == null || pkg.getStaticSharedLibraryName() == null) ? false : true;
            packageRemovedInfo.populateUsers(packageSetting.queryInstalledUsers(this.mUserManagerInternal.getUserIds(), true), packageSetting);
            packageRemovedInfo.mIsExternal = packageSetting.isExternalStorage();
            packageRemovedInfo.mRemovedPackageVersionCode = packageSetting.getVersionCode();
        }
        removePackageLI(packageSetting.getPackageName(), (i & Integer.MIN_VALUE) != 0);
        int i4 = i & 1;
        if (i4 == 0) {
            AndroidPackage buildFakeForDeletion = pkg != null ? pkg : PackageImpl.buildFakeForDeletion(packageSetting.getPackageName(), packageSetting.getVolumeUuid());
            this.mAppDataHelper.destroyAppDataLIF(buildFakeForDeletion, -1, 7);
            this.mAppDataHelper.destroyAppProfilesLIF(buildFakeForDeletion);
            if (packageRemovedInfo != null) {
                packageRemovedInfo.mDataRemoved = true;
            }
        }
        if (i4 == 0) {
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            synchronized (this.mPm.mLock) {
                this.mPm.mDomainVerificationManager.clearPackage(packageSetting.getPackageName());
                this.mPm.mSettings.getKeySetManagerService().removeAppKeySetDataLPw(packageName);
                Computer snapshotComputer = this.mPm.snapshotComputer();
                this.mPm.mAppsFilter.removePackage(snapshotComputer, snapshotComputer.getPackageStateInternal(packageName));
                int removePackageLPw = this.mPm.mSettings.removePackageLPw(packageName);
                if (packageRemovedInfo != null) {
                    packageRemovedInfo.mRemovedAppId = removePackageLPw;
                }
                if (this.mPm.mSettings.isDisabledSystemPackageLPr(packageName)) {
                    i3 = removePackageLPw;
                } else {
                    SharedUserSetting sharedUserSettingLPr = this.mPm.mSettings.getSharedUserSettingLPr(packageSetting);
                    i3 = removePackageLPw;
                    this.mPermissionManager.onPackageUninstalled(packageName, packageSetting.getAppId(), packageSetting, pkg, sharedUserSettingLPr != null ? sharedUserSettingLPr.getPackages() : Collections.emptyList(), -1);
                    if (sharedUserSettingLPr != null) {
                        this.mPm.mSettings.checkAndConvertSharedUserSettingsLPw(sharedUserSettingLPr);
                    }
                }
                this.mPm.clearPackagePreferredActivitiesLPw(packageSetting.getPackageName(), sparseBooleanArray, -1);
                this.mPm.mSettings.removeRenamedPackageLPw(packageSetting.getRealName());
            }
            if (sparseBooleanArray.size() > 0) {
                new PreferredActivityHelper(this.mPm).updateDefaultHomeNotLocked(this.mPm.snapshotComputer(), sparseBooleanArray);
                this.mPm.postPreferredActivityChangedBroadcast(-1);
            }
            i2 = i3;
        } else {
            i2 = -1;
        }
        if (packageRemovedInfo != null && packageRemovedInfo.mOrigUsers != null) {
            boolean z3 = false;
            for (int i5 : iArr) {
                boolean contains = ArrayUtils.contains(packageRemovedInfo.mOrigUsers, i5);
                if (contains != packageSetting.getInstalled(i5)) {
                    z3 = true;
                }
                packageSetting.setInstalled(contains, i5);
                if (contains) {
                    packageSetting.setUninstallReason(0, i5);
                }
            }
            z2 = z3;
        }
        synchronized (this.mPm.mLock) {
            if (z) {
                try {
                    this.mPm.writeSettingsLPrTEMP();
                } finally {
                }
            }
            if (z2) {
                this.mPm.mSettings.writeKernelMappingLPr(packageSetting);
            }
        }
        if (i2 != -1) {
            this.mPm.mInjector.getBackgroundHandler().post(new Runnable() { // from class: com.android.server.pm.RemovePackageHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemovePackageHelper.this.lambda$removePackageDataLIF$0(i2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removePackageDataLIF$0(int i) {
        try {
            Trace.traceBegin(262144L, "clearKeystoreData:" + i);
            this.mAppDataHelper.clearKeystoreData(-1, i);
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public void cleanUpResources(File file, String[] strArr) {
        synchronized (this.mPm.mInstallLock) {
            cleanUpResourcesLI(file, strArr);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void cleanUpResourcesLI(File file, String[] strArr) {
        List<String> list = Collections.EMPTY_LIST;
        if (file != null && file.exists()) {
            ParseResult parsePackageLite = ApkLiteParseUtils.parsePackageLite(ParseTypeImpl.forDefaultParsing().reset(), file, 0);
            if (parsePackageLite.isSuccess()) {
                list = ((PackageLite) parsePackageLite.getResult()).getAllApkPaths();
            }
        }
        removeCodePathLI(file);
        removeDexFilesLI(list, strArr);
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void removeDexFilesLI(List<String> list, String[] strArr) {
        if (list.isEmpty()) {
            return;
        }
        if (strArr == null) {
            throw new IllegalStateException("instructionSet == null");
        }
        if (DexOptHelper.useArtService()) {
            return;
        }
        String[] dexCodeInstructionSets = InstructionSets.getDexCodeInstructionSets(strArr);
        for (String str : list) {
            for (String str2 : dexCodeInstructionSets) {
                try {
                    this.mPm.mInstaller.rmdex(str, str2);
                } catch (Installer.InstallerException unused) {
                } catch (Installer.LegacyDexoptDisabledException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void cleanUpForMoveInstall(String str, String str2, String str3) {
        File file = new File(Environment.getDataAppDirectory(str), new File(str3).getName());
        Slog.d("PackageManager", "Cleaning up " + str2 + " on " + str);
        int[] userIds = this.mPm.mUserManager.getUserIds();
        synchronized (this.mPm.mInstallLock) {
            for (int i : userIds) {
                try {
                    this.mPm.mInstaller.destroyAppData(str, str2, i, 131075, 0L);
                } catch (Installer.InstallerException e) {
                    Slog.w("PackageManager", String.valueOf(e));
                }
            }
            removeCodePathLI(file);
        }
    }
}
