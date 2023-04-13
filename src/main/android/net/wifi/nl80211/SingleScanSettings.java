package android.net.wifi.nl80211;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SingleScanSettings implements Parcelable {
    public static final Parcelable.Creator<SingleScanSettings> CREATOR = new Parcelable.Creator<SingleScanSettings>() { // from class: android.net.wifi.nl80211.SingleScanSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SingleScanSettings createFromParcel(Parcel in) {
            SingleScanSettings result = new SingleScanSettings();
            result.scanType = in.readInt();
            if (!SingleScanSettings.isValidScanType(result.scanType)) {
                Log.wtf(SingleScanSettings.TAG, "Invalid scan type " + result.scanType);
            }
            result.enable6GhzRnr = in.readBoolean();
            result.channelSettings = new ArrayList<>();
            in.readTypedList(result.channelSettings, ChannelSettings.CREATOR);
            result.hiddenNetworks = new ArrayList<>();
            in.readTypedList(result.hiddenNetworks, HiddenNetwork.CREATOR);
            result.vendorIes = in.createByteArray();
            if (result.vendorIes == null) {
                result.vendorIes = new byte[0];
            }
            if (in.dataAvail() != 0) {
                Log.m110e(SingleScanSettings.TAG, "Found trailing data after parcel parsing.");
            }
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SingleScanSettings[] newArray(int size) {
            return new SingleScanSettings[size];
        }
    };
    private static final String TAG = "SingleScanSettings";
    public ArrayList<ChannelSettings> channelSettings;
    public boolean enable6GhzRnr;
    public ArrayList<HiddenNetwork> hiddenNetworks;
    public int scanType;
    public byte[] vendorIes;

    public boolean equals(Object rhs) {
        SingleScanSettings settings;
        if (this == rhs) {
            return true;
        }
        if ((rhs instanceof SingleScanSettings) && (settings = (SingleScanSettings) rhs) != null) {
            return this.scanType == settings.scanType && this.enable6GhzRnr == settings.enable6GhzRnr && this.channelSettings.equals(settings.channelSettings) && this.hiddenNetworks.equals(settings.hiddenNetworks) && Arrays.equals(this.vendorIes, settings.vendorIes);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.scanType), this.channelSettings, this.hiddenNetworks, Boolean.valueOf(this.enable6GhzRnr), Integer.valueOf(Arrays.hashCode(this.vendorIes)));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidScanType(int scanType) {
        return scanType == 0 || scanType == 1 || scanType == 2;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (!isValidScanType(this.scanType)) {
            Log.wtf(TAG, "Invalid scan type " + this.scanType);
        }
        out.writeInt(this.scanType);
        out.writeBoolean(this.enable6GhzRnr);
        out.writeTypedList(this.channelSettings);
        out.writeTypedList(this.hiddenNetworks);
        byte[] bArr = this.vendorIes;
        if (bArr == null) {
            out.writeByteArray(new byte[0]);
        } else {
            out.writeByteArray(bArr);
        }
    }
}
