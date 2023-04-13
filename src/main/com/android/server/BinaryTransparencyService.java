package com.android.server;

import android.annotation.SuppressLint;
import android.apex.ApexInfo;
import android.apex.IApexService;
import android.app.compat.CompatChanges;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApexStagedEvent;
import android.content.pm.IBackgroundInstallControlService;
import android.content.pm.IPackageManagerNative;
import android.content.pm.IStagedApexObserver;
import android.content.pm.InstallSourceInfo;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.hardware.biometrics.SensorProperties;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorProperties;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.face.IFaceAuthenticatorsRegisteredCallback;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorProperties;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintAuthenticatorsRegisteredCallback;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.PackageUtils;
import android.util.Slog;
import android.util.apk.ApkSignatureVerifier;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.expresslog.Histogram;
import com.android.internal.os.IBinaryTransparencyService;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.BinaryTransparencyService;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.AndroidPackageSplit;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import libcore.util.HexEncoding;
/* loaded from: classes.dex */
public class BinaryTransparencyService extends SystemService {
    @VisibleForTesting
    static final String BINARY_HASH_ERROR = "SHA256HashError";
    @VisibleForTesting
    static final String KEY_ENABLE_BIOMETRIC_PROPERTY_VERIFICATION = "enable_biometric_property_verification";
    @VisibleForTesting
    static final String SYSPROP_NAME_VBETA_DIGEST = "ro.boot.vbmeta.digest";
    @VisibleForTesting
    static final String VBMETA_DIGEST_UNAVAILABLE = "vbmeta-digest-unavailable";
    @VisibleForTesting
    static final String VBMETA_DIGEST_UNINITIALIZED = "vbmeta-digest-uninitialized";
    public static final Histogram digestAllPackagesLatency = new Histogram("binary_transparency.value_digest_all_packages_latency_uniform", new Histogram.UniformOptions(50, 0.0f, 500.0f));
    public BiometricLogger mBiometricLogger;
    public final Context mContext;
    public long mMeasurementsLastRecordedMs;
    public PackageManagerInternal mPackageManagerInternal;
    public final BinaryTransparencyServiceImpl mServiceImpl;
    public String mVbmetaDigest;

    public final int toFaceSensorType(int i) {
        if (i != 1) {
            return i != 2 ? 0 : 7;
        }
        return 6;
    }

