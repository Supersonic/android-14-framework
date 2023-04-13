package android.companion.virtual;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class VirtualDevice implements Parcelable {
    public static final Parcelable.Creator<VirtualDevice> CREATOR = new Parcelable.Creator<VirtualDevice>() { // from class: android.companion.virtual.VirtualDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDevice createFromParcel(Parcel in) {
            return new VirtualDevice(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDevice[] newArray(int size) {
            return new VirtualDevice[size];
        }
    };
    private final int mId;
    private final String mName;

    public VirtualDevice(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("VirtualDevice ID mist be greater than 0");
        }
        this.mId = id;
        this.mName = name;
    }

    private VirtualDevice(Parcel parcel) {
        this.mId = parcel.readInt();
        this.mName = parcel.readString8();
    }

    public int getDeviceId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString8(this.mName);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VirtualDevice) {
            VirtualDevice that = (VirtualDevice) o;
            return this.mId == that.mId && Objects.equals(this.mName, that.mName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mId), this.mName);
    }

    public String toString() {
        return "VirtualDevice( mId=" + this.mId + " mName=" + this.mName + NavigationBarInflaterView.KEY_CODE_END;
    }
}
