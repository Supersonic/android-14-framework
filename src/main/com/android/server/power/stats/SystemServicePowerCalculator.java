package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class SystemServicePowerCalculator extends PowerCalculator {
    public final CpuPowerCalculator mCpuPowerCalculator;
    public final UsageBasedPowerEstimator[] mPowerEstimators;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 7;
    }

    public SystemServicePowerCalculator(PowerProfile powerProfile) {
        this.mCpuPowerCalculator = new CpuPowerCalculator(powerProfile);
        int numCpuClusters = powerProfile.getNumCpuClusters();
        int i = 0;
        for (int i2 = 0; i2 < numCpuClusters; i2++) {
            i += powerProfile.getNumSpeedStepsInCpuCluster(i2);
        }
        this.mPowerEstimators = new UsageBasedPowerEstimator[i];
        int i3 = 0;
        for (int i4 = 0; i4 < numCpuClusters; i4++) {
            int numSpeedStepsInCpuCluster = powerProfile.getNumSpeedStepsInCpuCluster(i4);
            int i5 = 0;
            while (i5 < numSpeedStepsInCpuCluster) {
                this.mPowerEstimators[i3] = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForCpuCore(i4, i5));
                i5++;
                i3++;
            }
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double calculatePowerUsingPowerProfile;
        BatteryStats.Uid uid = (BatteryStats.Uid) batteryStats.getUidStats().get(1000);
        if (uid == null) {
            return;
        }
        long cpuEnergyConsumptionUC = uid.getCpuEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(cpuEnergyConsumptionUC, batteryUsageStatsQuery);
        if (powerModel == 2) {
            calculatePowerUsingPowerProfile = calculatePowerUsingEnergyConsumption(batteryStats, uid, cpuEnergyConsumptionUC);
        } else {
            calculatePowerUsingPowerProfile = calculatePowerUsingPowerProfile(batteryStats);
        }
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.get(1000);
        if (builder2 != null) {
            calculatePowerUsingPowerProfile = Math.min(calculatePowerUsingPowerProfile, builder2.getTotalPower());
            builder2.setConsumedPower(17, -calculatePowerUsingPowerProfile, powerModel);
        }
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder3 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            if (builder3 != builder2) {
                builder3.setConsumedPower(7, builder3.getBatteryStatsUid().getProportionalSystemServiceUsage() * calculatePowerUsingPowerProfile, powerModel);
            }
        }
        builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(7, calculatePowerUsingPowerProfile);
        builder.getAggregateBatteryConsumerBuilder(1).setConsumedPower(7, calculatePowerUsingPowerProfile);
    }

    public final double calculatePowerUsingEnergyConsumption(BatteryStats batteryStats, BatteryStats.Uid uid, long j) {
        double calculatePowerUsingPowerProfile = calculatePowerUsingPowerProfile(batteryStats);
        double calculateUidModeledPowerMah = this.mCpuPowerCalculator.calculateUidModeledPowerMah(uid, 0);
        if (calculateUidModeledPowerMah > 0.0d) {
            return (PowerCalculator.uCtoMah(j) * calculatePowerUsingPowerProfile) / calculateUidModeledPowerMah;
        }
        return 0.0d;
    }

    public final double calculatePowerUsingPowerProfile(BatteryStats batteryStats) {
        long[] systemServiceTimeAtCpuSpeeds = batteryStats.getSystemServiceTimeAtCpuSpeeds();
        double d = 0.0d;
        if (systemServiceTimeAtCpuSpeeds == null) {
            return 0.0d;
        }
        int min = Math.min(this.mPowerEstimators.length, systemServiceTimeAtCpuSpeeds.length);
        for (int i = 0; i < min; i++) {
            d += this.mPowerEstimators[i].calculatePower(systemServiceTimeAtCpuSpeeds[i] / 1000);
        }
        return d;
    }
}
