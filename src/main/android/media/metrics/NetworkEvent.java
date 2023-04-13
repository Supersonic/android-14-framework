package android.media.metrics;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NetworkEvent extends Event implements Parcelable {
    public static final Parcelable.Creator<NetworkEvent> CREATOR = new Parcelable.Creator<NetworkEvent>() { // from class: android.media.metrics.NetworkEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkEvent[] newArray(int size) {
            return new NetworkEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkEvent createFromParcel(Parcel in) {
            return new NetworkEvent(in);
        }
    };
    public static final int NETWORK_TYPE_2G = 4;
    public static final int NETWORK_TYPE_3G = 5;
    public static final int NETWORK_TYPE_4G = 6;
    public static final int NETWORK_TYPE_5G_NSA = 7;
    public static final int NETWORK_TYPE_5G_SA = 8;
    public static final int NETWORK_TYPE_ETHERNET = 3;
    public static final int NETWORK_TYPE_OFFLINE = 9;
    public static final int NETWORK_TYPE_OTHER = 1;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 2;
    private final int mNetworkType;
    private final long mTimeSinceCreatedMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface NetworkType {
    }

    public static String networkTypeToString(int value) {
        switch (value) {
            case 0:
                return "NETWORK_TYPE_UNKNOWN";
            case 1:
                return "NETWORK_TYPE_OTHER";
            case 2:
                return "NETWORK_TYPE_WIFI";
            case 3:
                return "NETWORK_TYPE_ETHERNET";
            case 4:
                return "NETWORK_TYPE_2G";
            case 5:
                return "NETWORK_TYPE_3G";
            case 6:
                return "NETWORK_TYPE_4G";
            case 7:
                return "NETWORK_TYPE_5G_NSA";
            case 8:
                return "NETWORK_TYPE_5G_SA";
            case 9:
                return "NETWORK_TYPE_OFFLINE";
            default:
                return Integer.toHexString(value);
        }
    }

    private NetworkEvent(int type, long timeSinceCreatedMillis, Bundle extras) {
        this.mNetworkType = type;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mMetricsBundle = extras == null ? null : extras.deepCopy();
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    @Override // android.media.metrics.Event
    public long getTimeSinceCreatedMillis() {
        return this.mTimeSinceCreatedMillis;
    }

    @Override // android.media.metrics.Event
    public Bundle getMetricsBundle() {
        return this.mMetricsBundle;
    }

    public String toString() {
        return "NetworkEvent { networkType = " + this.mNetworkType + ", timeSinceCreatedMillis = " + this.mTimeSinceCreatedMillis + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkEvent that = (NetworkEvent) o;
        if (this.mNetworkType == that.mNetworkType && this.mTimeSinceCreatedMillis == that.mTimeSinceCreatedMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mNetworkType), Long.valueOf(this.mTimeSinceCreatedMillis));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mNetworkType);
        dest.writeLong(this.mTimeSinceCreatedMillis);
        dest.writeBundle(this.mMetricsBundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private NetworkEvent(Parcel in) {
        int type = in.readInt();
        long timeSinceCreatedMillis = in.readLong();
        Bundle extras = in.readBundle();
        this.mNetworkType = type;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mMetricsBundle = extras;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mNetworkType = 0;
        private long mTimeSinceCreatedMillis = -1;
        private Bundle mMetricsBundle = new Bundle();

        public Builder setNetworkType(int value) {
            this.mNetworkType = value;
            return this;
        }

        public Builder setTimeSinceCreatedMillis(long value) {
            this.mTimeSinceCreatedMillis = value;
            return this;
        }

        public Builder setMetricsBundle(Bundle metricsBundle) {
            this.mMetricsBundle = metricsBundle;
            return this;
        }

        public NetworkEvent build() {
            NetworkEvent o = new NetworkEvent(this.mNetworkType, this.mTimeSinceCreatedMillis, this.mMetricsBundle);
            return o;
        }
    }
}
