package android.app.job;

import android.app.Notification;
import android.app.Service;
import android.app.job.IJobService;
import android.compat.Compatibility;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.p028os.SomeArgs;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public abstract class JobServiceEngine {
    private static final int MSG_EXECUTE_JOB = 0;
    private static final int MSG_GET_TRANSFERRED_DOWNLOAD_BYTES = 3;
    private static final int MSG_GET_TRANSFERRED_UPLOAD_BYTES = 4;
    private static final int MSG_INFORM_OF_NETWORK_CHANGE = 8;
    private static final int MSG_JOB_FINISHED = 2;
    private static final int MSG_SET_NOTIFICATION = 7;
    private static final int MSG_STOP_JOB = 1;
    private static final int MSG_UPDATE_ESTIMATED_NETWORK_BYTES = 6;
    private static final int MSG_UPDATE_TRANSFERRED_NETWORK_BYTES = 5;
    private static final String TAG = "JobServiceEngine";
    private final IJobService mBinder = new JobInterface(this);
    JobHandler mHandler;

    public abstract boolean onStartJob(JobParameters jobParameters);

    public abstract boolean onStopJob(JobParameters jobParameters);

    /* loaded from: classes.dex */
    static final class JobInterface extends IJobService.Stub {
        final WeakReference<JobServiceEngine> mService;

        JobInterface(JobServiceEngine service) {
            this.mService = new WeakReference<>(service);
        }

        @Override // android.app.job.IJobService
        public void getTransferredDownloadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
            JobServiceEngine service = this.mService.get();
            if (service != null) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = jobParams;
                args.arg2 = jobWorkItem;
                service.mHandler.obtainMessage(3, args).sendToTarget();
            }
        }

        @Override // android.app.job.IJobService
        public void getTransferredUploadBytes(JobParameters jobParams, JobWorkItem jobWorkItem) throws RemoteException {
            JobServiceEngine service = this.mService.get();
            if (service != null) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = jobParams;
                args.arg2 = jobWorkItem;
                service.mHandler.obtainMessage(4, args).sendToTarget();
            }
        }

        @Override // android.app.job.IJobService
        public void startJob(JobParameters jobParams) throws RemoteException {
            JobServiceEngine service = this.mService.get();
            if (service != null) {
                Message m = Message.obtain(service.mHandler, 0, jobParams);
                m.sendToTarget();
            }
        }

        @Override // android.app.job.IJobService
        public void onNetworkChanged(JobParameters jobParams) throws RemoteException {
            JobServiceEngine service = this.mService.get();
            if (service != null) {
                service.mHandler.removeMessages(8);
                service.mHandler.obtainMessage(8, jobParams).sendToTarget();
            }
        }

        @Override // android.app.job.IJobService
        public void stopJob(JobParameters jobParams) throws RemoteException {
            JobServiceEngine service = this.mService.get();
            if (service != null) {
                Message m = Message.obtain(service.mHandler, 1, jobParams);
                m.sendToTarget();
            }
        }
    }

    /* loaded from: classes.dex */
    class JobHandler extends Handler {
        JobHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    JobParameters params = (JobParameters) msg.obj;
                    try {
                        boolean workOngoing = JobServiceEngine.this.onStartJob(params);
                        ackStartMessage(params, workOngoing);
                        return;
                    } catch (Exception e) {
                        Log.m110e(JobServiceEngine.TAG, "Error while executing job: " + params.getJobId());
                        throw new RuntimeException(e);
                    }
                case 1:
                    JobParameters params2 = (JobParameters) msg.obj;
                    try {
                        boolean ret = JobServiceEngine.this.onStopJob(params2);
                        ackStopMessage(params2, ret);
                        return;
                    } catch (Exception e2) {
                        Log.m109e(JobServiceEngine.TAG, "Application unable to handle onStopJob.", e2);
                        throw new RuntimeException(e2);
                    }
                case 2:
                    JobParameters params3 = (JobParameters) msg.obj;
                    boolean needsReschedule = msg.arg2 == 1;
                    IJobCallback callback = params3.getCallback();
                    if (callback != null) {
                        try {
                            callback.jobFinished(params3.getJobId(), needsReschedule);
                            return;
                        } catch (RemoteException e3) {
                            Log.m110e(JobServiceEngine.TAG, "Error reporting job finish to system: binder has goneaway.");
                            return;
                        }
                    }
                    Log.m110e(JobServiceEngine.TAG, "finishJob() called for a nonexistent job id.");
                    return;
                case 3:
                    SomeArgs args = (SomeArgs) msg.obj;
                    JobParameters params4 = (JobParameters) args.arg1;
                    JobWorkItem item = (JobWorkItem) args.arg2;
                    try {
                        long ret2 = JobServiceEngine.this.getTransferredDownloadBytes(params4, item);
                        ackGetTransferredDownloadBytesMessage(params4, item, ret2);
                        args.recycle();
                        return;
                    } catch (Exception e4) {
                        Log.m109e(JobServiceEngine.TAG, "Application unable to handle getTransferredDownloadBytes.", e4);
                        throw new RuntimeException(e4);
                    }
                case 4:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    JobParameters params5 = (JobParameters) args2.arg1;
                    JobWorkItem item2 = (JobWorkItem) args2.arg2;
                    try {
                        long ret3 = JobServiceEngine.this.getTransferredUploadBytes(params5, item2);
                        ackGetTransferredUploadBytesMessage(params5, item2, ret3);
                        args2.recycle();
                        return;
                    } catch (Exception e5) {
                        Log.m109e(JobServiceEngine.TAG, "Application unable to handle getTransferredUploadBytes.", e5);
                        throw new RuntimeException(e5);
                    }
                case 5:
                    SomeArgs args3 = (SomeArgs) msg.obj;
                    JobParameters params6 = (JobParameters) args3.arg1;
                    IJobCallback callback2 = params6.getCallback();
                    if (callback2 != null) {
                        try {
                            callback2.updateTransferredNetworkBytes(params6.getJobId(), (JobWorkItem) args3.arg2, args3.argl1, args3.argl2);
                        } catch (RemoteException e6) {
                            Log.m110e(JobServiceEngine.TAG, "Error updating data transfer progress to system: binder has gone away.");
                        }
                    } else {
                        Log.m110e(JobServiceEngine.TAG, "updateDataTransferProgress() called for a nonexistent job id.");
                    }
                    args3.recycle();
                    return;
                case 6:
                    SomeArgs args4 = (SomeArgs) msg.obj;
                    JobParameters params7 = (JobParameters) args4.arg1;
                    IJobCallback callback3 = params7.getCallback();
                    if (callback3 != null) {
                        try {
                            callback3.updateEstimatedNetworkBytes(params7.getJobId(), (JobWorkItem) args4.arg2, args4.argl1, args4.argl2);
                        } catch (RemoteException e7) {
                            Log.m110e(JobServiceEngine.TAG, "Error updating estimated transfer size to system: binder has gone away.");
                        }
                    } else {
                        Log.m110e(JobServiceEngine.TAG, "updateEstimatedNetworkBytes() called for a nonexistent job id.");
                    }
                    args4.recycle();
                    return;
                case 7:
                    SomeArgs args5 = (SomeArgs) msg.obj;
                    JobParameters params8 = (JobParameters) args5.arg1;
                    Notification notification = (Notification) args5.arg2;
                    IJobCallback callback4 = params8.getCallback();
                    if (callback4 != null) {
                        try {
                            callback4.setNotification(params8.getJobId(), args5.argi1, notification, args5.argi2);
                        } catch (RemoteException e8) {
                            Log.m110e(JobServiceEngine.TAG, "Error providing notification: binder has gone away.");
                        }
                    } else {
                        Log.m110e(JobServiceEngine.TAG, "setNotification() called for a nonexistent job.");
                    }
                    args5.recycle();
                    return;
                case 8:
                    JobParameters params9 = (JobParameters) msg.obj;
                    try {
                        JobServiceEngine.this.onNetworkChanged(params9);
                        return;
                    } catch (Exception e9) {
                        Log.m110e(JobServiceEngine.TAG, "Error while executing job: " + params9.getJobId());
                        throw new RuntimeException(e9);
                    }
                default:
                    Log.m110e(JobServiceEngine.TAG, "Unrecognised message received.");
                    return;
            }
        }

        private void ackGetTransferredDownloadBytesMessage(JobParameters params, JobWorkItem item, long progress) {
            IJobCallback callback = params.getCallback();
            int jobId = params.getJobId();
            int workId = item == null ? -1 : item.getWorkId();
            if (callback == null) {
                if (Log.isLoggable(JobServiceEngine.TAG, 3)) {
                    Log.m112d(JobServiceEngine.TAG, "Attempting to ack a job that has already been processed.");
                    return;
                }
                return;
            }
            try {
                callback.acknowledgeGetTransferredDownloadBytesMessage(jobId, workId, progress);
            } catch (RemoteException e) {
                Log.m110e(JobServiceEngine.TAG, "System unreachable for returning progress.");
            }
        }

        private void ackGetTransferredUploadBytesMessage(JobParameters params, JobWorkItem item, long progress) {
            IJobCallback callback = params.getCallback();
            int jobId = params.getJobId();
            int workId = item == null ? -1 : item.getWorkId();
            if (callback == null) {
                if (Log.isLoggable(JobServiceEngine.TAG, 3)) {
                    Log.m112d(JobServiceEngine.TAG, "Attempting to ack a job that has already been processed.");
                    return;
                }
                return;
            }
            try {
                callback.acknowledgeGetTransferredUploadBytesMessage(jobId, workId, progress);
            } catch (RemoteException e) {
                Log.m110e(JobServiceEngine.TAG, "System unreachable for returning progress.");
            }
        }

        private void ackStartMessage(JobParameters params, boolean workOngoing) {
            IJobCallback callback = params.getCallback();
            int jobId = params.getJobId();
            if (callback == null) {
                if (Log.isLoggable(JobServiceEngine.TAG, 3)) {
                    Log.m112d(JobServiceEngine.TAG, "Attempting to ack a job that has already been processed.");
                    return;
                }
                return;
            }
            try {
                callback.acknowledgeStartMessage(jobId, workOngoing);
            } catch (RemoteException e) {
                Log.m110e(JobServiceEngine.TAG, "System unreachable for starting job.");
            }
        }

        private void ackStopMessage(JobParameters params, boolean reschedule) {
            IJobCallback callback = params.getCallback();
            int jobId = params.getJobId();
            if (callback == null) {
                if (Log.isLoggable(JobServiceEngine.TAG, 3)) {
                    Log.m112d(JobServiceEngine.TAG, "Attempting to ack a job that has already been processed.");
                    return;
                }
                return;
            }
            try {
                callback.acknowledgeStopMessage(jobId, reschedule);
            } catch (RemoteException e) {
                Log.m110e(JobServiceEngine.TAG, "System unreachable for stopping job.");
            }
        }
    }

    public JobServiceEngine(Service service) {
        this.mHandler = new JobHandler(service.getMainLooper());
    }

    public final IBinder getBinder() {
        return this.mBinder.asBinder();
    }

    public void jobFinished(JobParameters params, boolean needsReschedule) {
        if (params == null) {
            throw new NullPointerException("params");
        }
        Message m = Message.obtain(this.mHandler, 2, params);
        m.arg2 = needsReschedule ? 1 : 0;
        m.sendToTarget();
    }

    public void onNetworkChanged(JobParameters params) {
        Log.m104w(TAG, "onNetworkChanged() not implemented. Must override in a subclass.");
    }

    public long getTransferredDownloadBytes(JobParameters params, JobWorkItem item) {
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public long getTransferredUploadBytes(JobParameters params, JobWorkItem item) {
        if (Compatibility.isChangeEnabled((long) JobScheduler.THROW_ON_INVALID_DATA_TRANSFER_IMPLEMENTATION)) {
            throw new RuntimeException("Not implemented. Must override in a subclass.");
        }
        return 0L;
    }

    public void updateTransferredNetworkBytes(JobParameters params, JobWorkItem item, long downloadBytes, long uploadBytes) {
        if (params == null) {
            throw new NullPointerException("params");
        }
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = params;
        args.arg2 = item;
        args.argl1 = downloadBytes;
        args.argl2 = uploadBytes;
        this.mHandler.obtainMessage(5, args).sendToTarget();
    }

    public void updateEstimatedNetworkBytes(JobParameters params, JobWorkItem item, long downloadBytes, long uploadBytes) {
        if (params == null) {
            throw new NullPointerException("params");
        }
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = params;
        args.arg2 = item;
        args.argl1 = downloadBytes;
        args.argl2 = uploadBytes;
        this.mHandler.obtainMessage(6, args).sendToTarget();
    }

    public void setNotification(JobParameters params, int notificationId, Notification notification, int jobEndNotificationPolicy) {
        if (params == null) {
            throw new NullPointerException("params");
        }
        if (notification == null) {
            throw new NullPointerException("notification");
        }
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = params;
        args.arg2 = notification;
        args.argi1 = notificationId;
        args.argi2 = jobEndNotificationPolicy;
        this.mHandler.obtainMessage(7, args).sendToTarget();
    }
}
