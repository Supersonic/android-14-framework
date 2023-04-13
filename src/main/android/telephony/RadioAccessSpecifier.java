package android.telephony;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.AccessNetworkConstants;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class RadioAccessSpecifier implements Parcelable {
    public static final Parcelable.Creator<RadioAccessSpecifier> CREATOR = new Parcelable.Creator<RadioAccessSpecifier>() { // from class: android.telephony.RadioAccessSpecifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioAccessSpecifier createFromParcel(Parcel in) {
            return new RadioAccessSpecifier(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioAccessSpecifier[] newArray(int size) {
            return new RadioAccessSpecifier[size];
        }
    };
    private int[] mBands;
    private int[] mChannels;
    private int mRadioAccessNetwork;

    public RadioAccessSpecifier(int ran, int[] bands, int[] channels) {
        this.mRadioAccessNetwork = ran;
        if (bands != null) {
            this.mBands = (int[]) bands.clone();
        } else {
            this.mBands = null;
        }
        if (channels != null) {
            this.mChannels = (int[]) channels.clone();
        } else {
            this.mChannels = null;
        }
    }

    public int getRadioAccessNetwork() {
        return this.mRadioAccessNetwork;
    }

    public int[] getBands() {
        int[] iArr = this.mBands;
        if (iArr == null) {
            return null;
        }
        return (int[]) iArr.clone();
    }

    public int[] getChannels() {
        int[] iArr = this.mChannels;
        if (iArr == null) {
            return null;
        }
        return (int[]) iArr.clone();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRadioAccessNetwork);
        dest.writeIntArray(this.mBands);
        dest.writeIntArray(this.mChannels);
    }

    private RadioAccessSpecifier(Parcel in) {
        this.mRadioAccessNetwork = in.readInt();
        this.mBands = in.createIntArray();
        this.mChannels = in.createIntArray();
    }

    public boolean equals(Object o) {
        try {
            RadioAccessSpecifier ras = (RadioAccessSpecifier) o;
            return o != null && this.mRadioAccessNetwork == ras.mRadioAccessNetwork && Arrays.equals(this.mBands, ras.mBands) && Arrays.equals(this.mChannels, ras.mChannels);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return (this.mRadioAccessNetwork * 31) + (Arrays.hashCode(this.mBands) * 37) + (Arrays.hashCode(this.mChannels) * 39);
    }

    public String toString() {
        return "RadioAccessSpecifier[mRadioAccessNetwork=" + AccessNetworkConstants.AccessNetworkType.toString(this.mRadioAccessNetwork) + ", mBands=" + Arrays.toString(this.mBands) + ", mChannels=" + Arrays.toString(this.mChannels) + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
