package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class CameraPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator mPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 3;
    }

    public CameraPowerCalculator(PowerProfile powerProfile) {
        this.mPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("camera.avg"));
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double calculatePower;
        super.calculate(builder, batteryStats, j, j2, batteryUsageStatsQuery);
        long cameraEnergyConsumptionUC = batteryStats.getCameraEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(cameraEnergyConsumptionUC, batteryUsageStatsQuery);
        long cameraOnTime = batteryStats.getCameraOnTime(j, 0) / 1000;
        if (powerModel == 2) {
            calculatePower = PowerCalculator.uCtoMah(cameraEnergyConsumptionUC);
        } else {
            calculatePower = this.mPowerEstimator.calculatePower(cameraOnTime);
        }
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(3, cameraOnTime).setConsumedPower(3, calculatePower, powerModel);
        builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(3, cameraOnTime).setConsumedPower(3, calculatePower, powerModel);
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double calculatePower;
        long cameraEnergyConsumptionUC = builder.getBatteryStatsUid().getCameraEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(cameraEnergyConsumptionUC, batteryUsageStatsQuery);
        long calculateDuration = this.mPowerEstimator.calculateDuration(uid.getCameraTurnedOnTimer(), j, 0);
        if (powerModel == 2) {
            calculatePower = PowerCalculator.uCtoMah(cameraEnergyConsumptionUC);
        } else {
            calculatePower = this.mPowerEstimator.calculatePower(calculateDuration);
        }
        builder.setUsageDurationMillis(3, calculateDuration).setConsumedPower(3, calculatePower, powerModel);
    }
}
