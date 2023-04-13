package com.android.server.p011pm;

import android.app.ApplicationPackageManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.content.pm.VersionedPackage;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import dalvik.system.VMRuntime;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.server.pm.DeletePackageHelper */
/* loaded from: classes2.dex */
public final class DeletePackageHelper {
    public final AppDataHelper mAppDataHelper;
    public final PermissionManagerServiceInternal mPermissionManager;
    public final PackageManagerService mPm;
    public final RemovePackageHelper mRemovePackageHelper;
    public final UserManagerInternal mUserManagerInternal;

    public DeletePackageHelper(PackageManagerService packageManagerService, RemovePackageHelper removePackageHelper, AppDataHelper appDataHelper) {
        this.mPm = packageManagerService;
        this.mUserManagerInternal = packageManagerService.mInjector.getUserManagerInternal();
        this.mPermissionManager = packageManagerService.mInjector.getPermissionManagerServiceInternal();
        this.mRemovePackageHelper = removePackageHelper;
        this.mAppDataHelper = appDataHelper;
    }

    public DeletePackageHelper(PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
        AppDataHelper appDataHelper = new AppDataHelper(packageManagerService);
        this.mAppDataHelper = appDataHelper;
        this.mUserManagerInternal = packageManagerService.mInjector.getUserManagerInternal();
        this.mPermissionManager = packageManagerService.mInjector.getPermissionManagerServiceInternal();
        this.mRemovePackageHelper = new RemovePackageHelper(packageManagerService, appDataHelper);
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x00ce, code lost:
        if (r1.getUserInfo(r1.getProfileParentId(r29)).isAdmin() == false) goto L34;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int deletePackageX(String str, long j, int i, int i2, boolean z) {
        int[] iArr;
        AndroidPackageInternal androidPackageInternal;
        SparseArray sparseArray;
        int i3;
        AndroidPackageInternal androidPackageInternal2;
        int[] iArr2;
        boolean deletePackageLIF;
        int i4;
        PackageSetting packageLPr;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        SharedLibraryInfo sharedLibraryInfo;
        int i5;
        int i6;
        PackageRemovedInfo packageRemovedInfo = new PackageRemovedInfo(this.mPm);
        int i7 = (i2 & 2) != 0 ? -1 : i;
        if (this.mPm.isPackageDeviceAdmin(str, i7)) {
            Slog.w("PackageManager", "Not removing package " + str + ": has active device admin");
            return -2;
        }
        synchronized (this.mPm.mLock) {
            Computer snapshotComputer = this.mPm.snapshotComputer();
            PackageSetting packageLPr2 = this.mPm.mSettings.getPackageLPr(str);
            if (packageLPr2 == null) {
                Slog.w("PackageManager", "Not removing non-existent package " + str);
                return -1;
            } else if (j != -1 && packageLPr2.getVersionCode() != j) {
                Slog.w("PackageManager", "Not removing package " + str + " with versionCode " + packageLPr2.getVersionCode() + " != " + j);
                return -1;
            } else {
                boolean z6 = false;
                if (PackageManagerServiceUtils.isUpdatedSystemApp(packageLPr2) && (i2 & 4) == 0) {
                    UserInfo userInfo = this.mUserManagerInternal.getUserInfo(i);
                    if (userInfo != null) {
                        if (!userInfo.isAdmin()) {
                            UserManagerInternal userManagerInternal = this.mUserManagerInternal;
                        }
                    }
                    Slog.w("PackageManager", "Not removing package " + str + " as only admin user (or their profile) may downgrade system apps");
                    EventLog.writeEvent(1397638484, "170646036", -1, str);
                    return -3;
                }
                PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(str);
                AndroidPackage androidPackage = this.mPm.mPackages.get(str);
                int[] userIds = this.mUserManagerInternal.getUserIds();
                if (androidPackage != null) {
                    if (androidPackage.getStaticSharedLibraryName() != null) {
                        sharedLibraryInfo = snapshotComputer.getSharedLibraryInfo(androidPackage.getStaticSharedLibraryName(), androidPackage.getStaticSharedLibraryVersion());
                    } else {
                        sharedLibraryInfo = androidPackage.getSdkLibraryName() != null ? snapshotComputer.getSharedLibraryInfo(androidPackage.getSdkLibraryName(), androidPackage.getSdkLibVersionMajor()) : null;
                    }
                    if (sharedLibraryInfo != null) {
                        int length = userIds.length;
                        int i8 = 0;
                        while (i8 < length) {
                            int i9 = userIds[i8];
                            if (i7 == -1 || i7 == i9) {
                                i5 = i8;
                                i6 = length;
                                List<VersionedPackage> packagesUsingSharedLibrary = snapshotComputer.getPackagesUsingSharedLibrary(sharedLibraryInfo, 4202496L, 1000, i9);
                                if (!ArrayUtils.isEmpty(packagesUsingSharedLibrary)) {
                                    Slog.w("PackageManager", "Not removing package " + androidPackage.getManifestPackageName() + " hosting lib " + sharedLibraryInfo.getName() + " version " + sharedLibraryInfo.getLongVersion() + " used by " + packagesUsingSharedLibrary + " for user " + i9);
                                    return -6;
                                }
                            } else {
                                i5 = i8;
                                i6 = length;
                            }
                            i8 = i5 + 1;
                            length = i6;
                        }
                    }
                }
                packageRemovedInfo.mOrigUsers = packageLPr2.queryInstalledUsers(userIds, true);
                if (PackageManagerServiceUtils.isUpdatedSystemApp(packageLPr2) && (i2 & 4) == 0) {
                    SparseArray sparseArray2 = new SparseArray();
                    int i10 = 0;
                    while (i10 < userIds.length) {
                        PackageUserStateInternal readUserState = packageLPr2.readUserState(userIds[i10]);
                        sparseArray2.put(userIds[i10], new TempUserState(readUserState.getEnabledState(), readUserState.getLastDisableAppCaller(), readUserState.isInstalled()));
                        i10++;
                        userIds = userIds;
                    }
                    iArr = userIds;
                    androidPackageInternal = null;
                    sparseArray = sparseArray2;
                    i3 = -1;
                } else {
                    iArr = userIds;
                    androidPackageInternal = null;
                    sparseArray = null;
                    i3 = i7;
                }
                boolean isInstallerPackage = this.mPm.mSettings.isInstallerPackage(str);
                synchronized (this.mPm.mInstallLock) {
                    androidPackageInternal2 = androidPackageInternal;
                    iArr2 = iArr;
                    PackageFreezer freezePackageForDelete = this.mPm.freezePackageForDelete(str, i3, i2, "deletePackageX", 13);
                    deletePackageLIF = deletePackageLIF(str, UserHandle.of(i7), true, iArr2, i2 | Integer.MIN_VALUE, packageRemovedInfo, true);
                    if (freezePackageForDelete != null) {
                        freezePackageForDelete.close();
                    }
                    if (deletePackageLIF && androidPackage != null) {
                        synchronized (this.mPm.mLock) {
                            z5 = this.mPm.mPackages.get(androidPackage.getPackageName()) != null;
                        }
                        this.mPm.mInstantAppRegistry.onPackageUninstalled(androidPackage, packageLPr2, packageRemovedInfo.mRemovedUsers, z5);
                    }
                    synchronized (this.mPm.mLock) {
                        if (deletePackageLIF) {
                            this.mPm.updateSequenceNumberLP(packageLPr2, packageRemovedInfo.mRemovedUsers);
                            this.mPm.updateInstantAppInstallerLocked(str);
                        }
                    }
                    ApplicationPackageManager.invalidateGetPackagesForUidCache();
                }
                if (deletePackageLIF) {
                    if ((i2 & 8) == 0) {
                        z3 = z;
                        z4 = true;
                    } else {
                        z3 = z;
                        z4 = false;
                    }
                    packageRemovedInfo.sendPackageRemovedBroadcasts(z4, z3);
                    packageRemovedInfo.sendSystemPackageUpdatedBroadcasts();
                    PackageMetrics.onUninstallSucceeded(packageRemovedInfo, i2, i7);
                }
                VMRuntime.getRuntime().requestConcurrentGC();
                synchronized (this.mPm.mInstallLock) {
                    InstallArgs installArgs = packageRemovedInfo.mArgs;
                    if (installArgs != null) {
                        this.mRemovePackageHelper.cleanUpResources(installArgs.mCodeFile, installArgs.mInstructionSets);
                    }
                    if (sparseArray != null) {
                        synchronized (this.mPm.mLock) {
                            PackageSetting packageSettingForMutation = this.mPm.getPackageSettingForMutation(str);
                            if (packageSettingForMutation != null) {
                                AndroidPackageInternal pkg = packageSettingForMutation.getPkg();
                                boolean z7 = pkg != null && pkg.isEnabled();
                                for (int i11 = 0; i11 < iArr2.length; i11++) {
                                    TempUserState tempUserState = (TempUserState) sparseArray.get(iArr2[i11]);
                                    int i12 = tempUserState.enabledState;
                                    packageSettingForMutation.setEnabled(i12, iArr2[i11], tempUserState.lastDisableAppCaller);
                                    if (!z6 && tempUserState.installed) {
                                        if (i12 == 0 && z7) {
                                            z2 = true;
                                            z6 = z2;
                                        }
                                        z2 = true;
                                        if (i12 != 1) {
                                        }
                                        z6 = z2;
                                    }
                                }
                                i4 = 1;
                            } else {
                                i4 = 1;
                                Slog.w("PackageManager", "Missing PackageSetting after uninstalling the update for system app: " + str + ". This should not happen.");
                            }
                            this.mPm.mSettings.writeAllUsersPackageRestrictionsLPr();
                        }
                    } else {
                        i4 = 1;
                    }
                    AndroidPackageInternal pkg2 = disabledSystemPkgLPr == null ? androidPackageInternal2 : disabledSystemPkgLPr.getPkg();
                    if (pkg2 != null && pkg2.isStub()) {
                        synchronized (this.mPm.mLock) {
                            packageLPr = this.mPm.mSettings.getPackageLPr(pkg2.getPackageName());
                        }
                        if (packageLPr != null) {
                            if (z6) {
                                if (PackageManagerService.DEBUG_COMPRESSION) {
                                    Slog.i("PackageManager", "Enabling system stub after removal; pkg: " + pkg2.getPackageName());
                                }
                                new InstallPackageHelper(this.mPm).enableCompressedPackage(pkg2, packageLPr);
                            } else if (PackageManagerService.DEBUG_COMPRESSION) {
                                Slog.i("PackageManager", "System stub disabled for all users, leaving uncompressed after removal; pkg: " + pkg2.getPackageName());
                            }
                        }
                    }
                }
                if (deletePackageLIF && isInstallerPackage) {
                    this.mPm.mInjector.getPackageInstallerService().onInstallerPackageDeleted(packageLPr2.getAppId(), i7);
                }
                if (deletePackageLIF) {
                    return i4;
                }
                return -1;
            }
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public boolean deletePackageLIF(String str, UserHandle userHandle, boolean z, int[] iArr, int i, PackageRemovedInfo packageRemovedInfo, boolean z2) {
        DeletePackageAction mayDeletePackageLocked;
        synchronized (this.mPm.mLock) {
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(str);
            mayDeletePackageLocked = mayDeletePackageLocked(packageRemovedInfo, packageLPr, this.mPm.mSettings.getDisabledSystemPkgLPr(packageLPr), i, userHandle);
        }
        if (mayDeletePackageLocked == null) {
            return false;
        }
        try {
            executeDeletePackageLIF(mayDeletePackageLocked, str, z, iArr, z2);
            return true;
        } catch (SystemDeleteException unused) {
            return false;
        }
    }

    public static DeletePackageAction mayDeletePackageLocked(PackageRemovedInfo packageRemovedInfo, PackageSetting packageSetting, PackageSetting packageSetting2, int i, UserHandle userHandle) {
        if (packageSetting == null) {
            return null;
        }
        if (PackageManagerServiceUtils.isSystemApp(packageSetting)) {
            boolean z = true;
            boolean z2 = (i & 4) != 0;
            if (userHandle != null && userHandle.getIdentifier() != -1) {
                z = false;
            }
            if ((!z2 || z) && packageSetting2 == null) {
                Slog.w("PackageManager", "Attempt to delete unknown system package " + packageSetting.getPkg().getPackageName());
                return null;
            }
        }
        return new DeletePackageAction(packageSetting, packageSetting2, packageRemovedInfo, i, userHandle);
    }

    public void executeDeletePackage(DeletePackageAction deletePackageAction, String str, boolean z, int[] iArr, boolean z2) throws SystemDeleteException {
        synchronized (this.mPm.mInstallLock) {
            executeDeletePackageLIF(deletePackageAction, str, z, iArr, z2);
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void executeDeletePackageLIF(DeletePackageAction deletePackageAction, String str, boolean z, int[] iArr, boolean z2) throws SystemDeleteException {
        boolean z3;
        boolean z4;
        PackageSetting packageSetting = deletePackageAction.mDeletingPs;
        PackageRemovedInfo packageRemovedInfo = deletePackageAction.mRemovedInfo;
        UserHandle userHandle = deletePackageAction.mUser;
        int i = deletePackageAction.mFlags;
        boolean isSystemApp = PackageManagerServiceUtils.isSystemApp(packageSetting);
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        for (int i2 : iArr) {
            sparseBooleanArray.put(i2, this.mPm.checkPermission("android.permission.SUSPEND_APPS", str, i2) == 0);
        }
        int identifier = userHandle == null ? -1 : userHandle.getIdentifier();
        if ((isSystemApp && (i & 4) == 0) || identifier == -1) {
            z3 = true;
        } else {
            synchronized (this.mPm.mLock) {
                markPackageUninstalledForUserLPw(packageSetting, userHandle);
                if (!isSystemApp) {
                    boolean shouldKeepUninstalledPackageLPr = this.mPm.shouldKeepUninstalledPackageLPr(str);
                    if (!packageSetting.isAnyInstalled(this.mUserManagerInternal.getUserIds()) && !shouldKeepUninstalledPackageLPr) {
                        z3 = true;
                        packageSetting.setInstalled(true, identifier);
                        this.mPm.mSettings.writeKernelMappingLPr(packageSetting);
                        z4 = false;
                    }
                }
                z3 = true;
                z4 = true;
            }
            if (z4) {
                clearPackageStateForUserLIF(packageSetting, identifier, packageRemovedInfo, i);
                this.mPm.scheduleWritePackageRestrictions(userHandle);
                return;
            }
        }
        if (isSystemApp) {
            deleteInstalledSystemPackage(deletePackageAction, iArr, z2);
            new InstallPackageHelper(this.mPm).restoreDisabledSystemPackageLIF(deletePackageAction, iArr, z2);
        } else {
            deleteInstalledPackageLIF(packageSetting, z, i, iArr, packageRemovedInfo, z2);
        }
        int[] iArr2 = packageRemovedInfo != null ? packageRemovedInfo.mRemovedUsers : null;
        if (iArr2 == null) {
            iArr2 = this.mPm.resolveUserIds(identifier);
        }
        Computer snapshotComputer = this.mPm.snapshotComputer();
        for (int i3 : iArr2) {
            if (sparseBooleanArray.get(i3)) {
                this.mPm.unsuspendForSuspendingPackage(snapshotComputer, str, i3);
                this.mPm.removeAllDistractingPackageRestrictions(snapshotComputer, i3);
            }
        }
        if (packageRemovedInfo != null) {
            synchronized (this.mPm.mLock) {
                packageRemovedInfo.mRemovedForAllUsers = this.mPm.mPackages.get(packageSetting.getPackageName()) == null ? z3 : false;
            }
        }
    }

    public final void clearPackageStateForUserLIF(PackageSetting packageSetting, int i, PackageRemovedInfo packageRemovedInfo, int i2) {
        AndroidPackage androidPackage;
        SharedUserSetting sharedUserSettingLPr;
        int[] iArr;
        synchronized (this.mPm.mLock) {
            androidPackage = this.mPm.mPackages.get(packageSetting.getPackageName());
            sharedUserSettingLPr = this.mPm.mSettings.getSharedUserSettingLPr(packageSetting);
        }
        this.mAppDataHelper.destroyAppProfilesLIF(androidPackage);
        List<AndroidPackage> packages = sharedUserSettingLPr != null ? sharedUserSettingLPr.getPackages() : Collections.emptyList();
        PreferredActivityHelper preferredActivityHelper = new PreferredActivityHelper(this.mPm);
        if (i == -1) {
            iArr = this.mUserManagerInternal.getUserIds();
        } else {
            iArr = new int[]{i};
        }
        int[] iArr2 = iArr;
        boolean z = false;
        for (int i3 : iArr2) {
            if ((i2 & 1) == 0) {
                this.mAppDataHelper.destroyAppDataLIF(androidPackage, i3, 7);
            }
            this.mAppDataHelper.clearKeystoreData(i3, packageSetting.getAppId());
            preferredActivityHelper.clearPackagePreferredActivities(packageSetting.getPackageName(), i3);
            this.mPm.mDomainVerificationManager.clearPackageForUser(packageSetting.getPackageName(), i3);
        }
        this.mPermissionManager.onPackageUninstalled(packageSetting.getPackageName(), packageSetting.getAppId(), packageSetting, androidPackage, packages, i);
        if (packageRemovedInfo != null) {
            if ((i2 & 1) == 0) {
                packageRemovedInfo.mDataRemoved = true;
            }
            packageRemovedInfo.mRemovedPackage = packageSetting.getPackageName();
            packageRemovedInfo.mInstallerPackageName = packageSetting.getInstallSource().mInstallerPackageName;
            if (androidPackage != null && androidPackage.getStaticSharedLibraryName() != null) {
                z = true;
            }
            packageRemovedInfo.mIsStaticSharedLib = z;
            packageRemovedInfo.mRemovedAppId = packageSetting.getAppId();
            packageRemovedInfo.mRemovedUsers = iArr2;
            packageRemovedInfo.mBroadcastUsers = iArr2;
            packageRemovedInfo.mIsExternal = packageSetting.isExternalStorage();
            packageRemovedInfo.mRemovedPackageVersionCode = packageSetting.getVersionCode();
        }
    }

    @GuardedBy({"mPm.mInstallLock"})
    public final void deleteInstalledPackageLIF(PackageSetting packageSetting, boolean z, int i, int[] iArr, PackageRemovedInfo packageRemovedInfo, boolean z2) {
        synchronized (this.mPm.mLock) {
            if (packageRemovedInfo != null) {
                packageRemovedInfo.mUid = packageSetting.getAppId();
                PackageManagerService packageManagerService = this.mPm;
                packageRemovedInfo.mBroadcastAllowList = packageManagerService.mAppsFilter.getVisibilityAllowList(packageManagerService.snapshotComputer(), packageSetting, iArr, this.mPm.mSettings.getPackagesLocked());
            }
        }
        this.mRemovePackageHelper.removePackageDataLIF(packageSetting, iArr, packageRemovedInfo, i, z2);
        if (!z || packageRemovedInfo == null) {
            return;
        }
        packageRemovedInfo.mArgs = new InstallArgs(packageSetting.getPathString(), InstructionSets.getAppDexInstructionSets(packageSetting.getPrimaryCpuAbiLegacy(), packageSetting.getSecondaryCpuAbiLegacy()));
    }

    @GuardedBy({"mPm.mLock"})
    public final void markPackageUninstalledForUserLPw(PackageSetting packageSetting, UserHandle userHandle) {
        int[] userIds;
        if (userHandle == null || userHandle.getIdentifier() == -1) {
            userIds = this.mUserManagerInternal.getUserIds();
        } else {
            userIds = new int[]{userHandle.getIdentifier()};
        }
        for (int i : userIds) {
            packageSetting.setUserState(i, 0L, 0, false, true, true, false, 0, null, false, false, null, null, null, 0, 0, null, null, 0L);
        }
        this.mPm.mSettings.writeKernelMappingLPr(packageSetting);
    }

    public final void deleteInstalledSystemPackage(DeletePackageAction deletePackageAction, int[] iArr, boolean z) {
        int i = deletePackageAction.mFlags;
        PackageSetting packageSetting = deletePackageAction.mDeletingPs;
        PackageRemovedInfo packageRemovedInfo = deletePackageAction.mRemovedInfo;
        if (packageRemovedInfo != null) {
            int[] iArr2 = packageRemovedInfo.mOrigUsers;
        }
        packageSetting.getPkg();
        PackageSetting packageSetting2 = deletePackageAction.mDisabledPs;
        Slog.d("PackageManager", "Deleting system pkg from data partition");
        if (packageRemovedInfo != null) {
            packageRemovedInfo.mIsRemovedPackageSystemUpdate = true;
        }
        int i2 = (packageSetting2.getVersionCode() < packageSetting.getVersionCode() || packageSetting2.getAppId() != packageSetting.getAppId()) ? i & (-2) : i | 1;
        synchronized (this.mPm.mInstallLock) {
            deleteInstalledPackageLIF(packageSetting, true, i2, iArr, packageRemovedInfo, z);
        }
    }

    public void deletePackageVersionedInternal(VersionedPackage versionedPackage, final IPackageDeleteObserver2 iPackageDeleteObserver2, final int i, final int i2, boolean z) {
        final int callingUid = Binder.getCallingUid();
        this.mPm.mContext.enforceCallingOrSelfPermission("android.permission.DELETE_PACKAGES", null);
        Computer snapshotComputer = this.mPm.snapshotComputer();
        final boolean canViewInstantApps = snapshotComputer.canViewInstantApps(callingUid, i);
        Preconditions.checkNotNull(versionedPackage);
        Preconditions.checkNotNull(iPackageDeleteObserver2);
        Preconditions.checkArgumentInRange(versionedPackage.getLongVersionCode(), -1L, Long.MAX_VALUE, "versionCode must be >= -1");
        final String packageName = versionedPackage.getPackageName();
        final long longVersionCode = versionedPackage.getLongVersionCode();
        if (this.mPm.mProtectedPackages.isPackageDataProtected(i, packageName)) {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DeletePackageHelper.lambda$deletePackageVersionedInternal$0(packageName, iPackageDeleteObserver2);
                }
            });
            return;
        }
        try {
            if (((ActivityTaskManagerInternal) this.mPm.mInjector.getLocalService(ActivityTaskManagerInternal.class)).isBaseOfLockedTask(packageName)) {
                iPackageDeleteObserver2.onPackageDeleted(packageName, -7, (String) null);
                EventLog.writeEvent(1397638484, "127605586", -1, "");
                return;
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        final String resolveInternalPackageName = snapshotComputer.resolveInternalPackageName(packageName, longVersionCode);
        int callingUid2 = Binder.getCallingUid();
        if (!isOrphaned(snapshotComputer, resolveInternalPackageName) && !z && !isCallerAllowedToSilentlyUninstall(snapshotComputer, callingUid2, resolveInternalPackageName, i)) {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DeletePackageHelper.lambda$deletePackageVersionedInternal$1(packageName, iPackageDeleteObserver2);
                }
            });
            return;
        }
        final boolean z2 = (i2 & 2) != 0;
        final int[] userIds = z2 ? this.mUserManagerInternal.getUserIds() : new int[]{i};
        if (UserHandle.getUserId(callingUid2) != i || (z2 && userIds.length > 1)) {
            Context context = this.mPm.mContext;
            context.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "deletePackage for user " + i);
        }
        if (this.mPm.isUserRestricted(i, "no_uninstall_apps")) {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DeletePackageHelper.lambda$deletePackageVersionedInternal$2(iPackageDeleteObserver2, packageName);
                }
            });
        } else if (!z2 && snapshotComputer.getBlockUninstallForUser(resolveInternalPackageName, i)) {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    DeletePackageHelper.lambda$deletePackageVersionedInternal$3(iPackageDeleteObserver2, packageName);
                }
            });
        } else {
            this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    DeletePackageHelper.this.lambda$deletePackageVersionedInternal$4(resolveInternalPackageName, callingUid, canViewInstantApps, z2, longVersionCode, i, i2, userIds, iPackageDeleteObserver2, packageName);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$deletePackageVersionedInternal$0(String str, IPackageDeleteObserver2 iPackageDeleteObserver2) {
        try {
            Slog.w("PackageManager", "Attempted to delete protected package: " + str);
            iPackageDeleteObserver2.onPackageDeleted(str, -1, (String) null);
        } catch (RemoteException unused) {
        }
    }

    public static /* synthetic */ void lambda$deletePackageVersionedInternal$1(String str, IPackageDeleteObserver2 iPackageDeleteObserver2) {
        try {
            Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
            intent.setData(Uri.fromParts("package", str, null));
            intent.putExtra("android.content.pm.extra.CALLBACK", iPackageDeleteObserver2.asBinder());
            iPackageDeleteObserver2.onUserActionRequired(intent);
        } catch (RemoteException unused) {
        }
    }

    public static /* synthetic */ void lambda$deletePackageVersionedInternal$2(IPackageDeleteObserver2 iPackageDeleteObserver2, String str) {
        try {
            iPackageDeleteObserver2.onPackageDeleted(str, -3, (String) null);
        } catch (RemoteException unused) {
        }
    }

    public static /* synthetic */ void lambda$deletePackageVersionedInternal$3(IPackageDeleteObserver2 iPackageDeleteObserver2, String str) {
        try {
            iPackageDeleteObserver2.onPackageDeleted(str, -4, (String) null);
        } catch (RemoteException unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deletePackageVersionedInternal$4(String str, int i, boolean z, boolean z2, long j, int i2, int i3, int[] iArr, IPackageDeleteObserver2 iPackageDeleteObserver2, String str2) {
        int i4;
        int i5;
        int i6;
        int[] iArr2;
        int i7;
        int i8;
        UserProperties userProperties;
        Computer snapshotComputer = this.mPm.snapshotComputer();
        PackageStateInternal packageStateInternal = snapshotComputer.getPackageStateInternal(str);
        int i9 = 0;
        if (!(packageStateInternal == null || !packageStateInternal.getUserStateOrDefault(UserHandle.getUserId(i)).isInstantApp() || z)) {
            i4 = -1;
        } else if (!z2) {
            int deletePackageX = deletePackageX(str, j, i2, i3, false);
            int[] profileIds = this.mUserManagerInternal.getProfileIds(i2, true);
            int length = profileIds.length;
            int i10 = 0;
            i4 = deletePackageX;
            while (i10 < length) {
                int i11 = profileIds[i10];
                if (i11 == i2 || (userProperties = this.mUserManagerInternal.getUserProperties(i11)) == null || !userProperties.getDeleteAppWithParent()) {
                    i7 = i10;
                    i8 = length;
                } else {
                    i7 = i10;
                    i8 = length;
                    int deletePackageX2 = deletePackageX(str, j, i11, i3, false);
                    if (deletePackageX2 != 1) {
                        Slog.w("PackageManager", "Package delete failed for user " + i11 + ", returnCode " + deletePackageX2);
                        i4 = -8;
                    }
                }
                i10 = i7 + 1;
                length = i8;
            }
        } else {
            int[] blockUninstallForUsers = getBlockUninstallForUsers(snapshotComputer, str, iArr);
            if (ArrayUtils.isEmpty(blockUninstallForUsers)) {
                i5 = deletePackageX(str, j, i2, i3, false);
            } else {
                int i12 = i3 & (-3);
                int length2 = iArr.length;
                while (i9 < length2) {
                    int i13 = iArr[i9];
                    if (ArrayUtils.contains(blockUninstallForUsers, i13)) {
                        i6 = length2;
                        iArr2 = blockUninstallForUsers;
                    } else {
                        i6 = length2;
                        iArr2 = blockUninstallForUsers;
                        int deletePackageX3 = deletePackageX(str, j, i13, i12, false);
                        if (deletePackageX3 != 1) {
                            Slog.w("PackageManager", "Package delete failed for user " + i13 + ", returnCode " + deletePackageX3);
                        }
                    }
                    i9++;
                    length2 = i6;
                    blockUninstallForUsers = iArr2;
                }
                i5 = -4;
            }
            i4 = i5;
        }
        try {
            iPackageDeleteObserver2.onPackageDeleted(str2, i4, (String) null);
        } catch (RemoteException unused) {
            Log.i("PackageManager", "Observer no longer exists.");
        }
        this.mPm.schedulePruneUnusedStaticSharedLibraries(true);
    }

    public final boolean isOrphaned(Computer computer, String str) {
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
        return packageStateInternal != null && packageStateInternal.getInstallSource().mIsOrphaned;
    }

    public final boolean isCallerAllowedToSilentlyUninstall(Computer computer, int i, String str, int i2) {
        if (PackageManagerServiceUtils.isRootOrShell(i) || UserHandle.getAppId(i) == 1000) {
            return true;
        }
        int userId = UserHandle.getUserId(i);
        if (i == computer.getPackageUid(computer.getInstallerPackageName(str, i2), 0L, userId)) {
            return true;
        }
        for (String str2 : this.mPm.mRequiredVerifierPackages) {
            if (i == computer.getPackageUid(str2, 0L, userId)) {
                return true;
            }
        }
        String str3 = this.mPm.mRequiredUninstallerPackage;
        if (str3 == null || i != computer.getPackageUid(str3, 0L, userId)) {
            String str4 = this.mPm.mStorageManagerPackage;
            return (str4 != null && i == computer.getPackageUid(str4, 0L, userId)) || computer.checkUidPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS", i) == 0;
        }
        return true;
    }

    public final int[] getBlockUninstallForUsers(Computer computer, String str, int[] iArr) {
        int[] iArr2 = PackageManagerService.EMPTY_INT_ARRAY;
        for (int i : iArr) {
            if (computer.getBlockUninstallForUser(str, i)) {
                iArr2 = ArrayUtils.appendInt(iArr2, i);
            }
        }
        return iArr2;
    }

    /* renamed from: com.android.server.pm.DeletePackageHelper$TempUserState */
    /* loaded from: classes2.dex */
    public static class TempUserState {
        public final int enabledState;
        public final boolean installed;
        public final String lastDisableAppCaller;

        public TempUserState(int i, String str, boolean z) {
            this.enabledState = i;
            this.lastDisableAppCaller = str;
            this.installed = z;
        }
    }

    @GuardedBy({"mPm.mLock"})
    public void removeUnusedPackagesLPw(UserManagerService userManagerService, final int i) {
        int[] userIds = userManagerService.getUserIds();
        int size = this.mPm.mSettings.getPackagesLocked().size();
        for (int i2 = 0; i2 < size; i2++) {
            PackageSetting valueAt = this.mPm.mSettings.getPackagesLocked().valueAt(i2);
            if (valueAt.getPkg() != null) {
                final String packageName = valueAt.getPkg().getPackageName();
                boolean z = true;
                if ((valueAt.getFlags() & 1) == 0 && TextUtils.isEmpty(valueAt.getPkg().getStaticSharedLibraryName()) && TextUtils.isEmpty(valueAt.getPkg().getSdkLibraryName())) {
                    boolean shouldKeepUninstalledPackageLPr = this.mPm.shouldKeepUninstalledPackageLPr(packageName);
                    if (!shouldKeepUninstalledPackageLPr) {
                        for (int i3 : userIds) {
                            if (i3 != i && valueAt.getInstalled(i3)) {
                                break;
                            }
                        }
                    }
                    z = shouldKeepUninstalledPackageLPr;
                    if (!z) {
                        this.mPm.mHandler.post(new Runnable() { // from class: com.android.server.pm.DeletePackageHelper$$ExternalSyntheticLambda5
                            @Override // java.lang.Runnable
                            public final void run() {
                                DeletePackageHelper.this.lambda$removeUnusedPackagesLPw$5(packageName, i);
                            }
                        });
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeUnusedPackagesLPw$5(String str, int i) {
        deletePackageX(str, -1L, i, 0, true);
    }

    public void deleteExistingPackageAsUser(VersionedPackage versionedPackage, IPackageDeleteObserver2 iPackageDeleteObserver2, int i) {
        int length;
        this.mPm.mContext.enforceCallingOrSelfPermission("android.permission.DELETE_PACKAGES", null);
        Preconditions.checkNotNull(versionedPackage);
        Preconditions.checkNotNull(iPackageDeleteObserver2);
        String packageName = versionedPackage.getPackageName();
        long longVersionCode = versionedPackage.getLongVersionCode();
        synchronized (this.mPm.mLock) {
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(this.mPm.snapshotComputer().resolveInternalPackageName(packageName, longVersionCode));
            length = packageLPr != null ? packageLPr.queryInstalledUsers(this.mUserManagerInternal.getUserIds(), true).length : 0;
        }
        if (length > 1) {
            deletePackageVersionedInternal(versionedPackage, iPackageDeleteObserver2, i, 0, true);
            return;
        }
        try {
            iPackageDeleteObserver2.onPackageDeleted(packageName, -1, (String) null);
        } catch (RemoteException unused) {
        }
    }
}
