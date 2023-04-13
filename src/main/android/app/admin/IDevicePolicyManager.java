package android.app.admin;

import android.accounts.Account;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.admin.StartInstallingUpdateCallback;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p001pm.IPackageDataObserver;
import android.content.p001pm.ParceledListSlice;
import android.content.p001pm.StringParceledListSlice;
import android.graphics.Bitmap;
import android.net.ProxyInfo;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keystore.ParcelableKeyGenParameterSpec;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import com.android.internal.infra.AndroidFuture;
import java.util.List;
/* loaded from: classes.dex */
public interface IDevicePolicyManager extends IInterface {
    void acknowledgeDeviceCompliant() throws RemoteException;

    void acknowledgeNewUserDisclaimer(int i) throws RemoteException;

    void addCrossProfileIntentFilter(ComponentName componentName, String str, IntentFilter intentFilter, int i) throws RemoteException;

    boolean addCrossProfileWidgetProvider(ComponentName componentName, String str, String str2) throws RemoteException;

    int addOverrideApn(ComponentName componentName, ApnSetting apnSetting) throws RemoteException;

    void addPersistentPreferredActivity(ComponentName componentName, String str, IntentFilter intentFilter, ComponentName componentName2) throws RemoteException;

    boolean approveCaCert(String str, int i, boolean z) throws RemoteException;

    boolean bindDeviceAdminServiceAsUser(ComponentName componentName, IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, IServiceConnection iServiceConnection, long j, int i) throws RemoteException;

    boolean canAdminGrantSensorsPermissions() throws RemoteException;

    boolean canProfileOwnerResetPasswordWhenLocked(int i) throws RemoteException;

    boolean canUsbDataSignalingBeDisabled() throws RemoteException;

    boolean checkDeviceIdentifierAccess(String str, int i, int i2) throws RemoteException;

    int checkProvisioningPrecondition(String str, String str2) throws RemoteException;

    void choosePrivateKeyAlias(int i, Uri uri, String str, IBinder iBinder) throws RemoteException;

