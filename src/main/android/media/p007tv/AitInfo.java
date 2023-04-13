package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.AitInfo */
/* loaded from: classes2.dex */
public final class AitInfo implements Parcelable {
    public static final Parcelable.Creator<AitInfo> CREATOR = new Parcelable.Creator<AitInfo>() { // from class: android.media.tv.AitInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AitInfo createFromParcel(Parcel in) {
            return new AitInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AitInfo[] newArray(int size) {
            return new AitInfo[size];
        }
    };
    static final String TAG = "AitInfo";
    private final int mType;
    private final int mVersion;

    private AitInfo(Parcel in) {
        this.mType = in.readInt();
        this.mVersion = in.readInt();
    }

    public AitInfo(int type, int version) {
        this.mType = type;
        this.mVersion = version;
    }

    public int getType() {
        return this.mType;
    }

    public int getVersion() {
        return this.mVersion;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeInt(this.mVersion);
    }

    public String toString() {
        return "type=" + this.mType + ";version=" + this.mVersion;
    }
}
