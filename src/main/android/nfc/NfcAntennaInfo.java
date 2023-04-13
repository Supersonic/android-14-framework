package android.nfc;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class NfcAntennaInfo implements Parcelable {
    public static final Parcelable.Creator<NfcAntennaInfo> CREATOR = new Parcelable.Creator<NfcAntennaInfo>() { // from class: android.nfc.NfcAntennaInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NfcAntennaInfo createFromParcel(Parcel in) {
            return new NfcAntennaInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NfcAntennaInfo[] newArray(int size) {
            return new NfcAntennaInfo[size];
        }
    };
    private final List<AvailableNfcAntenna> mAvailableNfcAntennas;
    private final boolean mDeviceFoldable;
    private final int mDeviceHeight;
    private final int mDeviceWidth;

    public NfcAntennaInfo(int deviceWidth, int deviceHeight, boolean deviceFoldable, List<AvailableNfcAntenna> availableNfcAntennas) {
        this.mDeviceWidth = deviceWidth;
        this.mDeviceHeight = deviceHeight;
        this.mDeviceFoldable = deviceFoldable;
        this.mAvailableNfcAntennas = availableNfcAntennas;
    }

    public int getDeviceWidth() {
        return this.mDeviceWidth;
    }

    public int getDeviceHeight() {
        return this.mDeviceHeight;
    }

    public boolean isDeviceFoldable() {
        return this.mDeviceFoldable;
    }

    public List<AvailableNfcAntenna> getAvailableNfcAntennas() {
        return this.mAvailableNfcAntennas;
    }

    private NfcAntennaInfo(Parcel in) {
        this.mDeviceWidth = in.readInt();
        this.mDeviceHeight = in.readInt();
        this.mDeviceFoldable = in.readByte() != 0;
        ArrayList arrayList = new ArrayList();
        this.mAvailableNfcAntennas = arrayList;
        in.readParcelableList(arrayList, AvailableNfcAntenna.class.getClassLoader());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDeviceWidth);
        dest.writeInt(this.mDeviceHeight);
        dest.writeByte(this.mDeviceFoldable ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mAvailableNfcAntennas, 0);
    }
}
