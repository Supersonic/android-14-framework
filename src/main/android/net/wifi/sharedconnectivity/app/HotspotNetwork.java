package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Set;
@SystemApi
/* loaded from: classes2.dex */
public final class HotspotNetwork implements Parcelable {
    public static final Parcelable.Creator<HotspotNetwork> CREATOR = new Parcelable.Creator<HotspotNetwork>() { // from class: android.net.wifi.sharedconnectivity.app.HotspotNetwork.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotspotNetwork createFromParcel(Parcel in) {
            return HotspotNetwork.readFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotspotNetwork[] newArray(int size) {
            return new HotspotNetwork[size];
        }
    };
    public static final int NETWORK_TYPE_CELLULAR = 1;
    public static final int NETWORK_TYPE_ETHERNET = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 2;
    private final long mDeviceId;
    private final String mHotspotBssid;
    private final ArraySet<Integer> mHotspotSecurityTypes;
    private final String mHotspotSsid;
    private final String mNetworkName;
    private final NetworkProviderInfo mNetworkProviderInfo;
    private final int mNetworkType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface NetworkType {
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private String mHotspotBssid;
        private String mHotspotSsid;
        private String mNetworkName;
        private NetworkProviderInfo mNetworkProviderInfo;
        private int mNetworkType;
        private long mDeviceId = -1;
        private final ArraySet<Integer> mHotspotSecurityTypes = new ArraySet<>();

        public Builder setDeviceId(long deviceId) {
            this.mDeviceId = deviceId;
            return this;
        }

        public Builder setNetworkProviderInfo(NetworkProviderInfo networkProviderInfo) {
            this.mNetworkProviderInfo = networkProviderInfo;
            return this;
        }

        public Builder setHostNetworkType(int networkType) {
            this.mNetworkType = networkType;
            return this;
        }

        public Builder setNetworkName(String networkName) {
            this.mNetworkName = networkName;
            return this;
        }

        public Builder setHotspotSsid(String hotspotSsid) {
            this.mHotspotSsid = hotspotSsid;
            return this;
        }

        public Builder setHotspotBssid(String hotspotBssid) {
            this.mHotspotBssid = hotspotBssid;
            return this;
        }

        public Builder addHotspotSecurityType(int hotspotSecurityType) {
            this.mHotspotSecurityTypes.add(Integer.valueOf(hotspotSecurityType));
            return this;
        }

        public HotspotNetwork build() {
            return new HotspotNetwork(this.mDeviceId, this.mNetworkProviderInfo, this.mNetworkType, this.mNetworkName, this.mHotspotSsid, this.mHotspotBssid, this.mHotspotSecurityTypes);
        }
    }

    private static void validate(long deviceId, int networkType, String networkName) {
        if (deviceId < 0) {
            throw new IllegalArgumentException("DeviceId must be set");
        }
        if (networkType != 1 && networkType != 2 && networkType != 3 && networkType != 0) {
            throw new IllegalArgumentException("Illegal network type");
        }
        if (Objects.isNull(networkName)) {
            throw new IllegalArgumentException("NetworkName must be set");
        }
    }

    private HotspotNetwork(long deviceId, NetworkProviderInfo networkProviderInfo, int networkType, String networkName, String hotspotSsid, String hotspotBssid, ArraySet<Integer> hotspotSecurityTypes) {
        validate(deviceId, networkType, networkName);
        this.mDeviceId = deviceId;
        this.mNetworkProviderInfo = networkProviderInfo;
        this.mNetworkType = networkType;
        this.mNetworkName = networkName;
        this.mHotspotSsid = hotspotSsid;
        this.mHotspotBssid = hotspotBssid;
        this.mHotspotSecurityTypes = new ArraySet<>(hotspotSecurityTypes);
    }

    public long getDeviceId() {
        return this.mDeviceId;
    }

    public NetworkProviderInfo getNetworkProviderInfo() {
        return this.mNetworkProviderInfo;
    }

    public int getHostNetworkType() {
        return this.mNetworkType;
    }

    public String getNetworkName() {
        return this.mNetworkName;
    }

    public String getHotspotSsid() {
        return this.mHotspotSsid;
    }

    public String getHotspotBssid() {
        return this.mHotspotBssid;
    }

    public Set<Integer> getHotspotSecurityTypes() {
        return this.mHotspotSecurityTypes;
    }

    public boolean equals(Object obj) {
        if (obj instanceof HotspotNetwork) {
            HotspotNetwork other = (HotspotNetwork) obj;
            return this.mDeviceId == other.getDeviceId() && Objects.equals(this.mNetworkProviderInfo, other.getNetworkProviderInfo()) && this.mNetworkType == other.getHostNetworkType() && Objects.equals(this.mNetworkName, other.getNetworkName()) && Objects.equals(this.mHotspotSsid, other.getHotspotSsid()) && Objects.equals(this.mHotspotBssid, other.getHotspotBssid()) && Objects.equals(this.mHotspotSecurityTypes, other.getHotspotSecurityTypes());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mDeviceId), this.mNetworkProviderInfo, this.mNetworkName, this.mHotspotSsid, this.mHotspotBssid, this.mHotspotSecurityTypes);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mDeviceId);
        this.mNetworkProviderInfo.writeToParcel(dest, flags);
        dest.writeInt(this.mNetworkType);
        dest.writeString(this.mNetworkName);
        dest.writeString(this.mHotspotSsid);
        dest.writeString(this.mHotspotBssid);
        dest.writeArraySet(this.mHotspotSecurityTypes);
    }

    public static HotspotNetwork readFromParcel(Parcel in) {
        return new HotspotNetwork(in.readLong(), NetworkProviderInfo.readFromParcel(in), in.readInt(), in.readString(), in.readString(), in.readString(), in.readArraySet(null));
    }

    public String toString() {
        return "HotspotNetwork[deviceId=" + this.mDeviceId + ", networkType=" + this.mNetworkType + ", networkProviderInfo=" + this.mNetworkProviderInfo.toString() + ", networkName=" + this.mNetworkName + ", hotspotSsid=" + this.mHotspotSsid + ", hotspotBssid=" + this.mHotspotBssid + ", hotspotSecurityTypes=" + this.mHotspotSecurityTypes.toString() + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
