package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.app.AppOpsManager;
import android.app.ApplicationPackageManager;
import android.app.BroadcastOptions;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.backup.IBackupManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInfoLite;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.SigningDetails;
import android.content.pm.VerifierInfo;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.incremental.IncrementalManager;
import android.os.incremental.IncrementalStorage;
import android.os.storage.StorageManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.F2fsUtils;
import com.android.internal.security.VerityUtils;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalManagerRegistry;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageAbiHelper;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.ParallelPackageParser;
import com.android.server.p011pm.dex.ArtManagerService;
import com.android.server.p011pm.dex.DexManager;
import com.android.server.p011pm.dex.DexoptOptions;
import com.android.server.p011pm.dex.ViewCompiler;
import com.android.server.p011pm.parsing.PackageCacher;
import com.android.server.p011pm.parsing.PackageParser2;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.permission.Permission;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.SharedLibraryWrapper;
import com.android.server.p011pm.pkg.component.ComponentMutateUtils;
import com.android.server.p011pm.pkg.component.ParsedInstrumentation;
import com.android.server.p011pm.pkg.component.ParsedPermission;
import com.android.server.p011pm.pkg.component.ParsedPermissionGroup;
import com.android.server.p011pm.pkg.parsing.ParsingPackageUtils;
import com.android.server.rollback.RollbackManagerInternal;
import com.android.server.security.FileIntegrityService;
import com.android.server.utils.WatchedArrayMap;
import com.android.server.utils.WatchedLongSparseArray;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
/* renamed from: com.android.server.pm.InstallPackageHelper */
/* loaded from: classes2.dex */
public final class InstallPackageHelper {
    public final ApexManager mApexManager;
    public final AppDataHelper mAppDataHelper;
    public final ArtManagerService mArtManagerService;
    public final BroadcastHelper mBroadcastHelper;
    public final Context mContext;
    public final DexManager mDexManager;
    public final IncrementalManager mIncrementalManager;
    public final PackageManagerServiceInjector mInjector;
    public final PackageAbiHelper mPackageAbiHelper;
    public final PackageDexOptimizer mPackageDexOptimizer;
    public final PackageManagerService mPm;
    public final RemovePackageHelper mRemovePackageHelper;
    public final SharedLibrariesImpl mSharedLibraries;
    public final ViewCompiler mViewCompiler;

    public InstallPackageHelper(PackageManagerService packageManagerService, AppDataHelper appDataHelper) {
        this.mPm = packageManagerService;
        this.mInjector = packageManagerService.mInjector;
        this.mAppDataHelper = appDataHelper;
        this.mBroadcastHelper = new BroadcastHelper(packageManagerService.mInjector);
        this.mRemovePackageHelper = new RemovePackageHelper(packageManagerService);
        this.mIncrementalManager = packageManagerService.mInjector.getIncrementalManager();
        this.mApexManager = packageManagerService.mInjector.getApexManager();
        this.mDexManager = packageManagerService.mInjector.getDexManager();
        this.mArtManagerService = packageManagerService.mInjector.getArtManagerService();
        this.mContext = packageManagerService.mInjector.getContext();
        this.mPackageDexOptimizer = packageManagerService.mInjector.getPackageDexOptimizer();
        this.mPackageAbiHelper = packageManagerService.mInjector.getAbiHelper();
        this.mViewCompiler = packageManagerService.mInjector.getViewCompiler();
        this.mSharedLibraries = packageManagerService.mInjector.getSharedLibrariesImpl();
    }

    public InstallPackageHelper(PackageManagerService packageManagerService) {
        this(packageManagerService, new AppDataHelper(packageManagerService));
    }

