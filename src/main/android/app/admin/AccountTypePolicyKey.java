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
public final class AccountTypePolicyKey extends PolicyKey {
    private static final String ATTR_ACCOUNT_TYPE = "account-type";
    public static final Parcelable.Creator<AccountTypePolicyKey> CREATOR = new Parcelable.Creator<AccountTypePolicyKey>() { // from class: android.app.admin.AccountTypePolicyKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccountTypePolicyKey createFromParcel(Parcel source) {
            return new AccountTypePolicyKey(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccountTypePolicyKey[] newArray(int size) {
            return new AccountTypePolicyKey[size];
        }
    };
    private final String mAccountType;

    public AccountTypePolicyKey(String key, String accountType) {
        super(key);
        this.mAccountType = (String) Objects.requireNonNull(accountType);
    }

    private AccountTypePolicyKey(Parcel source) {
        super(source.readString());
        this.mAccountType = source.readString();
    }

    public AccountTypePolicyKey(String key) {
        super(key);
        this.mAccountType = null;
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    @Override // android.app.admin.PolicyKey
    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        serializer.attribute(null, "policy-identifier", getIdentifier());
        serializer.attribute(null, ATTR_ACCOUNT_TYPE, this.mAccountType);
    }

    @Override // android.app.admin.PolicyKey
    public AccountTypePolicyKey readFromXml(TypedXmlPullParser parser) throws XmlPullParserException, IOException {
        String policyKey = parser.getAttributeValue(null, "policy-identifier");
        String accountType = parser.getAttributeValue(null, ATTR_ACCOUNT_TYPE);
        return new AccountTypePolicyKey(policyKey, accountType);
    }

    @Override // android.app.admin.PolicyKey
    public void writeToBundle(Bundle bundle) {
        bundle.putString(PolicyUpdateReceiver.EXTRA_POLICY_KEY, getIdentifier());
        Bundle extraPolicyParams = new Bundle();
        extraPolicyParams.putString(PolicyUpdateReceiver.EXTRA_ACCOUNT_TYPE, this.mAccountType);
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
        AccountTypePolicyKey other = (AccountTypePolicyKey) o;
        if (Objects.equals(getIdentifier(), other.getIdentifier()) && Objects.equals(this.mAccountType, other.mAccountType)) {
            return true;
        }
        return false;
    }

    @Override // android.app.admin.PolicyKey
    public int hashCode() {
        return Objects.hash(getIdentifier(), this.mAccountType);
    }

    public String toString() {
        return "AccountTypePolicyKey{mPolicyKey= " + getIdentifier() + "; mAccountType= " + this.mAccountType + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdentifier());
        dest.writeString(this.mAccountType);
    }
}
