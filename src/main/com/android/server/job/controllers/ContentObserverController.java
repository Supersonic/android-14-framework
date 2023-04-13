package com.android.server.job.controllers;

import android.app.job.JobInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.job.JobSchedulerService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class ContentObserverController extends StateController {
    public static final boolean DEBUG;
    public final Handler mHandler;
    public final SparseArray<ArrayMap<JobInfo.TriggerContentUri, ObserverInstance>> mObservers;
    public final ArraySet<JobStatus> mTrackedTasks;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.ContentObserver", 3);
    }

    public ContentObserverController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
        this.mTrackedTasks = new ArraySet<>();
        this.mObservers = new SparseArray<>();
        this.mHandler = new Handler(this.mContext.getMainLooper());
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        JobInstance jobInstance;
        if (jobStatus.hasContentTriggerConstraint()) {
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            if (jobStatus.contentObserverJobInstance == null) {
                jobStatus.contentObserverJobInstance = new JobInstance(jobStatus);
            }
            if (DEBUG) {
                Slog.i("JobScheduler.ContentObserver", "Tracking content-trigger job " + jobStatus);
            }
            this.mTrackedTasks.add(jobStatus);
            jobStatus.setTrackingController(4);
            JobInstance jobInstance2 = jobStatus.contentObserverJobInstance;
            ArraySet<String> arraySet = jobInstance2.mChangedAuthorities;
            boolean z = true;
            boolean z2 = arraySet != null;
            if (jobStatus.changedAuthorities != null) {
                if (arraySet == null) {
                    jobInstance2.mChangedAuthorities = new ArraySet<>();
                }
                Iterator<String> it = jobStatus.changedAuthorities.iterator();
                while (it.hasNext()) {
                    jobStatus.contentObserverJobInstance.mChangedAuthorities.add(it.next());
                }
                if (jobStatus.changedUris != null) {
                    JobInstance jobInstance3 = jobStatus.contentObserverJobInstance;
                    if (jobInstance3.mChangedUris == null) {
                        jobInstance3.mChangedUris = new ArraySet<>();
                    }
                    Iterator<Uri> it2 = jobStatus.changedUris.iterator();
                    while (it2.hasNext()) {
                        jobStatus.contentObserverJobInstance.mChangedUris.add(it2.next());
                    }
                }
            } else {
                z = z2;
            }
            jobStatus.changedAuthorities = null;
            jobStatus.changedUris = null;
            jobStatus.setContentTriggerConstraintSatisfied(millis, z);
        }
        if (jobStatus2 == null || (jobInstance = jobStatus2.contentObserverJobInstance) == null) {
            return;
        }
        jobInstance.detachLocked();
        jobStatus2.contentObserverJobInstance = null;
    }

    @Override // com.android.server.job.controllers.StateController
    public void prepareForExecutionLocked(JobStatus jobStatus) {
        JobInstance jobInstance;
        if (!jobStatus.hasContentTriggerConstraint() || (jobInstance = jobStatus.contentObserverJobInstance) == null) {
            return;
        }
        jobStatus.changedUris = jobInstance.mChangedUris;
        jobStatus.changedAuthorities = jobInstance.mChangedAuthorities;
        jobInstance.mChangedUris = null;
        jobInstance.mChangedAuthorities = null;
    }

    @Override // com.android.server.job.controllers.StateController
    public void unprepareFromExecutionLocked(JobStatus jobStatus) {
        JobInstance jobInstance;
        if (!jobStatus.hasContentTriggerConstraint() || (jobInstance = jobStatus.contentObserverJobInstance) == null) {
            return;
        }
        ArraySet<Uri> arraySet = jobInstance.mChangedUris;
        if (arraySet == null) {
            jobInstance.mChangedUris = jobStatus.changedUris;
        } else {
            arraySet.addAll((ArraySet<? extends Uri>) jobStatus.changedUris);
        }
        JobInstance jobInstance2 = jobStatus.contentObserverJobInstance;
        ArraySet<String> arraySet2 = jobInstance2.mChangedAuthorities;
        if (arraySet2 == null) {
            jobInstance2.mChangedAuthorities = jobStatus.changedAuthorities;
        } else {
            arraySet2.addAll((ArraySet<? extends String>) jobStatus.changedAuthorities);
        }
        jobStatus.changedUris = null;
        jobStatus.changedAuthorities = null;
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.clearTrackingController(4)) {
            this.mTrackedTasks.remove(jobStatus);
            JobInstance jobInstance = jobStatus.contentObserverJobInstance;
            if (jobInstance != null) {
                jobInstance.unscheduleLocked();
                if (jobStatus2 != null) {
                    JobInstance jobInstance2 = jobStatus.contentObserverJobInstance;
                    if (jobInstance2 != null && jobInstance2.mChangedAuthorities != null) {
                        if (jobStatus2.contentObserverJobInstance == null) {
                            jobStatus2.contentObserverJobInstance = new JobInstance(jobStatus2);
                        }
                        JobInstance jobInstance3 = jobStatus2.contentObserverJobInstance;
                        JobInstance jobInstance4 = jobStatus.contentObserverJobInstance;
                        jobInstance3.mChangedAuthorities = jobInstance4.mChangedAuthorities;
                        jobInstance3.mChangedUris = jobInstance4.mChangedUris;
                        jobInstance4.mChangedAuthorities = null;
                        jobInstance4.mChangedUris = null;
                    }
                } else {
                    jobStatus.contentObserverJobInstance.detachLocked();
                    jobStatus.contentObserverJobInstance = null;
                }
            }
            if (DEBUG) {
                Slog.i("JobScheduler.ContentObserver", "No longer tracking job " + jobStatus);
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void rescheduleForFailureLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus2.hasContentTriggerConstraint() && jobStatus.hasContentTriggerConstraint()) {
            jobStatus.changedAuthorities = jobStatus2.changedAuthorities;
            jobStatus.changedUris = jobStatus2.changedUris;
        }
    }

    /* loaded from: classes.dex */
    public final class ObserverInstance extends ContentObserver {
        public final ArraySet<JobInstance> mJobs;
        public final JobInfo.TriggerContentUri mUri;
        public final int mUserId;

        public ObserverInstance(Handler handler, JobInfo.TriggerContentUri triggerContentUri, int i) {
            super(handler);
            this.mJobs = new ArraySet<>();
            this.mUri = triggerContentUri;
            this.mUserId = i;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (ContentObserverController.DEBUG) {
                Slog.i("JobScheduler.ContentObserver", "onChange(self=" + z + ") for " + uri + " when mUri=" + this.mUri + " mUserId=" + this.mUserId);
            }
            synchronized (ContentObserverController.this.mLock) {
                int size = this.mJobs.size();
                for (int i = 0; i < size; i++) {
                    JobInstance valueAt = this.mJobs.valueAt(i);
                    if (valueAt.mChangedUris == null) {
                        valueAt.mChangedUris = new ArraySet<>();
                    }
                    if (valueAt.mChangedUris.size() < 50) {
                        valueAt.mChangedUris.add(uri);
                    }
                    if (valueAt.mChangedAuthorities == null) {
                        valueAt.mChangedAuthorities = new ArraySet<>();
                    }
                    valueAt.mChangedAuthorities.add(uri.getAuthority());
                    valueAt.scheduleLocked();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class TriggerRunnable implements Runnable {
        public final JobInstance mInstance;

        public TriggerRunnable(JobInstance jobInstance) {
            this.mInstance = jobInstance;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mInstance.trigger();
        }
    }

    /* loaded from: classes.dex */
    public final class JobInstance {
        public ArraySet<String> mChangedAuthorities;
        public ArraySet<Uri> mChangedUris;
        public final JobStatus mJobStatus;
        public boolean mTriggerPending;
        public final ArrayList<ObserverInstance> mMyObservers = new ArrayList<>();
        public final Runnable mExecuteRunner = new TriggerRunnable(this);
        public final Runnable mTimeoutRunner = new TriggerRunnable(this);

        public JobInstance(JobStatus jobStatus) {
            this.mJobStatus = jobStatus;
            JobInfo.TriggerContentUri[] triggerContentUris = jobStatus.getJob().getTriggerContentUris();
            int sourceUserId = jobStatus.getSourceUserId();
            ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap = ContentObserverController.this.mObservers.get(sourceUserId);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                ContentObserverController.this.mObservers.put(sourceUserId, arrayMap);
            }
            if (triggerContentUris != null) {
                for (JobInfo.TriggerContentUri triggerContentUri : triggerContentUris) {
                    ObserverInstance observerInstance = arrayMap.get(triggerContentUri);
                    if (observerInstance == null) {
                        observerInstance = new ObserverInstance(ContentObserverController.this.mHandler, triggerContentUri, jobStatus.getSourceUserId());
                        arrayMap.put(triggerContentUri, observerInstance);
                        boolean z = (triggerContentUri.getFlags() & 1) != 0;
                        if (ContentObserverController.DEBUG) {
                            Slog.v("JobScheduler.ContentObserver", "New observer " + observerInstance + " for " + triggerContentUri.getUri() + " andDescendants=" + z + " sourceUserId=" + sourceUserId);
                        }
                        ContentObserverController.this.mContext.getContentResolver().registerContentObserver(triggerContentUri.getUri(), z, observerInstance, sourceUserId);
                    } else if (ContentObserverController.DEBUG) {
                        Slog.v("JobScheduler.ContentObserver", "Reusing existing observer " + observerInstance + " for " + triggerContentUri.getUri() + " andDescendants=" + ((triggerContentUri.getFlags() & 1) != 0));
                    }
                    observerInstance.mJobs.add(this);
                    this.mMyObservers.add(observerInstance);
                }
            }
        }

        public void trigger() {
            boolean z;
            synchronized (ContentObserverController.this.mLock) {
                if (this.mTriggerPending) {
                    z = this.mJobStatus.setContentTriggerConstraintSatisfied(JobSchedulerService.sElapsedRealtimeClock.millis(), true);
                    unscheduleLocked();
                } else {
                    z = false;
                }
            }
            if (z) {
                ArraySet<JobStatus> arraySet = new ArraySet<>();
                arraySet.add(this.mJobStatus);
                ContentObserverController.this.mStateChangedListener.onControllerStateChanged(arraySet);
            }
        }

        public void scheduleLocked() {
            if (!this.mTriggerPending) {
                this.mTriggerPending = true;
                ContentObserverController.this.mHandler.postDelayed(this.mTimeoutRunner, this.mJobStatus.getTriggerContentMaxDelay());
            }
            ContentObserverController.this.mHandler.removeCallbacks(this.mExecuteRunner);
            if (this.mChangedUris.size() >= 40) {
                ContentObserverController.this.mHandler.post(this.mExecuteRunner);
            } else {
                ContentObserverController.this.mHandler.postDelayed(this.mExecuteRunner, this.mJobStatus.getTriggerContentUpdateDelay());
            }
        }

        public void unscheduleLocked() {
            if (this.mTriggerPending) {
                ContentObserverController.this.mHandler.removeCallbacks(this.mExecuteRunner);
                ContentObserverController.this.mHandler.removeCallbacks(this.mTimeoutRunner);
                this.mTriggerPending = false;
            }
        }

        public void detachLocked() {
            int size = this.mMyObservers.size();
            for (int i = 0; i < size; i++) {
                ObserverInstance observerInstance = this.mMyObservers.get(i);
                observerInstance.mJobs.remove(this);
                if (observerInstance.mJobs.size() == 0) {
                    if (ContentObserverController.DEBUG) {
                        Slog.i("JobScheduler.ContentObserver", "Unregistering observer " + observerInstance + " for " + observerInstance.mUri.getUri());
                    }
                    ContentObserverController.this.mContext.getContentResolver().unregisterContentObserver(observerInstance);
                    ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap = ContentObserverController.this.mObservers.get(observerInstance.mUserId);
                    if (arrayMap != null) {
                        arrayMap.remove(observerInstance.mUri);
                    }
                }
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        boolean z;
        int i;
        int i2;
        for (int i3 = 0; i3 < this.mTrackedTasks.size(); i3++) {
            JobStatus valueAt = this.mTrackedTasks.valueAt(i3);
            if (predicate.test(valueAt)) {
                indentingPrintWriter.print("#");
                valueAt.printUniqueId(indentingPrintWriter);
                indentingPrintWriter.print(" from ");
                UserHandle.formatUid(indentingPrintWriter, valueAt.getSourceUid());
                indentingPrintWriter.println();
            }
        }
        indentingPrintWriter.println();
        int size = this.mObservers.size();
        if (size > 0) {
            indentingPrintWriter.println("Observers:");
            indentingPrintWriter.increaseIndent();
            for (int i4 = 0; i4 < size; i4++) {
                ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap = this.mObservers.get(this.mObservers.keyAt(i4));
                int size2 = arrayMap.size();
                int i5 = 0;
                while (i5 < size2) {
                    ObserverInstance valueAt2 = arrayMap.valueAt(i5);
                    int size3 = valueAt2.mJobs.size();
                    int i6 = 0;
                    while (true) {
                        if (i6 >= size3) {
                            z = false;
                            break;
                        } else if (predicate.test(valueAt2.mJobs.valueAt(i6).mJobStatus)) {
                            z = true;
                            break;
                        } else {
                            i6++;
                        }
                    }
                    if (z) {
                        JobInfo.TriggerContentUri keyAt = arrayMap.keyAt(i5);
                        indentingPrintWriter.print(keyAt.getUri());
                        indentingPrintWriter.print(" 0x");
                        indentingPrintWriter.print(Integer.toHexString(keyAt.getFlags()));
                        indentingPrintWriter.print(" (");
                        indentingPrintWriter.print(System.identityHashCode(valueAt2));
                        indentingPrintWriter.println("):");
                        indentingPrintWriter.increaseIndent();
                        indentingPrintWriter.println("Jobs:");
                        indentingPrintWriter.increaseIndent();
                        int i7 = 0;
                        while (i7 < size3) {
                            JobInstance valueAt3 = valueAt2.mJobs.valueAt(i7);
                            indentingPrintWriter.print("#");
                            valueAt3.mJobStatus.printUniqueId(indentingPrintWriter);
                            indentingPrintWriter.print(" from ");
                            UserHandle.formatUid(indentingPrintWriter, valueAt3.mJobStatus.getSourceUid());
                            if (valueAt3.mChangedAuthorities != null) {
                                indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                                indentingPrintWriter.increaseIndent();
                                if (valueAt3.mTriggerPending) {
                                    indentingPrintWriter.print("Trigger pending: update=");
                                    i2 = size;
                                    TimeUtils.formatDuration(valueAt3.mJobStatus.getTriggerContentUpdateDelay(), indentingPrintWriter);
                                    indentingPrintWriter.print(", max=");
                                    TimeUtils.formatDuration(valueAt3.mJobStatus.getTriggerContentMaxDelay(), indentingPrintWriter);
                                    indentingPrintWriter.println();
                                } else {
                                    i2 = size;
                                }
                                indentingPrintWriter.println("Changed Authorities:");
                                for (int i8 = 0; i8 < valueAt3.mChangedAuthorities.size(); i8++) {
                                    indentingPrintWriter.println(valueAt3.mChangedAuthorities.valueAt(i8));
                                }
                                if (valueAt3.mChangedUris != null) {
                                    indentingPrintWriter.println("          Changed URIs:");
                                    for (int i9 = 0; i9 < valueAt3.mChangedUris.size(); i9++) {
                                        indentingPrintWriter.println(valueAt3.mChangedUris.valueAt(i9));
                                    }
                                }
                                indentingPrintWriter.decreaseIndent();
                            } else {
                                i2 = size;
                                indentingPrintWriter.println();
                            }
                            i7++;
                            size = i2;
                        }
                        i = size;
                        indentingPrintWriter.decreaseIndent();
                        indentingPrintWriter.decreaseIndent();
                    } else {
                        i = size;
                    }
                    i5++;
                    size = i;
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
        boolean z;
        long j2;
        long j3;
        ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap;
        int i;
        ObserverInstance observerInstance;
        int i2;
        ContentObserverController contentObserverController = this;
        Predicate<JobStatus> predicate2 = predicate;
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268036L);
        for (int i3 = 0; i3 < contentObserverController.mTrackedTasks.size(); i3++) {
            JobStatus valueAt = contentObserverController.mTrackedTasks.valueAt(i3);
            if (predicate2.test(valueAt)) {
                long start3 = protoOutputStream.start(2246267895809L);
                valueAt.writeToShortProto(protoOutputStream, 1146756268033L);
                protoOutputStream.write(1120986464258L, valueAt.getSourceUid());
                protoOutputStream.end(start3);
            }
        }
        int size = contentObserverController.mObservers.size();
        int i4 = 0;
        while (i4 < size) {
            int i5 = size;
            long start4 = protoOutputStream.start(2246267895810L);
            int keyAt = contentObserverController.mObservers.keyAt(i4);
            protoOutputStream.write(1120986464257L, keyAt);
            ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap2 = contentObserverController.mObservers.get(keyAt);
            int size2 = arrayMap2.size();
            int i6 = 0;
            while (i6 < size2) {
                ObserverInstance valueAt2 = arrayMap2.valueAt(i6);
                int size3 = valueAt2.mJobs.size();
                int i7 = 0;
                while (true) {
                    if (i7 >= size3) {
                        z = false;
                        break;
                    } else if (predicate2.test(valueAt2.mJobs.valueAt(i7).mJobStatus)) {
                        z = true;
                        break;
                    } else {
                        i7++;
                    }
                }
                if (z) {
                    j2 = start;
                    j3 = start2;
                    long start5 = protoOutputStream.start(2246267895810L);
                    JobInfo.TriggerContentUri keyAt2 = arrayMap2.keyAt(i6);
                    Uri uri = keyAt2.getUri();
                    if (uri != null) {
                        protoOutputStream.write(1138166333441L, uri.toString());
                    }
                    protoOutputStream.write(1120986464258L, keyAt2.getFlags());
                    int i8 = 0;
                    while (i8 < size3) {
                        long start6 = protoOutputStream.start(2246267895811L);
                        JobInstance valueAt3 = valueAt2.mJobs.valueAt(i8);
                        ArrayMap<JobInfo.TriggerContentUri, ObserverInstance> arrayMap3 = arrayMap2;
                        int i9 = size2;
                        valueAt3.mJobStatus.writeToShortProto(protoOutputStream, 1146756268033L);
                        protoOutputStream.write(1120986464258L, valueAt3.mJobStatus.getSourceUid());
                        if (valueAt3.mChangedAuthorities == null) {
                            protoOutputStream.end(start6);
                            observerInstance = valueAt2;
                            i2 = size3;
                        } else {
                            if (valueAt3.mTriggerPending) {
                                observerInstance = valueAt2;
                                i2 = size3;
                                protoOutputStream.write(1112396529667L, valueAt3.mJobStatus.getTriggerContentUpdateDelay());
                                protoOutputStream.write(1112396529668L, valueAt3.mJobStatus.getTriggerContentMaxDelay());
                            } else {
                                observerInstance = valueAt2;
                                i2 = size3;
                            }
                            for (int i10 = 0; i10 < valueAt3.mChangedAuthorities.size(); i10++) {
                                protoOutputStream.write(2237677961221L, valueAt3.mChangedAuthorities.valueAt(i10));
                            }
                            if (valueAt3.mChangedUris != null) {
                                for (int i11 = 0; i11 < valueAt3.mChangedUris.size(); i11++) {
                                    Uri valueAt4 = valueAt3.mChangedUris.valueAt(i11);
                                    if (valueAt4 != null) {
                                        protoOutputStream.write(2237677961222L, valueAt4.toString());
                                    }
                                }
                            }
                            protoOutputStream.end(start6);
                        }
                        i8++;
                        valueAt2 = observerInstance;
                        arrayMap2 = arrayMap3;
                        size2 = i9;
                        size3 = i2;
                    }
                    arrayMap = arrayMap2;
                    i = size2;
                    protoOutputStream.end(start5);
                } else {
                    j2 = start;
                    j3 = start2;
                    arrayMap = arrayMap2;
                    i = size2;
                }
                i6++;
                predicate2 = predicate;
                start2 = j3;
                start = j2;
                arrayMap2 = arrayMap;
                size2 = i;
            }
            protoOutputStream.end(start4);
            i4++;
            contentObserverController = this;
            size = i5;
            predicate2 = predicate;
        }
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }
}
