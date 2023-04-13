package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.NoSuchElementException;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class LongArrayQueue {
    private int mHead;
    private int mSize;
    private int mTail;
    private long[] mValues;

    public LongArrayQueue(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mValues = EmptyArray.LONG;
        } else {
            this.mValues = ArrayUtils.newUnpaddedLongArray(initialCapacity);
        }
        this.mSize = 0;
        this.mTail = 0;
        this.mHead = 0;
    }

    public LongArrayQueue() {
        this(16);
    }

    private void grow() {
        int i = this.mSize;
        if (i < this.mValues.length) {
            throw new IllegalStateException("Queue not full yet!");
        }
        int newSize = GrowingArrayUtils.growSize(i);
        long[] newArray = ArrayUtils.newUnpaddedLongArray(newSize);
        long[] jArr = this.mValues;
        int length = jArr.length;
        int i2 = this.mHead;
        int r = length - i2;
        System.arraycopy(jArr, i2, newArray, 0, r);
        System.arraycopy(this.mValues, 0, newArray, r, this.mHead);
        this.mValues = newArray;
        this.mHead = 0;
        this.mTail = this.mSize;
    }

    public int size() {
        return this.mSize;
    }

    public void clear() {
        this.mSize = 0;
        this.mTail = 0;
        this.mHead = 0;
    }

    public void addLast(long value) {
        if (this.mSize == this.mValues.length) {
            grow();
        }
        long[] jArr = this.mValues;
        int i = this.mTail;
        jArr[i] = value;
        this.mTail = (i + 1) % jArr.length;
        this.mSize++;
    }

    public long removeFirst() {
        int i = this.mSize;
        if (i == 0) {
            throw new NoSuchElementException("Queue is empty!");
        }
        long[] jArr = this.mValues;
        int i2 = this.mHead;
        long ret = jArr[i2];
        this.mHead = (i2 + 1) % jArr.length;
        this.mSize = i - 1;
        return ret;
    }

    public long get(int position) {
        if (position < 0 || position >= this.mSize) {
            throw new IndexOutOfBoundsException("Index " + position + " not valid for a queue of size " + this.mSize);
        }
        long[] jArr = this.mValues;
        int index = (this.mHead + position) % jArr.length;
        return jArr[index];
    }

    public long peekFirst() {
        if (this.mSize == 0) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return this.mValues[this.mHead];
    }

    public long peekLast() {
        if (this.mSize == 0) {
            throw new NoSuchElementException("Queue is empty!");
        }
        int i = this.mTail;
        if (i == 0) {
            i = this.mValues.length;
        }
        int index = i - 1;
        return this.mValues[index];
    }

    public String toString() {
        int i = this.mSize;
        if (i <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(i * 64);
        buffer.append('{');
        buffer.append(get(0));
        for (int i2 = 1; i2 < this.mSize; i2++) {
            buffer.append(", ");
            buffer.append(get(i2));
        }
        buffer.append('}');
        return buffer.toString();
    }
}
