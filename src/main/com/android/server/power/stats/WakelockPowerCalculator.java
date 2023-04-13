package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class WakelockPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator mPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 12;
    }

    /* loaded from: classes2.dex */
    public static class PowerAndDuration {
        public long durationMs;
        public double powerMah;

        public PowerAndDuration() {
        }
    }

    public WakelockPowerCalculator(PowerProfile powerProfile) {
        this.mPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("cpu.idle"));
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        int i;
        PowerAndDuration powerAndDuration = new PowerAndDuration();
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        UidBatteryConsumer.Builder builder2 = null;
        double d = 0.0d;
        double d2 = 0.0d;
        long j3 = 0;
        long j4 = 0;
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder3 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            double d3 = d;
            long j5 = j3;
            calculateApp(powerAndDuration, builder3.getBatteryStatsUid(), j, 0);
            builder3.setUsageDurationMillis(12, powerAndDuration.durationMs).setConsumedPower(12, powerAndDuration.powerMah);
            if (builder3.isVirtualUid()) {
                j3 = j5;
            } else {
                j3 = j5 + powerAndDuration.durationMs;
                d2 += powerAndDuration.powerMah;
            }
            if (builder3.getUid() == 0) {
                long j6 = powerAndDuration.durationMs;
                d = powerAndDuration.powerMah;
                j4 = j6;
                builder2 = builder3;
            } else {
                d = d3;
            }
        }
        double d4 = d;
        long j7 = j3;
        double d5 = d2;
        UidBatteryConsumer.Builder builder4 = builder2;
        calculateRemaining(powerAndDuration, batteryStats, j, j2, 0, d4, j4, j7);
        double d6 = powerAndDuration.powerMah;
        if (builder4 != null) {
            i = 12;
            builder4.setUsageDurationMillis(12, powerAndDuration.durationMs).setConsumedPower(12, d6);
        } else {
            i = 12;
        }
        long calculateWakeTimeMillis = calculateWakeTimeMillis(batteryStats, j, j2);
        if (calculateWakeTimeMillis < 0) {
            calculateWakeTimeMillis = 0;
        }
        int i2 = i;
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(i2, calculateWakeTimeMillis).setConsumedPower(i2, d6 + d5);
        builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(i2, j7).setConsumedPower(i2, d5);
    }

    public final void calculateApp(PowerAndDuration powerAndDuration, BatteryStats.Uid uid, long j, int i) {
        ArrayMap wakelockStats = uid.getWakelockStats();
        int size = wakelockStats.size();
        long j2 = 0;
        for (int i2 = 0; i2 < size; i2++) {
            BatteryStats.Timer wakeTime = ((BatteryStats.Uid.Wakelock) wakelockStats.valueAt(i2)).getWakeTime(0);
            if (wakeTime != null) {
                j2 += wakeTime.getTotalTimeLocked(j, i);
            }
        }
        long j3 = j2 / 1000;
        powerAndDuration.durationMs = j3;
        powerAndDuration.powerMah = this.mPowerEstimator.calculatePower(j3);
    }

    public final void calculateRemaining(PowerAndDuration powerAndDuration, BatteryStats batteryStats, long j, long j2, int i, double d, long j3, long j4) {
        long calculateWakeTimeMillis = calculateWakeTimeMillis(batteryStats, j, j2) - j4;
        if (calculateWakeTimeMillis > 0) {
            double calculatePower = this.mPowerEstimator.calculatePower(calculateWakeTimeMillis);
            powerAndDuration.durationMs = j3 + calculateWakeTimeMillis;
            powerAndDuration.powerMah = d + calculatePower;
            return;
        }
        powerAndDuration.durationMs = 0L;
        powerAndDuration.powerMah = 0.0d;
    }

    public final long calculateWakeTimeMillis(BatteryStats batteryStats, long j, long j2) {
        return (batteryStats.getBatteryUptime(j2) - batteryStats.getScreenOnTime(j, 0)) / 1000;
    }
}
