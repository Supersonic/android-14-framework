package com.android.internal.telephony.data;

import android.telephony.TelephonyManager;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DataConfigManager$$ExternalSyntheticLambda8 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return TelephonyManager.getNetworkTypeName(((Integer) obj).intValue());
    }
}
