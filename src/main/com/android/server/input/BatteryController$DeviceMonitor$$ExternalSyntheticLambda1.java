package com.android.server.input;

import com.android.server.input.BatteryController;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class BatteryController$DeviceMonitor$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ BatteryController.DeviceMonitor f$0;

    public /* synthetic */ BatteryController$DeviceMonitor$$ExternalSyntheticLambda1(BatteryController.DeviceMonitor deviceMonitor) {
        this.f$0 = deviceMonitor;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.updateBatteryStateFromNative(((Long) obj).longValue());
    }
}
