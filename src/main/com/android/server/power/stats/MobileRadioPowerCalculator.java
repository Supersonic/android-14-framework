package com.android.server.power.stats;

import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.telephony.CellSignalStrength;
import android.util.Log;
import android.util.LongArrayQueue;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class MobileRadioPowerCalculator extends PowerCalculator {
    public static final int NUM_SIGNAL_STRENGTH_LEVELS = CellSignalStrength.getNumSignalStrengthLevels();
    public static final BatteryConsumer.Key[] UNINITIALIZED_KEYS = new BatteryConsumer.Key[0];
    public final UsageBasedPowerEstimator mActivePowerEstimator;
    public final UsageBasedPowerEstimator mIdlePowerEstimator;
    public final UsageBasedPowerEstimator[] mIdlePowerEstimators = new UsageBasedPowerEstimator[NUM_SIGNAL_STRENGTH_LEVELS];
    public final PowerProfile mPowerProfile;
    public final UsageBasedPowerEstimator mScanPowerEstimator;
    public final UsageBasedPowerEstimator mSleepPowerEstimator;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 8;
    }

    /* loaded from: classes2.dex */
    public static class PowerAndDuration {
        public long remainingDurationMs;
        public double remainingPowerMah;
        public long totalAppDurationMs;
        public double totalAppPowerMah;

        public PowerAndDuration() {
        }
    }

    public MobileRadioPowerCalculator(PowerProfile powerProfile) {
        int i;
        this.mPowerProfile = powerProfile;
        double averageBatteryDrainOrDefaultMa = powerProfile.getAverageBatteryDrainOrDefaultMa(4294967296L, Double.NaN);
        if (Double.isNaN(averageBatteryDrainOrDefaultMa)) {
            this.mSleepPowerEstimator = null;
        } else {
            this.mSleepPowerEstimator = new UsageBasedPowerEstimator(averageBatteryDrainOrDefaultMa);
        }
        double averageBatteryDrainOrDefaultMa2 = powerProfile.getAverageBatteryDrainOrDefaultMa(4563402752L, Double.NaN);
        if (Double.isNaN(averageBatteryDrainOrDefaultMa2)) {
            this.mIdlePowerEstimator = null;
        } else {
            this.mIdlePowerEstimator = new UsageBasedPowerEstimator(averageBatteryDrainOrDefaultMa2);
        }
        double averagePowerOrDefault = powerProfile.getAveragePowerOrDefault("radio.active", Double.NaN);
        if (Double.isNaN(averagePowerOrDefault)) {
            double averagePower = powerProfile.getAveragePower("modem.controller.rx") + 0.0d;
            int i2 = 0;
            while (true) {
                if (i2 >= NUM_SIGNAL_STRENGTH_LEVELS) {
                    break;
                }
                averagePower += powerProfile.getAveragePower("modem.controller.tx", i2);
                i2++;
            }
            averagePowerOrDefault = averagePower / (i + 1);
        }
        this.mActivePowerEstimator = new UsageBasedPowerEstimator(averagePowerOrDefault);
        if (!Double.isNaN(powerProfile.getAveragePowerOrDefault("radio.on", Double.NaN))) {
            for (int i3 = 0; i3 < NUM_SIGNAL_STRENGTH_LEVELS; i3++) {
                this.mIdlePowerEstimators[i3] = new UsageBasedPowerEstimator(powerProfile.getAveragePower("radio.on", i3));
            }
        } else {
            double averagePower2 = powerProfile.getAveragePower("modem.controller.idle");
            this.mIdlePowerEstimators[0] = new UsageBasedPowerEstimator((25.0d * averagePower2) / 180.0d);
            for (int i4 = 1; i4 < NUM_SIGNAL_STRENGTH_LEVELS; i4++) {
                this.mIdlePowerEstimators[i4] = new UsageBasedPowerEstimator(Math.max(1.0d, averagePower2 / 256.0d));
            }
        }
        this.mScanPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePowerOrDefault("radio.scanning", 0.0d));
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00d3  */
    @Override // com.android.server.power.stats.PowerCalculator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        double calculateActiveModemPowerMah;
        LongArrayQueue longArrayQueue;
        int i;
        double d;
        ArrayList arrayList;
        long j3;
        BatteryStats.Uid uid;
        PowerAndDuration powerAndDuration;
        double d2;
        int i2;
        BatteryConsumer.Key[] keyArr;
        BatteryStats.Uid uid2;
        BatteryConsumer.Key[] keyArr2;
        ArrayList arrayList2 = null;
        PowerAndDuration powerAndDuration2 = new PowerAndDuration();
        long mobileRadioEnergyConsumptionUC = batteryStats.getMobileRadioEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(mobileRadioEnergyConsumptionUC, batteryUsageStatsQuery);
        if (powerModel == 2) {
            longArrayQueue = null;
            calculateActiveModemPowerMah = Double.NaN;
        } else {
            calculateActiveModemPowerMah = calculateActiveModemPowerMah(batteryStats, j);
            arrayList2 = new ArrayList();
            longArrayQueue = new LongArrayQueue();
        }
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        BatteryConsumer.Key[] keyArr3 = UNINITIALIZED_KEYS;
        int size = uidBatteryConsumerBuilders.size() - 1;
        while (size >= 0) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            SparseArray sparseArray = uidBatteryConsumerBuilders;
            BatteryStats.Uid batteryStatsUid = builder2.getBatteryStatsUid();
            long j4 = mobileRadioEnergyConsumptionUC;
            if (keyArr3 == UNINITIALIZED_KEYS) {
                if (batteryUsageStatsQuery.isProcessStateDataNeeded()) {
                    keyArr3 = builder2.getKeys(8);
                } else {
                    d2 = calculateActiveModemPowerMah;
                    i2 = 0;
                    keyArr3 = null;
                    long calculateDuration = calculateDuration(batteryStatsUid, i2);
                    if (!builder2.isVirtualUid()) {
                        powerAndDuration2.totalAppDurationMs += calculateDuration;
                    }
                    builder2.setUsageDurationMillis(8, calculateDuration);
                    if (powerModel != 2) {
                        long mobileRadioEnergyConsumptionUC2 = batteryStatsUid.getMobileRadioEnergyConsumptionUC();
                        if (mobileRadioEnergyConsumptionUC2 != -1) {
                            double uCtoMah = PowerCalculator.uCtoMah(mobileRadioEnergyConsumptionUC2);
                            if (!builder2.isVirtualUid()) {
                                powerAndDuration2.totalAppPowerMah += uCtoMah;
                            }
                            builder2.setConsumedPower(8, uCtoMah, powerModel);
                            if (batteryUsageStatsQuery.isProcessStateDataNeeded() && keyArr3 != null) {
                                int length = keyArr3.length;
                                int i3 = 0;
                                while (i3 < length) {
                                    BatteryConsumer.Key key = keyArr3[i3];
                                    int i4 = key.processState;
                                    if (i4 == 0) {
                                        uid2 = batteryStatsUid;
                                        keyArr2 = keyArr3;
                                    } else {
                                        uid2 = batteryStatsUid;
                                        keyArr2 = keyArr3;
                                        builder2.setConsumedPower(key, PowerCalculator.uCtoMah(batteryStatsUid.getMobileRadioEnergyConsumptionUC(i4)), powerModel);
                                    }
                                    i3++;
                                    batteryStatsUid = uid2;
                                    keyArr3 = keyArr2;
                                }
                            }
                        }
                        keyArr = keyArr3;
                    } else {
                        keyArr = keyArr3;
                        arrayList2.add(builder2);
                        longArrayQueue.addLast(calculateDuration);
                    }
                    size--;
                    uidBatteryConsumerBuilders = sparseArray;
                    keyArr3 = keyArr;
                    mobileRadioEnergyConsumptionUC = j4;
                    calculateActiveModemPowerMah = d2;
                }
            }
            d2 = calculateActiveModemPowerMah;
            i2 = 0;
            long calculateDuration2 = calculateDuration(batteryStatsUid, i2);
            if (!builder2.isVirtualUid()) {
            }
            builder2.setUsageDurationMillis(8, calculateDuration2);
            if (powerModel != 2) {
            }
            size--;
            uidBatteryConsumerBuilders = sparseArray;
            keyArr3 = keyArr;
            mobileRadioEnergyConsumptionUC = j4;
            calculateActiveModemPowerMah = d2;
        }
        long j5 = mobileRadioEnergyConsumptionUC;
        double d3 = calculateActiveModemPowerMah;
        long mobileRadioActiveTime = batteryStats.getMobileRadioActiveTime(j, 0) / 1000;
        long j6 = powerAndDuration2.totalAppDurationMs;
        if (mobileRadioActiveTime < j6) {
            mobileRadioActiveTime = j6;
        }
        double d4 = 0.0d;
        if (powerModel != 2) {
            int size2 = arrayList2.size();
            int i5 = 0;
            while (i5 < size2) {
                UidBatteryConsumer.Builder builder3 = (UidBatteryConsumer.Builder) arrayList2.get(i5);
                int i6 = size2;
                long j7 = longArrayQueue.get(i5);
                double d5 = mobileRadioActiveTime;
                if (d5 != d4) {
                    d4 = (j7 * d3) / d5;
                }
                if (!builder3.isVirtualUid()) {
                    powerAndDuration2.totalAppPowerMah += d4;
                }
                builder3.setConsumedPower(8, d4, powerModel);
                if (!batteryUsageStatsQuery.isProcessStateDataNeeded() || keyArr3 == null) {
                    arrayList = arrayList2;
                } else {
                    BatteryStats.Uid batteryStatsUid2 = builder3.getBatteryStatsUid();
                    int length2 = keyArr3.length;
                    arrayList = arrayList2;
                    int i7 = 0;
                    while (i7 < length2) {
                        int i8 = length2;
                        BatteryConsumer.Key key2 = keyArr3[i7];
                        LongArrayQueue longArrayQueue2 = longArrayQueue;
                        int i9 = key2.processState;
                        if (i9 == 0) {
                            uid = batteryStatsUid2;
                            powerAndDuration = powerAndDuration2;
                            j3 = mobileRadioActiveTime;
                        } else {
                            j3 = mobileRadioActiveTime;
                            long mobileRadioActiveTimeInProcessState = batteryStatsUid2.getMobileRadioActiveTimeInProcessState(i9) / 1000;
                            uid = batteryStatsUid2;
                            powerAndDuration = powerAndDuration2;
                            double d6 = j7;
                            builder3.setConsumedPower(key2, d6 == 0.0d ? 0.0d : (mobileRadioActiveTimeInProcessState * d4) / d6, powerModel);
                        }
                        i7++;
                        powerAndDuration2 = powerAndDuration;
                        length2 = i8;
                        longArrayQueue = longArrayQueue2;
                        batteryStatsUid2 = uid;
                        mobileRadioActiveTime = j3;
                    }
                }
                i5++;
                size2 = i6;
                powerAndDuration2 = powerAndDuration2;
                arrayList2 = arrayList;
                longArrayQueue = longArrayQueue;
                mobileRadioActiveTime = mobileRadioActiveTime;
                d4 = 0.0d;
            }
        }
        long j8 = mobileRadioActiveTime;
        PowerAndDuration powerAndDuration3 = powerAndDuration2;
        long j9 = j8 - powerAndDuration3.totalAppDurationMs;
        powerAndDuration3.remainingDurationMs = j9;
        if (powerModel == 2) {
            double uCtoMah2 = PowerCalculator.uCtoMah(j5) - powerAndDuration3.totalAppPowerMah;
            powerAndDuration3.remainingPowerMah = uCtoMah2;
            if (uCtoMah2 < 0.0d) {
                powerAndDuration3.remainingPowerMah = 0.0d;
            }
        } else {
            if (j8 != 0) {
                powerAndDuration3.remainingPowerMah += (d3 * j9) / j8;
            }
            BatteryStats.ControllerActivityCounter modemControllerActivity = batteryStats.getModemControllerActivity();
            if (modemControllerActivity != null) {
                i = 0;
                d = calcInactiveStatePowerMah(modemControllerActivity.getSleepTimeCounter().getCountLocked(0), modemControllerActivity.getIdleTimeCounter().getCountLocked(0));
            } else {
                i = 0;
                d = Double.NaN;
            }
            if (Double.isNaN(d)) {
                double calcScanTimePowerMah = calcScanTimePowerMah(batteryStats.getPhoneSignalScanningTime(j, i) / 1000);
                int i10 = i;
                while (i10 < NUM_SIGNAL_STRENGTH_LEVELS) {
                    calcScanTimePowerMah += calcIdlePowerAtSignalStrengthMah(batteryStats.getPhoneSignalStrengthTime(i10, j, i) / 1000, i10);
                    i10++;
                    i = 0;
                }
                d = calcScanTimePowerMah;
            }
            if (!Double.isNaN(d)) {
                powerAndDuration3.remainingPowerMah += d;
            }
        }
        if (powerAndDuration3.remainingPowerMah == 0.0d && powerAndDuration3.totalAppPowerMah == 0.0d) {
            return;
        }
        builder.getAggregateBatteryConsumerBuilder(0).setUsageDurationMillis(8, powerAndDuration3.remainingDurationMs + powerAndDuration3.totalAppDurationMs).setConsumedPower(8, powerAndDuration3.remainingPowerMah + powerAndDuration3.totalAppPowerMah, powerModel);
        builder.getAggregateBatteryConsumerBuilder(1).setUsageDurationMillis(8, powerAndDuration3.totalAppDurationMs).setConsumedPower(8, powerAndDuration3.totalAppPowerMah, powerModel);
    }

    public final long calculateDuration(BatteryStats.Uid uid, int i) {
        return uid.getMobileRadioActiveTime(i) / 1000;
    }

    public final double calculateActiveModemPowerMah(BatteryStats batteryStats, long j) {
        long j2 = j / 1000;
        int numSignalStrengthLevels = CellSignalStrength.getNumSignalStrengthLevels();
        int i = 0;
        boolean z = false;
        int i2 = 0;
        double d = 0.0d;
        while (i2 < 3) {
            int i3 = i2 == 2 ? 5 : 1;
            int i4 = i;
            while (i4 < i3) {
                boolean z2 = z;
                double d2 = d;
                int i5 = i;
                while (i5 < numSignalStrengthLevels) {
                    int i6 = i5;
                    int i7 = i3;
                    int i8 = i;
                    int i9 = i2;
                    long activeTxRadioDurationMs = batteryStats.getActiveTxRadioDurationMs(i2, i4, i6, j2);
                    if (activeTxRadioDurationMs != -1) {
                        double calcTxStatePowerMah = calcTxStatePowerMah(i9, i4, i6, activeTxRadioDurationMs);
                        if (!Double.isNaN(calcTxStatePowerMah)) {
                            d2 += calcTxStatePowerMah;
                            z2 = true;
                        }
                    }
                    i5 = i6 + 1;
                    i = i8;
                    i3 = i7;
                    i2 = i9;
                }
                int i10 = i3;
                int i11 = i;
                int i12 = i2;
                long activeRxRadioDurationMs = batteryStats.getActiveRxRadioDurationMs(i12, i4, j2);
                if (activeRxRadioDurationMs != -1) {
                    double calcRxStatePowerMah = calcRxStatePowerMah(i12, i4, activeRxRadioDurationMs);
                    if (!Double.isNaN(calcRxStatePowerMah)) {
                        d2 += calcRxStatePowerMah;
                        z = true;
                        d = d2;
                        i4++;
                        i2 = i12;
                        i = i11;
                        i3 = i10;
                    }
                }
                z = z2;
                d = d2;
                i4++;
                i2 = i12;
                i = i11;
                i3 = i10;
            }
            i2++;
            i = i;
        }
        int i13 = i;
        if (z) {
            return d;
        }
        long mobileRadioActiveTime = batteryStats.getMobileRadioActiveTime(j, i13) / 1000;
        if (mobileRadioActiveTime > 0) {
            return calcPowerFromRadioActiveDurationMah(mobileRadioActiveTime);
        }
        return 0.0d;
    }

    public static long buildModemPowerProfileKey(int i, int i2, int i3, int i4) {
        long j;
        long j2;
        long j3;
        long j4 = i != -1 ? 4294967296L | i : 4294967296L;
        if (i2 != -1) {
            if (i2 != 0) {
                if (i2 == 1) {
                    j3 = 1048576;
                } else if (i2 != 2) {
                    Log.w("MobRadioPowerCalculator", "Unexpected RadioAccessTechnology : " + i2);
                } else {
                    j3 = 2097152;
                }
                j4 |= j3;
            } else {
                j4 |= 0;
            }
        }
        if (i3 != -1) {
            if (i3 != 0) {
                if (i3 == 1) {
                    j2 = 65536;
                } else if (i3 == 2) {
                    j2 = 131072;
                } else if (i3 == 3) {
                    j2 = 196608;
                } else if (i3 != 4) {
                    Log.w("MobRadioPowerCalculator", "Unexpected NR frequency range : " + i3);
                } else {
                    j2 = 262144;
                }
                j4 |= j2;
            } else {
                j4 |= 0;
            }
        }
        if (i4 != -1) {
            if (i4 != 0) {
                if (i4 == 1) {
                    j = 16777216;
                } else if (i4 == 2) {
                    j = 33554432;
                } else if (i4 == 3) {
                    j = 50331648;
                } else if (i4 != 4) {
                    Log.w("MobRadioPowerCalculator", "Unexpected transmission level : " + i4);
                    return j4;
                } else {
                    j = 67108864;
                }
                return j4 | j;
            }
            return j4 | 0;
        }
        return j4;
    }

    public double calcRxStatePowerMah(int i, int i2, long j) {
        long buildModemPowerProfileKey = buildModemPowerProfileKey(536870912, i, i2, -1);
        double averageBatteryDrainOrDefaultMa = this.mPowerProfile.getAverageBatteryDrainOrDefaultMa(buildModemPowerProfileKey, Double.NaN);
        if (Double.isNaN(averageBatteryDrainOrDefaultMa)) {
            Log.w("MobRadioPowerCalculator", "Unavailable Power Profile constant for key 0x" + Long.toHexString(buildModemPowerProfileKey));
            return Double.NaN;
        }
        return (averageBatteryDrainOrDefaultMa * j) / 3600000.0d;
    }

    public double calcTxStatePowerMah(int i, int i2, int i3, long j) {
        long buildModemPowerProfileKey = buildModemPowerProfileKey(805306368, i, i2, i3);
        double averageBatteryDrainOrDefaultMa = this.mPowerProfile.getAverageBatteryDrainOrDefaultMa(buildModemPowerProfileKey, Double.NaN);
        if (Double.isNaN(averageBatteryDrainOrDefaultMa)) {
            Log.w("MobRadioPowerCalculator", "Unavailable Power Profile constant for key 0x" + Long.toHexString(buildModemPowerProfileKey));
            return Double.NaN;
        }
        return (averageBatteryDrainOrDefaultMa * j) / 3600000.0d;
    }

    public double calcInactiveStatePowerMah(long j, long j2) {
        UsageBasedPowerEstimator usageBasedPowerEstimator = this.mSleepPowerEstimator;
        if (usageBasedPowerEstimator == null || this.mIdlePowerEstimator == null) {
            return Double.NaN;
        }
        return usageBasedPowerEstimator.calculatePower(j) + this.mIdlePowerEstimator.calculatePower(j2);
    }

    public double calcPowerFromRadioActiveDurationMah(long j) {
        return this.mActivePowerEstimator.calculatePower(j);
    }

    public double calcIdlePowerAtSignalStrengthMah(long j, int i) {
        return this.mIdlePowerEstimators[i].calculatePower(j);
    }

    public double calcScanTimePowerMah(long j) {
        return this.mScanPowerEstimator.calculatePower(j);
    }
}
