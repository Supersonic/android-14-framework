package com.android.server.utils;

import android.util.ArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class WatchedArrayMap<K, V> extends WatchableImpl implements Map<K, V>, Snappable {
    public final Watcher mObserver;
    public final ArrayMap<K, V> mStorage;
    public volatile boolean mWatching;

    public final void onChanged() {
        dispatchChange(this);
    }

    public final void registerChild(Object obj) {
        if (this.mWatching && (obj instanceof Watchable)) {
            ((Watchable) obj).registerObserver(this.mObserver);
        }
    }

    public final void unregisterChild(Object obj) {
        if (this.mWatching && (obj instanceof Watchable)) {
            ((Watchable) obj).unregisterObserver(this.mObserver);
        }
    }

    public final void unregisterChildIf(Object obj) {
        if (this.mWatching && (obj instanceof Watchable) && !this.mStorage.containsValue(obj)) {
            ((Watchable) obj).unregisterObserver(this.mObserver);
        }
    }

    @Override // com.android.server.utils.WatchableImpl, com.android.server.utils.Watchable
    public void registerObserver(Watcher watcher) {
        super.registerObserver(watcher);
        if (registeredObserverCount() == 1) {
            this.mWatching = true;
            int size = this.mStorage.size();
            for (int i = 0; i < size; i++) {
                registerChild(this.mStorage.valueAt(i));
            }
        }
    }

    @Override // com.android.server.utils.WatchableImpl, com.android.server.utils.Watchable
    public void unregisterObserver(Watcher watcher) {
        super.unregisterObserver(watcher);
        if (registeredObserverCount() == 0) {
            int size = this.mStorage.size();
            for (int i = 0; i < size; i++) {
                unregisterChild(this.mStorage.valueAt(i));
            }
            this.mWatching = false;
        }
    }

    public WatchedArrayMap() {
        this(0, false);
    }

    public WatchedArrayMap(int i) {
        this(i, false);
    }

    public WatchedArrayMap(int i, boolean z) {
        this.mWatching = false;
        this.mObserver = new Watcher() { // from class: com.android.server.utils.WatchedArrayMap.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                WatchedArrayMap.this.dispatchChange(watchable);
            }
        };
        this.mStorage = new ArrayMap<>(i, z);
    }

    public ArrayMap<K, V> untrackedStorage() {
        return this.mStorage;
    }

    @Override // java.util.Map
    public void clear() {
        if (this.mWatching) {
            int size = this.mStorage.size();
            for (int i = 0; i < size; i++) {
                unregisterChild(this.mStorage.valueAt(i));
            }
        }
        this.mStorage.clear();
        onChanged();
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        return this.mStorage.containsKey(obj);
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        return this.mStorage.containsValue(obj);
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.mStorage.entrySet());
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        if (obj instanceof WatchedArrayMap) {
            return this.mStorage.equals(((WatchedArrayMap) obj).mStorage);
        }
        return false;
    }

    @Override // java.util.Map
    public V get(Object obj) {
        return this.mStorage.get(obj);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.mStorage.hashCode();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.mStorage.isEmpty();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.mStorage.keySet());
    }

    @Override // java.util.Map
    public V put(K k, V v) {
        V put = this.mStorage.put(k, v);
        registerChild(v);
        onChanged();
        return put;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        V remove = this.mStorage.remove(obj);
        unregisterChildIf(remove);
        onChanged();
        return remove;
    }

    @Override // java.util.Map
    public int size() {
        return this.mStorage.size();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.mStorage.values());
    }

    public K keyAt(int i) {
        return this.mStorage.keyAt(i);
    }

    public V valueAt(int i) {
        return this.mStorage.valueAt(i);
    }

    public V removeAt(int i) {
        V removeAt = this.mStorage.removeAt(i);
        unregisterChildIf(removeAt);
        onChanged();
        return removeAt;
    }

    @Override // com.android.server.utils.Snappable
    public WatchedArrayMap<K, V> snapshot() {
        WatchedArrayMap<K, V> watchedArrayMap = new WatchedArrayMap<>();
        snapshot(watchedArrayMap, this);
        return watchedArrayMap;
    }

    public void snapshot(WatchedArrayMap<K, V> watchedArrayMap) {
        snapshot(this, watchedArrayMap);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> void snapshot(WatchedArrayMap<K, V> watchedArrayMap, WatchedArrayMap<K, V> watchedArrayMap2) {
        if (watchedArrayMap.size() != 0) {
            throw new IllegalArgumentException("snapshot destination is not empty");
        }
        int size = watchedArrayMap2.size();
        watchedArrayMap.mStorage.ensureCapacity(size);
        for (int i = 0; i < size; i++) {
            Object maybeSnapshot = Snapshots.maybeSnapshot(watchedArrayMap2.valueAt(i));
            watchedArrayMap.mStorage.put(watchedArrayMap2.keyAt(i), maybeSnapshot);
        }
        watchedArrayMap.seal();
    }
}
