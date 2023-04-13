package android.content.res;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class ObbInfo implements Parcelable {
    public static final Parcelable.Creator<ObbInfo> CREATOR = new Parcelable.Creator<ObbInfo>() { // from class: android.content.res.ObbInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ObbInfo createFromParcel(Parcel source) {
            return new ObbInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ObbInfo[] newArray(int size) {
            return new ObbInfo[size];
        }
    };
    public static final int OBB_OVERLAY = 1;
    public String filename;
    public int flags;
    public String packageName;
    public byte[] salt;
    public int version;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ObbInfo() {
    }

    public String toString() {
        return "ObbInfo{" + Integer.toHexString(System.identityHashCode(this)) + " packageName=" + this.packageName + ",version=" + this.version + ",flags=" + this.flags + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.filename);
        dest.writeString(this.packageName);
        dest.writeInt(this.version);
        dest.writeInt(this.flags);
        dest.writeByteArray(this.salt);
    }

    private ObbInfo(Parcel source) {
        this.filename = source.readString();
        this.packageName = source.readString();
        this.version = source.readInt();
        this.flags = source.readInt();
        this.salt = source.createByteArray();
    }
}
