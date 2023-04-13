package android.hardware.input;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class HostUsiVersion implements Parcelable {
    public static final Parcelable.Creator<HostUsiVersion> CREATOR = new Parcelable.Creator<HostUsiVersion>() { // from class: android.hardware.input.HostUsiVersion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HostUsiVersion[] newArray(int size) {
            return new HostUsiVersion[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HostUsiVersion createFromParcel(Parcel in) {
            return new HostUsiVersion(in);
        }
    };
    private final int mMajorVersion;
    private final int mMinorVersion;

    public boolean isValid() {
        return this.mMajorVersion >= 0 && this.mMinorVersion >= 0;
    }

    public HostUsiVersion(int majorVersion, int minorVersion) {
        this.mMajorVersion = majorVersion;
        this.mMinorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return this.mMajorVersion;
    }

    public int getMinorVersion() {
        return this.mMinorVersion;
    }

    public String toString() {
        return "HostUsiVersion { majorVersion = " + this.mMajorVersion + ", minorVersion = " + this.mMinorVersion + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostUsiVersion that = (HostUsiVersion) o;
        if (this.mMajorVersion == that.mMajorVersion && this.mMinorVersion == that.mMinorVersion) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mMajorVersion;
        return (_hash * 31) + this.mMinorVersion;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMajorVersion);
        dest.writeInt(this.mMinorVersion);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    HostUsiVersion(Parcel in) {
        int majorVersion = in.readInt();
        int minorVersion = in.readInt();
        this.mMajorVersion = majorVersion;
        this.mMinorVersion = minorVersion;
    }

    @Deprecated
    private void __metadata() {
    }
}
