package com.android.server.backup;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
/* loaded from: classes.dex */
public class FullBackupJob extends JobService {
    @VisibleForTesting
    public static final int MAX_JOB_ID = 52419896;
    @VisibleForTesting
    public static final int MIN_JOB_ID = 52418896;
    public static ComponentName sIdleService = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, FullBackupJob.class.getName());
    @GuardedBy({"mParamsForUser"})
    public final SparseArray<JobParameters> mParamsForUser = new SparseArray<>();

    public static void schedule(int i, Context context, long j, UserBackupManagerService userBackupManagerService) {
        if (userBackupManagerService.isFrameworkSchedulingEnabled()) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
            JobInfo.Builder builder = new JobInfo.Builder(getJobIdForUserId(i), sIdleService);
            BackupManagerConstants constants = userBackupManagerService.getConstants();
            synchronized (constants) {
                builder.setRequiresDeviceIdle(true).setRequiredNetworkType(constants.getFullBackupRequiredNetworkType()).setRequiresCharging(constants.getFullBackupRequireCharging());
            }
            if (j > 0) {
                builder.setMinimumLatency(j);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("userId", i);
            builder.setTransientExtras(bundle);
            jobScheduler.schedule(builder.build());
        }
    }

    public static void cancel(int i, Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).cancel(getJobIdForUserId(i));
    }

    public void finishBackupPass(int i) {
        synchronized (this.mParamsForUser) {
            JobParameters jobParameters = this.mParamsForUser.get(i);
            if (jobParameters != null) {
                jobFinished(jobParameters, false);
                this.mParamsForUser.remove(i);
            }
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        int i = jobParameters.getTransientExtras().getInt("userId");
        synchronized (this.mParamsForUser) {
            this.mParamsForUser.put(i, jobParameters);
        }
        return BackupManagerService.getInstance().beginFullBackup(i, this);
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        int i = jobParameters.getTransientExtras().getInt("userId");
        synchronized (this.mParamsForUser) {
            if (this.mParamsForUser.removeReturnOld(i) == null) {
                return false;
            }
            BackupManagerService.getInstance().endFullBackup(i);
            return false;
        }
    }

    public static int getJobIdForUserId(int i) {
        return JobIdManager.getJobIdForUserId(52418896, MAX_JOB_ID, i);
    }
}
