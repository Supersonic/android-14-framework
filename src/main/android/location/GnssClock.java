package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class GnssClock implements Parcelable {
    public static final Parcelable.Creator<GnssClock> CREATOR = new Parcelable.Creator<GnssClock>() { // from class: android.location.GnssClock.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssClock createFromParcel(Parcel parcel) {
            GnssClock gpsClock = new GnssClock();
            gpsClock.mFlags = parcel.readInt();
            gpsClock.mLeapSecond = parcel.readInt();
            gpsClock.mTimeNanos = parcel.readLong();
            gpsClock.mTimeUncertaintyNanos = parcel.readDouble();
            gpsClock.mFullBiasNanos = parcel.readLong();
            gpsClock.mBiasNanos = parcel.readDouble();
            gpsClock.mBiasUncertaintyNanos = parcel.readDouble();
            gpsClock.mDriftNanosPerSecond = parcel.readDouble();
            gpsClock.mDriftUncertaintyNanosPerSecond = parcel.readDouble();
            gpsClock.mHardwareClockDiscontinuityCount = parcel.readInt();
            gpsClock.mElapsedRealtimeNanos = parcel.readLong();
            gpsClock.mElapsedRealtimeUncertaintyNanos = parcel.readDouble();
            gpsClock.mReferenceConstellationTypeForIsb = parcel.readInt();
            gpsClock.mReferenceCarrierFrequencyHzForIsb = parcel.readDouble();
            gpsClock.mReferenceCodeTypeForIsb = parcel.readString();
            return gpsClock;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssClock[] newArray(int size) {
            return new GnssClock[size];
        }
    };
    private static final int HAS_BIAS = 8;
    private static final int HAS_BIAS_UNCERTAINTY = 16;
    private static final int HAS_DRIFT = 32;
    private static final int HAS_DRIFT_UNCERTAINTY = 64;
    private static final int HAS_ELAPSED_REALTIME_NANOS = 128;
    private static final int HAS_ELAPSED_REALTIME_UNCERTAINTY_NANOS = 256;
    private static final int HAS_FULL_BIAS = 4;
    private static final int HAS_LEAP_SECOND = 1;
    private static final int HAS_NO_FLAGS = 0;
    private static final int HAS_REFERENCE_CARRIER_FREQUENCY_FOR_ISB = 1024;
    private static final int HAS_REFERENCE_CODE_TYPE_FOR_ISB = 2048;
    private static final int HAS_REFERENCE_CONSTELLATION_TYPE_FOR_ISB = 512;
    private static final int HAS_TIME_UNCERTAINTY = 2;
    private double mBiasNanos;
    private double mBiasUncertaintyNanos;
    private double mDriftNanosPerSecond;
    private double mDriftUncertaintyNanosPerSecond;
    private long mElapsedRealtimeNanos;
    private double mElapsedRealtimeUncertaintyNanos;
    private int mFlags;
    private long mFullBiasNanos;
    private int mHardwareClockDiscontinuityCount;
    private int mLeapSecond;
    private double mReferenceCarrierFrequencyHzForIsb;
    private String mReferenceCodeTypeForIsb;
    private int mReferenceConstellationTypeForIsb;
    private long mTimeNanos;
    private double mTimeUncertaintyNanos;

    public GnssClock() {
        initialize();
    }

    public void set(GnssClock clock) {
        this.mFlags = clock.mFlags;
        this.mLeapSecond = clock.mLeapSecond;
        this.mTimeNanos = clock.mTimeNanos;
        this.mTimeUncertaintyNanos = clock.mTimeUncertaintyNanos;
        this.mFullBiasNanos = clock.mFullBiasNanos;
        this.mBiasNanos = clock.mBiasNanos;
        this.mBiasUncertaintyNanos = clock.mBiasUncertaintyNanos;
        this.mDriftNanosPerSecond = clock.mDriftNanosPerSecond;
        this.mDriftUncertaintyNanosPerSecond = clock.mDriftUncertaintyNanosPerSecond;
        this.mHardwareClockDiscontinuityCount = clock.mHardwareClockDiscontinuityCount;
        this.mElapsedRealtimeNanos = clock.mElapsedRealtimeNanos;
        this.mElapsedRealtimeUncertaintyNanos = clock.mElapsedRealtimeUncertaintyNanos;
        this.mReferenceConstellationTypeForIsb = clock.mReferenceConstellationTypeForIsb;
        this.mReferenceCarrierFrequencyHzForIsb = clock.mReferenceCarrierFrequencyHzForIsb;
        this.mReferenceCodeTypeForIsb = clock.mReferenceCodeTypeForIsb;
    }

    public void reset() {
        initialize();
    }

    public boolean hasLeapSecond() {
        return isFlagSet(1);
    }

    public int getLeapSecond() {
        return this.mLeapSecond;
    }

    public void setLeapSecond(int leapSecond) {
        setFlag(1);
        this.mLeapSecond = leapSecond;
    }

    public void resetLeapSecond() {
        resetFlag(1);
        this.mLeapSecond = Integer.MIN_VALUE;
    }

    public long getTimeNanos() {
        return this.mTimeNanos;
    }

    public void setTimeNanos(long timeNanos) {
        this.mTimeNanos = timeNanos;
    }

    public boolean hasTimeUncertaintyNanos() {
        return isFlagSet(2);
    }

    public double getTimeUncertaintyNanos() {
        return this.mTimeUncertaintyNanos;
    }

    public void setTimeUncertaintyNanos(double timeUncertaintyNanos) {
        setFlag(2);
        this.mTimeUncertaintyNanos = timeUncertaintyNanos;
    }

    public void resetTimeUncertaintyNanos() {
        resetFlag(2);
    }

    public boolean hasFullBiasNanos() {
        return isFlagSet(4);
    }

    public long getFullBiasNanos() {
        return this.mFullBiasNanos;
    }

    public void setFullBiasNanos(long value) {
        setFlag(4);
        this.mFullBiasNanos = value;
    }

    public void resetFullBiasNanos() {
        resetFlag(4);
        this.mFullBiasNanos = Long.MIN_VALUE;
    }

    public boolean hasBiasNanos() {
        return isFlagSet(8);
    }

    public double getBiasNanos() {
        return this.mBiasNanos;
    }

    public void setBiasNanos(double biasNanos) {
        setFlag(8);
        this.mBiasNanos = biasNanos;
    }

    public void resetBiasNanos() {
        resetFlag(8);
    }

    public boolean hasBiasUncertaintyNanos() {
        return isFlagSet(16);
    }

    public double getBiasUncertaintyNanos() {
        return this.mBiasUncertaintyNanos;
    }

    public void setBiasUncertaintyNanos(double biasUncertaintyNanos) {
        setFlag(16);
        this.mBiasUncertaintyNanos = biasUncertaintyNanos;
    }

    public void resetBiasUncertaintyNanos() {
        resetFlag(16);
    }

    public boolean hasDriftNanosPerSecond() {
        return isFlagSet(32);
    }

    public double getDriftNanosPerSecond() {
        return this.mDriftNanosPerSecond;
    }

    public void setDriftNanosPerSecond(double driftNanosPerSecond) {
        setFlag(32);
        this.mDriftNanosPerSecond = driftNanosPerSecond;
    }

    public void resetDriftNanosPerSecond() {
        resetFlag(32);
    }

    public boolean hasDriftUncertaintyNanosPerSecond() {
        return isFlagSet(64);
    }

    public double getDriftUncertaintyNanosPerSecond() {
        return this.mDriftUncertaintyNanosPerSecond;
    }

    public void setDriftUncertaintyNanosPerSecond(double driftUncertaintyNanosPerSecond) {
        setFlag(64);
        this.mDriftUncertaintyNanosPerSecond = driftUncertaintyNanosPerSecond;
    }

    public void resetDriftUncertaintyNanosPerSecond() {
        resetFlag(64);
    }

    public boolean hasElapsedRealtimeNanos() {
        return isFlagSet(128);
    }

    public long getElapsedRealtimeNanos() {
        return this.mElapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        setFlag(128);
        this.mElapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public void resetElapsedRealtimeNanos() {
        resetFlag(128);
        this.mElapsedRealtimeNanos = 0L;
    }

    public boolean hasElapsedRealtimeUncertaintyNanos() {
        return isFlagSet(256);
    }

    public double getElapsedRealtimeUncertaintyNanos() {
        return this.mElapsedRealtimeUncertaintyNanos;
    }

    public void setElapsedRealtimeUncertaintyNanos(double elapsedRealtimeUncertaintyNanos) {
        setFlag(256);
        this.mElapsedRealtimeUncertaintyNanos = elapsedRealtimeUncertaintyNanos;
    }

    public void resetElapsedRealtimeUncertaintyNanos() {
        resetFlag(256);
    }

    public boolean hasReferenceConstellationTypeForIsb() {
        return isFlagSet(512);
    }

    public int getReferenceConstellationTypeForIsb() {
        return this.mReferenceConstellationTypeForIsb;
    }

    public void setReferenceConstellationTypeForIsb(int value) {
        setFlag(512);
        this.mReferenceConstellationTypeForIsb = value;
    }

    public void resetReferenceConstellationTypeForIsb() {
        resetFlag(512);
        this.mReferenceConstellationTypeForIsb = 0;
    }

    public boolean hasReferenceCarrierFrequencyHzForIsb() {
        return isFlagSet(1024);
    }

    public double getReferenceCarrierFrequencyHzForIsb() {
        return this.mReferenceCarrierFrequencyHzForIsb;
    }

    public void setReferenceCarrierFrequencyHzForIsb(double value) {
        setFlag(1024);
        this.mReferenceCarrierFrequencyHzForIsb = value;
    }

    public void resetReferenceCarrierFrequencyHzForIsb() {
        resetFlag(1024);
    }

    public boolean hasReferenceCodeTypeForIsb() {
        return isFlagSet(2048);
    }

    public String getReferenceCodeTypeForIsb() {
        return this.mReferenceCodeTypeForIsb;
    }

    public void setReferenceCodeTypeForIsb(String codeType) {
        setFlag(2048);
        this.mReferenceCodeTypeForIsb = codeType;
    }

    public void resetReferenceCodeTypeForIsb() {
        resetFlag(2048);
        this.mReferenceCodeTypeForIsb = "UNKNOWN";
    }

    public int getHardwareClockDiscontinuityCount() {
        return this.mHardwareClockDiscontinuityCount;
    }

    public void setHardwareClockDiscontinuityCount(int value) {
        this.mHardwareClockDiscontinuityCount = value;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mFlags);
        parcel.writeInt(this.mLeapSecond);
        parcel.writeLong(this.mTimeNanos);
        parcel.writeDouble(this.mTimeUncertaintyNanos);
        parcel.writeLong(this.mFullBiasNanos);
        parcel.writeDouble(this.mBiasNanos);
        parcel.writeDouble(this.mBiasUncertaintyNanos);
        parcel.writeDouble(this.mDriftNanosPerSecond);
        parcel.writeDouble(this.mDriftUncertaintyNanosPerSecond);
        parcel.writeInt(this.mHardwareClockDiscontinuityCount);
        parcel.writeLong(this.mElapsedRealtimeNanos);
        parcel.writeDouble(this.mElapsedRealtimeUncertaintyNanos);
        parcel.writeInt(this.mReferenceConstellationTypeForIsb);
        parcel.writeDouble(this.mReferenceCarrierFrequencyHzForIsb);
        parcel.writeString(this.mReferenceCodeTypeForIsb);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GnssClock:\n");
        if (hasLeapSecond()) {
            builder.append(String.format("   %-15s = %s\n", "LeapSecond", Integer.valueOf(this.mLeapSecond)));
        }
        Object[] objArr = new Object[4];
        objArr[0] = "TimeNanos";
        objArr[1] = Long.valueOf(this.mTimeNanos);
        objArr[2] = "TimeUncertaintyNanos";
        objArr[3] = hasTimeUncertaintyNanos() ? Double.valueOf(this.mTimeUncertaintyNanos) : null;
        builder.append(String.format("   %-15s = %-25s   %-26s = %s\n", objArr));
        if (hasFullBiasNanos()) {
            builder.append(String.format("   %-15s = %s\n", "FullBiasNanos", Long.valueOf(this.mFullBiasNanos)));
        }
        if (hasBiasNanos() || hasBiasUncertaintyNanos()) {
            Object[] objArr2 = new Object[4];
            objArr2[0] = "BiasNanos";
            objArr2[1] = hasBiasNanos() ? Double.valueOf(this.mBiasNanos) : null;
            objArr2[2] = "BiasUncertaintyNanos";
            objArr2[3] = hasBiasUncertaintyNanos() ? Double.valueOf(this.mBiasUncertaintyNanos) : null;
            builder.append(String.format("   %-15s = %-25s   %-26s = %s\n", objArr2));
        }
        if (hasDriftNanosPerSecond() || hasDriftUncertaintyNanosPerSecond()) {
            Object[] objArr3 = new Object[4];
            objArr3[0] = "DriftNanosPerSecond";
            objArr3[1] = hasDriftNanosPerSecond() ? Double.valueOf(this.mDriftNanosPerSecond) : null;
            objArr3[2] = "DriftUncertaintyNanosPerSecond";
            objArr3[3] = hasDriftUncertaintyNanosPerSecond() ? Double.valueOf(this.mDriftUncertaintyNanosPerSecond) : null;
            builder.append(String.format("   %-15s = %-25s   %-26s = %s\n", objArr3));
        }
        builder.append(String.format("   %-15s = %s\n", "HardwareClockDiscontinuityCount", Integer.valueOf(this.mHardwareClockDiscontinuityCount)));
        if (hasElapsedRealtimeNanos() || hasElapsedRealtimeUncertaintyNanos()) {
            Object[] objArr4 = new Object[4];
            objArr4[0] = "ElapsedRealtimeNanos";
            objArr4[1] = hasElapsedRealtimeNanos() ? Long.valueOf(this.mElapsedRealtimeNanos) : null;
            objArr4[2] = "ElapsedRealtimeUncertaintyNanos";
            objArr4[3] = hasElapsedRealtimeUncertaintyNanos() ? Double.valueOf(this.mElapsedRealtimeUncertaintyNanos) : null;
            builder.append(String.format("   %-15s = %-25s   %-26s = %s\n", objArr4));
        }
        if (hasReferenceConstellationTypeForIsb()) {
            builder.append(String.format("   %-15s = %s\n", "ReferenceConstellationTypeForIsb", Integer.valueOf(this.mReferenceConstellationTypeForIsb)));
        }
        if (hasReferenceCarrierFrequencyHzForIsb()) {
            builder.append(String.format("   %-15s = %s\n", "ReferenceCarrierFrequencyHzForIsb", Double.valueOf(this.mReferenceCarrierFrequencyHzForIsb)));
        }
        if (hasReferenceCodeTypeForIsb()) {
            builder.append(String.format("   %-15s = %s\n", "ReferenceCodeTypeForIsb", this.mReferenceCodeTypeForIsb));
        }
        return builder.toString();
    }

    private void initialize() {
        this.mFlags = 0;
        resetLeapSecond();
        setTimeNanos(Long.MIN_VALUE);
        resetTimeUncertaintyNanos();
        resetFullBiasNanos();
        resetBiasNanos();
        resetBiasUncertaintyNanos();
        resetDriftNanosPerSecond();
        resetDriftUncertaintyNanosPerSecond();
        setHardwareClockDiscontinuityCount(Integer.MIN_VALUE);
        resetElapsedRealtimeNanos();
        resetElapsedRealtimeUncertaintyNanos();
        resetReferenceConstellationTypeForIsb();
        resetReferenceCarrierFrequencyHzForIsb();
        resetReferenceCodeTypeForIsb();
    }

    private void setFlag(int flag) {
        this.mFlags |= flag;
    }

    private void resetFlag(int flag) {
        this.mFlags &= ~flag;
    }

    private boolean isFlagSet(int flag) {
        return (this.mFlags & flag) == flag;
    }
}
