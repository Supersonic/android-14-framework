package com.android.server.p011pm;

import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstallSourceInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.KeySet;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.pm.dex.IArtManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.permission.PermissionManager;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.p011pm.verify.domain.proxy.DomainVerificationProxyV1;
import java.util.List;
import java.util.Objects;
/* renamed from: com.android.server.pm.IPackageManagerBase */
/* loaded from: classes2.dex */
public abstract class IPackageManagerBase extends IPackageManager.Stub {
    public final Context mContext;
    public final DexOptHelper mDexOptHelper;
    public final DomainVerificationConnection mDomainVerificationConnection;
    public final DomainVerificationManagerInternal mDomainVerificationManager;
    public final PackageInstallerService mInstallerService;
    public final ComponentName mInstantAppResolverSettingsComponent;
    public final ModuleInfoProvider mModuleInfoProvider;
    public final PackageProperty mPackageProperty;
    public final PreferredActivityHelper mPreferredActivityHelper;
    public final String mRequiredSupplementalProcessPackage;
    public final ComponentName mResolveComponentName;
    public final ResolveIntentHelper mResolveIntentHelper;
    public final PackageManagerService mService;
    public final String mServicesExtensionPackageName;
    public final String mSharedSystemSharedLibraryPackageName;

    @Deprecated
    public final boolean hasSystemUidErrors() {
        return false;
    }

    public IPackageManagerBase(PackageManagerService packageManagerService, Context context, DexOptHelper dexOptHelper, ModuleInfoProvider moduleInfoProvider, PreferredActivityHelper preferredActivityHelper, ResolveIntentHelper resolveIntentHelper, DomainVerificationManagerInternal domainVerificationManagerInternal, DomainVerificationConnection domainVerificationConnection, PackageInstallerService packageInstallerService, PackageProperty packageProperty, ComponentName componentName, ComponentName componentName2, String str, String str2, String str3) {
        this.mService = packageManagerService;
        this.mContext = context;
        this.mDexOptHelper = dexOptHelper;
        this.mModuleInfoProvider = moduleInfoProvider;
        this.mPreferredActivityHelper = preferredActivityHelper;
        this.mResolveIntentHelper = resolveIntentHelper;
        this.mDomainVerificationManager = domainVerificationManagerInternal;
        this.mDomainVerificationConnection = domainVerificationConnection;
        this.mInstallerService = packageInstallerService;
        this.mPackageProperty = packageProperty;
        this.mResolveComponentName = componentName;
        this.mInstantAppResolverSettingsComponent = componentName2;
        this.mRequiredSupplementalProcessPackage = str;
        this.mServicesExtensionPackageName = str2;
        this.mSharedSystemSharedLibraryPackageName = str3;
    }

    public Computer snapshot() {
        return this.mService.snapshotComputer();
    }

    @Deprecated
    public final boolean activitySupportsIntentAsUser(ComponentName componentName, Intent intent, String str, int i) {
        return snapshot().activitySupportsIntentAsUser(this.mResolveComponentName, componentName, intent, str, i);
    }

    @Deprecated
    public final void addCrossProfileIntentFilter(IntentFilter intentFilter, String str, int i, int i2, int i3) {
        this.mService.addCrossProfileIntentFilter(snapshot(), new WatchedIntentFilter(intentFilter), str, i, i2, i3);
    }

    @Deprecated
    public final boolean addPermission(PermissionInfo permissionInfo) {
        return ((PermissionManager) this.mContext.getSystemService(PermissionManager.class)).addPermission(permissionInfo, false);
    }

    @Deprecated
    public final boolean addPermissionAsync(PermissionInfo permissionInfo) {
        return ((PermissionManager) this.mContext.getSystemService(PermissionManager.class)).addPermission(permissionInfo, true);
    }

    @Deprecated
    public final void addPersistentPreferredActivity(IntentFilter intentFilter, ComponentName componentName, int i) {
        this.mPreferredActivityHelper.addPersistentPreferredActivity(new WatchedIntentFilter(intentFilter), componentName, i);
    }