    public final int toFingerprintSensorType(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        return i != 5 ? 0 : 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public final int toSensorStrength(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? 0 : 3;
            }
            return 2;
        }
        return 1;
    }

    /* loaded from: classes.dex */
    public final class BinaryTransparencyServiceImpl extends IBinaryTransparencyService.Stub {
        public BinaryTransparencyServiceImpl() {
        }

        public String getSignedImageInfo() {
            return BinaryTransparencyService.this.mVbmetaDigest;
        }

        public final String[] computePackageSignerSha256Digests(SigningInfo signingInfo) {
            if (signingInfo == null) {
                Slog.e("TransparencyService", "signingInfo is null");
                return null;
            }
            Signature[] apkContentsSigners = signingInfo.getApkContentsSigners();
            ArrayList arrayList = new ArrayList();
            for (Signature signature : apkContentsSigners) {
                arrayList.add(HexEncoding.encodeToString(PackageUtils.computeSha256DigestBytes(signature.toByteArray()), false));
            }
            return (String[]) arrayList.toArray(new String[1]);
        }

        public final List<IBinaryTransparencyService.AppInfo> collectAppInfo(PackageState packageState, int i) {
            ArrayList arrayList = new ArrayList();
            String packageName = packageState.getPackageName();
            long versionCode = packageState.getVersionCode();
            String[] computePackageSignerSha256Digests = computePackageSignerSha256Digests(packageState.getSigningInfo());
            for (AndroidPackageSplit androidPackageSplit : packageState.getAndroidPackage().getSplits()) {
                IBinaryTransparencyService.AppInfo appInfo = new IBinaryTransparencyService.AppInfo();
                appInfo.packageName = packageName;
                appInfo.longVersion = versionCode;
                appInfo.splitName = androidPackageSplit.getName();
                appInfo.signerDigests = computePackageSignerSha256Digests;
                appInfo.mbaStatus = i;
                Digest measureApk = measureApk(androidPackageSplit.getPath());
                appInfo.digest = measureApk.value;
                appInfo.digestAlgorithm = measureApk.algorithm;
                arrayList.add(appInfo);
            }
            IBinaryTransparencyService.AppInfo appInfo2 = (IBinaryTransparencyService.AppInfo) arrayList.get(0);
            InstallSourceInfo installSourceInfo = BinaryTransparencyService.this.getInstallSourceInfo(packageState.getPackageName());
            if (installSourceInfo != null) {
                appInfo2.initiator = installSourceInfo.getInitiatingPackageName();
                SigningInfo initiatingPackageSigningInfo = installSourceInfo.getInitiatingPackageSigningInfo();
                if (initiatingPackageSigningInfo != null) {
                    appInfo2.initiatorSignerDigests = computePackageSignerSha256Digests(initiatingPackageSigningInfo);
                }
                appInfo2.installer = installSourceInfo.getInstallingPackageName();
                appInfo2.originator = installSourceInfo.getOriginatingPackageName();
            }
            return arrayList;
        }

        public final Digest measureApk(String str) {
            Map<Integer, byte[]> computeApkContentDigest = computeApkContentDigest(str);
            if (computeApkContentDigest == null) {
                Slog.d("TransparencyService", "Failed to compute content digest for " + str);
            } else if (computeApkContentDigest.containsKey(1)) {
                return new Digest(1, computeApkContentDigest.get(1));
            } else {
                if (computeApkContentDigest.containsKey(2)) {
                    return new Digest(2, computeApkContentDigest.get(2));
                }
            }
            return new Digest(4, PackageUtils.computeSha256DigestForLargeFileAsBytes(str, PackageUtils.createLargeFileBuffer()));
        }

        public void recordMeasurementsForAllPackages() {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - BinaryTransparencyService.this.mMeasurementsLastRecordedMs < BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) {
                Slog.d("TransparencyService", "Skip measurement since the last measurement was only taken at " + BinaryTransparencyService.this.mMeasurementsLastRecordedMs + " within the cooldown period");
                return;
            }
            Slog.d("TransparencyService", "Measurement was last taken at " + BinaryTransparencyService.this.mMeasurementsLastRecordedMs + " and is now updated to: " + currentTimeMillis);
            BinaryTransparencyService.this.mMeasurementsLastRecordedMs = currentTimeMillis;
            Bundle bundle = new Bundle();
            for (IBinaryTransparencyService.ApexInfo apexInfo : collectAllApexInfo(false)) {
                bundle.putBoolean(apexInfo.packageName, true);
                recordApexInfo(apexInfo);
            }
            for (IBinaryTransparencyService.AppInfo appInfo : collectAllUpdatedPreloadInfo(bundle)) {
                bundle.putBoolean(appInfo.packageName, true);
                writeAppInfoToLog(appInfo);
            }
            if (CompatChanges.isChangeEnabled(245692487L)) {
                for (IBinaryTransparencyService.AppInfo appInfo2 : collectAllSilentInstalledMbaInfo(bundle)) {
                    bundle.putBoolean(appInfo2.packageName, true);
                    writeAppInfoToLog(appInfo2);
                }
            }
            BinaryTransparencyService.digestAllPackagesLatency.logSample((float) (System.currentTimeMillis() - currentTimeMillis));
        }

        public List<IBinaryTransparencyService.ApexInfo> collectAllApexInfo(boolean z) {
            ArrayList arrayList = new ArrayList();
            for (PackageInfo packageInfo : BinaryTransparencyService.this.getCurrentInstalledApexs()) {
                PackageStateInternal packageStateInternal = BinaryTransparencyService.this.mPackageManagerInternal.getPackageStateInternal(packageInfo.packageName);
                if (packageStateInternal == null) {
                    Slog.w("TransparencyService", "Package state is unavailable, ignoring the APEX " + packageInfo.packageName);
                } else {
                    AndroidPackage androidPackage = packageStateInternal.getAndroidPackage();
                    if (androidPackage == null) {
                        Slog.w("TransparencyService", "Skipping the missing APK in " + androidPackage.getPath());
                    } else {
                        Digest measureApk = measureApk(androidPackage.getPath());
                        if (measureApk == null) {
                            Slog.w("TransparencyService", "Skipping the missing APEX in " + androidPackage.getPath());
                        } else {
                            IBinaryTransparencyService.ApexInfo apexInfo = new IBinaryTransparencyService.ApexInfo();
                            apexInfo.packageName = packageStateInternal.getPackageName();
                            apexInfo.longVersion = packageStateInternal.getVersionCode();
                            apexInfo.digest = measureApk.value;
                            apexInfo.digestAlgorithm = measureApk.algorithm;
                            apexInfo.signerDigests = computePackageSignerSha256Digests(packageStateInternal.getSigningInfo());
                            if (z) {
                                apexInfo.moduleName = BinaryTransparencyService.this.apexPackageNameToModuleName(packageStateInternal.getPackageName());
                            }
                            arrayList.add(apexInfo);
                        }
                    }
                }
            }
            return arrayList;
        }

        public List<IBinaryTransparencyService.AppInfo> collectAllUpdatedPreloadInfo(final Bundle bundle) {
            final ArrayList arrayList = new ArrayList();
            BinaryTransparencyService.this.mContext.getPackageManager();
            BinaryTransparencyService.this.mPackageManagerInternal.forEachPackageState(new Consumer() { // from class: com.android.server.BinaryTransparencyService$BinaryTransparencyServiceImpl$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    BinaryTransparencyService.BinaryTransparencyServiceImpl.this.lambda$collectAllUpdatedPreloadInfo$0(bundle, arrayList, (PackageStateInternal) obj);
                }
            });
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$collectAllUpdatedPreloadInfo$0(Bundle bundle, ArrayList arrayList, PackageStateInternal packageStateInternal) {
            if (packageStateInternal.isUpdatedSystemApp() && !bundle.containsKey(packageStateInternal.getPackageName())) {
                Slog.d("TransparencyService", "Preload " + packageStateInternal.getPackageName() + " at " + packageStateInternal.getPath() + " has likely been updated.");
                arrayList.addAll(collectAppInfo(packageStateInternal, 2));
            }
        }

        public List<IBinaryTransparencyService.AppInfo> collectAllSilentInstalledMbaInfo(Bundle bundle) {
            ArrayList arrayList = new ArrayList();
            for (PackageInfo packageInfo : BinaryTransparencyService.this.getNewlyInstalledMbas()) {
                if (!bundle.containsKey(packageInfo.packageName)) {
                    PackageStateInternal packageStateInternal = BinaryTransparencyService.this.mPackageManagerInternal.getPackageStateInternal(packageInfo.packageName);
                    if (packageStateInternal == null) {
                        Slog.w("TransparencyService", "Package state is unavailable, ignoring the package " + packageInfo.packageName);
                    } else {
                        arrayList.addAll(collectAppInfo(packageStateInternal, 3));
                    }
                }
            }
            return arrayList;
        }

        public final void recordApexInfo(IBinaryTransparencyService.ApexInfo apexInfo) {
            String str = apexInfo.packageName;
            long j = apexInfo.longVersion;
            byte[] bArr = apexInfo.digest;
            FrameworkStatsLog.write((int) FrameworkStatsLog.APEX_INFO_GATHERED, str, j, bArr != null ? HexEncoding.encodeToString(bArr, false) : null, apexInfo.digestAlgorithm, apexInfo.signerDigests);
        }

        public final void writeAppInfoToLog(IBinaryTransparencyService.AppInfo appInfo) {
            String str = appInfo.packageName;
            long j = appInfo.longVersion;
            byte[] bArr = appInfo.digest;
            FrameworkStatsLog.write((int) FrameworkStatsLog.MOBILE_BUNDLED_APP_INFO_GATHERED, str, j, bArr != null ? HexEncoding.encodeToString(bArr, false) : null, appInfo.digestAlgorithm, appInfo.signerDigests, appInfo.mbaStatus, appInfo.initiator, appInfo.initiatorSignerDigests, appInfo.installer, appInfo.originator, appInfo.splitName);
        }

        public final Map<Integer, byte[]> computeApkContentDigest(String str) {
            ParseResult verifySignaturesInternal = ApkSignatureVerifier.verifySignaturesInternal(ParseTypeImpl.forDefaultParsing(), str, 2, false);
            if (verifySignaturesInternal.isError()) {
                Slog.e("TransparencyService", "Failed to compute content digest for " + str + " due to: " + verifySignaturesInternal.getErrorMessage());
                return null;
            }
            return ((ApkSignatureVerifier.SigningDetailsWithDigests) verifySignaturesInternal.getResult()).contentDigests;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new ShellCommand() { // from class: com.android.server.BinaryTransparencyService.BinaryTransparencyServiceImpl.1
                public final int printSignedImageInfo() {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    boolean z = false;
                    while (true) {
                        String nextOption = getNextOption();
                        if (nextOption != null) {
                            if (!nextOption.equals("-a")) {
                                outPrintWriter.println("ERROR: Unknown option: " + nextOption);
                                return 1;
                            }
                            z = true;
                        } else {
                            String signedImageInfo = BinaryTransparencyServiceImpl.this.getSignedImageInfo();
                            outPrintWriter.println("Image Info:");
                            outPrintWriter.println(Build.FINGERPRINT);
                            outPrintWriter.println(signedImageInfo);
                            outPrintWriter.println("");
                            if (z) {
                                if (BinaryTransparencyService.this.mContext.getPackageManager() == null) {
                                    outPrintWriter.println("ERROR: Failed to obtain an instance of package manager.");
                                    return -1;
                                }
                                outPrintWriter.println("Other partitions:");
                                for (Build.Partition partition : Build.getFingerprintedPartitions()) {
                                    outPrintWriter.println("Name: " + partition.getName());
                                    outPrintWriter.println("Fingerprint: " + partition.getFingerprint());
                                    outPrintWriter.println("Build time (ms): " + partition.getBuildTimeMillis());
                                }
                            }
                            return 0;
                        }
                    }
                }

                public final void printPackageMeasurements(PackageInfo packageInfo, boolean z, PrintWriter printWriter) {
                    Map computeApkContentDigest = BinaryTransparencyServiceImpl.this.computeApkContentDigest(packageInfo.applicationInfo.sourceDir);
                    if (computeApkContentDigest == null) {
                        printWriter.println("ERROR: Failed to compute package content digest for " + packageInfo.applicationInfo.sourceDir);
                        return;
                    }
                    if (z) {
                        String computeSha256DigestForLargeFile = PackageUtils.computeSha256DigestForLargeFile(packageInfo.applicationInfo.sourceDir, PackageUtils.createLargeFileBuffer());
                        printWriter.print(computeSha256DigestForLargeFile + ",");
                    }
                    for (Map.Entry entry : computeApkContentDigest.entrySet()) {
                        printWriter.print(BinaryTransparencyService.this.translateContentDigestAlgorithmIdToString(((Integer) entry.getKey()).intValue()));
                        printWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                        printWriter.print(HexEncoding.encodeToString((byte[]) entry.getValue(), false));
                        printWriter.print("\n");
                    }
                }

                public final void printPackageInstallationInfo(PackageInfo packageInfo, boolean z, PrintWriter printWriter) {
                    printWriter.println("--- Package Installation Info ---");
                    printWriter.println("Current install location: " + packageInfo.applicationInfo.sourceDir);
                    if (packageInfo.applicationInfo.sourceDir.startsWith("/data/apex/")) {
                        String originalApexPreinstalledLocation = BinaryTransparencyService.this.getOriginalApexPreinstalledLocation(packageInfo.packageName);
                        printWriter.println("|--> Pre-installed package install location: " + originalApexPreinstalledLocation);
                        if (!originalApexPreinstalledLocation.equals("could-not-be-determined")) {
                            if (z) {
                                String computeSha256DigestForLargeFile = PackageUtils.computeSha256DigestForLargeFile(originalApexPreinstalledLocation, PackageUtils.createLargeFileBuffer());
                                printWriter.println("|--> Pre-installed package SHA-256 digest: " + computeSha256DigestForLargeFile);
                            }
                            Map computeApkContentDigest = BinaryTransparencyServiceImpl.this.computeApkContentDigest(originalApexPreinstalledLocation);
                            if (computeApkContentDigest == null) {
                                printWriter.println("|--> ERROR: Failed to compute package content digest for " + originalApexPreinstalledLocation);
                            } else {
                                for (Map.Entry entry : computeApkContentDigest.entrySet()) {
                                    printWriter.println("|--> Pre-installed package content digest: " + HexEncoding.encodeToString((byte[]) entry.getValue(), false));
                                    printWriter.println("|--> Pre-installed package content digest algorithm: " + BinaryTransparencyService.this.translateContentDigestAlgorithmIdToString(((Integer) entry.getKey()).intValue()));
                                }
                            }
                        }
                    }
                    printWriter.println("First install time (ms): " + packageInfo.firstInstallTime);
                    printWriter.println("Last update time (ms):   " + packageInfo.lastUpdateTime);
                    boolean z2 = packageInfo.firstInstallTime == packageInfo.lastUpdateTime;
                    printWriter.println("Is preloaded: " + z2);
                    InstallSourceInfo installSourceInfo = BinaryTransparencyService.this.getInstallSourceInfo(packageInfo.packageName);
                    if (installSourceInfo == null) {
                        printWriter.println("ERROR: Unable to obtain installSourceInfo of " + packageInfo.packageName);
                    } else {
                        printWriter.println("Installation initiated by: " + installSourceInfo.getInitiatingPackageName());
                        printWriter.println("Installation done by: " + installSourceInfo.getInstallingPackageName());
                        printWriter.println("Installation originating from: " + installSourceInfo.getOriginatingPackageName());
                    }
                    if (packageInfo.isApex) {
                        printWriter.println("Is an active APEX: " + packageInfo.isActiveApex);
                    }
                }

                public final void printPackageSignerDetails(SigningInfo signingInfo, PrintWriter printWriter) {
                    if (signingInfo == null) {
                        printWriter.println("ERROR: Package's signingInfo is null.");
                        return;
                    }
                    printWriter.println("--- Package Signer Info ---");
                    printWriter.println("Has multiple signers: " + signingInfo.hasMultipleSigners());
                    printWriter.println("Signing key has been rotated: " + signingInfo.hasPastSigningCertificates());
                    Signature[] apkContentsSigners = signingInfo.getApkContentsSigners();
                    int length = apkContentsSigners.length;
                    for (int i = 0; i < length; i++) {
                        Signature signature = apkContentsSigners[i];
                        String encodeToString = HexEncoding.encodeToString(PackageUtils.computeSha256DigestBytes(signature.toByteArray()), false);
                        printWriter.println("Signer cert's SHA256-digest: " + encodeToString);
                        try {
                            PublicKey publicKey = signature.getPublicKey();
                            printWriter.println("Signing key algorithm: " + publicKey.getAlgorithm());
                        } catch (CertificateException e) {
                            Slog.e("ShellCommand", "Failed to obtain public key of signer for cert with hash: " + encodeToString, e);
                        }
                    }
                    if (signingInfo.hasMultipleSigners() || !signingInfo.hasPastSigningCertificates()) {
                        return;
                    }
                    printWriter.println("== Signing Cert Lineage (Excluding The Most Recent) ==");
                    printWriter.println("(Certs are sorted in the order of rotation, beginning with the original signing cert)");
                    Signature[] signingCertificateHistory = signingInfo.getSigningCertificateHistory();
                    int i2 = 0;
                    while (i2 < signingCertificateHistory.length - 1) {
                        Signature signature2 = signingCertificateHistory[i2];
                        String encodeToString2 = HexEncoding.encodeToString(PackageUtils.computeSha256DigestBytes(signature2.toByteArray()), false);
                        StringBuilder sb = new StringBuilder();
                        sb.append("  ++ Signer cert #");
                        i2++;
                        sb.append(i2);
                        sb.append(" ++");
                        printWriter.println(sb.toString());
                        printWriter.println("  Cert SHA256-digest: " + encodeToString2);
                        try {
                            PublicKey publicKey2 = signature2.getPublicKey();
                            printWriter.println("  Signing key algorithm: " + publicKey2.getAlgorithm());
                        } catch (CertificateException e2) {
                            Slog.e("ShellCommand", "Failed to obtain public key of signer for cert with hash: " + encodeToString2, e2);
                        }
                    }
                }

                public final void printModuleDetails(ModuleInfo moduleInfo, PrintWriter printWriter) {
                    printWriter.println("--- Module Details ---");
                    printWriter.println("Module name: " + ((Object) moduleInfo.getName()));
                    StringBuilder sb = new StringBuilder();
                    sb.append("Module visibility: ");
                    sb.append(moduleInfo.isHidden() ? "hidden" : "visible");
                    printWriter.println(sb.toString());
                }

                public final void printAppDetails(PackageInfo packageInfo, boolean z, PrintWriter printWriter) {
                    printWriter.println("--- App Details ---");
                    printWriter.println("Name: " + packageInfo.applicationInfo.name);
                    printWriter.println("Label: " + ((Object) BinaryTransparencyService.this.mContext.getPackageManager().getApplicationLabel(packageInfo.applicationInfo)));
                    printWriter.println("Description: " + ((Object) packageInfo.applicationInfo.loadDescription(BinaryTransparencyService.this.mContext.getPackageManager())));
                    printWriter.println("Has code: " + packageInfo.applicationInfo.hasCode());
                    printWriter.println("Is enabled: " + packageInfo.applicationInfo.enabled);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Is suspended: ");
                    int i = 0;
                    sb.append((packageInfo.applicationInfo.flags & 1073741824) != 0);
                    printWriter.println(sb.toString());
                    printWriter.println("Compile SDK version: " + packageInfo.compileSdkVersion);
                    printWriter.println("Target SDK version: " + packageInfo.applicationInfo.targetSdkVersion);
                    printWriter.println("Is privileged: " + packageInfo.applicationInfo.isPrivilegedApp());
                    printWriter.println("Is a stub: " + packageInfo.isStub);
                    printWriter.println("Is a core app: " + packageInfo.coreApp);
                    printWriter.println("SEInfo: " + packageInfo.applicationInfo.seInfo);
                    printWriter.println("Component factory: " + packageInfo.applicationInfo.appComponentFactory);
                    printWriter.println("Process name: " + packageInfo.applicationInfo.processName);
                    printWriter.println("Task affinity: " + packageInfo.applicationInfo.taskAffinity);
                    printWriter.println("UID: " + packageInfo.applicationInfo.uid);
                    printWriter.println("Shared UID: " + packageInfo.sharedUserId);
                    if (z) {
                        printWriter.println("== App's Shared Libraries ==");
                        List sharedLibraryInfos = packageInfo.applicationInfo.getSharedLibraryInfos();
                        if (sharedLibraryInfos == null || sharedLibraryInfos.isEmpty()) {
                            printWriter.println("<none>");
                        }
                        while (i < sharedLibraryInfos.size()) {
                            SharedLibraryInfo sharedLibraryInfo = (SharedLibraryInfo) sharedLibraryInfos.get(i);
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("  ++ Library #");
                            i++;
                            sb2.append(i);
                            sb2.append(" ++");
                            printWriter.println(sb2.toString());
                            printWriter.println("  Lib name: " + sharedLibraryInfo.getName());
                            long longVersion = sharedLibraryInfo.getLongVersion();
                            printWriter.print("  Lib version: ");
                            if (longVersion == -1) {
                                printWriter.print("undefined");
                            } else {
                                printWriter.print(longVersion);
                            }
                            printWriter.print("\n");
                            printWriter.println("  Lib package name (if available): " + sharedLibraryInfo.getPackageName());
                            printWriter.println("  Lib path: " + sharedLibraryInfo.getPath());
                            printWriter.print("  Lib type: ");
                            int type = sharedLibraryInfo.getType();
                            if (type == 0) {
                                printWriter.print("built-in");
                            } else if (type == 1) {
                                printWriter.print("dynamic");
                            } else if (type == 2) {
                                printWriter.print("static");
                            } else if (type == 3) {
                                printWriter.print("SDK");
                            } else {
                                printWriter.print("undefined");
                            }
                            printWriter.print("\n");
                            printWriter.println("  Is a native lib: " + sharedLibraryInfo.isNative());
                        }
                    }
                }

                public final void printHeadersHelper(String str, boolean z, PrintWriter printWriter) {
                    printWriter.print(str + " Info [Format: package_name,package_version,");
                    if (z) {
                        printWriter.print("package_sha256_digest,");
                    }
                    printWriter.print("content_digest_algorithm:content_digest]:\n");
                }

                public final int printAllApexs() {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    boolean z = false;
                    boolean z2 = false;
                    boolean z3 = true;
                    while (true) {
                        String nextOption = getNextOption();
                        char c = 65535;
                        if (nextOption == null) {
                            PackageManager packageManager = BinaryTransparencyService.this.mContext.getPackageManager();
                            if (packageManager == null) {
                                outPrintWriter.println("ERROR: Failed to obtain an instance of package manager.");
                                return -1;
                            }
                            if (!z && z3) {
                                printHeadersHelper("APEX", z2, outPrintWriter);
                            }
                            for (PackageInfo packageInfo : BinaryTransparencyService.this.getCurrentInstalledApexs()) {
                                if (z && z3) {
                                    printHeadersHelper("APEX", z2, outPrintWriter);
                                }
                                outPrintWriter.print(packageInfo.packageName + "," + packageInfo.getLongVersionCode() + ",");
                                printPackageMeasurements(packageInfo, z2, outPrintWriter);
                                if (z) {
                                    try {
                                        ModuleInfo moduleInfo = packageManager.getModuleInfo(packageInfo.packageName, 0);
                                        outPrintWriter.println("Is a module: true");
                                        printModuleDetails(moduleInfo, outPrintWriter);
                                    } catch (PackageManager.NameNotFoundException unused) {
                                        outPrintWriter.println("Is a module: false");
                                    }
                                    printPackageInstallationInfo(packageInfo, z2, outPrintWriter);
                                    printPackageSignerDetails(packageInfo.signingInfo, outPrintWriter);
                                    outPrintWriter.println("");
                                }
                            }
                            return 0;
                        }
                        switch (nextOption.hashCode()) {
                            case 1506:
                                if (nextOption.equals("-o")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1513:
                                if (nextOption.equals("-v")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 43009159:
                                if (nextOption.equals("--old")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 967085338:
                                if (nextOption.equals("--no-headers")) {
                                    c = 3;
                                    break;
                                }
                                break;
                            case 1737088994:
                                if (nextOption.equals("--verbose")) {
                                    c = 4;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                            case 2:
                                z2 = true;
                                break;
                            case 1:
                            case 4:
                                z = true;
                                break;
                            case 3:
                                z3 = false;
                                break;
                            default:
                                outPrintWriter.println("ERROR: Unknown option: " + nextOption);
                                return 1;
                        }
                    }
                }

                public final int printAllModules() {
                    PackageManager packageManager;
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    boolean z = true;
                    boolean z2 = false;
                    boolean z3 = false;
                    while (true) {
                        String nextOption = getNextOption();
                        char c = 65535;
                        if (nextOption == null) {
                            PackageManager packageManager2 = BinaryTransparencyService.this.mContext.getPackageManager();
                            if (packageManager2 == null) {
                                outPrintWriter.println("ERROR: Failed to obtain an instance of package manager.");
                                return -1;
                            }
                            if (!z2 && z) {
                                printHeadersHelper("Module", z3, outPrintWriter);
                            }
                            for (ModuleInfo moduleInfo : packageManager2.getInstalledModules(IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES)) {
                                String packageName = moduleInfo.getPackageName();
                                if (z2 && z) {
                                    printHeadersHelper("Module", z3, outPrintWriter);
                                }
                                try {
                                    PackageInfo packageInfo = packageManager2.getPackageInfo(packageName, 1207959552);
                                    outPrintWriter.print(packageInfo.packageName + ",");
                                    StringBuilder sb = new StringBuilder();
                                    packageManager = packageManager2;
                                    try {
                                        sb.append(packageInfo.getLongVersionCode());
                                        sb.append(",");
                                        outPrintWriter.print(sb.toString());
                                        printPackageMeasurements(packageInfo, z3, outPrintWriter);
                                        if (z2) {
                                            printModuleDetails(moduleInfo, outPrintWriter);
                                            printPackageInstallationInfo(packageInfo, z3, outPrintWriter);
                                            printPackageSignerDetails(packageInfo.signingInfo, outPrintWriter);
                                            outPrintWriter.println("");
                                        }
                                    } catch (PackageManager.NameNotFoundException unused) {
                                        outPrintWriter.println(packageName + ",ERROR:Unable to find PackageInfo for this module.");
                                        if (z2) {
                                            printModuleDetails(moduleInfo, outPrintWriter);
                                            outPrintWriter.println("");
                                        }
                                        packageManager2 = packageManager;
                                    }
                                } catch (PackageManager.NameNotFoundException unused2) {
                                    packageManager = packageManager2;
                                }
                                packageManager2 = packageManager;
                            }
                            return 0;
                        }
                        switch (nextOption.hashCode()) {
                            case 1506:
                                if (nextOption.equals("-o")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1513:
                                if (nextOption.equals("-v")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 43009159:
                                if (nextOption.equals("--old")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 967085338:
                                if (nextOption.equals("--no-headers")) {
                                    c = 3;
                                    break;
                                }
                                break;
                            case 1737088994:
                                if (nextOption.equals("--verbose")) {
                                    c = 4;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                            case 2:
                                z3 = true;
                                break;
                            case 1:
                            case 4:
                                z2 = true;
                                break;
                            case 3:
                                z = false;
                                break;
                            default:
                                outPrintWriter.println("ERROR: Unknown option: " + nextOption);
                                return 1;
                        }
                    }
                }

                public final int printAllMbas() {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    boolean z = true;
                    boolean z2 = false;
                    boolean z3 = false;
                    boolean z4 = false;
                    boolean z5 = false;
                    while (true) {
                        String nextOption = getNextOption();
                        if (nextOption == null) {
                            if (!z2 && z) {
                                if (z3) {
                                    printHeadersHelper("Preload", z4, outPrintWriter);
                                } else {
                                    printHeadersHelper("MBA", z4, outPrintWriter);
                                }
                            }
                            PackageManager packageManager = BinaryTransparencyService.this.mContext.getPackageManager();
                            Iterator it = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(136314880L)).iterator();
                            while (it.hasNext()) {
                                PackageInfo packageInfo = (PackageInfo) it.next();
                                if (packageInfo.signingInfo == null) {
                                    try {
                                        packageManager.getPackageInfo(packageInfo.packageName, PackageManager.PackageInfoFlags.of(134348800L));
                                    } catch (PackageManager.NameNotFoundException unused) {
                                        Slog.e("ShellCommand", "Failed to obtain an updated PackageInfo of " + packageInfo.packageName);
                                    }
                                }
                                if (z2 && z) {
                                    printHeadersHelper("Preload", z4, outPrintWriter);
                                }
                                outPrintWriter.print(packageInfo.packageName + ",");
                                StringBuilder sb = new StringBuilder();
                                PackageManager packageManager2 = packageManager;
                                Iterator it2 = it;
                                sb.append(packageInfo.getLongVersionCode());
                                sb.append(",");
                                outPrintWriter.print(sb.toString());
                                printPackageMeasurements(packageInfo, z4, outPrintWriter);
                                if (z2) {
                                    printAppDetails(packageInfo, z5, outPrintWriter);
                                    printPackageInstallationInfo(packageInfo, z4, outPrintWriter);
                                    printPackageSignerDetails(packageInfo.signingInfo, outPrintWriter);
                                    outPrintWriter.println("");
                                }
                                packageManager = packageManager2;
                                it = it2;
                            }
                            if (z3) {
                                return 0;
                            }
                            for (PackageInfo packageInfo2 : BinaryTransparencyService.this.getNewlyInstalledMbas()) {
                                if (z2 && z) {
                                    printHeadersHelper("MBA", z4, outPrintWriter);
                                }
                                outPrintWriter.print(packageInfo2.packageName + ",");
                                outPrintWriter.print(packageInfo2.getLongVersionCode() + ",");
                                printPackageMeasurements(packageInfo2, z4, outPrintWriter);
                                if (z2) {
                                    printAppDetails(packageInfo2, z5, outPrintWriter);
                                    printPackageInstallationInfo(packageInfo2, z4, outPrintWriter);
                                    printPackageSignerDetails(packageInfo2.signingInfo, outPrintWriter);
                                    outPrintWriter.println("");
                                }
                            }
                            return 0;
                        }
                        char c = 65535;
                        switch (nextOption.hashCode()) {
                            case 1503:
                                if (nextOption.equals("-l")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1506:
                                if (nextOption.equals("-o")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 1513:
                                if (nextOption.equals("-v")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 43009159:
                                if (nextOption.equals("--old")) {
                                    c = 3;
                                    break;
                                }
                                break;
                            case 705409647:
                                if (nextOption.equals("--preloads-only")) {
                                    c = 4;
                                    break;
                                }
                                break;
                            case 967085338:
                                if (nextOption.equals("--no-headers")) {
                                    c = 5;
                                    break;
                                }
                                break;
                            case 1737088994:
                                if (nextOption.equals("--verbose")) {
                                    c = 6;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                z5 = true;
                                break;
                            case 1:
                            case 3:
                                z4 = true;
                                break;
                            case 2:
                            case 6:
                                z2 = true;
                                break;
                            case 4:
                                z3 = true;
                                break;
                            case 5:
                                z = false;
                                break;
                            default:
                                outPrintWriter.println("ERROR: Unknown option: " + nextOption);
                                return 1;
                        }
                    }
                }

                public int onCommand(String str) {
                    if (str == null) {
                        return handleDefaultCommands(str);
                    }
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    if (str.equals("get")) {
                        String nextArg = getNextArg();
                        char c = 65535;
                        if (nextArg == null) {
                            printHelpMenu();
                            return -1;
                        }
                        switch (nextArg.hashCode()) {
                            case -1443097326:
                                if (nextArg.equals("image_info")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case -1195140447:
                                if (nextArg.equals("module_info")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 636812193:
                                if (nextArg.equals("mba_info")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 1366866347:
                                if (nextArg.equals("apex_info")) {
                                    c = 3;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                return printSignedImageInfo();
                            case 1:
                                return printAllModules();
                            case 2:
                                return printAllMbas();
                            case 3:
                                return printAllApexs();
                            default:
                                outPrintWriter.println(String.format("ERROR: Unknown info type '%s'", nextArg));
                                return 1;
                        }
                    }
                    return handleDefaultCommands(str);
                }

                public final void printHelpMenu() {
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    outPrintWriter.println("Transparency manager (transparency) commands:");
                    outPrintWriter.println("  help");
                    outPrintWriter.println("    Print this help text.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  get image_info [-a]");
                    outPrintWriter.println("    Print information about loaded image (firmware). Options:");
                    outPrintWriter.println("        -a: lists all other identifiable partitions.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  get apex_info [-o] [-v] [--no-headers]");
                    outPrintWriter.println("    Print information about installed APEXs on device.");
                    outPrintWriter.println("      -o: also uses the old digest scheme (SHA256) to compute APEX hashes. WARNING: This can be a very slow and CPU-intensive computation.");
                    outPrintWriter.println("      -v: lists more verbose information about each APEX.");
                    outPrintWriter.println("      --no-headers: does not print the header if specified.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  get module_info [-o] [-v] [--no-headers]");
                    outPrintWriter.println("    Print information about installed modules on device.");
                    outPrintWriter.println("      -o: also uses the old digest scheme (SHA256) to compute module hashes. WARNING: This can be a very slow and CPU-intensive computation.");
                    outPrintWriter.println("      -v: lists more verbose information about each module.");
                    outPrintWriter.println("      --no-headers: does not print the header if specified.");
                    outPrintWriter.println("");
                    outPrintWriter.println("  get mba_info [-o] [-v] [-l] [--no-headers] [--preloads-only]");
                    outPrintWriter.println("    Print information about installed mobile bundle apps (MBAs on device).");
                    outPrintWriter.println("      -o: also uses the old digest scheme (SHA256) to compute MBA hashes. WARNING: This can be a very slow and CPU-intensive computation.");
                    outPrintWriter.println("      -v: lists more verbose information about each app.");
                    outPrintWriter.println("      -l: lists shared library info. (This option only works when -v option is also specified)");
                    outPrintWriter.println("      --no-headers: does not print the header if specified.");
                    outPrintWriter.println("      --preloads-only: lists only preloaded apps. This options can also be combined with others.");
                    outPrintWriter.println("");
                }

                public void onHelp() {
                    printHelpMenu();
                }
            }.exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class BiometricLogger {
        public static final BiometricLogger sInstance = new BiometricLogger();

        private BiometricLogger() {
        }

        public static BiometricLogger getInstance() {
            return sInstance;
        }

        public void logStats(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.BIOMETRIC_PROPERTIES_COLLECTED, i, i2, i3, i4, str, str2, str3, str4, str5);
        }
    }

    public BinaryTransparencyService(Context context) {
        this(context, BiometricLogger.getInstance());
    }

    @VisibleForTesting
    public BinaryTransparencyService(Context context, BiometricLogger biometricLogger) {
        super(context);
        this.mContext = context;
        this.mServiceImpl = new BinaryTransparencyServiceImpl();
        this.mVbmetaDigest = VBMETA_DIGEST_UNINITIALIZED;
        this.mMeasurementsLastRecordedMs = 0L;
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mBiometricLogger = biometricLogger;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        try {
            publishBinderService("transparency", this.mServiceImpl);
            Slog.i("TransparencyService", "Started BinaryTransparencyService");
        } catch (Throwable th) {
            Slog.e("TransparencyService", "Failed to start BinaryTransparencyService.", th);
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 1000) {
            Slog.i("TransparencyService", "Boot completed. Getting VBMeta Digest.");
            getVBMetaDigestInformation();
            Slog.i("TransparencyService", "Boot completed. Collecting biometric system properties.");
            collectBiometricProperties();
            Slog.i("TransparencyService", "Scheduling measurements to be taken.");
            UpdateMeasurementsJobService.scheduleBinaryMeasurements(this.mContext, this);
            registerAllPackageUpdateObservers();
        }
    }

    /* loaded from: classes.dex */
    public static class UpdateMeasurementsJobService extends JobService {
        public static long sTimeLastRanMs;

        @Override // android.app.job.JobService
        public boolean onStopJob(JobParameters jobParameters) {
            return false;
        }

        @Override // android.app.job.JobService
        public boolean onStartJob(final JobParameters jobParameters) {
            Slog.d("TransparencyService", "Job to update binary measurements started.");
            if (jobParameters.getJobId() != 1740526926) {
                return false;
            }
            Executors.defaultThreadFactory().newThread(new Runnable() { // from class: com.android.server.BinaryTransparencyService$UpdateMeasurementsJobService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BinaryTransparencyService.UpdateMeasurementsJobService.this.lambda$onStartJob$0(jobParameters);
                }
            }).start();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters) {
            try {
                IBinaryTransparencyService.Stub.asInterface(ServiceManager.getService("transparency")).recordMeasurementsForAllPackages();
                sTimeLastRanMs = System.currentTimeMillis();
                jobFinished(jobParameters, false);
            } catch (RemoteException e) {
                Slog.e("TransparencyService", "Taking binary measurements was interrupted.", e);
            }
        }

        @SuppressLint({"DefaultLocale"})
        public static void scheduleBinaryMeasurements(Context context, BinaryTransparencyService binaryTransparencyService) {
            Slog.i("TransparencyService", "Scheduling binary content-digest computation job");
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
            if (jobScheduler == null) {
                Slog.e("TransparencyService", "Failed to obtain an instance of JobScheduler.");
            } else if (jobScheduler.getPendingJob(1740526926) != null) {
                Slog.d("TransparencyService", "A measurement job has already been scheduled.");
            } else {
                long j = 0;
                if (sTimeLastRanMs != 0) {
                    j = Math.max(0L, Math.min(BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS - (System.currentTimeMillis() - sTimeLastRanMs), (long) BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS));
                    Slog.d("TransparencyService", "Scheduling the next measurement to be done at least " + j + "ms from now.");
                }
                if (jobScheduler.schedule(new JobInfo.Builder(1740526926, new ComponentName(context, UpdateMeasurementsJobService.class)).setRequiresDeviceIdle(true).setRequiresCharging(true).setMinimumLatency(j).build()) != 1) {
                    Slog.e("TransparencyService", "Failed to schedule job to measure binaries.");
                } else {
                    Slog.d("TransparencyService", TextUtils.formatSimple("Job %d to measure binaries was scheduled successfully.", new Object[]{1740526926}));
                }
            }
        }
    }

    public final void logBiometricProperties(SensorProperties sensorProperties, int i, int i2) {
        int sensorId = sensorProperties.getSensorId();
        int sensorStrength = toSensorStrength(sensorProperties.getSensorStrength());
        for (SensorProperties.ComponentInfo componentInfo : sensorProperties.getComponentInfo()) {
            this.mBiometricLogger.logStats(sensorId, i, i2, sensorStrength, componentInfo.getComponentId().trim(), componentInfo.getHardwareVersion().trim(), componentInfo.getFirmwareVersion().trim(), componentInfo.getSerialNumber().trim(), componentInfo.getSoftwareVersion().trim());
        }
    }

    @VisibleForTesting
    public void collectBiometricProperties() {
        if (DeviceConfig.getBoolean("biometrics", KEY_ENABLE_BIOMETRIC_PROPERTY_VERIFICATION, false)) {
            PackageManager packageManager = this.mContext.getPackageManager();
            FaceManager faceManager = null;
            FingerprintManager fingerprintManager = (packageManager == null || !packageManager.hasSystemFeature("android.hardware.fingerprint")) ? null : (FingerprintManager) this.mContext.getSystemService(FingerprintManager.class);
            if (packageManager != null && packageManager.hasSystemFeature("android.hardware.biometrics.face")) {
                faceManager = (FaceManager) this.mContext.getSystemService(FaceManager.class);
            }
            if (fingerprintManager != null) {
                fingerprintManager.addAuthenticatorsRegisteredCallback(new IFingerprintAuthenticatorsRegisteredCallback.Stub() { // from class: com.android.server.BinaryTransparencyService.1
                    public void onAllAuthenticatorsRegistered(List<FingerprintSensorPropertiesInternal> list) {
                        for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : list) {
                            FingerprintSensorProperties from = FingerprintSensorProperties.from(fingerprintSensorPropertiesInternal);
                            BinaryTransparencyService binaryTransparencyService = BinaryTransparencyService.this;
                            binaryTransparencyService.logBiometricProperties(from, 1, binaryTransparencyService.toFingerprintSensorType(from.getSensorType()));
                        }
                    }
                });
            }
            if (faceManager != null) {
                faceManager.addAuthenticatorsRegisteredCallback(new IFaceAuthenticatorsRegisteredCallback.Stub() { // from class: com.android.server.BinaryTransparencyService.2
                    public void onAllAuthenticatorsRegistered(List<FaceSensorPropertiesInternal> list) {
                        for (FaceSensorPropertiesInternal faceSensorPropertiesInternal : list) {
                            FaceSensorProperties from = FaceSensorProperties.from(faceSensorPropertiesInternal);
                            BinaryTransparencyService binaryTransparencyService = BinaryTransparencyService.this;
                            binaryTransparencyService.logBiometricProperties(from, 4, binaryTransparencyService.toFaceSensorType(from.getSensorType()));
                        }
                    }
                });
            }
        }
    }

    public final void getVBMetaDigestInformation() {
        String str = SystemProperties.get(SYSPROP_NAME_VBETA_DIGEST, VBMETA_DIGEST_UNAVAILABLE);
        this.mVbmetaDigest = str;
        Slog.d("TransparencyService", String.format("VBMeta Digest: %s", str));
        FrameworkStatsLog.write((int) FrameworkStatsLog.VBMETA_DIGEST_REPORTED, this.mVbmetaDigest);
    }

    public final void registerApkAndNonStagedApexUpdateListener() {
        Slog.d("TransparencyService", "Registering APK & Non-Staged APEX updates...");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(new PackageUpdatedReceiver(), intentFilter);
    }

    public final void registerStagedApexUpdateObserver() {
        Slog.d("TransparencyService", "Registering APEX updates...");
        IPackageManagerNative asInterface = IPackageManagerNative.Stub.asInterface(ServiceManager.getService("package_native"));
        if (asInterface == null) {
            Slog.e("TransparencyService", "IPackageManagerNative is null");
            return;
        }
        try {
            asInterface.registerStagedApexObserver(new IStagedApexObserver.Stub() { // from class: com.android.server.BinaryTransparencyService.3
                public void onApexStaged(ApexStagedEvent apexStagedEvent) throws RemoteException {
                    Slog.d("TransparencyService", "A new APEX has been staged for update. There are currently " + apexStagedEvent.stagedApexModuleNames.length + " APEX(s) staged for update. Scheduling measurement...");
                    UpdateMeasurementsJobService.scheduleBinaryMeasurements(BinaryTransparencyService.this.mContext, BinaryTransparencyService.this);
                }
            });
        } catch (RemoteException unused) {
            Slog.e("TransparencyService", "Failed to register a StagedApexObserver.");
        }
    }

    public final boolean isPackagePreloaded(String str) {
        try {
            this.mContext.getPackageManager().getPackageInfo(str, PackageManager.PackageInfoFlags.of(2097152L));
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final boolean isPackageAnApex(String str) {
        try {
            return this.mContext.getPackageManager().getPackageInfo(str, PackageManager.PackageInfoFlags.of(1073741824L)).isApex;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public class PackageUpdatedReceiver extends BroadcastReceiver {
        public PackageUpdatedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                Uri data = intent.getData();
                if (data == null) {
                    Slog.e("TransparencyService", "Shouldn't happen: intent data is null!");
                } else if (!intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    Slog.d("TransparencyService", "Not an update. Skipping...");
                } else {
                    String schemeSpecificPart = data.getSchemeSpecificPart();
                    if (BinaryTransparencyService.this.isPackagePreloaded(schemeSpecificPart) || BinaryTransparencyService.this.isPackageAnApex(schemeSpecificPart)) {
                        Slog.d("TransparencyService", schemeSpecificPart + " was updated. Scheduling measurement...");
                        UpdateMeasurementsJobService.scheduleBinaryMeasurements(BinaryTransparencyService.this.mContext, BinaryTransparencyService.this);
                    }
                }
            }
        }
    }

    public final void registerAllPackageUpdateObservers() {
        registerApkAndNonStagedApexUpdateListener();
        registerStagedApexUpdateObserver();
    }

    public final String translateContentDigestAlgorithmIdToString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        return "UNKNOWN_ALGO_ID(" + i + ")";
                    }
                    return "SHA256";
                }
                return "VERITY_CHUNKED_SHA256";
            }
            return "CHUNKED_SHA512";
        }
        return "CHUNKED_SHA256";
    }

    public final List<PackageInfo> getCurrentInstalledApexs() {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager == null) {
            Slog.e("TransparencyService", "Error obtaining an instance of PackageManager.");
            return arrayList;
        }
        List installedPackages = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(1207959552L));
        if (installedPackages == null) {
            Slog.e("TransparencyService", "Error obtaining installed packages (including APEX)");
            return arrayList;
        }
        return (List) installedPackages.stream().filter(new Predicate() { // from class: com.android.server.BinaryTransparencyService$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean z;
                z = ((PackageInfo) obj).isApex;
                return z;
            }
        }).collect(Collectors.toList());
    }

    public final InstallSourceInfo getInstallSourceInfo(String str) {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager == null) {
            Slog.e("TransparencyService", "Error obtaining an instance of PackageManager.");
            return null;
        }
        try {
            return packageManager.getInstallSourceInfo(str);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final String getOriginalApexPreinstalledLocation(String str) {
        ApexInfo[] allPackages;
        try {
            String apexPackageNameToModuleName = apexPackageNameToModuleName(str);
            for (ApexInfo apexInfo : IApexService.Stub.asInterface(Binder.allowBlocking(ServiceManager.waitForService("apexservice"))).getAllPackages()) {
                if (apexPackageNameToModuleName.equals(apexInfo.moduleName)) {
                    return apexInfo.preinstalledModulePath;
                }
            }
            return "could-not-be-determined";
        } catch (RemoteException e) {
            Slog.e("TransparencyService", "Unable to get package list from apexservice", e);
            return "could-not-be-determined";
        }
    }

    public final String apexPackageNameToModuleName(String str) {
        return ApexManager.getInstance().getApexModuleNameForPackageName(str);
    }

    public final List<PackageInfo> getNewlyInstalledMbas() {
        ArrayList arrayList = new ArrayList();
        IBackgroundInstallControlService asInterface = IBackgroundInstallControlService.Stub.asInterface(ServiceManager.getService("background_install_control"));
        if (asInterface == null) {
            Slog.e("TransparencyService", "Failed to obtain an IBinder instance of IBackgroundInstallControlService");
            return arrayList;
        }
        try {
            return asInterface.getBackgroundInstalledPackages(134348800L, 0).getList();
        } catch (RemoteException e) {
            Slog.e("TransparencyService", "Failed to get a list of MBAs.", e);
            return arrayList;
        }
    }

    /* loaded from: classes.dex */
    public static class Digest {
        public int algorithm;
        public byte[] value;

        public Digest(int i, byte[] bArr) {
            this.algorithm = i;
            this.value = bArr;
        }
    }
}
