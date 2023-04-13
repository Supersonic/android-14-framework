package com.android.server.devicepolicy;

import android.app.admin.PolicyValue;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class PolicyState<V> {
    public PolicyValue<V> mCurrentResolvedPolicy;
    public final LinkedHashMap<EnforcingAdmin, PolicyValue<V>> mPoliciesSetByAdmins;
    public final PolicyDefinition<V> mPolicyDefinition;

    public PolicyState(PolicyDefinition<V> policyDefinition) {
        this.mPoliciesSetByAdmins = new LinkedHashMap<>();
        Objects.requireNonNull(policyDefinition);
        this.mPolicyDefinition = policyDefinition;
    }

    public PolicyState(PolicyDefinition<V> policyDefinition, LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap, PolicyValue<V> policyValue) {
        LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap2 = new LinkedHashMap<>();
        this.mPoliciesSetByAdmins = linkedHashMap2;
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(linkedHashMap);
        this.mPolicyDefinition = policyDefinition;
        linkedHashMap2.putAll(linkedHashMap);
        this.mCurrentResolvedPolicy = policyValue;
    }

    public boolean addPolicy(EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue) {
        Objects.requireNonNull(enforcingAdmin);
        Objects.requireNonNull(policyValue);
        this.mPoliciesSetByAdmins.remove(enforcingAdmin);
        this.mPoliciesSetByAdmins.put(enforcingAdmin, policyValue);
        return resolvePolicy();
    }

    public boolean addPolicy(EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap2 = this.mPoliciesSetByAdmins;
        Objects.requireNonNull(enforcingAdmin);
        Objects.requireNonNull(policyValue);
        linkedHashMap2.put(enforcingAdmin, policyValue);
        return resolvePolicy(linkedHashMap);
    }

    public boolean removePolicy(EnforcingAdmin enforcingAdmin) {
        Objects.requireNonNull(enforcingAdmin);
        if (this.mPoliciesSetByAdmins.remove(enforcingAdmin) == null) {
            return false;
        }
        return resolvePolicy();
    }

    public boolean removePolicy(EnforcingAdmin enforcingAdmin, LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        Objects.requireNonNull(enforcingAdmin);
        if (this.mPoliciesSetByAdmins.remove(enforcingAdmin) == null) {
            return false;
        }
        return resolvePolicy(linkedHashMap);
    }

    public boolean resolvePolicy(LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap) {
        if (this.mPolicyDefinition.isNonCoexistablePolicy()) {
            return false;
        }
        LinkedHashMap<EnforcingAdmin, PolicyValue<V>> linkedHashMap2 = new LinkedHashMap<>(linkedHashMap);
        linkedHashMap2.putAll(this.mPoliciesSetByAdmins);
        PolicyValue<V> resolvePolicy = this.mPolicyDefinition.resolvePolicy(linkedHashMap2);
        boolean z = !Objects.equals(resolvePolicy, this.mCurrentResolvedPolicy);
        this.mCurrentResolvedPolicy = resolvePolicy;
        return z;
    }

    public LinkedHashMap<EnforcingAdmin, PolicyValue<V>> getPoliciesSetByAdmins() {
        return new LinkedHashMap<>(this.mPoliciesSetByAdmins);
    }

    public final boolean resolvePolicy() {
        if (this.mPolicyDefinition.isNonCoexistablePolicy()) {
            return false;
        }
        PolicyValue<V> resolvePolicy = this.mPolicyDefinition.resolvePolicy(this.mPoliciesSetByAdmins);
        boolean z = !Objects.equals(resolvePolicy, this.mCurrentResolvedPolicy);
        this.mCurrentResolvedPolicy = resolvePolicy;
        return z;
    }

    public PolicyValue<V> getCurrentResolvedPolicy() {
        return this.mCurrentResolvedPolicy;
    }

    public android.app.admin.PolicyState<V> getParcelablePolicyState() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (EnforcingAdmin enforcingAdmin : this.mPoliciesSetByAdmins.keySet()) {
            linkedHashMap.put(enforcingAdmin.getParcelableAdmin(), this.mPoliciesSetByAdmins.get(enforcingAdmin));
        }
        return new android.app.admin.PolicyState<>(linkedHashMap, this.mCurrentResolvedPolicy, this.mPolicyDefinition.getResolutionMechanism().mo3049getParcelableResolutionMechanism());
    }

    public String toString() {
        return "PolicyState { mPolicyDefinition= " + this.mPolicyDefinition + ", mPoliciesSetByAdmins= " + this.mPoliciesSetByAdmins + ", mCurrentResolvedPolicy= " + this.mCurrentResolvedPolicy + " }";
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        this.mPolicyDefinition.saveToXml(typedXmlSerializer);
        PolicyValue<V> policyValue = this.mCurrentResolvedPolicy;
        if (policyValue != null) {
            this.mPolicyDefinition.savePolicyValueToXml(typedXmlSerializer, "resolved-policy", policyValue.getValue());
        }
        for (EnforcingAdmin enforcingAdmin : this.mPoliciesSetByAdmins.keySet()) {
            typedXmlSerializer.startTag((String) null, "admin-policy-entry");
            this.mPolicyDefinition.savePolicyValueToXml(typedXmlSerializer, "policy-value", this.mPoliciesSetByAdmins.get(enforcingAdmin).getValue());
            typedXmlSerializer.startTag((String) null, "enforcing-admin-entry");
            enforcingAdmin.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "enforcing-admin-entry");
            typedXmlSerializer.endTag((String) null, "admin-policy-entry");
        }
    }

    public static <V> PolicyState<V> readFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        PolicyDefinition readFromXml = PolicyDefinition.readFromXml(typedXmlPullParser);
        PolicyValue<V> readPolicyValueFromXml = readFromXml.readPolicyValueFromXml(typedXmlPullParser, "resolved-policy");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            String name = typedXmlPullParser.getName();
            if ("admin-policy-entry".equals(name)) {
                PolicyValue<V> readPolicyValueFromXml2 = readFromXml.readPolicyValueFromXml(typedXmlPullParser, "policy-value");
                if (XmlUtils.nextElementWithin(typedXmlPullParser, typedXmlPullParser.getDepth()) && typedXmlPullParser.getName().equals("enforcing-admin-entry")) {
                    linkedHashMap.put(EnforcingAdmin.readFromXml(typedXmlPullParser), readPolicyValueFromXml2);
                }
            } else {
                Log.e("DevicePolicyEngine", "Unknown tag: " + name);
            }
        }
        return new PolicyState<>(readFromXml, linkedHashMap, readPolicyValueFromXml);
    }

    public PolicyDefinition<V> getPolicyDefinition() {
        return this.mPolicyDefinition;
    }
}
