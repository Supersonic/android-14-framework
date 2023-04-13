package com.android.server;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.AsyncTask;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class PruneInstantAppsJobService extends JobService {
    public static final long PRUNE_INSTANT_APPS_PERIOD_MILLIS = TimeUnit.DAYS.toMillis(1);

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void schedule(Context context) {
        ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(new JobInfo.Builder(765123, new ComponentName(context.getPackageName(), PruneInstantAppsJobService.class.getName())).setRequiresDeviceIdle(true).setPeriodic(PRUNE_INSTANT_APPS_PERIOD_MILLIS).build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        AsyncTask.execute(new Runnable() { // from class: com.android.server.PruneInstantAppsJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PruneInstantAppsJobService.this.lambda$onStartJob$0(jobParameters);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters) {
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).pruneInstantApps();
        jobFinished(jobParameters, false);
    }
}
