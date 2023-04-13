package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.IOnChecksumsReadyListener;
import android.content.pm.PackageInfo;
import android.content.pm.ProcessInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.SuspendDialogInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.SharedUserApi;
import com.android.server.p011pm.pkg.mutate.PackageStateMutator;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.pm.PackageManagerInternalBase */
/* loaded from: classes2.dex */
public abstract class PackageManagerInternalBase extends PackageManagerInternal {
    public final PackageManagerService mService;

    public abstract ApexManager getApexManager();

    public abstract AppDataHelper getAppDataHelper();

    public abstract Context getContext();

    public abstract DistractingPackageHelper getDistractingPackageHelper();

    public abstract InstantAppRegistry getInstantAppRegistry();

    public abstract PackageObserverHelper getPackageObserverHelper();

    public abstract PermissionManagerServiceInternal getPermissionManager();

    public abstract ProtectedPackages getProtectedPackages();

    public abstract ResolveIntentHelper getResolveIntentHelper();

    public abstract SuspendPackageHelper getSuspendPackageHelper();

    public PackageManagerInternalBase(PackageManagerService packageManagerService) {
        this.mService = packageManagerService;
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public final Computer snapshot() {
        return this.mService.snapshotComputer();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<ApplicationInfo> getInstalledApplications(long j, int i, int i2) {
        return snapshot().getInstalledApplications(j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isInstantApp(String str, int i) {
        return snapshot().isInstantApp(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String getInstantAppPackageName(int i) {
        return snapshot().getInstantAppPackageName(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean filterAppAccess(AndroidPackage androidPackage, int i, int i2) {
        return snapshot().filterAppAccess(androidPackage, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean filterAppAccess(String str, int i, int i2, boolean z) {
        return snapshot().filterAppAccess(str, i, i2, z);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean filterAppAccess(int i, int i2) {
        return snapshot().filterAppAccess(i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int[] getVisibilityAllowList(String str, int i) {
        return snapshot().getVisibilityAllowList(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean canQueryPackage(int i, String str) {
        return snapshot().canQueryPackage(i, str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final AndroidPackage getPackage(String str) {
        return snapshot().getPackage(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final AndroidPackage getAndroidPackage(String str) {
        return snapshot().getPackage(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final AndroidPackage getPackage(int i) {
        return snapshot().getPackage(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<AndroidPackage> getPackagesForAppId(int i) {
        return snapshot().getPackagesForAppId(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final PackageStateInternal getPackageStateInternal(String str) {
        return snapshot().getPackageStateInternal(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ArrayMap<String, ? extends PackageStateInternal> getPackageStates() {
        return snapshot().getPackageStates();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void removePackageListObserver(PackageManagerInternal.PackageListObserver packageListObserver) {
        getPackageObserverHelper().removeObserver(packageListObserver);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final PackageStateInternal getDisabledSystemPackage(String str) {
        return snapshot().getDisabledSystemPackage(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String[] getKnownPackageNames(int i, int i2) {
        return this.mService.getKnownPackageNamesInternal(snapshot(), i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void setKeepUninstalledPackages(List<String> list) {
        this.mService.setKeepUninstalledPackagesInternal(snapshot(), list);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPermissionsReviewRequired(String str, int i) {
        return getPermissionManager().isPermissionsReviewRequired(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final PackageInfo getPackageInfo(String str, long j, int i, int i2) {
        return snapshot().getPackageInfoInternal(str, -1L, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final Bundle getSuspendedPackageLauncherExtras(String str, int i) {
        return getSuspendPackageHelper().getSuspendedPackageLauncherExtras(snapshot(), str, i, Binder.getCallingUid());
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPackageSuspended(String str, int i) {
        return getSuspendPackageHelper().isPackageSuspended(snapshot(), str, i, Binder.getCallingUid());
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void removeNonSystemPackageSuspensions(String str, int i) {
        getSuspendPackageHelper().removeSuspensionsBySuspendingPackage(snapshot(), new String[]{str}, new Predicate() { // from class: com.android.server.pm.PackageManagerInternalBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeNonSystemPackageSuspensions$0;
                lambda$removeNonSystemPackageSuspensions$0 = PackageManagerInternalBase.lambda$removeNonSystemPackageSuspensions$0((String) obj);
                return lambda$removeNonSystemPackageSuspensions$0;
            }
        }, i);
    }

    public static /* synthetic */ boolean lambda$removeNonSystemPackageSuspensions$0(String str) {
        return !PackageManagerShellCommandDataLoader.PACKAGE.equals(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void removeDistractingPackageRestrictions(String str, int i) {
        getDistractingPackageHelper().removeDistractingPackageRestrictions(snapshot(), new String[]{str}, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void removeAllDistractingPackageRestrictions(int i) {
        this.mService.removeAllDistractingPackageRestrictions(snapshot(), i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String getSuspendingPackage(String str, int i) {
        return getSuspendPackageHelper().getSuspendingPackage(snapshot(), str, i, Binder.getCallingUid());
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final SuspendDialogInfo getSuspendedDialogInfo(String str, String str2, int i) {
        return getSuspendPackageHelper().getSuspendedDialogInfo(snapshot(), str, str2, i, Binder.getCallingUid());
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getDistractingPackageRestrictions(String str, int i) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        if (packageStateInternal == null) {
            return 0;
        }
        return packageStateInternal.getUserStateOrDefault(i).getDistractionFlags();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getPackageUid(String str, long j, int i) {
        return snapshot().getPackageUidInternal(str, j, i, 1000);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ApplicationInfo getApplicationInfo(String str, long j, int i, int i2) {
        return snapshot().getApplicationInfoInternal(str, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ActivityInfo getActivityInfo(ComponentName componentName, long j, int i, int i2) {
        return snapshot().getActivityInfoInternal(componentName, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<ResolveInfo> queryIntentActivities(Intent intent, String str, long j, int i, int i2) {
        return snapshot().queryIntentActivitiesInternal(intent, str, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<ResolveInfo> queryIntentReceivers(Intent intent, String str, long j, int i, int i2, boolean z) {
        return getResolveIntentHelper().queryIntentReceiversInternal(snapshot(), intent, str, j, i2, i, z);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<ResolveInfo> queryIntentServices(Intent intent, long j, int i, int i2) {
        return snapshot().queryIntentServicesInternal(intent, intent.resolveTypeIfNeeded(getContext().getContentResolver()), j, i2, i, false);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ComponentName getHomeActivitiesAsUser(List<ResolveInfo> list, int i) {
        return snapshot().getHomeActivitiesAsUser(list, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ComponentName getDefaultHomeActivity(int i) {
        return snapshot().getDefaultHomeActivity(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ComponentName getSystemUiServiceComponent() {
        return ComponentName.unflattenFromString(getContext().getResources().getString(17040013));
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void setOwnerProtectedPackages(int i, List<String> list) {
        getProtectedPackages().setOwnerProtectedPackages(i, list);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPackageDataProtected(int i, String str) {
        return getProtectedPackages().isPackageDataProtected(i, str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPackageStateProtected(String str, int i) {
        return getProtectedPackages().isPackageStateProtected(i, str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPackageEphemeral(int i, String str) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        return packageStateInternal != null && packageStateInternal.getUserStateOrDefault(i).isInstantApp();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean wasPackageEverLaunched(String str, int i) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        if (packageStateInternal == null) {
            throw new IllegalArgumentException("Unknown package: " + str);
        }
        return !packageStateInternal.getUserStateOrDefault(i).isNotLaunched();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String getNameForUid(int i) {
        return snapshot().getNameForUid(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void requestInstantAppResolutionPhaseTwo(AuxiliaryResolveInfo auxiliaryResolveInfo, Intent intent, String str, String str2, String str3, boolean z, Bundle bundle, int i) {
        this.mService.requestInstantAppResolutionPhaseTwo(auxiliaryResolveInfo, intent, str, str2, str3, z, bundle, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void grantImplicitAccess(int i, Intent intent, int i2, int i3, boolean z) {
        grantImplicitAccess(i, intent, i2, i3, z, false);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void grantImplicitAccess(int i, Intent intent, int i2, int i3, boolean z, boolean z2) {
        this.mService.grantImplicitAccess(snapshot(), i, intent, i2, i3, z, z2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isInstantAppInstallerComponent(ComponentName componentName) {
        ActivityInfo activityInfo = this.mService.mInstantAppInstallerActivity;
        return activityInfo != null && activityInfo.getComponentName().equals(componentName);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void pruneInstantApps() {
        getInstantAppRegistry().pruneInstantApps(snapshot());
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String getSetupWizardPackageName() {
        return this.mService.mSetupWizardPackage;
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ResolveInfo resolveIntentExported(Intent intent, String str, long j, long j2, int i, boolean z, int i2, int i3) {
        return getResolveIntentHelper().resolveIntentInternal(snapshot(), intent, str, j, j2, i, z, i2, true, i3);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ResolveInfo resolveService(Intent intent, String str, long j, int i, int i2) {
        return getResolveIntentHelper().resolveServiceInternal(snapshot(), intent, str, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ProviderInfo resolveContentProvider(String str, long j, int i, int i2) {
        return snapshot().resolveContentProvider(str, j, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getUidTargetSdkVersion(int i) {
        return snapshot().getUidTargetSdkVersion(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getPackageTargetSdkVersion(String str) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        return (packageStateInternal == null || packageStateInternal.getPkg() == null) ? FrameworkStatsLog.WIFI_BYTES_TRANSFER : packageStateInternal.getPkg().getTargetSdkVersion();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean canAccessInstantApps(int i, int i2) {
        return snapshot().canViewInstantApps(i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean canAccessComponent(int i, ComponentName componentName, int i2) {
        return snapshot().canAccessComponent(i, componentName, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean hasInstantApplicationMetadata(String str, int i) {
        return getInstantAppRegistry().hasInstantApplicationMetadata(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final String[] getSharedUserPackagesForPackage(String str, int i) {
        return snapshot().getSharedUserPackagesForPackage(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ArrayMap<String, ProcessInfo> getProcessesForUid(int i) {
        return snapshot().getProcessesForUid(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int[] getPermissionGids(String str, int i) {
        return getPermissionManager().getPermissionGids(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void freeStorage(String str, long j, int i) throws IOException {
        this.mService.freeStorage(str, j, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void freeAllAppCacheAboveQuota(String str) throws IOException {
        this.mService.freeAllAppCacheAboveQuota(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void forEachPackageSetting(Consumer<PackageSetting> consumer) {
        this.mService.forEachPackageSetting(consumer);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void forEachPackageState(Consumer<PackageStateInternal> consumer) {
        this.mService.forEachPackageState(snapshot(), consumer);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void forEachPackage(Consumer<AndroidPackage> consumer) {
        this.mService.forEachPackage(snapshot(), consumer);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void forEachInstalledPackage(Consumer<AndroidPackage> consumer, int i) {
        this.mService.forEachInstalledPackage(snapshot(), consumer, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ArraySet<String> getEnabledComponents(String str, int i) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        if (packageStateInternal == null) {
            return new ArraySet<>();
        }
        return packageStateInternal.getUserStateOrDefault(i).getEnabledComponents();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final ArraySet<String> getDisabledComponents(String str, int i) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        if (packageStateInternal == null) {
            return new ArraySet<>();
        }
        return packageStateInternal.getUserStateOrDefault(i).getDisabledComponents();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getApplicationEnabledState(String str, int i) {
        PackageStateInternal packageStateInternal = getPackageStateInternal(str);
        if (packageStateInternal == null) {
            return 0;
        }
        return packageStateInternal.getUserStateOrDefault(i).getEnabledState();
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final int getComponentEnabledSetting(ComponentName componentName, int i, int i2) {
        return snapshot().getComponentEnabledSettingInternal(componentName, i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void setEnableRollbackCode(int i, int i2) {
        this.mService.setEnableRollbackCode(i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void finishPackageInstall(int i, boolean z) {
        this.mService.finishPackageInstall(i, z);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isApexPackage(String str) {
        return snapshot().isApexPackage(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final List<String> getApksInApex(String str) {
        return getApexManager().getApksInApex(str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isCallerInstallerOfRecord(AndroidPackage androidPackage, int i) {
        return snapshot().isCallerInstallerOfRecord(androidPackage, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isSystemPackage(String str) {
        return str.equals(this.mService.ensureSystemPackageName(snapshot(), str));
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void unsuspendForSuspendingPackage(String str, int i) {
        this.mService.unsuspendForSuspendingPackage(snapshot(), str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isSuspendingAnyPackages(String str, int i) {
        return snapshot().isSuspendingAnyPackages(str, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void requestChecksums(String str, boolean z, int i, int i2, List list, IOnChecksumsReadyListener iOnChecksumsReadyListener, int i3, Executor executor, Handler handler) {
        this.mService.requestChecksumsInternal(snapshot(), str, z, i, i2, list, iOnChecksumsReadyListener, i3, executor, handler);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final boolean isPackageFrozen(String str, int i, int i2) {
        return snapshot().getPackageStartability(this.mService.getSafeMode(), str, i, i2) == 3;
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final long deleteOatArtifactsOfPackage(String str) {
        return this.mService.deleteOatArtifactsOfPackage(snapshot(), str);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void reconcileAppsData(int i, int i2, boolean z) {
        getAppDataHelper().reconcileAppsData(i, i2, z);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public ArraySet<PackageStateInternal> getSharedUserPackages(int i) {
        return snapshot().getSharedUserPackages(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public SharedUserApi getSharedUserApi(int i) {
        return snapshot().getSharedUser(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public boolean isUidPrivileged(int i) {
        return snapshot().isUidPrivileged(i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public int checkUidSignaturesForAllUsers(int i, int i2) {
        return snapshot().checkUidSignaturesForAllUsers(i, i2);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    public void setPackageStoppedState(String str, boolean z, int i) {
        this.mService.setPackageStoppedState(snapshot(), str, z, i);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final PackageStateMutator.Result commitPackageStateMutation(PackageStateMutator.InitialState initialState, Consumer<PackageStateMutator> consumer) {
        return this.mService.commitPackageStateMutation(initialState, consumer);
    }

    @Override // android.content.p000pm.PackageManagerInternal
    @Deprecated
    public final void shutdown() {
        this.mService.shutdown();
    }
}
