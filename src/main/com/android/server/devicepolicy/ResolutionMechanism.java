package com.android.server.devicepolicy;

import android.app.admin.PolicyValue;
import java.util.LinkedHashMap;
/* loaded from: classes.dex */
public abstract class ResolutionMechanism<V> {
    /* renamed from: getParcelableResolutionMechanism */
    public abstract android.app.admin.ResolutionMechanism<V> mo3049getParcelableResolutionMechanism();

    public abstract PolicyValue<V> resolve(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap);
}
