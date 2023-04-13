package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ManagedSubscriptionsPolicy implements Parcelable {
    public static final Parcelable.Creator<ManagedSubscriptionsPolicy> CREATOR = new Parcelable.Creator<ManagedSubscriptionsPolicy>() { // from class: android.app.admin.ManagedSubscriptionsPolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManagedSubscriptionsPolicy createFromParcel(Parcel in) {
            ManagedSubscriptionsPolicy policy = new ManagedSubscriptionsPolicy(in.readInt());
            return policy;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManagedSubscriptionsPolicy[] newArray(int size) {
            return new ManagedSubscriptionsPolicy[size];
        }
    };
    private static final String KEY_POLICY_TYPE = "policy_type";
    private static final String TAG = "ManagedSubscriptionsPolicy";
    public static final int TYPE_ALL_MANAGED_SUBSCRIPTIONS = 1;
    public static final int TYPE_ALL_PERSONAL_SUBSCRIPTIONS = 0;
    private final int mPolicyType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface ManagedSubscriptionsPolicyType {
    }

    public ManagedSubscriptionsPolicy(int policyType) {
        if (policyType != 0 && policyType != 1) {
            throw new IllegalArgumentException("Invalid policy type");
        }
        this.mPolicyType = policyType;
    }

    public int getPolicyType() {
        return this.mPolicyType;
    }

    public String toString() {
        return TextUtils.formatSimple("ManagedSubscriptionsPolicy (type: %d)", Integer.valueOf(this.mPolicyType));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPolicyType);
    }

    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (thatObject instanceof ManagedSubscriptionsPolicy) {
            ManagedSubscriptionsPolicy that = (ManagedSubscriptionsPolicy) thatObject;
            return this.mPolicyType == that.mPolicyType;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPolicyType));
    }

    public static ManagedSubscriptionsPolicy readFromXml(TypedXmlPullParser parser) {
        try {
            ManagedSubscriptionsPolicy policy = new ManagedSubscriptionsPolicy(parser.getAttributeInt(null, KEY_POLICY_TYPE, -1));
            return policy;
        } catch (IllegalArgumentException e) {
            Log.m103w(TAG, "Load xml failed", e);
            return null;
        }
    }

    public void saveToXml(TypedXmlSerializer out) throws IOException {
        out.attributeInt(null, KEY_POLICY_TYPE, this.mPolicyType);
    }
}
