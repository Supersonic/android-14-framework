package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* loaded from: classes.dex */
public final class PackagePermissionPolicyKey extends PolicyKey {
    private static final String ATTR_PACKAGE_NAME = "package-name";
    private static final String ATTR_PERMISSION_NAME = "permission-name";
    public static final Parcelable.Creator<PackagePermissionPolicyKey> CREATOR = new Parcelable.Creator<PackagePermissionPolicyKey>() { // from class: android.app.admin.PackagePermissionPolicyKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePermissionPolicyKey createFromParcel(Parcel source) {
            return new PackagePermissionPolicyKey(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePermissionPolicyKey[] newArray(int size) {
            return new PackagePermissionPolicyKey[size];
        }
    };
    private final String mPackageName;
    private final String mPermissionName;

    public PackagePermissionPolicyKey(String identifier, String packageName, String permissionName) {
        super(identifier);
        this.mPackageName = (String) Objects.requireNonNull(packageName);
        this.mPermissionName = (String) Objects.requireNonNull(permissionName);
    }

    public PackagePermissionPolicyKey(String identifier) {
        super(identifier);
        this.mPackageName = null;
        this.mPermissionName = null;
    }

    private PackagePermissionPolicyKey(Parcel source) {
        super(source.readString());
        this.mPackageName = source.readString();
        this.mPermissionName = source.readString();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getPermissionName() {
        return this.mPermissionName;
    }

    @Override // android.app.admin.PolicyKey
    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        serializer.attribute(null, "policy-identifier", getIdentifier());
        serializer.attribute(null, ATTR_PACKAGE_NAME, this.mPackageName);
        serializer.attribute(null, ATTR_PERMISSION_NAME, this.mPermissionName);
    }

    @Override // android.app.admin.PolicyKey
    public PackagePermissionPolicyKey readFromXml(TypedXmlPullParser parser) throws XmlPullParserException, IOException {
        String identifier = parser.getAttributeValue(null, "policy-identifier");
        String packageName = parser.getAttributeValue(null, ATTR_PACKAGE_NAME);
        String permissionName = parser.getAttributeValue(null, ATTR_PERMISSION_NAME);
        return new PackagePermissionPolicyKey(identifier, packageName, permissionName);
    }

    @Override // android.app.admin.PolicyKey
    public void writeToBundle(Bundle bundle) {
        bundle.putString(PolicyUpdateReceiver.EXTRA_POLICY_KEY, getIdentifier());
        Bundle extraPolicyParams = new Bundle();
        extraPolicyParams.putString(PolicyUpdateReceiver.EXTRA_PACKAGE_NAME, this.mPackageName);
        extraPolicyParams.putString(PolicyUpdateReceiver.EXTRA_PERMISSION_NAME, this.mPermissionName);
        bundle.putBundle(PolicyUpdateReceiver.EXTRA_POLICY_BUNDLE_KEY, extraPolicyParams);
    }

    @Override // android.app.admin.PolicyKey
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PackagePermissionPolicyKey other = (PackagePermissionPolicyKey) o;
        if (Objects.equals(getIdentifier(), other.getIdentifier()) && Objects.equals(this.mPackageName, other.mPackageName) && Objects.equals(this.mPermissionName, other.mPermissionName)) {
            return true;
        }
        return false;
    }

    @Override // android.app.admin.PolicyKey
    public int hashCode() {
        return Objects.hash(getIdentifier(), this.mPackageName, this.mPermissionName);
    }

    public String toString() {
        return "PackagePermissionPolicyKey{mIdentifier= " + getIdentifier() + "; mPackageName= " + this.mPackageName + "; mPermissionName= " + this.mPermissionName + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdentifier());
        dest.writeString(this.mPackageName);
        dest.writeString(this.mPermissionName);
    }
}
