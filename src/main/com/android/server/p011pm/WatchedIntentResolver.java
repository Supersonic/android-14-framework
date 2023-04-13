package com.android.server.p011pm;

import com.android.server.IntentResolver;
import com.android.server.p011pm.WatchedIntentFilter;
import com.android.server.p011pm.snapshot.PackageDataSnapshot;
import com.android.server.utils.Snappable;
import com.android.server.utils.Watchable;
import com.android.server.utils.WatchableImpl;
import com.android.server.utils.Watcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* renamed from: com.android.server.pm.WatchedIntentResolver */
/* loaded from: classes2.dex */
public abstract class WatchedIntentResolver<F extends WatchedIntentFilter, R extends WatchedIntentFilter> extends IntentResolver<F, R> implements Watchable, Snappable {
    public static final Comparator<WatchedIntentFilter> sResolvePrioritySorter = new Comparator<WatchedIntentFilter>() { // from class: com.android.server.pm.WatchedIntentResolver.2
        @Override // java.util.Comparator
        public int compare(WatchedIntentFilter watchedIntentFilter, WatchedIntentFilter watchedIntentFilter2) {
            int priority = watchedIntentFilter.getPriority();
            int priority2 = watchedIntentFilter2.getPriority();
            if (priority > priority2) {
                return -1;
            }
            return priority < priority2 ? 1 : 0;
        }
    };
    public final Watchable mWatchable = new WatchableImpl();
    public final Watcher mWatcher = new Watcher() { // from class: com.android.server.pm.WatchedIntentResolver.1
        @Override // com.android.server.utils.Watcher
        public void onChange(Watchable watchable) {
            WatchedIntentResolver.this.dispatchChange(watchable);
        }
    };

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.IntentResolver
    public /* bridge */ /* synthetic */ void addFilter(PackageDataSnapshot packageDataSnapshot, Object obj) {
        addFilter(packageDataSnapshot, (PackageDataSnapshot) ((WatchedIntentFilter) obj));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.IntentResolver
    public /* bridge */ /* synthetic */ void removeFilter(Object obj) {
        removeFilter((WatchedIntentResolver<F, R>) ((WatchedIntentFilter) obj));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.IntentResolver
    public /* bridge */ /* synthetic */ void removeFilterInternal(Object obj) {
        removeFilterInternal((WatchedIntentResolver<F, R>) ((WatchedIntentFilter) obj));
    }

    @Override // com.android.server.utils.Watchable
    public void registerObserver(Watcher watcher) {
        this.mWatchable.registerObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public void unregisterObserver(Watcher watcher) {
        this.mWatchable.unregisterObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public boolean isRegisteredObserver(Watcher watcher) {
        return this.mWatchable.isRegisteredObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public void dispatchChange(Watchable watchable) {
        this.mWatchable.dispatchChange(watchable);
    }

    public void onChanged() {
        dispatchChange(this);
    }

    public void addFilter(PackageDataSnapshot packageDataSnapshot, F f) {
        super.addFilter(packageDataSnapshot, (PackageDataSnapshot) f);
        f.registerObserver(this.mWatcher);
        onChanged();
    }

    public void removeFilter(F f) {
        f.unregisterObserver(this.mWatcher);
        super.removeFilter((WatchedIntentResolver<F, R>) f);
        onChanged();
    }

    public void removeFilterInternal(F f) {
        f.unregisterObserver(this.mWatcher);
        super.removeFilterInternal((WatchedIntentResolver<F, R>) f);
        onChanged();
    }

    @Override // com.android.server.IntentResolver
    public void sortResults(List<R> list) {
        Collections.sort(list, sResolvePrioritySorter);
    }

    public ArrayList<F> findFilters(WatchedIntentFilter watchedIntentFilter) {
        return super.findFilters(watchedIntentFilter.getIntentFilter());
    }

    public void copyFrom(WatchedIntentResolver watchedIntentResolver) {
        super.copyFrom((IntentResolver) watchedIntentResolver);
    }
}
