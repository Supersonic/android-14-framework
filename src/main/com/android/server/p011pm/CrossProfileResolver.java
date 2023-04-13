package com.android.server.p011pm;

import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Binder;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.resolution.ComponentResolverApi;
import java.util.List;
import java.util.function.Function;
/* renamed from: com.android.server.pm.CrossProfileResolver */
/* loaded from: classes2.dex */
public abstract class CrossProfileResolver {
    public ComponentResolverApi mComponentResolver;
    public UserManagerService mUserManager;

    public abstract List<CrossProfileDomainInfo> filterResolveInfoWithDomainPreferredActivity(Intent intent, List<CrossProfileDomainInfo> list, long j, int i, int i2, int i3);

    public abstract List<CrossProfileDomainInfo> resolveIntent(Computer computer, Intent intent, String str, int i, int i2, long j, String str2, List<CrossProfileIntentFilter> list, boolean z, Function<String, PackageStateInternal> function);

    public CrossProfileResolver(ComponentResolverApi componentResolverApi, UserManagerService userManagerService) {
        this.mComponentResolver = componentResolverApi;
        this.mUserManager = userManagerService;
    }

    public final boolean isUserEnabled(int i) {
        boolean z;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UserInfo userInfo = this.mUserManager.getUserInfo(i);
            if (userInfo != null) {
                if (userInfo.isEnabled()) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final List<CrossProfileDomainInfo> filterIfNotSystemUser(List<CrossProfileDomainInfo> list, int i) {
        if (i == 0) {
            return list;
        }
        for (int size = CollectionUtils.size(list) - 1; size >= 0; size--) {
            if ((list.get(size).mResolveInfo.activityInfo.flags & 536870912) != 0) {
                list.remove(size);
            }
        }
        return list;
    }

    public final UserInfo getProfileParent(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mUserManager.getProfileParent(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
