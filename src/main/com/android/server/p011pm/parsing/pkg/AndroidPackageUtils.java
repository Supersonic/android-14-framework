package com.android.server.p011pm.parsing.pkg;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.incremental.IncrementalManager;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.PackageManagerException;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedInstrumentation;
import com.android.server.p011pm.pkg.component.ParsedProvider;
import com.android.server.p011pm.pkg.component.ParsedService;
import com.android.server.p011pm.pkg.parsing.ParsingPackageHidden;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* renamed from: com.android.server.pm.parsing.pkg.AndroidPackageUtils */
/* loaded from: classes2.dex */
public class AndroidPackageUtils {
    public static List<String> getAllCodePathsExcludingResourceOnly(AndroidPackage androidPackage) {
        PackageImpl packageImpl = (PackageImpl) androidPackage;
        ArrayList arrayList = new ArrayList();
        if (packageImpl.isDeclaredHavingCode()) {
            arrayList.add(packageImpl.getBaseApkPath());
        }
        String[] splitCodePaths = packageImpl.getSplitCodePaths();
        if (!ArrayUtils.isEmpty(splitCodePaths)) {
            for (int i = 0; i < splitCodePaths.length; i++) {
                if ((packageImpl.getSplitFlags()[i] & 4) != 0) {
                    arrayList.add(splitCodePaths[i]);
                }
            }
        }
        return arrayList;
    }

    public static List<String> getAllCodePaths(AndroidPackage androidPackage) {
        PackageImpl packageImpl = (PackageImpl) androidPackage;
        ArrayList arrayList = new ArrayList();
        arrayList.add(packageImpl.getBaseApkPath());
        String[] splitCodePaths = packageImpl.getSplitCodePaths();
        if (!ArrayUtils.isEmpty(splitCodePaths)) {
            Collections.addAll(arrayList, splitCodePaths);
        }
        return arrayList;
    }

    public static SharedLibraryInfo createSharedLibraryForSdk(AndroidPackage androidPackage) {
        return new SharedLibraryInfo(null, androidPackage.getPackageName(), getAllCodePaths(androidPackage), androidPackage.getSdkLibraryName(), androidPackage.getSdkLibVersionMajor(), 3, new VersionedPackage(androidPackage.getManifestPackageName(), androidPackage.getLongVersionCode()), null, null, false);
    }

    public static SharedLibraryInfo createSharedLibraryForStatic(AndroidPackage androidPackage) {
        return new SharedLibraryInfo(null, androidPackage.getPackageName(), getAllCodePaths(androidPackage), androidPackage.getStaticSharedLibraryName(), androidPackage.getStaticSharedLibraryVersion(), 2, new VersionedPackage(androidPackage.getManifestPackageName(), androidPackage.getLongVersionCode()), null, null, false);
    }

    public static SharedLibraryInfo createSharedLibraryForDynamic(AndroidPackage androidPackage, String str) {
        return new SharedLibraryInfo(null, androidPackage.getPackageName(), getAllCodePaths(androidPackage), str, -1L, 1, new VersionedPackage(androidPackage.getPackageName(), androidPackage.getLongVersionCode()), null, null, false);
    }

    public static Map<String, String> getPackageDexMetadata(AndroidPackage androidPackage) {
        return DexMetadataHelper.buildPackageApkToDexMetadataMap(getAllCodePaths(androidPackage));
    }

    public static void validatePackageDexMetadata(AndroidPackage androidPackage) throws PackageManagerException {
        Collection<String> values = getPackageDexMetadata(androidPackage).values();
        String packageName = androidPackage.getPackageName();
        long longVersionCode = androidPackage.getLongVersionCode();
        ParseTypeImpl forDefaultParsing = ParseTypeImpl.forDefaultParsing();
        for (String str : values) {
            ParseResult validateDexMetadataFile = DexMetadataHelper.validateDexMetadataFile(forDefaultParsing.reset(), str, packageName, longVersionCode);
            if (validateDexMetadataFile.isError()) {
                throw new PackageManagerException(validateDexMetadataFile.getErrorCode(), validateDexMetadataFile.getErrorMessage(), validateDexMetadataFile.getException());
            }
        }
    }

    public static NativeLibraryHelper.Handle createNativeLibraryHandle(AndroidPackage androidPackage) throws IOException {
        return NativeLibraryHelper.Handle.create(getAllCodePaths(androidPackage), androidPackage.isMultiArch(), androidPackage.isExtractNativeLibrariesRequested(), androidPackage.isDebuggable());
    }

