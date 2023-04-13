package android.p008os.connectivity;

import android.annotation.SystemApi;
import android.p008os.BatteryStats;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.CellSignalStrength;
import android.telephony.ModemActivityInfo;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* renamed from: android.os.connectivity.CellularBatteryStats */
/* loaded from: classes3.dex */
public final class CellularBatteryStats implements Parcelable {
    public static final Parcelable.Creator<CellularBatteryStats> CREATOR = new Parcelable.Creator<CellularBatteryStats>() { // from class: android.os.connectivity.CellularBatteryStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellularBatteryStats createFromParcel(Parcel in) {
            long loggingDurationMs = in.readLong();
            long kernelActiveTimeMs = in.readLong();
            long numPacketsTx = in.readLong();
            long numBytesTx = in.readLong();
            long numPacketsRx = in.readLong();
            long numBytesRx = in.readLong();
            long sleepTimeMs = in.readLong();
            long idleTimeMs = in.readLong();
            long rxTimeMs = in.readLong();
            long energyConsumedMaMs = in.readLong();
            long[] timeInRatMs = in.createLongArray();
            long[] timeInRxSignalStrengthLevelMs = in.createLongArray();
            long[] txTimeMs = in.createLongArray();
            long monitoredRailChargeConsumedMaMs = in.readLong();
            return new CellularBatteryStats(loggingDurationMs, kernelActiveTimeMs, numPacketsTx, numBytesTx, numPacketsRx, numBytesRx, sleepTimeMs, idleTimeMs, rxTimeMs, Long.valueOf(energyConsumedMaMs), timeInRatMs, timeInRxSignalStrengthLevelMs, txTimeMs, monitoredRailChargeConsumedMaMs);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellularBatteryStats[] newArray(int size) {
            return new CellularBatteryStats[size];
        }
    };
    private final long mEnergyConsumedMaMs;
    private final long mIdleTimeMs;
    private final long mKernelActiveTimeMs;
    private final long mLoggingDurationMs;
    private final long mMonitoredRailChargeConsumedMaMs;
    private final long mNumBytesRx;
    private final long mNumBytesTx;
    private final long mNumPacketsRx;
    private final long mNumPacketsTx;
    private final long mRxTimeMs;
    private final long mSleepTimeMs;
    private final long[] mTimeInRatMs;
    private final long[] mTimeInRxSignalStrengthLevelMs;
    private final long[] mTxTimeMs;

