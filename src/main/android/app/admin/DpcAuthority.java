package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class DpcAuthority extends Authority {
    public static final DpcAuthority DPC_AUTHORITY = new DpcAuthority();
    public static final Parcelable.Creator<DpcAuthority> CREATOR = new Parcelable.Creator<DpcAuthority>() { // from class: android.app.admin.DpcAuthority.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DpcAuthority createFromParcel(Parcel source) {
            return DpcAuthority.DPC_AUTHORITY;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DpcAuthority[] newArray(int size) {
            return new DpcAuthority[size];
        }
    };

    public String toString() {
        return "DpcAuthority {}";
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
