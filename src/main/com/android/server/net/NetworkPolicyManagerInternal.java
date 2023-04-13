package com.android.server.net;

import android.net.Network;
import com.android.server.net.NetworkPolicyManagerService;
import java.util.Set;
/* loaded from: classes2.dex */
public abstract class NetworkPolicyManagerInternal {
    public abstract long getSubscriptionOpportunisticQuota(Network network, int i);

    public abstract void onAdminDataAvailable();

    public abstract void onTempPowerSaveWhitelistChange(int i, boolean z, int i2, String str);

    public abstract void resetUserState(int i);

    public abstract void setAppIdleWhitelist(int i, boolean z);

    public abstract void setLowPowerStandbyActive(boolean z);

    public abstract void setLowPowerStandbyAllowlist(int[] iArr);

    public abstract void setMeteredRestrictedPackages(Set<String> set, int i);

    public abstract void setMeteredRestrictedPackagesAsync(Set<String> set, int i);

    public static int updateBlockedReasonsWithProcState(int i, int i2) {
        return NetworkPolicyManagerService.UidBlockedState.getEffectiveBlockedReasons(i, NetworkPolicyManagerService.UidBlockedState.getAllowedReasonsForProcState(i2));
    }
}
