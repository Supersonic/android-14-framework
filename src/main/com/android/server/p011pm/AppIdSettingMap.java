package com.android.server.p011pm;

import com.android.internal.util.FrameworkStatsLog;
import com.android.server.utils.SnapshotCache;
import com.android.server.utils.WatchedArrayList;
import com.android.server.utils.WatchedSparseArray;
import com.android.server.utils.Watcher;
/* renamed from: com.android.server.pm.AppIdSettingMap */
/* loaded from: classes2.dex */
public final class AppIdSettingMap {
    public int mFirstAvailableAppId;
    public final WatchedArrayList<SettingBase> mNonSystemSettings;
    public final SnapshotCache<WatchedArrayList<SettingBase>> mNonSystemSettingsSnapshot;
    public final WatchedSparseArray<SettingBase> mSystemSettings;
    public final SnapshotCache<WatchedSparseArray<SettingBase>> mSystemSettingsSnapshot;

    public AppIdSettingMap() {
        this.mFirstAvailableAppId = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        WatchedArrayList<SettingBase> watchedArrayList = new WatchedArrayList<>();
        this.mNonSystemSettings = watchedArrayList;
        this.mNonSystemSettingsSnapshot = new SnapshotCache.Auto(watchedArrayList, watchedArrayList, "AppIdSettingMap.mNonSystemSettings");
        WatchedSparseArray<SettingBase> watchedSparseArray = new WatchedSparseArray<>();
        this.mSystemSettings = watchedSparseArray;
        this.mSystemSettingsSnapshot = new SnapshotCache.Auto(watchedSparseArray, watchedSparseArray, "AppIdSettingMap.mSystemSettings");
    }

    public AppIdSettingMap(AppIdSettingMap appIdSettingMap) {
        this.mFirstAvailableAppId = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        this.mNonSystemSettings = appIdSettingMap.mNonSystemSettingsSnapshot.snapshot();
        this.mNonSystemSettingsSnapshot = new SnapshotCache.Sealed();
        this.mSystemSettings = appIdSettingMap.mSystemSettingsSnapshot.snapshot();
        this.mSystemSettingsSnapshot = new SnapshotCache.Sealed();
    }

    public boolean registerExistingAppId(int i, SettingBase settingBase, Object obj) {
        if (i >= 10000) {
            int i2 = i - 10000;
            for (int size = this.mNonSystemSettings.size(); i2 >= size; size++) {
                this.mNonSystemSettings.add(null);
            }
            if (this.mNonSystemSettings.get(i2) != null) {
                PackageManagerService.reportSettingsProblem(5, "Adding duplicate app id: " + i + " name=" + obj);
                return false;
            }
            this.mNonSystemSettings.set(i2, settingBase);
            return true;
        } else if (this.mSystemSettings.get(i) != null) {
            PackageManagerService.reportSettingsProblem(5, "Adding duplicate shared id: " + i + " name=" + obj);
            return false;
        } else {
            this.mSystemSettings.put(i, settingBase);
            return true;
        }
    }

    public SettingBase getSetting(int i) {
        if (i >= 10000) {
            int size = this.mNonSystemSettings.size();
            int i2 = i - FrameworkStatsLog.WIFI_BYTES_TRANSFER;
            if (i2 < size) {
                return this.mNonSystemSettings.get(i2);
            }
            return null;
        }
        return this.mSystemSettings.get(i);
    }

    public void removeSetting(int i) {
        if (i >= 10000) {
            int i2 = i - 10000;
            if (i2 < this.mNonSystemSettings.size()) {
                this.mNonSystemSettings.set(i2, null);
            }
        } else {
            this.mSystemSettings.remove(i);
        }
        setFirstAvailableAppId(i + 1);
    }

    public final void setFirstAvailableAppId(int i) {
        if (i > this.mFirstAvailableAppId) {
            this.mFirstAvailableAppId = i;
        }
    }

    public void replaceSetting(int i, SettingBase settingBase) {
        if (i >= 10000) {
            int i2 = i - 10000;
            if (i2 < this.mNonSystemSettings.size()) {
                this.mNonSystemSettings.set(i2, settingBase);
                return;
            }
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: calling replaceAppIdLpw to replace SettingBase at appId=" + i + " but nothing is replaced.");
            return;
        }
        this.mSystemSettings.put(i, settingBase);
    }

    public int acquireAndRegisterNewAppId(SettingBase settingBase) {
        int size = this.mNonSystemSettings.size();
        for (int i = this.mFirstAvailableAppId - 10000; i < size; i++) {
            if (this.mNonSystemSettings.get(i) == null) {
                this.mNonSystemSettings.set(i, settingBase);
                return i + FrameworkStatsLog.WIFI_BYTES_TRANSFER;
            }
        }
        if (size > 9999) {
            return -1;
        }
        this.mNonSystemSettings.add(settingBase);
        return size + FrameworkStatsLog.WIFI_BYTES_TRANSFER;
    }

    public AppIdSettingMap snapshot() {
        return new AppIdSettingMap(this);
    }

    public void registerObserver(Watcher watcher) {
        this.mNonSystemSettings.registerObserver(watcher);
        this.mSystemSettings.registerObserver(watcher);
    }
}
