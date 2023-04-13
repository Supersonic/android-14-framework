package android.app.time;

import android.annotation.SystemApi;
import android.app.time.TimeConfiguration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TimeCapabilities implements Parcelable {
    public static final Parcelable.Creator<TimeCapabilities> CREATOR = new Parcelable.Creator<TimeCapabilities>() { // from class: android.app.time.TimeCapabilities.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeCapabilities createFromParcel(Parcel in) {
            return TimeCapabilities.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeCapabilities[] newArray(int size) {
            return new TimeCapabilities[size];
        }
    };
    private final int mConfigureAutoDetectionEnabledCapability;
    private final int mSetManualTimeCapability;
    private final UserHandle mUserHandle;

    private TimeCapabilities(Builder builder) {
        this.mUserHandle = (UserHandle) Objects.requireNonNull(builder.mUserHandle);
        this.mConfigureAutoDetectionEnabledCapability = builder.mConfigureAutoDetectionEnabledCapability;
        this.mSetManualTimeCapability = builder.mSetManualTimeCapability;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeCapabilities createFromParcel(Parcel in) {
        UserHandle userHandle = UserHandle.readFromParcel(in);
        return new Builder(userHandle).setConfigureAutoDetectionEnabledCapability(in.readInt()).setSetManualTimeCapability(in.readInt()).build();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        UserHandle.writeToParcel(this.mUserHandle, dest);
        dest.writeInt(this.mConfigureAutoDetectionEnabledCapability);
        dest.writeInt(this.mSetManualTimeCapability);
    }

    public int getConfigureAutoDetectionEnabledCapability() {
        return this.mConfigureAutoDetectionEnabledCapability;
    }

    public int getSetManualTimeCapability() {
        return this.mSetManualTimeCapability;
    }

    public TimeConfiguration tryApplyConfigChanges(TimeConfiguration config, TimeConfiguration requestedChanges) {
        TimeConfiguration.Builder newConfigBuilder = new TimeConfiguration.Builder(config);
        if (requestedChanges.hasIsAutoDetectionEnabled()) {
            if (getConfigureAutoDetectionEnabledCapability() < 30) {
                return null;
            }
            newConfigBuilder.setAutoDetectionEnabled(requestedChanges.isAutoDetectionEnabled());
        }
        return newConfigBuilder.build();
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
        TimeCapabilities that = (TimeCapabilities) o;
        if (this.mConfigureAutoDetectionEnabledCapability == that.mConfigureAutoDetectionEnabledCapability && this.mSetManualTimeCapability == that.mSetManualTimeCapability && this.mUserHandle.equals(that.mUserHandle)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mUserHandle, Integer.valueOf(this.mConfigureAutoDetectionEnabledCapability), Integer.valueOf(this.mSetManualTimeCapability));
    }

    public String toString() {
        return "TimeCapabilities{mUserHandle=" + this.mUserHandle + ", mConfigureAutoDetectionEnabledCapability=" + this.mConfigureAutoDetectionEnabledCapability + ", mSetManualTimeCapability=" + this.mSetManualTimeCapability + '}';
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private int mConfigureAutoDetectionEnabledCapability;
        private int mSetManualTimeCapability;
        private final UserHandle mUserHandle;

        public Builder(UserHandle userHandle) {
            this.mUserHandle = (UserHandle) Objects.requireNonNull(userHandle);
        }

        public Builder(TimeCapabilities timeCapabilities) {
            Objects.requireNonNull(timeCapabilities);
            this.mUserHandle = timeCapabilities.mUserHandle;
            this.mConfigureAutoDetectionEnabledCapability = timeCapabilities.mConfigureAutoDetectionEnabledCapability;
            this.mSetManualTimeCapability = timeCapabilities.mSetManualTimeCapability;
        }

        public Builder setConfigureAutoDetectionEnabledCapability(int value) {
            this.mConfigureAutoDetectionEnabledCapability = value;
            return this;
        }

        public Builder setSetManualTimeCapability(int value) {
            this.mSetManualTimeCapability = value;
            return this;
        }

        public TimeCapabilities build() {
            verifyCapabilitySet(this.mConfigureAutoDetectionEnabledCapability, "configureAutoDetectionEnabledCapability");
            verifyCapabilitySet(this.mSetManualTimeCapability, "mSetManualTimeCapability");
            return new TimeCapabilities(this);
        }

        private void verifyCapabilitySet(int value, String name) {
            if (value == 0) {
                throw new IllegalStateException(name + " was not set");
            }
        }
    }
}
