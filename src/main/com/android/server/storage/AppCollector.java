package com.android.server.storage;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes2.dex */
public class AppCollector {
    public static String TAG = "AppCollector";
    public final BackgroundHandler mBackgroundHandler;
    public CompletableFuture<List<PackageStats>> mStats;

    public AppCollector(Context context, VolumeInfo volumeInfo) {
        Objects.requireNonNull(volumeInfo);
        this.mBackgroundHandler = new BackgroundHandler(BackgroundThread.get().getLooper(), volumeInfo, context.getPackageManager(), (UserManager) context.getSystemService("user"), (StorageStatsManager) context.getSystemService("storagestats"));
    }

    public List<PackageStats> getPackageStats(long j) {
        synchronized (this) {
            if (this.mStats == null) {
                this.mStats = new CompletableFuture<>();
                this.mBackgroundHandler.sendEmptyMessage(0);
            }
        }
        try {
            return this.mStats.get(j, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "An exception occurred while getting app storage", e);
            return null;
        } catch (TimeoutException unused) {
            Log.e(TAG, "AppCollector timed out");
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public class BackgroundHandler extends Handler {
        public final PackageManager mPm;
        public final StorageStatsManager mStorageStatsManager;
        public final UserManager mUm;
        public final VolumeInfo mVolume;

        public BackgroundHandler(Looper looper, VolumeInfo volumeInfo, PackageManager packageManager, UserManager userManager, StorageStatsManager storageStatsManager) {
            super(looper);
            this.mVolume = volumeInfo;
            this.mPm = packageManager;
            this.mUm = userManager;
            this.mStorageStatsManager = storageStatsManager;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 0) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            List users = this.mUm.getUsers();
            int size = users.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = (UserInfo) users.get(i);
                List installedApplicationsAsUser = this.mPm.getInstalledApplicationsAsUser(512, userInfo.id);
                int size2 = installedApplicationsAsUser.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    ApplicationInfo applicationInfo = (ApplicationInfo) installedApplicationsAsUser.get(i2);
                    if (Objects.equals(applicationInfo.volumeUuid, this.mVolume.getFsUuid())) {
                        try {
                            StorageStats queryStatsForPackage = this.mStorageStatsManager.queryStatsForPackage(applicationInfo.storageUuid, applicationInfo.packageName, userInfo.getUserHandle());
                            PackageStats packageStats = new PackageStats(applicationInfo.packageName, userInfo.id);
                            packageStats.cacheSize = queryStatsForPackage.getCacheBytes();
                            packageStats.codeSize = queryStatsForPackage.getAppBytes();
                            packageStats.dataSize = queryStatsForPackage.getDataBytes();
                            arrayList.add(packageStats);
                        } catch (PackageManager.NameNotFoundException | IOException e) {
                            Log.e(AppCollector.TAG, "An exception occurred while fetching app size", e);
                        }
                    }
                }
            }
            AppCollector.this.mStats.complete(arrayList);
        }
    }
}
