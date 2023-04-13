package android.content.p001pm;

import android.annotation.SystemApi;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* renamed from: android.content.pm.InstantAppInfo */
/* loaded from: classes.dex */
public final class InstantAppInfo implements Parcelable {
    public static final Parcelable.Creator<InstantAppInfo> CREATOR = new Parcelable.Creator<InstantAppInfo>() { // from class: android.content.pm.InstantAppInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstantAppInfo createFromParcel(Parcel parcel) {
            return new InstantAppInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstantAppInfo[] newArray(int size) {
            return new InstantAppInfo[0];
        }
    };
    private final ApplicationInfo mApplicationInfo;
    private final String[] mGrantedPermissions;
    private final CharSequence mLabelText;
    private final String mPackageName;
    private final String[] mRequestedPermissions;

    public InstantAppInfo(ApplicationInfo appInfo, String[] requestedPermissions, String[] grantedPermissions) {
        this.mApplicationInfo = appInfo;
        this.mPackageName = null;
        this.mLabelText = null;
        this.mRequestedPermissions = requestedPermissions;
        this.mGrantedPermissions = grantedPermissions;
    }

    public InstantAppInfo(String packageName, CharSequence label, String[] requestedPermissions, String[] grantedPermissions) {
        this.mApplicationInfo = null;
        this.mPackageName = packageName;
        this.mLabelText = label;
        this.mRequestedPermissions = requestedPermissions;
        this.mGrantedPermissions = grantedPermissions;
    }

    private InstantAppInfo(Parcel parcel) {
        this.mPackageName = parcel.readString();
        this.mLabelText = parcel.readCharSequence();
        this.mRequestedPermissions = parcel.readStringArray();
        this.mGrantedPermissions = parcel.createStringArray();
        this.mApplicationInfo = (ApplicationInfo) parcel.readParcelable(null, ApplicationInfo.class);
    }

    public ApplicationInfo getApplicationInfo() {
        return this.mApplicationInfo;
    }

    public String getPackageName() {
        ApplicationInfo applicationInfo = this.mApplicationInfo;
        if (applicationInfo != null) {
            return applicationInfo.packageName;
        }
        return this.mPackageName;
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        ApplicationInfo applicationInfo = this.mApplicationInfo;
        if (applicationInfo != null) {
            return applicationInfo.loadLabel(packageManager);
        }
        return this.mLabelText;
    }

    public Drawable loadIcon(PackageManager packageManager) {
        ApplicationInfo applicationInfo = this.mApplicationInfo;
        if (applicationInfo != null) {
            return applicationInfo.loadIcon(packageManager);
        }
        return packageManager.getInstantAppIcon(this.mPackageName);
    }

    public String[] getRequestedPermissions() {
        return this.mRequestedPermissions;
    }

    public String[] getGrantedPermissions() {
        return this.mGrantedPermissions;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mPackageName);
        parcel.writeCharSequence(this.mLabelText);
        parcel.writeStringArray(this.mRequestedPermissions);
        parcel.writeStringArray(this.mGrantedPermissions);
        parcel.writeParcelable(this.mApplicationInfo, flags);
    }
}
