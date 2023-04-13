package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Set;
@SystemApi
/* loaded from: classes2.dex */
public final class KnownNetwork implements Parcelable {
    public static final Parcelable.Creator<KnownNetwork> CREATOR = new Parcelable.Creator<KnownNetwork>() { // from class: android.net.wifi.sharedconnectivity.app.KnownNetwork.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KnownNetwork createFromParcel(Parcel in) {
            return KnownNetwork.readFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KnownNetwork[] newArray(int size) {
            return new KnownNetwork[size];
        }
    };
    public static final int NETWORK_SOURCE_CLOUD_SELF = 2;
    public static final int NETWORK_SOURCE_NEARBY_SELF = 1;
    public static final int NETWORK_SOURCE_UNKNOWN = 0;
    private final NetworkProviderInfo mNetworkProviderInfo;
    private final int mNetworkSource;
    private final ArraySet<Integer> mSecurityTypes;
    private final String mSsid;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface NetworkSource {
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private NetworkProviderInfo mNetworkProviderInfo;
        private int mNetworkSource = -1;
        private final ArraySet<Integer> mSecurityTypes = new ArraySet<>();
        private String mSsid;

        public Builder setNetworkSource(int networkSource) {
            this.mNetworkSource = networkSource;
            return this;
        }

        public Builder setSsid(String ssid) {
            this.mSsid = ssid;
            return this;
        }

        public Builder addSecurityType(int securityType) {
            this.mSecurityTypes.add(Integer.valueOf(securityType));
            return this;
        }

        public Builder setNetworkProviderInfo(NetworkProviderInfo networkProviderInfo) {
            this.mNetworkProviderInfo = networkProviderInfo;
            return this;
        }

        public KnownNetwork build() {
            return new KnownNetwork(this.mNetworkSource, this.mSsid, this.mSecurityTypes, this.mNetworkProviderInfo);
        }
    }

    private static void validate(int networkSource, String ssid, Set<Integer> securityTypes, NetworkProviderInfo networkProviderInfo) {
        if (networkSource != 0 && networkSource != 2 && networkSource != 1) {
            throw new IllegalArgumentException("Illegal network source");
        }
        if (TextUtils.isEmpty(ssid)) {
            throw new IllegalArgumentException("SSID must be set");
        }
        if (securityTypes.isEmpty()) {
            throw new IllegalArgumentException("SecurityTypes must be set");
        }
        if (networkSource == 1 && networkProviderInfo == null) {
            throw new IllegalArgumentException("Device info must be provided when network source is NETWORK_SOURCE_NEARBY_SELF");
        }
    }

    private KnownNetwork(int networkSource, String ssid, ArraySet<Integer> securityTypes, NetworkProviderInfo networkProviderInfo) {
        validate(networkSource, ssid, securityTypes, networkProviderInfo);
        this.mNetworkSource = networkSource;
        this.mSsid = ssid;
        this.mSecurityTypes = new ArraySet<>(securityTypes);
        this.mNetworkProviderInfo = networkProviderInfo;
    }

    public int getNetworkSource() {
        return this.mNetworkSource;
    }

    public String getSsid() {
        return this.mSsid;
    }

    public Set<Integer> getSecurityTypes() {
        return this.mSecurityTypes;
    }

    public NetworkProviderInfo getNetworkProviderInfo() {
        return this.mNetworkProviderInfo;
    }

    public boolean equals(Object obj) {
        if (obj instanceof KnownNetwork) {
            KnownNetwork other = (KnownNetwork) obj;
            return this.mNetworkSource == other.getNetworkSource() && Objects.equals(this.mSsid, other.getSsid()) && Objects.equals(this.mSecurityTypes, other.getSecurityTypes()) && Objects.equals(this.mNetworkProviderInfo, other.getNetworkProviderInfo());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mNetworkSource), this.mSsid, this.mSecurityTypes, this.mNetworkProviderInfo);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mNetworkSource);
        dest.writeString(this.mSsid);
        dest.writeArraySet(this.mSecurityTypes);
        this.mNetworkProviderInfo.writeToParcel(dest, flags);
    }

    public static KnownNetwork readFromParcel(Parcel in) {
        return new KnownNetwork(in.readInt(), in.readString(), in.readArraySet(null), NetworkProviderInfo.readFromParcel(in));
    }

    public String toString() {
        return "KnownNetwork[NetworkSource=" + this.mNetworkSource + ", ssid=" + this.mSsid + ", securityTypes=" + this.mSecurityTypes.toString() + ", networkProviderInfo=" + this.mNetworkProviderInfo.toString() + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
