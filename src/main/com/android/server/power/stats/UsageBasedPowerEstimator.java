package com.android.server.power.stats;

import android.os.BatteryStats;
/* loaded from: classes2.dex */
public class UsageBasedPowerEstimator {
    public final double mAveragePowerMahPerMs;

    public UsageBasedPowerEstimator(double d) {
        this.mAveragePowerMahPerMs = d / 3600000.0d;
    }

    public boolean isSupported() {
        return this.mAveragePowerMahPerMs != 0.0d;
    }

    public long calculateDuration(BatteryStats.Timer timer, long j, int i) {
        if (timer == null) {
            return 0L;
        }
        return timer.getTotalTimeLocked(j, i) / 1000;
    }

    public double calculatePower(long j) {
        return this.mAveragePowerMahPerMs * j;
    }
}
