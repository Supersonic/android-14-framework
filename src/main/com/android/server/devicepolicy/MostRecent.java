package com.android.server.devicepolicy;

import android.app.admin.PolicyValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
/* loaded from: classes.dex */
public final class MostRecent<V> extends ResolutionMechanism<V> {
    public String toString() {
        return "MostRecent {}";
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    public PolicyValue<V> resolve(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        ArrayList arrayList = new ArrayList(linkedHashMap.entrySet());
        if (arrayList.isEmpty()) {
            return null;
        }
        return (PolicyValue) ((Map.Entry) arrayList.get(arrayList.size() - 1)).getValue();
    }

    @Override // com.android.server.devicepolicy.ResolutionMechanism
    /* renamed from: getParcelableResolutionMechanism */
    public android.app.admin.MostRecent<V> mo3049getParcelableResolutionMechanism() {
        return new android.app.admin.MostRecent<>();
    }
}
