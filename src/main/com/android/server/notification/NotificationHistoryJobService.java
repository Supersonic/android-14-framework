package com.android.server.notification;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.CancellationSignal;
import android.util.Slog;
import com.android.server.LocalServices;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class NotificationHistoryJobService extends JobService {
    public static final long JOB_RUN_INTERVAL = TimeUnit.MINUTES.toMillis(20);
    public CancellationSignal mSignal;

    public static void scheduleJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        if (jobScheduler.getPendingJob(237039804) != null || jobScheduler.schedule(new JobInfo.Builder(237039804, new ComponentName(context, NotificationHistoryJobService.class)).setRequiresDeviceIdle(false).setPeriodic(JOB_RUN_INTERVAL).build()) == 1) {
            return;
        }
        Slog.w("NotificationHistoryJob", "Failed to schedule history cleanup job");
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        this.mSignal = new CancellationSignal();
        new Thread(new Runnable() { // from class: com.android.server.notification.NotificationHistoryJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NotificationHistoryJobService.this.lambda$onStartJob$0(jobParameters);
            }
        }).start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(JobParameters jobParameters) {
        ((NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class)).cleanupHistoryFiles();
        jobFinished(jobParameters, this.mSignal.isCanceled());
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        CancellationSignal cancellationSignal = this.mSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            return false;
        }
        return false;
    }
}
