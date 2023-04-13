package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseLongArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class ScreenPowerCalculator extends PowerCalculator {
    public final UsageBasedPowerEstimator[] mScreenFullPowerEstimators;
    public final UsageBasedPowerEstimator[] mScreenOnPowerEstimators;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 0;
    }

    /* loaded from: classes2.dex */
    public static class PowerAndDuration {
        public long durationMs;
        public double powerMah;

        public PowerAndDuration() {
        }
    }

    public ScreenPowerCalculator(PowerProfile powerProfile) {
        int numDisplays = powerProfile.getNumDisplays();
        this.mScreenOnPowerEstimators = new UsageBasedPowerEstimator[numDisplays];
        this.mScreenFullPowerEstimators = new UsageBasedPowerEstimator[numDisplays];
        for (int i = 0; i < numDisplays; i++) {
            this.mScreenOnPowerEstimators[i] = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForOrdinal("screen.on.display", i));
            this.mScreenFullPowerEstimators[i] = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForOrdinal("screen.full.display", i));
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double d;
        long j3;
        long j4 = j;
        PowerAndDuration powerAndDuration = new PowerAndDuration();
        long screenOnEnergyConsumptionUC = batteryStats.getScreenOnEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(screenOnEnergyConsumptionUC, batteryUsageStatsQuery);
        calculateTotalDurationAndPower(powerAndDuration, powerModel, batteryStats, j, 0, screenOnEnergyConsumptionUC);
        SparseArray<UidBatteryConsumer.Builder> uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        if (powerModel == 2) {
            PowerAndDuration powerAndDuration2 = new PowerAndDuration();
            int size = uidBatteryConsumerBuilders.size() - 1;
            double d2 = 0.0d;
            long j5 = 0;
            while (size >= 0) {
                UidBatteryConsumer.Builder valueAt = uidBatteryConsumerBuilders.valueAt(size);
                calculateAppUsingEnergyConsumption(powerAndDuration2, valueAt.getBatteryStatsUid(), j4);
                valueAt.setUsageDurationMillis(0, powerAndDuration2.durationMs).setConsumedPower(0, powerAndDuration2.powerMah, powerModel);
                if (!valueAt.isVirtualUid()) {
                    d2 += powerAndDuration2.powerMah;
                    j5 += powerAndDuration2.durationMs;
                }
                size--;
                j4 = j;
            }
            d = d2;
            j3 = j5;
        } else {
            smearScreenBatteryDrain(uidBatteryConsumerBuilders, powerAndDuration, j4);
            d = powerAndDuration.powerMah;
            j3 = powerAndDuration.durationMs;
        }
        builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(0, Math.max(powerAndDuration.powerMah, d), powerModel).setUsageDurationMillis(0, powerAndDuration.durationMs);
        builder.getAggregateBatteryConsumerBuilder(1).setConsumedPower(0, d, powerModel).setUsageDurationMillis(0, j3);
    }

    public final void calculateTotalDurationAndPower(PowerAndDuration powerAndDuration, int i, BatteryStats batteryStats, long j, int i2, long j2) {
        powerAndDuration.durationMs = calculateDuration(batteryStats, j, i2);
        if (i == 2) {
            powerAndDuration.powerMah = PowerCalculator.uCtoMah(j2);
        } else {
            powerAndDuration.powerMah = calculateTotalPowerFromBrightness(batteryStats, j);
        }
    }

    public final void calculateAppUsingEnergyConsumption(PowerAndDuration powerAndDuration, BatteryStats.Uid uid, long j) {
        powerAndDuration.durationMs = getProcessForegroundTimeMs(uid, j);
        long screenOnEnergyConsumptionUC = uid.getScreenOnEnergyConsumptionUC();
        if (screenOnEnergyConsumptionUC < 0) {
            Slog.wtf("ScreenPowerCalculator", "Screen energy not supported, so calculateApp shouldn't de called");
            powerAndDuration.powerMah = 0.0d;
            return;
        }
        powerAndDuration.powerMah = PowerCalculator.uCtoMah(screenOnEnergyConsumptionUC);
    }

    public final long calculateDuration(BatteryStats batteryStats, long j, int i) {
        return batteryStats.getScreenOnTime(j, i) / 1000;
    }

    public final double calculateTotalPowerFromBrightness(BatteryStats batteryStats, long j) {
        int length = this.mScreenOnPowerEstimators.length;
        double d = 0.0d;
        for (int i = 0; i < length; i++) {
            long j2 = 1000;
            d += this.mScreenOnPowerEstimators[i].calculatePower(batteryStats.getDisplayScreenOnTime(i, j) / 1000);
            int i2 = 0;
            while (i2 < 5) {
                d += (this.mScreenFullPowerEstimators[i].calculatePower(batteryStats.getDisplayScreenBrightnessTime(i, i2, j) / j2) * (i2 + 0.5f)) / 5.0d;
                i2++;
                j2 = 1000;
            }
        }
        return d;
    }

    public final void smearScreenBatteryDrain(SparseArray<UidBatteryConsumer.Builder> sparseArray, PowerAndDuration powerAndDuration, long j) {
        SparseLongArray sparseLongArray = new SparseLongArray();
        long j2 = 0;
        long j3 = 0;
        for (int size = sparseArray.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder valueAt = sparseArray.valueAt(size);
            BatteryStats.Uid batteryStatsUid = valueAt.getBatteryStatsUid();
            long processForegroundTimeMs = getProcessForegroundTimeMs(batteryStatsUid, j);
            sparseLongArray.put(batteryStatsUid.getUid(), processForegroundTimeMs);
            if (!valueAt.isVirtualUid()) {
                j3 += processForegroundTimeMs;
            }
        }
        if (j3 >= 600000) {
            double d = powerAndDuration.powerMah;
            int size2 = sparseArray.size() - 1;
            while (size2 >= 0) {
                UidBatteryConsumer.Builder valueAt2 = sparseArray.valueAt(size2);
                long j4 = sparseLongArray.get(valueAt2.getUid(), j2);
                valueAt2.setUsageDurationMillis(0, j4).setConsumedPower(0, (j4 * d) / j3, 1);
                size2--;
                j2 = 0;
            }
        }
    }

    @VisibleForTesting
    public long getProcessForegroundTimeMs(BatteryStats.Uid uid, long j) {
        return Math.min(0 + uid.getProcessStateTime(new int[]{0}[0], j, 0), getForegroundActivityTotalTimeUs(uid, j)) / 1000;
    }

    @VisibleForTesting
    public long getForegroundActivityTotalTimeUs(BatteryStats.Uid uid, long j) {
        BatteryStats.Timer foregroundActivityTimer = uid.getForegroundActivityTimer();
        if (foregroundActivityTimer == null) {
            return 0L;
        }
        return foregroundActivityTimer.getTotalTimeLocked(j, 0);
    }
}
