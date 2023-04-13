package com.google.protobuf;

import com.google.protobuf.Internal;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class BooleanArrayList extends AbstractProtobufList<Boolean> implements Internal.BooleanList, RandomAccess, PrimitiveNonBoxingCollection {
    private static final BooleanArrayList EMPTY_LIST;
    private boolean[] array;
    private int size;

    static {
        BooleanArrayList booleanArrayList = new BooleanArrayList(new boolean[0], 0);
        EMPTY_LIST = booleanArrayList;
        booleanArrayList.makeImmutable();
    }

    public static BooleanArrayList emptyList() {
        return EMPTY_LIST;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BooleanArrayList() {
        this(new boolean[10], 0);
    }

    private BooleanArrayList(boolean[] other, int size) {
        this.array = other;
        this.size = size;
    }

    @Override // java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        ensureIsMutable();
        if (toIndex < fromIndex) {
            throw new IndexOutOfBoundsException("toIndex < fromIndex");
        }
        boolean[] zArr = this.array;
        System.arraycopy(zArr, toIndex, zArr, fromIndex, this.size - toIndex);
        this.size -= toIndex - fromIndex;
        this.modCount++;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.Collection, java.util.List
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanArrayList)) {
            return super.equals(o);
        }
        BooleanArrayList other = (BooleanArrayList) o;
        if (this.size != other.size) {
            return false;
        }
        boolean[] arr = other.array;
        for (int i = 0; i < this.size; i++) {
            if (this.array[i] != arr[i]) {
                return false;
            }
        }
        return true;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.size; i++) {
            result = (result * 31) + Internal.hashBoolean(this.array[i]);
        }
        return result;
    }

    @Override // com.google.protobuf.Internal.ProtobufList, com.google.protobuf.Internal.BooleanList
    /* renamed from: mutableCopyWithCapacity */
    public Internal.ProtobufList<Boolean> mutableCopyWithCapacity2(int capacity) {
        if (capacity < this.size) {
            throw new IllegalArgumentException();
        }
        return new BooleanArrayList(Arrays.copyOf(this.array, capacity), this.size);
    }

    @Override // java.util.AbstractList, java.util.List
    public Boolean get(int index) {
        return Boolean.valueOf(getBoolean(index));
    }

    @Override // com.google.protobuf.Internal.BooleanList
    public boolean getBoolean(int index) {
        ensureIndexInRange(index);
        return this.array[index];
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object element) {
        if (element instanceof Boolean) {
            boolean unboxedElement = ((Boolean) element).booleanValue();
            int numElems = size();
            for (int i = 0; i < numElems; i++) {
                if (this.array[i] == unboxedElement) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object element) {
        return indexOf(element) != -1;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public Boolean set(int index, Boolean element) {
        return Boolean.valueOf(setBoolean(index, element.booleanValue()));
    }

    @Override // com.google.protobuf.Internal.BooleanList
    public boolean setBoolean(int index, boolean element) {
        ensureIsMutable();
        ensureIndexInRange(index);
        boolean[] zArr = this.array;
        boolean previousValue = zArr[index];
        zArr[index] = element;
        return previousValue;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(Boolean element) {
        addBoolean(element.booleanValue());
        return true;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public void add(int index, Boolean element) {
        addBoolean(index, element.booleanValue());
    }

    @Override // com.google.protobuf.Internal.BooleanList
    public void addBoolean(boolean element) {
        ensureIsMutable();
        int i = this.size;
        boolean[] zArr = this.array;
        if (i == zArr.length) {
            int length = ((i * 3) / 2) + 1;
            boolean[] newArray = new boolean[length];
            System.arraycopy(zArr, 0, newArray, 0, i);
            this.array = newArray;
        }
        boolean[] zArr2 = this.array;
        int i2 = this.size;
        this.size = i2 + 1;
        zArr2[i2] = element;
    }

    private void addBoolean(int index, boolean element) {
        int i;
        ensureIsMutable();
        if (index < 0 || index > (i = this.size)) {
            throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
        }
        boolean[] zArr = this.array;
        if (i < zArr.length) {
            System.arraycopy(zArr, index, zArr, index + 1, i - index);
        } else {
            int length = ((i * 3) / 2) + 1;
            boolean[] newArray = new boolean[length];
            System.arraycopy(zArr, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
        }
        this.array[index] = element;
        this.size++;
        this.modCount++;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends Boolean> collection) {
        ensureIsMutable();
        Internal.checkNotNull(collection);
        if (!(collection instanceof BooleanArrayList)) {
            return super.addAll(collection);
        }
        BooleanArrayList list = (BooleanArrayList) collection;
        int i = list.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        int overflow = Reader.READ_DONE - i2;
        if (overflow < i) {
            throw new OutOfMemoryError();
        }
        int newSize = i2 + i;
        boolean[] zArr = this.array;
        if (newSize > zArr.length) {
            this.array = Arrays.copyOf(zArr, newSize);
        }
        System.arraycopy(list.array, 0, this.array, this.size, list.size);
        this.size = newSize;
        this.modCount++;
        return true;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public Boolean remove(int index) {
        int i;
        ensureIsMutable();
        ensureIndexInRange(index);
        boolean[] zArr = this.array;
        boolean value = zArr[index];
        if (index < this.size - 1) {
            System.arraycopy(zArr, index + 1, zArr, index, (i - index) - 1);
        }
        this.size--;
        this.modCount++;
        return Boolean.valueOf(value);
    }

    private void ensureIndexInRange(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
        }
    }

    private String makeOutOfBoundsExceptionMessage(int index) {
        return "Index:" + index + ", Size:" + this.size;
    }
}
