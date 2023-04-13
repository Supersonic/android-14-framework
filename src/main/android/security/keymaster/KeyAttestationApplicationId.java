package android.security.keymaster;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public class KeyAttestationApplicationId implements Parcelable {
    public static final Parcelable.Creator<KeyAttestationApplicationId> CREATOR = new Parcelable.Creator<KeyAttestationApplicationId>() { // from class: android.security.keymaster.KeyAttestationApplicationId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyAttestationApplicationId createFromParcel(Parcel source) {
            return new KeyAttestationApplicationId(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyAttestationApplicationId[] newArray(int size) {
            return new KeyAttestationApplicationId[size];
        }
    };
    private final KeyAttestationPackageInfo[] mAttestationPackageInfos;

    public KeyAttestationApplicationId(KeyAttestationPackageInfo[] mAttestationPackageInfos) {
        this.mAttestationPackageInfos = mAttestationPackageInfos;
    }

    public KeyAttestationPackageInfo[] getAttestationPackageInfos() {
        return this.mAttestationPackageInfos;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.mAttestationPackageInfos, flags);
    }

    KeyAttestationApplicationId(Parcel source) {
        this.mAttestationPackageInfos = (KeyAttestationPackageInfo[]) source.createTypedArray(KeyAttestationPackageInfo.CREATOR);
    }
}
