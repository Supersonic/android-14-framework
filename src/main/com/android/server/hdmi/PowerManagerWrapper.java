package com.android.server.hdmi;

import android.content.Context;
import android.os.PowerManager;
/* loaded from: classes.dex */
public class PowerManagerWrapper {
    public final PowerManager mPowerManager;

    public PowerManagerWrapper(Context context) {
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public boolean isInteractive() {
        return this.mPowerManager.isInteractive();
    }

    public void wakeUp(long j, int i, String str) {
        this.mPowerManager.wakeUp(j, i, str);
    }

    public void goToSleep(long j, int i, int i2) {
        this.mPowerManager.goToSleep(j, i, i2);
    }

    public PowerManager.WakeLock newWakeLock(int i, String str) {
        return this.mPowerManager.newWakeLock(i, str);
    }
}
