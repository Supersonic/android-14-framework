package android.location.provider;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ProviderProperties implements Parcelable {
    public static final int ACCURACY_COARSE = 2;
    public static final int ACCURACY_FINE = 1;
    public static final Parcelable.Creator<ProviderProperties> CREATOR = new Parcelable.Creator<ProviderProperties>() { // from class: android.location.provider.ProviderProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderProperties createFromParcel(Parcel in) {
            return new ProviderProperties(in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderProperties[] newArray(int size) {
            return new ProviderProperties[size];
        }
    };
    public static final int POWER_USAGE_HIGH = 3;
    public static final int POWER_USAGE_LOW = 1;
    public static final int POWER_USAGE_MEDIUM = 2;
    private final int mAccuracy;
    private final boolean mHasAltitudeSupport;
    private final boolean mHasBearingSupport;
    private final boolean mHasCellRequirement;
    private final boolean mHasMonetaryCost;
    private final boolean mHasNetworkRequirement;
    private final boolean mHasSatelliteRequirement;
    private final boolean mHasSpeedSupport;
    private final int mPowerUsage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Accuracy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PowerUsage {
    }

    private ProviderProperties(boolean hasNetworkRequirement, boolean hasSatelliteRequirement, boolean hasCellRequirement, boolean hasMonetaryCost, boolean hasAltitudeSupport, boolean hasSpeedSupport, boolean hasBearingSupport, int powerUsage, int accuracy) {
        this.mHasNetworkRequirement = hasNetworkRequirement;
        this.mHasSatelliteRequirement = hasSatelliteRequirement;
        this.mHasCellRequirement = hasCellRequirement;
        this.mHasMonetaryCost = hasMonetaryCost;
        this.mHasAltitudeSupport = hasAltitudeSupport;
        this.mHasSpeedSupport = hasSpeedSupport;
        this.mHasBearingSupport = hasBearingSupport;
        this.mPowerUsage = powerUsage;
        this.mAccuracy = accuracy;
    }

    public boolean hasNetworkRequirement() {
        return this.mHasNetworkRequirement;
    }

    public boolean hasSatelliteRequirement() {
        return this.mHasSatelliteRequirement;
    }

    public boolean hasCellRequirement() {
        return this.mHasCellRequirement;
    }

    public boolean hasMonetaryCost() {
        return this.mHasMonetaryCost;
    }

    public boolean hasAltitudeSupport() {
        return this.mHasAltitudeSupport;
    }

    public boolean hasSpeedSupport() {
        return this.mHasSpeedSupport;
    }

    public boolean hasBearingSupport() {
        return this.mHasBearingSupport;
    }

    public int getPowerUsage() {
        return this.mPowerUsage;
    }

    public int getAccuracy() {
        return this.mAccuracy;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.mHasNetworkRequirement);
        parcel.writeBoolean(this.mHasSatelliteRequirement);
        parcel.writeBoolean(this.mHasCellRequirement);
        parcel.writeBoolean(this.mHasMonetaryCost);
        parcel.writeBoolean(this.mHasAltitudeSupport);
        parcel.writeBoolean(this.mHasSpeedSupport);
        parcel.writeBoolean(this.mHasBearingSupport);
        parcel.writeInt(this.mPowerUsage);
        parcel.writeInt(this.mAccuracy);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ProviderProperties) {
            ProviderProperties that = (ProviderProperties) o;
            return this.mHasNetworkRequirement == that.mHasNetworkRequirement && this.mHasSatelliteRequirement == that.mHasSatelliteRequirement && this.mHasCellRequirement == that.mHasCellRequirement && this.mHasMonetaryCost == that.mHasMonetaryCost && this.mHasAltitudeSupport == that.mHasAltitudeSupport && this.mHasSpeedSupport == that.mHasSpeedSupport && this.mHasBearingSupport == that.mHasBearingSupport && this.mPowerUsage == that.mPowerUsage && this.mAccuracy == that.mAccuracy;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mHasNetworkRequirement), Boolean.valueOf(this.mHasSatelliteRequirement), Boolean.valueOf(this.mHasCellRequirement), Boolean.valueOf(this.mHasMonetaryCost), Boolean.valueOf(this.mHasAltitudeSupport), Boolean.valueOf(this.mHasSpeedSupport), Boolean.valueOf(this.mHasBearingSupport), Integer.valueOf(this.mPowerUsage), Integer.valueOf(this.mAccuracy));
    }

    public String toString() {
        StringBuilder b = new StringBuilder("ProviderProperties[");
        b.append("powerUsage=").append(powerToString(this.mPowerUsage)).append(", ");
        b.append("accuracy=").append(accuracyToString(this.mAccuracy));
        if (this.mHasNetworkRequirement || this.mHasSatelliteRequirement || this.mHasCellRequirement) {
            b.append(", requires=");
            if (this.mHasNetworkRequirement) {
                b.append("network,");
            }
            if (this.mHasSatelliteRequirement) {
                b.append("satellite,");
            }
            if (this.mHasCellRequirement) {
                b.append("cell,");
            }
            b.setLength(b.length() - 1);
        }
        if (this.mHasMonetaryCost) {
            b.append(", hasMonetaryCost");
        }
        if (this.mHasBearingSupport || this.mHasSpeedSupport || this.mHasAltitudeSupport) {
            b.append(", supports=[");
            if (this.mHasBearingSupport) {
                b.append("bearing,");
            }
            if (this.mHasSpeedSupport) {
                b.append("speed,");
            }
            if (this.mHasAltitudeSupport) {
                b.append("altitude,");
            }
            b.setLength(b.length() - 1);
            b.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        b.append(NavigationBarInflaterView.SIZE_MOD_END);
        return b.toString();
    }

    private static String powerToString(int power) {
        switch (power) {
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                throw new AssertionError();
        }
    }

    private static String accuracyToString(int accuracy) {
        switch (accuracy) {
            case 1:
                return "Fine";
            case 2:
                return "Coarse";
            default:
                throw new AssertionError();
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAccuracy;
        private boolean mHasAltitudeSupport;
        private boolean mHasBearingSupport;
        private boolean mHasCellRequirement;
        private boolean mHasMonetaryCost;
        private boolean mHasNetworkRequirement;
        private boolean mHasSatelliteRequirement;
        private boolean mHasSpeedSupport;
        private int mPowerUsage;

        public Builder() {
            this.mHasNetworkRequirement = false;
            this.mHasSatelliteRequirement = false;
            this.mHasCellRequirement = false;
            this.mHasMonetaryCost = false;
            this.mHasAltitudeSupport = false;
            this.mHasSpeedSupport = false;
            this.mHasBearingSupport = false;
            this.mPowerUsage = 3;
            this.mAccuracy = 2;
        }

        public Builder(ProviderProperties providerProperties) {
            this.mHasNetworkRequirement = providerProperties.mHasNetworkRequirement;
            this.mHasSatelliteRequirement = providerProperties.mHasSatelliteRequirement;
            this.mHasCellRequirement = providerProperties.mHasCellRequirement;
            this.mHasMonetaryCost = providerProperties.mHasMonetaryCost;
            this.mHasAltitudeSupport = providerProperties.mHasAltitudeSupport;
            this.mHasSpeedSupport = providerProperties.mHasSpeedSupport;
            this.mHasBearingSupport = providerProperties.mHasBearingSupport;
            this.mPowerUsage = providerProperties.mPowerUsage;
            this.mAccuracy = providerProperties.mAccuracy;
        }

        public Builder setHasNetworkRequirement(boolean requiresNetwork) {
            this.mHasNetworkRequirement = requiresNetwork;
            return this;
        }

        public Builder setHasSatelliteRequirement(boolean requiresSatellite) {
            this.mHasSatelliteRequirement = requiresSatellite;
            return this;
        }

        public Builder setHasCellRequirement(boolean requiresCell) {
            this.mHasCellRequirement = requiresCell;
            return this;
        }

        public Builder setHasMonetaryCost(boolean monetaryCost) {
            this.mHasMonetaryCost = monetaryCost;
            return this;
        }

        public Builder setHasAltitudeSupport(boolean supportsAltitude) {
            this.mHasAltitudeSupport = supportsAltitude;
            return this;
        }

        public Builder setHasSpeedSupport(boolean supportsSpeed) {
            this.mHasSpeedSupport = supportsSpeed;
            return this;
        }

        public Builder setHasBearingSupport(boolean supportsBearing) {
            this.mHasBearingSupport = supportsBearing;
            return this;
        }

        public Builder setPowerUsage(int powerUsage) {
            this.mPowerUsage = Preconditions.checkArgumentInRange(powerUsage, 1, 3, "powerUsage");
            return this;
        }

        public Builder setAccuracy(int accuracy) {
            this.mAccuracy = Preconditions.checkArgumentInRange(accuracy, 1, 2, "accuracy");
            return this;
        }

        public ProviderProperties build() {
            return new ProviderProperties(this.mHasNetworkRequirement, this.mHasSatelliteRequirement, this.mHasCellRequirement, this.mHasMonetaryCost, this.mHasAltitudeSupport, this.mHasSpeedSupport, this.mHasBearingSupport, this.mPowerUsage, this.mAccuracy);
        }
    }
}
