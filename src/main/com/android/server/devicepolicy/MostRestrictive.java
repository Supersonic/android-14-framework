package com.android.server.devicepolicy;

import android.app.admin.PolicyValue;
import java.util.LinkedHashMap;
import java.util.List;
/* loaded from: classes.dex */
public final class MostRestrictive<V> extends ResolutionMechanism<V> {
    public List<PolicyValue<V>> mMostToLeastRestrictive;

    public MostRestrictive(List<PolicyValue<V>> list) {
        this.mMostToLeastRestrictive = list;
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    public PolicyValue<V> resolve(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        if (linkedHashMap.isEmpty()) {
            return null;
        }
        for (PolicyValue<V> policyValue : this.mMostToLeastRestrictive) {
            if (linkedHashMap.containsValue(policyValue)) {
                return policyValue;
            }
        }
        return linkedHashMap.entrySet().stream().findFirst().get().getValue();
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    /* renamed from: getParcelableResolutionMechanism */
    public android.app.admin.MostRestrictive<V> mo3049getParcelableResolutionMechanism() {
        return new android.app.admin.MostRestrictive<>(this.mMostToLeastRestrictive);
    }

    public String toString() {
        return "MostRestrictive { mMostToLeastRestrictive= " + this.mMostToLeastRestrictive + " }";
    }
}
