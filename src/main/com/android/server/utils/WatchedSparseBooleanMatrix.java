package com.android.server.utils;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class WatchedSparseBooleanMatrix extends WatchableImpl implements Snappable {
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    static final int STEP = 64;
    public boolean[] mInUse;
    public int[] mKeys;
    public int[] mMap;
    public int mOrder;
    public int mSize;
    public int[] mValues;

    public final void onChanged() {
        dispatchChange(this);
    }

    public WatchedSparseBooleanMatrix() {
        this(64);
    }

    public WatchedSparseBooleanMatrix(int i) {
        this.mOrder = i;
        if (i < 64) {
            this.mOrder = 64;
        }
        if (this.mOrder % 64 != 0) {
            this.mOrder = ((i / 64) + 1) * 64;
        }
        int i2 = this.mOrder;
        if (i2 < 64 || i2 % 64 != 0) {
            throw new RuntimeException("mOrder is " + this.mOrder + " initCap is " + i);
        }
        this.mInUse = ArrayUtils.newUnpaddedBooleanArray(i2);
        this.mKeys = ArrayUtils.newUnpaddedIntArray(this.mOrder);
        this.mMap = ArrayUtils.newUnpaddedIntArray(this.mOrder);
        int i3 = this.mOrder;
        this.mValues = ArrayUtils.newUnpaddedIntArray((i3 * i3) / 32);
        this.mSize = 0;
    }

    public WatchedSparseBooleanMatrix(WatchedSparseBooleanMatrix watchedSparseBooleanMatrix) {
        copyFrom(watchedSparseBooleanMatrix);
    }

    public void copyFrom(WatchedSparseBooleanMatrix watchedSparseBooleanMatrix) {
        this.mOrder = watchedSparseBooleanMatrix.mOrder;
        this.mSize = watchedSparseBooleanMatrix.mSize;
        this.mKeys = (int[]) watchedSparseBooleanMatrix.mKeys.clone();
        this.mMap = (int[]) watchedSparseBooleanMatrix.mMap.clone();
        this.mInUse = (boolean[]) watchedSparseBooleanMatrix.mInUse.clone();
        this.mValues = (int[]) watchedSparseBooleanMatrix.mValues.clone();
    }

    @Override // com.android.server.utils.Snappable
    public WatchedSparseBooleanMatrix snapshot() {
        return new WatchedSparseBooleanMatrix(this);
    }

    public void put(int i, int i2, boolean z) {
        int indexOfKey = indexOfKey(i);
        int indexOfKey2 = indexOfKey(i2);
        if (indexOfKey < 0 || indexOfKey2 < 0) {
            if (indexOfKey < 0) {
                indexOfKey(i, true);
            }
            if (indexOfKey2 < 0) {
                indexOfKey(i2, true);
            }
            indexOfKey = indexOfKey(i);
            indexOfKey2 = indexOfKey(i2);
        }
        if (indexOfKey >= 0 && indexOfKey2 >= 0) {
            setValueAt(indexOfKey, indexOfKey2, z);
            return;
        }
        throw new RuntimeException("matrix overflow");
    }

    public void removeAt(int i) {
        validateIndex(i);
        this.mInUse[this.mMap[i]] = false;
        int[] iArr = this.mKeys;
        int i2 = i + 1;
        System.arraycopy(iArr, i2, iArr, i, this.mSize - i2);
        int[] iArr2 = this.mKeys;
        int i3 = this.mSize;
        iArr2[i3 - 1] = 0;
        int[] iArr3 = this.mMap;
        System.arraycopy(iArr3, i2, iArr3, i, i3 - i2);
        int[] iArr4 = this.mMap;
        int i4 = this.mSize;
        iArr4[i4 - 1] = 0;
        this.mSize = i4 - 1;
        onChanged();
    }

    public void removeRange(int i, int i2) {
        if (i2 < i) {
            throw new ArrayIndexOutOfBoundsException("toIndex < fromIndex");
        }
        int i3 = i2 - i;
        if (i3 == 0) {
            return;
        }
        validateIndex(i);
        validateIndex(i2 - 1);
        for (int i4 = i; i4 < i2; i4++) {
            this.mInUse[this.mMap[i4]] = false;
        }
        int[] iArr = this.mKeys;
        System.arraycopy(iArr, i2, iArr, i, this.mSize - i2);
        int[] iArr2 = this.mMap;
        System.arraycopy(iArr2, i2, iArr2, i, this.mSize - i2);
        int i5 = this.mSize - i3;
        while (true) {
            int i6 = this.mSize;
            if (i5 < i6) {
                this.mKeys[i5] = 0;
                this.mMap[i5] = 0;
                i5++;
            } else {
                this.mSize = i6 - i3;
                onChanged();
                return;
            }
        }
    }

    public int size() {
        return this.mSize;
    }

    public void clear() {
        this.mSize = 0;
        Arrays.fill(this.mInUse, false);
        onChanged();
    }

    public int keyAt(int i) {
        validateIndex(i);
        return this.mKeys[i];
    }

    public final boolean valueAtInternal(int i, int i2) {
        int i3 = (i * this.mOrder) + i2;
        return (this.mValues[i3 / 32] & (1 << (i3 % 32))) != 0;
    }

    public boolean valueAt(int i, int i2) {
        validateIndex(i, i2);
        int[] iArr = this.mMap;
        return valueAtInternal(iArr[i], iArr[i2]);
    }

    public final void setValueAtInternal(int i, int i2, boolean z) {
        int i3 = (i * this.mOrder) + i2;
        int i4 = i3 / 32;
        int i5 = 1 << (i3 % 32);
        if (z) {
            int[] iArr = this.mValues;
            iArr[i4] = i5 | iArr[i4];
            return;
        }
        int[] iArr2 = this.mValues;
        iArr2[i4] = (~i5) & iArr2[i4];
    }

    public void setValueAt(int i, int i2, boolean z) {
        validateIndex(i, i2);
        int[] iArr = this.mMap;
        setValueAtInternal(iArr[i], iArr[i2], z);
        onChanged();
    }

    public int indexOfKey(int i) {
        return binarySearch(this.mKeys, this.mSize, i);
    }

    public final int indexOfKey(int i, boolean z) {
        int binarySearch = binarySearch(this.mKeys, this.mSize, i);
        if (binarySearch < 0 && z) {
            binarySearch = ~binarySearch;
            if (this.mSize >= this.mOrder) {
                growMatrix();
            }
            int nextFree = nextFree(true);
            this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, binarySearch, i);
            this.mMap = GrowingArrayUtils.insert(this.mMap, this.mSize, binarySearch, nextFree);
            this.mSize++;
            int i2 = this.mOrder / 32;
            int i3 = nextFree / 32;
            int i4 = ~(1 << (nextFree % 32));
            Arrays.fill(this.mValues, nextFree * i2, (nextFree + 1) * i2, 0);
            for (int i5 = 0; i5 < this.mSize; i5++) {
                int[] iArr = this.mValues;
                int i6 = (i5 * i2) + i3;
                iArr[i6] = iArr[i6] & i4;
            }
        }
        return binarySearch;
    }

    public final void validateIndex(int i) {
        if (i >= this.mSize) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public final void validateIndex(int i, int i2) {
        validateIndex(i);
        validateIndex(i2);
    }

    public final void growMatrix() {
        resizeMatrix(this.mOrder + 64);
    }

    public final void resizeMatrix(int i) {
        if (i % 64 != 0) {
            throw new IllegalArgumentException("matrix order " + i + " is not a multiple of 64");
        }
        int min = Math.min(this.mOrder, i);
        boolean[] newUnpaddedBooleanArray = ArrayUtils.newUnpaddedBooleanArray(i);
        System.arraycopy(this.mInUse, 0, newUnpaddedBooleanArray, 0, min);
        int[] newUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(i);
        System.arraycopy(this.mMap, 0, newUnpaddedIntArray, 0, min);
        int[] newUnpaddedIntArray2 = ArrayUtils.newUnpaddedIntArray(i);
        System.arraycopy(this.mKeys, 0, newUnpaddedIntArray2, 0, min);
        int[] newUnpaddedIntArray3 = ArrayUtils.newUnpaddedIntArray((i * i) / 32);
        for (int i2 = 0; i2 < min; i2++) {
            System.arraycopy(this.mValues, (this.mOrder * i2) / 32, newUnpaddedIntArray3, (i * i2) / 32, min / 32);
        }
        this.mInUse = newUnpaddedBooleanArray;
        this.mMap = newUnpaddedIntArray;
        this.mKeys = newUnpaddedIntArray2;
        this.mValues = newUnpaddedIntArray3;
        this.mOrder = i;
    }

    public final int nextFree(boolean z) {
        int i = 0;
        while (true) {
            boolean[] zArr = this.mInUse;
            if (i < zArr.length) {
                if (!zArr[i]) {
                    zArr[i] = z;
                    return i;
                }
                i++;
            } else {
                throw new RuntimeException();
            }
        }
    }

    public final int lastInuse() {
        for (int i = this.mOrder - 1; i >= 0; i--) {
            if (this.mInUse[i]) {
                for (int i2 = 0; i2 < this.mSize; i2++) {
                    if (this.mMap[i2] == i) {
                        return i2;
                    }
                }
                throw new IndexOutOfBoundsException();
            }
        }
        return -1;
    }

    public final void pack() {
        int i = this.mSize;
        if (i == 0 || i == this.mOrder) {
            return;
        }
        while (true) {
            int nextFree = nextFree(false);
            if (nextFree >= this.mSize) {
                return;
            }
            this.mInUse[nextFree] = true;
            int lastInuse = lastInuse();
            int[] iArr = this.mMap;
            int i2 = iArr[lastInuse];
            this.mInUse[i2] = false;
            iArr[lastInuse] = nextFree;
            int[] iArr2 = this.mValues;
            int i3 = this.mOrder;
            System.arraycopy(iArr2, (i2 * i3) / 32, iArr2, (nextFree * i3) / 32, i3 / 32);
            int i4 = i2 / 32;
            int i5 = 1 << (i2 % 32);
            int i6 = nextFree / 32;
            int i7 = 1 << (nextFree % 32);
            int i8 = 0;
            while (true) {
                int i9 = this.mOrder;
                if (i8 < i9) {
                    int[] iArr3 = this.mValues;
                    if ((iArr3[i4] & i5) == 0) {
                        iArr3[i6] = iArr3[i6] & (~i7);
                    } else {
                        iArr3[i6] = iArr3[i6] | i7;
                    }
                    i4 += i9 / 32;
                    i6 += i9 / 32;
                    i8++;
                }
            }
        }
    }

    public void compact() {
        pack();
        int i = this.mOrder;
        int i2 = (i - this.mSize) / 64;
        if (i2 > 0) {
            resizeMatrix(i - (i2 * 64));
        }
    }

    public int[] keys() {
        return Arrays.copyOf(this.mKeys, this.mSize);
    }

    public void setCapacity(int i) {
        if (i <= this.mOrder) {
            return;
        }
        if (i % 64 != 0) {
            i = ((i / 64) + 1) * 64;
        }
        resizeMatrix(i);
    }

    public int hashCode() {
        int hashCode = (((this.mSize * 31) + Arrays.hashCode(this.mKeys)) * 31) + Arrays.hashCode(this.mMap);
        for (int i = 0; i < this.mSize; i++) {
            int i2 = this.mMap[i];
            for (int i3 = 0; i3 < this.mSize; i3++) {
                hashCode = (hashCode * 31) + (valueAtInternal(i2, this.mMap[i3]) ? 1 : 0);
            }
        }
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WatchedSparseBooleanMatrix) {
            WatchedSparseBooleanMatrix watchedSparseBooleanMatrix = (WatchedSparseBooleanMatrix) obj;
            if (this.mSize == watchedSparseBooleanMatrix.mSize && Arrays.equals(this.mKeys, watchedSparseBooleanMatrix.mKeys)) {
                for (int i = 0; i < this.mSize; i++) {
                    int i2 = this.mMap[i];
                    for (int i3 = 0; i3 < this.mSize; i3++) {
                        int i4 = this.mMap[i3];
                        if (valueAtInternal(i2, i4) != watchedSparseBooleanMatrix.valueAtInternal(i2, i4)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public String[] matrixToStringMeta() {
        String[] strArr = new String[3];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSize; i++) {
            sb.append(this.mKeys[i]);
            if (i < this.mSize - 1) {
                sb.append(" ");
            }
        }
        strArr[0] = sb.substring(0);
        StringBuilder sb2 = new StringBuilder();
        for (int i2 = 0; i2 < this.mSize; i2++) {
            sb2.append(this.mMap[i2]);
            if (i2 < this.mSize - 1) {
                sb2.append(" ");
            }
        }
        strArr[1] = sb2.substring(0);
        StringBuilder sb3 = new StringBuilder();
        for (int i3 = 0; i3 < this.mOrder; i3++) {
            sb3.append(this.mInUse[i3] ? "1" : "0");
        }
        strArr[2] = sb3.substring(0);
        return strArr;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public String[] matrixToStringRaw() {
        String[] strArr = new String[this.mOrder];
        int i = 0;
        while (true) {
            int i2 = this.mOrder;
            if (i >= i2) {
                return strArr;
            }
            StringBuilder sb = new StringBuilder(i2);
            for (int i3 = 0; i3 < this.mOrder; i3++) {
                sb.append(valueAtInternal(i, i3) ? "1" : "0");
            }
            strArr[i] = sb.substring(0);
            i++;
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public String[] matrixToStringCooked() {
        String[] strArr = new String[this.mSize];
        int i = 0;
        while (true) {
            int i2 = this.mSize;
            if (i >= i2) {
                return strArr;
            }
            int i3 = this.mMap[i];
            StringBuilder sb = new StringBuilder(i2);
            for (int i4 = 0; i4 < this.mSize; i4++) {
                sb.append(valueAtInternal(i3, this.mMap[i4]) ? "1" : "0");
            }
            strArr[i] = sb.substring(0);
            i++;
        }
    }

    public String toString() {
        return "{" + this.mSize + "x" + this.mSize + "}";
    }

    public static int binarySearch(int[] iArr, int i, int i2) {
        int i3 = i - 1;
        int i4 = 0;
        while (i4 <= i3) {
            int i5 = (i4 + i3) >>> 1;
            int i6 = iArr[i5];
            if (i6 < i2) {
                i4 = i5 + 1;
            } else if (i6 <= i2) {
                return i5;
            } else {
                i3 = i5 - 1;
            }
        }
        return ~i4;
    }
}
