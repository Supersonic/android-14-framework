package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* renamed from: android.content.pm.SharedLibraryInfo */
/* loaded from: classes.dex */
public final class SharedLibraryInfo implements Parcelable {
    public static final Parcelable.Creator<SharedLibraryInfo> CREATOR = new Parcelable.Creator<SharedLibraryInfo>() { // from class: android.content.pm.SharedLibraryInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedLibraryInfo createFromParcel(Parcel source) {
            return new SharedLibraryInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedLibraryInfo[] newArray(int size) {
            return new SharedLibraryInfo[size];
        }
    };
    public static final int TYPE_BUILTIN = 0;
    public static final int TYPE_DYNAMIC = 1;
    public static final int TYPE_SDK_PACKAGE = 3;
    public static final int TYPE_STATIC = 2;
    public static final int VERSION_UNDEFINED = -1;
    private final List<String> mCodePaths;
    private final VersionedPackage mDeclaringPackage;
    private List<SharedLibraryInfo> mDependencies;
    private final List<VersionedPackage> mDependentPackages;
    private final boolean mIsNative;
    private final String mName;
    private final String mPackageName;
    private final String mPath;
    private final int mType;
    private final long mVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.SharedLibraryInfo$Type */
    /* loaded from: classes.dex */
    public @interface Type {
    }

    public SharedLibraryInfo(String path, String packageName, List<String> codePaths, String name, long version, int type, VersionedPackage declaringPackage, List<VersionedPackage> dependentPackages, List<SharedLibraryInfo> dependencies, boolean isNative) {
        this.mPath = path;
        this.mPackageName = packageName;
        this.mCodePaths = codePaths;
        this.mName = name;
        this.mVersion = version;
        this.mType = type;
        this.mDeclaringPackage = declaringPackage;
        this.mDependentPackages = dependentPackages;
        this.mDependencies = dependencies;
        this.mIsNative = isNative;
    }

    private SharedLibraryInfo(Parcel parcel) {
        this.mPath = parcel.readString8();
        this.mPackageName = parcel.readString8();
        if (parcel.readInt() != 0) {
            this.mCodePaths = Arrays.asList(parcel.createString8Array());
        } else {
            this.mCodePaths = null;
        }
        this.mName = parcel.readString8();
        this.mVersion = parcel.readLong();
        this.mType = parcel.readInt();
        this.mDeclaringPackage = (VersionedPackage) parcel.readParcelable(null, VersionedPackage.class);
        this.mDependentPackages = parcel.readArrayList(null, VersionedPackage.class);
        this.mDependencies = parcel.createTypedArrayList(CREATOR);
        this.mIsNative = parcel.readBoolean();
    }

    public int getType() {
        return this.mType;
    }

    public boolean isNative() {
        return this.mIsNative;
    }

    public String getName() {
        return this.mName;
    }

    public String getPath() {
        return this.mPath;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public List<String> getAllCodePaths() {
        if (getPath() != null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(getPath());
            return list;
        }
        return (List) Objects.requireNonNull(this.mCodePaths);
    }

    public void addDependency(SharedLibraryInfo info) {
        if (info == null) {
            return;
        }
        if (this.mDependencies == null) {
            this.mDependencies = new ArrayList();
        }
        this.mDependencies.add(info);
    }

    public void clearDependencies() {
        this.mDependencies = null;
    }

    public List<SharedLibraryInfo> getDependencies() {
        return this.mDependencies;
    }

    @Deprecated
    public int getVersion() {
        long j = this.mVersion;
        if (j >= 0) {
            j &= 2147483647L;
        }
        return (int) j;
    }

    public long getLongVersion() {
        return this.mVersion;
    }

    public boolean isBuiltin() {
        return this.mType == 0;
    }

    public boolean isDynamic() {
        return this.mType == 1;
    }

    public boolean isStatic() {
        return this.mType == 2;
    }

    public boolean isSdk() {
        return this.mType == 3;
    }

    public VersionedPackage getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public List<VersionedPackage> getDependentPackages() {
        List<VersionedPackage> list = this.mDependentPackages;
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "SharedLibraryInfo{name:" + this.mName + ", type:" + typeToString(this.mType) + ", version:" + this.mVersion + (!getDependentPackages().isEmpty() ? " has dependents" : "") + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString8(this.mPath);
        parcel.writeString8(this.mPackageName);
        if (this.mCodePaths != null) {
            parcel.writeInt(1);
            List<String> list = this.mCodePaths;
            parcel.writeString8Array((String[]) list.toArray(new String[list.size()]));
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString8(this.mName);
        parcel.writeLong(this.mVersion);
        parcel.writeInt(this.mType);
        parcel.writeParcelable(this.mDeclaringPackage, flags);
        parcel.writeList(this.mDependentPackages);
        parcel.writeTypedList(this.mDependencies);
        parcel.writeBoolean(this.mIsNative);
    }

    private static String typeToString(int type) {
        switch (type) {
            case 0:
                return "builtin";
            case 1:
                return "dynamic";
            case 2:
                return "static";
            case 3:
                return "sdk";
            default:
                return "unknown";
        }
    }
}
