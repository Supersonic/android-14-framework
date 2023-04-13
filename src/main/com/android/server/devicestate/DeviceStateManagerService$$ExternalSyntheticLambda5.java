package com.android.server.devicestate;

import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DeviceStateManagerService$$ExternalSyntheticLambda5 implements Consumer {
    public final /* synthetic */ OverrideRequestController f$0;

    public /* synthetic */ DeviceStateManagerService$$ExternalSyntheticLambda5(OverrideRequestController overrideRequestController) {
        this.f$0 = overrideRequestController;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.cancelRequest((OverrideRequest) obj);
    }
}
