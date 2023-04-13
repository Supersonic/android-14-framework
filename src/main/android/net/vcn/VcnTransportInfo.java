package android.net.vcn;

import android.net.TransportInfo;
import android.net.wifi.WifiInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public class VcnTransportInfo implements TransportInfo, Parcelable {
    public static final Parcelable.Creator<VcnTransportInfo> CREATOR = new Parcelable.Creator<VcnTransportInfo>() { // from class: android.net.vcn.VcnTransportInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnTransportInfo createFromParcel(Parcel in) {
            int subId = in.readInt();
            WifiInfo wifiInfo = (WifiInfo) in.readParcelable(null, WifiInfo.class);
            int minUdpPort4500NatTimeoutSeconds = in.readInt();
            if (wifiInfo == null && subId == -1 && minUdpPort4500NatTimeoutSeconds == -1) {
                return null;
            }
            return new VcnTransportInfo(wifiInfo, subId, minUdpPort4500NatTimeoutSeconds);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VcnTransportInfo[] newArray(int size) {
            return new VcnTransportInfo[size];
        }
    };
    private final int mMinUdpPort4500NatTimeoutSeconds;
    private final int mSubId;
    private final WifiInfo mWifiInfo;

    public VcnTransportInfo(WifiInfo wifiInfo) {
        this(wifiInfo, -1, -1);
    }

    public VcnTransportInfo(WifiInfo wifiInfo, int minUdpPort4500NatTimeoutSeconds) {
        this(wifiInfo, -1, minUdpPort4500NatTimeoutSeconds);
    }

    public VcnTransportInfo(int subId) {
        this(null, subId, -1);
    }

    public VcnTransportInfo(int subId, int minUdpPort4500NatTimeoutSeconds) {
        this(null, subId, minUdpPort4500NatTimeoutSeconds);
    }

    private VcnTransportInfo(WifiInfo wifiInfo, int subId, int minUdpPort4500NatTimeoutSeconds) {
        this.mWifiInfo = wifiInfo;
        this.mSubId = subId;
        this.mMinUdpPort4500NatTimeoutSeconds = minUdpPort4500NatTimeoutSeconds;
    }

    public WifiInfo getWifiInfo() {
        return this.mWifiInfo;
    }

    public int getSubId() {
        return this.mSubId;
    }

    public int getMinUdpPort4500NatTimeoutSeconds() {
        return this.mMinUdpPort4500NatTimeoutSeconds;
    }

    public int hashCode() {
        return Objects.hash(this.mWifiInfo, Integer.valueOf(this.mSubId), Integer.valueOf(this.mMinUdpPort4500NatTimeoutSeconds));
    }

    public boolean equals(Object o) {
        if (o instanceof VcnTransportInfo) {
            VcnTransportInfo that = (VcnTransportInfo) o;
            return Objects.equals(this.mWifiInfo, that.mWifiInfo) && this.mSubId == that.mSubId && this.mMinUdpPort4500NatTimeoutSeconds == that.mMinUdpPort4500NatTimeoutSeconds;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TransportInfo makeCopy(long redactions) {
        if ((4 & redactions) != 0) {
            return new VcnTransportInfo(null, -1, -1);
        }
        WifiInfo wifiInfo = this.mWifiInfo;
        return new VcnTransportInfo(wifiInfo != null ? wifiInfo.makeCopy(redactions) : null, this.mSubId, this.mMinUdpPort4500NatTimeoutSeconds);
    }

    public long getApplicableRedactions() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            long redactions = 4 | wifiInfo.getApplicableRedactions();
            return redactions;
        }
        return 4L;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSubId);
        dest.writeParcelable(this.mWifiInfo, flags);
        dest.writeInt(this.mMinUdpPort4500NatTimeoutSeconds);
    }

    public String toString() {
        return "VcnTransportInfo { mWifiInfo = " + this.mWifiInfo + ", mSubId = " + this.mSubId + " }";
    }
}
