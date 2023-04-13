package android.app.backup;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public class RestoreDescription implements Parcelable {
    public static final int TYPE_FULL_STREAM = 2;
    public static final int TYPE_KEY_VALUE = 1;
    private final int mDataType;
    private final String mPackageName;
    private static final String NO_MORE_PACKAGES_SENTINEL = "NO_MORE_PACKAGES";
    public static final RestoreDescription NO_MORE_PACKAGES = new RestoreDescription(NO_MORE_PACKAGES_SENTINEL, 0);
    public static final Parcelable.Creator<RestoreDescription> CREATOR = new Parcelable.Creator<RestoreDescription>() { // from class: android.app.backup.RestoreDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RestoreDescription createFromParcel(Parcel in) {
            RestoreDescription unparceled = new RestoreDescription(in);
            if (RestoreDescription.NO_MORE_PACKAGES_SENTINEL.equals(unparceled.mPackageName)) {
                return RestoreDescription.NO_MORE_PACKAGES;
            }
            return unparceled;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RestoreDescription[] newArray(int size) {
            return new RestoreDescription[size];
        }
    };

    public String toString() {
        return "RestoreDescription{" + this.mPackageName + " : " + (this.mDataType == 1 ? "KEY_VALUE" : "STREAM") + '}';
    }

    public RestoreDescription(String packageName, int dataType) {
        this.mPackageName = packageName;
        this.mDataType = dataType;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getDataType() {
        return this.mDataType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mPackageName);
        out.writeInt(this.mDataType);
    }

    private RestoreDescription(Parcel in) {
        this.mPackageName = in.readString();
        this.mDataType = in.readInt();
    }
}
