package android.app.job;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class JobScheduler {
    public static final int PENDING_JOB_REASON_APP = 1;
    public static final int PENDING_JOB_REASON_APP_STANDBY = 2;
    public static final int PENDING_JOB_REASON_BACKGROUND_RESTRICTION = 3;
    public static final int PENDING_JOB_REASON_CONSTRAINT_BATTERY_NOT_LOW = 4;
    public static final int PENDING_JOB_REASON_CONSTRAINT_CHARGING = 5;
    public static final int PENDING_JOB_REASON_CONSTRAINT_CONNECTIVITY = 6;
    public static final int PENDING_JOB_REASON_CONSTRAINT_CONTENT_TRIGGER = 7;
    public static final int PENDING_JOB_REASON_CONSTRAINT_DEVICE_IDLE = 8;
    public static final int PENDING_JOB_REASON_CONSTRAINT_MINIMUM_LATENCY = 9;
    public static final int PENDING_JOB_REASON_CONSTRAINT_PREFETCH = 10;
    public static final int PENDING_JOB_REASON_CONSTRAINT_STORAGE_NOT_LOW = 11;
    public static final int PENDING_JOB_REASON_DEVICE_STATE = 12;
    public static final int PENDING_JOB_REASON_EXECUTING = -1;
    public static final int PENDING_JOB_REASON_INVALID_JOB_ID = -2;
    public static final int PENDING_JOB_REASON_JOB_SCHEDULER_OPTIMIZATION = 13;
    public static final int PENDING_JOB_REASON_QUOTA = 14;
    public static final int PENDING_JOB_REASON_UNDEFINED = 0;
    public static final int PENDING_JOB_REASON_USER = 15;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final long THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION = 255371817;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PendingJobReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Result {
    }

    public abstract void cancel(int i);

    public abstract void cancelAll();

    public abstract int enqueue(JobInfo jobInfo, JobWorkItem jobWorkItem);

    public abstract List<JobSnapshot> getAllJobSnapshots();

    public abstract List<JobInfo> getAllPendingJobs();

    public abstract JobInfo getPendingJob(int i);

    public abstract List<JobInfo> getStartedJobs();

    public abstract void notePendingUserRequestedAppStop(String str, int i, String str2);

    public abstract void registerUserVisibleJobObserver(IUserVisibleJobObserver iUserVisibleJobObserver);

    public abstract int schedule(JobInfo jobInfo);

    @SystemApi
    public abstract int scheduleAsPackage(JobInfo jobInfo, String str, int i, String str2);

    public abstract void unregisterUserVisibleJobObserver(IUserVisibleJobObserver iUserVisibleJobObserver);

    public JobScheduler forNamespace(String namespace) {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    public String getNamespace() {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    public void cancelInAllNamespaces() {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    public Map<String, List<JobInfo>> getPendingJobsInAllNamespaces() {
        throw new RuntimeException("Not implemented. Must override in a subclass.");
    }

    public int getPendingJobReason(int jobId) {
        return 0;
    }

    public boolean canRunUserInitiatedJobs() {
        return false;
    }

    public boolean hasRunUserInitiatedJobsPermission(String packageName, int userId) {
        return false;
    }
}
