package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class NetworkProviderInfo implements Parcelable {
    public static final Parcelable.Creator<NetworkProviderInfo> CREATOR = new Parcelable.Creator<NetworkProviderInfo>() { // from class: android.net.wifi.sharedconnectivity.app.NetworkProviderInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkProviderInfo createFromParcel(Parcel in) {
            return NetworkProviderInfo.readFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkProviderInfo[] newArray(int size) {
            return new NetworkProviderInfo[size];
        }
    };
    public static final int DEVICE_TYPE_AUTO = 5;
    public static final int DEVICE_TYPE_LAPTOP = 3;
    public static final int DEVICE_TYPE_PHONE = 1;
    public static final int DEVICE_TYPE_TABLET = 2;
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    public static final int DEVICE_TYPE_WATCH = 4;
    private final int mBatteryPercentage;
    private final int mConnectionStrength;
    private final String mDeviceName;
    private final int mDeviceType;
    private final String mModelName;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface DeviceType {
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mBatteryPercentage;
        private int mConnectionStrength;
        private String mDeviceName;
        private int mDeviceType;
        private String mModelName;

        public Builder(String deviceName, String modelName) {
            Objects.requireNonNull(deviceName);
            Objects.requireNonNull(modelName);
            this.mDeviceName = deviceName;
            this.mModelName = modelName;
        }

        public Builder setDeviceType(int deviceType) {
            this.mDeviceType = deviceType;
            return this;
        }

        public Builder setDeviceName(String deviceName) {
            Objects.requireNonNull(deviceName);
            this.mDeviceName = deviceName;
            return this;
        }

        public Builder setModelName(String modelName) {
            Objects.requireNonNull(modelName);
            this.mModelName = modelName;
            return this;
        }

        public Builder setBatteryPercentage(int batteryPercentage) {
            this.mBatteryPercentage = batteryPercentage;
            return this;
        }

        public Builder setConnectionStrength(int connectionStrength) {
            this.mConnectionStrength = connectionStrength;
            return this;
        }

        public NetworkProviderInfo build() {
            return new NetworkProviderInfo(this.mDeviceType, this.mDeviceName, this.mModelName, this.mBatteryPercentage, this.mConnectionStrength);
        }
    }

    private static void validate(int deviceType, String deviceName, String modelName, int batteryPercentage, int connectionStrength) {
        if (deviceType != 0 && deviceType != 1 && deviceType != 2 && deviceType != 3 && deviceType != 4 && deviceType != 5) {
            throw new IllegalArgumentException("Illegal device type");
        }
        if (batteryPercentage < 0 || batteryPercentage > 100) {
            throw new IllegalArgumentException("BatteryPercentage must be in range 0-100");
        }
        if (connectionStrength < 0 || connectionStrength > 3) {
            throw new IllegalArgumentException("ConnectionStrength must be in range 0-3");
        }
    }

    private NetworkProviderInfo(int deviceType, String deviceName, String modelName, int batteryPercentage, int connectionStrength) {
        validate(deviceType, deviceName, modelName, batteryPercentage, connectionStrength);
        this.mDeviceType = deviceType;
        this.mDeviceName = deviceName;
        this.mModelName = modelName;
        this.mBatteryPercentage = batteryPercentage;
        this.mConnectionStrength = connectionStrength;
    }

    public int getDeviceType() {
        return this.mDeviceType;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public String getModelName() {
        return this.mModelName;
    }

    public int getBatteryPercentage() {
        return this.mBatteryPercentage;
    }

    public int getConnectionStrength() {
        return this.mConnectionStrength;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NetworkProviderInfo) {
            NetworkProviderInfo other = (NetworkProviderInfo) obj;
            return this.mDeviceType == other.getDeviceType() && Objects.equals(this.mDeviceName, other.mDeviceName) && Objects.equals(this.mModelName, other.mModelName) && this.mBatteryPercentage == other.mBatteryPercentage && this.mConnectionStrength == other.mConnectionStrength;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDeviceType), this.mDeviceName, this.mModelName, Integer.valueOf(this.mBatteryPercentage), Integer.valueOf(this.mConnectionStrength));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDeviceType);
        dest.writeString(this.mDeviceName);
        dest.writeString(this.mModelName);
        dest.writeInt(this.mBatteryPercentage);
        dest.writeInt(this.mConnectionStrength);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static NetworkProviderInfo readFromParcel(Parcel in) {
        return new NetworkProviderInfo(in.readInt(), in.readString(), in.readString(), in.readInt(), in.readInt());
    }

    public String toString() {
        return "NetworkProviderInfo[deviceType=" + this.mDeviceType + ", deviceName=" + this.mDeviceName + ", modelName=" + this.mModelName + ", batteryPercentage=" + this.mBatteryPercentage + ", connectionStrength=" + this.mConnectionStrength + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
