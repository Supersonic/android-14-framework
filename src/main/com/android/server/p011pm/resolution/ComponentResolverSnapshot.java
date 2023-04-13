package com.android.server.p011pm.resolution;

import android.util.ArrayMap;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.UserNeedsBadgingCache;
import com.android.server.p011pm.resolution.ComponentResolver;
/* renamed from: com.android.server.pm.resolution.ComponentResolverSnapshot */
/* loaded from: classes2.dex */
public class ComponentResolverSnapshot extends ComponentResolverBase {
    public ComponentResolverSnapshot(ComponentResolver componentResolver, UserNeedsBadgingCache userNeedsBadgingCache) {
        super(UserManagerService.getInstance());
        this.mActivities = new ComponentResolver.ActivityIntentResolver(componentResolver.mActivities, this.mUserManager, userNeedsBadgingCache);
        this.mProviders = new ComponentResolver.ProviderIntentResolver(componentResolver.mProviders, this.mUserManager);
        this.mReceivers = new ComponentResolver.ReceiverIntentResolver(componentResolver.mReceivers, this.mUserManager, userNeedsBadgingCache);
        this.mServices = new ComponentResolver.ServiceIntentResolver(componentResolver.mServices, this.mUserManager);
        this.mProvidersByAuthority = new ArrayMap<>(componentResolver.mProvidersByAuthority);
    }
}
