package com.android.internal.telephony;

import android.os.SystemClock;
import android.util.LongArrayQueue;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class SlidingWindowEventCounter {
    private final int mNumOccurrences;
    private final LongArrayQueue mTimestampQueueMillis;
    private final long mWindowSizeMillis;

    public SlidingWindowEventCounter(long j, int i) {
        if (j < 0) {
            throw new IllegalArgumentException("windowSizeMillis must be greater or equal to 0");
        }
        if (i <= 1) {
            throw new IllegalArgumentException("numOccurrences must be greater than 1");
        }
        this.mWindowSizeMillis = j;
        this.mNumOccurrences = i;
        this.mTimestampQueueMillis = new LongArrayQueue(i);
    }

    public synchronized boolean addOccurrence() {
        return addOccurrence(SystemClock.elapsedRealtime());
    }

    public synchronized boolean addOccurrence(long j) {
        this.mTimestampQueueMillis.addLast(j);
        if (this.mTimestampQueueMillis.size() > this.mNumOccurrences) {
            this.mTimestampQueueMillis.removeFirst();
        }
        return isInWindow();
    }

    public synchronized boolean isInWindow() {
        boolean z;
        if (this.mTimestampQueueMillis.size() == this.mNumOccurrences) {
            z = this.mTimestampQueueMillis.peekFirst() + this.mWindowSizeMillis > this.mTimestampQueueMillis.peekLast();
        }
        return z;
    }

    @VisibleForTesting
    int getQueuedNumOccurrences() {
        return this.mTimestampQueueMillis.size();
    }

    public synchronized long getWindowSizeMillis() {
        return this.mWindowSizeMillis;
    }

    public synchronized int getNumOccurrences() {
        return this.mNumOccurrences;
    }

    public String getFrequencyString() {
        if (this.mWindowSizeMillis >= 1000) {
            return this.mNumOccurrences + " times within " + (this.mWindowSizeMillis / 1000) + " seconds";
        }
        return this.mNumOccurrences + " times within " + this.mWindowSizeMillis + "ms";
    }

    public String toString() {
        return String.format("SlidingWindowEventCounter=[windowSizeMillis=" + this.mWindowSizeMillis + ", numOccurrences=" + this.mNumOccurrences + ", timestampQueueMillis=" + this.mTimestampQueueMillis + "]", new Object[0]);
    }
}
