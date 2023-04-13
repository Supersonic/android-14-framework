package com.android.internal.app;

import android.util.ArrayMap;
import android.util.SparseArray;
/* loaded from: classes4.dex */
public class ProcessMap<E> {
    final ArrayMap<String, SparseArray<E>> mMap = new ArrayMap<>();

    public E get(String name, int uid) {
        SparseArray<E> uids = this.mMap.get(name);
        if (uids == null) {
            return null;
        }
        return uids.get(uid);
    }

    public E put(String name, int uid, E value) {
        SparseArray<E> uids = this.mMap.get(name);
        if (uids == null) {
            uids = new SparseArray<>(2);
            this.mMap.put(name, uids);
        }
        uids.put(uid, value);
        return value;
    }

    public E remove(String name, int uid) {
        SparseArray<E> uids = this.mMap.get(name);
        if (uids != null) {
            E old = uids.removeReturnOld(uid);
            if (uids.size() == 0) {
                this.mMap.remove(name);
            }
            return old;
        }
        return null;
    }

    public ArrayMap<String, SparseArray<E>> getMap() {
        return this.mMap;
    }

    public int size() {
        return this.mMap.size();
    }

    public void clear() {
        this.mMap.clear();
    }

    public void putAll(ProcessMap<E> other) {
        this.mMap.putAll((ArrayMap<? extends String, ? extends SparseArray<E>>) other.mMap);
    }
}
