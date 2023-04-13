package android.content.p001pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p001pm.IDexModuleRegisterCallback;
import android.content.p001pm.IOnChecksumsReadyListener;
import android.content.p001pm.IPackageDataObserver;
import android.content.p001pm.IPackageDeleteObserver;
import android.content.p001pm.IPackageDeleteObserver2;
import android.content.p001pm.IPackageInstaller;
import android.content.p001pm.IPackageManager;
import android.content.p001pm.IPackageMoveObserver;
import android.content.p001pm.IPackageStatsObserver;
import android.content.p001pm.PackageManager;
import android.content.p001pm.dex.IArtManager;
import android.graphics.Bitmap;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* renamed from: android.content.pm.IPackageManager */
/* loaded from: classes.dex */
public interface IPackageManager extends IInterface {
    boolean activitySupportsIntentAsUser(ComponentName componentName, Intent intent, String str, int i) throws RemoteException;

    void addCrossProfileIntentFilter(IntentFilter intentFilter, String str, int i, int i2, int i3) throws RemoteException;

    boolean addPermission(PermissionInfo permissionInfo) throws RemoteException;

    boolean addPermissionAsync(PermissionInfo permissionInfo) throws RemoteException;

    void addPersistentPreferredActivity(IntentFilter intentFilter, ComponentName componentName, int i) throws RemoteException;

