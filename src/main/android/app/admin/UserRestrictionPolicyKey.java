package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class UserRestrictionPolicyKey extends PolicyKey {
    public static final Parcelable.Creator<UserRestrictionPolicyKey> CREATOR = new Parcelable.Creator<UserRestrictionPolicyKey>() { // from class: android.app.admin.UserRestrictionPolicyKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserRestrictionPolicyKey createFromParcel(Parcel source) {
            return new UserRestrictionPolicyKey(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserRestrictionPolicyKey[] newArray(int size) {
            return new UserRestrictionPolicyKey[size];
        }
    };
    private final String mRestriction;

    public UserRestrictionPolicyKey(String identifier, String restriction) {
        super(identifier);
        this.mRestriction = (String) Objects.requireNonNull(restriction);
    }

    private UserRestrictionPolicyKey(Parcel source) {
        this(source.readString(), source.readString());
    }

    public String getRestriction() {
        return this.mRestriction;
    }

    @Override // android.app.admin.PolicyKey
    public void writeToBundle(Bundle bundle) {
        bundle.putString(PolicyUpdateReceiver.EXTRA_POLICY_KEY, getIdentifier());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdentifier());
        dest.writeString(this.mRestriction);
    }

    public String toString() {
        return "UserRestrictionPolicyKey " + getIdentifier();
    }
}
