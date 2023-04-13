package android.net.wifi.nl80211;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.util.Objects;
/* loaded from: classes2.dex */
public class ChannelSettings implements Parcelable {
    public static final Parcelable.Creator<ChannelSettings> CREATOR = new Parcelable.Creator<ChannelSettings>() { // from class: android.net.wifi.nl80211.ChannelSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ChannelSettings createFromParcel(Parcel in) {
            ChannelSettings result = new ChannelSettings();
            result.frequency = in.readInt();
            if (in.dataAvail() != 0) {
                Log.m110e(ChannelSettings.TAG, "Found trailing data after parcel parsing.");
            }
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ChannelSettings[] newArray(int size) {
            return new ChannelSettings[size];
        }
    };
    private static final String TAG = "ChannelSettings";
    public int frequency;

    public boolean equals(Object rhs) {
        ChannelSettings channel;
        if (this == rhs) {
            return true;
        }
        return (rhs instanceof ChannelSettings) && (channel = (ChannelSettings) rhs) != null && this.frequency == channel.frequency;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.frequency));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.frequency);
    }
}
