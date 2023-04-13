package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.content.pm.PackageInfo */
/* loaded from: classes.dex */
public class PackageInfo implements Parcelable {
    public static final Parcelable.Creator<PackageInfo> CREATOR = new Parcelable.Creator<PackageInfo>() { // from class: android.content.pm.PackageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageInfo createFromParcel(Parcel source) {
            return new PackageInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageInfo[] newArray(int size) {
            return new PackageInfo[size];
        }
    };
    public static final int INSTALL_LOCATION_AUTO = 0;
    public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
    public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;
    public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
    public static final int REQUESTED_PERMISSION_GRANTED = 2;
    public static final int REQUESTED_PERMISSION_IMPLICIT = 4;
    public static final int REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 65536;
    public static final int REQUESTED_PERMISSION_REQUIRED = 1;
    public ActivityInfo[] activities;
    public ApplicationInfo applicationInfo;
    public Attribution[] attributions;
    public int baseRevisionCode;
    public int compileSdkVersion;
    public String compileSdkVersionCodename;
    public ConfigurationInfo[] configPreferences;
    public boolean coreApp;
    public FeatureGroupInfo[] featureGroups;
    public long firstInstallTime;
    public int[] gids;
    public int installLocation;
    public InstrumentationInfo[] instrumentation;
    public boolean isActiveApex;
    public boolean isApex;
    public boolean isStub;
    public long lastUpdateTime;
    public boolean mOverlayIsStatic;
    public String overlayCategory;
    public int overlayPriority;
    public String overlayTarget;
    public String packageName;
    public PermissionInfo[] permissions;
    public ProviderInfo[] providers;
    public ActivityInfo[] receivers;
    public FeatureInfo[] reqFeatures;
    public String[] requestedPermissions;
    public int[] requestedPermissionsFlags;
    public String requiredAccountType;
    public boolean requiredForAllUsers;
    public String restrictedAccountType;
    public ServiceInfo[] services;
    public String sharedUserId;
    public int sharedUserLabel;
    @Deprecated
    public Signature[] signatures;
    public SigningInfo signingInfo;
    public String[] splitNames;
    public int[] splitRevisionCodes;
    public String targetOverlayableName;
    @Deprecated
    public int versionCode;
    public int versionCodeMajor;
    public String versionName;

    public long getLongVersionCode() {
        return composeLongVersionCode(this.versionCodeMajor, this.versionCode);
    }

    public void setLongVersionCode(long longVersionCode) {
        this.versionCodeMajor = (int) (longVersionCode >> 32);
        this.versionCode = (int) longVersionCode;
    }

    public static long composeLongVersionCode(int major, int minor) {
        return (major << 32) | (minor & 4294967295L);
    }

    public PackageInfo() {
        this.installLocation = 1;
    }

    public boolean isOverlayPackage() {
        return this.overlayTarget != null;
    }

    public boolean isStaticOverlayPackage() {
        return this.overlayTarget != null && this.mOverlayIsStatic;
    }

