package android.p008os.connectivity;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
/* renamed from: android.os.connectivity.GpsBatteryStats */
/* loaded from: classes3.dex */
public final class GpsBatteryStats implements Parcelable {
    public static final Parcelable.Creator<GpsBatteryStats> CREATOR = new Parcelable.Creator<GpsBatteryStats>() { // from class: android.os.connectivity.GpsBatteryStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsBatteryStats createFromParcel(Parcel in) {
            return new GpsBatteryStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsBatteryStats[] newArray(int size) {
            return new GpsBatteryStats[size];
        }
    };
    private long mEnergyConsumedMaMs;
    private long mLoggingDurationMs;
    private long[] mTimeInGpsSignalQualityLevel;

    public GpsBatteryStats() {
        initialize();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mLoggingDurationMs);
        out.writeLong(this.mEnergyConsumedMaMs);
        out.writeLongArray(this.mTimeInGpsSignalQualityLevel);
    }

    public void readFromParcel(Parcel in) {
        this.mLoggingDurationMs = in.readLong();
        this.mEnergyConsumedMaMs = in.readLong();
        in.readLongArray(this.mTimeInGpsSignalQualityLevel);
    }

    public long getLoggingDurationMs() {
        return this.mLoggingDurationMs;
    }

    public long getEnergyConsumedMaMs() {
        return this.mEnergyConsumedMaMs;
    }

    public long[] getTimeInGpsSignalQualityLevel() {
        return this.mTimeInGpsSignalQualityLevel;
    }

    public void setLoggingDurationMs(long t) {
        this.mLoggingDurationMs = t;
    }

    public void setEnergyConsumedMaMs(long e) {
        this.mEnergyConsumedMaMs = e;
    }

    public void setTimeInGpsSignalQualityLevel(long[] t) {
        this.mTimeInGpsSignalQualityLevel = Arrays.copyOfRange(t, 0, Math.min(t.length, 2));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private GpsBatteryStats(Parcel in) {
        initialize();
        readFromParcel(in);
    }

    private void initialize() {
        this.mLoggingDurationMs = 0L;
        this.mEnergyConsumedMaMs = 0L;
        this.mTimeInGpsSignalQualityLevel = new long[2];
    }
}
