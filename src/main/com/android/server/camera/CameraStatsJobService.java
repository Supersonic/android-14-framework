package com.android.server.camera;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class CameraStatsJobService extends JobService {
    public static ComponentName sCameraStatsJobServiceName = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, CameraStatsJobService.class.getName());

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        CameraServiceProxy cameraServiceProxy = (CameraServiceProxy) LocalServices.getService(CameraServiceProxy.class);
        if (cameraServiceProxy == null) {
            Slog.w("CameraStatsJobService", "Can't collect camera usage stats - no camera service proxy found");
            return false;
        }
        cameraServiceProxy.dumpUsageEvents();
        return false;
    }

    public static void schedule(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (jobScheduler == null) {
            Slog.e("CameraStatsJobService", "Can't collect camera usage stats - no Job Scheduler");
        } else {
            jobScheduler.schedule(new JobInfo.Builder(13254266, sCameraStatsJobServiceName).setMinimumLatency(TimeUnit.DAYS.toMillis(1L)).setRequiresDeviceIdle(true).build());
        }
    }
}
