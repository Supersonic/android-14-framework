package com.android.server.devicepolicy;

import android.app.admin.DevicePolicyState;
import android.app.admin.PolicyKey;
import android.app.admin.PolicyValue;
import android.app.admin.UserRestrictionPolicyKey;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.util.AtomicFile;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class DevicePolicyEngine {
    public final Context mContext;
    public final DeviceAdminServiceController mDeviceAdminServiceController;
    public final SparseArray<Set<EnforcingAdmin>> mEnforcingAdmins;
    public final Map<PolicyKey, PolicyState<?>> mGlobalPolicies;
    public final SparseArray<Map<PolicyKey, PolicyState<?>>> mLocalPolicies;
    public final Object mLock = new Object();
    public final UserManager mUserManager;

    public DevicePolicyEngine(Context context, DeviceAdminServiceController deviceAdminServiceController) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(deviceAdminServiceController);
        this.mDeviceAdminServiceController = deviceAdminServiceController;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mLocalPolicies = new SparseArray<>();
        this.mGlobalPolicies = new HashMap();
        this.mEnforcingAdmins = new SparseArray<>();
    }

    public <V> void setLocalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, int i, boolean z) {
        boolean addPolicy;
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        Objects.requireNonNull(policyValue);
        synchronized (this.mLock) {
            PolicyState<V> localPolicyStateLocked = getLocalPolicyStateLocked(policyDefinition, i);
            if (policyDefinition.isNonCoexistablePolicy()) {
                setNonCoexistableLocalPolicy(policyDefinition, localPolicyStateLocked, enforcingAdmin, policyValue, i, z);
                return;
            }
            if (hasGlobalPolicyLocked(policyDefinition)) {
                addPolicy = localPolicyStateLocked.addPolicy(enforcingAdmin, policyValue, getGlobalPolicyStateLocked(policyDefinition).getPoliciesSetByAdmins());
            } else {
                addPolicy = localPolicyStateLocked.addPolicy(enforcingAdmin, policyValue);
            }
            if (!z) {
                if (addPolicy) {
                    onLocalPolicyChanged(policyDefinition, enforcingAdmin, i);
                }
                sendPolicyResultToAdmin(enforcingAdmin, policyDefinition, Objects.equals(localPolicyStateLocked.getCurrentResolvedPolicy(), policyValue) ? 0 : 1, i);
            }
            updateDeviceAdminServiceOnPolicyAddLocked(enforcingAdmin);
            write();
            applyToInheritableProfiles(policyDefinition, enforcingAdmin, policyValue, i);
        }
    }

    public final <V> void setNonCoexistableLocalPolicy(PolicyDefinition<V> policyDefinition, PolicyState<V> policyState, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, int i, boolean z) {
        if (policyValue == null) {
            policyState.removePolicy(enforcingAdmin);
        } else {
            policyState.addPolicy(enforcingAdmin, policyValue);
        }
        if (!z) {
            enforcePolicy(policyDefinition, policyValue, i);
        }
        if (policyState.getPoliciesSetByAdmins().isEmpty()) {
            removeLocalPolicyStateLocked(policyDefinition, i);
        }
        updateDeviceAdminServiceOnPolicyAddLocked(enforcingAdmin);
        write();
    }

    public <V> void setLocalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, int i) {
        setLocalPolicy(policyDefinition, enforcingAdmin, policyValue, i, false);
    }

    public <V> void removeLocalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, int i) {
        boolean removePolicy;
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        synchronized (this.mLock) {
            if (hasLocalPolicyLocked(policyDefinition, i)) {
                PolicyState<V> localPolicyStateLocked = getLocalPolicyStateLocked(policyDefinition, i);
                if (policyDefinition.isNonCoexistablePolicy()) {
                    setNonCoexistableLocalPolicy(policyDefinition, localPolicyStateLocked, enforcingAdmin, null, i, false);
                    return;
                }
                if (hasGlobalPolicyLocked(policyDefinition)) {
                    removePolicy = localPolicyStateLocked.removePolicy(enforcingAdmin, getGlobalPolicyStateLocked(policyDefinition).getPoliciesSetByAdmins());
                } else {
                    removePolicy = localPolicyStateLocked.removePolicy(enforcingAdmin);
                }
                if (removePolicy) {
                    onLocalPolicyChanged(policyDefinition, enforcingAdmin, i);
                }
                sendPolicyResultToAdmin(enforcingAdmin, policyDefinition, 2, i);
                if (localPolicyStateLocked.getPoliciesSetByAdmins().isEmpty()) {
                    removeLocalPolicyStateLocked(policyDefinition, i);
                }
                updateDeviceAdminServiceOnPolicyRemoveLocked(enforcingAdmin);
                write();
                applyToInheritableProfiles(policyDefinition, enforcingAdmin, null, i);
            }
        }
    }

    public final <V> void applyToInheritableProfiles(final PolicyDefinition<V> policyDefinition, final EnforcingAdmin enforcingAdmin, final PolicyValue<V> policyValue, final int i) {
        if (policyDefinition.isInheritable()) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyEngine$$ExternalSyntheticLambda0
                public final void runOrThrow() {
                    DevicePolicyEngine.this.lambda$applyToInheritableProfiles$0(i, policyValue, policyDefinition, enforcingAdmin);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyToInheritableProfiles$0(int i, PolicyValue policyValue, PolicyDefinition policyDefinition, EnforcingAdmin enforcingAdmin) throws Exception {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            int identifier = userInfo.getUserHandle().getIdentifier();
            if (isProfileOfUser(identifier, i) && isInheritDevicePolicyFromParent(userInfo)) {
                if (policyValue != null) {
                    setLocalPolicy(policyDefinition, enforcingAdmin, policyValue, identifier);
                } else {
                    removeLocalPolicy(policyDefinition, enforcingAdmin, identifier);
                }
            }
        }
    }

    public final boolean isProfileOfUser(int i, int i2) {
        UserInfo profileParent = this.mUserManager.getProfileParent(i);
        return (i == i2 || profileParent == null || profileParent.getUserHandle().getIdentifier() != i2) ? false : true;
    }

    public final boolean isInheritDevicePolicyFromParent(UserInfo userInfo) {
        return this.mUserManager.getUserProperties(userInfo.getUserHandle()) != null && this.mUserManager.getUserProperties(userInfo.getUserHandle()).getInheritDevicePolicy() == 1;
    }

    public final <V> void onLocalPolicyChanged(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, int i) {
        PolicyState<V> localPolicyStateLocked = getLocalPolicyStateLocked(policyDefinition, i);
        enforcePolicy(policyDefinition, localPolicyStateLocked.getCurrentResolvedPolicy(), i);
        sendPolicyChangedToAdmins(localPolicyStateLocked, enforcingAdmin, policyDefinition, i);
        if (hasGlobalPolicyLocked(policyDefinition)) {
            sendPolicyChangedToAdmins(getGlobalPolicyStateLocked(policyDefinition), enforcingAdmin, policyDefinition, i);
        }
    }

    public <V> void setGlobalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue) {
        setGlobalPolicy(policyDefinition, enforcingAdmin, policyValue, false);
    }

    public <V> void setGlobalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, boolean z) {
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        Objects.requireNonNull(policyValue);
        synchronized (this.mLock) {
            PolicyState<V> globalPolicyStateLocked = getGlobalPolicyStateLocked(policyDefinition);
            boolean addPolicy = globalPolicyStateLocked.addPolicy(enforcingAdmin, policyValue);
            boolean applyGlobalPolicyOnUsersWithLocalPoliciesLocked = applyGlobalPolicyOnUsersWithLocalPoliciesLocked(policyDefinition, enforcingAdmin, policyValue, z);
            if (!z) {
                if (addPolicy) {
                    onGlobalPolicyChanged(policyDefinition, enforcingAdmin);
                }
                sendPolicyResultToAdmin(enforcingAdmin, policyDefinition, Objects.equals(globalPolicyStateLocked.getCurrentResolvedPolicy(), policyValue) && applyGlobalPolicyOnUsersWithLocalPoliciesLocked ? 0 : 1, -1);
            }
            updateDeviceAdminServiceOnPolicyAddLocked(enforcingAdmin);
            write();
        }
    }

    public <V> void removeGlobalPolicy(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin) {
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        synchronized (this.mLock) {
            PolicyState<V> globalPolicyStateLocked = getGlobalPolicyStateLocked(policyDefinition);
            if (globalPolicyStateLocked.removePolicy(enforcingAdmin)) {
                onGlobalPolicyChanged(policyDefinition, enforcingAdmin);
            }
            applyGlobalPolicyOnUsersWithLocalPoliciesLocked(policyDefinition, enforcingAdmin, null, true);
            sendPolicyResultToAdmin(enforcingAdmin, policyDefinition, 2, -1);
            if (globalPolicyStateLocked.getPoliciesSetByAdmins().isEmpty()) {
                removeGlobalPolicyStateLocked(policyDefinition);
            }
            updateDeviceAdminServiceOnPolicyRemoveLocked(enforcingAdmin);
            write();
        }
    }

    public final <V> void onGlobalPolicyChanged(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin) {
        PolicyState<V> globalPolicyStateLocked = getGlobalPolicyStateLocked(policyDefinition);
        enforcePolicy(policyDefinition, globalPolicyStateLocked.getCurrentResolvedPolicy(), -1);
        sendPolicyChangedToAdmins(globalPolicyStateLocked, enforcingAdmin, policyDefinition, -1);
    }

    public final <V> boolean applyGlobalPolicyOnUsersWithLocalPoliciesLocked(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, PolicyValue<V> policyValue, boolean z) {
        boolean z2 = true;
        if (policyDefinition.isGlobalOnlyPolicy()) {
            return true;
        }
        for (int i = 0; i < this.mLocalPolicies.size(); i++) {
            int keyAt = this.mLocalPolicies.keyAt(i);
            if (hasLocalPolicyLocked(policyDefinition, keyAt)) {
                PolicyState<V> localPolicyStateLocked = getLocalPolicyStateLocked(policyDefinition, keyAt);
                if (localPolicyStateLocked.resolvePolicy(getGlobalPolicyStateLocked(policyDefinition).getPoliciesSetByAdmins()) && !z) {
                    enforcePolicy(policyDefinition, localPolicyStateLocked.getCurrentResolvedPolicy(), keyAt);
                    sendPolicyChangedToAdmins(localPolicyStateLocked, enforcingAdmin, policyDefinition, keyAt);
                }
                z2 &= Objects.equals(policyValue, localPolicyStateLocked.getCurrentResolvedPolicy());
            }
        }
        return z2;
    }

    public <V> V getResolvedPolicy(PolicyDefinition<V> policyDefinition, int i) {
        V v;
        Objects.requireNonNull(policyDefinition);
        synchronized (this.mLock) {
            v = null;
            PolicyValue<V> currentResolvedPolicy = hasLocalPolicyLocked(policyDefinition, i) ? getLocalPolicyStateLocked(policyDefinition, i).getCurrentResolvedPolicy() : null;
            if (hasGlobalPolicyLocked(policyDefinition)) {
                currentResolvedPolicy = getGlobalPolicyStateLocked(policyDefinition).getCurrentResolvedPolicy();
            }
            if (currentResolvedPolicy != null) {
                v = (V) currentResolvedPolicy.getValue();
            }
        }
        return v;
    }

    public <V> V getLocalPolicySetByAdmin(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, int i) {
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        synchronized (this.mLock) {
            V v = null;
            if (hasLocalPolicyLocked(policyDefinition, i)) {
                PolicyValue<V> policyValue = getLocalPolicyStateLocked(policyDefinition, i).getPoliciesSetByAdmins().get(enforcingAdmin);
                if (policyValue != null) {
                    v = (V) policyValue.getValue();
                }
                return v;
            }
            return null;
        }
    }

    public <V> LinkedHashMap<EnforcingAdmin, PolicyValue<V>> getLocalPoliciesSetByAdmins(PolicyDefinition<V> policyDefinition, int i) {
        Objects.requireNonNull(policyDefinition);
        synchronized (this.mLock) {
            if (!hasLocalPolicyLocked(policyDefinition, i)) {
                return new LinkedHashMap<>();
            }
            return getLocalPolicyStateLocked(policyDefinition, i).getPoliciesSetByAdmins();
        }
    }

    public <V> Set<PolicyKey> getLocalPolicyKeysSetByAdmin(PolicyDefinition<V> policyDefinition, EnforcingAdmin enforcingAdmin, int i) {
        Objects.requireNonNull(policyDefinition);
        Objects.requireNonNull(enforcingAdmin);
        synchronized (this.mLock) {
            if (!policyDefinition.isGlobalOnlyPolicy() && this.mLocalPolicies.contains(i)) {
                HashSet hashSet = new HashSet();
                for (PolicyKey policyKey : this.mLocalPolicies.get(i).keySet()) {
                    if (policyKey.hasSameIdentifierAs(policyDefinition.getPolicyKey()) && this.mLocalPolicies.get(i).get(policyKey).getPoliciesSetByAdmins().containsKey(enforcingAdmin)) {
                        hashSet.add(policyKey);
                    }
                }
                return hashSet;
            }
            return Set.of();
        }
    }

    public Set<UserRestrictionPolicyKey> getUserRestrictionPolicyKeysForAdmin(EnforcingAdmin enforcingAdmin, int i) {
        Objects.requireNonNull(enforcingAdmin);
        synchronized (this.mLock) {
            if (i == -1) {
                return getUserRestrictionPolicyKeysForAdminLocked(this.mGlobalPolicies, enforcingAdmin);
            } else if (!this.mLocalPolicies.contains(i)) {
                return Set.of();
            } else {
                return getUserRestrictionPolicyKeysForAdminLocked(this.mLocalPolicies.get(i), enforcingAdmin);
            }
        }
    }

    public final Set<UserRestrictionPolicyKey> getUserRestrictionPolicyKeysForAdminLocked(Map<PolicyKey, PolicyState<?>> map, EnforcingAdmin enforcingAdmin) {
        PolicyValue<?> policyValue;
        HashSet hashSet = new HashSet();
        Iterator<PolicyKey> it = map.keySet().iterator();
        while (it.hasNext()) {
            UserRestrictionPolicyKey userRestrictionPolicyKey = (PolicyKey) it.next();
            if (map.get(userRestrictionPolicyKey).getPolicyDefinition().isUserRestrictionPolicy() && (policyValue = map.get(userRestrictionPolicyKey).getPoliciesSetByAdmins().get(enforcingAdmin)) != null && ((Boolean) policyValue.getValue()).booleanValue()) {
                hashSet.add(userRestrictionPolicyKey);
            }
        }
        return hashSet;
    }

    public final <V> boolean hasLocalPolicyLocked(PolicyDefinition<V> policyDefinition, int i) {
        if (!policyDefinition.isGlobalOnlyPolicy() && this.mLocalPolicies.contains(i) && this.mLocalPolicies.get(i).containsKey(policyDefinition.getPolicyKey())) {
            return !this.mLocalPolicies.get(i).get(policyDefinition.getPolicyKey()).getPoliciesSetByAdmins().isEmpty();
        }
        return false;
    }

    public final <V> boolean hasGlobalPolicyLocked(PolicyDefinition<V> policyDefinition) {
        if (!policyDefinition.isLocalOnlyPolicy() && this.mGlobalPolicies.containsKey(policyDefinition.getPolicyKey())) {
            return !this.mGlobalPolicies.get(policyDefinition.getPolicyKey()).getPoliciesSetByAdmins().isEmpty();
        }
        return false;
    }

    public final <V> PolicyState<V> getLocalPolicyStateLocked(PolicyDefinition<V> policyDefinition, int i) {
        if (policyDefinition.isGlobalOnlyPolicy()) {
            throw new IllegalArgumentException(policyDefinition.getPolicyKey() + " is a global onlypolicy.");
        }
        if (!this.mLocalPolicies.contains(i)) {
            this.mLocalPolicies.put(i, new HashMap());
        }
        if (!this.mLocalPolicies.get(i).containsKey(policyDefinition.getPolicyKey())) {
            this.mLocalPolicies.get(i).put(policyDefinition.getPolicyKey(), new PolicyState<>(policyDefinition));
        }
        return getPolicyState(this.mLocalPolicies.get(i), policyDefinition);
    }

    public final <V> void removeLocalPolicyStateLocked(PolicyDefinition<V> policyDefinition, int i) {
        if (this.mLocalPolicies.contains(i)) {
            this.mLocalPolicies.get(i).remove(policyDefinition.getPolicyKey());
        }
    }

    public final <V> PolicyState<V> getGlobalPolicyStateLocked(PolicyDefinition<V> policyDefinition) {
        if (policyDefinition.isLocalOnlyPolicy()) {
            throw new IllegalArgumentException(policyDefinition.getPolicyKey() + " is a local onlypolicy.");
        }
        if (!this.mGlobalPolicies.containsKey(policyDefinition.getPolicyKey())) {
            this.mGlobalPolicies.put(policyDefinition.getPolicyKey(), new PolicyState<>(policyDefinition));
        }
        return getPolicyState(this.mGlobalPolicies, policyDefinition);
    }

    public final <V> void removeGlobalPolicyStateLocked(PolicyDefinition<V> policyDefinition) {
        this.mGlobalPolicies.remove(policyDefinition.getPolicyKey());
    }

    public static <V> PolicyState<V> getPolicyState(Map<PolicyKey, PolicyState<?>> map, PolicyDefinition<V> policyDefinition) {
        try {
            return (PolicyState<V>) map.get(policyDefinition.getPolicyKey());
        } catch (ClassCastException unused) {
            throw new IllegalArgumentException();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <V> void enforcePolicy(PolicyDefinition<V> policyDefinition, PolicyValue<V> policyValue, int i) {
        policyDefinition.enforcePolicy(policyValue == null ? null : policyValue.getValue(), this.mContext, i);
    }

    public final <V> void sendPolicyResultToAdmin(EnforcingAdmin enforcingAdmin, PolicyDefinition<V> policyDefinition, int i, int i2) {
        Intent intent = new Intent("android.app.admin.action.DEVICE_POLICY_SET_RESULT");
        intent.setPackage(enforcingAdmin.getPackageName());
        List<ResolveInfo> queryBroadcastReceiversAsUser = this.mContext.getPackageManager().queryBroadcastReceiversAsUser(intent, PackageManager.ResolveInfoFlags.of(2L), enforcingAdmin.getUserId());
        if (queryBroadcastReceiversAsUser.isEmpty()) {
            Log.i("DevicePolicyEngine", "Couldn't find any receivers that handle ACTION_DEVICE_POLICY_SET_RESULTin package " + enforcingAdmin.getPackageName());
            return;
        }
        Bundle bundle = new Bundle();
        policyDefinition.getPolicyKey().writeToBundle(bundle);
        bundle.putInt("android.app.admin.extra.POLICY_TARGET_USER_ID", getTargetUser(enforcingAdmin.getUserId(), i2));
        bundle.putInt("android.app.admin.extra.POLICY_UPDATE_RESULT_KEY", i);
        intent.putExtras(bundle);
        maybeSendIntentToAdminReceivers(intent, UserHandle.of(enforcingAdmin.getUserId()), queryBroadcastReceiversAsUser);
    }

    public final <V> void sendPolicyChangedToAdmins(PolicyState<V> policyState, EnforcingAdmin enforcingAdmin, PolicyDefinition<V> policyDefinition, int i) {
        for (EnforcingAdmin enforcingAdmin2 : policyState.getPoliciesSetByAdmins().keySet()) {
            if (!enforcingAdmin2.equals(enforcingAdmin)) {
                maybeSendOnPolicyChanged(enforcingAdmin2, policyDefinition, !Objects.equals(policyState.getPoliciesSetByAdmins().get(enforcingAdmin2), policyState.getCurrentResolvedPolicy()) ? 1 : 0, i);
            }
        }
    }

    public final <V> void maybeSendOnPolicyChanged(EnforcingAdmin enforcingAdmin, PolicyDefinition<V> policyDefinition, int i, int i2) {
        Intent intent = new Intent("android.app.admin.action.DEVICE_POLICY_CHANGED");
        intent.setPackage(enforcingAdmin.getPackageName());
        List<ResolveInfo> queryBroadcastReceiversAsUser = this.mContext.getPackageManager().queryBroadcastReceiversAsUser(intent, PackageManager.ResolveInfoFlags.of(2L), enforcingAdmin.getUserId());
        if (queryBroadcastReceiversAsUser.isEmpty()) {
            Log.i("DevicePolicyEngine", "Couldn't find any receivers that handle ACTION_DEVICE_POLICY_CHANGEDin package " + enforcingAdmin.getPackageName());
            return;
        }
        Bundle bundle = new Bundle();
        policyDefinition.getPolicyKey().writeToBundle(bundle);
        bundle.putInt("android.app.admin.extra.POLICY_TARGET_USER_ID", getTargetUser(enforcingAdmin.getUserId(), i2));
        bundle.putInt("android.app.admin.extra.POLICY_UPDATE_RESULT_KEY", i);
        intent.putExtras(bundle);
        intent.addFlags(268435456);
        maybeSendIntentToAdminReceivers(intent, UserHandle.of(enforcingAdmin.getUserId()), queryBroadcastReceiversAsUser);
    }

    public final void maybeSendIntentToAdminReceivers(Intent intent, UserHandle userHandle, List<ResolveInfo> list) {
        for (ResolveInfo resolveInfo : list) {
            if ("android.permission.BIND_DEVICE_ADMIN".equals(resolveInfo.activityInfo.permission)) {
                this.mContext.sendBroadcastAsUser(intent, userHandle);
            } else {
                Log.w("DevicePolicyEngine", "Receiver " + resolveInfo.activityInfo + " is not protected byBIND_DEVICE_ADMIN permission!");
            }
        }
    }

    public final int getTargetUser(int i, int i2) {
        if (i2 == -1) {
            return -3;
        }
        if (i == i2) {
            return -1;
        }
        return getProfileParentId(i) == i2 ? -2 : -3;
    }

    public final int getProfileParentId(final int i) {
        return ((Integer) Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingSupplier() { // from class: com.android.server.devicepolicy.DevicePolicyEngine$$ExternalSyntheticLambda2
            public final Object getOrThrow() {
                Integer lambda$getProfileParentId$1;
                lambda$getProfileParentId$1 = DevicePolicyEngine.this.lambda$getProfileParentId$1(i);
                return lambda$getProfileParentId$1;
            }
        })).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$getProfileParentId$1(int i) throws Exception {
        UserInfo profileParent = this.mUserManager.getProfileParent(i);
        if (profileParent != null) {
            i = profileParent.id;
        }
        return Integer.valueOf(i);
    }

    public final void updateDeviceAdminsServicesForUser(int i, boolean z, String str) {
        if (!z) {
            this.mDeviceAdminServiceController.stopServicesForUser(i, str);
            return;
        }
        for (EnforcingAdmin enforcingAdmin : getEnforcingAdminsForUser(i)) {
            if (!enforcingAdmin.hasAuthority("enterprise")) {
                this.mDeviceAdminServiceController.startServiceForAdmin(enforcingAdmin.getPackageName(), i, str);
            }
        }
    }

    public void handleUserCreated(UserInfo userInfo) {
        enforcePoliciesOnInheritableProfilesIfApplicable(userInfo);
    }

    public final void enforcePoliciesOnInheritableProfilesIfApplicable(final UserInfo userInfo) {
        if (userInfo.isProfile()) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.devicepolicy.DevicePolicyEngine$$ExternalSyntheticLambda3
                public final void runOrThrow() {
                    DevicePolicyEngine.this.lambda$enforcePoliciesOnInheritableProfilesIfApplicable$2(userInfo);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enforcePoliciesOnInheritableProfilesIfApplicable$2(UserInfo userInfo) throws Exception {
        int i;
        UserInfo profileParent;
        UserProperties userProperties = this.mUserManager.getUserProperties(userInfo.getUserHandle());
        if (userProperties == null || userProperties.getInheritDevicePolicy() != 1 || (profileParent = this.mUserManager.getProfileParent((i = userInfo.id))) == null || profileParent.getUserHandle().getIdentifier() == i) {
            return;
        }
        for (Map.Entry<PolicyKey, PolicyState<?>> entry : this.mLocalPolicies.get(profileParent.getUserHandle().getIdentifier()).entrySet()) {
            enforcePolicyOnUser(i, entry.getValue());
        }
    }

    public final <V> void enforcePolicyOnUser(int i, PolicyState<V> policyState) {
        if (policyState.getPolicyDefinition().isInheritable()) {
            for (Map.Entry<EnforcingAdmin, PolicyValue<V>> entry : policyState.getPoliciesSetByAdmins().entrySet()) {
                setLocalPolicy(policyState.getPolicyDefinition(), entry.getKey(), entry.getValue(), i);
            }
        }
    }

    public void handleStartUser(int i) {
        updateDeviceAdminsServicesForUser(i, true, "start-user");
    }

    public void handleUnlockUser(int i) {
        updateDeviceAdminsServicesForUser(i, true, "unlock-user");
    }

    public void handleStopUser(int i) {
        updateDeviceAdminsServicesForUser(i, false, "stop-user");
    }

    public void handlePackageChanged(String str, int i) {
        if (str == null) {
            return;
        }
        updateDeviceAdminServiceOnPackageChanged(str, i);
    }

    public DevicePolicyState getDevicePolicyState() {
        HashMap hashMap = new HashMap();
        for (int i = 0; i < this.mLocalPolicies.size(); i++) {
            UserHandle of = UserHandle.of(this.mLocalPolicies.keyAt(i));
            hashMap.put(of, new HashMap());
            for (PolicyKey policyKey : this.mLocalPolicies.valueAt(i).keySet()) {
                ((Map) hashMap.get(of)).put(policyKey, this.mLocalPolicies.valueAt(i).get(policyKey).getParcelablePolicyState());
            }
        }
        if (!this.mGlobalPolicies.isEmpty()) {
            hashMap.put(UserHandle.ALL, new HashMap());
            for (PolicyKey policyKey2 : this.mGlobalPolicies.keySet()) {
                ((Map) hashMap.get(UserHandle.ALL)).put(policyKey2, this.mGlobalPolicies.get(policyKey2).getParcelablePolicyState());
            }
        }
        return new DevicePolicyState(hashMap);
    }

    public final void updateDeviceAdminServiceOnPackageChanged(String str, int i) {
        for (EnforcingAdmin enforcingAdmin : getEnforcingAdminsForUser(i)) {
            if (!enforcingAdmin.hasAuthority("enterprise") && str.equals(enforcingAdmin.getPackageName())) {
                this.mDeviceAdminServiceController.startServiceForAdmin(str, i, "package-broadcast");
            }
        }
    }

    public final void updateDeviceAdminServiceOnPolicyAddLocked(EnforcingAdmin enforcingAdmin) {
        int userId = enforcingAdmin.getUserId();
        if (this.mEnforcingAdmins.contains(userId) && this.mEnforcingAdmins.get(userId).contains(enforcingAdmin)) {
            return;
        }
        if (!this.mEnforcingAdmins.contains(enforcingAdmin.getUserId())) {
            this.mEnforcingAdmins.put(enforcingAdmin.getUserId(), new HashSet());
        }
        this.mEnforcingAdmins.get(enforcingAdmin.getUserId()).add(enforcingAdmin);
        if (enforcingAdmin.hasAuthority("enterprise")) {
            return;
        }
        this.mDeviceAdminServiceController.startServiceForAdmin(enforcingAdmin.getPackageName(), userId, "policy-added");
    }

    public final void updateDeviceAdminServiceOnPolicyRemoveLocked(EnforcingAdmin enforcingAdmin) {
        if (doesAdminHavePolicies(enforcingAdmin)) {
            return;
        }
        int userId = enforcingAdmin.getUserId();
        if (this.mEnforcingAdmins.contains(userId)) {
            this.mEnforcingAdmins.get(userId).remove(enforcingAdmin);
            if (this.mEnforcingAdmins.get(userId).isEmpty()) {
                this.mEnforcingAdmins.remove(enforcingAdmin.getUserId());
            }
        }
        if (enforcingAdmin.hasAuthority("enterprise")) {
            return;
        }
        this.mDeviceAdminServiceController.stopServiceForAdmin(enforcingAdmin.getPackageName(), userId, "policy-removed");
    }

    public final boolean doesAdminHavePolicies(EnforcingAdmin enforcingAdmin) {
        for (PolicyKey policyKey : this.mGlobalPolicies.keySet()) {
            if (this.mGlobalPolicies.get(policyKey).getPoliciesSetByAdmins().containsKey(enforcingAdmin)) {
                return true;
            }
        }
        for (int i = 0; i < this.mLocalPolicies.size(); i++) {
            SparseArray<Map<PolicyKey, PolicyState<?>>> sparseArray = this.mLocalPolicies;
            for (PolicyKey policyKey2 : sparseArray.get(sparseArray.keyAt(i)).keySet()) {
                SparseArray<Map<PolicyKey, PolicyState<?>>> sparseArray2 = this.mLocalPolicies;
                if (sparseArray2.get(sparseArray2.keyAt(i)).get(policyKey2).getPoliciesSetByAdmins().containsKey(enforcingAdmin)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final Set<EnforcingAdmin> getEnforcingAdminsForUser(int i) {
        return this.mEnforcingAdmins.contains(i) ? this.mEnforcingAdmins.get(i) : Collections.emptySet();
    }

    public final void write() {
        Log.d("DevicePolicyEngine", "Writing device policies to file.");
        new DevicePoliciesReaderWriter().writeToFileLocked();
    }

    public void load() {
        Log.d("DevicePolicyEngine", "Reading device policies from file.");
        synchronized (this.mLock) {
            clear();
            new DevicePoliciesReaderWriter().readFromFileLocked();
        }
    }

    public void clearAllPolicies() {
        synchronized (this.mLock) {
            clear();
            write();
        }
    }

    public final void clear() {
        synchronized (this.mLock) {
            this.mGlobalPolicies.clear();
            this.mLocalPolicies.clear();
            this.mEnforcingAdmins.clear();
        }
    }

    public boolean hasActivePolicies() {
        return this.mEnforcingAdmins.size() > 0;
    }

    public boolean canAdminAddPolicies(final String str, int i) {
        if (isCoexistenceFlagEnabled()) {
            return true;
        }
        if (this.mEnforcingAdmins.contains(i) && this.mEnforcingAdmins.get(i).stream().anyMatch(new Predicate() { // from class: com.android.server.devicepolicy.DevicePolicyEngine$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$canAdminAddPolicies$3;
                lambda$canAdminAddPolicies$3 = DevicePolicyEngine.lambda$canAdminAddPolicies$3(str, (EnforcingAdmin) obj);
                return lambda$canAdminAddPolicies$3;
            }
        })) {
            return true;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.mEnforcingAdmins.size(); i3++) {
            i2 += this.mEnforcingAdmins.get(i3).size();
        }
        return i2 == 0 || i2 > 1;
    }

    public static /* synthetic */ boolean lambda$canAdminAddPolicies$3(String str, EnforcingAdmin enforcingAdmin) {
        return enforcingAdmin.getPackageName().equals(str);
    }

    public final boolean isCoexistenceFlagEnabled() {
        return DeviceConfig.getBoolean("device_policy_manager", "enable_coexistence", true);
    }

    /* loaded from: classes.dex */
    public class DevicePoliciesReaderWriter {
        public final File mFile;

        public DevicePoliciesReaderWriter() {
            this.mFile = new File(Environment.getDataSystemDirectory(), "device_policy_state.xml");
        }

        public void writeToFileLocked() {
            Log.d("DevicePolicyEngine", "Writing to " + this.mFile);
            AtomicFile atomicFile = new AtomicFile(this.mFile);
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream startWrite = atomicFile.startWrite();
                try {
                    TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                    resolveSerializer.startDocument((String) null, Boolean.TRUE);
                    writeInner(resolveSerializer);
                    resolveSerializer.endDocument();
                    resolveSerializer.flush();
                    atomicFile.finishWrite(startWrite);
                } catch (IOException e) {
                    e = e;
                    fileOutputStream = startWrite;
                    Log.e("DevicePolicyEngine", "Exception when writing", e);
                    if (fileOutputStream != null) {
                        atomicFile.failWrite(fileOutputStream);
                    }
                }
            } catch (IOException e2) {
                e = e2;
            }
        }

        public void writeInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            writeLocalPoliciesInner(typedXmlSerializer);
            writeGlobalPoliciesInner(typedXmlSerializer);
            writeEnforcingAdminsInner(typedXmlSerializer);
        }

        public final void writeLocalPoliciesInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            if (DevicePolicyEngine.this.mLocalPolicies != null) {
                for (int i = 0; i < DevicePolicyEngine.this.mLocalPolicies.size(); i++) {
                    int keyAt = DevicePolicyEngine.this.mLocalPolicies.keyAt(i);
                    for (Map.Entry entry : ((Map) DevicePolicyEngine.this.mLocalPolicies.get(keyAt)).entrySet()) {
                        typedXmlSerializer.startTag((String) null, "local-policy-entry");
                        typedXmlSerializer.attributeInt((String) null, "user-id", keyAt);
                        ((PolicyKey) entry.getKey()).saveToXml(typedXmlSerializer);
                        typedXmlSerializer.startTag((String) null, "admins-policy-entry");
                        ((PolicyState) entry.getValue()).saveToXml(typedXmlSerializer);
                        typedXmlSerializer.endTag((String) null, "admins-policy-entry");
                        typedXmlSerializer.endTag((String) null, "local-policy-entry");
                    }
                }
            }
        }

        public final void writeGlobalPoliciesInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            if (DevicePolicyEngine.this.mGlobalPolicies != null) {
                for (Map.Entry entry : DevicePolicyEngine.this.mGlobalPolicies.entrySet()) {
                    typedXmlSerializer.startTag((String) null, "global-policy-entry");
                    ((PolicyKey) entry.getKey()).saveToXml(typedXmlSerializer);
                    typedXmlSerializer.startTag((String) null, "admins-policy-entry");
                    ((PolicyState) entry.getValue()).saveToXml(typedXmlSerializer);
                    typedXmlSerializer.endTag((String) null, "admins-policy-entry");
                    typedXmlSerializer.endTag((String) null, "global-policy-entry");
                }
            }
        }

        public final void writeEnforcingAdminsInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            if (DevicePolicyEngine.this.mEnforcingAdmins != null) {
                for (int i = 0; i < DevicePolicyEngine.this.mEnforcingAdmins.size(); i++) {
                    for (EnforcingAdmin enforcingAdmin : (Set) DevicePolicyEngine.this.mEnforcingAdmins.get(DevicePolicyEngine.this.mEnforcingAdmins.keyAt(i))) {
                        typedXmlSerializer.startTag((String) null, "enforcing-admins-entry");
                        enforcingAdmin.saveToXml(typedXmlSerializer);
                        typedXmlSerializer.endTag((String) null, "enforcing-admins-entry");
                    }
                }
            }
        }

        public void readFromFileLocked() {
            if (!this.mFile.exists()) {
                Log.d("DevicePolicyEngine", "" + this.mFile + " doesn't exist");
                return;
            }
            Log.d("DevicePolicyEngine", "Reading from " + this.mFile);
            FileInputStream fileInputStream = null;
            try {
                try {
                    fileInputStream = new AtomicFile(this.mFile).openRead();
                    readInner(Xml.resolvePullParser(fileInputStream));
                } catch (IOException | ClassNotFoundException | XmlPullParserException e) {
                    Log.e("DevicePolicyEngine", "Error parsing resources file", e);
                }
            } finally {
                IoUtils.closeQuietly(fileInputStream);
            }
        }

        public final void readInner(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException, ClassNotFoundException {
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                String name = typedXmlPullParser.getName();
                name.hashCode();
                char c = 65535;
                switch (name.hashCode()) {
                    case -1900677631:
                        if (name.equals("global-policy-entry")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1329955015:
                        if (name.equals("local-policy-entry")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1016501079:
                        if (name.equals("enforcing-admins-entry")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        readGlobalPoliciesInner(typedXmlPullParser);
                        break;
                    case 1:
                        readLocalPoliciesInner(typedXmlPullParser);
                        break;
                    case 2:
                        readEnforcingAdminsInner(typedXmlPullParser);
                        break;
                    default:
                        Log.e("DevicePolicyEngine", "Unknown tag " + name);
                        break;
                }
            }
        }

        public final void readLocalPoliciesInner(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "user-id");
            PolicyKey readPolicyKeyFromXml = PolicyDefinition.readPolicyKeyFromXml(typedXmlPullParser);
            if (!DevicePolicyEngine.this.mLocalPolicies.contains(attributeInt)) {
                DevicePolicyEngine.this.mLocalPolicies.put(attributeInt, new HashMap());
            }
            PolicyState<?> parseAdminsPolicy = parseAdminsPolicy(typedXmlPullParser);
            if (parseAdminsPolicy != null) {
                ((Map) DevicePolicyEngine.this.mLocalPolicies.get(attributeInt)).put(readPolicyKeyFromXml, parseAdminsPolicy);
                return;
            }
            Log.e("DevicePolicyEngine", "Error parsing file, " + readPolicyKeyFromXml + "doesn't have an AdminsPolicy.");
        }

        public final void readGlobalPoliciesInner(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            PolicyKey readPolicyKeyFromXml = PolicyDefinition.readPolicyKeyFromXml(typedXmlPullParser);
            PolicyState<?> parseAdminsPolicy = parseAdminsPolicy(typedXmlPullParser);
            if (parseAdminsPolicy != null) {
                DevicePolicyEngine.this.mGlobalPolicies.put(readPolicyKeyFromXml, parseAdminsPolicy);
                return;
            }
            Log.e("DevicePolicyEngine", "Error parsing file, " + readPolicyKeyFromXml + "doesn't have an AdminsPolicy.");
        }

        public final void readEnforcingAdminsInner(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException {
            EnforcingAdmin readFromXml = EnforcingAdmin.readFromXml(typedXmlPullParser);
            if (!DevicePolicyEngine.this.mEnforcingAdmins.contains(readFromXml.getUserId())) {
                DevicePolicyEngine.this.mEnforcingAdmins.put(readFromXml.getUserId(), new HashSet());
            }
            ((Set) DevicePolicyEngine.this.mEnforcingAdmins.get(readFromXml.getUserId())).add(readFromXml);
        }

        public final PolicyState<?> parseAdminsPolicy(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                String name = typedXmlPullParser.getName();
                if (name.equals("admins-policy-entry")) {
                    return PolicyState.readFromXml(typedXmlPullParser);
                }
                Log.e("DevicePolicyEngine", "Unknown tag " + name);
            }
            Log.e("DevicePolicyEngine", "Error parsing file, AdminsPolicy not found");
            return null;
        }
    }
}
