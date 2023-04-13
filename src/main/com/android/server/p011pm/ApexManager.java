package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.apex.ApexInfoList;
import android.apex.ApexSessionInfo;
import android.apex.ApexSessionParams;
import android.apex.CompressedApexInfoList;
import android.apex.IApexService;
import android.os.Binder;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.sysprop.ApexProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Singleton;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.modules.utils.build.UnboundedSdkLevel;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.component.ParsedApexSystemService;
import com.android.server.utils.TimingsTraceAndSlog;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.pm.ApexManager */
/* loaded from: classes2.dex */
public abstract class ApexManager {
    public static final Singleton<ApexManager> sApexManagerSingleton = new Singleton<ApexManager>() { // from class: com.android.server.pm.ApexManager.1
        /* renamed from: create */
        public ApexManager m5352create() {
            if (((Boolean) ApexProperties.updatable().orElse(Boolean.FALSE)).booleanValue()) {
                return new ApexManagerImpl();
            }
            return new ApexManagerFlattenedApex();
        }
    };

    public abstract boolean abortStagedSession(int i);

    public abstract boolean destroyCeSnapshots(int i, int i2);

    public abstract boolean destroyCeSnapshotsNotSpecified(int i, int[] iArr);

    public abstract boolean destroyDeSnapshots(int i);

    public abstract void dump(PrintWriter printWriter);

    public abstract List<ActiveApexInfo> getActiveApexInfos();

    public abstract String getActiveApexPackageNameContainingPackage(String str);

    public abstract String getActivePackageNameForApexModuleName(String str);

    public abstract ApexInfo[] getAllApexInfos();

    public abstract String getApexModuleNameForPackageName(String str);

    public abstract List<ApexSystemServiceInfo> getApexSystemServices();

    public abstract String getApkInApexInstallError(String str);

    public abstract List<String> getApksInApex(String str);

    public abstract File getBackingApexFile(File file);

    public abstract SparseArray<ApexSessionInfo> getSessions();

    public abstract ApexInfo[] getStagedApexInfos(ApexSessionParams apexSessionParams);

    public abstract ApexSessionInfo getStagedSessionInfo(int i);

    public abstract ApexInfo installPackage(File file) throws PackageManagerException;

    public abstract boolean isApexSupported();

    public abstract void markBootCompleted();

    public abstract void markStagedSessionReady(int i) throws PackageManagerException;

    public abstract void markStagedSessionSuccessful(int i);

    public abstract void notifyScanResult(List<ScanResult> list);

    public abstract void registerApkInApex(AndroidPackage androidPackage);

    public abstract void reportErrorWithApkInApex(String str, String str2);

    public abstract void reserveSpaceForCompressedApex(CompressedApexInfoList compressedApexInfoList) throws RemoteException;

    public abstract boolean restoreCeData(int i, int i2, String str);

    public abstract boolean revertActiveSessions();

    public abstract boolean snapshotCeData(int i, int i2, String str);

    public abstract ApexInfoList submitStagedSession(ApexSessionParams apexSessionParams) throws PackageManagerException;

    public abstract boolean uninstallApex(String str);

    public static ApexManager getInstance() {
        return (ApexManager) sApexManagerSingleton.get();
    }

    /* renamed from: com.android.server.pm.ApexManager$ScanResult */
    /* loaded from: classes2.dex */
    public static class ScanResult {
        public final ApexInfo apexInfo;
        public final String packageName;
        public final AndroidPackage pkg;

        public ScanResult(ApexInfo apexInfo, AndroidPackage androidPackage, String str) {
            this.apexInfo = apexInfo;
            this.pkg = androidPackage;
            this.packageName = str;
        }
    }

    /* renamed from: com.android.server.pm.ApexManager$ActiveApexInfo */
    /* loaded from: classes2.dex */
    public static class ActiveApexInfo {
        public final boolean activeApexChanged;
        public final File apexDirectory;
        public final File apexFile;
        public final String apexModuleName;
        public final boolean isFactory;
        public final File preInstalledApexPath;

        public ActiveApexInfo(File file, File file2, File file3) {
            this(null, file, file2, true, file3, false);
        }

