package com.android.internal.p028os.anr;

import android.p008os.SystemClock;
import android.p008os.Trace;
import com.android.internal.util.FrameworkStatsLog;
import java.util.concurrent.atomic.AtomicInteger;
/* renamed from: com.android.internal.os.anr.AnrLatencyTracker */
/* loaded from: classes4.dex */
public class AnrLatencyTracker implements AutoCloseable {
    private static final AtomicInteger sNextAnrRecordPlacedOnQueueCookieGenerator = new AtomicInteger();
    private long mAMSLockLastTryAcquireStart;
    private long mAnrProcessingStartedUptime;
    private int mAnrQueueSize;
    private long mAnrRecordLastTryAcquireStart;
    private long mAnrRecordPlacedOnQueueUptime;
    private long mAnrTriggerUptime;
    private int mAnrType;
    private long mAppNotRespondingStartUptime;
    private long mCriticalEventLoglastCallUptime;
    private long mCurrentPsiStateLastCallUptime;
    private long mDumpStackTracesStartUptime;
    private long mEndUptime;
    private long mExtraPidsDumpingStartUptime;
    private long mFirstPidsDumpingStartUptime;
    private long mGlobalLockLastTryAcquireStart;
    private long mNativePidsDumpingStartUptime;
    private long mPidLockLastTryAcquireStart;
    private long mProcLockLastTryAcquireStart;
    private long mProcessCpuTrackerMethodsLastCallUptime;
    private long mUpdateCpuStatsNowLastCallUptime;
    private long mUpdateCpuStatsNowTotalLatency = 0;
    private long mCurrentPsiStateTotalLatency = 0;
    private long mProcessCpuTrackerMethodsTotalLatency = 0;
    private long mCriticalEventLogTotalLatency = 0;
    private long mGlobalLockTotalContention = 0;
    private long mPidLockTotalContention = 0;
    private long mAMSLockTotalContention = 0;
    private long mProcLockTotalContention = 0;
    private long mAnrRecordLockTotalContention = 0;
    private int mDumpedProcessesCount = 0;
    private long mFirstPidsDumpingDuration = 0;
    private long mNativePidsDumpingDuration = 0;
    private long mExtraPidsDumpingDuration = 0;
    private boolean mIsPushed = false;
    private boolean mIsSkipped = false;
    private final int mAnrRecordPlacedOnQueueCookie = sNextAnrRecordPlacedOnQueueCookieGenerator.incrementAndGet();

    public AnrLatencyTracker(int timeoutKind, long anrTriggerUptime) {
        this.mAnrTriggerUptime = anrTriggerUptime;
        this.mAnrType = timeoutKindToAnrType(timeoutKind);
    }

    public void appNotRespondingStarted() {
        this.mAppNotRespondingStartUptime = getUptimeMillis();
        Trace.traceBegin(64L, "AnrHelper#appNotResponding()");
    }

    public void appNotRespondingEnded() {
        Trace.traceEnd(64L);
    }

    public void anrRecordPlacingOnQueueWithSize(int queueSize) {
        this.mAnrRecordPlacedOnQueueUptime = getUptimeMillis();
        Trace.asyncTraceBegin(64L, "anrRecordPlacedOnQueue", this.mAnrRecordPlacedOnQueueCookie);
        this.mAnrQueueSize = queueSize;
        Trace.traceCounter(64L, "anrRecordsQueueSize", queueSize + 1);
    }

    public void anrProcessingStarted() {
        this.mAnrProcessingStartedUptime = getUptimeMillis();
        Trace.asyncTraceEnd(64L, "anrRecordPlacedOnQueue", this.mAnrRecordPlacedOnQueueCookie);
        Trace.traceBegin(64L, "anrProcessing");
    }

    public void anrProcessingEnded() {
        Trace.traceEnd(64L);
        close();
    }

    public void dumpStackTracesStarted() {
        this.mDumpStackTracesStartUptime = getUptimeMillis();
        Trace.traceBegin(64L, "dumpStackTraces()");
    }

    public void dumpStackTracesEnded() {
        Trace.traceEnd(64L);
    }

    public void updateCpuStatsNowCalled() {
        this.mUpdateCpuStatsNowLastCallUptime = getUptimeMillis();
        Trace.traceBegin(64L, "updateCpuStatsNow()");
    }

