package com.android.server.utils;

import android.util.SparseIntArray;
/* loaded from: classes2.dex */
public class WatchedSparseIntArray extends WatchableImpl implements Snappable {
    public final SparseIntArray mStorage;

    public final void onChanged() {
        dispatchChange(this);
    }

    public WatchedSparseIntArray() {
        this.mStorage = new SparseIntArray();
    }

    public WatchedSparseIntArray(WatchedSparseIntArray watchedSparseIntArray) {
        this.mStorage = watchedSparseIntArray.mStorage.clone();
    }

    public int get(int i, int i2) {
        return this.mStorage.get(i, i2);
    }

    public void delete(int i) {
        int indexOfKey = this.mStorage.indexOfKey(i);
        if (indexOfKey >= 0) {
            this.mStorage.removeAt(indexOfKey);
            onChanged();
        }
    }

    public void put(int i, int i2) {
        this.mStorage.put(i, i2);
        onChanged();
    }

    public int size() {
        return this.mStorage.size();
    }

    public int keyAt(int i) {
        return this.mStorage.keyAt(i);
    }

    public int valueAt(int i) {
        return this.mStorage.valueAt(i);
    }

    public int hashCode() {
        return this.mStorage.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof WatchedSparseIntArray) {
            return this.mStorage.equals(((WatchedSparseIntArray) obj).mStorage);
        }
        return false;
    }

    public String toString() {
        return this.mStorage.toString();
    }

    @Override // com.android.server.utils.Snappable
    public WatchedSparseIntArray snapshot() {
        WatchedSparseIntArray watchedSparseIntArray = new WatchedSparseIntArray(this);
        watchedSparseIntArray.seal();
        return watchedSparseIntArray;
    }

    public void snapshot(WatchedSparseIntArray watchedSparseIntArray) {
        snapshot(this, watchedSparseIntArray);
    }

    public static void snapshot(WatchedSparseIntArray watchedSparseIntArray, WatchedSparseIntArray watchedSparseIntArray2) {
        if (watchedSparseIntArray.size() != 0) {
            throw new IllegalArgumentException("snapshot destination is not empty");
        }
        int size = watchedSparseIntArray2.size();
        for (int i = 0; i < size; i++) {
            watchedSparseIntArray.mStorage.put(watchedSparseIntArray2.keyAt(i), watchedSparseIntArray2.valueAt(i));
        }
        watchedSparseIntArray.seal();
    }
}
