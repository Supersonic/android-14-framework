package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.job.JobSchedulerService;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class StorageController extends StateController {
    public static final boolean DEBUG;
    public final StorageTracker mStorageTracker;
    public final ArraySet<JobStatus> mTrackedTasks;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.Storage", 3);
    }

    @VisibleForTesting
    public StorageTracker getTracker() {
        return this.mStorageTracker;
    }

    public StorageController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
        this.mTrackedTasks = new ArraySet<>();
        StorageTracker storageTracker = new StorageTracker();
        this.mStorageTracker = storageTracker;
        storageTracker.startTracking();
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.hasStorageNotLowConstraint()) {
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mTrackedTasks.add(jobStatus);
            jobStatus.setTrackingController(16);
            jobStatus.setStorageNotLowConstraintSatisfied(millis, this.mStorageTracker.isStorageNotLow());
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.clearTrackingController(16)) {
            this.mTrackedTasks.remove(jobStatus);
        }
    }

    public final void maybeReportNewStorageState() {
        boolean z;
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        boolean isStorageNotLow = this.mStorageTracker.isStorageNotLow();
        synchronized (this.mLock) {
            z = false;
            for (int size = this.mTrackedTasks.size() - 1; size >= 0; size--) {
                z |= this.mTrackedTasks.valueAt(size).setStorageNotLowConstraintSatisfied(millis, isStorageNotLow);
            }
        }
        if (isStorageNotLow) {
            this.mStateChangedListener.onRunJobNow(null);
        } else if (z) {
            this.mStateChangedListener.onControllerStateChanged(this.mTrackedTasks);
        }
    }

    /* loaded from: classes.dex */
    public final class StorageTracker extends BroadcastReceiver {
        public int mLastStorageSeq = -1;
        public boolean mStorageLow;

        public StorageTracker() {
        }

        public void startTracking() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.DEVICE_STORAGE_LOW");
            intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
            StorageController.this.mContext.registerReceiver(this, intentFilter);
        }

        public boolean isStorageNotLow() {
            return !this.mStorageLow;
        }

        public int getSeq() {
            return this.mLastStorageSeq;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            onReceiveInternal(intent);
        }

        @VisibleForTesting
        public void onReceiveInternal(Intent intent) {
            String action = intent.getAction();
            this.mLastStorageSeq = intent.getIntExtra("seq", this.mLastStorageSeq);
            if ("android.intent.action.DEVICE_STORAGE_LOW".equals(action)) {
                if (StorageController.DEBUG) {
                    Slog.d("JobScheduler.Storage", "Available storage too low to do work. @ " + JobSchedulerService.sElapsedRealtimeClock.millis());
                }
                this.mStorageLow = true;
                StorageController.this.maybeReportNewStorageState();
            } else if ("android.intent.action.DEVICE_STORAGE_OK".equals(action)) {
                if (StorageController.DEBUG) {
                    Slog.d("JobScheduler.Storage", "Available storage high enough to do work. @ " + JobSchedulerService.sElapsedRealtimeClock.millis());
                }
                this.mStorageLow = false;
                StorageController.this.maybeReportNewStorageState();
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        indentingPrintWriter.println("Not low: " + this.mStorageTracker.isStorageNotLow());
        indentingPrintWriter.println("Sequence: " + this.mStorageTracker.getSeq());
        indentingPrintWriter.println();
        for (int i = 0; i < this.mTrackedTasks.size(); i++) {
            JobStatus valueAt = this.mTrackedTasks.valueAt(i);
            if (predicate.test(valueAt)) {
                indentingPrintWriter.print("#");
                valueAt.printUniqueId(indentingPrintWriter);
                indentingPrintWriter.print(" from ");
                UserHandle.formatUid(indentingPrintWriter, valueAt.getSourceUid());
                indentingPrintWriter.println();
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268039L);
        protoOutputStream.write(1133871366145L, this.mStorageTracker.isStorageNotLow());
        protoOutputStream.write(1120986464258L, this.mStorageTracker.getSeq());
        for (int i = 0; i < this.mTrackedTasks.size(); i++) {
            JobStatus valueAt = this.mTrackedTasks.valueAt(i);
            if (predicate.test(valueAt)) {
                long start3 = protoOutputStream.start(2246267895811L);
                valueAt.writeToShortProto(protoOutputStream, 1146756268033L);
                protoOutputStream.write(1120986464258L, valueAt.getSourceUid());
                protoOutputStream.end(start3);
            }
        }
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }
}
