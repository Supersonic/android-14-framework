package com.android.server.storage;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.UserHandle;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.storage.FileCollector;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class DiskStatsLoggingService extends JobService {
    public static ComponentName sDiskStatsLoggingService = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, DiskStatsLoggingService.class.getName());

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        if (!isCharging(this) || !isDumpsysTaskEnabled(getContentResolver())) {
            jobFinished(jobParameters, true);
            return false;
        }
        VolumeInfo primaryStorageCurrentVolume = getPackageManager().getPrimaryStorageCurrentVolume();
        if (primaryStorageCurrentVolume == null) {
            return false;
        }
        AppCollector appCollector = new AppCollector(this, primaryStorageCurrentVolume);
        Environment.UserEnvironment userEnvironment = new Environment.UserEnvironment(UserHandle.myUserId());
        LogRunnable logRunnable = new LogRunnable();
        logRunnable.setDownloadsDirectory(userEnvironment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        logRunnable.setSystemSize(FileCollector.getSystemSize(this));
        logRunnable.setLogOutputFile(new File("/data/system/diskstats_cache.json"));
        logRunnable.setAppCollector(appCollector);
        logRunnable.setJobService(this, jobParameters);
        logRunnable.setContext(this);
        AsyncTask.execute(logRunnable);
        return true;
    }

    public static void schedule(Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(1145656139, sDiskStatsLoggingService).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(TimeUnit.DAYS.toMillis(1L)).build());
    }

    public static boolean isCharging(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService("batterymanager");
        if (batteryManager != null) {
            return batteryManager.isCharging();
        }
        return false;
    }

    @VisibleForTesting
    public static boolean isDumpsysTaskEnabled(ContentResolver contentResolver) {
        return Settings.Global.getInt(contentResolver, "enable_diskstats_logging", 1) != 0;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class LogRunnable implements Runnable {
        public static final long TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(10);
        public AppCollector mCollector;
        public Context mContext;
        public File mDownloadsDirectory;
        public JobService mJobService;
        public File mOutputFile;
        public JobParameters mParams;
        public long mSystemSize;

        public void setDownloadsDirectory(File file) {
            this.mDownloadsDirectory = file;
        }

        public void setAppCollector(AppCollector appCollector) {
            this.mCollector = appCollector;
        }

        public void setLogOutputFile(File file) {
            this.mOutputFile = file;
        }

        public void setSystemSize(long j) {
            this.mSystemSize = j;
        }

        public void setContext(Context context) {
            this.mContext = context;
        }

        public void setJobService(JobService jobService, JobParameters jobParameters) {
            this.mJobService = jobService;
            this.mParams = jobParameters;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z = true;
            try {
                FileCollector.MeasurementResult measurementResult = FileCollector.getMeasurementResult(this.mContext);
                FileCollector.MeasurementResult measurementResult2 = FileCollector.getMeasurementResult(this.mDownloadsDirectory);
                List<PackageStats> packageStats = this.mCollector.getPackageStats(TIMEOUT_MILLIS);
                if (packageStats != null) {
                    logToFile(measurementResult, measurementResult2, packageStats, this.mSystemSize);
                    z = false;
                } else {
                    Log.w("DiskStatsLogService", "Timed out while fetching package stats.");
                }
                finishJob(z);
            } catch (IllegalStateException e) {
                Log.e("DiskStatsLogService", "Error while measuring storage", e);
                finishJob(true);
            }
        }

        public final void logToFile(FileCollector.MeasurementResult measurementResult, FileCollector.MeasurementResult measurementResult2, List<PackageStats> list, long j) {
            DiskStatsFileLogger diskStatsFileLogger = new DiskStatsFileLogger(measurementResult, measurementResult2, list, j);
            try {
                this.mOutputFile.createNewFile();
                diskStatsFileLogger.dumpToFile(this.mOutputFile);
            } catch (IOException e) {
                Log.e("DiskStatsLogService", "Exception while writing opportunistic disk file cache.", e);
            }
        }

        public final void finishJob(boolean z) {
            JobService jobService = this.mJobService;
            if (jobService != null) {
                jobService.jobFinished(this.mParams, z);
            }
        }
    }
}
