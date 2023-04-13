package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class DevicePolicyState implements Parcelable {
    public static final Parcelable.Creator<DevicePolicyState> CREATOR = new Parcelable.Creator<DevicePolicyState>() { // from class: android.app.admin.DevicePolicyState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyState createFromParcel(Parcel source) {
            return new DevicePolicyState(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyState[] newArray(int size) {
            return new DevicePolicyState[size];
        }
    };
    private final Map<UserHandle, Map<PolicyKey, PolicyState<?>>> mPolicies;

    public DevicePolicyState(Map<UserHandle, Map<PolicyKey, PolicyState<?>>> policies) {
        this.mPolicies = (Map) Objects.requireNonNull(policies);
    }

    private DevicePolicyState(Parcel source) {
        this.mPolicies = new HashMap();
        int usersSize = source.readInt();
        for (int i = 0; i < usersSize; i++) {
            UserHandle userHandle = UserHandle.m145of(source.readInt());
            this.mPolicies.put(userHandle, new HashMap());
            int policiesSize = source.readInt();
            for (int j = 0; j < policiesSize; j++) {
                PolicyKey policyKey = (PolicyKey) source.readParcelable(PolicyKey.class.getClassLoader());
                PolicyState<?> policyState = (PolicyState) source.readParcelable(PolicyState.class.getClassLoader());
                this.mPolicies.get(userHandle).put(policyKey, policyState);
            }
        }
    }

    public Map<UserHandle, Map<PolicyKey, PolicyState<?>>> getPoliciesForAllUsers() {
        return this.mPolicies;
    }

    public Map<PolicyKey, PolicyState<?>> getPoliciesForUser(UserHandle user) {
        return this.mPolicies.containsKey(user) ? this.mPolicies.get(user) : new HashMap();
    }

    public String toString() {
        return "DevicePolicyState { mPolicies= " + this.mPolicies + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPolicies.size());
        for (UserHandle user : this.mPolicies.keySet()) {
            dest.writeInt(user.getIdentifier());
            dest.writeInt(this.mPolicies.get(user).size());
            for (PolicyKey key : this.mPolicies.get(user).keySet()) {
                dest.writeParcelable(key, flags);
                dest.writeParcelable(this.mPolicies.get(user).get(key), flags);
            }
        }
    }
}
