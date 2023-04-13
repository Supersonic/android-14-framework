package android.companion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AssociatedDevice implements Parcelable {
    private static final int BLUETOOTH_LE = 1;
    private static final int CLASSIC_BLUETOOTH = 0;
    public static final Parcelable.Creator<AssociatedDevice> CREATOR = new Parcelable.Creator<AssociatedDevice>() { // from class: android.companion.AssociatedDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AssociatedDevice[] newArray(int size) {
            return new AssociatedDevice[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AssociatedDevice createFromParcel(Parcel in) {
            return new AssociatedDevice(in);
        }
    };
    private static final int WIFI = 2;
    private final Parcelable mDevice;

    public AssociatedDevice(Parcelable device) {
        this.mDevice = device;
    }

    private AssociatedDevice(Parcel in) {
        Parcelable.Creator<? extends Parcelable> creator = getDeviceCreator(in.readInt());
        this.mDevice = creator.createFromParcel(in);
    }

    public BluetoothDevice getBluetoothDevice() {
        Parcelable parcelable = this.mDevice;
        if (parcelable instanceof BluetoothDevice) {
            return (BluetoothDevice) parcelable;
        }
        return null;
    }

    public ScanResult getBleDevice() {
        Parcelable parcelable = this.mDevice;
        if (parcelable instanceof ScanResult) {
            return (ScanResult) parcelable;
        }
        return null;
    }

    public android.net.wifi.ScanResult getWifiDevice() {
        Parcelable parcelable = this.mDevice;
        if (parcelable instanceof android.net.wifi.ScanResult) {
            return (android.net.wifi.ScanResult) parcelable;
        }
        return null;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getDeviceType());
        this.mDevice.writeToParcel(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private int getDeviceType() {
        Parcelable parcelable = this.mDevice;
        if (parcelable instanceof BluetoothDevice) {
            return 0;
        }
        if (parcelable instanceof ScanResult) {
            return 1;
        }
        if (parcelable instanceof android.net.wifi.ScanResult) {
            return 2;
        }
        throw new UnsupportedOperationException("Unsupported device type.");
    }

    private static Parcelable.Creator<? extends Parcelable> getDeviceCreator(int deviceType) {
        switch (deviceType) {
            case 0:
                return BluetoothDevice.CREATOR;
            case 1:
                return ScanResult.CREATOR;
            case 2:
                return android.net.wifi.ScanResult.CREATOR;
            default:
                throw new UnsupportedOperationException("Unsupported device type.");
        }
    }

    public String toString() {
        return "AssociatedDevice { device = " + this.mDevice + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssociatedDevice that = (AssociatedDevice) o;
        if (getDeviceType() != that.getDeviceType()) {
            return false;
        }
        Parcelable parcelable = this.mDevice;
        if ((parcelable instanceof ScanResult) || (parcelable instanceof android.net.wifi.ScanResult)) {
            return parcelable.toString().equals(that.mDevice.toString());
        }
        return Objects.equals(parcelable, that.mDevice);
    }

    public int hashCode() {
        return Objects.hash(this.mDevice);
    }
}
