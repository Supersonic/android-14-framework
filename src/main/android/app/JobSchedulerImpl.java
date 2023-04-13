package android.app;

import android.app.job.IJobScheduler;
import android.app.job.IUserVisibleJobObserver;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobSnapshot;
import android.app.job.JobWorkItem;
import android.content.Context;
import android.content.p001pm.ParceledListSlice;
import android.p008os.RemoteException;
import android.util.ArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class JobSchedulerImpl extends JobScheduler {
    IJobScheduler mBinder;
    private final Context mContext;
    private final String mNamespace;

    public JobSchedulerImpl(Context context, IJobScheduler binder) {
        this(context, binder, null);
    }

    private JobSchedulerImpl(Context context, IJobScheduler binder, String namespace) {
        this.mContext = context;
        this.mBinder = binder;
        this.mNamespace = namespace;
    }

    private JobSchedulerImpl(JobSchedulerImpl jsi, String namespace) {
        this(jsi.mContext, jsi.mBinder, namespace);
    }

    @Override // android.app.job.JobScheduler
    public JobScheduler forNamespace(String namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("namespace cannot be null");
        }
        return new JobSchedulerImpl(this, namespace);
    }

    @Override // android.app.job.JobScheduler
    public String getNamespace() {
        return this.mNamespace;
    }

    @Override // android.app.job.JobScheduler
    public int schedule(JobInfo job) {
        try {
            return this.mBinder.schedule(this.mNamespace, job);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.app.job.JobScheduler
    public int enqueue(JobInfo job, JobWorkItem work) {
        try {
            return this.mBinder.enqueue(this.mNamespace, job, work);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.app.job.JobScheduler
    public int scheduleAsPackage(JobInfo job, String packageName, int userId, String tag) {
        try {
            return this.mBinder.scheduleAsPackage(this.mNamespace, job, packageName, userId, tag);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.app.job.JobScheduler
    public void cancel(int jobId) {
        try {
            this.mBinder.cancel(this.mNamespace, jobId);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.job.JobScheduler
    public void cancelAll() {
        try {
            this.mBinder.cancelAllInNamespace(this.mNamespace);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.job.JobScheduler
    public void cancelInAllNamespaces() {
        try {
            this.mBinder.cancelAll();
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.job.JobScheduler
    public List<JobInfo> getAllPendingJobs() {
        try {
            return this.mBinder.getAllPendingJobsInNamespace(this.mNamespace).getList();
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.app.job.JobScheduler
    public Map<String, List<JobInfo>> getPendingJobsInAllNamespaces() {
        try {
            Map<String, ParceledListSlice<JobInfo>> parceledList = this.mBinder.getAllPendingJobs();
            ArrayMap<String, List<JobInfo>> jobMap = new ArrayMap<>();
            Set<String> keys = parceledList.keySet();
            for (String key : keys) {
                jobMap.put(key, parceledList.get(key).getList());
            }
            return jobMap;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.app.job.JobScheduler
    public JobInfo getPendingJob(int jobId) {
        try {
            return this.mBinder.getPendingJob(this.mNamespace, jobId);
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.app.job.JobScheduler
    public int getPendingJobReason(int jobId) {
        try {
            return this.mBinder.getPendingJobReason(this.mNamespace, jobId);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.app.job.JobScheduler
    public boolean canRunUserInitiatedJobs() {
        try {
            return this.mBinder.canRunUserInitiatedJobs(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.app.job.JobScheduler
    public boolean hasRunUserInitiatedJobsPermission(String packageName, int userId) {
        try {
            return this.mBinder.hasRunUserInitiatedJobsPermission(packageName, userId);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.app.job.JobScheduler
    public List<JobInfo> getStartedJobs() {
        try {
            return this.mBinder.getStartedJobs();
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.app.job.JobScheduler
    public List<JobSnapshot> getAllJobSnapshots() {
        try {
            return this.mBinder.getAllJobSnapshots().getList();
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.app.job.JobScheduler
    public void registerUserVisibleJobObserver(IUserVisibleJobObserver observer) {
        try {
            this.mBinder.registerUserVisibleJobObserver(observer);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.job.JobScheduler
    public void unregisterUserVisibleJobObserver(IUserVisibleJobObserver observer) {
        try {
            this.mBinder.unregisterUserVisibleJobObserver(observer);
        } catch (RemoteException e) {
        }
    }

    @Override // android.app.job.JobScheduler
    public void notePendingUserRequestedAppStop(String packageName, int userId, String debugReason) {
        try {
            this.mBinder.notePendingUserRequestedAppStop(packageName, userId, debugReason);
        } catch (RemoteException e) {
        }
    }
}
