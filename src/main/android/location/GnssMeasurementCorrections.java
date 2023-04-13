package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@SystemApi
/* loaded from: classes2.dex */
public final class GnssMeasurementCorrections implements Parcelable {
    public static final Parcelable.Creator<GnssMeasurementCorrections> CREATOR = new Parcelable.Creator<GnssMeasurementCorrections>() { // from class: android.location.GnssMeasurementCorrections.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssMeasurementCorrections createFromParcel(Parcel parcel) {
            Builder gnssMeasurementCorrectons = new Builder().setLatitudeDegrees(parcel.readDouble()).setLongitudeDegrees(parcel.readDouble()).setAltitudeMeters(parcel.readDouble()).setHorizontalPositionUncertaintyMeters(parcel.readDouble()).setVerticalPositionUncertaintyMeters(parcel.readDouble()).setToaGpsNanosecondsOfWeek(parcel.readLong());
            ArrayList arrayList = new ArrayList();
            parcel.readTypedList(arrayList, GnssSingleSatCorrection.CREATOR);
            gnssMeasurementCorrectons.setSingleSatelliteCorrectionList(arrayList);
            boolean hasEnvironmentBearing = parcel.readBoolean();
            if (hasEnvironmentBearing) {
                gnssMeasurementCorrectons.setEnvironmentBearingDegrees(parcel.readFloat());
                gnssMeasurementCorrectons.setEnvironmentBearingUncertaintyDegrees(parcel.readFloat());
            }
            return gnssMeasurementCorrectons.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssMeasurementCorrections[] newArray(int i) {
            return new GnssMeasurementCorrections[i];
        }
    };
    private final double mAltitudeMeters;
    private final float mEnvironmentBearingDegrees;
    private final float mEnvironmentBearingUncertaintyDegrees;
    private final boolean mHasEnvironmentBearing;
    private final double mHorizontalPositionUncertaintyMeters;
    private final double mLatitudeDegrees;
    private final double mLongitudeDegrees;
    private final List<GnssSingleSatCorrection> mSingleSatCorrectionList;
    private final long mToaGpsNanosecondsOfWeek;
    private final double mVerticalPositionUncertaintyMeters;

    private GnssMeasurementCorrections(Builder builder) {
        this.mLatitudeDegrees = builder.mLatitudeDegrees;
        this.mLongitudeDegrees = builder.mLongitudeDegrees;
        this.mAltitudeMeters = builder.mAltitudeMeters;
        this.mHorizontalPositionUncertaintyMeters = builder.mHorizontalPositionUncertaintyMeters;
        this.mVerticalPositionUncertaintyMeters = builder.mVerticalPositionUncertaintyMeters;
        this.mToaGpsNanosecondsOfWeek = builder.mToaGpsNanosecondsOfWeek;
        List<GnssSingleSatCorrection> singleSatCorrList = builder.mSingleSatCorrectionList;
        boolean z = true;
        Preconditions.checkArgument((singleSatCorrList == null || singleSatCorrList.isEmpty()) ? false : true);
        this.mSingleSatCorrectionList = Collections.unmodifiableList(new ArrayList(singleSatCorrList));
        this.mHasEnvironmentBearing = (builder.mEnvironmentBearingIsSet && builder.mEnvironmentBearingUncertaintyIsSet) ? z : false;
        this.mEnvironmentBearingDegrees = builder.mEnvironmentBearingDegrees;
        this.mEnvironmentBearingUncertaintyDegrees = builder.mEnvironmentBearingUncertaintyDegrees;
    }

    public double getLatitudeDegrees() {
        return this.mLatitudeDegrees;
    }

    public double getLongitudeDegrees() {
        return this.mLongitudeDegrees;
    }

    public double getAltitudeMeters() {
        return this.mAltitudeMeters;
    }

    public double getHorizontalPositionUncertaintyMeters() {
        return this.mHorizontalPositionUncertaintyMeters;
    }

    public double getVerticalPositionUncertaintyMeters() {
        return this.mVerticalPositionUncertaintyMeters;
    }

    public long getToaGpsNanosecondsOfWeek() {
        return this.mToaGpsNanosecondsOfWeek;
    }

    public List<GnssSingleSatCorrection> getSingleSatelliteCorrectionList() {
        return this.mSingleSatCorrectionList;
    }

    public boolean hasEnvironmentBearing() {
        return this.mHasEnvironmentBearing;
    }

    public float getEnvironmentBearingDegrees() {
        return this.mEnvironmentBearingDegrees;
    }

