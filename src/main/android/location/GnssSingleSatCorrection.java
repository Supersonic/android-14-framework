package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class GnssSingleSatCorrection implements Parcelable {
    public static final Parcelable.Creator<GnssSingleSatCorrection> CREATOR = new Parcelable.Creator<GnssSingleSatCorrection>() { // from class: android.location.GnssSingleSatCorrection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssSingleSatCorrection createFromParcel(Parcel parcel) {
            int singleSatCorrectionFlags = parcel.readInt();
            int constellationType = parcel.readInt();
            int satId = parcel.readInt();
            float carrierFrequencyHz = parcel.readFloat();
            float probSatIsLos = (singleSatCorrectionFlags & 1) != 0 ? parcel.readFloat() : 0.0f;
            float combinedExcessPathLengthMeters = (singleSatCorrectionFlags & 2) != 0 ? parcel.readFloat() : 0.0f;
            float combinedExcessPathLengthUncertaintyMeters = (singleSatCorrectionFlags & 4) != 0 ? parcel.readFloat() : 0.0f;
            float combinedAttenuationDb = (singleSatCorrectionFlags & 16) != 0 ? parcel.readFloat() : 0.0f;
            List<GnssExcessPathInfo> gnssExcessPathInfoList = parcel.createTypedArrayList(GnssExcessPathInfo.CREATOR);
            return new GnssSingleSatCorrection(singleSatCorrectionFlags, constellationType, satId, carrierFrequencyHz, probSatIsLos, combinedExcessPathLengthMeters, combinedExcessPathLengthUncertaintyMeters, combinedAttenuationDb, gnssExcessPathInfoList);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssSingleSatCorrection[] newArray(int i) {
            return new GnssSingleSatCorrection[i];
        }
    };
    private static final int HAS_COMBINED_ATTENUATION_MASK = 16;
    private static final int HAS_COMBINED_EXCESS_PATH_LENGTH_MASK = 2;
    private static final int HAS_COMBINED_EXCESS_PATH_LENGTH_UNC_MASK = 4;
    private static final int HAS_PROB_SAT_IS_LOS_MASK = 1;
    private final float mCarrierFrequencyHz;
    private final float mCombinedAttenuationDb;
    private final float mCombinedExcessPathLengthMeters;
    private final float mCombinedExcessPathLengthUncertaintyMeters;
    private final int mConstellationType;
    private final List<GnssExcessPathInfo> mGnssExcessPathInfoList;
    private final float mProbSatIsLos;
    private final int mSatId;
    private final int mSingleSatCorrectionFlags;

    private GnssSingleSatCorrection(int singleSatCorrectionFlags, int constellationType, int satId, float carrierFrequencyHz, float probSatIsLos, float excessPathLengthMeters, float excessPathLengthUncertaintyMeters, float combinedAttenuationDb, List<GnssExcessPathInfo> gnssExcessPathInfoList) {
        this.mSingleSatCorrectionFlags = singleSatCorrectionFlags;
        this.mConstellationType = constellationType;
        this.mSatId = satId;
        this.mCarrierFrequencyHz = carrierFrequencyHz;
        this.mProbSatIsLos = probSatIsLos;
        this.mCombinedExcessPathLengthMeters = excessPathLengthMeters;
        this.mCombinedExcessPathLengthUncertaintyMeters = excessPathLengthUncertaintyMeters;
        this.mCombinedAttenuationDb = combinedAttenuationDb;
        this.mGnssExcessPathInfoList = gnssExcessPathInfoList;
    }

    public int getSingleSatelliteCorrectionFlags() {
        return this.mSingleSatCorrectionFlags;
    }

    public int getConstellationType() {
        return this.mConstellationType;
    }

    public int getSatelliteId() {
        return this.mSatId;
    }

    public float getCarrierFrequencyHz() {
        return this.mCarrierFrequencyHz;
    }

    public float getProbabilityLineOfSight() {
        return this.mProbSatIsLos;
    }

    public float getExcessPathLengthMeters() {
        return this.mCombinedExcessPathLengthMeters;
    }

    public float getExcessPathLengthUncertaintyMeters() {
        return this.mCombinedExcessPathLengthUncertaintyMeters;
    }

    public float getCombinedAttenuationDb() {
        return this.mCombinedAttenuationDb;
    }

    @Deprecated
    public GnssReflectingPlane getReflectingPlane() {
        return null;
    }

    public List<GnssExcessPathInfo> getGnssExcessPathInfoList() {
        return this.mGnssExcessPathInfoList;
    }

    public boolean hasValidSatelliteLineOfSight() {
        return (this.mSingleSatCorrectionFlags & 1) != 0;
    }

    public boolean hasExcessPathLength() {
        return (this.mSingleSatCorrectionFlags & 2) != 0;
    }

    public boolean hasExcessPathLengthUncertainty() {
        return (this.mSingleSatCorrectionFlags & 4) != 0;
    }

    @Deprecated
    public boolean hasReflectingPlane() {
        return false;
    }

    public boolean hasCombinedAttenuation() {
        return (this.mSingleSatCorrectionFlags & 16) != 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mSingleSatCorrectionFlags);
        parcel.writeInt(this.mConstellationType);
        parcel.writeInt(this.mSatId);
        parcel.writeFloat(this.mCarrierFrequencyHz);
        if (hasValidSatelliteLineOfSight()) {
            parcel.writeFloat(this.mProbSatIsLos);
        }
        if (hasExcessPathLength()) {
            parcel.writeFloat(this.mCombinedExcessPathLengthMeters);
        }
        if (hasExcessPathLengthUncertainty()) {
            parcel.writeFloat(this.mCombinedExcessPathLengthUncertaintyMeters);
        }
        if (hasCombinedAttenuation()) {
            parcel.writeFloat(this.mCombinedAttenuationDb);
        }
        parcel.writeTypedList(this.mGnssExcessPathInfoList);
    }

    public boolean equals(Object obj) {
        if (obj instanceof GnssSingleSatCorrection) {
            GnssSingleSatCorrection that = (GnssSingleSatCorrection) obj;
            if (this.mSingleSatCorrectionFlags == that.mSingleSatCorrectionFlags && this.mConstellationType == that.mConstellationType && this.mSatId == that.mSatId && Float.compare(this.mCarrierFrequencyHz, that.mCarrierFrequencyHz) == 0) {
                if (!hasValidSatelliteLineOfSight() || Float.compare(this.mProbSatIsLos, that.mProbSatIsLos) == 0) {
                    if (!hasExcessPathLength() || Float.compare(this.mCombinedExcessPathLengthMeters, that.mCombinedExcessPathLengthMeters) == 0) {
                        if (!hasExcessPathLengthUncertainty() || Float.compare(this.mCombinedExcessPathLengthUncertaintyMeters, that.mCombinedExcessPathLengthUncertaintyMeters) == 0) {
                            return (!hasCombinedAttenuation() || Float.compare(this.mCombinedAttenuationDb, that.mCombinedAttenuationDb) == 0) && this.mGnssExcessPathInfoList.equals(that.mGnssExcessPathInfoList);
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSingleSatCorrectionFlags), Integer.valueOf(this.mConstellationType), Integer.valueOf(this.mSatId), Float.valueOf(this.mCarrierFrequencyHz), Float.valueOf(this.mProbSatIsLos), Float.valueOf(this.mCombinedExcessPathLengthMeters), Float.valueOf(this.mCombinedExcessPathLengthUncertaintyMeters), Float.valueOf(this.mCombinedAttenuationDb), this.mGnssExcessPathInfoList);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GnssSingleSatCorrection:[");
        builder.append(" ConstellationType=").append(this.mConstellationType);
        builder.append(" SatId=").append(this.mSatId);
        builder.append(" CarrierFrequencyHz=").append(this.mCarrierFrequencyHz);
        if (hasValidSatelliteLineOfSight()) {
            builder.append(" ProbSatIsLos=").append(this.mProbSatIsLos);
        }
        if (hasExcessPathLength()) {
            builder.append(" CombinedExcessPathLengthMeters=").append(this.mCombinedExcessPathLengthMeters);
        }
        if (hasExcessPathLengthUncertainty()) {
            builder.append(" CombinedExcessPathLengthUncertaintyMeters=").append(this.mCombinedExcessPathLengthUncertaintyMeters);
        }
        if (hasCombinedAttenuation()) {
            builder.append(" CombinedAttenuationDb=").append(this.mCombinedAttenuationDb);
        }
        if (!this.mGnssExcessPathInfoList.isEmpty()) {
            builder.append(' ').append(this.mGnssExcessPathInfoList.toString());
        }
        builder.append(']');
        return builder.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private float mCarrierFrequencyHz;
        private float mCombinedAttenuationDb;
        private float mCombinedExcessPathLengthMeters;
        private float mCombinedExcessPathLengthUncertaintyMeters;
        private int mConstellationType;
        private List<GnssExcessPathInfo> mGnssExcessInfoList = new ArrayList();
        private float mProbSatIsLos;
        private int mSatId;
        private int mSingleSatCorrectionFlags;

        public Builder setConstellationType(int constellationType) {
            this.mConstellationType = constellationType;
            return this;
        }

        public Builder setSatelliteId(int satId) {
            Preconditions.checkArgumentNonnegative(satId, "satId should be non-negative.");
            this.mSatId = satId;
            return this;
        }

        public Builder setCarrierFrequencyHz(float carrierFrequencyHz) {
            Preconditions.checkArgumentInRange(carrierFrequencyHz, 0.0f, Float.MAX_VALUE, "carrierFrequencyHz");
            this.mCarrierFrequencyHz = carrierFrequencyHz;
            return this;
        }

        public Builder setProbabilityLineOfSight(float probSatIsLos) {
            Preconditions.checkArgumentInRange(probSatIsLos, 0.0f, 1.0f, "probSatIsLos should be between 0 and 1.");
            this.mProbSatIsLos = probSatIsLos;
            this.mSingleSatCorrectionFlags |= 1;
            return this;
        }

        public Builder clearProbabilityLineOfSight() {
            this.mProbSatIsLos = 0.0f;
            this.mSingleSatCorrectionFlags &= -2;
            return this;
        }

        public Builder setExcessPathLengthMeters(float combinedExcessPathLengthMeters) {
            Preconditions.checkArgumentInRange(combinedExcessPathLengthMeters, 0.0f, Float.MAX_VALUE, "excessPathLengthMeters");
            this.mCombinedExcessPathLengthMeters = combinedExcessPathLengthMeters;
            this.mSingleSatCorrectionFlags |= 2;
            return this;
        }

        public Builder clearExcessPathLengthMeters() {
            this.mCombinedExcessPathLengthMeters = 0.0f;
            this.mSingleSatCorrectionFlags &= -3;
            return this;
        }

        public Builder setExcessPathLengthUncertaintyMeters(float combinedExcessPathLengthUncertaintyMeters) {
            Preconditions.checkArgumentInRange(combinedExcessPathLengthUncertaintyMeters, 0.0f, Float.MAX_VALUE, "excessPathLengthUncertaintyMeters");
            this.mCombinedExcessPathLengthUncertaintyMeters = combinedExcessPathLengthUncertaintyMeters;
            this.mSingleSatCorrectionFlags |= 4;
            return this;
        }

        public Builder clearExcessPathLengthUncertaintyMeters() {
            this.mCombinedExcessPathLengthUncertaintyMeters = 0.0f;
            this.mSingleSatCorrectionFlags &= -5;
            return this;
        }

        public Builder setCombinedAttenuationDb(float combinedAttenuationDb) {
            Preconditions.checkArgumentInRange(combinedAttenuationDb, 0.0f, Float.MAX_VALUE, "combinedAttenuationDb");
            this.mCombinedAttenuationDb = combinedAttenuationDb;
            this.mSingleSatCorrectionFlags |= 16;
            return this;
        }

        public Builder clearCombinedAttenuationDb() {
            this.mCombinedAttenuationDb = 0.0f;
            this.mSingleSatCorrectionFlags &= -17;
            return this;
        }

        @Deprecated
        public Builder setReflectingPlane(GnssReflectingPlane reflectingPlane) {
            return this;
        }

        public Builder setGnssExcessPathInfoList(List<GnssExcessPathInfo> infoList) {
            this.mGnssExcessInfoList = new ArrayList(infoList);
            return this;
        }

        public GnssSingleSatCorrection build() {
            return new GnssSingleSatCorrection(this.mSingleSatCorrectionFlags, this.mConstellationType, this.mSatId, this.mCarrierFrequencyHz, this.mProbSatIsLos, this.mCombinedExcessPathLengthMeters, this.mCombinedExcessPathLengthUncertaintyMeters, this.mCombinedAttenuationDb, this.mGnssExcessInfoList);
        }
    }
}