    public void updateCpuStatsNowReturned() {
        this.mUpdateCpuStatsNowTotalLatency += getUptimeMillis() - this.mUpdateCpuStatsNowLastCallUptime;
        Trace.traceEnd(64L);
    }

    public void currentPsiStateCalled() {
        this.mCurrentPsiStateLastCallUptime = getUptimeMillis();
        Trace.traceBegin(64L, "currentPsiState()");
    }

    public void currentPsiStateReturned() {
        this.mCurrentPsiStateTotalLatency += getUptimeMillis() - this.mCurrentPsiStateLastCallUptime;
        Trace.traceEnd(64L);
    }

    public void processCpuTrackerMethodsCalled() {
        this.mProcessCpuTrackerMethodsLastCallUptime = getUptimeMillis();
        Trace.traceBegin(64L, "processCpuTracker");
    }

    public void processCpuTrackerMethodsReturned() {
        this.mProcessCpuTrackerMethodsTotalLatency += getUptimeMillis() - this.mProcessCpuTrackerMethodsLastCallUptime;
        Trace.traceEnd(64L);
    }

    public void criticalEventLogStarted() {
        this.mCriticalEventLoglastCallUptime = getUptimeMillis();
        Trace.traceBegin(64L, "criticalEventLog");
    }

    public void criticalEventLogEnded() {
        this.mCriticalEventLogTotalLatency += getUptimeMillis() - this.mCriticalEventLoglastCallUptime;
        Trace.traceEnd(64L);
    }

    public void nativePidCollectionStarted() {
        Trace.traceBegin(64L, "nativePidCollection");
    }

    public void nativePidCollectionEnded() {
        Trace.traceEnd(64L);
    }

    public void dumpingPidStarted(int pid) {
        Trace.traceBegin(64L, "dumpingPid#" + pid);
    }

    public void dumpingPidEnded() {
        this.mDumpedProcessesCount++;
        Trace.traceEnd(64L);
    }

    public void dumpingFirstPidsStarted() {
        this.mFirstPidsDumpingStartUptime = getUptimeMillis();
        Trace.traceBegin(64L, "dumpingFirstPids");
    }

    public void dumpingFirstPidsEnded() {
        this.mFirstPidsDumpingDuration = getUptimeMillis() - this.mFirstPidsDumpingStartUptime;
        Trace.traceEnd(64L);
    }

    public void dumpingNativePidsStarted() {
        this.mNativePidsDumpingStartUptime = getUptimeMillis();
        Trace.traceBegin(64L, "dumpingNativePids");
    }

    public void dumpingNativePidsEnded() {
        this.mNativePidsDumpingDuration = getUptimeMillis() - this.mNativePidsDumpingStartUptime;
        Trace.traceEnd(64L);
    }

    public void dumpingExtraPidsStarted() {
        this.mExtraPidsDumpingStartUptime = getUptimeMillis();
        Trace.traceBegin(64L, "dumpingExtraPids");
    }

    public void dumpingExtraPidsEnded() {
        this.mExtraPidsDumpingDuration = getUptimeMillis() - this.mExtraPidsDumpingStartUptime;
        Trace.traceEnd(64L);
    }

    public void waitingOnGlobalLockStarted() {
        this.mGlobalLockLastTryAcquireStart = getUptimeMillis();
        Trace.traceBegin(64L, "globalLock");
    }

    public void waitingOnGlobalLockEnded() {
        this.mGlobalLockTotalContention += getUptimeMillis() - this.mGlobalLockLastTryAcquireStart;
        Trace.traceEnd(64L);
    }

    public void waitingOnPidLockStarted() {
        this.mPidLockLastTryAcquireStart = getUptimeMillis();
        Trace.traceBegin(64L, "pidLockContention");
    }

    public void waitingOnPidLockEnded() {
        this.mPidLockTotalContention += getUptimeMillis() - this.mPidLockLastTryAcquireStart;
        Trace.traceEnd(64L);
    }

    public void waitingOnAMSLockStarted() {
        this.mAMSLockLastTryAcquireStart = getUptimeMillis();
        Trace.traceBegin(64L, "AMSLockContention");
    }

