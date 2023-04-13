package com.android.server.p011pm;

import android.content.pm.UserInfo;
import android.os.Binder;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
/* renamed from: com.android.server.pm.UserNeedsBadgingCache */
/* loaded from: classes2.dex */
public class UserNeedsBadgingCache {
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mUserCache = new SparseBooleanArray();
    public final UserManagerService mUserManager;

    public UserNeedsBadgingCache(UserManagerService userManagerService) {
        this.mUserManager = userManagerService;
    }

    public void delete(int i) {
        synchronized (this.mLock) {
            this.mUserCache.delete(i);
        }
    }

    public boolean get(int i) {
        synchronized (this.mLock) {
            int indexOfKey = this.mUserCache.indexOfKey(i);
            if (indexOfKey >= 0) {
                return this.mUserCache.valueAt(indexOfKey);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                UserInfo userInfo = this.mUserManager.getUserInfo(i);
                boolean z = userInfo != null && userInfo.isManagedProfile();
                synchronized (this.mLock) {
                    this.mUserCache.put(i, z);
                }
                return z;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
