package android.hardware.fingerprint;

import android.hardware.biometrics.BiometricAuthenticator;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class Fingerprint extends BiometricAuthenticator.Identifier {
    public static final Parcelable.Creator<Fingerprint> CREATOR = new Parcelable.Creator<Fingerprint>() { // from class: android.hardware.fingerprint.Fingerprint.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Fingerprint createFromParcel(Parcel in) {
            return new Fingerprint(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Fingerprint[] newArray(int size) {
            return new Fingerprint[size];
        }
    };
    private int mGroupId;

    public Fingerprint(CharSequence name, int groupId, int fingerId, long deviceId) {
        super(name, fingerId, deviceId);
        this.mGroupId = groupId;
    }

    public Fingerprint(CharSequence name, int fingerId, long deviceId) {
        super(name, fingerId, deviceId);
    }

    private Fingerprint(Parcel in) {
        super(in.readString(), in.readInt(), in.readLong());
        this.mGroupId = in.readInt();
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName().toString());
        out.writeInt(getBiometricId());
        out.writeLong(getDeviceId());
        out.writeInt(this.mGroupId);
    }
}
