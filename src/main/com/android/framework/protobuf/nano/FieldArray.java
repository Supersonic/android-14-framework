package com.android.framework.protobuf.nano;
/* loaded from: classes4.dex */
public final class FieldArray implements Cloneable {
    private static final FieldData DELETED = new FieldData();
    private FieldData[] mData;
    private int[] mFieldNumbers;
    private boolean mGarbage;
    private int mSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldArray() {
        this(10);
    }

    FieldArray(int initialCapacity) {
        this.mGarbage = false;
        int initialCapacity2 = idealIntArraySize(initialCapacity);
        this.mFieldNumbers = new int[initialCapacity2];
        this.mData = new FieldData[initialCapacity2];
        this.mSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldData get(int fieldNumber) {
        FieldData fieldData;
        int i = binarySearch(fieldNumber);
        if (i < 0 || (fieldData = this.mData[i]) == DELETED) {
            return null;
        }
        return fieldData;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove(int fieldNumber) {
        int i = binarySearch(fieldNumber);
        if (i >= 0) {
            FieldData[] fieldDataArr = this.mData;
            FieldData fieldData = fieldDataArr[i];
            FieldData fieldData2 = DELETED;
            if (fieldData != fieldData2) {
                fieldDataArr[i] = fieldData2;
                this.mGarbage = true;
            }
        }
    }

    /* renamed from: gc */
    private void m72gc() {
        int n = this.mSize;
        int o = 0;
        int[] keys = this.mFieldNumbers;
        FieldData[] values = this.mData;
        for (int i = 0; i < n; i++) {
            FieldData val = values[i];
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(int fieldNumber, FieldData data) {
        int i = binarySearch(fieldNumber);
        if (i >= 0) {
            this.mData[i] = data;
            return;
        }
        int i2 = ~i;
        int i3 = this.mSize;
        if (i2 < i3) {
            FieldData[] fieldDataArr = this.mData;
            if (fieldDataArr[i2] == DELETED) {
                this.mFieldNumbers[i2] = fieldNumber;
                fieldDataArr[i2] = data;
                return;
            }
        }
        if (this.mGarbage && i3 >= this.mFieldNumbers.length) {
            m72gc();
            i2 = ~binarySearch(fieldNumber);
        }
        int i4 = this.mSize;
        if (i4 >= this.mFieldNumbers.length) {
            int n = idealIntArraySize(i4 + 1);
            int[] nkeys = new int[n];
            FieldData[] nvalues = new FieldData[n];
            int[] iArr = this.mFieldNumbers;
            System.arraycopy(iArr, 0, nkeys, 0, iArr.length);
            FieldData[] fieldDataArr2 = this.mData;
            System.arraycopy(fieldDataArr2, 0, nvalues, 0, fieldDataArr2.length);
            this.mFieldNumbers = nkeys;
            this.mData = nvalues;
        }
        int n2 = this.mSize;
        if (n2 - i2 != 0) {
            int[] iArr2 = this.mFieldNumbers;
            System.arraycopy(iArr2, i2, iArr2, i2 + 1, n2 - i2);
            FieldData[] fieldDataArr3 = this.mData;
            System.arraycopy(fieldDataArr3, i2, fieldDataArr3, i2 + 1, this.mSize - i2);
        }
        this.mFieldNumbers[i2] = fieldNumber;
        this.mData[i2] = data;
        this.mSize++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int size() {
        if (this.mGarbage) {
            m72gc();
        }
        return this.mSize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldData dataAt(int index) {
        if (this.mGarbage) {
            m72gc();
        }
        return this.mData[index];
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof FieldArray) {
            FieldArray other = (FieldArray) o;
            if (size() != other.size()) {
                return false;
            }
            return arrayEquals(this.mFieldNumbers, other.mFieldNumbers, this.mSize) && arrayEquals(this.mData, other.mData, this.mSize);
        }
        return false;
    }

    public int hashCode() {
        if (this.mGarbage) {
            m72gc();
        }
        int result = 17;
        for (int i = 0; i < this.mSize; i++) {
            result = (((result * 31) + this.mFieldNumbers[i]) * 31) + this.mData[i].hashCode();
        }
        return result;
    }

    private int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    private int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return need;
    }

    private int binarySearch(int value) {
        int lo = 0;
        int hi = this.mSize - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midVal = this.mFieldNumbers[mid];
            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return ~lo;
    }

    private boolean arrayEquals(int[] a, int[] b, int size) {
        for (int i = 0; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean arrayEquals(FieldData[] a, FieldData[] b, int size) {
        for (int i = 0; i < size; i++) {
            if (!a[i].equals(b[i])) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: clone */
    public final FieldArray m6507clone() {
        int size = size();
        FieldArray clone = new FieldArray(size);
        System.arraycopy(this.mFieldNumbers, 0, clone.mFieldNumbers, 0, size);
        for (int i = 0; i < size; i++) {
            FieldData fieldData = this.mData[i];
            if (fieldData != null) {
                clone.mData[i] = fieldData.m6508clone();
            }
        }
        clone.mSize = size;
        return clone;
    }
}
