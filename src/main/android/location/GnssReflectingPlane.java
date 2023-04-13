package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class GnssReflectingPlane implements Parcelable {
    public static final Parcelable.Creator<GnssReflectingPlane> CREATOR = new Parcelable.Creator<GnssReflectingPlane>() { // from class: android.location.GnssReflectingPlane.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssReflectingPlane createFromParcel(Parcel parcel) {
            GnssReflectingPlane reflectingPlane = new Builder().setLatitudeDegrees(parcel.readDouble()).setLongitudeDegrees(parcel.readDouble()).setAltitudeMeters(parcel.readDouble()).setAzimuthDegrees(parcel.readDouble()).build();
            return reflectingPlane;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssReflectingPlane[] newArray(int i) {
            return new GnssReflectingPlane[i];
        }
    };
    private final double mAltitudeMeters;
    private final double mAzimuthDegrees;
    private final double mLatitudeDegrees;
    private final double mLongitudeDegrees;

    private GnssReflectingPlane(Builder builder) {
        this.mLatitudeDegrees = builder.mLatitudeDegrees;
        this.mLongitudeDegrees = builder.mLongitudeDegrees;
        this.mAltitudeMeters = builder.mAltitudeMeters;
        this.mAzimuthDegrees = builder.mAzimuthDegrees;
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

    public double getAzimuthDegrees() {
        return this.mAzimuthDegrees;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.mLatitudeDegrees);
        parcel.writeDouble(this.mLongitudeDegrees);
        parcel.writeDouble(this.mAltitudeMeters);
        parcel.writeDouble(this.mAzimuthDegrees);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ReflectingPlane[");
        builder.append(" LatitudeDegrees=").append(this.mLatitudeDegrees);
        builder.append(" LongitudeDegrees=").append(this.mLongitudeDegrees);
        builder.append(" AltitudeMeters=").append(this.mAltitudeMeters);
        builder.append(" AzimuthDegrees=").append(this.mAzimuthDegrees);
        builder.append(']');
        return builder.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof GnssReflectingPlane) {
            GnssReflectingPlane that = (GnssReflectingPlane) obj;
            return Double.compare(this.mLatitudeDegrees, that.mLatitudeDegrees) == 0 && Double.compare(this.mLongitudeDegrees, that.mLongitudeDegrees) == 0 && Double.compare(this.mAltitudeMeters, that.mAltitudeMeters) == 0 && Double.compare(this.mAzimuthDegrees, that.mAzimuthDegrees) == 0;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Double.valueOf(this.mLatitudeDegrees), Double.valueOf(this.mLatitudeDegrees), Double.valueOf(this.mAltitudeMeters), Double.valueOf(this.mAzimuthDegrees));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private double mAltitudeMeters;
        private double mAzimuthDegrees;
        private double mLatitudeDegrees;
        private double mLongitudeDegrees;

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

        public Builder setAzimuthDegrees(double azimuthDegrees) {
            this.mAzimuthDegrees = azimuthDegrees;
            return this;
        }

        public GnssReflectingPlane build() {
            return new GnssReflectingPlane(this);
        }
    }
}
