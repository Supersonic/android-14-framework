package com.android.internal.telephony.data;

import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DataConfigManager$$ExternalSyntheticLambda15 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(DataUtils.apnTypeToNetworkCapability(((Integer) obj).intValue()));
    }
}