    public void waitingOnAMSLockEnded() {
        this.mAMSLockTotalContention += getUptimeMillis() - this.mAMSLockLastTryAcquireStart;
        Trace.traceEnd(64L);
    }

    public void waitingOnProcLockStarted() {
        this.mProcLockLastTryAcquireStart = getUptimeMillis();
        Trace.traceBegin(64L, "procLockContention");
    }

    public void waitingOnProcLockEnded() {
        this.mProcLockTotalContention += getUptimeMillis() - this.mProcLockLastTryAcquireStart;
        Trace.traceEnd(64L);
    }

    public void waitingOnAnrRecordLockStarted() {
        this.mAnrRecordLastTryAcquireStart = getUptimeMillis();
        Trace.traceBegin(64L, "anrRecordLockContention");
    }

    public void waitingOnAnrRecordLockEnded() {
        this.mAnrRecordLockTotalContention += getUptimeMillis() - this.mAnrRecordLastTryAcquireStart;
        Trace.traceEnd(64L);
    }

    public void anrRecordsQueueSizeWhenPopped(int queueSize) {
        Trace.traceCounter(64L, "anrRecordsQueueSize", queueSize);
    }

    public void anrSkippedProcessErrorStateRecordAppNotResponding() {
        anrSkipped("appNotResponding");
    }

    public void anrSkippedDumpStackTraces() {
        anrSkipped("dumpStackTraces");
    }

    public String dumpAsCommaSeparatedArrayWithHeader() {
        return "DurationsV2: " + this.mAnrTriggerUptime + "," + (this.mAppNotRespondingStartUptime - this.mAnrTriggerUptime) + "," + (this.mAnrRecordPlacedOnQueueUptime - this.mAppNotRespondingStartUptime) + "," + (this.mAnrProcessingStartedUptime - this.mAnrRecordPlacedOnQueueUptime) + "," + (this.mDumpStackTracesStartUptime - this.mAnrProcessingStartedUptime) + "," + this.mUpdateCpuStatsNowTotalLatency + "," + this.mCurrentPsiStateTotalLatency + "," + this.mProcessCpuTrackerMethodsTotalLatency + "," + this.mCriticalEventLogTotalLatency + "," + this.mGlobalLockTotalContention + "," + this.mPidLockTotalContention + "," + this.mAMSLockTotalContention + "," + this.mProcLockTotalContention + "," + this.mAnrRecordLockTotalContention + "," + this.mAnrQueueSize + "," + (this.mFirstPidsDumpingStartUptime - this.mDumpStackTracesStartUptime) + "\n\n";
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (!this.mIsSkipped && !this.mIsPushed) {
            this.mEndUptime = getUptimeMillis();
            pushAtom();
            this.mIsPushed = true;
        }
    }

    private static int timeoutKindToAnrType(int timeoutKind) {
        switch (timeoutKind) {
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 1;
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
                return 6;
            case 7:
            default:
                return 0;
            case 8:
                return 7;
        }
    }

    public long getUptimeMillis() {
        return SystemClock.uptimeMillis();
    }

    public void pushAtom() {
        long j = this.mEndUptime;
        long j2 = this.mAnrTriggerUptime;
        long j3 = this.mAppNotRespondingStartUptime;
        long j4 = this.mAnrRecordPlacedOnQueueUptime;
        long j5 = this.mAnrProcessingStartedUptime;
        FrameworkStatsLog.write(516, j - j2, this.mFirstPidsDumpingStartUptime - j2, j3 - j2, j4 - j3, j5 - j4, this.mDumpStackTracesStartUptime - j5, this.mFirstPidsDumpingDuration + this.mNativePidsDumpingDuration + this.mExtraPidsDumpingDuration, this.mUpdateCpuStatsNowTotalLatency, this.mCurrentPsiStateTotalLatency, this.mProcessCpuTrackerMethodsTotalLatency, this.mCriticalEventLogTotalLatency, this.mGlobalLockTotalContention, this.mPidLockTotalContention, this.mAMSLockTotalContention, this.mProcLockTotalContention, this.mAnrRecordLockTotalContention, this.mAnrQueueSize, this.mAnrType, this.mDumpedProcessesCount);
    }

    private void anrSkipped(String method) {
        Trace.instant(64L, "AnrSkipped@" + method);
        this.mIsSkipped = true;
    }
}
