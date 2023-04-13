package android.net;

import android.annotation.SystemApi;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class NetworkKey implements Parcelable {
    public static final Parcelable.Creator<NetworkKey> CREATOR = new Parcelable.Creator<NetworkKey>() { // from class: android.net.NetworkKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkKey createFromParcel(Parcel in) {
            return new NetworkKey(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkKey[] newArray(int size) {
            return new NetworkKey[size];
        }
    };
    private static final String TAG = "NetworkKey";
    public static final int TYPE_WIFI = 1;
    public final int type;
    public final WifiKey wifiKey;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface NetworkType {
    }

    public static NetworkKey createFromScanResult(ScanResult result) {
        Objects.requireNonNull(result);
        String ssid = result.SSID;
        if (TextUtils.isEmpty(ssid) || ssid.equals("<unknown ssid>")) {
            return null;
        }
        String bssid = result.BSSID;
        if (TextUtils.isEmpty(bssid)) {
            return null;
        }
        try {
            WifiKey wifiKey = new WifiKey(String.format("\"%s\"", ssid), bssid);
            return new NetworkKey(wifiKey);
        } catch (IllegalArgumentException e) {
            Log.m109e(TAG, "Unable to create WifiKey.", e);
            return null;
        }
    }

    public static NetworkKey createFromWifiInfo(WifiInfo wifiInfo) {
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            String bssid = wifiInfo.getBSSID();
            if (!TextUtils.isEmpty(ssid) && !ssid.equals("<unknown ssid>") && !TextUtils.isEmpty(bssid)) {
                try {
                    WifiKey wifiKey = new WifiKey(ssid, bssid);
                    return new NetworkKey(wifiKey);
                } catch (IllegalArgumentException e) {
                    Log.m109e(TAG, "Unable to create WifiKey.", e);
                    return null;
                }
            }
        }
        return null;
    }

    public NetworkKey(WifiKey wifiKey) {
        this.type = 1;
        this.wifiKey = wifiKey;
    }

    private NetworkKey(Parcel in) {
        int readInt = in.readInt();
        this.type = readInt;
        switch (readInt) {
            case 1:
                this.wifiKey = WifiKey.CREATOR.createFromParcel(in);
                return;
            default:
                throw new IllegalArgumentException("Parcel has unknown type: " + readInt);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.type);
        switch (this.type) {
            case 1:
                this.wifiKey.writeToParcel(out, flags);
                return;
            default:
                throw new IllegalStateException("NetworkKey has unknown type " + this.type);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkKey that = (NetworkKey) o;
        if (this.type == that.type && Objects.equals(this.wifiKey, that.wifiKey)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.type), this.wifiKey);
    }

    public String toString() {
        switch (this.type) {
            case 1:
                return this.wifiKey.toString();
            default:
                return "InvalidKey";
        }
    }
}