        public ActiveApexInfo(String str, File file, File file2, boolean z, File file3, boolean z2) {
            this.apexModuleName = str;
            this.apexDirectory = file;
            this.preInstalledApexPath = file2;
            this.isFactory = z;
            this.apexFile = file3;
            this.activeApexChanged = z2;
        }

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public ActiveApexInfo(ApexInfo apexInfo) {
            this(r1, new File(Environment.getApexDirectory() + File.separator + apexInfo.moduleName), new File(apexInfo.preinstalledModulePath), apexInfo.isFactory, new File(apexInfo.modulePath), apexInfo.activeApexChanged);
            String str = apexInfo.moduleName;
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.ApexManager$ApexManagerImpl */
    /* loaded from: classes2.dex */
    public static class ApexManagerImpl extends ApexManager {
        @GuardedBy({"mLock"})
        public Set<ActiveApexInfo> mActiveApexInfosCache;
        @GuardedBy({"mLock"})
        public ArrayMap<String, String> mApexModuleNameToActivePackageName;
        @GuardedBy({"mLock"})
        public ArrayMap<String, String> mPackageNameToApexModuleName;
        public final Object mLock = new Object();
        @GuardedBy({"mLock"})
        public List<ApexSystemServiceInfo> mApexSystemServices = new ArrayList();
        @GuardedBy({"mLock"})
        public ArrayMap<String, List<String>> mApksInApex = new ArrayMap<>();
        @GuardedBy({"mLock"})
        public Map<String, String> mErrorWithApkInApex = new ArrayMap();

        @Override // com.android.server.p011pm.ApexManager
        public boolean isApexSupported() {
            return true;
        }

        @VisibleForTesting
        public IApexService waitForApexService() {
            return IApexService.Stub.asInterface(Binder.allowBlocking(ServiceManager.waitForService("apexservice")));
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo[] getAllApexInfos() {
            try {
                return waitForApexService().getAllPackages();
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to retrieve packages from apexservice: " + e.toString());
                throw new RuntimeException(e);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void notifyScanResult(List<ScanResult> list) {
            synchronized (this.mLock) {
                notifyScanResultLocked(list);
            }
        }

        @GuardedBy({"mLock"})
        public final void notifyScanResultLocked(List<ScanResult> list) {
            this.mPackageNameToApexModuleName = new ArrayMap<>();
            this.mApexModuleNameToActivePackageName = new ArrayMap<>();
            for (ScanResult scanResult : list) {
                ApexInfo apexInfo = scanResult.apexInfo;
                String str = scanResult.packageName;
                for (ParsedApexSystemService parsedApexSystemService : scanResult.pkg.getApexSystemServices()) {
                    String minSdkVersion = parsedApexSystemService.getMinSdkVersion();
                    if (minSdkVersion != null && !UnboundedSdkLevel.isAtLeast(minSdkVersion)) {
                        Slog.d("ApexManager", String.format("ApexSystemService %s with min_sdk_version=%s is skipped", parsedApexSystemService.getName(), parsedApexSystemService.getMinSdkVersion()));
                    } else {
                        String maxSdkVersion = parsedApexSystemService.getMaxSdkVersion();
                        if (maxSdkVersion != null && !UnboundedSdkLevel.isAtMost(maxSdkVersion)) {
                            Slog.d("ApexManager", String.format("ApexSystemService %s with max_sdk_version=%s is skipped", parsedApexSystemService.getName(), parsedApexSystemService.getMaxSdkVersion()));
                        } else if (apexInfo.isActive) {
                            String name = parsedApexSystemService.getName();
                            for (int i = 0; i < this.mApexSystemServices.size(); i++) {
                                ApexSystemServiceInfo apexSystemServiceInfo = this.mApexSystemServices.get(i);
                                if (apexSystemServiceInfo.getName().equals(name)) {
                                    throw new IllegalStateException(TextUtils.formatSimple("Duplicate apex-system-service %s from %s, %s", new Object[]{name, apexSystemServiceInfo.mJarPath, parsedApexSystemService.getJarPath()}));
                                }
                            }
                            this.mApexSystemServices.add(new ApexSystemServiceInfo(parsedApexSystemService.getName(), parsedApexSystemService.getJarPath(), parsedApexSystemService.getInitOrder()));
                        } else {
                            continue;
                        }
                    }
                }
                Collections.sort(this.mApexSystemServices);
                this.mPackageNameToApexModuleName.put(str, apexInfo.moduleName);
                if (apexInfo.isActive) {
                    if (this.mApexModuleNameToActivePackageName.containsKey(apexInfo.moduleName)) {
                        throw new IllegalStateException("Two active packages have the same APEX module name: " + apexInfo.moduleName);
                    }
                    this.mApexModuleNameToActivePackageName.put(apexInfo.moduleName, str);
                }
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<ActiveApexInfo> getActiveApexInfos() {
            TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("ApexManagerTiming", 262144L);
            synchronized (this.mLock) {
                if (this.mActiveApexInfosCache == null) {
                    timingsTraceAndSlog.traceBegin("getActiveApexInfos_noCache");
                    try {
                        this.mActiveApexInfosCache = new ArraySet();
                        for (ApexInfo apexInfo : waitForApexService().getActivePackages()) {
                            this.mActiveApexInfosCache.add(new ActiveApexInfo(apexInfo));
                        }
                    } catch (RemoteException e) {
                        Slog.e("ApexManager", "Unable to retrieve packages from apexservice", e);
                    }
                    timingsTraceAndSlog.traceEnd();
                }
                if (this.mActiveApexInfosCache != null) {
                    return new ArrayList(this.mActiveApexInfosCache);
                }
                return Collections.emptyList();
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getActiveApexPackageNameContainingPackage(String str) {
            Objects.requireNonNull(str);
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                int size = this.mApksInApex.size();
                for (int i = 0; i < size; i++) {
                    if (this.mApksInApex.valueAt(i).contains(str)) {
                        String keyAt = this.mApksInApex.keyAt(i);
                        int size2 = this.mPackageNameToApexModuleName.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            if (this.mPackageNameToApexModuleName.valueAt(i2).equals(keyAt)) {
                                return this.mPackageNameToApexModuleName.keyAt(i2);
                            }
                        }
                        continue;
                    }
                }
                return null;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexSessionInfo getStagedSessionInfo(int i) {
            try {
                ApexSessionInfo stagedSessionInfo = waitForApexService().getStagedSessionInfo(i);
                if (stagedSessionInfo.isUnknown) {
                    return null;
                }
                return stagedSessionInfo;
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                throw new RuntimeException(e);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public SparseArray<ApexSessionInfo> getSessions() {
            try {
                ApexSessionInfo[] sessions = waitForApexService().getSessions();
                SparseArray<ApexSessionInfo> sparseArray = new SparseArray<>(sessions.length);
                for (ApexSessionInfo apexSessionInfo : sessions) {
                    sparseArray.put(apexSessionInfo.sessionId, apexSessionInfo);
                }
                return sparseArray;
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                throw new RuntimeException(e);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfoList submitStagedSession(ApexSessionParams apexSessionParams) throws PackageManagerException {
            try {
                ApexInfoList apexInfoList = new ApexInfoList();
                waitForApexService().submitStagedSession(apexSessionParams, apexInfoList);
                return apexInfoList;
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                throw new RuntimeException(e);
            } catch (Exception e2) {
                throw new PackageManagerException(-22, "apexd verification failed : " + e2.getMessage());
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo[] getStagedApexInfos(ApexSessionParams apexSessionParams) {
            try {
                return waitForApexService().getStagedApexInfos(apexSessionParams);
            } catch (RemoteException e) {
                Slog.w("ApexManager", "Unable to contact apexservice" + e.getMessage());
                throw new RuntimeException(e);
            } catch (Exception e2) {
                Slog.w("ApexManager", "Failed to collect staged apex infos" + e2.getMessage());
                return new ApexInfo[0];
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markStagedSessionReady(int i) throws PackageManagerException {
            try {
                waitForApexService().markStagedSessionReady(i);
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                throw new RuntimeException(e);
            } catch (Exception e2) {
                throw new PackageManagerException(-22, "Failed to mark apexd session as ready : " + e2.getMessage());
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markStagedSessionSuccessful(int i) {
            try {
                waitForApexService().markStagedSessionSuccessful(i);
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                throw new RuntimeException(e);
            } catch (Exception e2) {
                Slog.e("ApexManager", "Failed to mark session " + i + " as successful", e2);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean revertActiveSessions() {
            try {
                waitForApexService().revertActiveSessions();
                return true;
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
                return false;
            } catch (Exception e2) {
                Slog.e("ApexManager", e2.getMessage(), e2);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean abortStagedSession(int i) {
            try {
                waitForApexService().abortStagedSession(i);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean uninstallApex(String str) {
            try {
                waitForApexService().unstagePackages(Collections.singletonList(str));
                return true;
            } catch (Exception unused) {
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void registerApkInApex(AndroidPackage androidPackage) {
            synchronized (this.mLock) {
                for (ActiveApexInfo activeApexInfo : this.mActiveApexInfosCache) {
                    String baseApkPath = androidPackage.getBaseApkPath();
                    if (baseApkPath.startsWith(activeApexInfo.apexDirectory.getAbsolutePath() + File.separator)) {
                        List<String> list = this.mApksInApex.get(activeApexInfo.apexModuleName);
                        if (list == null) {
                            list = Lists.newArrayList();
                            this.mApksInApex.put(activeApexInfo.apexModuleName, list);
                        }
                        Slog.i("ApexManager", "Registering " + androidPackage.getPackageName() + " as apk-in-apex of " + activeApexInfo.apexModuleName);
                        list.add(androidPackage.getPackageName());
                    }
                }
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void reportErrorWithApkInApex(String str, String str2) {
            synchronized (this.mLock) {
                for (ActiveApexInfo activeApexInfo : this.mActiveApexInfosCache) {
                    if (str.startsWith(activeApexInfo.apexDirectory.getAbsolutePath())) {
                        this.mErrorWithApkInApex.put(activeApexInfo.apexModuleName, str2);
                    }
                }
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getApkInApexInstallError(String str) {
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                String str2 = this.mPackageNameToApexModuleName.get(str);
                if (str2 == null) {
                    return null;
                }
                return this.mErrorWithApkInApex.get(str2);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<String> getApksInApex(String str) {
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                String str2 = this.mPackageNameToApexModuleName.get(str);
                if (str2 == null) {
                    return Collections.emptyList();
                }
                return this.mApksInApex.getOrDefault(str2, Collections.emptyList());
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getApexModuleNameForPackageName(String str) {
            String str2;
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                str2 = this.mPackageNameToApexModuleName.get(str);
            }
            return str2;
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getActivePackageNameForApexModuleName(String str) {
            String str2;
            synchronized (this.mLock) {
                Preconditions.checkState(this.mApexModuleNameToActivePackageName != null, "APEX packages have not been scanned");
                str2 = this.mApexModuleNameToActivePackageName.get(str);
            }
            return str2;
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean snapshotCeData(int i, int i2, String str) {
            String str2;
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                str2 = this.mPackageNameToApexModuleName.get(str);
            }
            if (str2 == null) {
                Slog.e("ApexManager", "Invalid apex package name: " + str);
                return false;
            }
            try {
                waitForApexService().snapshotCeData(i, i2, str2);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean restoreCeData(int i, int i2, String str) {
            String str2;
            synchronized (this.mLock) {
                Preconditions.checkState(this.mPackageNameToApexModuleName != null, "APEX packages have not been scanned");
                str2 = this.mPackageNameToApexModuleName.get(str);
            }
            if (str2 == null) {
                Slog.e("ApexManager", "Invalid apex package name: " + str);
                return false;
            }
            try {
                waitForApexService().restoreCeData(i, i2, str2);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyDeSnapshots(int i) {
            try {
                waitForApexService().destroyDeSnapshots(i);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyCeSnapshots(int i, int i2) {
            try {
                waitForApexService().destroyCeSnapshots(i, i2);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyCeSnapshotsNotSpecified(int i, int[] iArr) {
            try {
                waitForApexService().destroyCeSnapshotsNotSpecified(i, iArr);
                return true;
            } catch (Exception e) {
                Slog.e("ApexManager", e.getMessage(), e);
                return false;
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markBootCompleted() {
            try {
                waitForApexService().markBootCompleted();
            } catch (RemoteException e) {
                Slog.e("ApexManager", "Unable to contact apexservice", e);
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public void reserveSpaceForCompressedApex(CompressedApexInfoList compressedApexInfoList) throws RemoteException {
            waitForApexService().reserveSpaceForCompressedApex(compressedApexInfoList);
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo installPackage(File file) throws PackageManagerException {
            try {
                return waitForApexService().installAndActivatePackage(file.getAbsolutePath());
            } catch (RemoteException unused) {
                throw new PackageManagerException(-110, "apexservice not available");
            } catch (Exception e) {
                throw new PackageManagerException(-110, e.getMessage());
            }
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<ApexSystemServiceInfo> getApexSystemServices() {
            List<ApexSystemServiceInfo> list;
            synchronized (this.mLock) {
                Preconditions.checkState(this.mApexSystemServices != null, "APEX packages have not been scanned");
                list = this.mApexSystemServices;
            }
            return list;
        }

        @Override // com.android.server.p011pm.ApexManager
        public File getBackingApexFile(File file) {
            Path path = file.toPath();
            if (path.startsWith(Environment.getApexDirectory().toPath()) && path.getNameCount() >= 2) {
                String path2 = file.toPath().getName(1).toString();
                List<ActiveApexInfo> activeApexInfos = getActiveApexInfos();
                for (int i = 0; i < activeApexInfos.size(); i++) {
                    if (activeApexInfos.get(i).apexModuleName.equals(path2)) {
                        return activeApexInfos.get(i).apexFile;
                    }
                }
                return null;
            }
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public void dump(PrintWriter printWriter) {
            ApexSessionInfo[] sessions;
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ", 120);
            try {
                indentingPrintWriter.println();
                indentingPrintWriter.println("APEX session state:");
                indentingPrintWriter.increaseIndent();
                for (ApexSessionInfo apexSessionInfo : waitForApexService().getSessions()) {
                    indentingPrintWriter.println("Session ID: " + apexSessionInfo.sessionId);
                    indentingPrintWriter.increaseIndent();
                    if (apexSessionInfo.isUnknown) {
                        indentingPrintWriter.println("State: UNKNOWN");
                    } else if (apexSessionInfo.isVerified) {
                        indentingPrintWriter.println("State: VERIFIED");
                    } else if (apexSessionInfo.isStaged) {
                        indentingPrintWriter.println("State: STAGED");
                    } else if (apexSessionInfo.isActivated) {
                        indentingPrintWriter.println("State: ACTIVATED");
                    } else if (apexSessionInfo.isActivationFailed) {
                        indentingPrintWriter.println("State: ACTIVATION FAILED");
                    } else if (apexSessionInfo.isSuccess) {
                        indentingPrintWriter.println("State: SUCCESS");
                    } else if (apexSessionInfo.isRevertInProgress) {
                        indentingPrintWriter.println("State: REVERT IN PROGRESS");
                    } else if (apexSessionInfo.isReverted) {
                        indentingPrintWriter.println("State: REVERTED");
                    } else if (apexSessionInfo.isRevertFailed) {
                        indentingPrintWriter.println("State: REVERT FAILED");
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            } catch (RemoteException unused) {
                indentingPrintWriter.println("Couldn't communicate with apexd.");
            }
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.ApexManager$ApexManagerFlattenedApex */
    /* loaded from: classes2.dex */
    public static final class ApexManagerFlattenedApex extends ApexManager {
        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyCeSnapshots(int i, int i2) {
            return true;
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyCeSnapshotsNotSpecified(int i, int[] iArr) {
            return true;
        }

        @Override // com.android.server.p011pm.ApexManager
        public void dump(PrintWriter printWriter) {
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getActivePackageNameForApexModuleName(String str) {
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo[] getAllApexInfos() {
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getApexModuleNameForPackageName(String str) {
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getApkInApexInstallError(String str) {
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public File getBackingApexFile(File file) {
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean isApexSupported() {
            return false;
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markBootCompleted() {
        }

        @Override // com.android.server.p011pm.ApexManager
        public void notifyScanResult(List<ScanResult> list) {
        }

        @Override // com.android.server.p011pm.ApexManager
        public void registerApkInApex(AndroidPackage androidPackage) {
        }

        @Override // com.android.server.p011pm.ApexManager
        public void reportErrorWithApkInApex(String str, String str2) {
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<ActiveApexInfo> getActiveApexInfos() {
            File[] listFiles;
            ArrayList arrayList = new ArrayList();
            File apexDirectory = Environment.getApexDirectory();
            if (apexDirectory.isDirectory() && (listFiles = apexDirectory.listFiles()) != null) {
                for (File file : listFiles) {
                    if (file.isDirectory() && !file.getName().contains("@") && !file.getName().equals("com.android.art.debug")) {
                        arrayList.add(new ActiveApexInfo(file, Environment.getRootDirectory(), file));
                    }
                }
            }
            return arrayList;
        }

        @Override // com.android.server.p011pm.ApexManager
        public String getActiveApexPackageNameContainingPackage(String str) {
            Objects.requireNonNull(str);
            return null;
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexSessionInfo getStagedSessionInfo(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public SparseArray<ApexSessionInfo> getSessions() {
            return new SparseArray<>(0);
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfoList submitStagedSession(ApexSessionParams apexSessionParams) throws PackageManagerException {
            throw new PackageManagerException(-110, "Device doesn't support updating APEX");
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo[] getStagedApexInfos(ApexSessionParams apexSessionParams) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markStagedSessionReady(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public void markStagedSessionSuccessful(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean revertActiveSessions() {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean abortStagedSession(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean uninstallApex(String str) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<String> getApksInApex(String str) {
            return Collections.emptyList();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean snapshotCeData(int i, int i2, String str) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean restoreCeData(int i, int i2, String str) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public boolean destroyDeSnapshots(int i) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public void reserveSpaceForCompressedApex(CompressedApexInfoList compressedApexInfoList) {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.server.p011pm.ApexManager
        public ApexInfo installPackage(File file) {
            throw new UnsupportedOperationException("APEX updates are not supported");
        }

        @Override // com.android.server.p011pm.ApexManager
        public List<ApexSystemServiceInfo> getApexSystemServices() {
            return Collections.emptyList();
        }
    }
}
