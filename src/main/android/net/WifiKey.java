package android.net;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.regex.Pattern;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class WifiKey implements Parcelable {
    public final String bssid;
    public final String ssid;
    private static final Pattern SSID_PATTERN = Pattern.compile("(\".*\")|(0x[\\p{XDigit}]+)", 32);
    private static final Pattern BSSID_PATTERN = Pattern.compile("([\\p{XDigit}]{2}:){5}[\\p{XDigit}]{2}");
    public static final Parcelable.Creator<WifiKey> CREATOR = new Parcelable.Creator<WifiKey>() { // from class: android.net.WifiKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiKey createFromParcel(Parcel in) {
            return new WifiKey(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiKey[] newArray(int size) {
            return new WifiKey[size];
        }
    };

    public WifiKey(String ssid, String bssid) {
        if (ssid == null || !SSID_PATTERN.matcher(ssid).matches()) {
            throw new IllegalArgumentException("Invalid ssid: " + ssid);
        }
        if (bssid == null || !BSSID_PATTERN.matcher(bssid).matches()) {
            throw new IllegalArgumentException("Invalid bssid: " + bssid);
        }
        this.ssid = ssid;
        this.bssid = bssid;
    }

    private WifiKey(Parcel in) {
        this.ssid = in.readString();
        this.bssid = in.readString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.ssid);
        out.writeString(this.bssid);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WifiKey wifiKey = (WifiKey) o;
        if (Objects.equals(this.ssid, wifiKey.ssid) && Objects.equals(this.bssid, wifiKey.bssid)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.ssid, this.bssid);
    }

    public String toString() {
        return "WifiKey[SSID=" + this.ssid + ",BSSID=" + this.bssid + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
