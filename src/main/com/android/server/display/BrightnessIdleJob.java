package com.android.server.display;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.display.DisplayManagerInternal;
import com.android.server.LocalServices;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class BrightnessIdleJob extends JobService {
    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        JobInfo pendingJob = jobScheduler.getPendingJob(3923512);
        JobInfo build = new JobInfo.Builder(3923512, new ComponentName(context, BrightnessIdleJob.class)).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(TimeUnit.HOURS.toMillis(24L)).build();
        if (pendingJob != null && !pendingJob.equals(build)) {
            jobScheduler.cancel(3923512);
            pendingJob = null;
        }
        if (pendingJob == null) {
            jobScheduler.schedule(build);
        }
    }

    public static void cancelJob(Context context) {
        ((JobScheduler) context.getSystemService(JobScheduler.class)).cancel(3923512);
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).persistBrightnessTrackerState();
        return false;
    }
}