    public String toString() {
        return "PackageInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        boolean prevAllowSquashing = dest.allowSquashing();
        dest.writeString8(this.packageName);
        dest.writeString8Array(this.splitNames);
        dest.writeInt(this.versionCode);
        dest.writeInt(this.versionCodeMajor);
        dest.writeString8(this.versionName);
        dest.writeInt(this.baseRevisionCode);
        dest.writeIntArray(this.splitRevisionCodes);
        dest.writeString8(this.sharedUserId);
        dest.writeInt(this.sharedUserLabel);
        if (this.applicationInfo != null) {
            dest.writeInt(1);
            this.applicationInfo.writeToParcel(dest, parcelableFlags);
        } else {
            dest.writeInt(0);
        }
        dest.writeLong(this.firstInstallTime);
        dest.writeLong(this.lastUpdateTime);
        dest.writeIntArray(this.gids);
        dest.writeTypedArray(this.activities, parcelableFlags);
        dest.writeTypedArray(this.receivers, parcelableFlags);
        dest.writeTypedArray(this.services, parcelableFlags);
        dest.writeTypedArray(this.providers, parcelableFlags);
        dest.writeTypedArray(this.instrumentation, parcelableFlags);
        dest.writeTypedArray(this.permissions, parcelableFlags);
        dest.writeString8Array(this.requestedPermissions);
        dest.writeIntArray(this.requestedPermissionsFlags);
        dest.writeTypedArray(this.signatures, parcelableFlags);
        dest.writeTypedArray(this.configPreferences, parcelableFlags);
        dest.writeTypedArray(this.reqFeatures, parcelableFlags);
        dest.writeTypedArray(this.featureGroups, parcelableFlags);
        dest.writeTypedArray(this.attributions, parcelableFlags);
        dest.writeInt(this.installLocation);
        dest.writeInt(this.isStub ? 1 : 0);
        dest.writeInt(this.coreApp ? 1 : 0);
        dest.writeInt(this.requiredForAllUsers ? 1 : 0);
        dest.writeString8(this.restrictedAccountType);
        dest.writeString8(this.requiredAccountType);
        dest.writeString8(this.overlayTarget);
        dest.writeString8(this.overlayCategory);
        dest.writeInt(this.overlayPriority);
        dest.writeBoolean(this.mOverlayIsStatic);
        dest.writeInt(this.compileSdkVersion);
        dest.writeString8(this.compileSdkVersionCodename);
        if (this.signingInfo != null) {
            dest.writeInt(1);
            this.signingInfo.writeToParcel(dest, parcelableFlags);
        } else {
            dest.writeInt(0);
        }
        dest.writeBoolean(this.isApex);
        dest.writeBoolean(this.isActiveApex);
        dest.restoreAllowSquashing(prevAllowSquashing);
    }

    private PackageInfo(Parcel source) {
        this.installLocation = 1;
        this.packageName = source.readString8();
        this.splitNames = source.createString8Array();
        this.versionCode = source.readInt();
        this.versionCodeMajor = source.readInt();
        this.versionName = source.readString8();
        this.baseRevisionCode = source.readInt();
        this.splitRevisionCodes = source.createIntArray();
        this.sharedUserId = source.readString8();
        this.sharedUserLabel = source.readInt();
        int hasApp = source.readInt();
        if (hasApp != 0) {
            this.applicationInfo = ApplicationInfo.CREATOR.createFromParcel(source);
        }
        this.firstInstallTime = source.readLong();
        this.lastUpdateTime = source.readLong();
        this.gids = source.createIntArray();
        this.activities = (ActivityInfo[]) source.createTypedArray(ActivityInfo.CREATOR);
        this.receivers = (ActivityInfo[]) source.createTypedArray(ActivityInfo.CREATOR);
        this.services = (ServiceInfo[]) source.createTypedArray(ServiceInfo.CREATOR);
        this.providers = (ProviderInfo[]) source.createTypedArray(ProviderInfo.CREATOR);
        this.instrumentation = (InstrumentationInfo[]) source.createTypedArray(InstrumentationInfo.CREATOR);
        this.permissions = (PermissionInfo[]) source.createTypedArray(PermissionInfo.CREATOR);
        this.requestedPermissions = source.createString8Array();
        this.requestedPermissionsFlags = source.createIntArray();
        this.signatures = (Signature[]) source.createTypedArray(Signature.CREATOR);
        this.configPreferences = (ConfigurationInfo[]) source.createTypedArray(ConfigurationInfo.CREATOR);
        this.reqFeatures = (FeatureInfo[]) source.createTypedArray(FeatureInfo.CREATOR);
        this.featureGroups = (FeatureGroupInfo[]) source.createTypedArray(FeatureGroupInfo.CREATOR);
        this.attributions = (Attribution[]) source.createTypedArray(Attribution.CREATOR);
        this.installLocation = source.readInt();
        this.isStub = source.readInt() != 0;
        this.coreApp = source.readInt() != 0;
        this.requiredForAllUsers = source.readInt() != 0;
        this.restrictedAccountType = source.readString8();
        this.requiredAccountType = source.readString8();
        this.overlayTarget = source.readString8();
        this.overlayCategory = source.readString8();
        this.overlayPriority = source.readInt();
        this.mOverlayIsStatic = source.readBoolean();
        this.compileSdkVersion = source.readInt();
        this.compileSdkVersionCodename = source.readString8();
        int hasSigningInfo = source.readInt();
        if (hasSigningInfo != 0) {
            this.signingInfo = SigningInfo.CREATOR.createFromParcel(source);
        }
        this.isApex = source.readBoolean();
        this.isActiveApex = source.readBoolean();
    }
}
