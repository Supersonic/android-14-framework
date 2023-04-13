package com.android.internal.util;
/* loaded from: classes3.dex */
public class RingBufferIndices {
    private final int mCapacity;
    private int mSize;
    private int mStart;

    public RingBufferIndices(int capacity) {
        this.mCapacity = capacity;
    }

    public int add() {
        int i = this.mSize;
        int i2 = this.mCapacity;
        if (i < i2) {
            int pos = this.mSize;
            this.mSize = i + 1;
            return pos;
        }
        int pos2 = this.mStart;
        int i3 = this.mStart + 1;
        this.mStart = i3;
        if (i3 == i2) {
            this.mStart = 0;
        }
        return pos2;
    }

    public void clear() {
        this.mStart = 0;
        this.mSize = 0;
    }

    public int size() {
        return this.mSize;
    }

    public int indexOf(int pos) {
        int index = this.mStart + pos;
        int i = this.mCapacity;
        if (index >= i) {
            return index - i;
        }
        return index;
    }
}