    void clearApplicationUserData(ComponentName componentName, String str, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void clearCrossProfileIntentFilters(ComponentName componentName, String str) throws RemoteException;

    void clearDeviceOwner(String str) throws RemoteException;

    void clearOrganizationIdForUser(int i) throws RemoteException;

    void clearPackagePersistentPreferredActivities(ComponentName componentName, String str, String str2) throws RemoteException;

    void clearProfileOwner(ComponentName componentName) throws RemoteException;

    boolean clearResetPasswordToken(ComponentName componentName, String str) throws RemoteException;

    void clearSystemUpdatePolicyFreezePeriodRecord() throws RemoteException;

    Intent createAdminSupportIntent(String str) throws RemoteException;

    UserHandle createAndManageUser(ComponentName componentName, String str, ComponentName componentName2, PersistableBundle persistableBundle, int i) throws RemoteException;

    UserHandle createAndProvisionManagedProfile(ManagedProfileProvisioningParams managedProfileProvisioningParams, String str) throws RemoteException;

    void enableSystemApp(ComponentName componentName, String str, String str2) throws RemoteException;

    int enableSystemAppWithIntent(ComponentName componentName, String str, Intent intent) throws RemoteException;

    void enforceCanManageCaCerts(ComponentName componentName, String str) throws RemoteException;

    void finalizeWorkProfileProvisioning(UserHandle userHandle, Account account) throws RemoteException;

    long forceNetworkLogs() throws RemoteException;

    void forceRemoveActiveAdmin(ComponentName componentName, int i) throws RemoteException;

    long forceSecurityLogs() throws RemoteException;

    void forceUpdateUserSetupComplete(int i) throws RemoteException;

    boolean generateKeyPair(ComponentName componentName, String str, String str2, ParcelableKeyGenParameterSpec parcelableKeyGenParameterSpec, int i, KeymasterCertificateChain keymasterCertificateChain) throws RemoteException;

    String[] getAccountTypesWithManagementDisabled(String str) throws RemoteException;

    String[] getAccountTypesWithManagementDisabledAsUser(int i, String str, boolean z) throws RemoteException;

    List<ComponentName> getActiveAdmins(int i) throws RemoteException;

    List<String> getAffiliationIds(ComponentName componentName) throws RemoteException;

    int getAggregatedPasswordComplexityForUser(int i, boolean z) throws RemoteException;

    List<String> getAllCrossProfilePackages() throws RemoteException;

    List<String> getAlwaysOnVpnLockdownAllowlist(ComponentName componentName) throws RemoteException;

    String getAlwaysOnVpnPackage(ComponentName componentName) throws RemoteException;

    String getAlwaysOnVpnPackageForUser(int i) throws RemoteException;

    int[] getApplicationExemptions(String str) throws RemoteException;

    Bundle getApplicationRestrictions(ComponentName componentName, String str, String str2) throws RemoteException;

    String getApplicationRestrictionsManagingPackage(ComponentName componentName) throws RemoteException;

    boolean getAutoTimeEnabled(ComponentName componentName, String str) throws RemoteException;

    boolean getAutoTimeRequired() throws RemoteException;

    boolean getAutoTimeZoneEnabled(ComponentName componentName, String str) throws RemoteException;

    List<UserHandle> getBindDeviceAdminTargetUsers(ComponentName componentName) throws RemoteException;

    boolean getBluetoothContactSharingDisabled(ComponentName componentName) throws RemoteException;

    boolean getBluetoothContactSharingDisabledForUser(int i) throws RemoteException;

    boolean getCameraDisabled(ComponentName componentName, String str, int i, boolean z) throws RemoteException;

    String getCertInstallerPackage(ComponentName componentName) throws RemoteException;

    PackagePolicy getCredentialManagerPolicy() throws RemoteException;

    List<String> getCrossProfileCalendarPackages(ComponentName componentName) throws RemoteException;

    List<String> getCrossProfileCalendarPackagesForUser(int i) throws RemoteException;

    boolean getCrossProfileCallerIdDisabled(ComponentName componentName) throws RemoteException;

    boolean getCrossProfileCallerIdDisabledForUser(int i) throws RemoteException;

    boolean getCrossProfileContactsSearchDisabled(ComponentName componentName) throws RemoteException;

    boolean getCrossProfileContactsSearchDisabledForUser(int i) throws RemoteException;

    List<String> getCrossProfilePackages(ComponentName componentName) throws RemoteException;

    List<String> getCrossProfileWidgetProviders(ComponentName componentName, String str) throws RemoteException;

    int getCurrentFailedPasswordAttempts(String str, int i, boolean z) throws RemoteException;

    List<String> getDefaultCrossProfilePackages() throws RemoteException;

    List<String> getDelegatePackages(ComponentName componentName, String str) throws RemoteException;

    List<String> getDelegatedScopes(ComponentName componentName, String str) throws RemoteException;

    ComponentName getDeviceOwnerComponent(boolean z) throws RemoteException;

    CharSequence getDeviceOwnerLockScreenInfo() throws RemoteException;

    String getDeviceOwnerName() throws RemoteException;

    CharSequence getDeviceOwnerOrganizationName() throws RemoteException;

    int getDeviceOwnerType(ComponentName componentName) throws RemoteException;

    int getDeviceOwnerUserId() throws RemoteException;

    DevicePolicyState getDevicePolicyState() throws RemoteException;

    List<String> getDisallowedSystemApps(ComponentName componentName, int i, String str) throws RemoteException;

    boolean getDoNotAskCredentialsOnBoot() throws RemoteException;

    ParcelableResource getDrawable(String str, String str2, String str3) throws RemoteException;

    CharSequence getEndUserSessionMessage(ComponentName componentName) throws RemoteException;

    Bundle getEnforcingAdminAndUserDetails(int i, String str) throws RemoteException;

    String getEnrollmentSpecificId(String str) throws RemoteException;

    FactoryResetProtectionPolicy getFactoryResetProtectionPolicy(ComponentName componentName) throws RemoteException;

    boolean getForceEphemeralUsers(ComponentName componentName) throws RemoteException;

    String getGlobalPrivateDnsHost(ComponentName componentName) throws RemoteException;

    int getGlobalPrivateDnsMode(ComponentName componentName) throws RemoteException;

    ComponentName getGlobalProxyAdmin(int i) throws RemoteException;

    List<String> getKeepUninstalledPackages(ComponentName componentName, String str) throws RemoteException;

    ParcelableGranteeMap getKeyPairGrants(String str, String str2) throws RemoteException;

    int getKeyguardDisabledFeatures(ComponentName componentName, int i, boolean z) throws RemoteException;

    long getLastBugReportRequestTime() throws RemoteException;

    long getLastNetworkLogRetrievalTime() throws RemoteException;

    long getLastSecurityLogRetrievalTime() throws RemoteException;

    int getLockTaskFeatures(ComponentName componentName, String str) throws RemoteException;

    String[] getLockTaskPackages(ComponentName componentName, String str) throws RemoteException;

    int getLogoutUserId() throws RemoteException;

    CharSequence getLongSupportMessage(ComponentName componentName) throws RemoteException;

    CharSequence getLongSupportMessageForUser(ComponentName componentName, int i) throws RemoteException;

    PackagePolicy getManagedProfileCallerIdAccessPolicy() throws RemoteException;

    PackagePolicy getManagedProfileContactsAccessPolicy() throws RemoteException;

    long getManagedProfileMaximumTimeOff(ComponentName componentName) throws RemoteException;

    ManagedSubscriptionsPolicy getManagedSubscriptionsPolicy() throws RemoteException;

    int getMaximumFailedPasswordsForWipe(ComponentName componentName, int i, boolean z) throws RemoteException;

    long getMaximumTimeToLock(ComponentName componentName, int i, boolean z) throws RemoteException;

    List<String> getMeteredDataDisabledPackages(ComponentName componentName) throws RemoteException;

    int getMinimumRequiredWifiSecurityLevel() throws RemoteException;

    int getMtePolicy(String str) throws RemoteException;

    int getNearbyAppStreamingPolicy(int i) throws RemoteException;

    int getNearbyNotificationStreamingPolicy(int i) throws RemoteException;

    int getOrganizationColor(ComponentName componentName) throws RemoteException;

    int getOrganizationColorForUser(int i) throws RemoteException;

    CharSequence getOrganizationName(ComponentName componentName, String str) throws RemoteException;

    CharSequence getOrganizationNameForUser(int i) throws RemoteException;

    List<ApnSetting> getOverrideApns(ComponentName componentName) throws RemoteException;

    StringParceledListSlice getOwnerInstalledCaCerts(UserHandle userHandle) throws RemoteException;

    int getPasswordComplexity(boolean z) throws RemoteException;

    long getPasswordExpiration(ComponentName componentName, int i, boolean z) throws RemoteException;

    long getPasswordExpirationTimeout(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordHistoryLength(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumLength(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumLetters(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumLowerCase(ComponentName componentName, int i, boolean z) throws RemoteException;

    PasswordMetrics getPasswordMinimumMetrics(int i, boolean z) throws RemoteException;

    int getPasswordMinimumNonLetter(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumNumeric(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumSymbols(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordMinimumUpperCase(ComponentName componentName, int i, boolean z) throws RemoteException;

    int getPasswordQuality(ComponentName componentName, int i, boolean z) throws RemoteException;

    SystemUpdateInfo getPendingSystemUpdate(ComponentName componentName) throws RemoteException;

    int getPermissionGrantState(ComponentName componentName, String str, String str2, String str3) throws RemoteException;

    int getPermissionPolicy(ComponentName componentName) throws RemoteException;

    List<String> getPermittedAccessibilityServices(ComponentName componentName) throws RemoteException;

    List<String> getPermittedAccessibilityServicesForUser(int i) throws RemoteException;

    List<String> getPermittedCrossProfileNotificationListeners(ComponentName componentName) throws RemoteException;

    List<String> getPermittedInputMethods(ComponentName componentName, String str, boolean z) throws RemoteException;

    List<String> getPermittedInputMethodsAsUser(int i) throws RemoteException;

    int getPersonalAppsSuspendedReasons(ComponentName componentName) throws RemoteException;

    List<UserHandle> getPolicyManagedProfiles(UserHandle userHandle) throws RemoteException;

    List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs() throws RemoteException;

    ComponentName getProfileOwnerAsUser(int i) throws RemoteException;

    String getProfileOwnerName(int i) throws RemoteException;

    ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle userHandle) throws RemoteException;

    int getProfileWithMinimumFailedPasswordsForWipe(int i, boolean z) throws RemoteException;

    void getRemoveWarning(ComponentName componentName, RemoteCallback remoteCallback, int i) throws RemoteException;

    int getRequiredPasswordComplexity(String str, boolean z) throws RemoteException;

    long getRequiredStrongAuthTimeout(ComponentName componentName, int i, boolean z) throws RemoteException;

    ComponentName getRestrictionsProvider(int i) throws RemoteException;

    boolean getScreenCaptureDisabled(ComponentName componentName, int i, boolean z) throws RemoteException;

    List<UserHandle> getSecondaryUsers(ComponentName componentName) throws RemoteException;

    CharSequence getShortSupportMessage(ComponentName componentName, String str) throws RemoteException;

    CharSequence getShortSupportMessageForUser(ComponentName componentName, int i) throws RemoteException;

    CharSequence getStartUserSessionMessage(ComponentName componentName) throws RemoteException;

    boolean getStorageEncryption(ComponentName componentName, int i) throws RemoteException;

    int getStorageEncryptionStatus(String str, int i) throws RemoteException;

    ParcelableResource getString(String str) throws RemoteException;

    SystemUpdatePolicy getSystemUpdatePolicy() throws RemoteException;

    PersistableBundle getTransferOwnershipBundle() throws RemoteException;

    List<PersistableBundle> getTrustAgentConfiguration(ComponentName componentName, ComponentName componentName2, int i, boolean z) throws RemoteException;

    List<String> getUserControlDisabledPackages(ComponentName componentName, String str) throws RemoteException;

    int getUserProvisioningState(int i) throws RemoteException;

    Bundle getUserRestrictions(ComponentName componentName, String str, boolean z) throws RemoteException;

    Bundle getUserRestrictionsGlobally(String str) throws RemoteException;

    String getWifiMacAddress(ComponentName componentName, String str) throws RemoteException;

    WifiSsidPolicy getWifiSsidPolicy(String str) throws RemoteException;

    boolean hasDeviceOwner() throws RemoteException;

    boolean hasGrantedPolicy(ComponentName componentName, int i, int i2) throws RemoteException;

    boolean hasKeyPair(String str, String str2) throws RemoteException;

    boolean hasLockdownAdminConfiguredNetworks(ComponentName componentName) throws RemoteException;

    boolean hasManagedProfileCallerIdAccess(int i, String str) throws RemoteException;

    boolean hasManagedProfileContactsAccess(int i, String str) throws RemoteException;

    boolean hasUserSetupCompleted() throws RemoteException;

    boolean installCaCert(ComponentName componentName, String str, byte[] bArr) throws RemoteException;

    boolean installExistingPackage(ComponentName componentName, String str, String str2) throws RemoteException;

    boolean installKeyPair(ComponentName componentName, String str, byte[] bArr, byte[] bArr2, byte[] bArr3, String str2, boolean z, boolean z2) throws RemoteException;

    void installUpdateFromFile(ComponentName componentName, String str, ParcelFileDescriptor parcelFileDescriptor, StartInstallingUpdateCallback startInstallingUpdateCallback) throws RemoteException;

    boolean isAccessibilityServicePermittedByAdmin(ComponentName componentName, String str, int i) throws RemoteException;

    boolean isActivePasswordSufficient(String str, int i, boolean z) throws RemoteException;

    boolean isActivePasswordSufficientForDeviceRequirement() throws RemoteException;

    boolean isAdminActive(ComponentName componentName, int i) throws RemoteException;

    boolean isAffiliatedUser(int i) throws RemoteException;

    boolean isAlwaysOnVpnLockdownEnabled(ComponentName componentName) throws RemoteException;

    boolean isAlwaysOnVpnLockdownEnabledForUser(int i) throws RemoteException;

    boolean isApplicationHidden(ComponentName componentName, String str, String str2, boolean z) throws RemoteException;

    boolean isBackupServiceEnabled(ComponentName componentName) throws RemoteException;

    boolean isCaCertApproved(String str, int i) throws RemoteException;

    boolean isCallerApplicationRestrictionsManagingPackage(String str) throws RemoteException;

    boolean isCallingUserAffiliated() throws RemoteException;

    boolean isCommonCriteriaModeEnabled(ComponentName componentName) throws RemoteException;

    boolean isComplianceAcknowledgementRequired() throws RemoteException;

    boolean isCurrentInputMethodSetByOwner() throws RemoteException;

    boolean isDeviceProvisioned() throws RemoteException;

    boolean isDeviceProvisioningConfigApplied() throws RemoteException;

    boolean isDpcDownloaded() throws RemoteException;

    boolean isEphemeralUser(ComponentName componentName) throws RemoteException;

    boolean isFactoryResetProtectionPolicySupported() throws RemoteException;

    boolean isInputMethodPermittedByAdmin(ComponentName componentName, String str, int i, boolean z) throws RemoteException;

    boolean isKeyPairGrantedToWifiAuth(String str, String str2) throws RemoteException;

    boolean isLockTaskPermitted(String str) throws RemoteException;

    boolean isLogoutEnabled() throws RemoteException;

    boolean isManagedKiosk() throws RemoteException;

    boolean isManagedProfile(ComponentName componentName) throws RemoteException;

    boolean isMasterVolumeMuted(ComponentName componentName) throws RemoteException;

    boolean isMeteredDataDisabledPackageForUser(ComponentName componentName, String str, int i) throws RemoteException;

    boolean isNetworkLoggingEnabled(ComponentName componentName, String str) throws RemoteException;

    boolean isNewUserDisclaimerAcknowledged(int i) throws RemoteException;

    boolean isNotificationListenerServicePermitted(String str, int i) throws RemoteException;

    boolean isOrganizationOwnedDeviceWithManagedProfile() throws RemoteException;

    boolean isOverrideApnEnabled(ComponentName componentName) throws RemoteException;

    boolean isPackageAllowedToAccessCalendarForUser(String str, int i) throws RemoteException;

    boolean isPackageSuspended(ComponentName componentName, String str, String str2) throws RemoteException;

    boolean isPasswordSufficientAfterProfileUnification(int i, int i2) throws RemoteException;

    boolean isProvisioningAllowed(String str, String str2) throws RemoteException;

    boolean isRemovingAdmin(ComponentName componentName, int i) throws RemoteException;

    boolean isResetPasswordTokenActive(ComponentName componentName, String str) throws RemoteException;

    boolean isSafeOperation(int i) throws RemoteException;

    boolean isSecondaryLockscreenEnabled(UserHandle userHandle) throws RemoteException;

    boolean isSecurityLoggingEnabled(ComponentName componentName, String str) throws RemoteException;

    boolean isStatusBarDisabled(String str) throws RemoteException;

    boolean isSupervisionComponent(ComponentName componentName) throws RemoteException;

    boolean isUnattendedManagedKiosk() throws RemoteException;

    boolean isUninstallBlocked(String str) throws RemoteException;

    boolean isUninstallInQueue(String str) throws RemoteException;

    boolean isUsbDataSignalingEnabled(String str) throws RemoteException;

    boolean isUsbDataSignalingEnabledForUser(int i) throws RemoteException;

    boolean isUsingUnifiedPassword(ComponentName componentName) throws RemoteException;

    List<UserHandle> listForegroundAffiliatedUsers() throws RemoteException;

    List<String> listPolicyExemptApps() throws RemoteException;

    void lockNow(int i, boolean z) throws RemoteException;

    int logoutUser(ComponentName componentName) throws RemoteException;

    int logoutUserInternal() throws RemoteException;

    void notifyLockTaskModeChanged(boolean z, String str, int i) throws RemoteException;

    void notifyPendingSystemUpdate(SystemUpdateInfo systemUpdateInfo) throws RemoteException;

    boolean packageHasActiveAdmins(String str, int i) throws RemoteException;

    void provisionFullyManagedDevice(FullyManagedDeviceProvisioningParams fullyManagedDeviceProvisioningParams, String str) throws RemoteException;

    void reboot(ComponentName componentName) throws RemoteException;

    void removeActiveAdmin(ComponentName componentName, int i) throws RemoteException;

    boolean removeCrossProfileWidgetProvider(ComponentName componentName, String str, String str2) throws RemoteException;

    boolean removeKeyPair(ComponentName componentName, String str, String str2) throws RemoteException;

    boolean removeOverrideApn(ComponentName componentName, int i) throws RemoteException;

    boolean removeUser(ComponentName componentName, UserHandle userHandle) throws RemoteException;

    void reportFailedBiometricAttempt(int i) throws RemoteException;

    void reportFailedPasswordAttempt(int i, boolean z) throws RemoteException;

    void reportKeyguardDismissed(int i) throws RemoteException;

    void reportKeyguardSecured(int i) throws RemoteException;

    void reportPasswordChanged(PasswordMetrics passwordMetrics, int i) throws RemoteException;

    void reportSuccessfulBiometricAttempt(int i) throws RemoteException;

    void reportSuccessfulPasswordAttempt(int i) throws RemoteException;

    boolean requestBugreport(ComponentName componentName) throws RemoteException;

    void resetDefaultCrossProfileIntentFilters(int i) throws RemoteException;

    void resetDrawables(List<String> list) throws RemoteException;

    boolean resetPassword(String str, int i) throws RemoteException;

    boolean resetPasswordWithToken(ComponentName componentName, String str, String str2, byte[] bArr, int i) throws RemoteException;

    /* renamed from: resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
    void mo197xce7fef36() throws RemoteException;

    void resetStrings(List<String> list) throws RemoteException;

    List<NetworkEvent> retrieveNetworkLogs(ComponentName componentName, String str, long j) throws RemoteException;

    ParceledListSlice retrievePreRebootSecurityLogs(ComponentName componentName, String str) throws RemoteException;

    ParceledListSlice retrieveSecurityLogs(ComponentName componentName, String str) throws RemoteException;

    void sendLostModeLocationUpdate(AndroidFuture<Boolean> androidFuture) throws RemoteException;

    void setAccountManagementDisabled(ComponentName componentName, String str, String str2, boolean z, boolean z2) throws RemoteException;

    void setActiveAdmin(ComponentName componentName, boolean z, int i) throws RemoteException;

    void setAffiliationIds(ComponentName componentName, List<String> list) throws RemoteException;

    boolean setAlwaysOnVpnPackage(ComponentName componentName, String str, boolean z, List<String> list) throws RemoteException;

    void setApplicationExemptions(String str, int[] iArr) throws RemoteException;

    boolean setApplicationHidden(ComponentName componentName, String str, String str2, boolean z, boolean z2) throws RemoteException;

    void setApplicationRestrictions(ComponentName componentName, String str, String str2, Bundle bundle) throws RemoteException;

    boolean setApplicationRestrictionsManagingPackage(ComponentName componentName, String str) throws RemoteException;

    void setAutoTimeEnabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setAutoTimeRequired(ComponentName componentName, boolean z) throws RemoteException;

    void setAutoTimeZoneEnabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setBackupServiceEnabled(ComponentName componentName, boolean z) throws RemoteException;

    void setBluetoothContactSharingDisabled(ComponentName componentName, boolean z) throws RemoteException;

    void setCameraDisabled(ComponentName componentName, String str, boolean z, boolean z2) throws RemoteException;

    void setCertInstallerPackage(ComponentName componentName, String str) throws RemoteException;

    void setCommonCriteriaModeEnabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setConfiguredNetworksLockdownState(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setCredentialManagerPolicy(PackagePolicy packagePolicy) throws RemoteException;

    void setCrossProfileCalendarPackages(ComponentName componentName, List<String> list) throws RemoteException;

    void setCrossProfileCallerIdDisabled(ComponentName componentName, boolean z) throws RemoteException;

    void setCrossProfileContactsSearchDisabled(ComponentName componentName, boolean z) throws RemoteException;

    void setCrossProfilePackages(ComponentName componentName, List<String> list) throws RemoteException;

    void setDefaultDialerApplication(String str) throws RemoteException;

    void setDefaultSmsApplication(ComponentName componentName, String str, String str2, boolean z) throws RemoteException;

    void setDelegatedScopes(ComponentName componentName, String str, List<String> list) throws RemoteException;

    boolean setDeviceOwner(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setDeviceOwnerLockScreenInfo(ComponentName componentName, CharSequence charSequence) throws RemoteException;

    void setDeviceOwnerType(ComponentName componentName, int i) throws RemoteException;

    void setDeviceProvisioningConfigApplied() throws RemoteException;

    void setDpcDownloaded(boolean z) throws RemoteException;

    void setDrawables(List<DevicePolicyDrawableResource> list) throws RemoteException;

    void setEndUserSessionMessage(ComponentName componentName, CharSequence charSequence) throws RemoteException;

    void setFactoryResetProtectionPolicy(ComponentName componentName, String str, FactoryResetProtectionPolicy factoryResetProtectionPolicy) throws RemoteException;

    void setForceEphemeralUsers(ComponentName componentName, boolean z) throws RemoteException;

    int setGlobalPrivateDns(ComponentName componentName, int i, String str) throws RemoteException;

    ComponentName setGlobalProxy(ComponentName componentName, String str, String str2) throws RemoteException;

    void setGlobalSetting(ComponentName componentName, String str, String str2) throws RemoteException;

    void setKeepUninstalledPackages(ComponentName componentName, String str, List<String> list) throws RemoteException;

    boolean setKeyGrantForApp(ComponentName componentName, String str, String str2, String str3, boolean z) throws RemoteException;

    boolean setKeyGrantToWifiAuth(String str, String str2, boolean z) throws RemoteException;

    boolean setKeyPairCertificate(ComponentName componentName, String str, String str2, byte[] bArr, byte[] bArr2, boolean z) throws RemoteException;

    boolean setKeyguardDisabled(ComponentName componentName, boolean z) throws RemoteException;

    void setKeyguardDisabledFeatures(ComponentName componentName, String str, int i, boolean z) throws RemoteException;

    void setLocationEnabled(ComponentName componentName, boolean z) throws RemoteException;

    void setLockTaskFeatures(ComponentName componentName, String str, int i) throws RemoteException;

    void setLockTaskPackages(ComponentName componentName, String str, String[] strArr) throws RemoteException;

    void setLogoutEnabled(ComponentName componentName, boolean z) throws RemoteException;

    void setLongSupportMessage(ComponentName componentName, CharSequence charSequence) throws RemoteException;

    void setManagedProfileCallerIdAccessPolicy(PackagePolicy packagePolicy) throws RemoteException;

    void setManagedProfileContactsAccessPolicy(PackagePolicy packagePolicy) throws RemoteException;

    void setManagedProfileMaximumTimeOff(ComponentName componentName, long j) throws RemoteException;

    void setManagedSubscriptionsPolicy(ManagedSubscriptionsPolicy managedSubscriptionsPolicy) throws RemoteException;

    void setMasterVolumeMuted(ComponentName componentName, boolean z) throws RemoteException;

    void setMaximumFailedPasswordsForWipe(ComponentName componentName, String str, int i, boolean z) throws RemoteException;

    void setMaximumTimeToLock(ComponentName componentName, String str, long j, boolean z) throws RemoteException;

    List<String> setMeteredDataDisabledPackages(ComponentName componentName, List<String> list) throws RemoteException;

    void setMinimumRequiredWifiSecurityLevel(String str, int i) throws RemoteException;

    void setMtePolicy(int i, String str) throws RemoteException;

    void setNearbyAppStreamingPolicy(int i) throws RemoteException;

    void setNearbyNotificationStreamingPolicy(int i) throws RemoteException;

    void setNetworkLoggingEnabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setNextOperationSafety(int i, int i2) throws RemoteException;

    void setOrganizationColor(ComponentName componentName, int i) throws RemoteException;

    void setOrganizationColorForUser(int i, int i2) throws RemoteException;

    void setOrganizationIdForUser(String str, String str2, int i) throws RemoteException;

    void setOrganizationName(ComponentName componentName, String str, CharSequence charSequence) throws RemoteException;

    void setOverrideApnsEnabled(ComponentName componentName, boolean z) throws RemoteException;

    void setOverrideKeepProfilesRunning(boolean z) throws RemoteException;

    String[] setPackagesSuspended(ComponentName componentName, String str, String[] strArr, boolean z) throws RemoteException;

    void setPasswordExpirationTimeout(ComponentName componentName, String str, long j, boolean z) throws RemoteException;

    void setPasswordHistoryLength(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumLength(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumLetters(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumLowerCase(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumNonLetter(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumNumeric(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumSymbols(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordMinimumUpperCase(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPasswordQuality(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setPermissionGrantState(ComponentName componentName, String str, String str2, String str3, int i, RemoteCallback remoteCallback) throws RemoteException;

    void setPermissionPolicy(ComponentName componentName, String str, int i) throws RemoteException;

    boolean setPermittedAccessibilityServices(ComponentName componentName, List<String> list) throws RemoteException;

    boolean setPermittedCrossProfileNotificationListeners(ComponentName componentName, List<String> list) throws RemoteException;

    boolean setPermittedInputMethods(ComponentName componentName, String str, List<String> list, boolean z) throws RemoteException;

    void setPersonalAppsSuspended(ComponentName componentName, boolean z) throws RemoteException;

    void setPreferentialNetworkServiceConfigs(List<PreferentialNetworkServiceConfig> list) throws RemoteException;

    void setProfileEnabled(ComponentName componentName) throws RemoteException;

    void setProfileName(ComponentName componentName, String str) throws RemoteException;

    boolean setProfileOwner(ComponentName componentName, int i) throws RemoteException;

    void setProfileOwnerOnOrganizationOwnedDevice(ComponentName componentName, int i, boolean z) throws RemoteException;

    void setRecommendedGlobalProxy(ComponentName componentName, ProxyInfo proxyInfo) throws RemoteException;

    void setRequiredPasswordComplexity(String str, int i, boolean z) throws RemoteException;

    void setRequiredStrongAuthTimeout(ComponentName componentName, String str, long j, boolean z) throws RemoteException;

    boolean setResetPasswordToken(ComponentName componentName, String str, byte[] bArr) throws RemoteException;

    void setRestrictionsProvider(ComponentName componentName, ComponentName componentName2) throws RemoteException;

    void setScreenCaptureDisabled(ComponentName componentName, String str, boolean z, boolean z2) throws RemoteException;

    void setSecondaryLockscreenEnabled(ComponentName componentName, boolean z) throws RemoteException;

    void setSecureSetting(ComponentName componentName, String str, String str2) throws RemoteException;

    void setSecurityLoggingEnabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    void setShortSupportMessage(ComponentName componentName, String str, CharSequence charSequence) throws RemoteException;

    void setStartUserSessionMessage(ComponentName componentName, CharSequence charSequence) throws RemoteException;

    boolean setStatusBarDisabled(ComponentName componentName, String str, boolean z) throws RemoteException;

    int setStorageEncryption(ComponentName componentName, boolean z) throws RemoteException;

    void setStrings(List<DevicePolicyStringResource> list) throws RemoteException;

    void setSystemSetting(ComponentName componentName, String str, String str2) throws RemoteException;

    void setSystemUpdatePolicy(ComponentName componentName, String str, SystemUpdatePolicy systemUpdatePolicy) throws RemoteException;

    boolean setTime(ComponentName componentName, String str, long j) throws RemoteException;

    boolean setTimeZone(ComponentName componentName, String str, String str2) throws RemoteException;

    void setTrustAgentConfiguration(ComponentName componentName, String str, ComponentName componentName2, PersistableBundle persistableBundle, boolean z) throws RemoteException;

    void setUninstallBlocked(ComponentName componentName, String str, String str2, boolean z) throws RemoteException;

    void setUsbDataSignalingEnabled(String str, boolean z) throws RemoteException;

    void setUserControlDisabledPackages(ComponentName componentName, String str, List<String> list) throws RemoteException;

    void setUserIcon(ComponentName componentName, Bitmap bitmap) throws RemoteException;

    void setUserProvisioningState(int i, int i2) throws RemoteException;

    void setUserRestriction(ComponentName componentName, String str, String str2, boolean z, boolean z2) throws RemoteException;

    void setUserRestrictionGlobally(String str, String str2) throws RemoteException;

    void setWifiSsidPolicy(String str, WifiSsidPolicy wifiSsidPolicy) throws RemoteException;

    boolean shouldAllowBypassingDevicePolicyManagementRoleQualification() throws RemoteException;

    void startManagedQuickContact(String str, long j, boolean z, long j2, Intent intent) throws RemoteException;

    int startUserInBackground(ComponentName componentName, UserHandle userHandle) throws RemoteException;

    boolean startViewCalendarEventInManagedProfile(String str, long j, long j2, long j3, boolean z, int i) throws RemoteException;

    int stopUser(ComponentName componentName, UserHandle userHandle) throws RemoteException;

    boolean switchUser(ComponentName componentName, UserHandle userHandle) throws RemoteException;

    void transferOwnership(ComponentName componentName, ComponentName componentName2, PersistableBundle persistableBundle) throws RemoteException;

    boolean triggerDevicePolicyEngineMigration(boolean z) throws RemoteException;

    void uninstallCaCerts(ComponentName componentName, String str, String[] strArr) throws RemoteException;

    void uninstallPackageWithActiveAdmins(String str) throws RemoteException;

    boolean updateOverrideApn(ComponentName componentName, int i, ApnSetting apnSetting) throws RemoteException;

    void wipeDataWithReason(String str, int i, String str2, boolean z, boolean z2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IDevicePolicyManager {
        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordQuality(ComponentName who, int quality, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordQuality(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumLength(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumLength(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumUpperCase(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumUpperCase(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumLowerCase(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumLowerCase(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumLetters(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumLetters(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumNumeric(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumNumeric(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumSymbols(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumSymbols(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordMinimumNonLetter(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordMinimumNonLetter(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public PasswordMetrics getPasswordMinimumMetrics(int userHandle, boolean deviceWideOnly) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordHistoryLength(ComponentName who, int length, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordHistoryLength(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPasswordExpirationTimeout(ComponentName who, String callerPackageName, long expiration, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getPasswordExpirationTimeout(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getPasswordExpiration(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isActivePasswordSufficient(String callerPackageName, int userHandle, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isActivePasswordSufficientForDeviceRequirement() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isPasswordSufficientAfterProfileUnification(int userHandle, int profileUser) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPasswordComplexity(boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setRequiredPasswordComplexity(String callerPackageName, int passwordComplexity, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getRequiredPasswordComplexity(String callerPackageName, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getAggregatedPasswordComplexityForUser(int userId, boolean deviceWideOnly) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUsingUnifiedPassword(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getCurrentFailedPasswordAttempts(String callerPackageName, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getProfileWithMinimumFailedPasswordsForWipe(int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setMaximumFailedPasswordsForWipe(ComponentName admin, String callerPackageName, int num, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean resetPassword(String password, int flags) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setMaximumTimeToLock(ComponentName who, String callerPackageName, long timeMs, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getMaximumTimeToLock(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setRequiredStrongAuthTimeout(ComponentName who, String callerPackageName, long timeMs, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getRequiredStrongAuthTimeout(ComponentName who, int userId, boolean parent) throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void lockNow(int flags, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void wipeDataWithReason(String callerPackageName, int flags, String wipeReasonForUser, boolean parent, boolean factoryReset) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setFactoryResetProtectionPolicy(ComponentName who, String callerPackageName, FactoryResetProtectionPolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public FactoryResetProtectionPolicy getFactoryResetProtectionPolicy(ComponentName who) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isFactoryResetProtectionPolicySupported() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void sendLostModeLocationUpdate(AndroidFuture<Boolean> future) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName setGlobalProxy(ComponentName admin, String proxySpec, String exclusionList) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName getGlobalProxyAdmin(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setRecommendedGlobalProxy(ComponentName admin, ProxyInfo proxyInfo) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int setStorageEncryption(ComponentName who, boolean encrypt) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getStorageEncryption(ComponentName who, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getStorageEncryptionStatus(String callerPackage, int userHandle) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean requestBugreport(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCameraDisabled(ComponentName who, String callerPackageName, boolean disabled, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getCameraDisabled(ComponentName who, String callerPackageName, int userHandle, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setScreenCaptureDisabled(ComponentName who, String callerPackageName, boolean disabled, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getScreenCaptureDisabled(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setNearbyNotificationStreamingPolicy(int policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getNearbyNotificationStreamingPolicy(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setNearbyAppStreamingPolicy(int policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getNearbyAppStreamingPolicy(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setKeyguardDisabledFeatures(ComponentName who, String callerPackageName, int which, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getKeyguardDisabledFeatures(ComponentName who, int userHandle, boolean parent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing, int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isAdminActive(ComponentName policyReceiver, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<ComponentName> getActiveAdmins(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean packageHasActiveAdmins(String packageName, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void getRemoveWarning(ComponentName policyReceiver, RemoteCallback result, int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void removeActiveAdmin(ComponentName policyReceiver, int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void forceRemoveActiveAdmin(ComponentName policyReceiver, int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasGrantedPolicy(ComponentName policyReceiver, int usesPolicy, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportPasswordChanged(PasswordMetrics metrics, int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportFailedPasswordAttempt(int userHandle, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportSuccessfulPasswordAttempt(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportFailedBiometricAttempt(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportSuccessfulBiometricAttempt(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportKeyguardDismissed(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reportKeyguardSecured(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setDeviceOwner(ComponentName who, int userId, boolean setProfileOwnerOnCurrentUserIfNecessary) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName getDeviceOwnerComponent(boolean callingUserOnly) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasDeviceOwner() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getDeviceOwnerName() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearDeviceOwner(String packageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getDeviceOwnerUserId() throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setProfileOwner(ComponentName who, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName getProfileOwnerAsUser(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isSupervisionComponent(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getProfileOwnerName(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setProfileEnabled(ComponentName who) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setProfileName(ComponentName who, String profileName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearProfileOwner(ComponentName who) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasUserSetupCompleted() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isOrganizationOwnedDeviceWithManagedProfile() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean checkDeviceIdentifierAccess(String packageName, int pid, int uid) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDeviceOwnerLockScreenInfo(ComponentName who, CharSequence deviceOwnerInfo) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getDeviceOwnerLockScreenInfo() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String[] setPackagesSuspended(ComponentName admin, String callerPackage, String[] packageNames, boolean suspended) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isPackageSuspended(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> listPolicyExemptApps() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean installCaCert(ComponentName admin, String callerPackage, byte[] certBuffer) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void uninstallCaCerts(ComponentName admin, String callerPackage, String[] aliases) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void enforceCanManageCaCerts(ComponentName admin, String callerPackage) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean approveCaCert(String alias, int userHandle, boolean approval) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isCaCertApproved(String alias, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean installKeyPair(ComponentName who, String callerPackage, byte[] privKeyBuffer, byte[] certBuffer, byte[] certChainBuffer, String alias, boolean requestAccess, boolean isUserSelectable) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean removeKeyPair(ComponentName who, String callerPackage, String alias) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasKeyPair(String callerPackage, String alias) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean generateKeyPair(ComponentName who, String callerPackage, String algorithm, ParcelableKeyGenParameterSpec keySpec, int idAttestationFlags, KeymasterCertificateChain attestationChain) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setKeyPairCertificate(ComponentName who, String callerPackage, String alias, byte[] certBuffer, byte[] certChainBuffer, boolean isUserSelectable) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void choosePrivateKeyAlias(int uid, Uri uri, String alias, IBinder aliasCallback) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDelegatedScopes(ComponentName who, String delegatePackage, List<String> scopes) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getDelegatedScopes(ComponentName who, String delegatePackage) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getDelegatePackages(ComponentName who, String scope) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCertInstallerPackage(ComponentName who, String installerPackage) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getCertInstallerPackage(ComponentName who) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setAlwaysOnVpnPackage(ComponentName who, String vpnPackage, boolean lockdown, List<String> lockdownAllowlist) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getAlwaysOnVpnPackage(ComponentName who) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getAlwaysOnVpnPackageForUser(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isAlwaysOnVpnLockdownEnabled(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isAlwaysOnVpnLockdownEnabledForUser(int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getAlwaysOnVpnLockdownAllowlist(ComponentName who) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void addPersistentPreferredActivity(ComponentName admin, String callerPackageName, IntentFilter filter, ComponentName activity) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearPackagePersistentPreferredActivities(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDefaultSmsApplication(ComponentName admin, String callerPackageName, String packageName, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDefaultDialerApplication(String packageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setApplicationRestrictions(ComponentName who, String callerPackage, String packageName, Bundle settings) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public Bundle getApplicationRestrictions(ComponentName who, String callerPackage, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setApplicationRestrictionsManagingPackage(ComponentName admin, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getApplicationRestrictionsManagingPackage(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isCallerApplicationRestrictionsManagingPackage(String callerPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setRestrictionsProvider(ComponentName who, ComponentName provider) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ComponentName getRestrictionsProvider(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUserRestriction(ComponentName who, String callerPackage, String key, boolean enable, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUserRestrictionGlobally(String callerPackage, String key) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public Bundle getUserRestrictions(ComponentName who, String callerPackage, boolean parent) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public Bundle getUserRestrictionsGlobally(String callerPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void addCrossProfileIntentFilter(ComponentName admin, String callerPackageName, IntentFilter filter, int flags) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearCrossProfileIntentFilters(ComponentName admin, String callerPackageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setPermittedAccessibilityServices(ComponentName admin, List<String> packageList) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getPermittedAccessibilityServices(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getPermittedAccessibilityServicesForUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isAccessibilityServicePermittedByAdmin(ComponentName admin, String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setPermittedInputMethods(ComponentName admin, String callerPackageName, List<String> packageList, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getPermittedInputMethods(ComponentName admin, String callerPackageName, boolean parent) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getPermittedInputMethodsAsUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isInputMethodPermittedByAdmin(ComponentName admin, String packageName, int userId, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setPermittedCrossProfileNotificationListeners(ComponentName admin, List<String> packageList) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getPermittedCrossProfileNotificationListeners(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isNotificationListenerServicePermitted(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public Intent createAdminSupportIntent(String restriction) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public Bundle getEnforcingAdminAndUserDetails(int userId, String restriction) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setApplicationHidden(ComponentName admin, String callerPackage, String packageName, boolean hidden, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isApplicationHidden(ComponentName admin, String callerPackage, String packageName, boolean parent) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public UserHandle createAndManageUser(ComponentName who, String name, ComponentName profileOwner, PersistableBundle adminExtras, int flags) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean removeUser(ComponentName who, UserHandle userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean switchUser(ComponentName who, UserHandle userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int startUserInBackground(ComponentName who, UserHandle userHandle) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int stopUser(ComponentName who, UserHandle userHandle) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int logoutUser(ComponentName who) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int logoutUserInternal() throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getLogoutUserId() throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<UserHandle> getSecondaryUsers(ComponentName who) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void acknowledgeNewUserDisclaimer(int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isNewUserDisclaimerAcknowledged(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void enableSystemApp(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int enableSystemAppWithIntent(ComponentName admin, String callerPackage, Intent intent) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean installExistingPackage(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setAccountManagementDisabled(ComponentName who, String callerPackageName, String accountType, boolean disabled, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String[] getAccountTypesWithManagementDisabled(String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String[] getAccountTypesWithManagementDisabledAsUser(int userId, String callerPackageName, boolean parent) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setSecondaryLockscreenEnabled(ComponentName who, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isSecondaryLockscreenEnabled(UserHandle userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPreferentialNetworkServiceConfigs(List<PreferentialNetworkServiceConfig> preferentialNetworkServiceConfigs) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setLockTaskPackages(ComponentName who, String callerPackageName, String[] packages) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String[] getLockTaskPackages(ComponentName who, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isLockTaskPermitted(String pkg) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setLockTaskFeatures(ComponentName who, String callerPackageName, int flags) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getLockTaskFeatures(ComponentName who, String callerPackageName) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setGlobalSetting(ComponentName who, String setting, String value) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setSystemSetting(ComponentName who, String setting, String value) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setSecureSetting(ComponentName who, String setting, String value) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setConfiguredNetworksLockdownState(ComponentName who, String callerPackageName, boolean lockdown) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasLockdownAdminConfiguredNetworks(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setLocationEnabled(ComponentName who, boolean locationEnabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setTime(ComponentName who, String callerPackageName, long millis) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setTimeZone(ComponentName who, String callerPackageName, String timeZone) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setMasterVolumeMuted(ComponentName admin, boolean on) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isMasterVolumeMuted(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void notifyLockTaskModeChanged(boolean isEnabled, String pkg, int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUninstallBlocked(ComponentName admin, String callerPackage, String packageName, boolean uninstallBlocked) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUninstallBlocked(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCrossProfileCallerIdDisabled(ComponentName who, boolean disabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getCrossProfileCallerIdDisabled(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getCrossProfileCallerIdDisabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCrossProfileContactsSearchDisabled(ComponentName who, boolean disabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getCrossProfileContactsSearchDisabled(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getCrossProfileContactsSearchDisabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void startManagedQuickContact(String lookupKey, long contactId, boolean isContactIdIgnored, long directoryId, Intent originalIntent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setManagedProfileCallerIdAccessPolicy(PackagePolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public PackagePolicy getManagedProfileCallerIdAccessPolicy() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasManagedProfileCallerIdAccess(int userId, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCredentialManagerPolicy(PackagePolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public PackagePolicy getCredentialManagerPolicy() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setManagedProfileContactsAccessPolicy(PackagePolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public PackagePolicy getManagedProfileContactsAccessPolicy() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean hasManagedProfileContactsAccess(int userId, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setBluetoothContactSharingDisabled(ComponentName who, boolean disabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getBluetoothContactSharingDisabled(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getBluetoothContactSharingDisabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setTrustAgentConfiguration(ComponentName admin, String callerPackageName, ComponentName agent, PersistableBundle args, boolean parent) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent, int userId, boolean parent) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean addCrossProfileWidgetProvider(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean removeCrossProfileWidgetProvider(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getCrossProfileWidgetProviders(ComponentName admin, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setAutoTimeRequired(ComponentName who, boolean required) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getAutoTimeRequired() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setAutoTimeEnabled(ComponentName who, String callerPackageName, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getAutoTimeEnabled(ComponentName who, String callerPackageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setAutoTimeZoneEnabled(ComponentName who, String callerPackageName, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getAutoTimeZoneEnabled(ComponentName who, String callerPackageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setForceEphemeralUsers(ComponentName who, boolean forceEpehemeralUsers) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getForceEphemeralUsers(ComponentName who) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isRemovingAdmin(ComponentName adminReceiver, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUserIcon(ComponentName admin, Bitmap icon) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setSystemUpdatePolicy(ComponentName who, String callerPackageName, SystemUpdatePolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public SystemUpdatePolicy getSystemUpdatePolicy() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearSystemUpdatePolicyFreezePeriodRecord() throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setKeyguardDisabled(ComponentName admin, boolean disabled) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setStatusBarDisabled(ComponentName who, String callerPackageName, boolean disabled) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isStatusBarDisabled(String callerPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean getDoNotAskCredentialsOnBoot() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void notifyPendingSystemUpdate(SystemUpdateInfo info) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public SystemUpdateInfo getPendingSystemUpdate(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPermissionPolicy(ComponentName admin, String callerPackage, int policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPermissionPolicy(ComponentName admin) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission, int grantState, RemoteCallback resultReceiver) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isProvisioningAllowed(String action, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int checkProvisioningPrecondition(String action, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setKeepUninstalledPackages(ComponentName admin, String callerPackage, List<String> packageList) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getKeepUninstalledPackages(ComponentName admin, String callerPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isManagedProfile(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getWifiMacAddress(ComponentName admin, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void reboot(ComponentName admin) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setShortSupportMessage(ComponentName admin, String callerPackageName, CharSequence message) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getShortSupportMessage(ComponentName admin, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setLongSupportMessage(ComponentName admin, CharSequence message) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getLongSupportMessage(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getShortSupportMessageForUser(ComponentName admin, int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getLongSupportMessageForUser(ComponentName admin, int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOrganizationColor(ComponentName admin, int color) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOrganizationColorForUser(int color, int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearOrganizationIdForUser(int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getOrganizationColor(ComponentName admin) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getOrganizationColorForUser(int userHandle) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOrganizationName(ComponentName admin, String callerPackageName, CharSequence title) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getOrganizationName(ComponentName admin, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getDeviceOwnerOrganizationName() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getOrganizationNameForUser(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getUserProvisioningState(int userHandle) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUserProvisioningState(int state, int userHandle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setAffiliationIds(ComponentName admin, List<String> ids) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getAffiliationIds(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isCallingUserAffiliated() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isAffiliatedUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setSecurityLoggingEnabled(ComponentName admin, String packageName, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isSecurityLoggingEnabled(ComponentName admin, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ParceledListSlice retrieveSecurityLogs(ComponentName admin, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ParceledListSlice retrievePreRebootSecurityLogs(ComponentName admin, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long forceNetworkLogs() throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long forceSecurityLogs() throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUninstallInQueue(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void uninstallPackageWithActiveAdmins(String packageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isDeviceProvisioned() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isDeviceProvisioningConfigApplied() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDeviceProvisioningConfigApplied() throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void forceUpdateUserSetupComplete(int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setBackupServiceEnabled(ComponentName admin, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isBackupServiceEnabled(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setNetworkLoggingEnabled(ComponentName admin, String packageName, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isNetworkLoggingEnabled(ComponentName admin, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<NetworkEvent> retrieveNetworkLogs(ComponentName admin, String packageName, long batchToken) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean bindDeviceAdminServiceAsUser(ComponentName admin, IApplicationThread caller, IBinder token, Intent service, IServiceConnection connection, long flags, int targetUserId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<UserHandle> getBindDeviceAdminTargetUsers(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isEphemeralUser(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getLastSecurityLogRetrievalTime() throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getLastBugReportRequestTime() throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getLastNetworkLogRetrievalTime() throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setResetPasswordToken(ComponentName admin, String callerPackageName, byte[] token) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean clearResetPasswordToken(ComponentName admin, String callerPackageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isResetPasswordTokenActive(ComponentName admin, String callerPackageName) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean resetPasswordWithToken(ComponentName admin, String callerPackageName, String password, byte[] token, int flags) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isCurrentInputMethodSetByOwner() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public StringParceledListSlice getOwnerInstalledCaCerts(UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void clearApplicationUserData(ComponentName admin, String packageName, IPackageDataObserver callback) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setLogoutEnabled(ComponentName admin, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isLogoutEnabled() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getDisallowedSystemApps(ComponentName admin, int userId, String provisioningAction) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void transferOwnership(ComponentName admin, ComponentName target, PersistableBundle bundle) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public PersistableBundle getTransferOwnershipBundle() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setStartUserSessionMessage(ComponentName admin, CharSequence startUserSessionMessage) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setEndUserSessionMessage(ComponentName admin, CharSequence endUserSessionMessage) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getStartUserSessionMessage(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public CharSequence getEndUserSessionMessage(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> setMeteredDataDisabledPackages(ComponentName admin, List<String> packageNames) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getMeteredDataDisabledPackages(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int addOverrideApn(ComponentName admin, ApnSetting apnSetting) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean updateOverrideApn(ComponentName admin, int apnId, ApnSetting apnSetting) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean removeOverrideApn(ComponentName admin, int apnId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<ApnSetting> getOverrideApns(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOverrideApnsEnabled(ComponentName admin, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isOverrideApnEnabled(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isMeteredDataDisabledPackageForUser(ComponentName admin, String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int setGlobalPrivateDns(ComponentName admin, int mode, String privateDnsHost) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getGlobalPrivateDnsMode(ComponentName admin) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getGlobalPrivateDnsHost(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setProfileOwnerOnOrganizationOwnedDevice(ComponentName who, int userId, boolean isProfileOwnerOnOrganizationOwnedDevice) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void installUpdateFromFile(ComponentName admin, String callerPackageName, ParcelFileDescriptor updateFileDescriptor, StartInstallingUpdateCallback listener) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCrossProfileCalendarPackages(ComponentName admin, List<String> packageNames) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getCrossProfileCalendarPackages(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isPackageAllowedToAccessCalendarForUser(String packageName, int userHandle) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getCrossProfileCalendarPackagesForUser(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCrossProfilePackages(ComponentName admin, List<String> packageNames) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getCrossProfilePackages(ComponentName admin) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getAllCrossProfilePackages() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getDefaultCrossProfilePackages() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isManagedKiosk() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUnattendedManagedKiosk() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean startViewCalendarEventInManagedProfile(String packageName, long eventId, long start, long end, boolean allDay, int flags) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setKeyGrantForApp(ComponentName admin, String callerPackage, String alias, String packageName, boolean hasGrant) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ParcelableGranteeMap getKeyPairGrants(String callerPackage, String alias) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean setKeyGrantToWifiAuth(String callerPackage, String alias, boolean hasGrant) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isKeyPairGrantedToWifiAuth(String callerPackage, String alias) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUserControlDisabledPackages(ComponentName admin, String callerPackageName, List<String> packages) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<String> getUserControlDisabledPackages(ComponentName admin, String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setCommonCriteriaModeEnabled(ComponentName admin, String callerPackageName, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isCommonCriteriaModeEnabled(ComponentName admin) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getPersonalAppsSuspendedReasons(ComponentName admin) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setPersonalAppsSuspended(ComponentName admin, boolean suspended) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public long getManagedProfileMaximumTimeOff(ComponentName admin) throws RemoteException {
            return 0L;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setManagedProfileMaximumTimeOff(ComponentName admin, long timeoutMs) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void acknowledgeDeviceCompliant() throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isComplianceAcknowledgementRequired() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean canProfileOwnerResetPasswordWhenLocked(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setNextOperationSafety(int operation, int reason) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isSafeOperation(int reason) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public String getEnrollmentSpecificId(String callerPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOrganizationIdForUser(String callerPackage, String enterpriseId, int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public UserHandle createAndProvisionManagedProfile(ManagedProfileProvisioningParams provisioningParams, String callerPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void provisionFullyManagedDevice(FullyManagedDeviceProvisioningParams provisioningParams, String callerPackage) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void finalizeWorkProfileProvisioning(UserHandle managedProfileUser, Account migratedAccount) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDeviceOwnerType(ComponentName admin, int deviceOwnerType) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getDeviceOwnerType(ComponentName admin) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void resetDefaultCrossProfileIntentFilters(int userId) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean canAdminGrantSensorsPermissions() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setUsbDataSignalingEnabled(String callerPackage, boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUsbDataSignalingEnabled(String callerPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isUsbDataSignalingEnabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean canUsbDataSignalingBeDisabled() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setMinimumRequiredWifiSecurityLevel(String callerPackageName, int level) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getMinimumRequiredWifiSecurityLevel() throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setWifiSsidPolicy(String callerPackageName, WifiSsidPolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public WifiSsidPolicy getWifiSsidPolicy(String callerPackageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<UserHandle> listForegroundAffiliatedUsers() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDrawables(List<DevicePolicyDrawableResource> drawables) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void resetDrawables(List<String> drawableIds) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ParcelableResource getDrawable(String drawableId, String drawableStyle, String drawableSource) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean isDpcDownloaded() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setDpcDownloaded(boolean downloaded) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setStrings(List<DevicePolicyStringResource> strings) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void resetStrings(List<String> stringIds) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ParcelableResource getString(String stringId) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        /* renamed from: resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
        public void mo197xce7fef36() throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean shouldAllowBypassingDevicePolicyManagementRoleQualification() throws RemoteException {
            return false;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public List<UserHandle> getPolicyManagedProfiles(UserHandle userHandle) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setApplicationExemptions(String packageName, int[] exemptions) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int[] getApplicationExemptions(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setMtePolicy(int flag, String callerPackageName) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public int getMtePolicy(String callerPackageName) throws RemoteException {
            return 0;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setManagedSubscriptionsPolicy(ManagedSubscriptionsPolicy policy) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public ManagedSubscriptionsPolicy getManagedSubscriptionsPolicy() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public DevicePolicyState getDevicePolicyState() throws RemoteException {
            return null;
        }

        @Override // android.app.admin.IDevicePolicyManager
        public void setOverrideKeepProfilesRunning(boolean enabled) throws RemoteException {
        }

        @Override // android.app.admin.IDevicePolicyManager
        public boolean triggerDevicePolicyEngineMigration(boolean forceMigration) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDevicePolicyManager {
        public static final String DESCRIPTOR = "android.app.admin.IDevicePolicyManager";
        static final int TRANSACTION_acknowledgeDeviceCompliant = 343;
        static final int TRANSACTION_acknowledgeNewUserDisclaimer = 163;
        static final int TRANSACTION_addCrossProfileIntentFilter = 137;
        static final int TRANSACTION_addCrossProfileWidgetProvider = 213;
        static final int TRANSACTION_addOverrideApn = 308;
        static final int TRANSACTION_addPersistentPreferredActivity = 122;
        static final int TRANSACTION_approveCaCert = 103;
        static final int TRANSACTION_bindDeviceAdminServiceAsUser = 284;
        static final int TRANSACTION_canAdminGrantSensorsPermissions = 356;
        static final int TRANSACTION_canProfileOwnerResetPasswordWhenLocked = 345;
        static final int TRANSACTION_canUsbDataSignalingBeDisabled = 360;
        static final int TRANSACTION_checkDeviceIdentifierAccess = 94;
        static final int TRANSACTION_checkProvisioningPrecondition = 240;
        static final int TRANSACTION_choosePrivateKeyAlias = 110;
        static final int TRANSACTION_clearApplicationUserData = 296;
        static final int TRANSACTION_clearCrossProfileIntentFilters = 138;
        static final int TRANSACTION_clearDeviceOwner = 82;
        static final int TRANSACTION_clearOrganizationIdForUser = 254;
        static final int TRANSACTION_clearPackagePersistentPreferredActivities = 123;
        static final int TRANSACTION_clearProfileOwner = 91;
        static final int TRANSACTION_clearResetPasswordToken = 291;
        static final int TRANSACTION_clearSystemUpdatePolicyFreezePeriodRecord = 228;
        static final int TRANSACTION_createAdminSupportIntent = 150;
        static final int TRANSACTION_createAndManageUser = 154;
        static final int TRANSACTION_createAndProvisionManagedProfile = 350;
        static final int TRANSACTION_enableSystemApp = 165;
        static final int TRANSACTION_enableSystemAppWithIntent = 166;
        static final int TRANSACTION_enforceCanManageCaCerts = 102;
        static final int TRANSACTION_finalizeWorkProfileProvisioning = 352;
        static final int TRANSACTION_forceNetworkLogs = 271;
        static final int TRANSACTION_forceRemoveActiveAdmin = 69;
        static final int TRANSACTION_forceSecurityLogs = 272;
        static final int TRANSACTION_forceUpdateUserSetupComplete = 278;
        static final int TRANSACTION_generateKeyPair = 108;
        static final int TRANSACTION_getAccountTypesWithManagementDisabled = 169;
        static final int TRANSACTION_getAccountTypesWithManagementDisabledAsUser = 170;
        static final int TRANSACTION_getActiveAdmins = 65;
        static final int TRANSACTION_getAffiliationIds = 264;
        static final int TRANSACTION_getAggregatedPasswordComplexityForUser = 29;
        static final int TRANSACTION_getAllCrossProfilePackages = 326;
        static final int TRANSACTION_getAlwaysOnVpnLockdownAllowlist = 121;
        static final int TRANSACTION_getAlwaysOnVpnPackage = 117;
        static final int TRANSACTION_getAlwaysOnVpnPackageForUser = 118;
        static final int TRANSACTION_getApplicationExemptions = 378;
        static final int TRANSACTION_getApplicationRestrictions = 127;
        static final int TRANSACTION_getApplicationRestrictionsManagingPackage = 129;
        static final int TRANSACTION_getAutoTimeEnabled = 219;
        static final int TRANSACTION_getAutoTimeRequired = 217;
        static final int TRANSACTION_getAutoTimeZoneEnabled = 221;
        static final int TRANSACTION_getBindDeviceAdminTargetUsers = 285;
        static final int TRANSACTION_getBluetoothContactSharingDisabled = 209;
        static final int TRANSACTION_getBluetoothContactSharingDisabledForUser = 210;
        static final int TRANSACTION_getCameraDisabled = 54;
        static final int TRANSACTION_getCertInstallerPackage = 115;
        static final int TRANSACTION_getCredentialManagerPolicy = 204;
        static final int TRANSACTION_getCrossProfileCalendarPackages = 321;
        static final int TRANSACTION_getCrossProfileCalendarPackagesForUser = 323;
        static final int TRANSACTION_getCrossProfileCallerIdDisabled = 194;
        static final int TRANSACTION_getCrossProfileCallerIdDisabledForUser = 195;
        static final int TRANSACTION_getCrossProfileContactsSearchDisabled = 197;
        static final int TRANSACTION_getCrossProfileContactsSearchDisabledForUser = 198;
        static final int TRANSACTION_getCrossProfilePackages = 325;
        static final int TRANSACTION_getCrossProfileWidgetProviders = 215;
        static final int TRANSACTION_getCurrentFailedPasswordAttempts = 31;
        static final int TRANSACTION_getDefaultCrossProfilePackages = 327;
        static final int TRANSACTION_getDelegatePackages = 113;
        static final int TRANSACTION_getDelegatedScopes = 112;
        static final int TRANSACTION_getDeviceOwnerComponent = 79;
        static final int TRANSACTION_getDeviceOwnerLockScreenInfo = 96;
        static final int TRANSACTION_getDeviceOwnerName = 81;
        static final int TRANSACTION_getDeviceOwnerOrganizationName = 259;
        static final int TRANSACTION_getDeviceOwnerType = 354;
        static final int TRANSACTION_getDeviceOwnerUserId = 83;
        static final int TRANSACTION_getDevicePolicyState = 383;
        static final int TRANSACTION_getDisallowedSystemApps = 299;
        static final int TRANSACTION_getDoNotAskCredentialsOnBoot = 232;
        static final int TRANSACTION_getDrawable = 368;
        static final int TRANSACTION_getEndUserSessionMessage = 305;
        static final int TRANSACTION_getEnforcingAdminAndUserDetails = 151;
        static final int TRANSACTION_getEnrollmentSpecificId = 348;
        static final int TRANSACTION_getFactoryResetProtectionPolicy = 43;
        static final int TRANSACTION_getForceEphemeralUsers = 223;
        static final int TRANSACTION_getGlobalPrivateDnsHost = 317;
        static final int TRANSACTION_getGlobalPrivateDnsMode = 316;
        static final int TRANSACTION_getGlobalProxyAdmin = 47;
        static final int TRANSACTION_getKeepUninstalledPackages = 242;
        static final int TRANSACTION_getKeyPairGrants = 332;
        static final int TRANSACTION_getKeyguardDisabledFeatures = 62;
        static final int TRANSACTION_getLastBugReportRequestTime = 288;
        static final int TRANSACTION_getLastNetworkLogRetrievalTime = 289;
        static final int TRANSACTION_getLastSecurityLogRetrievalTime = 287;
        static final int TRANSACTION_getLockTaskFeatures = 179;
        static final int TRANSACTION_getLockTaskPackages = 176;
        static final int TRANSACTION_getLogoutUserId = 161;
        static final int TRANSACTION_getLongSupportMessage = 249;
        static final int TRANSACTION_getLongSupportMessageForUser = 251;
        static final int TRANSACTION_getManagedProfileCallerIdAccessPolicy = 201;
        static final int TRANSACTION_getManagedProfileContactsAccessPolicy = 206;
        static final int TRANSACTION_getManagedProfileMaximumTimeOff = 341;
        static final int TRANSACTION_getManagedSubscriptionsPolicy = 382;
        static final int TRANSACTION_getMaximumFailedPasswordsForWipe = 34;
        static final int TRANSACTION_getMaximumTimeToLock = 37;
        static final int TRANSACTION_getMeteredDataDisabledPackages = 307;
        static final int TRANSACTION_getMinimumRequiredWifiSecurityLevel = 362;
        static final int TRANSACTION_getMtePolicy = 380;
        static final int TRANSACTION_getNearbyAppStreamingPolicy = 60;
        static final int TRANSACTION_getNearbyNotificationStreamingPolicy = 58;
        static final int TRANSACTION_getOrganizationColor = 255;
        static final int TRANSACTION_getOrganizationColorForUser = 256;
        static final int TRANSACTION_getOrganizationName = 258;
        static final int TRANSACTION_getOrganizationNameForUser = 260;
        static final int TRANSACTION_getOverrideApns = 311;
        static final int TRANSACTION_getOwnerInstalledCaCerts = 295;
        static final int TRANSACTION_getPasswordComplexity = 26;
        static final int TRANSACTION_getPasswordExpiration = 22;
        static final int TRANSACTION_getPasswordExpirationTimeout = 21;
        static final int TRANSACTION_getPasswordHistoryLength = 19;
        static final int TRANSACTION_getPasswordMinimumLength = 4;
        static final int TRANSACTION_getPasswordMinimumLetters = 10;
        static final int TRANSACTION_getPasswordMinimumLowerCase = 8;
        static final int TRANSACTION_getPasswordMinimumMetrics = 17;
        static final int TRANSACTION_getPasswordMinimumNonLetter = 16;
        static final int TRANSACTION_getPasswordMinimumNumeric = 12;
        static final int TRANSACTION_getPasswordMinimumSymbols = 14;
        static final int TRANSACTION_getPasswordMinimumUpperCase = 6;
        static final int TRANSACTION_getPasswordQuality = 2;
        static final int TRANSACTION_getPendingSystemUpdate = 234;
        static final int TRANSACTION_getPermissionGrantState = 238;
        static final int TRANSACTION_getPermissionPolicy = 236;
        static final int TRANSACTION_getPermittedAccessibilityServices = 140;
        static final int TRANSACTION_getPermittedAccessibilityServicesForUser = 141;
        static final int TRANSACTION_getPermittedCrossProfileNotificationListeners = 148;
        static final int TRANSACTION_getPermittedInputMethods = 144;
        static final int TRANSACTION_getPermittedInputMethodsAsUser = 145;
        static final int TRANSACTION_getPersonalAppsSuspendedReasons = 339;
        static final int TRANSACTION_getPolicyManagedProfiles = 376;
        static final int TRANSACTION_getPreferentialNetworkServiceConfigs = 174;
        static final int TRANSACTION_getProfileOwnerAsUser = 85;
        static final int TRANSACTION_getProfileOwnerName = 88;
        static final int TRANSACTION_getProfileOwnerOrDeviceOwnerSupervisionComponent = 86;
        static final int TRANSACTION_getProfileWithMinimumFailedPasswordsForWipe = 32;
        static final int TRANSACTION_getRemoveWarning = 67;
        static final int TRANSACTION_getRequiredPasswordComplexity = 28;
        static final int TRANSACTION_getRequiredStrongAuthTimeout = 39;
        static final int TRANSACTION_getRestrictionsProvider = 132;
        static final int TRANSACTION_getScreenCaptureDisabled = 56;
        static final int TRANSACTION_getSecondaryUsers = 162;
        static final int TRANSACTION_getShortSupportMessage = 247;
        static final int TRANSACTION_getShortSupportMessageForUser = 250;
        static final int TRANSACTION_getStartUserSessionMessage = 304;
        static final int TRANSACTION_getStorageEncryption = 50;
        static final int TRANSACTION_getStorageEncryptionStatus = 51;
        static final int TRANSACTION_getString = 373;
        static final int TRANSACTION_getSystemUpdatePolicy = 227;
        static final int TRANSACTION_getTransferOwnershipBundle = 301;
        static final int TRANSACTION_getTrustAgentConfiguration = 212;
        static final int TRANSACTION_getUserControlDisabledPackages = 336;
        static final int TRANSACTION_getUserProvisioningState = 261;
        static final int TRANSACTION_getUserRestrictions = 135;
        static final int TRANSACTION_getUserRestrictionsGlobally = 136;
        static final int TRANSACTION_getWifiMacAddress = 244;
        static final int TRANSACTION_getWifiSsidPolicy = 364;
        static final int TRANSACTION_hasDeviceOwner = 80;
        static final int TRANSACTION_hasGrantedPolicy = 70;
        static final int TRANSACTION_hasKeyPair = 107;
        static final int TRANSACTION_hasLockdownAdminConfiguredNetworks = 184;
        static final int TRANSACTION_hasManagedProfileCallerIdAccess = 202;
        static final int TRANSACTION_hasManagedProfileContactsAccess = 207;
        static final int TRANSACTION_hasUserSetupCompleted = 92;
        static final int TRANSACTION_installCaCert = 100;
        static final int TRANSACTION_installExistingPackage = 167;
        static final int TRANSACTION_installKeyPair = 105;
        static final int TRANSACTION_installUpdateFromFile = 319;
        static final int TRANSACTION_isAccessibilityServicePermittedByAdmin = 142;
        static final int TRANSACTION_isActivePasswordSufficient = 23;
        static final int TRANSACTION_isActivePasswordSufficientForDeviceRequirement = 24;
        static final int TRANSACTION_isAdminActive = 64;
        static final int TRANSACTION_isAffiliatedUser = 266;
        static final int TRANSACTION_isAlwaysOnVpnLockdownEnabled = 119;
        static final int TRANSACTION_isAlwaysOnVpnLockdownEnabledForUser = 120;
        static final int TRANSACTION_isApplicationHidden = 153;
        static final int TRANSACTION_isBackupServiceEnabled = 280;
        static final int TRANSACTION_isCaCertApproved = 104;
        static final int TRANSACTION_isCallerApplicationRestrictionsManagingPackage = 130;
        static final int TRANSACTION_isCallingUserAffiliated = 265;
        static final int TRANSACTION_isCommonCriteriaModeEnabled = 338;
        static final int TRANSACTION_isComplianceAcknowledgementRequired = 344;
        static final int TRANSACTION_isCurrentInputMethodSetByOwner = 294;
        static final int TRANSACTION_isDeviceProvisioned = 275;
        static final int TRANSACTION_isDeviceProvisioningConfigApplied = 276;
        static final int TRANSACTION_isDpcDownloaded = 369;
        static final int TRANSACTION_isEphemeralUser = 286;
        static final int TRANSACTION_isFactoryResetProtectionPolicySupported = 44;
        static final int TRANSACTION_isInputMethodPermittedByAdmin = 146;
        static final int TRANSACTION_isKeyPairGrantedToWifiAuth = 334;
        static final int TRANSACTION_isLockTaskPermitted = 177;
        static final int TRANSACTION_isLogoutEnabled = 298;
        static final int TRANSACTION_isManagedKiosk = 328;
        static final int TRANSACTION_isManagedProfile = 243;
        static final int TRANSACTION_isMasterVolumeMuted = 189;
        static final int TRANSACTION_isMeteredDataDisabledPackageForUser = 314;
        static final int TRANSACTION_isNetworkLoggingEnabled = 282;
        static final int TRANSACTION_isNewUserDisclaimerAcknowledged = 164;
        static final int TRANSACTION_isNotificationListenerServicePermitted = 149;
        static final int TRANSACTION_isOrganizationOwnedDeviceWithManagedProfile = 93;
        static final int TRANSACTION_isOverrideApnEnabled = 313;
        static final int TRANSACTION_isPackageAllowedToAccessCalendarForUser = 322;
        static final int TRANSACTION_isPackageSuspended = 98;
        static final int TRANSACTION_isPasswordSufficientAfterProfileUnification = 25;
        static final int TRANSACTION_isProvisioningAllowed = 239;
        static final int TRANSACTION_isRemovingAdmin = 224;
        static final int TRANSACTION_isResetPasswordTokenActive = 292;
        static final int TRANSACTION_isSafeOperation = 347;
        static final int TRANSACTION_isSecondaryLockscreenEnabled = 172;
        static final int TRANSACTION_isSecurityLoggingEnabled = 268;
        static final int TRANSACTION_isStatusBarDisabled = 231;
        static final int TRANSACTION_isSupervisionComponent = 87;
        static final int TRANSACTION_isUnattendedManagedKiosk = 329;
        static final int TRANSACTION_isUninstallBlocked = 192;
        static final int TRANSACTION_isUninstallInQueue = 273;
        static final int TRANSACTION_isUsbDataSignalingEnabled = 358;
        static final int TRANSACTION_isUsbDataSignalingEnabledForUser = 359;
        static final int TRANSACTION_isUsingUnifiedPassword = 30;
        static final int TRANSACTION_listForegroundAffiliatedUsers = 365;
        static final int TRANSACTION_listPolicyExemptApps = 99;
        static final int TRANSACTION_lockNow = 40;
        static final int TRANSACTION_logoutUser = 159;
        static final int TRANSACTION_logoutUserInternal = 160;
        static final int TRANSACTION_notifyLockTaskModeChanged = 190;
        static final int TRANSACTION_notifyPendingSystemUpdate = 233;
        static final int TRANSACTION_packageHasActiveAdmins = 66;
        static final int TRANSACTION_provisionFullyManagedDevice = 351;
        static final int TRANSACTION_reboot = 245;
        static final int TRANSACTION_removeActiveAdmin = 68;
        static final int TRANSACTION_removeCrossProfileWidgetProvider = 214;
        static final int TRANSACTION_removeKeyPair = 106;
        static final int TRANSACTION_removeOverrideApn = 310;
        static final int TRANSACTION_removeUser = 155;
        static final int TRANSACTION_reportFailedBiometricAttempt = 74;
        static final int TRANSACTION_reportFailedPasswordAttempt = 72;
        static final int TRANSACTION_reportKeyguardDismissed = 76;
        static final int TRANSACTION_reportKeyguardSecured = 77;
        static final int TRANSACTION_reportPasswordChanged = 71;
        static final int TRANSACTION_reportSuccessfulBiometricAttempt = 75;
        static final int TRANSACTION_reportSuccessfulPasswordAttempt = 73;
        static final int TRANSACTION_requestBugreport = 52;
        static final int TRANSACTION_resetDefaultCrossProfileIntentFilters = 355;
        static final int TRANSACTION_resetDrawables = 367;
        static final int TRANSACTION_resetPassword = 35;
        static final int TRANSACTION_resetPasswordWithToken = 293;

        /* renamed from: TRANSACTION_resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
        static final int f25x8692ba75 = 374;
        static final int TRANSACTION_resetStrings = 372;
        static final int TRANSACTION_retrieveNetworkLogs = 283;
        static final int TRANSACTION_retrievePreRebootSecurityLogs = 270;
        static final int TRANSACTION_retrieveSecurityLogs = 269;
        static final int TRANSACTION_sendLostModeLocationUpdate = 45;
        static final int TRANSACTION_setAccountManagementDisabled = 168;
        static final int TRANSACTION_setActiveAdmin = 63;
        static final int TRANSACTION_setAffiliationIds = 263;
        static final int TRANSACTION_setAlwaysOnVpnPackage = 116;
        static final int TRANSACTION_setApplicationExemptions = 377;
        static final int TRANSACTION_setApplicationHidden = 152;
        static final int TRANSACTION_setApplicationRestrictions = 126;
        static final int TRANSACTION_setApplicationRestrictionsManagingPackage = 128;
        static final int TRANSACTION_setAutoTimeEnabled = 218;
        static final int TRANSACTION_setAutoTimeRequired = 216;
        static final int TRANSACTION_setAutoTimeZoneEnabled = 220;
        static final int TRANSACTION_setBackupServiceEnabled = 279;
        static final int TRANSACTION_setBluetoothContactSharingDisabled = 208;
        static final int TRANSACTION_setCameraDisabled = 53;
        static final int TRANSACTION_setCertInstallerPackage = 114;
        static final int TRANSACTION_setCommonCriteriaModeEnabled = 337;
        static final int TRANSACTION_setConfiguredNetworksLockdownState = 183;
        static final int TRANSACTION_setCredentialManagerPolicy = 203;
        static final int TRANSACTION_setCrossProfileCalendarPackages = 320;
        static final int TRANSACTION_setCrossProfileCallerIdDisabled = 193;
        static final int TRANSACTION_setCrossProfileContactsSearchDisabled = 196;
        static final int TRANSACTION_setCrossProfilePackages = 324;
        static final int TRANSACTION_setDefaultDialerApplication = 125;
        static final int TRANSACTION_setDefaultSmsApplication = 124;
        static final int TRANSACTION_setDelegatedScopes = 111;
        static final int TRANSACTION_setDeviceOwner = 78;
        static final int TRANSACTION_setDeviceOwnerLockScreenInfo = 95;
        static final int TRANSACTION_setDeviceOwnerType = 353;
        static final int TRANSACTION_setDeviceProvisioningConfigApplied = 277;
        static final int TRANSACTION_setDpcDownloaded = 370;
        static final int TRANSACTION_setDrawables = 366;
        static final int TRANSACTION_setEndUserSessionMessage = 303;
        static final int TRANSACTION_setFactoryResetProtectionPolicy = 42;
        static final int TRANSACTION_setForceEphemeralUsers = 222;
        static final int TRANSACTION_setGlobalPrivateDns = 315;
        static final int TRANSACTION_setGlobalProxy = 46;
        static final int TRANSACTION_setGlobalSetting = 180;
        static final int TRANSACTION_setKeepUninstalledPackages = 241;
        static final int TRANSACTION_setKeyGrantForApp = 331;
        static final int TRANSACTION_setKeyGrantToWifiAuth = 333;
        static final int TRANSACTION_setKeyPairCertificate = 109;
        static final int TRANSACTION_setKeyguardDisabled = 229;
        static final int TRANSACTION_setKeyguardDisabledFeatures = 61;
        static final int TRANSACTION_setLocationEnabled = 185;
        static final int TRANSACTION_setLockTaskFeatures = 178;
        static final int TRANSACTION_setLockTaskPackages = 175;
        static final int TRANSACTION_setLogoutEnabled = 297;
        static final int TRANSACTION_setLongSupportMessage = 248;
        static final int TRANSACTION_setManagedProfileCallerIdAccessPolicy = 200;
        static final int TRANSACTION_setManagedProfileContactsAccessPolicy = 205;
        static final int TRANSACTION_setManagedProfileMaximumTimeOff = 342;
        static final int TRANSACTION_setManagedSubscriptionsPolicy = 381;
        static final int TRANSACTION_setMasterVolumeMuted = 188;
        static final int TRANSACTION_setMaximumFailedPasswordsForWipe = 33;
        static final int TRANSACTION_setMaximumTimeToLock = 36;
        static final int TRANSACTION_setMeteredDataDisabledPackages = 306;
        static final int TRANSACTION_setMinimumRequiredWifiSecurityLevel = 361;
        static final int TRANSACTION_setMtePolicy = 379;
        static final int TRANSACTION_setNearbyAppStreamingPolicy = 59;
        static final int TRANSACTION_setNearbyNotificationStreamingPolicy = 57;
        static final int TRANSACTION_setNetworkLoggingEnabled = 281;
        static final int TRANSACTION_setNextOperationSafety = 346;
        static final int TRANSACTION_setOrganizationColor = 252;
        static final int TRANSACTION_setOrganizationColorForUser = 253;
        static final int TRANSACTION_setOrganizationIdForUser = 349;
        static final int TRANSACTION_setOrganizationName = 257;
        static final int TRANSACTION_setOverrideApnsEnabled = 312;
        static final int TRANSACTION_setOverrideKeepProfilesRunning = 384;
        static final int TRANSACTION_setPackagesSuspended = 97;
        static final int TRANSACTION_setPasswordExpirationTimeout = 20;
        static final int TRANSACTION_setPasswordHistoryLength = 18;
        static final int TRANSACTION_setPasswordMinimumLength = 3;
        static final int TRANSACTION_setPasswordMinimumLetters = 9;
        static final int TRANSACTION_setPasswordMinimumLowerCase = 7;
        static final int TRANSACTION_setPasswordMinimumNonLetter = 15;
        static final int TRANSACTION_setPasswordMinimumNumeric = 11;
        static final int TRANSACTION_setPasswordMinimumSymbols = 13;
        static final int TRANSACTION_setPasswordMinimumUpperCase = 5;
        static final int TRANSACTION_setPasswordQuality = 1;
        static final int TRANSACTION_setPermissionGrantState = 237;
        static final int TRANSACTION_setPermissionPolicy = 235;
        static final int TRANSACTION_setPermittedAccessibilityServices = 139;
        static final int TRANSACTION_setPermittedCrossProfileNotificationListeners = 147;
        static final int TRANSACTION_setPermittedInputMethods = 143;
        static final int TRANSACTION_setPersonalAppsSuspended = 340;
        static final int TRANSACTION_setPreferentialNetworkServiceConfigs = 173;
        static final int TRANSACTION_setProfileEnabled = 89;
        static final int TRANSACTION_setProfileName = 90;
        static final int TRANSACTION_setProfileOwner = 84;
        static final int TRANSACTION_setProfileOwnerOnOrganizationOwnedDevice = 318;
        static final int TRANSACTION_setRecommendedGlobalProxy = 48;
        static final int TRANSACTION_setRequiredPasswordComplexity = 27;
        static final int TRANSACTION_setRequiredStrongAuthTimeout = 38;
        static final int TRANSACTION_setResetPasswordToken = 290;
        static final int TRANSACTION_setRestrictionsProvider = 131;
        static final int TRANSACTION_setScreenCaptureDisabled = 55;
        static final int TRANSACTION_setSecondaryLockscreenEnabled = 171;
        static final int TRANSACTION_setSecureSetting = 182;
        static final int TRANSACTION_setSecurityLoggingEnabled = 267;
        static final int TRANSACTION_setShortSupportMessage = 246;
        static final int TRANSACTION_setStartUserSessionMessage = 302;
        static final int TRANSACTION_setStatusBarDisabled = 230;
        static final int TRANSACTION_setStorageEncryption = 49;
        static final int TRANSACTION_setStrings = 371;
        static final int TRANSACTION_setSystemSetting = 181;
        static final int TRANSACTION_setSystemUpdatePolicy = 226;
        static final int TRANSACTION_setTime = 186;
        static final int TRANSACTION_setTimeZone = 187;
        static final int TRANSACTION_setTrustAgentConfiguration = 211;
        static final int TRANSACTION_setUninstallBlocked = 191;
        static final int TRANSACTION_setUsbDataSignalingEnabled = 357;
        static final int TRANSACTION_setUserControlDisabledPackages = 335;
        static final int TRANSACTION_setUserIcon = 225;
        static final int TRANSACTION_setUserProvisioningState = 262;
        static final int TRANSACTION_setUserRestriction = 133;
        static final int TRANSACTION_setUserRestrictionGlobally = 134;
        static final int TRANSACTION_setWifiSsidPolicy = 363;

        /* renamed from: TRANSACTION_shouldAllowBypassingDevicePolicyManagementRoleQualification */
        static final int f26x6af08ee9 = 375;
        static final int TRANSACTION_startManagedQuickContact = 199;
        static final int TRANSACTION_startUserInBackground = 157;
        static final int TRANSACTION_startViewCalendarEventInManagedProfile = 330;
        static final int TRANSACTION_stopUser = 158;
        static final int TRANSACTION_switchUser = 156;
        static final int TRANSACTION_transferOwnership = 300;
        static final int TRANSACTION_triggerDevicePolicyEngineMigration = 385;
        static final int TRANSACTION_uninstallCaCerts = 101;
        static final int TRANSACTION_uninstallPackageWithActiveAdmins = 274;
        static final int TRANSACTION_updateOverrideApn = 309;
        static final int TRANSACTION_wipeDataWithReason = 41;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDevicePolicyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDevicePolicyManager)) {
                return (IDevicePolicyManager) iin;
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
                    return "setPasswordQuality";
                case 2:
                    return "getPasswordQuality";
                case 3:
                    return "setPasswordMinimumLength";
                case 4:
                    return "getPasswordMinimumLength";
                case 5:
                    return "setPasswordMinimumUpperCase";
                case 6:
                    return "getPasswordMinimumUpperCase";
                case 7:
                    return "setPasswordMinimumLowerCase";
                case 8:
                    return "getPasswordMinimumLowerCase";
                case 9:
                    return "setPasswordMinimumLetters";
                case 10:
                    return "getPasswordMinimumLetters";
                case 11:
                    return "setPasswordMinimumNumeric";
                case 12:
                    return "getPasswordMinimumNumeric";
                case 13:
                    return "setPasswordMinimumSymbols";
                case 14:
                    return "getPasswordMinimumSymbols";
                case 15:
                    return "setPasswordMinimumNonLetter";
                case 16:
                    return "getPasswordMinimumNonLetter";
                case 17:
                    return "getPasswordMinimumMetrics";
                case 18:
                    return "setPasswordHistoryLength";
                case 19:
                    return "getPasswordHistoryLength";
                case 20:
                    return "setPasswordExpirationTimeout";
                case 21:
                    return "getPasswordExpirationTimeout";
                case 22:
                    return "getPasswordExpiration";
                case 23:
                    return "isActivePasswordSufficient";
                case 24:
                    return "isActivePasswordSufficientForDeviceRequirement";
                case 25:
                    return "isPasswordSufficientAfterProfileUnification";
                case 26:
                    return "getPasswordComplexity";
                case 27:
                    return "setRequiredPasswordComplexity";
                case 28:
                    return "getRequiredPasswordComplexity";
                case 29:
                    return "getAggregatedPasswordComplexityForUser";
                case 30:
                    return "isUsingUnifiedPassword";
                case 31:
                    return "getCurrentFailedPasswordAttempts";
                case 32:
                    return "getProfileWithMinimumFailedPasswordsForWipe";
                case 33:
                    return "setMaximumFailedPasswordsForWipe";
                case 34:
                    return "getMaximumFailedPasswordsForWipe";
                case 35:
                    return "resetPassword";
                case 36:
                    return "setMaximumTimeToLock";
                case 37:
                    return "getMaximumTimeToLock";
                case 38:
                    return "setRequiredStrongAuthTimeout";
                case 39:
                    return "getRequiredStrongAuthTimeout";
                case 40:
                    return "lockNow";
                case 41:
                    return "wipeDataWithReason";
                case 42:
                    return "setFactoryResetProtectionPolicy";
                case 43:
                    return "getFactoryResetProtectionPolicy";
                case 44:
                    return "isFactoryResetProtectionPolicySupported";
                case 45:
                    return "sendLostModeLocationUpdate";
                case 46:
                    return "setGlobalProxy";
                case 47:
                    return "getGlobalProxyAdmin";
                case 48:
                    return "setRecommendedGlobalProxy";
                case 49:
                    return "setStorageEncryption";
                case 50:
                    return "getStorageEncryption";
                case 51:
                    return "getStorageEncryptionStatus";
                case 52:
                    return "requestBugreport";
                case 53:
                    return "setCameraDisabled";
                case 54:
                    return "getCameraDisabled";
                case 55:
                    return "setScreenCaptureDisabled";
                case 56:
                    return "getScreenCaptureDisabled";
                case 57:
                    return "setNearbyNotificationStreamingPolicy";
                case 58:
                    return "getNearbyNotificationStreamingPolicy";
                case 59:
                    return "setNearbyAppStreamingPolicy";
                case 60:
                    return "getNearbyAppStreamingPolicy";
                case 61:
                    return "setKeyguardDisabledFeatures";
                case 62:
                    return "getKeyguardDisabledFeatures";
                case 63:
                    return "setActiveAdmin";
                case 64:
                    return "isAdminActive";
                case 65:
                    return "getActiveAdmins";
                case 66:
                    return "packageHasActiveAdmins";
                case 67:
                    return "getRemoveWarning";
                case 68:
                    return "removeActiveAdmin";
                case 69:
                    return "forceRemoveActiveAdmin";
                case 70:
                    return "hasGrantedPolicy";
                case 71:
                    return "reportPasswordChanged";
                case 72:
                    return "reportFailedPasswordAttempt";
                case 73:
                    return "reportSuccessfulPasswordAttempt";
                case 74:
                    return "reportFailedBiometricAttempt";
                case 75:
                    return "reportSuccessfulBiometricAttempt";
                case 76:
                    return "reportKeyguardDismissed";
                case 77:
                    return "reportKeyguardSecured";
                case 78:
                    return "setDeviceOwner";
                case 79:
                    return "getDeviceOwnerComponent";
                case 80:
                    return "hasDeviceOwner";
                case 81:
                    return "getDeviceOwnerName";
                case 82:
                    return "clearDeviceOwner";
                case 83:
                    return "getDeviceOwnerUserId";
                case 84:
                    return "setProfileOwner";
                case 85:
                    return "getProfileOwnerAsUser";
                case 86:
                    return "getProfileOwnerOrDeviceOwnerSupervisionComponent";
                case 87:
                    return "isSupervisionComponent";
                case 88:
                    return "getProfileOwnerName";
                case 89:
                    return "setProfileEnabled";
                case 90:
                    return "setProfileName";
                case 91:
                    return "clearProfileOwner";
                case 92:
                    return "hasUserSetupCompleted";
                case 93:
                    return "isOrganizationOwnedDeviceWithManagedProfile";
                case 94:
                    return "checkDeviceIdentifierAccess";
                case 95:
                    return "setDeviceOwnerLockScreenInfo";
                case 96:
                    return "getDeviceOwnerLockScreenInfo";
                case 97:
                    return "setPackagesSuspended";
                case 98:
                    return "isPackageSuspended";
                case 99:
                    return "listPolicyExemptApps";
                case 100:
                    return "installCaCert";
                case 101:
                    return "uninstallCaCerts";
                case 102:
                    return "enforceCanManageCaCerts";
                case 103:
                    return "approveCaCert";
                case 104:
                    return "isCaCertApproved";
                case 105:
                    return "installKeyPair";
                case 106:
                    return "removeKeyPair";
                case 107:
                    return "hasKeyPair";
                case 108:
                    return "generateKeyPair";
                case 109:
                    return "setKeyPairCertificate";
                case 110:
                    return "choosePrivateKeyAlias";
                case 111:
                    return "setDelegatedScopes";
                case 112:
                    return "getDelegatedScopes";
                case 113:
                    return "getDelegatePackages";
                case 114:
                    return "setCertInstallerPackage";
                case 115:
                    return "getCertInstallerPackage";
                case 116:
                    return "setAlwaysOnVpnPackage";
                case 117:
                    return "getAlwaysOnVpnPackage";
                case 118:
                    return "getAlwaysOnVpnPackageForUser";
                case 119:
                    return "isAlwaysOnVpnLockdownEnabled";
                case 120:
                    return "isAlwaysOnVpnLockdownEnabledForUser";
                case 121:
                    return "getAlwaysOnVpnLockdownAllowlist";
                case 122:
                    return "addPersistentPreferredActivity";
                case 123:
                    return "clearPackagePersistentPreferredActivities";
                case 124:
                    return "setDefaultSmsApplication";
                case 125:
                    return "setDefaultDialerApplication";
                case 126:
                    return "setApplicationRestrictions";
                case 127:
                    return "getApplicationRestrictions";
                case 128:
                    return "setApplicationRestrictionsManagingPackage";
                case 129:
                    return "getApplicationRestrictionsManagingPackage";
                case 130:
                    return "isCallerApplicationRestrictionsManagingPackage";
                case 131:
                    return "setRestrictionsProvider";
                case 132:
                    return "getRestrictionsProvider";
                case 133:
                    return "setUserRestriction";
                case 134:
                    return "setUserRestrictionGlobally";
                case 135:
                    return "getUserRestrictions";
                case 136:
                    return "getUserRestrictionsGlobally";
                case 137:
                    return "addCrossProfileIntentFilter";
                case 138:
                    return "clearCrossProfileIntentFilters";
                case 139:
                    return "setPermittedAccessibilityServices";
                case 140:
                    return "getPermittedAccessibilityServices";
                case 141:
                    return "getPermittedAccessibilityServicesForUser";
                case 142:
                    return "isAccessibilityServicePermittedByAdmin";
                case 143:
                    return "setPermittedInputMethods";
                case 144:
                    return "getPermittedInputMethods";
                case 145:
                    return "getPermittedInputMethodsAsUser";
                case 146:
                    return "isInputMethodPermittedByAdmin";
                case 147:
                    return "setPermittedCrossProfileNotificationListeners";
                case 148:
                    return "getPermittedCrossProfileNotificationListeners";
                case 149:
                    return "isNotificationListenerServicePermitted";
                case 150:
                    return "createAdminSupportIntent";
                case 151:
                    return "getEnforcingAdminAndUserDetails";
                case 152:
                    return "setApplicationHidden";
                case 153:
                    return "isApplicationHidden";
                case 154:
                    return "createAndManageUser";
                case 155:
                    return "removeUser";
                case 156:
                    return "switchUser";
                case 157:
                    return "startUserInBackground";
                case 158:
                    return "stopUser";
                case 159:
                    return "logoutUser";
                case 160:
                    return "logoutUserInternal";
                case 161:
                    return "getLogoutUserId";
                case 162:
                    return "getSecondaryUsers";
                case 163:
                    return "acknowledgeNewUserDisclaimer";
                case 164:
                    return "isNewUserDisclaimerAcknowledged";
                case 165:
                    return "enableSystemApp";
                case 166:
                    return "enableSystemAppWithIntent";
                case 167:
                    return "installExistingPackage";
                case 168:
                    return "setAccountManagementDisabled";
                case 169:
                    return "getAccountTypesWithManagementDisabled";
                case 170:
                    return "getAccountTypesWithManagementDisabledAsUser";
                case 171:
                    return "setSecondaryLockscreenEnabled";
                case 172:
                    return "isSecondaryLockscreenEnabled";
                case 173:
                    return "setPreferentialNetworkServiceConfigs";
                case 174:
                    return "getPreferentialNetworkServiceConfigs";
                case 175:
                    return "setLockTaskPackages";
                case 176:
                    return "getLockTaskPackages";
                case 177:
                    return "isLockTaskPermitted";
                case 178:
                    return "setLockTaskFeatures";
                case 179:
                    return "getLockTaskFeatures";
                case 180:
                    return "setGlobalSetting";
                case 181:
                    return "setSystemSetting";
                case 182:
                    return "setSecureSetting";
                case 183:
                    return "setConfiguredNetworksLockdownState";
                case 184:
                    return "hasLockdownAdminConfiguredNetworks";
                case 185:
                    return "setLocationEnabled";
                case 186:
                    return "setTime";
                case 187:
                    return "setTimeZone";
                case 188:
                    return "setMasterVolumeMuted";
                case 189:
                    return "isMasterVolumeMuted";
                case 190:
                    return "notifyLockTaskModeChanged";
                case 191:
                    return "setUninstallBlocked";
                case 192:
                    return "isUninstallBlocked";
                case 193:
                    return "setCrossProfileCallerIdDisabled";
                case 194:
                    return "getCrossProfileCallerIdDisabled";
                case 195:
                    return "getCrossProfileCallerIdDisabledForUser";
                case 196:
                    return "setCrossProfileContactsSearchDisabled";
                case 197:
                    return "getCrossProfileContactsSearchDisabled";
                case 198:
                    return "getCrossProfileContactsSearchDisabledForUser";
                case 199:
                    return "startManagedQuickContact";
                case 200:
                    return "setManagedProfileCallerIdAccessPolicy";
                case 201:
                    return "getManagedProfileCallerIdAccessPolicy";
                case 202:
                    return "hasManagedProfileCallerIdAccess";
                case 203:
                    return "setCredentialManagerPolicy";
                case 204:
                    return "getCredentialManagerPolicy";
                case 205:
                    return "setManagedProfileContactsAccessPolicy";
                case 206:
                    return "getManagedProfileContactsAccessPolicy";
                case 207:
                    return "hasManagedProfileContactsAccess";
                case 208:
                    return "setBluetoothContactSharingDisabled";
                case 209:
                    return "getBluetoothContactSharingDisabled";
                case 210:
                    return "getBluetoothContactSharingDisabledForUser";
                case 211:
                    return "setTrustAgentConfiguration";
                case 212:
                    return "getTrustAgentConfiguration";
                case 213:
                    return "addCrossProfileWidgetProvider";
                case 214:
                    return "removeCrossProfileWidgetProvider";
                case 215:
                    return "getCrossProfileWidgetProviders";
                case 216:
                    return "setAutoTimeRequired";
                case 217:
                    return "getAutoTimeRequired";
                case 218:
                    return "setAutoTimeEnabled";
                case 219:
                    return "getAutoTimeEnabled";
                case 220:
                    return "setAutoTimeZoneEnabled";
                case 221:
                    return "getAutoTimeZoneEnabled";
                case 222:
                    return "setForceEphemeralUsers";
                case 223:
                    return "getForceEphemeralUsers";
                case 224:
                    return "isRemovingAdmin";
                case 225:
                    return "setUserIcon";
                case 226:
                    return "setSystemUpdatePolicy";
                case 227:
                    return "getSystemUpdatePolicy";
                case 228:
                    return "clearSystemUpdatePolicyFreezePeriodRecord";
                case 229:
                    return "setKeyguardDisabled";
                case 230:
                    return "setStatusBarDisabled";
                case 231:
                    return "isStatusBarDisabled";
                case 232:
                    return "getDoNotAskCredentialsOnBoot";
                case 233:
                    return "notifyPendingSystemUpdate";
                case 234:
                    return "getPendingSystemUpdate";
                case 235:
                    return "setPermissionPolicy";
                case 236:
                    return "getPermissionPolicy";
                case 237:
                    return "setPermissionGrantState";
                case 238:
                    return "getPermissionGrantState";
                case 239:
                    return "isProvisioningAllowed";
                case 240:
                    return "checkProvisioningPrecondition";
                case 241:
                    return "setKeepUninstalledPackages";
                case 242:
                    return "getKeepUninstalledPackages";
                case 243:
                    return "isManagedProfile";
                case 244:
                    return "getWifiMacAddress";
                case 245:
                    return "reboot";
                case 246:
                    return "setShortSupportMessage";
                case 247:
                    return "getShortSupportMessage";
                case 248:
                    return "setLongSupportMessage";
                case 249:
                    return "getLongSupportMessage";
                case 250:
                    return "getShortSupportMessageForUser";
                case 251:
                    return "getLongSupportMessageForUser";
                case 252:
                    return "setOrganizationColor";
                case 253:
                    return "setOrganizationColorForUser";
                case 254:
                    return "clearOrganizationIdForUser";
                case 255:
                    return "getOrganizationColor";
                case 256:
                    return "getOrganizationColorForUser";
                case 257:
                    return "setOrganizationName";
                case 258:
                    return "getOrganizationName";
                case 259:
                    return "getDeviceOwnerOrganizationName";
                case 260:
                    return "getOrganizationNameForUser";
                case 261:
                    return "getUserProvisioningState";
                case 262:
                    return "setUserProvisioningState";
                case 263:
                    return "setAffiliationIds";
                case 264:
                    return "getAffiliationIds";
                case 265:
                    return "isCallingUserAffiliated";
                case 266:
                    return "isAffiliatedUser";
                case 267:
                    return "setSecurityLoggingEnabled";
                case 268:
                    return "isSecurityLoggingEnabled";
                case 269:
                    return "retrieveSecurityLogs";
                case 270:
                    return "retrievePreRebootSecurityLogs";
                case 271:
                    return "forceNetworkLogs";
                case 272:
                    return "forceSecurityLogs";
                case 273:
                    return "isUninstallInQueue";
                case 274:
                    return "uninstallPackageWithActiveAdmins";
                case 275:
                    return "isDeviceProvisioned";
                case 276:
                    return "isDeviceProvisioningConfigApplied";
                case 277:
                    return "setDeviceProvisioningConfigApplied";
                case 278:
                    return "forceUpdateUserSetupComplete";
                case 279:
                    return "setBackupServiceEnabled";
                case 280:
                    return "isBackupServiceEnabled";
                case 281:
                    return "setNetworkLoggingEnabled";
                case 282:
                    return "isNetworkLoggingEnabled";
                case 283:
                    return "retrieveNetworkLogs";
                case 284:
                    return "bindDeviceAdminServiceAsUser";
                case 285:
                    return "getBindDeviceAdminTargetUsers";
                case 286:
                    return "isEphemeralUser";
                case 287:
                    return "getLastSecurityLogRetrievalTime";
                case 288:
                    return "getLastBugReportRequestTime";
                case 289:
                    return "getLastNetworkLogRetrievalTime";
                case 290:
                    return "setResetPasswordToken";
                case 291:
                    return "clearResetPasswordToken";
                case 292:
                    return "isResetPasswordTokenActive";
                case 293:
                    return "resetPasswordWithToken";
                case 294:
                    return "isCurrentInputMethodSetByOwner";
                case 295:
                    return "getOwnerInstalledCaCerts";
                case 296:
                    return "clearApplicationUserData";
                case 297:
                    return "setLogoutEnabled";
                case 298:
                    return "isLogoutEnabled";
                case 299:
                    return "getDisallowedSystemApps";
                case 300:
                    return "transferOwnership";
                case 301:
                    return "getTransferOwnershipBundle";
                case 302:
                    return "setStartUserSessionMessage";
                case 303:
                    return "setEndUserSessionMessage";
                case 304:
                    return "getStartUserSessionMessage";
                case 305:
                    return "getEndUserSessionMessage";
                case 306:
                    return "setMeteredDataDisabledPackages";
                case 307:
                    return "getMeteredDataDisabledPackages";
                case 308:
                    return "addOverrideApn";
                case 309:
                    return "updateOverrideApn";
                case 310:
                    return "removeOverrideApn";
                case 311:
                    return "getOverrideApns";
                case 312:
                    return "setOverrideApnsEnabled";
                case 313:
                    return "isOverrideApnEnabled";
                case 314:
                    return "isMeteredDataDisabledPackageForUser";
                case 315:
                    return "setGlobalPrivateDns";
                case 316:
                    return "getGlobalPrivateDnsMode";
                case 317:
                    return "getGlobalPrivateDnsHost";
                case 318:
                    return "setProfileOwnerOnOrganizationOwnedDevice";
                case 319:
                    return "installUpdateFromFile";
                case 320:
                    return "setCrossProfileCalendarPackages";
                case 321:
                    return "getCrossProfileCalendarPackages";
                case 322:
                    return "isPackageAllowedToAccessCalendarForUser";
                case 323:
                    return "getCrossProfileCalendarPackagesForUser";
                case 324:
                    return "setCrossProfilePackages";
                case 325:
                    return "getCrossProfilePackages";
                case 326:
                    return "getAllCrossProfilePackages";
                case 327:
                    return "getDefaultCrossProfilePackages";
                case 328:
                    return "isManagedKiosk";
                case 329:
                    return "isUnattendedManagedKiosk";
                case 330:
                    return "startViewCalendarEventInManagedProfile";
                case 331:
                    return "setKeyGrantForApp";
                case 332:
                    return "getKeyPairGrants";
                case 333:
                    return "setKeyGrantToWifiAuth";
                case 334:
                    return "isKeyPairGrantedToWifiAuth";
                case 335:
                    return "setUserControlDisabledPackages";
                case 336:
                    return "getUserControlDisabledPackages";
                case 337:
                    return "setCommonCriteriaModeEnabled";
                case 338:
                    return "isCommonCriteriaModeEnabled";
                case 339:
                    return "getPersonalAppsSuspendedReasons";
                case 340:
                    return "setPersonalAppsSuspended";
                case 341:
                    return "getManagedProfileMaximumTimeOff";
                case 342:
                    return "setManagedProfileMaximumTimeOff";
                case 343:
                    return "acknowledgeDeviceCompliant";
                case 344:
                    return "isComplianceAcknowledgementRequired";
                case 345:
                    return "canProfileOwnerResetPasswordWhenLocked";
                case 346:
                    return "setNextOperationSafety";
                case 347:
                    return "isSafeOperation";
                case 348:
                    return "getEnrollmentSpecificId";
                case 349:
                    return "setOrganizationIdForUser";
                case 350:
                    return "createAndProvisionManagedProfile";
                case 351:
                    return "provisionFullyManagedDevice";
                case 352:
                    return "finalizeWorkProfileProvisioning";
                case 353:
                    return "setDeviceOwnerType";
                case 354:
                    return "getDeviceOwnerType";
                case 355:
                    return "resetDefaultCrossProfileIntentFilters";
                case 356:
                    return "canAdminGrantSensorsPermissions";
                case 357:
                    return "setUsbDataSignalingEnabled";
                case 358:
                    return "isUsbDataSignalingEnabled";
                case 359:
                    return "isUsbDataSignalingEnabledForUser";
                case 360:
                    return "canUsbDataSignalingBeDisabled";
                case 361:
                    return "setMinimumRequiredWifiSecurityLevel";
                case 362:
                    return "getMinimumRequiredWifiSecurityLevel";
                case 363:
                    return "setWifiSsidPolicy";
                case 364:
                    return "getWifiSsidPolicy";
                case 365:
                    return "listForegroundAffiliatedUsers";
                case 366:
                    return "setDrawables";
                case 367:
                    return "resetDrawables";
                case 368:
                    return "getDrawable";
                case 369:
                    return "isDpcDownloaded";
                case 370:
                    return "setDpcDownloaded";
                case 371:
                    return "setStrings";
                case 372:
                    return "resetStrings";
                case 373:
                    return "getString";
                case 374:
                    return "resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState";
                case 375:
                    return "shouldAllowBypassingDevicePolicyManagementRoleQualification";
                case 376:
                    return "getPolicyManagedProfiles";
                case 377:
                    return "setApplicationExemptions";
                case 378:
                    return "getApplicationExemptions";
                case 379:
                    return "setMtePolicy";
                case 380:
                    return "getMtePolicy";
                case 381:
                    return "setManagedSubscriptionsPolicy";
                case 382:
                    return "getManagedSubscriptionsPolicy";
                case 383:
                    return "getDevicePolicyState";
                case 384:
                    return "setOverrideKeepProfilesRunning";
                case 385:
                    return "triggerDevicePolicyEngineMigration";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
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
                            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg1 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordQuality(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            ComponentName _arg02 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg12 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result = getPasswordQuality(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 3:
                            ComponentName _arg03 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg13 = data.readInt();
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordMinimumLength(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 4:
                            ComponentName _arg04 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg14 = data.readInt();
                            boolean _arg24 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result2 = getPasswordMinimumLength(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            ComponentName _arg05 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg15 = data.readInt();
                            boolean _arg25 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordMinimumUpperCase(_arg05, _arg15, _arg25);
                            reply.writeNoException();
                            break;
                        case 6:
                            ComponentName _arg06 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg16 = data.readInt();
                            boolean _arg26 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result3 = getPasswordMinimumUpperCase(_arg06, _arg16, _arg26);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 7:
                            ComponentName _arg07 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg17 = data.readInt();
                            boolean _arg27 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordMinimumLowerCase(_arg07, _arg17, _arg27);
                            reply.writeNoException();
                            break;
                        case 8:
                            ComponentName _arg08 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg18 = data.readInt();
                            boolean _arg28 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result4 = getPasswordMinimumLowerCase(_arg08, _arg18, _arg28);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 9:
                            ComponentName _arg09 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg19 = data.readInt();
                            boolean _arg29 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordMinimumLetters(_arg09, _arg19, _arg29);
                            reply.writeNoException();
                            break;
                        case 10:
                            ComponentName _arg010 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg110 = data.readInt();
                            boolean _arg210 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result5 = getPasswordMinimumLetters(_arg010, _arg110, _arg210);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 11:
                            ComponentName _arg011 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg111 = data.readInt();
                            boolean _arg211 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPasswordMinimumNumeric(_arg011, _arg111, _arg211);
                            reply.writeNoException();
                            break;
                        case 12:
                            ComponentName _arg012 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg112 = data.readInt();
                            boolean _arg212 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result6 = getPasswordMinimumNumeric(_arg012, _arg112, _arg212);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 13:
                            return onTransact$setPasswordMinimumSymbols$(data, reply);
                        case 14:
                            return onTransact$getPasswordMinimumSymbols$(data, reply);
                        case 15:
                            return onTransact$setPasswordMinimumNonLetter$(data, reply);
                        case 16:
                            return onTransact$getPasswordMinimumNonLetter$(data, reply);
                        case 17:
                            int _arg013 = data.readInt();
                            boolean _arg113 = data.readBoolean();
                            data.enforceNoDataAvail();
                            PasswordMetrics _result7 = getPasswordMinimumMetrics(_arg013, _arg113);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 18:
                            return onTransact$setPasswordHistoryLength$(data, reply);
                        case 19:
                            return onTransact$getPasswordHistoryLength$(data, reply);
                        case 20:
                            return onTransact$setPasswordExpirationTimeout$(data, reply);
                        case 21:
                            return onTransact$getPasswordExpirationTimeout$(data, reply);
                        case 22:
                            return onTransact$getPasswordExpiration$(data, reply);
                        case 23:
                            return onTransact$isActivePasswordSufficient$(data, reply);
                        case 24:
                            boolean _result8 = isActivePasswordSufficientForDeviceRequirement();
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 25:
                            int _arg014 = data.readInt();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = isPasswordSufficientAfterProfileUnification(_arg014, _arg114);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 26:
                            boolean _arg015 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result10 = getPasswordComplexity(_arg015);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 27:
                            return onTransact$setRequiredPasswordComplexity$(data, reply);
                        case 28:
                            String _arg016 = data.readString();
                            boolean _arg115 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result11 = getRequiredPasswordComplexity(_arg016, _arg115);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 29:
                            int _arg017 = data.readInt();
                            boolean _arg116 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result12 = getAggregatedPasswordComplexityForUser(_arg017, _arg116);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 30:
                            ComponentName _arg018 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result13 = isUsingUnifiedPassword(_arg018);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 31:
                            return onTransact$getCurrentFailedPasswordAttempts$(data, reply);
                        case 32:
                            int _arg019 = data.readInt();
                            boolean _arg117 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result14 = getProfileWithMinimumFailedPasswordsForWipe(_arg019, _arg117);
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 33:
                            return onTransact$setMaximumFailedPasswordsForWipe$(data, reply);
                        case 34:
                            return onTransact$getMaximumFailedPasswordsForWipe$(data, reply);
                        case 35:
                            String _arg020 = data.readString();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = resetPassword(_arg020, _arg118);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 36:
                            return onTransact$setMaximumTimeToLock$(data, reply);
                        case 37:
                            return onTransact$getMaximumTimeToLock$(data, reply);
                        case 38:
                            return onTransact$setRequiredStrongAuthTimeout$(data, reply);
                        case 39:
                            return onTransact$getRequiredStrongAuthTimeout$(data, reply);
                        case 40:
                            int _arg021 = data.readInt();
                            boolean _arg119 = data.readBoolean();
                            data.enforceNoDataAvail();
                            lockNow(_arg021, _arg119);
                            reply.writeNoException();
                            break;
                        case 41:
                            return onTransact$wipeDataWithReason$(data, reply);
                        case 42:
                            return onTransact$setFactoryResetProtectionPolicy$(data, reply);
                        case 43:
                            ComponentName _arg022 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            FactoryResetProtectionPolicy _result16 = getFactoryResetProtectionPolicy(_arg022);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            break;
                        case 44:
                            boolean _result17 = isFactoryResetProtectionPolicySupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 45:
                            AndroidFuture<Boolean> _arg023 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            sendLostModeLocationUpdate(_arg023);
                            reply.writeNoException();
                            break;
                        case 46:
                            return onTransact$setGlobalProxy$(data, reply);
                        case 47:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            ComponentName _result18 = getGlobalProxyAdmin(_arg024);
                            reply.writeNoException();
                            reply.writeTypedObject(_result18, 1);
                            break;
                        case 48:
                            ComponentName _arg025 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            ProxyInfo _arg120 = (ProxyInfo) data.readTypedObject(ProxyInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setRecommendedGlobalProxy(_arg025, _arg120);
                            reply.writeNoException();
                            break;
                        case 49:
                            ComponentName _arg026 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg121 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result19 = setStorageEncryption(_arg026, _arg121);
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            break;
                        case 50:
                            ComponentName _arg027 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result20 = getStorageEncryption(_arg027, _arg122);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            break;
                        case 51:
                            String _arg028 = data.readString();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result21 = getStorageEncryptionStatus(_arg028, _arg123);
                            reply.writeNoException();
                            reply.writeInt(_result21);
                            break;
                        case 52:
                            ComponentName _arg029 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result22 = requestBugreport(_arg029);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            break;
                        case 53:
                            return onTransact$setCameraDisabled$(data, reply);
                        case 54:
                            return onTransact$getCameraDisabled$(data, reply);
                        case 55:
                            return onTransact$setScreenCaptureDisabled$(data, reply);
                        case 56:
                            return onTransact$getScreenCaptureDisabled$(data, reply);
                        case 57:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            setNearbyNotificationStreamingPolicy(_arg030);
                            reply.writeNoException();
                            break;
                        case 58:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result23 = getNearbyNotificationStreamingPolicy(_arg031);
                            reply.writeNoException();
                            reply.writeInt(_result23);
                            break;
                        case 59:
                            int _arg032 = data.readInt();
                            data.enforceNoDataAvail();
                            setNearbyAppStreamingPolicy(_arg032);
                            reply.writeNoException();
                            break;
                        case 60:
                            int _arg033 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result24 = getNearbyAppStreamingPolicy(_arg033);
                            reply.writeNoException();
                            reply.writeInt(_result24);
                            break;
                        case 61:
                            return onTransact$setKeyguardDisabledFeatures$(data, reply);
                        case 62:
                            return onTransact$getKeyguardDisabledFeatures$(data, reply);
                        case 63:
                            return onTransact$setActiveAdmin$(data, reply);
                        case 64:
                            ComponentName _arg034 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result25 = isAdminActive(_arg034, _arg124);
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            break;
                        case 65:
                            int _arg035 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ComponentName> _result26 = getActiveAdmins(_arg035);
                            reply.writeNoException();
                            reply.writeTypedList(_result26, 1);
                            break;
                        case 66:
                            String _arg036 = data.readString();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result27 = packageHasActiveAdmins(_arg036, _arg125);
                            reply.writeNoException();
                            reply.writeBoolean(_result27);
                            break;
                        case 67:
                            return onTransact$getRemoveWarning$(data, reply);
                        case 68:
                            ComponentName _arg037 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg126 = data.readInt();
                            data.enforceNoDataAvail();
                            removeActiveAdmin(_arg037, _arg126);
                            reply.writeNoException();
                            break;
                        case 69:
                            ComponentName _arg038 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg127 = data.readInt();
                            data.enforceNoDataAvail();
                            forceRemoveActiveAdmin(_arg038, _arg127);
                            reply.writeNoException();
                            break;
                        case 70:
                            return onTransact$hasGrantedPolicy$(data, reply);
                        case 71:
                            PasswordMetrics _arg039 = (PasswordMetrics) data.readTypedObject(PasswordMetrics.CREATOR);
                            int _arg128 = data.readInt();
                            data.enforceNoDataAvail();
                            reportPasswordChanged(_arg039, _arg128);
                            reply.writeNoException();
                            break;
                        case 72:
                            int _arg040 = data.readInt();
                            boolean _arg129 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportFailedPasswordAttempt(_arg040, _arg129);
                            reply.writeNoException();
                            break;
                        case 73:
                            int _arg041 = data.readInt();
                            data.enforceNoDataAvail();
                            reportSuccessfulPasswordAttempt(_arg041);
                            reply.writeNoException();
                            break;
                        case 74:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            reportFailedBiometricAttempt(_arg042);
                            reply.writeNoException();
                            break;
                        case 75:
                            int _arg043 = data.readInt();
                            data.enforceNoDataAvail();
                            reportSuccessfulBiometricAttempt(_arg043);
                            reply.writeNoException();
                            break;
                        case 76:
                            int _arg044 = data.readInt();
                            data.enforceNoDataAvail();
                            reportKeyguardDismissed(_arg044);
                            reply.writeNoException();
                            break;
                        case 77:
                            int _arg045 = data.readInt();
                            data.enforceNoDataAvail();
                            reportKeyguardSecured(_arg045);
                            reply.writeNoException();
                            break;
                        case 78:
                            return onTransact$setDeviceOwner$(data, reply);
                        case 79:
                            boolean _arg046 = data.readBoolean();
                            data.enforceNoDataAvail();
                            ComponentName _result28 = getDeviceOwnerComponent(_arg046);
                            reply.writeNoException();
                            reply.writeTypedObject(_result28, 1);
                            break;
                        case 80:
                            boolean _result29 = hasDeviceOwner();
                            reply.writeNoException();
                            reply.writeBoolean(_result29);
                            break;
                        case 81:
                            String _result30 = getDeviceOwnerName();
                            reply.writeNoException();
                            reply.writeString(_result30);
                            break;
                        case 82:
                            String _arg047 = data.readString();
                            data.enforceNoDataAvail();
                            clearDeviceOwner(_arg047);
                            reply.writeNoException();
                            break;
                        case 83:
                            int _result31 = getDeviceOwnerUserId();
                            reply.writeNoException();
                            reply.writeInt(_result31);
                            break;
                        case 84:
                            ComponentName _arg048 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg130 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = setProfileOwner(_arg048, _arg130);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 85:
                            int _arg049 = data.readInt();
                            data.enforceNoDataAvail();
                            ComponentName _result33 = getProfileOwnerAsUser(_arg049);
                            reply.writeNoException();
                            reply.writeTypedObject(_result33, 1);
                            break;
                        case 86:
                            UserHandle _arg050 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            ComponentName _result34 = getProfileOwnerOrDeviceOwnerSupervisionComponent(_arg050);
                            reply.writeNoException();
                            reply.writeTypedObject(_result34, 1);
                            break;
                        case 87:
                            ComponentName _arg051 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result35 = isSupervisionComponent(_arg051);
                            reply.writeNoException();
                            reply.writeBoolean(_result35);
                            break;
                        case 88:
                            int _arg052 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result36 = getProfileOwnerName(_arg052);
                            reply.writeNoException();
                            reply.writeString(_result36);
                            break;
                        case 89:
                            ComponentName _arg053 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setProfileEnabled(_arg053);
                            reply.writeNoException();
                            break;
                        case 90:
                            ComponentName _arg054 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg131 = data.readString();
                            data.enforceNoDataAvail();
                            setProfileName(_arg054, _arg131);
                            reply.writeNoException();
                            break;
                        case 91:
                            ComponentName _arg055 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            clearProfileOwner(_arg055);
                            reply.writeNoException();
                            break;
                        case 92:
                            boolean _result37 = hasUserSetupCompleted();
                            reply.writeNoException();
                            reply.writeBoolean(_result37);
                            break;
                        case 93:
                            boolean _result38 = isOrganizationOwnedDeviceWithManagedProfile();
                            reply.writeNoException();
                            reply.writeBoolean(_result38);
                            break;
                        case 94:
                            return onTransact$checkDeviceIdentifierAccess$(data, reply);
                        case 95:
                            ComponentName _arg056 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            CharSequence _arg132 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setDeviceOwnerLockScreenInfo(_arg056, _arg132);
                            reply.writeNoException();
                            break;
                        case 96:
                            CharSequence _result39 = getDeviceOwnerLockScreenInfo();
                            reply.writeNoException();
                            if (_result39 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result39, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 97:
                            return onTransact$setPackagesSuspended$(data, reply);
                        case 98:
                            return onTransact$isPackageSuspended$(data, reply);
                        case 99:
                            List<String> _result40 = listPolicyExemptApps();
                            reply.writeNoException();
                            reply.writeStringList(_result40);
                            break;
                        case 100:
                            return onTransact$installCaCert$(data, reply);
                        case 101:
                            return onTransact$uninstallCaCerts$(data, reply);
                        case 102:
                            ComponentName _arg057 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg133 = data.readString();
                            data.enforceNoDataAvail();
                            enforceCanManageCaCerts(_arg057, _arg133);
                            reply.writeNoException();
                            break;
                        case 103:
                            return onTransact$approveCaCert$(data, reply);
                        case 104:
                            String _arg058 = data.readString();
                            int _arg134 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result41 = isCaCertApproved(_arg058, _arg134);
                            reply.writeNoException();
                            reply.writeBoolean(_result41);
                            break;
                        case 105:
                            return onTransact$installKeyPair$(data, reply);
                        case 106:
                            return onTransact$removeKeyPair$(data, reply);
                        case 107:
                            String _arg059 = data.readString();
                            String _arg135 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result42 = hasKeyPair(_arg059, _arg135);
                            reply.writeNoException();
                            reply.writeBoolean(_result42);
                            break;
                        case 108:
                            return onTransact$generateKeyPair$(data, reply);
                        case 109:
                            return onTransact$setKeyPairCertificate$(data, reply);
                        case 110:
                            return onTransact$choosePrivateKeyAlias$(data, reply);
                        case 111:
                            return onTransact$setDelegatedScopes$(data, reply);
                        case 112:
                            ComponentName _arg060 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg136 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result43 = getDelegatedScopes(_arg060, _arg136);
                            reply.writeNoException();
                            reply.writeStringList(_result43);
                            break;
                        case 113:
                            ComponentName _arg061 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg137 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result44 = getDelegatePackages(_arg061, _arg137);
                            reply.writeNoException();
                            reply.writeStringList(_result44);
                            break;
                        case 114:
                            ComponentName _arg062 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg138 = data.readString();
                            data.enforceNoDataAvail();
                            setCertInstallerPackage(_arg062, _arg138);
                            reply.writeNoException();
                            break;
                        case 115:
                            ComponentName _arg063 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result45 = getCertInstallerPackage(_arg063);
                            reply.writeNoException();
                            reply.writeString(_result45);
                            break;
                        case 116:
                            return onTransact$setAlwaysOnVpnPackage$(data, reply);
                        case 117:
                            ComponentName _arg064 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result46 = getAlwaysOnVpnPackage(_arg064);
                            reply.writeNoException();
                            reply.writeString(_result46);
                            break;
                        case 118:
                            int _arg065 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result47 = getAlwaysOnVpnPackageForUser(_arg065);
                            reply.writeNoException();
                            reply.writeString(_result47);
                            break;
                        case 119:
                            ComponentName _arg066 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result48 = isAlwaysOnVpnLockdownEnabled(_arg066);
                            reply.writeNoException();
                            reply.writeBoolean(_result48);
                            break;
                        case 120:
                            int _arg067 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result49 = isAlwaysOnVpnLockdownEnabledForUser(_arg067);
                            reply.writeNoException();
                            reply.writeBoolean(_result49);
                            break;
                        case 121:
                            ComponentName _arg068 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result50 = getAlwaysOnVpnLockdownAllowlist(_arg068);
                            reply.writeNoException();
                            reply.writeStringList(_result50);
                            break;
                        case 122:
                            return onTransact$addPersistentPreferredActivity$(data, reply);
                        case 123:
                            return onTransact$clearPackagePersistentPreferredActivities$(data, reply);
                        case 124:
                            return onTransact$setDefaultSmsApplication$(data, reply);
                        case 125:
                            String _arg069 = data.readString();
                            data.enforceNoDataAvail();
                            setDefaultDialerApplication(_arg069);
                            reply.writeNoException();
                            break;
                        case 126:
                            return onTransact$setApplicationRestrictions$(data, reply);
                        case 127:
                            return onTransact$getApplicationRestrictions$(data, reply);
                        case 128:
                            ComponentName _arg070 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg139 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result51 = setApplicationRestrictionsManagingPackage(_arg070, _arg139);
                            reply.writeNoException();
                            reply.writeBoolean(_result51);
                            break;
                        case 129:
                            ComponentName _arg071 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result52 = getApplicationRestrictionsManagingPackage(_arg071);
                            reply.writeNoException();
                            reply.writeString(_result52);
                            break;
                        case 130:
                            String _arg072 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result53 = isCallerApplicationRestrictionsManagingPackage(_arg072);
                            reply.writeNoException();
                            reply.writeBoolean(_result53);
                            break;
                        case 131:
                            ComponentName _arg073 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            ComponentName _arg140 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setRestrictionsProvider(_arg073, _arg140);
                            reply.writeNoException();
                            break;
                        case 132:
                            int _arg074 = data.readInt();
                            data.enforceNoDataAvail();
                            ComponentName _result54 = getRestrictionsProvider(_arg074);
                            reply.writeNoException();
                            reply.writeTypedObject(_result54, 1);
                            break;
                        case 133:
                            return onTransact$setUserRestriction$(data, reply);
                        case 134:
                            String _arg075 = data.readString();
                            String _arg141 = data.readString();
                            data.enforceNoDataAvail();
                            setUserRestrictionGlobally(_arg075, _arg141);
                            reply.writeNoException();
                            break;
                        case 135:
                            return onTransact$getUserRestrictions$(data, reply);
                        case 136:
                            String _arg076 = data.readString();
                            data.enforceNoDataAvail();
                            Bundle _result55 = getUserRestrictionsGlobally(_arg076);
                            reply.writeNoException();
                            reply.writeTypedObject(_result55, 1);
                            break;
                        case 137:
                            return onTransact$addCrossProfileIntentFilter$(data, reply);
                        case 138:
                            ComponentName _arg077 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg142 = data.readString();
                            data.enforceNoDataAvail();
                            clearCrossProfileIntentFilters(_arg077, _arg142);
                            reply.writeNoException();
                            break;
                        case 139:
                            ComponentName _arg078 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg143 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            boolean _result56 = setPermittedAccessibilityServices(_arg078, _arg143);
                            reply.writeNoException();
                            reply.writeBoolean(_result56);
                            break;
                        case 140:
                            ComponentName _arg079 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result57 = getPermittedAccessibilityServices(_arg079);
                            reply.writeNoException();
                            reply.writeStringList(_result57);
                            break;
                        case 141:
                            int _arg080 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result58 = getPermittedAccessibilityServicesForUser(_arg080);
                            reply.writeNoException();
                            reply.writeStringList(_result58);
                            break;
                        case 142:
                            return onTransact$isAccessibilityServicePermittedByAdmin$(data, reply);
                        case 143:
                            return onTransact$setPermittedInputMethods$(data, reply);
                        case 144:
                            return onTransact$getPermittedInputMethods$(data, reply);
                        case 145:
                            int _arg081 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result59 = getPermittedInputMethodsAsUser(_arg081);
                            reply.writeNoException();
                            reply.writeStringList(_result59);
                            break;
                        case 146:
                            return onTransact$isInputMethodPermittedByAdmin$(data, reply);
                        case 147:
                            ComponentName _arg082 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg144 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            boolean _result60 = setPermittedCrossProfileNotificationListeners(_arg082, _arg144);
                            reply.writeNoException();
                            reply.writeBoolean(_result60);
                            break;
                        case 148:
                            ComponentName _arg083 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result61 = getPermittedCrossProfileNotificationListeners(_arg083);
                            reply.writeNoException();
                            reply.writeStringList(_result61);
                            break;
                        case 149:
                            String _arg084 = data.readString();
                            int _arg145 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result62 = isNotificationListenerServicePermitted(_arg084, _arg145);
                            reply.writeNoException();
                            reply.writeBoolean(_result62);
                            break;
                        case 150:
                            String _arg085 = data.readString();
                            data.enforceNoDataAvail();
                            Intent _result63 = createAdminSupportIntent(_arg085);
                            reply.writeNoException();
                            reply.writeTypedObject(_result63, 1);
                            break;
                        case 151:
                            int _arg086 = data.readInt();
                            String _arg146 = data.readString();
                            data.enforceNoDataAvail();
                            Bundle _result64 = getEnforcingAdminAndUserDetails(_arg086, _arg146);
                            reply.writeNoException();
                            reply.writeTypedObject(_result64, 1);
                            break;
                        case 152:
                            return onTransact$setApplicationHidden$(data, reply);
                        case 153:
                            return onTransact$isApplicationHidden$(data, reply);
                        case 154:
                            return onTransact$createAndManageUser$(data, reply);
                        case 155:
                            ComponentName _arg087 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg147 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result65 = removeUser(_arg087, _arg147);
                            reply.writeNoException();
                            reply.writeBoolean(_result65);
                            break;
                        case 156:
                            ComponentName _arg088 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg148 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result66 = switchUser(_arg088, _arg148);
                            reply.writeNoException();
                            reply.writeBoolean(_result66);
                            break;
                        case 157:
                            ComponentName _arg089 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg149 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result67 = startUserInBackground(_arg089, _arg149);
                            reply.writeNoException();
                            reply.writeInt(_result67);
                            break;
                        case 158:
                            ComponentName _arg090 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg150 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result68 = stopUser(_arg090, _arg150);
                            reply.writeNoException();
                            reply.writeInt(_result68);
                            break;
                        case 159:
                            ComponentName _arg091 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result69 = logoutUser(_arg091);
                            reply.writeNoException();
                            reply.writeInt(_result69);
                            break;
                        case 160:
                            int _result70 = logoutUserInternal();
                            reply.writeNoException();
                            reply.writeInt(_result70);
                            break;
                        case 161:
                            int _result71 = getLogoutUserId();
                            reply.writeNoException();
                            reply.writeInt(_result71);
                            break;
                        case 162:
                            ComponentName _arg092 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<UserHandle> _result72 = getSecondaryUsers(_arg092);
                            reply.writeNoException();
                            reply.writeTypedList(_result72, 1);
                            break;
                        case 163:
                            int _arg093 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeNewUserDisclaimer(_arg093);
                            reply.writeNoException();
                            break;
                        case 164:
                            int _arg094 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result73 = isNewUserDisclaimerAcknowledged(_arg094);
                            reply.writeNoException();
                            reply.writeBoolean(_result73);
                            break;
                        case 165:
                            return onTransact$enableSystemApp$(data, reply);
                        case 166:
                            return onTransact$enableSystemAppWithIntent$(data, reply);
                        case 167:
                            return onTransact$installExistingPackage$(data, reply);
                        case 168:
                            return onTransact$setAccountManagementDisabled$(data, reply);
                        case 169:
                            String _arg095 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result74 = getAccountTypesWithManagementDisabled(_arg095);
                            reply.writeNoException();
                            reply.writeStringArray(_result74);
                            break;
                        case 170:
                            return onTransact$getAccountTypesWithManagementDisabledAsUser$(data, reply);
                        case 171:
                            ComponentName _arg096 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg151 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSecondaryLockscreenEnabled(_arg096, _arg151);
                            reply.writeNoException();
                            break;
                        case 172:
                            UserHandle _arg097 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result75 = isSecondaryLockscreenEnabled(_arg097);
                            reply.writeNoException();
                            reply.writeBoolean(_result75);
                            break;
                        case 173:
                            List<PreferentialNetworkServiceConfig> _arg098 = data.createTypedArrayList(PreferentialNetworkServiceConfig.CREATOR);
                            data.enforceNoDataAvail();
                            setPreferentialNetworkServiceConfigs(_arg098);
                            reply.writeNoException();
                            break;
                        case 174:
                            List<PreferentialNetworkServiceConfig> _result76 = getPreferentialNetworkServiceConfigs();
                            reply.writeNoException();
                            reply.writeTypedList(_result76, 1);
                            break;
                        case 175:
                            return onTransact$setLockTaskPackages$(data, reply);
                        case 176:
                            ComponentName _arg099 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg152 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result77 = getLockTaskPackages(_arg099, _arg152);
                            reply.writeNoException();
                            reply.writeStringArray(_result77);
                            break;
                        case 177:
                            String _arg0100 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result78 = isLockTaskPermitted(_arg0100);
                            reply.writeNoException();
                            reply.writeBoolean(_result78);
                            break;
                        case 178:
                            return onTransact$setLockTaskFeatures$(data, reply);
                        case 179:
                            ComponentName _arg0101 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg153 = data.readString();
                            data.enforceNoDataAvail();
                            int _result79 = getLockTaskFeatures(_arg0101, _arg153);
                            reply.writeNoException();
                            reply.writeInt(_result79);
                            break;
                        case 180:
                            return onTransact$setGlobalSetting$(data, reply);
                        case 181:
                            return onTransact$setSystemSetting$(data, reply);
                        case 182:
                            return onTransact$setSecureSetting$(data, reply);
                        case 183:
                            return onTransact$setConfiguredNetworksLockdownState$(data, reply);
                        case 184:
                            ComponentName _arg0102 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result80 = hasLockdownAdminConfiguredNetworks(_arg0102);
                            reply.writeNoException();
                            reply.writeBoolean(_result80);
                            break;
                        case 185:
                            ComponentName _arg0103 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg154 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLocationEnabled(_arg0103, _arg154);
                            reply.writeNoException();
                            break;
                        case 186:
                            return onTransact$setTime$(data, reply);
                        case 187:
                            return onTransact$setTimeZone$(data, reply);
                        case 188:
                            ComponentName _arg0104 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg155 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMasterVolumeMuted(_arg0104, _arg155);
                            reply.writeNoException();
                            break;
                        case 189:
                            ComponentName _arg0105 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result81 = isMasterVolumeMuted(_arg0105);
                            reply.writeNoException();
                            reply.writeBoolean(_result81);
                            break;
                        case 190:
                            return onTransact$notifyLockTaskModeChanged$(data, reply);
                        case 191:
                            return onTransact$setUninstallBlocked$(data, reply);
                        case 192:
                            String _arg0106 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result82 = isUninstallBlocked(_arg0106);
                            reply.writeNoException();
                            reply.writeBoolean(_result82);
                            break;
                        case 193:
                            ComponentName _arg0107 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg156 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setCrossProfileCallerIdDisabled(_arg0107, _arg156);
                            reply.writeNoException();
                            break;
                        case 194:
                            ComponentName _arg0108 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result83 = getCrossProfileCallerIdDisabled(_arg0108);
                            reply.writeNoException();
                            reply.writeBoolean(_result83);
                            break;
                        case 195:
                            int _arg0109 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result84 = getCrossProfileCallerIdDisabledForUser(_arg0109);
                            reply.writeNoException();
                            reply.writeBoolean(_result84);
                            break;
                        case 196:
                            ComponentName _arg0110 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg157 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setCrossProfileContactsSearchDisabled(_arg0110, _arg157);
                            reply.writeNoException();
                            break;
                        case 197:
                            ComponentName _arg0111 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result85 = getCrossProfileContactsSearchDisabled(_arg0111);
                            reply.writeNoException();
                            reply.writeBoolean(_result85);
                            break;
                        case 198:
                            int _arg0112 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result86 = getCrossProfileContactsSearchDisabledForUser(_arg0112);
                            reply.writeNoException();
                            reply.writeBoolean(_result86);
                            break;
                        case 199:
                            return onTransact$startManagedQuickContact$(data, reply);
                        case 200:
                            PackagePolicy _arg0113 = (PackagePolicy) data.readTypedObject(PackagePolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setManagedProfileCallerIdAccessPolicy(_arg0113);
                            reply.writeNoException();
                            break;
                        case 201:
                            PackagePolicy _result87 = getManagedProfileCallerIdAccessPolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result87, 1);
                            break;
                        case 202:
                            int _arg0114 = data.readInt();
                            String _arg158 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result88 = hasManagedProfileCallerIdAccess(_arg0114, _arg158);
                            reply.writeNoException();
                            reply.writeBoolean(_result88);
                            break;
                        case 203:
                            PackagePolicy _arg0115 = (PackagePolicy) data.readTypedObject(PackagePolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setCredentialManagerPolicy(_arg0115);
                            reply.writeNoException();
                            break;
                        case 204:
                            PackagePolicy _result89 = getCredentialManagerPolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result89, 1);
                            break;
                        case 205:
                            PackagePolicy _arg0116 = (PackagePolicy) data.readTypedObject(PackagePolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setManagedProfileContactsAccessPolicy(_arg0116);
                            reply.writeNoException();
                            break;
                        case 206:
                            PackagePolicy _result90 = getManagedProfileContactsAccessPolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result90, 1);
                            break;
                        case 207:
                            int _arg0117 = data.readInt();
                            String _arg159 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result91 = hasManagedProfileContactsAccess(_arg0117, _arg159);
                            reply.writeNoException();
                            reply.writeBoolean(_result91);
                            break;
                        case 208:
                            ComponentName _arg0118 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg160 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBluetoothContactSharingDisabled(_arg0118, _arg160);
                            reply.writeNoException();
                            break;
                        case 209:
                            ComponentName _arg0119 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result92 = getBluetoothContactSharingDisabled(_arg0119);
                            reply.writeNoException();
                            reply.writeBoolean(_result92);
                            break;
                        case 210:
                            int _arg0120 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result93 = getBluetoothContactSharingDisabledForUser(_arg0120);
                            reply.writeNoException();
                            reply.writeBoolean(_result93);
                            break;
                        case 211:
                            return onTransact$setTrustAgentConfiguration$(data, reply);
                        case 212:
                            return onTransact$getTrustAgentConfiguration$(data, reply);
                        case 213:
                            return onTransact$addCrossProfileWidgetProvider$(data, reply);
                        case 214:
                            return onTransact$removeCrossProfileWidgetProvider$(data, reply);
                        case 215:
                            ComponentName _arg0121 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg161 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result94 = getCrossProfileWidgetProviders(_arg0121, _arg161);
                            reply.writeNoException();
                            reply.writeStringList(_result94);
                            break;
                        case 216:
                            ComponentName _arg0122 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg162 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setAutoTimeRequired(_arg0122, _arg162);
                            reply.writeNoException();
                            break;
                        case 217:
                            boolean _result95 = getAutoTimeRequired();
                            reply.writeNoException();
                            reply.writeBoolean(_result95);
                            break;
                        case 218:
                            return onTransact$setAutoTimeEnabled$(data, reply);
                        case 219:
                            ComponentName _arg0123 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg163 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result96 = getAutoTimeEnabled(_arg0123, _arg163);
                            reply.writeNoException();
                            reply.writeBoolean(_result96);
                            break;
                        case 220:
                            return onTransact$setAutoTimeZoneEnabled$(data, reply);
                        case 221:
                            ComponentName _arg0124 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg164 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result97 = getAutoTimeZoneEnabled(_arg0124, _arg164);
                            reply.writeNoException();
                            reply.writeBoolean(_result97);
                            break;
                        case 222:
                            ComponentName _arg0125 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg165 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setForceEphemeralUsers(_arg0125, _arg165);
                            reply.writeNoException();
                            break;
                        case 223:
                            ComponentName _arg0126 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result98 = getForceEphemeralUsers(_arg0126);
                            reply.writeNoException();
                            reply.writeBoolean(_result98);
                            break;
                        case 224:
                            ComponentName _arg0127 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg166 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result99 = isRemovingAdmin(_arg0127, _arg166);
                            reply.writeNoException();
                            reply.writeBoolean(_result99);
                            break;
                        case 225:
                            ComponentName _arg0128 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Bitmap _arg167 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            data.enforceNoDataAvail();
                            setUserIcon(_arg0128, _arg167);
                            reply.writeNoException();
                            break;
                        case 226:
                            return onTransact$setSystemUpdatePolicy$(data, reply);
                        case 227:
                            SystemUpdatePolicy _result100 = getSystemUpdatePolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result100, 1);
                            break;
                        case 228:
                            clearSystemUpdatePolicyFreezePeriodRecord();
                            reply.writeNoException();
                            break;
                        case 229:
                            ComponentName _arg0129 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg168 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result101 = setKeyguardDisabled(_arg0129, _arg168);
                            reply.writeNoException();
                            reply.writeBoolean(_result101);
                            break;
                        case 230:
                            return onTransact$setStatusBarDisabled$(data, reply);
                        case 231:
                            String _arg0130 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result102 = isStatusBarDisabled(_arg0130);
                            reply.writeNoException();
                            reply.writeBoolean(_result102);
                            break;
                        case 232:
                            boolean _result103 = getDoNotAskCredentialsOnBoot();
                            reply.writeNoException();
                            reply.writeBoolean(_result103);
                            break;
                        case 233:
                            SystemUpdateInfo _arg0131 = (SystemUpdateInfo) data.readTypedObject(SystemUpdateInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyPendingSystemUpdate(_arg0131);
                            reply.writeNoException();
                            break;
                        case 234:
                            ComponentName _arg0132 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            SystemUpdateInfo _result104 = getPendingSystemUpdate(_arg0132);
                            reply.writeNoException();
                            reply.writeTypedObject(_result104, 1);
                            break;
                        case 235:
                            return onTransact$setPermissionPolicy$(data, reply);
                        case 236:
                            ComponentName _arg0133 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result105 = getPermissionPolicy(_arg0133);
                            reply.writeNoException();
                            reply.writeInt(_result105);
                            break;
                        case 237:
                            return onTransact$setPermissionGrantState$(data, reply);
                        case 238:
                            return onTransact$getPermissionGrantState$(data, reply);
                        case 239:
                            String _arg0134 = data.readString();
                            String _arg169 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result106 = isProvisioningAllowed(_arg0134, _arg169);
                            reply.writeNoException();
                            reply.writeBoolean(_result106);
                            break;
                        case 240:
                            String _arg0135 = data.readString();
                            String _arg170 = data.readString();
                            data.enforceNoDataAvail();
                            int _result107 = checkProvisioningPrecondition(_arg0135, _arg170);
                            reply.writeNoException();
                            reply.writeInt(_result107);
                            break;
                        case 241:
                            return onTransact$setKeepUninstalledPackages$(data, reply);
                        case 242:
                            ComponentName _arg0136 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg171 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result108 = getKeepUninstalledPackages(_arg0136, _arg171);
                            reply.writeNoException();
                            reply.writeStringList(_result108);
                            break;
                        case 243:
                            ComponentName _arg0137 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result109 = isManagedProfile(_arg0137);
                            reply.writeNoException();
                            reply.writeBoolean(_result109);
                            break;
                        case 244:
                            ComponentName _arg0138 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg172 = data.readString();
                            data.enforceNoDataAvail();
                            String _result110 = getWifiMacAddress(_arg0138, _arg172);
                            reply.writeNoException();
                            reply.writeString(_result110);
                            break;
                        case 245:
                            ComponentName _arg0139 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            reboot(_arg0139);
                            reply.writeNoException();
                            break;
                        case 246:
                            return onTransact$setShortSupportMessage$(data, reply);
                        case 247:
                            ComponentName _arg0140 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg173 = data.readString();
                            data.enforceNoDataAvail();
                            CharSequence _result111 = getShortSupportMessage(_arg0140, _arg173);
                            reply.writeNoException();
                            if (_result111 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result111, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 248:
                            ComponentName _arg0141 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            CharSequence _arg174 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setLongSupportMessage(_arg0141, _arg174);
                            reply.writeNoException();
                            break;
                        case 249:
                            ComponentName _arg0142 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            CharSequence _result112 = getLongSupportMessage(_arg0142);
                            reply.writeNoException();
                            if (_result112 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result112, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 250:
                            ComponentName _arg0143 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg175 = data.readInt();
                            data.enforceNoDataAvail();
                            CharSequence _result113 = getShortSupportMessageForUser(_arg0143, _arg175);
                            reply.writeNoException();
                            if (_result113 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result113, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 251:
                            ComponentName _arg0144 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg176 = data.readInt();
                            data.enforceNoDataAvail();
                            CharSequence _result114 = getLongSupportMessageForUser(_arg0144, _arg176);
                            reply.writeNoException();
                            if (_result114 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result114, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 252:
                            ComponentName _arg0145 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg177 = data.readInt();
                            data.enforceNoDataAvail();
                            setOrganizationColor(_arg0145, _arg177);
                            reply.writeNoException();
                            break;
                        case 253:
                            int _arg0146 = data.readInt();
                            int _arg178 = data.readInt();
                            data.enforceNoDataAvail();
                            setOrganizationColorForUser(_arg0146, _arg178);
                            reply.writeNoException();
                            break;
                        case 254:
                            int _arg0147 = data.readInt();
                            data.enforceNoDataAvail();
                            clearOrganizationIdForUser(_arg0147);
                            reply.writeNoException();
                            break;
                        case 255:
                            ComponentName _arg0148 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result115 = getOrganizationColor(_arg0148);
                            reply.writeNoException();
                            reply.writeInt(_result115);
                            break;
                        case 256:
                            int _arg0149 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result116 = getOrganizationColorForUser(_arg0149);
                            reply.writeNoException();
                            reply.writeInt(_result116);
                            break;
                        case 257:
                            return onTransact$setOrganizationName$(data, reply);
                        case 258:
                            ComponentName _arg0150 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg179 = data.readString();
                            data.enforceNoDataAvail();
                            CharSequence _result117 = getOrganizationName(_arg0150, _arg179);
                            reply.writeNoException();
                            if (_result117 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result117, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 259:
                            CharSequence _result118 = getDeviceOwnerOrganizationName();
                            reply.writeNoException();
                            if (_result118 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result118, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 260:
                            int _arg0151 = data.readInt();
                            data.enforceNoDataAvail();
                            CharSequence _result119 = getOrganizationNameForUser(_arg0151);
                            reply.writeNoException();
                            if (_result119 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result119, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 261:
                            int _arg0152 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result120 = getUserProvisioningState(_arg0152);
                            reply.writeNoException();
                            reply.writeInt(_result120);
                            break;
                        case 262:
                            int _arg0153 = data.readInt();
                            int _arg180 = data.readInt();
                            data.enforceNoDataAvail();
                            setUserProvisioningState(_arg0153, _arg180);
                            reply.writeNoException();
                            break;
                        case 263:
                            ComponentName _arg0154 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg181 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setAffiliationIds(_arg0154, _arg181);
                            reply.writeNoException();
                            break;
                        case 264:
                            ComponentName _arg0155 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result121 = getAffiliationIds(_arg0155);
                            reply.writeNoException();
                            reply.writeStringList(_result121);
                            break;
                        case 265:
                            boolean _result122 = isCallingUserAffiliated();
                            reply.writeNoException();
                            reply.writeBoolean(_result122);
                            break;
                        case 266:
                            int _arg0156 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result123 = isAffiliatedUser(_arg0156);
                            reply.writeNoException();
                            reply.writeBoolean(_result123);
                            break;
                        case 267:
                            return onTransact$setSecurityLoggingEnabled$(data, reply);
                        case 268:
                            ComponentName _arg0157 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg182 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result124 = isSecurityLoggingEnabled(_arg0157, _arg182);
                            reply.writeNoException();
                            reply.writeBoolean(_result124);
                            break;
                        case 269:
                            ComponentName _arg0158 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg183 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result125 = retrieveSecurityLogs(_arg0158, _arg183);
                            reply.writeNoException();
                            reply.writeTypedObject(_result125, 1);
                            break;
                        case 270:
                            ComponentName _arg0159 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg184 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result126 = retrievePreRebootSecurityLogs(_arg0159, _arg184);
                            reply.writeNoException();
                            reply.writeTypedObject(_result126, 1);
                            break;
                        case 271:
                            long _result127 = forceNetworkLogs();
                            reply.writeNoException();
                            reply.writeLong(_result127);
                            break;
                        case 272:
                            long _result128 = forceSecurityLogs();
                            reply.writeNoException();
                            reply.writeLong(_result128);
                            break;
                        case 273:
                            String _arg0160 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result129 = isUninstallInQueue(_arg0160);
                            reply.writeNoException();
                            reply.writeBoolean(_result129);
                            break;
                        case 274:
                            String _arg0161 = data.readString();
                            data.enforceNoDataAvail();
                            uninstallPackageWithActiveAdmins(_arg0161);
                            reply.writeNoException();
                            break;
                        case 275:
                            boolean _result130 = isDeviceProvisioned();
                            reply.writeNoException();
                            reply.writeBoolean(_result130);
                            break;
                        case 276:
                            boolean _result131 = isDeviceProvisioningConfigApplied();
                            reply.writeNoException();
                            reply.writeBoolean(_result131);
                            break;
                        case 277:
                            setDeviceProvisioningConfigApplied();
                            reply.writeNoException();
                            break;
                        case 278:
                            int _arg0162 = data.readInt();
                            data.enforceNoDataAvail();
                            forceUpdateUserSetupComplete(_arg0162);
                            reply.writeNoException();
                            break;
                        case 279:
                            ComponentName _arg0163 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg185 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBackupServiceEnabled(_arg0163, _arg185);
                            reply.writeNoException();
                            break;
                        case 280:
                            ComponentName _arg0164 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result132 = isBackupServiceEnabled(_arg0164);
                            reply.writeNoException();
                            reply.writeBoolean(_result132);
                            break;
                        case 281:
                            return onTransact$setNetworkLoggingEnabled$(data, reply);
                        case 282:
                            ComponentName _arg0165 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg186 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result133 = isNetworkLoggingEnabled(_arg0165, _arg186);
                            reply.writeNoException();
                            reply.writeBoolean(_result133);
                            break;
                        case 283:
                            return onTransact$retrieveNetworkLogs$(data, reply);
                        case 284:
                            return onTransact$bindDeviceAdminServiceAsUser$(data, reply);
                        case 285:
                            ComponentName _arg0166 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<UserHandle> _result134 = getBindDeviceAdminTargetUsers(_arg0166);
                            reply.writeNoException();
                            reply.writeTypedList(_result134, 1);
                            break;
                        case 286:
                            ComponentName _arg0167 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result135 = isEphemeralUser(_arg0167);
                            reply.writeNoException();
                            reply.writeBoolean(_result135);
                            break;
                        case 287:
                            long _result136 = getLastSecurityLogRetrievalTime();
                            reply.writeNoException();
                            reply.writeLong(_result136);
                            break;
                        case 288:
                            long _result137 = getLastBugReportRequestTime();
                            reply.writeNoException();
                            reply.writeLong(_result137);
                            break;
                        case 289:
                            long _result138 = getLastNetworkLogRetrievalTime();
                            reply.writeNoException();
                            reply.writeLong(_result138);
                            break;
                        case 290:
                            return onTransact$setResetPasswordToken$(data, reply);
                        case 291:
                            ComponentName _arg0168 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg187 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result139 = clearResetPasswordToken(_arg0168, _arg187);
                            reply.writeNoException();
                            reply.writeBoolean(_result139);
                            break;
                        case 292:
                            ComponentName _arg0169 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg188 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result140 = isResetPasswordTokenActive(_arg0169, _arg188);
                            reply.writeNoException();
                            reply.writeBoolean(_result140);
                            break;
                        case 293:
                            return onTransact$resetPasswordWithToken$(data, reply);
                        case 294:
                            boolean _result141 = isCurrentInputMethodSetByOwner();
                            reply.writeNoException();
                            reply.writeBoolean(_result141);
                            break;
                        case 295:
                            UserHandle _arg0170 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            StringParceledListSlice _result142 = getOwnerInstalledCaCerts(_arg0170);
                            reply.writeNoException();
                            reply.writeTypedObject(_result142, 1);
                            break;
                        case 296:
                            return onTransact$clearApplicationUserData$(data, reply);
                        case 297:
                            ComponentName _arg0171 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg189 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLogoutEnabled(_arg0171, _arg189);
                            reply.writeNoException();
                            break;
                        case 298:
                            boolean _result143 = isLogoutEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result143);
                            break;
                        case 299:
                            return onTransact$getDisallowedSystemApps$(data, reply);
                        case 300:
                            return onTransact$transferOwnership$(data, reply);
                        case 301:
                            PersistableBundle _result144 = getTransferOwnershipBundle();
                            reply.writeNoException();
                            reply.writeTypedObject(_result144, 1);
                            break;
                        case 302:
                            ComponentName _arg0172 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            CharSequence _arg190 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setStartUserSessionMessage(_arg0172, _arg190);
                            reply.writeNoException();
                            break;
                        case 303:
                            ComponentName _arg0173 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            CharSequence _arg191 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setEndUserSessionMessage(_arg0173, _arg191);
                            reply.writeNoException();
                            break;
                        case 304:
                            ComponentName _arg0174 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            CharSequence _result145 = getStartUserSessionMessage(_arg0174);
                            reply.writeNoException();
                            if (_result145 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result145, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 305:
                            ComponentName _arg0175 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            CharSequence _result146 = getEndUserSessionMessage(_arg0175);
                            reply.writeNoException();
                            if (_result146 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result146, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 306:
                            ComponentName _arg0176 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg192 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            List<String> _result147 = setMeteredDataDisabledPackages(_arg0176, _arg192);
                            reply.writeNoException();
                            reply.writeStringList(_result147);
                            break;
                        case 307:
                            ComponentName _arg0177 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result148 = getMeteredDataDisabledPackages(_arg0177);
                            reply.writeNoException();
                            reply.writeStringList(_result148);
                            break;
                        case 308:
                            ComponentName _arg0178 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            ApnSetting _arg193 = (ApnSetting) data.readTypedObject(ApnSetting.CREATOR);
                            data.enforceNoDataAvail();
                            int _result149 = addOverrideApn(_arg0178, _arg193);
                            reply.writeNoException();
                            reply.writeInt(_result149);
                            break;
                        case 309:
                            return onTransact$updateOverrideApn$(data, reply);
                        case 310:
                            ComponentName _arg0179 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg194 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result150 = removeOverrideApn(_arg0179, _arg194);
                            reply.writeNoException();
                            reply.writeBoolean(_result150);
                            break;
                        case 311:
                            ComponentName _arg0180 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<ApnSetting> _result151 = getOverrideApns(_arg0180);
                            reply.writeNoException();
                            reply.writeTypedList(_result151, 1);
                            break;
                        case 312:
                            ComponentName _arg0181 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg195 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setOverrideApnsEnabled(_arg0181, _arg195);
                            reply.writeNoException();
                            break;
                        case 313:
                            ComponentName _arg0182 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result152 = isOverrideApnEnabled(_arg0182);
                            reply.writeNoException();
                            reply.writeBoolean(_result152);
                            break;
                        case 314:
                            return onTransact$isMeteredDataDisabledPackageForUser$(data, reply);
                        case 315:
                            return onTransact$setGlobalPrivateDns$(data, reply);
                        case 316:
                            ComponentName _arg0183 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result153 = getGlobalPrivateDnsMode(_arg0183);
                            reply.writeNoException();
                            reply.writeInt(_result153);
                            break;
                        case 317:
                            ComponentName _arg0184 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result154 = getGlobalPrivateDnsHost(_arg0184);
                            reply.writeNoException();
                            reply.writeString(_result154);
                            break;
                        case 318:
                            return onTransact$setProfileOwnerOnOrganizationOwnedDevice$(data, reply);
                        case 319:
                            return onTransact$installUpdateFromFile$(data, reply);
                        case 320:
                            ComponentName _arg0185 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg196 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setCrossProfileCalendarPackages(_arg0185, _arg196);
                            reply.writeNoException();
                            break;
                        case 321:
                            ComponentName _arg0186 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result155 = getCrossProfileCalendarPackages(_arg0186);
                            reply.writeNoException();
                            reply.writeStringList(_result155);
                            break;
                        case 322:
                            String _arg0187 = data.readString();
                            int _arg197 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result156 = isPackageAllowedToAccessCalendarForUser(_arg0187, _arg197);
                            reply.writeNoException();
                            reply.writeBoolean(_result156);
                            break;
                        case 323:
                            int _arg0188 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result157 = getCrossProfileCalendarPackagesForUser(_arg0188);
                            reply.writeNoException();
                            reply.writeStringList(_result157);
                            break;
                        case 324:
                            ComponentName _arg0189 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            List<String> _arg198 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setCrossProfilePackages(_arg0189, _arg198);
                            reply.writeNoException();
                            break;
                        case 325:
                            ComponentName _arg0190 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<String> _result158 = getCrossProfilePackages(_arg0190);
                            reply.writeNoException();
                            reply.writeStringList(_result158);
                            break;
                        case 326:
                            List<String> _result159 = getAllCrossProfilePackages();
                            reply.writeNoException();
                            reply.writeStringList(_result159);
                            break;
                        case 327:
                            List<String> _result160 = getDefaultCrossProfilePackages();
                            reply.writeNoException();
                            reply.writeStringList(_result160);
                            break;
                        case 328:
                            boolean _result161 = isManagedKiosk();
                            reply.writeNoException();
                            reply.writeBoolean(_result161);
                            break;
                        case 329:
                            boolean _result162 = isUnattendedManagedKiosk();
                            reply.writeNoException();
                            reply.writeBoolean(_result162);
                            break;
                        case 330:
                            return onTransact$startViewCalendarEventInManagedProfile$(data, reply);
                        case 331:
                            return onTransact$setKeyGrantForApp$(data, reply);
                        case 332:
                            String _arg0191 = data.readString();
                            String _arg199 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelableGranteeMap _result163 = getKeyPairGrants(_arg0191, _arg199);
                            reply.writeNoException();
                            reply.writeTypedObject(_result163, 1);
                            break;
                        case 333:
                            return onTransact$setKeyGrantToWifiAuth$(data, reply);
                        case 334:
                            String _arg0192 = data.readString();
                            String _arg1100 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result164 = isKeyPairGrantedToWifiAuth(_arg0192, _arg1100);
                            reply.writeNoException();
                            reply.writeBoolean(_result164);
                            break;
                        case 335:
                            return onTransact$setUserControlDisabledPackages$(data, reply);
                        case 336:
                            ComponentName _arg0193 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg1101 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result165 = getUserControlDisabledPackages(_arg0193, _arg1101);
                            reply.writeNoException();
                            reply.writeStringList(_result165);
                            break;
                        case 337:
                            return onTransact$setCommonCriteriaModeEnabled$(data, reply);
                        case 338:
                            ComponentName _arg0194 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result166 = isCommonCriteriaModeEnabled(_arg0194);
                            reply.writeNoException();
                            reply.writeBoolean(_result166);
                            break;
                        case 339:
                            ComponentName _arg0195 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result167 = getPersonalAppsSuspendedReasons(_arg0195);
                            reply.writeNoException();
                            reply.writeInt(_result167);
                            break;
                        case 340:
                            ComponentName _arg0196 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            boolean _arg1102 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPersonalAppsSuspended(_arg0196, _arg1102);
                            reply.writeNoException();
                            break;
                        case 341:
                            ComponentName _arg0197 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            long _result168 = getManagedProfileMaximumTimeOff(_arg0197);
                            reply.writeNoException();
                            reply.writeLong(_result168);
                            break;
                        case 342:
                            ComponentName _arg0198 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            long _arg1103 = data.readLong();
                            data.enforceNoDataAvail();
                            setManagedProfileMaximumTimeOff(_arg0198, _arg1103);
                            reply.writeNoException();
                            break;
                        case 343:
                            acknowledgeDeviceCompliant();
                            reply.writeNoException();
                            break;
                        case 344:
                            boolean _result169 = isComplianceAcknowledgementRequired();
                            reply.writeNoException();
                            reply.writeBoolean(_result169);
                            break;
                        case 345:
                            int _arg0199 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result170 = canProfileOwnerResetPasswordWhenLocked(_arg0199);
                            reply.writeNoException();
                            reply.writeBoolean(_result170);
                            break;
                        case 346:
                            int _arg0200 = data.readInt();
                            int _arg1104 = data.readInt();
                            data.enforceNoDataAvail();
                            setNextOperationSafety(_arg0200, _arg1104);
                            reply.writeNoException();
                            break;
                        case 347:
                            int _arg0201 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result171 = isSafeOperation(_arg0201);
                            reply.writeNoException();
                            reply.writeBoolean(_result171);
                            break;
                        case 348:
                            String _arg0202 = data.readString();
                            data.enforceNoDataAvail();
                            String _result172 = getEnrollmentSpecificId(_arg0202);
                            reply.writeNoException();
                            reply.writeString(_result172);
                            break;
                        case 349:
                            return onTransact$setOrganizationIdForUser$(data, reply);
                        case 350:
                            ManagedProfileProvisioningParams _arg0203 = (ManagedProfileProvisioningParams) data.readTypedObject(ManagedProfileProvisioningParams.CREATOR);
                            String _arg1105 = data.readString();
                            data.enforceNoDataAvail();
                            UserHandle _result173 = createAndProvisionManagedProfile(_arg0203, _arg1105);
                            reply.writeNoException();
                            reply.writeTypedObject(_result173, 1);
                            break;
                        case 351:
                            FullyManagedDeviceProvisioningParams _arg0204 = (FullyManagedDeviceProvisioningParams) data.readTypedObject(FullyManagedDeviceProvisioningParams.CREATOR);
                            String _arg1106 = data.readString();
                            data.enforceNoDataAvail();
                            provisionFullyManagedDevice(_arg0204, _arg1106);
                            reply.writeNoException();
                            break;
                        case 352:
                            UserHandle _arg0205 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            Account _arg1107 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            finalizeWorkProfileProvisioning(_arg0205, _arg1107);
                            reply.writeNoException();
                            break;
                        case 353:
                            ComponentName _arg0206 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg1108 = data.readInt();
                            data.enforceNoDataAvail();
                            setDeviceOwnerType(_arg0206, _arg1108);
                            reply.writeNoException();
                            break;
                        case 354:
                            ComponentName _arg0207 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int _result174 = getDeviceOwnerType(_arg0207);
                            reply.writeNoException();
                            reply.writeInt(_result174);
                            break;
                        case 355:
                            int _arg0208 = data.readInt();
                            data.enforceNoDataAvail();
                            resetDefaultCrossProfileIntentFilters(_arg0208);
                            reply.writeNoException();
                            break;
                        case 356:
                            boolean _result175 = canAdminGrantSensorsPermissions();
                            reply.writeNoException();
                            reply.writeBoolean(_result175);
                            break;
                        case 357:
                            String _arg0209 = data.readString();
                            boolean _arg1109 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setUsbDataSignalingEnabled(_arg0209, _arg1109);
                            reply.writeNoException();
                            break;
                        case 358:
                            String _arg0210 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result176 = isUsbDataSignalingEnabled(_arg0210);
                            reply.writeNoException();
                            reply.writeBoolean(_result176);
                            break;
                        case 359:
                            int _arg0211 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result177 = isUsbDataSignalingEnabledForUser(_arg0211);
                            reply.writeNoException();
                            reply.writeBoolean(_result177);
                            break;
                        case 360:
                            boolean _result178 = canUsbDataSignalingBeDisabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result178);
                            break;
                        case 361:
                            String _arg0212 = data.readString();
                            int _arg1110 = data.readInt();
                            data.enforceNoDataAvail();
                            setMinimumRequiredWifiSecurityLevel(_arg0212, _arg1110);
                            reply.writeNoException();
                            break;
                        case 362:
                            int _result179 = getMinimumRequiredWifiSecurityLevel();
                            reply.writeNoException();
                            reply.writeInt(_result179);
                            break;
                        case 363:
                            String _arg0213 = data.readString();
                            WifiSsidPolicy _arg1111 = (WifiSsidPolicy) data.readTypedObject(WifiSsidPolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setWifiSsidPolicy(_arg0213, _arg1111);
                            reply.writeNoException();
                            break;
                        case 364:
                            String _arg0214 = data.readString();
                            data.enforceNoDataAvail();
                            WifiSsidPolicy _result180 = getWifiSsidPolicy(_arg0214);
                            reply.writeNoException();
                            reply.writeTypedObject(_result180, 1);
                            break;
                        case 365:
                            List<UserHandle> _result181 = listForegroundAffiliatedUsers();
                            reply.writeNoException();
                            reply.writeTypedList(_result181, 1);
                            break;
                        case 366:
                            List<DevicePolicyDrawableResource> _arg0215 = data.createTypedArrayList(DevicePolicyDrawableResource.CREATOR);
                            data.enforceNoDataAvail();
                            setDrawables(_arg0215);
                            reply.writeNoException();
                            break;
                        case 367:
                            List<String> _arg0216 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            resetDrawables(_arg0216);
                            reply.writeNoException();
                            break;
                        case 368:
                            return onTransact$getDrawable$(data, reply);
                        case 369:
                            boolean _result182 = isDpcDownloaded();
                            reply.writeNoException();
                            reply.writeBoolean(_result182);
                            break;
                        case 370:
                            boolean _arg0217 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDpcDownloaded(_arg0217);
                            reply.writeNoException();
                            break;
                        case 371:
                            List<DevicePolicyStringResource> _arg0218 = data.createTypedArrayList(DevicePolicyStringResource.CREATOR);
                            data.enforceNoDataAvail();
                            setStrings(_arg0218);
                            reply.writeNoException();
                            break;
                        case 372:
                            List<String> _arg0219 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            resetStrings(_arg0219);
                            reply.writeNoException();
                            break;
                        case 373:
                            String _arg0220 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelableResource _result183 = getString(_arg0220);
                            reply.writeNoException();
                            reply.writeTypedObject(_result183, 1);
                            break;
                        case 374:
                            mo197xce7fef36();
                            reply.writeNoException();
                            break;
                        case 375:
                            boolean _result184 = shouldAllowBypassingDevicePolicyManagementRoleQualification();
                            reply.writeNoException();
                            reply.writeBoolean(_result184);
                            break;
                        case 376:
                            UserHandle _arg0221 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            List<UserHandle> _result185 = getPolicyManagedProfiles(_arg0221);
                            reply.writeNoException();
                            reply.writeTypedList(_result185, 1);
                            break;
                        case 377:
                            String _arg0222 = data.readString();
                            int[] _arg1112 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setApplicationExemptions(_arg0222, _arg1112);
                            reply.writeNoException();
                            break;
                        case 378:
                            String _arg0223 = data.readString();
                            data.enforceNoDataAvail();
                            int[] _result186 = getApplicationExemptions(_arg0223);
                            reply.writeNoException();
                            reply.writeIntArray(_result186);
                            break;
                        case 379:
                            int _arg0224 = data.readInt();
                            String _arg1113 = data.readString();
                            data.enforceNoDataAvail();
                            setMtePolicy(_arg0224, _arg1113);
                            reply.writeNoException();
                            break;
                        case 380:
                            String _arg0225 = data.readString();
                            data.enforceNoDataAvail();
                            int _result187 = getMtePolicy(_arg0225);
                            reply.writeNoException();
                            reply.writeInt(_result187);
                            break;
                        case 381:
                            ManagedSubscriptionsPolicy _arg0226 = (ManagedSubscriptionsPolicy) data.readTypedObject(ManagedSubscriptionsPolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setManagedSubscriptionsPolicy(_arg0226);
                            reply.writeNoException();
                            break;
                        case 382:
                            ManagedSubscriptionsPolicy _result188 = getManagedSubscriptionsPolicy();
                            reply.writeNoException();
                            reply.writeTypedObject(_result188, 1);
                            break;
                        case 383:
                            DevicePolicyState _result189 = getDevicePolicyState();
                            reply.writeNoException();
                            reply.writeTypedObject(_result189, 1);
                            break;
                        case 384:
                            boolean _arg0227 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setOverrideKeepProfilesRunning(_arg0227);
                            reply.writeNoException();
                            break;
                        case 385:
                            boolean _arg0228 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result190 = triggerDevicePolicyEngineMigration(_arg0228);
                            reply.writeNoException();
                            reply.writeBoolean(_result190);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IDevicePolicyManager {
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

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordQuality(ComponentName who, int quality, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(quality);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordQuality(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLength(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLength(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumUpperCase(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumUpperCase(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLowerCase(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLowerCase(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumLetters(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumLetters(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumNumeric(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumNumeric(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumSymbols(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumSymbols(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordMinimumNonLetter(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordMinimumNonLetter(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public PasswordMetrics getPasswordMinimumMetrics(int userHandle, boolean deviceWideOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(deviceWideOnly);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    PasswordMetrics _result = (PasswordMetrics) _reply.readTypedObject(PasswordMetrics.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordHistoryLength(ComponentName who, int length, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(length);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordHistoryLength(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPasswordExpirationTimeout(ComponentName who, String callerPackageName, long expiration, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeLong(expiration);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getPasswordExpirationTimeout(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getPasswordExpiration(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isActivePasswordSufficient(String callerPackageName, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isActivePasswordSufficientForDeviceRequirement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isPasswordSufficientAfterProfileUnification(int userHandle, int profileUser) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeInt(profileUser);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPasswordComplexity(boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setRequiredPasswordComplexity(String callerPackageName, int passwordComplexity, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeInt(passwordComplexity);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getRequiredPasswordComplexity(String callerPackageName, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getAggregatedPasswordComplexityForUser(int userId, boolean deviceWideOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(deviceWideOnly);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUsingUnifiedPassword(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getCurrentFailedPasswordAttempts(String callerPackageName, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getProfileWithMinimumFailedPasswordsForWipe(int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMaximumFailedPasswordsForWipe(ComponentName admin, String callerPackageName, int num, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeInt(num);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean resetPassword(String password, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(password);
                    _data.writeInt(flags);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMaximumTimeToLock(ComponentName who, String callerPackageName, long timeMs, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeLong(timeMs);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getMaximumTimeToLock(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setRequiredStrongAuthTimeout(ComponentName who, String callerPackageName, long timeMs, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeLong(timeMs);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getRequiredStrongAuthTimeout(ComponentName who, int userId, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void lockNow(int flags, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void wipeDataWithReason(String callerPackageName, int flags, String wipeReasonForUser, boolean parent, boolean factoryReset) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeInt(flags);
                    _data.writeString(wipeReasonForUser);
                    _data.writeBoolean(parent);
                    _data.writeBoolean(factoryReset);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setFactoryResetProtectionPolicy(ComponentName who, String callerPackageName, FactoryResetProtectionPolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public FactoryResetProtectionPolicy getFactoryResetProtectionPolicy(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    FactoryResetProtectionPolicy _result = (FactoryResetProtectionPolicy) _reply.readTypedObject(FactoryResetProtectionPolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isFactoryResetProtectionPolicySupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void sendLostModeLocationUpdate(AndroidFuture<Boolean> future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName setGlobalProxy(ComponentName admin, String proxySpec, String exclusionList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(proxySpec);
                    _data.writeString(exclusionList);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getGlobalProxyAdmin(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setRecommendedGlobalProxy(ComponentName admin, ProxyInfo proxyInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeTypedObject(proxyInfo, 0);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int setStorageEncryption(ComponentName who, boolean encrypt) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(encrypt);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getStorageEncryption(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getStorageEncryptionStatus(String callerPackage, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean requestBugreport(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCameraDisabled(ComponentName who, String callerPackageName, boolean disabled, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(disabled);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCameraDisabled(ComponentName who, String callerPackageName, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setScreenCaptureDisabled(ComponentName who, String callerPackageName, boolean disabled, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(disabled);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getScreenCaptureDisabled(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setNearbyNotificationStreamingPolicy(int policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(policy);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getNearbyNotificationStreamingPolicy(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setNearbyAppStreamingPolicy(int policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(policy);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getNearbyAppStreamingPolicy(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setKeyguardDisabledFeatures(ComponentName who, String callerPackageName, int which, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeInt(which);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getKeyguardDisabledFeatures(ComponentName who, int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeBoolean(refreshing);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAdminActive(ComponentName policyReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<ComponentName> getActiveAdmins(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    List<ComponentName> _result = _reply.createTypedArrayList(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean packageHasActiveAdmins(String packageName, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void getRemoveWarning(ComponentName policyReceiver, RemoteCallback result, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeTypedObject(result, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void removeActiveAdmin(ComponentName policyReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void forceRemoveActiveAdmin(ComponentName policyReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasGrantedPolicy(ComponentName policyReceiver, int usesPolicy, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyReceiver, 0);
                    _data.writeInt(usesPolicy);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportPasswordChanged(PasswordMetrics metrics, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(metrics, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportFailedPasswordAttempt(int userHandle, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportSuccessfulPasswordAttempt(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportFailedBiometricAttempt(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportSuccessfulBiometricAttempt(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportKeyguardDismissed(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reportKeyguardSecured(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setDeviceOwner(ComponentName who, int userId, boolean setProfileOwnerOnCurrentUserIfNecessary) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(setProfileOwnerOnCurrentUserIfNecessary);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getDeviceOwnerComponent(boolean callingUserOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(callingUserOnly);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasDeviceOwner() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getDeviceOwnerName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearDeviceOwner(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getDeviceOwnerUserId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setProfileOwner(ComponentName who, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getProfileOwnerAsUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getProfileOwnerOrDeviceOwnerSupervisionComponent(UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isSupervisionComponent(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getProfileOwnerName(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setProfileEnabled(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setProfileName(ComponentName who, String profileName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(profileName);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearProfileOwner(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasUserSetupCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isOrganizationOwnedDeviceWithManagedProfile() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean checkDeviceIdentifierAccess(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDeviceOwnerLockScreenInfo(ComponentName who, CharSequence deviceOwnerInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    if (deviceOwnerInfo != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(deviceOwnerInfo, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getDeviceOwnerLockScreenInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String[] setPackagesSuspended(ComponentName admin, String callerPackage, String[] packageNames, boolean suspended) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeStringArray(packageNames);
                    _data.writeBoolean(suspended);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isPackageSuspended(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> listPolicyExemptApps() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean installCaCert(ComponentName admin, String callerPackage, byte[] certBuffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeByteArray(certBuffer);
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void uninstallCaCerts(ComponentName admin, String callerPackage, String[] aliases) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeStringArray(aliases);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void enforceCanManageCaCerts(ComponentName admin, String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean approveCaCert(String alias, int userHandle, boolean approval) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeInt(userHandle);
                    _data.writeBoolean(approval);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isCaCertApproved(String alias, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(104, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean installKeyPair(ComponentName who, String callerPackage, byte[] privKeyBuffer, byte[] certBuffer, byte[] certChainBuffer, String alias, boolean requestAccess, boolean isUserSelectable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeByteArray(privKeyBuffer);
                    _data.writeByteArray(certBuffer);
                    _data.writeByteArray(certChainBuffer);
                    _data.writeString(alias);
                    _data.writeBoolean(requestAccess);
                    _data.writeBoolean(isUserSelectable);
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean removeKeyPair(ComponentName who, String callerPackage, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    this.mRemote.transact(106, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasKeyPair(String callerPackage, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean generateKeyPair(ComponentName who, String callerPackage, String algorithm, ParcelableKeyGenParameterSpec keySpec, int idAttestationFlags, KeymasterCertificateChain attestationChain) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(algorithm);
                    _data.writeTypedObject(keySpec, 0);
                    _data.writeInt(idAttestationFlags);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    if (_reply.readInt() != 0) {
                        attestationChain.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setKeyPairCertificate(ComponentName who, String callerPackage, String alias, byte[] certBuffer, byte[] certChainBuffer, boolean isUserSelectable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    _data.writeByteArray(certBuffer);
                    _data.writeByteArray(certChainBuffer);
                    _data.writeBoolean(isUserSelectable);
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void choosePrivateKeyAlias(int uid, Uri uri, String alias, IBinder aliasCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeTypedObject(uri, 0);
                    _data.writeString(alias);
                    _data.writeStrongBinder(aliasCallback);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDelegatedScopes(ComponentName who, String delegatePackage, List<String> scopes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(delegatePackage);
                    _data.writeStringList(scopes);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getDelegatedScopes(ComponentName who, String delegatePackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(delegatePackage);
                    this.mRemote.transact(112, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getDelegatePackages(ComponentName who, String scope) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(scope);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCertInstallerPackage(ComponentName who, String installerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(installerPackage);
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getCertInstallerPackage(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(115, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setAlwaysOnVpnPackage(ComponentName who, String vpnPackage, boolean lockdown, List<String> lockdownAllowlist) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(vpnPackage);
                    _data.writeBoolean(lockdown);
                    _data.writeStringList(lockdownAllowlist);
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getAlwaysOnVpnPackage(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getAlwaysOnVpnPackageForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAlwaysOnVpnLockdownEnabled(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(119, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAlwaysOnVpnLockdownEnabledForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getAlwaysOnVpnLockdownAllowlist(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(121, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void addPersistentPreferredActivity(ComponentName admin, String callerPackageName, IntentFilter filter, ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(filter, 0);
                    _data.writeTypedObject(activity, 0);
                    this.mRemote.transact(122, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearPackagePersistentPreferredActivities(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(packageName);
                    this.mRemote.transact(123, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDefaultSmsApplication(ComponentName admin, String callerPackageName, String packageName, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(packageName);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(124, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDefaultDialerApplication(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(125, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setApplicationRestrictions(ComponentName who, String callerPackage, String packageName, Bundle settings) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeTypedObject(settings, 0);
                    this.mRemote.transact(126, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public Bundle getApplicationRestrictions(ComponentName who, String callerPackage, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setApplicationRestrictionsManagingPackage(ComponentName admin, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getApplicationRestrictionsManagingPackage(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(129, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isCallerApplicationRestrictionsManagingPackage(String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(130, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setRestrictionsProvider(ComponentName who, ComponentName provider) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeTypedObject(provider, 0);
                    this.mRemote.transact(131, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ComponentName getRestrictionsProvider(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(132, _data, _reply, 0);
                    _reply.readException();
                    ComponentName _result = (ComponentName) _reply.readTypedObject(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUserRestriction(ComponentName who, String callerPackage, String key, boolean enable, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(key);
                    _data.writeBoolean(enable);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(133, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUserRestrictionGlobally(String callerPackage, String key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(key);
                    this.mRemote.transact(134, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public Bundle getUserRestrictions(ComponentName who, String callerPackage, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackage);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(135, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public Bundle getUserRestrictionsGlobally(String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(136, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void addCrossProfileIntentFilter(ComponentName admin, String callerPackageName, IntentFilter filter, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(filter, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(137, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearCrossProfileIntentFilters(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(138, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setPermittedAccessibilityServices(ComponentName admin, List<String> packageList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(packageList);
                    this.mRemote.transact(139, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getPermittedAccessibilityServices(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(140, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getPermittedAccessibilityServicesForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(141, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAccessibilityServicePermittedByAdmin(ComponentName admin, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(142, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setPermittedInputMethods(ComponentName admin, String callerPackageName, List<String> packageList, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeStringList(packageList);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(143, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getPermittedInputMethods(ComponentName admin, String callerPackageName, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(144, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getPermittedInputMethodsAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(145, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isInputMethodPermittedByAdmin(ComponentName admin, String packageName, int userId, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(146, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setPermittedCrossProfileNotificationListeners(ComponentName admin, List<String> packageList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(packageList);
                    this.mRemote.transact(147, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getPermittedCrossProfileNotificationListeners(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(148, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isNotificationListenerServicePermitted(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(149, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public Intent createAdminSupportIntent(String restriction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restriction);
                    this.mRemote.transact(150, _data, _reply, 0);
                    _reply.readException();
                    Intent _result = (Intent) _reply.readTypedObject(Intent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public Bundle getEnforcingAdminAndUserDetails(int userId, String restriction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(restriction);
                    this.mRemote.transact(151, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setApplicationHidden(ComponentName admin, String callerPackage, String packageName, boolean hidden, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeBoolean(hidden);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(152, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isApplicationHidden(ComponentName admin, String callerPackage, String packageName, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(153, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public UserHandle createAndManageUser(ComponentName who, String name, ComponentName profileOwner, PersistableBundle adminExtras, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(name);
                    _data.writeTypedObject(profileOwner, 0);
                    _data.writeTypedObject(adminExtras, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(154, _data, _reply, 0);
                    _reply.readException();
                    UserHandle _result = (UserHandle) _reply.readTypedObject(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean removeUser(ComponentName who, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(155, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean switchUser(ComponentName who, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(156, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int startUserInBackground(ComponentName who, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(157, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int stopUser(ComponentName who, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(158, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int logoutUser(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(159, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int logoutUserInternal() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(160, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getLogoutUserId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(161, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<UserHandle> getSecondaryUsers(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(162, _data, _reply, 0);
                    _reply.readException();
                    List<UserHandle> _result = _reply.createTypedArrayList(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void acknowledgeNewUserDisclaimer(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(163, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isNewUserDisclaimerAcknowledged(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(164, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void enableSystemApp(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    this.mRemote.transact(165, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int enableSystemAppWithIntent(ComponentName admin, String callerPackage, Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(166, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean installExistingPackage(ComponentName admin, String callerPackage, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    this.mRemote.transact(167, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setAccountManagementDisabled(ComponentName who, String callerPackageName, String accountType, boolean disabled, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(accountType);
                    _data.writeBoolean(disabled);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(168, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String[] getAccountTypesWithManagementDisabled(String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(169, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String[] getAccountTypesWithManagementDisabledAsUser(int userId, String callerPackageName, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(170, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setSecondaryLockscreenEnabled(ComponentName who, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(171, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isSecondaryLockscreenEnabled(UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(172, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPreferentialNetworkServiceConfigs(List<PreferentialNetworkServiceConfig> preferentialNetworkServiceConfigs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(preferentialNetworkServiceConfigs, 0);
                    this.mRemote.transact(173, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<PreferentialNetworkServiceConfig> getPreferentialNetworkServiceConfigs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(174, _data, _reply, 0);
                    _reply.readException();
                    List<PreferentialNetworkServiceConfig> _result = _reply.createTypedArrayList(PreferentialNetworkServiceConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setLockTaskPackages(ComponentName who, String callerPackageName, String[] packages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeStringArray(packages);
                    this.mRemote.transact(175, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String[] getLockTaskPackages(ComponentName who, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(176, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isLockTaskPermitted(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(177, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setLockTaskFeatures(ComponentName who, String callerPackageName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeInt(flags);
                    this.mRemote.transact(178, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getLockTaskFeatures(ComponentName who, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(179, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setGlobalSetting(ComponentName who, String setting, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(setting);
                    _data.writeString(value);
                    this.mRemote.transact(180, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setSystemSetting(ComponentName who, String setting, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(setting);
                    _data.writeString(value);
                    this.mRemote.transact(181, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setSecureSetting(ComponentName who, String setting, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(setting);
                    _data.writeString(value);
                    this.mRemote.transact(182, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setConfiguredNetworksLockdownState(ComponentName who, String callerPackageName, boolean lockdown) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(lockdown);
                    this.mRemote.transact(183, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasLockdownAdminConfiguredNetworks(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(184, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setLocationEnabled(ComponentName who, boolean locationEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(locationEnabled);
                    this.mRemote.transact(185, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setTime(ComponentName who, String callerPackageName, long millis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeLong(millis);
                    this.mRemote.transact(186, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setTimeZone(ComponentName who, String callerPackageName, String timeZone) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(timeZone);
                    this.mRemote.transact(187, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMasterVolumeMuted(ComponentName admin, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(on);
                    this.mRemote.transact(188, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isMasterVolumeMuted(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(189, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void notifyLockTaskModeChanged(boolean isEnabled, String pkg, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isEnabled);
                    _data.writeString(pkg);
                    _data.writeInt(userId);
                    this.mRemote.transact(190, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUninstallBlocked(ComponentName admin, String callerPackage, String packageName, boolean uninstallBlocked) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeBoolean(uninstallBlocked);
                    this.mRemote.transact(191, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUninstallBlocked(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(192, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCrossProfileCallerIdDisabled(ComponentName who, boolean disabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(disabled);
                    this.mRemote.transact(193, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCrossProfileCallerIdDisabled(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(194, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCrossProfileCallerIdDisabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(195, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCrossProfileContactsSearchDisabled(ComponentName who, boolean disabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(disabled);
                    this.mRemote.transact(196, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCrossProfileContactsSearchDisabled(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(197, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getCrossProfileContactsSearchDisabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(198, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void startManagedQuickContact(String lookupKey, long contactId, boolean isContactIdIgnored, long directoryId, Intent originalIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lookupKey);
                    _data.writeLong(contactId);
                    _data.writeBoolean(isContactIdIgnored);
                    _data.writeLong(directoryId);
                    _data.writeTypedObject(originalIntent, 0);
                    this.mRemote.transact(199, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setManagedProfileCallerIdAccessPolicy(PackagePolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(200, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public PackagePolicy getManagedProfileCallerIdAccessPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(201, _data, _reply, 0);
                    _reply.readException();
                    PackagePolicy _result = (PackagePolicy) _reply.readTypedObject(PackagePolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasManagedProfileCallerIdAccess(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    this.mRemote.transact(202, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCredentialManagerPolicy(PackagePolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(203, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public PackagePolicy getCredentialManagerPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(204, _data, _reply, 0);
                    _reply.readException();
                    PackagePolicy _result = (PackagePolicy) _reply.readTypedObject(PackagePolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setManagedProfileContactsAccessPolicy(PackagePolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(205, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public PackagePolicy getManagedProfileContactsAccessPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(206, _data, _reply, 0);
                    _reply.readException();
                    PackagePolicy _result = (PackagePolicy) _reply.readTypedObject(PackagePolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean hasManagedProfileContactsAccess(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    this.mRemote.transact(207, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setBluetoothContactSharingDisabled(ComponentName who, boolean disabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(disabled);
                    this.mRemote.transact(208, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getBluetoothContactSharingDisabled(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(209, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getBluetoothContactSharingDisabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(210, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setTrustAgentConfiguration(ComponentName admin, String callerPackageName, ComponentName agent, PersistableBundle args, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(agent, 0);
                    _data.writeTypedObject(args, 0);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(211, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent, int userId, boolean parent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeTypedObject(agent, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(parent);
                    this.mRemote.transact(212, _data, _reply, 0);
                    _reply.readException();
                    List<PersistableBundle> _result = _reply.createTypedArrayList(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean addCrossProfileWidgetProvider(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(packageName);
                    this.mRemote.transact(213, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean removeCrossProfileWidgetProvider(ComponentName admin, String callerPackageName, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(packageName);
                    this.mRemote.transact(214, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getCrossProfileWidgetProviders(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(215, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setAutoTimeRequired(ComponentName who, boolean required) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(required);
                    this.mRemote.transact(216, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getAutoTimeRequired() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(217, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setAutoTimeEnabled(ComponentName who, String callerPackageName, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(218, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getAutoTimeEnabled(ComponentName who, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(219, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setAutoTimeZoneEnabled(ComponentName who, String callerPackageName, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(220, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getAutoTimeZoneEnabled(ComponentName who, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(221, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setForceEphemeralUsers(ComponentName who, boolean forceEpehemeralUsers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeBoolean(forceEpehemeralUsers);
                    this.mRemote.transact(222, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getForceEphemeralUsers(ComponentName who) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    this.mRemote.transact(223, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isRemovingAdmin(ComponentName adminReceiver, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(adminReceiver, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(224, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUserIcon(ComponentName admin, Bitmap icon) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeTypedObject(icon, 0);
                    this.mRemote.transact(225, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setSystemUpdatePolicy(ComponentName who, String callerPackageName, SystemUpdatePolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(226, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public SystemUpdatePolicy getSystemUpdatePolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(227, _data, _reply, 0);
                    _reply.readException();
                    SystemUpdatePolicy _result = (SystemUpdatePolicy) _reply.readTypedObject(SystemUpdatePolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearSystemUpdatePolicyFreezePeriodRecord() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(228, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setKeyguardDisabled(ComponentName admin, boolean disabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(disabled);
                    this.mRemote.transact(229, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setStatusBarDisabled(ComponentName who, String callerPackageName, boolean disabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(disabled);
                    this.mRemote.transact(230, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isStatusBarDisabled(String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(231, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean getDoNotAskCredentialsOnBoot() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(232, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void notifyPendingSystemUpdate(SystemUpdateInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(233, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public SystemUpdateInfo getPendingSystemUpdate(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(234, _data, _reply, 0);
                    _reply.readException();
                    SystemUpdateInfo _result = (SystemUpdateInfo) _reply.readTypedObject(SystemUpdateInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPermissionPolicy(ComponentName admin, String callerPackage, int policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeInt(policy);
                    this.mRemote.transact(235, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPermissionPolicy(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(236, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission, int grantState, RemoteCallback resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeString(permission);
                    _data.writeInt(grantState);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(237, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(packageName);
                    _data.writeString(permission);
                    this.mRemote.transact(238, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isProvisioningAllowed(String action, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeString(packageName);
                    this.mRemote.transact(239, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int checkProvisioningPrecondition(String action, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeString(packageName);
                    this.mRemote.transact(240, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setKeepUninstalledPackages(ComponentName admin, String callerPackage, List<String> packageList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeStringList(packageList);
                    this.mRemote.transact(241, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getKeepUninstalledPackages(ComponentName admin, String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(242, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isManagedProfile(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(243, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getWifiMacAddress(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(244, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void reboot(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(245, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setShortSupportMessage(ComponentName admin, String callerPackageName, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(246, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getShortSupportMessage(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(247, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setLongSupportMessage(ComponentName admin, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(248, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getLongSupportMessage(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(249, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getShortSupportMessageForUser(ComponentName admin, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(250, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getLongSupportMessageForUser(ComponentName admin, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(251, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOrganizationColor(ComponentName admin, int color) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(color);
                    this.mRemote.transact(252, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOrganizationColorForUser(int color, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(color);
                    _data.writeInt(userId);
                    this.mRemote.transact(253, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearOrganizationIdForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(254, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getOrganizationColor(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(255, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getOrganizationColorForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(256, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOrganizationName(ComponentName admin, String callerPackageName, CharSequence title) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    if (title != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(title, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(257, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getOrganizationName(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(258, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getDeviceOwnerOrganizationName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(259, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getOrganizationNameForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(260, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getUserProvisioningState(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(261, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUserProvisioningState(int state, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(262, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setAffiliationIds(ComponentName admin, List<String> ids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(ids);
                    this.mRemote.transact(263, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getAffiliationIds(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(264, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isCallingUserAffiliated() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(265, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isAffiliatedUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(266, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setSecurityLoggingEnabled(ComponentName admin, String packageName, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(267, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isSecurityLoggingEnabled(ComponentName admin, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(268, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ParceledListSlice retrieveSecurityLogs(ComponentName admin, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(269, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ParceledListSlice retrievePreRebootSecurityLogs(ComponentName admin, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(270, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long forceNetworkLogs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(271, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long forceSecurityLogs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(272, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUninstallInQueue(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(273, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void uninstallPackageWithActiveAdmins(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(274, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isDeviceProvisioned() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(275, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isDeviceProvisioningConfigApplied() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(276, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDeviceProvisioningConfigApplied() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(277, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void forceUpdateUserSetupComplete(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(278, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setBackupServiceEnabled(ComponentName admin, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(279, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isBackupServiceEnabled(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(280, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setNetworkLoggingEnabled(ComponentName admin, String packageName, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(281, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isNetworkLoggingEnabled(ComponentName admin, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(282, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<NetworkEvent> retrieveNetworkLogs(ComponentName admin, String packageName, long batchToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeLong(batchToken);
                    this.mRemote.transact(283, _data, _reply, 0);
                    _reply.readException();
                    List<NetworkEvent> _result = _reply.createTypedArrayList(NetworkEvent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean bindDeviceAdminServiceAsUser(ComponentName admin, IApplicationThread caller, IBinder token, Intent service, IServiceConnection connection, long flags, int targetUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStrongInterface(caller);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(service, 0);
                    _data.writeStrongInterface(connection);
                    _data.writeLong(flags);
                    _data.writeInt(targetUserId);
                    this.mRemote.transact(284, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<UserHandle> getBindDeviceAdminTargetUsers(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(285, _data, _reply, 0);
                    _reply.readException();
                    List<UserHandle> _result = _reply.createTypedArrayList(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isEphemeralUser(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(286, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getLastSecurityLogRetrievalTime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(287, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getLastBugReportRequestTime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(288, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getLastNetworkLogRetrievalTime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(289, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setResetPasswordToken(ComponentName admin, String callerPackageName, byte[] token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeByteArray(token);
                    this.mRemote.transact(290, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean clearResetPasswordToken(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(291, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isResetPasswordTokenActive(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(292, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean resetPasswordWithToken(ComponentName admin, String callerPackageName, String password, byte[] token, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeString(password);
                    _data.writeByteArray(token);
                    _data.writeInt(flags);
                    this.mRemote.transact(293, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isCurrentInputMethodSetByOwner() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(294, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public StringParceledListSlice getOwnerInstalledCaCerts(UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(295, _data, _reply, 0);
                    _reply.readException();
                    StringParceledListSlice _result = (StringParceledListSlice) _reply.readTypedObject(StringParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void clearApplicationUserData(ComponentName admin, String packageName, IPackageDataObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(296, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setLogoutEnabled(ComponentName admin, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(297, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isLogoutEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(298, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getDisallowedSystemApps(ComponentName admin, int userId, String provisioningAction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(userId);
                    _data.writeString(provisioningAction);
                    this.mRemote.transact(299, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void transferOwnership(ComponentName admin, ComponentName target, PersistableBundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeTypedObject(target, 0);
                    _data.writeTypedObject(bundle, 0);
                    this.mRemote.transact(300, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public PersistableBundle getTransferOwnershipBundle() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(301, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setStartUserSessionMessage(ComponentName admin, CharSequence startUserSessionMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    if (startUserSessionMessage != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(startUserSessionMessage, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(302, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setEndUserSessionMessage(ComponentName admin, CharSequence endUserSessionMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    if (endUserSessionMessage != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(endUserSessionMessage, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(303, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getStartUserSessionMessage(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(304, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public CharSequence getEndUserSessionMessage(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(305, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> setMeteredDataDisabledPackages(ComponentName admin, List<String> packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(packageNames);
                    this.mRemote.transact(306, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getMeteredDataDisabledPackages(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(307, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int addOverrideApn(ComponentName admin, ApnSetting apnSetting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeTypedObject(apnSetting, 0);
                    this.mRemote.transact(308, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean updateOverrideApn(ComponentName admin, int apnId, ApnSetting apnSetting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(apnId);
                    _data.writeTypedObject(apnSetting, 0);
                    this.mRemote.transact(309, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean removeOverrideApn(ComponentName admin, int apnId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(apnId);
                    this.mRemote.transact(310, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<ApnSetting> getOverrideApns(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(311, _data, _reply, 0);
                    _reply.readException();
                    List<ApnSetting> _result = _reply.createTypedArrayList(ApnSetting.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOverrideApnsEnabled(ComponentName admin, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(312, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isOverrideApnEnabled(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(313, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isMeteredDataDisabledPackageForUser(ComponentName admin, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(314, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int setGlobalPrivateDns(ComponentName admin, int mode, String privateDnsHost) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(mode);
                    _data.writeString(privateDnsHost);
                    this.mRemote.transact(315, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getGlobalPrivateDnsMode(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(316, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getGlobalPrivateDnsHost(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(317, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setProfileOwnerOnOrganizationOwnedDevice(ComponentName who, int userId, boolean isProfileOwnerOnOrganizationOwnedDevice) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(who, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(isProfileOwnerOnOrganizationOwnedDevice);
                    this.mRemote.transact(318, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void installUpdateFromFile(ComponentName admin, String callerPackageName, ParcelFileDescriptor updateFileDescriptor, StartInstallingUpdateCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(updateFileDescriptor, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(319, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCrossProfileCalendarPackages(ComponentName admin, List<String> packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(packageNames);
                    this.mRemote.transact(320, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getCrossProfileCalendarPackages(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(321, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isPackageAllowedToAccessCalendarForUser(String packageName, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(322, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getCrossProfileCalendarPackagesForUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(323, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCrossProfilePackages(ComponentName admin, List<String> packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeStringList(packageNames);
                    this.mRemote.transact(324, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getCrossProfilePackages(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(325, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getAllCrossProfilePackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(326, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getDefaultCrossProfilePackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(327, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isManagedKiosk() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(328, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUnattendedManagedKiosk() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(329, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean startViewCalendarEventInManagedProfile(String packageName, long eventId, long start, long end, boolean allDay, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(eventId);
                    _data.writeLong(start);
                    _data.writeLong(end);
                    _data.writeBoolean(allDay);
                    _data.writeInt(flags);
                    this.mRemote.transact(330, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setKeyGrantForApp(ComponentName admin, String callerPackage, String alias, String packageName, boolean hasGrant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    _data.writeString(packageName);
                    _data.writeBoolean(hasGrant);
                    this.mRemote.transact(331, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ParcelableGranteeMap getKeyPairGrants(String callerPackage, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    this.mRemote.transact(332, _data, _reply, 0);
                    _reply.readException();
                    ParcelableGranteeMap _result = (ParcelableGranteeMap) _reply.readTypedObject(ParcelableGranteeMap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean setKeyGrantToWifiAuth(String callerPackage, String alias, boolean hasGrant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    _data.writeBoolean(hasGrant);
                    this.mRemote.transact(333, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isKeyPairGrantedToWifiAuth(String callerPackage, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(alias);
                    this.mRemote.transact(334, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUserControlDisabledPackages(ComponentName admin, String callerPackageName, List<String> packages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeStringList(packages);
                    this.mRemote.transact(335, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<String> getUserControlDisabledPackages(ComponentName admin, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(336, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setCommonCriteriaModeEnabled(ComponentName admin, String callerPackageName, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeString(callerPackageName);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(337, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isCommonCriteriaModeEnabled(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(338, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getPersonalAppsSuspendedReasons(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(339, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setPersonalAppsSuspended(ComponentName admin, boolean suspended) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeBoolean(suspended);
                    this.mRemote.transact(340, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public long getManagedProfileMaximumTimeOff(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(341, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setManagedProfileMaximumTimeOff(ComponentName admin, long timeoutMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeLong(timeoutMs);
                    this.mRemote.transact(342, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void acknowledgeDeviceCompliant() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(343, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isComplianceAcknowledgementRequired() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(344, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean canProfileOwnerResetPasswordWhenLocked(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(345, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setNextOperationSafety(int operation, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(operation);
                    _data.writeInt(reason);
                    this.mRemote.transact(346, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isSafeOperation(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(347, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public String getEnrollmentSpecificId(String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(348, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOrganizationIdForUser(String callerPackage, String enterpriseId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeString(enterpriseId);
                    _data.writeInt(userId);
                    this.mRemote.transact(349, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public UserHandle createAndProvisionManagedProfile(ManagedProfileProvisioningParams provisioningParams, String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(provisioningParams, 0);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(350, _data, _reply, 0);
                    _reply.readException();
                    UserHandle _result = (UserHandle) _reply.readTypedObject(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void provisionFullyManagedDevice(FullyManagedDeviceProvisioningParams provisioningParams, String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(provisioningParams, 0);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(351, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void finalizeWorkProfileProvisioning(UserHandle managedProfileUser, Account migratedAccount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(managedProfileUser, 0);
                    _data.writeTypedObject(migratedAccount, 0);
                    this.mRemote.transact(352, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDeviceOwnerType(ComponentName admin, int deviceOwnerType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    _data.writeInt(deviceOwnerType);
                    this.mRemote.transact(353, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getDeviceOwnerType(ComponentName admin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(admin, 0);
                    this.mRemote.transact(354, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void resetDefaultCrossProfileIntentFilters(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(355, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean canAdminGrantSensorsPermissions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(356, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setUsbDataSignalingEnabled(String callerPackage, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(357, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUsbDataSignalingEnabled(String callerPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackage);
                    this.mRemote.transact(358, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isUsbDataSignalingEnabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(359, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean canUsbDataSignalingBeDisabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(360, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMinimumRequiredWifiSecurityLevel(String callerPackageName, int level) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeInt(level);
                    this.mRemote.transact(361, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getMinimumRequiredWifiSecurityLevel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(362, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setWifiSsidPolicy(String callerPackageName, WifiSsidPolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(363, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public WifiSsidPolicy getWifiSsidPolicy(String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(364, _data, _reply, 0);
                    _reply.readException();
                    WifiSsidPolicy _result = (WifiSsidPolicy) _reply.readTypedObject(WifiSsidPolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<UserHandle> listForegroundAffiliatedUsers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(365, _data, _reply, 0);
                    _reply.readException();
                    List<UserHandle> _result = _reply.createTypedArrayList(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDrawables(List<DevicePolicyDrawableResource> drawables) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(drawables, 0);
                    this.mRemote.transact(366, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void resetDrawables(List<String> drawableIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(drawableIds);
                    this.mRemote.transact(367, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ParcelableResource getDrawable(String drawableId, String drawableStyle, String drawableSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(drawableId);
                    _data.writeString(drawableStyle);
                    _data.writeString(drawableSource);
                    this.mRemote.transact(368, _data, _reply, 0);
                    _reply.readException();
                    ParcelableResource _result = (ParcelableResource) _reply.readTypedObject(ParcelableResource.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean isDpcDownloaded() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(369, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setDpcDownloaded(boolean downloaded) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(downloaded);
                    this.mRemote.transact(370, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setStrings(List<DevicePolicyStringResource> strings) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(strings, 0);
                    this.mRemote.transact(371, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void resetStrings(List<String> stringIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(stringIds);
                    this.mRemote.transact(372, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ParcelableResource getString(String stringId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(stringId);
                    this.mRemote.transact(373, _data, _reply, 0);
                    _reply.readException();
                    ParcelableResource _result = (ParcelableResource) _reply.readTypedObject(ParcelableResource.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            /* renamed from: resetShouldAllowBypassingDevicePolicyManagementRoleQualificationState */
            public void mo197xce7fef36() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(374, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean shouldAllowBypassingDevicePolicyManagementRoleQualification() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(375, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public List<UserHandle> getPolicyManagedProfiles(UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(376, _data, _reply, 0);
                    _reply.readException();
                    List<UserHandle> _result = _reply.createTypedArrayList(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setApplicationExemptions(String packageName, int[] exemptions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeIntArray(exemptions);
                    this.mRemote.transact(377, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int[] getApplicationExemptions(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(378, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setMtePolicy(int flag, String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(379, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public int getMtePolicy(String callerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    this.mRemote.transact(380, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setManagedSubscriptionsPolicy(ManagedSubscriptionsPolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(381, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public ManagedSubscriptionsPolicy getManagedSubscriptionsPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(382, _data, _reply, 0);
                    _reply.readException();
                    ManagedSubscriptionsPolicy _result = (ManagedSubscriptionsPolicy) _reply.readTypedObject(ManagedSubscriptionsPolicy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public DevicePolicyState getDevicePolicyState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(383, _data, _reply, 0);
                    _reply.readException();
                    DevicePolicyState _result = (DevicePolicyState) _reply.readTypedObject(DevicePolicyState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public void setOverrideKeepProfilesRunning(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(384, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IDevicePolicyManager
            public boolean triggerDevicePolicyEngineMigration(boolean forceMigration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(forceMigration);
                    this.mRemote.transact(385, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        private boolean onTransact$setPasswordMinimumSymbols$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setPasswordMinimumSymbols(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getPasswordMinimumSymbols$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getPasswordMinimumSymbols(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setPasswordMinimumNonLetter$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setPasswordMinimumNonLetter(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getPasswordMinimumNonLetter$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getPasswordMinimumNonLetter(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setPasswordHistoryLength$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setPasswordHistoryLength(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getPasswordHistoryLength$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getPasswordHistoryLength(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setPasswordExpirationTimeout$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            long _arg2 = data.readLong();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setPasswordExpirationTimeout(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getPasswordExpirationTimeout$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            long _result = getPasswordExpirationTimeout(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeLong(_result);
            return true;
        }

        private boolean onTransact$getPasswordExpiration$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            long _result = getPasswordExpiration(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeLong(_result);
            return true;
        }

        private boolean onTransact$isActivePasswordSufficient$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = isActivePasswordSufficient(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setRequiredPasswordComplexity$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setRequiredPasswordComplexity(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getCurrentFailedPasswordAttempts$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getCurrentFailedPasswordAttempts(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setMaximumFailedPasswordsForWipe$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setMaximumFailedPasswordsForWipe(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getMaximumFailedPasswordsForWipe$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getMaximumFailedPasswordsForWipe(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setMaximumTimeToLock$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            long _arg2 = data.readLong();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setMaximumTimeToLock(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getMaximumTimeToLock$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            long _result = getMaximumTimeToLock(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeLong(_result);
            return true;
        }

        private boolean onTransact$setRequiredStrongAuthTimeout$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            long _arg2 = data.readLong();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setRequiredStrongAuthTimeout(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getRequiredStrongAuthTimeout$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            long _result = getRequiredStrongAuthTimeout(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeLong(_result);
            return true;
        }

        private boolean onTransact$wipeDataWithReason$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            wipeDataWithReason(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setFactoryResetProtectionPolicy$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            FactoryResetProtectionPolicy _arg2 = (FactoryResetProtectionPolicy) data.readTypedObject(FactoryResetProtectionPolicy.CREATOR);
            data.enforceNoDataAvail();
            setFactoryResetProtectionPolicy(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setGlobalProxy$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            ComponentName _result = setGlobalProxy(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeTypedObject(_result, 1);
            return true;
        }

        private boolean onTransact$setCameraDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setCameraDisabled(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getCameraDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = getCameraDisabled(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setScreenCaptureDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setScreenCaptureDisabled(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getScreenCaptureDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = getScreenCaptureDisabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setKeyguardDisabledFeatures$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setKeyguardDisabledFeatures(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getKeyguardDisabledFeatures$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            int _result = getKeyguardDisabledFeatures(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setActiveAdmin$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            boolean _arg1 = data.readBoolean();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            setActiveAdmin(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getRemoveWarning$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            RemoteCallback _arg1 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            getRemoveWarning(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$hasGrantedPolicy$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = hasGrantedPolicy(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setDeviceOwner$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setDeviceOwner(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$checkDeviceIdentifierAccess$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = checkDeviceIdentifierAccess(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setPackagesSuspended$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String[] _arg2 = data.createStringArray();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            String[] _result = setPackagesSuspended(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeStringArray(_result);
            return true;
        }

        private boolean onTransact$isPackageSuspended$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = isPackageSuspended(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$installCaCert$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            byte[] _arg2 = data.createByteArray();
            data.enforceNoDataAvail();
            boolean _result = installCaCert(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$uninstallCaCerts$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String[] _arg2 = data.createStringArray();
            data.enforceNoDataAvail();
            uninstallCaCerts(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$approveCaCert$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = approveCaCert(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$installKeyPair$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            byte[] _arg2 = data.createByteArray();
            byte[] _arg3 = data.createByteArray();
            byte[] _arg4 = data.createByteArray();
            String _arg5 = data.readString();
            boolean _arg6 = data.readBoolean();
            boolean _arg7 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = installKeyPair(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$removeKeyPair$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = removeKeyPair(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$generateKeyPair$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            ParcelableKeyGenParameterSpec _arg3 = (ParcelableKeyGenParameterSpec) data.readTypedObject(ParcelableKeyGenParameterSpec.CREATOR);
            int _arg4 = data.readInt();
            KeymasterCertificateChain _arg5 = new KeymasterCertificateChain();
            data.enforceNoDataAvail();
            boolean _result = generateKeyPair(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
            reply.writeNoException();
            reply.writeBoolean(_result);
            reply.writeTypedObject(_arg5, 1);
            return true;
        }

        private boolean onTransact$setKeyPairCertificate$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            byte[] _arg3 = data.createByteArray();
            byte[] _arg4 = data.createByteArray();
            boolean _arg5 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setKeyPairCertificate(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$choosePrivateKeyAlias$(Parcel data, Parcel reply) throws RemoteException {
            int _arg0 = data.readInt();
            Uri _arg1 = (Uri) data.readTypedObject(Uri.CREATOR);
            String _arg2 = data.readString();
            IBinder _arg3 = data.readStrongBinder();
            data.enforceNoDataAvail();
            choosePrivateKeyAlias(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setDelegatedScopes$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            List<String> _arg2 = data.createStringArrayList();
            data.enforceNoDataAvail();
            setDelegatedScopes(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setAlwaysOnVpnPackage$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            List<String> _arg3 = data.createStringArrayList();
            data.enforceNoDataAvail();
            boolean _result = setAlwaysOnVpnPackage(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$addPersistentPreferredActivity$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            IntentFilter _arg2 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
            ComponentName _arg3 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            data.enforceNoDataAvail();
            addPersistentPreferredActivity(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$clearPackagePersistentPreferredActivities$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            clearPackagePersistentPreferredActivities(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setDefaultSmsApplication$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setDefaultSmsApplication(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setApplicationRestrictions$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
            data.enforceNoDataAvail();
            setApplicationRestrictions(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getApplicationRestrictions$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            Bundle _result = getApplicationRestrictions(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeTypedObject(_result, 1);
            return true;
        }

        private boolean onTransact$setUserRestriction$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            setUserRestriction(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getUserRestrictions$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            Bundle _result = getUserRestrictions(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeTypedObject(_result, 1);
            return true;
        }

        private boolean onTransact$addCrossProfileIntentFilter$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            IntentFilter _arg2 = (IntentFilter) data.readTypedObject(IntentFilter.CREATOR);
            int _arg3 = data.readInt();
            data.enforceNoDataAvail();
            addCrossProfileIntentFilter(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$isAccessibilityServicePermittedByAdmin$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = isAccessibilityServicePermittedByAdmin(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setPermittedInputMethods$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            List<String> _arg2 = data.createStringArrayList();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setPermittedInputMethods(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$getPermittedInputMethods$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            List<String> _result = getPermittedInputMethods(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeStringList(_result);
            return true;
        }

        private boolean onTransact$isInputMethodPermittedByAdmin$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = isInputMethodPermittedByAdmin(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setApplicationHidden$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setApplicationHidden(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$isApplicationHidden$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = isApplicationHidden(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$createAndManageUser$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            ComponentName _arg2 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            PersistableBundle _arg3 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
            int _arg4 = data.readInt();
            data.enforceNoDataAvail();
            UserHandle _result = createAndManageUser(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            reply.writeTypedObject(_result, 1);
            return true;
        }

        private boolean onTransact$enableSystemApp$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            enableSystemApp(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$enableSystemAppWithIntent$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            Intent _arg2 = (Intent) data.readTypedObject(Intent.CREATOR);
            data.enforceNoDataAvail();
            int _result = enableSystemAppWithIntent(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$installExistingPackage$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = installExistingPackage(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setAccountManagementDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            setAccountManagementDisabled(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getAccountTypesWithManagementDisabledAsUser$(Parcel data, Parcel reply) throws RemoteException {
            int _arg0 = data.readInt();
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            String[] _result = getAccountTypesWithManagementDisabledAsUser(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeStringArray(_result);
            return true;
        }

        private boolean onTransact$setLockTaskPackages$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String[] _arg2 = data.createStringArray();
            data.enforceNoDataAvail();
            setLockTaskPackages(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setLockTaskFeatures$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            setLockTaskFeatures(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setGlobalSetting$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            setGlobalSetting(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setSystemSetting$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            setSystemSetting(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setSecureSetting$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            setSecureSetting(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setConfiguredNetworksLockdownState$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setConfiguredNetworksLockdownState(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setTime$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            long _arg2 = data.readLong();
            data.enforceNoDataAvail();
            boolean _result = setTime(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setTimeZone$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = setTimeZone(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$notifyLockTaskModeChanged$(Parcel data, Parcel reply) throws RemoteException {
            boolean _arg0 = data.readBoolean();
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            notifyLockTaskModeChanged(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setUninstallBlocked$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            setUninstallBlocked(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$startManagedQuickContact$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            long _arg1 = data.readLong();
            boolean _arg2 = data.readBoolean();
            long _arg3 = data.readLong();
            Intent _arg4 = (Intent) data.readTypedObject(Intent.CREATOR);
            data.enforceNoDataAvail();
            startManagedQuickContact(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setTrustAgentConfiguration$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            ComponentName _arg2 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            PersistableBundle _arg3 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            setTrustAgentConfiguration(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getTrustAgentConfiguration$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            ComponentName _arg1 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg2 = data.readInt();
            boolean _arg3 = data.readBoolean();
            data.enforceNoDataAvail();
            List<PersistableBundle> _result = getTrustAgentConfiguration(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeTypedList(_result, 1);
            return true;
        }

        private boolean onTransact$addCrossProfileWidgetProvider$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = addCrossProfileWidgetProvider(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$removeCrossProfileWidgetProvider$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            boolean _result = removeCrossProfileWidgetProvider(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setAutoTimeEnabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setAutoTimeEnabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setAutoTimeZoneEnabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setAutoTimeZoneEnabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setSystemUpdatePolicy$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            SystemUpdatePolicy _arg2 = (SystemUpdatePolicy) data.readTypedObject(SystemUpdatePolicy.CREATOR);
            data.enforceNoDataAvail();
            setSystemUpdatePolicy(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setStatusBarDisabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setStatusBarDisabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setPermissionPolicy$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            setPermissionPolicy(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setPermissionGrantState$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            String _arg3 = data.readString();
            int _arg4 = data.readInt();
            RemoteCallback _arg5 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
            data.enforceNoDataAvail();
            setPermissionGrantState(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getPermissionGrantState$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            String _arg3 = data.readString();
            data.enforceNoDataAvail();
            int _result = getPermissionGrantState(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setKeepUninstalledPackages$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            List<String> _arg2 = data.createStringArrayList();
            data.enforceNoDataAvail();
            setKeepUninstalledPackages(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setShortSupportMessage$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            CharSequence _arg2 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
            data.enforceNoDataAvail();
            setShortSupportMessage(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setOrganizationName$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            CharSequence _arg2 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
            data.enforceNoDataAvail();
            setOrganizationName(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setSecurityLoggingEnabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setSecurityLoggingEnabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setNetworkLoggingEnabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setNetworkLoggingEnabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$retrieveNetworkLogs$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            long _arg2 = data.readLong();
            data.enforceNoDataAvail();
            List<NetworkEvent> _result = retrieveNetworkLogs(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeTypedList(_result, 1);
            return true;
        }

        private boolean onTransact$bindDeviceAdminServiceAsUser$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            IApplicationThread _arg1 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
            IBinder _arg2 = data.readStrongBinder();
            Intent _arg3 = (Intent) data.readTypedObject(Intent.CREATOR);
            IServiceConnection _arg4 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
            long _arg5 = data.readLong();
            int _arg6 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = bindDeviceAdminServiceAsUser(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setResetPasswordToken$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            byte[] _arg2 = data.createByteArray();
            data.enforceNoDataAvail();
            boolean _result = setResetPasswordToken(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$resetPasswordWithToken$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            byte[] _arg3 = data.createByteArray();
            int _arg4 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = resetPasswordWithToken(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$clearApplicationUserData$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            IPackageDataObserver _arg2 = IPackageDataObserver.Stub.asInterface(data.readStrongBinder());
            data.enforceNoDataAvail();
            clearApplicationUserData(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getDisallowedSystemApps$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            List<String> _result = getDisallowedSystemApps(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeStringList(_result);
            return true;
        }

        private boolean onTransact$transferOwnership$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            ComponentName _arg1 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            PersistableBundle _arg2 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
            data.enforceNoDataAvail();
            transferOwnership(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$updateOverrideApn$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            ApnSetting _arg2 = (ApnSetting) data.readTypedObject(ApnSetting.CREATOR);
            data.enforceNoDataAvail();
            boolean _result = updateOverrideApn(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$isMeteredDataDisabledPackageForUser$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = isMeteredDataDisabledPackageForUser(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setGlobalPrivateDns$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            int _result = setGlobalPrivateDns(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeInt(_result);
            return true;
        }

        private boolean onTransact$setProfileOwnerOnOrganizationOwnedDevice$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            int _arg1 = data.readInt();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setProfileOwnerOnOrganizationOwnedDevice(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$installUpdateFromFile$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            ParcelFileDescriptor _arg2 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
            StartInstallingUpdateCallback _arg3 = StartInstallingUpdateCallback.Stub.asInterface(data.readStrongBinder());
            data.enforceNoDataAvail();
            installUpdateFromFile(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$startViewCalendarEventInManagedProfile$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            long _arg1 = data.readLong();
            long _arg2 = data.readLong();
            long _arg3 = data.readLong();
            boolean _arg4 = data.readBoolean();
            int _arg5 = data.readInt();
            data.enforceNoDataAvail();
            boolean _result = startViewCalendarEventInManagedProfile(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setKeyGrantForApp$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            String _arg3 = data.readString();
            boolean _arg4 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setKeyGrantForApp(_arg0, _arg1, _arg2, _arg3, _arg4);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setKeyGrantToWifiAuth$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            boolean _result = setKeyGrantToWifiAuth(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeBoolean(_result);
            return true;
        }

        private boolean onTransact$setUserControlDisabledPackages$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            List<String> _arg2 = data.createStringArrayList();
            data.enforceNoDataAvail();
            setUserControlDisabledPackages(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setCommonCriteriaModeEnabled$(Parcel data, Parcel reply) throws RemoteException {
            ComponentName _arg0 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
            String _arg1 = data.readString();
            boolean _arg2 = data.readBoolean();
            data.enforceNoDataAvail();
            setCommonCriteriaModeEnabled(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$setOrganizationIdForUser$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            String _arg1 = data.readString();
            int _arg2 = data.readInt();
            data.enforceNoDataAvail();
            setOrganizationIdForUser(_arg0, _arg1, _arg2);
            reply.writeNoException();
            return true;
        }

        private boolean onTransact$getDrawable$(Parcel data, Parcel reply) throws RemoteException {
            String _arg0 = data.readString();
            String _arg1 = data.readString();
            String _arg2 = data.readString();
            data.enforceNoDataAvail();
            ParcelableResource _result = getDrawable(_arg0, _arg1, _arg2);
            reply.writeNoException();
            reply.writeTypedObject(_result, 1);
            return true;
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 384;
        }
    }
}
