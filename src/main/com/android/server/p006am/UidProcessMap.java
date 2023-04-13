package com.android.server.p006am;

import android.util.ArrayMap;
import android.util.SparseArray;
/* renamed from: com.android.server.am.UidProcessMap */
/* loaded from: classes.dex */
public class UidProcessMap<E> {
    public final SparseArray<ArrayMap<String, E>> mMap = new SparseArray<>();

    public E get(int i, String str) {
        ArrayMap<String, E> arrayMap = this.mMap.get(i);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str);
    }

    public E put(int i, String str, E e) {
        ArrayMap<String, E> arrayMap = this.mMap.get(i);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>(2);
            this.mMap.put(i, arrayMap);
        }
        arrayMap.put(str, e);
        return e;
    }

    public E remove(int i, String str) {
        ArrayMap<String, E> valueAt;
        int indexOfKey = this.mMap.indexOfKey(i);
        if (indexOfKey >= 0 && (valueAt = this.mMap.valueAt(indexOfKey)) != null) {
            E remove = valueAt.remove(str);
            if (valueAt.isEmpty()) {
                this.mMap.removeAt(indexOfKey);
            }
            return remove;
        }
        return null;
    }

    public SparseArray<ArrayMap<String, E>> getMap() {
        return this.mMap;
    }

    public void clear() {
        this.mMap.clear();
    }
}
