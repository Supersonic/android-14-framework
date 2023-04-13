package com.android.server.notification;
/* loaded from: classes2.dex */
public class RateEstimator {
    public double mInterarrivalTime = 1000.0d;
    public Long mLastEventTime;

    public float update(long j) {
        float f;
        if (this.mLastEventTime == null) {
            f = 0.0f;
        } else {
            double interarrivalEstimate = getInterarrivalEstimate(j);
            this.mInterarrivalTime = interarrivalEstimate;
            f = (float) (1.0d / interarrivalEstimate);
        }
        this.mLastEventTime = Long.valueOf(j);
        return f;
    }

    public float getRate(long j) {
        if (this.mLastEventTime == null) {
            return 0.0f;
        }
        return (float) (1.0d / getInterarrivalEstimate(j));
    }

    public final double getInterarrivalEstimate(long j) {
        return (this.mInterarrivalTime * 0.8d) + (Math.max((j - this.mLastEventTime.longValue()) / 1000.0d, 5.0E-4d) * 0.19999999999999996d);
    }
}
