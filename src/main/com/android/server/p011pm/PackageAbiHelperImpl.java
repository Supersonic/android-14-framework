package com.android.server.p011pm;

import android.content.pm.parsing.ApkLiteParseUtils;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Trace;
import android.os.incremental.IncrementalManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.PackageAbiHelper;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.PackageAbiHelperImpl */
/* loaded from: classes2.dex */
public final class PackageAbiHelperImpl implements PackageAbiHelper {
    public static String calculateBundledApkRoot(String str) {
        File canonicalFile;
        File file = new File(str);
        if (FileUtils.contains(Environment.getRootDirectory(), file)) {
            canonicalFile = Environment.getRootDirectory();
        } else if (FileUtils.contains(Environment.getOemDirectory(), file)) {
            canonicalFile = Environment.getOemDirectory();
        } else if (FileUtils.contains(Environment.getVendorDirectory(), file)) {
            canonicalFile = Environment.getVendorDirectory();
        } else if (FileUtils.contains(Environment.getOdmDirectory(), file)) {
            canonicalFile = Environment.getOdmDirectory();
        } else if (FileUtils.contains(Environment.getProductDirectory(), file)) {
            canonicalFile = Environment.getProductDirectory();
        } else if (FileUtils.contains(Environment.getSystemExtDirectory(), file)) {
            canonicalFile = Environment.getSystemExtDirectory();
        } else if (FileUtils.contains(Environment.getOdmDirectory(), file)) {
            canonicalFile = Environment.getOdmDirectory();
        } else if (FileUtils.contains(Environment.getApexDirectory(), file)) {
            String absolutePath = file.getAbsolutePath();
            String str2 = File.separator;
            String[] split = absolutePath.split(str2);
            if (split.length > 2) {
                canonicalFile = new File(split[1] + str2 + split[2]);
            } else {
                Slog.w("PackageManager", "Can't canonicalize code path " + file);
                canonicalFile = Environment.getApexDirectory();
            }
        } else {
            try {
                canonicalFile = file.getCanonicalFile();
                File parentFile = canonicalFile.getParentFile();
                while (true) {
                    File parentFile2 = parentFile.getParentFile();
                    if (parentFile2 == null) {
                        break;
                    }
                    canonicalFile = parentFile;
                    parentFile = parentFile2;
                }
                Slog.w("PackageManager", "Unrecognized code path " + file + " - using " + canonicalFile);
            } catch (IOException unused) {
                Slog.w("PackageManager", "Can't canonicalize code path " + file);
                return Environment.getRootDirectory().getPath();
            }
        }
        return canonicalFile.getPath();
    }

    public static String deriveCodePathName(String str) {
        if (str == null) {
            return null;
        }
        File file = new File(str);
        String name = file.getName();
        if (file.isDirectory()) {
            return name;
        }
        if (name.endsWith(".apk") || name.endsWith(".tmp")) {
            return name.substring(0, name.lastIndexOf(46));
        }
        Slog.w("PackageManager", "Odd, " + str + " doesn't look like an APK");
        return null;
    }

    public static void maybeThrowExceptionForMultiArchCopy(String str, int i) throws PackageManagerException {
        if (i < 0 && i != -114 && i != -113) {
            throw new PackageManagerException(i, str);
        }
    }

