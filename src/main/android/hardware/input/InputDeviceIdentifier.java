package android.hardware.input;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class InputDeviceIdentifier implements Parcelable {
    public static final Parcelable.Creator<InputDeviceIdentifier> CREATOR = new Parcelable.Creator<InputDeviceIdentifier>() { // from class: android.hardware.input.InputDeviceIdentifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputDeviceIdentifier createFromParcel(Parcel source) {
            return new InputDeviceIdentifier(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputDeviceIdentifier[] newArray(int size) {
            return new InputDeviceIdentifier[size];
        }
    };
    private final String mDescriptor;
    private final int mProductId;
    private final int mVendorId;

    public InputDeviceIdentifier(String descriptor, int vendorId, int productId) {
        this.mDescriptor = descriptor;
        this.mVendorId = vendorId;
        this.mProductId = productId;
    }

    private InputDeviceIdentifier(Parcel src) {
        this.mDescriptor = src.readString();
        this.mVendorId = src.readInt();
        this.mProductId = src.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDescriptor);
        dest.writeInt(this.mVendorId);
        dest.writeInt(this.mProductId);
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getProductId() {
        return this.mProductId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof InputDeviceIdentifier)) {
            return false;
        }
        InputDeviceIdentifier that = (InputDeviceIdentifier) o;
        if (this.mVendorId == that.mVendorId && this.mProductId == that.mProductId && TextUtils.equals(this.mDescriptor, that.mDescriptor)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mDescriptor, Integer.valueOf(this.mVendorId), Integer.valueOf(this.mProductId));
    }

    public String toString() {
        return "InputDeviceIdentifier: vendorId: " + this.mVendorId + ", productId: " + this.mProductId + ", descriptor: " + this.mDescriptor;
    }
}
