package com.android.server.p011pm.parsing.pkg;

import android.content.pm.SigningDetails;
import com.android.server.p011pm.pkg.AndroidPackage;
/* renamed from: com.android.server.pm.parsing.pkg.ParsedPackage */
/* loaded from: classes2.dex */
public interface ParsedPackage extends AndroidPackage {
    ParsedPackage addUsesLibrary(int i, String str);

    ParsedPackage addUsesOptionalLibrary(int i, String str);

    ParsedPackage capPermissionPriorities();

    ParsedPackage clearAdoptPermissions();

    ParsedPackage clearOriginalPackages();

    ParsedPackage clearProtectedBroadcasts();

    AndroidPackageInternal hideAsFinal();

    ParsedPackage markNotActivitiesAsNotExportedIfSingleUser();

    ParsedPackage removePermission(int i);

    ParsedPackage removeUsesLibrary(String str);

    ParsedPackage removeUsesOptionalLibrary(String str);

    ParsedPackage setAllComponentsDirectBootAware(boolean z);

    ParsedPackage setApex(boolean z);

    ParsedPackage setBaseApkPath(String str);

    ParsedPackage setCoreApp(boolean z);

    ParsedPackage setDefaultToDeviceProtectedStorage(boolean z);

    ParsedPackage setDirectBootAware(boolean z);

    ParsedPackage setFactoryTest(boolean z);

    ParsedPackage setNativeLibraryDir(String str);

    ParsedPackage setNativeLibraryRootDir(String str);

    ParsedPackage setNativeLibraryRootRequiresIsa(boolean z);

    ParsedPackage setOdm(boolean z);

    ParsedPackage setOem(boolean z);

    ParsedPackage setPackageName(String str);

    ParsedPackage setPath(String str);

    ParsedPackage setPersistent(boolean z);

    ParsedPackage setPrimaryCpuAbi(String str);

    ParsedPackage setPrivileged(boolean z);

    ParsedPackage setProduct(boolean z);

    ParsedPackage setRestrictUpdateHash(byte[] bArr);

    ParsedPackage setSecondaryCpuAbi(String str);

    ParsedPackage setSecondaryNativeLibraryDir(String str);

    ParsedPackage setSignedWithPlatformKey(boolean z);

    ParsedPackage setSigningDetails(SigningDetails signingDetails);

    ParsedPackage setSplitCodePaths(String[] strArr);

    ParsedPackage setStub(boolean z);

    ParsedPackage setSystem(boolean z);

    ParsedPackage setSystemExt(boolean z);

    ParsedPackage setUid(int i);

    ParsedPackage setVendor(boolean z);

    ParsedPackage setVersionCode(int i);

    ParsedPackage setVersionCodeMajor(int i);
}
