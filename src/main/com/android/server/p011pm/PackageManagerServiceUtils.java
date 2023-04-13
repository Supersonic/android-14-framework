package com.android.server.p011pm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfoLite;
import android.content.pm.PackageInstaller;
import android.content.pm.PackagePartitions;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.pm.SigningDetails;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.PackageLite;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.FileUtils;
import android.os.SystemProperties;
import android.os.incremental.IncrementalManager;
import android.os.incremental.IncrementalStorage;
import android.os.incremental.V4Signature;
import android.os.storage.DiskInfo;
import android.os.storage.VolumeInfo;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Base64;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.content.InstallLocationUtils;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.HexDump;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.IntentResolver;
import com.android.server.LocalManagerRegistry;
import com.android.server.Watchdog;
import com.android.server.compat.PlatformCompat;
import com.android.server.p006am.ActivityManagerUtils;
import com.android.server.p011pm.dex.PackageDexUsage;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.component.ParsedComponent;
import com.android.server.p011pm.resolution.ComponentResolverApi;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.PackageManagerServiceUtils */
/* loaded from: classes2.dex */
public class PackageManagerServiceUtils {
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final Predicate<PackageStateInternal> REMOVE_IF_APEX_PKG = new Predicate() { // from class: com.android.server.pm.PackageManagerServiceUtils$$ExternalSyntheticLambda0
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            boolean lambda$static$0;
            lambda$static$0 = PackageManagerServiceUtils.lambda$static$0((PackageStateInternal) obj);
            return lambda$static$0;
        }
    };
    public static final Predicate<PackageStateInternal> REMOVE_IF_NULL_PKG = new Predicate() { // from class: com.android.server.pm.PackageManagerServiceUtils$$ExternalSyntheticLambda1
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            boolean lambda$static$1;
            lambda$static$1 = PackageManagerServiceUtils.lambda$static$1((PackageStateInternal) obj);
            return lambda$static$1;
        }
    };
    public static final ThreadLocal<Boolean> DISABLE_ENFORCE_INTENTS_TO_MATCH_INTENT_FILTERS = ThreadLocal.withInitial(new Supplier() { // from class: com.android.server.pm.PackageManagerServiceUtils$$ExternalSyntheticLambda2
        @Override // java.util.function.Supplier
        public final Object get() {
            Boolean bool;
            bool = Boolean.FALSE;
            return bool;
        }
    });

    public static boolean isApkVerificationForced(PackageSetting packageSetting) {
        return false;
    }

    public static boolean isRootOrShell(int i) {
        return i == 0 || i == 2000;
    }

    public static boolean isSystemOrRoot(int i) {
        return i == 1000 || i == 0;
    }

    public static boolean isSystemOrRootOrShell(int i) {
        return i == 1000 || i == 0 || i == 2000;
    }

    public static /* synthetic */ boolean lambda$static$0(PackageStateInternal packageStateInternal) {
        return packageStateInternal.getPkg().isApex();
    }

    public static /* synthetic */ boolean lambda$static$1(PackageStateInternal packageStateInternal) {
        return packageStateInternal.getPkg() == null;
    }

    public static PackageManagerLocal getPackageManagerLocal() {
        try {
            return (PackageManagerLocal) LocalManagerRegistry.getManagerOrThrow(PackageManagerLocal.class);
        } catch (LocalManagerRegistry.ManagerNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isUnusedSinceTimeInMillis(long j, long j2, long j3, PackageDexUsage.PackageUseInfo packageUseInfo, long j4, long j5) {
        boolean z = false;
        if (j2 - j < j3) {
            return false;
        }
        if (j2 - j5 < j3) {
            return false;
        }
        if (j2 - j4 < j3 && packageUseInfo.isAnyCodePathUsedByOtherApps()) {
            z = true;
        }
        return !z;
    }

    public static String realpath(File file) throws IOException {
        try {
            return Os.realpath(file.getAbsolutePath());
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static boolean checkISA(String str) {
        for (String str2 : Build.SUPPORTED_ABIS) {
            if (VMRuntime.getInstructionSet(str2).equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static long getLastModifiedTime(AndroidPackage androidPackage) {
        File file = new File(androidPackage.getPath());
        if (!file.isDirectory()) {
            return file.lastModified();
        }
        long lastModified = new File(androidPackage.getBaseApkPath()).lastModified();
        for (int length = androidPackage.getSplitCodePaths().length - 1; length >= 0; length--) {
            lastModified = Math.max(lastModified, new File(androidPackage.getSplitCodePaths()[length]).lastModified());
        }
        return lastModified;
    }

    public static File getSettingsProblemFile() {
        return new File(new File(Environment.getDataDirectory(), "system"), "uiderrors.txt");
    }

    public static void dumpCriticalInfo(ProtoOutputStream protoOutputStream) {
        File settingsProblemFile = getSettingsProblemFile();
        long length = settingsProblemFile.length() - 3000000;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(settingsProblemFile));
            if (length > 0) {
                bufferedReader.skip(length);
            }
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    if (!readLine.contains("ignored: updated version")) {
                        protoOutputStream.write(2237677961223L, readLine);
                    }
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (IOException unused) {
        }
    }

    public static void dumpCriticalInfo(PrintWriter printWriter, String str) {
        File settingsProblemFile = getSettingsProblemFile();
        long length = settingsProblemFile.length() - 3000000;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(settingsProblemFile));
            if (length > 0) {
                bufferedReader.skip(length);
            }
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    if (!readLine.contains("ignored: updated version")) {
                        if (str != null) {
                            printWriter.print(str);
                        }
                        printWriter.println(readLine);
                    }
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (IOException unused) {
        }
    }

    public static void logCriticalInfo(int i, String str) {
        Slog.println(i, "PackageManager", str);
        EventLogTags.writePmCriticalInfo(str);
        try {
            File settingsProblemFile = getSettingsProblemFile();
            FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(settingsProblemFile, true));
            String format = new SimpleDateFormat().format(new Date(System.currentTimeMillis()));
            fastPrintWriter.println(format + ": " + str);
            fastPrintWriter.close();
            FileUtils.setPermissions(settingsProblemFile.toString(), 508, -1, -1);
        } catch (IOException unused) {
        }
    }

    public static void enforceShellRestriction(UserManagerInternal userManagerInternal, String str, int i, int i2) {
        if (i == 2000) {
            if (i2 >= 0 && userManagerInternal.hasUserRestriction(str, i2)) {
                throw new SecurityException("Shell does not have permission to access user " + i2);
            } else if (i2 < 0) {
                Slog.e("PackageManager", "Unable to check shell permission for user " + i2 + "\n\t" + Debug.getCallers(3));
            }
        }
    }

    public static void enforceSystemOrPhoneCaller(String str, int i) {
        if (i == 1001 || i == 1000) {
            return;
        }
        throw new SecurityException("Cannot call " + str + " from UID " + i);
    }

    public static String deriveAbiOverride(String str) {
        if (PackageManagerShellCommandDataLoader.STDIN_PATH.equals(str)) {
            return null;
        }
        return str;
    }

    public static int compareSignatures(Signature[] signatureArr, Signature[] signatureArr2) {
        if (signatureArr == null) {
            return signatureArr2 == null ? 1 : -1;
        } else if (signatureArr2 == null) {
            return -2;
        } else {
            if (signatureArr.length != signatureArr2.length) {
                return -3;
            }
            if (signatureArr.length == 1) {
                return signatureArr[0].equals(signatureArr2[0]) ? 0 : -3;
            }
            ArraySet arraySet = new ArraySet();
            for (Signature signature : signatureArr) {
                arraySet.add(signature);
            }
            ArraySet arraySet2 = new ArraySet();
            for (Signature signature2 : signatureArr2) {
                arraySet2.add(signature2);
            }
            return arraySet.equals(arraySet2) ? 0 : -3;
        }
    }

    public static boolean comparePackageSignatures(PackageSetting packageSetting, Signature[] signatureArr) {
        SigningDetails signingDetails = packageSetting.getSigningDetails();
        return signingDetails == SigningDetails.UNKNOWN || compareSignatures(signingDetails.getSignatures(), signatureArr) == 0;
    }

    public static boolean matchSignaturesCompat(String str, PackageSignatures packageSignatures, SigningDetails signingDetails) {
        Signature[] signatures;
        ArraySet arraySet = new ArraySet();
        for (Signature signature : packageSignatures.mSigningDetails.getSignatures()) {
            arraySet.add(signature);
        }
        ArraySet arraySet2 = new ArraySet();
        for (Signature signature2 : signingDetails.getSignatures()) {
            try {
                for (Signature signature3 : signature2.getChainSignatures()) {
                    arraySet2.add(signature3);
                }
            } catch (CertificateEncodingException unused) {
                arraySet2.add(signature2);
            }
        }
        if (arraySet2.equals(arraySet)) {
            packageSignatures.mSigningDetails = signingDetails;
            return true;
        }
        if (signingDetails.hasPastSigningCertificates()) {
            logCriticalInfo(4, "Existing package " + str + " has flattened signing certificate chain. Unable to install newer version with rotated signing certificate.");
        }
        return false;
    }

    public static boolean matchSignaturesRecover(String str, SigningDetails signingDetails, SigningDetails signingDetails2, @SigningDetails.CertCapabilities int i) {
        String message;
        try {
        } catch (CertificateException e) {
            message = e.getMessage();
        }
        if (signingDetails2.checkCapabilityRecover(signingDetails, i)) {
            logCriticalInfo(4, "Recovered effectively matching certificates for " + str);
            return true;
        }
        message = null;
        logCriticalInfo(4, "Failed to recover certificates for " + str + ": " + message);
        return false;
    }

    public static boolean matchSignatureInSystem(String str, SigningDetails signingDetails, PackageSetting packageSetting) {
        if (signingDetails.checkCapability(packageSetting.getSigningDetails(), 1) || packageSetting.getSigningDetails().checkCapability(signingDetails, 8)) {
            return true;
        }
        logCriticalInfo(6, "Updated system app mismatches cert on /system: " + str);
        return false;
    }

    public static boolean isApkVerityEnabled() {
        return Build.VERSION.DEVICE_INITIAL_SDK_INT >= 30 || SystemProperties.getInt("ro.apk_verity.mode", 0) == 2;
    }

    public static boolean verifySignatures(PackageSetting packageSetting, SharedUserSetting sharedUserSetting, PackageSetting packageSetting2, SigningDetails signingDetails, boolean z, boolean z2, boolean z3) throws PackageManagerException {
        boolean z4;
        String packageName = packageSetting.getPackageName();
        boolean z5 = true;
        if (packageSetting.getSigningDetails().getSignatures() != null) {
            boolean z6 = signingDetails.checkCapability(packageSetting.getSigningDetails(), 1) || packageSetting.getSigningDetails().checkCapability(signingDetails, 8);
            if (z6 || !z) {
                z4 = false;
            } else {
                z6 = matchSignaturesCompat(packageName, packageSetting.getSignatures(), signingDetails);
                z4 = z6;
            }
            if (!z6 && z2) {
                z6 = matchSignaturesRecover(packageName, packageSetting.getSigningDetails(), signingDetails, 1) || matchSignaturesRecover(packageName, signingDetails, packageSetting.getSigningDetails(), 8);
            }
            if (!z6 && isApkVerificationForced(packageSetting2)) {
                z6 = matchSignatureInSystem(packageName, packageSetting.getSigningDetails(), packageSetting2);
            }
            if (!z6 && z3) {
                z6 = packageSetting.getSigningDetails().hasAncestorOrSelf(signingDetails);
            }
            if (!z6) {
                throw new PackageManagerException(-7, "Existing package " + packageName + " signatures do not match newer version; ignoring!");
            }
        } else {
            z4 = false;
        }
        if (sharedUserSetting != null && sharedUserSetting.getSigningDetails() != SigningDetails.UNKNOWN) {
            boolean canJoinSharedUserId = canJoinSharedUserId(packageName, signingDetails, sharedUserSetting, packageSetting.getSigningDetails().getSignatures() != null ? 1 : 0);
            if (!canJoinSharedUserId && z) {
                canJoinSharedUserId = matchSignaturesCompat(packageName, sharedUserSetting.signatures, signingDetails);
            }
            if (!canJoinSharedUserId && z2) {
                if (!matchSignaturesRecover(packageName, sharedUserSetting.signatures.mSigningDetails, signingDetails, 2) && !matchSignaturesRecover(packageName, signingDetails, sharedUserSetting.signatures.mSigningDetails, 2)) {
                    z5 = false;
                }
                z4 |= z5;
                canJoinSharedUserId = z5;
            }
            if (!canJoinSharedUserId) {
                throw new PackageManagerException(-8, "Package " + packageName + " has no signatures that match those in shared user " + sharedUserSetting.name + "; ignoring!");
            } else if (!signingDetails.hasCommonAncestor(sharedUserSetting.signatures.mSigningDetails)) {
                throw new PackageManagerException(-8, "Package " + packageName + " has a signing lineage that diverges from the lineage of the sharedUserId");
            }
        }
        return z4;
    }

    public static boolean canJoinSharedUserId(String str, SigningDetails signingDetails, SharedUserSetting sharedUserSetting, int i) {
        SigningDetails signingDetails2 = sharedUserSetting.getSigningDetails();
        boolean z = signingDetails.checkCapability(signingDetails2, 2) || signingDetails2.checkCapability(signingDetails, 2);
        if (!z || i == 0) {
            if (!z && signingDetails2.hasAncestor(signingDetails)) {
                return i == 2;
            } else if (!z && signingDetails.hasAncestor(signingDetails2)) {
                return i != 0;
            } else if (z) {
                ArraySet<? extends PackageStateInternal> packageStates = sharedUserSetting.getPackageStates();
                if (signingDetails.hasPastSigningCertificates()) {
                    Iterator<? extends PackageStateInternal> it = packageStates.iterator();
                    while (it.hasNext()) {
                        PackageStateInternal next = it.next();
                        SigningDetails signingDetails3 = next.getSigningDetails();
                        if (signingDetails.hasAncestor(signingDetails3) && !signingDetails.checkCapability(signingDetails3, 2)) {
                            Slog.d("PackageManager", "Package " + str + " revoked the sharedUserId capability from the signing key used to sign " + next.getPackageName());
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static int extractNativeBinaries(File file, String str) {
        AutoCloseable create;
        File file2 = new File(file, "lib");
        AutoCloseable autoCloseable = null;
        try {
            try {
                create = NativeLibraryHelper.Handle.create(file);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException unused) {
        }
        try {
            int copyNativeBinariesWithOverride = NativeLibraryHelper.copyNativeBinariesWithOverride(create, file2, (String) null, false);
            IoUtils.closeQuietly(create);
            return copyNativeBinariesWithOverride;
        } catch (IOException unused2) {
            autoCloseable = create;
            logCriticalInfo(6, "Failed to extract native libraries; pkg: " + str);
            IoUtils.closeQuietly(autoCloseable);
            return -110;
        } catch (Throwable th2) {
            th = th2;
            autoCloseable = create;
            IoUtils.closeQuietly(autoCloseable);
            throw th;
        }
    }

    public static void removeNativeBinariesLI(PackageSetting packageSetting) {
        if (packageSetting != null) {
            NativeLibraryHelper.removeNativeBinariesLI(packageSetting.getLegacyNativeLibraryPath());
        }
    }

    public static void waitForNativeBinariesExtractionForIncremental(ArraySet<IncrementalStorage> arraySet) {
        if (arraySet.isEmpty()) {
            return;
        }
        try {
            Watchdog.getInstance().pauseWatchingCurrentThread("native_lib_extract");
            for (int i = 0; i < arraySet.size(); i++) {
                ((IncrementalStorage) arraySet.valueAtUnchecked(i)).waitForNativeBinariesExtraction();
            }
        } finally {
            Watchdog.getInstance().resumeWatchingCurrentThread("native_lib_extract");
        }
    }

    public static int decompressFiles(String str, File file, String str2) {
        String name;
        File[] compressedFiles = getCompressedFiles(str);
        int i = 1;
        try {
            makeDirRecursive(file, 493);
            int i2 = 1;
            for (File file2 : compressedFiles) {
                try {
                    String substring = file2.getName().substring(0, name.length() - 3);
                    i2 = decompressFile(file2, new File(file, substring));
                    if (i2 != 1) {
                        logCriticalInfo(6, "Failed to decompress; pkg: " + str2 + ", file: " + substring);
                        return i2;
                    }
                } catch (ErrnoException e) {
                    e = e;
                    i = i2;
                    logCriticalInfo(6, "Failed to decompress; pkg: " + str2 + ", err: " + e.errno);
                    return i;
                }
            }
            return i2;
        } catch (ErrnoException e2) {
            e = e2;
        }
    }

    public static int decompressFile(File file, File file2) throws ErrnoException {
        if (PackageManagerService.DEBUG_COMPRESSION) {
            Slog.i("PackageManager", "Decompress file; src: " + file.getAbsolutePath() + ", dst: " + file2.getAbsolutePath());
        }
        AtomicFile atomicFile = new AtomicFile(file2);
        FileOutputStream fileOutputStream = null;
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file));
            fileOutputStream = atomicFile.startWrite();
            FileUtils.copy(gZIPInputStream, fileOutputStream);
            fileOutputStream.flush();
            Os.fchmod(fileOutputStream.getFD(), FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
            atomicFile.finishWrite(fileOutputStream);
            gZIPInputStream.close();
            return 1;
        } catch (IOException unused) {
            logCriticalInfo(6, "Failed to decompress file; src: " + file.getAbsolutePath() + ", dst: " + file2.getAbsolutePath());
            atomicFile.failWrite(fileOutputStream);
            return -110;
        }
    }

    public static File[] getCompressedFiles(String str) {
        File file = new File(str);
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf("-Stub");
        if (lastIndexOf < 0 || name.length() != lastIndexOf + 5) {
            return null;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            Slog.e("PackageManager", "Unable to determine stub parent dir for codePath: " + str);
            return null;
        }
        File[] listFiles = new File(parentFile, name.substring(0, lastIndexOf)).listFiles(new FilenameFilter() { // from class: com.android.server.pm.PackageManagerServiceUtils.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file2, String str2) {
                return str2.toLowerCase().endsWith(".gz");
            }
        });
        if (PackageManagerService.DEBUG_COMPRESSION && listFiles != null && listFiles.length > 0) {
            Slog.i("PackageManager", "getCompressedFiles[" + str + "]: " + Arrays.toString(listFiles));
        }
        return listFiles;
    }

    public static boolean compressedFileExists(String str) {
        File[] compressedFiles = getCompressedFiles(str);
        return compressedFiles != null && compressedFiles.length > 0;
    }

    public static PackageInfoLite getMinimalPackageInfo(Context context, PackageLite packageLite, String str, int i, String str2) {
        PackageInfoLite packageInfoLite = new PackageInfoLite();
        if (str == null || packageLite == null) {
            Slog.i("PackageManager", "Invalid package file " + str);
            packageInfoLite.recommendedInstallLocation = -2;
            return packageInfoLite;
        }
        File file = new File(str);
        try {
            long calculateInstalledSize = InstallLocationUtils.calculateInstalledSize(packageLite, str2);
            PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(-1);
            sessionParams.appPackageName = packageLite.getPackageName();
            sessionParams.installLocation = packageLite.getInstallLocation();
            sessionParams.sizeBytes = calculateInstalledSize;
            sessionParams.installFlags = i;
            try {
                int resolveInstallLocation = InstallLocationUtils.resolveInstallLocation(context, sessionParams);
                packageInfoLite.packageName = packageLite.getPackageName();
                packageInfoLite.splitNames = packageLite.getSplitNames();
                packageInfoLite.versionCode = packageLite.getVersionCode();
                packageInfoLite.versionCodeMajor = packageLite.getVersionCodeMajor();
                packageInfoLite.baseRevisionCode = packageLite.getBaseRevisionCode();
                packageInfoLite.splitRevisionCodes = packageLite.getSplitRevisionCodes();
                packageInfoLite.installLocation = packageLite.getInstallLocation();
                packageInfoLite.verifiers = packageLite.getVerifiers();
                packageInfoLite.recommendedInstallLocation = resolveInstallLocation;
                packageInfoLite.multiArch = packageLite.isMultiArch();
                packageInfoLite.debuggable = packageLite.isDebuggable();
                packageInfoLite.isSdkLibrary = packageLite.isIsSdkLibrary();
                return packageInfoLite;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } catch (IOException unused) {
            if (!file.exists()) {
                packageInfoLite.recommendedInstallLocation = -6;
            } else {
                packageInfoLite.recommendedInstallLocation = -2;
            }
            return packageInfoLite;
        }
    }

    public static long calculateInstalledSize(String str, String str2) {
        try {
            ParseResult parsePackageLite = ApkLiteParseUtils.parsePackageLite(ParseTypeImpl.forDefaultParsing().reset(), new File(str), 0);
            if (parsePackageLite.isError()) {
                throw new PackageManagerException(parsePackageLite.getErrorCode(), parsePackageLite.getErrorMessage(), parsePackageLite.getException());
            }
            return InstallLocationUtils.calculateInstalledSize((PackageLite) parsePackageLite.getResult(), str2);
        } catch (PackageManagerException | IOException e) {
            Slog.w("PackageManager", "Failed to calculate installed size: " + e);
            return -1L;
        }
    }

    public static boolean isDowngradePermitted(int i, boolean z) {
        if ((i & 128) != 0) {
            return (Build.IS_DEBUGGABLE || z) || (i & 1048576) != 0;
        }
        return false;
    }

    public static int copyPackage(String str, File file) {
        if (str == null) {
            return -3;
        }
        try {
            ParseResult parsePackageLite = ApkLiteParseUtils.parsePackageLite(ParseTypeImpl.forDefaultParsing().reset(), new File(str), 0);
            if (parsePackageLite.isError()) {
                Slog.w("PackageManager", "Failed to parse package at " + str);
                return parsePackageLite.getErrorCode();
            }
            PackageLite packageLite = (PackageLite) parsePackageLite.getResult();
            copyFile(packageLite.getBaseApkPath(), file, "base.apk");
            if (ArrayUtils.isEmpty(packageLite.getSplitNames())) {
                return 1;
            }
            for (int i = 0; i < packageLite.getSplitNames().length; i++) {
                String str2 = packageLite.getSplitApkPaths()[i];
                copyFile(str2, file, "split_" + packageLite.getSplitNames()[i] + ".apk");
            }
            return 1;
        } catch (ErrnoException | IOException e) {
            Slog.w("PackageManager", "Failed to copy package at " + str + ": " + e);
            return -4;
        }
    }

    public static void copyFile(String str, File file, String str2) throws ErrnoException, IOException {
        if (!FileUtils.isValidExtFilename(str2)) {
            throw new IllegalArgumentException("Invalid filename: " + str2);
        }
        Slog.d("PackageManager", "Copying " + str + " to " + str2);
        File file2 = new File(file, str2);
        FileDescriptor open = Os.open(file2.getAbsolutePath(), OsConstants.O_RDWR | OsConstants.O_CREAT, FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
        Os.chmod(file2.getAbsolutePath(), FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(str);
            try {
                FileUtils.copy(fileInputStream2.getFD(), open);
                IoUtils.closeQuietly(fileInputStream2);
            } catch (Throwable th) {
                th = th;
                fileInputStream = fileInputStream2;
                IoUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void makeDirRecursive(File file, int i) throws ErrnoException {
        Path path = file.toPath();
        int nameCount = path.getNameCount();
        for (int i2 = 1; i2 <= nameCount; i2++) {
            File file2 = path.subpath(0, i2).toFile();
            if (!file2.exists()) {
                Os.mkdir(file2.getAbsolutePath(), i);
                Os.chmod(file2.getAbsolutePath(), i);
            }
        }
    }

    public static String buildVerificationRootHashString(String str, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(str.lastIndexOf(File.separator) + 1));
        sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
        byte[] rootHash = getRootHash(str);
        if (rootHash == null) {
            sb.append("0");
        } else {
            sb.append(HexDump.toHexString(rootHash));
        }
        if (strArr == null || strArr.length == 0) {
            return sb.toString();
        }
        for (int length = strArr.length - 1; length >= 0; length--) {
            String str2 = strArr[length];
            String substring = str2.substring(str2.lastIndexOf(File.separator) + 1);
            byte[] rootHash2 = getRootHash(str2);
            sb.append(";");
            sb.append(substring);
            sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
            if (rootHash2 == null) {
                sb.append("0");
            } else {
                sb.append(HexDump.toHexString(rootHash2));
            }
        }
        return sb.toString();
    }

    public static byte[] getRootHash(String str) {
        try {
            byte[] unsafeGetFileSignature = IncrementalManager.unsafeGetFileSignature(str);
            if (unsafeGetFileSignature == null) {
                throw new IOException("File signature not present");
            }
            byte[] bArr = V4Signature.readFrom(unsafeGetFileSignature).hashingInfo;
            if (bArr == null) {
                throw new IOException("Hashing info not present");
            }
            V4Signature.HashingInfo fromByteArray = V4Signature.HashingInfo.fromByteArray(bArr);
            if (ArrayUtils.isEmpty(fromByteArray.rawRootHash)) {
                throw new IOException("Root has not present");
            }
            return ApkChecksums.verityHashForFile(new File(str), fromByteArray.rawRootHash);
        } catch (IOException e) {
            Slog.i("PackageManager", "Could not obtain verity root hash", e);
            return null;
        }
    }

    public static boolean isSystemApp(PackageStateInternal packageStateInternal) {
        return (packageStateInternal.getFlags() & 1) != 0;
    }

    public static boolean isUpdatedSystemApp(PackageStateInternal packageStateInternal) {
        return (packageStateInternal.getFlags() & 128) != 0;
    }

    public static void applyEnforceIntentFilterMatching(PlatformCompat platformCompat, ComponentResolverApi componentResolverApi, List<ResolveInfo> list, boolean z, Intent intent, String str, int i) {
        ParsedComponent service;
        if (DISABLE_ENFORCE_INTENTS_TO_MATCH_INTENT_FILTERS.get().booleanValue()) {
            return;
        }
        for (int size = list.size() - 1; size >= 0; size--) {
            ComponentInfo componentInfo = list.get(size).getComponentInfo();
            boolean z2 = false;
            if (ActivityManager.checkComponentPermission(null, i, componentInfo.applicationInfo.uid, false) != 0) {
                if (componentInfo instanceof ActivityInfo) {
                    if (z) {
                        service = componentResolverApi.getReceiver(componentInfo.getComponentName());
                    } else {
                        service = componentResolverApi.getActivity(componentInfo.getComponentName());
                    }
                } else if (componentInfo instanceof ServiceInfo) {
                    service = componentResolverApi.getService(componentInfo.getComponentName());
                } else {
                    throw new IllegalArgumentException("Unsupported component type");
                }
                if (service != null && !service.getIntents().isEmpty()) {
                    boolean isChangeEnabledInternal = platformCompat.isChangeEnabledInternal(161252188L, componentInfo.applicationInfo);
                    int size2 = service.getIntents().size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            break;
                        } else if (IntentResolver.intentMatchesFilter(service.getIntents().get(i2).getIntentFilter(), intent, str)) {
                            z2 = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (!z2) {
                        ActivityManagerUtils.logUnsafeIntentEvent(3, i, intent, str, isChangeEnabledInternal);
                        if (isChangeEnabledInternal) {
                            Slog.w("PackageManager", "Intent does not match component's intent filter: " + intent);
                            Slog.w("PackageManager", "Access blocked: " + service.getComponentName());
                            list.remove(size);
                        }
                    }
                }
            }
        }
    }

    public static boolean hasAnyDomainApproval(DomainVerificationManagerInternal domainVerificationManagerInternal, PackageStateInternal packageStateInternal, Intent intent, long j, int i) {
        return domainVerificationManagerInternal.approvalLevelForDomain(packageStateInternal, intent, j, i) > 0;
    }

    public static Intent updateIntentForResolve(Intent intent) {
        return intent.getSelector() != null ? intent.getSelector() : intent;
    }

    public static String arrayToString(int[] iArr) {
        StringBuilder sb = new StringBuilder(128);
        sb.append('[');
        if (iArr != null) {
            for (int i = 0; i < iArr.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(iArr[i]);
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public static File getNextCodePath(File file, String str) {
        File file2;
        SecureRandom secureRandom = new SecureRandom();
        byte[] bArr = new byte[16];
        do {
            secureRandom.nextBytes(bArr);
            file2 = new File(file, "~~" + Base64.encodeToString(bArr, 10));
        } while (file2.exists());
        secureRandom.nextBytes(bArr);
        File file3 = new File(file2, str + '-' + Base64.encodeToString(bArr, 10));
        if (!DEBUG || Objects.equals(tryParsePackageName(file3.getName()), str)) {
            return file3;
        }
        throw new RuntimeException("codepath is off: " + file3.getName() + " (" + str + ")");
    }

    public static String tryParsePackageName(String str) throws IllegalArgumentException {
        int indexOf = str.indexOf(45);
        if (indexOf == -1) {
            throw new IllegalArgumentException("Not a valid package folder name");
        }
        return str.substring(0, indexOf);
    }

    public static int getPackageExternalStorageType(VolumeInfo volumeInfo, boolean z) {
        DiskInfo disk;
        if (volumeInfo == null || (disk = volumeInfo.getDisk()) == null) {
            return 0;
        }
        if (disk.isSd()) {
            return 1;
        }
        if (disk.isUsb()) {
            return 2;
        }
        return z ? 3 : 0;
    }

    public static void enforceSystemOrRootOrShell(String str) {
        if (!isSystemOrRootOrShell()) {
            throw new SecurityException(str);
        }
    }

    public static boolean isSystemOrRootOrShell() {
        return isSystemOrRootOrShell(Binder.getCallingUid());
    }

    public static boolean isSystemOrRoot() {
        return isSystemOrRoot(Binder.getCallingUid());
    }

    public static void enforceSystemOrRoot(String str) {
        if (!isSystemOrRoot()) {
            throw new SecurityException(str);
        }
    }

    public static File preparePackageParserCache(boolean z, boolean z2, String str) {
        File[] listFilesOrEmpty;
        if (z) {
            return null;
        }
        if (SystemProperties.getBoolean("pm.boot.disable_package_cache", false)) {
            Slog.i("PackageManager", "Disabling package parser cache due to system property.");
            return null;
        }
        File packageCacheDirectory = Environment.getPackageCacheDirectory();
        if (FileUtils.createDir(packageCacheDirectory)) {
            String str2 = PackagePartitions.FINGERPRINT;
            for (File file : FileUtils.listFilesOrEmpty(packageCacheDirectory)) {
                if (Objects.equals(str2, file.getName())) {
                    Slog.d("PackageManager", "Keeping known cache " + file.getName());
                } else {
                    Slog.d("PackageManager", "Destroying unknown cache " + file.getName());
                    FileUtils.deleteContentsAndDir(file);
                }
            }
            File createDir = FileUtils.createDir(packageCacheDirectory, str2);
            if (createDir == null) {
                Slog.wtf("PackageManager", "Cache directory cannot be created - wiping base dir " + packageCacheDirectory);
                FileUtils.deleteContentsAndDir(packageCacheDirectory);
                return null;
            } else if (z2 && str.startsWith("eng.")) {
                Slog.w("PackageManager", "Wiping cache directory because the system partition changed.");
                if (createDir.lastModified() < new File(Environment.getRootDirectory(), "framework").lastModified()) {
                    FileUtils.deleteContents(packageCacheDirectory);
                    return FileUtils.createDir(packageCacheDirectory, str2);
                }
                return createDir;
            } else {
                return createDir;
            }
        }
        return null;
    }

    public static void checkDowngrade(AndroidPackage androidPackage, PackageInfoLite packageInfoLite) throws PackageManagerException {
        if (packageInfoLite.getLongVersionCode() < androidPackage.getLongVersionCode()) {
            throw new PackageManagerException(-25, "Update version code " + packageInfoLite.versionCode + " is older than current " + androidPackage.getLongVersionCode());
        } else if (packageInfoLite.getLongVersionCode() != androidPackage.getLongVersionCode()) {
        } else {
            if (packageInfoLite.baseRevisionCode < androidPackage.getBaseRevisionCode()) {
                throw new PackageManagerException(-25, "Update base revision code " + packageInfoLite.baseRevisionCode + " is older than current " + androidPackage.getBaseRevisionCode());
            } else if (ArrayUtils.isEmpty(packageInfoLite.splitNames)) {
            } else {
                int i = 0;
                while (true) {
                    String[] strArr = packageInfoLite.splitNames;
                    if (i >= strArr.length) {
                        return;
                    }
                    String str = strArr[i];
                    int indexOf = ArrayUtils.indexOf(androidPackage.getSplitNames(), str);
                    if (indexOf != -1 && packageInfoLite.splitRevisionCodes[i] < androidPackage.getSplitRevisionCodes()[indexOf]) {
                        throw new PackageManagerException(-25, "Update split " + str + " revision code " + packageInfoLite.splitRevisionCodes[i] + " is older than current " + androidPackage.getSplitRevisionCodes()[indexOf]);
                    }
                    i++;
                }
            }
        }
    }
}
