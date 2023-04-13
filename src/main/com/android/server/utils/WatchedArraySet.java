package com.android.server.utils;

import android.util.ArraySet;
import java.util.Collection;
/* loaded from: classes2.dex */
public class WatchedArraySet<E> extends WatchableImpl implements Snappable {
    public final Watcher mObserver;
    public final ArraySet<E> mStorage;
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
        if (this.mWatching && (obj instanceof Watchable) && !this.mStorage.contains(obj)) {
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

    public WatchedArraySet() {
        this(0, false);
    }

    public WatchedArraySet(int i, boolean z) {
        this.mWatching = false;
        this.mObserver = new Watcher() { // from class: com.android.server.utils.WatchedArraySet.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                WatchedArraySet.this.dispatchChange(watchable);
            }
        };
        this.mStorage = new ArraySet<>(i, z);
    }

    public ArraySet<E> untrackedStorage() {
        return this.mStorage;
    }

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

    public boolean contains(Object obj) {
        return this.mStorage.contains(obj);
    }

    public E valueAt(int i) {
        return this.mStorage.valueAt(i);
    }

    public boolean isEmpty() {
        return this.mStorage.isEmpty();
    }

    public boolean add(E e) {
        boolean add = this.mStorage.add(e);
        registerChild(e);
        onChanged();
        return add;
    }

    public void addAll(Collection<? extends E> collection) {
        this.mStorage.addAll(collection);
        onChanged();
    }

    public boolean remove(Object obj) {
        if (this.mStorage.remove(obj)) {
            unregisterChildIf(obj);
            onChanged();
            return true;
        }
        return false;
    }

    public E removeAt(int i) {
        E removeAt = this.mStorage.removeAt(i);
        unregisterChildIf(removeAt);
        onChanged();
        return removeAt;
    }

    public int size() {
        return this.mStorage.size();
    }

    public boolean equals(Object obj) {
        if (obj instanceof WatchedArraySet) {
            return this.mStorage.equals(((WatchedArraySet) obj).mStorage);
        }
        return this.mStorage.equals(obj);
    }

    public int hashCode() {
        return this.mStorage.hashCode();
    }

    public String toString() {
        return this.mStorage.toString();
    }

    @Override // com.android.server.utils.Snappable
    public WatchedArraySet<E> snapshot() {
        WatchedArraySet<E> watchedArraySet = new WatchedArraySet<>();
        snapshot(watchedArraySet, this);
        return watchedArraySet;
    }

    public static <E> void snapshot(WatchedArraySet<E> watchedArraySet, WatchedArraySet<E> watchedArraySet2) {
        if (watchedArraySet.size() != 0) {
            throw new IllegalArgumentException("snapshot destination is not empty");
        }
        int size = watchedArraySet2.size();
        watchedArraySet.mStorage.ensureCapacity(size);
        for (int i = 0; i < size; i++) {
            watchedArraySet.mStorage.append(Snapshots.maybeSnapshot(watchedArraySet2.valueAt(i)));
        }
        watchedArraySet.seal();
    }
}
