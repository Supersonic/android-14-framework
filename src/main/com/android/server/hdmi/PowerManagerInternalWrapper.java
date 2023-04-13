package com.android.server.hdmi;

import android.os.PowerManagerInternal;
import com.android.server.LocalServices;
/* loaded from: classes.dex */
public class PowerManagerInternalWrapper {
    public PowerManagerInternal mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);

    public boolean wasDeviceIdleFor(long j) {
        return this.mPowerManagerInternal.wasDeviceIdleFor(j);
    }
}
