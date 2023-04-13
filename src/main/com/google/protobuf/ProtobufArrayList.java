package com.google.protobuf;

import java.util.Arrays;
import java.util.RandomAccess;
/* loaded from: classes.dex */
final class ProtobufArrayList<E> extends AbstractProtobufList<E> implements RandomAccess {
    private static final ProtobufArrayList<Object> EMPTY_LIST;
    private E[] array;
    private int size;

    static {
        ProtobufArrayList<Object> protobufArrayList = new ProtobufArrayList<>(new Object[0], 0);
        EMPTY_LIST = protobufArrayList;
        protobufArrayList.makeImmutable();
    }

    public static <E> ProtobufArrayList<E> emptyList() {
        return (ProtobufArrayList<E>) EMPTY_LIST;
    }

    ProtobufArrayList() {
        this(new Object[10], 0);
    }

    private ProtobufArrayList(E[] array, int size) {
        this.array = array;
        this.size = size;
    }

    @Override // com.google.protobuf.Internal.ProtobufList, com.google.protobuf.Internal.BooleanList
    public ProtobufArrayList<E> mutableCopyWithCapacity(int capacity) {
        if (capacity < this.size) {
            throw new IllegalArgumentException();
        }
        return new ProtobufArrayList<>(Arrays.copyOf(this.array, capacity), this.size);
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(E element) {
        ensureIsMutable();
        int i = this.size;
        E[] eArr = this.array;
        if (i == eArr.length) {
            int length = ((i * 3) / 2) + 1;
            E[] newArray = (E[]) Arrays.copyOf(eArr, length);
            this.array = newArray;
        }
        E[] eArr2 = this.array;
        int i2 = this.size;
        this.size = i2 + 1;
        eArr2[i2] = element;
        this.modCount++;
        return true;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        int i;
        ensureIsMutable();
        if (index < 0 || index > (i = this.size)) {
            throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index));
        }
        E[] eArr = this.array;
        if (i < eArr.length) {
            System.arraycopy(eArr, index, eArr, index + 1, i - index);
        } else {
            int length = ((i * 3) / 2) + 1;
            E[] newArray = (E[]) createArray(length);
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
        }
        this.array[index] = element;
        this.size++;
        this.modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        ensureIndexInRange(index);
        return this.array[index];
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public E remove(int index) {
        int i;
        ensureIsMutable();
        ensureIndexInRange(index);
        E[] eArr = this.array;
        E value = eArr[index];
        if (index < this.size - 1) {
            System.arraycopy(eArr, index + 1, eArr, index, (i - index) - 1);
        }
        this.size--;
        this.modCount++;
        return value;
    }

    @Override // com.google.protobuf.AbstractProtobufList, java.util.AbstractList, java.util.List
    public E set(int index, E element) {
        ensureIsMutable();
        ensureIndexInRange(index);
        E[] eArr = this.array;
        E toReturn = eArr[index];
        eArr[index] = element;
        this.modCount++;
        return toReturn;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    private static <E> E[] createArray(int capacity) {
        return (E[]) new Object[capacity];
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