    /* JADX WARN: Type inference failed for: r10v11, types: [boolean] */
    @GuardedBy({"mPm.mLock"})
    public AndroidPackage commitReconciledScanResultLocked(ReconciledPackage reconciledPackage, int[] iArr) {
        PackageSetting packageSetting;
        List<String> list;
        int i;
        int i2;
        int userId;
        PackageSetting packageLPr;
        InstallRequest installRequest = reconciledPackage.mInstallRequest;
        ParsedPackage parsedPackage = installRequest.getParsedPackage();
        if (parsedPackage != null && PackageManagerShellCommandDataLoader.PACKAGE.equals(parsedPackage.getPackageName())) {
            parsedPackage.setVersionCode(this.mPm.getSdkVersion()).setVersionCodeMajor(0);
        }
        int scanFlags = installRequest.getScanFlags();
        PackageSetting scanRequestOldPackageSetting = installRequest.getScanRequestOldPackageSetting();
        PackageSetting scanRequestOriginalPackageSetting = installRequest.getScanRequestOriginalPackageSetting();
        String realPackageName = installRequest.getRealPackageName();
        List<String> changedAbiCodePath = DexOptHelper.useArtService() ? null : installRequest.getChangedAbiCodePath();
        if (installRequest.getScanRequestPackageSetting() != null) {
            SharedUserSetting sharedUserSettingLPr = this.mPm.mSettings.getSharedUserSettingLPr(installRequest.getScanRequestPackageSetting());
            SharedUserSetting sharedUserSettingLPr2 = this.mPm.mSettings.getSharedUserSettingLPr(installRequest.getScanRequestPackageSetting());
            if (sharedUserSettingLPr != null && sharedUserSettingLPr != sharedUserSettingLPr2) {
                sharedUserSettingLPr.removePackage(installRequest.getScanRequestPackageSetting());
                if (this.mPm.mSettings.checkAndPruneSharedUserLPw(sharedUserSettingLPr, false)) {
                    installRequest.setRemovedAppId(sharedUserSettingLPr.mAppId);
                }
            }
        }
        if (installRequest.isExistingSettingCopied()) {
            packageSetting = installRequest.getScanRequestPackageSetting();
            packageSetting.updateFrom(installRequest.getScannedPackageSetting());
        } else {
            PackageSetting scannedPackageSetting = installRequest.getScannedPackageSetting();
            if (scanRequestOriginalPackageSetting != null) {
                this.mPm.mSettings.addRenamedPackageLPw(AndroidPackageUtils.getRealPackageOrNull(parsedPackage, scannedPackageSetting.isSystem()), scanRequestOriginalPackageSetting.getPackageName());
                this.mPm.mTransferredPackages.add(scanRequestOriginalPackageSetting.getPackageName());
            } else {
                this.mPm.mSettings.removeRenamedPackageLPw(parsedPackage.getPackageName());
            }
            packageSetting = scannedPackageSetting;
        }
        SharedUserSetting sharedUserSettingLPr3 = this.mPm.mSettings.getSharedUserSettingLPr(packageSetting);
        if (sharedUserSettingLPr3 != null) {
            sharedUserSettingLPr3.addPackage(packageSetting);
            if (parsedPackage.isLeavingSharedUser() && SharedUidMigration.applyStrategy(2) && sharedUserSettingLPr3.isSingleUser()) {
                this.mPm.mSettings.convertSharedUserSettingsLPw(sharedUserSettingLPr3);
            }
        }
        if (installRequest.isForceQueryableOverride()) {
            packageSetting.setForceQueryableOverride(true);
        }
        InstallSource installSource = installRequest.getInstallSource();
        boolean z = (67108864 & scanFlags) != 0;
        boolean z2 = scanRequestOldPackageSetting != null;
        boolean isAllowUpdateOwnership = parsedPackage.isAllowUpdateOwnership();
        String str = z2 ? scanRequestOldPackageSetting.getInstallSource().mUpdateOwnerPackageName : null;
        String systemAppUpdateOwnerPackageName = (z || !packageSetting.isSystem()) ? null : this.mPm.mInjector.getSystemConfig().getSystemAppUpdateOwnerPackageName(parsedPackage.getPackageName());
        boolean z3 = str != null;
        if (installSource != null) {
            String str2 = installSource.mInitiatingPackageName;
            list = changedAbiCodePath;
            if (str2 != null && (packageLPr = this.mPm.mSettings.getPackageLPr(str2)) != null) {
                installSource = installSource.setInitiatingPackageSignatures(packageLPr.getSignatures());
            }
            if (!isAllowUpdateOwnership) {
                installSource = installSource.setUpdateOwnerPackageName(null);
            } else if (!z) {
                int i3 = installSource.mInstallerPackageUid;
                if (i3 != -1) {
                    userId = UserHandle.getUserId(i3);
                } else {
                    userId = installRequest.getUserId();
                }
                boolean z4 = z2 && (userId < 0 ? scanRequestOldPackageSetting.getNotInstalledUserIds().length <= UserManager.isHeadlessSystemUserMode() : scanRequestOldPackageSetting.getInstalled(userId));
                boolean z5 = (installRequest.getInstallFlags() & 33554432) != 0;
                boolean equals = TextUtils.equals(str, installSource.mInstallerPackageName);
                if (z4) {
                    if (!equals || !z3) {
                        installSource = installSource.setUpdateOwnerPackageName(null);
                    }
                } else if (!z5) {
                    installSource = installSource.setUpdateOwnerPackageName(null);
                } else if ((!z3 && z2) || (z3 && !equals)) {
                    installSource = installSource.setUpdateOwnerPackageName(null);
                }
            }
            packageSetting.setInstallSource(installSource);
        } else {
            list = changedAbiCodePath;
            if (packageSetting.isSystem()) {
                if (!isAllowUpdateOwnership) {
                    packageSetting.setUpdateOwnerPackage(null);
                } else {
                    boolean z6 = z3 && TextUtils.equals(str, systemAppUpdateOwnerPackageName);
                    if (!z2 || z6) {
                        packageSetting.setUpdateOwnerPackage(systemAppUpdateOwnerPackageName);
                    } else {
                        packageSetting.setUpdateOwnerPackage(null);
                    }
                }
            }
        }
        if ((8388608 & scanFlags) != 0) {
            i = 1;
            packageSetting.getPkgState().setApkInUpdatedApex(!((scanFlags & 33554432) != 0));
        } else {
            i = 1;
        }
        packageSetting.getPkgState().setApexModuleName(installRequest.getApexModuleName());
        parsedPackage.setUid(packageSetting.getAppId());
        AndroidPackageInternal hideAsFinal = parsedPackage.hideAsFinal();
        this.mPm.mSettings.writeUserRestrictionsLPw(packageSetting, scanRequestOldPackageSetting);
        if (realPackageName != null) {
            this.mPm.mTransferredPackages.add(hideAsFinal.getPackageName());
        }
        if (reconciledPackage.mCollectedSharedLibraryInfos == null && (scanRequestOldPackageSetting == null || scanRequestOldPackageSetting.getSharedLibraryDependencies().isEmpty())) {
            i2 = i;
        } else {
            i2 = i;
            this.mSharedLibraries.executeSharedLibrariesUpdate(hideAsFinal, packageSetting, null, null, reconciledPackage.mCollectedSharedLibraryInfos, iArr);
        }
        KeySetManagerService keySetManagerService = this.mPm.mSettings.getKeySetManagerService();
        if (reconciledPackage.mRemoveAppKeySetData) {
            keySetManagerService.removeAppKeySetDataLPw(hideAsFinal.getPackageName());
        }
        if (reconciledPackage.mSharedUserSignaturesChanged) {
            sharedUserSettingLPr3.signaturesChanged = Boolean.TRUE;
            sharedUserSettingLPr3.signatures.mSigningDetails = reconciledPackage.mSigningDetails;
        }
        packageSetting.setSigningDetails(reconciledPackage.mSigningDetails);
        if (list != null && list.size() > 0) {
            int size = list.size() - i2;
            while (size >= 0) {
                List<String> list2 = list;
                String str3 = list2.get(size);
                try {
                    synchronized (this.mPm.mInstallLock) {
                        this.mPm.mInstaller.rmdex(str3, InstructionSets.getDexCodeInstructionSet(InstructionSets.getPreferredInstructionSet()));
                    }
                } catch (Installer.InstallerException unused) {
                } catch (Installer.LegacyDexoptDisabledException e) {
                    throw new RuntimeException(e);
                }
                size--;
                list = list2;
            }
        }
        int userId2 = installRequest.getUserId();
        commitPackageSettings(hideAsFinal, packageSetting, scanRequestOldPackageSetting, reconciledPackage);
        if (packageSetting.getInstantApp(userId2)) {
            this.mPm.mInstantAppRegistry.addInstantApp(userId2, packageSetting.getAppId());
        }
        if (!IncrementalManager.isIncrementalPath(packageSetting.getPathString())) {
            packageSetting.setLoadingProgress(1.0f);
        }
        return hideAsFinal;
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x012d A[Catch: all -> 0x0190, TryCatch #2 {, blocks: (B:35:0x00bb, B:37:0x00d2, B:38:0x00d7, B:40:0x00dc, B:41:0x00e7, B:45:0x0101, B:47:0x010b, B:50:0x0112, B:52:0x0121, B:54:0x012d, B:57:0x014b, B:59:0x0158, B:58:0x0153, B:60:0x015f, B:61:0x0162, B:63:0x016c, B:64:0x0170, B:71:0x017d, B:75:0x0188, B:76:0x018b, B:51:0x011a, B:65:0x0171, B:66:0x0178), top: B:82:0x00bb }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x016c A[Catch: all -> 0x0190, TryCatch #2 {, blocks: (B:35:0x00bb, B:37:0x00d2, B:38:0x00d7, B:40:0x00dc, B:41:0x00e7, B:45:0x0101, B:47:0x010b, B:50:0x0112, B:52:0x0121, B:54:0x012d, B:57:0x014b, B:59:0x0158, B:58:0x0153, B:60:0x015f, B:61:0x0162, B:63:0x016c, B:64:0x0170, B:71:0x017d, B:75:0x0188, B:76:0x018b, B:51:0x011a, B:65:0x0171, B:66:0x0178), top: B:82:0x00bb }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0187  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void commitPackageSettings(AndroidPackage androidPackage, PackageSetting packageSetting, PackageSetting packageSetting2, ReconciledPackage reconciledPackage) {
        int size;
        int i;
        List<String> protectedBroadcasts;
        String packageName = androidPackage.getPackageName();
        InstallRequest installRequest = reconciledPackage.mInstallRequest;
        AndroidPackage scanRequestOldPackage = installRequest.getScanRequestOldPackage();
        int scanFlags = installRequest.getScanFlags();
        boolean z = (installRequest.getParseFlags() & Integer.MIN_VALUE) != 0;
        ComponentName componentName = this.mPm.mCustomResolverComponentName;
        if (componentName != null && componentName.getPackageName().equals(androidPackage.getPackageName())) {
            this.mPm.setUpCustomResolverActivity(androidPackage, packageSetting);
        }
        if (androidPackage.getPackageName().equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
            this.mPm.setPlatformPackage(androidPackage, packageSetting);
        }
        boolean z2 = z;
        ArrayList<AndroidPackage> commitSharedLibraryChanges = this.mSharedLibraries.commitSharedLibraryChanges(androidPackage, packageSetting, reconciledPackage.mAllowedSharedLibraryInfos, reconciledPackage.getCombinedAvailablePackages(), scanFlags);
        installRequest.setLibraryConsumers(commitSharedLibraryChanges);
        if ((scanFlags & 16) == 0 && (scanFlags & 1024) == 0 && (scanFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0) {
            this.mPm.snapshotComputer().checkPackageFrozen(packageName);
        }
        boolean isInstallReplace = installRequest.isInstallReplace();
        if (commitSharedLibraryChanges != null && (androidPackage.getStaticSharedLibraryName() == null || isInstallReplace)) {
            for (int i2 = 0; i2 < commitSharedLibraryChanges.size(); i2++) {
                AndroidPackage androidPackage2 = commitSharedLibraryChanges.get(i2);
                this.mPm.killApplication(androidPackage2.getPackageName(), androidPackage2.getUid(), "update lib", 12);
            }
        }
        Trace.traceBegin(262144L, "updateSettings");
        synchronized (this.mPm.mLock) {
            this.mPm.mSettings.insertPackageSettingLPw(packageSetting, androidPackage);
            this.mPm.mPackages.put(androidPackage.getPackageName(), androidPackage);
            if ((8388608 & scanFlags) != 0) {
                this.mApexManager.registerApkInApex(androidPackage);
            }
            if ((67108864 & scanFlags) == 0) {
                this.mPm.mSettings.getKeySetManagerService().addScannedPackageLPw(androidPackage);
            }
            Computer snapshotComputer = this.mPm.snapshotComputer();
            PackageManagerService packageManagerService = this.mPm;
            packageManagerService.mComponentResolver.addAllComponents(androidPackage, z2, packageManagerService.mSetupWizardPackage, snapshotComputer);
            this.mPm.mAppsFilter.addPackage(snapshotComputer, packageSetting, isInstallReplace, (scanFlags & 1024) != 0);
            this.mPm.addAllPackageProperties(androidPackage);
            if (packageSetting2 != null && packageSetting2.getPkg() != null) {
                this.mPm.mDomainVerificationManager.migrateState(packageSetting2, packageSetting);
                size = ArrayUtils.size(androidPackage.getInstrumentations());
                StringBuilder sb = null;
                for (i = 0; i < size; i++) {
                    ParsedInstrumentation parsedInstrumentation = androidPackage.getInstrumentations().get(i);
                    ComponentMutateUtils.setPackageName(parsedInstrumentation, androidPackage.getPackageName());
                    this.mPm.addInstrumentation(parsedInstrumentation.getComponentName(), parsedInstrumentation);
                    if (z2) {
                        if (sb == null) {
                            sb = new StringBuilder(256);
                        } else {
                            sb.append(' ');
                        }
                        sb.append(parsedInstrumentation.getName());
                    }
                }
                protectedBroadcasts = androidPackage.getProtectedBroadcasts();
                if (!protectedBroadcasts.isEmpty()) {
                    synchronized (this.mPm.mProtectedBroadcasts) {
                        this.mPm.mProtectedBroadcasts.addAll(protectedBroadcasts);
                    }
                }
                this.mPm.mPermissionManager.onPackageAdded(packageSetting, (scanFlags & IInstalld.FLAG_FORCE) == 0, scanRequestOldPackage);
            }
            this.mPm.mDomainVerificationManager.addPackage(packageSetting);
            size = ArrayUtils.size(androidPackage.getInstrumentations());
            StringBuilder sb2 = null;
            while (i < size) {
            }
            protectedBroadcasts = androidPackage.getProtectedBroadcasts();
            if (!protectedBroadcasts.isEmpty()) {
            }
            this.mPm.mPermissionManager.onPackageAdded(packageSetting, (scanFlags & IInstalld.FLAG_FORCE) == 0, scanRequestOldPackage);
        }
        Trace.traceEnd(262144L);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x0119 A[Catch: all -> 0x01c7, TRY_ENTER, TryCatch #2 {all -> 0x01c7, blocks: (B:21:0x0086, B:22:0x008a, B:52:0x0119, B:54:0x012b, B:57:0x0132, B:59:0x0138, B:62:0x0140, B:63:0x0144, B:65:0x014a, B:67:0x0154, B:68:0x015f, B:69:0x0173, B:76:0x0182, B:77:0x0195, B:80:0x01a0, B:23:0x008b, B:25:0x009c, B:28:0x00a1, B:30:0x00ab, B:32:0x00b9, B:35:0x00c8, B:37:0x00cf, B:40:0x00d5, B:42:0x00db, B:49:0x010f, B:50:0x0116, B:45:0x0104, B:78:0x0196, B:79:0x019f, B:70:0x0174, B:71:0x017d), top: B:93:0x0086 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int installExistingPackageAsUser(final String str, final int i, int i2, int i3, List<String> list, final IntentSender intentSender) {
        boolean z;
        boolean z2;
        int callingUid = Binder.getCallingUid();
        if (this.mContext.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") != 0 && this.mContext.checkCallingOrSelfPermission("com.android.permission.INSTALL_EXISTING_PACKAGES") != 0) {
            throw new SecurityException("Neither user " + callingUid + " nor current process has android.permission.INSTALL_PACKAGES.");
        }
        Computer snapshotComputer = this.mPm.snapshotComputer();
        snapshotComputer.enforceCrossUserPermission(callingUid, i, true, true, "installExistingPackage for user " + i);
        if (this.mPm.isUserRestricted(i, "no_install_apps")) {
            return -111;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z3 = (i2 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
        boolean z4 = (i2 & 16384) != 0;
        try {
            synchronized (this.mPm.mLock) {
                Computer snapshotComputer2 = this.mPm.snapshotComputer();
                PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(str);
                if (packageLPr == null) {
                    return -3;
                }
                if (!snapshotComputer2.canViewInstantApps(callingUid, UserHandle.getUserId(callingUid))) {
                    int[] userIds = this.mPm.mUserManager.getUserIds();
                    int length = userIds.length;
                    int i4 = 0;
                    boolean z5 = false;
                    while (true) {
                        if (i4 >= length) {
                            break;
                        }
                        boolean z6 = !packageLPr.getInstantApp(userIds[i4]);
                        if (z6) {
                            z5 = z6;
                            break;
                        }
                        i4++;
                        z5 = z6;
                    }
                    if (!z5) {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return -3;
                    }
                }
                if (packageLPr.getInstalled(i)) {
                    z = false;
                    if (!z4 || !packageLPr.getInstantApp(i)) {
                        z2 = false;
                        ScanPackageUtils.setInstantAppForUser(this.mPm.mInjector, packageLPr, i, z3, z4);
                        if (z2) {
                            String str2 = packageLPr.getInstallSource().mUpdateOwnerPackageName;
                            DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) this.mInjector.getLocalService(DevicePolicyManagerInternal.class);
                            if (devicePolicyManagerInternal != null && devicePolicyManagerInternal.isUserOrganizationManaged(i)) {
                                z = true;
                            }
                            if (!snapshotComputer.isCallerSameApp(str2, callingUid) && (!packageLPr.isSystem() || !z)) {
                                packageLPr.setUpdateOwnerPackage(null);
                            }
                            if (packageLPr.getPkg() != null) {
                                PermissionManagerServiceInternal.PackageInstalledParams.Builder builder = new PermissionManagerServiceInternal.PackageInstalledParams.Builder();
                                if ((4194304 & i2) != 0) {
                                    builder.setAllowlistedRestrictedPermissions(packageLPr.getPkg().getRequestedPermissions());
                                }
                                this.mPm.mPermissionManager.onPackageInstalled(packageLPr.getPkg(), -1, builder.build(), i);
                                synchronized (this.mPm.mInstallLock) {
                                    this.mAppDataHelper.prepareAppDataAfterInstallLIF(packageLPr.getPkg());
                                }
                            }
                            PackageManagerService packageManagerService = this.mPm;
                            packageManagerService.sendPackageAddedForUser(packageManagerService.snapshotComputer(), str, packageLPr, i, 0);
                            synchronized (this.mPm.mLock) {
                                this.mPm.updateSequenceNumberLP(packageLPr, new int[]{i});
                            }
                            restoreAndPostInstall(new InstallRequest(i, 1, packageLPr.getPkg(), new int[]{i}, new Runnable() { // from class: com.android.server.pm.InstallPackageHelper$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    InstallPackageHelper.this.lambda$installExistingPackageAsUser$0(str, i, intentSender);
                                }
                            }));
                        }
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return 1;
                    }
                } else {
                    packageLPr.setInstalled(true, i);
                    z = false;
                    packageLPr.setHidden(false, i);
                    packageLPr.setInstallReason(i3, i);
                    packageLPr.setUninstallReason(0, i);
                    packageLPr.setFirstInstallTime(System.currentTimeMillis(), i);
                    this.mPm.mSettings.writePackageRestrictionsLPr(i);
                    this.mPm.mSettings.writeKernelMappingLPr(packageLPr);
                }
                z2 = true;
                ScanPackageUtils.setInstantAppForUser(this.mPm.mInjector, packageLPr, i, z3, z4);
                if (z2) {
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$installExistingPackageAsUser$0(String str, int i, IntentSender intentSender) {
        this.mPm.restorePermissionsAndUpdateRolesForNewUserInstall(str, i);
        if (intentSender != null) {
            onRestoreComplete(1, this.mContext, intentSender);
        }
    }

    public static void onRestoreComplete(int i, Context context, IntentSender intentSender) {
        Intent intent = new Intent();
        intent.putExtra("android.content.pm.extra.STATUS", PackageManager.installStatusToPublicStatus(i));
        try {
            BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
            makeBasic.setPendingIntentBackgroundActivityLaunchAllowed(false);
            intentSender.sendIntent(context, 0, intent, null, null, null, makeBasic.toBundle());
        } catch (IntentSender.SendIntentException unused) {
        }
    }

    public void restoreAndPostInstall(InstallRequest installRequest) {
        int userId = installRequest.getUserId();
        boolean isUpdate = installRequest.isUpdate();
        boolean z = (isUpdate || installRequest.getPkg() == null) ? false : true;
        PackageManagerService packageManagerService = this.mPm;
        if (packageManagerService.mNextInstallToken < 0) {
            packageManagerService.mNextInstallToken = 1;
        }
        int i = packageManagerService.mNextInstallToken;
        packageManagerService.mNextInstallToken = i + 1;
        packageManagerService.mRunningInstalls.put(i, installRequest);
        if (installRequest.getReturnCode() == 1 && z) {
            installRequest.closeFreezer();
            z = performBackupManagerRestore(userId, i, installRequest);
        }
        if (installRequest.getReturnCode() == 1 && !z && isUpdate) {
            z = performRollbackManagerRestore(userId, i, installRequest);
        }
        if (z) {
            return;
        }
        Trace.asyncTraceBegin(262144L, "postInstall", i);
        this.mPm.mHandler.sendMessage(this.mPm.mHandler.obtainMessage(9, i, 0));
    }

    public final boolean performBackupManagerRestore(int i, int i2, InstallRequest installRequest) {
        if (installRequest.getPkg() == null) {
            return false;
        }
        IBackupManager iBackupManager = this.mInjector.getIBackupManager();
        if (iBackupManager != null) {
            if (i == -1) {
                i = 0;
            }
            Trace.asyncTraceBegin(262144L, "restore", i2);
            try {
                if (iBackupManager.isUserReadyForBackup(i)) {
                    iBackupManager.restoreAtInstallForUser(i, installRequest.getPkg().getPackageName(), i2);
                    return true;
                }
                Slog.w("PackageManager", "User " + i + " is not ready. Restore at install didn't take place.");
                return false;
            } catch (RemoteException unused) {
                return true;
            } catch (Exception e) {
                Slog.e("PackageManager", "Exception trying to enqueue restore", e);
                return false;
            }
        }
        Slog.e("PackageManager", "Backup Manager not found!");
        return false;
    }

    public final boolean performRollbackManagerRestore(int i, int i2, InstallRequest installRequest) {
        PackageSetting packageLPr;
        int[] iArr;
        long j;
        int i3;
        if (installRequest.getPkg() == null) {
            return false;
        }
        String packageName = installRequest.getPkg().getPackageName();
        int[] userIds = this.mPm.mUserManager.getUserIds();
        synchronized (this.mPm.mLock) {
            packageLPr = this.mPm.mSettings.getPackageLPr(packageName);
            if (packageLPr != null) {
                i3 = packageLPr.getAppId();
                j = packageLPr.getCeDataInode(i);
                iArr = packageLPr.queryInstalledUsers(userIds, true);
            } else {
                iArr = new int[0];
                j = -1;
                i3 = -1;
            }
        }
        int installFlags = installRequest.getInstallFlags();
        boolean z = ((262144 & installFlags) == 0 && (installFlags & 128) == 0) ? false : true;
        if (packageLPr == null || !z) {
            return false;
        }
        ((RollbackManagerInternal) this.mInjector.getLocalService(RollbackManagerInternal.class)).snapshotAndRestoreUserData(packageName, UserHandle.toUserHandles(iArr), i3, j, packageLPr.getSeInfo(), i2);
        return true;
    }

    public void installPackagesTraced(List<InstallRequest> list) {
        synchronized (this.mPm.mInstallLock) {
            Trace.traceBegin(262144L, "installPackages");
            installPackagesLI(list);
            Trace.traceEnd(262144L);
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:159:0x0374
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    @com.android.internal.annotations.GuardedBy({"mPm.mInstallLock"})
    public final void installPackagesLI(java.util.List<com.android.server.p011pm.InstallRequest> r30) {
        /*
            Method dump skipped, instructions count: 1182
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.p011pm.InstallPackageHelper.installPackagesLI(java.util.List):void");
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final boolean checkNoAppStorageIsConsistent(AndroidPackage androidPackage, AndroidPackage androidPackage2) {
        if (androidPackage == null) {
            return true;
        }
        PackageManager.Property property = androidPackage.getProperties().get("android.internal.PROPERTY_NO_APP_DATA_STORAGE");
        PackageManager.Property property2 = androidPackage2.getProperties().get("android.internal.PROPERTY_NO_APP_DATA_STORAGE");
        return (property == null || !property.getBoolean()) ? property2 == null || !property2.getBoolean() : property2 != null && property2.getBoolean();
    }

    /* JADX WARN: Removed duplicated region for block: B:145:0x02b0 A[Catch: all -> 0x0bf7, TryCatch #15 {, blocks: (B:135:0x027d, B:137:0x028f, B:139:0x0299, B:145:0x02b0, B:149:0x02cb, B:150:0x02fc, B:151:0x02fd, B:153:0x0303, B:156:0x0308, B:157:0x0328, B:161:0x032f, B:163:0x0339, B:165:0x033f, B:167:0x034b, B:169:0x0351, B:171:0x0366, B:173:0x036c, B:175:0x0374, B:177:0x0382, B:179:0x039a, B:202:0x0416, B:204:0x042f, B:206:0x043a, B:208:0x0455, B:210:0x045b, B:212:0x045f, B:214:0x0469, B:217:0x0470, B:218:0x04bd, B:219:0x04be, B:220:0x04c6, B:222:0x04d6, B:225:0x04f6, B:228:0x0531, B:230:0x053b, B:232:0x0543, B:233:0x0573, B:234:0x05ab, B:235:0x05ac, B:237:0x05b8, B:239:0x05c1, B:241:0x05c7, B:242:0x05f9, B:244:0x05ff, B:248:0x0608, B:254:0x0628, B:256:0x0635, B:258:0x063f, B:261:0x0647, B:262:0x06a5, B:263:0x06a6, B:264:0x06f7, B:251:0x0622, B:265:0x06f8, B:267:0x0705, B:182:0x03a4, B:183:0x03c4, B:185:0x03c7, B:187:0x03f2, B:188:0x03f6, B:195:0x0403, B:198:0x0406, B:199:0x0411, B:140:0x02a1), top: B:571:0x027d, inners: #5 }] */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0329  */
    /* JADX WARN: Removed duplicated region for block: B:520:0x0bcb  */
    @GuardedBy({"mPm.mInstallLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void preparePackageLI(InstallRequest installRequest) throws PrepareFailure {
        boolean z;
        boolean z2;
        boolean z3;
        int i;
        boolean z4;
        boolean z5;
        int i2;
        boolean z6;
        PackageSetting packageLPr;
        boolean z7;
        int i3;
        Throwable th;
        int i4;
        PackageSetting packageLPr2;
        Throwable th2;
        boolean z8;
        PackageSetting packageSetting;
        AndroidPackage androidPackage;
        PackageSetting packageSetting2;
        int i5;
        boolean z9;
        boolean z10;
        boolean z11;
        ParsedPermissionGroup parsedPermissionGroup;
        SharedLibraryInfo latestStaticSharedLibraVersion;
        WatchedLongSparseArray<SharedLibraryInfo> sharedLibraryInfos;
        int installFlags = installRequest.getInstallFlags();
        boolean z12 = installRequest.getVolumeUuid() != null;
        boolean z13 = (installFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
        boolean z14 = (installFlags & 16384) != 0;
        boolean z15 = (installFlags & 65536) != 0;
        boolean z16 = (installFlags & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0;
        boolean z17 = installRequest.getInstallReason() == 5;
        int i6 = installRequest.isInstallMove() ? 518 : 6;
        if ((installFlags & IInstalld.FLAG_USE_QUOTA) != 0) {
            i6 |= 1024;
        }
        if (z13) {
            i6 |= IInstalld.FLAG_FORCE;
        }
        if (z14) {
            i6 |= 16384;
        }
        if (z15) {
            i6 |= 32768;
        }
        if (z16) {
            i6 |= 67108864;
        }
        File file = new File(z16 ? installRequest.getApexInfo().modulePath : installRequest.getCodePath());
        if (z13 && z12) {
            Slog.i("PackageManager", "Incompatible ephemeral install; external=" + z12);
            throw new PrepareFailure(-116);
        }
        int defParseFlags = this.mPm.getDefParseFlags() | Integer.MIN_VALUE | 64 | (z12 ? 8 : 0);
        boolean z18 = z17;
        Trace.traceBegin(262144L, "parsePackage");
        try {
            try {
                PackageParser2 preparingPackageParser = this.mPm.mInjector.getPreparingPackageParser();
                try {
                    ParsedPackage parsePackage = preparingPackageParser.parsePackage(file, defParseFlags, false);
                    AndroidPackageUtils.validatePackageDexMetadata(parsePackage);
                    preparingPackageParser.close();
                    Trace.traceEnd(262144L);
                    if (DeviceConfig.getBoolean("package_manager_service", "MinInstallableTargetSdk__install_block_enabled", false)) {
                        int i7 = DeviceConfig.getInt("package_manager_service", "MinInstallableTargetSdk__min_installable_target_sdk", 0);
                        boolean z19 = DeviceConfig.getBoolean("package_manager_service", "MinInstallableTargetSdk__install_block_strict_mode_enabled", false) && parsePackage.getTargetSdkVersion() < DeviceConfig.getInt("package_manager_service", "MinInstallableTargetSdk__strict_mode_target_sdk", 0);
                        boolean z20 = (16777216 & installFlags) != 0;
                        if (!z19 && !z20 && (installFlags & 32) != 0) {
                            z20 = true;
                        }
                        if (!z19 && !z20 && (installRequest.getInstallerPackageName() == null || this.mContext.getPackageManager().getInstallerPackageName(installRequest.getInstallerPackageName()) == null)) {
                            z20 = true;
                        }
                        if (!z20 && parsePackage.isTestOnly()) {
                            z20 = true;
                        }
                        if (!z20 && parsePackage.getTargetSdkVersion() < i7) {
                            Slog.w("PackageManager", "App " + parsePackage.getPackageName() + " targets deprecated sdk version");
                            throw new PrepareFailure(-29, "App package must target at least SDK version " + i7 + ", but found " + parsePackage.getTargetSdkVersion());
                        }
                    }
                    if (z13) {
                        if (parsePackage.getTargetSdkVersion() < 26) {
                            Slog.w("PackageManager", "Instant app package " + parsePackage.getPackageName() + " does not target at least O");
                            throw new PrepareFailure(-116, "Instant app package must target at least O");
                        } else if (parsePackage.getSharedUserId() != null) {
                            Slog.w("PackageManager", "Instant app package " + parsePackage.getPackageName() + " may not declare sharedUserId.");
                            throw new PrepareFailure(-116, "Instant app package may not declare a sharedUserId");
                        }
                    }
                    if (parsePackage.isStaticSharedLibrary()) {
                        PackageManagerService.renameStaticSharedLibraryPackage(parsePackage);
                        if (z12) {
                            Slog.i("PackageManager", "Static shared libs can only be installed on internal storage.");
                            throw new PrepareFailure(-19, "Static shared libs can only be installed on internal storage.");
                        }
                    }
                    String packageName = parsePackage.getPackageName();
                    installRequest.setName(packageName);
                    if (parsePackage.isTestOnly() && (installFlags & 4) == 0) {
                        throw new PrepareFailure(-15, "Failed to install test-only apk. Did you forget to add -t?");
                    }
                    if (installRequest.getSigningDetails() != SigningDetails.UNKNOWN) {
                        parsePackage.setSigningDetails(installRequest.getSigningDetails());
                    } else {
                        ParseResult<SigningDetails> signingDetails = ParsingPackageUtils.getSigningDetails((ParseInput) ParseTypeImpl.forDefaultParsing(), parsePackage, false);
                        if (signingDetails.isError()) {
                            throw new PrepareFailure("Failed collect during installPackageLI", signingDetails.getException());
                        }
                        parsePackage.setSigningDetails((SigningDetails) signingDetails.getResult());
                    }
                    if (z13 && parsePackage.getSigningDetails().getSignatureSchemeVersion() < 2) {
                        Slog.w("PackageManager", "Instant app package " + parsePackage.getPackageName() + " is not signed with at least APK Signature Scheme v2");
                        throw new PrepareFailure(-116, "Instant app package must be signed with APK Signature Scheme v2 or greater");
                    }
                    synchronized (this.mPm.mLock) {
                        if ((installFlags & 2) != 0) {
                            String renamedPackageLPr = this.mPm.mSettings.getRenamedPackageLPr(packageName);
                            if (parsePackage.getOriginalPackages().contains(renamedPackageLPr) && this.mPm.mPackages.containsKey(renamedPackageLPr)) {
                                parsePackage.setPackageName(renamedPackageLPr);
                                packageName = parsePackage.getPackageName();
                            } else if (!this.mPm.mPackages.containsKey(packageName)) {
                                z = false;
                                if (z) {
                                    z2 = z;
                                } else {
                                    AndroidPackage androidPackage2 = this.mPm.mPackages.get(packageName);
                                    int targetSdkVersion = androidPackage2.getTargetSdkVersion();
                                    int targetSdkVersion2 = parsePackage.getTargetSdkVersion();
                                    z2 = z;
                                    if (targetSdkVersion > 22 && targetSdkVersion2 <= 22) {
                                        throw new PrepareFailure(-26, "Package " + parsePackage.getPackageName() + " new target SDK " + targetSdkVersion2 + " doesn't support runtime permissions but the old target SDK " + targetSdkVersion + " does.");
                                    }
                                    if (androidPackage2.isPersistent() && (installFlags & 2097152) == 0) {
                                        throw new PrepareFailure(-2, "Package " + androidPackage2.getPackageName() + " is a persistent app. Persistent apps are not updateable.");
                                    }
                                }
                                z3 = z2;
                            }
                            z = true;
                            if (z) {
                            }
                            z3 = z2;
                        } else {
                            z3 = false;
                        }
                        PackageSetting packageLPr3 = this.mPm.mSettings.getPackageLPr(packageName);
                        PackageSetting packageLPr4 = (packageLPr3 != null || !parsePackage.isSdkLibrary() || (sharedLibraryInfos = this.mSharedLibraries.getSharedLibraryInfos(parsePackage.getSdkLibraryName())) == null || sharedLibraryInfos.size() <= 0) ? packageLPr3 : this.mPm.mSettings.getPackageLPr(sharedLibraryInfos.valueAt(0).getPackageName());
                        if (parsePackage.isStaticSharedLibrary() && (latestStaticSharedLibraVersion = this.mSharedLibraries.getLatestStaticSharedLibraVersion(parsePackage)) != null) {
                            packageLPr4 = this.mPm.mSettings.getPackageLPr(latestStaticSharedLibraVersion.getPackageName());
                        }
                        if (packageLPr4 != null) {
                            KeySetManagerService keySetManagerService = this.mPm.mSettings.getKeySetManagerService();
                            SharedUserSetting sharedUserSettingLPr = this.mPm.mSettings.getSharedUserSettingLPr(packageLPr4);
                            if (keySetManagerService.shouldCheckUpgradeKeySetLocked(packageLPr4, sharedUserSettingLPr, i6)) {
                                if (!keySetManagerService.checkUpgradeKeySetLocked(packageLPr4, parsePackage)) {
                                    throw new PrepareFailure(-7, "Package " + parsePackage.getPackageName() + " upgrade keys do not match the previously installed version");
                                }
                                i = defParseFlags;
                            } else {
                                try {
                                    i = defParseFlags;
                                    if (PackageManagerServiceUtils.verifySignatures(packageLPr4, sharedUserSettingLPr, null, parsePackage.getSigningDetails(), ReconcilePackageUtils.isCompatSignatureUpdateNeeded(this.mPm.getSettingsVersionForPackage(parsePackage)), ReconcilePackageUtils.isRecoverSignatureUpdateNeeded(this.mPm.getSettingsVersionForPackage(parsePackage)), z18)) {
                                        synchronized (this.mPm.mLock) {
                                            keySetManagerService.removeAppKeySetDataLPw(parsePackage.getPackageName());
                                        }
                                    }
                                } catch (PackageManagerException e) {
                                    throw new PrepareFailure(e.error, e.getMessage());
                                }
                            }
                        } else {
                            i = defParseFlags;
                        }
                        if (packageLPr3 != null) {
                            boolean isSystem = packageLPr3.isSystem();
                            installRequest.setOriginUsers(packageLPr3.queryInstalledUsers(this.mPm.mUserManager.getUserIds(), true));
                            z4 = isSystem;
                        } else {
                            z4 = false;
                        }
                        int size = ArrayUtils.size(parsePackage.getPermissionGroups());
                        int i8 = 0;
                        while (i8 < size) {
                            int i9 = installFlags;
                            boolean z21 = z16;
                            PermissionGroupInfo permissionGroupInfo = this.mPm.getPermissionGroupInfo(parsePackage.getPermissionGroups().get(i8).getName(), 0);
                            if (permissionGroupInfo != null && cannotInstallWithBadPermissionGroups(parsePackage)) {
                                String str = permissionGroupInfo.packageName;
                                if ((z3 || !parsePackage.getPackageName().equals(str)) && !doesSignatureMatchForPermissions(str, parsePackage, i6)) {
                                    EventLog.writeEvent(1397638484, "146211400", -1, parsePackage.getPackageName());
                                    throw new PrepareFailure(-126, "Package " + parsePackage.getPackageName() + " attempting to redeclare permission group " + parsedPermissionGroup.getName() + " already owned by " + str);
                                }
                            }
                            i8++;
                            installFlags = i9;
                            z16 = z21;
                        }
                        z5 = z16;
                        i2 = installFlags;
                        int size2 = ArrayUtils.size(parsePackage.getPermissions()) - 1;
                        while (size2 >= 0) {
                            ParsedPermission parsedPermission = parsePackage.getPermissions().get(size2);
                            Permission permissionTEMP = this.mPm.mPermissionManager.getPermissionTEMP(parsedPermission.getName());
                            if ((parsedPermission.getProtectionLevel() & IInstalld.FLAG_USE_QUOTA) == 0 || z4) {
                                z10 = z3;
                            } else {
                                StringBuilder sb = new StringBuilder();
                                z10 = z3;
                                sb.append("Non-System package ");
                                sb.append(parsePackage.getPackageName());
                                sb.append(" attempting to delcare ephemeral permission ");
                                sb.append(parsedPermission.getName());
                                sb.append("; Removing ephemeral.");
                                Slog.w("PackageManager", sb.toString());
                                ComponentMutateUtils.setProtectionLevel(parsedPermission, parsedPermission.getProtectionLevel() & (-4097));
                            }
                            if (permissionTEMP != null) {
                                String packageName2 = permissionTEMP.getPackageName();
                                if (!doesSignatureMatchForPermissions(packageName2, parsePackage, i6)) {
                                    if (!packageName2.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
                                        throw new PrepareFailure(-112, "Package " + parsePackage.getPackageName() + " attempting to redeclare permission " + parsedPermission.getName() + " already owned by " + packageName2).conflictsWithExistingPermission(parsedPermission.getName(), packageName2);
                                    }
                                    Slog.w("PackageManager", "Package " + parsePackage.getPackageName() + " attempting to redeclare system permission " + parsedPermission.getName() + "; ignoring new declaration");
                                    parsePackage.removePermission(size2);
                                } else if (!PackageManagerShellCommandDataLoader.PACKAGE.equals(parsePackage.getPackageName()) && (parsedPermission.getProtectionLevel() & 15) == 1 && !permissionTEMP.isRuntime()) {
                                    Slog.w("PackageManager", "Package " + parsePackage.getPackageName() + " trying to change a non-runtime permission " + parsedPermission.getName() + " to runtime; keeping old protection level");
                                    ComponentMutateUtils.setProtectionLevel(parsedPermission, permissionTEMP.getProtectionLevel());
                                }
                            }
                            if (parsedPermission.getGroup() != null && cannotInstallWithBadPermissionGroups(parsePackage)) {
                                int i10 = 0;
                                while (true) {
                                    if (i10 >= size) {
                                        z11 = false;
                                        break;
                                    } else if (parsePackage.getPermissionGroups().get(i10).getName().equals(parsedPermission.getGroup())) {
                                        z11 = true;
                                        break;
                                    } else {
                                        i10++;
                                    }
                                }
                                if (z11) {
                                    continue;
                                } else {
                                    PermissionGroupInfo permissionGroupInfo2 = this.mPm.getPermissionGroupInfo(parsedPermission.getGroup(), 0);
                                    if (permissionGroupInfo2 == null) {
                                        EventLog.writeEvent(1397638484, "146211400", -1, parsePackage.getPackageName());
                                        throw new PrepareFailure(-127, "Package " + parsePackage.getPackageName() + " attempting to declare permission " + parsedPermission.getName() + " in non-existing group " + parsedPermission.getGroup());
                                    }
                                    String str2 = permissionGroupInfo2.packageName;
                                    if (!PackageManagerShellCommandDataLoader.PACKAGE.equals(str2) && !doesSignatureMatchForPermissions(str2, parsePackage, i6)) {
                                        EventLog.writeEvent(1397638484, "146211400", -1, parsePackage.getPackageName());
                                        throw new PrepareFailure(-127, "Package " + parsePackage.getPackageName() + " attempting to declare permission " + parsedPermission.getName() + " in group " + parsedPermission.getGroup() + " owned by package " + str2 + " with incompatible certificate");
                                    }
                                }
                            }
                            size2--;
                            z3 = z10;
                        }
                        z6 = z3;
                    }
                    if (z4) {
                        if (z12) {
                            throw new PrepareFailure(-19, "Cannot install updates to system apps on sdcard");
                        }
                        if (z13) {
                            throw new PrepareFailure(-116, "Cannot update a system app with an instant app");
                        }
                    }
                    if (installRequest.isInstallMove()) {
                        int i11 = i6 | 1 | 256;
                        synchronized (this.mPm.mLock) {
                            PackageSetting packageLPr5 = this.mPm.mSettings.getPackageLPr(packageName);
                            if (packageLPr5 == null) {
                                installRequest.setError(PackageManagerException.ofInternalError("Missing settings for moved package " + packageName, -3));
                            }
                            parsePackage.setPrimaryCpuAbi(packageLPr5.getPrimaryCpuAbiLegacy()).setSecondaryCpuAbi(packageLPr5.getSecondaryCpuAbiLegacy());
                        }
                        i3 = i11;
                    } else {
                        int i12 = i6 | 1;
                        try {
                            synchronized (this.mPm.mLock) {
                                packageLPr = this.mPm.mSettings.getPackageLPr(packageName);
                            }
                            boolean z22 = packageLPr != null && packageLPr.isUpdatedSystemApp();
                            String deriveAbiOverride = PackageManagerServiceUtils.deriveAbiOverride(installRequest.getAbiOverride());
                            boolean z23 = packageLPr != null && packageLPr.isSystem();
                            PackageAbiHelper packageAbiHelper = this.mPackageAbiHelper;
                            if (!z22 && !z23) {
                                z7 = false;
                                Pair<PackageAbiHelper.Abis, PackageAbiHelper.NativeLibraryPaths> derivePackageAbi = packageAbiHelper.derivePackageAbi(parsePackage, z4, z7, deriveAbiOverride, ScanPackageUtils.getAppLib32InstallDir());
                                ((PackageAbiHelper.Abis) derivePackageAbi.first).applyTo(parsePackage);
                                ((PackageAbiHelper.NativeLibraryPaths) derivePackageAbi.second).applyTo(parsePackage);
                                i3 = i12;
                            }
                            z7 = true;
                            Pair<PackageAbiHelper.Abis, PackageAbiHelper.NativeLibraryPaths> derivePackageAbi2 = packageAbiHelper.derivePackageAbi(parsePackage, z4, z7, deriveAbiOverride, ScanPackageUtils.getAppLib32InstallDir());
                            ((PackageAbiHelper.Abis) derivePackageAbi2.first).applyTo(parsePackage);
                            ((PackageAbiHelper.NativeLibraryPaths) derivePackageAbi2.second).applyTo(parsePackage);
                            i3 = i12;
                        } catch (PackageManagerException e2) {
                            Slog.e("PackageManager", "Error deriving application ABI", e2);
                            throw PrepareFailure.ofInternalError("Error deriving application ABI: " + e2.getMessage(), -4);
                        }
                    }
                    if (!z5) {
                        doRenameLI(installRequest, parsePackage);
                        try {
                            setUpFsVerity(parsePackage);
                        } catch (Installer.InstallerException | IOException | DigestException | NoSuchAlgorithmException e3) {
                            throw PrepareFailure.ofInternalError("Failed to set up verity: " + e3, -5);
                        }
                    } else {
                        parsePackage.setPath(installRequest.getApexInfo().modulePath);
                        parsePackage.setBaseApkPath(installRequest.getApexInfo().modulePath);
                    }
                    int i13 = i2;
                    PackageFreezer freezePackageForInstall = freezePackageForInstall(packageName, -1, i13, "installPackageLI", 16);
                    try {
                        if (z6) {
                            try {
                                String packageName3 = parsePackage.getPackageName();
                                synchronized (this.mPm.mLock) {
                                    packageLPr2 = this.mPm.mSettings.getPackageLPr(packageName3);
                                }
                                AndroidPackage androidPackage3 = packageLPr2.getAndroidPackage();
                                try {
                                    if (parsePackage.isStaticSharedLibrary() && androidPackage3 != null && (i2 & 32) == 0) {
                                        throw new PrepareFailure(-5, "Packages declaring static-shared libs cannot be updated");
                                    }
                                    boolean z24 = (i3 & IInstalld.FLAG_FORCE) != 0;
                                    try {
                                        synchronized (this.mPm.mLock) {
                                            try {
                                                PackageSetting packageLPr6 = this.mPm.mSettings.getPackageLPr(packageName3);
                                                PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(packageLPr6);
                                                SharedUserSetting sharedUserSettingLPr2 = this.mPm.mSettings.getSharedUserSettingLPr(packageLPr6);
                                                KeySetManagerService keySetManagerService2 = this.mPm.mSettings.getKeySetManagerService();
                                                try {
                                                    if (keySetManagerService2.shouldCheckUpgradeKeySetLocked(packageLPr6, sharedUserSettingLPr2, i3)) {
                                                        if (!keySetManagerService2.checkUpgradeKeySetLocked(packageLPr6, parsePackage)) {
                                                            throw new PrepareFailure(-7, "New package not signed by keys specified by upgrade-keysets: " + packageName3);
                                                        }
                                                    } else {
                                                        SigningDetails signingDetails2 = parsePackage.getSigningDetails();
                                                        SigningDetails signingDetails3 = androidPackage3.getSigningDetails();
                                                        z8 = true;
                                                        try {
                                                            if (!signingDetails2.checkCapability(signingDetails3, 1) && !signingDetails3.checkCapability(signingDetails2, 8) && (!z18 || !signingDetails3.hasAncestorOrSelf(signingDetails2))) {
                                                                throw new PrepareFailure(-7, "New package has a different signature: " + packageName3);
                                                            }
                                                        } catch (Throwable th3) {
                                                            th = th3;
                                                            th2 = th;
                                                            throw th2;
                                                        }
                                                    }
                                                    if (androidPackage3.getRestrictUpdateHash() == null || !packageLPr2.isSystem()) {
                                                        packageSetting = disabledSystemPkgLPr;
                                                    } else {
                                                        try {
                                                            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                                                            updateDigest(messageDigest, new File(parsePackage.getBaseApkPath()));
                                                            if (ArrayUtils.isEmpty(parsePackage.getSplitCodePaths())) {
                                                                packageSetting = disabledSystemPkgLPr;
                                                            } else {
                                                                String[] splitCodePaths = parsePackage.getSplitCodePaths();
                                                                int length = splitCodePaths.length;
                                                                packageSetting = disabledSystemPkgLPr;
                                                                int i14 = 0;
                                                                while (i14 < length) {
                                                                    updateDigest(messageDigest, new File(splitCodePaths[i14]));
                                                                    i14++;
                                                                    length = length;
                                                                    splitCodePaths = splitCodePaths;
                                                                }
                                                            }
                                                            if (!Arrays.equals(androidPackage3.getRestrictUpdateHash(), messageDigest.digest())) {
                                                                throw new PrepareFailure(-2, "New package fails restrict-update check: " + packageName3);
                                                            }
                                                            parsePackage.setRestrictUpdateHash(androidPackage3.getRestrictUpdateHash());
                                                        } catch (IOException | NoSuchAlgorithmException unused) {
                                                            throw new PrepareFailure(-2, "Could not compute hash: " + packageName3);
                                                        }
                                                    }
                                                    String sharedUserId = androidPackage3.getSharedUserId() != null ? androidPackage3.getSharedUserId() : "<nothing>";
                                                    String sharedUserId2 = parsePackage.getSharedUserId() != null ? parsePackage.getSharedUserId() : "<nothing>";
                                                    if (!sharedUserId.equals(sharedUserId2)) {
                                                        throw new PrepareFailure(-24, "Package " + parsePackage.getPackageName() + " shared user changed from " + sharedUserId + " to " + sharedUserId2);
                                                    }
                                                    if (androidPackage3.isLeavingSharedUser() && !parsePackage.isLeavingSharedUser()) {
                                                        throw new PrepareFailure(-24, "Package " + parsePackage.getPackageName() + " attempting to rejoin " + sharedUserId2);
                                                    }
                                                    int[] userIds = this.mPm.mUserManager.getUserIds();
                                                    z8 = true;
                                                    int[] queryInstalledUsers = packageLPr6.queryInstalledUsers(userIds, true);
                                                    int[] queryInstalledUsers2 = packageLPr6.queryInstalledUsers(userIds, false);
                                                    if (z24) {
                                                        if (installRequest.getUserId() == -1) {
                                                            int length2 = userIds.length;
                                                            int i15 = 0;
                                                            while (i15 < length2) {
                                                                int i16 = length2;
                                                                int i17 = userIds[i15];
                                                                if (!packageLPr6.getInstantApp(i17)) {
                                                                    Slog.w("PackageManager", "Can't replace full app with instant app: " + packageName3 + " for user: " + i17);
                                                                    throw new PrepareFailure(-116);
                                                                }
                                                                i15++;
                                                                length2 = i16;
                                                            }
                                                        } else if (!packageLPr6.getInstantApp(installRequest.getUserId())) {
                                                            Slog.w("PackageManager", "Can't replace full app with instant app: " + packageName3 + " for user: " + installRequest.getUserId());
                                                            throw new PrepareFailure(-116);
                                                        }
                                                    }
                                                    PackageRemovedInfo packageRemovedInfo = new PackageRemovedInfo(this.mPm);
                                                    packageRemovedInfo.mUid = androidPackage3.getUid();
                                                    packageRemovedInfo.mRemovedPackage = androidPackage3.getPackageName();
                                                    packageRemovedInfo.mInstallerPackageName = packageLPr6.getInstallSource().mInstallerPackageName;
                                                    packageRemovedInfo.mIsStaticSharedLib = parsePackage.getStaticSharedLibraryName() != null;
                                                    packageRemovedInfo.mIsUpdate = true;
                                                    packageRemovedInfo.mOrigUsers = queryInstalledUsers;
                                                    packageRemovedInfo.mInstallReasons = new SparseIntArray(queryInstalledUsers.length);
                                                    for (int i18 : queryInstalledUsers) {
                                                        packageRemovedInfo.mInstallReasons.put(i18, packageLPr6.getInstallReason(i18));
                                                    }
                                                    packageRemovedInfo.mUninstallReasons = new SparseIntArray(queryInstalledUsers2.length);
                                                    for (int i19 : queryInstalledUsers2) {
                                                        packageRemovedInfo.mUninstallReasons.put(i19, packageLPr6.getUninstallReason(i19));
                                                    }
                                                    packageRemovedInfo.mIsExternal = androidPackage3.isExternalStorage();
                                                    packageRemovedInfo.mRemovedPackageVersionCode = androidPackage3.getLongVersionCode();
                                                    installRequest.setRemovedInfo(packageRemovedInfo);
                                                    boolean isSystem2 = packageLPr2.isSystem();
                                                    if (isSystem2) {
                                                        i3 = i3 | 65536 | (packageLPr2.isPrivileged() ? IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES : 0) | (packageLPr2.isOem() ? 262144 : 0) | (packageLPr2.isVendor() ? 524288 : 0) | (packageLPr2.isProduct() ? 1048576 : 0) | (packageLPr2.isOdm() ? 4194304 : 0) | (packageLPr2.isSystemExt() ? 2097152 : 0);
                                                        installRequest.setReturnCode(1);
                                                        installRequest.setApexModuleName(packageLPr2.getApexModuleName());
                                                    }
                                                    androidPackage = androidPackage3;
                                                    packageSetting2 = packageLPr6;
                                                    i5 = i3;
                                                    z9 = isSystem2;
                                                } catch (Throwable th4) {
                                                    th2 = th4;
                                                    throw th2;
                                                }
                                            } catch (Throwable th5) {
                                                th = th5;
                                            }
                                        }
                                    } catch (Throwable th6) {
                                        th = th6;
                                    }
                                } catch (Throwable th7) {
                                    th = th7;
                                    i4 = 1;
                                    installRequest.setFreezer(freezePackageForInstall);
                                    if (i4 != 0) {
                                        freezePackageForInstall.close();
                                    }
                                    throw th;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                                i13 = 1;
                                th = th;
                                i4 = i13;
                                installRequest.setFreezer(freezePackageForInstall);
                                if (i4 != 0) {
                                }
                                throw th;
                            }
                        } else {
                            String packageName4 = parsePackage.getPackageName();
                            synchronized (this.mPm.mLock) {
                                String renamedPackageLPr2 = this.mPm.mSettings.getRenamedPackageLPr(packageName4);
                                if (renamedPackageLPr2 != null) {
                                    throw new PrepareFailure(-1, "Attempt to re-install " + packageName4 + " without first uninstalling package running as " + renamedPackageLPr2);
                                } else if (this.mPm.mPackages.containsKey(packageName4)) {
                                    throw new PrepareFailure(-1, "Attempt to re-install " + packageName4 + " without first uninstalling.");
                                }
                            }
                            androidPackage = null;
                            packageSetting2 = null;
                            packageSetting = null;
                            i5 = i3;
                            z9 = false;
                        }
                        try {
                            installRequest.setPrepareResult(z6, i5, i, androidPackage, parsePackage, z6, z9, packageSetting2, packageSetting);
                            installRequest.setFreezer(freezePackageForInstall);
                        } catch (Throwable th9) {
                            th = th9;
                            i4 = 0;
                            installRequest.setFreezer(freezePackageForInstall);
                            if (i4 != 0) {
                            }
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                    }
                } catch (Throwable th11) {
                    if (preparingPackageParser != null) {
                        try {
                            preparingPackageParser.close();
                        } catch (Throwable th12) {
                            th11.addSuppressed(th12);
                        }
                    }
                    throw th11;
                }
            } catch (Throwable th13) {
                Trace.traceEnd(262144L);
                throw th13;
            }
        } catch (PackageManagerException e4) {
            throw new PrepareFailure("Failed parse during installPackageLI", e4);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void doRenameLI(InstallRequest installRequest, ParsedPackage parsedPackage) throws PrepareFailure {
        int returnCode = installRequest.getReturnCode();
        String returnMsg = installRequest.getReturnMsg();
        boolean z = true;
        if (installRequest.isInstallMove()) {
            if (returnCode == 1) {
                return;
            }
            this.mRemovePackageHelper.cleanUpForMoveInstall(installRequest.getMoveToUuid(), installRequest.getMovePackageName(), installRequest.getMoveFromCodePath());
            throw new PrepareFailure(returnCode, returnMsg);
        } else if (returnCode != 1) {
            this.mRemovePackageHelper.removeCodePath(installRequest.getCodeFile());
            throw new PrepareFailure(returnCode, returnMsg);
        } else {
            File resolveTargetDir = resolveTargetDir(installRequest.getInstallFlags(), installRequest.getCodeFile());
            File codeFile = installRequest.getCodeFile();
            File nextCodePath = PackageManagerServiceUtils.getNextCodePath(resolveTargetDir, parsedPackage.getPackageName());
            z = (this.mPm.mIncrementalManager == null || !IncrementalManager.isIncrementalPath(codeFile.getAbsolutePath())) ? false : false;
            try {
                PackageManagerServiceUtils.makeDirRecursive(nextCodePath.getParentFile(), 505);
                if (z) {
                    this.mPm.mIncrementalManager.linkCodePath(codeFile, nextCodePath);
                } else {
                    Os.rename(codeFile.getAbsolutePath(), nextCodePath.getAbsolutePath());
                }
                if (!z && !SELinux.restoreconRecursive(nextCodePath)) {
                    Slog.w("PackageManager", "Failed to restorecon");
                    throw new PrepareFailure(-20, "Failed to restorecon");
                }
                installRequest.setCodeFile(nextCodePath);
                try {
                    parsedPackage.setPath(nextCodePath.getCanonicalPath());
                    parsedPackage.setBaseApkPath(FileUtils.rewriteAfterRename(codeFile, nextCodePath, parsedPackage.getBaseApkPath()));
                    parsedPackage.setSplitCodePaths(FileUtils.rewriteAfterRename(codeFile, nextCodePath, parsedPackage.getSplitCodePaths()));
                } catch (IOException e) {
                    Slog.e("PackageManager", "Failed to get path: " + nextCodePath, e);
                    throw new PrepareFailure(-20, "Failed to get path: " + nextCodePath);
                }
            } catch (ErrnoException | IOException e2) {
                Slog.w("PackageManager", "Failed to rename", e2);
                throw new PrepareFailure(-4, "Failed to rename");
            }
        }
    }

    public final File resolveTargetDir(int i, File file) {
        if ((2097152 & i) != 0) {
            return Environment.getDataAppDirectory(null);
        }
        return file.getParentFile();
    }

    public static boolean cannotInstallWithBadPermissionGroups(ParsedPackage parsedPackage) {
        return parsedPackage.getTargetSdkVersion() >= 31;
    }

    public final boolean doesSignatureMatchForPermissions(String str, ParsedPackage parsedPackage, int i) {
        PackageSetting packageLPr;
        KeySetManagerService keySetManagerService;
        SharedUserSetting sharedUserSettingLPr;
        synchronized (this.mPm.mLock) {
            packageLPr = this.mPm.mSettings.getPackageLPr(str);
            keySetManagerService = this.mPm.mSettings.getKeySetManagerService();
            sharedUserSettingLPr = this.mPm.mSettings.getSharedUserSettingLPr(packageLPr);
        }
        SigningDetails signingDetails = packageLPr == null ? SigningDetails.UNKNOWN : packageLPr.getSigningDetails();
        if (str.equals(parsedPackage.getPackageName()) && keySetManagerService.shouldCheckUpgradeKeySetLocked(packageLPr, sharedUserSettingLPr, i)) {
            return keySetManagerService.checkUpgradeKeySetLocked(packageLPr, parsedPackage);
        }
        if (signingDetails.checkCapability(parsedPackage.getSigningDetails(), 4)) {
            return true;
        }
        if (parsedPackage.getSigningDetails().checkCapability(signingDetails, 4)) {
            synchronized (this.mPm.mLock) {
                packageLPr.setSigningDetails(parsedPackage.getSigningDetails());
            }
            return true;
        }
        return false;
    }

    public final void setUpFsVerity(AndroidPackage androidPackage) throws Installer.InstallerException, PrepareFailure, IOException, DigestException, NoSuchAlgorithmException {
        String[] splitCodePaths;
        if (PackageManagerServiceUtils.isApkVerityEnabled()) {
            if (!IncrementalManager.isIncrementalPath(androidPackage.getPath()) || IncrementalManager.getVersion() >= 2) {
                ArrayMap arrayMap = new ArrayMap();
                arrayMap.put(androidPackage.getBaseApkPath(), VerityUtils.getFsveritySignatureFilePath(androidPackage.getBaseApkPath()));
                String buildDexMetadataPathForApk = DexMetadataHelper.buildDexMetadataPathForApk(androidPackage.getBaseApkPath());
                if (new File(buildDexMetadataPathForApk).exists()) {
                    arrayMap.put(buildDexMetadataPathForApk, VerityUtils.getFsveritySignatureFilePath(buildDexMetadataPathForApk));
                }
                for (String str : androidPackage.getSplitCodePaths()) {
                    arrayMap.put(str, VerityUtils.getFsveritySignatureFilePath(str));
                    String buildDexMetadataPathForApk2 = DexMetadataHelper.buildDexMetadataPathForApk(str);
                    if (new File(buildDexMetadataPathForApk2).exists()) {
                        arrayMap.put(buildDexMetadataPathForApk2, VerityUtils.getFsveritySignatureFilePath(buildDexMetadataPathForApk2));
                    }
                }
                FileIntegrityService service = FileIntegrityService.getService();
                for (Map.Entry entry : arrayMap.entrySet()) {
                    try {
                        String str2 = (String) entry.getKey();
                        if (!VerityUtils.hasFsverity(str2)) {
                            String str3 = (String) entry.getValue();
                            if (new File(str3).exists()) {
                                VerityUtils.setUpFsverity(str2);
                                if (!service.verifyPkcs7DetachedSignature(str3, str2)) {
                                    throw new PrepareFailure(-118, "fs-verity signature does not verify against a known key");
                                }
                            } else {
                                continue;
                            }
                        }
                    } catch (IOException e) {
                        throw new PrepareFailure(-118, "Failed to enable fs-verity: " + e);
                    }
                }
            }
        }
    }

    public final PackageFreezer freezePackageForInstall(String str, int i, int i2, String str2, int i3) {
        if ((i2 & IInstalld.FLAG_USE_QUOTA) != 0) {
            return new PackageFreezer(this.mPm);
        }
        return this.mPm.freezePackage(str, i, str2, i3);
    }

    public static void updateDigest(MessageDigest messageDigest, File file) throws IOException {
        DigestInputStream digestInputStream = new DigestInputStream(new FileInputStream(file), messageDigest);
        do {
            try {
            } catch (Throwable th) {
                try {
                    digestInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } while (digestInputStream.read() != -1);
        digestInputStream.close();
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0123  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0142  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x016c A[SYNTHETIC] */
    @GuardedBy({"mPm.mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void commitPackagesLocked(List<ReconciledPackage> list, int[] iArr) {
        PackageSetting packageLPr;
        int i;
        AndroidPackage androidPackage;
        PackageSetting packageLPr2;
        for (ReconciledPackage reconciledPackage : list) {
            InstallRequest installRequest = reconciledPackage.mInstallRequest;
            ParsedPackage parsedPackage = installRequest.getParsedPackage();
            String packageName = parsedPackage.getPackageName();
            RemovePackageHelper removePackageHelper = new RemovePackageHelper(this.mPm);
            DeletePackageHelper deletePackageHelper = new DeletePackageHelper(this.mPm);
            installRequest.onCommitStarted();
            if (installRequest.isInstallReplace()) {
                AndroidPackage androidPackage2 = this.mPm.mPackages.get(packageName);
                PackageStateInternal packageStateInternal = this.mPm.snapshotComputer().getPackageStateInternal(androidPackage2.getPackageName());
                installRequest.setScannedPackageSettingFirstInstallTimeFromReplaced(packageStateInternal, iArr);
                installRequest.setScannedPackageSettingLastUpdateTime(System.currentTimeMillis());
                PackageRemovedInfo removedInfo = installRequest.getRemovedInfo();
                PackageManagerService packageManagerService = this.mPm;
                removedInfo.mBroadcastAllowList = packageManagerService.mAppsFilter.getVisibilityAllowList(packageManagerService.snapshotComputer(), installRequest.getScannedPackageSetting(), iArr, this.mPm.mSettings.getPackagesLocked());
                if (installRequest.isInstallSystem()) {
                    removePackageHelper.removePackage(androidPackage2, true);
                    if (!disableSystemPackageLPw(androidPackage2)) {
                        installRequest.getRemovedInfo().mArgs = new InstallArgs(androidPackage2.getPath(), InstructionSets.getAppDexInstructionSets(packageStateInternal.getPrimaryCpuAbi(), packageStateInternal.getSecondaryCpuAbi()));
                    } else {
                        installRequest.getRemovedInfo().mArgs = null;
                    }
                } else {
                    try {
                        androidPackage = androidPackage2;
                    } catch (SystemDeleteException e) {
                        e = e;
                        androidPackage = androidPackage2;
                    }
                    try {
                        deletePackageHelper.executeDeletePackage(reconciledPackage.mDeletePackageAction, packageName, true, iArr, false);
                    } catch (SystemDeleteException e2) {
                        e = e2;
                        if (this.mPm.mIsEngBuild) {
                            throw new RuntimeException("Unexpected failure", e);
                        }
                        PackageSetting packageLPr3 = this.mPm.mSettings.getPackageLPr(installRequest.getExistingPackageName());
                        if ((installRequest.getInstallFlags() & 1) != 0) {
                        }
                        if (installRequest.getReturnCode() == 1) {
                            installRequest.getRemovedInfo().mRemovedForAllUsers = this.mPm.mPackages.get(packageLPr2.getPackageName()) != null;
                        }
                        updateSettingsLI(commitReconciledScanResultLocked(reconciledPackage, iArr), iArr, installRequest);
                        packageLPr = this.mPm.mSettings.getPackageLPr(packageName);
                        if (packageLPr == null) {
                        }
                        if (installRequest.getReturnCode() != i) {
                        }
                        installRequest.onCommitFinished();
                    }
                    PackageSetting packageLPr32 = this.mPm.mSettings.getPackageLPr(installRequest.getExistingPackageName());
                    if ((installRequest.getInstallFlags() & 1) != 0) {
                        Set<String> oldCodePaths = packageLPr32.getOldCodePaths();
                        if (oldCodePaths == null) {
                            oldCodePaths = new ArraySet<>();
                        }
                        Collections.addAll(oldCodePaths, androidPackage.getBaseApkPath());
                        Collections.addAll(oldCodePaths, androidPackage.getSplitCodePaths());
                        packageLPr32.setOldCodePaths(oldCodePaths);
                    } else {
                        packageLPr32.setOldCodePaths(null);
                    }
                    if (installRequest.getReturnCode() == 1 && (packageLPr2 = this.mPm.mSettings.getPackageLPr(parsedPackage.getPackageName())) != null) {
                        installRequest.getRemovedInfo().mRemovedForAllUsers = this.mPm.mPackages.get(packageLPr2.getPackageName()) != null;
                    }
                }
            }
            updateSettingsLI(commitReconciledScanResultLocked(reconciledPackage, iArr), iArr, installRequest);
            packageLPr = this.mPm.mSettings.getPackageLPr(packageName);
            if (packageLPr == null) {
                i = 1;
                installRequest.setNewUsers(packageLPr.queryInstalledUsers(this.mPm.mUserManager.getUserIds(), true));
                packageLPr.setUpdateAvailable(false);
            } else {
                i = 1;
            }
            if (installRequest.getReturnCode() != i) {
                this.mPm.updateSequenceNumberLP(packageLPr, installRequest.getNewUsers());
                this.mPm.updateInstantAppInstallerLocked(packageName);
            }
            installRequest.onCommitFinished();
        }
        ApplicationPackageManager.invalidateGetPackagesForUidCache();
    }

    @GuardedBy({"mPm.mLock"})
    public final boolean disableSystemPackageLPw(AndroidPackage androidPackage) {
        return this.mPm.mSettings.disableSystemPackageLPw(androidPackage.getPackageName(), true);
    }

    public final void updateSettingsLI(AndroidPackage androidPackage, int[] iArr, InstallRequest installRequest) {
        updateSettingsInternalLI(androidPackage, iArr, installRequest);
    }

    public final void updateSettingsInternalLI(AndroidPackage androidPackage, int[] iArr, InstallRequest installRequest) {
        List<String> allowlistedRestrictedPermissions;
        IncrementalManager incrementalManager;
        PackageSetting packageLPr;
        Trace.traceBegin(262144L, "updateSettings");
        String packageName = androidPackage.getPackageName();
        int[] originUsers = installRequest.getOriginUsers();
        int installReason = installRequest.getInstallReason();
        String sourceInstallerPackageName = installRequest.getSourceInstallerPackageName();
        synchronized (this.mPm.mLock) {
            PackageSetting packageLPr2 = this.mPm.mSettings.getPackageLPr(packageName);
            int userId = installRequest.getUserId();
            if (packageLPr2 != null) {
                int i = -1;
                if (packageLPr2.isSystem()) {
                    if (originUsers != null && !installRequest.isApplicationEnabledSettingPersistent()) {
                        for (int i2 : originUsers) {
                            if (userId == -1 || userId == i2) {
                                packageLPr2.setEnabled(0, i2, sourceInstallerPackageName);
                            }
                        }
                    }
                    if (iArr != null && originUsers != null) {
                        for (int i3 : iArr) {
                            packageLPr2.setInstalled(ArrayUtils.contains(originUsers, i3), i3);
                        }
                    }
                    if (iArr != null) {
                        for (int i4 : iArr) {
                            packageLPr2.resetOverrideComponentLabelIcon(i4);
                        }
                    }
                }
                if (!packageLPr2.getPkgState().getUsesLibraryInfos().isEmpty()) {
                    Iterator<SharedLibraryWrapper> it = packageLPr2.getPkgState().getUsesLibraryInfos().iterator();
                    while (it.hasNext()) {
                        SharedLibraryWrapper next = it.next();
                        int[] userIds = UserManagerService.getInstance().getUserIds();
                        int length = userIds.length;
                        int i5 = 0;
                        while (i5 < length) {
                            int i6 = userIds[i5];
                            Iterator<SharedLibraryWrapper> it2 = it;
                            int[] iArr2 = userIds;
                            if (next.getType() == 1 && (packageLPr = this.mPm.mSettings.getPackageLPr(next.getPackageName())) != null) {
                                packageLPr2.setOverlayPathsForLibrary(next.getName(), packageLPr.getOverlayPaths(i6), i6);
                            }
                            i5++;
                            it = it2;
                            userIds = iArr2;
                            i = -1;
                        }
                    }
                }
                if (userId != i) {
                    packageLPr2.setInstalled(true, userId);
                    if (!installRequest.isApplicationEnabledSettingPersistent()) {
                        packageLPr2.setEnabled(0, userId, sourceInstallerPackageName);
                    }
                } else if (iArr != null) {
                    for (int i7 : iArr) {
                        packageLPr2.setInstalled(true, i7);
                        if (!installRequest.isApplicationEnabledSettingPersistent()) {
                            packageLPr2.setEnabled(0, i7, sourceInstallerPackageName);
                        }
                    }
                }
                this.mPm.mSettings.addInstallerPackageNames(packageLPr2.getInstallSource());
                ArraySet arraySet = new ArraySet();
                if (installRequest.getRemovedInfo() != null && installRequest.getRemovedInfo().mInstallReasons != null) {
                    int size = installRequest.getRemovedInfo().mInstallReasons.size();
                    for (int i8 = 0; i8 < size; i8++) {
                        int keyAt = installRequest.getRemovedInfo().mInstallReasons.keyAt(i8);
                        packageLPr2.setInstallReason(installRequest.getRemovedInfo().mInstallReasons.valueAt(i8), keyAt);
                        arraySet.add(Integer.valueOf(keyAt));
                    }
                }
                if (installRequest.getRemovedInfo() != null && installRequest.getRemovedInfo().mUninstallReasons != null) {
                    for (int i9 = 0; i9 < installRequest.getRemovedInfo().mUninstallReasons.size(); i9++) {
                        packageLPr2.setUninstallReason(installRequest.getRemovedInfo().mUninstallReasons.valueAt(i9), installRequest.getRemovedInfo().mUninstallReasons.keyAt(i9));
                    }
                }
                int[] userIds2 = this.mPm.mUserManager.getUserIds();
                if (userId == -1) {
                    for (int i10 : userIds2) {
                        if (!arraySet.contains(Integer.valueOf(i10)) && packageLPr2.getInstalled(i10)) {
                            packageLPr2.setInstallReason(installReason, i10);
                        }
                    }
                } else if (!arraySet.contains(Integer.valueOf(userId))) {
                    packageLPr2.setInstallReason(installReason, userId);
                }
                String pathString = packageLPr2.getPathString();
                if (IncrementalManager.isIncrementalPath(pathString) && (incrementalManager = this.mIncrementalManager) != null) {
                    incrementalManager.registerLoadingProgressCallback(pathString, new IncrementalProgressListener(packageLPr2.getPackageName(), this.mPm));
                }
                for (int i11 : userIds2) {
                    if (packageLPr2.getInstalled(i11)) {
                        packageLPr2.setUninstallReason(0, i11);
                    }
                }
                this.mPm.mSettings.writeKernelMappingLPr(packageLPr2);
                PermissionManagerServiceInternal.PackageInstalledParams.Builder builder = new PermissionManagerServiceInternal.PackageInstalledParams.Builder();
                if ((installRequest.getInstallFlags() & 256) != 0) {
                    ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
                    List<String> requestedPermissions = androidPackage.getRequestedPermissions();
                    for (int i12 = 0; i12 < requestedPermissions.size(); i12++) {
                        arrayMap.put(requestedPermissions.get(i12), 1);
                    }
                    builder.setPermissionStates(arrayMap);
                } else {
                    ArrayMap<String, Integer> permissionStates = installRequest.getPermissionStates();
                    if (permissionStates != null) {
                        builder.setPermissionStates(permissionStates);
                    }
                }
                if ((installRequest.getInstallFlags() & 4194304) != 0) {
                    allowlistedRestrictedPermissions = androidPackage.getRequestedPermissions();
                } else {
                    allowlistedRestrictedPermissions = installRequest.getAllowlistedRestrictedPermissions();
                }
                if (allowlistedRestrictedPermissions != null) {
                    builder.setAllowlistedRestrictedPermissions(allowlistedRestrictedPermissions);
                }
                builder.setAutoRevokePermissionsMode(installRequest.getAutoRevokePermissionsMode());
                this.mPm.mPermissionManager.onPackageInstalled(androidPackage, installRequest.getPreviousAppId(), builder.build(), userId);
                if (installRequest.getPackageSource() == 3 || installRequest.getPackageSource() == 4) {
                    enableRestrictedSettings(packageName, androidPackage.getUid());
                }
            }
            installRequest.setName(packageName);
            installRequest.setAppId(androidPackage.getUid());
            installRequest.setPkg(androidPackage);
            installRequest.setReturnCode(1);
            Trace.traceBegin(262144L, "writeSettings");
            this.mPm.writeSettingsLPrTEMP();
            Trace.traceEnd(262144L);
        }
        Trace.traceEnd(262144L);
    }

    public final void enableRestrictedSettings(String str, int i) {
        AppOpsManager appOpsManager = (AppOpsManager) this.mPm.mContext.getSystemService(AppOpsManager.class);
        for (int i2 : this.mPm.mUserManager.getUserIds()) {
            appOpsManager.setMode(119, UserHandle.getUid(i2, i), str, 2);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void executePostCommitStepsLIF(List<ReconciledPackage> list) {
        long j;
        ArraySet arraySet = new ArraySet();
        for (ReconciledPackage reconciledPackage : list) {
            InstallRequest installRequest = reconciledPackage.mInstallRequest;
            boolean z = (installRequest.getScanFlags() & IInstalld.FLAG_FORCE) != 0;
            boolean z2 = (installRequest.getScanFlags() & 67108864) != 0;
            AndroidPackageInternal pkg = installRequest.getScannedPackageSetting().getPkg();
            String packageName = pkg.getPackageName();
            String path = pkg.getPath();
            boolean z3 = this.mIncrementalManager != null && IncrementalManager.isIncrementalPath(path);
            if (z3) {
                IncrementalStorage openStorage = this.mIncrementalManager.openStorage(path);
                if (openStorage == null) {
                    throw new IllegalArgumentException("Install: null storage for incremental package " + packageName);
                }
                arraySet.add(openStorage);
            }
            this.mAppDataHelper.prepareAppDataPostCommitLIF(pkg, 0);
            if (installRequest.isClearCodeCache()) {
                this.mAppDataHelper.clearAppDataLIF(pkg, -1, 39);
            }
            if (installRequest.isInstallReplace()) {
                this.mDexManager.notifyPackageUpdated(pkg.getPackageName(), pkg.getBaseApkPath(), pkg.getSplitCodePaths());
            }
            if (!DexOptHelper.useArtService()) {
                try {
                    this.mArtManagerService.prepareAppProfiles((AndroidPackage) pkg, this.mPm.resolveUserIds(installRequest.getUserId()), true);
                } catch (Installer.LegacyDexoptDisabledException e) {
                    throw new RuntimeException(e);
                }
            }
            DexoptOptions dexoptOptions = new DexoptOptions(packageName, this.mDexManager.getCompilationReasonForInstallScenario(installRequest.getInstallScenario()), (installRequest.getInstallReason() == 2 || installRequest.getInstallReason() == 3 ? IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES : 0) | 1029);
            if (((z && Settings.Global.getInt(this.mContext.getContentResolver(), "instant_app_dexopt_enabled", 0) == 0) || pkg.isDebuggable() || z3 || !dexoptOptions.isCompilationEnabled() || z2) ? false : true) {
                if (SystemProperties.getBoolean("pm.precompile_layouts", false)) {
                    Trace.traceBegin(262144L, "compileLayouts");
                    this.mViewCompiler.compileLayouts(pkg);
                    Trace.traceEnd(262144L);
                }
                Trace.traceBegin(262144L, "dexopt");
                PackageSetting realPackageSetting = installRequest.getRealPackageSetting();
                realPackageSetting.getPkgState().setUpdatedSystemApp(installRequest.getScannedPackageSetting().isUpdatedSystemApp());
                if (DexOptHelper.useArtService()) {
                    PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = ((PackageManagerLocal) LocalManagerRegistry.getManager(PackageManagerLocal.class)).withFilteredSnapshot();
                    try {
                        installRequest.onDexoptFinished(DexOptHelper.getArtManagerLocal().dexoptPackage(withFilteredSnapshot, packageName, dexoptOptions.convertToDexoptParams(0)));
                        if (withFilteredSnapshot != null) {
                            withFilteredSnapshot.close();
                        }
                        j = 262144;
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
                } else {
                    try {
                        j = 262144;
                        this.mPackageDexOptimizer.performDexOpt(pkg, realPackageSetting, null, this.mPm.getOrCreateCompilerPackageStats(pkg), this.mDexManager.getPackageUseInfoOrDefault(packageName), dexoptOptions);
                    } catch (Installer.LegacyDexoptDisabledException e2) {
                        throw new RuntimeException(e2);
                    }
                }
                Trace.traceEnd(j);
            }
            if (!DexOptHelper.useArtService()) {
                try {
                    BackgroundDexOptService.getService().notifyPackageChanged(packageName);
                } catch (Installer.LegacyDexoptDisabledException e3) {
                    throw new RuntimeException(e3);
                }
            }
        }
        PackageManagerServiceUtils.waitForNativeBinariesExtractionForIncremental(arraySet);
    }

    public Pair<Integer, String> verifyReplacingVersionCode(PackageInfoLite packageInfoLite, long j, int i) {
        if ((131072 & i) != 0) {
            return verifyReplacingVersionCodeForApex(packageInfoLite, j, i);
        }
        String str = packageInfoLite.packageName;
        synchronized (this.mPm.mLock) {
            AndroidPackage androidPackage = this.mPm.mPackages.get(str);
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(str);
            if (androidPackage == null && packageLPr != null) {
                androidPackage = packageLPr.getPkg();
            }
            if (j != -1) {
                if (androidPackage == null) {
                    String str2 = "Required installed version code was " + j + " but package is not installed";
                    Slog.w("PackageManager", str2);
                    return Pair.create(-121, str2);
                } else if (androidPackage.getLongVersionCode() != j) {
                    String str3 = "Required installed version code was " + j + " but actual installed version is " + androidPackage.getLongVersionCode();
                    Slog.w("PackageManager", str3);
                    return Pair.create(-121, str3);
                }
            }
            if (androidPackage != null && !androidPackage.isSdkLibrary()) {
                if (!PackageManagerServiceUtils.isDowngradePermitted(i, androidPackage.isDebuggable())) {
                    try {
                        PackageManagerServiceUtils.checkDowngrade(androidPackage, packageInfoLite);
                    } catch (PackageManagerException e) {
                        String str4 = "Downgrade detected: " + e.getMessage();
                        Slog.w("PackageManager", str4);
                        return Pair.create(-25, str4);
                    }
                } else if (packageLPr.isSystem()) {
                    PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(packageLPr);
                    if (disabledSystemPkgLPr != null) {
                        androidPackage = disabledSystemPkgLPr.getPkg();
                    }
                    if (!Build.IS_DEBUGGABLE && !androidPackage.isDebuggable()) {
                        try {
                            PackageManagerServiceUtils.checkDowngrade(androidPackage, packageInfoLite);
                        } catch (PackageManagerException e2) {
                            String str5 = "System app: " + str + " cannot be downgraded to older than its preloaded version on the system image. " + e2.getMessage();
                            Slog.w("PackageManager", str5);
                            return Pair.create(-25, str5);
                        }
                    }
                }
            }
            return Pair.create(1, null);
        }
    }

    public final Pair<Integer, String> verifyReplacingVersionCodeForApex(PackageInfoLite packageInfoLite, long j, int i) {
        String str = packageInfoLite.packageName;
        PackageInfo packageInfo = this.mPm.snapshotComputer().getPackageInfo(str, 1073741824L, 0);
        if (packageInfo == null) {
            String str2 = "Attempting to install new APEX package " + str;
            Slog.w("PackageManager", str2);
            return Pair.create(-23, str2);
        }
        long longVersionCode = packageInfo.getLongVersionCode();
        if (j != -1 && longVersionCode != j) {
            String str3 = "Installed version of APEX package " + str + " does not match required. Active version: " + longVersionCode + " required: " + j;
            Slog.w("PackageManager", str3);
            return Pair.create(-121, str3);
        }
        boolean z = (packageInfo.applicationInfo.flags & 2) != 0;
        long longVersionCode2 = packageInfoLite.getLongVersionCode();
        if (!PackageManagerServiceUtils.isDowngradePermitted(i, z) && longVersionCode2 < longVersionCode) {
            String str4 = "Downgrade of APEX package " + str + " is not allowed. Active version: " + longVersionCode + " attempted: " + longVersionCode2;
            Slog.w("PackageManager", str4);
            return Pair.create(-25, str4);
        }
        return Pair.create(1, null);
    }

    public int getUidForVerifier(VerifierInfo verifierInfo) {
        synchronized (this.mPm.mLock) {
            AndroidPackage androidPackage = this.mPm.mPackages.get(verifierInfo.packageName);
            if (androidPackage == null) {
                return -1;
            }
            if (androidPackage.getSigningDetails().getSignatures().length != 1) {
                Slog.i("PackageManager", "Verifier package " + verifierInfo.packageName + " has more than one signature; ignoring");
                return -1;
            }
            try {
                if (!Arrays.equals(verifierInfo.publicKey.getEncoded(), androidPackage.getSigningDetails().getSignatures()[0].getPublicKey().getEncoded())) {
                    Slog.i("PackageManager", "Verifier package " + verifierInfo.packageName + " does not have the expected public key; ignoring");
                    return -1;
                }
                return androidPackage.getUid();
            } catch (CertificateException unused) {
                return -1;
            }
        }
    }

    public void sendPendingBroadcasts() {
        synchronized (this.mPm.mLock) {
            SparseArray<ArrayMap<String, ArrayList<String>>> copiedMap = this.mPm.mPendingBroadcasts.copiedMap();
            int size = copiedMap.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                i += copiedMap.valueAt(i2).size();
            }
            if (i == 0) {
                return;
            }
            String[] strArr = new String[i];
            ArrayList<String>[] arrayListArr = new ArrayList[i];
            int[] iArr = new int[i];
            int i3 = 0;
            for (int i4 = 0; i4 < size; i4++) {
                int keyAt = copiedMap.keyAt(i4);
                ArrayMap<String, ArrayList<String>> valueAt = copiedMap.valueAt(i4);
                int size2 = CollectionUtils.size(valueAt);
                for (int i5 = 0; i5 < size2; i5++) {
                    strArr[i3] = valueAt.keyAt(i5);
                    arrayListArr[i3] = valueAt.valueAt(i5);
                    PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(strArr[i3]);
                    iArr[i3] = packageLPr != null ? UserHandle.getUid(keyAt, packageLPr.getAppId()) : -1;
                    i3++;
                }
            }
            this.mPm.mPendingBroadcasts.clear();
            Computer snapshotComputer = this.mPm.snapshotComputer();
            for (int i6 = 0; i6 < i3; i6++) {
                this.mPm.sendPackageChangedBroadcast(snapshotComputer, strArr[i6], true, arrayListArr[i6], iArr[i6], null);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:132:0x03a1  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x03f9  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0455  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x0489  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0490  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0494  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x04a6  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x04b7  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x012f  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01a2  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01e0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handlePackagePostInstall(InstallRequest installRequest, boolean z) {
        boolean z2;
        boolean z3;
        String str;
        String str2;
        int[] iArr;
        boolean z4;
        int length;
        int i;
        Bundle bundle;
        String str3;
        InstallArgs installArgs;
        InstallArgs installArgs2;
        SparseArray<int[]> visibilityAllowList;
        String[] strArr;
        int packageExternalStorageType;
        String[] strArr2;
        PackageStateInternal packageStateInternal;
        int[] iArr2;
        boolean z5;
        boolean z6;
        boolean z7 = (installRequest.getInstallFlags() & IInstalld.FLAG_USE_QUOTA) == 0;
        boolean z8 = (installRequest.getInstallFlags() & 65536) != 0;
        String installerPackageName = installRequest.getInstallerPackageName();
        int dataLoaderType = installRequest.getDataLoaderType();
        boolean z9 = installRequest.getReturnCode() == 1;
        boolean isUpdate = installRequest.isUpdate();
        String name = installRequest.getName();
        PackageStateInternal packageStateInternal2 = z9 ? this.mPm.snapshotComputer().getPackageStateInternal(name) : null;
        boolean z10 = packageStateInternal2 == null || (packageStateInternal2.isSystem() && !packageStateInternal2.getPath().getPath().equals(installRequest.getPkg().getPath()));
        if (z9 && z10) {
            Slog.e("PackageManager", name + " was removed before handlePackagePostInstall could be executed");
            installRequest.setReturnCode(-23);
            installRequest.setReturnMessage("Package was removed before install could complete.");
            this.mRemovePackageHelper.cleanUpResources(installRequest.getOldCodeFile(), installRequest.getOldInstructionSet());
            this.mPm.notifyInstallObserver(installRequest);
            return;
        }
        if (z9) {
            this.mPm.mPerUidReadTimeoutsCache = null;
            if (installRequest.getRemovedInfo() != null) {
                if (installRequest.getRemovedInfo().mIsExternal) {
                    String[] strArr3 = {installRequest.getRemovedInfo().mRemovedPackage};
                    int[] iArr3 = {installRequest.getRemovedInfo().mUid};
                    BroadcastHelper broadcastHelper = this.mBroadcastHelper;
                    PackageManagerService packageManagerService = this.mPm;
                    Objects.requireNonNull(packageManagerService);
                    broadcastHelper.sendResourcesChangedBroadcast(new InstallPackageHelper$$ExternalSyntheticLambda1(packageManagerService), false, true, strArr3, iArr3);
                }
                installRequest.getRemovedInfo().sendPackageRemovedBroadcasts(z7, false);
            }
            if (installRequest.getInstallerPackageName() != null) {
                str2 = installRequest.getInstallerPackageName();
            } else if (installRequest.getRemovedInfo() != null) {
                str2 = installRequest.getRemovedInfo().mInstallerPackageName;
            } else {
                str = null;
                this.mPm.notifyInstantAppPackageInstalled(installRequest.getPkg().getPackageName(), installRequest.getNewUsers());
                iArr = PackageManagerService.EMPTY_INT_ARRAY;
                z4 = installRequest.getOriginUsers() != null || installRequest.getOriginUsers().length == 0;
                int[] newUsers = installRequest.getNewUsers();
                length = newUsers.length;
                i = 0;
                int[] iArr4 = iArr;
                int[] iArr5 = iArr4;
                int[] iArr6 = iArr5;
                while (i < length) {
                    int i2 = length;
                    int i3 = newUsers[i];
                    boolean isInstantApp = packageStateInternal2.getUserStateOrDefault(i3).isInstantApp();
                    if (z4) {
                        if (isInstantApp) {
                            iArr5 = ArrayUtils.appendInt(iArr5, i3);
                        } else {
                            iArr = ArrayUtils.appendInt(iArr, i3);
                        }
                        packageStateInternal = packageStateInternal2;
                        iArr2 = newUsers;
                        z5 = z9;
                    } else {
                        packageStateInternal = packageStateInternal2;
                        int[] originUsers = installRequest.getOriginUsers();
                        iArr2 = newUsers;
                        int length2 = originUsers.length;
                        z5 = z9;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= length2) {
                                z6 = true;
                                break;
                            }
                            int i5 = length2;
                            if (originUsers[i4] == i3) {
                                z6 = false;
                                break;
                            } else {
                                i4++;
                                length2 = i5;
                            }
                        }
                        if (z6) {
                            if (isInstantApp) {
                                iArr5 = ArrayUtils.appendInt(iArr5, i3);
                            } else {
                                iArr = ArrayUtils.appendInt(iArr, i3);
                            }
                        } else if (isInstantApp) {
                            iArr4 = ArrayUtils.appendInt(iArr4, i3);
                        } else {
                            iArr6 = ArrayUtils.appendInt(iArr6, i3);
                        }
                    }
                    i++;
                    length = i2;
                    packageStateInternal2 = packageStateInternal;
                    newUsers = iArr2;
                    z9 = z5;
                }
                z3 = z9;
                bundle = new Bundle();
                bundle.putInt("android.intent.extra.UID", installRequest.getAppId());
                if (isUpdate) {
                    bundle.putBoolean("android.intent.extra.REPLACING", true);
                }
                bundle.putInt("android.content.pm.extra.DATA_LOADER_TYPE", dataLoaderType);
                if (str != null && installRequest.getPkg().getStaticSharedLibraryName() != null) {
                    this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", name, bundle, 0, str, null, installRequest.getNewUsers(), null, null, null);
                }
                if (installRequest.getPkg().getStaticSharedLibraryName() != null) {
                    this.mPm.mProcessLoggingHandler.invalidateBaseApkHash(installRequest.getPkg().getBaseApkPath());
                    int appId = UserHandle.getAppId(installRequest.getAppId());
                    boolean isInstallSystem = installRequest.isInstallSystem();
                    PackageManagerService packageManagerService2 = this.mPm;
                    Computer snapshotComputer = packageManagerService2.snapshotComputer();
                    int[] iArr7 = iArr;
                    z2 = z7;
                    String str4 = str;
                    installArgs = null;
                    str3 = name;
                    packageManagerService2.sendPackageAddedForNewUsers(snapshotComputer, name, isInstallSystem || z8, z8, appId, iArr7, iArr5, dataLoaderType);
                    synchronized (this.mPm.mLock) {
                        Computer snapshotComputer2 = this.mPm.snapshotComputer();
                        visibilityAllowList = this.mPm.mAppsFilter.getVisibilityAllowList(snapshotComputer2, snapshotComputer2.getPackageStateInternal(str3, 1000), iArr6, this.mPm.mSettings.getPackagesLocked());
                    }
                    this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str3, bundle, 0, null, null, iArr6, iArr4, visibilityAllowList, null);
                    if (str4 != null) {
                        this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str3, bundle, 0, str4, null, iArr6, iArr4, null, null);
                    }
                    if (BroadcastHelper.isPrivacySafetyLabelChangeNotificationsEnabled()) {
                        PackageManagerService packageManagerService3 = this.mPm;
                        packageManagerService3.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str3, bundle, 0, packageManagerService3.mRequiredPermissionControllerPackage, null, iArr6, iArr4, null, null);
                    }
                    for (String str5 : this.mPm.mRequiredVerifierPackages) {
                        if (str5 != null && !str5.equals(str4)) {
                            this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str3, bundle, 0, str5, null, iArr6, iArr4, null, null);
                        }
                    }
                    PackageManagerService packageManagerService4 = this.mPm;
                    String str6 = packageManagerService4.mRequiredInstallerPackage;
                    if (str6 != null) {
                        packageManagerService4.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str3, bundle, 16777216, str6, null, iArr7, iArr4, null, null);
                    }
                    if (isUpdate) {
                        this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_REPLACED", str3, bundle, 0, null, null, iArr6, iArr4, installRequest.getRemovedInfo().mBroadcastAllowList, null);
                        if (str4 != null) {
                            this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_REPLACED", str3, bundle, 0, str4, null, iArr6, iArr4, null, null);
                        }
                        for (String str7 : this.mPm.mRequiredVerifierPackages) {
                            if (str7 != null && !str7.equals(str4)) {
                                this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_REPLACED", str3, bundle, 0, str7, null, iArr6, iArr4, null, null);
                            }
                        }
                        this.mPm.sendPackageBroadcast("android.intent.action.MY_PACKAGE_REPLACED", null, null, 0, str3, null, iArr6, iArr4, null, this.mBroadcastHelper.getTemporaryAppAllowlistBroadcastOptions(FrameworkStatsLog.f93x9057b857).toBundle());
                    } else if (z && !installRequest.isInstallSystem()) {
                        iArr = iArr7;
                        this.mBroadcastHelper.sendFirstLaunchBroadcast(str3, installerPackageName, iArr, iArr5);
                        if (installRequest.getPkg().isExternalStorage()) {
                            if (!isUpdate && (packageExternalStorageType = PackageManagerServiceUtils.getPackageExternalStorageType(((StorageManager) this.mInjector.getSystemService(StorageManager.class)).findVolumeByUuid(StorageManager.convert(installRequest.getPkg().getVolumeUuid()).toString()), installRequest.getPkg().isExternalStorage())) != 0) {
                                FrameworkStatsLog.write(181, packageExternalStorageType, str3);
                            }
                            int[] iArr8 = {installRequest.getPkg().getUid()};
                            BroadcastHelper broadcastHelper2 = this.mBroadcastHelper;
                            PackageManagerService packageManagerService5 = this.mPm;
                            Objects.requireNonNull(packageManagerService5);
                            broadcastHelper2.sendResourcesChangedBroadcast(new InstallPackageHelper$$ExternalSyntheticLambda1(packageManagerService5), true, true, new String[]{str3}, iArr8);
                        }
                    }
                    iArr = iArr7;
                    if (installRequest.getPkg().isExternalStorage()) {
                    }
                } else {
                    z2 = z7;
                    str3 = name;
                    installArgs = null;
                    if (!ArrayUtils.isEmpty(installRequest.getLibraryConsumers())) {
                        Computer snapshotComputer3 = this.mPm.snapshotComputer();
                        boolean z11 = (isUpdate || installRequest.getPkg().getStaticSharedLibraryName() == null) ? false : true;
                        for (int i6 = 0; i6 < installRequest.getLibraryConsumers().size(); i6++) {
                            AndroidPackage androidPackage = installRequest.getLibraryConsumers().get(i6);
                            this.mPm.sendPackageChangedBroadcast(snapshotComputer3, androidPackage.getPackageName(), z11, new ArrayList<>(Collections.singletonList(androidPackage.getPackageName())), androidPackage.getUid(), null);
                        }
                    }
                }
                if (iArr.length > 0) {
                    for (int i7 : iArr) {
                        this.mPm.restorePermissionsAndUpdateRolesForNewUserInstall(str3, i7);
                    }
                }
                if (!z4 && !isUpdate) {
                    this.mPm.notifyPackageAdded(str3, installRequest.getAppId());
                } else {
                    this.mPm.notifyPackageChanged(str3, installRequest.getAppId());
                }
                EventLog.writeEvent(3110, getUnknownSourcesSettings());
                installArgs2 = installRequest.getRemovedInfo() == null ? installRequest.getRemovedInfo().mArgs : installArgs;
                if (installArgs2 != null) {
                    VMRuntime.getRuntime().requestConcurrentGC();
                } else if (!z2) {
                    this.mPm.scheduleDeferredNoKillPostDelete(installArgs2);
                } else {
                    this.mRemovePackageHelper.cleanUpResources(installArgs2.mCodeFile, installArgs2.mInstructionSets);
                }
                Computer snapshotComputer4 = this.mPm.snapshotComputer();
                for (int i8 : iArr) {
                    PackageInfo packageInfo = snapshotComputer4.getPackageInfo(str3, 0L, i8);
                    if (packageInfo != null) {
                        this.mDexManager.notifyPackageInstalled(packageInfo, i8);
                    }
                }
            }
            str = str2;
            this.mPm.notifyInstantAppPackageInstalled(installRequest.getPkg().getPackageName(), installRequest.getNewUsers());
            iArr = PackageManagerService.EMPTY_INT_ARRAY;
            if (installRequest.getOriginUsers() != null) {
            }
            int[] newUsers2 = installRequest.getNewUsers();
            length = newUsers2.length;
            i = 0;
            int[] iArr42 = iArr;
            int[] iArr52 = iArr42;
            int[] iArr62 = iArr52;
            while (i < length) {
            }
            z3 = z9;
            bundle = new Bundle();
            bundle.putInt("android.intent.extra.UID", installRequest.getAppId());
            if (isUpdate) {
            }
            bundle.putInt("android.content.pm.extra.DATA_LOADER_TYPE", dataLoaderType);
            if (str != null) {
                this.mPm.sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", name, bundle, 0, str, null, installRequest.getNewUsers(), null, null, null);
            }
            if (installRequest.getPkg().getStaticSharedLibraryName() != null) {
            }
            if (iArr.length > 0) {
            }
            if (!z4) {
            }
            this.mPm.notifyPackageChanged(str3, installRequest.getAppId());
            EventLog.writeEvent(3110, getUnknownSourcesSettings());
            if (installRequest.getRemovedInfo() == null) {
            }
            if (installArgs2 != null) {
            }
            Computer snapshotComputer42 = this.mPm.snapshotComputer();
            while (r3 < r2) {
            }
        } else {
            z2 = z7;
            z3 = z9;
        }
        if (!(z3 && isUpdate)) {
            this.mPm.notifyInstallObserver(installRequest);
        } else if (z2) {
            this.mPm.scheduleDeferredPendingKillInstallObserver(installRequest);
        } else {
            this.mPm.scheduleDeferredNoKillInstallObserver(installRequest);
        }
        this.mPm.schedulePruneUnusedStaticSharedLibraries(true);
        if (installRequest.getTraceMethod() != null) {
            Trace.asyncTraceEnd(262144L, installRequest.getTraceMethod(), installRequest.getTraceCookie());
        }
    }

    public final int getUnknownSourcesSettings() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "install_non_market_apps", -1, 0);
    }

    @GuardedBy({"mPm.mLock", "mPm.mInstallLock"})
    public void installSystemStubPackages(List<String> list, int i) {
        int size = list.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            String str = list.get(size);
            if (this.mPm.mSettings.isDisabledSystemPackageLPr(str)) {
                list.remove(size);
            } else {
                AndroidPackage androidPackage = this.mPm.mPackages.get(str);
                if (androidPackage == null) {
                    list.remove(size);
                } else {
                    PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(str);
                    if (packageLPr != null && packageLPr.getEnabled(0) == 3) {
                        list.remove(size);
                    } else {
                        try {
                            installStubPackageLI(androidPackage, 0, i);
                            packageLPr.setEnabled(0, 0, PackageManagerShellCommandDataLoader.PACKAGE);
                            list.remove(size);
                        } catch (PackageManagerException e) {
                            Slog.e("PackageManager", "Failed to parse uncompressed system package: " + e.getMessage());
                        }
                    }
                }
            }
        }
        for (int size2 = list.size() - 1; size2 >= 0; size2 += -1) {
            String str2 = list.get(size2);
            this.mPm.mSettings.getPackageLPr(str2).setEnabled(2, 0, PackageManagerShellCommandDataLoader.PACKAGE);
            PackageManagerServiceUtils.logCriticalInfo(6, "Stub disabled; pkg: " + str2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x006c A[Catch: all -> 0x009a, PackageManagerException -> 0x009f, TRY_ENTER, TRY_LEAVE, TryCatch #12 {PackageManagerException -> 0x009f, blocks: (B:23:0x006c, B:38:0x0099, B:37:0x0096), top: B:114:0x0026 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean enableCompressedPackage(AndroidPackage androidPackage, PackageSetting packageSetting) {
        PackageFreezer freezePackage;
        PackageManagerTracedLock packageManagerTracedLock;
        int defParseFlags = this.mPm.getDefParseFlags() | Integer.MIN_VALUE | 64;
        synchronized (this.mPm.mInstallLock) {
            try {
                freezePackage = this.mPm.freezePackage(androidPackage.getPackageName(), -1, "setEnabledSetting", 16);
            } catch (PackageManagerException unused) {
            }
            try {
                try {
                    AndroidPackage installStubPackageLI = installStubPackageLI(androidPackage, defParseFlags, 0);
                    this.mAppDataHelper.prepareAppDataAfterInstallLIF(installStubPackageLI);
                    PackageManagerTracedLock packageManagerTracedLock2 = this.mPm.mLock;
                    try {
                        synchronized (packageManagerTracedLock2) {
                            try {
                                packageManagerTracedLock = packageManagerTracedLock2;
                                try {
                                    this.mSharedLibraries.updateSharedLibraries(installStubPackageLI, packageSetting, null, null, Collections.unmodifiableMap(this.mPm.mPackages));
                                } catch (PackageManagerException e) {
                                    e = e;
                                    Slog.w("PackageManager", "updateAllSharedLibrariesLPw failed: ", e);
                                    this.mPm.mPermissionManager.onPackageInstalled(installStubPackageLI, -1, PermissionManagerServiceInternal.PackageInstalledParams.DEFAULT, -1);
                                    this.mPm.writeSettingsLPrTEMP();
                                    if (freezePackage != null) {
                                    }
                                    this.mAppDataHelper.clearAppDataLIF(installStubPackageLI, -1, 39);
                                    this.mDexManager.notifyPackageUpdated(installStubPackageLI.getPackageName(), installStubPackageLI.getBaseApkPath(), installStubPackageLI.getSplitCodePaths());
                                    return true;
                                }
                            } catch (PackageManagerException e2) {
                                e = e2;
                                packageManagerTracedLock = packageManagerTracedLock2;
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                            this.mPm.mPermissionManager.onPackageInstalled(installStubPackageLI, -1, PermissionManagerServiceInternal.PackageInstalledParams.DEFAULT, -1);
                            this.mPm.writeSettingsLPrTEMP();
                            if (freezePackage != null) {
                                freezePackage.close();
                            }
                            this.mAppDataHelper.clearAppDataLIF(installStubPackageLI, -1, 39);
                            this.mDexManager.notifyPackageUpdated(installStubPackageLI.getPackageName(), installStubPackageLI.getBaseApkPath(), installStubPackageLI.getSplitCodePaths());
                            return true;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                    throw th;
                } finally {
                }
            } catch (PackageManagerException unused2) {
                try {
                    freezePackage = this.mPm.freezePackage(androidPackage.getPackageName(), -1, "setEnabledSetting", 16);
                } catch (PackageManagerException e3) {
                    Slog.wtf("PackageManager", "Failed to restore system package:" + androidPackage.getPackageName(), e3);
                    synchronized (this.mPm.mLock) {
                        PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
                        if (packageLPr != null) {
                            packageLPr.setEnabled(2, 0, PackageManagerShellCommandDataLoader.PACKAGE);
                        }
                        this.mPm.writeSettingsLPrTEMP();
                    }
                }
                try {
                    synchronized (this.mPm.mLock) {
                        this.mPm.mSettings.enableSystemPackageLPw(androidPackage.getPackageName());
                    }
                    installPackageFromSystemLIF(androidPackage.getPath(), this.mPm.mUserManager.getUserIds(), null, true);
                    if (freezePackage != null) {
                        freezePackage.close();
                    }
                    synchronized (this.mPm.mLock) {
                        PackageSetting packageLPr2 = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
                        if (packageLPr2 != null) {
                            packageLPr2.setEnabled(2, 0, PackageManagerShellCommandDataLoader.PACKAGE);
                        }
                        this.mPm.writeSettingsLPrTEMP();
                    }
                    return false;
                } finally {
                }
            }
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final AndroidPackage installStubPackageLI(AndroidPackage androidPackage, int i, int i2) throws PackageManagerException {
        if (PackageManagerService.DEBUG_COMPRESSION) {
            Slog.i("PackageManager", "Uncompressing system stub; pkg: " + androidPackage.getPackageName());
        }
        File decompressPackage = decompressPackage(androidPackage.getPackageName(), androidPackage.getPath());
        if (decompressPackage == null) {
            throw PackageManagerException.ofInternalError("Unable to decompress stub at " + androidPackage.getPath(), -11);
        }
        synchronized (this.mPm.mLock) {
            this.mPm.mSettings.disableSystemPackageLPw(androidPackage.getPackageName(), true);
        }
        RemovePackageHelper removePackageHelper = new RemovePackageHelper(this.mPm);
        removePackageHelper.removePackage(androidPackage, true);
        try {
            return scanSystemPackageTracedLI(decompressPackage, i, i2, null);
        } catch (PackageManagerException e) {
            Slog.w("PackageManager", "Failed to install compressed system package:" + androidPackage.getPackageName(), e);
            removePackageHelper.removeCodePath(decompressPackage);
            throw e;
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final File decompressPackage(String str, String str2) {
        if (!PackageManagerServiceUtils.compressedFileExists(str2)) {
            if (PackageManagerService.DEBUG_COMPRESSION) {
                Slog.i("PackageManager", "No files to decompress at: " + str2);
            }
            return null;
        }
        File nextCodePath = PackageManagerServiceUtils.getNextCodePath(Environment.getDataAppDirectory(null), str);
        int decompressFiles = PackageManagerServiceUtils.decompressFiles(str2, nextCodePath, str);
        if (decompressFiles == 1) {
            decompressFiles = PackageManagerServiceUtils.extractNativeBinaries(nextCodePath, str);
        }
        if (decompressFiles == 1) {
            if (!this.mPm.isSystemReady()) {
                PackageManagerService packageManagerService = this.mPm;
                if (packageManagerService.mReleaseOnSystemReady == null) {
                    packageManagerService.mReleaseOnSystemReady = new ArrayList();
                }
                this.mPm.mReleaseOnSystemReady.add(nextCodePath);
            } else {
                F2fsUtils.releaseCompressedBlocks(this.mContext.getContentResolver(), nextCodePath);
            }
            return nextCodePath;
        } else if (nextCodePath.exists()) {
            new RemovePackageHelper(this.mPm).removeCodePath(nextCodePath);
            return null;
        } else {
            return null;
        }
    }

    public void restoreDisabledSystemPackageLIF(DeletePackageAction deletePackageAction, int[] iArr, boolean z) throws SystemDeleteException {
        PackageSetting packageSetting = deletePackageAction.mDeletingPs;
        PackageRemovedInfo packageRemovedInfo = deletePackageAction.mRemovedInfo;
        PackageSetting packageSetting2 = deletePackageAction.mDisabledPs;
        synchronized (this.mPm.mLock) {
            this.mPm.mSettings.enableSystemPackageLPw(packageSetting2.getPkg().getPackageName());
            PackageManagerServiceUtils.removeNativeBinariesLI(packageSetting);
        }
        try {
            try {
                synchronized (this.mPm.mInstallLock) {
                    installPackageFromSystemLIF(packageSetting2.getPathString(), iArr, packageRemovedInfo == null ? null : packageRemovedInfo.mOrigUsers, z);
                }
                if (packageSetting2.getPkg().isStub()) {
                    synchronized (this.mPm.mLock) {
                        disableStubPackage(deletePackageAction, packageSetting, iArr);
                    }
                }
            } catch (PackageManagerException e) {
                Slog.w("PackageManager", "Failed to restore system package:" + packageSetting.getPackageName() + ": " + e.getMessage());
                throw new SystemDeleteException(e);
            }
        } catch (Throwable th) {
            if (packageSetting2.getPkg().isStub()) {
                synchronized (this.mPm.mLock) {
                    disableStubPackage(deletePackageAction, packageSetting, iArr);
                }
            }
            throw th;
        }
    }

    @GuardedBy({"mPm.mLock"})
    public final void disableStubPackage(DeletePackageAction deletePackageAction, PackageSetting packageSetting, int[] iArr) {
        PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(packageSetting.getPackageName());
        if (packageLPr != null) {
            UserHandle userHandle = deletePackageAction.mUser;
            int identifier = userHandle == null ? -1 : userHandle.getIdentifier();
            if (identifier != -1) {
                if (identifier >= 0) {
                    packageLPr.setEnabled(2, identifier, PackageManagerShellCommandDataLoader.PACKAGE);
                    return;
                }
                return;
            }
            for (int i : iArr) {
                packageLPr.setEnabled(2, i, PackageManagerShellCommandDataLoader.PACKAGE);
            }
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void installPackageFromSystemLIF(String str, int[] iArr, int[] iArr2, boolean z) throws PackageManagerException {
        File file = new File(str);
        AndroidPackage scanSystemPackageTracedLI = scanSystemPackageTracedLI(file, this.mPm.getDefParseFlags() | 1 | 16, this.mPm.getSystemPackageScanFlags(file), null);
        synchronized (this.mPm.mLock) {
            try {
                this.mSharedLibraries.updateSharedLibraries(scanSystemPackageTracedLI, this.mPm.mSettings.getPackageLPr(scanSystemPackageTracedLI.getPackageName()), null, null, Collections.unmodifiableMap(this.mPm.mPackages));
            } catch (PackageManagerException e) {
                Slog.e("PackageManager", "updateAllSharedLibrariesLPw failed: " + e.getMessage());
            }
        }
        this.mAppDataHelper.prepareAppDataAfterInstallLIF(scanSystemPackageTracedLI);
        setPackageInstalledForSystemPackage(scanSystemPackageTracedLI, iArr, iArr2, z);
    }

    public final void setPackageInstalledForSystemPackage(AndroidPackage androidPackage, int[] iArr, int[] iArr2, boolean z) {
        synchronized (this.mPm.mLock) {
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
            boolean z2 = iArr2 != null;
            if (z2) {
                boolean z3 = false;
                for (int i : iArr) {
                    boolean contains = ArrayUtils.contains(iArr2, i);
                    if (contains != packageLPr.getInstalled(i)) {
                        z3 = true;
                    }
                    packageLPr.setInstalled(contains, i);
                    if (contains) {
                        packageLPr.setUninstallReason(0, i);
                    }
                }
                this.mPm.mSettings.writeAllUsersPackageRestrictionsLPr();
                if (z3) {
                    this.mPm.mSettings.writeKernelMappingLPr(packageLPr);
                }
            }
            this.mPm.mPermissionManager.onPackageInstalled(androidPackage, -1, PermissionManagerServiceInternal.PackageInstalledParams.DEFAULT, -1);
            for (int i2 : iArr) {
                if (z2) {
                    this.mPm.mSettings.writePermissionStateForUserLPr(i2, false);
                }
            }
            if (z) {
                this.mPm.writeSettingsLPrTEMP();
            }
        }
    }

    @GuardedBy({"mPm.mLock"})
    public void prepareSystemPackageCleanUp(WatchedArrayMap<String, PackageSetting> watchedArrayMap, List<String> list, ArrayMap<String, File> arrayMap, final int[] iArr) {
        for (int size = watchedArrayMap.size() - 1; size >= 0; size--) {
            final PackageSetting valueAt = watchedArrayMap.valueAt(size);
            String packageName = valueAt.getPackageName();
            if (valueAt.isSystem()) {
                AndroidPackage androidPackage = this.mPm.mPackages.get(packageName);
                PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(packageName);
                if (androidPackage != null) {
                    if (!androidPackage.isApex() && disabledSystemPkgLPr != null) {
                        PackageManagerServiceUtils.logCriticalInfo(5, "Expecting better updated system app for " + packageName + "; removing system app.  Last known codePath=" + valueAt.getPathString() + ", versionCode=" + valueAt.getVersionCode() + "; scanned versionCode=" + androidPackage.getLongVersionCode());
                        this.mRemovePackageHelper.removePackage(androidPackage, true);
                        arrayMap.put(valueAt.getPackageName(), valueAt.getPath());
                    }
                } else if (disabledSystemPkgLPr == null) {
                    PackageManagerServiceUtils.logCriticalInfo(5, "System package " + packageName + " no longer exists; its data will be wiped");
                    this.mInjector.getHandler().post(new Runnable() { // from class: com.android.server.pm.InstallPackageHelper$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            InstallPackageHelper.this.lambda$prepareSystemPackageCleanUp$1(valueAt, iArr);
                        }
                    });
                } else if (disabledSystemPkgLPr.getPath() == null || !disabledSystemPkgLPr.getPath().exists() || disabledSystemPkgLPr.getPkg() == null) {
                    list.add(packageName);
                } else {
                    arrayMap.put(disabledSystemPkgLPr.getPackageName(), disabledSystemPkgLPr.getPath());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareSystemPackageCleanUp$1(PackageSetting packageSetting, int[] iArr) {
        this.mRemovePackageHelper.removePackageData(packageSetting, iArr, null, 0, false);
    }

    @GuardedBy({"mPm.mLock"})
    public void cleanupDisabledPackageSettings(List<String> list, int[] iArr, int i) {
        String str;
        for (int size = list.size() - 1; size >= 0; size--) {
            String str2 = list.get(size);
            AndroidPackage androidPackage = this.mPm.mPackages.get(str2);
            this.mPm.mSettings.removeDisabledSystemPackageLPw(str2);
            if (androidPackage == null) {
                str = "Updated system package " + str2 + " no longer exists; removing its data";
            } else {
                String str3 = "Updated system package " + str2 + " no longer exists; rescanning package on data";
                this.mRemovePackageHelper.removePackage(androidPackage, true);
                try {
                    File file = new File(androidPackage.getPath());
                    synchronized (this.mPm.mInstallLock) {
                        scanSystemPackageTracedLI(file, 0, i, null);
                    }
                } catch (PackageManagerException e) {
                    Slog.e("PackageManager", "Failed to parse updated, ex-system package: " + e.getMessage());
                }
                str = str3;
            }
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(str2);
            if (packageLPr != null && this.mPm.mPackages.get(str2) == null) {
                this.mRemovePackageHelper.removePackageData(packageLPr, iArr, null, 0, false);
            }
            PackageManagerServiceUtils.logCriticalInfo(5, str);
        }
    }

    @GuardedBy({"mPm.mInstallLock", "mPm.mLock"})
    public List<ApexManager.ScanResult> scanApexPackages(ApexInfo[] apexInfoArr, int i, int i2, PackageParser2 packageParser2, ExecutorService executorService) {
        int i3;
        int i4;
        if (apexInfoArr == null) {
            return Collections.EMPTY_LIST;
        }
        ParallelPackageParser parallelPackageParser = new ParallelPackageParser(packageParser2, executorService);
        final ArrayMap arrayMap = new ArrayMap();
        for (ApexInfo apexInfo : apexInfoArr) {
            File file = new File(apexInfo.modulePath);
            parallelPackageParser.submit(file, i);
            arrayMap.put(file, apexInfo);
        }
        ArrayList arrayList = new ArrayList(arrayMap.size());
        for (int i5 = 0; i5 < arrayMap.size(); i5++) {
            arrayList.add(parallelPackageParser.take());
        }
        Collections.sort(arrayList, new Comparator() { // from class: com.android.server.pm.InstallPackageHelper$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$scanApexPackages$2;
                lambda$scanApexPackages$2 = InstallPackageHelper.lambda$scanApexPackages$2(arrayMap, (ParallelPackageParser.ParseResult) obj, (ParallelPackageParser.ParseResult) obj2);
                return lambda$scanApexPackages$2;
            }
        });
        ArrayList arrayList2 = new ArrayList(arrayMap.size());
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            ParallelPackageParser.ParseResult parseResult = (ParallelPackageParser.ParseResult) arrayList.get(i6);
            Throwable th = parseResult.throwable;
            ApexInfo apexInfo2 = (ApexInfo) arrayMap.get(parseResult.scanFile);
            int systemPackageScanFlags = i2 | 67108864 | this.mPm.getSystemPackageScanFlags(parseResult.scanFile);
            if (apexInfo2.isFactory) {
                i3 = systemPackageScanFlags;
                i4 = i;
            } else {
                i4 = i & (-17);
                i3 = systemPackageScanFlags | 4;
            }
            if (th == null) {
                try {
                    addForInitLI(parseResult.parsedPackage, i4, i3, null, new ApexManager.ActiveApexInfo(apexInfo2));
                    AndroidPackageInternal hideAsFinal = parseResult.parsedPackage.hideAsFinal();
                    if (apexInfo2.isFactory && !apexInfo2.isActive) {
                        disableSystemPackageLPw(hideAsFinal);
                    }
                    arrayList2.add(new ApexManager.ScanResult(apexInfo2, hideAsFinal, hideAsFinal.getPackageName()));
                } catch (PackageManagerException e) {
                    throw new IllegalStateException("Failed to scan: " + apexInfo2.modulePath, e);
                }
            } else if (th instanceof PackageManagerException) {
                throw new IllegalStateException("Unable to parse: " + apexInfo2.modulePath, th);
            } else {
                throw new IllegalStateException("Unexpected exception occurred while parsing " + apexInfo2.modulePath, th);
            }
        }
        return arrayList2;
    }

    public static /* synthetic */ int lambda$scanApexPackages$2(ArrayMap arrayMap, ParallelPackageParser.ParseResult parseResult, ParallelPackageParser.ParseResult parseResult2) {
        return Boolean.compare(((ApexInfo) arrayMap.get(parseResult2.scanFile)).isFactory, ((ApexInfo) arrayMap.get(parseResult.scanFile)).isFactory);
    }

    @GuardedBy({"mPm.mInstallLock", "mPm.mLock"})
    public void installPackagesFromDir(File file, int i, int i2, PackageParser2 packageParser2, ExecutorService executorService, ApexManager.ActiveApexInfo activeApexInfo) {
        PackageManagerException packageManagerException;
        int i3;
        String str;
        String str2;
        File[] listFiles = file.listFiles();
        if (ArrayUtils.isEmpty(listFiles)) {
            Log.d("PackageManager", "No files in app dir " + file);
            return;
        }
        ParallelPackageParser parallelPackageParser = new ParallelPackageParser(packageParser2, executorService);
        int length = listFiles.length;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (true) {
            if (i5 >= length) {
                break;
            }
            File file2 = listFiles[i5];
            if ((ApkLiteParseUtils.isApkFile(file2) || file2.isDirectory()) && !PackageInstallerService.isStageName(file2.getName())) {
                if ((i2 & 16777216) != 0) {
                    PackageCacher packageCacher = new PackageCacher(this.mPm.getCacheDir());
                    Log.w("PackageManager", "Dropping cache of " + file2.getAbsolutePath());
                    packageCacher.cleanCachedResult(file2);
                }
                parallelPackageParser.submit(file2, i);
                i6++;
            }
            i5++;
        }
        int i7 = i6;
        while (i7 > 0) {
            ParallelPackageParser.ParseResult take = parallelPackageParser.take();
            Throwable th = take.throwable;
            if (th == null) {
                if (take.parsedPackage.isStaticSharedLibrary()) {
                    PackageManagerService.renameStaticSharedLibraryPackage(take.parsedPackage);
                }
                try {
                    ParsedPackage parsedPackage = take.parsedPackage;
                    UserHandle userHandle = new UserHandle(i4);
                    str2 = ": ";
                    try {
                        addForInitLI(parsedPackage, i, i2, userHandle, activeApexInfo);
                        str = null;
                        i3 = 1;
                    } catch (PackageManagerException e) {
                        e = e;
                        i3 = e.error;
                        str = "Failed to scan " + take.scanFile + str2 + e.getMessage();
                        Slog.w("PackageManager", str);
                        if ((i2 & 8388608) != 0) {
                            this.mApexManager.reportErrorWithApkInApex(file.getAbsolutePath(), str);
                        }
                        if ((i2 & 65536) == 0) {
                            PackageManagerServiceUtils.logCriticalInfo(5, "Deleting invalid package at " + take.scanFile);
                            this.mRemovePackageHelper.removeCodePath(take.scanFile);
                        }
                        i7--;
                        i4 = 0;
                    }
                } catch (PackageManagerException e2) {
                    e = e2;
                    str2 = ": ";
                }
            } else if (th instanceof PackageManagerException) {
                i3 = ((PackageManagerException) th).error;
                str = "Failed to parse " + take.scanFile + ": " + packageManagerException.getMessage();
                Slog.w("PackageManager", str);
            } else {
                throw new IllegalStateException("Unexpected exception occurred while parsing " + take.scanFile, th);
            }
            if ((i2 & 8388608) != 0 && i3 != 1) {
                this.mApexManager.reportErrorWithApkInApex(file.getAbsolutePath(), str);
            }
            if ((i2 & 65536) == 0 && i3 != 1) {
                PackageManagerServiceUtils.logCriticalInfo(5, "Deleting invalid package at " + take.scanFile);
                this.mRemovePackageHelper.removeCodePath(take.scanFile);
            }
            i7--;
            i4 = 0;
        }
    }

    @GuardedBy({"mPm.mLock"})
    public void checkExistingBetterPackages(ArrayMap<String, File> arrayMap, List<String> list, int i, int i2) {
        for (int i3 = 0; i3 < arrayMap.size(); i3++) {
            String keyAt = arrayMap.keyAt(i3);
            if (!this.mPm.mPackages.containsKey(keyAt)) {
                File valueAt = arrayMap.valueAt(i3);
                PackageManagerServiceUtils.logCriticalInfo(5, "Expected better " + keyAt + " but never showed up; reverting to system");
                Pair<Integer, Integer> systemPackageRescanFlagsAndReparseFlags = this.mPm.getSystemPackageRescanFlagsAndReparseFlags(valueAt, i, i2);
                int intValue = ((Integer) systemPackageRescanFlagsAndReparseFlags.first).intValue();
                int intValue2 = ((Integer) systemPackageRescanFlagsAndReparseFlags.second).intValue();
                if (intValue == 0) {
                    Slog.e("PackageManager", "Ignoring unexpected fallback path " + valueAt);
                } else {
                    this.mPm.mSettings.enableSystemPackageLPw(keyAt);
                    try {
                        synchronized (this.mPm.mInstallLock) {
                            if (scanSystemPackageTracedLI(valueAt, intValue2, intValue, null).isStub()) {
                                list.add(keyAt);
                            }
                        }
                    } catch (PackageManagerException e) {
                        Slog.e("PackageManager", "Failed to parse original system package: " + e.getMessage());
                    }
                }
            }
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public AndroidPackage scanSystemPackageTracedLI(File file, int i, int i2, ApexManager.ActiveApexInfo activeApexInfo) throws PackageManagerException {
        Trace.traceBegin(262144L, "scanPackage [" + file.toString() + "]");
        try {
            return scanSystemPackageLI(file, i, i2, activeApexInfo);
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final AndroidPackage scanSystemPackageLI(File file, int i, int i2, ApexManager.ActiveApexInfo activeApexInfo) throws PackageManagerException {
        Trace.traceBegin(262144L, "parsePackage");
        try {
            PackageParser2 scanningPackageParser = this.mPm.mInjector.getScanningPackageParser();
            ParsedPackage parsePackage = scanningPackageParser.parsePackage(file, i, false);
            scanningPackageParser.close();
            Trace.traceEnd(262144L);
            if (parsePackage.isStaticSharedLibrary()) {
                PackageManagerService.renameStaticSharedLibraryPackage(parsePackage);
            }
            return addForInitLI(parsePackage, i, i2, new UserHandle(0), activeApexInfo);
        } catch (Throwable th) {
            Trace.traceEnd(262144L);
            throw th;
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final AndroidPackage addForInitLI(ParsedPackage parsedPackage, int i, int i2, UserHandle userHandle, ApexManager.ActiveApexInfo activeApexInfo) throws PackageManagerException {
        PackageSetting disabledSystemPkgLPr;
        String apexModuleName;
        boolean z;
        PackageSetting packageSetting;
        synchronized (this.mPm.mLock) {
            disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(parsedPackage.getPackageName());
            if (activeApexInfo != null && disabledSystemPkgLPr != null) {
                disabledSystemPkgLPr.setApexModuleName(activeApexInfo.apexModuleName);
            }
        }
        Pair<ScanResult, Boolean> scanSystemPackageLI = scanSystemPackageLI(parsedPackage, i, i2, userHandle);
        ScanResult scanResult = (ScanResult) scanSystemPackageLI.first;
        boolean booleanValue = ((Boolean) scanSystemPackageLI.second).booleanValue();
        InstallRequest installRequest = new InstallRequest(parsedPackage, i, i2, userHandle, scanResult);
        synchronized (this.mPm.mLock) {
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(parsedPackage.getPackageName());
            apexModuleName = packageLPr != null ? packageLPr.getApexModuleName() : null;
        }
        if (activeApexInfo != null) {
            installRequest.setApexModuleName(activeApexInfo.apexModuleName);
        } else if (disabledSystemPkgLPr != null) {
            installRequest.setApexModuleName(disabledSystemPkgLPr.getApexModuleName());
        } else if (apexModuleName != null) {
            installRequest.setApexModuleName(apexModuleName);
        }
        synchronized (this.mPm.mLock) {
            boolean z2 = false;
            try {
                String packageName = scanResult.mPkgSetting.getPackageName();
                List singletonList = Collections.singletonList(installRequest);
                PackageManagerService packageManagerService = this.mPm;
                List<ReconciledPackage> reconcilePackages = ReconcilePackageUtils.reconcilePackages(singletonList, packageManagerService.mPackages, Collections.singletonMap(packageName, packageManagerService.getSettingsVersionForPackage(parsedPackage)), this.mSharedLibraries, this.mPm.mSettings.getKeySetManagerService(), this.mPm.mSettings);
                if ((i2 & 67108864) == 0) {
                    z = optimisticallyRegisterAppId(installRequest);
                } else {
                    installRequest.setScannedPackageSettingAppId(-1);
                    z = false;
                }
                try {
                    commitReconciledScanResultLocked(reconcilePackages.get(0), this.mPm.mUserManager.getUserIds());
                } catch (PackageManagerException e) {
                    e = e;
                    z2 = z;
                    if (z2) {
                        cleanUpAppIdCreation(installRequest);
                    }
                    throw e;
                }
            } catch (PackageManagerException e2) {
                e = e2;
            }
        }
        if (booleanValue) {
            synchronized (this.mPm.mLock) {
                this.mPm.mSettings.disableSystemPackageLPw(parsedPackage.getPackageName(), true);
            }
        }
        if (this.mIncrementalManager != null && IncrementalManager.isIncrementalPath(parsedPackage.getPath()) && (packageSetting = scanResult.mPkgSetting) != null && packageSetting.isLoading()) {
            this.mIncrementalManager.registerLoadingProgressCallback(parsedPackage.getPath(), new IncrementalProgressListener(parsedPackage.getPackageName(), this.mPm));
        }
        return scanResult.mPkgSetting.getPkg();
    }

    public final boolean optimisticallyRegisterAppId(InstallRequest installRequest) throws PackageManagerException {
        boolean registerAppIdLPw;
        if (!installRequest.isExistingSettingCopied() || installRequest.needsNewAppId()) {
            synchronized (this.mPm.mLock) {
                registerAppIdLPw = this.mPm.mSettings.registerAppIdLPw(installRequest.getScannedPackageSetting(), installRequest.needsNewAppId());
            }
            return registerAppIdLPw;
        }
        return false;
    }

    public final void cleanUpAppIdCreation(InstallRequest installRequest) {
        if (installRequest.getScannedPackageSetting() == null || installRequest.getScannedPackageSetting().getAppId() <= 0) {
            return;
        }
        synchronized (this.mPm.mLock) {
            this.mPm.mSettings.removeAppIdLPw(installRequest.getScannedPackageSetting().getAppId());
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final ScanResult scanPackageTracedLI(ParsedPackage parsedPackage, int i, int i2, long j, UserHandle userHandle, String str) throws PackageManagerException {
        Trace.traceBegin(262144L, "scanPackage");
        try {
            return scanPackageNewLI(parsedPackage, i, i2, j, userHandle, str);
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x009a A[Catch: all -> 0x00d6, TryCatch #0 {, blocks: (B:4:0x0008, B:6:0x0024, B:7:0x0027, B:9:0x0045, B:10:0x0064, B:12:0x0073, B:19:0x0084, B:21:0x008a, B:24:0x009a, B:26:0x00a5, B:16:0x007c), top: B:41:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00a4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ScanRequest prepareInitialScanRequest(ParsedPackage parsedPackage, int i, int i2, UserHandle userHandle, String str) throws PackageManagerException {
        AndroidPackage platformPackage;
        String realPackageName;
        PackageSetting originalPackageLocked;
        PackageSetting packageLPr;
        PackageSetting disabledSystemPkgLPr;
        boolean isLeavingSharedUser;
        SharedUserSetting sharedUserLPw;
        SharedUserSetting sharedUserSettingLPr;
        synchronized (this.mPm.mLock) {
            platformPackage = this.mPm.getPlatformPackage();
            boolean isSystem = AndroidPackageUtils.isSystem(parsedPackage);
            String renamedPackageLPr = this.mPm.mSettings.getRenamedPackageLPr(AndroidPackageUtils.getRealPackageOrNull(parsedPackage, isSystem));
            realPackageName = ScanPackageUtils.getRealPackageName(parsedPackage, renamedPackageLPr, isSystem);
            if (realPackageName != null) {
                ScanPackageUtils.ensurePackageRenamed(parsedPackage, renamedPackageLPr);
            }
            originalPackageLocked = getOriginalPackageLocked(parsedPackage, renamedPackageLPr);
            packageLPr = this.mPm.mSettings.getPackageLPr(parsedPackage.getPackageName());
            if (this.mPm.mTransferredPackages.contains(parsedPackage.getPackageName())) {
                Slog.w("PackageManager", "Package " + parsedPackage.getPackageName() + " was transferred to another, but its .apk remains");
            }
            disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(parsedPackage.getPackageName());
            if (packageLPr != null && packageLPr.hasSharedUser()) {
                isLeavingSharedUser = false;
                sharedUserLPw = (!isLeavingSharedUser || parsedPackage.getSharedUserId() == null) ? null : this.mPm.mSettings.getSharedUserLPw(parsedPackage.getSharedUserId(), 0, 0, true);
                sharedUserSettingLPr = packageLPr == null ? this.mPm.mSettings.getSharedUserSettingLPr(packageLPr) : null;
            }
            isLeavingSharedUser = parsedPackage.isLeavingSharedUser();
            if (isLeavingSharedUser) {
            }
            if (packageLPr == null) {
            }
        }
        return new ScanRequest(parsedPackage, sharedUserSettingLPr, packageLPr == null ? null : packageLPr.getPkg(), packageLPr, sharedUserLPw, disabledSystemPkgLPr, originalPackageLocked, realPackageName, i, i2, platformPackage != null && platformPackage.getPackageName().equals(parsedPackage.getPackageName()), userHandle, str);
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final ScanResult scanPackageNewLI(ParsedPackage parsedPackage, int i, int i2, long j, UserHandle userHandle, String str) throws PackageManagerException {
        boolean z;
        ScanResult scanPackageOnlyLI;
        ScanRequest prepareInitialScanRequest = prepareInitialScanRequest(parsedPackage, i, i2, userHandle, str);
        PackageSetting packageSetting = prepareInitialScanRequest.mPkgSetting;
        PackageSetting packageSetting2 = prepareInitialScanRequest.mDisabledPkgSetting;
        if (packageSetting != null) {
            z = packageSetting.isUpdatedSystemApp();
        } else {
            z = packageSetting2 != null;
        }
        boolean z2 = z;
        int adjustScanFlags = adjustScanFlags(i2, packageSetting, packageSetting2, userHandle, parsedPackage);
        ScanPackageUtils.applyPolicy(parsedPackage, adjustScanFlags, this.mPm.getPlatformPackage(), z2);
        synchronized (this.mPm.mLock) {
            assertPackageIsValid(parsedPackage, i, adjustScanFlags);
            ScanRequest scanRequest = new ScanRequest(parsedPackage, prepareInitialScanRequest.mOldSharedUserSetting, prepareInitialScanRequest.mOldPkg, packageSetting, prepareInitialScanRequest.mSharedUserSetting, packageSetting2, prepareInitialScanRequest.mOriginalPkgSetting, prepareInitialScanRequest.mRealPkgName, i, i2, prepareInitialScanRequest.mIsPlatformPackage, userHandle, str);
            PackageManagerService packageManagerService = this.mPm;
            scanPackageOnlyLI = ScanPackageUtils.scanPackageOnlyLI(scanRequest, packageManagerService.mInjector, packageManagerService.mFactoryTest, j);
        }
        return scanPackageOnlyLI;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:120:0x02cb  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x032e  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0380  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0388 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:133:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0213  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0216  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Pair<ScanResult, Boolean> scanSystemPackageLI(ParsedPackage parsedPackage, int i, int i2, UserHandle userHandle) throws PackageManagerException {
        PackageManagerTracedLock packageManagerTracedLock;
        PackageSetting packageSetting;
        ScanRequest scanRequest;
        int i3;
        int i4;
        int i5;
        int i6;
        PackageSetting packageSetting2;
        boolean z;
        int i7;
        PackageSetting packageSetting3;
        boolean z2 = (i & 16) != 0;
        ScanRequest prepareInitialScanRequest = prepareInitialScanRequest(parsedPackage, i, i2, userHandle, null);
        PackageSetting packageSetting4 = prepareInitialScanRequest.mPkgSetting;
        PackageSetting packageSetting5 = prepareInitialScanRequest.mOriginalPkgSetting;
        PackageSetting packageSetting6 = packageSetting5 == null ? packageSetting4 : packageSetting5;
        boolean z3 = packageSetting6 != null;
        String packageName = z3 ? packageSetting6.getPackageName() : parsedPackage.getPackageName();
        PackageManagerTracedLock packageManagerTracedLock2 = this.mPm.mLock;
        synchronized (packageManagerTracedLock2) {
            try {
                try {
                    boolean isDeviceUpgrading = this.mPm.isDeviceUpgrading();
                    if (z2 && !z3 && this.mPm.mSettings.getDisabledSystemPkgLPr(packageName) != null) {
                        Slog.w("PackageManager", "Inconsistent package setting of updated system app for " + packageName + ". To recover it, enable the system app and install it as non-updated system app.");
                        this.mPm.mSettings.removeDisabledSystemPackageLPw(packageName);
                    }
                    PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(packageName);
                    boolean z4 = disabledSystemPkgLPr != null;
                    if (z2 && z4) {
                        packageManagerTracedLock = packageManagerTracedLock2;
                        packageSetting = packageSetting6;
                        scanRequest = prepareInitialScanRequest;
                        ScanRequest scanRequest2 = new ScanRequest(parsedPackage, this.mPm.mSettings.getSharedUserSettingLPr(disabledSystemPkgLPr), null, disabledSystemPkgLPr, prepareInitialScanRequest.mSharedUserSetting, null, null, null, i, i2, prepareInitialScanRequest.mIsPlatformPackage, userHandle, null);
                        i4 = i2;
                        i3 = 1;
                        ScanPackageUtils.applyPolicy(parsedPackage, i4, this.mPm.getPlatformPackage(), true);
                        PackageManagerService packageManagerService = this.mPm;
                        ScanResult scanPackageOnlyLI = ScanPackageUtils.scanPackageOnlyLI(scanRequest2, packageManagerService.mInjector, packageManagerService.mFactoryTest, -1L);
                        if (scanPackageOnlyLI.mExistingSettingCopied && (packageSetting3 = scanPackageOnlyLI.mRequest.mPkgSetting) != null) {
                            packageSetting3.updateFrom(scanPackageOnlyLI.mPkgSetting);
                        }
                    } else {
                        packageManagerTracedLock = packageManagerTracedLock2;
                        packageSetting = packageSetting6;
                        scanRequest = prepareInitialScanRequest;
                        i3 = 1;
                        i4 = i2;
                    }
                    int i8 = (!z3 || packageSetting.getPathString().equals(parsedPackage.getPath())) ? 0 : i3;
                    int i9 = (!z3 || parsedPackage.getLongVersionCode() <= packageSetting.getVersionCode()) ? 0 : i3;
                    if (z3) {
                        ScanRequest scanRequest3 = scanRequest;
                        if (scanRequest3.mOldSharedUserSetting != scanRequest3.mSharedUserSetting) {
                            i5 = i3;
                            i6 = (z2 || !z4 || i8 == 0 || (i9 == 0 && i5 == 0)) ? 0 : i3;
                            if (i6 != 0) {
                                synchronized (this.mPm.mLock) {
                                    this.mPm.mPackages.remove(packageSetting.getPackageName());
                                }
                                PackageManagerServiceUtils.logCriticalInfo(5, "System package updated; name: " + packageSetting.getPackageName() + "; " + packageSetting.getVersionCode() + " --> " + parsedPackage.getLongVersionCode() + "; " + packageSetting.getPathString() + " --> " + parsedPackage.getPath());
                                this.mRemovePackageHelper.cleanUpResources(new File(packageSetting.getPathString()), InstructionSets.getAppDexInstructionSets(packageSetting.getPrimaryCpuAbiLegacy(), packageSetting.getSecondaryCpuAbiLegacy()));
                                synchronized (this.mPm.mLock) {
                                    this.mPm.mSettings.enableSystemPackageLPw(packageSetting.getPackageName());
                                }
                            }
                            if (!z2 && z4 && i6 == 0) {
                                parsedPackage.hideAsFinal();
                                StringBuilder sb = new StringBuilder();
                                sb.append("Package ");
                                sb.append(parsedPackage.getPackageName());
                                sb.append(" at ");
                                sb.append(parsedPackage.getPath());
                                sb.append(" ignored: updated version ");
                                sb.append(z3 ? String.valueOf(packageSetting.getVersionCode()) : "unknown");
                                sb.append(" better than this ");
                                sb.append(parsedPackage.getLongVersionCode());
                                throw PackageManagerException.ofInternalError(sb.toString(), -12);
                            }
                            boolean isApkVerificationForced = !z2 ? isDeviceUpgrading : PackageManagerServiceUtils.isApkVerificationForced(packageSetting);
                            packageSetting2 = packageSetting;
                            ScanPackageUtils.collectCertificatesLI(packageSetting2, parsedPackage, this.mPm.getSettingsVersionForPackage(parsedPackage), isApkVerificationForced, (!z2 || (isApkVerificationForced && canSkipForcedPackageVerification(parsedPackage))) ? i3 : 0, this.mPm.isPreNMR1Upgrade());
                            maybeClearProfilesForUpgradesLI(packageSetting2, parsedPackage);
                            if (z2 && !z4 && z3 && !packageSetting2.isSystem()) {
                                if (parsedPackage.getSigningDetails().checkCapability(packageSetting2.getSigningDetails(), i3) && !packageSetting2.getSigningDetails().checkCapability(parsedPackage.getSigningDetails(), 8)) {
                                    PackageManagerServiceUtils.logCriticalInfo(5, "System package signature mismatch; name: " + packageSetting2.getPackageName());
                                    PackageFreezer freezePackage = this.mPm.freezePackage(parsedPackage.getPackageName(), -1, "scanPackageInternalLI", 13);
                                    try {
                                        new DeletePackageHelper(this.mPm).deletePackageLIF(parsedPackage.getPackageName(), null, true, this.mPm.mUserManager.getUserIds(), 0, null, false);
                                        if (freezePackage != null) {
                                            freezePackage.close();
                                        }
                                    } catch (Throwable th) {
                                        if (freezePackage != null) {
                                            try {
                                                freezePackage.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                        }
                                        throw th;
                                    }
                                } else if (i9 == 0) {
                                    PackageManagerServiceUtils.logCriticalInfo(5, "System package enabled; name: " + packageSetting2.getPackageName() + "; " + packageSetting2.getVersionCode() + " --> " + parsedPackage.getLongVersionCode() + "; " + packageSetting2.getPathString() + " --> " + parsedPackage.getPath());
                                    this.mRemovePackageHelper.cleanUpResources(new File(packageSetting2.getPathString()), InstructionSets.getAppDexInstructionSets(packageSetting2.getPrimaryCpuAbiLegacy(), packageSetting2.getSecondaryCpuAbiLegacy()));
                                } else {
                                    PackageManagerServiceUtils.logCriticalInfo(4, "System package disabled; name: " + packageSetting2.getPackageName() + "; old: " + packageSetting2.getPathString() + " @ " + packageSetting2.getVersionCode() + "; new: " + parsedPackage.getPath() + " @ " + parsedPackage.getPath());
                                    z = i3;
                                    int i10 = (67108864 & i4) != 0 ? i3 : 0;
                                    if (this.mPm.mShouldStopSystemPackagesByDefault && z2 && !z3 && i10 == 0) {
                                        if (!this.mPm.mInitialNonStoppedSystemPackages.contains(parsedPackage.getPackageName())) {
                                            i7 = 134217728 | i4;
                                            return new Pair<>(scanPackageNewLI(parsedPackage, i, i7 | 2, 0L, userHandle, null), Boolean.valueOf(z));
                                        }
                                    }
                                    i7 = i4;
                                    return new Pair<>(scanPackageNewLI(parsedPackage, i, i7 | 2, 0L, userHandle, null), Boolean.valueOf(z));
                                }
                            }
                            z = 0;
                            if ((67108864 & i4) != 0) {
                            }
                            if (this.mPm.mShouldStopSystemPackagesByDefault) {
                                if (!this.mPm.mInitialNonStoppedSystemPackages.contains(parsedPackage.getPackageName())) {
                                }
                            }
                            i7 = i4;
                            return new Pair<>(scanPackageNewLI(parsedPackage, i, i7 | 2, 0L, userHandle, null), Boolean.valueOf(z));
                        }
                    }
                    i5 = 0;
                    if (z2) {
                    }
                    if (i6 != 0) {
                    }
                    if (!z2) {
                    }
                    if (!z2) {
                    }
                    packageSetting2 = packageSetting;
                    ScanPackageUtils.collectCertificatesLI(packageSetting2, parsedPackage, this.mPm.getSettingsVersionForPackage(parsedPackage), isApkVerificationForced, (!z2 || (isApkVerificationForced && canSkipForcedPackageVerification(parsedPackage))) ? i3 : 0, this.mPm.isPreNMR1Upgrade());
                    maybeClearProfilesForUpgradesLI(packageSetting2, parsedPackage);
                    if (z2) {
                        if (parsedPackage.getSigningDetails().checkCapability(packageSetting2.getSigningDetails(), i3)) {
                        }
                        if (i9 == 0) {
                        }
                    }
                    z = 0;
                    if ((67108864 & i4) != 0) {
                    }
                    if (this.mPm.mShouldStopSystemPackagesByDefault) {
                    }
                    i7 = i4;
                    return new Pair<>(scanPackageNewLI(parsedPackage, i, i7 | 2, 0L, userHandle, null), Boolean.valueOf(z));
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                throw th;
            }
        }
        throw th;
    }

    public final boolean canSkipForcedPackageVerification(AndroidPackage androidPackage) {
        if (VerityUtils.hasFsverity(androidPackage.getBaseApkPath())) {
            String[] splitCodePaths = androidPackage.getSplitCodePaths();
            if (ArrayUtils.isEmpty(splitCodePaths)) {
                return true;
            }
            for (String str : splitCodePaths) {
                if (!VerityUtils.hasFsverity(str)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final void maybeClearProfilesForUpgradesLI(PackageSetting packageSetting, AndroidPackage androidPackage) {
        if (packageSetting == null || !this.mPm.isDeviceUpgrading() || packageSetting.getVersionCode() == androidPackage.getLongVersionCode()) {
            return;
        }
        this.mAppDataHelper.clearAppProfilesLIF(androidPackage);
    }

    @GuardedBy({"mPm.mLock"})
    public final PackageSetting getOriginalPackageLocked(AndroidPackage androidPackage, String str) {
        if (ScanPackageUtils.isPackageRenamed(androidPackage, str)) {
            return null;
        }
        for (int size = ArrayUtils.size(androidPackage.getOriginalPackages()) - 1; size >= 0; size--) {
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getOriginalPackages().get(size));
            if (packageLPr != null && verifyPackageUpdateLPr(packageLPr, androidPackage)) {
                if (this.mPm.mSettings.getSharedUserSettingLPr(packageLPr) != null) {
                    String str2 = this.mPm.mSettings.getSharedUserSettingLPr(packageLPr).name;
                    if (!str2.equals(androidPackage.getSharedUserId())) {
                        Slog.w("PackageManager", "Unable to migrate data from " + packageLPr.getPackageName() + " to " + androidPackage.getPackageName() + ": old shared user settings name " + str2 + " differs from " + androidPackage.getSharedUserId());
                    }
                }
                return packageLPr;
            }
        }
        return null;
    }

    @GuardedBy({"mPm.mLock"})
    public final boolean verifyPackageUpdateLPr(PackageSetting packageSetting, AndroidPackage androidPackage) {
        if ((packageSetting.getFlags() & 1) == 0) {
            Slog.w("PackageManager", "Unable to update from " + packageSetting.getPackageName() + " to " + androidPackage.getPackageName() + ": old package not in system partition");
            return false;
        } else if (this.mPm.mPackages.get(packageSetting.getPackageName()) != null) {
            Slog.w("PackageManager", "Unable to update from " + packageSetting.getPackageName() + " to " + androidPackage.getPackageName() + ": old package still exists");
            return false;
        } else {
            return true;
        }
    }

    public final void assertPackageIsValid(AndroidPackage androidPackage, int i, int i2) throws PackageManagerException {
        if ((i & 64) != 0) {
            ScanPackageUtils.assertCodePolicy(androidPackage);
        }
        if (androidPackage.getPath() == null) {
            throw new PackageManagerException(-2, "Code and resource paths haven't been set correctly");
        }
        boolean z = (i2 & 16) == 0;
        boolean z2 = (i2 & IInstalld.FLAG_USE_QUOTA) != 0;
        boolean z3 = (67108864 & i2) != 0;
        if ((z || z2) && this.mPm.snapshotComputer().isApexPackage(androidPackage.getPackageName()) && !z3) {
            throw new PackageManagerException(-5, androidPackage.getPackageName() + " is an APEX package and can't be installed as an APK.");
        }
        this.mPm.mSettings.getKeySetManagerService().assertScannedPackageValid(androidPackage);
        synchronized (this.mPm.mLock) {
            if (androidPackage.getPackageName().equals(PackageManagerShellCommandDataLoader.PACKAGE) && this.mPm.getCoreAndroidApplication() != null) {
                Slog.w("PackageManager", "*************************************************");
                Slog.w("PackageManager", "Core android package being redefined.  Skipping.");
                Slog.w("PackageManager", " codePath=" + androidPackage.getPath());
                Slog.w("PackageManager", "*************************************************");
                throw new PackageManagerException(-5, "Core android package being redefined.  Skipping.");
            }
            int i3 = i2 & 4;
            if (i3 == 0 && this.mPm.mPackages.containsKey(androidPackage.getPackageName())) {
                throw new PackageManagerException(-5, "Application package " + androidPackage.getPackageName() + " already installed.  Skipping duplicate.");
            }
            if (androidPackage.isStaticSharedLibrary()) {
                if (i3 == 0 && this.mPm.mPackages.containsKey(androidPackage.getManifestPackageName())) {
                    throw PackageManagerException.ofInternalError("Duplicate static shared lib provider package", -13);
                }
                ScanPackageUtils.assertStaticSharedLibraryIsValid(androidPackage, i2);
                assertStaticSharedLibraryVersionCodeIsValid(androidPackage);
            }
            if ((i2 & 128) != 0) {
                if (this.mPm.isExpectingBetter(androidPackage.getPackageName())) {
                    Slog.w("PackageManager", "Relax SCAN_REQUIRE_KNOWN requirement for package " + androidPackage.getPackageName());
                } else {
                    PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getPackageName());
                    if (packageLPr != null) {
                        if (!androidPackage.getPath().equals(packageLPr.getPathString())) {
                            throw new PackageManagerException(-23, "Application package " + androidPackage.getPackageName() + " found at " + androidPackage.getPath() + " but expected at " + packageLPr.getPathString() + "; ignoring.");
                        }
                    } else {
                        throw new PackageManagerException(-19, "Application package " + androidPackage.getPackageName() + " not found; ignoring.");
                    }
                }
            }
            if (i3 != 0) {
                this.mPm.mComponentResolver.assertProvidersNotDefined(androidPackage);
            }
            ScanPackageUtils.assertProcessesAreValid(androidPackage);
            assertPackageWithSharedUserIdIsPrivileged(androidPackage);
            if (androidPackage.getOverlayTarget() != null) {
                assertOverlayIsValid(androidPackage, i, i2);
            }
            ScanPackageUtils.assertMinSignatureSchemeIsValid(androidPackage, i);
        }
    }

    public final void assertStaticSharedLibraryVersionCodeIsValid(AndroidPackage androidPackage) throws PackageManagerException {
        WatchedLongSparseArray<SharedLibraryInfo> sharedLibraryInfos = this.mSharedLibraries.getSharedLibraryInfos(androidPackage.getStaticSharedLibraryName());
        long j = Long.MIN_VALUE;
        long j2 = Long.MAX_VALUE;
        if (sharedLibraryInfos != null) {
            int size = sharedLibraryInfos.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                SharedLibraryInfo valueAt = sharedLibraryInfos.valueAt(i);
                long longVersionCode = valueAt.getDeclaringPackage().getLongVersionCode();
                if (valueAt.getLongVersion() < androidPackage.getStaticSharedLibraryVersion()) {
                    j = Math.max(j, longVersionCode + 1);
                } else if (valueAt.getLongVersion() <= androidPackage.getStaticSharedLibraryVersion()) {
                    j = longVersionCode;
                    j2 = j;
                    break;
                } else {
                    j2 = Math.min(j2, longVersionCode - 1);
                }
                i++;
            }
        }
        if (androidPackage.getLongVersionCode() < j || androidPackage.getLongVersionCode() > j2) {
            throw PackageManagerException.ofInternalError("Static shared lib version codes must be ordered as lib versions", -14);
        }
    }

    public final void assertOverlayIsValid(AndroidPackage androidPackage, int i, int i2) throws PackageManagerException {
        PackageSetting packageLPr;
        PackageSetting packageLPr2;
        PackageSetting packageLPr3;
        if ((65536 & i2) != 0) {
            if ((i & 16) == 0) {
                if (this.mPm.isOverlayMutable(androidPackage.getPackageName())) {
                    return;
                }
                throw PackageManagerException.ofInternalError("Overlay " + androidPackage.getPackageName() + " is static and cannot be upgraded.", -15);
            } else if ((524288 & i2) != 0) {
                if (androidPackage.getTargetSdkVersion() < ScanPackageUtils.getVendorPartitionVersion()) {
                    Slog.w("PackageManager", "System overlay " + androidPackage.getPackageName() + " targets an SDK below the required SDK level of vendor overlays (" + ScanPackageUtils.getVendorPartitionVersion() + "). This will become an install error in a future release");
                    return;
                }
                return;
            } else {
                int targetSdkVersion = androidPackage.getTargetSdkVersion();
                int i3 = Build.VERSION.SDK_INT;
                if (targetSdkVersion < i3) {
                    Slog.w("PackageManager", "System overlay " + androidPackage.getPackageName() + " targets an SDK below the required SDK level of system overlays (" + i3 + "). This will become an install error in a future release");
                    return;
                }
                return;
            }
        }
        if (androidPackage.getTargetSdkVersion() < 29) {
            synchronized (this.mPm.mLock) {
                packageLPr3 = this.mPm.mSettings.getPackageLPr(PackageManagerShellCommandDataLoader.PACKAGE);
            }
            if (!PackageManagerServiceUtils.comparePackageSignatures(packageLPr3, androidPackage.getSigningDetails().getSignatures())) {
                throw PackageManagerException.ofInternalError("Overlay " + androidPackage.getPackageName() + " must target Q or later, or be signed with the platform certificate", -16);
            }
        }
        if (androidPackage.getOverlayTargetOverlayableName() == null) {
            synchronized (this.mPm.mLock) {
                packageLPr = this.mPm.mSettings.getPackageLPr(androidPackage.getOverlayTarget());
            }
            if (packageLPr == null || PackageManagerServiceUtils.comparePackageSignatures(packageLPr, androidPackage.getSigningDetails().getSignatures())) {
                return;
            }
            PackageManagerService packageManagerService = this.mPm;
            if (packageManagerService.mOverlayConfigSignaturePackage == null) {
                throw PackageManagerException.ofInternalError("Overlay " + androidPackage.getPackageName() + " and target " + androidPackage.getOverlayTarget() + " signed with different certificates, and the overlay lacks <overlay android:targetName>", -17);
            }
            synchronized (packageManagerService.mLock) {
                PackageManagerService packageManagerService2 = this.mPm;
                packageLPr2 = packageManagerService2.mSettings.getPackageLPr(packageManagerService2.mOverlayConfigSignaturePackage);
            }
            if (PackageManagerServiceUtils.comparePackageSignatures(packageLPr2, androidPackage.getSigningDetails().getSignatures())) {
                return;
            }
            throw PackageManagerException.ofInternalError("Overlay " + androidPackage.getPackageName() + " signed with a different certificate than both the reference package and target " + androidPackage.getOverlayTarget() + ", and the overlay lacks <overlay android:targetName>", -18);
        }
    }

    public final void assertPackageWithSharedUserIdIsPrivileged(AndroidPackage androidPackage) throws PackageManagerException {
        PackageSetting packageLPr;
        if (AndroidPackageUtils.isPrivileged(androidPackage) || androidPackage.getSharedUserId() == null) {
            return;
        }
        SharedUserSetting sharedUserSetting = null;
        try {
            synchronized (this.mPm.mLock) {
                sharedUserSetting = this.mPm.mSettings.getSharedUserLPw(androidPackage.getSharedUserId(), 0, 0, false);
            }
        } catch (PackageManagerException unused) {
        }
        if (sharedUserSetting == null || !sharedUserSetting.isPrivileged()) {
            return;
        }
        synchronized (this.mPm.mLock) {
            packageLPr = this.mPm.mSettings.getPackageLPr(PackageManagerShellCommandDataLoader.PACKAGE);
        }
        if (PackageManagerServiceUtils.comparePackageSignatures(packageLPr, androidPackage.getSigningDetails().getSignatures())) {
            return;
        }
        throw PackageManagerException.ofInternalError("Apps that share a user with a privileged app must themselves be marked as privileged. " + androidPackage.getPackageName() + " shares privileged user " + androidPackage.getSharedUserId() + ".", -19);
    }

    public final int adjustScanFlags(int i, PackageSetting packageSetting, PackageSetting packageSetting2, UserHandle userHandle, AndroidPackage androidPackage) {
        SharedUserSetting sharedUserSetting;
        int adjustScanFlagsWithPackageSetting = ScanPackageUtils.adjustScanFlagsWithPackageSetting(i, packageSetting, packageSetting2, userHandle);
        boolean z = (524288 & adjustScanFlagsWithPackageSetting) != 0 && ScanPackageUtils.getVendorPartitionVersion() < 28;
        if ((adjustScanFlagsWithPackageSetting & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) == 0 && !AndroidPackageUtils.isPrivileged(androidPackage) && androidPackage.getSharedUserId() != null && !z) {
            synchronized (this.mPm.mLock) {
                try {
                    sharedUserSetting = this.mPm.mSettings.getSharedUserLPw(androidPackage.getSharedUserId(), 0, 0, false);
                } catch (PackageManagerException unused) {
                    sharedUserSetting = null;
                }
                if (sharedUserSetting != null && sharedUserSetting.isPrivileged() && PackageManagerServiceUtils.compareSignatures(this.mPm.mSettings.getPackageLPr(PackageManagerShellCommandDataLoader.PACKAGE).getSigningDetails().getSignatures(), androidPackage.getSigningDetails().getSignatures()) != 0) {
                    adjustScanFlagsWithPackageSetting |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                }
            }
        }
        return adjustScanFlagsWithPackageSetting;
    }
}
