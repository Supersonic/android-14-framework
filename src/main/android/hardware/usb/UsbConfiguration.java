package android.hardware.usb;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
/* loaded from: classes2.dex */
public class UsbConfiguration implements Parcelable {
    private static final int ATTR_REMOTE_WAKEUP = 32;
    private static final int ATTR_SELF_POWERED = 64;
    public static final Parcelable.Creator<UsbConfiguration> CREATOR = new Parcelable.Creator<UsbConfiguration>() { // from class: android.hardware.usb.UsbConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbConfiguration createFromParcel(Parcel in) {
            int id = in.readInt();
            String name = in.readString();
            int attributes = in.readInt();
            int maxPower = in.readInt();
            Parcelable[] interfaces = in.readParcelableArray(UsbInterface.class.getClassLoader());
            UsbConfiguration configuration = new UsbConfiguration(id, name, attributes, maxPower);
            configuration.setInterfaces(interfaces);
            return configuration;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbConfiguration[] newArray(int size) {
            return new UsbConfiguration[size];
        }
    };
    private final int mAttributes;
    private final int mId;
    private Parcelable[] mInterfaces;
    private final int mMaxPower;
    private final String mName;

    public UsbConfiguration(int id, String name, int attributes, int maxPower) {
        this.mId = id;
        this.mName = name;
        this.mAttributes = attributes;
        this.mMaxPower = maxPower;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isSelfPowered() {
        return (this.mAttributes & 64) != 0;
    }

    public boolean isRemoteWakeup() {
        return (this.mAttributes & 32) != 0;
    }

    public int getAttributes() {
        return this.mAttributes;
    }

    public int getMaxPower() {
        return this.mMaxPower * 2;
    }

    public int getInterfaceCount() {
        return this.mInterfaces.length;
    }

    public UsbInterface getInterface(int index) {
        return (UsbInterface) this.mInterfaces[index];
    }

    public void setInterfaces(Parcelable[] interfaces) {
        this.mInterfaces = (Parcelable[]) Preconditions.checkArrayElementsNotNull(interfaces, "interfaces");
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("UsbConfiguration[mId=" + this.mId + ",mName=" + this.mName + ",mAttributes=" + this.mAttributes + ",mMaxPower=" + this.mMaxPower + ",mInterfaces=[");
        for (int i = 0; i < this.mInterfaces.length; i++) {
            builder.append("\n");
            builder.append(this.mInterfaces[i].toString());
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        return builder.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mId);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mAttributes);
        parcel.writeInt(this.mMaxPower);
        parcel.writeParcelableArray(this.mInterfaces, 0);
    }
}
