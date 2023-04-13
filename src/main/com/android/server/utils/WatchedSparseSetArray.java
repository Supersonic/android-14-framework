package com.android.server.utils;

import android.util.ArraySet;
import android.util.SparseSetArray;
/* loaded from: classes2.dex */
public class WatchedSparseSetArray<T> extends WatchableImpl implements Snappable {
    public final SparseSetArray mStorage;

    public final void onChanged() {
        dispatchChange(this);
    }

    public WatchedSparseSetArray() {
        this.mStorage = new SparseSetArray();
    }

    public WatchedSparseSetArray(WatchedSparseSetArray<T> watchedSparseSetArray) {
        this.mStorage = new SparseSetArray(watchedSparseSetArray.untrackedStorage());
    }

    public SparseSetArray<T> untrackedStorage() {
        return this.mStorage;
    }

    public boolean add(int i, T t) {
        boolean add = this.mStorage.add(i, t);
        onChanged();
        return add;
    }

    public void addAll(int i, ArraySet<T> arraySet) {
        this.mStorage.addAll(i, arraySet);
        onChanged();
    }

    public void clear() {
        this.mStorage.clear();
        onChanged();
    }

    public boolean contains(int i, T t) {
        return this.mStorage.contains(i, t);
    }

    public ArraySet<T> get(int i) {
        return this.mStorage.get(i);
    }

    public boolean remove(int i, T t) {
        if (this.mStorage.remove(i, t)) {
            onChanged();
            return true;
        }
        return false;
    }

    public void remove(int i) {
        this.mStorage.remove(i);
        onChanged();
    }

    public int size() {
        return this.mStorage.size();
    }

    public int keyAt(int i) {
        return this.mStorage.keyAt(i);
    }

    @Override // com.android.server.utils.Snappable
    public Object snapshot() {
        WatchedSparseSetArray watchedSparseSetArray = new WatchedSparseSetArray(this);
        watchedSparseSetArray.seal();
        return watchedSparseSetArray;
    }
}
