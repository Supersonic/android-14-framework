package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class SparseBooleanArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private boolean[] mValues;

    public SparseBooleanArray() {
        this(10);
    }

    public SparseBooleanArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = EmptyArray.INT;
            this.mValues = EmptyArray.BOOLEAN;
        } else {
            int[] newUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(initialCapacity);
            this.mKeys = newUnpaddedIntArray;
            this.mValues = new boolean[newUnpaddedIntArray.length];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public SparseBooleanArray m4830clone() {
        SparseBooleanArray clone = null;
        try {
            clone = (SparseBooleanArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (boolean[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public boolean get(int key) {
        return get(key, false);
    }

    public boolean get(int key, boolean valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            int[] iArr = this.mKeys;
            System.arraycopy(iArr, i + 1, iArr, i, this.mSize - (i + 1));
            boolean[] zArr = this.mValues;
            System.arraycopy(zArr, i + 1, zArr, i, this.mSize - (i + 1));
            this.mSize--;
        }
    }

    public void removeAt(int index) {
        int[] iArr = this.mKeys;
        System.arraycopy(iArr, index + 1, iArr, index, this.mSize - (index + 1));
        boolean[] zArr = this.mValues;
        System.arraycopy(zArr, index + 1, zArr, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(int key, boolean value) {
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

    public boolean valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.mValues[index];
    }

    public void setValueAt(int index, boolean value) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.mValues[index] = value;
    }

    public void setKeyAt(int index, int key) {
        if (index >= this.mSize) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.mKeys[index] = key;
    }

    public int indexOfKey(int key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(boolean value) {
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

    public void append(int key, boolean value) {
        int i = this.mSize;
        if (i != 0 && key <= this.mKeys[i - 1]) {
            put(key, value);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, i, key);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, value);
        this.mSize++;
    }

    public int hashCode() {
        int hashCode = this.mSize;
        for (int i = 0; i < this.mSize; i++) {
            hashCode = ((hashCode * 31) + this.mKeys[i]) | (this.mValues[i] ? 1 : 0);
        }
        return hashCode;
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof SparseBooleanArray) {
            SparseBooleanArray other = (SparseBooleanArray) that;
            if (this.mSize != other.mSize) {
                return false;
            }
            for (int i = 0; i < this.mSize; i++) {
                if (this.mKeys[i] != other.mKeys[i] || this.mValues[i] != other.mValues[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
            boolean value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }
}
