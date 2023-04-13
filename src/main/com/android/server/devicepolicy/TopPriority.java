package com.android.server.devicepolicy;

import android.app.admin.Authority;
import android.app.admin.PolicyValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class TopPriority<V> extends ResolutionMechanism<V> {
    public final List<String> mHighestToLowestPriorityAuthorities;

    public TopPriority(List<String> list) {
        Objects.requireNonNull(list);
        this.mHighestToLowestPriorityAuthorities = list;
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    public PolicyValue<V> resolve(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        if (linkedHashMap.isEmpty()) {
            return null;
        }
        for (final String str : this.mHighestToLowestPriorityAuthorities) {
            Optional<EnforcingAdmin> findFirst = linkedHashMap.keySet().stream().filter(new Predicate() { // from class: com.android.server.devicepolicy.TopPriority$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean hasAuthority;
                    hasAuthority = ((EnforcingAdmin) obj).hasAuthority(str);
                    return hasAuthority;
                }
            }).findFirst();
            if (findFirst.isPresent()) {
                return linkedHashMap.get(findFirst.get());
            }
        }
        return linkedHashMap.entrySet().stream().findFirst().get().getValue();
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    /* renamed from: getParcelableResolutionMechanism */
    public android.app.admin.TopPriority<V> mo3049getParcelableResolutionMechanism() {
        return new android.app.admin.TopPriority<>(getParcelableAuthorities());
    }

    public final List<Authority> getParcelableAuthorities() {
        ArrayList arrayList = new ArrayList();
        for (String str : this.mHighestToLowestPriorityAuthorities) {
            arrayList.add(EnforcingAdmin.getParcelableAuthority(str));
        }
        return arrayList;
    }

    public String toString() {
        return "TopPriority { mHighestToLowestPriorityAuthorities= " + this.mHighestToLowestPriorityAuthorities + " }";
    }
}
