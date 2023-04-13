package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssAntennaInfo implements Parcelable {
    public static final Parcelable.Creator<GnssAntennaInfo> CREATOR = new Parcelable.Creator<GnssAntennaInfo>() { // from class: android.location.GnssAntennaInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssAntennaInfo createFromParcel(Parcel in) {
            double carrierFrequencyMHz = in.readDouble();
            PhaseCenterOffset phaseCenterOffset = (PhaseCenterOffset) in.readTypedObject(PhaseCenterOffset.CREATOR);
            SphericalCorrections phaseCenterVariationCorrections = (SphericalCorrections) in.readTypedObject(SphericalCorrections.CREATOR);
            SphericalCorrections signalGainCorrections = (SphericalCorrections) in.readTypedObject(SphericalCorrections.CREATOR);
            return new GnssAntennaInfo(carrierFrequencyMHz, phaseCenterOffset, phaseCenterVariationCorrections, signalGainCorrections);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssAntennaInfo[] newArray(int size) {
            return new GnssAntennaInfo[size];
        }
    };
    private final double mCarrierFrequencyMHz;
    private final PhaseCenterOffset mPhaseCenterOffset;
    private final SphericalCorrections mPhaseCenterVariationCorrections;
    private final SphericalCorrections mSignalGainCorrections;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onGnssAntennaInfoReceived(List<GnssAntennaInfo> list);
    }

    /* loaded from: classes2.dex */
    public static final class PhaseCenterOffset implements Parcelable {
        public static final Parcelable.Creator<PhaseCenterOffset> CREATOR = new Parcelable.Creator<PhaseCenterOffset>() { // from class: android.location.GnssAntennaInfo.PhaseCenterOffset.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PhaseCenterOffset createFromParcel(Parcel in) {
                return new PhaseCenterOffset(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PhaseCenterOffset[] newArray(int size) {
                return new PhaseCenterOffset[size];
            }
        };
        private final double mOffsetXMm;
        private final double mOffsetXUncertaintyMm;
        private final double mOffsetYMm;
        private final double mOffsetYUncertaintyMm;
        private final double mOffsetZMm;
        private final double mOffsetZUncertaintyMm;

        public PhaseCenterOffset(double offsetXMm, double offsetXUncertaintyMm, double offsetYMm, double offsetYUncertaintyMm, double offsetZMm, double offsetZUncertaintyMm) {
            this.mOffsetXMm = offsetXMm;
            this.mOffsetYMm = offsetYMm;
            this.mOffsetZMm = offsetZMm;
            this.mOffsetXUncertaintyMm = offsetXUncertaintyMm;
            this.mOffsetYUncertaintyMm = offsetYUncertaintyMm;
            this.mOffsetZUncertaintyMm = offsetZUncertaintyMm;
        }

        public double getXOffsetMm() {
            return this.mOffsetXMm;
        }

        public double getXOffsetUncertaintyMm() {
            return this.mOffsetXUncertaintyMm;
        }

        public double getYOffsetMm() {
            return this.mOffsetYMm;
        }

        public double getYOffsetUncertaintyMm() {
            return this.mOffsetYUncertaintyMm;
        }

        public double getZOffsetMm() {
            return this.mOffsetZMm;
        }

        public double getZOffsetUncertaintyMm() {
            return this.mOffsetZUncertaintyMm;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.mOffsetXMm);
            dest.writeDouble(this.mOffsetXUncertaintyMm);
            dest.writeDouble(this.mOffsetYMm);
            dest.writeDouble(this.mOffsetYUncertaintyMm);
            dest.writeDouble(this.mOffsetZMm);
            dest.writeDouble(this.mOffsetZUncertaintyMm);
        }

        public String toString() {
            return "PhaseCenterOffset{OffsetXMm=" + this.mOffsetXMm + " +/-" + this.mOffsetXUncertaintyMm + ", OffsetYMm=" + this.mOffsetYMm + " +/-" + this.mOffsetYUncertaintyMm + ", OffsetZMm=" + this.mOffsetZMm + " +/-" + this.mOffsetZUncertaintyMm + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof PhaseCenterOffset) {
                PhaseCenterOffset that = (PhaseCenterOffset) o;
                return Double.compare(that.mOffsetXMm, this.mOffsetXMm) == 0 && Double.compare(that.mOffsetXUncertaintyMm, this.mOffsetXUncertaintyMm) == 0 && Double.compare(that.mOffsetYMm, this.mOffsetYMm) == 0 && Double.compare(that.mOffsetYUncertaintyMm, this.mOffsetYUncertaintyMm) == 0 && Double.compare(that.mOffsetZMm, this.mOffsetZMm) == 0 && Double.compare(that.mOffsetZUncertaintyMm, this.mOffsetZUncertaintyMm) == 0;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Double.valueOf(this.mOffsetXMm), Double.valueOf(this.mOffsetYMm), Double.valueOf(this.mOffsetZMm));
        }
    }

    /* loaded from: classes2.dex */
    public static final class SphericalCorrections implements Parcelable {
        public static final Parcelable.Creator<SphericalCorrections> CREATOR = new Parcelable.Creator<SphericalCorrections>() { // from class: android.location.GnssAntennaInfo.SphericalCorrections.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SphericalCorrections createFromParcel(Parcel in) {
                return new SphericalCorrections(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SphericalCorrections[] newArray(int size) {
                return new SphericalCorrections[size];
            }
        };
        private final double[][] mCorrectionUncertainties;
        private final double[][] mCorrections;
        private final int mNumColumns;
        private final int mNumRows;

        public SphericalCorrections(double[][] corrections, double[][] correctionUncertainties) {
            if (corrections.length != correctionUncertainties.length || corrections.length < 1) {
                throw new IllegalArgumentException("correction and uncertainty arrays must have the same (non-zero) dimensions");
            }
            this.mNumRows = corrections.length;
            this.mNumColumns = corrections[0].length;
            for (int i = 0; i < corrections.length; i++) {
                int length = corrections[i].length;
                int i2 = this.mNumColumns;
                if (length != i2 || correctionUncertainties[i].length != i2 || i2 < 2) {
                    throw new IllegalArgumentException("correction and uncertainty arrays must all  have the same (greater than 2) number of columns");
                }
            }
            this.mCorrections = corrections;
            this.mCorrectionUncertainties = correctionUncertainties;
        }

        private SphericalCorrections(Parcel in) {
            int numRows = in.readInt();
            int numColumns = in.readInt();
            double[][] corrections = (double[][]) Array.newInstance(Double.TYPE, numRows, numColumns);
            double[][] correctionUncertainties = (double[][]) Array.newInstance(Double.TYPE, numRows, numColumns);
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numColumns; col++) {
                    corrections[row][col] = in.readDouble();
                    correctionUncertainties[row][col] = in.readDouble();
                }
            }
            this.mNumRows = numRows;
            this.mNumColumns = numColumns;
            this.mCorrections = corrections;
            this.mCorrectionUncertainties = correctionUncertainties;
        }

        public double[][] getCorrectionsArray() {
            return this.mCorrections;
        }

        public double[][] getCorrectionUncertaintiesArray() {
            return this.mCorrectionUncertainties;
        }

        public double getDeltaTheta() {
            return 360.0d / this.mNumRows;
        }

        public double getDeltaPhi() {
            return 180.0d / (this.mNumColumns - 1);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mNumRows);
            dest.writeInt(this.mNumColumns);
            for (int row = 0; row < this.mNumRows; row++) {
                for (int col = 0; col < this.mNumColumns; col++) {
                    dest.writeDouble(this.mCorrections[row][col]);
                    dest.writeDouble(this.mCorrectionUncertainties[row][col]);
                }
            }
        }

        public String toString() {
            return "SphericalCorrections{Corrections=" + Arrays.deepToString(this.mCorrections) + ", CorrectionUncertainties=" + Arrays.deepToString(this.mCorrectionUncertainties) + ", DeltaTheta=" + getDeltaTheta() + ", DeltaPhi=" + getDeltaPhi() + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof SphericalCorrections) {
                SphericalCorrections that = (SphericalCorrections) o;
                return this.mNumRows == that.mNumRows && this.mNumColumns == that.mNumColumns && Arrays.deepEquals(this.mCorrections, that.mCorrections) && Arrays.deepEquals(this.mCorrectionUncertainties, that.mCorrectionUncertainties);
            }
            return false;
        }

        public int hashCode() {
            int result = Arrays.deepHashCode(this.mCorrections);
            return (result * 31) + Arrays.deepHashCode(this.mCorrectionUncertainties);
        }
    }

    private GnssAntennaInfo(double carrierFrequencyMHz, PhaseCenterOffset phaseCenterOffset, SphericalCorrections phaseCenterVariationCorrections, SphericalCorrections signalGainCorrectionDbi) {
        this.mCarrierFrequencyMHz = carrierFrequencyMHz;
        this.mPhaseCenterOffset = (PhaseCenterOffset) Objects.requireNonNull(phaseCenterOffset);
        this.mPhaseCenterVariationCorrections = phaseCenterVariationCorrections;
        this.mSignalGainCorrections = signalGainCorrectionDbi;
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private double mCarrierFrequencyMHz;
        private PhaseCenterOffset mPhaseCenterOffset;
        private SphericalCorrections mPhaseCenterVariationCorrections;
        private SphericalCorrections mSignalGainCorrections;

        @Deprecated
        public Builder() {
            this(0.0d, new PhaseCenterOffset(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d));
        }

        public Builder(double carrierFrequencyMHz, PhaseCenterOffset phaseCenterOffset) {
            this.mCarrierFrequencyMHz = carrierFrequencyMHz;
            this.mPhaseCenterOffset = (PhaseCenterOffset) Objects.requireNonNull(phaseCenterOffset);
        }

        public Builder(GnssAntennaInfo antennaInfo) {
            this.mCarrierFrequencyMHz = antennaInfo.mCarrierFrequencyMHz;
            this.mPhaseCenterOffset = antennaInfo.mPhaseCenterOffset;
            this.mPhaseCenterVariationCorrections = antennaInfo.mPhaseCenterVariationCorrections;
            this.mSignalGainCorrections = antennaInfo.mSignalGainCorrections;
        }

        public Builder setCarrierFrequencyMHz(double carrierFrequencyMHz) {
            this.mCarrierFrequencyMHz = carrierFrequencyMHz;
            return this;
        }

        public Builder setPhaseCenterOffset(PhaseCenterOffset phaseCenterOffset) {
            this.mPhaseCenterOffset = (PhaseCenterOffset) Objects.requireNonNull(phaseCenterOffset);
            return this;
        }

        public Builder setPhaseCenterVariationCorrections(SphericalCorrections phaseCenterVariationCorrections) {
            this.mPhaseCenterVariationCorrections = phaseCenterVariationCorrections;
            return this;
        }

        public Builder setSignalGainCorrections(SphericalCorrections signalGainCorrections) {
            this.mSignalGainCorrections = signalGainCorrections;
            return this;
        }

        public GnssAntennaInfo build() {
            return new GnssAntennaInfo(this.mCarrierFrequencyMHz, this.mPhaseCenterOffset, this.mPhaseCenterVariationCorrections, this.mSignalGainCorrections);
        }
    }

    public double getCarrierFrequencyMHz() {
        return this.mCarrierFrequencyMHz;
    }

    public PhaseCenterOffset getPhaseCenterOffset() {
        return this.mPhaseCenterOffset;
    }

    public SphericalCorrections getPhaseCenterVariationCorrections() {
        return this.mPhaseCenterVariationCorrections;
    }

    public SphericalCorrections getSignalGainCorrections() {
        return this.mSignalGainCorrections;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.mCarrierFrequencyMHz);
        parcel.writeTypedObject(this.mPhaseCenterOffset, flags);
        parcel.writeTypedObject(this.mPhaseCenterVariationCorrections, flags);
        parcel.writeTypedObject(this.mSignalGainCorrections, flags);
    }

    public String toString() {
        return "GnssAntennaInfo{CarrierFrequencyMHz=" + this.mCarrierFrequencyMHz + ", PhaseCenterOffset=" + this.mPhaseCenterOffset + ", PhaseCenterVariationCorrections=" + this.mPhaseCenterVariationCorrections + ", SignalGainCorrections=" + this.mSignalGainCorrections + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GnssAntennaInfo) {
            GnssAntennaInfo that = (GnssAntennaInfo) o;
            return Double.compare(that.mCarrierFrequencyMHz, this.mCarrierFrequencyMHz) == 0 && this.mPhaseCenterOffset.equals(that.mPhaseCenterOffset) && Objects.equals(this.mPhaseCenterVariationCorrections, that.mPhaseCenterVariationCorrections) && Objects.equals(this.mSignalGainCorrections, that.mSignalGainCorrections);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Double.valueOf(this.mCarrierFrequencyMHz), this.mPhaseCenterOffset, this.mPhaseCenterVariationCorrections, this.mSignalGainCorrections);
    }
}
