package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class FlashlightPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator mPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 6;
    }

    public FlashlightPowerCalculator(PowerProfile powerProfile) {
        this.mPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("camera.flashlight"));
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        super.calculate(builder, batteryStats, j, j2, batteryUsageStatsQuery);
        long flashlightOnTime = batteryStats.getFlashlightOnTime(j, 0) / 1000;
        double calculatePower = this.mPowerEstimator.calculatePower(flashlightOnTime);
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(6, flashlightOnTime).setConsumedPower(6, calculatePower);
        builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(6, flashlightOnTime).setConsumedPower(6, calculatePower);
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        long calculateDuration = this.mPowerEstimator.calculateDuration(uid.getFlashlightTurnedOnTimer(), j, 0);
        builder.setUsageDurationMillis(6, calculateDuration).setConsumedPower(6, this.mPowerEstimator.calculatePower(calculateDuration));
    }
}
