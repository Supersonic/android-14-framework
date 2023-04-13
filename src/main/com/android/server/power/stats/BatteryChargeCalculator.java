package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
/* loaded from: classes2.dex */
public class BatteryChargeCalculator extends PowerCalculator {
    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return true;
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        builder.setDischargePercentage(batteryStats.getDischargeAmount(0));
        int learnedBatteryCapacity = batteryStats.getLearnedBatteryCapacity() / 1000;
        if (learnedBatteryCapacity <= 0 && (learnedBatteryCapacity = batteryStats.getMinLearnedBatteryCapacity() / 1000) <= 0) {
            learnedBatteryCapacity = batteryStats.getEstimatedBatteryCapacity();
        }
        builder.setBatteryCapacity(learnedBatteryCapacity);
        double lowDischargeAmountSinceCharge = (batteryStats.getLowDischargeAmountSinceCharge() * learnedBatteryCapacity) / 100.0d;
        double highDischargeAmountSinceCharge = (batteryStats.getHighDischargeAmountSinceCharge() * learnedBatteryCapacity) / 100.0d;
        builder.setDischargePercentage(batteryStats.getDischargeAmount(0)).setDischargedPowerRange(lowDischargeAmountSinceCharge, highDischargeAmountSinceCharge).setDischargeDurationMs(batteryStats.getBatteryRealtime(j) / 1000);
        long computeBatteryTimeRemaining = batteryStats.computeBatteryTimeRemaining(j);
        if (computeBatteryTimeRemaining != -1) {
            builder.setBatteryTimeRemainingMs(computeBatteryTimeRemaining / 1000);
        }
        long computeChargeTimeRemaining = batteryStats.computeChargeTimeRemaining(j);
        if (computeChargeTimeRemaining != -1) {
            builder.setChargeTimeRemainingMs(computeChargeTimeRemaining / 1000);
        }
        long uahDischarge = batteryStats.getUahDischarge(0) / 1000;
        if (uahDischarge == 0) {
            uahDischarge = (long) (((lowDischargeAmountSinceCharge + highDischargeAmountSinceCharge) / 2.0d) + 0.5d);
        }
        builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(uahDischarge);
    }
}
