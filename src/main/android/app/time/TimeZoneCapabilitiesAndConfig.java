package android.app.time;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TimeZoneCapabilitiesAndConfig implements Parcelable {
    public static final Parcelable.Creator<TimeZoneCapabilitiesAndConfig> CREATOR = new Parcelable.Creator<TimeZoneCapabilitiesAndConfig>() { // from class: android.app.time.TimeZoneCapabilitiesAndConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneCapabilitiesAndConfig createFromParcel(Parcel in) {
            return TimeZoneCapabilitiesAndConfig.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneCapabilitiesAndConfig[] newArray(int size) {
            return new TimeZoneCapabilitiesAndConfig[size];
        }
    };
    private final TimeZoneCapabilities mCapabilities;
    private final TimeZoneConfiguration mConfiguration;
    private final TimeZoneDetectorStatus mDetectorStatus;

    public TimeZoneCapabilitiesAndConfig(TimeZoneDetectorStatus detectorStatus, TimeZoneCapabilities capabilities, TimeZoneConfiguration configuration) {
        this.mDetectorStatus = (TimeZoneDetectorStatus) Objects.requireNonNull(detectorStatus);
        this.mCapabilities = (TimeZoneCapabilities) Objects.requireNonNull(capabilities);
        this.mConfiguration = (TimeZoneConfiguration) Objects.requireNonNull(configuration);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeZoneCapabilitiesAndConfig createFromParcel(Parcel in) {
        TimeZoneDetectorStatus detectorStatus = (TimeZoneDetectorStatus) in.readParcelable(null, TimeZoneDetectorStatus.class);
        TimeZoneCapabilities capabilities = (TimeZoneCapabilities) in.readParcelable(null, TimeZoneCapabilities.class);
        TimeZoneConfiguration configuration = (TimeZoneConfiguration) in.readParcelable(null, TimeZoneConfiguration.class);
        return new TimeZoneCapabilitiesAndConfig(detectorStatus, capabilities, configuration);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mDetectorStatus, flags);
        dest.writeParcelable(this.mCapabilities, flags);
        dest.writeParcelable(this.mConfiguration, flags);
    }

    public TimeZoneDetectorStatus getDetectorStatus() {
        return this.mDetectorStatus;
    }

    public TimeZoneCapabilities getCapabilities() {
        return this.mCapabilities;
    }

    public TimeZoneConfiguration getConfiguration() {
        return this.mConfiguration;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeZoneCapabilitiesAndConfig that = (TimeZoneCapabilitiesAndConfig) o;
        if (this.mDetectorStatus.equals(that.mDetectorStatus) && this.mCapabilities.equals(that.mCapabilities) && this.mConfiguration.equals(that.mConfiguration)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mCapabilities, this.mConfiguration);
    }

    public String toString() {
        return "TimeZoneCapabilitiesAndConfig{mDetectorStatus=" + this.mDetectorStatus + ", mCapabilities=" + this.mCapabilities + ", mConfiguration=" + this.mConfiguration + '}';
    }
}
