package com.android.server.p006am;

import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.expresslog.Counter;
/* renamed from: com.android.server.am.DropboxRateLimiter */
/* loaded from: classes.dex */
public class DropboxRateLimiter {
    public final Clock mClock;
    @GuardedBy({"mErrorClusterRecords"})
    public final ArrayMap<String, ErrorRecord> mErrorClusterRecords;
    public long mLastMapCleanUp;

    /* renamed from: com.android.server.am.DropboxRateLimiter$Clock */
    /* loaded from: classes.dex */
    public interface Clock {
        long uptimeMillis();
    }

    public DropboxRateLimiter() {
        this(new DefaultClock());
    }

    public DropboxRateLimiter(Clock clock) {
        this.mErrorClusterRecords = new ArrayMap<>();
        this.mLastMapCleanUp = 0L;
        this.mClock = clock;
    }

    public RateLimitResult shouldRateLimit(String str, String str2) {
        long uptimeMillis = this.mClock.uptimeMillis();
        synchronized (this.mErrorClusterRecords) {
            maybeRemoveExpiredRecords(uptimeMillis);
            ErrorRecord errorRecord = this.mErrorClusterRecords.get(errorKey(str, str2));
            if (errorRecord == null) {
                this.mErrorClusterRecords.put(errorKey(str, str2), new ErrorRecord(uptimeMillis, 1));
                return new RateLimitResult(false, 0);
            } else if (uptimeMillis - errorRecord.getStartTime() > 600000) {
                int recentlyDroppedCount = recentlyDroppedCount(errorRecord);
                errorRecord.setStartTime(uptimeMillis);
                errorRecord.setCount(1);
                return new RateLimitResult(false, recentlyDroppedCount);
            } else {
                errorRecord.incrementCount();
                if (errorRecord.getCount() > 6) {
                    return new RateLimitResult(true, recentlyDroppedCount(errorRecord));
                }
                return new RateLimitResult(false, 0);
            }
        }
    }

    public final int recentlyDroppedCount(ErrorRecord errorRecord) {
        if (errorRecord == null || errorRecord.getCount() < 6) {
            return 0;
        }
        return errorRecord.getCount() - 6;
    }

    public final void maybeRemoveExpiredRecords(long j) {
        if (j - this.mLastMapCleanUp <= 1800000) {
            return;
        }
        for (int size = this.mErrorClusterRecords.size() - 1; size >= 0; size--) {
            if (j - this.mErrorClusterRecords.valueAt(size).getStartTime() > 1800000) {
                Counter.logIncrement("stability_errors.value_dropbox_buffer_expired_count", this.mErrorClusterRecords.valueAt(size).getCount());
                this.mErrorClusterRecords.removeAt(size);
            }
        }
        this.mLastMapCleanUp = j;
    }

    public void reset() {
        synchronized (this.mErrorClusterRecords) {
            this.mErrorClusterRecords.clear();
        }
        this.mLastMapCleanUp = 0L;
        Slog.i("DropboxRateLimiter", "Rate limiter reset.");
    }

    public String errorKey(String str, String str2) {
        return str + str2;
    }

    /* renamed from: com.android.server.am.DropboxRateLimiter$RateLimitResult */
    /* loaded from: classes.dex */
    public class RateLimitResult {
        public final int mDroppedCountSinceRateLimitActivated;
        public final boolean mShouldRateLimit;

        public RateLimitResult(boolean z, int i) {
            this.mShouldRateLimit = z;
            this.mDroppedCountSinceRateLimitActivated = i;
        }

        public boolean shouldRateLimit() {
            return this.mShouldRateLimit;
        }

        public int droppedCountSinceRateLimitActivated() {
            return this.mDroppedCountSinceRateLimitActivated;
        }

        public String createHeader() {
            return "Dropped-Count: " + this.mDroppedCountSinceRateLimitActivated + "\n";
        }
    }

    /* renamed from: com.android.server.am.DropboxRateLimiter$ErrorRecord */
    /* loaded from: classes.dex */
    public class ErrorRecord {
        public int mCount;
        public long mStartTime;

        public ErrorRecord(long j, int i) {
            this.mStartTime = j;
            this.mCount = i;
        }

        public void setStartTime(long j) {
            this.mStartTime = j;
        }

        public void setCount(int i) {
            this.mCount = i;
        }

        public void incrementCount() {
            this.mCount++;
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        public int getCount() {
            return this.mCount;
        }
    }

    /* renamed from: com.android.server.am.DropboxRateLimiter$DefaultClock */
    /* loaded from: classes.dex */
    public static class DefaultClock implements Clock {
        public DefaultClock() {
        }

        @Override // com.android.server.p006am.DropboxRateLimiter.Clock
        public long uptimeMillis() {
            return SystemClock.uptimeMillis();
        }
    }
}
