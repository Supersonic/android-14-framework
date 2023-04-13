package com.android.server.net.watchlist;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.NetworkWatchlistManager;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class ReportWatchlistJobService extends JobService {
    public static final long REPORT_WATCHLIST_RECORDS_PERIOD_MILLIS = TimeUnit.HOURS.toMillis(12);

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() != 882313) {
            return false;
        }
        new NetworkWatchlistManager(this).reportWatchlistIfNecessary();
        jobFinished(jobParameters, false);
        return true;
    }

    public static void schedule(Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(882313, new ComponentName(context, ReportWatchlistJobService.class)).setPeriodic(REPORT_WATCHLIST_RECORDS_PERIOD_MILLIS).setRequiresDeviceIdle(true).setRequiresBatteryNotLow(true).setPersisted(false).build());
    }
}
