package com.android.server.devicepolicy;

import android.app.admin.PolicyValue;
import android.app.admin.StringSetPolicyValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class StringSetUnion extends ResolutionMechanism<Set<String>> {
    public String toString() {
        return "SetUnion {}";
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    public PolicyValue<Set<String>> resolve(LinkedHashMap<EnforcingAdmin, PolicyValue<Set<String>>> linkedHashMap) {
        Objects.requireNonNull(linkedHashMap);
        if (linkedHashMap.isEmpty()) {
            return null;
        }
        HashSet hashSet = new HashSet();
        for (PolicyValue<Set<String>> policyValue : linkedHashMap.values()) {
            hashSet.addAll((Collection) policyValue.getValue());
        }
        return new StringSetPolicyValue(hashSet);
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    /* renamed from: getParcelableResolutionMechanism  reason: avoid collision after fix types in other method */
    public android.app.admin.StringSetUnion mo3049getParcelableResolutionMechanism() {
        return new android.app.admin.StringSetUnion();
    }
}
