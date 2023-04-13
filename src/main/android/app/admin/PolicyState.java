package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.LinkedHashMap;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class PolicyState<V> implements Parcelable {
    public static final Parcelable.Creator<PolicyState<?>> CREATOR = new Parcelable.Creator<PolicyState<?>>() { // from class: android.app.admin.PolicyState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PolicyState<?> createFromParcel(Parcel source) {
            return new PolicyState<>(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PolicyState<?>[] newArray(int size) {
            return new PolicyState[size];
        }
    };
    private PolicyValue<V> mCurrentResolvedPolicy;
    private final LinkedHashMap<EnforcingAdmin, PolicyValue<V>> mPoliciesSetByAdmins;
    private ResolutionMechanism<V> mResolutionMechanism;

    public PolicyState(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> policiesSetByAdmins, PolicyValue<V> currentEnforcedPolicy, ResolutionMechanism<V> resolutionMechanism) {
        LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap = new LinkedHashMap<>();
        this.mPoliciesSetByAdmins = linkedHashMap;
        Objects.requireNonNull(policiesSetByAdmins);
        Objects.requireNonNull(resolutionMechanism);
        linkedHashMap.putAll(policiesSetByAdmins);
        this.mCurrentResolvedPolicy = currentEnforcedPolicy;
        this.mResolutionMechanism = resolutionMechanism;
    }

    private PolicyState(Parcel source) {
        this.mPoliciesSetByAdmins = new LinkedHashMap<>();
        int size = source.readInt();
        for (int i = 0; i < size; i++) {
            EnforcingAdmin admin = (EnforcingAdmin) source.readParcelable(EnforcingAdmin.class.getClassLoader());
            PolicyValue<V> policyValue = (PolicyValue) source.readParcelable(PolicyValue.class.getClassLoader());
            this.mPoliciesSetByAdmins.put(admin, policyValue);
        }
        this.mCurrentResolvedPolicy = (PolicyValue) source.readParcelable(PolicyValue.class.getClassLoader());
        this.mResolutionMechanism = (ResolutionMechanism) source.readParcelable(ResolutionMechanism.class.getClassLoader());
    }

    public LinkedHashMap<EnforcingAdmin, V> getPoliciesSetByAdmins() {
        LinkedHashMap<EnforcingAdmin, V> policies = new LinkedHashMap<>();
        for (EnforcingAdmin admin : this.mPoliciesSetByAdmins.keySet()) {
            policies.put(admin, this.mPoliciesSetByAdmins.get(admin).getValue());
        }
        return policies;
    }

    public V getCurrentResolvedPolicy() {
        PolicyValue<V> policyValue = this.mCurrentResolvedPolicy;
        if (policyValue == null) {
            return null;
        }
        return policyValue.getValue();
    }

    public ResolutionMechanism<V> getResolutionMechanism() {
        return this.mResolutionMechanism;
    }

    public String toString() {
        return "PolicyState { mPoliciesSetByAdmins= " + this.mPoliciesSetByAdmins + ", mCurrentResolvedPolicy= " + this.mCurrentResolvedPolicy + ", mResolutionMechanism= " + this.mResolutionMechanism + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPoliciesSetByAdmins.size());
        for (EnforcingAdmin admin : this.mPoliciesSetByAdmins.keySet()) {
            dest.writeParcelable(admin, flags);
            dest.writeParcelable(this.mPoliciesSetByAdmins.get(admin), flags);
        }
        dest.writeParcelable(this.mCurrentResolvedPolicy, flags);
        dest.writeParcelable(this.mResolutionMechanism, flags);
    }
}
