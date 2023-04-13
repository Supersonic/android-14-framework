package com.android.internal.telephony.data;

import com.android.internal.telephony.util.TelephonyUtils;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DataSettingsManager$$ExternalSyntheticLambda1 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return TelephonyUtils.mobileDataPolicyToString(((Integer) obj).intValue());
    }
}