    public static boolean canHaveOatDir(PackageState packageState, AndroidPackage androidPackage) {
        return (!packageState.isSystem() || packageState.isUpdatedSystemApp()) && !IncrementalManager.isIncrementalPath(androidPackage.getPath());
    }

    public static boolean hasComponentClassName(AndroidPackage androidPackage, String str) {
        List<ParsedActivity> activities = androidPackage.getActivities();
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            if (Objects.equals(str, activities.get(i).getName())) {
                return true;
            }
        }
        List<ParsedActivity> receivers = androidPackage.getReceivers();
        int size2 = receivers.size();
        for (int i2 = 0; i2 < size2; i2++) {
            if (Objects.equals(str, receivers.get(i2).getName())) {
                return true;
            }
        }
        List<ParsedProvider> providers = androidPackage.getProviders();
        int size3 = providers.size();
        for (int i3 = 0; i3 < size3; i3++) {
            if (Objects.equals(str, providers.get(i3).getName())) {
                return true;
            }
        }
        List<ParsedService> services = androidPackage.getServices();
        int size4 = services.size();
        for (int i4 = 0; i4 < size4; i4++) {
            if (Objects.equals(str, services.get(i4).getName())) {
                return true;
            }
        }
        List<ParsedInstrumentation> instrumentations = androidPackage.getInstrumentations();
        int size5 = instrumentations.size();
        for (int i5 = 0; i5 < size5; i5++) {
            if (Objects.equals(str, instrumentations.get(i5).getName())) {
                return true;
            }
        }
        return androidPackage.getBackupAgentName() != null && Objects.equals(str, androidPackage.getBackupAgentName());
    }

    public static boolean isEncryptionAware(AndroidPackage androidPackage) {
        return androidPackage.isDirectBootAware() || androidPackage.isPartiallyDirectBootAware();
    }

    public static boolean isLibrary(AndroidPackage androidPackage) {
        return (androidPackage.getSdkLibraryName() == null && androidPackage.getStaticSharedLibraryName() == null && androidPackage.getLibraryNames().isEmpty()) ? false : true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0029, code lost:
        if (com.android.server.SystemConfig.getInstance().getHiddenApiWhitelistedApps().contains(r3.getPackageName()) != false) goto L4;
     */
    /* JADX WARN: Removed duplicated region for block: B:15:0x002d A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:16:0x002e A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getHiddenApiEnforcementPolicy(AndroidPackage androidPackage, PackageStateInternal packageStateInternal) {
        boolean z;
        if (androidPackage != null) {
            z = true;
            if (!androidPackage.isSignedWithPlatformKey()) {
                if (packageStateInternal.isSystem()) {
                    if (!androidPackage.isNonSdkApiRequested()) {
                    }
                }
            }
            return !z ? 0 : 2;
        }
        z = false;
        if (!z) {
        }
    }

    public static boolean isMatchForSystemOnly(PackageState packageState, long j) {
        if ((j & 1048576) != 0) {
            return packageState.isSystem();
        }
        return true;
    }

    public static String getRawPrimaryCpuAbi(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).getPrimaryCpuAbi();
    }

    public static String getRawSecondaryCpuAbi(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).getSecondaryCpuAbi();
    }

    @Deprecated
    public static ApplicationInfo generateAppInfoWithoutState(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).toAppInfoWithoutState();
    }

    public static String getRealPackageOrNull(AndroidPackage androidPackage, boolean z) {
        if (androidPackage.getOriginalPackages().isEmpty() || !z) {
            return null;
        }
        return androidPackage.getManifestPackageName();
    }

    public static void fillVersionCodes(AndroidPackage androidPackage, PackageInfo packageInfo) {
        ParsingPackageHidden parsingPackageHidden = (ParsingPackageHidden) androidPackage;
        packageInfo.versionCode = parsingPackageHidden.getVersionCode();
        packageInfo.versionCodeMajor = parsingPackageHidden.getVersionCodeMajor();
    }

    @Deprecated
    public static boolean isSystem(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isSystem();
    }

    @Deprecated
    public static boolean isSystemExt(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isSystemExt();
    }

    @Deprecated
    public static boolean isPrivileged(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isPrivileged();
    }

    @Deprecated
    public static boolean isOem(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isOem();
    }

    @Deprecated
    public static boolean isVendor(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isVendor();
    }

    @Deprecated
    public static boolean isProduct(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isProduct();
    }

    @Deprecated
    public static boolean isOdm(AndroidPackage androidPackage) {
        return ((AndroidPackageHidden) androidPackage).isOdm();
    }
}