    void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2, boolean z) throws RemoteException;

    boolean canForwardTo(Intent intent, String str, int i, int i2) throws RemoteException;

    boolean[] canPackageQuery(String str, String[] strArr, int i) throws RemoteException;

    boolean canRequestPackageInstalls(String str, int i) throws RemoteException;

    String[] canonicalToCurrentPackageNames(String[] strArr) throws RemoteException;

    void checkPackageStartable(String str, int i) throws RemoteException;

    int checkPermission(String str, String str2, int i) throws RemoteException;

    int checkSignatures(String str, String str2, int i) throws RemoteException;

    int checkUidPermission(String str, int i) throws RemoteException;

    int checkUidSignatures(int i, int i2) throws RemoteException;

    void clearApplicationProfileData(String str) throws RemoteException;

    void clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException;

    void clearCrossProfileIntentFilters(int i, String str) throws RemoteException;

    void clearPackagePersistentPreferredActivities(String str, int i) throws RemoteException;

    void clearPackagePreferredActivities(String str) throws RemoteException;

    void clearPersistentPreferredActivity(IntentFilter intentFilter, int i) throws RemoteException;

    String[] currentToCanonicalPackageNames(String[] strArr) throws RemoteException;

    void deleteApplicationCacheFiles(String str, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void deleteApplicationCacheFilesAsUser(String str, int i, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void deleteExistingPackageAsUser(VersionedPackage versionedPackage, IPackageDeleteObserver2 iPackageDeleteObserver2, int i) throws RemoteException;

    @Deprecated
    void deletePackageAsUser(String str, int i, IPackageDeleteObserver iPackageDeleteObserver, int i2, int i3) throws RemoteException;

    void deletePackageVersioned(VersionedPackage versionedPackage, IPackageDeleteObserver2 iPackageDeleteObserver2, int i, int i2) throws RemoteException;

    void deletePreloadsFileCache() throws RemoteException;

    void enterSafeMode() throws RemoteException;

    void extendVerificationTimeout(int i, int i2, long j) throws RemoteException;

    ResolveInfo findPersistentPreferredActivity(Intent intent, int i) throws RemoteException;

    void finishPackageInstall(int i, boolean z) throws RemoteException;

    void flushPackageRestrictionsAsUser(int i) throws RemoteException;

    void freeStorage(String str, long j, int i, IntentSender intentSender) throws RemoteException;

    void freeStorageAndNotify(String str, long j, int i, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    ActivityInfo getActivityInfo(ComponentName componentName, long j, int i) throws RemoteException;

    ParceledListSlice getAllIntentFilters(String str) throws RemoteException;

    List<String> getAllPackages() throws RemoteException;

    ParcelFileDescriptor getAppMetadataFd(String str, int i) throws RemoteException;

    String[] getAppOpPermissionPackages(String str, int i) throws RemoteException;

    String getAppPredictionServicePackageName() throws RemoteException;

    int getApplicationEnabledSetting(String str, int i) throws RemoteException;

    boolean getApplicationHiddenSettingAsUser(String str, int i) throws RemoteException;

    ApplicationInfo getApplicationInfo(String str, long j, int i) throws RemoteException;

    IArtManager getArtManager() throws RemoteException;

    String getAttentionServicePackageName() throws RemoteException;

    boolean getBlockUninstallForUser(String str, int i) throws RemoteException;

    ChangedPackages getChangedPackages(int i, int i2) throws RemoteException;

    int getComponentEnabledSetting(ComponentName componentName, int i) throws RemoteException;

    ParceledListSlice getDeclaredSharedLibraries(String str, long j, int i) throws RemoteException;

    byte[] getDefaultAppsBackup(int i) throws RemoteException;

    String getDefaultTextClassifierPackageName() throws RemoteException;

    byte[] getDomainVerificationBackup(int i) throws RemoteException;

    int getFlagsForUid(int i) throws RemoteException;

    CharSequence getHarmfulAppWarning(String str, int i) throws RemoteException;

    IBinder getHoldLockToken() throws RemoteException;

    ComponentName getHomeActivities(List<ResolveInfo> list) throws RemoteException;

    String getIncidentReportApproverPackageName() throws RemoteException;

    int getInstallLocation() throws RemoteException;

    int getInstallReason(String str, int i) throws RemoteException;

    InstallSourceInfo getInstallSourceInfo(String str) throws RemoteException;

    ParceledListSlice getInstalledApplications(long j, int i) throws RemoteException;

    List<ModuleInfo> getInstalledModules(int i) throws RemoteException;

    ParceledListSlice getInstalledPackages(long j, int i) throws RemoteException;

    String getInstallerPackageName(String str) throws RemoteException;

    String getInstantAppAndroidId(String str, int i) throws RemoteException;

    byte[] getInstantAppCookie(String str, int i) throws RemoteException;

    Bitmap getInstantAppIcon(String str, int i) throws RemoteException;

    ComponentName getInstantAppInstallerComponent() throws RemoteException;

    ComponentName getInstantAppResolverComponent() throws RemoteException;

    ComponentName getInstantAppResolverSettingsComponent() throws RemoteException;

    ParceledListSlice getInstantApps(int i) throws RemoteException;

    InstrumentationInfo getInstrumentationInfoAsUser(ComponentName componentName, int i, int i2) throws RemoteException;

    @Deprecated
    ParceledListSlice getIntentFilterVerifications(String str) throws RemoteException;

    @Deprecated
    int getIntentVerificationStatus(String str, int i) throws RemoteException;

    KeySet getKeySetByAlias(String str, String str2) throws RemoteException;

    ResolveInfo getLastChosenActivity(Intent intent, String str, int i) throws RemoteException;

    IntentSender getLaunchIntentSenderForPackage(String str, String str2, String str3, int i) throws RemoteException;

    List<String> getMimeGroup(String str, String str2) throws RemoteException;

    ModuleInfo getModuleInfo(String str, int i) throws RemoteException;

    int getMoveStatus(int i) throws RemoteException;

    String getNameForUid(int i) throws RemoteException;

    String[] getNamesForUids(int[] iArr) throws RemoteException;

    int[] getPackageGids(String str, long j, int i) throws RemoteException;

    PackageInfo getPackageInfo(String str, long j, int i) throws RemoteException;

    PackageInfo getPackageInfoVersioned(VersionedPackage versionedPackage, long j, int i) throws RemoteException;

    IPackageInstaller getPackageInstaller() throws RemoteException;

    void getPackageSizeInfo(String str, int i, IPackageStatsObserver iPackageStatsObserver) throws RemoteException;

    int getPackageUid(String str, long j, int i) throws RemoteException;

    String[] getPackagesForUid(int i) throws RemoteException;

    ParceledListSlice getPackagesHoldingPermissions(String[] strArr, long j, int i) throws RemoteException;

    String getPermissionControllerPackageName() throws RemoteException;

    PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException;

    ParceledListSlice getPersistentApplications(int i) throws RemoteException;

    int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str) throws RemoteException;

    byte[] getPreferredActivityBackup(int i) throws RemoteException;

    int getPrivateFlagsForUid(int i) throws RemoteException;

    PackageManager.Property getPropertyAsUser(String str, String str2, String str3, int i) throws RemoteException;

    ProviderInfo getProviderInfo(ComponentName componentName, long j, int i) throws RemoteException;

    ActivityInfo getReceiverInfo(ComponentName componentName, long j, int i) throws RemoteException;

    String getRotationResolverPackageName() throws RemoteException;

    int getRuntimePermissionsVersion(int i) throws RemoteException;

    String getSdkSandboxPackageName() throws RemoteException;

    ServiceInfo getServiceInfo(ComponentName componentName, long j, int i) throws RemoteException;

    String getServicesSystemSharedLibraryPackageName() throws RemoteException;

    String getSetupWizardPackageName() throws RemoteException;

    ParceledListSlice getSharedLibraries(String str, long j, int i) throws RemoteException;

    String getSharedSystemSharedLibraryPackageName() throws RemoteException;

    KeySet getSigningKeySet(String str) throws RemoteException;

    String getSplashScreenTheme(String str, int i) throws RemoteException;

    Bundle getSuspendedPackageAppExtras(String str, int i) throws RemoteException;

    ParceledListSlice getSystemAvailableFeatures() throws RemoteException;

    String getSystemCaptionsServicePackageName() throws RemoteException;

    String[] getSystemSharedLibraryNames() throws RemoteException;

    String getSystemTextClassifierPackageName() throws RemoteException;

    int getTargetSdkVersion(String str) throws RemoteException;

    int getUidForSharedUser(String str) throws RemoteException;

    String[] getUnsuspendablePackagesForUser(String[] strArr, int i) throws RemoteException;

    VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException;

    String getWellbeingPackageName() throws RemoteException;

    void grantRuntimePermission(String str, String str2, int i) throws RemoteException;

    boolean hasSigningCertificate(String str, byte[] bArr, int i) throws RemoteException;

    boolean hasSystemFeature(String str, int i) throws RemoteException;

    boolean hasSystemUidErrors() throws RemoteException;

    boolean hasUidSigningCertificate(int i, byte[] bArr, int i2) throws RemoteException;

    void holdLock(IBinder iBinder, int i) throws RemoteException;

    int installExistingPackageAsUser(String str, int i, int i2, int i3, List<String> list) throws RemoteException;

    boolean isAutoRevokeWhitelisted(String str) throws RemoteException;

    boolean isDeviceUpgrading() throws RemoteException;

    boolean isFirstBoot() throws RemoteException;

    boolean isInstantApp(String str, int i) throws RemoteException;

    boolean isPackageAvailable(String str, int i) throws RemoteException;

    boolean isPackageDeviceAdminOnAnyUser(String str) throws RemoteException;

    boolean isPackageSignedByKeySet(String str, KeySet keySet) throws RemoteException;

    boolean isPackageSignedByKeySetExactly(String str, KeySet keySet) throws RemoteException;

    boolean isPackageStateProtected(String str, int i) throws RemoteException;

    boolean isPackageSuspendedForUser(String str, int i) throws RemoteException;

    boolean isProtectedBroadcast(String str) throws RemoteException;

    boolean isSafeMode() throws RemoteException;

    boolean isStorageLow() throws RemoteException;

    boolean isUidPrivileged(int i) throws RemoteException;

    void logAppProcessStartIfNeeded(String str, String str2, int i, String str3, String str4, int i2) throws RemoteException;

    void makeProviderVisible(int i, String str) throws RemoteException;

    void makeUidVisible(int i, int i2) throws RemoteException;

    int movePackage(String str, String str2) throws RemoteException;

    int movePrimaryStorage(String str) throws RemoteException;

    void notifyDexLoad(String str, Map<String, String> map, String str2) throws RemoteException;

    void notifyPackageUse(String str, int i) throws RemoteException;

    void notifyPackagesReplacedReceived(String[] strArr) throws RemoteException;

    void overrideLabelAndIcon(ComponentName componentName, String str, int i, int i2) throws RemoteException;

    boolean performDexOptMode(String str, boolean z, String str2, boolean z2, boolean z3, String str3) throws RemoteException;

    boolean performDexOptSecondary(String str, String str2, boolean z) throws RemoteException;

    ParceledListSlice queryContentProviders(String str, int i, long j, String str2) throws RemoteException;

    ParceledListSlice queryInstrumentationAsUser(String str, int i, int i2) throws RemoteException;

    ParceledListSlice queryIntentActivities(Intent intent, String str, long j, int i) throws RemoteException;

    ParceledListSlice queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, String[] strArr, Intent intent, String str, long j, int i) throws RemoteException;

    ParceledListSlice queryIntentContentProviders(Intent intent, String str, long j, int i) throws RemoteException;

    ParceledListSlice queryIntentReceivers(Intent intent, String str, long j, int i) throws RemoteException;

    ParceledListSlice queryIntentServices(Intent intent, String str, long j, int i) throws RemoteException;

    ParceledListSlice queryProperty(String str, int i) throws RemoteException;

    void querySyncProviders(List<String> list, List<ProviderInfo> list2) throws RemoteException;

    void registerDexModule(String str, String str2, boolean z, IDexModuleRegisterCallback iDexModuleRegisterCallback) throws RemoteException;

    void registerMoveCallback(IPackageMoveObserver iPackageMoveObserver) throws RemoteException;

    void relinquishUpdateOwnership(String str) throws RemoteException;

    boolean removeCrossProfileIntentFilter(IntentFilter intentFilter, String str, int i, int i2, int i3) throws RemoteException;

    void removePermission(String str) throws RemoteException;

    void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) throws RemoteException;

    void requestPackageChecksums(String str, boolean z, int i, int i2, List list, IOnChecksumsReadyListener iOnChecksumsReadyListener, int i3) throws RemoteException;

    void resetApplicationPreferences(int i) throws RemoteException;

    ProviderInfo resolveContentProvider(String str, long j, int i) throws RemoteException;

    ResolveInfo resolveIntent(Intent intent, String str, long j, int i) throws RemoteException;

    ResolveInfo resolveService(Intent intent, String str, long j, int i) throws RemoteException;

    void restoreDefaultApps(byte[] bArr, int i) throws RemoteException;

    void restoreDomainVerification(byte[] bArr, int i) throws RemoteException;

    void restoreLabelAndIcon(ComponentName componentName, int i) throws RemoteException;

    void restorePreferredActivities(byte[] bArr, int i) throws RemoteException;

    void sendDeviceCustomizationReadyBroadcast() throws RemoteException;

    void setApplicationCategoryHint(String str, int i, String str2) throws RemoteException;

    void setApplicationEnabledSetting(String str, int i, int i2, int i3, String str2) throws RemoteException;

    boolean setApplicationHiddenSettingAsUser(String str, boolean z, int i) throws RemoteException;

    boolean setBlockUninstallForUser(String str, boolean z, int i) throws RemoteException;

    void setComponentEnabledSetting(ComponentName componentName, int i, int i2, int i3) throws RemoteException;

    void setComponentEnabledSettings(List<PackageManager.ComponentEnabledSetting> list, int i) throws RemoteException;

    String[] setDistractingPackageRestrictionsAsUser(String[] strArr, int i, int i2) throws RemoteException;

    void setHarmfulAppWarning(String str, CharSequence charSequence, int i) throws RemoteException;

    void setHomeActivity(ComponentName componentName, int i) throws RemoteException;

    boolean setInstallLocation(int i) throws RemoteException;

    void setInstallerPackageName(String str, String str2) throws RemoteException;

    boolean setInstantAppCookie(String str, byte[] bArr, int i) throws RemoteException;

    void setKeepUninstalledPackages(List<String> list) throws RemoteException;

    void setLastChosenActivity(Intent intent, String str, int i, IntentFilter intentFilter, int i2, ComponentName componentName) throws RemoteException;

    void setMimeGroup(String str, String str2, List<String> list) throws RemoteException;

    void setPackageStoppedState(String str, boolean z, int i) throws RemoteException;

    String[] setPackagesSuspendedAsUser(String[] strArr, boolean z, PersistableBundle persistableBundle, PersistableBundle persistableBundle2, SuspendDialogInfo suspendDialogInfo, String str, int i) throws RemoteException;

    boolean setRequiredForSystemUser(String str, boolean z) throws RemoteException;

    void setRuntimePermissionsVersion(int i, int i2) throws RemoteException;

    void setSplashScreenTheme(String str, String str2, int i) throws RemoteException;

    void setSystemAppHiddenUntilInstalled(String str, boolean z) throws RemoteException;

    boolean setSystemAppInstallState(String str, boolean z, int i) throws RemoteException;

    void setUpdateAvailable(String str, boolean z) throws RemoteException;

    void unregisterMoveCallback(IPackageMoveObserver iPackageMoveObserver) throws RemoteException;

    @Deprecated
    boolean updateIntentVerificationStatus(String str, int i, int i2) throws RemoteException;

    @Deprecated
    void verifyIntentFilter(int i, int i2, List<String> list) throws RemoteException;

    void verifyPendingInstall(int i, int i2) throws RemoteException;

    /* renamed from: android.content.pm.IPackageManager$Default */
    /* loaded from: classes.dex */
    public static class Default implements IPackageManager {
        @Override // android.content.p001pm.IPackageManager
        public void checkPackageStartable(String packageName, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageAvailable(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public PackageInfo getPackageInfo(String packageName, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public PackageInfo getPackageInfoVersioned(VersionedPackage versionedPackage, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getPackageUid(String packageName, long flags, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int[] getPackageGids(String packageName, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] currentToCanonicalPackageNames(String[] names) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] canonicalToCurrentPackageNames(String[] names) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ApplicationInfo getApplicationInfo(String packageName, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getTargetSdkVersion(String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public ActivityInfo getActivityInfo(ComponentName className, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean activitySupportsIntentAsUser(ComponentName className, Intent intent, String resolvedType, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public ActivityInfo getReceiverInfo(ComponentName className, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ServiceInfo getServiceInfo(ComponentName className, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ProviderInfo getProviderInfo(ComponentName className, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isProtectedBroadcast(String actionName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public int checkSignatures(String pkg1, String pkg2, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int checkUidSignatures(int uid1, int uid2) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public List<String> getAllPackages() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] getPackagesForUid(int uid) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getNameForUid(int uid) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] getNamesForUids(int[] uids) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getUidForSharedUser(String sharedUserName) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getFlagsForUid(int uid) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getPrivateFlagsForUid(int uid) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isUidPrivileged(int uid) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public ResolveInfo resolveIntent(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ResolveInfo findPersistentPreferredActivity(Intent intent, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean canForwardTo(Intent intent, String resolvedType, int sourceUserId, int targetUserId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryIntentActivities(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryIntentReceivers(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ResolveInfo resolveService(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryIntentServices(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryIntentContentProviders(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getInstalledPackages(long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParcelFileDescriptor getAppMetadataFd(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getPackagesHoldingPermissions(String[] permissions, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getInstalledApplications(long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getPersistentApplications(int flags) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ProviderInfo resolveContentProvider(String name, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryContentProviders(String processName, int uid, long flags, String metaDataKey) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public InstrumentationInfo getInstrumentationInfoAsUser(ComponentName className, int flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryInstrumentationAsUser(String targetPackage, int flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void finishPackageInstall(int token, boolean didLaunch) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void setInstallerPackageName(String targetPackage, String installerPackageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void relinquishUpdateOwnership(String targetPackage) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void setApplicationCategoryHint(String packageName, int categoryHint, String callerPackageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void deletePackageAsUser(String packageName, int versionCode, IPackageDeleteObserver observer, int userId, int flags) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void deletePackageVersioned(VersionedPackage versionedPackage, IPackageDeleteObserver2 observer, int userId, int flags) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void deleteExistingPackageAsUser(VersionedPackage versionedPackage, IPackageDeleteObserver2 observer, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public String getInstallerPackageName(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public InstallSourceInfo getInstallSourceInfo(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void resetApplicationPreferences(int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId, boolean removeExisting) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearPackagePreferredActivities(String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void addPersistentPreferredActivity(IntentFilter filter, ComponentName activity, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearPackagePersistentPreferredActivities(String packageName, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearPersistentPreferredActivity(IntentFilter filter, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void addCrossProfileIntentFilter(IntentFilter intentFilter, String ownerPackage, int sourceUserId, int targetUserId, int flags) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean removeCrossProfileIntentFilter(IntentFilter intentFilter, String ownerPackage, int sourceUserId, int targetUserId, int flags) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearCrossProfileIntentFilters(int sourceUserId, String ownerPackage) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] setDistractingPackageRestrictionsAsUser(String[] packageNames, int restrictionFlags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] setPackagesSuspendedAsUser(String[] packageNames, boolean suspended, PersistableBundle appExtras, PersistableBundle launcherExtras, SuspendDialogInfo dialogInfo, String callingPackage, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] getUnsuspendablePackagesForUser(String[] packageNames, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageSuspendedForUser(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public Bundle getSuspendedPackageAppExtras(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public byte[] getPreferredActivityBackup(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void restorePreferredActivities(byte[] backup, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public byte[] getDefaultAppsBackup(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void restoreDefaultApps(byte[] backup, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public byte[] getDomainVerificationBackup(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void restoreDomainVerification(byte[] backup, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public ComponentName getHomeActivities(List<ResolveInfo> outHomeCandidates) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setHomeActivity(ComponentName className, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void overrideLabelAndIcon(ComponentName componentName, String nonLocalizedLabel, int icon, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void restoreLabelAndIcon(ComponentName componentName, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void setComponentEnabledSettings(List<PackageManager.ComponentEnabledSetting> settings, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int getComponentEnabledSetting(ComponentName componentName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId, String callingPackage) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int getApplicationEnabledSetting(String packageName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void logAppProcessStartIfNeeded(String packageName, String processName, int uid, String seinfo, String apkFile, int pid) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void flushPackageRestrictionsAsUser(int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void setPackageStoppedState(String packageName, boolean stopped, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void freeStorageAndNotify(String volumeUuid, long freeStorageSize, int storageFlags, IPackageDataObserver observer) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void freeStorage(String volumeUuid, long freeStorageSize, int storageFlags, IntentSender pi) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void deleteApplicationCacheFilesAsUser(String packageName, int userId, IPackageDataObserver observer) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void clearApplicationProfileData(String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] getSystemSharedLibraryNames() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getSystemAvailableFeatures() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean hasSystemFeature(String name, int version) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void enterSafeMode() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isSafeMode() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean hasSystemUidErrors() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void notifyPackageUse(String packageName, int reason) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void notifyDexLoad(String loadingPackageName, Map<String, String> classLoaderContextMap, String loaderIsa) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void registerDexModule(String packageName, String dexModulePath, boolean isSharedModule, IDexModuleRegisterCallback callback) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean performDexOptMode(String packageName, boolean checkProfiles, String targetCompilerFilter, boolean force, boolean bootComplete, String splitName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean performDexOptSecondary(String packageName, String targetCompilerFilter, boolean force) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getMoveStatus(int moveId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void registerMoveCallback(IPackageMoveObserver callback) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void unregisterMoveCallback(IPackageMoveObserver callback) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int movePackage(String packageName, String volumeUuid) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int movePrimaryStorage(String volumeUuid) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setInstallLocation(int loc) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getInstallLocation() throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public int installExistingPackageAsUser(String packageName, int userId, int installFlags, int installReason, List<String> whiteListedPermissions) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void verifyPendingInstall(int id, int verificationCode) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void verifyIntentFilter(int id, int verificationCode, List<String> failedDomains) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int getIntentVerificationStatus(String packageName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean updateIntentVerificationStatus(String packageName, int status, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getIntentFilterVerifications(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getAllIntentFilters(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isFirstBoot() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isDeviceUpgrading() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isStorageLow() throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setApplicationHiddenSettingAsUser(String packageName, boolean hidden, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean getApplicationHiddenSettingAsUser(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setSystemAppHiddenUntilInstalled(String packageName, boolean hidden) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setSystemAppInstallState(String packageName, boolean installed, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public IPackageInstaller getPackageInstaller() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setBlockUninstallForUser(String packageName, boolean blockUninstall, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean getBlockUninstallForUser(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public KeySet getKeySetByAlias(String packageName, String alias) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public KeySet getSigningKeySet(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageSignedByKeySet(String packageName, KeySet ks) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageSignedByKeySetExactly(String packageName, KeySet ks) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getPermissionControllerPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSdkSandboxPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getInstantApps(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public byte[] getInstantAppCookie(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setInstantAppCookie(String packageName, byte[] cookie, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public Bitmap getInstantAppIcon(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isInstantApp(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean setRequiredForSystemUser(String packageName, boolean systemUserApp) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setUpdateAvailable(String packageName, boolean updateAvaialble) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public String getServicesSystemSharedLibraryPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSharedSystemSharedLibraryPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ChangedPackages getChangedPackages(int sequenceNumber, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageDeviceAdminOnAnyUser(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getInstallReason(String packageName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getSharedLibraries(String packageName, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice getDeclaredSharedLibraries(String packageName, long flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean canRequestPackageInstalls(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void deletePreloadsFileCache() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public ComponentName getInstantAppResolverComponent() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ComponentName getInstantAppResolverSettingsComponent() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ComponentName getInstantAppInstallerComponent() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getInstantAppAndroidId(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public IArtManager getArtManager() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setHarmfulAppWarning(String packageName, CharSequence warning, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public CharSequence getHarmfulAppWarning(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean hasSigningCertificate(String packageName, byte[] signingCertificate, int flags) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean hasUidSigningCertificate(int uid, byte[] signingCertificate, int flags) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getDefaultTextClassifierPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSystemTextClassifierPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getAttentionServicePackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getRotationResolverPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getWellbeingPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getAppPredictionServicePackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSystemCaptionsServicePackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSetupWizardPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String getIncidentReportApproverPackageName() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isPackageStateProtected(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void sendDeviceCustomizationReadyBroadcast() throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public List<ModuleInfo> getInstalledModules(int flags) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ModuleInfo getModuleInfo(String packageName, int flags) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public int getRuntimePermissionsVersion(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setRuntimePermissionsVersion(int version, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void notifyPackagesReplacedReceived(String[] packages) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void requestPackageChecksums(String packageName, boolean includeSplits, int optional, int required, List trustedInstallers, IOnChecksumsReadyListener onChecksumsReadyListener, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public IntentSender getLaunchIntentSenderForPackage(String packageName, String callingPackage, String featureId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public String[] getAppOpPermissionPackages(String permissionName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean addPermission(PermissionInfo info) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean addPermissionAsync(PermissionInfo info) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void removePermission(String name) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int checkPermission(String permName, String pkgName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void grantRuntimePermission(String packageName, String permissionName, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public int checkUidPermission(String permName, int uid) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setMimeGroup(String packageName, String group, List<String> mimeTypes) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public String getSplashScreenTheme(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setSplashScreenTheme(String packageName, String themeName, int userId) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public List<String> getMimeGroup(String packageName, String group) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean isAutoRevokeWhitelisted(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.IPackageManager
        public void makeProviderVisible(int recipientAppId, String visibleAuthority) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public void makeUidVisible(int recipientAppId, int visibleUid) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public IBinder getHoldLockToken() throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void holdLock(IBinder token, int durationMs) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public PackageManager.Property getPropertyAsUser(String propertyName, String packageName, String className, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public ParceledListSlice queryProperty(String propertyName, int componentType) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.IPackageManager
        public void setKeepUninstalledPackages(List<String> packageList) throws RemoteException {
        }

        @Override // android.content.p001pm.IPackageManager
        public boolean[] canPackageQuery(String sourcePackageName, String[] targetPackageNames, int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IPackageManager$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPackageManager {
        public static final String DESCRIPTOR = "android.content.pm.IPackageManager";
        static final int TRANSACTION_activitySupportsIntentAsUser = 12;
        static final int TRANSACTION_addCrossProfileIntentFilter = 65;
        static final int TRANSACTION_addPermission = 186;
        static final int TRANSACTION_addPermissionAsync = 187;
        static final int TRANSACTION_addPersistentPreferredActivity = 62;
        static final int TRANSACTION_addPreferredActivity = 58;
        static final int TRANSACTION_canForwardTo = 29;
        static final int TRANSACTION_canPackageQuery = 204;
        static final int TRANSACTION_canRequestPackageInstalls = 155;
        static final int TRANSACTION_canonicalToCurrentPackageNames = 8;
        static final int TRANSACTION_checkPackageStartable = 1;
        static final int TRANSACTION_checkPermission = 189;
        static final int TRANSACTION_checkSignatures = 17;
        static final int TRANSACTION_checkUidPermission = 191;
        static final int TRANSACTION_checkUidSignatures = 18;
        static final int TRANSACTION_clearApplicationProfileData = 96;
        static final int TRANSACTION_clearApplicationUserData = 95;
        static final int TRANSACTION_clearCrossProfileIntentFilters = 67;
        static final int TRANSACTION_clearPackagePersistentPreferredActivities = 63;
        static final int TRANSACTION_clearPackagePreferredActivities = 60;
        static final int TRANSACTION_clearPersistentPreferredActivity = 64;
        static final int TRANSACTION_currentToCanonicalPackageNames = 7;
        static final int TRANSACTION_deleteApplicationCacheFiles = 93;
        static final int TRANSACTION_deleteApplicationCacheFilesAsUser = 94;
        static final int TRANSACTION_deleteExistingPackageAsUser = 52;
        static final int TRANSACTION_deletePackageAsUser = 50;
        static final int TRANSACTION_deletePackageVersioned = 51;
        static final int TRANSACTION_deletePreloadsFileCache = 156;
        static final int TRANSACTION_enterSafeMode = 101;
        static final int TRANSACTION_extendVerificationTimeout = 118;
        static final int TRANSACTION_findPersistentPreferredActivity = 28;
        static final int TRANSACTION_finishPackageInstall = 46;
        static final int TRANSACTION_flushPackageRestrictionsAsUser = 89;
        static final int TRANSACTION_freeStorage = 92;
        static final int TRANSACTION_freeStorageAndNotify = 91;
        static final int TRANSACTION_getActivityInfo = 11;
        static final int TRANSACTION_getAllIntentFilters = 123;
        static final int TRANSACTION_getAllPackages = 19;
        static final int TRANSACTION_getAppMetadataFd = 37;
        static final int TRANSACTION_getAppOpPermissionPackages = 184;
        static final int TRANSACTION_getAppPredictionServicePackageName = 171;
        static final int TRANSACTION_getApplicationEnabledSetting = 87;
        static final int TRANSACTION_getApplicationHiddenSettingAsUser = 129;
        static final int TRANSACTION_getApplicationInfo = 9;
        static final int TRANSACTION_getArtManager = 161;
        static final int TRANSACTION_getAttentionServicePackageName = 168;
        static final int TRANSACTION_getBlockUninstallForUser = 134;
        static final int TRANSACTION_getChangedPackages = 150;
        static final int TRANSACTION_getComponentEnabledSetting = 85;
        static final int TRANSACTION_getDeclaredSharedLibraries = 154;
        static final int TRANSACTION_getDefaultAppsBackup = 75;
        static final int TRANSACTION_getDefaultTextClassifierPackageName = 166;
        static final int TRANSACTION_getDomainVerificationBackup = 77;
        static final int TRANSACTION_getFlagsForUid = 24;
        static final int TRANSACTION_getHarmfulAppWarning = 163;
        static final int TRANSACTION_getHoldLockToken = 199;
        static final int TRANSACTION_getHomeActivities = 79;
        static final int TRANSACTION_getIncidentReportApproverPackageName = 174;
        static final int TRANSACTION_getInstallLocation = 115;
        static final int TRANSACTION_getInstallReason = 152;
        static final int TRANSACTION_getInstallSourceInfo = 54;
        static final int TRANSACTION_getInstalledApplications = 39;
        static final int TRANSACTION_getInstalledModules = 177;
        static final int TRANSACTION_getInstalledPackages = 36;
        static final int TRANSACTION_getInstallerPackageName = 53;
        static final int TRANSACTION_getInstantAppAndroidId = 160;
        static final int TRANSACTION_getInstantAppCookie = 142;
        static final int TRANSACTION_getInstantAppIcon = 144;
        static final int TRANSACTION_getInstantAppInstallerComponent = 159;
        static final int TRANSACTION_getInstantAppResolverComponent = 157;
        static final int TRANSACTION_getInstantAppResolverSettingsComponent = 158;
        static final int TRANSACTION_getInstantApps = 141;
        static final int TRANSACTION_getInstrumentationInfoAsUser = 44;
        static final int TRANSACTION_getIntentFilterVerifications = 122;
        static final int TRANSACTION_getIntentVerificationStatus = 120;
        static final int TRANSACTION_getKeySetByAlias = 135;
        static final int TRANSACTION_getLastChosenActivity = 56;
        static final int TRANSACTION_getLaunchIntentSenderForPackage = 183;
        static final int TRANSACTION_getMimeGroup = 195;
        static final int TRANSACTION_getModuleInfo = 178;
        static final int TRANSACTION_getMoveStatus = 109;
        static final int TRANSACTION_getNameForUid = 21;
        static final int TRANSACTION_getNamesForUids = 22;
        static final int TRANSACTION_getPackageGids = 6;
        static final int TRANSACTION_getPackageInfo = 3;
        static final int TRANSACTION_getPackageInfoVersioned = 4;
        static final int TRANSACTION_getPackageInstaller = 132;
        static final int TRANSACTION_getPackageSizeInfo = 97;
        static final int TRANSACTION_getPackageUid = 5;
        static final int TRANSACTION_getPackagesForUid = 20;
        static final int TRANSACTION_getPackagesHoldingPermissions = 38;
        static final int TRANSACTION_getPermissionControllerPackageName = 139;
        static final int TRANSACTION_getPermissionGroupInfo = 185;
        static final int TRANSACTION_getPersistentApplications = 40;
        static final int TRANSACTION_getPreferredActivities = 61;
        static final int TRANSACTION_getPreferredActivityBackup = 73;
        static final int TRANSACTION_getPrivateFlagsForUid = 25;
        static final int TRANSACTION_getPropertyAsUser = 201;
        static final int TRANSACTION_getProviderInfo = 15;
        static final int TRANSACTION_getReceiverInfo = 13;
        static final int TRANSACTION_getRotationResolverPackageName = 169;
        static final int TRANSACTION_getRuntimePermissionsVersion = 179;
        static final int TRANSACTION_getSdkSandboxPackageName = 140;
        static final int TRANSACTION_getServiceInfo = 14;
        static final int TRANSACTION_getServicesSystemSharedLibraryPackageName = 148;
        static final int TRANSACTION_getSetupWizardPackageName = 173;
        static final int TRANSACTION_getSharedLibraries = 153;
        static final int TRANSACTION_getSharedSystemSharedLibraryPackageName = 149;
        static final int TRANSACTION_getSigningKeySet = 136;
        static final int TRANSACTION_getSplashScreenTheme = 193;
        static final int TRANSACTION_getSuspendedPackageAppExtras = 72;
        static final int TRANSACTION_getSystemAvailableFeatures = 99;
        static final int TRANSACTION_getSystemCaptionsServicePackageName = 172;
        static final int TRANSACTION_getSystemSharedLibraryNames = 98;
        static final int TRANSACTION_getSystemTextClassifierPackageName = 167;
        static final int TRANSACTION_getTargetSdkVersion = 10;
        static final int TRANSACTION_getUidForSharedUser = 23;
        static final int TRANSACTION_getUnsuspendablePackagesForUser = 70;
        static final int TRANSACTION_getVerifierDeviceIdentity = 124;
        static final int TRANSACTION_getWellbeingPackageName = 170;
        static final int TRANSACTION_grantRuntimePermission = 190;
        static final int TRANSACTION_hasSigningCertificate = 164;
        static final int TRANSACTION_hasSystemFeature = 100;
        static final int TRANSACTION_hasSystemUidErrors = 103;
        static final int TRANSACTION_hasUidSigningCertificate = 165;
        static final int TRANSACTION_holdLock = 200;
        static final int TRANSACTION_installExistingPackageAsUser = 116;
        static final int TRANSACTION_isAutoRevokeWhitelisted = 196;
        static final int TRANSACTION_isDeviceUpgrading = 126;
        static final int TRANSACTION_isFirstBoot = 125;
        static final int TRANSACTION_isInstantApp = 145;
        static final int TRANSACTION_isPackageAvailable = 2;
        static final int TRANSACTION_isPackageDeviceAdminOnAnyUser = 151;
        static final int TRANSACTION_isPackageSignedByKeySet = 137;
        static final int TRANSACTION_isPackageSignedByKeySetExactly = 138;
        static final int TRANSACTION_isPackageStateProtected = 175;
        static final int TRANSACTION_isPackageSuspendedForUser = 71;
        static final int TRANSACTION_isProtectedBroadcast = 16;
        static final int TRANSACTION_isSafeMode = 102;
        static final int TRANSACTION_isStorageLow = 127;
        static final int TRANSACTION_isUidPrivileged = 26;
        static final int TRANSACTION_logAppProcessStartIfNeeded = 88;
        static final int TRANSACTION_makeProviderVisible = 197;
        static final int TRANSACTION_makeUidVisible = 198;
        static final int TRANSACTION_movePackage = 112;
        static final int TRANSACTION_movePrimaryStorage = 113;
        static final int TRANSACTION_notifyDexLoad = 105;
        static final int TRANSACTION_notifyPackageUse = 104;
        static final int TRANSACTION_notifyPackagesReplacedReceived = 181;
        static final int TRANSACTION_overrideLabelAndIcon = 81;
        static final int TRANSACTION_performDexOptMode = 107;
        static final int TRANSACTION_performDexOptSecondary = 108;
        static final int TRANSACTION_queryContentProviders = 43;
        static final int TRANSACTION_queryInstrumentationAsUser = 45;
        static final int TRANSACTION_queryIntentActivities = 30;
        static final int TRANSACTION_queryIntentActivityOptions = 31;
        static final int TRANSACTION_queryIntentContentProviders = 35;
        static final int TRANSACTION_queryIntentReceivers = 32;
        static final int TRANSACTION_queryIntentServices = 34;
        static final int TRANSACTION_queryProperty = 202;
        static final int TRANSACTION_querySyncProviders = 42;
        static final int TRANSACTION_registerDexModule = 106;
        static final int TRANSACTION_registerMoveCallback = 110;
        static final int TRANSACTION_relinquishUpdateOwnership = 48;
        static final int TRANSACTION_removeCrossProfileIntentFilter = 66;
        static final int TRANSACTION_removePermission = 188;
        static final int TRANSACTION_replacePreferredActivity = 59;
        static final int TRANSACTION_requestPackageChecksums = 182;
        static final int TRANSACTION_resetApplicationPreferences = 55;
        static final int TRANSACTION_resolveContentProvider = 41;
        static final int TRANSACTION_resolveIntent = 27;
        static final int TRANSACTION_resolveService = 33;
        static final int TRANSACTION_restoreDefaultApps = 76;
        static final int TRANSACTION_restoreDomainVerification = 78;
        static final int TRANSACTION_restoreLabelAndIcon = 82;
        static final int TRANSACTION_restorePreferredActivities = 74;
        static final int TRANSACTION_sendDeviceCustomizationReadyBroadcast = 176;
        static final int TRANSACTION_setApplicationCategoryHint = 49;
        static final int TRANSACTION_setApplicationEnabledSetting = 86;
        static final int TRANSACTION_setApplicationHiddenSettingAsUser = 128;
        static final int TRANSACTION_setBlockUninstallForUser = 133;
        static final int TRANSACTION_setComponentEnabledSetting = 83;
        static final int TRANSACTION_setComponentEnabledSettings = 84;
        static final int TRANSACTION_setDistractingPackageRestrictionsAsUser = 68;
        static final int TRANSACTION_setHarmfulAppWarning = 162;
        static final int TRANSACTION_setHomeActivity = 80;
        static final int TRANSACTION_setInstallLocation = 114;
        static final int TRANSACTION_setInstallerPackageName = 47;
        static final int TRANSACTION_setInstantAppCookie = 143;
        static final int TRANSACTION_setKeepUninstalledPackages = 203;
        static final int TRANSACTION_setLastChosenActivity = 57;
        static final int TRANSACTION_setMimeGroup = 192;
        static final int TRANSACTION_setPackageStoppedState = 90;
        static final int TRANSACTION_setPackagesSuspendedAsUser = 69;
        static final int TRANSACTION_setRequiredForSystemUser = 146;
        static final int TRANSACTION_setRuntimePermissionsVersion = 180;
        static final int TRANSACTION_setSplashScreenTheme = 194;
        static final int TRANSACTION_setSystemAppHiddenUntilInstalled = 130;
        static final int TRANSACTION_setSystemAppInstallState = 131;
        static final int TRANSACTION_setUpdateAvailable = 147;
        static final int TRANSACTION_unregisterMoveCallback = 111;
        static final int TRANSACTION_updateIntentVerificationStatus = 121;
        static final int TRANSACTION_verifyIntentFilter = 119;
        static final int TRANSACTION_verifyPendingInstall = 117;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPackageManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPackageManager)) {
                return (IPackageManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "checkPackageStartable";
                case 2:
                    return "isPackageAvailable";
                case 3:
                    return "getPackageInfo";
                case 4:
                    return "getPackageInfoVersioned";
                case 5:
                    return "getPackageUid";
                case 6:
                    return "getPackageGids";
                case 7:
                    return "currentToCanonicalPackageNames";
                case 8:
                    return "canonicalToCurrentPackageNames";
                case 9:
                    return "getApplicationInfo";
                case 10:
                    return "getTargetSdkVersion";
                case 11:
                    return "getActivityInfo";
                case 12:
                    return "activitySupportsIntentAsUser";
                case 13:
                    return "getReceiverInfo";
                case 14:
                    return "getServiceInfo";
                case 15:
                    return "getProviderInfo";
                case 16:
                    return "isProtectedBroadcast";
                case 17:
                    return "checkSignatures";
                case 18:
                    return "checkUidSignatures";
                case 19:
                    return "getAllPackages";
                case 20:
                    return "getPackagesForUid";
                case 21:
                    return "getNameForUid";
                case 22:
                    return "getNamesForUids";
                case 23:
                    return "getUidForSharedUser";
                case 24:
                    return "getFlagsForUid";
                case 25:
                    return "getPrivateFlagsForUid";
                case 26:
                    return "isUidPrivileged";
                case 27:
                    return "resolveIntent";
                case 28:
                    return "findPersistentPreferredActivity";
                case 29:
                    return "canForwardTo";
                case 30:
                    return "queryIntentActivities";
                case 31:
                    return "queryIntentActivityOptions";
                case 32:
                    return "queryIntentReceivers";
                case 33:
                    return "resolveService";
                case 34:
                    return "queryIntentServices";
                case 35:
                    return "queryIntentContentProviders";
                case 36:
                    return "getInstalledPackages";
                case 37:
                    return "getAppMetadataFd";
                case 38:
                    return "getPackagesHoldingPermissions";
                case 39:
                    return "getInstalledApplications";
                case 40:
                    return "getPersistentApplications";
                case 41:
                    return "resolveContentProvider";
                case 42:
                    return "querySyncProviders";
                case 43:
                    return "queryContentProviders";
                case 44:
                    return "getInstrumentationInfoAsUser";
                case 45:
                    return "queryInstrumentationAsUser";
                case 46:
                    return "finishPackageInstall";
                case 47:
                    return "setInstallerPackageName";
                case 48:
                    return "relinquishUpdateOwnership";
                case 49:
                    return "setApplicationCategoryHint";
                case 50:
                    return "deletePackageAsUser";
                case 51:
                    return "deletePackageVersioned";
                case 52:
                    return "deleteExistingPackageAsUser";
                case 53:
                    return "getInstallerPackageName";
                case 54:
                    return "getInstallSourceInfo";
                case 55:
                    return "resetApplicationPreferences";
                case 56:
                    return "getLastChosenActivity";
                case 57:
                    return "setLastChosenActivity";
                case 58:
                    return "addPreferredActivity";
                case 59:
                    return "replacePreferredActivity";
                case 60:
                    return "clearPackagePreferredActivities";
                case 61:
                    return "getPreferredActivities";
                case 62:
                    return "addPersistentPreferredActivity";
                case 63:
                    return "clearPackagePersistentPreferredActivities";
                case 64:
                    return "clearPersistentPreferredActivity";
                case 65:
                    return "addCrossProfileIntentFilter";
                case 66:
                    return "removeCrossProfileIntentFilter";
                case 67:
                    return "clearCrossProfileIntentFilters";
                case 68:
                    return "setDistractingPackageRestrictionsAsUser";
                case 69:
                    return "setPackagesSuspendedAsUser";
                case 70:
                    return "getUnsuspendablePackagesForUser";
                case 71:
                    return "isPackageSuspendedForUser";
                case 72:
                    return "getSuspendedPackageAppExtras";
                case 73:
                    return "getPreferredActivityBackup";
                case 74:
                    return "restorePreferredActivities";
                case 75:
                    return "getDefaultAppsBackup";
                case 76:
                    return "restoreDefaultApps";
                case 77:
                    return "getDomainVerificationBackup";
                case 78:
                    return "restoreDomainVerification";
                case 79:
                    return "getHomeActivities";
                case 80:
                    return "setHomeActivity";
                case 81:
                    return "overrideLabelAndIcon";
                case 82:
                    return "restoreLabelAndIcon";
                case 83:
                    return "setComponentEnabledSetting";
                case 84:
                    return "setComponentEnabledSettings";
                case 85:
                    return "getComponentEnabledSetting";
                case 86:
                    return "setApplicationEnabledSetting";
                case 87:
                    return "getApplicationEnabledSetting";
                case 88:
                    return "logAppProcessStartIfNeeded";
                case 89:
                    return "flushPackageRestrictionsAsUser";
                case 90:
                    return "setPackageStoppedState";
                case 91:
                    return "freeStorageAndNotify";
                case 92:
                    return "freeStorage";
                case 93:
                    return "deleteApplicationCacheFiles";
                case 94:
                    return "deleteApplicationCacheFilesAsUser";
                case 95:
                    return "clearApplicationUserData";
                case 96:
                    return "clearApplicationProfileData";
                case 97:
                    return "getPackageSizeInfo";
                case 98:
                    return "getSystemSharedLibraryNames";
                case 99:
                    return "getSystemAvailableFeatures";
                case 100:
                    return "hasSystemFeature";
                case 101:
                    return "enterSafeMode";
                case 102:
                    return "isSafeMode";
                case 103:
                    return "hasSystemUidErrors";
                case 104:
                    return "notifyPackageUse";
                case 105:
                    return "notifyDexLoad";
                case 106:
                    return "registerDexModule";
                case 107:
                    return "performDexOptMode";
                case 108:
                    return "performDexOptSecondary";
                case 109:
                    return "getMoveStatus";
                case 110:
                    return "registerMoveCallback";
                case 111:
                    return "unregisterMoveCallback";
                case 112:
                    return "movePackage";
                case 113:
                    return "movePrimaryStorage";
                case 114:
                    return "setInstallLocation";
                case 115:
                    return "getInstallLocation";
                case 116:
                    return "installExistingPackageAsUser";
                case 117:
                    return "verifyPendingInstall";
                case 118:
                    return "extendVerificationTimeout";
                case 119:
                    return "verifyIntentFilter";
                case 120:
                    return "getIntentVerificationStatus";
                case 121:
                    return "updateIntentVerificationStatus";
                case 122:
                    return "getIntentFilterVerifications";
                case 123:
                    return "getAllIntentFilters";
                case 124:
                    return "getVerifierDeviceIdentity";
                case 125:
                    return "isFirstBoot";
                case 126:
                    return "isDeviceUpgrading";
                case 127:
                    return "isStorageLow";
                case 128:
                    return "setApplicationHiddenSettingAsUser";
                case 129:
                    return "getApplicationHiddenSettingAsUser";
                case 130:
                    return "setSystemAppHiddenUntilInstalled";
                case 131:
                    return "setSystemAppInstallState";
                case 132:
                    return "getPackageInstaller";
                case 133:
                    return "setBlockUninstallForUser";
                case 134:
                    return "getBlockUninstallForUser";
                case 135:
                    return "getKeySetByAlias";
                case 136:
                    return "getSigningKeySet";
                case 137:
                    return "isPackageSignedByKeySet";
                case 138:
                    return "isPackageSignedByKeySetExactly";
                case 139:
                    return "getPermissionControllerPackageName";
                case 140:
                    return "getSdkSandboxPackageName";
                case 141:
                    return "getInstantApps";
                case 142:
                    return "getInstantAppCookie";
                case 143:
                    return "setInstantAppCookie";
                case 144:
                    return "getInstantAppIcon";
                case 145:
                    return "isInstantApp";
                case 146:
                    return "setRequiredForSystemUser";
                case 147:
                    return "setUpdateAvailable";
                case 148:
                    return "getServicesSystemSharedLibraryPackageName";
                case 149:
                    return "getSharedSystemSharedLibraryPackageName";
                case 150:
                    return "getChangedPackages";
                case 151:
                    return "isPackageDeviceAdminOnAnyUser";
                case 152:
                    return "getInstallReason";
                case 153:
                    return "getSharedLibraries";
                case 154:
                    return "getDeclaredSharedLibraries";
                case 155:
                    return "canRequestPackageInstalls";
                case 156:
                    return "deletePreloadsFileCache";
                case 157:
                    return "getInstantAppResolverComponent";
                case 158:
                    return "getInstantAppResolverSettingsComponent";
                case 159:
                    return "getInstantAppInstallerComponent";
                case 160:
                    return "getInstantAppAndroidId";
                case 161:
                    return "getArtManager";
                case 162:
                    return "setHarmfulAppWarning";
                case 163:
                    return "getHarmfulAppWarning";
                case 164:
                    return "hasSigningCertificate";
                case 165:
                    return "hasUidSigningCertificate";
                case 166:
                    return "getDefaultTextClassifierPackageName";
                case 167:
                    return "getSystemTextClassifierPackageName";
                case 168:
                    return "getAttentionServicePackageName";
                case 169:
                    return "getRotationResolverPackageName";
                case 170:
                    return "getWellbeingPackageName";
                case 171:
                    return "getAppPredictionServicePackageName";
                case 172:
                    return "getSystemCaptionsServicePackageName";
                case 173:
                    return "getSetupWizardPackageName";
                case 174:
                    return "getIncidentReportApproverPackageName";
                case 175:
                    return "isPackageStateProtected";
                case 176:
                    return "sendDeviceCustomizationReadyBroadcast";
                case 177:
                    return "getInstalledModules";
                case 178:
                    return "getModuleInfo";
                case 179:
                    return "getRuntimePermissionsVersion";
                case 180:
                    return "setRuntimePermissionsVersion";
                case 181:
                    return "notifyPackagesReplacedReceived";
                case 182:
                    return "requestPackageChecksums";
                case 183:
                    return "getLaunchIntentSenderForPackage";
                case 184:
                    return "getAppOpPermissionPackages";
                case 185:
                    return "getPermissionGroupInfo";
                case 186:
                    return "addPermission";
                case 187:
                    return "addPermissionAsync";
                case 188:
                    return "removePermission";
                case 189:
                    return "checkPermission";
                case 190:
                    return "grantRuntimePermission";
                case 191:
                    return "checkUidPermission";
                case 192:
                    return "setMimeGroup";
                case 193:
                    return "getSplashScreenTheme";
                case 194:
                    return "setSplashScreenTheme";
                case 195:
                    return "getMimeGroup";
                case 196:
                    return "isAutoRevokeWhitelisted";
                case 197:
                    return "makeProviderVisible";
                case 198:
                    return "makeUidVisible";
                case 199:
                    return "getHoldLockToken";
                case 200:
                    return "holdLock";
                case 201:
                    return "getPropertyAsUser";
                case 202:
                    return "queryProperty";
                case 203:
                    return "setKeepUninstalledPackages";
                case 204:
                    return "canPackageQuery";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, final Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            checkPackageStartable(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = isPackageAvailable(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            long _arg13 = data.readLong();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            PackageInfo _result2 = getPackageInfo(_arg03, _arg13, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            VersionedPackage _arg04 = (VersionedPackage) data.readTypedObject(VersionedPackage.CREATOR);
                            long _arg14 = data.readLong();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            PackageInfo _result3 = getPackageInfoVersioned(_arg04, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            long _arg15 = data.readLong();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result4 = getPackageUid(_arg05, _arg15, _arg23);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            long _arg16 = data.readLong();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            int[] _result5 = getPackageGids(_arg06, _arg16, _arg24);
                            reply.writeNoException();
                            reply.writeIntArray(_result5);
                            break;
                        case 7:
                            String[] _arg07 = data.createStringArray();
                            data.enforceNoDataAvail();
                            String[] _result6 = currentToCanonicalPackageNames(_arg07);
                            reply.writeNoException();
                            reply.writeStringArray(_result6);
                            break;
                        case 8:
                            String[] _arg08 = data.createStringArray();
                            data.enforceNoDataAvail();
                            String[] _result7 = canonicalToCurrentPackageNames(_arg08);
                            reply.writeNoException();
                            reply.writeStringArray(_result7);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            long _arg17 = data.readLong();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            ApplicationInfo _result8 = getApplicationInfo(_arg09, _arg17, _arg25);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            int _result9 = getTargetSdkVersion(_arg010);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 11:
                            ComponentName _arg011 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            long _arg18 = data.readLong();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            ActivityInfo _result10 = getActivityInfo(_arg011, _arg18, _arg26);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 12:
                            ComponentName _arg012 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Intent _arg19 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg27 = data.readString();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result11 = activitySupportsIntentAsUser(_arg012, _arg19, _arg27, _arg3);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 13:
                            ComponentName _arg013 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            long _arg110 = data.readLong();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            ActivityInfo _result12 = getReceiverInfo(_arg013, _arg110, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 14:
                            ComponentName _arg014 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            long _arg111 = data.readLong();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            ServiceInfo _result13 = getServiceInfo(_arg014, _arg111, _arg29);
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            break;
                        case 15:
                            ComponentName _arg015 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            long _arg112 = data.readLong();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            ProviderInfo _result14 = getProviderInfo(_arg015, _arg112, _arg210);
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result15 = isProtectedBroadcast(_arg016);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            String _arg113 = data.readString();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result16 = checkSignatures(_arg017, _arg113, _arg211);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result17 = checkUidSignatures(_arg018, _arg114);
                            reply.writeNoException();
                            reply.writeInt(_result17);
                            break;
                        case 19:
                            List<String> _result18 = getAllPackages();
                            reply.writeNoException();
                            reply.writeStringList(_result18);
                            break;
                        case 20:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result19 = getPackagesForUid(_arg019);
                            reply.writeNoException();
                            reply.writeStringArray(_result19);
                            break;
                        case 21:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result20 = getNameForUid(_arg020);
                            reply.writeNoException();
                            reply.writeString(_result20);
                            break;
                        case 22:
                            int[] _arg021 = data.createIntArray();
                            data.enforceNoDataAvail();
                            String[] _result21 = getNamesForUids(_arg021);
                            reply.writeNoException();
                            reply.writeStringArray(_result21);
                            break;
                        case 23:
                            String _arg022 = data.readString();
                            data.enforceNoDataAvail();
                            int _result22 = getUidForSharedUser(_arg022);
                            reply.writeNoException();
                            reply.writeInt(_result22);
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result23 = getFlagsForUid(_arg023);
                            reply.writeNoException();
                            reply.writeInt(_result23);
                            break;
                        case 25:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result24 = getPrivateFlagsForUid(_arg024);
                            reply.writeNoException();
                            reply.writeInt(_result24);
                            break;
                        case 26:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result25 = isUidPrivileged(_arg025);
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            break;
                        case 27:
                            Intent _arg026 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg115 = data.readString();
                            long _arg212 = data.readLong();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            ResolveInfo _result26 = resolveIntent(_arg026, _arg115, _arg212, _arg32);
                            reply.writeNoException();
                            reply.writeTypedObject(_result26, 1);
                            break;
                        case 28:
                            Intent _arg027 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            ResolveInfo _result27 = findPersistentPreferredActivity(_arg027, _arg116);
                            reply.writeNoException();
                            reply.writeTypedObject(_result27, 1);
                            break;
                        case 29:
                            Intent _arg028 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg117 = data.readString();
                            int _arg213 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result28 = canForwardTo(_arg028, _arg117, _arg213, _arg33);
                            reply.writeNoException();
                            reply.writeBoolean(_result28);
                            break;
                        case 30:
                            Intent _arg029 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg118 = data.readString();
                            long _arg214 = data.readLong();
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result29 = queryIntentActivities(_arg029, _arg118, _arg214, _arg34);
                            reply.writeNoException();
                            reply.writeTypedObject(_result29, 1);
                            break;
                        case 31:
                            ComponentName _arg030 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Intent[] _arg119 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                            String[] _arg215 = data.createStringArray();
                            Intent _arg35 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg4 = data.readString();
                            long _arg5 = data.readLong();
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result30 = queryIntentActivityOptions(_arg030, _arg119, _arg215, _arg35, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeTypedObject(_result30, 1);
                            break;
                        case 32:
                            Intent _arg031 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg120 = data.readString();
                            long _arg216 = data.readLong();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result31 = queryIntentReceivers(_arg031, _arg120, _arg216, _arg36);
                            reply.writeNoException();
                            reply.writeTypedObject(_result31, 1);
                            break;
                        case 33:
                            Intent _arg032 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg121 = data.readString();
                            long _arg217 = data.readLong();
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            ResolveInfo _result32 = resolveService(_arg032, _arg121, _arg217, _arg37);
                            reply.writeNoException();
                            reply.writeTypedObject(_result32, 1);
                            break;
                        case 34:
                            Intent _arg033 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg122 = data.readString();
                            long _arg218 = data.readLong();
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result33 = queryIntentServices(_arg033, _arg122, _arg218, _arg38);
                            reply.writeNoException();
                            reply.writeTypedObject(_result33, 1);
                            break;
                        case 35:
                            Intent _arg034 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg123 = data.readString();
                            long _arg219 = data.readLong();
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result34 = queryIntentContentProviders(_arg034, _arg123, _arg219, _arg39);
                            reply.writeNoException();
                            reply.writeTypedObject(_result34, 1);
                            break;
                        case 36:
                            long _arg035 = data.readLong();
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result35 = getInstalledPackages(_arg035, _arg124);
                            reply.writeNoException();
                            reply.writeTypedObject(_result35, 1);
                            break;
                        case 37:
                            String _arg036 = data.readString();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result36 = getAppMetadataFd(_arg036, _arg125);
                            reply.writeNoException();
                            reply.writeTypedObject(_result36, 1);
                            break;
                        case 38:
                            String[] _arg037 = data.createStringArray();
                            long _arg126 = data.readLong();
                            int _arg220 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result37 = getPackagesHoldingPermissions(_arg037, _arg126, _arg220);
                            reply.writeNoException();
                            reply.writeTypedObject(_result37, 1);
                            break;
                        case 39:
                            long _arg038 = data.readLong();
                            int _arg127 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result38 = getInstalledApplications(_arg038, _arg127);
                            reply.writeNoException();
                            reply.writeTypedObject(_result38, 1);
                            break;
                        case 40:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result39 = getPersistentApplications(_arg039);
                            reply.writeNoException();
                            reply.writeTypedObject(_result39, 1);
                            break;
                        case 41:
                            String _arg040 = data.readString();
                            long _arg128 = data.readLong();
                            int _arg221 = data.readInt();
                            data.enforceNoDataAvail();
                            ProviderInfo _result40 = resolveContentProvider(_arg040, _arg128, _arg221);
                            reply.writeNoException();
                            reply.writeTypedObject(_result40, 1);
                            break;
                        case 42:
                            List<String> _arg041 = data.createStringArrayList();
                            ArrayList createTypedArrayList = data.createTypedArrayList(ProviderInfo.CREATOR);
                            data.enforceNoDataAvail();
                            querySyncProviders(_arg041, createTypedArrayList);
                            reply.writeNoException();
                            reply.writeStringList(_arg041);
                            reply.writeTypedList(createTypedArrayList, 1);
                            break;
                        case 43:
                            String _arg042 = data.readString();
                            int _arg129 = data.readInt();
                            long _arg222 = data.readLong();
                            String _arg310 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result41 = queryContentProviders(_arg042, _arg129, _arg222, _arg310);
                            reply.writeNoException();
                            reply.writeTypedObject(_result41, 1);
                            break;
                        case 44:
                            ComponentName _arg043 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg130 = data.readInt();
                            int _arg223 = data.readInt();
                            data.enforceNoDataAvail();
                            InstrumentationInfo _result42 = getInstrumentationInfoAsUser(_arg043, _arg130, _arg223);
                            reply.writeNoException();
                            reply.writeTypedObject(_result42, 1);
                            break;
                        case 45:
                            String _arg044 = data.readString();
                            int _arg131 = data.readInt();
                            int _arg224 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result43 = queryInstrumentationAsUser(_arg044, _arg131, _arg224);
                            reply.writeNoException();
                            reply.writeTypedObject(_result43, 1);
                            break;
                        case 46:
                            int _arg045 = data.readInt();
                            boolean _arg132 = data.readBoolean();
                            data.enforceNoDataAvail();
                            finishPackageInstall(_arg045, _arg132);
                            reply.writeNoException();
                            break;
                        case 47:
                            String _arg046 = data.readString();
                            String _arg133 = data.readString();
                            data.enforceNoDataAvail();
                            setInstallerPackageName(_arg046, _arg133);
                            reply.writeNoException();
                            break;
                        case 48:
                            String _arg047 = data.readString();
                            data.enforceNoDataAvail();
                            relinquishUpdateOwnership(_arg047);
                            reply.writeNoException();
                            break;
                        case 49:
                            String _arg048 = data.readString();
                            int _arg134 = data.readInt();
                            String _arg225 = data.readString();
                            data.enforceNoDataAvail();
                            setApplicationCategoryHint(_arg048, _arg134, _arg225);
                            reply.writeNoException();
                            break;
                        case 50:
                            String _arg049 = data.readString();
                            int _arg135 = data.readInt();
                            IPackageDeleteObserver _arg226 = IPackageDeleteObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg311 = data.readInt();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            deletePackageAsUser(_arg049, _arg135, _arg226, _arg311, _arg42);
                            reply.writeNoException();
                            break;
                        case 51:
                            VersionedPackage _arg050 = (VersionedPackage) data.readTypedObject(VersionedPackage.CREATOR);
                            IPackageDeleteObserver2 _arg136 = IPackageDeleteObserver2.Stub.asInterface(data.readStrongBinder());
                            int _arg227 = data.readInt();
                            int _arg312 = data.readInt();
                            data.enforceNoDataAvail();
                            deletePackageVersioned(_arg050, _arg136, _arg227, _arg312);
                            reply.writeNoException();
                            break;
                        case 52:
                            VersionedPackage _arg051 = (VersionedPackage) data.readTypedObject(VersionedPackage.CREATOR);
                            IPackageDeleteObserver2 _arg137 = IPackageDeleteObserver2.Stub.asInterface(data.readStrongBinder());
                            int _arg228 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteExistingPackageAsUser(_arg051, _arg137, _arg228);
                            reply.writeNoException();
                            break;
                        case 53:
                            String _arg052 = data.readString();
                            data.enforceNoDataAvail();
                            String _result44 = getInstallerPackageName(_arg052);
                            reply.writeNoException();
                            reply.writeString(_result44);
                            break;
                        case 54:
                            String _arg053 = data.readString();
                            data.enforceNoDataAvail();
                            InstallSourceInfo _result45 = getInstallSourceInfo(_arg053);
                            reply.writeNoException();
                            reply.writeTypedObject(_result45, 1);
                            break;
                        case 55:
                            int _arg054 = data.readInt();
                            data.enforceNoDataAvail();
                            resetApplicationPreferences(_arg054);
                            reply.writeNoException();
                            break;
                        case 56:
                            Intent _arg055 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg138 = data.readString();
                            int _arg229 = data.readInt();
                            data.enforceNoDataAvail();
                            ResolveInfo _result46 = getLastChosenActivity(_arg055, _arg138, _arg229);
                            reply.writeNoException();
                            reply.writeTypedObject(_result46, 1);
                            break;
                        case 57:
                            Intent _arg056 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg139 = data.readString();
                            int _arg230 = data.readInt();
                            IntentFilter _arg313 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            int _arg43 = data.readInt();
                            ComponentName _arg52 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setLastChosenActivity(_arg056, _arg139, _arg230, _arg313, _arg43, _arg52);
                            reply.writeNoException();
                            break;
                        case 58:
                            IntentFilter _arg057 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            int _arg140 = data.readInt();
                            ComponentName[] _arg231 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                            ComponentName _arg314 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg44 = data.readInt();
                            boolean _arg53 = data.readBoolean();
                            data.enforceNoDataAvail();
                            addPreferredActivity(_arg057, _arg140, _arg231, _arg314, _arg44, _arg53);
                            reply.writeNoException();
                            break;
                        case 59:
                            IntentFilter _arg058 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            int _arg141 = data.readInt();
                            ComponentName[] _arg232 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                            ComponentName _arg315 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg45 = data.readInt();
                            data.enforceNoDataAvail();
                            replacePreferredActivity(_arg058, _arg141, _arg232, _arg315, _arg45);
                            reply.writeNoException();
                            break;
                        case 60:
                            String _arg059 = data.readString();
                            data.enforceNoDataAvail();
                            clearPackagePreferredActivities(_arg059);
                            reply.writeNoException();
                            break;
                        case 61:
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            String _arg233 = data.readString();
                            data.enforceNoDataAvail();
                            int _result47 = getPreferredActivities(arrayList, arrayList2, _arg233);
                            reply.writeNoException();
                            reply.writeInt(_result47);
                            reply.writeTypedList(arrayList, 1);
                            reply.writeTypedList(arrayList2, 1);
                            break;
                        case 62:
                            IntentFilter _arg060 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            ComponentName _arg142 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg234 = data.readInt();
                            data.enforceNoDataAvail();
                            addPersistentPreferredActivity(_arg060, _arg142, _arg234);
                            reply.writeNoException();
                            break;
                        case 63:
                            String _arg061 = data.readString();
                            int _arg143 = data.readInt();
                            data.enforceNoDataAvail();
                            clearPackagePersistentPreferredActivities(_arg061, _arg143);
                            reply.writeNoException();
                            break;
                        case 64:
                            IntentFilter _arg062 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            int _arg144 = data.readInt();
                            data.enforceNoDataAvail();
                            clearPersistentPreferredActivity(_arg062, _arg144);
                            reply.writeNoException();
                            break;
                        case 65:
                            IntentFilter _arg063 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            String _arg145 = data.readString();
                            int _arg235 = data.readInt();
                            int _arg316 = data.readInt();
                            int _arg46 = data.readInt();
                            data.enforceNoDataAvail();
                            addCrossProfileIntentFilter(_arg063, _arg145, _arg235, _arg316, _arg46);
                            reply.writeNoException();
                            break;
                        case 66:
                            IntentFilter _arg064 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
                            String _arg146 = data.readString();
                            int _arg236 = data.readInt();
                            int _arg317 = data.readInt();
                            int _arg47 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result48 = removeCrossProfileIntentFilter(_arg064, _arg146, _arg236, _arg317, _arg47);
                            reply.writeNoException();
                            reply.writeBoolean(_result48);
                            break;
                        case 67:
                            int _arg065 = data.readInt();
                            String _arg147 = data.readString();
                            data.enforceNoDataAvail();
                            clearCrossProfileIntentFilters(_arg065, _arg147);
                            reply.writeNoException();
                            break;
                        case 68:
                            String[] _arg066 = data.createStringArray();
                            int _arg148 = data.readInt();
                            int _arg237 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result49 = setDistractingPackageRestrictionsAsUser(_arg066, _arg148, _arg237);
                            reply.writeNoException();
                            reply.writeStringArray(_result49);
                            break;
                        case 69:
                            String[] _arg067 = data.createStringArray();
                            boolean _arg149 = data.readBoolean();
                            PersistableBundle _arg238 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            PersistableBundle _arg318 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            SuspendDialogInfo _arg48 = (SuspendDialogInfo) data.readTypedObject(SuspendDialogInfo.CREATOR);
                            String _arg54 = data.readString();
                            int _arg62 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result50 = setPackagesSuspendedAsUser(_arg067, _arg149, _arg238, _arg318, _arg48, _arg54, _arg62);
                            reply.writeNoException();
                            reply.writeStringArray(_result50);
                            break;
                        case 70:
                            String[] _arg068 = data.createStringArray();
                            int _arg150 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result51 = getUnsuspendablePackagesForUser(_arg068, _arg150);
                            reply.writeNoException();
                            reply.writeStringArray(_result51);
                            break;
                        case 71:
                            String _arg069 = data.readString();
                            int _arg151 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result52 = isPackageSuspendedForUser(_arg069, _arg151);
                            reply.writeNoException();
                            reply.writeBoolean(_result52);
                            break;
                        case 72:
                            String _arg070 = data.readString();
                            int _arg152 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result53 = getSuspendedPackageAppExtras(_arg070, _arg152);
                            reply.writeNoException();
                            reply.writeTypedObject(_result53, 1);
                            break;
                        case 73:
                            int _arg071 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result54 = getPreferredActivityBackup(_arg071);
                            reply.writeNoException();
                            reply.writeByteArray(_result54);
                            break;
                        case 74:
                            byte[] _arg072 = data.createByteArray();
                            int _arg153 = data.readInt();
                            data.enforceNoDataAvail();
                            restorePreferredActivities(_arg072, _arg153);
                            reply.writeNoException();
                            break;
                        case 75:
                            int _arg073 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result55 = getDefaultAppsBackup(_arg073);
                            reply.writeNoException();
                            reply.writeByteArray(_result55);
                            break;
                        case 76:
                            byte[] _arg074 = data.createByteArray();
                            int _arg154 = data.readInt();
                            data.enforceNoDataAvail();
                            restoreDefaultApps(_arg074, _arg154);
                            reply.writeNoException();
                            break;
                        case 77:
                            int _arg075 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result56 = getDomainVerificationBackup(_arg075);
                            reply.writeNoException();
                            reply.writeByteArray(_result56);
                            break;
                        case 78:
                            byte[] _arg076 = data.createByteArray();
                            int _arg155 = data.readInt();
                            data.enforceNoDataAvail();
                            restoreDomainVerification(_arg076, _arg155);
                            reply.writeNoException();
                            break;
                        case 79:
                            ArrayList arrayList3 = new ArrayList();
                            data.enforceNoDataAvail();
                            ComponentName _result57 = getHomeActivities(arrayList3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result57, 1);
                            reply.writeTypedList(arrayList3, 1);
                            break;
                        case 80:
                            ComponentName _arg077 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg156 = data.readInt();
                            data.enforceNoDataAvail();
                            setHomeActivity(_arg077, _arg156);
                            reply.writeNoException();
                            break;
                        case 81:
                            ComponentName _arg078 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg157 = data.readString();
                            int _arg239 = data.readInt();
                            int _arg319 = data.readInt();
                            data.enforceNoDataAvail();
                            overrideLabelAndIcon(_arg078, _arg157, _arg239, _arg319);
                            reply.writeNoException();
                            break;
                        case 82:
                            ComponentName _arg079 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg158 = data.readInt();
                            data.enforceNoDataAvail();
                            restoreLabelAndIcon(_arg079, _arg158);
                            reply.writeNoException();
                            break;
                        case 83:
                            ComponentName _arg080 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg159 = data.readInt();
                            int _arg240 = data.readInt();
                            int _arg320 = data.readInt();
                            data.enforceNoDataAvail();
                            setComponentEnabledSetting(_arg080, _arg159, _arg240, _arg320);
                            reply.writeNoException();
                            break;
                        case 84:
                            List<PackageManager.ComponentEnabledSetting> _arg081 = data.createTypedArrayList(PackageManager.ComponentEnabledSetting.CREATOR);
                            int _arg160 = data.readInt();
                            data.enforceNoDataAvail();
                            setComponentEnabledSettings(_arg081, _arg160);
                            reply.writeNoException();
                            break;
                        case 85:
                            ComponentName _arg082 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg161 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result58 = getComponentEnabledSetting(_arg082, _arg161);
                            reply.writeNoException();
                            reply.writeInt(_result58);
                            break;
                        case 86:
                            String _arg083 = data.readString();
                            int _arg162 = data.readInt();
                            int _arg241 = data.readInt();
                            int _arg321 = data.readInt();
                            String _arg49 = data.readString();
                            data.enforceNoDataAvail();
                            setApplicationEnabledSetting(_arg083, _arg162, _arg241, _arg321, _arg49);
                            reply.writeNoException();
                            break;
                        case 87:
                            String _arg084 = data.readString();
                            int _arg163 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result59 = getApplicationEnabledSetting(_arg084, _arg163);
                            reply.writeNoException();
                            reply.writeInt(_result59);
                            break;
                        case 88:
                            String _arg085 = data.readString();
                            String _arg164 = data.readString();
                            int _arg242 = data.readInt();
                            String _arg322 = data.readString();
                            String _arg410 = data.readString();
                            int _arg55 = data.readInt();
                            data.enforceNoDataAvail();
                            logAppProcessStartIfNeeded(_arg085, _arg164, _arg242, _arg322, _arg410, _arg55);
                            reply.writeNoException();
                            break;
                        case 89:
                            int _arg086 = data.readInt();
                            data.enforceNoDataAvail();
                            flushPackageRestrictionsAsUser(_arg086);
                            reply.writeNoException();
                            break;
                        case 90:
                            String _arg087 = data.readString();
                            boolean _arg165 = data.readBoolean();
                            int _arg243 = data.readInt();
                            data.enforceNoDataAvail();
                            setPackageStoppedState(_arg087, _arg165, _arg243);
                            reply.writeNoException();
                            break;
                        case 91:
                            String _arg088 = data.readString();
                            long _arg166 = data.readLong();
                            int _arg244 = data.readInt();
                            IPackageDataObserver _arg323 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            freeStorageAndNotify(_arg088, _arg166, _arg244, _arg323);
                            reply.writeNoException();
                            break;
                        case 92:
                            String _arg089 = data.readString();
                            long _arg167 = data.readLong();
                            int _arg245 = data.readInt();
                            IntentSender _arg324 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            freeStorage(_arg089, _arg167, _arg245, _arg324);
                            reply.writeNoException();
                            break;
                        case 93:
                            String _arg090 = data.readString();
                            IPackageDataObserver _arg168 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deleteApplicationCacheFiles(_arg090, _arg168);
                            reply.writeNoException();
                            break;
                        case 94:
                            String _arg091 = data.readString();
                            int _arg169 = data.readInt();
                            IPackageDataObserver _arg246 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deleteApplicationCacheFilesAsUser(_arg091, _arg169, _arg246);
                            reply.writeNoException();
                            break;
                        case 95:
                            String _arg092 = data.readString();
                            IPackageDataObserver _arg170 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg247 = data.readInt();
                            data.enforceNoDataAvail();
                            clearApplicationUserData(_arg092, _arg170, _arg247);
                            reply.writeNoException();
                            break;
                        case 96:
                            String _arg093 = data.readString();
                            data.enforceNoDataAvail();
                            clearApplicationProfileData(_arg093);
                            reply.writeNoException();
                            break;
                        case 97:
                            String _arg094 = data.readString();
                            int _arg171 = data.readInt();
                            IPackageStatsObserver _arg248 = IPackageStatsObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getPackageSizeInfo(_arg094, _arg171, _arg248);
                            reply.writeNoException();
                            break;
                        case 98:
                            String[] _result60 = getSystemSharedLibraryNames();
                            reply.writeNoException();
                            reply.writeStringArray(_result60);
                            break;
                        case 99:
                            ParceledListSlice _result61 = getSystemAvailableFeatures();
                            reply.writeNoException();
                            reply.writeTypedObject(_result61, 1);
                            break;
                        case 100:
                            String _arg095 = data.readString();
                            int _arg172 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result62 = hasSystemFeature(_arg095, _arg172);
                            reply.writeNoException();
                            reply.writeBoolean(_result62);
                            break;
                        case 101:
                            enterSafeMode();
                            reply.writeNoException();
                            break;
                        case 102:
                            boolean _result63 = isSafeMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result63);
                            break;
                        case 103:
                            boolean _result64 = hasSystemUidErrors();
                            reply.writeNoException();
                            reply.writeBoolean(_result64);
                            break;
                        case 104:
                            String _arg096 = data.readString();
                            int _arg173 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyPackageUse(_arg096, _arg173);
                            break;
                        case 105:
                            String _arg097 = data.readString();
                            int N = data.readInt();
                            final Map<String, String> _arg174 = N < 0 ? null : new HashMap<>();
                            IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.content.pm.IPackageManager$Stub$$ExternalSyntheticLambda0
                                @Override // java.util.function.IntConsumer
                                public final void accept(int i) {
                                    IPackageManager.Stub.lambda$onTransact$0(Parcel.this, _arg174, i);
                                }
                            });
                            String _arg249 = data.readString();
                            data.enforceNoDataAvail();
                            notifyDexLoad(_arg097, _arg174, _arg249);
                            break;
                        case 106:
                            String _arg098 = data.readString();
                            String _arg175 = data.readString();
                            boolean _arg250 = data.readBoolean();
                            IDexModuleRegisterCallback _arg325 = IDexModuleRegisterCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerDexModule(_arg098, _arg175, _arg250, _arg325);
                            break;
                        case 107:
                            String _arg099 = data.readString();
                            boolean _arg176 = data.readBoolean();
                            String _arg251 = data.readString();
                            boolean _arg326 = data.readBoolean();
                            boolean _arg411 = data.readBoolean();
                            String _arg56 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result65 = performDexOptMode(_arg099, _arg176, _arg251, _arg326, _arg411, _arg56);
                            reply.writeNoException();
                            reply.writeBoolean(_result65);
                            break;
                        case 108:
                            String _arg0100 = data.readString();
                            String _arg177 = data.readString();
                            boolean _arg252 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result66 = performDexOptSecondary(_arg0100, _arg177, _arg252);
                            reply.writeNoException();
                            reply.writeBoolean(_result66);
                            break;
                        case 109:
                            int _arg0101 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result67 = getMoveStatus(_arg0101);
                            reply.writeNoException();
                            reply.writeInt(_result67);
                            break;
                        case 110:
                            IPackageMoveObserver _arg0102 = IPackageMoveObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerMoveCallback(_arg0102);
                            reply.writeNoException();
                            break;
                        case 111:
                            IPackageMoveObserver _arg0103 = IPackageMoveObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterMoveCallback(_arg0103);
                            reply.writeNoException();
                            break;
                        case 112:
                            String _arg0104 = data.readString();
                            String _arg178 = data.readString();
                            data.enforceNoDataAvail();
                            int _result68 = movePackage(_arg0104, _arg178);
                            reply.writeNoException();
                            reply.writeInt(_result68);
                            break;
                        case 113:
                            String _arg0105 = data.readString();
                            data.enforceNoDataAvail();
                            int _result69 = movePrimaryStorage(_arg0105);
                            reply.writeNoException();
                            reply.writeInt(_result69);
                            break;
                        case 114:
                            int _arg0106 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result70 = setInstallLocation(_arg0106);
                            reply.writeNoException();
                            reply.writeBoolean(_result70);
                            break;
                        case 115:
                            int _result71 = getInstallLocation();
                            reply.writeNoException();
                            reply.writeInt(_result71);
                            break;
                        case 116:
                            String _arg0107 = data.readString();
                            int _arg179 = data.readInt();
                            int _arg253 = data.readInt();
                            int _arg327 = data.readInt();
                            List<String> _arg412 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            int _result72 = installExistingPackageAsUser(_arg0107, _arg179, _arg253, _arg327, _arg412);
                            reply.writeNoException();
                            reply.writeInt(_result72);
                            break;
                        case 117:
                            int _arg0108 = data.readInt();
                            int _arg180 = data.readInt();
                            data.enforceNoDataAvail();
                            verifyPendingInstall(_arg0108, _arg180);
                            reply.writeNoException();
                            break;
                        case 118:
                            int _arg0109 = data.readInt();
                            int _arg181 = data.readInt();
                            long _arg254 = data.readLong();
                            data.enforceNoDataAvail();
                            extendVerificationTimeout(_arg0109, _arg181, _arg254);
                            reply.writeNoException();
                            break;
                        case 119:
                            int _arg0110 = data.readInt();
                            int _arg182 = data.readInt();
                            List<String> _arg255 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            verifyIntentFilter(_arg0110, _arg182, _arg255);
                            reply.writeNoException();
                            break;
                        case 120:
                            String _arg0111 = data.readString();
                            int _arg183 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result73 = getIntentVerificationStatus(_arg0111, _arg183);
                            reply.writeNoException();
                            reply.writeInt(_result73);
                            break;
                        case 121:
                            String _arg0112 = data.readString();
                            int _arg184 = data.readInt();
                            int _arg256 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result74 = updateIntentVerificationStatus(_arg0112, _arg184, _arg256);
                            reply.writeNoException();
                            reply.writeBoolean(_result74);
                            break;
                        case 122:
                            String _arg0113 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result75 = getIntentFilterVerifications(_arg0113);
                            reply.writeNoException();
                            reply.writeTypedObject(_result75, 1);
                            break;
                        case 123:
                            String _arg0114 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result76 = getAllIntentFilters(_arg0114);
                            reply.writeNoException();
                            reply.writeTypedObject(_result76, 1);
                            break;
                        case 124:
                            VerifierDeviceIdentity _result77 = getVerifierDeviceIdentity();
                            reply.writeNoException();
                            reply.writeTypedObject(_result77, 1);
                            break;
                        case 125:
                            boolean _result78 = isFirstBoot();
                            reply.writeNoException();
                            reply.writeBoolean(_result78);
                            break;
                        case 126:
                            boolean _result79 = isDeviceUpgrading();
                            reply.writeNoException();
                            reply.writeBoolean(_result79);
                            break;
                        case 127:
                            boolean _result80 = isStorageLow();
                            reply.writeNoException();
                            reply.writeBoolean(_result80);
                            break;
                        case 128:
                            String _arg0115 = data.readString();
                            boolean _arg185 = data.readBoolean();
                            int _arg257 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result81 = setApplicationHiddenSettingAsUser(_arg0115, _arg185, _arg257);
                            reply.writeNoException();
                            reply.writeBoolean(_result81);
                            break;
                        case 129:
                            String _arg0116 = data.readString();
                            int _arg186 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result82 = getApplicationHiddenSettingAsUser(_arg0116, _arg186);
                            reply.writeNoException();
                            reply.writeBoolean(_result82);
                            break;
                        case 130:
                            String _arg0117 = data.readString();
                            boolean _arg187 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSystemAppHiddenUntilInstalled(_arg0117, _arg187);
                            reply.writeNoException();
                            break;
                        case 131:
                            String _arg0118 = data.readString();
                            boolean _arg188 = data.readBoolean();
                            int _arg258 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result83 = setSystemAppInstallState(_arg0118, _arg188, _arg258);
                            reply.writeNoException();
                            reply.writeBoolean(_result83);
                            break;
                        case 132:
                            IPackageInstaller _result84 = getPackageInstaller();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result84);
                            break;
                        case 133:
                            String _arg0119 = data.readString();
                            boolean _arg189 = data.readBoolean();
                            int _arg259 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result85 = setBlockUninstallForUser(_arg0119, _arg189, _arg259);
                            reply.writeNoException();
                            reply.writeBoolean(_result85);
                            break;
                        case 134:
                            String _arg0120 = data.readString();
                            int _arg190 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result86 = getBlockUninstallForUser(_arg0120, _arg190);
                            reply.writeNoException();
                            reply.writeBoolean(_result86);
                            break;
                        case 135:
                            String _arg0121 = data.readString();
                            String _arg191 = data.readString();
                            data.enforceNoDataAvail();
                            KeySet _result87 = getKeySetByAlias(_arg0121, _arg191);
                            reply.writeNoException();
                            reply.writeTypedObject(_result87, 1);
                            break;
                        case 136:
                            String _arg0122 = data.readString();
                            data.enforceNoDataAvail();
                            KeySet _result88 = getSigningKeySet(_arg0122);
                            reply.writeNoException();
                            reply.writeTypedObject(_result88, 1);
                            break;
                        case 137:
                            String _arg0123 = data.readString();
                            KeySet _arg192 = (KeySet) data.readTypedObject(KeySet.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result89 = isPackageSignedByKeySet(_arg0123, _arg192);
                            reply.writeNoException();
                            reply.writeBoolean(_result89);
                            break;
                        case 138:
                            String _arg0124 = data.readString();
                            KeySet _arg193 = (KeySet) data.readTypedObject(KeySet.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result90 = isPackageSignedByKeySetExactly(_arg0124, _arg193);
                            reply.writeNoException();
                            reply.writeBoolean(_result90);
                            break;
                        case 139:
                            String _result91 = getPermissionControllerPackageName();
                            reply.writeNoException();
                            reply.writeString(_result91);
                            break;
                        case 140:
                            String _result92 = getSdkSandboxPackageName();
                            reply.writeNoException();
                            reply.writeString(_result92);
                            break;
                        case 141:
                            int _arg0125 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result93 = getInstantApps(_arg0125);
                            reply.writeNoException();
                            reply.writeTypedObject(_result93, 1);
                            break;
                        case 142:
                            String _arg0126 = data.readString();
                            int _arg194 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result94 = getInstantAppCookie(_arg0126, _arg194);
                            reply.writeNoException();
                            reply.writeByteArray(_result94);
                            break;
                        case 143:
                            String _arg0127 = data.readString();
                            byte[] _arg195 = data.createByteArray();
                            int _arg260 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result95 = setInstantAppCookie(_arg0127, _arg195, _arg260);
                            reply.writeNoException();
                            reply.writeBoolean(_result95);
                            break;
                        case 144:
                            String _arg0128 = data.readString();
                            int _arg196 = data.readInt();
                            data.enforceNoDataAvail();
                            Bitmap _result96 = getInstantAppIcon(_arg0128, _arg196);
                            reply.writeNoException();
                            reply.writeTypedObject(_result96, 1);
                            break;
                        case 145:
                            String _arg0129 = data.readString();
                            int _arg197 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result97 = isInstantApp(_arg0129, _arg197);
                            reply.writeNoException();
                            reply.writeBoolean(_result97);
                            break;
                        case 146:
                            String _arg0130 = data.readString();
                            boolean _arg198 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result98 = setRequiredForSystemUser(_arg0130, _arg198);
                            reply.writeNoException();
                            reply.writeBoolean(_result98);
                            break;
                        case 147:
                            String _arg0131 = data.readString();
                            boolean _arg199 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUpdateAvailable(_arg0131, _arg199);
                            reply.writeNoException();
                            break;
                        case 148:
                            String _result99 = getServicesSystemSharedLibraryPackageName();
                            reply.writeNoException();
                            reply.writeString(_result99);
                            break;
                        case 149:
                            String _result100 = getSharedSystemSharedLibraryPackageName();
                            reply.writeNoException();
                            reply.writeString(_result100);
                            break;
                        case 150:
                            int _arg0132 = data.readInt();
                            int _arg1100 = data.readInt();
                            data.enforceNoDataAvail();
                            ChangedPackages _result101 = getChangedPackages(_arg0132, _arg1100);
                            reply.writeNoException();
                            reply.writeTypedObject(_result101, 1);
                            break;
                        case 151:
                            String _arg0133 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result102 = isPackageDeviceAdminOnAnyUser(_arg0133);
                            reply.writeNoException();
                            reply.writeBoolean(_result102);
                            break;
                        case 152:
                            String _arg0134 = data.readString();
                            int _arg1101 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result103 = getInstallReason(_arg0134, _arg1101);
                            reply.writeNoException();
                            reply.writeInt(_result103);
                            break;
                        case 153:
                            String _arg0135 = data.readString();
                            long _arg1102 = data.readLong();
                            int _arg261 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result104 = getSharedLibraries(_arg0135, _arg1102, _arg261);
                            reply.writeNoException();
                            reply.writeTypedObject(_result104, 1);
                            break;
                        case 154:
                            String _arg0136 = data.readString();
                            long _arg1103 = data.readLong();
                            int _arg262 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result105 = getDeclaredSharedLibraries(_arg0136, _arg1103, _arg262);
                            reply.writeNoException();
                            reply.writeTypedObject(_result105, 1);
                            break;
                        case 155:
                            String _arg0137 = data.readString();
                            int _arg1104 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result106 = canRequestPackageInstalls(_arg0137, _arg1104);
                            reply.writeNoException();
                            reply.writeBoolean(_result106);
                            break;
                        case 156:
                            deletePreloadsFileCache();
                            reply.writeNoException();
                            break;
                        case 157:
                            ComponentName _result107 = getInstantAppResolverComponent();
                            reply.writeNoException();
                            reply.writeTypedObject(_result107, 1);
                            break;
                        case 158:
                            ComponentName _result108 = getInstantAppResolverSettingsComponent();
                            reply.writeNoException();
                            reply.writeTypedObject(_result108, 1);
                            break;
                        case 159:
                            ComponentName _result109 = getInstantAppInstallerComponent();
                            reply.writeNoException();
                            reply.writeTypedObject(_result109, 1);
                            break;
                        case 160:
                            String _arg0138 = data.readString();
                            int _arg1105 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result110 = getInstantAppAndroidId(_arg0138, _arg1105);
                            reply.writeNoException();
                            reply.writeString(_result110);
                            break;
                        case 161:
                            IArtManager _result111 = getArtManager();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result111);
                            break;
                        case 162:
                            String _arg0139 = data.readString();
                            CharSequence _arg1106 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg263 = data.readInt();
                            data.enforceNoDataAvail();
                            setHarmfulAppWarning(_arg0139, _arg1106, _arg263);
                            reply.writeNoException();
                            break;
                        case 163:
                            String _arg0140 = data.readString();
                            int _arg1107 = data.readInt();
                            data.enforceNoDataAvail();
                            CharSequence _result112 = getHarmfulAppWarning(_arg0140, _arg1107);
                            reply.writeNoException();
                            if (_result112 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result112, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 164:
                            String _arg0141 = data.readString();
                            byte[] _arg1108 = data.createByteArray();
                            int _arg264 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result113 = hasSigningCertificate(_arg0141, _arg1108, _arg264);
                            reply.writeNoException();
                            reply.writeBoolean(_result113);
                            break;
                        case 165:
                            int _arg0142 = data.readInt();
                            byte[] _arg1109 = data.createByteArray();
                            int _arg265 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result114 = hasUidSigningCertificate(_arg0142, _arg1109, _arg265);
                            reply.writeNoException();
                            reply.writeBoolean(_result114);
                            break;
                        case 166:
                            String _result115 = getDefaultTextClassifierPackageName();
                            reply.writeNoException();
                            reply.writeString(_result115);
                            break;
                        case 167:
                            String _result116 = getSystemTextClassifierPackageName();
                            reply.writeNoException();
                            reply.writeString(_result116);
                            break;
                        case 168:
                            String _result117 = getAttentionServicePackageName();
                            reply.writeNoException();
                            reply.writeString(_result117);
                            break;
                        case 169:
                            String _result118 = getRotationResolverPackageName();
                            reply.writeNoException();
                            reply.writeString(_result118);
                            break;
                        case 170:
                            String _result119 = getWellbeingPackageName();
                            reply.writeNoException();
                            reply.writeString(_result119);
                            break;
                        case 171:
                            String _result120 = getAppPredictionServicePackageName();
                            reply.writeNoException();
                            reply.writeString(_result120);
                            break;
                        case 172:
                            String _result121 = getSystemCaptionsServicePackageName();
                            reply.writeNoException();
                            reply.writeString(_result121);
                            break;
                        case 173:
                            String _result122 = getSetupWizardPackageName();
                            reply.writeNoException();
                            reply.writeString(_result122);
                            break;
                        case 174:
                            String _result123 = getIncidentReportApproverPackageName();
                            reply.writeNoException();
                            reply.writeString(_result123);
                            break;
                        case 175:
                            String _arg0143 = data.readString();
                            int _arg1110 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result124 = isPackageStateProtected(_arg0143, _arg1110);
                            reply.writeNoException();
                            reply.writeBoolean(_result124);
                            break;
                        case 176:
                            sendDeviceCustomizationReadyBroadcast();
                            reply.writeNoException();
                            break;
                        case 177:
                            int _arg0144 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ModuleInfo> _result125 = getInstalledModules(_arg0144);
                            reply.writeNoException();
                            reply.writeTypedList(_result125, 1);
                            break;
                        case 178:
                            String _arg0145 = data.readString();
                            int _arg1111 = data.readInt();
                            data.enforceNoDataAvail();
                            ModuleInfo _result126 = getModuleInfo(_arg0145, _arg1111);
                            reply.writeNoException();
                            reply.writeTypedObject(_result126, 1);
                            break;
                        case 179:
                            int _arg0146 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result127 = getRuntimePermissionsVersion(_arg0146);
                            reply.writeNoException();
                            reply.writeInt(_result127);
                            break;
                        case 180:
                            int _arg0147 = data.readInt();
                            int _arg1112 = data.readInt();
                            data.enforceNoDataAvail();
                            setRuntimePermissionsVersion(_arg0147, _arg1112);
                            reply.writeNoException();
                            break;
                        case 181:
                            String[] _arg0148 = data.createStringArray();
                            data.enforceNoDataAvail();
                            notifyPackagesReplacedReceived(_arg0148);
                            reply.writeNoException();
                            break;
                        case 182:
                            String _arg0149 = data.readString();
                            boolean _arg1113 = data.readBoolean();
                            int _arg266 = data.readInt();
                            int _arg328 = data.readInt();
                            ClassLoader cl = getClass().getClassLoader();
                            List _arg413 = data.readArrayList(cl);
                            IOnChecksumsReadyListener _arg57 = IOnChecksumsReadyListener.Stub.asInterface(data.readStrongBinder());
                            int _arg63 = data.readInt();
                            data.enforceNoDataAvail();
                            requestPackageChecksums(_arg0149, _arg1113, _arg266, _arg328, _arg413, _arg57, _arg63);
                            reply.writeNoException();
                            break;
                        case 183:
                            String _arg0150 = data.readString();
                            String _arg1114 = data.readString();
                            String _arg267 = data.readString();
                            int _arg329 = data.readInt();
                            data.enforceNoDataAvail();
                            IntentSender _result128 = getLaunchIntentSenderForPackage(_arg0150, _arg1114, _arg267, _arg329);
                            reply.writeNoException();
                            reply.writeTypedObject(_result128, 1);
                            break;
                        case 184:
                            String _arg0151 = data.readString();
                            int _arg1115 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result129 = getAppOpPermissionPackages(_arg0151, _arg1115);
                            reply.writeNoException();
                            reply.writeStringArray(_result129);
                            break;
                        case 185:
                            String _arg0152 = data.readString();
                            int _arg1116 = data.readInt();
                            data.enforceNoDataAvail();
                            PermissionGroupInfo _result130 = getPermissionGroupInfo(_arg0152, _arg1116);
                            reply.writeNoException();
                            reply.writeTypedObject(_result130, 1);
                            break;
                        case 186:
                            PermissionInfo _arg0153 = (PermissionInfo) data.readTypedObject(PermissionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result131 = addPermission(_arg0153);
                            reply.writeNoException();
                            reply.writeBoolean(_result131);
                            break;
                        case 187:
                            PermissionInfo _arg0154 = (PermissionInfo) data.readTypedObject(PermissionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result132 = addPermissionAsync(_arg0154);
                            reply.writeNoException();
                            reply.writeBoolean(_result132);
                            break;
                        case 188:
                            String _arg0155 = data.readString();
                            data.enforceNoDataAvail();
                            removePermission(_arg0155);
                            reply.writeNoException();
                            break;
                        case 189:
                            String _arg0156 = data.readString();
                            String _arg1117 = data.readString();
                            int _arg268 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result133 = checkPermission(_arg0156, _arg1117, _arg268);
                            reply.writeNoException();
                            reply.writeInt(_result133);
                            break;
                        case 190:
                            String _arg0157 = data.readString();
                            String _arg1118 = data.readString();
                            int _arg269 = data.readInt();
                            data.enforceNoDataAvail();
                            grantRuntimePermission(_arg0157, _arg1118, _arg269);
                            reply.writeNoException();
                            break;
                        case 191:
                            String _arg0158 = data.readString();
                            int _arg1119 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result134 = checkUidPermission(_arg0158, _arg1119);
                            reply.writeNoException();
                            reply.writeInt(_result134);
                            break;
                        case 192:
                            String _arg0159 = data.readString();
                            String _arg1120 = data.readString();
                            List<String> _arg270 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setMimeGroup(_arg0159, _arg1120, _arg270);
                            reply.writeNoException();
                            break;
                        case 193:
                            String _arg0160 = data.readString();
                            int _arg1121 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result135 = getSplashScreenTheme(_arg0160, _arg1121);
                            reply.writeNoException();
                            reply.writeString(_result135);
                            break;
                        case 194:
                            String _arg0161 = data.readString();
                            String _arg1122 = data.readString();
                            int _arg271 = data.readInt();
                            data.enforceNoDataAvail();
                            setSplashScreenTheme(_arg0161, _arg1122, _arg271);
                            reply.writeNoException();
                            break;
                        case 195:
                            String _arg0162 = data.readString();
                            String _arg1123 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result136 = getMimeGroup(_arg0162, _arg1123);
                            reply.writeNoException();
                            reply.writeStringList(_result136);
                            break;
                        case 196:
                            String _arg0163 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result137 = isAutoRevokeWhitelisted(_arg0163);
                            reply.writeNoException();
                            reply.writeBoolean(_result137);
                            break;
                        case 197:
                            int _arg0164 = data.readInt();
                            String _arg1124 = data.readString();
                            data.enforceNoDataAvail();
                            makeProviderVisible(_arg0164, _arg1124);
                            reply.writeNoException();
                            break;
                        case 198:
                            int _arg0165 = data.readInt();
                            int _arg1125 = data.readInt();
                            data.enforceNoDataAvail();
                            makeUidVisible(_arg0165, _arg1125);
                            reply.writeNoException();
                            break;
                        case 199:
                            IBinder _result138 = getHoldLockToken();
                            reply.writeNoException();
                            reply.writeStrongBinder(_result138);
                            break;
                        case 200:
                            IBinder _arg0166 = data.readStrongBinder();
                            int _arg1126 = data.readInt();
                            data.enforceNoDataAvail();
                            holdLock(_arg0166, _arg1126);
                            reply.writeNoException();
                            break;
                        case 201:
                            String _arg0167 = data.readString();
                            String _arg1127 = data.readString();
                            String _arg272 = data.readString();
                            int _arg330 = data.readInt();
                            data.enforceNoDataAvail();
                            PackageManager.Property _result139 = getPropertyAsUser(_arg0167, _arg1127, _arg272, _arg330);
                            reply.writeNoException();
                            reply.writeTypedObject(_result139, 1);
                            break;
                        case 202:
                            String _arg0168 = data.readString();
                            int _arg1128 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result140 = queryProperty(_arg0168, _arg1128);
                            reply.writeNoException();
                            reply.writeTypedObject(_result140, 1);
                            break;
                        case 203:
                            List<String> _arg0169 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setKeepUninstalledPackages(_arg0169);
                            reply.writeNoException();
                            break;
                        case 204:
                            String _arg0170 = data.readString();
                            String[] _arg1129 = data.createStringArray();
                            int _arg273 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean[] _result141 = canPackageQuery(_arg0170, _arg1129, _arg273);
                            reply.writeNoException();
                            reply.writeBooleanArray(_result141);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel data, Map _arg1, int i) {
            String k = data.readString();
            String v = data.readString();
            _arg1.put(k, v);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.content.pm.IPackageManager$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IPackageManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IPackageManager
            public void checkPackageStartable(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageAvailable(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public PackageInfo getPackageInfo(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    PackageInfo _result = (PackageInfo) _reply.readTypedObject(PackageInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public PackageInfo getPackageInfoVersioned(VersionedPackage versionedPackage, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(versionedPackage, 0);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    PackageInfo _result = (PackageInfo) _reply.readTypedObject(PackageInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getPackageUid(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int[] getPackageGids(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] currentToCanonicalPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] canonicalToCurrentPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ApplicationInfo getApplicationInfo(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    ApplicationInfo _result = (ApplicationInfo) _reply.readTypedObject(ApplicationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getTargetSdkVersion(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ActivityInfo getActivityInfo(ComponentName className, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    ActivityInfo _result = (ActivityInfo) _reply.readTypedObject(ActivityInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean activitySupportsIntentAsUser(ComponentName className, Intent intent, String resolvedType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ActivityInfo getReceiverInfo(ComponentName className, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    ActivityInfo _result = (ActivityInfo) _reply.readTypedObject(ActivityInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ServiceInfo getServiceInfo(ComponentName className, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    ServiceInfo _result = (ServiceInfo) _reply.readTypedObject(ServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ProviderInfo getProviderInfo(ComponentName className, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    ProviderInfo _result = (ProviderInfo) _reply.readTypedObject(ProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isProtectedBroadcast(String actionName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(actionName);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int checkSignatures(String pkg1, String pkg2, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg1);
                    _data.writeString(pkg2);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int checkUidSignatures(int uid1, int uid2) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid1);
                    _data.writeInt(uid2);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public List<String> getAllPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] getPackagesForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getNameForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] getNamesForUids(int[] uids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(uids);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getUidForSharedUser(String sharedUserName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sharedUserName);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getFlagsForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getPrivateFlagsForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isUidPrivileged(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ResolveInfo resolveIntent(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    ResolveInfo _result = (ResolveInfo) _reply.readTypedObject(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ResolveInfo findPersistentPreferredActivity(Intent intent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    ResolveInfo _result = (ResolveInfo) _reply.readTypedObject(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean canForwardTo(Intent intent, String resolvedType, int sourceUserId, int targetUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(sourceUserId);
                    _data.writeInt(targetUserId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryIntentActivities(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(caller, 0);
                    _data.writeTypedArray(specifics, 0);
                    _data.writeStringArray(specificTypes);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryIntentReceivers(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ResolveInfo resolveService(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    ResolveInfo _result = (ResolveInfo) _reply.readTypedObject(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryIntentServices(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryIntentContentProviders(Intent intent, String resolvedType, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getInstalledPackages(long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParcelFileDescriptor getAppMetadataFd(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getPackagesHoldingPermissions(String[] permissions, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(permissions);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getInstalledApplications(long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getPersistentApplications(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ProviderInfo resolveContentProvider(String name, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    ProviderInfo _result = (ProviderInfo) _reply.readTypedObject(ProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(outNames);
                    _data.writeTypedList(outInfo, 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    _reply.readStringList(outNames);
                    _reply.readTypedList(outInfo, ProviderInfo.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryContentProviders(String processName, int uid, long flags, String metaDataKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    _data.writeLong(flags);
                    _data.writeString(metaDataKey);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public InstrumentationInfo getInstrumentationInfoAsUser(ComponentName className, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    InstrumentationInfo _result = (InstrumentationInfo) _reply.readTypedObject(InstrumentationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryInstrumentationAsUser(String targetPackage, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void finishPackageInstall(int token, boolean didLaunch) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeBoolean(didLaunch);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setInstallerPackageName(String targetPackage, String installerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeString(installerPackageName);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void relinquishUpdateOwnership(String targetPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setApplicationCategoryHint(String packageName, int categoryHint, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(categoryHint);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deletePackageAsUser(String packageName, int versionCode, IPackageDeleteObserver observer, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(versionCode);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deletePackageVersioned(VersionedPackage versionedPackage, IPackageDeleteObserver2 observer, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(versionedPackage, 0);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deleteExistingPackageAsUser(VersionedPackage versionedPackage, IPackageDeleteObserver2 observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(versionedPackage, 0);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getInstallerPackageName(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public InstallSourceInfo getInstallSourceInfo(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    InstallSourceInfo _result = (InstallSourceInfo) _reply.readTypedObject(InstallSourceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void resetApplicationPreferences(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    ResolveInfo _result = (ResolveInfo) _reply.readTypedObject(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeTypedObject(filter, 0);
                    _data.writeInt(match);
                    _data.writeTypedObject(activity, 0);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId, boolean removeExisting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(filter, 0);
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    _data.writeTypedObject(activity, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(removeExisting);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(filter, 0);
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    _data.writeTypedObject(activity, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearPackagePreferredActivities(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readTypedList(outFilters, IntentFilter.CREATOR);
                    _reply.readTypedList(outActivities, ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void addPersistentPreferredActivity(IntentFilter filter, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(filter, 0);
                    _data.writeTypedObject(activity, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearPackagePersistentPreferredActivities(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearPersistentPreferredActivity(IntentFilter filter, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(filter, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void addCrossProfileIntentFilter(IntentFilter intentFilter, String ownerPackage, int sourceUserId, int targetUserId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intentFilter, 0);
                    _data.writeString(ownerPackage);
                    _data.writeInt(sourceUserId);
                    _data.writeInt(targetUserId);
                    _data.writeInt(flags);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean removeCrossProfileIntentFilter(IntentFilter intentFilter, String ownerPackage, int sourceUserId, int targetUserId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intentFilter, 0);
                    _data.writeString(ownerPackage);
                    _data.writeInt(sourceUserId);
                    _data.writeInt(targetUserId);
                    _data.writeInt(flags);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearCrossProfileIntentFilters(int sourceUserId, String ownerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sourceUserId);
                    _data.writeString(ownerPackage);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] setDistractingPackageRestrictionsAsUser(String[] packageNames, int restrictionFlags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(restrictionFlags);
                    _data.writeInt(userId);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] setPackagesSuspendedAsUser(String[] packageNames, boolean suspended, PersistableBundle appExtras, PersistableBundle launcherExtras, SuspendDialogInfo dialogInfo, String callingPackage, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeBoolean(suspended);
                    _data.writeTypedObject(appExtras, 0);
                    _data.writeTypedObject(launcherExtras, 0);
                    _data.writeTypedObject(dialogInfo, 0);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] getUnsuspendablePackagesForUser(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageSuspendedForUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public Bundle getSuspendedPackageAppExtras(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public byte[] getPreferredActivityBackup(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void restorePreferredActivities(byte[] backup, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(backup);
                    _data.writeInt(userId);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public byte[] getDefaultAppsBackup(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void restoreDefaultApps(byte[] backup, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(backup);
                    _data.writeInt(userId);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public byte[] getDomainVerificationBackup(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void restoreDomainVerification(byte[] backup, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(backup);
                    _data.writeInt(userId);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ComponentName getHomeActivities(List<ResolveInfo> outHomeCandidates) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    _reply.readTypedList(outHomeCandidates, ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setHomeActivity(ComponentName className, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(className, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void overrideLabelAndIcon(ComponentName componentName, String nonLocalizedLabel, int icon, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeString(nonLocalizedLabel);
                    _data.writeInt(icon);
                    _data.writeInt(userId);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void restoreLabelAndIcon(ComponentName componentName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setComponentEnabledSettings(List<PackageManager.ComponentEnabledSetting> settings, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(settings, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getComponentEnabledSetting(ComponentName componentName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getApplicationEnabledSetting(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void logAppProcessStartIfNeeded(String packageName, String processName, int uid, String seinfo, String apkFile, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    _data.writeString(seinfo);
                    _data.writeString(apkFile);
                    _data.writeInt(pid);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void flushPackageRestrictionsAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setPackageStoppedState(String packageName, boolean stopped, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(stopped);
                    _data.writeInt(userId);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void freeStorageAndNotify(String volumeUuid, long freeStorageSize, int storageFlags, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeLong(freeStorageSize);
                    _data.writeInt(storageFlags);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void freeStorage(String volumeUuid, long freeStorageSize, int storageFlags, IntentSender pi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeLong(freeStorageSize);
                    _data.writeInt(storageFlags);
                    _data.writeTypedObject(pi, 0);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deleteApplicationCacheFilesAsUser(String packageName, int userId, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userId);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void clearApplicationProfileData(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] getSystemSharedLibraryNames() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getSystemAvailableFeatures() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean hasSystemFeature(String name, int version) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(version);
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void enterSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean hasSystemUidErrors() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void notifyPackageUse(String packageName, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(reason);
                    this.mRemote.transact(104, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void notifyDexLoad(String loadingPackageName, Map<String, String> classLoaderContextMap, String loaderIsa) throws RemoteException {
                final Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(loadingPackageName);
                    if (classLoaderContextMap == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(classLoaderContextMap.size());
                        classLoaderContextMap.forEach(new BiConsumer() { // from class: android.content.pm.IPackageManager$Stub$Proxy$$ExternalSyntheticLambda0
                            @Override // java.util.function.BiConsumer
                            public final void accept(Object obj, Object obj2) {
                                IPackageManager.Stub.Proxy.lambda$notifyDexLoad$0(Parcel.this, (String) obj, (String) obj2);
                            }
                        });
                    }
                    _data.writeString(loaderIsa);
                    this.mRemote.transact(105, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$notifyDexLoad$0(Parcel _data, String k, String v) {
                _data.writeString(k);
                _data.writeString(v);
            }

            @Override // android.content.p001pm.IPackageManager
            public void registerDexModule(String packageName, String dexModulePath, boolean isSharedModule, IDexModuleRegisterCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(dexModulePath);
                    _data.writeBoolean(isSharedModule);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(106, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean performDexOptMode(String packageName, boolean checkProfiles, String targetCompilerFilter, boolean force, boolean bootComplete, String splitName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(checkProfiles);
                    _data.writeString(targetCompilerFilter);
                    _data.writeBoolean(force);
                    _data.writeBoolean(bootComplete);
                    _data.writeString(splitName);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean performDexOptSecondary(String packageName, String targetCompilerFilter, boolean force) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(targetCompilerFilter);
                    _data.writeBoolean(force);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getMoveStatus(int moveId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(moveId);
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void registerMoveCallback(IPackageMoveObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void unregisterMoveCallback(IPackageMoveObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int movePackage(String packageName, String volumeUuid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(volumeUuid);
                    this.mRemote.transact(112, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int movePrimaryStorage(String volumeUuid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setInstallLocation(int loc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(loc);
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getInstallLocation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(115, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int installExistingPackageAsUser(String packageName, int userId, int installFlags, int installReason, List<String> whiteListedPermissions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeInt(installFlags);
                    _data.writeInt(installReason);
                    _data.writeStringList(whiteListedPermissions);
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void verifyPendingInstall(int id, int verificationCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCode);
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCodeAtTimeout);
                    _data.writeLong(millisecondsToDelay);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void verifyIntentFilter(int id, int verificationCode, List<String> failedDomains) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCode);
                    _data.writeStringList(failedDomains);
                    this.mRemote.transact(119, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getIntentVerificationStatus(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean updateIntentVerificationStatus(String packageName, int status, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(status);
                    _data.writeInt(userId);
                    this.mRemote.transact(121, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getIntentFilterVerifications(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(122, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getAllIntentFilters(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(123, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(124, _data, _reply, 0);
                    _reply.readException();
                    VerifierDeviceIdentity _result = (VerifierDeviceIdentity) _reply.readTypedObject(VerifierDeviceIdentity.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isFirstBoot() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(125, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isDeviceUpgrading() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(126, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isStorageLow() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setApplicationHiddenSettingAsUser(String packageName, boolean hidden, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(hidden);
                    _data.writeInt(userId);
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean getApplicationHiddenSettingAsUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(129, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setSystemAppHiddenUntilInstalled(String packageName, boolean hidden) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(hidden);
                    this.mRemote.transact(130, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setSystemAppInstallState(String packageName, boolean installed, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(installed);
                    _data.writeInt(userId);
                    this.mRemote.transact(131, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public IPackageInstaller getPackageInstaller() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(132, _data, _reply, 0);
                    _reply.readException();
                    IPackageInstaller _result = IPackageInstaller.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setBlockUninstallForUser(String packageName, boolean blockUninstall, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(blockUninstall);
                    _data.writeInt(userId);
                    this.mRemote.transact(133, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean getBlockUninstallForUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(134, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public KeySet getKeySetByAlias(String packageName, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(alias);
                    this.mRemote.transact(135, _data, _reply, 0);
                    _reply.readException();
                    KeySet _result = (KeySet) _reply.readTypedObject(KeySet.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public KeySet getSigningKeySet(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(136, _data, _reply, 0);
                    _reply.readException();
                    KeySet _result = (KeySet) _reply.readTypedObject(KeySet.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageSignedByKeySet(String packageName, KeySet ks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(ks, 0);
                    this.mRemote.transact(137, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageSignedByKeySetExactly(String packageName, KeySet ks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(ks, 0);
                    this.mRemote.transact(138, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getPermissionControllerPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(139, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSdkSandboxPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(140, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getInstantApps(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(141, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public byte[] getInstantAppCookie(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(142, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setInstantAppCookie(String packageName, byte[] cookie, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeByteArray(cookie);
                    _data.writeInt(userId);
                    this.mRemote.transact(143, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public Bitmap getInstantAppIcon(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(144, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isInstantApp(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(145, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean setRequiredForSystemUser(String packageName, boolean systemUserApp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(systemUserApp);
                    this.mRemote.transact(146, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setUpdateAvailable(String packageName, boolean updateAvaialble) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(updateAvaialble);
                    this.mRemote.transact(147, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getServicesSystemSharedLibraryPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(148, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSharedSystemSharedLibraryPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(149, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ChangedPackages getChangedPackages(int sequenceNumber, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequenceNumber);
                    _data.writeInt(userId);
                    this.mRemote.transact(150, _data, _reply, 0);
                    _reply.readException();
                    ChangedPackages _result = (ChangedPackages) _reply.readTypedObject(ChangedPackages.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageDeviceAdminOnAnyUser(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(151, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getInstallReason(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(152, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getSharedLibraries(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(153, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice getDeclaredSharedLibraries(String packageName, long flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(154, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean canRequestPackageInstalls(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(155, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void deletePreloadsFileCache() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(156, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ComponentName getInstantAppResolverComponent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(157, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ComponentName getInstantAppResolverSettingsComponent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(158, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ComponentName getInstantAppInstallerComponent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(159, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getInstantAppAndroidId(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(160, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public IArtManager getArtManager() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(161, _data, _reply, 0);
                    _reply.readException();
                    IArtManager _result = IArtManager.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setHarmfulAppWarning(String packageName, CharSequence warning, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (warning != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(warning, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(162, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public CharSequence getHarmfulAppWarning(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(163, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean hasSigningCertificate(String packageName, byte[] signingCertificate, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeByteArray(signingCertificate);
                    _data.writeInt(flags);
                    this.mRemote.transact(164, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean hasUidSigningCertificate(int uid, byte[] signingCertificate, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeByteArray(signingCertificate);
                    _data.writeInt(flags);
                    this.mRemote.transact(165, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getDefaultTextClassifierPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(166, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSystemTextClassifierPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(167, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getAttentionServicePackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(168, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getRotationResolverPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(169, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getWellbeingPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(170, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getAppPredictionServicePackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(171, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSystemCaptionsServicePackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(172, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSetupWizardPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(173, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getIncidentReportApproverPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(174, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isPackageStateProtected(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(175, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void sendDeviceCustomizationReadyBroadcast() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(176, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public List<ModuleInfo> getInstalledModules(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(177, _data, _reply, 0);
                    _reply.readException();
                    List<ModuleInfo> _result = _reply.createTypedArrayList(ModuleInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ModuleInfo getModuleInfo(String packageName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    this.mRemote.transact(178, _data, _reply, 0);
                    _reply.readException();
                    ModuleInfo _result = (ModuleInfo) _reply.readTypedObject(ModuleInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int getRuntimePermissionsVersion(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(179, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setRuntimePermissionsVersion(int version, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(version);
                    _data.writeInt(userId);
                    this.mRemote.transact(180, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void notifyPackagesReplacedReceived(String[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(packages);
                    this.mRemote.transact(181, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void requestPackageChecksums(String packageName, boolean includeSplits, int optional, int required, List trustedInstallers, IOnChecksumsReadyListener onChecksumsReadyListener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(includeSplits);
                    _data.writeInt(optional);
                    _data.writeInt(required);
                    _data.writeList(trustedInstallers);
                    _data.writeStrongInterface(onChecksumsReadyListener);
                    _data.writeInt(userId);
                    this.mRemote.transact(182, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public IntentSender getLaunchIntentSenderForPackage(String packageName, String callingPackage, String featureId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(callingPackage);
                    _data.writeString(featureId);
                    _data.writeInt(userId);
                    this.mRemote.transact(183, _data, _reply, 0);
                    _reply.readException();
                    IntentSender _result = (IntentSender) _reply.readTypedObject(IntentSender.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String[] getAppOpPermissionPackages(String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(184, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(185, _data, _reply, 0);
                    _reply.readException();
                    PermissionGroupInfo _result = (PermissionGroupInfo) _reply.readTypedObject(PermissionGroupInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean addPermission(PermissionInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(186, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean addPermissionAsync(PermissionInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(187, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void removePermission(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(188, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int checkPermission(String permName, String pkgName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeString(pkgName);
                    _data.writeInt(userId);
                    this.mRemote.transact(189, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void grantRuntimePermission(String packageName, String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(190, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public int checkUidPermission(String permName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeInt(uid);
                    this.mRemote.transact(191, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setMimeGroup(String packageName, String group, List<String> mimeTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(group);
                    _data.writeStringList(mimeTypes);
                    this.mRemote.transact(192, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public String getSplashScreenTheme(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(193, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setSplashScreenTheme(String packageName, String themeName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(themeName);
                    _data.writeInt(userId);
                    this.mRemote.transact(194, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public List<String> getMimeGroup(String packageName, String group) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(group);
                    this.mRemote.transact(195, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean isAutoRevokeWhitelisted(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(196, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void makeProviderVisible(int recipientAppId, String visibleAuthority) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recipientAppId);
                    _data.writeString(visibleAuthority);
                    this.mRemote.transact(197, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void makeUidVisible(int recipientAppId, int visibleUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recipientAppId);
                    _data.writeInt(visibleUid);
                    this.mRemote.transact(198, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public IBinder getHoldLockToken() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(199, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void holdLock(IBinder token, int durationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(durationMs);
                    this.mRemote.transact(200, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public PackageManager.Property getPropertyAsUser(String propertyName, String packageName, String className, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(propertyName);
                    _data.writeString(packageName);
                    _data.writeString(className);
                    _data.writeInt(userId);
                    this.mRemote.transact(201, _data, _reply, 0);
                    _reply.readException();
                    PackageManager.Property _result = (PackageManager.Property) _reply.readTypedObject(PackageManager.Property.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public ParceledListSlice queryProperty(String propertyName, int componentType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(propertyName);
                    _data.writeInt(componentType);
                    this.mRemote.transact(202, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public void setKeepUninstalledPackages(List<String> packageList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(packageList);
                    this.mRemote.transact(203, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IPackageManager
            public boolean[] canPackageQuery(String sourcePackageName, String[] targetPackageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sourcePackageName);
                    _data.writeStringArray(targetPackageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(204, _data, _reply, 0);
                    _reply.readException();
                    boolean[] _result = _reply.createBooleanArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 203;
        }
    }
}
