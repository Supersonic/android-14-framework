package com.android.server.p008om;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.server.SystemConfig;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* renamed from: com.android.server.om.OverlayReferenceMapper */
/* loaded from: classes2.dex */
public class OverlayReferenceMapper {
    @GuardedBy({"mLock"})
    public boolean mDeferRebuild;
    public final Provider mProvider;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, ArrayMap<String, ArraySet<String>>> mActorToTargetToOverlays = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, Set<String>> mActorPkgToPkgs = new ArrayMap<>();

    /* renamed from: com.android.server.om.OverlayReferenceMapper$Provider */
    /* loaded from: classes2.dex */
    public interface Provider {
        String getActorPkg(String str);

        Map<String, Set<String>> getTargetToOverlayables(AndroidPackage androidPackage);
    }

    public OverlayReferenceMapper(boolean z, Provider provider) {
        this.mDeferRebuild = z;
        this.mProvider = provider == null ? new Provider() { // from class: com.android.server.om.OverlayReferenceMapper.1
            @Override // com.android.server.p008om.OverlayReferenceMapper.Provider
            public String getActorPkg(String str) {
                return (String) OverlayActorEnforcer.getPackageNameForActor(str, SystemConfig.getInstance().getNamedActors()).first;
            }

            @Override // com.android.server.p008om.OverlayReferenceMapper.Provider
            public Map<String, Set<String>> getTargetToOverlayables(AndroidPackage androidPackage) {
                String overlayTarget = androidPackage.getOverlayTarget();
                if (TextUtils.isEmpty(overlayTarget)) {
                    return Collections.emptyMap();
                }
                String overlayTargetOverlayableName = androidPackage.getOverlayTargetOverlayableName();
                HashMap hashMap = new HashMap();
                HashSet hashSet = new HashSet();
                hashSet.add(overlayTargetOverlayableName);
                hashMap.put(overlayTarget, hashSet);
                return hashMap;
            }
        } : provider;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Map<String, Set<String>> getActorPkgToPkgs() {
        return this.mActorPkgToPkgs;
    }

    public boolean isValidActor(String str, String str2) {
        boolean z;
        synchronized (this.mLock) {
            ensureMapBuilt();
            Set<String> set = this.mActorPkgToPkgs.get(str2);
            z = set != null && set.contains(str);
        }
        return z;
    }

    public ArraySet<String> addPkg(AndroidPackage androidPackage, Map<String, AndroidPackage> map) {
        ArraySet<String> arraySet;
        synchronized (this.mLock) {
            arraySet = new ArraySet<>();
            if (!androidPackage.getOverlayables().isEmpty()) {
                addTarget(androidPackage, map, arraySet);
            }
            if (!this.mProvider.getTargetToOverlayables(androidPackage).isEmpty()) {
                addOverlay(androidPackage, map, arraySet);
            }
            if (!this.mDeferRebuild) {
                rebuild();
            }
        }
        return arraySet;
    }

    public ArraySet<String> removePkg(String str) {
        ArraySet<String> arraySet;
        synchronized (this.mLock) {
            arraySet = new ArraySet<>();
            removeTarget(str, arraySet);
            removeOverlay(str, arraySet);
            if (!this.mDeferRebuild) {
                rebuild();
            }
        }
        return arraySet;
    }

    public final void removeTarget(String str, Collection<String> collection) {
        synchronized (this.mLock) {
            for (int size = this.mActorToTargetToOverlays.size() - 1; size >= 0; size--) {
                ArrayMap<String, ArraySet<String>> valueAt = this.mActorToTargetToOverlays.valueAt(size);
                if (valueAt.containsKey(str)) {
                    valueAt.remove(str);
                    collection.add(this.mProvider.getActorPkg(this.mActorToTargetToOverlays.keyAt(size)));
                    if (valueAt.isEmpty()) {
                        this.mActorToTargetToOverlays.removeAt(size);
                    }
                }
            }
        }
    }

    public final void addTarget(AndroidPackage androidPackage, Map<String, AndroidPackage> map, Collection<String> collection) {
        synchronized (this.mLock) {
            String packageName = androidPackage.getPackageName();
            removeTarget(packageName, collection);
            Map<String, String> overlayables = androidPackage.getOverlayables();
            for (String str : overlayables.keySet()) {
                String str2 = overlayables.get(str);
                addTargetToMap(str2, packageName, collection);
                for (AndroidPackage androidPackage2 : map.values()) {
                    Set<String> set = this.mProvider.getTargetToOverlayables(androidPackage2).get(packageName);
                    if (!CollectionUtils.isEmpty(set) && set.contains(str)) {
                        addOverlayToMap(str2, packageName, androidPackage2.getPackageName(), collection);
                    }
                }
            }
        }
    }

    public final void removeOverlay(String str, Collection<String> collection) {
        synchronized (this.mLock) {
            for (int size = this.mActorToTargetToOverlays.size() - 1; size >= 0; size--) {
                ArrayMap<String, ArraySet<String>> valueAt = this.mActorToTargetToOverlays.valueAt(size);
                for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                    if (valueAt.valueAt(size2).remove(str)) {
                        collection.add(this.mProvider.getActorPkg(this.mActorToTargetToOverlays.keyAt(size)));
                    }
                }
                if (valueAt.isEmpty()) {
                    this.mActorToTargetToOverlays.removeAt(size);
                }
            }
        }
    }

