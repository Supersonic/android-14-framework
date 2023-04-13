package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class PhonePowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator mPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 14;
    }

    public PhonePowerCalculator(PowerProfile powerProfile) {
        this.mPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("radio.active"));
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double uCtoMah;
        long phoneEnergyConsumptionUC = batteryStats.getPhoneEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(phoneEnergyConsumptionUC, batteryUsageStatsQuery);
        long phoneOnTime = batteryStats.getPhoneOnTime(j, 0) / 1000;
        if (powerModel == 2) {
            uCtoMah = PowerCalculator.uCtoMah(phoneEnergyConsumptionUC);
        } else {
            uCtoMah = this.mPowerEstimator.calculatePower(phoneOnTime);
        }
        if (uCtoMah == 0.0d) {
            return;
        }
        builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(14, uCtoMah, powerModel).setUsageDurationMillis(14, phoneOnTime);
    }
}
