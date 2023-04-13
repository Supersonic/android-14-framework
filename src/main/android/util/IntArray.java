package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class IntArray implements Cloneable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private int mSize;
    private int[] mValues;

    private IntArray(int[] array, int size) {
        this.mValues = array;
        this.mSize = Preconditions.checkArgumentInRange(size, 0, array.length, "size");
    }

    public IntArray() {
        this(10);
    }

    public IntArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mValues = EmptyArray.INT;
        } else {
            this.mValues = ArrayUtils.newUnpaddedIntArray(initialCapacity);
        }
        this.mSize = 0;
    }

    public static IntArray wrap(int[] array) {
        return new IntArray(array, array.length);
    }

    public static IntArray fromArray(int[] array, int size) {
        return wrap(Arrays.copyOf(array, size));
    }

    public void resize(int newSize) {
        Preconditions.checkArgumentNonnegative(newSize);
        int[] iArr = this.mValues;
        if (newSize <= iArr.length) {
            Arrays.fill(iArr, newSize, iArr.length, 0);
        } else {
            ensureCapacity(newSize - this.mSize);
        }
        this.mSize = newSize;
    }

    public void add(int value) {
        add(this.mSize, value);
    }

    public void add(int index, int value) {
        ensureCapacity(1);
        int i = this.mSize;
        int rightSegment = i - index;
        int i2 = i + 1;
        this.mSize = i2;
        ArrayUtils.checkBounds(i2, index);
        if (rightSegment != 0) {
            int[] iArr = this.mValues;
            System.arraycopy(iArr, index, iArr, index + 1, rightSegment);
        }
        this.mValues[index] = value;
    }

    public int binarySearch(int value) {
        return ContainerHelpers.binarySearch(this.mValues, this.mSize, value);
    }

    public void addAll(IntArray values) {
        int count = values.mSize;
        ensureCapacity(count);
        System.arraycopy(values.mValues, 0, this.mValues, this.mSize, count);
        this.mSize += count;
    }

    public void addAll(int[] values) {
        int count = values.length;
        ensureCapacity(count);
        System.arraycopy(values, 0, this.mValues, this.mSize, count);
        this.mSize += count;
    }

    private void ensureCapacity(int count) {
        int currentSize = this.mSize;
        int minCapacity = currentSize + count;
        if (minCapacity >= this.mValues.length) {
            int targetCap = (currentSize < 6 ? 12 : currentSize >> 1) + currentSize;
            int newCapacity = targetCap > minCapacity ? targetCap : minCapacity;
            int[] newValues = ArrayUtils.newUnpaddedIntArray(newCapacity);
            System.arraycopy(this.mValues, 0, newValues, 0, currentSize);
            this.mValues = newValues;
        }
    }

    public void clear() {
        this.mSize = 0;
    }

    /* renamed from: clone */
    public IntArray m4803clone() {
        return new IntArray((int[]) this.mValues.clone(), this.mSize);
    }

    public int get(int index) {
        ArrayUtils.checkBounds(this.mSize, index);
        return this.mValues[index];
    }

    public void set(int index, int value) {
        ArrayUtils.checkBounds(this.mSize, index);
        this.mValues[index] = value;
    }

    public int indexOf(int value) {
        int n = this.mSize;
        for (int i = 0; i < n; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void remove(int index) {
        ArrayUtils.checkBounds(this.mSize, index);
        int[] iArr = this.mValues;
        System.arraycopy(iArr, index + 1, iArr, index, (this.mSize - index) - 1);
        this.mSize--;
    }

    public int size() {
        return this.mSize;
    }

    public int[] toArray() {
        return Arrays.copyOf(this.mValues, this.mSize);
    }

    public String toString() {
        int iMax = this.mSize - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        int i = 0;
        while (true) {
            b.append(this.mValues[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
            i++;
        }
    }
}