    public float getEnvironmentBearingUncertaintyDegrees() {
        return this.mEnvironmentBearingUncertaintyDegrees;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "GnssMeasurementCorrections:\n" + String.format("   %-29s = %s\n", "LatitudeDegrees = ", Double.valueOf(this.mLatitudeDegrees)) + String.format("   %-29s = %s\n", "LongitudeDegrees = ", Double.valueOf(this.mLongitudeDegrees)) + String.format("   %-29s = %s\n", "AltitudeMeters = ", Double.valueOf(this.mAltitudeMeters)) + String.format("   %-29s = %s\n", "HorizontalPositionUncertaintyMeters = ", Double.valueOf(this.mHorizontalPositionUncertaintyMeters)) + String.format("   %-29s = %s\n", "VerticalPositionUncertaintyMeters = ", Double.valueOf(this.mVerticalPositionUncertaintyMeters)) + String.format("   %-29s = %s\n", "ToaGpsNanosecondsOfWeek = ", Long.valueOf(this.mToaGpsNanosecondsOfWeek)) + String.format("   %-29s = %s\n", "mSingleSatCorrectionList = ", this.mSingleSatCorrectionList) + String.format("   %-29s = %s\n", "HasEnvironmentBearing = ", Boolean.valueOf(this.mHasEnvironmentBearing)) + String.format("   %-29s = %s\n", "EnvironmentBearingDegrees = ", Float.valueOf(this.mEnvironmentBearingDegrees)) + String.format("   %-29s = %s\n", "EnvironmentBearingUncertaintyDegrees = ", Float.valueOf(this.mEnvironmentBearingUncertaintyDegrees));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.mLatitudeDegrees);
        parcel.writeDouble(this.mLongitudeDegrees);
        parcel.writeDouble(this.mAltitudeMeters);
        parcel.writeDouble(this.mHorizontalPositionUncertaintyMeters);
        parcel.writeDouble(this.mVerticalPositionUncertaintyMeters);
        parcel.writeLong(this.mToaGpsNanosecondsOfWeek);
        parcel.writeTypedList(this.mSingleSatCorrectionList);
        parcel.writeBoolean(this.mHasEnvironmentBearing);
        if (this.mHasEnvironmentBearing) {
            parcel.writeFloat(this.mEnvironmentBearingDegrees);
            parcel.writeFloat(this.mEnvironmentBearingUncertaintyDegrees);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private double mAltitudeMeters;
        private float mEnvironmentBearingDegrees;
        private float mEnvironmentBearingUncertaintyDegrees;
        private double mHorizontalPositionUncertaintyMeters;
        private double mLatitudeDegrees;
        private double mLongitudeDegrees;
        private List<GnssSingleSatCorrection> mSingleSatCorrectionList;
        private long mToaGpsNanosecondsOfWeek;
        private double mVerticalPositionUncertaintyMeters;
        private boolean mEnvironmentBearingIsSet = false;
        private boolean mEnvironmentBearingUncertaintyIsSet = false;

        public Builder setLatitudeDegrees(double latitudeDegrees) {
            this.mLatitudeDegrees = latitudeDegrees;
            return this;
        }

        public Builder setLongitudeDegrees(double longitudeDegrees) {
            this.mLongitudeDegrees = longitudeDegrees;
            return this;
        }

        public Builder setAltitudeMeters(double altitudeMeters) {
            this.mAltitudeMeters = altitudeMeters;
            return this;
        }

        public Builder setHorizontalPositionUncertaintyMeters(double horizontalPositionUncertaintyMeters) {
            this.mHorizontalPositionUncertaintyMeters = horizontalPositionUncertaintyMeters;
            return this;
        }

        public Builder setVerticalPositionUncertaintyMeters(double verticalPositionUncertaintyMeters) {
            this.mVerticalPositionUncertaintyMeters = verticalPositionUncertaintyMeters;
            return this;
        }

        public Builder setToaGpsNanosecondsOfWeek(long toaGpsNanosecondsOfWeek) {
            this.mToaGpsNanosecondsOfWeek = toaGpsNanosecondsOfWeek;
            return this;
        }

        public Builder setSingleSatelliteCorrectionList(List<GnssSingleSatCorrection> singleSatCorrectionList) {
            this.mSingleSatCorrectionList = singleSatCorrectionList;
            return this;
        }

        public Builder setEnvironmentBearingDegrees(float environmentBearingDegrees) {
            this.mEnvironmentBearingDegrees = environmentBearingDegrees;
            this.mEnvironmentBearingIsSet = true;
            return this;
        }

        public Builder setEnvironmentBearingUncertaintyDegrees(float environmentBearingUncertaintyDegrees) {
            this.mEnvironmentBearingUncertaintyDegrees = environmentBearingUncertaintyDegrees;
            this.mEnvironmentBearingUncertaintyIsSet = true;
            return this;
        }

        public GnssMeasurementCorrections build() {
            if (this.mEnvironmentBearingIsSet ^ this.mEnvironmentBearingUncertaintyIsSet) {
                throw new IllegalStateException("Both environment bearing and environment bearing uncertainty must be set.");
            }
            return new GnssMeasurementCorrections(this);
        }
    }
}
