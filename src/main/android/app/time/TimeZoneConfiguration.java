package android.app.time;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TimeZoneConfiguration implements Parcelable {
    public static final Parcelable.Creator<TimeZoneConfiguration> CREATOR = new Parcelable.Creator<TimeZoneConfiguration>() { // from class: android.app.time.TimeZoneConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneConfiguration createFromParcel(Parcel in) {
            return TimeZoneConfiguration.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneConfiguration[] newArray(int size) {
            return new TimeZoneConfiguration[size];
        }
    };
    private static final String SETTING_AUTO_DETECTION_ENABLED = "autoDetectionEnabled";
    private static final String SETTING_GEO_DETECTION_ENABLED = "geoDetectionEnabled";
    private final Bundle mBundle;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface Setting {
    }

    private TimeZoneConfiguration(Builder builder) {
        this.mBundle = (Bundle) Objects.requireNonNull(builder.mBundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeZoneConfiguration createFromParcel(Parcel in) {
        return new Builder().setPropertyBundleInternal(in.readBundle()).build();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mBundle);
    }

    public boolean isComplete() {
        return hasIsAutoDetectionEnabled() && hasIsGeoDetectionEnabled();
    }

    public boolean isAutoDetectionEnabled() {
        enforceSettingPresent(SETTING_AUTO_DETECTION_ENABLED);
        return this.mBundle.getBoolean(SETTING_AUTO_DETECTION_ENABLED);
    }

    public boolean hasIsAutoDetectionEnabled() {
        return this.mBundle.containsKey(SETTING_AUTO_DETECTION_ENABLED);
    }

    public boolean isGeoDetectionEnabled() {
        enforceSettingPresent(SETTING_GEO_DETECTION_ENABLED);
        return this.mBundle.getBoolean(SETTING_GEO_DETECTION_ENABLED);
    }

    public boolean hasIsGeoDetectionEnabled() {
        return this.mBundle.containsKey(SETTING_GEO_DETECTION_ENABLED);
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
        TimeZoneConfiguration that = (TimeZoneConfiguration) o;
        return this.mBundle.kindofEquals(that.mBundle);
    }

    public int hashCode() {
        return Objects.hash(this.mBundle);
    }

    public String toString() {
        return "TimeZoneConfiguration{mBundle=" + this.mBundle + '}';
    }

    private void enforceSettingPresent(String setting) {
        if (!this.mBundle.containsKey(setting)) {
            throw new IllegalStateException(setting + " is not set");
        }
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private final Bundle mBundle = new Bundle();

        public Builder() {
        }

        public Builder(TimeZoneConfiguration toCopy) {
            mergeProperties(toCopy);
        }

        public Builder mergeProperties(TimeZoneConfiguration other) {
            this.mBundle.putAll(other.mBundle);
            return this;
        }

        Builder setPropertyBundleInternal(Bundle bundle) {
            this.mBundle.putAll(bundle);
            return this;
        }

        public Builder setAutoDetectionEnabled(boolean enabled) {
            this.mBundle.putBoolean(TimeZoneConfiguration.SETTING_AUTO_DETECTION_ENABLED, enabled);
            return this;
        }

        public Builder setGeoDetectionEnabled(boolean enabled) {
            this.mBundle.putBoolean(TimeZoneConfiguration.SETTING_GEO_DETECTION_ENABLED, enabled);
            return this;
        }

        public TimeZoneConfiguration build() {
            return new TimeZoneConfiguration(this);
        }
    }
}
