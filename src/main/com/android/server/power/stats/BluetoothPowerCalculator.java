package com.android.server.power.stats;

import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class BluetoothPowerCalculator extends PowerCalculator {
    public static final BatteryConsumer.Key[] UNINITIALIZED_KEYS = new BatteryConsumer.Key[0];
    public final boolean mHasBluetoothPowerController;
    public final double mIdleMa;
    public final double mRxMa;
    public final double mTxMa;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 2;
    }

    /* loaded from: classes2.dex */
    public static class PowerAndDuration {
        public long durationMs;
        public BatteryConsumer.Key[] keys;
        public double powerMah;
        public double[] powerPerKeyMah;
        public long totalDurationMs;
        public double totalPowerMah;

        public PowerAndDuration() {
        }
    }

    public BluetoothPowerCalculator(PowerProfile powerProfile) {
        double averagePower = powerProfile.getAveragePower("bluetooth.controller.idle");
        this.mIdleMa = averagePower;
        double averagePower2 = powerProfile.getAveragePower("bluetooth.controller.rx");
        this.mRxMa = averagePower2;
        double averagePower3 = powerProfile.getAveragePower("bluetooth.controller.tx");
        this.mTxMa = averagePower3;
        this.mHasBluetoothPowerController = (averagePower == 0.0d || averagePower2 == 0.0d || averagePower3 == 0.0d) ? false : true;
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        if (batteryStats.hasBluetoothActivityReporting()) {
            BatteryConsumer.Key[] keyArr = UNINITIALIZED_KEYS;
            PowerAndDuration powerAndDuration = new PowerAndDuration();
            SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
            for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
                UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
                if (keyArr == UNINITIALIZED_KEYS) {
                    if (batteryUsageStatsQuery.isProcessStateDataNeeded()) {
                        keyArr = builder2.getKeys(2);
                        powerAndDuration.keys = keyArr;
                        powerAndDuration.powerPerKeyMah = new double[keyArr.length];
                    } else {
                        keyArr = null;
                        calculateApp(builder2, powerAndDuration, batteryUsageStatsQuery);
                    }
                }
                calculateApp(builder2, powerAndDuration, batteryUsageStatsQuery);
            }
            long bluetoothEnergyConsumptionUC = batteryStats.getBluetoothEnergyConsumptionUC();
            int powerModel = PowerCalculator.getPowerModel(bluetoothEnergyConsumptionUC, batteryUsageStatsQuery);
            calculatePowerAndDuration(null, powerModel, bluetoothEnergyConsumptionUC, batteryStats.getBluetoothControllerActivity(), batteryUsageStatsQuery.shouldForceUsePowerProfileModel(), powerAndDuration);
            Math.max(0L, powerAndDuration.durationMs - powerAndDuration.totalDurationMs);
            builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(2, powerAndDuration.durationMs).setConsumedPower(2, Math.max(powerAndDuration.powerMah, powerAndDuration.totalPowerMah), powerModel);
            builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(2, powerAndDuration.totalDurationMs).setConsumedPower(2, powerAndDuration.totalPowerMah, powerModel);
        }
    }

    public final void calculateApp(UidBatteryConsumer.Builder builder, PowerAndDuration powerAndDuration, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        long bluetoothEnergyConsumptionUC = builder.getBatteryStatsUid().getBluetoothEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(bluetoothEnergyConsumptionUC, batteryUsageStatsQuery);
        calculatePowerAndDuration(builder.getBatteryStatsUid(), powerModel, bluetoothEnergyConsumptionUC, builder.getBatteryStatsUid().getBluetoothControllerActivity(), batteryUsageStatsQuery.shouldForceUsePowerProfileModel(), powerAndDuration);
        builder.setUsageDurationMillis(2, powerAndDuration.durationMs).setConsumedPower(2, powerAndDuration.powerMah, powerModel);
        if (!builder.isVirtualUid()) {
            powerAndDuration.totalDurationMs += powerAndDuration.durationMs;
            powerAndDuration.totalPowerMah += powerAndDuration.powerMah;
        }
        if (!batteryUsageStatsQuery.isProcessStateDataNeeded() || powerAndDuration.keys == null) {
            return;
        }
        int i = 0;
        while (true) {
            BatteryConsumer.Key[] keyArr = powerAndDuration.keys;
            if (i >= keyArr.length) {
                return;
            }
            BatteryConsumer.Key key = keyArr[i];
            if (key.processState != 0) {
                builder.setConsumedPower(key, powerAndDuration.powerPerKeyMah[i], powerModel);
            }
            i++;
        }
    }

    public final void calculatePowerAndDuration(BatteryStats.Uid uid, int i, long j, BatteryStats.ControllerActivityCounter controllerActivityCounter, boolean z, PowerAndDuration powerAndDuration) {
        if (controllerActivityCounter == null) {
            powerAndDuration.durationMs = 0L;
            powerAndDuration.powerMah = 0.0d;
            double[] dArr = powerAndDuration.powerPerKeyMah;
            if (dArr != null) {
                Arrays.fill(dArr, 0.0d);
                return;
            }
            return;
        }
        BatteryStats.LongCounter idleTimeCounter = controllerActivityCounter.getIdleTimeCounter();
        BatteryStats.LongCounter rxTimeCounter = controllerActivityCounter.getRxTimeCounter();
        int i2 = 0;
        BatteryStats.LongCounter longCounter = controllerActivityCounter.getTxTimeCounters()[0];
        long countLocked = idleTimeCounter.getCountLocked(0);
        long countLocked2 = rxTimeCounter.getCountLocked(0);
        long countLocked3 = longCounter.getCountLocked(0);
        powerAndDuration.durationMs = countLocked + countLocked2 + countLocked3;
        if (i == 2) {
            powerAndDuration.powerMah = PowerCalculator.uCtoMah(j);
            if (uid == null || powerAndDuration.keys == null) {
                return;
            }
            while (true) {
                BatteryConsumer.Key[] keyArr = powerAndDuration.keys;
                if (i2 >= keyArr.length) {
                    return;
                }
                int i3 = keyArr[i2].processState;
                if (i3 != 0) {
                    powerAndDuration.powerPerKeyMah[i2] = PowerCalculator.uCtoMah(uid.getBluetoothEnergyConsumptionUC(i3));
                }
                i2++;
            }
        } else {
            if (!z) {
                double countLocked4 = controllerActivityCounter.getPowerCounter().getCountLocked(0) / 3600000.0d;
                if (countLocked4 != 0.0d) {
                    powerAndDuration.powerMah = countLocked4;
                    double[] dArr2 = powerAndDuration.powerPerKeyMah;
                    if (dArr2 != null) {
                        Arrays.fill(dArr2, 0.0d);
                        return;
                    }
                    return;
                }
            }
            if (this.mHasBluetoothPowerController) {
                powerAndDuration.powerMah = calculatePowerMah(countLocked2, countLocked3, countLocked);
                if (powerAndDuration.keys == null) {
                    return;
                }
                int i4 = 0;
                while (true) {
                    BatteryConsumer.Key[] keyArr2 = powerAndDuration.keys;
                    if (i4 >= keyArr2.length) {
                        return;
                    }
                    int i5 = keyArr2[i4].processState;
                    if (i5 != 0) {
                        powerAndDuration.powerPerKeyMah[i4] = calculatePowerMah(rxTimeCounter.getCountForProcessState(i5), longCounter.getCountForProcessState(i5), idleTimeCounter.getCountForProcessState(i5));
                    }
                    i4++;
                }
            } else {
                powerAndDuration.powerMah = 0.0d;
                double[] dArr3 = powerAndDuration.powerPerKeyMah;
                if (dArr3 != null) {
                    Arrays.fill(dArr3, 0.0d);
                }
            }
        }
    }

    public double calculatePowerMah(long j, long j2, long j3) {
        return (((j3 * this.mIdleMa) + (j * this.mRxMa)) + (j2 * this.mTxMa)) / 3600000.0d;
    }
}
