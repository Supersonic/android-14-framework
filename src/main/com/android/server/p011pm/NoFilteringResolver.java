package com.android.server.p011pm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import com.android.internal.config.appcloning.AppCloningDeviceConfigHelper;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.resolution.ComponentResolverApi;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
/* renamed from: com.android.server.pm.NoFilteringResolver */
/* loaded from: classes2.dex */
public class NoFilteringResolver extends CrossProfileResolver {
    @Override // com.android.server.p011pm.CrossProfileResolver
    public List<CrossProfileDomainInfo> filterResolveInfoWithDomainPreferredActivity(Intent intent, List<CrossProfileDomainInfo> list, long j, int i, int i2, int i3) {
        return list;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x001c, code lost:
        if (hasPermission(r2, "android.permission.QUERY_CLONED_APPS") != false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean isIntentRedirectionAllowed(Context context, AppCloningDeviceConfigHelper appCloningDeviceConfigHelper, boolean z, long j) {
        boolean z2;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (appCloningDeviceConfigHelper.getEnableAppCloningBuildingBlocks()) {
                if (!z) {
                    if ((536870912 & j) != 0) {
                    }
                }
                z2 = true;
                return z2;
            }
            z2 = false;
            return z2;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public NoFilteringResolver(ComponentResolverApi componentResolverApi, UserManagerService userManagerService) {
        super(componentResolverApi, userManagerService);
    }

    @Override // com.android.server.p011pm.CrossProfileResolver
    public List<CrossProfileDomainInfo> resolveIntent(Computer computer, Intent intent, String str, int i, int i2, long j, String str2, List<CrossProfileIntentFilter> list, boolean z, Function<String, PackageStateInternal> function) {
        List<ResolveInfo> queryActivities = this.mComponentResolver.queryActivities(computer, intent, str, j, i2);
        ArrayList arrayList = new ArrayList();
        if (queryActivities != null) {
            for (int i3 = 0; i3 < queryActivities.size(); i3++) {
                arrayList.add(new CrossProfileDomainInfo(queryActivities.get(i3), 0, i2));
            }
        }
        return filterIfNotSystemUser(arrayList, i);
    }

    public static boolean hasPermission(Context context, String str) {
        return context.checkCallingOrSelfPermission(str) == 0;
    }
}
