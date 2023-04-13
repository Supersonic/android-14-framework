package com.android.server.p011pm;

import android.content.IntentFilter;
import com.android.server.utils.SnapshotCache;
/* renamed from: com.android.server.pm.PersistentPreferredIntentResolver */
/* loaded from: classes2.dex */
public class PersistentPreferredIntentResolver extends WatchedIntentResolver<PersistentPreferredActivity, PersistentPreferredActivity> {
    public final SnapshotCache<PersistentPreferredIntentResolver> mSnapshot;

    @Override // com.android.server.IntentResolver
    public PersistentPreferredActivity[] newArray(int i) {
        return new PersistentPreferredActivity[i];
    }

    @Override // com.android.server.IntentResolver
    public IntentFilter getIntentFilter(PersistentPreferredActivity persistentPreferredActivity) {
        return persistentPreferredActivity.getIntentFilter();
    }

    @Override // com.android.server.IntentResolver
    public boolean isPackageForFilter(String str, PersistentPreferredActivity persistentPreferredActivity) {
        return str.equals(persistentPreferredActivity.mComponent.getPackageName());
    }

    public PersistentPreferredIntentResolver() {
        this.mSnapshot = makeCache();
    }

    @Override // com.android.server.IntentResolver
    public PersistentPreferredActivity snapshot(PersistentPreferredActivity persistentPreferredActivity) {
        if (persistentPreferredActivity == null) {
            return null;
        }
        return persistentPreferredActivity.snapshot();
    }

    public PersistentPreferredIntentResolver(PersistentPreferredIntentResolver persistentPreferredIntentResolver) {
        copyFrom((WatchedIntentResolver) persistentPreferredIntentResolver);
        this.mSnapshot = new SnapshotCache.Sealed();
    }

    public final SnapshotCache makeCache() {
        return new SnapshotCache<PersistentPreferredIntentResolver>(this, this) { // from class: com.android.server.pm.PersistentPreferredIntentResolver.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.utils.SnapshotCache
            public PersistentPreferredIntentResolver createSnapshot() {
                return new PersistentPreferredIntentResolver();
            }
        };
    }

    @Override // com.android.server.utils.Snappable
    public PersistentPreferredIntentResolver snapshot() {
        return this.mSnapshot.snapshot();
    }
}
