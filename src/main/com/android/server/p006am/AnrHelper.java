package com.android.server.p006am;

import android.content.pm.ApplicationInfo;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.TimeoutRecord;
import com.android.server.p014wm.WindowProcessController;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: com.android.server.am.AnrHelper */
/* loaded from: classes.dex */
public class AnrHelper {
    @GuardedBy({"mAnrRecords"})
    public final ArrayList<AnrRecord> mAnrRecords;
    public final ExecutorService mAuxiliaryTaskExecutor;
    public long mLastAnrTimeMs;
    @GuardedBy({"mAnrRecords"})
    public int mProcessingPid;
    public final AtomicBoolean mRunning;
    public final ActivityManagerService mService;
    public static final long EXPIRED_REPORT_TIME_MS = TimeUnit.SECONDS.toMillis(10);
    public static final long CONSECUTIVE_ANR_TIME_MS = TimeUnit.MINUTES.toMillis(2);
    public static final ThreadFactory sDefaultThreadFactory = new ThreadFactory() { // from class: com.android.server.am.AnrHelper$$ExternalSyntheticLambda0
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            Thread lambda$static$0;
            lambda$static$0 = AnrHelper.lambda$static$0(runnable);
            return lambda$static$0;
        }
    };

    public static /* synthetic */ Thread lambda$static$0(Runnable runnable) {
        return new Thread(runnable, "AnrAuxiliaryTaskExecutor");
    }

    public AnrHelper(ActivityManagerService activityManagerService) {
        this(activityManagerService, new ThreadPoolExecutor(0, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(), sDefaultThreadFactory));
    }

    @VisibleForTesting
    public AnrHelper(ActivityManagerService activityManagerService, ExecutorService executorService) {
        this.mAnrRecords = new ArrayList<>();
        this.mRunning = new AtomicBoolean(false);
        this.mLastAnrTimeMs = 0L;
        this.mProcessingPid = -1;
        this.mService = activityManagerService;
        this.mAuxiliaryTaskExecutor = executorService;
    }

    public void appNotResponding(ProcessRecord processRecord, TimeoutRecord timeoutRecord) {
        appNotResponding(processRecord, null, null, null, null, false, timeoutRecord, false);
    }

    public void appNotResponding(ProcessRecord processRecord, String str, ApplicationInfo applicationInfo, String str2, WindowProcessController windowProcessController, boolean z, TimeoutRecord timeoutRecord, boolean z2) {
        try {
            timeoutRecord.mLatencyTracker.appNotRespondingStarted();
            int i = processRecord.mPid;
            timeoutRecord.mLatencyTracker.waitingOnAnrRecordLockStarted();
            synchronized (this.mAnrRecords) {
                timeoutRecord.mLatencyTracker.waitingOnAnrRecordLockEnded();
                if (i == 0) {
                    Slog.i("ActivityManager", "Skip zero pid ANR, process=" + processRecord.processName);
                } else if (this.mProcessingPid == i) {
                    Slog.i("ActivityManager", "Skip duplicated ANR, pid=" + i + " " + timeoutRecord.mReason);
                } else {
                    for (int size = this.mAnrRecords.size() - 1; size >= 0; size--) {
                        if (this.mAnrRecords.get(size).mPid == i) {
                            Slog.i("ActivityManager", "Skip queued ANR, pid=" + i + " " + timeoutRecord.mReason);
                        }
                    }
                    timeoutRecord.mLatencyTracker.anrRecordPlacingOnQueueWithSize(this.mAnrRecords.size());
                    this.mAnrRecords.add(new AnrRecord(processRecord, str, applicationInfo, str2, windowProcessController, z, this.mAuxiliaryTaskExecutor, timeoutRecord, z2));
                    startAnrConsumerIfNeeded();
                }
            }
        } finally {
            timeoutRecord.mLatencyTracker.appNotRespondingEnded();
        }
    }

    public final void startAnrConsumerIfNeeded() {
        if (this.mRunning.compareAndSet(false, true)) {
            new AnrConsumerThread().start();
        }
    }

    /* renamed from: com.android.server.am.AnrHelper$AnrConsumerThread */
    /* loaded from: classes.dex */
    public class AnrConsumerThread extends Thread {
        public AnrConsumerThread() {
            super("AnrConsumer");
        }

        public final AnrRecord next() {
            synchronized (AnrHelper.this.mAnrRecords) {
                if (AnrHelper.this.mAnrRecords.isEmpty()) {
                    return null;
                }
                AnrRecord anrRecord = (AnrRecord) AnrHelper.this.mAnrRecords.remove(0);
                AnrHelper.this.mProcessingPid = anrRecord.mPid;
                anrRecord.mTimeoutRecord.mLatencyTracker.anrRecordsQueueSizeWhenPopped(AnrHelper.this.mAnrRecords.size());
                return anrRecord;
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                AnrRecord next = next();
                if (next == null) {
                    break;
                }
                AnrHelper.this.scheduleBinderHeavyHitterAutoSamplerIfNecessary();
                int i = next.mApp.mPid;
                if (i != next.mPid) {
                    Slog.i("ActivityManager", "Skip ANR with mismatched pid=" + next.mPid + ", current pid=" + i);
                } else {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    long j = uptimeMillis - next.mTimestamp;
                    boolean z = j > AnrHelper.EXPIRED_REPORT_TIME_MS;
                    next.appNotResponding(z);
                    long uptimeMillis2 = SystemClock.uptimeMillis();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Completed ANR of ");
                    sb.append(next.mApp.processName);
                    sb.append(" in ");
                    sb.append(uptimeMillis2 - uptimeMillis);
                    sb.append("ms, latency ");
                    sb.append(j);
                    sb.append(z ? "ms (expired, only dump ANR app)" : "ms");
                    Slog.d("ActivityManager", sb.toString());
                }
            }
            AnrHelper.this.mRunning.set(false);
            synchronized (AnrHelper.this.mAnrRecords) {
                AnrHelper.this.mProcessingPid = -1;
                if (!AnrHelper.this.mAnrRecords.isEmpty()) {
                    AnrHelper.this.startAnrConsumerIfNeeded();
                }
            }
        }
    }

    public final void scheduleBinderHeavyHitterAutoSamplerIfNecessary() {
        try {
            Trace.traceBegin(64L, "scheduleBinderHeavyHitterAutoSamplerIfNecessary()");
            long uptimeMillis = SystemClock.uptimeMillis();
            if (this.mLastAnrTimeMs + CONSECUTIVE_ANR_TIME_MS > uptimeMillis) {
                this.mService.scheduleBinderHeavyHitterAutoSampler();
            }
            this.mLastAnrTimeMs = uptimeMillis;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    /* renamed from: com.android.server.am.AnrHelper$AnrRecord */
    /* loaded from: classes.dex */
    public static class AnrRecord {
        public final boolean mAboveSystem;
        public final String mActivityShortComponentName;
        public final ProcessRecord mApp;
        public final ApplicationInfo mAppInfo;
        public final ExecutorService mAuxiliaryTaskExecutor;
        public final boolean mIsContinuousAnr;
        public final WindowProcessController mParentProcess;
        public final String mParentShortComponentName;
        public final int mPid;
        public final TimeoutRecord mTimeoutRecord;
        public final long mTimestamp = SystemClock.uptimeMillis();

        public AnrRecord(ProcessRecord processRecord, String str, ApplicationInfo applicationInfo, String str2, WindowProcessController windowProcessController, boolean z, ExecutorService executorService, TimeoutRecord timeoutRecord, boolean z2) {
            this.mApp = processRecord;
            this.mPid = processRecord.mPid;
            this.mActivityShortComponentName = str;
            this.mParentShortComponentName = str2;
            this.mTimeoutRecord = timeoutRecord;
            this.mAppInfo = applicationInfo;
            this.mParentProcess = windowProcessController;
            this.mAboveSystem = z;
            this.mAuxiliaryTaskExecutor = executorService;
            this.mIsContinuousAnr = z2;
        }

        public void appNotResponding(boolean z) {
            try {
                this.mTimeoutRecord.mLatencyTracker.anrProcessingStarted();
                this.mApp.mErrorState.appNotResponding(this.mActivityShortComponentName, this.mAppInfo, this.mParentShortComponentName, this.mParentProcess, this.mAboveSystem, this.mTimeoutRecord, this.mAuxiliaryTaskExecutor, z, this.mIsContinuousAnr);
            } finally {
                this.mTimeoutRecord.mLatencyTracker.anrProcessingEnded();
            }
        }
    }
}
