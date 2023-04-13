package android.app.time;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TimeZoneDetectorStatus implements Parcelable {
    public static final Parcelable.Creator<TimeZoneDetectorStatus> CREATOR = new Parcelable.Creator<TimeZoneDetectorStatus>() { // from class: android.app.time.TimeZoneDetectorStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneDetectorStatus createFromParcel(Parcel in) {
            int detectorStatus = in.readInt();
            TelephonyTimeZoneAlgorithmStatus telephonyTimeZoneAlgorithmStatus = (TelephonyTimeZoneAlgorithmStatus) in.readParcelable(getClass().getClassLoader(), TelephonyTimeZoneAlgorithmStatus.class);
            LocationTimeZoneAlgorithmStatus locationTimeZoneAlgorithmStatus = (LocationTimeZoneAlgorithmStatus) in.readParcelable(getClass().getClassLoader(), LocationTimeZoneAlgorithmStatus.class);
            return new TimeZoneDetectorStatus(detectorStatus, telephonyTimeZoneAlgorithmStatus, locationTimeZoneAlgorithmStatus);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneDetectorStatus[] newArray(int size) {
            return new TimeZoneDetectorStatus[size];
        }
    };
    private final int mDetectorStatus;
    private final LocationTimeZoneAlgorithmStatus mLocationTimeZoneAlgorithmStatus;
    private final TelephonyTimeZoneAlgorithmStatus mTelephonyTimeZoneAlgorithmStatus;

    public TimeZoneDetectorStatus(int detectorStatus, TelephonyTimeZoneAlgorithmStatus telephonyTimeZoneAlgorithmStatus, LocationTimeZoneAlgorithmStatus locationTimeZoneAlgorithmStatus) {
        this.mDetectorStatus = DetectorStatusTypes.requireValidDetectorStatus(detectorStatus);
        this.mTelephonyTimeZoneAlgorithmStatus = (TelephonyTimeZoneAlgorithmStatus) Objects.requireNonNull(telephonyTimeZoneAlgorithmStatus);
        this.mLocationTimeZoneAlgorithmStatus = (LocationTimeZoneAlgorithmStatus) Objects.requireNonNull(locationTimeZoneAlgorithmStatus);
    }

    public int getDetectorStatus() {
        return this.mDetectorStatus;
    }

    public TelephonyTimeZoneAlgorithmStatus getTelephonyTimeZoneAlgorithmStatus() {
        return this.mTelephonyTimeZoneAlgorithmStatus;
    }

    public LocationTimeZoneAlgorithmStatus getLocationTimeZoneAlgorithmStatus() {
        return this.mLocationTimeZoneAlgorithmStatus;
    }

    public String toString() {
        return "TimeZoneDetectorStatus{mDetectorStatus=" + DetectorStatusTypes.detectorStatusToString(this.mDetectorStatus) + ", mTelephonyTimeZoneAlgorithmStatus=" + this.mTelephonyTimeZoneAlgorithmStatus + ", mLocationTimeZoneAlgorithmStatus=" + this.mLocationTimeZoneAlgorithmStatus + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mDetectorStatus);
        parcel.writeParcelable(this.mTelephonyTimeZoneAlgorithmStatus, flags);
        parcel.writeParcelable(this.mLocationTimeZoneAlgorithmStatus, flags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeZoneDetectorStatus that = (TimeZoneDetectorStatus) o;
        if (this.mDetectorStatus == that.mDetectorStatus && this.mTelephonyTimeZoneAlgorithmStatus.equals(that.mTelephonyTimeZoneAlgorithmStatus) && this.mLocationTimeZoneAlgorithmStatus.equals(that.mLocationTimeZoneAlgorithmStatus)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDetectorStatus), this.mTelephonyTimeZoneAlgorithmStatus, this.mLocationTimeZoneAlgorithmStatus);
    }
}
