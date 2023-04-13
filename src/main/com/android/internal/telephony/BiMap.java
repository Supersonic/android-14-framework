package com.android.internal.telephony;

import android.util.ArrayMap;
import java.util.Collection;
import java.util.Map;
/* loaded from: classes.dex */
public class BiMap<K, V> {
    private Map<K, V> mPrimaryMap = new ArrayMap();
    private Map<V, K> mSecondaryMap = new ArrayMap();

    public boolean put(K k, V v) {
        if (k == null || v == null || this.mPrimaryMap.containsKey(k) || this.mSecondaryMap.containsKey(v)) {
            return false;
        }
        this.mPrimaryMap.put(k, v);
        this.mSecondaryMap.put(v, k);
        return true;
    }

    public boolean remove(K k) {
        if (k != null && this.mPrimaryMap.containsKey(k)) {
            V value = getValue(k);
            this.mPrimaryMap.remove(k);
            this.mSecondaryMap.remove(value);
            return true;
        }
        return false;
    }

    public boolean removeValue(V v) {
        if (v == null) {
            return false;
        }
        return remove(getKey(v));
    }

    public V getValue(K k) {
        return this.mPrimaryMap.get(k);
    }

    public K getKey(V v) {
        return this.mSecondaryMap.get(v);
    }

    public Collection<V> getValues() {
        return this.mPrimaryMap.values();
    }

    public void clear() {
        this.mPrimaryMap.clear();
        this.mSecondaryMap.clear();
    }
}
