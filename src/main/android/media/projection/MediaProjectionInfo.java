package android.media.projection;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MediaProjectionInfo implements Parcelable {
    public static final Parcelable.Creator<MediaProjectionInfo> CREATOR = new Parcelable.Creator<MediaProjectionInfo>() { // from class: android.media.projection.MediaProjectionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaProjectionInfo createFromParcel(Parcel in) {
            return new MediaProjectionInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaProjectionInfo[] newArray(int size) {
            return new MediaProjectionInfo[size];
        }
    };
    private final String mPackageName;
    private final UserHandle mUserHandle;

    public MediaProjectionInfo(String packageName, UserHandle handle) {
        this.mPackageName = packageName;
        this.mUserHandle = handle;
    }

    public MediaProjectionInfo(Parcel in) {
        this.mPackageName = in.readString();
        this.mUserHandle = UserHandle.readFromParcel(in);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public boolean equals(Object o) {
        if (o instanceof MediaProjectionInfo) {
            MediaProjectionInfo other = (MediaProjectionInfo) o;
            return Objects.equals(other.mPackageName, this.mPackageName) && Objects.equals(other.mUserHandle, this.mUserHandle);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mPackageName, this.mUserHandle);
    }

    public String toString() {
        return "MediaProjectionInfo{mPackageName=" + this.mPackageName + ", mUserHandle=" + this.mUserHandle + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mPackageName);
        UserHandle.writeToParcel(this.mUserHandle, out);
    }
}
