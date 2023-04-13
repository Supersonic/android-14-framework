package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
/* loaded from: classes2.dex */
public class GnssPowerCalculator extends PowerCalculator {
    public final double mAveragePowerGnssOn;
    public final double[] mAveragePowerPerSignalQuality = new double[2];

    public final double computePower(long j, double d) {
        return (j * d) / 3600000.0d;
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 10;
    }

    public GnssPowerCalculator(PowerProfile powerProfile) {
        this.mAveragePowerGnssOn = powerProfile.getAveragePowerOrDefault("gps.on", -1.0d);
        for (int i = 0; i < 2; i++) {
            this.mAveragePowerPerSignalQuality[i] = powerProfile.getAveragePower("gps.signalqualitybased", i);
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double averageGnssPower = getAverageGnssPower(batteryStats, j, 0);
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        int size = uidBatteryConsumerBuilders.size() - 1;
        double d = 0.0d;
        while (size >= 0) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            long gnssEnergyConsumptionUC = builder2.getBatteryStatsUid().getGnssEnergyConsumptionUC();
            double d2 = d;
            int i = size;
            SparseArray sparseArray = uidBatteryConsumerBuilders;
            d = !builder2.isVirtualUid() ? d2 + calculateApp(builder2, builder2.getBatteryStatsUid(), PowerCalculator.getPowerModel(gnssEnergyConsumptionUC, batteryUsageStatsQuery), j, averageGnssPower, gnssEnergyConsumptionUC) : d2;
            size = i - 1;
            uidBatteryConsumerBuilders = sparseArray;
        }
        long gnssEnergyConsumptionUC2 = batteryStats.getGnssEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(gnssEnergyConsumptionUC2, batteryUsageStatsQuery);
        builder.getAggregateBatteryConsumerBuilder(0).setConsumedPower(10, powerModel == 2 ? PowerCalculator.uCtoMah(gnssEnergyConsumptionUC2) : d, powerModel);
        builder.getAggregateBatteryConsumerBuilder(1).setConsumedPower(10, d, powerModel);
    }

    public final double calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, int i, long j, double d, long j2) {
        double uCtoMah;
        long computeDuration = computeDuration(uid, j, 0);
        if (i == 2) {
            uCtoMah = PowerCalculator.uCtoMah(j2);
        } else {
            uCtoMah = computePower(computeDuration, d);
        }
        builder.setUsageDurationMillis(10, computeDuration).setConsumedPower(10, uCtoMah, i);
        return uCtoMah;
    }

    public final long computeDuration(BatteryStats.Uid uid, long j, int i) {
        BatteryStats.Uid.Sensor sensor = (BatteryStats.Uid.Sensor) uid.getSensorStats().get(-10000);
        if (sensor == null) {
            return 0L;
        }
        return sensor.getSensorTime().getTotalTimeLocked(j, i) / 1000;
    }

    public final double getAverageGnssPower(BatteryStats batteryStats, long j, int i) {
        double d = this.mAveragePowerGnssOn;
        if (d != -1.0d) {
            return d;
        }
        long j2 = 0;
        double d2 = 0.0d;
        for (int i2 = 0; i2 < 2; i2++) {
            long gpsSignalQualityTime = batteryStats.getGpsSignalQualityTime(i2, j, i);
            j2 += gpsSignalQualityTime;
            d2 += this.mAveragePowerPerSignalQuality[i2] * gpsSignalQualityTime;
        }
        if (j2 != 0) {
            return d2 / j2;
        }
        return 0.0d;
    }
}
