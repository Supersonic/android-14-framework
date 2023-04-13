package com.android.internal.p028os;

import android.content.Intent;
import android.p008os.SystemClock;
import com.android.internal.p028os.anr.AnrLatencyTracker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: com.android.internal.os.TimeoutRecord */
/* loaded from: classes4.dex */
public class TimeoutRecord {
    public final boolean mEndTakenBeforeLocks;
    public final long mEndUptimeMillis;
    public final int mKind;
    public final AnrLatencyTracker mLatencyTracker;
    public final String mReason;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.android.internal.os.TimeoutRecord$TimeoutKind */
    /* loaded from: classes4.dex */
    public @interface TimeoutKind {
        public static final int APP_REGISTERED = 7;
        public static final int BROADCAST_RECEIVER = 3;
        public static final int CONTENT_PROVIDER = 6;
        public static final int INPUT_DISPATCH_NO_FOCUSED_WINDOW = 1;
        public static final int INPUT_DISPATCH_WINDOW_UNRESPONSIVE = 2;
        public static final int JOB_SERVICE = 9;
        public static final int SERVICE_EXEC = 5;
        public static final int SERVICE_START = 4;
        public static final int SHORT_FGS_TIMEOUT = 8;
    }

    private TimeoutRecord(int kind, String reason, long endUptimeMillis, boolean endTakenBeforeLocks) {
        this.mKind = kind;
        this.mReason = reason;
        this.mEndUptimeMillis = endUptimeMillis;
        this.mEndTakenBeforeLocks = endTakenBeforeLocks;
        this.mLatencyTracker = new AnrLatencyTracker(kind, endUptimeMillis);
    }

    private static TimeoutRecord endingNow(int kind, String reason) {
        long endUptimeMillis = SystemClock.uptimeMillis();
        return new TimeoutRecord(kind, reason, endUptimeMillis, true);
    }

    private static TimeoutRecord endingApproximatelyNow(int kind, String reason) {
        long endUptimeMillis = SystemClock.uptimeMillis();
        return new TimeoutRecord(kind, reason, endUptimeMillis, false);
    }

    public static TimeoutRecord forBroadcastReceiver(Intent intent) {
        String reason = "Broadcast of " + intent.toString();
        return endingNow(3, reason);
    }

    public static TimeoutRecord forBroadcastReceiver(Intent intent, long timeoutDurationMs) {
        String reason = "Broadcast of " + intent.toString() + ", waited " + timeoutDurationMs + "ms";
        return endingNow(3, reason);
    }

    public static TimeoutRecord forInputDispatchNoFocusedWindow(String reason) {
        return endingNow(1, reason);
    }

    public static TimeoutRecord forInputDispatchWindowUnresponsive(String reason) {
        return endingNow(2, reason);
    }

    public static TimeoutRecord forServiceExec(String reason) {
        return endingNow(5, reason);
    }

    public static TimeoutRecord forServiceStartWithEndTime(String reason, long endUptimeMillis) {
        return new TimeoutRecord(4, reason, endUptimeMillis, true);
    }

    public static TimeoutRecord forContentProvider(String reason) {
        return endingApproximatelyNow(6, reason);
    }

    public static TimeoutRecord forApp(String reason) {
        return endingApproximatelyNow(7, reason);
    }

    public static TimeoutRecord forShortFgsTimeout(String reason) {
        return endingNow(8, reason);
    }

    public static TimeoutRecord forJobService(String reason) {
        return endingNow(9, reason);
    }
}
