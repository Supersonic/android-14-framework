package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.GentleUpdateHelper */
/* loaded from: classes2.dex */
public class GentleUpdateHelper {
    public static final long PENDING_CHECK_MILLIS = TimeUnit.SECONDS.toMillis(10);
    public final AppStateHelper mAppStateHelper;
    public final Context mContext;
    public final Handler mHandler;
    public boolean mHasPendingIdleJob;
    public final ArrayDeque<PendingInstallConstraintsCheck> mPendingChecks = new ArrayDeque<>();
    public final ArrayList<CompletableFuture<Boolean>> mPendingIdleFutures = new ArrayList<>();

    /* renamed from: com.android.server.pm.GentleUpdateHelper$Service */
    /* loaded from: classes2.dex */
    public static class Service extends JobService {
        @Override // android.app.job.JobService
        public boolean onStopJob(JobParameters jobParameters) {
            return false;
        }

        @Override // android.app.job.JobService
        public boolean onStartJob(JobParameters jobParameters) {
            try {
                final GentleUpdateHelper gentleUpdateHelper = ActivityThread.getPackageManager().getPackageInstaller().getGentleUpdateHelper();
                Handler handler = gentleUpdateHelper.mHandler;
                Objects.requireNonNull(gentleUpdateHelper);
                handler.post(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$Service$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        GentleUpdateHelper.this.runIdleJob();
                    }
                });
                return false;
            } catch (Exception e) {
                Slog.e("GentleUpdateHelper", "Failed to get PackageInstallerService", e);
                return false;
            }
        }
    }

    /* renamed from: com.android.server.pm.GentleUpdateHelper$PendingInstallConstraintsCheck */
    /* loaded from: classes2.dex */
    public static class PendingInstallConstraintsCheck {
        public final PackageInstaller.InstallConstraints constraints;
        public final CompletableFuture<PackageInstaller.InstallConstraintsResult> future;
        public final long mFinishTime;
        public final List<String> packageNames;

        public PendingInstallConstraintsCheck(List<String> list, PackageInstaller.InstallConstraints installConstraints, CompletableFuture<PackageInstaller.InstallConstraintsResult> completableFuture, long j) {
            this.packageNames = list;
            this.constraints = installConstraints;
            this.future = completableFuture;
            this.mFinishTime = SystemClock.elapsedRealtime() + Math.max(0L, Math.min(604800000L, j));
        }

        public boolean isTimedOut() {
            return SystemClock.elapsedRealtime() >= this.mFinishTime;
        }

        public long getRemainingTimeMillis() {
            return Math.max(this.mFinishTime - SystemClock.elapsedRealtime(), 0L);
        }
    }

    public GentleUpdateHelper(Context context, Looper looper, AppStateHelper appStateHelper) {
        this.mContext = context;
        this.mHandler = new Handler(looper);
        this.mAppStateHelper = appStateHelper;
    }

    public void systemReady() {
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
        activityManager.addOnUidImportanceListener(new ActivityManager.OnUidImportanceListener() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda2
            public final void onUidImportance(int i, int i2) {
                GentleUpdateHelper.this.onUidImportance(i, i2);
            }
        }, 100);
        activityManager.addOnUidImportanceListener(new ActivityManager.OnUidImportanceListener() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda2
            public final void onUidImportance(int i, int i2) {
                GentleUpdateHelper.this.onUidImportance(i, i2);
            }
        }, 125);
    }

    public CompletableFuture<PackageInstaller.InstallConstraintsResult> checkInstallConstraints(final List<String> list, final PackageInstaller.InstallConstraints installConstraints, final long j) {
        final CompletableFuture<PackageInstaller.InstallConstraintsResult> completableFuture = new CompletableFuture<>();
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                GentleUpdateHelper.this.lambda$checkInstallConstraints$2(list, installConstraints, completableFuture, j);
            }
        });
        return completableFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkInstallConstraints$2(List list, PackageInstaller.InstallConstraints installConstraints, CompletableFuture completableFuture, long j) {
        final PendingInstallConstraintsCheck pendingInstallConstraintsCheck = new PendingInstallConstraintsCheck(list, installConstraints, completableFuture, j);
        (installConstraints.isDeviceIdleRequired() ? checkDeviceIdle() : CompletableFuture.completedFuture(Boolean.FALSE)).thenAccept(new Consumer() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GentleUpdateHelper.this.lambda$checkInstallConstraints$1(pendingInstallConstraintsCheck, (Boolean) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkInstallConstraints$1(final PendingInstallConstraintsCheck pendingInstallConstraintsCheck, Boolean bool) {
        Preconditions.checkState(this.mHandler.getLooper().isCurrentThread());
        if (processPendingCheck(pendingInstallConstraintsCheck, bool.booleanValue())) {
            return;
        }
        this.mPendingChecks.add(pendingInstallConstraintsCheck);
        scheduleIdleJob();
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GentleUpdateHelper.this.lambda$checkInstallConstraints$0(pendingInstallConstraintsCheck);
            }
        }, pendingInstallConstraintsCheck.getRemainingTimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkInstallConstraints$0(PendingInstallConstraintsCheck pendingInstallConstraintsCheck) {
        processPendingCheck(pendingInstallConstraintsCheck, false);
    }

    public final CompletableFuture<Boolean> checkDeviceIdle() {
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        this.mPendingIdleFutures.add(completableFuture);
        scheduleIdleJob();
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GentleUpdateHelper.lambda$checkDeviceIdle$3(completableFuture);
            }
        }, PENDING_CHECK_MILLIS);
        return completableFuture;
    }

    public static /* synthetic */ void lambda$checkDeviceIdle$3(CompletableFuture completableFuture) {
        completableFuture.complete(Boolean.FALSE);
    }

    public final void scheduleIdleJob() {
        if (SystemProperties.getBoolean("debug.pm.gentle_update_test.is_idle", false)) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GentleUpdateHelper.this.runIdleJob();
                }
            });
        } else if (this.mHasPendingIdleJob) {
        } else {
            this.mHasPendingIdleJob = true;
            ((JobScheduler) this.mContext.getSystemService(JobScheduler.class)).schedule(new JobInfo.Builder(235306967, new ComponentName(this.mContext.getPackageName(), Service.class.getName())).setRequiresDeviceIdle(true).build());
        }
    }

    public final void runIdleJob() {
        this.mHasPendingIdleJob = false;
        processPendingChecksInIdle();
        Iterator<CompletableFuture<Boolean>> it = this.mPendingIdleFutures.iterator();
        while (it.hasNext()) {
            it.next().complete(Boolean.TRUE);
        }
        this.mPendingIdleFutures.clear();
    }

    public final boolean areConstraintsSatisfied(List<String> list, PackageInstaller.InstallConstraints installConstraints, boolean z) {
        return (!installConstraints.isDeviceIdleRequired() || z) && !((installConstraints.isAppNotForegroundRequired() && this.mAppStateHelper.hasForegroundApp(list)) || ((installConstraints.isAppNotInteractingRequired() && this.mAppStateHelper.hasInteractingApp(list)) || ((installConstraints.isAppNotTopVisibleRequired() && this.mAppStateHelper.hasTopVisibleApp(list)) || (installConstraints.isNotInCallRequired() && this.mAppStateHelper.isInCall()))));
    }

    public final boolean processPendingCheck(PendingInstallConstraintsCheck pendingInstallConstraintsCheck, boolean z) {
        CompletableFuture<PackageInstaller.InstallConstraintsResult> completableFuture = pendingInstallConstraintsCheck.future;
        if (completableFuture.isDone()) {
            return true;
        }
        boolean areConstraintsSatisfied = areConstraintsSatisfied(this.mAppStateHelper.getDependencyPackages(pendingInstallConstraintsCheck.packageNames), pendingInstallConstraintsCheck.constraints, z);
        if (areConstraintsSatisfied || pendingInstallConstraintsCheck.isTimedOut()) {
            completableFuture.complete(new PackageInstaller.InstallConstraintsResult(areConstraintsSatisfied));
            return true;
        }
        return false;
    }

    public final void processPendingChecksInIdle() {
        int size = this.mPendingChecks.size();
        for (int i = 0; i < size; i++) {
            PendingInstallConstraintsCheck remove = this.mPendingChecks.remove();
            if (!processPendingCheck(remove, true)) {
                this.mPendingChecks.add(remove);
            }
        }
        if (this.mPendingChecks.isEmpty()) {
            return;
        }
        scheduleIdleJob();
    }

    /* renamed from: onUidImportance */
    public final void lambda$onUidImportance$4(String str, int i) {
        int size = this.mPendingChecks.size();
        for (int i2 = 0; i2 < size; i2++) {
            PendingInstallConstraintsCheck remove = this.mPendingChecks.remove();
            if (!this.mAppStateHelper.getDependencyPackages(remove.packageNames).contains(str) || !processPendingCheck(remove, false)) {
                this.mPendingChecks.add(remove);
            }
        }
        if (this.mPendingChecks.isEmpty()) {
            return;
        }
        scheduleIdleJob();
    }

    public final void onUidImportance(int i, final int i2) {
        try {
            final String nameForUid = ActivityThread.getPackageManager().getNameForUid(i);
            this.mHandler.post(new Runnable() { // from class: com.android.server.pm.GentleUpdateHelper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    GentleUpdateHelper.this.lambda$onUidImportance$4(nameForUid, i2);
                }
            });
        } catch (RemoteException unused) {
        }
    }
}
