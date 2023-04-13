package com.android.server.policy;

import android.content.Context;
import com.android.server.devicestate.DeviceStatePolicy;
import com.android.server.devicestate.DeviceStateProvider;
/* loaded from: classes2.dex */
public final class DeviceStatePolicyImpl extends DeviceStatePolicy {
    public final DeviceStateProvider mProvider;

    public DeviceStatePolicyImpl(Context context) {
        super(context);
        this.mProvider = DeviceStateProviderImpl.create(this.mContext);
    }

    @Override // com.android.server.devicestate.DeviceStatePolicy
    public DeviceStateProvider getDeviceStateProvider() {
        return this.mProvider;
    }

    @Override // com.android.server.devicestate.DeviceStatePolicy
    public void configureDeviceForState(int i, Runnable runnable) {
        runnable.run();
    }
}
