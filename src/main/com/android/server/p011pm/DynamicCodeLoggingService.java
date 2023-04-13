package com.android.server.p011pm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.Process;
import android.util.EventLog;
import android.util.Log;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.art.DexUseManagerLocal;
import com.android.server.art.model.DexContainerFileUseInfo;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.dex.DynamicCodeLogger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.HexEncoding;
/* renamed from: com.android.server.pm.DynamicCodeLoggingService */
/* loaded from: classes2.dex */
public class DynamicCodeLoggingService extends JobService {
    public static final String TAG = DynamicCodeLoggingService.class.getName();
    public static final long IDLE_LOGGING_PERIOD_MILLIS = TimeUnit.DAYS.toMillis(1);
    public static final long AUDIT_WATCHING_PERIOD_MILLIS = TimeUnit.HOURS.toMillis(2);
    public static final Pattern EXECUTE_NATIVE_AUDIT_PATTERN = Pattern.compile(".*\\bavc: granted \\{ execute(?:_no_trans|) \\} .*\\bpath=(?:\"([^\" ]*)\"|([0-9A-F]+)) .*\\bscontext=u:r:untrusted_app(?:_25|_27)?:.*\\btcontext=u:object_r:app_data_file:.*\\btclass=file\\b.*");
    public volatile boolean mIdleLoggingStopRequested = false;
    public volatile boolean mAuditWatchingStopRequested = false;

    public static void schedule(Context context) {
        ComponentName componentName = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, DynamicCodeLoggingService.class.getName());
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        jobScheduler.schedule(new JobInfo.Builder(2030028, componentName).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(IDLE_LOGGING_PERIOD_MILLIS).build());
        jobScheduler.schedule(new JobInfo.Builder(203142925, componentName).setRequiresDeviceIdle(true).setRequiresBatteryNotLow(true).setPeriodic(AUDIT_WATCHING_PERIOD_MILLIS).build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        int jobId = jobParameters.getJobId();
        if (jobId == 2030028) {
            this.mIdleLoggingStopRequested = false;
            new IdleLoggingThread(jobParameters).start();
            return true;
        } else if (jobId != 203142925) {
            return false;
        } else {
            this.mAuditWatchingStopRequested = false;
            new AuditWatchingThread(jobParameters).start();
            return true;
        }
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        int jobId = jobParameters.getJobId();
        if (jobId == 2030028) {
            this.mIdleLoggingStopRequested = true;
            return true;
        } else if (jobId != 203142925) {
            return false;
        } else {
            this.mAuditWatchingStopRequested = true;
            return true;
        }
    }

    public static DynamicCodeLogger getDynamicCodeLogger() {
        return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getDynamicCodeLogger();
    }

    public static void syncDataFromArtService(DynamicCodeLogger dynamicCodeLogger) {
        DexUseManagerLocal dexUseManagerLocal = DexOptHelper.getDexUseManagerLocal();
        if (dexUseManagerLocal == null) {
            return;
        }
        PackageManagerLocal packageManagerLocal = (PackageManagerLocal) LocalManagerRegistry.getManager(PackageManagerLocal.class);
        Objects.requireNonNull(packageManagerLocal);
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            for (String str : withUnfilteredSnapshot.getPackageStates().keySet()) {
                for (DexContainerFileUseInfo dexContainerFileUseInfo : dexUseManagerLocal.getSecondaryDexContainerFileUseInfo(str)) {
                    for (String str2 : dexContainerFileUseInfo.getLoadingPackages()) {
                        dynamicCodeLogger.recordDex(dexContainerFileUseInfo.getUserHandle().getIdentifier(), dexContainerFileUseInfo.getDexContainerFile(), str, str2);
                    }
                }
            }
            withUnfilteredSnapshot.close();
        } catch (Throwable th) {
            if (withUnfilteredSnapshot != null) {
                try {
                    withUnfilteredSnapshot.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* renamed from: com.android.server.pm.DynamicCodeLoggingService$IdleLoggingThread */
    /* loaded from: classes2.dex */
    public class IdleLoggingThread extends Thread {
        public final JobParameters mParams;

        public IdleLoggingThread(JobParameters jobParameters) {
            super("DynamicCodeLoggingService_IdleLoggingJob");
            this.mParams = jobParameters;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            DynamicCodeLogger dynamicCodeLogger = DynamicCodeLoggingService.getDynamicCodeLogger();
            DynamicCodeLoggingService.syncDataFromArtService(dynamicCodeLogger);
            for (String str : dynamicCodeLogger.getAllPackagesWithDynamicCodeLoading()) {
                if (DynamicCodeLoggingService.this.mIdleLoggingStopRequested) {
                    Log.w(DynamicCodeLoggingService.TAG, "Stopping IdleLoggingJob run at scheduler request");
                    return;
                }
                dynamicCodeLogger.logDynamicCodeLoading(str);
            }
            DynamicCodeLoggingService.this.jobFinished(this.mParams, false);
        }
    }

    /* renamed from: com.android.server.pm.DynamicCodeLoggingService$AuditWatchingThread */
    /* loaded from: classes2.dex */
    public class AuditWatchingThread extends Thread {
        public final JobParameters mParams;

        public AuditWatchingThread(JobParameters jobParameters) {
            super("DynamicCodeLoggingService_AuditWatchingJob");
            this.mParams = jobParameters;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (processAuditEvents()) {
                DynamicCodeLoggingService.this.jobFinished(this.mParams, false);
            }
        }

        public final boolean processAuditEvents() {
            try {
                int tagCode = EventLog.getTagCode("auditd");
                int[] iArr = {tagCode};
                if (tagCode == -1) {
                    return true;
                }
                DynamicCodeLogger dynamicCodeLogger = DynamicCodeLoggingService.getDynamicCodeLogger();
                ArrayList arrayList = new ArrayList();
                EventLog.readEvents(iArr, arrayList);
                Matcher matcher = DynamicCodeLoggingService.EXECUTE_NATIVE_AUDIT_PATTERN.matcher("");
                for (int i = 0; i < arrayList.size(); i++) {
                    if (DynamicCodeLoggingService.this.mAuditWatchingStopRequested) {
                        Log.w(DynamicCodeLoggingService.TAG, "Stopping AuditWatchingJob run at scheduler request");
                        return false;
                    }
                    EventLog.Event event = (EventLog.Event) arrayList.get(i);
                    int uid = event.getUid();
                    if (Process.isApplicationUid(uid)) {
                        Object data = event.getData();
                        if (data instanceof String) {
                            String str = (String) data;
                            if (str.startsWith("type=1400 ")) {
                                matcher.reset(str);
                                if (matcher.matches()) {
                                    String group = matcher.group(1);
                                    if (group == null) {
                                        group = DynamicCodeLoggingService.unhex(matcher.group(2));
                                    }
                                    dynamicCodeLogger.recordNative(uid, group);
                                }
                            }
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                Log.e(DynamicCodeLoggingService.TAG, "AuditWatchingJob failed", e);
                return true;
            }
        }
    }

    public static String unhex(String str) {
        return (str == null || str.length() == 0) ? "" : new String(HexEncoding.decode(str, false));
    }
}
