package android.content.p001pm;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.content.pm.VersionedPackage */
/* loaded from: classes.dex */
public final class VersionedPackage implements Parcelable {
    public static final Parcelable.Creator<VersionedPackage> CREATOR = new Parcelable.Creator<VersionedPackage>() { // from class: android.content.pm.VersionedPackage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VersionedPackage createFromParcel(Parcel source) {
            return new VersionedPackage(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VersionedPackage[] newArray(int size) {
            return new VersionedPackage[size];
        }
    };
    private final String mPackageName;
    private final long mVersionCode;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.VersionedPackage$VersionCode */
    /* loaded from: classes.dex */
    public @interface VersionCode {
    }

    public VersionedPackage(String packageName, int versionCode) {
        this.mPackageName = packageName;
        this.mVersionCode = versionCode;
    }

    public VersionedPackage(String packageName, long versionCode) {
        this.mPackageName = packageName;
        this.mVersionCode = versionCode;
    }

    private VersionedPackage(Parcel parcel) {
        this.mPackageName = parcel.readString8();
        this.mVersionCode = parcel.readLong();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    @Deprecated
    public int getVersionCode() {
        return (int) (this.mVersionCode & 2147483647L);
    }

    public long getLongVersionCode() {
        return this.mVersionCode;
    }

    public String toString() {
        return "VersionedPackage[" + this.mPackageName + "/" + this.mVersionCode + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public boolean equals(Object o) {
        return (o instanceof VersionedPackage) && ((VersionedPackage) o).mPackageName.equals(this.mPackageName) && ((VersionedPackage) o).mVersionCode == this.mVersionCode;
    }

    public int hashCode() {
        return (this.mPackageName.hashCode() * 31) + Long.hashCode(this.mVersionCode);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString8(this.mPackageName);
        parcel.writeLong(this.mVersionCode);
    }
}
