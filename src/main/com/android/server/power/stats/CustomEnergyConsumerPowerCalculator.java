package com.android.server.power.stats;

import android.os.AggregateBatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class CustomEnergyConsumerPowerCalculator extends PowerCalculator {
    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return false;
    }

    public CustomEnergyConsumerPowerCalculator(PowerProfile powerProfile) {
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        double[] dArr = null;
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            dArr = calculateApp(builder2, builder2.getBatteryStatsUid(), dArr);
        }
        double[] uCtoMah = uCtoMah(batteryStats.getCustomEnergyConsumerBatteryConsumptionUC());
        if (uCtoMah != null) {
            AggregateBatteryConsumer.Builder aggregateBatteryConsumerBuilder = builder.getAggregateBatteryConsumerBuilder(0);
            for (int i = 0; i < uCtoMah.length; i++) {
                aggregateBatteryConsumerBuilder.setConsumedPowerForCustomComponent(i + 1000, uCtoMah[i]);
            }
        }
        if (dArr != null) {
            AggregateBatteryConsumer.Builder aggregateBatteryConsumerBuilder2 = builder.getAggregateBatteryConsumerBuilder(1);
            for (int i2 = 0; i2 < dArr.length; i2++) {
                aggregateBatteryConsumerBuilder2.setConsumedPowerForCustomComponent(i2 + 1000, dArr[i2]);
            }
        }
    }

    public final double[] calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, double[] dArr) {
        double[] uCtoMah = uCtoMah(uid.getCustomEnergyConsumerBatteryConsumptionUC());
        if (uCtoMah != null) {
            if (dArr == null) {
                dArr = new double[uCtoMah.length];
            } else if (dArr.length != uCtoMah.length) {
                Slog.wtf("CustomEnergyCsmrPowerCalc", "Number of custom energy components is not the same for all apps: " + dArr.length + ", " + uCtoMah.length);
                dArr = Arrays.copyOf(dArr, uCtoMah.length);
            }
            for (int i = 0; i < uCtoMah.length; i++) {
                builder.setConsumedPowerForCustomComponent(i + 1000, uCtoMah[i]);
                if (!builder.isVirtualUid()) {
                    dArr[i] = dArr[i] + uCtoMah[i];
                }
            }
            return dArr;
        }
        return null;
    }

    public final double[] uCtoMah(long[] jArr) {
        if (jArr == null) {
            return null;
        }
        double[] dArr = new double[jArr.length];
        for (int i = 0; i < jArr.length; i++) {
            dArr[i] = PowerCalculator.uCtoMah(jArr[i]);
        }
        return dArr;
    }
}
