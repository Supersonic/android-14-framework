package android.p008os.connectivity;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* renamed from: android.os.connectivity.WifiBatteryStats */
/* loaded from: classes3.dex */
public final class WifiBatteryStats implements Parcelable {
    public static final Parcelable.Creator<WifiBatteryStats> CREATOR = new Parcelable.Creator<WifiBatteryStats>() { // from class: android.os.connectivity.WifiBatteryStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiBatteryStats createFromParcel(Parcel in) {
            long loggingDurationMillis = in.readLong();
            long kernelActiveTimeMillis = in.readLong();
            long numPacketsTx = in.readLong();
            long numBytesTx = in.readLong();
            long numPacketsRx = in.readLong();
            long numBytesRx = in.readLong();
            long sleepTimeMillis = in.readLong();
            long scanTimeMillis = in.readLong();
            long idleTimeMillis = in.readLong();
            long rxTimeMillis = in.readLong();
            long txTimeMillis = in.readLong();
            long energyConsumedMaMillis = in.readLong();
            long appScanRequestCount = in.readLong();
            long[] timeInStateMillis = in.createLongArray();
            long[] timeInRxSignalStrengthLevelMillis = in.createLongArray();
            long[] timeInSupplicantStateMillis = in.createLongArray();
            long monitoredRailChargeConsumedMaMillis = in.readLong();
            return new WifiBatteryStats(loggingDurationMillis, kernelActiveTimeMillis, numPacketsTx, numBytesTx, numPacketsRx, numBytesRx, sleepTimeMillis, scanTimeMillis, idleTimeMillis, rxTimeMillis, txTimeMillis, energyConsumedMaMillis, appScanRequestCount, timeInStateMillis, timeInRxSignalStrengthLevelMillis, timeInSupplicantStateMillis, monitoredRailChargeConsumedMaMillis);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiBatteryStats[] newArray(int size) {
            return new WifiBatteryStats[size];
        }
    };
    private final long mAppScanRequestCount;
    private final long mEnergyConsumedMaMillis;
    private final long mIdleTimeMillis;
    private final long mKernelActiveTimeMillis;
    private final long mLoggingDurationMillis;
    private final long mMonitoredRailChargeConsumedMaMillis;
    private final long mNumBytesRx;
    private final long mNumBytesTx;
    private final long mNumPacketsRx;
    private final long mNumPacketsTx;
    private final long mRxTimeMillis;
    private final long mScanTimeMillis;
    private final long mSleepTimeMillis;
    private final long[] mTimeInRxSignalStrengthLevelMillis;
    private final long[] mTimeInStateMillis;
    private final long[] mTimeInSupplicantStateMillis;
    private final long mTxTimeMillis;

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mLoggingDurationMillis);
        out.writeLong(this.mKernelActiveTimeMillis);
        out.writeLong(this.mNumPacketsTx);
        out.writeLong(this.mNumBytesTx);
        out.writeLong(this.mNumPacketsRx);
        out.writeLong(this.mNumBytesRx);
        out.writeLong(this.mSleepTimeMillis);
        out.writeLong(this.mScanTimeMillis);
        out.writeLong(this.mIdleTimeMillis);
        out.writeLong(this.mRxTimeMillis);
        out.writeLong(this.mTxTimeMillis);
        out.writeLong(this.mEnergyConsumedMaMillis);
        out.writeLong(this.mAppScanRequestCount);
        out.writeLongArray(this.mTimeInStateMillis);
        out.writeLongArray(this.mTimeInRxSignalStrengthLevelMillis);
        out.writeLongArray(this.mTimeInSupplicantStateMillis);
        out.writeLong(this.mMonitoredRailChargeConsumedMaMillis);
    }

    public boolean equals(Object other) {
        if (other instanceof WifiBatteryStats) {
            if (other == this) {
                return true;
            }
            WifiBatteryStats otherStats = (WifiBatteryStats) other;
            return this.mLoggingDurationMillis == otherStats.mLoggingDurationMillis && this.mKernelActiveTimeMillis == otherStats.mKernelActiveTimeMillis && this.mNumPacketsTx == otherStats.mNumPacketsTx && this.mNumBytesTx == otherStats.mNumBytesTx && this.mNumPacketsRx == otherStats.mNumPacketsRx && this.mNumBytesRx == otherStats.mNumBytesRx && this.mSleepTimeMillis == otherStats.mSleepTimeMillis && this.mScanTimeMillis == otherStats.mScanTimeMillis && this.mIdleTimeMillis == otherStats.mIdleTimeMillis && this.mRxTimeMillis == otherStats.mRxTimeMillis && this.mTxTimeMillis == otherStats.mTxTimeMillis && this.mEnergyConsumedMaMillis == otherStats.mEnergyConsumedMaMillis && this.mAppScanRequestCount == otherStats.mAppScanRequestCount && Arrays.equals(this.mTimeInStateMillis, otherStats.mTimeInStateMillis) && Arrays.equals(this.mTimeInSupplicantStateMillis, otherStats.mTimeInSupplicantStateMillis) && Arrays.equals(this.mTimeInRxSignalStrengthLevelMillis, otherStats.mTimeInRxSignalStrengthLevelMillis) && this.mMonitoredRailChargeConsumedMaMillis == otherStats.mMonitoredRailChargeConsumedMaMillis;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mLoggingDurationMillis), Long.valueOf(this.mKernelActiveTimeMillis), Long.valueOf(this.mNumPacketsTx), Long.valueOf(this.mNumBytesTx), Long.valueOf(this.mNumPacketsRx), Long.valueOf(this.mNumBytesRx), Long.valueOf(this.mSleepTimeMillis), Long.valueOf(this.mScanTimeMillis), Long.valueOf(this.mIdleTimeMillis), Long.valueOf(this.mRxTimeMillis), Long.valueOf(this.mTxTimeMillis), Long.valueOf(this.mEnergyConsumedMaMillis), Long.valueOf(this.mAppScanRequestCount), Integer.valueOf(Arrays.hashCode(this.mTimeInStateMillis)), Integer.valueOf(Arrays.hashCode(this.mTimeInSupplicantStateMillis)), Integer.valueOf(Arrays.hashCode(this.mTimeInRxSignalStrengthLevelMillis)), Long.valueOf(this.mMonitoredRailChargeConsumedMaMillis));
    }

    public WifiBatteryStats(long loggingDurationMillis, long kernelActiveTimeMillis, long numPacketsTx, long numBytesTx, long numPacketsRx, long numBytesRx, long sleepTimeMillis, long scanTimeMillis, long idleTimeMillis, long rxTimeMillis, long txTimeMillis, long energyConsumedMaMillis, long appScanRequestCount, long[] timeInStateMillis, long[] timeInRxSignalStrengthLevelMillis, long[] timeInSupplicantStateMillis, long monitoredRailChargeConsumedMaMillis) {
        this.mLoggingDurationMillis = loggingDurationMillis;
        this.mKernelActiveTimeMillis = kernelActiveTimeMillis;
        this.mNumPacketsTx = numPacketsTx;
        this.mNumBytesTx = numBytesTx;
        this.mNumPacketsRx = numPacketsRx;
        this.mNumBytesRx = numBytesRx;
        this.mSleepTimeMillis = sleepTimeMillis;
        this.mScanTimeMillis = scanTimeMillis;
        this.mIdleTimeMillis = idleTimeMillis;
        this.mRxTimeMillis = rxTimeMillis;
        this.mTxTimeMillis = txTimeMillis;
        this.mEnergyConsumedMaMillis = energyConsumedMaMillis;
        this.mAppScanRequestCount = appScanRequestCount;
        this.mTimeInStateMillis = Arrays.copyOfRange(timeInStateMillis, 0, Math.min(timeInStateMillis.length, 8));
        this.mTimeInRxSignalStrengthLevelMillis = Arrays.copyOfRange(timeInRxSignalStrengthLevelMillis, 0, Math.min(timeInRxSignalStrengthLevelMillis.length, 5));
        this.mTimeInSupplicantStateMillis = Arrays.copyOfRange(timeInSupplicantStateMillis, 0, Math.min(timeInSupplicantStateMillis.length, 13));
        this.mMonitoredRailChargeConsumedMaMillis = monitoredRailChargeConsumedMaMillis;
    }

    public long getLoggingDurationMillis() {
        return this.mLoggingDurationMillis;
    }

    public long getKernelActiveTimeMillis() {
        return this.mKernelActiveTimeMillis;
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
        return this.mSleepTimeMillis;
    }

    public long getScanTimeMillis() {
        return this.mScanTimeMillis;
    }

    public long getIdleTimeMillis() {
        return this.mIdleTimeMillis;
    }

    public long getRxTimeMillis() {
        return this.mRxTimeMillis;
    }

    public long getTxTimeMillis() {
        return this.mTxTimeMillis;
    }

    public long getEnergyConsumedMaMillis() {
        return this.mEnergyConsumedMaMillis;
    }

    public long getAppScanRequestCount() {
        return this.mAppScanRequestCount;
    }

    public long getMonitoredRailChargeConsumedMaMillis() {
        return this.mMonitoredRailChargeConsumedMaMillis;
    }
}
