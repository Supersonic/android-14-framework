package com.android.server.display;
/* loaded from: classes.dex */
public class HbmEvent {
    public long mEndTimeMillis;
    public long mStartTimeMillis;

    public HbmEvent(long j, long j2) {
        this.mStartTimeMillis = j;
        this.mEndTimeMillis = j2;
    }

    public long getStartTimeMillis() {
        return this.mStartTimeMillis;
    }

    public long getEndTimeMillis() {
        return this.mEndTimeMillis;
    }

    public String toString() {
        return "HbmEvent: {startTimeMillis:" + this.mStartTimeMillis + ", endTimeMillis: " + this.mEndTimeMillis + "}, total: " + ((this.mEndTimeMillis - this.mStartTimeMillis) / 1000) + "]";
    }
}
