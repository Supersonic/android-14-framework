package com.android.server.job.controllers;

import android.content.Context;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.proto.ProtoOutputStream;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.controllers.idle.CarIdlenessTracker;
import com.android.server.job.controllers.idle.DeviceIdlenessTracker;
import com.android.server.job.controllers.idle.IdlenessListener;
import com.android.server.job.controllers.idle.IdlenessTracker;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class IdleController extends RestrictingController implements IdlenessListener {
    public final FlexibilityController mFlexibilityController;
    public IdlenessTracker mIdleTracker;
    public final ArraySet<JobStatus> mTrackedTasks;

    public IdleController(JobSchedulerService jobSchedulerService, FlexibilityController flexibilityController) {
        super(jobSchedulerService);
        this.mTrackedTasks = new ArraySet<>();
        initIdleStateTracking(this.mContext);
        this.mFlexibilityController = flexibilityController;
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.hasIdleConstraint()) {
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mTrackedTasks.add(jobStatus);
            jobStatus.setTrackingController(8);
            jobStatus.setIdleConstraintSatisfied(millis, this.mIdleTracker.isIdle());
        }
    }

    @Override // com.android.server.job.controllers.RestrictingController
    public void startTrackingRestrictedJobLocked(JobStatus jobStatus) {
        maybeStartTrackingJobLocked(jobStatus, null);
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.clearTrackingController(8)) {
            this.mTrackedTasks.remove(jobStatus);
        }
    }

    @Override // com.android.server.job.controllers.RestrictingController
    public void stopTrackingRestrictedJobLocked(JobStatus jobStatus) {
        if (jobStatus.hasIdleConstraint()) {
            return;
        }
        maybeStopTrackingJobLocked(jobStatus, null);
    }

    @Override // com.android.server.job.controllers.idle.IdlenessListener
    public void reportNewIdleState(boolean z) {
        synchronized (this.mLock) {
            logDeviceWideConstraintStateToStatsd(4, z);
            long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mFlexibilityController.setConstraintSatisfied(4, z, millis);
            for (int size = this.mTrackedTasks.size() - 1; size >= 0; size--) {
                this.mTrackedTasks.valueAt(size).setIdleConstraintSatisfied(millis, z);
            }
        }
        this.mStateChangedListener.onControllerStateChanged(this.mTrackedTasks);
    }

    public final void initIdleStateTracking(Context context) {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            this.mIdleTracker = new CarIdlenessTracker();
        } else {
            this.mIdleTracker = new DeviceIdlenessTracker();
        }
        this.mIdleTracker.startTracking(context, this);
    }

    @Override // com.android.server.job.controllers.StateController
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        indentingPrintWriter.println("Currently idle: " + this.mIdleTracker.isIdle());
        indentingPrintWriter.println("Idleness tracker:");
        this.mIdleTracker.dump(indentingPrintWriter);
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
        long start2 = protoOutputStream.start(1146756268038L);
        protoOutputStream.write(1133871366145L, this.mIdleTracker.isIdle());
        this.mIdleTracker.dump(protoOutputStream, 1146756268035L);
        for (int i = 0; i < this.mTrackedTasks.size(); i++) {
            JobStatus valueAt = this.mTrackedTasks.valueAt(i);
            if (predicate.test(valueAt)) {
                long start3 = protoOutputStream.start(2246267895810L);
                valueAt.writeToShortProto(protoOutputStream, 1146756268033L);
                protoOutputStream.write(1120986464258L, valueAt.getSourceUid());
                protoOutputStream.end(start3);
            }
        }
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }
}
