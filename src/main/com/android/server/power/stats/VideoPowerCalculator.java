package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class VideoPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator mPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 5;
    }

    /* loaded from: classes2.dex */
    public static class PowerAndDuration {
        public long durationMs;
        public double powerMah;

        public PowerAndDuration() {
        }
    }

    public VideoPowerCalculator(PowerProfile powerProfile) {
        this.mPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("video"));
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        PowerAndDuration powerAndDuration = new PowerAndDuration();
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            calculateApp(builder2, powerAndDuration, builder2.getBatteryStatsUid(), j);
        }
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(5, powerAndDuration.durationMs).setConsumedPower(5, powerAndDuration.powerMah);
        builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(5, powerAndDuration.durationMs).setConsumedPower(5, powerAndDuration.powerMah);
    }

    public final void calculateApp(UidBatteryConsumer.Builder builder, PowerAndDuration powerAndDuration, BatteryStats.Uid uid, long j) {
        long calculateDuration = this.mPowerEstimator.calculateDuration(uid.getVideoTurnedOnTimer(), j, 0);
        double calculatePower = this.mPowerEstimator.calculatePower(calculateDuration);
        builder.setUsageDurationMillis(5, calculateDuration).setConsumedPower(5, calculatePower);
        if (builder.isVirtualUid()) {
            return;
        }
        powerAndDuration.durationMs += calculateDuration;
        powerAndDuration.powerMah += calculatePower;
    }
}
