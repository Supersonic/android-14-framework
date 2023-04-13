package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Arrays;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class SparseIntArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private int[] mValues;

    public SparseIntArray() {
        this(10);
    }

    public SparseIntArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = EmptyArray.INT;
            this.mValues = EmptyArray.INT;
        } else {
            int[] newUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(initialCapacity);
            this.mKeys = newUnpaddedIntArray;
            this.mValues = new int[newUnpaddedIntArray.length];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public SparseIntArray m4832clone() {
        SparseIntArray clone = null;
        try {
            clone = (SparseIntArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (int[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public int get(int key) {
        return get(key, 0);
    }

    public int get(int key, int valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        int[] iArr = this.mKeys;
        System.arraycopy(iArr, index + 1, iArr, index, this.mSize - (index + 1));
        int[] iArr2 = this.mValues;
        System.arraycopy(iArr2, index + 1, iArr2, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(int key, int value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        int i2 = ~i;
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, key);
        this.mValues = GrowingArrayUtils.insert(this.mValues, this.mSize, i2, value);
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.mKeys[index];
    }

    public int valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.mValues[index];
    }

    public void setValueAt(int index, int value) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.mValues[index] = value;
    }

    public int indexOfKey(int key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(int value) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int key, int value) {
        int i = this.mSize;
        if (i != 0 && key <= this.mKeys[i - 1]) {
            put(key, value);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, i, key);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, value);
        this.mSize++;
    }

    public int[] copyKeys() {
        if (size() == 0) {
            return null;
        }
        return Arrays.copyOf(this.mKeys, size());
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 28);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            int key = keyAt(i);
            buffer.append(key);
            buffer.append('=');
            int value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }
}
