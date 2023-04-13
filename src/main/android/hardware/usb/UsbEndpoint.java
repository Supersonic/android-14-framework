package android.hardware.usb;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public class UsbEndpoint implements Parcelable {
    public static final Parcelable.Creator<UsbEndpoint> CREATOR = new Parcelable.Creator<UsbEndpoint>() { // from class: android.hardware.usb.UsbEndpoint.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbEndpoint createFromParcel(Parcel in) {
            int address = in.readInt();
            int attributes = in.readInt();
            int maxPacketSize = in.readInt();
            int interval = in.readInt();
            return new UsbEndpoint(address, attributes, maxPacketSize, interval);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbEndpoint[] newArray(int size) {
            return new UsbEndpoint[size];
        }
    };
    private final int mAddress;
    private final int mAttributes;
    private final int mInterval;
    private final int mMaxPacketSize;

    public UsbEndpoint(int address, int attributes, int maxPacketSize, int interval) {
        this.mAddress = address;
        this.mAttributes = attributes;
        this.mMaxPacketSize = maxPacketSize;
        this.mInterval = interval;
    }

    public int getAddress() {
        return this.mAddress;
    }

    public int getEndpointNumber() {
        return this.mAddress & 15;
    }

    public int getDirection() {
        return this.mAddress & 128;
    }

    public int getAttributes() {
        return this.mAttributes;
    }

    public int getType() {
        return this.mAttributes & 3;
    }

    public int getMaxPacketSize() {
        return this.mMaxPacketSize;
    }

    public int getInterval() {
        return this.mInterval;
    }

    public String toString() {
        return "UsbEndpoint[mAddress=" + this.mAddress + ",mAttributes=" + this.mAttributes + ",mMaxPacketSize=" + this.mMaxPacketSize + ",mInterval=" + this.mInterval + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mAddress);
        parcel.writeInt(this.mAttributes);
        parcel.writeInt(this.mMaxPacketSize);
        parcel.writeInt(this.mInterval);
    }
}
