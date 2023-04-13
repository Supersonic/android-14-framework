package com.android.server.location.gnss;
/* loaded from: classes.dex */
public class ExponentialBackOff {
    public long mCurrentIntervalMillis;
    public final long mInitIntervalMillis;
    public final long mMaxIntervalMillis;

    public ExponentialBackOff(long j, long j2) {
        this.mInitIntervalMillis = j;
        this.mMaxIntervalMillis = j2;
        this.mCurrentIntervalMillis = j / 2;
    }

    public long nextBackoffMillis() {
        long j = this.mCurrentIntervalMillis;
        long j2 = this.mMaxIntervalMillis;
        if (j > j2) {
            return j2;
        }
        long j3 = j * 2;
        this.mCurrentIntervalMillis = j3;
        return j3;
    }

    public void reset() {
        this.mCurrentIntervalMillis = this.mInitIntervalMillis / 2;
    }

    public String toString() {
        return "ExponentialBackOff{mInitIntervalMillis=" + this.mInitIntervalMillis + ", mMaxIntervalMillis=" + this.mMaxIntervalMillis + ", mCurrentIntervalMillis=" + this.mCurrentIntervalMillis + '}';
    }
}
