package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.util.LongSparseArray;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class MemoryPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator[] mPowerEstimators;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 13;
    }

    public MemoryPowerCalculator(PowerProfile powerProfile) {
        int numElements = powerProfile.getNumElements("memory.bandwidths");
        this.mPowerEstimators = new UsageBasedPowerEstimator[numElements];
        for (int i = 0; i < numElements; i++) {
            this.mPowerEstimators[i] = new UsageBasedPowerEstimator(powerProfile.getAveragePower("memory.bandwidths", i));
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        long calculateDuration = calculateDuration(batteryStats, j, 0);
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(13, calculateDuration).setConsumedPower(13, calculatePower(batteryStats, j, 0));
    }

    public final long calculateDuration(BatteryStats batteryStats, long j, int i) {
        LongSparseArray kernelMemoryStats = batteryStats.getKernelMemoryStats();
        long j2 = 0;
        for (int i2 = 0; i2 < kernelMemoryStats.size(); i2++) {
            UsageBasedPowerEstimator[] usageBasedPowerEstimatorArr = this.mPowerEstimators;
            if (i2 >= usageBasedPowerEstimatorArr.length) {
                break;
            }
            j2 += usageBasedPowerEstimatorArr[i2].calculateDuration((BatteryStats.Timer) kernelMemoryStats.valueAt(i2), j, i);
        }
        return j2;
    }

    public final double calculatePower(BatteryStats batteryStats, long j, int i) {
        LongSparseArray kernelMemoryStats = batteryStats.getKernelMemoryStats();
        double d = 0.0d;
        for (int i2 = 0; i2 < kernelMemoryStats.size(); i2++) {
            UsageBasedPowerEstimator[] usageBasedPowerEstimatorArr = this.mPowerEstimators;
            if (i2 >= usageBasedPowerEstimatorArr.length) {
                break;
            }
            UsageBasedPowerEstimator usageBasedPowerEstimator = usageBasedPowerEstimatorArr[(int) kernelMemoryStats.keyAt(i2)];
            d += usageBasedPowerEstimator.calculatePower(usageBasedPowerEstimator.calculateDuration((BatteryStats.Timer) kernelMemoryStats.valueAt(i2), j, i));
        }
        return d;
    }
}
