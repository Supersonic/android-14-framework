package android.permission;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
@SystemApi
/* loaded from: classes3.dex */
public final class RuntimePermissionUsageInfo implements Parcelable {
    public static final Parcelable.Creator<RuntimePermissionUsageInfo> CREATOR = new Parcelable.Creator<RuntimePermissionUsageInfo>() { // from class: android.permission.RuntimePermissionUsageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RuntimePermissionUsageInfo createFromParcel(Parcel source) {
            return new RuntimePermissionUsageInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RuntimePermissionUsageInfo[] newArray(int size) {
            return new RuntimePermissionUsageInfo[size];
        }
    };
    private final String mName;
    private final int mNumUsers;

    public RuntimePermissionUsageInfo(String name, int numUsers) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgumentNonnegative(numUsers);
        this.mName = name;
        this.mNumUsers = numUsers;
    }

    private RuntimePermissionUsageInfo(Parcel parcel) {
        this(parcel.readString(), parcel.readInt());
    }

    public int getAppAccessCount() {
        return this.mNumUsers;
    }

    public String getName() {
        return this.mName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mName);
        parcel.writeInt(this.mNumUsers);
    }
}
