package com.google.android.mms.util;

import java.util.HashMap;
/* loaded from: classes5.dex */
public abstract class AbstractCache<K, V> {
    private static final boolean DEBUG = false;
    private static final boolean LOCAL_LOGV = false;
    private static final int MAX_CACHED_ITEMS = 500;
    private static final String TAG = "AbstractCache";
    private final HashMap<K, CacheEntry<V>> mCacheMap = new HashMap<>();

    public boolean put(K key, V value) {
        if (this.mCacheMap.size() < 500 && key != null) {
            CacheEntry<V> cacheEntry = new CacheEntry<>();
            cacheEntry.value = value;
            this.mCacheMap.put(key, cacheEntry);
            return true;
        }
        return false;
    }

    public V get(K key) {
        CacheEntry<V> cacheEntry;
        if (key != null && (cacheEntry = this.mCacheMap.get(key)) != null) {
            cacheEntry.hit++;
            return cacheEntry.value;
        }
        return null;
    }

    public V purge(K key) {
        CacheEntry<V> v = this.mCacheMap.remove(key);
        if (v != null) {
            return v.value;
        }
        return null;
    }

    public void purgeAll() {
        this.mCacheMap.clear();
    }

    public int size() {
        return this.mCacheMap.size();
    }

    /* loaded from: classes5.dex */
    private static class CacheEntry<V> {
        int hit;
        V value;

        private CacheEntry() {
        }
    }
}
