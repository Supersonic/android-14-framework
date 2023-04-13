package com.android.internal.telephony.data;

import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DataNetwork$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ DataConfigManager f$0;

    public /* synthetic */ DataNetwork$$ExternalSyntheticLambda0(DataConfigManager dataConfigManager) {
        this.f$0 = dataConfigManager;
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(this.f$0.getNetworkCapabilityPriority(((Integer) obj).intValue()));
    }
}
