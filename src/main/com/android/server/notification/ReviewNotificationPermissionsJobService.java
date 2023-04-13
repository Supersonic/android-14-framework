package com.android.server.notification;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
/* loaded from: classes2.dex */
public class ReviewNotificationPermissionsJobService extends JobService {
    @VisibleForTesting
    protected static final int JOB_ID = 225373531;

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public static void scheduleJob(Context context, long j) {
        ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(new JobInfo.Builder(JOB_ID, new ComponentName(context, ReviewNotificationPermissionsJobService.class)).setPersisted(true).setMinimumLatency(j).build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        ((NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class)).sendReviewPermissionsNotification();
        return false;
    }
}
