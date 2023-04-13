package android.hardware.display;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class WifiDisplay implements Parcelable {
    private final boolean mCanConnect;
    private final String mDeviceAddress;
    private final String mDeviceAlias;
    private final String mDeviceName;
    private final boolean mIsAvailable;
    private final boolean mIsRemembered;
    public static final WifiDisplay[] EMPTY_ARRAY = new WifiDisplay[0];
    public static final Parcelable.Creator<WifiDisplay> CREATOR = new Parcelable.Creator<WifiDisplay>() { // from class: android.hardware.display.WifiDisplay.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiDisplay createFromParcel(Parcel in) {
            String deviceAddress = in.readString();
            String deviceName = in.readString();
            String deviceAlias = in.readString();
            boolean isAvailable = in.readInt() != 0;
            boolean canConnect = in.readInt() != 0;
            boolean isRemembered = in.readInt() != 0;
            return new WifiDisplay(deviceAddress, deviceName, deviceAlias, isAvailable, canConnect, isRemembered);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiDisplay[] newArray(int size) {
            return size == 0 ? WifiDisplay.EMPTY_ARRAY : new WifiDisplay[size];
        }
    };

    public WifiDisplay(String deviceAddress, String deviceName, String deviceAlias, boolean available, boolean canConnect, boolean remembered) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        if (deviceName == null) {
            throw new IllegalArgumentException("deviceName must not be null");
        }
        this.mDeviceAddress = deviceAddress;
        this.mDeviceName = deviceName;
        this.mDeviceAlias = deviceAlias;
        this.mIsAvailable = available;
        this.mCanConnect = canConnect;
        this.mIsRemembered = remembered;
    }

    public String getDeviceAddress() {
        return this.mDeviceAddress;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public String getDeviceAlias() {
        return this.mDeviceAlias;
    }

    public boolean isAvailable() {
        return this.mIsAvailable;
    }

    public boolean canConnect() {
        return this.mCanConnect;
    }

    public boolean isRemembered() {
        return this.mIsRemembered;
    }

    public String getFriendlyDisplayName() {
        String str = this.mDeviceAlias;
        return str != null ? str : this.mDeviceName;
    }

    public boolean equals(Object o) {
        return (o instanceof WifiDisplay) && equals((WifiDisplay) o);
    }

    public boolean equals(WifiDisplay other) {
        return other != null && this.mDeviceAddress.equals(other.mDeviceAddress) && this.mDeviceName.equals(other.mDeviceName) && Objects.equals(this.mDeviceAlias, other.mDeviceAlias);
    }

    public boolean hasSameAddress(WifiDisplay other) {
        return other != null && this.mDeviceAddress.equals(other.mDeviceAddress);
    }

    public int hashCode() {
        return this.mDeviceAddress.hashCode();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDeviceAddress);
        dest.writeString(this.mDeviceName);
        dest.writeString(this.mDeviceAlias);
        dest.writeInt(this.mIsAvailable ? 1 : 0);
        dest.writeInt(this.mCanConnect ? 1 : 0);
        dest.writeInt(this.mIsRemembered ? 1 : 0);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        String result = this.mDeviceName + " (" + this.mDeviceAddress + NavigationBarInflaterView.KEY_CODE_END;
        if (this.mDeviceAlias != null) {
            result = result + ", alias " + this.mDeviceAlias;
        }
        return result + ", isAvailable " + this.mIsAvailable + ", canConnect " + this.mCanConnect + ", isRemembered " + this.mIsRemembered;
    }
}
