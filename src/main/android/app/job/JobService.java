package android.app.job;

import android.app.Notification;
import android.app.Service;
import android.compat.Compatibility;
import android.content.Intent;
import android.p008os.IBinder;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public abstract class JobService extends Service {
    public static final int JOB_END_NOTIFICATION_POLICY_DETACH = 0;
    public static final int JOB_END_NOTIFICATION_POLICY_REMOVE = 1;
    public static final String PERMISSION_BIND = "android.permission.BIND_JOB_SERVICE";
    private static final String TAG = "JobService";
    private JobServiceEngine mEngine;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface JobEndNotificationPolicy {
    }

    public abstract boolean onStartJob(JobParameters jobParameters);

    public abstract boolean onStopJob(JobParameters jobParameters);

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mEngine == null) {
            this.mEngine = new JobServiceEngine(this) { // from class: android.app.job.JobService.1
                @Override // android.app.job.JobServiceEngine
                public boolean onStartJob(JobParameters params) {
                    return JobService.this.onStartJob(params);
                }

                @Override // android.app.job.JobServiceEngine
                public boolean onStopJob(JobParameters params) {
                    return JobService.this.onStopJob(params);
                }

                @Override // android.app.job.JobServiceEngine
                public long getTransferredDownloadBytes(JobParameters params, JobWorkItem item) {
                    if (item == null) {
                        return JobService.this.getTransferredDownloadBytes(params);
                    }
                    return JobService.this.getTransferredDownloadBytes(params, item);
                }

                @Override // android.app.job.JobServiceEngine
                public long getTransferredUploadBytes(JobParameters params, JobWorkItem item) {
                    if (item == null) {
                        return JobService.this.getTransferredUploadBytes(params);
                    }
                    return JobService.this.getTransferredUploadBytes(params, item);
                }

                @Override // android.app.job.JobServiceEngine
                public void onNetworkChanged(JobParameters params) {
                    JobService.this.onNetworkChanged(params);
                }
            };
        }
        return this.mEngine.getBinder();
    }

    public final void jobFinished(JobParameters params, boolean wantsReschedule) {
        this.mEngine.jobFinished(params, wantsReschedule);
    }

    public void onNetworkChanged(JobParameters params) {
        Log.m104w(TAG, "onNetworkChanged() not implemented. Must override in a subclass.");
    }

    public final void updateEstimatedNetworkBytes(JobParameters params, long downloadBytes, long uploadBytes) {
        this.mEngine.updateEstimatedNetworkBytes(params, null, downloadBytes, uploadBytes);
    }

    public final void updateEstimatedNetworkBytes(JobParameters params, JobWorkItem jobWorkItem, long downloadBytes, long uploadBytes) {
        this.mEngine.updateEstimatedNetworkBytes(params, jobWorkItem, downloadBytes, uploadBytes);
    }

    public final void updateTransferredNetworkBytes(JobParameters params, long transferredDownloadBytes, long transferredUploadBytes) {
        this.mEngine.updateTransferredNetworkBytes(params, null, transferredDownloadBytes, transferredUploadBytes);
    }

    public final void updateTransferredNetworkBytes(JobParameters params, JobWorkItem item, long transferredDownloadBytes, long transferredUploadBytes) {
        this.mEngine.updateTransferredNetworkBytes(params, item, transferredDownloadBytes, transferredUploadBytes);
    }

    public long getTransferredDownloadBytes(JobParameters params) {
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public long getTransferredUploadBytes(JobParameters params) {
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public long getTransferredDownloadBytes(JobParameters params, JobWorkItem item) {
        if (item == null) {
            return getTransferredDownloadBytes(params);
        }
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public long getTransferredUploadBytes(JobParameters params, JobWorkItem item) {
        if (item == null) {
            return getTransferredUploadBytes(params);
        }
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public final void setNotification(JobParameters params, int notificationId, Notification notification, int jobEndNotificationPolicy) {
        this.mEngine.setNotification(params, notificationId, notification, jobEndNotificationPolicy);
    }
}
