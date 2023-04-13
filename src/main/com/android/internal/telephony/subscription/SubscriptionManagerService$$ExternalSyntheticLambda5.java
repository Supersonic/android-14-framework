package com.android.internal.telephony.subscription;

import android.telephony.SubscriptionInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SubscriptionManagerService$$ExternalSyntheticLambda5 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((SubscriptionInfo) obj).getSimSlotIndex());
    }
}
