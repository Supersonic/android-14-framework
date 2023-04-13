package com.android.server.permission.access.collection;

import android.util.SparseBooleanArray;
/* compiled from: IntSet.kt */
/* loaded from: classes2.dex */
public final class IntSet {
    public final SparseBooleanArray array;

    public IntSet(SparseBooleanArray sparseBooleanArray) {
        this.array = sparseBooleanArray;
    }

    public IntSet() {
        this(new SparseBooleanArray());
    }

    public final int getSize() {
        return this.array.size();
    }

    public final int elementAt(int i) {
        return this.array.keyAt(i);
    }

    public final void add(int i) {
        this.array.put(i, true);
    }

    public final void remove(int i) {
        this.array.delete(i);
    }

    public final void clear() {
        this.array.clear();
    }

    public final IntSet copy() {
        return new IntSet(this.array.clone());
    }
}