    @Deprecated
    public final void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2, boolean z) {
        this.mPreferredActivityHelper.addPreferredActivity(snapshot(), new WatchedIntentFilter(intentFilter), i, componentNameArr, componentName, true, i2, "Adding preferred", z);
    }

    @Deprecated
    public final boolean canForwardTo(Intent intent, String str, int i, int i2) {
        return snapshot().canForwardTo(intent, str, i, i2);
    }

    @Deprecated
    public final boolean canRequestPackageInstalls(String str, int i) {
        return snapshot().canRequestPackageInstalls(str, Binder.getCallingUid(), i, true);
    }

    @Deprecated
    public final String[] canonicalToCurrentPackageNames(String[] strArr) {
        return snapshot().canonicalToCurrentPackageNames(strArr);
    }

    @Deprecated
    public final int checkPermission(String str, String str2, int i) {
        return this.mService.checkPermission(str, str2, i);
    }

    @Deprecated
    public final int checkSignatures(String str, String str2, int i) {
        return snapshot().checkSignatures(str, str2, i);
    }

    @Deprecated
    public final int checkUidPermission(String str, int i) {
        return snapshot().checkUidPermission(str, i);
    }

    @Deprecated
    public final int checkUidSignatures(int i, int i2) {
        return snapshot().checkUidSignatures(i, i2);
    }

    @Deprecated
    public final void clearPackagePersistentPreferredActivities(String str, int i) {
        this.mPreferredActivityHelper.clearPackagePersistentPreferredActivities(str, i);
    }

    @Deprecated
    public final void clearPersistentPreferredActivity(IntentFilter intentFilter, int i) {
        this.mPreferredActivityHelper.clearPersistentPreferredActivity(intentFilter, i);
    }

    @Deprecated
    public final void clearPackagePreferredActivities(String str) {
        this.mPreferredActivityHelper.clearPackagePreferredActivities(snapshot(), str);
    }

    @Deprecated
    public final String[] currentToCanonicalPackageNames(String[] strArr) {
        return snapshot().currentToCanonicalPackageNames(strArr);
    }

    @Deprecated
    public final void deleteExistingPackageAsUser(VersionedPackage versionedPackage, IPackageDeleteObserver2 iPackageDeleteObserver2, int i) {
        this.mService.deleteExistingPackageAsUser(versionedPackage, iPackageDeleteObserver2, i);
    }

    @Deprecated
    public final void deletePackageAsUser(String str, int i, IPackageDeleteObserver iPackageDeleteObserver, int i2, int i3) {
        deletePackageVersioned(new VersionedPackage(str, i), new PackageManager.LegacyPackageDeleteObserver(iPackageDeleteObserver).getBinder(), i2, i3);
    }

    @Deprecated
    public final void deletePackageVersioned(VersionedPackage versionedPackage, IPackageDeleteObserver2 iPackageDeleteObserver2, int i, int i2) {
        this.mService.deletePackageVersioned(versionedPackage, iPackageDeleteObserver2, i, i2);
    }

    @Deprecated
    public final ResolveInfo findPersistentPreferredActivity(Intent intent, int i) {
        return this.mPreferredActivityHelper.findPersistentPreferredActivity(snapshot(), intent, i);
    }

    @Deprecated
    public final ActivityInfo getActivityInfo(ComponentName componentName, long j, int i) {
        return snapshot().getActivityInfo(componentName, j, i);
    }

    @Deprecated
    public final ParceledListSlice<IntentFilter> getAllIntentFilters(String str) {
        return snapshot().getAllIntentFilters(str);
    }

    @Deprecated
    public final List<String> getAllPackages() {
        return snapshot().getAllPackages();
    }

    @Deprecated
    public final String[] getAppOpPermissionPackages(String str, int i) {
        return snapshot().getAppOpPermissionPackages(str, i);
    }

    @Deprecated
    public final String getAppPredictionServicePackageName() {
        return this.mService.mAppPredictionServicePackage;
    }

    @Deprecated
    public final int getApplicationEnabledSetting(String str, int i) {
        return snapshot().getApplicationEnabledSetting(str, i);
    }

    @Deprecated
    public final boolean getApplicationHiddenSettingAsUser(String str, int i) {
        return snapshot().getApplicationHiddenSettingAsUser(str, i);
    }

    @Deprecated
    public final ApplicationInfo getApplicationInfo(String str, long j, int i) {
        return snapshot().getApplicationInfo(str, j, i);
    }

    @Deprecated
    public final IArtManager getArtManager() {
        return this.mService.mArtManagerService;
    }

    @Deprecated
    public final String getAttentionServicePackageName() {
        return this.mService.ensureSystemPackageName(snapshot(), this.mService.getPackageFromComponentString(17039873));
    }

    @Deprecated
    public final boolean getBlockUninstallForUser(String str, int i) {
        return snapshot().getBlockUninstallForUser(str, i);
    }

    @Deprecated
    public final int getComponentEnabledSetting(ComponentName componentName, int i) {
        return snapshot().getComponentEnabledSetting(componentName, Binder.getCallingUid(), i);
    }

    @Deprecated
    public final ParceledListSlice<SharedLibraryInfo> getDeclaredSharedLibraries(String str, long j, int i) {
        return snapshot().getDeclaredSharedLibraries(str, j, i);
    }

    @Deprecated
    public final byte[] getDefaultAppsBackup(int i) {
        return this.mPreferredActivityHelper.getDefaultAppsBackup(i);
    }

    @Deprecated
    public final String getDefaultTextClassifierPackageName() {
        return this.mService.mDefaultTextClassifierPackage;
    }

    @Deprecated
    public final int getFlagsForUid(int i) {
        return snapshot().getFlagsForUid(i);
    }

    @Deprecated
    public final CharSequence getHarmfulAppWarning(String str, int i) {
        return snapshot().getHarmfulAppWarning(str, i);
    }

    @Deprecated
    public final ComponentName getHomeActivities(List<ResolveInfo> list) {
        Computer snapshot = snapshot();
        if (snapshot.getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return null;
        }
        return snapshot.getHomeActivitiesAsUser(list, UserHandle.getCallingUserId());
    }

    @Deprecated
    public final String getIncidentReportApproverPackageName() {
        return this.mService.mIncidentReportApproverPackage;
    }

    @Deprecated
    public final int getInstallLocation() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "default_install_location", 0);
    }

    @Deprecated
    public final int getInstallReason(String str, int i) {
        return snapshot().getInstallReason(str, i);
    }

    @Deprecated
    public final InstallSourceInfo getInstallSourceInfo(String str) {
        return snapshot().getInstallSourceInfo(str);
    }

    @Deprecated
    public final ParceledListSlice<ApplicationInfo> getInstalledApplications(long j, int i) {
        return new ParceledListSlice<>(snapshot().getInstalledApplications(j, i, Binder.getCallingUid()));
    }

    @Deprecated
    public final List<ModuleInfo> getInstalledModules(int i) {
        return this.mModuleInfoProvider.getInstalledModules(i);
    }

    @Deprecated
    public final ParceledListSlice<PackageInfo> getInstalledPackages(long j, int i) {
        return snapshot().getInstalledPackages(j, i);
    }

    @Deprecated
    public final String getInstallerPackageName(String str) {
        return snapshot().getInstallerPackageName(str, UserHandle.getCallingUserId());
    }

    @Deprecated
    public final ComponentName getInstantAppInstallerComponent() {
        Computer snapshot = snapshot();
        if (snapshot.getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return null;
        }
        return snapshot.getInstantAppInstallerComponent();
    }

    @Deprecated
    public final ComponentName getInstantAppResolverComponent() {
        Computer snapshot = snapshot();
        if (snapshot.getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return null;
        }
        return this.mService.getInstantAppResolver(snapshot);
    }

    @Deprecated
    public final ComponentName getInstantAppResolverSettingsComponent() {
        return this.mInstantAppResolverSettingsComponent;
    }

    @Deprecated
    public final InstrumentationInfo getInstrumentationInfoAsUser(ComponentName componentName, int i, int i2) {
        return snapshot().getInstrumentationInfoAsUser(componentName, i, i2);
    }

    @Deprecated
    public final ParceledListSlice<IntentFilterVerificationInfo> getIntentFilterVerifications(String str) {
        return ParceledListSlice.emptyList();
    }

    @Deprecated
    public final int getIntentVerificationStatus(String str, int i) {
        return this.mDomainVerificationManager.getLegacyState(str, i);
    }

    @Deprecated
    public final KeySet getKeySetByAlias(String str, String str2) {
        return snapshot().getKeySetByAlias(str, str2);
    }

    @Deprecated
    public final ModuleInfo getModuleInfo(String str, int i) {
        return this.mModuleInfoProvider.getModuleInfo(str, i);
    }

    @Deprecated
    public final String getNameForUid(int i) {
        return snapshot().getNameForUid(i);
    }

    @Deprecated
    public final String[] getNamesForUids(int[] iArr) {
        return snapshot().getNamesForUids(iArr);
    }

    @Deprecated
    public final int[] getPackageGids(String str, long j, int i) {
        return snapshot().getPackageGids(str, j, i);
    }

    @Deprecated
    public final PackageInfo getPackageInfo(String str, long j, int i) {
        return snapshot().getPackageInfo(str, j, i);
    }

    @Deprecated
    public final PackageInfo getPackageInfoVersioned(VersionedPackage versionedPackage, long j, int i) {
        return snapshot().getPackageInfoInternal(versionedPackage.getPackageName(), versionedPackage.getLongVersionCode(), j, Binder.getCallingUid(), i);
    }

    @Deprecated
    public final IPackageInstaller getPackageInstaller() {
        if (PackageManagerServiceUtils.isSystemOrRoot()) {
            return this.mInstallerService;
        }
        if (snapshot().getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return null;
        }
        return this.mInstallerService;
    }

    @Deprecated
    public final void getPackageSizeInfo(String str, int i, IPackageStatsObserver iPackageStatsObserver) {
        throw new UnsupportedOperationException("Shame on you for calling the hidden API getPackageSizeInfo(). Shame!");
    }

    @Deprecated
    public final int getPackageUid(String str, long j, int i) {
        return snapshot().getPackageUid(str, j, i);
    }

    @Deprecated
    public final String[] getPackagesForUid(int i) {
        snapshot().enforceCrossUserOrProfilePermission(Binder.getCallingUid(), UserHandle.getUserId(i), false, false, "getPackagesForUid");
        return snapshot().getPackagesForUid(i);
    }

    @Deprecated
    public final ParceledListSlice<PackageInfo> getPackagesHoldingPermissions(String[] strArr, long j, int i) {
        return snapshot().getPackagesHoldingPermissions(strArr, j, i);
    }

    @Deprecated
    public final PermissionGroupInfo getPermissionGroupInfo(String str, int i) {
        return this.mService.getPermissionGroupInfo(str, i);
    }

    @Deprecated
    public final ParceledListSlice<ApplicationInfo> getPersistentApplications(int i) {
        Computer snapshot = snapshot();
        if (snapshot.getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return ParceledListSlice.emptyList();
        }
        return new ParceledListSlice<>(snapshot.getPersistentApplications(isSafeMode(), i));
    }

    @Deprecated
    public final int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str) {
        return this.mPreferredActivityHelper.getPreferredActivities(snapshot(), list, list2, str);
    }

    @Deprecated
    public final byte[] getPreferredActivityBackup(int i) {
        return this.mPreferredActivityHelper.getPreferredActivityBackup(i);
    }

    @Deprecated
    public final int getPrivateFlagsForUid(int i) {
        return snapshot().getPrivateFlagsForUid(i);
    }

    @Deprecated
    public final PackageManager.Property getPropertyAsUser(String str, String str2, String str3, int i) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(str2);
        int callingUid = Binder.getCallingUid();
        Computer snapshot = snapshot();
        snapshot.enforceCrossUserOrProfilePermission(callingUid, i, false, false, "getPropertyAsUser");
        if (snapshot.getPackageStateForInstalledAndFiltered(str2, callingUid, i) == null) {
            return null;
        }
        return this.mPackageProperty.getProperty(str, str2, str3);
    }

    @Deprecated
    public final ProviderInfo getProviderInfo(ComponentName componentName, long j, int i) {
        return snapshot().getProviderInfo(componentName, j, i);
    }

    @Deprecated
    public final ActivityInfo getReceiverInfo(ComponentName componentName, long j, int i) {
        return snapshot().getReceiverInfo(componentName, j, i);
    }

    @Deprecated
    public final String getRotationResolverPackageName() {
        return this.mService.ensureSystemPackageName(snapshot(), this.mService.getPackageFromComponentString(17039898));
    }

    @Deprecated
    public final ServiceInfo getServiceInfo(ComponentName componentName, long j, int i) {
        return snapshot().getServiceInfo(componentName, j, i);
    }

    @Deprecated
    public final String getServicesSystemSharedLibraryPackageName() {
        return this.mServicesExtensionPackageName;
    }

    @Deprecated
    public final String getSetupWizardPackageName() {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Non-system caller");
        }
        return this.mService.mSetupWizardPackage;
    }

    @Deprecated
    public final ParceledListSlice<SharedLibraryInfo> getSharedLibraries(String str, long j, int i) {
        return snapshot().getSharedLibraries(str, j, i);
    }

    @Deprecated
    public final String getSharedSystemSharedLibraryPackageName() {
        return this.mSharedSystemSharedLibraryPackageName;
    }

    @Deprecated
    public final KeySet getSigningKeySet(String str) {
        return snapshot().getSigningKeySet(str);
    }

    @Deprecated
    public final String getSdkSandboxPackageName() {
        return this.mService.getSdkSandboxPackageName();
    }

    @Deprecated
    public final String getSystemCaptionsServicePackageName() {
        return this.mService.ensureSystemPackageName(snapshot(), this.mService.getPackageFromComponentString(17039903));
    }

    @Deprecated
    public final String[] getSystemSharedLibraryNames() {
        return snapshot().getSystemSharedLibraryNames();
    }

    @Deprecated
    public final String getSystemTextClassifierPackageName() {
        return this.mService.mSystemTextClassifierPackageName;
    }

    @Deprecated
    public final int getTargetSdkVersion(String str) {
        return snapshot().getTargetSdkVersion(str);
    }

    @Deprecated
    public final int getUidForSharedUser(String str) {
        return snapshot().getUidForSharedUser(str);
    }

    @SuppressLint({"MissingPermission"})
    @Deprecated
    public final String getWellbeingPackageName() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return (String) CollectionUtils.firstOrNull(((RoleManager) this.mContext.getSystemService(RoleManager.class)).getRoleHolders("android.app.role.SYSTEM_WELLBEING"));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @SuppressLint({"MissingPermission"})
    @Deprecated
    public final void grantRuntimePermission(String str, String str2, int i) {
        ((PermissionManager) this.mContext.getSystemService(PermissionManager.class)).grantRuntimePermission(str, str2, UserHandle.of(i));
    }

    @Deprecated
    public final boolean hasSigningCertificate(String str, byte[] bArr, int i) {
        return snapshot().hasSigningCertificate(str, bArr, i);
    }

    @Deprecated
    public final boolean hasSystemFeature(String str, int i) {
        return this.mService.hasSystemFeature(str, i);
    }

    @Deprecated
    public final boolean hasUidSigningCertificate(int i, byte[] bArr, int i2) {
        return snapshot().hasUidSigningCertificate(i, bArr, i2);
    }

    @Deprecated
    public final boolean isDeviceUpgrading() {
        return this.mService.isDeviceUpgrading();
    }

    @Deprecated
    public final boolean isFirstBoot() {
        return this.mService.isFirstBoot();
    }

    @Deprecated
    public final boolean isInstantApp(String str, int i) {
        return snapshot().isInstantApp(str, i);
    }

    @Deprecated
    public final boolean isPackageAvailable(String str, int i) {
        return snapshot().isPackageAvailable(str, i);
    }

    @Deprecated
    public final boolean isPackageDeviceAdminOnAnyUser(String str) {
        return this.mService.isPackageDeviceAdminOnAnyUser(snapshot(), str);
    }

    @Deprecated
    public final boolean isPackageSignedByKeySet(String str, KeySet keySet) {
        return snapshot().isPackageSignedByKeySet(str, keySet);
    }

    @Deprecated
    public final boolean isPackageSignedByKeySetExactly(String str, KeySet keySet) {
        return snapshot().isPackageSignedByKeySetExactly(str, keySet);
    }

    @Deprecated
    public final boolean isPackageSuspendedForUser(String str, int i) {
        return snapshot().isPackageSuspendedForUser(str, i);
    }

    @Deprecated
    public final boolean isSafeMode() {
        return this.mService.getSafeMode();
    }

    @Deprecated
    public final boolean isStorageLow() {
        return this.mService.isStorageLow();
    }

    @Deprecated
    public final boolean isUidPrivileged(int i) {
        return snapshot().isUidPrivileged(i);
    }

    @Deprecated
    public final boolean performDexOptMode(String str, boolean z, String str2, boolean z2, boolean z3, String str3) {
        Computer snapshot = snapshot();
        if (!z) {
            Log.w("PackageManager", "Ignored checkProfiles=false flag");
        }
        return this.mDexOptHelper.performDexOptMode(snapshot, str, str2, z2, z3, str3);
    }

    @Deprecated
    public final boolean performDexOptSecondary(String str, String str2, boolean z) {
        return this.mDexOptHelper.performDexOptSecondary(str, str2, z);
    }

    @Deprecated
    public final ParceledListSlice<ResolveInfo> queryIntentActivities(Intent intent, String str, long j, int i) {
        try {
            Trace.traceBegin(262144L, "queryIntentActivities");
            return new ParceledListSlice<>(snapshot().queryIntentActivitiesInternal(intent, str, j, i));
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    @Deprecated
    public final ParceledListSlice<ProviderInfo> queryContentProviders(String str, int i, long j, String str2) {
        return snapshot().queryContentProviders(str, i, j, str2);
    }

    @Deprecated
    public final ParceledListSlice<InstrumentationInfo> queryInstrumentationAsUser(String str, int i, int i2) {
        return snapshot().queryInstrumentationAsUser(str, i, i2);
    }

    @Deprecated
    public final ParceledListSlice<ResolveInfo> queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, String[] strArr, Intent intent, String str, long j, int i) {
        return new ParceledListSlice<>(this.mResolveIntentHelper.queryIntentActivityOptionsInternal(snapshot(), componentName, intentArr, strArr, intent, str, j, i));
    }

    @Deprecated
    public final ParceledListSlice<ResolveInfo> queryIntentContentProviders(Intent intent, String str, long j, int i) {
        return new ParceledListSlice<>(this.mResolveIntentHelper.queryIntentContentProvidersInternal(snapshot(), intent, str, j, i));
    }

    @Deprecated
    public final ParceledListSlice<ResolveInfo> queryIntentReceivers(Intent intent, String str, long j, int i) {
        return new ParceledListSlice<>(this.mResolveIntentHelper.queryIntentReceiversInternal(snapshot(), intent, str, j, i, Binder.getCallingUid()));
    }

    @Deprecated
    public final ParceledListSlice<ResolveInfo> queryIntentServices(Intent intent, String str, long j, int i) {
        return new ParceledListSlice<>(snapshot().queryIntentServicesInternal(intent, str, j, i, Binder.getCallingUid(), false));
    }

    @Deprecated
    public final void querySyncProviders(List<String> list, List<ProviderInfo> list2) {
        snapshot().querySyncProviders(isSafeMode(), list, list2);
    }

    @Deprecated
    public final void removePermission(String str) {
        ((PermissionManager) this.mContext.getSystemService(PermissionManager.class)).removePermission(str);
    }

    @Deprecated
    public final void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) {
        this.mPreferredActivityHelper.replacePreferredActivity(snapshot(), new WatchedIntentFilter(intentFilter), i, componentNameArr, componentName, i2);
    }

    @Deprecated
    public final ProviderInfo resolveContentProvider(String str, long j, int i) {
        return snapshot().resolveContentProvider(str, j, i, Binder.getCallingUid());
    }

    @Deprecated
    public final void resetApplicationPreferences(int i) {
        this.mPreferredActivityHelper.resetApplicationPreferences(i);
    }

    @Deprecated
    public final ResolveInfo resolveIntent(Intent intent, String str, long j, int i) {
        return this.mResolveIntentHelper.resolveIntentInternal(snapshot(), intent, str, j, 0L, i, false, Binder.getCallingUid());
    }

    @Deprecated
    public final ResolveInfo resolveService(Intent intent, String str, long j, int i) {
        return this.mResolveIntentHelper.resolveServiceInternal(snapshot(), intent, str, j, i, Binder.getCallingUid());
    }

    @Deprecated
    public final void restoreDefaultApps(byte[] bArr, int i) {
        this.mPreferredActivityHelper.restoreDefaultApps(bArr, i);
    }

    @Deprecated
    public final void restorePreferredActivities(byte[] bArr, int i) {
        this.mPreferredActivityHelper.restorePreferredActivities(bArr, i);
    }

    @Deprecated
    public final void setHomeActivity(ComponentName componentName, int i) {
        this.mPreferredActivityHelper.setHomeActivity(snapshot(), componentName, i);
    }

    @Deprecated
    public final void setLastChosenActivity(Intent intent, String str, int i, IntentFilter intentFilter, int i2, ComponentName componentName) {
        this.mPreferredActivityHelper.setLastChosenActivity(snapshot(), intent, str, i, new WatchedIntentFilter(intentFilter), i2, componentName);
    }

    @Deprecated
    public final boolean updateIntentVerificationStatus(String str, int i, int i2) {
        return this.mDomainVerificationManager.setLegacyUserState(str, i2, i);
    }

    @Deprecated
    public final void verifyIntentFilter(int i, int i2, List<String> list) {
        DomainVerificationProxyV1.queueLegacyVerifyResult(this.mContext, this.mDomainVerificationConnection, i, i2, list, Binder.getCallingUid());
    }

    @Deprecated
    public final boolean[] canPackageQuery(String str, String[] strArr, int i) {
        return snapshot().canPackageQuery(str, strArr, i);
    }

    @Deprecated
    public final void deletePreloadsFileCache() throws RemoteException {
        this.mService.deletePreloadsFileCache();
    }

    @Deprecated
    public final void setSystemAppHiddenUntilInstalled(String str, boolean z) throws RemoteException {
        this.mService.setSystemAppHiddenUntilInstalled(snapshot(), str, z);
    }

    @Deprecated
    public final boolean setSystemAppInstallState(String str, boolean z, int i) throws RemoteException {
        return this.mService.setSystemAppInstallState(snapshot(), str, z, i);
    }

    @Deprecated
    public final void finishPackageInstall(int i, boolean z) throws RemoteException {
        this.mService.finishPackageInstall(i, z);
    }
}
