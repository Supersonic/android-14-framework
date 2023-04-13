package com.android.server.job;

import android.app.job.JobInfo;
import android.util.proto.ProtoOutputStream;
import java.util.List;
/* loaded from: classes5.dex */
public interface JobSchedulerInternal {
    void addBackingUpUid(int i);

    void cancelJobsForUid(int i, boolean z, int i2, int i3, String str);

    void clearAllBackingUpUids();

    String getCloudMediaProviderPackage(int i);

    JobStorePersistStats getPersistStats();

    List<JobInfo> getSystemScheduledOwnJobs(String str);

    void removeBackingUpUid(int i);

    void reportAppUsage(String str, int i);

    /* loaded from: classes5.dex */
    public static class JobStorePersistStats {
        public int countAllJobsLoaded;
        public int countAllJobsSaved;
        public int countSystemServerJobsLoaded;
        public int countSystemServerJobsSaved;
        public int countSystemSyncManagerJobsLoaded;
        public int countSystemSyncManagerJobsSaved;

        public JobStorePersistStats() {
            this.countAllJobsLoaded = -1;
            this.countSystemServerJobsLoaded = -1;
            this.countSystemSyncManagerJobsLoaded = -1;
            this.countAllJobsSaved = -1;
            this.countSystemServerJobsSaved = -1;
            this.countSystemSyncManagerJobsSaved = -1;
        }

        public JobStorePersistStats(JobStorePersistStats source) {
            this.countAllJobsLoaded = -1;
            this.countSystemServerJobsLoaded = -1;
            this.countSystemSyncManagerJobsLoaded = -1;
            this.countAllJobsSaved = -1;
            this.countSystemServerJobsSaved = -1;
            this.countSystemSyncManagerJobsSaved = -1;
            this.countAllJobsLoaded = source.countAllJobsLoaded;
            this.countSystemServerJobsLoaded = source.countSystemServerJobsLoaded;
            this.countSystemSyncManagerJobsLoaded = source.countSystemSyncManagerJobsLoaded;
            this.countAllJobsSaved = source.countAllJobsSaved;
            this.countSystemServerJobsSaved = source.countSystemServerJobsSaved;
            this.countSystemSyncManagerJobsSaved = source.countSystemSyncManagerJobsSaved;
        }

        public String toString() {
            return "FirstLoad: " + this.countAllJobsLoaded + "/" + this.countSystemServerJobsLoaded + "/" + this.countSystemSyncManagerJobsLoaded + " LastSave: " + this.countAllJobsSaved + "/" + this.countSystemServerJobsSaved + "/" + this.countSystemSyncManagerJobsSaved;
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            long flToken = proto.start(1146756268033L);
            proto.write(1120986464257L, this.countAllJobsLoaded);
            proto.write(1120986464258L, this.countSystemServerJobsLoaded);
            proto.write(1120986464259L, this.countSystemSyncManagerJobsLoaded);
            proto.end(flToken);
            long lsToken = proto.start(1146756268034L);
            proto.write(1120986464257L, this.countAllJobsSaved);
            proto.write(1120986464258L, this.countSystemServerJobsSaved);
            proto.write(1120986464259L, this.countSystemSyncManagerJobsSaved);
            proto.end(lsToken);
            proto.end(token);
        }
    }
}
