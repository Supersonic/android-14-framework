package com.android.server.content;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import com.android.internal.annotations.GuardedBy;
/* loaded from: classes.dex */
public class SyncJobService extends JobService {
    @GuardedBy({"sLock"})
    public static SyncJobService sInstance;
    public static final Object sLock = new Object();
    @GuardedBy({"sLock"})
    public static final SparseArray<JobParameters> sJobParamsMap = new SparseArray<>();
    @GuardedBy({"sLock"})
    public static final SparseBooleanArray sStartedSyncs = new SparseBooleanArray();
    @GuardedBy({"sLock"})
    public static final SparseLongArray sJobStartUptimes = new SparseLongArray();
    public static final SyncLogger sLogger = SyncLogger.getInstance();

    public final void updateInstance() {
        synchronized (SyncJobService.class) {
            sInstance = this;
        }
    }

    public static SyncJobService getInstance() {
        SyncJobService syncJobService;
        synchronized (sLock) {
            if (sInstance == null) {
                Slog.wtf("SyncManager", "sInstance == null");
            }
            syncJobService = sInstance;
        }
        return syncJobService;
    }

    public static boolean isReady() {
        boolean z;
        synchronized (sLock) {
            z = sInstance != null;
        }
        return z;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        updateInstance();
        SyncLogger syncLogger = sLogger;
        syncLogger.purgeOldLogs();
        SyncOperation maybeCreateFromJobExtras = SyncOperation.maybeCreateFromJobExtras(jobParameters.getExtras());
        if (maybeCreateFromJobExtras == null) {
            Slog.wtf("SyncManager", "Got invalid job " + jobParameters.getJobId());
            return false;
        }
        boolean readyToSync = SyncManager.readyToSync(maybeCreateFromJobExtras.target.userId);
        syncLogger.log("onStartJob() jobid=", Integer.valueOf(jobParameters.getJobId()), " op=", maybeCreateFromJobExtras, " readyToSync", Boolean.valueOf(readyToSync));
        if (!readyToSync) {
            jobFinished(jobParameters, !maybeCreateFromJobExtras.isPeriodic);
            return true;
        }
        boolean isLoggable = Log.isLoggable("SyncManager", 2);
        synchronized (sLock) {
            int jobId = jobParameters.getJobId();
            sJobParamsMap.put(jobId, jobParameters);
            sStartedSyncs.delete(jobId);
            sJobStartUptimes.put(jobId, SystemClock.uptimeMillis());
        }
        Message obtain = Message.obtain();
        obtain.what = 10;
        if (isLoggable) {
            Slog.v("SyncManager", "Got start job message " + maybeCreateFromJobExtras.target);
        }
        obtain.obj = maybeCreateFromJobExtras;
        SyncManager.sendMessage(obtain);
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "onStopJob called " + jobParameters.getJobId() + ", reason: " + jobParameters.getInternalStopReasonCode());
        }
        SyncOperation maybeCreateFromJobExtras = SyncOperation.maybeCreateFromJobExtras(jobParameters.getExtras());
        if (maybeCreateFromJobExtras == null) {
            Slog.wtf("SyncManager", "Got invalid job " + jobParameters.getJobId());
            return false;
        }
        boolean readyToSync = SyncManager.readyToSync(maybeCreateFromJobExtras.target.userId);
        SyncLogger syncLogger = sLogger;
        syncLogger.log("onStopJob() ", syncLogger.jobParametersToString(jobParameters), " readyToSync=", Boolean.valueOf(readyToSync));
        synchronized (sLock) {
            int jobId = jobParameters.getJobId();
            sJobParamsMap.remove(jobId);
            SparseLongArray sparseLongArray = sJobStartUptimes;
            long j = sparseLongArray.get(jobId);
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis - j > 60000 && readyToSync && !sStartedSyncs.get(jobId)) {
                wtf("Job " + jobId + " didn't start:  startUptime=" + j + " nowUptime=" + uptimeMillis + " params=" + jobParametersToString(jobParameters));
            }
            sStartedSyncs.delete(jobId);
            sparseLongArray.delete(jobId);
        }
        Message obtain = Message.obtain();
        obtain.what = 11;
        obtain.obj = maybeCreateFromJobExtras;
        obtain.arg1 = jobParameters.getInternalStopReasonCode() != 0 ? 1 : 0;
        obtain.arg2 = jobParameters.getInternalStopReasonCode() != 3 ? 0 : 1;
        SyncManager.sendMessage(obtain);
        return false;
    }

    public static void callJobFinished(int i, boolean z, String str) {
        SyncJobService syncJobService = getInstance();
        if (syncJobService != null) {
            syncJobService.callJobFinishedInner(i, z, str);
        }
    }

    public void callJobFinishedInner(int i, boolean z, String str) {
        synchronized (sLock) {
            SparseArray<JobParameters> sparseArray = sJobParamsMap;
            JobParameters jobParameters = sparseArray.get(i);
            SyncLogger syncLogger = sLogger;
            syncLogger.log("callJobFinished()", " jobid=", Integer.valueOf(i), " needsReschedule=", Boolean.valueOf(z), " ", syncLogger.jobParametersToString(jobParameters), " why=", str);
            if (jobParameters != null) {
                jobFinished(jobParameters, z);
                sparseArray.remove(i);
            } else {
                Slog.e("SyncManager", "Job params not found for " + String.valueOf(i));
            }
        }
    }

    public static void markSyncStarted(int i) {
        synchronized (sLock) {
            sStartedSyncs.put(i, true);
        }
    }

    public static String jobParametersToString(JobParameters jobParameters) {
        if (jobParameters == null) {
            return "job:null";
        }
        return "job:#" + jobParameters.getJobId() + ":sr=[" + jobParameters.getInternalStopReasonCode() + "/" + jobParameters.getDebugStopReason() + "]:" + SyncOperation.maybeCreateFromJobExtras(jobParameters.getExtras());
    }

    public static void wtf(String str) {
        sLogger.log(str);
        Slog.wtf("SyncManager", str);
    }
}
