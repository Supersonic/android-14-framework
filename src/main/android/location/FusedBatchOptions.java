package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public class FusedBatchOptions implements Parcelable {
    public static final Parcelable.Creator<FusedBatchOptions> CREATOR = new Parcelable.Creator<FusedBatchOptions>() { // from class: android.location.FusedBatchOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FusedBatchOptions createFromParcel(Parcel parcel) {
            FusedBatchOptions options = new FusedBatchOptions();
            options.setMaxPowerAllocationInMW(parcel.readDouble());
            options.setPeriodInNS(parcel.readLong());
            options.setSourceToUse(parcel.readInt());
            options.setFlag(parcel.readInt());
            options.setSmallestDisplacementMeters(parcel.readFloat());
            return options;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FusedBatchOptions[] newArray(int size) {
            return new FusedBatchOptions[size];
        }
    };
    private volatile long mPeriodInNS = 0;
    private volatile int mSourcesToUse = 0;
    private volatile int mFlags = 0;
    private volatile double mMaxPowerAllocationInMW = 0.0d;
    private volatile float mSmallestDisplacementMeters = 0.0f;

    /* loaded from: classes2.dex */
    public static final class BatchFlags {
        public static int WAKEUP_ON_FIFO_FULL = 1;
        public static int CALLBACK_ON_LOCATION_FIX = 2;
    }

    /* loaded from: classes2.dex */
    public static final class SourceTechnologies {
        public static int GNSS = 1;
        public static int WIFI = 2;
        public static int SENSORS = 4;
        public static int CELL = 8;
        public static int BLUETOOTH = 16;
    }

    public void setMaxPowerAllocationInMW(double value) {
        this.mMaxPowerAllocationInMW = value;
    }

    public double getMaxPowerAllocationInMW() {
        return this.mMaxPowerAllocationInMW;
    }

    public void setPeriodInNS(long value) {
        this.mPeriodInNS = value;
    }

    public long getPeriodInNS() {
        return this.mPeriodInNS;
    }

    public void setSmallestDisplacementMeters(float value) {
        this.mSmallestDisplacementMeters = value;
    }

    public float getSmallestDisplacementMeters() {
        return this.mSmallestDisplacementMeters;
    }

    public void setSourceToUse(int source) {
        this.mSourcesToUse |= source;
    }

    public void resetSourceToUse(int source) {
        this.mSourcesToUse &= ~source;
    }

    public boolean isSourceToUseSet(int source) {
        return (this.mSourcesToUse & source) != 0;
    }

    public int getSourcesToUse() {
        return this.mSourcesToUse;
    }

    public void setFlag(int flag) {
        this.mFlags |= flag;
    }

    public void resetFlag(int flag) {
        this.mFlags &= ~flag;
    }

    public boolean isFlagSet(int flag) {
        return (this.mFlags & flag) != 0;
    }

    public int getFlags() {
        return this.mFlags;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.mMaxPowerAllocationInMW);
        parcel.writeLong(this.mPeriodInNS);
        parcel.writeInt(this.mSourcesToUse);
        parcel.writeInt(this.mFlags);
        parcel.writeFloat(this.mSmallestDisplacementMeters);
    }
}
