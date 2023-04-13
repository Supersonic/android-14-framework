package com.android.internal.util.jobs;
/* loaded from: classes.dex */
public class RingBufferIndices {
    public final int mCapacity;
    public int mSize;
    public int mStart;

    public RingBufferIndices(int i) {
        this.mCapacity = i;
    }

    public int add() {
        int i = this.mSize;
        int i2 = this.mCapacity;
        if (i < i2) {
            this.mSize = i + 1;
            return i;
        }
        int i3 = this.mStart;
        int i4 = i3 + 1;
        this.mStart = i4;
        if (i4 == i2) {
            this.mStart = 0;
        }
        return i3;
    }

    public void clear() {
        this.mStart = 0;
        this.mSize = 0;
    }

    public int size() {
        return this.mSize;
    }

    public int indexOf(int i) {
        int i2 = this.mStart + i;
        int i3 = this.mCapacity;
        return i2 >= i3 ? i2 - i3 : i2;
    }
}
