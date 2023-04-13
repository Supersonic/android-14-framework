package android.hardware.camera2.params;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class VendorTagDescriptor implements Parcelable {
    public static final Parcelable.Creator<VendorTagDescriptor> CREATOR = new Parcelable.Creator<VendorTagDescriptor>() { // from class: android.hardware.camera2.params.VendorTagDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VendorTagDescriptor createFromParcel(Parcel source) {
            return new VendorTagDescriptor(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VendorTagDescriptor[] newArray(int size) {
            return new VendorTagDescriptor[size];
        }
    };
    private static final String TAG = "VendorTagDescriptor";

    private VendorTagDescriptor(Parcel source) {
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (dest == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
    }
}
