package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.admin.SecurityLog;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.os.UserHandle;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
/* renamed from: com.android.server.pm.PackageMetrics */
/* loaded from: classes2.dex */
public final class PackageMetrics {
    public final InstallRequest mInstallRequest;
    public final SparseArray<InstallStep> mInstallSteps = new SparseArray<>();
    public final long mInstallStartTimestampMillis = System.currentTimeMillis();

    public PackageMetrics(InstallRequest installRequest) {
        this.mInstallRequest = installRequest;
    }

    public void onInstallSucceed() {
        reportInstallationToSecurityLog(this.mInstallRequest.getUserId());
        reportInstallationStats(true);
    }

    public void onInstallFailed() {
        reportInstallationStats(false);
    }

    public final void reportInstallationStats(boolean z) {
        long j;
        long j2;
        PackageSetting scannedPackageSetting;
        UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        if (userManagerInternal == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis() - this.mInstallStartTimestampMillis;
        Pair<int[], long[]> installStepDurations = getInstallStepDurations();
        int[] newUsers = this.mInstallRequest.getNewUsers();
        int[] originUsers = this.mInstallRequest.getOriginUsers();
        String name = (z || this.mInstallRequest.isInstallFromAdb()) ? null : this.mInstallRequest.getName();
        int installerPackageUid = this.mInstallRequest.getInstallerPackageUid();
        if (!z || (scannedPackageSetting = this.mInstallRequest.getScannedPackageSetting()) == null) {
            j = 0;
            j2 = 0;
        } else {
            j2 = scannedPackageSetting.getVersionCode();
            j = getApksSize(scannedPackageSetting.getPath());
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.PACKAGE_INSTALLATION_SESSION_REPORTED, this.mInstallRequest.getSessionId(), name, getUid(this.mInstallRequest.getAppId(), this.mInstallRequest.getUserId()), newUsers, userManagerInternal.getUserTypesForStatsd(newUsers), originUsers, userManagerInternal.getUserTypesForStatsd(originUsers), this.mInstallRequest.getReturnCode(), this.mInstallRequest.getInternalErrorCode(), j, j2, (int[]) installStepDurations.first, (long[]) installStepDurations.second, currentTimeMillis, this.mInstallRequest.getInstallFlags(), installerPackageUid, -1, this.mInstallRequest.getDataLoaderType(), this.mInstallRequest.getRequireUserAction(), this.mInstallRequest.isInstantInstall(), this.mInstallRequest.isInstallReplace(), this.mInstallRequest.isInstallSystem(), this.mInstallRequest.isInstallInherit(), this.mInstallRequest.isInstallForUsers(), this.mInstallRequest.isInstallMove(), false);
    }

    public static int getUid(int i, int i2) {
        if (i2 == -1) {
            i2 = ActivityManager.getCurrentUser();
        }
        return UserHandle.getUid(i2, i);
    }

    public final long getApksSize(File file) {
        final AtomicLong atomicLong = new AtomicLong();
        try {
            Stream<Path> walk = Files.walk(file.toPath(), new FileVisitOption[0]);
            walk.filter(new Predicate() { // from class: com.android.server.pm.PackageMetrics$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getApksSize$0;
                    lambda$getApksSize$0 = PackageMetrics.lambda$getApksSize$0((Path) obj);
                    return lambda$getApksSize$0;
                }
            }).forEach(new Consumer() { // from class: com.android.server.pm.PackageMetrics$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PackageMetrics.lambda$getApksSize$1(atomicLong, (Path) obj);
                }
            });
            walk.close();
        } catch (IOException unused) {
        }
        return atomicLong.get();
    }

    public static /* synthetic */ boolean lambda$getApksSize$0(Path path) {
        return path.toFile().isFile() && ApkLiteParseUtils.isApkFile(path.toFile());
    }

    public static /* synthetic */ void lambda$getApksSize$1(AtomicLong atomicLong, Path path) {
        atomicLong.addAndGet(path.toFile().length());
    }

    public void onStepStarted(int i) {
        this.mInstallSteps.put(i, new InstallStep());
    }

    public void onStepFinished(int i) {
        InstallStep installStep = this.mInstallSteps.get(i);
        if (installStep != null) {
            installStep.finish();
        }
    }

    public void onStepFinished(int i, long j) {
        this.mInstallSteps.put(i, new InstallStep(j));
    }

    public final Pair<int[], long[]> getInstallStepDurations() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < this.mInstallSteps.size(); i++) {
            if (this.mInstallSteps.valueAt(i).getDurationMillis() >= 0) {
                arrayList.add(Integer.valueOf(this.mInstallSteps.keyAt(i)));
                arrayList2.add(Long.valueOf(this.mInstallSteps.valueAt(i).getDurationMillis()));
            }
        }
        int size = arrayList.size();
        int[] iArr = new int[size];
        long[] jArr = new long[arrayList2.size()];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
            jArr[i2] = ((Long) arrayList2.get(i2)).longValue();
        }
        return new Pair<>(iArr, jArr);
    }

    /* renamed from: com.android.server.pm.PackageMetrics$InstallStep */
    /* loaded from: classes2.dex */
    public static class InstallStep {
        public long mDurationMillis;
        public final long mStartTimestampMillis;

        public InstallStep() {
            this.mDurationMillis = -1L;
            this.mStartTimestampMillis = System.currentTimeMillis();
        }

        public InstallStep(long j) {
            this.mStartTimestampMillis = -1L;
            this.mDurationMillis = j;
        }

        public void finish() {
            this.mDurationMillis = System.currentTimeMillis() - this.mStartTimestampMillis;
        }

        public long getDurationMillis() {
            return this.mDurationMillis;
        }
    }

    public static void onUninstallSucceeded(PackageRemovedInfo packageRemovedInfo, int i, int i2) {
        UserManagerInternal userManagerInternal;
        if (packageRemovedInfo.mIsUpdate || (userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class)) == null) {
            return;
        }
        int[] iArr = packageRemovedInfo.mRemovedUsers;
        int[] userTypesForStatsd = userManagerInternal.getUserTypesForStatsd(iArr);
        int[] iArr2 = packageRemovedInfo.mOrigUsers;
        FrameworkStatsLog.write((int) FrameworkStatsLog.PACKAGE_UNINSTALLATION_REPORTED, getUid(packageRemovedInfo.mUid, i2), iArr, userTypesForStatsd, iArr2, userManagerInternal.getUserTypesForStatsd(iArr2), i, 1, packageRemovedInfo.mIsRemovedPackageSystemUpdate, !packageRemovedInfo.mRemovedForAllUsers);
        reportUninstallationToSecurityLog(packageRemovedInfo.mRemovedPackage, packageRemovedInfo.mRemovedPackageVersionCode, i2);
    }

    public static void onVerificationFailed(VerifyingSession verifyingSession) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.PACKAGE_INSTALLATION_SESSION_REPORTED, verifyingSession.getSessionId(), (String) null, -1, (int[]) null, (int[]) null, (int[]) null, (int[]) null, verifyingSession.getRet(), 0, 0L, 0L, (int[]) null, (long[]) null, 0L, 0, verifyingSession.getInstallerPackageUid(), -1, verifyingSession.getDataLoaderType(), verifyingSession.getUserActionRequiredType(), verifyingSession.isInstant(), false, false, verifyingSession.isInherit(), false, false, verifyingSession.isStaged());
    }

    public final void reportInstallationToSecurityLog(int i) {
        PackageSetting scannedPackageSetting;
        if (SecurityLog.isLoggingEnabled() && (scannedPackageSetting = this.mInstallRequest.getScannedPackageSetting()) != null) {
            String packageName = scannedPackageSetting.getPackageName();
            long versionCode = scannedPackageSetting.getVersionCode();
            if (!this.mInstallRequest.isInstallReplace()) {
                SecurityLog.writeEvent(210041, new Object[]{packageName, Long.valueOf(versionCode), Integer.valueOf(i)});
            } else {
                SecurityLog.writeEvent(210042, new Object[]{packageName, Long.valueOf(versionCode), Integer.valueOf(i)});
            }
        }
    }

    public static void reportUninstallationToSecurityLog(String str, long j, int i) {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210043, new Object[]{str, Long.valueOf(j), Integer.valueOf(i)});
        }
    }
}
