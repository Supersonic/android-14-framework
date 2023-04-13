package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssAutomaticGainControl implements Parcelable {
    public static final Parcelable.Creator<GnssAutomaticGainControl> CREATOR = new Parcelable.Creator<GnssAutomaticGainControl>() { // from class: android.location.GnssAutomaticGainControl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssAutomaticGainControl createFromParcel(Parcel parcel) {
            return new GnssAutomaticGainControl(parcel.readDouble(), parcel.readInt(), parcel.readLong());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssAutomaticGainControl[] newArray(int i) {
            return new GnssAutomaticGainControl[i];
        }
    };
    private final long mCarrierFrequencyHz;
    private final int mConstellationType;
    private final double mLevelDb;

    private GnssAutomaticGainControl(double levelDb, int constellationType, long carrierFrequencyHz) {
        this.mLevelDb = levelDb;
        this.mConstellationType = constellationType;
        this.mCarrierFrequencyHz = carrierFrequencyHz;
    }

    public double getLevelDb() {
        return this.mLevelDb;
    }

    public int getConstellationType() {
        return this.mConstellationType;
    }

    public long getCarrierFrequencyHz() {
        return this.mCarrierFrequencyHz;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeDouble(this.mLevelDb);
        parcel.writeInt(this.mConstellationType);
        parcel.writeLong(this.mCarrierFrequencyHz);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("GnssAutomaticGainControl[");
        s.append("Level=").append(this.mLevelDb).append(" dB");
        s.append(" Constellation=").append(GnssStatus.constellationTypeToString(this.mConstellationType));
        s.append(" CarrierFrequency=").append(this.mCarrierFrequencyHz).append(" Hz");
        s.append(']');
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GnssAutomaticGainControl) {
            GnssAutomaticGainControl other = (GnssAutomaticGainControl) obj;
            return Double.compare(this.mLevelDb, other.mLevelDb) == 0 && this.mConstellationType == other.mConstellationType && this.mCarrierFrequencyHz == other.mCarrierFrequencyHz;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Double.valueOf(this.mLevelDb), Integer.valueOf(this.mConstellationType), Long.valueOf(this.mCarrierFrequencyHz));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private long mCarrierFrequencyHz;
        private int mConstellationType;
        private double mLevelDb;

        public Builder() {
        }

        public Builder(GnssAutomaticGainControl agc) {
            this.mLevelDb = agc.getLevelDb();
            this.mConstellationType = agc.getConstellationType();
            this.mCarrierFrequencyHz = agc.getCarrierFrequencyHz();
        }

        public Builder setLevelDb(double levelDb) {
            Preconditions.checkArgument(levelDb >= -10000.0d && levelDb <= 10000.0d);
            this.mLevelDb = levelDb;
            return this;
        }

        public Builder setConstellationType(int constellationType) {
            this.mConstellationType = constellationType;
            return this;
        }

        public Builder setCarrierFrequencyHz(long carrierFrequencyHz) {
            Preconditions.checkArgumentNonnegative(carrierFrequencyHz);
            this.mCarrierFrequencyHz = carrierFrequencyHz;
            return this;
        }

        public GnssAutomaticGainControl build() {
            return new GnssAutomaticGainControl(this.mLevelDb, this.mConstellationType, this.mCarrierFrequencyHz);
        }
    }
}
