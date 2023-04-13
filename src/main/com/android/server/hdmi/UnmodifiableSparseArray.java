package com.android.server.hdmi;

import android.util.SparseArray;
/* loaded from: classes.dex */
public final class UnmodifiableSparseArray<E> {
    public final SparseArray<E> mArray;

    public UnmodifiableSparseArray(SparseArray<E> sparseArray) {
        this.mArray = sparseArray;
    }

    public E get(int i) {
        return this.mArray.get(i);
    }

    public E get(int i, E e) {
        return this.mArray.get(i, e);
    }

    public String toString() {
        return this.mArray.toString();
    }
}
