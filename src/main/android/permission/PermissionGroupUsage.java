package android.permission;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class PermissionGroupUsage implements Parcelable {
    public static final Parcelable.Creator<PermissionGroupUsage> CREATOR = new Parcelable.Creator<PermissionGroupUsage>() { // from class: android.permission.PermissionGroupUsage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PermissionGroupUsage[] newArray(int size) {
            return new PermissionGroupUsage[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PermissionGroupUsage createFromParcel(Parcel in) {
            return new PermissionGroupUsage(in);
        }
    };
    private final boolean mActive;
    private final CharSequence mAttributionLabel;
    private final CharSequence mAttributionTag;
    private final long mLastAccessTimeMillis;
    private final String mPackageName;
    private final String mPermissionGroupName;
    private final boolean mPhoneCall;
    private final CharSequence mProxyLabel;
    private final int mUid;

    public PermissionGroupUsage(String packageName, int uid, long lastAccessTimeMillis, String permissionGroupName, boolean active, boolean phoneCall, CharSequence attributionTag, CharSequence attributionLabel, CharSequence proxyLabel) {
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mUid = uid;
        this.mLastAccessTimeMillis = lastAccessTimeMillis;
        this.mPermissionGroupName = permissionGroupName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) permissionGroupName);
        this.mActive = active;
        this.mPhoneCall = phoneCall;
        this.mAttributionTag = attributionTag;
        this.mAttributionLabel = attributionLabel;
        this.mProxyLabel = proxyLabel;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getUid() {
        return this.mUid;
    }

    public long getLastAccessTimeMillis() {
        return this.mLastAccessTimeMillis;
    }

    public String getPermissionGroupName() {
        return this.mPermissionGroupName;
    }

    public boolean isActive() {
        return this.mActive;
    }

    public boolean isPhoneCall() {
        return this.mPhoneCall;
    }

    public CharSequence getAttributionTag() {
        return this.mAttributionTag;
    }

    public CharSequence getAttributionLabel() {
        return this.mAttributionLabel;
    }

    public CharSequence getProxyLabel() {
        return this.mProxyLabel;
    }

    public String toString() {
        return "PermissionGroupUsage { packageName = " + this.mPackageName + ", uid = " + this.mUid + ", lastAccessTimeMillis = " + this.mLastAccessTimeMillis + ", permissionGroupName = " + this.mPermissionGroupName + ", active = " + this.mActive + ", phoneCall = " + this.mPhoneCall + ", attributionTag = " + ((Object) this.mAttributionTag) + ", attributionLabel = " + ((Object) this.mAttributionLabel) + ", proxyLabel = " + ((Object) this.mProxyLabel) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionGroupUsage that = (PermissionGroupUsage) o;
        if (Objects.equals(this.mPackageName, that.mPackageName) && this.mUid == that.mUid && this.mLastAccessTimeMillis == that.mLastAccessTimeMillis && Objects.equals(this.mPermissionGroupName, that.mPermissionGroupName) && this.mActive == that.mActive && this.mPhoneCall == that.mPhoneCall && Objects.equals(this.mAttributionTag, that.mAttributionTag) && Objects.equals(this.mAttributionLabel, that.mAttributionLabel) && Objects.equals(this.mProxyLabel, that.mProxyLabel)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mPackageName);
        return (((((((((((((((_hash * 31) + this.mUid) * 31) + Long.hashCode(this.mLastAccessTimeMillis)) * 31) + Objects.hashCode(this.mPermissionGroupName)) * 31) + Boolean.hashCode(this.mActive)) * 31) + Boolean.hashCode(this.mPhoneCall)) * 31) + Objects.hashCode(this.mAttributionTag)) * 31) + Objects.hashCode(this.mAttributionLabel)) * 31) + Objects.hashCode(this.mProxyLabel);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int flg = this.mActive ? 0 | 16 : 0;
        if (this.mPhoneCall) {
            flg |= 32;
        }
        if (this.mAttributionTag != null) {
            flg |= 64;
        }
        if (this.mAttributionLabel != null) {
            flg |= 128;
        }
        if (this.mProxyLabel != null) {
            flg |= 256;
        }
        dest.writeInt(flg);
        dest.writeString(this.mPackageName);
        dest.writeInt(this.mUid);
        dest.writeLong(this.mLastAccessTimeMillis);
        dest.writeString(this.mPermissionGroupName);
        CharSequence charSequence = this.mAttributionTag;
        if (charSequence != null) {
            dest.writeCharSequence(charSequence);
        }
        CharSequence charSequence2 = this.mAttributionLabel;
        if (charSequence2 != null) {
            dest.writeCharSequence(charSequence2);
        }
        CharSequence charSequence3 = this.mProxyLabel;
        if (charSequence3 != null) {
            dest.writeCharSequence(charSequence3);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    PermissionGroupUsage(Parcel in) {
        int flg = in.readInt();
        boolean active = (flg & 16) != 0;
        boolean phoneCall = (flg & 32) != 0;
        String packageName = in.readString();
        int uid = in.readInt();
        long lastAccessTimeMillis = in.readLong();
        String permissionGroupName = in.readString();
        CharSequence attributionTag = (flg & 64) == 0 ? null : in.readCharSequence();
        CharSequence attributionLabel = (flg & 128) == 0 ? null : in.readCharSequence();
        CharSequence proxyLabel = (flg & 256) == 0 ? null : in.readCharSequence();
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mUid = uid;
        this.mLastAccessTimeMillis = lastAccessTimeMillis;
        this.mPermissionGroupName = permissionGroupName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) permissionGroupName);
        this.mActive = active;
        this.mPhoneCall = phoneCall;
        this.mAttributionTag = attributionTag;
        this.mAttributionLabel = attributionLabel;
        this.mProxyLabel = proxyLabel;
    }

    @Deprecated
    private void __metadata() {
    }
}
