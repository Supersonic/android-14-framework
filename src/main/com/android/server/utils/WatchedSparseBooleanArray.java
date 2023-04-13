package com.android.server.utils;

import android.util.SparseBooleanArray;
/* loaded from: classes2.dex */
public class WatchedSparseBooleanArray extends WatchableImpl implements Snappable {
    public final SparseBooleanArray mStorage;

    public final void onChanged() {
        dispatchChange(this);
    }

    public WatchedSparseBooleanArray() {
        this.mStorage = new SparseBooleanArray();
    }

    public WatchedSparseBooleanArray(WatchedSparseBooleanArray watchedSparseBooleanArray) {
        this.mStorage = watchedSparseBooleanArray.mStorage.clone();
    }

    public boolean get(int i) {
        return this.mStorage.get(i);
    }

    public void delete(int i) {
        this.mStorage.delete(i);
        onChanged();
    }

    public void put(int i, boolean z) {
        this.mStorage.put(i, z);
        onChanged();
    }

    public int hashCode() {
        return this.mStorage.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof WatchedSparseBooleanArray) {
            return this.mStorage.equals(((WatchedSparseBooleanArray) obj).mStorage);
        }
        return false;
    }

    public String toString() {
        return this.mStorage.toString();
    }

    @Override // com.android.server.utils.Snappable
    public WatchedSparseBooleanArray snapshot() {
        WatchedSparseBooleanArray watchedSparseBooleanArray = new WatchedSparseBooleanArray(this);
        watchedSparseBooleanArray.seal();
        return watchedSparseBooleanArray;
    }
}
