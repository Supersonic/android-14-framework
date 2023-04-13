package com.android.server;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Slog;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class SmartStorageMaintIdler extends JobService {
    public static final ComponentName SMART_STORAGE_MAINT_SERVICE = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, SmartStorageMaintIdler.class.getName());
    public final Runnable mFinishCallback = new Runnable() { // from class: com.android.server.SmartStorageMaintIdler.1
        @Override // java.lang.Runnable
        public void run() {
            Slog.i("SmartStorageMaintIdler", "Got smart storage maintenance service completion callback");
            if (SmartStorageMaintIdler.this.mStarted) {
                SmartStorageMaintIdler smartStorageMaintIdler = SmartStorageMaintIdler.this;
                smartStorageMaintIdler.jobFinished(smartStorageMaintIdler.mJobParams, false);
                SmartStorageMaintIdler.this.mStarted = false;
            }
            SmartStorageMaintIdler.scheduleSmartIdlePass(SmartStorageMaintIdler.this, StorageManagerService.sSmartIdleMaintPeriod);
        }
    };
    public JobParameters mJobParams;
    public boolean mStarted;

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        this.mJobParams = jobParameters;
        StorageManagerService storageManagerService = StorageManagerService.sSelf;
        if (storageManagerService != null) {
            this.mStarted = true;
            storageManagerService.runSmartIdleMaint(this.mFinishCallback);
        }
        return storageManagerService != null;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        this.mStarted = false;
        return false;
    }

    public static void scheduleSmartIdlePass(Context context, int i) {
        StorageManagerService storageManagerService = StorageManagerService.sSelf;
        if (storageManagerService == null || storageManagerService.isPassedLifetimeThresh()) {
            return;
        }
        long millis = TimeUnit.MINUTES.toMillis(i);
        JobInfo.Builder builder = new JobInfo.Builder(2808, SMART_STORAGE_MAINT_SERVICE);
        builder.setMinimumLatency(millis);
        ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(builder.build());
    }
}
