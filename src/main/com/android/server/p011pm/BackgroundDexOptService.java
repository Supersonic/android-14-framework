package com.android.server.p011pm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.BatteryManagerInternal;
import android.os.Binder;
import android.os.Environment;
import android.os.IThermalService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.PinnerService;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.dex.ArtStatsLogUtils;
import com.android.server.p011pm.dex.DexManager;
import com.android.server.p011pm.dex.DexoptOptions;
import com.android.server.utils.TimingsTraceAndSlog;
import dalvik.system.DexFile;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/* renamed from: com.android.server.pm.BackgroundDexOptService */
/* loaded from: classes2.dex */
public final class BackgroundDexOptService {
    @VisibleForTesting
    static final int JOB_IDLE_OPTIMIZE = 800;
    @VisibleForTesting
    static final int JOB_POST_BOOT_UPDATE = 801;
    @GuardedBy({"mLock"})
    public Thread mDexOptCancellingThread;
    public final DexOptHelper mDexOptHelper;
    @GuardedBy({"mLock"})
    public Thread mDexOptThread;
    @GuardedBy({"mLock"})
    public boolean mDisableJobSchedulerJobs;
    public final long mDowngradeUnusedAppsThresholdInMillis;
    @GuardedBy({"mLock"})
    public final ArraySet<String> mFailedPackageNamesPrimary;
    @GuardedBy({"mLock"})
    public final ArraySet<String> mFailedPackageNamesSecondary;
    @GuardedBy({"mLock"})
    public boolean mFinishedPostBootUpdate;
    public final Injector mInjector;
    @GuardedBy({"mLock"})
    public final ArraySet<String> mLastCancelledPackages;
    @GuardedBy({"mLock"})
    public long mLastExecutionDurationMs;
    @GuardedBy({"mLock"})
    public long mLastExecutionStartUptimeMs;
    @GuardedBy({"mLock"})
    public int mLastExecutionStatus;
    public final Object mLock;
    public List<PackagesUpdatedListener> mPackagesUpdatedListeners;
    public final ArtStatsLogUtils.BackgroundDexoptJobStatsLogger mStatsLogger;
    public int mThermalStatusCutoff;
    public static final boolean DEBUG = Log.isLoggable("BackgroundDexOptService", 3);
    public static final long IDLE_OPTIMIZATION_PERIOD = TimeUnit.DAYS.toMillis(1);
    public static ComponentName sDexoptServiceName = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, BackgroundDexOptJobService.class.getName());

    /* renamed from: com.android.server.pm.BackgroundDexOptService$PackagesUpdatedListener */
    /* loaded from: classes2.dex */
    public interface PackagesUpdatedListener {
        void onPackagesUpdated(ArraySet<String> arraySet);
    }

    public BackgroundDexOptService(Context context, DexManager dexManager, PackageManagerService packageManagerService) throws Installer.LegacyDexoptDisabledException {
        this(new Injector(context, dexManager, packageManagerService));
    }

    @VisibleForTesting
    public BackgroundDexOptService(Injector injector) throws Installer.LegacyDexoptDisabledException {
        this.mStatsLogger = new ArtStatsLogUtils.BackgroundDexoptJobStatsLogger();
        this.mLock = new Object();
        this.mLastExecutionStatus = -1;
        this.mLastCancelledPackages = new ArraySet<>();
        this.mFailedPackageNamesPrimary = new ArraySet<>();
        this.mFailedPackageNamesSecondary = new ArraySet<>();
        this.mPackagesUpdatedListeners = new ArrayList();
        this.mThermalStatusCutoff = 2;
        Installer.checkLegacyDexoptDisabled();
        this.mInjector = injector;
        this.mDexOptHelper = injector.getDexOptHelper();
        LocalServices.addService(BackgroundDexOptService.class, this);
        this.mDowngradeUnusedAppsThresholdInMillis = injector.getDowngradeUnusedAppsThresholdInMillis();
    }

    public void systemReady() throws Installer.LegacyDexoptDisabledException {
        Installer.checkLegacyDexoptDisabled();
        if (this.mInjector.isBackgroundDexOptDisabled()) {
            return;
        }
        this.mInjector.getContext().registerReceiver(new BroadcastReceiver() { // from class: com.android.server.pm.BackgroundDexOptService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                BackgroundDexOptService.this.mInjector.getContext().unregisterReceiver(this);
                BackgroundDexOptService.this.scheduleAJob(BackgroundDexOptService.JOB_POST_BOOT_UPDATE);
                BackgroundDexOptService.this.scheduleAJob(BackgroundDexOptService.JOB_IDLE_OPTIMIZE);
                if (BackgroundDexOptService.DEBUG) {
                    Slog.d("BackgroundDexOptService", "BootBgDexopt scheduled");
                }
            }
        }, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        boolean isBackgroundDexOptDisabled = this.mInjector.isBackgroundDexOptDisabled();
        indentingPrintWriter.print("enabled:");
        indentingPrintWriter.println(!isBackgroundDexOptDisabled);
        if (isBackgroundDexOptDisabled) {
            return;
        }
        synchronized (this.mLock) {
            indentingPrintWriter.print("mDexOptThread:");
            indentingPrintWriter.println(this.mDexOptThread);
            indentingPrintWriter.print("mDexOptCancellingThread:");
            indentingPrintWriter.println(this.mDexOptCancellingThread);
            indentingPrintWriter.print("mFinishedPostBootUpdate:");
            indentingPrintWriter.println(this.mFinishedPostBootUpdate);
            indentingPrintWriter.print("mDisableJobSchedulerJobs:");
            indentingPrintWriter.println(this.mDisableJobSchedulerJobs);
            indentingPrintWriter.print("mLastExecutionStatus:");
            indentingPrintWriter.println(this.mLastExecutionStatus);
            indentingPrintWriter.print("mLastExecutionStartUptimeMs:");
            indentingPrintWriter.println(this.mLastExecutionStartUptimeMs);
            indentingPrintWriter.print("mLastExecutionDurationMs:");
            indentingPrintWriter.println(this.mLastExecutionDurationMs);
            indentingPrintWriter.print("now:");
            indentingPrintWriter.println(SystemClock.elapsedRealtime());
            indentingPrintWriter.print("mLastCancelledPackages:");
            indentingPrintWriter.println(String.join(",", this.mLastCancelledPackages));
            indentingPrintWriter.print("mFailedPackageNamesPrimary:");
            indentingPrintWriter.println(String.join(",", this.mFailedPackageNamesPrimary));
            indentingPrintWriter.print("mFailedPackageNamesSecondary:");
            indentingPrintWriter.println(String.join(",", this.mFailedPackageNamesSecondary));
        }
    }

    public static BackgroundDexOptService getService() {
        return (BackgroundDexOptService) LocalServices.getService(BackgroundDexOptService.class);
    }

    public boolean runBackgroundDexoptJob(List<String> list) throws Installer.LegacyDexoptDisabledException {
        enforceRootOrShell();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                waitForDexOptThreadToFinishLocked();
                resetStatesForNewDexOptRunLocked(Thread.currentThread());
            }
            PackageManagerService packageManagerService = this.mInjector.getPackageManagerService();
            if (list == null) {
                list = this.mDexOptHelper.getOptimizablePackages(packageManagerService.snapshotComputer());
            }
            return runIdleOptimization(packageManagerService, list, false);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            markDexOptCompleted();
        }
    }

    public void cancelBackgroundDexoptJob() throws Installer.LegacyDexoptDisabledException {
        Installer.checkLegacyDexoptDisabled();
        enforceRootOrShell();
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.BackgroundDexOptService$$ExternalSyntheticLambda0
            public final void runOrThrow() {
                BackgroundDexOptService.this.lambda$cancelBackgroundDexoptJob$0();
            }
        });
    }

    public void setDisableJobSchedulerJobs(boolean z) throws Installer.LegacyDexoptDisabledException {
        Installer.checkLegacyDexoptDisabled();
        enforceRootOrShell();
        synchronized (this.mLock) {
            this.mDisableJobSchedulerJobs = z;
        }
    }

    public void notifyPackageChanged(String str) throws Installer.LegacyDexoptDisabledException {
        Installer.checkLegacyDexoptDisabled();
        synchronized (this.mLock) {
            this.mFailedPackageNamesPrimary.remove(str);
            this.mFailedPackageNamesSecondary.remove(str);
        }
    }

    public boolean onStartJob(final BackgroundDexOptJobService backgroundDexOptJobService, final JobParameters jobParameters) {
        Slog.i("BackgroundDexOptService", "onStartJob:" + jobParameters.getJobId());
        boolean z = jobParameters.getJobId() == JOB_POST_BOOT_UPDATE;
        final PackageManagerService packageManagerService = this.mInjector.getPackageManagerService();
        if (packageManagerService.isStorageLow()) {
            Slog.w("BackgroundDexOptService", "Low storage, skipping this run");
            markPostBootUpdateCompleted(jobParameters);
            return false;
        }
        final List<String> optimizablePackages = this.mDexOptHelper.getOptimizablePackages(packageManagerService.snapshotComputer());
        if (optimizablePackages.isEmpty()) {
            Slog.i("BackgroundDexOptService", "No packages to optimize");
            markPostBootUpdateCompleted(jobParameters);
            return false;
        }
        this.mThermalStatusCutoff = this.mInjector.getDexOptThermalCutoff();
        synchronized (this.mLock) {
            if (this.mDisableJobSchedulerJobs) {
                Slog.i("BackgroundDexOptService", "JobScheduler invocations disabled");
                return false;
            }
            Thread thread = this.mDexOptThread;
            if (thread == null || !thread.isAlive()) {
                if (z || this.mFinishedPostBootUpdate) {
                    try {
                        Injector injector = this.mInjector;
                        StringBuilder sb = new StringBuilder();
                        sb.append("BackgroundDexOptService_");
                        sb.append(z ? "PostBoot" : "Idle");
                        resetStatesForNewDexOptRunLocked(injector.createAndStartThread(sb.toString(), new Runnable() { // from class: com.android.server.pm.BackgroundDexOptService$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                BackgroundDexOptService.this.lambda$onStartJob$1(packageManagerService, optimizablePackages, jobParameters, backgroundDexOptJobService);
                            }
                        }));
                    } catch (Installer.LegacyDexoptDisabledException e) {
                        Slog.wtf("BackgroundDexOptService", e);
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00b7  */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5, types: [int] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$onStartJob$1(PackageManagerService packageManagerService, List list, JobParameters jobParameters, BackgroundDexOptJobService backgroundDexOptJobService) {
        boolean z;
        String str;
        ?? r1 = "dexopt finishing. jobid:";
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("BackgroundDexOptService", 16384L);
        timingsTraceAndSlog.traceBegin("jobExecution");
        boolean z2 = true;
        try {
            try {
                boolean runIdleOptimization = runIdleOptimization(packageManagerService, list, jobParameters.getJobId() == JOB_POST_BOOT_UPDATE);
                timingsTraceAndSlog.traceEnd();
                StringBuilder sb = new StringBuilder();
                sb.append("dexopt finishing. jobid:");
                r1 = jobParameters.getJobId();
                sb.append((int) r1);
                sb.append(" completed:");
                sb.append(runIdleOptimization);
                Slog.i("BackgroundDexOptService", sb.toString());
                writeStatsLog(jobParameters);
                if (jobParameters.getJobId() == JOB_POST_BOOT_UPDATE && runIdleOptimization) {
                    markPostBootUpdateCompleted(jobParameters);
                }
                if (runIdleOptimization) {
                    z2 = false;
                }
            } catch (Throwable th) {
                th = th;
                z = false;
                str = r1;
                timingsTraceAndSlog.traceEnd();
                Slog.i("BackgroundDexOptService", str + jobParameters.getJobId() + " completed:false");
                writeStatsLog(jobParameters);
                jobParameters.getJobId();
                if (z) {
                    z2 = false;
                }
                backgroundDexOptJobService.jobFinished(jobParameters, z2);
                markDexOptCompleted();
                throw th;
            }
        } catch (Installer.LegacyDexoptDisabledException e) {
            Slog.wtf("BackgroundDexOptService", e);
            timingsTraceAndSlog.traceEnd();
            Slog.i("BackgroundDexOptService", "dexopt finishing. jobid:" + jobParameters.getJobId() + " completed:false");
            writeStatsLog(jobParameters);
            jobParameters.getJobId();
        } catch (RuntimeException e2) {
            try {
                throw e2;
            } catch (Throwable th2) {
                th = th2;
                z = true;
                str = r1;
                timingsTraceAndSlog.traceEnd();
                Slog.i("BackgroundDexOptService", str + jobParameters.getJobId() + " completed:false");
                writeStatsLog(jobParameters);
                jobParameters.getJobId();
                if (z) {
                }
                backgroundDexOptJobService.jobFinished(jobParameters, z2);
                markDexOptCompleted();
                throw th;
            }
        }
        backgroundDexOptJobService.jobFinished(jobParameters, z2);
        markDexOptCompleted();
    }

    public boolean onStopJob(BackgroundDexOptJobService backgroundDexOptJobService, JobParameters jobParameters) {
        Slog.i("BackgroundDexOptService", "onStopJob:" + jobParameters.getJobId());
        this.mInjector.createAndStartThread("DexOptCancel", new Runnable() { // from class: com.android.server.pm.BackgroundDexOptService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BackgroundDexOptService.this.lambda$onStopJob$2();
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStopJob$2() {
        try {
            lambda$cancelBackgroundDexoptJob$0();
        } catch (Installer.LegacyDexoptDisabledException e) {
            Slog.wtf("BackgroundDexOptService", e);
        }
    }

    /* renamed from: cancelDexOptAndWaitForCompletion */
    public final void lambda$cancelBackgroundDexoptJob$0() throws Installer.LegacyDexoptDisabledException {
        synchronized (this.mLock) {
            if (this.mDexOptThread == null) {
                return;
            }
            Thread thread = this.mDexOptCancellingThread;
            if (thread != null && thread.isAlive()) {
                waitForDexOptThreadToFinishLocked();
                return;
            }
            this.mDexOptCancellingThread = Thread.currentThread();
            controlDexOptBlockingLocked(true);
            waitForDexOptThreadToFinishLocked();
            this.mDexOptCancellingThread = null;
            this.mDexOptThread = null;
            controlDexOptBlockingLocked(false);
            this.mLock.notifyAll();
        }
    }

    @GuardedBy({"mLock"})
    public final void waitForDexOptThreadToFinishLocked() {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("BackgroundDexOptService", 262144L);
        timingsTraceAndSlog.traceBegin("waitForDexOptThreadToFinishLocked");
        while (true) {
            try {
                Thread thread = this.mDexOptThread;
                if (thread == null || !thread.isAlive()) {
                    break;
                }
                this.mLock.wait(200L);
            } catch (InterruptedException unused) {
                Slog.w("BackgroundDexOptService", "Interrupted while waiting for dexopt thread");
                Thread.currentThread().interrupt();
            }
        }
        timingsTraceAndSlog.traceEnd();
    }

    public final void markDexOptCompleted() {
        synchronized (this.mLock) {
            if (this.mDexOptThread != Thread.currentThread()) {
                throw new IllegalStateException("Only mDexOptThread can mark completion, mDexOptThread:" + this.mDexOptThread + " current:" + Thread.currentThread());
            }
            this.mDexOptThread = null;
            this.mLock.notifyAll();
        }
    }

    @GuardedBy({"mLock"})
    public final void resetStatesForNewDexOptRunLocked(Thread thread) throws Installer.LegacyDexoptDisabledException {
        this.mDexOptThread = thread;
        this.mLastCancelledPackages.clear();
        controlDexOptBlockingLocked(false);
    }

    public final void enforceRootOrShell() {
        int callingUid = this.mInjector.getCallingUid();
        if (callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("Should be shell or root user");
        }
    }

    @GuardedBy({"mLock"})
    public final void controlDexOptBlockingLocked(boolean z) throws Installer.LegacyDexoptDisabledException {
        this.mInjector.getPackageManagerService();
        this.mDexOptHelper.controlDexOptBlocking(z);
    }

    public final void scheduleAJob(int i) {
        JobScheduler jobScheduler = this.mInjector.getJobScheduler();
        JobInfo.Builder requiresDeviceIdle = new JobInfo.Builder(i, sDexoptServiceName).setRequiresDeviceIdle(true);
        if (i == JOB_IDLE_OPTIMIZE) {
            requiresDeviceIdle.setRequiresCharging(true).setPeriodic(IDLE_OPTIMIZATION_PERIOD);
        }
        jobScheduler.schedule(requiresDeviceIdle.build());
    }

    public final long getLowStorageThreshold() {
        long dataDirStorageLowBytes = this.mInjector.getDataDirStorageLowBytes();
        if (dataDirStorageLowBytes == 0) {
            Slog.e("BackgroundDexOptService", "Invalid low storage threshold");
        }
        return dataDirStorageLowBytes;
    }

    public final void logStatus(int i) {
        if (i == 0) {
            Slog.i("BackgroundDexOptService", "Idle optimizations completed.");
        } else if (i == 1) {
            Slog.w("BackgroundDexOptService", "Idle optimizations aborted by cancellation.");
        } else if (i == 2) {
            Slog.w("BackgroundDexOptService", "Idle optimizations aborted because of space constraints.");
        } else if (i == 3) {
            Slog.w("BackgroundDexOptService", "Idle optimizations aborted by thermal throttling.");
        } else if (i == 4) {
            Slog.w("BackgroundDexOptService", "Idle optimizations aborted by low battery.");
        } else if (i == 5) {
            Slog.w("BackgroundDexOptService", "Idle optimizations failed from dexopt.");
        } else {
            Slog.w("BackgroundDexOptService", "Idle optimizations ended with unexpected code: " + i);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x0045 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean runIdleOptimization(PackageManagerService packageManagerService, List<String> list, boolean z) throws Installer.LegacyDexoptDisabledException {
        int i;
        synchronized (this.mLock) {
            i = -1;
            this.mLastExecutionStatus = -1;
            this.mLastExecutionStartUptimeMs = SystemClock.uptimeMillis();
            this.mLastExecutionDurationMs = -1L;
        }
        try {
            i = idleOptimizePackages(packageManagerService, list, getLowStorageThreshold(), z);
            logStatus(i);
            boolean z2 = i == 0 || i == 5;
            synchronized (this.mLock) {
                this.mLastExecutionStatus = i;
                this.mLastExecutionDurationMs = SystemClock.uptimeMillis() - this.mLastExecutionStartUptimeMs;
            }
            return z2;
        } catch (RuntimeException e) {
            try {
                throw e;
            } catch (Throwable th) {
                th = th;
                i = 6;
                synchronized (this.mLock) {
                    this.mLastExecutionStatus = i;
                    this.mLastExecutionDurationMs = SystemClock.uptimeMillis() - this.mLastExecutionStartUptimeMs;
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            synchronized (this.mLock) {
            }
        }
    }

    public final long getDirectorySize(File file) {
        if (file.isDirectory()) {
            long j = 0;
            for (File file2 : file.listFiles()) {
                j += getDirectorySize(file2);
            }
            return j;
        }
        return file.length();
    }

    public final long getPackageSize(Computer computer, String str) {
        ApplicationInfo applicationInfo;
        long j = 0;
        PackageInfo packageInfo = computer.getPackageInfo(str, 0L, 0);
        if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null) {
            File file = Paths.get(applicationInfo.sourceDir, new String[0]).toFile();
            if (file.isFile()) {
                file = file.getParentFile();
            }
            j = 0 + getDirectorySize(file);
            if (!ArrayUtils.isEmpty(packageInfo.applicationInfo.splitSourceDirs)) {
                for (String str2 : packageInfo.applicationInfo.splitSourceDirs) {
                    File file2 = Paths.get(str2, new String[0]).toFile();
                    if (file2.isFile()) {
                        file2 = file2.getParentFile();
                    }
                    if (!file.getAbsolutePath().equals(file2.getAbsolutePath())) {
                        j += getDirectorySize(file2);
                    }
                }
            }
        }
        return j;
    }

    public final int idleOptimizePackages(PackageManagerService packageManagerService, List<String> list, long j, boolean z) throws Installer.LegacyDexoptDisabledException {
        ArrayList arrayList;
        int abortIdleOptimizations;
        ArraySet<String> arraySet = new ArraySet<>();
        try {
            boolean supportSecondaryDex = this.mInjector.supportSecondaryDex();
            if (!supportSecondaryDex || (abortIdleOptimizations = reconcileSecondaryDexFiles()) == 0) {
                boolean shouldDowngrade = shouldDowngrade(2 * j);
                boolean z2 = DEBUG;
                if (z2) {
                    Slog.d("BackgroundDexOptService", "Should Downgrade " + shouldDowngrade);
                }
                if (shouldDowngrade) {
                    Computer snapshotComputer = packageManagerService.snapshotComputer();
                    Set<String> unusedPackages = snapshotComputer.getUnusedPackages(this.mDowngradeUnusedAppsThresholdInMillis);
                    if (z2) {
                        Slog.d("BackgroundDexOptService", "Unsused Packages " + String.join(",", unusedPackages));
                    }
                    if (!unusedPackages.isEmpty()) {
                        for (String str : unusedPackages) {
                            abortIdleOptimizations = abortIdleOptimizations(-1L);
                            if (abortIdleOptimizations == 0) {
                                int downgradePackage = downgradePackage(snapshotComputer, packageManagerService, str, true, z);
                                if (downgradePackage == 1) {
                                    arraySet.add(str);
                                }
                                abortIdleOptimizations = convertPackageDexOptimizerStatusToInternal(downgradePackage);
                                if (abortIdleOptimizations == 0 && (!supportSecondaryDex || (abortIdleOptimizations = convertPackageDexOptimizerStatusToInternal(downgradePackage(snapshotComputer, packageManagerService, str, false, z))) == 0)) {
                                }
                            }
                        }
                        ArrayList arrayList2 = new ArrayList(list);
                        arrayList2.removeAll(unusedPackages);
                        arrayList = arrayList2;
                        return optimizePackages(arrayList, j, arraySet, z);
                    }
                }
                arrayList = list;
                return optimizePackages(arrayList, j, arraySet, z);
            }
            return abortIdleOptimizations;
        } finally {
            notifyPinService(arraySet);
            notifyPackagesUpdated(arraySet);
        }
    }

    public final int optimizePackages(List<String> list, long j, ArraySet<String> arraySet, boolean z) throws Installer.LegacyDexoptDisabledException {
        boolean supportSecondaryDex = this.mInjector.supportSecondaryDex();
        int i = 0;
        for (String str : list) {
            int abortIdleOptimizations = abortIdleOptimizations(j);
            if (abortIdleOptimizations != 0) {
                return abortIdleOptimizations;
            }
            int optimizePackage = optimizePackage(str, true, z);
            if (optimizePackage == 2) {
                return 1;
            }
            if (optimizePackage == 1) {
                arraySet.add(str);
            } else if (optimizePackage == -1) {
                i = convertPackageDexOptimizerStatusToInternal(optimizePackage);
            }
            if (supportSecondaryDex) {
                int optimizePackage2 = optimizePackage(str, false, z);
                if (optimizePackage2 == 2) {
                    return 1;
                }
                if (optimizePackage2 == -1) {
                    i = convertPackageDexOptimizerStatusToInternal(optimizePackage2);
                }
            }
        }
        return i;
    }

    public final int downgradePackage(Computer computer, PackageManagerService packageManagerService, String str, boolean z, boolean z2) throws Installer.LegacyDexoptDisabledException {
        int performDexOptPrimary;
        if (DEBUG) {
            Slog.d("BackgroundDexOptService", "Downgrading " + str);
        }
        if (isCancelling()) {
            return 2;
        }
        String compilerFilterForReason = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(11);
        int i = DexFile.isProfileGuidedCompilerFilter(compilerFilterForReason) ? 37 : 36;
        if (!z2) {
            i |= 512;
        }
        long packageSize = getPackageSize(computer, str);
        if (z || PackageManagerShellCommandDataLoader.PACKAGE.equals(str)) {
            if (!packageManagerService.canHaveOatDir(computer, str)) {
                packageManagerService.deleteOatArtifactsOfPackage(computer, str);
                performDexOptPrimary = 0;
            } else {
                performDexOptPrimary = performDexOptPrimary(str, 11, compilerFilterForReason, i);
            }
        } else {
            performDexOptPrimary = performDexOptSecondary(str, 11, compilerFilterForReason, i);
        }
        if (performDexOptPrimary == 1) {
            FrameworkStatsLog.write(128, str, packageSize, getPackageSize(packageManagerService.snapshotComputer(), str), false);
        }
        return performDexOptPrimary;
    }

    public final int reconcileSecondaryDexFiles() throws Installer.LegacyDexoptDisabledException {
        for (String str : this.mInjector.getDexManager().getAllPackagesWithSecondaryDexFiles()) {
            if (isCancelling()) {
                return 1;
            }
            this.mInjector.getDexManager().reconcileSecondaryDexFiles(str);
        }
        return 0;
    }

    public final int optimizePackage(String str, boolean z, boolean z2) throws Installer.LegacyDexoptDisabledException {
        int i = z2 ? 2 : 9;
        String compilerFilterForReason = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(i);
        int i2 = !z2 ? FrameworkStatsLog.RESOURCE_API_INFO : 4;
        if (DexFile.isProfileGuidedCompilerFilter(compilerFilterForReason)) {
            i2 |= 1;
        }
        if (z || PackageManagerShellCommandDataLoader.PACKAGE.equals(str)) {
            return performDexOptPrimary(str, i, compilerFilterForReason, i2);
        }
        return performDexOptSecondary(str, i, compilerFilterForReason, i2);
    }

    public final int performDexOptPrimary(String str, int i, String str2, int i2) throws Installer.LegacyDexoptDisabledException {
        final DexoptOptions dexoptOptions = new DexoptOptions(str, i, str2, null, i2);
        return trackPerformDexOpt(str, true, new FunctionalUtils.ThrowingCheckedSupplier() { // from class: com.android.server.pm.BackgroundDexOptService$$ExternalSyntheticLambda3
            public final Object get() {
                Integer lambda$performDexOptPrimary$3;
                lambda$performDexOptPrimary$3 = BackgroundDexOptService.this.lambda$performDexOptPrimary$3(dexoptOptions);
                return lambda$performDexOptPrimary$3;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$performDexOptPrimary$3(DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        return Integer.valueOf(this.mDexOptHelper.performDexOptWithStatus(dexoptOptions));
    }

    public final int performDexOptSecondary(String str, int i, String str2, int i2) throws Installer.LegacyDexoptDisabledException {
        final DexoptOptions dexoptOptions = new DexoptOptions(str, i, str2, null, i2 | 8);
        return trackPerformDexOpt(str, false, new FunctionalUtils.ThrowingCheckedSupplier() { // from class: com.android.server.pm.BackgroundDexOptService$$ExternalSyntheticLambda4
            public final Object get() {
                Integer lambda$performDexOptSecondary$4;
                lambda$performDexOptSecondary$4 = BackgroundDexOptService.this.lambda$performDexOptSecondary$4(dexoptOptions);
                return lambda$performDexOptSecondary$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$performDexOptSecondary$4(DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        return Integer.valueOf(this.mDexOptHelper.performDexOpt(dexoptOptions) ? 1 : -1);
    }

    public final int trackPerformDexOpt(String str, boolean z, FunctionalUtils.ThrowingCheckedSupplier<Integer, Installer.LegacyDexoptDisabledException> throwingCheckedSupplier) throws Installer.LegacyDexoptDisabledException {
        synchronized (this.mLock) {
            ArraySet<String> arraySet = z ? this.mFailedPackageNamesPrimary : this.mFailedPackageNamesSecondary;
            if (arraySet.contains(str)) {
                return 0;
            }
            int intValue = ((Integer) throwingCheckedSupplier.get()).intValue();
            if (intValue == -1) {
                synchronized (this.mLock) {
                    arraySet.add(str);
                }
            } else if (intValue == 2) {
                synchronized (this.mLock) {
                    this.mLastCancelledPackages.add(str);
                }
            }
            return intValue;
        }
    }

    public final int convertPackageDexOptimizerStatusToInternal(int i) {
        if (i != -1) {
            if (i == 0 || i == 1) {
                return 0;
            }
            if (i != 2) {
                Slog.e("BackgroundDexOptService", "Unkknown error code from PackageDexOptimizer:" + i, new RuntimeException());
                return 5;
            }
            return 1;
        }
        return 5;
    }

    public final int abortIdleOptimizations(long j) {
        if (isCancelling()) {
            return 1;
        }
        int currentThermalStatus = this.mInjector.getCurrentThermalStatus();
        if (DEBUG) {
            Log.d("BackgroundDexOptService", "Thermal throttling status during bgdexopt: " + currentThermalStatus);
        }
        if (currentThermalStatus >= this.mThermalStatusCutoff) {
            return 3;
        }
        if (this.mInjector.isBatteryLevelLow()) {
            return 4;
        }
        long dataDirUsableSpace = this.mInjector.getDataDirUsableSpace();
        if (dataDirUsableSpace < j) {
            Slog.w("BackgroundDexOptService", "Aborting background dex opt job due to low storage: " + dataDirUsableSpace);
            return 2;
        }
        return 0;
    }

    public final boolean shouldDowngrade(long j) {
        return this.mInjector.getDataDirUsableSpace() < j;
    }

    public final boolean isCancelling() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDexOptCancellingThread != null;
        }
        return z;
    }

    public final void markPostBootUpdateCompleted(JobParameters jobParameters) {
        if (jobParameters.getJobId() != JOB_POST_BOOT_UPDATE) {
            return;
        }
        synchronized (this.mLock) {
            if (!this.mFinishedPostBootUpdate) {
                this.mFinishedPostBootUpdate = true;
            }
        }
        this.mInjector.getJobScheduler().cancel(JOB_POST_BOOT_UPDATE);
    }

    public final void notifyPinService(ArraySet<String> arraySet) {
        PinnerService pinnerService = this.mInjector.getPinnerService();
        if (pinnerService != null) {
            Slog.i("BackgroundDexOptService", "Pinning optimized code " + arraySet);
            pinnerService.update(arraySet, false);
        }
    }

    public final void notifyPackagesUpdated(ArraySet<String> arraySet) {
        synchronized (this.mLock) {
            for (PackagesUpdatedListener packagesUpdatedListener : this.mPackagesUpdatedListeners) {
                packagesUpdatedListener.onPackagesUpdated(arraySet);
            }
        }
    }

    public final void writeStatsLog(JobParameters jobParameters) {
        int i;
        long j;
        synchronized (this.mLock) {
            i = this.mLastExecutionStatus;
            j = this.mLastExecutionDurationMs;
        }
        this.mStatsLogger.write(i, jobParameters.getStopReason(), j);
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.BackgroundDexOptService$Injector */
    /* loaded from: classes2.dex */
    public static final class Injector {
        public final Context mContext;
        public final File mDataDir = Environment.getDataDirectory();
        public final DexManager mDexManager;
        public final PackageManagerService mPackageManagerService;

        public Injector(Context context, DexManager dexManager, PackageManagerService packageManagerService) {
            this.mContext = context;
            this.mDexManager = dexManager;
            this.mPackageManagerService = packageManagerService;
        }

        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public Context getContext() {
            return this.mContext;
        }

        public PackageManagerService getPackageManagerService() {
            return this.mPackageManagerService;
        }

        public DexOptHelper getDexOptHelper() {
            return new DexOptHelper(getPackageManagerService());
        }

        public JobScheduler getJobScheduler() {
            return (JobScheduler) this.mContext.getSystemService(JobScheduler.class);
        }

        public DexManager getDexManager() {
            return this.mDexManager;
        }

        public PinnerService getPinnerService() {
            return (PinnerService) LocalServices.getService(PinnerService.class);
        }

        public boolean isBackgroundDexOptDisabled() {
            return SystemProperties.getBoolean("pm.dexopt.disable_bg_dexopt", false);
        }

        public boolean isBatteryLevelLow() {
            return ((BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class)).getBatteryLevelLow();
        }

        public long getDowngradeUnusedAppsThresholdInMillis() {
            String str = SystemProperties.get("pm.dexopt.downgrade_after_inactive_days");
            if (str == null || str.isEmpty()) {
                Slog.w("BackgroundDexOptService", "SysProp pm.dexopt.downgrade_after_inactive_days not set");
                return Long.MAX_VALUE;
            }
            return TimeUnit.DAYS.toMillis(Long.parseLong(str));
        }

        public boolean supportSecondaryDex() {
            return SystemProperties.getBoolean("dalvik.vm.dexopt.secondary", false);
        }

        public long getDataDirUsableSpace() {
            return this.mDataDir.getUsableSpace();
        }

        public long getDataDirStorageLowBytes() {
            return ((StorageManager) this.mContext.getSystemService(StorageManager.class)).getStorageLowBytes(this.mDataDir);
        }

        public int getCurrentThermalStatus() {
            try {
                return IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice")).getCurrentThermalStatus();
            } catch (RemoteException unused) {
                return 3;
            }
        }

        public int getDexOptThermalCutoff() {
            return SystemProperties.getInt("dalvik.vm.dexopt.thermal-cutoff", 2);
        }

        public Thread createAndStartThread(String str, Runnable runnable) {
            Thread thread = new Thread(runnable, str);
            Slog.i("BackgroundDexOptService", "Starting thread:" + str);
            thread.start();
            return thread;
        }
    }
}
