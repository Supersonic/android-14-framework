package com.android.internal.util;

import java.lang.reflect.Array;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class RingBuffer<T> {
    private final T[] mBuffer;
    private long mCursor = 0;

    public RingBuffer(Class<T> c, int capacity) {
        Preconditions.checkArgumentPositive(capacity, "A RingBuffer cannot have 0 capacity");
        this.mBuffer = (T[]) ((Object[]) Array.newInstance((Class<?>) c, capacity));
    }

    public int size() {
        return (int) Math.min(this.mBuffer.length, this.mCursor);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        for (int i = 0; i < size(); i++) {
            this.mBuffer[i] = null;
        }
        this.mCursor = 0L;
    }

    public void append(T t) {
        T[] tArr = this.mBuffer;
        long j = this.mCursor;
        this.mCursor = 1 + j;
        tArr[indexOf(j)] = t;
    }

    public T getNextSlot() {
        long j = this.mCursor;
        this.mCursor = 1 + j;
        int nextSlotIdx = indexOf(j);
        T[] tArr = this.mBuffer;
        if (tArr[nextSlotIdx] == null) {
            tArr[nextSlotIdx] = createNewItem();
        }
        return this.mBuffer[nextSlotIdx];
    }

    protected T createNewItem() {
        try {
            return (T) this.mBuffer.getClass().getComponentType().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    public T[] toArray() {
        T[] out = (T[]) Arrays.copyOf(this.mBuffer, size(), this.mBuffer.getClass());
        long inCursor = this.mCursor - 1;
        int outIdx = out.length - 1;
        while (outIdx >= 0) {
            out[outIdx] = this.mBuffer[indexOf(inCursor)];
            outIdx--;
            inCursor--;
        }
        return out;
    }

    private int indexOf(long cursor) {
        return (int) Math.abs(cursor % this.mBuffer.length);
    }
}
