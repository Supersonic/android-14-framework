package com.android.server.power.stats;

import android.os.AggregateBatteryConsumer;
import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.PowerProfile;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class CpuPowerCalculator extends PowerCalculator {
    public static final BatteryConsumer.Key[] UNINITIALIZED_KEYS = new BatteryConsumer.Key[0];
    public final UsageBasedPowerEstimator mCpuActivePowerEstimator;
    public final int mNumCpuClusters;
    public final UsageBasedPowerEstimator[] mPerClusterPowerEstimators;
    public final UsageBasedPowerEstimator[] mPerCpuFreqPowerEstimators;
    public final UsageBasedPowerEstimator[][] mPerCpuFreqPowerEstimatorsByCluster;

    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return i == 1;
    }

    /* loaded from: classes2.dex */
    public static class Result {
        public long[] cpuFreqTimes;
        public long durationFgMs;
        public long durationMs;
        public String packageWithHighestDrain;
        public double[] perProcStatePowerMah;
        public double powerMah;

        public Result() {
        }
    }

    public CpuPowerCalculator(PowerProfile powerProfile) {
        int i;
        int numCpuClusters = powerProfile.getNumCpuClusters();
        this.mNumCpuClusters = numCpuClusters;
        this.mCpuActivePowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePower("cpu.active"));
        this.mPerClusterPowerEstimators = new UsageBasedPowerEstimator[numCpuClusters];
        for (int i2 = 0; i2 < this.mNumCpuClusters; i2++) {
            this.mPerClusterPowerEstimators[i2] = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForCpuCluster(i2));
        }
        int i3 = 0;
        int i4 = 0;
        while (true) {
            i = this.mNumCpuClusters;
            if (i3 >= i) {
                break;
            }
            i4 += powerProfile.getNumSpeedStepsInCpuCluster(i3);
            i3++;
        }
        this.mPerCpuFreqPowerEstimatorsByCluster = new UsageBasedPowerEstimator[i];
        this.mPerCpuFreqPowerEstimators = new UsageBasedPowerEstimator[i4];
        int i5 = 0;
        for (int i6 = 0; i6 < this.mNumCpuClusters; i6++) {
            int numSpeedStepsInCpuCluster = powerProfile.getNumSpeedStepsInCpuCluster(i6);
            this.mPerCpuFreqPowerEstimatorsByCluster[i6] = new UsageBasedPowerEstimator[numSpeedStepsInCpuCluster];
            int i7 = 0;
            while (i7 < numSpeedStepsInCpuCluster) {
                UsageBasedPowerEstimator usageBasedPowerEstimator = new UsageBasedPowerEstimator(powerProfile.getAveragePowerForCpuCore(i6, i7));
                this.mPerCpuFreqPowerEstimatorsByCluster[i6][i7] = usageBasedPowerEstimator;
                this.mPerCpuFreqPowerEstimators[i5] = usageBasedPowerEstimator;
                i7++;
                i5++;
            }
        }
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        BatteryConsumer.Key[] keyArr = UNINITIALIZED_KEYS;
        Result result = new Result();
        if (batteryUsageStatsQuery.isProcessStateDataNeeded()) {
            result.cpuFreqTimes = new long[batteryStats.getCpuFreqCount()];
        }
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        double d = 0.0d;
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            if (keyArr == UNINITIALIZED_KEYS) {
                keyArr = batteryUsageStatsQuery.isProcessStateDataNeeded() ? builder2.getKeys(1) : null;
            }
            calculateApp(builder2, builder2.getBatteryStatsUid(), batteryUsageStatsQuery, result, keyArr);
            if (!builder2.isVirtualUid()) {
                d += result.powerMah;
            }
        }
        long cpuEnergyConsumptionUC = batteryStats.getCpuEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(cpuEnergyConsumptionUC, batteryUsageStatsQuery);
        builder.getAggregateBatteryConsumerBuilder(1).setConsumedPower(1, d);
        AggregateBatteryConsumer.Builder aggregateBatteryConsumerBuilder = builder.getAggregateBatteryConsumerBuilder(0);
        if (powerModel == 2) {
            d = PowerCalculator.uCtoMah(cpuEnergyConsumptionUC);
        }
        aggregateBatteryConsumerBuilder.setConsumedPower(1, d, powerModel);
    }

    public final void calculateApp(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, BatteryUsageStatsQuery batteryUsageStatsQuery, Result result, BatteryConsumer.Key[] keyArr) {
        long cpuEnergyConsumptionUC = uid.getCpuEnergyConsumptionUC();
        int powerModel = PowerCalculator.getPowerModel(cpuEnergyConsumptionUC, batteryUsageStatsQuery);
        calculatePowerAndDuration(uid, powerModel, cpuEnergyConsumptionUC, 0, result);
        builder.setConsumedPower(1, result.powerMah, powerModel).setUsageDurationMillis(1, result.durationMs).setPackageWithHighestDrain(result.packageWithHighestDrain);
        if (!batteryUsageStatsQuery.isProcessStateDataNeeded() || keyArr == null) {
            return;
        }
        if (powerModel == 1) {
            calculateModeledPowerPerProcessState(builder, uid, keyArr, result);
        } else if (powerModel != 2) {
        } else {
            calculateEnergyConsumptionPerProcessState(builder, uid, keyArr);
        }
    }

    public final void calculateEnergyConsumptionPerProcessState(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, BatteryConsumer.Key[] keyArr) {
        for (BatteryConsumer.Key key : keyArr) {
            int i = key.processState;
            if (i != 0) {
                long cpuEnergyConsumptionUC = uid.getCpuEnergyConsumptionUC(i);
                if (cpuEnergyConsumptionUC != 0) {
                    builder.setConsumedPower(key, PowerCalculator.uCtoMah(cpuEnergyConsumptionUC), 2);
                }
            }
        }
    }

    public final void calculateModeledPowerPerProcessState(UidBatteryConsumer.Builder builder, BatteryStats.Uid uid, BatteryConsumer.Key[] keyArr, Result result) {
        double[] dArr = result.perProcStatePowerMah;
        if (dArr == null) {
            result.perProcStatePowerMah = new double[5];
        } else {
            Arrays.fill(dArr, 0.0d);
        }
        for (int i = 0; i < 7; i++) {
            int mapUidProcessStateToBatteryConsumerProcessState = BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(i);
            if (mapUidProcessStateToBatteryConsumerProcessState != 0 && uid.getCpuFreqTimes(result.cpuFreqTimes, i)) {
                double[] dArr2 = result.perProcStatePowerMah;
                dArr2[mapUidProcessStateToBatteryConsumerProcessState] = dArr2[mapUidProcessStateToBatteryConsumerProcessState] + calculateUidModeledPowerMah(uid, 0L, null, result.cpuFreqTimes);
            }
        }
        for (BatteryConsumer.Key key : keyArr) {
            int i2 = key.processState;
            if (i2 != 0) {
                long cpuActiveTime = uid.getCpuActiveTime(i2);
                builder.setConsumedPower(key, result.perProcStatePowerMah[key.processState] + this.mCpuActivePowerEstimator.calculatePower(cpuActiveTime), 1).setUsageDurationMillis(key, cpuActiveTime);
            }
        }
    }

    public final void calculatePowerAndDuration(BatteryStats.Uid uid, int i, long j, int i2, Result result) {
        double uCtoMah;
        double d;
        int i3 = i2;
        long userCpuTimeUs = (uid.getUserCpuTimeUs(i3) + uid.getSystemCpuTimeUs(i3)) / 1000;
        if (i == 2) {
            uCtoMah = PowerCalculator.uCtoMah(j);
        } else {
            uCtoMah = calculateUidModeledPowerMah(uid, i3);
        }
        ArrayMap processStats = uid.getProcessStats();
        int size = processStats.size();
        double d2 = 0.0d;
        String str = null;
        long j2 = 0;
        int i4 = 0;
        while (i4 < size) {
            BatteryStats.Uid.Proc proc = (BatteryStats.Uid.Proc) processStats.valueAt(i4);
            String str2 = (String) processStats.keyAt(i4);
            j2 += proc.getForegroundTime(i3);
            ArrayMap arrayMap = processStats;
            long userTime = proc.getUserTime(i3) + proc.getSystemTime(i3) + proc.getForegroundTime(i3);
            if (str == null || str.startsWith("*")) {
                d = userTime;
            } else {
                d = userTime;
                if (d2 < d) {
                    if (str2.startsWith("*")) {
                    }
                }
                i4++;
                processStats = arrayMap;
                i3 = i2;
            }
            d2 = d;
            str = str2;
            i4++;
            processStats = arrayMap;
            i3 = i2;
        }
        if (j2 > userCpuTimeUs) {
            userCpuTimeUs = j2;
        }
        result.durationMs = userCpuTimeUs;
        result.durationFgMs = j2;
        result.powerMah = uCtoMah;
        result.packageWithHighestDrain = str;
    }

    public double calculateUidModeledPowerMah(BatteryStats.Uid uid, int i) {
        return calculateUidModeledPowerMah(uid, uid.getCpuActiveTime(), uid.getCpuClusterTimes(), uid.getCpuFreqTimes(i));
    }

    public final double calculateUidModeledPowerMah(BatteryStats.Uid uid, long j, long[] jArr, long[] jArr2) {
        double calculateActiveCpuPowerMah = calculateActiveCpuPowerMah(j);
        if (jArr != null) {
            if (jArr.length == this.mNumCpuClusters) {
                for (int i = 0; i < this.mNumCpuClusters; i++) {
                    calculateActiveCpuPowerMah += this.mPerClusterPowerEstimators[i].calculatePower(jArr[i]);
                }
            } else {
                Log.w("CpuPowerCalculator", "UID " + uid.getUid() + " CPU cluster # mismatch: Power Profile # " + this.mNumCpuClusters + " actual # " + jArr.length);
            }
        }
        if (jArr2 != null) {
            if (jArr2.length == this.mPerCpuFreqPowerEstimators.length) {
                for (int i2 = 0; i2 < jArr2.length; i2++) {
                    calculateActiveCpuPowerMah += this.mPerCpuFreqPowerEstimators[i2].calculatePower(jArr2[i2]);
                }
            } else {
                Log.w("CpuPowerCalculator", "UID " + uid.getUid() + " CPU freq # mismatch: Power Profile # " + this.mPerCpuFreqPowerEstimators.length + " actual # " + jArr2.length);
            }
        }
        return calculateActiveCpuPowerMah;
    }

    public double calculateActiveCpuPowerMah(long j) {
        return this.mCpuActivePowerEstimator.calculatePower(j);
    }

    public double calculatePerCpuClusterPowerMah(int i, long j) {
        return this.mPerClusterPowerEstimators[i].calculatePower(j);
    }

    public double calculatePerCpuFreqPowerMah(int i, int i2, long j) {
        return this.mPerCpuFreqPowerEstimatorsByCluster[i][i2].calculatePower(j);
    }
}
