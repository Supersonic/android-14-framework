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
public class WifiPowerCalculator extends PowerCalculator {
    public static final BatteryConsumer.Key[] UNINITIALIZED_KEYS = new BatteryConsumer.Key[0];
    public final UsageBasedPowerEstimator mBatchScanPowerEstimator;
    public final boolean mHasWifiPowerController;
    public final UsageBasedPowerEstimator mIdlePowerEstimator;
    public final UsageBasedPowerEstimator mPowerOnPowerEstimator;
    public final UsageBasedPowerEstimator mRxPowerEstimator;
    public final UsageBasedPowerEstimator mScanPowerEstimator;
    public final UsageBasedPowerEstimator mTxPowerEstimator;
    public final double mWifiPowerPerPacket;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 11;
    }

    /* loaded from: classes2.dex */
    public static class PowerDurationAndTraffic {
        public long durationMs;
        public BatteryConsumer.Key[] keys;
        public double powerMah;
        public double[] powerPerKeyMah;
        public long wifiRxBytes;
        public long wifiRxPackets;
        public long wifiTxBytes;
        public long wifiTxPackets;

        public PowerDurationAndTraffic() {
        }
    }

    public WifiPowerCalculator(PowerProfile powerProfile) {
        this.mPowerOnPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.on"));
        this.mScanPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.scan"));
        this.mBatchScanPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.batchedscan"));
        UsageBasedPowerEstimator usageBasedPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.controller.idle"));
        this.mIdlePowerEstimator = usageBasedPowerEstimator;
        UsageBasedPowerEstimator usageBasedPowerEstimator2 = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.controller.tx"));
        this.mTxPowerEstimator = usageBasedPowerEstimator2;
        UsageBasedPowerEstimator usageBasedPowerEstimator3 = new UsageBasedPowerEstimator(powerProfile.getAveragePower("wifi.controller.rx"));
        this.mRxPowerEstimator = usageBasedPowerEstimator3;
        this.mWifiPowerPerPacket = getWifiPowerPerPacket(powerProfile);
        this.mHasWifiPowerController = usageBasedPowerEstimator.isSupported() && usageBasedPowerEstimator2.isSupported() && usageBasedPowerEstimator3.isSupported();
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        BatteryUsageStatsQuery batteryUsageStatsQuery2 = batteryUsageStatsQuery;
        BatteryConsumer.Key[] keyArr = UNINITIALIZED_KEYS;
        PowerDurationAndTraffic powerDurationAndTraffic = new PowerDurationAndTraffic();
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        int size = uidBatteryConsumerBuilders.size() - 1;
        long j3 = 0;
        double d = 0.0d;
        while (size >= 0) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            if (keyArr == UNINITIALIZED_KEYS) {
                if (batteryUsageStatsQuery.isProcessStateDataNeeded()) {
                    keyArr = builder2.getKeys(11);
                    powerDurationAndTraffic.keys = keyArr;
                    powerDurationAndTraffic.powerPerKeyMah = new double[keyArr.length];
                } else {
                    keyArr = null;
                }
            }
            long wifiEnergyConsumptionUC = builder2.getBatteryStatsUid().getWifiEnergyConsumptionUC();
            int powerModel = PowerCalculator.getPowerModel(wifiEnergyConsumptionUC, batteryUsageStatsQuery2);
            double d2 = d;
            int i = size;
            calculateApp(powerDurationAndTraffic, builder2.getBatteryStatsUid(), powerModel, j, 0, batteryStats.hasWifiActivityReporting(), wifiEnergyConsumptionUC);
            if (builder2.isVirtualUid()) {
                d = d2;
            } else {
                j3 += powerDurationAndTraffic.durationMs;
                d = d2 + powerDurationAndTraffic.powerMah;
            }
            builder2.setUsageDurationMillis(11, powerDurationAndTraffic.durationMs);
            builder2.setConsumedPower(11, powerDurationAndTraffic.powerMah, powerModel);
            if (batteryUsageStatsQuery.isProcessStateDataNeeded() && keyArr != null) {
                for (int i2 = 0; i2 < keyArr.length; i2++) {
                    BatteryConsumer.Key key = keyArr[i2];
                    if (key.processState != 0) {
                        builder2.setConsumedPower(key, powerDurationAndTraffic.powerPerKeyMah[i2], powerModel);
                    }
                }
            }
            size = i - 1;
            batteryUsageStatsQuery2 = batteryUsageStatsQuery;
        }
        double d3 = d;
        long wifiEnergyConsumptionUC2 = batteryStats.getWifiEnergyConsumptionUC();
        int powerModel2 = PowerCalculator.getPowerModel(wifiEnergyConsumptionUC2, batteryUsageStatsQuery);
        calculateRemaining(powerDurationAndTraffic, powerModel2, batteryStats, j, 0, batteryStats.hasWifiActivityReporting(), j3, d3, wifiEnergyConsumptionUC2);
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(11, powerDurationAndTraffic.durationMs).setConsumedPower(11, d3 + powerDurationAndTraffic.powerMah, powerModel2);
        builder.getAggregateBatteryConsumerBuilder(1).setConsumedPower(11, d3, powerModel2);
    }

    public final void calculateApp(PowerDurationAndTraffic powerDurationAndTraffic, BatteryStats.Uid uid, int i, long j, int i2, boolean z, long j2) {
        powerDurationAndTraffic.wifiRxPackets = uid.getNetworkActivityPackets(2, i2);
        powerDurationAndTraffic.wifiTxPackets = uid.getNetworkActivityPackets(3, i2);
        powerDurationAndTraffic.wifiRxBytes = uid.getNetworkActivityBytes(2, i2);
        powerDurationAndTraffic.wifiTxBytes = uid.getNetworkActivityBytes(3, i2);
        if (z && this.mHasWifiPowerController) {
            BatteryStats.ControllerActivityCounter wifiControllerActivity = uid.getWifiControllerActivity();
            if (wifiControllerActivity != null) {
                BatteryStats.LongCounter rxTimeCounter = wifiControllerActivity.getRxTimeCounter();
                BatteryStats.LongCounter longCounter = wifiControllerActivity.getTxTimeCounters()[0];
                BatteryStats.LongCounter idleTimeCounter = wifiControllerActivity.getIdleTimeCounter();
                long countLocked = rxTimeCounter.getCountLocked(i2);
                long countLocked2 = longCounter.getCountLocked(i2);
                long countLocked3 = idleTimeCounter.getCountLocked(i2);
                powerDurationAndTraffic.durationMs = countLocked3 + countLocked + countLocked2;
                if (i == 1) {
                    powerDurationAndTraffic.powerMah = calcPowerFromControllerDataMah(countLocked, countLocked2, countLocked3);
                } else {
                    powerDurationAndTraffic.powerMah = PowerCalculator.uCtoMah(j2);
                }
                if (powerDurationAndTraffic.keys == null) {
                    return;
                }
                int i3 = 0;
                while (true) {
                    BatteryConsumer.Key[] keyArr = powerDurationAndTraffic.keys;
                    if (i3 >= keyArr.length) {
                        return;
                    }
                    int i4 = keyArr[i3].processState;
                    if (i4 != 0) {
                        if (i == 1) {
                            powerDurationAndTraffic.powerPerKeyMah[i3] = calcPowerFromControllerDataMah(rxTimeCounter.getCountForProcessState(i4), longCounter.getCountForProcessState(i4), idleTimeCounter.getCountForProcessState(i4));
                        } else {
                            powerDurationAndTraffic.powerPerKeyMah[i3] = PowerCalculator.uCtoMah(uid.getWifiEnergyConsumptionUC(i4));
                        }
                    }
                    i3++;
                }
            } else {
                powerDurationAndTraffic.durationMs = 0L;
                powerDurationAndTraffic.powerMah = 0.0d;
                double[] dArr = powerDurationAndTraffic.powerPerKeyMah;
                if (dArr != null) {
                    Arrays.fill(dArr, 0.0d);
                }
            }
        } else {
            long wifiRunningTime = uid.getWifiRunningTime(j, i2) / 1000;
            powerDurationAndTraffic.durationMs = wifiRunningTime;
            if (i == 1) {
                long wifiScanTime = uid.getWifiScanTime(j, i2) / 1000;
                long j3 = 0;
                for (int i5 = 0; i5 < 5; i5++) {
                    j3 += uid.getWifiBatchedScanTime(i5, j, i2) / 1000;
                }
                powerDurationAndTraffic.powerMah = calcPowerWithoutControllerDataMah(powerDurationAndTraffic.wifiRxPackets, powerDurationAndTraffic.wifiTxPackets, wifiRunningTime, wifiScanTime, j3);
            } else {
                powerDurationAndTraffic.powerMah = PowerCalculator.uCtoMah(j2);
            }
            double[] dArr2 = powerDurationAndTraffic.powerPerKeyMah;
            if (dArr2 != null) {
                Arrays.fill(dArr2, 0.0d);
            }
        }
    }

    public final void calculateRemaining(PowerDurationAndTraffic powerDurationAndTraffic, int i, BatteryStats batteryStats, long j, int i2, boolean z, long j2, double d, long j3) {
        long j4;
        double uCtoMah = i == 2 ? PowerCalculator.uCtoMah(j3) : 0.0d;
        if (z && this.mHasWifiPowerController) {
            BatteryStats.ControllerActivityCounter wifiControllerActivity = batteryStats.getWifiControllerActivity();
            long countLocked = wifiControllerActivity.getIdleTimeCounter().getCountLocked(i2);
            long countLocked2 = wifiControllerActivity.getTxTimeCounters()[0].getCountLocked(i2);
            long countLocked3 = wifiControllerActivity.getRxTimeCounter().getCountLocked(i2);
            j4 = countLocked + countLocked3 + countLocked2;
            if (i == 1) {
                double countLocked4 = wifiControllerActivity.getPowerCounter().getCountLocked(i2) / 3600000.0d;
                uCtoMah = countLocked4 == 0.0d ? calcPowerFromControllerDataMah(countLocked3, countLocked2, countLocked) : countLocked4;
            }
        } else {
            long globalWifiRunningTime = batteryStats.getGlobalWifiRunningTime(j, i2) / 1000;
            if (i == 1) {
                uCtoMah = calcGlobalPowerWithoutControllerDataMah(globalWifiRunningTime);
            }
            j4 = globalWifiRunningTime;
        }
        powerDurationAndTraffic.durationMs = Math.max(0L, j4 - j2);
        powerDurationAndTraffic.powerMah = Math.max(0.0d, uCtoMah - d);
    }

    public double calcPowerFromControllerDataMah(long j, long j2, long j3) {
        return this.mRxPowerEstimator.calculatePower(j) + this.mTxPowerEstimator.calculatePower(j2) + this.mIdlePowerEstimator.calculatePower(j3);
    }

    public double calcPowerWithoutControllerDataMah(long j, long j2, long j3, long j4, long j5) {
        return ((j + j2) * this.mWifiPowerPerPacket) + this.mPowerOnPowerEstimator.calculatePower(j3) + this.mScanPowerEstimator.calculatePower(j4) + this.mBatchScanPowerEstimator.calculatePower(j5);
    }

    public double calcGlobalPowerWithoutControllerDataMah(long j) {
        return this.mPowerOnPowerEstimator.calculatePower(j);
    }

    public static double getWifiPowerPerPacket(PowerProfile powerProfile) {
        return (powerProfile.getAveragePower("wifi.active") / 3600.0d) / 61.03515625d;
    }
}
