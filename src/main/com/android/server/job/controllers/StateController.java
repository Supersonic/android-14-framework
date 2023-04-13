package com.android.server.job.controllers;

import android.content.Context;
import android.provider.DeviceConfig;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.StateChangedListener;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class StateController {
    public final JobSchedulerService.Constants mConstants;
    public final Context mContext;
    public final Object mLock;
    public final JobSchedulerService mService;
    public final StateChangedListener mStateChangedListener;

    public void dumpConstants(IndentingPrintWriter indentingPrintWriter) {
    }

    public void dumpConstants(ProtoOutputStream protoOutputStream) {
    }

    public abstract void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate);

    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
    }

    public void evaluateStateLocked(JobStatus jobStatus) {
    }

    public abstract void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2);

    public abstract void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2);

    public void onAppRemovedLocked(String str, int i) {
    }

    @GuardedBy({"mLock"})
    public void onBatteryStateChangedLocked() {
    }

    public void onConstantsUpdatedLocked() {
    }

    public void onSystemServicesReady() {
    }

    @GuardedBy({"mLock"})
    public void onUidBiasChangedLocked(int i, int i2, int i3) {
    }

    public void onUserAddedLocked(int i) {
    }

    public void onUserRemovedLocked(int i) {
    }

    public void prepareForExecutionLocked(JobStatus jobStatus) {
    }

    public void prepareForUpdatedConstantsLocked() {
    }

    public void processConstantLocked(DeviceConfig.Properties properties, String str) {
    }

    public void reevaluateStateLocked(int i) {
    }

    public void rescheduleForFailureLocked(JobStatus jobStatus, JobStatus jobStatus2) {
    }

    public void unprepareFromExecutionLocked(JobStatus jobStatus) {
    }

    public StateController(JobSchedulerService jobSchedulerService) {
        this.mService = jobSchedulerService;
        this.mStateChangedListener = jobSchedulerService;
        this.mContext = jobSchedulerService.getTestableContext();
        this.mLock = jobSchedulerService.getLock();
        this.mConstants = jobSchedulerService.getConstants();
    }

    public boolean wouldBeReadyWithConstraintLocked(JobStatus jobStatus, int i) {
        boolean wouldBeReadyWithConstraint = jobStatus.wouldBeReadyWithConstraint(i);
        if (JobSchedulerService.DEBUG) {
            Slog.v("JobScheduler.SC", "wouldBeReadyWithConstraintLocked: " + jobStatus.toShortString() + " constraint=" + i + " readyWithConstraint=" + wouldBeReadyWithConstraint);
        }
        if (wouldBeReadyWithConstraint) {
            return this.mService.areComponentsInPlaceLocked(jobStatus);
        }
        return false;
    }

    public void logDeviceWideConstraintStateToStatsd(int i, boolean z) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.DEVICE_WIDE_JOB_CONSTRAINT_CHANGED, JobStatus.getProtoConstraint(i), z ? 2 : 1);
    }

    public static String packageToString(int i, String str) {
        return "<" + i + ">" + str;
    }
}
