package android.permission;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
@SystemApi
/* loaded from: classes3.dex */
public final class RuntimePermissionPresentationInfo implements Parcelable {
    public static final Parcelable.Creator<RuntimePermissionPresentationInfo> CREATOR = new Parcelable.Creator<RuntimePermissionPresentationInfo>() { // from class: android.permission.RuntimePermissionPresentationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RuntimePermissionPresentationInfo createFromParcel(Parcel source) {
            CharSequence label = source.readCharSequence();
            int flags = source.readInt();
            return new RuntimePermissionPresentationInfo(label, (flags & 1) != 0, (flags & 2) != 0);
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
        Preconditions.checkNotNull(label);
        this.mLabel = label;
        int flags = granted ? 0 | 1 : 0;
        this.mFlags = standard ? flags | 2 : flags;
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
