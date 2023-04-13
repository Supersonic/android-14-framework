package com.android.server.p011pm;

import android.app.job.JobParameters;
import android.app.job.JobService;
/* renamed from: com.android.server.pm.BackgroundDexOptJobService */
/* loaded from: classes2.dex */
public final class BackgroundDexOptJobService extends JobService {
    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        return BackgroundDexOptService.getService().onStartJob(this, jobParameters);
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return BackgroundDexOptService.getService().onStopJob(this, jobParameters);
    }
}
