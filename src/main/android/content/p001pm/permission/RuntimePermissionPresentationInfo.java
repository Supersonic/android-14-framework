package android.content.p001pm.permission;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
@Deprecated
/* renamed from: android.content.pm.permission.RuntimePermissionPresentationInfo */
/* loaded from: classes.dex */
public final class RuntimePermissionPresentationInfo implements Parcelable {
    public static final Parcelable.Creator<RuntimePermissionPresentationInfo> CREATOR = new Parcelable.Creator<RuntimePermissionPresentationInfo>() { // from class: android.content.pm.permission.RuntimePermissionPresentationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RuntimePermissionPresentationInfo createFromParcel(Parcel source) {
            return new RuntimePermissionPresentationInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RuntimePermissionPresentationInfo[] newArray(int size) {
            return new RuntimePermissionPresentationInfo[size];
        }
    };
    private static final int FLAG_GRANTED = 1;
    private static final int FLAG_STANDARD = 2;
    private final int mFlags;
    private final CharSequence mLabel;

    public RuntimePermissionPresentationInfo(CharSequence label, boolean granted, boolean standard) {
        this.mLabel = label;
        int flags = granted ? 0 | 1 : 0;
        this.mFlags = standard ? flags | 2 : flags;
    }

    private RuntimePermissionPresentationInfo(Parcel parcel) {
        this.mLabel = parcel.readCharSequence();
        this.mFlags = parcel.readInt();
    }

    public boolean isGranted() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isStandard() {
        return (this.mFlags & 2) != 0;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeCharSequence(this.mLabel);
        parcel.writeInt(this.mFlags);
    }
}
