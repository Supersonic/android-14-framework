package com.android.internal.telephony;

import android.telephony.PhysicalChannelConfig;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ServiceStateTracker$$ExternalSyntheticLambda2 implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((PhysicalChannelConfig) obj).getCellBandwidthDownlinkKhz());
    }
}
