package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.content.pm.InstallSourceInfo */
/* loaded from: classes.dex */
public final class InstallSourceInfo implements Parcelable {
    public static final Parcelable.Creator<InstallSourceInfo> CREATOR = new Parcelable.Creator<InstallSourceInfo>() { // from class: android.content.pm.InstallSourceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstallSourceInfo createFromParcel(Parcel source) {
            return new InstallSourceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstallSourceInfo[] newArray(int size) {
            return new InstallSourceInfo[size];
        }
    };
    private final String mInitiatingPackageName;
    private final SigningInfo mInitiatingPackageSigningInfo;
    private final String mInstallingPackageName;
    private final String mOriginatingPackageName;
    private final int mPackageSource;
    private final String mUpdateOwnerPackageName;

    public InstallSourceInfo(String initiatingPackageName, SigningInfo initiatingPackageSigningInfo, String originatingPackageName, String installingPackageName) {
        this(initiatingPackageName, initiatingPackageSigningInfo, originatingPackageName, installingPackageName, null, 0);
    }

    public InstallSourceInfo(String initiatingPackageName, SigningInfo initiatingPackageSigningInfo, String originatingPackageName, String installingPackageName, String updateOwnerPackageName, int packageSource) {
        this.mInitiatingPackageName = initiatingPackageName;
        this.mInitiatingPackageSigningInfo = initiatingPackageSigningInfo;
        this.mOriginatingPackageName = originatingPackageName;
        this.mInstallingPackageName = installingPackageName;
        this.mUpdateOwnerPackageName = updateOwnerPackageName;
        this.mPackageSource = packageSource;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        SigningInfo signingInfo = this.mInitiatingPackageSigningInfo;
        if (signingInfo == null) {
            return 0;
        }
        return signingInfo.describeContents();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mInitiatingPackageName);
        dest.writeParcelable(this.mInitiatingPackageSigningInfo, flags);
        dest.writeString(this.mOriginatingPackageName);
        dest.writeString(this.mInstallingPackageName);
        dest.writeString8(this.mUpdateOwnerPackageName);
        dest.writeInt(this.mPackageSource);
    }

    private InstallSourceInfo(Parcel source) {
        this.mInitiatingPackageName = source.readString();
        this.mInitiatingPackageSigningInfo = (SigningInfo) source.readParcelable(SigningInfo.class.getClassLoader(), SigningInfo.class);
        this.mOriginatingPackageName = source.readString();
        this.mInstallingPackageName = source.readString();
        this.mUpdateOwnerPackageName = source.readString8();
        this.mPackageSource = source.readInt();
    }

    public String getInitiatingPackageName() {
        return this.mInitiatingPackageName;
    }

    public SigningInfo getInitiatingPackageSigningInfo() {
        return this.mInitiatingPackageSigningInfo;
    }

    public String getOriginatingPackageName() {
        return this.mOriginatingPackageName;
    }

    public String getInstallingPackageName() {
        return this.mInstallingPackageName;
    }

    public String getUpdateOwnerPackageName() {
        return this.mUpdateOwnerPackageName;
    }

    public int getPackageSource() {
        return this.mPackageSource;
    }
}
