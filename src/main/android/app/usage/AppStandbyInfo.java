package android.app.usage;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class AppStandbyInfo implements Parcelable {
    public static final Parcelable.Creator<AppStandbyInfo> CREATOR = new Parcelable.Creator<AppStandbyInfo>() { // from class: android.app.usage.AppStandbyInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppStandbyInfo createFromParcel(Parcel source) {
            return new AppStandbyInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppStandbyInfo[] newArray(int size) {
            return new AppStandbyInfo[size];
        }
    };
    public String mPackageName;
    public int mStandbyBucket;

    private AppStandbyInfo(Parcel in) {
        this.mPackageName = in.readString();
        this.mStandbyBucket = in.readInt();
    }

    public AppStandbyInfo(String packageName, int bucket) {
        this.mPackageName = packageName;
        this.mStandbyBucket = bucket;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPackageName);
        dest.writeInt(this.mStandbyBucket);
    }
}
