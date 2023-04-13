package com.android.server.usage;

import android.annotation.SystemApi;
import android.content.pm.PackageStats;
import android.os.UserHandle;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes2.dex */
public interface StorageStatsManagerLocal {

    /* loaded from: classes2.dex */
    public interface StorageStatsAugmenter {
        void augmentStatsForPackageForUser(PackageStats packageStats, String str, UserHandle userHandle, boolean z);

        void augmentStatsForUid(PackageStats packageStats, int i, boolean z);

        void augmentStatsForUser(PackageStats packageStats, UserHandle userHandle);
    }

    void registerStorageStatsAugmenter(StorageStatsAugmenter storageStatsAugmenter, String str);
}
