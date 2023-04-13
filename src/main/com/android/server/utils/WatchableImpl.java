package com.android.server.utils;

import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public class WatchableImpl implements Watchable {
    public final ArrayList<Watcher> mObservers = new ArrayList<>();
    @GuardedBy({"mObservers"})
    public boolean mSealed = false;

    @Override // com.android.server.utils.Watchable
    public void registerObserver(Watcher watcher) {
        Objects.requireNonNull(watcher, "observer may not be null");
        synchronized (this.mObservers) {
            if (!this.mObservers.contains(watcher)) {
                this.mObservers.add(watcher);
            }
        }
    }

    @Override // com.android.server.utils.Watchable
    public void unregisterObserver(Watcher watcher) {
        Objects.requireNonNull(watcher, "observer may not be null");
        synchronized (this.mObservers) {
            this.mObservers.remove(watcher);
        }
    }

    @Override // com.android.server.utils.Watchable
    public boolean isRegisteredObserver(Watcher watcher) {
        boolean contains;
        synchronized (this.mObservers) {
            contains = this.mObservers.contains(watcher);
        }
        return contains;
    }

    public int registeredObserverCount() {
        return this.mObservers.size();
    }

    @Override // com.android.server.utils.Watchable
    public void dispatchChange(Watchable watchable) {
        synchronized (this.mObservers) {
            if (this.mSealed) {
                throw new IllegalStateException("attempt to change a sealed object");
            }
            int size = this.mObservers.size();
            for (int i = 0; i < size; i++) {
                this.mObservers.get(i).onChange(watchable);
            }
        }
    }

    public void seal() {
        synchronized (this.mObservers) {
            this.mSealed = true;
        }
    }
}
