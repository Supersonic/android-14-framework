package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class IdlePowerCalculator extends PowerCalculator {
    public final double mAveragePowerCpuIdleMahPerUs;
    public final double mAveragePowerCpuSuspendMahPerUs;
    public long mDurationMs;
    public double mPowerMah;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 16;
    }

    public IdlePowerCalculator(PowerProfile powerProfile) {
        this.mAveragePowerCpuSuspendMahPerUs = powerProfile.getAveragePower("cpu.suspend") / 3.6E9d;
        this.mAveragePowerCpuIdleMahPerUs = powerProfile.getAveragePower("cpu.idle") / 3.6E9d;
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        calculatePowerAndDuration(batteryStats, j, j2, 0);
        if (this.mPowerMah != 0.0d) {
            builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(16, this.mPowerMah).setUsageDurationMillis(16, this.mDurationMs);
        }
    }

    public final void calculatePowerAndDuration(BatteryStats batteryStats, long j, long j2, int i) {
        long computeBatteryRealtime = batteryStats.computeBatteryRealtime(j, i);
        this.mPowerMah = (computeBatteryRealtime * this.mAveragePowerCpuSuspendMahPerUs) + (batteryStats.computeBatteryUptime(j2, i) * this.mAveragePowerCpuIdleMahPerUs);
        this.mDurationMs = computeBatteryRealtime / 1000;
    }
}
