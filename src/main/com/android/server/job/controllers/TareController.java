package com.android.server.job.controllers;

import android.app.job.JobInfo;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AppSchedulingModuleThread;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.tare.EconomyManagerInternal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class TareController extends StateController {
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_DEFAULT;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_HIGH;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_HIGH_EXPEDITED;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_LOW;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_MAX;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_MAX_EXPEDITED;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_RUNNING_MIN;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_DEFAULT;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_HIGH;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_HIGH_EXPEDITED;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_LOW;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_MAX;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_MAX_EXPEDITED;
    public static final EconomyManagerInternal.ActionBill BILL_JOB_START_MIN;
    public static final boolean DEBUG;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, ArrayMap<EconomyManagerInternal.ActionBill, Boolean>> mAffordabilityCache;
    public final EconomyManagerInternal.AffordabilityChangeListener mAffordabilityChangeListener;
    public final BackgroundJobsController mBackgroundJobsController;
    public final ConnectivityController mConnectivityController;
    public final EconomyManagerInternal mEconomyManagerInternal;
    @GuardedBy({"mLock"})
    public boolean mIsEnabled;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, ArrayMap<EconomyManagerInternal.ActionBill, ArraySet<JobStatus>>> mRegisteredBillsAndJobs;
    @GuardedBy({"mLock"})
    public final ArraySet<JobStatus> mTopStartedJobs;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.TARE", 3);
        BILL_JOB_START_MIN = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612740, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 120000L)));
        BILL_JOB_RUNNING_MIN = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 60000L)));
        BILL_JOB_START_LOW = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612740, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 60000L)));
        BILL_JOB_RUNNING_LOW = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 30000L)));
        BILL_JOB_START_DEFAULT = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612740, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 30000L)));
        BILL_JOB_RUNNING_DEFAULT = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612741, 0, 1000L)));
        BILL_JOB_START_HIGH = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612738, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612739, 0, 30000L)));
        BILL_JOB_RUNNING_HIGH = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612739, 0, 1000L)));
        BILL_JOB_START_MAX = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612736, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612737, 0, 30000L)));
        BILL_JOB_RUNNING_MAX = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612737, 0, 1000L)));
        BILL_JOB_START_MAX_EXPEDITED = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612736, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612737, 0, 30000L)));
        BILL_JOB_RUNNING_MAX_EXPEDITED = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612737, 0, 1000L)));
        BILL_JOB_START_HIGH_EXPEDITED = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612738, 1, 0L), new EconomyManagerInternal.AnticipatedAction(1610612739, 0, 30000L)));
        BILL_JOB_RUNNING_HIGH_EXPEDITED = new EconomyManagerInternal.ActionBill(List.of(new EconomyManagerInternal.AnticipatedAction(1610612739, 0, 1000L)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:26:0x008c A[Catch: all -> 0x00b2, TryCatch #0 {, blocks: (B:7:0x003a, B:9:0x0044, B:10:0x004e, B:12:0x005f, B:14:0x0067, B:15:0x006e, B:17:0x0074, B:19:0x007c, B:24:0x0086, B:26:0x008c, B:27:0x008f, B:29:0x0095, B:31:0x009f, B:32:0x00a2, B:33:0x00a5, B:35:0x00ab, B:36:0x00b0), top: B:41:0x003a }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$new$0(int i, String str, EconomyManagerInternal.ActionBill actionBill, boolean z) {
        ArraySet arraySet;
        boolean z2;
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (DEBUG) {
            Slog.d("JobScheduler.TARE", i + XmlUtils.STRING_ARRAY_SEPARATOR + str + " affordability for " + getBillName(actionBill) + " changed to " + z);
        }
        synchronized (this.mLock) {
            ArrayMap arrayMap = (ArrayMap) this.mAffordabilityCache.get(i, str);
            if (arrayMap == null) {
                arrayMap = new ArrayMap();
                this.mAffordabilityCache.add(i, str, arrayMap);
            }
            arrayMap.put(actionBill, Boolean.valueOf(z));
            ArrayMap arrayMap2 = (ArrayMap) this.mRegisteredBillsAndJobs.get(i, str);
            if (arrayMap2 != null && (arraySet = (ArraySet) arrayMap2.get(actionBill)) != null) {
                ArraySet<JobStatus> arraySet2 = new ArraySet<>();
                for (int i2 = 0; i2 < arraySet.size(); i2++) {
                    JobStatus jobStatus = (JobStatus) arraySet.valueAt(i2);
                    if (!z && !hasEnoughWealthLocked(jobStatus)) {
                        z2 = false;
                        if (jobStatus.setTareWealthConstraintSatisfied(millis, z2)) {
                            arraySet2.add(jobStatus);
                        }
                        if (jobStatus.isRequestedExpeditedJob() && setExpeditedTareApproved(jobStatus, millis, canAffordExpeditedBillLocked(jobStatus))) {
                            arraySet2.add(jobStatus);
                        }
                    }
                    z2 = true;
                    if (jobStatus.setTareWealthConstraintSatisfied(millis, z2)) {
                    }
                    if (jobStatus.isRequestedExpeditedJob()) {
                        arraySet2.add(jobStatus);
                    }
                }
                if (arraySet2.size() > 0) {
                    this.mStateChangedListener.onControllerStateChanged(arraySet2);
                }
            }
        }
    }

    public TareController(JobSchedulerService jobSchedulerService, BackgroundJobsController backgroundJobsController, ConnectivityController connectivityController) {
        super(jobSchedulerService);
        this.mAffordabilityCache = new SparseArrayMap<>();
        this.mRegisteredBillsAndJobs = new SparseArrayMap<>();
        this.mAffordabilityChangeListener = new EconomyManagerInternal.AffordabilityChangeListener() { // from class: com.android.server.job.controllers.TareController$$ExternalSyntheticLambda0
            @Override // com.android.server.tare.EconomyManagerInternal.AffordabilityChangeListener
            public final void onAffordabilityChanged(int i, String str, EconomyManagerInternal.ActionBill actionBill, boolean z) {
                TareController.this.lambda$new$0(i, str, actionBill, z);
            }
        };
        this.mTopStartedJobs = new ArraySet<>();
        this.mBackgroundJobsController = backgroundJobsController;
        this.mConnectivityController = connectivityController;
        this.mEconomyManagerInternal = (EconomyManagerInternal) LocalServices.getService(EconomyManagerInternal.class);
        this.mIsEnabled = this.mConstants.USE_TARE_POLICY;
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        boolean z = true;
        if (jobStatus.shouldTreatAsUserInitiatedJob()) {
            jobStatus.setTareWealthConstraintSatisfied(millis, true);
            return;
        }
        jobStatus.setTareWealthConstraintSatisfied(millis, hasEnoughWealthLocked(jobStatus));
        setExpeditedTareApproved(jobStatus, millis, (jobStatus.isRequestedExpeditedJob() && canAffordExpeditedBillLocked(jobStatus)) ? false : false);
        ArraySet<EconomyManagerInternal.ActionBill> possibleStartBills = getPossibleStartBills(jobStatus);
        for (int i = 0; i < possibleStartBills.size(); i++) {
            addJobToBillList(jobStatus, possibleStartBills.valueAt(i));
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void prepareForExecutionLocked(JobStatus jobStatus) {
        if (jobStatus.shouldTreatAsUserInitiatedJob()) {
            return;
        }
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        ArrayMap arrayMap = (ArrayMap) this.mRegisteredBillsAndJobs.get(sourceUserId, sourcePackageName);
        if (arrayMap == null) {
            Slog.e("JobScheduler.TARE", "Job is being prepared but doesn't have a pre-existing billToJobMap");
        } else {
            for (int i = 0; i < arrayMap.size(); i++) {
                removeJobFromBillList(jobStatus, (EconomyManagerInternal.ActionBill) arrayMap.keyAt(i));
            }
        }
        if (this.mService.getUidBias(jobStatus.getSourceUid()) == 40) {
            if (DEBUG) {
                Slog.d("JobScheduler.TARE", jobStatus.toShortString() + " is top started job");
            }
            this.mTopStartedJobs.add(jobStatus);
            return;
        }
        addJobToBillList(jobStatus, getRunningBill(jobStatus));
        this.mEconomyManagerInternal.noteOngoingEventStarted(sourceUserId, sourcePackageName, getRunningActionId(jobStatus), String.valueOf(jobStatus.getJobId()));
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void unprepareFromExecutionLocked(JobStatus jobStatus) {
        if (jobStatus.shouldTreatAsUserInitiatedJob()) {
            return;
        }
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        if (!this.mTopStartedJobs.remove(jobStatus)) {
            this.mEconomyManagerInternal.noteOngoingEventStopped(sourceUserId, sourcePackageName, getRunningActionId(jobStatus), String.valueOf(jobStatus.getJobId()));
        }
        ArraySet<EconomyManagerInternal.ActionBill> possibleStartBills = getPossibleStartBills(jobStatus);
        ArrayMap arrayMap = (ArrayMap) this.mRegisteredBillsAndJobs.get(sourceUserId, sourcePackageName);
        if (arrayMap == null) {
            Slog.e("JobScheduler.TARE", "Job was just unprepared but didn't have a pre-existing billToJobMap");
        } else {
            for (int i = 0; i < arrayMap.size(); i++) {
                removeJobFromBillList(jobStatus, (EconomyManagerInternal.ActionBill) arrayMap.keyAt(i));
            }
        }
        for (int i2 = 0; i2 < possibleStartBills.size(); i2++) {
            addJobToBillList(jobStatus, possibleStartBills.valueAt(i2));
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.shouldTreatAsUserInitiatedJob()) {
            return;
        }
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        if (!this.mTopStartedJobs.remove(jobStatus) && jobStatus.madeActive > 0) {
            this.mEconomyManagerInternal.noteOngoingEventStopped(sourceUserId, sourcePackageName, getRunningActionId(jobStatus), String.valueOf(jobStatus.getJobId()));
        }
        ArrayMap arrayMap = (ArrayMap) this.mRegisteredBillsAndJobs.get(sourceUserId, sourcePackageName);
        if (arrayMap != null) {
            for (int i = 0; i < arrayMap.size(); i++) {
                removeJobFromBillList(jobStatus, (EconomyManagerInternal.ActionBill) arrayMap.keyAt(i));
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onConstantsUpdatedLocked() {
        boolean z = this.mIsEnabled;
        boolean z2 = this.mConstants.USE_TARE_POLICY;
        if (z != z2) {
            this.mIsEnabled = z2;
            AppSchedulingModuleThread.getHandler().post(new Runnable() { // from class: com.android.server.job.controllers.TareController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TareController.this.lambda$onConstantsUpdatedLocked$2();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConstantsUpdatedLocked$2() {
        synchronized (this.mLock) {
            final long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mService.getJobStore().forEachJob(new Consumer() { // from class: com.android.server.job.controllers.TareController$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TareController.this.lambda$onConstantsUpdatedLocked$1(millis, (JobStatus) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConstantsUpdatedLocked$1(long j, JobStatus jobStatus) {
        boolean z = true;
        if (!this.mIsEnabled) {
            jobStatus.setTareWealthConstraintSatisfied(j, true);
            setExpeditedTareApproved(jobStatus, j, true);
            return;
        }
        jobStatus.setTareWealthConstraintSatisfied(j, hasEnoughWealthLocked(jobStatus));
        setExpeditedTareApproved(jobStatus, j, (jobStatus.isRequestedExpeditedJob() && canAffordExpeditedBillLocked(jobStatus)) ? false : false);
    }

    @GuardedBy({"mLock"})
    public boolean canScheduleEJ(JobStatus jobStatus) {
        if (this.mIsEnabled) {
            if (jobStatus.getEffectivePriority() == 500) {
                return canAffordBillLocked(jobStatus, BILL_JOB_START_MAX_EXPEDITED);
            }
            return canAffordBillLocked(jobStatus, BILL_JOB_START_HIGH_EXPEDITED);
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final boolean isTopStartedJobLocked(JobStatus jobStatus) {
        return this.mTopStartedJobs.contains(jobStatus);
    }

    @GuardedBy({"mLock"})
    public long getMaxJobExecutionTimeMsLocked(JobStatus jobStatus) {
        if (!this.mIsEnabled) {
            return this.mConstants.RUNTIME_FREE_QUOTA_MAX_LIMIT_MS;
        }
        return this.mEconomyManagerInternal.getMaxDurationMs(jobStatus.getSourceUserId(), jobStatus.getSourcePackageName(), getRunningBill(jobStatus));
    }

    @GuardedBy({"mLock"})
    public final void addJobToBillList(JobStatus jobStatus, EconomyManagerInternal.ActionBill actionBill) {
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        ArrayMap arrayMap = (ArrayMap) this.mRegisteredBillsAndJobs.get(sourceUserId, sourcePackageName);
        if (arrayMap == null) {
            arrayMap = new ArrayMap();
            this.mRegisteredBillsAndJobs.add(sourceUserId, sourcePackageName, arrayMap);
        }
        ArraySet arraySet = (ArraySet) arrayMap.get(actionBill);
        if (arraySet == null) {
            arraySet = new ArraySet();
            arrayMap.put(actionBill, arraySet);
        }
        if (arraySet.add(jobStatus)) {
            this.mEconomyManagerInternal.registerAffordabilityChangeListener(sourceUserId, sourcePackageName, this.mAffordabilityChangeListener, actionBill);
        }
    }

    @GuardedBy({"mLock"})
    public final void removeJobFromBillList(JobStatus jobStatus, EconomyManagerInternal.ActionBill actionBill) {
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        ArrayMap arrayMap = (ArrayMap) this.mRegisteredBillsAndJobs.get(sourceUserId, sourcePackageName);
        if (arrayMap != null) {
            ArraySet arraySet = (ArraySet) arrayMap.get(actionBill);
            if (arraySet == null || (arraySet.remove(jobStatus) && arraySet.size() == 0)) {
                this.mEconomyManagerInternal.unregisterAffordabilityChangeListener(sourceUserId, sourcePackageName, this.mAffordabilityChangeListener, actionBill);
                ArrayMap arrayMap2 = (ArrayMap) this.mAffordabilityCache.get(sourceUserId, sourcePackageName);
                if (arrayMap2 != null) {
                    arrayMap2.remove(actionBill);
                }
            }
        }
    }

    public final ArraySet<EconomyManagerInternal.ActionBill> getPossibleStartBills(JobStatus jobStatus) {
        ArraySet<EconomyManagerInternal.ActionBill> arraySet = new ArraySet<>();
        if (jobStatus.isRequestedExpeditedJob()) {
            if (jobStatus.getEffectivePriority() == 500) {
                arraySet.add(BILL_JOB_START_MAX_EXPEDITED);
            } else {
                arraySet.add(BILL_JOB_START_HIGH_EXPEDITED);
            }
        }
        int effectivePriority = jobStatus.getEffectivePriority();
        if (effectivePriority == 100) {
            arraySet.add(BILL_JOB_START_MIN);
        } else if (effectivePriority == 200) {
            arraySet.add(BILL_JOB_START_LOW);
        } else if (effectivePriority == 300) {
            arraySet.add(BILL_JOB_START_DEFAULT);
        } else if (effectivePriority == 400) {
            arraySet.add(BILL_JOB_START_HIGH);
        } else if (effectivePriority == 500) {
            arraySet.add(BILL_JOB_START_MAX);
        } else {
            Slog.wtf("JobScheduler.TARE", "Unexpected priority: " + JobInfo.getPriorityString(jobStatus.getEffectivePriority()));
        }
        return arraySet;
    }

    public final EconomyManagerInternal.ActionBill getRunningBill(JobStatus jobStatus) {
        if (jobStatus.shouldTreatAsExpeditedJob() || jobStatus.startedAsExpeditedJob) {
            if (jobStatus.getEffectivePriority() == 500) {
                return BILL_JOB_RUNNING_MAX_EXPEDITED;
            }
            return BILL_JOB_RUNNING_HIGH_EXPEDITED;
        }
        int effectivePriority = jobStatus.getEffectivePriority();
        if (effectivePriority != 100) {
            if (effectivePriority == 200) {
                return BILL_JOB_RUNNING_LOW;
            }
            if (effectivePriority != 300) {
                if (effectivePriority == 400) {
                    return BILL_JOB_RUNNING_HIGH;
                }
                if (effectivePriority == 500) {
                    return BILL_JOB_RUNNING_MAX;
                }
                Slog.wtf("JobScheduler.TARE", "Got unexpected priority: " + jobStatus.getEffectivePriority());
            }
            return BILL_JOB_RUNNING_DEFAULT;
        }
        return BILL_JOB_RUNNING_MIN;
    }

    public static int getRunningActionId(JobStatus jobStatus) {
        int effectivePriority = jobStatus.getEffectivePriority();
        if (effectivePriority != 100) {
            if (effectivePriority != 200) {
                if (effectivePriority != 300) {
                    if (effectivePriority != 400) {
                        if (effectivePriority != 500) {
                            Slog.wtf("JobScheduler.TARE", "Unknown priority: " + JobInfo.getPriorityString(jobStatus.getEffectivePriority()));
                            return 1610612741;
                        }
                        return 1610612737;
                    }
                    return 1610612739;
                }
                return 1610612741;
            }
            return 1610612743;
        }
        return 1610612745;
    }

    @GuardedBy({"mLock"})
    public final boolean canAffordBillLocked(JobStatus jobStatus, EconomyManagerInternal.ActionBill actionBill) {
        if (!this.mIsEnabled || this.mService.getUidBias(jobStatus.getSourceUid()) == 40 || isTopStartedJobLocked(jobStatus)) {
            return true;
        }
        int sourceUserId = jobStatus.getSourceUserId();
        String sourcePackageName = jobStatus.getSourcePackageName();
        ArrayMap arrayMap = (ArrayMap) this.mAffordabilityCache.get(sourceUserId, sourcePackageName);
        if (arrayMap == null) {
            arrayMap = new ArrayMap();
            this.mAffordabilityCache.add(sourceUserId, sourcePackageName, arrayMap);
        }
        if (arrayMap.containsKey(actionBill)) {
            return ((Boolean) arrayMap.get(actionBill)).booleanValue();
        }
        boolean canPayFor = this.mEconomyManagerInternal.canPayFor(sourceUserId, sourcePackageName, actionBill);
        arrayMap.put(actionBill, Boolean.valueOf(canPayFor));
        return canPayFor;
    }

    @GuardedBy({"mLock"})
    public final boolean canAffordExpeditedBillLocked(JobStatus jobStatus) {
        if (this.mIsEnabled) {
            if (jobStatus.isRequestedExpeditedJob()) {
                if (this.mService.getUidBias(jobStatus.getSourceUid()) == 40 || isTopStartedJobLocked(jobStatus)) {
                    return true;
                }
                if (this.mService.isCurrentlyRunningLocked(jobStatus)) {
                    return canAffordBillLocked(jobStatus, getRunningBill(jobStatus));
                }
                if (jobStatus.getEffectivePriority() == 500) {
                    return canAffordBillLocked(jobStatus, BILL_JOB_START_MAX_EXPEDITED);
                }
                return canAffordBillLocked(jobStatus, BILL_JOB_START_HIGH_EXPEDITED);
            }
            return false;
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final boolean hasEnoughWealthLocked(JobStatus jobStatus) {
        if (!this.mIsEnabled || jobStatus.shouldTreatAsUserInitiatedJob() || this.mService.getUidBias(jobStatus.getSourceUid()) == 40 || isTopStartedJobLocked(jobStatus)) {
            return true;
        }
        if (this.mService.isCurrentlyRunningLocked(jobStatus)) {
            return canAffordBillLocked(jobStatus, getRunningBill(jobStatus));
        }
        ArraySet<EconomyManagerInternal.ActionBill> possibleStartBills = getPossibleStartBills(jobStatus);
        for (int i = 0; i < possibleStartBills.size(); i++) {
            if (canAffordBillLocked(jobStatus, possibleStartBills.valueAt(i))) {
                return true;
            }
        }
        return false;
    }

    public final boolean setExpeditedTareApproved(JobStatus jobStatus, long j, boolean z) {
        if (jobStatus.setExpeditedJobTareApproved(j, z)) {
            this.mBackgroundJobsController.evaluateStateLocked(jobStatus);
            this.mConnectivityController.evaluateStateLocked(jobStatus);
            if (z && jobStatus.isReady()) {
                this.mStateChangedListener.onRunJobNow(jobStatus);
                return true;
            }
            return true;
        }
        return false;
    }

    public final String getBillName(EconomyManagerInternal.ActionBill actionBill) {
        if (actionBill.equals(BILL_JOB_START_MAX_EXPEDITED)) {
            return "EJ_MAX_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_MAX_EXPEDITED)) {
            return "EJ_MAX_RUNNING_BILL";
        }
        if (actionBill.equals(BILL_JOB_START_HIGH_EXPEDITED)) {
            return "EJ_HIGH_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_HIGH_EXPEDITED)) {
            return "EJ_HIGH_RUNNING_BILL";
        }
        if (actionBill.equals(BILL_JOB_START_HIGH)) {
            return "HIGH_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_HIGH)) {
            return "HIGH_RUNNING_BILL";
        }
        if (actionBill.equals(BILL_JOB_START_DEFAULT)) {
            return "DEFAULT_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_DEFAULT)) {
            return "DEFAULT_RUNNING_BILL";
        }
        if (actionBill.equals(BILL_JOB_START_LOW)) {
            return "LOW_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_LOW)) {
            return "LOW_RUNNING_BILL";
        }
        if (actionBill.equals(BILL_JOB_START_MIN)) {
            return "MIN_START_BILL";
        }
        if (actionBill.equals(BILL_JOB_RUNNING_MIN)) {
            return "MIN_RUNNING_BILL";
        }
        return "UNKNOWN_BILL (" + actionBill + ")";
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(final IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        indentingPrintWriter.print("Is enabled: ");
        indentingPrintWriter.println(this.mIsEnabled);
        indentingPrintWriter.println("Affordability cache:");
        indentingPrintWriter.increaseIndent();
        this.mAffordabilityCache.forEach(new SparseArrayMap.TriConsumer() { // from class: com.android.server.job.controllers.TareController$$ExternalSyntheticLambda1
            public final void accept(int i, Object obj, Object obj2) {
                TareController.this.lambda$dumpControllerStateLocked$3(indentingPrintWriter, i, (String) obj, (ArrayMap) obj2);
            }
        });
        indentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dumpControllerStateLocked$3(IndentingPrintWriter indentingPrintWriter, int i, String str, ArrayMap arrayMap) {
        int size = arrayMap.size();
        if (size > 0) {
            indentingPrintWriter.print(i);
            indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.print(str);
            indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            for (int i2 = 0; i2 < size; i2++) {
                indentingPrintWriter.print(getBillName((EconomyManagerInternal.ActionBill) arrayMap.keyAt(i2)));
                indentingPrintWriter.print(": ");
                indentingPrintWriter.println(arrayMap.valueAt(i2));
            }
            indentingPrintWriter.decreaseIndent();
        }
    }
}
