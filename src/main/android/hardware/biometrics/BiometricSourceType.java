package android.hardware.biometrics;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public enum BiometricSourceType implements Parcelable {
    FINGERPRINT,
    FACE,
    IRIS;
    
    public static final Parcelable.Creator<BiometricSourceType> CREATOR = new Parcelable.Creator<BiometricSourceType>() { // from class: android.hardware.biometrics.BiometricSourceType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BiometricSourceType createFromParcel(Parcel source) {
            return BiometricSourceType.valueOf(source.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BiometricSourceType[] newArray(int size) {
            return new BiometricSourceType[size];
        }
    };

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}
