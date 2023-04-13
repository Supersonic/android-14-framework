package com.android.internal.telephony.metrics;

import android.os.BatteryStatsManager;
import android.os.connectivity.CellularBatteryStats;
import android.telephony.CellSignalStrength;
import android.telephony.ModemActivityInfo;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.nano.TelephonyProto$ModemPowerStats;
import java.util.ArrayList;
import java.util.function.ToLongFunction;
/* loaded from: classes.dex */
public class ModemPowerMetrics {
    private static final int DATA_CONNECTION_EMERGENCY_SERVICE;
    private static final int DATA_CONNECTION_OTHER;
    private static final int NUM_DATA_CONNECTION_TYPES;
    private BatteryStatsManager mBatteryStatsManager;

    static {
        int length = TelephonyManager.getAllNetworkTypes().length + 1;
        DATA_CONNECTION_EMERGENCY_SERVICE = length;
        int i = length + 1;
        DATA_CONNECTION_OTHER = i;
        NUM_DATA_CONNECTION_TYPES = i + 1;
    }

    public ModemPowerMetrics(BatteryStatsManager batteryStatsManager) {
        this.mBatteryStatsManager = batteryStatsManager;
    }

    public TelephonyProto$ModemPowerStats buildProto() {
        TelephonyProto$ModemPowerStats telephonyProto$ModemPowerStats = new TelephonyProto$ModemPowerStats();
        CellularBatteryStats stats = getStats();
        if (stats != null) {
            telephonyProto$ModemPowerStats.loggingDurationMs = stats.getLoggingDurationMillis();
            telephonyProto$ModemPowerStats.energyConsumedMah = stats.getEnergyConsumedMaMillis() / 3600000.0d;
            telephonyProto$ModemPowerStats.numPacketsTx = stats.getNumPacketsTx();
            telephonyProto$ModemPowerStats.cellularKernelActiveTimeMs = stats.getKernelActiveTimeMillis();
            long timeInRxSignalStrengthLevelMicros = stats.getTimeInRxSignalStrengthLevelMicros(0);
            if (timeInRxSignalStrengthLevelMicros >= 0) {
                telephonyProto$ModemPowerStats.timeInVeryPoorRxSignalLevelMs = timeInRxSignalStrengthLevelMicros;
            }
            telephonyProto$ModemPowerStats.sleepTimeMs = stats.getSleepTimeMillis();
            telephonyProto$ModemPowerStats.idleTimeMs = stats.getIdleTimeMillis();
            telephonyProto$ModemPowerStats.rxTimeMs = stats.getRxTimeMillis();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < ModemActivityInfo.getNumTxPowerLevels(); i++) {
                long txTimeMillis = stats.getTxTimeMillis(i);
                if (txTimeMillis >= 0) {
                    arrayList.add(Long.valueOf(txTimeMillis));
                }
            }
            telephonyProto$ModemPowerStats.txTimeMs = arrayList.stream().mapToLong(new ToLongFunction() { // from class: com.android.internal.telephony.metrics.ModemPowerMetrics$$ExternalSyntheticLambda0
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((Long) obj).longValue();
                }
            }).toArray();
            telephonyProto$ModemPowerStats.numBytesTx = stats.getNumBytesTx();
            telephonyProto$ModemPowerStats.numPacketsRx = stats.getNumPacketsRx();
            telephonyProto$ModemPowerStats.numBytesRx = stats.getNumBytesRx();
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < NUM_DATA_CONNECTION_TYPES; i2++) {
                long timeInRatMicros = stats.getTimeInRatMicros(i2);
                if (timeInRatMicros >= 0) {
                    arrayList2.add(Long.valueOf(timeInRatMicros));
                }
            }
            telephonyProto$ModemPowerStats.timeInRatMs = arrayList2.stream().mapToLong(new ToLongFunction() { // from class: com.android.internal.telephony.metrics.ModemPowerMetrics$$ExternalSyntheticLambda0
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((Long) obj).longValue();
                }
            }).toArray();
            ArrayList arrayList3 = new ArrayList();
            for (int i3 = 0; i3 < CellSignalStrength.getNumSignalStrengthLevels(); i3++) {
                long timeInRxSignalStrengthLevelMicros2 = stats.getTimeInRxSignalStrengthLevelMicros(i3);
                if (timeInRxSignalStrengthLevelMicros2 >= 0) {
                    arrayList3.add(Long.valueOf(timeInRxSignalStrengthLevelMicros2));
                }
            }
            telephonyProto$ModemPowerStats.timeInRxSignalStrengthLevelMs = arrayList3.stream().mapToLong(new ToLongFunction() { // from class: com.android.internal.telephony.metrics.ModemPowerMetrics$$ExternalSyntheticLambda0
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((Long) obj).longValue();
                }
            }).toArray();
            telephonyProto$ModemPowerStats.monitoredRailEnergyConsumedMah = stats.getMonitoredRailChargeConsumedMaMillis() / 3600000.0d;
        }
        return telephonyProto$ModemPowerStats;
    }

    private CellularBatteryStats getStats() {
        BatteryStatsManager batteryStatsManager = this.mBatteryStatsManager;
        if (batteryStatsManager == null) {
            return null;
        }
        return batteryStatsManager.getCellularBatteryStats();
    }
}
