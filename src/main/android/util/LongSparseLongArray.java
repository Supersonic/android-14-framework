package android.util;

import android.p008os.Parcel;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.Preconditions;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class LongSparseLongArray implements Cloneable {
    private long[] mKeys;
    private int mSize;
    private long[] mValues;

    public LongSparseLongArray() {
        this(10);
    }

    public LongSparseLongArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = EmptyArray.LONG;
            this.mValues = EmptyArray.LONG;
        } else {
            long[] newUnpaddedLongArray = ArrayUtils.newUnpaddedLongArray(initialCapacity);
            this.mKeys = newUnpaddedLongArray;
            this.mValues = new long[newUnpaddedLongArray.length];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public LongSparseLongArray m4821clone() {
        LongSparseLongArray clone = null;
        try {
            clone = (LongSparseLongArray) super.clone();
            clone.mKeys = (long[]) this.mKeys.clone();
            clone.mValues = (long[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public long get(long key) {
        return get(key, 0L);
    }

    public long get(long key, long valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(long key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        long[] jArr = this.mKeys;
        System.arraycopy(jArr, index + 1, jArr, index, this.mSize - (index + 1));
        long[] jArr2 = this.mValues;
        System.arraycopy(jArr2, index + 1, jArr2, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(long key, long value) {
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

    public long keyAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.mKeys[index];
    }

    public long valueAt(int index) {
        if (index >= this.mSize && UtilConfig.sThrowExceptionForUpperArrayOutOfBounds) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.mValues[index];
    }

    public int indexOfKey(long key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(long value) {
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

    public void append(long key, long value) {
        int i = this.mSize;
        if (i != 0 && key <= this.mKeys[i - 1]) {
            put(key, value);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, i, key);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, value);
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
            long value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }

    /* loaded from: classes3.dex */
    public static class Parcelling implements com.android.internal.util.Parcelling<LongSparseLongArray> {
        @Override // com.android.internal.util.Parcelling
        public void parcel(LongSparseLongArray array, Parcel dest, int parcelFlags) {
            if (array == null) {
                dest.writeInt(-1);
                return;
            }
            dest.writeInt(array.mSize);
            dest.writeLongArray(array.mKeys);
            dest.writeLongArray(array.mValues);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.util.Parcelling
        public LongSparseLongArray unparcel(Parcel source) {
            int size = source.readInt();
            if (size == -1) {
                return null;
            }
            LongSparseLongArray array = new LongSparseLongArray(0);
            array.mSize = size;
            array.mKeys = source.createLongArray();
            array.mValues = source.createLongArray();
            Preconditions.checkArgument(array.mKeys.length >= size);
            Preconditions.checkArgument(array.mValues.length >= size);
            if (size > 0) {
                long last = array.mKeys[0];
                for (int i = 1; i < size; i++) {
                    Preconditions.checkArgument(last < array.mKeys[i]);
                }
            }
            return array;
        }
    }
}