    public CellularBatteryStats(long loggingDurationMs, long kernelActiveTimeMs, long numPacketsTx, long numBytesTx, long numPacketsRx, long numBytesRx, long sleepTimeMs, long idleTimeMs, long rxTimeMs, Long energyConsumedMaMs, long[] timeInRatMs, long[] timeInRxSignalStrengthLevelMs, long[] txTimeMs, long monitoredRailChargeConsumedMaMs) {
        this.mLoggingDurationMs = loggingDurationMs;
        this.mKernelActiveTimeMs = kernelActiveTimeMs;
        this.mNumPacketsTx = numPacketsTx;
        this.mNumBytesTx = numBytesTx;
        this.mNumPacketsRx = numPacketsRx;
        this.mNumBytesRx = numBytesRx;
        this.mSleepTimeMs = sleepTimeMs;
        this.mIdleTimeMs = idleTimeMs;
        this.mRxTimeMs = rxTimeMs;
        this.mEnergyConsumedMaMs = energyConsumedMaMs.longValue();
        this.mTimeInRatMs = Arrays.copyOfRange(timeInRatMs, 0, Math.min(timeInRatMs.length, BatteryStats.NUM_DATA_CONNECTION_TYPES));
        this.mTimeInRxSignalStrengthLevelMs = Arrays.copyOfRange(timeInRxSignalStrengthLevelMs, 0, Math.min(timeInRxSignalStrengthLevelMs.length, CellSignalStrength.getNumSignalStrengthLevels()));
        this.mTxTimeMs = Arrays.copyOfRange(txTimeMs, 0, Math.min(txTimeMs.length, ModemActivityInfo.getNumTxPowerLevels()));
        this.mMonitoredRailChargeConsumedMaMs = monitoredRailChargeConsumedMaMs;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mLoggingDurationMs);
        out.writeLong(this.mKernelActiveTimeMs);
        out.writeLong(this.mNumPacketsTx);
        out.writeLong(this.mNumBytesTx);
        out.writeLong(this.mNumPacketsRx);
        out.writeLong(this.mNumBytesRx);
        out.writeLong(this.mSleepTimeMs);
        out.writeLong(this.mIdleTimeMs);
        out.writeLong(this.mRxTimeMs);
        out.writeLong(this.mEnergyConsumedMaMs);
        out.writeLongArray(this.mTimeInRatMs);
        out.writeLongArray(this.mTimeInRxSignalStrengthLevelMs);
        out.writeLongArray(this.mTxTimeMs);
        out.writeLong(this.mMonitoredRailChargeConsumedMaMs);
    }

    public boolean equals(Object other) {
        if (other instanceof CellularBatteryStats) {
            if (other == this) {
                return true;
            }
            CellularBatteryStats otherStats = (CellularBatteryStats) other;
            return this.mLoggingDurationMs == otherStats.mLoggingDurationMs && this.mKernelActiveTimeMs == otherStats.mKernelActiveTimeMs && this.mNumPacketsTx == otherStats.mNumPacketsTx && this.mNumBytesTx == otherStats.mNumBytesTx && this.mNumPacketsRx == otherStats.mNumPacketsRx && this.mNumBytesRx == otherStats.mNumBytesRx && this.mSleepTimeMs == otherStats.mSleepTimeMs && this.mIdleTimeMs == otherStats.mIdleTimeMs && this.mRxTimeMs == otherStats.mRxTimeMs && this.mEnergyConsumedMaMs == otherStats.mEnergyConsumedMaMs && Arrays.equals(this.mTimeInRatMs, otherStats.mTimeInRatMs) && Arrays.equals(this.mTimeInRxSignalStrengthLevelMs, otherStats.mTimeInRxSignalStrengthLevelMs) && Arrays.equals(this.mTxTimeMs, otherStats.mTxTimeMs) && this.mMonitoredRailChargeConsumedMaMs == otherStats.mMonitoredRailChargeConsumedMaMs;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mLoggingDurationMs), Long.valueOf(this.mKernelActiveTimeMs), Long.valueOf(this.mNumPacketsTx), Long.valueOf(this.mNumBytesTx), Long.valueOf(this.mNumPacketsRx), Long.valueOf(this.mNumBytesRx), Long.valueOf(this.mSleepTimeMs), Long.valueOf(this.mIdleTimeMs), Long.valueOf(this.mRxTimeMs), Long.valueOf(this.mEnergyConsumedMaMs), Integer.valueOf(Arrays.hashCode(this.mTimeInRatMs)), Integer.valueOf(Arrays.hashCode(this.mTimeInRxSignalStrengthLevelMs)), Integer.valueOf(Arrays.hashCode(this.mTxTimeMs)), Long.valueOf(this.mMonitoredRailChargeConsumedMaMs));
    }

    public long getLoggingDurationMillis() {
        return this.mLoggingDurationMs;
    }

    public long getKernelActiveTimeMillis() {
        return this.mKernelActiveTimeMs;
    }

    public long getNumPacketsTx() {
        return this.mNumPacketsTx;
    }

    public long getNumBytesTx() {
        return this.mNumBytesTx;
    }

    public long getNumPacketsRx() {
        return this.mNumPacketsRx;
    }

    public long getNumBytesRx() {
        return this.mNumBytesRx;
    }

    public long getSleepTimeMillis() {
        return this.mSleepTimeMs;
    }

    public long getIdleTimeMillis() {
        return this.mIdleTimeMs;
    }

    public long getRxTimeMillis() {
        return this.mRxTimeMs;
    }

    public long getEnergyConsumedMaMillis() {
        return this.mEnergyConsumedMaMs;
    }

    public long getTimeInRatMicros(int networkType) {
        long[] jArr = this.mTimeInRatMs;
        if (networkType >= jArr.length) {
            return -1L;
        }
        return jArr[networkType];
    }

    public long getTimeInRxSignalStrengthLevelMicros(int signalStrengthBin) {
        long[] jArr = this.mTimeInRxSignalStrengthLevelMs;
        if (signalStrengthBin >= jArr.length) {
            return -1L;
        }
        return jArr[signalStrengthBin];
    }

    public long getTxTimeMillis(int level) {
        long[] jArr = this.mTxTimeMs;
        if (level >= jArr.length) {
            return -1L;
        }
        return jArr[level];
    }

    public long getMonitoredRailChargeConsumedMaMillis() {
        return this.mMonitoredRailChargeConsumedMaMs;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
