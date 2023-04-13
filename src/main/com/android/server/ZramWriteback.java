package com.android.server;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.FileUtils;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public final class ZramWriteback extends JobService {
    public static final ComponentName sZramWriteback = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, ZramWriteback.class.getName());
    public static int sZramDeviceId = 0;

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public final void markPagesAsIdle() {
        String format = String.format("/sys/block/zram%d/idle", Integer.valueOf(sZramDeviceId));
        try {
            FileUtils.stringToFile(new File(format), "all");
        } catch (IOException unused) {
            Slog.e("ZramWriteback", "Failed to write to " + format);
        }
    }

    public final void flushIdlePages() {
        String format = String.format("/sys/block/zram%d/writeback", Integer.valueOf(sZramDeviceId));
        try {
            FileUtils.stringToFile(new File(format), "idle");
        } catch (IOException unused) {
            Slog.e("ZramWriteback", "Failed to write to " + format);
        }
    }

    public final int getWrittenPageCount() {
        String format = String.format("/sys/block/zram%d/bd_stat", Integer.valueOf(sZramDeviceId));
        try {
            return Integer.parseInt(FileUtils.readTextFile(new File(format), 128, "").trim().split("\\s+")[2], 10);
        } catch (IOException unused) {
            Slog.e("ZramWriteback", "Failed to read writeback stats from " + format);
            return -1;
        }
    }

    public final void markAndFlushPages() {
        int writtenPageCount = getWrittenPageCount();
        flushIdlePages();
        markPagesAsIdle();
        if (writtenPageCount != -1) {
            Slog.i("ZramWriteback", "Total pages written to disk is " + (getWrittenPageCount() - writtenPageCount));
        }
    }

    public static boolean isWritebackEnabled() {
        try {
        } catch (IOException unused) {
            Slog.w("ZramWriteback", "Writeback is not enabled on zram");
        }
        if ("none".equals(FileUtils.readTextFile(new File(String.format("/sys/block/zram%d/backing_dev", Integer.valueOf(sZramDeviceId))), 128, "").trim())) {
            Slog.w("ZramWriteback", "Writeback device is not set");
            return false;
        }
        return true;
    }

    public static void schedNextWriteback(Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(812, sZramWriteback).setMinimumLatency(TimeUnit.HOURS.toMillis(SystemProperties.getInt("ro.zram.periodic_wb_delay_hours", 24))).setRequiresDeviceIdle(!SystemProperties.getBoolean("zram.force_writeback", false)).build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        if (!isWritebackEnabled()) {
            jobFinished(jobParameters, false);
            return false;
        } else if (jobParameters.getJobId() == 811) {
            markPagesAsIdle();
            jobFinished(jobParameters, false);
            return false;
        } else {
            new Thread("ZramWriteback_WritebackIdlePages") { // from class: com.android.server.ZramWriteback.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    ZramWriteback.this.markAndFlushPages();
                    ZramWriteback.schedNextWriteback(ZramWriteback.this);
                    ZramWriteback.this.jobFinished(jobParameters, false);
                }
            }.start();
            return true;
        }
    }

    public static void scheduleZramWriteback(Context context) {
        int i = SystemProperties.getInt("ro.zram.mark_idle_delay_mins", 20);
        int i2 = SystemProperties.getInt("ro.zram.first_wb_delay_mins", (int) FrameworkStatsLog.f369xce0f313f);
        boolean z = SystemProperties.getBoolean("zram.force_writeback", false);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        ComponentName componentName = sZramWriteback;
        JobInfo.Builder builder = new JobInfo.Builder(811, componentName);
        TimeUnit timeUnit = TimeUnit.MINUTES;
        long j = i;
        jobScheduler.schedule(builder.setMinimumLatency(timeUnit.toMillis(j)).setOverrideDeadline(timeUnit.toMillis(j)).build());
        jobScheduler.schedule(new JobInfo.Builder(812, componentName).setMinimumLatency(timeUnit.toMillis(i2)).setRequiresDeviceIdle(!z).build());
    }
}
