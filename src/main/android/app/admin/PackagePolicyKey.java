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
public final class PackagePolicyKey extends PolicyKey {
    private static final String ATTR_PACKAGE_NAME = "package-name";
    public static final Parcelable.Creator<PackagePolicyKey> CREATOR = new Parcelable.Creator<PackagePolicyKey>() { // from class: android.app.admin.PackagePolicyKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePolicyKey createFromParcel(Parcel source) {
            return new PackagePolicyKey(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackagePolicyKey[] newArray(int size) {
            return new PackagePolicyKey[size];
        }
    };
    private final String mPackageName;

    public PackagePolicyKey(String key, String packageName) {
        super(key);
        this.mPackageName = (String) Objects.requireNonNull(packageName);
    }

    private PackagePolicyKey(Parcel source) {
        super(source.readString());
        this.mPackageName = source.readString();
    }

    public PackagePolicyKey(String key) {
        super(key);
        this.mPackageName = null;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    @Override // android.app.admin.PolicyKey
    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        serializer.attribute(null, "policy-identifier", getIdentifier());
        serializer.attribute(null, ATTR_PACKAGE_NAME, this.mPackageName);
    }

    @Override // android.app.admin.PolicyKey
    public PackagePolicyKey readFromXml(TypedXmlPullParser parser) throws XmlPullParserException, IOException {
        String policyKey = parser.getAttributeValue(null, "policy-identifier");
        String packageName = parser.getAttributeValue(null, ATTR_PACKAGE_NAME);
        return new PackagePolicyKey(policyKey, packageName);
    }

    @Override // android.app.admin.PolicyKey
    public void writeToBundle(Bundle bundle) {
        bundle.putString(PolicyUpdateReceiver.EXTRA_POLICY_KEY, getIdentifier());
        Bundle extraPolicyParams = new Bundle();
        extraPolicyParams.putString(PolicyUpdateReceiver.EXTRA_PACKAGE_NAME, this.mPackageName);
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
        PackagePolicyKey other = (PackagePolicyKey) o;
        if (Objects.equals(getIdentifier(), other.getIdentifier()) && Objects.equals(this.mPackageName, other.mPackageName)) {
            return true;
        }
        return false;
    }

    @Override // android.app.admin.PolicyKey
    public int hashCode() {
        return Objects.hash(getIdentifier(), this.mPackageName);
    }

    public String toString() {
        return "PackagePolicyKey{mPolicyKey= " + getIdentifier() + "; mPackageName= " + this.mPackageName + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdentifier());
        dest.writeString(this.mPackageName);
    }
}
