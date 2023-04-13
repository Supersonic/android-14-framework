package com.android.server.display.utils;

import java.time.Clock;
/* loaded from: classes.dex */
public class History {
    public Clock mClock;
    public int mCount;
    public int mEnd;
    public int mSize;
    public int mStart;
    public long[] mTimes;
    public float[] mValues;

    public History(int i) {
        this(i, Clock.systemUTC());
    }

    public History(int i, Clock clock) {
        this.mSize = i;
        this.mCount = 0;
        this.mStart = 0;
        this.mEnd = 0;
        this.mTimes = new long[i];
        this.mValues = new float[i];
        this.mClock = clock;
    }

    public void add(float f) {
        this.mTimes[this.mEnd] = this.mClock.millis();
        float[] fArr = this.mValues;
        int i = this.mEnd;
        fArr[i] = f;
        int i2 = this.mCount;
        int i3 = this.mSize;
        if (i2 < i3) {
            this.mCount = i2 + 1;
        } else {
            this.mStart = (this.mStart + 1) % i3;
        }
        this.mEnd = (i + 1) % i3;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        while (i < this.mCount) {
            int i2 = (this.mStart + i) % this.mSize;
            long j = this.mTimes[i2];
            float f = this.mValues[i2];
            sb.append(f + " @ " + j);
            i++;
            if (i != this.mCount) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
