package com.android.server.p014wm;

import android.content.res.Configuration;
import android.os.Binder;
import android.os.LocaleList;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.util.Optional;
/* renamed from: com.android.server.wm.PackageConfigurationUpdaterImpl */
/* loaded from: classes2.dex */
public final class PackageConfigurationUpdaterImpl implements ActivityTaskManagerInternal.PackageConfigurationUpdater {
    public ActivityTaskManagerService mAtm;
    @Configuration.GrammaticalGender
    public int mGrammaticalGender;
    public LocaleList mLocales;
    public Integer mNightMode;
    public String mPackageName;
    public final Optional<Integer> mPid;
    public int mUserId;

    public PackageConfigurationUpdaterImpl(int i, ActivityTaskManagerService activityTaskManagerService) {
        this.mPid = Optional.of(Integer.valueOf(i));
        this.mAtm = activityTaskManagerService;
    }

    public PackageConfigurationUpdaterImpl(String str, int i, ActivityTaskManagerService activityTaskManagerService) {
        this.mPackageName = str;
        this.mUserId = i;
        this.mAtm = activityTaskManagerService;
        this.mPid = Optional.empty();
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.PackageConfigurationUpdater
    public ActivityTaskManagerInternal.PackageConfigurationUpdater setNightMode(int i) {
        synchronized (this) {
            this.mNightMode = Integer.valueOf(i);
        }
        return this;
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.PackageConfigurationUpdater
    public ActivityTaskManagerInternal.PackageConfigurationUpdater setLocales(LocaleList localeList) {
        synchronized (this) {
            this.mLocales = localeList;
        }
        return this;
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.PackageConfigurationUpdater
    public ActivityTaskManagerInternal.PackageConfigurationUpdater setGrammaticalGender(@Configuration.GrammaticalGender int i) {
        synchronized (this) {
            this.mGrammaticalGender = i;
        }
        return this;
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.PackageConfigurationUpdater
    public boolean commit() {
        int i;
        synchronized (this) {
            synchronized (this.mAtm.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (this.mPid.isPresent()) {
                        WindowProcessController process = this.mAtm.mProcessMap.getProcess(this.mPid.get().intValue());
                        if (process == null) {
                            Slog.w("PackageConfigurationUpdaterImpl", "commit: Override application configuration failed: cannot find pid " + this.mPid);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        i = process.mUid;
                        this.mUserId = process.mUserId;
                        this.mPackageName = process.mInfo.packageName;
                    } else {
                        int packageUid = this.mAtm.getPackageManagerInternalLocked().getPackageUid(this.mPackageName, 131072L, this.mUserId);
                        if (packageUid < 0) {
                            Slog.w("PackageConfigurationUpdaterImpl", "commit: update of application configuration failed: userId or packageName not valid " + this.mUserId);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        i = packageUid;
                    }
                    updateConfig(i, this.mPackageName);
                    boolean updateFromImpl = this.mAtm.mPackageConfigPersister.updateFromImpl(this.mPackageName, this.mUserId, this);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return updateFromImpl;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final void updateConfig(int i, String str) {
        ArraySet<WindowProcessController> processes = this.mAtm.mProcessMap.getProcesses(i);
        if (processes == null || processes.isEmpty()) {
            return;
        }
        LocaleList combineLocalesIfOverlayExists = LocaleOverlayHelper.combineLocalesIfOverlayExists(this.mLocales, this.mAtm.getGlobalConfiguration().getLocales());
        for (int size = processes.size() - 1; size >= 0; size--) {
            WindowProcessController valueAt = processes.valueAt(size);
            if (valueAt.mInfo.packageName.equals(str)) {
                valueAt.applyAppSpecificConfig(this.mNightMode, combineLocalesIfOverlayExists, Integer.valueOf(this.mGrammaticalGender));
            }
            valueAt.updateAppSpecificSettingsForAllActivitiesInPackage(str, this.mNightMode, combineLocalesIfOverlayExists, this.mGrammaticalGender);
        }
    }

    public Integer getNightMode() {
        return this.mNightMode;
    }

    public LocaleList getLocales() {
        return this.mLocales;
    }

    @Configuration.GrammaticalGender
    public Integer getGrammaticalGender() {
        return Integer.valueOf(this.mGrammaticalGender);
    }
}
