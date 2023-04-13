package com.android.server;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.SystemProperties;
import android.util.Slog;
/* loaded from: classes.dex */
public class PreloadsFileCacheExpirationJobService extends JobService {
    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        SystemProperties.set("persist.sys.preloads.file_cache_expired", "1");
        Slog.i("PreloadsFileCacheExpirationJobService", "Set persist.sys.preloads.file_cache_expired=1");
        return false;
    }
}
