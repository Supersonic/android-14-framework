package com.android.server.display.utils;
/* loaded from: classes.dex */
public class RollingBuffer {
    public int mCount;
    public int mEnd;
    public int mStart;
    public int mSize = 50;
    public long[] mTimes = new long[50];
    public float[] mValues = new float[50];

    public RollingBuffer() {
        clear();
    }

    public void add(long j, float f) {
        if (this.mCount >= this.mSize) {
            expandBuffer();
        }
        long[] jArr = this.mTimes;
        int i = this.mEnd;
        jArr[i] = j;
        this.mValues[i] = f;
        this.mEnd = (i + 1) % this.mSize;
        this.mCount++;
    }

    public int size() {
        return this.mCount;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public long getTime(int i) {
        return this.mTimes[offsetOf(i)];
    }

    public float getValue(int i) {
        return this.mValues[offsetOf(i)];
    }

    public void truncate(long j) {
        if (isEmpty() || getTime(0) >= j) {
            return;
        }
        int latestIndexBefore = getLatestIndexBefore(j);
        int offsetOf = offsetOf(latestIndexBefore);
        this.mStart = offsetOf;
        this.mCount -= latestIndexBefore;
        this.mTimes[offsetOf] = j;
    }

    public void clear() {
        this.mCount = 0;
        this.mStart = 0;
        this.mEnd = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        while (i < this.mCount) {
            int offsetOf = offsetOf(i);
            sb.append(this.mValues[offsetOf] + " @ " + this.mTimes[offsetOf]);
            i++;
            if (i != this.mCount) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public final int offsetOf(int i) {
        if (i < 0 || i >= this.mCount) {
            throw new ArrayIndexOutOfBoundsException("invalid index: " + i + ", mCount= " + this.mCount);
        }
        return (this.mStart + i) % this.mSize;
    }

    public final void expandBuffer() {
        int i = this.mSize * 2;
        long[] jArr = new long[i];
        float[] fArr = new float[i];
        long[] jArr2 = this.mTimes;
        int i2 = this.mStart;
        System.arraycopy(jArr2, i2, jArr, 0, this.mCount - i2);
        long[] jArr3 = this.mTimes;
        int i3 = this.mCount;
        int i4 = this.mStart;
        System.arraycopy(jArr3, 0, jArr, i3 - i4, i4);
        float[] fArr2 = this.mValues;
        int i5 = this.mStart;
        System.arraycopy(fArr2, i5, fArr, 0, this.mCount - i5);
        float[] fArr3 = this.mValues;
        int i6 = this.mCount;
        int i7 = this.mStart;
        System.arraycopy(fArr3, 0, fArr, i6 - i7, i7);
        this.mSize = i;
        this.mStart = 0;
        this.mEnd = this.mCount;
        this.mTimes = jArr;
        this.mValues = fArr;
    }

    public final int getLatestIndexBefore(long j) {
        int i = 1;
        while (true) {
            int i2 = this.mCount;
            if (i >= i2) {
                return i2 - 1;
            }
            if (this.mTimes[offsetOf(i)] > j) {
                return i - 1;
            }
            i++;
        }
    }
}
