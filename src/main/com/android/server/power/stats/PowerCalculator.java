package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.SparseArray;
/* loaded from: classes2.dex */
public abstract class PowerCalculator {
    public static double uCtoMah(long j) {
        return j * 2.777777777777778E-7d;
    }

    public void calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
    }

    public abstract boolean isPowerComponentSupported(int i);

    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            calculateApp(builder2, builder2.getBatteryStatsUid(), j, j2, batteryUsageStatsQuery);
        }
    }

    public static int getPowerModel(long j, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        return (j == -1 || batteryUsageStatsQuery.shouldForceUsePowerProfileModel()) ? 1 : 2;
    }
}