    @Override // com.android.server.p011pm.PackageAbiHelper
    public PackageAbiHelper.NativeLibraryPaths deriveNativeLibraryPaths(AndroidPackage androidPackage, boolean z, boolean z2, File file) {
        return deriveNativeLibraryPaths(new PackageAbiHelper.Abis(AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackage), AndroidPackageUtils.getRawSecondaryCpuAbi(androidPackage)), file, androidPackage.getPath(), androidPackage.getBaseApkPath(), z, z2);
    }

    public static PackageAbiHelper.NativeLibraryPaths deriveNativeLibraryPaths(PackageAbiHelper.Abis abis, File file, String str, String str2, boolean z, boolean z2) {
        String absolutePath;
        String absolutePath2;
        File file2 = new File(str);
        boolean z3 = true;
        boolean z4 = z && !z2;
        String str3 = null;
        if (ApkLiteParseUtils.isApkFile(file2)) {
            if (z4) {
                String calculateBundledApkRoot = calculateBundledApkRoot(str2);
                boolean is64BitInstructionSet = VMRuntime.is64BitInstructionSet(InstructionSets.getPrimaryInstructionSet(abis));
                String deriveCodePathName = deriveCodePathName(str);
                absolutePath = Environment.buildPath(new File(calculateBundledApkRoot), new String[]{is64BitInstructionSet ? "lib64" : "lib", deriveCodePathName}).getAbsolutePath();
                if (abis.secondary != null) {
                    str3 = Environment.buildPath(new File(calculateBundledApkRoot), new String[]{is64BitInstructionSet ? "lib" : "lib64", deriveCodePathName}).getAbsolutePath();
                }
            } else {
                absolutePath = new File(file, deriveCodePathName(str)).getAbsolutePath();
            }
            absolutePath2 = absolutePath;
            z3 = false;
        } else {
            absolutePath = new File(file2, "lib").getAbsolutePath();
            absolutePath2 = new File(absolutePath, InstructionSets.getPrimaryInstructionSet(abis)).getAbsolutePath();
            if (abis.secondary != null) {
                str3 = new File(absolutePath, VMRuntime.getInstructionSet(abis.secondary)).getAbsolutePath();
            }
        }
        return new PackageAbiHelper.NativeLibraryPaths(absolutePath, z3, absolutePath2, str3);
    }

    @Override // com.android.server.p011pm.PackageAbiHelper
    public PackageAbiHelper.Abis getBundledAppAbis(AndroidPackage androidPackage) {
        return getBundledAppAbi(androidPackage, calculateBundledApkRoot(androidPackage.getBaseApkPath()), deriveCodePathName(androidPackage.getPath()));
    }

    public final PackageAbiHelper.Abis getBundledAppAbi(AndroidPackage androidPackage, String str, String str2) {
        boolean exists;
        boolean exists2;
        String str3;
        String str4;
        File file = new File(androidPackage.getPath());
        if (ApkLiteParseUtils.isApkFile(file)) {
            exists = new File(str, new File("lib64", str2).getPath()).exists();
            exists2 = new File(str, new File("lib", str2).getPath()).exists();
        } else {
            File file2 = new File(file, "lib");
            String[] strArr = Build.SUPPORTED_64_BIT_ABIS;
            exists = (ArrayUtils.isEmpty(strArr) || TextUtils.isEmpty(strArr[0])) ? false : new File(file2, VMRuntime.getInstructionSet(strArr[0])).exists();
            String[] strArr2 = Build.SUPPORTED_32_BIT_ABIS;
            exists2 = (ArrayUtils.isEmpty(strArr2) || TextUtils.isEmpty(strArr2[0])) ? false : new File(file2, VMRuntime.getInstructionSet(strArr2[0])).exists();
        }
        String str5 = null;
        if (exists && !exists2) {
            str4 = Build.SUPPORTED_64_BIT_ABIS[0];
        } else if (exists2 && !exists) {
            str4 = Build.SUPPORTED_32_BIT_ABIS[0];
        } else {
            if (exists2 && exists) {
                if (!androidPackage.isMultiArch()) {
                    Slog.e("PackageManager", "Package " + androidPackage + " has multiple bundled libs, but is not multiarch.");
                }
                if (VMRuntime.is64BitInstructionSet(InstructionSets.getPreferredInstructionSet())) {
                    str5 = Build.SUPPORTED_64_BIT_ABIS[0];
                    str3 = Build.SUPPORTED_32_BIT_ABIS[0];
                } else {
                    str5 = Build.SUPPORTED_32_BIT_ABIS[0];
                    str3 = Build.SUPPORTED_64_BIT_ABIS[0];
                }
            } else {
                str3 = null;
            }
            return new PackageAbiHelper.Abis(str5, str3);
        }
        str5 = str4;
        str3 = null;
        return new PackageAbiHelper.Abis(str5, str3);
    }

    /* JADX WARN: Code restructure failed: missing block: B:75:0x0129, code lost:
        if (com.android.server.p011pm.parsing.pkg.AndroidPackageUtils.isLibrary(r17) != false) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x012b, code lost:
        r0 = r2[r0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0135, code lost:
        throw new com.android.server.p011pm.PackageManagerException(-110, "Shared library with native libs must be multiarch");
     */
    @Override // com.android.server.p011pm.PackageAbiHelper
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Pair<PackageAbiHelper.Abis, PackageAbiHelper.NativeLibraryPaths> derivePackageAbi(AndroidPackage androidPackage, boolean z, boolean z2, String str, File file) throws PackageManagerException {
        AutoCloseable autoCloseable;
        String str2;
        AutoCloseable autoCloseable2;
        String str3;
        String str4;
        File file2;
        int i;
        boolean z3;
        int findSupportedAbi;
        int i2;
        int findSupportedAbi2;
        PackageAbiHelper.NativeLibraryPaths deriveNativeLibraryPaths = deriveNativeLibraryPaths(new PackageAbiHelper.Abis(AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackage), AndroidPackageUtils.getRawSecondaryCpuAbi(androidPackage)), file, androidPackage.getPath(), androidPackage.getBaseApkPath(), z, z2);
        boolean shouldExtractLibs = shouldExtractLibs(androidPackage, z, z2);
        String str5 = deriveNativeLibraryPaths.nativeLibraryRootDir;
        boolean z4 = deriveNativeLibraryPaths.nativeLibraryRootRequiresIsa;
        boolean isIncrementalPath = IncrementalManager.isIncrementalPath(androidPackage.getPath());
        try {
            autoCloseable2 = AndroidPackageUtils.createNativeLibraryHandle(androidPackage);
            try {
                try {
                    file2 = new File(str5);
                    i = -114;
                } catch (Throwable th) {
                    th = th;
                    autoCloseable = autoCloseable2;
                    IoUtils.closeQuietly(autoCloseable);
                    throw th;
                }
            } catch (IOException e) {
                e = e;
                str2 = null;
            }
        } catch (IOException e2) {
            e = e2;
            str2 = null;
            autoCloseable2 = null;
        } catch (Throwable th2) {
            th = th2;
            autoCloseable = null;
        }
        if (androidPackage.isMultiArch()) {
            String[] strArr = Build.SUPPORTED_32_BIT_ABIS;
            if (strArr.length > 0) {
                if (shouldExtractLibs) {
                    Trace.traceBegin(262144L, "copyNativeBinaries");
                    i2 = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(autoCloseable2, file2, strArr, z4, isIncrementalPath);
                } else {
                    Trace.traceBegin(262144L, "findSupportedAbi");
                    i2 = NativeLibraryHelper.findSupportedAbi(autoCloseable2, strArr);
                }
                Trace.traceEnd(262144L);
            } else {
                i2 = -114;
            }
            if (i2 >= 0 && AndroidPackageUtils.isLibrary(androidPackage) && shouldExtractLibs) {
                throw new PackageManagerException(-110, "Shared library native lib extraction not supported");
            }
            maybeThrowExceptionForMultiArchCopy("Error unpackaging 32 bit native libs for multiarch app.", i2);
            String[] strArr2 = Build.SUPPORTED_64_BIT_ABIS;
            if (strArr2.length > 0) {
                if (shouldExtractLibs) {
                    Trace.traceBegin(262144L, "copyNativeBinaries");
                    findSupportedAbi2 = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(autoCloseable2, file2, strArr2, z4, isIncrementalPath);
                } else {
                    Trace.traceBegin(262144L, "findSupportedAbi");
                    findSupportedAbi2 = NativeLibraryHelper.findSupportedAbi(autoCloseable2, strArr2);
                }
                i = findSupportedAbi2;
                Trace.traceEnd(262144L);
            }
            maybeThrowExceptionForMultiArchCopy("Error unpackaging 64 bit native libs for multiarch app.", i);
            if (i >= 0) {
                if (shouldExtractLibs && AndroidPackageUtils.isLibrary(androidPackage)) {
                    throw new PackageManagerException(-110, "Shared library native lib extraction not supported");
                }
                str2 = strArr2[i];
            } else {
                str2 = null;
            }
            if (i2 >= 0) {
                try {
                    str3 = strArr[i2];
                } catch (IOException e3) {
                    e = e3;
                    Slog.e("PackageManager", "Unable to get canonical file " + e.toString());
                    IoUtils.closeQuietly(autoCloseable2);
                    str3 = str2;
                    str4 = null;
                    PackageAbiHelper.Abis abis = new PackageAbiHelper.Abis(str3, str4);
                    return new Pair<>(abis, deriveNativeLibraryPaths(abis, file, androidPackage.getPath(), androidPackage.getBaseApkPath(), z, z2));
                }
                if (i >= 0) {
                    if (androidPackage.is32BitAbiPreferred()) {
                        str4 = str2;
                    } else {
                        str4 = str3;
                        str3 = str2;
                    }
                    IoUtils.closeQuietly(autoCloseable2);
                    PackageAbiHelper.Abis abis2 = new PackageAbiHelper.Abis(str3, str4);
                    return new Pair<>(abis2, deriveNativeLibraryPaths(abis2, file, androidPackage.getPath(), androidPackage.getBaseApkPath(), z, z2));
                }
            } else {
                str3 = str2;
            }
        } else {
            String[] strArr3 = str != null ? new String[]{str} : Build.SUPPORTED_ABIS;
            if (Build.SUPPORTED_64_BIT_ABIS.length > 0 && str == null && NativeLibraryHelper.hasRenderscriptBitcode(autoCloseable2)) {
                strArr3 = Build.SUPPORTED_32_BIT_ABIS;
                if (strArr3.length <= 0) {
                    throw new PackageManagerException(-16, "Apps that contain RenderScript with target API level < 21 are not supported on 64-bit only platforms");
                }
                z3 = true;
            } else {
                z3 = false;
            }
            if (shouldExtractLibs) {
                Trace.traceBegin(262144L, "copyNativeBinaries");
                findSupportedAbi = NativeLibraryHelper.copyNativeBinariesForSupportedAbi(autoCloseable2, file2, strArr3, z4, isIncrementalPath);
            } else {
                Trace.traceBegin(262144L, "findSupportedAbi");
                findSupportedAbi = NativeLibraryHelper.findSupportedAbi(autoCloseable2, strArr3);
            }
            Trace.traceEnd(262144L);
            if (findSupportedAbi < 0 && findSupportedAbi != -114) {
                throw new PackageManagerException(-110, "Error unpackaging native libs for app, errorCode=" + findSupportedAbi);
            }
            if (findSupportedAbi != -114 || str == null) {
                str3 = z3 ? strArr3[0] : null;
            } else {
                str3 = str;
            }
        }
        str4 = null;
        IoUtils.closeQuietly(autoCloseable2);
        PackageAbiHelper.Abis abis22 = new PackageAbiHelper.Abis(str3, str4);
        return new Pair<>(abis22, deriveNativeLibraryPaths(abis22, file, androidPackage.getPath(), androidPackage.getBaseApkPath(), z, z2));
    }

    public final boolean shouldExtractLibs(AndroidPackage androidPackage, boolean z, boolean z2) {
        boolean z3 = !AndroidPackageUtils.isLibrary(androidPackage) && androidPackage.isExtractNativeLibrariesRequested();
        if (!z || z2) {
            return z3;
        }
        return false;
    }

    @Override // com.android.server.p011pm.PackageAbiHelper
    public String getAdjustedAbiForSharedUser(ArraySet<? extends PackageStateInternal> arraySet, AndroidPackage androidPackage) {
        String rawPrimaryCpuAbi;
        String instructionSet = (androidPackage == null || (rawPrimaryCpuAbi = AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackage)) == null) ? null : VMRuntime.getInstructionSet(rawPrimaryCpuAbi);
        Iterator<? extends PackageStateInternal> it = arraySet.iterator();
        PackageStateInternal packageStateInternal = null;
        while (it.hasNext()) {
            PackageStateInternal next = it.next();
            if (androidPackage == null || !androidPackage.getPackageName().equals(next.getPackageName())) {
                if (next.getPrimaryCpuAbiLegacy() != null) {
                    String instructionSet2 = VMRuntime.getInstructionSet(next.getPrimaryCpuAbiLegacy());
                    if (instructionSet != null && !instructionSet.equals(instructionSet2)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Instruction set mismatch, ");
                        sb.append(packageStateInternal == null ? "[caller]" : packageStateInternal);
                        sb.append(" requires ");
                        sb.append(instructionSet);
                        sb.append(" whereas ");
                        sb.append(next);
                        sb.append(" requires ");
                        sb.append(instructionSet2);
                        Slog.w("PackageManager", sb.toString());
                    }
                    if (instructionSet == null) {
                        packageStateInternal = next;
                        instructionSet = instructionSet2;
                    }
                }
            }
        }
        if (instructionSet == null) {
            return null;
        }
        if (packageStateInternal != null) {
            return packageStateInternal.getPrimaryCpuAbiLegacy();
        }
        return AndroidPackageUtils.getRawPrimaryCpuAbi(androidPackage);
    }
}
