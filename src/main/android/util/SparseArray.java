package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Objects;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class SparseArray<E> implements Cloneable {
    private static final Object DELETED = new Object();
    private boolean mGarbage;
    private int[] mKeys;
    private int mSize;
    private Object[] mValues;

    public SparseArray() {
        this(10);
    }

    public SparseArray(int initialCapacity) {
        this.mGarbage = false;
        if (initialCapacity == 0) {
            this.mKeys = EmptyArray.INT;
            this.mValues = EmptyArray.OBJECT;
        } else {
            Object[] newUnpaddedObjectArray = ArrayUtils.newUnpaddedObjectArray(initialCapacity);
            this.mValues = newUnpaddedObjectArray;
            this.mKeys = new int[newUnpaddedObjectArray.length];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public SparseArray<E> m4829clone() {
        SparseArray<E> clone = null;
        try {
            clone = (SparseArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (Object[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public boolean contains(int key) {
        return indexOfKey(key) >= 0;
    }

    public E get(int key) {
        return get(key, null);
    }

    public E get(int key, E valueIfKeyNotFound) {
        E e;
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0 || (e = (E) this.mValues[i]) == DELETED) {
            return valueIfKeyNotFound;
        }
        return e;
    }

    public void delete(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            Object[] objArr = this.mValues;
            Object obj = objArr[i];
            Object obj2 = DELETED;
            if (obj != obj2) {
                objArr[i] = obj2;
                this.mGarbage = true;
            }
        }
    }

    public E removeReturnOld(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            Object[] objArr = this.mValues;
            Object obj = objArr[i];
            Object obj2 = DELETED;
            if (obj != obj2) {
                E old = (E) objArr[i];
                objArr[i] = obj2;
                this.mGarbage = true;
                return old;
            }
            return null;
        }
        return null;
    }

    public void remove(int key) {
        delete(key);
    }

    public void removeAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        Object[] objArr = this.mValues;
        Object obj = objArr[index];
        Object obj2 = DELETED;
        if (obj != obj2) {
            objArr[index] = obj2;
            this.mGarbage = true;
        }
    }

    public void removeAtRange(int index, int size) {
        int end = Math.min(this.mSize, index + size);
        for (int i = index; i < end; i++) {
            removeAt(i);
        }
    }

    /* renamed from: gc */
    private void m87gc() {
        int n = this.mSize;
        int o = 0;
        int[] keys = this.mKeys;
        Object[] values = this.mValues;
        for (int i = 0; i < n; i++) {
            Object val = values[i];
            if (val != DELETED) {
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                    values[i] = null;
                }
                o++;
            }
        }
        this.mGarbage = false;
        this.mSize = o;
    }

    public void set(int key, E value) {
        put(key, value);
    }

    public void put(int key, E value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        int i2 = ~i;
        int i3 = this.mSize;
        if (i2 < i3) {
            Object[] objArr = this.mValues;
            if (objArr[i2] == DELETED) {
                this.mKeys[i2] = key;
                objArr[i2] = value;
                return;
            }
        }
        if (this.mGarbage && i3 >= this.mKeys.length) {
            m87gc();
            i2 = ~ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        }
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, key);
        this.mValues = GrowingArrayUtils.insert((E[]) this.mValues, this.mSize, i2, value);
        this.mSize++;
    }

    public int size() {
        if (this.mGarbage) {
            m87gc();
        }
        return this.mSize;
    }

    public int keyAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m87gc();
        }
        return this.mKeys[index];
    }

    public E valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m87gc();
        }
        return (E) this.mValues[index];
    }

    public void setValueAt(int index, E value) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m87gc();
        }
        this.mValues[index] = value;
    }

    public int indexOfKey(int key) {
        if (this.mGarbage) {
            m87gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(E value) {
        if (this.mGarbage) {
            m87gc();
        }
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfValueByValue(E value) {
        if (this.mGarbage) {
            m87gc();
        }
        for (int i = 0; i < this.mSize; i++) {
            if (value == null) {
                if (this.mValues[i] == null) {
                    return i;
                }
            } else if (value.equals(this.mValues[i])) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        int n = this.mSize;
        Object[] values = this.mValues;
        for (int i = 0; i < n; i++) {
            values[i] = null;
        }
        this.mSize = 0;
        this.mGarbage = false;
    }

    public void append(int key, E value) {
        int i = this.mSize;
        if (i != 0 && key <= this.mKeys[i - 1]) {
            put(key, value);
            return;
        }
        if (this.mGarbage && i >= this.mKeys.length) {
            m87gc();
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, key);
        this.mValues = GrowingArrayUtils.append((E[]) this.mValues, this.mSize, value);
        this.mSize++;
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
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Map)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    public boolean contentEquals(SparseArray<?> other) {
        int size;
        if (other == null || (size = size()) != other.size()) {
            return false;
        }
        for (int index = 0; index < size; index++) {
            if (this.mKeys[index] != other.mKeys[index] || !Objects.equals(this.mValues[index], other.mValues[index])) {
                return false;
            }
        }
        return true;
    }

    public int contentHashCode() {
        int hash = 0;
        int size = size();
        for (int index = 0; index < size; index++) {
            int key = this.mKeys[index];
            hash = (((hash * 31) + key) * 31) + Objects.hashCode(this.mValues[index]);
        }
        return hash;
    }
}
