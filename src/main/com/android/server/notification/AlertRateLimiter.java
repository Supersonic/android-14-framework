package com.android.server.notification;
/* loaded from: classes2.dex */
public class AlertRateLimiter {
    public long mLastNotificationMillis = 0;

    public boolean shouldRateLimitAlert(long j) {
        long j2 = j - this.mLastNotificationMillis;
        if (j2 < 0 || j2 < 1000) {
            return true;
        }
        this.mLastNotificationMillis = j;
        return false;
    }
}
