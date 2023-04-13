package com.android.server.compat;

import android.annotation.EnforcePermission;
import android.annotation.RequiresNoPermission;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.compat.PackageOverride;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.compat.AndroidBuildClassifier;
import com.android.internal.compat.ChangeReporter;
import com.android.internal.compat.CompatibilityChangeConfig;
import com.android.internal.compat.CompatibilityChangeInfo;
import com.android.internal.compat.CompatibilityOverrideConfig;
import com.android.internal.compat.CompatibilityOverridesByPackageConfig;
import com.android.internal.compat.CompatibilityOverridesToRemoveByPackageConfig;
import com.android.internal.compat.CompatibilityOverridesToRemoveConfig;
import com.android.internal.compat.IOverrideValidator;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.compat.CompatChange;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class PlatformCompat extends IPlatformCompat.Stub {
    public final AndroidBuildClassifier mBuildClassifier;
    public final ChangeReporter mChangeReporter = new ChangeReporter(2);
    public final CompatConfig mCompatConfig;
    public final Context mContext;

    public PlatformCompat(Context context) {
        this.mContext = context;
        AndroidBuildClassifier androidBuildClassifier = new AndroidBuildClassifier();
        this.mBuildClassifier = androidBuildClassifier;
        this.mCompatConfig = CompatConfig.create(androidBuildClassifier, context);
    }

    @VisibleForTesting
    public PlatformCompat(Context context, CompatConfig compatConfig, AndroidBuildClassifier androidBuildClassifier) {
        this.mContext = context;
        this.mCompatConfig = compatConfig;
        this.mBuildClassifier = androidBuildClassifier;
        registerPackageReceiver(context);
    }

    @EnforcePermission("android.permission.LOG_COMPAT_CHANGE")
    public void reportChange(long j, ApplicationInfo applicationInfo) {
        super.reportChange_enforcePermission();
        reportChangeInternal(j, applicationInfo.uid, 3);
    }

    @EnforcePermission("android.permission.LOG_COMPAT_CHANGE")
    public void reportChangeByPackageName(long j, String str, int i) {
        super.reportChangeByPackageName_enforcePermission();
        ApplicationInfo applicationInfo = getApplicationInfo(str, i);
        if (applicationInfo != null) {
            reportChangeInternal(j, applicationInfo.uid, 3);
        }
    }

    @EnforcePermission("android.permission.LOG_COMPAT_CHANGE")
    public void reportChangeByUid(long j, int i) {
        super.reportChangeByUid_enforcePermission();
        reportChangeInternal(j, i, 3);
    }

    public final void reportChangeInternal(long j, int i, int i2) {
        this.mChangeReporter.reportChange(i, j, i2);
    }

    @EnforcePermission(allOf = {"android.permission.LOG_COMPAT_CHANGE", "android.permission.READ_COMPAT_CHANGE_CONFIG"})
    public boolean isChangeEnabled(long j, ApplicationInfo applicationInfo) {
        super.isChangeEnabled_enforcePermission();
        return isChangeEnabledInternal(j, applicationInfo);
    }

    @EnforcePermission(allOf = {"android.permission.LOG_COMPAT_CHANGE", "android.permission.READ_COMPAT_CHANGE_CONFIG"})
    public boolean isChangeEnabledByPackageName(long j, String str, int i) {
        super.isChangeEnabledByPackageName_enforcePermission();
        ApplicationInfo applicationInfo = getApplicationInfo(str, i);
        if (applicationInfo == null) {
            return this.mCompatConfig.willChangeBeEnabled(j, str);
        }
        return isChangeEnabledInternal(j, applicationInfo);
    }

    @EnforcePermission(allOf = {"android.permission.LOG_COMPAT_CHANGE", "android.permission.READ_COMPAT_CHANGE_CONFIG"})
    public boolean isChangeEnabledByUid(long j, int i) {
        super.isChangeEnabledByUid_enforcePermission();
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return this.mCompatConfig.defaultChangeIdValue(j);
        }
        boolean z = true;
        for (String str : packagesForUid) {
            z &= isChangeEnabledByPackageName(j, str, UserHandle.getUserId(i));
        }
        return z;
    }

    public boolean isChangeEnabledInternalNoLogging(long j, ApplicationInfo applicationInfo) {
        return this.mCompatConfig.isChangeEnabled(j, applicationInfo);
    }

    public boolean isChangeEnabledInternal(long j, ApplicationInfo applicationInfo) {
        boolean isChangeEnabledInternalNoLogging = isChangeEnabledInternalNoLogging(j, applicationInfo);
        if (applicationInfo != null) {
            reportChangeInternal(j, applicationInfo.uid, isChangeEnabledInternalNoLogging ? 1 : 2);
        }
        return isChangeEnabledInternalNoLogging;
    }

    public boolean isChangeEnabledInternal(long j, String str, int i) {
        if (this.mCompatConfig.willChangeBeEnabled(j, str)) {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = str;
            applicationInfo.targetSdkVersion = i;
            return isChangeEnabledInternalNoLogging(j, applicationInfo);
        }
        return false;
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public void setOverrides(CompatibilityChangeConfig compatibilityChangeConfig, String str) {
        super.setOverrides_enforcePermission();
        HashMap hashMap = new HashMap();
        for (Long l : compatibilityChangeConfig.enabledChanges()) {
            hashMap.put(Long.valueOf(l.longValue()), new PackageOverride.Builder().setEnabled(true).build());
        }
        for (Long l2 : compatibilityChangeConfig.disabledChanges()) {
            hashMap.put(Long.valueOf(l2.longValue()), new PackageOverride.Builder().setEnabled(false).build());
        }
        this.mCompatConfig.addPackageOverrides(new CompatibilityOverrideConfig(hashMap), str, false);
        killPackage(str);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public void setOverridesForTest(CompatibilityChangeConfig compatibilityChangeConfig, String str) {
        super.setOverridesForTest_enforcePermission();
        HashMap hashMap = new HashMap();
        for (Long l : compatibilityChangeConfig.enabledChanges()) {
            hashMap.put(Long.valueOf(l.longValue()), new PackageOverride.Builder().setEnabled(true).build());
        }
        for (Long l2 : compatibilityChangeConfig.disabledChanges()) {
            hashMap.put(Long.valueOf(l2.longValue()), new PackageOverride.Builder().setEnabled(false).build());
        }
        this.mCompatConfig.addPackageOverrides(new CompatibilityOverrideConfig(hashMap), str, false);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG_ON_RELEASE_BUILD")
    public void putAllOverridesOnReleaseBuilds(CompatibilityOverridesByPackageConfig compatibilityOverridesByPackageConfig) {
        super.putAllOverridesOnReleaseBuilds_enforcePermission();
        for (CompatibilityOverrideConfig compatibilityOverrideConfig : compatibilityOverridesByPackageConfig.packageNameToOverrides.values()) {
            checkAllCompatOverridesAreOverridable(compatibilityOverrideConfig.overrides.keySet());
        }
        this.mCompatConfig.addAllPackageOverrides(compatibilityOverridesByPackageConfig, true);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG_ON_RELEASE_BUILD")
    public void putOverridesOnReleaseBuilds(CompatibilityOverrideConfig compatibilityOverrideConfig, String str) {
        super.putOverridesOnReleaseBuilds_enforcePermission();
        checkAllCompatOverridesAreOverridable(compatibilityOverrideConfig.overrides.keySet());
        this.mCompatConfig.addPackageOverrides(compatibilityOverrideConfig, str, true);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public int enableTargetSdkChanges(String str, int i) {
        super.enableTargetSdkChanges_enforcePermission();
        int enableTargetSdkChangesForPackage = this.mCompatConfig.enableTargetSdkChangesForPackage(str, i);
        killPackage(str);
        return enableTargetSdkChangesForPackage;
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public int disableTargetSdkChanges(String str, int i) {
        super.disableTargetSdkChanges_enforcePermission();
        int disableTargetSdkChangesForPackage = this.mCompatConfig.disableTargetSdkChangesForPackage(str, i);
        killPackage(str);
        return disableTargetSdkChangesForPackage;
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public void clearOverrides(String str) {
        super.clearOverrides_enforcePermission();
        this.mCompatConfig.removePackageOverrides(str);
        killPackage(str);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public void clearOverridesForTest(String str) {
        super.clearOverridesForTest_enforcePermission();
        this.mCompatConfig.removePackageOverrides(str);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public boolean clearOverride(long j, String str) {
        super.clearOverride_enforcePermission();
        boolean removeOverride = this.mCompatConfig.removeOverride(j, str);
        killPackage(str);
        return removeOverride;
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG")
    public boolean clearOverrideForTest(long j, String str) {
        super.clearOverrideForTest_enforcePermission();
        return this.mCompatConfig.removeOverride(j, str);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG_ON_RELEASE_BUILD")
    public void removeAllOverridesOnReleaseBuilds(CompatibilityOverridesToRemoveByPackageConfig compatibilityOverridesToRemoveByPackageConfig) {
        super.removeAllOverridesOnReleaseBuilds_enforcePermission();
        for (CompatibilityOverridesToRemoveConfig compatibilityOverridesToRemoveConfig : compatibilityOverridesToRemoveByPackageConfig.packageNameToOverridesToRemove.values()) {
            checkAllCompatOverridesAreOverridable(compatibilityOverridesToRemoveConfig.changeIds);
        }
        this.mCompatConfig.removeAllPackageOverrides(compatibilityOverridesToRemoveByPackageConfig);
    }

    @EnforcePermission("android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG_ON_RELEASE_BUILD")
    public void removeOverridesOnReleaseBuilds(CompatibilityOverridesToRemoveConfig compatibilityOverridesToRemoveConfig, String str) {
        super.removeOverridesOnReleaseBuilds_enforcePermission();
        checkAllCompatOverridesAreOverridable(compatibilityOverridesToRemoveConfig.changeIds);
        this.mCompatConfig.removePackageOverrides(compatibilityOverridesToRemoveConfig, str);
    }

    @EnforcePermission(allOf = {"android.permission.LOG_COMPAT_CHANGE", "android.permission.READ_COMPAT_CHANGE_CONFIG"})
    public CompatibilityChangeConfig getAppConfig(ApplicationInfo applicationInfo) {
        super.getAppConfig_enforcePermission();
        return this.mCompatConfig.getAppConfig(applicationInfo);
    }

    @EnforcePermission("android.permission.READ_COMPAT_CHANGE_CONFIG")
    public CompatibilityChangeInfo[] listAllChanges() {
        super.listAllChanges_enforcePermission();
        return this.mCompatConfig.dumpChanges();
    }

    @RequiresNoPermission
    public CompatibilityChangeInfo[] listUIChanges() {
        return (CompatibilityChangeInfo[]) Arrays.stream(listAllChanges()).filter(new Predicate() { // from class: com.android.server.compat.PlatformCompat$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isShownInUI;
                isShownInUI = PlatformCompat.this.isShownInUI((CompatibilityChangeInfo) obj);
                return isShownInUI;
            }
        }).toArray(new IntFunction() { // from class: com.android.server.compat.PlatformCompat$$ExternalSyntheticLambda1
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                CompatibilityChangeInfo[] lambda$listUIChanges$0;
                lambda$listUIChanges$0 = PlatformCompat.lambda$listUIChanges$0(i);
                return lambda$listUIChanges$0;
            }
        });
    }

    public static /* synthetic */ CompatibilityChangeInfo[] lambda$listUIChanges$0(int i) {
        return new CompatibilityChangeInfo[i];
    }

    public boolean isKnownChangeId(long j) {
        return this.mCompatConfig.isKnownChangeId(j);
    }

    public long[] getDisabledChanges(ApplicationInfo applicationInfo) {
        return this.mCompatConfig.getDisabledChanges(applicationInfo);
    }

    public long lookupChangeId(String str) {
        return this.mCompatConfig.lookupChangeId(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "platform_compat", printWriter)) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_COMPAT_CHANGE_CONFIG", "Cannot read compat change");
            this.mContext.enforceCallingOrSelfPermission("android.permission.LOG_COMPAT_CHANGE", "Cannot read log compat change usage");
            this.mCompatConfig.dumpConfig(printWriter);
        }
    }

    @RequiresNoPermission
    public IOverrideValidator getOverrideValidator() {
        return this.mCompatConfig.getOverrideValidator();
    }

    public void resetReporting(ApplicationInfo applicationInfo) {
        this.mChangeReporter.resetReportedChanges(applicationInfo.uid);
    }

    public final ApplicationInfo getApplicationInfo(String str, int i) {
        return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getApplicationInfo(str, 0L, Process.myUid(), i);
    }

    public final void killPackage(String str) {
        int packageUid = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageUid(str, 0L, UserHandle.myUserId());
        if (packageUid < 0) {
            Slog.w("Compatibility", "Didn't find package " + str + " on device.");
            return;
        }
        Slog.d("Compatibility", "Killing package " + str + " (UID " + packageUid + ").");
        killUid(UserHandle.getAppId(packageUid));
    }

    public final void killUid(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IActivityManager service = ActivityManager.getService();
            if (service != null) {
                service.killUid(i, -1, "PlatformCompat overrides");
            }
        } catch (RemoteException unused) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public final void checkAllCompatOverridesAreOverridable(Collection<Long> collection) {
        for (Long l : collection) {
            if (isKnownChangeId(l.longValue()) && !this.mCompatConfig.isOverridable(l.longValue())) {
                throw new SecurityException("Only change ids marked as Overridable can be overridden.");
            }
        }
    }

    public final boolean isShownInUI(CompatibilityChangeInfo compatibilityChangeInfo) {
        if (compatibilityChangeInfo.getLoggingOnly() || compatibilityChangeInfo.getId() == 149391281) {
            return false;
        }
        if (compatibilityChangeInfo.getEnableSinceTargetSdk() > 0) {
            return compatibilityChangeInfo.getEnableSinceTargetSdk() >= 29 && compatibilityChangeInfo.getEnableSinceTargetSdk() <= this.mBuildClassifier.platformTargetSdk();
        }
        return true;
    }

    public boolean registerListener(long j, CompatChange.ChangeListener changeListener) {
        return this.mCompatConfig.registerListener(j, changeListener);
    }

    public void registerPackageReceiver(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.compat.PlatformCompat.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Uri data;
                String schemeSpecificPart;
                if (intent == null || (data = intent.getData()) == null || (schemeSpecificPart = data.getSchemeSpecificPart()) == null) {
                    return;
                }
                PlatformCompat.this.mCompatConfig.recheckOverrides(schemeSpecificPart);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        context.registerReceiverForAllUsers(broadcastReceiver, intentFilter, null, null);
    }

    public void registerContentObserver() {
        this.mCompatConfig.registerContentObserver();
    }
}
