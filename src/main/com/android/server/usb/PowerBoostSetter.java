package com.android.server.usb;

import android.os.PowerManagerInternal;
import android.util.Log;
import com.android.server.LocalServices;
import java.time.Instant;
/* loaded from: classes2.dex */
public class PowerBoostSetter {
    public PowerManagerInternal mPowerManagerInternal;
    public Instant mPreviousTimeout = null;

    public PowerBoostSetter() {
        this.mPowerManagerInternal = null;
        this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
    }

    public void boostPower() {
        if (this.mPowerManagerInternal == null) {
            this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        }
        if (this.mPowerManagerInternal == null) {
            Log.w("PowerBoostSetter", "PowerManagerInternal null");
        } else if (this.mPreviousTimeout == null || Instant.now().isAfter(this.mPreviousTimeout.plusMillis(7500L))) {
            this.mPreviousTimeout = Instant.now();
            this.mPowerManagerInternal.setPowerBoost(0, 15000);
        }
    }
}
