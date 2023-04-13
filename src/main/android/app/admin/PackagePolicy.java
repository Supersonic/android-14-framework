package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class PackagePolicy implements Parcelable {
    public static final Parcelable.Creator<PackagePolicy> CREATOR = new Parcelable.Creator<PackagePolicy>() { // from class: android.app.admin.PackagePolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePolicy createFromParcel(Parcel in) {
            return new PackagePolicy(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePolicy[] newArray(int size) {
            return new PackagePolicy[size];
        }
    };
    public static final int PACKAGE_POLICY_ALLOWLIST = 3;
    public static final int PACKAGE_POLICY_ALLOWLIST_AND_SYSTEM = 2;
    public static final int PACKAGE_POLICY_BLOCKLIST = 1;
    private ArraySet<String> mPackageNames;
    private int mPolicyType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PackagePolicyType {
    }

    public PackagePolicy(int policyType) {
        this(policyType, Collections.emptySet());
    }

    public PackagePolicy(int policyType, Set<String> packageNames) {
        if (policyType != 1 && policyType != 2 && policyType != 3) {
            throw new IllegalArgumentException("Invalid policy type");
        }
        this.mPolicyType = policyType;
        this.mPackageNames = new ArraySet<>(packageNames);
    }

    private PackagePolicy(Parcel in) {
        this.mPolicyType = in.readInt();
        this.mPackageNames = in.readArraySet(null);
    }

    public int getPolicyType() {
        return this.mPolicyType;
    }

    public Set<String> getPackageNames() {
        return Collections.unmodifiableSet(this.mPackageNames);
    }

    public boolean isPackageAllowed(String packageName, Set<String> systemPackages) {
        if (this.mPolicyType == 1) {
            return !this.mPackageNames.contains(packageName);
        }
        if (this.mPackageNames.contains(packageName)) {
            return true;
        }
        return this.mPolicyType == 2 && systemPackages.contains(packageName);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPolicyType);
        dest.writeArraySet(this.mPackageNames);
    }

    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject instanceof PackagePolicy) {
            PackagePolicy that = (PackagePolicy) thatObject;
            return this.mPolicyType == that.mPolicyType && this.mPackageNames.equals(that.mPackageNames);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPolicyType), this.mPackageNames);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
