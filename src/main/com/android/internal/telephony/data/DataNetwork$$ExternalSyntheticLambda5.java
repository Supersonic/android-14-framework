package com.android.internal.telephony.data;

import java.util.function.ToIntFunction;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DataNetwork$$ExternalSyntheticLambda5 implements ToIntFunction {
    public final /* synthetic */ DataConfigManager f$0;

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return this.f$0.getNetworkCapabilityPriority(((Integer) obj).intValue());
    }
}
