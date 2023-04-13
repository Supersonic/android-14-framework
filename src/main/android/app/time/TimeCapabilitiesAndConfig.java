package android.app.time;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TimeCapabilitiesAndConfig implements Parcelable {
    public static final Parcelable.Creator<TimeCapabilitiesAndConfig> CREATOR = new Parcelable.Creator<TimeCapabilitiesAndConfig>() { // from class: android.app.time.TimeCapabilitiesAndConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeCapabilitiesAndConfig createFromParcel(Parcel source) {
            return TimeCapabilitiesAndConfig.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeCapabilitiesAndConfig[] newArray(int size) {
            return new TimeCapabilitiesAndConfig[size];
        }
    };
    private final TimeCapabilities mCapabilities;
    private final TimeConfiguration mConfiguration;

    public TimeCapabilitiesAndConfig(TimeCapabilities timeCapabilities, TimeConfiguration timeConfiguration) {
        this.mCapabilities = (TimeCapabilities) Objects.requireNonNull(timeCapabilities);
        this.mConfiguration = (TimeConfiguration) Objects.requireNonNull(timeConfiguration);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeCapabilitiesAndConfig readFromParcel(Parcel in) {
        TimeCapabilities capabilities = (TimeCapabilities) in.readParcelable(null, TimeCapabilities.class);
        TimeConfiguration configuration = (TimeConfiguration) in.readParcelable(null, TimeConfiguration.class);
        return new TimeCapabilitiesAndConfig(capabilities, configuration);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mCapabilities, flags);
        dest.writeParcelable(this.mConfiguration, flags);
    }

    public TimeCapabilities getCapabilities() {
        return this.mCapabilities;
    }

    public TimeConfiguration getConfiguration() {
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
        TimeCapabilitiesAndConfig that = (TimeCapabilitiesAndConfig) o;
        if (this.mCapabilities.equals(that.mCapabilities) && this.mConfiguration.equals(that.mConfiguration)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mCapabilities, this.mConfiguration);
    }

    public String toString() {
        return "TimeCapabilitiesAndConfig{mCapabilities=" + this.mCapabilities + ", mConfiguration=" + this.mConfiguration + '}';
    }
}
