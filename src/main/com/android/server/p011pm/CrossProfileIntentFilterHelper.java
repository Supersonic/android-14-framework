package com.android.server.p011pm;

import android.content.Context;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.util.ArraySet;
import java.util.Iterator;
/* renamed from: com.android.server.pm.CrossProfileIntentFilterHelper */
/* loaded from: classes2.dex */
public class CrossProfileIntentFilterHelper {
    public Context mContext;
    public PackageManagerTracedLock mLock;
    public Settings mSettings;
    public UserManagerInternal mUserManagerInternal;
    public UserManagerService mUserManagerService;

    public CrossProfileIntentFilterHelper(Settings settings, UserManagerService userManagerService, PackageManagerTracedLock packageManagerTracedLock, UserManagerInternal userManagerInternal, Context context) {
        this.mSettings = settings;
        this.mUserManagerService = userManagerService;
        this.mLock = packageManagerTracedLock;
        this.mContext = context;
        this.mUserManagerInternal = userManagerInternal;
    }

    public void updateDefaultCrossProfileIntentFilter() {
        int profileParentId;
        int i;
        for (UserInfo userInfo : this.mUserManagerInternal.getUsers(false)) {
            UserProperties userProperties = this.mUserManagerInternal.getUserProperties(userInfo.id);
            if (userProperties != null && userProperties.getUpdateCrossProfileIntentFiltersOnOTA() && (profileParentId = this.mUserManagerInternal.getProfileParentId(userInfo.id)) != (i = userInfo.id)) {
                clearCrossProfileIntentFilters(i, this.mContext.getOpPackageName(), Integer.valueOf(profileParentId));
                clearCrossProfileIntentFilters(profileParentId, this.mContext.getOpPackageName(), Integer.valueOf(userInfo.id));
                this.mUserManagerInternal.setDefaultCrossProfileIntentFilters(profileParentId, userInfo.id);
            }
        }
    }

    public void clearCrossProfileIntentFilters(int i, String str, Integer num) {
        synchronized (this.mLock) {
            CrossProfileIntentResolver editCrossProfileIntentResolverLPw = this.mSettings.editCrossProfileIntentResolverLPw(i);
            Iterator it = new ArraySet(editCrossProfileIntentResolverLPw.filterSet()).iterator();
            while (it.hasNext()) {
                CrossProfileIntentFilter crossProfileIntentFilter = (CrossProfileIntentFilter) it.next();
                if (crossProfileIntentFilter.getOwnerPackage().equals(str) && (num == null || crossProfileIntentFilter.mTargetUserId == num.intValue())) {
                    if (this.mUserManagerService.isCrossProfileIntentFilterAccessible(i, crossProfileIntentFilter.mTargetUserId, false)) {
                        editCrossProfileIntentResolverLPw.removeFilter((CrossProfileIntentResolver) crossProfileIntentFilter);
                    }
                }
            }
        }
    }
}
