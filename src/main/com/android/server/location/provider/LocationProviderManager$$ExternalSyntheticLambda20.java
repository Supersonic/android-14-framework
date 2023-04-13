package com.android.server.location.provider;

import com.android.server.location.provider.LocationProviderManager;
import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class LocationProviderManager$$ExternalSyntheticLambda20 implements Predicate {
    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((LocationProviderManager.Registration) obj).onProviderLocationRequestChanged();
    }
}
