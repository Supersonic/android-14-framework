package com.android.server.p011pm.pkg;

import android.annotation.SystemApi;
import android.content.pm.SigningInfo;
import android.os.UserHandle;
import android.util.SparseArray;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.pm.pkg.PackageState */
/* loaded from: classes2.dex */
public interface PackageState {
    AndroidPackage getAndroidPackage();

    String getApexModuleName();

    int getAppId();

    int getCategoryOverride();

    String getCpuAbiOverride();

    int getHiddenApiEnforcementPolicy();

    long getLastModifiedTime();

    long[] getLastPackageUsageTime();

    long getLastUpdateTime();

    Map<String, Set<String>> getMimeGroups();

    String getPackageName();

    File getPath();

    String getPrimaryCpuAbi();

    String getSeInfo();

    String getSecondaryCpuAbi();

    List<SharedLibrary> getSharedLibraryDependencies();

    int getSharedUserAppId();

    SigningInfo getSigningInfo();

    PackageUserState getStateForUser(UserHandle userHandle);

    SparseArray<? extends PackageUserState> getUserStates();

    List<String> getUsesLibraryFiles();

    String[] getUsesSdkLibraries();

    long[] getUsesSdkLibrariesVersionsMajor();

    String[] getUsesStaticLibraries();

    long[] getUsesStaticLibrariesVersions();

    long getVersionCode();

    String getVolumeUuid();

    boolean hasSharedUser();

    boolean isApex();

    boolean isApkInUpdatedApex();

    boolean isExternalStorage();

    boolean isForceQueryableOverride();

    boolean isHiddenUntilInstalled();

    boolean isInstallPermissionsFixed();

    boolean isOdm();

    boolean isOem();

    boolean isPrivileged();

    boolean isProduct();

    boolean isRequiredForSystemUser();

    boolean isSystem();

    boolean isSystemExt();

    boolean isUpdateAvailable();

    boolean isUpdatedSystemApp();

    boolean isVendor();

    default PackageUserState getUserStateOrDefault(int i) {
        PackageUserState packageUserState = getUserStates().get(i);
        return packageUserState == null ? PackageUserState.DEFAULT : packageUserState;
    }
}
