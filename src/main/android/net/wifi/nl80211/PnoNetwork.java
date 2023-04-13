package android.net.wifi.nl80211;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class PnoNetwork implements Parcelable {
    public static final Parcelable.Creator<PnoNetwork> CREATOR = new Parcelable.Creator<PnoNetwork>() { // from class: android.net.wifi.nl80211.PnoNetwork.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PnoNetwork createFromParcel(Parcel in) {
            PnoNetwork result = new PnoNetwork();
            result.mIsHidden = in.readInt() != 0;
            result.mSsid = in.createByteArray();
            if (result.mSsid == null) {
                result.mSsid = new byte[0];
            }
            result.mFrequencies = in.createIntArray();
            if (result.mFrequencies == null) {
                result.mFrequencies = new int[0];
            }
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PnoNetwork[] newArray(int size) {
            return new PnoNetwork[size];
        }
    };
    private int[] mFrequencies;
    private boolean mIsHidden;
    private byte[] mSsid;

    public boolean isHidden() {
        return this.mIsHidden;
    }

    public void setHidden(boolean isHidden) {
        this.mIsHidden = isHidden;
    }

    public byte[] getSsid() {
        return this.mSsid;
    }

    public void setSsid(byte[] ssid) {
        if (ssid == null) {
            throw new IllegalArgumentException("null argument");
        }
        this.mSsid = ssid;
    }

    public int[] getFrequenciesMhz() {
        return this.mFrequencies;
    }

    public void setFrequenciesMhz(int[] frequenciesMhz) {
        if (frequenciesMhz == null) {
            throw new IllegalArgumentException("null argument");
        }
        this.mFrequencies = frequenciesMhz;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs instanceof PnoNetwork) {
            PnoNetwork network = (PnoNetwork) rhs;
            return Arrays.equals(this.mSsid, network.mSsid) && Arrays.equals(this.mFrequencies, network.mFrequencies) && this.mIsHidden == network.mIsHidden;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mIsHidden), Integer.valueOf(Arrays.hashCode(this.mSsid)), Integer.valueOf(Arrays.hashCode(this.mFrequencies)));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mIsHidden ? 1 : 0);
        out.writeByteArray(this.mSsid);
        out.writeIntArray(this.mFrequencies);
    }
}
