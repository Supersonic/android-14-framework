package com.android.server.job.controllers;

import android.app.ActivityManagerInternal;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.server.AppStateTracker;
import com.android.server.AppStateTrackerImpl;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.JobStore;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class BackgroundJobsController extends StateController {
    public static final boolean DEBUG;
    public final ActivityManagerInternal mActivityManagerInternal;
    public final AppStateTrackerImpl mAppStateTracker;
    public final AppStateTrackerImpl.Listener mForceAppStandbyListener;
    public final UpdateJobFunctor mUpdateJobFunctor;

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
    }

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.Background", 3);
    }

    public BackgroundJobsController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
        this.mUpdateJobFunctor = new UpdateJobFunctor();
        AppStateTrackerImpl.Listener listener = new AppStateTrackerImpl.Listener() { // from class: com.android.server.job.controllers.BackgroundJobsController.1
            @Override // com.android.server.AppStateTrackerImpl.Listener
            public void updateAllJobs() {
                synchronized (BackgroundJobsController.this.mLock) {
                    BackgroundJobsController.this.updateAllJobRestrictionsLocked();
                }
            }

            @Override // com.android.server.AppStateTrackerImpl.Listener
            public void updateJobsForUid(int i, boolean z) {
                synchronized (BackgroundJobsController.this.mLock) {
                    BackgroundJobsController.this.updateJobRestrictionsForUidLocked(i, z);
                }
            }

            @Override // com.android.server.AppStateTrackerImpl.Listener
            public void updateJobsForUidPackage(int i, String str, boolean z) {
                synchronized (BackgroundJobsController.this.mLock) {
                    BackgroundJobsController.this.updateJobRestrictionsForUidLocked(i, z);
                }
            }
        };
        this.mForceAppStandbyListener = listener;
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        Objects.requireNonNull(activityManagerInternal);
        this.mActivityManagerInternal = activityManagerInternal;
        AppStateTracker appStateTracker = (AppStateTracker) LocalServices.getService(AppStateTracker.class);
        Objects.requireNonNull(appStateTracker);
        AppStateTrackerImpl appStateTrackerImpl = (AppStateTrackerImpl) appStateTracker;
        this.mAppStateTracker = appStateTrackerImpl;
        appStateTrackerImpl.addListener(listener);
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        updateSingleJobRestrictionLocked(jobStatus, JobSchedulerService.sElapsedRealtimeClock.millis(), 0);
    }

    @Override // com.android.server.job.controllers.StateController
    public void evaluateStateLocked(JobStatus jobStatus) {
        if (jobStatus.isRequestedExpeditedJob()) {
            updateSingleJobRestrictionLocked(jobStatus, JobSchedulerService.sElapsedRealtimeClock.millis(), 0);
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(final IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        this.mAppStateTracker.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        this.mService.getJobStore().forEachJob(predicate, new Consumer() { // from class: com.android.server.job.controllers.BackgroundJobsController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                BackgroundJobsController.this.lambda$dumpControllerStateLocked$0(indentingPrintWriter, (JobStatus) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dumpControllerStateLocked$0(IndentingPrintWriter indentingPrintWriter, JobStatus jobStatus) {
        int sourceUid = jobStatus.getSourceUid();
        String sourcePackageName = jobStatus.getSourcePackageName();
        indentingPrintWriter.print("#");
        jobStatus.printUniqueId(indentingPrintWriter);
        indentingPrintWriter.print(" from ");
        UserHandle.formatUid(indentingPrintWriter, sourceUid);
        indentingPrintWriter.print(this.mAppStateTracker.isUidActive(sourceUid) ? " active" : " idle");
        if (this.mAppStateTracker.isUidPowerSaveExempt(sourceUid) || this.mAppStateTracker.isUidTempPowerSaveExempt(sourceUid)) {
            indentingPrintWriter.print(", exempted");
        }
        indentingPrintWriter.print(": ");
        indentingPrintWriter.print(sourcePackageName);
        indentingPrintWriter.print(" [RUN_ANY_IN_BACKGROUND ");
        indentingPrintWriter.print(this.mAppStateTracker.isRunAnyInBackgroundAppOpsAllowed(sourceUid, sourcePackageName) ? "allowed]" : "disallowed]");
        if ((jobStatus.satisfiedConstraints & 4194304) != 0) {
            indentingPrintWriter.println(" RUNNABLE");
        } else {
            indentingPrintWriter.println(" WAITING");
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(final ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268033L);
        this.mAppStateTracker.dumpProto(protoOutputStream, 1146756268033L);
        this.mService.getJobStore().forEachJob(predicate, new Consumer() { // from class: com.android.server.job.controllers.BackgroundJobsController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                BackgroundJobsController.this.lambda$dumpControllerStateLocked$1(protoOutputStream, (JobStatus) obj);
            }
        });
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dumpControllerStateLocked$1(ProtoOutputStream protoOutputStream, JobStatus jobStatus) {
        long start = protoOutputStream.start(2246267895810L);
        jobStatus.writeToShortProto(protoOutputStream, 1146756268033L);
        int sourceUid = jobStatus.getSourceUid();
        protoOutputStream.write(1120986464258L, sourceUid);
        String sourcePackageName = jobStatus.getSourcePackageName();
        protoOutputStream.write(1138166333443L, sourcePackageName);
        protoOutputStream.write(1133871366148L, this.mAppStateTracker.isUidActive(sourceUid));
        protoOutputStream.write(1133871366149L, this.mAppStateTracker.isUidPowerSaveExempt(sourceUid) || this.mAppStateTracker.isUidTempPowerSaveExempt(sourceUid));
        protoOutputStream.write(1133871366150L, this.mAppStateTracker.isRunAnyInBackgroundAppOpsAllowed(sourceUid, sourcePackageName));
        protoOutputStream.write(1133871366151L, (jobStatus.satisfiedConstraints & 4194304) != 0);
        protoOutputStream.end(start);
    }

    public final void updateAllJobRestrictionsLocked() {
        updateJobRestrictionsLocked(-1, 0);
    }

    public final void updateJobRestrictionsForUidLocked(int i, boolean z) {
        updateJobRestrictionsLocked(i, z ? 1 : 2);
    }

    public final void updateJobRestrictionsLocked(int i, int i2) {
        this.mUpdateJobFunctor.prepare(i2);
        boolean z = DEBUG;
        long elapsedRealtimeNanos = z ? SystemClock.elapsedRealtimeNanos() : 0L;
        JobStore jobStore = this.mService.getJobStore();
        if (i > 0) {
            jobStore.forEachJobForSourceUid(i, this.mUpdateJobFunctor);
        } else {
            jobStore.forEachJob(this.mUpdateJobFunctor);
        }
        long elapsedRealtimeNanos2 = z ? SystemClock.elapsedRealtimeNanos() - elapsedRealtimeNanos : 0L;
        if (z) {
            Slog.d("JobScheduler.Background", String.format("Job status updated: %d/%d checked/total jobs, %d us", Integer.valueOf(this.mUpdateJobFunctor.mCheckedCount), Integer.valueOf(this.mUpdateJobFunctor.mTotalCount), Long.valueOf(elapsedRealtimeNanos2 / 1000)));
        }
        if (this.mUpdateJobFunctor.mChangedJobs.size() > 0) {
            this.mStateChangedListener.onControllerStateChanged(this.mUpdateJobFunctor.mChangedJobs);
        }
    }

    public boolean updateSingleJobRestrictionLocked(JobStatus jobStatus, long j, int i) {
        boolean z;
        int sourceUid = jobStatus.getSourceUid();
        String sourcePackageName = jobStatus.getSourcePackageName();
        boolean z2 = true;
        boolean z3 = !this.mAppStateTracker.areJobsRestricted(sourceUid, sourcePackageName, jobStatus.canRunInBatterySaver());
        if (i == 0) {
            z = this.mAppStateTracker.isUidActive(sourceUid);
        } else {
            z = i == 1;
        }
        if (z && jobStatus.getStandbyBucket() == 4) {
            jobStatus.maybeLogBucketMismatch();
        }
        if (this.mActivityManagerInternal.isBgAutoRestrictedBucketFeatureFlagEnabled() || this.mAppStateTracker.isRunAnyInBackgroundAppOpsAllowed(sourceUid, sourcePackageName)) {
            z2 = false;
        }
        return jobStatus.setBackgroundNotRestrictedConstraintSatisfied(j, z3, z2) | jobStatus.setUidActive(z);
    }

    /* loaded from: classes.dex */
    public final class UpdateJobFunctor implements Consumer<JobStatus> {
        public int mActiveState;
        public final ArraySet<JobStatus> mChangedJobs;
        public int mCheckedCount;
        public int mTotalCount;
        public long mUpdateTimeElapsed;

        public UpdateJobFunctor() {
            this.mChangedJobs = new ArraySet<>();
            this.mTotalCount = 0;
            this.mCheckedCount = 0;
            this.mUpdateTimeElapsed = 0L;
        }

        public void prepare(int i) {
            this.mActiveState = i;
            this.mUpdateTimeElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mChangedJobs.clear();
            this.mTotalCount = 0;
            this.mCheckedCount = 0;
        }

        @Override // java.util.function.Consumer
        public void accept(JobStatus jobStatus) {
            this.mTotalCount++;
            this.mCheckedCount++;
            if (BackgroundJobsController.this.updateSingleJobRestrictionLocked(jobStatus, this.mUpdateTimeElapsed, this.mActiveState)) {
                this.mChangedJobs.add(jobStatus);
            }
        }
    }
}
