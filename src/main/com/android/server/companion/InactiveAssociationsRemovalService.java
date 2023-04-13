package com.android.server.companion;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Slog;
import com.android.server.LocalServices;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class InactiveAssociationsRemovalService extends JobService {
    public static final int JOB_ID = InactiveAssociationsRemovalService.class.hashCode();
    public static final long ONE_DAY_INTERVAL = TimeUnit.DAYS.toMillis(1);

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        Slog.i("CDM_CompanionDeviceManagerService", "Execute the Association Removal job");
        ((CompanionDeviceManagerServiceInternal) LocalServices.getService(CompanionDeviceManagerServiceInternal.class)).removeInactiveSelfManagedAssociations();
        jobFinished(jobParameters, false);
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        Slog.i("CDM_CompanionDeviceManagerService", "Association removal job stopped; id=" + jobParameters.getJobId() + ", reason=" + JobParameters.getInternalReasonCodeDescription(jobParameters.getInternalStopReasonCode()));
        return false;
    }

    public static void schedule(Context context) {
        Slog.i("CDM_CompanionDeviceManagerService", "Scheduling the Association Removal job");
        ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(new JobInfo.Builder(JOB_ID, new ComponentName(context, InactiveAssociationsRemovalService.class)).setRequiresCharging(true).setRequiresDeviceIdle(true).setPeriodic(ONE_DAY_INTERVAL).build());
    }
}
