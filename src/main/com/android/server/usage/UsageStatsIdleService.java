package com.android.server.usage;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import com.android.server.LocalServices;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class UsageStatsIdleService extends JobService {
    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void schedulePruneJob(Context context, int i) {
        ComponentName componentName = new ComponentName(context.getPackageName(), UsageStatsIdleService.class.getName());
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putInt("user_id", i);
        scheduleJobInternal(context, new JobInfo.Builder(i, componentName).setRequiresDeviceIdle(true).setExtras(persistableBundle).setPersisted(true).build(), "usagestats_prune", i);
    }

    public static void scheduleUpdateMappingsJob(Context context, int i) {
        ComponentName componentName = new ComponentName(context.getPackageName(), UsageStatsIdleService.class.getName());
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putInt("user_id", i);
        JobInfo.Builder persisted = new JobInfo.Builder(i, componentName).setPersisted(true);
        TimeUnit timeUnit = TimeUnit.DAYS;
        scheduleJobInternal(context, persisted.setMinimumLatency(timeUnit.toMillis(1L)).setOverrideDeadline(timeUnit.toMillis(2L)).setExtras(persistableBundle).build(), "usagestats_mapping", i);
    }

    public static void scheduleJobInternal(Context context, JobInfo jobInfo, String str, int i) {
        JobScheduler forNamespace = ((JobScheduler) context.getSystemService(JobScheduler.class)).forNamespace(str);
        if (jobInfo.equals(forNamespace.getPendingJob(i))) {
            return;
        }
        forNamespace.cancel(i);
        forNamespace.schedule(jobInfo);
    }

    public static void cancelPruneJob(Context context, int i) {
        cancelJobInternal(context, "usagestats_prune", i);
    }

    public static void cancelUpdateMappingsJob(Context context, int i) {
        cancelJobInternal(context, "usagestats_mapping", i);
    }

    public static void cancelJobInternal(Context context, String str, int i) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.forNamespace(str).cancel(i);
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        final int i = jobParameters.getExtras().getInt("user_id", -1);
        if (i == -1) {
            return false;
        }
        AsyncTask.execute(new Runnable() { // from class: com.android.server.usage.UsageStatsIdleService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UsageStatsIdleService.this.lambda$onStartJob$0(jobParameters, i);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters, int i) {
        UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if ("usagestats_mapping".equals(jobParameters.getJobNamespace())) {
            jobFinished(jobParameters, !usageStatsManagerInternal.updatePackageMappingsData(i));
        } else {
            jobFinished(jobParameters, !usageStatsManagerInternal.pruneUninstalledPackagesData(i));
        }
    }
}
