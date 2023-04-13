package com.android.server.content;

import android.app.ActivityManagerInternal;
import android.app.usage.UsageStatsManagerInternal;
import android.content.pm.UserPackage;
import android.os.SystemClock;
import com.android.server.LocalServices;
import java.util.HashMap;
/* loaded from: classes.dex */
public class SyncAdapterStateFetcher {
    public final HashMap<UserPackage, Integer> mBucketCache = new HashMap<>();

    public int getStandbyBucket(int i, String str) {
        UserPackage of = UserPackage.of(i, str);
        Integer num = this.mBucketCache.get(of);
        if (num != null) {
            return num.intValue();
        }
        UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (usageStatsManagerInternal == null) {
            return -1;
        }
        int appStandbyBucket = usageStatsManagerInternal.getAppStandbyBucket(str, i, SystemClock.elapsedRealtime());
        this.mBucketCache.put(of, Integer.valueOf(appStandbyBucket));
        return appStandbyBucket;
    }

    public boolean isAppActive(int i) {
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        if (activityManagerInternal != null) {
            return activityManagerInternal.isUidActive(i);
        }
        return false;
    }
}