    public final void addOverlay(AndroidPackage androidPackage, Map<String, AndroidPackage> map, Collection<String> collection) {
        synchronized (this.mLock) {
            String packageName = androidPackage.getPackageName();
            removeOverlay(packageName, collection);
            for (Map.Entry<String, Set<String>> entry : this.mProvider.getTargetToOverlayables(androidPackage).entrySet()) {
                Set<String> value = entry.getValue();
                AndroidPackage androidPackage2 = map.get(entry.getKey());
                if (androidPackage2 != null) {
                    String packageName2 = androidPackage2.getPackageName();
                    Map<String, String> overlayables = androidPackage2.getOverlayables();
                    for (String str : value) {
                        String str2 = overlayables.get(str);
                        if (!TextUtils.isEmpty(str2)) {
                            addOverlayToMap(str2, packageName2, packageName, collection);
                        }
                    }
                }
            }
        }
    }

    public void rebuildIfDeferred() {
        synchronized (this.mLock) {
            if (this.mDeferRebuild) {
                rebuild();
                this.mDeferRebuild = false;
            }
        }
    }

    public final void ensureMapBuilt() {
        if (this.mDeferRebuild) {
            rebuildIfDeferred();
            Slog.w("OverlayReferenceMapper", "The actor map was queried before the system was ready, which mayresult in decreased performance.");
        }
    }

    public final void rebuild() {
        synchronized (this.mLock) {
            this.mActorPkgToPkgs.clear();
            for (String str : this.mActorToTargetToOverlays.keySet()) {
                String actorPkg = this.mProvider.getActorPkg(str);
                if (!TextUtils.isEmpty(actorPkg)) {
                    ArrayMap<String, ArraySet<String>> arrayMap = this.mActorToTargetToOverlays.get(str);
                    HashSet hashSet = new HashSet();
                    for (String str2 : arrayMap.keySet()) {
                        hashSet.add(str2);
                        hashSet.addAll(arrayMap.get(str2));
                    }
                    this.mActorPkgToPkgs.put(actorPkg, hashSet);
                }
            }
        }
    }

    public final void addTargetToMap(String str, String str2, Collection<String> collection) {
        ArrayMap<String, ArraySet<String>> arrayMap = this.mActorToTargetToOverlays.get(str);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            this.mActorToTargetToOverlays.put(str, arrayMap);
        }
        if (arrayMap.get(str2) == null) {
            arrayMap.put(str2, new ArraySet<>());
        }
        collection.add(this.mProvider.getActorPkg(str));
    }

    public final void addOverlayToMap(String str, String str2, String str3, Collection<String> collection) {
        synchronized (this.mLock) {
            ArrayMap<String, ArraySet<String>> arrayMap = this.mActorToTargetToOverlays.get(str);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mActorToTargetToOverlays.put(str, arrayMap);
            }
            ArraySet<String> arraySet = arrayMap.get(str2);
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                arrayMap.put(str2, arraySet);
            }
            arraySet.add(str3);
        }
        collection.add(this.mProvider.getActorPkg(str));
    }
}
