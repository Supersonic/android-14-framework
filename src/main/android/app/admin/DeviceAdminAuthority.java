package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class DeviceAdminAuthority extends Authority {
    public static final DeviceAdminAuthority DEVICE_ADMIN_AUTHORITY = new DeviceAdminAuthority();
    public static final Parcelable.Creator<DeviceAdminAuthority> CREATOR = new Parcelable.Creator<DeviceAdminAuthority>() { // from class: android.app.admin.DeviceAdminAuthority.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceAdminAuthority createFromParcel(Parcel source) {
            return DeviceAdminAuthority.DEVICE_ADMIN_AUTHORITY;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceAdminAuthority[] newArray(int size) {
            return new DeviceAdminAuthority[size];
        }
    };

    public String toString() {
        return "DeviceAdminAuthority {}";
    }

    @Override // android.app.admin.Authority
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override // android.app.admin.Authority
    public int hashCode() {
        return 0;
    }

    @Override // android.app.admin.Authority, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
    }
}
