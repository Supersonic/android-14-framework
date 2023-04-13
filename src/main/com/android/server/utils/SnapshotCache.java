package com.android.server.utils;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes2.dex */
public abstract class SnapshotCache<T> extends Watcher {
    public static final WeakHashMap<SnapshotCache, Void> sCaches = new WeakHashMap<>();
    public volatile boolean mSealed;
    public volatile T mSnapshot;
    public final T mSource;
    public final Statistics mStatistics;

    public abstract T createSnapshot();

    /* loaded from: classes2.dex */
    public static class Statistics {
        public final String mName;
        public final AtomicInteger mReused = new AtomicInteger(0);
        public final AtomicInteger mRebuilt = new AtomicInteger(0);

        public Statistics(String str) {
            this.mName = str;
        }
    }

    public SnapshotCache(T t, Watchable watchable, String str) {
        this.mSnapshot = null;
        this.mSealed = false;
        this.mSource = t;
        watchable.registerObserver(this);
        if (str != null) {
            this.mStatistics = new Statistics(str);
            sCaches.put(this, null);
            return;
        }
        this.mStatistics = null;
    }

    public SnapshotCache(T t, Watchable watchable) {
        this(t, watchable, null);
    }

    public SnapshotCache() {
        this.mSnapshot = null;
        this.mSealed = false;
        this.mSource = null;
        this.mSealed = true;
        this.mStatistics = null;
    }

    @Override // com.android.server.utils.Watcher
    public final void onChange(Watchable watchable) {
        if (this.mSealed) {
            throw new IllegalStateException("attempt to change a sealed object");
        }
        this.mSnapshot = null;
    }

    public final T snapshot() {
        T t = this.mSnapshot;
        if (t == null) {
            t = createSnapshot();
            this.mSnapshot = t;
            Statistics statistics = this.mStatistics;
            if (statistics != null) {
                statistics.mRebuilt.incrementAndGet();
            }
        } else {
            Statistics statistics2 = this.mStatistics;
            if (statistics2 != null) {
                statistics2.mReused.incrementAndGet();
            }
        }
        return t;
    }

    /* loaded from: classes2.dex */
    public static class Sealed<T> extends SnapshotCache<T> {
        @Override // com.android.server.utils.SnapshotCache
        public T createSnapshot() {
            throw new UnsupportedOperationException("cannot snapshot a sealed snaphot");
        }
    }

    /* loaded from: classes2.dex */
    public static class Auto<T extends Snappable<T>> extends SnapshotCache<T> {
        public Auto(T t, Watchable watchable, String str) {
            super(t, watchable, str);
        }

        @Override // com.android.server.utils.SnapshotCache
        public T createSnapshot() {
            return (T) ((Snappable) this.mSource).snapshot();
        }
    }
}
