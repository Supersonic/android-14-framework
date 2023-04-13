package com.android.server.job.controllers;

import android.app.AlarmManager;
import android.os.UserHandle;
import android.os.WorkSource;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.expresslog.Counter;
import com.android.server.job.JobSchedulerService;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class TimeController extends StateController {
    public static final boolean DEBUG;
    @VisibleForTesting
    static final long DELAY_COALESCE_TIME_MS = 30000;
    public final String DEADLINE_TAG;
    public final String DELAY_TAG;
    public AlarmManager mAlarmService;
    public final AlarmManager.OnAlarmListener mDeadlineExpiredListener;
    public volatile long mLastFiredDelayExpiredElapsedMillis;
    public long mNextDelayExpiredElapsedMillis;
    public final AlarmManager.OnAlarmListener mNextDelayExpiredListener;
    public long mNextJobExpiredElapsedMillis;
    public final List<JobStatus> mTrackedJobs;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.Time", 3);
    }

    public TimeController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
        this.DEADLINE_TAG = "*job.deadline*";
        this.DELAY_TAG = "*job.delay*";
        this.mAlarmService = null;
        this.mTrackedJobs = new LinkedList();
        this.mDeadlineExpiredListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.job.controllers.TimeController.1
            @Override // android.app.AlarmManager.OnAlarmListener
            public void onAlarm() {
                if (TimeController.DEBUG) {
                    Slog.d("JobScheduler.Time", "Deadline-expired alarm fired");
                }
                TimeController.this.checkExpiredDeadlinesAndResetAlarm();
            }
        };
        this.mNextDelayExpiredListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.job.controllers.TimeController.2
            @Override // android.app.AlarmManager.OnAlarmListener
            public void onAlarm() {
                if (TimeController.DEBUG) {
                    Slog.d("JobScheduler.Time", "Delay-expired alarm fired");
                }
                TimeController.this.mLastFiredDelayExpiredElapsedMillis = JobSchedulerService.sElapsedRealtimeClock.millis();
                TimeController.this.checkExpiredDelaysAndResetAlarm();
            }
        };
        this.mNextJobExpiredElapsedMillis = Long.MAX_VALUE;
        this.mNextDelayExpiredElapsedMillis = Long.MAX_VALUE;
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        boolean z;
        if (jobStatus.hasTimingDelayConstraint() || jobStatus.hasDeadlineConstraint()) {
            maybeStopTrackingJobLocked(jobStatus, null);
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            if (jobStatus.hasDeadlineConstraint() && evaluateDeadlineConstraint(jobStatus, millis)) {
                return;
            }
            if (jobStatus.hasTimingDelayConstraint() && evaluateTimingDelayConstraint(jobStatus, millis) && !jobStatus.hasDeadlineConstraint()) {
                return;
            }
            List<JobStatus> list = this.mTrackedJobs;
            ListIterator<JobStatus> listIterator = list.listIterator(list.size());
            while (true) {
                if (!listIterator.hasPrevious()) {
                    z = false;
                    break;
                } else if (listIterator.previous().getLatestRunTimeElapsed() < jobStatus.getLatestRunTimeElapsed()) {
                    z = true;
                    break;
                }
            }
            if (z) {
                listIterator.next();
            }
            listIterator.add(jobStatus);
            jobStatus.setTrackingController(32);
            WorkSource deriveWorkSource = this.mService.deriveWorkSource(jobStatus.getSourceUid(), jobStatus.getSourcePackageName());
            if (jobStatus.hasTimingDelayConstraint() && wouldBeReadyWithConstraintLocked(jobStatus, Integer.MIN_VALUE)) {
                maybeUpdateDelayAlarmLocked(jobStatus.getEarliestRunTime(), deriveWorkSource);
            }
            if (jobStatus.hasDeadlineConstraint() && wouldBeReadyWithConstraintLocked(jobStatus, 1073741824)) {
                maybeUpdateDeadlineAlarmLocked(jobStatus.getLatestRunTimeElapsed(), deriveWorkSource);
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.clearTrackingController(32) && this.mTrackedJobs.remove(jobStatus)) {
            checkExpiredDelaysAndResetAlarm();
            checkExpiredDeadlinesAndResetAlarm();
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void evaluateStateLocked(JobStatus jobStatus) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (jobStatus.hasDeadlineConstraint() && !jobStatus.isConstraintSatisfied(1073741824) && jobStatus.getLatestRunTimeElapsed() <= this.mNextJobExpiredElapsedMillis) {
            if (evaluateDeadlineConstraint(jobStatus, millis)) {
                if (jobStatus.isReady()) {
                    this.mStateChangedListener.onRunJobNow(jobStatus);
                }
                this.mTrackedJobs.remove(jobStatus);
                Counter.logIncrement("job_scheduler.value_job_scheduler_job_deadline_expired_counter");
            } else if (wouldBeReadyWithConstraintLocked(jobStatus, 1073741824)) {
                setDeadlineExpiredAlarmLocked(jobStatus.getLatestRunTimeElapsed(), this.mService.deriveWorkSource(jobStatus.getSourceUid(), jobStatus.getSourcePackageName()));
            }
        }
        if (!jobStatus.hasTimingDelayConstraint() || jobStatus.isConstraintSatisfied(Integer.MIN_VALUE) || jobStatus.getEarliestRunTime() > this.mNextDelayExpiredElapsedMillis) {
            return;
        }
        if (evaluateTimingDelayConstraint(jobStatus, millis)) {
            if (canStopTrackingJobLocked(jobStatus)) {
                this.mTrackedJobs.remove(jobStatus);
            }
        } else if (wouldBeReadyWithConstraintLocked(jobStatus, Integer.MIN_VALUE)) {
            setDelayExpiredAlarmLocked(jobStatus.getEarliestRunTime(), this.mService.deriveWorkSource(jobStatus.getSourceUid(), jobStatus.getSourcePackageName()));
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void reevaluateStateLocked(int i) {
        checkExpiredDeadlinesAndResetAlarm();
        checkExpiredDelaysAndResetAlarm();
    }

    public final boolean canStopTrackingJobLocked(JobStatus jobStatus) {
        return (!jobStatus.hasTimingDelayConstraint() || jobStatus.isConstraintSatisfied(Integer.MIN_VALUE)) && (!jobStatus.hasDeadlineConstraint() || jobStatus.isConstraintSatisfied(1073741824));
    }

    public final void ensureAlarmServiceLocked() {
        if (this.mAlarmService == null) {
            this.mAlarmService = (AlarmManager) this.mContext.getSystemService("alarm");
        }
    }

    @VisibleForTesting
    public void checkExpiredDeadlinesAndResetAlarm() {
        long j;
        int i;
        String str;
        synchronized (this.mLock) {
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            ListIterator<JobStatus> listIterator = this.mTrackedJobs.listIterator();
            while (true) {
                if (!listIterator.hasNext()) {
                    j = Long.MAX_VALUE;
                    i = 0;
                    str = null;
                    break;
                }
                JobStatus next = listIterator.next();
                if (next.hasDeadlineConstraint()) {
                    if (evaluateDeadlineConstraint(next, millis)) {
                        if (next.isReady()) {
                            this.mStateChangedListener.onRunJobNow(next);
                        }
                        Counter.logIncrement("job_scheduler.value_job_scheduler_job_deadline_expired_counter");
                        listIterator.remove();
                    } else if (!wouldBeReadyWithConstraintLocked(next, 1073741824)) {
                        if (DEBUG) {
                            Slog.i("JobScheduler.Time", "Skipping " + next + " because deadline won't make it ready.");
                        }
                    } else {
                        j = next.getLatestRunTimeElapsed();
                        i = next.getSourceUid();
                        str = next.getSourcePackageName();
                        break;
                    }
                }
            }
            setDeadlineExpiredAlarmLocked(j, this.mService.deriveWorkSource(i, str));
        }
    }

    public final boolean evaluateDeadlineConstraint(JobStatus jobStatus, long j) {
        if (jobStatus.getLatestRunTimeElapsed() <= j) {
            if (jobStatus.hasTimingDelayConstraint()) {
                jobStatus.setTimingDelayConstraintSatisfied(j, true);
            }
            jobStatus.setDeadlineConstraintSatisfied(j, true);
            return true;
        }
        return false;
    }

    @VisibleForTesting
    public void checkExpiredDelaysAndResetAlarm() {
        synchronized (this.mLock) {
            ArraySet<JobStatus> arraySet = new ArraySet<>();
            Iterator<JobStatus> it = this.mTrackedJobs.iterator();
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            long j = Long.MAX_VALUE;
            int i = 0;
            String str = null;
            while (it.hasNext()) {
                JobStatus next = it.next();
                if (next.hasTimingDelayConstraint()) {
                    if (evaluateTimingDelayConstraint(next, millis)) {
                        if (canStopTrackingJobLocked(next)) {
                            it.remove();
                        }
                        arraySet.add(next);
                    } else if (!wouldBeReadyWithConstraintLocked(next, Integer.MIN_VALUE)) {
                        if (DEBUG) {
                            Slog.i("JobScheduler.Time", "Skipping " + next + " because delay won't make it ready.");
                        }
                    } else {
                        long earliestRunTime = next.getEarliestRunTime();
                        if (j > earliestRunTime) {
                            i = next.getSourceUid();
                            str = next.getSourcePackageName();
                            j = earliestRunTime;
                        }
                    }
                }
            }
            if (arraySet.size() > 0) {
                this.mStateChangedListener.onControllerStateChanged(arraySet);
            }
            setDelayExpiredAlarmLocked(j, this.mService.deriveWorkSource(i, str));
        }
    }

    public final boolean evaluateTimingDelayConstraint(JobStatus jobStatus, long j) {
        if (jobStatus.getEarliestRunTime() <= j) {
            jobStatus.setTimingDelayConstraintSatisfied(j, true);
            return true;
        }
        return false;
    }

    public final void maybeUpdateDelayAlarmLocked(long j, WorkSource workSource) {
        if (j < this.mNextDelayExpiredElapsedMillis) {
            setDelayExpiredAlarmLocked(j, workSource);
        }
    }

    public final void maybeUpdateDeadlineAlarmLocked(long j, WorkSource workSource) {
        if (j < this.mNextJobExpiredElapsedMillis) {
            setDeadlineExpiredAlarmLocked(j, workSource);
        }
    }

    public final void setDelayExpiredAlarmLocked(long j, WorkSource workSource) {
        long maybeAdjustAlarmTime = maybeAdjustAlarmTime(Math.max(j, this.mLastFiredDelayExpiredElapsedMillis + 30000));
        if (this.mNextDelayExpiredElapsedMillis == maybeAdjustAlarmTime) {
            return;
        }
        this.mNextDelayExpiredElapsedMillis = maybeAdjustAlarmTime;
        updateAlarmWithListenerLocked("*job.delay*", 3, this.mNextDelayExpiredListener, maybeAdjustAlarmTime, workSource);
    }

    public final void setDeadlineExpiredAlarmLocked(long j, WorkSource workSource) {
        long maybeAdjustAlarmTime = maybeAdjustAlarmTime(j);
        if (this.mNextJobExpiredElapsedMillis == maybeAdjustAlarmTime) {
            return;
        }
        this.mNextJobExpiredElapsedMillis = maybeAdjustAlarmTime;
        updateAlarmWithListenerLocked("*job.deadline*", 2, this.mDeadlineExpiredListener, maybeAdjustAlarmTime, workSource);
    }

    public final long maybeAdjustAlarmTime(long j) {
        return Math.max(j, JobSchedulerService.sElapsedRealtimeClock.millis());
    }

    public final void updateAlarmWithListenerLocked(String str, int i, AlarmManager.OnAlarmListener onAlarmListener, long j, WorkSource workSource) {
        ensureAlarmServiceLocked();
        if (j == Long.MAX_VALUE) {
            this.mAlarmService.cancel(onAlarmListener);
            return;
        }
        if (DEBUG) {
            Slog.d("JobScheduler.Time", "Setting " + str + " for: " + j);
        }
        this.mAlarmService.set(i, j, -1L, 0L, str, onAlarmListener, null, workSource);
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        indentingPrintWriter.println("Elapsed clock: " + millis);
        indentingPrintWriter.print("Next delay alarm in ");
        TimeUtils.formatDuration(this.mNextDelayExpiredElapsedMillis, millis, indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.print("Last delay alarm fired @ ");
        TimeUtils.formatDuration(millis, this.mLastFiredDelayExpiredElapsedMillis, indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.print("Next deadline alarm in ");
        TimeUtils.formatDuration(this.mNextJobExpiredElapsedMillis, millis, indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.println();
        for (JobStatus jobStatus : this.mTrackedJobs) {
            if (predicate.test(jobStatus)) {
                indentingPrintWriter.print("#");
                jobStatus.printUniqueId(indentingPrintWriter);
                indentingPrintWriter.print(" from ");
                UserHandle.formatUid(indentingPrintWriter, jobStatus.getSourceUid());
                indentingPrintWriter.print(": Delay=");
                if (jobStatus.hasTimingDelayConstraint()) {
                    TimeUtils.formatDuration(jobStatus.getEarliestRunTime(), millis, indentingPrintWriter);
                } else {
                    indentingPrintWriter.print("N/A");
                }
                indentingPrintWriter.print(", Deadline=");
                if (jobStatus.hasDeadlineConstraint()) {
                    TimeUtils.formatDuration(jobStatus.getLatestRunTimeElapsed(), millis, indentingPrintWriter);
                } else {
                    indentingPrintWriter.print("N/A");
                }
                indentingPrintWriter.println();
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268040L);
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        protoOutputStream.write(1112396529665L, millis);
        protoOutputStream.write(1112396529666L, this.mNextDelayExpiredElapsedMillis - millis);
        protoOutputStream.write(1112396529667L, this.mNextJobExpiredElapsedMillis - millis);
        for (JobStatus jobStatus : this.mTrackedJobs) {
            if (predicate.test(jobStatus)) {
                long start3 = protoOutputStream.start(2246267895812L);
                jobStatus.writeToShortProto(protoOutputStream, 1146756268033L);
                protoOutputStream.write(1133871366147L, jobStatus.hasTimingDelayConstraint());
                protoOutputStream.write(1112396529668L, jobStatus.getEarliestRunTime() - millis);
                protoOutputStream.write(1133871366149L, jobStatus.hasDeadlineConstraint());
                protoOutputStream.write(1112396529670L, jobStatus.getLatestRunTimeElapsed() - millis);
                protoOutputStream.end(start3);
            }
        }
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }
}
