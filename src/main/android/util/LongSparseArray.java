package android.util;

import android.p008os.Parcel;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.Parcelling;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.LongObjPredicate;
import java.util.Arrays;
import java.util.Objects;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class LongSparseArray<E> implements Cloneable {
    private static final Object DELETED = new Object();
    private boolean mGarbage;
    private long[] mKeys;
    private int mSize;
    private Object[] mValues;

    public LongSparseArray() {
        this(10);
    }

    public LongSparseArray(int initialCapacity) {
        this.mGarbage = false;
        if (initialCapacity == 0) {
            this.mKeys = EmptyArray.LONG;
            this.mValues = EmptyArray.OBJECT;
        } else {
            this.mKeys = ArrayUtils.newUnpaddedLongArray(initialCapacity);
            this.mValues = ArrayUtils.newUnpaddedObjectArray(initialCapacity);
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public LongSparseArray<E> m4814clone() {
        LongSparseArray<E> clone = null;
        try {
            clone = (LongSparseArray) super.clone();
            clone.mKeys = (long[]) this.mKeys.clone();
            clone.mValues = (Object[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public E get(long key) {
        return get(key, null);
    }

    public E get(long key, E valueIfKeyNotFound) {
        E e;
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0 || (e = (E) this.mValues[i]) == DELETED) {
            return valueIfKeyNotFound;
        }
        return e;
    }

    public void delete(long key) {
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

    public void remove(long key) {
        delete(key);
    }

    public void removeIf(LongObjPredicate<? super E> filter) {
        Objects.requireNonNull(filter);
        for (int i = 0; i < this.mSize; i++) {
            Object obj = this.mValues[i];
            Object obj2 = DELETED;
            if (obj != obj2 && filter.test(this.mKeys[i], obj)) {
                this.mValues[i] = obj2;
                this.mGarbage = true;
            }
        }
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

    /* renamed from: gc */
    private void m101gc() {
        int n = this.mSize;
        int o = 0;
        long[] keys = this.mKeys;
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

    public void put(long key, E value) {
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
            m101gc();
            i2 = ~ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        }
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, key);
        this.mValues = GrowingArrayUtils.insert((E[]) this.mValues, this.mSize, i2, value);
        this.mSize++;
    }

    public int size() {
        if (this.mGarbage) {
            m101gc();
        }
        return this.mSize;
    }

    public long keyAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m101gc();
        }
        return this.mKeys[index];
    }

    public E valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m101gc();
        }
        return (E) this.mValues[index];
    }

    public void setValueAt(int index, E value) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (this.mGarbage) {
            m101gc();
        }
        this.mValues[index] = value;
    }

    public int indexOfKey(long key) {
        if (this.mGarbage) {
            m101gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(E value) {
        if (this.mGarbage) {
            m101gc();
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
            m101gc();
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

    public void append(long key, E value) {
        int i = this.mSize;
        if (i != 0 && key <= this.mKeys[i - 1]) {
            put(key, value);
            return;
        }
        if (this.mGarbage && i >= this.mKeys.length) {
            m101gc();
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
            long key = keyAt(i);
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

    /* loaded from: classes3.dex */
    public static class StringParcelling implements Parcelling<LongSparseArray<String>> {
        @Override // com.android.internal.util.Parcelling
        public void parcel(LongSparseArray<String> array, Parcel dest, int parcelFlags) {
            if (array == null) {
                dest.writeInt(-1);
                return;
            }
            int size = ((LongSparseArray) array).mSize;
            dest.writeInt(size);
            dest.writeLongArray(((LongSparseArray) array).mKeys);
            dest.writeStringArray((String[]) Arrays.copyOfRange(((LongSparseArray) array).mValues, 0, size, String[].class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.util.Parcelling
        public LongSparseArray<String> unparcel(Parcel source) {
            int size = source.readInt();
            if (size == -1) {
                return null;
            }
            LongSparseArray<String> array = new LongSparseArray<>(0);
            ((LongSparseArray) array).mSize = size;
            ((LongSparseArray) array).mKeys = source.createLongArray();
            ((LongSparseArray) array).mValues = source.createStringArray();
            Preconditions.checkArgument(((LongSparseArray) array).mKeys.length >= size);
            Preconditions.checkArgument(((LongSparseArray) array).mValues.length >= size);
            if (size > 0) {
                long last = ((LongSparseArray) array).mKeys[0];
                for (int i = 1; i < size; i++) {
                    Preconditions.checkArgument(last < ((LongSparseArray) array).mKeys[i]);
                }
            }
            return array;
        }
    }
}
