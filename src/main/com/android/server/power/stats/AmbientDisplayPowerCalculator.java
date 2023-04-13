package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class AmbientDisplayPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator[] mPowerEstimators;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 15;
    }

    public AmbientDisplayPowerCalculator(PowerProfile powerProfile) {
        int numDisplays = powerProfile.getNumDisplays();
        this.mPowerEstimators = new UsageBasedPowerEstimator[numDisplays];
        for (int i = 0; i < numDisplays; i++) {
            this.mPowerEstimators[i] = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForOrdinal("ambient.on.display", i));
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        long screenDozeEnergyConsumptionUC = batteryStats.getScreenDozeEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(screenDozeEnergyConsumptionUC, batteryUsageStatsQuery);
        long calculateDuration = calculateDuration(batteryStats, j, 0);
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(15, calculateDuration).setConsumedPower(15, calculateTotalPower(powerModel, batteryStats, j, screenDozeEnergyConsumptionUC), powerModel);
    }

    public final long calculateDuration(BatteryStats batteryStats, long j, int i) {
        return batteryStats.getScreenDozeTime(j, i) / 1000;
    }

    public final double calculateTotalPower(int i, BatteryStats batteryStats, long j, long j2) {
        if (i == 2) {
            return PowerCalculator.uCtoMah(j2);
        }
        return calculateEstimatedPower(batteryStats, j);
    }

    public final double calculateEstimatedPower(BatteryStats batteryStats, long j) {
        int length = this.mPowerEstimators.length;
        double d = 0.0d;
        for (int i = 0; i < length; i++) {
            d += this.mPowerEstimators[i].calculatePower(batteryStats.getDisplayScreenDozeTime(i, j) / 1000);
        }
        return d;
    }
}
