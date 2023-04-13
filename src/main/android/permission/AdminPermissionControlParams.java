package android.permission;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
@SystemApi
/* loaded from: classes3.dex */
public final class AdminPermissionControlParams implements Parcelable {
    public static final Parcelable.Creator<AdminPermissionControlParams> CREATOR = new Parcelable.Creator<AdminPermissionControlParams>() { // from class: android.permission.AdminPermissionControlParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdminPermissionControlParams createFromParcel(Parcel in) {
            String granteePackageName = in.readString();
            String permission = in.readString();
            int grantState = in.readInt();
            boolean mayAdminGrantSensorPermissions = in.readBoolean();
            return new AdminPermissionControlParams(granteePackageName, permission, grantState, mayAdminGrantSensorPermissions);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdminPermissionControlParams[] newArray(int size) {
            return new AdminPermissionControlParams[size];
        }
    };
    private final boolean mCanAdminGrantSensorsPermissions;
    private final int mGrantState;
    private final String mGranteePackageName;
    private final String mPermission;

    public AdminPermissionControlParams(String granteePackageName, String permission, int grantState, boolean canAdminGrantSensorsPermissions) {
        Preconditions.checkStringNotEmpty(granteePackageName, "Package name must not be empty.");
        Preconditions.checkStringNotEmpty(permission, "Permission must not be empty.");
        boolean z = true;
        if (grantState != 1 && grantState != 2 && grantState != 0) {
            z = false;
        }
        Preconditions.checkArgument(z);
        this.mGranteePackageName = granteePackageName;
        this.mPermission = permission;
        this.mGrantState = grantState;
        this.mCanAdminGrantSensorsPermissions = canAdminGrantSensorsPermissions;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mGranteePackageName);
        dest.writeString(this.mPermission);
        dest.writeInt(this.mGrantState);
        dest.writeBoolean(this.mCanAdminGrantSensorsPermissions);
    }

    public String getGranteePackageName() {
        return this.mGranteePackageName;
    }

    public String getPermission() {
        return this.mPermission;
    }

    public int getGrantState() {
        return this.mGrantState;
    }

    public boolean canAdminGrantSensorsPermissions() {
        return this.mCanAdminGrantSensorsPermissions;
    }

    public String toString() {
        return String.format("Grantee %s Permission %s state: %d admin grant of sensors permissions: %b", this.mGranteePackageName, this.mPermission, Integer.valueOf(this.mGrantState), Boolean.valueOf(this.mCanAdminGrantSensorsPermissions));
    }
}
