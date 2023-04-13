package com.android.server.p011pm;

import android.content.pm.SharedLibraryInfo;
import android.content.pm.SigningDetails;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import android.util.apk.ApkSignatureVerifier;
import android.util.jar.StrictJarFile;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.SystemConfig;
import com.android.server.p011pm.PackageAbiHelper;
import com.android.server.p011pm.Settings;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.parsing.library.PackageBackwardCompatibility;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageStateUtils;
import com.android.server.p011pm.pkg.component.ComponentMutateUtils;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedMainComponent;
import com.android.server.p011pm.pkg.component.ParsedProcess;
import com.android.server.p011pm.pkg.component.ParsedProvider;
import com.android.server.p011pm.pkg.component.ParsedService;
import com.android.server.p011pm.pkg.parsing.ParsingPackageUtils;
import com.android.server.utils.WatchedArraySet;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
/* renamed from: com.android.server.pm.ScanPackageUtils */
/* loaded from: classes2.dex */
public final class ScanPackageUtils {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0344  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x034e  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x0357  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0369  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x037d  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x03ba  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x03cf  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0419  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x041f  */
    /* JADX WARN: Removed duplicated region for block: B:155:0x042b  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x0431  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x043d  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0468  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0089  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00a9  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00ee  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00f5  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0159  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x01d3  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01d6  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01dd  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01f3  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0222  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x023b  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x02a1  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x02c4  */
    /* JADX WARN: Type inference failed for: r4v64 */
    /* JADX WARN: Type inference failed for: r4v65, types: [com.android.server.pm.pkg.PackageStateInternal] */
    /* JADX WARN: Type inference failed for: r4v66 */
    @GuardedBy({"mPm.mInstallLock"})
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static ScanResult scanPackageOnlyLI(ScanRequest scanRequest, PackageManagerServiceInjector packageManagerServiceInjector, boolean z, long j) throws PackageManagerException {
        String str;
        String str2;
        File file;
        String[] strArr;
        boolean z2;
        String[] strArr2;
        boolean z3;
        UserHandle userHandle;
        SharedUserSetting sharedUserSetting;
        boolean z4;
        ArrayList arrayList;
        PackageSetting packageSetting;
        int identifier;
        boolean isSystem;
        int i;
        SharedUserSetting sharedUserSetting2;
        int i2;
        SharedLibraryInfo sharedLibraryInfo;
        int i3;
        long firstInstallTimeMillis;
        String volumeUuid;
        ArrayList arrayList2;
        boolean z5;
        ?? r4;
        String str3;
        PackageAbiHelper abiHelper = packageManagerServiceInjector.getAbiHelper();
        ParsedPackage parsedPackage = scanRequest.mParsedPackage;
        PackageSetting packageSetting2 = scanRequest.mPkgSetting;
        PackageSetting packageSetting3 = scanRequest.mDisabledPkgSetting;
        PackageSetting packageSetting4 = scanRequest.mOriginalPkgSetting;
        int i4 = scanRequest.mParseFlags;
        int i5 = scanRequest.mScanFlags;
        String str4 = scanRequest.mRealPkgName;
        SharedUserSetting sharedUserSetting3 = scanRequest.mOldSharedUserSetting;
        SharedUserSetting sharedUserSetting4 = scanRequest.mSharedUserSetting;
        UserHandle userHandle2 = scanRequest.mUser;
        boolean z6 = scanRequest.mIsPlatformPackage;
        File file2 = new File(parsedPackage.getPath());
        boolean z7 = (i5 & IInstalld.FLAG_USE_QUOTA) != 0;
        if (!z7) {
            if (packageSetting2 != null && (packageSetting2.getPkg() == null || !packageSetting2.getPkg().isStub())) {
                str = packageSetting2.getPrimaryCpuAbiLegacy();
                str2 = packageSetting2.getSecondaryCpuAbiLegacy();
                if (packageSetting2 != null && sharedUserSetting3 != sharedUserSetting4) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Package ");
                    sb.append(parsedPackage.getPackageName());
                    sb.append(" shared user changed from ");
                    String str5 = "<nothing>";
                    if (sharedUserSetting3 == null) {
                        str3 = "<nothing>";
                        str5 = sharedUserSetting3.name;
                    } else {
                        str3 = "<nothing>";
                    }
                    sb.append(str5);
                    sb.append(" to ");
                    sb.append(sharedUserSetting4 == null ? sharedUserSetting4.name : str3);
                    sb.append("; replacing with new");
                    PackageManagerService.reportSettingsProblem(5, sb.toString());
                    packageSetting2 = null;
                }
                if (parsedPackage.getUsesSdkLibraries().isEmpty()) {
                    strArr = new String[parsedPackage.getUsesSdkLibraries().size()];
                    file = file2;
                    parsedPackage.getUsesSdkLibraries().toArray(strArr);
                } else {
                    file = file2;
                    strArr = null;
                }
                if (parsedPackage.getUsesStaticLibraries().isEmpty()) {
                    String[] strArr3 = new String[parsedPackage.getUsesStaticLibraries().size()];
                    z2 = z6;
                    parsedPackage.getUsesStaticLibraries().toArray(strArr3);
                    strArr2 = strArr3;
                } else {
                    z2 = z6;
                    strArr2 = null;
                }
                UUID generateNewId = packageManagerServiceInjector.getDomainVerificationManagerInternal().generateNewId();
                z3 = packageSetting2 != null;
                if (!z3) {
                    boolean z8 = (i5 & IInstalld.FLAG_FORCE) != 0;
                    boolean z9 = (32768 & i5) != 0;
                    if ((134217728 & i5) != 0) {
                        r4 = 0;
                        z5 = true;
                    } else {
                        z5 = false;
                        r4 = 0;
                    }
                    z4 = z2;
                    userHandle = userHandle2;
                    sharedUserSetting = sharedUserSetting4;
                    PackageSetting createNewSetting = Settings.createNewSetting(parsedPackage.getPackageName(), packageSetting4, packageSetting3, str4, sharedUserSetting4, file, parsedPackage.getNativeLibraryRootDir(), AndroidPackageUtils.getRawPrimaryCpuAbi(parsedPackage), AndroidPackageUtils.getRawSecondaryCpuAbi(parsedPackage), parsedPackage.getLongVersionCode(), PackageInfoUtils.appInfoFlags(parsedPackage, (PackageStateInternal) r4), PackageInfoUtils.appInfoPrivateFlags(parsedPackage, (PackageStateInternal) r4), userHandle, true, z8, z9, z5, UserManagerService.getInstance(), strArr, parsedPackage.getUsesSdkLibrariesVersionsMajor(), strArr2, parsedPackage.getUsesStaticLibrariesVersions(), parsedPackage.getMimeGroups(), generateNewId);
                    arrayList = r4;
                    packageSetting = createNewSetting;
                } else {
                    userHandle = userHandle2;
                    sharedUserSetting = sharedUserSetting4;
                    z4 = z2;
                    arrayList = null;
                    PackageSetting packageSetting5 = new PackageSetting(packageSetting2);
                    packageSetting5.setPkg(parsedPackage);
                    Settings.updatePackageSetting(packageSetting5, packageSetting3, sharedUserSetting3, sharedUserSetting, file, parsedPackage.getNativeLibraryDir(), packageSetting5.getPrimaryCpuAbi(), packageSetting5.getSecondaryCpuAbi(), PackageInfoUtils.appInfoFlags(parsedPackage, packageSetting5), PackageInfoUtils.appInfoPrivateFlags(parsedPackage, packageSetting5), UserManagerService.getInstance(), strArr, parsedPackage.getUsesSdkLibrariesVersionsMajor(), strArr2, parsedPackage.getUsesStaticLibrariesVersions(), parsedPackage.getMimeGroups(), generateNewId);
                    packageSetting = packageSetting5;
                }
                if (z3 && packageSetting4 != null) {
                    parsedPackage.setPackageName(packageSetting4.getPackageName());
                    PackageManagerService.reportSettingsProblem(5, "New package " + packageSetting.getRealName() + " renamed to replace old package " + packageSetting.getPackageName());
                }
                identifier = userHandle != null ? 0 : userHandle.getIdentifier();
                if (!z3) {
                    setInstantAppForUser(packageManagerServiceInjector, packageSetting, identifier, (i5 & IInstalld.FLAG_FORCE) != 0, (i5 & 16384) != 0);
                }
                if (packageSetting3 == null || ((i5 & 4) != 0 && packageSetting != null && packageSetting.isSystem())) {
                    packageSetting.getPkgState().setUpdatedSystemApp(true);
                }
                packageSetting.getTransientState().setSeInfo(SELinuxMMAC.getSeInfo(packageSetting, parsedPackage, sharedUserSetting, packageManagerServiceInjector.getCompatibility()));
                if (packageSetting.isSystem()) {
                    configurePackageComponents(parsedPackage);
                }
                String deriveAbiOverride = PackageManagerServiceUtils.deriveAbiOverride(scanRequest.mCpuAbiOverride);
                isSystem = packageSetting.isSystem();
                boolean isUpdatedSystemApp = packageSetting.isUpdatedSystemApp();
                File appLib32InstallDir = getAppLib32InstallDir();
                i = i5 & 4;
                if (i == 0) {
                    sharedUserSetting2 = sharedUserSetting3;
                    i2 = i5;
                    sharedLibraryInfo = arrayList;
                    i3 = i4;
                    if ((i2 & 256) != 0) {
                        parsedPackage.setPrimaryCpuAbi(packageSetting.getPrimaryCpuAbiLegacy()).setSecondaryCpuAbi(packageSetting.getSecondaryCpuAbiLegacy());
                    }
                    abiHelper.deriveNativeLibraryPaths(parsedPackage, isSystem, isUpdatedSystemApp, appLib32InstallDir).applyTo(parsedPackage);
                } else if (z7) {
                    Trace.traceBegin(262144L, "derivePackageAbi");
                    sharedUserSetting2 = sharedUserSetting3;
                    i2 = i5;
                    sharedLibraryInfo = arrayList;
                    i3 = i4;
                    Pair<PackageAbiHelper.Abis, PackageAbiHelper.NativeLibraryPaths> derivePackageAbi = abiHelper.derivePackageAbi(parsedPackage, isSystem, isUpdatedSystemApp, deriveAbiOverride, appLib32InstallDir);
                    ((PackageAbiHelper.Abis) derivePackageAbi.first).applyTo(parsedPackage);
                    ((PackageAbiHelper.NativeLibraryPaths) derivePackageAbi.second).applyTo(parsedPackage);
                    Trace.traceEnd(262144L);
                    String rawPrimaryCpuAbi = AndroidPackageUtils.getRawPrimaryCpuAbi(parsedPackage);
                    if (isSystem && !isUpdatedSystemApp && rawPrimaryCpuAbi == null) {
                        PackageAbiHelper.Abis bundledAppAbis = abiHelper.getBundledAppAbis(parsedPackage);
                        bundledAppAbis.applyTo(parsedPackage);
                        bundledAppAbis.applyTo(packageSetting);
                        abiHelper.deriveNativeLibraryPaths(parsedPackage, isSystem, isUpdatedSystemApp, appLib32InstallDir).applyTo(parsedPackage);
                    }
                } else {
                    sharedUserSetting2 = sharedUserSetting3;
                    i2 = i5;
                    sharedLibraryInfo = arrayList;
                    i3 = i4;
                    parsedPackage.setPrimaryCpuAbi(str).setSecondaryCpuAbi(str2);
                    abiHelper.deriveNativeLibraryPaths(parsedPackage, isSystem, isUpdatedSystemApp, appLib32InstallDir).applyTo(parsedPackage);
                }
                if (z4) {
                    parsedPackage.setPrimaryCpuAbi(VMRuntime.getRuntime().is64Bit() ? Build.SUPPORTED_64_BIT_ABIS[0] : Build.SUPPORTED_32_BIT_ABIS[0]);
                }
                if ((i2 & 1) == 0 && i != 0 && deriveAbiOverride == null) {
                    Slog.w("PackageManager", "Ignoring persisted ABI override for package " + parsedPackage.getPackageName());
                }
                packageSetting.setPrimaryCpuAbi(AndroidPackageUtils.getRawPrimaryCpuAbi(parsedPackage)).setSecondaryCpuAbi(AndroidPackageUtils.getRawSecondaryCpuAbi(parsedPackage)).setCpuAbiOverride(deriveAbiOverride);
                packageSetting.setLegacyNativeLibraryPath(parsedPackage.getNativeLibraryRootDir());
                List<String> applyAdjustedAbiToSharedUser = ((i2 & 16) == 0 || sharedUserSetting2 == null) ? sharedLibraryInfo : applyAdjustedAbiToSharedUser(sharedUserSetting2, parsedPackage, abiHelper.getAdjustedAbiForSharedUser(sharedUserSetting2.getPackageStates(), parsedPackage));
                parsedPackage.setFactoryTest(!z && parsedPackage.getRequestedPermissions().contains("android.permission.FACTORY_TEST"));
                if (isSystem) {
                    packageSetting.setIsOrphaned(true);
                }
                long lastModifiedTime = PackageManagerServiceUtils.getLastModifiedTime(parsedPackage);
                if (identifier != -1) {
                    firstInstallTimeMillis = PackageStateUtils.getEarliestFirstInstallTime(packageSetting.getUserStates());
                } else {
                    firstInstallTimeMillis = packageSetting.readUserState(identifier).getFirstInstallTimeMillis();
                }
                List list = applyAdjustedAbiToSharedUser;
                if (j == 0) {
                    if (firstInstallTimeMillis == 0) {
                        packageSetting.setFirstInstallTime(j, identifier).setLastUpdateTime(j);
                    } else if ((i2 & 8) != 0) {
                        packageSetting.setLastUpdateTime(j);
                    }
                } else if (firstInstallTimeMillis == 0) {
                    packageSetting.setFirstInstallTime(lastModifiedTime, identifier).setLastUpdateTime(lastModifiedTime);
                } else if ((i3 & 16) != 0 && lastModifiedTime != packageSetting.getLastModifiedTime()) {
                    packageSetting.setLastUpdateTime(lastModifiedTime);
                }
                packageSetting.setLastModifiedTime(lastModifiedTime);
                packageSetting.setPkg(parsedPackage).setFlags(PackageInfoUtils.appInfoFlags(parsedPackage, packageSetting)).setPrivateFlags(PackageInfoUtils.appInfoPrivateFlags(parsedPackage, packageSetting));
                if (parsedPackage.getLongVersionCode() != packageSetting.getVersionCode()) {
                    packageSetting.setLongVersionCode(parsedPackage.getLongVersionCode());
                }
                volumeUuid = parsedPackage.getVolumeUuid();
                if (!Objects.equals(volumeUuid, packageSetting.getVolumeUuid())) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Update");
                    sb2.append(packageSetting.isSystem() ? " system" : "");
                    sb2.append(" package ");
                    sb2.append(parsedPackage.getPackageName());
                    sb2.append(" volume from ");
                    sb2.append(packageSetting.getVolumeUuid());
                    sb2.append(" to ");
                    sb2.append(volumeUuid);
                    Slog.i("PackageManager", sb2.toString());
                    packageSetting.setVolumeUuid(volumeUuid);
                }
                SharedLibraryInfo createSharedLibraryForSdk = TextUtils.isEmpty(parsedPackage.getSdkLibraryName()) ? AndroidPackageUtils.createSharedLibraryForSdk(parsedPackage) : sharedLibraryInfo;
                SharedLibraryInfo createSharedLibraryForStatic = TextUtils.isEmpty(parsedPackage.getStaticSharedLibraryName()) ? AndroidPackageUtils.createSharedLibraryForStatic(parsedPackage) : sharedLibraryInfo;
                if (ArrayUtils.isEmpty(parsedPackage.getLibraryNames())) {
                    ArrayList arrayList3 = new ArrayList(parsedPackage.getLibraryNames().size());
                    for (String str6 : parsedPackage.getLibraryNames()) {
                        arrayList3.add(AndroidPackageUtils.createSharedLibraryForDynamic(parsedPackage, str6));
                    }
                    arrayList2 = arrayList3;
                } else {
                    arrayList2 = sharedLibraryInfo;
                }
                return new ScanResult(scanRequest, packageSetting, list, !z3, -1, createSharedLibraryForSdk, createSharedLibraryForStatic, arrayList2);
            }
            z7 = true;
        }
        str = null;
        str2 = null;
        if (packageSetting2 != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Package ");
            sb3.append(parsedPackage.getPackageName());
            sb3.append(" shared user changed from ");
            String str52 = "<nothing>";
            if (sharedUserSetting3 == null) {
            }
            sb3.append(str52);
            sb3.append(" to ");
            sb3.append(sharedUserSetting4 == null ? sharedUserSetting4.name : str3);
            sb3.append("; replacing with new");
            PackageManagerService.reportSettingsProblem(5, sb3.toString());
            packageSetting2 = null;
        }
        if (parsedPackage.getUsesSdkLibraries().isEmpty()) {
        }
        if (parsedPackage.getUsesStaticLibraries().isEmpty()) {
        }
        UUID generateNewId2 = packageManagerServiceInjector.getDomainVerificationManagerInternal().generateNewId();
        if (packageSetting2 != null) {
        }
        if (!z3) {
        }
        if (z3) {
            parsedPackage.setPackageName(packageSetting4.getPackageName());
            PackageManagerService.reportSettingsProblem(5, "New package " + packageSetting.getRealName() + " renamed to replace old package " + packageSetting.getPackageName());
        }
        if (userHandle != null) {
        }
        if (!z3) {
        }
        if (packageSetting3 == null) {
        }
        packageSetting.getPkgState().setUpdatedSystemApp(true);
        packageSetting.getTransientState().setSeInfo(SELinuxMMAC.getSeInfo(packageSetting, parsedPackage, sharedUserSetting, packageManagerServiceInjector.getCompatibility()));
        if (packageSetting.isSystem()) {
        }
        String deriveAbiOverride2 = PackageManagerServiceUtils.deriveAbiOverride(scanRequest.mCpuAbiOverride);
        isSystem = packageSetting.isSystem();
        boolean isUpdatedSystemApp2 = packageSetting.isUpdatedSystemApp();
        File appLib32InstallDir2 = getAppLib32InstallDir();
        i = i5 & 4;
        if (i == 0) {
        }
        if (z4) {
        }
        if ((i2 & 1) == 0) {
            Slog.w("PackageManager", "Ignoring persisted ABI override for package " + parsedPackage.getPackageName());
        }
        packageSetting.setPrimaryCpuAbi(AndroidPackageUtils.getRawPrimaryCpuAbi(parsedPackage)).setSecondaryCpuAbi(AndroidPackageUtils.getRawSecondaryCpuAbi(parsedPackage)).setCpuAbiOverride(deriveAbiOverride2);
        packageSetting.setLegacyNativeLibraryPath(parsedPackage.getNativeLibraryRootDir());
        if ((i2 & 16) == 0) {
        }
        parsedPackage.setFactoryTest(!z && parsedPackage.getRequestedPermissions().contains("android.permission.FACTORY_TEST"));
        if (isSystem) {
        }
        long lastModifiedTime2 = PackageManagerServiceUtils.getLastModifiedTime(parsedPackage);
        if (identifier != -1) {
        }
        List list2 = applyAdjustedAbiToSharedUser;
        if (j == 0) {
        }
        packageSetting.setLastModifiedTime(lastModifiedTime2);
        packageSetting.setPkg(parsedPackage).setFlags(PackageInfoUtils.appInfoFlags(parsedPackage, packageSetting)).setPrivateFlags(PackageInfoUtils.appInfoPrivateFlags(parsedPackage, packageSetting));
        if (parsedPackage.getLongVersionCode() != packageSetting.getVersionCode()) {
        }
        volumeUuid = parsedPackage.getVolumeUuid();
        if (!Objects.equals(volumeUuid, packageSetting.getVolumeUuid())) {
        }
        if (TextUtils.isEmpty(parsedPackage.getSdkLibraryName())) {
        }
        if (TextUtils.isEmpty(parsedPackage.getStaticSharedLibraryName())) {
        }
        if (ArrayUtils.isEmpty(parsedPackage.getLibraryNames())) {
        }
        return new ScanResult(scanRequest, packageSetting, list2, !z3, -1, createSharedLibraryForSdk, createSharedLibraryForStatic, arrayList2);
    }

    public static int adjustScanFlagsWithPackageSetting(int i, PackageSetting packageSetting, PackageSetting packageSetting2, UserHandle userHandle) {
        if ((i & 4) != 0 && packageSetting2 == null && packageSetting != null && packageSetting.isSystem()) {
            packageSetting2 = packageSetting;
        }
        if (packageSetting2 != null) {
            i |= 65536;
            if ((packageSetting2.getPrivateFlags() & 8) != 0) {
                i |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
            }
            if ((packageSetting2.getPrivateFlags() & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0) {
                i |= 262144;
            }
            if ((packageSetting2.getPrivateFlags() & 262144) != 0) {
                i |= 524288;
            }
            if ((packageSetting2.getPrivateFlags() & 524288) != 0) {
                i |= 1048576;
            }
            if ((packageSetting2.getPrivateFlags() & 2097152) != 0) {
                i |= 2097152;
            }
            if ((packageSetting2.getPrivateFlags() & 1073741824) != 0) {
                i |= 4194304;
            }
        }
        if (packageSetting != null) {
            int identifier = userHandle == null ? 0 : userHandle.getIdentifier();
            if (packageSetting.getInstantApp(identifier)) {
                i |= IInstalld.FLAG_FORCE;
            }
            return packageSetting.getVirtualPreload(identifier) ? i | 32768 : i;
        }
        return i;
    }

    public static void assertCodePolicy(AndroidPackage androidPackage) throws PackageManagerException {
        if (androidPackage.isDeclaredHavingCode() && !apkHasCode(androidPackage.getBaseApkPath())) {
            throw new PackageManagerException(-2, "Package " + androidPackage.getBaseApkPath() + " code is missing");
        } else if (!ArrayUtils.isEmpty(androidPackage.getSplitCodePaths())) {
            for (int i = 0; i < androidPackage.getSplitCodePaths().length; i++) {
                if (((androidPackage.getSplitFlags()[i] & 4) != 0) && !apkHasCode(androidPackage.getSplitCodePaths()[i])) {
                    throw new PackageManagerException(-2, "Package " + androidPackage.getSplitCodePaths()[i] + " code is missing");
                }
            }
        }
    }

    public static void assertStaticSharedLibraryIsValid(AndroidPackage androidPackage, int i) throws PackageManagerException {
        if (androidPackage.getTargetSdkVersion() < 26) {
            throw PackageManagerException.ofInternalError("Packages declaring static-shared libs must target O SDK or higher", -22);
        }
        if ((i & IInstalld.FLAG_FORCE) != 0) {
            throw PackageManagerException.ofInternalError("Packages declaring static-shared libs cannot be instant apps", -23);
        }
        if (!ArrayUtils.isEmpty(androidPackage.getOriginalPackages())) {
            throw PackageManagerException.ofInternalError("Packages declaring static-shared libs cannot be renamed", -24);
        }
        if (!ArrayUtils.isEmpty(androidPackage.getLibraryNames())) {
            throw PackageManagerException.ofInternalError("Packages declaring static-shared libs cannot declare dynamic libs", -25);
        }
        if (androidPackage.getSharedUserId() != null) {
            throw PackageManagerException.ofInternalError("Packages declaring static-shared libs cannot declare shared users", -26);
        }
        if (!androidPackage.getActivities().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare activities", -27);
        }
        if (!androidPackage.getServices().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare services", -28);
        }
        if (!androidPackage.getProviders().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare content providers", -29);
        }
        if (!androidPackage.getReceivers().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare broadcast receivers", -30);
        }
        if (!androidPackage.getPermissionGroups().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare permission groups", -31);
        }
        if (!androidPackage.getAttributions().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare features", -32);
        }
        if (!androidPackage.getPermissions().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare permissions", -33);
        }
        if (!androidPackage.getProtectedBroadcasts().isEmpty()) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot declare protected broadcasts", -34);
        }
        if (androidPackage.getOverlayTarget() != null) {
            throw PackageManagerException.ofInternalError("Static shared libs cannot be overlay targets", -35);
        }
    }

    public static void assertProcessesAreValid(AndroidPackage androidPackage) throws PackageManagerException {
        Map<String, ParsedProcess> processes = androidPackage.getProcesses();
        if (processes.isEmpty()) {
            return;
        }
        if (!processes.containsKey(androidPackage.getProcessName())) {
            throw new PackageManagerException(-122, "Can't install because application tag's process attribute " + androidPackage.getProcessName() + " (in package " + androidPackage.getPackageName() + ") is not included in the <processes> list");
        }
        assertPackageProcesses(androidPackage, androidPackage.getActivities(), processes, "activity");
        assertPackageProcesses(androidPackage, androidPackage.getServices(), processes, "service");
        assertPackageProcesses(androidPackage, androidPackage.getReceivers(), processes, "receiver");
        assertPackageProcesses(androidPackage, androidPackage.getProviders(), processes, "provider");
    }

    public static <T extends ParsedMainComponent> void assertPackageProcesses(AndroidPackage androidPackage, List<T> list, Map<String, ParsedProcess> map, String str) throws PackageManagerException {
        T t;
        if (list == null) {
            return;
        }
        for (int size = list.size() - 1; size >= 0; size--) {
            if (!map.containsKey(list.get(size).getProcessName())) {
                throw new PackageManagerException(-122, "Can't install because " + str + " " + t.getClassName() + "'s process attribute " + t.getProcessName() + " (in package " + androidPackage.getPackageName() + ") is not included in the <processes> list");
            }
        }
    }

    public static void assertMinSignatureSchemeIsValid(AndroidPackage androidPackage, int i) throws PackageManagerException {
        int minimumSignatureSchemeVersionForTargetSdk = ApkSignatureVerifier.getMinimumSignatureSchemeVersionForTargetSdk(androidPackage.getTargetSdkVersion());
        if (androidPackage.getSigningDetails().getSignatureSchemeVersion() >= minimumSignatureSchemeVersionForTargetSdk) {
            return;
        }
        throw new PackageManagerException(-103, "No signature found in package of version " + minimumSignatureSchemeVersionForTargetSdk + " or newer for package " + androidPackage.getPackageName());
    }

    public static String getRealPackageName(AndroidPackage androidPackage, String str, boolean z) {
        if (isPackageRenamed(androidPackage, str)) {
            return AndroidPackageUtils.getRealPackageOrNull(androidPackage, z);
        }
        return null;
    }

    public static boolean isPackageRenamed(AndroidPackage androidPackage, String str) {
        return androidPackage.getOriginalPackages().contains(str);
    }

    public static void ensurePackageRenamed(ParsedPackage parsedPackage, String str) {
        if (!parsedPackage.getOriginalPackages().contains(str) || parsedPackage.getPackageName().equals(str)) {
            return;
        }
        parsedPackage.setPackageName(str);
    }

    public static boolean apkHasCode(String str) {
        StrictJarFile strictJarFile = null;
        try {
            StrictJarFile strictJarFile2 = new StrictJarFile(str, false, false);
            try {
                boolean z = strictJarFile2.findEntry("classes.dex") != null;
                try {
                    strictJarFile2.close();
                } catch (IOException unused) {
                }
                return z;
            } catch (IOException unused2) {
                strictJarFile = strictJarFile2;
                if (strictJarFile != null) {
                    try {
                        strictJarFile.close();
                    } catch (IOException unused3) {
                    }
                }
                return false;
            } catch (Throwable th) {
                th = th;
                strictJarFile = strictJarFile2;
                if (strictJarFile != null) {
                    try {
                        strictJarFile.close();
                    } catch (IOException unused4) {
                    }
                }
                throw th;
            }
        } catch (IOException unused5) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void configurePackageComponents(AndroidPackage androidPackage) {
        ArrayMap<String, Boolean> componentsEnabledStates = SystemConfig.getInstance().getComponentsEnabledStates(androidPackage.getPackageName());
        if (componentsEnabledStates == null) {
            return;
        }
        for (int size = ArrayUtils.size(androidPackage.getActivities()) - 1; size >= 0; size--) {
            ParsedActivity parsedActivity = androidPackage.getActivities().get(size);
            Boolean bool = componentsEnabledStates.get(parsedActivity.getName());
            if (bool != null) {
                ComponentMutateUtils.setEnabled(parsedActivity, bool.booleanValue());
            }
        }
        for (int size2 = ArrayUtils.size(androidPackage.getReceivers()) - 1; size2 >= 0; size2--) {
            ParsedActivity parsedActivity2 = androidPackage.getReceivers().get(size2);
            Boolean bool2 = componentsEnabledStates.get(parsedActivity2.getName());
            if (bool2 != null) {
                ComponentMutateUtils.setEnabled(parsedActivity2, bool2.booleanValue());
            }
        }
        for (int size3 = ArrayUtils.size(androidPackage.getProviders()) - 1; size3 >= 0; size3--) {
            ParsedProvider parsedProvider = androidPackage.getProviders().get(size3);
            Boolean bool3 = componentsEnabledStates.get(parsedProvider.getName());
            if (bool3 != null) {
                ComponentMutateUtils.setEnabled(parsedProvider, bool3.booleanValue());
            }
        }
        for (int size4 = ArrayUtils.size(androidPackage.getServices()) - 1; size4 >= 0; size4--) {
            ParsedService parsedService = androidPackage.getServices().get(size4);
            Boolean bool4 = componentsEnabledStates.get(parsedService.getName());
            if (bool4 != null) {
                ComponentMutateUtils.setEnabled(parsedService, bool4.booleanValue());
            }
        }
    }

    public static int getVendorPartitionVersion() {
        String str = SystemProperties.get("ro.vndk.version");
        if (str.isEmpty()) {
            return 28;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            if (ArrayUtils.contains(Build.VERSION.ACTIVE_CODENAMES, str)) {
                return FrameworkStatsLog.WIFI_BYTES_TRANSFER;
            }
            return 28;
        }
    }

    public static void applyPolicy(ParsedPackage parsedPackage, int i, AndroidPackage androidPackage, boolean z) {
        boolean z2;
        boolean z3 = true;
        if ((65536 & i) != 0) {
            parsedPackage.setSystem(true);
            if (parsedPackage.isDirectBootAware()) {
                parsedPackage.setAllComponentsDirectBootAware(true);
            }
            if (PackageManagerServiceUtils.compressedFileExists(parsedPackage.getPath())) {
                parsedPackage.setStub(true);
            }
            z2 = true;
        } else {
            parsedPackage.clearProtectedBroadcasts().setCoreApp(false).setPersistent(false).setDefaultToDeviceProtectedStorage(false).setDirectBootAware(false).capPermissionPriorities();
            z2 = z;
        }
        int i2 = 131072 & i;
        if (i2 == 0) {
            parsedPackage.markNotActivitiesAsNotExportedIfSingleUser();
        }
        parsedPackage.setApex((67108864 & i) != 0);
        parsedPackage.setPrivileged(i2 != 0).setOem((262144 & i) != 0).setVendor((524288 & i) != 0).setProduct((1048576 & i) != 0).setSystemExt((2097152 & i) != 0).setOdm((i & 4194304) != 0);
        if (!PackageManagerShellCommandDataLoader.PACKAGE.equals(parsedPackage.getPackageName()) && (androidPackage == null || PackageManagerServiceUtils.compareSignatures(androidPackage.getSigningDetails().getSignatures(), parsedPackage.getSigningDetails().getSignatures()) != 0)) {
            z3 = false;
        }
        parsedPackage.setSignedWithPlatformKey(z3);
        if (!z2) {
            parsedPackage.clearOriginalPackages().clearAdoptPermissions();
        }
        PackageBackwardCompatibility.modifySharedLibraries(parsedPackage, z2, z);
    }

    public static List<String> applyAdjustedAbiToSharedUser(SharedUserSetting sharedUserSetting, ParsedPackage parsedPackage, String str) {
        if (parsedPackage != null) {
            parsedPackage.setPrimaryCpuAbi(str);
        }
        WatchedArraySet<PackageSetting> packageSettings = sharedUserSetting.getPackageSettings();
        ArrayList arrayList = null;
        for (int i = 0; i < packageSettings.size(); i++) {
            PackageSetting valueAt = packageSettings.valueAt(i);
            if ((parsedPackage == null || !parsedPackage.getPackageName().equals(valueAt.getPackageName())) && valueAt.getPrimaryCpuAbiLegacy() == null) {
                valueAt.setPrimaryCpuAbi(str);
                valueAt.onChanged();
                if (valueAt.getPkg() != null && !TextUtils.equals(str, AndroidPackageUtils.getRawPrimaryCpuAbi(valueAt.getPkg()))) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(valueAt.getPathString());
                }
            }
        }
        return arrayList;
    }

    public static void collectCertificatesLI(PackageSetting packageSetting, ParsedPackage parsedPackage, Settings.VersionInfo versionInfo, boolean z, boolean z2, boolean z3) throws PackageManagerException {
        long lastModifiedTime;
        if (z3) {
            lastModifiedTime = new File(parsedPackage.getPath()).lastModified();
        } else {
            lastModifiedTime = PackageManagerServiceUtils.getLastModifiedTime(parsedPackage);
        }
        if (packageSetting != null && !z && packageSetting.getPathString().equals(parsedPackage.getPath()) && packageSetting.getLastModifiedTime() == lastModifiedTime && !ReconcilePackageUtils.isCompatSignatureUpdateNeeded(versionInfo) && !ReconcilePackageUtils.isRecoverSignatureUpdateNeeded(versionInfo)) {
            if (packageSetting.getSigningDetails().getSignatures() != null && packageSetting.getSigningDetails().getSignatures().length != 0 && packageSetting.getSigningDetails().getSignatureSchemeVersion() != 0) {
                parsedPackage.setSigningDetails(new SigningDetails(packageSetting.getSigningDetails()));
                return;
            }
            Slog.w("PackageManager", "PackageSetting for " + packageSetting.getPackageName() + " is missing signatures.  Collecting certs again to recover them.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(parsedPackage.getPath());
            sb.append(" changed; collecting certs");
            sb.append(z ? " (forced)" : "");
            Slog.i("PackageManager", sb.toString());
        }
        try {
            Trace.traceBegin(262144L, "collectCertificates");
            ParseResult<SigningDetails> signingDetails = ParsingPackageUtils.getSigningDetails((ParseInput) ParseTypeImpl.forDefaultParsing(), parsedPackage, z2);
            if (signingDetails.isError()) {
                throw new PackageManagerException(signingDetails.getErrorCode(), signingDetails.getErrorMessage(), signingDetails.getException());
            }
            parsedPackage.setSigningDetails((SigningDetails) signingDetails.getResult());
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public static void setInstantAppForUser(PackageManagerServiceInjector packageManagerServiceInjector, PackageSetting packageSetting, int i, boolean z, boolean z2) {
        int[] userIds;
        if (z || z2) {
            if (i != -1) {
                if (z && !packageSetting.getInstantApp(i)) {
                    packageSetting.setInstantApp(true, i);
                    return;
                } else if (z2 && packageSetting.getInstantApp(i)) {
                    packageSetting.setInstantApp(false, i);
                    return;
                } else {
                    return;
                }
            }
            for (int i2 : packageManagerServiceInjector.getUserManagerInternal().getUserIds()) {
                if (z && !packageSetting.getInstantApp(i2)) {
                    packageSetting.setInstantApp(true, i2);
                } else if (z2 && packageSetting.getInstantApp(i2)) {
                    packageSetting.setInstantApp(false, i2);
                }
            }
        }
    }

    public static File getAppLib32InstallDir() {
        return new File(Environment.getDataDirectory(), "app-lib");
    }
}
