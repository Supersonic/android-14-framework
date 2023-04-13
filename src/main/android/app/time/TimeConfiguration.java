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
public final class TimeConfiguration implements Parcelable {
    public static final Parcelable.Creator<TimeConfiguration> CREATOR = new Parcelable.Creator<TimeConfiguration>() { // from class: android.app.time.TimeConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeConfiguration createFromParcel(Parcel source) {
            return TimeConfiguration.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeConfiguration[] newArray(int size) {
            return new TimeConfiguration[size];
        }
    };
    private static final String SETTING_AUTO_DETECTION_ENABLED = "autoDetectionEnabled";
    private final Bundle mBundle;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface Setting {
    }

    private TimeConfiguration(Builder builder) {
        this.mBundle = builder.mBundle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeConfiguration readFromParcel(Parcel in) {
        return new Builder().setPropertyBundleInternal(in.readBundle()).build();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mBundle);
    }

    public boolean isComplete() {
        return hasIsAutoDetectionEnabled();
    }

    public boolean isAutoDetectionEnabled() {
        enforceSettingPresent(SETTING_AUTO_DETECTION_ENABLED);
        return this.mBundle.getBoolean(SETTING_AUTO_DETECTION_ENABLED);
    }

    public boolean hasIsAutoDetectionEnabled() {
        return this.mBundle.containsKey(SETTING_AUTO_DETECTION_ENABLED);
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
        TimeConfiguration that = (TimeConfiguration) o;
        return this.mBundle.kindofEquals(that.mBundle);
    }

    public int hashCode() {
        return Objects.hash(this.mBundle);
    }

    public String toString() {
        return "TimeConfiguration{mBundle=" + this.mBundle + '}';
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

        public Builder(TimeConfiguration toCopy) {
            mergeProperties(toCopy);
        }

        public Builder mergeProperties(TimeConfiguration toCopy) {
            this.mBundle.putAll(toCopy.mBundle);
            return this;
        }

        Builder setPropertyBundleInternal(Bundle bundle) {
            this.mBundle.putAll(bundle);
            return this;
        }

        public Builder setAutoDetectionEnabled(boolean enabled) {
            this.mBundle.putBoolean(TimeConfiguration.SETTING_AUTO_DETECTION_ENABLED, enabled);
            return this;
        }

        public TimeConfiguration build() {
            return new TimeConfiguration(this);
        }
    }
}
